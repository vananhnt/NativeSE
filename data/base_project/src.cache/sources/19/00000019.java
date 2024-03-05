package android.accessibilityservice;

import android.accessibilityservice.IAccessibilityServiceClient;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.os.HandlerCaller;

/* loaded from: AccessibilityService.class */
public abstract class AccessibilityService extends Service {
    public static final int GESTURE_SWIPE_UP = 1;
    public static final int GESTURE_SWIPE_DOWN = 2;
    public static final int GESTURE_SWIPE_LEFT = 3;
    public static final int GESTURE_SWIPE_RIGHT = 4;
    public static final int GESTURE_SWIPE_LEFT_AND_RIGHT = 5;
    public static final int GESTURE_SWIPE_RIGHT_AND_LEFT = 6;
    public static final int GESTURE_SWIPE_UP_AND_DOWN = 7;
    public static final int GESTURE_SWIPE_DOWN_AND_UP = 8;
    public static final int GESTURE_SWIPE_LEFT_AND_UP = 9;
    public static final int GESTURE_SWIPE_LEFT_AND_DOWN = 10;
    public static final int GESTURE_SWIPE_RIGHT_AND_UP = 11;
    public static final int GESTURE_SWIPE_RIGHT_AND_DOWN = 12;
    public static final int GESTURE_SWIPE_UP_AND_LEFT = 13;
    public static final int GESTURE_SWIPE_UP_AND_RIGHT = 14;
    public static final int GESTURE_SWIPE_DOWN_AND_LEFT = 15;
    public static final int GESTURE_SWIPE_DOWN_AND_RIGHT = 16;
    public static final String SERVICE_INTERFACE = "android.accessibilityservice.AccessibilityService";
    public static final String SERVICE_META_DATA = "android.accessibilityservice";
    public static final int GLOBAL_ACTION_BACK = 1;
    public static final int GLOBAL_ACTION_HOME = 2;
    public static final int GLOBAL_ACTION_RECENTS = 3;
    public static final int GLOBAL_ACTION_NOTIFICATIONS = 4;
    public static final int GLOBAL_ACTION_QUICK_SETTINGS = 5;
    private static final String LOG_TAG = "AccessibilityService";
    private int mConnectionId;
    private AccessibilityServiceInfo mInfo;

    /* loaded from: AccessibilityService$Callbacks.class */
    public interface Callbacks {
        void onAccessibilityEvent(AccessibilityEvent accessibilityEvent);

        void onInterrupt();

        void onServiceConnected();

        void onSetConnectionId(int i);

        boolean onGesture(int i);

        boolean onKeyEvent(KeyEvent keyEvent);
    }

    public abstract void onAccessibilityEvent(AccessibilityEvent accessibilityEvent);

    public abstract void onInterrupt();

    protected void onServiceConnected() {
    }

    protected boolean onGesture(int gestureId) {
        return false;
    }

    protected boolean onKeyEvent(KeyEvent event) {
        return false;
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        return AccessibilityInteractionClient.getInstance().getRootInActiveWindow(this.mConnectionId);
    }

