package android.support.v4.view;

import android.os.Build;

/* loaded from: ScaleGestureDetectorCompat.class */
public class ScaleGestureDetectorCompat {
    static final ScaleGestureDetectorImpl IMPL;

    /* loaded from: ScaleGestureDetectorCompat$BaseScaleGestureDetectorImpl.class */
    private static class BaseScaleGestureDetectorImpl implements ScaleGestureDetectorImpl {
        private BaseScaleGestureDetectorImpl() {
        }

        @Override // android.support.v4.view.ScaleGestureDetectorCompat.ScaleGestureDetectorImpl
        public boolean isQuickScaleEnabled(Object obj) {
            return false;
        }

        @Override // android.support.v4.view.ScaleGestureDetectorCompat.ScaleGestureDetectorImpl
        public void setQuickScaleEnabled(Object obj, boolean z) {
        }
    }

    /* loaded from: ScaleGestureDetectorCompat$ScaleGestureDetectorCompatKitKatImpl.class */
    private static class ScaleGestureDetectorCompatKitKatImpl implements ScaleGestureDetectorImpl {
        private ScaleGestureDetectorCompatKitKatImpl() {
        }

        @Override // android.support.v4.view.ScaleGestureDetectorCompat.ScaleGestureDetectorImpl
        public boolean isQuickScaleEnabled(Object obj) {
            return ScaleGestureDetectorCompatKitKat.isQuickScaleEnabled(obj);
        }

        @Override // android.support.v4.view.ScaleGestureDetectorCompat.ScaleGestureDetectorImpl
        public void setQuickScaleEnabled(Object obj, boolean z) {
            ScaleGestureDetectorCompatKitKat.setQuickScaleEnabled(obj, z);
        }
    }

    /* loaded from: ScaleGestureDetectorCompat$ScaleGestureDetectorImpl.class */
    interface ScaleGestureDetectorImpl {
        boolean isQuickScaleEnabled(Object obj);

        void setQuickScaleEnabled(Object obj, boolean z);
    }

    static {
        if (Build.VERSION.SDK_INT >= 19) {
            IMPL = new ScaleGestureDetectorCompatKitKatImpl();
        } else {
            IMPL = new BaseScaleGestureDetectorImpl();
        }
    }

    private ScaleGestureDetectorCompat() {
    }

    public static boolean isQuickScaleEnabled(Object obj) {
        return IMPL.isQuickScaleEnabled(obj);
    }

    public static void setQuickScaleEnabled(Object obj, boolean z) {
        IMPL.setQuickScaleEnabled(obj, z);
    }
}