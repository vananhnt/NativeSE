package android.support.v4.view;

import android.view.ViewGroup;

/* loaded from: ViewGroupCompatApi21.class */
class ViewGroupCompatApi21 {
    ViewGroupCompatApi21() {
    }

    public static boolean isTransitionGroup(ViewGroup viewGroup) {
        return viewGroup.isTransitionGroup();
    }

    public static void setTransitionGroup(ViewGroup viewGroup, boolean z) {
        viewGroup.setTransitionGroup(z);
    }
}