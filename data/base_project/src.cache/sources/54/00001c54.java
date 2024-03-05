package com.android.server;

/* loaded from: INativeDaemonConnectorCallbacks.class */
interface INativeDaemonConnectorCallbacks {
    void onDaemonConnected();

    boolean onEvent(int i, String str, String[] strArr);
}