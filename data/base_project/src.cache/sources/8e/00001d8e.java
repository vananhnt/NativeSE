package com.android.server.am;

import android.Manifest;
import android.os.Binder;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: ActivityManagerService$MemBinder.class */
class ActivityManagerService$MemBinder extends Binder {
    ActivityManagerService mActivityManagerService;

    ActivityManagerService$MemBinder(ActivityManagerService activityManagerService) {
        this.mActivityManagerService = activityManagerService;
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mActivityManagerService.checkCallingPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump meminfo from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + Manifest.permission.DUMP);
        } else {
            this.mActivityManagerService.dumpApplicationMemoryUsage(fd, pw, "  ", args, false, (PrintWriter) null);
        }
    }
}