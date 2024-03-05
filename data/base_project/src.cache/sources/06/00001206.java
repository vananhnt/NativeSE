package android.support.v7.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggleHoneycomb;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/* loaded from: ActionBarDrawerToggle.class */
public class ActionBarDrawerToggle implements DrawerLayout.DrawerListener {
    private final Delegate mActivityImpl;
    private final int mCloseDrawerContentDescRes;
    private boolean mDrawerIndicatorEnabled;
    private final DrawerLayout mDrawerLayout;
    private boolean mHasCustomUpIndicator;
    private Drawable mHomeAsUpIndicator;
    private final int mOpenDrawerContentDescRes;
    private DrawerToggle mSlider;
    private View.OnClickListener mToolbarNavigationClickListener;

    /* loaded from: ActionBarDrawerToggle$Delegate.class */
    public interface Delegate {
        Context getActionBarThemedContext();

        Drawable getThemeUpIndicator();

        void setActionBarDescription(int i);

        void setActionBarUpIndicator(Drawable drawable, int i);
    }

    /* loaded from: ActionBarDrawerToggle$DelegateProvider.class */
    public interface DelegateProvider {
        @Nullable
        Delegate getDrawerToggleDelegate();
    }

    /* loaded from: ActionBarDrawerToggle$DrawerArrowDrawableToggle.class */
    static class DrawerArrowDrawableToggle extends DrawerArrowDrawable implements DrawerToggle {
        private final Activity mActivity;

