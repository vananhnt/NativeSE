package android.os;

import android.util.Log;
import android.util.PrefixPrinter;
import android.util.Printer;
import gov.nist.core.Separators;

/* loaded from: Looper.class */
public final class Looper {
    private static final String TAG = "Looper";
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
    private static Looper sMainLooper;
    final MessageQueue mQueue;
    private Printer mLogging;
    volatile boolean mRun = true;
    final Thread mThread = Thread.currentThread();

    public static void prepare() {
        prepare(true);
    }

    private static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper(quitAllowed));
    }

    public static void prepareMainLooper() {
        prepare(false);
        synchronized (Looper.class) {
            if (sMainLooper != null) {
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            sMainLooper = myLooper();
        }
    }

    public static Looper getMainLooper() {
        Looper looper;
        synchronized (Looper.class) {
            looper = sMainLooper;
        }
        return looper;
    }

    public static void loop() {
        Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        MessageQueue queue = me.mQueue;
        Binder.clearCallingIdentity();
        long ident = Binder.clearCallingIdentity();
        while (true) {
            Message msg = queue.next();
            if (msg == null) {
                return;
            }
            Printer logging = me.mLogging;
            if (logging != null) {
                logging.println(">>>>> Dispatching to " + msg.target + Separators.SP + msg.callback + ": " + msg.what);
            }
            msg.target.dispatchMessage(msg);
            if (logging != null) {
                logging.println("<<<<< Finished to " + msg.target + Separators.SP + msg.callback);
            }
            long newIdent = Binder.clearCallingIdentity();
            if (ident != newIdent) {
                Log.wtf(TAG, "Thread identity changed from 0x" + Long.toHexString(ident) + " to 0x" + Long.toHexString(newIdent) + " while dispatching to " + msg.target.getClass().getName() + Separators.SP + msg.callback + " what=" + msg.what);
            }
            msg.recycle();
        }
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public void setMessageLogging(Printer printer) {
        this.mLogging = printer;
    }

    public static MessageQueue myQueue() {
        return myLooper().mQueue;
    }

    private Looper(boolean quitAllowed) {
        this.mQueue = new MessageQueue(quitAllowed);
    }

    public boolean isCurrentThread() {
        return Thread.currentThread() == this.mThread;
    }

    public void quit() {
        this.mQueue.quit(false);
    }

    public void quitSafely() {
        this.mQueue.quit(true);
    }

    public int postSyncBarrier() {
        return this.mQueue.enqueueSyncBarrier(SystemClock.uptimeMillis());
    }

    public void removeSyncBarrier(int token) {
        this.mQueue.removeSyncBarrier(token);
    }

    public Thread getThread() {
        return this.mThread;
    }

    public MessageQueue getQueue() {
        return this.mQueue;
    }

    public boolean isIdling() {
        return this.mQueue.isIdling();
    }

    public void dump(Printer pw, String prefix) {
        Printer pw2 = PrefixPrinter.create(pw, prefix);
        pw2.println(toString());
        pw2.println("mRun=" + this.mRun);
        pw2.println("mThread=" + this.mThread);
        pw2.println("mQueue=" + (this.mQueue != null ? this.mQueue : "(null"));
        if (this.mQueue != null) {
            synchronized (this.mQueue) {
                long now = SystemClock.uptimeMillis();
                int n = 0;
                for (Message msg = this.mQueue.mMessages; msg != null; msg = msg.next) {
                    pw2.println("  Message " + n + ": " + msg.toString(now));
                    n++;
                }
                pw2.println("(Total messages: " + n + Separators.RPAREN);
            }
        }
    }

    public String toString() {
        return "Looper{" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }
}