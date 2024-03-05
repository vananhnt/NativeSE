package android.webkit;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WebSyncManager.class */
public abstract class WebSyncManager implements Runnable {
    private static final int SYNC_MESSAGE = 101;
    private static int SYNC_NOW_INTERVAL = 100;
    private static int SYNC_LATER_INTERVAL = 300000;
    private Thread mSyncThread;
    private String mThreadName;
    protected Handler mHandler;
    protected WebViewDatabase mDataBase;
    private int mStartSyncRefCount;
    protected static final String LOGTAG = "websync";

    abstract void syncFromRamToFlash();

    /* loaded from: WebSyncManager$SyncHandler.class */
    private class SyncHandler extends Handler {
        private SyncHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 101) {
                WebSyncManager.this.syncFromRamToFlash();
                Message newmsg = obtainMessage(101);
                sendMessageDelayed(newmsg, WebSyncManager.SYNC_LATER_INTERVAL);
            }
        }
    }

    protected WebSyncManager(Context context, String name) {
        this(name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebSyncManager(String name) {
        this.mThreadName = name;
        this.mSyncThread = new Thread(this);
        this.mSyncThread.setName(this.mThreadName);
        this.mSyncThread.start();
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("doesn't implement Cloneable");
    }

    @Override // java.lang.Runnable
    public void run() {
        Looper.prepare();
        this.mHandler = new SyncHandler();
        onSyncInit();
        Process.setThreadPriority(10);
        Message msg = this.mHandler.obtainMessage(101);
        this.mHandler.sendMessageDelayed(msg, SYNC_LATER_INTERVAL);
        Looper.loop();
    }

    public void sync() {
        if (this.mHandler == null) {
            return;
        }
        this.mHandler.removeMessages(101);
        Message msg = this.mHandler.obtainMessage(101);
        this.mHandler.sendMessageDelayed(msg, SYNC_NOW_INTERVAL);
    }

    public void resetSync() {
        if (this.mHandler == null) {
            return;
        }
        this.mHandler.removeMessages(101);
        Message msg = this.mHandler.obtainMessage(101);
        this.mHandler.sendMessageDelayed(msg, SYNC_LATER_INTERVAL);
    }

    public void startSync() {
        if (this.mHandler == null) {
            return;
        }
        int i = this.mStartSyncRefCount + 1;
        this.mStartSyncRefCount = i;
        if (i == 1) {
            Message msg = this.mHandler.obtainMessage(101);
            this.mHandler.sendMessageDelayed(msg, SYNC_LATER_INTERVAL);
        }
    }

    public void stopSync() {
        if (this.mHandler == null) {
            return;
        }
        int i = this.mStartSyncRefCount - 1;
        this.mStartSyncRefCount = i;
        if (i == 0) {
            this.mHandler.removeMessages(101);
        }
    }

    protected void onSyncInit() {
    }
}