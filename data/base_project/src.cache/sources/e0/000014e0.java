package android.view;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.GLES20Layer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: GLES20RenderLayer.class */
public class GLES20RenderLayer extends GLES20Layer {
    private int mLayerWidth;
    private int mLayerHeight;
    private final GLES20Canvas mCanvas;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GLES20RenderLayer(int width, int height, boolean isOpaque) {
        super(width, height, isOpaque);
        int[] layerInfo = new int[2];
        this.mLayer = GLES20Canvas.nCreateLayer(width, height, isOpaque, layerInfo);
        if (this.mLayer != 0) {
            this.mLayerWidth = layerInfo[0];
            this.mLayerHeight = layerInfo[1];
            this.mCanvas = new GLES20Canvas(this.mLayer, !isOpaque);
            this.mFinalizer = new GLES20Layer.Finalizer(this.mLayer);
            return;
        }
        this.mCanvas = null;
        this.mFinalizer = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public boolean isValid() {
        return this.mLayer != 0 && this.mLayerWidth > 0 && this.mLayerHeight > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public boolean resize(int width, int height) {
        if (!isValid() || width <= 0 || height <= 0) {
            return false;
        }
        this.mWidth = width;
        this.mHeight = height;
        if (width != this.mLayerWidth || height != this.mLayerHeight) {
            int[] layerInfo = new int[2];
            if (GLES20Canvas.nResizeLayer(this.mLayer, width, height, layerInfo)) {
                this.mLayerWidth = layerInfo[0];
                this.mLayerHeight = layerInfo[1];
            } else {
                this.mLayer = 0;
                this.mLayerWidth = 0;
                this.mLayerHeight = 0;
            }
        }
        return isValid();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void setOpaque(boolean isOpaque) {
        this.mOpaque = isOpaque;
        GLES20Canvas.nSetOpaqueLayer(this.mLayer, isOpaque);
    }

    @Override // android.view.HardwareLayer
    HardwareCanvas getCanvas() {
        return this.mCanvas;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void end(Canvas currentCanvas) {
        HardwareCanvas canvas = getCanvas();
        if (canvas != null) {
            canvas.onPostDraw();
        }
        if (currentCanvas instanceof GLES20Canvas) {
            ((GLES20Canvas) currentCanvas).resume();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public HardwareCanvas start(Canvas currentCanvas) {
        return start(currentCanvas, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public HardwareCanvas start(Canvas currentCanvas, Rect dirty) {
        if (currentCanvas instanceof GLES20Canvas) {
            ((GLES20Canvas) currentCanvas).interrupt();
        }
        HardwareCanvas canvas = getCanvas();
        canvas.setViewport(this.mWidth, this.mHeight);
        canvas.onPreDraw(dirty);
        return canvas;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void setTransform(Matrix matrix) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void redrawLater(DisplayList displayList, Rect dirtyRect) {
        GLES20Canvas.nUpdateRenderLayer(this.mLayer, this.mCanvas.getRenderer(), ((GLES20DisplayList) displayList).getNativeDisplayList(), dirtyRect.left, dirtyRect.top, dirtyRect.right, dirtyRect.bottom);
    }
}