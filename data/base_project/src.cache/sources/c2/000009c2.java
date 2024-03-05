package android.net.wifi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.backup.IBackupManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.DhcpResults;
import android.net.DhcpStateMachine;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.Uri;
import android.net.wifi.WpsResult;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pService;
import android.os.BatteryStats;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.LruCache;
import android.util.TimedRemoteCaller;
import com.android.internal.R;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.location.LocationFudger;
import com.android.server.net.BaseNetworkObserver;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/* loaded from: WifiStateMachine.class */
public class WifiStateMachine extends StateMachine {
    private static final String NETWORKTYPE = "WIFI";
    private static final boolean DBG = false;
    private WifiMonitor mWifiMonitor;
    private WifiNative mWifiNative;
    private WifiConfigStore mWifiConfigStore;
    private INetworkManagementService mNwService;
    private ConnectivityManager mCm;
    private final boolean mP2pSupported;
    private final AtomicBoolean mP2pConnected;
    private boolean mTemporarilyDisconnectWifi;
    private final String mPrimaryDeviceType;
    private List<ScanResult> mScanResults;
    private static final Pattern scanResultPattern = Pattern.compile("\t+");
    private static final int SCAN_RESULT_CACHE_SIZE = 80;
    private final LruCache<String, ScanResult> mScanResultCache;
    private final List<BatchedScanResult> mBatchedScanResults;
    private int mBatchedScanOwnerUid;
    private int mExpectedBatchedScans;
    private long mBatchedScanMinPollTime;
    private final boolean mBackgroundScanSupported;
    private String mInterfaceName;
    private String mTetherInterfaceName;
    private int mLastSignalLevel;
    private String mLastBssid;
    private int mLastNetworkId;
    private boolean mEnableRssiPolling;
    private boolean mEnableBackgroundScan;
    private int mRssiPollToken;
    private int mReconnectCount;
    private int mOperationalMode;
    private boolean mScanResultIsPending;
    private WorkSource mScanWorkSource;
    private static final int UNKNOWN_SCAN_SOURCE = -1;
    private AtomicBoolean mScreenBroadcastReceived;
    private boolean mBluetoothConnectionActive;
    private PowerManager.WakeLock mSuspendWakeLock;
    private static final int POLL_RSSI_INTERVAL_MSECS = 3000;
    private static final int SUPPLICANT_RESTART_INTERVAL_MSECS = 5000;
    private static final int SUPPLICANT_RESTART_TRIES = 5;
    private int mSupplicantRestartCount;
    private int mSupplicantStopFailureToken;
    private static final int TETHER_NOTIFICATION_TIME_OUT_MSECS = 5000;
    private int mTetherToken;
    private static final int DRIVER_START_TIME_OUT_MSECS = 10000;
    private int mDriverStartToken;
    private LinkProperties mLinkProperties;
    private final LinkProperties mNetlinkLinkProperties;
    private int mPeriodicScanToken;
    private PowerManager.WakeLock mWakeLock;
    private Context mContext;
    private final Object mDhcpResultsLock;
    private DhcpResults mDhcpResults;
    private WifiInfo mWifiInfo;
    private NetworkInfo mNetworkInfo;
    private SupplicantStateTracker mSupplicantStateTracker;
    private DhcpStateMachine mDhcpStateMachine;
    private boolean mDhcpActive;
    private InterfaceObserver mInterfaceObserver;
    private AlarmManager mAlarmManager;
    private PendingIntent mScanIntent;
    private PendingIntent mDriverStopIntent;
    private PendingIntent mBatchedScanIntervalIntent;
    private AtomicInteger mFrequencyBand;
    private AtomicBoolean mFilteringMulticastV4Packets;
    private AsyncChannel mReplyChannel;
    private WifiP2pManager mWifiP2pManager;
    private AsyncChannel mWifiP2pChannel;
    private AsyncChannel mWifiApConfigChannel;
    static final int BASE = 131072;
    static final int CMD_START_SUPPLICANT = 131083;
    static final int CMD_STOP_SUPPLICANT = 131084;
    static final int CMD_START_DRIVER = 131085;
    static final int CMD_STOP_DRIVER = 131086;
    static final int CMD_STATIC_IP_SUCCESS = 131087;
    static final int CMD_STATIC_IP_FAILURE = 131088;
    static final int CMD_STOP_SUPPLICANT_FAILED = 131089;
    static final int CMD_DELAYED_STOP_DRIVER = 131090;
    static final int CMD_DRIVER_START_TIMED_OUT = 131091;
    static final int CMD_CAPTIVE_CHECK_COMPLETE = 131092;
    static final int CMD_START_AP = 131093;
    static final int CMD_START_AP_SUCCESS = 131094;
    static final int CMD_START_AP_FAILURE = 131095;
    static final int CMD_STOP_AP = 131096;
    static final int CMD_SET_AP_CONFIG = 131097;
    static final int CMD_SET_AP_CONFIG_COMPLETED = 131098;
    static final int CMD_REQUEST_AP_CONFIG = 131099;
    static final int CMD_RESPONSE_AP_CONFIG = 131100;
    static final int CMD_TETHER_STATE_CHANGE = 131101;
    static final int CMD_TETHER_NOTIFICATION_TIMED_OUT = 131102;
    static final int CMD_BLUETOOTH_ADAPTER_STATE_CHANGE = 131103;
    static final int CMD_PING_SUPPLICANT = 131123;
    static final int CMD_ADD_OR_UPDATE_NETWORK = 131124;
    static final int CMD_REMOVE_NETWORK = 131125;
    static final int CMD_ENABLE_NETWORK = 131126;
    static final int CMD_ENABLE_ALL_NETWORKS = 131127;
    static final int CMD_BLACKLIST_NETWORK = 131128;
    static final int CMD_CLEAR_BLACKLIST = 131129;
    static final int CMD_SAVE_CONFIG = 131130;
    static final int CMD_GET_CONFIGURED_NETWORKS = 131131;
    static final int CMD_START_SCAN = 131143;
    static final int CMD_SET_OPERATIONAL_MODE = 131144;
    static final int CMD_DISCONNECT = 131145;
    static final int CMD_RECONNECT = 131146;
    static final int CMD_REASSOCIATE = 131147;
    static final int CMD_SET_HIGH_PERF_MODE = 131149;
    static final int CMD_SET_COUNTRY_CODE = 131152;
    static final int CMD_ENABLE_RSSI_POLL = 131154;
    static final int CMD_RSSI_POLL = 131155;
    static final int CMD_START_PACKET_FILTERING = 131156;
    static final int CMD_STOP_PACKET_FILTERING = 131157;
    static final int CMD_SET_SUSPEND_OPT_ENABLED = 131158;
    static final int CMD_NO_NETWORKS_PERIODIC_SCAN = 131160;
    static final int MULTICAST_V6 = 1;
    static final int MULTICAST_V4 = 0;
    static final int CMD_SET_FREQUENCY_BAND = 131162;
    static final int CMD_ENABLE_BACKGROUND_SCAN = 131163;
    static final int CMD_ENABLE_TDLS = 131164;
    static final int CMD_RESET_SUPPLICANT_STATE = 131183;
    public static final int CMD_ENABLE_P2P = 131203;
    public static final int CMD_DISABLE_P2P_REQ = 131204;
    public static final int CMD_DISABLE_P2P_RSP = 131205;
    public static final int CMD_BOOT_COMPLETED = 131206;
    public static final int CMD_SET_BATCHED_SCAN = 131207;
    public static final int CMD_START_NEXT_BATCHED_SCAN = 131208;
    public static final int CMD_POLL_BATCHED_SCAN = 131209;
    static final int CMD_IP_ADDRESS_UPDATED = 131212;
    static final int CMD_IP_ADDRESS_REMOVED = 131213;
    static final int CMD_RELOAD_TLS_AND_RECONNECT = 131214;
    public static final int CONNECT_MODE = 1;
    public static final int SCAN_ONLY_MODE = 2;
    public static final int SCAN_ONLY_WITH_WIFI_OFF_MODE = 3;
    private static final int SUCCESS = 1;
    private static final int FAILURE = -1;
    private static final int DEFAULT_MAX_DHCP_RETRIES = 9;
    private int mSuspendOptNeedsDisabled;
    private static final int SUSPEND_DUE_TO_DHCP = 1;
    private static final int SUSPEND_DUE_TO_HIGH_PERF = 2;
    private static final int SUSPEND_DUE_TO_SCREEN = 4;
    private AtomicBoolean mUserWantsSuspendOpt;
    private final int mDefaultFrameworkScanIntervalMs;
    private long mSupplicantScanIntervalMs;
    private static final int MIN_INTERVAL_ENABLE_ALL_NETWORKS_MS = 600000;
    private long mLastEnableAllNetworksTime;
    private final int mDriverStopDelayMs;
    private int mDelayedStopCounter;
    private boolean mInDelayedStop;
    private volatile String mPersistedCountryCode;
    private String mLastSetCountryCode;
    private static final int MIN_RSSI = -200;
    private static final int MAX_RSSI = 256;
    private State mDefaultState;
    private State mInitialState;
    private State mSupplicantStartingState;
    private State mSupplicantStartedState;
    private State mSupplicantStoppingState;
    private State mDriverStartingState;
    private State mDriverStartedState;
    private State mWaitForP2pDisableState;
    private State mDriverStoppingState;
    private State mDriverStoppedState;
    private State mScanModeState;
    private State mConnectModeState;
    private State mL2ConnectedState;
    private State mObtainingIpState;
    private State mVerifyingLinkState;
    private State mCaptivePortalCheckState;
    private State mConnectedState;
    private State mDisconnectingState;
    private State mDisconnectedState;
    private State mWpsRunningState;
    private State mSoftApStartingState;
    private State mSoftApStartedState;
    private State mTetheringState;
    private State mTetheredState;
    private State mUntetheringState;
    private final AtomicInteger mWifiState;
    private final AtomicInteger mWifiApState;
    private static final int SCAN_REQUEST = 0;
    private static final String ACTION_START_SCAN = "com.android.server.WifiManager.action.START_SCAN";
    private static final String DELAYED_STOP_COUNTER = "DelayedStopCounter";
    private static final int DRIVER_STOP_REQUEST = 0;
    private static final String ACTION_DELAYED_DRIVER_STOP = "com.android.server.WifiManager.action.DELAYED_DRIVER_STOP";
    private static final String ACTION_REFRESH_BATCHED_SCAN = "com.android.server.WifiManager.action.REFRESH_BATCHED_SCAN";
    private boolean mIsRunning;
    private boolean mReportedRunning;
    private final WorkSource mRunningWifiUids;
    private final WorkSource mLastRunningWifiUids;
    private final IBatteryStats mBatteryStats;
    private BatchedScanSettings mBatchedScanSettings;
    private static final boolean DEBUG_PARSE = false;
    private static final String ID_STR = "id=";
    private static final String BSSID_STR = "bssid=";
    private static final String FREQ_STR = "freq=";
    private static final String LEVEL_STR = "level=";
    private static final String TSF_STR = "tsf=";
    private static final String FLAGS_STR = "flags=";
    private static final String SSID_STR = "ssid=";
    private static final String DELIMITER_STR = "====";
    private static final String END_STR = "####";

    static /* synthetic */ int access$5504(WifiStateMachine x0) {
        int i = x0.mSupplicantRestartCount + 1;
        x0.mSupplicantRestartCount = i;
        return i;
    }

    static /* synthetic */ int access$8704(WifiStateMachine x0) {
        int i = x0.mSupplicantStopFailureToken + 1;
        x0.mSupplicantStopFailureToken = i;
        return i;
    }

    static /* synthetic */ int access$9304(WifiStateMachine x0) {
        int i = x0.mDriverStartToken + 1;
        x0.mDriverStartToken = i;
        return i;
    }

    static /* synthetic */ int access$10408(WifiStateMachine x0) {
        int i = x0.mDelayedStopCounter;
        x0.mDelayedStopCounter = i + 1;
        return i;
    }

    static /* synthetic */ int access$16408(WifiStateMachine x0) {
        int i = x0.mRssiPollToken;
        x0.mRssiPollToken = i + 1;
        return i;
    }

    static /* synthetic */ int access$20804(WifiStateMachine x0) {
        int i = x0.mPeriodicScanToken + 1;
        x0.mPeriodicScanToken = i;
        return i;
    }

    static /* synthetic */ int access$23104(WifiStateMachine x0) {
        int i = x0.mTetherToken + 1;
        x0.mTetherToken = i;
        return i;
    }

    /* loaded from: WifiStateMachine$InterfaceObserver.class */
    private class InterfaceObserver extends BaseNetworkObserver {
        private WifiStateMachine mWifiStateMachine;

        InterfaceObserver(WifiStateMachine wifiStateMachine) {
            this.mWifiStateMachine = wifiStateMachine;
        }

        @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
        public void addressUpdated(String address, String iface, int flags, int scope) {
            if (this.mWifiStateMachine.mInterfaceName.equals(iface)) {
                this.mWifiStateMachine.sendMessage(WifiStateMachine.CMD_IP_ADDRESS_UPDATED, new LinkAddress(address));
            }
        }

        @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
        public void addressRemoved(String address, String iface, int flags, int scope) {
            if (this.mWifiStateMachine.mInterfaceName.equals(iface)) {
                this.mWifiStateMachine.sendMessage(WifiStateMachine.CMD_IP_ADDRESS_REMOVED, new LinkAddress(address));
            }
        }
    }

    /* loaded from: WifiStateMachine$TetherStateChange.class */
    private class TetherStateChange {
        ArrayList<String> available;
        ArrayList<String> active;

        TetherStateChange(ArrayList<String> av, ArrayList<String> ac) {
            this.available = av;
            this.active = ac;
        }
    }

