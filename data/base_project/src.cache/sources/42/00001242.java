package android.support.v7.internal.view.menu;

import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.internal.view.SupportSubMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: BaseMenuWrapper.class */
public abstract class BaseMenuWrapper<T> extends BaseWrapper<T> {
    private HashMap<MenuItem, SupportMenuItem> mMenuItems;
    private HashMap<SubMenu, SubMenu> mSubMenus;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseMenuWrapper(T t) {
        super(t);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final SupportMenuItem getMenuItemWrapper(MenuItem menuItem) {
        SupportMenuItem supportMenuItem;
        if (menuItem != null) {
            if (this.mMenuItems == null) {
                this.mMenuItems = new HashMap<>();
            }
            SupportMenuItem supportMenuItem2 = this.mMenuItems.get(menuItem);
            if (supportMenuItem2 == null) {
                SupportMenuItem createSupportMenuItemWrapper = MenuWrapperFactory.createSupportMenuItemWrapper(menuItem);
                this.mMenuItems.put(menuItem, createSupportMenuItemWrapper);
                supportMenuItem = createSupportMenuItemWrapper;
            } else {
                supportMenuItem = supportMenuItem2;
            }
            return supportMenuItem;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final SubMenu getSubMenuWrapper(SubMenu subMenu) {
        SupportSubMenu supportSubMenu;
        if (subMenu != null) {
            if (this.mSubMenus == null) {
                this.mSubMenus = new HashMap<>();
            }
            SubMenu subMenu2 = this.mSubMenus.get(subMenu);
            if (subMenu2 == null) {
                SupportSubMenu createSupportSubMenuWrapper = MenuWrapperFactory.createSupportSubMenuWrapper(subMenu);
                this.mSubMenus.put(subMenu, createSupportSubMenuWrapper);
                supportSubMenu = createSupportSubMenuWrapper;
            } else {
                supportSubMenu = subMenu2;
            }
            return supportSubMenu;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalClear() {
        HashMap<MenuItem, SupportMenuItem> hashMap = this.mMenuItems;
        if (hashMap != null) {
            hashMap.clear();
        }
        HashMap<SubMenu, SubMenu> hashMap2 = this.mSubMenus;
        if (hashMap2 != null) {
            hashMap2.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalRemoveGroup(int i) {
        HashMap<MenuItem, SupportMenuItem> hashMap = this.mMenuItems;
        if (hashMap == null) {
            return;
        }
        Iterator<MenuItem> it = hashMap.keySet().iterator();
        while (it.hasNext()) {
            if (i == it.next().getGroupId()) {
                it.remove();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalRemoveItem(int i) {
        HashMap<MenuItem, SupportMenuItem> hashMap = this.mMenuItems;
        if (hashMap == null) {
            return;
        }
        Iterator<MenuItem> it = hashMap.keySet().iterator();
        while (it.hasNext()) {
            if (i == it.next().getItemId()) {
                it.remove();
                return;
            }
        }
    }
}