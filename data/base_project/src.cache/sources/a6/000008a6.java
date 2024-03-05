package android.media.videoeditor;

/* loaded from: VideoEditorProfile.class */
public class VideoEditorProfile {
    public int maxInputVideoFrameWidth;
    public int maxInputVideoFrameHeight;
    public int maxOutputVideoFrameWidth;
    public int maxOutputVideoFrameHeight;

    private static final native void native_init();

    private static final native VideoEditorProfile native_get_videoeditor_profile();

    private static final native int native_get_videoeditor_export_profile(int i);

    private static final native int native_get_videoeditor_export_level(int i);

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    public static VideoEditorProfile get() {
        return native_get_videoeditor_profile();
    }

    public static int getExportProfile(int vidCodec) {
        switch (vidCodec) {
            case 1:
            case 2:
            case 3:
                int profile = native_get_videoeditor_export_profile(vidCodec);
                return profile;
            default:
                throw new IllegalArgumentException("Unsupported video codec" + vidCodec);
        }
    }

    public static int getExportLevel(int vidCodec) {
        switch (vidCodec) {
            case 1:
            case 2:
            case 3:
                int level = native_get_videoeditor_export_level(vidCodec);
                return level;
            default:
                throw new IllegalArgumentException("Unsupported video codec" + vidCodec);
        }
    }

    private VideoEditorProfile(int inputWidth, int inputHeight, int outputWidth, int outputHeight) {
        this.maxInputVideoFrameWidth = inputWidth;
        this.maxInputVideoFrameHeight = inputHeight;
        this.maxOutputVideoFrameWidth = outputWidth;
        this.maxOutputVideoFrameHeight = outputHeight;
    }
}