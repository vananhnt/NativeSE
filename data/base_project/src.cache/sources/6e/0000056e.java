package android.graphics;

import android.graphics.PorterDuff;

/* loaded from: PorterDuffColorFilter.class */
public class PorterDuffColorFilter extends ColorFilter {
    private static native int native_CreatePorterDuffFilter(int i, int i2);

    private static native int nCreatePorterDuffFilter(int i, int i2, int i3);

    public PorterDuffColorFilter(int srcColor, PorterDuff.Mode mode) {
        this.native_instance = native_CreatePorterDuffFilter(srcColor, mode.nativeInt);
        this.nativeColorFilter = nCreatePorterDuffFilter(this.native_instance, srcColor, mode.nativeInt);
    }
}