package android.support.v7.internal.view.menu;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.internal.view.SupportMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

/* loaded from: MenuWrapperICS.class */
class MenuWrapperICS extends BaseMenuWrapper<Menu> implements SupportMenu {
    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuWrapperICS(Menu menu) {
        super(menu);
    }

    @Override // android.view.Menu
    public MenuItem add(int i) {
        return getMenuItemWrapper(((Menu) this.mWrappedObject).add(i));
    }

    @Override // android.view.Menu
    public MenuItem add(int i, int i2, int i3, int i4) {
        return getMenuItemWrapper(((Menu) this.mWrappedObject).add(i, i2, i3, i4));
    }

    @Override // android.view.Menu
    public MenuItem add(int i, int i2, int i3, CharSequence charSequence) {
        return getMenuItemWrapper(((Menu) this.mWrappedObject).add(i, i2, i3, charSequence));
    }

    @Override // android.view.Menu
    public MenuItem add(CharSequence charSequence) {
        return getMenuItemWrapper(((Menu) this.mWrappedObject).add(charSequence));
    }

    @Override // android.view.Menu
    public int addIntentOptions(int i, int i2, int i3, ComponentName componentName, Intent[] intentArr, Intent intent, int i4, MenuItem[] menuItemArr) {
        MenuItem[] menuItemArr2 = menuItemArr != null ? new MenuItem[menuItemArr.length] : null;
        int addIntentOptions = ((Menu) this.mWrappedObject).addIntentOptions(i, i2, i3, componentName, intentArr, intent, i4, menuItemArr2);
        if (menuItemArr2 != null) {
            int length = menuItemArr2.length;
            for (int i5 = 0; i5 < length; i5++) {
                menuItemArr[i5] = getMenuItemWrapper(menuItemArr2[i5]);
            }
        }
        return addIntentOptions;
    }

    @Override // android.view.Menu
    public SubMenu addSubMenu(int i) {
        return getSubMenuWrapper(((Menu) this.mWrappedObject).addSubMenu(i));
    }

    @Override // android.view.Menu
    public SubMenu addSubMenu(int i, int i2, int i3, int i4) {
        return getSubMenuWrapper(((Menu) this.mWrappedObject).addSubMenu(i, i2, i3, i4));
    }

    @Override // android.view.Menu
    public SubMenu addSubMenu(int i, int i2, int i3, CharSequence charSequence) {
        return getSubMenuWrapper(((Menu) this.mWrappedObject).addSubMenu(i, i2, i3, charSequence));
    }

    @Override // android.view.Menu
    public SubMenu addSubMenu(CharSequence charSequence) {
        return getSubMenuWrapper(((Menu) this.mWrappedObject).addSubMenu(charSequence));
    }

    @Override // android.view.Menu
    public void clear() {
        internalClear();
        ((Menu) this.mWrappedObject).clear();
    }

    @Override // android.view.Menu
    public void close() {
        ((Menu) this.mWrappedObject).close();
    }

    @Override // android.view.Menu
    public MenuItem findItem(int i) {
        return getMenuItemWrapper(((Menu) this.mWrappedObject).findItem(i));
    }

    @Override // android.view.Menu
    public MenuItem getItem(int i) {
        return getMenuItemWrapper(((Menu) this.mWrappedObject).getItem(i));
    }

    @Override // android.view.Menu
    public boolean hasVisibleItems() {
        return ((Menu) this.mWrappedObject).hasVisibleItems();
    }

    @Override // android.view.Menu
    public boolean isShortcutKey(int i, KeyEvent keyEvent) {
        return ((Menu) this.mWrappedObject).isShortcutKey(i, keyEvent);
    }

    @Override // android.view.Menu
    public boolean performIdentifierAction(int i, int i2) {
        return ((Menu) this.mWrappedObject).performIdentifierAction(i, i2);
    }

    @Override // android.view.Menu
    public boolean performShortcut(int i, KeyEvent keyEvent, int i2) {
        return ((Menu) this.mWrappedObject).performShortcut(i, keyEvent, i2);
    }

    @Override // android.view.Menu
    public void removeGroup(int i) {
        internalRemoveGroup(i);
        ((Menu) this.mWrappedObject).removeGroup(i);
    }

    @Override // android.view.Menu
    public void removeItem(int i) {
        internalRemoveItem(i);
        ((Menu) this.mWrappedObject).removeItem(i);
    }

    @Override // android.view.Menu
    public void setGroupCheckable(int i, boolean z, boolean z2) {
        ((Menu) this.mWrappedObject).setGroupCheckable(i, z, z2);
    }

    @Override // android.view.Menu
    public void setGroupEnabled(int i, boolean z) {
        ((Menu) this.mWrappedObject).setGroupEnabled(i, z);
    }

    @Override // android.view.Menu
    public void setGroupVisible(int i, boolean z) {
        ((Menu) this.mWrappedObject).setGroupVisible(i, z);
    }

    @Override // android.view.Menu
    public void setQwertyMode(boolean z) {
        ((Menu) this.mWrappedObject).setQwertyMode(z);
    }

    @Override // android.view.Menu
    public int size() {
        return ((Menu) this.mWrappedObject).size();
    }
}