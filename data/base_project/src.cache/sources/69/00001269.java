package android.support.v7.internal.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.appcompat.R;
import android.support.v7.view.ActionMode;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/* loaded from: ActionBarContainer.class */
public class ActionBarContainer extends FrameLayout {
    private View mActionBarView;
    Drawable mBackground;
    private View mContextView;
    private int mHeight;
    boolean mIsSplit;
    boolean mIsStacked;
    private boolean mIsTransitioning;
    Drawable mSplitBackground;
    Drawable mStackedBackground;
    private View mTabContainer;

    public ActionBarContainer(Context context) {
        this(context, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0088, code lost:
        if (r5.mSplitBackground == null) goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x008b, code lost:
        r9 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x00a4, code lost:
        if (r5.mStackedBackground == null) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public ActionBarContainer(android.content.Context r6, android.util.AttributeSet r7) {
        /*
            r5 = this;
            r0 = r5
            r1 = r6
            r2 = r7
            r0.<init>(r1, r2)
            boolean r0 = android.support.v7.internal.VersionUtils.isAtLeastL()
            if (r0 == 0) goto L18
            android.support.v7.internal.widget.ActionBarBackgroundDrawableV21 r0 = new android.support.v7.internal.widget.ActionBarBackgroundDrawableV21
            r1 = r0
            r2 = r5
            r1.<init>(r2)
            r8 = r0
            goto L21
        L18:
            android.support.v7.internal.widget.ActionBarBackgroundDrawable r0 = new android.support.v7.internal.widget.ActionBarBackgroundDrawable
            r1 = r0
            r2 = r5
            r1.<init>(r2)
            r8 = r0
        L21:
            r0 = r5
            r1 = r8
            r0.setBackgroundDrawable(r1)
            r0 = r6
            r1 = r7
            int[] r2 = android.support.v7.appcompat.R.styleable.ActionBar
            android.content.res.TypedArray r0 = r0.obtainStyledAttributes(r1, r2)
            r6 = r0
            r0 = r5
            r1 = r6
            int r2 = android.support.v7.appcompat.R.styleable.ActionBar_background
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
            r0.mBackground = r1
            r0 = r5
            r1 = r6
            int r2 = android.support.v7.appcompat.R.styleable.ActionBar_backgroundStacked
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
            r0.mStackedBackground = r1
            r0 = r5
            r1 = r6
            int r2 = android.support.v7.appcompat.R.styleable.ActionBar_height
            r3 = -1
            int r1 = r1.getDimensionPixelSize(r2, r3)
            r0.mHeight = r1
            r0 = r5
            int r0 = r0.getId()
            int r1 = android.support.v7.appcompat.R.id.split_action_bar
            if (r0 != r1) goto L6e
            r0 = r5
            r1 = 1
            r0.mIsSplit = r1
            r0 = r5
            r1 = r6
            int r2 = android.support.v7.appcompat.R.styleable.ActionBar_backgroundSplit
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
            r0.mSplitBackground = r1
            goto L6e
        L6e:
            r0 = r6
            r0.recycle()
            r0 = r5
            boolean r0 = r0.mIsSplit
            r9 = r0
            r0 = 0
            r10 = r0
            r0 = r9
            if (r0 == 0) goto L91
            r0 = r10
            r9 = r0
            r0 = r5
            android.graphics.drawable.Drawable r0 = r0.mSplitBackground
            if (r0 != 0) goto Laa
        L8b:
            r0 = 1
            r9 = r0
            goto Laa
        L91:
            r0 = r10
            r9 = r0
            r0 = r5
            android.graphics.drawable.Drawable r0 = r0.mBackground
            if (r0 != 0) goto Laa
            r0 = r10
            r9 = r0
            r0 = r5
            android.graphics.drawable.Drawable r0 = r0.mStackedBackground
            if (r0 != 0) goto Laa
            goto L8b
        Laa:
            r0 = r5
            r1 = r9
            r0.setWillNotDraw(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.internal.widget.ActionBarContainer.<init>(android.content.Context, android.util.AttributeSet):void");
    }

    private int getMeasuredHeightWithMargins(View view) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        return view.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
    }

    private boolean isCollapsed(View view) {
        return view == null || view.getVisibility() == 8 || view.getMeasuredHeight() == 0;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mBackground;
        if (drawable != null && drawable.isStateful()) {
            this.mBackground.setState(getDrawableState());
        }
        Drawable drawable2 = this.mStackedBackground;
        if (drawable2 != null && drawable2.isStateful()) {
            this.mStackedBackground.setState(getDrawableState());
        }
        Drawable drawable3 = this.mSplitBackground;
        if (drawable3 == null || !drawable3.isStateful()) {
            return;
        }
        this.mSplitBackground.setState(getDrawableState());
    }

    public View getTabContainer() {
        return this.mTabContainer;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        if (Build.VERSION.SDK_INT >= 11) {
            super.jumpDrawablesToCurrentState();
            Drawable drawable = this.mBackground;
            if (drawable != null) {
                drawable.jumpToCurrentState();
            }
            Drawable drawable2 = this.mStackedBackground;
            if (drawable2 != null) {
                drawable2.jumpToCurrentState();
            }
            Drawable drawable3 = this.mSplitBackground;
            if (drawable3 != null) {
                drawable3.jumpToCurrentState();
            }
        }
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mActionBarView = findViewById(R.id.action_bar);
        this.mContextView = findViewById(R.id.action_context_bar);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mIsTransitioning || super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        Drawable drawable;
        super.onLayout(z, i, i2, i3, i4);
        View view = this.mTabContainer;
        boolean z2 = (view == null || view.getVisibility() == 8) ? false : true;
        if (view != null && view.getVisibility() != 8) {
            int measuredHeight = getMeasuredHeight();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            view.layout(i, (measuredHeight - view.getMeasuredHeight()) - layoutParams.bottomMargin, i3, measuredHeight - layoutParams.bottomMargin);
        }
        boolean z3 = false;
        if (this.mIsSplit) {
            Drawable drawable2 = this.mSplitBackground;
            if (drawable2 != null) {
                drawable2.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                z3 = true;
            } else {
                z3 = false;
            }
        } else {
            if (this.mBackground != null) {
                if (this.mActionBarView.getVisibility() == 0) {
                    this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
                } else {
                    View view2 = this.mContextView;
                    if (view2 == null || view2.getVisibility() != 0) {
                        this.mBackground.setBounds(0, 0, 0, 0);
                    } else {
                        this.mBackground.setBounds(this.mContextView.getLeft(), this.mContextView.getTop(), this.mContextView.getRight(), this.mContextView.getBottom());
                    }
                }
                z3 = true;
            }
            this.mIsStacked = z2;
            if (z2 && (drawable = this.mStackedBackground) != null) {
                drawable.setBounds(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                z3 = true;
            }
        }
        if (z3) {
            invalidate();
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int i3;
        if (this.mActionBarView == null && View.MeasureSpec.getMode(i2) == Integer.MIN_VALUE && (i3 = this.mHeight) >= 0) {
            i2 = View.MeasureSpec.makeMeasureSpec(Math.min(i3, View.MeasureSpec.getSize(i2)), Integer.MIN_VALUE);
        }
        super.onMeasure(i, i2);
        if (this.mActionBarView == null) {
            return;
        }
        int mode = View.MeasureSpec.getMode(i2);
        View view = this.mTabContainer;
        if (view == null || view.getVisibility() == 8 || mode == 1073741824) {
            return;
        }
        setMeasuredDimension(getMeasuredWidth(), Math.min(getMeasuredHeightWithMargins(this.mTabContainer) + (!isCollapsed(this.mActionBarView) ? getMeasuredHeightWithMargins(this.mActionBarView) : !isCollapsed(this.mContextView) ? getMeasuredHeightWithMargins(this.mContextView) : 0), mode == Integer.MIN_VALUE ? View.MeasureSpec.getSize(i2) : Integer.MAX_VALUE));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return true;
    }

    public void setPrimaryBackground(Drawable drawable) {
        Drawable drawable2 = this.mBackground;
        if (drawable2 != null) {
            drawable2.setCallback(null);
            unscheduleDrawable(this.mBackground);
        }
        this.mBackground = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            View view = this.mActionBarView;
            if (view != null) {
                this.mBackground.setBounds(view.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
            }
        }
        boolean z = true;
        if (!this.mIsSplit ? this.mBackground != null || this.mStackedBackground != null : this.mSplitBackground != null) {
            z = false;
        }
        setWillNotDraw(z);
        invalidate();
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x005b, code lost:
        if (r6.mSplitBackground == null) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x005e, code lost:
        r10 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0075, code lost:
        if (r6.mStackedBackground == null) goto L15;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setSplitBackground(android.graphics.drawable.Drawable r7) {
        /*
            r6 = this;
            r0 = r6
            android.graphics.drawable.Drawable r0 = r0.mSplitBackground
            r8 = r0
            r0 = r8
            if (r0 == 0) goto L19
            r0 = r8
            r1 = 0
            r0.setCallback(r1)
            r0 = r6
            r1 = r6
            android.graphics.drawable.Drawable r1 = r1.mSplitBackground
            r0.unscheduleDrawable(r1)
            goto L19
        L19:
            r0 = r6
            r1 = r7
            r0.mSplitBackground = r1
            r0 = 0
            r9 = r0
            r0 = r7
            if (r0 == 0) goto L4d
            r0 = r7
            r1 = r6
            r0.setCallback(r1)
            r0 = r6
            boolean r0 = r0.mIsSplit
            if (r0 == 0) goto L4a
            r0 = r6
            android.graphics.drawable.Drawable r0 = r0.mSplitBackground
            r7 = r0
            r0 = r7
            if (r0 == 0) goto L4a
            r0 = r7
            r1 = 0
            r2 = 0
            r3 = r6
            int r3 = r3.getMeasuredWidth()
            r4 = r6
            int r4 = r4.getMeasuredHeight()
            r0.setBounds(r1, r2, r3, r4)
            goto L4d
        L4a:
            goto L4d
        L4d:
            r0 = r6
            boolean r0 = r0.mIsSplit
            if (r0 == 0) goto L64
            r0 = r9
            r10 = r0
            r0 = r6
            android.graphics.drawable.Drawable r0 = r0.mSplitBackground
            if (r0 != 0) goto L7b
        L5e:
            r0 = 1
            r10 = r0
            goto L7b
        L64:
            r0 = r9
            r10 = r0
            r0 = r6
            android.graphics.drawable.Drawable r0 = r0.mBackground
            if (r0 != 0) goto L7b
            r0 = r9
            r10 = r0
            r0 = r6
            android.graphics.drawable.Drawable r0 = r0.mStackedBackground
            if (r0 != 0) goto L7b
            goto L5e
        L7b:
            r0 = r6
            r1 = r10
            r0.setWillNotDraw(r1)
            r0 = r6
            r0.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.internal.widget.ActionBarContainer.setSplitBackground(android.graphics.drawable.Drawable):void");
    }

    public void setStackedBackground(Drawable drawable) {
        Drawable drawable2;
        Drawable drawable3 = this.mStackedBackground;
        if (drawable3 != null) {
            drawable3.setCallback(null);
            unscheduleDrawable(this.mStackedBackground);
        }
        this.mStackedBackground = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            if (this.mIsStacked && (drawable2 = this.mStackedBackground) != null) {
                drawable2.setBounds(this.mTabContainer.getLeft(), this.mTabContainer.getTop(), this.mTabContainer.getRight(), this.mTabContainer.getBottom());
            }
        }
        boolean z = true;
        if (!this.mIsSplit ? this.mBackground != null || this.mStackedBackground != null : this.mSplitBackground != null) {
            z = false;
        }
        setWillNotDraw(z);
        invalidate();
    }

    public void setTabContainer(ScrollingTabContainerView scrollingTabContainerView) {
        View view = this.mTabContainer;
        if (view != null) {
            removeView(view);
        }
        this.mTabContainer = scrollingTabContainerView;
        if (scrollingTabContainerView != null) {
            addView(scrollingTabContainerView);
            ViewGroup.LayoutParams layoutParams = scrollingTabContainerView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -2;
            scrollingTabContainerView.setAllowCollapse(false);
        }
    }

    public void setTransitioning(boolean z) {
        this.mIsTransitioning = z;
        setDescendantFocusability(z ? 393216 : 262144);
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        boolean z = i == 0;
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setVisible(z, false);
        }
        Drawable drawable2 = this.mStackedBackground;
        if (drawable2 != null) {
            drawable2.setVisible(z, false);
        }
        Drawable drawable3 = this.mSplitBackground;
        if (drawable3 != null) {
            drawable3.setVisible(z, false);
        }
    }

    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        return null;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public android.view.ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        return null;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return (drawable == this.mBackground && !this.mIsSplit) || (drawable == this.mStackedBackground && this.mIsStacked) || ((drawable == this.mSplitBackground && this.mIsSplit) || super.verifyDrawable(drawable));
    }
}