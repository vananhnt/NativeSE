package com.android.server.am;

import android.content.IIntentReceiver;
import android.content.Intent;
import android.os.Bundle;
import com.android.internal.R;
import java.util.ArrayList;

/* loaded from: ActivityManagerService$13.class */
class ActivityManagerService$13 extends IIntentReceiver.Stub {
    final /* synthetic */ ArrayList val$doneReceivers;
    final /* synthetic */ Runnable val$goingCallback;
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$13(ActivityManagerService activityManagerService, ArrayList arrayList, Runnable runnable) {
        this.this$0 = activityManagerService;
        this.val$doneReceivers = arrayList;
        this.val$goingCallback = runnable;
    }

    @Override // android.content.IIntentReceiver
    public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
        this.this$0.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityManagerService$13.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (ActivityManagerService$13.this.this$0) {
                    ActivityManagerService$13.this.this$0.mDidUpdate = true;
                }
                ActivityManagerService.access$800(ActivityManagerService$13.this.val$doneReceivers);
                ActivityManagerService$13.this.this$0.showBootMessage(ActivityManagerService$13.this.this$0.mContext.getText(R.string.android_upgrading_complete), false);
                ActivityManagerService$13.this.this$0.systemReady(ActivityManagerService$13.this.val$goingCallback);
            }
        });
    }
}