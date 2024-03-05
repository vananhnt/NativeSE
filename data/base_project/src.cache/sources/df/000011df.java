package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

/* loaded from: SwipeRefreshLayout.class */
public class SwipeRefreshLayout extends ViewGroup {
    private static final int ALPHA_ANIMATION_DURATION = 300;
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int CIRCLE_BG_LIGHT = -328966;
    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0f;
    public static final int DEFAULT = 1;
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private static final float DRAG_RATE = 0.5f;
    private static final int INVALID_POINTER = -1;
    public static final int LARGE = 0;
    private static final int MAX_ALPHA = 255;
    private static final float MAX_PROGRESS_ANGLE = 0.8f;
    private static final int SCALE_DOWN_DURATION = 150;
    private static final int STARTING_PROGRESS_ALPHA = 76;
    private int mActivePointerId;
    private Animation mAlphaMaxAnimation;
    private Animation mAlphaStartAnimation;
    private final Animation mAnimateToCorrectPosition;
    private final Animation mAnimateToStartPosition;
    private int mCircleHeight;
    private CircleImageView mCircleView;
    private int mCircleViewIndex;
    private int mCircleWidth;
    private int mCurrentTargetOffsetTop;
    private final DecelerateInterpolator mDecelerateInterpolator;
    protected int mFrom;
    private float mInitialMotionY;
    private boolean mIsBeingDragged;
    private OnRefreshListener mListener;
    private int mMediumAnimationDuration;
    private boolean mNotify;
    private boolean mOriginalOffsetCalculated;
    protected int mOriginalOffsetTop;
    private MaterialProgressDrawable mProgress;
    private Animation.AnimationListener mRefreshListener;
    private boolean mRefreshing;
    private boolean mReturningToStart;
    private boolean mScale;
    private Animation mScaleAnimation;
    private Animation mScaleDownAnimation;
    private Animation mScaleDownToStartAnimation;
    private float mSpinnerFinalOffset;
    private float mStartingScale;
    private View mTarget;
    private float mTotalDragDistance;
    private int mTouchSlop;
    private boolean mUsingCustomStart;
    private static final String LOG_TAG = SwipeRefreshLayout.class.getSimpleName();
    private static final int[] LAYOUT_ATTRS = {16842766};

    /* loaded from: SwipeRefreshLayout$OnRefreshListener.class */
    public interface OnRefreshListener {
        void onRefresh();
    }

