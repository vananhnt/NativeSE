package android.support.v4.widget;

import android.os.Build;
import android.view.View;

/* loaded from: PopupMenuCompat.class */
public class PopupMenuCompat {
    static final PopupMenuImpl IMPL;

    /* loaded from: PopupMenuCompat$BasePopupMenuImpl.class */
    static class BasePopupMenuImpl implements PopupMenuImpl {
        BasePopupMenuImpl() {
        }

        @Override // android.support.v4.widget.PopupMenuCompat.PopupMenuImpl
        public View.OnTouchListener getDragToOpenListener(Object obj) {
            return null;
        }
    }

    /* loaded from: PopupMenuCompat$KitKatPopupMenuImpl.class */
    static class KitKatPopupMenuImpl extends BasePopupMenuImpl {
        KitKatPopupMenuImpl() {
        }

        @Override // android.support.v4.widget.PopupMenuCompat.BasePopupMenuImpl, android.support.v4.widget.PopupMenuCompat.PopupMenuImpl
        public View.OnTouchListener getDragToOpenListener(Object obj) {
            return PopupMenuCompatKitKat.getDragToOpenListener(obj);
        }
    }

    /* loaded from: PopupMenuCompat$PopupMenuImpl.class */
    interface PopupMenuImpl {
        View.OnTouchListener getDragToOpenListener(Object obj);
    }

    static {
        if (Build.VERSION.SDK_INT >= 19) {
            IMPL = new KitKatPopupMenuImpl();
        } else {
            IMPL = new BasePopupMenuImpl();
        }
    }

    private PopupMenuCompat() {
    }

    public static View.OnTouchListener getDragToOpenListener(Object obj) {
        return IMPL.getDragToOpenListener(obj);
    }
}