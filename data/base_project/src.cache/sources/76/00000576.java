package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pools;

/* loaded from: Region.class */
public class Region implements Parcelable {
    private static final int MAX_POOL_SIZE = 10;
    public final int mNativeRegion;
    private static final Pools.SynchronizedPool<Region> sPool = new Pools.SynchronizedPool<>(10);
    public static final Parcelable.Creator<Region> CREATOR = new Parcelable.Creator<Region>() { // from class: android.graphics.Region.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Region createFromParcel(Parcel p) {
            int ni = Region.nativeCreateFromParcel(p);
            if (ni == 0) {
                throw new RuntimeException();
            }
            return new Region(ni);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };

    public native boolean isEmpty();

    public native boolean isRect();

    public native boolean isComplex();

    public native boolean contains(int i, int i2);

    public native boolean quickContains(int i, int i2, int i3, int i4);

    public native boolean quickReject(int i, int i2, int i3, int i4);

    public native boolean quickReject(Region region);

    public native void translate(int i, int i2, Region region);

    public native void scale(float f, Region region);

    private static native boolean nativeEquals(int i, int i2);

    private static native int nativeConstructor();

    private static native void nativeDestructor(int i);

    private static native void nativeSetRegion(int i, int i2);

    private static native boolean nativeSetRect(int i, int i2, int i3, int i4, int i5);

    private static native boolean nativeSetPath(int i, int i2, int i3);

    private static native boolean nativeGetBounds(int i, Rect rect);

    private static native boolean nativeGetBoundaryPath(int i, int i2);

    private static native boolean nativeOp(int i, int i2, int i3, int i4, int i5, int i6);

    private static native boolean nativeOp(int i, Rect rect, int i2, int i3);

    private static native boolean nativeOp(int i, int i2, int i3, int i4);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nativeCreateFromParcel(Parcel parcel);

    private static native boolean nativeWriteToParcel(int i, Parcel parcel);

    private static native String nativeToString(int i);

    /* loaded from: Region$Op.class */
    public enum Op {
        DIFFERENCE(0),
        INTERSECT(1),
        UNION(2),
        XOR(3),
        REVERSE_DIFFERENCE(4),
        REPLACE(5);
        
        public final int nativeInt;

        Op(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public Region() {
        this(nativeConstructor());
    }

    public Region(Region region) {
        this(nativeConstructor());
        nativeSetRegion(this.mNativeRegion, region.mNativeRegion);
    }

    public Region(Rect r) {
        this.mNativeRegion = nativeConstructor();
        nativeSetRect(this.mNativeRegion, r.left, r.top, r.right, r.bottom);
    }

    public Region(int left, int top, int right, int bottom) {
        this.mNativeRegion = nativeConstructor();
        nativeSetRect(this.mNativeRegion, left, top, right, bottom);
    }

    public void setEmpty() {
        nativeSetRect(this.mNativeRegion, 0, 0, 0, 0);
    }

    public boolean set(Region region) {
        nativeSetRegion(this.mNativeRegion, region.mNativeRegion);
        return true;
    }

    public boolean set(Rect r) {
        return nativeSetRect(this.mNativeRegion, r.left, r.top, r.right, r.bottom);
    }

    public boolean set(int left, int top, int right, int bottom) {
        return nativeSetRect(this.mNativeRegion, left, top, right, bottom);
    }

    public boolean setPath(Path path, Region clip) {
        return nativeSetPath(this.mNativeRegion, path.ni(), clip.mNativeRegion);
    }

    public Rect getBounds() {
        Rect r = new Rect();
        nativeGetBounds(this.mNativeRegion, r);
        return r;
    }

    public boolean getBounds(Rect r) {
        if (r == null) {
            throw new NullPointerException();
        }
        return nativeGetBounds(this.mNativeRegion, r);
    }

    public Path getBoundaryPath() {
        Path path = new Path();
        nativeGetBoundaryPath(this.mNativeRegion, path.ni());
        return path;
    }

    public boolean getBoundaryPath(Path path) {
        return nativeGetBoundaryPath(this.mNativeRegion, path.ni());
    }

    public boolean quickContains(Rect r) {
        return quickContains(r.left, r.top, r.right, r.bottom);
    }

    public boolean quickReject(Rect r) {
        return quickReject(r.left, r.top, r.right, r.bottom);
    }

    public void translate(int dx, int dy) {
        translate(dx, dy, null);
    }

    public void scale(float scale) {
        scale(scale, null);
    }

    public final boolean union(Rect r) {
        return op(r, Op.UNION);
    }

    public boolean op(Rect r, Op op) {
        return nativeOp(this.mNativeRegion, r.left, r.top, r.right, r.bottom, op.nativeInt);
    }

    public boolean op(int left, int top, int right, int bottom, Op op) {
        return nativeOp(this.mNativeRegion, left, top, right, bottom, op.nativeInt);
    }

    public boolean op(Region region, Op op) {
        return op(this, region, op);
    }

    public boolean op(Rect rect, Region region, Op op) {
        return nativeOp(this.mNativeRegion, rect, region.mNativeRegion, op.nativeInt);
    }

    public boolean op(Region region1, Region region2, Op op) {
        return nativeOp(this.mNativeRegion, region1.mNativeRegion, region2.mNativeRegion, op.nativeInt);
    }

    public String toString() {
        return nativeToString(this.mNativeRegion);
    }

    public static Region obtain() {
        Region region = sPool.acquire();
        return region != null ? region : new Region();
    }

    public static Region obtain(Region other) {
        Region region = obtain();
        region.set(other);
        return region;
    }

    public void recycle() {
        setEmpty();
        sPool.release(this);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel p, int flags) {
        if (!nativeWriteToParcel(this.mNativeRegion, p)) {
            throw new RuntimeException();
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Region)) {
            return false;
        }
        Region peer = (Region) obj;
        return nativeEquals(this.mNativeRegion, peer.mNativeRegion);
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeRegion);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Region(int ni) {
        if (ni == 0) {
            throw new RuntimeException();
        }
        this.mNativeRegion = ni;
    }

    private Region(int ni, int dummy) {
        this(ni);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int ni() {
        return this.mNativeRegion;
    }
}