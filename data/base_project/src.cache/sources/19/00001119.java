package android.support.v4.view;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/* loaded from: ViewParentCompat.class */
public class ViewParentCompat {
    static final ViewParentCompatImpl IMPL;

    /* loaded from: ViewParentCompat$ViewParentCompatICSImpl.class */
    static class ViewParentCompatICSImpl extends ViewParentCompatStubImpl {
        ViewParentCompatICSImpl() {
        }

        @Override // android.support.v4.view.ViewParentCompat.ViewParentCompatStubImpl, android.support.v4.view.ViewParentCompat.ViewParentCompatImpl
        public boolean requestSendAccessibilityEvent(ViewParent viewParent, View view, AccessibilityEvent accessibilityEvent) {
            return ViewParentCompatICS.requestSendAccessibilityEvent(viewParent, view, accessibilityEvent);
        }
    }

    /* loaded from: ViewParentCompat$ViewParentCompatImpl.class */
    interface ViewParentCompatImpl {
        boolean requestSendAccessibilityEvent(ViewParent viewParent, View view, AccessibilityEvent accessibilityEvent);
    }

    /* loaded from: ViewParentCompat$ViewParentCompatStubImpl.class */
    static class ViewParentCompatStubImpl implements ViewParentCompatImpl {
        ViewParentCompatStubImpl() {
        }

        @Override // android.support.v4.view.ViewParentCompat.ViewParentCompatImpl
        public boolean requestSendAccessibilityEvent(ViewParent viewParent, View view, AccessibilityEvent accessibilityEvent) {
            if (view == null) {
                return false;
            }
            ((AccessibilityManager) view.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE)).sendAccessibilityEvent(accessibilityEvent);
            return true;
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new ViewParentCompatICSImpl();
        } else {
            IMPL = new ViewParentCompatStubImpl();
        }
    }

    private ViewParentCompat() {
    }

    public static boolean requestSendAccessibilityEvent(ViewParent viewParent, View view, AccessibilityEvent accessibilityEvent) {
        return IMPL.requestSendAccessibilityEvent(viewParent, view, accessibilityEvent);
    }
}