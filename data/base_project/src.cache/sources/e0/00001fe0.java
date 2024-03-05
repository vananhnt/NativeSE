package com.android.server.wifi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiStateMachine;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.WorkSource;
import android.provider.Settings;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.wifi.WifiService;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WifiController.class */
public class WifiController extends StateMachine {
    private static final String TAG = "WifiController";
    private static final boolean DBG = false;
    private Context mContext;
    private boolean mScreenOff;
    private boolean mDeviceIdle;
    private int mPluggedType;
    private int mStayAwakeConditions;
    private long mIdleMillis;
    private int mSleepPolicy;
    private boolean mFirstUserSignOnSeen;
    private AlarmManager mAlarmManager;
    private PendingIntent mIdleIntent;
    private static final int IDLE_REQUEST = 0;
    private static final long DEFAULT_IDLE_MS = 900000;
    private static final long DEFAULT_REENABLE_DELAY_MS = 500;
    private static final long DEFER_MARGIN_MS = 5;
    NetworkInfo mNetworkInfo;
    private static final String ACTION_DEVICE_IDLE = "com.android.server.WifiManager.action.DEVICE_IDLE";
    final WifiStateMachine mWifiStateMachine;
    final WifiSettingsStore mSettingsStore;
    final WifiService.LockList mLocks;
    private final WorkSource mTmpWorkSource;
    private long mReEnableDelayMillis;
    private static final int BASE = 155648;
    static final int CMD_EMERGENCY_MODE_CHANGED = 155649;
    static final int CMD_SCREEN_ON = 155650;
    static final int CMD_SCREEN_OFF = 155651;
    static final int CMD_BATTERY_CHANGED = 155652;
    static final int CMD_DEVICE_IDLE = 155653;
    static final int CMD_LOCKS_CHANGED = 155654;
    static final int CMD_SCAN_ALWAYS_MODE_CHANGED = 155655;
    static final int CMD_WIFI_TOGGLED = 155656;
    static final int CMD_AIRPLANE_TOGGLED = 155657;
    static final int CMD_SET_AP = 155658;
    static final int CMD_DEFERRED_TOGGLE = 155659;
    static final int CMD_USER_PRESENT = 155660;
    private DefaultState mDefaultState;
    private StaEnabledState mStaEnabledState;
    private ApStaDisabledState mApStaDisabledState;
    private StaDisabledWithScanState mStaDisabledWithScanState;
    private ApEnabledState mApEnabledState;
    private DeviceActiveState mDeviceActiveState;
    private DeviceInactiveState mDeviceInactiveState;
    private ScanOnlyLockHeldState mScanOnlyLockHeldState;
    private FullLockHeldState mFullLockHeldState;
    private FullHighPerfLockHeldState mFullHighPerfLockHeldState;
    private NoLockHeldState mNoLockHeldState;
    private EcmState mEcmState;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiController(Context context, WifiService service, Looper looper) {
        super(TAG, looper);
        this.mFirstUserSignOnSeen = false;
        this.mNetworkInfo = new NetworkInfo(1, 0, "WIFI", "");
        this.mTmpWorkSource = new WorkSource();
        this.mDefaultState = new DefaultState();
        this.mStaEnabledState = new StaEnabledState();
        this.mApStaDisabledState = new ApStaDisabledState();
        this.mStaDisabledWithScanState = new StaDisabledWithScanState();
        this.mApEnabledState = new ApEnabledState();
        this.mDeviceActiveState = new DeviceActiveState();
        this.mDeviceInactiveState = new DeviceInactiveState();
        this.mScanOnlyLockHeldState = new ScanOnlyLockHeldState();
        this.mFullLockHeldState = new FullLockHeldState();
        this.mFullHighPerfLockHeldState = new FullHighPerfLockHeldState();
        this.mNoLockHeldState = new NoLockHeldState();
        this.mEcmState = new EcmState();
        this.mContext = context;
        this.mWifiStateMachine = service.mWifiStateMachine;
        this.mSettingsStore = service.mSettingsStore;
        this.mLocks = service.mLocks;
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        Intent idleIntent = new Intent(ACTION_DEVICE_IDLE, (Uri) null);
        this.mIdleIntent = PendingIntent.getBroadcast(this.mContext, 0, idleIntent, 0);
        addState(this.mDefaultState);
        addState(this.mApStaDisabledState, this.mDefaultState);
        addState(this.mStaEnabledState, this.mDefaultState);
        addState(this.mDeviceActiveState, this.mStaEnabledState);
        addState(this.mDeviceInactiveState, this.mStaEnabledState);
        addState(this.mScanOnlyLockHeldState, this.mDeviceInactiveState);
        addState(this.mFullLockHeldState, this.mDeviceInactiveState);
        addState(this.mFullHighPerfLockHeldState, this.mDeviceInactiveState);
        addState(this.mNoLockHeldState, this.mDeviceInactiveState);
        addState(this.mStaDisabledWithScanState, this.mDefaultState);
        addState(this.mApEnabledState, this.mDefaultState);
        addState(this.mEcmState, this.mDefaultState);
        if (this.mSettingsStore.isScanAlwaysAvailable()) {
            setInitialState(this.mStaDisabledWithScanState);
        } else {
            setInitialState(this.mApStaDisabledState);
        }
        setLogRecSize(100);
        setLogOnlyTransitions(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DEVICE_IDLE);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.wifi.WifiController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (action.equals(WifiController.ACTION_DEVICE_IDLE)) {
                    WifiController.this.sendMessage(WifiController.CMD_DEVICE_IDLE);
                } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    WifiController.this.mNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                }
            }
        }, new IntentFilter(filter));
        initializeAndRegisterForSettingsChange(looper);
    }

    private void initializeAndRegisterForSettingsChange(Looper looper) {
        Handler handler = new Handler(looper);
        readStayAwakeConditions();
        registerForStayAwakeModeChange(handler);
        readWifiIdleTime();
        registerForWifiIdleTimeChange(handler);
        readWifiSleepPolicy();
        registerForWifiSleepPolicyChange(handler);
        readWifiReEnableDelay();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readStayAwakeConditions() {
        this.mStayAwakeConditions = Settings.Global.getInt(this.mContext.getContentResolver(), "stay_on_while_plugged_in", 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readWifiIdleTime() {
        this.mIdleMillis = Settings.Global.getLong(this.mContext.getContentResolver(), "wifi_idle_ms", 900000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readWifiSleepPolicy() {
        this.mSleepPolicy = Settings.Global.getInt(this.mContext.getContentResolver(), "wifi_sleep_policy", 2);
    }

    private void readWifiReEnableDelay() {
        this.mReEnableDelayMillis = Settings.Global.getLong(this.mContext.getContentResolver(), Settings.Global.WIFI_REENABLE_DELAY_MS, DEFAULT_REENABLE_DELAY_MS);
    }

    private void registerForStayAwakeModeChange(Handler handler) {
        ContentObserver contentObserver = new ContentObserver(handler) { // from class: com.android.server.wifi.WifiController.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                WifiController.this.readStayAwakeConditions();
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("stay_on_while_plugged_in"), false, contentObserver);
    }

    private void registerForWifiIdleTimeChange(Handler handler) {
        ContentObserver contentObserver = new ContentObserver(handler) { // from class: com.android.server.wifi.WifiController.3
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                WifiController.this.readWifiIdleTime();
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_idle_ms"), false, contentObserver);
    }

    private void registerForWifiSleepPolicyChange(Handler handler) {
        ContentObserver contentObserver = new ContentObserver(handler) { // from class: com.android.server.wifi.WifiController.4
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                WifiController.this.readWifiSleepPolicy();
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_sleep_policy"), false, contentObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldWifiStayAwake(int pluggedType) {
        if (this.mSleepPolicy == 2) {
            return true;
        }
        if (this.mSleepPolicy == 1 && pluggedType != 0) {
            return true;
        }
        return shouldDeviceStayAwake(pluggedType);
    }

    private boolean shouldDeviceStayAwake(int pluggedType) {
        return (this.mStayAwakeConditions & pluggedType) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBatteryWorkSource() {
        this.mTmpWorkSource.clear();
        if (this.mDeviceIdle) {
            this.mLocks.updateWorkSource(this.mTmpWorkSource);
        }
        this.mWifiStateMachine.updateBatteryWorkSource(this.mTmpWorkSource);
    }

    /* loaded from: WifiController$DefaultState.class */
    class DefaultState extends State {
        DefaultState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiController.CMD_EMERGENCY_MODE_CHANGED /* 155649 */:
                case WifiController.CMD_LOCKS_CHANGED /* 155654 */:
                case WifiController.CMD_SCAN_ALWAYS_MODE_CHANGED /* 155655 */:
                case WifiController.CMD_WIFI_TOGGLED /* 155656 */:
                case WifiController.CMD_AIRPLANE_TOGGLED /* 155657 */:
                case WifiController.CMD_SET_AP /* 155658 */:
                    return true;
                case WifiController.CMD_SCREEN_ON /* 155650 */:
                    WifiController.this.mAlarmManager.cancel(WifiController.this.mIdleIntent);
                    WifiController.this.mScreenOff = false;
                    WifiController.this.mDeviceIdle = false;
                    WifiController.this.updateBatteryWorkSource();
                    return true;
                case WifiController.CMD_SCREEN_OFF /* 155651 */:
                    WifiController.this.mScreenOff = true;
                    if (!WifiController.this.shouldWifiStayAwake(WifiController.this.mPluggedType)) {
                        if (WifiController.this.mNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                            WifiController.this.mAlarmManager.set(0, System.currentTimeMillis() + WifiController.this.mIdleMillis, WifiController.this.mIdleIntent);
                            return true;
                        }
                        WifiController.this.sendMessage(WifiController.CMD_DEVICE_IDLE);
                        return true;
                    }
                    return true;
                case WifiController.CMD_BATTERY_CHANGED /* 155652 */:
                    int pluggedType = msg.arg1;
                    if (WifiController.this.mScreenOff && WifiController.this.shouldWifiStayAwake(WifiController.this.mPluggedType) && !WifiController.this.shouldWifiStayAwake(pluggedType)) {
                        long triggerTime = System.currentTimeMillis() + WifiController.this.mIdleMillis;
                        WifiController.this.mAlarmManager.set(0, triggerTime, WifiController.this.mIdleIntent);
                    }
                    WifiController.this.mPluggedType = pluggedType;
                    return true;
                case WifiController.CMD_DEVICE_IDLE /* 155653 */:
                    WifiController.this.mDeviceIdle = true;
                    WifiController.this.updateBatteryWorkSource();
                    return true;
                case WifiController.CMD_DEFERRED_TOGGLE /* 155659 */:
                    WifiController.this.log("DEFERRED_TOGGLE ignored due to state change");
                    return true;
                case WifiController.CMD_USER_PRESENT /* 155660 */:
                    WifiController.this.mFirstUserSignOnSeen = true;
                    return true;
                default:
                    throw new RuntimeException("WifiController.handleMessage " + msg.what);
            }
        }
    }

    /* loaded from: WifiController$ApStaDisabledState.class */
    class ApStaDisabledState extends State {
        private int mDeferredEnableSerialNumber = 0;
        private boolean mHaveDeferredEnable = false;
        private long mDisabledTimestamp;

        ApStaDisabledState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiController.this.mWifiStateMachine.setSupplicantRunning(false);
            this.mDisabledTimestamp = SystemClock.elapsedRealtime();
            this.mDeferredEnableSerialNumber++;
            this.mHaveDeferredEnable = false;
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiController.CMD_SCAN_ALWAYS_MODE_CHANGED /* 155655 */:
                    if (WifiController.this.mSettingsStore.isScanAlwaysAvailable()) {
                        WifiController.this.transitionTo(WifiController.this.mStaDisabledWithScanState);
                        return true;
                    }
                    return true;
                case WifiController.CMD_WIFI_TOGGLED /* 155656 */:
                case WifiController.CMD_AIRPLANE_TOGGLED /* 155657 */:
                    if (WifiController.this.mSettingsStore.isWifiToggleEnabled()) {
                        if (!doDeferEnable(msg)) {
                            if (!WifiController.this.mDeviceIdle) {
                                WifiController.this.transitionTo(WifiController.this.mDeviceActiveState);
                                return true;
                            }
                            WifiController.this.checkLocksAndTransitionWhenDeviceIdle();
                            return true;
                        }
                        if (this.mHaveDeferredEnable) {
                            this.mDeferredEnableSerialNumber++;
                        }
                        this.mHaveDeferredEnable = !this.mHaveDeferredEnable;
                        return true;
                    }
                    return true;
                case WifiController.CMD_SET_AP /* 155658 */:
                    if (msg.arg1 == 1) {
                        WifiController.this.mWifiStateMachine.setHostApRunning((WifiConfiguration) msg.obj, true);
                        WifiController.this.transitionTo(WifiController.this.mApEnabledState);
                        return true;
                    }
                    return true;
                case WifiController.CMD_DEFERRED_TOGGLE /* 155659 */:
                    if (msg.arg1 != this.mDeferredEnableSerialNumber) {
                        WifiController.this.log("DEFERRED_TOGGLE ignored due to serial mismatch");
                        return true;
                    }
                    WifiController.this.log("DEFERRED_TOGGLE handled");
                    WifiController.this.sendMessage((Message) msg.obj);
                    return true;
                default:
                    return false;
            }
        }

        private boolean doDeferEnable(Message msg) {
            long delaySoFar = SystemClock.elapsedRealtime() - this.mDisabledTimestamp;
            if (delaySoFar < WifiController.this.mReEnableDelayMillis) {
                WifiController.this.log("WifiController msg " + msg + " deferred for " + (WifiController.this.mReEnableDelayMillis - delaySoFar) + "ms");
                Message deferredMsg = WifiController.this.obtainMessage(WifiController.CMD_DEFERRED_TOGGLE);
                deferredMsg.obj = Message.obtain(msg);
                int i = this.mDeferredEnableSerialNumber + 1;
                this.mDeferredEnableSerialNumber = i;
                deferredMsg.arg1 = i;
                WifiController.this.sendMessageDelayed(deferredMsg, (WifiController.this.mReEnableDelayMillis - delaySoFar) + WifiController.DEFER_MARGIN_MS);
                return true;
            }
            return false;
        }
    }

    /* loaded from: WifiController$StaEnabledState.class */
    class StaEnabledState extends State {
        StaEnabledState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiController.this.mWifiStateMachine.setSupplicantRunning(true);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiController.CMD_EMERGENCY_MODE_CHANGED /* 155649 */:
                    if (msg.arg1 == 1) {
                        WifiController.this.transitionTo(WifiController.this.mEcmState);
                        return true;
                    }
                    return false;
                case WifiController.CMD_WIFI_TOGGLED /* 155656 */:
                    if (!WifiController.this.mSettingsStore.isWifiToggleEnabled()) {
                        if (WifiController.this.mSettingsStore.isScanAlwaysAvailable()) {
                            WifiController.this.transitionTo(WifiController.this.mStaDisabledWithScanState);
                            return true;
                        }
                        WifiController.this.transitionTo(WifiController.this.mApStaDisabledState);
                        return true;
                    }
                    return true;
                case WifiController.CMD_AIRPLANE_TOGGLED /* 155657 */:
                    if (!WifiController.this.mSettingsStore.isWifiToggleEnabled()) {
                        WifiController.this.transitionTo(WifiController.this.mApStaDisabledState);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiController$StaDisabledWithScanState.class */
    class StaDisabledWithScanState extends State {
        private int mDeferredEnableSerialNumber = 0;
        private boolean mHaveDeferredEnable = false;
        private long mDisabledTimestamp;

        StaDisabledWithScanState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiController.this.mWifiStateMachine.setSupplicantRunning(true);
            WifiController.this.mWifiStateMachine.setOperationalMode(3);
            WifiController.this.mWifiStateMachine.setDriverStart(true);
            this.mDisabledTimestamp = SystemClock.elapsedRealtime();
            this.mDeferredEnableSerialNumber++;
            this.mHaveDeferredEnable = false;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiController.CMD_SCAN_ALWAYS_MODE_CHANGED /* 155655 */:
                    break;
                case WifiController.CMD_WIFI_TOGGLED /* 155656 */:
                    if (WifiController.this.mSettingsStore.isWifiToggleEnabled()) {
                        if (!doDeferEnable(msg)) {
                            if (!WifiController.this.mDeviceIdle) {
                                WifiController.this.transitionTo(WifiController.this.mDeviceActiveState);
                                return true;
                            }
                            WifiController.this.checkLocksAndTransitionWhenDeviceIdle();
                            return true;
                        }
                        if (this.mHaveDeferredEnable) {
                            this.mDeferredEnableSerialNumber++;
                        }
                        this.mHaveDeferredEnable = !this.mHaveDeferredEnable;
                        return true;
                    }
                    return true;
                case WifiController.CMD_AIRPLANE_TOGGLED /* 155657 */:
                    if (WifiController.this.mSettingsStore.isAirplaneModeOn() && !WifiController.this.mSettingsStore.isWifiToggleEnabled()) {
                        WifiController.this.transitionTo(WifiController.this.mApStaDisabledState);
                        break;
                    }
                    break;
                case WifiController.CMD_SET_AP /* 155658 */:
                    if (msg.arg1 == 1) {
                        WifiController.this.deferMessage(msg);
                        WifiController.this.transitionTo(WifiController.this.mApStaDisabledState);
                        return true;
                    }
                    return true;
                case WifiController.CMD_DEFERRED_TOGGLE /* 155659 */:
                    if (msg.arg1 != this.mDeferredEnableSerialNumber) {
                        WifiController.this.log("DEFERRED_TOGGLE ignored due to serial mismatch");
                        return true;
                    }
                    WifiController.this.logd("DEFERRED_TOGGLE handled");
                    WifiController.this.sendMessage((Message) msg.obj);
                    return true;
                default:
                    return false;
            }
            if (!WifiController.this.mSettingsStore.isScanAlwaysAvailable()) {
                WifiController.this.transitionTo(WifiController.this.mApStaDisabledState);
                return true;
            }
            return true;
        }

        private boolean doDeferEnable(Message msg) {
            long delaySoFar = SystemClock.elapsedRealtime() - this.mDisabledTimestamp;
            if (delaySoFar < WifiController.this.mReEnableDelayMillis) {
                WifiController.this.log("WifiController msg " + msg + " deferred for " + (WifiController.this.mReEnableDelayMillis - delaySoFar) + "ms");
                Message deferredMsg = WifiController.this.obtainMessage(WifiController.CMD_DEFERRED_TOGGLE);
                deferredMsg.obj = Message.obtain(msg);
                int i = this.mDeferredEnableSerialNumber + 1;
                this.mDeferredEnableSerialNumber = i;
                deferredMsg.arg1 = i;
                WifiController.this.sendMessageDelayed(deferredMsg, (WifiController.this.mReEnableDelayMillis - delaySoFar) + WifiController.DEFER_MARGIN_MS);
                return true;
            }
            return false;
        }
    }

    /* loaded from: WifiController$ApEnabledState.class */
    class ApEnabledState extends State {
        ApEnabledState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiController.CMD_AIRPLANE_TOGGLED /* 155657 */:
                    if (WifiController.this.mSettingsStore.isAirplaneModeOn()) {
                        WifiController.this.mWifiStateMachine.setHostApRunning(null, false);
                        WifiController.this.transitionTo(WifiController.this.mApStaDisabledState);
                        return true;
                    }
                    return true;
                case WifiController.CMD_SET_AP /* 155658 */:
                    if (msg.arg1 == 0) {
                        WifiController.this.mWifiStateMachine.setHostApRunning(null, false);
                        WifiController.this.transitionTo(WifiController.this.mApStaDisabledState);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiController$EcmState.class */
    class EcmState extends State {
        EcmState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiController.this.mWifiStateMachine.setSupplicantRunning(false);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            if (msg.what == WifiController.CMD_EMERGENCY_MODE_CHANGED && msg.arg1 == 0) {
                if (WifiController.this.mSettingsStore.isWifiToggleEnabled()) {
                    if (!WifiController.this.mDeviceIdle) {
                        WifiController.this.transitionTo(WifiController.this.mDeviceActiveState);
                        return true;
                    }
                    WifiController.this.checkLocksAndTransitionWhenDeviceIdle();
                    return true;
                } else if (WifiController.this.mSettingsStore.isScanAlwaysAvailable()) {
                    WifiController.this.transitionTo(WifiController.this.mStaDisabledWithScanState);
                    return true;
                } else {
                    WifiController.this.transitionTo(WifiController.this.mApStaDisabledState);
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: WifiController$DeviceActiveState.class */
    class DeviceActiveState extends State {
        DeviceActiveState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiController.this.mWifiStateMachine.setOperationalMode(1);
            WifiController.this.mWifiStateMachine.setDriverStart(true);
            WifiController.this.mWifiStateMachine.setHighPerfModeEnabled(false);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            if (msg.what == WifiController.CMD_DEVICE_IDLE) {
                WifiController.this.checkLocksAndTransitionWhenDeviceIdle();
                return false;
            } else if (msg.what == WifiController.CMD_USER_PRESENT) {
                if (!WifiController.this.mFirstUserSignOnSeen) {
                    WifiController.this.mWifiStateMachine.reloadTlsNetworksAndReconnect();
                }
                WifiController.this.mFirstUserSignOnSeen = true;
                return true;
            } else {
                return false;
            }
        }
    }

    /* loaded from: WifiController$DeviceInactiveState.class */
    class DeviceInactiveState extends State {
        DeviceInactiveState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiController.CMD_SCREEN_ON /* 155650 */:
                    WifiController.this.transitionTo(WifiController.this.mDeviceActiveState);
                    return false;
                case WifiController.CMD_LOCKS_CHANGED /* 155654 */:
                    WifiController.this.checkLocksAndTransitionWhenDeviceIdle();
                    WifiController.this.updateBatteryWorkSource();
                    return true;
                default:
                    return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WifiController$ScanOnlyLockHeldState.class */
    public class ScanOnlyLockHeldState extends State {
        ScanOnlyLockHeldState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiController.this.mWifiStateMachine.setOperationalMode(2);
            WifiController.this.mWifiStateMachine.setDriverStart(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WifiController$FullLockHeldState.class */
    public class FullLockHeldState extends State {
        FullLockHeldState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiController.this.mWifiStateMachine.setOperationalMode(1);
            WifiController.this.mWifiStateMachine.setDriverStart(true);
            WifiController.this.mWifiStateMachine.setHighPerfModeEnabled(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WifiController$FullHighPerfLockHeldState.class */
    public class FullHighPerfLockHeldState extends State {
        FullHighPerfLockHeldState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiController.this.mWifiStateMachine.setOperationalMode(1);
            WifiController.this.mWifiStateMachine.setDriverStart(true);
            WifiController.this.mWifiStateMachine.setHighPerfModeEnabled(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WifiController$NoLockHeldState.class */
    public class NoLockHeldState extends State {
        NoLockHeldState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WifiController.this.mWifiStateMachine.setDriverStart(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkLocksAndTransitionWhenDeviceIdle() {
        if (this.mLocks.hasLocks()) {
            switch (this.mLocks.getStrongestLockMode()) {
                case 1:
                    transitionTo(this.mFullLockHeldState);
                    return;
                case 2:
                    transitionTo(this.mScanOnlyLockHeldState);
                    return;
                case 3:
                    transitionTo(this.mFullHighPerfLockHeldState);
                    return;
                default:
                    loge("Illegal lock " + this.mLocks.getStrongestLockMode());
                    return;
            }
        } else if (this.mSettingsStore.isScanAlwaysAvailable()) {
            transitionTo(this.mScanOnlyLockHeldState);
        } else {
            transitionTo(this.mNoLockHeldState);
        }
    }

    @Override // com.android.internal.util.StateMachine
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        super.dump(fd, pw, args);
        pw.println("mScreenOff " + this.mScreenOff);
        pw.println("mDeviceIdle " + this.mDeviceIdle);
        pw.println("mPluggedType " + this.mPluggedType);
        pw.println("mIdleMillis " + this.mIdleMillis);
        pw.println("mSleepPolicy " + this.mSleepPolicy);
    }
}