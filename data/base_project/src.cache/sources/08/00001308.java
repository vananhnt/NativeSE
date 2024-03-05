package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.appcompat.R;
import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.internal.view.menu.MenuView;
import android.support.v7.internal.view.menu.SubMenuBuilder;
import android.support.v7.internal.widget.DecorToolbar;
import android.support.v7.internal.widget.RtlSpacingHelper;
import android.support.v7.internal.widget.TintManager;
import android.support.v7.internal.widget.TintTypedArray;
import android.support.v7.internal.widget.ToolbarWidgetWrapper;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.view.CollapsibleActionView;
import android.support.v7.widget.ActionMenuView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/* loaded from: Toolbar.class */
public class Toolbar extends ViewGroup {
    private static final String TAG = "Toolbar";
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private int mButtonGravity;
    private ImageButton mCollapseButtonView;
    private Drawable mCollapseIcon;
    private boolean mCollapsible;
    private final RtlSpacingHelper mContentInsets;
    private boolean mEatingTouch;
    View mExpandedActionView;
    private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    private int mGravity;
    private ImageView mLogoView;
    private int mMaxButtonHeight;
    private MenuBuilder.Callback mMenuBuilderCallback;
    private ActionMenuView mMenuView;
    private final ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener;
    private int mMinHeight;
    private ImageButton mNavButtonView;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private ActionMenuPresenter mOuterActionMenuPresenter;
    private Context mPopupContext;
    private int mPopupTheme;
    private final Runnable mShowOverflowMenuRunnable;
    private CharSequence mSubtitleText;
    private int mSubtitleTextAppearance;
    private int mSubtitleTextColor;
    private TextView mSubtitleTextView;
    private final int[] mTempMargins;
    private final ArrayList<View> mTempViews;
    private final TintManager mTintManager;
    private int mTitleMarginBottom;
    private int mTitleMarginEnd;
    private int mTitleMarginStart;
    private int mTitleMarginTop;
    private CharSequence mTitleText;
    private int mTitleTextAppearance;
    private int mTitleTextColor;
    private TextView mTitleTextView;
    private ToolbarWidgetWrapper mWrapper;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Toolbar$ExpandedActionViewMenuPresenter.class */
    public class ExpandedActionViewMenuPresenter implements MenuPresenter {
        MenuItemImpl mCurrentExpandedItem;
        MenuBuilder mMenu;
        final Toolbar this$0;

