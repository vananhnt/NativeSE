package android.support.v7.internal.view.menu;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.internal.text.AllCapsTransformationMethod;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuView;
import android.support.v7.internal.widget.CompatTextView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/* loaded from: ActionMenuItemView.class */
public class ActionMenuItemView extends CompatTextView implements MenuView.ItemView, View.OnClickListener, View.OnLongClickListener, ActionMenuView.ActionMenuChildView {
    private static final int MAX_ICON_SIZE = 32;
    private static final String TAG = "ActionMenuItemView";
    private boolean mAllowTextWithIcon;
    private boolean mExpandedFormat;
    private ListPopupWindow.ForwardingListener mForwardingListener;
    private Drawable mIcon;
    private MenuItemImpl mItemData;
    private MenuBuilder.ItemInvoker mItemInvoker;
    private int mMaxIconSize;
    private int mMinWidth;
    private PopupCallback mPopupCallback;
    private int mSavedPaddingLeft;
    private CharSequence mTitle;

    /* loaded from: ActionMenuItemView$ActionMenuItemForwardingListener.class */
    private class ActionMenuItemForwardingListener extends ListPopupWindow.ForwardingListener {
        final ActionMenuItemView this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ActionMenuItemForwardingListener(ActionMenuItemView actionMenuItemView) {
            super(actionMenuItemView);
            this.this$0 = actionMenuItemView;
        }

        @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
        public ListPopupWindow getPopup() {
            if (this.this$0.mPopupCallback != null) {
                return this.this$0.mPopupCallback.getPopup();
            }
            return null;
        }

        @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
        protected boolean onForwardingStarted() {
            if (this.this$0.mItemInvoker == null || !this.this$0.mItemInvoker.invokeItem(this.this$0.mItemData)) {
                return false;
            }
            ListPopupWindow popup = getPopup();
            boolean z = false;
            if (popup != null) {
                z = false;
                if (popup.isShowing()) {
                    z = true;
                }
            }
            return z;
        }

        @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
        protected boolean onForwardingStopped() {
            ListPopupWindow popup = getPopup();
            if (popup != null) {
                popup.dismiss();
                return true;
            }
            return false;
        }
    }

    /* loaded from: ActionMenuItemView$PopupCallback.class */
    public static abstract class PopupCallback {
        public abstract ListPopupWindow getPopup();
    }

    public ActionMenuItemView(Context context) {
        this(context, null);
    }