    public WifiStateMachine(Context context, String wlanInterface) {
        super("WifiStateMachine");
        this.mP2pConnected = new AtomicBoolean(false);
        this.mTemporarilyDisconnectWifi = false;
        this.mScanResults = new ArrayList();
        this.mBatchedScanResults = new ArrayList();
        this.mBatchedScanOwnerUid = -1;
        this.mExpectedBatchedScans = 0;
        this.mBatchedScanMinPollTime = 0L;
        this.mLastSignalLevel = -1;
        this.mEnableRssiPolling = false;
        this.mEnableBackgroundScan = false;
        this.mRssiPollToken = 0;
        this.mReconnectCount = 0;
        this.mOperationalMode = 1;
        this.mScanResultIsPending = false;
        this.mScanWorkSource = null;
        this.mScreenBroadcastReceived = new AtomicBoolean(false);
        this.mBluetoothConnectionActive = false;
        this.mSupplicantRestartCount = 0;
        this.mSupplicantStopFailureToken = 0;
        this.mTetherToken = 0;
        this.mDriverStartToken = 0;
        this.mPeriodicScanToken = 0;
        this.mDhcpResultsLock = new Object();
        this.mDhcpActive = false;
        this.mFrequencyBand = new AtomicInteger(0);
        this.mFilteringMulticastV4Packets = new AtomicBoolean(true);
        this.mReplyChannel = new AsyncChannel();
        this.mSuspendOptNeedsDisabled = 0;
        this.mUserWantsSuspendOpt = new AtomicBoolean(true);
        this.mInDelayedStop = false;
        this.mDefaultState = new DefaultState();
        this.mInitialState = new InitialState();
        this.mSupplicantStartingState = new SupplicantStartingState();
        this.mSupplicantStartedState = new SupplicantStartedState();
        this.mSupplicantStoppingState = new SupplicantStoppingState();
        this.mDriverStartingState = new DriverStartingState();
        this.mDriverStartedState = new DriverStartedState();
        this.mWaitForP2pDisableState = new WaitForP2pDisableState();
        this.mDriverStoppingState = new DriverStoppingState();
        this.mDriverStoppedState = new DriverStoppedState();
        this.mScanModeState = new ScanModeState();
        this.mConnectModeState = new ConnectModeState();
        this.mL2ConnectedState = new L2ConnectedState();
        this.mObtainingIpState = new ObtainingIpState();
        this.mVerifyingLinkState = new VerifyingLinkState();
        this.mCaptivePortalCheckState = new CaptivePortalCheckState();
        this.mConnectedState = new ConnectedState();
        this.mDisconnectingState = new DisconnectingState();
        this.mDisconnectedState = new DisconnectedState();
        this.mWpsRunningState = new WpsRunningState();
        this.mSoftApStartingState = new SoftApStartingState();
        this.mSoftApStartedState = new SoftApStartedState();
        this.mTetheringState = new TetheringState();
        this.mTetheredState = new TetheredState();
        this.mUntetheringState = new UntetheringState();
        this.mWifiState = new AtomicInteger(1);
        this.mWifiApState = new AtomicInteger(11);
        this.mIsRunning = false;
        this.mReportedRunning = false;
        this.mRunningWifiUids = new WorkSource();
        this.mLastRunningWifiUids = new WorkSource();
        this.mBatchedScanSettings = null;
        this.mContext = context;
        this.mInterfaceName = wlanInterface;
        this.mNetworkInfo = new NetworkInfo(1, 0, NETWORKTYPE, "");
        this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService(BatteryStats.SERVICE_NAME));
        IBinder b = ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE);
        this.mNwService = INetworkManagementService.Stub.asInterface(b);
        this.mP2pSupported = this.mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT);
        this.mWifiNative = new WifiNative(this.mInterfaceName);
        this.mWifiConfigStore = new WifiConfigStore(context, this.mWifiNative);
        this.mWifiMonitor = new WifiMonitor(this, this.mWifiNative);
        this.mWifiInfo = new WifiInfo();
        this.mSupplicantStateTracker = new SupplicantStateTracker(context, this, this.mWifiConfigStore, getHandler());
        this.mLinkProperties = new LinkProperties();
        this.mNetlinkLinkProperties = new LinkProperties();
        this.mWifiP2pManager = (WifiP2pManager) this.mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        this.mNetworkInfo.setIsAvailable(false);
        this.mLastBssid = null;
        this.mLastNetworkId = -1;
        this.mLastSignalLevel = -1;
        this.mInterfaceObserver = new InterfaceObserver(this);
        try {
            this.mNwService.registerObserver(this.mInterfaceObserver);
        } catch (RemoteException e) {
            loge("Couldn't register interface observer: " + e.toString());
        }
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        Intent scanIntent = new Intent(ACTION_START_SCAN, (Uri) null);
        this.mScanIntent = PendingIntent.getBroadcast(this.mContext, 0, scanIntent, 0);
        Intent batchedIntent = new Intent(ACTION_REFRESH_BATCHED_SCAN, (Uri) null);
        this.mBatchedScanIntervalIntent = PendingIntent.getBroadcast(this.mContext, 0, batchedIntent, 0);
        this.mDefaultFrameworkScanIntervalMs = this.mContext.getResources().getInteger(R.integer.config_wifi_framework_scan_interval);
        this.mDriverStopDelayMs = this.mContext.getResources().getInteger(R.integer.config_wifi_driver_stop_delay);
        this.mBackgroundScanSupported = this.mContext.getResources().getBoolean(R.bool.config_wifi_background_scan_support);
        this.mPrimaryDeviceType = this.mContext.getResources().getString(R.string.config_wifi_p2p_device_type);
        this.mUserWantsSuspendOpt.set(Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.WIFI_SUSPEND_OPTIMIZATIONS_ENABLED, 1) == 1);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.net.wifi.WifiStateMachine.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                ArrayList<String> available = intent.getStringArrayListExtra(ConnectivityManager.EXTRA_AVAILABLE_TETHER);
                ArrayList<String> active = intent.getStringArrayListExtra(ConnectivityManager.EXTRA_ACTIVE_TETHER);
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_TETHER_STATE_CHANGE, new TetherStateChange(available, active));
            }
        }, new IntentFilter(ConnectivityManager.ACTION_TETHER_STATE_CHANGED));
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.net.wifi.WifiStateMachine.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiStateMachine.this.startScan(-1, null);
            }
        }, new IntentFilter(ACTION_START_SCAN));
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_REFRESH_BATCHED_SCAN);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.net.wifi.WifiStateMachine.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_SCREEN_ON)) {
                    WifiStateMachine.this.handleScreenStateChanged(true);
                } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    WifiStateMachine.this.handleScreenStateChanged(false);
                } else if (action.equals(WifiStateMachine.ACTION_REFRESH_BATCHED_SCAN)) {
                    WifiStateMachine.this.startNextBatchedScanAsync();
                }
            }
        }, filter);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.net.wifi.WifiStateMachine.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int counter = intent.getIntExtra(WifiStateMachine.DELAYED_STOP_COUNTER, 0);
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_DELAYED_STOP_DRIVER, counter, 0);
            }
        }, new IntentFilter(ACTION_DELAYED_DRIVER_STOP));
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(Settings.Global.WIFI_SUSPEND_OPTIMIZATIONS_ENABLED), false, new ContentObserver(getHandler()) { // from class: android.net.wifi.WifiStateMachine.5
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                WifiStateMachine.this.mUserWantsSuspendOpt.set(Settings.Global.getInt(WifiStateMachine.this.mContext.getContentResolver(), Settings.Global.WIFI_SUSPEND_OPTIMIZATIONS_ENABLED, 1) == 1);
            }
        });
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.net.wifi.WifiStateMachine.6
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_BOOT_COMPLETED);
            }
        }, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        this.mScanResultCache = new LruCache<>(80);
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = powerManager.newWakeLock(1, getName());
        this.mSuspendWakeLock = powerManager.newWakeLock(1, "WifiSuspend");
        this.mSuspendWakeLock.setReferenceCounted(false);
        addState(this.mDefaultState);
        addState(this.mInitialState, this.mDefaultState);
        addState(this.mSupplicantStartingState, this.mDefaultState);
        addState(this.mSupplicantStartedState, this.mDefaultState);
        addState(this.mDriverStartingState, this.mSupplicantStartedState);
        addState(this.mDriverStartedState, this.mSupplicantStartedState);
        addState(this.mScanModeState, this.mDriverStartedState);
        addState(this.mConnectModeState, this.mDriverStartedState);
        addState(this.mL2ConnectedState, this.mConnectModeState);
        addState(this.mObtainingIpState, this.mL2ConnectedState);
        addState(this.mVerifyingLinkState, this.mL2ConnectedState);
        addState(this.mCaptivePortalCheckState, this.mL2ConnectedState);
        addState(this.mConnectedState, this.mL2ConnectedState);
        addState(this.mDisconnectingState, this.mConnectModeState);
        addState(this.mDisconnectedState, this.mConnectModeState);
        addState(this.mWpsRunningState, this.mConnectModeState);
        addState(this.mWaitForP2pDisableState, this.mSupplicantStartedState);
        addState(this.mDriverStoppingState, this.mSupplicantStartedState);
        addState(this.mDriverStoppedState, this.mSupplicantStartedState);
        addState(this.mSupplicantStoppingState, this.mDefaultState);
        addState(this.mSoftApStartingState, this.mDefaultState);
        addState(this.mSoftApStartedState, this.mDefaultState);
        addState(this.mTetheringState, this.mSoftApStartedState);
        addState(this.mTetheredState, this.mSoftApStartedState);
        addState(this.mUntetheringState, this.mSoftApStartedState);
        setInitialState(this.mInitialState);
        setLogRecSize(2000);
        setLogOnlyTransitions(false);
        start();
        Intent intent = new Intent(WifiManager.WIFI_SCAN_AVAILABLE);
        intent.addFlags(67108864);
        intent.putExtra(WifiManager.EXTRA_SCAN_AVAILABLE, 1);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    public Messenger getMessenger() {
        return new Messenger(getHandler());
    }

    public boolean syncPingSupplicant(AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_PING_SUPPLICANT);
        boolean result = resultMsg.arg1 != -1;
        resultMsg.recycle();
        return result;
    }

    public void startScan(int callingUid, WorkSource workSource) {
        sendMessage(CMD_START_SCAN, callingUid, 0, workSource);
    }

    public void setBatchedScanSettings(BatchedScanSettings settings, int callingUid) {
        sendMessage(CMD_SET_BATCHED_SCAN, callingUid, 0, settings);
    }

    public List<BatchedScanResult> syncGetBatchedScanResultsList() {
        List<BatchedScanResult> batchedScanList;
        synchronized (this.mBatchedScanResults) {
            batchedScanList = new ArrayList<>(this.mBatchedScanResults.size());
            for (BatchedScanResult result : this.mBatchedScanResults) {
                batchedScanList.add(new BatchedScanResult(result));
            }
        }
        return batchedScanList;
    }

    public void requestBatchedScanPoll() {
        sendMessage(CMD_POLL_BATCHED_SCAN);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startBatchedScan() {
        if (this.mDhcpActive) {
            return;
        }
        retrieveBatchedScanData();
        this.mAlarmManager.cancel(this.mBatchedScanIntervalIntent);
        String scansExpected = this.mWifiNative.setBatchedScanSettings(this.mBatchedScanSettings);
        try {
            this.mExpectedBatchedScans = Integer.parseInt(scansExpected);
            setNextBatchedAlarm(this.mExpectedBatchedScans);
        } catch (NumberFormatException e) {
            stopBatchedScan();
            loge("Exception parsing WifiNative.setBatchedScanSettings response " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNextBatchedScanAsync() {
        sendMessage(CMD_START_NEXT_BATCHED_SCAN);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNextBatchedScan() {
        retrieveBatchedScanData();
        setNextBatchedAlarm(this.mExpectedBatchedScans);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBatchedScanPollRequest() {
        if (this.mBatchedScanMinPollTime == 0 || this.mBatchedScanSettings == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now > this.mBatchedScanMinPollTime) {
            startNextBatchedScan();
            return;
        }
        this.mAlarmManager.setExact(0, this.mBatchedScanMinPollTime, this.mBatchedScanIntervalIntent);
        this.mBatchedScanMinPollTime = 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean recordBatchedScanSettings(BatchedScanSettings settings) {
        if (settings != null) {
            if (settings.equals(this.mBatchedScanSettings)) {
                return false;
            }
        } else if (this.mBatchedScanSettings == null) {
            return false;
        }
        this.mBatchedScanSettings = settings;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopBatchedScan() {
        this.mAlarmManager.cancel(this.mBatchedScanIntervalIntent);
        if (this.mBatchedScanSettings != null) {
            retrieveBatchedScanData();
            this.mWifiNative.setBatchedScanSettings(null);
        }
    }

    private void setNextBatchedAlarm(int scansExpected) {
        if (this.mBatchedScanSettings == null || scansExpected < 1) {
            return;
        }
        this.mBatchedScanMinPollTime = System.currentTimeMillis() + (this.mBatchedScanSettings.scanIntervalSec * 1000);
        if (this.mBatchedScanSettings.maxScansPerBatch < scansExpected) {
            scansExpected = this.mBatchedScanSettings.maxScansPerBatch;
        }
        int secToFull = this.mBatchedScanSettings.scanIntervalSec;
        int secToFull2 = secToFull * scansExpected;
        int debugPeriod = SystemProperties.getInt("wifi.batchedScan.pollPeriod", 0);
        if (debugPeriod > 0) {
            secToFull2 = debugPeriod;
        }
        this.mAlarmManager.setExact(0, System.currentTimeMillis() + ((secToFull2 - (this.mBatchedScanSettings.scanIntervalSec / 2)) * 1000), this.mBatchedScanIntervalIntent);
    }

    private void retrieveBatchedScanData() {
        String rawData = this.mWifiNative.getBatchedScanResults();
        this.mBatchedScanMinPollTime = 0L;
        if (rawData == null || rawData.equalsIgnoreCase("OK")) {
            loge("Unexpected BatchedScanResults :" + rawData);
            return;
        }
        int scanCount = 0;
        String[] splitData = rawData.split(Separators.RETURN);
        int n = 0;
        if (splitData[0].startsWith("scancount=")) {
            try {
                n = 0 + 1;
                scanCount = Integer.parseInt(splitData[0].substring("scancount=".length()));
            } catch (NumberFormatException e) {
                loge("scancount parseInt Exception from " + splitData[n]);
            }
        } else {
            log("scancount not found");
        }
        if (scanCount == 0) {
            loge("scanCount==0 - aborting");
            return;
        }
        Intent intent = new Intent(WifiManager.BATCHED_SCAN_RESULTS_AVAILABLE_ACTION);
        intent.addFlags(67108864);
        synchronized (this.mBatchedScanResults) {
            this.mBatchedScanResults.clear();
            BatchedScanResult batchedScanResult = new BatchedScanResult();
            String bssid = null;
            WifiSsid wifiSsid = null;
            int level = 0;
            int freq = 0;
            long tsf = 0;
            int distSd = -1;
            int dist = -1;
            long now = SystemClock.elapsedRealtime();
            int bssidStrLen = BSSID_STR.length();
            while (true) {
                if (n < splitData.length) {
                    if (splitData[n].equals("----")) {
                        if (n + 1 != splitData.length) {
                            loge("didn't consume " + (splitData.length - n));
                        }
                        if (this.mBatchedScanResults.size() > 0) {
                            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
                        }
                        logd("retrieveBatchedScanResults X");
                        return;
                    }
                    if (splitData[n].equals(END_STR) || splitData[n].equals(DELIMITER_STR)) {
                        if (bssid != null) {
                            batchedScanResult.scanResults.add(new ScanResult(wifiSsid, bssid, "", level, freq, tsf, dist, distSd));
                            wifiSsid = null;
                            bssid = null;
                            level = 0;
                            freq = 0;
                            tsf = 0;
                            distSd = -1;
                            dist = -1;
                        }
                        if (splitData[n].equals(END_STR)) {
                            if (batchedScanResult.scanResults.size() != 0) {
                                this.mBatchedScanResults.add(batchedScanResult);
                                batchedScanResult = new BatchedScanResult();
                            } else {
                                logd("Found empty batch");
                            }
                        }
                    } else if (splitData[n].equals("trunc")) {
                        batchedScanResult.truncated = true;
                    } else if (splitData[n].startsWith(BSSID_STR)) {
                        bssid = new String(splitData[n].getBytes(), bssidStrLen, splitData[n].length() - bssidStrLen);
                    } else {
                        if (splitData[n].startsWith(FREQ_STR)) {
                            try {
                                freq = Integer.parseInt(splitData[n].substring(FREQ_STR.length()));
                            } catch (NumberFormatException e2) {
                                loge("Invalid freqency: " + splitData[n]);
                                freq = 0;
                            }
                        } else if (splitData[n].startsWith("age=")) {
                            try {
                                long tsf2 = now - Long.parseLong(splitData[n].substring("age=".length()));
                                tsf = tsf2 * 1000;
                            } catch (NumberFormatException e3) {
                                loge("Invalid timestamp: " + splitData[n]);
                                tsf = 0;
                            }
                        } else if (splitData[n].startsWith(SSID_STR)) {
                            wifiSsid = WifiSsid.createFromAsciiEncoded(splitData[n].substring(SSID_STR.length()));
                        } else if (splitData[n].startsWith(LEVEL_STR)) {
                            try {
                                level = Integer.parseInt(splitData[n].substring(LEVEL_STR.length()));
                                if (level > 0) {
                                    level -= 256;
                                }
                            } catch (NumberFormatException e4) {
                                loge("Invalid level: " + splitData[n]);
                                level = 0;
                            }
                        } else if (splitData[n].startsWith("dist=")) {
                            try {
                                dist = Integer.parseInt(splitData[n].substring("dist=".length()));
                            } catch (NumberFormatException e5) {
                                loge("Invalid distance: " + splitData[n]);
                                dist = -1;
                            }
                        } else if (splitData[n].startsWith("distSd=")) {
                            try {
                                distSd = Integer.parseInt(splitData[n].substring("distSd=".length()));
                            } catch (NumberFormatException e6) {
                                loge("Invalid distanceSd: " + splitData[n]);
                                distSd = -1;
                            }
                        } else {
                            loge("Unable to parse batched scan result line: " + splitData[n]);
                        }
                    }
                    n++;
                } else {
                    String rawData2 = this.mWifiNative.getBatchedScanResults();
                    if (rawData2 == null) {
                        loge("Unexpected null BatchedScanResults");
                        return;
                    }
                    splitData = rawData2.split(Separators.RETURN);
                    if (splitData.length == 0 || splitData[0].equals("ok")) {
                        break;
                    }
                    n = 0;
                }
            }
            loge("batch scan results just ended!");
            if (this.mBatchedScanResults.size() > 0) {
                this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void noteScanStart(int callingUid, WorkSource workSource) {
        if (this.mScanWorkSource == null) {
            if (callingUid != -1 || workSource != null) {
                this.mScanWorkSource = workSource != null ? workSource : new WorkSource(callingUid);
                try {
                    this.mBatteryStats.noteWifiScanStartedFromSource(this.mScanWorkSource);
                } catch (RemoteException e) {
                    log(e.toString());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void noteScanEnd() {
        if (this.mScanWorkSource != null) {
            try {
                try {
                    this.mBatteryStats.noteWifiScanStoppedFromSource(this.mScanWorkSource);
                    this.mScanWorkSource = null;
                } catch (RemoteException e) {
                    log(e.toString());
                    this.mScanWorkSource = null;
                }
            } catch (Throwable th) {
                this.mScanWorkSource = null;
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startScanNative(int type) {
        this.mWifiNative.scan(type);
        this.mScanResultIsPending = true;
    }

    public void setSupplicantRunning(boolean enable) {
        if (enable) {
            sendMessage(CMD_START_SUPPLICANT);
        } else {
            sendMessage(CMD_STOP_SUPPLICANT);
        }
    }

    public void setHostApRunning(WifiConfiguration wifiConfig, boolean enable) {
        if (enable) {
            sendMessage(CMD_START_AP, wifiConfig);
        } else {
            sendMessage(CMD_STOP_AP);
        }
    }

    public void setWifiApConfiguration(WifiConfiguration config) {
        this.mWifiApConfigChannel.sendMessage(CMD_SET_AP_CONFIG, config);
    }

    public WifiConfiguration syncGetWifiApConfiguration() {
        Message resultMsg = this.mWifiApConfigChannel.sendMessageSynchronously(CMD_REQUEST_AP_CONFIG);
        WifiConfiguration ret = (WifiConfiguration) resultMsg.obj;
        resultMsg.recycle();
        return ret;
    }

    public int syncGetWifiState() {
        return this.mWifiState.get();
    }

    public String syncGetWifiStateByName() {
        switch (this.mWifiState.get()) {
            case 0:
                return "disabling";
            case 1:
                return "disabled";
            case 2:
                return "enabling";
            case 3:
                return "enabled";
            case 4:
                return "unknown state";
            default:
                return "[invalid state]";
        }
    }

    public int syncGetWifiApState() {
        return this.mWifiApState.get();
    }

    public String syncGetWifiApStateByName() {
        switch (this.mWifiApState.get()) {
            case 10:
                return "disabling";
            case 11:
                return "disabled";
            case 12:
                return "enabling";
            case 13:
                return "enabled";
            case 14:
                return "failed";
            default:
                return "[invalid state]";
        }
    }

    public WifiInfo syncRequestConnectionInfo() {
        return this.mWifiInfo;
    }

    public DhcpResults syncGetDhcpResults() {
        DhcpResults dhcpResults;
        synchronized (this.mDhcpResultsLock) {
            dhcpResults = new DhcpResults(this.mDhcpResults);
        }
        return dhcpResults;
    }

    public void setDriverStart(boolean enable) {
        if (enable) {
            sendMessage(CMD_START_DRIVER);
        } else {
            sendMessage(CMD_STOP_DRIVER);
        }
    }

    public void captivePortalCheckComplete() {
        sendMessage(CMD_CAPTIVE_CHECK_COMPLETE);
    }

    public void setOperationalMode(int mode) {
        sendMessage(CMD_SET_OPERATIONAL_MODE, mode, 0);
    }

    public List<ScanResult> syncGetScanResultsList() {
        List<ScanResult> scanList;
        synchronized (this.mScanResultCache) {
            scanList = new ArrayList<>();
            for (ScanResult result : this.mScanResults) {
                scanList.add(new ScanResult(result));
            }
        }
        return scanList;
    }

    public void disconnectCommand() {
        sendMessage(CMD_DISCONNECT);
    }

    public void reconnectCommand() {
        sendMessage(CMD_RECONNECT);
    }

    public void reassociateCommand() {
        sendMessage(CMD_REASSOCIATE);
    }

    public void reloadTlsNetworksAndReconnect() {
        sendMessage(CMD_RELOAD_TLS_AND_RECONNECT);
    }

    public int syncAddOrUpdateNetwork(AsyncChannel channel, WifiConfiguration config) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_ADD_OR_UPDATE_NETWORK, config);
        int result = resultMsg.arg1;
        resultMsg.recycle();
        return result;
    }

    public List<WifiConfiguration> syncGetConfiguredNetworks(AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_GET_CONFIGURED_NETWORKS);
        List<WifiConfiguration> result = (List) resultMsg.obj;
        resultMsg.recycle();
        return result;
    }

    public boolean syncRemoveNetwork(AsyncChannel channel, int networkId) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_REMOVE_NETWORK, networkId);
        boolean result = resultMsg.arg1 != -1;
        resultMsg.recycle();
        return result;
    }

    public boolean syncEnableNetwork(AsyncChannel channel, int netId, boolean disableOthers) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_ENABLE_NETWORK, netId, disableOthers ? 1 : 0);
        boolean result = resultMsg.arg1 != -1;
        resultMsg.recycle();
        return result;
    }

    public boolean syncDisableNetwork(AsyncChannel channel, int netId) {
        Message resultMsg = channel.sendMessageSynchronously(WifiManager.DISABLE_NETWORK, netId);
        boolean result = resultMsg.arg1 != 151570;
        resultMsg.recycle();
        return result;
    }

    public void addToBlacklist(String bssid) {
        sendMessage(CMD_BLACKLIST_NETWORK, bssid);
    }

    public void clearBlacklist() {
        sendMessage(CMD_CLEAR_BLACKLIST);
    }

    public void enableRssiPolling(boolean enabled) {
        sendMessage(CMD_ENABLE_RSSI_POLL, enabled ? 1 : 0, 0);
    }

    public void enableBackgroundScanCommand(boolean enabled) {
        sendMessage(CMD_ENABLE_BACKGROUND_SCAN, enabled ? 1 : 0, 0);
    }

    public void enableAllNetworks() {
        sendMessage(CMD_ENABLE_ALL_NETWORKS);
    }

    public void startFilteringMulticastV4Packets() {
        this.mFilteringMulticastV4Packets.set(true);
        sendMessage(CMD_START_PACKET_FILTERING, 0, 0);
    }

    public void stopFilteringMulticastV4Packets() {
        this.mFilteringMulticastV4Packets.set(false);
        sendMessage(CMD_STOP_PACKET_FILTERING, 0, 0);
    }

    public void startFilteringMulticastV6Packets() {
        sendMessage(CMD_START_PACKET_FILTERING, 1, 0);
    }

    public void stopFilteringMulticastV6Packets() {
        sendMessage(CMD_STOP_PACKET_FILTERING, 1, 0);
    }

    public void setHighPerfModeEnabled(boolean enable) {
        sendMessage(CMD_SET_HIGH_PERF_MODE, enable ? 1 : 0, 0);
    }

    public void setCountryCode(String countryCode, boolean persist) {
        if (persist) {
            this.mPersistedCountryCode = countryCode;
            Settings.Global.putString(this.mContext.getContentResolver(), Settings.Global.WIFI_COUNTRY_CODE, countryCode);
        }
        sendMessage(CMD_SET_COUNTRY_CODE, countryCode);
        this.mWifiP2pChannel.sendMessage(WifiP2pService.SET_COUNTRY_CODE, countryCode);
    }

    public void setFrequencyBand(int band, boolean persist) {
        if (persist) {
            Settings.Global.putInt(this.mContext.getContentResolver(), Settings.Global.WIFI_FREQUENCY_BAND, band);
        }
        sendMessage(CMD_SET_FREQUENCY_BAND, band, 0);
    }

    public void enableTdls(String remoteMacAddress, boolean enable) {
        int enabler = enable ? 1 : 0;
        sendMessage(CMD_ENABLE_TDLS, enabler, 0, remoteMacAddress);
    }

    public int getFrequencyBand() {
        return this.mFrequencyBand.get();
    }

    public String getConfigFile() {
        return this.mWifiConfigStore.getConfigFile();
    }

    public void sendBluetoothAdapterStateChange(int state) {
        sendMessage(CMD_BLUETOOTH_ADAPTER_STATE_CHANGE, state, 0);
    }

    public boolean syncSaveConfig(AsyncChannel channel) {
        Message resultMsg = channel.sendMessageSynchronously(CMD_SAVE_CONFIG);
        boolean result = resultMsg.arg1 != -1;
        resultMsg.recycle();
        return result;
    }

    public void updateBatteryWorkSource(WorkSource newSource) {
        synchronized (this.mRunningWifiUids) {
            if (newSource != null) {
                try {
                    this.mRunningWifiUids.set(newSource);
                } catch (RemoteException e) {
                }
            }
            if (this.mIsRunning) {
                if (this.mReportedRunning) {
                    if (this.mLastRunningWifiUids.diff(this.mRunningWifiUids)) {
                        this.mBatteryStats.noteWifiRunningChanged(this.mLastRunningWifiUids, this.mRunningWifiUids);
                        this.mLastRunningWifiUids.set(this.mRunningWifiUids);
                    }
                } else {
                    this.mBatteryStats.noteWifiRunning(this.mRunningWifiUids);
                    this.mLastRunningWifiUids.set(this.mRunningWifiUids);
                    this.mReportedRunning = true;
                }
            } else if (this.mReportedRunning) {
                this.mBatteryStats.noteWifiStopped(this.mLastRunningWifiUids);
                this.mLastRunningWifiUids.clear();
                this.mReportedRunning = false;
            }
            this.mWakeLock.setWorkSource(newSource);
        }
    }

    @Override // com.android.internal.util.StateMachine
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        super.dump(fd, pw, args);
        this.mSupplicantStateTracker.dump(fd, pw, args);
        pw.println("mLinkProperties " + this.mLinkProperties);
        pw.println("mWifiInfo " + this.mWifiInfo);
        pw.println("mDhcpResults " + this.mDhcpResults);
        pw.println("mNetworkInfo " + this.mNetworkInfo);
        pw.println("mLastSignalLevel " + this.mLastSignalLevel);
        pw.println("mLastBssid " + this.mLastBssid);
        pw.println("mLastNetworkId " + this.mLastNetworkId);
        pw.println("mReconnectCount " + this.mReconnectCount);
        pw.println("mOperationalMode " + this.mOperationalMode);
        pw.println("mUserWantsSuspendOpt " + this.mUserWantsSuspendOpt);
        pw.println("mSuspendOptNeedsDisabled " + this.mSuspendOptNeedsDisabled);
        pw.println("Supplicant status " + this.mWifiNative.status());
        pw.println("mEnableBackgroundScan " + this.mEnableBackgroundScan);
        pw.println();
        this.mWifiConfigStore.dump(fd, pw, args);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScreenStateChanged(boolean screenOn) {
        enableRssiPolling(screenOn);
        if (this.mBackgroundScanSupported) {
            enableBackgroundScanCommand(!screenOn);
        }
        if (screenOn) {
            enableAllNetworks();
        }
        if (this.mUserWantsSuspendOpt.get()) {
            if (screenOn) {
                sendMessage(CMD_SET_SUSPEND_OPT_ENABLED, 0, 0);
            } else {
                this.mSuspendWakeLock.acquire(2000L);
                sendMessage(CMD_SET_SUSPEND_OPT_ENABLED, 1, 0);
            }
        }
        this.mScreenBroadcastReceived.set(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkAndSetConnectivityInstance() {
        if (this.mCm == null) {
            this.mCm = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean startTethering(ArrayList<String> available) {
        checkAndSetConnectivityInstance();
        String[] wifiRegexs = this.mCm.getTetherableWifiRegexs();
        Iterator i$ = available.iterator();
        while (i$.hasNext()) {
            String intf = i$.next();
            for (String regex : wifiRegexs) {
                if (intf.matches(regex)) {
                    try {
                        InterfaceConfiguration ifcg = this.mNwService.getInterfaceConfig(intf);
                        if (ifcg != null) {
                            ifcg.setLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress("192.168.43.1"), 24));
                            ifcg.setInterfaceUp();
                            this.mNwService.setInterfaceConfig(intf, ifcg);
                        }
                        if (this.mCm.tether(intf) != 0) {
                            loge("Error tethering on " + intf);
                            return false;
                        }
                        this.mTetherInterfaceName = intf;
                        return true;
                    } catch (Exception e) {
                        loge("Error configuring interface " + intf + ", :" + e);
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopTethering() {
        checkAndSetConnectivityInstance();
        try {
            InterfaceConfiguration ifcg = this.mNwService.getInterfaceConfig(this.mTetherInterfaceName);
            if (ifcg != null) {
                ifcg.setLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress("0.0.0.0"), 0));
                this.mNwService.setInterfaceConfig(this.mTetherInterfaceName, ifcg);
            }
        } catch (Exception e) {
            loge("Error resetting interface " + this.mTetherInterfaceName + ", :" + e);
        }
        if (this.mCm.untether(this.mTetherInterfaceName) != 0) {
            loge("Untether initiate failed!");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isWifiTethered(ArrayList<String> active) {
        checkAndSetConnectivityInstance();
        String[] wifiRegexs = this.mCm.getTetherableWifiRegexs();
        Iterator i$ = active.iterator();
        while (i$.hasNext()) {
            String intf = i$.next();
            for (String regex : wifiRegexs) {
                if (intf.matches(regex)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCountryCode() {
        String countryCode = Settings.Global.getString(this.mContext.getContentResolver(), Settings.Global.WIFI_COUNTRY_CODE);
        if (countryCode != null && !countryCode.isEmpty()) {
            setCountryCode(countryCode, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFrequencyBand() {
        int band = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.WIFI_FREQUENCY_BAND, 0);
        setFrequencyBand(band, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSuspendOptimizationsNative(int reason, boolean enabled) {
        if (enabled) {
            this.mSuspendOptNeedsDisabled &= reason ^ (-1);
            if (this.mSuspendOptNeedsDisabled == 0 && this.mUserWantsSuspendOpt.get()) {
                this.mWifiNative.setSuspendOptimizations(true);
                return;
            }
            return;
        }
        this.mSuspendOptNeedsDisabled |= reason;
        this.mWifiNative.setSuspendOptimizations(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSuspendOptimizations(int reason, boolean enabled) {
        if (enabled) {
            this.mSuspendOptNeedsDisabled &= reason ^ (-1);
        } else {
            this.mSuspendOptNeedsDisabled |= reason;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWifiState(int wifiState) {
        int previousWifiState = this.mWifiState.get();
        try {
        } catch (RemoteException e) {
            loge("Failed to note battery stats in wifi");
        }
        if (wifiState == 3) {
            this.mBatteryStats.noteWifiOn();
        } else {
            if (wifiState == 1) {
                this.mBatteryStats.noteWifiOff();
            }
            this.mWifiState.set(wifiState);
            Intent intent = new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intent.addFlags(67108864);
            intent.putExtra("wifi_state", wifiState);
            intent.putExtra("previous_wifi_state", previousWifiState);
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }
        this.mWifiState.set(wifiState);
        Intent intent2 = new Intent(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intent2.addFlags(67108864);
        intent2.putExtra("wifi_state", wifiState);
        intent2.putExtra("previous_wifi_state", previousWifiState);
        this.mContext.sendStickyBroadcastAsUser(intent2, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWifiApState(int wifiApState) {
        int previousWifiApState = this.mWifiApState.get();
        try {
        } catch (RemoteException e) {
            loge("Failed to note battery stats in wifi");
        }
        if (wifiApState == 13) {
            this.mBatteryStats.noteWifiOn();
        } else {
            if (wifiApState == 11) {
                this.mBatteryStats.noteWifiOff();
            }
            this.mWifiApState.set(wifiApState);
            Intent intent = new Intent(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
            intent.addFlags(67108864);
            intent.putExtra("wifi_state", wifiApState);
            intent.putExtra("previous_wifi_state", previousWifiApState);
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }
        this.mWifiApState.set(wifiApState);
        Intent intent2 = new Intent(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        intent2.addFlags(67108864);
        intent2.putExtra("wifi_state", wifiApState);
        intent2.putExtra("previous_wifi_state", previousWifiApState);
        this.mContext.sendStickyBroadcastAsUser(intent2, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScanResults() {
        String bssid = "";
        int level = 0;
        int freq = 0;
        long tsf = 0;
        String flags = "";
        WifiSsid wifiSsid = null;
        StringBuffer scanResultsBuf = new StringBuffer();
        int sid = 0;
        do {
            String tmpResults = this.mWifiNative.scanResults(sid);
            if (TextUtils.isEmpty(tmpResults)) {
                break;
            }
            scanResultsBuf.append(tmpResults);
            scanResultsBuf.append(Separators.RETURN);
            String[] lines = tmpResults.split(Separators.RETURN);
            sid = -1;
            int i = lines.length - 1;
            while (true) {
                if (i < 0 || lines[i].startsWith(END_STR)) {
                    break;
                } else if (lines[i].startsWith(ID_STR)) {
                    try {
                        sid = Integer.parseInt(lines[i].substring(ID_STR.length())) + 1;
                        break;
                    } catch (NumberFormatException e) {
                    }
                } else {
                    i--;
                }
            }
        } while (sid != -1);
        String scanResults = scanResultsBuf.toString();
        if (TextUtils.isEmpty(scanResults)) {
            return;
        }
        synchronized (this.mScanResultCache) {
            this.mScanResults = new ArrayList();
            String[] lines2 = scanResults.split(Separators.RETURN);
            int bssidStrLen = BSSID_STR.length();
            int flagLen = FLAGS_STR.length();
            for (String line : lines2) {
                if (line.startsWith(BSSID_STR)) {
                    bssid = new String(line.getBytes(), bssidStrLen, line.length() - bssidStrLen);
                } else if (line.startsWith(FREQ_STR)) {
                    try {
                        freq = Integer.parseInt(line.substring(FREQ_STR.length()));
                    } catch (NumberFormatException e2) {
                        freq = 0;
                    }
                } else if (line.startsWith(LEVEL_STR)) {
                    try {
                        level = Integer.parseInt(line.substring(LEVEL_STR.length()));
                        if (level > 0) {
                            level -= 256;
                        }
                    } catch (NumberFormatException e3) {
                        level = 0;
                    }
                } else if (line.startsWith(TSF_STR)) {
                    try {
                        tsf = Long.parseLong(line.substring(TSF_STR.length()));
                    } catch (NumberFormatException e4) {
                        tsf = 0;
                    }
                } else if (line.startsWith(FLAGS_STR)) {
                    flags = new String(line.getBytes(), flagLen, line.length() - flagLen);
                } else if (line.startsWith(SSID_STR)) {
                    wifiSsid = WifiSsid.createFromAsciiEncoded(line.substring(SSID_STR.length()));
                } else if (line.startsWith(DELIMITER_STR) || line.startsWith(END_STR)) {
                    if (bssid != null) {
                        String ssid = wifiSsid != null ? wifiSsid.toString() : WifiSsid.NONE;
                        String key = bssid + ssid;
                        ScanResult scanResult = this.mScanResultCache.get(key);
                        if (scanResult != null) {
                            scanResult.level = level;
                            scanResult.wifiSsid = wifiSsid;
                            scanResult.SSID = wifiSsid != null ? wifiSsid.toString() : WifiSsid.NONE;
                            scanResult.capabilities = flags;
                            scanResult.frequency = freq;
                            scanResult.timestamp = tsf;
                        } else {
                            scanResult = new ScanResult(wifiSsid, bssid, flags, level, freq, tsf);
                            this.mScanResultCache.put(key, scanResult);
                        }
                        this.mScanResults.add(scanResult);
                    }
                    bssid = null;
                    level = 0;
                    freq = 0;
                    tsf = 0;
                    flags = "";
                    wifiSsid = null;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchRssiAndLinkSpeedNative() {
        int newRssi = -1;
        int newLinkSpeed = -1;
        String signalPoll = this.mWifiNative.signalPoll();
        if (signalPoll != null) {
            String[] lines = signalPoll.split(Separators.RETURN);
            for (String line : lines) {
                String[] prop = line.split(Separators.EQUALS);
                if (prop.length >= 2) {
                    try {
                        if (prop[0].equals("RSSI")) {
                            newRssi = Integer.parseInt(prop[1]);
                        } else if (prop[0].equals("LINKSPEED")) {
                            newLinkSpeed = Integer.parseInt(prop[1]);
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        if (newRssi != -1 && -200 < newRssi && newRssi < 256) {
            if (newRssi > 0) {
                newRssi -= 256;
            }
            this.mWifiInfo.setRssi(newRssi);
            int newSignalLevel = WifiManager.calculateSignalLevel(newRssi, 5);
            if (newSignalLevel != this.mLastSignalLevel) {
                sendRssiChangeBroadcast(newRssi);
            }
            this.mLastSignalLevel = newSignalLevel;
        } else {
            this.mWifiInfo.setRssi(-200);
        }
        if (newLinkSpeed != -1) {
            this.mWifiInfo.setLinkSpeed(newLinkSpeed);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchPktcntNative(RssiPacketCountInfo info) {
        String pktcntPoll = this.mWifiNative.pktcntPoll();
        if (pktcntPoll != null) {
            String[] lines = pktcntPoll.split(Separators.RETURN);
            for (String line : lines) {
                String[] prop = line.split(Separators.EQUALS);
                if (prop.length >= 2) {
                    try {
                        if (prop[0].equals("TXGOOD")) {
                            info.txgood = Integer.parseInt(prop[1]);
                        } else if (prop[0].equals("TXBAD")) {
                            info.txbad = Integer.parseInt(prop[1]);
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLinkProperties() {
        LinkProperties newLp = new LinkProperties();
        newLp.setInterfaceName(this.mInterfaceName);
        newLp.setHttpProxy(this.mWifiConfigStore.getProxyProperties(this.mLastNetworkId));
        newLp.setLinkAddresses(this.mNetlinkLinkProperties.getLinkAddresses());
        synchronized (this.mDhcpResultsLock) {
            if (this.mDhcpResults != null && this.mDhcpResults.linkProperties != null) {
                LinkProperties lp = this.mDhcpResults.linkProperties;
                for (RouteInfo route : lp.getRoutes()) {
                    newLp.addRoute(route);
                }
                for (InetAddress dns : lp.getDnses()) {
                    newLp.addDns(dns);
                }
                newLp.setDomains(lp.getDomains());
            }
        }
        if (!newLp.equals(this.mLinkProperties)) {
            this.mLinkProperties = newLp;
            if (getNetworkDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                sendLinkConfigurationChangedBroadcast();
            }
        }
    }

    private void clearLinkProperties() {
        if (!this.mWifiConfigStore.isUsingStaticIp(this.mLastNetworkId)) {
            this.mWifiConfigStore.clearLinkProperties(this.mLastNetworkId);
        }
        synchronized (this.mDhcpResultsLock) {
            if (this.mDhcpResults != null && this.mDhcpResults.linkProperties != null) {
                this.mDhcpResults.linkProperties.clear();
            }
        }
        this.mNetlinkLinkProperties.clear();
        this.mLinkProperties.clear();
    }

    private int getMaxDhcpRetries() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "wifi_max_dhcp_retry_count", 9);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendScanResultsAvailableBroadcast() {
        noteScanEnd();
        Intent intent = new Intent(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intent.addFlags(67108864);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void sendRssiChangeBroadcast(int newRssi) {
        Intent intent = new Intent(WifiManager.RSSI_CHANGED_ACTION);
        intent.addFlags(67108864);
        intent.putExtra(WifiManager.EXTRA_NEW_RSSI, newRssi);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendNetworkStateChangeBroadcast(String bssid) {
        Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intent.addFlags(67108864);
        intent.putExtra("networkInfo", new NetworkInfo(this.mNetworkInfo));
        intent.putExtra("linkProperties", new LinkProperties(this.mLinkProperties));
        if (bssid != null) {
            intent.putExtra("bssid", bssid);
        }
        if (this.mNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.VERIFYING_POOR_LINK || this.mNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            intent.putExtra(WifiManager.EXTRA_WIFI_INFO, new WifiInfo(this.mWifiInfo));
        }
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void sendLinkConfigurationChangedBroadcast() {
        Intent intent = new Intent(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        intent.addFlags(67108864);
        intent.putExtra("linkProperties", new LinkProperties(this.mLinkProperties));
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSupplicantConnectionChangedBroadcast(boolean connected) {
        Intent intent = new Intent(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intent.addFlags(67108864);
        intent.putExtra("connected", connected);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNetworkDetailedState(NetworkInfo.DetailedState state) {
        if (state != this.mNetworkInfo.getDetailedState()) {
            this.mNetworkInfo.setDetailedState(state, null, this.mWifiInfo.getSSID());
        }
    }

    private NetworkInfo.DetailedState getNetworkDetailedState() {
        return this.mNetworkInfo.getDetailedState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SupplicantState handleSupplicantStateChange(Message message) {
        StateChangeResult stateChangeResult = (StateChangeResult) message.obj;
        SupplicantState state = stateChangeResult.state;
        this.mWifiInfo.setSupplicantState(state);
        if (SupplicantState.isConnecting(state)) {
            this.mWifiInfo.setNetworkId(stateChangeResult.networkId);
        } else {
            this.mWifiInfo.setNetworkId(-1);
        }
        this.mWifiInfo.setBSSID(stateChangeResult.BSSID);
        this.mWifiInfo.setSSID(stateChangeResult.wifiSsid);
        this.mSupplicantStateTracker.sendMessage(Message.obtain(message));
        return state;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNetworkDisconnect() {
        stopDhcp();
        try {
            this.mNwService.clearInterfaceAddresses(this.mInterfaceName);
            this.mNwService.disableIpv6(this.mInterfaceName);
        } catch (Exception e) {
            loge("Failed to clear addresses or disable ipv6" + e);
        }
        this.mWifiInfo.setInetAddress(null);
        this.mWifiInfo.setBSSID(null);
        this.mWifiInfo.setSSID(null);
        this.mWifiInfo.setNetworkId(-1);
        this.mWifiInfo.setRssi(-200);
        this.mWifiInfo.setLinkSpeed(-1);
        this.mWifiInfo.setMeteredHint(false);
        setNetworkDetailedState(NetworkInfo.DetailedState.DISCONNECTED);
        this.mWifiConfigStore.updateStatus(this.mLastNetworkId, NetworkInfo.DetailedState.DISCONNECTED);
        clearLinkProperties();
        sendNetworkStateChangeBroadcast(this.mLastBssid);
        this.mLastBssid = null;
        this.mLastNetworkId = -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSupplicantConnectionLoss() {
        this.mWifiMonitor.killSupplicant(this.mP2pSupported);
        sendSupplicantConnectionChangedBroadcast(false);
        setWifiState(1);
    }

    void handlePreDhcpSetup() {
        this.mDhcpActive = true;
        if (!this.mBluetoothConnectionActive) {
            WifiNative wifiNative = this.mWifiNative;
            WifiNative wifiNative2 = this.mWifiNative;
            wifiNative.setBluetoothCoexistenceMode(1);
        }
        setSuspendOptimizationsNative(1, false);
        this.mWifiNative.setPowerSave(false);
        stopBatchedScan();
        Message msg = new Message();
        msg.what = WifiP2pService.BLOCK_DISCOVERY;
        msg.arg1 = 1;
        msg.arg2 = DhcpStateMachine.CMD_PRE_DHCP_ACTION_COMPLETE;
        msg.obj = this.mDhcpStateMachine;
        this.mWifiP2pChannel.sendMessage(msg);
    }

    void startDhcp() {
        if (this.mDhcpStateMachine == null) {
            this.mDhcpStateMachine = DhcpStateMachine.makeDhcpStateMachine(this.mContext, this, this.mInterfaceName);
        }
        this.mDhcpStateMachine.registerForPreDhcpNotification();
        this.mDhcpStateMachine.sendMessage(DhcpStateMachine.CMD_START_DHCP);
    }

    void stopDhcp() {
        if (this.mDhcpStateMachine != null) {
            handlePostDhcpSetup();
            this.mDhcpStateMachine.sendMessage(DhcpStateMachine.CMD_STOP_DHCP);
        }
    }

    void handlePostDhcpSetup() {
        setSuspendOptimizationsNative(1, true);
        this.mWifiNative.setPowerSave(true);
        this.mWifiP2pChannel.sendMessage(WifiP2pService.BLOCK_DISCOVERY, 0);
        WifiNative wifiNative = this.mWifiNative;
        WifiNative wifiNative2 = this.mWifiNative;
        wifiNative.setBluetoothCoexistenceMode(2);
        this.mDhcpActive = false;
        if (this.mBatchedScanSettings != null) {
            startBatchedScan();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSuccessfulIpConfiguration(DhcpResults dhcpResults) {
        this.mLastSignalLevel = -1;
        this.mReconnectCount = 0;
        synchronized (this.mDhcpResultsLock) {
            this.mDhcpResults = dhcpResults;
        }
        LinkProperties linkProperties = dhcpResults.linkProperties;
        this.mWifiConfigStore.setLinkProperties(this.mLastNetworkId, new LinkProperties(linkProperties));
        InetAddress addr = null;
        Iterator<InetAddress> addrs = linkProperties.getAddresses().iterator();
        if (addrs.hasNext()) {
            addr = addrs.next();
        }
        this.mWifiInfo.setInetAddress(addr);
        this.mWifiInfo.setMeteredHint(dhcpResults.hasMeteredHint());
        updateLinkProperties();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFailedIpConfiguration() {
        loge("IP configuration failed");
        this.mWifiInfo.setInetAddress(null);
        this.mWifiInfo.setMeteredHint(false);
        int maxRetries = getMaxDhcpRetries();
        if (maxRetries > 0) {
            int i = this.mReconnectCount + 1;
            this.mReconnectCount = i;
            if (i > maxRetries) {
                loge("Failed " + this.mReconnectCount + " times, Disabling " + this.mLastNetworkId);
                this.mWifiConfigStore.disableNetwork(this.mLastNetworkId, 2);
                this.mReconnectCount = 0;
            }
        }
        this.mWifiNative.disconnect();
        this.mWifiNative.reconnect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSoftApWithConfig(final WifiConfiguration config) {
        new Thread(new Runnable() { // from class: android.net.wifi.WifiStateMachine.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    WifiStateMachine.this.mNwService.startAccessPoint(config, WifiStateMachine.this.mInterfaceName);
                } catch (Exception e) {
                    WifiStateMachine.this.loge("Exception in softap start " + e);
                    try {
                        WifiStateMachine.this.mNwService.stopAccessPoint(WifiStateMachine.this.mInterfaceName);
                        WifiStateMachine.this.mNwService.startAccessPoint(config, WifiStateMachine.this.mInterfaceName);
                    } catch (Exception e1) {
                        WifiStateMachine.this.loge("Exception in softap re-start " + e1);
                        WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_START_AP_FAILURE);
                        return;
                    }
                }
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_START_AP_SUCCESS);
            }
        }).start();
    }

    /* loaded from: WifiStateMachine$DefaultState.class */
    class DefaultState extends State {
        DefaultState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case 69632:
                    if (message.arg1 == 0) {
                        WifiStateMachine.this.mWifiP2pChannel.sendMessage(AsyncChannel.CMD_CHANNEL_FULL_CONNECTION);
                        return true;
                    }
                    WifiStateMachine.this.loge("WifiP2pService connection failure, error=" + message.arg1);
                    return true;
                case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                    WifiStateMachine.this.loge("WifiP2pService channel lost, message.arg1 =" + message.arg1);
                    return true;
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT_FAILED /* 131089 */:
                case WifiStateMachine.CMD_DELAYED_STOP_DRIVER /* 131090 */:
                case WifiStateMachine.CMD_DRIVER_START_TIMED_OUT /* 131091 */:
                case WifiStateMachine.CMD_CAPTIVE_CHECK_COMPLETE /* 131092 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_START_AP_SUCCESS /* 131094 */:
                case WifiStateMachine.CMD_START_AP_FAILURE /* 131095 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_SET_AP_CONFIG /* 131097 */:
                case WifiStateMachine.CMD_SET_AP_CONFIG_COMPLETED /* 131098 */:
                case WifiStateMachine.CMD_REQUEST_AP_CONFIG /* 131099 */:
                case WifiStateMachine.CMD_RESPONSE_AP_CONFIG /* 131100 */:
                case WifiStateMachine.CMD_TETHER_STATE_CHANGE /* 131101 */:
                case WifiStateMachine.CMD_TETHER_NOTIFICATION_TIMED_OUT /* 131102 */:
                case WifiStateMachine.CMD_ENABLE_ALL_NETWORKS /* 131127 */:
                case WifiStateMachine.CMD_BLACKLIST_NETWORK /* 131128 */:
                case WifiStateMachine.CMD_CLEAR_BLACKLIST /* 131129 */:
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_DISCONNECT /* 131145 */:
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_RSSI_POLL /* 131155 */:
                case WifiStateMachine.CMD_NO_NETWORKS_PERIODIC_SCAN /* 131160 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                case WifiStateMachine.CMD_DISABLE_P2P_RSP /* 131205 */:
                case WifiStateMachine.CMD_RELOAD_TLS_AND_RECONNECT /* 131214 */:
                case 135189:
                case 135190:
                case WifiMonitor.SUP_CONNECTION_EVENT /* 147457 */:
                case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                case WifiMonitor.NETWORK_CONNECTION_EVENT /* 147459 */:
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /* 147460 */:
                case WifiMonitor.SCAN_RESULTS_EVENT /* 147461 */:
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /* 147463 */:
                case WifiMonitor.WPS_OVERLAP_EVENT /* 147466 */:
                case WifiMonitor.ASSOCIATION_REJECTION_EVENT /* 147499 */:
                case DhcpStateMachine.CMD_PRE_DHCP_ACTION /* 196612 */:
                case DhcpStateMachine.CMD_POST_DHCP_ACTION /* 196613 */:
                    return true;
                case WifiStateMachine.CMD_BLUETOOTH_ADAPTER_STATE_CHANGE /* 131103 */:
                    WifiStateMachine.this.mBluetoothConnectionActive = message.arg1 != 0;
                    return true;
                case WifiStateMachine.CMD_PING_SUPPLICANT /* 131123 */:
                case WifiStateMachine.CMD_ADD_OR_UPDATE_NETWORK /* 131124 */:
                case WifiStateMachine.CMD_REMOVE_NETWORK /* 131125 */:
                case WifiStateMachine.CMD_ENABLE_NETWORK /* 131126 */:
                case WifiStateMachine.CMD_SAVE_CONFIG /* 131130 */:
                    WifiStateMachine.this.replyToMessage(message, message.what, -1);
                    return true;
                case WifiStateMachine.CMD_GET_CONFIGURED_NETWORKS /* 131131 */:
                    WifiStateMachine.this.replyToMessage(message, message.what, (Object) null);
                    return true;
                case WifiStateMachine.CMD_SET_HIGH_PERF_MODE /* 131149 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.setSuspendOptimizations(2, false);
                        return true;
                    }
                    WifiStateMachine.this.setSuspendOptimizations(2, true);
                    return true;
                case WifiStateMachine.CMD_ENABLE_RSSI_POLL /* 131154 */:
                    WifiStateMachine.this.mEnableRssiPolling = message.arg1 == 1;
                    return true;
                case WifiStateMachine.CMD_SET_SUSPEND_OPT_ENABLED /* 131158 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.mSuspendWakeLock.release();
                        WifiStateMachine.this.setSuspendOptimizations(4, true);
                        return true;
                    }
                    WifiStateMachine.this.setSuspendOptimizations(4, false);
                    return true;
                case WifiStateMachine.CMD_ENABLE_BACKGROUND_SCAN /* 131163 */:
                    WifiStateMachine.this.mEnableBackgroundScan = message.arg1 == 1;
                    return true;
                case WifiStateMachine.CMD_BOOT_COMPLETED /* 131206 */:
                    String countryCode = WifiStateMachine.this.mPersistedCountryCode;
                    if (!TextUtils.isEmpty(countryCode)) {
                        Settings.Global.putString(WifiStateMachine.this.mContext.getContentResolver(), Settings.Global.WIFI_COUNTRY_CODE, countryCode);
                        WifiStateMachine.this.sendMessageAtFrontOfQueue((int) WifiStateMachine.CMD_SET_COUNTRY_CODE, countryCode);
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_SET_BATCHED_SCAN /* 131207 */:
                    WifiStateMachine.this.recordBatchedScanSettings((BatchedScanSettings) message.obj);
                    return true;
                case WifiStateMachine.CMD_START_NEXT_BATCHED_SCAN /* 131208 */:
                    WifiStateMachine.this.startNextBatchedScan();
                    return true;
                case WifiStateMachine.CMD_POLL_BATCHED_SCAN /* 131209 */:
                    WifiStateMachine.this.handleBatchedScanPollRequest();
                    return true;
                case WifiStateMachine.CMD_IP_ADDRESS_UPDATED /* 131212 */:
                    if (WifiStateMachine.this.mNetlinkLinkProperties.addLinkAddress((LinkAddress) message.obj)) {
                        WifiStateMachine.this.updateLinkProperties();
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_IP_ADDRESS_REMOVED /* 131213 */:
                    if (WifiStateMachine.this.mNetlinkLinkProperties.removeLinkAddress((LinkAddress) message.obj)) {
                        WifiStateMachine.this.updateLinkProperties();
                        return true;
                    }
                    return true;
                case WifiP2pService.P2P_CONNECTION_CHANGED /* 143371 */:
                    NetworkInfo info = (NetworkInfo) message.obj;
                    WifiStateMachine.this.mP2pConnected.set(info.isConnected());
                    return true;
                case WifiP2pService.DISCONNECT_WIFI_REQUEST /* 143372 */:
                    WifiStateMachine.this.mTemporarilyDisconnectWifi = message.arg1 == 1;
                    WifiStateMachine.this.replyToMessage(message, WifiP2pService.DISCONNECT_WIFI_RESPONSE);
                    return true;
                case WifiMonitor.DRIVER_HUNG_EVENT /* 147468 */:
                    WifiStateMachine.this.setSupplicantRunning(false);
                    WifiStateMachine.this.setSupplicantRunning(true);
                    return true;
                case WifiManager.CONNECT_NETWORK /* 151553 */:
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.CONNECT_NETWORK_FAILED, 2);
                    return true;
                case WifiManager.FORGET_NETWORK /* 151556 */:
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.FORGET_NETWORK_FAILED, 2);
                    return true;
                case WifiManager.SAVE_NETWORK /* 151559 */:
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.SAVE_NETWORK_FAILED, 2);
                    return true;
                case WifiManager.START_WPS /* 151562 */:
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.WPS_FAILED, 2);
                    return true;
                case WifiManager.CANCEL_WPS /* 151566 */:
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.CANCEL_WPS_FAILED, 2);
                    return true;
                case WifiManager.DISABLE_NETWORK /* 151569 */:
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.DISABLE_NETWORK_FAILED, 2);
                    return true;
                case WifiManager.RSSI_PKTCNT_FETCH /* 151572 */:
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.RSSI_PKTCNT_FETCH_FAILED, 2);
                    return true;
                case DhcpStateMachine.CMD_ON_QUIT /* 196614 */:
                    WifiStateMachine.this.mDhcpStateMachine = null;
                    return true;
                default:
                    WifiStateMachine.this.loge("Error! unhandled message" + message);
                    return true;
            }
        }
    }

    /* loaded from: WifiStateMachine$InitialState.class */
    class InitialState extends State {
        InitialState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiNative unused = WifiStateMachine.this.mWifiNative;
            WifiNative.unloadDriver();
            if (WifiStateMachine.this.mWifiP2pChannel == null) {
                WifiStateMachine.this.mWifiP2pChannel = new AsyncChannel();
                WifiStateMachine.this.mWifiP2pChannel.connect(WifiStateMachine.this.mContext, WifiStateMachine.this.getHandler(), WifiStateMachine.this.mWifiP2pManager.getMessenger());
            }
            if (WifiStateMachine.this.mWifiApConfigChannel == null) {
                WifiStateMachine.this.mWifiApConfigChannel = new AsyncChannel();
                WifiApConfigStore wifiApConfigStore = WifiApConfigStore.makeWifiApConfigStore(WifiStateMachine.this.mContext, WifiStateMachine.this.getHandler());
                wifiApConfigStore.loadApConfiguration();
                WifiStateMachine.this.mWifiApConfigChannel.connectSync(WifiStateMachine.this.mContext, WifiStateMachine.this.getHandler(), wifiApConfigStore.getMessenger());
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                    WifiNative unused = WifiStateMachine.this.mWifiNative;
                    if (WifiNative.loadDriver()) {
                        try {
                            WifiStateMachine.this.mNwService.wifiFirmwareReload(WifiStateMachine.this.mInterfaceName, "STA");
                        } catch (Exception e) {
                            WifiStateMachine.this.loge("Failed to reload STA firmware " + e);
                        }
                        try {
                            WifiStateMachine.this.mNwService.setInterfaceDown(WifiStateMachine.this.mInterfaceName);
                            WifiStateMachine.this.mNwService.setInterfaceIpv6PrivacyExtensions(WifiStateMachine.this.mInterfaceName, true);
                            WifiStateMachine.this.mNwService.disableIpv6(WifiStateMachine.this.mInterfaceName);
                        } catch (RemoteException re) {
                            WifiStateMachine.this.loge("Unable to change interface settings: " + re);
                        } catch (IllegalStateException ie) {
                            WifiStateMachine.this.loge("Unable to change interface settings: " + ie);
                        }
                        WifiStateMachine.this.mWifiMonitor.killSupplicant(WifiStateMachine.this.mP2pSupported);
                        WifiNative unused2 = WifiStateMachine.this.mWifiNative;
                        if (WifiNative.startSupplicant(WifiStateMachine.this.mP2pSupported)) {
                            WifiStateMachine.this.setWifiState(2);
                            WifiStateMachine.this.mWifiMonitor.startMonitoring();
                            WifiStateMachine.this.transitionTo(WifiStateMachine.this.mSupplicantStartingState);
                            return true;
                        }
                        WifiStateMachine.this.loge("Failed to start supplicant!");
                        return true;
                    }
                    WifiStateMachine.this.loge("Failed to load driver");
                    return true;
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                    WifiNative unused3 = WifiStateMachine.this.mWifiNative;
                    if (WifiNative.loadDriver()) {
                        WifiStateMachine.this.setWifiApState(12);
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mSoftApStartingState);
                        return false;
                    }
                    WifiStateMachine.this.loge("Failed to load driver for softap");
                    return false;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$SupplicantStartingState.class */
    class SupplicantStartingState extends State {
        SupplicantStartingState() {
        }

        private void initializeWpsDetails() {
            String detail = SystemProperties.get("ro.product.name", "");
            if (!WifiStateMachine.this.mWifiNative.setDeviceName(detail)) {
                WifiStateMachine.this.loge("Failed to set device name " + detail);
            }
            String detail2 = SystemProperties.get("ro.product.manufacturer", "");
            if (!WifiStateMachine.this.mWifiNative.setManufacturer(detail2)) {
                WifiStateMachine.this.loge("Failed to set manufacturer " + detail2);
            }
            String detail3 = SystemProperties.get("ro.product.model", "");
            if (!WifiStateMachine.this.mWifiNative.setModelName(detail3)) {
                WifiStateMachine.this.loge("Failed to set model name " + detail3);
            }
            String detail4 = SystemProperties.get("ro.product.model", "");
            if (!WifiStateMachine.this.mWifiNative.setModelNumber(detail4)) {
                WifiStateMachine.this.loge("Failed to set model number " + detail4);
            }
            String detail5 = SystemProperties.get("ro.serialno", "");
            if (!WifiStateMachine.this.mWifiNative.setSerialNumber(detail5)) {
                WifiStateMachine.this.loge("Failed to set serial number " + detail5);
            }
            if (!WifiStateMachine.this.mWifiNative.setConfigMethods("physical_display virtual_push_button")) {
                WifiStateMachine.this.loge("Failed to set WPS config methods");
            }
            if (!WifiStateMachine.this.mWifiNative.setDeviceType(WifiStateMachine.this.mPrimaryDeviceType)) {
                WifiStateMachine.this.loge("Failed to set primary device type " + WifiStateMachine.this.mPrimaryDeviceType);
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiMonitor.SUP_CONNECTION_EVENT /* 147457 */:
                    WifiStateMachine.this.setWifiState(3);
                    WifiStateMachine.this.mSupplicantRestartCount = 0;
                    WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiStateMachine.CMD_RESET_SUPPLICANT_STATE);
                    WifiStateMachine.this.mLastBssid = null;
                    WifiStateMachine.this.mLastNetworkId = -1;
                    WifiStateMachine.this.mLastSignalLevel = -1;
                    WifiStateMachine.this.mWifiInfo.setMacAddress(WifiStateMachine.this.mWifiNative.getMacAddress());
                    WifiStateMachine.this.mWifiConfigStore.loadAndEnableAllNetworks();
                    initializeWpsDetails();
                    WifiStateMachine.this.sendSupplicantConnectionChangedBroadcast(true);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDriverStartedState);
                    return true;
                case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                    if (WifiStateMachine.access$5504(WifiStateMachine.this) <= 5) {
                        WifiStateMachine.this.loge("Failed to setup control channel, restart supplicant");
                        WifiStateMachine.this.mWifiMonitor.killSupplicant(WifiStateMachine.this.mP2pSupported);
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mInitialState);
                        WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.CMD_START_SUPPLICANT, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                        return true;
                    }
                    WifiStateMachine.this.loge("Failed " + WifiStateMachine.this.mSupplicantRestartCount + " times to start supplicant, unload driver");
                    WifiStateMachine.this.mSupplicantRestartCount = 0;
                    WifiStateMachine.this.setWifiState(4);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mInitialState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$SupplicantStartedState.class */
    class SupplicantStartedState extends State {
        SupplicantStartedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiStateMachine.this.mNetworkInfo.setIsAvailable(true);
            int defaultInterval = WifiStateMachine.this.mContext.getResources().getInteger(R.integer.config_wifi_supplicant_scan_interval);
            WifiStateMachine.this.mSupplicantScanIntervalMs = Settings.Global.getLong(WifiStateMachine.this.mContext.getContentResolver(), Settings.Global.WIFI_SUPPLICANT_SCAN_INTERVAL_MS, defaultInterval);
            WifiStateMachine.this.mWifiNative.setScanInterval(((int) WifiStateMachine.this.mSupplicantScanIntervalMs) / 1000);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                    if (WifiStateMachine.this.mP2pSupported) {
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mWaitForP2pDisableState);
                        return true;
                    }
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mSupplicantStoppingState);
                    return true;
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                    WifiStateMachine.this.loge("Failed to start soft AP with a running supplicant");
                    WifiStateMachine.this.setWifiApState(14);
                    return true;
                case WifiStateMachine.CMD_PING_SUPPLICANT /* 131123 */:
                    boolean ok = WifiStateMachine.this.mWifiNative.ping();
                    WifiStateMachine.this.replyToMessage(message, message.what, ok ? 1 : -1);
                    return true;
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                    WifiStateMachine.this.mOperationalMode = message.arg1;
                    return true;
                case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                    WifiStateMachine.this.loge("Connection lost, restart supplicant");
                    WifiStateMachine.this.handleSupplicantConnectionLoss();
                    WifiStateMachine.this.handleNetworkDisconnect();
                    WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiStateMachine.CMD_RESET_SUPPLICANT_STATE);
                    if (WifiStateMachine.this.mP2pSupported) {
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mWaitForP2pDisableState);
                    } else {
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mInitialState);
                    }
                    WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.CMD_START_SUPPLICANT, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                    return true;
                case WifiMonitor.SCAN_RESULTS_EVENT /* 147461 */:
                    WifiStateMachine.this.setScanResults();
                    WifiStateMachine.this.sendScanResultsAvailableBroadcast();
                    WifiStateMachine.this.mScanResultIsPending = false;
                    return true;
                default:
                    return false;
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void exit() {
            WifiStateMachine.this.mNetworkInfo.setIsAvailable(false);
        }
    }

    /* loaded from: WifiStateMachine$SupplicantStoppingState.class */
    class SupplicantStoppingState extends State {
        SupplicantStoppingState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiStateMachine.this.handleNetworkDisconnect();
            if (WifiStateMachine.this.mDhcpStateMachine != null) {
                WifiStateMachine.this.mDhcpStateMachine.doQuit();
            }
            WifiStateMachine.this.mWifiMonitor.stopSupplicant();
            WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.this.obtainMessage(WifiStateMachine.CMD_STOP_SUPPLICANT_FAILED, WifiStateMachine.access$8704(WifiStateMachine.this), 0), TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            WifiStateMachine.this.setWifiState(0);
            WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiStateMachine.CMD_RESET_SUPPLICANT_STATE);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_STOP_SUPPLICANT_FAILED /* 131089 */:
                    if (message.arg1 == WifiStateMachine.this.mSupplicantStopFailureToken) {
                        WifiStateMachine.this.loge("Timed out on a supplicant stop, kill and proceed");
                        WifiStateMachine.this.handleSupplicantConnectionLoss();
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mInitialState);
                        return true;
                    }
                    return true;
                case WifiMonitor.SUP_CONNECTION_EVENT /* 147457 */:
                    WifiStateMachine.this.loge("Supplicant connection received while stopping");
                    return true;
                case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                    WifiStateMachine.this.handleSupplicantConnectionLoss();
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mInitialState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$DriverStartingState.class */
    class DriverStartingState extends State {
        private int mTries;

        DriverStartingState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            this.mTries = 1;
            WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.this.obtainMessage(WifiStateMachine.CMD_DRIVER_START_TIMED_OUT, WifiStateMachine.access$9304(WifiStateMachine.this), 0), 10000L);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                case WifiStateMachine.CMD_DISCONNECT /* 131145 */:
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                case WifiMonitor.NETWORK_CONNECTION_EVENT /* 147459 */:
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /* 147460 */:
                case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /* 147463 */:
                case WifiMonitor.WPS_OVERLAP_EVENT /* 147466 */:
                case WifiMonitor.ASSOCIATION_REJECTION_EVENT /* 147499 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_DRIVER_START_TIMED_OUT /* 131091 */:
                    if (message.arg1 == WifiStateMachine.this.mDriverStartToken) {
                        if (this.mTries >= 2) {
                            WifiStateMachine.this.loge("Failed to start driver after " + this.mTries);
                            WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDriverStoppedState);
                            return true;
                        }
                        WifiStateMachine.this.loge("Driver start failed, retrying");
                        WifiStateMachine.this.mWakeLock.acquire();
                        WifiStateMachine.this.mWifiNative.startDriver();
                        WifiStateMachine.this.mWakeLock.release();
                        this.mTries++;
                        WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.this.obtainMessage(WifiStateMachine.CMD_DRIVER_START_TIMED_OUT, WifiStateMachine.access$9304(WifiStateMachine.this), 0), 10000L);
                        return true;
                    }
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    SupplicantState state = WifiStateMachine.this.handleSupplicantStateChange(message);
                    if (SupplicantState.isDriverActive(state)) {
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDriverStartedState);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$DriverStartedState.class */
    class DriverStartedState extends State {
        DriverStartedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiStateMachine.this.mIsRunning = true;
            WifiStateMachine.this.mInDelayedStop = false;
            WifiStateMachine.access$10408(WifiStateMachine.this);
            WifiStateMachine.this.updateBatteryWorkSource(null);
            WifiStateMachine.this.mWifiNative.setBluetoothCoexistenceScanMode(WifiStateMachine.this.mBluetoothConnectionActive);
            WifiStateMachine.this.setCountryCode();
            WifiStateMachine.this.setFrequencyBand();
            WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.DISCONNECTED);
            WifiStateMachine.this.mWifiNative.stopFilteringMulticastV6Packets();
            if (WifiStateMachine.this.mFilteringMulticastV4Packets.get()) {
                WifiStateMachine.this.mWifiNative.startFilteringMulticastV4Packets();
            } else {
                WifiStateMachine.this.mWifiNative.stopFilteringMulticastV4Packets();
            }
            WifiStateMachine.this.mDhcpActive = false;
            if (WifiStateMachine.this.mBatchedScanSettings != null) {
                WifiStateMachine.this.startBatchedScan();
            }
            if (WifiStateMachine.this.mOperationalMode != 1) {
                WifiStateMachine.this.mWifiNative.disconnect();
                WifiStateMachine.this.mWifiConfigStore.disableAllNetworks();
                if (WifiStateMachine.this.mOperationalMode == 3) {
                    WifiStateMachine.this.setWifiState(1);
                }
                WifiStateMachine.this.transitionTo(WifiStateMachine.this.mScanModeState);
            } else {
                WifiStateMachine.this.mWifiConfigStore.enableAllNetworks();
                WifiStateMachine.this.mWifiNative.reconnect();
                WifiStateMachine.this.mWifiNative.status();
                WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectedState);
            }
            if (!WifiStateMachine.this.mScreenBroadcastReceived.get()) {
                PowerManager powerManager = (PowerManager) WifiStateMachine.this.mContext.getSystemService(Context.POWER_SERVICE);
                WifiStateMachine.this.handleScreenStateChanged(powerManager.isScreenOn());
            } else {
                WifiStateMachine.this.mWifiNative.setSuspendOptimizations(WifiStateMachine.this.mSuspendOptNeedsDisabled == 0 && WifiStateMachine.this.mUserWantsSuspendOpt.get());
            }
            WifiStateMachine.this.mWifiNative.setPowerSave(true);
            if (WifiStateMachine.this.mP2pSupported && WifiStateMachine.this.mOperationalMode == 1) {
                WifiStateMachine.this.mWifiP2pChannel.sendMessage(WifiStateMachine.CMD_ENABLE_P2P);
            }
            Intent intent = new Intent(WifiManager.WIFI_SCAN_AVAILABLE);
            intent.addFlags(67108864);
            intent.putExtra(WifiManager.EXTRA_SCAN_AVAILABLE, 3);
            WifiStateMachine.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                    if (WifiStateMachine.this.mInDelayedStop) {
                        WifiStateMachine.this.mInDelayedStop = false;
                        WifiStateMachine.access$10408(WifiStateMachine.this);
                        WifiStateMachine.this.mAlarmManager.cancel(WifiStateMachine.this.mDriverStopIntent);
                        if (WifiStateMachine.this.mOperationalMode == 1) {
                            WifiStateMachine.this.mWifiConfigStore.enableAllNetworks();
                            return true;
                        }
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                    int i = message.arg1;
                    if (!WifiStateMachine.this.mInDelayedStop) {
                        WifiStateMachine.this.mWifiConfigStore.disableAllNetworks();
                        WifiStateMachine.this.mInDelayedStop = true;
                        WifiStateMachine.access$10408(WifiStateMachine.this);
                        Intent driverStopIntent = new Intent(WifiStateMachine.ACTION_DELAYED_DRIVER_STOP, (Uri) null);
                        driverStopIntent.putExtra(WifiStateMachine.DELAYED_STOP_COUNTER, WifiStateMachine.this.mDelayedStopCounter);
                        WifiStateMachine.this.mDriverStopIntent = PendingIntent.getBroadcast(WifiStateMachine.this.mContext, 0, driverStopIntent, 134217728);
                        WifiStateMachine.this.mAlarmManager.set(0, System.currentTimeMillis() + WifiStateMachine.this.mDriverStopDelayMs, WifiStateMachine.this.mDriverStopIntent);
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_DELAYED_STOP_DRIVER /* 131090 */:
                    if (message.arg1 == WifiStateMachine.this.mDelayedStopCounter) {
                        if (WifiStateMachine.this.getCurrentState() != WifiStateMachine.this.mDisconnectedState) {
                            WifiStateMachine.this.mWifiNative.disconnect();
                            WifiStateMachine.this.handleNetworkDisconnect();
                        }
                        WifiStateMachine.this.mWakeLock.acquire();
                        WifiStateMachine.this.mWifiNative.stopDriver();
                        WifiStateMachine.this.mWakeLock.release();
                        if (WifiStateMachine.this.mP2pSupported) {
                            WifiStateMachine.this.transitionTo(WifiStateMachine.this.mWaitForP2pDisableState);
                            return true;
                        }
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDriverStoppingState);
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_BLUETOOTH_ADAPTER_STATE_CHANGE /* 131103 */:
                    WifiStateMachine.this.mBluetoothConnectionActive = message.arg1 != 0;
                    WifiStateMachine.this.mWifiNative.setBluetoothCoexistenceScanMode(WifiStateMachine.this.mBluetoothConnectionActive);
                    return true;
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                    WifiStateMachine.this.noteScanStart(message.arg1, (WorkSource) message.obj);
                    WifiStateMachine.this.startScanNative(2);
                    return true;
                case WifiStateMachine.CMD_SET_HIGH_PERF_MODE /* 131149 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.setSuspendOptimizationsNative(2, false);
                        return true;
                    }
                    WifiStateMachine.this.setSuspendOptimizationsNative(2, true);
                    return true;
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                    String country = (String) message.obj;
                    if (country != null) {
                        String country2 = country.toUpperCase(Locale.ROOT);
                        if (WifiStateMachine.this.mLastSetCountryCode == null || !country2.equals(WifiStateMachine.this.mLastSetCountryCode)) {
                            if (WifiStateMachine.this.mWifiNative.setCountryCode(country2)) {
                                WifiStateMachine.this.mLastSetCountryCode = country2;
                                return true;
                            }
                            WifiStateMachine.this.loge("Failed to set country code " + country2);
                            return true;
                        }
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.mWifiNative.startFilteringMulticastV6Packets();
                        return true;
                    } else if (message.arg1 == 0) {
                        WifiStateMachine.this.mWifiNative.startFilteringMulticastV4Packets();
                        return true;
                    } else {
                        WifiStateMachine.this.loge("Illegal arugments to CMD_START_PACKET_FILTERING");
                        return true;
                    }
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.mWifiNative.stopFilteringMulticastV6Packets();
                        return true;
                    } else if (message.arg1 == 0) {
                        WifiStateMachine.this.mWifiNative.stopFilteringMulticastV4Packets();
                        return true;
                    } else {
                        WifiStateMachine.this.loge("Illegal arugments to CMD_STOP_PACKET_FILTERING");
                        return true;
                    }
                case WifiStateMachine.CMD_SET_SUSPEND_OPT_ENABLED /* 131158 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.setSuspendOptimizationsNative(4, true);
                        WifiStateMachine.this.mSuspendWakeLock.release();
                        return true;
                    }
                    WifiStateMachine.this.setSuspendOptimizationsNative(4, false);
                    return true;
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    int band = message.arg1;
                    if (WifiStateMachine.this.mWifiNative.setBand(band)) {
                        WifiStateMachine.this.mFrequencyBand.set(band);
                        WifiStateMachine.this.mWifiNative.bssFlush();
                        WifiStateMachine.this.startScanNative(2);
                        return true;
                    }
                    WifiStateMachine.this.loge("Failed to set frequency band " + band);
                    return true;
                case WifiStateMachine.CMD_ENABLE_TDLS /* 131164 */:
                    if (message.obj != null) {
                        String remoteAddress = (String) message.obj;
                        boolean enable = message.arg1 == 1;
                        WifiStateMachine.this.mWifiNative.startTdls(remoteAddress, enable);
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_SET_BATCHED_SCAN /* 131207 */:
                    WifiStateMachine.this.recordBatchedScanSettings((BatchedScanSettings) message.obj);
                    WifiStateMachine.this.startBatchedScan();
                    return true;
                default:
                    return false;
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void exit() {
            WifiStateMachine.this.mIsRunning = false;
            WifiStateMachine.this.updateBatteryWorkSource(null);
            WifiStateMachine.this.mScanResults = new ArrayList();
            if (WifiStateMachine.this.mBatchedScanSettings != null) {
                WifiStateMachine.this.stopBatchedScan();
            }
            Intent intent = new Intent(WifiManager.WIFI_SCAN_AVAILABLE);
            intent.addFlags(67108864);
            intent.putExtra(WifiManager.EXTRA_SCAN_AVAILABLE, 1);
            WifiStateMachine.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            WifiStateMachine.this.noteScanEnd();
            WifiStateMachine.this.mLastSetCountryCode = null;
        }
    }

    /* loaded from: WifiStateMachine$WaitForP2pDisableState.class */
    class WaitForP2pDisableState extends State {
        private State mTransitionToState;

        WaitForP2pDisableState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            switch (WifiStateMachine.this.getCurrentMessage().what) {
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                    this.mTransitionToState = WifiStateMachine.this.mSupplicantStoppingState;
                    break;
                case WifiStateMachine.CMD_DELAYED_STOP_DRIVER /* 131090 */:
                    this.mTransitionToState = WifiStateMachine.this.mDriverStoppingState;
                    break;
                case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                    this.mTransitionToState = WifiStateMachine.this.mInitialState;
                    break;
                default:
                    this.mTransitionToState = WifiStateMachine.this.mDriverStoppingState;
                    break;
            }
            WifiStateMachine.this.mWifiP2pChannel.sendMessage(WifiStateMachine.CMD_DISABLE_P2P_REQ);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_DISCONNECT /* 131145 */:
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_DISABLE_P2P_RSP /* 131205 */:
                    WifiStateMachine.this.transitionTo(this.mTransitionToState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$DriverStoppingState.class */
    class DriverStoppingState extends State {
        DriverStoppingState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                case WifiStateMachine.CMD_DISCONNECT /* 131145 */:
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    SupplicantState state = WifiStateMachine.this.handleSupplicantStateChange(message);
                    if (state == SupplicantState.INTERFACE_DISABLED) {
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDriverStoppedState);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$DriverStoppedState.class */
    class DriverStoppedState extends State {
        DriverStoppedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                    WifiStateMachine.this.mWakeLock.acquire();
                    WifiStateMachine.this.mWifiNative.startDriver();
                    WifiStateMachine.this.mWakeLock.release();
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDriverStartingState);
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    StateChangeResult stateChangeResult = (StateChangeResult) message.obj;
                    SupplicantState state = stateChangeResult.state;
                    if (SupplicantState.isDriverActive(state)) {
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDriverStartedState);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$ScanModeState.class */
    class ScanModeState extends State {
        private int mLastOperationMode;

        ScanModeState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            this.mLastOperationMode = WifiStateMachine.this.mOperationalMode;
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                    WifiStateMachine.this.noteScanStart(message.arg1, (WorkSource) message.obj);
                    WifiStateMachine.this.startScanNative(1);
                    return true;
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                    if (message.arg1 == 1) {
                        if (this.mLastOperationMode == 3) {
                            WifiStateMachine.this.setWifiState(3);
                            WifiStateMachine.this.mWifiConfigStore.loadAndEnableAllNetworks();
                            WifiStateMachine.this.mWifiP2pChannel.sendMessage(WifiStateMachine.CMD_ENABLE_P2P);
                        } else {
                            WifiStateMachine.this.mWifiConfigStore.enableAllNetworks();
                        }
                        WifiStateMachine.this.mWifiNative.reconnect();
                        WifiStateMachine.this.mOperationalMode = 1;
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectedState);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$ConnectModeState.class */
    class ConnectModeState extends State {
        ConnectModeState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            WpsResult wpsResult;
            switch (message.what) {
                case WifiStateMachine.CMD_ADD_OR_UPDATE_NETWORK /* 131124 */:
                    WifiStateMachine.this.replyToMessage(message, (int) WifiStateMachine.CMD_ADD_OR_UPDATE_NETWORK, WifiStateMachine.this.mWifiConfigStore.addOrUpdateNetwork((WifiConfiguration) message.obj));
                    return true;
                case WifiStateMachine.CMD_REMOVE_NETWORK /* 131125 */:
                    boolean ok = WifiStateMachine.this.mWifiConfigStore.removeNetwork(message.arg1);
                    WifiStateMachine.this.replyToMessage(message, message.what, ok ? 1 : -1);
                    return true;
                case WifiStateMachine.CMD_ENABLE_NETWORK /* 131126 */:
                    boolean ok2 = WifiStateMachine.this.mWifiConfigStore.enableNetwork(message.arg1, message.arg2 == 1);
                    WifiStateMachine.this.replyToMessage(message, message.what, ok2 ? 1 : -1);
                    return true;
                case WifiStateMachine.CMD_ENABLE_ALL_NETWORKS /* 131127 */:
                    long time = SystemClock.elapsedRealtime();
                    if (time - WifiStateMachine.this.mLastEnableAllNetworksTime > LocationFudger.FASTEST_INTERVAL_MS) {
                        WifiStateMachine.this.mWifiConfigStore.enableAllNetworks();
                        WifiStateMachine.this.mLastEnableAllNetworksTime = time;
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_BLACKLIST_NETWORK /* 131128 */:
                    WifiStateMachine.this.mWifiNative.addToBlacklist((String) message.obj);
                    return true;
                case WifiStateMachine.CMD_CLEAR_BLACKLIST /* 131129 */:
                    WifiStateMachine.this.mWifiNative.clearBlacklist();
                    return true;
                case WifiStateMachine.CMD_SAVE_CONFIG /* 131130 */:
                    boolean ok3 = WifiStateMachine.this.mWifiConfigStore.saveConfig();
                    WifiStateMachine.this.replyToMessage(message, (int) WifiStateMachine.CMD_SAVE_CONFIG, ok3 ? 1 : -1);
                    IBackupManager ibm = IBackupManager.Stub.asInterface(ServiceManager.getService(Context.BACKUP_SERVICE));
                    if (ibm != null) {
                        try {
                            ibm.dataChanged("com.android.providers.settings");
                            return true;
                        } catch (Exception e) {
                            return true;
                        }
                    }
                    return true;
                case WifiStateMachine.CMD_GET_CONFIGURED_NETWORKS /* 131131 */:
                    WifiStateMachine.this.replyToMessage(message, message.what, WifiStateMachine.this.mWifiConfigStore.getConfiguredNetworks());
                    return true;
                case WifiStateMachine.CMD_DISCONNECT /* 131145 */:
                    WifiStateMachine.this.mWifiNative.disconnect();
                    return true;
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                    WifiStateMachine.this.mWifiNative.reconnect();
                    return true;
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                    WifiStateMachine.this.mWifiNative.reassociate();
                    return true;
                case WifiStateMachine.CMD_RELOAD_TLS_AND_RECONNECT /* 131214 */:
                    if (WifiStateMachine.this.mWifiConfigStore.needsUnlockedKeyStore()) {
                        WifiStateMachine.this.logd("Reconnecting to give a chance to un-connected TLS networks");
                        WifiStateMachine.this.mWifiNative.disconnect();
                        WifiStateMachine.this.mWifiNative.reconnect();
                        return true;
                    }
                    return true;
                case WifiP2pService.DISCONNECT_WIFI_REQUEST /* 143372 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.mWifiNative.disconnect();
                        WifiStateMachine.this.mTemporarilyDisconnectWifi = true;
                        return true;
                    }
                    WifiStateMachine.this.mWifiNative.reconnect();
                    WifiStateMachine.this.mTemporarilyDisconnectWifi = false;
                    return true;
                case WifiMonitor.NETWORK_CONNECTION_EVENT /* 147459 */:
                    WifiStateMachine.this.mLastNetworkId = message.arg1;
                    WifiStateMachine.this.mLastBssid = (String) message.obj;
                    WifiStateMachine.this.mWifiInfo.setBSSID(WifiStateMachine.this.mLastBssid);
                    WifiStateMachine.this.mWifiInfo.setNetworkId(WifiStateMachine.this.mLastNetworkId);
                    WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.OBTAINING_IPADDR);
                    WifiStateMachine.this.sendNetworkStateChangeBroadcast(WifiStateMachine.this.mLastBssid);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mObtainingIpState);
                    return true;
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /* 147460 */:
                    WifiStateMachine.this.handleNetworkDisconnect();
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectedState);
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    SupplicantState state = WifiStateMachine.this.handleSupplicantStateChange(message);
                    if (!SupplicantState.isDriverActive(state)) {
                        if (WifiStateMachine.this.mNetworkInfo.getState() != NetworkInfo.State.DISCONNECTED) {
                            WifiStateMachine.this.handleNetworkDisconnect();
                        }
                        WifiStateMachine.this.log("Detected an interface down, restart driver");
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDriverStoppedState);
                        WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_START_DRIVER);
                        return true;
                    } else if (state == SupplicantState.DISCONNECTED && WifiStateMachine.this.mNetworkInfo.getState() != NetworkInfo.State.DISCONNECTED) {
                        WifiStateMachine.this.handleNetworkDisconnect();
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectedState);
                        return true;
                    } else {
                        return true;
                    }
                case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /* 147463 */:
                    WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiMonitor.AUTHENTICATION_FAILURE_EVENT);
                    return true;
                case WifiMonitor.ASSOCIATION_REJECTION_EVENT /* 147499 */:
                    WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiMonitor.ASSOCIATION_REJECTION_EVENT);
                    return true;
                case WifiManager.CONNECT_NETWORK /* 151553 */:
                    int netId = message.arg1;
                    WifiConfiguration config = (WifiConfiguration) message.obj;
                    if (config != null) {
                        NetworkUpdateResult result = WifiStateMachine.this.mWifiConfigStore.saveNetwork(config);
                        netId = result.getNetworkId();
                    }
                    if (!WifiStateMachine.this.mWifiConfigStore.selectNetwork(netId) || !WifiStateMachine.this.mWifiNative.reconnect()) {
                        WifiStateMachine.this.loge("Failed to connect config: " + config + " netId: " + netId);
                        WifiStateMachine.this.replyToMessage(message, (int) WifiManager.CONNECT_NETWORK_FAILED, 0);
                        return true;
                    }
                    WifiStateMachine.this.mSupplicantStateTracker.sendMessage(WifiManager.CONNECT_NETWORK);
                    WifiStateMachine.this.replyToMessage(message, WifiManager.CONNECT_NETWORK_SUCCEEDED);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectingState);
                    return true;
                case WifiManager.FORGET_NETWORK /* 151556 */:
                    if (WifiStateMachine.this.mWifiConfigStore.forgetNetwork(message.arg1)) {
                        WifiStateMachine.this.replyToMessage(message, WifiManager.FORGET_NETWORK_SUCCEEDED);
                        return true;
                    }
                    WifiStateMachine.this.loge("Failed to forget network");
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.FORGET_NETWORK_FAILED, 0);
                    return true;
                case WifiManager.SAVE_NETWORK /* 151559 */:
                    NetworkUpdateResult result2 = WifiStateMachine.this.mWifiConfigStore.saveNetwork((WifiConfiguration) message.obj);
                    if (result2.getNetworkId() != -1) {
                        WifiStateMachine.this.replyToMessage(message, WifiManager.SAVE_NETWORK_SUCCEEDED);
                        return true;
                    }
                    WifiStateMachine.this.loge("Failed to save network");
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.SAVE_NETWORK_FAILED, 0);
                    return true;
                case WifiManager.START_WPS /* 151562 */:
                    WpsInfo wpsInfo = (WpsInfo) message.obj;
                    switch (wpsInfo.setup) {
                        case 0:
                            wpsResult = WifiStateMachine.this.mWifiConfigStore.startWpsPbc(wpsInfo);
                            break;
                        case 1:
                            wpsResult = WifiStateMachine.this.mWifiConfigStore.startWpsWithPinFromDevice(wpsInfo);
                            break;
                        case 2:
                            wpsResult = WifiStateMachine.this.mWifiConfigStore.startWpsWithPinFromAccessPoint(wpsInfo);
                            break;
                        default:
                            wpsResult = new WpsResult(WpsResult.Status.FAILURE);
                            WifiStateMachine.this.loge("Invalid setup for WPS");
                            break;
                    }
                    if (wpsResult.status == WpsResult.Status.SUCCESS) {
                        WifiStateMachine.this.replyToMessage(message, (int) WifiManager.START_WPS_SUCCEEDED, wpsResult);
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mWpsRunningState);
                        return true;
                    }
                    WifiStateMachine.this.loge("Failed to start WPS with config " + wpsInfo.toString());
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.WPS_FAILED, 0);
                    return true;
                case WifiManager.DISABLE_NETWORK /* 151569 */:
                    if (WifiStateMachine.this.mWifiConfigStore.disableNetwork(message.arg1, 0)) {
                        WifiStateMachine.this.replyToMessage(message, WifiManager.DISABLE_NETWORK_SUCCEEDED);
                        return true;
                    }
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.DISABLE_NETWORK_FAILED, 0);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$L2ConnectedState.class */
    class L2ConnectedState extends State {
        L2ConnectedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiStateMachine.access$16408(WifiStateMachine.this);
            if (WifiStateMachine.this.mEnableRssiPolling) {
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_RSSI_POLL, WifiStateMachine.this.mRssiPollToken, 0);
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void exit() {
            WifiStateMachine.this.handleNetworkDisconnect();
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                    WifiStateMachine.this.noteScanStart(message.arg1, (WorkSource) message.obj);
                    WifiStateMachine.this.startScanNative(1);
                    return true;
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                    if (message.arg1 != 1) {
                        WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_DISCONNECT);
                        WifiStateMachine.this.deferMessage(message);
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_DISCONNECT /* 131145 */:
                    WifiStateMachine.this.mWifiNative.disconnect();
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectingState);
                    return true;
                case WifiStateMachine.CMD_ENABLE_RSSI_POLL /* 131154 */:
                    WifiStateMachine.this.mEnableRssiPolling = message.arg1 == 1;
                    WifiStateMachine.access$16408(WifiStateMachine.this);
                    if (WifiStateMachine.this.mEnableRssiPolling) {
                        WifiStateMachine.this.fetchRssiAndLinkSpeedNative();
                        WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.this.obtainMessage(WifiStateMachine.CMD_RSSI_POLL, WifiStateMachine.this.mRssiPollToken, 0), 3000L);
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_RSSI_POLL /* 131155 */:
                    if (message.arg1 == WifiStateMachine.this.mRssiPollToken) {
                        WifiStateMachine.this.fetchRssiAndLinkSpeedNative();
                        WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.this.obtainMessage(WifiStateMachine.CMD_RSSI_POLL, WifiStateMachine.this.mRssiPollToken, 0), 3000L);
                        return true;
                    }
                    return true;
                case WifiP2pService.DISCONNECT_WIFI_REQUEST /* 143372 */:
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.mWifiNative.disconnect();
                        WifiStateMachine.this.mTemporarilyDisconnectWifi = true;
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectingState);
                        return true;
                    }
                    return true;
                case WifiMonitor.NETWORK_CONNECTION_EVENT /* 147459 */:
                    return true;
                case WifiManager.CONNECT_NETWORK /* 151553 */:
                    int netId = message.arg1;
                    if (WifiStateMachine.this.mWifiInfo.getNetworkId() != netId) {
                        return false;
                    }
                    return true;
                case WifiManager.SAVE_NETWORK /* 151559 */:
                    WifiConfiguration config = (WifiConfiguration) message.obj;
                    NetworkUpdateResult result = WifiStateMachine.this.mWifiConfigStore.saveNetwork(config);
                    if (WifiStateMachine.this.mWifiInfo.getNetworkId() == result.getNetworkId()) {
                        if (result.hasIpChanged()) {
                            WifiStateMachine.this.log("Reconfiguring IP on connection");
                            WifiStateMachine.this.transitionTo(WifiStateMachine.this.mObtainingIpState);
                        }
                        if (result.hasProxyChanged()) {
                            WifiStateMachine.this.log("Reconfiguring proxy on connection");
                            WifiStateMachine.this.updateLinkProperties();
                        }
                    }
                    if (result.getNetworkId() != -1) {
                        WifiStateMachine.this.replyToMessage(message, WifiManager.SAVE_NETWORK_SUCCEEDED);
                        return true;
                    }
                    WifiStateMachine.this.loge("Failed to save network");
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.SAVE_NETWORK_FAILED, 0);
                    return true;
                case WifiManager.RSSI_PKTCNT_FETCH /* 151572 */:
                    RssiPacketCountInfo info = new RssiPacketCountInfo();
                    WifiStateMachine.this.fetchRssiAndLinkSpeedNative();
                    info.rssi = WifiStateMachine.this.mWifiInfo.getRssi();
                    WifiStateMachine.this.fetchPktcntNative(info);
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.RSSI_PKTCNT_FETCH_SUCCEEDED, info);
                    return true;
                case DhcpStateMachine.CMD_PRE_DHCP_ACTION /* 196612 */:
                    WifiStateMachine.this.handlePreDhcpSetup();
                    return true;
                case DhcpStateMachine.CMD_POST_DHCP_ACTION /* 196613 */:
                    WifiStateMachine.this.handlePostDhcpSetup();
                    if (message.arg1 == 1) {
                        WifiStateMachine.this.handleSuccessfulIpConfiguration((DhcpResults) message.obj);
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mVerifyingLinkState);
                        return true;
                    } else if (message.arg1 == 2) {
                        WifiStateMachine.this.handleFailedIpConfiguration();
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectingState);
                        return true;
                    } else {
                        return true;
                    }
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$ObtainingIpState.class */
    class ObtainingIpState extends State {
        ObtainingIpState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            if (!WifiStateMachine.this.mWifiConfigStore.isUsingStaticIp(WifiStateMachine.this.mLastNetworkId)) {
                WifiStateMachine.this.startDhcp();
                return;
            }
            WifiStateMachine.this.stopDhcp();
            DhcpResults dhcpResults = new DhcpResults(WifiStateMachine.this.mWifiConfigStore.getLinkProperties(WifiStateMachine.this.mLastNetworkId));
            InterfaceConfiguration ifcg = new InterfaceConfiguration();
            Iterator<LinkAddress> addrs = dhcpResults.linkProperties.getLinkAddresses().iterator();
            if (!addrs.hasNext()) {
                WifiStateMachine.this.loge("Static IP lacks address");
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_STATIC_IP_FAILURE);
                return;
            }
            ifcg.setLinkAddress(addrs.next());
            ifcg.setInterfaceUp();
            try {
                WifiStateMachine.this.mNwService.setInterfaceConfig(WifiStateMachine.this.mInterfaceName, ifcg);
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_STATIC_IP_SUCCESS, dhcpResults);
            } catch (RemoteException re) {
                WifiStateMachine.this.loge("Static IP configuration failed: " + re);
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_STATIC_IP_FAILURE);
            } catch (IllegalStateException e) {
                WifiStateMachine.this.loge("Static IP configuration failed: " + e);
                WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_STATIC_IP_FAILURE);
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_STATIC_IP_SUCCESS /* 131087 */:
                    WifiStateMachine.this.handleSuccessfulIpConfiguration((DhcpResults) message.obj);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mVerifyingLinkState);
                    return true;
                case WifiStateMachine.CMD_STATIC_IP_FAILURE /* 131088 */:
                    WifiStateMachine.this.handleFailedIpConfiguration();
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectingState);
                    return true;
                case WifiStateMachine.CMD_START_SCAN /* 131143 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_SET_HIGH_PERF_MODE /* 131149 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiManager.SAVE_NETWORK /* 151559 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$VerifyingLinkState.class */
    class VerifyingLinkState extends State {
        VerifyingLinkState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiStateMachine.this.log(getName() + " enter");
            WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.VERIFYING_POOR_LINK);
            WifiStateMachine.this.mWifiConfigStore.updateStatus(WifiStateMachine.this.mLastNetworkId, NetworkInfo.DetailedState.VERIFYING_POOR_LINK);
            WifiStateMachine.this.sendNetworkStateChangeBroadcast(WifiStateMachine.this.mLastBssid);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case 135189:
                    WifiStateMachine.this.log(getName() + " POOR_LINK_DETECTED: no transition");
                    return true;
                case 135190:
                    WifiStateMachine.this.log(getName() + " GOOD_LINK_DETECTED: transition to captive portal check");
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mCaptivePortalCheckState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$CaptivePortalCheckState.class */
    class CaptivePortalCheckState extends State {
        CaptivePortalCheckState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiStateMachine.this.log(getName() + " enter");
            WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK);
            WifiStateMachine.this.mWifiConfigStore.updateStatus(WifiStateMachine.this.mLastNetworkId, NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK);
            WifiStateMachine.this.sendNetworkStateChangeBroadcast(WifiStateMachine.this.mLastBssid);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_CAPTIVE_CHECK_COMPLETE /* 131092 */:
                    WifiStateMachine.this.log(getName() + " CMD_CAPTIVE_CHECK_COMPLETE");
                    try {
                        WifiStateMachine.this.mNwService.enableIpv6(WifiStateMachine.this.mInterfaceName);
                    } catch (RemoteException re) {
                        WifiStateMachine.this.loge("Failed to enable IPv6: " + re);
                    } catch (IllegalStateException e) {
                        WifiStateMachine.this.loge("Failed to enable IPv6: " + e);
                    }
                    WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.CONNECTED);
                    WifiStateMachine.this.mWifiConfigStore.updateStatus(WifiStateMachine.this.mLastNetworkId, NetworkInfo.DetailedState.CONNECTED);
                    WifiStateMachine.this.sendNetworkStateChangeBroadcast(WifiStateMachine.this.mLastBssid);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mConnectedState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$ConnectedState.class */
    class ConnectedState extends State {
        ConnectedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case 135189:
                    try {
                        WifiStateMachine.this.mNwService.disableIpv6(WifiStateMachine.this.mInterfaceName);
                    } catch (RemoteException re) {
                        WifiStateMachine.this.loge("Failed to disable IPv6: " + re);
                    } catch (IllegalStateException e) {
                        WifiStateMachine.this.loge("Failed to disable IPv6: " + e);
                    }
                    WifiStateMachine.this.setNetworkDetailedState(NetworkInfo.DetailedState.DISCONNECTED);
                    WifiStateMachine.this.mWifiConfigStore.updateStatus(WifiStateMachine.this.mLastNetworkId, NetworkInfo.DetailedState.DISCONNECTED);
                    WifiStateMachine.this.sendNetworkStateChangeBroadcast(WifiStateMachine.this.mLastBssid);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mVerifyingLinkState);
                    return true;
                default:
                    return false;
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void exit() {
            WifiStateMachine.this.checkAndSetConnectivityInstance();
            WifiStateMachine.this.mCm.requestNetworkTransitionWakelock(getName());
        }
    }

    /* loaded from: WifiStateMachine$DisconnectingState.class */
    class DisconnectingState extends State {
        DisconnectingState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                    if (message.arg1 != 1) {
                        WifiStateMachine.this.deferMessage(message);
                        return true;
                    }
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    WifiStateMachine.this.deferMessage(message);
                    WifiStateMachine.this.handleNetworkDisconnect();
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectedState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$DisconnectedState.class */
    class DisconnectedState extends State {
        private boolean mAlarmEnabled = false;
        private long mFrameworkScanIntervalMs;

        DisconnectedState() {
        }

        private void setScanAlarm(boolean enabled) {
            if (enabled == this.mAlarmEnabled) {
                return;
            }
            if (!enabled) {
                WifiStateMachine.this.mAlarmManager.cancel(WifiStateMachine.this.mScanIntent);
                this.mAlarmEnabled = false;
            } else if (this.mFrameworkScanIntervalMs > 0) {
                WifiStateMachine.this.mAlarmManager.setRepeating(0, System.currentTimeMillis() + this.mFrameworkScanIntervalMs, this.mFrameworkScanIntervalMs, WifiStateMachine.this.mScanIntent);
                this.mAlarmEnabled = true;
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            if (WifiStateMachine.this.mTemporarilyDisconnectWifi) {
                WifiStateMachine.this.mWifiP2pChannel.sendMessage(WifiP2pService.DISCONNECT_WIFI_RESPONSE);
                return;
            }
            this.mFrameworkScanIntervalMs = Settings.Global.getLong(WifiStateMachine.this.mContext.getContentResolver(), Settings.Global.WIFI_FRAMEWORK_SCAN_INTERVAL_MS, WifiStateMachine.this.mDefaultFrameworkScanIntervalMs);
            if (WifiStateMachine.this.mEnableBackgroundScan) {
                if (!WifiStateMachine.this.mScanResultIsPending) {
                    WifiStateMachine.this.mWifiNative.enableBackgroundScan(true);
                }
            } else {
                setScanAlarm(true);
            }
            if (!WifiStateMachine.this.mP2pConnected.get() && WifiStateMachine.this.mWifiConfigStore.getConfiguredNetworks().size() == 0) {
                WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.this.obtainMessage(WifiStateMachine.CMD_NO_NETWORKS_PERIODIC_SCAN, WifiStateMachine.access$20804(WifiStateMachine.this), 0), WifiStateMachine.this.mSupplicantScanIntervalMs);
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:48:0x0280  */
        @Override // com.android.internal.util.State, com.android.internal.util.IState
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean processMessage(android.os.Message r7) {
            /*
                Method dump skipped, instructions count: 649
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiStateMachine.DisconnectedState.processMessage(android.os.Message):boolean");
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void exit() {
            if (WifiStateMachine.this.mEnableBackgroundScan) {
                WifiStateMachine.this.mWifiNative.enableBackgroundScan(false);
            }
            setScanAlarm(false);
        }
    }

    /* loaded from: WifiStateMachine$WpsRunningState.class */
    class WpsRunningState extends State {
        private Message mSourceMessage;

        WpsRunningState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            this.mSourceMessage = Message.obtain(WifiStateMachine.this.getCurrentMessage());
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_ENABLE_NETWORK /* 131126 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_RECONNECT /* 131146 */:
                case WifiStateMachine.CMD_REASSOCIATE /* 131147 */:
                case WifiManager.CONNECT_NETWORK /* 151553 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiMonitor.NETWORK_CONNECTION_EVENT /* 147459 */:
                    WifiStateMachine.this.replyToMessage(this.mSourceMessage, WifiManager.WPS_COMPLETED);
                    this.mSourceMessage.recycle();
                    this.mSourceMessage = null;
                    WifiStateMachine.this.deferMessage(message);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectedState);
                    return true;
                case WifiMonitor.NETWORK_DISCONNECTION_EVENT /* 147460 */:
                    WifiStateMachine.this.handleNetworkDisconnect();
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /* 147463 */:
                case WifiMonitor.WPS_SUCCESS_EVENT /* 147464 */:
                case WifiMonitor.ASSOCIATION_REJECTION_EVENT /* 147499 */:
                    return true;
                case WifiMonitor.WPS_FAIL_EVENT /* 147465 */:
                    WifiStateMachine.this.replyToMessage(this.mSourceMessage, (int) WifiManager.WPS_FAILED, message.arg1);
                    this.mSourceMessage.recycle();
                    this.mSourceMessage = null;
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectedState);
                    return true;
                case WifiMonitor.WPS_OVERLAP_EVENT /* 147466 */:
                    WifiStateMachine.this.replyToMessage(this.mSourceMessage, (int) WifiManager.WPS_FAILED, 3);
                    this.mSourceMessage.recycle();
                    this.mSourceMessage = null;
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectedState);
                    return true;
                case WifiMonitor.WPS_TIMEOUT_EVENT /* 147467 */:
                    WifiStateMachine.this.replyToMessage(this.mSourceMessage, (int) WifiManager.WPS_FAILED, 7);
                    this.mSourceMessage.recycle();
                    this.mSourceMessage = null;
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectedState);
                    return true;
                case WifiManager.START_WPS /* 151562 */:
                    WifiStateMachine.this.replyToMessage(message, (int) WifiManager.WPS_FAILED, 1);
                    return true;
                case WifiManager.CANCEL_WPS /* 151566 */:
                    if (WifiStateMachine.this.mWifiNative.cancelWps()) {
                        WifiStateMachine.this.replyToMessage(message, WifiManager.CANCEL_WPS_SUCCEDED);
                    } else {
                        WifiStateMachine.this.replyToMessage(message, (int) WifiManager.CANCEL_WPS_FAILED, 0);
                    }
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mDisconnectedState);
                    return true;
                default:
                    return false;
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void exit() {
            WifiStateMachine.this.mWifiConfigStore.enableAllNetworks();
            WifiStateMachine.this.mWifiConfigStore.loadConfiguredNetworks();
        }
    }

    /* loaded from: WifiStateMachine$SoftApStartingState.class */
    class SoftApStartingState extends State {
        SoftApStartingState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            Message message = WifiStateMachine.this.getCurrentMessage();
            if (message.what == WifiStateMachine.CMD_START_AP) {
                WifiConfiguration config = (WifiConfiguration) message.obj;
                if (config == null) {
                    WifiStateMachine.this.mWifiApConfigChannel.sendMessage(WifiStateMachine.CMD_REQUEST_AP_CONFIG);
                    return;
                }
                WifiStateMachine.this.mWifiApConfigChannel.sendMessage(WifiStateMachine.CMD_SET_AP_CONFIG, config);
                WifiStateMachine.this.startSoftApWithConfig(config);
                return;
            }
            throw new RuntimeException("Illegal transition to SoftApStartingState: " + message);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_TETHER_STATE_CHANGE /* 131101 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_START_AP_SUCCESS /* 131094 */:
                    WifiStateMachine.this.setWifiApState(13);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mSoftApStartedState);
                    return true;
                case WifiStateMachine.CMD_START_AP_FAILURE /* 131095 */:
                    WifiStateMachine.this.setWifiApState(14);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mInitialState);
                    return true;
                case WifiStateMachine.CMD_RESPONSE_AP_CONFIG /* 131100 */:
                    WifiConfiguration config = (WifiConfiguration) message.obj;
                    if (config != null) {
                        WifiStateMachine.this.startSoftApWithConfig(config);
                        return true;
                    }
                    WifiStateMachine.this.loge("Softap config is null!");
                    WifiStateMachine.this.sendMessage(WifiStateMachine.CMD_START_AP_FAILURE);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$SoftApStartedState.class */
    class SoftApStartedState extends State {
        SoftApStartedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                    WifiStateMachine.this.loge("Cannot start supplicant with a running soft AP");
                    WifiStateMachine.this.setWifiState(4);
                    return true;
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                    return true;
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                    try {
                        WifiStateMachine.this.mNwService.stopAccessPoint(WifiStateMachine.this.mInterfaceName);
                    } catch (Exception e) {
                        WifiStateMachine.this.loge("Exception in stopAccessPoint()");
                    }
                    WifiStateMachine.this.setWifiApState(11);
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mInitialState);
                    return true;
                case WifiStateMachine.CMD_TETHER_STATE_CHANGE /* 131101 */:
                    TetherStateChange stateChange = (TetherStateChange) message.obj;
                    if (WifiStateMachine.this.startTethering(stateChange.available)) {
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mTetheringState);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$TetheringState.class */
    class TetheringState extends State {
        TetheringState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.this.obtainMessage(WifiStateMachine.CMD_TETHER_NOTIFICATION_TIMED_OUT, WifiStateMachine.access$23104(WifiStateMachine.this), 0), TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_TETHER_STATE_CHANGE /* 131101 */:
                    TetherStateChange stateChange = (TetherStateChange) message.obj;
                    if (WifiStateMachine.this.isWifiTethered(stateChange.active)) {
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mTetheredState);
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_TETHER_NOTIFICATION_TIMED_OUT /* 131102 */:
                    if (message.arg1 == WifiStateMachine.this.mTetherToken) {
                        WifiStateMachine.this.loge("Failed to get tether update, shutdown soft access point");
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mSoftApStartedState);
                        WifiStateMachine.this.sendMessageAtFrontOfQueue((int) WifiStateMachine.CMD_STOP_AP);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$TetheredState.class */
    class TetheredState extends State {
        TetheredState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                    WifiStateMachine.this.setWifiApState(10);
                    WifiStateMachine.this.stopTethering();
                    WifiStateMachine.this.transitionTo(WifiStateMachine.this.mUntetheringState);
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_TETHER_STATE_CHANGE /* 131101 */:
                    TetherStateChange stateChange = (TetherStateChange) message.obj;
                    if (!WifiStateMachine.this.isWifiTethered(stateChange.active)) {
                        WifiStateMachine.this.loge("Tethering reports wifi as untethered!, shut down soft Ap");
                        WifiStateMachine.this.setHostApRunning(null, false);
                        WifiStateMachine.this.setHostApRunning(null, true);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiStateMachine$UntetheringState.class */
    class UntetheringState extends State {
        UntetheringState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiStateMachine.this.sendMessageDelayed(WifiStateMachine.this.obtainMessage(WifiStateMachine.CMD_TETHER_NOTIFICATION_TIMED_OUT, WifiStateMachine.access$23104(WifiStateMachine.this), 0), TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiStateMachine.CMD_START_SUPPLICANT /* 131083 */:
                case WifiStateMachine.CMD_STOP_SUPPLICANT /* 131084 */:
                case WifiStateMachine.CMD_START_DRIVER /* 131085 */:
                case WifiStateMachine.CMD_STOP_DRIVER /* 131086 */:
                case WifiStateMachine.CMD_START_AP /* 131093 */:
                case WifiStateMachine.CMD_STOP_AP /* 131096 */:
                case WifiStateMachine.CMD_SET_OPERATIONAL_MODE /* 131144 */:
                case WifiStateMachine.CMD_SET_COUNTRY_CODE /* 131152 */:
                case WifiStateMachine.CMD_START_PACKET_FILTERING /* 131156 */:
                case WifiStateMachine.CMD_STOP_PACKET_FILTERING /* 131157 */:
                case WifiStateMachine.CMD_SET_FREQUENCY_BAND /* 131162 */:
                    WifiStateMachine.this.deferMessage(message);
                    return true;
                case WifiStateMachine.CMD_TETHER_STATE_CHANGE /* 131101 */:
                    TetherStateChange stateChange = (TetherStateChange) message.obj;
                    if (!WifiStateMachine.this.isWifiTethered(stateChange.active)) {
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mSoftApStartedState);
                        return true;
                    }
                    return true;
                case WifiStateMachine.CMD_TETHER_NOTIFICATION_TIMED_OUT /* 131102 */:
                    if (message.arg1 == WifiStateMachine.this.mTetherToken) {
                        WifiStateMachine.this.loge("Failed to get tether update, force stop access point");
                        WifiStateMachine.this.transitionTo(WifiStateMachine.this.mSoftApStartedState);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyToMessage(Message msg, int what) {
        if (msg.replyTo == null) {
            return;
        }
        Message dstMsg = obtainMessageWithArg2(msg);
        dstMsg.what = what;
        this.mReplyChannel.replyToMessage(msg, dstMsg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyToMessage(Message msg, int what, int arg1) {
        if (msg.replyTo == null) {
            return;
        }
        Message dstMsg = obtainMessageWithArg2(msg);
        dstMsg.what = what;
        dstMsg.arg1 = arg1;
        this.mReplyChannel.replyToMessage(msg, dstMsg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyToMessage(Message msg, int what, Object obj) {
        if (msg.replyTo == null) {
            return;
        }
        Message dstMsg = obtainMessageWithArg2(msg);
        dstMsg.what = what;
        dstMsg.obj = obj;
        this.mReplyChannel.replyToMessage(msg, dstMsg);
    }

    private Message obtainMessageWithArg2(Message srcMsg) {
        Message msg = Message.obtain();
        msg.arg2 = srcMsg.arg2;
        return msg;
    }
}