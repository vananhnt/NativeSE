package android.animation;

import android.graphics.Rect;

/* loaded from: RectEvaluator.class */
public class RectEvaluator implements TypeEvaluator<Rect> {
    @Override // android.animation.TypeEvaluator
    public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
        return new Rect(startValue.left + ((int) ((endValue.left - startValue.left) * fraction)), startValue.top + ((int) ((endValue.top - startValue.top) * fraction)), startValue.right + ((int) ((endValue.right - startValue.right) * fraction)), startValue.bottom + ((int) ((endValue.bottom - startValue.bottom) * fraction)));
    }
}