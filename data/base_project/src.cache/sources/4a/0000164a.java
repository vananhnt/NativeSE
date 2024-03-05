package android.view.animation;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: BounceInterpolator.class */
public class BounceInterpolator implements Interpolator {
    public BounceInterpolator() {
    }

    public BounceInterpolator(Context context, AttributeSet attrs) {
    }

    private static float bounce(float t) {
        return t * t * 8.0f;
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        float t2 = t * 1.1226f;
        return t2 < 0.3535f ? bounce(t2) : t2 < 0.7408f ? bounce(t2 - 0.54719f) + 0.7f : t2 < 0.9644f ? bounce(t2 - 0.8526f) + 0.9f : bounce(t2 - 1.0435f) + 0.95f;
    }
}