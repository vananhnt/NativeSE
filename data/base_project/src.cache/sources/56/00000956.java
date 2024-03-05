package android.net.http;

import android.app.backup.FullBackup;
import android.content.Context;
import android.net.http.RequestQueue;
import android.os.Process;
import android.os.SystemClock;
import gov.nist.core.Separators;

/* loaded from: ConnectionThread.class */
class ConnectionThread extends Thread {
    static final int WAIT_TIMEOUT = 5000;
    static final int WAIT_TICK = 1000;
    long mCurrentThreadTime;
    long mTotalThreadTime;
    private boolean mWaiting;
    private volatile boolean mRunning = true;
    private Context mContext;
    private RequestQueue.ConnectionManager mConnectionManager;
    private RequestFeeder mRequestFeeder;
    private int mId;
    Connection mConnection;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionThread(Context context, int id, RequestQueue.ConnectionManager connectionManager, RequestFeeder requestFeeder) {
        this.mContext = context;
        setName("http" + id);
        this.mId = id;
        this.mConnectionManager = connectionManager;
        this.mRequestFeeder = requestFeeder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestStop() {
        synchronized (this.mRequestFeeder) {
            this.mRunning = false;
            this.mRequestFeeder.notify();
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Process.setThreadPriority(1);
        this.mCurrentThreadTime = 0L;
        this.mTotalThreadTime = 0L;
        while (this.mRunning) {
            if (this.mCurrentThreadTime == -1) {
                this.mCurrentThreadTime = SystemClock.currentThreadTimeMillis();
            }
            Request request = this.mRequestFeeder.getRequest();
            if (request == null) {
                synchronized (this.mRequestFeeder) {
                    this.mWaiting = true;
                    try {
                        this.mRequestFeeder.wait();
                    } catch (InterruptedException e) {
                    }
                    this.mWaiting = false;
                    if (this.mCurrentThreadTime != 0) {
                        this.mCurrentThreadTime = SystemClock.currentThreadTimeMillis();
                    }
                }
            } else {
                this.mConnection = this.mConnectionManager.getConnection(this.mContext, request.mHost);
                this.mConnection.processRequests(request);
                if (this.mConnection.getCanPersist()) {
                    if (!this.mConnectionManager.recycleConnection(this.mConnection)) {
                        this.mConnection.closeConnection();
                    }
                } else {
                    this.mConnection.closeConnection();
                }
                this.mConnection = null;
                if (this.mCurrentThreadTime > 0) {
                    long start = this.mCurrentThreadTime;
                    this.mCurrentThreadTime = SystemClock.currentThreadTimeMillis();
                    this.mTotalThreadTime += this.mCurrentThreadTime - start;
                }
            }
        }
    }

    @Override // java.lang.Thread
    public synchronized String toString() {
        String con = this.mConnection == null ? "" : this.mConnection.toString();
        String active = this.mWaiting ? "w" : FullBackup.APK_TREE_TOKEN;
        return "cid " + this.mId + Separators.SP + active + Separators.SP + con;
    }
}