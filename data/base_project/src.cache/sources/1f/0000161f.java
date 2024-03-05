package android.view.accessibility;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.view.IWindow;
import android.view.accessibility.IAccessibilityManager;
import android.view.accessibility.IAccessibilityManagerClient;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: AccessibilityManager.class */
public final class AccessibilityManager {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AccessibilityManager";
    public static final int STATE_FLAG_ACCESSIBILITY_ENABLED = 1;
    public static final int STATE_FLAG_TOUCH_EXPLORATION_ENABLED = 2;
    static final Object sInstanceSync = new Object();
    private static AccessibilityManager sInstance;
    private static final int DO_SET_STATE = 10;
    final IAccessibilityManager mService;
    final int mUserId;
    final Handler mHandler;
    boolean mIsEnabled;
    boolean mIsTouchExplorationEnabled;
    private final CopyOnWriteArrayList<AccessibilityStateChangeListener> mAccessibilityStateChangeListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<TouchExplorationStateChangeListener> mTouchExplorationStateChangeListeners = new CopyOnWriteArrayList<>();
    final IAccessibilityManagerClient.Stub mClient = new IAccessibilityManagerClient.Stub() { // from class: android.view.accessibility.AccessibilityManager.1
        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void setState(int state) {
            AccessibilityManager.this.mHandler.obtainMessage(10, state, 0).sendToTarget();
        }
    };

    /* loaded from: AccessibilityManager$AccessibilityStateChangeListener.class */
    public interface AccessibilityStateChangeListener {
        void onAccessibilityStateChanged(boolean z);
    }

    /* loaded from: AccessibilityManager$TouchExplorationStateChangeListener.class */
    public interface TouchExplorationStateChangeListener {
        void onTouchExplorationStateChanged(boolean z);
    }

