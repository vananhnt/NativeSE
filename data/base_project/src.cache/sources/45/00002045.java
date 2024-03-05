package com.android.webview.chromium;

import android.util.Log;
import android.view.HardwareCanvas;
import android.view.ViewRootImpl;
import com.android.org.chromium.content.common.CleanupReference;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: DrawGLFunctor.class */
public class DrawGLFunctor {
    private static final String TAG = DrawGLFunctor.class.getSimpleName();
    private CleanupReference mCleanupReference;
    private DestroyRunnable mDestroyRunnable;

    private static native int nativeCreateGLFunctor(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeDestroyGLFunctor(int i);

    private static native void nativeSetChromiumAwDrawGLFunction(int i);

    public DrawGLFunctor(int viewContext) {
        this.mDestroyRunnable = new DestroyRunnable(nativeCreateGLFunctor(viewContext));
        this.mCleanupReference = new CleanupReference(this, this.mDestroyRunnable);
    }

    public void destroy() {
        if (this.mCleanupReference != null) {
            this.mCleanupReference.cleanupNow();
            this.mCleanupReference = null;
            this.mDestroyRunnable = null;
        }
    }

    public void detach() {
        this.mDestroyRunnable.detachNativeFunctor();
    }

    public boolean requestDrawGL(HardwareCanvas canvas, ViewRootImpl viewRootImpl) {
        if (this.mDestroyRunnable.mNativeDrawGLFunctor == 0) {
            throw new RuntimeException("requested DrawGL on already destroyed DrawGLFunctor");
        }
        this.mDestroyRunnable.mViewRootImpl = viewRootImpl;
        if (canvas != null) {
            int ret = canvas.callDrawGLFunction(this.mDestroyRunnable.mNativeDrawGLFunctor);
            if (ret != 0) {
                Log.e(TAG, "callDrawGLFunction error: " + ret);
                return false;
            }
            return true;
        }
        viewRootImpl.attachFunctor(this.mDestroyRunnable.mNativeDrawGLFunctor);
        return true;
    }

    public static void setChromiumAwDrawGLFunction(int functionPointer) {
        nativeSetChromiumAwDrawGLFunction(functionPointer);
    }

    /* loaded from: DrawGLFunctor$DestroyRunnable.class */
    private static final class DestroyRunnable implements Runnable {
        ViewRootImpl mViewRootImpl;
        int mNativeDrawGLFunctor;

        DestroyRunnable(int nativeDrawGLFunctor) {
            this.mNativeDrawGLFunctor = nativeDrawGLFunctor;
        }

        @Override // java.lang.Runnable
        public void run() {
            detachNativeFunctor();
            DrawGLFunctor.nativeDestroyGLFunctor(this.mNativeDrawGLFunctor);
            this.mNativeDrawGLFunctor = 0;
        }

        void detachNativeFunctor() {
            if (this.mNativeDrawGLFunctor != 0 && this.mViewRootImpl != null) {
                this.mViewRootImpl.detachFunctor(this.mNativeDrawGLFunctor);
            }
            this.mViewRootImpl = null;
        }
    }
}