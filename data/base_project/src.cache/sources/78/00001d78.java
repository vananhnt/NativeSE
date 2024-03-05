package com.android.server.am;

import android.content.IIntentReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;

/* loaded from: ActivityManagerService$22.class */
class ActivityManagerService$22 extends IIntentReceiver.Stub {
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$22(ActivityManagerService activityManagerService) {
        this.this$0 = activityManagerService;
    }

    @Override // android.content.IIntentReceiver
    public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) throws RemoteException {
    }
}