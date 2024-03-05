package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* loaded from: CycleInterpolator.class */
public class CycleInterpolator implements Interpolator {
    private float mCycles;

    public CycleInterpolator(float cycles) {
        this.mCycles = cycles;
    }

    public CycleInterpolator(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CycleInterpolator);
        this.mCycles = a.getFloat(0, 1.0f);
        a.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        return (float) Math.sin(2.0f * this.mCycles * 3.141592653589793d * input);
    }
}