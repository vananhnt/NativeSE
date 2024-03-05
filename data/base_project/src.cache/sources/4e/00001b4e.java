package com.android.internal.view;

import android.view.InputQueue;
import android.view.SurfaceHolder;

/* loaded from: RootViewSurfaceTaker.class */
public interface RootViewSurfaceTaker {
    SurfaceHolder.Callback2 willYouTakeTheSurface();

    void setSurfaceType(int i);

    void setSurfaceFormat(int i);

    void setSurfaceKeepScreenOn(boolean z);

    InputQueue.Callback willYouTakeTheInputQueue();
}