    public SwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRefreshing = false;
        this.mTotalDragDistance = -1.0f;
        this.mOriginalOffsetCalculated = false;
        this.mActivePointerId = -1;
        this.mCircleViewIndex = -1;
        this.mRefreshListener = new Animation.AnimationListener(this) { // from class: android.support.v4.widget.SwipeRefreshLayout.1
            final SwipeRefreshLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                if (this.this$0.mRefreshing) {
                    this.this$0.mProgress.setAlpha(255);
                    this.this$0.mProgress.start();
                    if (this.this$0.mNotify && this.this$0.mListener != null) {
                        this.this$0.mListener.onRefresh();
                    }
                } else {
                    this.this$0.mProgress.stop();
                    this.this$0.mCircleView.setVisibility(8);
                    this.this$0.setColorViewAlpha(255);
                    if (this.this$0.mScale) {
                        this.this$0.setAnimationProgress(0.0f);
                    } else {
                        SwipeRefreshLayout swipeRefreshLayout = this.this$0;
                        swipeRefreshLayout.setTargetOffsetTopAndBottom(swipeRefreshLayout.mOriginalOffsetTop - this.this$0.mCurrentTargetOffsetTop, true);
                    }
                }
                SwipeRefreshLayout swipeRefreshLayout2 = this.this$0;
                swipeRefreshLayout2.mCurrentTargetOffsetTop = swipeRefreshLayout2.mCircleView.getTop();
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }
        };
        this.mAnimateToCorrectPosition = new Animation(this) { // from class: android.support.v4.widget.SwipeRefreshLayout.6
            final SwipeRefreshLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.animation.Animation
            public void applyTransformation(float f, Transformation transformation) {
                int abs = !this.this$0.mUsingCustomStart ? (int) (this.this$0.mSpinnerFinalOffset - Math.abs(this.this$0.mOriginalOffsetTop)) : (int) this.this$0.mSpinnerFinalOffset;
                this.this$0.setTargetOffsetTopAndBottom((this.this$0.mFrom + ((int) ((abs - this.this$0.mFrom) * f))) - this.this$0.mCircleView.getTop(), false);
            }
        };
        this.mAnimateToStartPosition = new Animation(this) { // from class: android.support.v4.widget.SwipeRefreshLayout.7
            final SwipeRefreshLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.animation.Animation
            public void applyTransformation(float f, Transformation transformation) {
                this.this$0.moveToStart(f);
            }
        };
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMediumAnimationDuration = getResources().getInteger(17694721);
        setWillNotDraw(false);
        this.mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, LAYOUT_ATTRS);
        setEnabled(obtainStyledAttributes.getBoolean(0, true));
        obtainStyledAttributes.recycle();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.mCircleWidth = (int) (displayMetrics.density * 40.0f);
        this.mCircleHeight = (int) (displayMetrics.density * 40.0f);
        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        this.mSpinnerFinalOffset = displayMetrics.density * 64.0f;
        this.mTotalDragDistance = this.mSpinnerFinalOffset;
    }

    private void animateOffsetToCorrectPosition(int i, Animation.AnimationListener animationListener) {
        this.mFrom = i;
        this.mAnimateToCorrectPosition.reset();
        this.mAnimateToCorrectPosition.setDuration(200L);
        this.mAnimateToCorrectPosition.setInterpolator(this.mDecelerateInterpolator);
        if (animationListener != null) {
            this.mCircleView.setAnimationListener(animationListener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int i, Animation.AnimationListener animationListener) {
        if (this.mScale) {
            startScaleDownReturnToStartAnimation(i, animationListener);
            return;
        }
        this.mFrom = i;
        this.mAnimateToStartPosition.reset();
        this.mAnimateToStartPosition.setDuration(200L);
        this.mAnimateToStartPosition.setInterpolator(this.mDecelerateInterpolator);
        if (animationListener != null) {
            this.mCircleView.setAnimationListener(animationListener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mAnimateToStartPosition);
    }

    private void createProgressView() {
        this.mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, 20.0f);
        this.mProgress = new MaterialProgressDrawable(getContext(), this);
        this.mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        this.mCircleView.setImageDrawable(this.mProgress);
        this.mCircleView.setVisibility(8);
        addView(this.mCircleView);
    }

    private void ensureTarget() {
        if (this.mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (!childAt.equals(this.mCircleView)) {
                    this.mTarget = childAt;
                    return;
                }
            }
        }
    }

    private float getMotionEventY(MotionEvent motionEvent, int i) {
        int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, i);
        if (findPointerIndex < 0) {
            return -1.0f;
        }
        return MotionEventCompat.getY(motionEvent, findPointerIndex);
    }

    private boolean isAlphaUsedForScale() {
        return Build.VERSION.SDK_INT < 11;
    }

    private boolean isAnimationRunning(Animation animation) {
        return (animation == null || !animation.hasStarted() || animation.hasEnded()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveToStart(float f) {
        int i = this.mFrom;
        setTargetOffsetTopAndBottom((i + ((int) ((this.mOriginalOffsetTop - i) * f))) - this.mCircleView.getTop(), false);
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (MotionEventCompat.getPointerId(motionEvent, actionIndex) == this.mActivePointerId) {
            this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex == 0 ? 1 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAnimationProgress(float f) {
        if (isAlphaUsedForScale()) {
            setColorViewAlpha((int) (255.0f * f));
            return;
        }
        ViewCompat.setScaleX(this.mCircleView, f);
        ViewCompat.setScaleY(this.mCircleView, f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setColorViewAlpha(int i) {
        this.mCircleView.getBackground().setAlpha(i);
        this.mProgress.setAlpha(i);
    }

    private void setRefreshing(boolean z, boolean z2) {
        if (this.mRefreshing != z) {
            this.mNotify = z2;
            ensureTarget();
            this.mRefreshing = z;
            if (this.mRefreshing) {
                animateOffsetToCorrectPosition(this.mCurrentTargetOffsetTop, this.mRefreshListener);
            } else {
                startScaleDownAnimation(this.mRefreshListener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTargetOffsetTopAndBottom(int i, boolean z) {
        this.mCircleView.bringToFront();
        this.mCircleView.offsetTopAndBottom(i);
        this.mCurrentTargetOffsetTop = this.mCircleView.getTop();
        if (!z || Build.VERSION.SDK_INT >= 11) {
            return;
        }
        invalidate();
    }

    private Animation startAlphaAnimation(int i, int i2) {
        if (this.mScale && isAlphaUsedForScale()) {
            return null;
        }
        Animation animation = new Animation(this, i, i2) { // from class: android.support.v4.widget.SwipeRefreshLayout.4
            final SwipeRefreshLayout this$0;
            final int val$endingAlpha;
            final int val$startingAlpha;

            {
                this.this$0 = this;
                this.val$startingAlpha = i;
                this.val$endingAlpha = i2;
            }

            @Override // android.view.animation.Animation
            public void applyTransformation(float f, Transformation transformation) {
                MaterialProgressDrawable materialProgressDrawable = this.this$0.mProgress;
                int i3 = this.val$startingAlpha;
                materialProgressDrawable.setAlpha((int) (i3 + ((this.val$endingAlpha - i3) * f)));
            }
        };
        animation.setDuration(300L);
        this.mCircleView.setAnimationListener(null);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(animation);
        return animation;
    }

    private void startProgressAlphaMaxAnimation() {
        this.mAlphaMaxAnimation = startAlphaAnimation(this.mProgress.getAlpha(), 255);
    }

    private void startProgressAlphaStartAnimation() {
        this.mAlphaStartAnimation = startAlphaAnimation(this.mProgress.getAlpha(), 76);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startScaleDownAnimation(Animation.AnimationListener animationListener) {
        this.mScaleDownAnimation = new Animation(this) { // from class: android.support.v4.widget.SwipeRefreshLayout.3
            final SwipeRefreshLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.animation.Animation
            public void applyTransformation(float f, Transformation transformation) {
                this.this$0.setAnimationProgress(1.0f - f);
            }
        };
        this.mScaleDownAnimation.setDuration(150L);
        this.mCircleView.setAnimationListener(animationListener);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownAnimation);
    }

    private void startScaleDownReturnToStartAnimation(int i, Animation.AnimationListener animationListener) {
        this.mFrom = i;
        if (isAlphaUsedForScale()) {
            this.mStartingScale = this.mProgress.getAlpha();
        } else {
            this.mStartingScale = ViewCompat.getScaleX(this.mCircleView);
        }
        this.mScaleDownToStartAnimation = new Animation(this) { // from class: android.support.v4.widget.SwipeRefreshLayout.8
            final SwipeRefreshLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.animation.Animation
            public void applyTransformation(float f, Transformation transformation) {
                this.this$0.setAnimationProgress(this.this$0.mStartingScale + ((-this.this$0.mStartingScale) * f));
                this.this$0.moveToStart(f);
            }
        };
        this.mScaleDownToStartAnimation.setDuration(150L);
        if (animationListener != null) {
            this.mCircleView.setAnimationListener(animationListener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownToStartAnimation);
    }

    private void startScaleUpAnimation(Animation.AnimationListener animationListener) {
        this.mCircleView.setVisibility(0);
        if (Build.VERSION.SDK_INT >= 11) {
            this.mProgress.setAlpha(255);
        }
        this.mScaleAnimation = new Animation(this) { // from class: android.support.v4.widget.SwipeRefreshLayout.2
            final SwipeRefreshLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.animation.Animation
            public void applyTransformation(float f, Transformation transformation) {
                this.this$0.setAnimationProgress(f);
            }
        };
        this.mScaleAnimation.setDuration(this.mMediumAnimationDuration);
        if (animationListener != null) {
            this.mCircleView.setAnimationListener(animationListener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleAnimation);
    }

    public boolean canChildScrollUp() {
        if (Build.VERSION.SDK_INT < 14) {
            View view = this.mTarget;
            boolean z = true;
            if (view instanceof AbsListView) {
                AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            }
            if (view.getScrollY() <= 0) {
                z = false;
            }
            return z;
        }
        return ViewCompat.canScrollVertically(this.mTarget, -1);
    }

    @Override // android.view.ViewGroup
    protected int getChildDrawingOrder(int i, int i2) {
        int i3 = this.mCircleViewIndex;
        return i3 < 0 ? i2 : i2 == i - 1 ? i3 : i2 >= i3 ? i2 + 1 : i2;
    }

    public boolean isRefreshing() {
        return this.mRefreshing;
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x00b4  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00c0  */
    @Override // android.view.ViewGroup
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r5) {
        /*
            Method dump skipped, instructions count: 262
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.SwipeRefreshLayout.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (this.mTarget == null) {
            ensureTarget();
        }
        if (this.mTarget == null) {
            return;
        }
        View view = this.mTarget;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        view.layout(paddingLeft, paddingTop, paddingLeft + ((measuredWidth - getPaddingLeft()) - getPaddingRight()), paddingTop + ((measuredHeight - getPaddingTop()) - getPaddingBottom()));
        int measuredWidth2 = this.mCircleView.getMeasuredWidth();
        int measuredHeight2 = this.mCircleView.getMeasuredHeight();
        CircleImageView circleImageView = this.mCircleView;
        int i5 = measuredWidth / 2;
        int i6 = measuredWidth2 / 2;
        int i7 = this.mCurrentTargetOffsetTop;
        circleImageView.layout(i5 - i6, i7, (measuredWidth / 2) + (measuredWidth2 / 2), i7 + measuredHeight2);
    }

    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mTarget == null) {
            ensureTarget();
        }
        View view = this.mTarget;
        if (view == null) {
            return;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), 1073741824), View.MeasureSpec.makeMeasureSpec((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), 1073741824));
        this.mCircleView.measure(View.MeasureSpec.makeMeasureSpec(this.mCircleWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mCircleHeight, 1073741824));
        if (!this.mUsingCustomStart && !this.mOriginalOffsetCalculated) {
            this.mOriginalOffsetCalculated = true;
            int i3 = -this.mCircleView.getMeasuredHeight();
            this.mOriginalOffsetTop = i3;
            this.mCurrentTargetOffsetTop = i3;
        }
        this.mCircleViewIndex = -1;
        for (int i4 = 0; i4 < getChildCount(); i4++) {
            if (getChildAt(i4) == this.mCircleView) {
                this.mCircleViewIndex = i4;
                return;
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        if (this.mReturningToStart && actionMasked == 0) {
            this.mReturningToStart = false;
        }
        if (!isEnabled() || this.mReturningToStart || canChildScrollUp()) {
            return false;
        }
        switch (actionMasked) {
            case 0:
                this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                this.mIsBeingDragged = false;
                return true;
            case 1:
            case 3:
                int i = this.mActivePointerId;
                if (i == -1) {
                    if (actionMasked == 1) {
                        Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                        return false;
                    }
                    return false;
                }
                float y = MotionEventCompat.getY(motionEvent, MotionEventCompat.findPointerIndex(motionEvent, i));
                float f = this.mInitialMotionY;
                this.mIsBeingDragged = false;
                if ((y - f) * DRAG_RATE > this.mTotalDragDistance) {
                    setRefreshing(true, true);
                } else {
                    this.mRefreshing = false;
                    this.mProgress.setStartEndTrim(0.0f, 0.0f);
                    Animation.AnimationListener animationListener = null;
                    if (!this.mScale) {
                        animationListener = new Animation.AnimationListener(this) { // from class: android.support.v4.widget.SwipeRefreshLayout.5
                            final SwipeRefreshLayout this$0;

                            {
                                this.this$0 = this;
                            }

                            @Override // android.view.animation.Animation.AnimationListener
                            public void onAnimationEnd(Animation animation) {
                                if (this.this$0.mScale) {
                                    return;
                                }
                                this.this$0.startScaleDownAnimation(null);
                            }

                            @Override // android.view.animation.Animation.AnimationListener
                            public void onAnimationRepeat(Animation animation) {
                            }

                            @Override // android.view.animation.Animation.AnimationListener
                            public void onAnimationStart(Animation animation) {
                            }
                        };
                    }
                    animateOffsetToStartPosition(this.mCurrentTargetOffsetTop, animationListener);
                    this.mProgress.showArrow(false);
                }
                this.mActivePointerId = -1;
                return false;
            case 2:
                int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId);
                if (findPointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }
                float y2 = (MotionEventCompat.getY(motionEvent, findPointerIndex) - this.mInitialMotionY) * DRAG_RATE;
                if (this.mIsBeingDragged) {
                    this.mProgress.showArrow(true);
                    float f2 = y2 / this.mTotalDragDistance;
                    if (f2 < 0.0f) {
                        return false;
                    }
                    float min = Math.min(1.0f, Math.abs(f2));
                    float max = (((float) Math.max(min - 0.4d, 0.0d)) * 5.0f) / 3.0f;
                    float abs = Math.abs(y2);
                    float f3 = this.mTotalDragDistance;
                    float f4 = this.mUsingCustomStart ? this.mSpinnerFinalOffset - this.mOriginalOffsetTop : this.mSpinnerFinalOffset;
                    float max2 = Math.max(0.0f, Math.min(abs - f3, f4 * DECELERATE_INTERPOLATION_FACTOR) / f4);
                    float pow = ((float) ((max2 / 4.0f) - Math.pow(max2 / 4.0f, 2.0d))) * DECELERATE_INTERPOLATION_FACTOR;
                    int i2 = this.mOriginalOffsetTop;
                    int i3 = (int) ((f4 * min) + (f4 * pow * DECELERATE_INTERPOLATION_FACTOR));
                    if (this.mCircleView.getVisibility() != 0) {
                        this.mCircleView.setVisibility(0);
                    }
                    if (!this.mScale) {
                        ViewCompat.setScaleX(this.mCircleView, 1.0f);
                        ViewCompat.setScaleY(this.mCircleView, 1.0f);
                    }
                    float f5 = this.mTotalDragDistance;
                    if (y2 < f5) {
                        if (this.mScale) {
                            setAnimationProgress(y2 / f5);
                        }
                        if (this.mProgress.getAlpha() > 76 && !isAnimationRunning(this.mAlphaStartAnimation)) {
                            startProgressAlphaStartAnimation();
                        }
                        this.mProgress.setStartEndTrim(0.0f, Math.min(0.8f, max * 0.8f));
                        this.mProgress.setArrowScale(Math.min(1.0f, max));
                    } else if (this.mProgress.getAlpha() < 255 && !isAnimationRunning(this.mAlphaMaxAnimation)) {
                        startProgressAlphaMaxAnimation();
                    }
                    this.mProgress.setProgressRotation((((0.4f * max) - 0.25f) + (pow * DECELERATE_INTERPOLATION_FACTOR)) * DRAG_RATE);
                    setTargetOffsetTopAndBottom((i2 + i3) - this.mCurrentTargetOffsetTop, true);
                    return true;
                }
                return true;
            case 4:
            default:
                return true;
            case 5:
                this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, MotionEventCompat.getActionIndex(motionEvent));
                return true;
            case 6:
                onSecondaryPointerUp(motionEvent);
                return true;
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
    }

    @Deprecated
    public void setColorScheme(int... iArr) {
        setColorSchemeResources(iArr);
    }

    public void setColorSchemeColors(int... iArr) {
        ensureTarget();
        this.mProgress.setColorSchemeColors(iArr);
    }

    public void setColorSchemeResources(int... iArr) {
        Resources resources = getResources();
        int[] iArr2 = new int[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            iArr2[i] = resources.getColor(iArr[i]);
        }
        setColorSchemeColors(iArr2);
    }

    public void setDistanceToTriggerSync(int i) {
        this.mTotalDragDistance = i;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mListener = onRefreshListener;
    }

    public void setProgressBackgroundColor(int i) {
        this.mCircleView.setBackgroundColor(i);
        this.mProgress.setBackgroundColor(getResources().getColor(i));
    }

    public void setProgressViewEndTarget(boolean z, int i) {
        this.mSpinnerFinalOffset = i;
        this.mScale = z;
        this.mCircleView.invalidate();
    }

    public void setProgressViewOffset(boolean z, int i, int i2) {
        this.mScale = z;
        this.mCircleView.setVisibility(8);
        this.mCurrentTargetOffsetTop = i;
        this.mOriginalOffsetTop = i;
        this.mSpinnerFinalOffset = i2;
        this.mUsingCustomStart = true;
        this.mCircleView.invalidate();
    }

    public void setRefreshing(boolean z) {
        if (!z || this.mRefreshing == z) {
            setRefreshing(z, false);
            return;
        }
        this.mRefreshing = z;
        setTargetOffsetTopAndBottom((!this.mUsingCustomStart ? (int) (this.mSpinnerFinalOffset + this.mOriginalOffsetTop) : (int) this.mSpinnerFinalOffset) - this.mCurrentTargetOffsetTop, true);
        this.mNotify = false;
        startScaleUpAnimation(this.mRefreshListener);
    }

    public void setSize(int i) {
        if (i == 0 || i == 1) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            if (i == 0) {
                int i2 = (int) (displayMetrics.density * 56.0f);
                this.mCircleWidth = i2;
                this.mCircleHeight = i2;
            } else {
                int i3 = (int) (displayMetrics.density * 40.0f);
                this.mCircleWidth = i3;
                this.mCircleHeight = i3;
            }
            this.mCircleView.setImageDrawable(null);
            this.mProgress.updateSizes(i);
            this.mCircleView.setImageDrawable(this.mProgress);
        }
    }
}