package com.android.server.display;

import android.graphics.Rect;
import android.view.DisplayInfo;
import java.io.PrintWriter;
import java.util.List;
import libcore.util.Objects;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: LogicalDisplay.class */
public final class LogicalDisplay {
    private static final int BLANK_LAYER_STACK = -1;
    private final int mDisplayId;
    private final int mLayerStack;
    private DisplayInfo mOverrideDisplayInfo;
    private DisplayInfo mInfo;
    private DisplayDevice mPrimaryDisplayDevice;
    private DisplayDeviceInfo mPrimaryDisplayDeviceInfo;
    private boolean mHasContent;
    private final DisplayInfo mBaseDisplayInfo = new DisplayInfo();
    private final Rect mTempLayerStackRect = new Rect();
    private final Rect mTempDisplayRect = new Rect();

    public LogicalDisplay(int displayId, int layerStack, DisplayDevice primaryDisplayDevice) {
        this.mDisplayId = displayId;
        this.mLayerStack = layerStack;
        this.mPrimaryDisplayDevice = primaryDisplayDevice;
    }

    public int getDisplayIdLocked() {
        return this.mDisplayId;
    }

    public DisplayDevice getPrimaryDisplayDeviceLocked() {
        return this.mPrimaryDisplayDevice;
    }

    public DisplayInfo getDisplayInfoLocked() {
        if (this.mInfo == null) {
            this.mInfo = new DisplayInfo();
            if (this.mOverrideDisplayInfo != null) {
                this.mInfo.copyFrom(this.mOverrideDisplayInfo);
                this.mInfo.layerStack = this.mBaseDisplayInfo.layerStack;
                this.mInfo.name = this.mBaseDisplayInfo.name;
            } else {
                this.mInfo.copyFrom(this.mBaseDisplayInfo);
            }
        }
        return this.mInfo;
    }

