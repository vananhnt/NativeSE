package android.view;

/* loaded from: SurfaceSession.class */
public final class SurfaceSession {
    private int mNativeClient = nativeCreate();

    private static native int nativeCreate();

    private static native void nativeDestroy(int i);

    private static native void nativeKill(int i);

    protected void finalize() throws Throwable {
        try {
            if (this.mNativeClient != 0) {
                nativeDestroy(this.mNativeClient);
            }
        } finally {
            super.finalize();
        }
    }

    public void kill() {
        nativeKill(this.mNativeClient);
    }
}