package android.graphics;

/* loaded from: LayerRasterizer.class */
public class LayerRasterizer extends Rasterizer {
    private static native int nativeConstructor();

    private static native void nativeAddLayer(int i, int i2, float f, float f2);

    public LayerRasterizer() {
        this.native_instance = nativeConstructor();
    }

    public void addLayer(Paint paint, float dx, float dy) {
        nativeAddLayer(this.native_instance, paint.mNativePaint, dx, dy);
    }

    public void addLayer(Paint paint) {
        nativeAddLayer(this.native_instance, paint.mNativePaint, 0.0f, 0.0f);
    }
}