package android.opengl;

import android.os.Looper;
import android.util.Log;
import com.google.android.gles_jni.EGLImpl;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;

/* loaded from: ManagedEGLContext.class */
public abstract class ManagedEGLContext {
    static final String TAG = "ManagedEGLContext";
    static final ArrayList<ManagedEGLContext> sActive = new ArrayList<>();
    final javax.microedition.khronos.egl.EGLContext mContext;

    public abstract void onTerminate(javax.microedition.khronos.egl.EGLContext eGLContext);

    public ManagedEGLContext(javax.microedition.khronos.egl.EGLContext context) {
        this.mContext = context;
        synchronized (sActive) {
            sActive.add(this);
        }
    }

    public javax.microedition.khronos.egl.EGLContext getContext() {
        return this.mContext;
    }

    public void terminate() {
        execTerminate();
    }

    void execTerminate() {
        onTerminate(this.mContext);
    }

    public static boolean doTerminate() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Called on wrong thread");
        }
        synchronized (sActive) {
            if (sActive.size() <= 0) {
                return false;
            }
            EGL10 egl = (EGL10) javax.microedition.khronos.egl.EGLContext.getEGL();
            javax.microedition.khronos.egl.EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (display == EGL10.EGL_NO_DISPLAY) {
                Log.w(TAG, "doTerminate failed: no display");
                return false;
            } else if (EGLImpl.getInitCount(display) != sActive.size()) {
                Log.w(TAG, "doTerminate failed: EGL count is " + EGLImpl.getInitCount(display) + " but managed count is " + sActive.size());
                return false;
            } else {
                ArrayList<ManagedEGLContext> active = new ArrayList<>(sActive);
                sActive.clear();
                for (int i = 0; i < active.size(); i++) {
                    active.get(i).execTerminate();
                }
                return true;
            }
        }
    }
}