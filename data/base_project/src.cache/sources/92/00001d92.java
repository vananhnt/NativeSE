package com.android.server.am;

import android.os.Binder;
import android.os.Bundle;
import android.util.Slog;

/* loaded from: ActivityManagerService$PendingAssistExtras.class */
public class ActivityManagerService$PendingAssistExtras extends Binder implements Runnable {
    public final ActivityRecord activity;
    public boolean haveResult = false;
    public Bundle result = null;
    final /* synthetic */ ActivityManagerService this$0;

    public ActivityManagerService$PendingAssistExtras(ActivityManagerService activityManagerService, ActivityRecord _activity) {
        this.this$0 = activityManagerService;
        this.activity = _activity;
    }

    @Override // java.lang.Runnable
    public void run() {
        Slog.w("ActivityManager", "getAssistContextExtras failed: timeout retrieving from " + this.activity);
        synchronized (this) {
            this.haveResult = true;
            notifyAll();
        }
    }
}