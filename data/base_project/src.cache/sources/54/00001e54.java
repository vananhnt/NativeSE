package com.android.server.display;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplaySessionInfo;
import android.hardware.display.WifiDisplayStatus;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Slog;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.internal.R;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;
import com.android.server.display.WifiDisplayController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import libcore.util.Objects;

/* loaded from: WifiDisplayAdapter.class */
final class WifiDisplayAdapter extends DisplayAdapter {
    private static final String TAG = "WifiDisplayAdapter";
    private static final boolean DEBUG = false;
    private static final int MSG_SEND_STATUS_CHANGE_BROADCAST = 1;
    private static final int MSG_UPDATE_NOTIFICATION = 2;
    private static final String ACTION_DISCONNECT = "android.server.display.wfd.DISCONNECT";
    private final WifiDisplayHandler mHandler;
    private final PersistentDataStore mPersistentDataStore;
    private final boolean mSupportsProtectedBuffers;
    private final NotificationManager mNotificationManager;
    private PendingIntent mSettingsPendingIntent;
    private PendingIntent mDisconnectPendingIntent;
    private WifiDisplayController mDisplayController;
    private WifiDisplayDevice mDisplayDevice;
    private WifiDisplayStatus mCurrentStatus;
    private int mFeatureState;
    private int mScanState;
    private int mActiveDisplayState;
    private WifiDisplay mActiveDisplay;
    private WifiDisplay[] mDisplays;
    private WifiDisplay[] mAvailableDisplays;
    private WifiDisplay[] mRememberedDisplays;
    private WifiDisplaySessionInfo mSessionInfo;
    private boolean mPendingStatusChangeBroadcast;
    private boolean mPendingNotificationUpdate;
    private final BroadcastReceiver mBroadcastReceiver;
    private final WifiDisplayController.Listener mWifiDisplayListener;

