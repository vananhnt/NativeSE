package android.support.v4.media;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.media.MediaMetadataCompatApi21;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import java.util.Set;

/* loaded from: MediaMetadataCompat.class */
public final class MediaMetadataCompat implements Parcelable {
    public static final Parcelable.Creator<MediaMetadataCompat> CREATOR;
    private static final ArrayMap<String, Integer> METADATA_KEYS_TYPE = new ArrayMap<>();
    public static final String METADATA_KEY_ALBUM = "android.media.metadata.ALBUM";
    public static final String METADATA_KEY_ALBUM_ART = "android.media.metadata.ALBUM_ART";
    public static final String METADATA_KEY_ALBUM_ARTIST = "android.media.metadata.ALBUM_ARTIST";
    public static final String METADATA_KEY_ALBUM_ART_URI = "android.media.metadata.ALBUM_ART_URI";
    public static final String METADATA_KEY_ART = "android.media.metadata.ART";
    public static final String METADATA_KEY_ARTIST = "android.media.metadata.ARTIST";
    public static final String METADATA_KEY_ART_URI = "android.media.metadata.ART_URI";
    public static final String METADATA_KEY_AUTHOR = "android.media.metadata.AUTHOR";
    public static final String METADATA_KEY_COMPILATION = "android.media.metadata.COMPILATION";
    public static final String METADATA_KEY_COMPOSER = "android.media.metadata.COMPOSER";
    public static final String METADATA_KEY_DATE = "android.media.metadata.DATE";
    public static final String METADATA_KEY_DISC_NUMBER = "android.media.metadata.DISC_NUMBER";
    public static final String METADATA_KEY_DISPLAY_DESCRIPTION = "android.media.metadata.DISPLAY_DESCRIPTION";
    public static final String METADATA_KEY_DISPLAY_ICON = "android.media.metadata.DISPLAY_ICON";
    public static final String METADATA_KEY_DISPLAY_ICON_URI = "android.media.metadata.DISPLAY_ICON_URI";
    public static final String METADATA_KEY_DISPLAY_SUBTITLE = "android.media.metadata.DISPLAY_SUBTITLE";
    public static final String METADATA_KEY_DISPLAY_TITLE = "android.media.metadata.DISPLAY_TITLE";
    public static final String METADATA_KEY_DURATION = "android.media.metadata.DURATION";
    public static final String METADATA_KEY_GENRE = "android.media.metadata.GENRE";
    public static final String METADATA_KEY_NUM_TRACKS = "android.media.metadata.NUM_TRACKS";
    public static final String METADATA_KEY_RATING = "android.media.metadata.RATING";
    public static final String METADATA_KEY_TITLE = "android.media.metadata.TITLE";
    public static final String METADATA_KEY_TRACK_NUMBER = "android.media.metadata.TRACK_NUMBER";
    public static final String METADATA_KEY_USER_RATING = "android.media.metadata.USER_RATING";
    public static final String METADATA_KEY_WRITER = "android.media.metadata.WRITER";
    public static final String METADATA_KEY_YEAR = "android.media.metadata.YEAR";
    private static final int METADATA_TYPE_BITMAP = 2;
    private static final int METADATA_TYPE_LONG = 0;
    private static final int METADATA_TYPE_RATING = 3;
    private static final int METADATA_TYPE_TEXT = 1;
    private static final String TAG = "MediaMetadata";
    private final Bundle mBundle;
    private Object mMetadataObj;

    /* loaded from: MediaMetadataCompat$Builder.class */
    public static final class Builder {
        private final Bundle mBundle;

        public Builder() {
            this.mBundle = new Bundle();
        }

        public Builder(MediaMetadataCompat mediaMetadataCompat) {
            this.mBundle = new Bundle(mediaMetadataCompat.mBundle);
        }

        public MediaMetadataCompat build() {
            return new MediaMetadataCompat(this.mBundle);
        }

        public Builder putBitmap(String str, Bitmap bitmap) {
            if (!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(str) || ((Integer) MediaMetadataCompat.METADATA_KEYS_TYPE.get(str)).intValue() == 2) {
                this.mBundle.putParcelable(str, bitmap);
                return this;
            }
            throw new IllegalArgumentException("The " + str + " key cannot be used to put a Bitmap");
        }

        public Builder putLong(String str, long j) {
            if (!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(str) || ((Integer) MediaMetadataCompat.METADATA_KEYS_TYPE.get(str)).intValue() == 0) {
                this.mBundle.putLong(str, j);
                return this;
            }
            throw new IllegalArgumentException("The " + str + " key cannot be used to put a long");
        }

        public Builder putRating(String str, RatingCompat ratingCompat) {
            if (!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(str) || ((Integer) MediaMetadataCompat.METADATA_KEYS_TYPE.get(str)).intValue() == 3) {
                this.mBundle.putParcelable(str, ratingCompat);
                return this;
            }
            throw new IllegalArgumentException("The " + str + " key cannot be used to put a Rating");
        }

        public Builder putString(String str, String str2) {
            if (!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(str) || ((Integer) MediaMetadataCompat.METADATA_KEYS_TYPE.get(str)).intValue() == 1) {
                this.mBundle.putCharSequence(str, str2);
                return this;
            }
            throw new IllegalArgumentException("The " + str + " key cannot be used to put a String");
        }

        public Builder putText(String str, CharSequence charSequence) {
            if (!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(str) || ((Integer) MediaMetadataCompat.METADATA_KEYS_TYPE.get(str)).intValue() == 1) {
                this.mBundle.putCharSequence(str, charSequence);
                return this;
            }
            throw new IllegalArgumentException("The " + str + " key cannot be used to put a CharSequence");
        }
    }

