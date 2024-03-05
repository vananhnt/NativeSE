package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.appcompat.R;
import android.support.v7.internal.view.ViewPropertyAnimatorCompatSet;
import android.support.v7.widget.ActionMenuPresenter;
import android.support.v7.widget.ActionMenuView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: AbsActionBarView.class */
public abstract class AbsActionBarView extends ViewGroup {
    private static final int FADE_DURATION = 200;
    private static final Interpolator sAlphaInterpolator = new DecelerateInterpolator();
    protected ActionMenuPresenter mActionMenuPresenter;
    protected int mContentHeight;
    protected ActionMenuView mMenuView;
    protected final Context mPopupContext;
    protected boolean mSplitActionBar;
    protected ViewGroup mSplitView;
    protected boolean mSplitWhenNarrow;
    protected final VisibilityAnimListener mVisAnimListener;
    protected ViewPropertyAnimatorCompat mVisibilityAnim;

    /* loaded from: AbsActionBarView$VisibilityAnimListener.class */
    protected class VisibilityAnimListener implements ViewPropertyAnimatorListener {
        private boolean mCanceled = false;
        int mFinalVisibility;
        final AbsActionBarView this$0;

        protected VisibilityAnimListener(AbsActionBarView absActionBarView) {
            this.this$0 = absActionBarView;
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListener
        public void onAnimationCancel(View view) {
            this.mCanceled = true;
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListener
        public void onAnimationEnd(View view) {
            if (this.mCanceled) {
                return;
            }
            AbsActionBarView absActionBarView = this.this$0;
            absActionBarView.mVisibilityAnim = null;
            absActionBarView.setVisibility(this.mFinalVisibility);
            if (this.this$0.mSplitView == null || this.this$0.mMenuView == null) {
                return;
            }
            this.this$0.mMenuView.setVisibility(this.mFinalVisibility);
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListener
        public void onAnimationStart(View view) {
            this.this$0.setVisibility(0);
            this.mCanceled = false;
        }

        public VisibilityAnimListener withFinalVisibility(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, int i) {
            this.this$0.mVisibilityAnim = viewPropertyAnimatorCompat;
            this.mFinalVisibility = i;
            return this;
        }
    }

    AbsActionBarView(Context context) {
        this(context, null);
    }

    AbsActionBarView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbsActionBarView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mVisAnimListener = new VisibilityAnimListener(this);
        TypedValue typedValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(R.attr.actionBarPopupTheme, typedValue, true) || typedValue.resourceId == 0) {
            this.mPopupContext = context;
        } else {
            this.mPopupContext = new ContextThemeWrapper(context, typedValue.resourceId);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int next(int i, int i2, boolean z) {
        return z ? i - i2 : i + i2;
    }

    public void animateToVisibility(int i) {
        ActionMenuView actionMenuView;
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = this.mVisibilityAnim;
        if (viewPropertyAnimatorCompat != null) {
            viewPropertyAnimatorCompat.cancel();
        }
        if (i != 0) {
            ViewPropertyAnimatorCompat alpha = ViewCompat.animate(this).alpha(0.0f);
            alpha.setDuration(200L);
            alpha.setInterpolator(sAlphaInterpolator);
            if (this.mSplitView == null || this.mMenuView == null) {
                alpha.setListener(this.mVisAnimListener.withFinalVisibility(alpha, i));
                alpha.start();
                return;
            }
            ViewPropertyAnimatorCompatSet viewPropertyAnimatorCompatSet = new ViewPropertyAnimatorCompatSet();
            ViewPropertyAnimatorCompat alpha2 = ViewCompat.animate(this.mMenuView).alpha(0.0f);
            alpha2.setDuration(200L);
            viewPropertyAnimatorCompatSet.setListener(this.mVisAnimListener.withFinalVisibility(alpha, i));
            viewPropertyAnimatorCompatSet.play(alpha).play(alpha2);
            viewPropertyAnimatorCompatSet.start();
            return;
        }
        if (getVisibility() != 0) {
            ViewCompat.setAlpha(this, 0.0f);
            if (this.mSplitView != null && (actionMenuView = this.mMenuView) != null) {
                ViewCompat.setAlpha(actionMenuView, 0.0f);
            }
        }
        ViewPropertyAnimatorCompat alpha3 = ViewCompat.animate(this).alpha(1.0f);
        alpha3.setDuration(200L);
        alpha3.setInterpolator(sAlphaInterpolator);
        if (this.mSplitView == null || this.mMenuView == null) {
            alpha3.setListener(this.mVisAnimListener.withFinalVisibility(alpha3, i));
            alpha3.start();
            return;
        }
        ViewPropertyAnimatorCompatSet viewPropertyAnimatorCompatSet2 = new ViewPropertyAnimatorCompatSet();
        ViewPropertyAnimatorCompat alpha4 = ViewCompat.animate(this.mMenuView).alpha(1.0f);
        alpha4.setDuration(200L);
        viewPropertyAnimatorCompatSet2.setListener(this.mVisAnimListener.withFinalVisibility(alpha3, i));
        viewPropertyAnimatorCompatSet2.play(alpha3).play(alpha4);
        viewPropertyAnimatorCompatSet2.start();
    }

    public boolean canShowOverflowMenu() {
        return isOverflowReserved() && getVisibility() == 0;
    }

    public void dismissPopupMenus() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.dismissPopupMenus();
        }
    }

