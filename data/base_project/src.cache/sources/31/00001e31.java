package com.android.server.display;

import android.graphics.Rect;
import android.os.IBinder;
import android.view.Surface;
import android.view.SurfaceControl;
import java.io.PrintWriter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: DisplayDevice.class */
public abstract class DisplayDevice {
    private final DisplayAdapter mDisplayAdapter;
    private final IBinder mDisplayToken;
    private int mCurrentLayerStack = -1;
    private int mCurrentOrientation = -1;
    private Rect mCurrentLayerStackRect;
    private Rect mCurrentDisplayRect;
    private Surface mCurrentSurface;

    public abstract DisplayDeviceInfo getDisplayDeviceInfoLocked();

    public DisplayDevice(DisplayAdapter displayAdapter, IBinder displayToken) {
        this.mDisplayAdapter = displayAdapter;
        this.mDisplayToken = displayToken;
    }

    public final DisplayAdapter getAdapterLocked() {
        return this.mDisplayAdapter;
    }

    public final IBinder getDisplayTokenLocked() {
        return this.mDisplayToken;
    }

    public final String getNameLocked() {
        return getDisplayDeviceInfoLocked().name;
    }

    public void applyPendingDisplayDeviceInfoChangesLocked() {
    }

    public void performTraversalInTransactionLocked() {
    }

    public void blankLocked() {
    }

    public void unblankLocked() {
    }

    public final void setLayerStackInTransactionLocked(int layerStack) {
        if (this.mCurrentLayerStack != layerStack) {
            this.mCurrentLayerStack = layerStack;
            SurfaceControl.setDisplayLayerStack(this.mDisplayToken, layerStack);
        }
    }

    public final void setProjectionInTransactionLocked(int orientation, Rect layerStackRect, Rect displayRect) {
        if (this.mCurrentOrientation != orientation || this.mCurrentLayerStackRect == null || !this.mCurrentLayerStackRect.equals(layerStackRect) || this.mCurrentDisplayRect == null || !this.mCurrentDisplayRect.equals(displayRect)) {
            this.mCurrentOrientation = orientation;
            if (this.mCurrentLayerStackRect == null) {
                this.mCurrentLayerStackRect = new Rect();
            }
            this.mCurrentLayerStackRect.set(layerStackRect);
            if (this.mCurrentDisplayRect == null) {
                this.mCurrentDisplayRect = new Rect();
            }
            this.mCurrentDisplayRect.set(displayRect);
            SurfaceControl.setDisplayProjection(this.mDisplayToken, orientation, layerStackRect, displayRect);
        }
    }

    public final void setSurfaceInTransactionLocked(Surface surface) {
        if (this.mCurrentSurface != surface) {
            this.mCurrentSurface = surface;
            SurfaceControl.setDisplaySurface(this.mDisplayToken, surface);
        }
    }

    public final void populateViewportLocked(DisplayViewport viewport) {
        viewport.orientation = this.mCurrentOrientation;
        if (this.mCurrentLayerStackRect != null) {
            viewport.logicalFrame.set(this.mCurrentLayerStackRect);
        } else {
            viewport.logicalFrame.setEmpty();
        }
        if (this.mCurrentDisplayRect != null) {
            viewport.physicalFrame.set(this.mCurrentDisplayRect);
        } else {
            viewport.physicalFrame.setEmpty();
        }
        boolean isRotated = this.mCurrentOrientation == 1 || this.mCurrentOrientation == 3;
        DisplayDeviceInfo info = getDisplayDeviceInfoLocked();
        viewport.deviceWidth = isRotated ? info.height : info.width;
        viewport.deviceHeight = isRotated ? info.width : info.height;
    }

    public void dumpLocked(PrintWriter pw) {
        pw.println("mAdapter=" + this.mDisplayAdapter.getName());
        pw.println("mDisplayToken=" + this.mDisplayToken);
        pw.println("mCurrentLayerStack=" + this.mCurrentLayerStack);
        pw.println("mCurrentOrientation=" + this.mCurrentOrientation);
        pw.println("mCurrentLayerStackRect=" + this.mCurrentLayerStackRect);
        pw.println("mCurrentDisplayRect=" + this.mCurrentDisplayRect);
        pw.println("mCurrentSurface=" + this.mCurrentSurface);
    }
}