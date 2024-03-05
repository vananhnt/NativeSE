package com.android.server.am;

import android.content.IIntentReceiver;
import android.content.Intent;
import android.os.Bundle;

/* loaded from: ActivityManagerService$26.class */
class ActivityManagerService$26 extends IIntentReceiver.Stub {
    final /* synthetic */ UserStartedState val$uss;
    final /* synthetic */ Intent val$shutdownIntent;
    final /* synthetic */ IIntentReceiver val$shutdownReceiver;
    final /* synthetic */ int val$userId;
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$26(ActivityManagerService activityManagerService, UserStartedState userStartedState, Intent intent, IIntentReceiver iIntentReceiver, int i) {
        this.this$0 = activityManagerService;
        this.val$uss = userStartedState;
        this.val$shutdownIntent = intent;
        this.val$shutdownReceiver = iIntentReceiver;
        this.val$userId = i;
    }

    @Override // android.content.IIntentReceiver
    public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
        synchronized (this.this$0) {
            if (this.val$uss.mState != 2) {
                return;
            }
            this.val$uss.mState = 3;
            ActivityManagerService.access$100(this.this$0, (ProcessRecord) null, (String) null, this.val$shutdownIntent, (String) null, this.val$shutdownReceiver, 0, (String) null, (Bundle) null, (String) null, -1, true, false, ActivityManagerService.MY_PID, 1000, this.val$userId);
        }
    }
}