    /* loaded from: AccessibilityManager$MyHandler.class */
    class MyHandler extends Handler {
        MyHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 10:
                    AccessibilityManager.this.setState(message.arg1);
                    return;
                default:
                    Log.w(AccessibilityManager.LOG_TAG, "Unknown message type: " + message.what);
                    return;
            }
        }
    }

    public static AccessibilityManager getInstance(Context context) {
        int userId;
        synchronized (sInstanceSync) {
            if (sInstance == null) {
                if (Binder.getCallingUid() == 1000 || context.checkCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS) == 0 || context.checkCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL) == 0) {
                    userId = -2;
                } else {
                    userId = UserHandle.myUserId();
                }
                IBinder iBinder = ServiceManager.getService(Context.ACCESSIBILITY_SERVICE);
                IAccessibilityManager service = IAccessibilityManager.Stub.asInterface(iBinder);
                sInstance = new AccessibilityManager(context, service, userId);
            }
        }
        return sInstance;
    }

    public AccessibilityManager(Context context, IAccessibilityManager service, int userId) {
        this.mHandler = new MyHandler(context.getMainLooper());
        this.mService = service;
        this.mUserId = userId;
        try {
            int stateFlags = this.mService.addClient(this.mClient, userId);
            setState(stateFlags);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "AccessibilityManagerService is dead", re);
        }
    }

    public boolean isEnabled() {
        boolean z;
        synchronized (this.mHandler) {
            z = this.mIsEnabled;
        }
        return z;
    }

    public boolean isTouchExplorationEnabled() {
        boolean z;
        synchronized (this.mHandler) {
            z = this.mIsTouchExplorationEnabled;
        }
        return z;
    }

    public IAccessibilityManagerClient getClient() {
        return (IAccessibilityManagerClient) this.mClient.asBinder();
    }

    public void sendAccessibilityEvent(AccessibilityEvent event) {
        if (!this.mIsEnabled) {
            throw new IllegalStateException("Accessibility off. Did you forget to check that?");
        }
        boolean doRecycle = false;
        try {
            try {
                event.setEventTime(SystemClock.uptimeMillis());
                long identityToken = Binder.clearCallingIdentity();
                doRecycle = this.mService.sendAccessibilityEvent(event, this.mUserId);
                Binder.restoreCallingIdentity(identityToken);
                if (doRecycle) {
                    event.recycle();
                }
            } catch (RemoteException re) {
                Log.e(LOG_TAG, "Error during sending " + event + Separators.SP, re);
                if (doRecycle) {
                    event.recycle();
                }
            }
        } catch (Throwable th) {
            if (doRecycle) {
                event.recycle();
            }
            throw th;
        }
    }

    public void interrupt() {
        if (!this.mIsEnabled) {
            throw new IllegalStateException("Accessibility off. Did you forget to check that?");
        }
        try {
            this.mService.interrupt(this.mUserId);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error while requesting interrupt from all services. ", re);
        }
    }

    @Deprecated
    public List<ServiceInfo> getAccessibilityServiceList() {
        List<AccessibilityServiceInfo> infos = getInstalledAccessibilityServiceList();
        List<ServiceInfo> services = new ArrayList<>();
        int infoCount = infos.size();
        for (int i = 0; i < infoCount; i++) {
            AccessibilityServiceInfo info = infos.get(i);
            services.add(info.getResolveInfo().serviceInfo);
        }
        return Collections.unmodifiableList(services);
    }

    public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList() {
        List<AccessibilityServiceInfo> services = null;
        try {
            services = this.mService.getInstalledAccessibilityServiceList(this.mUserId);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error while obtaining the installed AccessibilityServices. ", re);
        }
        return Collections.unmodifiableList(services);
    }

    public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int feedbackTypeFlags) {
        List<AccessibilityServiceInfo> services = null;
        try {
            services = this.mService.getEnabledAccessibilityServiceList(feedbackTypeFlags, this.mUserId);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error while obtaining the installed AccessibilityServices. ", re);
        }
        return Collections.unmodifiableList(services);
    }

    public boolean addAccessibilityStateChangeListener(AccessibilityStateChangeListener listener) {
        return this.mAccessibilityStateChangeListeners.add(listener);
    }

    public boolean removeAccessibilityStateChangeListener(AccessibilityStateChangeListener listener) {
        return this.mAccessibilityStateChangeListeners.remove(listener);
    }

    public boolean addTouchExplorationStateChangeListener(TouchExplorationStateChangeListener listener) {
        return this.mTouchExplorationStateChangeListeners.add(listener);
    }

    public boolean removeTouchExplorationStateChangeListener(TouchExplorationStateChangeListener listener) {
        return this.mTouchExplorationStateChangeListeners.remove(listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setState(int stateFlags) {
        boolean enabled = (stateFlags & 1) != 0;
        boolean touchExplorationEnabled = (stateFlags & 2) != 0;
        synchronized (this.mHandler) {
            boolean wasEnabled = this.mIsEnabled;
            boolean wasTouchExplorationEnabled = this.mIsTouchExplorationEnabled;
            this.mIsEnabled = enabled;
            this.mIsTouchExplorationEnabled = touchExplorationEnabled;
            if (wasEnabled != enabled) {
                notifyAccessibilityStateChangedLh();
            }
            if (wasTouchExplorationEnabled != touchExplorationEnabled) {
                notifyTouchExplorationStateChangedLh();
            }
        }
    }

    private void notifyAccessibilityStateChangedLh() {
        int listenerCount = this.mAccessibilityStateChangeListeners.size();
        for (int i = 0; i < listenerCount; i++) {
            this.mAccessibilityStateChangeListeners.get(i).onAccessibilityStateChanged(this.mIsEnabled);
        }
    }

    private void notifyTouchExplorationStateChangedLh() {
        int listenerCount = this.mTouchExplorationStateChangeListeners.size();
        for (int i = 0; i < listenerCount; i++) {
            this.mTouchExplorationStateChangeListeners.get(i).onTouchExplorationStateChanged(this.mIsTouchExplorationEnabled);
        }
    }

    public int addAccessibilityInteractionConnection(IWindow windowToken, IAccessibilityInteractionConnection connection) {
        try {
            return this.mService.addAccessibilityInteractionConnection(windowToken, connection, this.mUserId);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error while adding an accessibility interaction connection. ", re);
            return -1;
        }
    }

    public void removeAccessibilityInteractionConnection(IWindow windowToken) {
        try {
            this.mService.removeAccessibilityInteractionConnection(windowToken);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error while removing an accessibility interaction connection. ", re);
        }
    }
}