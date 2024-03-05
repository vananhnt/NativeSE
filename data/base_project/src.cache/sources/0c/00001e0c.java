package com.android.server.content;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ISyncAdapter;
import android.content.ISyncContext;
import android.content.ISyncStatusObserver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PeriodicSync;
import android.content.ServiceConnection;
import android.content.SyncActivityTooManyDeletes;
import android.content.SyncAdapterType;
import android.content.SyncAdaptersCache;
import android.content.SyncInfo;
import android.content.SyncResult;
import android.content.SyncStatusInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.RegisteredServicesCacheListener;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.EventLog;
import android.util.Log;
import android.util.Pair;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.accounts.AccountManagerService;
import com.android.server.content.SyncStorageEngine;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.sip.header.SubscriptionStateHeader;

/* loaded from: SyncManager.class */
public class SyncManager {
    private static final String TAG = "SyncManager";
    private static final long LOCAL_SYNC_DELAY;
    private static final long MAX_TIME_PER_SYNC;
    private static final long SYNC_NOTIFICATION_DELAY;
    private static final long INITIAL_SYNC_RETRY_TIME_IN_MS = 30000;
    private static final long DEFAULT_MAX_SYNC_RETRY_TIME_IN_SECONDS = 3600;
    private static final int DELAY_RETRY_SYNC_IN_PROGRESS_IN_SECONDS = 10;
    private static final int INITIALIZATION_UNBIND_DELAY_MS = 5000;
    private static final String SYNC_WAKE_LOCK_PREFIX = "*sync*";
    private static final String HANDLE_SYNC_ALARM_WAKE_LOCK = "SyncManagerHandleSyncAlarm";
    private static final String SYNC_LOOP_WAKE_LOCK = "SyncLoopWakeLock";
    private static final int MAX_SIMULTANEOUS_REGULAR_SYNCS;
    private static final int MAX_SIMULTANEOUS_INITIALIZATION_SYNCS;
    private Context mContext;
    private static final AccountAndUser[] INITIAL_ACCOUNTS_ARRAY;
    private volatile PowerManager.WakeLock mHandleAlarmWakeLock;
    private volatile PowerManager.WakeLock mSyncManagerWakeLock;
    private final NotificationManager mNotificationMgr;
    private SyncStorageEngine mSyncStorageEngine;
    @GuardedBy("mSyncQueue")
    private final SyncQueue mSyncQueue;
    private final PendingIntent mSyncAlarmIntent;
    private ConnectivityManager mConnManagerDoNotUseDirectly;
    protected SyncAdaptersCache mSyncAdapters;
    private final PowerManager mPowerManager;
    private int mSyncRandomOffsetMillis;
    private final UserManager mUserManager;
    private static final long SYNC_ALARM_TIMEOUT_MIN = 30000;
    private static final long SYNC_ALARM_TIMEOUT_MAX = 7200000;
    private static final String ACTION_SYNC_ALARM = "android.content.syncmanager.SYNC_ALARM";
    private final SyncHandler mSyncHandler;
    private volatile AccountAndUser[] mRunningAccounts = INITIAL_ACCOUNTS_ARRAY;
    private volatile boolean mDataConnectionIsConnected = false;
    private volatile boolean mStorageIsLow = false;
    private AlarmManager mAlarmService = null;
    protected final ArrayList<ActiveSyncContext> mActiveSyncContexts = Lists.newArrayList();
    private boolean mNeedSyncActiveNotification = false;
    private BroadcastReceiver mStorageIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_DEVICE_STORAGE_LOW.equals(action)) {
                if (Log.isLoggable(SyncManager.TAG, 2)) {
                    Log.v(SyncManager.TAG, "Internal storage is low.");
                }
                SyncManager.this.mStorageIsLow = true;
                SyncManager.this.cancelActiveSync(null, -1, null);
            } else if (Intent.ACTION_DEVICE_STORAGE_OK.equals(action)) {
                if (Log.isLoggable(SyncManager.TAG, 2)) {
                    Log.v(SyncManager.TAG, "Internal storage is ok.");
                }
                SyncManager.this.mStorageIsLow = false;
                SyncManager.this.sendCheckAlarmsMessage();
            }
        }
    };
    private BroadcastReceiver mBootCompletedReceiver = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            SyncManager.this.mSyncHandler.onBootCompleted();
        }
    };
    private BroadcastReceiver mBackgroundDataSettingChanged = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (SyncManager.this.getConnectivityManager().getBackgroundDataSetting()) {
                SyncManager.this.scheduleSync(null, -1, -1, null, new Bundle(), 0L, 0L, false);
            }
        }
    };
    private BroadcastReceiver mAccountsUpdatedReceiver = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            SyncManager.this.updateRunningAccounts();
            SyncManager.this.scheduleSync(null, -1, -2, null, null, 0L, 0L, false);
        }
    };
    private BroadcastReceiver mConnectivityIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean wasConnected = SyncManager.this.mDataConnectionIsConnected;
            SyncManager.this.mDataConnectionIsConnected = SyncManager.this.readDataConnectionState();
            if (SyncManager.this.mDataConnectionIsConnected) {
                if (!wasConnected) {
                    if (Log.isLoggable(SyncManager.TAG, 2)) {
                        Log.v(SyncManager.TAG, "Reconnection detected: clearing all backoffs");
                    }
                    synchronized (SyncManager.this.mSyncQueue) {
                        SyncManager.this.mSyncStorageEngine.clearAllBackoffsLocked(SyncManager.this.mSyncQueue);
                    }
                }
                SyncManager.this.sendCheckAlarmsMessage();
            }
        }
    };
    private BroadcastReceiver mShutdownIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.6
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.w(SyncManager.TAG, "Writing sync state before shutdown...");
            SyncManager.this.getSyncStorageEngine().writeAllState();
        }
    };
    private BroadcastReceiver mUserIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.7
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -10000);
            if (userId == -10000) {
                return;
            }
            if (Intent.ACTION_USER_REMOVED.equals(action)) {
                SyncManager.this.onUserRemoved(userId);
            } else if (Intent.ACTION_USER_STARTING.equals(action)) {
                SyncManager.this.onUserStarting(userId);
            } else if (Intent.ACTION_USER_STOPPING.equals(action)) {
                SyncManager.this.onUserStopping(userId);
            }
        }
    };
    private volatile boolean mBootCompleted = false;

    static /* synthetic */ boolean access$402(SyncManager x0, boolean x1) {
        x0.mDataConnectionIsConnected = x1;
        return x1;
    }

    static /* synthetic */ boolean access$500(SyncManager x0) {
        return x0.readDataConnectionState();
    }

    static /* synthetic */ PowerManager.WakeLock access$2300(SyncManager x0) {
        return x0.mSyncManagerWakeLock;
    }

    static /* synthetic */ boolean access$2400(SyncManager x0, ActiveSyncContext x1) {
        return x0.isSyncStillActive(x1);
    }

    static {
        boolean isLargeRAM = !ActivityManager.isLowRamDeviceStatic();
        int defaultMaxInitSyncs = isLargeRAM ? 5 : 2;
        int defaultMaxRegularSyncs = isLargeRAM ? 2 : 1;
        MAX_SIMULTANEOUS_INITIALIZATION_SYNCS = SystemProperties.getInt("sync.max_init_syncs", defaultMaxInitSyncs);
        MAX_SIMULTANEOUS_REGULAR_SYNCS = SystemProperties.getInt("sync.max_regular_syncs", defaultMaxRegularSyncs);
        LOCAL_SYNC_DELAY = SystemProperties.getLong("sync.local_sync_delay", LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS);
        MAX_TIME_PER_SYNC = SystemProperties.getLong("sync.max_time_per_sync", 300000L);
        SYNC_NOTIFICATION_DELAY = SystemProperties.getLong("sync.notification_delay", LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS);
        INITIAL_ACCOUNTS_ARRAY = new AccountAndUser[0];
    }

    private List<UserInfo> getAllUsers() {
        return this.mUserManager.getUsers();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean containsAccountAndUser(AccountAndUser[] accounts, Account account, int userId) {
        boolean found = false;
        int i = 0;
        while (true) {
            if (i < accounts.length) {
                if (accounts[i].userId != userId || !accounts[i].account.equals(account)) {
                    i++;
                } else {
                    found = true;
                    break;
                }
            } else {
                break;
            }
        }
        return found;
    }

    public void updateRunningAccounts() {
        this.mRunningAccounts = AccountManagerService.getSingleton().getRunningAccounts();
        if (this.mBootCompleted) {
            doDatabaseCleanup();
        }
        Iterator i$ = this.mActiveSyncContexts.iterator();
        while (i$.hasNext()) {
            ActiveSyncContext currentSyncContext = i$.next();
            if (!containsAccountAndUser(this.mRunningAccounts, currentSyncContext.mSyncOperation.account, currentSyncContext.mSyncOperation.userId)) {
                Log.d(TAG, "canceling sync since the account is no longer running");
                sendSyncFinishedOrCanceledMessage(currentSyncContext, null);
            }
        }
        sendCheckAlarmsMessage();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDatabaseCleanup() {
        for (UserInfo user : this.mUserManager.getUsers(true)) {
            if (!user.partial) {
                Account[] accountsForUser = AccountManagerService.getSingleton().getAccounts(user.id);
                this.mSyncStorageEngine.doDatabaseCleanup(accountsForUser, user.id);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean readDataConnectionState() {
        NetworkInfo networkInfo = getConnectivityManager().getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ConnectivityManager getConnectivityManager() {
        ConnectivityManager connectivityManager;
        synchronized (this) {
            if (this.mConnManagerDoNotUseDirectly == null) {
                this.mConnManagerDoNotUseDirectly = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            connectivityManager = this.mConnManagerDoNotUseDirectly;
        }
        return connectivityManager;
    }

    public SyncManager(Context context, boolean factoryTest) {
        this.mContext = context;
        SyncStorageEngine.init(context);
        this.mSyncStorageEngine = SyncStorageEngine.getSingleton();
        this.mSyncStorageEngine.setOnSyncRequestListener(new SyncStorageEngine.OnSyncRequestListener() { // from class: com.android.server.content.SyncManager.8
            @Override // com.android.server.content.SyncStorageEngine.OnSyncRequestListener
            public void onSyncRequest(Account account, int userId, int reason, String authority, Bundle extras) {
                SyncManager.this.scheduleSync(account, userId, reason, authority, extras, 0L, 0L, false);
            }
        });
        this.mSyncAdapters = new SyncAdaptersCache(this.mContext);
        this.mSyncQueue = new SyncQueue(this.mContext.getPackageManager(), this.mSyncStorageEngine, this.mSyncAdapters);
        this.mSyncHandler = new SyncHandler(BackgroundThread.get().getLooper());
        this.mSyncAdapters.setListener(new RegisteredServicesCacheListener<SyncAdapterType>() { // from class: com.android.server.content.SyncManager.9
            @Override // android.content.pm.RegisteredServicesCacheListener
            public void onServiceChanged(SyncAdapterType type, int userId, boolean removed) {
                if (!removed) {
                    SyncManager.this.scheduleSync(null, -1, -3, type.authority, null, 0L, 0L, false);
                }
            }
        }, this.mSyncHandler);
        this.mSyncAlarmIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_SYNC_ALARM), 0);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this.mConnectivityIntentReceiver, intentFilter);
        if (!factoryTest) {
            IntentFilter intentFilter2 = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
            context.registerReceiver(this.mBootCompletedReceiver, intentFilter2);
        }
        IntentFilter intentFilter3 = new IntentFilter(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
        context.registerReceiver(this.mBackgroundDataSettingChanged, intentFilter3);
        IntentFilter intentFilter4 = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
        intentFilter4.addAction(Intent.ACTION_DEVICE_STORAGE_OK);
        context.registerReceiver(this.mStorageIntentReceiver, intentFilter4);
        IntentFilter intentFilter5 = new IntentFilter(Intent.ACTION_SHUTDOWN);
        intentFilter5.setPriority(100);
        context.registerReceiver(this.mShutdownIntentReceiver, intentFilter5);
        IntentFilter intentFilter6 = new IntentFilter();
        intentFilter6.addAction(Intent.ACTION_USER_REMOVED);
        intentFilter6.addAction(Intent.ACTION_USER_STARTING);
        intentFilter6.addAction(Intent.ACTION_USER_STOPPING);
        this.mContext.registerReceiverAsUser(this.mUserIntentReceiver, UserHandle.ALL, intentFilter6, null, null);
        if (!factoryTest) {
            this.mNotificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            context.registerReceiver(new SyncAlarmIntentReceiver(), new IntentFilter(ACTION_SYNC_ALARM));
        } else {
            this.mNotificationMgr = null;
        }
        this.mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mHandleAlarmWakeLock = this.mPowerManager.newWakeLock(1, HANDLE_SYNC_ALARM_WAKE_LOCK);
        this.mHandleAlarmWakeLock.setReferenceCounted(false);
        this.mSyncManagerWakeLock = this.mPowerManager.newWakeLock(1, SYNC_LOOP_WAKE_LOCK);
        this.mSyncManagerWakeLock.setReferenceCounted(false);
        this.mSyncStorageEngine.addStatusChangeListener(1, new ISyncStatusObserver.Stub() { // from class: com.android.server.content.SyncManager.10
            @Override // android.content.ISyncStatusObserver
            public void onStatusChanged(int which) {
                SyncManager.this.sendCheckAlarmsMessage();
            }
        });
        if (!factoryTest) {
            this.mContext.registerReceiverAsUser(this.mAccountsUpdatedReceiver, UserHandle.ALL, new IntentFilter(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION), null, null);
        }
        this.mSyncRandomOffsetMillis = this.mSyncStorageEngine.getSyncRandomOffset() * 1000;
    }

    private long jitterize(long minValue, long maxValue) {
        Random random = new Random(SystemClock.elapsedRealtime());
        long spread = maxValue - minValue;
        if (spread > 2147483647L) {
            throw new IllegalArgumentException("the difference between the maxValue and the minValue must be less than 2147483647");
        }
        return minValue + random.nextInt((int) spread);
    }

    public SyncStorageEngine getSyncStorageEngine() {
        return this.mSyncStorageEngine;
    }

    public int getIsSyncable(Account account, int userId, String providerName) {
        int isSyncable = this.mSyncStorageEngine.getIsSyncable(account, userId, providerName);
        UserInfo userInfo = UserManager.get(this.mContext).getUserInfo(userId);
        if (userInfo == null || !userInfo.isRestricted()) {
            return isSyncable;
        }
        RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterInfo = this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(providerName, account.type), userId);
        if (syncAdapterInfo == null) {
            return isSyncable;
        }
        try {
            PackageInfo pInfo = AppGlobals.getPackageManager().getPackageInfo(syncAdapterInfo.componentName.getPackageName(), 0, userId);
            if (pInfo == null) {
                return isSyncable;
            }
            if (pInfo.restrictedAccountType != null && pInfo.restrictedAccountType.equals(account.type)) {
                return isSyncable;
            }
            return 0;
        } catch (RemoteException e) {
            return isSyncable;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureAlarmService() {
        if (this.mAlarmService == null) {
            this.mAlarmService = (AlarmManager) this.mContext.getSystemService("alarm");
        }
    }

    public void scheduleSync(Account requestedAccount, int userId, int reason, String requestedAuthority, Bundle extras, long beforeRuntimeMillis, long runtimeMillis, boolean onlyThoseWithUnkownSyncableState) {
        AccountAndUser[] accounts;
        int source;
        RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterInfo;
        boolean isLoggable = Log.isLoggable(TAG, 2);
        boolean backgroundDataUsageAllowed = !this.mBootCompleted || getConnectivityManager().getBackgroundDataSetting();
        if (extras == null) {
            extras = new Bundle();
        }
        if (isLoggable) {
            Log.d(TAG, "one-time sync for: " + requestedAccount + Separators.SP + extras.toString() + Separators.SP + requestedAuthority);
        }
        Boolean expedited = Boolean.valueOf(extras.getBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false));
        if (expedited.booleanValue()) {
            runtimeMillis = -1;
        }
        if (requestedAccount == null || userId == -1) {
            accounts = this.mRunningAccounts;
            if (accounts.length == 0) {
                if (isLoggable) {
                    Log.v(TAG, "scheduleSync: no accounts configured, dropping");
                    return;
                }
                return;
            }
        } else {
            accounts = new AccountAndUser[]{new AccountAndUser(requestedAccount, userId)};
        }
        boolean uploadOnly = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
        boolean manualSync = extras.getBoolean("force", false);
        if (manualSync) {
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
        }
        boolean ignoreSettings = extras.getBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, false);
        if (uploadOnly) {
            source = 1;
        } else if (manualSync) {
            source = 3;
        } else if (requestedAuthority == null) {
            source = 2;
        } else {
            source = 0;
        }
        AccountAndUser[] arr$ = accounts;
        for (AccountAndUser account : arr$) {
            HashSet<String> syncableAuthorities = new HashSet<>();
            for (RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapter : this.mSyncAdapters.getAllServices(account.userId)) {
                syncableAuthorities.add(syncAdapter.type.authority);
            }
            if (requestedAuthority != null) {
                boolean hasSyncAdapter = syncableAuthorities.contains(requestedAuthority);
                syncableAuthorities.clear();
                if (hasSyncAdapter) {
                    syncableAuthorities.add(requestedAuthority);
                }
            }
            Iterator i$ = syncableAuthorities.iterator();
            while (i$.hasNext()) {
                String authority = i$.next();
                int isSyncable = getIsSyncable(account.account, account.userId, authority);
                if (isSyncable != 0 && (syncAdapterInfo = this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(authority, account.account.type), account.userId)) != null) {
                    boolean allowParallelSyncs = syncAdapterInfo.type.allowParallelSyncs();
                    boolean isAlwaysSyncable = syncAdapterInfo.type.isAlwaysSyncable();
                    if (isSyncable < 0 && isAlwaysSyncable) {
                        this.mSyncStorageEngine.setIsSyncable(account.account, account.userId, authority, 1);
                        isSyncable = 1;
                    }
                    if (!onlyThoseWithUnkownSyncableState || isSyncable < 0) {
                        if (syncAdapterInfo.type.supportsUploading() || !uploadOnly) {
                            boolean syncAllowed = isSyncable < 0 || ignoreSettings || (backgroundDataUsageAllowed && this.mSyncStorageEngine.getMasterSyncAutomatically(account.userId) && this.mSyncStorageEngine.getSyncAutomatically(account.account, account.userId, authority));
                            if (!syncAllowed) {
                                if (isLoggable) {
                                    Log.d(TAG, "scheduleSync: sync of " + account + ", " + authority + " is not allowed, dropping request");
                                }
                            } else {
                                Pair<Long, Long> backoff = this.mSyncStorageEngine.getBackoff(account.account, account.userId, authority);
                                long delayUntil = this.mSyncStorageEngine.getDelayUntilTime(account.account, account.userId, authority);
                                long backoffTime = backoff != null ? backoff.first.longValue() : 0L;
                                if (isSyncable < 0) {
                                    Bundle newExtras = new Bundle();
                                    newExtras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
                                    if (isLoggable) {
                                        Log.v(TAG, "schedule initialisation Sync:, delay until " + delayUntil + ", run by 0, source " + source + ", account " + account + ", authority " + authority + ", extras " + newExtras);
                                    }
                                    scheduleSyncOperation(new SyncOperation(account.account, account.userId, reason, source, authority, newExtras, 0L, 0L, backoffTime, delayUntil, allowParallelSyncs));
                                }
                                if (!onlyThoseWithUnkownSyncableState) {
                                    if (isLoggable) {
                                        Log.v(TAG, "scheduleSync: delay until " + delayUntil + " run by " + runtimeMillis + " flex " + beforeRuntimeMillis + ", source " + source + ", account " + account + ", authority " + authority + ", extras " + extras);
                                    }
                                    scheduleSyncOperation(new SyncOperation(account.account, account.userId, reason, source, authority, extras, runtimeMillis, beforeRuntimeMillis, backoffTime, delayUntil, allowParallelSyncs));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void scheduleLocalSync(Account account, int userId, int reason, String authority) {
        Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        scheduleSync(account, userId, reason, authority, extras, LOCAL_SYNC_DELAY, 2 * LOCAL_SYNC_DELAY, false);
    }

    public SyncAdapterType[] getSyncAdapterTypes(int userId) {
        Collection<RegisteredServicesCache.ServiceInfo<SyncAdapterType>> serviceInfos = this.mSyncAdapters.getAllServices(userId);
        SyncAdapterType[] types = new SyncAdapterType[serviceInfos.size()];
        int i = 0;
        for (RegisteredServicesCache.ServiceInfo<SyncAdapterType> serviceInfo : serviceInfos) {
            types[i] = serviceInfo.type;
            i++;
        }
        return types;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSyncAlarmMessage() {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "sending MESSAGE_SYNC_ALARM");
        }
        this.mSyncHandler.sendEmptyMessage(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendCheckAlarmsMessage() {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "sending MESSAGE_CHECK_ALARMS");
        }
        this.mSyncHandler.removeMessages(3);
        this.mSyncHandler.sendEmptyMessage(3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSyncFinishedOrCanceledMessage(ActiveSyncContext syncContext, SyncResult syncResult) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "sending MESSAGE_SYNC_FINISHED");
        }
        Message msg = this.mSyncHandler.obtainMessage();
        msg.what = 1;
        msg.obj = new SyncHandlerMessagePayload(syncContext, syncResult);
        this.mSyncHandler.sendMessage(msg);
    }

    private void sendCancelSyncsMessage(Account account, int userId, String authority) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "sending MESSAGE_CANCEL");
        }
        Message msg = this.mSyncHandler.obtainMessage();
        msg.what = 6;
        msg.obj = Pair.create(account, authority);
        msg.arg1 = userId;
        this.mSyncHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SyncManager$SyncHandlerMessagePayload.class */
    public class SyncHandlerMessagePayload {
        public final ActiveSyncContext activeSyncContext;
        public final SyncResult syncResult;

        SyncHandlerMessagePayload(ActiveSyncContext syncContext, SyncResult syncResult) {
            this.activeSyncContext = syncContext;
            this.syncResult = syncResult;
        }
    }

    /* loaded from: SyncManager$SyncAlarmIntentReceiver.class */
    class SyncAlarmIntentReceiver extends BroadcastReceiver {
        SyncAlarmIntentReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            SyncManager.this.mHandleAlarmWakeLock.acquire();
            SyncManager.this.sendSyncAlarmMessage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearBackoffSetting(SyncOperation op) {
        this.mSyncStorageEngine.setBackoff(op.account, op.userId, op.authority, -1L, -1L);
        synchronized (this.mSyncQueue) {
            this.mSyncQueue.onBackoffChanged(op.account, op.userId, op.authority, 0L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void increaseBackoffSetting(SyncOperation op) {
        long now = SystemClock.elapsedRealtime();
        Pair<Long, Long> previousSettings = this.mSyncStorageEngine.getBackoff(op.account, op.userId, op.authority);
        long newDelayInMs = -1;
        if (previousSettings != null) {
            if (now < previousSettings.first.longValue()) {
                if (Log.isLoggable(TAG, 2)) {
                    Log.v(TAG, "Still in backoff, do not increase it. Remaining: " + ((previousSettings.first.longValue() - now) / 1000) + " seconds.");
                    return;
                }
                return;
            }
            newDelayInMs = previousSettings.second.longValue() * 2;
        }
        if (newDelayInMs <= 0) {
            newDelayInMs = jitterize(LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS, 33000L);
        }
        long maxSyncRetryTimeInSeconds = Settings.Global.getLong(this.mContext.getContentResolver(), Settings.Global.SYNC_MAX_RETRY_DELAY_IN_SECONDS, DEFAULT_MAX_SYNC_RETRY_TIME_IN_SECONDS);
        if (newDelayInMs > maxSyncRetryTimeInSeconds * 1000) {
            newDelayInMs = maxSyncRetryTimeInSeconds * 1000;
        }
        long backoff = now + newDelayInMs;
        this.mSyncStorageEngine.setBackoff(op.account, op.userId, op.authority, backoff, newDelayInMs);
        op.backoff = Long.valueOf(backoff);
        op.updateEffectiveRunTime();
        synchronized (this.mSyncQueue) {
            this.mSyncQueue.onBackoffChanged(op.account, op.userId, op.authority, backoff);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDelayUntilTime(SyncOperation op, long delayUntilSeconds) {
        long newDelayUntilTime;
        long delayUntil = delayUntilSeconds * 1000;
        long absoluteNow = System.currentTimeMillis();
        if (delayUntil > absoluteNow) {
            newDelayUntilTime = SystemClock.elapsedRealtime() + (delayUntil - absoluteNow);
        } else {
            newDelayUntilTime = 0;
        }
        this.mSyncStorageEngine.setDelayUntilTime(op.account, op.userId, op.authority, newDelayUntilTime);
        synchronized (this.mSyncQueue) {
            this.mSyncQueue.onDelayUntilTimeChanged(op.account, op.authority, newDelayUntilTime);
        }
    }

    public void cancelActiveSync(Account account, int userId, String authority) {
        sendCancelSyncsMessage(account, userId, authority);
    }

    public void scheduleSyncOperation(SyncOperation syncOperation) {
        boolean queueChanged;
        synchronized (this.mSyncQueue) {
            queueChanged = this.mSyncQueue.add(syncOperation);
        }
        if (queueChanged) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "scheduleSyncOperation: enqueued " + syncOperation);
            }
            sendCheckAlarmsMessage();
        } else if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "scheduleSyncOperation: dropping duplicate sync operation " + syncOperation);
        }
    }

    public void clearScheduledSyncOperations(Account account, int userId, String authority) {
        synchronized (this.mSyncQueue) {
            this.mSyncQueue.remove(account, userId, authority);
        }
        this.mSyncStorageEngine.setBackoff(account, userId, authority, -1L, -1L);
    }

    void maybeRescheduleSync(SyncResult syncResult, SyncOperation operation) {
        boolean isLoggable = Log.isLoggable(TAG, 3);
        if (isLoggable) {
            Log.d(TAG, "encountered error(s) during the sync: " + syncResult + ", " + operation);
        }
        SyncOperation operation2 = new SyncOperation(operation);
        if (operation2.extras.getBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, false)) {
            operation2.extras.remove(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF);
        }
        if (operation2.extras.getBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false)) {
            Log.d(TAG, "not retrying sync operation because SYNC_EXTRAS_DO_NOT_RETRY was specified " + operation2);
        } else if (operation2.extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false) && !syncResult.syncAlreadyInProgress) {
            operation2.extras.remove(ContentResolver.SYNC_EXTRAS_UPLOAD);
            Log.d(TAG, "retrying sync operation as a two-way sync because an upload-only sync encountered an error: " + operation2);
            scheduleSyncOperation(operation2);
        } else if (syncResult.tooManyRetries) {
            Log.d(TAG, "not retrying sync operation because it retried too many times: " + operation2);
        } else if (syncResult.madeSomeProgress()) {
            if (isLoggable) {
                Log.d(TAG, "retrying sync operation because even though it had an error it achieved some success");
            }
            scheduleSyncOperation(operation2);
        } else if (syncResult.syncAlreadyInProgress) {
            if (isLoggable) {
                Log.d(TAG, "retrying sync operation that failed because there was already a sync in progress: " + operation2);
            }
            scheduleSyncOperation(new SyncOperation(operation2.account, operation2.userId, operation2.reason, operation2.syncSource, operation2.authority, operation2.extras, 10000L, operation2.flexTime, operation2.backoff.longValue(), operation2.delayUntil, operation2.allowParallelSyncs));
        } else if (syncResult.hasSoftError()) {
            if (isLoggable) {
                Log.d(TAG, "retrying sync operation because it encountered a soft error: " + operation2);
            }
            scheduleSyncOperation(operation2);
        } else {
            Log.d(TAG, "not retrying sync operation because the error is a hard error: " + operation2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserStarting(int userId) {
        AccountManagerService.getSingleton().validateAccounts(userId);
        this.mSyncAdapters.invalidateCache(userId);
        updateRunningAccounts();
        synchronized (this.mSyncQueue) {
            this.mSyncQueue.addPendingOperations(userId);
        }
        Account[] accounts = AccountManagerService.getSingleton().getAccounts(userId);
        for (Account account : accounts) {
            scheduleSync(account, userId, -8, null, null, 0L, 0L, true);
        }
        sendCheckAlarmsMessage();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserStopping(int userId) {
        updateRunningAccounts();
        cancelActiveSync(null, userId, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserRemoved(int userId) {
        updateRunningAccounts();
        this.mSyncStorageEngine.doDatabaseCleanup(new Account[0], userId);
        synchronized (this.mSyncQueue) {
            this.mSyncQueue.removeUser(userId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SyncManager$ActiveSyncContext.class */
    public class ActiveSyncContext extends ISyncContext.Stub implements ServiceConnection, IBinder.DeathRecipient {
        final SyncOperation mSyncOperation;
        final long mHistoryRowId;
        boolean mBound;
        final PowerManager.WakeLock mSyncWakeLock;
        final int mSyncAdapterUid;
        SyncInfo mSyncInfo;
        boolean mIsLinkedToDeath = false;
        ISyncAdapter mSyncAdapter = null;
        final long mStartTime = SystemClock.elapsedRealtime();
        long mTimeoutStartTime = this.mStartTime;

        public ActiveSyncContext(SyncOperation syncOperation, long historyRowId, int syncAdapterUid) {
            this.mSyncAdapterUid = syncAdapterUid;
            this.mSyncOperation = syncOperation;
            this.mHistoryRowId = historyRowId;
            this.mSyncWakeLock = SyncManager.this.mSyncHandler.getSyncWakeLock(this.mSyncOperation.account, this.mSyncOperation.authority);
            this.mSyncWakeLock.setWorkSource(new WorkSource(syncAdapterUid));
            this.mSyncWakeLock.acquire();
        }

        @Override // android.content.ISyncContext
        public void sendHeartbeat() {
        }

        @Override // android.content.ISyncContext
        public void onFinished(SyncResult result) {
            if (Log.isLoggable(SyncManager.TAG, 2)) {
                Log.v(SyncManager.TAG, "onFinished: " + this);
            }
            SyncManager.this.sendSyncFinishedOrCanceledMessage(this, result);
        }

        public void toString(StringBuilder sb) {
            sb.append("startTime ").append(this.mStartTime).append(", mTimeoutStartTime ").append(this.mTimeoutStartTime).append(", mHistoryRowId ").append(this.mHistoryRowId).append(", syncOperation ").append(this.mSyncOperation);
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Message msg = SyncManager.this.mSyncHandler.obtainMessage();
            msg.what = 4;
            msg.obj = new ServiceConnectionData(this, ISyncAdapter.Stub.asInterface(service));
            SyncManager.this.mSyncHandler.sendMessage(msg);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Message msg = SyncManager.this.mSyncHandler.obtainMessage();
            msg.what = 5;
            msg.obj = new ServiceConnectionData(this, null);
            SyncManager.this.mSyncHandler.sendMessage(msg);
        }

        boolean bindToSyncAdapter(RegisteredServicesCache.ServiceInfo info, int userId) {
            if (Log.isLoggable(SyncManager.TAG, 2)) {
                Log.d(SyncManager.TAG, "bindToSyncAdapter: " + info.componentName + ", connection " + this);
            }
            Intent intent = new Intent();
            intent.setAction("android.content.SyncAdapter");
            intent.setComponent(info.componentName);
            intent.putExtra(Intent.EXTRA_CLIENT_LABEL, R.string.sync_binding_label);
            intent.putExtra(Intent.EXTRA_CLIENT_INTENT, PendingIntent.getActivityAsUser(SyncManager.this.mContext, 0, new Intent(Settings.ACTION_SYNC_SETTINGS), 0, null, new UserHandle(userId)));
            this.mBound = true;
            boolean bindResult = SyncManager.this.mContext.bindServiceAsUser(intent, this, 21, new UserHandle(this.mSyncOperation.userId));
            if (!bindResult) {
                this.mBound = false;
            }
            return bindResult;
        }

        protected void close() {
            if (Log.isLoggable(SyncManager.TAG, 2)) {
                Log.d(SyncManager.TAG, "unBindFromSyncAdapter: connection " + this);
            }
            if (this.mBound) {
                this.mBound = false;
                SyncManager.this.mContext.unbindService(this);
            }
            this.mSyncWakeLock.release();
            this.mSyncWakeLock.setWorkSource(null);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            toString(sb);
            return sb.toString();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            SyncManager.this.sendSyncFinishedOrCanceledMessage(this, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw) {
        IndentingPrintWriter ipw = new IndentingPrintWriter(pw, "  ");
        dumpSyncState(ipw);
        dumpSyncHistory(ipw);
        dumpSyncAdapters(ipw);
    }

    static String formatTime(long time) {
        Time tobj = new Time();
        tobj.set(time);
        return tobj.format("%Y-%m-%d %H:%M:%S");
    }

    protected void dumpSyncState(PrintWriter pw) {
        pw.print("data connected: ");
        pw.println(this.mDataConnectionIsConnected);
        pw.print("auto sync: ");
        List<UserInfo> users = getAllUsers();
        if (users != null) {
            for (UserInfo user : users) {
                pw.print("u" + user.id + Separators.EQUALS + this.mSyncStorageEngine.getMasterSyncAutomatically(user.id) + Separators.SP);
            }
            pw.println();
        }
        pw.print("memory low: ");
        pw.println(this.mStorageIsLow);
        AccountAndUser[] accounts = AccountManagerService.getSingleton().getAllAccounts();
        pw.print("accounts: ");
        if (accounts != INITIAL_ACCOUNTS_ARRAY) {
            pw.println(accounts.length);
        } else {
            pw.println("not known yet");
        }
        long now = SystemClock.elapsedRealtime();
        pw.print("now: ");
        pw.print(now);
        pw.println(" (" + formatTime(System.currentTimeMillis()) + Separators.RPAREN);
        pw.print("offset: ");
        pw.print(DateUtils.formatElapsedTime(this.mSyncRandomOffsetMillis / 1000));
        pw.println(" (HH:MM:SS)");
        pw.print("uptime: ");
        pw.print(DateUtils.formatElapsedTime(now / 1000));
        pw.println(" (HH:MM:SS)");
        pw.print("time spent syncing: ");
        pw.print(DateUtils.formatElapsedTime(this.mSyncHandler.mSyncTimeTracker.timeSpentSyncing() / 1000));
        pw.print(" (HH:MM:SS), sync ");
        pw.print(this.mSyncHandler.mSyncTimeTracker.mLastWasSyncing ? "" : "not ");
        pw.println("in progress");
        if (this.mSyncHandler.mAlarmScheduleTime != null) {
            pw.print("next alarm time: ");
            pw.print(this.mSyncHandler.mAlarmScheduleTime);
            pw.print(" (");
            pw.print(DateUtils.formatElapsedTime((this.mSyncHandler.mAlarmScheduleTime.longValue() - now) / 1000));
            pw.println(" (HH:MM:SS) from now)");
        } else {
            pw.println("no alarm is scheduled (there had better not be any pending syncs)");
        }
        pw.print("notification info: ");
        StringBuilder sb = new StringBuilder();
        this.mSyncHandler.mSyncNotificationInfo.toString(sb);
        pw.println(sb.toString());
        pw.println();
        pw.println("Active Syncs: " + this.mActiveSyncContexts.size());
        PackageManager pm = this.mContext.getPackageManager();
        Iterator i$ = this.mActiveSyncContexts.iterator();
        while (i$.hasNext()) {
            ActiveSyncContext activeSyncContext = i$.next();
            long durationInSeconds = (now - activeSyncContext.mStartTime) / 1000;
            pw.print("  ");
            pw.print(DateUtils.formatElapsedTime(durationInSeconds));
            pw.print(" - ");
            pw.print(activeSyncContext.mSyncOperation.dump(pm, false));
            pw.println();
        }
        synchronized (this.mSyncQueue) {
            sb.setLength(0);
            this.mSyncQueue.dump(sb);
            getSyncStorageEngine().dumpPendingOperations(sb);
        }
        pw.println();
        pw.print(sb.toString());
        pw.println();
        pw.println("Sync Status");
        for (AccountAndUser account : accounts) {
            pw.printf("Account %s u%d %s\n", account.account.name, Integer.valueOf(account.userId), account.account.type);
            pw.println("=======================================================================");
            PrintTable table = new PrintTable(13);
            table.set(0, 0, "Authority", "Syncable", "Enabled", "Delay", "Loc", "Poll", "Per", "Serv", "User", "Tot", "Time", "Last Sync", "Periodic");
            List<RegisteredServicesCache.ServiceInfo<SyncAdapterType>> sorted = Lists.newArrayList();
            sorted.addAll(this.mSyncAdapters.getAllServices(account.userId));
            Collections.sort(sorted, new Comparator<RegisteredServicesCache.ServiceInfo<SyncAdapterType>>() { // from class: com.android.server.content.SyncManager.11
                @Override // java.util.Comparator
                public int compare(RegisteredServicesCache.ServiceInfo<SyncAdapterType> lhs, RegisteredServicesCache.ServiceInfo<SyncAdapterType> rhs) {
                    return lhs.type.authority.compareTo(rhs.type.authority);
                }
            });
            for (RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterType : sorted) {
                if (((SyncAdapterType) syncAdapterType.type).accountType.equals(account.account.type)) {
                    int row = table.getNumRows();
                    Pair<SyncStorageEngine.AuthorityInfo, SyncStatusInfo> syncAuthoritySyncStatus = this.mSyncStorageEngine.getCopyOfAuthorityWithSyncStatus(account.account, account.userId, ((SyncAdapterType) syncAdapterType.type).authority);
                    SyncStorageEngine.AuthorityInfo settings = syncAuthoritySyncStatus.first;
                    SyncStatusInfo status = syncAuthoritySyncStatus.second;
                    String authority = settings.authority;
                    if (authority.length() > 50) {
                        authority = authority.substring(authority.length() - 50);
                    }
                    table.set(row, 0, authority, Integer.valueOf(settings.syncable), Boolean.valueOf(settings.enabled));
                    table.set(row, 4, Integer.valueOf(status.numSourceLocal), Integer.valueOf(status.numSourcePoll), Integer.valueOf(status.numSourcePeriodic), Integer.valueOf(status.numSourceServer), Integer.valueOf(status.numSourceUser), Integer.valueOf(status.numSyncs), DateUtils.formatElapsedTime(status.totalElapsedTime / 1000));
                    for (int i = 0; i < settings.periodicSyncs.size(); i++) {
                        PeriodicSync sync = settings.periodicSyncs.get(i);
                        String period = String.format("[p:%d s, f: %d s]", Long.valueOf(sync.period), Long.valueOf(sync.flexTime));
                        String extras = sync.extras.size() > 0 ? sync.extras.toString() : "Bundle[]";
                        String next = "Next sync: " + formatTime(status.getPeriodicSyncTime(i) + (sync.period * 1000));
                        table.set(row + (i * 2), 12, period + Separators.SP + extras);
                        table.set(row + (i * 2) + 1, 12, next);
                    }
                    int row1 = row;
                    if (settings.delayUntil > now) {
                        row1++;
                        table.set(row1, 12, "D: " + ((settings.delayUntil - now) / 1000));
                        if (settings.backoffTime > now) {
                            int row12 = row1 + 1;
                            table.set(row1, 12, "B: " + ((settings.backoffTime - now) / 1000));
                            row1 = row12 + 1;
                            table.set(row12, 12, Long.valueOf(settings.backoffDelay / 1000));
                        }
                    }
                    if (status.lastSuccessTime != 0) {
                        int i2 = row1;
                        int row13 = row1 + 1;
                        table.set(i2, 11, SyncStorageEngine.SOURCES[status.lastSuccessSource] + Separators.SP + "SUCCESS");
                        row1 = row13 + 1;
                        table.set(row13, 11, formatTime(status.lastSuccessTime));
                    }
                    if (status.lastFailureTime != 0) {
                        int i3 = row1;
                        int row14 = row1 + 1;
                        table.set(i3, 11, SyncStorageEngine.SOURCES[status.lastFailureSource] + Separators.SP + "FAILURE");
                        int row15 = row14 + 1;
                        table.set(row14, 11, formatTime(status.lastFailureTime));
                        int i4 = row15 + 1;
                        table.set(row15, 11, status.lastFailureMesg);
                    }
                }
            }
            table.writeTo(pw);
        }
    }

    private String getLastFailureMessage(int code) {
        switch (code) {
            case 1:
                return "sync already in progress";
            case 2:
                return "authentication error";
            case 3:
                return "I/O error";
            case 4:
                return "parse error";
            case 5:
                return "conflict error";
            case 6:
                return "too many deletions error";
            case 7:
                return "too many retries error";
            case 8:
                return "internal error";
            default:
                return "unknown";
        }
    }

    private void dumpTimeSec(PrintWriter pw, long time) {
        pw.print(time / 1000);
        pw.print('.');
        pw.print((time / 100) % 10);
        pw.print('s');
    }

    private void dumpDayStatistic(PrintWriter pw, SyncStorageEngine.DayStats ds) {
        pw.print("Success (");
        pw.print(ds.successCount);
        if (ds.successCount > 0) {
            pw.print(" for ");
            dumpTimeSec(pw, ds.successTime);
            pw.print(" avg=");
            dumpTimeSec(pw, ds.successTime / ds.successCount);
        }
        pw.print(") Failure (");
        pw.print(ds.failureCount);
        if (ds.failureCount > 0) {
            pw.print(" for ");
            dumpTimeSec(pw, ds.failureTime);
            pw.print(" avg=");
            dumpTimeSec(pw, ds.failureTime / ds.failureCount);
        }
        pw.println(Separators.RPAREN);
    }

    protected void dumpSyncHistory(PrintWriter pw) {
        dumpRecentHistory(pw);
        dumpDayStatistics(pw);
    }

    private void dumpRecentHistory(PrintWriter pw) {
        String authorityName;
        String accountKey;
        String authorityName2;
        String accountKey2;
        String diffString;
        String authorityName3;
        String accountKey3;
        ArrayList<SyncStorageEngine.SyncHistoryItem> items = this.mSyncStorageEngine.getSyncHistory();
        if (items != null && items.size() > 0) {
            Map<String, AuthoritySyncStats> authorityMap = Maps.newHashMap();
            long totalElapsedTime = 0;
            long totalTimes = 0;
            int N = items.size();
            int maxAuthority = 0;
            int maxAccount = 0;
            Iterator i$ = items.iterator();
            while (i$.hasNext()) {
                SyncStorageEngine.SyncHistoryItem item = i$.next();
                SyncStorageEngine.AuthorityInfo authority = this.mSyncStorageEngine.getAuthority(item.authorityId);
                if (authority != null) {
                    authorityName3 = authority.authority;
                    accountKey3 = authority.account.name + Separators.SLASH + authority.account.type + " u" + authority.userId;
                } else {
                    authorityName3 = SubscriptionStateHeader.UNKNOWN;
                    accountKey3 = SubscriptionStateHeader.UNKNOWN;
                }
                int length = authorityName3.length();
                if (length > maxAuthority) {
                    maxAuthority = length;
                }
                int length2 = accountKey3.length();
                if (length2 > maxAccount) {
                    maxAccount = length2;
                }
                long elapsedTime = item.elapsedTime;
                totalElapsedTime += elapsedTime;
                totalTimes++;
                AuthoritySyncStats authoritySyncStats = authorityMap.get(authorityName3);
                if (authoritySyncStats == null) {
                    authoritySyncStats = new AuthoritySyncStats(authorityName3);
                    authorityMap.put(authorityName3, authoritySyncStats);
                }
                authoritySyncStats.elapsedTime += elapsedTime;
                authoritySyncStats.times++;
                Map<String, AccountSyncStats> accountMap = authoritySyncStats.accountMap;
                AccountSyncStats accountSyncStats = accountMap.get(accountKey3);
                if (accountSyncStats == null) {
                    accountSyncStats = new AccountSyncStats(accountKey3);
                    accountMap.put(accountKey3, accountSyncStats);
                }
                accountSyncStats.elapsedTime += elapsedTime;
                accountSyncStats.times++;
            }
            if (totalElapsedTime > 0) {
                pw.println();
                pw.printf("Detailed Statistics (Recent history):  %d (# of times) %ds (sync time)\n", Long.valueOf(totalTimes), Long.valueOf(totalElapsedTime / 1000));
                List<AuthoritySyncStats> sortedAuthorities = new ArrayList<>(authorityMap.values());
                Collections.sort(sortedAuthorities, new Comparator<AuthoritySyncStats>() { // from class: com.android.server.content.SyncManager.12
                    @Override // java.util.Comparator
                    public int compare(AuthoritySyncStats lhs, AuthoritySyncStats rhs) {
                        int compare = Integer.compare(rhs.times, lhs.times);
                        if (compare == 0) {
                            compare = Long.compare(rhs.elapsedTime, lhs.elapsedTime);
                        }
                        return compare;
                    }
                });
                int maxLength = Math.max(maxAuthority, maxAccount + 3);
                int padLength = 4 + maxLength + 2 + 10 + 11;
                char[] chars = new char[padLength];
                Arrays.fill(chars, '-');
                String separator = new String(chars);
                String authorityFormat = String.format("  %%-%ds: %%-9s  %%-11s\n", Integer.valueOf(maxLength + 2));
                String accountFormat = String.format("    %%-%ds:   %%-9s  %%-11s\n", Integer.valueOf(maxLength));
                pw.println(separator);
                for (AuthoritySyncStats authoritySyncStats2 : sortedAuthorities) {
                    String name = authoritySyncStats2.name;
                    long elapsedTime2 = authoritySyncStats2.elapsedTime;
                    int times = authoritySyncStats2.times;
                    String timeStr = String.format("%ds/%d%%", Long.valueOf(elapsedTime2 / 1000), Long.valueOf((elapsedTime2 * 100) / totalElapsedTime));
                    String timesStr = String.format("%d/%d%%", Integer.valueOf(times), Long.valueOf((times * 100) / totalTimes));
                    pw.printf(authorityFormat, name, timesStr, timeStr);
                    List<AccountSyncStats> sortedAccounts = new ArrayList<>(authoritySyncStats2.accountMap.values());
                    Collections.sort(sortedAccounts, new Comparator<AccountSyncStats>() { // from class: com.android.server.content.SyncManager.13
                        @Override // java.util.Comparator
                        public int compare(AccountSyncStats lhs, AccountSyncStats rhs) {
                            int compare = Integer.compare(rhs.times, lhs.times);
                            if (compare == 0) {
                                compare = Long.compare(rhs.elapsedTime, lhs.elapsedTime);
                            }
                            return compare;
                        }
                    });
                    for (AccountSyncStats stats : sortedAccounts) {
                        long elapsedTime3 = stats.elapsedTime;
                        int times2 = stats.times;
                        String timeStr2 = String.format("%ds/%d%%", Long.valueOf(elapsedTime3 / 1000), Long.valueOf((elapsedTime3 * 100) / totalElapsedTime));
                        String timesStr2 = String.format("%d/%d%%", Integer.valueOf(times2), Long.valueOf((times2 * 100) / totalTimes));
                        pw.printf(accountFormat, stats.name, timesStr2, timeStr2);
                    }
                    pw.println(separator);
                }
            }
            pw.println();
            pw.println("Recent Sync History");
            String format = "  %-" + maxAccount + "s  %-" + maxAuthority + "s %s\n";
            Map<String, Long> lastTimeMap = Maps.newHashMap();
            PackageManager pm = this.mContext.getPackageManager();
            for (int i = 0; i < N; i++) {
                SyncStorageEngine.SyncHistoryItem item2 = items.get(i);
                SyncStorageEngine.AuthorityInfo authority2 = this.mSyncStorageEngine.getAuthority(item2.authorityId);
                if (authority2 != null) {
                    authorityName2 = authority2.authority;
                    accountKey2 = authority2.account.name + Separators.SLASH + authority2.account.type + " u" + authority2.userId;
                } else {
                    authorityName2 = SubscriptionStateHeader.UNKNOWN;
                    accountKey2 = SubscriptionStateHeader.UNKNOWN;
                }
                long elapsedTime4 = item2.elapsedTime;
                Time time = new Time();
                long eventTime = item2.eventTime;
                time.set(eventTime);
                String key = authorityName2 + Separators.SLASH + accountKey2;
                Long lastEventTime = lastTimeMap.get(key);
                if (lastEventTime == null) {
                    diffString = "";
                } else {
                    long diff = (lastEventTime.longValue() - eventTime) / 1000;
                    if (diff < 60) {
                        diffString = String.valueOf(diff);
                    } else if (diff < DEFAULT_MAX_SYNC_RETRY_TIME_IN_SECONDS) {
                        diffString = String.format("%02d:%02d", Long.valueOf(diff / 60), Long.valueOf(diff % 60));
                    } else {
                        long sec = diff % DEFAULT_MAX_SYNC_RETRY_TIME_IN_SECONDS;
                        diffString = String.format("%02d:%02d:%02d", Long.valueOf(diff / DEFAULT_MAX_SYNC_RETRY_TIME_IN_SECONDS), Long.valueOf(sec / 60), Long.valueOf(sec % 60));
                    }
                }
                lastTimeMap.put(key, Long.valueOf(eventTime));
                pw.printf("  #%-3d: %s %8s  %5.1fs  %8s", Integer.valueOf(i + 1), formatTime(eventTime), SyncStorageEngine.SOURCES[item2.source], Float.valueOf(((float) elapsedTime4) / 1000.0f), diffString);
                pw.printf(format, accountKey2, authorityName2, SyncOperation.reasonToString(pm, item2.reason));
                if (item2.event != 1 || item2.upstreamActivity != 0 || item2.downstreamActivity != 0) {
                    pw.printf("    event=%d upstreamActivity=%d downstreamActivity=%d\n", Integer.valueOf(item2.event), Long.valueOf(item2.upstreamActivity), Long.valueOf(item2.downstreamActivity));
                }
                if (item2.mesg != null && !SyncStorageEngine.MESG_SUCCESS.equals(item2.mesg)) {
                    pw.printf("    mesg=%s\n", item2.mesg);
                }
            }
            pw.println();
            pw.println("Recent Sync History Extras");
            for (int i2 = 0; i2 < N; i2++) {
                SyncStorageEngine.SyncHistoryItem item3 = items.get(i2);
                Bundle extras = item3.extras;
                if (extras != null && extras.size() != 0) {
                    SyncStorageEngine.AuthorityInfo authority3 = this.mSyncStorageEngine.getAuthority(item3.authorityId);
                    if (authority3 != null) {
                        authorityName = authority3.authority;
                        accountKey = authority3.account.name + Separators.SLASH + authority3.account.type + " u" + authority3.userId;
                    } else {
                        authorityName = SubscriptionStateHeader.UNKNOWN;
                        accountKey = SubscriptionStateHeader.UNKNOWN;
                    }
                    Time time2 = new Time();
                    long eventTime2 = item3.eventTime;
                    time2.set(eventTime2);
                    pw.printf("  #%-3d: %s %8s ", Integer.valueOf(i2 + 1), formatTime(eventTime2), SyncStorageEngine.SOURCES[item3.source]);
                    pw.printf(format, accountKey, authorityName, extras);
                }
            }
        }
    }

    private void dumpDayStatistics(PrintWriter pw) {
        SyncStorageEngine.DayStats ds;
        int delta;
        SyncStorageEngine.DayStats[] dses = this.mSyncStorageEngine.getDayStatistics();
        if (dses != null && dses[0] != null) {
            pw.println();
            pw.println("Sync Statistics");
            pw.print("  Today:  ");
            dumpDayStatistic(pw, dses[0]);
            int today = dses[0].day;
            int i = 1;
            while (i <= 6 && i < dses.length && (ds = dses[i]) != null && (delta = today - ds.day) <= 6) {
                pw.print("  Day-");
                pw.print(delta);
                pw.print(":  ");
                dumpDayStatistic(pw, ds);
                i++;
            }
            int weekDay = today;
            while (i < dses.length) {
                SyncStorageEngine.DayStats aggr = null;
                weekDay -= 7;
                while (true) {
                    if (i >= dses.length) {
                        break;
                    }
                    SyncStorageEngine.DayStats ds2 = dses[i];
                    if (ds2 == null) {
                        i = dses.length;
                        break;
                    } else if (weekDay - ds2.day > 6) {
                        break;
                    } else {
                        i++;
                        if (aggr == null) {
                            aggr = new SyncStorageEngine.DayStats(weekDay);
                        }
                        aggr.successCount += ds2.successCount;
                        aggr.successTime += ds2.successTime;
                        aggr.failureCount += ds2.failureCount;
                        aggr.failureTime += ds2.failureTime;
                    }
                }
                if (aggr != null) {
                    pw.print("  Week-");
                    pw.print((today - weekDay) / 7);
                    pw.print(": ");
                    dumpDayStatistic(pw, aggr);
                }
            }
        }
    }

    private void dumpSyncAdapters(IndentingPrintWriter pw) {
        pw.println();
        List<UserInfo> users = getAllUsers();
        if (users != null) {
            for (UserInfo user : users) {
                pw.println("Sync adapters for " + user + Separators.COLON);
                pw.increaseIndent();
                for (RegisteredServicesCache.ServiceInfo<SyncAdapterType> info : this.mSyncAdapters.getAllServices(user.id)) {
                    pw.println(info);
                }
                pw.decreaseIndent();
                pw.println();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SyncManager$AuthoritySyncStats.class */
    public static class AuthoritySyncStats {
        String name;
        long elapsedTime;
        int times;
        Map<String, AccountSyncStats> accountMap;

        private AuthoritySyncStats(String name) {
            this.accountMap = Maps.newHashMap();
            this.name = name;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SyncManager$AccountSyncStats.class */
    public static class AccountSyncStats {
        String name;
        long elapsedTime;
        int times;

        private AccountSyncStats(String name) {
            this.name = name;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SyncManager$SyncTimeTracker.class */
    public class SyncTimeTracker {
        boolean mLastWasSyncing;
        long mWhenSyncStarted;
        private long mTimeSpentSyncing;

        private SyncTimeTracker() {
            this.mLastWasSyncing = false;
            this.mWhenSyncStarted = 0L;
        }

        public synchronized void update() {
            boolean isSyncInProgress = !SyncManager.this.mActiveSyncContexts.isEmpty();
            if (isSyncInProgress == this.mLastWasSyncing) {
                return;
            }
            long now = SystemClock.elapsedRealtime();
            if (isSyncInProgress) {
                this.mWhenSyncStarted = now;
            } else {
                this.mTimeSpentSyncing += now - this.mWhenSyncStarted;
            }
            this.mLastWasSyncing = isSyncInProgress;
        }

        public synchronized long timeSpentSyncing() {
            if (this.mLastWasSyncing) {
                long now = SystemClock.elapsedRealtime();
                return this.mTimeSpentSyncing + (now - this.mWhenSyncStarted);
            }
            return this.mTimeSpentSyncing;
        }
    }

    /* loaded from: SyncManager$ServiceConnectionData.class */
    class ServiceConnectionData {
        public final ActiveSyncContext activeSyncContext;
        public final ISyncAdapter syncAdapter;

        ServiceConnectionData(ActiveSyncContext activeSyncContext, ISyncAdapter syncAdapter) {
            this.activeSyncContext = activeSyncContext;
            this.syncAdapter = syncAdapter;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SyncManager$SyncHandler.class */
    public class SyncHandler extends Handler {
        private static final int MESSAGE_SYNC_FINISHED = 1;
        private static final int MESSAGE_SYNC_ALARM = 2;
        private static final int MESSAGE_CHECK_ALARMS = 3;
        private static final int MESSAGE_SERVICE_CONNECTED = 4;
        private static final int MESSAGE_SERVICE_DISCONNECTED = 5;
        private static final int MESSAGE_CANCEL = 6;
        public final SyncNotificationInfo mSyncNotificationInfo;
        private Long mAlarmScheduleTime;
        public final SyncTimeTracker mSyncTimeTracker;
        private final HashMap<Pair<Account, String>, PowerManager.WakeLock> mWakeLocks;
        private List<Message> mBootQueue;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncManager.SyncHandler.handleMessage(android.os.Message):void, file: SyncManager$SyncHandler.class
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
        @Override // android.os.Handler
        public void handleMessage(android.os.Message r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncManager.SyncHandler.handleMessage(android.os.Message):void, file: SyncManager$SyncHandler.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncManager.SyncHandler.handleMessage(android.os.Message):void");
        }

        public void onBootCompleted() {
            if (Log.isLoggable(SyncManager.TAG, 2)) {
                Log.v(SyncManager.TAG, "Boot completed, clearing boot queue.");
            }
            SyncManager.this.doDatabaseCleanup();
            synchronized (this) {
                for (Message message : this.mBootQueue) {
                    sendMessage(message);
                }
                this.mBootQueue = null;
                SyncManager.this.mBootCompleted = true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public PowerManager.WakeLock getSyncWakeLock(Account account, String authority) {
            Pair<Account, String> wakeLockKey = Pair.create(account, authority);
            PowerManager.WakeLock wakeLock = this.mWakeLocks.get(wakeLockKey);
            if (wakeLock == null) {
                String name = "*sync*/" + authority + Separators.SLASH + account.type + Separators.SLASH + account.name;
                wakeLock = SyncManager.this.mPowerManager.newWakeLock(1, name);
                wakeLock.setReferenceCounted(false);
                this.mWakeLocks.put(wakeLockKey, wakeLock);
            }
            return wakeLock;
        }

        private boolean tryEnqueueMessageUntilReadyToRun(Message msg) {
            synchronized (this) {
                if (!SyncManager.this.mBootCompleted) {
                    this.mBootQueue.add(Message.obtain(msg));
                    return true;
                }
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: SyncManager$SyncHandler$SyncNotificationInfo.class */
        public class SyncNotificationInfo {
            public boolean isActive = false;
            public Long startTime = null;

            SyncNotificationInfo() {
            }

            public void toString(StringBuilder sb) {
                sb.append("isActive ").append(this.isActive).append(", startTime ").append(this.startTime);
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                toString(sb);
                return sb.toString();
            }
        }

        public SyncHandler(Looper looper) {
            super(looper);
            this.mSyncNotificationInfo = new SyncNotificationInfo();
            this.mAlarmScheduleTime = null;
            this.mSyncTimeTracker = new SyncTimeTracker();
            this.mWakeLocks = Maps.newHashMap();
            this.mBootQueue = new ArrayList();
        }

        private long scheduleReadyPeriodicSyncs() {
            long nextPollTimeAbsolute;
            boolean isLoggable = Log.isLoggable(SyncManager.TAG, 2);
            if (isLoggable) {
                Log.v(SyncManager.TAG, "scheduleReadyPeriodicSyncs");
            }
            boolean backgroundDataUsageAllowed = SyncManager.this.getConnectivityManager().getBackgroundDataSetting();
            long earliestFuturePollTime = Long.MAX_VALUE;
            if (backgroundDataUsageAllowed) {
                AccountAndUser[] accounts = SyncManager.this.mRunningAccounts;
                long nowAbsolute = System.currentTimeMillis();
                long shiftedNowAbsolute = 0 < nowAbsolute - ((long) SyncManager.this.mSyncRandomOffsetMillis) ? nowAbsolute - SyncManager.this.mSyncRandomOffsetMillis : 0L;
                ArrayList<Pair<SyncStorageEngine.AuthorityInfo, SyncStatusInfo>> infos = SyncManager.this.mSyncStorageEngine.getCopyOfAllAuthoritiesWithSyncStatus();
                Iterator i$ = infos.iterator();
                while (i$.hasNext()) {
                    Pair<SyncStorageEngine.AuthorityInfo, SyncStatusInfo> info = i$.next();
                    SyncStorageEngine.AuthorityInfo authorityInfo = info.first;
                    SyncStatusInfo status = info.second;
                    if (!TextUtils.isEmpty(authorityInfo.authority)) {
                        if (SyncManager.this.containsAccountAndUser(accounts, authorityInfo.account, authorityInfo.userId) && SyncManager.this.mSyncStorageEngine.getMasterSyncAutomatically(authorityInfo.userId) && SyncManager.this.mSyncStorageEngine.getSyncAutomatically(authorityInfo.account, authorityInfo.userId, authorityInfo.authority) && SyncManager.this.getIsSyncable(authorityInfo.account, authorityInfo.userId, authorityInfo.authority) != 0) {
                            int N = authorityInfo.periodicSyncs.size();
                            for (int i = 0; i < N; i++) {
                                PeriodicSync sync = authorityInfo.periodicSyncs.get(i);
                                Bundle extras = sync.extras;
                                long periodInMillis = sync.period * 1000;
                                long flexInMillis = sync.flexTime * 1000;
                                if (periodInMillis > 0) {
                                    long lastPollTimeAbsolute = status.getPeriodicSyncTime(i);
                                    long remainingMillis = periodInMillis - (shiftedNowAbsolute % periodInMillis);
                                    long timeSinceLastRunMillis = nowAbsolute - lastPollTimeAbsolute;
                                    boolean runEarly = remainingMillis <= flexInMillis && timeSinceLastRunMillis > periodInMillis - flexInMillis;
                                    if (isLoggable) {
                                        Log.v(SyncManager.TAG, "sync: " + i + " for " + authorityInfo.authority + Separators.DOT + " period: " + periodInMillis + " flex: " + flexInMillis + " remaining: " + remainingMillis + " time_since_last: " + timeSinceLastRunMillis + " last poll absol: " + lastPollTimeAbsolute + " shifted now: " + shiftedNowAbsolute + " run_early: " + runEarly);
                                    }
                                    if (runEarly || remainingMillis == periodInMillis || lastPollTimeAbsolute > nowAbsolute || timeSinceLastRunMillis >= periodInMillis) {
                                        Pair<Long, Long> backoff = SyncManager.this.mSyncStorageEngine.getBackoff(authorityInfo.account, authorityInfo.userId, authorityInfo.authority);
                                        RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterInfo = SyncManager.this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(authorityInfo.authority, authorityInfo.account.type), authorityInfo.userId);
                                        if (syncAdapterInfo != null) {
                                            SyncManager.this.mSyncStorageEngine.setPeriodicSyncTime(authorityInfo.ident, authorityInfo.periodicSyncs.get(i), nowAbsolute);
                                            SyncManager.this.scheduleSyncOperation(new SyncOperation(authorityInfo.account, authorityInfo.userId, -4, 4, authorityInfo.authority, extras, 0L, 0L, backoff != null ? backoff.first.longValue() : 0L, SyncManager.this.mSyncStorageEngine.getDelayUntilTime(authorityInfo.account, authorityInfo.userId, authorityInfo.authority), syncAdapterInfo.type.allowParallelSyncs()));
                                        }
                                    }
                                    if (runEarly) {
                                        nextPollTimeAbsolute = nowAbsolute + periodInMillis + remainingMillis;
                                    } else {
                                        nextPollTimeAbsolute = nowAbsolute + remainingMillis;
                                    }
                                    if (nextPollTimeAbsolute < earliestFuturePollTime) {
                                        earliestFuturePollTime = nextPollTimeAbsolute;
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e(SyncManager.TAG, "Got an empty provider string. Skipping: " + authorityInfo);
                    }
                }
                if (earliestFuturePollTime == Long.MAX_VALUE) {
                    return Long.MAX_VALUE;
                }
                return SystemClock.elapsedRealtime() + (earliestFuturePollTime < nowAbsolute ? 0L : earliestFuturePollTime - nowAbsolute);
            }
            return Long.MAX_VALUE;
        }

        /* JADX WARN: Removed duplicated region for block: B:197:0x063c  */
        /* JADX WARN: Removed duplicated region for block: B:212:0x065a A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private long maybeStartNextSyncLocked() {
            /*
                Method dump skipped, instructions count: 1668
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncManager.SyncHandler.maybeStartNextSyncLocked():long");
        }

        private boolean dispatchSyncOperation(SyncOperation op) {
            if (Log.isLoggable(SyncManager.TAG, 2)) {
                Log.v(SyncManager.TAG, "dispatchSyncOperation: we are going to sync " + op);
                Log.v(SyncManager.TAG, "num active syncs: " + SyncManager.this.mActiveSyncContexts.size());
                Iterator i$ = SyncManager.this.mActiveSyncContexts.iterator();
                while (i$.hasNext()) {
                    ActiveSyncContext syncContext = i$.next();
                    Log.v(SyncManager.TAG, syncContext.toString());
                }
            }
            SyncAdapterType syncAdapterType = SyncAdapterType.newKey(op.authority, op.account.type);
            RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterInfo = SyncManager.this.mSyncAdapters.getServiceInfo(syncAdapterType, op.userId);
            if (syncAdapterInfo == null) {
                Log.d(SyncManager.TAG, "can't find a sync adapter for " + syncAdapterType + ", removing settings for it");
                SyncManager.this.mSyncStorageEngine.removeAuthority(op.account, op.userId, op.authority);
                return false;
            }
            ActiveSyncContext activeSyncContext = new ActiveSyncContext(op, insertStartSyncEvent(op), syncAdapterInfo.uid);
            activeSyncContext.mSyncInfo = SyncManager.this.mSyncStorageEngine.addActiveSync(activeSyncContext);
            SyncManager.this.mActiveSyncContexts.add(activeSyncContext);
            if (Log.isLoggable(SyncManager.TAG, 2)) {
                Log.v(SyncManager.TAG, "dispatchSyncOperation: starting " + activeSyncContext);
            }
            if (!activeSyncContext.bindToSyncAdapter(syncAdapterInfo, op.userId)) {
                Log.e(SyncManager.TAG, "Bind attempt failed to " + syncAdapterInfo);
                closeActiveSyncContext(activeSyncContext);
                return false;
            }
            return true;
        }

        private void runBoundToSyncAdapter(ActiveSyncContext activeSyncContext, ISyncAdapter syncAdapter) {
            activeSyncContext.mSyncAdapter = syncAdapter;
            SyncOperation syncOperation = activeSyncContext.mSyncOperation;
            try {
                activeSyncContext.mIsLinkedToDeath = true;
                syncAdapter.asBinder().linkToDeath(activeSyncContext, 0);
                syncAdapter.startSync(activeSyncContext, syncOperation.authority, syncOperation.account, syncOperation.extras);
            } catch (RemoteException remoteExc) {
                Log.d(SyncManager.TAG, "maybeStartNextSync: caught a RemoteException, rescheduling", remoteExc);
                closeActiveSyncContext(activeSyncContext);
                SyncManager.this.increaseBackoffSetting(syncOperation);
                SyncManager.this.scheduleSyncOperation(new SyncOperation(syncOperation));
            } catch (RuntimeException exc) {
                closeActiveSyncContext(activeSyncContext);
                Log.e(SyncManager.TAG, "Caught RuntimeException while starting the sync " + syncOperation, exc);
            }
        }

        private void cancelActiveSyncLocked(Account account, int userId, String authority) {
            ArrayList<ActiveSyncContext> activeSyncs = new ArrayList<>(SyncManager.this.mActiveSyncContexts);
            Iterator i$ = activeSyncs.iterator();
            while (i$.hasNext()) {
                ActiveSyncContext activeSyncContext = i$.next();
                if (activeSyncContext != null && (account == null || account.equals(activeSyncContext.mSyncOperation.account))) {
                    if (authority == null || authority.equals(activeSyncContext.mSyncOperation.authority)) {
                        if (userId == -1 || userId == activeSyncContext.mSyncOperation.userId) {
                            runSyncFinishedOrCanceledLocked(null, activeSyncContext);
                        }
                    }
                }
            }
        }

        private void runSyncFinishedOrCanceledLocked(SyncResult syncResult, ActiveSyncContext activeSyncContext) {
            String historyMessage;
            int downstreamActivity;
            int upstreamActivity;
            boolean isLoggable = Log.isLoggable(SyncManager.TAG, 2);
            if (activeSyncContext.mIsLinkedToDeath) {
                activeSyncContext.mSyncAdapter.asBinder().unlinkToDeath(activeSyncContext, 0);
                activeSyncContext.mIsLinkedToDeath = false;
            }
            closeActiveSyncContext(activeSyncContext);
            SyncOperation syncOperation = activeSyncContext.mSyncOperation;
            long elapsedTime = SystemClock.elapsedRealtime() - activeSyncContext.mStartTime;
            if (syncResult != null) {
                if (isLoggable) {
                    Log.v(SyncManager.TAG, "runSyncFinishedOrCanceled [finished]: " + syncOperation + ", result " + syncResult);
                }
                if (!syncResult.hasError()) {
                    historyMessage = SyncStorageEngine.MESG_SUCCESS;
                    downstreamActivity = 0;
                    upstreamActivity = 0;
                    SyncManager.this.clearBackoffSetting(syncOperation);
                } else {
                    Log.d(SyncManager.TAG, "failed sync operation " + syncOperation + ", " + syncResult);
                    if (!syncResult.syncAlreadyInProgress) {
                        SyncManager.this.increaseBackoffSetting(syncOperation);
                    }
                    SyncManager.this.maybeRescheduleSync(syncResult, syncOperation);
                    historyMessage = ContentResolver.syncErrorToString(syncResultToErrorNumber(syncResult));
                    downstreamActivity = 0;
                    upstreamActivity = 0;
                }
                SyncManager.this.setDelayUntilTime(syncOperation, syncResult.delayUntil);
            } else {
                if (isLoggable) {
                    Log.v(SyncManager.TAG, "runSyncFinishedOrCanceled [canceled]: " + syncOperation);
                }
                if (activeSyncContext.mSyncAdapter != null) {
                    try {
                        activeSyncContext.mSyncAdapter.cancelSync(activeSyncContext);
                    } catch (RemoteException e) {
                    }
                }
                historyMessage = SyncStorageEngine.MESG_CANCELED;
                downstreamActivity = 0;
                upstreamActivity = 0;
            }
            stopSyncEvent(activeSyncContext.mHistoryRowId, syncOperation, historyMessage, upstreamActivity, downstreamActivity, elapsedTime);
            if (syncResult == null || !syncResult.tooManyDeletions) {
                SyncManager.this.mNotificationMgr.cancelAsUser(null, syncOperation.account.hashCode() ^ syncOperation.authority.hashCode(), new UserHandle(syncOperation.userId));
            } else {
                installHandleTooManyDeletesNotification(syncOperation.account, syncOperation.authority, syncResult.stats.numDeletes, syncOperation.userId);
            }
            if (syncResult != null && syncResult.fullSyncRequested) {
                SyncManager.this.scheduleSyncOperation(new SyncOperation(syncOperation.account, syncOperation.userId, syncOperation.reason, syncOperation.syncSource, syncOperation.authority, new Bundle(), 0L, 0L, syncOperation.backoff.longValue(), syncOperation.delayUntil, syncOperation.allowParallelSyncs));
            }
        }

        private void closeActiveSyncContext(ActiveSyncContext activeSyncContext) {
            activeSyncContext.close();
            SyncManager.this.mActiveSyncContexts.remove(activeSyncContext);
            SyncManager.this.mSyncStorageEngine.removeActiveSync(activeSyncContext.mSyncInfo, activeSyncContext.mSyncOperation.userId);
        }

        private int syncResultToErrorNumber(SyncResult syncResult) {
            if (syncResult.syncAlreadyInProgress) {
                return 1;
            }
            if (syncResult.stats.numAuthExceptions > 0) {
                return 2;
            }
            if (syncResult.stats.numIoExceptions > 0) {
                return 3;
            }
            if (syncResult.stats.numParseExceptions > 0) {
                return 4;
            }
            if (syncResult.stats.numConflictDetectedExceptions > 0) {
                return 5;
            }
            if (syncResult.tooManyDeletions) {
                return 6;
            }
            if (syncResult.tooManyRetries) {
                return 7;
            }
            if (syncResult.databaseError) {
                return 8;
            }
            throw new IllegalStateException("we are not in an error state, " + syncResult);
        }

        private void manageSyncNotificationLocked() {
            boolean shouldCancel;
            boolean shouldInstall;
            if (SyncManager.this.mActiveSyncContexts.isEmpty()) {
                this.mSyncNotificationInfo.startTime = null;
                shouldCancel = this.mSyncNotificationInfo.isActive;
                shouldInstall = false;
            } else {
                long now = SystemClock.elapsedRealtime();
                if (this.mSyncNotificationInfo.startTime == null) {
                    this.mSyncNotificationInfo.startTime = Long.valueOf(now);
                }
                if (this.mSyncNotificationInfo.isActive) {
                    shouldCancel = false;
                    shouldInstall = false;
                } else {
                    shouldCancel = false;
                    boolean timeToShowNotification = now > this.mSyncNotificationInfo.startTime.longValue() + SyncManager.SYNC_NOTIFICATION_DELAY;
                    if (timeToShowNotification) {
                        shouldInstall = true;
                    } else {
                        shouldInstall = false;
                        Iterator i$ = SyncManager.this.mActiveSyncContexts.iterator();
                        while (true) {
                            if (!i$.hasNext()) {
                                break;
                            }
                            ActiveSyncContext activeSyncContext = i$.next();
                            boolean manualSync = activeSyncContext.mSyncOperation.extras.getBoolean("force", false);
                            if (manualSync) {
                                shouldInstall = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (shouldCancel && !shouldInstall) {
                SyncManager.this.mNeedSyncActiveNotification = false;
                sendSyncStateIntent();
                this.mSyncNotificationInfo.isActive = false;
            }
            if (shouldInstall) {
                SyncManager.this.mNeedSyncActiveNotification = true;
                sendSyncStateIntent();
                this.mSyncNotificationInfo.isActive = true;
            }
        }

        private void manageSyncAlarmLocked(long nextPeriodicEventElapsedTime, long nextPendingEventElapsedTime) {
            if (SyncManager.this.mDataConnectionIsConnected && !SyncManager.this.mStorageIsLow) {
                long notificationTime = (SyncManager.this.mSyncHandler.mSyncNotificationInfo.isActive || SyncManager.this.mSyncHandler.mSyncNotificationInfo.startTime == null) ? Long.MAX_VALUE : SyncManager.this.mSyncHandler.mSyncNotificationInfo.startTime.longValue() + SyncManager.SYNC_NOTIFICATION_DELAY;
                long earliestTimeoutTime = Long.MAX_VALUE;
                Iterator i$ = SyncManager.this.mActiveSyncContexts.iterator();
                while (i$.hasNext()) {
                    ActiveSyncContext currentSyncContext = i$.next();
                    long currentSyncTimeoutTime = currentSyncContext.mTimeoutStartTime + SyncManager.MAX_TIME_PER_SYNC;
                    if (Log.isLoggable(SyncManager.TAG, 2)) {
                        Log.v(SyncManager.TAG, "manageSyncAlarm: active sync, mTimeoutStartTime + MAX is " + currentSyncTimeoutTime);
                    }
                    if (earliestTimeoutTime > currentSyncTimeoutTime) {
                        earliestTimeoutTime = currentSyncTimeoutTime;
                    }
                }
                if (Log.isLoggable(SyncManager.TAG, 2)) {
                    Log.v(SyncManager.TAG, "manageSyncAlarm: notificationTime is " + notificationTime);
                }
                if (Log.isLoggable(SyncManager.TAG, 2)) {
                    Log.v(SyncManager.TAG, "manageSyncAlarm: earliestTimeoutTime is " + earliestTimeoutTime);
                }
                if (Log.isLoggable(SyncManager.TAG, 2)) {
                    Log.v(SyncManager.TAG, "manageSyncAlarm: nextPeriodicEventElapsedTime is " + nextPeriodicEventElapsedTime);
                }
                if (Log.isLoggable(SyncManager.TAG, 2)) {
                    Log.v(SyncManager.TAG, "manageSyncAlarm: nextPendingEventElapsedTime is " + nextPendingEventElapsedTime);
                }
                long alarmTime = Math.min(Math.min(Math.min(notificationTime, earliestTimeoutTime), nextPeriodicEventElapsedTime), nextPendingEventElapsedTime);
                long now = SystemClock.elapsedRealtime();
                if (alarmTime < now + LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS) {
                    if (Log.isLoggable(SyncManager.TAG, 2)) {
                        Log.v(SyncManager.TAG, "manageSyncAlarm: the alarmTime is too small, " + alarmTime + ", setting to " + (now + LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS));
                    }
                    alarmTime = now + LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS;
                } else if (alarmTime > now + SyncManager.SYNC_ALARM_TIMEOUT_MAX) {
                    if (Log.isLoggable(SyncManager.TAG, 2)) {
                        Log.v(SyncManager.TAG, "manageSyncAlarm: the alarmTime is too large, " + alarmTime + ", setting to " + (now + LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS));
                    }
                    alarmTime = now + SyncManager.SYNC_ALARM_TIMEOUT_MAX;
                }
                boolean shouldSet = false;
                boolean shouldCancel = false;
                boolean alarmIsActive = this.mAlarmScheduleTime != null && now < this.mAlarmScheduleTime.longValue();
                boolean needAlarm = alarmTime != Long.MAX_VALUE;
                if (needAlarm) {
                    if (!alarmIsActive || alarmTime < this.mAlarmScheduleTime.longValue()) {
                        shouldSet = true;
                    }
                } else {
                    shouldCancel = alarmIsActive;
                }
                SyncManager.this.ensureAlarmService();
                if (shouldSet) {
                    if (Log.isLoggable(SyncManager.TAG, 2)) {
                        Log.v(SyncManager.TAG, "requesting that the alarm manager wake us up at elapsed time " + alarmTime + ", now is " + now + ", " + ((alarmTime - now) / 1000) + " secs from now");
                    }
                    this.mAlarmScheduleTime = Long.valueOf(alarmTime);
                    SyncManager.this.mAlarmService.setExact(2, alarmTime, SyncManager.this.mSyncAlarmIntent);
                } else if (shouldCancel) {
                    this.mAlarmScheduleTime = null;
                    SyncManager.this.mAlarmService.cancel(SyncManager.this.mSyncAlarmIntent);
                }
            }
        }

        private void sendSyncStateIntent() {
            Intent syncStateIntent = new Intent(Intent.ACTION_SYNC_STATE_CHANGED);
            syncStateIntent.addFlags(67108864);
            syncStateIntent.putExtra("active", SyncManager.this.mNeedSyncActiveNotification);
            syncStateIntent.putExtra("failing", false);
            SyncManager.this.mContext.sendBroadcastAsUser(syncStateIntent, UserHandle.OWNER);
        }

        private void installHandleTooManyDeletesNotification(Account account, String authority, long numDeletes, int userId) {
            ProviderInfo providerInfo;
            if (SyncManager.this.mNotificationMgr == null || (providerInfo = SyncManager.this.mContext.getPackageManager().resolveContentProvider(authority, 0)) == null) {
                return;
            }
            CharSequence authorityName = providerInfo.loadLabel(SyncManager.this.mContext.getPackageManager());
            Intent clickIntent = new Intent(SyncManager.this.mContext, SyncActivityTooManyDeletes.class);
            clickIntent.putExtra("account", account);
            clickIntent.putExtra(ContactsContract.Directory.DIRECTORY_AUTHORITY, authority);
            clickIntent.putExtra("provider", authorityName.toString());
            clickIntent.putExtra("numDeletes", numDeletes);
            if (isActivityAvailable(clickIntent)) {
                PendingIntent pendingIntent = PendingIntent.getActivityAsUser(SyncManager.this.mContext, 0, clickIntent, 268435456, null, new UserHandle(userId));
                CharSequence tooManyDeletesDescFormat = SyncManager.this.mContext.getResources().getText(R.string.contentServiceTooManyDeletesNotificationDesc);
                Notification notification = new Notification(R.drawable.stat_notify_sync_error, SyncManager.this.mContext.getString(R.string.contentServiceSync), System.currentTimeMillis());
                notification.setLatestEventInfo(SyncManager.this.mContext, SyncManager.this.mContext.getString(R.string.contentServiceSyncNotificationTitle), String.format(tooManyDeletesDescFormat.toString(), authorityName), pendingIntent);
                notification.flags |= 2;
                SyncManager.this.mNotificationMgr.notifyAsUser(null, account.hashCode() ^ authority.hashCode(), notification, new UserHandle(userId));
                return;
            }
            Log.w(SyncManager.TAG, "No activity found to handle too many deletes.");
        }

        private boolean isActivityAvailable(Intent intent) {
            PackageManager pm = SyncManager.this.mContext.getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
            int listSize = list.size();
            for (int i = 0; i < listSize; i++) {
                ResolveInfo resolveInfo = list.get(i);
                if ((resolveInfo.activityInfo.applicationInfo.flags & 1) != 0) {
                    return true;
                }
            }
            return false;
        }

        public long insertStartSyncEvent(SyncOperation syncOperation) {
            int source = syncOperation.syncSource;
            long now = System.currentTimeMillis();
            EventLog.writeEvent(2720, syncOperation.authority, 0, Integer.valueOf(source), Integer.valueOf(syncOperation.account.name.hashCode()));
            return SyncManager.this.mSyncStorageEngine.insertStartSyncEvent(syncOperation.account, syncOperation.userId, syncOperation.reason, syncOperation.authority, now, source, syncOperation.isInitialization(), syncOperation.extras);
        }

        public void stopSyncEvent(long rowId, SyncOperation syncOperation, String resultMessage, int upstreamActivity, int downstreamActivity, long elapsedTime) {
            EventLog.writeEvent(2720, syncOperation.authority, 1, Integer.valueOf(syncOperation.syncSource), Integer.valueOf(syncOperation.account.name.hashCode()));
            SyncManager.this.mSyncStorageEngine.stopSyncEvent(rowId, elapsedTime, resultMessage, downstreamActivity, upstreamActivity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSyncStillActive(ActiveSyncContext activeSyncContext) {
        Iterator i$ = this.mActiveSyncContexts.iterator();
        while (i$.hasNext()) {
            ActiveSyncContext sync = i$.next();
            if (sync == activeSyncContext) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SyncManager$PrintTable.class */
    public static class PrintTable {
        private ArrayList<Object[]> mTable = Lists.newArrayList();
        private final int mCols;

        PrintTable(int cols) {
            this.mCols = cols;
        }

        void set(int row, int col, Object... values) {
            if (col + values.length > this.mCols) {
                throw new IndexOutOfBoundsException("Table only has " + this.mCols + " columns. can't set " + values.length + " at column " + col);
            }
            for (int i = this.mTable.size(); i <= row; i++) {
                Object[] list = new Object[this.mCols];
                this.mTable.add(list);
                for (int j = 0; j < this.mCols; j++) {
                    list[j] = "";
                }
            }
            System.arraycopy(values, 0, this.mTable.get(row), col, values.length);
        }

        void writeTo(PrintWriter out) {
            String[] formats = new String[this.mCols];
            int totalLength = 0;
            for (int col = 0; col < this.mCols; col++) {
                int maxLength = 0;
                Iterator i$ = this.mTable.iterator();
                while (i$.hasNext()) {
                    Object[] row = i$.next();
                    int length = row[col].toString().length();
                    if (length > maxLength) {
                        maxLength = length;
                    }
                }
                totalLength += maxLength;
                formats[col] = String.format("%%-%ds", Integer.valueOf(maxLength));
            }
            printRow(out, formats, this.mTable.get(0));
            int totalLength2 = totalLength + ((this.mCols - 1) * 2);
            for (int i = 0; i < totalLength2; i++) {
                out.print("-");
            }
            out.println();
            int mTableSize = this.mTable.size();
            for (int i2 = 1; i2 < mTableSize; i2++) {
                Object[] row2 = this.mTable.get(i2);
                printRow(out, formats, row2);
            }
        }

        private void printRow(PrintWriter out, String[] formats, Object[] row) {
            int rowLength = row.length;
            for (int j = 0; j < rowLength; j++) {
                out.printf(String.format(formats[j], row[j].toString()), new Object[0]);
                out.print("  ");
            }
            out.println();
        }

        public int getNumRows() {
            return this.mTable.size();
        }
    }
}