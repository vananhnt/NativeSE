package com.android.server.am;

import java.util.ArrayList;

/* loaded from: ActivityManagerService$MemItem.class */
final class ActivityManagerService$MemItem {
    final boolean isProc;
    final String label;
    final String shortLabel;
    final long pss;
    final int id;
    final boolean hasActivities;
    ArrayList<ActivityManagerService$MemItem> subitems;

    public ActivityManagerService$MemItem(String _label, String _shortLabel, long _pss, int _id, boolean _hasActivities) {
        this.isProc = true;
        this.label = _label;
        this.shortLabel = _shortLabel;
        this.pss = _pss;
        this.id = _id;
        this.hasActivities = _hasActivities;
    }

    public ActivityManagerService$MemItem(String _label, String _shortLabel, long _pss, int _id) {
        this.isProc = false;
        this.label = _label;
        this.shortLabel = _shortLabel;
        this.pss = _pss;
        this.id = _id;
        this.hasActivities = false;
    }
}