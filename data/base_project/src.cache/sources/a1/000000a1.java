package android.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import com.android.internal.R;

/* loaded from: ActionBar.class */
public abstract class ActionBar {
    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_LIST = 1;
    public static final int NAVIGATION_MODE_TABS = 2;
    public static final int DISPLAY_USE_LOGO = 1;
    public static final int DISPLAY_SHOW_HOME = 2;
    public static final int DISPLAY_HOME_AS_UP = 4;
    public static final int DISPLAY_SHOW_TITLE = 8;
    public static final int DISPLAY_SHOW_CUSTOM = 16;
    public static final int DISPLAY_TITLE_MULTIPLE_LINES = 32;

    /* loaded from: ActionBar$OnMenuVisibilityListener.class */
    public interface OnMenuVisibilityListener {
        void onMenuVisibilityChanged(boolean z);
    }

    /* loaded from: ActionBar$OnNavigationListener.class */
    public interface OnNavigationListener {
        boolean onNavigationItemSelected(int i, long j);
    }

    /* loaded from: ActionBar$Tab.class */
    public static abstract class Tab {
        public static final int INVALID_POSITION = -1;

        public abstract int getPosition();

        public abstract Drawable getIcon();

        public abstract CharSequence getText();

        public abstract Tab setIcon(Drawable drawable);

        public abstract Tab setIcon(int i);

        public abstract Tab setText(CharSequence charSequence);

        public abstract Tab setText(int i);

        public abstract Tab setCustomView(View view);

        public abstract Tab setCustomView(int i);

        public abstract View getCustomView();

        public abstract Tab setTag(Object obj);

        public abstract Object getTag();

        public abstract Tab setTabListener(TabListener tabListener);

        public abstract void select();

        public abstract Tab setContentDescription(int i);

        public abstract Tab setContentDescription(CharSequence charSequence);

        public abstract CharSequence getContentDescription();
    }

    /* loaded from: ActionBar$TabListener.class */
    public interface TabListener {
        void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction);

        void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction);

        void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction);
    }

    public abstract void setCustomView(View view);

    public abstract void setCustomView(View view, LayoutParams layoutParams);

    public abstract void setCustomView(int i);

    public abstract void setIcon(int i);

    public abstract void setIcon(Drawable drawable);

    public abstract void setLogo(int i);

    public abstract void setLogo(Drawable drawable);

    public abstract void setListNavigationCallbacks(SpinnerAdapter spinnerAdapter, OnNavigationListener onNavigationListener);

    public abstract void setSelectedNavigationItem(int i);

    public abstract int getSelectedNavigationIndex();

    public abstract int getNavigationItemCount();

    public abstract void setTitle(CharSequence charSequence);

    public abstract void setTitle(int i);

    public abstract void setSubtitle(CharSequence charSequence);

    public abstract void setSubtitle(int i);

    public abstract void setDisplayOptions(int i);

    public abstract void setDisplayOptions(int i, int i2);

    public abstract void setDisplayUseLogoEnabled(boolean z);

    public abstract void setDisplayShowHomeEnabled(boolean z);

    public abstract void setDisplayHomeAsUpEnabled(boolean z);

    public abstract void setDisplayShowTitleEnabled(boolean z);

    public abstract void setDisplayShowCustomEnabled(boolean z);

    public abstract void setBackgroundDrawable(Drawable drawable);

    public abstract View getCustomView();

    public abstract CharSequence getTitle();

    public abstract CharSequence getSubtitle();

    public abstract int getNavigationMode();

    public abstract void setNavigationMode(int i);

    public abstract int getDisplayOptions();

    public abstract Tab newTab();

    public abstract void addTab(Tab tab);

    public abstract void addTab(Tab tab, boolean z);

    public abstract void addTab(Tab tab, int i);

    public abstract void addTab(Tab tab, int i, boolean z);

    public abstract void removeTab(Tab tab);

    public abstract void removeTabAt(int i);

    public abstract void removeAllTabs();

    public abstract void selectTab(Tab tab);

    public abstract Tab getSelectedTab();

    public abstract Tab getTabAt(int i);

    public abstract int getTabCount();

    public abstract int getHeight();

    public abstract void show();

    public abstract void hide();

    public abstract boolean isShowing();

    public abstract void addOnMenuVisibilityListener(OnMenuVisibilityListener onMenuVisibilityListener);

    public abstract void removeOnMenuVisibilityListener(OnMenuVisibilityListener onMenuVisibilityListener);

    public void setStackedBackgroundDrawable(Drawable d) {
    }

    public void setSplitBackgroundDrawable(Drawable d) {
    }

    public void setHomeButtonEnabled(boolean enabled) {
    }

    public Context getThemedContext() {
        return null;
    }

    public boolean isTitleTruncated() {
        return false;
    }

    public void setHomeAsUpIndicator(Drawable indicator) {
    }

    public void setHomeAsUpIndicator(int resId) {
    }

    public void setHomeActionContentDescription(CharSequence description) {
    }

    public void setHomeActionContentDescription(int resId) {
    }

    /* loaded from: ActionBar$LayoutParams.class */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        @ViewDebug.ExportedProperty(category = "layout", mapping = {@ViewDebug.IntToString(from = -1, to = "NONE"), @ViewDebug.IntToString(from = 0, to = "NONE"), @ViewDebug.IntToString(from = 48, to = "TOP"), @ViewDebug.IntToString(from = 80, to = "BOTTOM"), @ViewDebug.IntToString(from = 3, to = "LEFT"), @ViewDebug.IntToString(from = 5, to = "RIGHT"), @ViewDebug.IntToString(from = 8388611, to = "START"), @ViewDebug.IntToString(from = 8388613, to = "END"), @ViewDebug.IntToString(from = 16, to = "CENTER_VERTICAL"), @ViewDebug.IntToString(from = 112, to = "FILL_VERTICAL"), @ViewDebug.IntToString(from = 1, to = "CENTER_HORIZONTAL"), @ViewDebug.IntToString(from = 7, to = "FILL_HORIZONTAL"), @ViewDebug.IntToString(from = 17, to = "CENTER"), @ViewDebug.IntToString(from = 119, to = "FILL")})
        public int gravity;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.gravity = 0;
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ActionBar_LayoutParams);
            this.gravity = a.getInt(0, 0);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.gravity = 0;
            this.gravity = 8388627;
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = 0;
            this.gravity = gravity;
        }

        public LayoutParams(int gravity) {
            this(-2, -1, gravity);
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.gravity = 0;
            this.gravity = source.gravity;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.gravity = 0;
        }
    }
}