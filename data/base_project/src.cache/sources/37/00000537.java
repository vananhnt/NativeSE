package android.graphics;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Region;
import android.text.GraphicsOperations;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import javax.microedition.khronos.opengles.GL;

/* loaded from: Canvas.class */
public class Canvas {
    public int mNativeCanvas;
    private Bitmap mBitmap;
    private DrawFilter mDrawFilter;
    protected int mDensity;
    protected int mScreenDensity;
    private int mSurfaceFormat;
    public static final int DIRECTION_LTR = 0;
    public static final int DIRECTION_RTL = 1;
    private static final int MAXMIMUM_BITMAP_SIZE = 32766;
    private final CanvasFinalizer mFinalizer;
    public static final int MATRIX_SAVE_FLAG = 1;
    public static final int CLIP_SAVE_FLAG = 2;
    public static final int HAS_ALPHA_LAYER_SAVE_FLAG = 4;
    public static final int FULL_COLOR_LAYER_SAVE_FLAG = 8;
    public static final int CLIP_TO_LAYER_SAVE_FLAG = 16;
    public static final int ALL_SAVE_FLAG = 31;

    public native boolean isOpaque();

    public native int getWidth();

    public native int getHeight();

    public native int save();

    public native int save(int i);

    public native void restore();

    public native int getSaveCount();

    public native void restoreToCount(int i);

    public native void translate(float f, float f2);

    public native void scale(float f, float f2);

    public native void rotate(float f);

    public native void skew(float f, float f2);

    public native boolean clipRect(RectF rectF);

    public native boolean clipRect(Rect rect);

    public native boolean clipRect(float f, float f2, float f3, float f4);

    public native boolean clipRect(int i, int i2, int i3, int i4);

    public native void drawPoints(float[] fArr, int i, int i2, Paint paint);

    public native void drawPoint(float f, float f2, Paint paint);

    public native void drawLines(float[] fArr, int i, int i2, Paint paint);

    public static native void freeCaches();

    public static native void freeTextLayoutCaches();

    private static native int initRaster(int i);

    private static native void copyNativeCanvasState(int i, int i2);

    private static native int native_saveLayer(int i, RectF rectF, int i2, int i3);

    private static native int native_saveLayer(int i, float f, float f2, float f3, float f4, int i2, int i3);

    private static native int native_saveLayerAlpha(int i, RectF rectF, int i2, int i3);

    private static native int native_saveLayerAlpha(int i, float f, float f2, float f3, float f4, int i2, int i3);

    private static native void native_concat(int i, int i2);

    private static native void native_setMatrix(int i, int i2);

    private static native boolean native_clipRect(int i, float f, float f2, float f3, float f4, int i2);

    private static native boolean native_clipPath(int i, int i2, int i3);

    private static native boolean native_clipRegion(int i, int i2, int i3);

    private static native void nativeSetDrawFilter(int i, int i2);

    private static native boolean native_getClipBounds(int i, Rect rect);

    private static native void native_getCTM(int i, int i2);

    private static native boolean native_quickReject(int i, RectF rectF);

    private static native boolean native_quickReject(int i, int i2);

    private static native boolean native_quickReject(int i, float f, float f2, float f3, float f4);

    private static native void native_drawRGB(int i, int i2, int i3, int i4);

    private static native void native_drawARGB(int i, int i2, int i3, int i4, int i5);

    private static native void native_drawColor(int i, int i2);

    private static native void native_drawColor(int i, int i2, int i3);

    private static native void native_drawPaint(int i, int i2);

    private static native void native_drawLine(int i, float f, float f2, float f3, float f4, int i2);

    private static native void native_drawRect(int i, RectF rectF, int i2);

    private static native void native_drawRect(int i, float f, float f2, float f3, float f4, int i2);

    private static native void native_drawOval(int i, RectF rectF, int i2);

    private static native void native_drawCircle(int i, float f, float f2, float f3, int i2);

    private static native void native_drawArc(int i, RectF rectF, float f, float f2, boolean z, int i2);

    private static native void native_drawRoundRect(int i, RectF rectF, float f, float f2, int i2);

    private static native void native_drawPath(int i, int i2, int i3);

    private native void native_drawBitmap(int i, int i2, float f, float f2, int i3, int i4, int i5, int i6);

    private native void native_drawBitmap(int i, int i2, Rect rect, RectF rectF, int i3, int i4, int i5);

