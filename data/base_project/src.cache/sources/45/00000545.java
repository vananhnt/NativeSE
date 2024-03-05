package android.graphics;

/* loaded from: EmbossMaskFilter.class */
public class EmbossMaskFilter extends MaskFilter {
    private static native int nativeConstructor(float[] fArr, float f, float f2, float f3);

    public EmbossMaskFilter(float[] direction, float ambient, float specular, float blurRadius) {
        if (direction.length < 3) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.native_instance = nativeConstructor(direction, ambient, specular, blurRadius);
    }
}