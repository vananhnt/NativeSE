package android.graphics;

/* loaded from: BlurMaskFilter.class */
public class BlurMaskFilter extends MaskFilter {
    private static native int nativeConstructor(float f, int i);

    /* loaded from: BlurMaskFilter$Blur.class */
    public enum Blur {
        NORMAL(0),
        SOLID(1),
        OUTER(2),
        INNER(3);
        
        final int native_int;

        Blur(int value) {
            this.native_int = value;
        }
    }

    public BlurMaskFilter(float radius, Blur style) {
        this.native_instance = nativeConstructor(radius, style.native_int);
    }
}