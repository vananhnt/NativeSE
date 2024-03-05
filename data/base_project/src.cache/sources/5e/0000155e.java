package android.view;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.IBinder;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Surface;
import dalvik.system.CloseGuard;
import gov.nist.core.Separators;

/* loaded from: SurfaceControl.class */
public class SurfaceControl {
    private static final String TAG = "SurfaceControl";
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private final String mName;
    int mNativeObject;
    private static final boolean HEADLESS = "1".equals(SystemProperties.get("ro.config.headless", "0"));
    public static final int HIDDEN = 4;
    public static final int SECURE = 128;
    public static final int NON_PREMULTIPLIED = 256;
    public static final int OPAQUE = 1024;
    public static final int PROTECTED_APP = 2048;
    public static final int FX_SURFACE_NORMAL = 0;
    public static final int FX_SURFACE_DIM = 131072;
    public static final int FX_SURFACE_MASK = 983040;
    public static final int SURFACE_HIDDEN = 1;
    public static final int BUILT_IN_DISPLAY_ID_MAIN = 0;
    public static final int BUILT_IN_DISPLAY_ID_HDMI = 1;

    private static native int nativeCreate(SurfaceSession surfaceSession, String str, int i, int i2, int i3, int i4) throws Surface.OutOfResourcesException;

    private static native void nativeRelease(int i);

    private static native void nativeDestroy(int i);

    private static native Bitmap nativeScreenshot(IBinder iBinder, int i, int i2, int i3, int i4, boolean z);

    private static native void nativeScreenshot(IBinder iBinder, Surface surface, int i, int i2, int i3, int i4, boolean z);

    private static native void nativeOpenTransaction();

    private static native void nativeCloseTransaction();

    private static native void nativeSetAnimationTransaction();

    private static native void nativeSetLayer(int i, int i2);

    private static native void nativeSetPosition(int i, float f, float f2);

    private static native void nativeSetSize(int i, int i2, int i3);

    private static native void nativeSetTransparentRegionHint(int i, Region region);

    private static native void nativeSetAlpha(int i, float f);

    private static native void nativeSetMatrix(int i, float f, float f2, float f3, float f4);

    private static native void nativeSetFlags(int i, int i2, int i3);

    private static native void nativeSetWindowCrop(int i, int i2, int i3, int i4, int i5);

    private static native void nativeSetLayerStack(int i, int i2);

    private static native IBinder nativeGetBuiltInDisplay(int i);

    private static native IBinder nativeCreateDisplay(String str, boolean z);

    private static native void nativeDestroyDisplay(IBinder iBinder);

    private static native void nativeSetDisplaySurface(IBinder iBinder, int i);

    private static native void nativeSetDisplayLayerStack(IBinder iBinder, int i);

    private static native void nativeSetDisplayProjection(IBinder iBinder, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9);

    private static native boolean nativeGetDisplayInfo(IBinder iBinder, PhysicalDisplayInfo physicalDisplayInfo);

    private static native void nativeBlankDisplay(IBinder iBinder);

    private static native void nativeUnblankDisplay(IBinder iBinder);

