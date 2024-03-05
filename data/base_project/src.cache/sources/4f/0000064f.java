package android.hardware.display;

import android.os.IBinder;
import android.view.Display;

/* loaded from: VirtualDisplay.class */
public final class VirtualDisplay {
    private final DisplayManagerGlobal mGlobal;
    private final Display mDisplay;
    private IBinder mToken;

    /* JADX INFO: Access modifiers changed from: package-private */
    public VirtualDisplay(DisplayManagerGlobal global, Display display, IBinder token) {
        this.mGlobal = global;
        this.mDisplay = display;
        this.mToken = token;
    }

    public Display getDisplay() {
        return this.mDisplay;
    }

    public void release() {
        if (this.mToken != null) {
            this.mGlobal.releaseVirtualDisplay(this.mToken);
            this.mToken = null;
        }
    }

    public String toString() {
        return "VirtualDisplay{display=" + this.mDisplay + ", token=" + this.mToken + "}";
    }
}