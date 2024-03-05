package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/* loaded from: Bitmap.class */
public final class Bitmap implements Parcelable {
    public static final int DENSITY_NONE = 0;
    public final int mNativeBitmap;
    public byte[] mBuffer;
    private final BitmapFinalizer mFinalizer;
    private final boolean mIsMutable;
    private boolean mIsPremultiplied;
    private byte[] mNinePatchChunk;
    private int[] mLayoutBounds;
    private int mWidth;
    private int mHeight;
    private boolean mRecycled;
    int mDensity;
    private static volatile Matrix sScaleMatrix;
    private static final int WORKING_COMPRESS_STORAGE = 4096;
    private static volatile int sDefaultDensity = -1;
    public static final Parcelable.Creator<Bitmap> CREATOR = new Parcelable.Creator<Bitmap>() { // from class: android.graphics.Bitmap.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Bitmap createFromParcel(Parcel p) {
            Bitmap bm = Bitmap.nativeCreateFromParcel(p);
            if (bm == null) {
                throw new RuntimeException("Failed to unparcel Bitmap");
            }
            return bm;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Bitmap[] newArray(int size) {
            return new Bitmap[size];
        }
    };

    private static native Bitmap nativeCreate(int[] iArr, int i, int i2, int i3, int i4, int i5, boolean z);

