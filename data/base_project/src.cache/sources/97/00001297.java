package android.support.v7.internal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.View;

@TargetApi(11)
/* loaded from: NativeActionModeAwareLayout.class */
public class NativeActionModeAwareLayout extends ContentFrameLayout {
    private OnActionModeForChildListener mActionModeForChildListener;

    /* loaded from: NativeActionModeAwareLayout$OnActionModeForChildListener.class */
    public interface OnActionModeForChildListener {
        ActionMode startActionModeForChild(View view, ActionMode.Callback callback);
    }

    public NativeActionModeAwareLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setActionModeForChildListener(OnActionModeForChildListener onActionModeForChildListener) {
        this.mActionModeForChildListener = onActionModeForChildListener;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        OnActionModeForChildListener onActionModeForChildListener = this.mActionModeForChildListener;
        return onActionModeForChildListener != null ? onActionModeForChildListener.startActionModeForChild(view, callback) : super.startActionModeForChild(view, callback);
    }
}