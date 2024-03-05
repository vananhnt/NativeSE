package android.inputmethodservice;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

/* loaded from: SoftInputWindow.class */
class SoftInputWindow extends Dialog {
    final KeyEvent.DispatcherState mDispatcherState;
    private final Rect mBounds;

    public void setToken(IBinder token) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.token = token;
        getWindow().setAttributes(lp);
    }

    public SoftInputWindow(Context context, int theme, KeyEvent.DispatcherState dispatcherState) {
        super(context, theme);
        this.mBounds = new Rect();
        this.mDispatcherState = dispatcherState;
        initDockWindow();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        this.mDispatcherState.reset();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getWindow().getDecorView().getHitRect(this.mBounds);
        if (ev.isWithinBoundsNoHistory(this.mBounds.left, this.mBounds.top, this.mBounds.right - 1, this.mBounds.bottom - 1)) {
            return super.dispatchTouchEvent(ev);
        }
        MotionEvent temp = ev.clampNoHistory(this.mBounds.left, this.mBounds.top, this.mBounds.right - 1, this.mBounds.bottom - 1);
        boolean handled = super.dispatchTouchEvent(temp);
        temp.recycle();
        return handled;
    }

    public int getSize() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (lp.gravity == 48 || lp.gravity == 80) {
            return lp.height;
        }
        return lp.width;
    }

    public void setSize(int size) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (lp.gravity == 48 || lp.gravity == 80) {
            lp.width = -1;
            lp.height = size;
        } else {
            lp.width = size;
            lp.height = -1;
        }
        getWindow().setAttributes(lp);
    }

    public void setGravity(int gravity) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        boolean oldIsVertical = lp.gravity == 48 || lp.gravity == 80;
        lp.gravity = gravity;
        boolean newIsVertical = lp.gravity == 48 || lp.gravity == 80;
        if (oldIsVertical != newIsVertical) {
            int tmp = lp.width;
            lp.width = lp.height;
            lp.height = tmp;
            getWindow().setAttributes(lp);
        }
    }

    private void initDockWindow() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.type = 2011;
        lp.setTitle("InputMethod");
        lp.gravity = 80;
        lp.width = -1;
        getWindow().setAttributes(lp);
        getWindow().setFlags(264, 266);
    }
}