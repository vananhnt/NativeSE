package android.transition;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/* loaded from: Recolor.class */
public class Recolor extends Transition {
    private static final String PROPNAME_BACKGROUND = "android:recolor:background";
    private static final String PROPNAME_TEXT_COLOR = "android:recolor:textColor";

    private void captureValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_BACKGROUND, transitionValues.view.getBackground());
        if (transitionValues.view instanceof TextView) {
            transitionValues.values.put(PROPNAME_TEXT_COLOR, Integer.valueOf(((TextView) transitionValues.view).getCurrentTextColor()));
        }
    }

    @Override // android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        View view = endValues.view;
        Drawable startBackground = (Drawable) startValues.values.get(PROPNAME_BACKGROUND);
        Drawable endBackground = (Drawable) endValues.values.get(PROPNAME_BACKGROUND);
        if ((startBackground instanceof ColorDrawable) && (endBackground instanceof ColorDrawable)) {
            ColorDrawable startColor = (ColorDrawable) startBackground;
            ColorDrawable endColor = (ColorDrawable) endBackground;
            if (startColor.getColor() != endColor.getColor()) {
                endColor.setColor(startColor.getColor());
                return ObjectAnimator.ofObject(endBackground, CalendarContract.ColorsColumns.COLOR, new ArgbEvaluator(), Integer.valueOf(startColor.getColor()), Integer.valueOf(endColor.getColor()));
            }
        }
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            int start = ((Integer) startValues.values.get(PROPNAME_TEXT_COLOR)).intValue();
            int end = ((Integer) endValues.values.get(PROPNAME_TEXT_COLOR)).intValue();
            if (start != end) {
                textView.setTextColor(end);
                return ObjectAnimator.ofObject(textView, "textColor", new ArgbEvaluator(), Integer.valueOf(start), Integer.valueOf(end));
            }
            return null;
        }
        return null;
    }
}