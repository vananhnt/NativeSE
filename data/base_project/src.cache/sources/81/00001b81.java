package com.android.internal.widget;

import android.view.View;

/* loaded from: LockScreenWidgetCallback.class */
public interface LockScreenWidgetCallback {
    void requestShow(View view);

    void requestHide(View view);

    boolean isVisible(View view);

    void userActivity(View view);
}