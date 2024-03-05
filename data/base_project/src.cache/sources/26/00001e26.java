package com.android.server.content;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ISyncStatusObserver;
import android.content.PeriodicSync;
import android.content.SyncInfo;
import android.content.SyncStatusInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.android.internal.R;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.server.content.SyncManager;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: SyncStorageEngine.class */
public class SyncStorageEngine extends Handler {
    private static final String TAG = "SyncManager";
    private static final boolean DEBUG = false;
    private static final String TAG_FILE = "SyncManagerFile";
    private static final String XML_ATTR_NEXT_AUTHORITY_ID = "nextAuthorityId";
    private static final String XML_ATTR_LISTEN_FOR_TICKLES = "listen-for-tickles";
    private static final String XML_ATTR_SYNC_RANDOM_OFFSET = "offsetInSeconds";
    private static final String XML_ATTR_ENABLED = "enabled";
    private static final String XML_ATTR_USER = "user";
    private static final String XML_TAG_LISTEN_FOR_TICKLES = "listenForTickles";
    private static final long DEFAULT_POLL_FREQUENCY_SECONDS = 86400;
    private static final double DEFAULT_FLEX_PERCENT_SYNC = 0.04d;
    private static final long DEFAULT_MIN_FLEX_ALLOWED_SECS = 5;
    static final long MILLIS_IN_4WEEKS = 2419200000L;
    public static final int EVENT_START = 0;
    public static final int EVENT_STOP = 1;
    public static final int SOURCE_SERVER = 0;
    public static final int SOURCE_LOCAL = 1;
    public static final int SOURCE_POLL = 2;
    public static final int SOURCE_USER = 3;
    public static final int SOURCE_PERIODIC = 4;
    public static final long NOT_IN_BACKOFF_MODE = -1;
    public static final String MESG_SUCCESS = "success";
    public static final String MESG_CANCELED = "canceled";
    public static final int MAX_HISTORY = 100;
    private static final int MSG_WRITE_STATUS = 1;
    private static final long WRITE_STATUS_DELAY = 600000;
    private static final int MSG_WRITE_STATISTICS = 2;
    private static final long WRITE_STATISTICS_DELAY = 1800000;
    private static final boolean SYNC_ENABLED_DEFAULT = false;
    private static final int ACCOUNTS_VERSION = 2;
    private final Calendar mCal;
    private int mYear;
    private int mYearInDays;
    private final Context mContext;
    private static volatile SyncStorageEngine sSyncStorageEngine;
    private int mSyncRandomOffset;
    private final AtomicFile mAccountInfoFile;
    private final AtomicFile mStatusFile;
    private final AtomicFile mStatisticsFile;
    private final AtomicFile mPendingFile;
    private static final int PENDING_FINISH_TO_WRITE = 4;
    private boolean mDefaultMasterSyncAutomatically;
    private OnSyncRequestListener mSyncRequestListener;
    public static final int STATUS_FILE_END = 0;
    public static final int STATUS_FILE_ITEM = 100;
    public static final int PENDING_OPERATION_VERSION = 3;
    private static final String XML_ATTR_AUTHORITYID = "authority_id";
    private static final String XML_ATTR_SOURCE = "source";
    private static final String XML_ATTR_EXPEDITED = "expedited";
    private static final String XML_ATTR_REASON = "reason";
    private static final String XML_ATTR_VERSION = "version";
    public static final int STATISTICS_FILE_END = 0;
    public static final int STATISTICS_FILE_ITEM_OLD = 100;
    public static final int STATISTICS_FILE_ITEM = 101;
    public static final String[] EVENTS = {"START", "STOP"};
    public static final String[] SOURCES = {"SERVER", CalendarContract.ACCOUNT_TYPE_LOCAL, "POLL", "USER", "PERIODIC"};
    private static HashMap<String, String> sAuthorityRenames = new HashMap<>();
    private final SparseArray<AuthorityInfo> mAuthorities = new SparseArray<>();
    private final HashMap<AccountAndUser, AccountInfo> mAccounts = new HashMap<>();
    private final ArrayList<PendingOperation> mPendingOperations = new ArrayList<>();
    private final SparseArray<ArrayList<SyncInfo>> mCurrentSyncs = new SparseArray<>();
    private final SparseArray<SyncStatusInfo> mSyncStatus = new SparseArray<>();
    private final ArrayList<SyncHistoryItem> mSyncHistory = new ArrayList<>();
    private final RemoteCallbackList<ISyncStatusObserver> mChangeListeners = new RemoteCallbackList<>();
    private final HashMap<ComponentName, SparseArray<AuthorityInfo>> mServices = new HashMap<>();
    private int mNextAuthorityId = 0;
    private final DayStats[] mDayStats = new DayStats[28];
    private int mNumPendingFinished = 0;
    private int mNextHistoryId = 0;
    private SparseArray<Boolean> mMasterSyncAutomatically = new SparseArray<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SyncStorageEngine$OnSyncRequestListener.class */
    public interface OnSyncRequestListener {
        void onSyncRequest(Account account, int i, int i2, String str, Bundle bundle);
    }

