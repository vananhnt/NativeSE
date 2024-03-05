package android.graphics;

import android.graphics.BitmapFactory;

/* loaded from: LargeBitmap.class */
public final class LargeBitmap {
    private int mNativeLargeBitmap;
    private boolean mRecycled = false;

    private static native Bitmap nativeDecodeRegion(int i, int i2, int i3, int i4, int i5, BitmapFactory.Options options);

    private static native int nativeGetWidth(int i);

    private static native int nativeGetHeight(int i);

    private static native void nativeClean(int i);

    private LargeBitmap(int lbm) {
        this.mNativeLargeBitmap = lbm;
    }

    public Bitmap decodeRegion(Rect rect, BitmapFactory.Options options) {
        checkRecycled("decodeRegion called on recycled large bitmap");
        if (rect.left < 0 || rect.top < 0 || rect.right > getWidth() || rect.bottom > getHeight()) {
            throw new IllegalArgumentException("rectangle is not inside the image");
        }
        return nativeDecodeRegion(this.mNativeLargeBitmap, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top, options);
    }

    public int getWidth() {
        checkRecycled("getWidth called on recycled large bitmap");
        return nativeGetWidth(this.mNativeLargeBitmap);
    }

    public int getHeight() {
        checkRecycled("getHeight called on recycled large bitmap");
        return nativeGetHeight(this.mNativeLargeBitmap);
    }

    public void recycle() {
        if (!this.mRecycled) {
            nativeClean(this.mNativeLargeBitmap);
            this.mRecycled = true;
        }
    }

    public final boolean isRecycled() {
        return this.mRecycled;
    }

    private void checkRecycled(String errorMessage) {
        if (this.mRecycled) {
            throw new IllegalStateException(errorMessage);
        }
    }

    protected void finalize() {
        recycle();
    }
}