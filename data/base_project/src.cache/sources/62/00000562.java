package android.graphics;

/* loaded from: PathEffect.class */
public class PathEffect {
    int native_instance;

    private static native void nativeDestructor(int i);

    protected void finalize() throws Throwable {
        nativeDestructor(this.native_instance);
    }
}