    /* loaded from: SyncStorageEngine$SyncHistoryItem.class */
    public static class SyncHistoryItem {
        int authorityId;
        int historyId;
        long eventTime;
        long elapsedTime;
        int source;
        int event;
        long upstreamActivity;
        long downstreamActivity;
        String mesg;
        boolean initialization;
        Bundle extras;
        int reason;
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.updateOrRemovePeriodicSync(android.content.PeriodicSync, int, boolean):void, file: SyncStorageEngine.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void updateOrRemovePeriodicSync(android.content.PeriodicSync r1, int r2, boolean r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.updateOrRemovePeriodicSync(android.content.PeriodicSync, int, boolean):void, file: SyncStorageEngine.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.updateOrRemovePeriodicSync(android.content.PeriodicSync, int, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.readAccountInfoLocked():void, file: SyncStorageEngine.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void readAccountInfoLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.readAccountInfoLocked():void, file: SyncStorageEngine.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.readAccountInfoLocked():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.readPendingOperationsLocked():void, file: SyncStorageEngine.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void readPendingOperationsLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.readPendingOperationsLocked():void, file: SyncStorageEngine.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.readPendingOperationsLocked():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.appendPendingOperationLocked(com.android.server.content.SyncStorageEngine$PendingOperation):void, file: SyncStorageEngine.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void appendPendingOperationLocked(com.android.server.content.SyncStorageEngine.PendingOperation r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.appendPendingOperationLocked(com.android.server.content.SyncStorageEngine$PendingOperation):void, file: SyncStorageEngine.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.appendPendingOperationLocked(com.android.server.content.SyncStorageEngine$PendingOperation):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.flattenBundle(android.os.Bundle):byte[], file: SyncStorageEngine.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private static byte[] flattenBundle(android.os.Bundle r0) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.flattenBundle(android.os.Bundle):byte[], file: SyncStorageEngine.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.flattenBundle(android.os.Bundle):byte[]");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.unflattenBundle(byte[]):android.os.Bundle, file: SyncStorageEngine.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private static android.os.Bundle unflattenBundle(byte[] r0) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.SyncStorageEngine.unflattenBundle(byte[]):android.os.Bundle, file: SyncStorageEngine.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.unflattenBundle(byte[]):android.os.Bundle");
    }

    static {
        sAuthorityRenames.put(Contacts.AUTHORITY, ContactsContract.AUTHORITY);
        sAuthorityRenames.put("calendar", CalendarContract.AUTHORITY);
        sSyncStorageEngine = null;
    }

    /* loaded from: SyncStorageEngine$PendingOperation.class */
    public static class PendingOperation {
        final Account account;
        final int userId;
        final int reason;
        final int syncSource;
        final String authority;
        final Bundle extras;
        final ComponentName serviceName;
        final boolean expedited;
        int authorityId;
        byte[] flatExtras;

        /* JADX INFO: Access modifiers changed from: package-private */
        public PendingOperation(Account account, int userId, int reason, int source, String authority, Bundle extras, boolean expedited) {
            this.account = account;
            this.userId = userId;
            this.syncSource = source;
            this.reason = reason;
            this.authority = authority;
            this.extras = extras != null ? new Bundle(extras) : extras;
            this.expedited = expedited;
            this.authorityId = -1;
            this.serviceName = null;
        }

        PendingOperation(PendingOperation other) {
            this.account = other.account;
            this.userId = other.userId;
            this.reason = other.reason;
            this.syncSource = other.syncSource;
            this.authority = other.authority;
            this.extras = other.extras;
            this.authorityId = other.authorityId;
            this.expedited = other.expedited;
            this.serviceName = other.serviceName;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SyncStorageEngine$AccountInfo.class */
    public static class AccountInfo {
        final AccountAndUser accountAndUser;
        final HashMap<String, AuthorityInfo> authorities = new HashMap<>();

        AccountInfo(AccountAndUser accountAndUser) {
            this.accountAndUser = accountAndUser;
        }
    }

    /* loaded from: SyncStorageEngine$AuthorityInfo.class */
    public static class AuthorityInfo {
        final ComponentName service;
        final Account account;
        final int userId;
        final String authority;
        final int ident;
        boolean enabled;
        int syncable;
        long backoffTime;
        long backoffDelay;
        long delayUntil;
        final ArrayList<PeriodicSync> periodicSyncs;

        AuthorityInfo(AuthorityInfo toCopy) {
            this.account = toCopy.account;
            this.userId = toCopy.userId;
            this.authority = toCopy.authority;
            this.service = toCopy.service;
            this.ident = toCopy.ident;
            this.enabled = toCopy.enabled;
            this.syncable = toCopy.syncable;
            this.backoffTime = toCopy.backoffTime;
            this.backoffDelay = toCopy.backoffDelay;
            this.delayUntil = toCopy.delayUntil;
            this.periodicSyncs = new ArrayList<>();
            Iterator i$ = toCopy.periodicSyncs.iterator();
            while (i$.hasNext()) {
                PeriodicSync sync = i$.next();
                this.periodicSyncs.add(new PeriodicSync(sync));
            }
        }

        AuthorityInfo(Account account, int userId, String authority, int ident) {
            this.account = account;
            this.userId = userId;
            this.authority = authority;
            this.service = null;
            this.ident = ident;
            this.enabled = false;
            this.syncable = -1;
            this.backoffTime = -1L;
            this.backoffDelay = -1L;
            this.periodicSyncs = new ArrayList<>();
            this.periodicSyncs.add(new PeriodicSync(account, authority, new Bundle(), SyncStorageEngine.DEFAULT_POLL_FREQUENCY_SECONDS, SyncStorageEngine.calculateDefaultFlexTime(SyncStorageEngine.DEFAULT_POLL_FREQUENCY_SECONDS)));
        }

        AuthorityInfo(ComponentName cname, int userId, int ident) {
            this.account = null;
            this.userId = userId;
            this.authority = null;
            this.service = cname;
            this.ident = ident;
            this.enabled = true;
            this.syncable = -1;
            this.backoffTime = -1L;
            this.backoffDelay = -1L;
            this.periodicSyncs = new ArrayList<>();
            this.periodicSyncs.add(new PeriodicSync(this.account, this.authority, new Bundle(), SyncStorageEngine.DEFAULT_POLL_FREQUENCY_SECONDS, SyncStorageEngine.calculateDefaultFlexTime(SyncStorageEngine.DEFAULT_POLL_FREQUENCY_SECONDS)));
        }
    }

    /* loaded from: SyncStorageEngine$DayStats.class */
    public static class DayStats {
        public final int day;
        public int successCount;
        public long successTime;
        public int failureCount;
        public long failureTime;

        public DayStats(int day) {
            this.day = day;
        }
    }

    private SyncStorageEngine(Context context, File dataDir) {
        this.mContext = context;
        sSyncStorageEngine = this;
        this.mCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        this.mDefaultMasterSyncAutomatically = this.mContext.getResources().getBoolean(R.bool.config_syncstorageengine_masterSyncAutomatically);
        File systemDir = new File(dataDir, "system");
        File syncDir = new File(systemDir, "sync");
        syncDir.mkdirs();
        maybeDeleteLegacyPendingInfoLocked(syncDir);
        this.mAccountInfoFile = new AtomicFile(new File(syncDir, "accounts.xml"));
        this.mStatusFile = new AtomicFile(new File(syncDir, "status.bin"));
        this.mPendingFile = new AtomicFile(new File(syncDir, "pending.xml"));
        this.mStatisticsFile = new AtomicFile(new File(syncDir, "stats.bin"));
        readAccountInfoLocked();
        readStatusLocked();
        readPendingOperationsLocked();
        readStatisticsLocked();
        readAndDeleteLegacyAccountInfoLocked();
        writeAccountInfoLocked();
        writeStatusLocked();
        writePendingOperationsLocked();
        writeStatisticsLocked();
    }

    public static SyncStorageEngine newTestInstance(Context context) {
        return new SyncStorageEngine(context, context.getFilesDir());
    }

    public static void init(Context context) {
        if (sSyncStorageEngine != null) {
            return;
        }
        File dataDir = Environment.getSecureDataDirectory();
        sSyncStorageEngine = new SyncStorageEngine(context, dataDir);
    }

    public static SyncStorageEngine getSingleton() {
        if (sSyncStorageEngine == null) {
            throw new IllegalStateException("not initialized");
        }
        return sSyncStorageEngine;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOnSyncRequestListener(OnSyncRequestListener listener) {
        if (this.mSyncRequestListener == null) {
            this.mSyncRequestListener = listener;
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        if (msg.what == 1) {
            synchronized (this.mAuthorities) {
                writeStatusLocked();
            }
        } else if (msg.what == 2) {
            synchronized (this.mAuthorities) {
                writeStatisticsLocked();
            }
        }
    }

    public int getSyncRandomOffset() {
        return this.mSyncRandomOffset;
    }

    public void addStatusChangeListener(int mask, ISyncStatusObserver callback) {
        synchronized (this.mAuthorities) {
            this.mChangeListeners.register(callback, Integer.valueOf(mask));
        }
    }

    public void removeStatusChangeListener(ISyncStatusObserver callback) {
        synchronized (this.mAuthorities) {
            this.mChangeListeners.unregister(callback);
        }
    }

    public static long calculateDefaultFlexTime(long syncTimeSeconds) {
        if (syncTimeSeconds < DEFAULT_MIN_FLEX_ALLOWED_SECS) {
            return 0L;
        }
        if (syncTimeSeconds < DEFAULT_POLL_FREQUENCY_SECONDS) {
            return (long) (syncTimeSeconds * DEFAULT_FLEX_PERCENT_SYNC);
        }
        return 3456L;
    }

    private void reportChange(int which) {
        ArrayList<ISyncStatusObserver> reports = null;
        synchronized (this.mAuthorities) {
            int i = this.mChangeListeners.beginBroadcast();
            while (i > 0) {
                i--;
                Integer mask = (Integer) this.mChangeListeners.getBroadcastCookie(i);
                if ((which & mask.intValue()) != 0) {
                    if (reports == null) {
                        reports = new ArrayList<>(i);
                    }
                    reports.add(this.mChangeListeners.getBroadcastItem(i));
                }
            }
            this.mChangeListeners.finishBroadcast();
        }
        if (reports != null) {
            int i2 = reports.size();
            while (i2 > 0) {
                i2--;
                try {
                    reports.get(i2).onStatusChanged(which);
                } catch (RemoteException e) {
                }
            }
        }
    }

    public boolean getSyncAutomatically(Account account, int userId, String providerName) {
        synchronized (this.mAuthorities) {
            if (account != null) {
                AuthorityInfo authority = getAuthorityLocked(account, userId, providerName, "getSyncAutomatically");
                return authority != null && authority.enabled;
            }
            int i = this.mAuthorities.size();
            while (i > 0) {
                i--;
                AuthorityInfo authority2 = this.mAuthorities.valueAt(i);
                if (authority2.authority.equals(providerName) && authority2.userId == userId && authority2.enabled) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setSyncAutomatically(Account account, int userId, String providerName, boolean sync) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getOrCreateAuthorityLocked(account, userId, providerName, -1, false);
            if (authority.enabled == sync) {
                return;
            }
            authority.enabled = sync;
            writeAccountInfoLocked();
            if (sync) {
                requestSync(account, userId, -6, providerName, new Bundle());
            }
            reportChange(1);
        }
    }

    public int getIsSyncable(Account account, int userId, String providerName) {
        synchronized (this.mAuthorities) {
            if (account != null) {
                AuthorityInfo authority = getAuthorityLocked(account, userId, providerName, "getIsSyncable");
                if (authority == null) {
                    return -1;
                }
                return authority.syncable;
            }
            int i = this.mAuthorities.size();
            while (i > 0) {
                i--;
                AuthorityInfo authority2 = this.mAuthorities.valueAt(i);
                if (authority2.authority.equals(providerName)) {
                    return authority2.syncable;
                }
            }
            return -1;
        }
    }

    public void setIsSyncable(Account account, int userId, String providerName, int syncable) {
        if (syncable > 1) {
            syncable = 1;
        } else if (syncable < -1) {
            syncable = -1;
        }
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getOrCreateAuthorityLocked(account, userId, providerName, -1, false);
            if (authority.syncable == syncable) {
                return;
            }
            authority.syncable = syncable;
            writeAccountInfoLocked();
            if (syncable > 0) {
                requestSync(account, userId, -5, providerName, new Bundle());
            }
            reportChange(1);
        }
    }

    public Pair<Long, Long> getBackoff(Account account, int userId, String providerName) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getAuthorityLocked(account, userId, providerName, "getBackoff");
            if (authority == null || authority.backoffTime < 0) {
                return null;
            }
            return Pair.create(Long.valueOf(authority.backoffTime), Long.valueOf(authority.backoffDelay));
        }
    }

    public void setBackoff(Account account, int userId, String providerName, long nextSyncTime, long nextDelay) {
        boolean changed = false;
        synchronized (this.mAuthorities) {
            if (account == null || providerName == null) {
                for (AccountInfo accountInfo : this.mAccounts.values()) {
                    if (account == null || account.equals(accountInfo.accountAndUser.account) || userId == accountInfo.accountAndUser.userId) {
                        for (AuthorityInfo authorityInfo : accountInfo.authorities.values()) {
                            if (providerName == null || providerName.equals(authorityInfo.authority)) {
                                if (authorityInfo.backoffTime != nextSyncTime || authorityInfo.backoffDelay != nextDelay) {
                                    authorityInfo.backoffTime = nextSyncTime;
                                    authorityInfo.backoffDelay = nextDelay;
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            } else {
                AuthorityInfo authority = getOrCreateAuthorityLocked(account, userId, providerName, -1, true);
                if (authority.backoffTime == nextSyncTime && authority.backoffDelay == nextDelay) {
                    return;
                }
                authority.backoffTime = nextSyncTime;
                authority.backoffDelay = nextDelay;
                changed = true;
            }
            if (changed) {
                reportChange(1);
            }
        }
    }

    public void clearAllBackoffsLocked(SyncQueue syncQueue) {
        boolean changed = false;
        synchronized (this.mAuthorities) {
            for (AccountInfo accountInfo : this.mAccounts.values()) {
                for (AuthorityInfo authorityInfo : accountInfo.authorities.values()) {
                    if (authorityInfo.backoffTime != -1 || authorityInfo.backoffDelay != -1) {
                        authorityInfo.backoffTime = -1L;
                        authorityInfo.backoffDelay = -1L;
                        syncQueue.onBackoffChanged(accountInfo.accountAndUser.account, accountInfo.accountAndUser.userId, authorityInfo.authority, 0L);
                        changed = true;
                    }
                }
            }
        }
        if (changed) {
            reportChange(1);
        }
    }

    public void setDelayUntilTime(Account account, int userId, String providerName, long delayUntil) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getOrCreateAuthorityLocked(account, userId, providerName, -1, true);
            if (authority.delayUntil == delayUntil) {
                return;
            }
            authority.delayUntil = delayUntil;
            reportChange(1);
        }
    }

    public long getDelayUntilTime(Account account, int userId, String providerName) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getAuthorityLocked(account, userId, providerName, "getDelayUntil");
            if (authority == null) {
                return 0L;
            }
            return authority.delayUntil;
        }
    }

    public void addPeriodicSync(PeriodicSync toAdd, int userId) {
        updateOrRemovePeriodicSync(toAdd, userId, true);
    }

    public void removePeriodicSync(PeriodicSync toRemove, int userId) {
        updateOrRemovePeriodicSync(toRemove, userId, false);
    }

    public List<PeriodicSync> getPeriodicSyncs(Account account, int userId, String providerName) {
        ArrayList<PeriodicSync> syncs = new ArrayList<>();
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getAuthorityLocked(account, userId, providerName, "getPeriodicSyncs");
            if (authority != null) {
                Iterator i$ = authority.periodicSyncs.iterator();
                while (i$.hasNext()) {
                    PeriodicSync item = i$.next();
                    syncs.add(new PeriodicSync(item));
                }
            }
        }
        return syncs;
    }

    public void setMasterSyncAutomatically(boolean flag, int userId) {
        synchronized (this.mAuthorities) {
            Boolean auto = this.mMasterSyncAutomatically.get(userId);
            if (auto == null || auto.booleanValue() != flag) {
                this.mMasterSyncAutomatically.put(userId, Boolean.valueOf(flag));
                writeAccountInfoLocked();
                if (flag) {
                    requestSync(null, userId, -7, null, new Bundle());
                }
                reportChange(1);
                this.mContext.sendBroadcast(ContentResolver.ACTION_SYNC_CONN_STATUS_CHANGED);
            }
        }
    }

    public boolean getMasterSyncAutomatically(int userId) {
        boolean booleanValue;
        synchronized (this.mAuthorities) {
            Boolean auto = this.mMasterSyncAutomatically.get(userId);
            booleanValue = auto == null ? this.mDefaultMasterSyncAutomatically : auto.booleanValue();
        }
        return booleanValue;
    }

    public void removeAuthority(Account account, int userId, String authority) {
        synchronized (this.mAuthorities) {
            removeAuthorityLocked(account, userId, authority, true);
        }
    }

    public AuthorityInfo getAuthority(int authorityId) {
        AuthorityInfo authorityInfo;
        synchronized (this.mAuthorities) {
            authorityInfo = this.mAuthorities.get(authorityId);
        }
        return authorityInfo;
    }

    public boolean isSyncActive(Account account, int userId, String authority) {
        synchronized (this.mAuthorities) {
            for (SyncInfo syncInfo : getCurrentSyncs(userId)) {
                AuthorityInfo ainfo = getAuthority(syncInfo.authorityId);
                if (ainfo != null && ainfo.account.equals(account) && ainfo.authority.equals(authority) && ainfo.userId == userId) {
                    return true;
                }
            }
            return false;
        }
    }

    public PendingOperation insertIntoPending(PendingOperation op) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getOrCreateAuthorityLocked(op.account, op.userId, op.authority, -1, true);
            if (authority == null) {
                return null;
            }
            PendingOperation op2 = new PendingOperation(op);
            op2.authorityId = authority.ident;
            this.mPendingOperations.add(op2);
            appendPendingOperationLocked(op2);
            SyncStatusInfo status = getOrCreateSyncStatusLocked(authority.ident);
            status.pending = true;
            reportChange(2);
            return op2;
        }
    }

    public boolean deleteFromPending(PendingOperation op) {
        boolean res = false;
        synchronized (this.mAuthorities) {
            if (this.mPendingOperations.remove(op)) {
                if (this.mPendingOperations.size() == 0 || this.mNumPendingFinished >= 4) {
                    writePendingOperationsLocked();
                    this.mNumPendingFinished = 0;
                } else {
                    this.mNumPendingFinished++;
                }
                AuthorityInfo authority = getAuthorityLocked(op.account, op.userId, op.authority, "deleteFromPending");
                if (authority != null) {
                    int N = this.mPendingOperations.size();
                    boolean morePending = false;
                    int i = 0;
                    while (true) {
                        if (i >= N) {
                            break;
                        }
                        PendingOperation cur = this.mPendingOperations.get(i);
                        if (!cur.account.equals(op.account) || !cur.authority.equals(op.authority) || cur.userId != op.userId) {
                            i++;
                        } else {
                            morePending = true;
                            break;
                        }
                    }
                    if (!morePending) {
                        SyncStatusInfo status = getOrCreateSyncStatusLocked(authority.ident);
                        status.pending = false;
                    }
                }
                res = true;
            }
        }
        reportChange(2);
        return res;
    }

    public ArrayList<PendingOperation> getPendingOperations() {
        ArrayList<PendingOperation> arrayList;
        synchronized (this.mAuthorities) {
            arrayList = new ArrayList<>(this.mPendingOperations);
        }
        return arrayList;
    }

    public int getPendingOperationCount() {
        int size;
        synchronized (this.mAuthorities) {
            size = this.mPendingOperations.size();
        }
        return size;
    }

    public void doDatabaseCleanup(Account[] accounts, int userId) {
        synchronized (this.mAuthorities) {
            SparseArray<AuthorityInfo> removing = new SparseArray<>();
            Iterator<AccountInfo> accIt = this.mAccounts.values().iterator();
            while (accIt.hasNext()) {
                AccountInfo acc = accIt.next();
                if (!ArrayUtils.contains(accounts, acc.accountAndUser.account) && acc.accountAndUser.userId == userId) {
                    for (AuthorityInfo auth : acc.authorities.values()) {
                        removing.put(auth.ident, auth);
                    }
                    accIt.remove();
                }
            }
            int i = removing.size();
            if (i > 0) {
                while (i > 0) {
                    i--;
                    int ident = removing.keyAt(i);
                    this.mAuthorities.remove(ident);
                    int j = this.mSyncStatus.size();
                    while (j > 0) {
                        j--;
                        if (this.mSyncStatus.keyAt(j) == ident) {
                            this.mSyncStatus.remove(this.mSyncStatus.keyAt(j));
                        }
                    }
                    int j2 = this.mSyncHistory.size();
                    while (j2 > 0) {
                        j2--;
                        if (this.mSyncHistory.get(j2).authorityId == ident) {
                            this.mSyncHistory.remove(j2);
                        }
                    }
                }
                writeAccountInfoLocked();
                writeStatusLocked();
                writePendingOperationsLocked();
                writeStatisticsLocked();
            }
        }
    }

    public SyncInfo addActiveSync(SyncManager.ActiveSyncContext activeSyncContext) {
        SyncInfo syncInfo;
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getOrCreateAuthorityLocked(activeSyncContext.mSyncOperation.account, activeSyncContext.mSyncOperation.userId, activeSyncContext.mSyncOperation.authority, -1, true);
            syncInfo = new SyncInfo(authority.ident, authority.account, authority.authority, activeSyncContext.mStartTime);
            getCurrentSyncs(authority.userId).add(syncInfo);
        }
        reportActiveChange();
        return syncInfo;
    }

