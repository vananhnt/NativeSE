package com.android.server.net;

import android.Manifest;
import android.app.IActivityManager;
import android.app.INotificationManager;
import android.app.IProcessObserver;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkPolicyListener;
import android.net.INetworkPolicyManager;
import android.net.INetworkStatsService;
import android.net.NetworkIdentity;
import android.net.NetworkInfo;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkQuotaInfo;
import android.net.NetworkState;
import android.net.NetworkTemplate;
import android.net.TrafficStats;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.INetworkManagementService;
import android.os.IPowerManager;
import android.os.Message;
import android.os.MessageQueue;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.text.format.Time;
import android.util.AtomicFile;
import android.util.Log;
import android.util.NtpTrustedTime;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TrustedTime;
import com.android.internal.R;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Objects;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.server.NetworkManagementService;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import com.google.android.collect.Sets;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: NetworkPolicyManagerService.class */
public class NetworkPolicyManagerService extends INetworkPolicyManager.Stub {
    private static final String TAG = "NetworkPolicy";
    private static final boolean LOGD = false;
    private static final boolean LOGV = false;
    private static final int VERSION_INIT = 1;
    private static final int VERSION_ADDED_SNOOZE = 2;
    private static final int VERSION_ADDED_RESTRICT_BACKGROUND = 3;
    private static final int VERSION_ADDED_METERED = 4;
    private static final int VERSION_SPLIT_SNOOZE = 5;
    private static final int VERSION_ADDED_TIMEZONE = 6;
    private static final int VERSION_ADDED_INFERRED = 7;
    private static final int VERSION_SWITCH_APP_ID = 8;
    private static final int VERSION_ADDED_NETWORK_ID = 9;
    private static final int VERSION_SWITCH_UID = 10;
    private static final int VERSION_LATEST = 10;
    public static final int TYPE_WARNING = 1;
    public static final int TYPE_LIMIT = 2;
    public static final int TYPE_LIMIT_SNOOZED = 3;
    private static final String TAG_POLICY_LIST = "policy-list";
    private static final String TAG_NETWORK_POLICY = "network-policy";
    private static final String TAG_UID_POLICY = "uid-policy";
    private static final String TAG_APP_POLICY = "app-policy";
    private static final String ATTR_VERSION = "version";
    private static final String ATTR_RESTRICT_BACKGROUND = "restrictBackground";
    private static final String ATTR_NETWORK_TEMPLATE = "networkTemplate";
    private static final String ATTR_SUBSCRIBER_ID = "subscriberId";
    private static final String ATTR_NETWORK_ID = "networkId";
    private static final String ATTR_CYCLE_DAY = "cycleDay";
    private static final String ATTR_CYCLE_TIMEZONE = "cycleTimezone";
    private static final String ATTR_WARNING_BYTES = "warningBytes";
    private static final String ATTR_LIMIT_BYTES = "limitBytes";
    private static final String ATTR_LAST_SNOOZE = "lastSnooze";
    private static final String ATTR_LAST_WARNING_SNOOZE = "lastWarningSnooze";
    private static final String ATTR_LAST_LIMIT_SNOOZE = "lastLimitSnooze";
    private static final String ATTR_METERED = "metered";
    private static final String ATTR_INFERRED = "inferred";
    private static final String ATTR_UID = "uid";
    private static final String ATTR_APP_ID = "appId";
    private static final String ATTR_POLICY = "policy";
    private static final String TAG_ALLOW_BACKGROUND = "NetworkPolicy:allowBackground";
    private static final String ACTION_ALLOW_BACKGROUND = "com.android.server.net.action.ALLOW_BACKGROUND";
    private static final String ACTION_SNOOZE_WARNING = "com.android.server.net.action.SNOOZE_WARNING";
    private static final long TIME_CACHE_MAX_AGE = 86400000;
    private static final int MSG_RULES_CHANGED = 1;
    private static final int MSG_METERED_IFACES_CHANGED = 2;
    private static final int MSG_FOREGROUND_ACTIVITIES_CHANGED = 3;
    private static final int MSG_PROCESS_DIED = 4;
    private static final int MSG_LIMIT_REACHED = 5;
    private static final int MSG_RESTRICT_BACKGROUND_CHANGED = 6;
    private static final int MSG_ADVISE_PERSIST_THRESHOLD = 7;
    private static final int MSG_SCREEN_ON_CHANGED = 8;
    private final Context mContext;
    private final IActivityManager mActivityManager;
    private final IPowerManager mPowerManager;
    private final INetworkStatsService mNetworkStats;
    private final INetworkManagementService mNetworkManager;
    private final TrustedTime mTime;
    private IConnectivityManager mConnManager;
    private INotificationManager mNotifManager;
    private final Object mRulesLock;
    private volatile boolean mScreenOn;
    private volatile boolean mRestrictBackground;
    private final boolean mSuppressDefaultPolicy;
    private HashMap<NetworkTemplate, NetworkPolicy> mNetworkPolicy;
    private HashMap<NetworkPolicy, String[]> mNetworkRules;
    private SparseIntArray mUidPolicy;
    private SparseIntArray mUidRules;
    private HashSet<String> mMeteredIfaces;
    private HashSet<NetworkTemplate> mOverLimitNotified;
    private HashSet<String> mActiveNotifs;
    private SparseBooleanArray mUidForeground;
    private SparseArray<SparseBooleanArray> mUidPidForeground;
    private final RemoteCallbackList<INetworkPolicyListener> mListeners;
    private final Handler mHandler;
    private final AtomicFile mPolicyFile;
    private IProcessObserver mProcessObserver;
    private BroadcastReceiver mScreenReceiver;
    private BroadcastReceiver mPackageReceiver;
    private BroadcastReceiver mUidRemovedReceiver;
    private BroadcastReceiver mUserReceiver;
    private BroadcastReceiver mStatsReceiver;
    private BroadcastReceiver mAllowReceiver;
    private BroadcastReceiver mSnoozeWarningReceiver;
    private BroadcastReceiver mWifiConfigReceiver;
    private BroadcastReceiver mWifiStateReceiver;
    private INetworkManagementEventObserver mAlertObserver;
    private BroadcastReceiver mConnReceiver;
    private Handler.Callback mHandlerCallback;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkPolicyManagerService.readPolicyLocked():void, file: NetworkPolicyManagerService.class
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
    private void readPolicyLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkPolicyManagerService.readPolicyLocked():void, file: NetworkPolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerService.readPolicyLocked():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkPolicyManagerService.snoozeLimit(android.net.NetworkTemplate):void, file: NetworkPolicyManagerService.class
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
    @Override // android.net.INetworkPolicyManager
    public void snoozeLimit(android.net.NetworkTemplate r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkPolicyManagerService.snoozeLimit(android.net.NetworkTemplate):void, file: NetworkPolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerService.snoozeLimit(android.net.NetworkTemplate):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkPolicyManagerService.getNetworkQuotaInfo(android.net.NetworkState):android.net.NetworkQuotaInfo, file: NetworkPolicyManagerService.class
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
    @Override // android.net.INetworkPolicyManager
    public android.net.NetworkQuotaInfo getNetworkQuotaInfo(android.net.NetworkState r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkPolicyManagerService.getNetworkQuotaInfo(android.net.NetworkState):android.net.NetworkQuotaInfo, file: NetworkPolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerService.getNetworkQuotaInfo(android.net.NetworkState):android.net.NetworkQuotaInfo");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkPolicyManagerService.isBandwidthControlEnabled():boolean, file: NetworkPolicyManagerService.class
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
    private boolean isBandwidthControlEnabled() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkPolicyManagerService.isBandwidthControlEnabled():boolean, file: NetworkPolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerService.isBandwidthControlEnabled():boolean");
    }

    public NetworkPolicyManagerService(Context context, IActivityManager activityManager, IPowerManager powerManager, INetworkStatsService networkStats, INetworkManagementService networkManagement) {
        this(context, activityManager, powerManager, networkStats, networkManagement, NtpTrustedTime.getInstance(context), getSystemDir(), false);
    }

    private static File getSystemDir() {
        return new File(Environment.getDataDirectory(), "system");
    }

    public NetworkPolicyManagerService(Context context, IActivityManager activityManager, IPowerManager powerManager, INetworkStatsService networkStats, INetworkManagementService networkManagement, TrustedTime time, File systemDir, boolean suppressDefaultPolicy) {
        this.mRulesLock = new Object();
        this.mNetworkPolicy = Maps.newHashMap();
        this.mNetworkRules = Maps.newHashMap();
        this.mUidPolicy = new SparseIntArray();
        this.mUidRules = new SparseIntArray();
        this.mMeteredIfaces = Sets.newHashSet();
        this.mOverLimitNotified = Sets.newHashSet();
        this.mActiveNotifs = Sets.newHashSet();
        this.mUidForeground = new SparseBooleanArray();
        this.mUidPidForeground = new SparseArray<>();
        this.mListeners = new RemoteCallbackList<>();
        this.mProcessObserver = new IProcessObserver.Stub() { // from class: com.android.server.net.NetworkPolicyManagerService.1
            @Override // android.app.IProcessObserver
            public void onForegroundActivitiesChanged(int pid, int uid, boolean foregroundActivities) {
                NetworkPolicyManagerService.this.mHandler.obtainMessage(3, pid, uid, Boolean.valueOf(foregroundActivities)).sendToTarget();
            }

            @Override // android.app.IProcessObserver
            public void onImportanceChanged(int pid, int uid, int importance) {
            }

            @Override // android.app.IProcessObserver
            public void onProcessDied(int pid, int uid) {
                NetworkPolicyManagerService.this.mHandler.obtainMessage(4, pid, uid).sendToTarget();
            }
        };
        this.mScreenReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkPolicyManagerService.this.mHandler.obtainMessage(8).sendToTarget();
            }
        };
        this.mPackageReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
                if (uid != -1 && Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                    synchronized (NetworkPolicyManagerService.this.mRulesLock) {
                        NetworkPolicyManagerService.this.updateRulesForUidLocked(uid);
                    }
                }
            }
        };
        this.mUidRemovedReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
                if (uid == -1) {
                    return;
                }
                synchronized (NetworkPolicyManagerService.this.mRulesLock) {
                    NetworkPolicyManagerService.this.mUidPolicy.delete(uid);
                    NetworkPolicyManagerService.this.updateRulesForUidLocked(uid);
                    NetworkPolicyManagerService.this.writePolicyLocked();
                }
            }
        };
        this.mUserReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                intent.getAction();
                int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1);
                if (userId == -1) {
                    return;
                }
                NetworkPolicyManagerService.this.removePoliciesForUserLocked(userId);
                synchronized (NetworkPolicyManagerService.this.mRulesLock) {
                    NetworkPolicyManagerService.this.updateRulesForRestrictBackgroundLocked();
                }
            }
        };
        this.mStatsReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.6
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkPolicyManagerService.this.maybeRefreshTrustedTime();
                synchronized (NetworkPolicyManagerService.this.mRulesLock) {
                    NetworkPolicyManagerService.this.updateNetworkEnabledLocked();
                    NetworkPolicyManagerService.this.updateNotificationsLocked();
                }
            }
        };
        this.mAllowReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkPolicyManagerService.this.setRestrictBackground(false);
            }
        };
        this.mSnoozeWarningReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.8
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkTemplate template = (NetworkTemplate) intent.getParcelableExtra(NetworkPolicyManager.EXTRA_NETWORK_TEMPLATE);
                NetworkPolicyManagerService.this.performSnooze(template, 1);
            }
        };
        this.mWifiConfigReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.9
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int reason = intent.getIntExtra(WifiManager.EXTRA_CHANGE_REASON, 0);
                if (reason == 1) {
                    WifiConfiguration config = (WifiConfiguration) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_CONFIGURATION);
                    if (config.SSID != null) {
                        NetworkTemplate template = NetworkTemplate.buildTemplateWifi(config.SSID);
                        synchronized (NetworkPolicyManagerService.this.mRulesLock) {
                            if (NetworkPolicyManagerService.this.mNetworkPolicy.containsKey(template)) {
                                NetworkPolicyManagerService.this.mNetworkPolicy.remove(template);
                                NetworkPolicyManagerService.this.writePolicyLocked();
                            }
                        }
                    }
                }
            }
        };
        this.mWifiStateReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.10
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkInfo netInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                if (netInfo.isConnected()) {
                    WifiInfo info = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    boolean meteredHint = info.getMeteredHint();
                    NetworkTemplate template = NetworkTemplate.buildTemplateWifi(info.getSSID());
                    synchronized (NetworkPolicyManagerService.this.mRulesLock) {
                        NetworkPolicy policy = (NetworkPolicy) NetworkPolicyManagerService.this.mNetworkPolicy.get(template);
                        if (policy == null && meteredHint) {
                            NetworkPolicyManagerService.this.addNetworkPolicyLocked(new NetworkPolicy(template, -1, Time.TIMEZONE_UTC, -1L, -1L, -1L, -1L, meteredHint, true));
                        } else if (policy != null && policy.inferred) {
                            policy.metered = meteredHint;
                            NetworkPolicyManagerService.this.updateNetworkRulesLocked();
                        }
                    }
                }
            }
        };
        this.mAlertObserver = new BaseNetworkObserver() { // from class: com.android.server.net.NetworkPolicyManagerService.11
            @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
            public void limitReached(String limitName, String iface) {
                NetworkPolicyManagerService.this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, NetworkPolicyManagerService.TAG);
                if (!NetworkManagementService.LIMIT_GLOBAL_ALERT.equals(limitName)) {
                    NetworkPolicyManagerService.this.mHandler.obtainMessage(5, iface).sendToTarget();
                }
            }
        };
        this.mConnReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.12
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkPolicyManagerService.this.maybeRefreshTrustedTime();
                synchronized (NetworkPolicyManagerService.this.mRulesLock) {
                    NetworkPolicyManagerService.this.ensureActiveMobilePolicyLocked();
                    NetworkPolicyManagerService.this.updateNetworkEnabledLocked();
                    NetworkPolicyManagerService.this.updateNetworkRulesLocked();
                    NetworkPolicyManagerService.this.updateNotificationsLocked();
                }
            }
        };
        this.mHandlerCallback = new Handler.Callback() { // from class: com.android.server.net.NetworkPolicyManagerService.13
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        int uid = msg.arg1;
                        int uidRules = msg.arg2;
                        int length = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i = 0; i < length; i++) {
                            INetworkPolicyListener listener = (INetworkPolicyListener) NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i);
                            if (listener != null) {
                                try {
                                    listener.onUidRulesChanged(uid, uidRules);
                                } catch (RemoteException e) {
                                }
                            }
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 2:
                        String[] meteredIfaces = (String[]) msg.obj;
                        int length2 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i2 = 0; i2 < length2; i2++) {
                            INetworkPolicyListener listener2 = (INetworkPolicyListener) NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i2);
                            if (listener2 != null) {
                                try {
                                    listener2.onMeteredIfacesChanged(meteredIfaces);
                                } catch (RemoteException e2) {
                                }
                            }
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 3:
                        int pid = msg.arg1;
                        int uid2 = msg.arg2;
                        boolean foregroundActivities = ((Boolean) msg.obj).booleanValue();
                        synchronized (NetworkPolicyManagerService.this.mRulesLock) {
                            SparseBooleanArray pidForeground = (SparseBooleanArray) NetworkPolicyManagerService.this.mUidPidForeground.get(uid2);
                            if (pidForeground == null) {
                                pidForeground = new SparseBooleanArray(2);
                                NetworkPolicyManagerService.this.mUidPidForeground.put(uid2, pidForeground);
                            }
                            pidForeground.put(pid, foregroundActivities);
                            NetworkPolicyManagerService.this.computeUidForegroundLocked(uid2);
                        }
                        return true;
                    case 4:
                        int pid2 = msg.arg1;
                        int uid3 = msg.arg2;
                        synchronized (NetworkPolicyManagerService.this.mRulesLock) {
                            SparseBooleanArray pidForeground2 = (SparseBooleanArray) NetworkPolicyManagerService.this.mUidPidForeground.get(uid3);
                            if (pidForeground2 != null) {
                                pidForeground2.delete(pid2);
                                NetworkPolicyManagerService.this.computeUidForegroundLocked(uid3);
                            }
                        }
                        return true;
                    case 5:
                        String iface = (String) msg.obj;
                        NetworkPolicyManagerService.this.maybeRefreshTrustedTime();
                        synchronized (NetworkPolicyManagerService.this.mRulesLock) {
                            if (NetworkPolicyManagerService.this.mMeteredIfaces.contains(iface)) {
                                try {
                                    NetworkPolicyManagerService.this.mNetworkStats.forceUpdate();
                                } catch (RemoteException e3) {
                                }
                                NetworkPolicyManagerService.this.updateNetworkEnabledLocked();
                                NetworkPolicyManagerService.this.updateNotificationsLocked();
                            }
                        }
                        return true;
                    case 6:
                        boolean restrictBackground = msg.arg1 != 0;
                        int length3 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i3 = 0; i3 < length3; i3++) {
                            INetworkPolicyListener listener3 = (INetworkPolicyListener) NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i3);
                            if (listener3 != null) {
                                try {
                                    listener3.onRestrictBackgroundChanged(restrictBackground);
                                } catch (RemoteException e4) {
                                }
                            }
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 7:
                        long lowestRule = ((Long) msg.obj).longValue();
                        try {
                            long persistThreshold = lowestRule / 1000;
                            NetworkPolicyManagerService.this.mNetworkStats.advisePersistThreshold(persistThreshold);
                            return true;
                        } catch (RemoteException e5) {
                            return true;
                        }
                    case 8:
                        NetworkPolicyManagerService.this.updateScreenOn();
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mContext = (Context) Preconditions.checkNotNull(context, "missing context");
        this.mActivityManager = (IActivityManager) Preconditions.checkNotNull(activityManager, "missing activityManager");
        this.mPowerManager = (IPowerManager) Preconditions.checkNotNull(powerManager, "missing powerManager");
        this.mNetworkStats = (INetworkStatsService) Preconditions.checkNotNull(networkStats, "missing networkStats");
        this.mNetworkManager = (INetworkManagementService) Preconditions.checkNotNull(networkManagement, "missing networkManagement");
        this.mTime = (TrustedTime) Preconditions.checkNotNull(time, "missing TrustedTime");
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        this.mHandler = new Handler(thread.getLooper(), this.mHandlerCallback);
        this.mSuppressDefaultPolicy = suppressDefaultPolicy;
        this.mPolicyFile = new AtomicFile(new File(systemDir, "netpolicy.xml"));
    }

    public void bindConnectivityManager(IConnectivityManager connManager) {
        this.mConnManager = (IConnectivityManager) Preconditions.checkNotNull(connManager, "missing IConnectivityManager");
    }

    public void bindNotificationManager(INotificationManager notifManager) {
        this.mNotifManager = (INotificationManager) Preconditions.checkNotNull(notifManager, "missing INotificationManager");
    }

    public void systemReady() {
        if (!isBandwidthControlEnabled()) {
            Slog.w(TAG, "bandwidth controls disabled, unable to enforce policy");
            return;
        }
        synchronized (this.mRulesLock) {
            readPolicyLocked();
            if (this.mRestrictBackground) {
                updateRulesForRestrictBackgroundLocked();
                updateNotificationsLocked();
            }
        }
        updateScreenOn();
        try {
            this.mActivityManager.registerProcessObserver(this.mProcessObserver);
            this.mNetworkManager.registerObserver(this.mAlertObserver);
        } catch (RemoteException e) {
        }
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        this.mContext.registerReceiver(this.mScreenReceiver, screenFilter);
        IntentFilter connFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION_IMMEDIATE);
        this.mContext.registerReceiver(this.mConnReceiver, connFilter, Manifest.permission.CONNECTIVITY_INTERNAL, this.mHandler);
        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        packageFilter.addDataScheme(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
        this.mContext.registerReceiver(this.mPackageReceiver, packageFilter, null, this.mHandler);
        this.mContext.registerReceiver(this.mUidRemovedReceiver, new IntentFilter(Intent.ACTION_UID_REMOVED), null, this.mHandler);
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction(Intent.ACTION_USER_ADDED);
        userFilter.addAction(Intent.ACTION_USER_REMOVED);
        this.mContext.registerReceiver(this.mUserReceiver, userFilter, null, this.mHandler);
        IntentFilter statsFilter = new IntentFilter(NetworkStatsService.ACTION_NETWORK_STATS_UPDATED);
        this.mContext.registerReceiver(this.mStatsReceiver, statsFilter, Manifest.permission.READ_NETWORK_USAGE_HISTORY, this.mHandler);
        IntentFilter allowFilter = new IntentFilter(ACTION_ALLOW_BACKGROUND);
        this.mContext.registerReceiver(this.mAllowReceiver, allowFilter, Manifest.permission.MANAGE_NETWORK_POLICY, this.mHandler);
        IntentFilter snoozeWarningFilter = new IntentFilter(ACTION_SNOOZE_WARNING);
        this.mContext.registerReceiver(this.mSnoozeWarningReceiver, snoozeWarningFilter, Manifest.permission.MANAGE_NETWORK_POLICY, this.mHandler);
        IntentFilter wifiConfigFilter = new IntentFilter(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        this.mContext.registerReceiver(this.mWifiConfigReceiver, wifiConfigFilter, Manifest.permission.CONNECTIVITY_INTERNAL, this.mHandler);
        IntentFilter wifiStateFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.mContext.registerReceiver(this.mWifiStateReceiver, wifiStateFilter, Manifest.permission.CONNECTIVITY_INTERNAL, this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotificationsLocked() {
        HashSet<String> beforeNotifs = Sets.newHashSet();
        beforeNotifs.addAll(this.mActiveNotifs);
        this.mActiveNotifs.clear();
        long currentTime = currentTimeMillis();
        for (NetworkPolicy policy : this.mNetworkPolicy.values()) {
            if (isTemplateRelevant(policy.template) && policy.hasCycle()) {
                long start = NetworkPolicyManager.computeLastCycleBoundary(currentTime, policy);
                long totalBytes = getTotalBytes(policy.template, start, currentTime);
                if (policy.isOverLimit(totalBytes)) {
                    if (policy.lastLimitSnooze >= start) {
                        enqueueNotification(policy, 3, totalBytes);
                    } else {
                        enqueueNotification(policy, 2, totalBytes);
                        notifyOverLimitLocked(policy.template);
                    }
                } else {
                    notifyUnderLimitLocked(policy.template);
                    if (policy.isOverWarning(totalBytes) && policy.lastWarningSnooze < start) {
                        enqueueNotification(policy, 1, totalBytes);
                    }
                }
            }
        }
        if (this.mRestrictBackground) {
            enqueueRestrictedNotification(TAG_ALLOW_BACKGROUND);
        }
        Iterator i$ = beforeNotifs.iterator();
        while (i$.hasNext()) {
            String tag = i$.next();
            if (!this.mActiveNotifs.contains(tag)) {
                cancelNotification(tag);
            }
        }
    }

    private boolean isTemplateRelevant(NetworkTemplate template) {
        TelephonyManager tele = TelephonyManager.from(this.mContext);
        switch (template.getMatchRule()) {
            case 1:
            case 2:
            case 3:
                if (tele.getSimState() == 5) {
                    return Objects.equal(tele.getSubscriberId(), template.getSubscriberId());
                }
                return false;
            default:
                return true;
        }
    }

    private void notifyOverLimitLocked(NetworkTemplate template) {
        if (!this.mOverLimitNotified.contains(template)) {
            this.mContext.startActivity(buildNetworkOverLimitIntent(template));
            this.mOverLimitNotified.add(template);
        }
    }

    private void notifyUnderLimitLocked(NetworkTemplate template) {
        this.mOverLimitNotified.remove(template);
    }

    private String buildNotificationTag(NetworkPolicy policy, int type) {
        return "NetworkPolicy:" + policy.template.hashCode() + Separators.COLON + type;
    }

    private void enqueueNotification(NetworkPolicy policy, int type, long totalBytes) {
        CharSequence title;
        CharSequence title2;
        String tag = buildNotificationTag(policy, type);
        Notification.Builder builder = new Notification.Builder(this.mContext);
        builder.setOnlyAlertOnce(true);
        builder.setWhen(0L);
        Resources res = this.mContext.getResources();
        switch (type) {
            case 1:
                CharSequence title3 = res.getText(R.string.data_usage_warning_title);
                CharSequence body = res.getString(R.string.data_usage_warning_body);
                builder.setSmallIcon(17301624);
                builder.setTicker(title3);
                builder.setContentTitle(title3);
                builder.setContentText(body);
                Intent snoozeIntent = buildSnoozeWarningIntent(policy.template);
                builder.setDeleteIntent(PendingIntent.getBroadcast(this.mContext, 0, snoozeIntent, 134217728));
                Intent viewIntent = buildViewDataUsageIntent(policy.template);
                builder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, viewIntent, 134217728));
                break;
            case 2:
                CharSequence body2 = res.getText(R.string.data_usage_limit_body);
                switch (policy.template.getMatchRule()) {
                    case 1:
                        title2 = res.getText(R.string.data_usage_mobile_limit_title);
                        break;
                    case 2:
                        title2 = res.getText(R.string.data_usage_3g_limit_title);
                        break;
                    case 3:
                        title2 = res.getText(R.string.data_usage_4g_limit_title);
                        break;
                    case 4:
                        title2 = res.getText(R.string.data_usage_wifi_limit_title);
                        break;
                    default:
                        title2 = null;
                        break;
                }
                builder.setOngoing(true);
                builder.setSmallIcon(R.drawable.stat_notify_disabled);
                builder.setTicker(title2);
                builder.setContentTitle(title2);
                builder.setContentText(body2);
                Intent intent = buildNetworkOverLimitIntent(policy.template);
                builder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, intent, 134217728));
                break;
            case 3:
                long overBytes = totalBytes - policy.limitBytes;
                CharSequence body3 = res.getString(R.string.data_usage_limit_snoozed_body, Formatter.formatFileSize(this.mContext, overBytes));
                switch (policy.template.getMatchRule()) {
                    case 1:
                        title = res.getText(R.string.data_usage_mobile_limit_snoozed_title);
                        break;
                    case 2:
                        title = res.getText(R.string.data_usage_3g_limit_snoozed_title);
                        break;
                    case 3:
                        title = res.getText(R.string.data_usage_4g_limit_snoozed_title);
                        break;
                    case 4:
                        title = res.getText(R.string.data_usage_wifi_limit_snoozed_title);
                        break;
                    default:
                        title = null;
                        break;
                }
                builder.setOngoing(true);
                builder.setSmallIcon(17301624);
                builder.setTicker(title);
                builder.setContentTitle(title);
                builder.setContentText(body3);
                Intent intent2 = buildViewDataUsageIntent(policy.template);
                builder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, intent2, 134217728));
                break;
        }
        try {
            String packageName = this.mContext.getPackageName();
            int[] idReceived = new int[1];
            this.mNotifManager.enqueueNotificationWithTag(packageName, packageName, tag, 0, builder.getNotification(), idReceived, 0);
            this.mActiveNotifs.add(tag);
        } catch (RemoteException e) {
        }
    }

    private void enqueueRestrictedNotification(String tag) {
        Resources res = this.mContext.getResources();
        Notification.Builder builder = new Notification.Builder(this.mContext);
        CharSequence title = res.getText(R.string.data_usage_restricted_title);
        CharSequence body = res.getString(R.string.data_usage_restricted_body);
        builder.setOnlyAlertOnce(true);
        builder.setOngoing(true);
        builder.setSmallIcon(17301624);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(body);
        Intent intent = buildAllowBackgroundDataIntent();
        builder.setContentIntent(PendingIntent.getBroadcast(this.mContext, 0, intent, 134217728));
        try {
            String packageName = this.mContext.getPackageName();
            int[] idReceived = new int[1];
            this.mNotifManager.enqueueNotificationWithTag(packageName, packageName, tag, 0, builder.getNotification(), idReceived, 0);
            this.mActiveNotifs.add(tag);
        } catch (RemoteException e) {
        }
    }

    private void cancelNotification(String tag) {
        try {
            String packageName = this.mContext.getPackageName();
            this.mNotifManager.cancelNotificationWithTag(packageName, tag, 0, 0);
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkEnabledLocked() {
        long currentTime = currentTimeMillis();
        for (NetworkPolicy policy : this.mNetworkPolicy.values()) {
            if (policy.limitBytes == -1 || !policy.hasCycle()) {
                setNetworkTemplateEnabled(policy.template, true);
            } else {
                long start = NetworkPolicyManager.computeLastCycleBoundary(currentTime, policy);
                long totalBytes = getTotalBytes(policy.template, start, currentTime);
                boolean overLimitWithoutSnooze = policy.isOverLimit(totalBytes) && policy.lastLimitSnooze < start;
                boolean networkEnabled = !overLimitWithoutSnooze;
                setNetworkTemplateEnabled(policy.template, networkEnabled);
            }
        }
    }

    private void setNetworkTemplateEnabled(NetworkTemplate template, boolean enabled) {
        TelephonyManager tele = TelephonyManager.from(this.mContext);
        switch (template.getMatchRule()) {
            case 1:
            case 2:
            case 3:
                if (tele.getSimState() == 5 && Objects.equal(tele.getSubscriberId(), template.getSubscriberId())) {
                    setPolicyDataEnable(0, enabled);
                    setPolicyDataEnable(6, enabled);
                    return;
                }
                return;
            case 4:
                setPolicyDataEnable(1, enabled);
                return;
            case 5:
                setPolicyDataEnable(9, enabled);
                return;
            default:
                throw new IllegalArgumentException("unexpected template");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkRulesLocked() {
        long start;
        long totalBytes;
        long quotaBytes;
        try {
            NetworkState[] states = this.mConnManager.getAllNetworkState();
            HashMap<NetworkIdentity, String> networks = Maps.newHashMap();
            for (NetworkState state : states) {
                if (state.networkInfo.isConnected()) {
                    String iface = state.linkProperties.getInterfaceName();
                    NetworkIdentity ident = NetworkIdentity.buildNetworkIdentity(this.mContext, state);
                    networks.put(ident, iface);
                }
            }
            this.mNetworkRules.clear();
            ArrayList<String> ifaceList = Lists.newArrayList();
            for (NetworkPolicy policy : this.mNetworkPolicy.values()) {
                ifaceList.clear();
                for (Map.Entry<NetworkIdentity, String> entry : networks.entrySet()) {
                    NetworkIdentity ident2 = entry.getKey();
                    if (policy.template.matches(ident2)) {
                        ifaceList.add(entry.getValue());
                    }
                }
                if (ifaceList.size() > 0) {
                    this.mNetworkRules.put(policy, (String[]) ifaceList.toArray(new String[ifaceList.size()]));
                }
            }
            long lowestRule = Long.MAX_VALUE;
            HashSet<String> newMeteredIfaces = Sets.newHashSet();
            long currentTime = currentTimeMillis();
            for (NetworkPolicy policy2 : this.mNetworkRules.keySet()) {
                String[] ifaces = this.mNetworkRules.get(policy2);
                if (policy2.hasCycle()) {
                    start = NetworkPolicyManager.computeLastCycleBoundary(currentTime, policy2);
                    totalBytes = getTotalBytes(policy2.template, start, currentTime);
                } else {
                    start = Long.MAX_VALUE;
                    totalBytes = 0;
                }
                boolean hasWarning = policy2.warningBytes != -1;
                boolean hasLimit = policy2.limitBytes != -1;
                if (hasLimit || policy2.metered) {
                    if (!hasLimit) {
                        quotaBytes = Long.MAX_VALUE;
                    } else if (policy2.lastLimitSnooze >= start) {
                        quotaBytes = Long.MAX_VALUE;
                    } else {
                        quotaBytes = Math.max(1L, policy2.limitBytes - totalBytes);
                    }
                    if (ifaces.length > 1) {
                        Slog.w(TAG, "shared quota unsupported; generating rule for each iface");
                    }
                    for (String iface2 : ifaces) {
                        removeInterfaceQuota(iface2);
                        setInterfaceQuota(iface2, quotaBytes);
                        newMeteredIfaces.add(iface2);
                    }
                }
                if (hasWarning && policy2.warningBytes < lowestRule) {
                    lowestRule = policy2.warningBytes;
                }
                if (hasLimit && policy2.limitBytes < lowestRule) {
                    lowestRule = policy2.limitBytes;
                }
            }
            this.mHandler.obtainMessage(7, Long.valueOf(lowestRule)).sendToTarget();
            Iterator i$ = this.mMeteredIfaces.iterator();
            while (i$.hasNext()) {
                String iface3 = i$.next();
                if (!newMeteredIfaces.contains(iface3)) {
                    removeInterfaceQuota(iface3);
                }
            }
            this.mMeteredIfaces = newMeteredIfaces;
            String[] meteredIfaces = (String[]) this.mMeteredIfaces.toArray(new String[this.mMeteredIfaces.size()]);
            this.mHandler.obtainMessage(2, meteredIfaces).sendToTarget();
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureActiveMobilePolicyLocked() {
        if (this.mSuppressDefaultPolicy) {
            return;
        }
        TelephonyManager tele = TelephonyManager.from(this.mContext);
        if (tele.getSimState() != 5) {
            return;
        }
        String subscriberId = tele.getSubscriberId();
        NetworkIdentity probeIdent = new NetworkIdentity(0, 0, subscriberId, null, false);
        boolean mobileDefined = false;
        for (NetworkPolicy policy : this.mNetworkPolicy.values()) {
            if (policy.template.matches(probeIdent)) {
                mobileDefined = true;
            }
        }
        if (!mobileDefined) {
            Slog.i(TAG, "no policy for active mobile network; generating default policy");
            long warningBytes = this.mContext.getResources().getInteger(R.integer.config_networkPolicyDefaultWarning) * TrafficStats.MB_IN_BYTES;
            Time time = new Time();
            time.setToNow();
            int cycleDay = time.monthDay;
            String cycleTimezone = time.timezone;
            NetworkTemplate template = NetworkTemplate.buildTemplateMobileAll(subscriberId);
            NetworkPolicy policy2 = new NetworkPolicy(template, cycleDay, cycleTimezone, warningBytes, -1L, -1L, -1L, true, true);
            addNetworkPolicyLocked(policy2);
        }
    }

    private void upgradeLegacyBackgroundData() {
        this.mRestrictBackground = Settings.Secure.getInt(this.mContext.getContentResolver(), Settings.Secure.BACKGROUND_DATA, 1) != 1;
        if (this.mRestrictBackground) {
            Intent broadcast = new Intent(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
            this.mContext.sendBroadcastAsUser(broadcast, UserHandle.ALL);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writePolicyLocked() {
        FileOutputStream fos = null;
        try {
            fos = this.mPolicyFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, "utf-8");
            out.startDocument(null, true);
            out.startTag(null, TAG_POLICY_LIST);
            XmlUtils.writeIntAttribute(out, "version", 10);
            XmlUtils.writeBooleanAttribute(out, ATTR_RESTRICT_BACKGROUND, this.mRestrictBackground);
            for (NetworkPolicy policy : this.mNetworkPolicy.values()) {
                NetworkTemplate template = policy.template;
                out.startTag(null, TAG_NETWORK_POLICY);
                XmlUtils.writeIntAttribute(out, ATTR_NETWORK_TEMPLATE, template.getMatchRule());
                String subscriberId = template.getSubscriberId();
                if (subscriberId != null) {
                    out.attribute(null, ATTR_SUBSCRIBER_ID, subscriberId);
                }
                String networkId = template.getNetworkId();
                if (networkId != null) {
                    out.attribute(null, ATTR_NETWORK_ID, networkId);
                }
                XmlUtils.writeIntAttribute(out, ATTR_CYCLE_DAY, policy.cycleDay);
                out.attribute(null, ATTR_CYCLE_TIMEZONE, policy.cycleTimezone);
                XmlUtils.writeLongAttribute(out, ATTR_WARNING_BYTES, policy.warningBytes);
                XmlUtils.writeLongAttribute(out, ATTR_LIMIT_BYTES, policy.limitBytes);
                XmlUtils.writeLongAttribute(out, ATTR_LAST_WARNING_SNOOZE, policy.lastWarningSnooze);
                XmlUtils.writeLongAttribute(out, ATTR_LAST_LIMIT_SNOOZE, policy.lastLimitSnooze);
                XmlUtils.writeBooleanAttribute(out, ATTR_METERED, policy.metered);
                XmlUtils.writeBooleanAttribute(out, ATTR_INFERRED, policy.inferred);
                out.endTag(null, TAG_NETWORK_POLICY);
            }
            for (int i = 0; i < this.mUidPolicy.size(); i++) {
                int uid = this.mUidPolicy.keyAt(i);
                int policy2 = this.mUidPolicy.valueAt(i);
                if (policy2 != 0) {
                    out.startTag(null, TAG_UID_POLICY);
                    XmlUtils.writeIntAttribute(out, "uid", uid);
                    XmlUtils.writeIntAttribute(out, ATTR_POLICY, policy2);
                    out.endTag(null, TAG_UID_POLICY);
                }
            }
            out.endTag(null, TAG_POLICY_LIST);
            out.endDocument();
            this.mPolicyFile.finishWrite(fos);
        } catch (IOException e) {
            if (fos != null) {
                this.mPolicyFile.failWrite(fos);
            }
        }
    }

    @Override // android.net.INetworkPolicyManager
    public void setUidPolicy(int uid, int policy) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_NETWORK_POLICY, TAG);
        if (!UserHandle.isApp(uid)) {
            throw new IllegalArgumentException("cannot apply policy to UID " + uid);
        }
        setUidPolicyUnchecked(uid, policy, true);
    }

    private void setUidPolicyUnchecked(int uid, int policy, boolean persist) {
        synchronized (this.mRulesLock) {
            getUidPolicy(uid);
            this.mUidPolicy.put(uid, policy);
            updateRulesForUidLocked(uid);
            if (persist) {
                writePolicyLocked();
            }
        }
    }

    @Override // android.net.INetworkPolicyManager
    public int getUidPolicy(int uid) {
        int i;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_NETWORK_POLICY, TAG);
        synchronized (this.mRulesLock) {
            i = this.mUidPolicy.get(uid, 0);
        }
        return i;
    }

    @Override // android.net.INetworkPolicyManager
    public int[] getUidsWithPolicy(int policy) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_NETWORK_POLICY, TAG);
        int[] uids = new int[0];
        synchronized (this.mRulesLock) {
            for (int i = 0; i < this.mUidPolicy.size(); i++) {
                int uid = this.mUidPolicy.keyAt(i);
                int uidPolicy = this.mUidPolicy.valueAt(i);
                if (uidPolicy == policy) {
                    uids = ArrayUtils.appendInt(uids, uid);
                }
            }
        }
        return uids;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removePoliciesForUserLocked(int userId) {
        int[] uids = new int[0];
        for (int i = 0; i < this.mUidPolicy.size(); i++) {
            int uid = this.mUidPolicy.keyAt(i);
            if (UserHandle.getUserId(uid) == userId) {
                uids = ArrayUtils.appendInt(uids, uid);
            }
        }
        if (uids.length > 0) {
            int[] arr$ = uids;
            for (int uid2 : arr$) {
                this.mUidPolicy.delete(uid2);
                updateRulesForUidLocked(uid2);
            }
            writePolicyLocked();
        }
    }

    @Override // android.net.INetworkPolicyManager
    public void registerListener(INetworkPolicyListener listener) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        this.mListeners.register(listener);
    }

    @Override // android.net.INetworkPolicyManager
    public void unregisterListener(INetworkPolicyListener listener) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        this.mListeners.unregister(listener);
    }

    @Override // android.net.INetworkPolicyManager
    public void setNetworkPolicies(NetworkPolicy[] policies) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_NETWORK_POLICY, TAG);
        maybeRefreshTrustedTime();
        synchronized (this.mRulesLock) {
            this.mNetworkPolicy.clear();
            for (NetworkPolicy policy : policies) {
                this.mNetworkPolicy.put(policy.template, policy);
            }
            updateNetworkEnabledLocked();
            updateNetworkRulesLocked();
            updateNotificationsLocked();
            writePolicyLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addNetworkPolicyLocked(NetworkPolicy policy) {
        this.mNetworkPolicy.put(policy.template, policy);
        updateNetworkEnabledLocked();
        updateNetworkRulesLocked();
        updateNotificationsLocked();
        writePolicyLocked();
    }

    @Override // android.net.INetworkPolicyManager
    public NetworkPolicy[] getNetworkPolicies() {
        NetworkPolicy[] networkPolicyArr;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_NETWORK_POLICY, TAG);
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE, TAG);
        synchronized (this.mRulesLock) {
            networkPolicyArr = (NetworkPolicy[]) this.mNetworkPolicy.values().toArray(new NetworkPolicy[this.mNetworkPolicy.size()]);
        }
        return networkPolicyArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performSnooze(NetworkTemplate template, int type) {
        maybeRefreshTrustedTime();
        long currentTime = currentTimeMillis();
        synchronized (this.mRulesLock) {
            NetworkPolicy policy = this.mNetworkPolicy.get(template);
            if (policy == null) {
                throw new IllegalArgumentException("unable to find policy for " + template);
            }
            switch (type) {
                case 1:
                    policy.lastWarningSnooze = currentTime;
                    break;
                case 2:
                    policy.lastLimitSnooze = currentTime;
                    break;
                default:
                    throw new IllegalArgumentException("unexpected type");
            }
            updateNetworkEnabledLocked();
            updateNetworkRulesLocked();
            updateNotificationsLocked();
            writePolicyLocked();
        }
    }

    @Override // android.net.INetworkPolicyManager
    public void setRestrictBackground(boolean restrictBackground) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_NETWORK_POLICY, TAG);
        maybeRefreshTrustedTime();
        synchronized (this.mRulesLock) {
            this.mRestrictBackground = restrictBackground;
            updateRulesForRestrictBackgroundLocked();
            updateNotificationsLocked();
            writePolicyLocked();
        }
        this.mHandler.obtainMessage(6, restrictBackground ? 1 : 0, 0).sendToTarget();
    }

    @Override // android.net.INetworkPolicyManager
    public boolean getRestrictBackground() {
        boolean z;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_NETWORK_POLICY, TAG);
        synchronized (this.mRulesLock) {
            z = this.mRestrictBackground;
        }
        return z;
    }

    private NetworkPolicy findPolicyForNetworkLocked(NetworkIdentity ident) {
        for (NetworkPolicy policy : this.mNetworkPolicy.values()) {
            if (policy.template.matches(ident)) {
                return policy;
            }
        }
        return null;
    }

    private NetworkQuotaInfo getNetworkQuotaInfoUnchecked(NetworkState state) {
        NetworkPolicy policy;
        NetworkIdentity ident = NetworkIdentity.buildNetworkIdentity(this.mContext, state);
        synchronized (this.mRulesLock) {
            policy = findPolicyForNetworkLocked(ident);
        }
        if (policy == null || !policy.hasCycle()) {
            return null;
        }
        long currentTime = currentTimeMillis();
        long start = NetworkPolicyManager.computeLastCycleBoundary(currentTime, policy);
        long totalBytes = getTotalBytes(policy.template, start, currentTime);
        long softLimitBytes = policy.warningBytes != -1 ? policy.warningBytes : -1L;
        long hardLimitBytes = policy.limitBytes != -1 ? policy.limitBytes : -1L;
        return new NetworkQuotaInfo(totalBytes, softLimitBytes, hardLimitBytes);
    }

    @Override // android.net.INetworkPolicyManager
    public boolean isNetworkMetered(NetworkState state) {
        NetworkPolicy policy;
        NetworkIdentity ident = NetworkIdentity.buildNetworkIdentity(this.mContext, state);
        if (ident.getRoaming()) {
            return true;
        }
        synchronized (this.mRulesLock) {
            policy = findPolicyForNetworkLocked(ident);
        }
        if (policy != null) {
            return policy.metered;
        }
        int type = state.networkInfo.getType();
        if (ConnectivityManager.isNetworkTypeMobile(type) || type == 6) {
            return true;
        }
        return false;
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DUMP, TAG);
        IndentingPrintWriter fout = new IndentingPrintWriter(writer, "  ");
        HashSet<String> argSet = new HashSet<>();
        for (String arg : args) {
            argSet.add(arg);
        }
        synchronized (this.mRulesLock) {
            if (argSet.contains("--unsnooze")) {
                for (NetworkPolicy policy : this.mNetworkPolicy.values()) {
                    policy.clearSnooze();
                }
                updateNetworkEnabledLocked();
                updateNetworkRulesLocked();
                updateNotificationsLocked();
                writePolicyLocked();
                fout.println("Cleared snooze timestamps");
                return;
            }
            fout.print("Restrict background: ");
            fout.println(this.mRestrictBackground);
            fout.println("Network policies:");
            fout.increaseIndent();
            for (NetworkPolicy policy2 : this.mNetworkPolicy.values()) {
                fout.println(policy2.toString());
            }
            fout.decreaseIndent();
            fout.println("Policy for UIDs:");
            fout.increaseIndent();
            int size = this.mUidPolicy.size();
            for (int i = 0; i < size; i++) {
                int uid = this.mUidPolicy.keyAt(i);
                int policy3 = this.mUidPolicy.valueAt(i);
                fout.print("UID=");
                fout.print(uid);
                fout.print(" policy=");
                NetworkPolicyManager.dumpPolicy(fout, policy3);
                fout.println();
            }
            fout.decreaseIndent();
            SparseBooleanArray knownUids = new SparseBooleanArray();
            collectKeys(this.mUidForeground, knownUids);
            collectKeys(this.mUidRules, knownUids);
            fout.println("Status for known UIDs:");
            fout.increaseIndent();
            int size2 = knownUids.size();
            for (int i2 = 0; i2 < size2; i2++) {
                int uid2 = knownUids.keyAt(i2);
                fout.print("UID=");
                fout.print(uid2);
                fout.print(" foreground=");
                int foregroundIndex = this.mUidPidForeground.indexOfKey(uid2);
                if (foregroundIndex < 0) {
                    fout.print(IccCardConstants.INTENT_VALUE_ICC_UNKNOWN);
                } else {
                    dumpSparseBooleanArray(fout, this.mUidPidForeground.valueAt(foregroundIndex));
                }
                fout.print(" rules=");
                int rulesIndex = this.mUidRules.indexOfKey(uid2);
                if (rulesIndex < 0) {
                    fout.print(IccCardConstants.INTENT_VALUE_ICC_UNKNOWN);
                } else {
                    NetworkPolicyManager.dumpRules(fout, this.mUidRules.valueAt(rulesIndex));
                }
                fout.println();
            }
            fout.decreaseIndent();
        }
    }

    @Override // android.net.INetworkPolicyManager
    public boolean isUidForeground(int uid) {
        boolean z;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_NETWORK_POLICY, TAG);
        synchronized (this.mRulesLock) {
            z = this.mUidForeground.get(uid, false) && this.mScreenOn;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void computeUidForegroundLocked(int uid) {
        SparseBooleanArray pidForeground = this.mUidPidForeground.get(uid);
        boolean uidForeground = false;
        int size = pidForeground.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            } else if (!pidForeground.valueAt(i)) {
                i++;
            } else {
                uidForeground = true;
                break;
            }
        }
        boolean oldUidForeground = this.mUidForeground.get(uid, false);
        if (oldUidForeground != uidForeground) {
            this.mUidForeground.put(uid, uidForeground);
            updateRulesForUidLocked(uid);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateScreenOn() {
        synchronized (this.mRulesLock) {
            try {
                this.mScreenOn = this.mPowerManager.isScreenOn();
            } catch (RemoteException e) {
            }
            updateRulesForScreenLocked();
        }
    }

    private void updateRulesForScreenLocked() {
        int size = this.mUidForeground.size();
        for (int i = 0; i < size; i++) {
            if (this.mUidForeground.valueAt(i)) {
                int uid = this.mUidForeground.keyAt(i);
                updateRulesForUidLocked(uid);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRulesForRestrictBackgroundLocked() {
        PackageManager pm = this.mContext.getPackageManager();
        UserManager um = (UserManager) this.mContext.getSystemService("user");
        List<UserInfo> users = um.getUsers();
        List<ApplicationInfo> apps = pm.getInstalledApplications(8704);
        for (UserInfo user : users) {
            for (ApplicationInfo app : apps) {
                int uid = UserHandle.getUid(user.id, app.uid);
                updateRulesForUidLocked(uid);
            }
        }
        updateRulesForUidLocked(1013);
        updateRulesForUidLocked(1019);
    }

    private static boolean isUidValidForRules(int uid) {
        if (uid == 1013 || uid == 1019 || UserHandle.isApp(uid)) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRulesForUidLocked(int uid) {
        if (isUidValidForRules(uid)) {
            int uidPolicy = getUidPolicy(uid);
            boolean uidForeground = isUidForeground(uid);
            int uidRules = 0;
            if (!uidForeground && (uidPolicy & 1) != 0) {
                uidRules = 1;
            }
            if (!uidForeground && this.mRestrictBackground) {
                uidRules = 1;
            }
            if (uidRules == 0) {
                this.mUidRules.delete(uid);
            } else {
                this.mUidRules.put(uid, uidRules);
            }
            boolean rejectMetered = (uidRules & 1) != 0;
            setUidNetworkRules(uid, rejectMetered);
            this.mHandler.obtainMessage(1, uid, uidRules).sendToTarget();
            try {
                this.mNetworkStats.setUidForeground(uid, uidForeground);
            } catch (RemoteException e) {
            }
        }
    }

    private void setInterfaceQuota(String iface, long quotaBytes) {
        try {
            this.mNetworkManager.setInterfaceQuota(iface, quotaBytes);
        } catch (RemoteException e) {
        } catch (IllegalStateException e2) {
            Log.wtf(TAG, "problem setting interface quota", e2);
        }
    }

    private void removeInterfaceQuota(String iface) {
        try {
            this.mNetworkManager.removeInterfaceQuota(iface);
        } catch (RemoteException e) {
        } catch (IllegalStateException e2) {
            Log.wtf(TAG, "problem removing interface quota", e2);
        }
    }

    private void setUidNetworkRules(int uid, boolean rejectOnQuotaInterfaces) {
        try {
            this.mNetworkManager.setUidNetworkRules(uid, rejectOnQuotaInterfaces);
        } catch (RemoteException e) {
        } catch (IllegalStateException e2) {
            Log.wtf(TAG, "problem setting uid rules", e2);
        }
    }

    private void setPolicyDataEnable(int networkType, boolean enabled) {
        try {
            this.mConnManager.setPolicyDataEnable(networkType, enabled);
        } catch (RemoteException e) {
        }
    }

    private long getTotalBytes(NetworkTemplate template, long start, long end) {
        try {
            return this.mNetworkStats.getNetworkTotalBytes(template, start, end);
        } catch (RemoteException e) {
            return 0L;
        } catch (RuntimeException e2) {
            Slog.w(TAG, "problem reading network stats: " + e2);
            return 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeRefreshTrustedTime() {
        if (this.mTime.getCacheAge() > 86400000) {
            this.mTime.forceRefresh();
        }
    }

    private long currentTimeMillis() {
        return this.mTime.hasCache() ? this.mTime.currentTimeMillis() : System.currentTimeMillis();
    }

    private static Intent buildAllowBackgroundDataIntent() {
        return new Intent(ACTION_ALLOW_BACKGROUND);
    }

    private static Intent buildSnoozeWarningIntent(NetworkTemplate template) {
        Intent intent = new Intent(ACTION_SNOOZE_WARNING);
        intent.putExtra(NetworkPolicyManager.EXTRA_NETWORK_TEMPLATE, template);
        return intent;
    }

    private static Intent buildNetworkOverLimitIntent(NetworkTemplate template) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.systemui", "com.android.systemui.net.NetworkOverLimitActivity"));
        intent.addFlags(268435456);
        intent.putExtra(NetworkPolicyManager.EXTRA_NETWORK_TEMPLATE, template);
        return intent;
    }

    private static Intent buildViewDataUsageIntent(NetworkTemplate template) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
        intent.addFlags(268435456);
        intent.putExtra(NetworkPolicyManager.EXTRA_NETWORK_TEMPLATE, template);
        return intent;
    }

    public void addIdleHandler(MessageQueue.IdleHandler handler) {
        this.mHandler.getLooper().getQueue().addIdleHandler(handler);
    }

    private static void collectKeys(SparseIntArray source, SparseBooleanArray target) {
        int size = source.size();
        for (int i = 0; i < size; i++) {
            target.put(source.keyAt(i), true);
        }
    }

    private static void collectKeys(SparseBooleanArray source, SparseBooleanArray target) {
        int size = source.size();
        for (int i = 0; i < size; i++) {
            target.put(source.keyAt(i), true);
        }
    }

    private static void dumpSparseBooleanArray(PrintWriter fout, SparseBooleanArray value) {
        fout.print("[");
        int size = value.size();
        for (int i = 0; i < size; i++) {
            fout.print(value.keyAt(i) + Separators.EQUALS + value.valueAt(i));
            if (i < size - 1) {
                fout.print(Separators.COMMA);
            }
        }
        fout.print("]");
    }
}