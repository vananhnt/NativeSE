package com.android.server.am;

import android.app.Dialog;

/* loaded from: ActivityManagerService$6.class */
class ActivityManagerService$6 implements Runnable {
    final /* synthetic */ ActivityRecord val$cur;
    final /* synthetic */ ActivityRecord val$next;
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$6(ActivityManagerService activityManagerService, ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        this.this$0 = activityManagerService;
        this.val$cur = activityRecord;
        this.val$next = activityRecord2;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.this$0) {
            final Dialog d = new LaunchWarningWindow(this.this$0.mContext, this.val$cur, this.val$next);
            d.show();
            this.this$0.mHandler.postDelayed(new Runnable() { // from class: com.android.server.am.ActivityManagerService$6.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (ActivityManagerService$6.this.this$0) {
                        d.dismiss();
                        ActivityManagerService$6.this.this$0.mLaunchWarningShown = false;
                    }
                }
            }, 4000L);
        }
    }
}