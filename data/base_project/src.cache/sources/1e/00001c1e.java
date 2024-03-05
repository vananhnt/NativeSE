package com.android.server;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothTetheringDataTracker;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.CaptivePortalTracker;
import android.net.ConnectivityManager;
import android.net.DummyDataStateTracker;
import android.net.EthernetDataTracker;
import android.net.IConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkPolicyListener;
import android.net.INetworkPolicyManager;
import android.net.INetworkStatsService;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.LinkQualityInfo;
import android.net.MobileDataStateTracker;
import android.net.NetworkConfig;
import android.net.NetworkInfo;
import android.net.NetworkState;
import android.net.NetworkStateTracker;
import android.net.NetworkUtils;
import android.net.ProxyProperties;
import android.net.RouteInfo;
import android.net.SamplingDataTracker;
import android.net.Uri;
import android.net.wifi.WifiStateTracker;
import android.net.wimax.WimaxManagerConstants;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.FileUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.security.Credentials;
import android.security.KeyStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.am.BatteryStatsService;
import com.android.server.connectivity.DataConnectionStats;
import com.android.server.connectivity.Nat464Xlat;
import com.android.server.connectivity.PacManager;
import com.android.server.connectivity.Tethering;
import com.android.server.connectivity.Vpn;
import com.android.server.net.BaseNetworkObserver;
import com.android.server.net.LockdownVpnTracker;
import com.google.android.collect.Lists;
import com.google.android.collect.Sets;
import dalvik.system.DexClassLoader;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: ConnectivityService.class */
public class ConnectivityService extends IConnectivityManager.Stub {
    private static final String TAG = "ConnectivityService";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    private static final boolean LOGD_RULES = false;
    private static final int RESTORE_DEFAULT_NETWORK_DELAY = 60000;
    private static final String NETWORK_RESTORE_DELAY_PROP_NAME = "android.telephony.apn-restore";
    private static final int DEFAULT_FAIL_FAST_TIME_MS = 60000;
    private static final String FAIL_FAST_TIME_MS = "persist.radio.fail_fast_time_ms";
    private static final String ACTION_PKT_CNT_SAMPLE_INTERVAL_ELAPSED = "android.net.ConnectivityService.action.PKT_CNT_SAMPLE_INTERVAL_ELAPSED";
    private static final int SAMPLE_INTERVAL_ELAPSED_REQUEST_CODE = 0;
    private PendingIntent mSampleIntervalElapsedIntent;
    private static final int DEFAULT_SAMPLING_INTERVAL_IN_SECONDS = 720;
    private static final int DEFAULT_START_SAMPLING_INTERVAL_IN_SECONDS = 60;
    AlarmManager mAlarmManager;
    private static final int MAX_HOSTROUTE_CYCLE_COUNT = 10;
    private Tethering mTethering;
    private KeyStore mKeyStore;
    @GuardedBy("mVpns")
    private final SparseArray<Vpn> mVpns;
    private VpnCallback mVpnCallback;
    private boolean mLockdownEnabled;
    private LockdownVpnTracker mLockdownTracker;
    private Nat464Xlat mClat;
    private Object mRulesLock;
    private SparseIntArray mUidRules;
    private HashSet<String> mMeteredIfaces;
    private NetworkStateTracker[] mNetTrackers;
    private CaptivePortalTracker mCaptivePortalTracker;
    private LinkProperties[] mCurrentLinkProperties;
    private List<Integer>[] mNetRequestersPids;
    private int[] mPriorityList;
    private Context mContext;
    private int mNetworkPreference;
    private int mActiveDefaultNetwork;
    private int mDefaultInetCondition;
    private int mDefaultInetConditionPublished;
    private boolean mInetConditionChangeInFlight;
    private int mDefaultConnectionSequence;
    private Object mDnsLock;
    private int mNumDnsEntries;
    private boolean mTestMode;
    private static ConnectivityService sServiceInstance;
    private INetworkManagementService mNetd;
    private INetworkPolicyManager mPolicyManager;
    private static final int ENABLED = 1;
    private static final int DISABLED = 0;
    private static final boolean ADD = true;
    private static final boolean REMOVE = false;
    private static final boolean TO_DEFAULT_TABLE = true;
    private static final boolean TO_SECONDARY_TABLE = false;
    private static final boolean EXEMPT = true;
    private static final boolean UNEXEMPT = false;
    private static final int EVENT_RESTORE_DEFAULT_NETWORK = 1;
    private static final int EVENT_CHANGE_MOBILE_DATA_ENABLED = 2;
    private static final int EVENT_SET_NETWORK_PREFERENCE = 3;
    private static final int EVENT_INET_CONDITION_CHANGE = 4;
    private static final int EVENT_INET_CONDITION_HOLD_END = 5;
    private static final int EVENT_SET_MOBILE_DATA = 7;
    private static final int EVENT_CLEAR_NET_TRANSITION_WAKELOCK = 8;
    private static final int EVENT_APPLY_GLOBAL_HTTP_PROXY = 9;
    private static final int EVENT_SET_DEPENDENCY_MET = 10;
    private static final int EVENT_SEND_STICKY_BROADCAST_INTENT = 11;
    private static final int EVENT_SET_POLICY_DATA_ENABLE = 12;
    private static final int EVENT_VPN_STATE_CHANGED = 13;
    private static final int EVENT_ENABLE_FAIL_FAST_MOBILE_DATA = 14;
    private static final int EVENT_SAMPLE_INTERVAL_ELAPSED = 15;
    private static final int EVENT_PROXY_HAS_CHANGED = 16;
    private InternalHandler mHandler;
    private NetworkStateTrackerHandler mTrackerHandler;
    private List<FeatureUser> mFeatureUsers;
    private boolean mSystemReady;
    private Intent mInitialBroadcast;
    private PowerManager.WakeLock mNetTransitionWakeLock;
    private String mNetTransitionWakeLockCausedBy;
    private int mNetTransitionWakeLockSerialNumber;
    private int mNetTransitionWakeLockTimeout;
    private InetAddress mDefaultDns;
    private final Object mRoutesLock;
    @GuardedBy("mRoutesLock")
    private Collection<RouteInfo> mAddedRoutes;
    @GuardedBy("mRoutesLock")
    private Collection<LinkAddress> mExemptAddresses;
    private static final int INET_CONDITION_LOG_MAX_SIZE = 15;
    private ArrayList mInetLog;
    private ProxyProperties mDefaultProxy;
    private Object mProxyLock;
    private boolean mDefaultProxyDisabled;
    private ProxyProperties mGlobalProxy;
    private PacManager mPacManager;
    private SettingsObserver mSettingsObserver;
    NetworkConfig[] mNetConfigs;
    int mNetworksDefined;
    RadioAttributes[] mRadioAttributes;
    List mProtectedNetworks;
    private DataConnectionStats mDataConnectionStats;
    private AtomicInteger mEnableFailFastMobileDataTag;
    TelephonyManager mTelephonyManager;
    private INetworkManagementEventObserver mDataActivityObserver;
    private INetworkPolicyListener mPolicyListener;
    private BroadcastReceiver mUserPresentReceiver;
    private static final int CMP_RESULT_CODE_NO_CONNECTION = 0;
    private static final int CMP_RESULT_CODE_CONNECTABLE = 1;
    private static final int CMP_RESULT_CODE_NO_DNS = 2;
    private static final int CMP_RESULT_CODE_NO_TCP_CONNECTION = 3;
    private static final int CMP_RESULT_CODE_REDIRECTED = 4;
    private static final int CMP_RESULT_CODE_PROVISIONING_NETWORK = 5;
    private AtomicBoolean mIsCheckingMobileProvisioning;
    private static final String CONNECTED_TO_PROVISIONING_NETWORK_ACTION = "com.android.server.connectivityservice.CONNECTED_TO_PROVISIONING_NETWORK_ACTION";
    private BroadcastReceiver mProvisioningReceiver;
    private static final String NOTIFICATION_ID = "CaptivePortal.Notification";
    private volatile boolean mIsNotificationVisible;
    private static final String PROVISIONING_URL_PATH = "/data/misc/radio/provisioning_urls.xml";
    private final File mProvisioningUrlFile;
    private static final String TAG_PROVISIONING_URLS = "provisioningUrls";
    private static final String TAG_PROVISIONING_URL = "provisioningUrl";
    private static final String TAG_REDIRECTED_URL = "redirectedUrl";
    private static final String ATTR_MCC = "mcc";
    private static final String ATTR_MNC = "mnc";
    private static final int REDIRECTED_PROVISIONING = 1;
    private static final int PROVISIONING = 2;
    private BroadcastReceiver mUserIntentReceiver;

