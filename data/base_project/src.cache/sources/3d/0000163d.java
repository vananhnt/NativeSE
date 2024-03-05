package android.view.animation;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: AccelerateDecelerateInterpolator.class */
public class AccelerateDecelerateInterpolator implements Interpolator {
    public AccelerateDecelerateInterpolator() {
    }

    public AccelerateDecelerateInterpolator(Context context, AttributeSet attrs) {
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        return ((float) (Math.cos((input + 1.0f) * 3.141592653589793d) / 2.0d)) + 0.5f;
    }
}