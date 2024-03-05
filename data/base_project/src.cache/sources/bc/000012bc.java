package android.support.v7.internal.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.appcompat.R;
import android.support.v7.internal.app.WindowCallback;
import android.support.v7.internal.view.menu.ActionMenuItem;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.ActionMenuPresenter;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.SpinnerAdapter;

/* loaded from: ToolbarWidgetWrapper.class */
public class ToolbarWidgetWrapper implements DecorToolbar {
    private static final int AFFECTS_LOGO_MASK = 3;
    private static final String TAG = "ToolbarWidgetWrapper";
    private ActionMenuPresenter mActionMenuPresenter;
    private View mCustomView;
    private int mDefaultNavigationContentDescription;
    private Drawable mDefaultNavigationIcon;
    private int mDisplayOpts;
    private CharSequence mHomeDescription;
    private Drawable mIcon;
    private Drawable mLogo;
    private boolean mMenuPrepared;
    private Drawable mNavIcon;
    private int mNavigationMode;
    private SpinnerCompat mSpinner;
    private CharSequence mSubtitle;
    private View mTabView;
    private final TintManager mTintManager;
    private CharSequence mTitle;
    private boolean mTitleSet;
    private Toolbar mToolbar;
    private WindowCallback mWindowCallback;

    public ToolbarWidgetWrapper(Toolbar toolbar, boolean z) {
        this(toolbar, z, R.string.abc_action_bar_up_description, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    }

    public ToolbarWidgetWrapper(Toolbar toolbar, boolean z, int i, int i2) {
        this.mNavigationMode = 0;
        this.mDefaultNavigationContentDescription = 0;
        this.mToolbar = toolbar;
        this.mTitle = toolbar.getTitle();
        this.mSubtitle = toolbar.getSubtitle();
        this.mTitleSet = this.mTitle != null;
        if (z) {
            TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(toolbar.getContext(), null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
            CharSequence text = obtainStyledAttributes.getText(R.styleable.ActionBar_title);
            if (!TextUtils.isEmpty(text)) {
                setTitle(text);
            }
            CharSequence text2 = obtainStyledAttributes.getText(R.styleable.ActionBar_subtitle);
            if (!TextUtils.isEmpty(text2)) {
                setSubtitle(text2);
            }
            Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.ActionBar_logo);
            if (drawable != null) {
                setLogo(drawable);
            }
            Drawable drawable2 = obtainStyledAttributes.getDrawable(R.styleable.ActionBar_icon);
            if (drawable2 != null) {
                setIcon(drawable2);
            }
            Drawable drawable3 = obtainStyledAttributes.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
            if (drawable3 != null) {
                setNavigationIcon(drawable3);
            }
            setDisplayOptions(obtainStyledAttributes.getInt(R.styleable.ActionBar_displayOptions, 0));
            int resourceId = obtainStyledAttributes.getResourceId(R.styleable.ActionBar_customNavigationLayout, 0);
            if (resourceId != 0) {
                setCustomView(LayoutInflater.from(this.mToolbar.getContext()).inflate(resourceId, (ViewGroup) this.mToolbar, false));
                setDisplayOptions(this.mDisplayOpts | 16);
            }
            int layoutDimension = obtainStyledAttributes.getLayoutDimension(R.styleable.ActionBar_height, 0);
            if (layoutDimension > 0) {
                ViewGroup.LayoutParams layoutParams = this.mToolbar.getLayoutParams();
                layoutParams.height = layoutDimension;
                this.mToolbar.setLayoutParams(layoutParams);
            }
            int dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.ActionBar_contentInsetStart, -1);
            int dimensionPixelOffset2 = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.ActionBar_contentInsetEnd, -1);
            if (dimensionPixelOffset >= 0 || dimensionPixelOffset2 >= 0) {
                this.mToolbar.setContentInsetsRelative(Math.max(dimensionPixelOffset, 0), Math.max(dimensionPixelOffset2, 0));
            }
            int resourceId2 = obtainStyledAttributes.getResourceId(R.styleable.ActionBar_titleTextStyle, 0);
            if (resourceId2 != 0) {
                Toolbar toolbar2 = this.mToolbar;
                toolbar2.setTitleTextAppearance(toolbar2.getContext(), resourceId2);
            }
            int resourceId3 = obtainStyledAttributes.getResourceId(R.styleable.ActionBar_subtitleTextStyle, 0);
            if (resourceId3 != 0) {
                Toolbar toolbar3 = this.mToolbar;
                toolbar3.setSubtitleTextAppearance(toolbar3.getContext(), resourceId3);
            }
            int resourceId4 = obtainStyledAttributes.getResourceId(R.styleable.ActionBar_popupTheme, 0);
            if (resourceId4 != 0) {
                this.mToolbar.setPopupTheme(resourceId4);
            }
            obtainStyledAttributes.recycle();
            this.mTintManager = obtainStyledAttributes.getTintManager();
        } else {
            this.mDisplayOpts = detectDisplayOptions();
            this.mTintManager = new TintManager(toolbar.getContext());
        }
        setDefaultNavigationContentDescription(i);
        this.mHomeDescription = this.mToolbar.getNavigationContentDescription();
        setDefaultNavigationIcon(this.mTintManager.getDrawable(i2));
        this.mToolbar.setNavigationOnClickListener(new View.OnClickListener(this) { // from class: android.support.v7.internal.widget.ToolbarWidgetWrapper.1
            final ActionMenuItem mNavItem;
            final ToolbarWidgetWrapper this$0;

            {
                this.this$0 = this;
                this.mNavItem = new ActionMenuItem(this.this$0.mToolbar.getContext(), 0, 16908332, 0, 0, this.this$0.mTitle);
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (this.this$0.mWindowCallback == null || !this.this$0.mMenuPrepared) {
                    return;
                }
                this.this$0.mWindowCallback.onMenuItemSelected(0, this.mNavItem);
            }
        });
    }

    private int detectDisplayOptions() {
        int i = 11;
        if (this.mToolbar.getNavigationIcon() != null) {
            i = 11 | 4;
        }
        return i;
    }

    private void ensureSpinner() {
        if (this.mSpinner == null) {
            this.mSpinner = new SpinnerCompat(getContext(), null, R.attr.actionDropDownStyle);
            this.mSpinner.setLayoutParams(new Toolbar.LayoutParams(-2, -2, 8388627));
        }
    }

    private void setTitleInt(CharSequence charSequence) {
        this.mTitle = charSequence;
        if ((this.mDisplayOpts & 8) != 0) {
            this.mToolbar.setTitle(charSequence);
        }
    }

    private void updateHomeAccessibility() {
        if ((this.mDisplayOpts & 4) != 0) {
            if (TextUtils.isEmpty(this.mHomeDescription)) {
                this.mToolbar.setNavigationContentDescription(this.mDefaultNavigationContentDescription);
            } else {
                this.mToolbar.setNavigationContentDescription(this.mHomeDescription);
            }
        }
    }

    private void updateNavigationIcon() {
        if ((this.mDisplayOpts & 4) != 0) {
            Toolbar toolbar = this.mToolbar;
            Drawable drawable = this.mNavIcon;
            if (drawable == null) {
                drawable = this.mDefaultNavigationIcon;
            }
            toolbar.setNavigationIcon(drawable);
        }
    }

    private void updateToolbarLogo() {
        Drawable drawable = null;
        int i = this.mDisplayOpts;
        if ((i & 2) != 0) {
            if ((i & 1) != 0) {
                drawable = this.mLogo;
                if (drawable == null) {
                    drawable = this.mIcon;
                }
            } else {
                drawable = this.mIcon;
            }
        }
        this.mToolbar.setLogo(drawable);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void animateToVisibility(int i) {
        if (i == 8) {
            ViewCompat.animate(this.mToolbar).alpha(0.0f).setListener(new ViewPropertyAnimatorListenerAdapter(this) { // from class: android.support.v7.internal.widget.ToolbarWidgetWrapper.2
                private boolean mCanceled = false;
                final ToolbarWidgetWrapper this$0;

                {
                    this.this$0 = this;
                }

                @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                public void onAnimationCancel(View view) {
                    this.mCanceled = true;
                }

                @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                public void onAnimationEnd(View view) {
                    if (this.mCanceled) {
                        return;
                    }
                    this.this$0.mToolbar.setVisibility(8);
                }
            });
        } else if (i == 0) {
            ViewCompat.animate(this.mToolbar).alpha(1.0f).setListener(new ViewPropertyAnimatorListenerAdapter(this) { // from class: android.support.v7.internal.widget.ToolbarWidgetWrapper.3
                final ToolbarWidgetWrapper this$0;

                {
                    this.this$0 = this;
                }

                @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
                public void onAnimationStart(View view) {
                    this.this$0.mToolbar.setVisibility(0);
                }
            });
        }
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean canShowOverflowMenu() {
        return this.mToolbar.canShowOverflowMenu();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean canSplit() {
        return false;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void collapseActionView() {
        this.mToolbar.collapseActionView();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void dismissPopupMenus() {
        this.mToolbar.dismissPopupMenus();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public Context getContext() {
        return this.mToolbar.getContext();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public View getCustomView() {
        return this.mCustomView;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public int getDisplayOptions() {
        return this.mDisplayOpts;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public int getDropdownItemCount() {
        SpinnerCompat spinnerCompat = this.mSpinner;
        return spinnerCompat != null ? spinnerCompat.getCount() : 0;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public int getDropdownSelectedPosition() {
        SpinnerCompat spinnerCompat = this.mSpinner;
        return spinnerCompat != null ? spinnerCompat.getSelectedItemPosition() : 0;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public int getNavigationMode() {
        return this.mNavigationMode;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public CharSequence getSubtitle() {
        return this.mToolbar.getSubtitle();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public CharSequence getTitle() {
        return this.mToolbar.getTitle();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public ViewGroup getViewGroup() {
        return this.mToolbar;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean hasEmbeddedTabs() {
        return this.mTabView != null;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean hasExpandedActionView() {
        return this.mToolbar.hasExpandedActionView();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean hasIcon() {
        return this.mIcon != null;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean hasLogo() {
        return this.mLogo != null;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean hideOverflowMenu() {
        return this.mToolbar.hideOverflowMenu();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void initIndeterminateProgress() {
        Log.i(TAG, "Progress display unsupported");
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void initProgress() {
        Log.i(TAG, "Progress display unsupported");
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean isOverflowMenuShowPending() {
        return this.mToolbar.isOverflowMenuShowPending();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean isOverflowMenuShowing() {
        return this.mToolbar.isOverflowMenuShowing();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean isSplit() {
        return false;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean isTitleTruncated() {
        return this.mToolbar.isTitleTruncated();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void restoreHierarchyState(SparseArray<Parcelable> sparseArray) {
        this.mToolbar.restoreHierarchyState(sparseArray);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void saveHierarchyState(SparseArray<Parcelable> sparseArray) {
        this.mToolbar.saveHierarchyState(sparseArray);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setCollapsible(boolean z) {
        this.mToolbar.setCollapsible(z);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setCustomView(View view) {
        View view2 = this.mCustomView;
        if (view2 != null && (this.mDisplayOpts & 16) != 0) {
            this.mToolbar.removeView(view2);
        }
        this.mCustomView = view;
        if (view == null || (this.mDisplayOpts & 16) == 0) {
            return;
        }
        this.mToolbar.addView(this.mCustomView);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setDefaultNavigationContentDescription(int i) {
        if (i == this.mDefaultNavigationContentDescription) {
            return;
        }
        this.mDefaultNavigationContentDescription = i;
        if (TextUtils.isEmpty(this.mToolbar.getNavigationContentDescription())) {
            setNavigationContentDescription(this.mDefaultNavigationContentDescription);
        }
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setDefaultNavigationIcon(Drawable drawable) {
        if (this.mDefaultNavigationIcon != drawable) {
            this.mDefaultNavigationIcon = drawable;
            updateNavigationIcon();
        }
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setDisplayOptions(int i) {
        View view;
        int i2 = this.mDisplayOpts ^ i;
        this.mDisplayOpts = i;
        if (i2 != 0) {
            if ((i2 & 4) != 0) {
                if ((i & 4) != 0) {
                    updateNavigationIcon();
                    updateHomeAccessibility();
                } else {
                    this.mToolbar.setNavigationIcon((Drawable) null);
                }
            }
            if ((i2 & 3) != 0) {
                updateToolbarLogo();
            }
            if ((i2 & 8) != 0) {
                if ((i & 8) != 0) {
                    this.mToolbar.setTitle(this.mTitle);
                    this.mToolbar.setSubtitle(this.mSubtitle);
                } else {
                    this.mToolbar.setTitle((CharSequence) null);
                    this.mToolbar.setSubtitle((CharSequence) null);
                }
            }
            if ((i2 & 16) == 0 || (view = this.mCustomView) == null) {
                return;
            }
            if ((i & 16) != 0) {
                this.mToolbar.addView(view);
            } else {
                this.mToolbar.removeView(view);
            }
        }
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setDropdownParams(SpinnerAdapter spinnerAdapter, AdapterViewCompat.OnItemSelectedListener onItemSelectedListener) {
        ensureSpinner();
        this.mSpinner.setAdapter(spinnerAdapter);
        this.mSpinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setDropdownSelectedPosition(int i) {
        SpinnerCompat spinnerCompat = this.mSpinner;
        if (spinnerCompat == null) {
            throw new IllegalStateException("Can't set dropdown selected position without an adapter");
        }
        spinnerCompat.setSelection(i);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setEmbeddedTabView(ScrollingTabContainerView scrollingTabContainerView) {
        View view = this.mTabView;
        if (view != null) {
            ViewParent parent = view.getParent();
            Toolbar toolbar = this.mToolbar;
            if (parent == toolbar) {
                toolbar.removeView(this.mTabView);
            }
        }
        this.mTabView = scrollingTabContainerView;
        if (scrollingTabContainerView == null || this.mNavigationMode != 2) {
            return;
        }
        this.mToolbar.addView(this.mTabView, 0);
        Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) this.mTabView.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.gravity = 8388691;
        scrollingTabContainerView.setAllowCollapse(true);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setHomeButtonEnabled(boolean z) {
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setIcon(int i) {
        setIcon(i != 0 ? this.mTintManager.getDrawable(i) : null);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        updateToolbarLogo();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setLogo(int i) {
        setLogo(i != 0 ? this.mTintManager.getDrawable(i) : null);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setLogo(Drawable drawable) {
        this.mLogo = drawable;
        updateToolbarLogo();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setMenu(Menu menu, MenuPresenter.Callback callback) {
        if (this.mActionMenuPresenter == null) {
            this.mActionMenuPresenter = new ActionMenuPresenter(this.mToolbar.getContext());
            this.mActionMenuPresenter.setId(R.id.action_menu_presenter);
        }
        this.mActionMenuPresenter.setCallback(callback);
        this.mToolbar.setMenu((MenuBuilder) menu, this.mActionMenuPresenter);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setMenuPrepared() {
        this.mMenuPrepared = true;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setNavigationContentDescription(int i) {
        setNavigationContentDescription(i == 0 ? null : getContext().getString(i));
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setNavigationContentDescription(CharSequence charSequence) {
        this.mHomeDescription = charSequence;
        updateHomeAccessibility();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setNavigationIcon(int i) {
        setNavigationIcon(i != 0 ? this.mTintManager.getDrawable(i) : null);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setNavigationIcon(Drawable drawable) {
        this.mNavIcon = drawable;
        updateNavigationIcon();
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setNavigationMode(int i) {
        int i2 = this.mNavigationMode;
        if (i != i2) {
            switch (i2) {
                case 1:
                    SpinnerCompat spinnerCompat = this.mSpinner;
                    if (spinnerCompat != null) {
                        ViewParent parent = spinnerCompat.getParent();
                        Toolbar toolbar = this.mToolbar;
                        if (parent == toolbar) {
                            toolbar.removeView(this.mSpinner);
                            break;
                        }
                    }
                    break;
                case 2:
                    View view = this.mTabView;
                    if (view != null) {
                        ViewParent parent2 = view.getParent();
                        Toolbar toolbar2 = this.mToolbar;
                        if (parent2 == toolbar2) {
                            toolbar2.removeView(this.mTabView);
                            break;
                        }
                    }
                    break;
            }
            this.mNavigationMode = i;
            switch (i) {
                case 0:
                    return;
                case 1:
                    ensureSpinner();
                    this.mToolbar.addView(this.mSpinner, 0);
                    return;
                case 2:
                    View view2 = this.mTabView;
                    if (view2 != null) {
                        this.mToolbar.addView(view2, 0);
                        Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) this.mTabView.getLayoutParams();
                        layoutParams.width = -2;
                        layoutParams.height = -2;
                        layoutParams.gravity = 8388691;
                        return;
                    }
                    return;
                default:
                    throw new IllegalArgumentException("Invalid navigation mode " + i);
            }
        }
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setSplitToolbar(boolean z) {
        if (z) {
            throw new UnsupportedOperationException("Cannot split an android.widget.Toolbar");
        }
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setSplitView(ViewGroup viewGroup) {
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setSplitWhenNarrow(boolean z) {
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setSubtitle(CharSequence charSequence) {
        this.mSubtitle = charSequence;
        if ((this.mDisplayOpts & 8) != 0) {
            this.mToolbar.setSubtitle(charSequence);
        }
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setTitle(CharSequence charSequence) {
        this.mTitleSet = true;
        setTitleInt(charSequence);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setWindowCallback(WindowCallback windowCallback) {
        this.mWindowCallback = windowCallback;
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public void setWindowTitle(CharSequence charSequence) {
        if (this.mTitleSet) {
            return;
        }
        setTitleInt(charSequence);
    }

    @Override // android.support.v7.internal.widget.DecorToolbar
    public boolean showOverflowMenu() {
        return this.mToolbar.showOverflowMenu();
    }
}