    /* loaded from: ConnectivityService$NetworkFactory.class */
    public interface NetworkFactory {
        NetworkStateTracker createTracker(int i, NetworkConfig networkConfig);
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.getActiveNetworkQuotaInfo():android.net.NetworkQuotaInfo, file: ConnectivityService.class
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
    @Override // android.net.IConnectivityManager
    public android.net.NetworkQuotaInfo getActiveNetworkQuotaInfo() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.getActiveNetworkQuotaInfo():android.net.NetworkQuotaInfo, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.getActiveNetworkQuotaInfo():android.net.NetworkQuotaInfo");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.isActiveNetworkMetered():boolean, file: ConnectivityService.class
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
    @Override // android.net.IConnectivityManager
    public boolean isActiveNetworkMetered() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.isActiveNetworkMetered():boolean, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.isActiveNetworkMetered():boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.startUsingNetworkFeature(int, java.lang.String, android.os.IBinder):int, file: ConnectivityService.class
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
    @Override // android.net.IConnectivityManager
    public int startUsingNetworkFeature(int r1, java.lang.String r2, android.os.IBinder r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.startUsingNetworkFeature(int, java.lang.String, android.os.IBinder):int, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.startUsingNetworkFeature(int, java.lang.String, android.os.IBinder):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.stopUsingNetworkFeature(com.android.server.ConnectivityService$FeatureUser, boolean):int, file: ConnectivityService.class
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
    /* JADX INFO: Access modifiers changed from: private */
    public int stopUsingNetworkFeature(com.android.server.ConnectivityService.FeatureUser r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.stopUsingNetworkFeature(com.android.server.ConnectivityService$FeatureUser, boolean):int, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.stopUsingNetworkFeature(com.android.server.ConnectivityService$FeatureUser, boolean):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.requestRouteToHostAddress(int, byte[]):boolean, file: ConnectivityService.class
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
    @Override // android.net.IConnectivityManager
    public boolean requestRouteToHostAddress(int r1, byte[] r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.requestRouteToHostAddress(int, byte[]):boolean, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.requestRouteToHostAddress(int, byte[]):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.sendDataActivityBroadcast(int, boolean):void, file: ConnectivityService.class
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
    /* JADX INFO: Access modifiers changed from: private */
    public void sendDataActivityBroadcast(int r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.sendDataActivityBroadcast(int, boolean):void, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.sendDataActivityBroadcast(int, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.sendStickyBroadcast(android.content.Intent):void, file: ConnectivityService.class
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
    /* JADX INFO: Access modifiers changed from: private */
    public void sendStickyBroadcast(android.content.Intent r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.sendStickyBroadcast(android.content.Intent):void, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.sendStickyBroadcast(android.content.Intent):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.flushVmDnsCache():void, file: ConnectivityService.class
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
    private void flushVmDnsCache() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.flushVmDnsCache():void, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.flushVmDnsCache():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.setGlobalProxy(android.net.ProxyProperties):void, file: ConnectivityService.class
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
    @Override // android.net.IConnectivityManager
    public void setGlobalProxy(android.net.ProxyProperties r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.setGlobalProxy(android.net.ProxyProperties):void, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.setGlobalProxy(android.net.ProxyProperties):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.sendProxyBroadcast(android.net.ProxyProperties):void, file: ConnectivityService.class
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
    /* JADX INFO: Access modifiers changed from: private */
    public void sendProxyBroadcast(android.net.ProxyProperties r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.sendProxyBroadcast(android.net.ProxyProperties):void, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.sendProxyBroadcast(android.net.ProxyProperties):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.protectVpn(android.os.ParcelFileDescriptor):boolean, file: ConnectivityService.class
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
    @Override // android.net.IConnectivityManager
    public boolean protectVpn(android.os.ParcelFileDescriptor r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.protectVpn(android.os.ParcelFileDescriptor):boolean, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.protectVpn(android.os.ParcelFileDescriptor):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.markSocketAsUser(android.os.ParcelFileDescriptor, int):void, file: ConnectivityService.class
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
    @Override // android.net.IConnectivityManager
    public void markSocketAsUser(android.os.ParcelFileDescriptor r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.markSocketAsUser(android.os.ParcelFileDescriptor, int):void, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.markSocketAsUser(android.os.ParcelFileDescriptor, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.checkMobileProvisioning(int):int, file: ConnectivityService.class
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
    @Override // android.net.IConnectivityManager
    public int checkMobileProvisioning(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.checkMobileProvisioning(int):int, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.checkMobileProvisioning(int):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.getProvisioningUrlBaseFromFile(int):java.lang.String, file: ConnectivityService.class
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
    private java.lang.String getProvisioningUrlBaseFromFile(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.getProvisioningUrlBaseFromFile(int):java.lang.String, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.getProvisioningUrlBaseFromFile(int):java.lang.String");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.setAirplaneMode(boolean):void, file: ConnectivityService.class
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
    @Override // android.net.IConnectivityManager
    public void setAirplaneMode(boolean r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.setAirplaneMode(boolean):void, file: ConnectivityService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.setAirplaneMode(boolean):void");
    }

    static /* synthetic */ NetworkStateTracker[] access$1000(ConnectivityService x0) {
        return x0.mNetTrackers;
    }

    static /* synthetic */ boolean access$4700(ConnectivityService x0) {
        return x0.isMobileDataStateTrackerReady();
    }

    static /* synthetic */ void access$4800(ConnectivityService x0, int x1) {
        x0.setEnableFailFastMobileData(x1);
    }

    /* loaded from: ConnectivityService$RadioAttributes.class */
    private static class RadioAttributes {
        public int mSimultaneity;
        public int mType;

        public RadioAttributes(String init) {
            String[] fragments = init.split(Separators.COMMA);
            this.mType = Integer.parseInt(fragments[0]);
            this.mSimultaneity = Integer.parseInt(fragments[1]);
        }
    }

    public ConnectivityService(Context context, INetworkManagementService netd, INetworkStatsService statsService, INetworkPolicyManager policyManager) {
        this(context, netd, statsService, policyManager, null);
    }

    public ConnectivityService(Context context, INetworkManagementService netManager, INetworkStatsService statsService, INetworkPolicyManager policyManager, NetworkFactory netFactory) {
        String id;
        this.mVpns = new SparseArray<>();
        this.mVpnCallback = new VpnCallback();
        this.mRulesLock = new Object();
        this.mUidRules = new SparseIntArray();
        this.mMeteredIfaces = Sets.newHashSet();
        this.mActiveDefaultNetwork = -1;
        this.mDefaultInetCondition = 0;
        this.mDefaultInetConditionPublished = 0;
        this.mInetConditionChangeInFlight = false;
        this.mDefaultConnectionSequence = 0;
        this.mDnsLock = new Object();
        this.mNetTransitionWakeLockCausedBy = "";
        this.mRoutesLock = new Object();
        this.mAddedRoutes = new ArrayList();
        this.mExemptAddresses = new ArrayList();
        this.mDefaultProxy = null;
        this.mProxyLock = new Object();
        this.mDefaultProxyDisabled = false;
        this.mGlobalProxy = null;
        this.mPacManager = null;
        this.mEnableFailFastMobileDataTag = new AtomicInteger(0);
        this.mDataActivityObserver = new BaseNetworkObserver() { // from class: com.android.server.ConnectivityService.2
            @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
            public void interfaceClassDataActivityChanged(String label, boolean active) {
                int deviceType = Integer.parseInt(label);
                ConnectivityService.this.sendDataActivityBroadcast(deviceType, active);
            }
        };
        this.mPolicyListener = new INetworkPolicyListener.Stub() { // from class: com.android.server.ConnectivityService.3
            @Override // android.net.INetworkPolicyListener
            public void onUidRulesChanged(int uid, int uidRules) {
                synchronized (ConnectivityService.this.mRulesLock) {
                    int oldRules = ConnectivityService.this.mUidRules.get(uid, 0);
                    if (oldRules == uidRules) {
                        return;
                    }
                    ConnectivityService.this.mUidRules.put(uid, uidRules);
                }
            }

            @Override // android.net.INetworkPolicyListener
            public void onMeteredIfacesChanged(String[] meteredIfaces) {
                synchronized (ConnectivityService.this.mRulesLock) {
                    ConnectivityService.this.mMeteredIfaces.clear();
                    for (String iface : meteredIfaces) {
                        ConnectivityService.this.mMeteredIfaces.add(iface);
                    }
                }
            }

            @Override // android.net.INetworkPolicyListener
            public void onRestrictBackgroundChanged(boolean restrictBackground) {
                NetworkStateTracker tracker;
                NetworkInfo info;
                int networkType = ConnectivityService.this.mActiveDefaultNetwork;
                if (ConnectivityManager.isNetworkTypeValid(networkType) && (tracker = ConnectivityService.this.mNetTrackers[networkType]) != null && (info = tracker.getNetworkInfo()) != null && info.isConnected()) {
                    ConnectivityService.this.sendConnectedBroadcast(info);
                }
            }
        };
        this.mUserPresentReceiver = new BroadcastReceiver() { // from class: com.android.server.ConnectivityService.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (ConnectivityService.this.updateLockdownVpn()) {
                    ConnectivityService.this.mContext.unregisterReceiver(this);
                }
            }
        };
        this.mIsCheckingMobileProvisioning = new AtomicBoolean(false);
        this.mProvisioningReceiver = new BroadcastReceiver() { // from class: com.android.server.ConnectivityService.6
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals(ConnectivityService.CONNECTED_TO_PROVISIONING_NETWORK_ACTION)) {
                    ConnectivityService.this.handleMobileProvisioningAction(intent.getStringExtra("EXTRA_URL"));
                }
            }
        };
        this.mIsNotificationVisible = false;
        this.mProvisioningUrlFile = new File(PROVISIONING_URL_PATH);
        this.mUserIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.ConnectivityService.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -10000);
                if (userId == -10000) {
                    return;
                }
                if (Intent.ACTION_USER_STARTING.equals(action)) {
                    ConnectivityService.this.onUserStart(userId);
                } else if (Intent.ACTION_USER_STOPPING.equals(action)) {
                    ConnectivityService.this.onUserStop(userId);
                }
            }
        };
        log("ConnectivityService starting up");
        HandlerThread handlerThread = new HandlerThread("ConnectivityServiceThread");
        handlerThread.start();
        this.mHandler = new InternalHandler(handlerThread.getLooper());
        this.mTrackerHandler = new NetworkStateTrackerHandler(handlerThread.getLooper());
        netFactory = netFactory == null ? new DefaultNetworkFactory(context, this.mTrackerHandler) : netFactory;
        if (TextUtils.isEmpty(SystemProperties.get("net.hostname")) && (id = Settings.Secure.getString(context.getContentResolver(), "android_id")) != null && id.length() > 0) {
            String name = new String("android-").concat(id);
            SystemProperties.set("net.hostname", name);
        }
        String dns = Settings.Global.getString(context.getContentResolver(), Settings.Global.DEFAULT_DNS_SERVER);
        dns = (dns == null || dns.length() == 0) ? context.getResources().getString(R.string.config_default_dns_server) : dns;
        try {
            this.mDefaultDns = NetworkUtils.numericToInetAddress(dns);
        } catch (IllegalArgumentException e) {
            loge("Error setting defaultDns using " + dns);
        }
        this.mContext = (Context) checkNotNull(context, "missing Context");
        this.mNetd = (INetworkManagementService) checkNotNull(netManager, "missing INetworkManagementService");
        this.mPolicyManager = (INetworkPolicyManager) checkNotNull(policyManager, "missing INetworkPolicyManager");
        this.mKeyStore = KeyStore.getInstance();
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        try {
            this.mPolicyManager.registerListener(this.mPolicyListener);
        } catch (RemoteException e2) {
            loge("unable to register INetworkPolicyListener" + e2.toString());
        }
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mNetTransitionWakeLock = powerManager.newWakeLock(1, TAG);
        this.mNetTransitionWakeLockTimeout = this.mContext.getResources().getInteger(R.integer.config_networkTransitionTimeout);
        this.mNetTrackers = new NetworkStateTracker[15];
        this.mCurrentLinkProperties = new LinkProperties[15];
        this.mRadioAttributes = new RadioAttributes[15];
        this.mNetConfigs = new NetworkConfig[15];
        String[] raStrings = context.getResources().getStringArray(R.array.radioAttributes);
        for (String raString : raStrings) {
            RadioAttributes r = new RadioAttributes(raString);
            if (r.mType > 14) {
                loge("Error in radioAttributes - ignoring attempt to define type " + r.mType);
            } else if (this.mRadioAttributes[r.mType] != null) {
                loge("Error in radioAttributes - ignoring attempt to redefine type " + r.mType);
            } else {
                this.mRadioAttributes[r.mType] = r;
            }
        }
        boolean wifiOnly = SystemProperties.getBoolean("ro.radio.noril", false);
        log("wifiOnly=" + wifiOnly);
        String[] naStrings = context.getResources().getStringArray(R.array.networkAttributes);
        for (String naString : naStrings) {
            try {
                NetworkConfig n = new NetworkConfig(naString);
                if (n.type > 14) {
                    loge("Error in networkAttributes - ignoring attempt to define type " + n.type);
                } else if (wifiOnly && ConnectivityManager.isNetworkTypeMobile(n.type)) {
                    log("networkAttributes - ignoring mobile as this dev is wifiOnly " + n.type);
                } else if (this.mNetConfigs[n.type] != null) {
                    loge("Error in networkAttributes - ignoring attempt to redefine type " + n.type);
                } else if (this.mRadioAttributes[n.radio] == null) {
                    loge("Error in networkAttributes - ignoring attempt to use undefined radio " + n.radio + " in network type " + n.type);
                } else {
                    this.mNetConfigs[n.type] = n;
                    this.mNetworksDefined++;
                }
            } catch (Exception e3) {
            }
        }
        this.mProtectedNetworks = new ArrayList();
        int[] protectedNetworks = context.getResources().getIntArray(R.array.config_protectedNetworks);
        for (int p : protectedNetworks) {
            if (this.mNetConfigs[p] != null && !this.mProtectedNetworks.contains(Integer.valueOf(p))) {
                this.mProtectedNetworks.add(Integer.valueOf(p));
            } else {
                loge("Ignoring protectedNetwork " + p);
            }
        }
        this.mPriorityList = new int[this.mNetworksDefined];
        int insertionPoint = this.mNetworksDefined - 1;
        int i = 0;
        while (true) {
            int currentLowest = i;
            int nextLowest = 0;
            if (insertionPoint <= -1) {
                break;
            }
            NetworkConfig[] arr$ = this.mNetConfigs;
            for (NetworkConfig na : arr$) {
                if (na != null && na.priority >= currentLowest) {
                    if (na.priority > currentLowest) {
                        if (na.priority < nextLowest || nextLowest == 0) {
                            nextLowest = na.priority;
                        }
                    } else {
                        int i2 = insertionPoint;
                        insertionPoint--;
                        this.mPriorityList[i2] = na.type;
                    }
                }
            }
            i = nextLowest;
        }
        this.mNetworkPreference = getPersistedNetworkPreference();
        if (this.mNetworkPreference == -1) {
            int[] arr$2 = this.mPriorityList;
            int len$ = arr$2.length;
            int i$ = 0;
            while (true) {
                if (i$ >= len$) {
                    break;
                }
                int n2 = arr$2[i$];
                if (!this.mNetConfigs[n2].isDefault() || !ConnectivityManager.isNetworkTypeValid(n2)) {
                    i$++;
                } else {
                    this.mNetworkPreference = n2;
                    break;
                }
            }
            if (this.mNetworkPreference == -1) {
                throw new IllegalStateException("You should set at least one default Network in config.xml!");
            }
        }
        this.mNetRequestersPids = new ArrayList[15];
        int[] arr$3 = this.mPriorityList;
        for (int i3 : arr$3) {
            this.mNetRequestersPids[i3] = new ArrayList();
        }
        this.mFeatureUsers = new ArrayList();
        this.mTestMode = SystemProperties.get("cm.test.mode").equals("true") && SystemProperties.get("ro.build.type").equals("eng");
        int[] arr$4 = this.mPriorityList;
        for (int targetNetworkType : arr$4) {
            NetworkConfig config = this.mNetConfigs[targetNetworkType];
            try {
                NetworkStateTracker tracker = netFactory.createTracker(targetNetworkType, config);
                this.mNetTrackers[targetNetworkType] = tracker;
                tracker.startMonitoring(context, this.mTrackerHandler);
                if (config.isDefault()) {
                    tracker.reconnect();
                }
            } catch (IllegalArgumentException e4) {
                Slog.e(TAG, "Problem creating " + ConnectivityManager.getNetworkTypeName(targetNetworkType) + " tracker: " + e4);
            }
        }
        this.mTethering = new Tethering(this.mContext, this.mNetd, statsService, this, this.mHandler.getLooper());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_STARTING);
        intentFilter.addAction(Intent.ACTION_USER_STOPPING);
        this.mContext.registerReceiverAsUser(this.mUserIntentReceiver, UserHandle.ALL, intentFilter, null, null);
        this.mClat = new Nat464Xlat(this.mContext, this.mNetd, this, this.mTrackerHandler);
        try {
            this.mNetd.registerObserver(this.mTethering);
            this.mNetd.registerObserver(this.mDataActivityObserver);
            this.mNetd.registerObserver(this.mClat);
        } catch (RemoteException e5) {
            loge("Error registering observer :" + e5);
        }
        this.mInetLog = new ArrayList();
        this.mSettingsObserver = new SettingsObserver(this.mHandler, 9);
        this.mSettingsObserver.observe(this.mContext);
        this.mDataConnectionStats = new DataConnectionStats(this.mContext);
        this.mDataConnectionStats.startMonitoring();
        Intent intent = new Intent(ACTION_PKT_CNT_SAMPLE_INTERVAL_ELAPSED, (Uri) null);
        this.mSampleIntervalElapsedIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 0);
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        setAlarm(60000, this.mSampleIntervalElapsedIntent);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PKT_CNT_SAMPLE_INTERVAL_ELAPSED);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.ConnectivityService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent2) {
                String action = intent2.getAction();
                if (action.equals(ConnectivityService.ACTION_PKT_CNT_SAMPLE_INTERVAL_ELAPSED)) {
                    ConnectivityService.this.mHandler.sendMessage(ConnectivityService.this.mHandler.obtainMessage(15));
                }
            }
        }, new IntentFilter(filter));
        this.mPacManager = new PacManager(this.mContext, this.mHandler, 16);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(CONNECTED_TO_PROVISIONING_NETWORK_ACTION);
        this.mContext.registerReceiver(this.mProvisioningReceiver, filter2);
    }

    /* loaded from: ConnectivityService$DefaultNetworkFactory.class */
    private static class DefaultNetworkFactory implements NetworkFactory {
        private final Context mContext;
        private final Handler mTrackerHandler;

        public DefaultNetworkFactory(Context context, Handler trackerHandler) {
            this.mContext = context;
            this.mTrackerHandler = trackerHandler;
        }

