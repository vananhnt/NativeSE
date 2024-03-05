package android.animation;

import android.animation.Keyframe;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: IntKeyframeSet.class */
public class IntKeyframeSet extends KeyframeSet {
    private int firstValue;
    private int lastValue;
    private int deltaValue;
    private boolean firstTime;

    public IntKeyframeSet(Keyframe.IntKeyframe... keyframes) {
        super(keyframes);
        this.firstTime = true;
    }

    @Override // android.animation.KeyframeSet
    public Object getValue(float fraction) {
        return Integer.valueOf(getIntValue(fraction));
    }

    @Override // android.animation.KeyframeSet
    /* renamed from: clone */
    public IntKeyframeSet mo8clone() {
        ArrayList<Keyframe> keyframes = this.mKeyframes;
        int numKeyframes = this.mKeyframes.size();
        Keyframe.IntKeyframe[] newKeyframes = new Keyframe.IntKeyframe[numKeyframes];
        for (int i = 0; i < numKeyframes; i++) {
            newKeyframes[i] = (Keyframe.IntKeyframe) keyframes.get(i).mo9clone();
        }
        IntKeyframeSet newSet = new IntKeyframeSet(newKeyframes);
        return newSet;
    }

    public int getIntValue(float fraction) {
        if (this.mNumKeyframes == 2) {
            if (this.firstTime) {
                this.firstTime = false;
                this.firstValue = ((Keyframe.IntKeyframe) this.mKeyframes.get(0)).getIntValue();
                this.lastValue = ((Keyframe.IntKeyframe) this.mKeyframes.get(1)).getIntValue();
                this.deltaValue = this.lastValue - this.firstValue;
            }
            if (this.mInterpolator != null) {
                fraction = this.mInterpolator.getInterpolation(fraction);
            }
            if (this.mEvaluator == null) {
                return this.firstValue + ((int) (fraction * this.deltaValue));
            }
            return ((Number) this.mEvaluator.evaluate(fraction, Integer.valueOf(this.firstValue), Integer.valueOf(this.lastValue))).intValue();
        } else if (fraction <= 0.0f) {
            Keyframe.IntKeyframe prevKeyframe = (Keyframe.IntKeyframe) this.mKeyframes.get(0);
            Keyframe.IntKeyframe nextKeyframe = (Keyframe.IntKeyframe) this.mKeyframes.get(1);
            int prevValue = prevKeyframe.getIntValue();
            int nextValue = nextKeyframe.getIntValue();
            float prevFraction = prevKeyframe.getFraction();
            float nextFraction = nextKeyframe.getFraction();
            TimeInterpolator interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            float intervalFraction = (fraction - prevFraction) / (nextFraction - prevFraction);
            return this.mEvaluator == null ? prevValue + ((int) (intervalFraction * (nextValue - prevValue))) : ((Number) this.mEvaluator.evaluate(intervalFraction, Integer.valueOf(prevValue), Integer.valueOf(nextValue))).intValue();
        } else if (fraction >= 1.0f) {
            Keyframe.IntKeyframe prevKeyframe2 = (Keyframe.IntKeyframe) this.mKeyframes.get(this.mNumKeyframes - 2);
            Keyframe.IntKeyframe nextKeyframe2 = (Keyframe.IntKeyframe) this.mKeyframes.get(this.mNumKeyframes - 1);
            int prevValue2 = prevKeyframe2.getIntValue();
            int nextValue2 = nextKeyframe2.getIntValue();
            float prevFraction2 = prevKeyframe2.getFraction();
            float nextFraction2 = nextKeyframe2.getFraction();
            TimeInterpolator interpolator2 = nextKeyframe2.getInterpolator();
            if (interpolator2 != null) {
                fraction = interpolator2.getInterpolation(fraction);
            }
            float intervalFraction2 = (fraction - prevFraction2) / (nextFraction2 - prevFraction2);
            return this.mEvaluator == null ? prevValue2 + ((int) (intervalFraction2 * (nextValue2 - prevValue2))) : ((Number) this.mEvaluator.evaluate(intervalFraction2, Integer.valueOf(prevValue2), Integer.valueOf(nextValue2))).intValue();
        } else {
            Keyframe.IntKeyframe prevKeyframe3 = (Keyframe.IntKeyframe) this.mKeyframes.get(0);
            for (int i = 1; i < this.mNumKeyframes; i++) {
                Keyframe.IntKeyframe nextKeyframe3 = (Keyframe.IntKeyframe) this.mKeyframes.get(i);
                if (fraction < nextKeyframe3.getFraction()) {
                    TimeInterpolator interpolator3 = nextKeyframe3.getInterpolator();
                    if (interpolator3 != null) {
                        fraction = interpolator3.getInterpolation(fraction);
                    }
                    float intervalFraction3 = (fraction - prevKeyframe3.getFraction()) / (nextKeyframe3.getFraction() - prevKeyframe3.getFraction());
                    int prevValue3 = prevKeyframe3.getIntValue();
                    int nextValue3 = nextKeyframe3.getIntValue();
                    return this.mEvaluator == null ? prevValue3 + ((int) (intervalFraction3 * (nextValue3 - prevValue3))) : ((Number) this.mEvaluator.evaluate(intervalFraction3, Integer.valueOf(prevValue3), Integer.valueOf(nextValue3))).intValue();
                }
                prevKeyframe3 = nextKeyframe3;
            }
            return ((Number) this.mKeyframes.get(this.mNumKeyframes - 1).getValue()).intValue();
        }
    }
}