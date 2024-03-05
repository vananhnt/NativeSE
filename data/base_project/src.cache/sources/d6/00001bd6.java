package com.android.server;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.IBackupAgent;
import android.app.PendingIntent;
import android.app.backup.BackupDataOutput;
import android.app.backup.FullBackup;
import android.app.backup.IBackupManager;
import android.app.backup.IFullBackupRestoreObserver;
import android.app.backup.IRestoreObserver;
import android.app.backup.IRestoreSession;
import android.app.backup.RestoreSet;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.os.storage.IMountService;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.StringBuilderPrinter;
import android.util.TimedRemoteCaller;
import com.android.internal.backup.IBackupTransport;
import com.android.internal.backup.IObbBackupService;
import com.android.internal.location.GpsNetInitiatedHandler;
import com.android.server.PackageManagerBackupAgent;
import gov.nist.core.Separators;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.DeflaterOutputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/*  JADX ERROR: NullPointerException in pass: ExtractFieldInit
    java.lang.NullPointerException
    	at jadx.core.utils.BlockUtils.isAllBlocksEmpty(BlockUtils.java:977)
    	at jadx.core.dex.visitors.ExtractFieldInit.getConstructorsList(ExtractFieldInit.java:379)
    	at jadx.core.dex.visitors.ExtractFieldInit.moveCommonFieldsInit(ExtractFieldInit.java:128)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:51)
    */
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: BackupManagerService.class */
public class BackupManagerService extends IBackupManager.Stub {
    private static final String TAG = "BackupManagerService";
    private static final boolean DEBUG = true;
    private static final boolean MORE_DEBUG = false;
    static final String BACKUP_MANIFEST_FILENAME = "_manifest";
    static final int BACKUP_MANIFEST_VERSION = 1;
    static final String BACKUP_FILE_HEADER_MAGIC = "ANDROID BACKUP\n";
    static final int BACKUP_FILE_VERSION = 1;
    static final boolean COMPRESS_FULL_BACKUPS = true;
    static final String SHARED_BACKUP_AGENT_PACKAGE = "com.android.sharedstoragebackup";
    private static final long BACKUP_INTERVAL = 3600000;
    private static final int FUZZ_MILLIS = 300000;
    private static final long FIRST_BACKUP_INTERVAL = 43200000;
    private static final String RUN_BACKUP_ACTION = "android.app.backup.intent.RUN";
    private static final String RUN_INITIALIZE_ACTION = "android.app.backup.intent.INIT";
    private static final String RUN_CLEAR_ACTION = "android.app.backup.intent.CLEAR";
    private static final int MSG_RUN_BACKUP = 1;
    private static final int MSG_RUN_FULL_BACKUP = 2;
    private static final int MSG_RUN_RESTORE = 3;
    private static final int MSG_RUN_CLEAR = 4;
    private static final int MSG_RUN_INITIALIZE = 5;
    private static final int MSG_RUN_GET_RESTORE_SETS = 6;
    private static final int MSG_TIMEOUT = 7;
    private static final int MSG_RESTORE_TIMEOUT = 8;
    private static final int MSG_FULL_CONFIRMATION_TIMEOUT = 9;
    private static final int MSG_RUN_FULL_RESTORE = 10;
    static final int MSG_BACKUP_RESTORE_STEP = 20;
    static final int MSG_OP_COMPLETE = 21;
    static final long TIMEOUT_INTERVAL = 10000;
    static final long TIMEOUT_BACKUP_INTERVAL = 30000;
    static final long TIMEOUT_FULL_BACKUP_INTERVAL = 300000;
    static final long TIMEOUT_SHARED_BACKUP_INTERVAL = 1800000;
    static final long TIMEOUT_RESTORE_INTERVAL = 60000;
    static final long TIMEOUT_FULL_CONFIRMATION = 60000;
    private Context mContext;
    private PackageManager mPackageManager;
    IPackageManager mPackageManagerBinder;
    private IActivityManager mActivityManager;
    private PowerManager mPowerManager;
    private AlarmManager mAlarmManager;
    private IMountService mMountService;
    IBackupManager mBackupManagerBinder;
    boolean mEnabled;
    boolean mProvisioned;
    boolean mAutoRestore;
    PowerManager.WakeLock mWakelock;
    HandlerThread mHandlerThread;
    BackupHandler mBackupHandler;
    PendingIntent mRunBackupIntent;
    PendingIntent mRunInitIntent;
    BroadcastReceiver mRunBackupReceiver;
    BroadcastReceiver mRunInitReceiver;
    final SparseArray<HashSet<String>> mBackupParticipants;
    HashMap<String, BackupRequest> mPendingBackups;
    static final String PACKAGE_MANAGER_SENTINEL = "@pm@";
    final Object mQueueLock;
    final Object mAgentConnectLock;
    IBackupAgent mConnectedAgent;
    volatile boolean mBackupRunning;
    volatile boolean mConnecting;
    volatile long mLastBackupPass;
    volatile long mNextBackupPass;
    static final boolean DEBUG_BACKUP_TRACE = true;
    final List<String> mBackupTrace;
    final Object mClearDataLock;
    volatile boolean mClearingData;
    final HashMap<String, IBackupTransport> mTransports;
    String mCurrentTransport;
    IBackupTransport mLocalTransport;
    IBackupTransport mGoogleTransport;
    ActiveRestoreSession mActiveRestoreSession;
    ContentObserver mProvisionedObserver;
    static final int OP_PENDING = 0;
    static final int OP_ACKNOWLEDGED = 1;
    static final int OP_TIMEOUT = -1;
    final SparseArray<Operation> mCurrentOperations;
    final Object mCurrentOpLock;
    final Random mTokenGenerator;
    final SparseArray<FullParams> mFullConfirmations;
    File mBaseStateDir;
    File mDataDir;
    File mJournalDir;
    File mJournal;
    private final SecureRandom mRng;
    private String mPasswordHash;
    private File mPasswordHashFile;
    private byte[] mPasswordSalt;
    static final int PBKDF2_HASH_ROUNDS = 10000;
    static final int PBKDF2_KEY_SIZE = 256;
    static final int PBKDF2_SALT_SIZE = 512;
    static final String ENCRYPTION_ALGORITHM_NAME = "AES-256";
    private File mEverStored;
    HashSet<String> mEverStoredApps;
    static final int CURRENT_ANCESTRAL_RECORD_VERSION = 1;
    File mTokenFile;
    Set<String> mAncestralPackages;
    long mAncestralToken;
    long mCurrentToken;
    static final String INIT_SENTINEL_FILE_NAME = "_need_init_";
    HashSet<String> mPendingInits;
    BroadcastReceiver mBroadcastReceiver;
    ServiceConnection mGoogleConnection;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BackupManagerService$BackupRestoreTask.class */
    public interface BackupRestoreTask {
        void execute();

        void operationComplete();