    public boolean setDisplayInfoOverrideFromWindowManagerLocked(DisplayInfo info) {
        if (info != null) {
            if (this.mOverrideDisplayInfo == null) {
                this.mOverrideDisplayInfo = new DisplayInfo(info);
                this.mInfo = null;
                return true;
            } else if (!this.mOverrideDisplayInfo.equals(info)) {
                this.mOverrideDisplayInfo.copyFrom(info);
                this.mInfo = null;
                return true;
            } else {
                return false;
            }
        } else if (this.mOverrideDisplayInfo != null) {
            this.mOverrideDisplayInfo = null;
            this.mInfo = null;
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidLocked() {
        return this.mPrimaryDisplayDevice != null;
    }

    public void updateLocked(List<DisplayDevice> devices) {
        if (this.mPrimaryDisplayDevice == null) {
            return;
        }
        if (!devices.contains(this.mPrimaryDisplayDevice)) {
            this.mPrimaryDisplayDevice = null;
            return;
        }
        DisplayDeviceInfo deviceInfo = this.mPrimaryDisplayDevice.getDisplayDeviceInfoLocked();
        if (!Objects.equal(this.mPrimaryDisplayDeviceInfo, deviceInfo)) {
            this.mBaseDisplayInfo.layerStack = this.mLayerStack;
            this.mBaseDisplayInfo.flags = 0;
            if ((deviceInfo.flags & 8) != 0) {
                this.mBaseDisplayInfo.flags |= 1;
            }
            if ((deviceInfo.flags & 4) != 0) {
                this.mBaseDisplayInfo.flags |= 2;
            }
            if ((deviceInfo.flags & 16) != 0) {
                this.mBaseDisplayInfo.flags |= 4;
            }
            if ((deviceInfo.flags & 64) != 0) {
                this.mBaseDisplayInfo.flags |= 8;
            }
            this.mBaseDisplayInfo.type = deviceInfo.type;
            this.mBaseDisplayInfo.address = deviceInfo.address;
            this.mBaseDisplayInfo.name = deviceInfo.name;
            this.mBaseDisplayInfo.appWidth = deviceInfo.width;
            this.mBaseDisplayInfo.appHeight = deviceInfo.height;
            this.mBaseDisplayInfo.logicalWidth = deviceInfo.width;
            this.mBaseDisplayInfo.logicalHeight = deviceInfo.height;
            this.mBaseDisplayInfo.rotation = 0;
            this.mBaseDisplayInfo.refreshRate = deviceInfo.refreshRate;
            this.mBaseDisplayInfo.logicalDensityDpi = deviceInfo.densityDpi;
            this.mBaseDisplayInfo.physicalXDpi = deviceInfo.xDpi;
            this.mBaseDisplayInfo.physicalYDpi = deviceInfo.yDpi;
            this.mBaseDisplayInfo.smallestNominalAppWidth = deviceInfo.width;
            this.mBaseDisplayInfo.smallestNominalAppHeight = deviceInfo.height;
            this.mBaseDisplayInfo.largestNominalAppWidth = deviceInfo.width;
            this.mBaseDisplayInfo.largestNominalAppHeight = deviceInfo.height;
            this.mBaseDisplayInfo.ownerUid = deviceInfo.ownerUid;
            this.mBaseDisplayInfo.ownerPackageName = deviceInfo.ownerPackageName;
            this.mPrimaryDisplayDeviceInfo = deviceInfo;
            this.mInfo = null;
        }
    }

    public void configureDisplayInTransactionLocked(DisplayDevice device, boolean isBlanked) {
        int displayRectWidth;
        int displayRectHeight;
        DisplayInfo displayInfo = getDisplayInfoLocked();
        DisplayDeviceInfo displayDeviceInfo = device.getDisplayDeviceInfoLocked();
        device.setLayerStackInTransactionLocked(isBlanked ? -1 : this.mLayerStack);
        this.mTempLayerStackRect.set(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
        int orientation = 0;
        if (device == this.mPrimaryDisplayDevice && (displayDeviceInfo.flags & 2) != 0) {
            orientation = displayInfo.rotation;
        }
        int orientation2 = (orientation + displayDeviceInfo.rotation) % 4;
        boolean rotated = orientation2 == 1 || orientation2 == 3;
        int physWidth = rotated ? displayDeviceInfo.height : displayDeviceInfo.width;
        int physHeight = rotated ? displayDeviceInfo.width : displayDeviceInfo.height;
        if (physWidth * displayInfo.logicalHeight < physHeight * displayInfo.logicalWidth) {
            displayRectWidth = physWidth;
            displayRectHeight = (displayInfo.logicalHeight * physWidth) / displayInfo.logicalWidth;
        } else {
            displayRectWidth = (displayInfo.logicalWidth * physHeight) / displayInfo.logicalHeight;
            displayRectHeight = physHeight;
        }
        int displayRectTop = (physHeight - displayRectHeight) / 2;
        int displayRectLeft = (physWidth - displayRectWidth) / 2;
        this.mTempDisplayRect.set(displayRectLeft, displayRectTop, displayRectLeft + displayRectWidth, displayRectTop + displayRectHeight);
        device.setProjectionInTransactionLocked(orientation2, this.mTempLayerStackRect, this.mTempDisplayRect);
    }

    public boolean hasContentLocked() {
        return this.mHasContent;
    }

    public void setHasContentLocked(boolean hasContent) {
        this.mHasContent = hasContent;
    }

    public void dumpLocked(PrintWriter pw) {
        pw.println("mDisplayId=" + this.mDisplayId);
        pw.println("mLayerStack=" + this.mLayerStack);
        pw.println("mHasContent=" + this.mHasContent);
        pw.println("mPrimaryDisplayDevice=" + (this.mPrimaryDisplayDevice != null ? this.mPrimaryDisplayDevice.getNameLocked() : "null"));
        pw.println("mBaseDisplayInfo=" + this.mBaseDisplayInfo);
        pw.println("mOverrideDisplayInfo=" + this.mOverrideDisplayInfo);
    }
}