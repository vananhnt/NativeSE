package android.support.v4.view;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

/* loaded from: ViewGroupCompat.class */
public class ViewGroupCompat {
    static final ViewGroupCompatImpl IMPL;
    public static final int LAYOUT_MODE_CLIP_BOUNDS = 0;
    public static final int LAYOUT_MODE_OPTICAL_BOUNDS = 1;

    /* loaded from: ViewGroupCompat$ViewGroupCompatApi21Impl.class */
    static class ViewGroupCompatApi21Impl extends ViewGroupCompatJellybeanMR2Impl {
        ViewGroupCompatApi21Impl() {
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatStubImpl, android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public boolean isTransitionGroup(ViewGroup viewGroup) {
            return ViewGroupCompatApi21.isTransitionGroup(viewGroup);
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatStubImpl, android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public void setTransitionGroup(ViewGroup viewGroup, boolean z) {
            ViewGroupCompatApi21.setTransitionGroup(viewGroup, z);
        }
    }

    /* loaded from: ViewGroupCompat$ViewGroupCompatHCImpl.class */
    static class ViewGroupCompatHCImpl extends ViewGroupCompatStubImpl {
        ViewGroupCompatHCImpl() {
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatStubImpl, android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public void setMotionEventSplittingEnabled(ViewGroup viewGroup, boolean z) {
            ViewGroupCompatHC.setMotionEventSplittingEnabled(viewGroup, z);
        }
    }

    /* loaded from: ViewGroupCompat$ViewGroupCompatIcsImpl.class */
    static class ViewGroupCompatIcsImpl extends ViewGroupCompatHCImpl {
        ViewGroupCompatIcsImpl() {
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatStubImpl, android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
            return ViewGroupCompatIcs.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
        }
    }

    /* loaded from: ViewGroupCompat$ViewGroupCompatImpl.class */
    interface ViewGroupCompatImpl {
        int getLayoutMode(ViewGroup viewGroup);

        boolean isTransitionGroup(ViewGroup viewGroup);

        boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent);

        void setLayoutMode(ViewGroup viewGroup, int i);

        void setMotionEventSplittingEnabled(ViewGroup viewGroup, boolean z);

        void setTransitionGroup(ViewGroup viewGroup, boolean z);
    }

    /* loaded from: ViewGroupCompat$ViewGroupCompatJellybeanMR2Impl.class */
    static class ViewGroupCompatJellybeanMR2Impl extends ViewGroupCompatIcsImpl {
        ViewGroupCompatJellybeanMR2Impl() {
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatStubImpl, android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public int getLayoutMode(ViewGroup viewGroup) {
            return ViewGroupCompatJellybeanMR2.getLayoutMode(viewGroup);
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatStubImpl, android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public void setLayoutMode(ViewGroup viewGroup, int i) {
            ViewGroupCompatJellybeanMR2.setLayoutMode(viewGroup, i);
        }
    }

    /* loaded from: ViewGroupCompat$ViewGroupCompatStubImpl.class */
    static class ViewGroupCompatStubImpl implements ViewGroupCompatImpl {
        ViewGroupCompatStubImpl() {
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public int getLayoutMode(ViewGroup viewGroup) {
            return 0;
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public boolean isTransitionGroup(ViewGroup viewGroup) {
            return false;
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
            return true;
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public void setLayoutMode(ViewGroup viewGroup, int i) {
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public void setMotionEventSplittingEnabled(ViewGroup viewGroup, boolean z) {
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatImpl
        public void setTransitionGroup(ViewGroup viewGroup, boolean z) {
        }
    }

    static {
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            IMPL = new ViewGroupCompatApi21Impl();
        } else if (i >= 18) {
            IMPL = new ViewGroupCompatJellybeanMR2Impl();
        } else if (i >= 14) {
            IMPL = new ViewGroupCompatIcsImpl();
        } else if (i >= 11) {
            IMPL = new ViewGroupCompatHCImpl();
        } else {
            IMPL = new ViewGroupCompatStubImpl();
        }
    }

    private ViewGroupCompat() {
    }

    public static int getLayoutMode(ViewGroup viewGroup) {
        return IMPL.getLayoutMode(viewGroup);
    }

    public static boolean isTransitionGroup(ViewGroup viewGroup) {
        return IMPL.isTransitionGroup(viewGroup);
    }

    public static boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
        return IMPL.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
    }

    public static void setLayoutMode(ViewGroup viewGroup, int i) {
        IMPL.setLayoutMode(viewGroup, i);
    }

    public static void setMotionEventSplittingEnabled(ViewGroup viewGroup, boolean z) {
        IMPL.setMotionEventSplittingEnabled(viewGroup, z);
    }

    public static void setTransitionGroup(ViewGroup viewGroup, boolean z) {
        IMPL.setTransitionGroup(viewGroup, z);
    }
}