        void handleTimeout();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BackupManagerService$BackupState.class */
    public enum BackupState {
        INITIAL,
        RUNNING_QUEUE,
        FINAL
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BackupManagerService$RestorePolicy.class */
    public enum RestorePolicy {
        IGNORE,
        ACCEPT,
        ACCEPT_IF_APK
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BackupManagerService$RestoreState.class */
    public enum RestoreState {
        INITIAL,
        DOWNLOAD_DATA,
        PM_METADATA,
        RUNNING_QUEUE,
        FINAL
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.<init>(android.content.Context):void, file: BackupManagerService.class
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
    public BackupManagerService(android.content.Context r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.<init>(android.content.Context):void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.<init>(android.content.Context):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.initPackageTracking():void, file: BackupManagerService.class
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
    private void initPackageTracking() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.initPackageTracking():void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.initPackageTracking():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.parseLeftoverJournals():void, file: BackupManagerService.class
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
    private void parseLeftoverJournals() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.parseLeftoverJournals():void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.parseLeftoverJournals():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.setBackupPassword(java.lang.String, java.lang.String):boolean, file: BackupManagerService.class
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
    @Override // android.app.backup.IBackupManager
    public boolean setBackupPassword(java.lang.String r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.setBackupPassword(java.lang.String, java.lang.String):boolean, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.setBackupPassword(java.lang.String, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.logBackupComplete(java.lang.String):void, file: BackupManagerService.class
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
    void logBackupComplete(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.logBackupComplete(java.lang.String):void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.logBackupComplete(java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.removeEverBackedUp(java.lang.String):void, file: BackupManagerService.class
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
    void removeEverBackedUp(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.removeEverBackedUp(java.lang.String):void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.removeEverBackedUp(java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.writeToJournalLocked(java.lang.String):void, file: BackupManagerService.class
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
    private void writeToJournalLocked(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.writeToJournalLocked(java.lang.String):void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.writeToJournalLocked(java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.fullBackup(android.os.ParcelFileDescriptor, boolean, boolean, boolean, boolean, boolean, java.lang.String[]):void, file: BackupManagerService.class
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
    @Override // android.app.backup.IBackupManager
    public void fullBackup(android.os.ParcelFileDescriptor r1, boolean r2, boolean r3, boolean r4, boolean r5, boolean r6, java.lang.String[] r7) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.fullBackup(android.os.ParcelFileDescriptor, boolean, boolean, boolean, boolean, boolean, java.lang.String[]):void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.fullBackup(android.os.ParcelFileDescriptor, boolean, boolean, boolean, boolean, boolean, java.lang.String[]):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.fullRestore(android.os.ParcelFileDescriptor):void, file: BackupManagerService.class
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
    @Override // android.app.backup.IBackupManager
    public void fullRestore(android.os.ParcelFileDescriptor r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.fullRestore(android.os.ParcelFileDescriptor):void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.fullRestore(android.os.ParcelFileDescriptor):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.acknowledgeFullBackupOrRestore(int, boolean, java.lang.String, java.lang.String, android.app.backup.IFullBackupRestoreObserver):void, file: BackupManagerService.class
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
    @Override // android.app.backup.IBackupManager
    public void acknowledgeFullBackupOrRestore(int r1, boolean r2, java.lang.String r3, java.lang.String r4, android.app.backup.IFullBackupRestoreObserver r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.acknowledgeFullBackupOrRestore(int, boolean, java.lang.String, java.lang.String, android.app.backup.IFullBackupRestoreObserver):void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.acknowledgeFullBackupOrRestore(int, boolean, java.lang.String, java.lang.String, android.app.backup.IFullBackupRestoreObserver):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.setBackupEnabled(boolean):void, file: BackupManagerService.class
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
    @Override // android.app.backup.IBackupManager
    public void setBackupEnabled(boolean r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.setBackupEnabled(boolean):void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.setBackupEnabled(boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: BackupManagerService.class
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
    @Override // android.os.Binder
    public void dump(java.io.FileDescriptor r1, java.io.PrintWriter r2, java.lang.String[] r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: BackupManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    static /* synthetic */ IBackupTransport access$100(BackupManagerService x0, String x1) {
        return x0.getTransport(x1);
    }

    static /* synthetic */ AlarmManager access$400(BackupManagerService x0) {
        return x0.mAlarmManager;
    }

    static /* synthetic */ PackageManager access$600(BackupManagerService x0) {
        return x0.mPackageManager;
    }

    static /* synthetic */ Context access$900(BackupManagerService x0) {
        return x0.mContext;
    }

    static /* synthetic */ void access$1000(BackupManagerService x0, ParcelFileDescriptor x1, OutputStream x2) throws IOException {
        x0.routeSocketDataToOutput(x1, x2);
    }

    static /* synthetic */ boolean access$1900(BackupManagerService x0, Signature[] x1, PackageInfo x2) {
        return x0.signaturesMatch(x1, x2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BackupManagerService$BackupRequest.class */
    public class BackupRequest {
        public String packageName;

        BackupRequest(String pkgName) {
            this.packageName = pkgName;
        }

        public String toString() {
            return "BackupRequest{pkg=" + this.packageName + "}";
        }
    }

    /* loaded from: BackupManagerService$ProvisionedObserver.class */
    class ProvisionedObserver extends ContentObserver {
        public ProvisionedObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            boolean wasProvisioned = BackupManagerService.this.mProvisioned;
            boolean isProvisioned = BackupManagerService.this.deviceIsProvisioned();
            BackupManagerService.this.mProvisioned = wasProvisioned || isProvisioned;
            synchronized (BackupManagerService.this.mQueueLock) {
                if (BackupManagerService.this.mProvisioned && !wasProvisioned && BackupManagerService.this.mEnabled) {
                    BackupManagerService.this.startBackupAlarmsLocked(43200000L);
                }
            }
        }
    }

    /* loaded from: BackupManagerService$RestoreGetSetsParams.class */
    class RestoreGetSetsParams {
        public IBackupTransport transport;
        public ActiveRestoreSession session;
        public IRestoreObserver observer;

        RestoreGetSetsParams(IBackupTransport _transport, ActiveRestoreSession _session, IRestoreObserver _observer) {
            this.transport = _transport;
            this.session = _session;
            this.observer = _observer;
        }
    }

    /* loaded from: BackupManagerService$RestoreParams.class */
    class RestoreParams {
        public IBackupTransport transport;
        public IRestoreObserver observer;
        public long token;
        public PackageInfo pkgInfo;
        public int pmToken;
        public boolean needFullBackup;
        public String[] filterSet;

        RestoreParams(IBackupTransport _transport, IRestoreObserver _obs, long _token, PackageInfo _pkg, int _pmToken, boolean _needFullBackup) {
            this.transport = _transport;
            this.observer = _obs;
            this.token = _token;
            this.pkgInfo = _pkg;
            this.pmToken = _pmToken;
            this.needFullBackup = _needFullBackup;
            this.filterSet = null;
        }

        RestoreParams(IBackupTransport _transport, IRestoreObserver _obs, long _token, boolean _needFullBackup) {
            this.transport = _transport;
            this.observer = _obs;
            this.token = _token;
            this.pkgInfo = null;
            this.pmToken = 0;
            this.needFullBackup = _needFullBackup;
            this.filterSet = null;
        }

        RestoreParams(IBackupTransport _transport, IRestoreObserver _obs, long _token, String[] _filterSet, boolean _needFullBackup) {
            this.transport = _transport;
            this.observer = _obs;
            this.token = _token;
            this.pkgInfo = null;
            this.pmToken = 0;
            this.needFullBackup = _needFullBackup;
            this.filterSet = _filterSet;
        }
    }

    /* loaded from: BackupManagerService$ClearParams.class */
    class ClearParams {
        public IBackupTransport transport;
        public PackageInfo packageInfo;

        ClearParams(IBackupTransport _transport, PackageInfo _info) {
            this.transport = _transport;
            this.packageInfo = _info;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BackupManagerService$FullParams.class */
    public class FullParams {
        public ParcelFileDescriptor fd;
        public final AtomicBoolean latch = new AtomicBoolean(false);
        public IFullBackupRestoreObserver observer;
        public String curPassword;
        public String encryptPassword;

        FullParams() {
        }
    }

    /* loaded from: BackupManagerService$FullBackupParams.class */
    class FullBackupParams extends FullParams {
        public boolean includeApks;
        public boolean includeObbs;
        public boolean includeShared;
        public boolean allApps;
        public boolean includeSystem;
        public String[] packages;

        FullBackupParams(ParcelFileDescriptor output, boolean saveApks, boolean saveObbs, boolean saveShared, boolean doAllApps, boolean doSystem, String[] pkgList) {
            super();
            this.fd = output;
            this.includeApks = saveApks;
            this.includeObbs = saveObbs;
            this.includeShared = saveShared;
            this.allApps = doAllApps;
            this.includeSystem = doSystem;
            this.packages = pkgList;
        }
    }

    /* loaded from: BackupManagerService$FullRestoreParams.class */
    class FullRestoreParams extends FullParams {
        FullRestoreParams(ParcelFileDescriptor input) {
            super();
            this.fd = input;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BackupManagerService$Operation.class */
    public class Operation {
        public int state;
        public BackupRestoreTask callback;

        Operation(int initialState, BackupRestoreTask callbackObj) {
            this.state = initialState;
            this.callback = callbackObj;
        }
    }

    int generateToken() {
        int token;
        do {
            synchronized (this.mTokenGenerator) {
                token = this.mTokenGenerator.nextInt();
            }
        } while (token < 0);
        return token;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: BackupManagerService$BackupHandler.class */
    public class BackupHandler extends Handler {
        public BackupHandler(Looper looper) {
            super(looper);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            HashSet<String> queue;
            switch (msg.what) {
                case 1:
                    BackupManagerService.this.mLastBackupPass = System.currentTimeMillis();
                    BackupManagerService.this.mNextBackupPass = BackupManagerService.this.mLastBackupPass + 3600000;
                    IBackupTransport transport = BackupManagerService.this.getTransport(BackupManagerService.this.mCurrentTransport);
                    if (transport == null) {
                        Slog.v(BackupManagerService.TAG, "Backup requested but no transport available");
                        synchronized (BackupManagerService.this.mQueueLock) {
                            BackupManagerService.this.mBackupRunning = false;
                        }
                        BackupManagerService.this.mWakelock.release();
                        return;
                    }
                    ArrayList<BackupRequest> queue2 = new ArrayList<>();
                    File oldJournal = BackupManagerService.this.mJournal;
                    synchronized (BackupManagerService.this.mQueueLock) {
                        if (BackupManagerService.this.mPendingBackups.size() > 0) {
                            for (BackupRequest b : BackupManagerService.this.mPendingBackups.values()) {
                                queue2.add(b);
                            }
                            Slog.v(BackupManagerService.TAG, "clearing pending backups");
                            BackupManagerService.this.mPendingBackups.clear();
                            BackupManagerService.this.mJournal = null;
                        }
                    }
                    if (queue2.size() > 0) {
                        PerformBackupTask pbt = new PerformBackupTask(transport, queue2, oldJournal);
                        Message pbtMessage = obtainMessage(20, pbt);
                        sendMessage(pbtMessage);
                        return;
                    }
                    Slog.v(BackupManagerService.TAG, "Backup requested but nothing pending");
                    synchronized (BackupManagerService.this.mQueueLock) {
                        BackupManagerService.this.mBackupRunning = false;
                    }
                    BackupManagerService.this.mWakelock.release();
                    return;
                case 2:
                    FullBackupParams params = (FullBackupParams) msg.obj;
                    PerformFullBackupTask task = new PerformFullBackupTask(params.fd, params.observer, params.includeApks, params.includeObbs, params.includeShared, params.curPassword, params.encryptPassword, params.allApps, params.includeSystem, params.packages, params.latch);
                    new Thread(task).start();
                    return;
                case 3:
                    RestoreParams params2 = (RestoreParams) msg.obj;
                    Slog.d(BackupManagerService.TAG, "MSG_RUN_RESTORE observer=" + params2.observer);
                    PerformRestoreTask task2 = new PerformRestoreTask(params2.transport, params2.observer, params2.token, params2.pkgInfo, params2.pmToken, params2.needFullBackup, params2.filterSet);
                    Message restoreMsg = obtainMessage(20, task2);
                    sendMessage(restoreMsg);
                    return;
                case 4:
                    ClearParams params3 = (ClearParams) msg.obj;
                    new PerformClearTask(params3.transport, params3.packageInfo).run();
                    return;
                case 5:
                    synchronized (BackupManagerService.this.mQueueLock) {
                        queue = new HashSet<>(BackupManagerService.this.mPendingInits);
                        BackupManagerService.this.mPendingInits.clear();
                    }
                    new PerformInitializeTask(queue).run();
                    return;
                case 6:
                    RestoreGetSetsParams params4 = (RestoreGetSetsParams) msg.obj;
                    try {
                        try {
                            RestoreSet[] sets = params4.transport.getAvailableRestoreSets();
                            synchronized (params4.session) {
                                params4.session.mRestoreSets = sets;
                            }
                            if (sets == null) {
                                EventLog.writeEvent((int) EventLogTags.RESTORE_TRANSPORT_FAILURE, new Object[0]);
                            }
                            if (params4.observer != null) {
                                try {
                                    params4.observer.restoreSetsAvailable(sets);
                                } catch (RemoteException e) {
                                    Slog.e(BackupManagerService.TAG, "Unable to report listing to observer");
                                } catch (Exception e2) {
                                    Slog.e(BackupManagerService.TAG, "Restore observer threw", e2);
                                }
                            }
                            removeMessages(8);
                            sendEmptyMessageDelayed(8, DateUtils.MINUTE_IN_MILLIS);
                            BackupManagerService.this.mWakelock.release();
                            return;
                        } catch (Exception e3) {
                            Slog.e(BackupManagerService.TAG, "Error from transport getting set list");
                            if (params4.observer != null) {
                                try {
                                    params4.observer.restoreSetsAvailable(null);
                                } catch (RemoteException e4) {
                                    Slog.e(BackupManagerService.TAG, "Unable to report listing to observer");
                                } catch (Exception e5) {
                                    Slog.e(BackupManagerService.TAG, "Restore observer threw", e5);
                                }
                            }
                            removeMessages(8);
                            sendEmptyMessageDelayed(8, DateUtils.MINUTE_IN_MILLIS);
                            BackupManagerService.this.mWakelock.release();
                            return;
                        }
                    } catch (Throwable th) {
                        if (params4.observer != null) {
                            try {
                                params4.observer.restoreSetsAvailable(null);
                            } catch (RemoteException e6) {
                                Slog.e(BackupManagerService.TAG, "Unable to report listing to observer");
                            } catch (Exception e7) {
                                Slog.e(BackupManagerService.TAG, "Restore observer threw", e7);
                            }
                        }
                        removeMessages(8);
                        sendEmptyMessageDelayed(8, DateUtils.MINUTE_IN_MILLIS);
                        BackupManagerService.this.mWakelock.release();
                        throw th;
                    }
                case 7:
                    BackupManagerService.this.handleTimeout(msg.arg1, msg.obj);
                    return;
                case 8:
                    synchronized (BackupManagerService.this) {
                        if (BackupManagerService.this.mActiveRestoreSession != null) {
                            Slog.w(BackupManagerService.TAG, "Restore session timed out; aborting");
                            ActiveRestoreSession activeRestoreSession = BackupManagerService.this.mActiveRestoreSession;
                            activeRestoreSession.getClass();
                            post(new ActiveRestoreSession.EndRestoreRunnable(BackupManagerService.this, BackupManagerService.this.mActiveRestoreSession));
                        }
                    }
                    break;
                case 9:
                    break;
                case 10:
                    FullRestoreParams params5 = (FullRestoreParams) msg.obj;
                    PerformFullRestoreTask task3 = new PerformFullRestoreTask(params5.fd, params5.curPassword, params5.encryptPassword, params5.observer, params5.latch);
                    new Thread(task3).start();
                    return;
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                default:
                    return;
                case 20:
                    try {
                        BackupRestoreTask task4 = (BackupRestoreTask) msg.obj;
                        task4.execute();
                        return;
                    } catch (ClassCastException e8) {
                        Slog.e(BackupManagerService.TAG, "Invalid backup task in flight, obj=" + msg.obj);
                        return;
                    }
                case 21:
                    try {
                        BackupRestoreTask task5 = (BackupRestoreTask) msg.obj;
                        task5.operationComplete();
                        return;
                    } catch (ClassCastException e9) {
                        Slog.e(BackupManagerService.TAG, "Invalid completion in flight, obj=" + msg.obj);
                        return;
                    }
            }
            synchronized (BackupManagerService.this.mFullConfirmations) {
                FullParams params6 = BackupManagerService.this.mFullConfirmations.get(msg.arg1);
                if (params6 != null) {
                    Slog.i(BackupManagerService.TAG, "Full backup/restore timed out waiting for user confirmation");
                    BackupManagerService.this.signalFullBackupRestoreCompletion(params6);
                    BackupManagerService.this.mFullConfirmations.delete(msg.arg1);
                    if (params6.observer != null) {
                        try {
                            params6.observer.onTimeout();
                        } catch (RemoteException e10) {
                        }
                    }
                } else {
                    Slog.d(BackupManagerService.TAG, "couldn't find params for token " + msg.arg1);
                }
            }
        }
    }

    void addBackupTrace(String s) {
        synchronized (this.mBackupTrace) {
            this.mBackupTrace.add(s);
        }
    }

    void clearBackupTrace() {
        synchronized (this.mBackupTrace) {
            this.mBackupTrace.clear();
        }
    }

    /* loaded from: BackupManagerService$RunBackupReceiver.class */
    private class RunBackupReceiver extends BroadcastReceiver {
        private RunBackupReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (BackupManagerService.RUN_BACKUP_ACTION.equals(intent.getAction())) {
                synchronized (BackupManagerService.this.mQueueLock) {
                    if (BackupManagerService.this.mPendingInits.size() > 0) {
                        Slog.v(BackupManagerService.TAG, "Init pending at scheduled backup");
                        try {
                            BackupManagerService.this.mAlarmManager.cancel(BackupManagerService.this.mRunInitIntent);
                            BackupManagerService.this.mRunInitIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            Slog.e(BackupManagerService.TAG, "Run init intent cancelled");
                        }
                    } else if (BackupManagerService.this.mEnabled && BackupManagerService.this.mProvisioned) {
                        if (!BackupManagerService.this.mBackupRunning) {
                            Slog.v(BackupManagerService.TAG, "Running a backup pass");
                            BackupManagerService.this.mBackupRunning = true;
                            BackupManagerService.this.mWakelock.acquire();
                            Message msg = BackupManagerService.this.mBackupHandler.obtainMessage(1);
                            BackupManagerService.this.mBackupHandler.sendMessage(msg);
                        } else {
                            Slog.i(BackupManagerService.TAG, "Backup time but one already running");
                        }
                    } else {
                        Slog.w(BackupManagerService.TAG, "Backup pass but e=" + BackupManagerService.this.mEnabled + " p=" + BackupManagerService.this.mProvisioned);
                    }
                }
            }
        }
    }

    /* loaded from: BackupManagerService$RunInitializeReceiver.class */
    private class RunInitializeReceiver extends BroadcastReceiver {
        private RunInitializeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (BackupManagerService.RUN_INITIALIZE_ACTION.equals(intent.getAction())) {
                synchronized (BackupManagerService.this.mQueueLock) {
                    Slog.v(BackupManagerService.TAG, "Running a device init");
                    BackupManagerService.this.mWakelock.acquire();
                    Message msg = BackupManagerService.this.mBackupHandler.obtainMessage(5);
                    BackupManagerService.this.mBackupHandler.sendMessage(msg);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SecretKey buildPasswordKey(String pw, byte[] salt, int rounds) {
        return buildCharArrayKey(pw.toCharArray(), salt, rounds);
    }

    private SecretKey buildCharArrayKey(char[] pwArray, byte[] salt, int rounds) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec ks = new PBEKeySpec(pwArray, salt, rounds, 256);
            return keyFactory.generateSecret(ks);
        } catch (NoSuchAlgorithmException e) {
            Slog.e(TAG, "PBKDF2 unavailable!");
            return null;
        } catch (InvalidKeySpecException e2) {
            Slog.e(TAG, "Invalid key spec for PBKDF2!");
            return null;
        }
    }

    private String buildPasswordHash(String pw, byte[] salt, int rounds) {
        SecretKey key = buildPasswordKey(pw, salt, rounds);
        if (key != null) {
            return byteArrayToHex(key.getEncoded());
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String byteArrayToHex(byte[] data) {
        StringBuilder buf = new StringBuilder(data.length * 2);
        for (byte b : data) {
            buf.append(Byte.toHexString(b, true));
        }
        return buf.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] hexToByteArray(String digits) {
        int bytes = digits.length() / 2;
        if (2 * bytes != digits.length()) {
            throw new IllegalArgumentException("Hex string must have an even number of digits");
        }
        byte[] result = new byte[bytes];
        for (int i = 0; i < digits.length(); i += 2) {
            result[i / 2] = (byte) Integer.parseInt(digits.substring(i, i + 2), 16);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] makeKeyChecksum(byte[] pwBytes, byte[] salt, int rounds) {
        char[] mkAsChar = new char[pwBytes.length];
        for (int i = 0; i < pwBytes.length; i++) {
            mkAsChar[i] = (char) pwBytes[i];
        }
        Key checksum = buildCharArrayKey(mkAsChar, salt, rounds);
        return checksum.getEncoded();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] randomBytes(int bits) {
        byte[] array = new byte[bits / 8];
        this.mRng.nextBytes(array);
        return array;
    }

    boolean passwordMatchesSaved(String candidatePw, int rounds) {
        try {
            boolean isEncrypted = this.mMountService.getEncryptionState() != 1;
            if (isEncrypted) {
                Slog.i(TAG, "Device encrypted; verifying against device data pw");
                int result = this.mMountService.verifyEncryptionPassword(candidatePw);
                if (result == 0) {
                    return true;
                }
                if (result != -2) {
                    return false;
                }
                Slog.e(TAG, "verified encryption state mismatch against query; no match allowed");
                return false;
            } else if (this.mPasswordHash == null) {
                if (candidatePw == null || "".equals(candidatePw)) {
                    return true;
                }
                return false;
            } else if (candidatePw != null && candidatePw.length() > 0) {
                String currentPwHash = buildPasswordHash(candidatePw, this.mPasswordSalt, rounds);
                if (this.mPasswordHash.equalsIgnoreCase(currentPwHash)) {
                    return true;
                }
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0027, code lost:
        if (r4.mPasswordHash.length() > 0) goto L12;
     */
    @Override // android.app.backup.IBackupManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean hasBackupPassword() {
        /*
            r4 = this;
            r0 = r4
            android.content.Context r0 = r0.mContext
            java.lang.String r1 = "android.permission.BACKUP"
            java.lang.String r2 = "hasBackupPassword"
            r0.enforceCallingOrSelfPermission(r1, r2)
            r0 = r4
            android.os.storage.IMountService r0 = r0.mMountService     // Catch: java.lang.Exception -> L30
            int r0 = r0.getEncryptionState()     // Catch: java.lang.Exception -> L30
            r1 = 1
            if (r0 != r1) goto L2a
            r0 = r4
            java.lang.String r0 = r0.mPasswordHash     // Catch: java.lang.Exception -> L30
            if (r0 == 0) goto L2e
            r0 = r4
            java.lang.String r0 = r0.mPasswordHash     // Catch: java.lang.Exception -> L30
            int r0 = r0.length()     // Catch: java.lang.Exception -> L30
            if (r0 <= 0) goto L2e
        L2a:
            r0 = 1
            goto L2f
        L2e:
            r0 = 0
        L2f:
            return r0
        L30:
            r5 = move-exception
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.hasBackupPassword():boolean");
    }

    void recordInitPendingLocked(boolean isPending, String transportName) {
        Slog.i(TAG, "recordInitPendingLocked: " + isPending + " on transport " + transportName);
        try {
            IBackupTransport transport = getTransport(transportName);
            String transportDirName = transport.transportDirName();
            File stateDir = new File(this.mBaseStateDir, transportDirName);
            File initPendingFile = new File(stateDir, INIT_SENTINEL_FILE_NAME);
            if (isPending) {
                this.mPendingInits.add(transportName);
                try {
                    new FileOutputStream(initPendingFile).close();
                } catch (IOException e) {
                }
            } else {
                initPendingFile.delete();
                this.mPendingInits.remove(transportName);
            }
        } catch (RemoteException e2) {
        }
    }

    void resetBackupState(File stateFileDir) {
        synchronized (this.mQueueLock) {
            this.mEverStoredApps.clear();
            this.mEverStored.delete();
            this.mCurrentToken = 0L;
            writeRestoreTokens();
            File[] arr$ = stateFileDir.listFiles();
            for (File sf : arr$) {
                if (!sf.getName().equals(INIT_SENTINEL_FILE_NAME)) {
                    sf.delete();
                }
            }
        }
        synchronized (this.mBackupParticipants) {
            int N = this.mBackupParticipants.size();
            for (int i = 0; i < N; i++) {
                HashSet<String> participants = this.mBackupParticipants.valueAt(i);
                if (participants != null) {
                    Iterator i$ = participants.iterator();
                    while (i$.hasNext()) {
                        String packageName = i$.next();
                        dataChangedImpl(packageName);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerTransport(String name, IBackupTransport transport) {
        synchronized (this.mTransports) {
            Slog.v(TAG, "Registering transport " + name + " = " + transport);
            if (transport != null) {
                this.mTransports.put(name, transport);
                try {
                    String transportName = transport.transportDirName();
                    File stateDir = new File(this.mBaseStateDir, transportName);
                    stateDir.mkdirs();
                    File initSentinel = new File(stateDir, INIT_SENTINEL_FILE_NAME);
                    if (initSentinel.exists()) {
                        synchronized (this.mQueueLock) {
                            this.mPendingInits.add(transportName);
                            this.mAlarmManager.set(0, System.currentTimeMillis() + DateUtils.MINUTE_IN_MILLIS, this.mRunInitIntent);
                        }
                    }
                    return;
                } catch (RemoteException e) {
                    return;
                }
            }
            this.mTransports.remove(name);
        }
    }

    void addPackageParticipantsLocked(String[] packageNames) {
        List<PackageInfo> targetApps = allAgentPackages();
        if (packageNames != null) {
            Slog.v(TAG, "addPackageParticipantsLocked: #" + packageNames.length);
            for (String packageName : packageNames) {
                addPackageParticipantsLockedInner(packageName, targetApps);
            }
            return;
        }
        Slog.v(TAG, "addPackageParticipantsLocked: all");
        addPackageParticipantsLockedInner(null, targetApps);
    }

    private void addPackageParticipantsLockedInner(String packageName, List<PackageInfo> targetPkgs) {
        for (PackageInfo pkg : targetPkgs) {
            if (packageName == null || pkg.packageName.equals(packageName)) {
                int uid = pkg.applicationInfo.uid;
                HashSet<String> set = this.mBackupParticipants.get(uid);
                if (set == null) {
                    set = new HashSet<>();
                    this.mBackupParticipants.put(uid, set);
                }
                set.add(pkg.packageName);
                Slog.i(TAG, "Scheduling backup for new app " + pkg.packageName);
                dataChangedImpl(pkg.packageName);
            }
        }
    }

    void removePackageParticipantsLocked(String[] packageNames, int oldUid) {
        if (packageNames == null) {
            Slog.w(TAG, "removePackageParticipants with null list");
            return;
        }
        Slog.v(TAG, "removePackageParticipantsLocked: uid=" + oldUid + " #" + packageNames.length);
        for (String pkg : packageNames) {
            HashSet<String> set = this.mBackupParticipants.get(oldUid);
            if (set != null && set.contains(pkg)) {
                removePackageFromSetLocked(set, pkg);
                if (set.isEmpty()) {
                    this.mBackupParticipants.remove(oldUid);
                }
            }
        }
    }

    private void removePackageFromSetLocked(HashSet<String> set, String packageName) {
        if (set.contains(packageName)) {
            set.remove(packageName);
            this.mPendingBackups.remove(packageName);
        }
    }

    List<PackageInfo> allAgentPackages() {
        List<PackageInfo> packages = this.mPackageManager.getInstalledPackages(64);
        int N = packages.size();
        for (int a = N - 1; a >= 0; a--) {
            PackageInfo pkg = packages.get(a);
            try {
                ApplicationInfo app = pkg.applicationInfo;
                if ((app.flags & 32768) == 0 || app.backupAgentName == null) {
                    packages.remove(a);
                } else {
                    pkg.applicationInfo.sharedLibraryFiles = this.mPackageManager.getApplicationInfo(pkg.packageName, 1024).sharedLibraryFiles;
                }
            } catch (PackageManager.NameNotFoundException e) {
                packages.remove(a);
            }
        }
        return packages;
    }

    void writeRestoreTokens() {
        try {
            RandomAccessFile af = new RandomAccessFile(this.mTokenFile, "rwd");
            af.writeInt(1);
            af.writeLong(this.mAncestralToken);
            af.writeLong(this.mCurrentToken);
            if (this.mAncestralPackages == null) {
                af.writeInt(-1);
            } else {
                af.writeInt(this.mAncestralPackages.size());
                Slog.v(TAG, "Ancestral packages:  " + this.mAncestralPackages.size());
                for (String pkgName : this.mAncestralPackages) {
                    af.writeUTF(pkgName);
                }
            }
            af.close();
        } catch (IOException e) {
            Slog.w(TAG, "Unable to write token file:", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IBackupTransport getTransport(String transportName) {
        IBackupTransport transport;
        synchronized (this.mTransports) {
            transport = this.mTransports.get(transportName);
            if (transport == null) {
                Slog.w(TAG, "Requested unavailable transport: " + transportName);
            }
        }
        return transport;
    }

    IBackupAgent bindToAgentSynchronous(ApplicationInfo app, int mode) {
        IBackupAgent agent = null;
        synchronized (this.mAgentConnectLock) {
            this.mConnecting = true;
            this.mConnectedAgent = null;
            try {
                if (this.mActivityManager.bindBackupAgent(app, mode)) {
                    Slog.d(TAG, "awaiting agent for " + app);
                    long timeoutMark = System.currentTimeMillis() + TIMEOUT_INTERVAL;
                    while (this.mConnecting && this.mConnectedAgent == null && System.currentTimeMillis() < timeoutMark) {
                        try {
                            this.mAgentConnectLock.wait(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                        } catch (InterruptedException e) {
                            Slog.w(TAG, "Interrupted: " + e);
                            this.mActivityManager.clearPendingBackup();
                            return null;
                        }
                    }
                    if (this.mConnecting) {
                        Slog.w(TAG, "Timeout waiting for agent " + app);
                        this.mActivityManager.clearPendingBackup();
                        return null;
                    }
                    Slog.i(TAG, "got agent " + this.mConnectedAgent);
                    agent = this.mConnectedAgent;
                }
            } catch (RemoteException e2) {
            }
            return agent;
        }
    }

    void clearApplicationDataSynchronous(String packageName) {
        try {
            PackageInfo info = this.mPackageManager.getPackageInfo(packageName, 0);
            if ((info.applicationInfo.flags & 64) == 0) {
                return;
            }
            ClearDataObserver observer = new ClearDataObserver();
            synchronized (this.mClearDataLock) {
                this.mClearingData = true;
                try {
                    this.mActivityManager.clearApplicationUserData(packageName, observer, 0);
                } catch (RemoteException e) {
                }
                long timeoutMark = System.currentTimeMillis() + TIMEOUT_INTERVAL;
                while (this.mClearingData && System.currentTimeMillis() < timeoutMark) {
                    try {
                        this.mClearDataLock.wait(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                    } catch (InterruptedException e2) {
                        this.mClearingData = false;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e3) {
            Slog.w(TAG, "Tried to clear data for " + packageName + " but not found");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BackupManagerService$ClearDataObserver.class */
    public class ClearDataObserver extends IPackageDataObserver.Stub {
        ClearDataObserver() {
        }

        @Override // android.content.pm.IPackageDataObserver
        public void onRemoveCompleted(String packageName, boolean succeeded) {
            synchronized (BackupManagerService.this.mClearDataLock) {
                BackupManagerService.this.mClearingData = false;
                BackupManagerService.this.mClearDataLock.notifyAll();
            }
        }
    }

    long getAvailableRestoreToken(String packageName) {
        long token = this.mAncestralToken;
        synchronized (this.mQueueLock) {
            if (this.mEverStoredApps.contains(packageName)) {
                token = this.mCurrentToken;
            }
        }
        return token;
    }

    void prepareOperationTimeout(int token, long interval, BackupRestoreTask callback) {
        synchronized (this.mCurrentOpLock) {
            this.mCurrentOperations.put(token, new Operation(0, callback));
            Message msg = this.mBackupHandler.obtainMessage(7, token, 0, callback);
            this.mBackupHandler.sendMessageDelayed(msg, interval);
        }
    }

    boolean waitUntilOperationComplete(int token) {
        int finalState = 0;
        synchronized (this.mCurrentOpLock) {
            while (true) {
                Operation op = this.mCurrentOperations.get(token);
                if (op == null) {
                    break;
                } else if (op.state == 0) {
                    try {
                        this.mCurrentOpLock.wait();
                    } catch (InterruptedException e) {
                    }
                } else {
                    finalState = op.state;
                    break;
                }
            }
        }
        this.mBackupHandler.removeMessages(7);
        return finalState == 1;
    }

    void handleTimeout(int token, Object obj) {
        Operation op;
        synchronized (this.mCurrentOpLock) {
            op = this.mCurrentOperations.get(token);
            int state = op != null ? op.state : -1;
            if (state == 0) {
                Slog.v(TAG, "TIMEOUT: token=" + Integer.toHexString(token));
                op.state = -1;
                this.mCurrentOperations.put(token, op);
            }
            this.mCurrentOpLock.notifyAll();
        }
        if (op != null && op.callback != null) {
            op.callback.handleTimeout();
        }
    }

    /* loaded from: BackupManagerService$PerformBackupTask.class */
    class PerformBackupTask implements BackupRestoreTask {
        private static final String TAG = "PerformBackupTask";
        IBackupTransport mTransport;
        ArrayList<BackupRequest> mQueue;
        ArrayList<BackupRequest> mOriginalQueue;
        File mStateDir;
        File mJournal;
        BackupState mCurrentState;
        PackageInfo mCurrentPackage;
        File mSavedStateName;
        File mBackupDataName;
        File mNewStateName;
        ParcelFileDescriptor mSavedState;
        ParcelFileDescriptor mBackupData;
        ParcelFileDescriptor mNewState;
        int mStatus;
        boolean mFinished;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformBackupTask.invokeNextAgent():void, file: BackupManagerService$PerformBackupTask.class
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
        void invokeNextAgent() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformBackupTask.invokeNextAgent():void, file: BackupManagerService$PerformBackupTask.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.PerformBackupTask.invokeNextAgent():void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformBackupTask.operationComplete():void, file: BackupManagerService$PerformBackupTask.class
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
        @Override // com.android.server.BackupManagerService.BackupRestoreTask
        public void operationComplete() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformBackupTask.operationComplete():void, file: BackupManagerService$PerformBackupTask.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.PerformBackupTask.operationComplete():void");
        }

        public PerformBackupTask(IBackupTransport transport, ArrayList<BackupRequest> queue, File journal) {
            this.mTransport = transport;
            this.mOriginalQueue = queue;
            this.mJournal = journal;
            try {
                this.mStateDir = new File(BackupManagerService.this.mBaseStateDir, transport.transportDirName());
            } catch (RemoteException e) {
            }
            this.mCurrentState = BackupState.INITIAL;
            this.mFinished = false;
            BackupManagerService.this.addBackupTrace("STATE => INITIAL");
        }

        @Override // com.android.server.BackupManagerService.BackupRestoreTask
        public void execute() {
            switch (this.mCurrentState) {
                case INITIAL:
                    beginBackup();
                    return;
                case RUNNING_QUEUE:
                    invokeNextAgent();
                    return;
                case FINAL:
                    if (this.mFinished) {
                        Slog.e(TAG, "Duplicate finish");
                    } else {
                        finalizeBackup();
                    }
                    this.mFinished = true;
                    return;
                default:
                    return;
            }
        }

        void beginBackup() {
            BackupManagerService.this.clearBackupTrace();
            StringBuilder b = new StringBuilder(256);
            b.append("beginBackup: [");
            Iterator i$ = this.mOriginalQueue.iterator();
            while (i$.hasNext()) {
                BackupRequest req = i$.next();
                b.append(' ');
                b.append(req.packageName);
            }
            b.append(" ]");
            BackupManagerService.this.addBackupTrace(b.toString());
            this.mStatus = 0;
            if (this.mOriginalQueue.isEmpty()) {
                Slog.w(TAG, "Backup begun with an empty queue - nothing to do.");
                BackupManagerService.this.addBackupTrace("queue empty at begin");
                executeNextState(BackupState.FINAL);
                return;
            }
            this.mQueue = (ArrayList) this.mOriginalQueue.clone();
            Slog.v(TAG, "Beginning backup of " + this.mQueue.size() + " targets");
            File pmState = new File(this.mStateDir, BackupManagerService.PACKAGE_MANAGER_SENTINEL);
            try {
                try {
                    String transportName = this.mTransport.transportDirName();
                    EventLog.writeEvent((int) EventLogTags.BACKUP_START, transportName);
                    if (this.mStatus == 0 && pmState.length() <= 0) {
                        Slog.i(TAG, "Initializing (wiping) backup state and transport storage");
                        BackupManagerService.this.addBackupTrace("initializing transport " + transportName);
                        BackupManagerService.this.resetBackupState(this.mStateDir);
                        this.mStatus = this.mTransport.initializeDevice();
                        BackupManagerService.this.addBackupTrace("transport.initializeDevice() == " + this.mStatus);
                        if (this.mStatus == 0) {
                            EventLog.writeEvent((int) EventLogTags.BACKUP_INITIALIZE, new Object[0]);
                        } else {
                            EventLog.writeEvent((int) EventLogTags.BACKUP_TRANSPORT_FAILURE, "(initialize)");
                            Slog.e(TAG, "Transport error in initializeDevice()");
                        }
                    }
                    if (this.mStatus == 0) {
                        PackageManagerBackupAgent pmAgent = new PackageManagerBackupAgent(BackupManagerService.this.mPackageManager, BackupManagerService.this.allAgentPackages());
                        this.mStatus = invokeAgentForBackup(BackupManagerService.PACKAGE_MANAGER_SENTINEL, IBackupAgent.Stub.asInterface(pmAgent.onBind()), this.mTransport);
                        BackupManagerService.this.addBackupTrace("PMBA invoke: " + this.mStatus);
                    }
                    if (this.mStatus == 2) {
                        EventLog.writeEvent((int) EventLogTags.BACKUP_RESET, this.mTransport.transportDirName());
                    }
                } catch (Exception e) {
                    Slog.e(TAG, "Error in backup thread", e);
                    BackupManagerService.this.addBackupTrace("Exception in backup thread: " + e);
                    this.mStatus = 1;
                    BackupManagerService.this.addBackupTrace("exiting prelim: " + this.mStatus);
                    if (this.mStatus != 0) {
                        BackupManagerService.this.resetBackupState(this.mStateDir);
                        executeNextState(BackupState.FINAL);
                    }
                }
            } finally {
                BackupManagerService.this.addBackupTrace("exiting prelim: " + this.mStatus);
                if (this.mStatus != 0) {
                    BackupManagerService.this.resetBackupState(this.mStateDir);
                    executeNextState(BackupState.FINAL);
                }
            }
        }

        void finalizeBackup() {
            BackupManagerService.this.addBackupTrace("finishing");
            if (this.mJournal != null && !this.mJournal.delete()) {
                Slog.e(TAG, "Unable to remove backup journal file " + this.mJournal);
            }
            if (BackupManagerService.this.mCurrentToken == 0 && this.mStatus == 0) {
                BackupManagerService.this.addBackupTrace("success; recording token");
                try {
                    BackupManagerService.this.mCurrentToken = this.mTransport.getCurrentRestoreSet();
                } catch (RemoteException e) {
                }
                BackupManagerService.this.writeRestoreTokens();
            }
            synchronized (BackupManagerService.this.mQueueLock) {
                BackupManagerService.this.mBackupRunning = false;
                if (this.mStatus == 2) {
                    clearMetadata();
                    Slog.d(TAG, "Server requires init; rerunning");
                    BackupManagerService.this.addBackupTrace("init required; rerunning");
                    BackupManagerService.this.backupNow();
                }
            }
            BackupManagerService.this.clearBackupTrace();
            Slog.i(TAG, "Backup pass finished.");
            BackupManagerService.this.mWakelock.release();
        }

        void clearMetadata() {
            File pmState = new File(this.mStateDir, BackupManagerService.PACKAGE_MANAGER_SENTINEL);
            if (pmState.exists()) {
                pmState.delete();
            }
        }

        int invokeAgentForBackup(String packageName, IBackupAgent agent, IBackupTransport transport) {
            Slog.d(TAG, "invokeAgentForBackup on " + packageName);
            BackupManagerService.this.addBackupTrace("invoking " + packageName);
            this.mSavedStateName = new File(this.mStateDir, packageName);
            this.mBackupDataName = new File(BackupManagerService.this.mDataDir, packageName + ".data");
            this.mNewStateName = new File(this.mStateDir, packageName + ".new");
            this.mSavedState = null;
            this.mBackupData = null;
            this.mNewState = null;
            int token = BackupManagerService.this.generateToken();
            try {
                if (packageName.equals(BackupManagerService.PACKAGE_MANAGER_SENTINEL)) {
                    this.mCurrentPackage = new PackageInfo();
                    this.mCurrentPackage.packageName = packageName;
                }
                this.mSavedState = ParcelFileDescriptor.open(this.mSavedStateName, 402653184);
                this.mBackupData = ParcelFileDescriptor.open(this.mBackupDataName, 1006632960);
                if (!SELinux.restorecon(this.mBackupDataName)) {
                    Slog.e(TAG, "SELinux restorecon failed on " + this.mBackupDataName);
                }
                this.mNewState = ParcelFileDescriptor.open(this.mNewStateName, 1006632960);
                BackupManagerService.this.addBackupTrace("setting timeout");
                BackupManagerService.this.prepareOperationTimeout(token, 30000L, this);
                BackupManagerService.this.addBackupTrace("calling agent doBackup()");
                agent.doBackup(this.mSavedState, this.mBackupData, this.mNewState, token, BackupManagerService.this.mBackupManagerBinder);
                BackupManagerService.this.addBackupTrace("invoke success");
                return 0;
            } catch (Exception e) {
                Slog.e(TAG, "Error invoking for backup on " + packageName);
                BackupManagerService.this.addBackupTrace("exception: " + e);
                EventLog.writeEvent((int) EventLogTags.BACKUP_AGENT_FAILURE, packageName, e.toString());
                agentErrorCleanup();
                return 3;
            }
        }

        @Override // com.android.server.BackupManagerService.BackupRestoreTask
        public void handleTimeout() {
            Slog.e(TAG, "Timeout backing up " + this.mCurrentPackage.packageName);
            EventLog.writeEvent((int) EventLogTags.BACKUP_AGENT_FAILURE, this.mCurrentPackage.packageName, GpsNetInitiatedHandler.NI_INTENT_KEY_TIMEOUT);
            BackupManagerService.this.addBackupTrace("timeout of " + this.mCurrentPackage.packageName);
            agentErrorCleanup();
            BackupManagerService.this.dataChangedImpl(this.mCurrentPackage.packageName);
        }

        void revertAndEndBackup() {
            BackupManagerService.this.addBackupTrace("transport error; reverting");
            Iterator i$ = this.mOriginalQueue.iterator();
            while (i$.hasNext()) {
                BackupRequest request = i$.next();
                BackupManagerService.this.dataChangedImpl(request.packageName);
            }
            restartBackupAlarm();
        }

        void agentErrorCleanup() {
            this.mBackupDataName.delete();
            this.mNewStateName.delete();
            clearAgentState();
            executeNextState(this.mQueue.isEmpty() ? BackupState.FINAL : BackupState.RUNNING_QUEUE);
        }

        void clearAgentState() {
            try {
                if (this.mSavedState != null) {
                    this.mSavedState.close();
                }
            } catch (IOException e) {
            }
            try {
                if (this.mBackupData != null) {
                    this.mBackupData.close();
                }
            } catch (IOException e2) {
            }
            try {
                if (this.mNewState != null) {
                    this.mNewState.close();
                }
            } catch (IOException e3) {
            }
            this.mNewState = null;
            this.mBackupData = null;
            this.mSavedState = null;
            synchronized (BackupManagerService.this.mCurrentOpLock) {
                BackupManagerService.this.mCurrentOperations.clear();
            }
            if (this.mCurrentPackage.applicationInfo != null) {
                BackupManagerService.this.addBackupTrace("unbinding " + this.mCurrentPackage.packageName);
                try {
                    BackupManagerService.this.mActivityManager.unbindBackupAgent(this.mCurrentPackage.applicationInfo);
                } catch (RemoteException e4) {
                }
            }
        }

        void restartBackupAlarm() {
            BackupManagerService.this.addBackupTrace("setting backup trigger");
            synchronized (BackupManagerService.this.mQueueLock) {
                try {
                    BackupManagerService.this.startBackupAlarmsLocked(this.mTransport.requestBackupTime());
                } catch (RemoteException e) {
                }
            }
        }

        void executeNextState(BackupState nextState) {
            BackupManagerService.this.addBackupTrace("executeNextState => " + nextState);
            this.mCurrentState = nextState;
            Message msg = BackupManagerService.this.mBackupHandler.obtainMessage(20, this);
            BackupManagerService.this.mBackupHandler.sendMessage(msg);
        }
    }

    /* loaded from: BackupManagerService$ObbServiceClient.class */
    abstract class ObbServiceClient {
        public IObbBackupService mObbService;

        ObbServiceClient() {
        }

        public void setObbBinder(IObbBackupService binder) {
            this.mObbService = binder;
        }
    }

    /* loaded from: BackupManagerService$FullBackupObbConnection.class */
    class FullBackupObbConnection implements ServiceConnection {
        volatile IObbBackupService mService = null;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.FullBackupObbConnection.backupObbs(android.content.pm.PackageInfo, java.io.OutputStream):boolean, file: BackupManagerService$FullBackupObbConnection.class
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
        public boolean backupObbs(android.content.pm.PackageInfo r1, java.io.OutputStream r2) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.FullBackupObbConnection.backupObbs(android.content.pm.PackageInfo, java.io.OutputStream):boolean, file: BackupManagerService$FullBackupObbConnection.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.FullBackupObbConnection.backupObbs(android.content.pm.PackageInfo, java.io.OutputStream):boolean");
        }

        FullBackupObbConnection() {
        }

        public void establish() {
            Slog.i(BackupManagerService.TAG, "Initiating bind of OBB service on " + this);
            Intent obbIntent = new Intent().setComponent(new ComponentName(BackupManagerService.SHARED_BACKUP_AGENT_PACKAGE, "com.android.sharedstoragebackup.ObbBackupService"));
            BackupManagerService.this.mContext.bindService(obbIntent, this, 1);
        }

        public void tearDown() {
            BackupManagerService.this.mContext.unbindService(this);
        }

        public void restoreObbFile(String pkgName, ParcelFileDescriptor data, long fileSize, int type, String path, long mode, long mtime, int token, IBackupManager callbackBinder) {
            waitForConnection();
            try {
                this.mService.restoreObbFile(pkgName, data, fileSize, type, path, mode, mtime, token, callbackBinder);
            } catch (Exception e) {
                Slog.w(BackupManagerService.TAG, "Unable to restore OBBs for " + pkgName, e);
            }
        }

        private void waitForConnection() {
            synchronized (this) {
                while (this.mService == null) {
                    Slog.i(BackupManagerService.TAG, "...waiting for OBB service binding...");
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                Slog.i(BackupManagerService.TAG, "Connected to OBB service; continuing");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (this) {
                this.mService = IObbBackupService.Stub.asInterface(service);
                Slog.i(BackupManagerService.TAG, "OBB service connection " + this.mService + " connected on " + this);
                notifyAll();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            synchronized (this) {
                this.mService = null;
                Slog.i(BackupManagerService.TAG, "OBB service connection disconnected on " + this);
                notifyAll();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void routeSocketDataToOutput(ParcelFileDescriptor inPipe, OutputStream out) throws IOException {
        FileInputStream raw = new FileInputStream(inPipe.getFileDescriptor());
        DataInputStream in = new DataInputStream(raw);
        byte[] buffer = new byte[32768];
        while (true) {
            int readInt = in.readInt();
            int chunkTotal = readInt;
            if (readInt > 0) {
                while (chunkTotal > 0) {
                    int toRead = chunkTotal > buffer.length ? buffer.length : chunkTotal;
                    int nRead = in.read(buffer, 0, toRead);
                    out.write(buffer, 0, nRead);
                    chunkTotal -= nRead;
                }
            } else {
                return;
            }
        }
    }

    /* loaded from: BackupManagerService$PerformFullBackupTask.class */
    class PerformFullBackupTask extends ObbServiceClient implements Runnable {
        ParcelFileDescriptor mOutputFile;
        DeflaterOutputStream mDeflater;
        IFullBackupRestoreObserver mObserver;
        boolean mIncludeApks;
        boolean mIncludeObbs;
        boolean mIncludeShared;
        boolean mAllApps;
        final boolean mIncludeSystem;
        String[] mPackages;
        String mCurrentPassword;
        String mEncryptPassword;
        AtomicBoolean mLatchObject;
        File mFilesDir;
        File mManifestFile;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformFullBackupTask.run():void, file: BackupManagerService$PerformFullBackupTask.class
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
        @Override // java.lang.Runnable
        public void run() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformFullBackupTask.run():void, file: BackupManagerService$PerformFullBackupTask.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.PerformFullBackupTask.run():void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformFullBackupTask.backupOnePackage(android.content.pm.PackageInfo, java.io.OutputStream):void, file: BackupManagerService$PerformFullBackupTask.class
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
        private void backupOnePackage(android.content.pm.PackageInfo r1, java.io.OutputStream r2) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformFullBackupTask.backupOnePackage(android.content.pm.PackageInfo, java.io.OutputStream):void, file: BackupManagerService$PerformFullBackupTask.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.PerformFullBackupTask.backupOnePackage(android.content.pm.PackageInfo, java.io.OutputStream):void");
        }

        static /* synthetic */ void access$1100(PerformFullBackupTask x0, PackageInfo x1, File x2, boolean x3) throws IOException {
            x0.writeAppManifest(x1, x2, x3);
        }

        static /* synthetic */ void access$1200(PerformFullBackupTask x0, PackageInfo x1, BackupDataOutput x2) {
            x0.writeApkToBackup(x1, x2);
        }

        /* loaded from: BackupManagerService$PerformFullBackupTask$FullBackupRunner.class */
        class FullBackupRunner implements Runnable {
            PackageInfo mPackage;
            IBackupAgent mAgent;
            ParcelFileDescriptor mPipe;
            int mToken;
            boolean mSendApk;
            boolean mWriteManifest;

            /*  JADX ERROR: Method load error
                jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformFullBackupTask.FullBackupRunner.run():void, file: BackupManagerService$PerformFullBackupTask$FullBackupRunner.class
                	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
                	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
                	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
                Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
                	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
                	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
                	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
                	... 2 more
                */
            @Override // java.lang.Runnable
            public void run() {
                /*
                // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformFullBackupTask.FullBackupRunner.run():void, file: BackupManagerService$PerformFullBackupTask$FullBackupRunner.class
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.PerformFullBackupTask.FullBackupRunner.run():void");
            }

            FullBackupRunner(PackageInfo pack, IBackupAgent agent, ParcelFileDescriptor pipe, int token, boolean sendApk, boolean writeManifest) throws IOException {
                this.mPackage = pack;
                this.mAgent = agent;
                this.mPipe = ParcelFileDescriptor.dup(pipe.getFileDescriptor());
                this.mToken = token;
                this.mSendApk = sendApk;
                this.mWriteManifest = writeManifest;
            }
        }

        PerformFullBackupTask(ParcelFileDescriptor fd, IFullBackupRestoreObserver observer, boolean includeApks, boolean includeObbs, boolean includeShared, String curPassword, String encryptPassword, boolean doAllApps, boolean doSystem, String[] packages, AtomicBoolean latch) {
            super();
            this.mOutputFile = fd;
            this.mObserver = observer;
            this.mIncludeApks = includeApks;
            this.mIncludeObbs = includeObbs;
            this.mIncludeShared = includeShared;
            this.mAllApps = doAllApps;
            this.mIncludeSystem = doSystem;
            this.mPackages = packages;
            this.mCurrentPassword = curPassword;
            if (encryptPassword == null || "".equals(encryptPassword)) {
                this.mEncryptPassword = curPassword;
            } else {
                this.mEncryptPassword = encryptPassword;
            }
            this.mLatchObject = latch;
            this.mFilesDir = new File("/data/system");
            this.mManifestFile = new File(this.mFilesDir, BackupManagerService.BACKUP_MANIFEST_FILENAME);
        }

        private OutputStream emitAesBackupHeader(StringBuilder headerbuf, OutputStream ofstream) throws Exception {
            byte[] newUserSalt = BackupManagerService.this.randomBytes(512);
            SecretKey userKey = BackupManagerService.this.buildPasswordKey(this.mEncryptPassword, newUserSalt, 10000);
            byte[] masterPw = new byte[32];
            BackupManagerService.this.mRng.nextBytes(masterPw);
            byte[] checksumSalt = BackupManagerService.this.randomBytes(512);
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec masterKeySpec = new SecretKeySpec(masterPw, "AES");
            c.init(1, masterKeySpec);
            OutputStream finalOutput = new CipherOutputStream(ofstream, c);
            headerbuf.append(BackupManagerService.ENCRYPTION_ALGORITHM_NAME);
            headerbuf.append('\n');
            headerbuf.append(BackupManagerService.this.byteArrayToHex(newUserSalt));
            headerbuf.append('\n');
            headerbuf.append(BackupManagerService.this.byteArrayToHex(checksumSalt));
            headerbuf.append('\n');
            headerbuf.append(10000);
            headerbuf.append('\n');
            Cipher mkC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mkC.init(1, userKey);
            headerbuf.append(BackupManagerService.this.byteArrayToHex(mkC.getIV()));
            headerbuf.append('\n');
            byte[] IV = c.getIV();
            byte[] mk = masterKeySpec.getEncoded();
            byte[] checksum = BackupManagerService.this.makeKeyChecksum(masterKeySpec.getEncoded(), checksumSalt, 10000);
            ByteArrayOutputStream blob = new ByteArrayOutputStream(IV.length + mk.length + checksum.length + 3);
            DataOutputStream mkOut = new DataOutputStream(blob);
            mkOut.writeByte(IV.length);
            mkOut.write(IV);
            mkOut.writeByte(mk.length);
            mkOut.write(mk);
            mkOut.writeByte(checksum.length);
            mkOut.write(checksum);
            mkOut.flush();
            byte[] encryptedMk = mkC.doFinal(blob.toByteArray());
            headerbuf.append(BackupManagerService.this.byteArrayToHex(encryptedMk));
            headerbuf.append('\n');
            return finalOutput;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void writeApkToBackup(PackageInfo pkg, BackupDataOutput output) {
            File[] obbFiles;
            String appSourceDir = pkg.applicationInfo.sourceDir;
            String apkDir = new File(appSourceDir).getParent();
            FullBackup.backupToTar(pkg.packageName, FullBackup.APK_TREE_TOKEN, null, apkDir, appSourceDir, output);
            Environment.UserEnvironment userEnv = new Environment.UserEnvironment(0);
            File obbDir = userEnv.buildExternalStorageAppObbDirs(pkg.packageName)[0];
            if (obbDir != null && (obbFiles = obbDir.listFiles()) != null) {
                String obbDirName = obbDir.getAbsolutePath();
                for (File obb : obbFiles) {
                    FullBackup.backupToTar(pkg.packageName, FullBackup.OBB_TREE_TOKEN, null, obbDirName, obb.getAbsolutePath(), output);
                }
            }
        }

        private void finalizeBackup(OutputStream out) {
            try {
                byte[] eof = new byte[1024];
                out.write(eof);
            } catch (IOException e) {
                Slog.w(BackupManagerService.TAG, "Error attempting to finalize backup stream");
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void writeAppManifest(PackageInfo pkg, File manifestFile, boolean withApk) throws IOException {
            StringBuilder builder = new StringBuilder(4096);
            StringBuilderPrinter printer = new StringBuilderPrinter(builder);
            printer.println(Integer.toString(1));
            printer.println(pkg.packageName);
            printer.println(Integer.toString(pkg.versionCode));
            printer.println(Integer.toString(Build.VERSION.SDK_INT));
            String installerName = BackupManagerService.this.mPackageManager.getInstallerPackageName(pkg.packageName);
            printer.println(installerName != null ? installerName : "");
            printer.println(withApk ? "1" : "0");
            if (pkg.signatures == null) {
                printer.println("0");
            } else {
                printer.println(Integer.toString(pkg.signatures.length));
                Signature[] arr$ = pkg.signatures;
                for (Signature sig : arr$) {
                    printer.println(sig.toCharsString());
                }
            }
            FileOutputStream outstream = new FileOutputStream(manifestFile);
            outstream.write(builder.toString().getBytes());
            outstream.close();
        }

        private void tearDown(PackageInfo pkg) {
            ApplicationInfo app;
            if (pkg != null && (app = pkg.applicationInfo) != null) {
                try {
                    BackupManagerService.this.mActivityManager.unbindBackupAgent(app);
                    if (app.uid != 1000 && app.uid != 1001) {
                        BackupManagerService.this.mActivityManager.killApplicationProcess(app.processName, app.uid);
                    }
                } catch (RemoteException e) {
                    Slog.d(BackupManagerService.TAG, "Lost app trying to shut down");
                }
            }
        }

        void sendStartBackup() {
            if (this.mObserver != null) {
                try {
                    this.mObserver.onStartBackup();
                } catch (RemoteException e) {
                    Slog.w(BackupManagerService.TAG, "full backup observer went away: startBackup");
                    this.mObserver = null;
                }
            }
        }

        void sendOnBackupPackage(String name) {
            if (this.mObserver != null) {
                try {
                    this.mObserver.onBackupPackage(name);
                } catch (RemoteException e) {
                    Slog.w(BackupManagerService.TAG, "full backup observer went away: backupPackage");
                    this.mObserver = null;
                }
            }
        }

        void sendEndBackup() {
            if (this.mObserver != null) {
                try {
                    this.mObserver.onEndBackup();
                } catch (RemoteException e) {
                    Slog.w(BackupManagerService.TAG, "full backup observer went away: endBackup");
                    this.mObserver = null;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BackupManagerService$FileMetadata.class */
    public static class FileMetadata {
        String packageName;
        String installerPackageName;
        int type;
        String domain;
        String path;
        long mode;
        long mtime;
        long size;

        FileMetadata() {
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("FileMetadata{");
            sb.append(this.packageName);
            sb.append(',');
            sb.append(this.type);
            sb.append(',');
            sb.append(this.domain);
            sb.append(':');
            sb.append(this.path);
            sb.append(',');
            sb.append(this.size);
            sb.append('}');
            return sb.toString();
        }
    }

    /* loaded from: BackupManagerService$PerformFullRestoreTask.class */
    class PerformFullRestoreTask extends ObbServiceClient implements Runnable {
        ParcelFileDescriptor mInputFile;
        String mCurrentPassword;
        String mDecryptPassword;
        IFullBackupRestoreObserver mObserver;
        AtomicBoolean mLatchObject;
        IBackupAgent mAgent;
        String mAgentPackage;
        ApplicationInfo mTargetApp;
        FullBackupObbConnection mObbConnection;
        ParcelFileDescriptor[] mPipes;
        long mBytes;
        final HashMap<String, RestorePolicy> mPackagePolicies;
        final HashMap<String, String> mPackageInstallers;
        final HashMap<String, Signature[]> mManifestSignatures;
        final HashSet<String> mClearedPackages;
        final RestoreInstallObserver mInstallObserver;
        final RestoreDeleteObserver mDeleteObserver;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformFullRestoreTask.run():void, file: BackupManagerService$PerformFullRestoreTask.class
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
        @Override // java.lang.Runnable
        public void run() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformFullRestoreTask.run():void, file: BackupManagerService$PerformFullRestoreTask.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.PerformFullRestoreTask.run():void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformFullRestoreTask.installApk(com.android.server.BackupManagerService$FileMetadata, java.lang.String, java.io.InputStream):boolean, file: BackupManagerService$PerformFullRestoreTask.class
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
        boolean installApk(com.android.server.BackupManagerService.FileMetadata r1, java.lang.String r2, java.io.InputStream r3) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformFullRestoreTask.installApk(com.android.server.BackupManagerService$FileMetadata, java.lang.String, java.io.InputStream):boolean, file: BackupManagerService$PerformFullRestoreTask.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.PerformFullRestoreTask.installApk(com.android.server.BackupManagerService$FileMetadata, java.lang.String, java.io.InputStream):boolean");
        }

        PerformFullRestoreTask(ParcelFileDescriptor fd, String curPassword, String decryptPassword, IFullBackupRestoreObserver observer, AtomicBoolean latch) {
            super();
            this.mObbConnection = null;
            this.mPipes = null;
            this.mPackagePolicies = new HashMap<>();
            this.mPackageInstallers = new HashMap<>();
            this.mManifestSignatures = new HashMap<>();
            this.mClearedPackages = new HashSet<>();
            this.mInstallObserver = new RestoreInstallObserver();
            this.mDeleteObserver = new RestoreDeleteObserver();
            this.mInputFile = fd;
            this.mCurrentPassword = curPassword;
            this.mDecryptPassword = decryptPassword;
            this.mObserver = observer;
            this.mLatchObject = latch;
            this.mAgent = null;
            this.mAgentPackage = null;
            this.mTargetApp = null;
            this.mObbConnection = new FullBackupObbConnection();
            this.mClearedPackages.add("android");
            this.mClearedPackages.add("com.android.providers.settings");
        }

        /* loaded from: BackupManagerService$PerformFullRestoreTask$RestoreFileRunnable.class */
        class RestoreFileRunnable implements Runnable {
            IBackupAgent mAgent;
            FileMetadata mInfo;
            ParcelFileDescriptor mSocket;
            int mToken;

            RestoreFileRunnable(IBackupAgent agent, FileMetadata info, ParcelFileDescriptor socket, int token) throws IOException {
                this.mAgent = agent;
                this.mInfo = info;
                this.mToken = token;
                this.mSocket = ParcelFileDescriptor.dup(socket.getFileDescriptor());
            }

            @Override // java.lang.Runnable
            public void run() {
                try {
                    this.mAgent.doRestoreFile(this.mSocket, this.mInfo.size, this.mInfo.type, this.mInfo.domain, this.mInfo.path, this.mInfo.mode, this.mInfo.mtime, this.mToken, BackupManagerService.this.mBackupManagerBinder);
                } catch (RemoteException e) {
                }
            }
        }

        String readHeaderLine(InputStream in) throws IOException {
            StringBuilder buffer = new StringBuilder(80);
            while (true) {
                int c = in.read();
                if (c < 0 || c == 10) {
                    break;
                }
                buffer.append((char) c);
            }
            return buffer.toString();
        }

        InputStream decodeAesHeaderAndInitialize(String encryptionName, InputStream rawInStream) {
            InputStream result = null;
            try {
                if (encryptionName.equals(BackupManagerService.ENCRYPTION_ALGORITHM_NAME)) {
                    String userSaltHex = readHeaderLine(rawInStream);
                    byte[] userSalt = BackupManagerService.this.hexToByteArray(userSaltHex);
                    String ckSaltHex = readHeaderLine(rawInStream);
                    byte[] ckSalt = BackupManagerService.this.hexToByteArray(ckSaltHex);
                    int rounds = Integer.parseInt(readHeaderLine(rawInStream));
                    String userIvHex = readHeaderLine(rawInStream);
                    String masterKeyBlobHex = readHeaderLine(rawInStream);
                    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    SecretKey userKey = BackupManagerService.this.buildPasswordKey(this.mDecryptPassword, userSalt, rounds);
                    byte[] IV = BackupManagerService.this.hexToByteArray(userIvHex);
                    IvParameterSpec ivSpec = new IvParameterSpec(IV);
                    c.init(2, new SecretKeySpec(userKey.getEncoded(), "AES"), ivSpec);
                    byte[] mkCipher = BackupManagerService.this.hexToByteArray(masterKeyBlobHex);
                    byte[] mkBlob = c.doFinal(mkCipher);
                    int offset = 0 + 1;
                    byte b = mkBlob[0];
                    byte[] IV2 = Arrays.copyOfRange(mkBlob, offset, offset + b);
                    int offset2 = offset + b;
                    int offset3 = offset2 + 1;
                    byte b2 = mkBlob[offset2];
                    byte[] mk = Arrays.copyOfRange(mkBlob, offset3, offset3 + b2);
                    int offset4 = offset3 + b2;
                    int offset5 = offset4 + 1;
                    byte[] mkChecksum = Arrays.copyOfRange(mkBlob, offset5, offset5 + mkBlob[offset4]);
                    byte[] calculatedCk = BackupManagerService.this.makeKeyChecksum(mk, ckSalt, rounds);
                    if (Arrays.equals(calculatedCk, mkChecksum)) {
                        IvParameterSpec ivSpec2 = new IvParameterSpec(IV2);
                        c.init(2, new SecretKeySpec(mk, "AES"), ivSpec2);
                        result = new CipherInputStream(rawInStream, c);
                    } else {
                        Slog.w(BackupManagerService.TAG, "Incorrect password");
                    }
                } else {
                    Slog.w(BackupManagerService.TAG, "Unsupported encryption method: " + encryptionName);
                }
            } catch (IOException e) {
                Slog.w(BackupManagerService.TAG, "Can't read input header");
            } catch (NumberFormatException e2) {
                Slog.w(BackupManagerService.TAG, "Can't parse restore data header");
            } catch (InvalidAlgorithmParameterException e3) {
                Slog.e(BackupManagerService.TAG, "Needed parameter spec unavailable!", e3);
            } catch (InvalidKeyException e4) {
                Slog.w(BackupManagerService.TAG, "Illegal password; aborting");
            } catch (NoSuchAlgorithmException e5) {
                Slog.e(BackupManagerService.TAG, "Needed decryption algorithm unavailable!");
            } catch (BadPaddingException e6) {
                Slog.w(BackupManagerService.TAG, "Incorrect password");
            } catch (IllegalBlockSizeException e7) {
                Slog.w(BackupManagerService.TAG, "Invalid block size in master key");
            } catch (NoSuchPaddingException e8) {
                Slog.e(BackupManagerService.TAG, "Needed padding mechanism unavailable!");
            }
            return result;
        }

        boolean restoreOneFile(InputStream instream, byte[] buffer) {
            FileMetadata info;
            try {
                info = readTarHeaders(instream);
                if (info != null) {
                    String pkg = info.packageName;
                    if (!pkg.equals(this.mAgentPackage)) {
                        if (!this.mPackagePolicies.containsKey(pkg)) {
                            this.mPackagePolicies.put(pkg, RestorePolicy.IGNORE);
                        }
                        if (this.mAgent != null) {
                            Slog.d(BackupManagerService.TAG, "Saw new package; tearing down old one");
                            tearDownPipes();
                            tearDownAgent(this.mTargetApp);
                            this.mTargetApp = null;
                            this.mAgentPackage = null;
                        }
                    }
                    if (info.path.equals(BackupManagerService.BACKUP_MANIFEST_FILENAME)) {
                        this.mPackagePolicies.put(pkg, readAppManifest(info, instream));
                        this.mPackageInstallers.put(pkg, info.installerPackageName);
                        skipTarPadding(info.size, instream);
                        sendOnRestorePackage(pkg);
                    } else {
                        boolean okay = true;
                        RestorePolicy policy = this.mPackagePolicies.get(pkg);
                        switch (policy) {
                            case IGNORE:
                                okay = false;
                                break;
                            case ACCEPT_IF_APK:
                                if (info.domain.equals(FullBackup.APK_TREE_TOKEN)) {
                                    Slog.d(BackupManagerService.TAG, "APK file; installing");
                                    String installerName = this.mPackageInstallers.get(pkg);
                                    boolean okay2 = installApk(info, installerName, instream);
                                    this.mPackagePolicies.put(pkg, okay2 ? RestorePolicy.ACCEPT : RestorePolicy.IGNORE);
                                    skipTarPadding(info.size, instream);
                                    return true;
                                }
                                this.mPackagePolicies.put(pkg, RestorePolicy.IGNORE);
                                okay = false;
                                break;
                            case ACCEPT:
                                if (info.domain.equals(FullBackup.APK_TREE_TOKEN)) {
                                    Slog.d(BackupManagerService.TAG, "apk present but ACCEPT");
                                    okay = false;
                                    break;
                                }
                                break;
                            default:
                                Slog.e(BackupManagerService.TAG, "Invalid policy from manifest");
                                okay = false;
                                this.mPackagePolicies.put(pkg, RestorePolicy.IGNORE);
                                break;
                        }
                        if (okay && this.mAgent != null) {
                            Slog.i(BackupManagerService.TAG, "Reusing existing agent instance");
                        }
                        if (okay && this.mAgent == null) {
                            Slog.d(BackupManagerService.TAG, "Need to launch agent for " + pkg);
                            try {
                                this.mTargetApp = BackupManagerService.this.mPackageManager.getApplicationInfo(pkg, 0);
                                if (!this.mClearedPackages.contains(pkg)) {
                                    if (this.mTargetApp.backupAgentName == null) {
                                        Slog.d(BackupManagerService.TAG, "Clearing app data preparatory to full restore");
                                        BackupManagerService.this.clearApplicationDataSynchronous(pkg);
                                    } else {
                                        Slog.d(BackupManagerService.TAG, "backup agent (" + this.mTargetApp.backupAgentName + ") => no clear");
                                    }
                                    this.mClearedPackages.add(pkg);
                                } else {
                                    Slog.d(BackupManagerService.TAG, "We've initialized this app already; no clear required");
                                }
                                setUpPipes();
                                this.mAgent = BackupManagerService.this.bindToAgentSynchronous(this.mTargetApp, 3);
                                this.mAgentPackage = pkg;
                            } catch (PackageManager.NameNotFoundException e) {
                            } catch (IOException e2) {
                            }
                            if (this.mAgent == null) {
                                Slog.d(BackupManagerService.TAG, "Unable to create agent for " + pkg);
                                okay = false;
                                tearDownPipes();
                                this.mPackagePolicies.put(pkg, RestorePolicy.IGNORE);
                            }
                        }
                        if (okay && !pkg.equals(this.mAgentPackage)) {
                            Slog.e(BackupManagerService.TAG, "Restoring data for " + pkg + " but agent is for " + this.mAgentPackage);
                            okay = false;
                        }
                        if (okay) {
                            boolean agentSuccess = true;
                            long toCopy = info.size;
                            int token = BackupManagerService.this.generateToken();
                            try {
                                try {
                                    BackupManagerService.this.prepareOperationTimeout(token, BackupManagerService.TIMEOUT_FULL_BACKUP_INTERVAL, null);
                                    if (info.domain.equals(FullBackup.OBB_TREE_TOKEN)) {
                                        Slog.d(BackupManagerService.TAG, "Restoring OBB file for " + pkg + " : " + info.path);
                                        this.mObbConnection.restoreObbFile(pkg, this.mPipes[0], info.size, info.type, info.path, info.mode, info.mtime, token, BackupManagerService.this.mBackupManagerBinder);
                                    } else {
                                        Slog.d(BackupManagerService.TAG, "Invoking agent to restore file " + info.path);
                                        if (this.mTargetApp.processName.equals("system")) {
                                            Slog.d(BackupManagerService.TAG, "system process agent - spinning a thread");
                                            RestoreFileRunnable runner = new RestoreFileRunnable(this.mAgent, info, this.mPipes[0], token);
                                            new Thread(runner).start();
                                        } else {
                                            this.mAgent.doRestoreFile(this.mPipes[0], info.size, info.type, info.domain, info.path, info.mode, info.mtime, token, BackupManagerService.this.mBackupManagerBinder);
                                        }
                                    }
                                } catch (RemoteException e3) {
                                    Slog.e(BackupManagerService.TAG, "Agent crashed during full restore");
                                    agentSuccess = false;
                                    okay = false;
                                }
                            } catch (IOException e4) {
                                Slog.d(BackupManagerService.TAG, "Couldn't establish restore");
                                agentSuccess = false;
                                okay = false;
                            }
                            if (okay) {
                                boolean pipeOkay = true;
                                FileOutputStream pipe = new FileOutputStream(this.mPipes[1].getFileDescriptor());
                                while (toCopy > 0) {
                                    int toRead = toCopy > ((long) buffer.length) ? buffer.length : (int) toCopy;
                                    int nRead = instream.read(buffer, 0, toRead);
                                    if (nRead >= 0) {
                                        this.mBytes += nRead;
                                    }
                                    if (nRead > 0) {
                                        toCopy -= nRead;
                                        if (pipeOkay) {
                                            try {
                                                pipe.write(buffer, 0, nRead);
                                            } catch (IOException e5) {
                                                Slog.e(BackupManagerService.TAG, "Failed to write to restore pipe", e5);
                                                pipeOkay = false;
                                            }
                                        }
                                    } else {
                                        skipTarPadding(info.size, instream);
                                        agentSuccess = BackupManagerService.this.waitUntilOperationComplete(token);
                                    }
                                }
                                skipTarPadding(info.size, instream);
                                agentSuccess = BackupManagerService.this.waitUntilOperationComplete(token);
                            }
                            if (!agentSuccess) {
                                BackupManagerService.this.mBackupHandler.removeMessages(7);
                                tearDownPipes();
                                tearDownAgent(this.mTargetApp);
                                this.mAgent = null;
                                this.mPackagePolicies.put(pkg, RestorePolicy.IGNORE);
                            }
                        }
                        if (!okay) {
                            Slog.d(BackupManagerService.TAG, "[discarding file content]");
                            long bytesToConsume = (info.size + 511) & (-512);
                            while (bytesToConsume > 0) {
                                int toRead2 = bytesToConsume > ((long) buffer.length) ? buffer.length : (int) bytesToConsume;
                                long nRead2 = instream.read(buffer, 0, toRead2);
                                if (nRead2 >= 0) {
                                    this.mBytes += nRead2;
                                }
                                if (nRead2 > 0) {
                                    bytesToConsume -= nRead2;
                                }
                            }
                        }
                    }
                }
            } catch (IOException e6) {
                Slog.w(BackupManagerService.TAG, "io exception on restore socket read", e6);
                info = null;
            }
            return info != null;
        }

        void setUpPipes() throws IOException {
            this.mPipes = ParcelFileDescriptor.createPipe();
        }

        void tearDownPipes() {
            if (this.mPipes != null) {
                try {
                    this.mPipes[0].close();
                    this.mPipes[0] = null;
                    this.mPipes[1].close();
                    this.mPipes[1] = null;
                } catch (IOException e) {
                    Slog.w(BackupManagerService.TAG, "Couldn't close agent pipes", e);
                }
                this.mPipes = null;
            }
        }

        void tearDownAgent(ApplicationInfo app) {
            if (this.mAgent != null) {
                try {
                    BackupManagerService.this.mActivityManager.unbindBackupAgent(app);
                    if (app.uid != 1000 && !app.packageName.equals("com.android.backupconfirm")) {
                        Slog.d(BackupManagerService.TAG, "Killing host process");
                        BackupManagerService.this.mActivityManager.killApplicationProcess(app.processName, app.uid);
                    } else {
                        Slog.d(BackupManagerService.TAG, "Not killing after full restore");
                    }
                } catch (RemoteException e) {
                    Slog.d(BackupManagerService.TAG, "Lost app trying to shut down");
                }
                this.mAgent = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: BackupManagerService$PerformFullRestoreTask$RestoreInstallObserver.class */
        public class RestoreInstallObserver extends IPackageInstallObserver.Stub {
            final AtomicBoolean mDone = new AtomicBoolean();
            String mPackageName;
            int mResult;

            RestoreInstallObserver() {
            }

            public void reset() {
                synchronized (this.mDone) {
                    this.mDone.set(false);
                }
            }

            public void waitForCompletion() {
                synchronized (this.mDone) {
                    while (!this.mDone.get()) {
                        try {
                            this.mDone.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }

            int getResult() {
                return this.mResult;
            }

            @Override // android.content.pm.IPackageInstallObserver
            public void packageInstalled(String packageName, int returnCode) throws RemoteException {
                synchronized (this.mDone) {
                    this.mResult = returnCode;
                    this.mPackageName = packageName;
                    this.mDone.set(true);
                    this.mDone.notifyAll();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: BackupManagerService$PerformFullRestoreTask$RestoreDeleteObserver.class */
        public class RestoreDeleteObserver extends IPackageDeleteObserver.Stub {
            final AtomicBoolean mDone = new AtomicBoolean();
            int mResult;

            RestoreDeleteObserver() {
            }

            public void reset() {
                synchronized (this.mDone) {
                    this.mDone.set(false);
                }
            }

            public void waitForCompletion() {
                synchronized (this.mDone) {
                    while (!this.mDone.get()) {
                        try {
                            this.mDone.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }

            @Override // android.content.pm.IPackageDeleteObserver
            public void packageDeleted(String packageName, int returnCode) throws RemoteException {
                synchronized (this.mDone) {
                    this.mResult = returnCode;
                    this.mDone.set(true);
                    this.mDone.notifyAll();
                }
            }
        }

        void skipTarPadding(long size, InputStream instream) throws IOException {
            long partial = (size + 512) % 512;
            if (partial > 0) {
                int needed = 512 - ((int) partial);
                byte[] buffer = new byte[needed];
                if (readExactly(instream, buffer, 0, needed) == needed) {
                    this.mBytes += needed;
                    return;
                }
                throw new IOException("Unexpected EOF in padding");
            }
        }

        RestorePolicy readAppManifest(FileMetadata info, InputStream instream) throws IOException {
            if (info.size > 65536) {
                throw new IOException("Restore manifest too big; corrupt? size=" + info.size);
            }
            byte[] buffer = new byte[(int) info.size];
            if (readExactly(instream, buffer, 0, (int) info.size) == info.size) {
                this.mBytes += info.size;
                RestorePolicy policy = RestorePolicy.IGNORE;
                String[] str = new String[1];
                try {
                    int offset = extractLine(buffer, 0, str);
                    int version = Integer.parseInt(str[0]);
                    if (version == 1) {
                        int offset2 = extractLine(buffer, offset, str);
                        String manifestPackage = str[0];
                        if (manifestPackage.equals(info.packageName)) {
                            int offset3 = extractLine(buffer, offset2, str);
                            int version2 = Integer.parseInt(str[0]);
                            int offset4 = extractLine(buffer, offset3, str);
                            Integer.parseInt(str[0]);
                            int offset5 = extractLine(buffer, offset4, str);
                            info.installerPackageName = str[0].length() > 0 ? str[0] : null;
                            int offset6 = extractLine(buffer, offset5, str);
                            boolean hasApk = str[0].equals("1");
                            int offset7 = extractLine(buffer, offset6, str);
                            int numSigs = Integer.parseInt(str[0]);
                            if (numSigs > 0) {
                                Signature[] sigs = new Signature[numSigs];
                                for (int i = 0; i < numSigs; i++) {
                                    offset7 = extractLine(buffer, offset7, str);
                                    sigs[i] = new Signature(str[0]);
                                }
                                this.mManifestSignatures.put(info.packageName, sigs);
                                try {
                                    PackageInfo pkgInfo = BackupManagerService.this.mPackageManager.getPackageInfo(info.packageName, 64);
                                    int flags = pkgInfo.applicationInfo.flags;
                                    if ((flags & 32768) != 0) {
                                        if (pkgInfo.applicationInfo.uid >= 10000 || pkgInfo.applicationInfo.backupAgentName != null) {
                                            if (BackupManagerService.this.signaturesMatch(sigs, pkgInfo)) {
                                                if (pkgInfo.versionCode >= version2) {
                                                    Slog.i(BackupManagerService.TAG, "Sig + version match; taking data");
                                                    policy = RestorePolicy.ACCEPT;
                                                } else {
                                                    Slog.d(BackupManagerService.TAG, "Data version " + version2 + " is newer than installed version " + pkgInfo.versionCode + " - requiring apk");
                                                    policy = RestorePolicy.ACCEPT_IF_APK;
                                                }
                                            } else {
                                                Slog.w(BackupManagerService.TAG, "Restore manifest signatures do not match installed application for " + info.packageName);
                                            }
                                        } else {
                                            Slog.w(BackupManagerService.TAG, "Package " + info.packageName + " is system level with no agent");
                                        }
                                    } else {
                                        Slog.i(BackupManagerService.TAG, "Restore manifest from " + info.packageName + " but allowBackup=false");
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    Slog.i(BackupManagerService.TAG, "Package " + info.packageName + " not installed; requiring apk in dataset");
                                    policy = RestorePolicy.ACCEPT_IF_APK;
                                }
                                if (policy == RestorePolicy.ACCEPT_IF_APK && !hasApk) {
                                    Slog.i(BackupManagerService.TAG, "Cannot restore package " + info.packageName + " without the matching .apk");
                                }
                            } else {
                                Slog.i(BackupManagerService.TAG, "Missing signature on backed-up package " + info.packageName);
                            }
                        } else {
                            Slog.i(BackupManagerService.TAG, "Expected package " + info.packageName + " but restore manifest claims " + manifestPackage);
                        }
                    } else {
                        Slog.i(BackupManagerService.TAG, "Unknown restore manifest version " + version + " for package " + info.packageName);
                    }
                } catch (NumberFormatException e2) {
                    Slog.w(BackupManagerService.TAG, "Corrupt restore manifest for package " + info.packageName);
                } catch (IllegalArgumentException e3) {
                    Slog.w(BackupManagerService.TAG, e3.getMessage());
                }
                return policy;
            }
            throw new IOException("Unexpected EOF in manifest");
        }

        int extractLine(byte[] buffer, int offset, String[] outStr) throws IOException {
            int end = buffer.length;
            if (offset >= end) {
                throw new IOException("Incomplete data");
            }
            int pos = offset;
            while (pos < end) {
                byte c = buffer[pos];
                if (c == 10) {
                    break;
                }
                pos++;
            }
            outStr[0] = new String(buffer, offset, pos - offset);
            return pos + 1;
        }

        void dumpFileMetadata(FileMetadata info) {
            StringBuilder b = new StringBuilder(128);
            b.append(info.type == 2 ? 'd' : '-');
            b.append((info.mode & 256) != 0 ? 'r' : '-');
            b.append((info.mode & 128) != 0 ? 'w' : '-');
            b.append((info.mode & 64) != 0 ? 'x' : '-');
            b.append((info.mode & 32) != 0 ? 'r' : '-');
            b.append((info.mode & 16) != 0 ? 'w' : '-');
            b.append((info.mode & 8) != 0 ? 'x' : '-');
            b.append((info.mode & 4) != 0 ? 'r' : '-');
            b.append((info.mode & 2) != 0 ? 'w' : '-');
            b.append((info.mode & 1) != 0 ? 'x' : '-');
            b.append(String.format(" %9d ", Long.valueOf(info.size)));
            Date stamp = new Date(info.mtime);
            b.append(new SimpleDateFormat("MMM dd HH:mm:ss ").format(stamp));
            b.append(info.packageName);
            b.append(" :: ");
            b.append(info.domain);
            b.append(" :: ");
            b.append(info.path);
            Slog.i(BackupManagerService.TAG, b.toString());
        }

        FileMetadata readTarHeaders(InputStream instream) throws IOException {
            byte[] block = new byte[512];
            FileMetadata info = null;
            if (readTarHeader(instream, block)) {
                try {
                    info = new FileMetadata();
                    info.size = extractRadix(block, 124, 12, 8);
                    info.mtime = extractRadix(block, 136, 12, 8);
                    info.mode = extractRadix(block, 100, 8, 8);
                    info.path = extractString(block, 345, 155);
                    String path = extractString(block, 0, 100);
                    if (path.length() > 0) {
                        if (info.path.length() > 0) {
                            info.path += '/';
                        }
                        info.path += path;
                    }
                    int typeChar = block[156];
                    if (typeChar == 120) {
                        boolean gotHeader = readPaxExtendedHeader(instream, info);
                        if (gotHeader) {
                            gotHeader = readTarHeader(instream, block);
                        }
                        if (!gotHeader) {
                            throw new IOException("Bad or missing pax header");
                        }
                        typeChar = block[156];
                    }
                    switch (typeChar) {
                        case 0:
                            Slog.w(BackupManagerService.TAG, "Saw type=0 in tar header block, info=" + info);
                            return null;
                        case 48:
                            info.type = 1;
                            break;
                        case 53:
                            info.type = 2;
                            if (info.size != 0) {
                                Slog.w(BackupManagerService.TAG, "Directory entry with nonzero size in header");
                                info.size = 0L;
                                break;
                            }
                            break;
                        default:
                            Slog.e(BackupManagerService.TAG, "Unknown tar entity type: " + typeChar);
                            throw new IOException("Unknown entity type " + typeChar);
                    }
                    if (FullBackup.SHARED_PREFIX.regionMatches(0, info.path, 0, FullBackup.SHARED_PREFIX.length())) {
                        info.path = info.path.substring(FullBackup.SHARED_PREFIX.length());
                        info.packageName = BackupManagerService.SHARED_BACKUP_AGENT_PACKAGE;
                        info.domain = "shared";
                        Slog.i(BackupManagerService.TAG, "File in shared storage: " + info.path);
                    } else if (FullBackup.APPS_PREFIX.regionMatches(0, info.path, 0, FullBackup.APPS_PREFIX.length())) {
                        info.path = info.path.substring(FullBackup.APPS_PREFIX.length());
                        int slash = info.path.indexOf(47);
                        if (slash < 0) {
                            throw new IOException("Illegal semantic path in " + info.path);
                        }
                        info.packageName = info.path.substring(0, slash);
                        info.path = info.path.substring(slash + 1);
                        if (!info.path.equals(BackupManagerService.BACKUP_MANIFEST_FILENAME)) {
                            int slash2 = info.path.indexOf(47);
                            if (slash2 < 0) {
                                throw new IOException("Illegal semantic path in non-manifest " + info.path);
                            }
                            info.domain = info.path.substring(0, slash2);
                            info.path = info.path.substring(slash2 + 1);
                        }
                    }
                } catch (IOException e) {
                    Slog.e(BackupManagerService.TAG, "Parse error in header: " + e.getMessage());
                    HEXLOG(block);
                    throw e;
                }
            }
            return info;
        }

        private void HEXLOG(byte[] block) {
            int offset = 0;
            int todo = block.length;
            StringBuilder buf = new StringBuilder(64);
            while (todo > 0) {
                buf.append(String.format("%04x   ", Integer.valueOf(offset)));
                int numThisLine = todo > 16 ? 16 : todo;
                for (int i = 0; i < numThisLine; i++) {
                    buf.append(String.format("%02x ", Byte.valueOf(block[offset + i])));
                }
                Slog.i("hexdump", buf.toString());
                buf.setLength(0);
                todo -= numThisLine;
                offset += numThisLine;
            }
        }

        int readExactly(InputStream in, byte[] buffer, int offset, int size) throws IOException {
            int soFar;
            int nRead;
            if (size <= 0) {
                throw new IllegalArgumentException("size must be > 0");
            }
            int i = 0;
            while (true) {
                soFar = i;
                if (soFar >= size || (nRead = in.read(buffer, offset + soFar, size - soFar)) <= 0) {
                    break;
                }
                i = soFar + nRead;
            }
            return soFar;
        }

        boolean readTarHeader(InputStream instream, byte[] block) throws IOException {
            int got = readExactly(instream, block, 0, 512);
            if (got == 0) {
                return false;
            }
            if (got < 512) {
                throw new IOException("Unable to read full block header");
            }
            this.mBytes += 512;
            return true;
        }

        boolean readPaxExtendedHeader(InputStream instream, FileMetadata info) throws IOException {
            if (info.size > Trace.TRACE_TAG_RS) {
                Slog.w(BackupManagerService.TAG, "Suspiciously large pax header size " + info.size + " - aborting");
                throw new IOException("Sanity failure: pax header size " + info.size);
            }
            int numBlocks = (int) ((info.size + 511) >> 9);
            byte[] data = new byte[numBlocks * 512];
            if (readExactly(instream, data, 0, data.length) < data.length) {
                throw new IOException("Unable to read full pax header");
            }
            this.mBytes += data.length;
            int contentSize = (int) info.size;
            int offset = 0;
            do {
                int eol = offset + 1;
                while (eol < contentSize && data[eol] != 32) {
                    eol++;
                }
                if (eol >= contentSize) {
                    throw new IOException("Invalid pax data");
                }
                int linelen = (int) extractRadix(data, offset, eol - offset, 10);
                int key = eol + 1;
                int eol2 = (offset + linelen) - 1;
                int value = key + 1;
                while (data[value] != 61 && value <= eol2) {
                    value++;
                }
                if (value > eol2) {
                    throw new IOException("Invalid pax declaration");
                }
                String keyStr = new String(data, key, value - key, "UTF-8");
                String valStr = new String(data, value + 1, (eol2 - value) - 1, "UTF-8");
                if ("path".equals(keyStr)) {
                    info.path = valStr;
                } else if ("size".equals(keyStr)) {
                    info.size = Long.parseLong(valStr);
                } else {
                    Slog.i(BackupManagerService.TAG, "Unhandled pax key: " + key);
                }
                offset += linelen;
            } while (offset < contentSize);
            return true;
        }

        long extractRadix(byte[] data, int offset, int maxChars, int radix) throws IOException {
            byte b;
            long value = 0;
            int end = offset + maxChars;
            for (int i = offset; i < end && (b = data[i]) != 0 && b != 32; i++) {
                if (b < 48 || b > (48 + radix) - 1) {
                    throw new IOException("Invalid number in header: '" + ((char) b) + "' for radix " + radix);
                }
                value = (radix * value) + (b - 48);
            }
            return value;
        }

        String extractString(byte[] data, int offset, int maxChars) throws IOException {
            int end = offset + maxChars;
            int eos = offset;
            while (eos < end && data[eos] != 0) {
                eos++;
            }
            return new String(data, offset, eos - offset, "US-ASCII");
        }

        void sendStartRestore() {
            if (this.mObserver != null) {
                try {
                    this.mObserver.onStartRestore();
                } catch (RemoteException e) {
                    Slog.w(BackupManagerService.TAG, "full restore observer went away: startRestore");
                    this.mObserver = null;
                }
            }
        }

        void sendOnRestorePackage(String name) {
            if (this.mObserver != null) {
                try {
                    this.mObserver.onRestorePackage(name);
                } catch (RemoteException e) {
                    Slog.w(BackupManagerService.TAG, "full restore observer went away: restorePackage");
                    this.mObserver = null;
                }
            }
        }

        void sendEndRestore() {
            if (this.mObserver != null) {
                try {
                    this.mObserver.onEndRestore();
                } catch (RemoteException e) {
                    Slog.w(BackupManagerService.TAG, "full restore observer went away: endRestore");
                    this.mObserver = null;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean signaturesMatch(Signature[] storedSigs, PackageInfo target) {
        if ((target.applicationInfo.flags & 1) != 0) {
            Slog.v(TAG, "System app " + target.packageName + " - skipping sig check");
            return true;
        }
        Signature[] deviceSigs = target.signatures;
        if ((storedSigs == null || storedSigs.length == 0) && (deviceSigs == null || deviceSigs.length == 0)) {
            return true;
        }
        if (storedSigs == null || deviceSigs == null) {
            return false;
        }
        int nDevice = deviceSigs.length;
        for (Signature signature : storedSigs) {
            boolean match = false;
            int j = 0;
            while (true) {
                if (j < nDevice) {
                    if (!signature.equals(deviceSigs[j])) {
                        j++;
                    } else {
                        match = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!match) {
                return false;
            }
        }
        return true;
    }

    /* loaded from: BackupManagerService$PerformRestoreTask.class */
    class PerformRestoreTask implements BackupRestoreTask {
        private IBackupTransport mTransport;
        private IRestoreObserver mObserver;
        private long mToken;
        private PackageInfo mTargetPackage;
        private File mStateDir;
        private int mPmToken;
        private boolean mNeedFullBackup;
        private HashSet<String> mFilterSet;
        private long mStartRealtime;
        private List<PackageInfo> mAgentPackages;
        private ArrayList<PackageInfo> mRestorePackages;
        private int mCount;
        private int mStatus;
        private File mBackupDataName;
        private File mNewStateName;
        private File mSavedStateName;
        private ParcelFileDescriptor mBackupData;
        private ParcelFileDescriptor mNewState;
        private PackageInfo mCurrentPackage;
        private RestoreState mCurrentState = RestoreState.INITIAL;
        private boolean mFinished = false;
        private PackageManagerBackupAgent mPmAgent = null;

        /* loaded from: BackupManagerService$PerformRestoreTask$RestoreRequest.class */
        class RestoreRequest {
            public PackageInfo app;
            public int storedAppVersion;

            RestoreRequest(PackageInfo _app, int _version) {
                this.app = _app;
                this.storedAppVersion = _version;
            }
        }

        PerformRestoreTask(IBackupTransport transport, IRestoreObserver observer, long restoreSetToken, PackageInfo targetPackage, int pmToken, boolean needFullBackup, String[] filterSet) {
            this.mTransport = transport;
            this.mObserver = observer;
            this.mToken = restoreSetToken;
            this.mTargetPackage = targetPackage;
            this.mPmToken = pmToken;
            this.mNeedFullBackup = needFullBackup;
            if (filterSet != null) {
                this.mFilterSet = new HashSet<>();
                for (String pkg : filterSet) {
                    this.mFilterSet.add(pkg);
                }
            } else {
                this.mFilterSet = null;
            }
            try {
                this.mStateDir = new File(BackupManagerService.this.mBaseStateDir, transport.transportDirName());
            } catch (RemoteException e) {
            }
        }

        @Override // com.android.server.BackupManagerService.BackupRestoreTask
        public void execute() {
            switch (this.mCurrentState) {
                case INITIAL:
                    beginRestore();
                    return;
                case DOWNLOAD_DATA:
                    downloadRestoreData();
                    return;
                case PM_METADATA:
                    restorePmMetadata();
                    return;
                case RUNNING_QUEUE:
                    restoreNextAgent();
                    return;
                case FINAL:
                    if (this.mFinished) {
                        Slog.e(BackupManagerService.TAG, "Duplicate finish");
                    } else {
                        finalizeRestore();
                    }
                    this.mFinished = true;
                    return;
                default:
                    return;
            }
        }

        void beginRestore() {
            BackupManagerService.this.mBackupHandler.removeMessages(8);
            this.mStatus = 1;
            try {
                EventLog.writeEvent((int) EventLogTags.RESTORE_START, this.mTransport.transportDirName(), Long.valueOf(this.mToken));
                this.mRestorePackages = new ArrayList<>();
                PackageInfo omPackage = new PackageInfo();
                omPackage.packageName = BackupManagerService.PACKAGE_MANAGER_SENTINEL;
                this.mRestorePackages.add(omPackage);
                this.mAgentPackages = BackupManagerService.this.allAgentPackages();
                if (this.mTargetPackage == null) {
                    if (this.mFilterSet != null) {
                        for (int i = this.mAgentPackages.size() - 1; i >= 0; i--) {
                            PackageInfo pkg = this.mAgentPackages.get(i);
                            if (!this.mFilterSet.contains(pkg.packageName)) {
                                this.mAgentPackages.remove(i);
                            }
                        }
                    }
                    this.mRestorePackages.addAll(this.mAgentPackages);
                } else {
                    this.mRestorePackages.add(this.mTargetPackage);
                }
                if (this.mObserver != null) {
                    try {
                        this.mObserver.restoreStarting(this.mRestorePackages.size());
                    } catch (RemoteException e) {
                        Slog.d(BackupManagerService.TAG, "Restore observer died at restoreStarting");
                        this.mObserver = null;
                    }
                }
                this.mStatus = 0;
                executeNextState(RestoreState.DOWNLOAD_DATA);
            } catch (RemoteException e2) {
                Slog.e(BackupManagerService.TAG, "Error communicating with transport for restore");
                executeNextState(RestoreState.FINAL);
            }
        }

        void downloadRestoreData() {
            try {
                this.mStatus = this.mTransport.startRestore(this.mToken, (PackageInfo[]) this.mRestorePackages.toArray(new PackageInfo[0]));
                if (this.mStatus != 0) {
                    Slog.e(BackupManagerService.TAG, "Error starting restore operation");
                    EventLog.writeEvent((int) EventLogTags.RESTORE_TRANSPORT_FAILURE, new Object[0]);
                    executeNextState(RestoreState.FINAL);
                    return;
                }
                executeNextState(RestoreState.PM_METADATA);
            } catch (RemoteException e) {
                Slog.e(BackupManagerService.TAG, "Error communicating with transport for restore");
                EventLog.writeEvent((int) EventLogTags.RESTORE_TRANSPORT_FAILURE, new Object[0]);
                this.mStatus = 1;
                executeNextState(RestoreState.FINAL);
            }
        }

        void restorePmMetadata() {
            try {
                String packageName = this.mTransport.nextRestorePackage();
                if (packageName == null) {
                    Slog.e(BackupManagerService.TAG, "Error getting first restore package");
                    EventLog.writeEvent((int) EventLogTags.RESTORE_TRANSPORT_FAILURE, new Object[0]);
                    this.mStatus = 1;
                    executeNextState(RestoreState.FINAL);
                } else if (packageName.equals("")) {
                    Slog.i(BackupManagerService.TAG, "No restore data available");
                    int millis = (int) (SystemClock.elapsedRealtime() - this.mStartRealtime);
                    EventLog.writeEvent(2834, 0, Integer.valueOf(millis));
                    this.mStatus = 0;
                    executeNextState(RestoreState.FINAL);
                } else if (!packageName.equals(BackupManagerService.PACKAGE_MANAGER_SENTINEL)) {
                    Slog.e(BackupManagerService.TAG, "Expected restore data for \"@pm@\", found only \"" + packageName + Separators.DOUBLE_QUOTE);
                    EventLog.writeEvent(2832, BackupManagerService.PACKAGE_MANAGER_SENTINEL, "Package manager data missing");
                    executeNextState(RestoreState.FINAL);
                } else {
                    PackageInfo omPackage = new PackageInfo();
                    omPackage.packageName = BackupManagerService.PACKAGE_MANAGER_SENTINEL;
                    this.mPmAgent = new PackageManagerBackupAgent(BackupManagerService.this.mPackageManager, this.mAgentPackages);
                    initiateOneRestore(omPackage, 0, IBackupAgent.Stub.asInterface(this.mPmAgent.onBind()), this.mNeedFullBackup);
                    if (!this.mPmAgent.hasMetadata()) {
                        Slog.e(BackupManagerService.TAG, "No restore metadata available, so not restoring settings");
                        EventLog.writeEvent(2832, BackupManagerService.PACKAGE_MANAGER_SENTINEL, "Package manager restore metadata missing");
                        this.mStatus = 1;
                        BackupManagerService.this.mBackupHandler.removeMessages(20, this);
                        executeNextState(RestoreState.FINAL);
                    }
                }
            } catch (RemoteException e) {
                Slog.e(BackupManagerService.TAG, "Error communicating with transport for restore");
                EventLog.writeEvent((int) EventLogTags.RESTORE_TRANSPORT_FAILURE, new Object[0]);
                this.mStatus = 1;
                BackupManagerService.this.mBackupHandler.removeMessages(20, this);
                executeNextState(RestoreState.FINAL);
            }
        }

        void restoreNextAgent() {
            try {
                String packageName = this.mTransport.nextRestorePackage();
                if (packageName == null) {
                    Slog.e(BackupManagerService.TAG, "Error getting next restore package");
                    EventLog.writeEvent((int) EventLogTags.RESTORE_TRANSPORT_FAILURE, new Object[0]);
                    executeNextState(RestoreState.FINAL);
                } else if (packageName.equals("")) {
                    Slog.v(BackupManagerService.TAG, "No next package, finishing restore");
                    int millis = (int) (SystemClock.elapsedRealtime() - this.mStartRealtime);
                    EventLog.writeEvent(2834, Integer.valueOf(this.mCount), Integer.valueOf(millis));
                    executeNextState(RestoreState.FINAL);
                } else {
                    if (this.mObserver != null) {
                        try {
                            this.mObserver.onUpdate(this.mCount, packageName);
                        } catch (RemoteException e) {
                            Slog.d(BackupManagerService.TAG, "Restore observer died in onUpdate");
                            this.mObserver = null;
                        }
                    }
                    PackageManagerBackupAgent.Metadata metaInfo = this.mPmAgent.getRestoredMetadata(packageName);
                    if (metaInfo == null) {
                        Slog.e(BackupManagerService.TAG, "Missing metadata for " + packageName);
                        EventLog.writeEvent(2832, packageName, "Package metadata missing");
                        executeNextState(RestoreState.RUNNING_QUEUE);
                        return;
                    }
                    try {
                        PackageInfo packageInfo = BackupManagerService.this.mPackageManager.getPackageInfo(packageName, 64);
                        if (packageInfo.applicationInfo.backupAgentName == null || "".equals(packageInfo.applicationInfo.backupAgentName)) {
                            Slog.i(BackupManagerService.TAG, "Data exists for package " + packageName + " but app has no agent; skipping");
                            EventLog.writeEvent(2832, packageName, "Package has no agent");
                            executeNextState(RestoreState.RUNNING_QUEUE);
                            return;
                        }
                        if (metaInfo.versionCode > packageInfo.versionCode) {
                            if ((packageInfo.applicationInfo.flags & 131072) == 0) {
                                String message = "Version " + metaInfo.versionCode + " > installed version " + packageInfo.versionCode;
                                Slog.w(BackupManagerService.TAG, "Package " + packageName + ": " + message);
                                EventLog.writeEvent(2832, packageName, message);
                                executeNextState(RestoreState.RUNNING_QUEUE);
                                return;
                            }
                            Slog.v(BackupManagerService.TAG, "Version " + metaInfo.versionCode + " > installed " + packageInfo.versionCode + " but restoreAnyVersion");
                        }
                        if (!BackupManagerService.this.signaturesMatch(metaInfo.signatures, packageInfo)) {
                            Slog.w(BackupManagerService.TAG, "Signature mismatch restoring " + packageName);
                            EventLog.writeEvent(2832, packageName, "Signature mismatch");
                            executeNextState(RestoreState.RUNNING_QUEUE);
                            return;
                        }
                        Slog.v(BackupManagerService.TAG, "Package " + packageName + " restore version [" + metaInfo.versionCode + "] is compatible with installed version [" + packageInfo.versionCode + "]");
                        IBackupAgent agent = BackupManagerService.this.bindToAgentSynchronous(packageInfo.applicationInfo, 0);
                        if (agent == null) {
                            Slog.w(BackupManagerService.TAG, "Can't find backup agent for " + packageName);
                            EventLog.writeEvent(2832, packageName, "Restore agent missing");
                            executeNextState(RestoreState.RUNNING_QUEUE);
                            return;
                        }
                        try {
                            initiateOneRestore(packageInfo, metaInfo.versionCode, agent, this.mNeedFullBackup);
                            this.mCount++;
                        } catch (Exception e2) {
                            Slog.e(BackupManagerService.TAG, "Error when attempting restore: " + e2.toString());
                            agentErrorCleanup();
                            executeNextState(RestoreState.RUNNING_QUEUE);
                        }
                    } catch (PackageManager.NameNotFoundException e3) {
                        Slog.e(BackupManagerService.TAG, "Invalid package restoring data", e3);
                        EventLog.writeEvent(2832, packageName, "Package missing on device");
                        executeNextState(RestoreState.RUNNING_QUEUE);
                    }
                }
            } catch (RemoteException e4) {
                Slog.e(BackupManagerService.TAG, "Unable to fetch restore data from transport");
                this.mStatus = 1;
                executeNextState(RestoreState.FINAL);
            }
        }

        void finalizeRestore() {
            try {
                this.mTransport.finishRestore();
            } catch (RemoteException e) {
                Slog.e(BackupManagerService.TAG, "Error finishing restore", e);
            }
            if (this.mObserver != null) {
                try {
                    this.mObserver.restoreFinished(this.mStatus);
                } catch (RemoteException e2) {
                    Slog.d(BackupManagerService.TAG, "Restore observer died at restoreFinished");
                }
            }
            if (this.mTargetPackage == null && this.mPmAgent != null) {
                BackupManagerService.this.mAncestralPackages = this.mPmAgent.getRestoredPackages();
                BackupManagerService.this.mAncestralToken = this.mToken;
                BackupManagerService.this.writeRestoreTokens();
            }
            if (this.mPmToken > 0) {
                try {
                    BackupManagerService.this.mPackageManagerBinder.finishPackageInstall(this.mPmToken);
                } catch (RemoteException e3) {
                }
            }
            BackupManagerService.this.mBackupHandler.removeMessages(8);
            BackupManagerService.this.mBackupHandler.sendEmptyMessageDelayed(8, DateUtils.MINUTE_IN_MILLIS);
            Slog.i(BackupManagerService.TAG, "Restore complete.");
            BackupManagerService.this.mWakelock.release();
        }

        void initiateOneRestore(PackageInfo app, int appVersionCode, IBackupAgent agent, boolean needFullBackup) {
            this.mCurrentPackage = app;
            String packageName = app.packageName;
            Slog.d(BackupManagerService.TAG, "initiateOneRestore packageName=" + packageName);
            this.mBackupDataName = new File(BackupManagerService.this.mDataDir, packageName + ".restore");
            this.mNewStateName = new File(this.mStateDir, packageName + ".new");
            this.mSavedStateName = new File(this.mStateDir, packageName);
            int token = BackupManagerService.this.generateToken();
            try {
                this.mBackupData = ParcelFileDescriptor.open(this.mBackupDataName, 1006632960);
                if (!SELinux.restorecon(this.mBackupDataName)) {
                    Slog.e(BackupManagerService.TAG, "SElinux restorecon failed for " + this.mBackupDataName);
                }
                if (this.mTransport.getRestoreData(this.mBackupData) != 0) {
                    Slog.e(BackupManagerService.TAG, "Error getting restore data for " + packageName);
                    EventLog.writeEvent((int) EventLogTags.RESTORE_TRANSPORT_FAILURE, new Object[0]);
                    this.mBackupData.close();
                    this.mBackupDataName.delete();
                    executeNextState(RestoreState.FINAL);
                    return;
                }
                this.mBackupData.close();
                this.mBackupData = ParcelFileDescriptor.open(this.mBackupDataName, 268435456);
                this.mNewState = ParcelFileDescriptor.open(this.mNewStateName, 1006632960);
                BackupManagerService.this.prepareOperationTimeout(token, DateUtils.MINUTE_IN_MILLIS, this);
                agent.doRestore(this.mBackupData, appVersionCode, this.mNewState, token, BackupManagerService.this.mBackupManagerBinder);
            } catch (Exception e) {
                Slog.e(BackupManagerService.TAG, "Unable to call app for restore: " + packageName, e);
                EventLog.writeEvent(2832, packageName, e.toString());
                agentErrorCleanup();
                executeNextState(RestoreState.RUNNING_QUEUE);
            }
        }

        void agentErrorCleanup() {
            BackupManagerService.this.clearApplicationDataSynchronous(this.mCurrentPackage.packageName);
            agentCleanup();
        }

        void agentCleanup() {
            this.mBackupDataName.delete();
            try {
                if (this.mBackupData != null) {
                    this.mBackupData.close();
                }
            } catch (IOException e) {
            }
            try {
                if (this.mNewState != null) {
                    this.mNewState.close();
                }
            } catch (IOException e2) {
            }
            this.mNewState = null;
            this.mBackupData = null;
            this.mNewStateName.delete();
            if (this.mCurrentPackage.applicationInfo != null) {
                try {
                    BackupManagerService.this.mActivityManager.unbindBackupAgent(this.mCurrentPackage.applicationInfo);
                    if (this.mTargetPackage == null && (this.mCurrentPackage.applicationInfo.flags & 65536) != 0) {
                        Slog.d(BackupManagerService.TAG, "Restore complete, killing host process of " + this.mCurrentPackage.applicationInfo.processName);
                        BackupManagerService.this.mActivityManager.killApplicationProcess(this.mCurrentPackage.applicationInfo.processName, this.mCurrentPackage.applicationInfo.uid);
                    }
                } catch (RemoteException e3) {
                }
            }
            BackupManagerService.this.mBackupHandler.removeMessages(7, this);
            synchronized (BackupManagerService.this.mCurrentOpLock) {
                BackupManagerService.this.mCurrentOperations.clear();
            }
        }

        @Override // com.android.server.BackupManagerService.BackupRestoreTask
        public void operationComplete() {
            int size = (int) this.mBackupDataName.length();
            EventLog.writeEvent(2833, this.mCurrentPackage.packageName, Integer.valueOf(size));
            agentCleanup();
            executeNextState(RestoreState.RUNNING_QUEUE);
        }

        @Override // com.android.server.BackupManagerService.BackupRestoreTask
        public void handleTimeout() {
            Slog.e(BackupManagerService.TAG, "Timeout restoring application " + this.mCurrentPackage.packageName);
            EventLog.writeEvent(2832, this.mCurrentPackage.packageName, "restore timeout");
            agentErrorCleanup();
            executeNextState(RestoreState.RUNNING_QUEUE);
        }

        void executeNextState(RestoreState nextState) {
            this.mCurrentState = nextState;
            Message msg = BackupManagerService.this.mBackupHandler.obtainMessage(20, this);
            BackupManagerService.this.mBackupHandler.sendMessage(msg);
        }
    }

    /* loaded from: BackupManagerService$PerformClearTask.class */
    class PerformClearTask implements Runnable {
        IBackupTransport mTransport;
        PackageInfo mPackage;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformClearTask.run():void, file: BackupManagerService$PerformClearTask.class
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
        @Override // java.lang.Runnable
        public void run() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformClearTask.run():void, file: BackupManagerService$PerformClearTask.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.PerformClearTask.run():void");
        }

        PerformClearTask(IBackupTransport transport, PackageInfo packageInfo) {
            this.mTransport = transport;
            this.mPackage = packageInfo;
        }
    }

    /* loaded from: BackupManagerService$PerformInitializeTask.class */
    class PerformInitializeTask implements Runnable {
        HashSet<String> mQueue;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformInitializeTask.run():void, file: BackupManagerService$PerformInitializeTask.class
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
        @Override // java.lang.Runnable
        public void run() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.PerformInitializeTask.run():void, file: BackupManagerService$PerformInitializeTask.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.PerformInitializeTask.run():void");
        }

        PerformInitializeTask(HashSet<String> transportNames) {
            this.mQueue = transportNames;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dataChangedImpl(String packageName) {
        HashSet<String> targets = dataChangedTargets(packageName);
        dataChangedImpl(packageName, targets);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dataChangedImpl(String packageName, HashSet<String> targets) {
        EventLog.writeEvent((int) EventLogTags.BACKUP_DATA_CHANGED, packageName);
        if (targets == null) {
            Slog.w(TAG, "dataChanged but no participant pkg='" + packageName + Separators.QUOTE + " uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mQueueLock) {
            if (targets.contains(packageName)) {
                BackupRequest req = new BackupRequest(packageName);
                if (this.mPendingBackups.put(packageName, req) == null) {
                    Slog.d(TAG, "Now staging backup of " + packageName);
                    writeToJournalLocked(packageName);
                }
            }
        }
    }

    private HashSet<String> dataChangedTargets(String packageName) {
        HashSet<String> hashSet;
        if (this.mContext.checkPermission(Manifest.permission.BACKUP, Binder.getCallingPid(), Binder.getCallingUid()) == -1) {
            synchronized (this.mBackupParticipants) {
                hashSet = this.mBackupParticipants.get(Binder.getCallingUid());
            }
            return hashSet;
        }
        HashSet<String> targets = new HashSet<>();
        synchronized (this.mBackupParticipants) {
            int N = this.mBackupParticipants.size();
            for (int i = 0; i < N; i++) {
                HashSet<String> s = this.mBackupParticipants.valueAt(i);
                if (s != null) {
                    targets.addAll(s);
                }
            }
        }
        return targets;
    }

    @Override // android.app.backup.IBackupManager
    public void dataChanged(final String packageName) {
        int callingUserHandle = UserHandle.getCallingUserId();
        if (callingUserHandle != 0) {
            return;
        }
        final HashSet<String> targets = dataChangedTargets(packageName);
        if (targets == null) {
            Slog.w(TAG, "dataChanged but no participant pkg='" + packageName + Separators.QUOTE + " uid=" + Binder.getCallingUid());
        } else {
            this.mBackupHandler.post(new Runnable() { // from class: com.android.server.BackupManagerService.3
                @Override // java.lang.Runnable
                public void run() {
                    BackupManagerService.this.dataChangedImpl(packageName, targets);
                }
            });
        }
    }

    @Override // android.app.backup.IBackupManager
    public void clearBackupData(String packageName) {
        HashSet<String> apps;
        Slog.v(TAG, "clearBackupData() of " + packageName);
        try {
            PackageInfo info = this.mPackageManager.getPackageInfo(packageName, 64);
            if (this.mContext.checkPermission(Manifest.permission.BACKUP, Binder.getCallingPid(), Binder.getCallingUid()) == -1) {
                apps = this.mBackupParticipants.get(Binder.getCallingUid());
            } else {
                Slog.v(TAG, "Privileged caller, allowing clear of other apps");
                apps = new HashSet<>();
                int N = this.mBackupParticipants.size();
                for (int i = 0; i < N; i++) {
                    HashSet<String> s = this.mBackupParticipants.valueAt(i);
                    if (s != null) {
                        apps.addAll(s);
                    }
                }
            }
            if (apps.contains(packageName)) {
                Slog.v(TAG, "Found the app - running clear process");
                synchronized (this.mQueueLock) {
                    long oldId = Binder.clearCallingIdentity();
                    this.mWakelock.acquire();
                    Message msg = this.mBackupHandler.obtainMessage(4, new ClearParams(getTransport(this.mCurrentTransport), info));
                    this.mBackupHandler.sendMessage(msg);
                    Binder.restoreCallingIdentity(oldId);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Slog.d(TAG, "No such package '" + packageName + "' - not clearing backup data");
        }
    }

    @Override // android.app.backup.IBackupManager
    public void backupNow() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "backupNow");
        Slog.v(TAG, "Scheduling immediate backup pass");
        synchronized (this.mQueueLock) {
            startBackupAlarmsLocked(3600000L);
            try {
                this.mRunBackupIntent.send();
            } catch (PendingIntent.CanceledException e) {
                Slog.e(TAG, "run-backup intent cancelled!");
            }
        }
    }

    boolean deviceIsProvisioned() {
        ContentResolver resolver = this.mContext.getContentResolver();
        return Settings.Global.getInt(resolver, "device_provisioned", 0) != 0;
    }

    boolean startConfirmationUi(int token, String action) {
        try {
            Intent confIntent = new Intent(action);
            confIntent.setClassName("com.android.backupconfirm", "com.android.backupconfirm.BackupRestoreConfirmation");
            confIntent.putExtra(FullBackup.CONF_TOKEN_INTENT_EXTRA, token);
            confIntent.addFlags(268435456);
            this.mContext.startActivity(confIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    void startConfirmationTimeout(int token, FullParams params) {
        Message msg = this.mBackupHandler.obtainMessage(9, token, 0, params);
        this.mBackupHandler.sendMessageDelayed(msg, DateUtils.MINUTE_IN_MILLIS);
    }

    void waitForCompletion(FullParams params) {
        synchronized (params.latch) {
            while (!params.latch.get()) {
                try {
                    params.latch.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    void signalFullBackupRestoreCompletion(FullParams params) {
        synchronized (params.latch) {
            params.latch.set(true);
            params.latch.notifyAll();
        }
    }

    @Override // android.app.backup.IBackupManager
    public void setAutoRestore(boolean doAutoRestore) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "setAutoRestore");
        Slog.i(TAG, "Auto restore => " + doAutoRestore);
        synchronized (this) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), Settings.Secure.BACKUP_AUTO_RESTORE, doAutoRestore ? 1 : 0);
            this.mAutoRestore = doAutoRestore;
        }
    }

    @Override // android.app.backup.IBackupManager
    public void setBackupProvisioned(boolean available) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "setBackupProvisioned");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startBackupAlarmsLocked(long delayBeforeFirstBackup) {
        Random random = new Random();
        long when = System.currentTimeMillis() + delayBeforeFirstBackup + random.nextInt(FUZZ_MILLIS);
        this.mAlarmManager.setRepeating(0, when, 3600000 + random.nextInt(FUZZ_MILLIS), this.mRunBackupIntent);
        this.mNextBackupPass = when;
    }

    @Override // android.app.backup.IBackupManager
    public boolean isBackupEnabled() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "isBackupEnabled");
        return this.mEnabled;
    }

    @Override // android.app.backup.IBackupManager
    public String getCurrentTransport() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "getCurrentTransport");
        return this.mCurrentTransport;
    }

    @Override // android.app.backup.IBackupManager
    public String[] listAllTransports() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "listAllTransports");
        String[] list = null;
        ArrayList<String> known = new ArrayList<>();
        for (Map.Entry<String, IBackupTransport> entry : this.mTransports.entrySet()) {
            if (entry.getValue() != null) {
                known.add(entry.getKey());
            }
        }
        if (known.size() > 0) {
            list = new String[known.size()];
            known.toArray(list);
        }
        return list;
    }

    @Override // android.app.backup.IBackupManager
    public String selectBackupTransport(String transport) {
        String str;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "selectBackupTransport");
        synchronized (this.mTransports) {
            String prevTransport = null;
            if (this.mTransports.get(transport) != null) {
                prevTransport = this.mCurrentTransport;
                this.mCurrentTransport = transport;
                Settings.Secure.putString(this.mContext.getContentResolver(), Settings.Secure.BACKUP_TRANSPORT, transport);
                Slog.v(TAG, "selectBackupTransport() set " + this.mCurrentTransport + " returning " + prevTransport);
            } else {
                Slog.w(TAG, "Attempt to select unavailable transport " + transport);
            }
            str = prevTransport;
        }
        return str;
    }

    @Override // android.app.backup.IBackupManager
    public Intent getConfigurationIntent(String transportName) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "getConfigurationIntent");
        synchronized (this.mTransports) {
            IBackupTransport transport = this.mTransports.get(transportName);
            if (transport != null) {
                try {
                    Intent intent = transport.configurationIntent();
                    return intent;
                } catch (RemoteException e) {
                }
            }
            return null;
        }
    }

    @Override // android.app.backup.IBackupManager
    public String getDestinationString(String transportName) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "getDestinationString");
        synchronized (this.mTransports) {
            IBackupTransport transport = this.mTransports.get(transportName);
            if (transport != null) {
                try {
                    String text = transport.currentDestinationString();
                    return text;
                } catch (RemoteException e) {
                }
            }
            return null;
        }
    }

    @Override // android.app.backup.IBackupManager
    public void agentConnected(String packageName, IBinder agentBinder) {
        synchronized (this.mAgentConnectLock) {
            if (Binder.getCallingUid() == 1000) {
                Slog.d(TAG, "agentConnected pkg=" + packageName + " agent=" + agentBinder);
                IBackupAgent agent = IBackupAgent.Stub.asInterface(agentBinder);
                this.mConnectedAgent = agent;
                this.mConnecting = false;
            } else {
                Slog.w(TAG, "Non-system process uid=" + Binder.getCallingUid() + " claiming agent connected");
            }
            this.mAgentConnectLock.notifyAll();
        }
    }

    @Override // android.app.backup.IBackupManager
    public void agentDisconnected(String packageName) {
        synchronized (this.mAgentConnectLock) {
            if (Binder.getCallingUid() == 1000) {
                this.mConnectedAgent = null;
                this.mConnecting = false;
            } else {
                Slog.w(TAG, "Non-system process uid=" + Binder.getCallingUid() + " claiming agent disconnected");
            }
            this.mAgentConnectLock.notifyAll();
        }
    }

    @Override // android.app.backup.IBackupManager
    public void restoreAtInstall(String packageName, int token) {
        if (Binder.getCallingUid() != 1000) {
            Slog.w(TAG, "Non-system process uid=" + Binder.getCallingUid() + " attemping install-time restore");
            return;
        }
        long restoreSet = getAvailableRestoreToken(packageName);
        Slog.v(TAG, "restoreAtInstall pkg=" + packageName + " token=" + Integer.toHexString(token) + " restoreSet=" + Long.toHexString(restoreSet));
        if (this.mAutoRestore && this.mProvisioned && restoreSet != 0) {
            PackageInfo pkg = new PackageInfo();
            pkg.packageName = packageName;
            this.mWakelock.acquire();
            Message msg = this.mBackupHandler.obtainMessage(3);
            msg.obj = new RestoreParams(getTransport(this.mCurrentTransport), null, restoreSet, pkg, token, true);
            this.mBackupHandler.sendMessage(msg);
            return;
        }
        Slog.v(TAG, "No restore set -- skipping restore");
        try {
            this.mPackageManagerBinder.finishPackageInstall(token);
        } catch (RemoteException e) {
        }
    }

    @Override // android.app.backup.IBackupManager
    public IRestoreSession beginRestoreSession(String packageName, String transport) {
        Slog.v(TAG, "beginRestoreSession: pkg=" + packageName + " transport=" + transport);
        boolean needPermission = true;
        if (transport == null) {
            transport = this.mCurrentTransport;
            if (packageName != null) {
                try {
                    PackageInfo app = this.mPackageManager.getPackageInfo(packageName, 0);
                    if (app.applicationInfo.uid == Binder.getCallingUid()) {
                        needPermission = false;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Slog.w(TAG, "Asked to restore nonexistent pkg " + packageName);
                    throw new IllegalArgumentException("Package " + packageName + " not found");
                }
            }
        }
        if (needPermission) {
            this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "beginRestoreSession");
        } else {
            Slog.d(TAG, "restoring self on current transport; no permission needed");
        }
        synchronized (this) {
            if (this.mActiveRestoreSession != null) {
                Slog.d(TAG, "Restore session requested but one already active");
                return null;
            }
            this.mActiveRestoreSession = new ActiveRestoreSession(packageName, transport);
            this.mBackupHandler.sendEmptyMessageDelayed(8, DateUtils.MINUTE_IN_MILLIS);
            return this.mActiveRestoreSession;
        }
    }

    void clearRestoreSession(ActiveRestoreSession currentSession) {
        synchronized (this) {
            if (currentSession != this.mActiveRestoreSession) {
                Slog.e(TAG, "ending non-current restore session");
            } else {
                Slog.v(TAG, "Clearing restore session and halting timeout");
                this.mActiveRestoreSession = null;
                this.mBackupHandler.removeMessages(8);
            }
        }
    }

    @Override // android.app.backup.IBackupManager
    public void opComplete(int token) {
        Operation op;
        synchronized (this.mCurrentOpLock) {
            op = this.mCurrentOperations.get(token);
            if (op != null) {
                op.state = 1;
            }
            this.mCurrentOpLock.notifyAll();
        }
        if (op != null && op.callback != null) {
            Message msg = this.mBackupHandler.obtainMessage(21, op.callback);
            this.mBackupHandler.sendMessage(msg);
        }
    }

    /* loaded from: BackupManagerService$ActiveRestoreSession.class */
    class ActiveRestoreSession extends IRestoreSession.Stub {
        private static final String TAG = "RestoreSession";
        private String mPackageName;
        private IBackupTransport mRestoreTransport;
        RestoreSet[] mRestoreSets = null;
        boolean mEnded = false;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.ActiveRestoreSession.getAvailableRestoreSets(android.app.backup.IRestoreObserver):int, file: BackupManagerService$ActiveRestoreSession.class
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
        @Override // android.app.backup.IRestoreSession
        public synchronized int getAvailableRestoreSets(android.app.backup.IRestoreObserver r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.ActiveRestoreSession.getAvailableRestoreSets(android.app.backup.IRestoreObserver):int, file: BackupManagerService$ActiveRestoreSession.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.ActiveRestoreSession.getAvailableRestoreSets(android.app.backup.IRestoreObserver):int");
        }

        static /* synthetic */ IBackupTransport access$2100(ActiveRestoreSession x0) {
            return x0.mRestoreTransport;
        }

        ActiveRestoreSession(String packageName, String transport) {
            this.mRestoreTransport = null;
            this.mPackageName = packageName;
            this.mRestoreTransport = BackupManagerService.this.getTransport(transport);
        }

        @Override // android.app.backup.IRestoreSession
        public synchronized int restoreAll(long token, IRestoreObserver observer) {
            BackupManagerService.this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "performRestore");
            Slog.d(TAG, "restoreAll token=" + Long.toHexString(token) + " observer=" + observer);
            if (this.mEnded) {
                throw new IllegalStateException("Restore session already ended");
            }
            if (this.mRestoreTransport == null || this.mRestoreSets == null) {
                Slog.e(TAG, "Ignoring restoreAll() with no restore set");
                return -1;
            } else if (this.mPackageName != null) {
                Slog.e(TAG, "Ignoring restoreAll() on single-package session");
                return -1;
            } else {
                synchronized (BackupManagerService.this.mQueueLock) {
                    for (int i = 0; i < this.mRestoreSets.length; i++) {
                        if (token == this.mRestoreSets[i].token) {
                            long oldId = Binder.clearCallingIdentity();
                            BackupManagerService.this.mWakelock.acquire();
                            Message msg = BackupManagerService.this.mBackupHandler.obtainMessage(3);
                            msg.obj = new RestoreParams(this.mRestoreTransport, observer, token, true);
                            BackupManagerService.this.mBackupHandler.sendMessage(msg);
                            Binder.restoreCallingIdentity(oldId);
                            return 0;
                        }
                    }
                    Slog.w(TAG, "Restore token " + Long.toHexString(token) + " not found");
                    return -1;
                }
            }
        }

        @Override // android.app.backup.IRestoreSession
        public synchronized int restoreSome(long token, IRestoreObserver observer, String[] packages) {
            BackupManagerService.this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "performRestore");
            StringBuilder b = new StringBuilder(128);
            b.append("restoreSome token=");
            b.append(Long.toHexString(token));
            b.append(" observer=");
            b.append(observer.toString());
            b.append(" packages=");
            if (packages == null) {
                b.append("null");
            } else {
                b.append('{');
                boolean first = true;
                for (String s : packages) {
                    if (!first) {
                        b.append(", ");
                    } else {
                        first = false;
                    }
                    b.append(s);
                }
                b.append('}');
            }
            Slog.d(TAG, b.toString());
            if (this.mEnded) {
                throw new IllegalStateException("Restore session already ended");
            }
            if (this.mRestoreTransport == null || this.mRestoreSets == null) {
                Slog.e(TAG, "Ignoring restoreAll() with no restore set");
                return -1;
            } else if (this.mPackageName != null) {
                Slog.e(TAG, "Ignoring restoreAll() on single-package session");
                return -1;
            } else {
                synchronized (BackupManagerService.this.mQueueLock) {
                    for (int i = 0; i < this.mRestoreSets.length; i++) {
                        if (token == this.mRestoreSets[i].token) {
                            long oldId = Binder.clearCallingIdentity();
                            BackupManagerService.this.mWakelock.acquire();
                            Message msg = BackupManagerService.this.mBackupHandler.obtainMessage(3);
                            msg.obj = new RestoreParams(this.mRestoreTransport, observer, token, packages, true);
                            BackupManagerService.this.mBackupHandler.sendMessage(msg);
                            Binder.restoreCallingIdentity(oldId);
                            return 0;
                        }
                    }
                    Slog.w(TAG, "Restore token " + Long.toHexString(token) + " not found");
                    return -1;
                }
            }
        }

        @Override // android.app.backup.IRestoreSession
        public synchronized int restorePackage(String packageName, IRestoreObserver observer) {
            Slog.v(TAG, "restorePackage pkg=" + packageName + " obs=" + observer);
            if (this.mEnded) {
                throw new IllegalStateException("Restore session already ended");
            }
            if (this.mPackageName == null || this.mPackageName.equals(packageName)) {
                try {
                    PackageInfo app = BackupManagerService.this.mPackageManager.getPackageInfo(packageName, 0);
                    int perm = BackupManagerService.this.mContext.checkPermission(Manifest.permission.BACKUP, Binder.getCallingPid(), Binder.getCallingUid());
                    if (perm == -1 && app.applicationInfo.uid != Binder.getCallingUid()) {
                        Slog.w(TAG, "restorePackage: bad packageName=" + packageName + " or calling uid=" + Binder.getCallingUid());
                        throw new SecurityException("No permission to restore other packages");
                    } else if (app.applicationInfo.backupAgentName == null) {
                        Slog.w(TAG, "Asked to restore package " + packageName + " with no agent");
                        return -1;
                    } else {
                        long token = BackupManagerService.this.getAvailableRestoreToken(packageName);
                        if (token == 0) {
                            Slog.w(TAG, "No data available for this package; not restoring");
                            return -1;
                        }
                        long oldId = Binder.clearCallingIdentity();
                        BackupManagerService.this.mWakelock.acquire();
                        Message msg = BackupManagerService.this.mBackupHandler.obtainMessage(3);
                        msg.obj = new RestoreParams(this.mRestoreTransport, observer, token, app, 0, false);
                        BackupManagerService.this.mBackupHandler.sendMessage(msg);
                        Binder.restoreCallingIdentity(oldId);
                        return 0;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Slog.w(TAG, "Asked to restore nonexistent pkg " + packageName);
                    return -1;
                }
            }
            Slog.e(TAG, "Ignoring attempt to restore pkg=" + packageName + " on session for package " + this.mPackageName);
            return -1;
        }

        /* loaded from: BackupManagerService$ActiveRestoreSession$EndRestoreRunnable.class */
        class EndRestoreRunnable implements Runnable {
            BackupManagerService mBackupManager;
            ActiveRestoreSession mSession;

            /*  JADX ERROR: Method load error
                jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.ActiveRestoreSession.EndRestoreRunnable.run():void, file: BackupManagerService$ActiveRestoreSession$EndRestoreRunnable.class
                	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
                	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
                	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
                Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
                	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
                	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
                	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
                	... 2 more
                */
            @Override // java.lang.Runnable
            public void run() {
                /*
                // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BackupManagerService.ActiveRestoreSession.EndRestoreRunnable.run():void, file: BackupManagerService$ActiveRestoreSession$EndRestoreRunnable.class
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.BackupManagerService.ActiveRestoreSession.EndRestoreRunnable.run():void");
            }

            EndRestoreRunnable(BackupManagerService manager, ActiveRestoreSession session) {
                this.mBackupManager = manager;
                this.mSession = session;
            }
        }

        @Override // android.app.backup.IRestoreSession
        public synchronized void endRestoreSession() {
            Slog.d(TAG, "endRestoreSession");
            if (this.mEnded) {
                throw new IllegalStateException("Restore session already ended");
            }
            BackupManagerService.this.mBackupHandler.post(new EndRestoreRunnable(BackupManagerService.this, this));
        }
    }

    private void dumpInternal(PrintWriter pw) {
        synchronized (this.mQueueLock) {
            pw.println("Backup Manager is " + (this.mEnabled ? "enabled" : "disabled") + " / " + (!this.mProvisioned ? "not " : "") + "provisioned / " + (this.mPendingInits.size() == 0 ? "not " : "") + "pending init");
            pw.println("Auto-restore is " + (this.mAutoRestore ? "enabled" : "disabled"));
            if (this.mBackupRunning) {
                pw.println("Backup currently running");
            }
            pw.println("Last backup pass started: " + this.mLastBackupPass + " (now = " + System.currentTimeMillis() + ')');
            pw.println("  next scheduled: " + this.mNextBackupPass);
            pw.println("Available transports:");
            String[] arr$ = listAllTransports();
            for (String t : arr$) {
                pw.println((t.equals(this.mCurrentTransport) ? "  * " : "    ") + t);
                try {
                    IBackupTransport transport = getTransport(t);
                    File dir = new File(this.mBaseStateDir, transport.transportDirName());
                    pw.println("       destination: " + transport.currentDestinationString());
                    pw.println("       intent: " + transport.configurationIntent());
                    File[] arr$2 = dir.listFiles();
                    for (File f : arr$2) {
                        pw.println("       " + f.getName() + " - " + f.length() + " state bytes");
                    }
                } catch (Exception e) {
                    Slog.e(TAG, "Error in transport", e);
                    pw.println("        Error: " + e);
                }
            }
            pw.println("Pending init: " + this.mPendingInits.size());
            Iterator i$ = this.mPendingInits.iterator();
            while (i$.hasNext()) {
                String s = i$.next();
                pw.println("    " + s);
            }
            synchronized (this.mBackupTrace) {
                if (!this.mBackupTrace.isEmpty()) {
                    pw.println("Most recent backup trace:");
                    for (String s2 : this.mBackupTrace) {
                        pw.println("   " + s2);
                    }
                }
            }
            int N = this.mBackupParticipants.size();
            pw.println("Participants:");
            for (int i = 0; i < N; i++) {
                int uid = this.mBackupParticipants.keyAt(i);
                pw.print("  uid: ");
                pw.println(uid);
                HashSet<String> participants = this.mBackupParticipants.valueAt(i);
                Iterator i$2 = participants.iterator();
                while (i$2.hasNext()) {
                    String app = i$2.next();
                    pw.println("    " + app);
                }
            }
            pw.println("Ancestral packages: " + (this.mAncestralPackages == null ? "none" : Integer.valueOf(this.mAncestralPackages.size())));
            if (this.mAncestralPackages != null) {
                for (String pkg : this.mAncestralPackages) {
                    pw.println("    " + pkg);
                }
            }
            pw.println("Ever backed up: " + this.mEverStoredApps.size());
            Iterator i$3 = this.mEverStoredApps.iterator();
            while (i$3.hasNext()) {
                String pkg2 = i$3.next();
                pw.println("    " + pkg2);
            }
            pw.println("Pending backup: " + this.mPendingBackups.size());
            for (BackupRequest req : this.mPendingBackups.values()) {
                pw.println("    " + req);
            }
        }
    }
}