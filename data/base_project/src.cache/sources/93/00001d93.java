package com.android.server.am;

import android.os.IPermissionController;

/* loaded from: ActivityManagerService$PermissionController.class */
class ActivityManagerService$PermissionController extends IPermissionController.Stub {
    ActivityManagerService mActivityManagerService;

    ActivityManagerService$PermissionController(ActivityManagerService activityManagerService) {
        this.mActivityManagerService = activityManagerService;
    }

    @Override // android.os.IPermissionController
    public boolean checkPermission(String permission, int pid, int uid) {
        return this.mActivityManagerService.checkPermission(permission, pid, uid) == 0;
    }
}