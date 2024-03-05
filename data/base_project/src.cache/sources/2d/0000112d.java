package android.support.v4.view;

import android.animation.ValueAnimator;
import android.view.View;

/* loaded from: ViewPropertyAnimatorCompatKK.class */
class ViewPropertyAnimatorCompatKK {
    ViewPropertyAnimatorCompatKK() {
    }

    public static void setUpdateListener(View view, ViewPropertyAnimatorUpdateListener viewPropertyAnimatorUpdateListener) {
        view.animate().setUpdateListener(new ValueAnimator.AnimatorUpdateListener(viewPropertyAnimatorUpdateListener, view) { // from class: android.support.v4.view.ViewPropertyAnimatorCompatKK.1
            final ViewPropertyAnimatorUpdateListener val$listener;
            final View val$view;

            {
                this.val$listener = viewPropertyAnimatorUpdateListener;
                this.val$view = view;
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.val$listener.onAnimationUpdate(this.val$view);
            }
        });
    }
}