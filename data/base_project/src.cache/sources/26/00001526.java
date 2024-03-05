package android.view;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.util.SparseIntArray;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

/* loaded from: InputEventReceiver.class */
public abstract class InputEventReceiver {
    private static final String TAG = "InputEventReceiver";
    private int mReceiverPtr;
    private InputChannel mInputChannel;
    private MessageQueue mMessageQueue;
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private final SparseIntArray mSeqMap = new SparseIntArray();

    /* loaded from: InputEventReceiver$Factory.class */
    public interface Factory {
        InputEventReceiver createInputEventReceiver(InputChannel inputChannel, Looper looper);
    }

    private static native int nativeInit(WeakReference<InputEventReceiver> weakReference, InputChannel inputChannel, MessageQueue messageQueue);

    private static native void nativeDispose(int i);

    private static native void nativeFinishInputEvent(int i, int i2, boolean z);

    private static native void nativeConsumeBatchedInputEvents(int i, long j);

    public InputEventReceiver(InputChannel inputChannel, Looper looper) {
        if (inputChannel == null) {
            throw new IllegalArgumentException("inputChannel must not be null");
        }
        if (looper == null) {
            throw new IllegalArgumentException("looper must not be null");
        }
        this.mInputChannel = inputChannel;
        this.mMessageQueue = looper.getQueue();
        this.mReceiverPtr = nativeInit(new WeakReference(this), inputChannel, this.mMessageQueue);
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
        this.mInputChannel = null;
        this.mMessageQueue = null;
    }

    public void onInputEvent(InputEvent event) {
        finishInputEvent(event, false);
    }

    public void onBatchedInputEventPending() {
        consumeBatchedInputEvents(-1L);
    }

    public final void finishInputEvent(InputEvent event, boolean handled) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (this.mReceiverPtr == 0) {
            Log.w(TAG, "Attempted to finish an input event but the input event receiver has already been disposed.");
        } else {
            int index = this.mSeqMap.indexOfKey(event.getSequenceNumber());
            if (index < 0) {
                Log.w(TAG, "Attempted to finish an input event that is not in progress.");
            } else {
                int seq = this.mSeqMap.valueAt(index);
                this.mSeqMap.removeAt(index);
                nativeFinishInputEvent(this.mReceiverPtr, seq, handled);
            }
        }
        event.recycleIfNeededAfterDispatch();
    }

    public final void consumeBatchedInputEvents(long frameTimeNanos) {
        if (this.mReceiverPtr == 0) {
            Log.w(TAG, "Attempted to consume batched input events but the input event receiver has already been disposed.");
        } else {
            nativeConsumeBatchedInputEvents(this.mReceiverPtr, frameTimeNanos);
        }
    }

    private void dispatchInputEvent(int seq, InputEvent event) {
        this.mSeqMap.put(event.getSequenceNumber(), seq);
        onInputEvent(event);
    }

    private void dispatchBatchedInputEventPending() {
        onBatchedInputEventPending();
    }
}