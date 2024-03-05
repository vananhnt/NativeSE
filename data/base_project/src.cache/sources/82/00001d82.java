package com.android.server.am;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* loaded from: ActivityManagerService$7.class */
class ActivityManagerService$7 extends BroadcastReceiver {
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$7(ActivityManagerService activityManagerService) {
        this.this$0 = activityManagerService;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String[] pkgs = intent.getStringArrayExtra(Intent.EXTRA_PACKAGES);
        if (pkgs != null) {
            for (String pkg : pkgs) {
                synchronized (this.this$0) {
                    if (ActivityManagerService.access$300(this.this$0, pkg, -1, false, false, false, false, 0, "finished booting")) {
                        setResultCode(-1);
                        return;
                    }
                }
            }
        }
    }
}