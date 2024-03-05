package android.media;

import android.text.format.Time;
import gov.nist.core.Separators;
import gov.nist.javax.sip.parser.TokenNames;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/* loaded from: ExifInterface.class */
public class ExifInterface {
    public static final String TAG_ORIENTATION = "Orientation";
    public static final String TAG_DATETIME = "DateTime";
    public static final String TAG_MAKE = "Make";
    public static final String TAG_MODEL = "Model";
    public static final String TAG_FLASH = "Flash";
    public static final String TAG_IMAGE_WIDTH = "ImageWidth";
    public static final String TAG_IMAGE_LENGTH = "ImageLength";
    public static final String TAG_GPS_LATITUDE = "GPSLatitude";
    public static final String TAG_GPS_LONGITUDE = "GPSLongitude";
    public static final String TAG_GPS_LATITUDE_REF = "GPSLatitudeRef";
    public static final String TAG_GPS_LONGITUDE_REF = "GPSLongitudeRef";
    public static final String TAG_EXPOSURE_TIME = "ExposureTime";
    public static final String TAG_APERTURE = "FNumber";
    public static final String TAG_ISO = "ISOSpeedRatings";
    public static final String TAG_GPS_ALTITUDE = "GPSAltitude";
    public static final String TAG_GPS_ALTITUDE_REF = "GPSAltitudeRef";
    public static final String TAG_GPS_TIMESTAMP = "GPSTimeStamp";
    public static final String TAG_GPS_DATESTAMP = "GPSDateStamp";
    public static final String TAG_WHITE_BALANCE = "WhiteBalance";
    public static final String TAG_FOCAL_LENGTH = "FocalLength";
    public static final String TAG_GPS_PROCESSING_METHOD = "GPSProcessingMethod";
    public static final int ORIENTATION_UNDEFINED = 0;
    public static final int ORIENTATION_NORMAL = 1;
    public static final int ORIENTATION_FLIP_HORIZONTAL = 2;
    public static final int ORIENTATION_ROTATE_180 = 3;
    public static final int ORIENTATION_FLIP_VERTICAL = 4;
    public static final int ORIENTATION_TRANSPOSE = 5;
    public static final int ORIENTATION_ROTATE_90 = 6;
    public static final int ORIENTATION_TRANSVERSE = 7;
    public static final int ORIENTATION_ROTATE_270 = 8;
    public static final int WHITEBALANCE_AUTO = 0;
    public static final int WHITEBALANCE_MANUAL = 1;
    private static SimpleDateFormat sFormatter;
    private String mFilename;
    private HashMap<String, String> mAttributes;
    private boolean mHasThumbnail;
    private static final Object sLock;

    private native boolean appendThumbnailNative(String str, String str2);

    private native void saveAttributesNative(String str, String str2);

    private native String getAttributesNative(String str);

    private native void commitChangesNative(String str);

    private native byte[] getThumbnailNative(String str);

    private native long[] getThumbnailRangeNative(String str);

    static {
        System.loadLibrary("exif_jni");
        sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        sFormatter.setTimeZone(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
        sLock = new Object();
    }

    public ExifInterface(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("filename cannot be null");
        }
        this.mFilename = filename;
        loadAttributes();
    }

    public String getAttribute(String tag) {
        return this.mAttributes.get(tag);
    }

