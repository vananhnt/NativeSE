package android.graphics;

/* loaded from: ColorFilter.class */
public class ColorFilter {
    int native_instance;
    public int nativeColorFilter;

    private static native void finalizer(int i, int i2);

    protected void finalize() throws Throwable {
        try {
            super.finalize();
            finalizer(this.native_instance, this.nativeColorFilter);
        } catch (Throwable th) {
            finalizer(this.native_instance, this.nativeColorFilter);
            throw th;
        }
    }
}