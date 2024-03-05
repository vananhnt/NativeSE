package com.android.server.wm;

import android.os.IBinder;
import gov.nist.core.Separators;
import java.io.PrintWriter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WindowToken.class */
public class WindowToken {
    final WindowManagerService service;
    final IBinder token;
    final int windowType;
    final boolean explicit;
    String stringName;
    AppWindowToken appWindowToken;
    final WindowList windows = new WindowList();
    boolean paused = false;
    boolean hidden;
    boolean hasVisible;
    boolean waitingToShow;
    boolean waitingToHide;
    boolean sendingToBottom;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowToken(WindowManagerService _service, IBinder _token, int type, boolean _explicit) {
        this.service = _service;
        this.token = _token;
        this.windowType = type;
        this.explicit = _explicit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("windows=");
        pw.println(this.windows);
        pw.print(prefix);
        pw.print("windowType=");
        pw.print(this.windowType);
        pw.print(" hidden=");
        pw.print(this.hidden);
        pw.print(" hasVisible=");
        pw.println(this.hasVisible);
        if (this.waitingToShow || this.waitingToHide || this.sendingToBottom) {
            pw.print(prefix);
            pw.print("waitingToShow=");
            pw.print(this.waitingToShow);
            pw.print(" waitingToHide=");
            pw.print(this.waitingToHide);
            pw.print(" sendingToBottom=");
            pw.print(this.sendingToBottom);
        }
    }

    public String toString() {
        if (this.stringName == null) {
            this.stringName = "WindowToken{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.token + '}';
        }
        return this.stringName;
    }
}