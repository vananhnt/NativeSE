package android.support.v7.internal.view.menu;

import android.graphics.drawable.Drawable;
import android.support.v4.internal.view.SupportSubMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/* loaded from: SubMenuWrapperICS.class */
class SubMenuWrapperICS extends MenuWrapperICS implements SupportSubMenu {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SubMenuWrapperICS(SubMenu subMenu) {
        super(subMenu);
    }

    @Override // android.view.SubMenu
    public void clearHeader() {
        ((SubMenu) this.mWrappedObject).clearHeader();
    }

    @Override // android.view.SubMenu
    public MenuItem getItem() {
        return getMenuItemWrapper(((SubMenu) this.mWrappedObject).getItem());
    }

    @Override // android.support.v7.internal.view.menu.BaseWrapper
    public SubMenu getWrappedObject() {
        return (SubMenu) this.mWrappedObject;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderIcon(int i) {
        ((SubMenu) this.mWrappedObject).setHeaderIcon(i);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderIcon(Drawable drawable) {
        ((SubMenu) this.mWrappedObject).setHeaderIcon(drawable);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderTitle(int i) {
        ((SubMenu) this.mWrappedObject).setHeaderTitle(i);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderTitle(CharSequence charSequence) {
        ((SubMenu) this.mWrappedObject).setHeaderTitle(charSequence);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setHeaderView(View view) {
        ((SubMenu) this.mWrappedObject).setHeaderView(view);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setIcon(int i) {
        ((SubMenu) this.mWrappedObject).setIcon(i);
        return this;
    }

    @Override // android.view.SubMenu
    public SubMenu setIcon(Drawable drawable) {
        ((SubMenu) this.mWrappedObject).setIcon(drawable);
        return this;
    }
}