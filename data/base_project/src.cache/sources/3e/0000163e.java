package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* loaded from: AccelerateInterpolator.class */
public class AccelerateInterpolator implements Interpolator {
    private final float mFactor;
    private final double mDoubleFactor;

    public AccelerateInterpolator() {
        this.mFactor = 1.0f;
        this.mDoubleFactor = 2.0d;
    }

    public AccelerateInterpolator(float factor) {
        this.mFactor = factor;
        this.mDoubleFactor = 2.0f * this.mFactor;
    }

    public AccelerateInterpolator(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AccelerateInterpolator);
        this.mFactor = a.getFloat(0, 1.0f);
        this.mDoubleFactor = 2.0f * this.mFactor;
        a.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        if (this.mFactor == 1.0f) {
            return input * input;
        }
        return (float) Math.pow(input, this.mDoubleFactor);
    }
}