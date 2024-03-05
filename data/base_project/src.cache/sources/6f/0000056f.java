package android.graphics;

import android.graphics.PorterDuff;

/* loaded from: PorterDuffXfermode.class */
public class PorterDuffXfermode extends Xfermode {
    public final PorterDuff.Mode mode;

    private static native int nativeCreateXfermode(int i);

    public PorterDuffXfermode(PorterDuff.Mode mode) {
        this.mode = mode;
        this.native_instance = nativeCreateXfermode(mode.nativeInt);
    }
}