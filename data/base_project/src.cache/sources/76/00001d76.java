package com.android.server.am;

/* loaded from: ActivityManagerService$20.class */
class ActivityManagerService$20 implements Runnable {
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$20(ActivityManagerService activityManagerService) {
        this.this$0 = activityManagerService;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.this$0) {
            this.this$0.mProcessStats.writeStateAsyncLocked();
        }
    }
}