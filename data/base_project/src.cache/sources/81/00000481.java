package android.filterfw.core;

/* compiled from: GLFrame.java */
/* loaded from: GLFrameTimer.class */
class GLFrameTimer {
    private static StopWatchMap mTimer = null;

    GLFrameTimer() {
    }

    public static StopWatchMap get() {
        if (mTimer == null) {
            mTimer = new StopWatchMap();
        }
        return mTimer;
    }
}