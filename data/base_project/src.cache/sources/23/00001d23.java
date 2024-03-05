package com.android.server.accessibility;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.IAccessibilityServiceConnection;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Pools;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.IWindow;
import android.view.IWindowManager;
import android.view.InputEventConsistencyVerifier;
import android.view.KeyEvent;
import android.view.MagnificationSpec;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.IAccessibilityInteractionConnection;
import android.view.accessibility.IAccessibilityManager;
import android.view.accessibility.IAccessibilityManagerClient;
import com.android.internal.R;
import com.android.internal.content.PackageMonitor;
import com.android.internal.statusbar.IStatusBarService;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: AccessibilityManagerService.class */
public class AccessibilityManagerService extends IAccessibilityManager.Stub {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AccessibilityManagerService";
    private static final int WAIT_FOR_USER_STATE_FULLY_INITIALIZED_MILLIS = 3000;
    private static final String FUNCTION_REGISTER_UI_TEST_AUTOMATION_SERVICE = "registerUiTestAutomationService";
    private static final String TEMPORARY_ENABLE_ACCESSIBILITY_UNTIL_KEYGUARD_REMOVED = "temporaryEnableAccessibilityStateUntilKeyguardRemoved";
    private static final String FUNCTION_DUMP = "dump";
    private static final char COMPONENT_NAME_SEPARATOR = ':';
    private static final int MAX_POOL_SIZE = 10;
    private static int sNextWindowId;
    private final Context mContext;
    private final Display mDefaultDisplay;
    private final PackageManager mPackageManager;
    private final MainHandler mMainHandler;
    private Service mQueryBridge;
    private AlertDialog mEnableTouchExplorationDialog;
    private AccessibilityInputFilter mInputFilter;
    private boolean mHasInputFilter;
    private boolean mInitialized;
    private static final ComponentName sFakeAccessibilityServiceComponentName = new ComponentName("foo.bar", "FakeService");
    private static final int OWN_PROCESS_ID = Process.myPid();
    private static int sIdCounter = 0;
    private final Object mLock = new Object();
    private final Pools.Pool<PendingEvent> mPendingEventPool = new Pools.SimplePool(10);
    private final TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
    private final List<AccessibilityServiceInfo> mEnabledServicesForFeedbackTempList = new ArrayList();
    private final Rect mTempRect = new Rect();
    private final Point mTempPoint = new Point();
    private final Set<ComponentName> mTempComponentNameSet = new HashSet();
    private final List<AccessibilityServiceInfo> mTempAccessibilityServiceInfoList = new ArrayList();
    private final RemoteCallbackList<IAccessibilityManagerClient> mGlobalClients = new RemoteCallbackList<>();
    private final SparseArray<AccessibilityConnectionWrapper> mGlobalInteractionConnections = new SparseArray<>();
    private final SparseArray<IBinder> mGlobalWindowTokens = new SparseArray<>();
    private final SparseArray<UserState> mUserStates = new SparseArray<>();
    private int mCurrentUserId = 0;
    private final IWindowManager mWindowManagerService = (IWindowManager) ServiceManager.getService(Context.WINDOW_SERVICE);
    private final SecurityPolicy mSecurityPolicy = new SecurityPolicy();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.getAccessibilityFocusBoundsInActiveWindow(android.graphics.Rect):boolean, file: AccessibilityManagerService.class
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
    boolean getAccessibilityFocusBoundsInActiveWindow(android.graphics.Rect r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.getAccessibilityFocusBoundsInActiveWindow(android.graphics.Rect):boolean, file: AccessibilityManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.getAccessibilityFocusBoundsInActiveWindow(android.graphics.Rect):boolean");
    }

    static /* synthetic */ Object access$000(AccessibilityManagerService x0) {
        return x0.mLock;
    }

    static /* synthetic */ int access$100(AccessibilityManagerService x0) {
        return x0.mCurrentUserId;
    }

    static /* synthetic */ void access$500(AccessibilityManagerService x0, UserState x1) {
        x0.onUserStateChangedLocked(x1);
    }

    static /* synthetic */ UserState access$600(AccessibilityManagerService x0, int x1) {
        return x0.getUserStateLocked(x1);
    }

    static /* synthetic */ SecurityPolicy access$2200(AccessibilityManagerService x0) {
        return x0.mSecurityPolicy;
    }

