package android.view;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import dalvik.system.CloseGuard;

/* loaded from: DisplayEventReceiver.class */
public abstract class DisplayEventReceiver {
    private static final String TAG = "DisplayEventReceiver";
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private int mReceiverPtr;
    private MessageQueue mMessageQueue;

    private static native int nativeInit(DisplayEventReceiver displayEventReceiver, MessageQueue messageQueue);

    private static native void nativeDispose(int i);

    private static native void nativeScheduleVsync(int i);

    public DisplayEventReceiver(Looper looper) {
        if (looper == null) {
            throw new IllegalArgumentException("looper must not be null");
        }
        this.mMessageQueue = looper.getQueue();
        this.mReceiverPtr = nativeInit(this, this.mMessageQueue);
        this.mCloseGuard.open("dispose");
    }

    protected void finalize() throws Throwable {
        try {
            dispose(true);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public void dispose() {
        dispose(false);
    }

    private void dispose(boolean finalized) {
        if (this.mCloseGuard != null) {
            if (finalized) {
                this.mCloseGuard.warnIfOpen();
            }
            this.mCloseGuard.close();
        }
        if (this.mReceiverPtr != 0) {
            nativeDispose(this.mReceiverPtr);
            this.mReceiverPtr = 0;
        }
        this.mMessageQueue = null;
    }

    public void onVsync(long timestampNanos, int builtInDisplayId, int frame) {
    }

    public void onHotplug(long timestampNanos, int builtInDisplayId, boolean connected) {
    }

    public void scheduleVsync() {
        if (this.mReceiverPtr == 0) {
            Log.w(TAG, "Attempted to schedule a vertical sync pulse but the display event receiver has already been disposed.");
        } else {
            nativeScheduleVsync(this.mReceiverPtr);
        }
    }

    private void dispatchVsync(long timestampNanos, int builtInDisplayId, int frame) {
        onVsync(timestampNanos, builtInDisplayId, frame);
    }

    private void dispatchHotplug(long timestampNanos, int builtInDisplayId, boolean connected) {
        onHotplug(timestampNanos, builtInDisplayId, connected);
    }
}