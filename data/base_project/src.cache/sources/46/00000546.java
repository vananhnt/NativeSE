package android.graphics;

/* loaded from: ImageFormat.class */
public class ImageFormat {
    public static final int UNKNOWN = 0;
    public static final int RGB_565 = 4;
    public static final int YV12 = 842094169;
    public static final int Y8 = 538982489;
    public static final int Y16 = 540422489;
    public static final int NV16 = 16;
    public static final int NV21 = 17;
    public static final int YUY2 = 20;
    public static final int JPEG = 256;
    public static final int YUV_420_888 = 35;
    public static final int RAW_SENSOR = 32;
    public static final int BAYER_RGGB = 512;

    public static int getBitsPerPixel(int format) {
        switch (format) {
            case 4:
                return 16;
            case 16:
                return 16;
            case 17:
                return 12;
            case 20:
                return 16;
            case 32:
                return 16;
            case 35:
                return 12;
            case 512:
                return 16;
            case Y8 /* 538982489 */:
                return 8;
            case Y16 /* 540422489 */:
                return 16;
            case YV12 /* 842094169 */:
                return 12;
            default:
                return -1;
        }
    }
}