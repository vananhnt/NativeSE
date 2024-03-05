package com.android.server.am;

import android.Manifest;
import android.os.Binder;
import android.os.SystemClock;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: ActivityManagerService$CpuBinder.class */
class ActivityManagerService$CpuBinder extends Binder {
    ActivityManagerService mActivityManagerService;

    ActivityManagerService$CpuBinder(ActivityManagerService activityManagerService) {
        this.mActivityManagerService = activityManagerService;
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mActivityManagerService.checkCallingPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump cpuinfo from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + Manifest.permission.DUMP);
            return;
        }
        synchronized (this.mActivityManagerService.mProcessCpuThread) {
            pw.print(this.mActivityManagerService.mProcessCpuTracker.printCurrentLoad());
            pw.print(this.mActivityManagerService.mProcessCpuTracker.printCurrentState(SystemClock.uptimeMillis()));
        }
    }
}