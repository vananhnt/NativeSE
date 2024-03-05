package com.android.internal.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.R;

/* loaded from: SlidingTab.class */
public class SlidingTab extends ViewGroup {
    private static final String LOG_TAG = "SlidingTab";
    private static final boolean DBG = false;
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final float THRESHOLD = 0.6666667f;
    private static final long VIBRATE_SHORT = 30;
    private static final long VIBRATE_LONG = 40;
    private static final int TRACKING_MARGIN = 50;
    private static final int ANIM_DURATION = 250;
    private static final int ANIM_TARGET_TIME = 500;
    private boolean mHoldLeftOnTransition;
    private boolean mHoldRightOnTransition;
    private OnTriggerListener mOnTriggerListener;
    private int mGrabbedState;
    private boolean mTriggered;
    private Vibrator mVibrator;
    private final float mDensity;
    private final int mOrientation;
    private final Slider mLeftSlider;
    private final Slider mRightSlider;
    private Slider mCurrentSlider;
    private boolean mTracking;
    private float mThreshold;
    private Slider mOtherSlider;
    private boolean mAnimating;
    private final Rect mTmpRect;
    private final Animation.AnimationListener mAnimationDoneListener;

    /* loaded from: SlidingTab$OnTriggerListener.class */
    public interface OnTriggerListener {
        public static final int NO_HANDLE = 0;
        public static final int LEFT_HANDLE = 1;
        public static final int RIGHT_HANDLE = 2;

        void onTrigger(View view, int i);

