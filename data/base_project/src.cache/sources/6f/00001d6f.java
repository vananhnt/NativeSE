package com.android.server.am;

import android.os.DropBoxManager;
import android.util.TimedRemoteCaller;

/* loaded from: ActivityManagerService$16.class */
class ActivityManagerService$16 extends Thread {
    final /* synthetic */ DropBoxManager val$dbox;
    final /* synthetic */ String val$dropboxTag;
    final /* synthetic */ ActivityManagerService this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ActivityManagerService$16(ActivityManagerService activityManagerService, String x0, DropBoxManager dropBoxManager, String str) {
        super(x0);
        this.this$0 = activityManagerService;
        this.val$dbox = dropBoxManager;
        this.val$dropboxTag = str;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        try {
            Thread.sleep(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        } catch (InterruptedException e) {
        }
        synchronized (ActivityManagerService.access$900(this.this$0)) {
            String errorReport = ActivityManagerService.access$900(this.this$0).toString();
            if (errorReport.length() == 0) {
                return;
            }
            ActivityManagerService.access$900(this.this$0).delete(0, ActivityManagerService.access$900(this.this$0).length());
            ActivityManagerService.access$900(this.this$0).trimToSize();
            this.val$dbox.addText(this.val$dropboxTag, errorReport);
        }
    }
}