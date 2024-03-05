package android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/* loaded from: ImageSwitcher.class */
public class ImageSwitcher extends ViewSwitcher {
    public ImageSwitcher(Context context) {
        super(context);
    }

    public ImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageResource(int resid) {
        ImageView image = (ImageView) getNextView();
        image.setImageResource(resid);
        showNext();
    }

    public void setImageURI(Uri uri) {
        ImageView image = (ImageView) getNextView();
        image.setImageURI(uri);
        showNext();
    }

    public void setImageDrawable(Drawable drawable) {
        ImageView image = (ImageView) getNextView();
        image.setImageDrawable(drawable);
        showNext();
    }

    @Override // android.widget.ViewSwitcher, android.widget.ViewAnimator, android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ImageSwitcher.class.getName());
    }

    @Override // android.widget.ViewSwitcher, android.widget.ViewAnimator, android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ImageSwitcher.class.getName());
    }
}