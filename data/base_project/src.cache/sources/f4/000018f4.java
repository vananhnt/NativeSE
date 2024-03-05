package android.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/* loaded from: ZoomButton.class */
public class ZoomButton extends ImageButton implements View.OnLongClickListener {
    private final Handler mHandler;
    private final Runnable mRunnable;
    private long mZoomSpeed;
    private boolean mIsInLongpress;

    public ZoomButton(Context context) {
        this(context, null);
    }

    public ZoomButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRunnable = new Runnable() { // from class: android.widget.ZoomButton.1
            @Override // java.lang.Runnable
            public void run() {
                if (ZoomButton.this.hasOnClickListeners() && ZoomButton.this.mIsInLongpress && ZoomButton.this.isEnabled()) {
                    ZoomButton.this.callOnClick();
                    ZoomButton.this.mHandler.postDelayed(this, ZoomButton.this.mZoomSpeed);
                }
            }
        };
        this.mZoomSpeed = 1000L;
        this.mHandler = new Handler();
        setOnLongClickListener(this);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 3 || event.getAction() == 1) {
            this.mIsInLongpress = false;
        }
        return super.onTouchEvent(event);
    }

    public void setZoomSpeed(long speed) {
        this.mZoomSpeed = speed;
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        this.mIsInLongpress = true;
        this.mHandler.post(this.mRunnable);
        return true;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        this.mIsInLongpress = false;
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            setPressed(false);
        }
        super.setEnabled(enabled);
    }

    @Override // android.view.View
    public boolean dispatchUnhandledMove(View focused, int direction) {
        clearFocus();
        return super.dispatchUnhandledMove(focused, direction);
    }

    @Override // android.widget.ImageButton, android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ZoomButton.class.getName());
    }

    @Override // android.widget.ImageButton, android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ZoomButton.class.getName());
    }
}