    private static native void native_drawBitmap(int i, int i2, Rect rect, Rect rect2, int i3, int i4, int i5);

    private static native void native_drawBitmap(int i, int[] iArr, int i2, int i3, float f, float f2, int i4, int i5, boolean z, int i6);

    private static native void nativeDrawBitmapMatrix(int i, int i2, int i3, int i4);

    private static native void nativeDrawBitmapMesh(int i, int i2, int i3, int i4, float[] fArr, int i5, int[] iArr, int i6, int i7);

    private static native void nativeDrawVertices(int i, int i2, int i3, float[] fArr, int i4, float[] fArr2, int i5, int[] iArr, int i6, short[] sArr, int i7, int i8, int i9);

    private static native void native_drawText(int i, char[] cArr, int i2, int i3, float f, float f2, int i4, int i5);

    private static native void native_drawText(int i, String str, int i2, int i3, float f, float f2, int i4, int i5);

    private static native void native_drawTextRun(int i, String str, int i2, int i3, int i4, int i5, float f, float f2, int i6, int i7);

    private static native void native_drawTextRun(int i, char[] cArr, int i2, int i3, int i4, int i5, float f, float f2, int i6, int i7);

    private static native void native_drawPosText(int i, char[] cArr, int i2, int i3, float[] fArr, int i4);

    private static native void native_drawPosText(int i, String str, float[] fArr, int i2);

    private static native void native_drawTextOnPath(int i, char[] cArr, int i2, int i3, int i4, float f, float f2, int i5, int i6);

    private static native void native_drawTextOnPath(int i, String str, int i2, float f, float f2, int i3, int i4);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void finalizer(int i);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Canvas$CanvasFinalizer.class */
    public static final class CanvasFinalizer {
        private int mNativeCanvas;

        public CanvasFinalizer(int nativeCanvas) {
            this.mNativeCanvas = nativeCanvas;
        }

        protected void finalize() throws Throwable {
            try {
                dispose();
                super.finalize();
            } catch (Throwable th) {
                super.finalize();
                throw th;
            }
        }

        public void dispose() {
            if (this.mNativeCanvas != 0) {
                Canvas.finalizer(this.mNativeCanvas);
                this.mNativeCanvas = 0;
            }
        }
    }

    public Canvas() {
        this.mDensity = 0;
        this.mScreenDensity = 0;
        if (!isHardwareAccelerated()) {
            this.mNativeCanvas = initRaster(0);
            this.mFinalizer = new CanvasFinalizer(this.mNativeCanvas);
            return;
        }
        this.mFinalizer = null;
    }

    public Canvas(Bitmap bitmap) {
        this.mDensity = 0;
        this.mScreenDensity = 0;
        if (!bitmap.isMutable()) {
            throw new IllegalStateException("Immutable bitmap passed to Canvas constructor");
        }
        throwIfCannotDraw(bitmap);
        this.mNativeCanvas = initRaster(bitmap.ni());
        this.mFinalizer = new CanvasFinalizer(this.mNativeCanvas);
        this.mBitmap = bitmap;
        this.mDensity = bitmap.mDensity;
    }

    public Canvas(int nativeCanvas) {
        this.mDensity = 0;
        this.mScreenDensity = 0;
        if (nativeCanvas == 0) {
            throw new IllegalStateException();
        }
        this.mNativeCanvas = nativeCanvas;
        this.mFinalizer = new CanvasFinalizer(this.mNativeCanvas);
        this.mDensity = Bitmap.getDefaultDensity();
    }

    private void safeCanvasSwap(int nativeCanvas, boolean copyState) {
        int oldCanvas = this.mNativeCanvas;
        this.mNativeCanvas = nativeCanvas;
        this.mFinalizer.mNativeCanvas = nativeCanvas;
        if (copyState) {
            copyNativeCanvasState(oldCanvas, this.mNativeCanvas);
        }
        finalizer(oldCanvas);
    }

    public int getNativeCanvas() {
        return this.mNativeCanvas;
    }

    @Deprecated
    protected GL getGL() {
        return null;
    }

    public boolean isHardwareAccelerated() {
        return false;
    }

