package com.android.server.am;

import android.content.IIntentReceiver;
import android.content.Intent;
import android.os.Bundle;

/* loaded from: ActivityManagerService$21.class */
class ActivityManagerService$21 extends IIntentReceiver.Stub {
    final /* synthetic */ UserStartedState val$uss;
    final /* synthetic */ int val$userId;
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$21(ActivityManagerService activityManagerService, UserStartedState userStartedState, int i) {
        this.this$0 = activityManagerService;
        this.val$uss = userStartedState;
        this.val$userId = i;
    }

    @Override // android.content.IIntentReceiver
    public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
        this.this$0.userInitialized(this.val$uss, this.val$userId);
    }
}