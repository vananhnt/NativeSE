package android.view;

import android.content.res.CompatibilityInfo;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import dalvik.system.CloseGuard;
import gov.nist.core.Separators;

/* loaded from: Surface.class */
public class Surface implements Parcelable {
    private static final String TAG = "Surface";
    public static final Parcelable.Creator<Surface> CREATOR = new Parcelable.Creator<Surface>() { // from class: android.view.Surface.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Surface createFromParcel(Parcel source) {
            try {
                Surface s = new Surface();
                s.readFromParcel(source);
                return s;
            } catch (Exception e) {
                Log.e(Surface.TAG, "Exception creating surface from parcel", e);
                return null;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Surface[] newArray(int size) {
            return new Surface[size];
        }
    };
    private String mName;
    int mNativeObject;
    private int mLockedObject;
    private int mGenerationId;
    private Matrix mCompatibleMatrix;
    public static final int ROTATION_0 = 0;
    public static final int ROTATION_90 = 1;
    public static final int ROTATION_180 = 2;
    public static final int ROTATION_270 = 3;
    private final CloseGuard mCloseGuard = CloseGuard.get();
    final Object mLock = new Object();
    private final Canvas mCanvas = new CompatibleCanvas();

    private static native int nativeCreateFromSurfaceTexture(SurfaceTexture surfaceTexture) throws OutOfResourcesException;

    private static native int nativeCreateFromSurfaceControl(int i);

    private static native int nativeLockCanvas(int i, Canvas canvas, Rect rect) throws OutOfResourcesException;

    private static native void nativeUnlockCanvasAndPost(int i, Canvas canvas);

    private static native void nativeRelease(int i);

    private static native boolean nativeIsValid(int i);

    private static native boolean nativeIsConsumerRunningBehind(int i);

    private static native int nativeReadFromParcel(int i, Parcel parcel);

    private static native void nativeWriteToParcel(int i, Parcel parcel);

    public Surface() {
    }

    public Surface(SurfaceTexture surfaceTexture) {
        if (surfaceTexture == null) {
            throw new IllegalArgumentException("surfaceTexture must not be null");
        }
        synchronized (this.mLock) {
            this.mName = surfaceTexture.toString();
            setNativeObjectLocked(nativeCreateFromSurfaceTexture(surfaceTexture));
        }
    }

    private Surface(int nativeObject) {
        synchronized (this.mLock) {
            setNativeObjectLocked(nativeObject);
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mCloseGuard != null) {
                this.mCloseGuard.warnIfOpen();
            }
            release();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public void release() {
        synchronized (this.mLock) {
            if (this.mNativeObject != 0) {
                nativeRelease(this.mNativeObject);
                setNativeObjectLocked(0);
            }
        }
    }

    public void destroy() {
        release();
    }

    public boolean isValid() {
        synchronized (this.mLock) {
            if (this.mNativeObject == 0) {
                return false;
            }
            return nativeIsValid(this.mNativeObject);
        }
    }

    public int getGenerationId() {
        int i;
        synchronized (this.mLock) {
            i = this.mGenerationId;
        }
        return i;
    }

    public boolean isConsumerRunningBehind() {
        boolean nativeIsConsumerRunningBehind;
        synchronized (this.mLock) {
            checkNotReleasedLocked();
            nativeIsConsumerRunningBehind = nativeIsConsumerRunningBehind(this.mNativeObject);
        }
        return nativeIsConsumerRunningBehind;
    }

    public Canvas lockCanvas(Rect inOutDirty) throws OutOfResourcesException, IllegalArgumentException {
        Canvas canvas;
        synchronized (this.mLock) {
            checkNotReleasedLocked();
            if (this.mLockedObject != 0) {
                throw new IllegalStateException("Surface was already locked");
            }
            this.mLockedObject = nativeLockCanvas(this.mNativeObject, this.mCanvas, inOutDirty);
            canvas = this.mCanvas;
        }
        return canvas;
    }

    public void unlockCanvasAndPost(Canvas canvas) {
        if (canvas != this.mCanvas) {
            throw new IllegalArgumentException("canvas object must be the same instance that was previously returned by lockCanvas");
        }
        synchronized (this.mLock) {
            checkNotReleasedLocked();
            if (this.mNativeObject != this.mLockedObject) {
                Log.w(TAG, "WARNING: Surface's mNativeObject (0x" + Integer.toHexString(this.mNativeObject) + ") != mLockedObject (0x" + Integer.toHexString(this.mLockedObject) + Separators.RPAREN);
            }
            if (this.mLockedObject == 0) {
                throw new IllegalStateException("Surface was not locked");
            }
            nativeUnlockCanvasAndPost(this.mLockedObject, canvas);
            nativeRelease(this.mLockedObject);
            this.mLockedObject = 0;
        }
    }

    @Deprecated
    public void unlockCanvas(Canvas canvas) {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCompatibilityTranslator(CompatibilityInfo.Translator translator) {
        if (translator != null) {
            float appScale = translator.applicationScale;
            this.mCompatibleMatrix = new Matrix();
            this.mCompatibleMatrix.setScale(appScale, appScale);
        }
    }

    public void copyFrom(SurfaceControl other) {
        if (other == null) {
            throw new IllegalArgumentException("other must not be null");
        }
        int surfaceControlPtr = other.mNativeObject;
        if (surfaceControlPtr == 0) {
            throw new NullPointerException("SurfaceControl native object is null. Are you using a released SurfaceControl?");
        }
        int newNativeObject = nativeCreateFromSurfaceControl(surfaceControlPtr);
        synchronized (this.mLock) {
            if (this.mNativeObject != 0) {
                nativeRelease(this.mNativeObject);
            }
            setNativeObjectLocked(newNativeObject);
        }
    }

    @Deprecated
    public void transferFrom(Surface other) {
        int newPtr;
        if (other == null) {
            throw new IllegalArgumentException("other must not be null");
        }
        if (other != this) {
            synchronized (other.mLock) {
                newPtr = other.mNativeObject;
                other.setNativeObjectLocked(0);
            }
            synchronized (this.mLock) {
                if (this.mNativeObject != 0) {
                    nativeRelease(this.mNativeObject);
                }
                setNativeObjectLocked(newPtr);
            }
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel source) {
        if (source == null) {
            throw new IllegalArgumentException("source must not be null");
        }
        synchronized (this.mLock) {
            this.mName = source.readString();
            setNativeObjectLocked(nativeReadFromParcel(this.mNativeObject, source));
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (dest == null) {
            throw new IllegalArgumentException("dest must not be null");
        }
        synchronized (this.mLock) {
            dest.writeString(this.mName);
            nativeWriteToParcel(this.mNativeObject, dest);
        }
        if ((flags & 1) != 0) {
            release();
        }
    }

    public String toString() {
        String str;
        synchronized (this.mLock) {
            str = "Surface(name=" + this.mName + ")/@0x" + Integer.toHexString(System.identityHashCode(this));
        }
        return str;
    }

    private void setNativeObjectLocked(int ptr) {
        if (this.mNativeObject != ptr) {
            if (this.mNativeObject == 0 && ptr != 0) {
                this.mCloseGuard.open("release");
            } else if (this.mNativeObject != 0 && ptr == 0) {
                this.mCloseGuard.close();
            }
            this.mNativeObject = ptr;
            this.mGenerationId++;
        }
    }

    private void checkNotReleasedLocked() {
        if (this.mNativeObject == 0) {
            throw new IllegalStateException("Surface has already been released.");
        }
    }

    /* loaded from: Surface$OutOfResourcesException.class */
    public static class OutOfResourcesException extends RuntimeException {
        public OutOfResourcesException() {
        }

        public OutOfResourcesException(String name) {
            super(name);
        }
    }

    public static String rotationToString(int rotation) {
        switch (rotation) {
            case 0:
                return "ROTATION_0";
            case 1:
                return "ROATATION_90";
            case 2:
                return "ROATATION_180";
            case 3:
                return "ROATATION_270";
            default:
                throw new IllegalArgumentException("Invalid rotation: " + rotation);
        }
    }

    /* loaded from: Surface$CompatibleCanvas.class */
    private final class CompatibleCanvas extends Canvas {
        private Matrix mOrigMatrix;

        private CompatibleCanvas() {
            this.mOrigMatrix = null;
        }

        @Override // android.graphics.Canvas
        public void setMatrix(Matrix matrix) {
            if (Surface.this.mCompatibleMatrix == null || this.mOrigMatrix == null || this.mOrigMatrix.equals(matrix)) {
                super.setMatrix(matrix);
                return;
            }
            Matrix m = new Matrix(Surface.this.mCompatibleMatrix);
            m.preConcat(matrix);
            super.setMatrix(m);
        }

        @Override // android.graphics.Canvas
        public void getMatrix(Matrix m) {
            super.getMatrix(m);
            if (this.mOrigMatrix == null) {
                this.mOrigMatrix = new Matrix();
            }
            this.mOrigMatrix.set(m);
        }
    }
}