package com.android.server.am;

import java.util.Comparator;

/* loaded from: ActivityManagerService$19.class */
class ActivityManagerService$19 implements Comparator<ActivityManagerService$MemItem> {
    ActivityManagerService$19() {
    }

    @Override // java.util.Comparator
    public int compare(ActivityManagerService$MemItem lhs, ActivityManagerService$MemItem rhs) {
        if (lhs.pss < rhs.pss) {
            return 1;
        }
        if (lhs.pss > rhs.pss) {
            return -1;
        }
        return 0;
    }
}