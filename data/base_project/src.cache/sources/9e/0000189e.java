package android.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.TableMaskFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TimedRemoteCaller;
import android.view.MotionEvent;
import android.view.RemotableViewMethod;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterViewAnimator;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.lang.ref.WeakReference;

@RemoteViews.RemoteView
/* loaded from: StackView.class */
public class StackView extends AdapterViewAnimator {
    private final String TAG = "StackView";
    private static final int DEFAULT_ANIMATION_DURATION = 400;
    private static final int MINIMUM_ANIMATION_DURATION = 50;
    private static final int STACK_RELAYOUT_DURATION = 100;
    private static final float PERSPECTIVE_SHIFT_FACTOR_Y = 0.1f;
    private static final float PERSPECTIVE_SHIFT_FACTOR_X = 0.1f;
    private float mPerspectiveShiftX;
    private float mPerspectiveShiftY;
    private float mNewPerspectiveShiftX;
    private float mNewPerspectiveShiftY;
    private static final float PERSPECTIVE_SCALE_FACTOR = 0.0f;
    private static final int ITEMS_SLIDE_UP = 0;
    private static final int ITEMS_SLIDE_DOWN = 1;
    private static final int GESTURE_NONE = 0;
    private static final int GESTURE_SLIDE_UP = 1;
    private static final int GESTURE_SLIDE_DOWN = 2;
    private static final float SWIPE_THRESHOLD_RATIO = 0.2f;
    private static final float SLIDE_UP_RATIO = 0.7f;
    private static final int INVALID_POINTER = -1;
    private static final int NUM_ACTIVE_VIEWS = 5;
    private static final int FRAME_PADDING = 4;
    private final Rect mTouchRect;
    private static final int MIN_TIME_BETWEEN_INTERACTION_AND_AUTOADVANCE = 5000;
    private static final long MIN_TIME_BETWEEN_SCROLLS = 100;
    private float mInitialY;
    private float mInitialX;
    private int mActivePointerId;
    private int mYVelocity;
    private int mSwipeGestureType;
    private int mSlideAmount;
    private int mSwipeThreshold;
    private int mTouchSlop;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;
    private boolean mTransitionIsSetup;
    private int mResOutColor;
    private int mClickColor;
    private static HolographicHelper sHolographicHelper;
    private ImageView mHighlight;
    private ImageView mClickFeedback;
    private boolean mClickFeedbackIsValid;
    private StackSlider mStackSlider;
    private boolean mFirstLayoutHappened;
    private long mLastInteractionTime;
    private long mLastScrollTime;
    private int mStackMode;
    private int mFramePadding;
    private final Rect stackInvalidateRect;

    public StackView(Context context) {
        this(context, null);
    }

