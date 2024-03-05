package com.android.server.am;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

/* loaded from: ActivityManagerService$12.class */
class ActivityManagerService$12 extends BroadcastReceiver {
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$12(ActivityManagerService activityManagerService) {
        this.this$0 = activityManagerService;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.i("ActivityManager", "Shutting down activity manager...");
        this.this$0.shutdown(10000);
        Log.i("ActivityManager", "Shutdown complete, restarting!");
        Process.killProcess(Process.myPid());
        System.exit(10);
    }
}