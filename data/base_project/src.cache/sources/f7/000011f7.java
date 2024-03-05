package android.support.v7.app;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.appcompat.R;
import android.support.v7.internal.app.WindowCallback;
import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ActionBarActivityDelegate.class */
public abstract class ActionBarActivityDelegate {
    static final String METADATA_UI_OPTIONS = "android.support.UI_OPTIONS";
    private static final String TAG = "ActionBarActivityDelegate";
    private ActionBar mActionBar;
    final ActionBarActivity mActivity;
    boolean mHasActionBar;
    private boolean mIsDestroyed;
    boolean mIsFloating;
    private MenuInflater mMenuInflater;
    boolean mOverlayActionBar;
    boolean mOverlayActionMode;
    final WindowCallback mDefaultWindowCallback = new WindowCallback(this) { // from class: android.support.v7.app.ActionBarActivityDelegate.1
        final ActionBarActivityDelegate this$0;

        {
            this.this$0 = this;
        }

        @Override // android.support.v7.internal.app.WindowCallback
        public boolean onCreatePanelMenu(int i, Menu menu) {
            return this.this$0.mActivity.superOnCreatePanelMenu(i, menu);
        }

        @Override // android.support.v7.internal.app.WindowCallback
        public View onCreatePanelView(int i) {
            return null;
        }

        @Override // android.support.v7.internal.app.WindowCallback
        public boolean onMenuItemSelected(int i, MenuItem menuItem) {
            return this.this$0.mActivity.onMenuItemSelected(i, menuItem);
        }

        @Override // android.support.v7.internal.app.WindowCallback
        public boolean onMenuOpened(int i, Menu menu) {
            return this.this$0.mActivity.onMenuOpened(i, menu);
        }

        @Override // android.support.v7.internal.app.WindowCallback
        public void onPanelClosed(int i, Menu menu) {
            this.this$0.mActivity.onPanelClosed(i, menu);
        }

        @Override // android.support.v7.internal.app.WindowCallback
        public boolean onPreparePanel(int i, View view, Menu menu) {
            return this.this$0.mActivity.superOnPreparePanel(i, view, menu);
        }

        @Override // android.support.v7.internal.app.WindowCallback
        public ActionMode startActionMode(ActionMode.Callback callback) {
            return this.this$0.startSupportActionModeFromWindow(callback);
        }
    };
    private WindowCallback mWindowCallback = this.mDefaultWindowCallback;

    /* loaded from: ActionBarActivityDelegate$ActionBarDrawableToggleImpl.class */
    private class ActionBarDrawableToggleImpl implements ActionBarDrawerToggle.Delegate, ActionBarDrawerToggle.Delegate {
        final ActionBarActivityDelegate this$0;

