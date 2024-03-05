package android.graphics;

/* loaded from: ColorMatrixColorFilter.class */
public class ColorMatrixColorFilter extends ColorFilter {
    private static native int nativeColorMatrixFilter(float[] fArr);

    private static native int nColorMatrixFilter(int i, float[] fArr);

    public ColorMatrixColorFilter(ColorMatrix matrix) {
        float[] colorMatrix = matrix.getArray();
        this.native_instance = nativeColorMatrixFilter(colorMatrix);
        this.nativeColorFilter = nColorMatrixFilter(this.native_instance, colorMatrix);
    }

    public ColorMatrixColorFilter(float[] array) {
        if (array.length < 20) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.native_instance = nativeColorMatrixFilter(array);
        this.nativeColorFilter = nColorMatrixFilter(this.native_instance, array);
    }
}