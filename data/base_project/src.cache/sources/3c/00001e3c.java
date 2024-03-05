package com.android.server.display;

import android.graphics.Rect;

/* loaded from: DisplayViewport.class */
public final class DisplayViewport {
    public boolean valid;
    public int displayId;
    public int orientation;
    public final Rect logicalFrame = new Rect();
    public final Rect physicalFrame = new Rect();
    public int deviceWidth;
    public int deviceHeight;

    public void copyFrom(DisplayViewport viewport) {
        this.valid = viewport.valid;
        this.displayId = viewport.displayId;
        this.orientation = viewport.orientation;
        this.logicalFrame.set(viewport.logicalFrame);
        this.physicalFrame.set(viewport.physicalFrame);
        this.deviceWidth = viewport.deviceWidth;
        this.deviceHeight = viewport.deviceHeight;
    }

    public String toString() {
        return "DisplayViewport{valid=" + this.valid + ", displayId=" + this.displayId + ", orientation=" + this.orientation + ", logicalFrame=" + this.logicalFrame + ", physicalFrame=" + this.physicalFrame + ", deviceWidth=" + this.deviceWidth + ", deviceHeight=" + this.deviceHeight + "}";
    }
}