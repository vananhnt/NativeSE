package android.net.wifi;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioService;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.LruCache;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.am.ProcessList;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: WifiWatchdogStateMachine.class */
public class WifiWatchdogStateMachine extends StateMachine {
    private static final boolean DBG = false;
    private static final int BASE = 135168;
    private static final int EVENT_WATCHDOG_TOGGLED = 135169;
    private static final int EVENT_NETWORK_STATE_CHANGE = 135170;
    private static final int EVENT_RSSI_CHANGE = 135171;
    private static final int EVENT_SUPPLICANT_STATE_CHANGE = 135172;
    private static final int EVENT_WIFI_RADIO_STATE_CHANGE = 135173;
    private static final int EVENT_WATCHDOG_SETTINGS_CHANGE = 135174;
    private static final int EVENT_BSSID_CHANGE = 135175;
    private static final int EVENT_SCREEN_ON = 135176;
    private static final int EVENT_SCREEN_OFF = 135177;
    private static final int CMD_RSSI_FETCH = 135179;
    static final int POOR_LINK_DETECTED = 135189;
    static final int GOOD_LINK_DETECTED = 135190;
    public static final boolean DEFAULT_POOR_NETWORK_AVOIDANCE_ENABLED = false;
    private static final int LINK_MONITOR_LEVEL_THRESHOLD = 4;
    private static final int BSSID_STAT_CACHE_SIZE = 20;
    private static final int BSSID_STAT_RANGE_LOW_DBM = -105;
    private static final int BSSID_STAT_RANGE_HIGH_DBM = -45;
    private static final int BSSID_STAT_EMPTY_COUNT = 3;
    private static final long LINK_SAMPLING_INTERVAL_MS = 1000;
    private static final double EXP_COEFFICIENT_RECORD = 0.1d;
    private static final double EXP_COEFFICIENT_MONITOR = 0.5d;
    private static final double POOR_LINK_LOSS_THRESHOLD = 0.5d;
    private static final double GOOD_LINK_LOSS_THRESHOLD = 0.1d;
    private static final int POOR_LINK_SAMPLE_COUNT = 3;
    private static final double POOR_LINK_MIN_VOLUME = 2.0d;
    private static final int GOOD_LINK_RSSI_RANGE_MIN = 3;
    private static final int GOOD_LINK_RSSI_RANGE_MAX = 20;
    private Context mContext;
    private ContentResolver mContentResolver;
    private WifiManager mWifiManager;
    private IntentFilter mIntentFilter;
    private BroadcastReceiver mBroadcastReceiver;
    private AsyncChannel mWsmChannel;
    private WifiInfo mWifiInfo;
    private LinkProperties mLinkProperties;
    private boolean mPoorNetworkDetectionEnabled;
    private LruCache<String, BssidStatistics> mBssidCache;
    private int mRssiFetchToken;
    private int mCurrentSignalLevel;
    private BssidStatistics mCurrentBssid;
    private VolumeWeightedEMA mCurrentLoss;
    private boolean mIsScreenOn;
    private static double[] sPresetLoss;
    private DefaultState mDefaultState;
    private WatchdogDisabledState mWatchdogDisabledState;
    private WatchdogEnabledState mWatchdogEnabledState;
    private NotConnectedState mNotConnectedState;
    private VerifyingLinkState mVerifyingLinkState;
    private ConnectedState mConnectedState;
    private OnlineWatchState mOnlineWatchState;
    private LinkMonitoringState mLinkMonitoringState;
    private OnlineState mOnlineState;
    private static final GoodLinkTarget[] GOOD_LINK_TARGET = {new GoodLinkTarget(0, 3, ProcessList.PSS_MAX_INTERVAL), new GoodLinkTarget(3, 5, 300000), new GoodLinkTarget(6, 10, 60000), new GoodLinkTarget(9, 30, 0)};
    private static final MaxAvoidTime[] MAX_AVOID_TIME = {new MaxAvoidTime(ProcessList.PSS_MAX_INTERVAL, AudioService.STREAM_REMOTE_MUSIC), new MaxAvoidTime(300000, -70), new MaxAvoidTime(0, -55)};
    private static boolean sWifiOnly = false;