    static /* synthetic */ int access$2908() {
        int i = sIdCounter;
        sIdCounter = i + 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public UserState getCurrentUserStateLocked() {
        return getUserStateLocked(this.mCurrentUserId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public UserState getUserStateLocked(int userId) {
        UserState state = this.mUserStates.get(userId);
        if (state == null) {
            state = new UserState(userId);
            this.mUserStates.put(userId, state);
        }
        return state;
    }

    public AccessibilityManagerService(Context context) {
        this.mContext = context;
        this.mPackageManager = this.mContext.getPackageManager();
        this.mMainHandler = new MainHandler(this.mContext.getMainLooper());
        DisplayManager displayManager = (DisplayManager) this.mContext.getSystemService(Context.DISPLAY_SERVICE);
        this.mDefaultDisplay = displayManager.getDisplay(0);
        registerBroadcastReceivers();
        new AccessibilityContentObserver(this.mMainHandler).register(context.getContentResolver());
    }

    private void registerBroadcastReceivers() {
        PackageMonitor monitor = new PackageMonitor() { // from class: com.android.server.accessibility.AccessibilityManagerService.1
            @Override // com.android.internal.content.PackageMonitor
            public void onSomePackagesChanged() {
                synchronized (AccessibilityManagerService.this.mLock) {
                    if (getChangingUserId() != AccessibilityManagerService.this.mCurrentUserId) {
                        return;
                    }
                    UserState userState = AccessibilityManagerService.this.getCurrentUserStateLocked();
                    userState.mInstalledServices.clear();
                    if (userState.mUiAutomationService == null && AccessibilityManagerService.this.readConfigurationForUserStateLocked(userState)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(userState);
                    }
                }
            }

            @Override // com.android.internal.content.PackageMonitor
            public void onPackageRemoved(String packageName, int uid) {
                synchronized (AccessibilityManagerService.this.mLock) {
                    int userId = getChangingUserId();
                    if (userId != AccessibilityManagerService.this.mCurrentUserId) {
                        return;
                    }
                    UserState userState = AccessibilityManagerService.this.getUserStateLocked(userId);
                    Iterator<ComponentName> it = userState.mEnabledServices.iterator();
                    while (it.hasNext()) {
                        ComponentName comp = it.next();
                        String compPkg = comp.getPackageName();
                        if (compPkg.equals(packageName)) {
                            it.remove();
                            AccessibilityManagerService.this.persistComponentNamesToSettingLocked(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, userState.mEnabledServices, userId);
                            userState.mTouchExplorationGrantedServices.remove(comp);
                            AccessibilityManagerService.this.persistComponentNamesToSettingLocked(Settings.Secure.TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES, userState.mTouchExplorationGrantedServices, userId);
                            if (userState.mUiAutomationService == null) {
                                AccessibilityManagerService.this.onUserStateChangedLocked(userState);
                            }
                            return;
                        }
                    }
                }
            }

            @Override // com.android.internal.content.PackageMonitor
            public boolean onHandleForceStop(Intent intent, String[] packages, int uid, boolean doit) {
                synchronized (AccessibilityManagerService.this.mLock) {
                    int userId = getChangingUserId();
                    if (userId == AccessibilityManagerService.this.mCurrentUserId) {
                        UserState userState = AccessibilityManagerService.this.getUserStateLocked(userId);
                        Iterator<ComponentName> it = userState.mEnabledServices.iterator();
                        while (it.hasNext()) {
                            ComponentName comp = it.next();
                            String compPkg = comp.getPackageName();
                            for (String pkg : packages) {
                                if (compPkg.equals(pkg)) {
                                    if (!doit) {
                                        return true;
                                    }
                                    it.remove();
                                    AccessibilityManagerService.this.persistComponentNamesToSettingLocked(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, userState.mEnabledServices, userId);
                                    if (userState.mUiAutomationService == null) {
                                        AccessibilityManagerService.this.onUserStateChangedLocked(userState);
                                    }
                                }
                            }
                        }
                        return false;
                    }
                    return false;
                }
            }
        };
        monitor.register(this.mContext, null, UserHandle.ALL, true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_SWITCHED);
        intentFilter.addAction(Intent.ACTION_USER_REMOVED);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.accessibility.AccessibilityManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_USER_SWITCHED.equals(action)) {
                    AccessibilityManagerService.this.switchUser(intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0));
                } else if (Intent.ACTION_USER_REMOVED.equals(action)) {
                    AccessibilityManagerService.this.removeUser(intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0));
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    UserState userState = AccessibilityManagerService.this.getCurrentUserStateLocked();
                    if (userState.mUiAutomationService == null && AccessibilityManagerService.this.readConfigurationForUserStateLocked(userState)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(userState);
                    }
                }
            }
        }, UserHandle.ALL, intentFilter, null, null);
    }

    @Override // android.view.accessibility.IAccessibilityManager
    public int addClient(IAccessibilityManagerClient client, int userId) {
        synchronized (this.mLock) {
            int resolvedUserId = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(userId);
            UserState userState = getUserStateLocked(resolvedUserId);
            if (this.mSecurityPolicy.isCallerInteractingAcrossUsers(userId)) {
                this.mGlobalClients.register(client);
                return userState.getClientState();
            }
            userState.mClients.register(client);
            return resolvedUserId == this.mCurrentUserId ? userState.getClientState() : 0;
        }
    }

    @Override // android.view.accessibility.IAccessibilityManager
    public boolean sendAccessibilityEvent(AccessibilityEvent event, int userId) {
        synchronized (this.mLock) {
            int resolvedUserId = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(userId);
            if (resolvedUserId != this.mCurrentUserId) {
                return true;
            }
            if (this.mSecurityPolicy.canDispatchAccessibilityEvent(event)) {
                this.mSecurityPolicy.updateEventSourceLocked(event);
                this.mMainHandler.obtainMessage(4, event.getWindowId(), event.getEventType()).sendToTarget();
                notifyAccessibilityServicesDelayedLocked(event, false);
                notifyAccessibilityServicesDelayedLocked(event, true);
            }
            if (this.mHasInputFilter && this.mInputFilter != null) {
                this.mMainHandler.obtainMessage(1, AccessibilityEvent.obtain(event)).sendToTarget();
            }
            event.recycle();
            getUserStateLocked(resolvedUserId).mHandledFeedbackTypes = 0;
            return OWN_PROCESS_ID != Binder.getCallingPid();
        }
    }

    @Override // android.view.accessibility.IAccessibilityManager
    public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int userId) {
        synchronized (this.mLock) {
            int resolvedUserId = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(userId);
            UserState userState = getUserStateLocked(resolvedUserId);
            if (userState.mUiAutomationService != null) {
                List<AccessibilityServiceInfo> installedServices = new ArrayList<>();
                installedServices.addAll(userState.mInstalledServices);
                installedServices.remove(userState.mUiAutomationService);
                return installedServices;
            }
            return userState.mInstalledServices;
        }
    }

    @Override // android.view.accessibility.IAccessibilityManager
    public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int feedbackType, int userId) {
        synchronized (this.mLock) {
            int resolvedUserId = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(userId);
            UserState userState = getUserStateLocked(resolvedUserId);
            if (userState.mUiAutomationService != null) {
                return Collections.emptyList();
            }
            List<AccessibilityServiceInfo> result = this.mEnabledServicesForFeedbackTempList;
            result.clear();
            List<Service> services = userState.mBoundServices;
            while (feedbackType != 0) {
                int feedbackTypeBit = 1 << Integer.numberOfTrailingZeros(feedbackType);
                feedbackType &= feedbackTypeBit ^ (-1);
                int serviceCount = services.size();
                for (int i = 0; i < serviceCount; i++) {
                    Service service = services.get(i);
                    if ((service.mFeedbackType & feedbackTypeBit) != 0) {
                        result.add(service.mAccessibilityServiceInfo);
                    }
                }
            }
            return result;
        }
    }

    @Override // android.view.accessibility.IAccessibilityManager
    public void interrupt(int userId) {
        synchronized (this.mLock) {
            int resolvedUserId = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(userId);
            if (resolvedUserId != this.mCurrentUserId) {
                return;
            }
            CopyOnWriteArrayList<Service> services = getUserStateLocked(resolvedUserId).mBoundServices;
            int count = services.size();
            for (int i = 0; i < count; i++) {
                Service service = services.get(i);
                try {
                    service.mServiceInterface.onInterrupt();
                } catch (RemoteException re) {
                    Slog.e(LOG_TAG, "Error during sending interrupt request to " + service.mService, re);
                }
            }
        }
    }

    @Override // android.view.accessibility.IAccessibilityManager
    public int addAccessibilityInteractionConnection(IWindow windowToken, IAccessibilityInteractionConnection connection, int userId) throws RemoteException {
        int windowId;
        synchronized (this.mLock) {
            int resolvedUserId = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(userId);
            windowId = sNextWindowId;
            sNextWindowId = windowId + 1;
            if (this.mSecurityPolicy.isCallerInteractingAcrossUsers(userId)) {
                AccessibilityConnectionWrapper wrapper = new AccessibilityConnectionWrapper(windowId, connection, -1);
                wrapper.linkToDeath();
                this.mGlobalInteractionConnections.put(windowId, wrapper);
                this.mGlobalWindowTokens.put(windowId, windowToken.asBinder());
            } else {
                AccessibilityConnectionWrapper wrapper2 = new AccessibilityConnectionWrapper(windowId, connection, resolvedUserId);
                wrapper2.linkToDeath();
                UserState userState = getUserStateLocked(resolvedUserId);
                userState.mInteractionConnections.put(windowId, wrapper2);
                userState.mWindowTokens.put(windowId, windowToken.asBinder());
            }
        }
        return windowId;
    }

    @Override // android.view.accessibility.IAccessibilityManager
    public void removeAccessibilityInteractionConnection(IWindow window) {
        synchronized (this.mLock) {
            this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(UserHandle.getCallingUserId());
            IBinder token = window.asBinder();
            int removedWindowId = removeAccessibilityInteractionConnectionInternalLocked(token, this.mGlobalWindowTokens, this.mGlobalInteractionConnections);
            if (removedWindowId >= 0) {
                return;
            }
            int userCount = this.mUserStates.size();
            for (int i = 0; i < userCount; i++) {
                UserState userState = this.mUserStates.valueAt(i);
                int removedWindowIdForUser = removeAccessibilityInteractionConnectionInternalLocked(token, userState.mWindowTokens, userState.mInteractionConnections);
                if (removedWindowIdForUser >= 0) {
                    return;
                }
            }
        }
    }

    private int removeAccessibilityInteractionConnectionInternalLocked(IBinder windowToken, SparseArray<IBinder> windowTokens, SparseArray<AccessibilityConnectionWrapper> interactionConnections) {
        int count = windowTokens.size();
        for (int i = 0; i < count; i++) {
            if (windowTokens.valueAt(i) == windowToken) {
                int windowId = windowTokens.keyAt(i);
                windowTokens.removeAt(i);
                AccessibilityConnectionWrapper wrapper = interactionConnections.get(windowId);
                wrapper.unlinkToDeath();
                interactionConnections.remove(windowId);
                return windowId;
            }
        }
        return -1;
    }

    @Override // android.view.accessibility.IAccessibilityManager
    public void registerUiTestAutomationService(IBinder owner, IAccessibilityServiceClient serviceClient, AccessibilityServiceInfo accessibilityServiceInfo) {
        this.mSecurityPolicy.enforceCallingPermission(Manifest.permission.RETRIEVE_WINDOW_CONTENT, FUNCTION_REGISTER_UI_TEST_AUTOMATION_SERVICE);
        accessibilityServiceInfo.setComponentName(sFakeAccessibilityServiceComponentName);
        synchronized (this.mLock) {
            UserState userState = getCurrentUserStateLocked();
            if (userState.mUiAutomationService != null) {
                throw new IllegalStateException("UiAutomationService " + serviceClient + "already registered!");
            }
            try {
                owner.linkToDeath(userState.mUiAutomationSerivceOnwerDeathRecipient, 0);
                userState.mUiAutomationServiceOwner = owner;
                userState.mUiAutomationServiceClient = serviceClient;
                userState.mIsAccessibilityEnabled = true;
                userState.mIsTouchExplorationEnabled = false;
                userState.mIsEnhancedWebAccessibilityEnabled = false;
                userState.mIsDisplayMagnificationEnabled = false;
                userState.mInstalledServices.add(accessibilityServiceInfo);
                userState.mEnabledServices.clear();
                userState.mEnabledServices.add(sFakeAccessibilityServiceComponentName);
                userState.mTouchExplorationGrantedServices.add(sFakeAccessibilityServiceComponentName);
                onUserStateChangedLocked(userState);
            } catch (RemoteException re) {
                Slog.e(LOG_TAG, "Couldn't register for the death of a UiTestAutomationService!", re);
            }
        }
    }

    @Override // android.view.accessibility.IAccessibilityManager
    public void unregisterUiTestAutomationService(IAccessibilityServiceClient serviceClient) {
        synchronized (this.mLock) {
            UserState userState = getCurrentUserStateLocked();
            if (userState.mUiAutomationService == null || serviceClient == null || userState.mUiAutomationService == null || userState.mUiAutomationService.mServiceInterface == null || userState.mUiAutomationService.mServiceInterface.asBinder() != serviceClient.asBinder()) {
                throw new IllegalStateException("UiAutomationService " + serviceClient + " not registered!");
            }
            userState.mUiAutomationService.binderDied();
        }
    }

    @Override // android.view.accessibility.IAccessibilityManager
    public void temporaryEnableAccessibilityStateUntilKeyguardRemoved(ComponentName service, boolean touchExplorationEnabled) {
        this.mSecurityPolicy.enforceCallingPermission(Manifest.permission.TEMPORARY_ENABLE_ACCESSIBILITY, TEMPORARY_ENABLE_ACCESSIBILITY_UNTIL_KEYGUARD_REMOVED);
        try {
            if (!this.mWindowManagerService.isKeyguardLocked()) {
                return;
            }
            synchronized (this.mLock) {
                UserState userState = getCurrentUserStateLocked();
                if (userState.mUiAutomationService != null) {
                    return;
                }
                userState.mIsAccessibilityEnabled = true;
                userState.mIsTouchExplorationEnabled = touchExplorationEnabled;
                userState.mIsEnhancedWebAccessibilityEnabled = false;
                userState.mIsDisplayMagnificationEnabled = false;
                userState.mEnabledServices.clear();
                userState.mEnabledServices.add(service);
                userState.mBindingServices.clear();
                userState.mTouchExplorationGrantedServices.clear();
                userState.mTouchExplorationGrantedServices.add(service);
                onUserStateChangedLocked(userState);
            }
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onGesture(int gestureId) {
        boolean z;
        synchronized (this.mLock) {
            boolean handled = notifyGestureLocked(gestureId, false);
            if (!handled) {
                handled = notifyGestureLocked(gestureId, true);
            }
            z = handled;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean notifyKeyEvent(KeyEvent event, int policyFlags) {
        boolean z;
        synchronized (this.mLock) {
            KeyEvent localClone = KeyEvent.obtain(event);
            boolean handled = notifyKeyEventLocked(localClone, policyFlags, false);
            if (!handled) {
                handled = notifyKeyEventLocked(localClone, policyFlags, true);
            }
            z = handled;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getActiveWindowBounds(Rect outBounds) {
        IBinder token;
        synchronized (this.mLock) {
            int windowId = this.mSecurityPolicy.mActiveWindowId;
            token = this.mGlobalWindowTokens.get(windowId);
            if (token == null) {
                token = getCurrentUserStateLocked().mWindowTokens.get(windowId);
            }
        }
        try {
            this.mWindowManagerService.getWindowFrame(token, outBounds);
            if (!outBounds.isEmpty()) {
                return true;
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getActiveWindowId() {
        return this.mSecurityPolicy.mActiveWindowId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTouchInteractionStart() {
        this.mSecurityPolicy.onTouchInteractionStart();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTouchInteractionEnd() {
        this.mSecurityPolicy.onTouchInteractionEnd();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onMagnificationStateChanged() {
        notifyClearAccessibilityNodeInfoCacheLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchUser(int userId) {
        synchronized (this.mLock) {
            if (this.mCurrentUserId == userId && this.mInitialized) {
                return;
            }
            UserState oldUserState = getUserStateLocked(this.mCurrentUserId);
            oldUserState.onSwitchToAnotherUser();
            if (oldUserState.mClients.getRegisteredCallbackCount() > 0) {
                this.mMainHandler.obtainMessage(3, oldUserState.mUserId, 0).sendToTarget();
            }
            UserManager userManager = (UserManager) this.mContext.getSystemService("user");
            boolean announceNewUser = userManager.getUsers().size() > 1;
            this.mCurrentUserId = userId;
            UserState userState = getCurrentUserStateLocked();
            if (userState.mUiAutomationService != null) {
                userState.mUiAutomationService.binderDied();
            }
            readConfigurationForUserStateLocked(userState);
            onUserStateChangedLocked(userState);
            if (announceNewUser) {
                this.mMainHandler.sendEmptyMessageDelayed(5, 3000L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeUser(int userId) {
        synchronized (this.mLock) {
            this.mUserStates.remove(userId);
        }
    }

    private Service getQueryBridge() {
        if (this.mQueryBridge == null) {
            AccessibilityServiceInfo info = new AccessibilityServiceInfo();
            info.setCapabilities(1);
            this.mQueryBridge = new Service(-10000, sFakeAccessibilityServiceComponentName, info);
        }
        return this.mQueryBridge;
    }

    private boolean notifyGestureLocked(int gestureId, boolean isDefault) {
        UserState state = getCurrentUserStateLocked();
        for (int i = state.mBoundServices.size() - 1; i >= 0; i--) {
            Service service = state.mBoundServices.get(i);
            if (service.mRequestTouchExplorationMode && service.mIsDefault == isDefault) {
                service.notifyGesture(gestureId);
                return true;
            }
        }
        return false;
    }

    private boolean notifyKeyEventLocked(KeyEvent event, int policyFlags, boolean isDefault) {
        UserState state = getCurrentUserStateLocked();
        for (int i = state.mBoundServices.size() - 1; i >= 0; i--) {
            Service service = state.mBoundServices.get(i);
            if (service.mRequestFilterKeyEvents && (service.mAccessibilityServiceInfo.getCapabilities() & 8) != 0 && service.mIsDefault == isDefault) {
                service.notifyKeyEvent(event, policyFlags);
                return true;
            }
        }
        return false;
    }

    private void notifyClearAccessibilityNodeInfoCacheLocked() {
        UserState state = getCurrentUserStateLocked();
        for (int i = state.mBoundServices.size() - 1; i >= 0; i--) {
            Service service = state.mBoundServices.get(i);
            service.notifyClearAccessibilityNodeInfoCache();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeAccessibilityInteractionConnectionLocked(int windowId, int userId) {
        if (userId == -1) {
            this.mGlobalWindowTokens.remove(windowId);
            this.mGlobalInteractionConnections.remove(windowId);
            return;
        }
        UserState userState = getCurrentUserStateLocked();
        userState.mWindowTokens.remove(windowId);
        userState.mInteractionConnections.remove(windowId);
    }

    private boolean readInstalledAccessibilityServiceLocked(UserState userState) {
        this.mTempAccessibilityServiceInfoList.clear();
        List<ResolveInfo> installedServices = this.mPackageManager.queryIntentServicesAsUser(new Intent(AccessibilityService.SERVICE_INTERFACE), 132, this.mCurrentUserId);
        int count = installedServices.size();
        for (int i = 0; i < count; i++) {
            ResolveInfo resolveInfo = installedServices.get(i);
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (!Manifest.permission.BIND_ACCESSIBILITY_SERVICE.equals(serviceInfo.permission)) {
                Slog.w(LOG_TAG, "Skipping accessibilty service " + new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToShortString() + ": it does not require the permission " + Manifest.permission.BIND_ACCESSIBILITY_SERVICE);
            } else {
                try {
                    AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo(resolveInfo, this.mContext);
                    this.mTempAccessibilityServiceInfoList.add(accessibilityServiceInfo);
                } catch (IOException ioe) {
                    Slog.e(LOG_TAG, "Error while initializing AccessibilityServiceInfo", ioe);
                } catch (XmlPullParserException xppe) {
                    Slog.e(LOG_TAG, "Error while initializing AccessibilityServiceInfo", xppe);
                }
            }
        }
        if (!this.mTempAccessibilityServiceInfoList.equals(userState.mInstalledServices)) {
            userState.mInstalledServices.clear();
            userState.mInstalledServices.addAll(this.mTempAccessibilityServiceInfoList);
            this.mTempAccessibilityServiceInfoList.clear();
            return true;
        }
        this.mTempAccessibilityServiceInfoList.clear();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean readEnabledAccessibilityServicesLocked(UserState userState) {
        this.mTempComponentNameSet.clear();
        readComponentNamesFromSettingLocked(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, userState.mUserId, this.mTempComponentNameSet);
        if (!this.mTempComponentNameSet.equals(userState.mEnabledServices)) {
            userState.mEnabledServices.clear();
            userState.mEnabledServices.addAll(this.mTempComponentNameSet);
            this.mTempComponentNameSet.clear();
            return true;
        }
        this.mTempComponentNameSet.clear();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean readTouchExplorationGrantedAccessibilityServicesLocked(UserState userState) {
        this.mTempComponentNameSet.clear();
        readComponentNamesFromSettingLocked(Settings.Secure.TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES, userState.mUserId, this.mTempComponentNameSet);
        if (!this.mTempComponentNameSet.equals(userState.mTouchExplorationGrantedServices)) {
            userState.mTouchExplorationGrantedServices.clear();
            userState.mTouchExplorationGrantedServices.addAll(this.mTempComponentNameSet);
            this.mTempComponentNameSet.clear();
            return true;
        }
        this.mTempComponentNameSet.clear();
        return false;
    }

    private void notifyAccessibilityServicesDelayedLocked(AccessibilityEvent event, boolean isDefault) {
        try {
            UserState state = getCurrentUserStateLocked();
            int count = state.mBoundServices.size();
            for (int i = 0; i < count; i++) {
                Service service = state.mBoundServices.get(i);
                if (service.mIsDefault == isDefault && canDispathEventLocked(service, event, state.mHandledFeedbackTypes)) {
                    state.mHandledFeedbackTypes |= service.mFeedbackType;
                    service.notifyAccessibilityEvent(event);
                }
            }
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addServiceLocked(Service service, UserState userState) {
        try {
            service.linkToOwnDeathLocked();
            userState.mBoundServices.add(service);
            userState.mComponentNameToServiceMap.put(service.mComponentName, service);
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeServiceLocked(Service service, UserState userState) {
        userState.mBoundServices.remove(service);
        userState.mComponentNameToServiceMap.remove(service.mComponentName);
        service.unlinkToOwnDeathLocked();
    }

    private boolean canDispathEventLocked(Service service, AccessibilityEvent event, int handledFeedbackTypes) {
        if (!service.canReceiveEventsLocked()) {
            return false;
        }
        if (!event.isImportantForAccessibility() && (service.mFetchFlags & 8) == 0) {
            return false;
        }
        int eventType = event.getEventType();
        if ((service.mEventTypes & eventType) != eventType) {
            return false;
        }
        Set<String> packageNames = service.mPackageNames;
        CharSequence packageName = event.getPackageName();
        if (packageNames.isEmpty() || packageNames.contains(packageName)) {
            int feedbackType = service.mFeedbackType;
            if ((handledFeedbackTypes & feedbackType) != feedbackType || feedbackType == 16) {
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindAllServicesLocked(UserState userState) {
        List<Service> services = userState.mBoundServices;
        int i = 0;
        int count = services.size();
        while (i < count) {
            Service service = services.get(i);
            if (service.unbindLocked()) {
                i--;
                count--;
            }
            i++;
        }
    }

    private void readComponentNamesFromSettingLocked(String settingName, int userId, Set<ComponentName> outComponentNames) {
        ComponentName enabledService;
        String settingValue = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), settingName, userId);
        outComponentNames.clear();
        if (settingValue != null) {
            TextUtils.SimpleStringSplitter splitter = this.mStringColonSplitter;
            splitter.setString(settingValue);
            while (splitter.hasNext()) {
                String str = splitter.next();
                if (str != null && str.length() > 0 && (enabledService = ComponentName.unflattenFromString(str)) != null) {
                    outComponentNames.add(enabledService);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void persistComponentNamesToSettingLocked(String settingName, Set<ComponentName> componentNames, int userId) {
        StringBuilder builder = new StringBuilder();
        for (ComponentName componentName : componentNames) {
            if (builder.length() > 0) {
                builder.append(':');
            }
            builder.append(componentName.flattenToShortString());
        }
        Settings.Secure.putStringForUser(this.mContext.getContentResolver(), settingName, builder.toString(), userId);
    }

    private void manageServicesLocked(UserState userState) {
        Map<ComponentName, Service> componentNameToServiceMap = userState.mComponentNameToServiceMap;
        boolean isEnabled = userState.mIsAccessibilityEnabled;
        int count = userState.mInstalledServices.size();
        for (int i = 0; i < count; i++) {
            AccessibilityServiceInfo installedService = userState.mInstalledServices.get(i);
            ComponentName componentName = ComponentName.unflattenFromString(installedService.getId());
            Service service = componentNameToServiceMap.get(componentName);
            if (isEnabled) {
                if (!userState.mBindingServices.contains(componentName)) {
                    if (userState.mEnabledServices.contains(componentName)) {
                        if (service == null) {
                            service = new Service(userState.mUserId, componentName, installedService);
                        } else if (userState.mBoundServices.contains(service)) {
                        }
                        service.bindLocked();
                    } else if (service != null) {
                        service.unbindLocked();
                    }
                }
            } else if (service != null) {
                service.unbindLocked();
            } else {
                userState.mBindingServices.remove(componentName);
            }
        }
        if (isEnabled && userState.mEnabledServices.isEmpty()) {
            userState.mIsAccessibilityEnabled = false;
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0, userState.mUserId);
        }
    }

    private void scheduleUpdateClientsIfNeededLocked(UserState userState) {
        int clientState = userState.getClientState();
        if (userState.mLastSentClientState != clientState) {
            if (this.mGlobalClients.getRegisteredCallbackCount() > 0 || userState.mClients.getRegisteredCallbackCount() > 0) {
                userState.mLastSentClientState = clientState;
                this.mMainHandler.obtainMessage(2, clientState, userState.mUserId).sendToTarget();
            }
        }
    }

    private void scheduleUpdateInputFilter(UserState userState) {
        this.mMainHandler.obtainMessage(6, userState).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInputFilter(UserState userState) {
        boolean setInputFilter = false;
        AccessibilityInputFilter inputFilter = null;
        synchronized (this.mLock) {
            int flags = 0;
            if (userState.mIsDisplayMagnificationEnabled) {
                flags = 0 | 1;
            }
            if (userState.mIsAccessibilityEnabled && userState.mIsTouchExplorationEnabled) {
                flags |= 2;
            }
            if (userState.mIsFilterKeyEventsEnabled) {
                flags |= 4;
            }
            if (flags != 0) {
                if (!this.mHasInputFilter) {
                    this.mHasInputFilter = true;
                    if (this.mInputFilter == null) {
                        this.mInputFilter = new AccessibilityInputFilter(this.mContext, this);
                    }
                    inputFilter = this.mInputFilter;
                    setInputFilter = true;
                }
                this.mInputFilter.setEnabledFeatures(flags);
            } else if (this.mHasInputFilter) {
                this.mHasInputFilter = false;
                this.mInputFilter.disableFeatures();
                inputFilter = null;
                setInputFilter = true;
            }
        }
        if (setInputFilter) {
            try {
                this.mWindowManagerService.setInputFilter(inputFilter);
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showEnableTouchExplorationDialog(final Service service) {
        synchronized (this.mLock) {
            String label = service.mResolveInfo.loadLabel(this.mContext.getPackageManager()).toString();
            final UserState state = getCurrentUserStateLocked();
            if (state.mIsTouchExplorationEnabled) {
                return;
            }
            if (this.mEnableTouchExplorationDialog == null || !this.mEnableTouchExplorationDialog.isShowing()) {
                this.mEnableTouchExplorationDialog = new AlertDialog.Builder(this.mContext).setIconAttribute(16843605).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.server.accessibility.AccessibilityManagerService.4
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        state.mTouchExplorationGrantedServices.add(service.mComponentName);
                        AccessibilityManagerService.this.persistComponentNamesToSettingLocked(Settings.Secure.TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES, state.mTouchExplorationGrantedServices, state.mUserId);
                        UserState userState = AccessibilityManagerService.this.getUserStateLocked(service.mUserId);
                        userState.mIsTouchExplorationEnabled = true;
                        Settings.Secure.putIntForUser(AccessibilityManagerService.this.mContext.getContentResolver(), Settings.Secure.TOUCH_EXPLORATION_ENABLED, 1, service.mUserId);
                        AccessibilityManagerService.this.onUserStateChangedLocked(userState);
                    }
                }).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.server.accessibility.AccessibilityManagerService.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setTitle(R.string.enable_explore_by_touch_warning_title).setMessage(this.mContext.getString(R.string.enable_explore_by_touch_warning_message, label)).create();
                this.mEnableTouchExplorationDialog.getWindow().setType(2003);
                this.mEnableTouchExplorationDialog.getWindow().getAttributes().privateFlags |= 16;
                this.mEnableTouchExplorationDialog.setCanceledOnTouchOutside(true);
                this.mEnableTouchExplorationDialog.show();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserStateChangedLocked(UserState userState) {
        this.mInitialized = true;
        updateLegacyCapabilities(userState);
        updateServicesLocked(userState);
        updateFilterKeyEventsLocked(userState);
        updateTouchExplorationLocked(userState);
        updateEnhancedWebAccessibilityLocked(userState);
        scheduleUpdateInputFilter(userState);
        scheduleUpdateClientsIfNeededLocked(userState);
    }

    private void updateLegacyCapabilities(UserState userState) {
        int installedServiceCount = userState.mInstalledServices.size();
        for (int i = 0; i < installedServiceCount; i++) {
            AccessibilityServiceInfo serviceInfo = userState.mInstalledServices.get(i);
            ResolveInfo resolveInfo = serviceInfo.getResolveInfo();
            if ((serviceInfo.getCapabilities() & 2) == 0 && resolveInfo.serviceInfo.applicationInfo.targetSdkVersion <= 17) {
                ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
                if (userState.mTouchExplorationGrantedServices.contains(componentName)) {
                    serviceInfo.setCapabilities(serviceInfo.getCapabilities() | 2);
                }
            }
        }
    }

    private void updateFilterKeyEventsLocked(UserState userState) {
        int serviceCount = userState.mBoundServices.size();
        for (int i = 0; i < serviceCount; i++) {
            Service service = userState.mBoundServices.get(i);
            if (service.mRequestFilterKeyEvents && (service.mAccessibilityServiceInfo.getCapabilities() & 8) != 0) {
                userState.mIsFilterKeyEventsEnabled = true;
                return;
            }
        }
        userState.mIsFilterKeyEventsEnabled = false;
    }

    private void updateServicesLocked(UserState userState) {
        if (userState.mIsAccessibilityEnabled) {
            manageServicesLocked(userState);
        } else {
            unbindAllServicesLocked(userState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean readConfigurationForUserStateLocked(UserState userState) {
        boolean somthingChanged = false | readAccessibilityEnabledSettingLocked(userState);
        return somthingChanged | readInstalledAccessibilityServiceLocked(userState) | readEnabledAccessibilityServicesLocked(userState) | readTouchExplorationGrantedAccessibilityServicesLocked(userState) | readTouchExplorationEnabledSettingLocked(userState) | readEnhancedWebAccessibilityEnabledChangedLocked(userState) | readDisplayMagnificationEnabledSettingLocked(userState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean readAccessibilityEnabledSettingLocked(UserState userState) {
        boolean accessibilityEnabled = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0, userState.mUserId) == 1;
        if (accessibilityEnabled != userState.mIsAccessibilityEnabled) {
            userState.mIsAccessibilityEnabled = accessibilityEnabled;
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean readTouchExplorationEnabledSettingLocked(UserState userState) {
        boolean touchExplorationEnabled = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), Settings.Secure.TOUCH_EXPLORATION_ENABLED, 0, userState.mUserId) == 1;
        if (touchExplorationEnabled != userState.mIsTouchExplorationEnabled) {
            userState.mIsTouchExplorationEnabled = touchExplorationEnabled;
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean readDisplayMagnificationEnabledSettingLocked(UserState userState) {
        boolean displayMagnificationEnabled = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED, 0, userState.mUserId) == 1;
        if (displayMagnificationEnabled != userState.mIsDisplayMagnificationEnabled) {
            userState.mIsDisplayMagnificationEnabled = displayMagnificationEnabled;
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean readEnhancedWebAccessibilityEnabledChangedLocked(UserState userState) {
        boolean enhancedWeAccessibilityEnabled = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_SCRIPT_INJECTION, 0, userState.mUserId) == 1;
        if (enhancedWeAccessibilityEnabled != userState.mIsEnhancedWebAccessibilityEnabled) {
            userState.mIsEnhancedWebAccessibilityEnabled = enhancedWeAccessibilityEnabled;
            return true;
        }
        return false;
    }

    private void updateTouchExplorationLocked(UserState userState) {
        boolean enabled = false;
        int serviceCount = userState.mBoundServices.size();
        int i = 0;
        while (true) {
            if (i >= serviceCount) {
                break;
            }
            Service service = userState.mBoundServices.get(i);
            if (!canRequestAndRequestsTouchExplorationLocked(service)) {
                i++;
            } else {
                enabled = true;
                break;
            }
        }
        if (enabled != userState.mIsTouchExplorationEnabled) {
            userState.mIsTouchExplorationEnabled = enabled;
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), Settings.Secure.TOUCH_EXPLORATION_ENABLED, enabled ? 1 : 0, userState.mUserId);
        }
        try {
            this.mWindowManagerService.setTouchExplorationEnabled(enabled);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean canRequestAndRequestsTouchExplorationLocked(Service service) {
        if (!service.canReceiveEventsLocked() || !service.mRequestTouchExplorationMode) {
            return false;
        }
        if (service.mIsAutomation) {
            return true;
        }
        if (service.mResolveInfo.serviceInfo.applicationInfo.targetSdkVersion <= 17) {
            UserState userState = getUserStateLocked(service.mUserId);
            if (userState.mTouchExplorationGrantedServices.contains(service.mComponentName)) {
                return true;
            }
            if (this.mEnableTouchExplorationDialog == null || !this.mEnableTouchExplorationDialog.isShowing()) {
                this.mMainHandler.obtainMessage(7, service).sendToTarget();
                return false;
            }
            return false;
        } else if ((service.mAccessibilityServiceInfo.getCapabilities() & 2) != 0) {
            return true;
        } else {
            return false;
        }
    }

    private void updateEnhancedWebAccessibilityLocked(UserState userState) {
        boolean enabled = false;
        int serviceCount = userState.mBoundServices.size();
        int i = 0;
        while (true) {
            if (i >= serviceCount) {
                break;
            }
            Service service = userState.mBoundServices.get(i);
            if (!canRequestAndRequestsEnhancedWebAccessibilityLocked(service)) {
                i++;
            } else {
                enabled = true;
                break;
            }
        }
        if (enabled != userState.mIsEnhancedWebAccessibilityEnabled) {
            userState.mIsEnhancedWebAccessibilityEnabled = enabled;
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_SCRIPT_INJECTION, enabled ? 1 : 0, userState.mUserId);
        }
    }

    private boolean canRequestAndRequestsEnhancedWebAccessibilityLocked(Service service) {
        if (!service.canReceiveEventsLocked() || !service.mRequestEnhancedWebAccessibility) {
            return false;
        }
        if (service.mIsAutomation || (service.mAccessibilityServiceInfo.getCapabilities() & 4) != 0) {
            return true;
        }
        return false;
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mSecurityPolicy.enforceCallingPermission(Manifest.permission.DUMP, FUNCTION_DUMP);
        synchronized (this.mLock) {
            pw.println("ACCESSIBILITY MANAGER (dumpsys accessibility)");
            pw.println();
            int userCount = this.mUserStates.size();
            for (int i = 0; i < userCount; i++) {
                UserState userState = this.mUserStates.valueAt(i);
                pw.append((CharSequence) ("User state[attributes:{id=" + userState.mUserId));
                pw.append((CharSequence) (", currentUser=" + (userState.mUserId == this.mCurrentUserId)));
                pw.append((CharSequence) (", accessibilityEnabled=" + userState.mIsAccessibilityEnabled));
                pw.append((CharSequence) (", touchExplorationEnabled=" + userState.mIsTouchExplorationEnabled));
                pw.append((CharSequence) (", displayMagnificationEnabled=" + userState.mIsDisplayMagnificationEnabled));
                if (userState.mUiAutomationService != null) {
                    pw.append(", ");
                    userState.mUiAutomationService.dump(fd, pw, args);
                    pw.println();
                }
                pw.append("}");
                pw.println();
                pw.append("           services:{");
                int serviceCount = userState.mBoundServices.size();
                for (int j = 0; j < serviceCount; j++) {
                    if (j > 0) {
                        pw.append(", ");
                        pw.println();
                        pw.append("                     ");
                    }
                    Service service = userState.mBoundServices.get(j);
                    service.dump(fd, pw, args);
                }
                pw.println("}]");
                pw.println();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AccessibilityManagerService$AccessibilityConnectionWrapper.class */
    public class AccessibilityConnectionWrapper implements IBinder.DeathRecipient {
        private final int mWindowId;
        private final int mUserId;
        private final IAccessibilityInteractionConnection mConnection;

        public AccessibilityConnectionWrapper(int windowId, IAccessibilityInteractionConnection connection, int userId) {
            this.mWindowId = windowId;
            this.mUserId = userId;
            this.mConnection = connection;
        }

        public void linkToDeath() throws RemoteException {
            this.mConnection.asBinder().linkToDeath(this, 0);
        }

        public void unlinkToDeath() {
            this.mConnection.asBinder().unlinkToDeath(this, 0);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            unlinkToDeath();
            synchronized (AccessibilityManagerService.this.mLock) {
                AccessibilityManagerService.this.removeAccessibilityInteractionConnectionLocked(this.mWindowId, this.mUserId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AccessibilityManagerService$MainHandler.class */
    public final class MainHandler extends Handler {
        public static final int MSG_SEND_ACCESSIBILITY_EVENT_TO_INPUT_FILTER = 1;
        public static final int MSG_SEND_STATE_TO_CLIENTS = 2;
        public static final int MSG_SEND_CLEARED_STATE_TO_CLIENTS_FOR_USER = 3;
        public static final int MSG_UPDATE_ACTIVE_WINDOW = 4;
        public static final int MSG_ANNOUNCE_NEW_USER_IF_NEEDED = 5;
        public static final int MSG_UPDATE_INPUT_FILTER = 6;
        public static final int MSG_SHOW_ENABLED_TOUCH_EXPLORATION_DIALOG = 7;
        public static final int MSG_SEND_KEY_EVENT_TO_INPUT_FILTER = 8;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.MainHandler.sendStateToClients(int, android.os.RemoteCallbackList<android.view.accessibility.IAccessibilityManagerClient>):void, file: AccessibilityManagerService$MainHandler.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        private void sendStateToClients(int r1, android.os.RemoteCallbackList<android.view.accessibility.IAccessibilityManagerClient> r2) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.MainHandler.sendStateToClients(int, android.os.RemoteCallbackList<android.view.accessibility.IAccessibilityManagerClient>):void, file: AccessibilityManagerService$MainHandler.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.MainHandler.sendStateToClients(int, android.os.RemoteCallbackList):void");
        }

        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case 1:
                    AccessibilityEvent event = (AccessibilityEvent) msg.obj;
                    synchronized (AccessibilityManagerService.this.mLock) {
                        if (AccessibilityManagerService.this.mHasInputFilter && AccessibilityManagerService.this.mInputFilter != null) {
                            AccessibilityManagerService.this.mInputFilter.notifyAccessibilityEvent(event);
                        }
                    }
                    event.recycle();
                    return;
                case 2:
                    int clientState = msg.arg1;
                    int userId = msg.arg2;
                    sendStateToClients(clientState, AccessibilityManagerService.this.mGlobalClients);
                    sendStateToClientsForUser(clientState, userId);
                    return;
                case 3:
                    int userId2 = msg.arg1;
                    sendStateToClientsForUser(0, userId2);
                    return;
                case 4:
                    int windowId = msg.arg1;
                    int eventType = msg.arg2;
                    AccessibilityManagerService.this.mSecurityPolicy.updateActiveWindow(windowId, eventType);
                    return;
                case 5:
                    announceNewUserIfNeeded();
                    return;
                case 6:
                    UserState userState = (UserState) msg.obj;
                    AccessibilityManagerService.this.updateInputFilter(userState);
                    return;
                case 7:
                    Service service = (Service) msg.obj;
                    AccessibilityManagerService.this.showEnableTouchExplorationDialog(service);
                    return;
                case 8:
                    KeyEvent event2 = (KeyEvent) msg.obj;
                    int policyFlags = msg.arg1;
                    synchronized (AccessibilityManagerService.this.mLock) {
                        if (AccessibilityManagerService.this.mHasInputFilter && AccessibilityManagerService.this.mInputFilter != null) {
                            AccessibilityManagerService.this.mInputFilter.sendInputEvent(event2, policyFlags);
                        }
                    }
                    event2.recycle();
                    return;
                default:
                    return;
            }
        }

        private void announceNewUserIfNeeded() {
            synchronized (AccessibilityManagerService.this.mLock) {
                UserState userState = AccessibilityManagerService.this.getCurrentUserStateLocked();
                if (userState.mIsAccessibilityEnabled) {
                    UserManager userManager = (UserManager) AccessibilityManagerService.this.mContext.getSystemService("user");
                    String message = AccessibilityManagerService.this.mContext.getString(R.string.user_switched, userManager.getUserInfo(AccessibilityManagerService.this.mCurrentUserId).name);
                    AccessibilityEvent event = AccessibilityEvent.obtain(16384);
                    event.getText().add(message);
                    event.setWindowId(AccessibilityManagerService.this.mSecurityPolicy.getRetrievalAllowingWindowLocked());
                    AccessibilityManagerService.this.sendAccessibilityEvent(event, AccessibilityManagerService.this.mCurrentUserId);
                }
            }
        }

        private void sendStateToClientsForUser(int clientState, int userId) {
            UserState userState;
            synchronized (AccessibilityManagerService.this.mLock) {
                userState = AccessibilityManagerService.this.getUserStateLocked(userId);
            }
            sendStateToClients(clientState, userState.mClients);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PendingEvent obtainPendingEventLocked(KeyEvent event, int policyFlags, int sequence) {
        PendingEvent pendingEvent = this.mPendingEventPool.acquire();
        if (pendingEvent == null) {
            pendingEvent = new PendingEvent();
        }
        pendingEvent.event = event;
        pendingEvent.policyFlags = policyFlags;
        pendingEvent.sequence = sequence;
        return pendingEvent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recyclePendingEventLocked(PendingEvent pendingEvent) {
        pendingEvent.clear();
        this.mPendingEventPool.release(pendingEvent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AccessibilityManagerService$Service.class */
    public class Service extends IAccessibilityServiceConnection.Stub implements ServiceConnection, IBinder.DeathRecipient {
        final int mUserId;
        int mId;
        AccessibilityServiceInfo mAccessibilityServiceInfo;
        IBinder mService;
        IAccessibilityServiceClient mServiceInterface;
        int mEventTypes;
        int mFeedbackType;
        boolean mIsDefault;
        boolean mRequestTouchExplorationMode;
        boolean mRequestEnhancedWebAccessibility;
        boolean mRequestFilterKeyEvents;
        int mFetchFlags;
        long mNotificationTimeout;
        ComponentName mComponentName;
        Intent mIntent;
        boolean mIsAutomation;
        final ResolveInfo mResolveInfo;
        boolean mWasConnectedAndDied;
        public Handler mEventDispatchHandler;
        public InvocationHandler mInvocationHandler;
        Set<String> mPackageNames = new HashSet();
        final Rect mTempBounds = new Rect();
        final SparseArray<AccessibilityEvent> mPendingEvents = new SparseArray<>();
        final KeyEventDispatcher mKeyEventDispatcher = new KeyEventDispatcher();

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.setServiceInfo(android.accessibilityservice.AccessibilityServiceInfo):void, file: AccessibilityManagerService$Service.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setServiceInfo(android.accessibilityservice.AccessibilityServiceInfo r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.setServiceInfo(android.accessibilityservice.AccessibilityServiceInfo):void, file: AccessibilityManagerService$Service.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.Service.setServiceInfo(android.accessibilityservice.AccessibilityServiceInfo):void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.findAccessibilityNodeInfosByViewId(int, long, java.lang.String, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean, file: AccessibilityManagerService$Service.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean findAccessibilityNodeInfosByViewId(int r1, long r2, java.lang.String r4, int r5, android.view.accessibility.IAccessibilityInteractionConnectionCallback r6, long r7) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.findAccessibilityNodeInfosByViewId(int, long, java.lang.String, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean, file: AccessibilityManagerService$Service.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.Service.findAccessibilityNodeInfosByViewId(int, long, java.lang.String, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.findAccessibilityNodeInfosByText(int, long, java.lang.String, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean, file: AccessibilityManagerService$Service.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean findAccessibilityNodeInfosByText(int r1, long r2, java.lang.String r4, int r5, android.view.accessibility.IAccessibilityInteractionConnectionCallback r6, long r7) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.findAccessibilityNodeInfosByText(int, long, java.lang.String, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean, file: AccessibilityManagerService$Service.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.Service.findAccessibilityNodeInfosByText(int, long, java.lang.String, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.findAccessibilityNodeInfoByAccessibilityId(int, long, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, long):boolean, file: AccessibilityManagerService$Service.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean findAccessibilityNodeInfoByAccessibilityId(int r1, long r2, int r4, android.view.accessibility.IAccessibilityInteractionConnectionCallback r5, int r6, long r7) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.findAccessibilityNodeInfoByAccessibilityId(int, long, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, long):boolean, file: AccessibilityManagerService$Service.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.Service.findAccessibilityNodeInfoByAccessibilityId(int, long, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, long):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.findFocus(int, long, int, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean, file: AccessibilityManagerService$Service.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean findFocus(int r1, long r2, int r4, int r5, android.view.accessibility.IAccessibilityInteractionConnectionCallback r6, long r7) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.findFocus(int, long, int, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean, file: AccessibilityManagerService$Service.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.Service.findFocus(int, long, int, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.focusSearch(int, long, int, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean, file: AccessibilityManagerService$Service.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean focusSearch(int r1, long r2, int r4, int r5, android.view.accessibility.IAccessibilityInteractionConnectionCallback r6, long r7) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.focusSearch(int, long, int, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean, file: AccessibilityManagerService$Service.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.Service.focusSearch(int, long, int, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.performAccessibilityAction(int, long, int, android.os.Bundle, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean, file: AccessibilityManagerService$Service.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean performAccessibilityAction(int r1, long r2, int r4, android.os.Bundle r5, int r6, android.view.accessibility.IAccessibilityInteractionConnectionCallback r7, long r8) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.performAccessibilityAction(int, long, int, android.os.Bundle, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean, file: AccessibilityManagerService$Service.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.Service.performAccessibilityAction(int, long, int, android.os.Bundle, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, long):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.performGlobalAction(int):boolean, file: AccessibilityManagerService$Service.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public boolean performGlobalAction(int r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.performGlobalAction(int):boolean, file: AccessibilityManagerService$Service.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.Service.performGlobalAction(int):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.notifyAccessibilityEventInternal(int):void, file: AccessibilityManagerService$Service.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        /* JADX INFO: Access modifiers changed from: private */
        public void notifyAccessibilityEventInternal(int r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.accessibility.AccessibilityManagerService.Service.notifyAccessibilityEventInternal(int):void, file: AccessibilityManagerService$Service.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.accessibility.AccessibilityManagerService.Service.notifyAccessibilityEventInternal(int):void");
        }

        public Service(int userId, ComponentName componentName, AccessibilityServiceInfo accessibilityServiceInfo) {
            this.mId = 0;
            this.mEventDispatchHandler = new Handler(AccessibilityManagerService.this.mMainHandler.getLooper()) { // from class: com.android.server.accessibility.AccessibilityManagerService.Service.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    int eventType = message.what;
                    Service.this.notifyAccessibilityEventInternal(eventType);
                }
            };
            this.mInvocationHandler = new InvocationHandler(AccessibilityManagerService.this.mMainHandler.getLooper());
            this.mUserId = userId;
            this.mResolveInfo = accessibilityServiceInfo.getResolveInfo();
            this.mId = AccessibilityManagerService.access$2908();
            this.mComponentName = componentName;
            this.mAccessibilityServiceInfo = accessibilityServiceInfo;
            this.mIsAutomation = AccessibilityManagerService.sFakeAccessibilityServiceComponentName.equals(componentName);
            if (!this.mIsAutomation) {
                this.mIntent = new Intent().setComponent(this.mComponentName);
                this.mIntent.putExtra(Intent.EXTRA_CLIENT_LABEL, R.string.accessibility_binding_label);
                this.mIntent.putExtra(Intent.EXTRA_CLIENT_INTENT, PendingIntent.getActivity(AccessibilityManagerService.this.mContext, 0, new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 0));
            }
            setDynamicallyConfigurableProperties(accessibilityServiceInfo);
        }

        public void setDynamicallyConfigurableProperties(AccessibilityServiceInfo info) {
            this.mEventTypes = info.eventTypes;
            this.mFeedbackType = info.feedbackType;
            String[] packageNames = info.packageNames;
            if (packageNames != null) {
                this.mPackageNames.addAll(Arrays.asList(packageNames));
            }
            this.mNotificationTimeout = info.notificationTimeout;
            this.mIsDefault = (info.flags & 1) != 0;
            if (this.mIsAutomation || info.getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion >= 16) {
                if ((info.flags & 2) != 0) {
                    this.mFetchFlags |= 8;
                } else {
                    this.mFetchFlags &= -9;
                }
            }
            if ((info.flags & 16) != 0) {
                this.mFetchFlags |= 16;
            } else {
                this.mFetchFlags &= -17;
            }
            this.mRequestTouchExplorationMode = (info.flags & 4) != 0;
            this.mRequestEnhancedWebAccessibility = (info.flags & 8) != 0;
            this.mRequestFilterKeyEvents = (info.flags & 32) != 0;
        }

        public boolean bindLocked() {
            UserState userState = AccessibilityManagerService.this.getUserStateLocked(this.mUserId);
            if (!this.mIsAutomation) {
                if (this.mService == null && AccessibilityManagerService.this.mContext.bindServiceAsUser(this.mIntent, this, 1, new UserHandle(this.mUserId))) {
                    userState.mBindingServices.add(this.mComponentName);
                    return false;
                }
                return false;
            }
            userState.mBindingServices.add(this.mComponentName);
            this.mService = userState.mUiAutomationServiceClient.asBinder();
            onServiceConnected(this.mComponentName, this.mService);
            userState.mUiAutomationService = this;
            return false;
        }

        public boolean unbindLocked() {
            if (this.mService != null) {
                UserState userState = AccessibilityManagerService.this.getUserStateLocked(this.mUserId);
                this.mKeyEventDispatcher.flush();
                if (!this.mIsAutomation) {
                    AccessibilityManagerService.this.mContext.unbindService(this);
                } else {
                    userState.destroyUiAutomationService();
                }
                AccessibilityManagerService.this.removeServiceLocked(this, userState);
                resetLocked();
                return true;
            }
            return false;
        }

        public boolean canReceiveEventsLocked() {
            return (this.mEventTypes == 0 || this.mFeedbackType == 0 || this.mService == null) ? false : true;
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public void setOnKeyEventResult(boolean handled, int sequence) {
            this.mKeyEventDispatcher.setOnKeyEventResult(handled, sequence);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceConnection
        public AccessibilityServiceInfo getServiceInfo() {
            AccessibilityServiceInfo accessibilityServiceInfo;
            synchronized (AccessibilityManagerService.this.mLock) {
                accessibilityServiceInfo = this.mAccessibilityServiceInfo;
            }
            return accessibilityServiceInfo;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            synchronized (AccessibilityManagerService.this.mLock) {
                this.mService = service;
                this.mServiceInterface = IAccessibilityServiceClient.Stub.asInterface(service);
                UserState userState = AccessibilityManagerService.this.getUserStateLocked(this.mUserId);
                AccessibilityManagerService.this.addServiceLocked(this, userState);
                if (userState.mBindingServices.contains(this.mComponentName) || this.mWasConnectedAndDied) {
                    userState.mBindingServices.remove(this.mComponentName);
                    this.mWasConnectedAndDied = false;
                    try {
                        this.mServiceInterface.setConnection(this, this.mId);
                        AccessibilityManagerService.this.onUserStateChangedLocked(userState);
                    } catch (RemoteException re) {
                        Slog.w(AccessibilityManagerService.LOG_TAG, "Error while setting connection for service: " + service, re);
                        binderDied();
                    }
                } else {
                    binderDied();
                }
            }
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            AccessibilityManagerService.this.mSecurityPolicy.enforceCallingPermission(Manifest.permission.DUMP, AccessibilityManagerService.FUNCTION_DUMP);
            synchronized (AccessibilityManagerService.this.mLock) {
                pw.append((CharSequence) ("Service[label=" + ((Object) this.mAccessibilityServiceInfo.getResolveInfo().loadLabel(AccessibilityManagerService.this.mContext.getPackageManager()))));
                pw.append((CharSequence) (", feedbackType" + AccessibilityServiceInfo.feedbackTypeToString(this.mFeedbackType)));
                pw.append((CharSequence) (", capabilities=" + this.mAccessibilityServiceInfo.getCapabilities()));
                pw.append((CharSequence) (", eventTypes=" + AccessibilityEvent.eventTypeToString(this.mEventTypes)));
                pw.append((CharSequence) (", notificationTimeout=" + this.mNotificationTimeout));
                pw.append("]");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        public void linkToOwnDeathLocked() throws RemoteException {
            this.mService.linkToDeath(this, 0);
        }

        public void unlinkToOwnDeathLocked() {
            this.mService.unlinkToDeath(this, 0);
        }

        public void resetLocked() {
            try {
                this.mServiceInterface.setConnection(null, this.mId);
            } catch (RemoteException e) {
            }
            this.mService = null;
            this.mServiceInterface = null;
        }

        public boolean isConnectedLocked() {
            return this.mService != null;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (AccessibilityManagerService.this.mLock) {
                if (isConnectedLocked()) {
                    this.mWasConnectedAndDied = true;
                    this.mKeyEventDispatcher.flush();
                    UserState userState = AccessibilityManagerService.this.getUserStateLocked(this.mUserId);
                    AccessibilityManagerService.this.removeServiceLocked(this, userState);
                    resetLocked();
                    if (this.mIsAutomation) {
                        userState.mInstalledServices.remove(this.mAccessibilityServiceInfo);
                        userState.mEnabledServices.remove(this.mComponentName);
                        userState.destroyUiAutomationService();
                    }
                }
            }
        }

        public void notifyAccessibilityEvent(AccessibilityEvent event) {
            synchronized (AccessibilityManagerService.this.mLock) {
                int eventType = event.getEventType();
                AccessibilityEvent newEvent = AccessibilityEvent.obtain(event);
                AccessibilityEvent oldEvent = this.mPendingEvents.get(eventType);
                this.mPendingEvents.put(eventType, newEvent);
                if (oldEvent != null) {
                    this.mEventDispatchHandler.removeMessages(eventType);
                    oldEvent.recycle();
                }
                Message message = this.mEventDispatchHandler.obtainMessage(eventType);
                this.mEventDispatchHandler.sendMessageDelayed(message, this.mNotificationTimeout);
            }
        }

        public void notifyGesture(int gestureId) {
            this.mInvocationHandler.obtainMessage(1, gestureId, 0).sendToTarget();
        }

        public void notifyKeyEvent(KeyEvent event, int policyFlags) {
            this.mInvocationHandler.obtainMessage(2, policyFlags, 0, event).sendToTarget();
        }

        public void notifyClearAccessibilityNodeInfoCache() {
            this.mInvocationHandler.sendEmptyMessage(3);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyGestureInternal(int gestureId) {
            IAccessibilityServiceClient listener = this.mServiceInterface;
            if (listener != null) {
                try {
                    listener.onGesture(gestureId);
                } catch (RemoteException re) {
                    Slog.e(AccessibilityManagerService.LOG_TAG, "Error during sending gesture " + gestureId + " to " + this.mService, re);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyKeyEventInternal(KeyEvent event, int policyFlags) {
            this.mKeyEventDispatcher.notifyKeyEvent(event, policyFlags);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyClearAccessibilityNodeInfoCacheInternal() {
            IAccessibilityServiceClient listener = this.mServiceInterface;
            if (listener != null) {
                try {
                    listener.clearAccessibilityNodeInfoCache();
                } catch (RemoteException re) {
                    Slog.e(AccessibilityManagerService.LOG_TAG, "Error during requesting accessibility info cache to be cleared.", re);
                }
            }
        }

        private void sendDownAndUpKeyEvents(int keyCode) {
            long token = Binder.clearCallingIdentity();
            long downTime = SystemClock.uptimeMillis();
            KeyEvent down = KeyEvent.obtain(downTime, downTime, 0, keyCode, 0, 0, -1, 0, 8, 257, null);
            InputManager.getInstance().injectInputEvent(down, 0);
            down.recycle();
            long upTime = SystemClock.uptimeMillis();
            KeyEvent up = KeyEvent.obtain(downTime, upTime, 1, keyCode, 0, 0, -1, 0, 8, 257, null);
            InputManager.getInstance().injectInputEvent(up, 0);
            up.recycle();
            Binder.restoreCallingIdentity(token);
        }

        private void expandNotifications() {
            long token = Binder.clearCallingIdentity();
            StatusBarManager statusBarManager = (StatusBarManager) AccessibilityManagerService.this.mContext.getSystemService(Context.STATUS_BAR_SERVICE);
            statusBarManager.expandNotificationsPanel();
            Binder.restoreCallingIdentity(token);
        }

        private void expandQuickSettings() {
            long token = Binder.clearCallingIdentity();
            StatusBarManager statusBarManager = (StatusBarManager) AccessibilityManagerService.this.mContext.getSystemService(Context.STATUS_BAR_SERVICE);
            statusBarManager.expandSettingsPanel();
            Binder.restoreCallingIdentity(token);
        }

        private void openRecents() {
            long token = Binder.clearCallingIdentity();
            IStatusBarService statusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService(Context.STATUS_BAR_SERVICE));
            try {
                statusBarService.toggleRecentApps();
            } catch (RemoteException e) {
                Slog.e(AccessibilityManagerService.LOG_TAG, "Error toggling recent apps.");
            }
            Binder.restoreCallingIdentity(token);
        }

        private IAccessibilityInteractionConnection getConnectionLocked(int windowId) {
            AccessibilityConnectionWrapper wrapper = (AccessibilityConnectionWrapper) AccessibilityManagerService.this.mGlobalInteractionConnections.get(windowId);
            if (wrapper == null) {
                wrapper = AccessibilityManagerService.this.getCurrentUserStateLocked().mInteractionConnections.get(windowId);
            }
            if (wrapper != null && wrapper.mConnection != null) {
                return wrapper.mConnection;
            }
            return null;
        }

        private int resolveAccessibilityWindowIdLocked(int accessibilityWindowId) {
            if (accessibilityWindowId != -1) {
                return accessibilityWindowId;
            }
            return AccessibilityManagerService.this.mSecurityPolicy.mActiveWindowId;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public MagnificationSpec getCompatibleMagnificationSpec(int windowId) {
            try {
                IBinder windowToken = (IBinder) AccessibilityManagerService.this.mGlobalWindowTokens.get(windowId);
                if (windowToken == null) {
                    windowToken = AccessibilityManagerService.this.getCurrentUserStateLocked().mWindowTokens.get(windowId);
                }
                if (windowToken != null) {
                    return AccessibilityManagerService.this.mWindowManagerService.getCompatibleMagnificationSpecForWindow(windowToken);
                }
                return null;
            } catch (RemoteException e) {
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: AccessibilityManagerService$Service$InvocationHandler.class */
        public final class InvocationHandler extends Handler {
            public static final int MSG_ON_GESTURE = 1;
            public static final int MSG_ON_KEY_EVENT = 2;
            public static final int MSG_CLEAR_ACCESSIBILITY_NODE_INFO_CACHE = 3;
            public static final int MSG_ON_KEY_EVENT_TIMEOUT = 4;

            public InvocationHandler(Looper looper) {
                super(looper, null, true);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int type = message.what;
                switch (type) {
                    case 1:
                        int gestureId = message.arg1;
                        Service.this.notifyGestureInternal(gestureId);
                        return;
                    case 2:
                        KeyEvent event = (KeyEvent) message.obj;
                        int policyFlags = message.arg1;
                        Service.this.notifyKeyEventInternal(event, policyFlags);
                        return;
                    case 3:
                        Service.this.notifyClearAccessibilityNodeInfoCacheInternal();
                        return;
                    case 4:
                        PendingEvent eventState = (PendingEvent) message.obj;
                        Service.this.setOnKeyEventResult(false, eventState.sequence);
                        return;
                    default:
                        throw new IllegalArgumentException("Unknown message: " + type);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: AccessibilityManagerService$Service$KeyEventDispatcher.class */
        public final class KeyEventDispatcher {
            private static final long ON_KEY_EVENT_TIMEOUT_MILLIS = 500;
            private PendingEvent mPendingEvents;
            private final InputEventConsistencyVerifier mSentEventsVerifier;

            private KeyEventDispatcher() {
                this.mSentEventsVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0, KeyEventDispatcher.class.getSimpleName()) : null;
            }

            public void notifyKeyEvent(KeyEvent event, int policyFlags) {
                PendingEvent pendingEvent;
                synchronized (AccessibilityManagerService.this.mLock) {
                    pendingEvent = addPendingEventLocked(event, policyFlags);
                }
                Message message = Service.this.mInvocationHandler.obtainMessage(4, pendingEvent);
                Service.this.mInvocationHandler.sendMessageDelayed(message, ON_KEY_EVENT_TIMEOUT_MILLIS);
                try {
                    Service.this.mServiceInterface.onKeyEvent(pendingEvent.event, pendingEvent.sequence);
                } catch (RemoteException e) {
                    setOnKeyEventResult(false, pendingEvent.sequence);
                }
            }

            public void setOnKeyEventResult(boolean handled, int sequence) {
                synchronized (AccessibilityManagerService.this.mLock) {
                    PendingEvent pendingEvent = removePendingEventLocked(sequence);
                    if (pendingEvent != null) {
                        Service.this.mInvocationHandler.removeMessages(4, pendingEvent);
                        pendingEvent.handled = handled;
                        finishPendingEventLocked(pendingEvent);
                    }
                }
            }

            public void flush() {
                synchronized (AccessibilityManagerService.this.mLock) {
                    cancelAllPendingEventsLocked();
                    if (this.mSentEventsVerifier != null) {
                        this.mSentEventsVerifier.reset();
                    }
                }
            }

            private PendingEvent addPendingEventLocked(KeyEvent event, int policyFlags) {
                int sequence = event.getSequenceNumber();
                PendingEvent pendingEvent = AccessibilityManagerService.this.obtainPendingEventLocked(event, policyFlags, sequence);
                pendingEvent.next = this.mPendingEvents;
                this.mPendingEvents = pendingEvent;
                return pendingEvent;
            }

            private PendingEvent removePendingEventLocked(int sequence) {
                PendingEvent previous = null;
                PendingEvent pendingEvent = this.mPendingEvents;
                while (true) {
                    PendingEvent current = pendingEvent;
                    if (current != null) {
                        if (current.sequence == sequence) {
                            if (previous != null) {
                                previous.next = current.next;
                            } else {
                                this.mPendingEvents = current.next;
                            }
                            current.next = null;
                            return current;
                        }
                        previous = current;
                        pendingEvent = current.next;
                    } else {
                        return null;
                    }
                }
            }

            private void finishPendingEventLocked(PendingEvent pendingEvent) {
                if (!pendingEvent.handled) {
                    sendKeyEventToInputFilter(pendingEvent.event, pendingEvent.policyFlags);
                }
                pendingEvent.event = null;
                AccessibilityManagerService.this.recyclePendingEventLocked(pendingEvent);
            }

            private void sendKeyEventToInputFilter(KeyEvent event, int policyFlags) {
                if (this.mSentEventsVerifier != null) {
                    this.mSentEventsVerifier.onKeyEvent(event, 0);
                }
                AccessibilityManagerService.this.mMainHandler.obtainMessage(8, policyFlags | 1073741824, 0, event).sendToTarget();
            }

            private void cancelAllPendingEventsLocked() {
                while (this.mPendingEvents != null) {
                    PendingEvent pendingEvent = removePendingEventLocked(this.mPendingEvents.sequence);
                    pendingEvent.handled = false;
                    Service.this.mInvocationHandler.removeMessages(4, pendingEvent);
                    finishPendingEventLocked(pendingEvent);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AccessibilityManagerService$PendingEvent.class */
    public static final class PendingEvent {
        PendingEvent next;
        KeyEvent event;
        int policyFlags;
        int sequence;
        boolean handled;

        private PendingEvent() {
        }

        public void clear() {
            if (this.event != null) {
                this.event.recycle();
                this.event = null;
            }
            this.next = null;
            this.policyFlags = 0;
            this.sequence = 0;
            this.handled = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AccessibilityManagerService$SecurityPolicy.class */
    public final class SecurityPolicy {
        private static final int VALID_ACTIONS = 2097151;
        private static final int RETRIEVAL_ALLOWING_EVENT_TYPES = 113087;
        private int mActiveWindowId;
        private boolean mTouchInteractionInProgress;

        SecurityPolicy() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean canDispatchAccessibilityEvent(AccessibilityEvent event) {
            int eventType = event.getEventType();
            switch (eventType) {
                case 32:
                case 64:
                case 128:
                case 256:
                case 512:
                case 1024:
                case 262144:
                case 524288:
                case 1048576:
                case 2097152:
                    return true;
                default:
                    return event.getWindowId() == this.mActiveWindowId;
            }
        }

        public void updateEventSourceLocked(AccessibilityEvent event) {
            if ((event.getEventType() & RETRIEVAL_ALLOWING_EVENT_TYPES) == 0) {
                event.setSource(null);
            }
        }

        public void updateActiveWindow(int windowId, int eventType) {
            switch (eventType) {
                case 32:
                    if (getFocusedWindowId() == windowId) {
                        this.mActiveWindowId = windowId;
                        return;
                    }
                    return;
                case 128:
                    if (this.mTouchInteractionInProgress) {
                        this.mActiveWindowId = windowId;
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onTouchInteractionStart() {
            this.mTouchInteractionInProgress = true;
        }

        public void onTouchInteractionEnd() {
            this.mTouchInteractionInProgress = false;
            this.mActiveWindowId = getFocusedWindowId();
        }

        public int getRetrievalAllowingWindowLocked() {
            return this.mActiveWindowId;
        }

        public boolean canGetAccessibilityNodeInfoLocked(Service service, int windowId) {
            return canRetrieveWindowContent(service) && isRetrievalAllowingWindow(windowId);
        }

        public boolean canPerformActionLocked(Service service, int windowId, int action, Bundle arguments) {
            return canRetrieveWindowContent(service) && isRetrievalAllowingWindow(windowId) && isActionPermitted(action);
        }

        public boolean canRetrieveWindowContent(Service service) {
            return (service.mAccessibilityServiceInfo.getCapabilities() & 1) != 0;
        }

        public void enforceCanRetrieveWindowContent(Service service) throws RemoteException {
            if (!canRetrieveWindowContent(service)) {
                Slog.e(AccessibilityManagerService.LOG_TAG, "Accessibility serivce " + service.mComponentName + " does not declare android:canRetrieveWindowContent.");
                throw new RemoteException();
            }
        }

        public int resolveCallingUserIdEnforcingPermissionsLocked(int userId) {
            int callingUid = Binder.getCallingUid();
            if (callingUid == 0 || callingUid == 1000 || callingUid == 2000) {
                return AccessibilityManagerService.this.mCurrentUserId;
            }
            int callingUserId = UserHandle.getUserId(callingUid);
            if (callingUserId == userId) {
                return userId;
            }
            if (!hasPermission(Manifest.permission.INTERACT_ACROSS_USERS) && !hasPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL)) {
                throw new SecurityException("Call from user " + callingUserId + " as user " + userId + " without permission INTERACT_ACROSS_USERS or INTERACT_ACROSS_USERS_FULL not allowed.");
            }
            if (userId == -2 || userId == -3) {
                return AccessibilityManagerService.this.mCurrentUserId;
            }
            throw new IllegalArgumentException("Calling user can be changed to only UserHandle.USER_CURRENT or UserHandle.USER_CURRENT_OR_SELF.");
        }

        public boolean isCallerInteractingAcrossUsers(int userId) {
            int callingUid = Binder.getCallingUid();
            return Binder.getCallingPid() == Process.myPid() || callingUid == 2000 || userId == -2 || userId == -3;
        }

        private boolean isRetrievalAllowingWindow(int windowId) {
            return this.mActiveWindowId == windowId;
        }

        private boolean isActionPermitted(int action) {
            return (VALID_ACTIONS & action) != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void enforceCallingPermission(String permission, String function) {
            if (AccessibilityManagerService.OWN_PROCESS_ID != Binder.getCallingPid() && !hasPermission(permission)) {
                throw new SecurityException("You do not have " + permission + " required to call " + function + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            }
        }

        private boolean hasPermission(String permission) {
            return AccessibilityManagerService.this.mContext.checkCallingPermission(permission) == 0;
        }

        private int getFocusedWindowId() {
            int i;
            try {
                IBinder token = AccessibilityManagerService.this.mWindowManagerService.getFocusedWindowToken();
                if (token != null) {
                    synchronized (AccessibilityManagerService.this.mLock) {
                        int windowId = getFocusedWindowIdLocked(token, AccessibilityManagerService.this.mGlobalWindowTokens);
                        if (windowId < 0) {
                            windowId = getFocusedWindowIdLocked(token, AccessibilityManagerService.this.getCurrentUserStateLocked().mWindowTokens);
                        }
                        i = windowId;
                    }
                    return i;
                }
                return -1;
            } catch (RemoteException e) {
                return -1;
            }
        }

        private int getFocusedWindowIdLocked(IBinder token, SparseArray<IBinder> windows) {
            int windowCount = windows.size();
            for (int i = 0; i < windowCount; i++) {
                if (windows.valueAt(i) == token) {
                    return windows.keyAt(i);
                }
            }
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AccessibilityManagerService$UserState.class */
    public class UserState {
        public final int mUserId;
        public boolean mIsAccessibilityEnabled;
        public boolean mIsTouchExplorationEnabled;
        public boolean mIsEnhancedWebAccessibilityEnabled;
        public boolean mIsDisplayMagnificationEnabled;
        public boolean mIsFilterKeyEventsEnabled;
        private Service mUiAutomationService;
        private IAccessibilityServiceClient mUiAutomationServiceClient;
        private IBinder mUiAutomationServiceOwner;
        public final RemoteCallbackList<IAccessibilityManagerClient> mClients = new RemoteCallbackList<>();
        public final SparseArray<AccessibilityConnectionWrapper> mInteractionConnections = new SparseArray<>();
        public final SparseArray<IBinder> mWindowTokens = new SparseArray<>();
        public final CopyOnWriteArrayList<Service> mBoundServices = new CopyOnWriteArrayList<>();
        public final Map<ComponentName, Service> mComponentNameToServiceMap = new HashMap();
        public final List<AccessibilityServiceInfo> mInstalledServices = new ArrayList();
        public final Set<ComponentName> mBindingServices = new HashSet();
        public final Set<ComponentName> mEnabledServices = new HashSet();
        public final Set<ComponentName> mTouchExplorationGrantedServices = new HashSet();
        public int mHandledFeedbackTypes = 0;
        public int mLastSentClientState = -1;
        private final IBinder.DeathRecipient mUiAutomationSerivceOnwerDeathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.accessibility.AccessibilityManagerService.UserState.1
            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                UserState.this.mUiAutomationServiceOwner.unlinkToDeath(UserState.this.mUiAutomationSerivceOnwerDeathRecipient, 0);
                UserState.this.mUiAutomationServiceOwner = null;
                if (UserState.this.mUiAutomationService != null) {
                    UserState.this.mUiAutomationService.binderDied();
                }
            }
        };

        public UserState(int userId) {
            this.mUserId = userId;
        }

        public int getClientState() {
            int clientState = 0;
            if (this.mIsAccessibilityEnabled) {
                clientState = 0 | 1;
            }
            if (this.mIsAccessibilityEnabled && this.mIsTouchExplorationEnabled) {
                clientState |= 2;
            }
            return clientState;
        }

        public void onSwitchToAnotherUser() {
            if (this.mUiAutomationService != null) {
                this.mUiAutomationService.binderDied();
            }
            AccessibilityManagerService.this.unbindAllServicesLocked(this);
            this.mBoundServices.clear();
            this.mBindingServices.clear();
            this.mHandledFeedbackTypes = 0;
            this.mLastSentClientState = -1;
            this.mEnabledServices.clear();
            this.mTouchExplorationGrantedServices.clear();
            this.mIsAccessibilityEnabled = false;
            this.mIsTouchExplorationEnabled = false;
            this.mIsEnhancedWebAccessibilityEnabled = false;
            this.mIsDisplayMagnificationEnabled = false;
        }

        public void destroyUiAutomationService() {
            this.mUiAutomationService = null;
            this.mUiAutomationServiceClient = null;
            if (this.mUiAutomationServiceOwner != null) {
                this.mUiAutomationServiceOwner.unlinkToDeath(this.mUiAutomationSerivceOnwerDeathRecipient, 0);
                this.mUiAutomationServiceOwner = null;
            }
        }
    }

    /* loaded from: AccessibilityManagerService$AccessibilityContentObserver.class */
    private final class AccessibilityContentObserver extends ContentObserver {
        private final Uri mAccessibilityEnabledUri;
        private final Uri mTouchExplorationEnabledUri;
        private final Uri mDisplayMagnificationEnabledUri;
        private final Uri mEnabledAccessibilityServicesUri;
        private final Uri mTouchExplorationGrantedAccessibilityServicesUri;
        private final Uri mEnhancedWebAccessibilityUri;

        public AccessibilityContentObserver(Handler handler) {
            super(handler);
            this.mAccessibilityEnabledUri = Settings.Secure.getUriFor(Settings.Secure.ACCESSIBILITY_ENABLED);
            this.mTouchExplorationEnabledUri = Settings.Secure.getUriFor(Settings.Secure.TOUCH_EXPLORATION_ENABLED);
            this.mDisplayMagnificationEnabledUri = Settings.Secure.getUriFor(Settings.Secure.ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED);
            this.mEnabledAccessibilityServicesUri = Settings.Secure.getUriFor(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            this.mTouchExplorationGrantedAccessibilityServicesUri = Settings.Secure.getUriFor(Settings.Secure.TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES);
            this.mEnhancedWebAccessibilityUri = Settings.Secure.getUriFor(Settings.Secure.ACCESSIBILITY_SCRIPT_INJECTION);
        }

        public void register(ContentResolver contentResolver) {
            contentResolver.registerContentObserver(this.mAccessibilityEnabledUri, false, this, -1);
            contentResolver.registerContentObserver(this.mTouchExplorationEnabledUri, false, this, -1);
            contentResolver.registerContentObserver(this.mDisplayMagnificationEnabledUri, false, this, -1);
            contentResolver.registerContentObserver(this.mEnabledAccessibilityServicesUri, false, this, -1);
            contentResolver.registerContentObserver(this.mTouchExplorationGrantedAccessibilityServicesUri, false, this, -1);
            contentResolver.registerContentObserver(this.mEnhancedWebAccessibilityUri, false, this, -1);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (this.mAccessibilityEnabledUri.equals(uri)) {
                synchronized (AccessibilityManagerService.this.mLock) {
                    UserState userState = AccessibilityManagerService.this.getCurrentUserStateLocked();
                    if (userState.mUiAutomationService == null && AccessibilityManagerService.this.readAccessibilityEnabledSettingLocked(userState)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(userState);
                    }
                }
            } else if (this.mTouchExplorationEnabledUri.equals(uri)) {
                synchronized (AccessibilityManagerService.this.mLock) {
                    UserState userState2 = AccessibilityManagerService.this.getCurrentUserStateLocked();
                    if (userState2.mUiAutomationService == null && AccessibilityManagerService.this.readTouchExplorationEnabledSettingLocked(userState2)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(userState2);
                    }
                }
            } else if (this.mDisplayMagnificationEnabledUri.equals(uri)) {
                synchronized (AccessibilityManagerService.this.mLock) {
                    UserState userState3 = AccessibilityManagerService.this.getCurrentUserStateLocked();
                    if (userState3.mUiAutomationService == null && AccessibilityManagerService.this.readDisplayMagnificationEnabledSettingLocked(userState3)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(userState3);
                    }
                }
            } else if (this.mEnabledAccessibilityServicesUri.equals(uri)) {
                synchronized (AccessibilityManagerService.this.mLock) {
                    UserState userState4 = AccessibilityManagerService.this.getCurrentUserStateLocked();
                    if (userState4.mUiAutomationService == null && AccessibilityManagerService.this.readEnabledAccessibilityServicesLocked(userState4)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(userState4);
                    }
                }
            } else if (this.mTouchExplorationGrantedAccessibilityServicesUri.equals(uri)) {
                synchronized (AccessibilityManagerService.this.mLock) {
                    UserState userState5 = AccessibilityManagerService.this.getCurrentUserStateLocked();
                    if (userState5.mUiAutomationService == null && AccessibilityManagerService.this.readTouchExplorationGrantedAccessibilityServicesLocked(userState5)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(userState5);
                    }
                }
            } else if (this.mEnhancedWebAccessibilityUri.equals(uri)) {
                synchronized (AccessibilityManagerService.this.mLock) {
                    UserState userState6 = AccessibilityManagerService.this.getCurrentUserStateLocked();
                    if (userState6.mUiAutomationService == null && AccessibilityManagerService.this.readEnhancedWebAccessibilityEnabledChangedLocked(userState6)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(userState6);
                    }
                }
            }
        }
    }
}