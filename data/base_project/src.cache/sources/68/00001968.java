package com.android.internal.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Property;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.SpinnerAdapter;
import com.android.internal.R;
import com.android.internal.view.ActionBarPolicy;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuPopupHelper;
import com.android.internal.view.menu.SubMenuBuilder;
import com.android.internal.widget.ActionBarContainer;
import com.android.internal.widget.ActionBarContextView;
import com.android.internal.widget.ActionBarOverlayLayout;
import com.android.internal.widget.ActionBarView;
import com.android.internal.widget.ScrollingTabContainerView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* loaded from: ActionBarImpl.class */
public class ActionBarImpl extends ActionBar {
    private static final String TAG = "ActionBarImpl";
    private Context mContext;
    private Context mThemedContext;
    private Activity mActivity;
    private Dialog mDialog;
    private ActionBarOverlayLayout mOverlayLayout;
    private ActionBarContainer mContainerView;
    private ActionBarView mActionView;
    private ActionBarContextView mContextView;
    private ActionBarContainer mSplitView;
    private View mContentView;
    private ScrollingTabContainerView mTabScrollView;
    private TabImpl mSelectedTab;
    private boolean mDisplayHomeAsUpSet;
    ActionModeImpl mActionMode;
    ActionMode mDeferredDestroyActionMode;
    ActionMode.Callback mDeferredModeDestroyCallback;
    private boolean mLastMenuVisibility;
    private static final int CONTEXT_DISPLAY_NORMAL = 0;
    private static final int CONTEXT_DISPLAY_SPLIT = 1;
    private static final int INVALID_POSITION = -1;
    private int mContextDisplayMode;
    private boolean mHasEmbeddedTabs;
    Runnable mTabSelector;
    private boolean mHiddenByApp;
    private boolean mHiddenBySystem;
    private boolean mShowingForMode;
    private Animator mCurrentShowAnim;
    private boolean mShowHideAnimationEnabled;
    private ArrayList<TabImpl> mTabs = new ArrayList<>();
    private int mSavedTabPosition = -1;
    private ArrayList<ActionBar.OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList<>();
    final Handler mHandler = new Handler();
    private int mCurWindowVisibility = 0;
    private boolean mContentAnimations = true;
    private boolean mNowShowing = true;
    final Animator.AnimatorListener mHideListener = new AnimatorListenerAdapter() { // from class: com.android.internal.app.ActionBarImpl.1
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            if (ActionBarImpl.this.mContentAnimations && ActionBarImpl.this.mContentView != null) {
                ActionBarImpl.this.mContentView.setTranslationY(0.0f);
                ActionBarImpl.this.mContainerView.setTranslationY(0.0f);
            }
            if (ActionBarImpl.this.mSplitView != null && ActionBarImpl.this.mContextDisplayMode == 1) {
                ActionBarImpl.this.mSplitView.setVisibility(8);
            }
            ActionBarImpl.this.mContainerView.setVisibility(8);
            ActionBarImpl.this.mContainerView.setTransitioning(false);
            ActionBarImpl.this.mCurrentShowAnim = null;
            ActionBarImpl.this.completeDeferredDestroyActionMode();
            if (ActionBarImpl.this.mOverlayLayout != null) {
                ActionBarImpl.this.mOverlayLayout.requestFitSystemWindows();
            }
        }
    };
    final Animator.AnimatorListener mShowListener = new AnimatorListenerAdapter() { // from class: com.android.internal.app.ActionBarImpl.2
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            ActionBarImpl.this.mCurrentShowAnim = null;
            ActionBarImpl.this.mContainerView.requestLayout();
        }
    };
    final ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.internal.app.ActionBarImpl.3
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            ViewParent parent = ActionBarImpl.this.mContainerView.getParent();
            ((View) parent).invalidate();
        }
    };

    public ActionBarImpl(Activity activity) {
        this.mActivity = activity;
        Window window = activity.getWindow();
        View decor = window.getDecorView();
        boolean overlayMode = this.mActivity.getWindow().hasFeature(9);
        init(decor);
        if (!overlayMode) {
            this.mContentView = decor.findViewById(16908290);
        }
    }

    public ActionBarImpl(Dialog dialog) {
        this.mDialog = dialog;
        init(dialog.getWindow().getDecorView());
    }

    private void init(View decor) {
        this.mContext = decor.getContext();
        this.mOverlayLayout = (ActionBarOverlayLayout) decor.findViewById(R.id.action_bar_overlay_layout);
        if (this.mOverlayLayout != null) {
            this.mOverlayLayout.setActionBar(this);
        }
        this.mActionView = (ActionBarView) decor.findViewById(R.id.action_bar);
        this.mContextView = (ActionBarContextView) decor.findViewById(R.id.action_context_bar);
        this.mContainerView = (ActionBarContainer) decor.findViewById(R.id.action_bar_container);
        this.mSplitView = (ActionBarContainer) decor.findViewById(R.id.split_action_bar);
        if (this.mActionView == null || this.mContextView == null || this.mContainerView == null) {
            throw new IllegalStateException(getClass().getSimpleName() + " can only be used with a compatible window decor layout");
        }
        this.mActionView.setContextView(this.mContextView);
        this.mContextDisplayMode = this.mActionView.isSplitActionBar() ? 1 : 0;
        int current = this.mActionView.getDisplayOptions();
        boolean homeAsUp = (current & 4) != 0;
        if (homeAsUp) {
            this.mDisplayHomeAsUpSet = true;
        }
        ActionBarPolicy abp = ActionBarPolicy.get(this.mContext);
        setHomeButtonEnabled(abp.enableHomeButtonByDefault() || homeAsUp);
        setHasEmbeddedTabs(abp.hasEmbeddedTabs());
    }

    public void onConfigurationChanged(Configuration newConfig) {
        setHasEmbeddedTabs(ActionBarPolicy.get(this.mContext).hasEmbeddedTabs());
    }

    private void setHasEmbeddedTabs(boolean hasEmbeddedTabs) {
        this.mHasEmbeddedTabs = hasEmbeddedTabs;
        if (!this.mHasEmbeddedTabs) {
            this.mActionView.setEmbeddedTabView(null);
            this.mContainerView.setTabContainer(this.mTabScrollView);
        } else {
            this.mContainerView.setTabContainer(null);
            this.mActionView.setEmbeddedTabView(this.mTabScrollView);
        }
        boolean isInTabMode = getNavigationMode() == 2;
        if (this.mTabScrollView != null) {
            if (isInTabMode) {
                this.mTabScrollView.setVisibility(0);
                if (this.mOverlayLayout != null) {
                    this.mOverlayLayout.requestFitSystemWindows();
                }
            } else {
                this.mTabScrollView.setVisibility(8);
            }
        }
        this.mActionView.setCollapsable(!this.mHasEmbeddedTabs && isInTabMode);
    }

    public boolean hasNonEmbeddedTabs() {
        return !this.mHasEmbeddedTabs && getNavigationMode() == 2;
    }

    private void ensureTabsExist() {
        if (this.mTabScrollView != null) {
            return;
        }
        ScrollingTabContainerView tabScroller = new ScrollingTabContainerView(this.mContext);
        if (this.mHasEmbeddedTabs) {
            tabScroller.setVisibility(0);
            this.mActionView.setEmbeddedTabView(tabScroller);
        } else {
            if (getNavigationMode() == 2) {
                tabScroller.setVisibility(0);
                if (this.mOverlayLayout != null) {
                    this.mOverlayLayout.requestFitSystemWindows();
                }
            } else {
                tabScroller.setVisibility(8);
            }
            this.mContainerView.setTabContainer(tabScroller);
        }
        this.mTabScrollView = tabScroller;
    }

    void completeDeferredDestroyActionMode() {
        if (this.mDeferredModeDestroyCallback != null) {
            this.mDeferredModeDestroyCallback.onDestroyActionMode(this.mDeferredDestroyActionMode);
            this.mDeferredDestroyActionMode = null;
            this.mDeferredModeDestroyCallback = null;
        }
    }

    public void setWindowVisibility(int visibility) {
        this.mCurWindowVisibility = visibility;
    }

    public void setShowHideAnimationEnabled(boolean enabled) {
        this.mShowHideAnimationEnabled = enabled;
        if (!enabled && this.mCurrentShowAnim != null) {
            this.mCurrentShowAnim.end();
        }
    }

    @Override // android.app.ActionBar
    public void addOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener listener) {
        this.mMenuVisibilityListeners.add(listener);
    }

    @Override // android.app.ActionBar
    public void removeOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener listener) {
        this.mMenuVisibilityListeners.remove(listener);
    }

    public void dispatchMenuVisibilityChanged(boolean isVisible) {
        if (isVisible == this.mLastMenuVisibility) {
            return;
        }
        this.mLastMenuVisibility = isVisible;
        int count = this.mMenuVisibilityListeners.size();
        for (int i = 0; i < count; i++) {
            this.mMenuVisibilityListeners.get(i).onMenuVisibilityChanged(isVisible);
        }
    }

    @Override // android.app.ActionBar
    public void setCustomView(int resId) {
        setCustomView(LayoutInflater.from(getThemedContext()).inflate(resId, (ViewGroup) this.mActionView, false));
    }

    @Override // android.app.ActionBar
    public void setDisplayUseLogoEnabled(boolean useLogo) {
        setDisplayOptions(useLogo ? 1 : 0, 1);
    }

    @Override // android.app.ActionBar
    public void setDisplayShowHomeEnabled(boolean showHome) {
        setDisplayOptions(showHome ? 2 : 0, 2);
    }

    @Override // android.app.ActionBar
    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
        setDisplayOptions(showHomeAsUp ? 4 : 0, 4);
    }

    @Override // android.app.ActionBar
    public void setDisplayShowTitleEnabled(boolean showTitle) {
        setDisplayOptions(showTitle ? 8 : 0, 8);
    }

    @Override // android.app.ActionBar
    public void setDisplayShowCustomEnabled(boolean showCustom) {
        setDisplayOptions(showCustom ? 16 : 0, 16);
    }

    @Override // android.app.ActionBar
    public void setHomeButtonEnabled(boolean enable) {
        this.mActionView.setHomeButtonEnabled(enable);
    }

    @Override // android.app.ActionBar
    public void setTitle(int resId) {
        setTitle(this.mContext.getString(resId));
    }

    @Override // android.app.ActionBar
    public void setSubtitle(int resId) {
        setSubtitle(this.mContext.getString(resId));
    }

    @Override // android.app.ActionBar
    public void setSelectedNavigationItem(int position) {
        switch (this.mActionView.getNavigationMode()) {
            case 1:
                this.mActionView.setDropdownSelectedPosition(position);
                return;
            case 2:
                selectTab(this.mTabs.get(position));
                return;
            default:
                throw new IllegalStateException("setSelectedNavigationIndex not valid for current navigation mode");
        }
    }

    @Override // android.app.ActionBar
    public void removeAllTabs() {
        cleanupTabs();
    }

    private void cleanupTabs() {
        if (this.mSelectedTab != null) {
            selectTab(null);
        }
        this.mTabs.clear();
        if (this.mTabScrollView != null) {
            this.mTabScrollView.removeAllTabs();
        }
        this.mSavedTabPosition = -1;
    }

    @Override // android.app.ActionBar
    public void setTitle(CharSequence title) {
        this.mActionView.setTitle(title);
    }

    @Override // android.app.ActionBar
    public void setSubtitle(CharSequence subtitle) {
        this.mActionView.setSubtitle(subtitle);
    }

    @Override // android.app.ActionBar
    public void setDisplayOptions(int options) {
        if ((options & 4) != 0) {
            this.mDisplayHomeAsUpSet = true;
        }
        this.mActionView.setDisplayOptions(options);
    }

    @Override // android.app.ActionBar
    public void setDisplayOptions(int options, int mask) {
        int current = this.mActionView.getDisplayOptions();
        if ((mask & 4) != 0) {
            this.mDisplayHomeAsUpSet = true;
        }
        this.mActionView.setDisplayOptions((options & mask) | (current & (mask ^ (-1))));
    }

    @Override // android.app.ActionBar
    public void setBackgroundDrawable(Drawable d) {
        this.mContainerView.setPrimaryBackground(d);
    }

    @Override // android.app.ActionBar
    public void setStackedBackgroundDrawable(Drawable d) {
        this.mContainerView.setStackedBackground(d);
    }

    @Override // android.app.ActionBar
    public void setSplitBackgroundDrawable(Drawable d) {
        if (this.mSplitView != null) {
            this.mSplitView.setSplitBackground(d);
        }
    }

    @Override // android.app.ActionBar
    public View getCustomView() {
        return this.mActionView.getCustomNavigationView();
    }

    @Override // android.app.ActionBar
    public CharSequence getTitle() {
        return this.mActionView.getTitle();
    }

    @Override // android.app.ActionBar
    public CharSequence getSubtitle() {
        return this.mActionView.getSubtitle();
    }

    @Override // android.app.ActionBar
    public int getNavigationMode() {
        return this.mActionView.getNavigationMode();
    }

    @Override // android.app.ActionBar
    public int getDisplayOptions() {
        return this.mActionView.getDisplayOptions();
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (this.mActionMode != null) {
            this.mActionMode.finish();
        }
        this.mContextView.killMode();
        ActionModeImpl mode = new ActionModeImpl(callback);
        if (mode.dispatchOnCreate()) {
            mode.invalidate();
            this.mContextView.initForMode(mode);
            animateToMode(true);
            if (this.mSplitView != null && this.mContextDisplayMode == 1 && this.mSplitView.getVisibility() != 0) {
                this.mSplitView.setVisibility(0);
                if (this.mOverlayLayout != null) {
                    this.mOverlayLayout.requestFitSystemWindows();
                }
            }
            this.mContextView.sendAccessibilityEvent(32);
            this.mActionMode = mode;
            return mode;
        }
        return null;
    }

    private void configureTab(ActionBar.Tab tab, int position) {
        TabImpl tabi = (TabImpl) tab;
        ActionBar.TabListener callback = tabi.getCallback();
        if (callback == null) {
            throw new IllegalStateException("Action Bar Tab must have a Callback");
        }
        tabi.setPosition(position);
        this.mTabs.add(position, tabi);
        int count = this.mTabs.size();
        for (int i = position + 1; i < count; i++) {
            this.mTabs.get(i).setPosition(i);
        }
    }

    @Override // android.app.ActionBar
    public void addTab(ActionBar.Tab tab) {
        addTab(tab, this.mTabs.isEmpty());
    }

    @Override // android.app.ActionBar
    public void addTab(ActionBar.Tab tab, int position) {
        addTab(tab, position, this.mTabs.isEmpty());
    }

    @Override // android.app.ActionBar
    public void addTab(ActionBar.Tab tab, boolean setSelected) {
        ensureTabsExist();
        this.mTabScrollView.addTab(tab, setSelected);
        configureTab(tab, this.mTabs.size());
        if (setSelected) {
            selectTab(tab);
        }
    }

    @Override // android.app.ActionBar
    public void addTab(ActionBar.Tab tab, int position, boolean setSelected) {
        ensureTabsExist();
        this.mTabScrollView.addTab(tab, position, setSelected);
        configureTab(tab, position);
        if (setSelected) {
            selectTab(tab);
        }
    }

    @Override // android.app.ActionBar
    public ActionBar.Tab newTab() {
        return new TabImpl();
    }

    @Override // android.app.ActionBar
    public void removeTab(ActionBar.Tab tab) {
        removeTabAt(tab.getPosition());
    }

    @Override // android.app.ActionBar
    public void removeTabAt(int position) {
        if (this.mTabScrollView == null) {
            return;
        }
        int selectedTabPosition = this.mSelectedTab != null ? this.mSelectedTab.getPosition() : this.mSavedTabPosition;
        this.mTabScrollView.removeTabAt(position);
        TabImpl removedTab = this.mTabs.remove(position);
        if (removedTab != null) {
            removedTab.setPosition(-1);
        }
        int newTabCount = this.mTabs.size();
        for (int i = position; i < newTabCount; i++) {
            this.mTabs.get(i).setPosition(i);
        }
        if (selectedTabPosition == position) {
            selectTab(this.mTabs.isEmpty() ? null : this.mTabs.get(Math.max(0, position - 1)));
        }
    }

    @Override // android.app.ActionBar
    public void selectTab(ActionBar.Tab tab) {
        if (getNavigationMode() != 2) {
            this.mSavedTabPosition = tab != null ? tab.getPosition() : -1;
            return;
        }
        FragmentTransaction trans = this.mActivity.getFragmentManager().beginTransaction().disallowAddToBackStack();
        if (this.mSelectedTab == tab) {
            if (this.mSelectedTab != null) {
                this.mSelectedTab.getCallback().onTabReselected(this.mSelectedTab, trans);
                this.mTabScrollView.animateToTab(tab.getPosition());
            }
        } else {
            this.mTabScrollView.setTabSelected(tab != null ? tab.getPosition() : -1);
            if (this.mSelectedTab != null) {
                this.mSelectedTab.getCallback().onTabUnselected(this.mSelectedTab, trans);
            }
            this.mSelectedTab = (TabImpl) tab;
            if (this.mSelectedTab != null) {
                this.mSelectedTab.getCallback().onTabSelected(this.mSelectedTab, trans);
            }
        }
        if (!trans.isEmpty()) {
            trans.commit();
        }
    }

    @Override // android.app.ActionBar
    public ActionBar.Tab getSelectedTab() {
        return this.mSelectedTab;
    }

    @Override // android.app.ActionBar
    public int getHeight() {
        return this.mContainerView.getHeight();
    }

    public void enableContentAnimations(boolean enabled) {
        this.mContentAnimations = enabled;
    }

    @Override // android.app.ActionBar
    public void show() {
        if (this.mHiddenByApp) {
            this.mHiddenByApp = false;
            updateVisibility(false);
        }
    }

    private void showForActionMode() {
        if (!this.mShowingForMode) {
            this.mShowingForMode = true;
            if (this.mOverlayLayout != null) {
                this.mOverlayLayout.setShowingForActionMode(true);
            }
            updateVisibility(false);
        }
    }

    public void showForSystem() {
        if (this.mHiddenBySystem) {
            this.mHiddenBySystem = false;
            updateVisibility(true);
        }
    }

    @Override // android.app.ActionBar
    public void hide() {
        if (!this.mHiddenByApp) {
            this.mHiddenByApp = true;
            updateVisibility(false);
        }
    }

    private void hideForActionMode() {
        if (this.mShowingForMode) {
            this.mShowingForMode = false;
            if (this.mOverlayLayout != null) {
                this.mOverlayLayout.setShowingForActionMode(false);
            }
            updateVisibility(false);
        }
    }

    public void hideForSystem() {
        if (!this.mHiddenBySystem) {
            this.mHiddenBySystem = true;
            updateVisibility(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean checkShowingFlags(boolean hiddenByApp, boolean hiddenBySystem, boolean showingForMode) {
        if (showingForMode) {
            return true;
        }
        if (hiddenByApp || hiddenBySystem) {
            return false;
        }
        return true;
    }

    private void updateVisibility(boolean fromSystem) {
        boolean shown = checkShowingFlags(this.mHiddenByApp, this.mHiddenBySystem, this.mShowingForMode);
        if (shown) {
            if (!this.mNowShowing) {
                this.mNowShowing = true;
                doShow(fromSystem);
            }
        } else if (this.mNowShowing) {
            this.mNowShowing = false;
            doHide(fromSystem);
        }
    }

    public void doShow(boolean fromSystem) {
        if (this.mCurrentShowAnim != null) {
            this.mCurrentShowAnim.end();
        }
        this.mContainerView.setVisibility(0);
        if (this.mCurWindowVisibility == 0 && (this.mShowHideAnimationEnabled || fromSystem)) {
            this.mContainerView.setTranslationY(0.0f);
            float startingY = -this.mContainerView.getHeight();
            if (fromSystem) {
                int[] topLeft = {0, 0};
                this.mContainerView.getLocationInWindow(topLeft);
                startingY -= topLeft[1];
            }
            this.mContainerView.setTranslationY(startingY);
            AnimatorSet anim = new AnimatorSet();
            ObjectAnimator a = ObjectAnimator.ofFloat(this.mContainerView, (Property<ActionBarContainer, Float>) View.TRANSLATION_Y, 0.0f);
            a.addUpdateListener(this.mUpdateListener);
            AnimatorSet.Builder b = anim.play(a);
            if (this.mContentAnimations && this.mContentView != null) {
                b.with(ObjectAnimator.ofFloat(this.mContentView, View.TRANSLATION_Y, startingY, 0.0f));
            }
            if (this.mSplitView != null && this.mContextDisplayMode == 1) {
                this.mSplitView.setTranslationY(this.mSplitView.getHeight());
                this.mSplitView.setVisibility(0);
                b.with(ObjectAnimator.ofFloat(this.mSplitView, (Property<ActionBarContainer, Float>) View.TRANSLATION_Y, 0.0f));
            }
            anim.setInterpolator(AnimationUtils.loadInterpolator(this.mContext, 17563651));
            anim.setDuration(250L);
            anim.addListener(this.mShowListener);
            this.mCurrentShowAnim = anim;
            anim.start();
        } else {
            this.mContainerView.setAlpha(1.0f);
            this.mContainerView.setTranslationY(0.0f);
            if (this.mContentAnimations && this.mContentView != null) {
                this.mContentView.setTranslationY(0.0f);
            }
            if (this.mSplitView != null && this.mContextDisplayMode == 1) {
                this.mSplitView.setAlpha(1.0f);
                this.mSplitView.setTranslationY(0.0f);
                this.mSplitView.setVisibility(0);
            }
            this.mShowListener.onAnimationEnd(null);
        }
        if (this.mOverlayLayout != null) {
            this.mOverlayLayout.requestFitSystemWindows();
        }
    }

    public void doHide(boolean fromSystem) {
        if (this.mCurrentShowAnim != null) {
            this.mCurrentShowAnim.end();
        }
        if (this.mCurWindowVisibility == 0 && (this.mShowHideAnimationEnabled || fromSystem)) {
            this.mContainerView.setAlpha(1.0f);
            this.mContainerView.setTransitioning(true);
            AnimatorSet anim = new AnimatorSet();
            float endingY = -this.mContainerView.getHeight();
            if (fromSystem) {
                int[] topLeft = {0, 0};
                this.mContainerView.getLocationInWindow(topLeft);
                endingY -= topLeft[1];
            }
            ObjectAnimator a = ObjectAnimator.ofFloat(this.mContainerView, (Property<ActionBarContainer, Float>) View.TRANSLATION_Y, endingY);
            a.addUpdateListener(this.mUpdateListener);
            AnimatorSet.Builder b = anim.play(a);
            if (this.mContentAnimations && this.mContentView != null) {
                b.with(ObjectAnimator.ofFloat(this.mContentView, View.TRANSLATION_Y, 0.0f, endingY));
            }
            if (this.mSplitView != null && this.mSplitView.getVisibility() == 0) {
                this.mSplitView.setAlpha(1.0f);
                b.with(ObjectAnimator.ofFloat(this.mSplitView, (Property<ActionBarContainer, Float>) View.TRANSLATION_Y, this.mSplitView.getHeight()));
            }
            anim.setInterpolator(AnimationUtils.loadInterpolator(this.mContext, 17563650));
            anim.setDuration(250L);
            anim.addListener(this.mHideListener);
            this.mCurrentShowAnim = anim;
            anim.start();
            return;
        }
        this.mHideListener.onAnimationEnd(null);
    }

    @Override // android.app.ActionBar
    public boolean isShowing() {
        return this.mNowShowing;
    }

    public boolean isSystemShowing() {
        return !this.mHiddenBySystem;
    }

    void animateToMode(boolean toActionMode) {
        if (toActionMode) {
            showForActionMode();
        } else {
            hideForActionMode();
        }
        this.mActionView.animateToVisibility(toActionMode ? 8 : 0);
        this.mContextView.animateToVisibility(toActionMode ? 0 : 8);
        if (this.mTabScrollView != null && !this.mActionView.hasEmbeddedTabs() && this.mActionView.isCollapsed()) {
            this.mTabScrollView.animateToVisibility(toActionMode ? 8 : 0);
        }
    }

    @Override // android.app.ActionBar
    public Context getThemedContext() {
        if (this.mThemedContext == null) {
            TypedValue outValue = new TypedValue();
            Resources.Theme currentTheme = this.mContext.getTheme();
            currentTheme.resolveAttribute(16843671, outValue, true);
            int targetThemeRes = outValue.resourceId;
            if (targetThemeRes != 0 && this.mContext.getThemeResId() != targetThemeRes) {
                this.mThemedContext = new ContextThemeWrapper(this.mContext, targetThemeRes);
            } else {
                this.mThemedContext = this.mContext;
            }
        }
        return this.mThemedContext;
    }

    @Override // android.app.ActionBar
    public boolean isTitleTruncated() {
        return this.mActionView != null && this.mActionView.isTitleTruncated();
    }

    @Override // android.app.ActionBar
    public void setHomeAsUpIndicator(Drawable indicator) {
        this.mActionView.setHomeAsUpIndicator(indicator);
    }

    @Override // android.app.ActionBar
    public void setHomeAsUpIndicator(int resId) {
        this.mActionView.setHomeAsUpIndicator(resId);
    }

    @Override // android.app.ActionBar
    public void setHomeActionContentDescription(CharSequence description) {
        this.mActionView.setHomeActionContentDescription(description);
    }

    @Override // android.app.ActionBar
    public void setHomeActionContentDescription(int resId) {
        this.mActionView.setHomeActionContentDescription(resId);
    }

    /* loaded from: ActionBarImpl$ActionModeImpl.class */
    public class ActionModeImpl extends ActionMode implements MenuBuilder.Callback {
        private ActionMode.Callback mCallback;
        private MenuBuilder mMenu;
        private WeakReference<View> mCustomView;

        public ActionModeImpl(ActionMode.Callback callback) {
            this.mCallback = callback;
            this.mMenu = new MenuBuilder(ActionBarImpl.this.getThemedContext()).setDefaultShowAsAction(1);
            this.mMenu.setCallback(this);
        }

        @Override // android.view.ActionMode
        public MenuInflater getMenuInflater() {
            return new MenuInflater(ActionBarImpl.this.getThemedContext());
        }

        @Override // android.view.ActionMode
        public Menu getMenu() {
            return this.mMenu;
        }

        @Override // android.view.ActionMode
        public void finish() {
            if (ActionBarImpl.this.mActionMode == this) {
                if (!ActionBarImpl.checkShowingFlags(ActionBarImpl.this.mHiddenByApp, ActionBarImpl.this.mHiddenBySystem, false)) {
                    ActionBarImpl.this.mDeferredDestroyActionMode = this;
                    ActionBarImpl.this.mDeferredModeDestroyCallback = this.mCallback;
                } else {
                    this.mCallback.onDestroyActionMode(this);
                }
                this.mCallback = null;
                ActionBarImpl.this.animateToMode(false);
                ActionBarImpl.this.mContextView.closeMode();
                ActionBarImpl.this.mActionView.sendAccessibilityEvent(32);
                ActionBarImpl.this.mActionMode = null;
            }
        }

        @Override // android.view.ActionMode
        public void invalidate() {
            this.mMenu.stopDispatchingItemsChanged();
            try {
                this.mCallback.onPrepareActionMode(this, this.mMenu);
                this.mMenu.startDispatchingItemsChanged();
            } catch (Throwable th) {
                this.mMenu.startDispatchingItemsChanged();
                throw th;
            }
        }

        public boolean dispatchOnCreate() {
            this.mMenu.stopDispatchingItemsChanged();
            try {
                boolean onCreateActionMode = this.mCallback.onCreateActionMode(this, this.mMenu);
                this.mMenu.startDispatchingItemsChanged();
                return onCreateActionMode;
            } catch (Throwable th) {
                this.mMenu.startDispatchingItemsChanged();
                throw th;
            }
        }

        @Override // android.view.ActionMode
        public void setCustomView(View view) {
            ActionBarImpl.this.mContextView.setCustomView(view);
            this.mCustomView = new WeakReference<>(view);
        }

        @Override // android.view.ActionMode
        public void setSubtitle(CharSequence subtitle) {
            ActionBarImpl.this.mContextView.setSubtitle(subtitle);
        }

        @Override // android.view.ActionMode
        public void setTitle(CharSequence title) {
            ActionBarImpl.this.mContextView.setTitle(title);
        }

        @Override // android.view.ActionMode
        public void setTitle(int resId) {
            setTitle(ActionBarImpl.this.mContext.getResources().getString(resId));
        }

        @Override // android.view.ActionMode
        public void setSubtitle(int resId) {
            setSubtitle(ActionBarImpl.this.mContext.getResources().getString(resId));
        }

        @Override // android.view.ActionMode
        public CharSequence getTitle() {
            return ActionBarImpl.this.mContextView.getTitle();
        }

        @Override // android.view.ActionMode
        public CharSequence getSubtitle() {
            return ActionBarImpl.this.mContextView.getSubtitle();
        }

        @Override // android.view.ActionMode
        public void setTitleOptionalHint(boolean titleOptional) {
            super.setTitleOptionalHint(titleOptional);
            ActionBarImpl.this.mContextView.setTitleOptional(titleOptional);
        }

        @Override // android.view.ActionMode
        public boolean isTitleOptional() {
            return ActionBarImpl.this.mContextView.isTitleOptional();
        }

        @Override // android.view.ActionMode
        public View getCustomView() {
            if (this.mCustomView != null) {
                return this.mCustomView.get();
            }
            return null;
        }

        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            if (this.mCallback != null) {
                return this.mCallback.onActionItemClicked(this, item);
            }
            return false;
        }

        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
            if (this.mCallback == null) {
                return false;
            }
            if (!subMenu.hasVisibleItems()) {
                return true;
            }
            new MenuPopupHelper(ActionBarImpl.this.getThemedContext(), subMenu).show();
            return true;
        }

        public void onCloseSubMenu(SubMenuBuilder menu) {
        }

        public void onMenuModeChange(MenuBuilder menu) {
            if (this.mCallback == null) {
                return;
            }
            invalidate();
            ActionBarImpl.this.mContextView.showOverflowMenu();
        }
    }

    /* loaded from: ActionBarImpl$TabImpl.class */
    public class TabImpl extends ActionBar.Tab {
        private ActionBar.TabListener mCallback;
        private Object mTag;
        private Drawable mIcon;
        private CharSequence mText;
        private CharSequence mContentDesc;
        private int mPosition = -1;
        private View mCustomView;

        public TabImpl() {
        }

        @Override // android.app.ActionBar.Tab
        public Object getTag() {
            return this.mTag;
        }

        @Override // android.app.ActionBar.Tab
        public ActionBar.Tab setTag(Object tag) {
            this.mTag = tag;
            return this;
        }

        public ActionBar.TabListener getCallback() {
            return this.mCallback;
        }

        @Override // android.app.ActionBar.Tab
        public ActionBar.Tab setTabListener(ActionBar.TabListener callback) {
            this.mCallback = callback;
            return this;
        }

        @Override // android.app.ActionBar.Tab
        public View getCustomView() {
            return this.mCustomView;
        }

        @Override // android.app.ActionBar.Tab
        public ActionBar.Tab setCustomView(View view) {
            this.mCustomView = view;
            if (this.mPosition >= 0) {
                ActionBarImpl.this.mTabScrollView.updateTab(this.mPosition);
            }
            return this;
        }

        @Override // android.app.ActionBar.Tab
        public ActionBar.Tab setCustomView(int layoutResId) {
            return setCustomView(LayoutInflater.from(ActionBarImpl.this.getThemedContext()).inflate(layoutResId, (ViewGroup) null));
        }

        @Override // android.app.ActionBar.Tab
        public Drawable getIcon() {
            return this.mIcon;
        }

        @Override // android.app.ActionBar.Tab
        public int getPosition() {
            return this.mPosition;
        }

        public void setPosition(int position) {
            this.mPosition = position;
        }

        @Override // android.app.ActionBar.Tab
        public CharSequence getText() {
            return this.mText;
        }

        @Override // android.app.ActionBar.Tab
        public ActionBar.Tab setIcon(Drawable icon) {
            this.mIcon = icon;
            if (this.mPosition >= 0) {
                ActionBarImpl.this.mTabScrollView.updateTab(this.mPosition);
            }
            return this;
        }

        @Override // android.app.ActionBar.Tab
        public ActionBar.Tab setIcon(int resId) {
            return setIcon(ActionBarImpl.this.mContext.getResources().getDrawable(resId));
        }

        @Override // android.app.ActionBar.Tab
        public ActionBar.Tab setText(CharSequence text) {
            this.mText = text;
            if (this.mPosition >= 0) {
                ActionBarImpl.this.mTabScrollView.updateTab(this.mPosition);
            }
            return this;
        }

        @Override // android.app.ActionBar.Tab
        public ActionBar.Tab setText(int resId) {
            return setText(ActionBarImpl.this.mContext.getResources().getText(resId));
        }

        @Override // android.app.ActionBar.Tab
        public void select() {
            ActionBarImpl.this.selectTab(this);
        }

        @Override // android.app.ActionBar.Tab
        public ActionBar.Tab setContentDescription(int resId) {
            return setContentDescription(ActionBarImpl.this.mContext.getResources().getText(resId));
        }

        @Override // android.app.ActionBar.Tab
        public ActionBar.Tab setContentDescription(CharSequence contentDesc) {
            this.mContentDesc = contentDesc;
            if (this.mPosition >= 0) {
                ActionBarImpl.this.mTabScrollView.updateTab(this.mPosition);
            }
            return this;
        }

        @Override // android.app.ActionBar.Tab
        public CharSequence getContentDescription() {
            return this.mContentDesc;
        }
    }

    @Override // android.app.ActionBar
    public void setCustomView(View view) {
        this.mActionView.setCustomNavigationView(view);
    }

    @Override // android.app.ActionBar
    public void setCustomView(View view, ActionBar.LayoutParams layoutParams) {
        view.setLayoutParams(layoutParams);
        this.mActionView.setCustomNavigationView(view);
    }

    @Override // android.app.ActionBar
    public void setListNavigationCallbacks(SpinnerAdapter adapter, ActionBar.OnNavigationListener callback) {
        this.mActionView.setDropdownAdapter(adapter);
        this.mActionView.setCallback(callback);
    }

    @Override // android.app.ActionBar
    public int getSelectedNavigationIndex() {
        switch (this.mActionView.getNavigationMode()) {
            case 1:
                return this.mActionView.getDropdownSelectedPosition();
            case 2:
                if (this.mSelectedTab != null) {
                    return this.mSelectedTab.getPosition();
                }
                return -1;
            default:
                return -1;
        }
    }

    @Override // android.app.ActionBar
    public int getNavigationItemCount() {
        switch (this.mActionView.getNavigationMode()) {
            case 1:
                SpinnerAdapter adapter = this.mActionView.getDropdownAdapter();
                if (adapter != null) {
                    return adapter.getCount();
                }
                return 0;
            case 2:
                return this.mTabs.size();
            default:
                return 0;
        }
    }

    @Override // android.app.ActionBar
    public int getTabCount() {
        return this.mTabs.size();
    }

    @Override // android.app.ActionBar
    public void setNavigationMode(int mode) {
        int oldMode = this.mActionView.getNavigationMode();
        switch (oldMode) {
            case 2:
                this.mSavedTabPosition = getSelectedNavigationIndex();
                selectTab(null);
                this.mTabScrollView.setVisibility(8);
                break;
        }
        if (oldMode != mode && !this.mHasEmbeddedTabs && this.mOverlayLayout != null) {
            this.mOverlayLayout.requestFitSystemWindows();
        }
        this.mActionView.setNavigationMode(mode);
        switch (mode) {
            case 2:
                ensureTabsExist();
                this.mTabScrollView.setVisibility(0);
                if (this.mSavedTabPosition != -1) {
                    setSelectedNavigationItem(this.mSavedTabPosition);
                    this.mSavedTabPosition = -1;
                    break;
                }
                break;
        }
        this.mActionView.setCollapsable(mode == 2 && !this.mHasEmbeddedTabs);
    }

    @Override // android.app.ActionBar
    public ActionBar.Tab getTabAt(int index) {
        return this.mTabs.get(index);
    }

    @Override // android.app.ActionBar
    public void setIcon(int resId) {
        this.mActionView.setIcon(resId);
    }

    @Override // android.app.ActionBar
    public void setIcon(Drawable icon) {
        this.mActionView.setIcon(icon);
    }

    public boolean hasIcon() {
        return this.mActionView.hasIcon();
    }

    @Override // android.app.ActionBar
    public void setLogo(int resId) {
        this.mActionView.setLogo(resId);
    }

    @Override // android.app.ActionBar
    public void setLogo(Drawable logo) {
        this.mActionView.setLogo(logo);
    }

    public boolean hasLogo() {
        return this.mActionView.hasLogo();
    }

    public void setDefaultDisplayHomeAsUpEnabled(boolean enable) {
        if (!this.mDisplayHomeAsUpSet) {
            setDisplayHomeAsUpEnabled(enable);
        }
    }
}