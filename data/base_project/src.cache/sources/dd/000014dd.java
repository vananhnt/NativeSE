package android.view;

import android.graphics.Bitmap;
import android.graphics.Paint;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: GLES20Layer.class */
public abstract class GLES20Layer extends HardwareLayer {
    int mLayer;
    Finalizer mFinalizer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GLES20Layer() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GLES20Layer(int width, int height, boolean opaque) {
        super(width, height, opaque);
    }

    public int getLayer() {
        return this.mLayer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void setLayerPaint(Paint paint) {
        if (paint != null) {
            GLES20Canvas.nSetLayerPaint(this.mLayer, paint.mNativePaint);
            GLES20Canvas.nSetLayerColorFilter(this.mLayer, paint.getColorFilter() != null ? paint.getColorFilter().nativeColorFilter : 0);
        }
    }

    @Override // android.view.HardwareLayer
    public boolean copyInto(Bitmap bitmap) {
        return GLES20Canvas.nCopyLayer(this.mLayer, bitmap.mNativeBitmap);
    }

    @Override // android.view.HardwareLayer
    public void destroy() {
        if (this.mDisplayList != null) {
            this.mDisplayList.reset();
        }
        if (this.mFinalizer != null) {
            this.mFinalizer.destroy();
            this.mFinalizer = null;
        }
        this.mLayer = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.HardwareLayer
    public void clearStorage() {
        if (this.mLayer != 0) {
            GLES20Canvas.nClearLayerTexture(this.mLayer);
        }
    }

    /* loaded from: GLES20Layer$Finalizer.class */
    static class Finalizer {
        private int mLayerId;

        public Finalizer(int layerId) {
            this.mLayerId = layerId;
        }

        protected void finalize() throws Throwable {
            try {
                if (this.mLayerId != 0) {
                    GLES20Canvas.nDestroyLayerDeferred(this.mLayerId);
                }
            } finally {
                super.finalize();
            }
        }

        void destroy() {
            GLES20Canvas.nDestroyLayer(this.mLayerId);
            this.mLayerId = 0;
        }
    }
}