    public int getAnimatedVisibility() {
        return this.mVisibilityAnim != null ? this.mVisAnimListener.mFinalVisibility : getVisibility();
    }

    public int getContentHeight() {
        return this.mContentHeight;
    }

    public boolean hideOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter != null) {
            return actionMenuPresenter.hideOverflowMenu();
        }
        return false;
    }

    public boolean isOverflowMenuShowPending() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter != null) {
            return actionMenuPresenter.isOverflowMenuShowPending();
        }
        return false;
    }

    public boolean isOverflowMenuShowing() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter != null) {
            return actionMenuPresenter.isOverflowMenuShowing();
        }
        return false;
    }

    public boolean isOverflowReserved() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowReserved();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int measureChildView(View view, int i, int i2, int i3) {
        view.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), i2);
        return Math.max(0, (i - view.getMeasuredWidth()) - i3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= 8) {
            super.onConfigurationChanged(configuration);
        }
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        setContentHeight(obtainStyledAttributes.getLayoutDimension(R.styleable.ActionBar_height, 0));
        obtainStyledAttributes.recycle();
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.onConfigurationChanged(configuration);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int positionChild(View view, int i, int i2, int i3, boolean z) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i4 = ((i3 - measuredHeight) / 2) + i2;
        if (z) {
            view.layout(i - measuredWidth, i4, i, i4 + measuredHeight);
        } else {
            view.layout(i, i4, i + measuredWidth, i4 + measuredHeight);
        }
        return z ? -measuredWidth : measuredWidth;
    }

    public void postShowOverflowMenu() {
        post(new Runnable(this) { // from class: android.support.v7.internal.widget.AbsActionBarView.1
            final AbsActionBarView this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.showOverflowMenu();
            }
        });
    }

    public void setContentHeight(int i) {
        this.mContentHeight = i;
        requestLayout();
    }

    public void setSplitToolbar(boolean z) {
        this.mSplitActionBar = z;
    }

    public void setSplitView(ViewGroup viewGroup) {
        this.mSplitView = viewGroup;
    }

    public void setSplitWhenNarrow(boolean z) {
        this.mSplitWhenNarrow = z;
    }

    public boolean showOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mActionMenuPresenter;
        if (actionMenuPresenter != null) {
            return actionMenuPresenter.showOverflowMenu();
        }
        return false;
    }
}