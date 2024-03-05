package android.support.v7.internal.view.menu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.ActionProvider;
import android.view.CollapsibleActionView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import java.lang.reflect.Method;

/* loaded from: MenuItemWrapperICS.class */
public class MenuItemWrapperICS extends BaseMenuWrapper<MenuItem> implements SupportMenuItem {
    static final String LOG_TAG = "MenuItemWrapper";
    private final boolean mEmulateProviderVisibilityOverride;
    private boolean mLastRequestVisible;
    private Method mSetExclusiveCheckableMethod;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MenuItemWrapperICS$ActionProviderWrapper.class */
    public class ActionProviderWrapper extends ActionProvider {
        final android.support.v4.view.ActionProvider mInner;
        final MenuItemWrapperICS this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ActionProviderWrapper(MenuItemWrapperICS menuItemWrapperICS, android.support.v4.view.ActionProvider actionProvider) {
            super(actionProvider.getContext());
            this.this$0 = menuItemWrapperICS;
            this.mInner = actionProvider;
            if (menuItemWrapperICS.mEmulateProviderVisibilityOverride) {
                this.mInner.setVisibilityListener(new ActionProvider.VisibilityListener(this, menuItemWrapperICS) { // from class: android.support.v7.internal.view.menu.MenuItemWrapperICS.ActionProviderWrapper.1
                    final ActionProviderWrapper this$1;
                    final MenuItemWrapperICS val$this$0;

                    {
                        this.this$1 = this;
                        this.val$this$0 = menuItemWrapperICS;
                    }

                    @Override // android.support.v4.view.ActionProvider.VisibilityListener
                    public void onActionProviderVisibilityChanged(boolean z) {
                        if (this.this$1.mInner.overridesItemVisibility() && this.this$1.this$0.mLastRequestVisible) {
                            this.this$1.this$0.wrappedSetVisible(z);
                        }
                    }
                });
            }
        }

        @Override // android.view.ActionProvider
        public boolean hasSubMenu() {
            return this.mInner.hasSubMenu();
        }

        @Override // android.view.ActionProvider
        public View onCreateActionView() {
            if (this.this$0.mEmulateProviderVisibilityOverride) {
                this.this$0.checkActionProviderOverrideVisibility();
            }
            return this.mInner.onCreateActionView();
        }

        @Override // android.view.ActionProvider
        public boolean onPerformDefaultAction() {
            return this.mInner.onPerformDefaultAction();
        }

        @Override // android.view.ActionProvider
        public void onPrepareSubMenu(SubMenu subMenu) {
            this.mInner.onPrepareSubMenu(this.this$0.getSubMenuWrapper(subMenu));
        }
    }

    /* loaded from: MenuItemWrapperICS$CollapsibleActionViewWrapper.class */
    static class CollapsibleActionViewWrapper extends FrameLayout implements CollapsibleActionView {
        final android.support.v7.view.CollapsibleActionView mWrappedView;

        CollapsibleActionViewWrapper(View view) {
            super(view.getContext());
            this.mWrappedView = (android.support.v7.view.CollapsibleActionView) view;
            addView(view);
        }

        View getWrappedView() {
            return (View) this.mWrappedView;
        }

        @Override // android.view.CollapsibleActionView
        public void onActionViewCollapsed() {
            this.mWrappedView.onActionViewCollapsed();
        }

        @Override // android.view.CollapsibleActionView
        public void onActionViewExpanded() {
            this.mWrappedView.onActionViewExpanded();
        }
    }

    /* loaded from: MenuItemWrapperICS$OnActionExpandListenerWrapper.class */
    private class OnActionExpandListenerWrapper extends BaseWrapper<MenuItemCompat.OnActionExpandListener> implements MenuItem.OnActionExpandListener {
        final MenuItemWrapperICS this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        OnActionExpandListenerWrapper(MenuItemWrapperICS menuItemWrapperICS, MenuItemCompat.OnActionExpandListener onActionExpandListener) {
            super(onActionExpandListener);
            this.this$0 = menuItemWrapperICS;
        }

        @Override // android.view.MenuItem.OnActionExpandListener
        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
            return ((MenuItemCompat.OnActionExpandListener) this.mWrappedObject).onMenuItemActionCollapse(this.this$0.getMenuItemWrapper(menuItem));
        }

