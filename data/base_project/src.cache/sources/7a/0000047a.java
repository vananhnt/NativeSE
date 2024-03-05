package android.filterfw.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/* loaded from: FilterSurfaceView.class */
public class FilterSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static int STATE_ALLOCATED = 0;
    private static int STATE_CREATED = 1;
    private static int STATE_INITIALIZED = 2;
    private int mState;
    private SurfaceHolder.Callback mListener;
    private GLEnvironment mGLEnv;
    private int mFormat;
    private int mWidth;
    private int mHeight;
    private int mSurfaceId;

    public FilterSurfaceView(Context context) {
        super(context);
        this.mState = STATE_ALLOCATED;
        this.mSurfaceId = -1;
        getHolder().addCallback(this);
    }

    public FilterSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mState = STATE_ALLOCATED;
        this.mSurfaceId = -1;
        getHolder().addCallback(this);
    }

    public synchronized void bindToListener(SurfaceHolder.Callback listener, GLEnvironment glEnv) {
        if (listener == null) {
            throw new NullPointerException("Attempting to bind null filter to SurfaceView!");
        }
        if (this.mListener != null && this.mListener != listener) {
            throw new RuntimeException("Attempting to bind filter " + listener + " to SurfaceView with another open filter " + this.mListener + " attached already!");
        }
        this.mListener = listener;
        if (this.mGLEnv != null && this.mGLEnv != glEnv) {
            this.mGLEnv.unregisterSurfaceId(this.mSurfaceId);
        }
        this.mGLEnv = glEnv;
        if (this.mState >= STATE_CREATED) {
            registerSurface();
            this.mListener.surfaceCreated(getHolder());
            if (this.mState == STATE_INITIALIZED) {
                this.mListener.surfaceChanged(getHolder(), this.mFormat, this.mWidth, this.mHeight);
            }
        }
    }

    public synchronized void unbind() {
        this.mListener = null;
    }

    public synchronized int getSurfaceId() {
        return this.mSurfaceId;
    }

    public synchronized GLEnvironment getGLEnv() {
        return this.mGLEnv;
    }

    @Override // android.view.SurfaceHolder.Callback
    public synchronized void surfaceCreated(SurfaceHolder holder) {
        this.mState = STATE_CREATED;
        if (this.mGLEnv != null) {
            registerSurface();
        }
        if (this.mListener != null) {
            this.mListener.surfaceCreated(holder);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public synchronized void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mFormat = format;
        this.mWidth = width;
        this.mHeight = height;
        this.mState = STATE_INITIALIZED;
        if (this.mListener != null) {
            this.mListener.surfaceChanged(holder, format, width, height);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public synchronized void surfaceDestroyed(SurfaceHolder holder) {
        this.mState = STATE_ALLOCATED;
        if (this.mListener != null) {
            this.mListener.surfaceDestroyed(holder);
        }
        unregisterSurface();
    }

    private void registerSurface() {
        this.mSurfaceId = this.mGLEnv.registerSurface(getHolder().getSurface());
        if (this.mSurfaceId < 0) {
            throw new RuntimeException("Could not register Surface: " + getHolder().getSurface() + " in FilterSurfaceView!");
        }
    }

    private void unregisterSurface() {
        if (this.mGLEnv != null && this.mSurfaceId > 0) {
            this.mGLEnv.unregisterSurfaceId(this.mSurfaceId);
        }
    }
}