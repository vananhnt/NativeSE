package com.android.server.display;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplaySessionInfo;
import android.media.RemoteDisplay;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pWfdInfo;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Slog;
import android.view.Surface;
import com.android.internal.util.DumpUtils;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import libcore.util.Objects;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WifiDisplayController.class */
public final class WifiDisplayController implements DumpUtils.Dump {
    private static final String TAG = "WifiDisplayController";
    private static final boolean DEBUG = false;
    private static final int DEFAULT_CONTROL_PORT = 7236;
    private static final int MAX_THROUGHPUT = 50;
    private static final int CONNECTION_TIMEOUT_SECONDS = 60;
    private static final int RTSP_TIMEOUT_SECONDS = 15;
    private static final int RTSP_TIMEOUT_SECONDS_CERT_MODE = 120;
    private static final int DISCOVER_PEERS_MAX_RETRIES = 10;
    private static final int DISCOVER_PEERS_RETRY_DELAY_MILLIS = 500;
    private static final int CONNECT_MAX_RETRIES = 3;
    private static final int CONNECT_RETRY_DELAY_MILLIS = 500;
    private final Context mContext;
    private final Handler mHandler;
    private final Listener mListener;
    private final WifiP2pManager mWifiP2pManager;
    private final WifiP2pManager.Channel mWifiP2pChannel;
    private boolean mWifiP2pEnabled;
    private boolean mWfdEnabled;
    private boolean mWfdEnabling;
    private NetworkInfo mNetworkInfo;
    private boolean mWifiDisplayOnSetting;
    private boolean mDiscoverPeersInProgress;
    private int mDiscoverPeersRetriesLeft;
    private WifiP2pDevice mDesiredDevice;
    private WifiP2pDevice mConnectingDevice;
    private WifiP2pDevice mDisconnectingDevice;
    private WifiP2pDevice mCancelingDevice;
    private WifiP2pDevice mConnectedDevice;
    private WifiP2pGroup mConnectedDeviceGroupInfo;
    private int mConnectionRetriesLeft;
    private RemoteDisplay mRemoteDisplay;
    private String mRemoteDisplayInterface;
    private boolean mRemoteDisplayConnected;
    private WifiDisplay mAdvertisedDisplay;
    private Surface mAdvertisedDisplaySurface;
    private int mAdvertisedDisplayWidth;
    private int mAdvertisedDisplayHeight;
    private int mAdvertisedDisplayFlags;
    private boolean mWifiDisplayCertMode;
    private WifiP2pDevice mThisDevice;
    private final ArrayList<WifiP2pDevice> mAvailableWifiDisplayPeers = new ArrayList<>();
    private int mWifiDisplayWpsConfig = 4;
    private final Runnable mConnectionTimeout = new Runnable() { // from class: com.android.server.display.WifiDisplayController.14
        @Override // java.lang.Runnable
        public void run() {
            if (WifiDisplayController.this.mConnectingDevice != null && WifiDisplayController.this.mConnectingDevice == WifiDisplayController.this.mDesiredDevice) {
                Slog.i(WifiDisplayController.TAG, "Timed out waiting for Wifi display connection after 60 seconds: " + WifiDisplayController.this.mConnectingDevice.deviceName);
                WifiDisplayController.this.handleConnectionFailure(true);
            }
        }
    };
    private final Runnable mRtspTimeout = new Runnable() { // from class: com.android.server.display.WifiDisplayController.15
        @Override // java.lang.Runnable
        public void run() {
            if (WifiDisplayController.this.mConnectedDevice != null && WifiDisplayController.this.mRemoteDisplay != null && !WifiDisplayController.this.mRemoteDisplayConnected) {
                Slog.i(WifiDisplayController.TAG, "Timed out waiting for Wifi display RTSP connection after 15 seconds: " + WifiDisplayController.this.mConnectedDevice.deviceName);
                WifiDisplayController.this.handleConnectionFailure(true);
            }
        }
    };
    private final BroadcastReceiver mWifiP2pReceiver = new BroadcastReceiver() { // from class: com.android.server.display.WifiDisplayController.18
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)) {
                boolean enabled = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, 1) == 2;
                WifiDisplayController.this.handleStateChanged(enabled);
            } else if (action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)) {
                WifiDisplayController.this.handlePeersChanged();
            } else if (action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                WifiDisplayController.this.handleConnectionChanged(networkInfo);
            } else if (action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)) {
                WifiDisplayController.this.mThisDevice = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            }
        }
    };

    /* loaded from: WifiDisplayController$Listener.class */
    public interface Listener {
        void onFeatureStateChanged(int i);

        void onScanStarted();

        void onScanFinished(WifiDisplay[] wifiDisplayArr);

        void onDisplayConnecting(WifiDisplay wifiDisplay);

        void onDisplayConnectionFailed();

        void onDisplayChanged(WifiDisplay wifiDisplay);

        void onDisplayConnected(WifiDisplay wifiDisplay, Surface surface, int i, int i2, int i3);

        void onDisplaySessionInfo(WifiDisplaySessionInfo wifiDisplaySessionInfo);

        void onDisplayDisconnected();
    }

    static /* synthetic */ int access$720(WifiDisplayController x0, int x1) {
        int i = x0.mDiscoverPeersRetriesLeft - x1;
        x0.mDiscoverPeersRetriesLeft = i;
        return i;
    }

    static /* synthetic */ int access$3220(WifiDisplayController x0, int x1) {
        int i = x0.mConnectionRetriesLeft - x1;
        x0.mConnectionRetriesLeft = i;
        return i;
    }

    public WifiDisplayController(Context context, Handler handler, Listener listener) {
        this.mContext = context;
        this.mHandler = handler;
        this.mListener = listener;
        this.mWifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.mWifiP2pChannel = this.mWifiP2pManager.initialize(context, handler.getLooper(), null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        context.registerReceiver(this.mWifiP2pReceiver, intentFilter, null, this.mHandler);
        ContentObserver settingsObserver = new ContentObserver(this.mHandler) { // from class: com.android.server.display.WifiDisplayController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                WifiDisplayController.this.updateSettings();
            }
        };
        ContentResolver resolver = this.mContext.getContentResolver();
        resolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.WIFI_DISPLAY_ON), false, settingsObserver);
        resolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.WIFI_DISPLAY_CERTIFICATION_ON), false, settingsObserver);
        resolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.WIFI_DISPLAY_WPS_CONFIG), false, settingsObserver);
        updateSettings();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSettings() {
        ContentResolver resolver = this.mContext.getContentResolver();
        this.mWifiDisplayOnSetting = Settings.Global.getInt(resolver, Settings.Global.WIFI_DISPLAY_ON, 0) != 0;
        this.mWifiDisplayCertMode = Settings.Global.getInt(resolver, Settings.Global.WIFI_DISPLAY_CERTIFICATION_ON, 0) != 0;
        this.mWifiDisplayWpsConfig = 4;
        if (this.mWifiDisplayCertMode) {
            this.mWifiDisplayWpsConfig = Settings.Global.getInt(resolver, Settings.Global.WIFI_DISPLAY_WPS_CONFIG, 4);
        }
        updateWfdEnableState();
    }

    @Override // com.android.internal.util.DumpUtils.Dump
    public void dump(PrintWriter pw) {
        pw.println("mWifiDisplayOnSetting=" + this.mWifiDisplayOnSetting);
        pw.println("mWifiP2pEnabled=" + this.mWifiP2pEnabled);
        pw.println("mWfdEnabled=" + this.mWfdEnabled);
        pw.println("mWfdEnabling=" + this.mWfdEnabling);
        pw.println("mNetworkInfo=" + this.mNetworkInfo);
        pw.println("mDiscoverPeersInProgress=" + this.mDiscoverPeersInProgress);
        pw.println("mDiscoverPeersRetriesLeft=" + this.mDiscoverPeersRetriesLeft);
        pw.println("mDesiredDevice=" + describeWifiP2pDevice(this.mDesiredDevice));
        pw.println("mConnectingDisplay=" + describeWifiP2pDevice(this.mConnectingDevice));
        pw.println("mDisconnectingDisplay=" + describeWifiP2pDevice(this.mDisconnectingDevice));
        pw.println("mCancelingDisplay=" + describeWifiP2pDevice(this.mCancelingDevice));
        pw.println("mConnectedDevice=" + describeWifiP2pDevice(this.mConnectedDevice));
        pw.println("mConnectionRetriesLeft=" + this.mConnectionRetriesLeft);
        pw.println("mRemoteDisplay=" + this.mRemoteDisplay);
        pw.println("mRemoteDisplayInterface=" + this.mRemoteDisplayInterface);
        pw.println("mRemoteDisplayConnected=" + this.mRemoteDisplayConnected);
        pw.println("mAdvertisedDisplay=" + this.mAdvertisedDisplay);
        pw.println("mAdvertisedDisplaySurface=" + this.mAdvertisedDisplaySurface);
        pw.println("mAdvertisedDisplayWidth=" + this.mAdvertisedDisplayWidth);
        pw.println("mAdvertisedDisplayHeight=" + this.mAdvertisedDisplayHeight);
        pw.println("mAdvertisedDisplayFlags=" + this.mAdvertisedDisplayFlags);
        pw.println("mAvailableWifiDisplayPeers: size=" + this.mAvailableWifiDisplayPeers.size());
        Iterator i$ = this.mAvailableWifiDisplayPeers.iterator();
        while (i$.hasNext()) {
            WifiP2pDevice device = i$.next();
            pw.println("  " + describeWifiP2pDevice(device));
        }
    }

    public void requestScan() {
        discoverPeers();
    }

    public void requestConnect(String address) {
        Iterator i$ = this.mAvailableWifiDisplayPeers.iterator();
        while (i$.hasNext()) {
            WifiP2pDevice device = i$.next();
            if (device.deviceAddress.equals(address)) {
                connect(device);
            }
        }
    }

    public void requestPause() {
        if (this.mRemoteDisplay != null) {
            this.mRemoteDisplay.pause();
        }
    }

    public void requestResume() {
        if (this.mRemoteDisplay != null) {
            this.mRemoteDisplay.resume();
        }
    }

    public void requestDisconnect() {
        disconnect();
    }

    private void updateWfdEnableState() {
        if (this.mWifiDisplayOnSetting && this.mWifiP2pEnabled) {
            if (!this.mWfdEnabled && !this.mWfdEnabling) {
                this.mWfdEnabling = true;
                WifiP2pWfdInfo wfdInfo = new WifiP2pWfdInfo();
                wfdInfo.setWfdEnabled(true);
                wfdInfo.setDeviceType(0);
                wfdInfo.setSessionAvailable(true);
                wfdInfo.setControlPort(DEFAULT_CONTROL_PORT);
                wfdInfo.setMaxThroughput(50);
                this.mWifiP2pManager.setWFDInfo(this.mWifiP2pChannel, wfdInfo, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.2
                    @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                    public void onSuccess() {
                        if (WifiDisplayController.this.mWfdEnabling) {
                            WifiDisplayController.this.mWfdEnabling = false;
                            WifiDisplayController.this.mWfdEnabled = true;
                            WifiDisplayController.this.reportFeatureState();
                        }
                    }

                    @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                    public void onFailure(int reason) {
                        WifiDisplayController.this.mWfdEnabling = false;
                    }
                });
                return;
            }
            return;
        }
        if (this.mWfdEnabled || this.mWfdEnabling) {
            WifiP2pWfdInfo wfdInfo2 = new WifiP2pWfdInfo();
            wfdInfo2.setWfdEnabled(false);
            this.mWifiP2pManager.setWFDInfo(this.mWifiP2pChannel, wfdInfo2, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.3
                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onSuccess() {
                }

                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onFailure(int reason) {
                }
            });
        }
        this.mWfdEnabling = false;
        this.mWfdEnabled = false;
        reportFeatureState();
        disconnect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportFeatureState() {
        final int featureState = computeFeatureState();
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.WifiDisplayController.4
            @Override // java.lang.Runnable
            public void run() {
                WifiDisplayController.this.mListener.onFeatureStateChanged(featureState);
            }
        });
    }

    private int computeFeatureState() {
        if (this.mWifiP2pEnabled) {
            return this.mWifiDisplayOnSetting ? 3 : 2;
        }
        return 1;
    }

    private void discoverPeers() {
        if (!this.mDiscoverPeersInProgress) {
            this.mDiscoverPeersInProgress = true;
            this.mDiscoverPeersRetriesLeft = 10;
            handleScanStarted();
            tryDiscoverPeers();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryDiscoverPeers() {
        this.mWifiP2pManager.discoverPeers(this.mWifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.5
            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onSuccess() {
                WifiDisplayController.this.mDiscoverPeersInProgress = false;
                WifiDisplayController.this.requestPeers();
            }

            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onFailure(int reason) {
                if (WifiDisplayController.this.mDiscoverPeersInProgress) {
                    if (reason != 0 || WifiDisplayController.this.mDiscoverPeersRetriesLeft <= 0 || !WifiDisplayController.this.mWfdEnabled) {
                        WifiDisplayController.this.handleScanFinished();
                        WifiDisplayController.this.mDiscoverPeersInProgress = false;
                        return;
                    }
                    WifiDisplayController.this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.display.WifiDisplayController.5.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (WifiDisplayController.this.mDiscoverPeersInProgress) {
                                if (WifiDisplayController.this.mDiscoverPeersRetriesLeft <= 0 || !WifiDisplayController.this.mWfdEnabled) {
                                    WifiDisplayController.this.handleScanFinished();
                                    WifiDisplayController.this.mDiscoverPeersInProgress = false;
                                    return;
                                }
                                WifiDisplayController.access$720(WifiDisplayController.this, 1);
                                WifiDisplayController.this.tryDiscoverPeers();
                            }
                        }
                    }, 500L);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestPeers() {
        this.mWifiP2pManager.requestPeers(this.mWifiP2pChannel, new WifiP2pManager.PeerListListener() { // from class: com.android.server.display.WifiDisplayController.6
            @Override // android.net.wifi.p2p.WifiP2pManager.PeerListListener
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                WifiDisplayController.this.mAvailableWifiDisplayPeers.clear();
                for (WifiP2pDevice device : peers.getDeviceList()) {
                    if (WifiDisplayController.isWifiDisplay(device)) {
                        WifiDisplayController.this.mAvailableWifiDisplayPeers.add(device);
                    }
                }
                WifiDisplayController.this.handleScanFinished();
            }
        });
    }

    private void handleScanStarted() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.WifiDisplayController.7
            @Override // java.lang.Runnable
            public void run() {
                WifiDisplayController.this.mListener.onScanStarted();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScanFinished() {
        int count = this.mAvailableWifiDisplayPeers.size();
        final WifiDisplay[] displays = WifiDisplay.CREATOR.newArray(count);
        for (int i = 0; i < count; i++) {
            WifiP2pDevice device = this.mAvailableWifiDisplayPeers.get(i);
            displays[i] = createWifiDisplay(device);
            updateDesiredDevice(device);
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.WifiDisplayController.8
            @Override // java.lang.Runnable
            public void run() {
                WifiDisplayController.this.mListener.onScanFinished(displays);
            }
        });
    }

    private void updateDesiredDevice(WifiP2pDevice device) {
        String address = device.deviceAddress;
        if (this.mDesiredDevice != null && this.mDesiredDevice.deviceAddress.equals(address)) {
            this.mDesiredDevice.update(device);
            if (this.mAdvertisedDisplay != null && this.mAdvertisedDisplay.getDeviceAddress().equals(address)) {
                readvertiseDisplay(createWifiDisplay(this.mDesiredDevice));
            }
        }
    }

    private void connect(WifiP2pDevice device) {
        if (this.mDesiredDevice != null && !this.mDesiredDevice.deviceAddress.equals(device.deviceAddress)) {
            return;
        }
        if (this.mConnectedDevice != null && !this.mConnectedDevice.deviceAddress.equals(device.deviceAddress) && this.mDesiredDevice == null) {
            return;
        }
        this.mDesiredDevice = device;
        this.mConnectionRetriesLeft = 3;
        updateConnection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disconnect() {
        this.mDesiredDevice = null;
        updateConnection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void retryConnection() {
        this.mDesiredDevice = new WifiP2pDevice(this.mDesiredDevice);
        updateConnection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConnection() {
        if (this.mRemoteDisplay != null && this.mConnectedDevice != this.mDesiredDevice) {
            Slog.i(TAG, "Stopped listening for RTSP connection on " + this.mRemoteDisplayInterface + " from Wifi display: " + this.mConnectedDevice.deviceName);
            this.mRemoteDisplay.dispose();
            this.mRemoteDisplay = null;
            this.mRemoteDisplayInterface = null;
            this.mRemoteDisplayConnected = false;
            this.mHandler.removeCallbacks(this.mRtspTimeout);
            this.mWifiP2pManager.setMiracastMode(0);
            unadvertiseDisplay();
        }
        if (this.mDisconnectingDevice != null) {
            return;
        }
        if (this.mConnectedDevice != null && this.mConnectedDevice != this.mDesiredDevice) {
            Slog.i(TAG, "Disconnecting from Wifi display: " + this.mConnectedDevice.deviceName);
            this.mDisconnectingDevice = this.mConnectedDevice;
            this.mConnectedDevice = null;
            this.mConnectedDeviceGroupInfo = null;
            unadvertiseDisplay();
            final WifiP2pDevice oldDevice = this.mDisconnectingDevice;
            this.mWifiP2pManager.removeGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.9
                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onSuccess() {
                    Slog.i(WifiDisplayController.TAG, "Disconnected from Wifi display: " + oldDevice.deviceName);
                    next();
                }

                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onFailure(int reason) {
                    Slog.i(WifiDisplayController.TAG, "Failed to disconnect from Wifi display: " + oldDevice.deviceName + ", reason=" + reason);
                    next();
                }

                private void next() {
                    if (WifiDisplayController.this.mDisconnectingDevice == oldDevice) {
                        WifiDisplayController.this.mDisconnectingDevice = null;
                        WifiDisplayController.this.updateConnection();
                    }
                }
            });
        } else if (this.mCancelingDevice != null) {
        } else {
            if (this.mConnectingDevice != null && this.mConnectingDevice != this.mDesiredDevice) {
                Slog.i(TAG, "Canceling connection to Wifi display: " + this.mConnectingDevice.deviceName);
                this.mCancelingDevice = this.mConnectingDevice;
                this.mConnectingDevice = null;
                unadvertiseDisplay();
                this.mHandler.removeCallbacks(this.mConnectionTimeout);
                final WifiP2pDevice oldDevice2 = this.mCancelingDevice;
                this.mWifiP2pManager.cancelConnect(this.mWifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.10
                    @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                    public void onSuccess() {
                        Slog.i(WifiDisplayController.TAG, "Canceled connection to Wifi display: " + oldDevice2.deviceName);
                        next();
                    }

                    @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                    public void onFailure(int reason) {
                        Slog.i(WifiDisplayController.TAG, "Failed to cancel connection to Wifi display: " + oldDevice2.deviceName + ", reason=" + reason);
                        next();
                    }

                    private void next() {
                        if (WifiDisplayController.this.mCancelingDevice == oldDevice2) {
                            WifiDisplayController.this.mCancelingDevice = null;
                            WifiDisplayController.this.updateConnection();
                        }
                    }
                });
            } else if (this.mDesiredDevice == null) {
                if (this.mWifiDisplayCertMode) {
                    this.mListener.onDisplaySessionInfo(getSessionInfo(this.mConnectedDeviceGroupInfo, 0));
                }
                unadvertiseDisplay();
            } else if (this.mConnectedDevice == null && this.mConnectingDevice == null) {
                Slog.i(TAG, "Connecting to Wifi display: " + this.mDesiredDevice.deviceName);
                this.mConnectingDevice = this.mDesiredDevice;
                WifiP2pConfig config = new WifiP2pConfig();
                WpsInfo wps = new WpsInfo();
                if (this.mWifiDisplayWpsConfig != 4) {
                    wps.setup = this.mWifiDisplayWpsConfig;
                } else if (this.mConnectingDevice.wpsPbcSupported()) {
                    wps.setup = 0;
                } else if (this.mConnectingDevice.wpsDisplaySupported()) {
                    wps.setup = 2;
                } else {
                    wps.setup = 1;
                }
                config.wps = wps;
                config.deviceAddress = this.mConnectingDevice.deviceAddress;
                config.groupOwnerIntent = 0;
                WifiDisplay display = createWifiDisplay(this.mConnectingDevice);
                advertiseDisplay(display, null, 0, 0, 0);
                final WifiP2pDevice newDevice = this.mDesiredDevice;
                this.mWifiP2pManager.connect(this.mWifiP2pChannel, config, new WifiP2pManager.ActionListener() { // from class: com.android.server.display.WifiDisplayController.11
                    @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                    public void onSuccess() {
                        Slog.i(WifiDisplayController.TAG, "Initiated connection to Wifi display: " + newDevice.deviceName);
                        WifiDisplayController.this.mHandler.postDelayed(WifiDisplayController.this.mConnectionTimeout, DateUtils.MINUTE_IN_MILLIS);
                    }

                    @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                    public void onFailure(int reason) {
                        if (WifiDisplayController.this.mConnectingDevice == newDevice) {
                            Slog.i(WifiDisplayController.TAG, "Failed to initiate connection to Wifi display: " + newDevice.deviceName + ", reason=" + reason);
                            WifiDisplayController.this.mConnectingDevice = null;
                            WifiDisplayController.this.handleConnectionFailure(false);
                        }
                    }
                });
            } else if (this.mConnectedDevice != null && this.mRemoteDisplay == null) {
                Inet4Address addr = getInterfaceAddress(this.mConnectedDeviceGroupInfo);
                if (addr == null) {
                    Slog.i(TAG, "Failed to get local interface address for communicating with Wifi display: " + this.mConnectedDevice.deviceName);
                    handleConnectionFailure(false);
                    return;
                }
                this.mWifiP2pManager.setMiracastMode(1);
                final WifiP2pDevice oldDevice3 = this.mConnectedDevice;
                int port = getPortNumber(this.mConnectedDevice);
                String iface = addr.getHostAddress() + Separators.COLON + port;
                this.mRemoteDisplayInterface = iface;
                Slog.i(TAG, "Listening for RTSP connection on " + iface + " from Wifi display: " + this.mConnectedDevice.deviceName);
                this.mRemoteDisplay = RemoteDisplay.listen(iface, new RemoteDisplay.Listener() { // from class: com.android.server.display.WifiDisplayController.12
                    @Override // android.media.RemoteDisplay.Listener
                    public void onDisplayConnected(Surface surface, int width, int height, int flags, int session) {
                        if (WifiDisplayController.this.mConnectedDevice == oldDevice3 && !WifiDisplayController.this.mRemoteDisplayConnected) {
                            Slog.i(WifiDisplayController.TAG, "Opened RTSP connection with Wifi display: " + WifiDisplayController.this.mConnectedDevice.deviceName);
                            WifiDisplayController.this.mRemoteDisplayConnected = true;
                            WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mRtspTimeout);
                            if (WifiDisplayController.this.mWifiDisplayCertMode) {
                                WifiDisplayController.this.mListener.onDisplaySessionInfo(WifiDisplayController.this.getSessionInfo(WifiDisplayController.this.mConnectedDeviceGroupInfo, session));
                            }
                            WifiDisplay display2 = WifiDisplayController.createWifiDisplay(WifiDisplayController.this.mConnectedDevice);
                            WifiDisplayController.this.advertiseDisplay(display2, surface, width, height, flags);
                        }
                    }

                    @Override // android.media.RemoteDisplay.Listener
                    public void onDisplayDisconnected() {
                        if (WifiDisplayController.this.mConnectedDevice == oldDevice3) {
                            Slog.i(WifiDisplayController.TAG, "Closed RTSP connection with Wifi display: " + WifiDisplayController.this.mConnectedDevice.deviceName);
                            WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mRtspTimeout);
                            WifiDisplayController.this.disconnect();
                        }
                    }

                    @Override // android.media.RemoteDisplay.Listener
                    public void onDisplayError(int error) {
                        if (WifiDisplayController.this.mConnectedDevice == oldDevice3) {
                            Slog.i(WifiDisplayController.TAG, "Lost RTSP connection with Wifi display due to error " + error + ": " + WifiDisplayController.this.mConnectedDevice.deviceName);
                            WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mRtspTimeout);
                            WifiDisplayController.this.handleConnectionFailure(false);
                        }
                    }
                }, this.mHandler);
                int rtspTimeout = this.mWifiDisplayCertMode ? 120 : 15;
                this.mHandler.postDelayed(this.mRtspTimeout, rtspTimeout * 1000);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public WifiDisplaySessionInfo getSessionInfo(WifiP2pGroup info, int session) {
        if (info == null) {
            return null;
        }
        Inet4Address addr = getInterfaceAddress(info);
        WifiDisplaySessionInfo sessionInfo = new WifiDisplaySessionInfo(!info.getOwner().deviceAddress.equals(this.mThisDevice.deviceAddress), session, info.getOwner().deviceAddress + Separators.SP + info.getNetworkName(), info.getPassphrase(), addr != null ? addr.getHostAddress() : "");
        return sessionInfo;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStateChanged(boolean enabled) {
        this.mWifiP2pEnabled = enabled;
        updateWfdEnableState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePeersChanged() {
        requestPeers();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConnectionChanged(NetworkInfo networkInfo) {
        this.mNetworkInfo = networkInfo;
        if (this.mWfdEnabled && networkInfo.isConnected()) {
            if (this.mDesiredDevice != null || this.mWifiDisplayCertMode) {
                this.mWifiP2pManager.requestGroupInfo(this.mWifiP2pChannel, new WifiP2pManager.GroupInfoListener() { // from class: com.android.server.display.WifiDisplayController.13
                    @Override // android.net.wifi.p2p.WifiP2pManager.GroupInfoListener
                    public void onGroupInfoAvailable(WifiP2pGroup info) {
                        if (WifiDisplayController.this.mConnectingDevice == null || info.contains(WifiDisplayController.this.mConnectingDevice)) {
                            if (WifiDisplayController.this.mDesiredDevice == null || info.contains(WifiDisplayController.this.mDesiredDevice)) {
                                if (WifiDisplayController.this.mWifiDisplayCertMode) {
                                    boolean owner = info.getOwner().deviceAddress.equals(WifiDisplayController.this.mThisDevice.deviceAddress);
                                    if (!owner || !info.getClientList().isEmpty()) {
                                        if (WifiDisplayController.this.mConnectingDevice == null && WifiDisplayController.this.mDesiredDevice == null) {
                                            WifiDisplayController.this.mConnectingDevice = WifiDisplayController.this.mDesiredDevice = owner ? info.getClientList().iterator().next() : info.getOwner();
                                        }
                                    } else {
                                        WifiDisplayController.this.mConnectingDevice = WifiDisplayController.this.mDesiredDevice = null;
                                        WifiDisplayController.this.mConnectedDeviceGroupInfo = info;
                                        WifiDisplayController.this.updateConnection();
                                    }
                                }
                                if (WifiDisplayController.this.mConnectingDevice != null && WifiDisplayController.this.mConnectingDevice == WifiDisplayController.this.mDesiredDevice) {
                                    Slog.i(WifiDisplayController.TAG, "Connected to Wifi display: " + WifiDisplayController.this.mConnectingDevice.deviceName);
                                    WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mConnectionTimeout);
                                    WifiDisplayController.this.mConnectedDeviceGroupInfo = info;
                                    WifiDisplayController.this.mConnectedDevice = WifiDisplayController.this.mConnectingDevice;
                                    WifiDisplayController.this.mConnectingDevice = null;
                                    WifiDisplayController.this.updateConnection();
                                    return;
                                }
                                return;
                            }
                            WifiDisplayController.this.disconnect();
                            return;
                        }
                        Slog.i(WifiDisplayController.TAG, "Aborting connection to Wifi display because the current P2P group does not contain the device we expected to find: " + WifiDisplayController.this.mConnectingDevice.deviceName + ", group info was: " + WifiDisplayController.describeWifiP2pGroup(info));
                        WifiDisplayController.this.handleConnectionFailure(false);
                    }
                });
                return;
            }
            return;
        }
        this.mConnectedDeviceGroupInfo = null;
        disconnect();
        if (this.mWfdEnabled) {
            requestPeers();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConnectionFailure(boolean timeoutOccurred) {
        Slog.i(TAG, "Wifi display connection failed!");
        if (this.mDesiredDevice != null) {
            if (this.mConnectionRetriesLeft > 0) {
                final WifiP2pDevice oldDevice = this.mDesiredDevice;
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.display.WifiDisplayController.16
                    @Override // java.lang.Runnable
                    public void run() {
                        if (WifiDisplayController.this.mDesiredDevice == oldDevice && WifiDisplayController.this.mConnectionRetriesLeft > 0) {
                            WifiDisplayController.access$3220(WifiDisplayController.this, 1);
                            Slog.i(WifiDisplayController.TAG, "Retrying Wifi display connection.  Retries left: " + WifiDisplayController.this.mConnectionRetriesLeft);
                            WifiDisplayController.this.retryConnection();
                        }
                    }
                }, timeoutOccurred ? 0L : 500L);
                return;
            }
            disconnect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void advertiseDisplay(final WifiDisplay display, final Surface surface, final int width, final int height, final int flags) {
        if (!Objects.equal(this.mAdvertisedDisplay, display) || this.mAdvertisedDisplaySurface != surface || this.mAdvertisedDisplayWidth != width || this.mAdvertisedDisplayHeight != height || this.mAdvertisedDisplayFlags != flags) {
            final WifiDisplay oldDisplay = this.mAdvertisedDisplay;
            final Surface oldSurface = this.mAdvertisedDisplaySurface;
            this.mAdvertisedDisplay = display;
            this.mAdvertisedDisplaySurface = surface;
            this.mAdvertisedDisplayWidth = width;
            this.mAdvertisedDisplayHeight = height;
            this.mAdvertisedDisplayFlags = flags;
            this.mHandler.post(new Runnable() { // from class: com.android.server.display.WifiDisplayController.17
                @Override // java.lang.Runnable
                public void run() {
                    if (oldSurface != null && surface != oldSurface) {
                        WifiDisplayController.this.mListener.onDisplayDisconnected();
                    } else if (oldDisplay != null && !oldDisplay.hasSameAddress(display)) {
                        WifiDisplayController.this.mListener.onDisplayConnectionFailed();
                    }
                    if (display != null) {
                        if (!display.hasSameAddress(oldDisplay)) {
                            WifiDisplayController.this.mListener.onDisplayConnecting(display);
                        } else if (!display.equals(oldDisplay)) {
                            WifiDisplayController.this.mListener.onDisplayChanged(display);
                        }
                        if (surface != null && surface != oldSurface) {
                            WifiDisplayController.this.mListener.onDisplayConnected(display, surface, width, height, flags);
                        }
                    }
                }
            });
        }
    }

    private void unadvertiseDisplay() {
        advertiseDisplay(null, null, 0, 0, 0);
    }

    private void readvertiseDisplay(WifiDisplay display) {
        advertiseDisplay(display, this.mAdvertisedDisplaySurface, this.mAdvertisedDisplayWidth, this.mAdvertisedDisplayHeight, this.mAdvertisedDisplayFlags);
    }

    private static Inet4Address getInterfaceAddress(WifiP2pGroup info) {
        try {
            NetworkInterface iface = NetworkInterface.getByName(info.getInterface());
            Enumeration<InetAddress> addrs = iface.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (addr instanceof Inet4Address) {
                    return (Inet4Address) addr;
                }
            }
            Slog.w(TAG, "Could not obtain address of network interface " + info.getInterface() + " because it had no IPv4 addresses.");
            return null;
        } catch (SocketException ex) {
            Slog.w(TAG, "Could not obtain address of network interface " + info.getInterface(), ex);
            return null;
        }
    }

    private static int getPortNumber(WifiP2pDevice device) {
        if (device.deviceName.startsWith("DIRECT-") && device.deviceName.endsWith("Broadcom")) {
            return 8554;
        }
        return DEFAULT_CONTROL_PORT;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isWifiDisplay(WifiP2pDevice device) {
        return device.wfdInfo != null && device.wfdInfo.isWfdEnabled() && isPrimarySinkDeviceType(device.wfdInfo.getDeviceType());
    }

    private static boolean isPrimarySinkDeviceType(int deviceType) {
        return deviceType == 1 || deviceType == 3;
    }

    private static String describeWifiP2pDevice(WifiP2pDevice device) {
        return device != null ? device.toString().replace('\n', ',') : "null";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String describeWifiP2pGroup(WifiP2pGroup group) {
        return group != null ? group.toString().replace('\n', ',') : "null";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static WifiDisplay createWifiDisplay(WifiP2pDevice device) {
        return new WifiDisplay(device.deviceAddress, device.deviceName, null, true, device.wfdInfo.isSessionAvailable(), false);
    }
}