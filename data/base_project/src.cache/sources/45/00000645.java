package android.hardware.display;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.IDisplayManager;
import android.hardware.display.IDisplayManagerCallback;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayAdjustments;
import android.view.DisplayInfo;
import android.view.Surface;
import gov.nist.core.Separators;
import java.util.ArrayList;

/* loaded from: DisplayManagerGlobal.class */
public final class DisplayManagerGlobal {
    private static final String TAG = "DisplayManager";
    private static final boolean DEBUG = false;
    private static final boolean USE_CACHE = false;
    public static final int EVENT_DISPLAY_ADDED = 1;
    public static final int EVENT_DISPLAY_CHANGED = 2;
    public static final int EVENT_DISPLAY_REMOVED = 3;
    private static DisplayManagerGlobal sInstance;
    private final IDisplayManager mDm;
    private DisplayManagerCallback mCallback;
    private int[] mDisplayIdCache;
    private final Object mLock = new Object();
    private final ArrayList<DisplayListenerDelegate> mDisplayListeners = new ArrayList<>();
    private final SparseArray<DisplayInfo> mDisplayInfoCache = new SparseArray<>();

    private DisplayManagerGlobal(IDisplayManager dm) {
        this.mDm = dm;
    }

    public static DisplayManagerGlobal getInstance() {
        DisplayManagerGlobal displayManagerGlobal;
        IBinder b;
        synchronized (DisplayManagerGlobal.class) {
            if (sInstance == null && (b = ServiceManager.getService(Context.DISPLAY_SERVICE)) != null) {
                sInstance = new DisplayManagerGlobal(IDisplayManager.Stub.asInterface(b));
            }
            displayManagerGlobal = sInstance;
        }
        return displayManagerGlobal;
    }

    public DisplayInfo getDisplayInfo(int displayId) {
        try {
            synchronized (this.mLock) {
                DisplayInfo info = this.mDm.getDisplayInfo(displayId);
                if (info == null) {
                    return null;
                }
                registerCallbackIfNeededLocked();
                return info;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "Could not get display information from display manager.", ex);
            return null;
        }
    }

    public int[] getDisplayIds() {
        int[] displayIds;
        try {
            synchronized (this.mLock) {
                displayIds = this.mDm.getDisplayIds();
                registerCallbackIfNeededLocked();
            }
            return displayIds;
        } catch (RemoteException ex) {
            Log.e(TAG, "Could not get display ids from display manager.", ex);
            return new int[]{0};
        }
    }

    public Display getCompatibleDisplay(int displayId, DisplayAdjustments daj) {
        DisplayInfo displayInfo = getDisplayInfo(displayId);
        if (displayInfo == null) {
            return null;
        }
        return new Display(this, displayId, displayInfo, daj);
    }

    public Display getRealDisplay(int displayId) {
        return getCompatibleDisplay(displayId, DisplayAdjustments.DEFAULT_DISPLAY_ADJUSTMENTS);
    }

    public Display getRealDisplay(int displayId, IBinder token) {
        return getCompatibleDisplay(displayId, new DisplayAdjustments(token));
    }