    static /* synthetic */ int access$2504(WifiWatchdogStateMachine x0) {
        int i = x0.mRssiFetchToken + 1;
        x0.mRssiFetchToken = i;
        return i;
    }

    private WifiWatchdogStateMachine(Context context) {
        super("WifiWatchdogStateMachine");
        this.mWsmChannel = new AsyncChannel();
        this.mBssidCache = new LruCache<>(20);
        this.mRssiFetchToken = 0;
        this.mIsScreenOn = true;
        this.mDefaultState = new DefaultState();
        this.mWatchdogDisabledState = new WatchdogDisabledState();
        this.mWatchdogEnabledState = new WatchdogEnabledState();
        this.mNotConnectedState = new NotConnectedState();
        this.mVerifyingLinkState = new VerifyingLinkState();
        this.mConnectedState = new ConnectedState();
        this.mOnlineWatchState = new OnlineWatchState();
        this.mLinkMonitoringState = new LinkMonitoringState();
        this.mOnlineState = new OnlineState();
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mWsmChannel.connectSync(this.mContext, getHandler(), this.mWifiManager.getWifiStateMachineMessenger());
        setupNetworkReceiver();
        registerForSettingsChanges();
        registerForWatchdogToggle();
        addState(this.mDefaultState);
        addState(this.mWatchdogDisabledState, this.mDefaultState);
        addState(this.mWatchdogEnabledState, this.mDefaultState);
        addState(this.mNotConnectedState, this.mWatchdogEnabledState);
        addState(this.mVerifyingLinkState, this.mWatchdogEnabledState);
        addState(this.mConnectedState, this.mWatchdogEnabledState);
        addState(this.mOnlineWatchState, this.mConnectedState);
        addState(this.mLinkMonitoringState, this.mConnectedState);
        addState(this.mOnlineState, this.mConnectedState);
        if (isWatchdogEnabled()) {
            setInitialState(this.mNotConnectedState);
        } else {
            setInitialState(this.mWatchdogDisabledState);
        }
        setLogRecSize(25);
        setLogOnlyTransitions(true);
        updateSettings();
    }

    public static WifiWatchdogStateMachine makeWifiWatchdogStateMachine(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        sWifiOnly = !cm.isNetworkSupported(0);
        putSettingsGlobalBoolean(contentResolver, "wifi_watchdog_on", true);
        WifiWatchdogStateMachine wwsm = new WifiWatchdogStateMachine(context);
        wwsm.start();
        return wwsm;
    }

