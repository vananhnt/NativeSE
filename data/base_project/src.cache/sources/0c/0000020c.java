package android.app;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.app.IUiAutomationConnection;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.input.InputManager;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IWindowManager;
import android.view.InputEvent;
import android.view.SurfaceControl;
import android.view.accessibility.IAccessibilityManager;

/* loaded from: UiAutomationConnection.class */
public final class UiAutomationConnection extends IUiAutomationConnection.Stub {
    private static final int INITIAL_FROZEN_ROTATION_UNSPECIFIED = -1;
    private final IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
    private final Object mLock = new Object();
    private final Binder mToken = new Binder();
    private int mInitialFrozenRotation = -1;
    private IAccessibilityServiceClient mClient;
    private boolean mIsShutdown;
    private int mOwningUid;

    @Override // android.app.IUiAutomationConnection
    public void connect(IAccessibilityServiceClient client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null!");
        }
        synchronized (this.mLock) {
            throwIfShutdownLocked();
            if (isConnectedLocked()) {
                throw new IllegalStateException("Already connected.");
            }
            this.mOwningUid = Binder.getCallingUid();
            registerUiTestAutomationServiceLocked(client);
            storeRotationStateLocked();
        }
    }

    @Override // android.app.IUiAutomationConnection
    public void disconnect() {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            if (!isConnectedLocked()) {
                throw new IllegalStateException("Already disconnected.");
            }
            this.mOwningUid = -1;
            unregisterUiTestAutomationServiceLocked();
            restoreRotationStateLocked();
        }
    }

    @Override // android.app.IUiAutomationConnection
    public boolean injectInputEvent(InputEvent event, boolean sync) {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        int mode = sync ? 2 : 0;
        long identity = Binder.clearCallingIdentity();
        try {
            boolean injectInputEvent = InputManager.getInstance().injectInputEvent(event, mode);
            Binder.restoreCallingIdentity(identity);
            return injectInputEvent;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    @Override // android.app.IUiAutomationConnection
    public boolean setRotation(int rotation) {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            if (rotation == -2) {
                this.mWindowManager.thawRotation();
            } else {
                this.mWindowManager.freezeRotation(rotation);
            }
            Binder.restoreCallingIdentity(identity);
            return true;
        } catch (RemoteException e) {
            Binder.restoreCallingIdentity(identity);
            return false;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    @Override // android.app.IUiAutomationConnection
    public Bitmap takeScreenshot(int width, int height) {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            throwIfNotConnectedLocked();
        }
        long identity = Binder.clearCallingIdentity();
        try {
            Bitmap screenshot = SurfaceControl.screenshot(width, height);
            Binder.restoreCallingIdentity(identity);
            return screenshot;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    @Override // android.app.IUiAutomationConnection
    public void shutdown() {
        synchronized (this.mLock) {
            throwIfCalledByNotTrustedUidLocked();
            throwIfShutdownLocked();
            this.mIsShutdown = true;
            if (isConnectedLocked()) {
                disconnect();
            }
        }
    }

    private void registerUiTestAutomationServiceLocked(IAccessibilityServiceClient client) {
        IAccessibilityManager manager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE));
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = -1;
        info.feedbackType = 16;
        info.flags |= 18;
        info.setCapabilities(15);
        try {
            manager.registerUiTestAutomationService(this.mToken, client, info);
            this.mClient = client;
        } catch (RemoteException re) {
            throw new IllegalStateException("Error while registering UiTestAutomationService.", re);
        }
    }

    private void unregisterUiTestAutomationServiceLocked() {
        IAccessibilityManager manager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE));
        try {
            manager.unregisterUiTestAutomationService(this.mClient);
            this.mClient = null;
        } catch (RemoteException re) {
            throw new IllegalStateException("Error while unregistering UiTestAutomationService", re);
        }
    }

    private void storeRotationStateLocked() {
        try {
            if (this.mWindowManager.isRotationFrozen()) {
                this.mInitialFrozenRotation = this.mWindowManager.getRotation();
            }
        } catch (RemoteException e) {
        }
    }

    private void restoreRotationStateLocked() {
        try {
            if (this.mInitialFrozenRotation != -1) {
                this.mWindowManager.freezeRotation(this.mInitialFrozenRotation);
            } else {
                this.mWindowManager.thawRotation();
            }
        } catch (RemoteException e) {
        }
    }

    private boolean isConnectedLocked() {
        return this.mClient != null;
    }

    private void throwIfShutdownLocked() {
        if (this.mIsShutdown) {
            throw new IllegalStateException("Connection shutdown!");
        }
    }

    private void throwIfNotConnectedLocked() {
        if (!isConnectedLocked()) {
            throw new IllegalStateException("Not connected!");
        }
    }

    private void throwIfCalledByNotTrustedUidLocked() {
        int callingUid = Binder.getCallingUid();
        if (callingUid != this.mOwningUid && this.mOwningUid != 1000 && callingUid != 0) {
            throw new SecurityException("Calling from not trusted UID!");
        }
    }
}