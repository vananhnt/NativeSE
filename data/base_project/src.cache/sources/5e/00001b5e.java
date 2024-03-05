package com.android.internal.widget;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.CollapsibleActionView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.transition.ActionBarTransition;
import com.android.internal.view.menu.ActionMenuItem;
import com.android.internal.view.menu.ActionMenuPresenter;
import com.android.internal.view.menu.ActionMenuView;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuPresenter;
import com.android.internal.view.menu.MenuView;
import com.android.internal.view.menu.SubMenuBuilder;

/* loaded from: ActionBarView.class */
public class ActionBarView extends AbsActionBarView {
    private static final String TAG = "ActionBarView";
    public static final int DISPLAY_DEFAULT = 0;
    private static final int DISPLAY_RELAYOUT_MASK = 63;
    private static final int DEFAULT_CUSTOM_GRAVITY = 8388627;
    private int mNavigationMode;
    private int mDisplayOptions;
    private CharSequence mTitle;
    private CharSequence mSubtitle;
    private Drawable mIcon;
    private Drawable mLogo;
    private CharSequence mHomeDescription;
    private int mHomeDescriptionRes;
    private HomeView mHomeLayout;
    private HomeView mExpandedHomeLayout;
    private LinearLayout mTitleLayout;
    private TextView mTitleView;
    private TextView mSubtitleView;
    private ViewGroup mUpGoerFive;
    private Spinner mSpinner;
    private LinearLayout mListNavLayout;
    private ScrollingTabContainerView mTabScrollView;
    private View mCustomNavView;
    private ProgressBar mProgressView;
    private ProgressBar mIndeterminateProgressView;
    private int mProgressBarPadding;
    private int mItemPadding;
    private int mTitleStyleRes;
    private int mSubtitleStyleRes;
    private int mProgressStyle;
    private int mIndeterminateProgressStyle;
    private boolean mUserTitle;
    private boolean mIncludeTabs;
    private boolean mIsCollapsable;
    private boolean mIsCollapsed;
    private boolean mWasHomeEnabled;
    private MenuBuilder mOptionsMenu;
    private boolean mMenuPrepared;
    private ActionBarContextView mContextView;
    private ActionMenuItem mLogoNavItem;
    private SpinnerAdapter mSpinnerAdapter;
    private ActionBar.OnNavigationListener mCallback;
    private Runnable mTabSelector;
    private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    View mExpandedActionView;
    Window.Callback mWindowCallback;
    private final AdapterView.OnItemSelectedListener mNavItemSelectedListener;
    private final View.OnClickListener mExpandedActionViewUpListener;
    private final View.OnClickListener mUpClickListener;

