package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

@RemoteViews.RemoteView
/* loaded from: Button.class */
public class Button extends TextView {
    public Button(Context context) {
        this(context, null);
    }

    public Button(Context context, AttributeSet attrs) {
        this(context, attrs, 16842824);
    }

    public Button(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(Button.class.getName());
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(Button.class.getName());
    }
}