package com.android.server.net;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IAlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.LinkProperties;
import android.net.NetworkIdentity;
import android.net.NetworkState;
import android.net.NetworkStats;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.os.Binder;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.MathUtils;
import android.util.NtpTrustedTime;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.TrustedTime;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FileRotator;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.server.EventLogTags;
import com.android.server.NetworkManagementService;
import com.android.server.NetworkManagementSocketTagger;
import com.google.android.collect.Maps;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/* loaded from: NetworkStatsService.class */
public class NetworkStatsService extends INetworkStatsService.Stub {
    private static final String TAG = "NetworkStats";
    private static final boolean LOGV = false;
    private static final int MSG_PERFORM_POLL = 1;
    private static final int MSG_UPDATE_IFACES = 2;
    private static final int MSG_REGISTER_GLOBAL_ALERT = 3;
    private static final int FLAG_PERSIST_NETWORK = 1;
    private static final int FLAG_PERSIST_UID = 2;
    private static final int FLAG_PERSIST_ALL = 3;
    private static final int FLAG_PERSIST_FORCE = 256;
    private static final String TAG_NETSTATS_ERROR = "netstats_error";
    private final Context mContext;
    private final INetworkManagementService mNetworkManager;
    private final AlarmManager mAlarmManager;
    private final TrustedTime mTime;
    private final TelephonyManager mTeleManager;
    private final NetworkStatsSettings mSettings;
    private final File mSystemDir;
    private final File mBaseDir;
    private final PowerManager.WakeLock mWakeLock;
    private IConnectivityManager mConnManager;
    public static final String ACTION_NETWORK_STATS_POLL = "com.android.server.action.NETWORK_STATS_POLL";
    public static final String ACTION_NETWORK_STATS_UPDATED = "com.android.server.action.NETWORK_STATS_UPDATED";
    private PendingIntent mPollIntent;
    private static final String PREFIX_DEV = "dev";
    private static final String PREFIX_XT = "xt";
    private static final String PREFIX_UID = "uid";
    private static final String PREFIX_UID_TAG = "uid_tag";
    private final Object mStatsLock;
    private HashMap<String, NetworkIdentitySet> mActiveIfaces;
    private String mActiveIface;
    private String[] mMobileIfaces;
    private final DropBoxNonMonotonicObserver mNonMonotonicObserver;
    private NetworkStatsRecorder mDevRecorder;
    private NetworkStatsRecorder mXtRecorder;
    private NetworkStatsRecorder mUidRecorder;
    private NetworkStatsRecorder mUidTagRecorder;
    private NetworkStatsCollection mDevStatsCached;
    private NetworkStatsCollection mXtStatsCached;
    private SparseIntArray mActiveUidCounterSet;
    private NetworkStats mUidOperations;
    private final Handler mHandler;
    private boolean mSystemReady;
    private long mPersistThreshold;
    private long mGlobalAlertBytes;
    private BroadcastReceiver mConnReceiver;
    private BroadcastReceiver mTetherReceiver;
    private BroadcastReceiver mPollReceiver;
    private BroadcastReceiver mRemovedReceiver;
    private BroadcastReceiver mUserReceiver;
    private BroadcastReceiver mShutdownReceiver;
    private INetworkManagementEventObserver mAlertObserver;
    private int mLastPhoneState;
    private int mLastPhoneNetworkType;
    private PhoneStateListener mPhoneListener;
    private Handler.Callback mHandlerCallback;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.getDataLayerSnapshotForUid(int):android.net.NetworkStats, file: NetworkStatsService.class
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
    @Override // android.net.INetworkStatsService
    public android.net.NetworkStats getDataLayerSnapshotForUid(int r1) throws android.os.RemoteException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.getDataLayerSnapshotForUid(int):android.net.NetworkStats, file: NetworkStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsService.getDataLayerSnapshotForUid(int):android.net.NetworkStats");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.forceUpdate():void, file: NetworkStatsService.class
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
    @Override // android.net.INetworkStatsService
    public void forceUpdate() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.forceUpdate():void, file: NetworkStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsService.forceUpdate():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.updateIfaces():void, file: NetworkStatsService.class
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
    public void updateIfaces() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.updateIfaces():void, file: NetworkStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsService.updateIfaces():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.performPoll(int):void, file: NetworkStatsService.class
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
    public void performPoll(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.performPoll(int):void, file: NetworkStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsService.performPoll(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.isBandwidthControlEnabled():boolean, file: NetworkStatsService.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.isBandwidthControlEnabled():boolean, file: NetworkStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsService.isBandwidthControlEnabled():boolean");
    }

    static /* synthetic */ Object access$100(NetworkStatsService x0) {
        return x0.mStatsLock;
    }

    static /* synthetic */ PowerManager.WakeLock access$900(NetworkStatsService x0) {
        return x0.mWakeLock;
    }

    static /* synthetic */ void access$1000(NetworkStatsService x0, int[] x1) {
        x0.removeUidsLocked(x1);
    }

    static /* synthetic */ void access$1100(NetworkStatsService x0, int x1) {
        x0.removeUserLocked(x1);
    }

    /* loaded from: NetworkStatsService$NetworkStatsSettings.class */
    public interface NetworkStatsSettings {
        long getPollInterval();

        long getTimeCacheMaxAge();

        boolean getSampleEnabled();

        boolean getReportXtOverDev();

        Config getDevConfig();

        Config getXtConfig();

        Config getUidConfig();

        Config getUidTagConfig();

        long getGlobalAlertBytes(long j);

        long getDevPersistBytes(long j);

        long getXtPersistBytes(long j);

        long getUidPersistBytes(long j);

        long getUidTagPersistBytes(long j);

        /* loaded from: NetworkStatsService$NetworkStatsSettings$Config.class */
        public static class Config {
            public final long bucketDuration;
            public final long rotateAgeMillis;
            public final long deleteAgeMillis;

            public Config(long bucketDuration, long rotateAgeMillis, long deleteAgeMillis) {
                this.bucketDuration = bucketDuration;
                this.rotateAgeMillis = rotateAgeMillis;
                this.deleteAgeMillis = deleteAgeMillis;
            }
        }
    }

    public NetworkStatsService(Context context, INetworkManagementService networkManager, IAlarmManager alarmManager) {
        this(context, networkManager, alarmManager, NtpTrustedTime.getInstance(context), getDefaultSystemDir(), new DefaultNetworkStatsSettings(context));
    }

    private static File getDefaultSystemDir() {
        return new File(Environment.getDataDirectory(), "system");
    }

    public NetworkStatsService(Context context, INetworkManagementService networkManager, IAlarmManager alarmManager, TrustedTime time, File systemDir, NetworkStatsSettings settings) {
        this.mStatsLock = new Object();
        this.mActiveIfaces = Maps.newHashMap();
        this.mMobileIfaces = new String[0];
        this.mNonMonotonicObserver = new DropBoxNonMonotonicObserver();
        this.mActiveUidCounterSet = new SparseIntArray();
        this.mUidOperations = new NetworkStats(0L, 10);
        this.mPersistThreshold = 2097152L;
        this.mConnReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkStatsService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkStatsService.this.updateIfaces();
            }
        };
        this.mTetherReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkStatsService.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkStatsService.this.performPoll(1);
            }
        };
        this.mPollReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkStatsService.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkStatsService.this.performPoll(3);
                NetworkStatsService.this.registerGlobalAlert();
            }
        };
        this.mRemovedReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkStatsService.5
            /*  JADX ERROR: Method load error
                jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.5.onReceive(android.content.Context, android.content.Intent):void, file: NetworkStatsService$5.class
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
            @Override // android.content.BroadcastReceiver
            public void onReceive(android.content.Context r1, android.content.Intent r2) {
                /*
                // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.5.onReceive(android.content.Context, android.content.Intent):void, file: NetworkStatsService$5.class
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsService.AnonymousClass5.onReceive(android.content.Context, android.content.Intent):void");
            }
        };
        this.mUserReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkStatsService.6
            /*  JADX ERROR: Method load error
                jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.6.onReceive(android.content.Context, android.content.Intent):void, file: NetworkStatsService$6.class
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
            @Override // android.content.BroadcastReceiver
            public void onReceive(android.content.Context r1, android.content.Intent r2) {
                /*
                // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsService.6.onReceive(android.content.Context, android.content.Intent):void, file: NetworkStatsService$6.class
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsService.AnonymousClass6.onReceive(android.content.Context, android.content.Intent):void");
            }
        };
        this.mShutdownReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkStatsService.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                synchronized (NetworkStatsService.this.mStatsLock) {
                    NetworkStatsService.this.shutdownLocked();
                }
            }
        };
        this.mAlertObserver = new BaseNetworkObserver() { // from class: com.android.server.net.NetworkStatsService.8
            @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
            public void limitReached(String limitName, String iface) {
                NetworkStatsService.this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, NetworkStatsService.TAG);
                if (NetworkManagementService.LIMIT_GLOBAL_ALERT.equals(limitName)) {
                    NetworkStatsService.this.mHandler.obtainMessage(1, 1, 0).sendToTarget();
                    NetworkStatsService.this.mHandler.obtainMessage(3).sendToTarget();
                }
            }
        };
        this.mLastPhoneState = -1;
        this.mLastPhoneNetworkType = 0;
        this.mPhoneListener = new PhoneStateListener() { // from class: com.android.server.net.NetworkStatsService.9
            @Override // android.telephony.PhoneStateListener
            public void onDataConnectionStateChanged(int state, int networkType) {
                boolean stateChanged = state != NetworkStatsService.this.mLastPhoneState;
                boolean networkTypeChanged = networkType != NetworkStatsService.this.mLastPhoneNetworkType;
                if (networkTypeChanged && !stateChanged) {
                    NetworkStatsService.this.mHandler.sendMessageDelayed(NetworkStatsService.this.mHandler.obtainMessage(2), 1000L);
                }
                NetworkStatsService.this.mLastPhoneState = state;
                NetworkStatsService.this.mLastPhoneNetworkType = networkType;
            }
        };
        this.mHandlerCallback = new Handler.Callback() { // from class: com.android.server.net.NetworkStatsService.10
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        int flags = msg.arg1;
                        NetworkStatsService.this.performPoll(flags);
                        return true;
                    case 2:
                        NetworkStatsService.this.updateIfaces();
                        return true;
                    case 3:
                        NetworkStatsService.this.registerGlobalAlert();
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mContext = (Context) Preconditions.checkNotNull(context, "missing Context");
        this.mNetworkManager = (INetworkManagementService) Preconditions.checkNotNull(networkManager, "missing INetworkManagementService");
        this.mTime = (TrustedTime) Preconditions.checkNotNull(time, "missing TrustedTime");
        this.mTeleManager = (TelephonyManager) Preconditions.checkNotNull(TelephonyManager.getDefault(), "missing TelephonyManager");
        this.mSettings = (NetworkStatsSettings) Preconditions.checkNotNull(settings, "missing NetworkStatsSettings");
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = powerManager.newWakeLock(1, TAG);
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        this.mHandler = new Handler(thread.getLooper(), this.mHandlerCallback);
        this.mSystemDir = (File) Preconditions.checkNotNull(systemDir);
        this.mBaseDir = new File(systemDir, Context.NETWORK_STATS_SERVICE);
        this.mBaseDir.mkdirs();
    }

    public void bindConnectivityManager(IConnectivityManager connManager) {
        this.mConnManager = (IConnectivityManager) Preconditions.checkNotNull(connManager, "missing IConnectivityManager");
    }

    public void systemReady() {
        this.mSystemReady = true;
        if (!isBandwidthControlEnabled()) {
            Slog.w(TAG, "bandwidth controls disabled, unable to track stats");
            return;
        }
        this.mDevRecorder = buildRecorder(PREFIX_DEV, this.mSettings.getDevConfig(), false);
        this.mXtRecorder = buildRecorder(PREFIX_XT, this.mSettings.getXtConfig(), false);
        this.mUidRecorder = buildRecorder("uid", this.mSettings.getUidConfig(), false);
        this.mUidTagRecorder = buildRecorder(PREFIX_UID_TAG, this.mSettings.getUidTagConfig(), true);
        updatePersistThresholds();
        synchronized (this.mStatsLock) {
            maybeUpgradeLegacyStatsLocked();
            this.mDevStatsCached = this.mDevRecorder.getOrLoadCompleteLocked();
            this.mXtStatsCached = this.mXtRecorder.getOrLoadCompleteLocked();
            bootstrapStatsLocked();
        }
        IntentFilter connFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION_IMMEDIATE);
        this.mContext.registerReceiver(this.mConnReceiver, connFilter, Manifest.permission.CONNECTIVITY_INTERNAL, this.mHandler);
        IntentFilter tetherFilter = new IntentFilter(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);
        this.mContext.registerReceiver(this.mTetherReceiver, tetherFilter, Manifest.permission.CONNECTIVITY_INTERNAL, this.mHandler);
        IntentFilter pollFilter = new IntentFilter(ACTION_NETWORK_STATS_POLL);
        this.mContext.registerReceiver(this.mPollReceiver, pollFilter, Manifest.permission.READ_NETWORK_USAGE_HISTORY, this.mHandler);
        IntentFilter removedFilter = new IntentFilter(Intent.ACTION_UID_REMOVED);
        this.mContext.registerReceiver(this.mRemovedReceiver, removedFilter, null, this.mHandler);
        IntentFilter userFilter = new IntentFilter(Intent.ACTION_USER_REMOVED);
        this.mContext.registerReceiver(this.mUserReceiver, userFilter, null, this.mHandler);
        IntentFilter shutdownFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        this.mContext.registerReceiver(this.mShutdownReceiver, shutdownFilter);
        try {
            this.mNetworkManager.registerObserver(this.mAlertObserver);
        } catch (RemoteException e) {
        }
        registerPollAlarmLocked();
        registerGlobalAlert();
    }

    private NetworkStatsRecorder buildRecorder(String prefix, NetworkStatsSettings.Config config, boolean includeTags) {
        DropBoxManager dropBox = (DropBoxManager) this.mContext.getSystemService(Context.DROPBOX_SERVICE);
        return new NetworkStatsRecorder(new FileRotator(this.mBaseDir, prefix, config.rotateAgeMillis, config.deleteAgeMillis), this.mNonMonotonicObserver, dropBox, prefix, config.bucketDuration, includeTags);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void shutdownLocked() {
        this.mContext.unregisterReceiver(this.mConnReceiver);
        this.mContext.unregisterReceiver(this.mTetherReceiver);
        this.mContext.unregisterReceiver(this.mPollReceiver);
        this.mContext.unregisterReceiver(this.mRemovedReceiver);
        this.mContext.unregisterReceiver(this.mShutdownReceiver);
        long currentTime = this.mTime.hasCache() ? this.mTime.currentTimeMillis() : System.currentTimeMillis();
        this.mDevRecorder.forcePersistLocked(currentTime);
        this.mXtRecorder.forcePersistLocked(currentTime);
        this.mUidRecorder.forcePersistLocked(currentTime);
        this.mUidTagRecorder.forcePersistLocked(currentTime);
        this.mDevRecorder = null;
        this.mXtRecorder = null;
        this.mUidRecorder = null;
        this.mUidTagRecorder = null;
        this.mDevStatsCached = null;
        this.mXtStatsCached = null;
        this.mSystemReady = false;
    }

    private void maybeUpgradeLegacyStatsLocked() {
        try {
            File file = new File(this.mSystemDir, "netstats.bin");
            if (file.exists()) {
                this.mDevRecorder.importLegacyNetworkLocked(file);
                file.delete();
            }
            File file2 = new File(this.mSystemDir, "netstats_xt.bin");
            if (file2.exists()) {
                file2.delete();
            }
            File file3 = new File(this.mSystemDir, "netstats_uid.bin");
            if (file3.exists()) {
                this.mUidRecorder.importLegacyUidLocked(file3);
                this.mUidTagRecorder.importLegacyUidLocked(file3);
                file3.delete();
            }
        } catch (IOException e) {
            Log.wtf(TAG, "problem during legacy upgrade", e);
        } catch (OutOfMemoryError e2) {
            Log.wtf(TAG, "problem during legacy upgrade", e2);
        }
    }

    private void registerPollAlarmLocked() {
        if (this.mPollIntent != null) {
            this.mAlarmManager.cancel(this.mPollIntent);
        }
        this.mPollIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_NETWORK_STATS_POLL), 0);
        long currentRealtime = SystemClock.elapsedRealtime();
        this.mAlarmManager.setInexactRepeating(3, currentRealtime, this.mSettings.getPollInterval(), this.mPollIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerGlobalAlert() {
        try {
            this.mNetworkManager.setGlobalAlert(this.mGlobalAlertBytes);
        } catch (RemoteException e) {
        } catch (IllegalStateException e2) {
            Slog.w(TAG, "problem registering for global alert: " + e2);
        }
    }

    @Override // android.net.INetworkStatsService
    public INetworkStatsSession openSession() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.READ_NETWORK_USAGE_HISTORY, TAG);
        assertBandwidthControlEnabled();
        return new INetworkStatsSession.Stub() { // from class: com.android.server.net.NetworkStatsService.1
            private NetworkStatsCollection mUidComplete;
            private NetworkStatsCollection mUidTagComplete;

            private NetworkStatsCollection getUidComplete() {
                if (this.mUidComplete == null) {
                    synchronized (NetworkStatsService.this.mStatsLock) {
                        this.mUidComplete = NetworkStatsService.this.mUidRecorder.getOrLoadCompleteLocked();
                    }
                }
                return this.mUidComplete;
            }

            private NetworkStatsCollection getUidTagComplete() {
                if (this.mUidTagComplete == null) {
                    synchronized (NetworkStatsService.this.mStatsLock) {
                        this.mUidTagComplete = NetworkStatsService.this.mUidTagRecorder.getOrLoadCompleteLocked();
                    }
                }
                return this.mUidTagComplete;
            }

            @Override // android.net.INetworkStatsSession
            public NetworkStats getSummaryForNetwork(NetworkTemplate template, long start, long end) {
                return NetworkStatsService.this.internalGetSummaryForNetwork(template, start, end);
            }

            @Override // android.net.INetworkStatsSession
            public NetworkStatsHistory getHistoryForNetwork(NetworkTemplate template, int fields) {
                return NetworkStatsService.this.internalGetHistoryForNetwork(template, fields);
            }

            @Override // android.net.INetworkStatsSession
            public NetworkStats getSummaryForAllUid(NetworkTemplate template, long start, long end, boolean includeTags) {
                NetworkStats stats = getUidComplete().getSummary(template, start, end);
                if (includeTags) {
                    NetworkStats tagStats = getUidTagComplete().getSummary(template, start, end);
                    stats.combineAllValues(tagStats);
                }
                return stats;
            }

            @Override // android.net.INetworkStatsSession
            public NetworkStatsHistory getHistoryForUid(NetworkTemplate template, int uid, int set, int tag, int fields) {
                if (tag == 0) {
                    return getUidComplete().getHistory(template, uid, set, tag, fields);
                }
                return getUidTagComplete().getHistory(template, uid, set, tag, fields);
            }

            @Override // android.net.INetworkStatsSession
            public void close() {
                this.mUidComplete = null;
                this.mUidTagComplete = null;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NetworkStats internalGetSummaryForNetwork(NetworkTemplate template, long start, long end) {
        if (!this.mSettings.getReportXtOverDev()) {
            return this.mDevStatsCached.getSummary(template, start, end);
        }
        long firstAtomicBucket = this.mXtStatsCached.getFirstAtomicBucketMillis();
        NetworkStats dev = this.mDevStatsCached.getSummary(template, Math.min(start, firstAtomicBucket), Math.min(end, firstAtomicBucket));
        NetworkStats xt = this.mXtStatsCached.getSummary(template, Math.max(start, firstAtomicBucket), Math.max(end, firstAtomicBucket));
        xt.combineAllValues(dev);
        return xt;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NetworkStatsHistory internalGetHistoryForNetwork(NetworkTemplate template, int fields) {
        if (!this.mSettings.getReportXtOverDev()) {
            return this.mDevStatsCached.getHistory(template, -1, -1, 0, fields);
        }
        long firstAtomicBucket = this.mXtStatsCached.getFirstAtomicBucketMillis();
        NetworkStatsHistory dev = this.mDevStatsCached.getHistory(template, -1, -1, 0, fields, Long.MIN_VALUE, firstAtomicBucket);
        NetworkStatsHistory xt = this.mXtStatsCached.getHistory(template, -1, -1, 0, fields, firstAtomicBucket, Long.MAX_VALUE);
        xt.recordEntireHistory(dev);
        return xt;
    }

    @Override // android.net.INetworkStatsService
    public long getNetworkTotalBytes(NetworkTemplate template, long start, long end) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.READ_NETWORK_USAGE_HISTORY, TAG);
        assertBandwidthControlEnabled();
        return internalGetSummaryForNetwork(template, start, end).getTotalBytes();
    }

    @Override // android.net.INetworkStatsService
    public String[] getMobileIfaces() {
        return this.mMobileIfaces;
    }

    @Override // android.net.INetworkStatsService
    public void incrementOperationCount(int uid, int tag, int operationCount) {
        if (Binder.getCallingUid() != uid) {
            this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MODIFY_NETWORK_ACCOUNTING, TAG);
        }
        if (operationCount < 0) {
            throw new IllegalArgumentException("operation count can only be incremented");
        }
        if (tag == 0) {
            throw new IllegalArgumentException("operation count must have specific tag");
        }
        synchronized (this.mStatsLock) {
            int set = this.mActiveUidCounterSet.get(uid, 0);
            this.mUidOperations.combineValues(this.mActiveIface, uid, set, tag, 0L, 0L, 0L, 0L, operationCount);
            this.mUidOperations.combineValues(this.mActiveIface, uid, set, 0, 0L, 0L, 0L, 0L, operationCount);
        }
    }

    @Override // android.net.INetworkStatsService
    public void setUidForeground(int uid, boolean uidForeground) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MODIFY_NETWORK_ACCOUNTING, TAG);
        synchronized (this.mStatsLock) {
            int set = uidForeground ? 1 : 0;
            int oldSet = this.mActiveUidCounterSet.get(uid, 0);
            if (oldSet != set) {
                this.mActiveUidCounterSet.put(uid, set);
                NetworkManagementSocketTagger.setKernelCounterSet(uid, set);
            }
        }
    }

    @Override // android.net.INetworkStatsService
    public void advisePersistThreshold(long thresholdBytes) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MODIFY_NETWORK_ACCOUNTING, TAG);
        assertBandwidthControlEnabled();
        this.mPersistThreshold = MathUtils.constrain(thresholdBytes, 131072L, 2097152L);
        long currentTime = this.mTime.hasCache() ? this.mTime.currentTimeMillis() : System.currentTimeMillis();
        synchronized (this.mStatsLock) {
            if (this.mSystemReady) {
                updatePersistThresholds();
                this.mDevRecorder.maybePersistLocked(currentTime);
                this.mXtRecorder.maybePersistLocked(currentTime);
                this.mUidRecorder.maybePersistLocked(currentTime);
                this.mUidTagRecorder.maybePersistLocked(currentTime);
                registerGlobalAlert();
            }
        }
    }

    private void updatePersistThresholds() {
        this.mDevRecorder.setPersistThreshold(this.mSettings.getDevPersistBytes(this.mPersistThreshold));
        this.mXtRecorder.setPersistThreshold(this.mSettings.getXtPersistBytes(this.mPersistThreshold));
        this.mUidRecorder.setPersistThreshold(this.mSettings.getUidPersistBytes(this.mPersistThreshold));
        this.mUidTagRecorder.setPersistThreshold(this.mSettings.getUidTagPersistBytes(this.mPersistThreshold));
        this.mGlobalAlertBytes = this.mSettings.getGlobalAlertBytes(this.mPersistThreshold);
    }

    private void updateIfacesLocked() {
        if (this.mSystemReady) {
            performPollLocked(1);
            try {
                NetworkState[] states = this.mConnManager.getAllNetworkState();
                LinkProperties activeLink = this.mConnManager.getActiveLinkProperties();
                this.mActiveIface = activeLink != null ? activeLink.getInterfaceName() : null;
                this.mActiveIfaces.clear();
                for (NetworkState state : states) {
                    if (state.networkInfo.isConnected()) {
                        String iface = state.linkProperties.getInterfaceName();
                        NetworkIdentitySet ident = this.mActiveIfaces.get(iface);
                        if (ident == null) {
                            ident = new NetworkIdentitySet();
                            this.mActiveIfaces.put(iface, ident);
                        }
                        ident.add(NetworkIdentity.buildNetworkIdentity(this.mContext, state));
                        if (ConnectivityManager.isNetworkTypeMobile(state.networkInfo.getType()) && iface != null && !ArrayUtils.contains(this.mMobileIfaces, iface)) {
                            this.mMobileIfaces = (String[]) ArrayUtils.appendElement(String.class, this.mMobileIfaces, iface);
                        }
                    }
                }
            } catch (RemoteException e) {
            }
        }
    }

    private void bootstrapStatsLocked() {
        long currentTime = this.mTime.hasCache() ? this.mTime.currentTimeMillis() : System.currentTimeMillis();
        try {
            NetworkStats uidSnapshot = getNetworkStatsUidDetail();
            NetworkStats xtSnapshot = this.mNetworkManager.getNetworkStatsSummaryXt();
            NetworkStats devSnapshot = this.mNetworkManager.getNetworkStatsSummaryDev();
            this.mDevRecorder.recordSnapshotLocked(devSnapshot, this.mActiveIfaces, currentTime);
            this.mXtRecorder.recordSnapshotLocked(xtSnapshot, this.mActiveIfaces, currentTime);
            this.mUidRecorder.recordSnapshotLocked(uidSnapshot, this.mActiveIfaces, currentTime);
            this.mUidTagRecorder.recordSnapshotLocked(uidSnapshot, this.mActiveIfaces, currentTime);
        } catch (RemoteException e) {
        } catch (IllegalStateException e2) {
            Slog.w(TAG, "problem reading network stats: " + e2);
        }
    }

    private void performPollLocked(int flags) {
        if (this.mSystemReady) {
            SystemClock.elapsedRealtime();
            boolean persistNetwork = (flags & 1) != 0;
            boolean persistUid = (flags & 2) != 0;
            boolean persistForce = (flags & 256) != 0;
            long currentTime = this.mTime.hasCache() ? this.mTime.currentTimeMillis() : System.currentTimeMillis();
            try {
                NetworkStats uidSnapshot = getNetworkStatsUidDetail();
                NetworkStats xtSnapshot = this.mNetworkManager.getNetworkStatsSummaryXt();
                NetworkStats devSnapshot = this.mNetworkManager.getNetworkStatsSummaryDev();
                this.mDevRecorder.recordSnapshotLocked(devSnapshot, this.mActiveIfaces, currentTime);
                this.mXtRecorder.recordSnapshotLocked(xtSnapshot, this.mActiveIfaces, currentTime);
                this.mUidRecorder.recordSnapshotLocked(uidSnapshot, this.mActiveIfaces, currentTime);
                this.mUidTagRecorder.recordSnapshotLocked(uidSnapshot, this.mActiveIfaces, currentTime);
                if (persistForce) {
                    this.mDevRecorder.forcePersistLocked(currentTime);
                    this.mXtRecorder.forcePersistLocked(currentTime);
                    this.mUidRecorder.forcePersistLocked(currentTime);
                    this.mUidTagRecorder.forcePersistLocked(currentTime);
                } else {
                    if (persistNetwork) {
                        this.mDevRecorder.maybePersistLocked(currentTime);
                        this.mXtRecorder.maybePersistLocked(currentTime);
                    }
                    if (persistUid) {
                        this.mUidRecorder.maybePersistLocked(currentTime);
                        this.mUidTagRecorder.maybePersistLocked(currentTime);
                    }
                }
                if (this.mSettings.getSampleEnabled()) {
                    performSampleLocked();
                }
                Intent updatedIntent = new Intent(ACTION_NETWORK_STATS_UPDATED);
                updatedIntent.setFlags(1073741824);
                this.mContext.sendBroadcastAsUser(updatedIntent, UserHandle.ALL, Manifest.permission.READ_NETWORK_USAGE_HISTORY);
            } catch (RemoteException e) {
            } catch (IllegalStateException e2) {
                Log.wtf(TAG, "problem reading network stats", e2);
            }
        }
    }

    private void performSampleLocked() {
        long trustedTime = this.mTime.hasCache() ? this.mTime.currentTimeMillis() : -1L;
        NetworkTemplate template = NetworkTemplate.buildTemplateMobileWildcard();
        NetworkStats.Entry devTotal = this.mDevRecorder.getTotalSinceBootLocked(template);
        NetworkStats.Entry xtTotal = this.mXtRecorder.getTotalSinceBootLocked(template);
        NetworkStats.Entry uidTotal = this.mUidRecorder.getTotalSinceBootLocked(template);
        EventLogTags.writeNetstatsMobileSample(devTotal.rxBytes, devTotal.rxPackets, devTotal.txBytes, devTotal.txPackets, xtTotal.rxBytes, xtTotal.rxPackets, xtTotal.txBytes, xtTotal.txPackets, uidTotal.rxBytes, uidTotal.rxPackets, uidTotal.txBytes, uidTotal.txPackets, trustedTime);
        NetworkTemplate template2 = NetworkTemplate.buildTemplateWifiWildcard();
        NetworkStats.Entry devTotal2 = this.mDevRecorder.getTotalSinceBootLocked(template2);
        NetworkStats.Entry xtTotal2 = this.mXtRecorder.getTotalSinceBootLocked(template2);
        NetworkStats.Entry uidTotal2 = this.mUidRecorder.getTotalSinceBootLocked(template2);
        EventLogTags.writeNetstatsWifiSample(devTotal2.rxBytes, devTotal2.rxPackets, devTotal2.txBytes, devTotal2.txPackets, xtTotal2.rxBytes, xtTotal2.rxPackets, xtTotal2.txBytes, xtTotal2.txPackets, uidTotal2.rxBytes, uidTotal2.rxPackets, uidTotal2.txBytes, uidTotal2.txPackets, trustedTime);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeUidsLocked(int... uids) {
        performPollLocked(3);
        this.mUidRecorder.removeUidsLocked(uids);
        this.mUidTagRecorder.removeUidsLocked(uids);
        for (int uid : uids) {
            NetworkManagementSocketTagger.resetKernelUidStats(uid);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeUserLocked(int userId) {
        int[] uids = new int[0];
        List<ApplicationInfo> apps = this.mContext.getPackageManager().getInstalledApplications(8704);
        for (ApplicationInfo app : apps) {
            int uid = UserHandle.getUid(userId, app.uid);
            uids = ArrayUtils.appendInt(uids, uid);
        }
        removeUidsLocked(uids);
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DUMP, TAG);
        HashSet<String> argSet = new HashSet<>();
        for (String arg : args) {
            argSet.add(arg);
        }
        boolean poll = argSet.contains("--poll") || argSet.contains("poll");
        boolean checkin = argSet.contains("--checkin");
        boolean fullHistory = argSet.contains("--full") || argSet.contains("full");
        boolean includeUid = argSet.contains("--uid") || argSet.contains("detail");
        boolean includeTag = argSet.contains("--tag") || argSet.contains("detail");
        IndentingPrintWriter pw = new IndentingPrintWriter(writer, "  ");
        synchronized (this.mStatsLock) {
            if (poll) {
                performPollLocked(259);
                pw.println("Forced poll");
            } else if (checkin) {
                pw.println("Current files:");
                pw.increaseIndent();
                String[] arr$ = this.mBaseDir.list();
                for (String file : arr$) {
                    pw.println(file);
                }
                pw.decreaseIndent();
            } else {
                pw.println("Active interfaces:");
                pw.increaseIndent();
                for (String iface : this.mActiveIfaces.keySet()) {
                    NetworkIdentitySet ident = this.mActiveIfaces.get(iface);
                    pw.print("iface=");
                    pw.print(iface);
                    pw.print(" ident=");
                    pw.println(ident.toString());
                }
                pw.decreaseIndent();
                pw.println("Dev stats:");
                pw.increaseIndent();
                this.mDevRecorder.dumpLocked(pw, fullHistory);
                pw.decreaseIndent();
                pw.println("Xt stats:");
                pw.increaseIndent();
                this.mXtRecorder.dumpLocked(pw, fullHistory);
                pw.decreaseIndent();
                if (includeUid) {
                    pw.println("UID stats:");
                    pw.increaseIndent();
                    this.mUidRecorder.dumpLocked(pw, fullHistory);
                    pw.decreaseIndent();
                }
                if (includeTag) {
                    pw.println("UID tag stats:");
                    pw.increaseIndent();
                    this.mUidTagRecorder.dumpLocked(pw, fullHistory);
                    pw.decreaseIndent();
                }
            }
        }
    }

    private NetworkStats getNetworkStatsUidDetail() throws RemoteException {
        NetworkStats uidSnapshot = this.mNetworkManager.getNetworkStatsUidDetail(-1);
        NetworkStats tetherSnapshot = getNetworkStatsTethering();
        uidSnapshot.combineAllValues(tetherSnapshot);
        uidSnapshot.combineAllValues(this.mUidOperations);
        return uidSnapshot;
    }

    private NetworkStats getNetworkStatsTethering() throws RemoteException {
        try {
            return this.mNetworkManager.getNetworkStatsTethering();
        } catch (IllegalStateException e) {
            Log.wtf(TAG, "problem reading network stats", e);
            return new NetworkStats(0L, 10);
        }
    }

    private void assertBandwidthControlEnabled() {
        if (!isBandwidthControlEnabled()) {
            throw new IllegalStateException("Bandwidth module disabled");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NetworkStatsService$DropBoxNonMonotonicObserver.class */
    public class DropBoxNonMonotonicObserver implements NetworkStats.NonMonotonicObserver<String> {
        private DropBoxNonMonotonicObserver() {
        }

        @Override // android.net.NetworkStats.NonMonotonicObserver
        public void foundNonMonotonic(NetworkStats left, int leftIndex, NetworkStats right, int rightIndex, String cookie) {
            Log.w(NetworkStatsService.TAG, "found non-monotonic values; saving to dropbox");
            StringBuilder builder = new StringBuilder();
            builder.append("found non-monotonic " + cookie + " values at left[" + leftIndex + "] - right[" + rightIndex + "]\n");
            builder.append("left=").append(left).append('\n');
            builder.append("right=").append(right).append('\n');
            DropBoxManager dropBox = (DropBoxManager) NetworkStatsService.this.mContext.getSystemService(Context.DROPBOX_SERVICE);
            dropBox.addText(NetworkStatsService.TAG_NETSTATS_ERROR, builder.toString());
        }
    }

    /* loaded from: NetworkStatsService$DefaultNetworkStatsSettings.class */
    private static class DefaultNetworkStatsSettings implements NetworkStatsSettings {
        private final ContentResolver mResolver;

        public DefaultNetworkStatsSettings(Context context) {
            this.mResolver = (ContentResolver) Preconditions.checkNotNull(context.getContentResolver());
        }

        private long getGlobalLong(String name, long def) {
            return Settings.Global.getLong(this.mResolver, name, def);
        }

        private boolean getGlobalBoolean(String name, boolean def) {
            int defInt = def ? 1 : 0;
            return Settings.Global.getInt(this.mResolver, name, defInt) != 0;
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public long getPollInterval() {
            return getGlobalLong(Settings.Global.NETSTATS_POLL_INTERVAL, AlarmManager.INTERVAL_HALF_HOUR);
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public long getTimeCacheMaxAge() {
            return getGlobalLong(Settings.Global.NETSTATS_TIME_CACHE_MAX_AGE, 86400000L);
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public long getGlobalAlertBytes(long def) {
            return getGlobalLong(Settings.Global.NETSTATS_GLOBAL_ALERT_BYTES, def);
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public boolean getSampleEnabled() {
            return getGlobalBoolean(Settings.Global.NETSTATS_SAMPLE_ENABLED, true);
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public boolean getReportXtOverDev() {
            return getGlobalBoolean(Settings.Global.NETSTATS_REPORT_XT_OVER_DEV, true);
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public NetworkStatsSettings.Config getDevConfig() {
            return new NetworkStatsSettings.Config(getGlobalLong(Settings.Global.NETSTATS_DEV_BUCKET_DURATION, 3600000L), getGlobalLong(Settings.Global.NETSTATS_DEV_ROTATE_AGE, 1296000000L), getGlobalLong(Settings.Global.NETSTATS_DEV_DELETE_AGE, 7776000000L));
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public NetworkStatsSettings.Config getXtConfig() {
            return getDevConfig();
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public NetworkStatsSettings.Config getUidConfig() {
            return new NetworkStatsSettings.Config(getGlobalLong(Settings.Global.NETSTATS_UID_BUCKET_DURATION, 7200000L), getGlobalLong(Settings.Global.NETSTATS_UID_ROTATE_AGE, 1296000000L), getGlobalLong(Settings.Global.NETSTATS_UID_DELETE_AGE, 7776000000L));
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public NetworkStatsSettings.Config getUidTagConfig() {
            return new NetworkStatsSettings.Config(getGlobalLong(Settings.Global.NETSTATS_UID_TAG_BUCKET_DURATION, 7200000L), getGlobalLong(Settings.Global.NETSTATS_UID_TAG_ROTATE_AGE, 432000000L), getGlobalLong(Settings.Global.NETSTATS_UID_TAG_DELETE_AGE, 1296000000L));
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public long getDevPersistBytes(long def) {
            return getGlobalLong(Settings.Global.NETSTATS_DEV_PERSIST_BYTES, def);
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public long getXtPersistBytes(long def) {
            return getDevPersistBytes(def);
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public long getUidPersistBytes(long def) {
            return getGlobalLong(Settings.Global.NETSTATS_UID_PERSIST_BYTES, def);
        }

        @Override // com.android.server.net.NetworkStatsService.NetworkStatsSettings
        public long getUidTagPersistBytes(long def) {
            return getGlobalLong(Settings.Global.NETSTATS_UID_TAG_PERSIST_BYTES, def);
        }
    }
}