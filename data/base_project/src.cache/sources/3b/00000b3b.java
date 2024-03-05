package android.os;

import android.util.AndroidRuntimeException;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: MessageQueue.class */
public final class MessageQueue {
    private final boolean mQuitAllowed;
    Message mMessages;
    private IdleHandler[] mPendingIdleHandlers;
    private boolean mQuitting;
    private boolean mBlocked;
    private int mNextBarrierToken;
    private final ArrayList<IdleHandler> mIdleHandlers = new ArrayList<>();
    private int mPtr = nativeInit();

    /* loaded from: MessageQueue$IdleHandler.class */
    public interface IdleHandler {
        boolean queueIdle();
    }

    private static native int nativeInit();

    private static native void nativeDestroy(int i);

    private static native void nativePollOnce(int i, int i2);

    private static native void nativeWake(int i);

    private static native boolean nativeIsIdling(int i);

    public void addIdleHandler(IdleHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Can't add a null IdleHandler");
        }
        synchronized (this) {
            this.mIdleHandlers.add(handler);
        }
    }

    public void removeIdleHandler(IdleHandler handler) {
        synchronized (this) {
            this.mIdleHandlers.remove(handler);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MessageQueue(boolean quitAllowed) {
        this.mQuitAllowed = quitAllowed;
    }

    protected void finalize() throws Throwable {
        try {
            dispose();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    private void dispose() {
        if (this.mPtr != 0) {
            nativeDestroy(this.mPtr);
            this.mPtr = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x010d, code lost:
        r8 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0111, code lost:
        if (r8 >= r6) goto L68;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0114, code lost:
        r0 = r5.mPendingIdleHandlers[r8];
        r5.mPendingIdleHandlers[r8] = null;
        r10 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0126, code lost:
        r10 = r0.queueIdle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x0132, code lost:
        r11 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x0134, code lost:
        android.util.Log.wtf("MessageQueue", "IdleHandler threw exception", r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x0166, code lost:
        r6 = 0;
        r7 = 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.os.Message next() {
        /*
            Method dump skipped, instructions count: 365
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.os.MessageQueue.next():android.os.Message");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void quit(boolean safe) {
        if (!this.mQuitAllowed) {
            throw new RuntimeException("Main thread not allowed to quit.");
        }
        synchronized (this) {
            if (this.mQuitting) {
                return;
            }
            this.mQuitting = true;
            if (safe) {
                removeAllFutureMessagesLocked();
            } else {
                removeAllMessagesLocked();
            }
            nativeWake(this.mPtr);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int enqueueSyncBarrier(long when) {
        int token;
        synchronized (this) {
            token = this.mNextBarrierToken;
            this.mNextBarrierToken = token + 1;
            Message msg = Message.obtain();
            msg.arg1 = token;
            Message prev = null;
            Message p = this.mMessages;
            if (when != 0) {
                while (p != null && p.when <= when) {
                    prev = p;
                    p = p.next;
                }
            }
            if (prev != null) {
                msg.next = p;
                prev.next = msg;
            } else {
                msg.next = p;
                this.mMessages = msg;
            }
        }
        return token;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeSyncBarrier(int token) {
        boolean needWake;
        synchronized (this) {
            Message prev = null;
            Message p = this.mMessages;
            while (p != null && (p.target != null || p.arg1 != token)) {
                prev = p;
                p = p.next;
            }
            if (p == null) {
                throw new IllegalStateException("The specified message queue synchronization  barrier token has not been posted or has already been removed.");
            }
            if (prev != null) {
                prev.next = p.next;
                needWake = false;
            } else {
                this.mMessages = p.next;
                needWake = this.mMessages == null || this.mMessages.target != null;
            }
            p.recycle();
            if (needWake && !this.mQuitting) {
                nativeWake(this.mPtr);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean enqueueMessage(Message msg, long when) {
        boolean needWake;
        Message prev;
        if (msg.isInUse()) {
            throw new AndroidRuntimeException(msg + " This message is already in use.");
        }
        if (msg.target == null) {
            throw new AndroidRuntimeException("Message must have a target.");
        }
        synchronized (this) {
            if (this.mQuitting) {
                RuntimeException e = new RuntimeException(msg.target + " sending message to a Handler on a dead thread");
                Log.w("MessageQueue", e.getMessage(), e);
                return false;
            }
            msg.when = when;
            Message p = this.mMessages;
            if (p == null || when == 0 || when < p.when) {
                msg.next = p;
                this.mMessages = msg;
                needWake = this.mBlocked;
            } else {
                needWake = this.mBlocked && p.target == null && msg.isAsynchronous();
                while (true) {
                    prev = p;
                    p = p.next;
                    if (p == null || when < p.when) {
                        break;
                    } else if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                msg.next = p;
                prev.next = msg;
            }
            if (needWake) {
                nativeWake(this.mPtr);
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasMessages(Handler h, int what, Object object) {
        if (h == null) {
            return false;
        }
        synchronized (this) {
            for (Message p = this.mMessages; p != null; p = p.next) {
                if (p.target == h && p.what == what && (object == null || p.obj == object)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasMessages(Handler h, Runnable r, Object object) {
        if (h == null) {
            return false;
        }
        synchronized (this) {
            for (Message p = this.mMessages; p != null; p = p.next) {
                if (p.target == h && p.callback == r && (object == null || p.obj == object)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isIdling() {
        boolean z;
        synchronized (this) {
            z = !this.mQuitting && nativeIsIdling(this.mPtr);
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeMessages(Handler h, int what, Object object) {
        if (h == null) {
            return;
        }
        synchronized (this) {
            Message p = this.mMessages;
            while (p != null && p.target == h && p.what == what && (object == null || p.obj == object)) {
                Message n = p.next;
                this.mMessages = n;
                p.recycle();
                p = n;
            }
            while (p != null) {
                Message n2 = p.next;
                if (n2 != null && n2.target == h && n2.what == what && (object == null || n2.obj == object)) {
                    Message nn = n2.next;
                    n2.recycle();
                    p.next = nn;
                } else {
                    p = n2;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeMessages(Handler h, Runnable r, Object object) {
        if (h == null || r == null) {
            return;
        }
        synchronized (this) {
            Message p = this.mMessages;
            while (p != null && p.target == h && p.callback == r && (object == null || p.obj == object)) {
                Message n = p.next;
                this.mMessages = n;
                p.recycle();
                p = n;
            }
            while (p != null) {
                Message n2 = p.next;
                if (n2 != null && n2.target == h && n2.callback == r && (object == null || n2.obj == object)) {
                    Message nn = n2.next;
                    n2.recycle();
                    p.next = nn;
                } else {
                    p = n2;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeCallbacksAndMessages(Handler h, Object object) {
        if (h == null) {
            return;
        }
        synchronized (this) {
            Message p = this.mMessages;
            while (p != null && p.target == h && (object == null || p.obj == object)) {
                Message n = p.next;
                this.mMessages = n;
                p.recycle();
                p = n;
            }
            while (p != null) {
                Message n2 = p.next;
                if (n2 != null && n2.target == h && (object == null || n2.obj == object)) {
                    Message nn = n2.next;
                    n2.recycle();
                    p.next = nn;
                } else {
                    p = n2;
                }
            }
        }
    }

    private void removeAllMessagesLocked() {
        Message message = this.mMessages;
        while (true) {
            Message p = message;
            if (p != null) {
                Message n = p.next;
                p.recycle();
                message = n;
            } else {
                this.mMessages = null;
                return;
            }
        }
    }

    private void removeAllFutureMessagesLocked() {
        long now = SystemClock.uptimeMillis();
        Message p = this.mMessages;
        if (p != null) {
            if (p.when > now) {
                removeAllMessagesLocked();
                return;
            }
            while (true) {
                Message n = p.next;
                if (n == null) {
                    return;
                }
                if (n.when <= now) {
                    p = n;
                } else {
                    p.next = null;
                    do {
                        Message p2 = n;
                        n = p2.next;
                        p2.recycle();
                    } while (n != null);
                    return;
                }
            }
        }
    }
}