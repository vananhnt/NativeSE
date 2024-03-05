package android.media;

import android.media.MediaCodecInfo;

/* loaded from: MediaCodecList.class */
public final class MediaCodecList {
    public static final native int getCodecCount();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final native String getCodecName(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final native boolean isEncoder(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final native String[] getSupportedTypes(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final native MediaCodecInfo.CodecCapabilities getCodecCapabilities(int i, String str);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final native int findCodecByName(String str);

    private static final native void native_init();

    public static final MediaCodecInfo getCodecInfoAt(int index) {
        if (index < 0 || index > getCodecCount()) {
            throw new IllegalArgumentException();
        }
        return new MediaCodecInfo(index);
    }

    private MediaCodecList() {
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}