package com.android.internal.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.android.internal.R;
import gov.nist.core.Separators;

@RemoteViews.RemoteView
/* loaded from: SizeAdaptiveLayout.class */
public class SizeAdaptiveLayout extends ViewGroup {
    private static final String TAG = "SizeAdaptiveLayout";
    private static final boolean DEBUG = false;
    private static final boolean REPORT_BAD_BOUNDS = true;
    private static final long CROSSFADE_TIME = 250;
    private static final int MIN_VALID_HEIGHT = 1;
    private static final int MAX_VALID_HEIGHT = 0;
    private View mActiveChild;
    private View mLastActive;
    private AnimatorSet mTransitionAnimation;
    private Animator.AnimatorListener mAnimatorListener;
    private ObjectAnimator mFadePanel;
    private ObjectAnimator mFadeView;
    private int mCanceledAnimationCount;
    private View mEnteringView;
    private View mLeavingView;
    private View mModestyPanel;
    private int mModestyPanelTop;

    static /* synthetic */ int access$010(SizeAdaptiveLayout x0) {
        int i = x0.mCanceledAnimationCount;
        x0.mCanceledAnimationCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$008(SizeAdaptiveLayout x0) {
        int i = x0.mCanceledAnimationCount;
        x0.mCanceledAnimationCount = i + 1;
        return i;
    }

    public SizeAdaptiveLayout(Context context) {
        super(context);
        initialize();
    }

    public SizeAdaptiveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SizeAdaptiveLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        this.mModestyPanel = new View(getContext());
        Drawable background = getBackground();
        if (background instanceof StateListDrawable) {
            StateListDrawable sld = (StateListDrawable) background;
            sld.setState(StateSet.WILD_CARD);
            background = sld.getCurrent();
        }
        if (background instanceof ColorDrawable) {
            this.mModestyPanel.setBackgroundDrawable(background);
        } else {
            this.mModestyPanel.setBackgroundColor(-16777216);
        }
        LayoutParams layout = new LayoutParams(-1, -1);
        this.mModestyPanel.setLayoutParams(layout);
        addView(this.mModestyPanel);
        this.mFadePanel = ObjectAnimator.ofFloat(this.mModestyPanel, "alpha", 0.0f);
        this.mFadeView = ObjectAnimator.ofFloat((Object) null, "alpha", 0.0f);
        this.mAnimatorListener = new BringToFrontOnEnd();
        this.mTransitionAnimation = new AnimatorSet();
        this.mTransitionAnimation.play(this.mFadeView).with(this.mFadePanel);
        this.mTransitionAnimation.setDuration(CROSSFADE_TIME);
        this.mTransitionAnimation.addListener(this.mAnimatorListener);
    }

    public Animator getTransitionAnimation() {
        return this.mTransitionAnimation;
    }

    public View getModestyPanel() {
        return this.mModestyPanel;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        this.mLastActive = null;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View model = selectActiveChild(heightMeasureSpec);
        LayoutParams layoutParams = (LayoutParams) model.getLayoutParams();
        measureChild(model, widthMeasureSpec, heightMeasureSpec);
        int childHeight = model.getMeasuredHeight();
        int childWidth = model.getMeasuredHeight();
        int childState = combineMeasuredStates(0, model.getMeasuredState());
        int resolvedWidth = resolveSizeAndState(childWidth, widthMeasureSpec, childState);
        int resolvedHeight = resolveSizeAndState(childHeight, heightMeasureSpec, childState);
        int boundedHeight = clampSizeToBounds(resolvedHeight, model);
        setMeasuredDimension(resolvedWidth, boundedHeight);
    }

