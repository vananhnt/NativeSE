package com.android.server.wm;

import android.os.Message;
import android.os.RemoteException;
import android.view.IApplicationToken;
import android.view.View;
import com.android.server.input.InputApplicationHandle;
import gov.nist.core.Separators;
import java.io.PrintWriter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: AppWindowToken.class */
public class AppWindowToken extends WindowToken {
    final IApplicationToken appToken;
    final WindowList allAppWindows;
    final AppWindowAnimator mAppAnimator;
    final WindowAnimator mAnimator;
    int groupId;
    boolean appFullscreen;
    int requestedOrientation;
    boolean showWhenLocked;
    long inputDispatchingTimeoutNanos;
    long lastTransactionSequence;
    int numInterestingWindows;
    int numDrawnWindows;
    boolean inPendingTransaction;
    boolean allDrawn;
    boolean deferClearAllDrawn;
    boolean willBeHidden;
    boolean hiddenRequested;
    boolean clientHidden;
    boolean reportedVisible;
    boolean reportedDrawn;
    boolean removed;
    StartingData startingData;
    WindowState startingWindow;
    View startingView;
    boolean startingDisplayed;
    boolean startingMoved;
    boolean firstWindowDrawn;
    final InputApplicationHandle mInputApplicationHandle;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppWindowToken(WindowManagerService _service, IApplicationToken _token) {
        super(_service, _token.asBinder(), 2, true);
        this.allAppWindows = new WindowList();
        this.groupId = -1;
        this.requestedOrientation = -1;
        this.lastTransactionSequence = Long.MIN_VALUE;
        this.appWindowToken = this;
        this.appToken = _token;
        this.mInputApplicationHandle = new InputApplicationHandle(this);
        this.mAnimator = this.service.mAnimator;
        this.mAppAnimator = new AppWindowAnimator(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendAppVisibilityToClients() {
        int N = this.allAppWindows.size();
        for (int i = 0; i < N; i++) {
            WindowState win = this.allAppWindows.get(i);
            if (win != this.startingWindow || !this.clientHidden) {
                try {
                    win.mClient.dispatchAppVisibility(!this.clientHidden);
                } catch (RemoteException e) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateReportedVisibilityLocked() {
        if (this.appToken == null) {
            return;
        }
        int numInteresting = 0;
        int numVisible = 0;
        int numDrawn = 0;
        boolean nowGone = true;
        int N = this.allAppWindows.size();
        for (int i = 0; i < N; i++) {
            WindowState win = this.allAppWindows.get(i);
            if (win != this.startingWindow && !win.mAppFreezing && win.mViewVisibility == 0 && win.mAttrs.type != 3 && !win.mDestroying) {
                numInteresting++;
                if (win.isDrawnLw()) {
                    numDrawn++;
                    if (!win.mWinAnimator.isAnimating()) {
                        numVisible++;
                    }
                    nowGone = false;
                } else if (win.mWinAnimator.isAnimating()) {
                    nowGone = false;
                }
            }
        }
        boolean nowDrawn = numInteresting > 0 && numDrawn >= numInteresting;
        boolean nowVisible = numInteresting > 0 && numVisible >= numInteresting;
        if (!nowGone) {
            if (!nowDrawn) {
                nowDrawn = this.reportedDrawn;
            }
            if (!nowVisible) {
                nowVisible = this.reportedVisible;
            }
        }
        if (nowDrawn != this.reportedDrawn) {
            if (nowDrawn) {
                Message m = this.service.mH.obtainMessage(9, this);
                this.service.mH.sendMessage(m);
            }
            this.reportedDrawn = nowDrawn;
        }
        if (nowVisible != this.reportedVisible) {
            this.reportedVisible = nowVisible;
            Message m2 = this.service.mH.obtainMessage(8, nowVisible ? 1 : 0, nowGone ? 1 : 0, this);
            this.service.mH.sendMessage(m2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:5:0x000c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.android.server.wm.WindowState findMainWindow() {
        /*
            r3 = this;
            r0 = r3
            com.android.server.wm.WindowList r0 = r0.windows
            int r0 = r0.size()
            r4 = r0
        L8:
            r0 = r4
            if (r0 <= 0) goto L36
            int r4 = r4 + (-1)
            r0 = r3
            com.android.server.wm.WindowList r0 = r0.windows
            r1 = r4
            java.lang.Object r0 = r0.get(r1)
            com.android.server.wm.WindowState r0 = (com.android.server.wm.WindowState) r0
            r5 = r0
            r0 = r5
            android.view.WindowManager$LayoutParams r0 = r0.mAttrs
            int r0 = r0.type
            r1 = 1
            if (r0 == r1) goto L31
            r0 = r5
            android.view.WindowManager$LayoutParams r0 = r0.mAttrs
            int r0 = r0.type
            r1 = 3
            if (r0 != r1) goto L33
        L31:
            r0 = r5
            return r0
        L33:
            goto L8
        L36:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AppWindowToken.findMainWindow():com.android.server.wm.WindowState");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isVisible() {
        int N = this.allAppWindows.size();
        for (int i = 0; i < N; i++) {
            WindowState win = this.allAppWindows.get(i);
            if (!win.mAppFreezing && ((win.mViewVisibility == 0 || (win.mWinAnimator.isAnimating() && !this.service.mAppTransition.isTransitionSet())) && !win.mDestroying && win.isDrawnLw())) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.server.wm.WindowToken
    public void dump(PrintWriter pw, String prefix) {
        super.dump(pw, prefix);
        if (this.appToken != null) {
            pw.print(prefix);
            pw.println("app=true");
        }
        if (this.allAppWindows.size() > 0) {
            pw.print(prefix);
            pw.print("allAppWindows=");
            pw.println(this.allAppWindows);
        }
        pw.print(prefix);
        pw.print("groupId=");
        pw.print(this.groupId);
        pw.print(" appFullscreen=");
        pw.print(this.appFullscreen);
        pw.print(" requestedOrientation=");
        pw.println(this.requestedOrientation);
        pw.print(prefix);
        pw.print("hiddenRequested=");
        pw.print(this.hiddenRequested);
        pw.print(" clientHidden=");
        pw.print(this.clientHidden);
        pw.print(" willBeHidden=");
        pw.print(this.willBeHidden);
        pw.print(" reportedDrawn=");
        pw.print(this.reportedDrawn);
        pw.print(" reportedVisible=");
        pw.println(this.reportedVisible);
        if (this.paused) {
            pw.print(prefix);
            pw.print("paused=");
            pw.println(this.paused);
        }
        if (this.numInterestingWindows != 0 || this.numDrawnWindows != 0 || this.allDrawn || this.mAppAnimator.allDrawn) {
            pw.print(prefix);
            pw.print("numInterestingWindows=");
            pw.print(this.numInterestingWindows);
            pw.print(" numDrawnWindows=");
            pw.print(this.numDrawnWindows);
            pw.print(" inPendingTransaction=");
            pw.print(this.inPendingTransaction);
            pw.print(" allDrawn=");
            pw.print(this.allDrawn);
            pw.print(" (animator=");
            pw.print(this.mAppAnimator.allDrawn);
            pw.println(Separators.RPAREN);
        }
        if (this.inPendingTransaction) {
            pw.print(prefix);
            pw.print("inPendingTransaction=");
            pw.println(this.inPendingTransaction);
        }
        if (this.startingData != null || this.removed || this.firstWindowDrawn) {
            pw.print(prefix);
            pw.print("startingData=");
            pw.print(this.startingData);
            pw.print(" removed=");
            pw.print(this.removed);
            pw.print(" firstWindowDrawn=");
            pw.println(this.firstWindowDrawn);
        }
        if (this.startingWindow != null || this.startingView != null || this.startingDisplayed || this.startingMoved) {
            pw.print(prefix);
            pw.print("startingWindow=");
            pw.print(this.startingWindow);
            pw.print(" startingView=");
            pw.print(this.startingView);
            pw.print(" startingDisplayed=");
            pw.print(this.startingDisplayed);
            pw.print(" startingMoved");
            pw.println(this.startingMoved);
        }
    }

    @Override // com.android.server.wm.WindowToken
    public String toString() {
        if (this.stringName == null) {
            this.stringName = "AppWindowToken{" + Integer.toHexString(System.identityHashCode(this)) + " token=" + this.token + '}';
        }
        return this.stringName;
    }
}