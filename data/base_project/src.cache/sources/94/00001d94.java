package com.android.server.am;

/* loaded from: ActivityManagerService$ProcessChangeItem.class */
class ActivityManagerService$ProcessChangeItem {
    static final int CHANGE_ACTIVITIES = 1;
    static final int CHANGE_IMPORTANCE = 2;
    int changes;
    int uid;
    int pid;
    int importance;
    boolean foregroundActivities;

    ActivityManagerService$ProcessChangeItem() {
    }
}