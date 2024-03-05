package android.graphics;

@Deprecated
/* loaded from: PixelXorXfermode.class */
public class PixelXorXfermode extends Xfermode {
    private static native int nativeCreate(int i);

    public PixelXorXfermode(int opColor) {
        this.native_instance = nativeCreate(opColor);
    }
}