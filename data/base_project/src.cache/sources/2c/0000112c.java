package android.support.v4.view;

import android.view.View;
import android.view.animation.Interpolator;

/* loaded from: ViewPropertyAnimatorCompatJellybeanMr2.class */
class ViewPropertyAnimatorCompatJellybeanMr2 {
    ViewPropertyAnimatorCompatJellybeanMr2() {
    }

    public static Interpolator getInterpolator(View view) {
        return (Interpolator) view.animate().getInterpolator();
    }
}