        public DrawerArrowDrawableToggle(Activity activity, Context context) {
            super(context);
            this.mActivity = activity;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.DrawerToggle
        public float getPosition() {
            return super.getProgress();
        }

        @Override // android.support.v7.app.DrawerArrowDrawable
        boolean isLayoutRtl() {
            boolean z = true;
            if (ViewCompat.getLayoutDirection(this.mActivity.getWindow().getDecorView()) != 1) {
                z = false;
            }
            return z;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.DrawerToggle
        public void setPosition(float f) {
            if (f == 1.0f) {
                setVerticalMirror(true);
            } else if (f == 0.0f) {
                setVerticalMirror(false);
            }
            super.setProgress(f);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActionBarDrawerToggle$DrawerToggle.class */
    public interface DrawerToggle {
        float getPosition();

        void setPosition(float f);
    }

    /* loaded from: ActionBarDrawerToggle$DummyDelegate.class */
    static class DummyDelegate implements Delegate {
        final Activity mActivity;

        DummyDelegate(Activity activity) {
            this.mActivity = activity;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate
        public Context getActionBarThemedContext() {
            return this.mActivity;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public Drawable getThemeUpIndicator() {
            return null;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public void setActionBarDescription(int i) {
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public void setActionBarUpIndicator(Drawable drawable, int i) {
        }
    }

    /* loaded from: ActionBarDrawerToggle$HoneycombDelegate.class */
    private static class HoneycombDelegate implements Delegate {
        final Activity mActivity;
        ActionBarDrawerToggleHoneycomb.SetIndicatorInfo mSetIndicatorInfo;

        private HoneycombDelegate(Activity activity) {
            this.mActivity = activity;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate
        public Context getActionBarThemedContext() {
            android.app.ActionBar actionBar = this.mActivity.getActionBar();
            return actionBar != null ? actionBar.getThemedContext() : this.mActivity;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public Drawable getThemeUpIndicator() {
            return ActionBarDrawerToggleHoneycomb.getThemeUpIndicator(this.mActivity);
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public void setActionBarDescription(int i) {
            this.mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarDescription(this.mSetIndicatorInfo, this.mActivity, i);
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public void setActionBarUpIndicator(Drawable drawable, int i) {
            this.mActivity.getActionBar().setDisplayShowHomeEnabled(true);
            this.mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(this.mSetIndicatorInfo, this.mActivity, drawable, i);
            this.mActivity.getActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    /* loaded from: ActionBarDrawerToggle$JellybeanMr2Delegate.class */
    private static class JellybeanMr2Delegate implements Delegate {
        final Activity mActivity;

        private JellybeanMr2Delegate(Activity activity) {
            this.mActivity = activity;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate
        public Context getActionBarThemedContext() {
            android.app.ActionBar actionBar = this.mActivity.getActionBar();
            return actionBar != null ? actionBar.getThemedContext() : this.mActivity;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public Drawable getThemeUpIndicator() {
            TypedArray obtainStyledAttributes = getActionBarThemedContext().obtainStyledAttributes(null, new int[]{16843531}, 16843470, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(0);
            obtainStyledAttributes.recycle();
            return drawable;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public void setActionBarDescription(int i) {
            android.app.ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeActionContentDescription(i);
            }
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public void setActionBarUpIndicator(Drawable drawable, int i) {
            android.app.ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeAsUpIndicator(drawable);
                actionBar.setHomeActionContentDescription(i);
            }
        }
    }

    /* loaded from: ActionBarDrawerToggle$TmpDelegateProvider.class */
    interface TmpDelegateProvider {
        @Nullable
        Delegate getV7DrawerToggleDelegate();
    }

    /* loaded from: ActionBarDrawerToggle$ToolbarCompatDelegate.class */
    static class ToolbarCompatDelegate implements Delegate {
        final Toolbar mToolbar;

        ToolbarCompatDelegate(Toolbar toolbar) {
            this.mToolbar = toolbar;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate
        public Context getActionBarThemedContext() {
            return this.mToolbar.getContext();
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public Drawable getThemeUpIndicator() {
            TypedArray obtainStyledAttributes = this.mToolbar.getContext().obtainStyledAttributes(new int[]{16908332});
            Drawable drawable = obtainStyledAttributes.getDrawable(0);
            obtainStyledAttributes.recycle();
            return drawable;
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public void setActionBarDescription(int i) {
            this.mToolbar.setNavigationContentDescription(i);
        }

        @Override // android.support.v7.app.ActionBarDrawerToggle.Delegate, android.support.v4.app.ActionBarDrawerToggle.Delegate
        public void setActionBarUpIndicator(Drawable drawable, int i) {
            this.mToolbar.setNavigationIcon(drawable);
            this.mToolbar.setNavigationContentDescription(i);
        }
    }

    public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int i, int i2) {
        this(activity, null, drawerLayout, null, i, i2);
    }

    public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int i, int i2) {
        this(activity, toolbar, drawerLayout, null, i, i2);
    }

    <T extends Drawable & DrawerToggle> ActionBarDrawerToggle(Activity activity, Toolbar toolbar, DrawerLayout drawerLayout, T t, int i, int i2) {
        this.mDrawerIndicatorEnabled = true;
        if (toolbar != null) {
            this.mActivityImpl = new ToolbarCompatDelegate(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener(this) { // from class: android.support.v7.app.ActionBarDrawerToggle.1
                final ActionBarDrawerToggle this$0;

                {
                    this.this$0 = this;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (this.this$0.mDrawerIndicatorEnabled) {
                        this.this$0.toggle();
                    } else if (this.this$0.mToolbarNavigationClickListener != null) {
                        this.this$0.mToolbarNavigationClickListener.onClick(view);
                    }
                }
            });
        } else if (activity instanceof DelegateProvider) {
            this.mActivityImpl = ((DelegateProvider) activity).getDrawerToggleDelegate();
        } else if (activity instanceof TmpDelegateProvider) {
            this.mActivityImpl = ((TmpDelegateProvider) activity).getV7DrawerToggleDelegate();
        } else if (Build.VERSION.SDK_INT >= 18) {
            this.mActivityImpl = new JellybeanMr2Delegate(activity);
        } else if (Build.VERSION.SDK_INT >= 11) {
            this.mActivityImpl = new HoneycombDelegate(activity);
        } else {
            this.mActivityImpl = new DummyDelegate(activity);
        }
        this.mDrawerLayout = drawerLayout;
        this.mOpenDrawerContentDescRes = i;
        this.mCloseDrawerContentDescRes = i2;
        if (t == null) {
            this.mSlider = new DrawerArrowDrawableToggle(activity, this.mActivityImpl.getActionBarThemedContext());
        } else {
            this.mSlider = t;
        }
        this.mHomeAsUpIndicator = getThemeUpIndicator();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggle() {
        if (this.mDrawerLayout.isDrawerVisible(8388611)) {
            this.mDrawerLayout.closeDrawer(8388611);
        } else {
            this.mDrawerLayout.openDrawer(8388611);
        }
    }

    Drawable getThemeUpIndicator() {
        return this.mActivityImpl.getThemeUpIndicator();
    }

    public View.OnClickListener getToolbarNavigationClickListener() {
        return this.mToolbarNavigationClickListener;
    }

    public boolean isDrawerIndicatorEnabled() {
        return this.mDrawerIndicatorEnabled;
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (!this.mHasCustomUpIndicator) {
            this.mHomeAsUpIndicator = getThemeUpIndicator();
        }
        syncState();
    }

    @Override // android.support.v4.widget.DrawerLayout.DrawerListener
    public void onDrawerClosed(View view) {
        this.mSlider.setPosition(0.0f);
        if (this.mDrawerIndicatorEnabled) {
            setActionBarDescription(this.mOpenDrawerContentDescRes);
        }
    }

    @Override // android.support.v4.widget.DrawerLayout.DrawerListener
    public void onDrawerOpened(View view) {
        this.mSlider.setPosition(1.0f);
        if (this.mDrawerIndicatorEnabled) {
            setActionBarDescription(this.mCloseDrawerContentDescRes);
        }
    }

    @Override // android.support.v4.widget.DrawerLayout.DrawerListener
    public void onDrawerSlide(View view, float f) {
        this.mSlider.setPosition(Math.min(1.0f, Math.max(0.0f, f)));
    }

    @Override // android.support.v4.widget.DrawerLayout.DrawerListener
    public void onDrawerStateChanged(int i) {
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem != null && menuItem.getItemId() == 16908332 && this.mDrawerIndicatorEnabled) {
            toggle();
            return true;
        }
        return false;
    }

    void setActionBarDescription(int i) {
        this.mActivityImpl.setActionBarDescription(i);
    }

    void setActionBarUpIndicator(Drawable drawable, int i) {
        this.mActivityImpl.setActionBarUpIndicator(drawable, i);
    }

    public void setDrawerIndicatorEnabled(boolean z) {
        if (z != this.mDrawerIndicatorEnabled) {
            if (z) {
                setActionBarUpIndicator((Drawable) this.mSlider, this.mDrawerLayout.isDrawerOpen(8388611) ? this.mCloseDrawerContentDescRes : this.mOpenDrawerContentDescRes);
            } else {
                setActionBarUpIndicator(this.mHomeAsUpIndicator, 0);
            }
            this.mDrawerIndicatorEnabled = z;
        }
    }

    public void setHomeAsUpIndicator(int i) {
        Drawable drawable = null;
        if (i != 0) {
            drawable = this.mDrawerLayout.getResources().getDrawable(i);
        }
        setHomeAsUpIndicator(drawable);
    }

    public void setHomeAsUpIndicator(Drawable drawable) {
        if (drawable == null) {
            this.mHomeAsUpIndicator = getThemeUpIndicator();
            this.mHasCustomUpIndicator = false;
        } else {
            this.mHomeAsUpIndicator = drawable;
            this.mHasCustomUpIndicator = true;
        }
        if (this.mDrawerIndicatorEnabled) {
            return;
        }
        setActionBarUpIndicator(this.mHomeAsUpIndicator, 0);
    }

    public void setToolbarNavigationClickListener(View.OnClickListener onClickListener) {
        this.mToolbarNavigationClickListener = onClickListener;
    }

    public void syncState() {
        if (this.mDrawerLayout.isDrawerOpen(8388611)) {
            this.mSlider.setPosition(1.0f);
        } else {
            this.mSlider.setPosition(0.0f);
        }
        if (this.mDrawerIndicatorEnabled) {
            setActionBarUpIndicator((Drawable) this.mSlider, this.mDrawerLayout.isDrawerOpen(8388611) ? this.mCloseDrawerContentDescRes : this.mOpenDrawerContentDescRes);
        }
    }
}