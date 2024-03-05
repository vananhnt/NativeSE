package android.graphics;

/* loaded from: DrawFilter.class */
public class DrawFilter {
    int mNativeInt;

    private static native void nativeDestructor(int i);

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeInt);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }
}