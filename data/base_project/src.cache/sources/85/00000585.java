package android.graphics;

/* loaded from: Xfermode.class */
public class Xfermode {
    int native_instance;

    private static native void finalizer(int i);

    protected void finalize() throws Throwable {
        try {
            finalizer(this.native_instance);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }
}