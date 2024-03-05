package com.android.internal.widget;

import android.content.res.Resources;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AbsListView;

/* loaded from: AutoScrollHelper.class */
public abstract class AutoScrollHelper implements View.OnTouchListener {
    public static final float RELATIVE_UNSPECIFIED = 0.0f;
    public static final float NO_MAX = Float.MAX_VALUE;
    public static final float NO_MIN = 0.0f;
    public static final int EDGE_TYPE_INSIDE = 0;
    public static final int EDGE_TYPE_INSIDE_EXTEND = 1;
    public static final int EDGE_TYPE_OUTSIDE = 2;
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private final View mTarget;
    private Runnable mRunnable;
    private int mEdgeType;
    private int mActivationDelay;
    private boolean mAlreadyDelayed;
    private boolean mNeedsReset;
    private boolean mNeedsCancel;
    private boolean mAnimating;
    private boolean mEnabled;
    private boolean mExclusive;
    private static final int DEFAULT_EDGE_TYPE = 1;
    private static final int DEFAULT_MINIMUM_VELOCITY_DIPS = 315;
    private static final int DEFAULT_MAXIMUM_VELOCITY_DIPS = 1575;
    private static final float DEFAULT_MAXIMUM_EDGE = Float.MAX_VALUE;
    private static final float DEFAULT_RELATIVE_EDGE = 0.2f;
    private static final float DEFAULT_RELATIVE_VELOCITY = 1.0f;
    private static final int DEFAULT_ACTIVATION_DELAY = ViewConfiguration.getTapTimeout();
    private static final int DEFAULT_RAMP_UP_DURATION = 500;
    private static final int DEFAULT_RAMP_DOWN_DURATION = 500;
    private final ClampedScroller mScroller = new ClampedScroller();
    private final Interpolator mEdgeInterpolator = new AccelerateInterpolator();
    private float[] mRelativeEdges = {0.0f, 0.0f};
    private float[] mMaximumEdges = {Float.MAX_VALUE, Float.MAX_VALUE};
    private float[] mRelativeVelocity = {0.0f, 0.0f};
    private float[] mMinimumVelocity = {0.0f, 0.0f};
    private float[] mMaximumVelocity = {Float.MAX_VALUE, Float.MAX_VALUE};

    public abstract void scrollTargetBy(int i, int i2);

    public abstract boolean canTargetScrollHorizontally(int i);

    public abstract boolean canTargetScrollVertically(int i);

    public AutoScrollHelper(View target) {
        this.mTarget = target;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int maxVelocity = (int) ((1575.0f * metrics.density) + 0.5f);
        int minVelocity = (int) ((315.0f * metrics.density) + 0.5f);
        setMaximumVelocity(maxVelocity, maxVelocity);
        setMinimumVelocity(minVelocity, minVelocity);
        setEdgeType(1);
        setMaximumEdges(Float.MAX_VALUE, Float.MAX_VALUE);
        setRelativeEdges(0.2f, 0.2f);
        setRelativeVelocity(1.0f, 1.0f);
        setActivationDelay(DEFAULT_ACTIVATION_DELAY);
        setRampUpDuration(500);
        setRampDownDuration(500);
    }

