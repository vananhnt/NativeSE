package com.android.server.am;

import android.app.IApplicationThread;
import android.os.IBinder;

/* loaded from: ActivityManagerService$AppDeathRecipient.class */
final class ActivityManagerService$AppDeathRecipient implements IBinder.DeathRecipient {
    final ProcessRecord mApp;
    final int mPid;
    final IApplicationThread mAppThread;
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$AppDeathRecipient(ActivityManagerService activityManagerService, ProcessRecord app, int pid, IApplicationThread thread) {
        this.this$0 = activityManagerService;
        this.mApp = app;
        this.mPid = pid;
        this.mAppThread = thread;
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        synchronized (this.this$0) {
            this.this$0.appDiedLocked(this.mApp, this.mPid, this.mAppThread);
        }
    }
}