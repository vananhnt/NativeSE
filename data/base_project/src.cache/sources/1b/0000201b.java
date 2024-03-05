package com.android.server.wm;

import android.app.ActivityManagerNative;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Slog;
import android.view.InputChannel;
import android.view.KeyEvent;
import android.view.WindowManager;
import com.android.server.input.InputApplicationHandle;
import com.android.server.input.InputManagerService;
import com.android.server.input.InputWindowHandle;
import java.util.Arrays;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: InputMonitor.class */
public final class InputMonitor implements InputManagerService.WindowManagerCallbacks {
    private final WindowManagerService mService;
    private WindowState mInputFocus;
    private boolean mInputDispatchFrozen;
    private boolean mInputDispatchEnabled;
    private InputWindowHandle[] mInputWindowHandles;
    private int mInputWindowHandleCount;
    private boolean mInputDevicesReady;
    private boolean mUpdateInputWindowsNeeded = true;
    private final Object mInputDevicesReadyMonitor = new Object();

    public InputMonitor(WindowManagerService service) {
        this.mService = service;
    }

    @Override // com.android.server.input.InputManagerService.WindowManagerCallbacks
    public void notifyInputChannelBroken(InputWindowHandle inputWindowHandle) {
        if (inputWindowHandle == null) {
            return;
        }
        synchronized (this.mService.mWindowMap) {
            WindowState windowState = (WindowState) inputWindowHandle.windowState;
            if (windowState != null) {
                Slog.i("WindowManager", "WINDOW DIED " + windowState);
                this.mService.removeWindowLocked(windowState.mSession, windowState);
            }
        }
    }

