package android.net.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/* loaded from: WifiManager.class */
public class WifiManager {
    private static final String TAG = "WifiManager";
    public static final int ERROR_AUTHENTICATING = 1;
    public static final String WIFI_SCAN_AVAILABLE = "wifi_scan_available";
    public static final String EXTRA_SCAN_AVAILABLE = "scan_enabled";
    public static final String WIFI_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";
    public static final String EXTRA_WIFI_STATE = "wifi_state";
    public static final String EXTRA_PREVIOUS_WIFI_STATE = "previous_wifi_state";
    public static final int WIFI_STATE_DISABLING = 0;
    public static final int WIFI_STATE_DISABLED = 1;
    public static final int WIFI_STATE_ENABLING = 2;
    public static final int WIFI_STATE_ENABLED = 3;
    public static final int WIFI_STATE_UNKNOWN = 4;
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = "previous_wifi_state";
    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;
    public static final String SUPPLICANT_CONNECTION_CHANGE_ACTION = "android.net.wifi.supplicant.CONNECTION_CHANGE";
    public static final String EXTRA_SUPPLICANT_CONNECTED = "connected";
    public static final String NETWORK_STATE_CHANGED_ACTION = "android.net.wifi.STATE_CHANGE";
    public static final String EXTRA_NETWORK_INFO = "networkInfo";
    public static final String EXTRA_BSSID = "bssid";
    public static final String EXTRA_WIFI_INFO = "wifiInfo";
    public static final String SUPPLICANT_STATE_CHANGED_ACTION = "android.net.wifi.supplicant.STATE_CHANGE";
    public static final String EXTRA_NEW_STATE = "newState";
    public static final String EXTRA_SUPPLICANT_ERROR = "supplicantError";
    public static final String CONFIGURED_NETWORKS_CHANGED_ACTION = "android.net.wifi.CONFIGURED_NETWORKS_CHANGE";
    public static final String EXTRA_WIFI_CONFIGURATION = "wifiConfiguration";
    public static final String EXTRA_MULTIPLE_NETWORKS_CHANGED = "multipleChanges";
    public static final String EXTRA_CHANGE_REASON = "changeReason";
    public static final int CHANGE_REASON_ADDED = 0;
    public static final int CHANGE_REASON_REMOVED = 1;
    public static final int CHANGE_REASON_CONFIG_CHANGE = 2;
    public static final String SCAN_RESULTS_AVAILABLE_ACTION = "android.net.wifi.SCAN_RESULTS";
    public static final String BATCHED_SCAN_RESULTS_AVAILABLE_ACTION = "android.net.wifi.BATCHED_RESULTS";
    public static final String RSSI_CHANGED_ACTION = "android.net.wifi.RSSI_CHANGED";
    public static final String EXTRA_NEW_RSSI = "newRssi";
    public static final String LINK_CONFIGURATION_CHANGED_ACTION = "android.net.wifi.LINK_CONFIGURATION_CHANGED";
    public static final String EXTRA_LINK_PROPERTIES = "linkProperties";
    public static final String EXTRA_LINK_CAPABILITIES = "linkCapabilities";
    public static final String NETWORK_IDS_CHANGED_ACTION = "android.net.wifi.NETWORK_IDS_CHANGED";
    public static final String ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE = "android.net.wifi.action.REQUEST_SCAN_ALWAYS_AVAILABLE";
    public static final String ACTION_PICK_WIFI_NETWORK = "android.net.wifi.PICK_WIFI_NETWORK";
    public static final int WIFI_MODE_FULL = 1;
    public static final int WIFI_MODE_SCAN_ONLY = 2;
    public static final int WIFI_MODE_FULL_HIGH_PERF = 3;
    private static final int MIN_RSSI = -100;
    private static final int MAX_RSSI = -55;
    public static final int RSSI_LEVELS = 5;
    public static final int WIFI_FREQUENCY_BAND_AUTO = 0;
    public static final int WIFI_FREQUENCY_BAND_5GHZ = 1;
    public static final int WIFI_FREQUENCY_BAND_2GHZ = 2;
    public static final int DATA_ACTIVITY_NOTIFICATION = 1;
    public static final int DATA_ACTIVITY_NONE = 0;
    public static final int DATA_ACTIVITY_IN = 1;
    public static final int DATA_ACTIVITY_OUT = 2;
    public static final int DATA_ACTIVITY_INOUT = 3;
    private static final int MAX_ACTIVE_LOCKS = 50;
    private int mActiveLockCount;
    private Context mContext;
    IWifiManager mService;
    private static final int INVALID_KEY = 0;
    private static AsyncChannel sAsyncChannel;
    private static CountDownLatch sConnected;
    private static int sThreadRefCount;
    private static HandlerThread sHandlerThread;
    private static final int BASE = 151552;
    public static final int CONNECT_NETWORK = 151553;
    public static final int CONNECT_NETWORK_FAILED = 151554;
    public static final int CONNECT_NETWORK_SUCCEEDED = 151555;
    public static final int FORGET_NETWORK = 151556;
    public static final int FORGET_NETWORK_FAILED = 151557;
    public static final int FORGET_NETWORK_SUCCEEDED = 151558;
    public static final int SAVE_NETWORK = 151559;
    public static final int SAVE_NETWORK_FAILED = 151560;
    public static final int SAVE_NETWORK_SUCCEEDED = 151561;
    public static final int START_WPS = 151562;
    public static final int START_WPS_SUCCEEDED = 151563;
    public static final int WPS_FAILED = 151564;
    public static final int WPS_COMPLETED = 151565;
    public static final int CANCEL_WPS = 151566;
    public static final int CANCEL_WPS_FAILED = 151567;
    public static final int CANCEL_WPS_SUCCEDED = 151568;
    public static final int DISABLE_NETWORK = 151569;
    public static final int DISABLE_NETWORK_FAILED = 151570;
    public static final int DISABLE_NETWORK_SUCCEEDED = 151571;
    public static final int RSSI_PKTCNT_FETCH = 151572;
    public static final int RSSI_PKTCNT_FETCH_SUCCEEDED = 151573;
    public static final int RSSI_PKTCNT_FETCH_FAILED = 151574;
    public static final int ERROR = 0;
    public static final int IN_PROGRESS = 1;
    public static final int BUSY = 2;
    public static final int WPS_OVERLAP_ERROR = 3;
    public static final int WPS_WEP_PROHIBITED = 4;
    public static final int WPS_TKIP_ONLY_PROHIBITED = 5;
    public static final int WPS_AUTH_FAILURE = 6;
    public static final int WPS_TIMED_OUT = 7;
    public static final int INVALID_ARGS = 8;
    private static int sListenerKey = 1;
    private static final SparseArray sListenerMap = new SparseArray();
    private static final Object sListenerMapLock = new Object();
    private static final Object sThreadRefLock = new Object();

