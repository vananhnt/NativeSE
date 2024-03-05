package android.graphics;

/* loaded from: NinePatch.class */
public class NinePatch {
    private final Bitmap mBitmap;
    public final int mNativeChunk;
    private Paint mPaint;
    private String mSrcName;

    public static native boolean isNinePatchChunk(byte[] bArr);

    private static native int validateNinePatchChunk(int i, byte[] bArr);

    private static native void nativeFinalize(int i);

    private static native void nativeDraw(int i, RectF rectF, int i2, int i3, int i4, int i5, int i6);

    private static native void nativeDraw(int i, Rect rect, int i2, int i3, int i4, int i5, int i6);

    private static native int nativeGetTransparentRegion(int i, int i2, Rect rect);

    public NinePatch(Bitmap bitmap, byte[] chunk) {
        this(bitmap, chunk, null);
    }

    public NinePatch(Bitmap bitmap, byte[] chunk, String srcName) {
        this.mBitmap = bitmap;
        this.mSrcName = srcName;
        this.mNativeChunk = validateNinePatchChunk(this.mBitmap.ni(), chunk);
    }

    public NinePatch(NinePatch patch) {
        this.mBitmap = patch.mBitmap;
        this.mSrcName = patch.mSrcName;
        if (patch.mPaint != null) {
            this.mPaint = new Paint(patch.mPaint);
        }
        this.mNativeChunk = patch.mNativeChunk;
    }

    protected void finalize() throws Throwable {
        try {
            nativeFinalize(this.mNativeChunk);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public String getName() {
        return this.mSrcName;
    }

    public Paint getPaint() {
        return this.mPaint;
    }

    public void setPaint(Paint p) {
        this.mPaint = p;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public void draw(Canvas canvas, RectF location) {
        canvas.drawPatch(this, location, this.mPaint);
    }

    public void draw(Canvas canvas, Rect location) {
        canvas.drawPatch(this, location, this.mPaint);
    }

    public void draw(Canvas canvas, Rect location, Paint paint) {
        canvas.drawPatch(this, location, paint);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void drawSoftware(Canvas canvas, RectF location, Paint paint) {
        nativeDraw(canvas.mNativeCanvas, location, this.mBitmap.ni(), this.mNativeChunk, paint != null ? paint.mNativePaint : 0, canvas.mDensity, this.mBitmap.mDensity);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void drawSoftware(Canvas canvas, Rect location, Paint paint) {
        nativeDraw(canvas.mNativeCanvas, location, this.mBitmap.ni(), this.mNativeChunk, paint != null ? paint.mNativePaint : 0, canvas.mDensity, this.mBitmap.mDensity);
    }

    public int getDensity() {
        return this.mBitmap.mDensity;
    }

    public int getWidth() {
        return this.mBitmap.getWidth();
    }

    public int getHeight() {
        return this.mBitmap.getHeight();
    }

    public final boolean hasAlpha() {
        return this.mBitmap.hasAlpha();
    }

    public final Region getTransparentRegion(Rect bounds) {
        int r = nativeGetTransparentRegion(this.mBitmap.ni(), this.mNativeChunk, bounds);
        if (r != 0) {
            return new Region(r);
        }
        return null;
    }
}