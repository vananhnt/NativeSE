package android.view.animation;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: LinearInterpolator.class */
public class LinearInterpolator implements Interpolator {
    public LinearInterpolator() {
    }

    public LinearInterpolator(Context context, AttributeSet attrs) {
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        return input;
    }
}