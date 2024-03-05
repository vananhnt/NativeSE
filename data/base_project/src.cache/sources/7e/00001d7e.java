package com.android.server.am;

import android.app.AlarmManager;
import android.os.SystemClock;
import android.util.Slog;

/* loaded from: ActivityManagerService$4.class */
class ActivityManagerService$4 extends Thread {
    final /* synthetic */ ActivityManagerService this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ActivityManagerService$4(ActivityManagerService activityManagerService, String x0) {
        super(x0);
        this.this$0 = activityManagerService;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (true) {
            try {
                try {
                    synchronized (this) {
                        long now = SystemClock.uptimeMillis();
                        long nextCpuDelay = (this.this$0.mLastCpuTime.get() + 268435455) - now;
                        long nextWriteDelay = (this.this$0.mLastWriteTime + AlarmManager.INTERVAL_HALF_HOUR) - now;
                        if (nextWriteDelay < nextCpuDelay) {
                            nextCpuDelay = nextWriteDelay;
                        }
                        if (nextCpuDelay > 0) {
                            this.this$0.mProcessCpuMutexFree.set(true);
                            wait(nextCpuDelay);
                        }
                    }
                } catch (InterruptedException e) {
                }
                this.this$0.updateCpuStatsNow();
            } catch (Exception e2) {
                Slog.e("ActivityManager", "Unexpected exception collecting process stats", e2);
            }
        }
    }
}