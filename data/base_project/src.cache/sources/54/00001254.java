package android.support.v7.internal.view.menu;

import android.support.v4.view.ActionProvider;
import android.support.v7.internal.view.menu.MenuItemWrapperICS;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.View;

/* loaded from: MenuItemWrapperJB.class */
class MenuItemWrapperJB extends MenuItemWrapperICS {

    /* loaded from: MenuItemWrapperJB$ActionProviderWrapperJB.class */
    class ActionProviderWrapperJB extends MenuItemWrapperICS.ActionProviderWrapper implements ActionProvider.VisibilityListener {
        ActionProvider.VisibilityListener mListener;
        final MenuItemWrapperJB this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ActionProviderWrapperJB(MenuItemWrapperJB menuItemWrapperJB, android.support.v4.view.ActionProvider actionProvider) {
            super(menuItemWrapperJB, actionProvider);
            this.this$0 = menuItemWrapperJB;
        }

        @Override // android.view.ActionProvider
        public boolean isVisible() {
            return this.mInner.isVisible();
        }

        @Override // android.support.v4.view.ActionProvider.VisibilityListener
        public void onActionProviderVisibilityChanged(boolean z) {
            ActionProvider.VisibilityListener visibilityListener = this.mListener;
            if (visibilityListener != null) {
                visibilityListener.onActionProviderVisibilityChanged(z);
            }
        }

        @Override // android.view.ActionProvider
        public View onCreateActionView(MenuItem menuItem) {
            return this.mInner.onCreateActionView(menuItem);
        }

        @Override // android.view.ActionProvider
        public boolean overridesItemVisibility() {
            return this.mInner.overridesItemVisibility();
        }

        @Override // android.view.ActionProvider
        public void refreshVisibility() {
            this.mInner.refreshVisibility();
        }

        @Override // android.view.ActionProvider
        public void setVisibilityListener(ActionProvider.VisibilityListener visibilityListener) {
            this.mListener = visibilityListener;
            this.mInner.setVisibilityListener(visibilityListener != null ? this : null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuItemWrapperJB(MenuItem menuItem) {
        super(menuItem, false);
    }

    @Override // android.support.v7.internal.view.menu.MenuItemWrapperICS
    MenuItemWrapperICS.ActionProviderWrapper createActionProviderWrapper(android.support.v4.view.ActionProvider actionProvider) {
        return new ActionProviderWrapperJB(this, actionProvider);
    }
}