    public SurfaceControl(SurfaceSession session, String name, int w, int h, int format, int flags) throws Surface.OutOfResourcesException {
        if (session == null) {
            throw new IllegalArgumentException("session must not be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if ((flags & 4) == 0) {
            Log.w(TAG, "Surfaces should always be created with the HIDDEN flag set to ensure that they are not made visible prematurely before all of the surface's properties have been configured.  Set the other properties and make the surface visible within a transaction.  New surface name: " + name, new Throwable());
        }
        checkHeadless();
        this.mName = name;
        this.mNativeObject = nativeCreate(session, name, w, h, format, flags);
        if (this.mNativeObject == 0) {
            throw new Surface.OutOfResourcesException("Couldn't allocate SurfaceControl native object");
        }
        this.mCloseGuard.open("release");
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mCloseGuard != null) {
                this.mCloseGuard.warnIfOpen();
            }
            if (this.mNativeObject != 0) {
                nativeRelease(this.mNativeObject);
            }
        } finally {
            super.finalize();
        }
    }

    public String toString() {
        return "Surface(name=" + this.mName + Separators.RPAREN;
    }

    public void release() {
        if (this.mNativeObject != 0) {
            nativeRelease(this.mNativeObject);
            this.mNativeObject = 0;
        }
        this.mCloseGuard.close();
    }

    public void destroy() {
        if (this.mNativeObject != 0) {
            nativeDestroy(this.mNativeObject);
            this.mNativeObject = 0;
        }
        this.mCloseGuard.close();
    }

    private void checkNotReleased() {
        if (this.mNativeObject == 0) {
            throw new NullPointerException("mNativeObject is null. Have you called release() already?");
        }
    }

    public static void openTransaction() {
        nativeOpenTransaction();
    }

    public static void closeTransaction() {
        nativeCloseTransaction();
    }

    public static void setAnimationTransaction() {
        nativeSetAnimationTransaction();
    }

    public void setLayer(int zorder) {
        checkNotReleased();
        nativeSetLayer(this.mNativeObject, zorder);
    }

    public void setPosition(float x, float y) {
        checkNotReleased();
        nativeSetPosition(this.mNativeObject, x, y);
    }

    public void setSize(int w, int h) {
        checkNotReleased();
        nativeSetSize(this.mNativeObject, w, h);
    }

    public void hide() {
        checkNotReleased();
        nativeSetFlags(this.mNativeObject, 1, 1);
    }

    public void show() {
        checkNotReleased();
        nativeSetFlags(this.mNativeObject, 0, 1);
    }

    public void setTransparentRegionHint(Region region) {
        checkNotReleased();
        nativeSetTransparentRegionHint(this.mNativeObject, region);
    }

    public void setAlpha(float alpha) {
        checkNotReleased();
        nativeSetAlpha(this.mNativeObject, alpha);
    }

    public void setMatrix(float dsdx, float dtdx, float dsdy, float dtdy) {
        checkNotReleased();
        nativeSetMatrix(this.mNativeObject, dsdx, dtdx, dsdy, dtdy);
    }

    public void setFlags(int flags, int mask) {
        checkNotReleased();
        nativeSetFlags(this.mNativeObject, flags, mask);
    }

    public void setWindowCrop(Rect crop) {
        checkNotReleased();
        if (crop != null) {
            nativeSetWindowCrop(this.mNativeObject, crop.left, crop.top, crop.right, crop.bottom);
        } else {
            nativeSetWindowCrop(this.mNativeObject, 0, 0, 0, 0);
        }
    }

    public void setLayerStack(int layerStack) {
        checkNotReleased();
        nativeSetLayerStack(this.mNativeObject, layerStack);
    }

    /* loaded from: SurfaceControl$PhysicalDisplayInfo.class */
    public static final class PhysicalDisplayInfo {
        public int width;
        public int height;
        public float refreshRate;
        public float density;
        public float xDpi;
        public float yDpi;
        public boolean secure;

        public PhysicalDisplayInfo() {
        }

        public PhysicalDisplayInfo(PhysicalDisplayInfo other) {
            copyFrom(other);
        }

        public boolean equals(Object o) {
            return (o instanceof PhysicalDisplayInfo) && equals((PhysicalDisplayInfo) o);
        }

        public boolean equals(PhysicalDisplayInfo other) {
            return other != null && this.width == other.width && this.height == other.height && this.refreshRate == other.refreshRate && this.density == other.density && this.xDpi == other.xDpi && this.yDpi == other.yDpi && this.secure == other.secure;
        }

        public int hashCode() {
            return 0;
        }

        public void copyFrom(PhysicalDisplayInfo other) {
            this.width = other.width;
            this.height = other.height;
            this.refreshRate = other.refreshRate;
            this.density = other.density;
            this.xDpi = other.xDpi;
            this.yDpi = other.yDpi;
            this.secure = other.secure;
        }

        public String toString() {
            return "PhysicalDisplayInfo{" + this.width + " x " + this.height + ", " + this.refreshRate + " fps, density " + this.density + ", " + this.xDpi + " x " + this.yDpi + " dpi, secure " + this.secure + "}";
        }
    }

    public static void unblankDisplay(IBinder displayToken) {
        if (displayToken == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        nativeUnblankDisplay(displayToken);
    }

    public static void blankDisplay(IBinder displayToken) {
        if (displayToken == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        nativeBlankDisplay(displayToken);
    }

    public static boolean getDisplayInfo(IBinder displayToken, PhysicalDisplayInfo outInfo) {
        if (displayToken == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        if (outInfo == null) {
            throw new IllegalArgumentException("outInfo must not be null");
        }
        return nativeGetDisplayInfo(displayToken, outInfo);
    }

    public static void setDisplayProjection(IBinder displayToken, int orientation, Rect layerStackRect, Rect displayRect) {
        if (displayToken == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        if (layerStackRect == null) {
            throw new IllegalArgumentException("layerStackRect must not be null");
        }
        if (displayRect == null) {
            throw new IllegalArgumentException("displayRect must not be null");
        }
        nativeSetDisplayProjection(displayToken, orientation, layerStackRect.left, layerStackRect.top, layerStackRect.right, layerStackRect.bottom, displayRect.left, displayRect.top, displayRect.right, displayRect.bottom);
    }

    public static void setDisplayLayerStack(IBinder displayToken, int layerStack) {
        if (displayToken == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        nativeSetDisplayLayerStack(displayToken, layerStack);
    }

    public static void setDisplaySurface(IBinder displayToken, Surface surface) {
        if (displayToken == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        if (surface != null) {
            synchronized (surface.mLock) {
                nativeSetDisplaySurface(displayToken, surface.mNativeObject);
            }
            return;
        }
        nativeSetDisplaySurface(displayToken, 0);
    }

    public static IBinder createDisplay(String name, boolean secure) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        return nativeCreateDisplay(name, secure);
    }

    public static void destroyDisplay(IBinder displayToken) {
        if (displayToken == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        nativeDestroyDisplay(displayToken);
    }

    public static IBinder getBuiltInDisplay(int builtInDisplayId) {
        return nativeGetBuiltInDisplay(builtInDisplayId);
    }

    public static void screenshot(IBinder display, Surface consumer, int width, int height, int minLayer, int maxLayer) {
        screenshot(display, consumer, width, height, minLayer, maxLayer, false);
    }

    public static void screenshot(IBinder display, Surface consumer, int width, int height) {
        screenshot(display, consumer, width, height, 0, 0, true);
    }

    public static void screenshot(IBinder display, Surface consumer) {
        screenshot(display, consumer, 0, 0, 0, 0, true);
    }

    public static Bitmap screenshot(int width, int height, int minLayer, int maxLayer) {
        IBinder displayToken = getBuiltInDisplay(0);
        return nativeScreenshot(displayToken, width, height, minLayer, maxLayer, false);
    }

    public static Bitmap screenshot(int width, int height) {
        IBinder displayToken = getBuiltInDisplay(0);
        return nativeScreenshot(displayToken, width, height, 0, 0, true);
    }

    private static void screenshot(IBinder display, Surface consumer, int width, int height, int minLayer, int maxLayer, boolean allLayers) {
        if (display == null) {
            throw new IllegalArgumentException("displayToken must not be null");
        }
        if (consumer == null) {
            throw new IllegalArgumentException("consumer must not be null");
        }
        nativeScreenshot(display, consumer, width, height, minLayer, maxLayer, allLayers);
    }

    private static void checkHeadless() {
        if (HEADLESS) {
            throw new UnsupportedOperationException("Device is headless");
        }
    }
}