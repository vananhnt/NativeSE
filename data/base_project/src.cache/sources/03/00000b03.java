package android.os;

import android.os.IMessenger;
import android.util.Log;
import android.util.Printer;

/* loaded from: Handler.class */
public class Handler {
    private static final boolean FIND_POTENTIAL_LEAKS = false;
    private static final String TAG = "Handler";
    final MessageQueue mQueue;
    final Looper mLooper;
    final Callback mCallback;
    final boolean mAsynchronous;
    IMessenger mMessenger;

    /* loaded from: Handler$Callback.class */
    public interface Callback {
        boolean handleMessage(Message message);
    }

    public void handleMessage(Message msg) {
    }

    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else if (this.mCallback != null && this.mCallback.handleMessage(msg)) {
        } else {
            handleMessage(msg);
        }
    }

    public Handler() {
        this((Callback) null, false);
    }

    public Handler(Callback callback) {
        this(callback, false);
    }

    public Handler(Looper looper) {
        this(looper, null, false);
    }

    public Handler(Looper looper, Callback callback) {
        this(looper, callback, false);
    }

    public Handler(boolean async) {
        this((Callback) null, async);
    }

    public Handler(Callback callback, boolean async) {
        this.mLooper = Looper.myLooper();
        if (this.mLooper == null) {
            throw new RuntimeException("Can't create handler inside thread that has not called Looper.prepare()");
        }
        this.mQueue = this.mLooper.mQueue;
        this.mCallback = callback;
        this.mAsynchronous = async;
    }

    public Handler(Looper looper, Callback callback, boolean async) {
        this.mLooper = looper;
        this.mQueue = looper.mQueue;
        this.mCallback = callback;
        this.mAsynchronous = async;
    }

    public String getMessageName(Message message) {
        if (message.callback != null) {
            return message.callback.getClass().getName();
        }
        return "0x" + Integer.toHexString(message.what);
    }

    public final Message obtainMessage() {
        return Message.obtain(this);
    }

    public final Message obtainMessage(int what) {
        return Message.obtain(this, what);
    }

    public final Message obtainMessage(int what, Object obj) {
        return Message.obtain(this, what, obj);
    }

    public final Message obtainMessage(int what, int arg1, int arg2) {
        return Message.obtain(this, what, arg1, arg2);
    }

    public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return Message.obtain(this, what, arg1, arg2, obj);
    }

    public final boolean post(Runnable r) {
        return sendMessageDelayed(getPostMessage(r), 0L);
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r), uptimeMillis);
    }

    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r, token), uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r), delayMillis);
    }

    public final boolean postAtFrontOfQueue(Runnable r) {
        return sendMessageAtFrontOfQueue(getPostMessage(r));
    }

    public final boolean runWithScissors(Runnable r, long timeout) {
        if (r == null) {
            throw new IllegalArgumentException("runnable must not be null");
        }
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout must be non-negative");
        }
        if (Looper.myLooper() == this.mLooper) {
            r.run();
            return true;
        }
        BlockingRunnable br = new BlockingRunnable(r);
        return br.postAndWait(this, timeout);
    }

    public final void removeCallbacks(Runnable r) {
        this.mQueue.removeMessages(this, r, (Object) null);
    }

    public final void removeCallbacks(Runnable r, Object token) {
        this.mQueue.removeMessages(this, r, token);
    }

    public final boolean sendMessage(Message msg) {
        return sendMessageDelayed(msg, 0L);
    }

    public final boolean sendEmptyMessage(int what) {
        return sendEmptyMessageDelayed(what, 0L);
    }

    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageDelayed(msg, delayMillis);
    }

    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageAtTime(msg, uptimeMillis);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        MessageQueue queue = this.mQueue;
        if (queue == null) {
            RuntimeException e = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
            Log.w("Looper", e.getMessage(), e);
            return false;
        }
        return enqueueMessage(queue, msg, uptimeMillis);
    }

    public final boolean sendMessageAtFrontOfQueue(Message msg) {
        MessageQueue queue = this.mQueue;
        if (queue == null) {
            RuntimeException e = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
            Log.w("Looper", e.getMessage(), e);
            return false;
        }
        return enqueueMessage(queue, msg, 0L);
    }

    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        if (this.mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }

    public final void removeMessages(int what) {
        this.mQueue.removeMessages(this, what, (Object) null);
    }

    public final void removeMessages(int what, Object object) {
        this.mQueue.removeMessages(this, what, object);
    }

    public final void removeCallbacksAndMessages(Object token) {
        this.mQueue.removeCallbacksAndMessages(this, token);
    }

    public final boolean hasMessages(int what) {
        return this.mQueue.hasMessages(this, what, (Object) null);
    }

    public final boolean hasMessages(int what, Object object) {
        return this.mQueue.hasMessages(this, what, object);
    }

    public final boolean hasCallbacks(Runnable r) {
        return this.mQueue.hasMessages(this, r, (Object) null);
    }

    public final Looper getLooper() {
        return this.mLooper;
    }

    public final void dump(Printer pw, String prefix) {
        pw.println(prefix + this + " @ " + SystemClock.uptimeMillis());
        if (this.mLooper == null) {
            pw.println(prefix + "looper uninitialized");
        } else {
            this.mLooper.dump(pw, prefix + "  ");
        }
    }

    public String toString() {
        return "Handler (" + getClass().getName() + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final IMessenger getIMessenger() {
        synchronized (this.mQueue) {
            if (this.mMessenger != null) {
                return this.mMessenger;
            }
            this.mMessenger = new MessengerImpl();
            return this.mMessenger;
        }
    }

    /* loaded from: Handler$MessengerImpl.class */
    private final class MessengerImpl extends IMessenger.Stub {
        private MessengerImpl() {
        }

        @Override // android.os.IMessenger
        public void send(Message msg) {
            Handler.this.sendMessage(msg);
        }
    }

    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        m.callback = r;
        return m;
    }

    private static Message getPostMessage(Runnable r, Object token) {
        Message m = Message.obtain();
        m.obj = token;
        m.callback = r;
        return m;
    }

    private static void handleCallback(Message message) {
        message.callback.run();
    }

    /* loaded from: Handler$BlockingRunnable.class */
    private static final class BlockingRunnable implements Runnable {
        private final Runnable mTask;
        private boolean mDone;

        public BlockingRunnable(Runnable task) {
            this.mTask = task;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.mTask.run();
                synchronized (this) {
                    this.mDone = true;
                    notifyAll();
                }
            } catch (Throwable th) {
                synchronized (this) {
                    this.mDone = true;
                    notifyAll();
                    throw th;
                }
            }
        }

        public boolean postAndWait(Handler handler, long timeout) {
            if (!handler.post(this)) {
                return false;
            }
            synchronized (this) {
                if (timeout > 0) {
                    long expirationTime = SystemClock.uptimeMillis() + timeout;
                    while (!this.mDone) {
                        long delay = expirationTime - SystemClock.uptimeMillis();
                        if (delay <= 0) {
                            return false;
                        }
                        try {
                            wait(delay);
                        } catch (InterruptedException e) {
                        }
                    }
                } else {
                    while (!this.mDone) {
                        try {
                            wait();
                        } catch (InterruptedException e2) {
                        }
                    }
                }
                return true;
            }
        }
    }
}