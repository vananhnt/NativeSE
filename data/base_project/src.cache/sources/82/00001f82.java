package com.android.server.power;

import android.animation.ValueAnimator;
import android.util.IntProperty;
import android.view.Choreographer;

/* loaded from: RampAnimator.class */
final class RampAnimator<T> {
    private final T mObject;
    private final IntProperty<T> mProperty;
    private int mCurrentValue;
    private int mTargetValue;
    private int mRate;
    private boolean mAnimating;
    private float mAnimatedValue;
    private long mLastFrameTimeNanos;
    private boolean mFirstTime = true;
    private final Runnable mCallback = new Runnable() { // from class: com.android.server.power.RampAnimator.1
        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.lang.Runnable
        public void run() {
            long frameTimeNanos = RampAnimator.this.mChoreographer.getFrameTimeNanos();
            float timeDelta = ((float) (frameTimeNanos - RampAnimator.this.mLastFrameTimeNanos)) * 1.0E-9f;
            RampAnimator.this.mLastFrameTimeNanos = frameTimeNanos;
            float scale = ValueAnimator.getDurationScale();
            if (scale == 0.0f) {
                RampAnimator.this.mAnimatedValue = RampAnimator.this.mTargetValue;
            } else {
                float amount = (timeDelta * RampAnimator.this.mRate) / scale;
                if (RampAnimator.this.mTargetValue > RampAnimator.this.mCurrentValue) {
                    RampAnimator.this.mAnimatedValue = Math.min(RampAnimator.this.mAnimatedValue + amount, RampAnimator.this.mTargetValue);
                } else {
                    RampAnimator.this.mAnimatedValue = Math.max(RampAnimator.this.mAnimatedValue - amount, RampAnimator.this.mTargetValue);
                }
            }
            int oldCurrentValue = RampAnimator.this.mCurrentValue;
            RampAnimator.this.mCurrentValue = Math.round(RampAnimator.this.mAnimatedValue);
            if (oldCurrentValue != RampAnimator.this.mCurrentValue) {
                RampAnimator.this.mProperty.setValue(RampAnimator.this.mObject, RampAnimator.this.mCurrentValue);
            }
            if (RampAnimator.this.mTargetValue != RampAnimator.this.mCurrentValue) {
                RampAnimator.this.postCallback();
            } else {
                RampAnimator.this.mAnimating = false;
            }
        }
    };
    private final Choreographer mChoreographer = Choreographer.getInstance();

    public RampAnimator(T object, IntProperty<T> property) {
        this.mObject = object;
        this.mProperty = property;
    }

    public boolean animateTo(int target, int rate) {
        if (this.mFirstTime) {
            this.mFirstTime = false;
            this.mProperty.setValue(this.mObject, target);
            this.mCurrentValue = target;
            return true;
        }
        if (!this.mAnimating || rate > this.mRate || ((target <= this.mCurrentValue && this.mCurrentValue <= this.mTargetValue) || (this.mTargetValue <= this.mCurrentValue && this.mCurrentValue <= target))) {
            this.mRate = rate;
        }
        boolean changed = this.mTargetValue != target;
        this.mTargetValue = target;
        if (!this.mAnimating && target != this.mCurrentValue) {
            this.mAnimating = true;
            this.mAnimatedValue = this.mCurrentValue;
            this.mLastFrameTimeNanos = System.nanoTime();
            postCallback();
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postCallback() {
        this.mChoreographer.postCallback(1, this.mCallback, null);
    }
}