package android.graphics;

/* loaded from: Rasterizer.class */
public class Rasterizer {
    int native_instance;

    private static native void finalizer(int i);

    protected void finalize() throws Throwable {
        finalizer(this.native_instance);
    }
}