package com.android.server.wm;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.view.WindowManagerPolicy;
import android.view.animation.Animation;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.util.ArrayList;

/* loaded from: WindowAnimator.class */
public class WindowAnimator {
    private static final String TAG = "WindowAnimator";
    final WindowManagerService mService;
    final Context mContext;
    final WindowManagerPolicy mPolicy;
    boolean mAnimating;
    long mCurrentTime;
    private int mAnimTransactionSequence;
    Object mLastWindowFreezeSource;
    static final int KEYGUARD_NOT_SHOWN = 0;
    static final int KEYGUARD_ANIMATING_IN = 1;
    static final int KEYGUARD_SHOWN = 2;
    static final int KEYGUARD_ANIMATING_OUT = 3;
    WindowState mWindowDetachedWallpaper = null;
    WindowStateAnimator mUniverseBackground = null;
    int mAboveUniverseLayer = 0;
    int mBulkUpdateParams = 0;
    SparseArray<DisplayContentsAnimator> mDisplayContentsAnimators = new SparseArray<>(2);
    boolean mInitialized = false;
    int mForceHiding = 0;
    final Runnable mAnimationRunnable = new Runnable() { // from class: com.android.server.wm.WindowAnimator.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (WindowAnimator.this.mService.mWindowMap) {
                WindowAnimator.this.mService.mAnimationScheduled = false;
                WindowAnimator.this.animateLocked();
            }
        }
    };

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowAnimator.animateLocked():void, file: WindowAnimator.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    /* JADX INFO: Access modifiers changed from: private */
    public void animateLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowAnimator.animateLocked():void, file: WindowAnimator.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowAnimator.animateLocked():void");
    }

    private String forceHidingToString() {
        switch (this.mForceHiding) {
            case 0:
                return "KEYGUARD_NOT_SHOWN";
            case 1:
                return "KEYGUARD_ANIMATING_IN";
            case 2:
                return "KEYGUARD_SHOWN";
            case 3:
                return "KEYGUARD_ANIMATING_OUT";
            default:
                return "KEYGUARD STATE UNKNOWN " + this.mForceHiding;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowAnimator(WindowManagerService service) {
        this.mService = service;
        this.mContext = service.mContext;
        this.mPolicy = service.mPolicy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addDisplayLocked(int displayId) {
        getDisplayContentsAnimatorLocked(displayId);
        if (displayId == 0) {
            this.mInitialized = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeDisplayLocked(int displayId) {
        DisplayContentsAnimator displayAnimator = this.mDisplayContentsAnimators.get(displayId);
        if (displayAnimator != null && displayAnimator.mScreenRotationAnimation != null) {
            displayAnimator.mScreenRotationAnimation.kill();
            displayAnimator.mScreenRotationAnimation = null;
        }
        this.mDisplayContentsAnimators.delete(displayId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppWindowAnimator getWallpaperAppAnimator() {
        if (this.mService.mWallpaperTarget == null || this.mService.mWallpaperTarget.mAppToken == null) {
            return null;
        }
        return this.mService.mWallpaperTarget.mAppToken.mAppAnimator;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideWallpapersLocked(WindowState w) {
        WindowState wallpaperTarget = this.mService.mWallpaperTarget;
        WindowState lowerWallpaperTarget = this.mService.mLowerWallpaperTarget;
        ArrayList<WindowToken> wallpaperTokens = this.mService.mWallpaperTokens;
        if ((wallpaperTarget == w && lowerWallpaperTarget == null) || wallpaperTarget == null) {
            int numTokens = wallpaperTokens.size();
            for (int i = numTokens - 1; i >= 0; i--) {
                WindowToken token = wallpaperTokens.get(i);
                int numWindows = token.windows.size();
                for (int j = numWindows - 1; j >= 0; j--) {
                    WindowState wallpaper = token.windows.get(j);
                    WindowStateAnimator winAnimator = wallpaper.mWinAnimator;
                    if (!winAnimator.mLastHidden) {
                        winAnimator.hide();
                        this.mService.dispatchWallpaperVisibility(wallpaper, false);
                        setPendingLayoutChanges(0, 4);
                    }
                }
                token.hidden = true;
            }
        }
    }

    private void updateAppWindowsLocked(int displayId) {
        DisplayContent displayContent = this.mService.getDisplayContentLocked(displayId);
        ArrayList<Task> tasks = displayContent.getTasks();
        int numTasks = tasks.size();
        for (int taskNdx = 0; taskNdx < numTasks; taskNdx++) {
            AppTokenList tokens = tasks.get(taskNdx).mAppTokens;
            int numTokens = tokens.size();
            for (int tokenNdx = 0; tokenNdx < numTokens; tokenNdx++) {
                AppWindowAnimator appAnimator = tokens.get(tokenNdx).mAppAnimator;
                boolean wasAnimating = (appAnimator.animation == null || appAnimator.animation == AppWindowAnimator.sDummyAnimation) ? false : true;
                if (appAnimator.stepAnimationLocked(this.mCurrentTime)) {
                    this.mAnimating = true;
                } else if (wasAnimating) {
                    setAppLayoutChanges(appAnimator, 4, "appToken " + appAnimator.mAppToken + " done");
                }
            }
        }
        AppTokenList exitingAppTokens = displayContent.mExitingAppTokens;
        int NEAT = exitingAppTokens.size();
        for (int i = 0; i < NEAT; i++) {
            AppWindowAnimator appAnimator2 = exitingAppTokens.get(i).mAppAnimator;
            boolean wasAnimating2 = (appAnimator2.animation == null || appAnimator2.animation == AppWindowAnimator.sDummyAnimation) ? false : true;
            if (appAnimator2.stepAnimationLocked(this.mCurrentTime)) {
                this.mAnimating = true;
            } else if (wasAnimating2) {
                setAppLayoutChanges(appAnimator2, 4, "exiting appToken " + appAnimator2.mAppToken + " done");
            }
        }
    }

    private void updateWindowsLocked(int displayId) {
        boolean changed;
        this.mAnimTransactionSequence++;
        WindowList windows = this.mService.getWindowListLocked(displayId);
        ArrayList<WindowStateAnimator> unForceHiding = null;
        boolean wallpaperInUnForceHiding = false;
        this.mForceHiding = 0;
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowState win = windows.get(i);
            WindowStateAnimator winAnimator = win.mWinAnimator;
            int flags = winAnimator.mAttrFlags;
            if (winAnimator.mSurfaceControl != null) {
                boolean wasAnimating = winAnimator.mWasAnimating;
                boolean nowAnimating = winAnimator.stepAnimationLocked(this.mCurrentTime);
                if (wasAnimating && !winAnimator.mAnimating && this.mService.mWallpaperTarget == win) {
                    this.mBulkUpdateParams |= 2;
                    setPendingLayoutChanges(0, 4);
                    this.mService.debugLayoutRepeats("updateWindowsAndWallpaperLocked 2", getPendingLayoutChanges(0));
                }
                if (this.mPolicy.doesForceHide(win, win.mAttrs)) {
                    if (!wasAnimating && nowAnimating) {
                        this.mBulkUpdateParams |= 4;
                        setPendingLayoutChanges(displayId, 4);
                        this.mService.debugLayoutRepeats("updateWindowsAndWallpaperLocked 3", getPendingLayoutChanges(displayId));
                        this.mService.mFocusMayChange = true;
                    }
                    if (win.isReadyForDisplay()) {
                        if (nowAnimating) {
                            if (winAnimator.mAnimationIsEntrance) {
                                this.mForceHiding = 1;
                            } else {
                                this.mForceHiding = 3;
                            }
                        } else {
                            this.mForceHiding = 2;
                        }
                    }
                } else if (this.mPolicy.canBeForceHidden(win, win.mAttrs)) {
                    boolean hideWhenLocked = (winAnimator.mAttrFlags & 524288) == 0;
                    if ((this.mForceHiding == 1 && (!winAnimator.isAnimating() || hideWhenLocked)) || (this.mForceHiding == 2 && hideWhenLocked)) {
                        changed = win.hideLw(false, false);
                    } else {
                        changed = win.showLw(false, false);
                        if (changed) {
                            if ((this.mBulkUpdateParams & 4) != 0 && win.isVisibleNow()) {
                                if (unForceHiding == null) {
                                    unForceHiding = new ArrayList<>();
                                }
                                unForceHiding.add(winAnimator);
                                if ((flags & 1048576) != 0) {
                                    wallpaperInUnForceHiding = true;
                                }
                            }
                            WindowState currentFocus = this.mService.mCurrentFocus;
                            if (currentFocus == null || currentFocus.mLayer < win.mLayer) {
                                this.mService.mFocusMayChange = true;
                            }
                        }
                    }
                    if (changed && (flags & 1048576) != 0) {
                        this.mBulkUpdateParams |= 2;
                        setPendingLayoutChanges(0, 4);
                        this.mService.debugLayoutRepeats("updateWindowsAndWallpaperLocked 4", getPendingLayoutChanges(0));
                    }
                }
            }
            AppWindowToken atoken = win.mAppToken;
            if (winAnimator.mDrawState == 3 && ((atoken == null || atoken.allDrawn) && winAnimator.performShowLocked())) {
                setPendingLayoutChanges(displayId, 8);
                this.mService.debugLayoutRepeats("updateWindowsAndWallpaperLocked 5", getPendingLayoutChanges(displayId));
            }
            AppWindowAnimator appAnimator = winAnimator.mAppAnimator;
            if (appAnimator != null && appAnimator.thumbnail != null) {
                if (appAnimator.thumbnailTransactionSeq != this.mAnimTransactionSequence) {
                    appAnimator.thumbnailTransactionSeq = this.mAnimTransactionSequence;
                    appAnimator.thumbnailLayer = 0;
                }
                if (appAnimator.thumbnailLayer < winAnimator.mAnimLayer) {
                    appAnimator.thumbnailLayer = winAnimator.mAnimLayer;
                }
            }
        }
        if (unForceHiding != null) {
            for (int i2 = unForceHiding.size() - 1; i2 >= 0; i2--) {
                Animation a = this.mPolicy.createForceHideEnterAnimation(wallpaperInUnForceHiding);
                if (a != null) {
                    WindowStateAnimator winAnimator2 = unForceHiding.get(i2);
                    winAnimator2.setAnimation(a);
                    winAnimator2.mAnimationIsEntrance = true;
                }
            }
        }
    }

    private void updateWallpaperLocked(int displayId) {
        this.mService.getDisplayContentLocked(displayId).resetAnimationBackgroundAnimator();
        WindowList windows = this.mService.getWindowListLocked(displayId);
        WindowState detachedWallpaper = null;
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowState win = windows.get(i);
            WindowStateAnimator winAnimator = win.mWinAnimator;
            if (winAnimator.mSurfaceControl != null) {
                int flags = winAnimator.mAttrFlags;
                if (winAnimator.mAnimating) {
                    if (winAnimator.mAnimation != null) {
                        if ((flags & 1048576) != 0 && winAnimator.mAnimation.getDetachWallpaper()) {
                            detachedWallpaper = win;
                        }
                        int color = winAnimator.mAnimation.getBackgroundColor();
                        if (color != 0) {
                            win.getStack().setAnimationBackground(winAnimator, color);
                        }
                    }
                    this.mAnimating = true;
                }
                AppWindowAnimator appAnimator = winAnimator.mAppAnimator;
                if (appAnimator != null && appAnimator.animation != null && appAnimator.animating) {
                    if ((flags & 1048576) != 0 && appAnimator.animation.getDetachWallpaper()) {
                        detachedWallpaper = win;
                    }
                    int color2 = appAnimator.animation.getBackgroundColor();
                    if (color2 != 0) {
                        win.getStack().setAnimationBackground(winAnimator, color2);
                    }
                }
            }
        }
        if (this.mWindowDetachedWallpaper != detachedWallpaper) {
            this.mWindowDetachedWallpaper = detachedWallpaper;
            this.mBulkUpdateParams |= 2;
        }
    }

    private void testTokenMayBeDrawnLocked(int displayId) {
        ArrayList<Task> tasks = this.mService.getDisplayContentLocked(displayId).getTasks();
        int numTasks = tasks.size();
        for (int taskNdx = 0; taskNdx < numTasks; taskNdx++) {
            AppTokenList tokens = tasks.get(taskNdx).mAppTokens;
            int numTokens = tokens.size();
            for (int tokenNdx = 0; tokenNdx < numTokens; tokenNdx++) {
                AppWindowToken wtoken = tokens.get(tokenNdx);
                AppWindowAnimator appAnimator = wtoken.mAppAnimator;
                boolean allDrawn = wtoken.allDrawn;
                if (allDrawn != appAnimator.allDrawn) {
                    appAnimator.allDrawn = allDrawn;
                    if (allDrawn) {
                        if (appAnimator.freezingScreen) {
                            appAnimator.showAllWindowsLocked();
                            this.mService.unsetAppFreezingScreenLocked(wtoken, false, true);
                            setAppLayoutChanges(appAnimator, 4, "testTokenMayBeDrawnLocked: freezingScreen");
                        } else {
                            setAppLayoutChanges(appAnimator, 8, "testTokenMayBeDrawnLocked");
                            if (!this.mService.mOpeningApps.contains(wtoken)) {
                                this.mAnimating |= appAnimator.showAllWindowsLocked();
                            }
                        }
                    }
                }
            }
        }
    }

    private void performAnimationsLocked(int displayId) {
        updateWindowsLocked(displayId);
        updateWallpaperLocked(displayId);
    }

    static String bulkUpdateParamsToString(int bulkUpdateParams) {
        StringBuilder builder = new StringBuilder(128);
        if ((bulkUpdateParams & 1) != 0) {
            builder.append(" UPDATE_ROTATION");
        }
        if ((bulkUpdateParams & 2) != 0) {
            builder.append(" WALLPAPER_MAY_CHANGE");
        }
        if ((bulkUpdateParams & 4) != 0) {
            builder.append(" FORCE_HIDING_CHANGED");
        }
        if ((bulkUpdateParams & 8) != 0) {
            builder.append(" ORIENTATION_CHANGE_COMPLETE");
        }
        if ((bulkUpdateParams & 16) != 0) {
            builder.append(" TURN_ON_SCREEN");
        }
        return builder.toString();
    }

    public void dumpLocked(PrintWriter pw, String prefix, boolean dumpAll) {
        String subPrefix = "  " + prefix;
        String subSubPrefix = "  " + subPrefix;
        for (int i = 0; i < this.mDisplayContentsAnimators.size(); i++) {
            pw.print(prefix);
            pw.print("DisplayContentsAnimator #");
            pw.print(this.mDisplayContentsAnimators.keyAt(i));
            pw.println(Separators.COLON);
            DisplayContentsAnimator displayAnimator = this.mDisplayContentsAnimators.valueAt(i);
            WindowList windows = this.mService.getWindowListLocked(this.mDisplayContentsAnimators.keyAt(i));
            int N = windows.size();
            for (int j = 0; j < N; j++) {
                WindowStateAnimator wanim = windows.get(j).mWinAnimator;
                pw.print(subPrefix);
                pw.print("Window #");
                pw.print(j);
                pw.print(": ");
                pw.println(wanim);
            }
            if (displayAnimator.mScreenRotationAnimation != null) {
                pw.print(subPrefix);
                pw.println("mScreenRotationAnimation:");
                displayAnimator.mScreenRotationAnimation.printTo(subSubPrefix, pw);
            } else if (dumpAll) {
                pw.print(subPrefix);
                pw.println("no ScreenRotationAnimation ");
            }
        }
        pw.println();
        if (dumpAll) {
            pw.print(prefix);
            pw.print("mAnimTransactionSequence=");
            pw.print(this.mAnimTransactionSequence);
            pw.print(" mForceHiding=");
            pw.println(forceHidingToString());
            pw.print(prefix);
            pw.print("mCurrentTime=");
            pw.println(TimeUtils.formatUptime(this.mCurrentTime));
        }
        if (this.mBulkUpdateParams != 0) {
            pw.print(prefix);
            pw.print("mBulkUpdateParams=0x");
            pw.print(Integer.toHexString(this.mBulkUpdateParams));
            pw.println(bulkUpdateParamsToString(this.mBulkUpdateParams));
        }
        if (this.mWindowDetachedWallpaper != null) {
            pw.print(prefix);
            pw.print("mWindowDetachedWallpaper=");
            pw.println(this.mWindowDetachedWallpaper);
        }
        if (this.mUniverseBackground != null) {
            pw.print(prefix);
            pw.print("mUniverseBackground=");
            pw.print(this.mUniverseBackground);
            pw.print(" mAboveUniverseLayer=");
            pw.println(this.mAboveUniverseLayer);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPendingLayoutChanges(int displayId) {
        return this.mService.getDisplayContentLocked(displayId).pendingLayoutChanges;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPendingLayoutChanges(int displayId, int changes) {
        this.mService.getDisplayContentLocked(displayId).pendingLayoutChanges |= changes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAppLayoutChanges(AppWindowAnimator appAnimator, int changes, String s) {
        SparseIntArray displays = new SparseIntArray(2);
        WindowList windows = appAnimator.mAppToken.allAppWindows;
        for (int i = windows.size() - 1; i >= 0; i--) {
            int displayId = windows.get(i).getDisplayId();
            if (displays.indexOfKey(displayId) < 0) {
                setPendingLayoutChanges(displayId, changes);
                this.mService.debugLayoutRepeats(s, getPendingLayoutChanges(displayId));
                displays.put(displayId, changes);
            }
        }
    }

    private DisplayContentsAnimator getDisplayContentsAnimatorLocked(int displayId) {
        DisplayContentsAnimator displayAnimator = this.mDisplayContentsAnimators.get(displayId);
        if (displayAnimator == null) {
            displayAnimator = new DisplayContentsAnimator();
            this.mDisplayContentsAnimators.put(displayId, displayAnimator);
        }
        return displayAnimator;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setScreenRotationAnimationLocked(int displayId, ScreenRotationAnimation animation) {
        getDisplayContentsAnimatorLocked(displayId).mScreenRotationAnimation = animation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScreenRotationAnimation getScreenRotationAnimationLocked(int displayId) {
        return getDisplayContentsAnimatorLocked(displayId).mScreenRotationAnimation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WindowAnimator$DisplayContentsAnimator.class */
    public class DisplayContentsAnimator {
        ScreenRotationAnimation mScreenRotationAnimation;

        private DisplayContentsAnimator() {
            this.mScreenRotationAnimation = null;
        }
    }
}