    public int getAttributeInt(String tag, int defaultValue) {
        String value = this.mAttributes.get(tag);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value).intValue();
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getAttributeDouble(String tag, double defaultValue) {
        String value = this.mAttributes.get(tag);
        if (value == null) {
            return defaultValue;
        }
        try {
            int index = value.indexOf(Separators.SLASH);
            if (index == -1) {
                return defaultValue;
            }
            double denom = Double.parseDouble(value.substring(index + 1));
            if (denom == 0.0d) {
                return defaultValue;
            }
            double num = Double.parseDouble(value.substring(0, index));
            return num / denom;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void setAttribute(String tag, String value) {
        this.mAttributes.put(tag, value);
    }

    private void loadAttributes() throws IOException {
        String attrStr;
        this.mAttributes = new HashMap<>();
        synchronized (sLock) {
            attrStr = getAttributesNative(this.mFilename);
        }
        int ptr = attrStr.indexOf(32);
        int count = Integer.parseInt(attrStr.substring(0, ptr));
        int ptr2 = ptr + 1;
        for (int i = 0; i < count; i++) {
            int equalPos = attrStr.indexOf(61, ptr2);
            String attrName = attrStr.substring(ptr2, equalPos);
            int ptr3 = equalPos + 1;
            int lenPos = attrStr.indexOf(32, ptr3);
            int attrLen = Integer.parseInt(attrStr.substring(ptr3, lenPos));
            int ptr4 = lenPos + 1;
            String attrValue = attrStr.substring(ptr4, ptr4 + attrLen);
            ptr2 = ptr4 + attrLen;
            if (attrName.equals("hasThumbnail")) {
                this.mHasThumbnail = attrValue.equalsIgnoreCase("true");
            } else {
                this.mAttributes.put(attrName, attrValue);
            }
        }
    }

    public void saveAttributes() throws IOException {
        StringBuilder sb = new StringBuilder();
        int size = this.mAttributes.size();
        if (this.mAttributes.containsKey("hasThumbnail")) {
            size--;
        }
        sb.append(size + Separators.SP);
        for (Map.Entry<String, String> iter : this.mAttributes.entrySet()) {
            String key = iter.getKey();
            if (!key.equals("hasThumbnail")) {
                String val = iter.getValue();
                sb.append(key + Separators.EQUALS);
                sb.append(val.length() + Separators.SP);
                sb.append(val);
            }
        }
        String s = sb.toString();
        synchronized (sLock) {
            saveAttributesNative(this.mFilename, s);
            commitChangesNative(this.mFilename);
        }
    }

    public boolean hasThumbnail() {
        return this.mHasThumbnail;
    }

    public byte[] getThumbnail() {
        byte[] thumbnailNative;
        synchronized (sLock) {
            thumbnailNative = getThumbnailNative(this.mFilename);
        }
        return thumbnailNative;
    }

    public long[] getThumbnailRange() {
        long[] thumbnailRangeNative;
        synchronized (sLock) {
            thumbnailRangeNative = getThumbnailRangeNative(this.mFilename);
        }
        return thumbnailRangeNative;
    }

    public boolean getLatLong(float[] output) {
        String latValue = this.mAttributes.get(TAG_GPS_LATITUDE);
        String latRef = this.mAttributes.get(TAG_GPS_LATITUDE_REF);
        String lngValue = this.mAttributes.get(TAG_GPS_LONGITUDE);
        String lngRef = this.mAttributes.get(TAG_GPS_LONGITUDE_REF);
        if (latValue != null && latRef != null && lngValue != null && lngRef != null) {
            try {
                output[0] = convertRationalLatLonToFloat(latValue, latRef);
                output[1] = convertRationalLatLonToFloat(lngValue, lngRef);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    public double getAltitude(double defaultValue) {
        double altitude = getAttributeDouble(TAG_GPS_ALTITUDE, -1.0d);
        int ref = getAttributeInt(TAG_GPS_ALTITUDE_REF, -1);
        if (altitude >= 0.0d && ref >= 0) {
            return altitude * (ref == 1 ? -1 : 1);
        }
        return defaultValue;
    }

    public long getDateTime() {
        String dateTimeString = this.mAttributes.get(TAG_DATETIME);
        if (dateTimeString == null) {
            return -1L;
        }
        ParsePosition pos = new ParsePosition(0);
        try {
            Date datetime = sFormatter.parse(dateTimeString, pos);
            if (datetime == null) {
                return -1L;
            }
            return datetime.getTime();
        } catch (IllegalArgumentException e) {
            return -1L;
        }
    }

    public long getGpsDateTime() {
        String dateTimeString;
        String date = this.mAttributes.get(TAG_GPS_DATESTAMP);
        String time = this.mAttributes.get(TAG_GPS_TIMESTAMP);
        if (date == null || time == null || (date + ' ' + time) == null) {
            return -1L;
        }
        ParsePosition pos = new ParsePosition(0);
        try {
            Date datetime = sFormatter.parse(dateTimeString, pos);
            if (datetime == null) {
                return -1L;
            }
            return datetime.getTime();
        } catch (IllegalArgumentException e) {
            return -1L;
        }
    }

    private static float convertRationalLatLonToFloat(String rationalString, String ref) {
        try {
            String[] parts = rationalString.split(Separators.COMMA);
            String[] pair = parts[0].split(Separators.SLASH);
            double degrees = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());
            String[] pair2 = parts[1].split(Separators.SLASH);
            double minutes = Double.parseDouble(pair2[0].trim()) / Double.parseDouble(pair2[1].trim());
            String[] pair3 = parts[2].split(Separators.SLASH);
            double seconds = Double.parseDouble(pair3[0].trim()) / Double.parseDouble(pair3[1].trim());
            double result = degrees + (minutes / 60.0d) + (seconds / 3600.0d);
            if (ref.equals(TokenNames.S) || ref.equals("W")) {
                return (float) (-result);
            }
            return (float) result;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        } catch (NumberFormatException e2) {
            throw new IllegalArgumentException();
        }
    }
}