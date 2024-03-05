package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.appcompat.R;
import android.support.v7.internal.VersionUtils;
import android.support.v7.internal.app.WindowCallback;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: ActionBarOverlayLayout.class */
public class ActionBarOverlayLayout extends ViewGroup implements DecorContentParent {
    static final int[] ATTRS = {R.attr.actionBarSize, 16842841};
    private static final String TAG = "ActionBarOverlayLayout";
    private final int ACTION_BAR_ANIMATE_DELAY;
    private ActionBarContainer mActionBarBottom;
    private int mActionBarHeight;
    private ActionBarContainer mActionBarTop;
    private ActionBarVisibilityCallback mActionBarVisibilityCallback;
    private final Runnable mAddActionBarHideOffset;
    private boolean mAnimatingForFling;
    private final Rect mBaseContentInsets;
    private final Rect mBaseInnerInsets;
    private final ViewPropertyAnimatorListener mBottomAnimatorListener;
    private ContentFrameLayout mContent;
    private final Rect mContentInsets;
    private ViewPropertyAnimatorCompat mCurrentActionBarBottomAnimator;
    private ViewPropertyAnimatorCompat mCurrentActionBarTopAnimator;
    private DecorToolbar mDecorToolbar;
    private ScrollerCompat mFlingEstimator;
    private boolean mHasNonEmbeddedTabs;
    private boolean mHideOnContentScroll;
    private int mHideOnContentScrollReference;
    private boolean mIgnoreWindowContentOverlay;
    private final Rect mInnerInsets;
    private final Rect mLastBaseContentInsets;
    private final Rect mLastInnerInsets;
    private int mLastSystemUiVisibility;
    private boolean mOverlayMode;
    private final Runnable mRemoveActionBarHideOffset;
    private final ViewPropertyAnimatorListener mTopAnimatorListener;
    private Drawable mWindowContentOverlay;
    private int mWindowVisibility;

    /* loaded from: ActionBarOverlayLayout$ActionBarVisibilityCallback.class */
    public interface ActionBarVisibilityCallback {
        void enableContentAnimations(boolean z);

        void hideForSystem();

        void onContentScrollStarted();

        void onContentScrollStopped();

        void onWindowVisibilityChanged(int i);

        void showForSystem();
    }

