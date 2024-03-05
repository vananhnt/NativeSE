package android.support.v7.internal.app;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.view.menu.ListMenuPresenter;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.internal.widget.DecorToolbar;
import android.support.v7.internal.widget.ToolbarWidgetWrapper;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.WindowCallbackWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SpinnerAdapter;
import java.util.ArrayList;

/* loaded from: ToolbarActionBar.class */
public class ToolbarActionBar extends ActionBar {
    private DecorToolbar mDecorToolbar;
    private boolean mLastMenuVisibility;
    private ListMenuPresenter mListMenuPresenter;
    private boolean mMenuCallbackSet;
    private Toolbar mToolbar;
    private boolean mToolbarMenuPrepared;
    private Window mWindow;
    private WindowCallback mWindowCallback;
    private ArrayList<ActionBar.OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList<>();
    private final Runnable mMenuInvalidator = new Runnable(this) { // from class: android.support.v7.internal.app.ToolbarActionBar.1
        final ToolbarActionBar this$0;

        {
            this.this$0 = this;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.populateOptionsMenu();
        }
    };
    private final Toolbar.OnMenuItemClickListener mMenuClicker = new Toolbar.OnMenuItemClickListener(this) { // from class: android.support.v7.internal.app.ToolbarActionBar.2
        final ToolbarActionBar this$0;

        {
            this.this$0 = this;
        }

        @Override // android.support.v7.widget.Toolbar.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem menuItem) {
            return this.this$0.mWindowCallback.onMenuItemSelected(0, menuItem);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ToolbarActionBar$ActionMenuPresenterCallback.class */
    public final class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        private boolean mClosingActionMenu;
        final ToolbarActionBar this$0;

        private ActionMenuPresenterCallback(ToolbarActionBar toolbarActionBar) {
            this.this$0 = toolbarActionBar;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
            if (this.mClosingActionMenu) {
                return;
            }
            this.mClosingActionMenu = true;
            this.this$0.mToolbar.dismissPopupMenus();
            if (this.this$0.mWindowCallback != null) {
                this.this$0.mWindowCallback.onPanelClosed(8, menuBuilder);
            }
            this.mClosingActionMenu = false;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            if (this.this$0.mWindowCallback != null) {
                this.this$0.mWindowCallback.onMenuOpened(8, menuBuilder);
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ToolbarActionBar$MenuBuilderCallback.class */
    public final class MenuBuilderCallback implements MenuBuilder.Callback {
        final ToolbarActionBar this$0;

        private MenuBuilderCallback(ToolbarActionBar toolbarActionBar) {
            this.this$0 = toolbarActionBar;
        }

        @Override // android.support.v7.internal.view.menu.MenuBuilder.Callback
        public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
            return false;
        }

        @Override // android.support.v7.internal.view.menu.MenuBuilder.Callback
        public void onMenuModeChange(MenuBuilder menuBuilder) {
            if (this.this$0.mWindowCallback != null) {
                if (this.this$0.mToolbar.isOverflowMenuShowing()) {
                    this.this$0.mWindowCallback.onPanelClosed(8, menuBuilder);
                } else if (this.this$0.mWindowCallback.onPreparePanel(0, null, menuBuilder)) {
                    this.this$0.mWindowCallback.onMenuOpened(8, menuBuilder);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ToolbarActionBar$PanelMenuPresenterCallback.class */
    public final class PanelMenuPresenterCallback implements MenuPresenter.Callback {
        final ToolbarActionBar this$0;

        private PanelMenuPresenterCallback(ToolbarActionBar toolbarActionBar) {
            this.this$0 = toolbarActionBar;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
            if (this.this$0.mWindowCallback != null) {
                this.this$0.mWindowCallback.onPanelClosed(0, menuBuilder);
            }
            this.this$0.mWindow.closePanel(0);
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            if (menuBuilder != null || this.this$0.mWindowCallback == null) {
                return true;
            }
            this.this$0.mWindowCallback.onMenuOpened(0, menuBuilder);
            return true;
        }
    }

    /* loaded from: ToolbarActionBar$ToolbarCallbackWrapper.class */
    private class ToolbarCallbackWrapper extends WindowCallbackWrapper {
        final ToolbarActionBar this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ToolbarCallbackWrapper(ToolbarActionBar toolbarActionBar, WindowCallback windowCallback) {
            super(windowCallback);
            this.this$0 = toolbarActionBar;
        }

        @Override // android.support.v7.widget.WindowCallbackWrapper, android.support.v7.internal.app.WindowCallback
        public View onCreatePanelView(int i) {
            if (i == 0) {
                if (!this.this$0.mToolbarMenuPrepared) {
                    this.this$0.populateOptionsMenu();
                    this.this$0.mToolbar.removeCallbacks(this.this$0.mMenuInvalidator);
                }
                if (this.this$0.mToolbarMenuPrepared && this.this$0.mWindowCallback != null) {
                    Menu menu = this.this$0.getMenu();
                    if (this.this$0.mWindowCallback.onPreparePanel(i, null, menu) && this.this$0.mWindowCallback.onMenuOpened(i, menu)) {
                        return this.this$0.getListMenuView(menu);
                    }
                }
            }
            return super.onCreatePanelView(i);
        }

        @Override // android.support.v7.widget.WindowCallbackWrapper, android.support.v7.internal.app.WindowCallback
        public boolean onPreparePanel(int i, View view, Menu menu) {
            boolean onPreparePanel = super.onPreparePanel(i, view, menu);
            if (onPreparePanel && !this.this$0.mToolbarMenuPrepared) {
                this.this$0.mDecorToolbar.setMenuPrepared();
                this.this$0.mToolbarMenuPrepared = true;
            }
            return onPreparePanel;
        }
    }

    public ToolbarActionBar(Toolbar toolbar, CharSequence charSequence, Window window, WindowCallback windowCallback) {
        this.mToolbar = toolbar;
        this.mDecorToolbar = new ToolbarWidgetWrapper(toolbar, false);
        this.mWindowCallback = new ToolbarCallbackWrapper(this, windowCallback);
        this.mDecorToolbar.setWindowCallback(this.mWindowCallback);
        toolbar.setOnMenuItemClickListener(this.mMenuClicker);
        this.mDecorToolbar.setWindowTitle(charSequence);
        this.mWindow = window;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getListMenuView(Menu menu) {
        ListMenuPresenter listMenuPresenter;
        if (menu == null || (listMenuPresenter = this.mListMenuPresenter) == null || listMenuPresenter.getAdapter().getCount() <= 0) {
            return null;
        }
        return (View) this.mListMenuPresenter.getMenuView(this.mToolbar);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Menu getMenu() {
        if (!this.mMenuCallbackSet) {
            this.mToolbar.setMenuCallbacks(new ActionMenuPresenterCallback(), new MenuBuilderCallback());
            this.mMenuCallbackSet = true;
        }
        return this.mToolbar.getMenu();
    }

    @Override // android.support.v7.app.ActionBar
    public void addOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener onMenuVisibilityListener) {
        this.mMenuVisibilityListeners.add(onMenuVisibilityListener);
    }

    @Override // android.support.v7.app.ActionBar
    public void addTab(ActionBar.Tab tab) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public void addTab(ActionBar.Tab tab, int i) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public void addTab(ActionBar.Tab tab, int i, boolean z) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public void addTab(ActionBar.Tab tab, boolean z) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public boolean collapseActionView() {
        if (this.mToolbar.hasExpandedActionView()) {
            this.mToolbar.collapseActionView();
            return true;
        }
        return false;
    }

    @Override // android.support.v7.app.ActionBar
    public void dispatchMenuVisibilityChanged(boolean z) {
        if (z == this.mLastMenuVisibility) {
            return;
        }
        this.mLastMenuVisibility = z;
        int size = this.mMenuVisibilityListeners.size();
        for (int i = 0; i < size; i++) {
            this.mMenuVisibilityListeners.get(i).onMenuVisibilityChanged(z);
        }
    }

    @Override // android.support.v7.app.ActionBar
    public View getCustomView() {
        return this.mDecorToolbar.getCustomView();
    }

    @Override // android.support.v7.app.ActionBar
    public int getDisplayOptions() {
        return this.mDecorToolbar.getDisplayOptions();
    }

    @Override // android.support.v7.app.ActionBar
    public float getElevation() {
        return ViewCompat.getElevation(this.mToolbar);
    }

    @Override // android.support.v7.app.ActionBar
    public int getHeight() {
        return this.mToolbar.getHeight();
    }

    @Override // android.support.v7.app.ActionBar
    public int getNavigationItemCount() {
        return 0;
    }

    @Override // android.support.v7.app.ActionBar
    public int getNavigationMode() {
        return 0;
    }

    @Override // android.support.v7.app.ActionBar
    public int getSelectedNavigationIndex() {
        return -1;
    }

    @Override // android.support.v7.app.ActionBar
    public ActionBar.Tab getSelectedTab() {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public CharSequence getSubtitle() {
        return this.mToolbar.getSubtitle();
    }

    @Override // android.support.v7.app.ActionBar
    public ActionBar.Tab getTabAt(int i) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public int getTabCount() {
        return 0;
    }

    @Override // android.support.v7.app.ActionBar
    public Context getThemedContext() {
        return this.mToolbar.getContext();
    }

    @Override // android.support.v7.app.ActionBar
    public CharSequence getTitle() {
        return this.mToolbar.getTitle();
    }

    public WindowCallback getWrappedWindowCallback() {
        return this.mWindowCallback;
    }

    @Override // android.support.v7.app.ActionBar
    public void hide() {
        this.mToolbar.setVisibility(8);
    }

    @Override // android.support.v7.app.ActionBar
    public boolean invalidateOptionsMenu() {
        this.mToolbar.removeCallbacks(this.mMenuInvalidator);
        ViewCompat.postOnAnimation(this.mToolbar, this.mMenuInvalidator);
        return true;
    }

    @Override // android.support.v7.app.ActionBar
    public boolean isShowing() {
        return this.mToolbar.getVisibility() == 0;
    }

    @Override // android.support.v7.app.ActionBar
    public boolean isTitleTruncated() {
        return super.isTitleTruncated();
    }

    @Override // android.support.v7.app.ActionBar
    public ActionBar.Tab newTab() {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override // android.support.v7.app.ActionBar
    public boolean onMenuKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1) {
            openOptionsMenu();
            return true;
        }
        return true;
    }

    @Override // android.support.v7.app.ActionBar
    public boolean openOptionsMenu() {
        return this.mToolbar.showOverflowMenu();
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0054 A[DONT_GENERATE] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x005b A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void populateOptionsMenu() {
        /*
            r5 = this;
            r0 = r5
            android.view.Menu r0 = r0.getMenu()
            r6 = r0
            r0 = r6
            boolean r0 = r0 instanceof android.support.v7.internal.view.menu.MenuBuilder
            if (r0 == 0) goto L14
            r0 = r6
            android.support.v7.internal.view.menu.MenuBuilder r0 = (android.support.v7.internal.view.menu.MenuBuilder) r0
            r7 = r0
            goto L16
        L14:
            r0 = 0
            r7 = r0
        L16:
            r0 = r7
            if (r0 == 0) goto L21
            r0 = r7
            r0.stopDispatchingItemsChanged()
            goto L21
        L21:
            r0 = r6
            r0.clear()     // Catch: java.lang.Throwable -> L5c
            r0 = r5
            android.support.v7.internal.app.WindowCallback r0 = r0.mWindowCallback     // Catch: java.lang.Throwable -> L5c
            r1 = 0
            r2 = r6
            boolean r0 = r0.onCreatePanelMenu(r1, r2)     // Catch: java.lang.Throwable -> L5c
            if (r0 == 0) goto L4a
            r0 = r5
            android.support.v7.internal.app.WindowCallback r0 = r0.mWindowCallback     // Catch: java.lang.Throwable -> L5c
            r1 = 0
            r2 = 0
            r3 = r6
            boolean r0 = r0.onPreparePanel(r1, r2, r3)     // Catch: java.lang.Throwable -> L5c
            if (r0 != 0) goto L47
            goto L4a
        L47:
            goto L50
        L4a:
            r0 = r6
            r0.clear()     // Catch: java.lang.Throwable -> L5c
        L50:
            r0 = r7
            if (r0 == 0) goto L5b
            r0 = r7
            r0.startDispatchingItemsChanged()
            goto L5b
        L5b:
            return
        L5c:
            r6 = move-exception
            r0 = r7
            if (r0 == 0) goto L68
            r0 = r7
            r0.startDispatchingItemsChanged()
            goto L68
        L68:
            r0 = r6
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.internal.app.ToolbarActionBar.populateOptionsMenu():void");
    }

    @Override // android.support.v7.app.ActionBar
    public void removeAllTabs() {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public void removeOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener onMenuVisibilityListener) {
        this.mMenuVisibilityListeners.remove(onMenuVisibilityListener);
    }

    @Override // android.support.v7.app.ActionBar
    public void removeTab(ActionBar.Tab tab) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public void removeTabAt(int i) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public void selectTab(ActionBar.Tab tab) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }

    @Override // android.support.v7.app.ActionBar
    public void setBackgroundDrawable(@Nullable Drawable drawable) {
        this.mToolbar.setBackgroundDrawable(drawable);
    }

    @Override // android.support.v7.app.ActionBar
    public void setCustomView(int i) {
        setCustomView(LayoutInflater.from(this.mToolbar.getContext()).inflate(i, (ViewGroup) this.mToolbar, false));
    }

    @Override // android.support.v7.app.ActionBar
    public void setCustomView(View view) {
        setCustomView(view, new ActionBar.LayoutParams(-2, -2));
    }

    @Override // android.support.v7.app.ActionBar
    public void setCustomView(View view, ActionBar.LayoutParams layoutParams) {
        view.setLayoutParams(layoutParams);
        this.mDecorToolbar.setCustomView(view);
    }

    @Override // android.support.v7.app.ActionBar
    public void setDefaultDisplayHomeAsUpEnabled(boolean z) {
    }

    @Override // android.support.v7.app.ActionBar
    public void setDisplayHomeAsUpEnabled(boolean z) {
        setDisplayOptions(z ? 4 : 0, 4);
    }

    @Override // android.support.v7.app.ActionBar
    public void setDisplayOptions(int i) {
        setDisplayOptions(i, -1);
    }

    @Override // android.support.v7.app.ActionBar
    public void setDisplayOptions(int i, int i2) {
        this.mDecorToolbar.setDisplayOptions((i & i2) | ((i2 ^ (-1)) & this.mDecorToolbar.getDisplayOptions()));
    }

    @Override // android.support.v7.app.ActionBar
    public void setDisplayShowCustomEnabled(boolean z) {
        setDisplayOptions(z ? 16 : 0, 16);
    }

    @Override // android.support.v7.app.ActionBar
    public void setDisplayShowHomeEnabled(boolean z) {
        setDisplayOptions(z ? 2 : 0, 2);
    }

    @Override // android.support.v7.app.ActionBar
    public void setDisplayShowTitleEnabled(boolean z) {
        setDisplayOptions(z ? 8 : 0, 8);
    }

    @Override // android.support.v7.app.ActionBar
    public void setDisplayUseLogoEnabled(boolean z) {
        setDisplayOptions(z ? 1 : 0, 1);
    }

    @Override // android.support.v7.app.ActionBar
    public void setElevation(float f) {
        ViewCompat.setElevation(this.mToolbar, f);
    }

    @Override // android.support.v7.app.ActionBar
    public void setHomeActionContentDescription(int i) {
        this.mDecorToolbar.setNavigationContentDescription(i);
    }

    @Override // android.support.v7.app.ActionBar
    public void setHomeActionContentDescription(CharSequence charSequence) {
        this.mDecorToolbar.setNavigationContentDescription(charSequence);
    }

    @Override // android.support.v7.app.ActionBar
    public void setHomeAsUpIndicator(int i) {
        this.mToolbar.setNavigationIcon(i);
    }

    @Override // android.support.v7.app.ActionBar
    public void setHomeAsUpIndicator(Drawable drawable) {
        this.mToolbar.setNavigationIcon(drawable);
    }

    @Override // android.support.v7.app.ActionBar
    public void setHomeButtonEnabled(boolean z) {
    }

    @Override // android.support.v7.app.ActionBar
    public void setIcon(int i) {
        this.mDecorToolbar.setIcon(i);
    }

    @Override // android.support.v7.app.ActionBar
    public void setIcon(Drawable drawable) {
        this.mDecorToolbar.setIcon(drawable);
    }

    public void setListMenuPresenter(ListMenuPresenter listMenuPresenter) {
        Menu menu = getMenu();
        if (menu instanceof MenuBuilder) {
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            ListMenuPresenter listMenuPresenter2 = this.mListMenuPresenter;
            if (listMenuPresenter2 != null) {
                listMenuPresenter2.setCallback(null);
                menuBuilder.removeMenuPresenter(this.mListMenuPresenter);
            }
            this.mListMenuPresenter = listMenuPresenter;
            if (listMenuPresenter != null) {
                listMenuPresenter.setCallback(new PanelMenuPresenterCallback());
                menuBuilder.addMenuPresenter(listMenuPresenter);
            }
        }
    }

    @Override // android.support.v7.app.ActionBar
    public void setListNavigationCallbacks(SpinnerAdapter spinnerAdapter, ActionBar.OnNavigationListener onNavigationListener) {
        this.mDecorToolbar.setDropdownParams(spinnerAdapter, new NavItemSelectedListener(onNavigationListener));
    }

    @Override // android.support.v7.app.ActionBar
    public void setLogo(int i) {
        this.mDecorToolbar.setLogo(i);
    }

    @Override // android.support.v7.app.ActionBar
    public void setLogo(Drawable drawable) {
        this.mDecorToolbar.setLogo(drawable);
    }

    @Override // android.support.v7.app.ActionBar
    public void setNavigationMode(int i) {
        if (i == 2) {
            throw new IllegalArgumentException("Tabs not supported in this configuration");
        }
        this.mDecorToolbar.setNavigationMode(i);
    }

    @Override // android.support.v7.app.ActionBar
    public void setSelectedNavigationItem(int i) {
        if (this.mDecorToolbar.getNavigationMode() != 1) {
            throw new IllegalStateException("setSelectedNavigationIndex not valid for current navigation mode");
        }
        this.mDecorToolbar.setDropdownSelectedPosition(i);
    }

    @Override // android.support.v7.app.ActionBar
    public void setShowHideAnimationEnabled(boolean z) {
    }

    @Override // android.support.v7.app.ActionBar
    public void setSplitBackgroundDrawable(Drawable drawable) {
    }

    @Override // android.support.v7.app.ActionBar
    public void setStackedBackgroundDrawable(Drawable drawable) {
    }

    @Override // android.support.v7.app.ActionBar
    public void setSubtitle(int i) {
        DecorToolbar decorToolbar = this.mDecorToolbar;
        decorToolbar.setSubtitle(i != 0 ? decorToolbar.getContext().getText(i) : null);
    }

    @Override // android.support.v7.app.ActionBar
    public void setSubtitle(CharSequence charSequence) {
        this.mDecorToolbar.setSubtitle(charSequence);
    }

    @Override // android.support.v7.app.ActionBar
    public void setTitle(int i) {
        DecorToolbar decorToolbar = this.mDecorToolbar;
        decorToolbar.setTitle(i != 0 ? decorToolbar.getContext().getText(i) : null);
    }

    @Override // android.support.v7.app.ActionBar
    public void setTitle(CharSequence charSequence) {
        this.mDecorToolbar.setTitle(charSequence);
    }

    @Override // android.support.v7.app.ActionBar
    public void setWindowTitle(CharSequence charSequence) {
        this.mDecorToolbar.setWindowTitle(charSequence);
    }

    @Override // android.support.v7.app.ActionBar
    public void show() {
        this.mToolbar.setVisibility(0);
    }

    @Override // android.support.v7.app.ActionBar
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return this.mWindowCallback.startActionMode(callback);
    }
}