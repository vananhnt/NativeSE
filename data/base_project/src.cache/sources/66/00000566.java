package android.graphics;

/* loaded from: PixelFormat.class */
public class PixelFormat {
    public static final int UNKNOWN = 0;
    public static final int TRANSLUCENT = -3;
    public static final int TRANSPARENT = -2;
    public static final int OPAQUE = -1;
    public static final int RGBA_8888 = 1;
    public static final int RGBX_8888 = 2;
    public static final int RGB_888 = 3;
    public static final int RGB_565 = 4;
    @Deprecated
    public static final int RGBA_5551 = 6;
    @Deprecated
    public static final int RGBA_4444 = 7;
    @Deprecated
    public static final int A_8 = 8;
    @Deprecated
    public static final int L_8 = 9;
    @Deprecated
    public static final int LA_88 = 10;
    @Deprecated
    public static final int RGB_332 = 11;
    @Deprecated
    public static final int YCbCr_422_SP = 16;
    @Deprecated
    public static final int YCbCr_420_SP = 17;
    @Deprecated
    public static final int YCbCr_422_I = 20;
    @Deprecated
    public static final int JPEG = 256;
    public int bytesPerPixel;
    public int bitsPerPixel;

    public static void getPixelFormatInfo(int format, PixelFormat info) {
        switch (format) {
            case 1:
            case 2:
                info.bitsPerPixel = 32;
                info.bytesPerPixel = 4;
                return;
            case 3:
                info.bitsPerPixel = 24;
                info.bytesPerPixel = 3;
                return;
            case 4:
            case 6:
            case 7:
            case 10:
                info.bitsPerPixel = 16;
                info.bytesPerPixel = 2;
                return;
            case 5:
            case 12:
            case 13:
            case 14:
            case 15:
            case 18:
            case 19:
            default:
                throw new IllegalArgumentException("unkonwon pixel format " + format);
            case 8:
            case 9:
            case 11:
                info.bitsPerPixel = 8;
                info.bytesPerPixel = 1;
                return;
            case 16:
            case 20:
                info.bitsPerPixel = 16;
                info.bytesPerPixel = 1;
                return;
            case 17:
                info.bitsPerPixel = 12;
                info.bytesPerPixel = 1;
                return;
        }
    }

    public static boolean formatHasAlpha(int format) {
        switch (format) {
            case -3:
            case -2:
            case 1:
            case 6:
            case 7:
            case 8:
            case 10:
                return true;
            case -1:
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
            case 9:
            default:
                return false;
        }
    }
}