package com.android.internal.policy;

import android.content.Context;
import android.view.FallbackEventHandler;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManagerPolicy;

/* loaded from: IPolicy.class */
public interface IPolicy {
    Window makeNewWindow(Context context);

    LayoutInflater makeNewLayoutInflater(Context context);

    WindowManagerPolicy makeNewWindowManager();

    FallbackEventHandler makeNewFallbackEventHandler(Context context);
}