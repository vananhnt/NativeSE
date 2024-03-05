package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* loaded from: AnticipateInterpolator.class */
public class AnticipateInterpolator implements Interpolator {
    private final float mTension;

    public AnticipateInterpolator() {
        this.mTension = 2.0f;
    }

    public AnticipateInterpolator(float tension) {
        this.mTension = tension;
    }

    public AnticipateInterpolator(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnticipateInterpolator);
        this.mTension = a.getFloat(0, 2.0f);
        a.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        return t * t * (((this.mTension + 1.0f) * t) - this.mTension);
    }
}