        void onGrabbedStateChange(View view, int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SlidingTab$Slider.class */
    public static class Slider {
        public static final int ALIGN_LEFT = 0;
        public static final int ALIGN_RIGHT = 1;
        public static final int ALIGN_TOP = 2;
        public static final int ALIGN_BOTTOM = 3;
        public static final int ALIGN_UNKNOWN = 4;
        private static final int STATE_NORMAL = 0;
        private static final int STATE_PRESSED = 1;
        private static final int STATE_ACTIVE = 2;
        private final ImageView tab;
        private final TextView text;
        private final ImageView target;
        private int currentState = 0;
        private int alignment = 4;
        private int alignment_value;

        Slider(ViewGroup parent, int tabId, int barId, int targetId) {
            this.tab = new ImageView(parent.getContext());
            this.tab.setBackgroundResource(tabId);
            this.tab.setScaleType(ImageView.ScaleType.CENTER);
            this.tab.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            this.text = new TextView(parent.getContext());
            this.text.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
            this.text.setBackgroundResource(barId);
            this.text.setTextAppearance(parent.getContext(), R.style.TextAppearance_SlidingTabNormal);
            this.target = new ImageView(parent.getContext());
            this.target.setImageResource(targetId);
            this.target.setScaleType(ImageView.ScaleType.CENTER);
            this.target.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            this.target.setVisibility(4);
            parent.addView(this.target);
            parent.addView(this.tab);
            parent.addView(this.text);
        }

        void setIcon(int iconId) {
            this.tab.setImageResource(iconId);
        }

        void setTabBackgroundResource(int tabId) {
            this.tab.setBackgroundResource(tabId);
        }

        void setBarBackgroundResource(int barId) {
            this.text.setBackgroundResource(barId);
        }

        void setHintText(int resId) {
            this.text.setText(resId);
        }

        void hide() {
            boolean horiz = this.alignment == 0 || this.alignment == 1;
            int dx = horiz ? this.alignment == 0 ? this.alignment_value - this.tab.getRight() : this.alignment_value - this.tab.getLeft() : 0;
            int dy = horiz ? 0 : this.alignment == 2 ? this.alignment_value - this.tab.getBottom() : this.alignment_value - this.tab.getTop();
            Animation trans = new TranslateAnimation(0.0f, dx, 0.0f, dy);
            trans.setDuration(250L);
            trans.setFillAfter(true);
            this.tab.startAnimation(trans);
            this.text.startAnimation(trans);
            this.target.setVisibility(4);
        }

        void show(boolean animate) {
            this.text.setVisibility(0);
            this.tab.setVisibility(0);
            if (animate) {
                boolean horiz = this.alignment == 0 || this.alignment == 1;
                int dx = horiz ? this.alignment == 0 ? this.tab.getWidth() : -this.tab.getWidth() : 0;
                int dy = horiz ? 0 : this.alignment == 2 ? this.tab.getHeight() : -this.tab.getHeight();
                Animation trans = new TranslateAnimation(-dx, 0.0f, -dy, 0.0f);
                trans.setDuration(250L);
                this.tab.startAnimation(trans);
                this.text.startAnimation(trans);
            }
        }

        void setState(int state) {
            this.text.setPressed(state == 1);
            this.tab.setPressed(state == 1);
            if (state == 2) {
                int[] activeState = {16842914};
                if (this.text.getBackground().isStateful()) {
                    this.text.getBackground().setState(activeState);
                }
                if (this.tab.getBackground().isStateful()) {
                    this.tab.getBackground().setState(activeState);
                }
                this.text.setTextAppearance(this.text.getContext(), R.style.TextAppearance_SlidingTabActive);
            } else {
                this.text.setTextAppearance(this.text.getContext(), R.style.TextAppearance_SlidingTabNormal);
            }
            this.currentState = state;
        }

        void showTarget() {
            AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
            alphaAnim.setDuration(500L);
            this.target.startAnimation(alphaAnim);
            this.target.setVisibility(0);
        }

        void reset(boolean animate) {
            setState(0);
            this.text.setVisibility(0);
            this.text.setTextAppearance(this.text.getContext(), R.style.TextAppearance_SlidingTabNormal);
            this.tab.setVisibility(0);
            this.target.setVisibility(4);
            boolean horiz = this.alignment == 0 || this.alignment == 1;
            int dx = horiz ? this.alignment == 0 ? this.alignment_value - this.tab.getLeft() : this.alignment_value - this.tab.getRight() : 0;
            int dy = horiz ? 0 : this.alignment == 2 ? this.alignment_value - this.tab.getTop() : this.alignment_value - this.tab.getBottom();
            if (animate) {
                TranslateAnimation trans = new TranslateAnimation(0.0f, dx, 0.0f, dy);
                trans.setDuration(250L);
                trans.setFillAfter(false);
                this.text.startAnimation(trans);
                this.tab.startAnimation(trans);
                return;
            }
            if (horiz) {
                this.text.offsetLeftAndRight(dx);
                this.tab.offsetLeftAndRight(dx);
            } else {
                this.text.offsetTopAndBottom(dy);
                this.tab.offsetTopAndBottom(dy);
            }
            this.text.clearAnimation();
            this.tab.clearAnimation();
            this.target.clearAnimation();
        }

        void setTarget(int targetId) {
            this.target.setImageResource(targetId);
        }

        void layout(int l, int t, int r, int b, int alignment) {
            this.alignment = alignment;
            Drawable tabBackground = this.tab.getBackground();
            int handleWidth = tabBackground.getIntrinsicWidth();
            int handleHeight = tabBackground.getIntrinsicHeight();
            Drawable targetDrawable = this.target.getDrawable();
            int targetWidth = targetDrawable.getIntrinsicWidth();
            int targetHeight = targetDrawable.getIntrinsicHeight();
            int parentWidth = r - l;
            int parentHeight = b - t;
            int leftTarget = (((int) (SlidingTab.THRESHOLD * parentWidth)) - targetWidth) + (handleWidth / 2);
            int rightTarget = ((int) (0.3333333f * parentWidth)) - (handleWidth / 2);
            int left = (parentWidth - handleWidth) / 2;
            int right = left + handleWidth;
            if (alignment == 0 || alignment == 1) {
                int targetTop = (parentHeight - targetHeight) / 2;
                int targetBottom = targetTop + targetHeight;
                int top = (parentHeight - handleHeight) / 2;
                int bottom = (parentHeight + handleHeight) / 2;
                if (alignment == 0) {
                    this.tab.layout(0, top, handleWidth, bottom);
                    this.text.layout(0 - parentWidth, top, 0, bottom);
                    this.text.setGravity(5);
                    this.target.layout(leftTarget, targetTop, leftTarget + targetWidth, targetBottom);
                    this.alignment_value = l;
                    return;
                }
                this.tab.layout(parentWidth - handleWidth, top, parentWidth, bottom);
                this.text.layout(parentWidth, top, parentWidth + parentWidth, bottom);
                this.target.layout(rightTarget, targetTop, rightTarget + targetWidth, targetBottom);
                this.text.setGravity(48);
                this.alignment_value = r;
                return;
            }
            int targetLeft = (parentWidth - targetWidth) / 2;
            int targetRight = (parentWidth + targetWidth) / 2;
            int top2 = (((int) (SlidingTab.THRESHOLD * parentHeight)) + (handleHeight / 2)) - targetHeight;
            int bottom2 = ((int) (0.3333333f * parentHeight)) - (handleHeight / 2);
            if (alignment == 2) {
                this.tab.layout(left, 0, right, handleHeight);
                this.text.layout(left, 0 - parentHeight, right, 0);
                this.target.layout(targetLeft, top2, targetRight, top2 + targetHeight);
                this.alignment_value = t;
                return;
            }
            this.tab.layout(left, parentHeight - handleHeight, right, parentHeight);
            this.text.layout(left, parentHeight, right, parentHeight + parentHeight);
            this.target.layout(targetLeft, bottom2, targetRight, bottom2 + targetHeight);
            this.alignment_value = b;
        }

        public void updateDrawableStates() {
            setState(this.currentState);
        }

        public void measure() {
            this.tab.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            this.text.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        }

        public int getTabWidth() {
            return this.tab.getMeasuredWidth();
        }

        public int getTabHeight() {
            return this.tab.getMeasuredHeight();
        }

        public void startAnimation(Animation anim1, Animation anim2) {
            this.tab.startAnimation(anim1);
            this.text.startAnimation(anim2);
        }

        public void hideTarget() {
            this.target.clearAnimation();
            this.target.setVisibility(4);
        }
    }

    public SlidingTab(Context context) {
        this(context, null);
    }

    public SlidingTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHoldLeftOnTransition = true;
        this.mHoldRightOnTransition = true;
        this.mGrabbedState = 0;
        this.mTriggered = false;
        this.mAnimationDoneListener = new Animation.AnimationListener() { // from class: com.android.internal.widget.SlidingTab.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                SlidingTab.this.onAnimationDone();
            }
        };
        this.mTmpRect = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidingTab);
        this.mOrientation = a.getInt(0, 0);
        a.recycle();
        Resources r = getResources();
        this.mDensity = r.getDisplayMetrics().density;
        this.mLeftSlider = new Slider(this, R.drawable.jog_tab_left_generic, R.drawable.jog_tab_bar_left_generic, R.drawable.jog_tab_target_gray);
        this.mRightSlider = new Slider(this, R.drawable.jog_tab_right_generic, R.drawable.jog_tab_bar_right_generic, R.drawable.jog_tab_target_gray);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        this.mLeftSlider.measure();
        this.mRightSlider.measure();
        int leftTabWidth = this.mLeftSlider.getTabWidth();
        int rightTabWidth = this.mRightSlider.getTabWidth();
        int leftTabHeight = this.mLeftSlider.getTabHeight();
        int rightTabHeight = this.mRightSlider.getTabHeight();
        if (isHorizontal()) {
            width = Math.max(widthSpecSize, leftTabWidth + rightTabWidth);
            height = Math.max(leftTabHeight, rightTabHeight);
        } else {
            width = Math.max(leftTabWidth, rightTabHeight);
            height = Math.max(heightSpecSize, leftTabHeight + rightTabHeight);
        }
        setMeasuredDimension(width, height);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        if (!this.mAnimating) {
            View leftHandle = this.mLeftSlider.tab;
            leftHandle.getHitRect(this.mTmpRect);
            boolean leftHit = this.mTmpRect.contains((int) x, (int) y);
            View rightHandle = this.mRightSlider.tab;
            rightHandle.getHitRect(this.mTmpRect);
            boolean rightHit = this.mTmpRect.contains((int) x, (int) y);
            if (!this.mTracking && !leftHit && !rightHit) {
                return false;
            }
            switch (action) {
                case 0:
                    this.mTracking = true;
                    this.mTriggered = false;
                    vibrate(VIBRATE_SHORT);
                    if (leftHit) {
                        this.mCurrentSlider = this.mLeftSlider;
                        this.mOtherSlider = this.mRightSlider;
                        this.mThreshold = isHorizontal() ? THRESHOLD : 0.3333333f;
                        setGrabbedState(1);
                    } else {
                        this.mCurrentSlider = this.mRightSlider;
                        this.mOtherSlider = this.mLeftSlider;
                        this.mThreshold = isHorizontal() ? 0.3333333f : THRESHOLD;
                        setGrabbedState(2);
                    }
                    this.mCurrentSlider.setState(1);
                    this.mCurrentSlider.showTarget();
                    this.mOtherSlider.hide();
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    public void reset(boolean animate) {
        this.mLeftSlider.reset(animate);
        this.mRightSlider.reset(animate);
        if (!animate) {
            this.mAnimating = false;
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        if (visibility != getVisibility() && visibility == 4) {
            reset(false);
        }
        super.setVisibility(visibility);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        boolean thresholdReached;
        if (this.mTracking) {
            int action = event.getAction();
            float x = event.getX();
            float y = event.getY();
            switch (action) {
                case 2:
                    if (withinView(x, y, this)) {
                        moveHandle(x, y);
                        float position = isHorizontal() ? x : y;
                        float target = this.mThreshold * (isHorizontal() ? getWidth() : getHeight());
                        if (isHorizontal()) {
                            thresholdReached = this.mCurrentSlider == this.mLeftSlider ? position > target : position < target;
                        } else {
                            thresholdReached = this.mCurrentSlider == this.mLeftSlider ? position < target : position > target;
                        }
                        if (!this.mTriggered && thresholdReached) {
                            this.mTriggered = true;
                            this.mTracking = false;
                            this.mCurrentSlider.setState(2);
                            boolean isLeft = this.mCurrentSlider == this.mLeftSlider;
                            dispatchTriggerEvent(isLeft ? 1 : 2);
                            startAnimating(isLeft ? this.mHoldLeftOnTransition : this.mHoldRightOnTransition);
                            setGrabbedState(0);
                            break;
                        }
                    }
                    break;
                case 1:
                case 3:
                    cancelGrab();
                    break;
            }
        }
        return this.mTracking || super.onTouchEvent(event);
    }

    private void cancelGrab() {
        this.mTracking = false;
        this.mTriggered = false;
        this.mOtherSlider.show(true);
        this.mCurrentSlider.reset(false);
        this.mCurrentSlider.hideTarget();
        this.mCurrentSlider = null;
        this.mOtherSlider = null;
        setGrabbedState(0);
    }

    void startAnimating(final boolean holdAfter) {
        int dx;
        int dy;
        this.mAnimating = true;
        Slider slider = this.mCurrentSlider;
        Slider slider2 = this.mOtherSlider;
        if (isHorizontal()) {
            int right = slider.tab.getRight();
            int width = slider.tab.getWidth();
            int left = slider.tab.getLeft();
            int viewWidth = getWidth();
            int holdOffset = holdAfter ? 0 : width;
            dx = slider == this.mRightSlider ? -((right + viewWidth) - holdOffset) : ((viewWidth - left) + viewWidth) - holdOffset;
            dy = 0;
        } else {
            int top = slider.tab.getTop();
            int bottom = slider.tab.getBottom();
            int height = slider.tab.getHeight();
            int viewHeight = getHeight();
            int holdOffset2 = holdAfter ? 0 : height;
            dx = 0;
            dy = slider == this.mRightSlider ? (top + viewHeight) - holdOffset2 : -(((viewHeight - bottom) + viewHeight) - holdOffset2);
        }
        Animation trans1 = new TranslateAnimation(0.0f, dx, 0.0f, dy);
        trans1.setDuration(250L);
        trans1.setInterpolator(new LinearInterpolator());
        trans1.setFillAfter(true);
        Animation trans2 = new TranslateAnimation(0.0f, dx, 0.0f, dy);
        trans2.setDuration(250L);
        trans2.setInterpolator(new LinearInterpolator());
        trans2.setFillAfter(true);
        final int i = dx;
        final int i2 = dy;
        trans1.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.internal.widget.SlidingTab.2
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                Animation anim;
                if (holdAfter) {
                    anim = new TranslateAnimation(i, i, i2, i2);
                    anim.setDuration(1000L);
                    SlidingTab.this.mAnimating = false;
                } else {
                    anim = new AlphaAnimation(0.5f, 1.0f);
                    anim.setDuration(250L);
                    SlidingTab.this.resetView();
                }
                anim.setAnimationListener(SlidingTab.this.mAnimationDoneListener);
                SlidingTab.this.mLeftSlider.startAnimation(anim, anim);
                SlidingTab.this.mRightSlider.startAnimation(anim, anim);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }
        });
        slider.hideTarget();
        slider.startAnimation(trans1, trans2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationDone() {
        resetView();
        this.mAnimating = false;
    }

    private boolean withinView(float x, float y, View view) {
        return (isHorizontal() && y > -50.0f && y < ((float) (50 + view.getHeight()))) || (!isHorizontal() && x > -50.0f && x < ((float) (50 + view.getWidth())));
    }

    private boolean isHorizontal() {
        return this.mOrientation == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetView() {
        this.mLeftSlider.reset(false);
        this.mRightSlider.reset(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            this.mLeftSlider.layout(l, t, r, b, isHorizontal() ? 0 : 3);
            this.mRightSlider.layout(l, t, r, b, isHorizontal() ? 1 : 2);
        }
    }

    private void moveHandle(float x, float y) {
        View handle = this.mCurrentSlider.tab;
        View content = this.mCurrentSlider.text;
        if (isHorizontal()) {
            int deltaX = (((int) x) - handle.getLeft()) - (handle.getWidth() / 2);
            handle.offsetLeftAndRight(deltaX);
            content.offsetLeftAndRight(deltaX);
        } else {
            int deltaY = (((int) y) - handle.getTop()) - (handle.getHeight() / 2);
            handle.offsetTopAndBottom(deltaY);
            content.offsetTopAndBottom(deltaY);
        }
        invalidate();
    }

    public void setLeftTabResources(int iconId, int targetId, int barId, int tabId) {
        this.mLeftSlider.setIcon(iconId);
        this.mLeftSlider.setTarget(targetId);
        this.mLeftSlider.setBarBackgroundResource(barId);
        this.mLeftSlider.setTabBackgroundResource(tabId);
        this.mLeftSlider.updateDrawableStates();
    }

    public void setLeftHintText(int resId) {
        if (isHorizontal()) {
            this.mLeftSlider.setHintText(resId);
        }
    }

    public void setRightTabResources(int iconId, int targetId, int barId, int tabId) {
        this.mRightSlider.setIcon(iconId);
        this.mRightSlider.setTarget(targetId);
        this.mRightSlider.setBarBackgroundResource(barId);
        this.mRightSlider.setTabBackgroundResource(tabId);
        this.mRightSlider.updateDrawableStates();
    }

    public void setRightHintText(int resId) {
        if (isHorizontal()) {
            this.mRightSlider.setHintText(resId);
        }
    }

    public void setHoldAfterTrigger(boolean holdLeft, boolean holdRight) {
        this.mHoldLeftOnTransition = holdLeft;
        this.mHoldRightOnTransition = holdRight;
    }

    private synchronized void vibrate(long duration) {
        boolean hapticEnabled = Settings.System.getIntForUser(this.mContext.getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED, 1, -2) != 0;
        if (hapticEnabled) {
            if (this.mVibrator == null) {
                this.mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            }
            this.mVibrator.vibrate(duration);
        }
    }

    public void setOnTriggerListener(OnTriggerListener listener) {
        this.mOnTriggerListener = listener;
    }

    private void dispatchTriggerEvent(int whichHandle) {
        vibrate(VIBRATE_LONG);
        if (this.mOnTriggerListener != null) {
            this.mOnTriggerListener.onTrigger(this, whichHandle);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == this && visibility != 0 && this.mGrabbedState != 0) {
            cancelGrab();
        }
    }

    private void setGrabbedState(int newState) {
        if (newState != this.mGrabbedState) {
            this.mGrabbedState = newState;
            if (this.mOnTriggerListener != null) {
                this.mOnTriggerListener.onGrabbedStateChange(this, this.mGrabbedState);
            }
        }
    }

    private void log(String msg) {
        Log.d(LOG_TAG, msg);
    }
}