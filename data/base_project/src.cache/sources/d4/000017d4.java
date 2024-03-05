package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

@RemoteViews.RemoteView
/* loaded from: ImageButton.class */
public class ImageButton extends ImageView {
    public ImageButton(Context context) {
        this(context, null);
    }

    public ImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 16842866);
    }

    public ImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
    }

    @Override // android.view.View
    protected boolean onSetAlpha(int alpha) {
        return false;
    }

    @Override // android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ImageButton.class.getName());
    }

    @Override // android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ImageButton.class.getName());
    }
}