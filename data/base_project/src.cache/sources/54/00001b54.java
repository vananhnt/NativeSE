package com.android.internal.view;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuPopupHelper;
import com.android.internal.view.menu.SubMenuBuilder;
import com.android.internal.widget.ActionBarContextView;
import java.lang.ref.WeakReference;

/* loaded from: StandaloneActionMode.class */
public class StandaloneActionMode extends ActionMode implements MenuBuilder.Callback {
    private Context mContext;
    private ActionBarContextView mContextView;
    private ActionMode.Callback mCallback;
    private WeakReference<View> mCustomView;
    private boolean mFinished;
    private boolean mFocusable;
    private MenuBuilder mMenu;

    public StandaloneActionMode(Context context, ActionBarContextView view, ActionMode.Callback callback, boolean isFocusable) {
        this.mContext = context;
        this.mContextView = view;
        this.mCallback = callback;
        this.mMenu = new MenuBuilder(context).setDefaultShowAsAction(1);
        this.mMenu.setCallback(this);
        this.mFocusable = isFocusable;
    }

    @Override // android.view.ActionMode
    public void setTitle(CharSequence title) {
        this.mContextView.setTitle(title);
    }

    @Override // android.view.ActionMode
    public void setSubtitle(CharSequence subtitle) {
        this.mContextView.setSubtitle(subtitle);
    }

    @Override // android.view.ActionMode
    public void setTitle(int resId) {
        setTitle(this.mContext.getString(resId));
    }

    @Override // android.view.ActionMode
    public void setSubtitle(int resId) {
        setSubtitle(this.mContext.getString(resId));
    }

    @Override // android.view.ActionMode
    public void setTitleOptionalHint(boolean titleOptional) {
        super.setTitleOptionalHint(titleOptional);
        this.mContextView.setTitleOptional(titleOptional);
    }

    @Override // android.view.ActionMode
    public boolean isTitleOptional() {
        return this.mContextView.isTitleOptional();
    }

    @Override // android.view.ActionMode
    public void setCustomView(View view) {
        this.mContextView.setCustomView(view);
        this.mCustomView = view != null ? new WeakReference<>(view) : null;
    }

    @Override // android.view.ActionMode
    public void invalidate() {
        this.mCallback.onPrepareActionMode(this, this.mMenu);
    }

    @Override // android.view.ActionMode
    public void finish() {
        if (this.mFinished) {
            return;
        }
        this.mFinished = true;
        this.mContextView.sendAccessibilityEvent(32);
        this.mCallback.onDestroyActionMode(this);
    }

    @Override // android.view.ActionMode
    public Menu getMenu() {
        return this.mMenu;
    }

    @Override // android.view.ActionMode
    public CharSequence getTitle() {
        return this.mContextView.getTitle();
    }

    @Override // android.view.ActionMode
    public CharSequence getSubtitle() {
        return this.mContextView.getSubtitle();
    }

    @Override // android.view.ActionMode
    public View getCustomView() {
        if (this.mCustomView != null) {
            return this.mCustomView.get();
        }
        return null;
    }

    @Override // android.view.ActionMode
    public MenuInflater getMenuInflater() {
        return new MenuInflater(this.mContext);
    }

    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        return this.mCallback.onActionItemClicked(this, item);
    }

    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
    }

    public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        if (!subMenu.hasVisibleItems()) {
            return true;
        }
        new MenuPopupHelper(this.mContext, subMenu).show();
        return true;
    }

    public void onCloseSubMenu(SubMenuBuilder menu) {
    }

    public void onMenuModeChange(MenuBuilder menu) {
        invalidate();
        this.mContextView.showOverflowMenu();
    }

    @Override // android.view.ActionMode
    public boolean isUiFocusable() {
        return this.mFocusable;
    }
}