package com.android.server.accessibility;

import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

/* loaded from: EventStreamTransformation.class */
interface EventStreamTransformation {
    void onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i);

    void onAccessibilityEvent(AccessibilityEvent accessibilityEvent);

    void setNext(EventStreamTransformation eventStreamTransformation);

    void clear();

    void onDestroy();
}