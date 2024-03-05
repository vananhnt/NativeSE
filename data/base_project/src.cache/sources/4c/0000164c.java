package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* loaded from: DecelerateInterpolator.class */
public class DecelerateInterpolator implements Interpolator {
    private float mFactor;

    public DecelerateInterpolator() {
        this.mFactor = 1.0f;
    }

    public DecelerateInterpolator(float factor) {
        this.mFactor = 1.0f;
        this.mFactor = factor;
    }

    public DecelerateInterpolator(Context context, AttributeSet attrs) {
        this.mFactor = 1.0f;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DecelerateInterpolator);
        this.mFactor = a.getFloat(0, 1.0f);
        a.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        float result;
        if (this.mFactor == 1.0f) {
            result = 1.0f - ((1.0f - input) * (1.0f - input));
        } else {
            result = (float) (1.0d - Math.pow(1.0f - input, 2.0f * this.mFactor));
        }
        return result;
    }
}