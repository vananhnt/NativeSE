package com.android.server.am;

import android.util.Pair;
import java.util.Comparator;

/* loaded from: ActivityManagerService$18.class */
class ActivityManagerService$18 implements Comparator<Pair<ProcessRecord, Integer>> {
    ActivityManagerService$18() {
    }

    @Override // java.util.Comparator
    public int compare(Pair<ProcessRecord, Integer> object1, Pair<ProcessRecord, Integer> object2) {
        if (object1.first.setAdj != object2.first.setAdj) {
            return object1.first.setAdj > object2.first.setAdj ? -1 : 1;
        } else if (object1.second.intValue() != object2.second.intValue()) {
            return object1.second.intValue() > object2.second.intValue() ? -1 : 1;
        } else {
            return 0;
        }
    }
}