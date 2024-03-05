package com.android.server.wm;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: StrictModeFlash.class */
public class StrictModeFlash {
    private static final String TAG = "StrictModeFlash";
    private final SurfaceControl mSurfaceControl;
    private int mLastDW;
    private int mLastDH;
    private boolean mDrawNeeded;
    private final Surface mSurface = new Surface();
    private final int mThickness = 20;

    public StrictModeFlash(Display display, SurfaceSession session) {
        SurfaceControl ctrl = null;
        try {
            ctrl = new SurfaceControl(session, TAG, 1, 1, -3, 4);
            ctrl.setLayerStack(display.getLayerStack());
            ctrl.setLayer(1010000);
            ctrl.setPosition(0.0f, 0.0f);
            ctrl.show();
            this.mSurface.copyFrom(ctrl);
        } catch (Surface.OutOfResourcesException e) {
        }
        this.mSurfaceControl = ctrl;
        this.mDrawNeeded = true;
    }

    private void drawIfNeeded() {
        if (!this.mDrawNeeded) {
            return;
        }
        this.mDrawNeeded = false;
        int dw = this.mLastDW;
        int dh = this.mLastDH;
        Rect dirty = new Rect(0, 0, dw, dh);
        Canvas c = null;
        try {
            c = this.mSurface.lockCanvas(dirty);
        } catch (Surface.OutOfResourcesException e) {
        } catch (IllegalArgumentException e2) {
        }
        if (c == null) {
            return;
        }
        c.clipRect(new Rect(0, 0, dw, 20), Region.Op.REPLACE);
        c.drawColor(-65536);
        c.clipRect(new Rect(0, 0, 20, dh), Region.Op.REPLACE);
        c.drawColor(-65536);
        c.clipRect(new Rect(dw - 20, 0, dw, dh), Region.Op.REPLACE);
        c.drawColor(-65536);
        c.clipRect(new Rect(0, dh - 20, dw, dh), Region.Op.REPLACE);
        c.drawColor(-65536);
        this.mSurface.unlockCanvasAndPost(c);
    }

    public void setVisibility(boolean on) {
        if (this.mSurfaceControl == null) {
            return;
        }
        drawIfNeeded();
        if (on) {
            this.mSurfaceControl.show();
        } else {
            this.mSurfaceControl.hide();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void positionSurface(int dw, int dh) {
        if (this.mLastDW == dw && this.mLastDH == dh) {
            return;
        }
        this.mLastDW = dw;
        this.mLastDH = dh;
        this.mSurfaceControl.setSize(dw, dh);
        this.mDrawNeeded = true;
    }
}