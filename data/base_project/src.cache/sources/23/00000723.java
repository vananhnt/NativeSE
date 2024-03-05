package android.media;

import android.hardware.Camera;

/* loaded from: CamcorderProfile.class */
public class CamcorderProfile {
    public static final int QUALITY_LOW = 0;
    public static final int QUALITY_HIGH = 1;
    public static final int QUALITY_QCIF = 2;
    public static final int QUALITY_CIF = 3;
    public static final int QUALITY_480P = 4;
    public static final int QUALITY_720P = 5;
    public static final int QUALITY_1080P = 6;
    public static final int QUALITY_QVGA = 7;
    private static final int QUALITY_LIST_START = 0;
    private static final int QUALITY_LIST_END = 7;
    public static final int QUALITY_TIME_LAPSE_LOW = 1000;
    public static final int QUALITY_TIME_LAPSE_HIGH = 1001;
    public static final int QUALITY_TIME_LAPSE_QCIF = 1002;
    public static final int QUALITY_TIME_LAPSE_CIF = 1003;
    public static final int QUALITY_TIME_LAPSE_480P = 1004;
    public static final int QUALITY_TIME_LAPSE_720P = 1005;
    public static final int QUALITY_TIME_LAPSE_1080P = 1006;
    public static final int QUALITY_TIME_LAPSE_QVGA = 1007;
    private static final int QUALITY_TIME_LAPSE_LIST_START = 1000;
    private static final int QUALITY_TIME_LAPSE_LIST_END = 1007;
    public int duration;
    public int quality;
    public int fileFormat;
    public int videoCodec;
    public int videoBitRate;
    public int videoFrameRate;
    public int videoFrameWidth;
    public int videoFrameHeight;
    public int audioCodec;
    public int audioBitRate;
    public int audioSampleRate;
    public int audioChannels;

    private static final native void native_init();

    private static final native CamcorderProfile native_get_camcorder_profile(int i, int i2);

    private static final native boolean native_has_camcorder_profile(int i, int i2);

    public static CamcorderProfile get(int quality) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 0) {
                return get(i, quality);
            }
        }
        return null;
    }

    public static CamcorderProfile get(int cameraId, int quality) {
        if ((quality < 0 || quality > 7) && (quality < 1000 || quality > 1007)) {
            String errMessage = "Unsupported quality level: " + quality;
            throw new IllegalArgumentException(errMessage);
        }
        return native_get_camcorder_profile(cameraId, quality);
    }

    public static boolean hasProfile(int quality) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 0) {
                return hasProfile(i, quality);
            }
        }
        return false;
    }

    public static boolean hasProfile(int cameraId, int quality) {
        return native_has_camcorder_profile(cameraId, quality);
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    private CamcorderProfile(int duration, int quality, int fileFormat, int videoCodec, int videoBitRate, int videoFrameRate, int videoWidth, int videoHeight, int audioCodec, int audioBitRate, int audioSampleRate, int audioChannels) {
        this.duration = duration;
        this.quality = quality;
        this.fileFormat = fileFormat;
        this.videoCodec = videoCodec;
        this.videoBitRate = videoBitRate;
        this.videoFrameRate = videoFrameRate;
        this.videoFrameWidth = videoWidth;
        this.videoFrameHeight = videoHeight;
        this.audioCodec = audioCodec;
        this.audioBitRate = audioBitRate;
        this.audioSampleRate = audioSampleRate;
        this.audioChannels = audioChannels;
    }
}