    public final boolean performGlobalAction(int action) {
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                return connection.performGlobalAction(action);
            } catch (RemoteException re) {
                Log.w(LOG_TAG, "Error while calling performGlobalAction", re);
                return false;
            }
        }
        return false;
    }

    public final AccessibilityServiceInfo getServiceInfo() {
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                return connection.getServiceInfo();
            } catch (RemoteException re) {
                Log.w(LOG_TAG, "Error while getting AccessibilityServiceInfo", re);
                return null;
            }
        }
        return null;
    }

    public final void setServiceInfo(AccessibilityServiceInfo info) {
        this.mInfo = info;
        sendServiceInfo();
    }

    private void sendServiceInfo() {
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
        if (this.mInfo != null && connection != null) {
            try {
                connection.setServiceInfo(this.mInfo);
                this.mInfo = null;
                AccessibilityInteractionClient.getInstance().clearCache();
            } catch (RemoteException re) {
                Log.w(LOG_TAG, "Error while setting AccessibilityServiceInfo", re);
            }
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new IAccessibilityServiceClientWrapper(this, getMainLooper(), new Callbacks() { // from class: android.accessibilityservice.AccessibilityService.1
            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onServiceConnected() {
                AccessibilityService.this.onServiceConnected();
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onInterrupt() {
                AccessibilityService.this.onInterrupt();
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onAccessibilityEvent(AccessibilityEvent event) {
                AccessibilityService.this.onAccessibilityEvent(event);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onSetConnectionId(int connectionId) {
                AccessibilityService.this.mConnectionId = connectionId;
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public boolean onGesture(int gestureId) {
                return AccessibilityService.this.onGesture(gestureId);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public boolean onKeyEvent(KeyEvent event) {
                return AccessibilityService.this.onKeyEvent(event);
            }
        });
    }

    /* loaded from: AccessibilityService$IAccessibilityServiceClientWrapper.class */
    public static class IAccessibilityServiceClientWrapper extends IAccessibilityServiceClient.Stub implements HandlerCaller.Callback {
        static final int NO_ID = -1;
        private static final int DO_SET_SET_CONNECTION = 10;
        private static final int DO_ON_INTERRUPT = 20;
        private static final int DO_ON_ACCESSIBILITY_EVENT = 30;
        private static final int DO_ON_GESTURE = 40;
        private static final int DO_CLEAR_ACCESSIBILITY_NODE_INFO_CACHE = 50;
        private static final int DO_ON_KEY_EVENT = 60;
        private final HandlerCaller mCaller;
        private final Callbacks mCallback;
        private int mConnectionId;

        public IAccessibilityServiceClientWrapper(Context context, Looper looper, Callbacks callback) {
            this.mCallback = callback;
            this.mCaller = new HandlerCaller(context, looper, this, true);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void setConnection(IAccessibilityServiceConnection connection, int connectionId) {
            Message message = this.mCaller.obtainMessageIO(10, connectionId, connection);
            this.mCaller.sendMessage(message);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onInterrupt() {
            Message message = this.mCaller.obtainMessage(20);
            this.mCaller.sendMessage(message);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onAccessibilityEvent(AccessibilityEvent event) {
            Message message = this.mCaller.obtainMessageO(30, event);
            this.mCaller.sendMessage(message);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onGesture(int gestureId) {
            Message message = this.mCaller.obtainMessageI(40, gestureId);
            this.mCaller.sendMessage(message);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void clearAccessibilityNodeInfoCache() {
            Message message = this.mCaller.obtainMessage(50);
            this.mCaller.sendMessage(message);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onKeyEvent(KeyEvent event, int sequence) {
            Message message = this.mCaller.obtainMessageIO(60, sequence, event);
            this.mCaller.sendMessage(message);
        }

        @Override // com.android.internal.os.HandlerCaller.Callback
        public void executeMessage(Message message) {
            switch (message.what) {
                case 10:
                    this.mConnectionId = message.arg1;
                    IAccessibilityServiceConnection connection = (IAccessibilityServiceConnection) message.obj;
                    if (connection != null) {
                        AccessibilityInteractionClient.getInstance().addConnection(this.mConnectionId, connection);
                        this.mCallback.onSetConnectionId(this.mConnectionId);
                        this.mCallback.onServiceConnected();
                        return;
                    }
                    AccessibilityInteractionClient.getInstance().removeConnection(this.mConnectionId);
                    AccessibilityInteractionClient.getInstance().clearCache();
                    this.mCallback.onSetConnectionId(-1);
                    return;
                case 20:
                    this.mCallback.onInterrupt();
                    return;
                case 30:
                    AccessibilityEvent event = (AccessibilityEvent) message.obj;
                    if (event != null) {
                        AccessibilityInteractionClient.getInstance().onAccessibilityEvent(event);
                        this.mCallback.onAccessibilityEvent(event);
                        event.recycle();
                        return;
                    }
                    return;
                case 40:
                    int gestureId = message.arg1;
                    this.mCallback.onGesture(gestureId);
                    return;
                case 50:
                    AccessibilityInteractionClient.getInstance().clearCache();
                    return;
                case 60:
                    KeyEvent event2 = (KeyEvent) message.obj;
                    try {
                        IAccessibilityServiceConnection connection2 = AccessibilityInteractionClient.getInstance().getConnection(this.mConnectionId);
                        if (connection2 != null) {
                            boolean result = this.mCallback.onKeyEvent(event2);
                            int sequence = message.arg1;
                            try {
                                connection2.setOnKeyEventResult(result, sequence);
                            } catch (RemoteException e) {
                            }
                        }
                        return;
                    } finally {
                        event2.recycle();
                    }
                default:
                    Log.w(AccessibilityService.LOG_TAG, "Unknown message type " + message.what);
                    return;
            }
        }
    }
}