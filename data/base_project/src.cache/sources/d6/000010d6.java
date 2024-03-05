package android.support.v4.view;

import android.view.ScaleGestureDetector;

/* loaded from: ScaleGestureDetectorCompatKitKat.class */
class ScaleGestureDetectorCompatKitKat {
    private ScaleGestureDetectorCompatKitKat() {
    }

    public static boolean isQuickScaleEnabled(Object obj) {
        return ((ScaleGestureDetector) obj).isQuickScaleEnabled();
    }

    public static void setQuickScaleEnabled(Object obj, boolean z) {
        ((ScaleGestureDetector) obj).setQuickScaleEnabled(z);
    }
}