    private void setupNetworkReceiver() {
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: android.net.wifi.WifiWatchdogStateMachine.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
                    WifiWatchdogStateMachine.this.obtainMessage(WifiWatchdogStateMachine.EVENT_RSSI_CHANGE, intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, AudioService.STREAM_REMOTE_MUSIC), 0).sendToTarget();
                } else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                    WifiWatchdogStateMachine.this.sendMessage(WifiWatchdogStateMachine.EVENT_SUPPLICANT_STATE_CHANGE, intent);
                } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    WifiWatchdogStateMachine.this.sendMessage(WifiWatchdogStateMachine.EVENT_NETWORK_STATE_CHANGE, intent);
                } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                    WifiWatchdogStateMachine.this.sendMessage(WifiWatchdogStateMachine.EVENT_SCREEN_ON);
                } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    WifiWatchdogStateMachine.this.sendMessage(WifiWatchdogStateMachine.EVENT_SCREEN_OFF);
                } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    WifiWatchdogStateMachine.this.sendMessage(WifiWatchdogStateMachine.EVENT_WIFI_RADIO_STATE_CHANGE, intent.getIntExtra("wifi_state", 4));
                }
            }
        };
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.mIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        this.mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        this.mIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        this.mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        this.mContext.registerReceiver(this.mBroadcastReceiver, this.mIntentFilter);
    }

    private void registerForWatchdogToggle() {
        ContentObserver contentObserver = new ContentObserver(getHandler()) { // from class: android.net.wifi.WifiWatchdogStateMachine.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                WifiWatchdogStateMachine.this.sendMessage(WifiWatchdogStateMachine.EVENT_WATCHDOG_TOGGLED);
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_watchdog_on"), false, contentObserver);
    }

    private void registerForSettingsChanges() {
        ContentObserver contentObserver = new ContentObserver(getHandler()) { // from class: android.net.wifi.WifiWatchdogStateMachine.3
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                WifiWatchdogStateMachine.this.sendMessage(WifiWatchdogStateMachine.EVENT_WATCHDOG_SETTINGS_CHANGE);
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(Settings.Global.WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED), false, contentObserver);
    }

    @Override // com.android.internal.util.StateMachine
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        super.dump(fd, pw, args);
        pw.println("mWifiInfo: [" + this.mWifiInfo + "]");
        pw.println("mLinkProperties: [" + this.mLinkProperties + "]");
        pw.println("mCurrentSignalLevel: [" + this.mCurrentSignalLevel + "]");
        pw.println("mPoorNetworkDetectionEnabled: [" + this.mPoorNetworkDetectionEnabled + "]");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isWatchdogEnabled() {
        boolean ret = getSettingsGlobalBoolean(this.mContentResolver, "wifi_watchdog_on", true);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSettings() {
        if (sWifiOnly) {
            logd("Disabling poor network avoidance for wi-fi only device");
            this.mPoorNetworkDetectionEnabled = false;
            return;
        }
        this.mPoorNetworkDetectionEnabled = getSettingsGlobalBoolean(this.mContentResolver, Settings.Global.WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED, false);
    }

    /* loaded from: WifiWatchdogStateMachine$DefaultState.class */
    class DefaultState extends State {
        DefaultState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiWatchdogStateMachine.EVENT_NETWORK_STATE_CHANGE /* 135170 */:
                case WifiWatchdogStateMachine.EVENT_SUPPLICANT_STATE_CHANGE /* 135172 */:
                case WifiWatchdogStateMachine.EVENT_WIFI_RADIO_STATE_CHANGE /* 135173 */:
                case WifiWatchdogStateMachine.EVENT_BSSID_CHANGE /* 135175 */:
                case WifiWatchdogStateMachine.CMD_RSSI_FETCH /* 135179 */:
                case WifiManager.RSSI_PKTCNT_FETCH_SUCCEEDED /* 151573 */:
                case WifiManager.RSSI_PKTCNT_FETCH_FAILED /* 151574 */:
                    return true;
                case WifiWatchdogStateMachine.EVENT_RSSI_CHANGE /* 135171 */:
                    WifiWatchdogStateMachine.this.mCurrentSignalLevel = WifiWatchdogStateMachine.this.calculateSignalLevel(msg.arg1);
                    return true;
                case WifiWatchdogStateMachine.EVENT_WATCHDOG_SETTINGS_CHANGE /* 135174 */:
                    WifiWatchdogStateMachine.this.updateSettings();
                    return true;
                case WifiWatchdogStateMachine.EVENT_SCREEN_ON /* 135176 */:
                    WifiWatchdogStateMachine.this.mIsScreenOn = true;
                    return true;
                case WifiWatchdogStateMachine.EVENT_SCREEN_OFF /* 135177 */:
                    WifiWatchdogStateMachine.this.mIsScreenOn = false;
                    return true;
                default:
                    WifiWatchdogStateMachine.this.loge("Unhandled message " + msg + " in state " + WifiWatchdogStateMachine.this.getCurrentState().getName());
                    return true;
            }
        }
    }

    /* loaded from: WifiWatchdogStateMachine$WatchdogDisabledState.class */
    class WatchdogDisabledState extends State {
        WatchdogDisabledState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiWatchdogStateMachine.EVENT_WATCHDOG_TOGGLED /* 135169 */:
                    if (WifiWatchdogStateMachine.this.isWatchdogEnabled()) {
                        WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mNotConnectedState);
                        return true;
                    }
                    return true;
                case WifiWatchdogStateMachine.EVENT_NETWORK_STATE_CHANGE /* 135170 */:
                    Intent intent = (Intent) msg.obj;
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    switch (networkInfo.getDetailedState()) {
                        case VERIFYING_POOR_LINK:
                            WifiWatchdogStateMachine.this.sendLinkStatusNotification(true);
                            return false;
                        default:
                            return false;
                    }
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiWatchdogStateMachine$WatchdogEnabledState.class */
    class WatchdogEnabledState extends State {
        WatchdogEnabledState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiWatchdogStateMachine.EVENT_WATCHDOG_TOGGLED /* 135169 */:
                    if (!WifiWatchdogStateMachine.this.isWatchdogEnabled()) {
                        WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mWatchdogDisabledState);
                        return true;
                    }
                    return true;
                case WifiWatchdogStateMachine.EVENT_NETWORK_STATE_CHANGE /* 135170 */:
                    Intent intent = (Intent) msg.obj;
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    WifiWatchdogStateMachine.this.mWifiInfo = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    WifiWatchdogStateMachine.this.updateCurrentBssid(WifiWatchdogStateMachine.this.mWifiInfo != null ? WifiWatchdogStateMachine.this.mWifiInfo.getBSSID() : null);
                    switch (networkInfo.getDetailedState()) {
                        case VERIFYING_POOR_LINK:
                            WifiWatchdogStateMachine.this.mLinkProperties = (LinkProperties) intent.getParcelableExtra("linkProperties");
                            if (WifiWatchdogStateMachine.this.mPoorNetworkDetectionEnabled) {
                                if (WifiWatchdogStateMachine.this.mWifiInfo == null || WifiWatchdogStateMachine.this.mCurrentBssid == null) {
                                    WifiWatchdogStateMachine.this.loge("Ignore, wifiinfo " + WifiWatchdogStateMachine.this.mWifiInfo + " bssid " + WifiWatchdogStateMachine.this.mCurrentBssid);
                                    WifiWatchdogStateMachine.this.sendLinkStatusNotification(true);
                                    return true;
                                }
                                WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mVerifyingLinkState);
                                return true;
                            }
                            WifiWatchdogStateMachine.this.sendLinkStatusNotification(true);
                            return true;
                        case CONNECTED:
                            WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mOnlineWatchState);
                            return true;
                        default:
                            WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mNotConnectedState);
                            return true;
                    }
                case WifiWatchdogStateMachine.EVENT_RSSI_CHANGE /* 135171 */:
                default:
                    return false;
                case WifiWatchdogStateMachine.EVENT_SUPPLICANT_STATE_CHANGE /* 135172 */:
                    SupplicantState supplicantState = (SupplicantState) ((Intent) msg.obj).getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                    if (supplicantState == SupplicantState.COMPLETED) {
                        WifiWatchdogStateMachine.this.mWifiInfo = WifiWatchdogStateMachine.this.mWifiManager.getConnectionInfo();
                        WifiWatchdogStateMachine.this.updateCurrentBssid(WifiWatchdogStateMachine.this.mWifiInfo.getBSSID());
                        return true;
                    }
                    return true;
                case WifiWatchdogStateMachine.EVENT_WIFI_RADIO_STATE_CHANGE /* 135173 */:
                    if (msg.arg1 == 0) {
                        WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mNotConnectedState);
                        return true;
                    }
                    return true;
            }
        }
    }

    /* loaded from: WifiWatchdogStateMachine$NotConnectedState.class */
    class NotConnectedState extends State {
        NotConnectedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }
    }

    /* loaded from: WifiWatchdogStateMachine$VerifyingLinkState.class */
    class VerifyingLinkState extends State {
        private int mSampleCount;

        VerifyingLinkState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            this.mSampleCount = 0;
            WifiWatchdogStateMachine.this.mCurrentBssid.newLinkDetected();
            WifiWatchdogStateMachine.this.sendMessage(WifiWatchdogStateMachine.this.obtainMessage(WifiWatchdogStateMachine.CMD_RSSI_FETCH, WifiWatchdogStateMachine.access$2504(WifiWatchdogStateMachine.this), 0));
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiWatchdogStateMachine.EVENT_WATCHDOG_SETTINGS_CHANGE /* 135174 */:
                    WifiWatchdogStateMachine.this.updateSettings();
                    if (!WifiWatchdogStateMachine.this.mPoorNetworkDetectionEnabled) {
                        WifiWatchdogStateMachine.this.sendLinkStatusNotification(true);
                        return true;
                    }
                    return true;
                case WifiWatchdogStateMachine.EVENT_BSSID_CHANGE /* 135175 */:
                    WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mVerifyingLinkState);
                    return true;
                case WifiWatchdogStateMachine.CMD_RSSI_FETCH /* 135179 */:
                    if (msg.arg1 == WifiWatchdogStateMachine.this.mRssiFetchToken) {
                        WifiWatchdogStateMachine.this.mWsmChannel.sendMessage(WifiManager.RSSI_PKTCNT_FETCH);
                        WifiWatchdogStateMachine.this.sendMessageDelayed(WifiWatchdogStateMachine.this.obtainMessage(WifiWatchdogStateMachine.CMD_RSSI_FETCH, WifiWatchdogStateMachine.access$2504(WifiWatchdogStateMachine.this), 0), 1000L);
                        return true;
                    }
                    return true;
                case WifiManager.RSSI_PKTCNT_FETCH_SUCCEEDED /* 151573 */:
                    RssiPacketCountInfo info = (RssiPacketCountInfo) msg.obj;
                    int rssi = info.rssi;
                    long time = WifiWatchdogStateMachine.this.mCurrentBssid.mBssidAvoidTimeMax - SystemClock.elapsedRealtime();
                    if (time <= 0) {
                        WifiWatchdogStateMachine.this.sendLinkStatusNotification(true);
                        return true;
                    } else if (rssi >= WifiWatchdogStateMachine.this.mCurrentBssid.mGoodLinkTargetRssi) {
                        int i = this.mSampleCount + 1;
                        this.mSampleCount = i;
                        if (i < WifiWatchdogStateMachine.this.mCurrentBssid.mGoodLinkTargetCount) {
                            return true;
                        }
                        WifiWatchdogStateMachine.this.mCurrentBssid.mBssidAvoidTimeMax = 0L;
                        WifiWatchdogStateMachine.this.sendLinkStatusNotification(true);
                        return true;
                    } else {
                        this.mSampleCount = 0;
                        return true;
                    }
                case WifiManager.RSSI_PKTCNT_FETCH_FAILED /* 151574 */:
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiWatchdogStateMachine$ConnectedState.class */
    class ConnectedState extends State {
        ConnectedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiWatchdogStateMachine.EVENT_WATCHDOG_SETTINGS_CHANGE /* 135174 */:
                    WifiWatchdogStateMachine.this.updateSettings();
                    if (WifiWatchdogStateMachine.this.mPoorNetworkDetectionEnabled) {
                        WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mOnlineWatchState);
                        return true;
                    }
                    WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mOnlineState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiWatchdogStateMachine$OnlineWatchState.class */
    class OnlineWatchState extends State {
        OnlineWatchState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            if (!WifiWatchdogStateMachine.this.mPoorNetworkDetectionEnabled) {
                WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mOnlineState);
            } else {
                handleRssiChange();
            }
        }

        private void handleRssiChange() {
            if (WifiWatchdogStateMachine.this.mCurrentSignalLevel <= 4 && WifiWatchdogStateMachine.this.mCurrentBssid != null) {
                WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mLinkMonitoringState);
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiWatchdogStateMachine.EVENT_RSSI_CHANGE /* 135171 */:
                    WifiWatchdogStateMachine.this.mCurrentSignalLevel = WifiWatchdogStateMachine.this.calculateSignalLevel(msg.arg1);
                    handleRssiChange();
                    return true;
                default:
                    return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WifiWatchdogStateMachine$LinkMonitoringState.class */
    public class LinkMonitoringState extends State {
        private int mSampleCount;
        private int mLastRssi;
        private int mLastTxGood;
        private int mLastTxBad;

        LinkMonitoringState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            this.mSampleCount = 0;
            WifiWatchdogStateMachine.this.mCurrentLoss = new VolumeWeightedEMA(0.5d);
            WifiWatchdogStateMachine.this.sendMessage(WifiWatchdogStateMachine.this.obtainMessage(WifiWatchdogStateMachine.CMD_RSSI_FETCH, WifiWatchdogStateMachine.access$2504(WifiWatchdogStateMachine.this), 0));
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiWatchdogStateMachine.EVENT_RSSI_CHANGE /* 135171 */:
                    WifiWatchdogStateMachine.this.mCurrentSignalLevel = WifiWatchdogStateMachine.this.calculateSignalLevel(msg.arg1);
                    if (WifiWatchdogStateMachine.this.mCurrentSignalLevel > 4) {
                        WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mOnlineWatchState);
                        return true;
                    }
                    return true;
                case WifiWatchdogStateMachine.EVENT_BSSID_CHANGE /* 135175 */:
                    WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mLinkMonitoringState);
                    return true;
                case WifiWatchdogStateMachine.CMD_RSSI_FETCH /* 135179 */:
                    if (!WifiWatchdogStateMachine.this.mIsScreenOn) {
                        WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mOnlineState);
                        return true;
                    } else if (msg.arg1 == WifiWatchdogStateMachine.this.mRssiFetchToken) {
                        WifiWatchdogStateMachine.this.mWsmChannel.sendMessage(WifiManager.RSSI_PKTCNT_FETCH);
                        WifiWatchdogStateMachine.this.sendMessageDelayed(WifiWatchdogStateMachine.this.obtainMessage(WifiWatchdogStateMachine.CMD_RSSI_FETCH, WifiWatchdogStateMachine.access$2504(WifiWatchdogStateMachine.this), 0), 1000L);
                        return true;
                    } else {
                        return true;
                    }
                case WifiManager.RSSI_PKTCNT_FETCH_SUCCEEDED /* 151573 */:
                    RssiPacketCountInfo info = (RssiPacketCountInfo) msg.obj;
                    int rssi = info.rssi;
                    int mrssi = (this.mLastRssi + rssi) / 2;
                    int txbad = info.txbad;
                    int txgood = info.txgood;
                    long now = SystemClock.elapsedRealtime();
                    if (now - WifiWatchdogStateMachine.this.mCurrentBssid.mLastTimeSample < 2000) {
                        int dbad = txbad - this.mLastTxBad;
                        int dgood = txgood - this.mLastTxGood;
                        int dtotal = dbad + dgood;
                        if (dtotal > 0) {
                            double loss = dbad / dtotal;
                            WifiWatchdogStateMachine.this.mCurrentLoss.update(loss, dtotal);
                            WifiWatchdogStateMachine.this.mCurrentBssid.updateLoss(mrssi, loss, dtotal);
                            if (WifiWatchdogStateMachine.this.mCurrentLoss.mValue > 0.5d && WifiWatchdogStateMachine.this.mCurrentLoss.mVolume > WifiWatchdogStateMachine.POOR_LINK_MIN_VOLUME) {
                                int i = this.mSampleCount + 1;
                                this.mSampleCount = i;
                                if (i >= 3 && WifiWatchdogStateMachine.this.mCurrentBssid.poorLinkDetected(rssi)) {
                                    WifiWatchdogStateMachine.this.sendLinkStatusNotification(false);
                                    WifiWatchdogStateMachine.access$2504(WifiWatchdogStateMachine.this);
                                }
                            } else {
                                this.mSampleCount = 0;
                            }
                        }
                    }
                    WifiWatchdogStateMachine.this.mCurrentBssid.mLastTimeSample = now;
                    this.mLastTxBad = txbad;
                    this.mLastTxGood = txgood;
                    this.mLastRssi = rssi;
                    return true;
                case WifiManager.RSSI_PKTCNT_FETCH_FAILED /* 151574 */:
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiWatchdogStateMachine$OnlineState.class */
    class OnlineState extends State {
        OnlineState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiWatchdogStateMachine.EVENT_SCREEN_ON /* 135176 */:
                    WifiWatchdogStateMachine.this.mIsScreenOn = true;
                    if (WifiWatchdogStateMachine.this.mPoorNetworkDetectionEnabled) {
                        WifiWatchdogStateMachine.this.transitionTo(WifiWatchdogStateMachine.this.mOnlineWatchState);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCurrentBssid(String bssid) {
        if (bssid == null) {
            if (this.mCurrentBssid == null) {
                return;
            }
            this.mCurrentBssid = null;
            sendMessage(EVENT_BSSID_CHANGE);
        } else if (this.mCurrentBssid == null || !bssid.equals(this.mCurrentBssid.mBssid)) {
            this.mCurrentBssid = this.mBssidCache.get(bssid);
            if (this.mCurrentBssid == null) {
                this.mCurrentBssid = new BssidStatistics(bssid);
                this.mBssidCache.put(bssid, this.mCurrentBssid);
            }
            sendMessage(EVENT_BSSID_CHANGE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int calculateSignalLevel(int rssi) {
        int signalLevel = WifiManager.calculateSignalLevel(rssi, 5);
        return signalLevel;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendLinkStatusNotification(boolean isGood) {
        if (isGood) {
            this.mWsmChannel.sendMessage(GOOD_LINK_DETECTED);
            if (this.mCurrentBssid != null) {
                this.mCurrentBssid.mLastTimeGood = SystemClock.elapsedRealtime();
                return;
            }
            return;
        }
        this.mWsmChannel.sendMessage(POOR_LINK_DETECTED);
        if (this.mCurrentBssid != null) {
            this.mCurrentBssid.mLastTimePoor = SystemClock.elapsedRealtime();
        }
        logd("Poor link notification is sent");
    }

    private static boolean getSettingsGlobalBoolean(ContentResolver cr, String name, boolean def) {
        return Settings.Global.getInt(cr, name, def ? 1 : 0) == 1;
    }

    private static boolean putSettingsGlobalBoolean(ContentResolver cr, String name, boolean value) {
        return Settings.Global.putInt(cr, name, value ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiWatchdogStateMachine$GoodLinkTarget.class */
    public static class GoodLinkTarget {
        public final int RSSI_ADJ_DBM;
        public final int SAMPLE_COUNT;
        public final int REDUCE_TIME_MS;

        public GoodLinkTarget(int adj, int count, int time) {
            this.RSSI_ADJ_DBM = adj;
            this.SAMPLE_COUNT = count;
            this.REDUCE_TIME_MS = time;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiWatchdogStateMachine$MaxAvoidTime.class */
    public static class MaxAvoidTime {
        public final int TIME_MS;
        public final int MIN_RSSI_DBM;

        public MaxAvoidTime(int time, int rssi) {
            this.TIME_MS = time;
            this.MIN_RSSI_DBM = rssi;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiWatchdogStateMachine$VolumeWeightedEMA.class */
    public class VolumeWeightedEMA {
        private double mValue = 0.0d;
        private double mVolume = 0.0d;
        private double mProduct = 0.0d;
        private final double mAlpha;

        public VolumeWeightedEMA(double coefficient) {
            this.mAlpha = coefficient;
        }

        public void update(double newValue, int newVolume) {
            if (newVolume <= 0) {
                return;
            }
            double newProduct = newValue * newVolume;
            this.mProduct = (this.mAlpha * newProduct) + ((1.0d - this.mAlpha) * this.mProduct);
            this.mVolume = (this.mAlpha * newVolume) + ((1.0d - this.mAlpha) * this.mVolume);
            this.mValue = this.mProduct / this.mVolume;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiWatchdogStateMachine$BssidStatistics.class */
    public class BssidStatistics {
        private final String mBssid;
        private int mGoodLinkTargetRssi;
        private int mGoodLinkTargetCount;
        private int mGoodLinkTargetIndex;
        private long mLastTimeSample;
        private long mLastTimeGood;
        private long mLastTimePoor;
        private long mBssidAvoidTimeMax;
        private int mRssiBase = -105;
        private int mEntriesSize = 61;
        private VolumeWeightedEMA[] mEntries = new VolumeWeightedEMA[this.mEntriesSize];

        public BssidStatistics(String bssid) {
            this.mBssid = bssid;
            for (int i = 0; i < this.mEntriesSize; i++) {
                this.mEntries[i] = new VolumeWeightedEMA(0.1d);
            }
        }

        public void updateLoss(int rssi, double value, int volume) {
            int index;
            if (volume > 0 && (index = rssi - this.mRssiBase) >= 0 && index < this.mEntriesSize) {
                this.mEntries[index].update(value, volume);
            }
        }

        public double presetLoss(int rssi) {
            if (rssi <= -90) {
                return 1.0d;
            }
            if (rssi > 0) {
                return 0.0d;
            }
            if (WifiWatchdogStateMachine.sPresetLoss == null) {
                double[] unused = WifiWatchdogStateMachine.sPresetLoss = new double[90];
                for (int i = 0; i < 90; i++) {
                    WifiWatchdogStateMachine.sPresetLoss[i] = 1.0d / Math.pow(90 - i, 1.5d);
                }
            }
            return WifiWatchdogStateMachine.sPresetLoss[-rssi];
        }

        public boolean poorLinkDetected(int rssi) {
            long now = SystemClock.elapsedRealtime();
            long j = now - this.mLastTimeGood;
            long lastPoor = now - this.mLastTimePoor;
            while (this.mGoodLinkTargetIndex > 0 && lastPoor >= WifiWatchdogStateMachine.GOOD_LINK_TARGET[this.mGoodLinkTargetIndex - 1].REDUCE_TIME_MS) {
                this.mGoodLinkTargetIndex--;
            }
            this.mGoodLinkTargetCount = WifiWatchdogStateMachine.GOOD_LINK_TARGET[this.mGoodLinkTargetIndex].SAMPLE_COUNT;
            int from = rssi + 3;
            int to = rssi + 20;
            this.mGoodLinkTargetRssi = findRssiTarget(from, to, 0.1d);
            this.mGoodLinkTargetRssi += WifiWatchdogStateMachine.GOOD_LINK_TARGET[this.mGoodLinkTargetIndex].RSSI_ADJ_DBM;
            if (this.mGoodLinkTargetIndex < WifiWatchdogStateMachine.GOOD_LINK_TARGET.length - 1) {
                this.mGoodLinkTargetIndex++;
            }
            int p = 0;
            int pmax = WifiWatchdogStateMachine.MAX_AVOID_TIME.length - 1;
            while (p < pmax && rssi >= WifiWatchdogStateMachine.MAX_AVOID_TIME[p + 1].MIN_RSSI_DBM) {
                p++;
            }
            long avoidMax = WifiWatchdogStateMachine.MAX_AVOID_TIME[p].TIME_MS;
            if (avoidMax <= 0) {
                return false;
            }
            this.mBssidAvoidTimeMax = now + avoidMax;
            return true;
        }

        public void newLinkDetected() {
            if (this.mBssidAvoidTimeMax > 0) {
                return;
            }
            this.mGoodLinkTargetRssi = findRssiTarget(-105, WifiWatchdogStateMachine.BSSID_STAT_RANGE_HIGH_DBM, 0.1d);
            this.mGoodLinkTargetCount = 1;
            this.mBssidAvoidTimeMax = SystemClock.elapsedRealtime() + WifiWatchdogStateMachine.MAX_AVOID_TIME[0].TIME_MS;
        }

        public int findRssiTarget(int from, int to, double threshold) {
            int from2 = from - this.mRssiBase;
            int to2 = to - this.mRssiBase;
            int emptyCount = 0;
            int d = from2 < to2 ? 1 : -1;
            int i = from2;
            while (true) {
                int i2 = i;
                if (i2 != to2) {
                    if (i2 >= 0 && i2 < this.mEntriesSize && this.mEntries[i2].mVolume > 1.0d) {
                        emptyCount = 0;
                        if (this.mEntries[i2].mValue < threshold) {
                            return this.mRssiBase + i2;
                        }
                    } else {
                        emptyCount++;
                        if (emptyCount >= 3) {
                            int rssi = this.mRssiBase + i2;
                            double lossPreset = presetLoss(rssi);
                            if (lossPreset < threshold) {
                                return rssi;
                            }
                        } else {
                            continue;
                        }
                    }
                    i = i2 + d;
                } else {
                    return this.mRssiBase + to2;
                }
            }
        }
    }
}