    private static native Bitmap nativeCopy(int i, int i2, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeDestructor(int i);

    private static native boolean nativeRecycle(int i);

    private static native void nativeReconfigure(int i, int i2, int i3, int i4, int i5);

    private static native boolean nativeCompress(int i, int i2, int i3, OutputStream outputStream, byte[] bArr);

    private static native void nativeErase(int i, int i2);

    private static native int nativeRowBytes(int i);

    private static native int nativeConfig(int i);

    private static native int nativeGetPixel(int i, int i2, int i3, boolean z);

    private static native void nativeGetPixels(int i, int[] iArr, int i2, int i3, int i4, int i5, int i6, int i7, boolean z);

    private static native void nativeSetPixel(int i, int i2, int i3, int i4, boolean z);

    private static native void nativeSetPixels(int i, int[] iArr, int i2, int i3, int i4, int i5, int i6, int i7, boolean z);

    private static native void nativeCopyPixelsToBuffer(int i, Buffer buffer);

    private static native void nativeCopyPixelsFromBuffer(int i, Buffer buffer);

    private static native int nativeGenerationId(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native Bitmap nativeCreateFromParcel(Parcel parcel);

    private static native boolean nativeWriteToParcel(int i, boolean z, int i2, Parcel parcel);

    private static native Bitmap nativeExtractAlpha(int i, int i2, int[] iArr);

    private static native void nativePrepareToDraw(int i);

    private static native boolean nativeHasAlpha(int i);

    private static native void nativeSetHasAlpha(int i, boolean z);

    private static native boolean nativeHasMipMap(int i);

    private static native void nativeSetHasMipMap(int i, boolean z);

    private static native boolean nativeSameAs(int i, int i2);

    static /* synthetic */ void access$100(int x0) {
        nativeDestructor(x0);
    }

    public static void setDefaultDensity(int density) {
        sDefaultDensity = density;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getDefaultDensity() {
        if (sDefaultDensity >= 0) {
            return sDefaultDensity;
        }
        sDefaultDensity = DisplayMetrics.DENSITY_DEVICE;
        return sDefaultDensity;
    }

    Bitmap(int nativeBitmap, byte[] buffer, int width, int height, int density, boolean isMutable, boolean isPremultiplied, byte[] ninePatchChunk, int[] layoutBounds) {
        this.mDensity = getDefaultDensity();
        if (nativeBitmap == 0) {
            throw new RuntimeException("internal error: native bitmap is 0");
        }
        this.mWidth = width;
        this.mHeight = height;
        this.mIsMutable = isMutable;
        this.mIsPremultiplied = isPremultiplied;
        this.mBuffer = buffer;
        this.mNativeBitmap = nativeBitmap;
        this.mFinalizer = new BitmapFinalizer(nativeBitmap);
        this.mNinePatchChunk = ninePatchChunk;
        this.mLayoutBounds = layoutBounds;
        if (density >= 0) {
            this.mDensity = density;
        }
    }

    void reinit(int width, int height, boolean isPremultiplied) {
        this.mWidth = width;
        this.mHeight = height;
        this.mIsPremultiplied = isPremultiplied;
    }

    public int getDensity() {
        return this.mDensity;
    }

    public void setDensity(int density) {
        this.mDensity = density;
    }

    public void reconfigure(int width, int height, Config config) {
        checkRecycled("Can't call reconfigure() on a recycled bitmap");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
        if (!isMutable()) {
            throw new IllegalStateException("only mutable bitmaps may be reconfigured");
        }
        if (this.mBuffer == null) {
            throw new IllegalStateException("native-backed bitmaps may not be reconfigured");
        }
        nativeReconfigure(this.mNativeBitmap, width, height, config.nativeInt, this.mBuffer.length);
        this.mWidth = width;
        this.mHeight = height;
    }

    public void setWidth(int width) {
        reconfigure(width, getHeight(), getConfig());
    }

    public void setHeight(int height) {
        reconfigure(getWidth(), height, getConfig());
    }

    public void setConfig(Config config) {
        reconfigure(getWidth(), getHeight(), config);
    }

    public void setNinePatchChunk(byte[] chunk) {
        this.mNinePatchChunk = chunk;
    }

    public void setLayoutBounds(int[] bounds) {
        this.mLayoutBounds = bounds;
    }

    public void recycle() {
        if (!this.mRecycled) {
            if (nativeRecycle(this.mNativeBitmap)) {
                this.mBuffer = null;
                this.mNinePatchChunk = null;
            }
            this.mRecycled = true;
        }
    }

    public final boolean isRecycled() {
        return this.mRecycled;
    }

    public int getGenerationId() {
        return nativeGenerationId(this.mNativeBitmap);
    }

    private void checkRecycled(String errorMessage) {
        if (this.mRecycled) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private static void checkXYSign(int x, int y) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be >= 0");
        }
        if (y < 0) {
            throw new IllegalArgumentException("y must be >= 0");
        }
    }

    private static void checkWidthHeight(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be > 0");
        }
    }

    /* loaded from: Bitmap$Config.class */
    public enum Config {
        ALPHA_8(2),
        RGB_565(4),
        ARGB_4444(5),
        ARGB_8888(6);
        
        final int nativeInt;
        private static Config[] sConfigs = {null, null, ALPHA_8, null, RGB_565, ARGB_4444, ARGB_8888};

        Config(int ni) {
            this.nativeInt = ni;
        }

        static Config nativeToConfig(int ni) {
            return sConfigs[ni];
        }
    }

    public void copyPixelsToBuffer(Buffer dst) {
        int shift;
        int elements = dst.remaining();
        if (dst instanceof ByteBuffer) {
            shift = 0;
        } else if (dst instanceof ShortBuffer) {
            shift = 1;
        } else if (dst instanceof IntBuffer) {
            shift = 2;
        } else {
            throw new RuntimeException("unsupported Buffer subclass");
        }
        long bufferSize = elements << shift;
        long pixelSize = getByteCount();
        if (bufferSize < pixelSize) {
            throw new RuntimeException("Buffer not large enough for pixels");
        }
        nativeCopyPixelsToBuffer(this.mNativeBitmap, dst);
        int position = dst.position();
        dst.position((int) (position + (pixelSize >> shift)));
    }

    public void copyPixelsFromBuffer(Buffer src) {
        int shift;
        checkRecycled("copyPixelsFromBuffer called on recycled bitmap");
        int elements = src.remaining();
        if (src instanceof ByteBuffer) {
            shift = 0;
        } else if (src instanceof ShortBuffer) {
            shift = 1;
        } else if (src instanceof IntBuffer) {
            shift = 2;
        } else {
            throw new RuntimeException("unsupported Buffer subclass");
        }
        long bufferBytes = elements << shift;
        long bitmapBytes = getByteCount();
        if (bufferBytes < bitmapBytes) {
            throw new RuntimeException("Buffer not large enough for pixels");
        }
        nativeCopyPixelsFromBuffer(this.mNativeBitmap, src);
        int position = src.position();
        src.position((int) (position + (bitmapBytes >> shift)));
    }

    public Bitmap copy(Config config, boolean isMutable) {
        checkRecycled("Can't copy a recycled bitmap");
        Bitmap b = nativeCopy(this.mNativeBitmap, config.nativeInt, isMutable);
        if (b != null) {
            b.mIsPremultiplied = this.mIsPremultiplied;
            b.mDensity = this.mDensity;
        }
        return b;
    }

    public static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter) {
        Matrix m;
        synchronized (Bitmap.class) {
            m = sScaleMatrix;
            sScaleMatrix = null;
        }
        if (m == null) {
            m = new Matrix();
        }
        int width = src.getWidth();
        int height = src.getHeight();
        float sx = dstWidth / width;
        float sy = dstHeight / height;
        m.setScale(sx, sy);
        Bitmap b = createBitmap(src, 0, 0, width, height, m, filter);
        synchronized (Bitmap.class) {
            if (sScaleMatrix == null) {
                sScaleMatrix = m;
            }
        }
        return b;
    }

