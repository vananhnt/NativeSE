package android.graphics;

/* loaded from: MaskFilter.class */
public class MaskFilter {
    int native_instance;

    private static native void nativeDestructor(int i);

    protected void finalize() throws Throwable {
        nativeDestructor(this.native_instance);
    }
}