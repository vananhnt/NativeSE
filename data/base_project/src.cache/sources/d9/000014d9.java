package android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.graphics.TemporaryBuffer;
import android.text.GraphicsOperations;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: GLES20Canvas.class */
public class GLES20Canvas extends HardwareCanvas {
    private static final int MODIFIER_NONE = 0;
    private static final int MODIFIER_SHADOW = 1;
    private static final int MODIFIER_SHADER = 2;
    private static final int MODIFIER_COLOR_FILTER = 4;
    private final boolean mOpaque;
    private int mRenderer;
    private CanvasFinalizer mFinalizer;
    private int mWidth;
    private int mHeight;
    private float[] mPoint;
    private float[] mLine;
    private Rect mClipBounds;
    private RectF mPathBounds;
    private DrawFilter mFilter;
    private static boolean sIsAvailable = nIsAvailable();
    static final int FLUSH_CACHES_LAYERS = 0;
    static final int FLUSH_CACHES_MODERATE = 1;
    static final int FLUSH_CACHES_FULL = 2;

    private static native boolean nIsAvailable();

    private static native int nCreateRenderer();

    private static native int nCreateLayerRenderer(int i);

    private static native int nCreateDisplayListRenderer();

    private static native void nResetDisplayListRenderer(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nDestroyRenderer(int i);

    private static native void nSetName(int i, String str);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native int nCreateTextureLayer(boolean z, int[] iArr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native int nCreateLayer(int i, int i2, boolean z, int[] iArr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native boolean nResizeLayer(int i, int i2, int i3, int[] iArr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void nSetOpaqueLayer(int i, boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void nSetLayerPaint(int i, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void nSetLayerColorFilter(int i, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void nUpdateTextureLayer(int i, int i2, int i3, boolean z, SurfaceTexture surfaceTexture);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void nClearLayerTexture(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void nSetTextureLayerTransform(int i, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void nDestroyLayer(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void nDestroyLayerDeferred(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void nUpdateRenderLayer(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native boolean nCopyLayer(int i, int i2);

    private static native void nClearLayerUpdates(int i);

    private static native void nFlushLayerUpdates(int i);

    private static native void nPushLayerUpdate(int i, int i2);

    private static native void nCancelLayerUpdate(int i, int i2);

    private static native int nGetMaximumTextureWidth();

    private static native int nGetMaximumTextureHeight();

    private static native void nSetViewport(int i, int i2, int i3);

    private static native int nPrepare(int i, boolean z);

    private static native int nPrepareDirty(int i, int i2, int i3, int i4, int i5, boolean z);

    private static native void nFinish(int i);

    private static native int nGetStencilSize();

    static native void nSetCountOverdrawEnabled(int i, boolean z);

    static native float nGetOverdraw(int i);

    private static native int nCallDrawGLFunction(int i, int i2);

    private static native int nInvokeFunctors(int i, Rect rect);

    private static native void nDetachFunctor(int i, int i2);

    private static native void nAttachFunctor(int i, int i2);

    private static native void nFlushCaches(int i);

    private static native void nTerminateCaches();

    private static native boolean nInitCaches();

    private static native void nInitAtlas(GraphicBuffer graphicBuffer, int[] iArr, int i);

    private static native int nGetDisplayList(int i, int i2);

    private static native void nOutputDisplayList(int i, int i2);

    private static native int nDrawDisplayList(int i, int i2, Rect rect, int i3);

    private static native void nDrawLayer(int i, int i2, float f, float f2);

    private static native void nInterrupt(int i);

    private static native void nResume(int i);

    private static native boolean nClipPath(int i, int i2, int i3);

    private static native boolean nClipRect(int i, float f, float f2, float f3, float f4, int i2);

    private static native boolean nClipRect(int i, int i2, int i3, int i4, int i5, int i6);

    private static native boolean nClipRegion(int i, int i2, int i3);

    private static native boolean nGetClipBounds(int i, Rect rect);

    private static native boolean nQuickReject(int i, float f, float f2, float f3, float f4);

    private static native void nTranslate(int i, float f, float f2);

    private static native void nSkew(int i, float f, float f2);

    private static native void nRotate(int i, float f);

    private static native void nScale(int i, float f, float f2);

    private static native void nSetMatrix(int i, int i2);

    private static native void nGetMatrix(int i, int i2);

    private static native void nConcatMatrix(int i, int i2);

    private static native int nSave(int i, int i2);

    private static native int nSaveLayer(int i, int i2, int i3);

    private static native int nSaveLayer(int i, float f, float f2, float f3, float f4, int i2, int i3);

    private static native int nSaveLayerAlpha(int i, int i2, int i3);

    private static native int nSaveLayerAlpha(int i, float f, float f2, float f3, float f4, int i2, int i3);

    private static native void nRestore(int i);

    private static native void nRestoreToCount(int i, int i2);

    private static native int nGetSaveCount(int i);

    private static native void nResetPaintFilter(int i);

    private static native void nSetupPaintFilter(int i, int i2, int i3);

    private static native void nDrawArc(int i, float f, float f2, float f3, float f4, float f5, float f6, boolean z, int i2);

    private static native void nDrawPatch(int i, int i2, byte[] bArr, int i3, float f, float f2, float f3, float f4, int i4);

    private static native void nDrawBitmap(int i, int i2, byte[] bArr, float f, float f2, int i3);

    private static native void nDrawBitmap(int i, int i2, byte[] bArr, int i3, int i4);

    private static native void nDrawBitmap(int i, int i2, byte[] bArr, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int i3);

    private static native void nDrawBitmap(int i, int[] iArr, int i2, int i3, float f, float f2, int i4, int i5, boolean z, int i6);

    private static native void nDrawBitmapMesh(int i, int i2, byte[] bArr, int i3, int i4, float[] fArr, int i5, int[] iArr, int i6, int i7);

    private static native void nDrawCircle(int i, float f, float f2, float f3, int i2);

    private static native void nDrawColor(int i, int i2, int i3);

    private static native void nDrawLines(int i, float[] fArr, int i2, int i3, int i4);

    private static native void nDrawOval(int i, float f, float f2, float f3, float f4, int i2);

    private static native void nDrawPath(int i, int i2, int i3);

    private static native void nDrawRects(int i, int i2, int i3);

    private static native void nDrawRects(int i, float[] fArr, int i2, int i3);

    private static native void nDrawPoints(int i, float[] fArr, int i2, int i3, int i4);

    private static native void nDrawPosText(int i, char[] cArr, int i2, int i3, float[] fArr, int i4);

    private static native void nDrawPosText(int i, String str, int i2, int i3, float[] fArr, int i4);

    private static native void nDrawRect(int i, float f, float f2, float f3, float f4, int i2);

    private static native void nDrawRoundRect(int i, float f, float f2, float f3, float f4, float f5, float f6, int i2);

    private static native void nDrawText(int i, char[] cArr, int i2, int i3, float f, float f2, int i4, int i5);

    private static native void nDrawText(int i, String str, int i2, int i3, float f, float f2, int i4, int i5);

    private static native void nDrawTextOnPath(int i, char[] cArr, int i2, int i3, int i4, float f, float f2, int i5, int i6);

    private static native void nDrawTextOnPath(int i, String str, int i2, int i3, int i4, float f, float f2, int i5, int i6);

    private static native void nDrawTextRun(int i, char[] cArr, int i2, int i3, int i4, int i5, float f, float f2, int i6, int i7);

    private static native void nDrawTextRun(int i, String str, int i2, int i3, int i4, int i5, float f, float f2, int i6, int i7);

    private static native void nSetupShader(int i, int i2);

    private static native void nSetupColorFilter(int i, int i2);

    private static native void nSetupShadow(int i, float f, float f2, float f3, int i2);

    private static native void nResetModifiers(int i, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isAvailable() {
        return sIsAvailable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GLES20Canvas(boolean translucent) {
        this(false, translucent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GLES20Canvas(int layer, boolean translucent) {
        this.mOpaque = !translucent;
        this.mRenderer = nCreateLayerRenderer(layer);
        setupFinalizer();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public GLES20Canvas(boolean record, boolean translucent) {
        this.mOpaque = !translucent;
        if (record) {
            this.mRenderer = nCreateDisplayListRenderer();
        } else {
            this.mRenderer = nCreateRenderer();
        }
        setupFinalizer();
    }

    private void setupFinalizer() {
        if (this.mRenderer == 0) {
            throw new IllegalStateException("Could not create GLES20Canvas renderer");
        }
        this.mFinalizer = new CanvasFinalizer(this.mRenderer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetDisplayListRenderer() {
        nResetDisplayListRenderer(this.mRenderer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: GLES20Canvas$CanvasFinalizer.class */
    public static final class CanvasFinalizer {
        private final int mRenderer;

        public CanvasFinalizer(int renderer) {
            this.mRenderer = renderer;
        }

        protected void finalize() throws Throwable {
            try {
                GLES20Canvas.nDestroyRenderer(this.mRenderer);
                super.finalize();
            } catch (Throwable th) {
                super.finalize();
                throw th;
            }
        }
    }

    @Override // android.view.HardwareCanvas
    public void setName(String name) {
        super.setName(name);
        nSetName(this.mRenderer, name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareCanvas
    public void pushLayerUpdate(HardwareLayer layer) {
        nPushLayerUpdate(this.mRenderer, ((GLES20RenderLayer) layer).mLayer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareCanvas
    public void cancelLayerUpdate(HardwareLayer layer) {
        nCancelLayerUpdate(this.mRenderer, ((GLES20RenderLayer) layer).mLayer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareCanvas
    public void flushLayerUpdates() {
        nFlushLayerUpdates(this.mRenderer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareCanvas
    public void clearLayerUpdates() {
        nClearLayerUpdates(this.mRenderer);
    }

    @Override // android.graphics.Canvas
    public boolean isOpaque() {
        return this.mOpaque;
    }

    @Override // android.graphics.Canvas
    public int getWidth() {
        return this.mWidth;
    }

    @Override // android.graphics.Canvas
    public int getHeight() {
        return this.mHeight;
    }

    @Override // android.graphics.Canvas
    public int getMaximumBitmapWidth() {
        return nGetMaximumTextureWidth();
    }

    @Override // android.graphics.Canvas
    public int getMaximumBitmapHeight() {
        return nGetMaximumTextureHeight();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRenderer() {
        return this.mRenderer;
    }

    @Override // android.graphics.Canvas
    public void setViewport(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        nSetViewport(this.mRenderer, width, height);
    }

    @Override // android.view.HardwareCanvas
    public int onPreDraw(Rect dirty) {
        if (dirty != null) {
            return nPrepareDirty(this.mRenderer, dirty.left, dirty.top, dirty.right, dirty.bottom, this.mOpaque);
        }
        return nPrepare(this.mRenderer, this.mOpaque);
    }

    @Override // android.view.HardwareCanvas
    public void onPostDraw() {
        nFinish(this.mRenderer);
    }

    public static int getStencilSize() {
        return nGetStencilSize();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCountOverdrawEnabled(boolean enabled) {
        nSetCountOverdrawEnabled(this.mRenderer, enabled);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getOverdraw() {
        return nGetOverdraw(this.mRenderer);
    }

    @Override // android.view.HardwareCanvas
    public int callDrawGLFunction(int drawGLFunction) {
        return nCallDrawGLFunction(this.mRenderer, drawGLFunction);
    }

    @Override // android.view.HardwareCanvas
    public int invokeFunctors(Rect dirty) {
        return nInvokeFunctors(this.mRenderer, dirty);
    }

    @Override // android.view.HardwareCanvas
    public void detachFunctor(int functor) {
        nDetachFunctor(this.mRenderer, functor);
    }

    @Override // android.view.HardwareCanvas
    public void attachFunctor(int functor) {
        nAttachFunctor(this.mRenderer, functor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void flushCaches(int level) {
        nFlushCaches(level);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void terminateCaches() {
        nTerminateCaches();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean initCaches() {
        return nInitCaches();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void initAtlas(GraphicBuffer buffer, int[] map) {
        nInitAtlas(buffer, map, map.length);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDisplayList(int displayList) {
        return nGetDisplayList(this.mRenderer, displayList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareCanvas
    public void outputDisplayList(DisplayList displayList) {
        nOutputDisplayList(this.mRenderer, ((GLES20DisplayList) displayList).getNativeDisplayList());
    }

    @Override // android.view.HardwareCanvas
    public int drawDisplayList(DisplayList displayList, Rect dirty, int flags) {
        return nDrawDisplayList(this.mRenderer, ((GLES20DisplayList) displayList).getNativeDisplayList(), dirty, flags);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareCanvas
    public void drawHardwareLayer(HardwareLayer layer, float x, float y, Paint paint) {
        layer.setLayerPaint(paint);
        GLES20Layer glLayer = (GLES20Layer) layer;
        nDrawLayer(this.mRenderer, glLayer.getLayer(), x, y);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void interrupt() {
        nInterrupt(this.mRenderer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resume() {
        nResume(this.mRenderer);
    }

    private Rect getInternalClipBounds() {
        if (this.mClipBounds == null) {
            this.mClipBounds = new Rect();
        }
        return this.mClipBounds;
    }

    private RectF getPathBounds() {
        if (this.mPathBounds == null) {
            this.mPathBounds = new RectF();
        }
        return this.mPathBounds;
    }

    private float[] getPointStorage() {
        if (this.mPoint == null) {
            this.mPoint = new float[2];
        }
        return this.mPoint;
    }

    private float[] getLineStorage() {
        if (this.mLine == null) {
            this.mLine = new float[4];
        }
        return this.mLine;
    }

    @Override // android.graphics.Canvas
    public boolean clipPath(Path path) {
        return nClipPath(this.mRenderer, path.mNativePath, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipPath(Path path, Region.Op op) {
        return nClipPath(this.mRenderer, path.mNativePath, op.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(float left, float top, float right, float bottom) {
        return nClipRect(this.mRenderer, left, top, right, bottom, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(float left, float top, float right, float bottom, Region.Op op) {
        return nClipRect(this.mRenderer, left, top, right, bottom, op.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(int left, int top, int right, int bottom) {
        return nClipRect(this.mRenderer, left, top, right, bottom, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(Rect rect) {
        return nClipRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(Rect rect, Region.Op op) {
        return nClipRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, op.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(RectF rect) {
        return nClipRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(RectF rect, Region.Op op) {
        return nClipRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, op.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRegion(Region region) {
        return nClipRegion(this.mRenderer, region.mNativeRegion, Region.Op.INTERSECT.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean clipRegion(Region region, Region.Op op) {
        return nClipRegion(this.mRenderer, region.mNativeRegion, op.nativeInt);
    }

    @Override // android.graphics.Canvas
    public boolean getClipBounds(Rect bounds) {
        return nGetClipBounds(this.mRenderer, bounds);
    }

    @Override // android.graphics.Canvas
    public boolean quickReject(float left, float top, float right, float bottom, Canvas.EdgeType type) {
        return nQuickReject(this.mRenderer, left, top, right, bottom);
    }

    @Override // android.graphics.Canvas
    public boolean quickReject(Path path, Canvas.EdgeType type) {
        RectF pathBounds = getPathBounds();
        path.computeBounds(pathBounds, true);
        return nQuickReject(this.mRenderer, pathBounds.left, pathBounds.top, pathBounds.right, pathBounds.bottom);
    }

    @Override // android.graphics.Canvas
    public boolean quickReject(RectF rect, Canvas.EdgeType type) {
        return nQuickReject(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom);
    }

    @Override // android.graphics.Canvas
    public void translate(float dx, float dy) {
        if (dx != 0.0f || dy != 0.0f) {
            nTranslate(this.mRenderer, dx, dy);
        }
    }

    @Override // android.graphics.Canvas
    public void skew(float sx, float sy) {
        nSkew(this.mRenderer, sx, sy);
    }

    @Override // android.graphics.Canvas
    public void rotate(float degrees) {
        nRotate(this.mRenderer, degrees);
    }

    @Override // android.graphics.Canvas
    public void scale(float sx, float sy) {
        nScale(this.mRenderer, sx, sy);
    }

    @Override // android.graphics.Canvas
    public void setMatrix(Matrix matrix) {
        nSetMatrix(this.mRenderer, matrix == null ? 0 : matrix.native_instance);
    }

    @Override // android.graphics.Canvas
    public void getMatrix(Matrix matrix) {
        nGetMatrix(this.mRenderer, matrix.native_instance);
    }

    @Override // android.graphics.Canvas
    public void concat(Matrix matrix) {
        if (matrix != null) {
            nConcatMatrix(this.mRenderer, matrix.native_instance);
        }
    }

    @Override // android.graphics.Canvas
    public int save() {
        return nSave(this.mRenderer, 3);
    }

    @Override // android.graphics.Canvas
    public int save(int saveFlags) {
        return nSave(this.mRenderer, saveFlags);
    }

    @Override // android.graphics.Canvas
    public int saveLayer(RectF bounds, Paint paint, int saveFlags) {
        int i;
        if (bounds != null) {
            return saveLayer(bounds.left, bounds.top, bounds.right, bounds.bottom, paint, saveFlags);
        }
        int modifier = paint != null ? setupColorFilter(paint) : 0;
        if (paint == null) {
            i = 0;
        } else {
            try {
                i = paint.mNativePaint;
            } catch (Throwable th) {
                if (modifier != 0) {
                    nResetModifiers(this.mRenderer, modifier);
                }
                throw th;
            }
        }
        int nativePaint = i;
        int count = nSaveLayer(this.mRenderer, nativePaint, saveFlags);
        if (modifier != 0) {
            nResetModifiers(this.mRenderer, modifier);
        }
        return count;
    }

    @Override // android.graphics.Canvas
    public int saveLayer(float left, float top, float right, float bottom, Paint paint, int saveFlags) {
        int i;
        if (left < right && top < bottom) {
            int modifier = paint != null ? setupColorFilter(paint) : 0;
            if (paint == null) {
                i = 0;
            } else {
                try {
                    i = paint.mNativePaint;
                } catch (Throwable th) {
                    if (modifier != 0) {
                        nResetModifiers(this.mRenderer, modifier);
                    }
                    throw th;
                }
            }
            int nativePaint = i;
            int count = nSaveLayer(this.mRenderer, left, top, right, bottom, nativePaint, saveFlags);
            if (modifier != 0) {
                nResetModifiers(this.mRenderer, modifier);
            }
            return count;
        }
        return save(saveFlags);
    }

    @Override // android.graphics.Canvas
    public int saveLayerAlpha(RectF bounds, int alpha, int saveFlags) {
        if (bounds != null) {
            return saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, alpha, saveFlags);
        }
        return nSaveLayerAlpha(this.mRenderer, alpha, saveFlags);
    }

    @Override // android.graphics.Canvas
    public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha, int saveFlags) {
        if (left < right && top < bottom) {
            return nSaveLayerAlpha(this.mRenderer, left, top, right, bottom, alpha, saveFlags);
        }
        return save(saveFlags);
    }

    @Override // android.graphics.Canvas
    public void restore() {
        nRestore(this.mRenderer);
    }

    @Override // android.graphics.Canvas
    public void restoreToCount(int saveCount) {
        nRestoreToCount(this.mRenderer, saveCount);
    }

    @Override // android.graphics.Canvas
    public int getSaveCount() {
        return nGetSaveCount(this.mRenderer);
    }

    @Override // android.graphics.Canvas
    public void setDrawFilter(DrawFilter filter) {
        this.mFilter = filter;
        if (filter == null) {
            nResetPaintFilter(this.mRenderer);
        } else if (filter instanceof PaintFlagsDrawFilter) {
            PaintFlagsDrawFilter flagsFilter = (PaintFlagsDrawFilter) filter;
            nSetupPaintFilter(this.mRenderer, flagsFilter.clearBits, flagsFilter.setBits);
        }
    }

    @Override // android.graphics.Canvas
    public DrawFilter getDrawFilter() {
        return this.mFilter;
    }

    @Override // android.graphics.Canvas
    public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
        int modifiers = setupModifiers(paint, 6);
        try {
            nDrawArc(this.mRenderer, oval.left, oval.top, oval.right, oval.bottom, startAngle, sweepAngle, useCenter, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawARGB(int a, int r, int g, int b) {
        drawColor(((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255));
    }

    @Override // android.graphics.Canvas
    public void drawPatch(NinePatch patch, Rect dst, Paint paint) {
        int i;
        Bitmap bitmap = patch.getBitmap();
        throwIfCannotDraw(bitmap);
        int modifier = paint != null ? setupColorFilter(paint) : 0;
        if (paint == null) {
            i = 0;
        } else {
            try {
                i = paint.mNativePaint;
            } catch (Throwable th) {
                if (modifier != 0) {
                    nResetModifiers(this.mRenderer, modifier);
                }
                throw th;
            }
        }
        int nativePaint = i;
        nDrawPatch(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, patch.mNativeChunk, dst.left, dst.top, dst.right, dst.bottom, nativePaint);
        if (modifier != 0) {
            nResetModifiers(this.mRenderer, modifier);
        }
    }

    @Override // android.graphics.Canvas
    public void drawPatch(NinePatch patch, RectF dst, Paint paint) {
        int i;
        Bitmap bitmap = patch.getBitmap();
        throwIfCannotDraw(bitmap);
        int modifier = paint != null ? setupColorFilter(paint) : 0;
        if (paint == null) {
            i = 0;
        } else {
            try {
                i = paint.mNativePaint;
            } catch (Throwable th) {
                if (modifier != 0) {
                    nResetModifiers(this.mRenderer, modifier);
                }
                throw th;
            }
        }
        int nativePaint = i;
        nDrawPatch(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, patch.mNativeChunk, dst.left, dst.top, dst.right, dst.bottom, nativePaint);
        if (modifier != 0) {
            nResetModifiers(this.mRenderer, modifier);
        }
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
        int i;
        throwIfCannotDraw(bitmap);
        int modifiers = paint != null ? setupModifiers(bitmap, paint) : 0;
        if (paint == null) {
            i = 0;
        } else {
            try {
                i = paint.mNativePaint;
            } catch (Throwable th) {
                if (modifiers != 0) {
                    nResetModifiers(this.mRenderer, modifiers);
                }
                throw th;
            }
        }
        int nativePaint = i;
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, left, top, nativePaint);
        if (modifiers != 0) {
            nResetModifiers(this.mRenderer, modifiers);
        }
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
        int i;
        throwIfCannotDraw(bitmap);
        int modifiers = paint != null ? setupModifiers(bitmap, paint) : 0;
        if (paint == null) {
            i = 0;
        } else {
            try {
                i = paint.mNativePaint;
            } catch (Throwable th) {
                if (modifiers != 0) {
                    nResetModifiers(this.mRenderer, modifiers);
                }
                throw th;
            }
        }
        int nativePaint = i;
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, matrix.native_instance, nativePaint);
        if (modifiers != 0) {
            nResetModifiers(this.mRenderer, modifiers);
        }
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
        int i;
        int left;
        int right;
        int top;
        int bottom;
        throwIfCannotDraw(bitmap);
        int modifiers = paint != null ? setupModifiers(bitmap, paint) : 0;
        if (paint == null) {
            i = 0;
        } else {
            try {
                i = paint.mNativePaint;
            } catch (Throwable th) {
                if (modifiers != 0) {
                    nResetModifiers(this.mRenderer, modifiers);
                }
                throw th;
            }
        }
        int nativePaint = i;
        if (src == null) {
            top = 0;
            left = 0;
            right = bitmap.getWidth();
            bottom = bitmap.getHeight();
        } else {
            left = src.left;
            right = src.right;
            top = src.top;
            bottom = src.bottom;
        }
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, left, top, right, bottom, dst.left, dst.top, dst.right, dst.bottom, nativePaint);
        if (modifiers != 0) {
            nResetModifiers(this.mRenderer, modifiers);
        }
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
        int i;
        float left;
        float right;
        float top;
        float bottom;
        throwIfCannotDraw(bitmap);
        int modifiers = paint != null ? setupModifiers(bitmap, paint) : 0;
        if (paint == null) {
            i = 0;
        } else {
            try {
                i = paint.mNativePaint;
            } catch (Throwable th) {
                if (modifiers != 0) {
                    nResetModifiers(this.mRenderer, modifiers);
                }
                throw th;
            }
        }
        int nativePaint = i;
        if (src == null) {
            top = 0.0f;
            left = 0.0f;
            right = bitmap.getWidth();
            bottom = bitmap.getHeight();
        } else {
            left = src.left;
            right = src.right;
            top = src.top;
            bottom = src.bottom;
        }
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, left, top, right, bottom, dst.left, dst.top, dst.right, dst.bottom, nativePaint);
        if (modifiers != 0) {
            nResetModifiers(this.mRenderer, modifiers);
        }
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(int[] colors, int offset, int stride, float x, float y, int width, int height, boolean hasAlpha, Paint paint) {
        int i;
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
        int modifier = paint != null ? setupColorFilter(paint) : 0;
        if (paint == null) {
            i = 0;
        } else {
            try {
                i = paint.mNativePaint;
            } catch (Throwable th) {
                if (modifier != 0) {
                    nResetModifiers(this.mRenderer, modifier);
                }
                throw th;
            }
        }
        int nativePaint = i;
        nDrawBitmap(this.mRenderer, colors, offset, stride, x, y, width, height, hasAlpha, nativePaint);
        if (modifier != 0) {
            nResetModifiers(this.mRenderer, modifier);
        }
    }

    @Override // android.graphics.Canvas
    public void drawBitmap(int[] colors, int offset, int stride, int x, int y, int width, int height, boolean hasAlpha, Paint paint) {
        drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint);
    }

    @Override // android.graphics.Canvas
    public void drawBitmapMesh(Bitmap bitmap, int meshWidth, int meshHeight, float[] verts, int vertOffset, int[] colors, int colorOffset, Paint paint) {
        int i;
        throwIfCannotDraw(bitmap);
        if (meshWidth < 0 || meshHeight < 0 || vertOffset < 0 || colorOffset < 0) {
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
        int modifiers = paint != null ? setupModifiers(bitmap, paint) : 0;
        if (paint == null) {
            i = 0;
        } else {
            try {
                i = paint.mNativePaint;
            } catch (Throwable th) {
                if (modifiers != 0) {
                    nResetModifiers(this.mRenderer, modifiers);
                }
                throw th;
            }
        }
        int nativePaint = i;
        nDrawBitmapMesh(this.mRenderer, bitmap.mNativeBitmap, bitmap.mBuffer, meshWidth, meshHeight, verts, vertOffset, colors, colorOffset, nativePaint);
        if (modifiers != 0) {
            nResetModifiers(this.mRenderer, modifiers);
        }
    }

    @Override // android.graphics.Canvas
    public void drawCircle(float cx, float cy, float radius, Paint paint) {
        int modifiers = setupModifiers(paint, 6);
        try {
            nDrawCircle(this.mRenderer, cx, cy, radius, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawColor(int color) {
        drawColor(color, PorterDuff.Mode.SRC_OVER);
    }

    @Override // android.graphics.Canvas
    public void drawColor(int color, PorterDuff.Mode mode) {
        nDrawColor(this.mRenderer, color, mode.nativeInt);
    }

    @Override // android.graphics.Canvas
    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        float[] line = getLineStorage();
        line[0] = startX;
        line[1] = startY;
        line[2] = stopX;
        line[3] = stopY;
        drawLines(line, 0, 4, paint);
    }

    @Override // android.graphics.Canvas
    public void drawLines(float[] pts, int offset, int count, Paint paint) {
        if (count < 4) {
            return;
        }
        if ((offset | count) < 0 || offset + count > pts.length) {
            throw new IllegalArgumentException("The lines array must contain 4 elements per line.");
        }
        int modifiers = setupModifiers(paint, 6);
        try {
            nDrawLines(this.mRenderer, pts, offset, count, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawLines(float[] pts, Paint paint) {
        drawLines(pts, 0, pts.length, paint);
    }

    @Override // android.graphics.Canvas
    public void drawOval(RectF oval, Paint paint) {
        int modifiers = setupModifiers(paint, 6);
        try {
            nDrawOval(this.mRenderer, oval.left, oval.top, oval.right, oval.bottom, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawPaint(Paint paint) {
        Rect r = getInternalClipBounds();
        nGetClipBounds(this.mRenderer, r);
        drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    @Override // android.graphics.Canvas
    public void drawPath(Path path, Paint paint) {
        int modifiers = setupModifiers(paint, 6);
        try {
            if (path.isSimplePath) {
                if (path.rects != null) {
                    nDrawRects(this.mRenderer, path.rects.mNativeRegion, paint.mNativePaint);
                }
            } else {
                nDrawPath(this.mRenderer, path.mNativePath, paint.mNativePaint);
            }
        } finally {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void drawRects(float[] rects, int count, Paint paint) {
        int modifiers = setupModifiers(paint, 6);
        try {
            nDrawRects(this.mRenderer, rects, count, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawPicture(Picture picture) {
        if (picture.createdFromStream) {
            return;
        }
        picture.endRecording();
    }

    @Override // android.graphics.Canvas
    public void drawPicture(Picture picture, Rect dst) {
        if (picture.createdFromStream) {
            return;
        }
        save();
        translate(dst.left, dst.top);
        if (picture.getWidth() > 0 && picture.getHeight() > 0) {
            scale(dst.width() / picture.getWidth(), dst.height() / picture.getHeight());
        }
        drawPicture(picture);
        restore();
    }

    @Override // android.graphics.Canvas
    public void drawPicture(Picture picture, RectF dst) {
        if (picture.createdFromStream) {
            return;
        }
        save();
        translate(dst.left, dst.top);
        if (picture.getWidth() > 0 && picture.getHeight() > 0) {
            scale(dst.width() / picture.getWidth(), dst.height() / picture.getHeight());
        }
        drawPicture(picture);
        restore();
    }

    @Override // android.graphics.Canvas
    public void drawPoint(float x, float y, Paint paint) {
        float[] point = getPointStorage();
        point[0] = x;
        point[1] = y;
        drawPoints(point, 0, 2, paint);
    }

    @Override // android.graphics.Canvas
    public void drawPoints(float[] pts, Paint paint) {
        drawPoints(pts, 0, pts.length, paint);
    }

    @Override // android.graphics.Canvas
    public void drawPoints(float[] pts, int offset, int count, Paint paint) {
        if (count < 2) {
            return;
        }
        int modifiers = setupModifiers(paint, 6);
        try {
            nDrawPoints(this.mRenderer, pts, offset, count, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawPosText(char[] text, int index, int count, float[] pos, Paint paint) {
        if (index < 0 || index + count > text.length || count * 2 > pos.length) {
            throw new IndexOutOfBoundsException();
        }
        int modifiers = setupModifiers(paint);
        try {
            nDrawPosText(this.mRenderer, text, index, count, pos, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawPosText(String text, float[] pos, Paint paint) {
        if (text.length() * 2 > pos.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int modifiers = setupModifiers(paint);
        try {
            nDrawPosText(this.mRenderer, text, 0, text.length(), pos, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        if (left == right || top == bottom) {
            return;
        }
        int modifiers = setupModifiers(paint, 6);
        try {
            nDrawRect(this.mRenderer, left, top, right, bottom, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawRect(Rect r, Paint paint) {
        drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    @Override // android.graphics.Canvas
    public void drawRect(RectF r, Paint paint) {
        drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    @Override // android.graphics.Canvas
    public void drawRGB(int r, int g, int b) {
        drawColor((-16777216) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255));
    }

    @Override // android.graphics.Canvas
    public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) {
        int modifiers = setupModifiers(paint, 6);
        try {
            nDrawRoundRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, rx, ry, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawText(char[] text, int index, int count, float x, float y, Paint paint) {
        if ((index | count | (index + count) | ((text.length - index) - count)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        int modifiers = setupModifiers(paint);
        try {
            nDrawText(this.mRenderer, text, index, count, x, y, paint.mBidiFlags, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawText(CharSequence text, int start, int end, float x, float y, Paint paint) {
        int modifiers = setupModifiers(paint);
        try {
            if ((text instanceof String) || (text instanceof SpannedString) || (text instanceof SpannableString)) {
                nDrawText(this.mRenderer, text.toString(), start, end, x, y, paint.mBidiFlags, paint.mNativePaint);
            } else if (text instanceof GraphicsOperations) {
                ((GraphicsOperations) text).drawText(this, start, end, x, y, paint);
            } else {
                char[] buf = TemporaryBuffer.obtain(end - start);
                TextUtils.getChars(text, start, end, buf, 0);
                nDrawText(this.mRenderer, buf, 0, end - start, x, y, paint.mBidiFlags, paint.mNativePaint);
                TemporaryBuffer.recycle(buf);
            }
        } finally {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawText(String text, int start, int end, float x, float y, Paint paint) {
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        int modifiers = setupModifiers(paint);
        try {
            nDrawText(this.mRenderer, text, start, end, x, y, paint.mBidiFlags, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawText(String text, float x, float y, Paint paint) {
        int modifiers = setupModifiers(paint);
        try {
            nDrawText(this.mRenderer, text, 0, text.length(), x, y, paint.mBidiFlags, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawTextOnPath(char[] text, int index, int count, Path path, float hOffset, float vOffset, Paint paint) {
        if (index < 0 || index + count > text.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int modifiers = setupModifiers(paint);
        try {
            nDrawTextOnPath(this.mRenderer, text, index, count, path.mNativePath, hOffset, vOffset, paint.mBidiFlags, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawTextOnPath(String text, Path path, float hOffset, float vOffset, Paint paint) {
        if (text.length() == 0) {
            return;
        }
        int modifiers = setupModifiers(paint);
        try {
            nDrawTextOnPath(this.mRenderer, text, 0, text.length(), path.mNativePath, hOffset, vOffset, paint.mBidiFlags, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawTextRun(char[] text, int index, int count, int contextIndex, int contextCount, float x, float y, int dir, Paint paint) {
        if ((index | count | ((text.length - index) - count)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (dir != 0 && dir != 1) {
            throw new IllegalArgumentException("Unknown direction: " + dir);
        }
        int modifiers = setupModifiers(paint);
        try {
            nDrawTextRun(this.mRenderer, text, index, count, contextIndex, contextCount, x, y, dir, paint.mNativePaint);
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        } catch (Throwable th) {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
            throw th;
        }
    }

    @Override // android.graphics.Canvas
    public void drawTextRun(CharSequence text, int start, int end, int contextStart, int contextEnd, float x, float y, int dir, Paint paint) {
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        int modifiers = setupModifiers(paint);
        try {
            int flags = dir == 0 ? 0 : 1;
            if ((text instanceof String) || (text instanceof SpannedString) || (text instanceof SpannableString)) {
                nDrawTextRun(this.mRenderer, text.toString(), start, end, contextStart, contextEnd, x, y, flags, paint.mNativePaint);
            } else if (text instanceof GraphicsOperations) {
                ((GraphicsOperations) text).drawTextRun(this, start, end, contextStart, contextEnd, x, y, flags, paint);
            } else {
                int contextLen = contextEnd - contextStart;
                int len = end - start;
                char[] buf = TemporaryBuffer.obtain(contextLen);
                TextUtils.getChars(text, contextStart, contextEnd, buf, 0);
                nDrawTextRun(this.mRenderer, buf, start - contextStart, len, 0, contextLen, x, y, flags, paint.mNativePaint);
                TemporaryBuffer.recycle(buf);
            }
        } finally {
            if (modifiers != 0) {
                nResetModifiers(this.mRenderer, modifiers);
            }
        }
    }

    @Override // android.graphics.Canvas
    public void drawVertices(Canvas.VertexMode mode, int vertexCount, float[] verts, int vertOffset, float[] texs, int texOffset, int[] colors, int colorOffset, short[] indices, int indexOffset, int indexCount, Paint paint) {
    }

    private int setupModifiers(Bitmap b, Paint paint) {
        if (b.getConfig() != Bitmap.Config.ALPHA_8) {
            ColorFilter filter = paint.getColorFilter();
            if (filter != null) {
                nSetupColorFilter(this.mRenderer, filter.nativeColorFilter);
                return 4;
            }
            return 0;
        }
        return setupModifiers(paint);
    }

    private int setupModifiers(Paint paint) {
        int modifiers = 0;
        if (paint.hasShadow) {
            nSetupShadow(this.mRenderer, paint.shadowRadius, paint.shadowDx, paint.shadowDy, paint.shadowColor);
            modifiers = 0 | 1;
        }
        Shader shader = paint.getShader();
        if (shader != null) {
            nSetupShader(this.mRenderer, shader.native_shader);
            modifiers |= 2;
        }
        ColorFilter filter = paint.getColorFilter();
        if (filter != null) {
            nSetupColorFilter(this.mRenderer, filter.nativeColorFilter);
            modifiers |= 4;
        }
        return modifiers;
    }

    private int setupModifiers(Paint paint, int flags) {
        int modifiers = 0;
        if (paint.hasShadow && (flags & 1) != 0) {
            nSetupShadow(this.mRenderer, paint.shadowRadius, paint.shadowDx, paint.shadowDy, paint.shadowColor);
            modifiers = 0 | 1;
        }
        Shader shader = paint.getShader();
        if (shader != null && (flags & 2) != 0) {
            nSetupShader(this.mRenderer, shader.native_shader);
            modifiers |= 2;
        }
        ColorFilter filter = paint.getColorFilter();
        if (filter != null && (flags & 4) != 0) {
            nSetupColorFilter(this.mRenderer, filter.nativeColorFilter);
            modifiers |= 4;
        }
        return modifiers;
    }

    private int setupColorFilter(Paint paint) {
        ColorFilter filter = paint.getColorFilter();
        if (filter != null) {
            nSetupColorFilter(this.mRenderer, filter.nativeColorFilter);
            return 4;
        }
        return 0;
    }
}