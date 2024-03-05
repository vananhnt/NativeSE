package com.android.server.power;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: SuspendBlocker.class */
public interface SuspendBlocker {
    void acquire();

    void release();
}