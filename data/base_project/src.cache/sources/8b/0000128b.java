package android.support.v7.internal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

/* loaded from: AppCompatPopupWindow.class */
public class AppCompatPopupWindow extends PopupWindow {
    private final boolean mOverlapAnchor;

    public AppCompatPopupWindow(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.PopupWindow, i, 0);
        this.mOverlapAnchor = obtainStyledAttributes.getBoolean(R.styleable.PopupWindow_overlapAnchor, false);
        setBackgroundDrawable(obtainStyledAttributes.getDrawable(R.styleable.PopupWindow_android_popupBackground));
        obtainStyledAttributes.recycle();
    }

    @Override // android.widget.PopupWindow
    public void showAsDropDown(View view, int i, int i2) {
        if (Build.VERSION.SDK_INT < 21 && this.mOverlapAnchor) {
            i2 -= view.getHeight();
        }
        super.showAsDropDown(view, i, i2);
    }

    @Override // android.widget.PopupWindow
    @TargetApi(19)
    public void showAsDropDown(View view, int i, int i2, int i3) {
        if (Build.VERSION.SDK_INT < 21 && this.mOverlapAnchor) {
            i2 -= view.getHeight();
        }
        super.showAsDropDown(view, i, i2, i3);
    }

    @Override // android.widget.PopupWindow
    public void update(View view, int i, int i2, int i3, int i4) {
        if (Build.VERSION.SDK_INT < 21 && this.mOverlapAnchor) {
            i2 -= view.getHeight();
        }
        super.update(view, i, i2, i3, i4);
    }
}