    public ActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDisplayOptions = -1;
        this.mNavItemSelectedListener = new AdapterView.OnItemSelectedListener() { // from class: com.android.internal.widget.ActionBarView.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                if (ActionBarView.this.mCallback != null) {
                    ActionBarView.this.mCallback.onNavigationItemSelected(position, id);
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView parent) {
            }
        };
        this.mExpandedActionViewUpListener = new View.OnClickListener() { // from class: com.android.internal.widget.ActionBarView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                MenuItemImpl item = ActionBarView.this.mExpandedMenuPresenter.mCurrentExpandedItem;
                if (item != null) {
                    item.collapseActionView();
                }
            }
        };
        this.mUpClickListener = new View.OnClickListener() { // from class: com.android.internal.widget.ActionBarView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (ActionBarView.this.mMenuPrepared) {
                    ActionBarView.this.mWindowCallback.onMenuItemSelected(0, ActionBarView.this.mLogoNavItem);
                }
            }
        };
        setBackgroundResource(0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionBar, 16843470, 0);
        context.getApplicationInfo();
        context.getPackageManager();
        this.mNavigationMode = a.getInt(7, 0);
        this.mTitle = a.getText(5);
        this.mSubtitle = a.getText(9);
        this.mLogo = a.getDrawable(6);
        this.mIcon = a.getDrawable(0);
        LayoutInflater inflater = LayoutInflater.from(context);
        int homeResId = a.getResourceId(15, R.layout.action_bar_home);
        this.mUpGoerFive = (ViewGroup) inflater.inflate(R.layout.action_bar_up_container, (ViewGroup) this, false);
        this.mHomeLayout = (HomeView) inflater.inflate(homeResId, this.mUpGoerFive, false);
        this.mExpandedHomeLayout = (HomeView) inflater.inflate(homeResId, this.mUpGoerFive, false);
        this.mExpandedHomeLayout.setShowUp(true);
        this.mExpandedHomeLayout.setOnClickListener(this.mExpandedActionViewUpListener);
        this.mExpandedHomeLayout.setContentDescription(getResources().getText(R.string.action_bar_up_description));
        Drawable upBackground = this.mUpGoerFive.getBackground();
        if (upBackground != null) {
            this.mExpandedHomeLayout.setBackground(upBackground.getConstantState().newDrawable());
        }
        this.mExpandedHomeLayout.setEnabled(true);
        this.mExpandedHomeLayout.setFocusable(true);
        this.mTitleStyleRes = a.getResourceId(11, 0);
        this.mSubtitleStyleRes = a.getResourceId(12, 0);
        this.mProgressStyle = a.getResourceId(1, 0);
        this.mIndeterminateProgressStyle = a.getResourceId(13, 0);
        this.mProgressBarPadding = a.getDimensionPixelOffset(14, 0);
        this.mItemPadding = a.getDimensionPixelOffset(16, 0);
        setDisplayOptions(a.getInt(8, 0));
        int customNavId = a.getResourceId(10, 0);
        if (customNavId != 0) {
            this.mCustomNavView = inflater.inflate(customNavId, (ViewGroup) this, false);
            this.mNavigationMode = 0;
            setDisplayOptions(this.mDisplayOptions | 16);
        }
        this.mContentHeight = a.getLayoutDimension(4, 0);
        a.recycle();
        this.mLogoNavItem = new ActionMenuItem(context, 0, 16908332, 0, 0, this.mTitle);
        this.mUpGoerFive.setOnClickListener(this.mUpClickListener);
        this.mUpGoerFive.setClickable(true);
        this.mUpGoerFive.setFocusable(true);
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.widget.AbsActionBarView, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mTitleView = null;
        this.mSubtitleView = null;
        if (this.mTitleLayout != null && this.mTitleLayout.getParent() == this.mUpGoerFive) {
            this.mUpGoerFive.removeView(this.mTitleLayout);
        }
        this.mTitleLayout = null;
        if ((this.mDisplayOptions & 8) != 0) {
            initTitle();
        }
        if (this.mHomeDescriptionRes != 0) {
            setHomeActionContentDescription(this.mHomeDescriptionRes);
        }
        if (this.mTabScrollView != null && this.mIncludeTabs) {
            ViewGroup.LayoutParams lp = this.mTabScrollView.getLayoutParams();
            if (lp != null) {
                lp.width = -2;
                lp.height = -1;
            }
            this.mTabScrollView.setAllowCollapse(true);
        }
    }

    public void setWindowCallback(Window.Callback cb) {
        this.mWindowCallback = cb;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mTabSelector);
        if (this.mActionMenuPresenter != null) {
            this.mActionMenuPresenter.hideOverflowMenu();
            this.mActionMenuPresenter.hideSubMenus();
        }
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public void initProgress() {
        this.mProgressView = new ProgressBar(this.mContext, null, 0, this.mProgressStyle);
        this.mProgressView.setId(R.id.progress_horizontal);
        this.mProgressView.setMax(10000);
        this.mProgressView.setVisibility(8);
        addView(this.mProgressView);
    }

    public void initIndeterminateProgress() {
        this.mIndeterminateProgressView = new ProgressBar(this.mContext, null, 0, this.mIndeterminateProgressStyle);
        this.mIndeterminateProgressView.setId(R.id.progress_circular);
        this.mIndeterminateProgressView.setVisibility(8);
        addView(this.mIndeterminateProgressView);
    }

    @Override // com.android.internal.widget.AbsActionBarView
    public void setSplitActionBar(boolean splitActionBar) {
        if (this.mSplitActionBar != splitActionBar) {
            if (this.mMenuView != null) {
                ViewGroup oldParent = (ViewGroup) this.mMenuView.getParent();
                if (oldParent != null) {
                    oldParent.removeView(this.mMenuView);
                }
                if (splitActionBar) {
                    if (this.mSplitView != null) {
                        this.mSplitView.addView(this.mMenuView);
                    }
                    this.mMenuView.getLayoutParams().width = -1;
                } else {
                    addView(this.mMenuView);
                    this.mMenuView.getLayoutParams().width = -2;
                }
                this.mMenuView.requestLayout();
            }
            if (this.mSplitView != null) {
                this.mSplitView.setVisibility(splitActionBar ? 0 : 8);
            }
            if (this.mActionMenuPresenter != null) {
                if (!splitActionBar) {
                    this.mActionMenuPresenter.setExpandedActionViewsExclusive(getResources().getBoolean(R.bool.action_bar_expanded_action_views_exclusive));
                } else {
                    this.mActionMenuPresenter.setExpandedActionViewsExclusive(false);
                    this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
                    this.mActionMenuPresenter.setItemLimit(Integer.MAX_VALUE);
                }
            }
            super.setSplitActionBar(splitActionBar);
        }
    }

    public boolean isSplitActionBar() {
        return this.mSplitActionBar;
    }

    public boolean hasEmbeddedTabs() {
        return this.mIncludeTabs;
    }

    public void setEmbeddedTabView(ScrollingTabContainerView tabs) {
        if (this.mTabScrollView != null) {
            removeView(this.mTabScrollView);
        }
        this.mTabScrollView = tabs;
        this.mIncludeTabs = tabs != null;
        if (this.mIncludeTabs && this.mNavigationMode == 2) {
            addView(this.mTabScrollView);
            ViewGroup.LayoutParams lp = this.mTabScrollView.getLayoutParams();
            lp.width = -2;
            lp.height = -1;
            tabs.setAllowCollapse(true);
        }
    }

    public void setCallback(ActionBar.OnNavigationListener callback) {
        this.mCallback = callback;
    }

    public void setMenuPrepared() {
        this.mMenuPrepared = true;
    }

    public void setMenu(Menu menu, MenuPresenter.Callback cb) {
        View view;
        ViewGroup oldParent;
        if (menu == this.mOptionsMenu) {
            return;
        }
        if (this.mOptionsMenu != null) {
            this.mOptionsMenu.removeMenuPresenter(this.mActionMenuPresenter);
            this.mOptionsMenu.removeMenuPresenter(this.mExpandedMenuPresenter);
        }
        MenuBuilder builder = (MenuBuilder) menu;
        this.mOptionsMenu = builder;
        if (this.mMenuView != null && (oldParent = (ViewGroup) this.mMenuView.getParent()) != null) {
            oldParent.removeView(this.mMenuView);
        }
        if (this.mActionMenuPresenter == null) {
            this.mActionMenuPresenter = new ActionMenuPresenter(this.mContext);
            this.mActionMenuPresenter.setCallback(cb);
            this.mActionMenuPresenter.setId((int) R.id.action_menu_presenter);
            this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
        }
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -1);
        if (!this.mSplitActionBar) {
            this.mActionMenuPresenter.setExpandedActionViewsExclusive(getResources().getBoolean(R.bool.action_bar_expanded_action_views_exclusive));
            configPresenters(builder);
            view = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
            ViewGroup oldParent2 = (ViewGroup) view.getParent();
            if (oldParent2 != null && oldParent2 != this) {
                oldParent2.removeView(view);
            }
            addView(view, layoutParams);
        } else {
            this.mActionMenuPresenter.setExpandedActionViewsExclusive(false);
            this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
            this.mActionMenuPresenter.setItemLimit(Integer.MAX_VALUE);
            layoutParams.width = -1;
            configPresenters(builder);
            view = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
            if (this.mSplitView != null) {
                ViewGroup oldParent3 = (ViewGroup) view.getParent();
                if (oldParent3 != null && oldParent3 != this.mSplitView) {
                    oldParent3.removeView(view);
                }
                view.setVisibility(getAnimatedVisibility());
                this.mSplitView.addView(view, layoutParams);
            } else {
                view.setLayoutParams(layoutParams);
            }
        }
        this.mMenuView = view;
    }

    private void configPresenters(MenuBuilder builder) {
        if (builder != null) {
            builder.addMenuPresenter(this.mActionMenuPresenter);
            builder.addMenuPresenter(this.mExpandedMenuPresenter);
            return;
        }
        this.mActionMenuPresenter.initForMenu(this.mContext, (MenuBuilder) null);
        this.mExpandedMenuPresenter.initForMenu(this.mContext, null);
        this.mActionMenuPresenter.updateMenuView(true);
        this.mExpandedMenuPresenter.updateMenuView(true);
    }

    public boolean hasExpandedActionView() {
        return (this.mExpandedMenuPresenter == null || this.mExpandedMenuPresenter.mCurrentExpandedItem == null) ? false : true;
    }

    public void collapseActionView() {
        MenuItemImpl item = this.mExpandedMenuPresenter == null ? null : this.mExpandedMenuPresenter.mCurrentExpandedItem;
        if (item != null) {
            item.collapseActionView();
        }
    }

    public void setCustomNavigationView(View view) {
        boolean showCustom = (this.mDisplayOptions & 16) != 0;
        if (showCustom) {
            ActionBarTransition.beginDelayedTransition(this);
        }
        if (this.mCustomNavView != null && showCustom) {
            removeView(this.mCustomNavView);
        }
        this.mCustomNavView = view;
        if (this.mCustomNavView != null && showCustom) {
            addView(this.mCustomNavView);
        }
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public void setTitle(CharSequence title) {
        this.mUserTitle = true;
        setTitleImpl(title);
    }

    public void setWindowTitle(CharSequence title) {
        if (!this.mUserTitle) {
            setTitleImpl(title);
        }
    }

    private void setTitleImpl(CharSequence title) {
        ActionBarTransition.beginDelayedTransition(this);
        this.mTitle = title;
        if (this.mTitleView != null) {
            this.mTitleView.setText(title);
            boolean visible = (this.mExpandedActionView != null || (this.mDisplayOptions & 8) == 0 || (TextUtils.isEmpty(this.mTitle) && TextUtils.isEmpty(this.mSubtitle))) ? false : true;
            this.mTitleLayout.setVisibility(visible ? 0 : 8);
        }
        if (this.mLogoNavItem != null) {
            this.mLogoNavItem.setTitle(title);
        }
        this.mUpGoerFive.setContentDescription(buildHomeContentDescription());
    }

    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }

    public void setSubtitle(CharSequence subtitle) {
        ActionBarTransition.beginDelayedTransition(this);
        this.mSubtitle = subtitle;
        if (this.mSubtitleView != null) {
            this.mSubtitleView.setText(subtitle);
            this.mSubtitleView.setVisibility(subtitle != null ? 0 : 8);
            boolean visible = (this.mExpandedActionView != null || (this.mDisplayOptions & 8) == 0 || (TextUtils.isEmpty(this.mTitle) && TextUtils.isEmpty(this.mSubtitle))) ? false : true;
            this.mTitleLayout.setVisibility(visible ? 0 : 8);
        }
        this.mUpGoerFive.setContentDescription(buildHomeContentDescription());
    }

    public void setHomeButtonEnabled(boolean enable) {
        setHomeButtonEnabled(enable, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHomeButtonEnabled(boolean enable, boolean recordState) {
        if (recordState) {
            this.mWasHomeEnabled = enable;
        }
        if (this.mExpandedActionView != null) {
            return;
        }
        this.mUpGoerFive.setEnabled(enable);
        this.mUpGoerFive.setFocusable(enable);
        if (!enable) {
            this.mUpGoerFive.setContentDescription(null);
            this.mUpGoerFive.setImportantForAccessibility(2);
            return;
        }
        this.mUpGoerFive.setImportantForAccessibility(0);
        this.mUpGoerFive.setContentDescription(buildHomeContentDescription());
    }

    private CharSequence buildHomeContentDescription() {
        CharSequence homeDesc;
        String result;
        if (this.mHomeDescription != null) {
            homeDesc = this.mHomeDescription;
        } else if ((this.mDisplayOptions & 4) != 0) {
            homeDesc = this.mContext.getResources().getText(R.string.action_bar_up_description);
        } else {
            homeDesc = this.mContext.getResources().getText(R.string.action_bar_home_description);
        }
        CharSequence title = getTitle();
        CharSequence subtitle = getSubtitle();
        if (!TextUtils.isEmpty(title)) {
            if (!TextUtils.isEmpty(subtitle)) {
                result = getResources().getString(R.string.action_bar_home_subtitle_description_format, title, subtitle, homeDesc);
            } else {
                result = getResources().getString(R.string.action_bar_home_description_format, title, homeDesc);
            }
            return result;
        }
        return homeDesc;
    }

    public void setDisplayOptions(int options) {
        int flagsChanged = this.mDisplayOptions == -1 ? -1 : options ^ this.mDisplayOptions;
        this.mDisplayOptions = options;
        if ((flagsChanged & 63) != 0) {
            ActionBarTransition.beginDelayedTransition(this);
            if ((flagsChanged & 4) != 0) {
                boolean setUp = (options & 4) != 0;
                this.mHomeLayout.setShowUp(setUp);
                if (setUp) {
                    setHomeButtonEnabled(true);
                }
            }
            if ((flagsChanged & 1) != 0) {
                boolean logoVis = (this.mLogo == null || (options & 1) == 0) ? false : true;
                this.mHomeLayout.setIcon(logoVis ? this.mLogo : this.mIcon);
            }
            if ((flagsChanged & 8) != 0) {
                if ((options & 8) != 0) {
                    initTitle();
                } else {
                    this.mUpGoerFive.removeView(this.mTitleLayout);
                }
            }
            boolean showHome = (options & 2) != 0;
            boolean homeAsUp = (this.mDisplayOptions & 4) != 0;
            boolean titleUp = !showHome && homeAsUp;
            this.mHomeLayout.setShowIcon(showHome);
            int homeVis = ((showHome || titleUp) && this.mExpandedActionView == null) ? 0 : 8;
            this.mHomeLayout.setVisibility(homeVis);
            if ((flagsChanged & 16) != 0 && this.mCustomNavView != null) {
                if ((options & 16) != 0) {
                    addView(this.mCustomNavView);
                } else {
                    removeView(this.mCustomNavView);
                }
            }
            if (this.mTitleLayout != null && (flagsChanged & 32) != 0) {
                if ((options & 32) != 0) {
                    this.mTitleView.setSingleLine(false);
                    this.mTitleView.setMaxLines(2);
                } else {
                    this.mTitleView.setMaxLines(1);
                    this.mTitleView.setSingleLine(true);
                }
            }
            requestLayout();
        } else {
            invalidate();
        }
        if (!this.mHomeLayout.isEnabled()) {
            this.mHomeLayout.setContentDescription(null);
            this.mHomeLayout.setImportantForAccessibility(2);
            return;
        }
        this.mHomeLayout.setImportantForAccessibility(0);
        if ((options & 4) != 0) {
            this.mHomeLayout.setContentDescription(this.mContext.getResources().getText(R.string.action_bar_up_description));
        } else {
            this.mHomeLayout.setContentDescription(this.mContext.getResources().getText(R.string.action_bar_home_description));
        }
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
        if (icon != null && ((this.mDisplayOptions & 1) == 0 || this.mLogo == null)) {
            this.mHomeLayout.setIcon(icon);
        }
        if (this.mExpandedActionView != null) {
            this.mExpandedHomeLayout.setIcon(this.mIcon.getConstantState().newDrawable(getResources()));
        }
    }

    public void setIcon(int resId) {
        setIcon(resId != 0 ? this.mContext.getResources().getDrawable(resId) : null);
    }

    public boolean hasIcon() {
        return this.mIcon != null;
    }

    public void setLogo(Drawable logo) {
        this.mLogo = logo;
        if (logo != null && (this.mDisplayOptions & 1) != 0) {
            this.mHomeLayout.setIcon(logo);
        }
    }

    public void setLogo(int resId) {
        setLogo(resId != 0 ? this.mContext.getResources().getDrawable(resId) : null);
    }

    public boolean hasLogo() {
        return this.mLogo != null;
    }

    public void setNavigationMode(int mode) {
        int oldMode = this.mNavigationMode;
        if (mode != oldMode) {
            ActionBarTransition.beginDelayedTransition(this);
            switch (oldMode) {
                case 1:
                    if (this.mListNavLayout != null) {
                        removeView(this.mListNavLayout);
                        break;
                    }
                    break;
                case 2:
                    if (this.mTabScrollView != null && this.mIncludeTabs) {
                        removeView(this.mTabScrollView);
                        break;
                    }
                    break;
            }
            switch (mode) {
                case 1:
                    if (this.mSpinner == null) {
                        this.mSpinner = new Spinner(this.mContext, null, 16843479);
                        this.mSpinner.setId(R.id.action_bar_spinner);
                        this.mListNavLayout = new LinearLayout(this.mContext, null, 16843508);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -1);
                        params.gravity = 17;
                        this.mListNavLayout.addView(this.mSpinner, params);
                    }
                    if (this.mSpinner.getAdapter() != this.mSpinnerAdapter) {
                        this.mSpinner.setAdapter(this.mSpinnerAdapter);
                    }
                    this.mSpinner.setOnItemSelectedListener(this.mNavItemSelectedListener);
                    addView(this.mListNavLayout);
                    break;
                case 2:
                    if (this.mTabScrollView != null && this.mIncludeTabs) {
                        addView(this.mTabScrollView);
                        break;
                    }
                    break;
            }
            this.mNavigationMode = mode;
            requestLayout();
        }
    }

    public void setDropdownAdapter(SpinnerAdapter adapter) {
        this.mSpinnerAdapter = adapter;
        if (this.mSpinner != null) {
            this.mSpinner.setAdapter(adapter);
        }
    }

    public SpinnerAdapter getDropdownAdapter() {
        return this.mSpinnerAdapter;
    }

    public void setDropdownSelectedPosition(int position) {
        this.mSpinner.setSelection(position);
    }

    public int getDropdownSelectedPosition() {
        return this.mSpinner.getSelectedItemPosition();
    }

    public View getCustomNavigationView() {
        return this.mCustomNavView;
    }

    public int getNavigationMode() {
        return this.mNavigationMode;
    }

    public int getDisplayOptions() {
        return this.mDisplayOptions;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ActionBar.LayoutParams((int) DEFAULT_CUSTOM_GRAVITY);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        ViewParent parent;
        super.onFinishInflate();
        this.mUpGoerFive.addView(this.mHomeLayout, 0);
        addView(this.mUpGoerFive);
        if (this.mCustomNavView != null && (this.mDisplayOptions & 16) != 0 && (parent = this.mCustomNavView.getParent()) != this) {
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.mCustomNavView);
            }
            addView(this.mCustomNavView);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initTitle() {
        if (this.mTitleLayout == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            this.mTitleLayout = (LinearLayout) inflater.inflate(R.layout.action_bar_title_item, (ViewGroup) this, false);
            this.mTitleView = (TextView) this.mTitleLayout.findViewById(R.id.action_bar_title);
            this.mSubtitleView = (TextView) this.mTitleLayout.findViewById(R.id.action_bar_subtitle);
            if (this.mTitleStyleRes != 0) {
                this.mTitleView.setTextAppearance(this.mContext, this.mTitleStyleRes);
            }
            if (this.mTitle != null) {
                this.mTitleView.setText(this.mTitle);
            }
            if (this.mSubtitleStyleRes != 0) {
                this.mSubtitleView.setTextAppearance(this.mContext, this.mSubtitleStyleRes);
            }
            if (this.mSubtitle != null) {
                this.mSubtitleView.setText(this.mSubtitle);
                this.mSubtitleView.setVisibility(0);
            }
        }
        ActionBarTransition.beginDelayedTransition(this);
        this.mUpGoerFive.addView(this.mTitleLayout);
        if (this.mExpandedActionView != null || (TextUtils.isEmpty(this.mTitle) && TextUtils.isEmpty(this.mSubtitle))) {
            this.mTitleLayout.setVisibility(8);
        } else {
            this.mTitleLayout.setVisibility(0);
        }
    }

    public void setContextView(ActionBarContextView view) {
        this.mContextView = view;
    }

    public void setCollapsable(boolean collapsable) {
        this.mIsCollapsable = collapsable;
    }

    public boolean isCollapsed() {
        return this.mIsCollapsed;
    }

    public boolean isTitleTruncated() {
        Layout titleLayout;
        if (this.mTitleView == null || (titleLayout = this.mTitleView.getLayout()) == null) {
            return false;
        }
        int lineCount = titleLayout.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            if (titleLayout.getEllipsisCount(i) > 0) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int homeWidthSpec;
        int customNavHeightMode;
        int childCount = getChildCount();
        if (this.mIsCollapsable) {
            int visibleChildren = 0;
            for (int i = 0; i < childCount; i++) {
                ActionMenuView childAt = getChildAt(i);
                if (childAt.getVisibility() != 8 && ((childAt != this.mMenuView || this.mMenuView.getChildCount() != 0) && childAt != this.mUpGoerFive)) {
                    visibleChildren++;
                }
            }
            int upChildCount = this.mUpGoerFive.getChildCount();
            for (int i2 = 0; i2 < upChildCount; i2++) {
                View child = this.mUpGoerFive.getChildAt(i2);
                if (child.getVisibility() != 8) {
                    visibleChildren++;
                }
            }
            if (visibleChildren == 0) {
                setMeasuredDimension(0, 0);
                this.mIsCollapsed = true;
                return;
            }
        }
        this.mIsCollapsed = false;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != 1073741824) {
            throw new IllegalStateException(getClass().getSimpleName() + " can only be used with android:layout_width=\"match_parent\" (or fill_parent)");
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != Integer.MIN_VALUE) {
            throw new IllegalStateException(getClass().getSimpleName() + " can only be used with android:layout_height=\"wrap_content\"");
        }
        int contentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = this.mContentHeight >= 0 ? this.mContentHeight : View.MeasureSpec.getSize(heightMeasureSpec);
        int verticalPadding = getPaddingTop() + getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int height = maxHeight - verticalPadding;
        int childSpecHeight = View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE);
        int exactHeightSpec = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
        int availableWidth = (contentWidth - paddingLeft) - paddingRight;
        int leftOfCenter = availableWidth / 2;
        int rightOfCenter = leftOfCenter;
        boolean showTitle = (this.mTitleLayout == null || this.mTitleLayout.getVisibility() == 8 || (this.mDisplayOptions & 8) == 0) ? false : true;
        HomeView homeLayout = this.mExpandedActionView != null ? this.mExpandedHomeLayout : this.mHomeLayout;
        ViewGroup.LayoutParams homeLp = homeLayout.getLayoutParams();
        if (homeLp.width < 0) {
            homeWidthSpec = View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE);
        } else {
            homeWidthSpec = View.MeasureSpec.makeMeasureSpec(homeLp.width, 1073741824);
        }
        homeLayout.measure(homeWidthSpec, exactHeightSpec);
        int homeWidth = 0;
        if ((homeLayout.getVisibility() != 8 && homeLayout.getParent() == this.mUpGoerFive) || showTitle) {
            homeWidth = homeLayout.getMeasuredWidth();
            int homeOffsetWidth = homeWidth + homeLayout.getStartOffset();
            availableWidth = Math.max(0, availableWidth - homeOffsetWidth);
            leftOfCenter = Math.max(0, availableWidth - homeOffsetWidth);
        }
        if (this.mMenuView != null && this.mMenuView.getParent() == this) {
            availableWidth = measureChildView(this.mMenuView, availableWidth, exactHeightSpec, 0);
            rightOfCenter = Math.max(0, rightOfCenter - this.mMenuView.getMeasuredWidth());
        }
        if (this.mIndeterminateProgressView != null && this.mIndeterminateProgressView.getVisibility() != 8) {
            availableWidth = measureChildView(this.mIndeterminateProgressView, availableWidth, childSpecHeight, 0);
            rightOfCenter = Math.max(0, rightOfCenter - this.mIndeterminateProgressView.getMeasuredWidth());
        }
        if (this.mExpandedActionView == null) {
            switch (this.mNavigationMode) {
                case 1:
                    if (this.mListNavLayout != null) {
                        int itemPaddingSize = showTitle ? this.mItemPadding * 2 : this.mItemPadding;
                        int availableWidth2 = Math.max(0, availableWidth - itemPaddingSize);
                        int leftOfCenter2 = Math.max(0, leftOfCenter - itemPaddingSize);
                        this.mListNavLayout.measure(View.MeasureSpec.makeMeasureSpec(availableWidth2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
                        int listNavWidth = this.mListNavLayout.getMeasuredWidth();
                        availableWidth = Math.max(0, availableWidth2 - listNavWidth);
                        leftOfCenter = Math.max(0, leftOfCenter2 - listNavWidth);
                        break;
                    }
                    break;
                case 2:
                    if (this.mTabScrollView != null) {
                        int itemPaddingSize2 = showTitle ? this.mItemPadding * 2 : this.mItemPadding;
                        int availableWidth3 = Math.max(0, availableWidth - itemPaddingSize2);
                        int leftOfCenter3 = Math.max(0, leftOfCenter - itemPaddingSize2);
                        this.mTabScrollView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
                        int tabWidth = this.mTabScrollView.getMeasuredWidth();
                        availableWidth = Math.max(0, availableWidth3 - tabWidth);
                        leftOfCenter = Math.max(0, leftOfCenter3 - tabWidth);
                        break;
                    }
                    break;
            }
        }
        View customView = null;
        if (this.mExpandedActionView != null) {
            customView = this.mExpandedActionView;
        } else if ((this.mDisplayOptions & 16) != 0 && this.mCustomNavView != null) {
            customView = this.mCustomNavView;
        }
        if (customView != null) {
            ViewGroup.LayoutParams lp = generateLayoutParams(customView.getLayoutParams());
            ActionBar.LayoutParams ablp = lp instanceof ActionBar.LayoutParams ? (ActionBar.LayoutParams) lp : null;
            int horizontalMargin = 0;
            int verticalMargin = 0;
            if (ablp != null) {
                horizontalMargin = ablp.leftMargin + ablp.rightMargin;
                verticalMargin = ablp.topMargin + ablp.bottomMargin;
            }
            if (this.mContentHeight <= 0) {
                customNavHeightMode = Integer.MIN_VALUE;
            } else {
                customNavHeightMode = lp.height != -2 ? 1073741824 : Integer.MIN_VALUE;
            }
            int customNavHeight = Math.max(0, (lp.height >= 0 ? Math.min(lp.height, height) : height) - verticalMargin);
            int customNavWidthMode = lp.width != -2 ? 1073741824 : Integer.MIN_VALUE;
            int customNavWidth = Math.max(0, (lp.width >= 0 ? Math.min(lp.width, availableWidth) : availableWidth) - horizontalMargin);
            int hgrav = (ablp != null ? ablp.gravity : DEFAULT_CUSTOM_GRAVITY) & 7;
            if (hgrav == 1 && lp.width == -1) {
                customNavWidth = Math.min(leftOfCenter, rightOfCenter) * 2;
            }
            customView.measure(View.MeasureSpec.makeMeasureSpec(customNavWidth, customNavWidthMode), View.MeasureSpec.makeMeasureSpec(customNavHeight, customNavHeightMode));
            availableWidth -= horizontalMargin + customView.getMeasuredWidth();
        }
        measureChildView(this.mUpGoerFive, availableWidth + homeWidth, View.MeasureSpec.makeMeasureSpec(this.mContentHeight, 1073741824), 0);
        if (this.mTitleLayout != null) {
            Math.max(0, leftOfCenter - this.mTitleLayout.getMeasuredWidth());
        }
        if (this.mContentHeight <= 0) {
            int measuredHeight = 0;
            for (int i3 = 0; i3 < childCount; i3++) {
                View v = getChildAt(i3);
                int paddedViewHeight = v.getMeasuredHeight() + verticalPadding;
                if (paddedViewHeight > measuredHeight) {
                    measuredHeight = paddedViewHeight;
                }
            }
            setMeasuredDimension(contentWidth, measuredHeight);
        } else {
            setMeasuredDimension(contentWidth, maxHeight);
        }
        if (this.mContextView != null) {
            this.mContextView.setContentHeight(getMeasuredHeight());
        }
        if (this.mProgressView != null && this.mProgressView.getVisibility() != 8) {
            this.mProgressView.measure(View.MeasureSpec.makeMeasureSpec(contentWidth - (this.mProgressBarPadding * 2), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int contentHeight = ((b - t) - getPaddingTop()) - getPaddingBottom();
        if (contentHeight <= 0) {
            return;
        }
        boolean isLayoutRtl = isLayoutRtl();
        int direction = isLayoutRtl ? 1 : -1;
        int menuStart = isLayoutRtl ? getPaddingLeft() : (r - l) - getPaddingRight();
        int x = isLayoutRtl ? (r - l) - getPaddingRight() : getPaddingLeft();
        int y = getPaddingTop();
        HomeView homeLayout = this.mExpandedActionView != null ? this.mExpandedHomeLayout : this.mHomeLayout;
        boolean showTitle = (this.mTitleLayout == null || this.mTitleLayout.getVisibility() == 8 || (this.mDisplayOptions & 8) == 0) ? false : true;
        int startOffset = 0;
        if (homeLayout.getParent() == this.mUpGoerFive) {
            if (homeLayout.getVisibility() != 8) {
                startOffset = homeLayout.getStartOffset();
            } else if (showTitle) {
                startOffset = homeLayout.getUpWidth();
            }
        }
        int x2 = next(x + positionChild(this.mUpGoerFive, next(x, startOffset, isLayoutRtl), y, contentHeight, isLayoutRtl), startOffset, isLayoutRtl);
        if (this.mExpandedActionView == null) {
            switch (this.mNavigationMode) {
                case 1:
                    if (this.mListNavLayout != null) {
                        if (showTitle) {
                            x2 = next(x2, this.mItemPadding, isLayoutRtl);
                        }
                        x2 = next(x2 + positionChild(this.mListNavLayout, x2, y, contentHeight, isLayoutRtl), this.mItemPadding, isLayoutRtl);
                        break;
                    }
                    break;
                case 2:
                    if (this.mTabScrollView != null) {
                        if (showTitle) {
                            x2 = next(x2, this.mItemPadding, isLayoutRtl);
                        }
                        x2 = next(x2 + positionChild(this.mTabScrollView, x2, y, contentHeight, isLayoutRtl), this.mItemPadding, isLayoutRtl);
                        break;
                    }
                    break;
            }
        }
        if (this.mMenuView != null && this.mMenuView.getParent() == this) {
            positionChild(this.mMenuView, menuStart, y, contentHeight, !isLayoutRtl);
            menuStart += direction * this.mMenuView.getMeasuredWidth();
        }
        if (this.mIndeterminateProgressView != null && this.mIndeterminateProgressView.getVisibility() != 8) {
            positionChild(this.mIndeterminateProgressView, menuStart, y, contentHeight, !isLayoutRtl);
            menuStart += direction * this.mIndeterminateProgressView.getMeasuredWidth();
        }
        View customView = null;
        if (this.mExpandedActionView != null) {
            customView = this.mExpandedActionView;
        } else if ((this.mDisplayOptions & 16) != 0 && this.mCustomNavView != null) {
            customView = this.mCustomNavView;
        }
        if (customView != null) {
            int layoutDirection = getLayoutDirection();
            ViewGroup.LayoutParams lp = customView.getLayoutParams();
            ActionBar.LayoutParams ablp = lp instanceof ActionBar.LayoutParams ? (ActionBar.LayoutParams) lp : null;
            int gravity = ablp != null ? ablp.gravity : DEFAULT_CUSTOM_GRAVITY;
            int navWidth = customView.getMeasuredWidth();
            int topMargin = 0;
            int bottomMargin = 0;
            if (ablp != null) {
                x2 = next(x2, ablp.getMarginStart(), isLayoutRtl);
                menuStart += direction * ablp.getMarginEnd();
                topMargin = ablp.topMargin;
                bottomMargin = ablp.bottomMargin;
            }
            int hgravity = gravity & 8388615;
            if (hgravity == 1) {
                int centeredLeft = ((this.mRight - this.mLeft) - navWidth) / 2;
                if (isLayoutRtl) {
                    int centeredStart = centeredLeft + navWidth;
                    if (centeredStart > x2) {
                        hgravity = 5;
                    } else if (centeredLeft < menuStart) {
                        hgravity = 3;
                    }
                } else {
                    int centeredEnd = centeredLeft + navWidth;
                    if (centeredLeft < x2) {
                        hgravity = 3;
                    } else if (centeredEnd > menuStart) {
                        hgravity = 5;
                    }
                }
            } else if (gravity == 0) {
                hgravity = 8388611;
            }
            int xpos = 0;
            switch (Gravity.getAbsoluteGravity(hgravity, layoutDirection)) {
                case 1:
                    xpos = ((this.mRight - this.mLeft) - navWidth) / 2;
                    break;
                case 3:
                    xpos = isLayoutRtl ? menuStart : x2;
                    break;
                case 5:
                    xpos = isLayoutRtl ? x2 - navWidth : menuStart - navWidth;
                    break;
            }
            int vgravity = gravity & 112;
            if (gravity == 0) {
                vgravity = 16;
            }
            int ypos = 0;
            switch (vgravity) {
                case 16:
                    int paddedTop = getPaddingTop();
                    int paddedBottom = (this.mBottom - this.mTop) - getPaddingBottom();
                    ypos = ((paddedBottom - paddedTop) - customView.getMeasuredHeight()) / 2;
                    break;
                case 48:
                    ypos = getPaddingTop() + topMargin;
                    break;
                case 80:
                    ypos = ((getHeight() - getPaddingBottom()) - customView.getMeasuredHeight()) - bottomMargin;
                    break;
            }
            int customWidth = customView.getMeasuredWidth();
            customView.layout(xpos, ypos, xpos + customWidth, ypos + customView.getMeasuredHeight());
            next(x2, customWidth, isLayoutRtl);
        }
        if (this.mProgressView != null) {
            this.mProgressView.bringToFront();
            int halfProgressHeight = this.mProgressView.getMeasuredHeight() / 2;
            this.mProgressView.layout(this.mProgressBarPadding, -halfProgressHeight, this.mProgressBarPadding + this.mProgressView.getMeasuredWidth(), halfProgressHeight);
        }
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ActionBar.LayoutParams(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp == null) {
            lp = generateDefaultLayoutParams();
        }
        return lp;
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);
        if (this.mExpandedMenuPresenter != null && this.mExpandedMenuPresenter.mCurrentExpandedItem != null) {
            state.expandedMenuItemId = this.mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
        }
        state.isOverflowOpen = isOverflowMenuShowing();
        return state;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable p) {
        MenuItem item;
        SavedState state = (SavedState) p;
        super.onRestoreInstanceState(state.getSuperState());
        if (state.expandedMenuItemId != 0 && this.mExpandedMenuPresenter != null && this.mOptionsMenu != null && (item = this.mOptionsMenu.findItem(state.expandedMenuItemId)) != null) {
            item.expandActionView();
        }
        if (state.isOverflowOpen) {
            postShowOverflowMenu();
        }
    }

    public void setHomeAsUpIndicator(Drawable indicator) {
        this.mHomeLayout.setUpIndicator(indicator);
    }

    public void setHomeAsUpIndicator(int resId) {
        this.mHomeLayout.setUpIndicator(resId);
    }

    public void setHomeActionContentDescription(CharSequence description) {
        this.mHomeDescription = description;
    }

    public void setHomeActionContentDescription(int resId) {
        this.mHomeDescriptionRes = resId;
        this.mHomeDescription = resId != 0 ? getResources().getText(resId) : null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActionBarView$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        int expandedMenuItemId;
        boolean isOverflowOpen;
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.internal.widget.ActionBarView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.expandedMenuItemId = in.readInt();
            this.isOverflowOpen = in.readInt() != 0;
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.expandedMenuItemId);
            out.writeInt(this.isOverflowOpen ? 1 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActionBarView$HomeView.class */
    public static class HomeView extends FrameLayout {
        private ImageView mUpView;
        private ImageView mIconView;
        private int mUpWidth;
        private int mStartOffset;
        private int mUpIndicatorRes;
        private Drawable mDefaultUpIndicator;
        private static final long DEFAULT_TRANSITION_DURATION = 150;

        public HomeView(Context context) {
            this(context, null);
        }

        public HomeView(Context context, AttributeSet attrs) {
            super(context, attrs);
            LayoutTransition t = getLayoutTransition();
            if (t != null) {
                t.setDuration(DEFAULT_TRANSITION_DURATION);
            }
        }

        public void setShowUp(boolean isUp) {
            this.mUpView.setVisibility(isUp ? 0 : 8);
        }

        public void setShowIcon(boolean showIcon) {
            this.mIconView.setVisibility(showIcon ? 0 : 8);
        }

        public void setIcon(Drawable icon) {
            this.mIconView.setImageDrawable(icon);
        }

        public void setUpIndicator(Drawable d) {
            this.mUpView.setImageDrawable(d != null ? d : this.mDefaultUpIndicator);
            this.mUpIndicatorRes = 0;
        }

        public void setUpIndicator(int resId) {
            this.mUpIndicatorRes = resId;
            this.mUpView.setImageDrawable(resId != 0 ? getResources().getDrawable(resId) : null);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            if (this.mUpIndicatorRes != 0) {
                setUpIndicator(this.mUpIndicatorRes);
            }
        }

        @Override // android.view.View
        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
            onPopulateAccessibilityEvent(event);
            return true;
        }

        @Override // android.view.View
        public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
            super.onPopulateAccessibilityEvent(event);
            CharSequence cdesc = getContentDescription();
            if (!TextUtils.isEmpty(cdesc)) {
                event.getText().add(cdesc);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchHoverEvent(MotionEvent event) {
            return onHoverEvent(event);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onFinishInflate() {
            this.mUpView = (ImageView) findViewById(R.id.up);
            this.mIconView = (ImageView) findViewById(16908332);
            this.mDefaultUpIndicator = this.mUpView.getDrawable();
        }

        public int getStartOffset() {
            if (this.mUpView.getVisibility() == 8) {
                return this.mStartOffset;
            }
            return 0;
        }

        public int getUpWidth() {
            return this.mUpWidth;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            measureChildWithMargins(this.mUpView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            FrameLayout.LayoutParams upLp = (FrameLayout.LayoutParams) this.mUpView.getLayoutParams();
            int upMargins = upLp.leftMargin + upLp.rightMargin;
            this.mUpWidth = this.mUpView.getMeasuredWidth();
            this.mStartOffset = this.mUpWidth + upMargins;
            int width = this.mUpView.getVisibility() == 8 ? 0 : this.mStartOffset;
            int height = upLp.topMargin + this.mUpView.getMeasuredHeight() + upLp.bottomMargin;
            if (this.mIconView.getVisibility() != 8) {
                measureChildWithMargins(this.mIconView, widthMeasureSpec, width, heightMeasureSpec, 0);
                FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) this.mIconView.getLayoutParams();
                width += iconLp.leftMargin + this.mIconView.getMeasuredWidth() + iconLp.rightMargin;
                height = Math.max(height, iconLp.topMargin + this.mIconView.getMeasuredHeight() + iconLp.bottomMargin);
            } else if (upMargins < 0) {
                width -= upMargins;
            }
            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            switch (widthMode) {
                case Integer.MIN_VALUE:
                    width = Math.min(width, widthSize);
                    break;
                case 1073741824:
                    width = widthSize;
                    break;
            }
            switch (heightMode) {
                case Integer.MIN_VALUE:
                    height = Math.min(height, heightSize);
                    break;
                case 1073741824:
                    height = heightSize;
                    break;
            }
            setMeasuredDimension(width, height);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int iconLeft;
            int iconRight;
            int upRight;
            int upLeft;
            int vCenter = (b - t) / 2;
            boolean isLayoutRtl = isLayoutRtl();
            int width = getWidth();
            int upOffset = 0;
            if (this.mUpView.getVisibility() != 8) {
                FrameLayout.LayoutParams upLp = (FrameLayout.LayoutParams) this.mUpView.getLayoutParams();
                int upHeight = this.mUpView.getMeasuredHeight();
                int upWidth = this.mUpView.getMeasuredWidth();
                upOffset = upLp.leftMargin + upWidth + upLp.rightMargin;
                int upTop = vCenter - (upHeight / 2);
                int upBottom = upTop + upHeight;
                if (isLayoutRtl) {
                    upRight = width;
                    upLeft = upRight - upWidth;
                    r -= upOffset;
                } else {
                    upRight = upWidth;
                    upLeft = 0;
                    l += upOffset;
                }
                this.mUpView.layout(upLeft, upTop, upRight, upBottom);
            }
            FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) this.mIconView.getLayoutParams();
            int iconHeight = this.mIconView.getMeasuredHeight();
            int iconWidth = this.mIconView.getMeasuredWidth();
            int hCenter = (r - l) / 2;
            int iconTop = Math.max(iconLp.topMargin, vCenter - (iconHeight / 2));
            int iconBottom = iconTop + iconHeight;
            int marginStart = iconLp.getMarginStart();
            int delta = Math.max(marginStart, hCenter - (iconWidth / 2));
            if (isLayoutRtl) {
                iconRight = (width - upOffset) - delta;
                iconLeft = iconRight - iconWidth;
            } else {
                iconLeft = upOffset + delta;
                iconRight = iconLeft + iconWidth;
            }
            this.mIconView.layout(iconLeft, iconTop, iconRight, iconBottom);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActionBarView$ExpandedActionViewMenuPresenter.class */
    public class ExpandedActionViewMenuPresenter implements MenuPresenter {
        MenuBuilder mMenu;
        MenuItemImpl mCurrentExpandedItem;

        private ExpandedActionViewMenuPresenter() {
        }

        public void initForMenu(Context context, MenuBuilder menu) {
            if (this.mMenu != null && this.mCurrentExpandedItem != null) {
                this.mMenu.collapseItemActionView(this.mCurrentExpandedItem);
            }
            this.mMenu = menu;
        }

        public MenuView getMenuView(ViewGroup root) {
            return null;
        }

        public void updateMenuView(boolean cleared) {
            if (this.mCurrentExpandedItem != null) {
                boolean found = false;
                if (this.mMenu != null) {
                    int count = this.mMenu.size();
                    int i = 0;
                    while (true) {
                        if (i >= count) {
                            break;
                        } else if (this.mMenu.getItem(i) != this.mCurrentExpandedItem) {
                            i++;
                        } else {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    collapseItemActionView(this.mMenu, this.mCurrentExpandedItem);
                }
            }
        }

        public void setCallback(MenuPresenter.Callback cb) {
        }

        public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
            return false;
        }

        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        public boolean flagActionItems() {
            return false;
        }

        public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
            ActionBarTransition.beginDelayedTransition(ActionBarView.this);
            ActionBarView.this.mExpandedActionView = item.getActionView();
            ActionBarView.this.mExpandedHomeLayout.setIcon(ActionBarView.this.mIcon.getConstantState().newDrawable(ActionBarView.this.getResources()));
            this.mCurrentExpandedItem = item;
            if (ActionBarView.this.mExpandedActionView.getParent() != ActionBarView.this) {
                ActionBarView.this.addView(ActionBarView.this.mExpandedActionView);
            }
            if (ActionBarView.this.mExpandedHomeLayout.getParent() != ActionBarView.this.mUpGoerFive) {
                ActionBarView.this.mUpGoerFive.addView(ActionBarView.this.mExpandedHomeLayout);
            }
            ActionBarView.this.mHomeLayout.setVisibility(8);
            if (ActionBarView.this.mTitleLayout != null) {
                ActionBarView.this.mTitleLayout.setVisibility(8);
            }
            if (ActionBarView.this.mTabScrollView != null) {
                ActionBarView.this.mTabScrollView.setVisibility(8);
            }
            if (ActionBarView.this.mSpinner != null) {
                ActionBarView.this.mSpinner.setVisibility(8);
            }
            if (ActionBarView.this.mCustomNavView != null) {
                ActionBarView.this.mCustomNavView.setVisibility(8);
            }
            ActionBarView.this.setHomeButtonEnabled(false, false);
            ActionBarView.this.requestLayout();
            item.setActionViewExpanded(true);
            if (ActionBarView.this.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) ActionBarView.this.mExpandedActionView).onActionViewExpanded();
                return true;
            }
            return true;
        }

        public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
            ActionBarTransition.beginDelayedTransition(ActionBarView.this);
            if (ActionBarView.this.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) ActionBarView.this.mExpandedActionView).onActionViewCollapsed();
            }
            ActionBarView.this.removeView(ActionBarView.this.mExpandedActionView);
            ActionBarView.this.mUpGoerFive.removeView(ActionBarView.this.mExpandedHomeLayout);
            ActionBarView.this.mExpandedActionView = null;
            if ((ActionBarView.this.mDisplayOptions & 2) != 0) {
                ActionBarView.this.mHomeLayout.setVisibility(0);
            }
            if ((ActionBarView.this.mDisplayOptions & 8) != 0) {
                if (ActionBarView.this.mTitleLayout == null) {
                    ActionBarView.this.initTitle();
                } else {
                    ActionBarView.this.mTitleLayout.setVisibility(0);
                }
            }
            if (ActionBarView.this.mTabScrollView != null) {
                ActionBarView.this.mTabScrollView.setVisibility(0);
            }
            if (ActionBarView.this.mSpinner != null) {
                ActionBarView.this.mSpinner.setVisibility(0);
            }
            if (ActionBarView.this.mCustomNavView != null) {
                ActionBarView.this.mCustomNavView.setVisibility(0);
            }
            ActionBarView.this.mExpandedHomeLayout.setIcon(null);
            this.mCurrentExpandedItem = null;
            ActionBarView.this.setHomeButtonEnabled(ActionBarView.this.mWasHomeEnabled);
            ActionBarView.this.requestLayout();
            item.setActionViewExpanded(false);
            return true;
        }

        public int getId() {
            return 0;
        }

        public Parcelable onSaveInstanceState() {
            return null;
        }

        public void onRestoreInstanceState(Parcelable state) {
        }
    }
}