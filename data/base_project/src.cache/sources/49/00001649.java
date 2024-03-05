package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* loaded from: AnticipateOvershootInterpolator.class */
public class AnticipateOvershootInterpolator implements Interpolator {
    private final float mTension;

    public AnticipateOvershootInterpolator() {
        this.mTension = 3.0f;
    }

    public AnticipateOvershootInterpolator(float tension) {
        this.mTension = tension * 1.5f;
    }

    public AnticipateOvershootInterpolator(float tension, float extraTension) {
        this.mTension = tension * extraTension;
    }

    public AnticipateOvershootInterpolator(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnticipateOvershootInterpolator);
        this.mTension = a.getFloat(0, 2.0f) * a.getFloat(1, 1.5f);
        a.recycle();
    }

    private static float a(float t, float s) {
        return t * t * (((s + 1.0f) * t) - s);
    }

    private static float o(float t, float s) {
        return t * t * (((s + 1.0f) * t) + s);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        return t < 0.5f ? 0.5f * a(t * 2.0f, this.mTension) : 0.5f * (o((t * 2.0f) - 2.0f, this.mTension) + 2.0f);
    }
}