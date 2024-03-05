package android.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;

/* loaded from: MenuItem.class */
public interface MenuItem {
    public static final int SHOW_AS_ACTION_NEVER = 0;
    public static final int SHOW_AS_ACTION_IF_ROOM = 1;
    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    public static final int SHOW_AS_ACTION_WITH_TEXT = 4;
    public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;

    /* loaded from: MenuItem$OnActionExpandListener.class */
    public interface OnActionExpandListener {
        boolean onMenuItemActionExpand(MenuItem menuItem);

        boolean onMenuItemActionCollapse(MenuItem menuItem);
    }

    /* loaded from: MenuItem$OnMenuItemClickListener.class */
    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    int getItemId();

    int getGroupId();

    int getOrder();

    MenuItem setTitle(CharSequence charSequence);

    MenuItem setTitle(int i);

    CharSequence getTitle();

    MenuItem setTitleCondensed(CharSequence charSequence);

    CharSequence getTitleCondensed();

    MenuItem setIcon(Drawable drawable);

    MenuItem setIcon(int i);

    Drawable getIcon();

    MenuItem setIntent(Intent intent);

    Intent getIntent();

    MenuItem setShortcut(char c, char c2);

    MenuItem setNumericShortcut(char c);

    char getNumericShortcut();

    MenuItem setAlphabeticShortcut(char c);

    char getAlphabeticShortcut();

    MenuItem setCheckable(boolean z);

    boolean isCheckable();

    MenuItem setChecked(boolean z);

    boolean isChecked();

    MenuItem setVisible(boolean z);

    boolean isVisible();

    MenuItem setEnabled(boolean z);

    boolean isEnabled();

    boolean hasSubMenu();

    SubMenu getSubMenu();

    MenuItem setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener);

    ContextMenu.ContextMenuInfo getMenuInfo();

    void setShowAsAction(int i);

    MenuItem setShowAsActionFlags(int i);

    MenuItem setActionView(View view);

    MenuItem setActionView(int i);

    View getActionView();

    MenuItem setActionProvider(ActionProvider actionProvider);

    ActionProvider getActionProvider();

    boolean expandActionView();

    boolean collapseActionView();

    boolean isActionViewExpanded();

    MenuItem setOnActionExpandListener(OnActionExpandListener onActionExpandListener);
}