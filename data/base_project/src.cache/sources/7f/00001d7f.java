package com.android.server.am;

import android.os.FileObserver;

/* loaded from: ActivityManagerService$5.class */
class ActivityManagerService$5 extends FileObserver {
    ActivityManagerService$5(String x0, int x1) {
        super(x0, x1);
    }

    @Override // android.os.FileObserver
    public synchronized void onEvent(int event, String path) {
        notify();
    }
}