    public void removeActiveSync(SyncInfo syncInfo, int userId) {
        synchronized (this.mAuthorities) {
            getCurrentSyncs(userId).remove(syncInfo);
        }
        reportActiveChange();
    }

    public void reportActiveChange() {
        reportChange(4);
    }

    public long insertStartSyncEvent(Account accountName, int userId, int reason, String authorityName, long now, int source, boolean initialization, Bundle extras) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getAuthorityLocked(accountName, userId, authorityName, "insertStartSyncEvent");
            if (authority == null) {
                return -1L;
            }
            SyncHistoryItem item = new SyncHistoryItem();
            item.initialization = initialization;
            item.authorityId = authority.ident;
            int i = this.mNextHistoryId;
            this.mNextHistoryId = i + 1;
            item.historyId = i;
            if (this.mNextHistoryId < 0) {
                this.mNextHistoryId = 0;
            }
            item.eventTime = now;
            item.source = source;
            item.reason = reason;
            item.extras = extras;
            item.event = 0;
            this.mSyncHistory.add(0, item);
            while (this.mSyncHistory.size() > 100) {
                this.mSyncHistory.remove(this.mSyncHistory.size() - 1);
            }
            long id = item.historyId;
            reportChange(8);
            return id;
        }
    }

    public void stopSyncEvent(long historyId, long elapsedTime, String resultMessage, long downstreamActivity, long upstreamActivity) {
        synchronized (this.mAuthorities) {
            SyncHistoryItem item = null;
            int i = this.mSyncHistory.size();
            while (i > 0) {
                i--;
                item = this.mSyncHistory.get(i);
                if (item.historyId == historyId) {
                    break;
                }
                item = null;
            }
            if (item == null) {
                Log.w(TAG, "stopSyncEvent: no history for id " + historyId);
                return;
            }
            item.elapsedTime = elapsedTime;
            item.event = 1;
            item.mesg = resultMessage;
            item.downstreamActivity = downstreamActivity;
            item.upstreamActivity = upstreamActivity;
            SyncStatusInfo status = getOrCreateSyncStatusLocked(item.authorityId);
            status.numSyncs++;
            status.totalElapsedTime += elapsedTime;
            switch (item.source) {
                case 0:
                    status.numSourceServer++;
                    break;
                case 1:
                    status.numSourceLocal++;
                    break;
                case 2:
                    status.numSourcePoll++;
                    break;
                case 3:
                    status.numSourceUser++;
                    break;
                case 4:
                    status.numSourcePeriodic++;
                    break;
            }
            boolean writeStatisticsNow = false;
            int day = getCurrentDayLocked();
            if (this.mDayStats[0] == null) {
                this.mDayStats[0] = new DayStats(day);
            } else if (day != this.mDayStats[0].day) {
                System.arraycopy(this.mDayStats, 0, this.mDayStats, 1, this.mDayStats.length - 1);
                this.mDayStats[0] = new DayStats(day);
                writeStatisticsNow = true;
            } else if (this.mDayStats[0] == null) {
            }
            DayStats ds = this.mDayStats[0];
            long lastSyncTime = item.eventTime + elapsedTime;
            boolean writeStatusNow = false;
            if (MESG_SUCCESS.equals(resultMessage)) {
                if (status.lastSuccessTime == 0 || status.lastFailureTime != 0) {
                    writeStatusNow = true;
                }
                status.lastSuccessTime = lastSyncTime;
                status.lastSuccessSource = item.source;
                status.lastFailureTime = 0L;
                status.lastFailureSource = -1;
                status.lastFailureMesg = null;
                status.initialFailureTime = 0L;
                ds.successCount++;
                ds.successTime += elapsedTime;
            } else if (!MESG_CANCELED.equals(resultMessage)) {
                if (status.lastFailureTime == 0) {
                    writeStatusNow = true;
                }
                status.lastFailureTime = lastSyncTime;
                status.lastFailureSource = item.source;
                status.lastFailureMesg = resultMessage;
                if (status.initialFailureTime == 0) {
                    status.initialFailureTime = lastSyncTime;
                }
                ds.failureCount++;
                ds.failureTime += elapsedTime;
            }
            if (writeStatusNow) {
                writeStatusLocked();
            } else if (!hasMessages(1)) {
                sendMessageDelayed(obtainMessage(1), 600000L);
            }
            if (writeStatisticsNow) {
                writeStatisticsLocked();
            } else if (!hasMessages(2)) {
                sendMessageDelayed(obtainMessage(2), 1800000L);
            }
            reportChange(8);
        }
    }

    public List<SyncInfo> getCurrentSyncs(int userId) {
        ArrayList<SyncInfo> arrayList;
        synchronized (this.mAuthorities) {
            ArrayList<SyncInfo> syncs = this.mCurrentSyncs.get(userId);
            if (syncs == null) {
                syncs = new ArrayList<>();
                this.mCurrentSyncs.put(userId, syncs);
            }
            arrayList = syncs;
        }
        return arrayList;
    }

    public ArrayList<SyncStatusInfo> getSyncStatus() {
        ArrayList<SyncStatusInfo> ops;
        synchronized (this.mAuthorities) {
            int N = this.mSyncStatus.size();
            ops = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                ops.add(this.mSyncStatus.valueAt(i));
            }
        }
        return ops;
    }

    public Pair<AuthorityInfo, SyncStatusInfo> getCopyOfAuthorityWithSyncStatus(Account account, int userId, String authority) {
        Pair<AuthorityInfo, SyncStatusInfo> createCopyPairOfAuthorityWithSyncStatusLocked;
        synchronized (this.mAuthorities) {
            AuthorityInfo authorityInfo = getOrCreateAuthorityLocked(account, userId, authority, -1, true);
            createCopyPairOfAuthorityWithSyncStatusLocked = createCopyPairOfAuthorityWithSyncStatusLocked(authorityInfo);
        }
        return createCopyPairOfAuthorityWithSyncStatusLocked;
    }

    public ArrayList<Pair<AuthorityInfo, SyncStatusInfo>> getCopyOfAllAuthoritiesWithSyncStatus() {
        ArrayList<Pair<AuthorityInfo, SyncStatusInfo>> infos;
        synchronized (this.mAuthorities) {
            infos = new ArrayList<>(this.mAuthorities.size());
            for (int i = 0; i < this.mAuthorities.size(); i++) {
                infos.add(createCopyPairOfAuthorityWithSyncStatusLocked(this.mAuthorities.valueAt(i)));
            }
        }
        return infos;
    }

    public SyncStatusInfo getStatusByAccountAndAuthority(Account account, int userId, String authority) {
        if (account == null || authority == null) {
            return null;
        }
        synchronized (this.mAuthorities) {
            int N = this.mSyncStatus.size();
            for (int i = 0; i < N; i++) {
                SyncStatusInfo cur = this.mSyncStatus.valueAt(i);
                AuthorityInfo ainfo = this.mAuthorities.get(cur.authorityId);
                if (ainfo != null && ainfo.authority.equals(authority) && ainfo.userId == userId && account.equals(ainfo.account)) {
                    return cur;
                }
            }
            return null;
        }
    }

    public boolean isSyncPending(Account account, int userId, String authority) {
        synchronized (this.mAuthorities) {
            int N = this.mSyncStatus.size();
            for (int i = 0; i < N; i++) {
                SyncStatusInfo cur = this.mSyncStatus.valueAt(i);
                AuthorityInfo ainfo = this.mAuthorities.get(cur.authorityId);
                if (ainfo != null && userId == ainfo.userId && ((account == null || ainfo.account.equals(account)) && ainfo.authority.equals(authority) && cur.pending)) {
                    return true;
                }
            }
            return false;
        }
    }

    public ArrayList<SyncHistoryItem> getSyncHistory() {
        ArrayList<SyncHistoryItem> items;
        synchronized (this.mAuthorities) {
            int N = this.mSyncHistory.size();
            items = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                items.add(this.mSyncHistory.get(i));
            }
        }
        return items;
    }

    public DayStats[] getDayStatistics() {
        DayStats[] ds;
        synchronized (this.mAuthorities) {
            ds = new DayStats[this.mDayStats.length];
            System.arraycopy(this.mDayStats, 0, ds, 0, ds.length);
        }
        return ds;
    }

    private Pair<AuthorityInfo, SyncStatusInfo> createCopyPairOfAuthorityWithSyncStatusLocked(AuthorityInfo authorityInfo) {
        SyncStatusInfo syncStatusInfo = getOrCreateSyncStatusLocked(authorityInfo.ident);
        return Pair.create(new AuthorityInfo(authorityInfo), new SyncStatusInfo(syncStatusInfo));
    }

    private int getCurrentDayLocked() {
        this.mCal.setTimeInMillis(System.currentTimeMillis());
        int dayOfYear = this.mCal.get(6);
        if (this.mYear != this.mCal.get(1)) {
            this.mYear = this.mCal.get(1);
            this.mCal.clear();
            this.mCal.set(1, this.mYear);
            this.mYearInDays = (int) (this.mCal.getTimeInMillis() / 86400000);
        }
        return dayOfYear + this.mYearInDays;
    }

    private AuthorityInfo getAuthorityLocked(Account accountName, int userId, String authorityName, String tag) {
        AccountAndUser au = new AccountAndUser(accountName, userId);
        AccountInfo accountInfo = this.mAccounts.get(au);
        if (accountInfo == null) {
            if (tag != null) {
            }
            return null;
        }
        AuthorityInfo authority = accountInfo.authorities.get(authorityName);
        if (authority == null) {
            if (tag != null) {
            }
            return null;
        }
        return authority;
    }

    private AuthorityInfo getAuthorityLocked(ComponentName service, int userId, String tag) {
        AuthorityInfo authority = this.mServices.get(service).get(userId);
        if (authority == null) {
            if (tag != null) {
            }
            return null;
        }
        return authority;
    }

    private AuthorityInfo getOrCreateAuthorityLocked(ComponentName cname, int userId, int ident, boolean doWrite) {
        SparseArray<AuthorityInfo> aInfo = this.mServices.get(cname);
        if (aInfo == null) {
            aInfo = new SparseArray<>();
            this.mServices.put(cname, aInfo);
        }
        AuthorityInfo authority = aInfo.get(userId);
        if (authority == null) {
            if (ident < 0) {
                ident = this.mNextAuthorityId;
                this.mNextAuthorityId++;
                doWrite = true;
            }
            authority = new AuthorityInfo(cname, userId, ident);
            aInfo.put(userId, authority);
            this.mAuthorities.put(ident, authority);
            if (doWrite) {
                writeAccountInfoLocked();
            }
        }
        return authority;
    }

    private AuthorityInfo getOrCreateAuthorityLocked(Account accountName, int userId, String authorityName, int ident, boolean doWrite) {
        AccountAndUser au = new AccountAndUser(accountName, userId);
        AccountInfo account = this.mAccounts.get(au);
        if (account == null) {
            account = new AccountInfo(au);
            this.mAccounts.put(au, account);
        }
        AuthorityInfo authority = account.authorities.get(authorityName);
        if (authority == null) {
            if (ident < 0) {
                ident = this.mNextAuthorityId;
                this.mNextAuthorityId++;
                doWrite = true;
            }
            authority = new AuthorityInfo(accountName, userId, authorityName, ident);
            account.authorities.put(authorityName, authority);
            this.mAuthorities.put(ident, authority);
            if (doWrite) {
                writeAccountInfoLocked();
            }
        }
        return authority;
    }

    private void removeAuthorityLocked(Account account, int userId, String authorityName, boolean doWrite) {
        AuthorityInfo authorityInfo;
        AccountInfo accountInfo = this.mAccounts.get(new AccountAndUser(account, userId));
        if (accountInfo != null && (authorityInfo = accountInfo.authorities.remove(authorityName)) != null) {
            this.mAuthorities.remove(authorityInfo.ident);
            if (doWrite) {
                writeAccountInfoLocked();
            }
        }
    }

    public void setPeriodicSyncTime(int authorityId, PeriodicSync targetPeriodicSync, long when) {
        AuthorityInfo authorityInfo;
        boolean found = false;
        synchronized (this.mAuthorities) {
            authorityInfo = this.mAuthorities.get(authorityId);
            int i = 0;
            while (true) {
                if (i >= authorityInfo.periodicSyncs.size()) {
                    break;
                }
                PeriodicSync periodicSync = authorityInfo.periodicSyncs.get(i);
                if (!targetPeriodicSync.equals(periodicSync)) {
                    i++;
                } else {
                    this.mSyncStatus.get(authorityId).setPeriodicSyncTime(i, when);
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            Log.w(TAG, "Ignoring setPeriodicSyncTime request for a sync that does not exist. Authority: " + authorityInfo.authority);
        }
    }

    private SyncStatusInfo getOrCreateSyncStatusLocked(int authorityId) {
        SyncStatusInfo status = this.mSyncStatus.get(authorityId);
        if (status == null) {
            status = new SyncStatusInfo(authorityId);
            this.mSyncStatus.put(authorityId, status);
        }
        return status;
    }

    public void writeAllState() {
        synchronized (this.mAuthorities) {
            if (this.mNumPendingFinished > 0) {
                writePendingOperationsLocked();
            }
            writeStatusLocked();
            writeStatisticsLocked();
        }
    }

    public void clearAndReadState() {
        synchronized (this.mAuthorities) {
            this.mAuthorities.clear();
            this.mAccounts.clear();
            this.mServices.clear();
            this.mPendingOperations.clear();
            this.mSyncStatus.clear();
            this.mSyncHistory.clear();
            readAccountInfoLocked();
            readStatusLocked();
            readPendingOperationsLocked();
            readStatisticsLocked();
            readAndDeleteLegacyAccountInfoLocked();
            writeAccountInfoLocked();
            writeStatusLocked();
            writePendingOperationsLocked();
            writeStatisticsLocked();
        }
    }

    private void maybeDeleteLegacyPendingInfoLocked(File syncDir) {
        File file = new File(syncDir, "pending.bin");
        if (!file.exists()) {
            return;
        }
        file.delete();
    }

    private boolean maybeMigrateSettingsForRenamedAuthorities() {
        boolean writeNeeded = false;
        ArrayList<AuthorityInfo> authoritiesToRemove = new ArrayList<>();
        int N = this.mAuthorities.size();
        for (int i = 0; i < N; i++) {
            AuthorityInfo authority = this.mAuthorities.valueAt(i);
            String newAuthorityName = sAuthorityRenames.get(authority.authority);
            if (newAuthorityName != null) {
                authoritiesToRemove.add(authority);
                if (authority.enabled && getAuthorityLocked(authority.account, authority.userId, newAuthorityName, "cleanup") == null) {
                    AuthorityInfo newAuthority = getOrCreateAuthorityLocked(authority.account, authority.userId, newAuthorityName, -1, false);
                    newAuthority.enabled = true;
                    writeNeeded = true;
                }
            }
        }
        Iterator i$ = authoritiesToRemove.iterator();
        while (i$.hasNext()) {
            AuthorityInfo authorityInfo = i$.next();
            removeAuthorityLocked(authorityInfo.account, authorityInfo.userId, authorityInfo.authority, false);
            writeNeeded = true;
        }
        return writeNeeded;
    }

    private void parseListenForTickles(XmlPullParser parser) {
        String user = parser.getAttributeValue(null, "user");
        int userId = 0;
        try {
            userId = Integer.parseInt(user);
        } catch (NullPointerException e) {
            Log.e(TAG, "the user in listen-for-tickles is null", e);
        } catch (NumberFormatException e2) {
            Log.e(TAG, "error parsing the user for listen-for-tickles", e2);
        }
        String enabled = parser.getAttributeValue(null, "enabled");
        boolean listen = enabled == null || Boolean.parseBoolean(enabled);
        this.mMasterSyncAutomatically.put(userId, Boolean.valueOf(listen));
    }

    private AuthorityInfo parseAuthority(XmlPullParser parser, int version) {
        AuthorityInfo authority = null;
        int id = -1;
        try {
            id = Integer.parseInt(parser.getAttributeValue(null, "id"));
        } catch (NullPointerException e) {
            Log.e(TAG, "the id of the authority is null", e);
        } catch (NumberFormatException e2) {
            Log.e(TAG, "error parsing the id of the authority", e2);
        }
        if (id >= 0) {
            String authorityName = parser.getAttributeValue(null, ContactsContract.Directory.DIRECTORY_AUTHORITY);
            String enabled = parser.getAttributeValue(null, "enabled");
            String syncable = parser.getAttributeValue(null, "syncable");
            String accountName = parser.getAttributeValue(null, "account");
            String accountType = parser.getAttributeValue(null, "type");
            String user = parser.getAttributeValue(null, "user");
            String packageName = parser.getAttributeValue(null, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
            String className = parser.getAttributeValue(null, "class");
            int userId = user == null ? 0 : Integer.parseInt(user);
            if (accountType == null) {
                accountType = "com.google";
                syncable = "unknown";
            }
            authority = this.mAuthorities.get(id);
            if (Log.isLoggable(TAG_FILE, 2)) {
                Log.v(TAG, "Adding authority: account=" + accountName + " auth=" + authorityName + " user=" + userId + " enabled=" + enabled + " syncable=" + syncable);
            }
            if (authority == null) {
                if (Log.isLoggable(TAG_FILE, 2)) {
                    Log.v(TAG, "Creating entry");
                }
                if (accountName != null && accountType != null) {
                    authority = getOrCreateAuthorityLocked(new Account(accountName, accountType), userId, authorityName, id, false);
                } else {
                    authority = getOrCreateAuthorityLocked(new ComponentName(packageName, className), userId, id, false);
                }
                if (version > 0) {
                    authority.periodicSyncs.clear();
                }
            }
            if (authority != null) {
                authority.enabled = enabled == null || Boolean.parseBoolean(enabled);
                if ("unknown".equals(syncable)) {
                    authority.syncable = -1;
                } else {
                    authority.syncable = (syncable == null || Boolean.parseBoolean(syncable)) ? 1 : 0;
                }
            } else {
                Log.w(TAG, "Failure adding authority: account=" + accountName + " auth=" + authorityName + " enabled=" + enabled + " syncable=" + syncable);
            }
        }
        return authority;
    }

    private PeriodicSync parsePeriodicSync(XmlPullParser parser, AuthorityInfo authority) {
        long flextime;
        Bundle extras = new Bundle();
        String periodValue = parser.getAttributeValue(null, "period");
        String flexValue = parser.getAttributeValue(null, "flex");
        try {
            long period = Long.parseLong(periodValue);
            try {
                flextime = Long.parseLong(flexValue);
            } catch (NullPointerException e) {
                flextime = calculateDefaultFlexTime(period);
                Log.d(TAG, "No flex time specified for this sync, using a default. period: " + period + " flex: " + flextime);
            } catch (NumberFormatException e2) {
                Log.e(TAG, "Error formatting value parsed for periodic sync flex: " + flexValue);
                flextime = calculateDefaultFlexTime(period);
            }
            PeriodicSync periodicSync = new PeriodicSync(authority.account, authority.authority, extras, period, flextime);
            authority.periodicSyncs.add(periodicSync);
            return periodicSync;
        } catch (NullPointerException e3) {
            Log.e(TAG, "the period of a periodic sync is null", e3);
            return null;
        } catch (NumberFormatException e4) {
            Log.e(TAG, "error parsing the period of a periodic sync", e4);
            return null;
        }
    }

    private void parseExtra(XmlPullParser parser, Bundle extras) {
        String name = parser.getAttributeValue(null, "name");
        String type = parser.getAttributeValue(null, "type");
        String value1 = parser.getAttributeValue(null, "value1");
        String value2 = parser.getAttributeValue(null, "value2");
        try {
            if ("long".equals(type)) {
                extras.putLong(name, Long.parseLong(value1));
            } else if ("integer".equals(type)) {
                extras.putInt(name, Integer.parseInt(value1));
            } else if ("double".equals(type)) {
                extras.putDouble(name, Double.parseDouble(value1));
            } else if ("float".equals(type)) {
                extras.putFloat(name, Float.parseFloat(value1));
            } else if ("boolean".equals(type)) {
                extras.putBoolean(name, Boolean.parseBoolean(value1));
            } else if ("string".equals(type)) {
                extras.putString(name, value1);
            } else if ("account".equals(type)) {
                extras.putParcelable(name, new Account(value1, value2));
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "error parsing bundle value", e);
        } catch (NumberFormatException e2) {
            Log.e(TAG, "error parsing bundle value", e2);
        }
    }

    private void writeAccountInfoLocked() {
        if (Log.isLoggable(TAG_FILE, 2)) {
            Log.v(TAG, "Writing new " + this.mAccountInfoFile.getBaseFile());
        }
        FileOutputStream fos = null;
        try {
            fos = this.mAccountInfoFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, "utf-8");
            out.startDocument(null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag(null, AccountManager.KEY_ACCOUNTS);
            out.attribute(null, "version", Integer.toString(2));
            out.attribute(null, XML_ATTR_NEXT_AUTHORITY_ID, Integer.toString(this.mNextAuthorityId));
            out.attribute(null, XML_ATTR_SYNC_RANDOM_OFFSET, Integer.toString(this.mSyncRandomOffset));
            int M = this.mMasterSyncAutomatically.size();
            for (int m = 0; m < M; m++) {
                int userId = this.mMasterSyncAutomatically.keyAt(m);
                Boolean listen = this.mMasterSyncAutomatically.valueAt(m);
                out.startTag(null, XML_TAG_LISTEN_FOR_TICKLES);
                out.attribute(null, "user", Integer.toString(userId));
                out.attribute(null, "enabled", Boolean.toString(listen.booleanValue()));
                out.endTag(null, XML_TAG_LISTEN_FOR_TICKLES);
            }
            int N = this.mAuthorities.size();
            for (int i = 0; i < N; i++) {
                AuthorityInfo authority = this.mAuthorities.valueAt(i);
                out.startTag(null, ContactsContract.Directory.DIRECTORY_AUTHORITY);
                out.attribute(null, "id", Integer.toString(authority.ident));
                out.attribute(null, "user", Integer.toString(authority.userId));
                out.attribute(null, "enabled", Boolean.toString(authority.enabled));
                if (authority.service == null) {
                    out.attribute(null, "account", authority.account.name);
                    out.attribute(null, "type", authority.account.type);
                    out.attribute(null, ContactsContract.Directory.DIRECTORY_AUTHORITY, authority.authority);
                } else {
                    out.attribute(null, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, authority.service.getPackageName());
                    out.attribute(null, "class", authority.service.getClassName());
                }
                if (authority.syncable < 0) {
                    out.attribute(null, "syncable", "unknown");
                } else {
                    out.attribute(null, "syncable", Boolean.toString(authority.syncable != 0));
                }
                Iterator i$ = authority.periodicSyncs.iterator();
                while (i$.hasNext()) {
                    PeriodicSync periodicSync = i$.next();
                    out.startTag(null, "periodicSync");
                    out.attribute(null, "period", Long.toString(periodicSync.period));
                    out.attribute(null, "flex", Long.toString(periodicSync.flexTime));
                    Bundle extras = periodicSync.extras;
                    extrasToXml(out, extras);
                    out.endTag(null, "periodicSync");
                }
                out.endTag(null, ContactsContract.Directory.DIRECTORY_AUTHORITY);
            }
            out.endTag(null, AccountManager.KEY_ACCOUNTS);
            out.endDocument();
            this.mAccountInfoFile.finishWrite(fos);
        } catch (IOException e1) {
            Log.w(TAG, "Error writing accounts", e1);
            if (fos != null) {
                this.mAccountInfoFile.failWrite(fos);
            }
        }
    }

    static int getIntColumn(Cursor c, String name) {
        return c.getInt(c.getColumnIndex(name));
    }

    static long getLongColumn(Cursor c, String name) {
        return c.getLong(c.getColumnIndex(name));
    }

    private void readAndDeleteLegacyAccountInfoLocked() {
        File file = this.mContext.getDatabasePath("syncmanager.db");
        if (!file.exists()) {
            return;
        }
        String path = file.getPath();
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(path, null, 1);
        } catch (SQLiteException e) {
        }
        if (db != null) {
            boolean hasType = db.getVersion() >= 11;
            if (Log.isLoggable(TAG_FILE, 2)) {
                Log.v(TAG, "Reading legacy sync accounts db");
            }
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables("stats, status");
            HashMap<String, String> map = new HashMap<>();
            map.put("_id", "status._id as _id");
            map.put("account", "stats.account as account");
            if (hasType) {
                map.put("account_type", "stats.account_type as account_type");
            }
            map.put(ContactsContract.Directory.DIRECTORY_AUTHORITY, "stats.authority as authority");
            map.put("totalElapsedTime", "totalElapsedTime");
            map.put("numSyncs", "numSyncs");
            map.put("numSourceLocal", "numSourceLocal");
            map.put("numSourcePoll", "numSourcePoll");
            map.put("numSourceServer", "numSourceServer");
            map.put("numSourceUser", "numSourceUser");
            map.put("lastSuccessSource", "lastSuccessSource");
            map.put("lastSuccessTime", "lastSuccessTime");
            map.put("lastFailureSource", "lastFailureSource");
            map.put("lastFailureTime", "lastFailureTime");
            map.put("lastFailureMesg", "lastFailureMesg");
            map.put("pending", "pending");
            qb.setProjectionMap(map);
            qb.appendWhere("stats._id = status.stats_id");
            Cursor c = qb.query(db, null, null, null, null, null, null);
            while (c.moveToNext()) {
                String accountName = c.getString(c.getColumnIndex("account"));
                String accountType = hasType ? c.getString(c.getColumnIndex("account_type")) : null;
                if (accountType == null) {
                    accountType = "com.google";
                }
                String authorityName = c.getString(c.getColumnIndex(ContactsContract.Directory.DIRECTORY_AUTHORITY));
                AuthorityInfo authority = getOrCreateAuthorityLocked(new Account(accountName, accountType), 0, authorityName, -1, false);
                if (authority != null) {
                    int i = this.mSyncStatus.size();
                    boolean found = false;
                    SyncStatusInfo st = null;
                    while (true) {
                        if (i <= 0) {
                            break;
                        }
                        i--;
                        st = this.mSyncStatus.valueAt(i);
                        if (st.authorityId == authority.ident) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        st = new SyncStatusInfo(authority.ident);
                        this.mSyncStatus.put(authority.ident, st);
                    }
                    st.totalElapsedTime = getLongColumn(c, "totalElapsedTime");
                    st.numSyncs = getIntColumn(c, "numSyncs");
                    st.numSourceLocal = getIntColumn(c, "numSourceLocal");
                    st.numSourcePoll = getIntColumn(c, "numSourcePoll");
                    st.numSourceServer = getIntColumn(c, "numSourceServer");
                    st.numSourceUser = getIntColumn(c, "numSourceUser");
                    st.numSourcePeriodic = 0;
                    st.lastSuccessSource = getIntColumn(c, "lastSuccessSource");
                    st.lastSuccessTime = getLongColumn(c, "lastSuccessTime");
                    st.lastFailureSource = getIntColumn(c, "lastFailureSource");
                    st.lastFailureTime = getLongColumn(c, "lastFailureTime");
                    st.lastFailureMesg = c.getString(c.getColumnIndex("lastFailureMesg"));
                    st.pending = getIntColumn(c, "pending") != 0;
                }
            }
            c.close();
            SQLiteQueryBuilder qb2 = new SQLiteQueryBuilder();
            qb2.setTables("settings");
            Cursor c2 = qb2.query(db, null, null, null, null, null, null);
            while (c2.moveToNext()) {
                String name = c2.getString(c2.getColumnIndex("name"));
                String value = c2.getString(c2.getColumnIndex("value"));
                if (name != null) {
                    if (name.equals("listen_for_tickles")) {
                        setMasterSyncAutomatically(value == null || Boolean.parseBoolean(value), 0);
                    } else if (name.startsWith("sync_provider_")) {
                        String provider = name.substring("sync_provider_".length(), name.length());
                        int i2 = this.mAuthorities.size();
                        while (i2 > 0) {
                            i2--;
                            AuthorityInfo authority2 = this.mAuthorities.valueAt(i2);
                            if (authority2.authority.equals(provider)) {
                                authority2.enabled = value == null || Boolean.parseBoolean(value);
                                authority2.syncable = 1;
                            }
                        }
                    }
                }
            }
            c2.close();
            db.close();
            new File(path).delete();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x00aa, code lost:
        android.util.Log.w(com.android.server.content.SyncStorageEngine.TAG, "Unknown status token: " + r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void readStatusLocked() {
        /*
            r5 = this;
            java.lang.String r0 = "SyncManagerFile"
            r1 = 2
            boolean r0 = android.util.Log.isLoggable(r0, r1)
            if (r0 == 0) goto L2a
            java.lang.String r0 = "SyncManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r2 = r1
            r2.<init>()
            java.lang.String r2 = "Reading "
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r5
            android.util.AtomicFile r2 = r2.mStatusFile
            java.io.File r2 = r2.getBaseFile()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            int r0 = android.util.Log.v(r0, r1)
        L2a:
            r0 = r5
            android.util.AtomicFile r0 = r0.mStatusFile     // Catch: java.io.IOException -> Lca
            byte[] r0 = r0.readFully()     // Catch: java.io.IOException -> Lca
            r6 = r0
            android.os.Parcel r0 = android.os.Parcel.obtain()     // Catch: java.io.IOException -> Lca
            r7 = r0
            r0 = r7
            r1 = r6
            r2 = 0
            r3 = r6
            int r3 = r3.length     // Catch: java.io.IOException -> Lca
            r0.unmarshall(r1, r2, r3)     // Catch: java.io.IOException -> Lca
            r0 = r7
            r1 = 0
            r0.setDataPosition(r1)     // Catch: java.io.IOException -> Lca
        L43:
            r0 = r7
            int r0 = r0.readInt()     // Catch: java.io.IOException -> Lca
            r1 = r0
            r8 = r1
            if (r0 == 0) goto Lc7
            r0 = r8
            r1 = 100
            if (r0 != r1) goto Laa
            android.content.SyncStatusInfo r0 = new android.content.SyncStatusInfo     // Catch: java.io.IOException -> Lca
            r1 = r0
            r2 = r7
            r1.<init>(r2)     // Catch: java.io.IOException -> Lca
            r9 = r0
            r0 = r5
            android.util.SparseArray<com.android.server.content.SyncStorageEngine$AuthorityInfo> r0 = r0.mAuthorities     // Catch: java.io.IOException -> Lca
            r1 = r9
            int r1 = r1.authorityId     // Catch: java.io.IOException -> Lca
            int r0 = r0.indexOfKey(r1)     // Catch: java.io.IOException -> Lca
            if (r0 < 0) goto La7
            r0 = r9
            r1 = 0
            r0.pending = r1     // Catch: java.io.IOException -> Lca
            java.lang.String r0 = "SyncManagerFile"
            r1 = 2
            boolean r0 = android.util.Log.isLoggable(r0, r1)     // Catch: java.io.IOException -> Lca
            if (r0 == 0) goto L99
            java.lang.String r0 = "SyncManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.io.IOException -> Lca
            r2 = r1
            r2.<init>()     // Catch: java.io.IOException -> Lca
            java.lang.String r2 = "Adding status for id "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.io.IOException -> Lca
            r2 = r9
            int r2 = r2.authorityId     // Catch: java.io.IOException -> Lca
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.io.IOException -> Lca
            java.lang.String r1 = r1.toString()     // Catch: java.io.IOException -> Lca
            int r0 = android.util.Log.v(r0, r1)     // Catch: java.io.IOException -> Lca
        L99:
            r0 = r5
            android.util.SparseArray<android.content.SyncStatusInfo> r0 = r0.mSyncStatus     // Catch: java.io.IOException -> Lca
            r1 = r9
            int r1 = r1.authorityId     // Catch: java.io.IOException -> Lca
            r2 = r9
            r0.put(r1, r2)     // Catch: java.io.IOException -> Lca
        La7:
            goto L43
        Laa:
            java.lang.String r0 = "SyncManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.io.IOException -> Lca
            r2 = r1
            r2.<init>()     // Catch: java.io.IOException -> Lca
            java.lang.String r2 = "Unknown status token: "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.io.IOException -> Lca
            r2 = r8
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.io.IOException -> Lca
            java.lang.String r1 = r1.toString()     // Catch: java.io.IOException -> Lca
            int r0 = android.util.Log.w(r0, r1)     // Catch: java.io.IOException -> Lca
            goto Lc7
        Lc7:
            goto Ld4
        Lca:
            r6 = move-exception
            java.lang.String r0 = "SyncManager"
            java.lang.String r1 = "No initial status"
            int r0 = android.util.Log.i(r0, r1)
        Ld4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.readStatusLocked():void");
    }

    private void writeStatusLocked() {
        if (Log.isLoggable(TAG_FILE, 2)) {
            Log.v(TAG, "Writing new " + this.mStatusFile.getBaseFile());
        }
        removeMessages(1);
        FileOutputStream fos = null;
        try {
            fos = this.mStatusFile.startWrite();
            Parcel out = Parcel.obtain();
            int N = this.mSyncStatus.size();
            for (int i = 0; i < N; i++) {
                SyncStatusInfo status = this.mSyncStatus.valueAt(i);
                out.writeInt(100);
                status.writeToParcel(out, 0);
            }
            out.writeInt(0);
            fos.write(out.marshall());
            out.recycle();
            this.mStatusFile.finishWrite(fos);
        } catch (IOException e1) {
            Log.w(TAG, "Error writing status", e1);
            if (fos != null) {
                this.mStatusFile.failWrite(fos);
            }
        }
    }

    private void writePendingOperationsLocked() {
        int N = this.mPendingOperations.size();
        try {
            if (N == 0) {
                if (Log.isLoggable(TAG_FILE, 2)) {
                    Log.v(TAG_FILE, "Truncating " + this.mPendingFile.getBaseFile());
                }
                this.mPendingFile.truncate();
                return;
            }
            if (Log.isLoggable(TAG_FILE, 2)) {
                Log.v(TAG_FILE, "Writing new " + this.mPendingFile.getBaseFile());
            }
            FileOutputStream fos = this.mPendingFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, "utf-8");
            for (int i = 0; i < N; i++) {
                PendingOperation pop = this.mPendingOperations.get(i);
                writePendingOperationLocked(pop, out);
            }
            out.endDocument();
            this.mPendingFile.finishWrite(fos);
        } catch (IOException e1) {
            Log.w(TAG, "Error writing pending operations", e1);
            if (0 != 0) {
                this.mPendingFile.failWrite(null);
            }
        }
    }

    private void writePendingOperationLocked(PendingOperation pop, XmlSerializer out) throws IOException {
        out.startTag(null, "op");
        out.attribute(null, "version", Integer.toString(3));
        out.attribute(null, XML_ATTR_AUTHORITYID, Integer.toString(pop.authorityId));
        out.attribute(null, XML_ATTR_SOURCE, Integer.toString(pop.syncSource));
        out.attribute(null, "expedited", Boolean.toString(pop.expedited));
        out.attribute(null, "reason", Integer.toString(pop.reason));
        extrasToXml(out, pop.extras);
        out.endTag(null, "op");
    }

    private void extrasToXml(XmlSerializer out, Bundle extras) throws IOException {
        for (String key : extras.keySet()) {
            out.startTag(null, "extra");
            out.attribute(null, "name", key);
            Object value = extras.get(key);
            if (value instanceof Long) {
                out.attribute(null, "type", "long");
                out.attribute(null, "value1", value.toString());
            } else if (value instanceof Integer) {
                out.attribute(null, "type", "integer");
                out.attribute(null, "value1", value.toString());
            } else if (value instanceof Boolean) {
                out.attribute(null, "type", "boolean");
                out.attribute(null, "value1", value.toString());
            } else if (value instanceof Float) {
                out.attribute(null, "type", "float");
                out.attribute(null, "value1", value.toString());
            } else if (value instanceof Double) {
                out.attribute(null, "type", "double");
                out.attribute(null, "value1", value.toString());
            } else if (value instanceof String) {
                out.attribute(null, "type", "string");
                out.attribute(null, "value1", value.toString());
            } else if (value instanceof Account) {
                out.attribute(null, "type", "account");
                out.attribute(null, "value1", ((Account) value).name);
                out.attribute(null, "value2", ((Account) value).type);
            }
            out.endTag(null, "extra");
        }
    }

    private void requestSync(Account account, int userId, int reason, String authority, Bundle extras) {
        if (Process.myUid() == 1000 && this.mSyncRequestListener != null) {
            this.mSyncRequestListener.onSyncRequest(account, userId, reason, authority, extras);
        } else {
            ContentResolver.requestSync(account, authority, extras);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0091, code lost:
        android.util.Log.w(com.android.server.content.SyncStorageEngine.TAG, "Unknown stats token: " + r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void readStatisticsLocked() {
        /*
            r5 = this;
            r0 = r5
            android.util.AtomicFile r0 = r0.mStatisticsFile     // Catch: java.io.IOException -> Lb1
            byte[] r0 = r0.readFully()     // Catch: java.io.IOException -> Lb1
            r6 = r0
            android.os.Parcel r0 = android.os.Parcel.obtain()     // Catch: java.io.IOException -> Lb1
            r7 = r0
            r0 = r7
            r1 = r6
            r2 = 0
            r3 = r6
            int r3 = r3.length     // Catch: java.io.IOException -> Lb1
            r0.unmarshall(r1, r2, r3)     // Catch: java.io.IOException -> Lb1
            r0 = r7
            r1 = 0
            r0.setDataPosition(r1)     // Catch: java.io.IOException -> Lb1
            r0 = 0
            r9 = r0
        L1c:
            r0 = r7
            int r0 = r0.readInt()     // Catch: java.io.IOException -> Lb1
            r1 = r0
            r8 = r1
            if (r0 == 0) goto Lae
            r0 = r8
            r1 = 101(0x65, float:1.42E-43)
            if (r0 == r1) goto L31
            r0 = r8
            r1 = 100
            if (r0 != r1) goto L91
        L31:
            r0 = r7
            int r0 = r0.readInt()     // Catch: java.io.IOException -> Lb1
            r10 = r0
            r0 = r8
            r1 = 100
            if (r0 != r1) goto L49
            r0 = r10
            r1 = 2009(0x7d9, float:2.815E-42)
            int r0 = r0 - r1
            r1 = 14245(0x37a5, float:1.9961E-41)
            int r0 = r0 + r1
            r10 = r0
        L49:
            com.android.server.content.SyncStorageEngine$DayStats r0 = new com.android.server.content.SyncStorageEngine$DayStats     // Catch: java.io.IOException -> Lb1
            r1 = r0
            r2 = r10
            r1.<init>(r2)     // Catch: java.io.IOException -> Lb1
            r11 = r0
            r0 = r11
            r1 = r7
            int r1 = r1.readInt()     // Catch: java.io.IOException -> Lb1
            r0.successCount = r1     // Catch: java.io.IOException -> Lb1
            r0 = r11
            r1 = r7
            long r1 = r1.readLong()     // Catch: java.io.IOException -> Lb1
            r0.successTime = r1     // Catch: java.io.IOException -> Lb1
            r0 = r11
            r1 = r7
            int r1 = r1.readInt()     // Catch: java.io.IOException -> Lb1
            r0.failureCount = r1     // Catch: java.io.IOException -> Lb1
            r0 = r11
            r1 = r7
            long r1 = r1.readLong()     // Catch: java.io.IOException -> Lb1
            r0.failureTime = r1     // Catch: java.io.IOException -> Lb1
            r0 = r9
            r1 = r5
            com.android.server.content.SyncStorageEngine$DayStats[] r1 = r1.mDayStats     // Catch: java.io.IOException -> Lb1
            int r1 = r1.length     // Catch: java.io.IOException -> Lb1
            if (r0 >= r1) goto L8e
            r0 = r5
            com.android.server.content.SyncStorageEngine$DayStats[] r0 = r0.mDayStats     // Catch: java.io.IOException -> Lb1
            r1 = r9
            r2 = r11
            r0[r1] = r2     // Catch: java.io.IOException -> Lb1
            int r9 = r9 + 1
        L8e:
            goto L1c
        L91:
            java.lang.String r0 = "SyncManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.io.IOException -> Lb1
            r2 = r1
            r2.<init>()     // Catch: java.io.IOException -> Lb1
            java.lang.String r2 = "Unknown stats token: "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.io.IOException -> Lb1
            r2 = r8
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.io.IOException -> Lb1
            java.lang.String r1 = r1.toString()     // Catch: java.io.IOException -> Lb1
            int r0 = android.util.Log.w(r0, r1)     // Catch: java.io.IOException -> Lb1
            goto Lae
        Lae:
            goto Lbb
        Lb1:
            r6 = move-exception
            java.lang.String r0 = "SyncManager"
            java.lang.String r1 = "No initial statistics"
            int r0 = android.util.Log.i(r0, r1)
        Lbb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.readStatisticsLocked():void");
    }

    private void writeStatisticsLocked() {
        DayStats ds;
        if (Log.isLoggable(TAG_FILE, 2)) {
            Log.v(TAG, "Writing new " + this.mStatisticsFile.getBaseFile());
        }
        removeMessages(2);
        FileOutputStream fos = null;
        try {
            fos = this.mStatisticsFile.startWrite();
            Parcel out = Parcel.obtain();
            int N = this.mDayStats.length;
            for (int i = 0; i < N && (ds = this.mDayStats[i]) != null; i++) {
                out.writeInt(101);
                out.writeInt(ds.day);
                out.writeInt(ds.successCount);
                out.writeLong(ds.successTime);
                out.writeInt(ds.failureCount);
                out.writeLong(ds.failureTime);
            }
            out.writeInt(0);
            fos.write(out.marshall());
            out.recycle();
            this.mStatisticsFile.finishWrite(fos);
        } catch (IOException e1) {
            Log.w(TAG, "Error writing stats", e1);
            if (fos != null) {
                this.mStatisticsFile.failWrite(fos);
            }
        }
    }

    public void dumpPendingOperations(StringBuilder sb) {
        sb.append("Pending Ops: ").append(this.mPendingOperations.size()).append(" operation(s)\n");
        Iterator i$ = this.mPendingOperations.iterator();
        while (i$.hasNext()) {
            PendingOperation pop = i$.next();
            sb.append(Separators.LPAREN + pop.account).append(", u" + pop.userId).append(", " + pop.authority).append(", " + pop.extras).append(")\n");
        }
    }
}