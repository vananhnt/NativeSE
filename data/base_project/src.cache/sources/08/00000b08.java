package android.os;

/* loaded from: HandlerThread.class */
public class HandlerThread extends Thread {
    int mPriority;
    int mTid;
    Looper mLooper;

    public HandlerThread(String name) {
        super(name);
        this.mTid = -1;
        this.mPriority = 0;
    }

    public HandlerThread(String name, int priority) {
        super(name);
        this.mTid = -1;
        this.mPriority = priority;
    }

    protected void onLooperPrepared() {
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        this.mTid = Process.myTid();
        Looper.prepare();
        synchronized (this) {
            this.mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(this.mPriority);
        onLooperPrepared();
        Looper.loop();
        this.mTid = -1;
    }

    public Looper getLooper() {
        if (!isAlive()) {
            return null;
        }
        synchronized (this) {
            while (isAlive() && this.mLooper == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return this.mLooper;
    }

    public boolean quit() {
        Looper looper = getLooper();
        if (looper != null) {
            looper.quit();
            return true;
        }
        return false;
    }

    public boolean quitSafely() {
        Looper looper = getLooper();
        if (looper != null) {
            looper.quitSafely();
            return true;
        }
        return false;
    }

    public int getThreadId() {
        return this.mTid;
    }
}