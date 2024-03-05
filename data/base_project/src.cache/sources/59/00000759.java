package android.media;

import android.os.Bundle;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

/* loaded from: MediaCodec.class */
public final class MediaCodec {
    public static final int BUFFER_FLAG_SYNC_FRAME = 1;
    public static final int BUFFER_FLAG_CODEC_CONFIG = 2;
    public static final int BUFFER_FLAG_END_OF_STREAM = 4;
    public static final int CONFIGURE_FLAG_ENCODE = 1;
    public static final int CRYPTO_MODE_UNENCRYPTED = 0;
    public static final int CRYPTO_MODE_AES_CTR = 1;
    public static final int INFO_TRY_AGAIN_LATER = -1;
    public static final int INFO_OUTPUT_FORMAT_CHANGED = -2;
    public static final int INFO_OUTPUT_BUFFERS_CHANGED = -3;
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;
    public static final String PARAMETER_KEY_VIDEO_BITRATE = "video-bitrate";
    public static final String PARAMETER_KEY_SUSPEND = "drop-input-frames";
    public static final String PARAMETER_KEY_REQUEST_SYNC_FRAME = "request-sync";
    private int mNativeContext;

    public final native void release();

    private final native void native_configure(String[] strArr, Object[] objArr, Surface surface, MediaCrypto mediaCrypto, int i);

    public final native Surface createInputSurface();

    public final native void start();

    public final native void stop();

    public final native void flush();

    public final native void queueInputBuffer(int i, int i2, int i3, long j, int i4) throws CryptoException;

    public final native void queueSecureInputBuffer(int i, int i2, CryptoInfo cryptoInfo, long j, int i3) throws CryptoException;

    public final native int dequeueInputBuffer(long j);

    public final native int dequeueOutputBuffer(BufferInfo bufferInfo, long j);

    public final native void releaseOutputBuffer(int i, boolean z);

    public final native void signalEndOfInputStream();

    private final native Map<String, Object> getOutputFormatNative();

    public final native void setVideoScalingMode(int i);

    public final native String getName();

    private final native void setParameters(String[] strArr, Object[] objArr);

    private final native ByteBuffer[] getBuffers(boolean z);

    private static final native void native_init();

    private final native void native_setup(String str, boolean z, boolean z2);

    private final native void native_finalize();

    /* loaded from: MediaCodec$BufferInfo.class */
    public static final class BufferInfo {
        public int offset;
        public int size;
        public long presentationTimeUs;
        public int flags;

        public void set(int newOffset, int newSize, long newTimeUs, int newFlags) {
            this.offset = newOffset;
            this.size = newSize;
            this.presentationTimeUs = newTimeUs;
            this.flags = newFlags;
        }
    }

    public static MediaCodec createDecoderByType(String type) {
        return new MediaCodec(type, true, false);
    }

    public static MediaCodec createEncoderByType(String type) {
        return new MediaCodec(type, true, true);
    }

    public static MediaCodec createByCodecName(String name) {
        return new MediaCodec(name, false, false);
    }

    private MediaCodec(String name, boolean nameIsType, boolean encoder) {
        native_setup(name, nameIsType, encoder);
    }

    protected void finalize() {
        native_finalize();
    }

    public void configure(MediaFormat format, Surface surface, MediaCrypto crypto, int flags) {
        Map<String, Object> formatMap = format.getMap();
        String[] keys = null;
        Object[] values = null;
        if (format != null) {
            keys = new String[formatMap.size()];
            values = new Object[formatMap.size()];
            int i = 0;
            for (Map.Entry<String, Object> entry : formatMap.entrySet()) {
                keys[i] = entry.getKey();
                values[i] = entry.getValue();
                i++;
            }
        }
        native_configure(keys, values, surface, crypto, flags);
    }

    /* loaded from: MediaCodec$CryptoException.class */
    public static final class CryptoException extends RuntimeException {
        public static final int ERROR_NO_KEY = 1;
        public static final int ERROR_KEY_EXPIRED = 2;
        public static final int ERROR_RESOURCE_BUSY = 3;
        private int mErrorCode;

        public CryptoException(int errorCode, String detailMessage) {
            super(detailMessage);
            this.mErrorCode = errorCode;
        }

        public int getErrorCode() {
            return this.mErrorCode;
        }
    }

    /* loaded from: MediaCodec$CryptoInfo.class */
    public static final class CryptoInfo {
        public int numSubSamples;
        public int[] numBytesOfClearData;
        public int[] numBytesOfEncryptedData;
        public byte[] key;
        public byte[] iv;
        public int mode;

        public void set(int newNumSubSamples, int[] newNumBytesOfClearData, int[] newNumBytesOfEncryptedData, byte[] newKey, byte[] newIV, int newMode) {
            this.numSubSamples = newNumSubSamples;
            this.numBytesOfClearData = newNumBytesOfClearData;
            this.numBytesOfEncryptedData = newNumBytesOfEncryptedData;
            this.key = newKey;
            this.iv = newIV;
            this.mode = newMode;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.numSubSamples + " subsamples, key [");
            for (int i = 0; i < this.key.length; i++) {
                builder.append("0123456789abcdef".charAt((this.key[i] & 240) >> 4));
                builder.append("0123456789abcdef".charAt(this.key[i] & 15));
            }
            builder.append("], iv [");
            for (int i2 = 0; i2 < this.key.length; i2++) {
                builder.append("0123456789abcdef".charAt((this.iv[i2] & 240) >> 4));
                builder.append("0123456789abcdef".charAt(this.iv[i2] & 15));
            }
            builder.append("], clear ");
            builder.append(Arrays.toString(this.numBytesOfClearData));
            builder.append(", encrypted ");
            builder.append(Arrays.toString(this.numBytesOfEncryptedData));
            return builder.toString();
        }
    }

    public final MediaFormat getOutputFormat() {
        return new MediaFormat(getOutputFormatNative());
    }

    public ByteBuffer[] getInputBuffers() {
        return getBuffers(true);
    }

    public ByteBuffer[] getOutputBuffers() {
        return getBuffers(false);
    }

    public final void setParameters(Bundle params) {
        if (params == null) {
            return;
        }
        String[] keys = new String[params.size()];
        Object[] values = new Object[params.size()];
        int i = 0;
        for (String key : params.keySet()) {
            keys[i] = key;
            values[i] = params.get(key);
            i++;
        }
        setParameters(keys, values);
    }

    public MediaCodecInfo getCodecInfo() {
        return MediaCodecList.getCodecInfoAt(MediaCodecList.findCodecByName(getName()));
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}