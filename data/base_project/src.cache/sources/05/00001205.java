package android.support.v7.app;

import android.annotation.TargetApi;
import android.support.v7.internal.view.SupportActionModeWrapper;
import android.support.v7.internal.widget.NativeActionModeAwareLayout;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.View;

/* JADX INFO: Access modifiers changed from: package-private */
@TargetApi(11)
/* loaded from: ActionBarActivityDelegateHC.class */
public class ActionBarActivityDelegateHC extends ActionBarActivityDelegateBase implements NativeActionModeAwareLayout.OnActionModeForChildListener {
    private NativeActionModeAwareLayout mNativeActionModeAwareLayout;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActionBarActivityDelegateHC(ActionBarActivity actionBarActivity) {
        super(actionBarActivity);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBarActivityDelegateBase, android.support.v7.app.ActionBarActivityDelegate
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override // android.support.v7.app.ActionBarActivityDelegateBase
    void onSubDecorInstalled() {
        this.mNativeActionModeAwareLayout = (NativeActionModeAwareLayout) this.mActivity.findViewById(16908290);
        NativeActionModeAwareLayout nativeActionModeAwareLayout = this.mNativeActionModeAwareLayout;
        if (nativeActionModeAwareLayout != null) {
            nativeActionModeAwareLayout.setActionModeForChildListener(this);
        }
    }

    @Override // android.support.v7.internal.widget.NativeActionModeAwareLayout.OnActionModeForChildListener
    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        android.support.v7.view.ActionMode startSupportActionMode = startSupportActionMode(new SupportActionModeWrapper.CallbackWrapper(view.getContext(), callback));
        if (startSupportActionMode != null) {
            return new SupportActionModeWrapper(this.mActivity, startSupportActionMode);
        }
        return null;
    }
}