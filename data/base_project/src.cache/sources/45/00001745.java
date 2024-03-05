package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/* loaded from: CheckBox.class */
public class CheckBox extends CompoundButton {
    public CheckBox(Context context) {
        this(context, null);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 16842860);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.CompoundButton, android.widget.Button, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CheckBox.class.getName());
    }

    @Override // android.widget.CompoundButton, android.widget.Button, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CheckBox.class.getName());
    }
}