        private ActionBarDrawableToggleImpl(ActionBarActivityDelegate actionBarActivityDelegate) {
            this.this$0 = actionBarActivityDelegate;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate
        public Context getActionBarThemedContext() {
            return this.this$0.getActionBarThemedContext();
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public Drawable getThemeUpIndicator() {
            TypedArray obtainStyledAttributes = this.this$0.getActionBarThemedContext().obtainStyledAttributes(new int[]{this.this$0.getHomeAsUpIndicatorAttrId()});
            Drawable drawable = obtainStyledAttributes.getDrawable(0);
            obtainStyledAttributes.recycle();
            return drawable;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public void setActionBarDescription(int i) {
            ActionBar supportActionBar = this.this$0.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setHomeActionContentDescription(i);
            }
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public void setActionBarUpIndicator(Drawable drawable, int i) {
            ActionBar supportActionBar = this.this$0.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setHomeAsUpIndicator(drawable);
                supportActionBar.setHomeActionContentDescription(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActionBarActivityDelegate(ActionBarActivity actionBarActivity) {
        this.mActivity = actionBarActivity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ActionBarActivityDelegate createDelegate(ActionBarActivity actionBarActivity) {
        return Build.VERSION.SDK_INT >= 11 ? new ActionBarActivityDelegateHC(actionBarActivity) : new ActionBarActivityDelegateBase(actionBarActivity);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void addContentView(View view, ViewGroup.LayoutParams layoutParams);

    abstract ActionBar createSupportActionBar();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract View createView(String str, @NonNull AttributeSet attributeSet);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void destroy() {
        this.mIsDestroyed = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Context getActionBarThemedContext() {
        ActionBarActivity actionBarActivity = null;
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            actionBarActivity = supportActionBar.getThemedContext();
        }
        if (actionBarActivity == null) {
            actionBarActivity = this.mActivity;
        }
        return actionBarActivity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ActionBarDrawerToggle.Delegate getDrawerToggleDelegate() {
        return new ActionBarDrawableToggleImpl();
    }

    abstract int getHomeAsUpIndicatorAttrId();

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuInflater getMenuInflater() {
        if (this.mMenuInflater == null) {
            this.mMenuInflater = new SupportMenuInflater(getActionBarThemedContext());
        }
        return this.mMenuInflater;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ActionBar getSupportActionBar() {
        if (this.mHasActionBar && this.mActionBar == null) {
            this.mActionBar = createSupportActionBar();
        }
        return this.mActionBar;
    }

    final String getUiOptionsFromMetadata() {
        try {
            ActivityInfo activityInfo = this.mActivity.getPackageManager().getActivityInfo(this.mActivity.getComponentName(), 128);
            String str = null;
            if (activityInfo.metaData != null) {
                str = activityInfo.metaData.getString(METADATA_UI_OPTIONS);
            }
            return str;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getUiOptionsFromMetadata: Activity '" + this.mActivity.getClass().getSimpleName() + "' not in manifest");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ActionBarDrawerToggle.Delegate getV7DrawerToggleDelegate() {
        return new ActionBarDrawableToggleImpl();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final WindowCallback getWindowCallback() {
        return this.mWindowCallback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isDestroyed() {
        return this.mIsDestroyed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean onBackPressed();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void onConfigurationChanged(Configuration configuration);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void onContentChanged();

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onCreate(Bundle bundle) {
        TypedArray obtainStyledAttributes = this.mActivity.obtainStyledAttributes(R.styleable.Theme);
        if (!obtainStyledAttributes.hasValue(R.styleable.Theme_windowActionBar)) {
            obtainStyledAttributes.recycle();
            throw new IllegalStateException("You need to use a Theme.AppCompat theme (or descendant) with this activity.");
        }
        this.mHasActionBar = obtainStyledAttributes.getBoolean(R.styleable.Theme_windowActionBar, false);
        this.mOverlayActionBar = obtainStyledAttributes.getBoolean(R.styleable.Theme_windowActionBarOverlay, false);
        this.mOverlayActionMode = obtainStyledAttributes.getBoolean(R.styleable.Theme_windowActionModeOverlay, false);
        this.mIsFloating = obtainStyledAttributes.getBoolean(R.styleable.Theme_android_windowIsFloating, false);
        obtainStyledAttributes.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean onCreatePanelMenu(int i, Menu menu);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract View onCreatePanelView(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean onKeyShortcut(int i, KeyEvent keyEvent);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean onMenuOpened(int i, Menu menu);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void onPanelClosed(int i, Menu menu);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void onPostResume();

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onPrepareOptionsPanel(View view, Menu menu) {
        return Build.VERSION.SDK_INT < 16 ? this.mActivity.onPrepareOptionsMenu(menu) : this.mActivity.superOnPrepareOptionsPanel(view, menu);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean onPreparePanel(int i, View view, Menu menu);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void onStop();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void onTitleChanged(CharSequence charSequence);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setContentView(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setContentView(View view);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setContentView(View view, ViewGroup.LayoutParams layoutParams);

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setSupportActionBar(ActionBar actionBar) {
        this.mActionBar = actionBar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setSupportActionBar(Toolbar toolbar);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setSupportProgress(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setSupportProgressBarIndeterminate(boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setSupportProgressBarIndeterminateVisibility(boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setSupportProgressBarVisibility(boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setWindowCallback(WindowCallback windowCallback) {
        if (windowCallback == null) {
            throw new IllegalArgumentException("callback can not be null");
        }
        this.mWindowCallback = windowCallback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract ActionMode startSupportActionMode(ActionMode.Callback callback);

    abstract ActionMode startSupportActionModeFromWindow(ActionMode.Callback callback);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void supportInvalidateOptionsMenu();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean supportRequestWindowFeature(int i);
}