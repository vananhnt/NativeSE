package com.android.server.am;

import android.os.DropBoxManager;

/* loaded from: ActivityManagerService$15.class */
class ActivityManagerService$15 extends Thread {
    final /* synthetic */ StringBuilder val$sb;
    final /* synthetic */ DropBoxManager val$dbox;
    final /* synthetic */ String val$dropboxTag;
    final /* synthetic */ ActivityManagerService this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ActivityManagerService$15(ActivityManagerService activityManagerService, String x0, StringBuilder sb, DropBoxManager dropBoxManager, String str) {
        super(x0);
        this.this$0 = activityManagerService;
        this.val$sb = sb;
        this.val$dbox = dropBoxManager;
        this.val$dropboxTag = str;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        String report;
        synchronized (this.val$sb) {
            report = this.val$sb.toString();
            this.val$sb.delete(0, this.val$sb.length());
            this.val$sb.trimToSize();
        }
        if (report.length() != 0) {
            this.val$dbox.addText(this.val$dropboxTag, report);
        }
    }
}