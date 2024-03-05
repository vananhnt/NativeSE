package com.android.server.wm;

import android.graphics.Region;
import android.view.DisplayInfo;
import android.view.MotionEvent;
import android.view.WindowManagerPolicy;

/* loaded from: StackTapPointerEventListener.class */
public class StackTapPointerEventListener implements WindowManagerPolicy.PointerEventListener {
    private static final int TAP_TIMEOUT_MSEC = 300;
    private static final float TAP_MOTION_SLOP_INCHES = 0.125f;
    private final int mMotionSlop;
    private float mDownX;
    private float mDownY;
    private int mPointerId;
    private final Region mTouchExcludeRegion;
    private final WindowManagerService mService;
    private final DisplayContent mDisplayContent;

    public StackTapPointerEventListener(WindowManagerService service, DisplayContent displayContent) {
        this.mService = service;
        this.mDisplayContent = displayContent;
        this.mTouchExcludeRegion = displayContent.mTouchExcludeRegion;
        DisplayInfo info = displayContent.getDisplayInfo();
        this.mMotionSlop = (int) (info.logicalDensityDpi * TAP_MOTION_SLOP_INCHES);
    }

    @Override // android.view.WindowManagerPolicy.PointerEventListener
    public void onPointerEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action & 255) {
            case 0:
                this.mPointerId = motionEvent.getPointerId(0);
                this.mDownX = motionEvent.getX();
                this.mDownY = motionEvent.getY();
                return;
            case 1:
            case 6:
                int index = (action & 65280) >> 8;
                if (this.mPointerId == motionEvent.getPointerId(index)) {
                    int x = (int) motionEvent.getX(index);
                    int y = (int) motionEvent.getY(index);
                    if (motionEvent.getEventTime() - motionEvent.getDownTime() < 300 && x - this.mDownX < this.mMotionSlop && y - this.mDownY < this.mMotionSlop && !this.mTouchExcludeRegion.contains(x, y)) {
                        this.mService.mH.obtainMessage(31, x, y, this.mDisplayContent).sendToTarget();
                    }
                    this.mPointerId = -1;
                    return;
                }
                return;
            case 2:
                if (this.mPointerId >= 0) {
                    int index2 = motionEvent.findPointerIndex(this.mPointerId);
                    if (motionEvent.getEventTime() - motionEvent.getDownTime() > 300 || motionEvent.getX(index2) - this.mDownX > this.mMotionSlop || motionEvent.getY(index2) - this.mDownY > this.mMotionSlop) {
                        this.mPointerId = -1;
                        return;
                    }
                    return;
                }
                return;
            case 3:
            case 4:
            case 5:
            default:
                return;
        }
    }
}