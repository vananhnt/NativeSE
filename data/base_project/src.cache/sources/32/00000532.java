package android.graphics;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: BitmapRegionDecoder.class */
public final class BitmapRegionDecoder {
    private int mNativeBitmapRegionDecoder;
    private final Object mNativeLock = new Object();
    private boolean mRecycled = false;

    private static native Bitmap nativeDecodeRegion(int i, int i2, int i3, int i4, int i5, BitmapFactory.Options options);

    private static native int nativeGetWidth(int i);

    private static native int nativeGetHeight(int i);

    private static native void nativeClean(int i);

    private static native BitmapRegionDecoder nativeNewInstance(byte[] bArr, int i, int i2, boolean z);

    private static native BitmapRegionDecoder nativeNewInstance(FileDescriptor fileDescriptor, boolean z);

    private static native BitmapRegionDecoder nativeNewInstance(InputStream inputStream, byte[] bArr, boolean z);

    private static native BitmapRegionDecoder nativeNewInstance(int i, boolean z);

    public static BitmapRegionDecoder newInstance(byte[] data, int offset, int length, boolean isShareable) throws IOException {
        if ((offset | length) < 0 || data.length < offset + length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return nativeNewInstance(data, offset, length, isShareable);
    }

    public static BitmapRegionDecoder newInstance(FileDescriptor fd, boolean isShareable) throws IOException {
        return nativeNewInstance(fd, isShareable);
    }

    public static BitmapRegionDecoder newInstance(InputStream is, boolean isShareable) throws IOException {
        if (is instanceof AssetManager.AssetInputStream) {
            return nativeNewInstance(((AssetManager.AssetInputStream) is).getAssetInt(), isShareable);
        }
        byte[] tempStorage = new byte[16384];
        return nativeNewInstance(is, tempStorage, isShareable);
    }

    public static BitmapRegionDecoder newInstance(String pathName, boolean isShareable) throws IOException {
        InputStream stream = null;
        try {
            stream = new FileInputStream(pathName);
            BitmapRegionDecoder decoder = newInstance(stream, isShareable);
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
            return decoder;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    private BitmapRegionDecoder(int decoder) {
        this.mNativeBitmapRegionDecoder = decoder;
    }

    public Bitmap decodeRegion(Rect rect, BitmapFactory.Options options) {
        Bitmap nativeDecodeRegion;
        synchronized (this.mNativeLock) {
            checkRecycled("decodeRegion called on recycled region decoder");
            if (rect.right <= 0 || rect.bottom <= 0 || rect.left >= getWidth() || rect.top >= getHeight()) {
                throw new IllegalArgumentException("rectangle is outside the image");
            }
            nativeDecodeRegion = nativeDecodeRegion(this.mNativeBitmapRegionDecoder, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top, options);
        }
        return nativeDecodeRegion;
    }

    public int getWidth() {
        int nativeGetWidth;
        synchronized (this.mNativeLock) {
            checkRecycled("getWidth called on recycled region decoder");
            nativeGetWidth = nativeGetWidth(this.mNativeBitmapRegionDecoder);
        }
        return nativeGetWidth;
    }

    public int getHeight() {
        int nativeGetHeight;
        synchronized (this.mNativeLock) {
            checkRecycled("getHeight called on recycled region decoder");
            nativeGetHeight = nativeGetHeight(this.mNativeBitmapRegionDecoder);
        }
        return nativeGetHeight;
    }

    public void recycle() {
        synchronized (this.mNativeLock) {
            if (!this.mRecycled) {
                nativeClean(this.mNativeBitmapRegionDecoder);
                this.mRecycled = true;
            }
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

    protected void finalize() throws Throwable {
        try {
            recycle();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }
}