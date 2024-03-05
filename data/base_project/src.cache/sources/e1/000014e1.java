package android.view;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.view.GLES20Layer;

/* loaded from: GLES20TextureLayer.class */
class GLES20TextureLayer extends GLES20Layer {
    private int mTexture;
    private SurfaceTexture mSurface;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GLES20TextureLayer(boolean isOpaque) {
        int[] layerInfo = new int[2];
        this.mLayer = GLES20Canvas.nCreateTextureLayer(isOpaque, layerInfo);
        if (this.mLayer != 0) {
            this.mTexture = layerInfo[0];
            this.mFinalizer = new GLES20Layer.Finalizer(this.mLayer);
            return;
        }
        this.mFinalizer = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public boolean isValid() {
        return (this.mLayer == 0 || this.mTexture == 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public boolean resize(int width, int height) {
        return isValid();
    }

    @Override // android.view.HardwareLayer
    HardwareCanvas getCanvas() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public HardwareCanvas start(Canvas currentCanvas) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public HardwareCanvas start(Canvas currentCanvas, Rect dirty) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void end(Canvas currentCanvas) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SurfaceTexture getSurfaceTexture() {
        if (this.mSurface == null) {
            this.mSurface = new SurfaceTexture(this.mTexture);
        }
        return this.mSurface;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (this.mSurface != null) {
            this.mSurface.release();
        }
        this.mSurface = surfaceTexture;
        this.mSurface.attachToGLContext(this.mTexture);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void update(int width, int height, boolean isOpaque) {
        super.update(width, height, isOpaque);
        GLES20Canvas.nUpdateTextureLayer(this.mLayer, width, height, isOpaque, this.mSurface);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void setOpaque(boolean isOpaque) {
        throw new UnsupportedOperationException("Use update(int, int, boolean) instead");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void setTransform(Matrix matrix) {
        GLES20Canvas.nSetTextureLayerTransform(this.mLayer, matrix.native_instance);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void redrawLater(DisplayList displayList, Rect dirtyRect) {
    }
}