    public WifiDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener, PersistentDataStore persistentDataStore) {
        super(syncRoot, context, handler, listener, TAG);
        this.mDisplays = WifiDisplay.EMPTY_ARRAY;
        this.mAvailableDisplays = WifiDisplay.EMPTY_ARRAY;
        this.mRememberedDisplays = WifiDisplay.EMPTY_ARRAY;
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.display.WifiDisplayAdapter.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals(WifiDisplayAdapter.ACTION_DISCONNECT)) {
                    synchronized (WifiDisplayAdapter.this.getSyncRoot()) {
                        WifiDisplayAdapter.this.requestDisconnectLocked();
                    }
                }
            }
        };
        this.mWifiDisplayListener = new WifiDisplayController.Listener() { // from class: com.android.server.display.WifiDisplayAdapter.8
            @Override // com.android.server.display.WifiDisplayController.Listener
            public void onFeatureStateChanged(int featureState) {
                synchronized (WifiDisplayAdapter.this.getSyncRoot()) {
                    if (WifiDisplayAdapter.this.mFeatureState != featureState) {
                        WifiDisplayAdapter.this.mFeatureState = featureState;
                        WifiDisplayAdapter.this.scheduleStatusChangedBroadcastLocked();
                    }
                }
            }

            @Override // com.android.server.display.WifiDisplayController.Listener
            public void onScanStarted() {
                synchronized (WifiDisplayAdapter.this.getSyncRoot()) {
                    if (WifiDisplayAdapter.this.mScanState != 1) {
                        WifiDisplayAdapter.this.mScanState = 1;
                        WifiDisplayAdapter.this.scheduleStatusChangedBroadcastLocked();
                    }
                }
            }

            @Override // com.android.server.display.WifiDisplayController.Listener
            public void onScanFinished(WifiDisplay[] availableDisplays) {
                synchronized (WifiDisplayAdapter.this.getSyncRoot()) {
                    WifiDisplay[] availableDisplays2 = WifiDisplayAdapter.this.mPersistentDataStore.applyWifiDisplayAliases(availableDisplays);
                    boolean changed = !Arrays.equals(WifiDisplayAdapter.this.mAvailableDisplays, availableDisplays2);
                    for (int i = 0; !changed && i < availableDisplays2.length; i++) {
                        changed = availableDisplays2[i].canConnect() != WifiDisplayAdapter.this.mAvailableDisplays[i].canConnect();
                    }
                    if (WifiDisplayAdapter.this.mScanState != 0 || changed) {
                        WifiDisplayAdapter.this.mScanState = 0;
                        WifiDisplayAdapter.this.mAvailableDisplays = availableDisplays2;
                        WifiDisplayAdapter.this.fixRememberedDisplayNamesFromAvailableDisplaysLocked();
                        WifiDisplayAdapter.this.updateDisplaysLocked();
                        WifiDisplayAdapter.this.scheduleStatusChangedBroadcastLocked();
                    }
                }
            }

            @Override // com.android.server.display.WifiDisplayController.Listener
            public void onDisplayConnecting(WifiDisplay display) {
                synchronized (WifiDisplayAdapter.this.getSyncRoot()) {
                    WifiDisplay display2 = WifiDisplayAdapter.this.mPersistentDataStore.applyWifiDisplayAlias(display);
                    if (WifiDisplayAdapter.this.mActiveDisplayState != 1 || WifiDisplayAdapter.this.mActiveDisplay == null || !WifiDisplayAdapter.this.mActiveDisplay.equals(display2)) {
                        WifiDisplayAdapter.this.mActiveDisplayState = 1;
                        WifiDisplayAdapter.this.mActiveDisplay = display2;
                        WifiDisplayAdapter.this.scheduleStatusChangedBroadcastLocked();
                    }
                }
            }

            @Override // com.android.server.display.WifiDisplayController.Listener
            public void onDisplayConnectionFailed() {
                synchronized (WifiDisplayAdapter.this.getSyncRoot()) {
                    if (WifiDisplayAdapter.this.mActiveDisplayState != 0 || WifiDisplayAdapter.this.mActiveDisplay != null) {
                        WifiDisplayAdapter.this.mActiveDisplayState = 0;
                        WifiDisplayAdapter.this.mActiveDisplay = null;
                        WifiDisplayAdapter.this.scheduleStatusChangedBroadcastLocked();
                    }
                }
            }

            @Override // com.android.server.display.WifiDisplayController.Listener
            public void onDisplayConnected(WifiDisplay display, Surface surface, int width, int height, int flags) {
                synchronized (WifiDisplayAdapter.this.getSyncRoot()) {
                    WifiDisplay display2 = WifiDisplayAdapter.this.mPersistentDataStore.applyWifiDisplayAlias(display);
                    WifiDisplayAdapter.this.addDisplayDeviceLocked(display2, surface, width, height, flags);
                    if (WifiDisplayAdapter.this.mActiveDisplayState != 2 || WifiDisplayAdapter.this.mActiveDisplay == null || !WifiDisplayAdapter.this.mActiveDisplay.equals(display2)) {
                        WifiDisplayAdapter.this.mActiveDisplayState = 2;
                        WifiDisplayAdapter.this.mActiveDisplay = display2;
                        WifiDisplayAdapter.this.scheduleStatusChangedBroadcastLocked();
                    }
                }
            }

            @Override // com.android.server.display.WifiDisplayController.Listener
            public void onDisplaySessionInfo(WifiDisplaySessionInfo sessionInfo) {
                synchronized (WifiDisplayAdapter.this.getSyncRoot()) {
                    WifiDisplayAdapter.this.mSessionInfo = sessionInfo;
                    WifiDisplayAdapter.this.scheduleStatusChangedBroadcastLocked();
                }
            }

            @Override // com.android.server.display.WifiDisplayController.Listener
            public void onDisplayChanged(WifiDisplay display) {
                synchronized (WifiDisplayAdapter.this.getSyncRoot()) {
                    WifiDisplay display2 = WifiDisplayAdapter.this.mPersistentDataStore.applyWifiDisplayAlias(display);
                    if (WifiDisplayAdapter.this.mActiveDisplay != null && WifiDisplayAdapter.this.mActiveDisplay.hasSameAddress(display2) && !WifiDisplayAdapter.this.mActiveDisplay.equals(display2)) {
                        WifiDisplayAdapter.this.mActiveDisplay = display2;
                        WifiDisplayAdapter.this.renameDisplayDeviceLocked(display2.getFriendlyDisplayName());
                        WifiDisplayAdapter.this.scheduleStatusChangedBroadcastLocked();
                    }
                }
            }

            @Override // com.android.server.display.WifiDisplayController.Listener
            public void onDisplayDisconnected() {
                synchronized (WifiDisplayAdapter.this.getSyncRoot()) {
                    WifiDisplayAdapter.this.removeDisplayDeviceLocked();
                    if (WifiDisplayAdapter.this.mActiveDisplayState != 0 || WifiDisplayAdapter.this.mActiveDisplay != null) {
                        WifiDisplayAdapter.this.mActiveDisplayState = 0;
                        WifiDisplayAdapter.this.mActiveDisplay = null;
                        WifiDisplayAdapter.this.scheduleStatusChangedBroadcastLocked();
                    }
                }
            }
        };
        this.mHandler = new WifiDisplayHandler(handler.getLooper());
        this.mPersistentDataStore = persistentDataStore;
        this.mSupportsProtectedBuffers = context.getResources().getBoolean(R.bool.config_wifiDisplaySupportsProtectedBuffers);
        this.mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override // com.android.server.display.DisplayAdapter
    public void dumpLocked(PrintWriter pw) {
        super.dumpLocked(pw);
        pw.println("mCurrentStatus=" + getWifiDisplayStatusLocked());
        pw.println("mFeatureState=" + this.mFeatureState);
        pw.println("mScanState=" + this.mScanState);
        pw.println("mActiveDisplayState=" + this.mActiveDisplayState);
        pw.println("mActiveDisplay=" + this.mActiveDisplay);
        pw.println("mDisplays=" + Arrays.toString(this.mDisplays));
        pw.println("mAvailableDisplays=" + Arrays.toString(this.mAvailableDisplays));
        pw.println("mRememberedDisplays=" + Arrays.toString(this.mRememberedDisplays));
        pw.println("mPendingStatusChangeBroadcast=" + this.mPendingStatusChangeBroadcast);
        pw.println("mPendingNotificationUpdate=" + this.mPendingNotificationUpdate);
        pw.println("mSupportsProtectedBuffers=" + this.mSupportsProtectedBuffers);
        if (this.mDisplayController == null) {
            pw.println("mDisplayController=null");
            return;
        }
        pw.println("mDisplayController:");
        IndentingPrintWriter ipw = new IndentingPrintWriter(pw, "  ");
        ipw.increaseIndent();
        DumpUtils.dumpAsync(getHandler(), this.mDisplayController, ipw, 200L);
    }

    @Override // com.android.server.display.DisplayAdapter
    public void registerLocked() {
        super.registerLocked();
        updateRememberedDisplaysLocked();
        getHandler().post(new Runnable() { // from class: com.android.server.display.WifiDisplayAdapter.1
            @Override // java.lang.Runnable
            public void run() {
                WifiDisplayAdapter.this.mDisplayController = new WifiDisplayController(WifiDisplayAdapter.this.getContext(), WifiDisplayAdapter.this.getHandler(), WifiDisplayAdapter.this.mWifiDisplayListener);
                WifiDisplayAdapter.this.getContext().registerReceiverAsUser(WifiDisplayAdapter.this.mBroadcastReceiver, UserHandle.ALL, new IntentFilter(WifiDisplayAdapter.ACTION_DISCONNECT), null, WifiDisplayAdapter.this.mHandler);
            }
        });
    }

    public void requestScanLocked() {
        getHandler().post(new Runnable() { // from class: com.android.server.display.WifiDisplayAdapter.2
            @Override // java.lang.Runnable
            public void run() {
                if (WifiDisplayAdapter.this.mDisplayController != null) {
                    WifiDisplayAdapter.this.mDisplayController.requestScan();
                }
            }
        });
    }

    public void requestConnectLocked(final String address, boolean trusted) {
        if (!trusted) {
            synchronized (getSyncRoot()) {
                if (!isRememberedDisplayLocked(address)) {
                    Slog.w(TAG, "Ignoring request by an untrusted client to connect to an unknown wifi display: " + address);
                    return;
                }
            }
        }
        getHandler().post(new Runnable() { // from class: com.android.server.display.WifiDisplayAdapter.3
            @Override // java.lang.Runnable
            public void run() {
                if (WifiDisplayAdapter.this.mDisplayController != null) {
                    WifiDisplayAdapter.this.mDisplayController.requestConnect(address);
                }
            }
        });
    }

    private boolean isRememberedDisplayLocked(String address) {
        WifiDisplay[] arr$ = this.mRememberedDisplays;
        for (WifiDisplay display : arr$) {
            if (display.getDeviceAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    public void requestPauseLocked() {
        getHandler().post(new Runnable() { // from class: com.android.server.display.WifiDisplayAdapter.4
            @Override // java.lang.Runnable
            public void run() {
                if (WifiDisplayAdapter.this.mDisplayController != null) {
                    WifiDisplayAdapter.this.mDisplayController.requestPause();
                }
            }
        });
    }

    public void requestResumeLocked() {
        getHandler().post(new Runnable() { // from class: com.android.server.display.WifiDisplayAdapter.5
            @Override // java.lang.Runnable
            public void run() {
                if (WifiDisplayAdapter.this.mDisplayController != null) {
                    WifiDisplayAdapter.this.mDisplayController.requestResume();
                }
            }
        });
    }

    public void requestDisconnectLocked() {
        getHandler().post(new Runnable() { // from class: com.android.server.display.WifiDisplayAdapter.6
            @Override // java.lang.Runnable
            public void run() {
                if (WifiDisplayAdapter.this.mDisplayController != null) {
                    WifiDisplayAdapter.this.mDisplayController.requestDisconnect();
                }
            }
        });
    }

    public void requestRenameLocked(String address, String alias) {
        if (alias != null) {
            alias = alias.trim();
            if (alias.isEmpty() || alias.equals(address)) {
                alias = null;
            }
        }
        WifiDisplay display = this.mPersistentDataStore.getRememberedWifiDisplay(address);
        if (display != null && !Objects.equal(display.getDeviceAlias(), alias)) {
            if (this.mPersistentDataStore.rememberWifiDisplay(new WifiDisplay(address, display.getDeviceName(), alias, false, false, false))) {
                this.mPersistentDataStore.saveIfNeeded();
                updateRememberedDisplaysLocked();
                scheduleStatusChangedBroadcastLocked();
            }
        }
        if (this.mActiveDisplay != null && this.mActiveDisplay.getDeviceAddress().equals(address)) {
            renameDisplayDeviceLocked(this.mActiveDisplay.getFriendlyDisplayName());
        }
    }

    public void requestForgetLocked(String address) {
        if (this.mPersistentDataStore.forgetWifiDisplay(address)) {
            this.mPersistentDataStore.saveIfNeeded();
            updateRememberedDisplaysLocked();
            scheduleStatusChangedBroadcastLocked();
        }
        if (this.mActiveDisplay != null && this.mActiveDisplay.getDeviceAddress().equals(address)) {
            requestDisconnectLocked();
        }
    }

    public WifiDisplayStatus getWifiDisplayStatusLocked() {
        if (this.mCurrentStatus == null) {
            this.mCurrentStatus = new WifiDisplayStatus(this.mFeatureState, this.mScanState, this.mActiveDisplayState, this.mActiveDisplay, this.mDisplays, this.mSessionInfo);
        }
        return this.mCurrentStatus;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDisplaysLocked() {
        List<WifiDisplay> displays = new ArrayList<>(this.mAvailableDisplays.length + this.mRememberedDisplays.length);
        boolean[] remembered = new boolean[this.mAvailableDisplays.length];
        WifiDisplay[] arr$ = this.mRememberedDisplays;
        for (WifiDisplay d : arr$) {
            boolean available = false;
            int i = 0;
            while (true) {
                if (i < this.mAvailableDisplays.length) {
                    if (!d.equals(this.mAvailableDisplays[i])) {
                        i++;
                    } else {
                        available = true;
                        remembered[i] = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!available) {
                displays.add(new WifiDisplay(d.getDeviceAddress(), d.getDeviceName(), d.getDeviceAlias(), false, false, true));
            }
        }
        for (int i2 = 0; i2 < this.mAvailableDisplays.length; i2++) {
            WifiDisplay d2 = this.mAvailableDisplays[i2];
            displays.add(new WifiDisplay(d2.getDeviceAddress(), d2.getDeviceName(), d2.getDeviceAlias(), true, d2.canConnect(), remembered[i2]));
        }
        this.mDisplays = (WifiDisplay[]) displays.toArray(WifiDisplay.EMPTY_ARRAY);
    }

    private void updateRememberedDisplaysLocked() {
        this.mRememberedDisplays = this.mPersistentDataStore.getRememberedWifiDisplays();
        this.mActiveDisplay = this.mPersistentDataStore.applyWifiDisplayAlias(this.mActiveDisplay);
        this.mAvailableDisplays = this.mPersistentDataStore.applyWifiDisplayAliases(this.mAvailableDisplays);
        updateDisplaysLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fixRememberedDisplayNamesFromAvailableDisplaysLocked() {
        boolean changed = false;
        for (int i = 0; i < this.mRememberedDisplays.length; i++) {
            WifiDisplay rememberedDisplay = this.mRememberedDisplays[i];
            WifiDisplay availableDisplay = findAvailableDisplayLocked(rememberedDisplay.getDeviceAddress());
            if (availableDisplay != null && !rememberedDisplay.equals(availableDisplay)) {
                this.mRememberedDisplays[i] = availableDisplay;
                changed |= this.mPersistentDataStore.rememberWifiDisplay(availableDisplay);
            }
        }
        if (changed) {
            this.mPersistentDataStore.saveIfNeeded();
        }
    }

    private WifiDisplay findAvailableDisplayLocked(String address) {
        WifiDisplay[] arr$ = this.mAvailableDisplays;
        for (WifiDisplay display : arr$) {
            if (display.getDeviceAddress().equals(address)) {
                return display;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addDisplayDeviceLocked(WifiDisplay display, Surface surface, int width, int height, int flags) {
        removeDisplayDeviceLocked();
        if (this.mPersistentDataStore.rememberWifiDisplay(display)) {
            this.mPersistentDataStore.saveIfNeeded();
            updateRememberedDisplaysLocked();
            scheduleStatusChangedBroadcastLocked();
        }
        boolean secure = (flags & 1) != 0;
        int deviceFlags = 64;
        if (secure) {
            deviceFlags = 64 | 4;
            if (this.mSupportsProtectedBuffers) {
                deviceFlags |= 8;
            }
        }
        String name = display.getFriendlyDisplayName();
        String address = display.getDeviceAddress();
        IBinder displayToken = SurfaceControl.createDisplay(name, secure);
        this.mDisplayDevice = new WifiDisplayDevice(displayToken, name, width, height, 60.0f, deviceFlags, address, surface);
        sendDisplayDeviceEventLocked(this.mDisplayDevice, 1);
        scheduleUpdateNotificationLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeDisplayDeviceLocked() {
        if (this.mDisplayDevice != null) {
            this.mDisplayDevice.destroyLocked();
            sendDisplayDeviceEventLocked(this.mDisplayDevice, 3);
            this.mDisplayDevice = null;
            scheduleUpdateNotificationLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renameDisplayDeviceLocked(String name) {
        if (this.mDisplayDevice != null && !this.mDisplayDevice.getNameLocked().equals(name)) {
            this.mDisplayDevice.setNameLocked(name);
            sendDisplayDeviceEventLocked(this.mDisplayDevice, 2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleStatusChangedBroadcastLocked() {
        this.mCurrentStatus = null;
        if (!this.mPendingStatusChangeBroadcast) {
            this.mPendingStatusChangeBroadcast = true;
            this.mHandler.sendEmptyMessage(1);
        }
    }

    private void scheduleUpdateNotificationLocked() {
        if (!this.mPendingNotificationUpdate) {
            this.mPendingNotificationUpdate = true;
            this.mHandler.sendEmptyMessage(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSendStatusChangeBroadcast() {
        synchronized (getSyncRoot()) {
            if (this.mPendingStatusChangeBroadcast) {
                this.mPendingStatusChangeBroadcast = false;
                Intent intent = new Intent(DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED);
                intent.addFlags(1073741824);
                intent.putExtra(DisplayManager.EXTRA_WIFI_DISPLAY_STATUS, getWifiDisplayStatusLocked());
                getContext().sendBroadcastAsUser(intent, UserHandle.ALL);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUpdateNotification() {
        synchronized (getSyncRoot()) {
            if (this.mPendingNotificationUpdate) {
                this.mPendingNotificationUpdate = false;
                boolean isConnected = this.mDisplayDevice != null;
                this.mNotificationManager.cancelAsUser(null, R.string.wifi_display_notification_title, UserHandle.ALL);
                if (isConnected) {
                    Context context = getContext();
                    if (this.mSettingsPendingIntent == null) {
                        Intent settingsIntent = new Intent(Settings.ACTION_WIFI_DISPLAY_SETTINGS);
                        settingsIntent.setFlags(337641472);
                        this.mSettingsPendingIntent = PendingIntent.getActivityAsUser(context, 0, settingsIntent, 0, null, UserHandle.CURRENT);
                    }
                    if (this.mDisconnectPendingIntent == null) {
                        Intent disconnectIntent = new Intent(ACTION_DISCONNECT);
                        this.mDisconnectPendingIntent = PendingIntent.getBroadcastAsUser(context, 0, disconnectIntent, 0, UserHandle.CURRENT);
                    }
                    Resources r = context.getResources();
                    Notification notification = new Notification.Builder(context).setContentTitle(r.getString(R.string.wifi_display_notification_title)).setContentText(r.getString(R.string.wifi_display_notification_message)).setContentIntent(this.mSettingsPendingIntent).setSmallIcon(R.drawable.ic_notify_wifidisplay).setOngoing(true).addAction(17301560, r.getString(R.string.wifi_display_notification_disconnect), this.mDisconnectPendingIntent).build();
                    this.mNotificationManager.notifyAsUser(null, R.string.wifi_display_notification_title, notification, UserHandle.ALL);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiDisplayAdapter$WifiDisplayDevice.class */
    public final class WifiDisplayDevice extends DisplayDevice {
        private String mName;
        private final int mWidth;
        private final int mHeight;
        private final float mRefreshRate;
        private final int mFlags;
        private final String mAddress;
        private Surface mSurface;
        private DisplayDeviceInfo mInfo;

        public WifiDisplayDevice(IBinder displayToken, String name, int width, int height, float refreshRate, int flags, String address, Surface surface) {
            super(WifiDisplayAdapter.this, displayToken);
            this.mName = name;
            this.mWidth = width;
            this.mHeight = height;
            this.mRefreshRate = refreshRate;
            this.mFlags = flags;
            this.mAddress = address;
            this.mSurface = surface;
        }

        public void destroyLocked() {
            if (this.mSurface != null) {
                this.mSurface.release();
                this.mSurface = null;
            }
            SurfaceControl.destroyDisplay(getDisplayTokenLocked());
        }

        public void setNameLocked(String name) {
            this.mName = name;
            this.mInfo = null;
        }

        @Override // com.android.server.display.DisplayDevice
        public void performTraversalInTransactionLocked() {
            if (this.mSurface != null) {
                setSurfaceInTransactionLocked(this.mSurface);
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public DisplayDeviceInfo getDisplayDeviceInfoLocked() {
            if (this.mInfo == null) {
                this.mInfo = new DisplayDeviceInfo();
                this.mInfo.name = this.mName;
                this.mInfo.width = this.mWidth;
                this.mInfo.height = this.mHeight;
                this.mInfo.refreshRate = this.mRefreshRate;
                this.mInfo.flags = this.mFlags;
                this.mInfo.type = 3;
                this.mInfo.address = this.mAddress;
                this.mInfo.touch = 2;
                this.mInfo.setAssumedDensityForExternalDisplay(this.mWidth, this.mHeight);
            }
            return this.mInfo;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiDisplayAdapter$WifiDisplayHandler.class */
    public final class WifiDisplayHandler extends Handler {
        public WifiDisplayHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    WifiDisplayAdapter.this.handleSendStatusChangeBroadcast();
                    return;
                case 2:
                    WifiDisplayAdapter.this.handleUpdateNotification();
                    return;
                default:
                    return;
            }
        }
    }
}