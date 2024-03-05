package android.media;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/* loaded from: MediaFormat.class */
public final class MediaFormat {
    private Map<String, Object> mMap;
    public static final String KEY_MIME = "mime";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_SAMPLE_RATE = "sample-rate";
    public static final String KEY_CHANNEL_COUNT = "channel-count";
    public static final String KEY_WIDTH = "width";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_MAX_WIDTH = "max-width";
    public static final String KEY_MAX_HEIGHT = "max-height";
    public static final String KEY_MAX_INPUT_SIZE = "max-input-size";
    public static final String KEY_BIT_RATE = "bitrate";
    public static final String KEY_COLOR_FORMAT = "color-format";
    public static final String KEY_FRAME_RATE = "frame-rate";
    public static final String KEY_I_FRAME_INTERVAL = "i-frame-interval";
    public static final String KEY_STRIDE = "stride";
    public static final String KEY_SLICE_HEIGHT = "slice-height";
    public static final String KEY_REPEAT_PREVIOUS_FRAME_AFTER = "repeat-previous-frame-after";
    public static final String KEY_PUSH_BLANK_BUFFERS_ON_STOP = "push-blank-buffers-on-shutdown";
    public static final String KEY_DURATION = "durationUs";
    public static final String KEY_IS_ADTS = "is-adts";
    public static final String KEY_CHANNEL_MASK = "channel-mask";
    public static final String KEY_AAC_PROFILE = "aac-profile";
    public static final String KEY_FLAC_COMPRESSION_LEVEL = "flac-compression-level";
    public static final String KEY_IS_AUTOSELECT = "is-autoselect";
    public static final String KEY_IS_DEFAULT = "is-default";
    public static final String KEY_IS_FORCED_SUBTITLE = "is-forced-subtitle";

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaFormat(Map<String, Object> map) {
        this.mMap = map;
    }

    public MediaFormat() {
        this.mMap = new HashMap();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<String, Object> getMap() {
        return this.mMap;
    }

    public final boolean containsKey(String name) {
        return this.mMap.containsKey(name);
    }

    public final int getInteger(String name) {
        return ((Integer) this.mMap.get(name)).intValue();
    }

    public final int getInteger(String name, int defaultValue) {
        try {
            return getInteger(name);
        } catch (ClassCastException | NullPointerException e) {
            return defaultValue;
        }
    }

    public final long getLong(String name) {
        return ((Long) this.mMap.get(name)).longValue();
    }

    public final float getFloat(String name) {
        return ((Float) this.mMap.get(name)).floatValue();
    }

    public final String getString(String name) {
        return (String) this.mMap.get(name);
    }

    public final ByteBuffer getByteBuffer(String name) {
        return (ByteBuffer) this.mMap.get(name);
    }

    public final void setInteger(String name, int value) {
        this.mMap.put(name, new Integer(value));
    }

    public final void setLong(String name, long value) {
        this.mMap.put(name, new Long(value));
    }

    public final void setFloat(String name, float value) {
        this.mMap.put(name, new Float(value));
    }

    public final void setString(String name, String value) {
        this.mMap.put(name, value);
    }

    public final void setByteBuffer(String name, ByteBuffer bytes) {
        this.mMap.put(name, bytes);
    }

    public static final MediaFormat createAudioFormat(String mime, int sampleRate, int channelCount) {
        MediaFormat format = new MediaFormat();
        format.setString(KEY_MIME, mime);
        format.setInteger(KEY_SAMPLE_RATE, sampleRate);
        format.setInteger(KEY_CHANNEL_COUNT, channelCount);
        return format;
    }

    public static final MediaFormat createSubtitleFormat(String mime, String language) {
        MediaFormat format = new MediaFormat();
        format.setString(KEY_MIME, mime);
        format.setString("language", language);
        return format;
    }

    public static final MediaFormat createVideoFormat(String mime, int width, int height) {
        MediaFormat format = new MediaFormat();
        format.setString(KEY_MIME, mime);
        format.setInteger("width", width);
        format.setInteger("height", height);
        return format;
    }

    public String toString() {
        return this.mMap.toString();
    }
}