    public ActionMenuItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ActionMenuItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Resources resources = context.getResources();
        this.mAllowTextWithIcon = resources.getBoolean(R.bool.abc_config_allowActionMenuItemTextWithIcon);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ActionMenuItemView, i, 0);
        this.mMinWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.ActionMenuItemView_android_minWidth, 0);
        obtainStyledAttributes.recycle();
        this.mMaxIconSize = (int) ((32.0f * resources.getDisplayMetrics().density) + 0.5f);
        setOnClickListener(this);
        setOnLongClickListener(this);
        setTransformationMethod(new AllCapsTransformationMethod(context));
        this.mSavedPaddingLeft = -1;
    }

    private void updateTextButtonVisibility() {
        boolean isEmpty = TextUtils.isEmpty(this.mTitle);
        boolean z = true;
        if (this.mIcon != null) {
            if (this.mItemData.showsTextAsAction()) {
                z = true;
                if (!this.mAllowTextWithIcon) {
                    if (this.mExpandedFormat) {
                        z = true;
                    }
                }
            }
            z = false;
        }
        setText((isEmpty ^ true) & z ? this.mTitle : null);
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    public boolean hasText() {
        return !TextUtils.isEmpty(getText());
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void initialize(MenuItemImpl menuItemImpl, int i) {
        this.mItemData = menuItemImpl;
        setIcon(menuItemImpl.getIcon());
        setTitle(menuItemImpl.getTitleForItemView(this));
        setId(menuItemImpl.getItemId());
        setVisibility(menuItemImpl.isVisible() ? 0 : 8);
        setEnabled(menuItemImpl.isEnabled());
        if (menuItemImpl.hasSubMenu() && this.mForwardingListener == null) {
            this.mForwardingListener = new ActionMenuItemForwardingListener(this);
        }
    }

    @Override // android.support.v7.widget.ActionMenuView.ActionMenuChildView
    public boolean needsDividerAfter() {
        return hasText();
    }

    @Override // android.support.v7.widget.ActionMenuView.ActionMenuChildView
    public boolean needsDividerBefore() {
        return hasText() && this.mItemData.getIcon() == null;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        MenuBuilder.ItemInvoker itemInvoker = this.mItemInvoker;
        if (itemInvoker != null) {
            itemInvoker.invokeItem(this.mItemData);
        }
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= 8) {
            super.onConfigurationChanged(configuration);
        }
        this.mAllowTextWithIcon = getContext().getResources().getBoolean(R.bool.abc_config_allowActionMenuItemTextWithIcon);
        updateTextButtonVisibility();
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        if (hasText()) {
            return false;
        }
        int[] iArr = new int[2];
        Rect rect = new Rect();
        getLocationOnScreen(iArr);
        getWindowVisibleDisplayFrame(rect);
        Context context = getContext();
        int width = getWidth();
        int height = getHeight();
        int i = iArr[1];
        int i2 = height / 2;
        int i3 = iArr[0] + (width / 2);
        if (ViewCompat.getLayoutDirection(view) == 0) {
            i3 = context.getResources().getDisplayMetrics().widthPixels - i3;
        }
        Toast makeText = Toast.makeText(context, this.mItemData.getTitle(), 0);
        if (i + i2 < rect.height()) {
            makeText.setGravity(8388661, i3, height);
        } else {
            makeText.setGravity(81, 0, height);
        }
        makeText.show();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int i, int i2) {
        int i3;
        boolean hasText = hasText();
        if (hasText && (i3 = this.mSavedPaddingLeft) >= 0) {
            super.setPadding(i3, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }
        super.onMeasure(i, i2);
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int measuredWidth = getMeasuredWidth();
        int min = mode == Integer.MIN_VALUE ? Math.min(size, this.mMinWidth) : this.mMinWidth;
        if (mode != 1073741824 && this.mMinWidth > 0 && measuredWidth < min) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(min, 1073741824), i2);
        }
        if (hasText || this.mIcon == null) {
            return;
        }
        super.setPadding((getMeasuredWidth() - this.mIcon.getBounds().width()) / 2, getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        ListPopupWindow.ForwardingListener forwardingListener;
        if (this.mItemData.hasSubMenu() && (forwardingListener = this.mForwardingListener) != null && forwardingListener.onTouch(this, motionEvent)) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public boolean prefersCondensedTitle() {
        return true;
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void setCheckable(boolean z) {
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void setChecked(boolean z) {
    }

    public void setExpandedFormat(boolean z) {
        if (this.mExpandedFormat != z) {
            this.mExpandedFormat = z;
            MenuItemImpl menuItemImpl = this.mItemData;
            if (menuItemImpl != null) {
                menuItemImpl.actionFormatChanged();
            }
        }
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int i = this.mMaxIconSize;
            if (intrinsicWidth > i) {
                float f = i / intrinsicWidth;
                intrinsicWidth = this.mMaxIconSize;
                intrinsicHeight = (int) (intrinsicHeight * f);
            }
            int i2 = this.mMaxIconSize;
            if (intrinsicHeight > i2) {
                float f2 = i2 / intrinsicHeight;
                intrinsicHeight = this.mMaxIconSize;
                intrinsicWidth = (int) (intrinsicWidth * f2);
            }
            drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        }
        setCompoundDrawables(drawable, null, null, null);
        updateTextButtonVisibility();
    }

    public void setItemInvoker(MenuBuilder.ItemInvoker itemInvoker) {
        this.mItemInvoker = itemInvoker;
    }

    @Override // android.widget.TextView, android.view.View
    public void setPadding(int i, int i2, int i3, int i4) {
        this.mSavedPaddingLeft = i;
        super.setPadding(i, i2, i3, i4);
    }

    public void setPopupCallback(PopupCallback popupCallback) {
        this.mPopupCallback = popupCallback;
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void setShortcut(boolean z, char c) {
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        setContentDescription(this.mTitle);
        updateTextButtonVisibility();
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public boolean showsIcon() {
        return true;
    }
}