        @Override // com.android.server.ConnectivityService.NetworkFactory
        public NetworkStateTracker createTracker(int targetNetworkType, NetworkConfig config) {
            switch (config.radio) {
                case 0:
                    return new MobileDataStateTracker(targetNetworkType, config.name);
                case 1:
                    return new WifiStateTracker(targetNetworkType, config.name);
                case 2:
                case 3:
                case 4:
                case 5:
                default:
                    throw new IllegalArgumentException("Trying to create a NetworkStateTracker for an unknown radio type: " + config.radio);
                case 6:
                    return ConnectivityService.makeWimaxStateTracker(this.mContext, this.mTrackerHandler);
                case 7:
                    return BluetoothTetheringDataTracker.getInstance();
                case 8:
                    return new DummyDataStateTracker(targetNetworkType, config.name);
                case 9:
                    return EthernetDataTracker.getInstance();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static NetworkStateTracker makeWimaxStateTracker(Context context, Handler trackerHandler) {
        boolean isWimaxEnabled = context.getResources().getBoolean(R.bool.config_wimaxEnabled);
        if (isWimaxEnabled) {
            try {
                String wimaxJarLocation = context.getResources().getString(R.string.config_wimaxServiceJarLocation);
                String wimaxLibLocation = context.getResources().getString(R.string.config_wimaxNativeLibLocation);
                String wimaxManagerClassName = context.getResources().getString(R.string.config_wimaxManagerClassname);
                String wimaxServiceClassName = context.getResources().getString(R.string.config_wimaxServiceClassname);
                String wimaxStateTrackerClassName = context.getResources().getString(R.string.config_wimaxStateTrackerClassname);
                log("wimaxJarLocation: " + wimaxJarLocation);
                DexClassLoader wimaxClassLoader = new DexClassLoader(wimaxJarLocation, new ContextWrapper(context).getCacheDir().getAbsolutePath(), wimaxLibLocation, ClassLoader.getSystemClassLoader());
                try {
                    wimaxClassLoader.loadClass(wimaxManagerClassName);
                    Class wimaxStateTrackerClass = wimaxClassLoader.loadClass(wimaxStateTrackerClassName);
                    Class wimaxServiceClass = wimaxClassLoader.loadClass(wimaxServiceClassName);
                    try {
                        log("Starting Wimax Service... ");
                        Constructor wmxStTrkrConst = wimaxStateTrackerClass.getConstructor(Context.class, Handler.class);
                        NetworkStateTracker wimaxStateTracker = (NetworkStateTracker) wmxStTrkrConst.newInstance(context, trackerHandler);
                        Constructor wmxSrvConst = wimaxServiceClass.getDeclaredConstructor(Context.class, wimaxStateTrackerClass);
                        wmxSrvConst.setAccessible(true);
                        IBinder svcInvoker = (IBinder) wmxSrvConst.newInstance(context, wimaxStateTracker);
                        wmxSrvConst.setAccessible(false);
                        ServiceManager.addService(WimaxManagerConstants.WIMAX_SERVICE, svcInvoker);
                        return wimaxStateTracker;
                    } catch (Exception ex) {
                        loge("Exception creating Wimax classes: " + ex.toString());
                        return null;
                    }
                } catch (ClassNotFoundException ex2) {
                    loge("Exception finding Wimax classes: " + ex2.toString());
                    return null;
                }
            } catch (Resources.NotFoundException e) {
                loge("Wimax Resources does not exist!!! ");
                return null;
            }
        }
        loge("Wimax is not enabled or not added to the network attributes!!! ");
        return null;
    }

    @Override // android.net.IConnectivityManager
    public void setNetworkPreference(int preference) {
        enforceChangePermission();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, preference, 0));
    }

    @Override // android.net.IConnectivityManager
    public int getNetworkPreference() {
        int preference;
        enforceAccessPermission();
        synchronized (this) {
            preference = this.mNetworkPreference;
        }
        return preference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetNetworkPreference(int preference) {
        if (ConnectivityManager.isNetworkTypeValid(preference) && this.mNetConfigs[preference] != null && this.mNetConfigs[preference].isDefault() && this.mNetworkPreference != preference) {
            ContentResolver cr = this.mContext.getContentResolver();
            Settings.Global.putInt(cr, "network_preference", preference);
            synchronized (this) {
                this.mNetworkPreference = preference;
            }
            enforcePreference();
        }
    }

    private int getConnectivityChangeDelay() {
        ContentResolver cr = this.mContext.getContentResolver();
        int defaultDelay = SystemProperties.getInt("conn.connectivity_change_delay", ConnectivityManager.CONNECTIVITY_CHANGE_DELAY_DEFAULT);
        return Settings.Global.getInt(cr, Settings.Global.CONNECTIVITY_CHANGE_DELAY, defaultDelay);
    }

    private int getPersistedNetworkPreference() {
        ContentResolver cr = this.mContext.getContentResolver();
        int networkPrefSetting = Settings.Global.getInt(cr, "network_preference", -1);
        return networkPrefSetting;
    }

    private void enforcePreference() {
        if (this.mNetTrackers[this.mNetworkPreference].getNetworkInfo().isConnected() || !this.mNetTrackers[this.mNetworkPreference].isAvailable()) {
            return;
        }
        for (int t = 0; t <= 14; t++) {
            if (t != this.mNetworkPreference && this.mNetTrackers[t] != null && this.mNetTrackers[t].getNetworkInfo().isConnected()) {
                log("tearing down " + this.mNetTrackers[t].getNetworkInfo() + " in enforcePreference");
                teardown(this.mNetTrackers[t]);
            }
        }
    }

    private boolean teardown(NetworkStateTracker netTracker) {
        if (netTracker.teardown()) {
            netTracker.setTeardownRequested(true);
            return true;
        }
        return false;
    }

    private boolean isNetworkBlocked(NetworkStateTracker tracker, int uid) {
        boolean networkCostly;
        int uidRules;
        String iface = tracker.getLinkProperties().getInterfaceName();
        synchronized (this.mRulesLock) {
            networkCostly = this.mMeteredIfaces.contains(iface);
            uidRules = this.mUidRules.get(uid, 0);
        }
        if (networkCostly && (uidRules & 1) != 0) {
            return true;
        }
        return false;
    }

    private NetworkInfo getFilteredNetworkInfo(NetworkStateTracker tracker, int uid) {
        NetworkInfo info = tracker.getNetworkInfo();
        if (isNetworkBlocked(tracker, uid)) {
            info = new NetworkInfo(info);
            info.setDetailedState(NetworkInfo.DetailedState.BLOCKED, null, null);
        }
        if (this.mLockdownTracker != null) {
            info = this.mLockdownTracker.augmentNetworkInfo(info);
        }
        return info;
    }

    @Override // android.net.IConnectivityManager
    public NetworkInfo getActiveNetworkInfo() {
        enforceAccessPermission();
        int uid = Binder.getCallingUid();
        return getNetworkInfo(this.mActiveDefaultNetwork, uid);
    }

    private NetworkInfo getProvisioningNetworkInfo() {
        enforceAccessPermission();
        NetworkInfo provNi = null;
        NetworkInfo[] arr$ = getAllNetworkInfo();
        int len$ = arr$.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            NetworkInfo ni = arr$[i$];
            if (!ni.isConnectedToProvisioningNetwork()) {
                i$++;
            } else {
                provNi = ni;
                break;
            }
        }
        log("getProvisioningNetworkInfo: X provNi=" + provNi);
        return provNi;
    }

    @Override // android.net.IConnectivityManager
    public NetworkInfo getProvisioningOrActiveNetworkInfo() {
        enforceAccessPermission();
        NetworkInfo provNi = getProvisioningNetworkInfo();
        if (provNi == null) {
            int uid = Binder.getCallingUid();
            provNi = getNetworkInfo(this.mActiveDefaultNetwork, uid);
        }
        log("getProvisioningOrActiveNetworkInfo: X provNi=" + provNi);
        return provNi;
    }

    public NetworkInfo getActiveNetworkInfoUnfiltered() {
        NetworkStateTracker tracker;
        enforceAccessPermission();
        if (ConnectivityManager.isNetworkTypeValid(this.mActiveDefaultNetwork) && (tracker = this.mNetTrackers[this.mActiveDefaultNetwork]) != null) {
            return tracker.getNetworkInfo();
        }
        return null;
    }

    @Override // android.net.IConnectivityManager
    public NetworkInfo getActiveNetworkInfoForUid(int uid) {
        enforceConnectivityInternalPermission();
        return getNetworkInfo(this.mActiveDefaultNetwork, uid);
    }

    @Override // android.net.IConnectivityManager
    public NetworkInfo getNetworkInfo(int networkType) {
        enforceAccessPermission();
        int uid = Binder.getCallingUid();
        return getNetworkInfo(networkType, uid);
    }

    private NetworkInfo getNetworkInfo(int networkType, int uid) {
        NetworkStateTracker tracker;
        NetworkInfo info = null;
        if (ConnectivityManager.isNetworkTypeValid(networkType) && (tracker = this.mNetTrackers[networkType]) != null) {
            info = getFilteredNetworkInfo(tracker, uid);
        }
        return info;
    }

    @Override // android.net.IConnectivityManager
    public NetworkInfo[] getAllNetworkInfo() {
        enforceAccessPermission();
        int uid = Binder.getCallingUid();
        ArrayList<NetworkInfo> result = Lists.newArrayList();
        synchronized (this.mRulesLock) {
            NetworkStateTracker[] arr$ = this.mNetTrackers;
            for (NetworkStateTracker tracker : arr$) {
                if (tracker != null) {
                    result.add(getFilteredNetworkInfo(tracker, uid));
                }
            }
        }
        return (NetworkInfo[]) result.toArray(new NetworkInfo[result.size()]);
    }

    @Override // android.net.IConnectivityManager
    public boolean isNetworkSupported(int networkType) {
        enforceAccessPermission();
        return ConnectivityManager.isNetworkTypeValid(networkType) && this.mNetTrackers[networkType] != null;
    }

    @Override // android.net.IConnectivityManager
    public LinkProperties getActiveLinkProperties() {
        return getLinkProperties(this.mActiveDefaultNetwork);
    }

    @Override // android.net.IConnectivityManager
    public LinkProperties getLinkProperties(int networkType) {
        NetworkStateTracker tracker;
        enforceAccessPermission();
        if (ConnectivityManager.isNetworkTypeValid(networkType) && (tracker = this.mNetTrackers[networkType]) != null) {
            return tracker.getLinkProperties();
        }
        return null;
    }

    @Override // android.net.IConnectivityManager
    public NetworkState[] getAllNetworkState() {
        enforceAccessPermission();
        int uid = Binder.getCallingUid();
        ArrayList<NetworkState> result = Lists.newArrayList();
        synchronized (this.mRulesLock) {
            NetworkStateTracker[] arr$ = this.mNetTrackers;
            for (NetworkStateTracker tracker : arr$) {
                if (tracker != null) {
                    NetworkInfo info = getFilteredNetworkInfo(tracker, uid);
                    result.add(new NetworkState(info, tracker.getLinkProperties(), tracker.getLinkCapabilities()));
                }
            }
        }
        return (NetworkState[]) result.toArray(new NetworkState[result.size()]);
    }

    private NetworkState getNetworkStateUnchecked(int networkType) {
        NetworkStateTracker tracker;
        if (ConnectivityManager.isNetworkTypeValid(networkType) && (tracker = this.mNetTrackers[networkType]) != null) {
            return new NetworkState(tracker.getNetworkInfo(), tracker.getLinkProperties(), tracker.getLinkCapabilities());
        }
        return null;
    }

    private boolean isNetworkMeteredUnchecked(int networkType) {
        NetworkState state = getNetworkStateUnchecked(networkType);
        if (state != null) {
            try {
                return this.mPolicyManager.isNetworkMetered(state);
            } catch (RemoteException e) {
                return false;
            }
        }
        return false;
    }

    @Override // android.net.IConnectivityManager
    public boolean setRadios(boolean turnOn) {
        boolean result = true;
        enforceChangePermission();
        NetworkStateTracker[] arr$ = this.mNetTrackers;
        for (NetworkStateTracker t : arr$) {
            if (t != null) {
                result = t.setRadio(turnOn) && result;
            }
        }
        return result;
    }

    @Override // android.net.IConnectivityManager
    public boolean setRadio(int netType, boolean turnOn) {
        NetworkStateTracker tracker;
        enforceChangePermission();
        return ConnectivityManager.isNetworkTypeValid(netType) && (tracker = this.mNetTrackers[netType]) != null && tracker.setRadio(turnOn);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ConnectivityService$FeatureUser.class */
    public class FeatureUser implements IBinder.DeathRecipient {
        int mNetworkType;
        String mFeature;
        IBinder mBinder;
        int mPid = Binder.getCallingPid();
        int mUid = Binder.getCallingUid();
        long mCreateTime = System.currentTimeMillis();

        FeatureUser(int type, String feature, IBinder binder) {
            this.mNetworkType = type;
            this.mFeature = feature;
            this.mBinder = binder;
            try {
                this.mBinder.linkToDeath(this, 0);
            } catch (RemoteException e) {
                binderDied();
            }
        }

        void unlinkDeathRecipient() {
            this.mBinder.unlinkToDeath(this, 0);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            ConnectivityService.log("ConnectivityService FeatureUser binderDied(" + this.mNetworkType + ", " + this.mFeature + ", " + this.mBinder + "), created " + (System.currentTimeMillis() - this.mCreateTime) + " mSec ago");
            ConnectivityService.this.stopUsingNetworkFeature(this, false);
        }

        public void expire() {
            ConnectivityService.this.stopUsingNetworkFeature(this, false);
        }

        public boolean isSameUser(FeatureUser u) {
            if (u == null) {
                return false;
            }
            return isSameUser(u.mPid, u.mUid, u.mNetworkType, u.mFeature);
        }

        public boolean isSameUser(int pid, int uid, int networkType, String feature) {
            if (this.mPid == pid && this.mUid == uid && this.mNetworkType == networkType && TextUtils.equals(this.mFeature, feature)) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "FeatureUser(" + this.mNetworkType + Separators.COMMA + this.mFeature + Separators.COMMA + this.mPid + Separators.COMMA + this.mUid + "), created " + (System.currentTimeMillis() - this.mCreateTime) + " mSec ago";
        }
    }

    @Override // android.net.IConnectivityManager
    public int stopUsingNetworkFeature(int networkType, String feature) {
        enforceChangePermission();
        int pid = getCallingPid();
        int uid = getCallingUid();
        FeatureUser u = null;
        boolean found = false;
        synchronized (this) {
            Iterator i$ = this.mFeatureUsers.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                FeatureUser x = i$.next();
                if (x.isSameUser(pid, uid, networkType, feature)) {
                    u = x;
                    found = true;
                    break;
                }
            }
        }
        if (found && u != null) {
            return stopUsingNetworkFeature(u, true);
        }
        return 1;
    }

    @Override // android.net.IConnectivityManager
    public boolean requestRouteToHost(int networkType, int hostAddress) {
        InetAddress inetAddress = NetworkUtils.intToInetAddress(hostAddress);
        if (inetAddress == null) {
            return false;
        }
        return requestRouteToHostAddress(networkType, inetAddress.getAddress());
    }

