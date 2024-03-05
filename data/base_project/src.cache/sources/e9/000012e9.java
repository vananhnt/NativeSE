package android.support.v7.widget;

import android.content.Context;
import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.internal.view.menu.SubMenuBuilder;
import android.support.v7.widget.ListPopupWindow;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/* loaded from: PopupMenu.class */
public class PopupMenu implements MenuBuilder.Callback, MenuPresenter.Callback {
    private View mAnchor;
    private Context mContext;
    private OnDismissListener mDismissListener;
    private View.OnTouchListener mDragListener;
    private MenuBuilder mMenu;
    private OnMenuItemClickListener mMenuItemClickListener;
    private MenuPopupHelper mPopup;

    /* loaded from: PopupMenu$OnDismissListener.class */
    public interface OnDismissListener {
        void onDismiss(PopupMenu popupMenu);
    }

    /* loaded from: PopupMenu$OnMenuItemClickListener.class */
    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public PopupMenu(Context context, View view) {
        this(context, view, 0);
    }

    public PopupMenu(Context context, View view, int i) {
        this.mContext = context;
        this.mMenu = new MenuBuilder(context);
        this.mMenu.setCallback(this);
        this.mAnchor = view;
        this.mPopup = new MenuPopupHelper(context, this.mMenu, view);
        this.mPopup.setGravity(i);
        this.mPopup.setCallback(this);
    }

    public void dismiss() {
        this.mPopup.dismiss();
    }

    public View.OnTouchListener getDragToOpenListener() {
        if (this.mDragListener == null) {
            this.mDragListener = new ListPopupWindow.ForwardingListener(this, this.mAnchor) { // from class: android.support.v7.widget.PopupMenu.1
                final PopupMenu this$0;

                {
                    this.this$0 = this;
                }

                @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                public ListPopupWindow getPopup() {
                    return this.this$0.mPopup.getPopup();
                }

                @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                protected boolean onForwardingStarted() {
                    this.this$0.show();
                    return true;
                }

                @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                protected boolean onForwardingStopped() {
                    this.this$0.dismiss();
                    return true;
                }
            };
        }
        return this.mDragListener;
    }

    public Menu getMenu() {
        return this.mMenu;
    }

    public MenuInflater getMenuInflater() {
        return new SupportMenuInflater(this.mContext);
    }

    public void inflate(int i) {
        getMenuInflater().inflate(i, this.mMenu);
    }

    @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
    public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        OnDismissListener onDismissListener = this.mDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
    }

    public void onCloseSubMenu(SubMenuBuilder subMenuBuilder) {
    }

    @Override // android.support.v7.internal.view.menu.MenuBuilder.Callback
    public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
        OnMenuItemClickListener onMenuItemClickListener = this.mMenuItemClickListener;
        if (onMenuItemClickListener != null) {
            return onMenuItemClickListener.onMenuItemClick(menuItem);
        }
        return false;
    }

    @Override // android.support.v7.internal.view.menu.MenuBuilder.Callback
    public void onMenuModeChange(MenuBuilder menuBuilder) {
    }

    @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
    public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
        if (menuBuilder == null) {
            return false;
        }
        if (menuBuilder.hasVisibleItems()) {
            new MenuPopupHelper(this.mContext, menuBuilder, this.mAnchor).show();
            return true;
        }
        return true;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mDismissListener = onDismissListener;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mMenuItemClickListener = onMenuItemClickListener;
    }

    public void show() {
        this.mPopup.show();
    }
}