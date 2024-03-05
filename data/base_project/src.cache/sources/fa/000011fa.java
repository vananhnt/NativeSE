package android.support.v7.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.appcompat.R;
import android.support.v7.internal.app.ToolbarActionBar;
import android.support.v7.internal.app.WindowCallback;
import android.support.v7.internal.app.WindowDecorActionBar;
import android.support.v7.internal.view.StandaloneActionMode;
import android.support.v7.internal.view.menu.ListMenuPresenter;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.internal.view.menu.MenuView;
import android.support.v7.internal.widget.ActionBarContextView;
import android.support.v7.internal.widget.DecorContentParent;
import android.support.v7.internal.widget.FitWindowsViewGroup;
import android.support.v7.internal.widget.ProgressBarCompat;
import android.support.v7.internal.widget.TintCheckBox;
import android.support.v7.internal.widget.TintCheckedTextView;
import android.support.v7.internal.widget.TintEditText;
import android.support.v7.internal.widget.TintRadioButton;
import android.support.v7.internal.widget.TintSpinner;
import android.support.v7.internal.widget.ViewStubCompat;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ActionBarActivityDelegateBase.class */
public class ActionBarActivityDelegateBase extends ActionBarActivityDelegate implements MenuBuilder.Callback {
    private static final String TAG = "ActionBarActivityDelegateBase";
    private ActionMenuPresenterCallback mActionMenuPresenterCallback;
    ActionMode mActionMode;
    PopupWindow mActionModePopup;
    ActionBarContextView mActionModeView;
    private boolean mClosingActionMenu;
    private DecorContentParent mDecorContentParent;
    private boolean mEnableDefaultActionBarUp;
    private boolean mFeatureIndeterminateProgress;
    private boolean mFeatureProgress;
    private int mInvalidatePanelMenuFeatures;
    private boolean mInvalidatePanelMenuPosted;
    private final Runnable mInvalidatePanelMenuRunnable;
    private PanelMenuPresenterCallback mPanelMenuPresenterCallback;
    private PanelFeatureState[] mPanels;
    private PanelFeatureState mPreparedPanel;
    Runnable mShowActionModePopup;
    private View mStatusGuard;
    private ViewGroup mSubDecor;
    private boolean mSubDecorInstalled;
    private Rect mTempRect1;
    private Rect mTempRect2;
    private CharSequence mTitleToSet;
    private ListMenuPresenter mToolbarListMenuPresenter;
    private ViewGroup mWindowDecor;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActionBarActivityDelegateBase$ActionMenuPresenterCallback.class */
    public final class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        final ActionBarActivityDelegateBase this$0;