    public static Bitmap createBitmap(Bitmap src) {
        return createBitmap(src, 0, 0, src.getWidth(), src.getHeight());
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height) {
        return createBitmap(source, x, y, width, height, (Matrix) null, false);
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height, Matrix m, boolean filter) {
        Bitmap bitmap;
        Paint paint;
        checkXYSign(x, y);
        checkWidthHeight(width, height);
        if (x + width > source.getWidth()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        }
        if (y + height > source.getHeight()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        }
        if (!source.isMutable() && x == 0 && y == 0 && width == source.getWidth() && height == source.getHeight() && (m == null || m.isIdentity())) {
            return source;
        }
        Canvas canvas = new Canvas();
        Rect srcR = new Rect(x, y, x + width, y + height);
        RectF dstR = new RectF(0.0f, 0.0f, width, height);
        Config newConfig = Config.ARGB_8888;
        Config config = source.getConfig();
        if (config != null) {
            switch (config) {
                case RGB_565:
                    newConfig = Config.RGB_565;
                    break;
                case ALPHA_8:
                    newConfig = Config.ALPHA_8;
                    break;
                case ARGB_4444:
                case ARGB_8888:
                default:
                    newConfig = Config.ARGB_8888;
                    break;
            }
        }
        if (m == null || m.isIdentity()) {
            bitmap = createBitmap(width, height, newConfig, source.hasAlpha());
            paint = null;
        } else {
            boolean transformed = !m.rectStaysRect();
            RectF deviceR = new RectF();
            m.mapRect(deviceR, dstR);
            int neww = Math.round(deviceR.width());
            int newh = Math.round(deviceR.height());
            bitmap = createBitmap(neww, newh, transformed ? Config.ARGB_8888 : newConfig, transformed || source.hasAlpha());
            canvas.translate(-deviceR.left, -deviceR.top);
            canvas.concat(m);
            paint = new Paint();
            paint.setFilterBitmap(filter);
            if (transformed) {
                paint.setAntiAlias(true);
            }
        }
        bitmap.mDensity = source.mDensity;
        bitmap.mIsPremultiplied = source.mIsPremultiplied;
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(source, srcR, dstR, paint);
        canvas.setBitmap(null);
        return bitmap;
    }

    public static Bitmap createBitmap(int width, int height, Config config) {
        return createBitmap(width, height, config, true);
    }

    public static Bitmap createBitmap(DisplayMetrics display, int width, int height, Config config) {
        return createBitmap(display, width, height, config, true);
    }

    private static Bitmap createBitmap(int width, int height, Config config, boolean hasAlpha) {
        return createBitmap((DisplayMetrics) null, width, height, config, hasAlpha);
    }

