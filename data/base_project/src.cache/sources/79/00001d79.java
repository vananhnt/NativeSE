package com.android.server.am;

import android.os.Bundle;
import android.os.IRemoteCallback;
import android.os.RemoteException;

/* loaded from: ActivityManagerService$23.class */
class ActivityManagerService$23 extends IRemoteCallback.Stub {
    int mCount = 0;
    final /* synthetic */ int val$N;
    final /* synthetic */ UserStartedState val$uss;
    final /* synthetic */ int val$oldUserId;
    final /* synthetic */ int val$newUserId;
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$23(ActivityManagerService activityManagerService, int i, UserStartedState userStartedState, int i2, int i3) {
        this.this$0 = activityManagerService;
        this.val$N = i;
        this.val$uss = userStartedState;
        this.val$oldUserId = i2;
        this.val$newUserId = i3;
    }

    @Override // android.os.IRemoteCallback
    public void sendResult(Bundle data) throws RemoteException {
        synchronized (this.this$0) {
            if (this.this$0.mCurUserSwitchCallback == this) {
                this.mCount++;
                if (this.mCount == this.val$N) {
                    this.this$0.sendContinueUserSwitchLocked(this.val$uss, this.val$oldUserId, this.val$newUserId);
                }
            }
        }
    }
}