    public StackView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.stackViewStyle);
    }

    public StackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.TAG = "StackView";
        this.mTouchRect = new Rect();
        this.mYVelocity = 0;
        this.mSwipeGestureType = 0;
        this.mTransitionIsSetup = false;
        this.mClickFeedbackIsValid = false;
        this.mFirstLayoutHappened = false;
        this.mLastInteractionTime = 0L;
        this.stackInvalidateRect = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StackView, defStyleAttr, 0);
        this.mResOutColor = a.getColor(0, 0);
        this.mClickColor = a.getColor(1, 0);
        a.recycle();
        initStackView();
    }

    private void initStackView() {
        configureViewAnimator(5, 1);
        setStaticTransformationsEnabled(true);
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.mActivePointerId = -1;
        this.mHighlight = new ImageView(getContext());
        this.mHighlight.setLayoutParams(new LayoutParams(this.mHighlight));
        addViewInLayout(this.mHighlight, -1, new LayoutParams(this.mHighlight));
        this.mClickFeedback = new ImageView(getContext());
        this.mClickFeedback.setLayoutParams(new LayoutParams(this.mClickFeedback));
        addViewInLayout(this.mClickFeedback, -1, new LayoutParams(this.mClickFeedback));
        this.mClickFeedback.setVisibility(4);
        this.mStackSlider = new StackSlider();
        if (sHolographicHelper == null) {
            sHolographicHelper = new HolographicHelper(this.mContext);
        }
        setClipChildren(false);
        setClipToPadding(false);
        this.mStackMode = 1;
        this.mWhichChild = -1;
        float density = this.mContext.getResources().getDisplayMetrics().density;
        this.mFramePadding = (int) Math.ceil(density * 4.0f);
    }

    @Override // android.widget.AdapterViewAnimator
    void transformViewForTransition(int fromIndex, int toIndex, final View view, boolean animate) {
        if (!animate) {
            ((StackFrame) view).cancelSliderAnimator();
            view.setRotationX(0.0f);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.setVerticalOffset(0);
            lp.setHorizontalOffset(0);
        }
        if (fromIndex == -1 && toIndex == getNumActiveViews() - 1) {
            transformViewAtIndex(toIndex, view, false);
            view.setVisibility(0);
            view.setAlpha(1.0f);
        } else if (fromIndex == 0 && toIndex == 1) {
            ((StackFrame) view).cancelSliderAnimator();
            view.setVisibility(0);
            int duration = Math.round(this.mStackSlider.getDurationForNeutralPosition(this.mYVelocity));
            StackSlider animationSlider = new StackSlider(this.mStackSlider);
            animationSlider.setView(view);
            if (animate) {
                PropertyValuesHolder slideInY = PropertyValuesHolder.ofFloat("YProgress", 0.0f);
                PropertyValuesHolder slideInX = PropertyValuesHolder.ofFloat("XProgress", 0.0f);
                ObjectAnimator slideIn = ObjectAnimator.ofPropertyValuesHolder(animationSlider, slideInX, slideInY);
                slideIn.setDuration(duration);
                slideIn.setInterpolator(new LinearInterpolator());
                ((StackFrame) view).setSliderAnimator(slideIn);
                slideIn.start();
            } else {
                animationSlider.setYProgress(0.0f);
                animationSlider.setXProgress(0.0f);
            }
        } else if (fromIndex == 1 && toIndex == 0) {
            ((StackFrame) view).cancelSliderAnimator();
            int duration2 = Math.round(this.mStackSlider.getDurationForOffscreenPosition(this.mYVelocity));
            StackSlider animationSlider2 = new StackSlider(this.mStackSlider);
            animationSlider2.setView(view);
            if (animate) {
                PropertyValuesHolder slideOutY = PropertyValuesHolder.ofFloat("YProgress", 1.0f);
                PropertyValuesHolder slideOutX = PropertyValuesHolder.ofFloat("XProgress", 0.0f);
                ObjectAnimator slideOut = ObjectAnimator.ofPropertyValuesHolder(animationSlider2, slideOutX, slideOutY);
                slideOut.setDuration(duration2);
                slideOut.setInterpolator(new LinearInterpolator());
                ((StackFrame) view).setSliderAnimator(slideOut);
                slideOut.start();
            } else {
                animationSlider2.setYProgress(1.0f);
                animationSlider2.setXProgress(0.0f);
            }
        } else if (toIndex == 0) {
            view.setAlpha(0.0f);
            view.setVisibility(4);
        } else if ((fromIndex == 0 || fromIndex == 1) && toIndex > 1) {
            view.setVisibility(0);
            view.setAlpha(1.0f);
            view.setRotationX(0.0f);
            LayoutParams lp2 = (LayoutParams) view.getLayoutParams();
            lp2.setVerticalOffset(0);
            lp2.setHorizontalOffset(0);
        } else if (fromIndex == -1) {
            view.setAlpha(1.0f);
            view.setVisibility(0);
        } else if (toIndex == -1) {
            if (animate) {
                postDelayed(new Runnable() { // from class: android.widget.StackView.1
                    @Override // java.lang.Runnable
                    public void run() {
                        view.setAlpha(0.0f);
                    }
                }, MIN_TIME_BETWEEN_SCROLLS);
            } else {
                view.setAlpha(0.0f);
            }
        }
        if (toIndex != -1) {
            transformViewAtIndex(toIndex, view, animate);
        }
    }

    private void transformViewAtIndex(int index, View view, boolean animate) {
        int index2;
        float maxPerspectiveShiftY = this.mPerspectiveShiftY;
        float maxPerspectiveShiftX = this.mPerspectiveShiftX;
        if (this.mStackMode == 1) {
            index2 = (this.mMaxNumActiveViews - index) - 1;
            if (index2 == this.mMaxNumActiveViews - 1) {
                index2--;
            }
        } else {
            index2 = index - 1;
            if (index2 < 0) {
                index2++;
            }
        }
        float r = (index2 * 1.0f) / (this.mMaxNumActiveViews - 2);
        float scale = 1.0f - (0.0f * (1.0f - r));
        float perspectiveTranslationY = r * maxPerspectiveShiftY;
        float scaleShiftCorrectionY = (scale - 1.0f) * ((getMeasuredHeight() * 0.9f) / 2.0f);
        float transY = perspectiveTranslationY + scaleShiftCorrectionY;
        float perspectiveTranslationX = (1.0f - r) * maxPerspectiveShiftX;
        float scaleShiftCorrectionX = (1.0f - scale) * ((getMeasuredWidth() * 0.9f) / 2.0f);
        float transX = perspectiveTranslationX + scaleShiftCorrectionX;
        if (view instanceof StackFrame) {
            ((StackFrame) view).cancelTransformAnimator();
        }
        if (animate) {
            PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat("translationX", transX);
            PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", transY);
            PropertyValuesHolder scalePropX = PropertyValuesHolder.ofFloat("scaleX", scale);
            PropertyValuesHolder scalePropY = PropertyValuesHolder.ofFloat("scaleY", scale);
            ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(view, scalePropX, scalePropY, translationY, translationX);
            oa.setDuration(MIN_TIME_BETWEEN_SCROLLS);
            if (view instanceof StackFrame) {
                ((StackFrame) view).setTransformAnimator(oa);
            }
            oa.start();
            return;
        }
        view.setTranslationX(transX);
        view.setTranslationY(transY);
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    private void setupStackSlider(View v, int mode) {
        this.mStackSlider.setMode(mode);
        if (v != null) {
            this.mHighlight.setImageBitmap(sHolographicHelper.createResOutline(v, this.mResOutColor));
            this.mHighlight.setRotation(v.getRotation());
            this.mHighlight.setTranslationY(v.getTranslationY());
            this.mHighlight.setTranslationX(v.getTranslationX());
            this.mHighlight.bringToFront();
            v.bringToFront();
            this.mStackSlider.setView(v);
            v.setVisibility(0);
        }
    }

    @Override // android.widget.AdapterViewAnimator
    @RemotableViewMethod
    public void showNext() {
        View v;
        if (this.mSwipeGestureType != 0) {
            return;
        }
        if (!this.mTransitionIsSetup && (v = getViewAtRelativeIndex(1)) != null) {
            setupStackSlider(v, 0);
            this.mStackSlider.setYProgress(0.0f);
            this.mStackSlider.setXProgress(0.0f);
        }
        super.showNext();
    }

    @Override // android.widget.AdapterViewAnimator
    @RemotableViewMethod
    public void showPrevious() {
        View v;
        if (this.mSwipeGestureType != 0) {
            return;
        }
        if (!this.mTransitionIsSetup && (v = getViewAtRelativeIndex(0)) != null) {
            setupStackSlider(v, 0);
            this.mStackSlider.setYProgress(1.0f);
            this.mStackSlider.setXProgress(0.0f);
        }
        super.showPrevious();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.AdapterViewAnimator
    public void showOnly(int childIndex, boolean animate) {
        View v;
        super.showOnly(childIndex, animate);
        for (int i = this.mCurrentWindowEnd; i >= this.mCurrentWindowStart; i--) {
            int index = modulo(i, getWindowSize());
            AdapterViewAnimator.ViewAndMetaData vm = this.mViewsMap.get(Integer.valueOf(index));
            if (vm != null && (v = this.mViewsMap.get(Integer.valueOf(index)).view) != null) {
                v.bringToFront();
            }
        }
        if (this.mHighlight != null) {
            this.mHighlight.bringToFront();
        }
        this.mTransitionIsSetup = false;
        this.mClickFeedbackIsValid = false;
    }

    void updateClickFeedback() {
        if (!this.mClickFeedbackIsValid) {
            View v = getViewAtRelativeIndex(1);
            if (v != null) {
                this.mClickFeedback.setImageBitmap(sHolographicHelper.createClickOutline(v, this.mClickColor));
                this.mClickFeedback.setTranslationX(v.getTranslationX());
                this.mClickFeedback.setTranslationY(v.getTranslationY());
            }
            this.mClickFeedbackIsValid = true;
        }
    }

    @Override // android.widget.AdapterViewAnimator
    void showTapFeedback(View v) {
        updateClickFeedback();
        this.mClickFeedback.setVisibility(0);
        this.mClickFeedback.bringToFront();
        invalidate();
    }

    @Override // android.widget.AdapterViewAnimator
    void hideTapFeedback(View v) {
        this.mClickFeedback.setVisibility(4);
        invalidate();
    }

    private void updateChildTransforms() {
        for (int i = 0; i < getNumActiveViews(); i++) {
            View v = getViewAtRelativeIndex(i);
            if (v != null) {
                transformViewAtIndex(i, v, false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StackView$StackFrame.class */
    public static class StackFrame extends FrameLayout {
        WeakReference<ObjectAnimator> transformAnimator;
        WeakReference<ObjectAnimator> sliderAnimator;

        public StackFrame(Context context) {
            super(context);
        }

        void setTransformAnimator(ObjectAnimator oa) {
            this.transformAnimator = new WeakReference<>(oa);
        }

        void setSliderAnimator(ObjectAnimator oa) {
            this.sliderAnimator = new WeakReference<>(oa);
        }

        boolean cancelTransformAnimator() {
            ObjectAnimator oa;
            if (this.transformAnimator != null && (oa = this.transformAnimator.get()) != null) {
                oa.cancel();
                return true;
            }
            return false;
        }

        boolean cancelSliderAnimator() {
            ObjectAnimator oa;
            if (this.sliderAnimator != null && (oa = this.sliderAnimator.get()) != null) {
                oa.cancel();
                return true;
            }
            return false;
        }
    }

    @Override // android.widget.AdapterViewAnimator
    FrameLayout getFrameForChild() {
        StackFrame fl = new StackFrame(this.mContext);
        fl.setPadding(this.mFramePadding, this.mFramePadding, this.mFramePadding, this.mFramePadding);
        return fl;
    }

    @Override // android.widget.AdapterViewAnimator
    void applyTransformForChildAtIndex(View child, int relativeIndex) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        boolean expandClipRegion = false;
        canvas.getClipBounds(this.stackInvalidateRect);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if ((lp.horizontalOffset == 0 && lp.verticalOffset == 0) || child.getAlpha() == 0.0f || child.getVisibility() != 0) {
                lp.resetInvalidateRect();
            }
            Rect childInvalidateRect = lp.getInvalidateRect();
            if (!childInvalidateRect.isEmpty()) {
                expandClipRegion = true;
                this.stackInvalidateRect.union(childInvalidateRect);
            }
        }
        if (expandClipRegion) {
            canvas.save(2);
            canvas.clipRect(this.stackInvalidateRect, Region.Op.UNION);
            super.dispatchDraw(canvas);
            canvas.restore();
            return;
        }
        super.dispatchDraw(canvas);
    }

    private void onLayout() {
        if (!this.mFirstLayoutHappened) {
            this.mFirstLayoutHappened = true;
            updateChildTransforms();
        }
        int newSlideAmount = Math.round(SLIDE_UP_RATIO * getMeasuredHeight());
        if (this.mSlideAmount != newSlideAmount) {
            this.mSlideAmount = newSlideAmount;
            this.mSwipeThreshold = Math.round(0.2f * newSlideAmount);
        }
        if (Float.compare(this.mPerspectiveShiftY, this.mNewPerspectiveShiftY) != 0 || Float.compare(this.mPerspectiveShiftX, this.mNewPerspectiveShiftX) != 0) {
            this.mPerspectiveShiftY = this.mNewPerspectiveShiftY;
            this.mPerspectiveShiftX = this.mNewPerspectiveShiftX;
            updateChildTransforms();
        }
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & 2) != 0) {
            switch (event.getAction()) {
                case 8:
                    float vscroll = event.getAxisValue(9);
                    if (vscroll < 0.0f) {
                        pacedScroll(false);
                        return true;
                    } else if (vscroll > 0.0f) {
                        pacedScroll(true);
                        return true;
                    }
                    break;
            }
        }
        return super.onGenericMotionEvent(event);
    }

    private void pacedScroll(boolean up) {
        long timeSinceLastScroll = System.currentTimeMillis() - this.mLastScrollTime;
        if (timeSinceLastScroll > MIN_TIME_BETWEEN_SCROLLS) {
            if (up) {
                showPrevious();
            } else {
                showNext();
            }
            this.mLastScrollTime = System.currentTimeMillis();
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action & 255) {
            case 0:
                if (this.mActivePointerId == -1) {
                    this.mInitialX = ev.getX();
                    this.mInitialY = ev.getY();
                    this.mActivePointerId = ev.getPointerId(0);
                    break;
                }
                break;
            case 1:
            case 3:
                this.mActivePointerId = -1;
                this.mSwipeGestureType = 0;
                break;
            case 2:
                int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                if (pointerIndex == -1) {
                    Log.d("StackView", "Error: No data for our primary pointer.");
                    return false;
                }
                float newY = ev.getY(pointerIndex);
                float deltaY = newY - this.mInitialY;
                beginGestureIfNeeded(deltaY);
                break;
            case 6:
                onSecondaryPointerUp(ev);
                break;
        }
        return this.mSwipeGestureType != 0;
    }

    private void beginGestureIfNeeded(float deltaY) {
        int activeIndex;
        int stackMode;
        if (((int) Math.abs(deltaY)) > this.mTouchSlop && this.mSwipeGestureType == 0) {
            int swipeGestureType = deltaY < 0.0f ? 1 : 2;
            cancelLongPress();
            requestDisallowInterceptTouchEvent(true);
            if (this.mAdapter == null) {
                return;
            }
            int adapterCount = getCount();
            if (this.mStackMode == 0) {
                activeIndex = swipeGestureType == 2 ? 0 : 1;
            } else {
                activeIndex = swipeGestureType == 2 ? 1 : 0;
            }
            boolean endOfStack = this.mLoopViews && adapterCount == 1 && ((this.mStackMode == 0 && swipeGestureType == 1) || (this.mStackMode == 1 && swipeGestureType == 2));
            boolean beginningOfStack = this.mLoopViews && adapterCount == 1 && ((this.mStackMode == 1 && swipeGestureType == 1) || (this.mStackMode == 0 && swipeGestureType == 2));
            if (this.mLoopViews && !beginningOfStack && !endOfStack) {
                stackMode = 0;
            } else if (this.mCurrentWindowStartUnbounded + activeIndex == -1 || beginningOfStack) {
                activeIndex++;
                stackMode = 1;
            } else if (this.mCurrentWindowStartUnbounded + activeIndex == adapterCount - 1 || endOfStack) {
                stackMode = 2;
            } else {
                stackMode = 0;
            }
            this.mTransitionIsSetup = stackMode == 0;
            View v = getViewAtRelativeIndex(activeIndex);
            if (v == null) {
                return;
            }
            setupStackSlider(v, stackMode);
            this.mSwipeGestureType = swipeGestureType;
            cancelHandleClick();
        }
    }

    @Override // android.widget.AdapterViewAnimator, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        int action = ev.getAction();
        int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
        if (pointerIndex == -1) {
            Log.d("StackView", "Error: No data for our primary pointer.");
            return false;
        }
        float newY = ev.getY(pointerIndex);
        float newX = ev.getX(pointerIndex);
        float deltaY = newY - this.mInitialY;
        float deltaX = newX - this.mInitialX;
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        switch (action & 255) {
            case 1:
                handlePointerUp(ev);
                return true;
            case 2:
                beginGestureIfNeeded(deltaY);
                float rx = deltaX / (this.mSlideAmount * 1.0f);
                if (this.mSwipeGestureType == 2) {
                    float r = ((deltaY - (this.mTouchSlop * 1.0f)) / this.mSlideAmount) * 1.0f;
                    if (this.mStackMode == 1) {
                        r = 1.0f - r;
                    }
                    this.mStackSlider.setYProgress(1.0f - r);
                    this.mStackSlider.setXProgress(rx);
                    return true;
                } else if (this.mSwipeGestureType == 1) {
                    float r2 = ((-(deltaY + (this.mTouchSlop * 1.0f))) / this.mSlideAmount) * 1.0f;
                    if (this.mStackMode == 1) {
                        r2 = 1.0f - r2;
                    }
                    this.mStackSlider.setYProgress(r2);
                    this.mStackSlider.setXProgress(rx);
                    return true;
                } else {
                    return true;
                }
            case 3:
                this.mActivePointerId = -1;
                this.mSwipeGestureType = 0;
                return true;
            case 4:
            case 5:
            default:
                return true;
            case 6:
                onSecondaryPointerUp(ev);
                return true;
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int activePointerIndex = ev.getActionIndex();
        int pointerId = ev.getPointerId(activePointerIndex);
        if (pointerId == this.mActivePointerId) {
            int activeViewIndex = this.mSwipeGestureType == 2 ? 0 : 1;
            View v = getViewAtRelativeIndex(activeViewIndex);
            if (v == null) {
                return;
            }
            for (int index = 0; index < ev.getPointerCount(); index++) {
                if (index != activePointerIndex) {
                    float x = ev.getX(index);
                    float y = ev.getY(index);
                    this.mTouchRect.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    if (this.mTouchRect.contains(Math.round(x), Math.round(y))) {
                        float oldX = ev.getX(activePointerIndex);
                        float oldY = ev.getY(activePointerIndex);
                        this.mInitialY += y - oldY;
                        this.mInitialX += x - oldX;
                        this.mActivePointerId = ev.getPointerId(index);
                        if (this.mVelocityTracker != null) {
                            this.mVelocityTracker.clear();
                            return;
                        }
                        return;
                    }
                }
            }
            handlePointerUp(ev);
        }
    }

    private void handlePointerUp(MotionEvent ev) {
        int duration;
        int duration2;
        int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
        float newY = ev.getY(pointerIndex);
        int deltaY = (int) (newY - this.mInitialY);
        this.mLastInteractionTime = System.currentTimeMillis();
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
            this.mYVelocity = (int) this.mVelocityTracker.getYVelocity(this.mActivePointerId);
        }
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        if (deltaY > this.mSwipeThreshold && this.mSwipeGestureType == 2 && this.mStackSlider.mMode == 0) {
            this.mSwipeGestureType = 0;
            if (this.mStackMode == 0) {
                showPrevious();
            } else {
                showNext();
            }
            this.mHighlight.bringToFront();
        } else if (deltaY < (-this.mSwipeThreshold) && this.mSwipeGestureType == 1 && this.mStackSlider.mMode == 0) {
            this.mSwipeGestureType = 0;
            if (this.mStackMode == 0) {
                showNext();
            } else {
                showPrevious();
            }
            this.mHighlight.bringToFront();
        } else if (this.mSwipeGestureType == 1) {
            float finalYProgress = this.mStackMode == 1 ? 1.0f : 0.0f;
            if (this.mStackMode == 0 || this.mStackSlider.mMode != 0) {
                duration2 = Math.round(this.mStackSlider.getDurationForNeutralPosition());
            } else {
                duration2 = Math.round(this.mStackSlider.getDurationForOffscreenPosition());
            }
            StackSlider animationSlider = new StackSlider(this.mStackSlider);
            PropertyValuesHolder snapBackY = PropertyValuesHolder.ofFloat("YProgress", finalYProgress);
            PropertyValuesHolder snapBackX = PropertyValuesHolder.ofFloat("XProgress", 0.0f);
            ObjectAnimator pa = ObjectAnimator.ofPropertyValuesHolder(animationSlider, snapBackX, snapBackY);
            pa.setDuration(duration2);
            pa.setInterpolator(new LinearInterpolator());
            pa.start();
        } else if (this.mSwipeGestureType == 2) {
            float finalYProgress2 = this.mStackMode == 1 ? 0.0f : 1.0f;
            if (this.mStackMode == 1 || this.mStackSlider.mMode != 0) {
                duration = Math.round(this.mStackSlider.getDurationForNeutralPosition());
            } else {
                duration = Math.round(this.mStackSlider.getDurationForOffscreenPosition());
            }
            StackSlider animationSlider2 = new StackSlider(this.mStackSlider);
            PropertyValuesHolder snapBackY2 = PropertyValuesHolder.ofFloat("YProgress", finalYProgress2);
            PropertyValuesHolder snapBackX2 = PropertyValuesHolder.ofFloat("XProgress", 0.0f);
            ObjectAnimator pa2 = ObjectAnimator.ofPropertyValuesHolder(animationSlider2, snapBackX2, snapBackY2);
            pa2.setDuration(duration);
            pa2.start();
        }
        this.mActivePointerId = -1;
        this.mSwipeGestureType = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StackView$StackSlider.class */
    public class StackSlider {
        View mView;
        float mYProgress;
        float mXProgress;
        static final int NORMAL_MODE = 0;
        static final int BEGINNING_OF_STACK_MODE = 1;
        static final int END_OF_STACK_MODE = 2;
        int mMode;

        public StackSlider() {
            this.mMode = 0;
        }

        public StackSlider(StackSlider copy) {
            this.mMode = 0;
            this.mView = copy.mView;
            this.mYProgress = copy.mYProgress;
            this.mXProgress = copy.mXProgress;
            this.mMode = copy.mMode;
        }

        private float cubic(float r) {
            return ((float) (Math.pow((2.0f * r) - 1.0f, 3.0d) + 1.0d)) / 2.0f;
        }

        private float highlightAlphaInterpolator(float r) {
            if (r < 0.4f) {
                return 0.85f * cubic(r / 0.4f);
            }
            return 0.85f * cubic(1.0f - ((r - 0.4f) / (1.0f - 0.4f)));
        }

        private float viewAlphaInterpolator(float r) {
            if (r > 0.3f) {
                return (r - 0.3f) / (1.0f - 0.3f);
            }
            return 0.0f;
        }

        private float rotationInterpolator(float r) {
            if (r >= 0.2f) {
                return (r - 0.2f) / (1.0f - 0.2f);
            }
            return 0.0f;
        }

        void setView(View v) {
            this.mView = v;
        }

        public void setYProgress(float r) {
            float r2 = Math.max(0.0f, Math.min(1.0f, r));
            this.mYProgress = r2;
            if (this.mView == null) {
                return;
            }
            LayoutParams viewLp = (LayoutParams) this.mView.getLayoutParams();
            LayoutParams highlightLp = (LayoutParams) StackView.this.mHighlight.getLayoutParams();
            int stackDirection = StackView.this.mStackMode == 0 ? 1 : -1;
            if (Float.compare(0.0f, this.mYProgress) != 0 && Float.compare(1.0f, this.mYProgress) != 0) {
                if (this.mView.getLayerType() == 0) {
                    this.mView.setLayerType(2, null);
                }
            } else if (this.mView.getLayerType() != 0) {
                this.mView.setLayerType(0, null);
            }
            switch (this.mMode) {
                case 0:
                    viewLp.setVerticalOffset(Math.round((-r2) * stackDirection * StackView.this.mSlideAmount));
                    highlightLp.setVerticalOffset(Math.round((-r2) * stackDirection * StackView.this.mSlideAmount));
                    StackView.this.mHighlight.setAlpha(highlightAlphaInterpolator(r2));
                    float alpha = viewAlphaInterpolator(1.0f - r2);
                    if (this.mView.getAlpha() == 0.0f && alpha != 0.0f && this.mView.getVisibility() != 0) {
                        this.mView.setVisibility(0);
                    } else if (alpha == 0.0f && this.mView.getAlpha() != 0.0f && this.mView.getVisibility() == 0) {
                        this.mView.setVisibility(4);
                    }
                    this.mView.setAlpha(alpha);
                    this.mView.setRotationX(stackDirection * 90.0f * rotationInterpolator(r2));
                    StackView.this.mHighlight.setRotationX(stackDirection * 90.0f * rotationInterpolator(r2));
                    return;
                case 1:
                    float r3 = (1.0f - r2) * 0.2f;
                    viewLp.setVerticalOffset(Math.round(stackDirection * r3 * StackView.this.mSlideAmount));
                    highlightLp.setVerticalOffset(Math.round(stackDirection * r3 * StackView.this.mSlideAmount));
                    StackView.this.mHighlight.setAlpha(highlightAlphaInterpolator(r3));
                    return;
                case 2:
                    float r4 = r2 * 0.2f;
                    viewLp.setVerticalOffset(Math.round((-stackDirection) * r4 * StackView.this.mSlideAmount));
                    highlightLp.setVerticalOffset(Math.round((-stackDirection) * r4 * StackView.this.mSlideAmount));
                    StackView.this.mHighlight.setAlpha(highlightAlphaInterpolator(r4));
                    return;
                default:
                    return;
            }
        }

        public void setXProgress(float r) {
            float r2 = Math.max(-2.0f, Math.min(2.0f, r));
            this.mXProgress = r2;
            if (this.mView == null) {
                return;
            }
            LayoutParams viewLp = (LayoutParams) this.mView.getLayoutParams();
            LayoutParams highlightLp = (LayoutParams) StackView.this.mHighlight.getLayoutParams();
            float r3 = r2 * 0.2f;
            viewLp.setHorizontalOffset(Math.round(r3 * StackView.this.mSlideAmount));
            highlightLp.setHorizontalOffset(Math.round(r3 * StackView.this.mSlideAmount));
        }

        void setMode(int mode) {
            this.mMode = mode;
        }

        float getDurationForNeutralPosition() {
            return getDuration(false, 0.0f);
        }

        float getDurationForOffscreenPosition() {
            return getDuration(true, 0.0f);
        }

        float getDurationForNeutralPosition(float velocity) {
            return getDuration(false, velocity);
        }

        float getDurationForOffscreenPosition(float velocity) {
            return getDuration(true, velocity);
        }

        private float getDuration(boolean invert, float velocity) {
            if (this.mView != null) {
                LayoutParams viewLp = (LayoutParams) this.mView.getLayoutParams();
                float d = (float) Math.sqrt(Math.pow(viewLp.horizontalOffset, 2.0d) + Math.pow(viewLp.verticalOffset, 2.0d));
                float maxd = (float) Math.sqrt(Math.pow(StackView.this.mSlideAmount, 2.0d) + Math.pow(0.4f * StackView.this.mSlideAmount, 2.0d));
                if (velocity == 0.0f) {
                    return (invert ? 1.0f - (d / maxd) : d / maxd) * 400.0f;
                }
                float duration = invert ? d / Math.abs(velocity) : (maxd - d) / Math.abs(velocity);
                if (duration < 50.0f || duration > 400.0f) {
                    return getDuration(invert, 0.0f);
                }
                return duration;
            }
            return 0.0f;
        }

        public float getYProgress() {
            return this.mYProgress;
        }

        public float getXProgress() {
            return this.mXProgress;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.AdapterViewAnimator
    public LayoutParams createOrReuseLayoutParams(View v) {
        ViewGroup.LayoutParams currentLp = v.getLayoutParams();
        if (currentLp instanceof LayoutParams) {
            LayoutParams lp = (LayoutParams) currentLp;
            lp.setHorizontalOffset(0);
            lp.setVerticalOffset(0);
            lp.width = 0;
            lp.width = 0;
            return lp;
        }
        return new LayoutParams(v);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AdapterViewAnimator, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        checkForAndHandleDataChanged();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childRight = this.mPaddingLeft + child.getMeasuredWidth();
            int childBottom = this.mPaddingTop + child.getMeasuredHeight();
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.layout(this.mPaddingLeft + lp.horizontalOffset, this.mPaddingTop + lp.verticalOffset, childRight + lp.horizontalOffset, childBottom + lp.verticalOffset);
        }
        onLayout();
    }

    @Override // android.widget.AdapterViewAnimator, android.widget.Advanceable
    public void advance() {
        long timeSinceLastInteraction = System.currentTimeMillis() - this.mLastInteractionTime;
        if (this.mAdapter == null) {
            return;
        }
        int adapterCount = getCount();
        if ((adapterCount != 1 || !this.mLoopViews) && this.mSwipeGestureType == 0 && timeSinceLastInteraction > TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS) {
            showNext();
        }
    }

    private void measureChildren() {
        int count = getChildCount();
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int childWidth = (Math.round(measuredWidth * 0.9f) - this.mPaddingLeft) - this.mPaddingRight;
        int childHeight = (Math.round(measuredHeight * 0.9f) - this.mPaddingTop) - this.mPaddingBottom;
        int maxWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.measure(View.MeasureSpec.makeMeasureSpec(childWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(childHeight, Integer.MIN_VALUE));
            if (child != this.mHighlight && child != this.mClickFeedback) {
                int childMeasuredWidth = child.getMeasuredWidth();
                int childMeasuredHeight = child.getMeasuredHeight();
                if (childMeasuredWidth > maxWidth) {
                    maxWidth = childMeasuredWidth;
                }
                if (childMeasuredHeight > maxHeight) {
                    maxHeight = childMeasuredHeight;
                }
            }
        }
        this.mNewPerspectiveShiftX = 0.1f * measuredWidth;
        this.mNewPerspectiveShiftY = 0.1f * measuredHeight;
        if (maxWidth > 0 && count > 0 && maxWidth < childWidth) {
            this.mNewPerspectiveShiftX = measuredWidth - maxWidth;
        }
        if (maxHeight > 0 && count > 0 && maxHeight < childHeight) {
            this.mNewPerspectiveShiftY = measuredHeight - maxHeight;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AdapterViewAnimator, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        boolean haveChildRefSize = (this.mReferenceChildWidth == -1 || this.mReferenceChildHeight == -1) ? false : true;
        if (heightSpecMode == 0) {
            heightSpecSize = haveChildRefSize ? Math.round(this.mReferenceChildHeight * (1.0f + 1.1111112f)) + this.mPaddingTop + this.mPaddingBottom : 0;
        } else if (heightSpecMode == Integer.MIN_VALUE) {
            if (haveChildRefSize) {
                int height = Math.round(this.mReferenceChildHeight * (1.0f + 1.1111112f)) + this.mPaddingTop + this.mPaddingBottom;
                if (height <= heightSpecSize) {
                    heightSpecSize = height;
                } else {
                    heightSpecSize |= 16777216;
                }
            } else {
                heightSpecSize = 0;
            }
        }
        if (widthSpecMode == 0) {
            widthSpecSize = haveChildRefSize ? Math.round(this.mReferenceChildWidth * (1.0f + 1.1111112f)) + this.mPaddingLeft + this.mPaddingRight : 0;
        } else if (heightSpecMode == Integer.MIN_VALUE) {
            if (haveChildRefSize) {
                int width = this.mReferenceChildWidth + this.mPaddingLeft + this.mPaddingRight;
                if (width <= widthSpecSize) {
                    widthSpecSize = width;
                } else {
                    widthSpecSize |= 16777216;
                }
            } else {
                widthSpecSize = 0;
            }
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);
        measureChildren();
    }

    @Override // android.widget.AdapterViewAnimator, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(StackView.class.getName());
    }

    @Override // android.widget.AdapterViewAnimator, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(StackView.class.getName());
        info.setScrollable(getChildCount() > 1);
        if (isEnabled()) {
            if (getDisplayedChild() < getChildCount() - 1) {
                info.addAction(4096);
            }
            if (getDisplayedChild() > 0) {
                info.addAction(8192);
            }
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }
        if (!isEnabled()) {
            return false;
        }
        switch (action) {
            case 4096:
                if (getDisplayedChild() < getChildCount() - 1) {
                    showNext();
                    return true;
                }
                return false;
            case 8192:
                if (getDisplayedChild() > 0) {
                    showPrevious();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: StackView$LayoutParams.class */
    public class LayoutParams extends ViewGroup.LayoutParams {
        int horizontalOffset;
        int verticalOffset;
        View mView;
        private final Rect parentRect;
        private final Rect invalidateRect;
        private final RectF invalidateRectf;
        private final Rect globalInvalidateRect;

        LayoutParams(View view) {
            super(0, 0);
            this.parentRect = new Rect();
            this.invalidateRect = new Rect();
            this.invalidateRectf = new RectF();
            this.globalInvalidateRect = new Rect();
            this.width = 0;
            this.height = 0;
            this.horizontalOffset = 0;
            this.verticalOffset = 0;
            this.mView = view;
        }

        LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.parentRect = new Rect();
            this.invalidateRect = new Rect();
            this.invalidateRectf = new RectF();
            this.globalInvalidateRect = new Rect();
            this.horizontalOffset = 0;
            this.verticalOffset = 0;
            this.width = 0;
            this.height = 0;
        }

        void invalidateGlobalRegion(View v, Rect r) {
            this.globalInvalidateRect.set(r);
            this.globalInvalidateRect.union(0, 0, StackView.this.getWidth(), StackView.this.getHeight());
            View p = v;
            if (v.getParent() == null || !(v.getParent() instanceof View)) {
                return;
            }
            boolean firstPass = true;
            this.parentRect.set(0, 0, 0, 0);
            while (p.getParent() != null && (p.getParent() instanceof View) && !this.parentRect.contains(this.globalInvalidateRect)) {
                if (!firstPass) {
                    this.globalInvalidateRect.offset(p.getLeft() - p.getScrollX(), p.getTop() - p.getScrollY());
                }
                firstPass = false;
                p = (View) p.getParent();
                this.parentRect.set(p.getScrollX(), p.getScrollY(), p.getWidth() + p.getScrollX(), p.getHeight() + p.getScrollY());
                p.invalidate(this.globalInvalidateRect.left, this.globalInvalidateRect.top, this.globalInvalidateRect.right, this.globalInvalidateRect.bottom);
            }
            p.invalidate(this.globalInvalidateRect.left, this.globalInvalidateRect.top, this.globalInvalidateRect.right, this.globalInvalidateRect.bottom);
        }

        Rect getInvalidateRect() {
            return this.invalidateRect;
        }

        void resetInvalidateRect() {
            this.invalidateRect.set(0, 0, 0, 0);
        }

        public void setVerticalOffset(int newVerticalOffset) {
            setOffsets(this.horizontalOffset, newVerticalOffset);
        }

        public void setHorizontalOffset(int newHorizontalOffset) {
            setOffsets(newHorizontalOffset, this.verticalOffset);
        }

        public void setOffsets(int newHorizontalOffset, int newVerticalOffset) {
            int horizontalOffsetDelta = newHorizontalOffset - this.horizontalOffset;
            this.horizontalOffset = newHorizontalOffset;
            int verticalOffsetDelta = newVerticalOffset - this.verticalOffset;
            this.verticalOffset = newVerticalOffset;
            if (this.mView != null) {
                this.mView.requestLayout();
                int left = Math.min(this.mView.getLeft() + horizontalOffsetDelta, this.mView.getLeft());
                int right = Math.max(this.mView.getRight() + horizontalOffsetDelta, this.mView.getRight());
                int top = Math.min(this.mView.getTop() + verticalOffsetDelta, this.mView.getTop());
                int bottom = Math.max(this.mView.getBottom() + verticalOffsetDelta, this.mView.getBottom());
                this.invalidateRectf.set(left, top, right, bottom);
                float xoffset = -this.invalidateRectf.left;
                float yoffset = -this.invalidateRectf.top;
                this.invalidateRectf.offset(xoffset, yoffset);
                this.mView.getMatrix().mapRect(this.invalidateRectf);
                this.invalidateRectf.offset(-xoffset, -yoffset);
                this.invalidateRect.set((int) Math.floor(this.invalidateRectf.left), (int) Math.floor(this.invalidateRectf.top), (int) Math.ceil(this.invalidateRectf.right), (int) Math.ceil(this.invalidateRectf.bottom));
                invalidateGlobalRegion(this.mView, this.invalidateRect);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StackView$HolographicHelper.class */
    public static class HolographicHelper {
        private static final int RES_OUT = 0;
        private static final int CLICK_FEEDBACK = 1;
        private float mDensity;
        private BlurMaskFilter mSmallBlurMaskFilter;
        private BlurMaskFilter mLargeBlurMaskFilter;
        private final Paint mHolographicPaint = new Paint();
        private final Paint mErasePaint = new Paint();
        private final Paint mBlurPaint = new Paint();
        private final Canvas mCanvas = new Canvas();
        private final Canvas mMaskCanvas = new Canvas();
        private final int[] mTmpXY = new int[2];
        private final Matrix mIdentityMatrix = new Matrix();

        HolographicHelper(Context context) {
            this.mDensity = context.getResources().getDisplayMetrics().density;
            this.mHolographicPaint.setFilterBitmap(true);
            this.mHolographicPaint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));
            this.mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            this.mErasePaint.setFilterBitmap(true);
            this.mSmallBlurMaskFilter = new BlurMaskFilter(2.0f * this.mDensity, BlurMaskFilter.Blur.NORMAL);
            this.mLargeBlurMaskFilter = new BlurMaskFilter(4.0f * this.mDensity, BlurMaskFilter.Blur.NORMAL);
        }

        Bitmap createClickOutline(View v, int color) {
            return createOutline(v, 1, color);
        }

        Bitmap createResOutline(View v, int color) {
            return createOutline(v, 0, color);
        }

        Bitmap createOutline(View v, int type, int color) {
            this.mHolographicPaint.setColor(color);
            if (type == 0) {
                this.mBlurPaint.setMaskFilter(this.mSmallBlurMaskFilter);
            } else if (type == 1) {
                this.mBlurPaint.setMaskFilter(this.mLargeBlurMaskFilter);
            }
            if (v.getMeasuredWidth() == 0 || v.getMeasuredHeight() == 0) {
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(v.getResources().getDisplayMetrics(), v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            this.mCanvas.setBitmap(bitmap);
            float rotationX = v.getRotationX();
            float rotation = v.getRotation();
            float translationY = v.getTranslationY();
            float translationX = v.getTranslationX();
            v.setRotationX(0.0f);
            v.setRotation(0.0f);
            v.setTranslationY(0.0f);
            v.setTranslationX(0.0f);
            v.draw(this.mCanvas);
            v.setRotationX(rotationX);
            v.setRotation(rotation);
            v.setTranslationY(translationY);
            v.setTranslationX(translationX);
            drawOutline(this.mCanvas, bitmap);
            this.mCanvas.setBitmap(null);
            return bitmap;
        }

        void drawOutline(Canvas dest, Bitmap src) {
            int[] xy = this.mTmpXY;
            Bitmap mask = src.extractAlpha(this.mBlurPaint, xy);
            this.mMaskCanvas.setBitmap(mask);
            this.mMaskCanvas.drawBitmap(src, -xy[0], -xy[1], this.mErasePaint);
            dest.drawColor(0, PorterDuff.Mode.CLEAR);
            dest.setMatrix(this.mIdentityMatrix);
            dest.drawBitmap(mask, xy[0], xy[1], this.mHolographicPaint);
            this.mMaskCanvas.setBitmap(null);
            mask.recycle();
        }
    }
}