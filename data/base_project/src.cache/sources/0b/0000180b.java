package android.widget;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListPopupWindow;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuPopupHelper;
import com.android.internal.view.menu.MenuPresenter;
import com.android.internal.view.menu.SubMenuBuilder;

/* loaded from: PopupMenu.class */
public class PopupMenu implements MenuBuilder.Callback, MenuPresenter.Callback {
    private Context mContext;
    private MenuBuilder mMenu;
    private View mAnchor;
    private MenuPopupHelper mPopup;
    private OnMenuItemClickListener mMenuItemClickListener;
    private OnDismissListener mDismissListener;
    private View.OnTouchListener mDragListener;

    /* loaded from: PopupMenu$OnDismissListener.class */
    public interface OnDismissListener {
        void onDismiss(PopupMenu popupMenu);
    }

    /* loaded from: PopupMenu$OnMenuItemClickListener.class */
    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public PopupMenu(Context context, View anchor) {
        this(context, anchor, 0);
    }

    public PopupMenu(Context context, View anchor, int gravity) {
        this.mContext = context;
        this.mMenu = new MenuBuilder(context);
        this.mMenu.setCallback(this);
        this.mAnchor = anchor;
        this.mPopup = new MenuPopupHelper(context, this.mMenu, anchor);
        this.mPopup.setGravity(gravity);
        this.mPopup.setCallback(this);
    }

    public View.OnTouchListener getDragToOpenListener() {
        if (this.mDragListener == null) {
            this.mDragListener = new ListPopupWindow.ForwardingListener(this.mAnchor) { // from class: android.widget.PopupMenu.1
                @Override // android.widget.ListPopupWindow.ForwardingListener
                protected boolean onForwardingStarted() {
                    PopupMenu.this.show();
                    return true;
                }

                @Override // android.widget.ListPopupWindow.ForwardingListener
                protected boolean onForwardingStopped() {
                    PopupMenu.this.dismiss();
                    return true;
                }

                @Override // android.widget.ListPopupWindow.ForwardingListener
                public ListPopupWindow getPopup() {
                    return PopupMenu.this.mPopup.getPopup();
                }
            };
        }
        return this.mDragListener;
    }

    public Menu getMenu() {
        return this.mMenu;
    }

    public MenuInflater getMenuInflater() {
        return new MenuInflater(this.mContext);
    }

    public void inflate(int menuRes) {
        getMenuInflater().inflate(menuRes, this.mMenu);
    }

    public void show() {
        this.mPopup.show();
    }

    public void dismiss() {
        this.mPopup.dismiss();
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mMenuItemClickListener = listener;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.mDismissListener = listener;
    }

    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        if (this.mMenuItemClickListener != null) {
            return this.mMenuItemClickListener.onMenuItemClick(item);
        }
        return false;
    }

    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        if (this.mDismissListener != null) {
            this.mDismissListener.onDismiss(this);
        }
    }

    public boolean onOpenSubMenu(MenuBuilder subMenu) {
        if (subMenu == null) {
            return false;
        }
        if (!subMenu.hasVisibleItems()) {
            return true;
        }
        new MenuPopupHelper(this.mContext, subMenu, this.mAnchor).show();
        return true;
    }

    public void onCloseSubMenu(SubMenuBuilder menu) {
    }

    public void onMenuModeChange(MenuBuilder menu) {
    }
}