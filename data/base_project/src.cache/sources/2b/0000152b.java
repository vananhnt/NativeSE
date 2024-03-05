package android.view;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Pools;
import android.util.SparseArray;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

/* loaded from: InputQueue.class */
public final class InputQueue {
    private final SparseArray<ActiveInputEvent> mActiveEventArray = new SparseArray<>(20);
    private final Pools.Pool<ActiveInputEvent> mActiveInputEventPool = new Pools.SimplePool(20);
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private int mPtr = nativeInit(new WeakReference(this), Looper.myQueue());

    /* loaded from: InputQueue$Callback.class */
    public interface Callback {
        void onInputQueueCreated(InputQueue inputQueue);

        void onInputQueueDestroyed(InputQueue inputQueue);
    }

    /* loaded from: InputQueue$FinishedInputEventCallback.class */
    public interface FinishedInputEventCallback {
        void onFinishedInputEvent(Object obj, boolean z);
    }

    private static native int nativeInit(WeakReference<InputQueue> weakReference, MessageQueue messageQueue);

    private static native int nativeSendKeyEvent(int i, KeyEvent keyEvent, boolean z);

    private static native int nativeSendMotionEvent(int i, MotionEvent motionEvent);

    private static native void nativeDispose(int i);

    public InputQueue() {
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

    public void dispose(boolean finalized) {
        if (this.mCloseGuard != null) {
            if (finalized) {
                this.mCloseGuard.warnIfOpen();
            }
            this.mCloseGuard.close();
        }
        if (this.mPtr != 0) {
            nativeDispose(this.mPtr);
            this.mPtr = 0;
        }
    }

    public int getNativePtr() {
        return this.mPtr;
    }

    public void sendInputEvent(InputEvent e, Object token, boolean predispatch, FinishedInputEventCallback callback) {
        int id;
        ActiveInputEvent event = obtainActiveInputEvent(token, callback);
        if (e instanceof KeyEvent) {
            id = nativeSendKeyEvent(this.mPtr, (KeyEvent) e, predispatch);
        } else {
            id = nativeSendMotionEvent(this.mPtr, (MotionEvent) e);
        }
        this.mActiveEventArray.put(id, event);
    }

    private void finishInputEvent(int id, boolean handled) {
        int index = this.mActiveEventArray.indexOfKey(id);
        if (index >= 0) {
            ActiveInputEvent e = this.mActiveEventArray.valueAt(index);
            this.mActiveEventArray.removeAt(index);
            e.mCallback.onFinishedInputEvent(e.mToken, handled);
            recycleActiveInputEvent(e);
        }
    }

    private ActiveInputEvent obtainActiveInputEvent(Object token, FinishedInputEventCallback callback) {
        ActiveInputEvent e = this.mActiveInputEventPool.acquire();
        if (e == null) {
            e = new ActiveInputEvent();
        }
        e.mToken = token;
        e.mCallback = callback;
        return e;
    }

    private void recycleActiveInputEvent(ActiveInputEvent e) {
        e.recycle();
        this.mActiveInputEventPool.release(e);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputQueue$ActiveInputEvent.class */
    public final class ActiveInputEvent {
        public Object mToken;
        public FinishedInputEventCallback mCallback;

        private ActiveInputEvent() {
        }

        public void recycle() {
            this.mToken = null;
            this.mCallback = null;
        }
    }
}