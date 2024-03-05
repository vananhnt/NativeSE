package android.support.v4.widget;

import android.widget.OverScroller;

/* loaded from: ScrollerCompatIcs.class */
class ScrollerCompatIcs {
    ScrollerCompatIcs() {
    }

    public static float getCurrVelocity(Object obj) {
        return ((OverScroller) obj).getCurrVelocity();
    }
}