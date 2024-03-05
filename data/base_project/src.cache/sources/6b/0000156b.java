package android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/* loaded from: TextureView.class */
public class TextureView extends View {
    private static final String LOG_TAG = "TextureView";
    private HardwareLayer mLayer;
    private SurfaceTexture mSurface;
    private SurfaceTextureListener mListener;
    private boolean mHadSurface;
    private boolean mOpaque;
    private final Matrix mMatrix;
    private boolean mMatrixChanged;
    private final Object[] mLock;
    private boolean mUpdateLayer;
    private boolean mUpdateSurface;
    private SurfaceTexture.OnFrameAvailableListener mUpdateListener;
    private Canvas mCanvas;
    private int mSaveCount;
    private final Object[] mNativeWindowLock;
    private int mNativeWindow;

    /* loaded from: TextureView$SurfaceTextureListener.class */
    public interface SurfaceTextureListener {
        void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2);

        void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2);

        boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);
    }

    private native void nCreateNativeWindow(SurfaceTexture surfaceTexture);

    private native void nDestroyNativeWindow();

    private static native boolean nLockCanvas(int i, Canvas canvas, Rect rect);

    private static native void nUnlockCanvasAndPost(int i, Canvas canvas);

    public TextureView(Context context) {
        super(context);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        init();
    }

    public TextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        init();
    }

    public TextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        init();
    }

    private void init() {
        this.mLayerPaint = new Paint();
    }

    @Override // android.view.View
    public boolean isOpaque() {
        return this.mOpaque;
    }

    public void setOpaque(boolean opaque) {
        if (opaque != this.mOpaque) {
            this.mOpaque = opaque;
            if (this.mLayer != null) {
                updateLayerAndInvalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isHardwareAccelerated()) {
            Log.w(LOG_TAG, "A TextureView or a subclass can only be used with hardware acceleration enabled.");
        }
        if (this.mHadSurface) {
            invalidate(true);
            this.mHadSurface = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mLayer != null) {
            boolean success = executeHardwareAction(new Runnable() { // from class: android.view.TextureView.1
                @Override // java.lang.Runnable
                public void run() {
                    TextureView.this.destroySurface();
                }
            });
            if (!success) {
                Log.w(LOG_TAG, "TextureView was not able to destroy its surface: " + this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void destroySurface() {
        if (this.mLayer != null) {
            this.mSurface.detachFromGLContext();
            this.mLayer.clearStorage();
            boolean shouldRelease = true;
            if (this.mListener != null) {
                shouldRelease = this.mListener.onSurfaceTextureDestroyed(this.mSurface);
            }
            synchronized (this.mNativeWindowLock) {
                nDestroyNativeWindow();
            }
            this.mLayer.destroy();
            if (shouldRelease) {
                this.mSurface.release();
            }
            this.mSurface = null;
            this.mLayer = null;
            this.mHadSurface = true;
        }
    }

    @Override // android.view.View
    public void setLayerType(int layerType, Paint paint) {
        if (paint != this.mLayerPaint) {
            this.mLayerPaint = paint == null ? new Paint() : paint;
            invalidate();
        }
    }

    @Override // android.view.View
    public int getLayerType() {
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.View
    public boolean hasStaticLayer() {
        return true;
    }

    @Override // android.view.View
    public void buildLayer() {
    }

    @Override // android.view.View
    public final void draw(Canvas canvas) {
        this.mPrivateFlags = (this.mPrivateFlags & (-6291457)) | 32;
        applyUpdate();
        applyTransformMatrix();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public final void onDraw(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mSurface != null) {
            this.mSurface.setDefaultBufferSize(getWidth(), getHeight());
            updateLayer();
            if (this.mListener != null) {
                this.mListener.onSurfaceTextureSizeChanged(this.mSurface, getWidth(), getHeight());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.view.View
    public boolean destroyLayer(boolean valid) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void destroyHardwareResources() {
        super.destroyHardwareResources();
        destroySurface();
        invalidateParentCaches();
        invalidate(true);
    }

    @Override // android.view.View
    HardwareLayer getHardwareLayer() {
        this.mPrivateFlags |= 32800;
        this.mPrivateFlags &= -6291457;
        if (this.mLayer == null) {
            if (this.mAttachInfo == null || this.mAttachInfo.mHardwareRenderer == null) {
                return null;
            }
            this.mLayer = this.mAttachInfo.mHardwareRenderer.createHardwareLayer(this.mOpaque);
            if (!this.mUpdateSurface) {
                this.mSurface = this.mAttachInfo.mHardwareRenderer.createSurfaceTexture(this.mLayer);
            }
            this.mSurface.setDefaultBufferSize(getWidth(), getHeight());
            nCreateNativeWindow(this.mSurface);
            this.mUpdateListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.view.TextureView.2
                @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    TextureView.this.updateLayer();
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        TextureView.this.invalidate();
                    } else {
                        TextureView.this.postInvalidate();
                    }
                }
            };
            this.mSurface.setOnFrameAvailableListener(this.mUpdateListener);
            if (this.mListener != null && !this.mUpdateSurface) {
                this.mListener.onSurfaceTextureAvailable(this.mSurface, getWidth(), getHeight());
            }
            this.mLayer.setLayerPaint(this.mLayerPaint);
        }
        if (this.mUpdateSurface) {
            this.mUpdateSurface = false;
            updateLayer();
            this.mMatrixChanged = true;
            this.mAttachInfo.mHardwareRenderer.setSurfaceTexture(this.mLayer, this.mSurface);
            this.mSurface.setDefaultBufferSize(getWidth(), getHeight());
        }
        applyUpdate();
        applyTransformMatrix();
        return this.mLayer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (this.mSurface != null) {
            if (visibility == 0) {
                this.mSurface.setOnFrameAvailableListener(this.mUpdateListener);
                updateLayerAndInvalidate();
                return;
            }
            this.mSurface.setOnFrameAvailableListener(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLayer() {
        synchronized (this.mLock) {
            this.mUpdateLayer = true;
        }
    }

    private void updateLayerAndInvalidate() {
        synchronized (this.mLock) {
            this.mUpdateLayer = true;
        }
        invalidate();
    }

    private void applyUpdate() {
        if (this.mLayer == null) {
            return;
        }
        synchronized (this.mLock) {
            if (this.mUpdateLayer) {
                this.mUpdateLayer = false;
                this.mLayer.update(getWidth(), getHeight(), this.mOpaque);
                if (this.mListener != null) {
                    this.mListener.onSurfaceTextureUpdated(this.mSurface);
                }
            }
        }
    }

    public void setTransform(Matrix transform) {
        this.mMatrix.set(transform);
        this.mMatrixChanged = true;
        invalidateParentIfNeeded();
    }

    public Matrix getTransform(Matrix transform) {
        if (transform == null) {
            transform = new Matrix();
        }
        transform.set(this.mMatrix);
        return transform;
    }

    private void applyTransformMatrix() {
        if (this.mMatrixChanged && this.mLayer != null) {
            this.mLayer.setTransform(this.mMatrix);
            this.mMatrixChanged = false;
        }
    }

    public Bitmap getBitmap() {
        return getBitmap(getWidth(), getHeight());
    }

    public Bitmap getBitmap(int width, int height) {
        if (isAvailable() && width > 0 && height > 0) {
            return getBitmap(Bitmap.createBitmap(getResources().getDisplayMetrics(), width, height, Bitmap.Config.ARGB_8888));
        }
        return null;
    }

    public Bitmap getBitmap(Bitmap bitmap) {
        if (bitmap != null && isAvailable()) {
            View.AttachInfo info = this.mAttachInfo;
            if (info != null && info.mHardwareRenderer != null && info.mHardwareRenderer.isEnabled() && !info.mHardwareRenderer.validate()) {
                throw new IllegalStateException("Could not acquire hardware rendering context");
            }
            applyUpdate();
            applyTransformMatrix();
            if (this.mLayer == null && this.mUpdateSurface) {
                getHardwareLayer();
            }
            if (this.mLayer != null) {
                this.mLayer.copyInto(bitmap);
            }
        }
        return bitmap;
    }

    public boolean isAvailable() {
        return this.mSurface != null;
    }

    public Canvas lockCanvas() {
        return lockCanvas(null);
    }

    public Canvas lockCanvas(Rect dirty) {
        if (isAvailable()) {
            if (this.mCanvas == null) {
                this.mCanvas = new Canvas();
            }
            synchronized (this.mNativeWindowLock) {
                if (!nLockCanvas(this.mNativeWindow, this.mCanvas, dirty)) {
                    return null;
                }
                this.mSaveCount = this.mCanvas.save();
                return this.mCanvas;
            }
        }
        return null;
    }

    public void unlockCanvasAndPost(Canvas canvas) {
        if (this.mCanvas != null && canvas == this.mCanvas) {
            canvas.restoreToCount(this.mSaveCount);
            this.mSaveCount = 0;
            synchronized (this.mNativeWindowLock) {
                nUnlockCanvasAndPost(this.mNativeWindow, this.mCanvas);
            }
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.mSurface;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (surfaceTexture == null) {
            throw new NullPointerException("surfaceTexture must not be null");
        }
        if (this.mSurface != null) {
            this.mSurface.release();
        }
        this.mSurface = surfaceTexture;
        this.mUpdateSurface = true;
        invalidateParentIfNeeded();
    }

    public SurfaceTextureListener getSurfaceTextureListener() {
        return this.mListener;
    }

    public void setSurfaceTextureListener(SurfaceTextureListener listener) {
        this.mListener = listener;
    }
}