package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ActionProvider;
import android.support.v7.appcompat.R;
import android.support.v7.internal.transition.ActionBarTransition;
import android.support.v7.internal.view.ActionBarPolicy;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.internal.view.menu.BaseMenuPresenter;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.internal.view.menu.MenuView;
import android.support.v7.internal.view.menu.SubMenuBuilder;
import android.support.v7.internal.widget.TintImageView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.ListPopupWindow;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

/* loaded from: ActionMenuPresenter.class */
public class ActionMenuPresenter extends BaseMenuPresenter implements ActionProvider.SubUiVisibilityListener {
    private static final String TAG = "ActionMenuPresenter";
    private final SparseBooleanArray mActionButtonGroups;
    private ActionButtonSubmenu mActionButtonPopup;
    private int mActionItemWidthLimit;
    private boolean mExpandedActionViewsExclusive;
    private int mMaxItems;
    private boolean mMaxItemsSet;
    private int mMinCellSize;
    int mOpenSubMenuId;
    private View mOverflowButton;
    private OverflowPopup mOverflowPopup;
    private ActionMenuPopupCallback mPopupCallback;
    final PopupPresenterCallback mPopupPresenterCallback;
    private OpenOverflowRunnable mPostedOpenRunnable;
    private boolean mReserveOverflow;
    private boolean mReserveOverflowSet;
    private View mScrapActionButtonView;
    private boolean mStrictWidthLimit;
    private int mWidthLimit;
    private boolean mWidthLimitSet;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActionMenuPresenter$ActionButtonSubmenu.class */
    public class ActionButtonSubmenu extends MenuPopupHelper {
        private SubMenuBuilder mSubMenu;
        final ActionMenuPresenter this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ActionButtonSubmenu(ActionMenuPresenter actionMenuPresenter, Context context, SubMenuBuilder subMenuBuilder) {
            super(context, subMenuBuilder, null, false, R.attr.actionOverflowMenuStyle);
            boolean z;
            this.this$0 = actionMenuPresenter;
            this.mSubMenu = subMenuBuilder;
            if (!((MenuItemImpl) subMenuBuilder.getItem()).isActionButton()) {
                setAnchorView(actionMenuPresenter.mOverflowButton == null ? (View) actionMenuPresenter.mMenuView : actionMenuPresenter.mOverflowButton);
            }
            setCallback(actionMenuPresenter.mPopupPresenterCallback);
            int size = subMenuBuilder.size();
            int i = 0;
            while (true) {
                z = false;
                if (i >= size) {
                    break;
                }
                MenuItem item = subMenuBuilder.getItem(i);
                if (item.isVisible() && item.getIcon() != null) {
                    z = true;
                    break;
                }
                i++;
            }
            setForceShowIcon(z);
        }

        @Override // android.support.v7.internal.view.menu.MenuPopupHelper, android.widget.PopupWindow.OnDismissListener
        public void onDismiss() {
            super.onDismiss();
            this.this$0.mActionButtonPopup = null;
            this.this$0.mOpenSubMenuId = 0;
        }
    }

    /* loaded from: ActionMenuPresenter$ActionMenuPopupCallback.class */
    private class ActionMenuPopupCallback extends ActionMenuItemView.PopupCallback {
        final ActionMenuPresenter this$0;

        private ActionMenuPopupCallback(ActionMenuPresenter actionMenuPresenter) {
            this.this$0 = actionMenuPresenter;
        }

