package android.graphics;

/* loaded from: LightingColorFilter.class */
public class LightingColorFilter extends ColorFilter {
    private static native int native_CreateLightingFilter(int i, int i2);

    private static native int nCreateLightingFilter(int i, int i2, int i3);

    public LightingColorFilter(int mul, int add) {
        this.native_instance = native_CreateLightingFilter(mul, add);
        this.nativeColorFilter = nCreateLightingFilter(this.native_instance, mul, add);
    }
}