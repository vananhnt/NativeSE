package android.support.v7.internal.widget;

import android.graphics.Rect;

/* loaded from: FitWindowsViewGroup.class */
public interface FitWindowsViewGroup {

    /* loaded from: FitWindowsViewGroup$OnFitSystemWindowsListener.class */
    public interface OnFitSystemWindowsListener {
        void onFitSystemWindows(Rect rect);
    }

    void setOnFitSystemWindowsListener(OnFitSystemWindowsListener onFitSystemWindowsListener);
}