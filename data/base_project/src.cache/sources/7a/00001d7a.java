package com.android.server.am;

import android.app.IStopUserCallback;
import android.os.RemoteException;

/* loaded from: ActivityManagerService$24.class */
class ActivityManagerService$24 implements Runnable {
    final /* synthetic */ IStopUserCallback val$callback;
    final /* synthetic */ int val$userId;
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$24(ActivityManagerService activityManagerService, IStopUserCallback iStopUserCallback, int i) {
        this.this$0 = activityManagerService;
        this.val$callback = iStopUserCallback;
        this.val$userId = i;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.val$callback.userStopped(this.val$userId);
        } catch (RemoteException e) {
        }
    }
}