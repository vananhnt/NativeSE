package android.view;

import android.view.ViewGroup;

/* loaded from: ViewManager.class */
public interface ViewManager {
    void addView(View view, ViewGroup.LayoutParams layoutParams);

    void updateViewLayout(View view, ViewGroup.LayoutParams layoutParams);

    void removeView(View view);
}