    @Override // com.android.server.input.InputManagerService.WindowManagerCallbacks
    public long notifyANR(InputApplicationHandle inputApplicationHandle, InputWindowHandle inputWindowHandle, String reason) {
        AppWindowToken appWindowToken = null;
        WindowState windowState = null;
        boolean aboveSystem = false;
        synchronized (this.mService.mWindowMap) {
            if (inputWindowHandle != null) {
                windowState = (WindowState) inputWindowHandle.windowState;
                if (windowState != null) {
                    appWindowToken = windowState.mAppToken;
                }
            }
            if (appWindowToken == null && inputApplicationHandle != null) {
                appWindowToken = (AppWindowToken) inputApplicationHandle.appWindowToken;
            }
            if (windowState != null) {
                Slog.i("WindowManager", "Input event dispatching timed out sending to " + ((Object) windowState.mAttrs.getTitle()) + ".  Reason: " + reason);
                int systemAlertLayer = this.mService.mPolicy.windowTypeToLayerLw(2003);
                aboveSystem = windowState.mBaseLayer > systemAlertLayer;
            } else if (appWindowToken != null) {
                Slog.i("WindowManager", "Input event dispatching timed out sending to application " + appWindowToken.stringName + ".  Reason: " + reason);
            } else {
                Slog.i("WindowManager", "Input event dispatching timed out .  Reason: " + reason);
            }
            this.mService.saveANRStateLocked(appWindowToken, windowState, reason);
        }
        if (appWindowToken != null && appWindowToken.appToken != null) {
            try {
                boolean abort = appWindowToken.appToken.keyDispatchingTimedOut(reason);
                if (!abort) {
                    return appWindowToken.inputDispatchingTimeoutNanos;
                }
                return 0L;
            } catch (RemoteException e) {
                return 0L;
            }
        } else if (windowState != null) {
            try {
                long timeout = ActivityManagerNative.getDefault().inputDispatchingTimedOut(windowState.mSession.mPid, aboveSystem, reason);
                if (timeout >= 0) {
                    return timeout;
                }
                return 0L;
            } catch (RemoteException e2) {
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    private void addInputWindowHandleLw(InputWindowHandle windowHandle) {
        if (this.mInputWindowHandles == null) {
            this.mInputWindowHandles = new InputWindowHandle[16];
        }
        if (this.mInputWindowHandleCount >= this.mInputWindowHandles.length) {
            this.mInputWindowHandles = (InputWindowHandle[]) Arrays.copyOf(this.mInputWindowHandles, this.mInputWindowHandleCount * 2);
        }
        InputWindowHandle[] inputWindowHandleArr = this.mInputWindowHandles;
        int i = this.mInputWindowHandleCount;
        this.mInputWindowHandleCount = i + 1;
        inputWindowHandleArr[i] = windowHandle;
    }

    private void addInputWindowHandleLw(InputWindowHandle inputWindowHandle, WindowState child, int flags, int privateFlags, int type, boolean isVisible, boolean hasFocus, boolean hasWallpaper) {
        inputWindowHandle.name = child.toString();
        boolean modal = (flags & 40) == 0;
        if (modal && child.mAppToken != null) {
            flags |= 32;
            inputWindowHandle.touchableRegion.set(child.getStackBounds());
        } else {
            child.getTouchableRegion(inputWindowHandle.touchableRegion);
        }
        inputWindowHandle.layoutParamsFlags = flags;
        inputWindowHandle.layoutParamsPrivateFlags = privateFlags;
        inputWindowHandle.layoutParamsType = type;
        inputWindowHandle.dispatchingTimeoutNanos = child.getInputDispatchingTimeoutNanos();
        inputWindowHandle.visible = isVisible;
        inputWindowHandle.canReceiveKeys = child.canReceiveKeys();
        inputWindowHandle.hasFocus = hasFocus;
        inputWindowHandle.hasWallpaper = hasWallpaper;
        inputWindowHandle.paused = child.mAppToken != null ? child.mAppToken.paused : false;
        inputWindowHandle.layer = child.mLayer;
        inputWindowHandle.ownerPid = child.mSession.mPid;
        inputWindowHandle.ownerUid = child.mSession.mUid;
        inputWindowHandle.inputFeatures = child.mAttrs.inputFeatures;
        Rect frame = child.mFrame;
        inputWindowHandle.frameLeft = frame.left;
        inputWindowHandle.frameTop = frame.top;
        inputWindowHandle.frameRight = frame.right;
        inputWindowHandle.frameBottom = frame.bottom;
        if (child.mGlobalScale != 1.0f) {
            inputWindowHandle.scaleFactor = 1.0f / child.mGlobalScale;
        } else {
            inputWindowHandle.scaleFactor = 1.0f;
        }
        addInputWindowHandleLw(inputWindowHandle);
    }

    private void clearInputWindowHandlesLw() {
        while (this.mInputWindowHandleCount != 0) {
            InputWindowHandle[] inputWindowHandleArr = this.mInputWindowHandles;
            int i = this.mInputWindowHandleCount - 1;
            this.mInputWindowHandleCount = i;
            inputWindowHandleArr[i] = null;
        }
    }

    public void setUpdateInputWindowsNeededLw() {
        this.mUpdateInputWindowsNeeded = true;
    }

    public void updateInputWindowsLw(boolean force) {
        if (!force && !this.mUpdateInputWindowsNeeded) {
            return;
        }
        this.mUpdateInputWindowsNeeded = false;
        WindowStateAnimator universeBackground = this.mService.mAnimator.mUniverseBackground;
        int aboveUniverseLayer = this.mService.mAnimator.mAboveUniverseLayer;
        boolean addedUniverse = false;
        boolean inDrag = this.mService.mDragState != null;
        if (inDrag) {
            InputWindowHandle dragWindowHandle = this.mService.mDragState.mDragWindowHandle;
            if (dragWindowHandle != null) {
                addInputWindowHandleLw(dragWindowHandle);
            } else {
                Slog.w("WindowManager", "Drag is in progress but there is no drag window handle.");
            }
        }
        int NFW = this.mService.mFakeWindows.size();
        for (int i = 0; i < NFW; i++) {
            addInputWindowHandleLw(this.mService.mFakeWindows.get(i).mWindowHandle);
        }
        int numDisplays = this.mService.mDisplayContents.size();
        for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
            WindowList windows = this.mService.mDisplayContents.valueAt(displayNdx).getWindowList();
            for (int winNdx = windows.size() - 1; winNdx >= 0; winNdx--) {
                WindowState child = windows.get(winNdx);
                InputChannel inputChannel = child.mInputChannel;
                InputWindowHandle inputWindowHandle = child.mInputWindowHandle;
                if (inputChannel != null && inputWindowHandle != null && !child.mRemoved) {
                    int flags = child.mAttrs.flags;
                    int privateFlags = child.mAttrs.privateFlags;
                    int type = child.mAttrs.type;
                    boolean hasFocus = child == this.mInputFocus;
                    boolean isVisible = child.isVisibleLw();
                    boolean hasWallpaper = child == this.mService.mWallpaperTarget && type != 2004;
                    boolean onDefaultDisplay = child.getDisplayId() == 0;
                    if (inDrag && isVisible && onDefaultDisplay) {
                        this.mService.mDragState.sendDragStartedIfNeededLw(child);
                    }
                    if (universeBackground != null && !addedUniverse && child.mBaseLayer < aboveUniverseLayer && onDefaultDisplay) {
                        WindowState u = universeBackground.mWin;
                        if (u.mInputChannel != null && u.mInputWindowHandle != null) {
                            addInputWindowHandleLw(u.mInputWindowHandle, u, u.mAttrs.flags, u.mAttrs.privateFlags, u.mAttrs.type, true, u == this.mInputFocus, false);
                        }
                        addedUniverse = true;
                    }
                    if (child.mWinAnimator != universeBackground) {
                        addInputWindowHandleLw(inputWindowHandle, child, flags, privateFlags, type, isVisible, hasFocus, hasWallpaper);
                    }
                }
            }
        }
        this.mService.mInputManager.setInputWindows(this.mInputWindowHandles);
        clearInputWindowHandlesLw();
    }

    @Override // com.android.server.input.InputManagerService.WindowManagerCallbacks
    public void notifyConfigurationChanged() {
        this.mService.sendNewConfiguration();
        synchronized (this.mInputDevicesReadyMonitor) {
            if (!this.mInputDevicesReady) {
                this.mInputDevicesReady = true;
                this.mInputDevicesReadyMonitor.notifyAll();
            }
        }
    }

    public boolean waitForInputDevicesReady(long timeoutMillis) {
        boolean z;
        synchronized (this.mInputDevicesReadyMonitor) {
            if (!this.mInputDevicesReady) {
                try {
                    this.mInputDevicesReadyMonitor.wait(timeoutMillis);
                } catch (InterruptedException e) {
                }
            }
            z = this.mInputDevicesReady;
        }
        return z;
    }

    @Override // com.android.server.input.InputManagerService.WindowManagerCallbacks
    public void notifyLidSwitchChanged(long whenNanos, boolean lidOpen) {
        this.mService.mPolicy.notifyLidSwitchChanged(whenNanos, lidOpen);
    }

    @Override // com.android.server.input.InputManagerService.WindowManagerCallbacks
    public int interceptKeyBeforeQueueing(KeyEvent event, int policyFlags, boolean isScreenOn) {
        return this.mService.mPolicy.interceptKeyBeforeQueueing(event, policyFlags, isScreenOn);
    }

    @Override // com.android.server.input.InputManagerService.WindowManagerCallbacks
    public int interceptMotionBeforeQueueingWhenScreenOff(int policyFlags) {
        return this.mService.mPolicy.interceptMotionBeforeQueueingWhenScreenOff(policyFlags);
    }

    @Override // com.android.server.input.InputManagerService.WindowManagerCallbacks
    public long interceptKeyBeforeDispatching(InputWindowHandle focus, KeyEvent event, int policyFlags) {
        WindowState windowState = focus != null ? (WindowState) focus.windowState : null;
        return this.mService.mPolicy.interceptKeyBeforeDispatching(windowState, event, policyFlags);
    }

    @Override // com.android.server.input.InputManagerService.WindowManagerCallbacks
    public KeyEvent dispatchUnhandledKey(InputWindowHandle focus, KeyEvent event, int policyFlags) {
        WindowState windowState = focus != null ? (WindowState) focus.windowState : null;
        return this.mService.mPolicy.dispatchUnhandledKey(windowState, event, policyFlags);
    }

    @Override // com.android.server.input.InputManagerService.WindowManagerCallbacks
    public int getPointerLayer() {
        return (this.mService.mPolicy.windowTypeToLayerLw(WindowManager.LayoutParams.TYPE_POINTER) * 10000) + 1000;
    }

    public void setInputFocusLw(WindowState newWindow, boolean updateInputWindows) {
        if (newWindow != this.mInputFocus) {
            if (newWindow != null && newWindow.canReceiveKeys()) {
                newWindow.mToken.paused = false;
            }
            this.mInputFocus = newWindow;
            setUpdateInputWindowsNeededLw();
            if (updateInputWindows) {
                updateInputWindowsLw(false);
            }
        }
    }

    public void setFocusedAppLw(AppWindowToken newApp) {
        if (newApp == null) {
            this.mService.mInputManager.setFocusedApplication(null);
            return;
        }
        InputApplicationHandle handle = newApp.mInputApplicationHandle;
        handle.name = newApp.toString();
        handle.dispatchingTimeoutNanos = newApp.inputDispatchingTimeoutNanos;
        this.mService.mInputManager.setFocusedApplication(handle);
    }

    public void pauseDispatchingLw(WindowToken window) {
        if (!window.paused) {
            window.paused = true;
            updateInputWindowsLw(true);
        }
    }

    public void resumeDispatchingLw(WindowToken window) {
        if (window.paused) {
            window.paused = false;
            updateInputWindowsLw(true);
        }
    }

    public void freezeInputDispatchingLw() {
        if (!this.mInputDispatchFrozen) {
            this.mInputDispatchFrozen = true;
            updateInputDispatchModeLw();
        }
    }

    public void thawInputDispatchingLw() {
        if (this.mInputDispatchFrozen) {
            this.mInputDispatchFrozen = false;
            updateInputDispatchModeLw();
        }
    }

    public void setEventDispatchingLw(boolean enabled) {
        if (this.mInputDispatchEnabled != enabled) {
            this.mInputDispatchEnabled = enabled;
            updateInputDispatchModeLw();
        }
    }

    private void updateInputDispatchModeLw() {
        this.mService.mInputManager.setInputDispatchMode(this.mInputDispatchEnabled, this.mInputDispatchFrozen);
    }
}