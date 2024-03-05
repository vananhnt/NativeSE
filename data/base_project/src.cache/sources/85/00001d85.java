package com.android.server.am;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.StrictMode;
import android.util.Slog;
import com.android.server.Watchdog;

/* loaded from: ActivityManagerService$AThread.class */
class ActivityManagerService$AThread extends Thread {
    ActivityManagerService mService;
    Looper mLooper;
    boolean mReady;

    public ActivityManagerService$AThread() {
        super("ActivityManager");
        this.mReady = false;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Looper.prepare();
        Process.setThreadPriority(-2);
        Process.setCanSelfBackground(false);
        ActivityManagerService m = new ActivityManagerService((ActivityManagerService$1) null);
        synchronized (this) {
            this.mService = m;
            this.mLooper = Looper.myLooper();
            Watchdog.getInstance().addThread(new Handler(this.mLooper), getName());
            notifyAll();
        }
        synchronized (this) {
            while (!this.mReady) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        if (StrictMode.conditionallyEnableDebugLogging()) {
            Slog.i("ActivityManager", "Enabled StrictMode logging for AThread's Looper");
        }
        Looper.loop();
    }
}