    public void registerDisplayListener(DisplayManager.DisplayListener listener, Handler handler) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mLock) {
            int index = findDisplayListenerLocked(listener);
            if (index < 0) {
                this.mDisplayListeners.add(new DisplayListenerDelegate(listener, handler));
                registerCallbackIfNeededLocked();
            }
        }
    }

    public void unregisterDisplayListener(DisplayManager.DisplayListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mLock) {
            int index = findDisplayListenerLocked(listener);
            if (index >= 0) {
                DisplayListenerDelegate d = this.mDisplayListeners.get(index);
                d.clearEvents();
                this.mDisplayListeners.remove(index);
            }
        }
    }

    private int findDisplayListenerLocked(DisplayManager.DisplayListener listener) {
        int numListeners = this.mDisplayListeners.size();
        for (int i = 0; i < numListeners; i++) {
            if (this.mDisplayListeners.get(i).mListener == listener) {
                return i;
            }
        }
        return -1;
    }

    private void registerCallbackIfNeededLocked() {
        if (this.mCallback == null) {
            this.mCallback = new DisplayManagerCallback();
            try {
                this.mDm.registerCallback(this.mCallback);
            } catch (RemoteException ex) {
                Log.e(TAG, "Failed to register callback with display manager service.", ex);
                this.mCallback = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayEvent(int displayId, int event) {
        synchronized (this.mLock) {
            int numListeners = this.mDisplayListeners.size();
            for (int i = 0; i < numListeners; i++) {
                this.mDisplayListeners.get(i).sendDisplayEvent(displayId, event);
            }
        }
    }

    public void scanWifiDisplays() {
        try {
            this.mDm.scanWifiDisplays();
        } catch (RemoteException ex) {
            Log.e(TAG, "Failed to scan for Wifi displays.", ex);
        }
    }

    public void connectWifiDisplay(String deviceAddress) {
        if (deviceAddress == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        }
        try {
            this.mDm.connectWifiDisplay(deviceAddress);
        } catch (RemoteException ex) {
            Log.e(TAG, "Failed to connect to Wifi display " + deviceAddress + Separators.DOT, ex);
        }
    }

    public void pauseWifiDisplay() {
        try {
            this.mDm.pauseWifiDisplay();
        } catch (RemoteException ex) {
            Log.e(TAG, "Failed to pause Wifi display.", ex);
        }
    }

    public void resumeWifiDisplay() {
        try {
            this.mDm.resumeWifiDisplay();
        } catch (RemoteException ex) {
            Log.e(TAG, "Failed to resume Wifi display.", ex);
        }
    }

    public void disconnectWifiDisplay() {
        try {
            this.mDm.disconnectWifiDisplay();
        } catch (RemoteException ex) {
            Log.e(TAG, "Failed to disconnect from Wifi display.", ex);
        }
    }

    public void renameWifiDisplay(String deviceAddress, String alias) {
        if (deviceAddress == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        }
        try {
            this.mDm.renameWifiDisplay(deviceAddress, alias);
        } catch (RemoteException ex) {
            Log.e(TAG, "Failed to rename Wifi display " + deviceAddress + " with alias " + alias + Separators.DOT, ex);
        }
    }

    public void forgetWifiDisplay(String deviceAddress) {
        if (deviceAddress == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        }
        try {
            this.mDm.forgetWifiDisplay(deviceAddress);
        } catch (RemoteException ex) {
            Log.e(TAG, "Failed to forget Wifi display.", ex);
        }
    }

    public WifiDisplayStatus getWifiDisplayStatus() {
        try {
            return this.mDm.getWifiDisplayStatus();
        } catch (RemoteException ex) {
            Log.e(TAG, "Failed to get Wifi display status.", ex);
            return new WifiDisplayStatus();
        }
    }

    public VirtualDisplay createVirtualDisplay(Context context, String name, int width, int height, int densityDpi, Surface surface, int flags) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name must be non-null and non-empty");
        }
        if (width <= 0 || height <= 0 || densityDpi <= 0) {
            throw new IllegalArgumentException("width, height, and densityDpi must be greater than 0");
        }
        if (surface == null) {
            throw new IllegalArgumentException("surface must not be null");
        }
        Binder token = new Binder();
        try {
            int displayId = this.mDm.createVirtualDisplay(token, context.getPackageName(), name, width, height, densityDpi, surface, flags);
            if (displayId < 0) {
                Log.e(TAG, "Could not create virtual display: " + name);
                return null;
            }
            Display display = getRealDisplay(displayId);
            if (display == null) {
                Log.wtf(TAG, "Could not obtain display info for newly created virtual display: " + name);
                try {
                    this.mDm.releaseVirtualDisplay(token);
                    return null;
                } catch (RemoteException e) {
                    return null;
                }
            }
            return new VirtualDisplay(this, display, token);
        } catch (RemoteException ex) {
            Log.e(TAG, "Could not create virtual display: " + name, ex);
            return null;
        }
    }

    public void releaseVirtualDisplay(IBinder token) {
        try {
            this.mDm.releaseVirtualDisplay(token);
        } catch (RemoteException ex) {
            Log.w(TAG, "Failed to release virtual display.", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DisplayManagerGlobal$DisplayManagerCallback.class */
    public final class DisplayManagerCallback extends IDisplayManagerCallback.Stub {
        private DisplayManagerCallback() {
        }

        @Override // android.hardware.display.IDisplayManagerCallback
        public void onDisplayEvent(int displayId, int event) {
            DisplayManagerGlobal.this.handleDisplayEvent(displayId, event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DisplayManagerGlobal$DisplayListenerDelegate.class */
    public static final class DisplayListenerDelegate extends Handler {
        public final DisplayManager.DisplayListener mListener;

        public DisplayListenerDelegate(DisplayManager.DisplayListener listener, Handler handler) {
            super(handler != null ? handler.getLooper() : Looper.myLooper(), null, true);
            this.mListener = listener;
        }

        public void sendDisplayEvent(int displayId, int event) {
            Message msg = obtainMessage(event, displayId, 0);
            sendMessage(msg);
        }

        public void clearEvents() {
            removeCallbacksAndMessages(null);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    this.mListener.onDisplayAdded(msg.arg1);
                    return;
                case 2:
                    this.mListener.onDisplayChanged(msg.arg1);
                    return;
                case 3:
                    this.mListener.onDisplayRemoved(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }
}