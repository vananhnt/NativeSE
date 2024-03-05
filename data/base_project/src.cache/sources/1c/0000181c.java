package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/* loaded from: RadioButton.class */
public class RadioButton extends CompoundButton {
    public RadioButton(Context context) {
        this(context, null);
    }

    public RadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 16842878);
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void toggle() {
        if (!isChecked()) {
            super.toggle();
        }
    }

    @Override // android.widget.CompoundButton, android.widget.Button, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(RadioButton.class.getName());
    }

    @Override // android.widget.CompoundButton, android.widget.Button, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(RadioButton.class.getName());
    }
}