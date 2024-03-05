package com.android.server.am;

import android.os.IBinder;

/* loaded from: ActivityManagerService$11.class */
class ActivityManagerService$11 implements IBinder.DeathRecipient {
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$11(ActivityManagerService activityManagerService) {
        this.this$0 = activityManagerService;
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        synchronized (this) {
            notifyAll();
        }
    }
}