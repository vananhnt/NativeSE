package android.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/* loaded from: Slide.class */
public class Slide extends Visibility {
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();

    @Override // android.transition.Visibility
    public Animator onAppear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        View endView = endValues != null ? endValues.view : null;
        endView.setTranslationY((-2) * endView.getHeight());
        ObjectAnimator anim = ObjectAnimator.ofFloat(endView, View.TRANSLATION_Y, (-2) * endView.getHeight(), 0.0f);
        anim.setInterpolator(sDecelerator);
        return anim;
    }

    @Override // android.transition.Visibility
    public Animator onDisappear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        View startView = startValues != null ? startValues.view : null;
        startView.setTranslationY(0.0f);
        ObjectAnimator anim = ObjectAnimator.ofFloat(startView, View.TRANSLATION_Y, 0.0f, (-2) * startView.getHeight());
        anim.setInterpolator(sAccelerator);
        return anim;
    }
}