        private ActionMenuPresenterCallback(ActionBarActivityDelegateBase actionBarActivityDelegateBase) {
            this.this$0 = actionBarActivityDelegateBase;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
            this.this$0.checkCloseActionMenu(menuBuilder);
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            WindowCallback windowCallback = this.this$0.getWindowCallback();
            if (windowCallback != null) {
                windowCallback.onMenuOpened(8, menuBuilder);
                return true;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActionBarActivityDelegateBase$ActionModeCallbackWrapper.class */
    public class ActionModeCallbackWrapper implements ActionMode.Callback {
        private ActionMode.Callback mWrapped;
        final ActionBarActivityDelegateBase this$0;

        public ActionModeCallbackWrapper(ActionBarActivityDelegateBase actionBarActivityDelegateBase, ActionMode.Callback callback) {
            this.this$0 = actionBarActivityDelegateBase;
            this.mWrapped = callback;
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return this.mWrapped.onActionItemClicked(actionMode, menuItem);
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return this.mWrapped.onCreateActionMode(actionMode, menu);
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            this.mWrapped.onDestroyActionMode(actionMode);
            if (this.this$0.mActionModePopup != null) {
                this.this$0.mActivity.getWindow().getDecorView().removeCallbacks(this.this$0.mShowActionModePopup);
                this.this$0.mActionModePopup.dismiss();
            } else if (this.this$0.mActionModeView != null) {
                this.this$0.mActionModeView.setVisibility(8);
                if (this.this$0.mActionModeView.getParent() != null) {
                    ViewCompat.requestApplyInsets((View) this.this$0.mActionModeView.getParent());
                }
            }
            if (this.this$0.mActionModeView != null) {
                this.this$0.mActionModeView.removeAllViews();
            }
            if (this.this$0.mActivity != null) {
                try {
                    this.this$0.mActivity.onSupportActionModeFinished(this.this$0.mActionMode);
                } catch (AbstractMethodError e) {
                }
            }
            this.this$0.mActionMode = null;
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(actionMode, menu);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActionBarActivityDelegateBase$PanelFeatureState.class */
    public static final class PanelFeatureState {
        ViewGroup decorView;
        int featureId;
        Bundle frozenActionViewState;
        Bundle frozenMenuState;
        boolean isHandled;
        boolean isOpen;
        boolean isPrepared;
        ListMenuPresenter listMenuPresenter;
        Context listPresenterContext;
        MenuBuilder menu;
        public boolean qwertyMode;
        boolean refreshDecorView = false;
        boolean refreshMenuContent;
        View shownPanelView;
        boolean wasLastOpen;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: ActionBarActivityDelegateBase$PanelFeatureState$SavedState.class */
        public static class SavedState implements Parcelable {
            public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.support.v7.app.ActionBarActivityDelegateBase.PanelFeatureState.SavedState.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.os.Parcelable.Creator
                public SavedState createFromParcel(Parcel parcel) {
                    return SavedState.readFromParcel(parcel);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.os.Parcelable.Creator
                public SavedState[] newArray(int i) {
                    return new SavedState[i];
                }
            };
            int featureId;
            boolean isOpen;
            Bundle menuState;

            private SavedState() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public static SavedState readFromParcel(Parcel parcel) {
                SavedState savedState = new SavedState();
                savedState.featureId = parcel.readInt();
                boolean z = true;
                if (parcel.readInt() != 1) {
                    z = false;
                }
                savedState.isOpen = z;
                if (savedState.isOpen) {
                    savedState.menuState = parcel.readBundle();
                }
                return savedState;
            }

            @Override // android.os.Parcelable
            public int describeContents() {
                return 0;
            }

            @Override // android.os.Parcelable
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(this.featureId);
                parcel.writeInt(this.isOpen ? 1 : 0);
                if (this.isOpen) {
                    parcel.writeBundle(this.menuState);
                }
            }
        }

        PanelFeatureState(int i) {
            this.featureId = i;
        }

        void applyFrozenState() {
            Bundle bundle;
            MenuBuilder menuBuilder = this.menu;
            if (menuBuilder == null || (bundle = this.frozenMenuState) == null) {
                return;
            }
            menuBuilder.restorePresenterStates(bundle);
            this.frozenMenuState = null;
        }

        public void clearMenuPresenters() {
            MenuBuilder menuBuilder = this.menu;
            if (menuBuilder != null) {
                menuBuilder.removeMenuPresenter(this.listMenuPresenter);
            }
            this.listMenuPresenter = null;
        }

        MenuView getListMenuView(MenuPresenter.Callback callback) {
            if (this.menu == null) {
                return null;
            }
            if (this.listMenuPresenter == null) {
                this.listMenuPresenter = new ListMenuPresenter(this.listPresenterContext, R.layout.abc_list_menu_item_layout);
                this.listMenuPresenter.setCallback(callback);
                this.menu.addMenuPresenter(this.listMenuPresenter);
            }
            return this.listMenuPresenter.getMenuView(this.decorView);
        }

        public boolean hasPanelItems() {
            boolean z = false;
            if (this.shownPanelView == null) {
                return false;
            }
            if (this.listMenuPresenter.getAdapter().getCount() > 0) {
                z = true;
            }
            return z;
        }

        void onRestoreInstanceState(Parcelable parcelable) {
            SavedState savedState = (SavedState) parcelable;
            this.featureId = savedState.featureId;
            this.wasLastOpen = savedState.isOpen;
            this.frozenMenuState = savedState.menuState;
            this.shownPanelView = null;
            this.decorView = null;
        }

        Parcelable onSaveInstanceState() {
            SavedState savedState = new SavedState();
            savedState.featureId = this.featureId;
            savedState.isOpen = this.isOpen;
            if (this.menu != null) {
                savedState.menuState = new Bundle();
                this.menu.savePresenterStates(savedState.menuState);
            }
            return savedState;
        }

        void setMenu(MenuBuilder menuBuilder) {
            ListMenuPresenter listMenuPresenter;
            MenuBuilder menuBuilder2 = this.menu;
            if (menuBuilder == menuBuilder2) {
                return;
            }
            if (menuBuilder2 != null) {
                menuBuilder2.removeMenuPresenter(this.listMenuPresenter);
            }
            this.menu = menuBuilder;
            if (menuBuilder == null || (listMenuPresenter = this.listMenuPresenter) == null) {
                return;
            }
            menuBuilder.addMenuPresenter(listMenuPresenter);
        }

        void setStyle(Context context) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme newTheme = context.getResources().newTheme();
            newTheme.setTo(context.getTheme());
            newTheme.resolveAttribute(R.attr.actionBarPopupTheme, typedValue, true);
            if (typedValue.resourceId != 0) {
                newTheme.applyStyle(typedValue.resourceId, true);
            }
            newTheme.resolveAttribute(R.attr.panelMenuListTheme, typedValue, true);
            if (typedValue.resourceId != 0) {
                newTheme.applyStyle(typedValue.resourceId, true);
            } else {
                newTheme.applyStyle(R.style.Theme_AppCompat_CompactMenu, true);
            }
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, 0);
            contextThemeWrapper.getTheme().setTo(newTheme);
            this.listPresenterContext = contextThemeWrapper;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActionBarActivityDelegateBase$PanelMenuPresenterCallback.class */
    public final class PanelMenuPresenterCallback implements MenuPresenter.Callback {
        final ActionBarActivityDelegateBase this$0;

        private PanelMenuPresenterCallback(ActionBarActivityDelegateBase actionBarActivityDelegateBase) {
            this.this$0 = actionBarActivityDelegateBase;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
            MenuBuilder rootMenu = menuBuilder.getRootMenu();
            boolean z2 = rootMenu != menuBuilder;
            ActionBarActivityDelegateBase actionBarActivityDelegateBase = this.this$0;
            if (z2) {
                menuBuilder = rootMenu;
            }
            PanelFeatureState findMenuPanel = actionBarActivityDelegateBase.findMenuPanel(menuBuilder);
            if (findMenuPanel != null) {
                if (z2) {
                    this.this$0.callOnPanelClosed(findMenuPanel.featureId, findMenuPanel, rootMenu);
                    this.this$0.closePanel(findMenuPanel, true);
                    return;
                }
                this.this$0.mActivity.closeOptionsMenu();
                this.this$0.closePanel(findMenuPanel, z);
            }
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            WindowCallback windowCallback;
            if (menuBuilder != null || !this.this$0.mHasActionBar || (windowCallback = this.this$0.getWindowCallback()) == null || this.this$0.isDestroyed()) {
                return true;
            }
            windowCallback.onMenuOpened(8, menuBuilder);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActionBarActivityDelegateBase(ActionBarActivity actionBarActivity) {
        super(actionBarActivity);
        this.mInvalidatePanelMenuRunnable = new Runnable(this) { // from class: android.support.v7.app.ActionBarActivityDelegateBase.1
            final ActionBarActivityDelegateBase this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                if ((this.this$0.mInvalidatePanelMenuFeatures & 1) != 0) {
                    this.this$0.doInvalidatePanelMenu(0);
                }
                if ((this.this$0.mInvalidatePanelMenuFeatures & 256) != 0) {
                    this.this$0.doInvalidatePanelMenu(8);
                }
                this.this$0.mInvalidatePanelMenuPosted = false;
                this.this$0.mInvalidatePanelMenuFeatures = 0;
            }
        };
    }

    private void applyFixedSizeWindow() {
        TypedArray obtainStyledAttributes = this.mActivity.obtainStyledAttributes(R.styleable.Theme);
        TypedValue typedValue = null;
        TypedValue typedValue2 = null;
        TypedValue typedValue3 = null;
        TypedValue typedValue4 = null;
        if (obtainStyledAttributes.hasValue(R.styleable.Theme_windowFixedWidthMajor)) {
            typedValue = null;
            if (0 == 0) {
                typedValue = new TypedValue();
            }
            obtainStyledAttributes.getValue(R.styleable.Theme_windowFixedWidthMajor, typedValue);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.Theme_windowFixedWidthMinor)) {
            if (0 == 0) {
                typedValue2 = new TypedValue();
            }
            obtainStyledAttributes.getValue(R.styleable.Theme_windowFixedWidthMinor, typedValue2);
        } else {
            typedValue2 = null;
        }
        if (obtainStyledAttributes.hasValue(R.styleable.Theme_windowFixedHeightMajor)) {
            typedValue3 = null;
            if (0 == 0) {
                typedValue3 = new TypedValue();
            }
            obtainStyledAttributes.getValue(R.styleable.Theme_windowFixedHeightMajor, typedValue3);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.Theme_windowFixedHeightMinor)) {
            typedValue4 = null;
            if (0 == 0) {
                typedValue4 = new TypedValue();
            }
            obtainStyledAttributes.getValue(R.styleable.Theme_windowFixedHeightMinor, typedValue4);
        }
        DisplayMetrics displayMetrics = this.mActivity.getResources().getDisplayMetrics();
        boolean z = displayMetrics.widthPixels < displayMetrics.heightPixels;
        int i = -1;
        if (z) {
            typedValue = typedValue2;
        }
        if (typedValue != null && typedValue.type != 0) {
            if (typedValue.type == 5) {
                i = (int) typedValue.getDimension(displayMetrics);
            } else if (typedValue.type == 6) {
                i = (int) typedValue.getFraction(displayMetrics.widthPixels, displayMetrics.widthPixels);
            }
        }
        if (!z) {
            typedValue3 = typedValue4;
        }
        int dimension = (typedValue3 == null || typedValue3.type == 0) ? -1 : typedValue3.type == 5 ? (int) typedValue3.getDimension(displayMetrics) : typedValue3.type == 6 ? (int) typedValue3.getFraction(displayMetrics.heightPixels, displayMetrics.heightPixels) : -1;
        if (i != -1 || dimension != -1) {
            this.mActivity.getWindow().setLayout(i, dimension);
        }
        obtainStyledAttributes.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callOnPanelClosed(int i, PanelFeatureState panelFeatureState, Menu menu) {
        if (menu == null) {
            if (panelFeatureState == null && i >= 0) {
                PanelFeatureState[] panelFeatureStateArr = this.mPanels;
                if (i < panelFeatureStateArr.length) {
                    panelFeatureState = panelFeatureStateArr[i];
                }
            }
            if (panelFeatureState != null) {
                menu = panelFeatureState.menu;
            }
        }
        if (panelFeatureState == null || panelFeatureState.isOpen) {
            getWindowCallback().onPanelClosed(i, menu);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkCloseActionMenu(MenuBuilder menuBuilder) {
        if (this.mClosingActionMenu) {
            return;
        }
        this.mClosingActionMenu = true;
        this.mDecorContentParent.dismissPopups();
        WindowCallback windowCallback = getWindowCallback();
        if (windowCallback != null && !isDestroyed()) {
            windowCallback.onPanelClosed(8, menuBuilder);
        }
        this.mClosingActionMenu = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closePanel(PanelFeatureState panelFeatureState, boolean z) {
        DecorContentParent decorContentParent;
        if (z && panelFeatureState.featureId == 0 && (decorContentParent = this.mDecorContentParent) != null && decorContentParent.isOverflowMenuShowing()) {
            checkCloseActionMenu(panelFeatureState.menu);
            return;
        }
        if (panelFeatureState.isOpen && z) {
            callOnPanelClosed(panelFeatureState.featureId, panelFeatureState, null);
        }
        panelFeatureState.isPrepared = false;
        panelFeatureState.isHandled = false;
        panelFeatureState.isOpen = false;
        panelFeatureState.shownPanelView = null;
        panelFeatureState.refreshDecorView = true;
        if (this.mPreparedPanel == panelFeatureState) {
            this.mPreparedPanel = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doInvalidatePanelMenu(int i) {
        PanelFeatureState panelState;
        PanelFeatureState panelState2 = getPanelState(i, true);
        if (panelState2.menu != null) {
            Bundle bundle = new Bundle();
            panelState2.menu.saveActionViewStates(bundle);
            if (bundle.size() > 0) {
                panelState2.frozenActionViewState = bundle;
            }
            panelState2.menu.stopDispatchingItemsChanged();
            panelState2.menu.clear();
        }
        panelState2.refreshMenuContent = true;
        panelState2.refreshDecorView = true;
        if ((i != 8 && i != 0) || this.mDecorContentParent == null || (panelState = getPanelState(0, false)) == null) {
            return;
        }
        panelState.isPrepared = false;
        preparePanel(panelState, null);
    }

    private void ensureToolbarListMenuPresenter() {
        if (this.mToolbarListMenuPresenter == null) {
            TypedValue typedValue = new TypedValue();
            this.mActivity.getTheme().resolveAttribute(R.attr.panelMenuListTheme, typedValue, true);
            this.mToolbarListMenuPresenter = new ListMenuPresenter(new ContextThemeWrapper(this.mActivity, typedValue.resourceId != 0 ? typedValue.resourceId : R.style.Theme_AppCompat_CompactMenu), R.layout.abc_list_menu_item_layout);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PanelFeatureState findMenuPanel(Menu menu) {
        PanelFeatureState[] panelFeatureStateArr = this.mPanels;
        int length = panelFeatureStateArr != null ? panelFeatureStateArr.length : 0;
        for (int i = 0; i < length; i++) {
            PanelFeatureState panelFeatureState = panelFeatureStateArr[i];
            if (panelFeatureState != null && panelFeatureState.menu == menu) {
                return panelFeatureState;
            }
        }
        return null;
    }

    private ProgressBarCompat getCircularProgressBar() {
        ProgressBarCompat progressBarCompat = (ProgressBarCompat) this.mActivity.findViewById(R.id.progress_circular);
        if (progressBarCompat != null) {
            progressBarCompat.setVisibility(4);
        }
        return progressBarCompat;
    }

    private ProgressBarCompat getHorizontalProgressBar() {
        ProgressBarCompat progressBarCompat = (ProgressBarCompat) this.mActivity.findViewById(R.id.progress_horizontal);
        if (progressBarCompat != null) {
            progressBarCompat.setVisibility(4);
        }
        return progressBarCompat;
    }

    private PanelFeatureState getPanelState(int i, boolean z) {
        PanelFeatureState[] panelFeatureStateArr = this.mPanels;
        PanelFeatureState[] panelFeatureStateArr2 = panelFeatureStateArr;
        if (panelFeatureStateArr == null || panelFeatureStateArr2.length <= i) {
            PanelFeatureState[] panelFeatureStateArr3 = new PanelFeatureState[i + 1];
            if (panelFeatureStateArr2 != null) {
                System.arraycopy(panelFeatureStateArr2, 0, panelFeatureStateArr3, 0, panelFeatureStateArr2.length);
            }
            panelFeatureStateArr2 = panelFeatureStateArr3;
            this.mPanels = panelFeatureStateArr3;
        }
        PanelFeatureState panelFeatureState = panelFeatureStateArr2[i];
        if (panelFeatureState == null) {
            PanelFeatureState panelFeatureState2 = new PanelFeatureState(i);
            panelFeatureState = panelFeatureState2;
            panelFeatureStateArr2[i] = panelFeatureState2;
        }
        return panelFeatureState;
    }

    private void hideProgressBars(ProgressBarCompat progressBarCompat, ProgressBarCompat progressBarCompat2) {
        if (this.mFeatureIndeterminateProgress && progressBarCompat2.getVisibility() == 0) {
            progressBarCompat2.setVisibility(4);
        }
        if (this.mFeatureProgress && progressBarCompat.getVisibility() == 0) {
            progressBarCompat.setVisibility(4);
        }
    }

    private boolean initializePanelContent(PanelFeatureState panelFeatureState) {
        boolean z = false;
        if (panelFeatureState.menu == null) {
            return false;
        }
        if (this.mPanelMenuPresenterCallback == null) {
            this.mPanelMenuPresenterCallback = new PanelMenuPresenterCallback();
        }
        panelFeatureState.shownPanelView = (View) panelFeatureState.getListMenuView(this.mPanelMenuPresenterCallback);
        if (panelFeatureState.shownPanelView != null) {
            z = true;
        }
        return z;
    }

    private void initializePanelDecor(PanelFeatureState panelFeatureState) {
        panelFeatureState.decorView = this.mWindowDecor;
        panelFeatureState.setStyle(getActionBarThemedContext());
    }

    private boolean initializePanelMenu(PanelFeatureState panelFeatureState) {
        Context context;
        Context context2 = this.mActivity;
        if ((panelFeatureState.featureId == 0 || panelFeatureState.featureId == 8) && this.mDecorContentParent != null) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context2.getTheme();
            theme.resolveAttribute(R.attr.actionBarTheme, typedValue, true);
            Resources.Theme theme2 = null;
            if (typedValue.resourceId != 0) {
                theme2 = context2.getResources().newTheme();
                theme2.setTo(theme);
                theme2.applyStyle(typedValue.resourceId, true);
                theme2.resolveAttribute(R.attr.actionBarWidgetTheme, typedValue, true);
            } else {
                theme.resolveAttribute(R.attr.actionBarWidgetTheme, typedValue, true);
            }
            if (typedValue.resourceId != 0) {
                if (theme2 == null) {
                    theme2 = context2.getResources().newTheme();
                    theme2.setTo(theme);
                }
                theme2.applyStyle(typedValue.resourceId, true);
            }
            if (theme2 != null) {
                Context contextThemeWrapper = new ContextThemeWrapper(context2, 0);
                contextThemeWrapper.getTheme().setTo(theme2);
                context = contextThemeWrapper;
            } else {
                context = context2;
            }
        } else {
            context = context2;
        }
        MenuBuilder menuBuilder = new MenuBuilder(context);
        menuBuilder.setCallback(this);
        panelFeatureState.setMenu(menuBuilder);
        return true;
    }

    private void invalidatePanelMenu(int i) {
        ViewGroup viewGroup;
        this.mInvalidatePanelMenuFeatures |= 1 << i;
        if (this.mInvalidatePanelMenuPosted || (viewGroup = this.mWindowDecor) == null) {
            return;
        }
        ViewCompat.postOnAnimation(viewGroup, this.mInvalidatePanelMenuRunnable);
        this.mInvalidatePanelMenuPosted = true;
    }

    private void openPanel(int i, KeyEvent keyEvent) {
        DecorContentParent decorContentParent;
        if (i != 0 || (decorContentParent = this.mDecorContentParent) == null || !decorContentParent.canShowOverflowMenu() || ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(this.mActivity))) {
            openPanel(getPanelState(i, true), keyEvent);
        } else {
            this.mDecorContentParent.showOverflowMenu();
        }
    }

    private void openPanel(PanelFeatureState panelFeatureState, KeyEvent keyEvent) {
        if (panelFeatureState.isOpen || isDestroyed()) {
            return;
        }
        if (panelFeatureState.featureId == 0) {
            ActionBarActivity actionBarActivity = this.mActivity;
            boolean z = (actionBarActivity.getResources().getConfiguration().screenLayout & 15) == 4;
            boolean z2 = actionBarActivity.getApplicationInfo().targetSdkVersion >= 11;
            if (z && z2) {
                return;
            }
        }
        WindowCallback windowCallback = getWindowCallback();
        if (windowCallback != null && !windowCallback.onMenuOpened(panelFeatureState.featureId, panelFeatureState.menu)) {
            closePanel(panelFeatureState, true);
        } else if (preparePanel(panelFeatureState, keyEvent)) {
            if (panelFeatureState.decorView == null || panelFeatureState.refreshDecorView) {
                initializePanelDecor(panelFeatureState);
            }
            if (initializePanelContent(panelFeatureState) && panelFeatureState.hasPanelItems()) {
                panelFeatureState.isHandled = false;
                panelFeatureState.isOpen = true;
            }
        }
    }

    private boolean preparePanel(PanelFeatureState panelFeatureState, KeyEvent keyEvent) {
        DecorContentParent decorContentParent;
        DecorContentParent decorContentParent2;
        DecorContentParent decorContentParent3;
        if (isDestroyed()) {
            return false;
        }
        if (panelFeatureState.isPrepared) {
            return true;
        }
        PanelFeatureState panelFeatureState2 = this.mPreparedPanel;
        if (panelFeatureState2 != null && panelFeatureState2 != panelFeatureState) {
            closePanel(panelFeatureState2, false);
        }
        boolean z = panelFeatureState.featureId == 0 || panelFeatureState.featureId == 8;
        if (z && (decorContentParent3 = this.mDecorContentParent) != null) {
            decorContentParent3.setMenuPrepared();
        }
        if (panelFeatureState.menu == null || panelFeatureState.refreshMenuContent) {
            if (panelFeatureState.menu == null && (!initializePanelMenu(panelFeatureState) || panelFeatureState.menu == null)) {
                return false;
            }
            if (z && this.mDecorContentParent != null) {
                if (this.mActionMenuPresenterCallback == null) {
                    this.mActionMenuPresenterCallback = new ActionMenuPresenterCallback();
                }
                this.mDecorContentParent.setMenu(panelFeatureState.menu, this.mActionMenuPresenterCallback);
            }
            panelFeatureState.menu.stopDispatchingItemsChanged();
            if (!getWindowCallback().onCreatePanelMenu(panelFeatureState.featureId, panelFeatureState.menu)) {
                panelFeatureState.setMenu(null);
                if (!z || (decorContentParent = this.mDecorContentParent) == null) {
                    return false;
                }
                decorContentParent.setMenu(null, this.mActionMenuPresenterCallback);
                return false;
            }
            panelFeatureState.refreshMenuContent = false;
        }
        panelFeatureState.menu.stopDispatchingItemsChanged();
        if (panelFeatureState.frozenActionViewState != null) {
            panelFeatureState.menu.restoreActionViewStates(panelFeatureState.frozenActionViewState);
            panelFeatureState.frozenActionViewState = null;
        }
        if (!getWindowCallback().onPreparePanel(0, null, panelFeatureState.menu)) {
            if (z && (decorContentParent2 = this.mDecorContentParent) != null) {
                decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
            }
            panelFeatureState.menu.startDispatchingItemsChanged();
            return false;
        }
        panelFeatureState.qwertyMode = KeyCharacterMap.load(keyEvent != null ? keyEvent.getDeviceId() : -1).getKeyboardType() != 1;
        panelFeatureState.menu.setQwertyMode(panelFeatureState.qwertyMode);
        panelFeatureState.menu.startDispatchingItemsChanged();
        panelFeatureState.isPrepared = true;
        panelFeatureState.isHandled = false;
        this.mPreparedPanel = panelFeatureState;
        return true;
    }

    private void reopenMenu(MenuBuilder menuBuilder, boolean z) {
        DecorContentParent decorContentParent = this.mDecorContentParent;
        if (decorContentParent == null || !decorContentParent.canShowOverflowMenu() || (ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(this.mActivity)) && !this.mDecorContentParent.isOverflowMenuShowPending())) {
            PanelFeatureState panelState = getPanelState(0, true);
            panelState.refreshDecorView = true;
            closePanel(panelState, false);
            openPanel(panelState, (KeyEvent) null);
            return;
        }
        WindowCallback windowCallback = getWindowCallback();
        if (this.mDecorContentParent.isOverflowMenuShowing() && z) {
            this.mDecorContentParent.hideOverflowMenu();
            if (isDestroyed()) {
                return;
            }
            this.mActivity.onPanelClosed(8, getPanelState(0, true).menu);
        } else if (windowCallback == null || isDestroyed()) {
        } else {
            if (this.mInvalidatePanelMenuPosted && (this.mInvalidatePanelMenuFeatures & 1) != 0) {
                this.mWindowDecor.removeCallbacks(this.mInvalidatePanelMenuRunnable);
                this.mInvalidatePanelMenuRunnable.run();
            }
            PanelFeatureState panelState2 = getPanelState(0, true);
            if (panelState2.menu == null || panelState2.refreshMenuContent || !windowCallback.onPreparePanel(0, null, panelState2.menu)) {
                return;
            }
            windowCallback.onMenuOpened(8, panelState2.menu);
            this.mDecorContentParent.showOverflowMenu();
        }
    }

    private void showProgressBars(ProgressBarCompat progressBarCompat, ProgressBarCompat progressBarCompat2) {
        if (this.mFeatureIndeterminateProgress && progressBarCompat2.getVisibility() == 4) {
            progressBarCompat2.setVisibility(0);
        }
        if (!this.mFeatureProgress || progressBarCompat.getProgress() >= 10000) {
            return;
        }
        progressBarCompat.setVisibility(0);
    }

    private void updateProgressBars(int i) {
        ProgressBarCompat circularProgressBar = getCircularProgressBar();
        ProgressBarCompat horizontalProgressBar = getHorizontalProgressBar();
        if (i == -1) {
            if (this.mFeatureProgress) {
                horizontalProgressBar.setVisibility((horizontalProgressBar.isIndeterminate() || horizontalProgressBar.getProgress() < 10000) ? 0 : 4);
            }
            if (this.mFeatureIndeterminateProgress) {
                circularProgressBar.setVisibility(0);
            }
        } else if (i == -2) {
            if (this.mFeatureProgress) {
                horizontalProgressBar.setVisibility(8);
            }
            if (this.mFeatureIndeterminateProgress) {
                circularProgressBar.setVisibility(8);
            }
        } else if (i == -3) {
            horizontalProgressBar.setIndeterminate(true);
        } else if (i == -4) {
            horizontalProgressBar.setIndeterminate(false);
        } else if (i < 0 || i > 10000) {
        } else {
            horizontalProgressBar.setProgress(i + 0);
            if (i < 10000) {
                showProgressBars(horizontalProgressBar, circularProgressBar);
            } else {
                hideProgressBars(horizontalProgressBar, circularProgressBar);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int updateStatusGuard(int i) {
        boolean z = false;
        ActionBarContextView actionBarContextView = this.mActionModeView;
        if (actionBarContextView == null) {
            z = false;
        } else if (actionBarContextView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mActionModeView.getLayoutParams();
            boolean z2 = false;
            if (this.mActionModeView.isShown()) {
                if (this.mTempRect1 == null) {
                    this.mTempRect1 = new Rect();
                    this.mTempRect2 = new Rect();
                }
                Rect rect = this.mTempRect1;
                Rect rect2 = this.mTempRect2;
                rect.set(0, i, 0, 0);
                ViewUtils.computeFitSystemWindows(this.mSubDecor, rect, rect2);
                if (marginLayoutParams.topMargin != (rect2.top == 0 ? i : 0)) {
                    z2 = true;
                    marginLayoutParams.topMargin = i;
                    View view = this.mStatusGuard;
                    if (view == null) {
                        this.mStatusGuard = new View(this.mActivity);
                        this.mStatusGuard.setBackgroundColor(this.mActivity.getResources().getColor(R.color.abc_input_method_navigation_guard));
                        this.mSubDecor.addView(this.mStatusGuard, -1, new ViewGroup.LayoutParams(-1, i));
                    } else {
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        if (layoutParams.height != i) {
                            layoutParams.height = i;
                            this.mStatusGuard.setLayoutParams(layoutParams);
                        }
                    }
                } else {
                    z2 = false;
                }
                z = this.mStatusGuard != null;
                if (!this.mOverlayActionMode && z) {
                    i = 0;
                }
            } else if (marginLayoutParams.topMargin != 0) {
                z2 = true;
                marginLayoutParams.topMargin = 0;
            }
            if (z2) {
                this.mActionModeView.setLayoutParams(marginLayoutParams);
            }
        } else {
            z = false;
        }
        View view2 = this.mStatusGuard;
        if (view2 != null) {
            view2.setVisibility(z ? 0 : 8);
        }
        return i;
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        ensureSubDecor();
        ((ViewGroup) this.mActivity.findViewById(16908290)).addView(view, layoutParams);
        this.mActivity.onSupportContentChanged();
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public ActionBar createSupportActionBar() {
        ensureSubDecor();
        WindowDecorActionBar windowDecorActionBar = new WindowDecorActionBar(this.mActivity, this.mOverlayActionBar);
        windowDecorActionBar.setDefaultDisplayHomeAsUpEnabled(this.mEnableDefaultActionBarUp);
        return windowDecorActionBar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegate
    public View createView(String str, @NonNull AttributeSet attributeSet) {
        if (Build.VERSION.SDK_INT < 21) {
            boolean z = true;
            switch (str.hashCode()) {
                case -1455429095:
                    if (str.equals("CheckedTextView")) {
                        z = true;
                        break;
                    }
                    break;
                case -339785223:
                    if (str.equals("Spinner")) {
                        z = true;
                        break;
                    }
                    break;
                case 776382189:
                    if (str.equals("RadioButton")) {
                        z = true;
                        break;
                    }
                    break;
                case 1601505219:
                    if (str.equals("CheckBox")) {
                        z = true;
                        break;
                    }
                    break;
                case 1666676343:
                    if (str.equals("EditText")) {
                        z = false;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    return new TintEditText(this.mActivity, attributeSet);
                case true:
                    return new TintSpinner(this.mActivity, attributeSet);
                case true:
                    return new TintCheckBox(this.mActivity, attributeSet);
                case true:
                    return new TintRadioButton(this.mActivity, attributeSet);
                case true:
                    return new TintCheckedTextView(this.mActivity, attributeSet);
                default:
                    return null;
            }
        }
        return null;
    }

    final void ensureSubDecor() {
        DecorContentParent decorContentParent;
        if (this.mSubDecorInstalled) {
            return;
        }
        if (this.mHasActionBar) {
            TypedValue typedValue = new TypedValue();
            this.mActivity.getTheme().resolveAttribute(R.attr.actionBarTheme, typedValue, true);
            this.mSubDecor = (ViewGroup) LayoutInflater.from(typedValue.resourceId != 0 ? new ContextThemeWrapper(this.mActivity, typedValue.resourceId) : this.mActivity).inflate(R.layout.abc_screen_toolbar, (ViewGroup) null);
            this.mDecorContentParent = (DecorContentParent) this.mSubDecor.findViewById(R.id.decor_content_parent);
            this.mDecorContentParent.setWindowCallback(getWindowCallback());
            if (this.mOverlayActionBar) {
                this.mDecorContentParent.initFeature(9);
            }
            if (this.mFeatureProgress) {
                this.mDecorContentParent.initFeature(2);
            }
            if (this.mFeatureIndeterminateProgress) {
                this.mDecorContentParent.initFeature(5);
            }
        } else {
            if (this.mOverlayActionMode) {
                this.mSubDecor = (ViewGroup) LayoutInflater.from(this.mActivity).inflate(R.layout.abc_screen_simple_overlay_action_mode, (ViewGroup) null);
            } else {
                this.mSubDecor = (ViewGroup) LayoutInflater.from(this.mActivity).inflate(R.layout.abc_screen_simple, (ViewGroup) null);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                ViewCompat.setOnApplyWindowInsetsListener(this.mSubDecor, new OnApplyWindowInsetsListener(this) { // from class: android.support.v7.app.ActionBarActivityDelegateBase.2
                    final ActionBarActivityDelegateBase this$0;

                    {
                        this.this$0 = this;
                    }

                    @Override // android.support.v4.view.OnApplyWindowInsetsListener
                    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                        int systemWindowInsetTop = windowInsetsCompat.getSystemWindowInsetTop();
                        int updateStatusGuard = this.this$0.updateStatusGuard(systemWindowInsetTop);
                        return systemWindowInsetTop != updateStatusGuard ? windowInsetsCompat.replaceSystemWindowInsets(windowInsetsCompat.getSystemWindowInsetLeft(), updateStatusGuard, windowInsetsCompat.getSystemWindowInsetRight(), windowInsetsCompat.getSystemWindowInsetBottom()) : windowInsetsCompat;
                    }
                });
            } else {
                ((FitWindowsViewGroup) this.mSubDecor).setOnFitSystemWindowsListener(new FitWindowsViewGroup.OnFitSystemWindowsListener(this) { // from class: android.support.v7.app.ActionBarActivityDelegateBase.3
                    final ActionBarActivityDelegateBase this$0;

                    {
                        this.this$0 = this;
                    }

                    @Override // android.support.v7.internal.widget.FitWindowsViewGroup.OnFitSystemWindowsListener
                    public void onFitSystemWindows(Rect rect) {
                        rect.top = this.this$0.updateStatusGuard(rect.top);
                    }
                });
            }
        }
        ViewUtils.makeOptionalFitsSystemWindows(this.mSubDecor);
        this.mActivity.superSetContentView(this.mSubDecor);
        View findViewById = this.mActivity.findViewById(16908290);
        findViewById.setId(-1);
        this.mActivity.findViewById(R.id.action_bar_activity_content).setId(16908290);
        if (findViewById instanceof FrameLayout) {
            ((FrameLayout) findViewById).setForeground(null);
        }
        CharSequence charSequence = this.mTitleToSet;
        if (charSequence != null && (decorContentParent = this.mDecorContentParent) != null) {
            decorContentParent.setWindowTitle(charSequence);
            this.mTitleToSet = null;
        }
        applyFixedSizeWindow();
        onSubDecorInstalled();
        this.mSubDecorInstalled = true;
        PanelFeatureState panelState = getPanelState(0, false);
        if (isDestroyed()) {
            return;
        }
        if (panelState == null || panelState.menu == null) {
            invalidatePanelMenu(8);
        }
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    int getHomeAsUpIndicatorAttrId() {
        return R.attr.homeAsUpIndicator;
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public boolean onBackPressed() {
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
            return true;
        }
        ActionBar supportActionBar = getSupportActionBar();
        return supportActionBar != null && supportActionBar.collapseActionView();
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void onConfigurationChanged(Configuration configuration) {
        ActionBar supportActionBar;
        if (this.mHasActionBar && this.mSubDecorInstalled && (supportActionBar = getSupportActionBar()) != null) {
            supportActionBar.onConfigurationChanged(configuration);
        }
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void onContentChanged() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWindowDecor = (ViewGroup) this.mActivity.getWindow().getDecorView();
        if (NavUtils.getParentActivityName(this.mActivity) != null) {
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar == null) {
                this.mEnableDefaultActionBarUp = true;
            } else {
                supportActionBar.setDefaultDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public boolean onCreatePanelMenu(int i, Menu menu) {
        if (i != 0) {
            return getWindowCallback().onCreatePanelMenu(i, menu);
        }
        return false;
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public View onCreatePanelView(int i) {
        View view = null;
        if (this.mActionMode == null) {
            WindowCallback windowCallback = getWindowCallback();
            if (windowCallback != null) {
                view = windowCallback.onCreatePanelView(i);
            }
            if (view == null && this.mToolbarListMenuPresenter == null) {
                PanelFeatureState panelState = getPanelState(i, true);
                openPanel(panelState, (KeyEvent) null);
                if (panelState.isOpen) {
                    view = panelState.shownPanelView;
                }
            }
        } else {
            view = null;
        }
        return view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegate
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return onKeyShortcut(i, keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegate
    public boolean onKeyShortcut(int i, KeyEvent keyEvent) {
        PanelFeatureState panelFeatureState = this.mPreparedPanel;
        if (panelFeatureState != null && performPanelShortcut(panelFeatureState, keyEvent.getKeyCode(), keyEvent, 1)) {
            PanelFeatureState panelFeatureState2 = this.mPreparedPanel;
            if (panelFeatureState2 != null) {
                panelFeatureState2.isHandled = true;
                return true;
            }
            return true;
        } else if (this.mPreparedPanel == null) {
            PanelFeatureState panelState = getPanelState(0, true);
            preparePanel(panelState, keyEvent);
            boolean performPanelShortcut = performPanelShortcut(panelState, keyEvent.getKeyCode(), keyEvent, 1);
            panelState.isPrepared = false;
            return performPanelShortcut;
        } else {
            return false;
        }
    }

    @Override // android.support.v7.internal.view.menu.MenuBuilder.Callback
    public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
        PanelFeatureState findMenuPanel;
        WindowCallback windowCallback = getWindowCallback();
        if (windowCallback == null || isDestroyed() || (findMenuPanel = findMenuPanel(menuBuilder.getRootMenu())) == null) {
            return false;
        }
        return windowCallback.onMenuItemSelected(findMenuPanel.featureId, menuItem);
    }

    @Override // android.support.v7.internal.view.menu.MenuBuilder.Callback
    public void onMenuModeChange(MenuBuilder menuBuilder) {
        reopenMenu(menuBuilder, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegate
    public boolean onMenuOpened(int i, Menu menu) {
        if (i == 8) {
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.dispatchMenuVisibilityChanged(true);
                return true;
            }
            return true;
        }
        return this.mActivity.superOnMenuOpened(i, menu);
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void onPanelClosed(int i, Menu menu) {
        PanelFeatureState panelState = getPanelState(i, false);
        if (panelState != null) {
            closePanel(panelState, false);
        }
        if (i != 8) {
            if (isDestroyed()) {
                return;
            }
            this.mActivity.superOnPanelClosed(i, menu);
            return;
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.dispatchMenuVisibilityChanged(false);
        }
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void onPostResume() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setShowHideAnimationEnabled(true);
        }
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public boolean onPreparePanel(int i, View view, Menu menu) {
        if (i != 0) {
            return getWindowCallback().onPreparePanel(i, view, menu);
        }
        return false;
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void onStop() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setShowHideAnimationEnabled(false);
        }
    }

    void onSubDecorInstalled() {
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void onTitleChanged(CharSequence charSequence) {
        DecorContentParent decorContentParent = this.mDecorContentParent;
        if (decorContentParent != null) {
            decorContentParent.setWindowTitle(charSequence);
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().setWindowTitle(charSequence);
        } else {
            this.mTitleToSet = charSequence;
        }
    }

    final boolean performPanelShortcut(PanelFeatureState panelFeatureState, int i, KeyEvent keyEvent, int i2) {
        if (keyEvent.isSystem()) {
            return false;
        }
        boolean z = false;
        if ((panelFeatureState.isPrepared || preparePanel(panelFeatureState, keyEvent)) && panelFeatureState.menu != null) {
            z = panelFeatureState.menu.performShortcut(i, keyEvent, i2);
        }
        if (z && (i2 & 1) == 0 && this.mDecorContentParent == null) {
            closePanel(panelFeatureState, true);
        }
        return z;
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void setContentView(int i) {
        ensureSubDecor();
        ViewGroup viewGroup = (ViewGroup) this.mActivity.findViewById(16908290);
        viewGroup.removeAllViews();
        this.mActivity.getLayoutInflater().inflate(i, viewGroup);
        this.mActivity.onSupportContentChanged();
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void setContentView(View view) {
        ensureSubDecor();
        ViewGroup viewGroup = (ViewGroup) this.mActivity.findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(view);
        this.mActivity.onSupportContentChanged();
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        ensureSubDecor();
        ViewGroup viewGroup = (ViewGroup) this.mActivity.findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(view, layoutParams);
        this.mActivity.onSupportContentChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void setSupportActionBar(Toolbar toolbar) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar instanceof WindowDecorActionBar) {
            throw new IllegalStateException("This Activity already has an action bar supplied by the window decor. Do not request Window.FEATURE_ACTION_BAR and set windowActionBar to false in your theme to use a Toolbar instead.");
        }
        if (supportActionBar instanceof ToolbarActionBar) {
            ((ToolbarActionBar) supportActionBar).setListMenuPresenter(null);
        }
        ToolbarActionBar toolbarActionBar = new ToolbarActionBar(toolbar, this.mActivity.getTitle(), this.mActivity.getWindow(), this.mDefaultWindowCallback);
        ensureToolbarListMenuPresenter();
        toolbarActionBar.setListMenuPresenter(this.mToolbarListMenuPresenter);
        setSupportActionBar(toolbarActionBar);
        setWindowCallback(toolbarActionBar.getWrappedWindowCallback());
        toolbarActionBar.invalidateOptionsMenu();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void setSupportProgress(int i) {
        updateProgressBars(i + 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void setSupportProgressBarIndeterminate(boolean z) {
        updateProgressBars(z ? -3 : -4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void setSupportProgressBarIndeterminateVisibility(boolean z) {
        updateProgressBars(z ? -1 : -2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void setSupportProgressBarVisibility(boolean z) {
        updateProgressBars(z ? -1 : -2);
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public ActionMode startSupportActionMode(ActionMode.Callback callback) {
        if (callback != null) {
            ActionMode actionMode = this.mActionMode;
            if (actionMode != null) {
                actionMode.finish();
            }
            ActionModeCallbackWrapper actionModeCallbackWrapper = new ActionModeCallbackWrapper(this, callback);
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                this.mActionMode = supportActionBar.startActionMode(actionModeCallbackWrapper);
                if (this.mActionMode != null) {
                    this.mActivity.onSupportActionModeStarted(this.mActionMode);
                }
            }
            if (this.mActionMode == null) {
                this.mActionMode = startSupportActionModeFromWindow(actionModeCallbackWrapper);
            }
            return this.mActionMode;
        }
        throw new IllegalArgumentException("ActionMode callback can not be null.");
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    ActionMode startSupportActionModeFromWindow(ActionMode.Callback callback) {
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
        ActionModeCallbackWrapper actionModeCallbackWrapper = new ActionModeCallbackWrapper(this, callback);
        Context actionBarThemedContext = getActionBarThemedContext();
        boolean z = true;
        if (this.mActionModeView == null) {
            if (this.mIsFloating) {
                this.mActionModeView = new ActionBarContextView(actionBarThemedContext);
                this.mActionModePopup = new PopupWindow(actionBarThemedContext, (AttributeSet) null, R.attr.actionModePopupWindowStyle);
                this.mActionModePopup.setContentView(this.mActionModeView);
                this.mActionModePopup.setWidth(-1);
                TypedValue typedValue = new TypedValue();
                this.mActivity.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true);
                this.mActionModeView.setContentHeight(TypedValue.complexToDimensionPixelSize(typedValue.data, this.mActivity.getResources().getDisplayMetrics()));
                this.mActionModePopup.setHeight(-2);
                this.mShowActionModePopup = new Runnable(this) { // from class: android.support.v7.app.ActionBarActivityDelegateBase.4
                    final ActionBarActivityDelegateBase this$0;

                    {
                        this.this$0 = this;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        this.this$0.mActionModePopup.showAtLocation(this.this$0.mActionModeView, 55, 0, 0);
                    }
                };
            } else {
                ViewStubCompat viewStubCompat = (ViewStubCompat) this.mActivity.findViewById(R.id.action_mode_bar_stub);
                if (viewStubCompat != null) {
                    viewStubCompat.setLayoutInflater(LayoutInflater.from(actionBarThemedContext));
                    this.mActionModeView = (ActionBarContextView) viewStubCompat.inflate();
                }
            }
        }
        ActionBarContextView actionBarContextView = this.mActionModeView;
        if (actionBarContextView != null) {
            actionBarContextView.killMode();
            ActionBarContextView actionBarContextView2 = this.mActionModeView;
            if (this.mActionModePopup != null) {
                z = false;
            }
            StandaloneActionMode standaloneActionMode = new StandaloneActionMode(actionBarThemedContext, actionBarContextView2, actionModeCallbackWrapper, z);
            if (callback.onCreateActionMode(standaloneActionMode, standaloneActionMode.getMenu())) {
                standaloneActionMode.invalidate();
                this.mActionModeView.initForMode(standaloneActionMode);
                this.mActionModeView.setVisibility(0);
                this.mActionMode = standaloneActionMode;
                if (this.mActionModePopup != null) {
                    this.mActivity.getWindow().getDecorView().post(this.mShowActionModePopup);
                }
                this.mActionModeView.sendAccessibilityEvent(32);
                if (this.mActionModeView.getParent() != null) {
                    ViewCompat.requestApplyInsets((View) this.mActionModeView.getParent());
                }
            } else {
                this.mActionMode = null;
            }
        }
        if (this.mActionMode != null && this.mActivity != null) {
            this.mActivity.onSupportActionModeStarted(this.mActionMode);
        }
        return this.mActionMode;
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public void supportInvalidateOptionsMenu() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar == null || !supportActionBar.invalidateOptionsMenu()) {
            invalidatePanelMenu(0);
        }
    }

    @Override // android.support.v7.app.ActionBarActivityDelegate
    public boolean supportRequestWindowFeature(int i) {
        if (i == 2) {
            this.mFeatureProgress = true;
            return true;
        } else if (i == 5) {
            this.mFeatureIndeterminateProgress = true;
            return true;
        } else {
            switch (i) {
                case 8:
                    this.mHasActionBar = true;
                    return true;
                case 9:
                    this.mOverlayActionBar = true;
                    return true;
                case 10:
                    this.mOverlayActionMode = true;
                    return true;
                default:
                    return this.mActivity.requestWindowFeature(i);
            }
        }
    }
}