    /* loaded from: ActionBarOverlayLayout$LayoutParams.class */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }
    }

    public ActionBarOverlayLayout(Context context) {
        super(context);
        this.mWindowVisibility = 0;
        this.mBaseContentInsets = new Rect();
        this.mLastBaseContentInsets = new Rect();
        this.mContentInsets = new Rect();
        this.mBaseInnerInsets = new Rect();
        this.mInnerInsets = new Rect();
        this.mLastInnerInsets = new Rect();
        this.ACTION_BAR_ANIMATE_DELAY = 600;
        this.mTopAnimatorListener = new ViewPropertyAnimatorListenerAdapter(this) { // from class: android.support.v7.internal.widget.ActionBarOverlayLayout.1
            final ActionBarOverlayLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
            public void onAnimationCancel(View view) {
                this.this$0.mCurrentActionBarTopAnimator = null;
                this.this$0.mAnimatingForFling = false;
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
            public void onAnimationEnd(View view) {
                this.this$0.mCurrentActionBarTopAnimator = null;
                this.this$0.mAnimatingForFling = false;
            }
        };
        this.mBottomAnimatorListener = new ViewPropertyAnimatorListenerAdapter(this) { // from class: android.support.v7.internal.widget.ActionBarOverlayLayout.2
            final ActionBarOverlayLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
            public void onAnimationCancel(View view) {
                this.this$0.mCurrentActionBarBottomAnimator = null;
                this.this$0.mAnimatingForFling = false;
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
            public void onAnimationEnd(View view) {
                this.this$0.mCurrentActionBarBottomAnimator = null;
                this.this$0.mAnimatingForFling = false;
            }
        };
        this.mRemoveActionBarHideOffset = new Runnable(this) { // from class: android.support.v7.internal.widget.ActionBarOverlayLayout.3
            final ActionBarOverlayLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.haltActionBarHideOffsetAnimations();
                ActionBarOverlayLayout actionBarOverlayLayout = this.this$0;
                actionBarOverlayLayout.mCurrentActionBarTopAnimator = ViewCompat.animate(actionBarOverlayLayout.mActionBarTop).translationY(0.0f).setListener(this.this$0.mTopAnimatorListener);
                if (this.this$0.mActionBarBottom == null || this.this$0.mActionBarBottom.getVisibility() == 8) {
                    return;
                }
                ActionBarOverlayLayout actionBarOverlayLayout2 = this.this$0;
                actionBarOverlayLayout2.mCurrentActionBarBottomAnimator = ViewCompat.animate(actionBarOverlayLayout2.mActionBarBottom).translationY(0.0f).setListener(this.this$0.mBottomAnimatorListener);
            }
        };
        this.mAddActionBarHideOffset = new Runnable(this) { // from class: android.support.v7.internal.widget.ActionBarOverlayLayout.4
            final ActionBarOverlayLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.haltActionBarHideOffsetAnimations();
                ActionBarOverlayLayout actionBarOverlayLayout = this.this$0;
                actionBarOverlayLayout.mCurrentActionBarTopAnimator = ViewCompat.animate(actionBarOverlayLayout.mActionBarTop).translationY(-this.this$0.mActionBarTop.getHeight()).setListener(this.this$0.mTopAnimatorListener);
                if (this.this$0.mActionBarBottom == null || this.this$0.mActionBarBottom.getVisibility() == 8) {
                    return;
                }
                ActionBarOverlayLayout actionBarOverlayLayout2 = this.this$0;
                actionBarOverlayLayout2.mCurrentActionBarBottomAnimator = ViewCompat.animate(actionBarOverlayLayout2.mActionBarBottom).translationY(this.this$0.mActionBarBottom.getHeight()).setListener(this.this$0.mBottomAnimatorListener);
            }
        };
        init(context);
    }

    public ActionBarOverlayLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mWindowVisibility = 0;
        this.mBaseContentInsets = new Rect();
        this.mLastBaseContentInsets = new Rect();
        this.mContentInsets = new Rect();
        this.mBaseInnerInsets = new Rect();
        this.mInnerInsets = new Rect();
        this.mLastInnerInsets = new Rect();
        this.ACTION_BAR_ANIMATE_DELAY = 600;
        this.mTopAnimatorListener = new ViewPropertyAnimatorListenerAdapter(this) { // from class: android.support.v7.internal.widget.ActionBarOverlayLayout.1
            final ActionBarOverlayLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
            public void onAnimationCancel(View view) {
                this.this$0.mCurrentActionBarTopAnimator = null;
                this.this$0.mAnimatingForFling = false;
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
            public void onAnimationEnd(View view) {
                this.this$0.mCurrentActionBarTopAnimator = null;
                this.this$0.mAnimatingForFling = false;
            }
        };
        this.mBottomAnimatorListener = new ViewPropertyAnimatorListenerAdapter(this) { // from class: android.support.v7.internal.widget.ActionBarOverlayLayout.2
            final ActionBarOverlayLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
            public void onAnimationCancel(View view) {
                this.this$0.mCurrentActionBarBottomAnimator = null;
                this.this$0.mAnimatingForFling = false;
            }

            @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
            public void onAnimationEnd(View view) {
                this.this$0.mCurrentActionBarBottomAnimator = null;
                this.this$0.mAnimatingForFling = false;
            }
        };
        this.mRemoveActionBarHideOffset = new Runnable(this) { // from class: android.support.v7.internal.widget.ActionBarOverlayLayout.3
            final ActionBarOverlayLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.haltActionBarHideOffsetAnimations();
                ActionBarOverlayLayout actionBarOverlayLayout = this.this$0;
                actionBarOverlayLayout.mCurrentActionBarTopAnimator = ViewCompat.animate(actionBarOverlayLayout.mActionBarTop).translationY(0.0f).setListener(this.this$0.mTopAnimatorListener);
                if (this.this$0.mActionBarBottom == null || this.this$0.mActionBarBottom.getVisibility() == 8) {
                    return;
                }
                ActionBarOverlayLayout actionBarOverlayLayout2 = this.this$0;
                actionBarOverlayLayout2.mCurrentActionBarBottomAnimator = ViewCompat.animate(actionBarOverlayLayout2.mActionBarBottom).translationY(0.0f).setListener(this.this$0.mBottomAnimatorListener);
            }
        };
        this.mAddActionBarHideOffset = new Runnable(this) { // from class: android.support.v7.internal.widget.ActionBarOverlayLayout.4
            final ActionBarOverlayLayout this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.haltActionBarHideOffsetAnimations();
                ActionBarOverlayLayout actionBarOverlayLayout = this.this$0;
                actionBarOverlayLayout.mCurrentActionBarTopAnimator = ViewCompat.animate(actionBarOverlayLayout.mActionBarTop).translationY(-this.this$0.mActionBarTop.getHeight()).setListener(this.this$0.mTopAnimatorListener);
                if (this.this$0.mActionBarBottom == null || this.this$0.mActionBarBottom.getVisibility() == 8) {
                    return;
                }
                ActionBarOverlayLayout actionBarOverlayLayout2 = this.this$0;
                actionBarOverlayLayout2.mCurrentActionBarBottomAnimator = ViewCompat.animate(actionBarOverlayLayout2.mActionBarBottom).translationY(this.this$0.mActionBarBottom.getHeight()).setListener(this.this$0.mBottomAnimatorListener);
            }
        };
        init(context);
    }

    private void addActionBarHideOffset() {
        haltActionBarHideOffsetAnimations();
        this.mAddActionBarHideOffset.run();
    }

    private boolean applyInsets(View view, Rect rect, boolean z, boolean z2, boolean z3, boolean z4) {
        boolean z5;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (!z || layoutParams.leftMargin == rect.left) {
            z5 = false;
        } else {
            z5 = true;
            layoutParams.leftMargin = rect.left;
        }
        if (z2 && layoutParams.topMargin != rect.top) {
            z5 = true;
            layoutParams.topMargin = rect.top;
        }
        if (z4 && layoutParams.rightMargin != rect.right) {
            z5 = true;
            layoutParams.rightMargin = rect.right;
        }
        if (z3 && layoutParams.bottomMargin != rect.bottom) {
            z5 = true;
            layoutParams.bottomMargin = rect.bottom;
        }
        return z5;
    }

    private DecorToolbar getDecorToolbar(View view) {
        if (view instanceof DecorToolbar) {
            return (DecorToolbar) view;
        }
        if (view instanceof Toolbar) {
            return ((Toolbar) view).getWrapper();
        }
        throw new IllegalStateException("Can't make a decor toolbar out of " + view.getClass().getSimpleName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void haltActionBarHideOffsetAnimations() {
        removeCallbacks(this.mRemoveActionBarHideOffset);
        removeCallbacks(this.mAddActionBarHideOffset);
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = this.mCurrentActionBarTopAnimator;
        if (viewPropertyAnimatorCompat != null) {
            viewPropertyAnimatorCompat.cancel();
        }
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat2 = this.mCurrentActionBarBottomAnimator;
        if (viewPropertyAnimatorCompat2 != null) {
            viewPropertyAnimatorCompat2.cancel();
        }
    }

    private void init(Context context) {
        TypedArray obtainStyledAttributes = getContext().getTheme().obtainStyledAttributes(ATTRS);
        this.mActionBarHeight = obtainStyledAttributes.getDimensionPixelSize(0, 0);
        this.mWindowContentOverlay = obtainStyledAttributes.getDrawable(1);
        setWillNotDraw(this.mWindowContentOverlay == null);
        obtainStyledAttributes.recycle();
        boolean z = false;
        if (context.getApplicationInfo().targetSdkVersion < 19) {
            z = true;
        }
        this.mIgnoreWindowContentOverlay = z;
        this.mFlingEstimator = ScrollerCompat.create(context);
    }

    private void postAddActionBarHideOffset() {
        haltActionBarHideOffsetAnimations();
        postDelayed(this.mAddActionBarHideOffset, 600L);
    }

    private void postRemoveActionBarHideOffset() {
        haltActionBarHideOffsetAnimations();
        postDelayed(this.mRemoveActionBarHideOffset, 600L);
    }

    private void removeActionBarHideOffset() {
        haltActionBarHideOffsetAnimations();
        this.mRemoveActionBarHideOffset.run();
    }

    private boolean shouldHideActionBarOnFling(float f, float f2) {
        this.mFlingEstimator.fling(0, 0, 0, (int) f2, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return this.mFlingEstimator.getFinalY() > this.mActionBarTop.getHeight();
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public boolean canShowOverflowMenu() {
        pullChildren();
        return this.mDecorToolbar.canShowOverflowMenu();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void dismissPopups() {
        pullChildren();
        this.mDecorToolbar.dismissPopupMenus();
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mWindowContentOverlay == null || this.mIgnoreWindowContentOverlay) {
            return;
        }
        int bottom = this.mActionBarTop.getVisibility() == 0 ? (int) (this.mActionBarTop.getBottom() + ViewCompat.getTranslationY(this.mActionBarTop) + 0.5f) : 0;
        this.mWindowContentOverlay.setBounds(0, bottom, getWidth(), this.mWindowContentOverlay.getIntrinsicHeight() + bottom);
        this.mWindowContentOverlay.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public boolean fitSystemWindows(Rect rect) {
        pullChildren();
        if ((ViewCompat.getWindowSystemUiVisibility(this) & 256) != 0) {
        }
        boolean applyInsets = applyInsets(this.mActionBarTop, rect, true, true, false, true);
        ActionBarContainer actionBarContainer = this.mActionBarBottom;
        if (actionBarContainer != null) {
            applyInsets |= applyInsets(actionBarContainer, rect, true, false, true, true);
        }
        this.mBaseInnerInsets.set(rect);
        ViewUtils.computeFitSystemWindows(this, this.mBaseInnerInsets, this.mBaseContentInsets);
        if (!this.mLastBaseContentInsets.equals(this.mBaseContentInsets)) {
            applyInsets = true;
            this.mLastBaseContentInsets.set(this.mBaseContentInsets);
        }
        if (applyInsets) {
            requestLayout();
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    public int getActionBarHideOffset() {
        ActionBarContainer actionBarContainer = this.mActionBarTop;
        return actionBarContainer != null ? -((int) ViewCompat.getTranslationY(actionBarContainer)) : 0;
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public CharSequence getTitle() {
        pullChildren();
        return this.mDecorToolbar.getTitle();
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public boolean hasIcon() {
        pullChildren();
        return this.mDecorToolbar.hasIcon();
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public boolean hasLogo() {
        pullChildren();
        return this.mDecorToolbar.hasLogo();
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public boolean hideOverflowMenu() {
        pullChildren();
        return this.mDecorToolbar.hideOverflowMenu();
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void initFeature(int i) {
        pullChildren();
        if (i == 2) {
            this.mDecorToolbar.initProgress();
        } else if (i == 5) {
            this.mDecorToolbar.initIndeterminateProgress();
        } else if (i != 9) {
        } else {
            setOverlayMode(true);
        }
    }

    public boolean isHideOnContentScrollEnabled() {
        return this.mHideOnContentScroll;
    }

    public boolean isInOverlayMode() {
        return this.mOverlayMode;
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public boolean isOverflowMenuShowPending() {
        pullChildren();
        return this.mDecorToolbar.isOverflowMenuShowPending();
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public boolean isOverflowMenuShowing() {
        pullChildren();
        return this.mDecorToolbar.isOverflowMenuShowing();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= 8) {
            super.onConfigurationChanged(configuration);
        }
        init(getContext());
        ViewCompat.requestApplyInsets(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        haltActionBarHideOffsetAnimations();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        int paddingLeft = getPaddingLeft();
        getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int measuredWidth = childAt.getMeasuredWidth();
                int measuredHeight = childAt.getMeasuredHeight();
                int i6 = layoutParams.leftMargin + paddingLeft;
                int i7 = childAt == this.mActionBarBottom ? (((i4 - i2) - paddingBottom) - measuredHeight) - layoutParams.bottomMargin : layoutParams.topMargin + paddingTop;
                childAt.layout(i6, i7, i6 + measuredWidth, i7 + measuredHeight);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        ActionBarContainer actionBarContainer;
        pullChildren();
        int i3 = 0;
        int i4 = 0;
        measureChildWithMargins(this.mActionBarTop, i, 0, i2, 0);
        LayoutParams layoutParams = (LayoutParams) this.mActionBarTop.getLayoutParams();
        int max = Math.max(0, this.mActionBarTop.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
        int max2 = Math.max(0, this.mActionBarTop.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
        int combineMeasuredStates = ViewUtils.combineMeasuredStates(0, ViewCompat.getMeasuredState(this.mActionBarTop));
        ActionBarContainer actionBarContainer2 = this.mActionBarBottom;
        if (actionBarContainer2 != null) {
            measureChildWithMargins(actionBarContainer2, i, 0, i2, 0);
            LayoutParams layoutParams2 = (LayoutParams) this.mActionBarBottom.getLayoutParams();
            max = Math.max(max, this.mActionBarBottom.getMeasuredWidth() + layoutParams2.leftMargin + layoutParams2.rightMargin);
            max2 = Math.max(max2, this.mActionBarBottom.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin);
            combineMeasuredStates = ViewUtils.combineMeasuredStates(combineMeasuredStates, ViewCompat.getMeasuredState(this.mActionBarBottom));
        }
        boolean z = (ViewCompat.getWindowSystemUiVisibility(this) & 256) != 0;
        if (z) {
            i3 = this.mActionBarHeight;
            if (this.mHasNonEmbeddedTabs && this.mActionBarTop.getTabContainer() != null) {
                i3 += this.mActionBarHeight;
            }
        } else if (this.mActionBarTop.getVisibility() != 8) {
            i3 = this.mActionBarTop.getMeasuredHeight();
        }
        if (this.mDecorToolbar.isSplit() && (actionBarContainer = this.mActionBarBottom) != null) {
            i4 = z ? this.mActionBarHeight : actionBarContainer.getMeasuredHeight();
        }
        this.mContentInsets.set(this.mBaseContentInsets);
        this.mInnerInsets.set(this.mBaseInnerInsets);
        if (this.mOverlayMode || z) {
            this.mInnerInsets.top += i3;
            this.mInnerInsets.bottom += i4;
        } else {
            this.mContentInsets.top += i3;
            this.mContentInsets.bottom += i4;
        }
        applyInsets(this.mContent, this.mContentInsets, true, true, true, true);
        if (!this.mLastInnerInsets.equals(this.mInnerInsets)) {
            this.mLastInnerInsets.set(this.mInnerInsets);
            this.mContent.dispatchFitSystemWindows(this.mInnerInsets);
        }
        measureChildWithMargins(this.mContent, i, 0, i2, 0);
        LayoutParams layoutParams3 = (LayoutParams) this.mContent.getLayoutParams();
        int max3 = Math.max(max, this.mContent.getMeasuredWidth() + layoutParams3.leftMargin + layoutParams3.rightMargin);
        int max4 = Math.max(max2, this.mContent.getMeasuredHeight() + layoutParams3.topMargin + layoutParams3.bottomMargin);
        int combineMeasuredStates2 = ViewUtils.combineMeasuredStates(combineMeasuredStates, ViewCompat.getMeasuredState(this.mContent));
        setMeasuredDimension(ViewCompat.resolveSizeAndState(Math.max(max3 + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), i, combineMeasuredStates2), ViewCompat.resolveSizeAndState(Math.max(max4 + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight()), i2, combineMeasuredStates2 << 16));
    }

    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        if (this.mHideOnContentScroll && z) {
            if (shouldHideActionBarOnFling(f, f2)) {
                addActionBarHideOffset();
            } else {
                removeActionBarHideOffset();
            }
            this.mAnimatingForFling = true;
            return true;
        }
        return false;
    }

    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        this.mHideOnContentScrollReference += i2;
        setActionBarHideOffset(this.mHideOnContentScrollReference);
    }

    public void onNestedScrollAccepted(View view, View view2, int i) {
        super.onNestedScrollAccepted(view, view2, i);
        this.mHideOnContentScrollReference = getActionBarHideOffset();
        haltActionBarHideOffsetAnimations();
        ActionBarVisibilityCallback actionBarVisibilityCallback = this.mActionBarVisibilityCallback;
        if (actionBarVisibilityCallback != null) {
            actionBarVisibilityCallback.onContentScrollStarted();
        }
    }

    public boolean onStartNestedScroll(View view, View view2, int i) {
        if ((i & 2) == 0 || this.mActionBarTop.getVisibility() != 0) {
            return false;
        }
        return this.mHideOnContentScroll;
    }

    public void onStopNestedScroll(View view) {
        super.onStopNestedScroll(view);
        if (this.mHideOnContentScroll && !this.mAnimatingForFling) {
            if (this.mHideOnContentScrollReference <= this.mActionBarTop.getHeight()) {
                postRemoveActionBarHideOffset();
            } else {
                postAddActionBarHideOffset();
            }
        }
        ActionBarVisibilityCallback actionBarVisibilityCallback = this.mActionBarVisibilityCallback;
        if (actionBarVisibilityCallback != null) {
            actionBarVisibilityCallback.onContentScrollStopped();
        }
    }

    @Override // android.view.View
    public void onWindowSystemUiVisibilityChanged(int i) {
        if (Build.VERSION.SDK_INT >= 16) {
            super.onWindowSystemUiVisibilityChanged(i);
        }
        pullChildren();
        int i2 = this.mLastSystemUiVisibility;
        this.mLastSystemUiVisibility = i;
        boolean z = true;
        boolean z2 = (i & 4) == 0;
        boolean z3 = (i & 256) != 0;
        ActionBarVisibilityCallback actionBarVisibilityCallback = this.mActionBarVisibilityCallback;
        if (actionBarVisibilityCallback != null) {
            if (z3) {
                z = false;
            }
            actionBarVisibilityCallback.enableContentAnimations(z);
            if (z2 || !z3) {
                this.mActionBarVisibilityCallback.showForSystem();
            } else {
                this.mActionBarVisibilityCallback.hideForSystem();
            }
        }
        if (((i2 ^ i) & 256) == 0 || this.mActionBarVisibilityCallback == null) {
            return;
        }
        ViewCompat.requestApplyInsets(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        this.mWindowVisibility = i;
        ActionBarVisibilityCallback actionBarVisibilityCallback = this.mActionBarVisibilityCallback;
        if (actionBarVisibilityCallback != null) {
            actionBarVisibilityCallback.onWindowVisibilityChanged(i);
        }
    }

    void pullChildren() {
        if (this.mContent == null) {
            this.mContent = (ContentFrameLayout) findViewById(R.id.action_bar_activity_content);
            this.mActionBarTop = (ActionBarContainer) findViewById(R.id.action_bar_container);
            this.mDecorToolbar = getDecorToolbar(findViewById(R.id.action_bar));
            this.mActionBarBottom = (ActionBarContainer) findViewById(R.id.split_action_bar);
        }
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void restoreToolbarHierarchyState(SparseArray<Parcelable> sparseArray) {
        pullChildren();
        this.mDecorToolbar.restoreHierarchyState(sparseArray);
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void saveToolbarHierarchyState(SparseArray<Parcelable> sparseArray) {
        pullChildren();
        this.mDecorToolbar.saveHierarchyState(sparseArray);
    }

    public void setActionBarHideOffset(int i) {
        haltActionBarHideOffsetAnimations();
        int height = this.mActionBarTop.getHeight();
        int max = Math.max(0, Math.min(i, height));
        ViewCompat.setTranslationY(this.mActionBarTop, -max);
        ActionBarContainer actionBarContainer = this.mActionBarBottom;
        if (actionBarContainer == null || actionBarContainer.getVisibility() == 8) {
            return;
        }
        ViewCompat.setTranslationY(this.mActionBarBottom, (int) (this.mActionBarBottom.getHeight() * (max / height)));
    }

    public void setActionBarVisibilityCallback(ActionBarVisibilityCallback actionBarVisibilityCallback) {
        this.mActionBarVisibilityCallback = actionBarVisibilityCallback;
        if (getWindowToken() != null) {
            this.mActionBarVisibilityCallback.onWindowVisibilityChanged(this.mWindowVisibility);
            if (this.mLastSystemUiVisibility != 0) {
                onWindowSystemUiVisibilityChanged(this.mLastSystemUiVisibility);
                ViewCompat.requestApplyInsets(this);
            }
        }
    }

    public void setHasNonEmbeddedTabs(boolean z) {
        this.mHasNonEmbeddedTabs = z;
    }

    public void setHideOnContentScrollEnabled(boolean z) {
        if (z != this.mHideOnContentScroll) {
            this.mHideOnContentScroll = z;
            if (z) {
                return;
            }
            if (VersionUtils.isAtLeastL()) {
                stopNestedScroll();
            }
            haltActionBarHideOffsetAnimations();
            setActionBarHideOffset(0);
        }
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void setIcon(int i) {
        pullChildren();
        this.mDecorToolbar.setIcon(i);
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void setIcon(Drawable drawable) {
        pullChildren();
        this.mDecorToolbar.setIcon(drawable);
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void setLogo(int i) {
        pullChildren();
        this.mDecorToolbar.setLogo(i);
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void setMenu(Menu menu, MenuPresenter.Callback callback) {
        pullChildren();
        this.mDecorToolbar.setMenu(menu, callback);
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void setMenuPrepared() {
        pullChildren();
        this.mDecorToolbar.setMenuPrepared();
    }

    public void setOverlayMode(boolean z) {
        this.mOverlayMode = z;
        this.mIgnoreWindowContentOverlay = z && getContext().getApplicationInfo().targetSdkVersion < 19;
    }

    public void setShowingForActionMode(boolean z) {
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void setUiOptions(int i) {
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void setWindowCallback(WindowCallback windowCallback) {
        pullChildren();
        this.mDecorToolbar.setWindowCallback(windowCallback);
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public void setWindowTitle(CharSequence charSequence) {
        pullChildren();
        this.mDecorToolbar.setWindowTitle(charSequence);
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override // android.support.v7.internal.widget.DecorContentParent
    public boolean showOverflowMenu() {
        pullChildren();
        return this.mDecorToolbar.showOverflowMenu();
    }
}