        private ExpandedActionViewMenuPresenter(Toolbar toolbar) {
            this.this$0 = toolbar;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public boolean collapseItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
            if (this.this$0.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) this.this$0.mExpandedActionView).onActionViewCollapsed();
            }
            Toolbar toolbar = this.this$0;
            toolbar.removeView(toolbar.mExpandedActionView);
            Toolbar toolbar2 = this.this$0;
            toolbar2.removeView(toolbar2.mCollapseButtonView);
            Toolbar toolbar3 = this.this$0;
            toolbar3.mExpandedActionView = null;
            toolbar3.setChildVisibilityForExpandedActionView(false);
            this.mCurrentExpandedItem = null;
            this.this$0.requestLayout();
            menuItemImpl.setActionViewExpanded(false);
            return true;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public boolean expandItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
            this.this$0.ensureCollapseButtonView();
            ViewParent parent = this.this$0.mCollapseButtonView.getParent();
            Toolbar toolbar = this.this$0;
            if (parent != toolbar) {
                toolbar.addView(toolbar.mCollapseButtonView);
            }
            this.this$0.mExpandedActionView = menuItemImpl.getActionView();
            this.mCurrentExpandedItem = menuItemImpl;
            ViewParent parent2 = this.this$0.mExpandedActionView.getParent();
            Toolbar toolbar2 = this.this$0;
            if (parent2 != toolbar2) {
                LayoutParams generateDefaultLayoutParams = toolbar2.generateDefaultLayoutParams();
                generateDefaultLayoutParams.gravity = 8388611 | (this.this$0.mButtonGravity & 112);
                generateDefaultLayoutParams.mViewType = 2;
                this.this$0.mExpandedActionView.setLayoutParams(generateDefaultLayoutParams);
                Toolbar toolbar3 = this.this$0;
                toolbar3.addView(toolbar3.mExpandedActionView);
            }
            this.this$0.setChildVisibilityForExpandedActionView(true);
            this.this$0.requestLayout();
            menuItemImpl.setActionViewExpanded(true);
            if (this.this$0.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) this.this$0.mExpandedActionView).onActionViewExpanded();
                return true;
            }
            return true;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public boolean flagActionItems() {
            return false;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public int getId() {
            return 0;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public MenuView getMenuView(ViewGroup viewGroup) {
            return null;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public void initForMenu(Context context, MenuBuilder menuBuilder) {
            MenuItemImpl menuItemImpl;
            MenuBuilder menuBuilder2 = this.mMenu;
            if (menuBuilder2 != null && (menuItemImpl = this.mCurrentExpandedItem) != null) {
                menuBuilder2.collapseItemActionView(menuItemImpl);
            }
            this.mMenu = menuBuilder;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public void onRestoreInstanceState(Parcelable parcelable) {
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public Parcelable onSaveInstanceState() {
            return null;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
            return false;
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public void setCallback(MenuPresenter.Callback callback) {
        }

        @Override // android.support.v7.internal.view.menu.MenuPresenter
        public void updateMenuView(boolean z) {
            boolean z2;
            if (this.mCurrentExpandedItem != null) {
                MenuBuilder menuBuilder = this.mMenu;
                if (menuBuilder != null) {
                    int size = menuBuilder.size();
                    int i = 0;
                    while (true) {
                        if (i >= size) {
                            z2 = false;
                            break;
                        } else if (this.mMenu.getItem(i) == this.mCurrentExpandedItem) {
                            z2 = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                } else {
                    z2 = false;
                }
                if (z2) {
                    return;
                }
                collapseItemActionView(this.mMenu, this.mCurrentExpandedItem);
            }
        }
    }

    /* loaded from: Toolbar$LayoutParams.class */
    public static class LayoutParams extends ActionBar.LayoutParams {
        static final int CUSTOM = 0;
        static final int EXPANDED = 2;
        static final int SYSTEM = 1;
        int mViewType;

        public LayoutParams(int i) {
            this(-2, -1, i);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.mViewType = 0;
            this.gravity = 8388627;
        }

        public LayoutParams(int i, int i2, int i3) {
            super(i, i2);
            this.mViewType = 0;
            this.gravity = i3;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mViewType = 0;
        }

        public LayoutParams(ActionBar.LayoutParams layoutParams) {
            super(layoutParams);
            this.mViewType = 0;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ActionBar.LayoutParams) layoutParams);
            this.mViewType = 0;
            this.mViewType = layoutParams.mViewType;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mViewType = 0;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.mViewType = 0;
            copyMarginsFromCompat(marginLayoutParams);
        }

        void copyMarginsFromCompat(ViewGroup.MarginLayoutParams marginLayoutParams) {
            this.leftMargin = marginLayoutParams.leftMargin;
            this.topMargin = marginLayoutParams.topMargin;
            this.rightMargin = marginLayoutParams.rightMargin;
            this.bottomMargin = marginLayoutParams.bottomMargin;
        }
    }

    /* loaded from: Toolbar$OnMenuItemClickListener.class */
    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Toolbar$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.support.v7.widget.Toolbar.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        public int expandedMenuItemId;
        public boolean isOverflowOpen;

        public SavedState(Parcel parcel) {
            super(parcel);
            this.expandedMenuItemId = parcel.readInt();
            this.isOverflowOpen = parcel.readInt() != 0;
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.expandedMenuItemId);
            parcel.writeInt(this.isOverflowOpen ? 1 : 0);
        }
    }

    public Toolbar(Context context) {
        this(context, null);
    }

    public Toolbar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.toolbarStyle);
    }

    public Toolbar(Context context, AttributeSet attributeSet, int i) {
        super(themifyContext(context, attributeSet, i), attributeSet, i);
        this.mContentInsets = new RtlSpacingHelper();
        this.mGravity = 8388627;
        this.mTempViews = new ArrayList<>();
        this.mTempMargins = new int[2];
        this.mMenuViewItemClickListener = new ActionMenuView.OnMenuItemClickListener(this) { // from class: android.support.v7.widget.Toolbar.1
            final Toolbar this$0;

            {
                this.this$0 = this;
            }

            @Override // android.support.v7.widget.ActionMenuView.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (this.this$0.mOnMenuItemClickListener != null) {
                    return this.this$0.mOnMenuItemClickListener.onMenuItemClick(menuItem);
                }
                return false;
            }
        };
        this.mShowOverflowMenuRunnable = new Runnable(this) { // from class: android.support.v7.widget.Toolbar.2
            final Toolbar this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.showOverflowMenu();
            }
        };
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(getContext(), attributeSet, R.styleable.Toolbar, i, 0);
        this.mTitleTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
        this.mSubtitleTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0);
        this.mGravity = obtainStyledAttributes.getInteger(R.styleable.Toolbar_android_gravity, this.mGravity);
        this.mButtonGravity = obtainStyledAttributes.getInteger(R.styleable.Toolbar_buttonGravity, 48);
        int dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMargins, 0);
        this.mTitleMarginBottom = dimensionPixelOffset;
        this.mTitleMarginTop = dimensionPixelOffset;
        this.mTitleMarginEnd = dimensionPixelOffset;
        this.mTitleMarginStart = dimensionPixelOffset;
        int dimensionPixelOffset2 = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginStart, -1);
        if (dimensionPixelOffset2 >= 0) {
            this.mTitleMarginStart = dimensionPixelOffset2;
        }
        int dimensionPixelOffset3 = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginEnd, -1);
        if (dimensionPixelOffset3 >= 0) {
            this.mTitleMarginEnd = dimensionPixelOffset3;
        }
        int dimensionPixelOffset4 = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginTop, -1);
        if (dimensionPixelOffset4 >= 0) {
            this.mTitleMarginTop = dimensionPixelOffset4;
        }
        int dimensionPixelOffset5 = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginBottom, -1);
        if (dimensionPixelOffset5 >= 0) {
            this.mTitleMarginBottom = dimensionPixelOffset5;
        }
        this.mMaxButtonHeight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.Toolbar_maxButtonHeight, -1);
        int dimensionPixelOffset6 = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStart, Integer.MIN_VALUE);
        int dimensionPixelOffset7 = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEnd, Integer.MIN_VALUE);
        this.mContentInsets.setAbsolute(obtainStyledAttributes.getDimensionPixelSize(R.styleable.Toolbar_contentInsetLeft, 0), obtainStyledAttributes.getDimensionPixelSize(R.styleable.Toolbar_contentInsetRight, 0));
        if (dimensionPixelOffset6 != Integer.MIN_VALUE || dimensionPixelOffset7 != Integer.MIN_VALUE) {
            this.mContentInsets.setRelative(dimensionPixelOffset6, dimensionPixelOffset7);
        }
        this.mCollapseIcon = obtainStyledAttributes.getDrawable(R.styleable.Toolbar_collapseIcon);
        CharSequence text = obtainStyledAttributes.getText(R.styleable.Toolbar_title);
        if (!TextUtils.isEmpty(text)) {
            setTitle(text);
        }
        CharSequence text2 = obtainStyledAttributes.getText(R.styleable.Toolbar_subtitle);
        if (!TextUtils.isEmpty(text2)) {
            setSubtitle(text2);
        }
        this.mPopupContext = getContext();
        setPopupTheme(obtainStyledAttributes.getResourceId(R.styleable.Toolbar_popupTheme, 0));
        Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.Toolbar_navigationIcon);
        if (drawable != null) {
            setNavigationIcon(drawable);
        }
        CharSequence text3 = obtainStyledAttributes.getText(R.styleable.Toolbar_navigationContentDescription);
        if (!TextUtils.isEmpty(text3)) {
            setNavigationContentDescription(text3);
        }
        this.mMinHeight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.Toolbar_android_minHeight, 0);
        obtainStyledAttributes.recycle();
        this.mTintManager = obtainStyledAttributes.getTintManager();
    }

    private void addCustomViewsWithGravity(List<View> list, int i) {
        boolean z = ViewCompat.getLayoutDirection(this) == 1;
        int childCount = getChildCount();
        int absoluteGravity = GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection(this));
        list.clear();
        if (z) {
            for (int i2 = childCount - 1; i2 >= 0; i2--) {
                View childAt = getChildAt(i2);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.mViewType == 0 && shouldLayout(childAt) && getChildHorizontalGravity(layoutParams.gravity) == absoluteGravity) {
                    list.add(childAt);
                }
            }
            return;
        }
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt2 = getChildAt(i3);
            LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
            if (layoutParams2.mViewType == 0 && shouldLayout(childAt2) && getChildHorizontalGravity(layoutParams2.gravity) == absoluteGravity) {
                list.add(childAt2);
            }
        }
    }

    private void addSystemView(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        LayoutParams generateDefaultLayoutParams = layoutParams == null ? generateDefaultLayoutParams() : !checkLayoutParams(layoutParams) ? generateLayoutParams(layoutParams) : (LayoutParams) layoutParams;
        generateDefaultLayoutParams.mViewType = 1;
        addView(view, generateDefaultLayoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureCollapseButtonView() {
        if (this.mCollapseButtonView == null) {
            this.mCollapseButtonView = new ImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
            this.mCollapseButtonView.setImageDrawable(this.mCollapseIcon);
            LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
            generateDefaultLayoutParams.gravity = 8388611 | (this.mButtonGravity & 112);
            generateDefaultLayoutParams.mViewType = 2;
            this.mCollapseButtonView.setLayoutParams(generateDefaultLayoutParams);
            this.mCollapseButtonView.setOnClickListener(new View.OnClickListener(this) { // from class: android.support.v7.widget.Toolbar.3
                final Toolbar this$0;

                {
                    this.this$0 = this;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    this.this$0.collapseActionView();
                }
            });
        }
    }

    private void ensureLogoView() {
        if (this.mLogoView == null) {
            this.mLogoView = new ImageView(getContext());
        }
    }

    private void ensureMenu() {
        ensureMenuView();
        if (this.mMenuView.peekMenu() == null) {
            MenuBuilder menuBuilder = (MenuBuilder) this.mMenuView.getMenu();
            if (this.mExpandedMenuPresenter == null) {
                this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
            }
            this.mMenuView.setExpandedActionViewsExclusive(true);
            menuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
        }
    }

    private void ensureMenuView() {
        if (this.mMenuView == null) {
            this.mMenuView = new ActionMenuView(getContext());
            this.mMenuView.setPopupTheme(this.mPopupTheme);
            this.mMenuView.setOnMenuItemClickListener(this.mMenuViewItemClickListener);
            this.mMenuView.setMenuCallbacks(this.mActionMenuPresenterCallback, this.mMenuBuilderCallback);
            LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
            generateDefaultLayoutParams.gravity = 8388613 | (this.mButtonGravity & 112);
            this.mMenuView.setLayoutParams(generateDefaultLayoutParams);
            addSystemView(this.mMenuView);
        }
    }

    private void ensureNavButtonView() {
        if (this.mNavButtonView == null) {
            this.mNavButtonView = new ImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
            LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
            generateDefaultLayoutParams.gravity = 8388611 | (this.mButtonGravity & 112);
            this.mNavButtonView.setLayoutParams(generateDefaultLayoutParams);
        }
    }

    private int getChildHorizontalGravity(int i) {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        int absoluteGravity = GravityCompat.getAbsoluteGravity(i, layoutDirection) & 7;
        if (absoluteGravity != 1) {
            int i2 = 3;
            if (absoluteGravity != 3 && absoluteGravity != 5) {
                if (layoutDirection == 1) {
                    i2 = 5;
                }
                return i2;
            }
        }
        return absoluteGravity;
    }

    private int getChildTop(View view, int i) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int measuredHeight = view.getMeasuredHeight();
        int i2 = i > 0 ? (measuredHeight - i) / 2 : 0;
        int childVerticalGravity = getChildVerticalGravity(layoutParams.gravity);
        if (childVerticalGravity != 48) {
            if (childVerticalGravity != 80) {
                int paddingTop = getPaddingTop();
                int paddingBottom = getPaddingBottom();
                int height = getHeight();
                int i3 = (((height - paddingTop) - paddingBottom) - measuredHeight) / 2;
                if (i3 < layoutParams.topMargin) {
                    i3 = layoutParams.topMargin;
                } else {
                    int i4 = (((height - paddingBottom) - measuredHeight) - i3) - paddingTop;
                    if (i4 < layoutParams.bottomMargin) {
                        i3 = Math.max(0, i3 - (layoutParams.bottomMargin - i4));
                    }
                }
                return paddingTop + i3;
            }
            return (((getHeight() - getPaddingBottom()) - measuredHeight) - layoutParams.bottomMargin) - i2;
        }
        return getPaddingTop() - i2;
    }

    private int getChildVerticalGravity(int i) {
        int i2 = i & 112;
        return (i2 == 16 || i2 == 48 || i2 == 80) ? i2 : this.mGravity & 112;
    }

    private int getHorizontalMargins(View view) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        return MarginLayoutParamsCompat.getMarginStart(marginLayoutParams) + MarginLayoutParamsCompat.getMarginEnd(marginLayoutParams);
    }

    private MenuInflater getMenuInflater() {
        return new SupportMenuInflater(getContext());
    }

    private int getMinimumHeightCompat() {
        return Build.VERSION.SDK_INT >= 16 ? ViewCompat.getMinimumHeight(this) : this.mMinHeight;
    }

    private int getVerticalMargins(View view) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        return marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
    }

    private int getViewListMeasuredWidth(List<View> list, int[] iArr) {
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = 0;
        int size = list.size();
        for (int i4 = 0; i4 < size; i4++) {
            View view = list.get(i4);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            int i5 = layoutParams.leftMargin - i;
            int i6 = layoutParams.rightMargin - i2;
            int max = Math.max(0, i5);
            int max2 = Math.max(0, i6);
            i = Math.max(0, -i5);
            i2 = Math.max(0, -i6);
            i3 += view.getMeasuredWidth() + max + max2;
        }
        return i3;
    }

    private static boolean isCustomView(View view) {
        return ((LayoutParams) view.getLayoutParams()).mViewType == 0;
    }

    private int layoutChildLeft(View view, int i, int[] iArr, int i2) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int i3 = layoutParams.leftMargin - iArr[0];
        int max = i + Math.max(0, i3);
        iArr[0] = Math.max(0, -i3);
        int childTop = getChildTop(view, i2);
        int measuredWidth = view.getMeasuredWidth();
        view.layout(max, childTop, max + measuredWidth, view.getMeasuredHeight() + childTop);
        return max + layoutParams.rightMargin + measuredWidth;
    }

    private int layoutChildRight(View view, int i, int[] iArr, int i2) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int i3 = layoutParams.rightMargin - iArr[1];
        int max = i - Math.max(0, i3);
        iArr[1] = Math.max(0, -i3);
        int childTop = getChildTop(view, i2);
        int measuredWidth = view.getMeasuredWidth();
        view.layout(max - measuredWidth, childTop, max, view.getMeasuredHeight() + childTop);
        return max - (layoutParams.leftMargin + measuredWidth);
    }

    private int measureChildCollapseMargins(View view, int i, int i2, int i3, int i4, int[] iArr) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int i5 = marginLayoutParams.leftMargin - iArr[0];
        int i6 = marginLayoutParams.rightMargin - iArr[1];
        int max = Math.max(0, i5) + Math.max(0, i6);
        iArr[0] = Math.max(0, -i5);
        iArr[1] = Math.max(0, -i6);
        view.measure(getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + max + i2, marginLayoutParams.width), getChildMeasureSpec(i3, getPaddingTop() + getPaddingBottom() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin + i4, marginLayoutParams.height));
        return view.getMeasuredWidth() + max;
    }

    private void measureChildConstrained(View view, int i, int i2, int i3, int i4, int i5) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int childMeasureSpec = getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + i2, marginLayoutParams.width);
        int childMeasureSpec2 = getChildMeasureSpec(i3, getPaddingTop() + getPaddingBottom() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin + i4, marginLayoutParams.height);
        int mode = View.MeasureSpec.getMode(childMeasureSpec2);
        if (mode != 1073741824 && i5 >= 0) {
            if (mode != 0) {
                i5 = Math.min(View.MeasureSpec.getSize(childMeasureSpec2), i5);
            }
            childMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(i5, 1073741824);
        }
        view.measure(childMeasureSpec, childMeasureSpec2);
    }

    private void postShowOverflowMenu() {
        removeCallbacks(this.mShowOverflowMenuRunnable);
        post(this.mShowOverflowMenuRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setChildVisibilityForExpandedActionView(boolean z) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (((LayoutParams) childAt.getLayoutParams()).mViewType != 2 && childAt != this.mMenuView) {
                childAt.setVisibility(z ? 8 : 0);
            }
        }
    }

    private boolean shouldCollapse() {
        if (this.mCollapsible) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (shouldLayout(childAt) && childAt.getMeasuredWidth() > 0 && childAt.getMeasuredHeight() > 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean shouldLayout(View view) {
        return (view == null || view.getParent() != this || view.getVisibility() == 8) ? false : true;
    }

    private static Context themifyContext(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Toolbar, i, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.Toolbar_theme, 0);
        if (resourceId != 0) {
            context = new ContextThemeWrapper(context, resourceId);
        }
        obtainStyledAttributes.recycle();
        return context;
    }

    private void updateChildVisibilityForExpandedActionView(View view) {
        if (((LayoutParams) view.getLayoutParams()).mViewType == 2 || view == this.mMenuView) {
            return;
        }
        view.setVisibility(this.mExpandedActionView != null ? 8 : 0);
    }

    public boolean canShowOverflowMenu() {
        ActionMenuView actionMenuView;
        return getVisibility() == 0 && (actionMenuView = this.mMenuView) != null && actionMenuView.isOverflowReserved();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return super.checkLayoutParams(layoutParams) && (layoutParams instanceof LayoutParams);
    }

    public void collapseActionView() {
        ExpandedActionViewMenuPresenter expandedActionViewMenuPresenter = this.mExpandedMenuPresenter;
        MenuItemImpl menuItemImpl = expandedActionViewMenuPresenter == null ? null : expandedActionViewMenuPresenter.mCurrentExpandedItem;
        if (menuItemImpl != null) {
            menuItemImpl.collapseActionView();
        }
    }

    public void dismissPopupMenus() {
        ActionMenuView actionMenuView = this.mMenuView;
        if (actionMenuView != null) {
            actionMenuView.dismissPopupMenus();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams ? new LayoutParams((LayoutParams) layoutParams) : layoutParams instanceof ActionBar.LayoutParams ? new LayoutParams((ActionBar.LayoutParams) layoutParams) : layoutParams instanceof ViewGroup.MarginLayoutParams ? new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams) : new LayoutParams(layoutParams);
    }

    public int getContentInsetEnd() {
        return this.mContentInsets.getEnd();
    }

    public int getContentInsetLeft() {
        return this.mContentInsets.getLeft();
    }

    public int getContentInsetRight() {
        return this.mContentInsets.getRight();
    }

    public int getContentInsetStart() {
        return this.mContentInsets.getStart();
    }

    public Drawable getLogo() {
        ImageView imageView = this.mLogoView;
        return imageView != null ? imageView.getDrawable() : null;
    }

    public CharSequence getLogoDescription() {
        ImageView imageView = this.mLogoView;
        return imageView != null ? imageView.getContentDescription() : null;
    }

    public Menu getMenu() {
        ensureMenu();
        return this.mMenuView.getMenu();
    }

    @Nullable
    public CharSequence getNavigationContentDescription() {
        ImageButton imageButton = this.mNavButtonView;
        return imageButton != null ? imageButton.getContentDescription() : null;
    }

    @Nullable
    public Drawable getNavigationIcon() {
        ImageButton imageButton = this.mNavButtonView;
        return imageButton != null ? imageButton.getDrawable() : null;
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    public CharSequence getSubtitle() {
        return this.mSubtitleText;
    }

    public CharSequence getTitle() {
        return this.mTitleText;
    }

    public DecorToolbar getWrapper() {
        if (this.mWrapper == null) {
            this.mWrapper = new ToolbarWidgetWrapper(this, true);
        }
        return this.mWrapper;
    }

    public boolean hasExpandedActionView() {
        ExpandedActionViewMenuPresenter expandedActionViewMenuPresenter = this.mExpandedMenuPresenter;
        return (expandedActionViewMenuPresenter == null || expandedActionViewMenuPresenter.mCurrentExpandedItem == null) ? false : true;
    }

    public boolean hideOverflowMenu() {
        ActionMenuView actionMenuView = this.mMenuView;
        return actionMenuView != null && actionMenuView.hideOverflowMenu();
    }

    public void inflateMenu(int i) {
        getMenuInflater().inflate(i, getMenu());
    }

    public boolean isOverflowMenuShowPending() {
        ActionMenuView actionMenuView = this.mMenuView;
        return actionMenuView != null && actionMenuView.isOverflowMenuShowPending();
    }

    public boolean isOverflowMenuShowing() {
        ActionMenuView actionMenuView = this.mMenuView;
        return actionMenuView != null && actionMenuView.isOverflowMenuShowing();
    }

    public boolean isTitleTruncated() {
        Layout layout;
        TextView textView = this.mTitleTextView;
        if (textView == null || (layout = textView.getLayout()) == null) {
            return false;
        }
        int lineCount = layout.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            if (layout.getEllipsisCount(i) > 0) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mShowOverflowMenuRunnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int paddingTop;
        int i6;
        int i7;
        int max;
        int i8;
        int i9;
        int max2;
        boolean z2 = ViewCompat.getLayoutDirection(this) == 1;
        int width = getWidth();
        int height = getHeight();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop2 = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int i10 = paddingLeft;
        int i11 = width - paddingRight;
        int[] iArr = this.mTempMargins;
        iArr[1] = 0;
        iArr[0] = 0;
        int minimumHeightCompat = getMinimumHeightCompat();
        if (shouldLayout(this.mNavButtonView)) {
            if (z2) {
                i11 = layoutChildRight(this.mNavButtonView, i11, iArr, minimumHeightCompat);
            } else {
                i10 = layoutChildLeft(this.mNavButtonView, i10, iArr, minimumHeightCompat);
            }
        }
        if (shouldLayout(this.mCollapseButtonView)) {
            if (z2) {
                i11 = layoutChildRight(this.mCollapseButtonView, i11, iArr, minimumHeightCompat);
            } else {
                i10 = layoutChildLeft(this.mCollapseButtonView, i10, iArr, minimumHeightCompat);
            }
        }
        if (shouldLayout(this.mMenuView)) {
            if (z2) {
                i10 = layoutChildLeft(this.mMenuView, i10, iArr, minimumHeightCompat);
            } else {
                i11 = layoutChildRight(this.mMenuView, i11, iArr, minimumHeightCompat);
            }
        }
        iArr[0] = Math.max(0, getContentInsetLeft() - i10);
        iArr[1] = Math.max(0, getContentInsetRight() - ((width - paddingRight) - i11));
        int max3 = Math.max(i10, getContentInsetLeft());
        int min = Math.min(i11, (width - paddingRight) - getContentInsetRight());
        if (!shouldLayout(this.mExpandedActionView)) {
            i5 = max3;
        } else if (z2) {
            min = layoutChildRight(this.mExpandedActionView, min, iArr, minimumHeightCompat);
            i5 = max3;
        } else {
            i5 = layoutChildLeft(this.mExpandedActionView, max3, iArr, minimumHeightCompat);
        }
        if (shouldLayout(this.mLogoView)) {
            if (z2) {
                min = layoutChildRight(this.mLogoView, min, iArr, minimumHeightCompat);
            } else {
                i5 = layoutChildLeft(this.mLogoView, i5, iArr, minimumHeightCompat);
            }
        }
        boolean shouldLayout = shouldLayout(this.mTitleTextView);
        boolean shouldLayout2 = shouldLayout(this.mSubtitleTextView);
        int i12 = 0;
        if (shouldLayout) {
            LayoutParams layoutParams = (LayoutParams) this.mTitleTextView.getLayoutParams();
            i12 = 0 + layoutParams.topMargin + this.mTitleTextView.getMeasuredHeight() + layoutParams.bottomMargin;
        }
        if (shouldLayout2) {
            LayoutParams layoutParams2 = (LayoutParams) this.mSubtitleTextView.getLayoutParams();
            i12 += layoutParams2.topMargin + this.mSubtitleTextView.getMeasuredHeight() + layoutParams2.bottomMargin;
        }
        if (shouldLayout || shouldLayout2) {
            TextView textView = shouldLayout ? this.mTitleTextView : this.mSubtitleTextView;
            TextView textView2 = shouldLayout2 ? this.mSubtitleTextView : this.mTitleTextView;
            LayoutParams layoutParams3 = (LayoutParams) textView.getLayoutParams();
            LayoutParams layoutParams4 = (LayoutParams) textView2.getLayoutParams();
            boolean z3 = (shouldLayout && this.mTitleTextView.getMeasuredWidth() > 0) || (shouldLayout2 && this.mSubtitleTextView.getMeasuredWidth() > 0);
            int i13 = this.mGravity & 112;
            if (i13 == 48) {
                paddingTop = this.mTitleMarginTop + getPaddingTop() + layoutParams3.topMargin;
            } else if (i13 != 80) {
                int i14 = (((height - paddingTop2) - paddingBottom) - i12) / 2;
                if (i14 < layoutParams3.topMargin + this.mTitleMarginTop) {
                    max2 = layoutParams3.topMargin + this.mTitleMarginTop;
                } else {
                    int i15 = (((height - paddingBottom) - i12) - i14) - paddingTop2;
                    max2 = i15 < layoutParams3.bottomMargin + this.mTitleMarginBottom ? Math.max(0, i14 - ((layoutParams4.bottomMargin + this.mTitleMarginBottom) - i15)) : i14;
                }
                paddingTop = paddingTop2 + max2;
            } else {
                paddingTop = (((height - paddingBottom) - layoutParams4.bottomMargin) - this.mTitleMarginBottom) - i12;
            }
            if (z2) {
                int i16 = (z3 ? this.mTitleMarginStart : 0) - iArr[1];
                min -= Math.max(0, i16);
                iArr[1] = Math.max(0, -i16);
                int i17 = min;
                if (shouldLayout) {
                    LayoutParams layoutParams5 = (LayoutParams) this.mTitleTextView.getLayoutParams();
                    int measuredWidth = min - this.mTitleTextView.getMeasuredWidth();
                    int measuredHeight = this.mTitleTextView.getMeasuredHeight() + paddingTop;
                    this.mTitleTextView.layout(measuredWidth, paddingTop, min, measuredHeight);
                    i9 = measuredWidth - this.mTitleMarginEnd;
                    i8 = measuredHeight + layoutParams5.bottomMargin;
                } else {
                    i8 = paddingTop;
                    i9 = min;
                }
                if (shouldLayout2) {
                    LayoutParams layoutParams6 = (LayoutParams) this.mSubtitleTextView.getLayoutParams();
                    int i18 = i8 + layoutParams6.topMargin;
                    this.mSubtitleTextView.layout(i17 - this.mSubtitleTextView.getMeasuredWidth(), i18, i17, this.mSubtitleTextView.getMeasuredHeight() + i18);
                    i17 -= this.mTitleMarginEnd;
                    int i19 = layoutParams6.bottomMargin;
                }
                if (z3) {
                    min = Math.min(i9, i17);
                }
                max = i5;
            } else {
                int i20 = (z3 ? this.mTitleMarginStart : 0) - iArr[0];
                int max4 = i5 + Math.max(0, i20);
                iArr[0] = Math.max(0, -i20);
                int i21 = max4;
                if (shouldLayout) {
                    LayoutParams layoutParams7 = (LayoutParams) this.mTitleTextView.getLayoutParams();
                    int measuredWidth2 = this.mTitleTextView.getMeasuredWidth() + max4;
                    int measuredHeight2 = this.mTitleTextView.getMeasuredHeight() + paddingTop;
                    this.mTitleTextView.layout(max4, paddingTop, measuredWidth2, measuredHeight2);
                    i7 = measuredWidth2 + this.mTitleMarginEnd;
                    i6 = measuredHeight2 + layoutParams7.bottomMargin;
                } else {
                    i6 = paddingTop;
                    i7 = max4;
                }
                if (shouldLayout2) {
                    LayoutParams layoutParams8 = (LayoutParams) this.mSubtitleTextView.getLayoutParams();
                    int i22 = i6 + layoutParams8.topMargin;
                    int measuredWidth3 = this.mSubtitleTextView.getMeasuredWidth() + i21;
                    this.mSubtitleTextView.layout(i21, i22, measuredWidth3, this.mSubtitleTextView.getMeasuredHeight() + i22);
                    i21 = measuredWidth3 + this.mTitleMarginEnd;
                    int i23 = layoutParams8.bottomMargin;
                }
                max = z3 ? Math.max(i7, i21) : max4;
            }
        } else {
            max = i5;
        }
        addCustomViewsWithGravity(this.mTempViews, 3);
        int size = this.mTempViews.size();
        for (int i24 = 0; i24 < size; i24++) {
            max = layoutChildLeft(this.mTempViews.get(i24), max, iArr, minimumHeightCompat);
        }
        addCustomViewsWithGravity(this.mTempViews, 5);
        int size2 = this.mTempViews.size();
        for (int i25 = 0; i25 < size2; i25++) {
            min = layoutChildRight(this.mTempViews.get(i25), min, iArr, minimumHeightCompat);
        }
        addCustomViewsWithGravity(this.mTempViews, 1);
        int viewListMeasuredWidth = getViewListMeasuredWidth(this.mTempViews, iArr);
        int i26 = (paddingLeft + (((width - paddingLeft) - paddingRight) / 2)) - (viewListMeasuredWidth / 2);
        int i27 = i26 + viewListMeasuredWidth;
        if (i26 >= max) {
            max = i27 > min ? i26 - (i27 - min) : i26;
        }
        int size3 = this.mTempViews.size();
        int i28 = max;
        for (int i29 = 0; i29 < size3; i29++) {
            i28 = layoutChildLeft(this.mTempViews.get(i29), i28, iArr, minimumHeightCompat);
        }
        this.mTempViews.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        byte b;
        byte b2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10 = 0;
        int i11 = 0;
        int[] iArr = this.mTempMargins;
        if (ViewUtils.isLayoutRtl(this)) {
            b = 1;
            b2 = 0;
        } else {
            b = 0;
            b2 = 1;
        }
        int i12 = 0;
        if (shouldLayout(this.mNavButtonView)) {
            measureChildConstrained(this.mNavButtonView, i, 0, i2, 0, this.mMaxButtonHeight);
            i12 = this.mNavButtonView.getMeasuredWidth() + getHorizontalMargins(this.mNavButtonView);
            i10 = Math.max(0, this.mNavButtonView.getMeasuredHeight() + getVerticalMargins(this.mNavButtonView));
            i11 = ViewUtils.combineMeasuredStates(0, ViewCompat.getMeasuredState(this.mNavButtonView));
        }
        if (shouldLayout(this.mCollapseButtonView)) {
            measureChildConstrained(this.mCollapseButtonView, i, 0, i2, 0, this.mMaxButtonHeight);
            i12 = this.mCollapseButtonView.getMeasuredWidth() + getHorizontalMargins(this.mCollapseButtonView);
            i10 = Math.max(i10, this.mCollapseButtonView.getMeasuredHeight() + getVerticalMargins(this.mCollapseButtonView));
            i11 = ViewUtils.combineMeasuredStates(i11, ViewCompat.getMeasuredState(this.mCollapseButtonView));
        }
        int contentInsetStart = getContentInsetStart();
        int max = 0 + Math.max(contentInsetStart, i12);
        iArr[b == 1 ? 1 : 0] = Math.max(0, contentInsetStart - i12);
        if (shouldLayout(this.mMenuView)) {
            measureChildConstrained(this.mMenuView, i, max, i2, 0, this.mMaxButtonHeight);
            int measuredWidth = this.mMenuView.getMeasuredWidth();
            int horizontalMargins = getHorizontalMargins(this.mMenuView);
            int max2 = Math.max(i10, this.mMenuView.getMeasuredHeight() + getVerticalMargins(this.mMenuView));
            i3 = ViewUtils.combineMeasuredStates(i11, ViewCompat.getMeasuredState(this.mMenuView));
            i4 = max2;
            i5 = measuredWidth + horizontalMargins;
        } else {
            i3 = i11;
            i4 = i10;
            i5 = 0;
        }
        int contentInsetEnd = getContentInsetEnd();
        int max3 = max + Math.max(contentInsetEnd, i5);
        iArr[b2 == 1 ? 1 : 0] = Math.max(0, contentInsetEnd - i5);
        if (shouldLayout(this.mExpandedActionView)) {
            int measureChildCollapseMargins = max3 + measureChildCollapseMargins(this.mExpandedActionView, i, max3, i2, 0, iArr);
            i7 = Math.max(i4, this.mExpandedActionView.getMeasuredHeight() + getVerticalMargins(this.mExpandedActionView));
            i6 = ViewUtils.combineMeasuredStates(i3, ViewCompat.getMeasuredState(this.mExpandedActionView));
            i8 = measureChildCollapseMargins;
        } else {
            i6 = i3;
            i7 = i4;
            i8 = max3;
        }
        if (shouldLayout(this.mLogoView)) {
            i8 += measureChildCollapseMargins(this.mLogoView, i, i8, i2, 0, iArr);
            i7 = Math.max(i7, this.mLogoView.getMeasuredHeight() + getVerticalMargins(this.mLogoView));
            i9 = ViewUtils.combineMeasuredStates(i6, ViewCompat.getMeasuredState(this.mLogoView));
        } else {
            i9 = i6;
        }
        int childCount = getChildCount();
        int i13 = i7;
        int i14 = i8;
        int i15 = i13;
        for (int i16 = 0; i16 < childCount; i16++) {
            View childAt = getChildAt(i16);
            if (((LayoutParams) childAt.getLayoutParams()).mViewType == 0 && shouldLayout(childAt)) {
                i14 += measureChildCollapseMargins(childAt, i, i14, i2, 0, iArr);
                i15 = Math.max(i15, childAt.getMeasuredHeight() + getVerticalMargins(childAt));
                i9 = ViewUtils.combineMeasuredStates(i9, ViewCompat.getMeasuredState(childAt));
            }
        }
        int i17 = 0;
        int i18 = 0;
        int i19 = this.mTitleMarginTop + this.mTitleMarginBottom;
        int i20 = this.mTitleMarginStart + this.mTitleMarginEnd;
        if (shouldLayout(this.mTitleTextView)) {
            measureChildCollapseMargins(this.mTitleTextView, i, i14 + i20, i2, i19, iArr);
            i17 = this.mTitleTextView.getMeasuredWidth() + getHorizontalMargins(this.mTitleTextView);
            i18 = this.mTitleTextView.getMeasuredHeight() + getVerticalMargins(this.mTitleTextView);
            i9 = ViewUtils.combineMeasuredStates(i9, ViewCompat.getMeasuredState(this.mTitleTextView));
        }
        if (shouldLayout(this.mSubtitleTextView)) {
            i17 = Math.max(i17, measureChildCollapseMargins(this.mSubtitleTextView, i, i14 + i20, i2, i18 + i19, iArr));
            int measuredHeight = this.mSubtitleTextView.getMeasuredHeight();
            int verticalMargins = getVerticalMargins(this.mSubtitleTextView);
            i9 = ViewUtils.combineMeasuredStates(i9, ViewCompat.getMeasuredState(this.mSubtitleTextView));
            i18 += measuredHeight + verticalMargins;
        }
        int max4 = Math.max(i15, i18);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int resolveSizeAndState = ViewCompat.resolveSizeAndState(Math.max(i14 + i17 + paddingLeft + paddingRight, getSuggestedMinimumWidth()), i, (-16777216) & i9);
        int resolveSizeAndState2 = ViewCompat.resolveSizeAndState(Math.max(max4 + paddingTop + paddingBottom, getSuggestedMinimumHeight()), i2, i9 << 16);
        if (shouldCollapse()) {
            resolveSizeAndState2 = 0;
        }
        setMeasuredDimension(resolveSizeAndState, resolveSizeAndState2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        MenuItem findItem;
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        ActionMenuView actionMenuView = this.mMenuView;
        MenuBuilder peekMenu = actionMenuView != null ? actionMenuView.peekMenu() : null;
        if (savedState.expandedMenuItemId != 0 && this.mExpandedMenuPresenter != null && peekMenu != null && (findItem = peekMenu.findItem(savedState.expandedMenuItemId)) != null) {
            MenuItemCompat.expandActionView(findItem);
        }
        if (savedState.isOverflowOpen) {
            postShowOverflowMenu();
        }
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        if (Build.VERSION.SDK_INT >= 17) {
            super.onRtlPropertiesChanged(i);
        }
        RtlSpacingHelper rtlSpacingHelper = this.mContentInsets;
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        rtlSpacingHelper.setDirection(z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        ExpandedActionViewMenuPresenter expandedActionViewMenuPresenter = this.mExpandedMenuPresenter;
        if (expandedActionViewMenuPresenter != null && expandedActionViewMenuPresenter.mCurrentExpandedItem != null) {
            savedState.expandedMenuItemId = this.mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
        }
        savedState.isOverflowOpen = isOverflowMenuShowing();
        return savedState;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        if (actionMasked == 0) {
            this.mEatingTouch = false;
        }
        if (!this.mEatingTouch) {
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if (actionMasked == 0 && !onTouchEvent) {
                this.mEatingTouch = true;
            }
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.mEatingTouch = false;
            return true;
        }
        return true;
    }

    public void setCollapsible(boolean z) {
        this.mCollapsible = z;
        requestLayout();
    }

    public void setContentInsetsAbsolute(int i, int i2) {
        this.mContentInsets.setAbsolute(i, i2);
    }

    public void setContentInsetsRelative(int i, int i2) {
        this.mContentInsets.setRelative(i, i2);
    }

    public void setLogo(int i) {
        setLogo(this.mTintManager.getDrawable(i));
    }

    public void setLogo(Drawable drawable) {
        if (drawable != null) {
            ensureLogoView();
            if (this.mLogoView.getParent() == null) {
                addSystemView(this.mLogoView);
                updateChildVisibilityForExpandedActionView(this.mLogoView);
            }
        } else {
            ImageView imageView = this.mLogoView;
            if (imageView != null && imageView.getParent() != null) {
                removeView(this.mLogoView);
            }
        }
        ImageView imageView2 = this.mLogoView;
        if (imageView2 != null) {
            imageView2.setImageDrawable(drawable);
        }
    }

    public void setLogoDescription(int i) {
        setLogoDescription(getContext().getText(i));
    }

    public void setLogoDescription(CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence)) {
            ensureLogoView();
        }
        ImageView imageView = this.mLogoView;
        if (imageView != null) {
            imageView.setContentDescription(charSequence);
        }
    }

    public void setMenu(MenuBuilder menuBuilder, ActionMenuPresenter actionMenuPresenter) {
        if (menuBuilder == null && this.mMenuView == null) {
            return;
        }
        ensureMenuView();
        MenuBuilder peekMenu = this.mMenuView.peekMenu();
        if (peekMenu == menuBuilder) {
            return;
        }
        if (peekMenu != null) {
            peekMenu.removeMenuPresenter(this.mOuterActionMenuPresenter);
            peekMenu.removeMenuPresenter(this.mExpandedMenuPresenter);
        }
        if (this.mExpandedMenuPresenter == null) {
            this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
        }
        actionMenuPresenter.setExpandedActionViewsExclusive(true);
        if (menuBuilder != null) {
            menuBuilder.addMenuPresenter(actionMenuPresenter, this.mPopupContext);
            menuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
        } else {
            actionMenuPresenter.initForMenu(this.mPopupContext, null);
            this.mExpandedMenuPresenter.initForMenu(this.mPopupContext, null);
            actionMenuPresenter.updateMenuView(true);
            this.mExpandedMenuPresenter.updateMenuView(true);
        }
        this.mMenuView.setPopupTheme(this.mPopupTheme);
        this.mMenuView.setPresenter(actionMenuPresenter);
        this.mOuterActionMenuPresenter = actionMenuPresenter;
    }

    public void setMenuCallbacks(MenuPresenter.Callback callback, MenuBuilder.Callback callback2) {
        this.mActionMenuPresenterCallback = callback;
        this.mMenuBuilderCallback = callback2;
    }

    @Override // android.view.View
    public void setMinimumHeight(int i) {
        this.mMinHeight = i;
        super.setMinimumHeight(i);
    }

    public void setNavigationContentDescription(int i) {
        setNavigationContentDescription(i != 0 ? getContext().getText(i) : null);
    }

    public void setNavigationContentDescription(@Nullable CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence)) {
            ensureNavButtonView();
        }
        ImageButton imageButton = this.mNavButtonView;
        if (imageButton != null) {
            imageButton.setContentDescription(charSequence);
        }
    }

    public void setNavigationIcon(int i) {
        setNavigationIcon(this.mTintManager.getDrawable(i));
    }

    public void setNavigationIcon(@Nullable Drawable drawable) {
        if (drawable != null) {
            ensureNavButtonView();
            if (this.mNavButtonView.getParent() == null) {
                addSystemView(this.mNavButtonView);
                updateChildVisibilityForExpandedActionView(this.mNavButtonView);
            }
        } else {
            ImageButton imageButton = this.mNavButtonView;
            if (imageButton != null && imageButton.getParent() != null) {
                removeView(this.mNavButtonView);
            }
        }
        ImageButton imageButton2 = this.mNavButtonView;
        if (imageButton2 != null) {
            imageButton2.setImageDrawable(drawable);
        }
    }

    public void setNavigationOnClickListener(View.OnClickListener onClickListener) {
        ensureNavButtonView();
        this.mNavButtonView.setOnClickListener(onClickListener);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setPopupTheme(int i) {
        if (this.mPopupTheme != i) {
            this.mPopupTheme = i;
            if (i == 0) {
                this.mPopupContext = getContext();
            } else {
                this.mPopupContext = new ContextThemeWrapper(getContext(), i);
            }
        }
    }

    public void setSubtitle(int i) {
        setSubtitle(getContext().getText(i));
    }

    public void setSubtitle(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            TextView textView = this.mSubtitleTextView;
            if (textView != null && textView.getParent() != null) {
                removeView(this.mSubtitleTextView);
            }
        } else {
            if (this.mSubtitleTextView == null) {
                Context context = getContext();
                this.mSubtitleTextView = new TextView(context);
                this.mSubtitleTextView.setSingleLine();
                this.mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                int i = this.mSubtitleTextAppearance;
                if (i != 0) {
                    this.mSubtitleTextView.setTextAppearance(context, i);
                }
                int i2 = this.mSubtitleTextColor;
                if (i2 != 0) {
                    this.mSubtitleTextView.setTextColor(i2);
                }
            }
            if (this.mSubtitleTextView.getParent() == null) {
                addSystemView(this.mSubtitleTextView);
                updateChildVisibilityForExpandedActionView(this.mSubtitleTextView);
            }
        }
        TextView textView2 = this.mSubtitleTextView;
        if (textView2 != null) {
            textView2.setText(charSequence);
        }
        this.mSubtitleText = charSequence;
    }

    public void setSubtitleTextAppearance(Context context, int i) {
        this.mSubtitleTextAppearance = i;
        TextView textView = this.mSubtitleTextView;
        if (textView != null) {
            textView.setTextAppearance(context, i);
        }
    }

    public void setSubtitleTextColor(int i) {
        this.mSubtitleTextColor = i;
        TextView textView = this.mSubtitleTextView;
        if (textView != null) {
            textView.setTextColor(i);
        }
    }

    public void setTitle(int i) {
        setTitle(getContext().getText(i));
    }

    public void setTitle(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            TextView textView = this.mTitleTextView;
            if (textView != null && textView.getParent() != null) {
                removeView(this.mTitleTextView);
            }
        } else {
            if (this.mTitleTextView == null) {
                Context context = getContext();
                this.mTitleTextView = new TextView(context);
                this.mTitleTextView.setSingleLine();
                this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                int i = this.mTitleTextAppearance;
                if (i != 0) {
                    this.mTitleTextView.setTextAppearance(context, i);
                }
                int i2 = this.mTitleTextColor;
                if (i2 != 0) {
                    this.mTitleTextView.setTextColor(i2);
                }
            }
            if (this.mTitleTextView.getParent() == null) {
                addSystemView(this.mTitleTextView);
                updateChildVisibilityForExpandedActionView(this.mTitleTextView);
            }
        }
        TextView textView2 = this.mTitleTextView;
        if (textView2 != null) {
            textView2.setText(charSequence);
        }
        this.mTitleText = charSequence;
    }

    public void setTitleTextAppearance(Context context, int i) {
        this.mTitleTextAppearance = i;
        TextView textView = this.mTitleTextView;
        if (textView != null) {
            textView.setTextAppearance(context, i);
        }
    }

    public void setTitleTextColor(int i) {
        this.mTitleTextColor = i;
        TextView textView = this.mTitleTextView;
        if (textView != null) {
            textView.setTextColor(i);
        }
    }

    public boolean showOverflowMenu() {
        ActionMenuView actionMenuView = this.mMenuView;
        return actionMenuView != null && actionMenuView.showOverflowMenu();
    }
}