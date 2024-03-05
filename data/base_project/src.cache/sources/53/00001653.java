package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* loaded from: OvershootInterpolator.class */
public class OvershootInterpolator implements Interpolator {
    private final float mTension;

    public OvershootInterpolator() {
        this.mTension = 2.0f;
    }

    public OvershootInterpolator(float tension) {
        this.mTension = tension;
    }

    public OvershootInterpolator(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OvershootInterpolator);
        this.mTension = a.getFloat(0, 2.0f);
        a.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * (((this.mTension + 1.0f) * t2) + this.mTension)) + 1.0f;
    }
}