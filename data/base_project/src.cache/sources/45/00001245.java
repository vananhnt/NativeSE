package android.support.v7.internal.view.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R;
import android.support.v7.internal.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

/* loaded from: ListMenuItemView.class */
public class ListMenuItemView extends LinearLayout implements MenuView.ItemView {
    private static final String TAG = "ListMenuItemView";
    private Drawable mBackground;
    private CheckBox mCheckBox;
    private Context mContext;
    private boolean mForceShowIcon;
    private ImageView mIconView;
    private LayoutInflater mInflater;
    private MenuItemImpl mItemData;
    private int mMenuType;
    private boolean mPreserveIconSpacing;
    private RadioButton mRadioButton;
    private TextView mShortcutView;
    private int mTextAppearance;
    private Context mTextAppearanceContext;
    private TextView mTitleView;

    public ListMenuItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ListMenuItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.mContext = context;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MenuView, i, 0);
        this.mBackground = obtainStyledAttributes.getDrawable(R.styleable.MenuView_android_itemBackground);
        this.mTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.MenuView_android_itemTextAppearance, -1);
        this.mPreserveIconSpacing = obtainStyledAttributes.getBoolean(R.styleable.MenuView_preserveIconSpacing, false);
        this.mTextAppearanceContext = context;
        obtainStyledAttributes.recycle();
    }

    private LayoutInflater getInflater() {
        if (this.mInflater == null) {
            this.mInflater = LayoutInflater.from(this.mContext);
        }
        return this.mInflater;
    }

    private void insertCheckBox() {
        this.mCheckBox = (CheckBox) getInflater().inflate(R.layout.abc_list_menu_item_checkbox, (ViewGroup) this, false);
        addView(this.mCheckBox);
    }

    private void insertIconView() {
        this.mIconView = (ImageView) getInflater().inflate(R.layout.abc_list_menu_item_icon, (ViewGroup) this, false);
        addView(this.mIconView, 0);
    }

    private void insertRadioButton() {
        this.mRadioButton = (RadioButton) getInflater().inflate(R.layout.abc_list_menu_item_radio, (ViewGroup) this, false);
        addView(this.mRadioButton);
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void initialize(MenuItemImpl menuItemImpl, int i) {
        this.mItemData = menuItemImpl;
        this.mMenuType = i;
        setVisibility(menuItemImpl.isVisible() ? 0 : 8);
        setTitle(menuItemImpl.getTitleForItemView(this));
        setCheckable(menuItemImpl.isCheckable());
        setShortcut(menuItemImpl.shouldShowShortcut(), menuItemImpl.getShortcut());
        setIcon(menuItemImpl.getIcon());
        setEnabled(menuItemImpl.isEnabled());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        setBackgroundDrawable(this.mBackground);
        this.mTitleView = (TextView) findViewById(R.id.title);
        int i = this.mTextAppearance;
        if (i != -1) {
            this.mTitleView.setTextAppearance(this.mTextAppearanceContext, i);
        }
        this.mShortcutView = (TextView) findViewById(R.id.shortcut);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int i, int i2) {
        if (this.mIconView != null && this.mPreserveIconSpacing) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mIconView.getLayoutParams();
            if (layoutParams.height > 0 && layoutParams2.width <= 0) {
                layoutParams2.width = layoutParams.height;
            }
        }
        super.onMeasure(i, i2);
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void setCheckable(boolean z) {
        CompoundButton compoundButton;
        TextView textView;
        if (!z && this.mRadioButton == null && this.mCheckBox == null) {
            return;
        }
        if (this.mItemData.isExclusiveCheckable()) {
            if (this.mRadioButton == null) {
                insertRadioButton();
            }
            compoundButton = this.mRadioButton;
            textView = this.mCheckBox;
        } else {
            if (this.mCheckBox == null) {
                insertCheckBox();
            }
            compoundButton = this.mCheckBox;
            textView = this.mRadioButton;
        }
        if (!z) {
            CheckBox checkBox = this.mCheckBox;
            if (checkBox != null) {
                checkBox.setVisibility(8);
            }
            RadioButton radioButton = this.mRadioButton;
            if (radioButton != null) {
                radioButton.setVisibility(8);
                return;
            }
            return;
        }
        compoundButton.setChecked(this.mItemData.isChecked());
        int i = z ? 0 : 8;
        if (compoundButton.getVisibility() != i) {
            compoundButton.setVisibility(i);
        }
        if (textView == null || textView.getVisibility() == 8) {
            return;
        }
        textView.setVisibility(8);
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void setChecked(boolean z) {
        CompoundButton compoundButton;
        if (this.mItemData.isExclusiveCheckable()) {
            if (this.mRadioButton == null) {
                insertRadioButton();
            }
            compoundButton = this.mRadioButton;
        } else {
            if (this.mCheckBox == null) {
                insertCheckBox();
            }
            compoundButton = this.mCheckBox;
        }
        compoundButton.setChecked(z);
    }

    public void setForceShowIcon(boolean z) {
        this.mForceShowIcon = z;
        this.mPreserveIconSpacing = z;
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void setIcon(Drawable drawable) {
        boolean z = this.mItemData.shouldShowIcon() || this.mForceShowIcon;
        if (z || this.mPreserveIconSpacing) {
            if (this.mIconView == null && drawable == null && !this.mPreserveIconSpacing) {
                return;
            }
            if (this.mIconView == null) {
                insertIconView();
            }
            if (drawable == null && !this.mPreserveIconSpacing) {
                this.mIconView.setVisibility(8);
                return;
            }
            ImageView imageView = this.mIconView;
            if (!z) {
                drawable = null;
            }
            imageView.setImageDrawable(drawable);
            if (this.mIconView.getVisibility() != 0) {
                this.mIconView.setVisibility(0);
            }
        }
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void setShortcut(boolean z, char c) {
        int i = (z && this.mItemData.shouldShowShortcut()) ? 0 : 8;
        if (i == 0) {
            this.mShortcutView.setText(this.mItemData.getShortcutLabel());
        }
        if (this.mShortcutView.getVisibility() != i) {
            this.mShortcutView.setVisibility(i);
        }
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public void setTitle(CharSequence charSequence) {
        if (charSequence == null) {
            if (this.mTitleView.getVisibility() != 8) {
                this.mTitleView.setVisibility(8);
                return;
            }
            return;
        }
        this.mTitleView.setText(charSequence);
        if (this.mTitleView.getVisibility() != 0) {
            this.mTitleView.setVisibility(0);
        }
    }

    @Override // android.support.v7.internal.view.menu.MenuView.ItemView
    public boolean showsIcon() {
        return this.mForceShowIcon;
    }
}