    private int clampSizeToBounds(int measuredHeight, View child) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int heightIn = 16777215 & measuredHeight;
        int height = Math.max(heightIn, lp.minHeight);
        if (lp.maxHeight != -1) {
            height = Math.min(height, lp.maxHeight);
        }
        if (heightIn != height) {
            Log.d(TAG, this + "child view " + child + Separators.SP + "measured out of bounds at " + heightIn + "px clamped to " + height + "px");
        }
        return height;
    }

    private View selectActiveChild(int heightMeasureSpec) {
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        View unboundedView = null;
        View tallestView = null;
        int tallestViewSize = 0;
        View smallestView = null;
        int smallestViewSize = Integer.MAX_VALUE;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != this.mModestyPanel) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.maxHeight == -1 && unboundedView == null) {
                    unboundedView = child;
                }
                if (lp.maxHeight > tallestViewSize) {
                    tallestViewSize = lp.maxHeight;
                    tallestView = child;
                }
                if (lp.minHeight < smallestViewSize) {
                    smallestViewSize = lp.minHeight;
                    smallestView = child;
                }
                if (heightMode != 0 && heightSize >= lp.minHeight && heightSize <= lp.maxHeight) {
                    return child;
                }
            }
        }
        if (unboundedView != null) {
            tallestView = unboundedView;
        }
        if (heightMode == 0 || heightSize > tallestViewSize) {
            return tallestView;
        }
        return smallestView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mLastActive = this.mActiveChild;
        int measureSpec = View.MeasureSpec.makeMeasureSpec(bottom - top, 1073741824);
        this.mActiveChild = selectActiveChild(measureSpec);
        this.mActiveChild.setVisibility(0);
        if (this.mLastActive != this.mActiveChild && this.mLastActive != null) {
            this.mEnteringView = this.mActiveChild;
            this.mLeavingView = this.mLastActive;
            this.mEnteringView.setAlpha(1.0f);
            this.mModestyPanel.setAlpha(1.0f);
            this.mModestyPanel.bringToFront();
            this.mModestyPanelTop = this.mLeavingView.getHeight();
            this.mModestyPanel.setVisibility(0);
            this.mLeavingView.bringToFront();
            if (this.mTransitionAnimation.isRunning()) {
                this.mTransitionAnimation.cancel();
            }
            this.mFadeView.setTarget(this.mLeavingView);
            this.mFadeView.setFloatValues(0.0f);
            this.mFadePanel.setFloatValues(0.0f);
            this.mTransitionAnimation.setupStartValues();
            this.mTransitionAnimation.start();
        }
        int childWidth = this.mActiveChild.getMeasuredWidth();
        int childHeight = this.mActiveChild.getMeasuredHeight();
        this.mActiveChild.layout(0, 0, childWidth, childHeight);
        this.mModestyPanel.layout(0, this.mModestyPanelTop, childWidth, this.mModestyPanelTop + childHeight);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* loaded from: SizeAdaptiveLayout$LayoutParams.class */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        @ViewDebug.ExportedProperty(category = "layout")
        public int minHeight;
        @ViewDebug.ExportedProperty(category = "layout")
        public int maxHeight;
        public static final int UNBOUNDED = -1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SizeAdaptiveLayout_Layout);
            this.minHeight = a.getDimensionPixelSize(1, 0);
            try {
                this.maxHeight = a.getLayoutDimension(0, -1);
            } catch (Exception e) {
            }
            a.recycle();
        }

        public LayoutParams(int width, int height, int minHeight, int maxHeight) {
            super(width, height);
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }

        public LayoutParams(int width, int height) {
            this(width, height, -1, -1);
        }

        public LayoutParams() {
            this(0, 0);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
            this.minHeight = -1;
            this.maxHeight = -1;
        }

        @Override // android.view.ViewGroup.LayoutParams
        public String debug(String output) {
            return output + "SizeAdaptiveLayout.LayoutParams={, max=" + this.maxHeight + ", max=" + this.minHeight + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SizeAdaptiveLayout$BringToFrontOnEnd.class */
    public class BringToFrontOnEnd implements Animator.AnimatorListener {
        static final /* synthetic */ boolean $assertionsDisabled;

        BringToFrontOnEnd() {
        }

        static {
            $assertionsDisabled = !SizeAdaptiveLayout.class.desiredAssertionStatus();
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            if (SizeAdaptiveLayout.this.mCanceledAnimationCount == 0) {
                SizeAdaptiveLayout.this.mLeavingView.setVisibility(8);
                SizeAdaptiveLayout.this.mModestyPanel.setVisibility(8);
                SizeAdaptiveLayout.this.mEnteringView.bringToFront();
                SizeAdaptiveLayout.this.mEnteringView = null;
                SizeAdaptiveLayout.this.mLeavingView = null;
                return;
            }
            SizeAdaptiveLayout.access$010(SizeAdaptiveLayout.this);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            SizeAdaptiveLayout.access$008(SizeAdaptiveLayout.this);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animation) {
            if (!$assertionsDisabled) {
                throw new AssertionError();
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
        }
    }
}