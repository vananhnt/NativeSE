package com.android.server.am;

/* loaded from: ActivityManagerService$10.class */
class ActivityManagerService$10 implements Runnable {
    final /* synthetic */ ProcessRecord val$proc;
    final /* synthetic */ ActivityRecord val$activity;
    final /* synthetic */ ActivityRecord val$parent;
    final /* synthetic */ boolean val$aboveSystem;
    final /* synthetic */ String val$annotation;
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$10(ActivityManagerService activityManagerService, ProcessRecord processRecord, ActivityRecord activityRecord, ActivityRecord activityRecord2, boolean z, String str) {
        this.this$0 = activityManagerService;
        this.val$proc = processRecord;
        this.val$activity = activityRecord;
        this.val$parent = activityRecord2;
        this.val$aboveSystem = z;
        this.val$annotation = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.this$0.appNotResponding(this.val$proc, this.val$activity, this.val$parent, this.val$aboveSystem, this.val$annotation);
    }
}