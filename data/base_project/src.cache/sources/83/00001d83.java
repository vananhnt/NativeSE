package com.android.server.am;

import android.content.IIntentReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

/* loaded from: ActivityManagerService$8.class */
class ActivityManagerService$8 extends IIntentReceiver.Stub {
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$8(ActivityManagerService activityManagerService) {
        this.this$0 = activityManagerService;
    }

    @Override // android.content.IIntentReceiver
    public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
        synchronized (this.this$0) {
            this.this$0.requestPssAllProcsLocked(SystemClock.uptimeMillis(), true, false);
        }
    }
}