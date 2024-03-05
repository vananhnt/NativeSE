package android.support.v7.widget;

import android.support.v7.internal.app.WindowCallback;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/* loaded from: WindowCallbackWrapper.class */
public class WindowCallbackWrapper implements WindowCallback {
    private WindowCallback mWrapped;

    public WindowCallbackWrapper(WindowCallback windowCallback) {
        if (windowCallback == null) {
            throw new IllegalArgumentException("Window callback may not be null");
        }
        this.mWrapped = windowCallback;
    }

    @Override // android.support.v7.internal.app.WindowCallback
    public boolean onCreatePanelMenu(int i, Menu menu) {
        return this.mWrapped.onCreatePanelMenu(i, menu);
    }

    @Override // android.support.v7.internal.app.WindowCallback
    public View onCreatePanelView(int i) {
        return this.mWrapped.onCreatePanelView(i);
    }

    @Override // android.support.v7.internal.app.WindowCallback
    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        return this.mWrapped.onMenuItemSelected(i, menuItem);
    }

    @Override // android.support.v7.internal.app.WindowCallback
    public boolean onMenuOpened(int i, Menu menu) {
        return this.mWrapped.onMenuOpened(i, menu);
    }

    @Override // android.support.v7.internal.app.WindowCallback
    public void onPanelClosed(int i, Menu menu) {
        this.mWrapped.onPanelClosed(i, menu);
    }

    @Override // android.support.v7.internal.app.WindowCallback
    public boolean onPreparePanel(int i, View view, Menu menu) {
        return this.mWrapped.onPreparePanel(i, view, menu);
    }

    @Override // android.support.v7.internal.app.WindowCallback
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return this.mWrapped.startActionMode(callback);
    }
}