    public AutoScrollHelper setEnabled(boolean enabled) {
        if (this.mEnabled && !enabled) {
            requestStop();
        }
        this.mEnabled = enabled;
        return this;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public AutoScrollHelper setExclusive(boolean exclusive) {
        this.mExclusive = exclusive;
        return this;
    }

    public boolean isExclusive() {
        return this.mExclusive;
    }

    public AutoScrollHelper setMaximumVelocity(float horizontalMax, float verticalMax) {
        this.mMaximumVelocity[0] = horizontalMax / 1000.0f;
        this.mMaximumVelocity[1] = verticalMax / 1000.0f;
        return this;
    }

    public AutoScrollHelper setMinimumVelocity(float horizontalMin, float verticalMin) {
        this.mMinimumVelocity[0] = horizontalMin / 1000.0f;
        this.mMinimumVelocity[1] = verticalMin / 1000.0f;
        return this;
    }

    public AutoScrollHelper setRelativeVelocity(float horizontal, float vertical) {
        this.mRelativeVelocity[0] = horizontal / 1000.0f;
        this.mRelativeVelocity[1] = vertical / 1000.0f;
        return this;
    }

    public AutoScrollHelper setEdgeType(int type) {
        this.mEdgeType = type;
        return this;
    }

    public AutoScrollHelper setRelativeEdges(float horizontal, float vertical) {
        this.mRelativeEdges[0] = horizontal;
        this.mRelativeEdges[1] = vertical;
        return this;
    }

    public AutoScrollHelper setMaximumEdges(float horizontalMax, float verticalMax) {
        this.mMaximumEdges[0] = horizontalMax;
        this.mMaximumEdges[1] = verticalMax;
        return this;
    }

    public AutoScrollHelper setActivationDelay(int delayMillis) {
        this.mActivationDelay = delayMillis;
        return this;
    }

    public AutoScrollHelper setRampUpDuration(int durationMillis) {
        this.mScroller.setRampUpDuration(durationMillis);
        return this;
    }

    public AutoScrollHelper setRampDownDuration(int durationMillis) {
        this.mScroller.setRampDownDuration(durationMillis);
        return this;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        if (!this.mEnabled) {
            return false;
        }
        int action = event.getActionMasked();
        switch (action) {
            case 0:
                this.mNeedsCancel = true;
                this.mAlreadyDelayed = false;
                float xTargetVelocity = computeTargetVelocity(0, event.getX(), v.getWidth(), this.mTarget.getWidth());
                float yTargetVelocity = computeTargetVelocity(1, event.getY(), v.getHeight(), this.mTarget.getHeight());
                this.mScroller.setTargetVelocity(xTargetVelocity, yTargetVelocity);
                if (!this.mAnimating && shouldAnimate()) {
                    startAnimating();
                    break;
                }
                break;
            case 1:
            case 3:
                requestStop();
                break;
            case 2:
                float xTargetVelocity2 = computeTargetVelocity(0, event.getX(), v.getWidth(), this.mTarget.getWidth());
                float yTargetVelocity2 = computeTargetVelocity(1, event.getY(), v.getHeight(), this.mTarget.getHeight());
                this.mScroller.setTargetVelocity(xTargetVelocity2, yTargetVelocity2);
                if (!this.mAnimating) {
                    startAnimating();
                    break;
                }
                break;
        }
        return this.mExclusive && this.mAnimating;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldAnimate() {
        ClampedScroller scroller = this.mScroller;
        int verticalDirection = scroller.getVerticalDirection();
        int horizontalDirection = scroller.getHorizontalDirection();
        return (verticalDirection != 0 && canTargetScrollVertically(verticalDirection)) || (horizontalDirection != 0 && canTargetScrollHorizontally(horizontalDirection));
    }

    private void startAnimating() {
        if (this.mRunnable == null) {
            this.mRunnable = new ScrollAnimationRunnable();
        }
        this.mAnimating = true;
        this.mNeedsReset = true;
        if (!this.mAlreadyDelayed && this.mActivationDelay > 0) {
            this.mTarget.postOnAnimationDelayed(this.mRunnable, this.mActivationDelay);
        } else {
            this.mRunnable.run();
        }
        this.mAlreadyDelayed = true;
    }

    private void requestStop() {
        if (this.mNeedsReset) {
            this.mAnimating = false;
        } else {
            this.mScroller.requestStop();
        }
    }

    private float computeTargetVelocity(int direction, float coordinate, float srcSize, float dstSize) {
        float relativeEdge = this.mRelativeEdges[direction];
        float maximumEdge = this.mMaximumEdges[direction];
        float value = getEdgeValue(relativeEdge, srcSize, maximumEdge, coordinate);
        if (value == 0.0f) {
            return 0.0f;
        }
        float relativeVelocity = this.mRelativeVelocity[direction];
        float minimumVelocity = this.mMinimumVelocity[direction];
        float maximumVelocity = this.mMaximumVelocity[direction];
        float targetVelocity = relativeVelocity * dstSize;
        if (value > 0.0f) {
            return constrain(value * targetVelocity, minimumVelocity, maximumVelocity);
        }
        return -constrain((-value) * targetVelocity, minimumVelocity, maximumVelocity);
    }

    private float getEdgeValue(float relativeValue, float size, float maxValue, float current) {
        float interpolated;
        float edgeSize = constrain(relativeValue * size, 0.0f, maxValue);
        float valueLeading = constrainEdgeValue(current, edgeSize);
        float valueTrailing = constrainEdgeValue(size - current, edgeSize);
        float value = valueTrailing - valueLeading;
        if (value < 0.0f) {
            interpolated = -this.mEdgeInterpolator.getInterpolation(-value);
        } else if (value > 0.0f) {
            interpolated = this.mEdgeInterpolator.getInterpolation(value);
        } else {
            return 0.0f;
        }
        return constrain(interpolated, -1.0f, 1.0f);
    }

    private float constrainEdgeValue(float current, float leading) {
        if (leading == 0.0f) {
            return 0.0f;
        }
        switch (this.mEdgeType) {
            case 0:
            case 1:
                if (current < leading) {
                    if (current >= 0.0f) {
                        return 1.0f - (current / leading);
                    }
                    if (this.mAnimating && this.mEdgeType == 1) {
                        return 1.0f;
                    }
                    return 0.0f;
                }
                return 0.0f;
            case 2:
                if (current < 0.0f) {
                    return current / (-leading);
                }
                return 0.0f;
            default:
                return 0.0f;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int constrain(int value, int min, int max) {
        if (value > max) {
            return max;
        }
        if (value < min) {
            return min;
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float constrain(float value, float min, float max) {
        if (value > max) {
            return max;
        }
        if (value < min) {
            return min;
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelTargetTouch() {
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent cancel = MotionEvent.obtain(eventTime, eventTime, 3, 0.0f, 0.0f, 0);
        this.mTarget.onTouchEvent(cancel);
        cancel.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AutoScrollHelper$ScrollAnimationRunnable.class */
    public class ScrollAnimationRunnable implements Runnable {
        private ScrollAnimationRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AutoScrollHelper.this.mAnimating) {
                if (AutoScrollHelper.this.mNeedsReset) {
                    AutoScrollHelper.this.mNeedsReset = false;
                    AutoScrollHelper.this.mScroller.start();
                }
                ClampedScroller scroller = AutoScrollHelper.this.mScroller;
                if (scroller.isFinished() || !AutoScrollHelper.this.shouldAnimate()) {
                    AutoScrollHelper.this.mAnimating = false;
                    return;
                }
                if (AutoScrollHelper.this.mNeedsCancel) {
                    AutoScrollHelper.this.mNeedsCancel = false;
                    AutoScrollHelper.this.cancelTargetTouch();
                }
                scroller.computeScrollDelta();
                int deltaX = scroller.getDeltaX();
                int deltaY = scroller.getDeltaY();
                AutoScrollHelper.this.scrollTargetBy(deltaX, deltaY);
                AutoScrollHelper.this.mTarget.postOnAnimation(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AutoScrollHelper$ClampedScroller.class */
    public static class ClampedScroller {
        private int mRampUpDuration;
        private int mRampDownDuration;
        private float mTargetVelocityX;
        private float mTargetVelocityY;
        private float mStopValue;
        private int mEffectiveRampDown;
        private long mStartTime = Long.MIN_VALUE;
        private long mStopTime = -1;
        private long mDeltaTime = 0;
        private int mDeltaX = 0;
        private int mDeltaY = 0;

        public void setRampUpDuration(int durationMillis) {
            this.mRampUpDuration = durationMillis;
        }

        public void setRampDownDuration(int durationMillis) {
            this.mRampDownDuration = durationMillis;
        }

        public void start() {
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mStopTime = -1L;
            this.mDeltaTime = this.mStartTime;
            this.mStopValue = 0.5f;
            this.mDeltaX = 0;
            this.mDeltaY = 0;
        }

        public void requestStop() {
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            this.mEffectiveRampDown = AutoScrollHelper.constrain((int) (currentTime - this.mStartTime), 0, this.mRampDownDuration);
            this.mStopValue = getValueAt(currentTime);
            this.mStopTime = currentTime;
        }

        public boolean isFinished() {
            return this.mStopTime > 0 && AnimationUtils.currentAnimationTimeMillis() > this.mStopTime + ((long) this.mEffectiveRampDown);
        }

        private float getValueAt(long currentTime) {
            if (currentTime < this.mStartTime) {
                return 0.0f;
            }
            if (this.mStopTime < 0 || currentTime < this.mStopTime) {
                long elapsedSinceStart = currentTime - this.mStartTime;
                return 0.5f * AutoScrollHelper.constrain(((float) elapsedSinceStart) / this.mRampUpDuration, 0.0f, 1.0f);
            }
            long elapsedSinceEnd = currentTime - this.mStopTime;
            return (1.0f - this.mStopValue) + (this.mStopValue * AutoScrollHelper.constrain(((float) elapsedSinceEnd) / this.mEffectiveRampDown, 0.0f, 1.0f));
        }

        private float interpolateValue(float value) {
            return ((-4.0f) * value * value) + (4.0f * value);
        }

        public void computeScrollDelta() {
            if (this.mDeltaTime == 0) {
                throw new RuntimeException("Cannot compute scroll delta before calling start()");
            }
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            float value = getValueAt(currentTime);
            float scale = interpolateValue(value);
            long elapsedSinceDelta = currentTime - this.mDeltaTime;
            this.mDeltaTime = currentTime;
            this.mDeltaX = (int) (((float) elapsedSinceDelta) * scale * this.mTargetVelocityX);
            this.mDeltaY = (int) (((float) elapsedSinceDelta) * scale * this.mTargetVelocityY);
        }

        public void setTargetVelocity(float x, float y) {
            this.mTargetVelocityX = x;
            this.mTargetVelocityY = y;
        }

        public int getHorizontalDirection() {
            return (int) (this.mTargetVelocityX / Math.abs(this.mTargetVelocityX));
        }

        public int getVerticalDirection() {
            return (int) (this.mTargetVelocityY / Math.abs(this.mTargetVelocityY));
        }

        public int getDeltaX() {
            return this.mDeltaX;
        }

        public int getDeltaY() {
            return this.mDeltaY;
        }
    }

    /* loaded from: AutoScrollHelper$AbsListViewAutoScroller.class */
    public static class AbsListViewAutoScroller extends AutoScrollHelper {
        private final AbsListView mTarget;

        public AbsListViewAutoScroller(AbsListView target) {
            super(target);
            this.mTarget = target;
        }

        @Override // com.android.internal.widget.AutoScrollHelper
        public void scrollTargetBy(int deltaX, int deltaY) {
            this.mTarget.scrollListBy(deltaY);
        }

        @Override // com.android.internal.widget.AutoScrollHelper
        public boolean canTargetScrollHorizontally(int direction) {
            return false;
        }

        @Override // com.android.internal.widget.AutoScrollHelper
        public boolean canTargetScrollVertically(int direction) {
            AbsListView target = this.mTarget;
            int itemCount = target.getCount();
            int childCount = target.getChildCount();
            int firstPosition = target.getFirstVisiblePosition();
            int lastPosition = firstPosition + childCount;
            if (direction > 0) {
                if (lastPosition >= itemCount) {
                    View lastView = target.getChildAt(childCount - 1);
                    if (lastView.getBottom() <= target.getHeight()) {
                        return false;
                    }
                    return true;
                }
                return true;
            } else if (direction < 0) {
                if (firstPosition <= 0) {
                    View firstView = target.getChildAt(0);
                    if (firstView.getTop() >= 0) {
                        return false;
                    }
                    return true;
                }
                return true;
            } else {
                return false;
            }
        }
    }
}