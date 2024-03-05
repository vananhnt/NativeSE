package android.support.v7.internal.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.internal.view.menu.MenuWrapperFactory;
import android.support.v7.view.ActionMode;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

@TargetApi(11)
/* loaded from: SupportActionModeWrapper.class */
public class SupportActionModeWrapper extends ActionMode {
    final MenuInflater mInflater;
    final android.support.v7.view.ActionMode mWrappedObject;

    /* loaded from: SupportActionModeWrapper$CallbackWrapper.class */
    public static class CallbackWrapper implements ActionMode.Callback {
        final SimpleArrayMap<android.support.v7.view.ActionMode, SupportActionModeWrapper> mActionModes = new SimpleArrayMap<>();
        final Context mContext;
        final ActionMode.Callback mWrappedCallback;

        public CallbackWrapper(Context context, ActionMode.Callback callback) {
            this.mContext = context;
            this.mWrappedCallback = callback;
        }

        private android.view.ActionMode getActionModeWrapper(android.support.v7.view.ActionMode actionMode) {
            SupportActionModeWrapper supportActionModeWrapper = this.mActionModes.get(actionMode);
            if (supportActionModeWrapper != null) {
                return supportActionModeWrapper;
            }
            SupportActionModeWrapper supportActionModeWrapper2 = new SupportActionModeWrapper(this.mContext, actionMode);
            this.mActionModes.put(actionMode, supportActionModeWrapper2);
            return supportActionModeWrapper2;
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public boolean onActionItemClicked(android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
            return this.mWrappedCallback.onActionItemClicked(getActionModeWrapper(actionMode), MenuWrapperFactory.createMenuItemWrapper(menuItem));
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            return this.mWrappedCallback.onCreateActionMode(getActionModeWrapper(actionMode), MenuWrapperFactory.createMenuWrapper(menu));
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public void onDestroyActionMode(android.support.v7.view.ActionMode actionMode) {
            this.mWrappedCallback.onDestroyActionMode(getActionModeWrapper(actionMode));
        }

        @Override // android.support.v7.view.ActionMode.Callback
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            return this.mWrappedCallback.onPrepareActionMode(getActionModeWrapper(actionMode), MenuWrapperFactory.createMenuWrapper(menu));
        }
    }

    public SupportActionModeWrapper(Context context, android.support.v7.view.ActionMode actionMode) {
        this.mWrappedObject = actionMode;
        this.mInflater = new SupportMenuInflater(context);
    }

    @Override // android.view.ActionMode
    public void finish() {
        this.mWrappedObject.finish();
    }

    @Override // android.view.ActionMode
    public View getCustomView() {
        return this.mWrappedObject.getCustomView();
    }

    @Override // android.view.ActionMode
    public Menu getMenu() {
        return MenuWrapperFactory.createMenuWrapper(this.mWrappedObject.getMenu());
    }

    @Override // android.view.ActionMode
    public MenuInflater getMenuInflater() {
        return this.mInflater;
    }

    @Override // android.view.ActionMode
    public CharSequence getSubtitle() {
        return this.mWrappedObject.getSubtitle();
    }

    @Override // android.view.ActionMode
    public Object getTag() {
        return this.mWrappedObject.getTag();
    }

    @Override // android.view.ActionMode
    public CharSequence getTitle() {
        return this.mWrappedObject.getTitle();
    }

    @Override // android.view.ActionMode
    public boolean getTitleOptionalHint() {
        return this.mWrappedObject.getTitleOptionalHint();
    }

    @Override // android.view.ActionMode
    public void invalidate() {
        this.mWrappedObject.invalidate();
    }

    @Override // android.view.ActionMode
    public boolean isTitleOptional() {
        return this.mWrappedObject.isTitleOptional();
    }

    @Override // android.view.ActionMode
    public void setCustomView(View view) {
        this.mWrappedObject.setCustomView(view);
    }

    @Override // android.view.ActionMode
    public void setSubtitle(int i) {
        this.mWrappedObject.setSubtitle(i);
    }

    @Override // android.view.ActionMode
    public void setSubtitle(CharSequence charSequence) {
        this.mWrappedObject.setSubtitle(charSequence);
    }

    @Override // android.view.ActionMode
    public void setTag(Object obj) {
        this.mWrappedObject.setTag(obj);
    }

    @Override // android.view.ActionMode
    public void setTitle(int i) {
        this.mWrappedObject.setTitle(i);
    }

    @Override // android.view.ActionMode
    public void setTitle(CharSequence charSequence) {
        this.mWrappedObject.setTitle(charSequence);
    }

    @Override // android.view.ActionMode
    public void setTitleOptionalHint(boolean z) {
        this.mWrappedObject.setTitleOptionalHint(z);
    }
}