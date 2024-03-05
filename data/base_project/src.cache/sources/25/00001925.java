package com.android.dex.util;

import android.widget.ExpandableListView;

/* loaded from: Unsigned.class */
public final class Unsigned {
    private Unsigned() {
    }

    public static int compare(short ushortA, short ushortB) {
        if (ushortA == ushortB) {
            return 0;
        }
        int a = ushortA & 65535;
        int b = ushortB & 65535;
        return a < b ? -1 : 1;
    }

    public static int compare(int uintA, int uintB) {
        if (uintA == uintB) {
            return 0;
        }
        long a = uintA & ExpandableListView.PACKED_POSITION_VALUE_NULL;
        long b = uintB & ExpandableListView.PACKED_POSITION_VALUE_NULL;
        return a < b ? -1 : 1;
    }
}