        @Override // android.support.v7.internal.view.menu.ActionMenuItemView.PopupCallback
        public ListPopupWindow getPopup() {
            return this.this$0.mActionButtonPopup != null ? this.this$0.mActionButtonPopup.getPopup() : null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActionMenuPresenter$OpenOverflowRunnable.class */
    public class OpenOverflowRunnable implements Runnable {
        private OverflowPopup mPopup;
        final ActionMenuPresenter this$0;

        public OpenOverflowRunnable(ActionMenuPresenter actionMenuPresenter, OverflowPopup overflowPopup) {
            this.this$0 = actionMenuPresenter;
            this.mPopup = overflowPopup;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.mMenu.changeMenuMode();
            View view = (View) this.this$0.mMenuView;
            if (view != null && view.getWindowToken() != null && this.mPopup.tryShow()) {
                this.this$0.mOverflowPopup = this.mPopup;
            }
            this.this$0.mPostedOpenRunnable = null;
        }
    }

    /* loaded from: ActionMenuPresenter$OverflowMenuButton.class */
    private class OverflowMenuButton extends TintImageView implements ActionMenuView.ActionMenuChildView {
        private final float[] mTempPts;
        final ActionMenuPresenter this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public OverflowMenuButton(ActionMenuPresenter actionMenuPresenter, Context context) {
            super(context, null, R.attr.actionOverflowButtonStyle);
            this.this$0 = actionMenuPresenter;
            this.mTempPts = new float[2];
            setClickable(true);
            setFocusable(true);
            setVisibility(0);
            setEnabled(true);
            setOnTouchListener(new ListPopupWindow.ForwardingListener(this, this, actionMenuPresenter) { // from class: android.support.v7.widget.ActionMenuPresenter.OverflowMenuButton.1
                final OverflowMenuButton this$1;
                final ActionMenuPresenter val$this$0;

                {
                    this.this$1 = this;
                    this.val$this$0 = actionMenuPresenter;
                }

                @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                public ListPopupWindow getPopup() {
                    if (this.this$1.this$0.mOverflowPopup == null) {
                        return null;
                    }
                    return this.this$1.this$0.mOverflowPopup.getPopup();
                }

                @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                public boolean onForwardingStarted() {
                    this.this$1.this$0.showOverflowMenu();
                    return true;
                }

                @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                public boolean onForwardingStopped() {
                    if (this.this$1.this$0.mPostedOpenRunnable != null) {
                        return false;
                    }
                    this.this$1.this$0.hideOverflowMenu();
                    return true;
                }
            });
        }

        @Override // android.support.v7.widget.ActionMenuView.ActionMenuChildView
        public boolean needsDividerAfter() {
            return false;
        }

        @Override // android.support.v7.widget.ActionMenuView.ActionMenuChildView
        public boolean needsDividerBefore() {
            return false;
        }

        @Override // android.view.View
        public boolean performClick() {
            if (super.performClick()) {
                return true;
            }
            playSoundEffect(0);
            this.this$0.showOverflowMenu();
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.ImageView, android.view.View
        public boolean setFrame(int i, int i2, int i3, int i4) {
            boolean frame = super.setFrame(i, i2, i3, i4);
            Drawable drawable = getDrawable();
            Drawable background = getBackground();
            if (drawable != null && background != null) {
                float[] fArr = this.mTempPts;
                fArr[0] = drawable.getBounds().centerX();
                getImageMatrix().mapPoints(fArr);
                int width = ((int) fArr[0]) - (getWidth() / 2);
                DrawableCompat.setHotspotBounds(background, width, 0, getWidth() + width, getHeight());
            }
            return frame;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActionMenuPresenter$OverflowPopup.class */
    public class OverflowPopup extends MenuPopupHelper {
        final ActionMenuPresenter this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public OverflowPopup(ActionMenuPresenter actionMenuPresenter, Context context, MenuBuilder menuBuilder, View view, boolean z) {
            super(context, menuBuilder, view, z, R.attr.actionOverflowMenuStyle);
            this.this$0 = actionMenuPresenter;
            setGravity(8388613);
            setCallback(actionMenuPresenter.mPopupPresenterCallback);
        }

        @Override // android.support.v7.internal.view.menu.MenuPopupHelper, android.widget.PopupWindow.OnDismissListener
        public void onDismiss() {
            super.onDismiss();
            this.this$0.mMenu.close();
            this.this$0.mOverflowPopup = null;
        }
    }

    /* loaded from: ActionMenuPresenter$PopupPresenterCallback.class */
    private class PopupPresenterCallback implements MenuPresenter.Callback {
        final ActionMenuPresenter this$0;

        private PopupPresenterCallback(ActionMenuPresenter actionMenuPresenter) {
            this.this$0 = actionMenuPresenter;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
            if (menuBuilder instanceof SubMenuBuilder) {
                ((SubMenuBuilder) menuBuilder).getRootMenu().close(false);
            }
            MenuPresenter.Callback callback = this.this$0.getCallback();
            if (callback != null) {
                callback.onCloseMenu(menuBuilder, z);
            }
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            boolean z = false;
            if (menuBuilder == null) {
                return false;
            }
            this.this$0.mOpenSubMenuId = ((SubMenuBuilder) menuBuilder).getItem().getItemId();
            MenuPresenter.Callback callback = this.this$0.getCallback();
            if (callback != null) {
                z = callback.onOpenSubMenu(menuBuilder);
            }
            return z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActionMenuPresenter$SavedState.class */
    public static class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.support.v7.widget.ActionMenuPresenter.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        public int openSubMenuId;

        SavedState() {
        }

        SavedState(Parcel parcel) {
            this.openSubMenuId = parcel.readInt();
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.openSubMenuId);
        }
    }

    public ActionMenuPresenter(Context context) {
        super(context, R.layout.abc_action_menu_layout, R.layout.abc_action_menu_item_layout);
        this.mActionButtonGroups = new SparseBooleanArray();
        this.mPopupPresenterCallback = new PopupPresenterCallback();
    }

    private View findViewForItem(MenuItem menuItem) {
        ViewGroup viewGroup = (ViewGroup) this.mMenuView;
        if (viewGroup == null) {
            return null;
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if ((childAt instanceof MenuView.ItemView) && ((MenuView.ItemView) childAt).getItemData() == menuItem) {
                return childAt;
            }
        }
        return null;
    }

    @Override // android.support.v7.internal.view.menu.BaseMenuPresenter
    public void bindItemView(MenuItemImpl menuItemImpl, MenuView.ItemView itemView) {
        itemView.initialize(menuItemImpl, 0);
        ActionMenuItemView actionMenuItemView = (ActionMenuItemView) itemView;
        actionMenuItemView.setItemInvoker((ActionMenuView) this.mMenuView);
        if (this.mPopupCallback == null) {
            this.mPopupCallback = new ActionMenuPopupCallback();
        }
        actionMenuItemView.setPopupCallback(this.mPopupCallback);
    }

    public boolean dismissPopupMenus() {
        return hideOverflowMenu() | hideSubMenus();
    }

    @Override // android.support.v7.internal.view.menu.BaseMenuPresenter
    public boolean filterLeftoverView(ViewGroup viewGroup, int i) {
        if (viewGroup.getChildAt(i) == this.mOverflowButton) {
            return false;
        }
        return super.filterLeftoverView(viewGroup, i);
    }

    @Override // android.support.v7.internal.view.menu.BaseMenuPresenter, android.support.v7.internal.view.menu.MenuPresenter
    public boolean flagActionItems() {
        int i;
        int i2;
        ArrayList<MenuItemImpl> visibleItems = this.mMenu.getVisibleItems();
        int size = visibleItems.size();
        int i3 = this.mMaxItems;
        int i4 = this.mActionItemWidthLimit;
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        ViewGroup viewGroup = (ViewGroup) this.mMenuView;
        int i5 = 0;
        int i6 = 0;
        boolean z = false;
        for (int i7 = 0; i7 < size; i7++) {
            MenuItemImpl menuItemImpl = visibleItems.get(i7);
            if (menuItemImpl.requiresActionButton()) {
                i5++;
            } else if (menuItemImpl.requestsActionButton()) {
                i6++;
            } else {
                z = true;
            }
            if (this.mExpandedActionViewsExclusive && menuItemImpl.isActionViewExpanded()) {
                i3 = 0;
            }
        }
        if (this.mReserveOverflow && (z || i5 + i6 > i3)) {
            i3--;
        }
        int i8 = i3 - i5;
        SparseBooleanArray sparseBooleanArray = this.mActionButtonGroups;
        sparseBooleanArray.clear();
        int i9 = 0;
        int i10 = 0;
        if (this.mStrictWidthLimit) {
            int i11 = this.mMinCellSize;
            i10 = i4 / i11;
            i9 = i11 + ((i4 % i11) / i10);
        }
        int i12 = 0;
        int i13 = i4;
        int i14 = i8;
        for (int i15 = 0; i15 < size; i15++) {
            MenuItemImpl menuItemImpl2 = visibleItems.get(i15);
            if (menuItemImpl2.requiresActionButton()) {
                View itemView = getItemView(menuItemImpl2, this.mScrapActionButtonView, viewGroup);
                if (this.mScrapActionButtonView == null) {
                    this.mScrapActionButtonView = itemView;
                }
                if (this.mStrictWidthLimit) {
                    i10 -= ActionMenuView.measureChildForCells(itemView, i9, i10, makeMeasureSpec, 0);
                } else {
                    itemView.measure(makeMeasureSpec, makeMeasureSpec);
                }
                int measuredWidth = itemView.getMeasuredWidth();
                i13 -= measuredWidth;
                if (i12 == 0) {
                    i12 = measuredWidth;
                }
                int groupId = menuItemImpl2.getGroupId();
                if (groupId != 0) {
                    sparseBooleanArray.put(groupId, true);
                }
                menuItemImpl2.setIsActionButton(true);
            } else if (menuItemImpl2.requestsActionButton()) {
                int groupId2 = menuItemImpl2.getGroupId();
                boolean z2 = sparseBooleanArray.get(groupId2);
                boolean z3 = (i14 > 0 || z2) && i13 > 0 && (!this.mStrictWidthLimit || i10 > 0);
                if (z3) {
                    View itemView2 = getItemView(menuItemImpl2, this.mScrapActionButtonView, viewGroup);
                    if (this.mScrapActionButtonView == null) {
                        this.mScrapActionButtonView = itemView2;
                    }
                    if (this.mStrictWidthLimit) {
                        int measureChildForCells = ActionMenuView.measureChildForCells(itemView2, i9, i10, makeMeasureSpec, 0);
                        i10 -= measureChildForCells;
                        if (measureChildForCells == 0) {
                            z3 = false;
                        }
                    } else {
                        itemView2.measure(makeMeasureSpec, makeMeasureSpec);
                    }
                    int measuredWidth2 = itemView2.getMeasuredWidth();
                    int i16 = i13 - measuredWidth2;
                    if (i12 == 0) {
                        i12 = measuredWidth2;
                    }
                    if (this.mStrictWidthLimit) {
                        z3 &= i16 >= 0;
                        i13 = i16;
                        i = i12;
                    } else {
                        z3 &= i16 + i12 > 0;
                        i13 = i16;
                        i = i12;
                    }
                } else {
                    i = i12;
                }
                if (z3 && groupId2 != 0) {
                    sparseBooleanArray.put(groupId2, true);
                    i2 = i14;
                } else if (z2) {
                    sparseBooleanArray.put(groupId2, false);
                    i2 = i14;
                    for (int i17 = 0; i17 < i15; i17++) {
                        MenuItemImpl menuItemImpl3 = visibleItems.get(i17);
                        if (menuItemImpl3.getGroupId() == groupId2) {
                            int i18 = i2;
                            if (menuItemImpl3.isActionButton()) {
                                i18 = i2 + 1;
                            }
                            menuItemImpl3.setIsActionButton(false);
                            i2 = i18;
                        }
                    }
                } else {
                    i2 = i14;
                }
                i14 = i2;
                if (z3) {
                    i14 = i2 - 1;
                }
                menuItemImpl2.setIsActionButton(z3);
                i12 = i;
            } else {
                menuItemImpl2.setIsActionButton(false);
            }
        }
        return true;
    }

    @Override // android.support.v7.internal.view.menu.BaseMenuPresenter
    public View getItemView(MenuItemImpl menuItemImpl, View view, ViewGroup viewGroup) {
        View actionView = menuItemImpl.getActionView();
        View itemView = (actionView == null || menuItemImpl.hasCollapsibleActionView()) ? super.getItemView(menuItemImpl, view, viewGroup) : actionView;
        itemView.setVisibility(menuItemImpl.isActionViewExpanded() ? 8 : 0);
        ActionMenuView actionMenuView = (ActionMenuView) viewGroup;
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        if (!actionMenuView.checkLayoutParams(layoutParams)) {
            itemView.setLayoutParams(actionMenuView.generateLayoutParams(layoutParams));
        }
        return itemView;
    }

    @Override // android.support.v7.internal.view.menu.BaseMenuPresenter, android.support.v7.internal.view.menu.MenuPresenter
    public MenuView getMenuView(ViewGroup viewGroup) {
        MenuView menuView = super.getMenuView(viewGroup);
        ((ActionMenuView) menuView).setPresenter(this);
        return menuView;
    }

    public boolean hideOverflowMenu() {
        if (this.mPostedOpenRunnable != null && this.mMenuView != null) {
            ((View) this.mMenuView).removeCallbacks(this.mPostedOpenRunnable);
            this.mPostedOpenRunnable = null;
            return true;
        }
        OverflowPopup overflowPopup = this.mOverflowPopup;
        if (overflowPopup != null) {
            overflowPopup.dismiss();
            return true;
        }
        return false;
    }

    public boolean hideSubMenus() {
        ActionButtonSubmenu actionButtonSubmenu = this.mActionButtonPopup;
        if (actionButtonSubmenu != null) {
            actionButtonSubmenu.dismiss();
            return true;
        }
        return false;
    }

    @Override // android.support.v7.internal.view.menu.BaseMenuPresenter, android.support.v7.internal.view.menu.MenuPresenter
    public void initForMenu(Context context, MenuBuilder menuBuilder) {
        super.initForMenu(context, menuBuilder);
        Resources resources = context.getResources();
        ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(context);
        if (!this.mReserveOverflowSet) {
            this.mReserveOverflow = actionBarPolicy.showsOverflowMenuButton();
        }
        if (!this.mWidthLimitSet) {
            this.mWidthLimit = actionBarPolicy.getEmbeddedMenuWidthLimit();
        }
        if (!this.mMaxItemsSet) {
            this.mMaxItems = actionBarPolicy.getMaxActionButtons();
        }
        int i = this.mWidthLimit;
        if (this.mReserveOverflow) {
            if (this.mOverflowButton == null) {
                this.mOverflowButton = new OverflowMenuButton(this, this.mSystemContext);
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
                this.mOverflowButton.measure(makeMeasureSpec, makeMeasureSpec);
            }
            i -= this.mOverflowButton.getMeasuredWidth();
        } else {
            this.mOverflowButton = null;
        }
        this.mActionItemWidthLimit = i;
        this.mMinCellSize = (int) (resources.getDisplayMetrics().density * 56.0f);
        this.mScrapActionButtonView = null;
    }

    public boolean isOverflowMenuShowPending() {
        return this.mPostedOpenRunnable != null || isOverflowMenuShowing();
    }

    public boolean isOverflowMenuShowing() {
        OverflowPopup overflowPopup = this.mOverflowPopup;
        return overflowPopup != null && overflowPopup.isShowing();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    @Override // android.support.v7.internal.view.menu.BaseMenuPresenter, android.support.v7.internal.view.menu.MenuPresenter
    public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        dismissPopupMenus();
        super.onCloseMenu(menuBuilder, z);
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (!this.mMaxItemsSet) {
            this.mMaxItems = this.mContext.getResources().getInteger(R.integer.abc_max_action_buttons);
        }
        if (this.mMenu != null) {
            this.mMenu.onItemsChanged(true);
        }
    }

    @Override // android.support.v7.internal.view.menu.MenuPresenter
    public void onRestoreInstanceState(Parcelable parcelable) {
        MenuItem findItem;
        SavedState savedState = (SavedState) parcelable;
        if (savedState.openSubMenuId <= 0 || (findItem = this.mMenu.findItem(savedState.openSubMenuId)) == null) {
            return;
        }
        onSubMenuSelected((SubMenuBuilder) findItem.getSubMenu());
    }

    @Override // android.support.v7.internal.view.menu.MenuPresenter
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        savedState.openSubMenuId = this.mOpenSubMenuId;
        return savedState;
    }

    @Override // android.support.v7.internal.view.menu.BaseMenuPresenter, android.support.v7.internal.view.menu.MenuPresenter
    public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
        SubMenuBuilder subMenuBuilder2;
        if (subMenuBuilder.hasVisibleItems()) {
            SubMenuBuilder subMenuBuilder3 = subMenuBuilder;
            while (true) {
                subMenuBuilder2 = subMenuBuilder3;
                if (subMenuBuilder2.getParentMenu() == this.mMenu) {
                    break;
                }
                subMenuBuilder3 = (SubMenuBuilder) subMenuBuilder2.getParentMenu();
            }
            View findViewForItem = findViewForItem(subMenuBuilder2.getItem());
            if (findViewForItem == null) {
                if (this.mOverflowButton == null) {
                    return false;
                }
                findViewForItem = this.mOverflowButton;
            }
            this.mOpenSubMenuId = subMenuBuilder.getItem().getItemId();
            this.mActionButtonPopup = new ActionButtonSubmenu(this, this.mContext, subMenuBuilder);
            this.mActionButtonPopup.setAnchorView(findViewForItem);
            this.mActionButtonPopup.show();
            super.onSubMenuSelected(subMenuBuilder);
            return true;
        }
        return false;
    }

    @Override // android.support.v4.view.ActionProvider.SubUiVisibilityListener
    public void onSubUiVisibilityChanged(boolean z) {
        if (z) {
            super.onSubMenuSelected(null);
        } else {
            this.mMenu.close(false);
        }
    }

    public void setExpandedActionViewsExclusive(boolean z) {
        this.mExpandedActionViewsExclusive = z;
    }

    public void setItemLimit(int i) {
        this.mMaxItems = i;
        this.mMaxItemsSet = true;
    }

    public void setMenuView(ActionMenuView actionMenuView) {
        this.mMenuView = actionMenuView;
        actionMenuView.initialize(this.mMenu);
    }

    public void setReserveOverflow(boolean z) {
        this.mReserveOverflow = z;
        this.mReserveOverflowSet = true;
    }

    public void setWidthLimit(int i, boolean z) {
        this.mWidthLimit = i;
        this.mStrictWidthLimit = z;
        this.mWidthLimitSet = true;
    }

    @Override // android.support.v7.internal.view.menu.BaseMenuPresenter
    public boolean shouldIncludeItem(int i, MenuItemImpl menuItemImpl) {
        return menuItemImpl.isActionButton();
    }

    public boolean showOverflowMenu() {
        if (!this.mReserveOverflow || isOverflowMenuShowing() || this.mMenu == null || this.mMenuView == null || this.mPostedOpenRunnable != null || this.mMenu.getNonActionItems().isEmpty()) {
            return false;
        }
        this.mPostedOpenRunnable = new OpenOverflowRunnable(this, new OverflowPopup(this, this.mContext, this.mMenu, this.mOverflowButton, true));
        ((View) this.mMenuView).post(this.mPostedOpenRunnable);
        super.onSubMenuSelected(null);
        return true;
    }

    @Override // android.support.v7.internal.view.menu.BaseMenuPresenter, android.support.v7.internal.view.menu.MenuPresenter
    public void updateMenuView(boolean z) {
        ViewGroup viewGroup = (ViewGroup) ((View) this.mMenuView).getParent();
        if (viewGroup != null) {
            ActionBarTransition.beginDelayedTransition(viewGroup);
        }
        super.updateMenuView(z);
        ((View) this.mMenuView).requestLayout();
        if (this.mMenu != null) {
            ArrayList<MenuItemImpl> actionItems = this.mMenu.getActionItems();
            int size = actionItems.size();
            for (int i = 0; i < size; i++) {
                ActionProvider supportActionProvider = actionItems.get(i).getSupportActionProvider();
                if (supportActionProvider != null) {
                    supportActionProvider.setSubUiVisibilityListener(this);
                }
            }
        }
        ArrayList<MenuItemImpl> nonActionItems = this.mMenu != null ? this.mMenu.getNonActionItems() : null;
        boolean z2 = false;
        if (this.mReserveOverflow && nonActionItems != null) {
            int size2 = nonActionItems.size();
            z2 = false;
            if (size2 == 1) {
                z2 = !nonActionItems.get(0).isActionViewExpanded();
            } else if (size2 > 0) {
                z2 = true;
            }
        }
        if (z2) {
            if (this.mOverflowButton == null) {
                this.mOverflowButton = new OverflowMenuButton(this, this.mSystemContext);
            }
            ViewGroup viewGroup2 = (ViewGroup) this.mOverflowButton.getParent();
            if (viewGroup2 != this.mMenuView) {
                if (viewGroup2 != null) {
                    viewGroup2.removeView(this.mOverflowButton);
                }
                ActionMenuView actionMenuView = (ActionMenuView) this.mMenuView;
                actionMenuView.addView(this.mOverflowButton, actionMenuView.generateOverflowButtonLayoutParams());
            }
        } else {
            View view = this.mOverflowButton;
            if (view != null && view.getParent() == this.mMenuView) {
                ((ViewGroup) this.mMenuView).removeView(this.mOverflowButton);
            }
        }
        ((ActionMenuView) this.mMenuView).setOverflowReserved(this.mReserveOverflow);
    }
}