    public void setBitmap(Bitmap bitmap) {
        if (isHardwareAccelerated()) {
            throw new RuntimeException("Can't set a bitmap device on a GL canvas");
        }
        if (bitmap == null) {
            safeCanvasSwap(initRaster(0), false);
            this.mDensity = 0;
        } else if (!bitmap.isMutable()) {
            throw new IllegalStateException();
        } else {
            throwIfCannotDraw(bitmap);
            safeCanvasSwap(initRaster(bitmap.ni()), true);
            this.mDensity = bitmap.mDensity;
        }
        this.mBitmap = bitmap;
    }

    public void setViewport(int width, int height) {
    }

    public int getDensity() {
        return this.mDensity;
    }

    public void setDensity(int density) {
        if (this.mBitmap != null) {
            this.mBitmap.setDensity(density);
        }
        this.mDensity = density;
    }

    public void setScreenDensity(int density) {
        this.mScreenDensity = density;
    }

    public int getMaximumBitmapWidth() {
        return MAXMIMUM_BITMAP_SIZE;
    }

    public int getMaximumBitmapHeight() {
        return MAXMIMUM_BITMAP_SIZE;
    }

    public int saveLayer(RectF bounds, Paint paint, int saveFlags) {
        return native_saveLayer(this.mNativeCanvas, bounds, paint != null ? paint.mNativePaint : 0, saveFlags);
    }

    public int saveLayer(float left, float top, float right, float bottom, Paint paint, int saveFlags) {
        return native_saveLayer(this.mNativeCanvas, left, top, right, bottom, paint != null ? paint.mNativePaint : 0, saveFlags);
    }

    public int saveLayerAlpha(RectF bounds, int alpha, int saveFlags) {
        return native_saveLayerAlpha(this.mNativeCanvas, bounds, Math.min(255, Math.max(0, alpha)), saveFlags);
    }

