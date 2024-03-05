package android.graphics;

/* loaded from: RegionIterator.class */
public class RegionIterator {
    private final int mNativeIter;

    private static native int nativeConstructor(int i);

    private static native void nativeDestructor(int i);

    private static native boolean nativeNext(int i, Rect rect);

    public RegionIterator(Region region) {
        this.mNativeIter = nativeConstructor(region.ni());
    }

    public final boolean next(Rect r) {
        if (r == null) {
            throw new NullPointerException("The Rect must be provided");
        }
        return nativeNext(this.mNativeIter, r);
    }

    protected void finalize() throws Throwable {
        nativeDestructor(this.mNativeIter);
    }
}