    /* loaded from: WifiManager$ActionListener.class */
    public interface ActionListener {
        void onSuccess();

        void onFailure(int i);
    }

    /* loaded from: WifiManager$TxPacketCountListener.class */
    public interface TxPacketCountListener {
        void onSuccess(int i);

        void onFailure(int i);
    }

    /* loaded from: WifiManager$WpsListener.class */
    public interface WpsListener {
        void onStartSuccess(String str);

        void onCompletion();

        void onFailure(int i);
    }

    static /* synthetic */ int access$508(WifiManager x0) {
        int i = x0.mActiveLockCount;
        x0.mActiveLockCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$510(WifiManager x0) {
        int i = x0.mActiveLockCount;
        x0.mActiveLockCount = i - 1;
        return i;
    }

    public WifiManager(Context context, IWifiManager service) {
        this.mContext = context;
        this.mService = service;
        init();
    }

    public List<WifiConfiguration> getConfiguredNetworks() {
        try {
            return this.mService.getConfiguredNetworks();
        } catch (RemoteException e) {
            return null;
        }
    }

    public int addNetwork(WifiConfiguration config) {
        if (config == null) {
            return -1;
        }
        config.networkId = -1;
        return addOrUpdateNetwork(config);
    }

    public int updateNetwork(WifiConfiguration config) {
        if (config == null || config.networkId < 0) {
            return -1;
        }
        return addOrUpdateNetwork(config);
    }

    private int addOrUpdateNetwork(WifiConfiguration config) {
        try {
            return this.mService.addOrUpdateNetwork(config);
        } catch (RemoteException e) {
            return -1;
        }
    }

    public boolean removeNetwork(int netId) {
        try {
            return this.mService.removeNetwork(netId);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean enableNetwork(int netId, boolean disableOthers) {
        try {
            return this.mService.enableNetwork(netId, disableOthers);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean disableNetwork(int netId) {
        try {
            return this.mService.disableNetwork(netId);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean disconnect() {
        try {
            this.mService.disconnect();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean reconnect() {
        try {
            this.mService.reconnect();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean reassociate() {
        try {
            this.mService.reassociate();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean pingSupplicant() {
        if (this.mService == null) {
            return false;
        }
        try {
            return this.mService.pingSupplicant();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean startScan() {
        try {
            this.mService.startScan(null);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean startScan(WorkSource workSource) {
        try {
            this.mService.startScan(workSource);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean requestBatchedScan(BatchedScanSettings requested) {
        try {
            return this.mService.requestBatchedScan(requested, new Binder());
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isBatchedScanSupported() {
        try {
            return this.mService.isBatchedScanSupported();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void stopBatchedScan(BatchedScanSettings requested) {
        try {
            this.mService.stopBatchedScan(requested);
        } catch (RemoteException e) {
        }
    }

    public List<BatchedScanResult> getBatchedScanResults() {
        try {
            return this.mService.getBatchedScanResults(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            return null;
        }
    }

    public void pollBatchedScan() {
        try {
            this.mService.pollBatchedScan();
        } catch (RemoteException e) {
        }
    }

    public WifiInfo getConnectionInfo() {
        try {
            return this.mService.getConnectionInfo();
        } catch (RemoteException e) {
            return null;
        }
    }

    public List<ScanResult> getScanResults() {
        try {
            return this.mService.getScanResults(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean isScanAlwaysAvailable() {
        try {
            return this.mService.isScanAlwaysAvailable();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean saveConfiguration() {
        try {
            return this.mService.saveConfiguration();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void setCountryCode(String country, boolean persist) {
        try {
            this.mService.setCountryCode(country, persist);
        } catch (RemoteException e) {
        }
    }

    public void setFrequencyBand(int band, boolean persist) {
        try {
            this.mService.setFrequencyBand(band, persist);
        } catch (RemoteException e) {
        }
    }

    public int getFrequencyBand() {
        try {
            return this.mService.getFrequencyBand();
        } catch (RemoteException e) {
            return -1;
        }
    }

    public boolean isDualBandSupported() {
        try {
            return this.mService.isDualBandSupported();
        } catch (RemoteException e) {
            return false;
        }
    }

    public DhcpInfo getDhcpInfo() {
        try {
            return this.mService.getDhcpInfo();
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean setWifiEnabled(boolean enabled) {
        try {
            return this.mService.setWifiEnabled(enabled);
        } catch (RemoteException e) {
            return false;
        }
    }

    public int getWifiState() {
        try {
            return this.mService.getWifiEnabledState();
        } catch (RemoteException e) {
            return 4;
        }
    }

    public boolean isWifiEnabled() {
        return getWifiState() == 3;
    }

    public void getTxPacketCount(TxPacketCountListener listener) {
        validateChannel();
        sAsyncChannel.sendMessage(RSSI_PKTCNT_FETCH, 0, putListener(listener));
    }

    public static int calculateSignalLevel(int rssi, int numLevels) {
        if (rssi <= -100) {
            return 0;
        }
        if (rssi >= MAX_RSSI) {
            return numLevels - 1;
        }
        float outputRange = numLevels - 1;
        return (int) (((rssi - (-100)) * outputRange) / 45.0f);
    }

    public static int compareSignalLevel(int rssiA, int rssiB) {
        return rssiA - rssiB;
    }

    public boolean setWifiApEnabled(WifiConfiguration wifiConfig, boolean enabled) {
        try {
            this.mService.setWifiApEnabled(wifiConfig, enabled);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public int getWifiApState() {
        try {
            return this.mService.getWifiApEnabledState();
        } catch (RemoteException e) {
            return 14;
        }
    }

    public boolean isWifiApEnabled() {
        return getWifiApState() == 13;
    }

    public WifiConfiguration getWifiApConfiguration() {
        try {
            return this.mService.getWifiApConfiguration();
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean setWifiApConfiguration(WifiConfiguration wifiConfig) {
        try {
            this.mService.setWifiApConfiguration(wifiConfig);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean startWifi() {
        try {
            this.mService.startWifi();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean stopWifi() {
        try {
            this.mService.stopWifi();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean addToBlacklist(String bssid) {
        try {
            this.mService.addToBlacklist(bssid);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean clearBlacklist() {
        try {
            this.mService.clearBlacklist();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public void setTdlsEnabled(InetAddress remoteIPAddress, boolean enable) {
        try {
            this.mService.enableTdls(remoteIPAddress.getHostAddress(), enable);
        } catch (RemoteException e) {
        }
    }

    public void setTdlsEnabledWithMacAddress(String remoteMacAddress, boolean enable) {
        try {
            this.mService.enableTdlsWithMacAddress(remoteMacAddress, enable);
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiManager$ServiceHandler.class */
    public static class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Object listener = WifiManager.removeListener(message.arg2);
            switch (message.what) {
                case 69632:
                    if (message.arg1 == 0) {
                        WifiManager.sAsyncChannel.sendMessage(AsyncChannel.CMD_CHANNEL_FULL_CONNECTION);
                    } else {
                        Log.e(WifiManager.TAG, "Failed to set up channel connection");
                        AsyncChannel unused = WifiManager.sAsyncChannel = null;
                    }
                    WifiManager.sConnected.countDown();
                    return;
                case AsyncChannel.CMD_CHANNEL_FULLY_CONNECTED /* 69634 */:
                default:
                    return;
                case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                    Log.e(WifiManager.TAG, "Channel connection lost");
                    AsyncChannel unused2 = WifiManager.sAsyncChannel = null;
                    getLooper().quit();
                    return;
                case WifiManager.CONNECT_NETWORK_FAILED /* 151554 */:
                case WifiManager.FORGET_NETWORK_FAILED /* 151557 */:
                case WifiManager.SAVE_NETWORK_FAILED /* 151560 */:
                case WifiManager.CANCEL_WPS_FAILED /* 151567 */:
                case WifiManager.DISABLE_NETWORK_FAILED /* 151570 */:
                    if (listener != null) {
                        ((ActionListener) listener).onFailure(message.arg1);
                        return;
                    }
                    return;
                case WifiManager.CONNECT_NETWORK_SUCCEEDED /* 151555 */:
                case WifiManager.FORGET_NETWORK_SUCCEEDED /* 151558 */:
                case WifiManager.SAVE_NETWORK_SUCCEEDED /* 151561 */:
                case WifiManager.CANCEL_WPS_SUCCEDED /* 151568 */:
                case WifiManager.DISABLE_NETWORK_SUCCEEDED /* 151571 */:
                    if (listener != null) {
                        ((ActionListener) listener).onSuccess();
                        return;
                    }
                    return;
                case WifiManager.START_WPS_SUCCEEDED /* 151563 */:
                    if (listener != null) {
                        WpsResult result = (WpsResult) message.obj;
                        ((WpsListener) listener).onStartSuccess(result.pin);
                        synchronized (WifiManager.sListenerMapLock) {
                            WifiManager.sListenerMap.put(message.arg2, listener);
                        }
                        return;
                    }
                    return;
                case WifiManager.WPS_FAILED /* 151564 */:
                    if (listener != null) {
                        ((WpsListener) listener).onFailure(message.arg1);
                        return;
                    }
                    return;
                case WifiManager.WPS_COMPLETED /* 151565 */:
                    if (listener != null) {
                        ((WpsListener) listener).onCompletion();
                        return;
                    }
                    return;
                case WifiManager.RSSI_PKTCNT_FETCH_SUCCEEDED /* 151573 */:
                    if (listener != null) {
                        RssiPacketCountInfo info = (RssiPacketCountInfo) message.obj;
                        if (info != null) {
                            ((TxPacketCountListener) listener).onSuccess(info.txgood + info.txbad);
                            return;
                        } else {
                            ((TxPacketCountListener) listener).onFailure(0);
                            return;
                        }
                    }
                    return;
                case WifiManager.RSSI_PKTCNT_FETCH_FAILED /* 151574 */:
                    if (listener != null) {
                        ((TxPacketCountListener) listener).onFailure(message.arg1);
                        return;
                    }
                    return;
            }
        }
    }

    private static int putListener(Object listener) {
        int key;
        if (listener == null) {
            return 0;
        }
        synchronized (sListenerMapLock) {
            do {
                key = sListenerKey;
                sListenerKey = key + 1;
            } while (key == 0);
            sListenerMap.put(key, listener);
        }
        return key;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object removeListener(int key) {
        Object listener;
        if (key == 0) {
            return null;
        }
        synchronized (sListenerMapLock) {
            listener = sListenerMap.get(key);
            sListenerMap.remove(key);
        }
        return listener;
    }

    private void init() {
        synchronized (sThreadRefLock) {
            int i = sThreadRefCount + 1;
            sThreadRefCount = i;
            if (i == 1) {
                Messenger messenger = getWifiServiceMessenger();
                if (messenger == null) {
                    sAsyncChannel = null;
                    return;
                }
                sHandlerThread = new HandlerThread(TAG);
                sAsyncChannel = new AsyncChannel();
                sConnected = new CountDownLatch(1);
                sHandlerThread.start();
                Handler handler = new ServiceHandler(sHandlerThread.getLooper());
                sAsyncChannel.connect(this.mContext, handler, messenger);
                try {
                    sConnected.await();
                } catch (InterruptedException e) {
                    Log.e(TAG, "interrupted wait at init");
                }
            }
        }
    }

    private void validateChannel() {
        if (sAsyncChannel == null) {
            throw new IllegalStateException("No permission to access and change wifi or a bad initialization");
        }
    }

    public void connect(WifiConfiguration config, ActionListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        validateChannel();
        sAsyncChannel.sendMessage(CONNECT_NETWORK, -1, putListener(listener), config);
    }

    public void connect(int networkId, ActionListener listener) {
        if (networkId < 0) {
            throw new IllegalArgumentException("Network id cannot be negative");
        }
        validateChannel();
        sAsyncChannel.sendMessage(CONNECT_NETWORK, networkId, putListener(listener));
    }

    public void save(WifiConfiguration config, ActionListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        validateChannel();
        sAsyncChannel.sendMessage(SAVE_NETWORK, 0, putListener(listener), config);
    }

    public void forget(int netId, ActionListener listener) {
        if (netId < 0) {
            throw new IllegalArgumentException("Network id cannot be negative");
        }
        validateChannel();
        sAsyncChannel.sendMessage(FORGET_NETWORK, netId, putListener(listener));
    }

    public void disable(int netId, ActionListener listener) {
        if (netId < 0) {
            throw new IllegalArgumentException("Network id cannot be negative");
        }
        validateChannel();
        sAsyncChannel.sendMessage(DISABLE_NETWORK, netId, putListener(listener));
    }

    public void startWps(WpsInfo config, WpsListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        validateChannel();
        sAsyncChannel.sendMessage(START_WPS, 0, putListener(listener), config);
    }

    public void cancelWps(ActionListener listener) {
        validateChannel();
        sAsyncChannel.sendMessage(CANCEL_WPS, 0, putListener(listener));
    }

    public Messenger getWifiServiceMessenger() {
        try {
            return this.mService.getWifiServiceMessenger();
        } catch (RemoteException e) {
            return null;
        } catch (SecurityException e2) {
            return null;
        }
    }

    public Messenger getWifiStateMachineMessenger() {
        try {
            return this.mService.getWifiStateMachineMessenger();
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getConfigFile() {
        try {
            return this.mService.getConfigFile();
        } catch (RemoteException e) {
            return null;
        }
    }

    /* loaded from: WifiManager$WifiLock.class */
    public class WifiLock {
        private String mTag;
        private final IBinder mBinder;
        private int mRefCount;
        int mLockType;
        private boolean mRefCounted;
        private boolean mHeld;
        private WorkSource mWorkSource;

        private WifiLock(int lockType, String tag) {
            this.mTag = tag;
            this.mLockType = lockType;
            this.mBinder = new Binder();
            this.mRefCount = 0;
            this.mRefCounted = true;
            this.mHeld = false;
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0024, code lost:
            if (r6.mHeld == false) goto L9;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void acquire() {
            /*
                r6 = this;
                r0 = r6
                android.os.IBinder r0 = r0.mBinder
                r1 = r0
                r7 = r1
                monitor-enter(r0)
                r0 = r6
                boolean r0 = r0.mRefCounted     // Catch: java.lang.Throwable -> L92
                if (r0 == 0) goto L20
                r0 = r6
                r1 = r0
                int r1 = r1.mRefCount     // Catch: java.lang.Throwable -> L92
                r2 = 1
                int r1 = r1 + r2
                r2 = r1; r1 = r0; r0 = r2;      // Catch: java.lang.Throwable -> L92
                r1.mRefCount = r2     // Catch: java.lang.Throwable -> L92
                r1 = 1
                if (r0 != r1) goto L8d
                goto L27
            L20:
                r0 = r6
                boolean r0 = r0.mHeld     // Catch: java.lang.Throwable -> L92
                if (r0 != 0) goto L8d
            L27:
                r0 = r6
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L87 java.lang.Throwable -> L92
                android.net.wifi.IWifiManager r0 = r0.mService     // Catch: android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r1 = r6
                android.os.IBinder r1 = r1.mBinder     // Catch: android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r2 = r6
                int r2 = r2.mLockType     // Catch: android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r3 = r6
                java.lang.String r3 = r3.mTag     // Catch: android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r4 = r6
                android.os.WorkSource r4 = r4.mWorkSource     // Catch: android.os.RemoteException -> L87 java.lang.Throwable -> L92
                boolean r0 = r0.acquireWifiLock(r1, r2, r3, r4)     // Catch: android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r0 = r6
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r1 = r0
                r8 = r1
                monitor-enter(r0)     // Catch: android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r0 = r6
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                int r0 = android.net.wifi.WifiManager.access$500(r0)     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r1 = 50
                if (r0 < r1) goto L72
                r0 = r6
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                android.net.wifi.IWifiManager r0 = r0.mService     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r1 = r6
                android.os.IBinder r1 = r1.mBinder     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                boolean r0 = r0.releaseWifiLock(r1)     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                java.lang.UnsupportedOperationException r0 = new java.lang.UnsupportedOperationException     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r1 = r0
                java.lang.String r2 = "Exceeded maximum number of wifi locks"
                r1.<init>(r2)     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                throw r0     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
            L72:
                r0 = r6
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                int r0 = android.net.wifi.WifiManager.access$508(r0)     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r0 = r8
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                goto L84
            L7f:
                r9 = move-exception
                r0 = r8
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L7f android.os.RemoteException -> L87 java.lang.Throwable -> L92
                r0 = r9
                throw r0     // Catch: android.os.RemoteException -> L87 java.lang.Throwable -> L92
            L84:
                goto L88
            L87:
                r8 = move-exception
            L88:
                r0 = r6
                r1 = 1
                r0.mHeld = r1     // Catch: java.lang.Throwable -> L92
            L8d:
                r0 = r7
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L92
                goto L99
            L92:
                r10 = move-exception
                r0 = r7
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L92
                r0 = r10
                throw r0
            L99:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiManager.WifiLock.acquire():void");
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0023, code lost:
            if (r5.mHeld != false) goto L9;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void release() {
            /*
                r5 = this;
                r0 = r5
                android.os.IBinder r0 = r0.mBinder
                r1 = r0
                r6 = r1
                monitor-enter(r0)
                r0 = r5
                boolean r0 = r0.mRefCounted     // Catch: java.lang.Throwable -> L83
                if (r0 == 0) goto L1f
                r0 = r5
                r1 = r0
                int r1 = r1.mRefCount     // Catch: java.lang.Throwable -> L83
                r2 = 1
                int r1 = r1 - r2
                r2 = r1; r1 = r0; r0 = r2;      // Catch: java.lang.Throwable -> L83
                r1.mRefCount = r2     // Catch: java.lang.Throwable -> L83
                if (r0 != 0) goto L59
                goto L26
            L1f:
                r0 = r5
                boolean r0 = r0.mHeld     // Catch: java.lang.Throwable -> L83
                if (r0 == 0) goto L59
            L26:
                r0 = r5
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L53 java.lang.Throwable -> L83
                android.net.wifi.IWifiManager r0 = r0.mService     // Catch: android.os.RemoteException -> L53 java.lang.Throwable -> L83
                r1 = r5
                android.os.IBinder r1 = r1.mBinder     // Catch: android.os.RemoteException -> L53 java.lang.Throwable -> L83
                boolean r0 = r0.releaseWifiLock(r1)     // Catch: android.os.RemoteException -> L53 java.lang.Throwable -> L83
                r0 = r5
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L53 java.lang.Throwable -> L83
                r1 = r0
                r7 = r1
                monitor-enter(r0)     // Catch: android.os.RemoteException -> L53 java.lang.Throwable -> L83
                r0 = r5
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L4b android.os.RemoteException -> L53 java.lang.Throwable -> L83
                int r0 = android.net.wifi.WifiManager.access$510(r0)     // Catch: java.lang.Throwable -> L4b android.os.RemoteException -> L53 java.lang.Throwable -> L83
                r0 = r7
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L4b android.os.RemoteException -> L53 java.lang.Throwable -> L83
                goto L50
            L4b:
                r8 = move-exception
                r0 = r7
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L4b android.os.RemoteException -> L53 java.lang.Throwable -> L83
                r0 = r8
                throw r0     // Catch: android.os.RemoteException -> L53 java.lang.Throwable -> L83
            L50:
                goto L54
            L53:
                r7 = move-exception
            L54:
                r0 = r5
                r1 = 0
                r0.mHeld = r1     // Catch: java.lang.Throwable -> L83
            L59:
                r0 = r5
                int r0 = r0.mRefCount     // Catch: java.lang.Throwable -> L83
                if (r0 >= 0) goto L7e
                java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch: java.lang.Throwable -> L83
                r1 = r0
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L83
                r3 = r2
                r3.<init>()     // Catch: java.lang.Throwable -> L83
                java.lang.String r3 = "WifiLock under-locked "
                java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L83
                r3 = r5
                java.lang.String r3 = r3.mTag     // Catch: java.lang.Throwable -> L83
                java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L83
                java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L83
                r1.<init>(r2)     // Catch: java.lang.Throwable -> L83
                throw r0     // Catch: java.lang.Throwable -> L83
            L7e:
                r0 = r6
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L83
                goto L8a
            L83:
                r9 = move-exception
                r0 = r6
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L83
                r0 = r9
                throw r0
            L8a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiManager.WifiLock.release():void");
        }

        public void setReferenceCounted(boolean refCounted) {
            this.mRefCounted = refCounted;
        }

        public boolean isHeld() {
            boolean z;
            synchronized (this.mBinder) {
                z = this.mHeld;
            }
            return z;
        }

        public void setWorkSource(WorkSource ws) {
            synchronized (this.mBinder) {
                if (ws != null) {
                    if (ws.size() == 0) {
                        ws = null;
                    }
                }
                boolean changed = true;
                if (ws == null) {
                    this.mWorkSource = null;
                } else {
                    ws.clearNames();
                    if (this.mWorkSource == null) {
                        changed = this.mWorkSource != null;
                        this.mWorkSource = new WorkSource(ws);
                    } else {
                        changed = this.mWorkSource.diff(ws);
                        if (changed) {
                            this.mWorkSource.set(ws);
                        }
                    }
                }
                if (changed && this.mHeld) {
                    try {
                        WifiManager.this.mService.updateWifiLockWorkSource(this.mBinder, this.mWorkSource);
                    } catch (RemoteException e) {
                    }
                }
            }
        }

        public String toString() {
            String s3;
            String str;
            synchronized (this.mBinder) {
                String s1 = Integer.toHexString(System.identityHashCode(this));
                String s2 = this.mHeld ? "held; " : "";
                if (this.mRefCounted) {
                    s3 = "refcounted: refcount = " + this.mRefCount;
                } else {
                    s3 = "not refcounted";
                }
                str = "WifiLock{ " + s1 + "; " + s2 + s3 + " }";
            }
            return str;
        }

        protected void finalize() throws Throwable {
            super.finalize();
            synchronized (this.mBinder) {
                if (this.mHeld) {
                    try {
                        WifiManager.this.mService.releaseWifiLock(this.mBinder);
                        synchronized (WifiManager.this) {
                            WifiManager.access$510(WifiManager.this);
                        }
                    } catch (RemoteException e) {
                    }
                }
            }
        }
    }

    public WifiLock createWifiLock(int lockType, String tag) {
        return new WifiLock(lockType, tag);
    }

    public WifiLock createWifiLock(String tag) {
        return new WifiLock(1, tag);
    }

    public MulticastLock createMulticastLock(String tag) {
        return new MulticastLock(tag);
    }

    /* loaded from: WifiManager$MulticastLock.class */
    public class MulticastLock {
        private String mTag;
        private final IBinder mBinder;
        private int mRefCount;
        private boolean mRefCounted;
        private boolean mHeld;

        private MulticastLock(String tag) {
            this.mTag = tag;
            this.mBinder = new Binder();
            this.mRefCount = 0;
            this.mRefCounted = true;
            this.mHeld = false;
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0024, code lost:
            if (r4.mHeld == false) goto L9;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void acquire() {
            /*
                r4 = this;
                r0 = r4
                android.os.IBinder r0 = r0.mBinder
                r1 = r0
                r5 = r1
                monitor-enter(r0)
                r0 = r4
                boolean r0 = r0.mRefCounted     // Catch: java.lang.Throwable -> L84
                if (r0 == 0) goto L20
                r0 = r4
                r1 = r0
                int r1 = r1.mRefCount     // Catch: java.lang.Throwable -> L84
                r2 = 1
                int r1 = r1 + r2
                r2 = r1; r1 = r0; r0 = r2;      // Catch: java.lang.Throwable -> L84
                r1.mRefCount = r2     // Catch: java.lang.Throwable -> L84
                r1 = 1
                if (r0 != r1) goto L7f
                goto L27
            L20:
                r0 = r4
                boolean r0 = r0.mHeld     // Catch: java.lang.Throwable -> L84
                if (r0 != 0) goto L7f
            L27:
                r0 = r4
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L79 java.lang.Throwable -> L84
                android.net.wifi.IWifiManager r0 = r0.mService     // Catch: android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r1 = r4
                android.os.IBinder r1 = r1.mBinder     // Catch: android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r2 = r4
                java.lang.String r2 = r2.mTag     // Catch: android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r0.acquireMulticastLock(r1, r2)     // Catch: android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r0 = r4
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r1 = r0
                r6 = r1
                monitor-enter(r0)     // Catch: android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r0 = r4
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                int r0 = android.net.wifi.WifiManager.access$500(r0)     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r1 = 50
                if (r0 < r1) goto L64
                r0 = r4
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                android.net.wifi.IWifiManager r0 = r0.mService     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r0.releaseMulticastLock()     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                java.lang.UnsupportedOperationException r0 = new java.lang.UnsupportedOperationException     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r1 = r0
                java.lang.String r2 = "Exceeded maximum number of wifi locks"
                r1.<init>(r2)     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                throw r0     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
            L64:
                r0 = r4
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                int r0 = android.net.wifi.WifiManager.access$508(r0)     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r0 = r6
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                goto L76
            L71:
                r7 = move-exception
                r0 = r6
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L71 android.os.RemoteException -> L79 java.lang.Throwable -> L84
                r0 = r7
                throw r0     // Catch: android.os.RemoteException -> L79 java.lang.Throwable -> L84
            L76:
                goto L7a
            L79:
                r6 = move-exception
            L7a:
                r0 = r4
                r1 = 1
                r0.mHeld = r1     // Catch: java.lang.Throwable -> L84
            L7f:
                r0 = r5
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L84
                goto L8b
            L84:
                r8 = move-exception
                r0 = r5
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L84
                r0 = r8
                throw r0
            L8b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiManager.MulticastLock.acquire():void");
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0023, code lost:
            if (r5.mHeld != false) goto L9;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void release() {
            /*
                r5 = this;
                r0 = r5
                android.os.IBinder r0 = r0.mBinder
                r1 = r0
                r6 = r1
                monitor-enter(r0)
                r0 = r5
                boolean r0 = r0.mRefCounted     // Catch: java.lang.Throwable -> L7e
                if (r0 == 0) goto L1f
                r0 = r5
                r1 = r0
                int r1 = r1.mRefCount     // Catch: java.lang.Throwable -> L7e
                r2 = 1
                int r1 = r1 - r2
                r2 = r1; r1 = r0; r0 = r2;      // Catch: java.lang.Throwable -> L7e
                r1.mRefCount = r2     // Catch: java.lang.Throwable -> L7e
                if (r0 != 0) goto L54
                goto L26
            L1f:
                r0 = r5
                boolean r0 = r0.mHeld     // Catch: java.lang.Throwable -> L7e
                if (r0 == 0) goto L54
            L26:
                r0 = r5
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L4e java.lang.Throwable -> L7e
                android.net.wifi.IWifiManager r0 = r0.mService     // Catch: android.os.RemoteException -> L4e java.lang.Throwable -> L7e
                r0.releaseMulticastLock()     // Catch: android.os.RemoteException -> L4e java.lang.Throwable -> L7e
                r0 = r5
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: android.os.RemoteException -> L4e java.lang.Throwable -> L7e
                r1 = r0
                r7 = r1
                monitor-enter(r0)     // Catch: android.os.RemoteException -> L4e java.lang.Throwable -> L7e
                r0 = r5
                android.net.wifi.WifiManager r0 = android.net.wifi.WifiManager.this     // Catch: java.lang.Throwable -> L46 android.os.RemoteException -> L4e java.lang.Throwable -> L7e
                int r0 = android.net.wifi.WifiManager.access$510(r0)     // Catch: java.lang.Throwable -> L46 android.os.RemoteException -> L4e java.lang.Throwable -> L7e
                r0 = r7
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L46 android.os.RemoteException -> L4e java.lang.Throwable -> L7e
                goto L4b
            L46:
                r8 = move-exception
                r0 = r7
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L46 android.os.RemoteException -> L4e java.lang.Throwable -> L7e
                r0 = r8
                throw r0     // Catch: android.os.RemoteException -> L4e java.lang.Throwable -> L7e
            L4b:
                goto L4f
            L4e:
                r7 = move-exception
            L4f:
                r0 = r5
                r1 = 0
                r0.mHeld = r1     // Catch: java.lang.Throwable -> L7e
            L54:
                r0 = r5
                int r0 = r0.mRefCount     // Catch: java.lang.Throwable -> L7e
                if (r0 >= 0) goto L79
                java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch: java.lang.Throwable -> L7e
                r1 = r0
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L7e
                r3 = r2
                r3.<init>()     // Catch: java.lang.Throwable -> L7e
                java.lang.String r3 = "MulticastLock under-locked "
                java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L7e
                r3 = r5
                java.lang.String r3 = r3.mTag     // Catch: java.lang.Throwable -> L7e
                java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L7e
                java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L7e
                r1.<init>(r2)     // Catch: java.lang.Throwable -> L7e
                throw r0     // Catch: java.lang.Throwable -> L7e
            L79:
                r0 = r6
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L7e
                goto L85
            L7e:
                r9 = move-exception
                r0 = r6
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L7e
                r0 = r9
                throw r0
            L85:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiManager.MulticastLock.release():void");
        }

        public void setReferenceCounted(boolean refCounted) {
            this.mRefCounted = refCounted;
        }

        public boolean isHeld() {
            boolean z;
            synchronized (this.mBinder) {
                z = this.mHeld;
            }
            return z;
        }

        public String toString() {
            String s3;
            String str;
            synchronized (this.mBinder) {
                String s1 = Integer.toHexString(System.identityHashCode(this));
                String s2 = this.mHeld ? "held; " : "";
                if (this.mRefCounted) {
                    s3 = "refcounted: refcount = " + this.mRefCount;
                } else {
                    s3 = "not refcounted";
                }
                str = "MulticastLock{ " + s1 + "; " + s2 + s3 + " }";
            }
            return str;
        }

        protected void finalize() throws Throwable {
            super.finalize();
            setReferenceCounted(false);
            release();
        }
    }

    public boolean isMulticastEnabled() {
        try {
            return this.mService.isMulticastEnabled();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean initializeMulticastFiltering() {
        try {
            this.mService.initializeMulticastFiltering();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public void captivePortalCheckComplete() {
        try {
            this.mService.captivePortalCheckComplete();
        } catch (RemoteException e) {
        }
    }

    protected void finalize() throws Throwable {
        try {
            synchronized (sThreadRefLock) {
                int i = sThreadRefCount - 1;
                sThreadRefCount = i;
                if (i == 0 && sAsyncChannel != null) {
                    sAsyncChannel.disconnect();
                }
            }
        } finally {
            super.finalize();
        }
    }
}