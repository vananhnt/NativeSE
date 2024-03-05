package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.internal.view.menu.MenuView;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

/* loaded from: ActionMenuView.class */
public class ActionMenuView extends LinearLayoutCompat implements MenuBuilder.ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private Context mContext;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    private MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    /* loaded from: ActionMenuView$ActionMenuChildView.class */
    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    /* loaded from: ActionMenuView$ActionMenuPresenterCallback.class */
    private class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        final ActionMenuView this$0;

        private ActionMenuPresenterCallback(ActionMenuView actionMenuView) {
            this.this$0 = actionMenuView;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            return false;
        }
    }

    /* loaded from: ActionMenuView$LayoutParams.class */
    public static class LayoutParams extends LinearLayoutCompat.LayoutParams {
        @ViewDebug.ExportedProperty
        public int cellsUsed;
        @ViewDebug.ExportedProperty
        public boolean expandable;
        boolean expanded;
        @ViewDebug.ExportedProperty
        public int extraPixels;
        @ViewDebug.ExportedProperty
        public boolean isOverflowButton;
        @ViewDebug.ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.isOverflowButton = false;
        }

        LayoutParams(int i, int i2, boolean z) {
            super(i, i2);
            this.isOverflowButton = z;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.LayoutParams) layoutParams);
            this.isOverflowButton = layoutParams.isOverflowButton;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }

    /* loaded from: ActionMenuView$MenuBuilderCallback.class */
    private class MenuBuilderCallback implements MenuBuilder.Callback {
        final ActionMenuView this$0;

        private MenuBuilderCallback(ActionMenuView actionMenuView) {
            this.this$0 = actionMenuView;
        }

        @Override // android.support.v7.internal.view.menu.MenuBuilder.Callback
        public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
            return this.this$0.mOnMenuItemClickListener != null && this.this$0.mOnMenuItemClickListener.onMenuItemClick(menuItem);
        }

        @Override // android.support.v7.internal.view.menu.MenuBuilder.Callback
        public void onMenuModeChange(MenuBuilder menuBuilder) {
            if (this.this$0.mMenuBuilderCallback != null) {
                this.this$0.mMenuBuilderCallback.onMenuModeChange(menuBuilder);
            }
        }
    }

    /* loaded from: ActionMenuView$OnMenuItemClickListener.class */
    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        setBaselineAligned(false);
        float f = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * f);
        this.mGeneratedItemPadding = (int) (4.0f * f);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int measureChildForCells(View view, int i, int i2, int i3, int i4) {
        int i5;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3) - i4, View.MeasureSpec.getMode(i3));
        ActionMenuItemView actionMenuItemView = view instanceof ActionMenuItemView ? (ActionMenuItemView) view : null;
        boolean z = actionMenuItemView != null && actionMenuItemView.hasText();
        if (i2 <= 0 || (z && i2 < 2)) {
            i5 = 0;
        } else {
            view.measure(View.MeasureSpec.makeMeasureSpec(i * i2, Integer.MIN_VALUE), makeMeasureSpec);
            int measuredWidth = view.getMeasuredWidth();
            int i6 = measuredWidth / i;
            i5 = i6;
            if (measuredWidth % i != 0) {
                i5 = i6 + 1;
            }
            if (z && i5 < 2) {
                i5 = 2;
            }
        }
        boolean z2 = false;
        if (!layoutParams.isOverflowButton) {
            z2 = false;
            if (z) {
                z2 = true;
            }
        }
        layoutParams.expandable = z2;
        layoutParams.cellsUsed = i5;
        view.measure(View.MeasureSpec.makeMeasureSpec(i5 * i, 1073741824), makeMeasureSpec);
        return i5;
    }

    private void onMeasureExactFormat(int i, int i2) {
        int i3;
        int mode = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int childMeasureSpec = getChildMeasureSpec(i2, paddingTop, -2);
        int i4 = size - paddingLeft;
        int i5 = this.mMinCellSize;
        int i6 = i4 / i5;
        int i7 = i4 % i5;
        if (i6 == 0) {
            setMeasuredDimension(i4, 0);
            return;
        }
        int i8 = i5 + (i7 / i6);
        boolean z = false;
        int childCount = getChildCount();
        int i9 = 0;
        int i10 = 0;
        int i11 = 0;
        int i12 = 0;
        int i13 = i6;
        int i14 = 0;
        long j = 0;
        while (i14 < childCount) {
            View childAt = getChildAt(i14);
            if (childAt.getVisibility() == 8) {
                i3 = i11;
            } else {
                boolean z2 = childAt instanceof ActionMenuItemView;
                i10++;
                if (z2) {
                    int i15 = this.mGeneratedItemPadding;
                    childAt.setPadding(i15, 0, i15, 0);
                }
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                layoutParams.expanded = false;
                layoutParams.extraPixels = 0;
                layoutParams.cellsUsed = 0;
                layoutParams.expandable = false;
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = 0;
                layoutParams.preventEdgeOffset = z2 && ((ActionMenuItemView) childAt).hasText();
                int measureChildForCells = measureChildForCells(childAt, i8, layoutParams.isOverflowButton ? 1 : i13, childMeasureSpec, paddingTop);
                i12 = Math.max(i12, measureChildForCells);
                i3 = i11;
                if (layoutParams.expandable) {
                    i3 = i11 + 1;
                }
                if (layoutParams.isOverflowButton) {
                    z = true;
                }
                i13 -= measureChildForCells;
                i9 = Math.max(i9, childAt.getMeasuredHeight());
                if (measureChildForCells == 1) {
                    j |= 1 << i14;
                }
            }
            i14++;
            i11 = i3;
        }
        boolean z3 = z && i10 == 2;
        int i16 = i13;
        int i17 = childCount;
        boolean z4 = false;
        while (true) {
            if (i11 <= 0 || i16 <= 0) {
                break;
            }
            long j2 = 0;
            int i18 = Integer.MAX_VALUE;
            int i19 = 0;
            for (int i20 = 0; i20 < i17; i20++) {
                LayoutParams layoutParams2 = (LayoutParams) getChildAt(i20).getLayoutParams();
                if (layoutParams2.expandable) {
                    if (layoutParams2.cellsUsed < i18) {
                        i18 = layoutParams2.cellsUsed;
                        j2 = 1 << i20;
                        i19 = 1;
                    } else if (layoutParams2.cellsUsed == i18) {
                        i19++;
                        j2 |= 1 << i20;
                    }
                }
            }
            boolean z5 = z4;
            int i21 = i17;
            j |= j2;
            if (i19 > i16) {
                i17 = i21;
                z4 = z5;
                break;
            }
            for (int i22 = 0; i22 < i21; i22++) {
                View childAt2 = getChildAt(i22);
                LayoutParams layoutParams3 = (LayoutParams) childAt2.getLayoutParams();
                if ((j2 & (1 << i22)) != 0) {
                    if (z3 && layoutParams3.preventEdgeOffset && i16 == 1) {
                        int i23 = this.mGeneratedItemPadding;
                        childAt2.setPadding(i23 + i8, 0, i23, 0);
                    }
                    layoutParams3.cellsUsed++;
                    layoutParams3.expanded = true;
                    i16--;
                } else if (layoutParams3.cellsUsed == i18 + 1) {
                    j |= 1 << i22;
                }
            }
            i17 = i21;
            z4 = true;
        }
        boolean z6 = !z && i10 == 1;
        if (i16 > 0 && j != 0 && (i16 < i10 - 1 || z6 || i12 > 1)) {
            float bitCount = Long.bitCount(j);
            if (!z6) {
                if ((j & 1) != 0 && !((LayoutParams) getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                    bitCount -= 0.5f;
                }
                if ((j & (1 << (i17 - 1))) != 0 && !((LayoutParams) getChildAt(i17 - 1).getLayoutParams()).preventEdgeOffset) {
                    bitCount -= 0.5f;
                }
            }
            int i24 = bitCount > 0.0f ? (int) ((i16 * i8) / bitCount) : 0;
            for (int i25 = 0; i25 < i17; i25++) {
                if ((j & (1 << i25)) != 0) {
                    View childAt3 = getChildAt(i25);
                    LayoutParams layoutParams4 = (LayoutParams) childAt3.getLayoutParams();
                    if (childAt3 instanceof ActionMenuItemView) {
                        layoutParams4.extraPixels = i24;
                        layoutParams4.expanded = true;
                        if (i25 == 0 && !layoutParams4.preventEdgeOffset) {
                            layoutParams4.leftMargin = (-i24) / 2;
                        }
                        z4 = true;
                    } else if (layoutParams4.isOverflowButton) {
                        layoutParams4.extraPixels = i24;
                        layoutParams4.expanded = true;
                        layoutParams4.rightMargin = (-i24) / 2;
                        z4 = true;
                    } else {
                        if (i25 != 0) {
                            layoutParams4.leftMargin = i24 / 2;
                        }
                        if (i25 != i17 - 1) {
                            layoutParams4.rightMargin = i24 / 2;
                        }
                    }
                }
            }
        }
        if (z4) {
            for (int i26 = 0; i26 < i17; i26++) {
                View childAt4 = getChildAt(i26);
                LayoutParams layoutParams5 = (LayoutParams) childAt4.getLayoutParams();
                if (layoutParams5.expanded) {
                    childAt4.measure(View.MeasureSpec.makeMeasureSpec((layoutParams5.cellsUsed * i8) + layoutParams5.extraPixels, 1073741824), childMeasureSpec);
                }
            }
        }
        if (mode == 1073741824) {
            i9 = size2;
        }
        setMeasuredDimension(i4, i9);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams != null && (layoutParams instanceof LayoutParams);
    }

    public void dismissPopupMenus() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.dismissPopupMenus();
        }
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.gravity = 16;
        return layoutParams;
    }

    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams != null) {
            LayoutParams layoutParams2 = layoutParams instanceof LayoutParams ? new LayoutParams((LayoutParams) layoutParams) : new LayoutParams(layoutParams);
            if (layoutParams2.gravity <= 0) {
                layoutParams2.gravity = 16;
            }
            return layoutParams2;
        }
        return generateDefaultLayoutParams();
    }

    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
        generateDefaultLayoutParams.isOverflowButton = true;
        return generateDefaultLayoutParams;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            this.mMenu = new MenuBuilder(context);
            this.mMenu.setCallback(new MenuBuilderCallback());
            this.mPresenter = new ActionMenuPresenter(context);
            this.mPresenter.setReserveOverflow(true);
            ActionMenuPresenter actionMenuPresenter = this.mPresenter;
            ActionMenuPresenterCallback actionMenuPresenterCallback = this.mActionMenuPresenterCallback;
            if (actionMenuPresenterCallback == null) {
                actionMenuPresenterCallback = new ActionMenuPresenterCallback();
            }
            actionMenuPresenter.setCallback(actionMenuPresenterCallback);
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    @Override // android.support.v7.internal.view.menu.MenuView
    public int getWindowAnimations() {
        return 0;
    }

    protected boolean hasSupportDividerBeforeChildAt(int i) {
        if (i == 0) {
            return false;
        }
        View childAt = getChildAt(i - 1);
        View childAt2 = getChildAt(i);
        boolean z = false;
        if (i < getChildCount() && (childAt instanceof ActionMenuChildView)) {
            z = false | ((ActionMenuChildView) childAt).needsDividerAfter();
        }
        if (i > 0 && (childAt2 instanceof ActionMenuChildView)) {
            z |= ((ActionMenuChildView) childAt2).needsDividerBefore();
        }
        return z;
    }

    public boolean hideOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.hideOverflowMenu();
    }

    @Override // android.support.v7.internal.view.menu.MenuView
    public void initialize(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    @Override // android.support.v7.internal.view.menu.MenuBuilder.ItemInvoker
    public boolean invokeItem(MenuItemImpl menuItemImpl) {
        return this.mMenu.performItemAction(menuItemImpl, 0);
    }

    public boolean isOverflowMenuShowPending() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowPending();
    }

    public boolean isOverflowMenuShowing() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowing();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= 8) {
            super.onConfigurationChanged(configuration);
        }
        this.mPresenter.updateMenuView(false);
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter == null || !actionMenuPresenter.isOverflowMenuShowing()) {
            return;
        }
        this.mPresenter.hideOverflowMenu();
        this.mPresenter.showOverflowMenu();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int width;
        int i5;
        if (!this.mFormatItems) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        int childCount = getChildCount();
        int i6 = (i2 + i4) / 2;
        int dividerWidth = getDividerWidth();
        int i7 = 0;
        int i8 = 0;
        int paddingRight = ((i3 - i) - getPaddingRight()) - getPaddingLeft();
        boolean z2 = false;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        for (int i9 = 0; i9 < childCount; i9++) {
            View childAt = getChildAt(i9);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isOverflowButton) {
                    int measuredWidth = childAt.getMeasuredWidth();
                    if (hasSupportDividerBeforeChildAt(i9)) {
                        measuredWidth += dividerWidth;
                    }
                    int measuredHeight = childAt.getMeasuredHeight();
                    if (isLayoutRtl) {
                        i5 = getPaddingLeft() + layoutParams.leftMargin;
                        width = i5 + measuredWidth;
                    } else {
                        width = (getWidth() - getPaddingRight()) - layoutParams.rightMargin;
                        i5 = width - measuredWidth;
                    }
                    int i10 = i6 - (measuredHeight / 2);
                    childAt.layout(i5, i10, width, i10 + measuredHeight);
                    paddingRight -= measuredWidth;
                    z2 = true;
                } else {
                    int measuredWidth2 = childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                    i7 += measuredWidth2;
                    paddingRight -= measuredWidth2;
                    if (hasSupportDividerBeforeChildAt(i9)) {
                        i7 += dividerWidth;
                    }
                    i8++;
                }
            }
        }
        if (childCount == 1 && !z2) {
            View childAt2 = getChildAt(0);
            int measuredWidth3 = childAt2.getMeasuredWidth();
            int measuredHeight2 = childAt2.getMeasuredHeight();
            int i11 = ((i3 - i) / 2) - (measuredWidth3 / 2);
            int i12 = i6 - (measuredHeight2 / 2);
            childAt2.layout(i11, i12, i11 + measuredWidth3, i12 + measuredHeight2);
            return;
        }
        int i13 = i8 - (z2 ? 0 : 1);
        int max = Math.max(0, i13 > 0 ? paddingRight / i13 : 0);
        if (isLayoutRtl) {
            int width2 = getWidth() - getPaddingRight();
            for (int i14 = 0; i14 < childCount; i14++) {
                View childAt3 = getChildAt(i14);
                LayoutParams layoutParams2 = (LayoutParams) childAt3.getLayoutParams();
                if (childAt3.getVisibility() != 8 && !layoutParams2.isOverflowButton) {
                    int i15 = width2 - layoutParams2.rightMargin;
                    int measuredWidth4 = childAt3.getMeasuredWidth();
                    int measuredHeight3 = childAt3.getMeasuredHeight();
                    int i16 = i6 - (measuredHeight3 / 2);
                    childAt3.layout(i15 - measuredWidth4, i16, i15, i16 + measuredHeight3);
                    width2 = i15 - ((layoutParams2.leftMargin + measuredWidth4) + max);
                }
            }
            return;
        }
        int paddingLeft = getPaddingLeft();
        for (int i17 = 0; i17 < childCount; i17++) {
            View childAt4 = getChildAt(i17);
            LayoutParams layoutParams3 = (LayoutParams) childAt4.getLayoutParams();
            if (childAt4.getVisibility() != 8 && !layoutParams3.isOverflowButton) {
                int i18 = paddingLeft + layoutParams3.leftMargin;
                int measuredWidth5 = childAt4.getMeasuredWidth();
                int measuredHeight4 = childAt4.getMeasuredHeight();
                int i19 = i6 - (measuredHeight4 / 2);
                childAt4.layout(i18, i19, i18 + measuredWidth5, i19 + measuredHeight4);
                paddingLeft = i18 + layoutParams3.rightMargin + measuredWidth5 + max;
            }
        }
    }

    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.View
    protected void onMeasure(int i, int i2) {
        MenuBuilder menuBuilder;
        boolean z = this.mFormatItems;
        this.mFormatItems = View.MeasureSpec.getMode(i) == 1073741824;
        if (z != this.mFormatItems) {
            this.mFormatItemsWidth = 0;
        }
        int size = View.MeasureSpec.getSize(i);
        if (this.mFormatItems && (menuBuilder = this.mMenu) != null && size != this.mFormatItemsWidth) {
            this.mFormatItemsWidth = size;
            menuBuilder.onItemsChanged(true);
        }
        int childCount = getChildCount();
        if (this.mFormatItems && childCount > 0) {
            onMeasureExactFormat(i, i2);
            return;
        }
        for (int i3 = 0; i3 < childCount; i3++) {
            LayoutParams layoutParams = (LayoutParams) getChildAt(i3).getLayoutParams();
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
        }
        super.onMeasure(i, i2);
    }

    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    public void setExpandedActionViewsExclusive(boolean z) {
        this.mPresenter.setExpandedActionViewsExclusive(z);
    }

    public void setMenuCallbacks(MenuPresenter.Callback callback, MenuBuilder.Callback callback2) {
        this.mActionMenuPresenterCallback = callback;
        this.mMenuBuilderCallback = callback2;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOverflowReserved(boolean z) {
        this.mReserveOverflow = z;
    }

    public void setPopupTheme(int i) {
        if (this.mPopupTheme != i) {
            this.mPopupTheme = i;
            if (i == 0) {
                this.mPopupContext = this.mContext;
            } else {
                this.mPopupContext = new ContextThemeWrapper(this.mContext, i);
            }
        }
    }

    public void setPresenter(ActionMenuPresenter actionMenuPresenter) {
        this.mPresenter = actionMenuPresenter;
        this.mPresenter.setMenuView(this);
    }

    public boolean showOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.showOverflowMenu();
    }
}