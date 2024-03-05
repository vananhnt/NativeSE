package android.graphics;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import java.lang.ref.WeakReference;

/* loaded from: SurfaceTexture.class */
public class SurfaceTexture {
    private EventHandler mEventHandler;
    private OnFrameAvailableListener mOnFrameAvailableListener;
    private int mSurfaceTexture;
    private int mBufferQueue;
    private int mFrameAvailableListener;

    /* loaded from: SurfaceTexture$OnFrameAvailableListener.class */
    public interface OnFrameAvailableListener {
        void onFrameAvailable(SurfaceTexture surfaceTexture);
    }

    private native void nativeInit(int i, boolean z, Object obj) throws Surface.OutOfResourcesException;

    private native void nativeFinalize();

    private native void nativeGetTransformMatrix(float[] fArr);

    private native long nativeGetTimestamp();

    private native void nativeSetDefaultBufferSize(int i, int i2);

    private native void nativeUpdateTexImage();

    private native void nativeReleaseTexImage();

    private native int nativeDetachFromGLContext();

    private native int nativeAttachToGLContext(int i);

    private native int nativeGetQueuedCount();

    private native void nativeRelease();

    private static native void nativeClassInit();

    @Deprecated
    /* loaded from: SurfaceTexture$OutOfResourcesException.class */
    public static class OutOfResourcesException extends Exception {
        public OutOfResourcesException() {
        }

        public OutOfResourcesException(String name) {
            super(name);
        }
    }

    public SurfaceTexture(int texName) {
        init(texName, false);
    }

    public SurfaceTexture(int texName, boolean singleBufferMode) {
        init(texName, singleBufferMode);
    }

    public void setOnFrameAvailableListener(OnFrameAvailableListener l) {
        this.mOnFrameAvailableListener = l;
    }

    public void setDefaultBufferSize(int width, int height) {
        nativeSetDefaultBufferSize(width, height);
    }

    public void updateTexImage() {
        nativeUpdateTexImage();
    }

    public void releaseTexImage() {
        nativeReleaseTexImage();
    }

    public void detachFromGLContext() {
        int err = nativeDetachFromGLContext();
        if (err != 0) {
            throw new RuntimeException("Error during detachFromGLContext (see logcat for details)");
        }
    }

    public void attachToGLContext(int texName) {
        int err = nativeAttachToGLContext(texName);
        if (err != 0) {
            throw new RuntimeException("Error during attachToGLContext (see logcat for details)");
        }
    }

    public void getTransformMatrix(float[] mtx) {
        if (mtx.length != 16) {
            throw new IllegalArgumentException();
        }
        nativeGetTransformMatrix(mtx);
    }

    public long getTimestamp() {
        return nativeGetTimestamp();
    }

    public void release() {
        nativeRelease();
    }

    protected void finalize() throws Throwable {
        try {
            nativeFinalize();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SurfaceTexture$EventHandler.class */
    public class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (SurfaceTexture.this.mOnFrameAvailableListener != null) {
                SurfaceTexture.this.mOnFrameAvailableListener.onFrameAvailable(SurfaceTexture.this);
            }
        }
    }

    private static void postEventFromNative(Object selfRef) {
        WeakReference weakSelf = (WeakReference) selfRef;
        SurfaceTexture st = (SurfaceTexture) weakSelf.get();
        if (st != null && st.mEventHandler != null) {
            Message m = st.mEventHandler.obtainMessage();
            st.mEventHandler.sendMessage(m);
        }
    }

    private void init(int texName, boolean singleBufferMode) throws Surface.OutOfResourcesException {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this.mEventHandler = new EventHandler(looper);
        } else {
            Looper looper2 = Looper.getMainLooper();
            if (looper2 != null) {
                this.mEventHandler = new EventHandler(looper2);
            } else {
                this.mEventHandler = null;
            }
        }
        nativeInit(texName, singleBufferMode, new WeakReference(this));
    }

    static {
        nativeClassInit();
    }
}