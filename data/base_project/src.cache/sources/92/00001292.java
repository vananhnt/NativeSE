package android.support.v7.internal.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.internal.widget.FitWindowsViewGroup;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/* loaded from: FitWindowsLinearLayout.class */
public class FitWindowsLinearLayout extends LinearLayout implements FitWindowsViewGroup {
    private FitWindowsViewGroup.OnFitSystemWindowsListener mListener;

    public FitWindowsLinearLayout(Context context) {
        super(context);
    }

    public FitWindowsLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public boolean fitSystemWindows(Rect rect) {
        FitWindowsViewGroup.OnFitSystemWindowsListener onFitSystemWindowsListener = this.mListener;
        if (onFitSystemWindowsListener != null) {
            onFitSystemWindowsListener.onFitSystemWindows(rect);
        }
        return super.fitSystemWindows(rect);
    }

    @Override // android.support.v7.internal.widget.FitWindowsViewGroup
    public void setOnFitSystemWindowsListener(FitWindowsViewGroup.OnFitSystemWindowsListener onFitSystemWindowsListener) {
        this.mListener = onFitSystemWindowsListener;
    }
}