    static {
        METADATA_KEYS_TYPE.put(METADATA_KEY_TITLE, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_ARTIST, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_DURATION, 0);
        METADATA_KEYS_TYPE.put(METADATA_KEY_ALBUM, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_AUTHOR, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_WRITER, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_COMPOSER, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_COMPILATION, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_DATE, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_YEAR, 0);
        METADATA_KEYS_TYPE.put(METADATA_KEY_GENRE, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_TRACK_NUMBER, 0);
        METADATA_KEYS_TYPE.put(METADATA_KEY_NUM_TRACKS, 0);
        METADATA_KEYS_TYPE.put(METADATA_KEY_DISC_NUMBER, 0);
        METADATA_KEYS_TYPE.put(METADATA_KEY_ALBUM_ARTIST, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_ART, 2);
        METADATA_KEYS_TYPE.put(METADATA_KEY_ART_URI, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_ALBUM_ART, 2);
        METADATA_KEYS_TYPE.put(METADATA_KEY_ALBUM_ART_URI, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_USER_RATING, 3);
        METADATA_KEYS_TYPE.put(METADATA_KEY_RATING, 3);
        METADATA_KEYS_TYPE.put(METADATA_KEY_DISPLAY_TITLE, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_DISPLAY_SUBTITLE, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_DISPLAY_DESCRIPTION, 1);
        METADATA_KEYS_TYPE.put(METADATA_KEY_DISPLAY_ICON, 2);
        METADATA_KEYS_TYPE.put(METADATA_KEY_DISPLAY_ICON_URI, 1);
        CREATOR = new Parcelable.Creator<MediaMetadataCompat>() { // from class: android.support.v4.media.MediaMetadataCompat.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MediaMetadataCompat createFromParcel(Parcel parcel) {
                return new MediaMetadataCompat(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MediaMetadataCompat[] newArray(int i) {
                return new MediaMetadataCompat[i];
            }
        };
    }

    private MediaMetadataCompat(Bundle bundle) {
        this.mBundle = new Bundle(bundle);
    }

    private MediaMetadataCompat(Parcel parcel) {
        this.mBundle = parcel.readBundle();
    }

    public static MediaMetadataCompat fromMediaMetadata(Object obj) {
        if (obj == null || Build.VERSION.SDK_INT < 21) {
            return null;
        }
        Builder builder = new Builder();
        for (String str : MediaMetadataCompatApi21.keySet(obj)) {
            Integer num = METADATA_KEYS_TYPE.get(str);
            if (num != null) {
                switch (num.intValue()) {
                    case 0:
                        builder.putLong(str, MediaMetadataCompatApi21.getLong(obj, str));
                        continue;
                    case 1:
                        builder.putText(str, MediaMetadataCompatApi21.getText(obj, str));
                        continue;
                    case 2:
                        builder.putBitmap(str, MediaMetadataCompatApi21.getBitmap(obj, str));
                        continue;
                    case 3:
                        builder.putRating(str, RatingCompat.fromRating(MediaMetadataCompatApi21.getRating(obj, str)));
                        continue;
                }
            }
        }
        MediaMetadataCompat build = builder.build();
        build.mMetadataObj = obj;
        return build;
    }

    public boolean containsKey(String str) {
        return this.mBundle.containsKey(str);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Bitmap getBitmap(String str) {
        Bitmap bitmap;
        try {
            bitmap = (Bitmap) this.mBundle.getParcelable(str);
        } catch (Exception e) {
            Log.w(TAG, "Failed to retrieve a key as Bitmap.", e);
            bitmap = null;
        }
        return bitmap;
    }

    public long getLong(String str) {
        return this.mBundle.getLong(str, 0L);
    }

    public Object getMediaMetadata() {
        if (this.mMetadataObj != null || Build.VERSION.SDK_INT < 21) {
            return this.mMetadataObj;
        }
        Object newInstance = MediaMetadataCompatApi21.Builder.newInstance();
        for (String str : keySet()) {
            Integer num = METADATA_KEYS_TYPE.get(str);
            if (num != null) {
                switch (num.intValue()) {
                    case 0:
                        MediaMetadataCompatApi21.Builder.putLong(newInstance, str, getLong(str));
                        continue;
                    case 1:
                        MediaMetadataCompatApi21.Builder.putText(newInstance, str, getText(str));
                        continue;
                    case 2:
                        MediaMetadataCompatApi21.Builder.putBitmap(newInstance, str, getBitmap(str));
                        continue;
                    case 3:
                        MediaMetadataCompatApi21.Builder.putRating(newInstance, str, getRating(str).getRating());
                        continue;
                }
            }
        }
        this.mMetadataObj = MediaMetadataCompatApi21.Builder.build(newInstance);
        return this.mMetadataObj;
    }

    public RatingCompat getRating(String str) {
        RatingCompat ratingCompat;
        try {
            ratingCompat = (RatingCompat) this.mBundle.getParcelable(str);
        } catch (Exception e) {
            Log.w(TAG, "Failed to retrieve a key as Rating.", e);
            ratingCompat = null;
        }
        return ratingCompat;
    }

    public String getString(String str) {
        CharSequence charSequence = this.mBundle.getCharSequence(str);
        if (charSequence != null) {
            return charSequence.toString();
        }
        return null;
    }

    public CharSequence getText(String str) {
        return this.mBundle.getCharSequence(str);
    }

    public Set<String> keySet() {
        return this.mBundle.keySet();
    }

    public int size() {
        return this.mBundle.size();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBundle(this.mBundle);
    }
}