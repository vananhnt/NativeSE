package com.android.internal.notification;

import android.app.Notification;
import android.content.Context;

/* loaded from: NotificationScorer.class */
public interface NotificationScorer {
    void initialize(Context context);

    int getScore(Notification notification, int i);
}