    private static Bitmap createBitmap(DisplayMetrics display, int width, int height, Config config, boolean hasAlpha) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
        Bitmap bm = nativeCreate(null, 0, width, width, height, config.nativeInt, true);
        if (display != null) {
            bm.mDensity = display.densityDpi;
        }
        if (config == Config.ARGB_8888 && !hasAlpha) {
            nativeErase(bm.mNativeBitmap, -16777216);
            nativeSetHasAlpha(bm.mNativeBitmap, hasAlpha);
        }
        return bm;
    }

    public static Bitmap createBitmap(int[] colors, int offset, int stride, int width, int height, Config config) {
        return createBitmap((DisplayMetrics) null, colors, offset, stride, width, height, config);
    }

    public static Bitmap createBitmap(DisplayMetrics display, int[] colors, int offset, int stride, int width, int height, Config config) {
        checkWidthHeight(width, height);
        if (Math.abs(stride) < width) {
            throw new IllegalArgumentException("abs(stride) must be >= width");
        }
        int lastScanline = offset + ((height - 1) * stride);
        int length = colors.length;
        if (offset < 0 || offset + width > length || lastScanline < 0 || lastScanline + width > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
        Bitmap bm = nativeCreate(colors, offset, stride, width, height, config.nativeInt, false);
        if (display != null) {
            bm.mDensity = display.densityDpi;
        }
        return bm;
    }

    public static Bitmap createBitmap(int[] colors, int width, int height, Config config) {
        return createBitmap((DisplayMetrics) null, colors, 0, width, width, height, config);
    }

    public static Bitmap createBitmap(DisplayMetrics display, int[] colors, int width, int height, Config config) {
        return createBitmap(display, colors, 0, width, width, height, config);
    }

    public byte[] getNinePatchChunk() {
        return this.mNinePatchChunk;
    }

    public int[] getLayoutBounds() {
        return this.mLayoutBounds;
    }

    /* loaded from: Bitmap$CompressFormat.class */
    public enum CompressFormat {
        JPEG(0),
        PNG(1),
        WEBP(2);
        
        final int nativeInt;

        CompressFormat(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public boolean compress(CompressFormat format, int quality, OutputStream stream) {
        checkRecycled("Can't compress a recycled bitmap");
        if (stream == null) {
            throw new NullPointerException();
        }
        if (quality < 0 || quality > 100) {
            throw new IllegalArgumentException("quality must be 0..100");
        }
        return nativeCompress(this.mNativeBitmap, format.nativeInt, quality, stream, new byte[4096]);
    }

    public final boolean isMutable() {
        return this.mIsMutable;
    }

    public final boolean isPremultiplied() {
        return this.mIsPremultiplied && getConfig() != Config.RGB_565 && hasAlpha();
    }

    public final void setPremultiplied(boolean premultiplied) {
        this.mIsPremultiplied = premultiplied;
    }

    public final int getWidth() {
        return this.mWidth;
    }

    public final int getHeight() {
        return this.mHeight;
    }

    public int getScaledWidth(Canvas canvas) {
        return scaleFromDensity(getWidth(), this.mDensity, canvas.mDensity);
    }

    public int getScaledHeight(Canvas canvas) {
        return scaleFromDensity(getHeight(), this.mDensity, canvas.mDensity);
    }

    public int getScaledWidth(DisplayMetrics metrics) {
        return scaleFromDensity(getWidth(), this.mDensity, metrics.densityDpi);
    }

    public int getScaledHeight(DisplayMetrics metrics) {
        return scaleFromDensity(getHeight(), this.mDensity, metrics.densityDpi);
    }

    public int getScaledWidth(int targetDensity) {
        return scaleFromDensity(getWidth(), this.mDensity, targetDensity);
    }

    public int getScaledHeight(int targetDensity) {
        return scaleFromDensity(getHeight(), this.mDensity, targetDensity);
    }

    public static int scaleFromDensity(int size, int sdensity, int tdensity) {
        if (sdensity == 0 || tdensity == 0 || sdensity == tdensity) {
            return size;
        }
        return ((size * tdensity) + (sdensity >> 1)) / sdensity;
    }

    public final int getRowBytes() {
        return nativeRowBytes(this.mNativeBitmap);
    }

    public final int getByteCount() {
        return getRowBytes() * getHeight();
    }

    public final int getAllocationByteCount() {
        return this.mBuffer.length;
    }

    public final Config getConfig() {
        return Config.nativeToConfig(nativeConfig(this.mNativeBitmap));
    }

    public final boolean hasAlpha() {
        return nativeHasAlpha(this.mNativeBitmap);
    }

    public void setHasAlpha(boolean hasAlpha) {
        nativeSetHasAlpha(this.mNativeBitmap, hasAlpha);
    }

    public final boolean hasMipMap() {
        return nativeHasMipMap(this.mNativeBitmap);
    }

    public final void setHasMipMap(boolean hasMipMap) {
        nativeSetHasMipMap(this.mNativeBitmap, hasMipMap);
    }

    public void eraseColor(int c) {
        checkRecycled("Can't erase a recycled bitmap");
        if (!isMutable()) {
            throw new IllegalStateException("cannot erase immutable bitmaps");
        }
        nativeErase(this.mNativeBitmap, c);
    }

    public int getPixel(int x, int y) {
        checkRecycled("Can't call getPixel() on a recycled bitmap");
        checkPixelAccess(x, y);
        return nativeGetPixel(this.mNativeBitmap, x, y, this.mIsPremultiplied);
    }

    public void getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
        checkRecycled("Can't call getPixels() on a recycled bitmap");
        if (width == 0 || height == 0) {
            return;
        }
        checkPixelsAccess(x, y, width, height, offset, stride, pixels);
        nativeGetPixels(this.mNativeBitmap, pixels, offset, stride, x, y, width, height, this.mIsPremultiplied);
    }

    private void checkPixelAccess(int x, int y) {
        checkXYSign(x, y);
        if (x >= getWidth()) {
            throw new IllegalArgumentException("x must be < bitmap.width()");
        }
        if (y >= getHeight()) {
            throw new IllegalArgumentException("y must be < bitmap.height()");
        }
    }

    private void checkPixelsAccess(int x, int y, int width, int height, int offset, int stride, int[] pixels) {
        checkXYSign(x, y);
        if (width < 0) {
            throw new IllegalArgumentException("width must be >= 0");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height must be >= 0");
        }
        if (x + width > getWidth()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        }
        if (y + height > getHeight()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        }
        if (Math.abs(stride) < width) {
            throw new IllegalArgumentException("abs(stride) must be >= width");
        }
        int lastScanline = offset + ((height - 1) * stride);
        int length = pixels.length;
        if (offset < 0 || offset + width > length || lastScanline < 0 || lastScanline + width > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void setPixel(int x, int y, int color) {
        checkRecycled("Can't call setPixel() on a recycled bitmap");
        if (!isMutable()) {
            throw new IllegalStateException();
        }
        checkPixelAccess(x, y);
        nativeSetPixel(this.mNativeBitmap, x, y, color, this.mIsPremultiplied);
    }

    public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
        checkRecycled("Can't call setPixels() on a recycled bitmap");
        if (!isMutable()) {
            throw new IllegalStateException();
        }
        if (width == 0 || height == 0) {
            return;
        }
        checkPixelsAccess(x, y, width, height, offset, stride, pixels);
        nativeSetPixels(this.mNativeBitmap, pixels, offset, stride, x, y, width, height, this.mIsPremultiplied);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel p, int flags) {
        checkRecycled("Can't parcel a recycled bitmap");
        if (!nativeWriteToParcel(this.mNativeBitmap, this.mIsMutable, this.mDensity, p)) {
            throw new RuntimeException("native writeToParcel failed");
        }
    }

    public Bitmap extractAlpha() {
        return extractAlpha(null, null);
    }

    public Bitmap extractAlpha(Paint paint, int[] offsetXY) {
        checkRecycled("Can't extractAlpha on a recycled bitmap");
        int nativePaint = paint != null ? paint.mNativePaint : 0;
        Bitmap bm = nativeExtractAlpha(this.mNativeBitmap, nativePaint, offsetXY);
        if (bm == null) {
            throw new RuntimeException("Failed to extractAlpha on Bitmap");
        }
        bm.mDensity = this.mDensity;
        return bm;
    }

    public boolean sameAs(Bitmap other) {
        return this == other || (other != null && nativeSameAs(this.mNativeBitmap, other.mNativeBitmap));
    }

    public void prepareToDraw() {
        nativePrepareToDraw(this.mNativeBitmap);
    }

    /* loaded from: Bitmap$BitmapFinalizer.class */
    private static class BitmapFinalizer {
        private final int mNativeBitmap;

        BitmapFinalizer(int nativeBitmap) {
            this.mNativeBitmap = nativeBitmap;
        }

        /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
            jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:7:0x0019
            	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:81)
            	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:47)
            	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
            */
        public void finalize() {
            /*
                r2 = this;
                r0 = r2
                super.finalize()     // Catch: java.lang.Throwable -> Le
                r0 = r2
                int r0 = r0.mNativeBitmap
                android.graphics.Bitmap.access$100(r0)
                goto L23
            Le:
                r3 = move-exception
                r0 = r2
                int r0 = r0.mNativeBitmap
                android.graphics.Bitmap.access$100(r0)
                goto L23
            L19:
                r4 = move-exception
                r0 = r2
                int r0 = r0.mNativeBitmap
                android.graphics.Bitmap.access$100(r0)
                r0 = r4
                throw r0
            L23:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.graphics.Bitmap.BitmapFinalizer.finalize():void");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int ni() {
        return this.mNativeBitmap;
    }
}