        @Override // android.view.MenuItem.OnActionExpandListener
        public boolean onMenuItemActionExpand(MenuItem menuItem) {
            return ((MenuItemCompat.OnActionExpandListener) this.mWrappedObject).onMenuItemActionExpand(this.this$0.getMenuItemWrapper(menuItem));
        }
    }

    /* loaded from: MenuItemWrapperICS$OnMenuItemClickListenerWrapper.class */
    private class OnMenuItemClickListenerWrapper extends BaseWrapper<MenuItem.OnMenuItemClickListener> implements MenuItem.OnMenuItemClickListener {
        final MenuItemWrapperICS this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        OnMenuItemClickListenerWrapper(MenuItemWrapperICS menuItemWrapperICS, MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
            super(onMenuItemClickListener);
            this.this$0 = menuItemWrapperICS;
        }

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem menuItem) {
            return ((MenuItem.OnMenuItemClickListener) this.mWrappedObject).onMenuItemClick(this.this$0.getMenuItemWrapper(menuItem));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuItemWrapperICS(MenuItem menuItem) {
        this(menuItem, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuItemWrapperICS(MenuItem menuItem, boolean z) {
        super(menuItem);
        this.mLastRequestVisible = menuItem.isVisible();
        this.mEmulateProviderVisibilityOverride = z;
    }

    final boolean checkActionProviderOverrideVisibility() {
        android.support.v4.view.ActionProvider supportActionProvider;
        if (!this.mLastRequestVisible || (supportActionProvider = getSupportActionProvider()) == null || !supportActionProvider.overridesItemVisibility() || supportActionProvider.isVisible()) {
            return false;
        }
        wrappedSetVisible(false);
        return true;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public boolean collapseActionView() {
        return ((MenuItem) this.mWrappedObject).collapseActionView();
    }

    ActionProviderWrapper createActionProviderWrapper(android.support.v4.view.ActionProvider actionProvider) {
        return new ActionProviderWrapper(this, actionProvider);
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public boolean expandActionView() {
        return ((MenuItem) this.mWrappedObject).expandActionView();
    }

    @Override // android.view.MenuItem
    public android.view.ActionProvider getActionProvider() {
        return ((MenuItem) this.mWrappedObject).getActionProvider();
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public View getActionView() {
        View actionView = ((MenuItem) this.mWrappedObject).getActionView();
        return actionView instanceof CollapsibleActionViewWrapper ? ((CollapsibleActionViewWrapper) actionView).getWrappedView() : actionView;
    }

    @Override // android.view.MenuItem
    public char getAlphabeticShortcut() {
        return ((MenuItem) this.mWrappedObject).getAlphabeticShortcut();
    }

    @Override // android.view.MenuItem
    public int getGroupId() {
        return ((MenuItem) this.mWrappedObject).getGroupId();
    }

    @Override // android.view.MenuItem
    public Drawable getIcon() {
        return ((MenuItem) this.mWrappedObject).getIcon();
    }

    @Override // android.view.MenuItem
    public Intent getIntent() {
        return ((MenuItem) this.mWrappedObject).getIntent();
    }

    @Override // android.view.MenuItem
    public int getItemId() {
        return ((MenuItem) this.mWrappedObject).getItemId();
    }

    @Override // android.view.MenuItem
    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return ((MenuItem) this.mWrappedObject).getMenuInfo();
    }

    @Override // android.view.MenuItem
    public char getNumericShortcut() {
        return ((MenuItem) this.mWrappedObject).getNumericShortcut();
    }

    @Override // android.view.MenuItem
    public int getOrder() {
        return ((MenuItem) this.mWrappedObject).getOrder();
    }

    @Override // android.view.MenuItem
    public SubMenu getSubMenu() {
        return getSubMenuWrapper(((MenuItem) this.mWrappedObject).getSubMenu());
    }

    @Override // android.support.v4.internal.view.SupportMenuItem
    public android.support.v4.view.ActionProvider getSupportActionProvider() {
        ActionProviderWrapper actionProviderWrapper = (ActionProviderWrapper) ((MenuItem) this.mWrappedObject).getActionProvider();
        return actionProviderWrapper != null ? actionProviderWrapper.mInner : null;
    }

    @Override // android.view.MenuItem
    public CharSequence getTitle() {
        return ((MenuItem) this.mWrappedObject).getTitle();
    }

    @Override // android.view.MenuItem
    public CharSequence getTitleCondensed() {
        return ((MenuItem) this.mWrappedObject).getTitleCondensed();
    }

    @Override // android.view.MenuItem
    public boolean hasSubMenu() {
        return ((MenuItem) this.mWrappedObject).hasSubMenu();
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public boolean isActionViewExpanded() {
        return ((MenuItem) this.mWrappedObject).isActionViewExpanded();
    }

    @Override // android.view.MenuItem
    public boolean isCheckable() {
        return ((MenuItem) this.mWrappedObject).isCheckable();
    }

    @Override // android.view.MenuItem
    public boolean isChecked() {
        return ((MenuItem) this.mWrappedObject).isChecked();
    }

    @Override // android.view.MenuItem
    public boolean isEnabled() {
        return ((MenuItem) this.mWrappedObject).isEnabled();
    }

    @Override // android.view.MenuItem
    public boolean isVisible() {
        return ((MenuItem) this.mWrappedObject).isVisible();
    }

    @Override // android.view.MenuItem
    public MenuItem setActionProvider(android.view.ActionProvider actionProvider) {
        ((MenuItem) this.mWrappedObject).setActionProvider(actionProvider);
        if (actionProvider != null && this.mEmulateProviderVisibilityOverride) {
            checkActionProviderOverrideVisibility();
        }
        return this;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public MenuItem setActionView(int i) {
        ((MenuItem) this.mWrappedObject).setActionView(i);
        View actionView = ((MenuItem) this.mWrappedObject).getActionView();
        if (actionView instanceof android.support.v7.view.CollapsibleActionView) {
            ((MenuItem) this.mWrappedObject).setActionView(new CollapsibleActionViewWrapper(actionView));
        }
        return this;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public MenuItem setActionView(View view) {
        if (view instanceof android.support.v7.view.CollapsibleActionView) {
            view = new CollapsibleActionViewWrapper(view);
        }
        ((MenuItem) this.mWrappedObject).setActionView(view);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setAlphabeticShortcut(char c) {
        ((MenuItem) this.mWrappedObject).setAlphabeticShortcut(c);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setCheckable(boolean z) {
        ((MenuItem) this.mWrappedObject).setCheckable(z);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setChecked(boolean z) {
        ((MenuItem) this.mWrappedObject).setChecked(z);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setEnabled(boolean z) {
        ((MenuItem) this.mWrappedObject).setEnabled(z);
        return this;
    }

    public void setExclusiveCheckable(boolean z) {
        try {
            if (this.mSetExclusiveCheckableMethod == null) {
                this.mSetExclusiveCheckableMethod = ((MenuItem) this.mWrappedObject).getClass().getDeclaredMethod("setExclusiveCheckable", Boolean.TYPE);
            }
            this.mSetExclusiveCheckableMethod.invoke(this.mWrappedObject, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.w(LOG_TAG, "Error while calling setExclusiveCheckable", e);
        }
    }

    @Override // android.view.MenuItem
    public MenuItem setIcon(int i) {
        ((MenuItem) this.mWrappedObject).setIcon(i);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setIcon(Drawable drawable) {
        ((MenuItem) this.mWrappedObject).setIcon(drawable);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setIntent(Intent intent) {
        ((MenuItem) this.mWrappedObject).setIntent(intent);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setNumericShortcut(char c) {
        ((MenuItem) this.mWrappedObject).setNumericShortcut(c);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener onActionExpandListener) {
        ((MenuItem) this.mWrappedObject).setOnActionExpandListener(onActionExpandListener);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        ((MenuItem) this.mWrappedObject).setOnMenuItemClickListener(onMenuItemClickListener != null ? new OnMenuItemClickListenerWrapper(this, onMenuItemClickListener) : null);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setShortcut(char c, char c2) {
        ((MenuItem) this.mWrappedObject).setShortcut(c, c2);
        return this;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public void setShowAsAction(int i) {
        ((MenuItem) this.mWrappedObject).setShowAsAction(i);
    }

    @Override // android.support.v4.internal.view.SupportMenuItem, android.view.MenuItem
    public MenuItem setShowAsActionFlags(int i) {
        ((MenuItem) this.mWrappedObject).setShowAsActionFlags(i);
        return this;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem
    public SupportMenuItem setSupportActionProvider(android.support.v4.view.ActionProvider actionProvider) {
        ((MenuItem) this.mWrappedObject).setActionProvider(actionProvider != null ? createActionProviderWrapper(actionProvider) : null);
        return this;
    }

    @Override // android.support.v4.internal.view.SupportMenuItem
    public SupportMenuItem setSupportOnActionExpandListener(MenuItemCompat.OnActionExpandListener onActionExpandListener) {
        ((MenuItem) this.mWrappedObject).setOnActionExpandListener(onActionExpandListener != null ? new OnActionExpandListenerWrapper(this, onActionExpandListener) : null);
        return null;
    }

    @Override // android.view.MenuItem
    public MenuItem setTitle(int i) {
        ((MenuItem) this.mWrappedObject).setTitle(i);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setTitle(CharSequence charSequence) {
        ((MenuItem) this.mWrappedObject).setTitle(charSequence);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setTitleCondensed(CharSequence charSequence) {
        ((MenuItem) this.mWrappedObject).setTitleCondensed(charSequence);
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setVisible(boolean z) {
        if (this.mEmulateProviderVisibilityOverride) {
            this.mLastRequestVisible = z;
            if (checkActionProviderOverrideVisibility()) {
                return this;
            }
        }
        return wrappedSetVisible(z);
    }

    final MenuItem wrappedSetVisible(boolean z) {
        return ((MenuItem) this.mWrappedObject).setVisible(z);
    }
}