    private boolean addRoute(LinkProperties p, RouteInfo r, boolean toDefaultTable, boolean exempt) {
        return modifyRoute(p, r, 0, true, toDefaultTable, exempt);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean removeRoute(LinkProperties p, RouteInfo r, boolean toDefaultTable) {
        return modifyRoute(p, r, 0, false, toDefaultTable, false);
    }

    private boolean addRouteToAddress(LinkProperties lp, InetAddress addr, boolean exempt) {
        return modifyRouteToAddress(lp, addr, true, true, exempt);
    }

    private boolean removeRouteToAddress(LinkProperties lp, InetAddress addr) {
        return modifyRouteToAddress(lp, addr, false, true, false);
    }

    private boolean modifyRouteToAddress(LinkProperties lp, InetAddress addr, boolean doAdd, boolean toDefaultTable, boolean exempt) {
        RouteInfo bestRoute;
        RouteInfo bestRoute2 = RouteInfo.selectBestRoute(lp.getAllRoutes(), addr);
        if (bestRoute2 == null) {
            bestRoute = RouteInfo.makeHostRoute(addr, lp.getInterfaceName());
        } else {
            String iface = bestRoute2.getInterface();
            if (bestRoute2.getGateway().equals(addr)) {
                bestRoute = RouteInfo.makeHostRoute(addr, iface);
            } else {
                bestRoute = RouteInfo.makeHostRoute(addr, bestRoute2.getGateway(), iface);
            }
        }
        return modifyRoute(lp, bestRoute, 0, doAdd, toDefaultTable, exempt);
    }

    private boolean modifyRoute(LinkProperties lp, RouteInfo r, int cycleCount, boolean doAdd, boolean toDefaultTable, boolean exempt) {
        RouteInfo bestRoute;
        RouteInfo bestRoute2;
        if (lp == null || r == null) {
            log("modifyRoute got unexpected null: " + lp + ", " + r);
            return false;
        } else if (cycleCount > 10) {
            loge("Error modifying route - too much recursion");
            return false;
        } else {
            String ifaceName = r.getInterface();
            if (ifaceName == null) {
                loge("Error modifying route - no interface name");
                return false;
            }
            if (r.hasGateway() && (bestRoute = RouteInfo.selectBestRoute(lp.getAllRoutes(), r.getGateway())) != null) {
                if (bestRoute.getGateway().equals(r.getGateway())) {
                    bestRoute2 = RouteInfo.makeHostRoute(r.getGateway(), ifaceName);
                } else {
                    bestRoute2 = RouteInfo.makeHostRoute(r.getGateway(), bestRoute.getGateway(), ifaceName);
                }
                modifyRoute(lp, bestRoute2, cycleCount + 1, doAdd, toDefaultTable, exempt);
            }
            if (doAdd) {
                try {
                    if (toDefaultTable) {
                        synchronized (this.mRoutesLock) {
                            this.mAddedRoutes.add(r);
                            this.mNetd.addRoute(ifaceName, r);
                            if (exempt) {
                                LinkAddress dest = r.getDestination();
                                if (!this.mExemptAddresses.contains(dest)) {
                                    this.mNetd.setHostExemption(dest);
                                    this.mExemptAddresses.add(dest);
                                }
                            }
                        }
                    } else {
                        this.mNetd.addSecondaryRoute(ifaceName, r);
                    }
                    return true;
                } catch (Exception e) {
                    loge("Exception trying to add a route: " + e);
                    return false;
                }
            } else if (toDefaultTable) {
                synchronized (this.mRoutesLock) {
                    this.mAddedRoutes.remove(r);
                    if (!this.mAddedRoutes.contains(r)) {
                        try {
                            this.mNetd.removeRoute(ifaceName, r);
                            LinkAddress dest2 = r.getDestination();
                            if (this.mExemptAddresses.contains(dest2)) {
                                this.mNetd.clearHostExemption(dest2);
                                this.mExemptAddresses.remove(dest2);
                            }
                        } catch (Exception e2) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                try {
                    this.mNetd.removeSecondaryRoute(ifaceName, r);
                    return true;
                } catch (Exception e3) {
                    return false;
                }
            }
        }
    }

    @Override // android.net.IConnectivityManager
    public boolean getMobileDataEnabled() {
        enforceAccessPermission();
        boolean retVal = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.MOBILE_DATA, 1) == 1;
        return retVal;
    }

    @Override // android.net.IConnectivityManager
    public void setDataDependency(int networkType, boolean met) {
        enforceConnectivityInternalPermission();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(10, met ? 1 : 0, networkType));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetDependencyMet(int networkType, boolean met) {
        if (this.mNetTrackers[networkType] != null) {
            log("handleSetDependencyMet(" + networkType + ", " + met + Separators.RPAREN);
            this.mNetTrackers[networkType].setDependencyMet(met);
        }
    }

    @Override // android.net.IConnectivityManager
    public void setMobileDataEnabled(boolean enabled) {
        enforceChangePermission();
        log("setMobileDataEnabled(" + enabled + Separators.RPAREN);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7, enabled ? 1 : 0, 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetMobileData(boolean enabled) {
        if (this.mNetTrackers[0] != null) {
            this.mNetTrackers[0].setUserDataEnable(enabled);
        }
        if (this.mNetTrackers[6] != null) {
            this.mNetTrackers[6].setUserDataEnable(enabled);
        }
    }

    @Override // android.net.IConnectivityManager
    public void setPolicyDataEnable(int networkType, boolean enabled) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_NETWORK_POLICY, TAG);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(12, networkType, enabled ? 1 : 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetPolicyDataEnable(int networkType, boolean enabled) {
        NetworkStateTracker tracker;
        if (ConnectivityManager.isNetworkTypeValid(networkType) && (tracker = this.mNetTrackers[networkType]) != null) {
            tracker.setPolicyDataEnable(enabled);
        }
    }

    private void enforceAccessPermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE, TAG);
    }

    private void enforceChangePermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE, TAG);
    }

