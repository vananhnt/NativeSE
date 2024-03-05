package com.android.internal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import com.android.internal.R;

/* loaded from: CheckableLinearLayout.class */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private CheckBox mCheckBox;

    public CheckableLinearLayout(Context context) {
        super(context);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mCheckBox = (CheckBox) findViewById(R.id.check);
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean checked) {
        this.mCheckBox.setChecked(checked);
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        return this.mCheckBox.isChecked();
    }

    @Override // android.widget.Checkable
    public void toggle() {
        this.mCheckBox.toggle();
    }
}