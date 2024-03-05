package android.media;

import android.os.Parcel;
import android.util.Log;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;

/* loaded from: Metadata.class */
public class Metadata {
    public static final int ANY = 0;
    public static final int PAUSE_AVAILABLE = 1;
    public static final int SEEK_BACKWARD_AVAILABLE = 2;
    public static final int SEEK_FORWARD_AVAILABLE = 3;
    public static final int SEEK_AVAILABLE = 4;
    public static final int TITLE = 5;
    public static final int COMMENT = 6;
    public static final int COPYRIGHT = 7;
    public static final int ALBUM = 8;
    public static final int ARTIST = 9;
    public static final int AUTHOR = 10;
    public static final int COMPOSER = 11;
    public static final int GENRE = 12;
    public static final int DATE = 13;
    public static final int DURATION = 14;
    public static final int CD_TRACK_NUM = 15;
    public static final int CD_TRACK_MAX = 16;
    public static final int RATING = 17;
    public static final int ALBUM_ART = 18;
    public static final int VIDEO_FRAME = 19;
    public static final int BIT_RATE = 20;
    public static final int AUDIO_BIT_RATE = 21;
    public static final int VIDEO_BIT_RATE = 22;
    public static final int AUDIO_SAMPLE_RATE = 23;
    public static final int VIDEO_FRAME_RATE = 24;
    public static final int MIME_TYPE = 25;
    public static final int AUDIO_CODEC = 26;
    public static final int VIDEO_CODEC = 27;
    public static final int VIDEO_HEIGHT = 28;
    public static final int VIDEO_WIDTH = 29;
    public static final int NUM_TRACKS = 30;
    public static final int DRM_CRIPPLED = 31;
    private static final int LAST_SYSTEM = 31;
    private static final int FIRST_CUSTOM = 8192;
    public static final Set<Integer> MATCH_NONE = Collections.EMPTY_SET;
    public static final Set<Integer> MATCH_ALL = Collections.singleton(0);
    public static final int STRING_VAL = 1;
    public static final int INTEGER_VAL = 2;
    public static final int BOOLEAN_VAL = 3;
    public static final int LONG_VAL = 4;
    public static final int DOUBLE_VAL = 5;
    public static final int DATE_VAL = 6;
    public static final int BYTE_ARRAY_VAL = 7;
    private static final int LAST_TYPE = 7;
    private static final String TAG = "media.Metadata";
    private static final int kInt32Size = 4;
    private static final int kMetaHeaderSize = 8;
    private static final int kRecordHeaderSize = 12;
    private static final int kMetaMarker = 1296389185;
    private Parcel mParcel;
    private final HashMap<Integer, Integer> mKeyToPosMap = new HashMap<>();

    /* JADX WARN: Code restructure failed: missing block: B:18:0x008b, code lost:
        android.util.Log.e(android.media.Metadata.TAG, "Invalid metadata type " + r0);
        r8 = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean scanAllRecords(android.os.Parcel r5, int r6) {
        /*
            Method dump skipped, instructions count: 237
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.Metadata.scanAllRecords(android.os.Parcel, int):boolean");
    }

    public boolean parse(Parcel parcel) {
        if (parcel.dataAvail() < 8) {
            Log.e(TAG, "Not enough data " + parcel.dataAvail());
            return false;
        }
        int pin = parcel.dataPosition();
        int size = parcel.readInt();
        if (parcel.dataAvail() + 4 < size || size < 8) {
            Log.e(TAG, "Bad size " + size + " avail " + parcel.dataAvail() + " position " + pin);
            parcel.setDataPosition(pin);
            return false;
        }
        int kShouldBeMetaMarker = parcel.readInt();
        if (kShouldBeMetaMarker != kMetaMarker) {
            Log.e(TAG, "Marker missing " + Integer.toHexString(kShouldBeMetaMarker));
            parcel.setDataPosition(pin);
            return false;
        } else if (!scanAllRecords(parcel, size - 8)) {
            parcel.setDataPosition(pin);
            return false;
        } else {
            this.mParcel = parcel;
            return true;
        }
    }

    public Set<Integer> keySet() {
        return this.mKeyToPosMap.keySet();
    }

    public boolean has(int metadataId) {
        if (!checkMetadataId(metadataId)) {
            throw new IllegalArgumentException("Invalid key: " + metadataId);
        }
        return this.mKeyToPosMap.containsKey(Integer.valueOf(metadataId));
    }

    public String getString(int key) {
        checkType(key, 1);
        return this.mParcel.readString();
    }

    public int getInt(int key) {
        checkType(key, 2);
        return this.mParcel.readInt();
    }

    public boolean getBoolean(int key) {
        checkType(key, 3);
        return this.mParcel.readInt() == 1;
    }

    public long getLong(int key) {
        checkType(key, 4);
        return this.mParcel.readLong();
    }

    public double getDouble(int key) {
        checkType(key, 5);
        return this.mParcel.readDouble();
    }

    public byte[] getByteArray(int key) {
        checkType(key, 7);
        return this.mParcel.createByteArray();
    }

    public Date getDate(int key) {
        checkType(key, 6);
        long timeSinceEpoch = this.mParcel.readLong();
        String timeZone = this.mParcel.readString();
        if (timeZone.length() == 0) {
            return new Date(timeSinceEpoch);
        }
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        Calendar cal = Calendar.getInstance(tz);
        cal.setTimeInMillis(timeSinceEpoch);
        return cal.getTime();
    }

    public static int lastSytemId() {
        return 31;
    }

    public static int firstCustomId() {
        return 8192;
    }

    public static int lastType() {
        return 7;
    }

    private boolean checkMetadataId(int val) {
        if (val <= 0 || (31 < val && val < 8192)) {
            Log.e(TAG, "Invalid metadata ID " + val);
            return false;
        }
        return true;
    }

    private void checkType(int key, int expectedType) {
        int pos = this.mKeyToPosMap.get(Integer.valueOf(key)).intValue();
        this.mParcel.setDataPosition(pos);
        int type = this.mParcel.readInt();
        if (type != expectedType) {
            throw new IllegalStateException("Wrong type " + expectedType + " but got " + type);
        }
    }
}