    public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha, int saveFlags) {
        return native_saveLayerAlpha(this.mNativeCanvas, left, top, right, bottom, alpha, saveFlags);
    }

    public final void scale(float sx, float sy, float px, float py) {
        translate(px, py);
        scale(sx, sy);
        translate(-px, -py);
    }

    public final void rotate(float degrees, float px, float py) {
        translate(px, py);
        rotate(degrees);
        translate(-px, -py);
    }

    public void concat(Matrix matrix) {
        if (matrix != null) {
            native_concat(this.mNativeCanvas, matrix.native_instance);
        }
    }

    public void setMatrix(Matrix matrix) {
        native_setMatrix(this.mNativeCanvas, matrix == null ? 0 : matrix.native_instance);
    }

    @Deprecated
    public void getMatrix(Matrix ctm) {
        native_getCTM(this.mNativeCanvas, ctm.native_instance);
    }

    @Deprecated
    public final Matrix getMatrix() {
        Matrix m = new Matrix();
        getMatrix(m);
        return m;
    }

    public boolean clipRect(RectF rect, Region.Op op) {
        return native_clipRect(this.mNativeCanvas, rect.left, rect.top, rect.right, rect.bottom, op.nativeInt);
    }

    public boolean clipRect(Rect rect, Region.Op op) {
        return native_clipRect(this.mNativeCanvas, rect.left, rect.top, rect.right, rect.bottom, op.nativeInt);
    }

    public boolean clipRect(float left, float top, float right, float bottom, Region.Op op) {
        return native_clipRect(this.mNativeCanvas, left, top, right, bottom, op.nativeInt);
    }

    public boolean clipPath(Path path, Region.Op op) {
        return native_clipPath(this.mNativeCanvas, path.ni(), op.nativeInt);
    }

    public boolean clipPath(Path path) {
        return clipPath(path, Region.Op.INTERSECT);
    }

    public boolean clipRegion(Region region, Region.Op op) {
        return native_clipRegion(this.mNativeCanvas, region.ni(), op.nativeInt);
    }

    public boolean clipRegion(Region region) {
        return clipRegion(region, Region.Op.INTERSECT);
    }

    public DrawFilter getDrawFilter() {
        return this.mDrawFilter;
    }

    public void setDrawFilter(DrawFilter filter) {
        int nativeFilter = 0;
        if (filter != null) {
            nativeFilter = filter.mNativeInt;
        }
        this.mDrawFilter = filter;
        nativeSetDrawFilter(this.mNativeCanvas, nativeFilter);
    }

    /* loaded from: Canvas$EdgeType.class */
    public enum EdgeType {
        BW(0),
        AA(1);
        
        public final int nativeInt;

        EdgeType(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public boolean quickReject(RectF rect, EdgeType type) {
        return native_quickReject(this.mNativeCanvas, rect);
    }

    public boolean quickReject(Path path, EdgeType type) {
        return native_quickReject(this.mNativeCanvas, path.ni());
    }

    public boolean quickReject(float left, float top, float right, float bottom, EdgeType type) {
        return native_quickReject(this.mNativeCanvas, left, top, right, bottom);
    }

    public boolean getClipBounds(Rect bounds) {
        return native_getClipBounds(this.mNativeCanvas, bounds);
    }

    public final Rect getClipBounds() {
        Rect r = new Rect();
        getClipBounds(r);
        return r;
    }

    public void drawRGB(int r, int g, int b) {
        native_drawRGB(this.mNativeCanvas, r, g, b);
    }

    public void drawARGB(int a, int r, int g, int b) {
        native_drawARGB(this.mNativeCanvas, a, r, g, b);
    }

    public void drawColor(int color) {
        native_drawColor(this.mNativeCanvas, color);
    }

    public void drawColor(int color, PorterDuff.Mode mode) {
        native_drawColor(this.mNativeCanvas, color, mode.nativeInt);
    }

    public void drawPaint(Paint paint) {
        native_drawPaint(this.mNativeCanvas, paint.mNativePaint);
    }

    public void drawPoints(float[] pts, Paint paint) {
        drawPoints(pts, 0, pts.length, paint);
    }

    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        native_drawLine(this.mNativeCanvas, startX, startY, stopX, stopY, paint.mNativePaint);
    }

    public void drawLines(float[] pts, Paint paint) {
        drawLines(pts, 0, pts.length, paint);
    }

    public void drawRect(RectF rect, Paint paint) {
        native_drawRect(this.mNativeCanvas, rect, paint.mNativePaint);
    }

    public void drawRect(Rect r, Paint paint) {
        drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        native_drawRect(this.mNativeCanvas, left, top, right, bottom, paint.mNativePaint);
    }

    public void drawOval(RectF oval, Paint paint) {
        if (oval == null) {
            throw new NullPointerException();
        }
        native_drawOval(this.mNativeCanvas, oval, paint.mNativePaint);
    }

    public void drawCircle(float cx, float cy, float radius, Paint paint) {
        native_drawCircle(this.mNativeCanvas, cx, cy, radius, paint.mNativePaint);
    }

    public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
        if (oval == null) {
            throw new NullPointerException();
        }
        native_drawArc(this.mNativeCanvas, oval, startAngle, sweepAngle, useCenter, paint.mNativePaint);
    }

    public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) {
        if (rect == null) {
            throw new NullPointerException();
        }
        native_drawRoundRect(this.mNativeCanvas, rect, rx, ry, paint.mNativePaint);
    }

    public void drawPath(Path path, Paint paint) {
        native_drawPath(this.mNativeCanvas, path.ni(), paint.mNativePaint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void throwIfCannotDraw(Bitmap bitmap) {
        if (bitmap.isRecycled()) {
            throw new RuntimeException("Canvas: trying to use a recycled bitmap " + bitmap);
        }
        if (!bitmap.isPremultiplied() && bitmap.getConfig() == Bitmap.Config.ARGB_8888 && bitmap.hasAlpha()) {
            throw new RuntimeException("Canvas: trying to use a non-premultiplied bitmap " + bitmap);
        }
    }

    public void drawPatch(NinePatch patch, Rect dst, Paint paint) {
        patch.drawSoftware(this, dst, paint);
    }

    public void drawPatch(NinePatch patch, RectF dst, Paint paint) {
        patch.drawSoftware(this, dst, paint);
    }

    public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
        throwIfCannotDraw(bitmap);
        native_drawBitmap(this.mNativeCanvas, bitmap.ni(), left, top, paint != null ? paint.mNativePaint : 0, this.mDensity, this.mScreenDensity, bitmap.mDensity);
    }

    public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
        if (dst == null) {
            throw new NullPointerException();
        }
        throwIfCannotDraw(bitmap);
        native_drawBitmap(this.mNativeCanvas, bitmap.ni(), src, dst, paint != null ? paint.mNativePaint : 0, this.mScreenDensity, bitmap.mDensity);
    }

    public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
        if (dst == null) {
            throw new NullPointerException();
        }
        throwIfCannotDraw(bitmap);
        native_drawBitmap(this.mNativeCanvas, bitmap.ni(), src, dst, paint != null ? paint.mNativePaint : 0, this.mScreenDensity, bitmap.mDensity);
    }

    public void drawBitmap(int[] colors, int offset, int stride, float x, float y, int width, int height, boolean hasAlpha, Paint paint) {
        if (width < 0) {
            throw new IllegalArgumentException("width must be >= 0");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height must be >= 0");
        }
        if (Math.abs(stride) < width) {
            throw new IllegalArgumentException("abs(stride) must be >= width");
        }
        int lastScanline = offset + ((height - 1) * stride);
        int length = colors.length;
        if (offset < 0 || offset + width > length || lastScanline < 0 || lastScanline + width > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (width == 0 || height == 0) {
            return;
        }
        native_drawBitmap(this.mNativeCanvas, colors, offset, stride, x, y, width, height, hasAlpha, paint != null ? paint.mNativePaint : 0);
    }

    public void drawBitmap(int[] colors, int offset, int stride, int x, int y, int width, int height, boolean hasAlpha, Paint paint) {
        drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint);
    }

    public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
        nativeDrawBitmapMatrix(this.mNativeCanvas, bitmap.ni(), matrix.ni(), paint != null ? paint.mNativePaint : 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void checkRange(int length, int offset, int count) {
        if ((offset | count) < 0 || offset + count > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void drawBitmapMesh(Bitmap bitmap, int meshWidth, int meshHeight, float[] verts, int vertOffset, int[] colors, int colorOffset, Paint paint) {
        if ((meshWidth | meshHeight | vertOffset | colorOffset) < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (meshWidth == 0 || meshHeight == 0) {
            return;
        }
        int count = (meshWidth + 1) * (meshHeight + 1);
        checkRange(verts.length, vertOffset, count * 2);
        if (colors != null) {
            checkRange(colors.length, colorOffset, count);
        }
        nativeDrawBitmapMesh(this.mNativeCanvas, bitmap.ni(), meshWidth, meshHeight, verts, vertOffset, colors, colorOffset, paint != null ? paint.mNativePaint : 0);
    }

    /* loaded from: Canvas$VertexMode.class */
    public enum VertexMode {
        TRIANGLES(0),
        TRIANGLE_STRIP(1),
        TRIANGLE_FAN(2);
        
        public final int nativeInt;

        VertexMode(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public void drawVertices(VertexMode mode, int vertexCount, float[] verts, int vertOffset, float[] texs, int texOffset, int[] colors, int colorOffset, short[] indices, int indexOffset, int indexCount, Paint paint) {
        checkRange(verts.length, vertOffset, vertexCount);
        if (texs != null) {
            checkRange(texs.length, texOffset, vertexCount);
        }
        if (colors != null) {
            checkRange(colors.length, colorOffset, vertexCount / 2);
        }
        if (indices != null) {
            checkRange(indices.length, indexOffset, indexCount);
        }
        nativeDrawVertices(this.mNativeCanvas, mode.nativeInt, vertexCount, verts, vertOffset, texs, texOffset, colors, colorOffset, indices, indexOffset, indexCount, paint.mNativePaint);
    }

    public void drawText(char[] text, int index, int count, float x, float y, Paint paint) {
        if ((index | count | (index + count) | ((text.length - index) - count)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        native_drawText(this.mNativeCanvas, text, index, count, x, y, paint.mBidiFlags, paint.mNativePaint);
    }

    public void drawText(String text, float x, float y, Paint paint) {
        native_drawText(this.mNativeCanvas, text, 0, text.length(), x, y, paint.mBidiFlags, paint.mNativePaint);
    }

    public void drawText(String text, int start, int end, float x, float y, Paint paint) {
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        native_drawText(this.mNativeCanvas, text, start, end, x, y, paint.mBidiFlags, paint.mNativePaint);
    }

    public void drawText(CharSequence text, int start, int end, float x, float y, Paint paint) {
        if ((text instanceof String) || (text instanceof SpannedString) || (text instanceof SpannableString)) {
            native_drawText(this.mNativeCanvas, text.toString(), start, end, x, y, paint.mBidiFlags, paint.mNativePaint);
        } else if (text instanceof GraphicsOperations) {
            ((GraphicsOperations) text).drawText(this, start, end, x, y, paint);
        } else {
            char[] buf = TemporaryBuffer.obtain(end - start);
            TextUtils.getChars(text, start, end, buf, 0);
            native_drawText(this.mNativeCanvas, buf, 0, end - start, x, y, paint.mBidiFlags, paint.mNativePaint);
            TemporaryBuffer.recycle(buf);
        }
    }

    public void drawTextRun(char[] text, int index, int count, int contextIndex, int contextCount, float x, float y, int dir, Paint paint) {
        if (text == null) {
            throw new NullPointerException("text is null");
        }
        if (paint == null) {
            throw new NullPointerException("paint is null");
        }
        if ((index | count | ((text.length - index) - count)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (dir != 0 && dir != 1) {
            throw new IllegalArgumentException("unknown dir: " + dir);
        }
        native_drawTextRun(this.mNativeCanvas, text, index, count, contextIndex, contextCount, x, y, dir, paint.mNativePaint);
    }

    public void drawTextRun(CharSequence text, int start, int end, int contextStart, int contextEnd, float x, float y, int dir, Paint paint) {
        if (text == null) {
            throw new NullPointerException("text is null");
        }
        if (paint == null) {
            throw new NullPointerException("paint is null");
        }
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        int flags = dir == 0 ? 0 : 1;
        if ((text instanceof String) || (text instanceof SpannedString) || (text instanceof SpannableString)) {
            native_drawTextRun(this.mNativeCanvas, text.toString(), start, end, contextStart, contextEnd, x, y, flags, paint.mNativePaint);
        } else if (text instanceof GraphicsOperations) {
            ((GraphicsOperations) text).drawTextRun(this, start, end, contextStart, contextEnd, x, y, flags, paint);
        } else {
            int contextLen = contextEnd - contextStart;
            int len = end - start;
            char[] buf = TemporaryBuffer.obtain(contextLen);
            TextUtils.getChars(text, contextStart, contextEnd, buf, 0);
            native_drawTextRun(this.mNativeCanvas, buf, start - contextStart, len, 0, contextLen, x, y, flags, paint.mNativePaint);
            TemporaryBuffer.recycle(buf);
        }
    }

    @Deprecated
    public void drawPosText(char[] text, int index, int count, float[] pos, Paint paint) {
        if (index < 0 || index + count > text.length || count * 2 > pos.length) {
            throw new IndexOutOfBoundsException();
        }
        native_drawPosText(this.mNativeCanvas, text, index, count, pos, paint.mNativePaint);
    }

    @Deprecated
    public void drawPosText(String text, float[] pos, Paint paint) {
        if (text.length() * 2 > pos.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        native_drawPosText(this.mNativeCanvas, text, pos, paint.mNativePaint);
    }

    public void drawTextOnPath(char[] text, int index, int count, Path path, float hOffset, float vOffset, Paint paint) {
        if (index < 0 || index + count > text.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        native_drawTextOnPath(this.mNativeCanvas, text, index, count, path.ni(), hOffset, vOffset, paint.mBidiFlags, paint.mNativePaint);
    }

    public void drawTextOnPath(String text, Path path, float hOffset, float vOffset, Paint paint) {
        if (text.length() > 0) {
            native_drawTextOnPath(this.mNativeCanvas, text, path.ni(), hOffset, vOffset, paint.mBidiFlags, paint.mNativePaint);
        }
    }

    public void drawPicture(Picture picture) {
        picture.endRecording();
        int restoreCount = save();
        picture.draw(this);
        restoreToCount(restoreCount);
    }

    public void drawPicture(Picture picture, RectF dst) {
        save();
        translate(dst.left, dst.top);
        if (picture.getWidth() > 0 && picture.getHeight() > 0) {
            scale(dst.width() / picture.getWidth(), dst.height() / picture.getHeight());
        }
        drawPicture(picture);
        restore();
    }

    public void drawPicture(Picture picture, Rect dst) {
        save();
        translate(dst.left, dst.top);
        if (picture.getWidth() > 0 && picture.getHeight() > 0) {
            scale(dst.width() / picture.getWidth(), dst.height() / picture.getHeight());
        }
        drawPicture(picture);
        restore();
    }

    public void release() {
        this.mFinalizer.dispose();
    }
}