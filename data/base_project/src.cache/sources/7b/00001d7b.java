package com.android.server.am;

import android.content.IIntentReceiver;
import android.content.Intent;
import android.os.Bundle;

/* loaded from: ActivityManagerService$25.class */
class ActivityManagerService$25 extends IIntentReceiver.Stub {
    final /* synthetic */ UserStartedState val$uss;
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$25(ActivityManagerService activityManagerService, UserStartedState userStartedState) {
        this.this$0 = activityManagerService;
        this.val$uss = userStartedState;
    }

    @Override // android.content.IIntentReceiver
    public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
        this.this$0.finishUserStop(this.val$uss);
    }
}