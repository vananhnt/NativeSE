package android.support.v4.widget;

import android.view.View;
import android.widget.PopupMenu;

/* loaded from: PopupMenuCompatKitKat.class */
class PopupMenuCompatKitKat {
    PopupMenuCompatKitKat() {
    }

    public static View.OnTouchListener getDragToOpenListener(Object obj) {
        return ((PopupMenu) obj).getDragToOpenListener();
    }
}