    private void enforceTetherChangePermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE, TAG);
    }

    private void enforceTetherAccessPermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE, TAG);
    }

    private void enforceConnectivityInternalPermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
    }

    private void enforceMarkNetworkSocketPermission() {
        if (Binder.getCallingUid() == 1013) {
            return;
        }
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MARK_NETWORK_SOCKET, TAG);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisconnect(NetworkInfo info) {
        LinkProperties l;
        int prevNetType = info.getType();
        this.mNetTrackers[prevNetType].setTeardownRequested(false);
        removeDataActivityTracking(prevNetType);
        if (!this.mNetConfigs[prevNetType].isDefault()) {
            List<Integer> pids = this.mNetRequestersPids[prevNetType];
            for (Integer pid : pids) {
                reassessPidDns(pid.intValue(), false);
            }
        }
        Intent intent = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
        intent.putExtra("networkInfo", new NetworkInfo(info));
        intent.putExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, info.getType());
        if (info.isFailover()) {
            intent.putExtra(ConnectivityManager.EXTRA_IS_FAILOVER, true);
            info.setFailover(false);
        }
        if (info.getReason() != null) {
            intent.putExtra("reason", info.getReason());
        }
        if (info.getExtraInfo() != null) {
            intent.putExtra(ConnectivityManager.EXTRA_EXTRA_INFO, info.getExtraInfo());
        }
        if (this.mNetConfigs[prevNetType].isDefault()) {
            tryFailover(prevNetType);
            if (this.mActiveDefaultNetwork != -1) {
                NetworkInfo switchTo = this.mNetTrackers[this.mActiveDefaultNetwork].getNetworkInfo();
                intent.putExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO, switchTo);
            } else {
                this.mDefaultInetConditionPublished = 0;
                intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, true);
            }
        }
        intent.putExtra(ConnectivityManager.EXTRA_INET_CONDITION, this.mDefaultInetConditionPublished);
        boolean doReset = true;
        LinkProperties linkProperties = this.mNetTrackers[prevNetType].getLinkProperties();
        if (linkProperties != null) {
            String oldIface = linkProperties.getInterfaceName();
            if (!TextUtils.isEmpty(oldIface)) {
                NetworkStateTracker[] arr$ = this.mNetTrackers;
                int len$ = arr$.length;
                int i$ = 0;
                while (true) {
                    if (i$ >= len$) {
                        break;
                    }
                    NetworkStateTracker networkStateTracker = arr$[i$];
                    if (networkStateTracker != null) {
                        NetworkInfo networkInfo = networkStateTracker.getNetworkInfo();
                        if (networkInfo.isConnected() && networkInfo.getType() != prevNetType && (l = networkStateTracker.getLinkProperties()) != null && oldIface.equals(l.getInterfaceName())) {
                            doReset = false;
                            break;
                        }
                    }
                    i$++;
                }
            }
        }
        handleConnectivityChange(prevNetType, doReset);
        Intent immediateIntent = new Intent(intent);
        immediateIntent.setAction(ConnectivityManager.CONNECTIVITY_ACTION_IMMEDIATE);
        sendStickyBroadcast(immediateIntent);
        sendStickyBroadcastDelayed(intent, getConnectivityChangeDelay());
        if (this.mActiveDefaultNetwork != -1) {
            sendConnectedBroadcastDelayed(this.mNetTrackers[this.mActiveDefaultNetwork].getNetworkInfo(), getConnectivityChangeDelay());
        }
    }

    private void tryFailover(int prevNetType) {
        if (this.mNetConfigs[prevNetType].isDefault()) {
            if (this.mActiveDefaultNetwork == prevNetType) {
                log("tryFailover: set mActiveDefaultNetwork=-1, prevNetType=" + prevNetType);
                this.mActiveDefaultNetwork = -1;
            }
            for (int checkType = 0; checkType <= 14; checkType++) {
                if (checkType != prevNetType && this.mNetConfigs[checkType] != null && this.mNetConfigs[checkType].isDefault() && this.mNetTrackers[checkType] != null) {
                    NetworkStateTracker checkTracker = this.mNetTrackers[checkType];
                    NetworkInfo checkInfo = checkTracker.getNetworkInfo();
                    if (!checkInfo.isConnectedOrConnecting() || checkTracker.isTeardownRequested()) {
                        checkInfo.setFailover(true);
                        checkTracker.reconnect();
                    }
                    log("Attempting to switch to " + checkInfo.getTypeName());
                }
            }
        }
    }

    public void sendConnectedBroadcast(NetworkInfo info) {
        enforceConnectivityInternalPermission();
        sendGeneralBroadcast(info, ConnectivityManager.CONNECTIVITY_ACTION_IMMEDIATE);
        sendGeneralBroadcast(info, ConnectivityManager.CONNECTIVITY_ACTION);
    }

    private void sendConnectedBroadcastDelayed(NetworkInfo info, int delayMs) {
        sendGeneralBroadcast(info, ConnectivityManager.CONNECTIVITY_ACTION_IMMEDIATE);
        sendGeneralBroadcastDelayed(info, ConnectivityManager.CONNECTIVITY_ACTION, delayMs);
    }

    private void sendInetConditionBroadcast(NetworkInfo info) {
        sendGeneralBroadcast(info, ConnectivityManager.INET_CONDITION_ACTION);
    }

    private Intent makeGeneralIntent(NetworkInfo info, String bcastType) {
        if (this.mLockdownTracker != null) {
            info = this.mLockdownTracker.augmentNetworkInfo(info);
        }
        Intent intent = new Intent(bcastType);
        intent.putExtra("networkInfo", new NetworkInfo(info));
        intent.putExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, info.getType());
        if (info.isFailover()) {
            intent.putExtra(ConnectivityManager.EXTRA_IS_FAILOVER, true);
            info.setFailover(false);
        }
        if (info.getReason() != null) {
            intent.putExtra("reason", info.getReason());
        }
        if (info.getExtraInfo() != null) {
            intent.putExtra(ConnectivityManager.EXTRA_EXTRA_INFO, info.getExtraInfo());
        }
        intent.putExtra(ConnectivityManager.EXTRA_INET_CONDITION, this.mDefaultInetConditionPublished);
        return intent;
    }

    private void sendGeneralBroadcast(NetworkInfo info, String bcastType) {
        sendStickyBroadcast(makeGeneralIntent(info, bcastType));
    }

    private void sendGeneralBroadcastDelayed(NetworkInfo info, String bcastType, int delayMs) {
        sendStickyBroadcastDelayed(makeGeneralIntent(info, bcastType), delayMs);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConnectionFailure(NetworkInfo info) {
        String reasonText;
        this.mNetTrackers[info.getType()].setTeardownRequested(false);
        String reason = info.getReason();
        String extraInfo = info.getExtraInfo();
        if (reason == null) {
            reasonText = Separators.DOT;
        } else {
            reasonText = " (" + reason + ").";
        }
        loge("Attempt to connect to " + info.getTypeName() + " failed" + reasonText);
        Intent intent = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
        intent.putExtra("networkInfo", new NetworkInfo(info));
        intent.putExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, info.getType());
        if (getActiveNetworkInfo() == null) {
            intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, true);
        }
        if (reason != null) {
            intent.putExtra("reason", reason);
        }
        if (extraInfo != null) {
            intent.putExtra(ConnectivityManager.EXTRA_EXTRA_INFO, extraInfo);
        }
        if (info.isFailover()) {
            intent.putExtra(ConnectivityManager.EXTRA_IS_FAILOVER, true);
            info.setFailover(false);
        }
        if (this.mNetConfigs[info.getType()].isDefault()) {
            tryFailover(info.getType());
            if (this.mActiveDefaultNetwork != -1) {
                NetworkInfo switchTo = this.mNetTrackers[this.mActiveDefaultNetwork].getNetworkInfo();
                intent.putExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO, switchTo);
            } else {
                this.mDefaultInetConditionPublished = 0;
                intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, true);
            }
        }
        intent.putExtra(ConnectivityManager.EXTRA_INET_CONDITION, this.mDefaultInetConditionPublished);
        Intent immediateIntent = new Intent(intent);
        immediateIntent.setAction(ConnectivityManager.CONNECTIVITY_ACTION_IMMEDIATE);
        sendStickyBroadcast(immediateIntent);
        sendStickyBroadcast(intent);
        if (this.mActiveDefaultNetwork != -1) {
            sendConnectedBroadcast(this.mNetTrackers[this.mActiveDefaultNetwork].getNetworkInfo());
        }
    }

    private void sendStickyBroadcastDelayed(Intent intent, int delayMs) {
        if (delayMs <= 0) {
            sendStickyBroadcast(intent);
        } else {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(11, intent), delayMs);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void systemReady() {
        this.mCaptivePortalTracker = CaptivePortalTracker.makeCaptivePortalTracker(this.mContext, this);
        loadGlobalProxy();
        synchronized (this) {
            this.mSystemReady = true;
            if (this.mInitialBroadcast != null) {
                this.mContext.sendStickyBroadcastAsUser(this.mInitialBroadcast, UserHandle.ALL);
                this.mInitialBroadcast = null;
            }
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(9));
        if (!updateLockdownVpn()) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
            this.mContext.registerReceiver(this.mUserPresentReceiver, filter);
        }
    }

    private boolean isNewNetTypePreferredOverCurrentNetType(int type) {
        if ((type != this.mNetworkPreference && this.mNetConfigs[this.mActiveDefaultNetwork].priority > this.mNetConfigs[type].priority) || this.mNetworkPreference == this.mActiveDefaultNetwork) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConnect(NetworkInfo info) {
        int newNetType = info.getType();
        setupDataActivityTracking(newNetType);
        info.isFailover();
        NetworkStateTracker thisNet = this.mNetTrackers[newNetType];
        String thisIface = thisNet.getLinkProperties().getInterfaceName();
        if (this.mNetConfigs[newNetType].isDefault()) {
            if (this.mActiveDefaultNetwork != -1 && this.mActiveDefaultNetwork != newNetType) {
                if (isNewNetTypePreferredOverCurrentNetType(newNetType)) {
                    NetworkStateTracker otherNet = this.mNetTrackers[this.mActiveDefaultNetwork];
                    log("Policy requires " + otherNet.getNetworkInfo().getTypeName() + " teardown");
                    if (!teardown(otherNet)) {
                        loge("Network declined teardown request");
                        teardown(thisNet);
                        return;
                    }
                } else {
                    teardown(thisNet);
                    return;
                }
            }
            synchronized (this) {
                if (this.mNetTransitionWakeLock.isHeld()) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(8, this.mNetTransitionWakeLockSerialNumber, 0), 1000L);
                }
            }
            this.mActiveDefaultNetwork = newNetType;
            this.mDefaultInetConditionPublished = 0;
            this.mDefaultConnectionSequence++;
            this.mInetConditionChangeInFlight = false;
        }
        thisNet.setTeardownRequested(false);
        updateNetworkSettings(thisNet);
        updateMtuSizeSettings(thisNet);
        handleConnectivityChange(newNetType, false);
        sendConnectedBroadcastDelayed(info, getConnectivityChangeDelay());
        if (thisIface != null) {
            try {
                BatteryStatsService.getService().noteNetworkInterfaceType(thisIface, newNetType);
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCaptivePortalTrackerCheck(NetworkInfo info) {
        log("Captive portal check " + info);
        int type = info.getType();
        NetworkStateTracker thisNet = this.mNetTrackers[type];
        if (this.mNetConfigs[type].isDefault() && this.mActiveDefaultNetwork != -1 && this.mActiveDefaultNetwork != type) {
            if (isNewNetTypePreferredOverCurrentNetType(type)) {
                log("Captive check on " + info.getTypeName());
                this.mCaptivePortalTracker.detectCaptivePortal(new NetworkInfo(info));
                return;
            }
            log("Tear down low priority net " + info.getTypeName());
            teardown(thisNet);
            return;
        }
        log("handleCaptivePortalTrackerCheck: call captivePortalCheckComplete ni=" + info);
        thisNet.captivePortalCheckComplete();
    }

    @Override // android.net.IConnectivityManager
    public void captivePortalCheckComplete(NetworkInfo info) {
        enforceConnectivityInternalPermission();
        log("captivePortalCheckComplete: ni=" + info);
        this.mNetTrackers[info.getType()].captivePortalCheckComplete();
    }

    @Override // android.net.IConnectivityManager
    public void captivePortalCheckCompleted(NetworkInfo info, boolean isCaptivePortal) {
        enforceConnectivityInternalPermission();
        log("captivePortalCheckCompleted: ni=" + info + " captive=" + isCaptivePortal);
        this.mNetTrackers[info.getType()].captivePortalCheckCompleted(isCaptivePortal);
    }

    private void setupDataActivityTracking(int type) {
        int timeout;
        NetworkStateTracker thisNet = this.mNetTrackers[type];
        String iface = thisNet.getLinkProperties().getInterfaceName();
        if (ConnectivityManager.isNetworkTypeMobile(type)) {
            timeout = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.DATA_ACTIVITY_TIMEOUT_MOBILE, 0);
            type = 0;
        } else if (1 == type) {
            timeout = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.DATA_ACTIVITY_TIMEOUT_WIFI, 0);
        } else {
            timeout = 0;
        }
        if (timeout > 0 && iface != null) {
            try {
                this.mNetd.addIdleTimer(iface, timeout, Integer.toString(type));
            } catch (RemoteException e) {
            }
        }
    }

    private void removeDataActivityTracking(int type) {
        NetworkStateTracker net = this.mNetTrackers[type];
        String iface = net.getLinkProperties().getInterfaceName();
        if (iface != null) {
            if (ConnectivityManager.isNetworkTypeMobile(type) || 1 == type) {
                try {
                    this.mNetd.removeIdleTimer(iface);
                } catch (RemoteException e) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConnectivityChange(int netType, boolean doReset) {
        int resetMask = doReset ? 3 : 0;
        boolean exempt = ConnectivityManager.isNetworkTypeExempt(netType);
        handleDnsConfigurationChange(netType);
        LinkProperties curLp = this.mCurrentLinkProperties[netType];
        LinkProperties newLp = null;
        if (this.mNetTrackers[netType].getNetworkInfo().isConnected()) {
            newLp = this.mNetTrackers[netType].getLinkProperties();
            if (curLp != null) {
                if (curLp.isIdenticalInterfaceName(newLp)) {
                    LinkProperties.CompareResult<LinkAddress> car = curLp.compareAddresses(newLp);
                    if (car.removed.size() != 0 || car.added.size() != 0) {
                        for (LinkAddress linkAddr : car.removed) {
                            if (linkAddr.getAddress() instanceof Inet4Address) {
                                resetMask |= 1;
                            }
                            if (linkAddr.getAddress() instanceof Inet6Address) {
                                resetMask |= 2;
                            }
                        }
                        log("handleConnectivityChange: addresses changed linkProperty[" + netType + "]: resetMask=" + resetMask + "\n   car=" + car);
                    } else {
                        log("handleConnectivityChange: address are the same reset per doReset linkProperty[" + netType + "]: resetMask=" + resetMask);
                    }
                } else {
                    resetMask = 3;
                    log("handleConnectivityChange: interface not not equivalent reset both linkProperty[" + netType + "]: resetMask=3");
                }
            }
            if (this.mNetConfigs[netType].isDefault()) {
                handleApplyDefaultProxy(newLp.getHttpProxy());
            }
        }
        this.mCurrentLinkProperties[netType] = newLp;
        boolean resetDns = updateRoutes(newLp, curLp, this.mNetConfigs[netType].isDefault(), exempt);
        if ((resetMask != 0 || resetDns) && curLp != null) {
            for (String iface : curLp.getAllInterfaceNames()) {
                if (!TextUtils.isEmpty(iface)) {
                    if (resetMask != 0) {
                        log("resetConnections(" + iface + ", " + resetMask + Separators.RPAREN);
                        NetworkUtils.resetConnections(iface, resetMask);
                        if ((resetMask & 1) != 0) {
                            synchronized (this.mVpns) {
                                for (int i = 0; i < this.mVpns.size(); i++) {
                                    this.mVpns.valueAt(i).interfaceStatusChanged(iface, false);
                                }
                            }
                        }
                    }
                    if (resetDns) {
                        flushVmDnsCache();
                        try {
                            this.mNetd.flushInterfaceDnsCache(iface);
                        } catch (Exception e) {
                            loge("Exception resetting dns cache: " + e);
                        }
                    }
                } else {
                    loge("Can't reset connection for type " + netType);
                }
            }
        }
        NetworkStateTracker tracker = this.mNetTrackers[netType];
        if (this.mClat.requiresClat(netType, tracker)) {
            if (Nat464Xlat.isRunningClat(curLp) && !Nat464Xlat.isRunningClat(newLp)) {
                this.mClat.stopClat();
            }
            if (this.mNetTrackers[netType].getNetworkInfo().isConnected()) {
                this.mClat.startClat(tracker);
            } else {
                this.mClat.stopClat();
            }
        }
        if (TextUtils.equals(this.mNetTrackers[netType].getNetworkInfo().getReason(), PhoneConstants.REASON_LINK_PROPERTIES_CHANGED) && isTetheringSupported()) {
            this.mTethering.handleTetherIfaceChange();
        }
    }

    private boolean updateRoutes(LinkProperties newLp, LinkProperties curLp, boolean isLinkDefault, boolean exempt) {
        LinkProperties.CompareResult<InetAddress> dnsDiff = new LinkProperties.CompareResult<>();
        LinkProperties.CompareResult<RouteInfo> routeDiff = new LinkProperties.CompareResult<>();
        if (curLp != null) {
            routeDiff = curLp.compareAllRoutes(newLp);
            dnsDiff = curLp.compareDnses(newLp);
        } else if (newLp != null) {
            routeDiff.added = newLp.getAllRoutes();
            dnsDiff.added = newLp.getDnses();
        }
        boolean routesChanged = (routeDiff.removed.size() == 0 && routeDiff.added.size() == 0) ? false : true;
        for (RouteInfo r : routeDiff.removed) {
            if (isLinkDefault || !r.isDefaultRoute()) {
                removeRoute(curLp, r, true);
            }
            if (!isLinkDefault) {
                removeRoute(curLp, r, false);
            }
        }
        if (!isLinkDefault) {
            if (routesChanged) {
                if (curLp != null) {
                    for (InetAddress oldDns : curLp.getDnses()) {
                        removeRouteToAddress(curLp, oldDns);
                    }
                }
                if (newLp != null) {
                    for (InetAddress newDns : newLp.getDnses()) {
                        addRouteToAddress(newLp, newDns, exempt);
                    }
                }
            } else {
                for (InetAddress oldDns2 : dnsDiff.removed) {
                    removeRouteToAddress(curLp, oldDns2);
                }
                for (InetAddress newDns2 : dnsDiff.added) {
                    addRouteToAddress(newLp, newDns2, exempt);
                }
            }
        }
        for (RouteInfo r2 : routeDiff.added) {
            if (isLinkDefault || !r2.isDefaultRoute()) {
                addRoute(newLp, r2, true, exempt);
            } else {
                addRoute(newLp, r2, false, false);
                String ifaceName = newLp.getInterfaceName();
                synchronized (this.mRoutesLock) {
                    if (!TextUtils.isEmpty(ifaceName) && !this.mAddedRoutes.contains(r2)) {
                        try {
                            this.mNetd.removeRoute(ifaceName, r2);
                        } catch (Exception e) {
                            loge("Exception trying to remove a route: " + e);
                        }
                    }
                }
            }
        }
        return routesChanged;
    }

    private void updateMtuSizeSettings(NetworkStateTracker nt) {
        String iface = nt.getLinkProperties().getInterfaceName();
        int mtu = nt.getLinkProperties().getMtu();
        if (mtu < 68 || mtu > 10000) {
            loge("Unexpected mtu value: " + nt);
            return;
        }
        try {
            this.mNetd.setMtu(iface, mtu);
        } catch (Exception e) {
            Slog.e(TAG, "exception in setMtu()" + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkSettings(NetworkStateTracker nt) {
        String key = nt.getTcpBufferSizesPropName();
        String bufferSizes = key == null ? null : SystemProperties.get(key);
        if (TextUtils.isEmpty(bufferSizes)) {
            bufferSizes = SystemProperties.get("net.tcp.buffersize.default");
        }
        if (bufferSizes.length() != 0) {
            setBufferSize(bufferSizes);
        }
    }

    private void setBufferSize(String bufferSizes) {
        try {
            String[] values = bufferSizes.split(Separators.COMMA);
            if (values.length == 6) {
                FileUtils.stringToFile("/sys/kernel/ipv4/tcp_rmem_min", values[0]);
                FileUtils.stringToFile("/sys/kernel/ipv4/tcp_rmem_def", values[1]);
                FileUtils.stringToFile("/sys/kernel/ipv4/tcp_rmem_max", values[2]);
                FileUtils.stringToFile("/sys/kernel/ipv4/tcp_wmem_min", values[3]);
                FileUtils.stringToFile("/sys/kernel/ipv4/tcp_wmem_def", values[4]);
                FileUtils.stringToFile("/sys/kernel/ipv4/tcp_wmem_max", values[5]);
            } else {
                loge("Invalid buffersize string: " + bufferSizes);
            }
        } catch (IOException e) {
            loge("Can't set tcp buffer sizes:" + e);
        }
    }

    private void reassessPidDns(int pid, boolean doBump) {
        LinkProperties p;
        Integer myPid = new Integer(pid);
        int[] arr$ = this.mPriorityList;
        for (int i : arr$) {
            if (!this.mNetConfigs[i].isDefault()) {
                NetworkStateTracker nt = this.mNetTrackers[i];
                if (nt.getNetworkInfo().isConnected() && !nt.isTeardownRequested() && (p = nt.getLinkProperties()) != null && this.mNetRequestersPids[i].contains(myPid)) {
                    try {
                        this.mNetd.setDnsInterfaceForPid(p.getInterfaceName(), pid);
                        return;
                    } catch (Exception e) {
                        Slog.e(TAG, "exception reasseses pid dns: " + e);
                        return;
                    }
                }
            }
        }
        try {
            this.mNetd.clearDnsInterfaceForPid(pid);
        } catch (Exception e2) {
            Slog.e(TAG, "exception clear interface from pid: " + e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDnsLocked(String network, String iface, Collection<InetAddress> dnses, String domains, boolean defaultDns) {
        int last = 0;
        if (dnses.size() == 0 && this.mDefaultDns != null) {
            dnses = new ArrayList();
            dnses.add(this.mDefaultDns);
            loge("no dns provided for " + network + " - using " + this.mDefaultDns.getHostAddress());
        }
        try {
            this.mNetd.setDnsServersForInterface(iface, NetworkUtils.makeStrings(dnses), domains);
            if (defaultDns) {
                this.mNetd.setDefaultInterfaceForDns(iface);
            }
            for (InetAddress dns : dnses) {
                last++;
                String key = "net.dns" + last;
                String value = dns.getHostAddress();
                SystemProperties.set(key, value);
            }
            for (int i = last + 1; i <= this.mNumDnsEntries; i++) {
                String key2 = "net.dns" + i;
                SystemProperties.set(key2, "");
            }
            this.mNumDnsEntries = last;
        } catch (Exception e) {
            loge("exception setting default dns interface: " + e);
        }
    }

    private void handleDnsConfigurationChange(int netType) {
        LinkProperties p;
        NetworkStateTracker nt = this.mNetTrackers[netType];
        if (nt == null || !nt.getNetworkInfo().isConnected() || nt.isTeardownRequested() || (p = nt.getLinkProperties()) == null) {
            return;
        }
        Collection<InetAddress> dnses = p.getDnses();
        if (this.mNetConfigs[netType].isDefault()) {
            String network = nt.getNetworkInfo().getTypeName();
            synchronized (this.mDnsLock) {
                updateDnsLocked(network, p.getInterfaceName(), dnses, p.getDomains(), true);
            }
        } else {
            try {
                this.mNetd.setDnsServersForInterface(p.getInterfaceName(), NetworkUtils.makeStrings(dnses), p.getDomains());
            } catch (Exception e) {
                loge("exception setting dns servers: " + e);
            }
            List<Integer> pids = this.mNetRequestersPids[netType];
            for (Integer pid : pids) {
                try {
                    this.mNetd.setDnsInterfaceForPid(p.getInterfaceName(), pid.intValue());
                } catch (Exception e2) {
                    Slog.e(TAG, "exception setting interface for pid: " + e2);
                }
            }
        }
        flushVmDnsCache();
    }

    private int getRestoreDefaultNetworkDelay(int networkType) {
        String restoreDefaultNetworkDelayStr = SystemProperties.get(NETWORK_RESTORE_DELAY_PROP_NAME);
        if (restoreDefaultNetworkDelayStr != null && restoreDefaultNetworkDelayStr.length() != 0) {
            try {
                return Integer.valueOf(restoreDefaultNetworkDelayStr).intValue();
            } catch (NumberFormatException e) {
            }
        }
        int ret = 60000;
        if (networkType <= 14 && this.mNetConfigs[networkType] != null) {
            ret = this.mNetConfigs[networkType].restoreTime;
        }
        return ret;
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        IndentingPrintWriter pw = new IndentingPrintWriter(writer, "  ");
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump ConnectivityService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println();
        for (int i = 0; i < this.mNetTrackers.length; i++) {
            NetworkStateTracker nst = this.mNetTrackers[i];
            if (nst != null) {
                pw.println("NetworkStateTracker for " + ConnectivityManager.getNetworkTypeName(i) + Separators.COLON);
                pw.increaseIndent();
                if (nst.getNetworkInfo().isConnected()) {
                    pw.println("Active network: " + nst.getNetworkInfo().getTypeName());
                }
                pw.println(nst.getNetworkInfo());
                pw.println(nst.getLinkProperties());
                pw.println(nst);
                pw.println();
                pw.decreaseIndent();
            }
        }
        pw.println("Network Requester Pids:");
        pw.increaseIndent();
        int[] arr$ = this.mPriorityList;
        for (int net : arr$) {
            String pidString = net + ": ";
            for (Integer pid : this.mNetRequestersPids[net]) {
                pidString = pidString + pid.toString() + ", ";
            }
            pw.println(pidString);
        }
        pw.println();
        pw.decreaseIndent();
        pw.println("FeatureUsers:");
        pw.increaseIndent();
        for (Object requester : this.mFeatureUsers) {
            pw.println(requester.toString());
        }
        pw.println();
        pw.decreaseIndent();
        synchronized (this) {
            pw.println("NetworkTranstionWakeLock is currently " + (this.mNetTransitionWakeLock.isHeld() ? "" : "not ") + "held.");
            pw.println("It was last requested for " + this.mNetTransitionWakeLockCausedBy);
        }
        pw.println();
        this.mTethering.dump(fd, pw, args);
        if (this.mInetLog != null) {
            pw.println();
            pw.println("Inet condition reports:");
            pw.increaseIndent();
            for (int i2 = 0; i2 < this.mInetLog.size(); i2++) {
                pw.println(this.mInetLog.get(i2));
            }
            pw.decreaseIndent();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ConnectivityService$NetworkStateTrackerHandler.class */
    public class NetworkStateTrackerHandler extends Handler {
        public NetworkStateTrackerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 458752:
                    NetworkInfo info = (NetworkInfo) msg.obj;
                    NetworkInfo.State state = info.getState();
                    if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.DISCONNECTED || state == NetworkInfo.State.SUSPENDED) {
                        ConnectivityService.log("ConnectivityChange for " + info.getTypeName() + ": " + state + Separators.SLASH + info.getDetailedState());
                    }
                    if (ConnectivityManager.isNetworkTypeMobile(info.getType()) && 0 != Settings.Global.getInt(ConnectivityService.this.mContext.getContentResolver(), "device_provisioned", 0) && ((state == NetworkInfo.State.CONNECTED && info.getType() == 0) || info.isConnectedToProvisioningNetwork())) {
                        ConnectivityService.log("ConnectivityChange checkMobileProvisioning for TYPE_MOBILE or ProvisioningNetwork");
                        ConnectivityService.this.checkMobileProvisioning(60000);
                    }
                    EventLogTags.writeConnectivityStateChanged(info.getType(), info.getSubtype(), info.getDetailedState().ordinal());
                    if (info.getDetailedState() == NetworkInfo.DetailedState.FAILED) {
                        ConnectivityService.this.handleConnectionFailure(info);
                    } else if (info.getDetailedState() == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK) {
                        ConnectivityService.this.handleCaptivePortalTrackerCheck(info);
                    } else if (info.isConnectedToProvisioningNetwork()) {
                        LinkProperties lp = ConnectivityService.this.getLinkProperties(info.getType());
                        ConnectivityService.log("EVENT_STATE_CHANGED: connected to provisioning network, lp=" + lp);
                        for (RouteInfo r : lp.getRoutes()) {
                            ConnectivityService.this.removeRoute(lp, r, true);
                        }
                    } else if (state == NetworkInfo.State.DISCONNECTED) {
                        ConnectivityService.this.handleDisconnect(info);
                    } else if (state == NetworkInfo.State.SUSPENDED) {
                        ConnectivityService.this.handleDisconnect(info);
                    } else if (state == NetworkInfo.State.CONNECTED) {
                        ConnectivityService.this.handleConnect(info);
                    }
                    if (ConnectivityService.this.mLockdownTracker != null) {
                        ConnectivityService.this.mLockdownTracker.onNetworkInfoChanged(info);
                        return;
                    }
                    return;
                case NetworkStateTracker.EVENT_CONFIGURATION_CHANGED /* 458753 */:
                    NetworkInfo info2 = (NetworkInfo) msg.obj;
                    ConnectivityService.this.handleConnectivityChange(info2.getType(), false);
                    return;
                case NetworkStateTracker.EVENT_RESTORE_DEFAULT_NETWORK /* 458754 */:
                default:
                    return;
                case NetworkStateTracker.EVENT_NETWORK_SUBTYPE_CHANGED /* 458755 */:
                    NetworkInfo info3 = (NetworkInfo) msg.obj;
                    int type = info3.getType();
                    ConnectivityService.this.updateNetworkSettings(ConnectivityService.this.mNetTrackers[type]);
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ConnectivityService$InternalHandler.class */
    public class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    FeatureUser u = (FeatureUser) msg.obj;
                    u.expire();
                    return;
                case 2:
                case 6:
                default:
                    return;
                case 3:
                    int preference = msg.arg1;
                    ConnectivityService.this.handleSetNetworkPreference(preference);
                    return;
                case 4:
                    int netType = msg.arg1;
                    int condition = msg.arg2;
                    ConnectivityService.this.handleInetConditionChange(netType, condition);
                    return;
                case 5:
                    int netType2 = msg.arg1;
                    int sequence = msg.arg2;
                    ConnectivityService.this.handleInetConditionHoldEnd(netType2, sequence);
                    return;
                case 7:
                    boolean enabled = msg.arg1 == 1;
                    ConnectivityService.this.handleSetMobileData(enabled);
                    return;
                case 8:
                    String causedBy = null;
                    synchronized (ConnectivityService.this) {
                        if (msg.arg1 == ConnectivityService.this.mNetTransitionWakeLockSerialNumber && ConnectivityService.this.mNetTransitionWakeLock.isHeld()) {
                            ConnectivityService.this.mNetTransitionWakeLock.release();
                            causedBy = ConnectivityService.this.mNetTransitionWakeLockCausedBy;
                        }
                    }
                    if (causedBy != null) {
                        ConnectivityService.log("NetTransition Wakelock for " + causedBy + " released by timeout");
                        return;
                    }
                    return;
                case 9:
                    ConnectivityService.this.handleDeprecatedGlobalHttpProxy();
                    return;
                case 10:
                    boolean met = msg.arg1 == 1;
                    ConnectivityService.this.handleSetDependencyMet(msg.arg2, met);
                    return;
                case 11:
                    Intent intent = (Intent) msg.obj;
                    ConnectivityService.this.sendStickyBroadcast(intent);
                    return;
                case 12:
                    int networkType = msg.arg1;
                    boolean enabled2 = msg.arg2 == 1;
                    ConnectivityService.this.handleSetPolicyDataEnable(networkType, enabled2);
                    return;
                case 13:
                    if (ConnectivityService.this.mLockdownTracker != null) {
                        ConnectivityService.this.mLockdownTracker.onVpnStateChanged((NetworkInfo) msg.obj);
                        return;
                    }
                    return;
                case 14:
                    int tag = ConnectivityService.this.mEnableFailFastMobileDataTag.get();
                    if (msg.arg1 == tag) {
                        MobileDataStateTracker mobileDst = (MobileDataStateTracker) ConnectivityService.this.mNetTrackers[0];
                        if (mobileDst != null) {
                            mobileDst.setEnableFailFastMobileData(msg.arg2);
                            return;
                        }
                        return;
                    }
                    ConnectivityService.log("EVENT_ENABLE_FAIL_FAST_MOBILE_DATA: stale arg1:" + msg.arg1 + " != tag:" + tag);
                    return;
                case 15:
                    ConnectivityService.this.handleNetworkSamplingTimeout();
                    return;
                case 16:
                    ConnectivityService.this.handleApplyDefaultProxy((ProxyProperties) msg.obj);
                    return;
            }
        }
    }

    @Override // android.net.IConnectivityManager
    public int tether(String iface) {
        enforceTetherChangePermission();
        if (isTetheringSupported()) {
            return this.mTethering.tether(iface);
        }
        return 3;
    }

    @Override // android.net.IConnectivityManager
    public int untether(String iface) {
        enforceTetherChangePermission();
        if (isTetheringSupported()) {
            return this.mTethering.untether(iface);
        }
        return 3;
    }

    @Override // android.net.IConnectivityManager
    public int getLastTetherError(String iface) {
        enforceTetherAccessPermission();
        if (isTetheringSupported()) {
            return this.mTethering.getLastTetherError(iface);
        }
        return 3;
    }

    @Override // android.net.IConnectivityManager
    public String[] getTetherableUsbRegexs() {
        enforceTetherAccessPermission();
        if (isTetheringSupported()) {
            return this.mTethering.getTetherableUsbRegexs();
        }
        return new String[0];
    }

    @Override // android.net.IConnectivityManager
    public String[] getTetherableWifiRegexs() {
        enforceTetherAccessPermission();
        if (isTetheringSupported()) {
            return this.mTethering.getTetherableWifiRegexs();
        }
        return new String[0];
    }

    @Override // android.net.IConnectivityManager
    public String[] getTetherableBluetoothRegexs() {
        enforceTetherAccessPermission();
        if (isTetheringSupported()) {
            return this.mTethering.getTetherableBluetoothRegexs();
        }
        return new String[0];
    }

    @Override // android.net.IConnectivityManager
    public int setUsbTethering(boolean enable) {
        enforceTetherChangePermission();
        if (isTetheringSupported()) {
            return this.mTethering.setUsbTethering(enable);
        }
        return 3;
    }

    @Override // android.net.IConnectivityManager
    public String[] getTetherableIfaces() {
        enforceTetherAccessPermission();
        return this.mTethering.getTetherableIfaces();
    }

    @Override // android.net.IConnectivityManager
    public String[] getTetheredIfaces() {
        enforceTetherAccessPermission();
        return this.mTethering.getTetheredIfaces();
    }

    @Override // android.net.IConnectivityManager
    public String[] getTetheringErroredIfaces() {
        enforceTetherAccessPermission();
        return this.mTethering.getErroredIfaces();
    }

    @Override // android.net.IConnectivityManager
    public boolean isTetheringSupported() {
        enforceTetherAccessPermission();
        int defaultVal = SystemProperties.get("ro.tether.denied").equals("true") ? 0 : 1;
        boolean tetherEnabledInSettings = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.TETHER_SUPPORTED, defaultVal) != 0;
        return tetherEnabledInSettings && !((this.mTethering.getTetherableUsbRegexs().length == 0 && this.mTethering.getTetherableWifiRegexs().length == 0 && this.mTethering.getTetherableBluetoothRegexs().length == 0) || this.mTethering.getUpstreamIfaceTypes().length == 0);
    }

    @Override // android.net.IConnectivityManager
    public void requestNetworkTransitionWakelock(String forWhom) {
        enforceConnectivityInternalPermission();
        synchronized (this) {
            if (this.mNetTransitionWakeLock.isHeld()) {
                return;
            }
            this.mNetTransitionWakeLockSerialNumber++;
            this.mNetTransitionWakeLock.acquire();
            this.mNetTransitionWakeLockCausedBy = forWhom;
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(8, this.mNetTransitionWakeLockSerialNumber, 0), this.mNetTransitionWakeLockTimeout);
        }
    }

    @Override // android.net.IConnectivityManager
    public void reportInetCondition(int networkType, int percentage) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.STATUS_BAR, TAG);
        int pid = getCallingPid();
        int uid = getCallingUid();
        String s = pid + Separators.LPAREN + uid + ") reports inet is " + (percentage > 50 ? "connected" : "disconnected") + " (" + percentage + ") on network Type " + networkType + " at " + GregorianCalendar.getInstance().getTime();
        this.mInetLog.add(s);
        while (this.mInetLog.size() > 15) {
            this.mInetLog.remove(0);
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, networkType, percentage));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleInetConditionChange(int netType, int condition) {
        int delay;
        if (this.mActiveDefaultNetwork == -1) {
            log("handleInetConditionChange: no active default network - ignore");
        } else if (this.mActiveDefaultNetwork != netType) {
            log("handleInetConditionChange: net=" + netType + " != default=" + this.mActiveDefaultNetwork + " - ignore");
        } else {
            this.mDefaultInetCondition = condition;
            if (!this.mInetConditionChangeInFlight) {
                if (this.mDefaultInetCondition > 50) {
                    delay = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.INET_CONDITION_DEBOUNCE_UP_DELAY, 500);
                } else {
                    delay = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.INET_CONDITION_DEBOUNCE_DOWN_DELAY, ConnectivityManager.CONNECTIVITY_CHANGE_DELAY_DEFAULT);
                }
                this.mInetConditionChangeInFlight = true;
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(5, this.mActiveDefaultNetwork, this.mDefaultConnectionSequence), delay);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleInetConditionHoldEnd(int netType, int sequence) {
        log("handleInetConditionHoldEnd: net=" + netType + ", condition=" + this.mDefaultInetCondition + ", published condition=" + this.mDefaultInetConditionPublished);
        this.mInetConditionChangeInFlight = false;
        if (this.mActiveDefaultNetwork == -1) {
            log("handleInetConditionHoldEnd: no active default network - ignoring");
        } else if (this.mDefaultConnectionSequence != sequence) {
            log("handleInetConditionHoldEnd: event hold for obsolete network - ignoring");
        } else {
            NetworkInfo networkInfo = this.mNetTrackers[this.mActiveDefaultNetwork].getNetworkInfo();
            if (!networkInfo.isConnected()) {
                log("handleInetConditionHoldEnd: default network not connected - ignoring");
                return;
            }
            this.mDefaultInetConditionPublished = this.mDefaultInetCondition;
            sendInetConditionBroadcast(networkInfo);
        }
    }

    @Override // android.net.IConnectivityManager
    public ProxyProperties getProxy() {
        ProxyProperties proxyProperties;
        synchronized (this.mProxyLock) {
            ProxyProperties ret = this.mGlobalProxy;
            if (ret == null && !this.mDefaultProxyDisabled) {
                ret = this.mDefaultProxy;
            }
            proxyProperties = ret;
        }
        return proxyProperties;
    }

    private void loadGlobalProxy() {
        ProxyProperties proxyProperties;
        ContentResolver res = this.mContext.getContentResolver();
        String host = Settings.Global.getString(res, Settings.Global.GLOBAL_HTTP_PROXY_HOST);
        int port = Settings.Global.getInt(res, Settings.Global.GLOBAL_HTTP_PROXY_PORT, 0);
        String exclList = Settings.Global.getString(res, Settings.Global.GLOBAL_HTTP_PROXY_EXCLUSION_LIST);
        String pacFileUrl = Settings.Global.getString(res, Settings.Global.GLOBAL_HTTP_PROXY_PAC);
        if (!TextUtils.isEmpty(host) || !TextUtils.isEmpty(pacFileUrl)) {
            if (!TextUtils.isEmpty(pacFileUrl)) {
                proxyProperties = new ProxyProperties(pacFileUrl);
            } else {
                proxyProperties = new ProxyProperties(host, port, exclList);
            }
            synchronized (this.mProxyLock) {
                this.mGlobalProxy = proxyProperties;
            }
        }
    }

    @Override // android.net.IConnectivityManager
    public ProxyProperties getGlobalProxy() {
        ProxyProperties proxyProperties;
        synchronized (this.mProxyLock) {
            proxyProperties = this.mGlobalProxy;
        }
        return proxyProperties;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleApplyDefaultProxy(ProxyProperties proxy) {
        if (proxy != null && TextUtils.isEmpty(proxy.getHost()) && TextUtils.isEmpty(proxy.getPacFileUrl())) {
            proxy = null;
        }
        synchronized (this.mProxyLock) {
            if (this.mDefaultProxy == null || !this.mDefaultProxy.equals(proxy)) {
                if (this.mDefaultProxy == proxy) {
                    return;
                }
                this.mDefaultProxy = proxy;
                if (this.mGlobalProxy != null) {
                    return;
                }
                if (!this.mDefaultProxyDisabled) {
                    sendProxyBroadcast(proxy);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDeprecatedGlobalHttpProxy() {
        String proxy = Settings.Global.getString(this.mContext.getContentResolver(), "http_proxy");
        if (!TextUtils.isEmpty(proxy)) {
            String[] data = proxy.split(Separators.COLON);
            if (data.length == 0) {
                return;
            }
            String str = data[0];
            int proxyPort = 8080;
            if (data.length > 1) {
                try {
                    proxyPort = Integer.parseInt(data[1]);
                } catch (NumberFormatException e) {
                    return;
                }
            }
            ProxyProperties p = new ProxyProperties(data[0], proxyPort, "");
            setGlobalProxy(p);
        }
    }

    /* loaded from: ConnectivityService$SettingsObserver.class */
    private static class SettingsObserver extends ContentObserver {
        private int mWhat;
        private Handler mHandler;

        SettingsObserver(Handler handler, int what) {
            super(handler);
            this.mHandler = handler;
            this.mWhat = what;
        }

        void observe(Context context) {
            ContentResolver resolver = context.getContentResolver();
            resolver.registerContentObserver(Settings.Global.getUriFor("http_proxy"), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            this.mHandler.obtainMessage(this.mWhat).sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String s) {
        Slog.d(TAG, s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String s) {
        Slog.e(TAG, s);
    }

    int convertFeatureToNetworkType(int networkType, String feature) {
        int usedNetworkType = networkType;
        if (networkType == 0) {
            if (TextUtils.equals(feature, Phone.FEATURE_ENABLE_MMS)) {
                usedNetworkType = 2;
            } else if (TextUtils.equals(feature, Phone.FEATURE_ENABLE_SUPL)) {
                usedNetworkType = 3;
            } else if (TextUtils.equals(feature, Phone.FEATURE_ENABLE_DUN) || TextUtils.equals(feature, Phone.FEATURE_ENABLE_DUN_ALWAYS)) {
                usedNetworkType = 4;
            } else if (TextUtils.equals(feature, Phone.FEATURE_ENABLE_HIPRI)) {
                usedNetworkType = 5;
            } else if (TextUtils.equals(feature, Phone.FEATURE_ENABLE_FOTA)) {
                usedNetworkType = 10;
            } else if (TextUtils.equals(feature, Phone.FEATURE_ENABLE_IMS)) {
                usedNetworkType = 11;
            } else if (TextUtils.equals(feature, Phone.FEATURE_ENABLE_CBS)) {
                usedNetworkType = 12;
            } else {
                Slog.e(TAG, "Can't match any mobile netTracker!");
            }
        } else if (networkType == 1) {
            if (TextUtils.equals(feature, "p2p")) {
                usedNetworkType = 13;
            } else {
                Slog.e(TAG, "Can't match any wifi netTracker!");
            }
        } else {
            Slog.e(TAG, "Unexpected network type");
        }
        return usedNetworkType;
    }

    private static <T> T checkNotNull(T value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }

    @Override // android.net.IConnectivityManager
    public boolean prepareVpn(String oldPackage, String newPackage) {
        boolean prepare;
        throwIfLockdownEnabled();
        int user = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mVpns) {
            prepare = this.mVpns.get(user).prepare(oldPackage, newPackage);
        }
        return prepare;
    }

    @Override // android.net.IConnectivityManager
    public ParcelFileDescriptor establishVpn(VpnConfig config) {
        ParcelFileDescriptor establish;
        throwIfLockdownEnabled();
        int user = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mVpns) {
            establish = this.mVpns.get(user).establish(config);
        }
        return establish;
    }

    @Override // android.net.IConnectivityManager
    public void startLegacyVpn(VpnProfile profile) {
        throwIfLockdownEnabled();
        LinkProperties egress = getActiveLinkProperties();
        if (egress == null) {
            throw new IllegalStateException("Missing active network connection");
        }
        int user = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mVpns) {
            this.mVpns.get(user).startLegacyVpn(profile, this.mKeyStore, egress);
        }
    }

    @Override // android.net.IConnectivityManager
    public LegacyVpnInfo getLegacyVpnInfo() {
        LegacyVpnInfo legacyVpnInfo;
        throwIfLockdownEnabled();
        int user = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mVpns) {
            legacyVpnInfo = this.mVpns.get(user).getLegacyVpnInfo();
        }
        return legacyVpnInfo;
    }

    @Override // android.net.IConnectivityManager
    public VpnConfig getVpnConfig() {
        VpnConfig vpnConfig;
        int user = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mVpns) {
            vpnConfig = this.mVpns.get(user).getVpnConfig();
        }
        return vpnConfig;
    }

    /* loaded from: ConnectivityService$VpnCallback.class */
    public class VpnCallback {
        private VpnCallback() {
        }

        public void onStateChanged(NetworkInfo info) {
            ConnectivityService.this.mHandler.obtainMessage(13, info).sendToTarget();
        }

        public void override(String iface, List<String> dnsServers, List<String> searchDomains) {
            if (dnsServers == null) {
                restore();
                return;
            }
            List<InetAddress> addresses = new ArrayList<>();
            for (String address : dnsServers) {
                try {
                    addresses.add(InetAddress.parseNumericAddress(address));
                } catch (Exception e) {
                }
            }
            if (addresses.isEmpty()) {
                restore();
                return;
            }
            StringBuilder buffer = new StringBuilder();
            if (searchDomains != null) {
                for (String domain : searchDomains) {
                    buffer.append(domain).append(' ');
                }
            }
            String domains = buffer.toString().trim();
            synchronized (ConnectivityService.this.mDnsLock) {
                ConnectivityService.this.updateDnsLocked("VPN", iface, addresses, domains, false);
            }
            synchronized (ConnectivityService.this.mProxyLock) {
                ConnectivityService.this.mDefaultProxyDisabled = true;
                if (ConnectivityService.this.mGlobalProxy == null && ConnectivityService.this.mDefaultProxy != null) {
                    ConnectivityService.this.sendProxyBroadcast(null);
                }
            }
        }

        public void restore() {
            synchronized (ConnectivityService.this.mProxyLock) {
                ConnectivityService.this.mDefaultProxyDisabled = false;
                if (ConnectivityService.this.mGlobalProxy == null && ConnectivityService.this.mDefaultProxy != null) {
                    ConnectivityService.this.sendProxyBroadcast(ConnectivityService.this.mDefaultProxy);
                }
            }
        }

        public void protect(ParcelFileDescriptor socket) {
            try {
                int mark = ConnectivityService.this.mNetd.getMarkForProtect();
                NetworkUtils.markSocket(socket.getFd(), mark);
            } catch (RemoteException e) {
            }
        }

        public void setRoutes(String interfaze, List<RouteInfo> routes) {
            for (RouteInfo route : routes) {
                try {
                    ConnectivityService.this.mNetd.setMarkedForwardingRoute(interfaze, route);
                } catch (RemoteException e) {
                }
            }
        }

        public void setMarkedForwarding(String interfaze) {
            try {
                ConnectivityService.this.mNetd.setMarkedForwarding(interfaze);
            } catch (RemoteException e) {
            }
        }

        public void clearMarkedForwarding(String interfaze) {
            try {
                ConnectivityService.this.mNetd.clearMarkedForwarding(interfaze);
            } catch (RemoteException e) {
            }
        }

        public void addUserForwarding(String interfaze, int uid, boolean forwardDns) {
            int uidStart = uid * UserHandle.PER_USER_RANGE;
            int uidEnd = (uidStart + UserHandle.PER_USER_RANGE) - 1;
            addUidForwarding(interfaze, uidStart, uidEnd, forwardDns);
        }

        public void clearUserForwarding(String interfaze, int uid, boolean forwardDns) {
            int uidStart = uid * UserHandle.PER_USER_RANGE;
            int uidEnd = (uidStart + UserHandle.PER_USER_RANGE) - 1;
            clearUidForwarding(interfaze, uidStart, uidEnd, forwardDns);
        }

        public void addUidForwarding(String interfaze, int uidStart, int uidEnd, boolean forwardDns) {
            try {
                ConnectivityService.this.mNetd.setUidRangeRoute(interfaze, uidStart, uidEnd);
                if (forwardDns) {
                    ConnectivityService.this.mNetd.setDnsInterfaceForUidRange(interfaze, uidStart, uidEnd);
                }
            } catch (RemoteException e) {
            }
        }

        public void clearUidForwarding(String interfaze, int uidStart, int uidEnd, boolean forwardDns) {
            try {
                ConnectivityService.this.mNetd.clearUidRangeRoute(interfaze, uidStart, uidEnd);
                if (forwardDns) {
                    ConnectivityService.this.mNetd.clearDnsInterfaceForUidRange(uidStart, uidEnd);
                }
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.net.IConnectivityManager
    public boolean updateLockdownVpn() {
        if (Binder.getCallingUid() != 1000) {
            Slog.w(TAG, "Lockdown VPN only available to AID_SYSTEM");
            return false;
        }
        this.mLockdownEnabled = LockdownVpnTracker.isEnabled();
        if (this.mLockdownEnabled) {
            if (!this.mKeyStore.isUnlocked()) {
                Slog.w(TAG, "KeyStore locked; unable to create LockdownTracker");
                return false;
            }
            String profileName = new String(this.mKeyStore.get(Credentials.LOCKDOWN_VPN));
            VpnProfile profile = VpnProfile.decode(profileName, this.mKeyStore.get(Credentials.VPN + profileName));
            int user = UserHandle.getUserId(Binder.getCallingUid());
            synchronized (this.mVpns) {
                setLockdownTracker(new LockdownVpnTracker(this.mContext, this.mNetd, this, this.mVpns.get(user), profile));
            }
            return true;
        }
        setLockdownTracker(null);
        return true;
    }

    private void setLockdownTracker(LockdownVpnTracker tracker) {
        LockdownVpnTracker existing = this.mLockdownTracker;
        this.mLockdownTracker = null;
        if (existing != null) {
            existing.shutdown();
        }
        try {
            if (tracker != null) {
                this.mNetd.setFirewallEnabled(true);
                this.mNetd.setFirewallInterfaceRule("lo", true);
                this.mLockdownTracker = tracker;
                this.mLockdownTracker.init();
            } else {
                this.mNetd.setFirewallEnabled(false);
            }
        } catch (RemoteException e) {
        }
    }

    private void throwIfLockdownEnabled() {
        if (this.mLockdownEnabled) {
            throw new IllegalStateException("Unavailable in lockdown mode");
        }
    }

    @Override // android.net.IConnectivityManager
    public void supplyMessenger(int networkType, Messenger messenger) {
        enforceConnectivityInternalPermission();
        if (ConnectivityManager.isNetworkTypeValid(networkType) && this.mNetTrackers[networkType] != null) {
            this.mNetTrackers[networkType].supplyMessenger(messenger);
        }
    }

    @Override // android.net.IConnectivityManager
    public int findConnectionTypeForIface(String iface) {
        LinkProperties lp;
        enforceConnectivityInternalPermission();
        if (TextUtils.isEmpty(iface)) {
            return -1;
        }
        NetworkStateTracker[] arr$ = this.mNetTrackers;
        for (NetworkStateTracker tracker : arr$) {
            if (tracker != null && (lp = tracker.getLinkProperties()) != null && iface.equals(lp.getInterfaceName())) {
                return tracker.getNetworkInfo().getType();
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEnableFailFastMobileData(int enabled) {
        int tag;
        if (enabled == 1) {
            tag = this.mEnableFailFastMobileDataTag.incrementAndGet();
        } else {
            tag = this.mEnableFailFastMobileDataTag.get();
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(14, tag, enabled));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isMobileDataStateTrackerReady() {
        MobileDataStateTracker mdst = (MobileDataStateTracker) this.mNetTrackers[5];
        return mdst != null && mdst.isReady();
    }

    /* renamed from: com.android.server.ConnectivityService$5  reason: invalid class name */
    /* loaded from: ConnectivityService$5.class */
    class AnonymousClass5 extends CheckMp.CallBack {
        AnonymousClass5() {
        }

        @Override // com.android.server.ConnectivityService.CheckMp.CallBack
        void onComplete(Integer result) {
            ConnectivityService.log("CheckMp.onComplete: result=" + result);
            NetworkInfo ni = ConnectivityService.this.mNetTrackers[5].getNetworkInfo();
            switch (result.intValue()) {
                case 0:
                case 1:
                case 2:
                case 3:
                    ConnectivityService.log("CheckMp.onComplete: ignore, connected or no connection");
                    break;
                case 4:
                    ConnectivityService.log("CheckMp.onComplete: warm sim");
                    String url = ConnectivityService.this.getMobileProvisioningUrl();
                    if (TextUtils.isEmpty(url)) {
                        url = ConnectivityService.this.getMobileRedirectedProvisioningUrl();
                    }
                    if (!TextUtils.isEmpty(url)) {
                        ConnectivityService.log("CheckMp.onComplete: warm (redirected), url=" + url);
                        ConnectivityService.this.setProvNotificationVisible(true, 5, ni.getExtraInfo(), url);
                        break;
                    } else {
                        ConnectivityService.log("CheckMp.onComplete: warm (redirected), no url");
                        break;
                    }
                case 5:
                    String url2 = ConnectivityService.this.getMobileProvisioningUrl();
                    if (!TextUtils.isEmpty(url2)) {
                        ConnectivityService.log("CheckMp.onComplete: warm (no dns/tcp), url=" + url2);
                        ConnectivityService.this.setProvNotificationVisible(true, 5, ni.getExtraInfo(), url2);
                        break;
                    } else {
                        ConnectivityService.log("CheckMp.onComplete: warm (no dns/tcp), no url");
                        break;
                    }
                default:
                    ConnectivityService.loge("CheckMp.onComplete: ignore unexpected result=" + result);
                    break;
            }
            ConnectivityService.this.mIsCheckingMobileProvisioning.set(false);
        }
    }

    /* loaded from: ConnectivityService$CheckMp.class */
    static class CheckMp extends AsyncTask<Params, Void, Integer> {
        private static final String CHECKMP_TAG = "CheckMp";
        public static final int MAX_TIMEOUT_MS = 60000;
        private static final int SOCKET_TIMEOUT_MS = 5000;
        private Context mContext;
        private ConnectivityService mCs;
        private TelephonyManager mTm;
        private Params mParams;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.CheckMp.isMobileOk(com.android.server.ConnectivityService$CheckMp$Params):java.lang.Integer, file: ConnectivityService$CheckMp.class
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
        private synchronized java.lang.Integer isMobileOk(com.android.server.ConnectivityService.CheckMp.Params r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ConnectivityService.CheckMp.isMobileOk(com.android.server.ConnectivityService$CheckMp$Params):java.lang.Integer, file: ConnectivityService$CheckMp.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.ConnectivityService.CheckMp.isMobileOk(com.android.server.ConnectivityService$CheckMp$Params):java.lang.Integer");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: ConnectivityService$CheckMp$Params.class */
        public static class Params {
            private String mUrl;
            private long mTimeOutMs;
            private CallBack mCb;

            static /* synthetic */ String access$4500(Params x0) {
                return x0.mUrl;
            }

            static /* synthetic */ long access$4600(Params x0) {
                return x0.mTimeOutMs;
            }

            Params(String url, long timeOutMs, CallBack cb) {
                this.mUrl = url;
                this.mTimeOutMs = timeOutMs;
                this.mCb = cb;
            }

            public String toString() {
                return "{ url=" + this.mUrl + " mTimeOutMs=" + this.mTimeOutMs + " mCb=" + this.mCb + "}";
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: ConnectivityService$CheckMp$CallBack.class */
        public static abstract class CallBack {
            abstract void onComplete(Integer num);

            CallBack() {
            }
        }

        public CheckMp(Context context, ConnectivityService cs) {
            this.mContext = context;
            this.mCs = cs;
            this.mTm = (TelephonyManager) this.mContext.getSystemService("phone");
        }

        public String getDefaultUrl() {
            String server = Settings.Global.getString(this.mContext.getContentResolver(), Settings.Global.CAPTIVE_PORTAL_SERVER);
            if (server == null) {
                server = "clients3.google.com";
            }
            return "http://" + server + "/generate_204";
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Integer doInBackground(Params... params) {
            return isMobileOk(params[0]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Integer result) {
            log("onPostExecute: result=" + result);
            if (this.mParams != null && this.mParams.mCb != null) {
                this.mParams.mCb.onComplete(result);
            }
        }

        private String inetAddressesToString(InetAddress[] addresses) {
            StringBuffer sb = new StringBuffer();
            boolean firstTime = true;
            for (InetAddress addr : addresses) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    sb.append(Separators.COMMA);
                }
                sb.append(addr);
            }
            return sb.toString();
        }

        private void printNetworkInfo() {
            boolean hasIccCard = this.mTm.hasIccCard();
            int simState = this.mTm.getSimState();
            log("hasIccCard=" + hasIccCard + " simState=" + simState);
            NetworkInfo[] ni = this.mCs.getAllNetworkInfo();
            if (ni != null) {
                log("ni.length=" + ni.length);
                for (NetworkInfo netInfo : ni) {
                    log("netInfo=" + netInfo.toString());
                }
                return;
            }
            log("no network info ni=null");
        }

        private static void sleep(int seconds) {
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void log(String s) {
            Slog.d(ConnectivityService.TAG, "[CheckMp] " + s);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMobileProvisioningAction(String url) {
        setProvNotificationVisible(false, 5, null, null);
        NetworkInfo ni = getProvisioningNetworkInfo();
        if (ni != null && ni.isConnectedToProvisioningNetwork()) {
            log("handleMobileProvisioningAction: on provisioning network");
            MobileDataStateTracker mdst = (MobileDataStateTracker) this.mNetTrackers[0];
            mdst.enableMobileProvisioning(url);
            return;
        }
        log("handleMobileProvisioningAction: on default network");
        Intent newIntent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
        newIntent.setData(Uri.parse(url));
        newIntent.setFlags(272629760);
        try {
            this.mContext.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            loge("handleMobileProvisioningAction: startActivity failed" + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setProvNotificationVisible(boolean visible, int networkType, String extraInfo, String url) {
        CharSequence title;
        CharSequence details;
        int icon;
        log("setProvNotificationVisible: E visible=" + visible + " networkType=" + networkType + " extraInfo=" + extraInfo + " url=" + url);
        Resources r = Resources.getSystem();
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (visible) {
            Notification notification = new Notification();
            switch (networkType) {
                case 0:
                case 5:
                    title = r.getString(R.string.network_available_sign_in, 0);
                    details = this.mTelephonyManager.getNetworkOperatorName();
                    icon = 17302918;
                    Intent intent = new Intent(CONNECTED_TO_PROVISIONING_NETWORK_ACTION);
                    intent.putExtra("EXTRA_URL", url);
                    intent.setFlags(0);
                    notification.contentIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 0);
                    break;
                case 1:
                    title = r.getString(R.string.wifi_available_sign_in, 0);
                    details = r.getString(R.string.network_available_sign_in_detailed, extraInfo);
                    icon = 17302922;
                    Intent intent2 = new Intent("android.intent.action.VIEW", Uri.parse(url));
                    intent2.setFlags(272629760);
                    notification.contentIntent = PendingIntent.getActivity(this.mContext, 0, intent2, 0);
                    break;
                default:
                    title = r.getString(R.string.network_available_sign_in, 0);
                    details = r.getString(R.string.network_available_sign_in_detailed, extraInfo);
                    icon = 17302918;
                    Intent intent3 = new Intent("android.intent.action.VIEW", Uri.parse(url));
                    intent3.setFlags(272629760);
                    notification.contentIntent = PendingIntent.getActivity(this.mContext, 0, intent3, 0);
                    break;
            }
            notification.when = 0L;
            notification.icon = icon;
            notification.flags = 16;
            notification.tickerText = title;
            notification.setLatestEventInfo(this.mContext, title, details, notification.contentIntent);
            try {
                notificationManager.notify(NOTIFICATION_ID, networkType, notification);
            } catch (NullPointerException npe) {
                loge("setNotificaitionVisible: visible notificationManager npe=" + npe);
                npe.printStackTrace();
            }
        } else {
            try {
                notificationManager.cancel(NOTIFICATION_ID, networkType);
            } catch (NullPointerException npe2) {
                loge("setNotificaitionVisible: cancel notificationManager npe=" + npe2);
                npe2.printStackTrace();
            }
        }
        this.mIsNotificationVisible = visible;
    }

    @Override // android.net.IConnectivityManager
    public String getMobileRedirectedProvisioningUrl() {
        enforceConnectivityInternalPermission();
        String url = getProvisioningUrlBaseFromFile(1);
        if (TextUtils.isEmpty(url)) {
            url = this.mContext.getResources().getString(R.string.mobile_redirected_provisioning_url);
        }
        return url;
    }

    @Override // android.net.IConnectivityManager
    public String getMobileProvisioningUrl() {
        enforceConnectivityInternalPermission();
        String url = getProvisioningUrlBaseFromFile(2);
        if (TextUtils.isEmpty(url)) {
            url = this.mContext.getResources().getString(R.string.mobile_provisioning_url);
            log("getMobileProvisioningUrl: mobile_provisioining_url from resource =" + url);
        } else {
            log("getMobileProvisioningUrl: mobile_provisioning_url from File =" + url);
        }
        if (!TextUtils.isEmpty(url)) {
            String phoneNumber = this.mTelephonyManager.getLine1Number();
            if (TextUtils.isEmpty(phoneNumber)) {
                phoneNumber = "0000000000";
            }
            url = String.format(url, this.mTelephonyManager.getSimSerialNumber(), this.mTelephonyManager.getDeviceId(), phoneNumber);
        }
        return url;
    }

    @Override // android.net.IConnectivityManager
    public void setProvisioningNotificationVisible(boolean visible, int networkType, String extraInfo, String url) {
        enforceConnectivityInternalPermission();
        setProvNotificationVisible(visible, networkType, extraInfo, url);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserStart(int userId) {
        synchronized (this.mVpns) {
            if (this.mVpns.get(userId) != null) {
                loge("Starting user already has a VPN");
                return;
            }
            Vpn userVpn = new Vpn(this.mContext, this.mVpnCallback, this.mNetd, this, userId);
            this.mVpns.put(userId, userVpn);
            userVpn.startMonitoring(this.mContext, this.mTrackerHandler);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserStop(int userId) {
        synchronized (this.mVpns) {
            Vpn userVpn = this.mVpns.get(userId);
            if (userVpn == null) {
                loge("Stopping user has no VPN");
            } else {
                this.mVpns.delete(userId);
            }
        }
    }

    @Override // android.net.IConnectivityManager
    public LinkQualityInfo getLinkQualityInfo(int networkType) {
        enforceAccessPermission();
        if (ConnectivityManager.isNetworkTypeValid(networkType)) {
            return this.mNetTrackers[networkType].getLinkQualityInfo();
        }
        return null;
    }

    @Override // android.net.IConnectivityManager
    public LinkQualityInfo getActiveLinkQualityInfo() {
        enforceAccessPermission();
        if (ConnectivityManager.isNetworkTypeValid(this.mActiveDefaultNetwork)) {
            return this.mNetTrackers[this.mActiveDefaultNetwork].getLinkQualityInfo();
        }
        return null;
    }

    @Override // android.net.IConnectivityManager
    public LinkQualityInfo[] getAllLinkQualityInfo() {
        LinkQualityInfo li;
        enforceAccessPermission();
        ArrayList<LinkQualityInfo> result = Lists.newArrayList();
        NetworkStateTracker[] arr$ = this.mNetTrackers;
        for (NetworkStateTracker tracker : arr$) {
            if (tracker != null && (li = tracker.getLinkQualityInfo()) != null) {
                result.add(li);
            }
        }
        return (LinkQualityInfo[]) result.toArray(new LinkQualityInfo[result.size()]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNetworkSamplingTimeout() {
        SamplingDataTracker.SamplingSnapshot ss;
        String ifaceName;
        log("Sampling interval elapsed, updating statistics ..");
        Map<String, SamplingDataTracker.SamplingSnapshot> mapIfaceToSample = new HashMap<>();
        NetworkStateTracker[] arr$ = this.mNetTrackers;
        for (NetworkStateTracker tracker : arr$) {
            if (tracker != null && (ifaceName = tracker.getNetworkInterfaceName()) != null) {
                mapIfaceToSample.put(ifaceName, null);
            }
        }
        SamplingDataTracker.getSamplingSnapshots(mapIfaceToSample);
        NetworkStateTracker[] arr$2 = this.mNetTrackers;
        for (NetworkStateTracker tracker2 : arr$2) {
            if (tracker2 != null && (ss = mapIfaceToSample.get(tracker2.getNetworkInterfaceName())) != null) {
                tracker2.stopSampling(ss);
                tracker2.startSampling(ss);
            }
        }
        log("Done.");
        int samplingIntervalInSeconds = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.CONNECTIVITY_SAMPLING_INTERVAL_IN_SECONDS, 720);
        log("Setting timer for " + String.valueOf(samplingIntervalInSeconds) + "seconds");
        setAlarm(samplingIntervalInSeconds * 1000, this.mSampleIntervalElapsedIntent);
    }

    void setAlarm(int timeoutInMilliseconds, PendingIntent intent) {
        long wakeupTime = SystemClock.elapsedRealtime() + timeoutInMilliseconds;
        this.mAlarmManager.set(2, wakeupTime, intent);
    }
}