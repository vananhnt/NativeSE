package android.view;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

/* loaded from: InputEventSender.class */
public abstract class InputEventSender {
    private static final String TAG = "InputEventSender";
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private int mSenderPtr;
    private InputChannel mInputChannel;
    private MessageQueue mMessageQueue;

    private static native int nativeInit(WeakReference<InputEventSender> weakReference, InputChannel inputChannel, MessageQueue messageQueue);

    private static native void nativeDispose(int i);

    private static native boolean nativeSendKeyEvent(int i, int i2, KeyEvent keyEvent);

    private static native boolean nativeSendMotionEvent(int i, int i2, MotionEvent motionEvent);

    public InputEventSender(InputChannel inputChannel, Looper looper) {
        if (inputChannel == null) {
            throw new IllegalArgumentException("inputChannel must not be null");
        }
        if (looper == null) {
            throw new IllegalArgumentException("looper must not be null");
        }
        this.mInputChannel = inputChannel;
        this.mMessageQueue = looper.getQueue();
        this.mSenderPtr = nativeInit(new WeakReference(this), inputChannel, this.mMessageQueue);
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
        if (this.mSenderPtr != 0) {
            nativeDispose(this.mSenderPtr);
            this.mSenderPtr = 0;
        }
        this.mInputChannel = null;
        this.mMessageQueue = null;
    }

    public void onInputEventFinished(int seq, boolean handled) {
    }

    public final boolean sendInputEvent(int seq, InputEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (this.mSenderPtr == 0) {
            Log.w(TAG, "Attempted to send an input event but the input event sender has already been disposed.");
            return false;
        } else if (event instanceof KeyEvent) {
            return nativeSendKeyEvent(this.mSenderPtr, seq, (KeyEvent) event);
        } else {
            return nativeSendMotionEvent(this.mSenderPtr, seq, (MotionEvent) event);
        }
    }

    private void dispatchInputEventFinished(int seq, boolean handled) {
        onInputEventFinished(seq, handled);
    }
}