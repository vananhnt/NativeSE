package android.animation;

import android.animation.Keyframe;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: FloatKeyframeSet.class */
public class FloatKeyframeSet extends KeyframeSet {
    private float firstValue;
    private float lastValue;
    private float deltaValue;
    private boolean firstTime;

    public FloatKeyframeSet(Keyframe.FloatKeyframe... keyframes) {
        super(keyframes);
        this.firstTime = true;
    }

    @Override // android.animation.KeyframeSet
    public Object getValue(float fraction) {
        return Float.valueOf(getFloatValue(fraction));
    }

    @Override // android.animation.KeyframeSet
    /* renamed from: clone */
    public FloatKeyframeSet mo8clone() {
        ArrayList<Keyframe> keyframes = this.mKeyframes;
        int numKeyframes = this.mKeyframes.size();
        Keyframe.FloatKeyframe[] newKeyframes = new Keyframe.FloatKeyframe[numKeyframes];
        for (int i = 0; i < numKeyframes; i++) {
            newKeyframes[i] = (Keyframe.FloatKeyframe) keyframes.get(i).mo9clone();
        }
        FloatKeyframeSet newSet = new FloatKeyframeSet(newKeyframes);
        return newSet;
    }

    public float getFloatValue(float fraction) {
        if (this.mNumKeyframes == 2) {
            if (this.firstTime) {
                this.firstTime = false;
                this.firstValue = ((Keyframe.FloatKeyframe) this.mKeyframes.get(0)).getFloatValue();
                this.lastValue = ((Keyframe.FloatKeyframe) this.mKeyframes.get(1)).getFloatValue();
                this.deltaValue = this.lastValue - this.firstValue;
            }
            if (this.mInterpolator != null) {
                fraction = this.mInterpolator.getInterpolation(fraction);
            }
            if (this.mEvaluator == null) {
                return this.firstValue + (fraction * this.deltaValue);
            }
            return ((Number) this.mEvaluator.evaluate(fraction, Float.valueOf(this.firstValue), Float.valueOf(this.lastValue))).floatValue();
        } else if (fraction <= 0.0f) {
            Keyframe.FloatKeyframe prevKeyframe = (Keyframe.FloatKeyframe) this.mKeyframes.get(0);
            Keyframe.FloatKeyframe nextKeyframe = (Keyframe.FloatKeyframe) this.mKeyframes.get(1);
            float prevValue = prevKeyframe.getFloatValue();
            float nextValue = nextKeyframe.getFloatValue();
            float prevFraction = prevKeyframe.getFraction();
            float nextFraction = nextKeyframe.getFraction();
            TimeInterpolator interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            float intervalFraction = (fraction - prevFraction) / (nextFraction - prevFraction);
            return this.mEvaluator == null ? prevValue + (intervalFraction * (nextValue - prevValue)) : ((Number) this.mEvaluator.evaluate(intervalFraction, Float.valueOf(prevValue), Float.valueOf(nextValue))).floatValue();
        } else if (fraction >= 1.0f) {
            Keyframe.FloatKeyframe prevKeyframe2 = (Keyframe.FloatKeyframe) this.mKeyframes.get(this.mNumKeyframes - 2);
            Keyframe.FloatKeyframe nextKeyframe2 = (Keyframe.FloatKeyframe) this.mKeyframes.get(this.mNumKeyframes - 1);
            float prevValue2 = prevKeyframe2.getFloatValue();
            float nextValue2 = nextKeyframe2.getFloatValue();
            float prevFraction2 = prevKeyframe2.getFraction();
            float nextFraction2 = nextKeyframe2.getFraction();
            TimeInterpolator interpolator2 = nextKeyframe2.getInterpolator();
            if (interpolator2 != null) {
                fraction = interpolator2.getInterpolation(fraction);
            }
            float intervalFraction2 = (fraction - prevFraction2) / (nextFraction2 - prevFraction2);
            return this.mEvaluator == null ? prevValue2 + (intervalFraction2 * (nextValue2 - prevValue2)) : ((Number) this.mEvaluator.evaluate(intervalFraction2, Float.valueOf(prevValue2), Float.valueOf(nextValue2))).floatValue();
        } else {
            Keyframe.FloatKeyframe prevKeyframe3 = (Keyframe.FloatKeyframe) this.mKeyframes.get(0);
            for (int i = 1; i < this.mNumKeyframes; i++) {
                Keyframe.FloatKeyframe nextKeyframe3 = (Keyframe.FloatKeyframe) this.mKeyframes.get(i);
                if (fraction < nextKeyframe3.getFraction()) {
                    TimeInterpolator interpolator3 = nextKeyframe3.getInterpolator();
                    if (interpolator3 != null) {
                        fraction = interpolator3.getInterpolation(fraction);
                    }
                    float intervalFraction3 = (fraction - prevKeyframe3.getFraction()) / (nextKeyframe3.getFraction() - prevKeyframe3.getFraction());
                    float prevValue3 = prevKeyframe3.getFloatValue();
                    float nextValue3 = nextKeyframe3.getFloatValue();
                    return this.mEvaluator == null ? prevValue3 + (intervalFraction3 * (nextValue3 - prevValue3)) : ((Number) this.mEvaluator.evaluate(intervalFraction3, Float.valueOf(prevValue3), Float.valueOf(nextValue3))).floatValue();
                }
                prevKeyframe3 = nextKeyframe3;
            }
            return ((Number) this.mKeyframes.get(this.mNumKeyframes - 1).getValue()).floatValue();
        }
    }
}