package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;

/* loaded from: AlphaAnimation.class */
public class AlphaAnimation extends Animation {
    private float mFromAlpha;
    private float mToAlpha;

    public AlphaAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AlphaAnimation);
        this.mFromAlpha = a.getFloat(0, 1.0f);
        this.mToAlpha = a.getFloat(1, 1.0f);
        a.recycle();
    }

    public AlphaAnimation(float fromAlpha, float toAlpha) {
        this.mFromAlpha = fromAlpha;
        this.mToAlpha = toAlpha;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.animation.Animation
    public void applyTransformation(float interpolatedTime, Transformation t) {
        float alpha = this.mFromAlpha;
        t.setAlpha(alpha + ((this.mToAlpha - alpha) * interpolatedTime));
    }

    @Override // android.view.animation.Animation
    public boolean willChangeTransformationMatrix() {
        return false;
    }

    @Override // android.view.animation.Animation
    public boolean willChangeBounds() {
        return false;
    }

    @Override // android.view.animation.Animation
    public boolean hasAlpha() {
        return true;
    }
}