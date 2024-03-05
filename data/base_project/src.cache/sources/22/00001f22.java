package com.android.server.pm;

import android.Manifest;
import android.accounts.GrantCredentialsPermissionActivity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.admin.IDevicePolicyManager;
import android.app.backup.IBackupManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ContainerEncryptionParams;
import android.content.pm.FeatureInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.InstrumentationInfo;
import android.content.pm.ManifestDigest;
import android.content.pm.PackageCleanItem;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInfoLite;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageStats;
import android.content.pm.PackageUserState;
import android.content.pm.ParceledListSlice;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.pm.VerificationParams;
import android.content.pm.VerifierDeviceIdentity;
import android.content.pm.VerifierInfo;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.media.videoeditor.MediaProperties;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.FileUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.security.KeyStore;
import android.security.SystemKeyStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.LogPrinter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import android.view.Display;
import android.view.WindowManager;
import com.android.internal.R;
import com.android.internal.app.IMediaContainerService;
import com.android.internal.app.ResolverActivity;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.content.PackageHelper;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.XmlUtils;
import com.android.server.DeviceStorageMonitorService;
import com.android.server.EventLogTags;
import com.android.server.IntentResolver;
import com.android.server.Watchdog;
import dalvik.system.DexFile;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import libcore.io.ErrnoException;
import libcore.io.Libcore;
import libcore.io.OsConstants;
import libcore.io.StructStat;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: PackageManagerService.class */
public class PackageManagerService extends IPackageManager.Stub {
    static final String TAG = "PackageManager";
    static final boolean DEBUG_SETTINGS = false;
    static final boolean DEBUG_PREFERRED = false;
    static final boolean DEBUG_UPGRADE = false;
    private static final boolean DEBUG_INSTALL = false;
    private static final boolean DEBUG_REMOVE = false;
    private static final boolean DEBUG_BROADCASTS = false;
    private static final boolean DEBUG_SHOW_INFO = false;
    private static final boolean DEBUG_PACKAGE_INFO = false;
    private static final boolean DEBUG_INTENT_MATCHING = false;
    private static final boolean DEBUG_PACKAGE_SCANNING = false;
    private static final boolean DEBUG_APP_DIR_OBSERVER = false;
    private static final boolean DEBUG_VERIFY = false;
    private static final int RADIO_UID = 1001;
    private static final int LOG_UID = 1007;
    private static final int NFC_UID = 1027;
    private static final int BLUETOOTH_UID = 1002;
    private static final int SHELL_UID = 2000;
    private static final boolean GET_CERTIFICATES = true;
    private static final int REMOVE_EVENTS = 584;
    private static final int ADD_EVENTS = 136;
    private static final int OBSERVER_EVENTS = 712;
    private static final String INSTALL_PACKAGE_SUFFIX = "-";
    static final int SCAN_MONITOR = 1;
    static final int SCAN_NO_DEX = 2;
    static final int SCAN_FORCE_DEX = 4;
    static final int SCAN_UPDATE_SIGNATURE = 8;
    static final int SCAN_NEW_INSTALL = 16;
    static final int SCAN_NO_PATHS = 32;
    static final int SCAN_UPDATE_TIME = 64;
    static final int SCAN_DEFER_DEX = 128;
    static final int SCAN_BOOTING = 256;
    static final int REMOVE_CHATTY = 65536;
    private static final boolean DEFAULT_VERIFY_ENABLE = true;
    private static final long DEFAULT_VERIFICATION_TIMEOUT = 10000;
    private static final int DEFAULT_VERIFICATION_RESPONSE = 1;
    private static final String PACKAGE_MIME_TYPE = "application/vnd.android.package-archive";
    private static final String LIB_DIR_NAME = "lib";
    static final String mTempContainerPrefix = "smdl2tmp";
    final PackageHandler mHandler;
    final String mSdkCodename;
    final Context mContext;
    final boolean mFactoryTest;
    final boolean mOnlyCore;
    final boolean mNoDexOpt;
    final DisplayMetrics mMetrics;
    final int mDefParseFlags;
    final String[] mSeparateProcesses;
    final File mAppDataDir;
    final File mUserAppDataDir;
    final String mAsecInternalPath;
    final FileObserver mFrameworkInstallObserver;
    final FileObserver mSystemInstallObserver;
    final FileObserver mPrivilegedInstallObserver;
    final FileObserver mVendorInstallObserver;
    final FileObserver mAppInstallObserver;
    final FileObserver mDrmAppInstallObserver;
    final Installer mInstaller;
    final File mAppInstallDir;
    private File mAppLibInstallDir;
    final File mDrmAppPrivateInstallDir;
    final Object mInstallLock;
    final HashMap<String, PackageParser.Package> mAppDirs;
    File mScanningPath;
    int mLastScanError;
    final HashMap<String, PackageParser.Package> mPackages;
    final Settings mSettings;
    boolean mRestoredSettings;
    int[] mGlobalGids;
    final SparseArray<HashSet<String>> mSystemPermissions;
    final HashMap<String, SharedLibraryEntry> mSharedLibraries;
    String[] mTmpSharedLibraries;
    final HashMap<String, FeatureInfo> mAvailableFeatures;
    boolean mFoundPolicyFile;
    final ActivityIntentResolver mActivities;
    final ActivityIntentResolver mReceivers;
    final ServiceIntentResolver mServices;
    final ProviderIntentResolver mProviders;
    final HashMap<String, PackageParser.Provider> mProvidersByAuthority;
    final HashMap<ComponentName, PackageParser.Instrumentation> mInstrumentation;
    final HashMap<String, PackageParser.PermissionGroup> mPermissionGroups;
    final HashSet<String> mTransferedPackages;
    final HashSet<String> mProtectedBroadcasts;
    final SparseArray<PackageVerificationState> mPendingVerification;
    HashSet<PackageParser.Package> mDeferredDexOpt;
    private int mPendingVerificationToken;
    boolean mSystemReady;
    boolean mSafeMode;
    boolean mHasSystemUidErrors;
    ApplicationInfo mAndroidApplication;
    final ActivityInfo mResolveActivity;
    final ResolveInfo mResolveInfo;
    ComponentName mResolveComponentName;
    PackageParser.Package mPlatformPackage;
    ComponentName mCustomResolverComponentName;
    boolean mResolverReplaced;
    final PendingPackageBroadcasts mPendingBroadcasts;
    private IMediaContainerService mContainerService;
    static final int SEND_PENDING_BROADCAST = 1;
    static final int MCS_BOUND = 3;
    static final int END_COPY = 4;
    static final int INIT_COPY = 5;
    static final int MCS_UNBIND = 6;
    static final int START_CLEANING_PACKAGE = 7;
    static final int FIND_INSTALL_LOC = 8;
    static final int POST_INSTALL = 9;
    static final int MCS_RECONNECT = 10;
    static final int MCS_GIVE_UP = 11;
    static final int UPDATED_MEDIA_STATUS = 12;
    static final int WRITE_SETTINGS = 13;
    static final int WRITE_PACKAGE_RESTRICTIONS = 14;
    static final int PACKAGE_VERIFIED = 15;
    static final int CHECK_PENDING_VERIFICATION = 16;
    static final int WRITE_SETTINGS_DELAY = 10000;
    static final int BROADCAST_DELAY = 10000;
    static UserManagerService sUserManager;
    private HashSet<Integer> mDirtyUsers;
    private final DefaultContainerConnection mDefContainerConn;
    final SparseArray<PostInstallData> mRunningInstalls;
    int mNextInstallToken;
    private final String mRequiredVerifierPackage;
    static final int DEX_OPT_SKIPPED = 0;
    static final int DEX_OPT_PERFORMED = 1;
    static final int DEX_OPT_DEFERRED = 2;
    static final int DEX_OPT_FAILED = -1;
    static final int UPDATE_PERMISSIONS_ALL = 1;
    static final int UPDATE_PERMISSIONS_REPLACE_PKG = 2;
    static final int UPDATE_PERMISSIONS_REPLACE_ALL = 4;
    static final boolean DEBUG_SD_INSTALL = false;
    private static final String SD_ENCRYPTION_KEYSTORE_NAME = "AppsOnSD";
    private static final String SD_ENCRYPTION_ALGORITHM = "AES";
    private boolean mMediaMounted;
    static final String DEFAULT_CONTAINER_PACKAGE = "com.android.defcontainer";
    static final ComponentName DEFAULT_CONTAINER_COMPONENT = new ComponentName(DEFAULT_CONTAINER_PACKAGE, "com.android.defcontainer.DefaultContainerService");
    private static final Comparator<ResolveInfo> mResolvePrioritySorter = new Comparator<ResolveInfo>() { // from class: com.android.server.pm.PackageManagerService.3
        @Override // java.util.Comparator
        public int compare(ResolveInfo r1, ResolveInfo r2) {
            int v1 = r1.priority;
            int v2 = r2.priority;
            if (v1 != v2) {
                return v1 > v2 ? -1 : 1;
            }
            int v12 = r1.preferredOrder;
            int v22 = r2.preferredOrder;
            if (v12 != v22) {
                return v12 > v22 ? -1 : 1;
            } else if (r1.isDefault != r2.isDefault) {
                return r1.isDefault ? -1 : 1;
            } else {
                int v13 = r1.match;
                int v23 = r2.match;
                if (v13 != v23) {
                    return v13 > v23 ? -1 : 1;
                } else if (r1.system != r2.system) {
                    return r1.system ? -1 : 1;
                } else {
                    return 0;
                }
            }
        }
    };
    private static final Comparator<ProviderInfo> mProviderInitOrderSorter = new Comparator<ProviderInfo>() { // from class: com.android.server.pm.PackageManagerService.4
        @Override // java.util.Comparator
        public int compare(ProviderInfo p1, ProviderInfo p2) {
            int v1 = p1.initOrder;
            int v2 = p2.initOrder;
            if (v1 > v2) {
                return -1;
            }
            return v1 < v2 ? 1 : 0;
        }
    };
    final HandlerThread mHandlerThread = new HandlerThread(TAG, 10);
    final int mSdkVersion = Build.VERSION.SDK_INT;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.revokePermission(java.lang.String, java.lang.String):void, file: PackageManagerService.class
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
    @Override // android.content.pm.IPackageManager
    public void revokePermission(java.lang.String r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.revokePermission(java.lang.String, java.lang.String):void, file: PackageManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.revokePermission(java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.setApplicationBlockedSettingAsUser(java.lang.String, boolean, int):boolean, file: PackageManagerService.class
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
    @Override // android.content.pm.IPackageManager
    public boolean setApplicationBlockedSettingAsUser(java.lang.String r1, boolean r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.setApplicationBlockedSettingAsUser(java.lang.String, boolean, int):boolean, file: PackageManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.setApplicationBlockedSettingAsUser(java.lang.String, boolean, int):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.getApplicationBlockedSettingAsUser(java.lang.String, int):boolean, file: PackageManagerService.class
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
    @Override // android.content.pm.IPackageManager
    public boolean getApplicationBlockedSettingAsUser(java.lang.String r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.getApplicationBlockedSettingAsUser(java.lang.String, int):boolean, file: PackageManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.getApplicationBlockedSettingAsUser(java.lang.String, int):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.installExistingPackageAsUser(java.lang.String, int):int, file: PackageManagerService.class
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
    @Override // android.content.pm.IPackageManager
    public int installExistingPackageAsUser(java.lang.String r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.installExistingPackageAsUser(java.lang.String, int):int, file: PackageManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.installExistingPackageAsUser(java.lang.String, int):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.clearExternalStorageDataSync(java.lang.String, int, boolean):void, file: PackageManagerService.class
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
    public void clearExternalStorageDataSync(java.lang.String r1, int r2, boolean r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.clearExternalStorageDataSync(java.lang.String, int, boolean):void, file: PackageManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.clearExternalStorageDataSync(java.lang.String, int, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.setEnabledSetting(java.lang.String, java.lang.String, int, int, int, java.lang.String):void, file: PackageManagerService.class
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
    private void setEnabledSetting(java.lang.String r1, java.lang.String r2, int r3, int r4, int r5, java.lang.String r6) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.setEnabledSetting(java.lang.String, java.lang.String, int, int, int, java.lang.String):void, file: PackageManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.setEnabledSetting(java.lang.String, java.lang.String, int, int, int, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: PackageManagerService.class
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
    protected void dump(java.io.FileDescriptor r1, java.io.PrintWriter r2, java.lang.String[] r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: PackageManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.loadMediaPackages(java.util.HashMap<com.android.server.pm.PackageManagerService$AsecInstallArgs, java.lang.String>, int[], java.util.HashSet<java.lang.String>):void, file: PackageManagerService.class
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
    private void loadMediaPackages(java.util.HashMap<com.android.server.pm.PackageManagerService.AsecInstallArgs, java.lang.String> r1, int[] r2, java.util.HashSet<java.lang.String> r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.loadMediaPackages(java.util.HashMap<com.android.server.pm.PackageManagerService$AsecInstallArgs, java.lang.String>, int[], java.util.HashSet<java.lang.String>):void, file: PackageManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.loadMediaPackages(java.util.HashMap, int[], java.util.HashSet):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.setPermissionEnforced(java.lang.String, boolean):void, file: PackageManagerService.class
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
    @Override // android.content.pm.IPackageManager
    public void setPermissionEnforced(java.lang.String r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.setPermissionEnforced(java.lang.String, boolean):void, file: PackageManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.setPermissionEnforced(java.lang.String, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.isStorageLow():boolean, file: PackageManagerService.class
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
    @Override // android.content.pm.IPackageManager
    public boolean isStorageLow() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.isStorageLow():boolean, file: PackageManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.isStorageLow():boolean");
    }

    static /* synthetic */ IMediaContainerService access$300(PackageManagerService x0) {
        return x0.mContainerService;
    }

    static /* synthetic */ File access$2600(PackageManagerService x0, File x1) {
        return x0.createTempPackageFile(x1);
    }

    static /* synthetic */ int access$3008(PackageManagerService x0) {
        int i = x0.mPendingVerificationToken;
        x0.mPendingVerificationToken = i + 1;
        return i;
    }

    static /* synthetic */ String access$4000(PackageManagerService x0) {
        return x0.getEncryptKey();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$SharedLibraryEntry.class */
    public static final class SharedLibraryEntry {
        final String path;
        final String apk;

        SharedLibraryEntry(String _path, String _apk) {
            this.path = _path;
            this.apk = _apk;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$PendingPackageBroadcasts.class */
    public static class PendingPackageBroadcasts {
        final SparseArray<HashMap<String, ArrayList<String>>> mUidMap = new SparseArray<>(2);

        public ArrayList<String> get(int userId, String packageName) {
            HashMap<String, ArrayList<String>> packages = getOrAllocate(userId);
            return packages.get(packageName);
        }

        public void put(int userId, String packageName, ArrayList<String> components) {
            HashMap<String, ArrayList<String>> packages = getOrAllocate(userId);
            packages.put(packageName, components);
        }

        public void remove(int userId, String packageName) {
            HashMap<String, ArrayList<String>> packages = this.mUidMap.get(userId);
            if (packages != null) {
                packages.remove(packageName);
            }
        }

        public void remove(int userId) {
            this.mUidMap.remove(userId);
        }

        public int userIdCount() {
            return this.mUidMap.size();
        }

        public int userIdAt(int n) {
            return this.mUidMap.keyAt(n);
        }

        public HashMap<String, ArrayList<String>> packagesForUserId(int userId) {
            return this.mUidMap.get(userId);
        }

        public int size() {
            int num = 0;
            for (int i = 0; i < this.mUidMap.size(); i++) {
                num += this.mUidMap.valueAt(i).size();
            }
            return num;
        }

        public void clear() {
            this.mUidMap.clear();
        }

        private HashMap<String, ArrayList<String>> getOrAllocate(int userId) {
            HashMap<String, ArrayList<String>> map = this.mUidMap.get(userId);
            if (map == null) {
                map = new HashMap<>();
                this.mUidMap.put(userId, map);
            }
            return map;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$DefaultContainerConnection.class */
    public class DefaultContainerConnection implements ServiceConnection {
        DefaultContainerConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMediaContainerService imcs = IMediaContainerService.Stub.asInterface(service);
            PackageManagerService.this.mHandler.sendMessage(PackageManagerService.this.mHandler.obtainMessage(3, imcs));
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    /* loaded from: PackageManagerService$PostInstallData.class */
    class PostInstallData {
        public InstallArgs args;
        public PackageInstalledInfo res;

        PostInstallData(InstallArgs _a, PackageInstalledInfo _r) {
            this.args = _a;
            this.res = _r;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$PackageHandler.class */
    public class PackageHandler extends Handler {
        private boolean mBound;
        final ArrayList<HandlerParams> mPendingInstalls;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.PackageHandler.handleMessage(android.os.Message):void, file: PackageManagerService$PackageHandler.class
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
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.PackageHandler.handleMessage(android.os.Message):void, file: PackageManagerService$PackageHandler.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.PackageHandler.handleMessage(android.os.Message):void");
        }

        private boolean connectToService() {
            Intent service = new Intent().setComponent(PackageManagerService.DEFAULT_CONTAINER_COMPONENT);
            Process.setThreadPriority(0);
            if (PackageManagerService.this.mContext.bindServiceAsUser(service, PackageManagerService.this.mDefContainerConn, 1, UserHandle.OWNER)) {
                Process.setThreadPriority(10);
                this.mBound = true;
                return true;
            }
            Process.setThreadPriority(10);
            return false;
        }

        private void disconnectService() {
            PackageManagerService.this.mContainerService = null;
            this.mBound = false;
            Process.setThreadPriority(0);
            PackageManagerService.this.mContext.unbindService(PackageManagerService.this.mDefContainerConn);
            Process.setThreadPriority(10);
        }

        PackageHandler(Looper looper) {
            super(looper);
            this.mBound = false;
            this.mPendingInstalls = new ArrayList<>();
        }

        void doHandleMessage(Message msg) {
            int ret;
            int[] firstUsers;
            switch (msg.what) {
                case 1:
                    Process.setThreadPriority(0);
                    synchronized (PackageManagerService.this.mPackages) {
                        if (PackageManagerService.this.mPendingBroadcasts == null) {
                            return;
                        }
                        int size = PackageManagerService.this.mPendingBroadcasts.size();
                        if (size <= 0) {
                            return;
                        }
                        String[] packages = new String[size];
                        ArrayList<String>[] components = new ArrayList[size];
                        int[] uids = new int[size];
                        int i = 0;
                        for (int n = 0; n < PackageManagerService.this.mPendingBroadcasts.userIdCount(); n++) {
                            int packageUserId = PackageManagerService.this.mPendingBroadcasts.userIdAt(n);
                            Iterator<Map.Entry<String, ArrayList<String>>> it = PackageManagerService.this.mPendingBroadcasts.packagesForUserId(packageUserId).entrySet().iterator();
                            while (it.hasNext() && i < size) {
                                Map.Entry<String, ArrayList<String>> ent = it.next();
                                packages[i] = ent.getKey();
                                components[i] = ent.getValue();
                                PackageSetting ps = PackageManagerService.this.mSettings.mPackages.get(ent.getKey());
                                uids[i] = ps != null ? UserHandle.getUid(packageUserId, ps.appId) : -1;
                                i++;
                            }
                        }
                        int size2 = i;
                        PackageManagerService.this.mPendingBroadcasts.clear();
                        for (int i2 = 0; i2 < size2; i2++) {
                            PackageManagerService.this.sendPackageChangedBroadcast(packages[i2], true, components[i2], uids[i2]);
                        }
                        Process.setThreadPriority(10);
                        return;
                    }
                case 2:
                case 4:
                case 8:
                default:
                    return;
                case 3:
                    if (msg.obj != null) {
                        PackageManagerService.this.mContainerService = (IMediaContainerService) msg.obj;
                    }
                    if (PackageManagerService.this.mContainerService == null) {
                        Slog.e(PackageManagerService.TAG, "Cannot bind to media container service");
                        Iterator i$ = this.mPendingInstalls.iterator();
                        while (i$.hasNext()) {
                            i$.next().serviceError();
                        }
                        this.mPendingInstalls.clear();
                        return;
                    } else if (this.mPendingInstalls.size() > 0) {
                        HandlerParams params = this.mPendingInstalls.get(0);
                        if (params != null && params.startCopy()) {
                            if (this.mPendingInstalls.size() > 0) {
                                this.mPendingInstalls.remove(0);
                            }
                            if (this.mPendingInstalls.size() == 0) {
                                if (this.mBound) {
                                    removeMessages(6);
                                    Message ubmsg = obtainMessage(6);
                                    sendMessageDelayed(ubmsg, PackageManagerService.DEFAULT_VERIFICATION_TIMEOUT);
                                    return;
                                }
                                return;
                            }
                            PackageManagerService.this.mHandler.sendEmptyMessage(3);
                            return;
                        }
                        return;
                    } else {
                        Slog.w(PackageManagerService.TAG, "Empty queue");
                        return;
                    }
                case 5:
                    HandlerParams params2 = (HandlerParams) msg.obj;
                    int idx = this.mPendingInstalls.size();
                    if (!this.mBound) {
                        if (!connectToService()) {
                            Slog.e(PackageManagerService.TAG, "Failed to bind to media container service");
                            params2.serviceError();
                            return;
                        }
                        this.mPendingInstalls.add(idx, params2);
                        return;
                    }
                    this.mPendingInstalls.add(idx, params2);
                    if (idx == 0) {
                        PackageManagerService.this.mHandler.sendEmptyMessage(3);
                        return;
                    }
                    return;
                case 6:
                    if (this.mPendingInstalls.size() == 0 && PackageManagerService.this.mPendingVerification.size() == 0) {
                        if (this.mBound) {
                            disconnectService();
                            return;
                        }
                        return;
                    } else if (this.mPendingInstalls.size() > 0) {
                        PackageManagerService.this.mHandler.sendEmptyMessage(3);
                        return;
                    } else {
                        return;
                    }
                case 7:
                    Process.setThreadPriority(0);
                    String packageName = (String) msg.obj;
                    int userId = msg.arg1;
                    boolean andCode = msg.arg2 != 0;
                    synchronized (PackageManagerService.this.mPackages) {
                        if (userId == -1) {
                            int[] users = PackageManagerService.sUserManager.getUserIds();
                            for (int user : users) {
                                PackageManagerService.this.mSettings.addPackageToCleanLPw(new PackageCleanItem(user, packageName, andCode));
                            }
                        } else {
                            PackageManagerService.this.mSettings.addPackageToCleanLPw(new PackageCleanItem(userId, packageName, andCode));
                        }
                    }
                    Process.setThreadPriority(10);
                    PackageManagerService.this.startCleaningPackages();
                    return;
                case 9:
                    PostInstallData data = PackageManagerService.this.mRunningInstalls.get(msg.arg1);
                    PackageManagerService.this.mRunningInstalls.delete(msg.arg1);
                    boolean deleteOld = false;
                    if (data != null) {
                        InstallArgs args = data.args;
                        PackageInstalledInfo res = data.res;
                        if (res.returnCode == 1) {
                            res.removedInfo.sendBroadcast(false, true, false);
                            Bundle extras = new Bundle(1);
                            extras.putInt(Intent.EXTRA_UID, res.uid);
                            int[] updateUsers = new int[0];
                            if (res.origUsers == null || res.origUsers.length == 0) {
                                firstUsers = res.newUsers;
                            } else {
                                firstUsers = new int[0];
                                for (int i3 = 0; i3 < res.newUsers.length; i3++) {
                                    int user2 = res.newUsers[i3];
                                    boolean isNew = true;
                                    int j = 0;
                                    while (true) {
                                        if (j < res.origUsers.length) {
                                            if (res.origUsers[j] != user2) {
                                                j++;
                                            } else {
                                                isNew = false;
                                            }
                                        }
                                    }
                                    if (isNew) {
                                        int[] newFirst = new int[firstUsers.length + 1];
                                        System.arraycopy(firstUsers, 0, newFirst, 0, firstUsers.length);
                                        newFirst[firstUsers.length] = user2;
                                        firstUsers = newFirst;
                                    } else {
                                        int[] newUpdate = new int[updateUsers.length + 1];
                                        System.arraycopy(updateUsers, 0, newUpdate, 0, updateUsers.length);
                                        newUpdate[updateUsers.length] = user2;
                                        updateUsers = newUpdate;
                                    }
                                }
                            }
                            PackageManagerService.sendPackageBroadcast(Intent.ACTION_PACKAGE_ADDED, res.pkg.applicationInfo.packageName, extras, null, null, firstUsers);
                            boolean update = res.removedInfo.removedPackage != null;
                            if (update) {
                                extras.putBoolean(Intent.EXTRA_REPLACING, true);
                            }
                            PackageManagerService.sendPackageBroadcast(Intent.ACTION_PACKAGE_ADDED, res.pkg.applicationInfo.packageName, extras, null, null, updateUsers);
                            if (update) {
                                PackageManagerService.sendPackageBroadcast(Intent.ACTION_PACKAGE_REPLACED, res.pkg.applicationInfo.packageName, extras, null, null, updateUsers);
                                PackageManagerService.sendPackageBroadcast(Intent.ACTION_MY_PACKAGE_REPLACED, null, null, res.pkg.applicationInfo.packageName, null, updateUsers);
                                if (PackageManagerService.isForwardLocked(res.pkg) || PackageManagerService.isExternal(res.pkg)) {
                                    int[] uidArray = {res.pkg.applicationInfo.uid};
                                    ArrayList<String> pkgList = new ArrayList<>(1);
                                    pkgList.add(res.pkg.applicationInfo.packageName);
                                    PackageManagerService.this.sendResourcesChangedBroadcast(true, false, pkgList, uidArray, null);
                                }
                            }
                            if (res.removedInfo.args != null) {
                                deleteOld = true;
                            }
                            EventLog.writeEvent((int) EventLogTags.UNKNOWN_SOURCES_ENABLED, PackageManagerService.this.getUnknownSourcesSettings());
                        }
                        Runtime.getRuntime().gc();
                        if (deleteOld) {
                            synchronized (PackageManagerService.this.mInstallLock) {
                                res.removedInfo.args.doPostDeleteLI(true);
                            }
                        }
                        if (args.observer != null) {
                            try {
                                args.observer.packageInstalled(res.name, res.returnCode);
                                return;
                            } catch (RemoteException e) {
                                Slog.i(PackageManagerService.TAG, "Observer no longer exists.");
                                return;
                            }
                        }
                        return;
                    }
                    Slog.e(PackageManagerService.TAG, "Bogus post-install token " + msg.arg1);
                    return;
                case 10:
                    if (this.mPendingInstalls.size() > 0) {
                        if (this.mBound) {
                            disconnectService();
                        }
                        if (!connectToService()) {
                            Slog.e(PackageManagerService.TAG, "Failed to bind to media container service");
                            Iterator i$2 = this.mPendingInstalls.iterator();
                            while (i$2.hasNext()) {
                                i$2.next().serviceError();
                            }
                            this.mPendingInstalls.clear();
                            return;
                        }
                        return;
                    }
                    return;
                case 11:
                    this.mPendingInstalls.remove(0);
                    return;
                case 12:
                    boolean reportStatus = msg.arg1 == 1;
                    boolean doGc = msg.arg2 == 1;
                    if (doGc) {
                        Runtime.getRuntime().gc();
                    }
                    if (msg.obj != null) {
                        PackageManagerService.this.unloadAllContainers((Set) msg.obj);
                    }
                    if (reportStatus) {
                        try {
                            PackageHelper.getMountService().finishMediaUpdate();
                            return;
                        } catch (RemoteException e2) {
                            Log.e(PackageManagerService.TAG, "MountService not running?");
                            return;
                        }
                    }
                    return;
                case 13:
                    Process.setThreadPriority(0);
                    synchronized (PackageManagerService.this.mPackages) {
                        removeMessages(13);
                        removeMessages(14);
                        PackageManagerService.this.mSettings.writeLPr();
                        PackageManagerService.this.mDirtyUsers.clear();
                    }
                    Process.setThreadPriority(10);
                    return;
                case 14:
                    Process.setThreadPriority(0);
                    synchronized (PackageManagerService.this.mPackages) {
                        removeMessages(14);
                        Iterator i$3 = PackageManagerService.this.mDirtyUsers.iterator();
                        while (i$3.hasNext()) {
                            PackageManagerService.this.mSettings.writePackageRestrictionsLPr(((Integer) i$3.next()).intValue());
                        }
                        PackageManagerService.this.mDirtyUsers.clear();
                    }
                    Process.setThreadPriority(10);
                    return;
                case 15:
                    int verificationId = msg.arg1;
                    PackageVerificationState state = PackageManagerService.this.mPendingVerification.get(verificationId);
                    if (state == null) {
                        Slog.w(PackageManagerService.TAG, "Invalid verification token " + verificationId + " received");
                        return;
                    }
                    PackageVerificationResponse response = (PackageVerificationResponse) msg.obj;
                    state.setVerifierResponse(response.callerUid, response.code);
                    if (state.isVerificationComplete()) {
                        PackageManagerService.this.mPendingVerification.remove(verificationId);
                        InstallArgs args2 = state.getInstallArgs();
                        if (state.isInstallAllowed()) {
                            ret = -110;
                            PackageManagerService.this.broadcastPackageVerified(verificationId, args2.packageURI, response.code, state.getInstallArgs().getUser());
                            try {
                                ret = args2.copyApk(PackageManagerService.this.mContainerService, true);
                            } catch (RemoteException e3) {
                                Slog.e(PackageManagerService.TAG, "Could not contact the ContainerService");
                            }
                        } else {
                            ret = -22;
                        }
                        PackageManagerService.this.processPendingInstall(args2, ret);
                        PackageManagerService.this.mHandler.sendEmptyMessage(6);
                        return;
                    }
                    return;
                case 16:
                    int verificationId2 = msg.arg1;
                    PackageVerificationState state2 = PackageManagerService.this.mPendingVerification.get(verificationId2);
                    if (state2 != null && !state2.timeoutExtended()) {
                        InstallArgs args3 = state2.getInstallArgs();
                        Slog.i(PackageManagerService.TAG, "Verification timed out for " + args3.packageURI.toString());
                        PackageManagerService.this.mPendingVerification.remove(verificationId2);
                        int ret2 = -22;
                        if (PackageManagerService.this.getDefaultVerificationResponse() != 1) {
                            PackageManagerService.this.broadcastPackageVerified(verificationId2, args3.packageURI, -1, state2.getInstallArgs().getUser());
                        } else {
                            Slog.i(PackageManagerService.TAG, "Continuing with installation of " + args3.packageURI.toString());
                            state2.setVerifierResponse(Binder.getCallingUid(), 2);
                            PackageManagerService.this.broadcastPackageVerified(verificationId2, args3.packageURI, 1, state2.getInstallArgs().getUser());
                            try {
                                ret2 = args3.copyApk(PackageManagerService.this.mContainerService, true);
                            } catch (RemoteException e4) {
                                Slog.e(PackageManagerService.TAG, "Could not contact the ContainerService");
                            }
                        }
                        PackageManagerService.this.processPendingInstall(args3, ret2);
                        PackageManagerService.this.mHandler.sendEmptyMessage(6);
                        return;
                    }
                    return;
            }
        }
    }

    void scheduleWriteSettingsLocked() {
        if (!this.mHandler.hasMessages(13)) {
            this.mHandler.sendEmptyMessageDelayed(13, DEFAULT_VERIFICATION_TIMEOUT);
        }
    }

    void scheduleWritePackageRestrictionsLocked(int userId) {
        if (sUserManager.exists(userId)) {
            this.mDirtyUsers.add(Integer.valueOf(userId));
            if (!this.mHandler.hasMessages(14)) {
                this.mHandler.sendEmptyMessageDelayed(14, DEFAULT_VERIFICATION_TIMEOUT);
            }
        }
    }

    public static final IPackageManager main(Context context, Installer installer, boolean factoryTest, boolean onlyCore) {
        PackageManagerService m = new PackageManagerService(context, installer, factoryTest, onlyCore);
        ServiceManager.addService(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, m);
        return m;
    }

    static String[] splitString(String str, char sep) {
        int count = 1;
        int i = 0;
        while (true) {
            int i2 = str.indexOf(sep, i);
            if (i2 < 0) {
                break;
            }
            count++;
            i = i2 + 1;
        }
        String[] res = new String[count];
        int i3 = 0;
        int count2 = 0;
        int i4 = 0;
        while (true) {
            int lastI = i4;
            int i5 = str.indexOf(sep, i3);
            if (i5 >= 0) {
                res[count2] = str.substring(lastI, i5);
                count2++;
                i3 = i5 + 1;
                i4 = i3;
            } else {
                res[count2] = str.substring(lastI, str.length());
                return res;
            }
        }
    }

    public PackageManagerService(Context context, Installer installer, boolean factoryTest, boolean onlyCore) {
        String msg;
        File dalvikCacheDir;
        String[] files;
        this.mSdkCodename = "REL".equals(Build.VERSION.CODENAME) ? null : Build.VERSION.CODENAME;
        this.mInstallLock = new Object();
        this.mAppDirs = new HashMap<>();
        this.mPackages = new HashMap<>();
        this.mSystemPermissions = new SparseArray<>();
        this.mSharedLibraries = new HashMap<>();
        this.mTmpSharedLibraries = null;
        this.mAvailableFeatures = new HashMap<>();
        this.mActivities = new ActivityIntentResolver();
        this.mReceivers = new ActivityIntentResolver();
        this.mServices = new ServiceIntentResolver();
        this.mProviders = new ProviderIntentResolver();
        this.mProvidersByAuthority = new HashMap<>();
        this.mInstrumentation = new HashMap<>();
        this.mPermissionGroups = new HashMap<>();
        this.mTransferedPackages = new HashSet<>();
        this.mProtectedBroadcasts = new HashSet<>();
        this.mPendingVerification = new SparseArray<>();
        this.mDeferredDexOpt = null;
        this.mPendingVerificationToken = 0;
        this.mResolveActivity = new ActivityInfo();
        this.mResolveInfo = new ResolveInfo();
        this.mResolverReplaced = false;
        this.mPendingBroadcasts = new PendingPackageBroadcasts();
        this.mContainerService = null;
        this.mDirtyUsers = new HashSet<>();
        this.mDefContainerConn = new DefaultContainerConnection();
        this.mRunningInstalls = new SparseArray<>();
        this.mNextInstallToken = 1;
        this.mMediaMounted = false;
        EventLog.writeEvent((int) EventLogTags.BOOT_PROGRESS_PMS_START, SystemClock.uptimeMillis());
        if (this.mSdkVersion <= 0) {
            Slog.w(TAG, "**** ro.build.version.sdk not set!");
        }
        this.mContext = context;
        this.mFactoryTest = factoryTest;
        this.mOnlyCore = onlyCore;
        this.mNoDexOpt = "eng".equals(SystemProperties.get("ro.build.type"));
        this.mMetrics = new DisplayMetrics();
        this.mSettings = new Settings(context);
        this.mSettings.addSharedUserLPw("android.uid.system", 1000, 1073741825);
        this.mSettings.addSharedUserLPw("android.uid.phone", 1001, 1073741825);
        this.mSettings.addSharedUserLPw("android.uid.log", 1007, 1073741825);
        this.mSettings.addSharedUserLPw("android.uid.nfc", 1027, 1073741825);
        this.mSettings.addSharedUserLPw("android.uid.bluetooth", 1002, 1073741825);
        this.mSettings.addSharedUserLPw("android.uid.shell", 2000, 1073741825);
        String separateProcesses = SystemProperties.get("debug.separate_processes");
        if (separateProcesses != null && separateProcesses.length() > 0) {
            if ("*".equals(separateProcesses)) {
                this.mDefParseFlags = 8;
                this.mSeparateProcesses = null;
                Slog.w(TAG, "Running with debug.separate_processes: * (ALL)");
            } else {
                this.mDefParseFlags = 0;
                this.mSeparateProcesses = separateProcesses.split(Separators.COMMA);
                Slog.w(TAG, "Running with debug.separate_processes: " + separateProcesses);
            }
        } else {
            this.mDefParseFlags = 0;
            this.mSeparateProcesses = null;
        }
        this.mInstaller = installer;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        d.getMetrics(this.mMetrics);
        synchronized (this.mInstallLock) {
            synchronized (this.mPackages) {
                this.mHandlerThread.start();
                this.mHandler = new PackageHandler(this.mHandlerThread.getLooper());
                Watchdog.getInstance().addThread(this.mHandler, this.mHandlerThread.getName());
                File dataDir = Environment.getDataDirectory();
                this.mAppDataDir = new File(dataDir, "data");
                this.mAppInstallDir = new File(dataDir, "app");
                this.mAppLibInstallDir = new File(dataDir, "app-lib");
                this.mAsecInternalPath = new File(dataDir, "app-asec").getPath();
                this.mUserAppDataDir = new File(dataDir, "user");
                this.mDrmAppPrivateInstallDir = new File(dataDir, "app-private");
                sUserManager = new UserManagerService(context, this, this.mInstallLock, this.mPackages);
                readPermissions();
                this.mFoundPolicyFile = SELinuxMMAC.readInstallPolicy();
                this.mRestoredSettings = this.mSettings.readLPw(this, sUserManager.getUsers(false), this.mSdkVersion, this.mOnlyCore);
                String customResolverActivity = Resources.getSystem().getString(R.string.config_customResolverActivity);
                if (!TextUtils.isEmpty(customResolverActivity)) {
                    this.mCustomResolverComponentName = ComponentName.unflattenFromString(customResolverActivity);
                }
                long startTime = SystemClock.uptimeMillis();
                EventLog.writeEvent((int) EventLogTags.BOOT_PROGRESS_PMS_SYSTEM_SCAN_START, startTime);
                int scanMode = 417;
                if (this.mNoDexOpt) {
                    Slog.w(TAG, "Running ENG build: no pre-dexopt!");
                    scanMode = 417 | 2;
                }
                HashSet<String> alreadyDexOpted = new HashSet<>();
                String bootClassPath = System.getProperty("java.boot.class.path");
                if (bootClassPath != null) {
                    String[] paths = splitString(bootClassPath, ':');
                    for (String str : paths) {
                        alreadyDexOpted.add(str);
                    }
                } else {
                    Slog.w(TAG, "No BOOTCLASSPATH found!");
                }
                boolean didDexOpt = false;
                if (this.mSharedLibraries.size() > 0) {
                    for (SharedLibraryEntry sharedLibraryEntry : this.mSharedLibraries.values()) {
                        String lib = sharedLibraryEntry.path;
                        if (lib != null) {
                            try {
                                if (DexFile.isDexOptNeeded(lib)) {
                                    alreadyDexOpted.add(lib);
                                    this.mInstaller.dexopt(lib, 1000, true);
                                    didDexOpt = true;
                                }
                            } catch (FileNotFoundException e) {
                                Slog.w(TAG, "Library not found: " + lib);
                            } catch (IOException e2) {
                                Slog.w(TAG, "Cannot dexopt " + lib + "; is it an APK or JAR? " + e2.getMessage());
                            }
                        }
                    }
                }
                File frameworkDir = new File(Environment.getRootDirectory(), "framework");
                alreadyDexOpted.add(frameworkDir.getPath() + "/framework-res.apk");
                alreadyDexOpted.add(frameworkDir.getPath() + "/core-libart.jar");
                String[] frameworkFiles = frameworkDir.list();
                if (frameworkFiles != null) {
                    for (String str2 : frameworkFiles) {
                        File libPath = new File(frameworkDir, str2);
                        String path = libPath.getPath();
                        if (!alreadyDexOpted.contains(path) && (path.endsWith(".apk") || path.endsWith(".jar"))) {
                            try {
                                if (DexFile.isDexOptNeeded(path)) {
                                    this.mInstaller.dexopt(path, 1000, true);
                                    didDexOpt = true;
                                }
                            } catch (FileNotFoundException e3) {
                                Slog.w(TAG, "Jar not found: " + path);
                            } catch (IOException e4) {
                                Slog.w(TAG, "Exception reading jar: " + path, e4);
                            }
                        }
                    }
                }
                if (didDexOpt && (files = (dalvikCacheDir = new File(dataDir, "dalvik-cache")).list()) != null) {
                    for (String fn : files) {
                        if (fn.startsWith("data@app@") || fn.startsWith("data@app-private@")) {
                            Slog.i(TAG, "Pruning dalvik file: " + fn);
                            new File(dalvikCacheDir, fn).delete();
                        }
                    }
                }
                this.mFrameworkInstallObserver = new AppDirObserver(frameworkDir.getPath(), OBSERVER_EVENTS, true, false);
                this.mFrameworkInstallObserver.startWatching();
                scanDirLI(frameworkDir, 65, scanMode | 2, 0L);
                File privilegedAppDir = new File(Environment.getRootDirectory(), "priv-app");
                this.mPrivilegedInstallObserver = new AppDirObserver(privilegedAppDir.getPath(), OBSERVER_EVENTS, true, true);
                this.mPrivilegedInstallObserver.startWatching();
                scanDirLI(privilegedAppDir, 193, scanMode, 0L);
                File systemAppDir = new File(Environment.getRootDirectory(), "app");
                this.mSystemInstallObserver = new AppDirObserver(systemAppDir.getPath(), OBSERVER_EVENTS, true, false);
                this.mSystemInstallObserver.startWatching();
                scanDirLI(systemAppDir, 65, scanMode, 0L);
                File vendorAppDir = new File("/vendor/app");
                this.mVendorInstallObserver = new AppDirObserver(vendorAppDir.getPath(), OBSERVER_EVENTS, true, false);
                this.mVendorInstallObserver.startWatching();
                scanDirLI(vendorAppDir, 65, scanMode, 0L);
                this.mInstaller.moveFiles();
                List<String> possiblyDeletedUpdatedSystemApps = new ArrayList<>();
                if (!this.mOnlyCore) {
                    Iterator<PackageSetting> psit = this.mSettings.mPackages.values().iterator();
                    while (psit.hasNext()) {
                        PackageSetting ps = psit.next();
                        if ((ps.pkgFlags & 1) != 0) {
                            PackageParser.Package scannedPkg = this.mPackages.get(ps.name);
                            if (scannedPkg != null) {
                                if (this.mSettings.isDisabledSystemPackageLPr(ps.name)) {
                                    Slog.i(TAG, "Expecting better updatd system app for " + ps.name + "; removing system app");
                                    removePackageLI(ps, true);
                                }
                            } else if (!this.mSettings.isDisabledSystemPackageLPr(ps.name)) {
                                psit.remove();
                                String msg2 = "System package " + ps.name + " no longer exists; wiping its data";
                                reportSettingsProblem(5, msg2);
                                removeDataDirsLI(ps.name);
                            } else {
                                PackageSetting disabledPs = this.mSettings.getDisabledSystemPkgLPr(ps.name);
                                if (disabledPs.codePath == null || !disabledPs.codePath.exists()) {
                                    possiblyDeletedUpdatedSystemApps.add(ps.name);
                                }
                            }
                        }
                    }
                }
                ArrayList<PackageSetting> deletePkgsList = this.mSettings.getListOfIncompleteInstallPackagesLPr();
                for (int i = 0; i < deletePkgsList.size(); i++) {
                    cleanupInstallFailedPackage(deletePkgsList.get(i));
                }
                deleteTempPackageFiles();
                this.mSettings.pruneSharedUsersLPw();
                if (!this.mOnlyCore) {
                    EventLog.writeEvent((int) EventLogTags.BOOT_PROGRESS_PMS_DATA_SCAN_START, SystemClock.uptimeMillis());
                    this.mAppInstallObserver = new AppDirObserver(this.mAppInstallDir.getPath(), OBSERVER_EVENTS, false, false);
                    this.mAppInstallObserver.startWatching();
                    scanDirLI(this.mAppInstallDir, 0, scanMode, 0L);
                    this.mDrmAppInstallObserver = new AppDirObserver(this.mDrmAppPrivateInstallDir.getPath(), OBSERVER_EVENTS, false, false);
                    this.mDrmAppInstallObserver.startWatching();
                    scanDirLI(this.mDrmAppPrivateInstallDir, 16, scanMode, 0L);
                    for (String deletedAppName : possiblyDeletedUpdatedSystemApps) {
                        PackageParser.Package deletedPkg = this.mPackages.get(deletedAppName);
                        this.mSettings.removeDisabledSystemPackageLPw(deletedAppName);
                        if (deletedPkg == null) {
                            msg = "Updated system package " + deletedAppName + " no longer exists; wiping its data";
                            removeDataDirsLI(deletedAppName);
                        } else {
                            msg = "Updated system app + " + deletedAppName + " no longer present; removing system privileges for " + deletedAppName;
                            deletedPkg.applicationInfo.flags &= -2;
                            PackageSetting deletedPs = this.mSettings.mPackages.get(deletedAppName);
                            deletedPs.pkgFlags &= -2;
                        }
                        reportSettingsProblem(5, msg);
                    }
                } else {
                    this.mAppInstallObserver = null;
                    this.mDrmAppInstallObserver = null;
                }
                updateAllSharedLibrariesLPw();
                EventLog.writeEvent((int) EventLogTags.BOOT_PROGRESS_PMS_SCAN_END, SystemClock.uptimeMillis());
                Slog.i(TAG, "Time to scan packages: " + (((float) (SystemClock.uptimeMillis() - startTime)) / 1000.0f) + " seconds");
                boolean regrantPermissions = this.mSettings.mInternalSdkPlatform != this.mSdkVersion;
                if (regrantPermissions) {
                    Slog.i(TAG, "Platform changed from " + this.mSettings.mInternalSdkPlatform + " to " + this.mSdkVersion + "; regranting permissions for internal storage");
                }
                this.mSettings.mInternalSdkPlatform = this.mSdkVersion;
                updatePermissionsLPw(null, null, 1 | (regrantPermissions ? 6 : 0));
                if (!this.mRestoredSettings && !onlyCore) {
                    this.mSettings.readDefaultPreferredAppsLPw(this, 0);
                }
                this.mSettings.writeLPr();
                EventLog.writeEvent((int) EventLogTags.BOOT_PROGRESS_PMS_READY, SystemClock.uptimeMillis());
                Runtime.getRuntime().gc();
                this.mRequiredVerifierPackage = getRequiredVerifierLPr();
            }
        }
    }

    @Override // android.content.pm.IPackageManager
    public boolean isFirstBoot() {
        return !this.mRestoredSettings;
    }

    @Override // android.content.pm.IPackageManager
    public boolean isOnlyCoreApps() {
        return this.mOnlyCore;
    }

    private String getRequiredVerifierLPr() {
        Intent verification = new Intent(Intent.ACTION_PACKAGE_NEEDS_VERIFICATION);
        List<ResolveInfo> receivers = queryIntentReceivers(verification, PACKAGE_MIME_TYPE, 512, 0);
        String requiredVerifier = null;
        int N = receivers.size();
        for (int i = 0; i < N; i++) {
            ResolveInfo info = receivers.get(i);
            if (info.activityInfo != null) {
                String packageName = info.activityInfo.packageName;
                PackageSetting ps = this.mSettings.mPackages.get(packageName);
                if (ps != null) {
                    GrantedPermissions gp = ps.sharedUser != null ? ps.sharedUser : ps;
                    if (!gp.grantedPermissions.contains(Manifest.permission.PACKAGE_VERIFICATION_AGENT)) {
                        continue;
                    } else if (requiredVerifier != null) {
                        throw new RuntimeException("There can be only one required verifier");
                    } else {
                        requiredVerifier = packageName;
                    }
                } else {
                    continue;
                }
            }
        }
        return requiredVerifier;
    }

    @Override // android.content.pm.IPackageManager.Stub, android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException) && !(e instanceof IllegalArgumentException)) {
                Slog.wtf(TAG, "Package Manager Crash", e);
            }
            throw e;
        }
    }

    void cleanupInstallFailedPackage(PackageSetting ps) {
        Slog.i(TAG, "Cleaning up incompletely installed app: " + ps.name);
        removeDataDirsLI(ps.name);
        if (ps.codePath != null && !ps.codePath.delete()) {
            Slog.w(TAG, "Unable to remove old code file: " + ps.codePath);
        }
        if (ps.resourcePath != null && !ps.resourcePath.delete() && !ps.resourcePath.equals(ps.codePath)) {
            Slog.w(TAG, "Unable to remove old code file: " + ps.resourcePath);
        }
        this.mSettings.removePackageLPw(ps.name);
    }

    void readPermissions() {
        File libraryDir = new File(Environment.getRootDirectory(), "etc/permissions");
        if (!libraryDir.exists() || !libraryDir.isDirectory()) {
            Slog.w(TAG, "No directory " + libraryDir + ", skipping");
        } else if (!libraryDir.canRead()) {
            Slog.w(TAG, "Directory " + libraryDir + " cannot be read");
        } else {
            File[] arr$ = libraryDir.listFiles();
            for (File f : arr$) {
                if (!f.getPath().endsWith("etc/permissions/platform.xml")) {
                    if (!f.getPath().endsWith(".xml")) {
                        Slog.i(TAG, "Non-xml file " + f + " in " + libraryDir + " directory, ignoring");
                    } else if (!f.canRead()) {
                        Slog.w(TAG, "Permissions library file " + f + " cannot be read");
                    } else {
                        readPermissionsFromXml(f);
                    }
                }
            }
            File permFile = new File(Environment.getRootDirectory(), "etc/permissions/platform.xml");
            readPermissionsFromXml(permFile);
        }
    }

    private void readPermissionsFromXml(File permFile) {
        try {
            FileReader permReader = new FileReader(permFile);
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(permReader);
                XmlUtils.beginDocument(parser, "permissions");
                while (true) {
                    XmlUtils.nextElement(parser);
                    if (parser.getEventType() != 1) {
                        String name = parser.getName();
                        if (WifiConfiguration.GroupCipher.varName.equals(name)) {
                            String gidStr = parser.getAttributeValue(null, "gid");
                            if (gidStr != null) {
                                int gid = Process.getGidForName(gidStr);
                                this.mGlobalGids = ArrayUtils.appendInt(this.mGlobalGids, gid);
                            } else {
                                Slog.w(TAG, "<group> without gid at " + parser.getPositionDescription());
                            }
                            XmlUtils.skipCurrentTag(parser);
                        } else if (UsbManager.EXTRA_PERMISSION_GRANTED.equals(name)) {
                            String perm = parser.getAttributeValue(null, "name");
                            if (perm == null) {
                                Slog.w(TAG, "<permission> without name at " + parser.getPositionDescription());
                                XmlUtils.skipCurrentTag(parser);
                            } else {
                                readPermission(parser, perm.intern());
                            }
                        } else if ("assign-permission".equals(name)) {
                            String perm2 = parser.getAttributeValue(null, "name");
                            if (perm2 == null) {
                                Slog.w(TAG, "<assign-permission> without name at " + parser.getPositionDescription());
                                XmlUtils.skipCurrentTag(parser);
                            } else {
                                String uidStr = parser.getAttributeValue(null, GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID);
                                if (uidStr == null) {
                                    Slog.w(TAG, "<assign-permission> without uid at " + parser.getPositionDescription());
                                    XmlUtils.skipCurrentTag(parser);
                                } else {
                                    int uid = Process.getUidForName(uidStr);
                                    if (uid < 0) {
                                        Slog.w(TAG, "<assign-permission> with unknown uid \"" + uidStr + "\" at " + parser.getPositionDescription());
                                        XmlUtils.skipCurrentTag(parser);
                                    } else {
                                        String perm3 = perm2.intern();
                                        HashSet<String> perms = this.mSystemPermissions.get(uid);
                                        if (perms == null) {
                                            perms = new HashSet<>();
                                            this.mSystemPermissions.put(uid, perms);
                                        }
                                        perms.add(perm3);
                                        XmlUtils.skipCurrentTag(parser);
                                    }
                                }
                            }
                        } else if ("library".equals(name)) {
                            String lname = parser.getAttributeValue(null, "name");
                            String lfile = parser.getAttributeValue(null, ContentResolver.SCHEME_FILE);
                            if (lname == null) {
                                Slog.w(TAG, "<library> without name at " + parser.getPositionDescription());
                            } else if (lfile == null) {
                                Slog.w(TAG, "<library> without file at " + parser.getPositionDescription());
                            } else {
                                this.mSharedLibraries.put(lname, new SharedLibraryEntry(lfile, null));
                            }
                            XmlUtils.skipCurrentTag(parser);
                        } else if ("feature".equals(name)) {
                            String fname = parser.getAttributeValue(null, "name");
                            if (fname == null) {
                                Slog.w(TAG, "<feature> without name at " + parser.getPositionDescription());
                            } else {
                                FeatureInfo fi = new FeatureInfo();
                                fi.name = fname;
                                this.mAvailableFeatures.put(fname, fi);
                            }
                            XmlUtils.skipCurrentTag(parser);
                        } else {
                            XmlUtils.skipCurrentTag(parser);
                        }
                    } else {
                        permReader.close();
                        return;
                    }
                }
            } catch (IOException e) {
                Slog.w(TAG, "Got execption parsing permissions.", e);
            } catch (XmlPullParserException e2) {
                Slog.w(TAG, "Got execption parsing permissions.", e2);
            }
        } catch (FileNotFoundException e3) {
            Slog.w(TAG, "Couldn't find or open permissions file " + permFile);
        }
    }

    void readPermission(XmlPullParser parser, String name) throws IOException, XmlPullParserException {
        String name2 = name.intern();
        BasePermission bp = this.mSettings.mPermissions.get(name2);
        if (bp == null) {
            bp = new BasePermission(name2, null, 1);
            this.mSettings.mPermissions.put(name2, bp);
        }
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (WifiConfiguration.GroupCipher.varName.equals(tagName)) {
                        String gidStr = parser.getAttributeValue(null, "gid");
                        if (gidStr != null) {
                            int gid = Process.getGidForName(gidStr);
                            bp.gids = ArrayUtils.appendInt(bp.gids, gid);
                        } else {
                            Slog.w(TAG, "<group> without gid at " + parser.getPositionDescription());
                        }
                    }
                    XmlUtils.skipCurrentTag(parser);
                }
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int[] appendInts(int[] cur, int[] add) {
        if (add == null) {
            return cur;
        }
        if (cur == null) {
            return add;
        }
        for (int i : add) {
            cur = ArrayUtils.appendInt(cur, i);
        }
        return cur;
    }

    static int[] removeInts(int[] cur, int[] rem) {
        if (rem != null && cur != null) {
            for (int i : rem) {
                cur = ArrayUtils.removeInt(cur, i);
            }
            return cur;
        }
        return cur;
    }

    PackageInfo generatePackageInfo(PackageParser.Package p, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            PackageSetting ps = (PackageSetting) p.mExtras;
            if (ps == null) {
                return null;
            }
            GrantedPermissions gp = ps.sharedUser != null ? ps.sharedUser : ps;
            PackageUserState state = ps.readUserState(userId);
            return PackageParser.generatePackageInfo(p, gp.gids, flags, ps.firstInstallTime, ps.lastUpdateTime, gp.grantedPermissions, state, userId);
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public PackageInfo getPackageInfo(String packageName, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            enforceCrossUserPermission(Binder.getCallingUid(), userId, false, "get package info");
            synchronized (this.mPackages) {
                PackageParser.Package p = this.mPackages.get(packageName);
                if (p != null) {
                    return generatePackageInfo(p, flags, userId);
                } else if ((flags & 8192) != 0) {
                    return generatePackageInfoFromSettingsLPw(packageName, flags, userId);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public String[] currentToCanonicalPackageNames(String[] names) {
        String[] out = new String[names.length];
        synchronized (this.mPackages) {
            for (int i = names.length - 1; i >= 0; i--) {
                PackageSetting ps = this.mSettings.mPackages.get(names[i]);
                out[i] = (ps == null || ps.realName == null) ? names[i] : ps.realName;
            }
        }
        return out;
    }

    @Override // android.content.pm.IPackageManager
    public String[] canonicalToCurrentPackageNames(String[] names) {
        String[] out = new String[names.length];
        synchronized (this.mPackages) {
            for (int i = names.length - 1; i >= 0; i--) {
                String cur = this.mSettings.mRenamedPackages.get(names[i]);
                out[i] = cur != null ? cur : names[i];
            }
        }
        return out;
    }

    @Override // android.content.pm.IPackageManager
    public int getPackageUid(String packageName, int userId) {
        if (sUserManager.exists(userId)) {
            enforceCrossUserPermission(Binder.getCallingUid(), userId, false, "get package uid");
            synchronized (this.mPackages) {
                PackageParser.Package p = this.mPackages.get(packageName);
                if (p != null) {
                    return UserHandle.getUid(userId, p.applicationInfo.uid);
                }
                PackageSetting ps = this.mSettings.mPackages.get(packageName);
                if (ps == null || ps.pkg == null || ps.pkg.applicationInfo == null) {
                    return -1;
                }
                PackageParser.Package p2 = ps.pkg;
                return p2 != null ? UserHandle.getUid(userId, p2.applicationInfo.uid) : -1;
            }
        }
        return -1;
    }

    @Override // android.content.pm.IPackageManager
    public int[] getPackageGids(String packageName) {
        synchronized (this.mPackages) {
            PackageParser.Package p = this.mPackages.get(packageName);
            if (p != null) {
                PackageSetting ps = (PackageSetting) p.mExtras;
                return ps.getGids();
            }
            return new int[0];
        }
    }

    static final PermissionInfo generatePermissionInfo(BasePermission bp, int flags) {
        if (bp.perm != null) {
            return PackageParser.generatePermissionInfo(bp.perm, flags);
        }
        PermissionInfo pi = new PermissionInfo();
        pi.name = bp.name;
        pi.packageName = bp.sourcePackage;
        pi.nonLocalizedLabel = bp.name;
        pi.protectionLevel = bp.protectionLevel;
        return pi;
    }

    @Override // android.content.pm.IPackageManager
    public PermissionInfo getPermissionInfo(String name, int flags) {
        synchronized (this.mPackages) {
            BasePermission p = this.mSettings.mPermissions.get(name);
            if (p != null) {
                return generatePermissionInfo(p, flags);
            }
            return null;
        }
    }

    @Override // android.content.pm.IPackageManager
    public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) {
        synchronized (this.mPackages) {
            ArrayList<PermissionInfo> out = new ArrayList<>(10);
            for (BasePermission p : this.mSettings.mPermissions.values()) {
                if (group == null) {
                    if (p.perm == null || p.perm.info.group == null) {
                        out.add(generatePermissionInfo(p, flags));
                    }
                } else if (p.perm != null && group.equals(p.perm.info.group)) {
                    out.add(PackageParser.generatePermissionInfo(p.perm, flags));
                }
            }
            if (out.size() > 0) {
                return out;
            }
            return this.mPermissionGroups.containsKey(group) ? out : null;
        }
    }

    @Override // android.content.pm.IPackageManager
    public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) {
        PermissionGroupInfo generatePermissionGroupInfo;
        synchronized (this.mPackages) {
            generatePermissionGroupInfo = PackageParser.generatePermissionGroupInfo(this.mPermissionGroups.get(name), flags);
        }
        return generatePermissionGroupInfo;
    }

    @Override // android.content.pm.IPackageManager
    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) {
        ArrayList<PermissionGroupInfo> out;
        synchronized (this.mPackages) {
            int N = this.mPermissionGroups.size();
            out = new ArrayList<>(N);
            for (PackageParser.PermissionGroup pg : this.mPermissionGroups.values()) {
                out.add(PackageParser.generatePermissionGroupInfo(pg, flags));
            }
        }
        return out;
    }

    private ApplicationInfo generateApplicationInfoFromSettingsLPw(String packageName, int flags, int userId) {
        PackageSetting ps;
        if (sUserManager.exists(userId) && (ps = this.mSettings.mPackages.get(packageName)) != null) {
            if (ps.pkg == null) {
                PackageInfo pInfo = generatePackageInfoFromSettingsLPw(packageName, flags, userId);
                if (pInfo != null) {
                    return pInfo.applicationInfo;
                }
                return null;
            }
            return PackageParser.generateApplicationInfo(ps.pkg, flags, ps.readUserState(userId), userId);
        }
        return null;
    }

    private PackageInfo generatePackageInfoFromSettingsLPw(String packageName, int flags, int userId) {
        PackageSetting ps;
        if (sUserManager.exists(userId) && (ps = this.mSettings.mPackages.get(packageName)) != null) {
            PackageParser.Package pkg = ps.pkg;
            if (pkg == null) {
                if ((flags & 8192) == 0) {
                    return null;
                }
                pkg = new PackageParser.Package(packageName);
                pkg.applicationInfo.packageName = packageName;
                pkg.applicationInfo.flags = ps.pkgFlags | 16777216;
                pkg.applicationInfo.publicSourceDir = ps.resourcePathString;
                pkg.applicationInfo.sourceDir = ps.codePathString;
                pkg.applicationInfo.dataDir = getDataPathForPackage(packageName, 0).getPath();
                pkg.applicationInfo.nativeLibraryDir = ps.nativeLibraryPathString;
            }
            return generatePackageInfo(pkg, flags, userId);
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public ApplicationInfo getApplicationInfo(String packageName, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            enforceCrossUserPermission(Binder.getCallingUid(), userId, false, "get application info");
            synchronized (this.mPackages) {
                PackageParser.Package p = this.mPackages.get(packageName);
                if (p != null) {
                    PackageSetting ps = this.mSettings.mPackages.get(packageName);
                    if (ps == null) {
                        return null;
                    }
                    return PackageParser.generateApplicationInfo(p, flags, ps.readUserState(userId), userId);
                } else if ("android".equals(packageName) || "system".equals(packageName)) {
                    return this.mAndroidApplication;
                } else if ((flags & 8192) != 0) {
                    return generateApplicationInfoFromSettingsLPw(packageName, flags, userId);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public void freeStorageAndNotify(final long freeStorageSize, final IPackageDataObserver observer) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CLEAR_APP_CACHE, null);
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageManagerService.1
            @Override // java.lang.Runnable
            public void run() {
                int retCode;
                PackageManagerService.this.mHandler.removeCallbacks(this);
                synchronized (PackageManagerService.this.mInstallLock) {
                    retCode = PackageManagerService.this.mInstaller.freeCache(freeStorageSize);
                    if (retCode < 0) {
                        Slog.w(PackageManagerService.TAG, "Couldn't clear application caches");
                    }
                }
                if (observer != null) {
                    try {
                        observer.onRemoveCompleted(null, retCode >= 0);
                    } catch (RemoteException e) {
                        Slog.w(PackageManagerService.TAG, "RemoveException when invoking call back");
                    }
                }
            }
        });
    }

    @Override // android.content.pm.IPackageManager
    public void freeStorage(final long freeStorageSize, final IntentSender pi) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CLEAR_APP_CACHE, null);
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageManagerService.2
            @Override // java.lang.Runnable
            public void run() {
                int retCode;
                PackageManagerService.this.mHandler.removeCallbacks(this);
                synchronized (PackageManagerService.this.mInstallLock) {
                    retCode = PackageManagerService.this.mInstaller.freeCache(freeStorageSize);
                    if (retCode < 0) {
                        Slog.w(PackageManagerService.TAG, "Couldn't clear application caches");
                    }
                }
                if (pi != null) {
                    try {
                        int code = retCode >= 0 ? 1 : 0;
                        pi.sendIntent(null, code, null, null, null);
                    } catch (IntentSender.SendIntentException e) {
                        Slog.i(PackageManagerService.TAG, "Failed to send pending intent");
                    }
                }
            }
        });
    }

    @Override // android.content.pm.IPackageManager
    public ActivityInfo getActivityInfo(ComponentName component, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            enforceCrossUserPermission(Binder.getCallingUid(), userId, false, "get activity info");
            synchronized (this.mPackages) {
                PackageParser.Activity a = (PackageParser.Activity) this.mActivities.mActivities.get(component);
                if (a != null && this.mSettings.isEnabledLPr(a.info, flags, userId)) {
                    PackageSetting ps = this.mSettings.mPackages.get(component.getPackageName());
                    if (ps == null) {
                        return null;
                    }
                    return PackageParser.generateActivityInfo(a, flags, ps.readUserState(userId), userId);
                } else if (this.mResolveComponentName.equals(component)) {
                    return this.mResolveActivity;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public ActivityInfo getReceiverInfo(ComponentName component, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            enforceCrossUserPermission(Binder.getCallingUid(), userId, false, "get receiver info");
            synchronized (this.mPackages) {
                PackageParser.Activity a = (PackageParser.Activity) this.mReceivers.mActivities.get(component);
                if (a != null && this.mSettings.isEnabledLPr(a.info, flags, userId)) {
                    PackageSetting ps = this.mSettings.mPackages.get(component.getPackageName());
                    if (ps == null) {
                        return null;
                    }
                    return PackageParser.generateActivityInfo(a, flags, ps.readUserState(userId), userId);
                }
                return null;
            }
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public ServiceInfo getServiceInfo(ComponentName component, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            enforceCrossUserPermission(Binder.getCallingUid(), userId, false, "get service info");
            synchronized (this.mPackages) {
                PackageParser.Service s = (PackageParser.Service) this.mServices.mServices.get(component);
                if (s != null && this.mSettings.isEnabledLPr(s.info, flags, userId)) {
                    PackageSetting ps = this.mSettings.mPackages.get(component.getPackageName());
                    if (ps == null) {
                        return null;
                    }
                    return PackageParser.generateServiceInfo(s, flags, ps.readUserState(userId), userId);
                }
                return null;
            }
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public ProviderInfo getProviderInfo(ComponentName component, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            enforceCrossUserPermission(Binder.getCallingUid(), userId, false, "get provider info");
            synchronized (this.mPackages) {
                PackageParser.Provider p = (PackageParser.Provider) this.mProviders.mProviders.get(component);
                if (p != null && this.mSettings.isEnabledLPr(p.info, flags, userId)) {
                    PackageSetting ps = this.mSettings.mPackages.get(component.getPackageName());
                    if (ps == null) {
                        return null;
                    }
                    return PackageParser.generateProviderInfo(p, flags, ps.readUserState(userId), userId);
                }
                return null;
            }
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public String[] getSystemSharedLibraryNames() {
        synchronized (this.mPackages) {
            Set<String> libSet = this.mSharedLibraries.keySet();
            int size = libSet.size();
            if (size > 0) {
                String[] libs = new String[size];
                libSet.toArray(libs);
                return libs;
            }
            return null;
        }
    }

    @Override // android.content.pm.IPackageManager
    public FeatureInfo[] getSystemAvailableFeatures() {
        synchronized (this.mPackages) {
            Collection<FeatureInfo> featSet = this.mAvailableFeatures.values();
            int size = featSet.size();
            if (size > 0) {
                FeatureInfo[] features = new FeatureInfo[size + 1];
                featSet.toArray(features);
                FeatureInfo fi = new FeatureInfo();
                fi.reqGlEsVersion = SystemProperties.getInt("ro.opengles.version", 0);
                features[size] = fi;
                return features;
            }
            return null;
        }
    }

    @Override // android.content.pm.IPackageManager
    public boolean hasSystemFeature(String name) {
        boolean containsKey;
        synchronized (this.mPackages) {
            containsKey = this.mAvailableFeatures.containsKey(name);
        }
        return containsKey;
    }

    private void checkValidCaller(int uid, int userId) {
        if (UserHandle.getUserId(uid) == userId || uid == 1000 || uid == 0) {
            return;
        }
        throw new SecurityException("Caller uid=" + uid + " is not privileged to communicate with user=" + userId);
    }

    @Override // android.content.pm.IPackageManager
    public int checkPermission(String permName, String pkgName) {
        synchronized (this.mPackages) {
            PackageParser.Package p = this.mPackages.get(pkgName);
            if (p != null && p.mExtras != null) {
                PackageSetting ps = (PackageSetting) p.mExtras;
                if (ps.sharedUser != null) {
                    if (ps.sharedUser.grantedPermissions.contains(permName)) {
                        return 0;
                    }
                } else if (ps.grantedPermissions.contains(permName)) {
                    return 0;
                }
            }
            return -1;
        }
    }

    @Override // android.content.pm.IPackageManager
    public int checkUidPermission(String permName, int uid) {
        synchronized (this.mPackages) {
            Object obj = this.mSettings.getUserIdLPr(UserHandle.getAppId(uid));
            if (obj != null) {
                GrantedPermissions gp = (GrantedPermissions) obj;
                if (gp.grantedPermissions.contains(permName)) {
                    return 0;
                }
            } else {
                HashSet<String> perms = this.mSystemPermissions.get(uid);
                if (perms != null && perms.contains(permName)) {
                    return 0;
                }
            }
            return -1;
        }
    }

    private void enforceCrossUserPermission(int callingUid, int userId, boolean requireFullPermission, String message) {
        if (userId < 0) {
            throw new IllegalArgumentException("Invalid userId " + userId);
        }
        if (userId != UserHandle.getUserId(callingUid) && callingUid != 1000 && callingUid != 0) {
            if (requireFullPermission) {
                this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL, message);
                return;
            }
            try {
                this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL, message);
            } catch (SecurityException e) {
                this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS, message);
            }
        }
    }

    private BasePermission findPermissionTreeLP(String permName) {
        for (BasePermission bp : this.mSettings.mPermissionTrees.values()) {
            if (permName.startsWith(bp.name) && permName.length() > bp.name.length() && permName.charAt(bp.name.length()) == '.') {
                return bp;
            }
        }
        return null;
    }

    private BasePermission checkPermissionTreeLP(String permName) {
        BasePermission bp;
        if (permName != null && (bp = findPermissionTreeLP(permName)) != null) {
            if (bp.uid == UserHandle.getAppId(Binder.getCallingUid())) {
                return bp;
            }
            throw new SecurityException("Calling uid " + Binder.getCallingUid() + " is not allowed to add to permission tree " + bp.name + " owned by uid " + bp.uid);
        }
        throw new SecurityException("No permission tree found for " + permName);
    }

    static boolean compareStrings(CharSequence s1, CharSequence s2) {
        if (s1 == null) {
            return s2 == null;
        } else if (s2 == null || s1.getClass() != s2.getClass()) {
            return false;
        } else {
            return s1.equals(s2);
        }
    }

    static boolean comparePermissionInfos(PermissionInfo pi1, PermissionInfo pi2) {
        return pi1.icon == pi2.icon && pi1.logo == pi2.logo && pi1.protectionLevel == pi2.protectionLevel && compareStrings(pi1.name, pi2.name) && compareStrings(pi1.nonLocalizedLabel, pi2.nonLocalizedLabel) && compareStrings(pi1.packageName, pi2.packageName);
    }

    boolean addPermissionLocked(PermissionInfo info, boolean async) {
        if (info.labelRes == 0 && info.nonLocalizedLabel == null) {
            throw new SecurityException("Label must be specified in permission");
        }
        BasePermission tree = checkPermissionTreeLP(info.name);
        BasePermission bp = this.mSettings.mPermissions.get(info.name);
        boolean added = bp == null;
        boolean changed = true;
        int fixedLevel = PermissionInfo.fixProtectionLevel(info.protectionLevel);
        if (added) {
            bp = new BasePermission(info.name, tree.sourcePackage, 2);
        } else if (bp.type != 2) {
            throw new SecurityException("Not allowed to modify non-dynamic permission " + info.name);
        } else {
            if (bp.protectionLevel == fixedLevel && bp.perm.owner.equals(tree.perm.owner) && bp.uid == tree.uid && comparePermissionInfos(bp.perm.info, info)) {
                changed = false;
            }
        }
        bp.protectionLevel = fixedLevel;
        PermissionInfo info2 = new PermissionInfo(info);
        info2.protectionLevel = fixedLevel;
        bp.perm = new PackageParser.Permission(tree.perm.owner, info2);
        bp.perm.info.packageName = tree.perm.info.packageName;
        bp.uid = tree.uid;
        if (added) {
            this.mSettings.mPermissions.put(info2.name, bp);
        }
        if (changed) {
            if (!async) {
                this.mSettings.writeLPr();
            } else {
                scheduleWriteSettingsLocked();
            }
        }
        return added;
    }

    @Override // android.content.pm.IPackageManager
    public boolean addPermission(PermissionInfo info) {
        boolean addPermissionLocked;
        synchronized (this.mPackages) {
            addPermissionLocked = addPermissionLocked(info, false);
        }
        return addPermissionLocked;
    }

    @Override // android.content.pm.IPackageManager
    public boolean addPermissionAsync(PermissionInfo info) {
        boolean addPermissionLocked;
        synchronized (this.mPackages) {
            addPermissionLocked = addPermissionLocked(info, true);
        }
        return addPermissionLocked;
    }

    @Override // android.content.pm.IPackageManager
    public void removePermission(String name) {
        synchronized (this.mPackages) {
            checkPermissionTreeLP(name);
            BasePermission bp = this.mSettings.mPermissions.get(name);
            if (bp != null) {
                if (bp.type != 2) {
                    throw new SecurityException("Not allowed to modify non-dynamic permission " + name);
                }
                this.mSettings.mPermissions.remove(name);
                this.mSettings.writeLPr();
            }
        }
    }

    private static void checkGrantRevokePermissions(PackageParser.Package pkg, BasePermission bp) {
        int index = pkg.requestedPermissions.indexOf(bp.name);
        if (index == -1) {
            throw new SecurityException("Package " + pkg.packageName + " has not requested permission " + bp.name);
        }
        boolean isNormal = (bp.protectionLevel & 15) == 0;
        boolean isDangerous = (bp.protectionLevel & 15) == 1;
        boolean isDevelopment = (bp.protectionLevel & 32) != 0;
        if (!isNormal && !isDangerous && !isDevelopment) {
            throw new SecurityException("Permission " + bp.name + " is not a changeable permission type");
        }
        if ((isNormal || isDangerous) && pkg.requestedPermissionsRequired.get(index).booleanValue()) {
            throw new SecurityException("Can't change " + bp.name + ". It is required by the application");
        }
    }

    @Override // android.content.pm.IPackageManager
    public void grantPermission(String packageName, String permissionName) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.GRANT_REVOKE_PERMISSIONS, null);
        synchronized (this.mPackages) {
            PackageParser.Package pkg = this.mPackages.get(packageName);
            if (pkg == null) {
                throw new IllegalArgumentException("Unknown package: " + packageName);
            }
            BasePermission bp = this.mSettings.mPermissions.get(permissionName);
            if (bp == null) {
                throw new IllegalArgumentException("Unknown permission: " + permissionName);
            }
            checkGrantRevokePermissions(pkg, bp);
            PackageSetting ps = (PackageSetting) pkg.mExtras;
            if (ps == null) {
                return;
            }
            GrantedPermissions gp = ps.sharedUser != null ? ps.sharedUser : ps;
            if (gp.grantedPermissions.add(permissionName)) {
                if (ps.haveGids) {
                    gp.gids = appendInts(gp.gids, bp.gids);
                }
                this.mSettings.writeLPr();
            }
        }
    }

    @Override // android.content.pm.IPackageManager
    public boolean isProtectedBroadcast(String actionName) {
        boolean contains;
        synchronized (this.mPackages) {
            contains = this.mProtectedBroadcasts.contains(actionName);
        }
        return contains;
    }

    @Override // android.content.pm.IPackageManager
    public int checkSignatures(String pkg1, String pkg2) {
        synchronized (this.mPackages) {
            PackageParser.Package p1 = this.mPackages.get(pkg1);
            PackageParser.Package p2 = this.mPackages.get(pkg2);
            if (p1 == null || p1.mExtras == null || p2 == null || p2.mExtras == null) {
                return -4;
            }
            return compareSignatures(p1.mSignatures, p2.mSignatures);
        }
    }

    @Override // android.content.pm.IPackageManager
    public int checkUidSignatures(int uid1, int uid2) {
        Signature[] s1;
        Signature[] s2;
        int uid12 = UserHandle.getAppId(uid1);
        int uid22 = UserHandle.getAppId(uid2);
        synchronized (this.mPackages) {
            Object obj = this.mSettings.getUserIdLPr(uid12);
            if (obj != null) {
                if (obj instanceof SharedUserSetting) {
                    s1 = ((SharedUserSetting) obj).signatures.mSignatures;
                } else if (obj instanceof PackageSetting) {
                    s1 = ((PackageSetting) obj).signatures.mSignatures;
                } else {
                    return -4;
                }
                Object obj2 = this.mSettings.getUserIdLPr(uid22);
                if (obj2 != null) {
                    if (obj2 instanceof SharedUserSetting) {
                        s2 = ((SharedUserSetting) obj2).signatures.mSignatures;
                    } else if (obj2 instanceof PackageSetting) {
                        s2 = ((PackageSetting) obj2).signatures.mSignatures;
                    } else {
                        return -4;
                    }
                    return compareSignatures(s1, s2);
                }
                return -4;
            }
            return -4;
        }
    }

    static int compareSignatures(Signature[] s1, Signature[] s2) {
        if (s1 == null) {
            return s2 == null ? 1 : -1;
        } else if (s2 == null) {
            return -2;
        } else {
            HashSet<Signature> set1 = new HashSet<>();
            for (Signature sig : s1) {
                set1.add(sig);
            }
            HashSet<Signature> set2 = new HashSet<>();
            for (Signature sig2 : s2) {
                set2.add(sig2);
            }
            if (set1.equals(set2)) {
                return 0;
            }
            return -3;
        }
    }

    @Override // android.content.pm.IPackageManager
    public String[] getPackagesForUid(int uid) {
        int uid2 = UserHandle.getAppId(uid);
        synchronized (this.mPackages) {
            Object obj = this.mSettings.getUserIdLPr(uid2);
            if (obj instanceof SharedUserSetting) {
                SharedUserSetting sus = (SharedUserSetting) obj;
                int N = sus.packages.size();
                String[] res = new String[N];
                Iterator<PackageSetting> it = sus.packages.iterator();
                int i = 0;
                while (it.hasNext()) {
                    int i2 = i;
                    i++;
                    res[i2] = it.next().name;
                }
                return res;
            } else if (obj instanceof PackageSetting) {
                PackageSetting ps = (PackageSetting) obj;
                return new String[]{ps.name};
            } else {
                return null;
            }
        }
    }

    @Override // android.content.pm.IPackageManager
    public String getNameForUid(int uid) {
        synchronized (this.mPackages) {
            Object obj = this.mSettings.getUserIdLPr(UserHandle.getAppId(uid));
            if (obj instanceof SharedUserSetting) {
                SharedUserSetting sus = (SharedUserSetting) obj;
                return sus.name + Separators.COLON + sus.userId;
            } else if (obj instanceof PackageSetting) {
                PackageSetting ps = (PackageSetting) obj;
                return ps.name;
            } else {
                return null;
            }
        }
    }

    @Override // android.content.pm.IPackageManager
    public int getUidForSharedUser(String sharedUserName) {
        if (sharedUserName == null) {
            return -1;
        }
        synchronized (this.mPackages) {
            SharedUserSetting suid = this.mSettings.getSharedUserLPw(sharedUserName, 0, false);
            if (suid == null) {
                return -1;
            }
            return suid.userId;
        }
    }

    @Override // android.content.pm.IPackageManager
    public int getFlagsForUid(int uid) {
        synchronized (this.mPackages) {
            Object obj = this.mSettings.getUserIdLPr(UserHandle.getAppId(uid));
            if (obj instanceof SharedUserSetting) {
                SharedUserSetting sus = (SharedUserSetting) obj;
                return sus.pkgFlags;
            } else if (obj instanceof PackageSetting) {
                PackageSetting ps = (PackageSetting) obj;
                return ps.pkgFlags;
            } else {
                return 0;
            }
        }
    }

    @Override // android.content.pm.IPackageManager
    public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            enforceCrossUserPermission(Binder.getCallingUid(), userId, false, "resolve intent");
            List<ResolveInfo> query = queryIntentActivities(intent, resolvedType, flags, userId);
            return chooseBestActivity(intent, resolvedType, flags, query, userId);
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public void setLastChosenActivity(Intent intent, String resolvedType, int flags, IntentFilter filter, int match, ComponentName activity) {
        int userId = UserHandle.getCallingUserId();
        intent.setComponent(null);
        List<ResolveInfo> query = queryIntentActivities(intent, resolvedType, flags, userId);
        findPreferredActivity(intent, resolvedType, flags, query, 0, false, true, false, userId);
        addPreferredActivityInternal(filter, match, null, activity, false, userId);
    }

    @Override // android.content.pm.IPackageManager
    public ResolveInfo getLastChosenActivity(Intent intent, String resolvedType, int flags) {
        int userId = UserHandle.getCallingUserId();
        List<ResolveInfo> query = queryIntentActivities(intent, resolvedType, flags, userId);
        return findPreferredActivity(intent, resolvedType, flags, query, 0, false, false, false, userId);
    }

    private ResolveInfo chooseBestActivity(Intent intent, String resolvedType, int flags, List<ResolveInfo> query, int userId) {
        if (query != null) {
            int N = query.size();
            if (N == 1) {
                return query.get(0);
            }
            if (N > 1) {
                boolean debug = (intent.getFlags() & 8) != 0;
                ResolveInfo r0 = query.get(0);
                ResolveInfo r1 = query.get(1);
                if (debug) {
                    Slog.v(TAG, r0.activityInfo.name + Separators.EQUALS + r0.priority + " vs " + r1.activityInfo.name + Separators.EQUALS + r1.priority);
                }
                if (r0.priority != r1.priority || r0.preferredOrder != r1.preferredOrder || r0.isDefault != r1.isDefault) {
                    return query.get(0);
                }
                ResolveInfo ri = findPreferredActivity(intent, resolvedType, flags, query, r0.priority, true, false, debug, userId);
                if (ri != null) {
                    return ri;
                }
                if (userId != 0) {
                    ResolveInfo ri2 = new ResolveInfo(this.mResolveInfo);
                    ri2.activityInfo = new ActivityInfo(ri2.activityInfo);
                    ri2.activityInfo.applicationInfo = new ApplicationInfo(ri2.activityInfo.applicationInfo);
                    ri2.activityInfo.applicationInfo.uid = UserHandle.getUid(userId, UserHandle.getAppId(ri2.activityInfo.applicationInfo.uid));
                    return ri2;
                }
                return this.mResolveInfo;
            }
            return null;
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:124:0x0373, code lost:
        continue;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    android.content.pm.ResolveInfo findPreferredActivity(android.content.Intent r9, java.lang.String r10, int r11, java.util.List<android.content.pm.ResolveInfo> r12, int r13, boolean r14, boolean r15, boolean r16, int r17) {
        /*
            Method dump skipped, instructions count: 928
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.findPreferredActivity(android.content.Intent, java.lang.String, int, java.util.List, int, boolean, boolean, boolean, int):android.content.pm.ResolveInfo");
    }

    @Override // android.content.pm.IPackageManager
    public List<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            enforceCrossUserPermission(Binder.getCallingUid(), userId, false, "query intent activities");
            ComponentName comp = intent.getComponent();
            if (comp == null && intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
            if (comp != null) {
                List<ResolveInfo> list = new ArrayList<>(1);
                ActivityInfo ai = getActivityInfo(comp, flags, userId);
                if (ai != null) {
                    ResolveInfo ri = new ResolveInfo();
                    ri.activityInfo = ai;
                    list.add(ri);
                }
                return list;
            }
            synchronized (this.mPackages) {
                String pkgName = intent.getPackage();
                if (pkgName == null) {
                    return this.mActivities.queryIntent(intent, resolvedType, flags, userId);
                }
                PackageParser.Package pkg = this.mPackages.get(pkgName);
                if (pkg != null) {
                    return this.mActivities.queryIntentForPackage(intent, resolvedType, flags, pkg.activities, userId);
                }
                return new ArrayList();
            }
        }
        return Collections.emptyList();
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x00ef  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x015c  */
    @Override // android.content.pm.IPackageManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List<android.content.pm.ResolveInfo> queryIntentActivityOptions(android.content.ComponentName r7, android.content.Intent[] r8, java.lang.String[] r9, android.content.Intent r10, java.lang.String r11, int r12, int r13) {
        /*
            Method dump skipped, instructions count: 733
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.queryIntentActivityOptions(android.content.ComponentName, android.content.Intent[], java.lang.String[], android.content.Intent, java.lang.String, int, int):java.util.List");
    }

    @Override // android.content.pm.IPackageManager
    public List<ResolveInfo> queryIntentReceivers(Intent intent, String resolvedType, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            ComponentName comp = intent.getComponent();
            if (comp == null && intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
            if (comp != null) {
                List<ResolveInfo> list = new ArrayList<>(1);
                ActivityInfo ai = getReceiverInfo(comp, flags, userId);
                if (ai != null) {
                    ResolveInfo ri = new ResolveInfo();
                    ri.activityInfo = ai;
                    list.add(ri);
                }
                return list;
            }
            synchronized (this.mPackages) {
                String pkgName = intent.getPackage();
                if (pkgName == null) {
                    return this.mReceivers.queryIntent(intent, resolvedType, flags, userId);
                }
                PackageParser.Package pkg = this.mPackages.get(pkgName);
                if (pkg != null) {
                    return this.mReceivers.queryIntentForPackage(intent, resolvedType, flags, pkg.receivers, userId);
                }
                return null;
            }
        }
        return Collections.emptyList();
    }

    @Override // android.content.pm.IPackageManager
    public ResolveInfo resolveService(Intent intent, String resolvedType, int flags, int userId) {
        List<ResolveInfo> query = queryIntentServices(intent, resolvedType, flags, userId);
        if (sUserManager.exists(userId) && query != null && query.size() >= 1) {
            return query.get(0);
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public List<ResolveInfo> queryIntentServices(Intent intent, String resolvedType, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            ComponentName comp = intent.getComponent();
            if (comp == null && intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
            if (comp != null) {
                List<ResolveInfo> list = new ArrayList<>(1);
                ServiceInfo si = getServiceInfo(comp, flags, userId);
                if (si != null) {
                    ResolveInfo ri = new ResolveInfo();
                    ri.serviceInfo = si;
                    list.add(ri);
                }
                return list;
            }
            synchronized (this.mPackages) {
                String pkgName = intent.getPackage();
                if (pkgName == null) {
                    return this.mServices.queryIntent(intent, resolvedType, flags, userId);
                }
                PackageParser.Package pkg = this.mPackages.get(pkgName);
                if (pkg != null) {
                    return this.mServices.queryIntentForPackage(intent, resolvedType, flags, pkg.services, userId);
                }
                return null;
            }
        }
        return Collections.emptyList();
    }

    @Override // android.content.pm.IPackageManager
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, String resolvedType, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            ComponentName comp = intent.getComponent();
            if (comp == null && intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
            if (comp != null) {
                List<ResolveInfo> list = new ArrayList<>(1);
                ProviderInfo pi = getProviderInfo(comp, flags, userId);
                if (pi != null) {
                    ResolveInfo ri = new ResolveInfo();
                    ri.providerInfo = pi;
                    list.add(ri);
                }
                return list;
            }
            synchronized (this.mPackages) {
                String pkgName = intent.getPackage();
                if (pkgName == null) {
                    return this.mProviders.queryIntent(intent, resolvedType, flags, userId);
                }
                PackageParser.Package pkg = this.mPackages.get(pkgName);
                if (pkg != null) {
                    return this.mProviders.queryIntentForPackage(intent, resolvedType, flags, pkg.providers, userId);
                }
                return null;
            }
        }
        return Collections.emptyList();
    }

    @Override // android.content.pm.IPackageManager
    public ParceledListSlice<PackageInfo> getInstalledPackages(int flags, int userId) {
        ArrayList<PackageInfo> list;
        ParceledListSlice<PackageInfo> parceledListSlice;
        PackageInfo pi;
        boolean listUninstalled = (flags & 8192) != 0;
        enforceCrossUserPermission(Binder.getCallingUid(), userId, true, "get installed packages");
        synchronized (this.mPackages) {
            if (listUninstalled) {
                list = new ArrayList<>(this.mSettings.mPackages.size());
                for (PackageSetting ps : this.mSettings.mPackages.values()) {
                    if (ps.pkg != null) {
                        pi = generatePackageInfo(ps.pkg, flags, userId);
                    } else {
                        pi = generatePackageInfoFromSettingsLPw(ps.name, flags, userId);
                    }
                    if (pi != null) {
                        list.add(pi);
                    }
                }
            } else {
                list = new ArrayList<>(this.mPackages.size());
                for (PackageParser.Package p : this.mPackages.values()) {
                    PackageInfo pi2 = generatePackageInfo(p, flags, userId);
                    if (pi2 != null) {
                        list.add(pi2);
                    }
                }
            }
            parceledListSlice = new ParceledListSlice<>(list);
        }
        return parceledListSlice;
    }

    private void addPackageHoldingPermissions(ArrayList<PackageInfo> list, PackageSetting ps, String[] permissions, boolean[] tmp, int flags, int userId) {
        PackageInfo pi;
        int numMatch = 0;
        GrantedPermissions gp = ps.sharedUser != null ? ps.sharedUser : ps;
        for (int i = 0; i < permissions.length; i++) {
            if (gp.grantedPermissions.contains(permissions[i])) {
                tmp[i] = true;
                numMatch++;
            } else {
                tmp[i] = false;
            }
        }
        if (numMatch == 0) {
            return;
        }
        if (ps.pkg != null) {
            pi = generatePackageInfo(ps.pkg, flags, userId);
        } else {
            pi = generatePackageInfoFromSettingsLPw(ps.name, flags, userId);
        }
        if ((flags & 4096) == 0) {
            if (numMatch == permissions.length) {
                pi.requestedPermissions = permissions;
            } else {
                pi.requestedPermissions = new String[numMatch];
                int numMatch2 = 0;
                for (int i2 = 0; i2 < permissions.length; i2++) {
                    if (tmp[i2]) {
                        pi.requestedPermissions[numMatch2] = permissions[i2];
                        numMatch2++;
                    }
                }
            }
        }
        list.add(pi);
    }

    @Override // android.content.pm.IPackageManager
    public ParceledListSlice<PackageInfo> getPackagesHoldingPermissions(String[] permissions, int flags, int userId) {
        ParceledListSlice<PackageInfo> parceledListSlice;
        if (sUserManager.exists(userId)) {
            boolean listUninstalled = (flags & 8192) != 0;
            synchronized (this.mPackages) {
                ArrayList<PackageInfo> list = new ArrayList<>();
                boolean[] tmpBools = new boolean[permissions.length];
                if (listUninstalled) {
                    for (PackageSetting ps : this.mSettings.mPackages.values()) {
                        addPackageHoldingPermissions(list, ps, permissions, tmpBools, flags, userId);
                    }
                } else {
                    for (PackageParser.Package pkg : this.mPackages.values()) {
                        PackageSetting ps2 = (PackageSetting) pkg.mExtras;
                        if (ps2 != null) {
                            addPackageHoldingPermissions(list, ps2, permissions, tmpBools, flags, userId);
                        }
                    }
                }
                parceledListSlice = new ParceledListSlice<>(list);
            }
            return parceledListSlice;
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public ParceledListSlice<ApplicationInfo> getInstalledApplications(int flags, int userId) {
        ArrayList<ApplicationInfo> list;
        ApplicationInfo ai;
        ParceledListSlice<ApplicationInfo> parceledListSlice;
        ApplicationInfo ai2;
        if (sUserManager.exists(userId)) {
            boolean listUninstalled = (flags & 8192) != 0;
            synchronized (this.mPackages) {
                if (listUninstalled) {
                    list = new ArrayList<>(this.mSettings.mPackages.size());
                    for (PackageSetting ps : this.mSettings.mPackages.values()) {
                        if (ps.pkg != null) {
                            ai2 = PackageParser.generateApplicationInfo(ps.pkg, flags, ps.readUserState(userId), userId);
                        } else {
                            ai2 = generateApplicationInfoFromSettingsLPw(ps.name, flags, userId);
                        }
                        if (ai2 != null) {
                            list.add(ai2);
                        }
                    }
                } else {
                    list = new ArrayList<>(this.mPackages.size());
                    for (PackageParser.Package p : this.mPackages.values()) {
                        if (p.mExtras != null && (ai = PackageParser.generateApplicationInfo(p, flags, ((PackageSetting) p.mExtras).readUserState(userId), userId)) != null) {
                            list.add(ai);
                        }
                    }
                }
                parceledListSlice = new ParceledListSlice<>(list);
            }
            return parceledListSlice;
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    public List<ApplicationInfo> getPersistentApplications(int flags) {
        PackageSetting ps;
        ApplicationInfo ai;
        ArrayList<ApplicationInfo> finalList = new ArrayList<>();
        synchronized (this.mPackages) {
            int userId = UserHandle.getCallingUserId();
            for (PackageParser.Package p : this.mPackages.values()) {
                if (p.applicationInfo != null && (p.applicationInfo.flags & 8) != 0 && ((!this.mSafeMode || isSystemApp(p)) && (ps = this.mSettings.mPackages.get(p.packageName)) != null && (ai = PackageParser.generateApplicationInfo(p, flags, ps.readUserState(userId), userId)) != null)) {
                    finalList.add(ai);
                }
            }
        }
        return finalList;
    }

    @Override // android.content.pm.IPackageManager
    public ProviderInfo resolveContentProvider(String name, int flags, int userId) {
        ProviderInfo generateProviderInfo;
        if (sUserManager.exists(userId)) {
            synchronized (this.mPackages) {
                PackageParser.Provider provider = this.mProvidersByAuthority.get(name);
                PackageSetting ps = provider != null ? this.mSettings.mPackages.get(provider.owner.packageName) : null;
                generateProviderInfo = (ps == null || !this.mSettings.isEnabledLPr(provider.info, flags, userId) || (this.mSafeMode && (provider.info.applicationInfo.flags & 1) == 0)) ? null : PackageParser.generateProviderInfo(provider, flags, ps.readUserState(userId), userId);
            }
            return generateProviderInfo;
        }
        return null;
    }

    @Override // android.content.pm.IPackageManager
    @Deprecated
    public void querySyncProviders(List<String> outNames, List<ProviderInfo> outInfo) {
        ProviderInfo info;
        synchronized (this.mPackages) {
            int userId = UserHandle.getCallingUserId();
            for (Map.Entry<String, PackageParser.Provider> entry : this.mProvidersByAuthority.entrySet()) {
                PackageParser.Provider p = entry.getValue();
                PackageSetting ps = this.mSettings.mPackages.get(p.owner.packageName);
                if (ps != null && p.syncable && ((!this.mSafeMode || (p.info.applicationInfo.flags & 1) != 0) && (info = PackageParser.generateProviderInfo(p, 0, ps.readUserState(userId), userId)) != null)) {
                    outNames.add(entry.getKey());
                    outInfo.add(info);
                }
            }
        }
    }

    @Override // android.content.pm.IPackageManager
    public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) {
        ArrayList<ProviderInfo> finalList = null;
        synchronized (this.mPackages) {
            int userId = processName != null ? UserHandle.getUserId(uid) : UserHandle.getCallingUserId();
            for (PackageParser.Provider p : this.mProviders.mProviders.values()) {
                PackageSetting ps = this.mSettings.mPackages.get(p.owner.packageName);
                if (ps != null && p.info.authority != null && ((processName == null || (p.info.processName.equals(processName) && UserHandle.isSameApp(p.info.applicationInfo.uid, uid))) && this.mSettings.isEnabledLPr(p.info, flags, userId) && (!this.mSafeMode || (p.info.applicationInfo.flags & 1) != 0))) {
                    if (finalList == null) {
                        finalList = new ArrayList<>(3);
                    }
                    ProviderInfo info = PackageParser.generateProviderInfo(p, flags, ps.readUserState(userId), userId);
                    if (info != null) {
                        finalList.add(info);
                    }
                }
            }
        }
        if (finalList != null) {
            Collections.sort(finalList, mProviderInitOrderSorter);
        }
        return finalList;
    }

    @Override // android.content.pm.IPackageManager
    public InstrumentationInfo getInstrumentationInfo(ComponentName name, int flags) {
        InstrumentationInfo generateInstrumentationInfo;
        synchronized (this.mPackages) {
            PackageParser.Instrumentation i = this.mInstrumentation.get(name);
            generateInstrumentationInfo = PackageParser.generateInstrumentationInfo(i, flags);
        }
        return generateInstrumentationInfo;
    }

    @Override // android.content.pm.IPackageManager
    public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) {
        InstrumentationInfo ii;
        ArrayList<InstrumentationInfo> finalList = new ArrayList<>();
        synchronized (this.mPackages) {
            for (PackageParser.Instrumentation p : this.mInstrumentation.values()) {
                if ((targetPackage == null || targetPackage.equals(p.info.targetPackage)) && (ii = PackageParser.generateInstrumentationInfo(p, flags)) != null) {
                    finalList.add(ii);
                }
            }
        }
        return finalList;
    }

    private void scanDirLI(File dir, int flags, int scanMode, long currentTime) {
        String[] files = dir.list();
        if (files == null) {
            Log.d(TAG, "No files in app dir " + dir);
            return;
        }
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (isPackageFilename(files[i])) {
                PackageParser.Package pkg = scanPackageLI(file, flags | 4, scanMode, currentTime, (UserHandle) null);
                if (pkg == null && (flags & 1) == 0 && this.mLastScanError == -2) {
                    Slog.w(TAG, "Cleaning up failed install of " + file);
                    file.delete();
                }
            }
        }
    }

    private static File getSettingsProblemFile() {
        File dataDir = Environment.getDataDirectory();
        File systemDir = new File(dataDir, "system");
        File fname = new File(systemDir, "uiderrors.txt");
        return fname;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void reportSettingsProblem(int priority, String msg) {
        try {
            File fname = getSettingsProblemFile();
            FileOutputStream out = new FileOutputStream(fname, true);
            PrintWriter pw = new FastPrintWriter(out);
            SimpleDateFormat formatter = new SimpleDateFormat();
            String dateString = formatter.format(new Date(System.currentTimeMillis()));
            pw.println(dateString + ": " + msg);
            pw.close();
            FileUtils.setPermissions(fname.toString(), 508, -1, -1);
        } catch (IOException e) {
        }
        Slog.println(priority, TAG, msg);
    }

    private boolean collectCertificatesLI(PackageParser pp, PackageSetting ps, PackageParser.Package pkg, File srcFile, int parseFlags) {
        if (ps != null && ps.codePath.equals(srcFile) && ps.timeStamp == srcFile.lastModified()) {
            if (ps.signatures.mSignatures != null && ps.signatures.mSignatures.length != 0) {
                pkg.mSignatures = ps.signatures.mSignatures;
                return true;
            }
            Slog.w(TAG, "PackageSetting for " + ps.name + " is missing signatures.  Collecting certs again to recover them.");
        } else {
            Log.i(TAG, srcFile.toString() + " changed; collecting certs");
        }
        if (!pp.collectCertificates(pkg, parseFlags)) {
            this.mLastScanError = pp.getParseError();
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PackageParser.Package scanPackageLI(File scanFile, int parseFlags, int scanMode, long currentTime, UserHandle user) {
        PackageSetting updatedPkg;
        this.mLastScanError = 1;
        String scanPath = scanFile.getPath();
        int parseFlags2 = parseFlags | this.mDefParseFlags;
        PackageParser pp = new PackageParser(scanPath);
        pp.setSeparateProcesses(this.mSeparateProcesses);
        pp.setOnlyCoreApps(this.mOnlyCore);
        PackageParser.Package pkg = pp.parsePackage(scanFile, scanPath, this.mMetrics, parseFlags2);
        if (pkg == null) {
            this.mLastScanError = pp.getParseError();
            return null;
        }
        PackageSetting ps = null;
        synchronized (this.mPackages) {
            String oldName = this.mSettings.mRenamedPackages.get(pkg.packageName);
            if (pkg.mOriginalPackages != null && pkg.mOriginalPackages.contains(oldName)) {
                ps = this.mSettings.peekPackageLPr(oldName);
            }
            if (ps == null) {
                ps = this.mSettings.peekPackageLPr(pkg.packageName);
            }
            updatedPkg = this.mSettings.getDisabledSystemPkgLPr(ps != null ? ps.name : pkg.packageName);
        }
        if (updatedPkg != null && (parseFlags2 & 1) != 0 && ps != null && !ps.codePath.equals(scanFile)) {
            if (pkg.mVersionCode < ps.versionCode) {
                Log.i(TAG, "Package " + ps.name + " at " + scanFile + " ignored: updated version " + ps.versionCode + " better than this " + pkg.mVersionCode);
                if (!updatedPkg.codePath.equals(scanFile)) {
                    Slog.w(TAG, "Code path for hidden system pkg : " + ps.name + " changing from " + updatedPkg.codePathString + " to " + scanFile);
                    updatedPkg.codePath = scanFile;
                    updatedPkg.codePathString = scanFile.toString();
                    if (locationIsPrivileged(scanFile)) {
                        updatedPkg.pkgFlags |= 1073741824;
                    }
                }
                updatedPkg.pkg = pkg;
                this.mLastScanError = -5;
                return null;
            }
            synchronized (this.mPackages) {
                this.mPackages.remove(ps.name);
            }
            Slog.w(TAG, "Package " + ps.name + " at " + scanFile + "reverting from " + ps.codePathString + ": new version " + pkg.mVersionCode + " better than installed " + ps.versionCode);
            InstallArgs args = createInstallArgs(packageFlagsToInstallFlags(ps), ps.codePathString, ps.resourcePathString, ps.nativeLibraryPathString);
            synchronized (this.mInstallLock) {
                args.cleanUpResourcesLI();
            }
            synchronized (this.mPackages) {
                this.mSettings.enableSystemPackageLPw(ps.name);
            }
        }
        if (updatedPkg != null) {
            parseFlags2 |= 1;
        }
        if (!collectCertificatesLI(pp, ps, pkg, scanFile, parseFlags2)) {
            Slog.w(TAG, "Failed verifying certificates for package:" + pkg.packageName);
            return null;
        }
        boolean shouldHideSystemApp = false;
        if (updatedPkg == null && ps != null && (parseFlags2 & 64) != 0 && !isSystemApp(ps)) {
            if (compareSignatures(ps.signatures.mSignatures, pkg.mSignatures) != 0) {
                deletePackageLI(pkg.packageName, null, true, null, null, 0, null, false);
                ps = null;
            } else if (pkg.mVersionCode < ps.versionCode) {
                shouldHideSystemApp = true;
            } else {
                Slog.w(TAG, "Package " + ps.name + " at " + scanFile + "reverting from " + ps.codePathString + ": new version " + pkg.mVersionCode + " better than installed " + ps.versionCode);
                InstallArgs args2 = createInstallArgs(packageFlagsToInstallFlags(ps), ps.codePathString, ps.resourcePathString, ps.nativeLibraryPathString);
                synchronized (this.mInstallLock) {
                    args2.cleanUpResourcesLI();
                }
            }
        }
        if ((parseFlags2 & 64) == 0 && ps != null && !ps.codePath.equals(ps.resourcePath)) {
            parseFlags2 |= 16;
        }
        String resPath = null;
        if ((parseFlags2 & 16) != 0) {
            if (ps != null && ps.resourcePathString != null) {
                resPath = ps.resourcePathString;
            } else {
                Slog.e(TAG, "Resource path not set for pkg : " + pkg.packageName);
            }
        } else {
            resPath = pkg.mScanPath;
        }
        String codePath = pkg.mScanPath;
        setApplicationInfoPaths(pkg, codePath, resPath);
        PackageParser.Package scannedPkg = scanPackageLI(pkg, parseFlags2, scanMode | 8, currentTime, user);
        if (shouldHideSystemApp) {
            synchronized (this.mPackages) {
                grantPermissionsLPw(pkg, true);
                this.mSettings.disableSystemPackageLPw(pkg.packageName);
            }
        }
        return scannedPkg;
    }

    private static void setApplicationInfoPaths(PackageParser.Package pkg, String destCodePath, String destResPath) {
        pkg.mScanPath = destCodePath;
        pkg.mPath = destCodePath;
        pkg.applicationInfo.sourceDir = destCodePath;
        pkg.applicationInfo.publicSourceDir = destResPath;
    }

    private static String fixProcessName(String defProcessName, String processName, int uid) {
        if (processName == null) {
            return defProcessName;
        }
        return processName;
    }

    private boolean verifySignaturesLP(PackageSetting pkgSetting, PackageParser.Package pkg) {
        if (pkgSetting.signatures.mSignatures != null && compareSignatures(pkgSetting.signatures.mSignatures, pkg.mSignatures) != 0) {
            Slog.e(TAG, "Package " + pkg.packageName + " signatures do not match the previously installed version; ignoring!");
            this.mLastScanError = -7;
            return false;
        } else if (pkgSetting.sharedUser != null && pkgSetting.sharedUser.signatures.mSignatures != null && compareSignatures(pkgSetting.sharedUser.signatures.mSignatures, pkg.mSignatures) != 0) {
            Slog.e(TAG, "Package " + pkg.packageName + " has no signatures that match those in shared user " + pkgSetting.sharedUser.name + "; ignoring!");
            this.mLastScanError = -8;
            return false;
        } else {
            return true;
        }
    }

    private static final void enforceSystemOrRoot(String message) {
        int uid = Binder.getCallingUid();
        if (uid != 1000 && uid != 0) {
            throw new SecurityException(message);
        }
    }

    @Override // android.content.pm.IPackageManager
    public void performBootDexOpt() {
        HashSet<PackageParser.Package> pkgs;
        synchronized (this.mPackages) {
            pkgs = this.mDeferredDexOpt;
            this.mDeferredDexOpt = null;
        }
        if (pkgs != null) {
            int i = 0;
            Iterator i$ = pkgs.iterator();
            while (i$.hasNext()) {
                PackageParser.Package pkg = i$.next();
                if (!isFirstBoot()) {
                    i++;
                    try {
                        ActivityManagerNative.getDefault().showBootMessage(this.mContext.getResources().getString(R.string.android_upgrading_apk, Integer.valueOf(i), Integer.valueOf(pkgs.size())), true);
                    } catch (RemoteException e) {
                    }
                }
                synchronized (this.mInstallLock) {
                    if (!pkg.mDidDexOpt) {
                        performDexOptLI(pkg, false, false, true);
                    }
                }
            }
        }
    }

    @Override // android.content.pm.IPackageManager
    public boolean performDexOpt(String packageName) {
        boolean z;
        enforceSystemOrRoot("Only the system can request dexopt be performed");
        if (!this.mNoDexOpt) {
            return false;
        }
        synchronized (this.mPackages) {
            PackageParser.Package p = this.mPackages.get(packageName);
            if (p == null || p.mDidDexOpt) {
                return false;
            }
            synchronized (this.mInstallLock) {
                z = performDexOptLI(p, false, false, true) == 1;
            }
            return z;
        }
    }

    private void performDexOptLibsLI(ArrayList<String> libs, boolean forceDex, boolean defer, HashSet<String> done) {
        String libName;
        PackageParser.Package libPkg;
        for (int i = 0; i < libs.size(); i++) {
            synchronized (this.mPackages) {
                libName = libs.get(i);
                SharedLibraryEntry lib = this.mSharedLibraries.get(libName);
                if (lib != null && lib.apk != null) {
                    libPkg = this.mPackages.get(lib.apk);
                } else {
                    libPkg = null;
                }
            }
            if (libPkg != null && !done.contains(libName)) {
                performDexOptLI(libPkg, forceDex, defer, done);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0056, code lost:
        if (dalvik.system.DexFile.isDexOptNeeded(r0) != false) goto L14;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int performDexOptLI(android.content.pm.PackageParser.Package r7, boolean r8, boolean r9, java.util.HashSet<java.lang.String> r10) {
        /*
            Method dump skipped, instructions count: 349
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.performDexOptLI(android.content.pm.PackageParser$Package, boolean, boolean, java.util.HashSet):int");
    }

    private int performDexOptLI(PackageParser.Package pkg, boolean forceDex, boolean defer, boolean inclDependencies) {
        HashSet<String> done;
        if (inclDependencies && (pkg.usesLibraries != null || pkg.usesOptionalLibraries != null)) {
            done = new HashSet<>();
            done.add(pkg.packageName);
        } else {
            done = null;
        }
        return performDexOptLI(pkg, forceDex, defer, done);
    }

    private boolean verifyPackageUpdateLPr(PackageSetting oldPkg, PackageParser.Package newPkg) {
        if ((oldPkg.pkgFlags & 1) == 0) {
            Slog.w(TAG, "Unable to update from " + oldPkg.name + " to " + newPkg.packageName + ": old package not in system partition");
            return false;
        } else if (this.mPackages.get(oldPkg.name) != null) {
            Slog.w(TAG, "Unable to update from " + oldPkg.name + " to " + newPkg.packageName + ": old package still exists");
            return false;
        } else {
            return true;
        }
    }

    File getDataPathForUser(int userId) {
        return new File(this.mUserAppDataDir.getAbsolutePath() + File.separator + userId);
    }

    private File getDataPathForPackage(String packageName, int userId) {
        if (userId == 0) {
            return new File(this.mAppDataDir, packageName);
        }
        return new File(this.mUserAppDataDir.getAbsolutePath() + File.separator + userId + File.separator + packageName);
    }

    private int createDataDirsLI(String packageName, int uid, String seinfo) {
        int[] users = sUserManager.getUserIds();
        int res = this.mInstaller.install(packageName, uid, uid, seinfo);
        if (res < 0) {
            return res;
        }
        for (int user : users) {
            if (user != 0) {
                res = this.mInstaller.createUserData(packageName, UserHandle.getUid(user, uid), user);
                if (res < 0) {
                    return res;
                }
            }
        }
        return res;
    }

    private int removeDataDirsLI(String packageName) {
        int[] users = sUserManager.getUserIds();
        int res = 0;
        for (int user : users) {
            int resInner = this.mInstaller.remove(packageName, user);
            if (resInner < 0) {
                res = resInner;
            }
        }
        File nativeLibraryFile = new File(this.mAppLibInstallDir, packageName);
        NativeLibraryHelper.removeNativeBinariesFromDirLI(nativeLibraryFile);
        if (!nativeLibraryFile.delete()) {
            Slog.w(TAG, "Couldn't delete native library directory " + nativeLibraryFile.getPath());
        }
        return res;
    }

    private int addSharedLibraryLPw(SharedLibraryEntry file, int num, PackageParser.Package changingLib) {
        if (file.path != null) {
            this.mTmpSharedLibraries[num] = file.path;
            return num + 1;
        }
        PackageParser.Package p = this.mPackages.get(file.apk);
        if (changingLib != null && changingLib.packageName.equals(file.apk) && (p == null || p.packageName.equals(changingLib.packageName))) {
            p = changingLib;
        }
        if (p != null) {
            String path = p.mPath;
            for (int i = 0; i < num; i++) {
                if (this.mTmpSharedLibraries[i].equals(path)) {
                    return num;
                }
            }
            this.mTmpSharedLibraries[num] = p.mPath;
            return num + 1;
        }
        return num;
    }

    private boolean updateSharedLibrariesLPw(PackageParser.Package pkg, PackageParser.Package changingLib) {
        if (pkg.usesLibraries != null || pkg.usesOptionalLibraries != null) {
            if (this.mTmpSharedLibraries == null || this.mTmpSharedLibraries.length < this.mSharedLibraries.size()) {
                this.mTmpSharedLibraries = new String[this.mSharedLibraries.size()];
            }
            int num = 0;
            int N = pkg.usesLibraries != null ? pkg.usesLibraries.size() : 0;
            for (int i = 0; i < N; i++) {
                SharedLibraryEntry file = this.mSharedLibraries.get(pkg.usesLibraries.get(i));
                if (file == null) {
                    Slog.e(TAG, "Package " + pkg.packageName + " requires unavailable shared library " + pkg.usesLibraries.get(i) + "; failing!");
                    this.mLastScanError = -9;
                    return false;
                }
                num = addSharedLibraryLPw(file, num, changingLib);
            }
            int N2 = pkg.usesOptionalLibraries != null ? pkg.usesOptionalLibraries.size() : 0;
            for (int i2 = 0; i2 < N2; i2++) {
                SharedLibraryEntry file2 = this.mSharedLibraries.get(pkg.usesOptionalLibraries.get(i2));
                if (file2 == null) {
                    Slog.w(TAG, "Package " + pkg.packageName + " desires unavailable shared library " + pkg.usesOptionalLibraries.get(i2) + "; ignoring!");
                } else {
                    num = addSharedLibraryLPw(file2, num, changingLib);
                }
            }
            if (num > 0) {
                pkg.usesLibraryFiles = new String[num];
                System.arraycopy(this.mTmpSharedLibraries, 0, pkg.usesLibraryFiles, 0, num);
                return true;
            }
            pkg.usesLibraryFiles = null;
            return true;
        }
        return true;
    }

    private static boolean hasString(List<String> list, List<String> which) {
        if (list == null) {
            return false;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            for (int j = which.size() - 1; j >= 0; j--) {
                if (which.get(j).equals(list.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateAllSharedLibrariesLPw() {
        for (PackageParser.Package pkg : this.mPackages.values()) {
            updateSharedLibrariesLPw(pkg, null);
        }
    }

    private ArrayList<PackageParser.Package> updateAllSharedLibrariesLPw(PackageParser.Package changingPkg) {
        ArrayList<PackageParser.Package> res = null;
        for (PackageParser.Package pkg : this.mPackages.values()) {
            if (hasString(pkg.usesLibraries, changingPkg.libraryNames) || hasString(pkg.usesOptionalLibraries, changingPkg.libraryNames)) {
                if (res == null) {
                    res = new ArrayList<>();
                }
                res.add(pkg);
                updateSharedLibrariesLPw(pkg, changingPkg);
            }
        }
        return res;
    }

    private PackageParser.Package scanPackageLI(PackageParser.Package pkg, int parseFlags, int scanMode, long currentTime, UserHandle user) {
        File dataPath;
        File scanFile = new File(pkg.mScanPath);
        if (scanFile == null || pkg.applicationInfo.sourceDir == null || pkg.applicationInfo.publicSourceDir == null) {
            Slog.w(TAG, " Code and resource paths haven't been set correctly");
            this.mLastScanError = -2;
            return null;
        }
        this.mScanningPath = scanFile;
        if ((parseFlags & 1) != 0) {
            pkg.applicationInfo.flags |= 1;
        }
        if ((parseFlags & 128) != 0) {
            pkg.applicationInfo.flags |= 1073741824;
        }
        if (this.mCustomResolverComponentName != null && this.mCustomResolverComponentName.getPackageName().equals(pkg.packageName)) {
            setUpCustomResolverActivity(pkg);
        }
        if (pkg.packageName.equals("android")) {
            synchronized (this.mPackages) {
                if (this.mAndroidApplication != null) {
                    Slog.w(TAG, "*************************************************");
                    Slog.w(TAG, "Core android package being redefined.  Skipping.");
                    Slog.w(TAG, " file=" + this.mScanningPath);
                    Slog.w(TAG, "*************************************************");
                    this.mLastScanError = -5;
                    return null;
                }
                this.mPlatformPackage = pkg;
                pkg.mVersionCode = this.mSdkVersion;
                this.mAndroidApplication = pkg.applicationInfo;
                if (!this.mResolverReplaced) {
                    this.mResolveActivity.applicationInfo = this.mAndroidApplication;
                    this.mResolveActivity.name = ResolverActivity.class.getName();
                    this.mResolveActivity.packageName = this.mAndroidApplication.packageName;
                    this.mResolveActivity.processName = "system:ui";
                    this.mResolveActivity.launchMode = 0;
                    this.mResolveActivity.flags = 32;
                    this.mResolveActivity.theme = R.style.Theme_Holo_Dialog_Alert;
                    this.mResolveActivity.exported = true;
                    this.mResolveActivity.enabled = true;
                    this.mResolveInfo.activityInfo = this.mResolveActivity;
                    this.mResolveInfo.priority = 0;
                    this.mResolveInfo.preferredOrder = 0;
                    this.mResolveInfo.match = 0;
                    this.mResolveComponentName = new ComponentName(this.mAndroidApplication.packageName, this.mResolveActivity.name);
                }
            }
        }
        if (this.mPackages.containsKey(pkg.packageName) || this.mSharedLibraries.containsKey(pkg.packageName)) {
            Slog.w(TAG, "Application package " + pkg.packageName + " already installed.  Skipping duplicate.");
            this.mLastScanError = -5;
            return null;
        }
        File destCodeFile = new File(pkg.applicationInfo.sourceDir);
        File destResourceFile = new File(pkg.applicationInfo.publicSourceDir);
        SharedUserSetting suid = null;
        if (!isSystemApp(pkg)) {
            pkg.mOriginalPackages = null;
            pkg.mRealPackage = null;
            pkg.mAdoptPermissions = null;
        }
        synchronized (this.mPackages) {
            if ((parseFlags & 64) == 0 && !updateSharedLibrariesLPw(pkg, null)) {
                return null;
            }
            if (pkg.mSharedUserId != null) {
                suid = this.mSettings.getSharedUserLPw(pkg.mSharedUserId, 0, true);
                if (suid == null) {
                    Slog.w(TAG, "Creating application package " + pkg.packageName + " for shared user failed");
                    this.mLastScanError = -4;
                    return null;
                }
            }
            PackageSetting origPackage = null;
            String realName = null;
            if (pkg.mOriginalPackages != null) {
                String renamed = this.mSettings.mRenamedPackages.get(pkg.mRealPackage);
                if (pkg.mOriginalPackages.contains(renamed)) {
                    realName = pkg.mRealPackage;
                    if (!pkg.packageName.equals(renamed)) {
                        pkg.setPackageName(renamed);
                    }
                } else {
                    for (int i = pkg.mOriginalPackages.size() - 1; i >= 0; i--) {
                        PackageSetting peekPackageLPr = this.mSettings.peekPackageLPr(pkg.mOriginalPackages.get(i));
                        origPackage = peekPackageLPr;
                        if (peekPackageLPr != null) {
                            if (!verifyPackageUpdateLPr(origPackage, pkg)) {
                                origPackage = null;
                            } else if (origPackage.sharedUser == null || origPackage.sharedUser.name.equals(pkg.mSharedUserId)) {
                                break;
                            } else {
                                Slog.w(TAG, "Unable to migrate data from " + origPackage.name + " to " + pkg.packageName + ": old uid " + origPackage.sharedUser.name + " differs from " + pkg.mSharedUserId);
                                origPackage = null;
                            }
                        }
                    }
                }
            }
            if (this.mTransferedPackages.contains(pkg.packageName)) {
                Slog.w(TAG, "Package " + pkg.packageName + " was transferred to another, but its .apk remains");
            }
            PackageSetting pkgSetting = this.mSettings.getPackageLPw(pkg, origPackage, realName, suid, destCodeFile, destResourceFile, pkg.applicationInfo.nativeLibraryDir, pkg.applicationInfo.flags, user, false);
            if (pkgSetting == null) {
                Slog.w(TAG, "Creating application package " + pkg.packageName + " failed");
                this.mLastScanError = -4;
                return null;
            }
            if (pkgSetting.origPackage != null) {
                pkg.setPackageName(origPackage.name);
                reportSettingsProblem(5, "New package " + pkgSetting.realName + " renamed to replace old package " + pkgSetting.name);
                this.mTransferedPackages.add(origPackage.name);
                pkgSetting.origPackage = null;
            }
            if (realName != null) {
                this.mTransferedPackages.add(pkg.packageName);
            }
            if (this.mSettings.isDisabledSystemPackageLPr(pkg.packageName)) {
                pkg.applicationInfo.flags |= 128;
            }
            if (this.mFoundPolicyFile) {
                SELinuxMMAC.assignSeinfoValue(pkg);
            }
            pkg.applicationInfo.uid = pkgSetting.appId;
            pkg.mExtras = pkgSetting;
            if (!verifySignaturesLP(pkgSetting, pkg)) {
                if ((parseFlags & 64) == 0) {
                    return null;
                }
                pkgSetting.signatures.mSignatures = pkg.mSignatures;
                if (pkgSetting.sharedUser != null && compareSignatures(pkgSetting.sharedUser.signatures.mSignatures, pkg.mSignatures) != 0) {
                    Log.w(TAG, "Signature mismatch for shared user : " + pkgSetting.sharedUser);
                    this.mLastScanError = PackageManager.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES;
                    return null;
                }
                reportSettingsProblem(5, "System package " + pkg.packageName + " signature changed; retaining data.");
            }
            if ((scanMode & 16) != 0) {
                int N = pkg.providers.size();
                for (int i2 = 0; i2 < N; i2++) {
                    PackageParser.Provider p = pkg.providers.get(i2);
                    if (p.info.authority != null) {
                        String[] names = p.info.authority.split(Separators.SEMICOLON);
                        for (int j = 0; j < names.length; j++) {
                            if (this.mProvidersByAuthority.containsKey(names[j])) {
                                PackageParser.Provider other = this.mProvidersByAuthority.get(names[j]);
                                Slog.w(TAG, "Can't install because provider name " + names[j] + " (in package " + pkg.applicationInfo.packageName + ") is already used by " + ((other == null || other.getComponentName() == null) ? Separators.QUESTION : other.getComponentName().getPackageName()));
                                this.mLastScanError = -13;
                                return null;
                            }
                        }
                        continue;
                    }
                }
            }
            if (pkg.mAdoptPermissions != null) {
                for (int i3 = pkg.mAdoptPermissions.size() - 1; i3 >= 0; i3--) {
                    String origName = pkg.mAdoptPermissions.get(i3);
                    PackageSetting orig = this.mSettings.peekPackageLPr(origName);
                    if (orig != null && verifyPackageUpdateLPr(orig, pkg)) {
                        Slog.i(TAG, "Adopting permissions from " + origName + " to " + pkg.packageName);
                        this.mSettings.transferPermissionsLPw(origName, pkg.packageName);
                    }
                }
            }
            String pkgName = pkg.packageName;
            long scanFileTime = scanFile.lastModified();
            boolean forceDex = (scanMode & 4) != 0;
            pkg.applicationInfo.processName = fixProcessName(pkg.applicationInfo.packageName, pkg.applicationInfo.processName, pkg.applicationInfo.uid);
            if (this.mPlatformPackage == pkg) {
                dataPath = new File(Environment.getDataDirectory(), "system");
                pkg.applicationInfo.dataDir = dataPath.getPath();
            } else {
                dataPath = getDataPathForPackage(pkg.packageName, 0);
                boolean uidError = false;
                if (dataPath.exists()) {
                    int currentUid = 0;
                    try {
                        StructStat stat = Libcore.os.stat(dataPath.getPath());
                        currentUid = stat.st_uid;
                    } catch (ErrnoException e) {
                        Slog.e(TAG, "Couldn't stat path " + dataPath.getPath(), e);
                    }
                    if (currentUid != pkg.applicationInfo.uid) {
                        boolean recovered = false;
                        if (currentUid == 0) {
                            int ret = this.mInstaller.fixUid(pkgName, pkg.applicationInfo.uid, pkg.applicationInfo.uid);
                            if (ret >= 0) {
                                recovered = true;
                                reportSettingsProblem(5, "Package " + pkg.packageName + " unexpectedly changed to uid 0; recovered to " + pkg.applicationInfo.uid);
                            }
                        }
                        if (!recovered && ((parseFlags & 1) != 0 || (scanMode & 256) != 0)) {
                            int ret2 = removeDataDirsLI(pkgName);
                            if (ret2 >= 0) {
                                String prefix = (parseFlags & 1) != 0 ? "System package " : "Third party package ";
                                reportSettingsProblem(5, prefix + pkg.packageName + " has changed from uid: " + currentUid + " to " + pkg.applicationInfo.uid + "; old data erased");
                                recovered = true;
                                int ret3 = createDataDirsLI(pkgName, pkg.applicationInfo.uid, pkg.applicationInfo.seinfo);
                                if (ret3 == -1) {
                                    reportSettingsProblem(5, prefix + pkg.packageName + " could not have data directory re-created after delete.");
                                    this.mLastScanError = -4;
                                    return null;
                                }
                            }
                            if (!recovered) {
                                this.mHasSystemUidErrors = true;
                            }
                        } else if (!recovered) {
                            this.mLastScanError = -24;
                            return null;
                        }
                        if (!recovered) {
                            pkg.applicationInfo.dataDir = "/mismatched_uid/settings_" + pkg.applicationInfo.uid + "/fs_" + currentUid;
                            pkg.applicationInfo.nativeLibraryDir = pkg.applicationInfo.dataDir;
                            String msg = "Package " + pkg.packageName + " has mismatched uid: " + currentUid + " on disk, " + pkg.applicationInfo.uid + " in settings";
                            synchronized (this.mPackages) {
                                this.mSettings.mReadMessages.append(msg);
                                this.mSettings.mReadMessages.append('\n');
                                uidError = true;
                                if (!pkgSetting.uidError) {
                                    reportSettingsProblem(6, msg);
                                }
                            }
                        }
                    }
                    pkg.applicationInfo.dataDir = dataPath.getPath();
                } else {
                    int ret4 = createDataDirsLI(pkgName, pkg.applicationInfo.uid, pkg.applicationInfo.seinfo);
                    if (ret4 < 0) {
                        this.mLastScanError = -4;
                        return null;
                    } else if (dataPath.exists()) {
                        pkg.applicationInfo.dataDir = dataPath.getPath();
                    } else {
                        Slog.w(TAG, "Unable to create data directory: " + dataPath);
                        pkg.applicationInfo.dataDir = null;
                    }
                }
                if (pkg.applicationInfo.nativeLibraryDir == null && pkg.applicationInfo.dataDir != null) {
                    if (pkgSetting.nativeLibraryPathString == null) {
                        setInternalAppNativeLibraryPath(pkg, pkgSetting);
                    } else {
                        pkg.applicationInfo.nativeLibraryDir = pkgSetting.nativeLibraryPathString;
                    }
                }
                pkgSetting.uidError = uidError;
            }
            String path = scanFile.getPath();
            if (pkg.applicationInfo.nativeLibraryDir != null) {
                try {
                    File nativeLibraryDir = new File(pkg.applicationInfo.nativeLibraryDir);
                    String dataPathString = dataPath.getCanonicalPath();
                    if (isSystemApp(pkg) && !isUpdatedSystemApp(pkg)) {
                        if (NativeLibraryHelper.removeNativeBinariesFromDirLI(nativeLibraryDir)) {
                            Log.i(TAG, "removed obsolete native libraries for system package " + path);
                        }
                    } else {
                        if (!isForwardLocked(pkg) && !isExternal(pkg)) {
                            if (nativeLibraryDir.getPath().startsWith(dataPathString)) {
                                setInternalAppNativeLibraryPath(pkg, pkgSetting);
                                nativeLibraryDir = new File(pkg.applicationInfo.nativeLibraryDir);
                            }
                            try {
                                if (copyNativeLibrariesForInternalApp(scanFile, nativeLibraryDir) != 1) {
                                    Slog.e(TAG, "Unable to copy native libraries");
                                    this.mLastScanError = -110;
                                    return null;
                                }
                            } catch (IOException e2) {
                                Slog.e(TAG, "Unable to copy native libraries", e2);
                                this.mLastScanError = -110;
                                return null;
                            }
                        }
                        int[] userIds = sUserManager.getUserIds();
                        synchronized (this.mInstallLock) {
                            for (int userId : userIds) {
                                if (this.mInstaller.linkNativeLibraryDirectory(pkg.packageName, pkg.applicationInfo.nativeLibraryDir, userId) < 0) {
                                    Slog.w(TAG, "Failed linking native library dir (user=" + userId + Separators.RPAREN);
                                    this.mLastScanError = -110;
                                    return null;
                                }
                            }
                        }
                    }
                } catch (IOException ioe) {
                    Slog.e(TAG, "Unable to get canonical file " + ioe.toString());
                }
            }
            pkg.mScanPath = path;
            if ((scanMode & 2) == 0) {
                if (performDexOptLI(pkg, forceDex, (scanMode & 128) != 0, false) == -1) {
                    this.mLastScanError = -11;
                    return null;
                }
            }
            if (this.mFactoryTest && pkg.requestedPermissions.contains(Manifest.permission.FACTORY_TEST)) {
                pkg.applicationInfo.flags |= 16;
            }
            ArrayList<PackageParser.Package> clientLibPkgs = null;
            synchronized (this.mPackages) {
                if ((pkg.applicationInfo.flags & 1) != 0 && pkg.libraryNames != null) {
                    for (int i4 = 0; i4 < pkg.libraryNames.size(); i4++) {
                        String name = pkg.libraryNames.get(i4);
                        boolean allowed = false;
                        if (isUpdatedSystemApp(pkg)) {
                            PackageSetting sysPs = this.mSettings.getDisabledSystemPkgLPr(pkg.packageName);
                            if (sysPs.pkg != null && sysPs.pkg.libraryNames != null) {
                                int j2 = 0;
                                while (true) {
                                    if (j2 < sysPs.pkg.libraryNames.size()) {
                                        if (!name.equals(sysPs.pkg.libraryNames.get(j2))) {
                                            j2++;
                                        } else {
                                            allowed = true;
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                        } else {
                            allowed = true;
                        }
                        if (allowed) {
                            if (!this.mSharedLibraries.containsKey(name)) {
                                this.mSharedLibraries.put(name, new SharedLibraryEntry(null, pkg.packageName));
                            } else if (!name.equals(pkg.packageName)) {
                                Slog.w(TAG, "Package " + pkg.packageName + " library " + name + " already exists; skipping");
                            }
                        } else {
                            Slog.w(TAG, "Package " + pkg.packageName + " declares lib " + name + " that is not declared on system image; skipping");
                        }
                    }
                    if ((scanMode & 256) == 0) {
                        clientLibPkgs = updateAllSharedLibrariesLPw(pkg);
                    }
                }
            }
            if (clientLibPkgs != null && (scanMode & 2) == 0) {
                for (int i5 = 0; i5 < clientLibPkgs.size(); i5++) {
                    if (performDexOptLI(clientLibPkgs.get(i5), forceDex, (scanMode & 128) != 0, false) == -1) {
                        this.mLastScanError = -11;
                        return null;
                    }
                }
            }
            if ((parseFlags & 2) != 0) {
                if (isForwardLocked(pkg) || isExternal(pkg)) {
                    int[] uidArray = {pkg.applicationInfo.uid};
                    ArrayList<String> pkgList = new ArrayList<>(1);
                    pkgList.add(pkg.applicationInfo.packageName);
                    sendResourcesChangedBroadcast(false, true, pkgList, uidArray, null);
                }
                killApplication(pkg.applicationInfo.packageName, pkg.applicationInfo.uid, "update pkg");
            }
            if (clientLibPkgs != null) {
                for (int i6 = 0; i6 < clientLibPkgs.size(); i6++) {
                    PackageParser.Package clientPkg = clientLibPkgs.get(i6);
                    killApplication(clientPkg.applicationInfo.packageName, clientPkg.applicationInfo.uid, "update lib");
                }
            }
            synchronized (this.mPackages) {
                if ((scanMode & 1) != 0) {
                    this.mAppDirs.put(pkg.mPath, pkg);
                }
                this.mSettings.insertPackageSettingLPw(pkgSetting, pkg);
                this.mPackages.put(pkg.applicationInfo.packageName, pkg);
                Iterator<PackageCleanItem> iter = this.mSettings.mPackagesToBeCleaned.iterator();
                while (iter.hasNext()) {
                    PackageCleanItem item = iter.next();
                    if (pkgName.equals(item.packageName)) {
                        iter.remove();
                    }
                }
                if (currentTime != 0) {
                    if (pkgSetting.firstInstallTime == 0) {
                        pkgSetting.lastUpdateTime = currentTime;
                        pkgSetting.firstInstallTime = currentTime;
                    } else if ((scanMode & 64) != 0) {
                        pkgSetting.lastUpdateTime = currentTime;
                    }
                } else if (pkgSetting.firstInstallTime == 0) {
                    pkgSetting.lastUpdateTime = scanFileTime;
                    pkgSetting.firstInstallTime = scanFileTime;
                } else if ((parseFlags & 64) != 0 && scanFileTime != pkgSetting.timeStamp) {
                    pkgSetting.lastUpdateTime = scanFileTime;
                }
                KeySetManager ksm = this.mSettings.mKeySetManager;
                try {
                    ksm.addSigningKeySetToPackage(pkg.packageName, pkg.mSigningKeys);
                    if (pkg.mKeySetMapping != null) {
                        for (Map.Entry<String, Set<PublicKey>> entry : pkg.mKeySetMapping.entrySet()) {
                            if (entry.getValue() != null) {
                                ksm.addDefinedKeySetToPackage(pkg.packageName, entry.getValue(), entry.getKey());
                            }
                        }
                    }
                } catch (IllegalArgumentException e3) {
                    Slog.e(TAG, "Could not add KeySet to malformed package" + pkg.packageName, e3);
                } catch (NullPointerException e4) {
                    Slog.e(TAG, "Could not add KeySet to " + pkg.packageName, e4);
                }
                int N2 = pkg.providers.size();
                StringBuilder r = null;
                for (int i7 = 0; i7 < N2; i7++) {
                    PackageParser.Provider p2 = pkg.providers.get(i7);
                    p2.info.processName = fixProcessName(pkg.applicationInfo.processName, p2.info.processName, pkg.applicationInfo.uid);
                    this.mProviders.addProvider(p2);
                    p2.syncable = p2.info.isSyncable;
                    if (p2.info.authority != null) {
                        String[] names2 = p2.info.authority.split(Separators.SEMICOLON);
                        p2.info.authority = null;
                        for (int j3 = 0; j3 < names2.length; j3++) {
                            if (j3 == 1 && p2.syncable) {
                                p2 = new PackageParser.Provider(p2);
                                p2.syncable = false;
                            }
                            if (!this.mProvidersByAuthority.containsKey(names2[j3])) {
                                this.mProvidersByAuthority.put(names2[j3], p2);
                                if (p2.info.authority == null) {
                                    p2.info.authority = names2[j3];
                                } else {
                                    p2.info.authority += Separators.SEMICOLON + names2[j3];
                                }
                            } else {
                                PackageParser.Provider other2 = this.mProvidersByAuthority.get(names2[j3]);
                                Slog.w(TAG, "Skipping provider name " + names2[j3] + " (in package " + pkg.applicationInfo.packageName + "): name already used by " + ((other2 == null || other2.getComponentName() == null) ? Separators.QUESTION : other2.getComponentName().getPackageName()));
                            }
                        }
                    }
                    if ((parseFlags & 2) != 0) {
                        if (r == null) {
                            r = new StringBuilder(256);
                        } else {
                            r.append(' ');
                        }
                        r.append(p2.info.name);
                    }
                }
                if (r != null) {
                }
                int N3 = pkg.services.size();
                StringBuilder r2 = null;
                for (int i8 = 0; i8 < N3; i8++) {
                    PackageParser.Service s = pkg.services.get(i8);
                    s.info.processName = fixProcessName(pkg.applicationInfo.processName, s.info.processName, pkg.applicationInfo.uid);
                    this.mServices.addService(s);
                    if ((parseFlags & 2) != 0) {
                        if (r2 == null) {
                            r2 = new StringBuilder(256);
                        } else {
                            r2.append(' ');
                        }
                        r2.append(s.info.name);
                    }
                }
                if (r2 != null) {
                }
                int N4 = pkg.receivers.size();
                StringBuilder r3 = null;
                for (int i9 = 0; i9 < N4; i9++) {
                    PackageParser.Activity a = pkg.receivers.get(i9);
                    a.info.processName = fixProcessName(pkg.applicationInfo.processName, a.info.processName, pkg.applicationInfo.uid);
                    this.mReceivers.addActivity(a, "receiver");
                    if ((parseFlags & 2) != 0) {
                        if (r3 == null) {
                            r3 = new StringBuilder(256);
                        } else {
                            r3.append(' ');
                        }
                        r3.append(a.info.name);
                    }
                }
                if (r3 != null) {
                }
                int N5 = pkg.activities.size();
                StringBuilder r4 = null;
                for (int i10 = 0; i10 < N5; i10++) {
                    PackageParser.Activity a2 = pkg.activities.get(i10);
                    a2.info.processName = fixProcessName(pkg.applicationInfo.processName, a2.info.processName, pkg.applicationInfo.uid);
                    this.mActivities.addActivity(a2, Context.ACTIVITY_SERVICE);
                    if ((parseFlags & 2) != 0) {
                        if (r4 == null) {
                            r4 = new StringBuilder(256);
                        } else {
                            r4.append(' ');
                        }
                        r4.append(a2.info.name);
                    }
                }
                if (r4 != null) {
                }
                int N6 = pkg.permissionGroups.size();
                StringBuilder r5 = null;
                for (int i11 = 0; i11 < N6; i11++) {
                    PackageParser.PermissionGroup pg = pkg.permissionGroups.get(i11);
                    PackageParser.PermissionGroup cur = this.mPermissionGroups.get(pg.info.name);
                    if (cur == null) {
                        this.mPermissionGroups.put(pg.info.name, pg);
                        if ((parseFlags & 2) != 0) {
                            if (r5 == null) {
                                r5 = new StringBuilder(256);
                            } else {
                                r5.append(' ');
                            }
                            r5.append(pg.info.name);
                        }
                    } else {
                        Slog.w(TAG, "Permission group " + pg.info.name + " from package " + pg.info.packageName + " ignored: original from " + cur.info.packageName);
                        if ((parseFlags & 2) != 0) {
                            if (r5 == null) {
                                r5 = new StringBuilder(256);
                            } else {
                                r5.append(' ');
                            }
                            r5.append("DUP:");
                            r5.append(pg.info.name);
                        }
                    }
                }
                if (r5 != null) {
                }
                int N7 = pkg.permissions.size();
                StringBuilder r6 = null;
                for (int i12 = 0; i12 < N7; i12++) {
                    PackageParser.Permission p3 = pkg.permissions.get(i12);
                    HashMap<String, BasePermission> permissionMap = p3.tree ? this.mSettings.mPermissionTrees : this.mSettings.mPermissions;
                    p3.group = this.mPermissionGroups.get(p3.info.group);
                    if (p3.info.group == null || p3.group != null) {
                        BasePermission bp = permissionMap.get(p3.info.name);
                        if (bp == null) {
                            bp = new BasePermission(p3.info.name, p3.info.packageName, 0);
                            permissionMap.put(p3.info.name, bp);
                        }
                        if (bp.perm == null) {
                            if (bp.sourcePackage == null || bp.sourcePackage.equals(p3.info.packageName)) {
                                BasePermission tree = findPermissionTreeLP(p3.info.name);
                                if (tree == null || tree.sourcePackage.equals(p3.info.packageName)) {
                                    bp.packageSetting = pkgSetting;
                                    bp.perm = p3;
                                    bp.uid = pkg.applicationInfo.uid;
                                    if ((parseFlags & 2) != 0) {
                                        if (r6 == null) {
                                            r6 = new StringBuilder(256);
                                        } else {
                                            r6.append(' ');
                                        }
                                        r6.append(p3.info.name);
                                    }
                                } else {
                                    Slog.w(TAG, "Permission " + p3.info.name + " from package " + p3.info.packageName + " ignored: base tree " + tree.name + " is from package " + tree.sourcePackage);
                                }
                            } else {
                                Slog.w(TAG, "Permission " + p3.info.name + " from package " + p3.info.packageName + " ignored: original from " + bp.sourcePackage);
                            }
                        } else if ((parseFlags & 2) != 0) {
                            if (r6 == null) {
                                r6 = new StringBuilder(256);
                            } else {
                                r6.append(' ');
                            }
                            r6.append("DUP:");
                            r6.append(p3.info.name);
                        }
                        if (bp.perm == p3) {
                            bp.protectionLevel = p3.info.protectionLevel;
                        }
                    } else {
                        Slog.w(TAG, "Permission " + p3.info.name + " from package " + p3.info.packageName + " ignored: no group " + p3.group);
                    }
                }
                if (r6 != null) {
                }
                int N8 = pkg.instrumentation.size();
                StringBuilder r7 = null;
                for (int i13 = 0; i13 < N8; i13++) {
                    PackageParser.Instrumentation a3 = pkg.instrumentation.get(i13);
                    a3.info.packageName = pkg.applicationInfo.packageName;
                    a3.info.sourceDir = pkg.applicationInfo.sourceDir;
                    a3.info.publicSourceDir = pkg.applicationInfo.publicSourceDir;
                    a3.info.dataDir = pkg.applicationInfo.dataDir;
                    a3.info.nativeLibraryDir = pkg.applicationInfo.nativeLibraryDir;
                    this.mInstrumentation.put(a3.getComponentName(), a3);
                    if ((parseFlags & 2) != 0) {
                        if (r7 == null) {
                            r7 = new StringBuilder(256);
                        } else {
                            r7.append(' ');
                        }
                        r7.append(a3.info.name);
                    }
                }
                if (r7 != null) {
                }
                if (pkg.protectedBroadcasts != null) {
                    int N9 = pkg.protectedBroadcasts.size();
                    for (int i14 = 0; i14 < N9; i14++) {
                        this.mProtectedBroadcasts.add(pkg.protectedBroadcasts.get(i14));
                    }
                }
                pkgSetting.setTimeStamp(scanFileTime);
            }
            return pkg;
        }
    }

    private void setUpCustomResolverActivity(PackageParser.Package pkg) {
        synchronized (this.mPackages) {
            this.mResolverReplaced = true;
            this.mResolveActivity.applicationInfo = pkg.applicationInfo;
            this.mResolveActivity.name = this.mCustomResolverComponentName.getClassName();
            this.mResolveActivity.packageName = pkg.applicationInfo.packageName;
            this.mResolveActivity.processName = null;
            this.mResolveActivity.launchMode = 0;
            this.mResolveActivity.flags = MediaProperties.HEIGHT_288;
            this.mResolveActivity.theme = 0;
            this.mResolveActivity.exported = true;
            this.mResolveActivity.enabled = true;
            this.mResolveInfo.activityInfo = this.mResolveActivity;
            this.mResolveInfo.priority = 0;
            this.mResolveInfo.preferredOrder = 0;
            this.mResolveInfo.match = 0;
            this.mResolveComponentName = this.mCustomResolverComponentName;
            Slog.i(TAG, "Replacing default ResolverActivity with custom activity: " + this.mResolveComponentName);
        }
    }

    private void setInternalAppNativeLibraryPath(PackageParser.Package pkg, PackageSetting pkgSetting) {
        String apkLibPath = getApkName(pkgSetting.codePathString);
        String nativeLibraryPath = new File(this.mAppLibInstallDir, apkLibPath).getPath();
        pkg.applicationInfo.nativeLibraryDir = nativeLibraryPath;
        pkgSetting.nativeLibraryPathString = nativeLibraryPath;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int copyNativeLibrariesForInternalApp(File scanFile, File nativeLibraryDir) throws IOException {
        if (!nativeLibraryDir.isDirectory()) {
            nativeLibraryDir.delete();
            if (!nativeLibraryDir.mkdir()) {
                throw new IOException("Cannot create " + nativeLibraryDir.getPath());
            }
            try {
                Libcore.os.chmod(nativeLibraryDir.getPath(), OsConstants.S_IRWXU | OsConstants.S_IRGRP | OsConstants.S_IXGRP | OsConstants.S_IROTH | OsConstants.S_IXOTH);
            } catch (ErrnoException e) {
                throw new IOException("Cannot chmod native library directory " + nativeLibraryDir.getPath(), e);
            }
        } else if (!SELinux.restorecon(nativeLibraryDir)) {
            throw new IOException("Cannot set SELinux context for " + nativeLibraryDir.getPath());
        }
        return NativeLibraryHelper.copyNativeBinariesIfNeededLI(scanFile, nativeLibraryDir);
    }

    private void killApplication(String pkgName, int appId, String reason) {
        IActivityManager am = ActivityManagerNative.getDefault();
        if (am != null) {
            try {
                am.killApplicationWithAppId(pkgName, appId, reason);
            } catch (RemoteException e) {
            }
        }
    }

    void removePackageLI(PackageSetting ps, boolean chatty) {
        synchronized (this.mPackages) {
            this.mPackages.remove(ps.name);
            if (ps.codePathString != null) {
                this.mAppDirs.remove(ps.codePathString);
            }
            PackageParser.Package pkg = ps.pkg;
            if (pkg != null) {
                cleanPackageDataStructuresLILPw(pkg, chatty);
            }
        }
    }

    void removeInstalledPackageLI(PackageParser.Package pkg, boolean chatty) {
        synchronized (this.mPackages) {
            this.mPackages.remove(pkg.applicationInfo.packageName);
            if (pkg.mPath != null) {
                this.mAppDirs.remove(pkg.mPath);
            }
            cleanPackageDataStructuresLILPw(pkg, chatty);
        }
    }

    void cleanPackageDataStructuresLILPw(PackageParser.Package pkg, boolean chatty) {
        int N = pkg.providers.size();
        for (int i = 0; i < N; i++) {
            PackageParser.Provider p = pkg.providers.get(i);
            this.mProviders.removeProvider(p);
            if (p.info.authority != null) {
                String[] names = p.info.authority.split(Separators.SEMICOLON);
                for (int j = 0; j < names.length; j++) {
                    if (this.mProvidersByAuthority.get(names[j]) == p) {
                        this.mProvidersByAuthority.remove(names[j]);
                    }
                }
            }
        }
        if (0 != 0) {
        }
        int N2 = pkg.services.size();
        StringBuilder r = null;
        for (int i2 = 0; i2 < N2; i2++) {
            PackageParser.Service s = pkg.services.get(i2);
            this.mServices.removeService(s);
            if (chatty) {
                if (r == null) {
                    r = new StringBuilder(256);
                } else {
                    r.append(' ');
                }
                r.append(s.info.name);
            }
        }
        if (r != null) {
        }
        int N3 = pkg.receivers.size();
        for (int i3 = 0; i3 < N3; i3++) {
            PackageParser.Activity a = pkg.receivers.get(i3);
            this.mReceivers.removeActivity(a, "receiver");
        }
        if (0 != 0) {
        }
        int N4 = pkg.activities.size();
        for (int i4 = 0; i4 < N4; i4++) {
            PackageParser.Activity a2 = pkg.activities.get(i4);
            this.mActivities.removeActivity(a2, Context.ACTIVITY_SERVICE);
        }
        if (0 != 0) {
        }
        int N5 = pkg.permissions.size();
        for (int i5 = 0; i5 < N5; i5++) {
            PackageParser.Permission p2 = pkg.permissions.get(i5);
            BasePermission bp = this.mSettings.mPermissions.get(p2.info.name);
            if (bp == null) {
                bp = this.mSettings.mPermissionTrees.get(p2.info.name);
            }
            if (bp != null && bp.perm == p2) {
                bp.perm = null;
            }
        }
        if (0 != 0) {
        }
        int N6 = pkg.instrumentation.size();
        for (int i6 = 0; i6 < N6; i6++) {
            PackageParser.Instrumentation a3 = pkg.instrumentation.get(i6);
            this.mInstrumentation.remove(a3.getComponentName());
        }
        if (0 != 0) {
        }
        if ((pkg.applicationInfo.flags & 1) != 0 && pkg.libraryNames != null) {
            for (int i7 = 0; i7 < pkg.libraryNames.size(); i7++) {
                String name = pkg.libraryNames.get(i7);
                SharedLibraryEntry cur = this.mSharedLibraries.get(name);
                if (cur != null && cur.apk != null && cur.apk.equals(pkg.packageName)) {
                    this.mSharedLibraries.remove(name);
                }
            }
        }
        if (0 != 0) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean isPackageFilename(String name) {
        return name != null && name.endsWith(".apk");
    }

    private static boolean hasPermission(PackageParser.Package pkgInfo, String perm) {
        for (int i = pkgInfo.permissions.size() - 1; i >= 0; i--) {
            if (pkgInfo.permissions.get(i).info.name.equals(perm)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePermissionsLPw(String changingPkg, PackageParser.Package pkgInfo, int flags) {
        BasePermission tree;
        Iterator<BasePermission> it = this.mSettings.mPermissionTrees.values().iterator();
        while (it.hasNext()) {
            BasePermission bp = it.next();
            if (bp.packageSetting == null) {
                bp.packageSetting = this.mSettings.mPackages.get(bp.sourcePackage);
            }
            if (bp.packageSetting == null) {
                Slog.w(TAG, "Removing dangling permission tree: " + bp.name + " from package " + bp.sourcePackage);
                it.remove();
            } else if (changingPkg != null && changingPkg.equals(bp.sourcePackage) && (pkgInfo == null || !hasPermission(pkgInfo, bp.name))) {
                Slog.i(TAG, "Removing old permission tree: " + bp.name + " from package " + bp.sourcePackage);
                flags |= 1;
                it.remove();
            }
        }
        Iterator<BasePermission> it2 = this.mSettings.mPermissions.values().iterator();
        while (it2.hasNext()) {
            BasePermission bp2 = it2.next();
            if (bp2.type == 2 && bp2.packageSetting == null && bp2.pendingInfo != null && (tree = findPermissionTreeLP(bp2.name)) != null && tree.perm != null) {
                bp2.packageSetting = tree.packageSetting;
                bp2.perm = new PackageParser.Permission(tree.perm.owner, new PermissionInfo(bp2.pendingInfo));
                bp2.perm.info.packageName = tree.perm.info.packageName;
                bp2.perm.info.name = bp2.name;
                bp2.uid = tree.uid;
            }
            if (bp2.packageSetting == null) {
                bp2.packageSetting = this.mSettings.mPackages.get(bp2.sourcePackage);
            }
            if (bp2.packageSetting == null) {
                Slog.w(TAG, "Removing dangling permission: " + bp2.name + " from package " + bp2.sourcePackage);
                it2.remove();
            } else if (changingPkg != null && changingPkg.equals(bp2.sourcePackage) && (pkgInfo == null || !hasPermission(pkgInfo, bp2.name))) {
                Slog.i(TAG, "Removing old permission: " + bp2.name + " from package " + bp2.sourcePackage);
                flags |= 1;
                it2.remove();
            }
        }
        if ((flags & 1) != 0) {
            for (PackageParser.Package pkg : this.mPackages.values()) {
                if (pkg != pkgInfo) {
                    grantPermissionsLPw(pkg, (flags & 4) != 0);
                }
            }
        }
        if (pkgInfo != null) {
            grantPermissionsLPw(pkgInfo, (flags & 2) != 0);
        }
    }

    private void grantPermissionsLPw(PackageParser.Package pkg, boolean replace) {
        boolean allowed;
        PackageSetting ps = (PackageSetting) pkg.mExtras;
        if (ps == null) {
            return;
        }
        GrantedPermissions gp = ps.sharedUser != null ? ps.sharedUser : ps;
        HashSet<String> origPermissions = gp.grantedPermissions;
        boolean changedPermission = false;
        if (replace) {
            ps.permissionsFixed = false;
            if (gp == ps) {
                origPermissions = new HashSet<>(gp.grantedPermissions);
                gp.grantedPermissions.clear();
                gp.gids = this.mGlobalGids;
            }
        }
        if (gp.gids == null) {
            gp.gids = this.mGlobalGids;
        }
        int N = pkg.requestedPermissions.size();
        for (int i = 0; i < N; i++) {
            String name = pkg.requestedPermissions.get(i);
            boolean required = pkg.requestedPermissionsRequired.get(i).booleanValue();
            BasePermission bp = this.mSettings.mPermissions.get(name);
            if (bp == null || bp.packageSetting == null) {
                Slog.w(TAG, "Unknown permission " + name + " in package " + pkg.packageName);
            } else {
                String perm = bp.name;
                boolean allowedSig = false;
                int level = bp.protectionLevel & 15;
                if (level == 0 || level == 1) {
                    allowed = required || origPermissions.contains(perm) || (isSystemApp(ps) && !isUpdatedSystemApp(ps));
                } else if (bp.packageSetting == null) {
                    allowed = false;
                } else if (level == 2) {
                    allowed = grantSignaturePermission(perm, pkg, bp, origPermissions);
                    if (allowed) {
                        allowedSig = true;
                    }
                } else {
                    allowed = false;
                }
                if (allowed) {
                    if (!isSystemApp(ps) && ps.permissionsFixed && !allowedSig && !gp.grantedPermissions.contains(perm)) {
                        allowed = isNewPlatformPermissionForPackage(perm, pkg);
                    }
                    if (allowed) {
                        if (!gp.grantedPermissions.contains(perm)) {
                            changedPermission = true;
                            gp.grantedPermissions.add(perm);
                            gp.gids = appendInts(gp.gids, bp.gids);
                        } else if (!ps.haveGids) {
                            gp.gids = appendInts(gp.gids, bp.gids);
                        }
                    } else {
                        Slog.w(TAG, "Not granting permission " + perm + " to package " + pkg.packageName + " because it was previously installed without");
                    }
                } else if (gp.grantedPermissions.remove(perm)) {
                    changedPermission = true;
                    gp.gids = removeInts(gp.gids, bp.gids);
                    Slog.i(TAG, "Un-granting permission " + perm + " from package " + pkg.packageName + " (protectionLevel=" + bp.protectionLevel + " flags=0x" + Integer.toHexString(pkg.applicationInfo.flags) + Separators.RPAREN);
                } else {
                    Slog.w(TAG, "Not granting permission " + perm + " to package " + pkg.packageName + " (protectionLevel=" + bp.protectionLevel + " flags=0x" + Integer.toHexString(pkg.applicationInfo.flags) + Separators.RPAREN);
                }
            }
        }
        if (((changedPermission || replace) && !ps.permissionsFixed && !isSystemApp(ps)) || isUpdatedSystemApp(ps)) {
            ps.permissionsFixed = true;
        }
        ps.haveGids = true;
    }

    private boolean isNewPlatformPermissionForPackage(String perm, PackageParser.Package pkg) {
        boolean allowed = false;
        int NP = PackageParser.NEW_PERMISSIONS.length;
        int ip = 0;
        while (true) {
            if (ip >= NP) {
                break;
            }
            PackageParser.NewPermissionInfo npi = PackageParser.NEW_PERMISSIONS[ip];
            if (!npi.name.equals(perm) || pkg.applicationInfo.targetSdkVersion >= npi.sdkVersion) {
                ip++;
            } else {
                allowed = true;
                Log.i(TAG, "Auto-granting " + perm + " to old pkg " + pkg.packageName);
                break;
            }
        }
        return allowed;
    }

    private boolean grantSignaturePermission(String perm, PackageParser.Package pkg, BasePermission bp, HashSet<String> origPermissions) {
        boolean allowed = compareSignatures(bp.packageSetting.signatures.mSignatures, pkg.mSignatures) == 0 || compareSignatures(this.mPlatformPackage.mSignatures, pkg.mSignatures) == 0;
        if (!allowed && (bp.protectionLevel & 16) != 0 && isSystemApp(pkg)) {
            if (isUpdatedSystemApp(pkg)) {
                PackageSetting sysPs = this.mSettings.getDisabledSystemPkgLPr(pkg.packageName);
                GrantedPermissions origGp = sysPs.sharedUser != null ? sysPs.sharedUser : sysPs;
                if (origGp.grantedPermissions.contains(perm)) {
                    allowed = true;
                } else if (sysPs.pkg != null && sysPs.isPrivileged()) {
                    int j = 0;
                    while (true) {
                        if (j >= sysPs.pkg.requestedPermissions.size()) {
                            break;
                        } else if (!perm.equals(sysPs.pkg.requestedPermissions.get(j))) {
                            j++;
                        } else {
                            allowed = true;
                            break;
                        }
                    }
                }
            } else {
                allowed = isPrivilegedApp(pkg);
            }
        }
        if (!allowed && (bp.protectionLevel & 32) != 0) {
            allowed = origPermissions.contains(perm);
        }
        return allowed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$ActivityIntentResolver.class */
    public final class ActivityIntentResolver extends IntentResolver<PackageParser.ActivityIntentInfo, ResolveInfo> {
        private final HashMap<ComponentName, PackageParser.Activity> mActivities = new HashMap<>();
        private int mFlags;

        ActivityIntentResolver() {
        }

        @Override // com.android.server.IntentResolver
        public List<ResolveInfo> queryIntent(Intent intent, String resolvedType, boolean defaultOnly, int userId) {
            if (PackageManagerService.sUserManager.exists(userId)) {
                this.mFlags = defaultOnly ? 65536 : 0;
                return super.queryIntent(intent, resolvedType, defaultOnly, userId);
            }
            return null;
        }

        public List<ResolveInfo> queryIntent(Intent intent, String resolvedType, int flags, int userId) {
            if (PackageManagerService.sUserManager.exists(userId)) {
                this.mFlags = flags;
                return super.queryIntent(intent, resolvedType, (flags & 65536) != 0, userId);
            }
            return null;
        }

        public List<ResolveInfo> queryIntentForPackage(Intent intent, String resolvedType, int flags, ArrayList<PackageParser.Activity> packageActivities, int userId) {
            if (!PackageManagerService.sUserManager.exists(userId) || packageActivities == null) {
                return null;
            }
            this.mFlags = flags;
            boolean defaultOnly = (flags & 65536) != 0;
            int N = packageActivities.size();
            ArrayList<PackageParser.ActivityIntentInfo[]> listCut = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                AbstractCollection abstractCollection = packageActivities.get(i).intents;
                if (abstractCollection != null && abstractCollection.size() > 0) {
                    PackageParser.ActivityIntentInfo[] array = new PackageParser.ActivityIntentInfo[abstractCollection.size()];
                    abstractCollection.toArray(array);
                    listCut.add(array);
                }
            }
            return super.queryIntentFromList(intent, resolvedType, defaultOnly, listCut, userId);
        }

        public final void addActivity(PackageParser.Activity a, String type) {
            boolean systemApp = PackageManagerService.isSystemApp(a.info.applicationInfo);
            this.mActivities.put(a.getComponentName(), a);
            int NI = a.intents.size();
            for (int j = 0; j < NI; j++) {
                PackageParser.ActivityIntentInfo intent = (PackageParser.ActivityIntentInfo) a.intents.get(j);
                if (!systemApp && intent.getPriority() > 0 && Context.ACTIVITY_SERVICE.equals(type)) {
                    intent.setPriority(0);
                    Log.w(PackageManagerService.TAG, "Package " + a.info.applicationInfo.packageName + " has activity " + a.className + " with priority > 0, forcing to 0");
                }
                if (!intent.debugCheck()) {
                    Log.w(PackageManagerService.TAG, "==> For Activity " + a.info.name);
                }
                addFilter(intent);
            }
        }

        public final void removeActivity(PackageParser.Activity a, String type) {
            this.mActivities.remove(a.getComponentName());
            int NI = a.intents.size();
            for (int j = 0; j < NI; j++) {
                PackageParser.ActivityIntentInfo intent = (PackageParser.ActivityIntentInfo) a.intents.get(j);
                removeFilter(intent);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public boolean allowFilterResult(PackageParser.ActivityIntentInfo filter, List<ResolveInfo> dest) {
            ActivityInfo filterAi = filter.activity.info;
            for (int i = dest.size() - 1; i >= 0; i--) {
                ActivityInfo destAi = dest.get(i).activityInfo;
                if (destAi.name == filterAi.name && destAi.packageName == filterAi.packageName) {
                    return false;
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.server.IntentResolver
        public PackageParser.ActivityIntentInfo[] newArray(int size) {
            return new PackageParser.ActivityIntentInfo[size];
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public boolean isFilterStopped(PackageParser.ActivityIntentInfo filter, int userId) {
            PackageSetting ps;
            if (PackageManagerService.sUserManager.exists(userId)) {
                PackageParser.Package p = filter.activity.owner;
                return p != null && (ps = (PackageSetting) p.mExtras) != null && (ps.pkgFlags & 1) == 0 && ps.getStopped(userId);
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public boolean isPackageForFilter(String packageName, PackageParser.ActivityIntentInfo info) {
            return packageName.equals(info.activity.owner.packageName);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public ResolveInfo newResult(PackageParser.ActivityIntentInfo info, int match, int userId) {
            PackageSetting ps;
            ActivityInfo ai;
            if (!PackageManagerService.sUserManager.exists(userId) || !PackageManagerService.this.mSettings.isEnabledLPr(info.activity.info, this.mFlags, userId)) {
                return null;
            }
            PackageParser.Activity activity = info.activity;
            if ((PackageManagerService.this.mSafeMode && (activity.info.applicationInfo.flags & 1) == 0) || (ps = (PackageSetting) activity.owner.mExtras) == null || (ai = PackageParser.generateActivityInfo(activity, this.mFlags, ps.readUserState(userId), userId)) == null) {
                return null;
            }
            ResolveInfo res = new ResolveInfo();
            res.activityInfo = ai;
            if ((this.mFlags & 64) != 0) {
                res.filter = info;
            }
            res.priority = info.getPriority();
            res.preferredOrder = activity.owner.mPreferredOrder;
            res.match = match;
            res.isDefault = info.hasDefault;
            res.labelRes = info.labelRes;
            res.nonLocalizedLabel = info.nonLocalizedLabel;
            res.icon = info.icon;
            res.system = PackageManagerService.isSystemApp(res.activityInfo.applicationInfo);
            return res;
        }

        @Override // com.android.server.IntentResolver
        protected void sortResults(List<ResolveInfo> results) {
            Collections.sort(results, PackageManagerService.mResolvePrioritySorter);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public void dumpFilter(PrintWriter out, String prefix, PackageParser.ActivityIntentInfo filter) {
            out.print(prefix);
            out.print(Integer.toHexString(System.identityHashCode(filter.activity)));
            out.print(' ');
            filter.activity.printComponentShortName(out);
            out.print(" filter ");
            out.println(Integer.toHexString(System.identityHashCode(filter)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PackageManagerService$ServiceIntentResolver.class */
    public final class ServiceIntentResolver extends IntentResolver<PackageParser.ServiceIntentInfo, ResolveInfo> {
        private final HashMap<ComponentName, PackageParser.Service> mServices;
        private int mFlags;

        private ServiceIntentResolver() {
            this.mServices = new HashMap<>();
        }

        @Override // com.android.server.IntentResolver
        public List<ResolveInfo> queryIntent(Intent intent, String resolvedType, boolean defaultOnly, int userId) {
            this.mFlags = defaultOnly ? 65536 : 0;
            return super.queryIntent(intent, resolvedType, defaultOnly, userId);
        }

        public List<ResolveInfo> queryIntent(Intent intent, String resolvedType, int flags, int userId) {
            if (PackageManagerService.sUserManager.exists(userId)) {
                this.mFlags = flags;
                return super.queryIntent(intent, resolvedType, (flags & 65536) != 0, userId);
            }
            return null;
        }

        public List<ResolveInfo> queryIntentForPackage(Intent intent, String resolvedType, int flags, ArrayList<PackageParser.Service> packageServices, int userId) {
            if (!PackageManagerService.sUserManager.exists(userId) || packageServices == null) {
                return null;
            }
            this.mFlags = flags;
            boolean defaultOnly = (flags & 65536) != 0;
            int N = packageServices.size();
            ArrayList<PackageParser.ServiceIntentInfo[]> listCut = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                AbstractCollection abstractCollection = packageServices.get(i).intents;
                if (abstractCollection != null && abstractCollection.size() > 0) {
                    PackageParser.ServiceIntentInfo[] array = new PackageParser.ServiceIntentInfo[abstractCollection.size()];
                    abstractCollection.toArray(array);
                    listCut.add(array);
                }
            }
            return super.queryIntentFromList(intent, resolvedType, defaultOnly, listCut, userId);
        }

        public final void addService(PackageParser.Service s) {
            this.mServices.put(s.getComponentName(), s);
            int NI = s.intents.size();
            for (int j = 0; j < NI; j++) {
                PackageParser.ServiceIntentInfo intent = (PackageParser.ServiceIntentInfo) s.intents.get(j);
                if (!intent.debugCheck()) {
                    Log.w(PackageManagerService.TAG, "==> For Service " + s.info.name);
                }
                addFilter(intent);
            }
        }

        public final void removeService(PackageParser.Service s) {
            this.mServices.remove(s.getComponentName());
            int NI = s.intents.size();
            for (int j = 0; j < NI; j++) {
                PackageParser.ServiceIntentInfo intent = (PackageParser.ServiceIntentInfo) s.intents.get(j);
                removeFilter(intent);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public boolean allowFilterResult(PackageParser.ServiceIntentInfo filter, List<ResolveInfo> dest) {
            ServiceInfo filterSi = filter.service.info;
            for (int i = dest.size() - 1; i >= 0; i--) {
                ServiceInfo destAi = dest.get(i).serviceInfo;
                if (destAi.name == filterSi.name && destAi.packageName == filterSi.packageName) {
                    return false;
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.server.IntentResolver
        public PackageParser.ServiceIntentInfo[] newArray(int size) {
            return new PackageParser.ServiceIntentInfo[size];
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public boolean isFilterStopped(PackageParser.ServiceIntentInfo filter, int userId) {
            PackageSetting ps;
            if (PackageManagerService.sUserManager.exists(userId)) {
                PackageParser.Package p = filter.service.owner;
                return p != null && (ps = (PackageSetting) p.mExtras) != null && (ps.pkgFlags & 1) == 0 && ps.getStopped(userId);
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public boolean isPackageForFilter(String packageName, PackageParser.ServiceIntentInfo info) {
            return packageName.equals(info.service.owner.packageName);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public ResolveInfo newResult(PackageParser.ServiceIntentInfo filter, int match, int userId) {
            PackageSetting ps;
            ServiceInfo si;
            if (!PackageManagerService.sUserManager.exists(userId) || !PackageManagerService.this.mSettings.isEnabledLPr(filter.service.info, this.mFlags, userId)) {
                return null;
            }
            PackageParser.Service service = filter.service;
            if ((PackageManagerService.this.mSafeMode && (service.info.applicationInfo.flags & 1) == 0) || (ps = (PackageSetting) service.owner.mExtras) == null || (si = PackageParser.generateServiceInfo(service, this.mFlags, ps.readUserState(userId), userId)) == null) {
                return null;
            }
            ResolveInfo res = new ResolveInfo();
            res.serviceInfo = si;
            if ((this.mFlags & 64) != 0) {
                res.filter = filter;
            }
            res.priority = filter.getPriority();
            res.preferredOrder = service.owner.mPreferredOrder;
            res.match = match;
            res.isDefault = filter.hasDefault;
            res.labelRes = filter.labelRes;
            res.nonLocalizedLabel = filter.nonLocalizedLabel;
            res.icon = filter.icon;
            res.system = PackageManagerService.isSystemApp(res.serviceInfo.applicationInfo);
            return res;
        }

        @Override // com.android.server.IntentResolver
        protected void sortResults(List<ResolveInfo> results) {
            Collections.sort(results, PackageManagerService.mResolvePrioritySorter);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public void dumpFilter(PrintWriter out, String prefix, PackageParser.ServiceIntentInfo filter) {
            out.print(prefix);
            out.print(Integer.toHexString(System.identityHashCode(filter.service)));
            out.print(' ');
            filter.service.printComponentShortName(out);
            out.print(" filter ");
            out.println(Integer.toHexString(System.identityHashCode(filter)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PackageManagerService$ProviderIntentResolver.class */
    public final class ProviderIntentResolver extends IntentResolver<PackageParser.ProviderIntentInfo, ResolveInfo> {
        private final HashMap<ComponentName, PackageParser.Provider> mProviders;
        private int mFlags;

        private ProviderIntentResolver() {
            this.mProviders = new HashMap<>();
        }

        static /* synthetic */ HashMap access$1600(ProviderIntentResolver x0) {
            return x0.mProviders;
        }

        @Override // com.android.server.IntentResolver
        public List<ResolveInfo> queryIntent(Intent intent, String resolvedType, boolean defaultOnly, int userId) {
            this.mFlags = defaultOnly ? 65536 : 0;
            return super.queryIntent(intent, resolvedType, defaultOnly, userId);
        }

        public List<ResolveInfo> queryIntent(Intent intent, String resolvedType, int flags, int userId) {
            if (!PackageManagerService.sUserManager.exists(userId)) {
                return null;
            }
            this.mFlags = flags;
            return super.queryIntent(intent, resolvedType, (flags & 65536) != 0, userId);
        }

        public List<ResolveInfo> queryIntentForPackage(Intent intent, String resolvedType, int flags, ArrayList<PackageParser.Provider> packageProviders, int userId) {
            if (!PackageManagerService.sUserManager.exists(userId) || packageProviders == null) {
                return null;
            }
            this.mFlags = flags;
            boolean defaultOnly = (flags & 65536) != 0;
            int N = packageProviders.size();
            ArrayList<PackageParser.ProviderIntentInfo[]> listCut = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                AbstractCollection abstractCollection = packageProviders.get(i).intents;
                if (abstractCollection != null && abstractCollection.size() > 0) {
                    PackageParser.ProviderIntentInfo[] array = new PackageParser.ProviderIntentInfo[abstractCollection.size()];
                    abstractCollection.toArray(array);
                    listCut.add(array);
                }
            }
            return super.queryIntentFromList(intent, resolvedType, defaultOnly, listCut, userId);
        }

        public final void addProvider(PackageParser.Provider p) {
            this.mProviders.put(p.getComponentName(), p);
            int NI = p.intents.size();
            for (int j = 0; j < NI; j++) {
                PackageParser.ProviderIntentInfo intent = (PackageParser.ProviderIntentInfo) p.intents.get(j);
                if (!intent.debugCheck()) {
                    Log.w(PackageManagerService.TAG, "==> For Provider " + p.info.name);
                }
                addFilter(intent);
            }
        }

        public final void removeProvider(PackageParser.Provider p) {
            this.mProviders.remove(p.getComponentName());
            int NI = p.intents.size();
            for (int j = 0; j < NI; j++) {
                PackageParser.ProviderIntentInfo intent = (PackageParser.ProviderIntentInfo) p.intents.get(j);
                removeFilter(intent);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public boolean allowFilterResult(PackageParser.ProviderIntentInfo filter, List<ResolveInfo> dest) {
            ProviderInfo filterPi = filter.provider.info;
            for (int i = dest.size() - 1; i >= 0; i--) {
                ProviderInfo destPi = dest.get(i).providerInfo;
                if (destPi.name == filterPi.name && destPi.packageName == filterPi.packageName) {
                    return false;
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.server.IntentResolver
        public PackageParser.ProviderIntentInfo[] newArray(int size) {
            return new PackageParser.ProviderIntentInfo[size];
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public boolean isFilterStopped(PackageParser.ProviderIntentInfo filter, int userId) {
            PackageSetting ps;
            if (!PackageManagerService.sUserManager.exists(userId)) {
                return true;
            }
            PackageParser.Package p = filter.provider.owner;
            return p != null && (ps = (PackageSetting) p.mExtras) != null && (ps.pkgFlags & 1) == 0 && ps.getStopped(userId);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public boolean isPackageForFilter(String packageName, PackageParser.ProviderIntentInfo info) {
            return packageName.equals(info.provider.owner.packageName);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public ResolveInfo newResult(PackageParser.ProviderIntentInfo filter, int match, int userId) {
            PackageSetting ps;
            ProviderInfo pi;
            if (!PackageManagerService.sUserManager.exists(userId) || !PackageManagerService.this.mSettings.isEnabledLPr(filter.provider.info, this.mFlags, userId)) {
                return null;
            }
            PackageParser.Provider provider = filter.provider;
            if ((PackageManagerService.this.mSafeMode && (provider.info.applicationInfo.flags & 1) == 0) || (ps = (PackageSetting) provider.owner.mExtras) == null || (pi = PackageParser.generateProviderInfo(provider, this.mFlags, ps.readUserState(userId), userId)) == null) {
                return null;
            }
            ResolveInfo res = new ResolveInfo();
            res.providerInfo = pi;
            if ((this.mFlags & 64) != 0) {
                res.filter = filter;
            }
            res.priority = filter.getPriority();
            res.preferredOrder = provider.owner.mPreferredOrder;
            res.match = match;
            res.isDefault = filter.hasDefault;
            res.labelRes = filter.labelRes;
            res.nonLocalizedLabel = filter.nonLocalizedLabel;
            res.icon = filter.icon;
            res.system = PackageManagerService.isSystemApp(res.providerInfo.applicationInfo);
            return res;
        }

        @Override // com.android.server.IntentResolver
        protected void sortResults(List<ResolveInfo> results) {
            Collections.sort(results, PackageManagerService.mResolvePrioritySorter);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.server.IntentResolver
        public void dumpFilter(PrintWriter out, String prefix, PackageParser.ProviderIntentInfo filter) {
            out.print(prefix);
            out.print(Integer.toHexString(System.identityHashCode(filter.provider)));
            out.print(' ');
            filter.provider.printComponentShortName(out);
            out.print(" filter ");
            out.println(Integer.toHexString(System.identityHashCode(filter)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void sendPackageBroadcast(String action, String pkg, Bundle extras, String targetPkg, IIntentReceiver finishedReceiver, int[] userIds) {
        IActivityManager am = ActivityManagerNative.getDefault();
        if (am != null) {
            if (userIds == null) {
                try {
                    userIds = am.getRunningUserIds();
                } catch (RemoteException e) {
                    return;
                }
            }
            int[] arr$ = userIds;
            for (int id : arr$) {
                Intent intent = new Intent(action, pkg != null ? Uri.fromParts(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, pkg, null) : null);
                if (extras != null) {
                    intent.putExtras(extras);
                }
                if (targetPkg != null) {
                    intent.setPackage(targetPkg);
                }
                int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
                if (uid > 0 && UserHandle.getUserId(uid) != id) {
                    intent.putExtra(Intent.EXTRA_UID, UserHandle.getUid(id, UserHandle.getAppId(uid)));
                }
                intent.putExtra(Intent.EXTRA_USER_HANDLE, id);
                intent.addFlags(67108864);
                am.broadcastIntent(null, intent, null, finishedReceiver, 0, null, null, null, -1, finishedReceiver != null, false, id);
            }
        }
    }

    private boolean isExternalMediaAvailable() {
        return this.mMediaMounted || Environment.isExternalStorageEmulated();
    }

    @Override // android.content.pm.IPackageManager
    public PackageCleanItem nextPackageToClean(PackageCleanItem lastPackage) {
        synchronized (this.mPackages) {
            if (!isExternalMediaAvailable()) {
                return null;
            }
            ArrayList<PackageCleanItem> pkgs = this.mSettings.mPackagesToBeCleaned;
            if (lastPackage != null) {
                pkgs.remove(lastPackage);
            }
            if (pkgs.size() > 0) {
                return pkgs.get(0);
            }
            return null;
        }
    }

    void schedulePackageCleaning(String packageName, int userId, boolean andCode) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7, userId, andCode ? 1 : 0, packageName));
    }

    void startCleaningPackages() {
        synchronized (this.mPackages) {
            if (isExternalMediaAvailable()) {
                if (this.mSettings.mPackagesToBeCleaned.isEmpty()) {
                    return;
                }
                Intent intent = new Intent(PackageManager.ACTION_CLEAN_EXTERNAL_STORAGE);
                intent.setComponent(DEFAULT_CONTAINER_COMPONENT);
                IActivityManager am = ActivityManagerNative.getDefault();
                if (am != null) {
                    try {
                        am.startService(null, intent, null, 0);
                    } catch (RemoteException e) {
                    }
                }
            }
        }
    }

    /* loaded from: PackageManagerService$AppDirObserver.class */
    private final class AppDirObserver extends FileObserver {
        private final String mRootDir;
        private final boolean mIsRom;
        private final boolean mIsPrivileged;

        public AppDirObserver(String path, int mask, boolean isrom, boolean isPrivileged) {
            super(path, mask);
            this.mRootDir = path;
            this.mIsRom = isrom;
            this.mIsPrivileged = isPrivileged;
        }

        @Override // android.os.FileObserver
        public void onEvent(int event, String path) {
            PackageParser.Package p;
            int[] addedUsers;
            String removedPackage = null;
            int removedAppId = -1;
            int[] removedUsers = null;
            String addedPackage = null;
            int addedAppId = -1;
            synchronized (PackageManagerService.this.mInstallLock) {
                String fullPathStr = null;
                File fullPath = null;
                if (path != null) {
                    fullPath = new File(this.mRootDir, path);
                    fullPathStr = fullPath.getPath();
                }
                if (PackageManagerService.isPackageFilename(path)) {
                    if (PackageManagerService.ignoreCodePath(fullPathStr)) {
                        return;
                    }
                    PackageSetting ps = null;
                    synchronized (PackageManagerService.this.mPackages) {
                        p = PackageManagerService.this.mAppDirs.get(fullPathStr);
                        if (p != null) {
                            ps = PackageManagerService.this.mSettings.mPackages.get(p.applicationInfo.packageName);
                            if (ps != null) {
                                removedUsers = ps.queryInstalledUsers(PackageManagerService.sUserManager.getUserIds(), true);
                            } else {
                                removedUsers = PackageManagerService.sUserManager.getUserIds();
                            }
                        }
                        addedUsers = PackageManagerService.sUserManager.getUserIds();
                    }
                    if ((event & PackageManagerService.REMOVE_EVENTS) != 0 && ps != null) {
                        PackageManagerService.this.removePackageLI(ps, true);
                        removedPackage = ps.name;
                        removedAppId = ps.appId;
                    }
                    if ((event & 136) != 0 && p == null) {
                        int flags = 6;
                        if (this.mIsRom) {
                            flags = 6 | 65;
                            if (this.mIsPrivileged) {
                                flags |= 128;
                            }
                        }
                        PackageParser.Package p2 = PackageManagerService.this.scanPackageLI(fullPath, flags, 97, System.currentTimeMillis(), UserHandle.ALL);
                        if (p2 != null) {
                            synchronized (PackageManagerService.this.mPackages) {
                                PackageManagerService.this.updatePermissionsLPw(p2.packageName, p2, p2.permissions.size() > 0 ? 1 : 0);
                            }
                            addedPackage = p2.applicationInfo.packageName;
                            addedAppId = UserHandle.getAppId(p2.applicationInfo.uid);
                        }
                    }
                    synchronized (PackageManagerService.this.mPackages) {
                        PackageManagerService.this.mSettings.writeLPr();
                    }
                    if (removedPackage != null) {
                        Bundle extras = new Bundle(1);
                        extras.putInt(Intent.EXTRA_UID, removedAppId);
                        extras.putBoolean(Intent.EXTRA_DATA_REMOVED, false);
                        PackageManagerService.sendPackageBroadcast(Intent.ACTION_PACKAGE_REMOVED, removedPackage, extras, null, null, removedUsers);
                    }
                    if (addedPackage != null) {
                        Bundle extras2 = new Bundle(1);
                        extras2.putInt(Intent.EXTRA_UID, addedAppId);
                        PackageManagerService.sendPackageBroadcast(Intent.ACTION_PACKAGE_ADDED, addedPackage, extras2, null, null, addedUsers);
                    }
                }
            }
        }
    }

    public void installPackage(Uri packageURI, IPackageInstallObserver observer, int flags) {
        installPackage(packageURI, observer, flags, null);
    }

    @Override // android.content.pm.IPackageManager
    public void installPackage(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName) {
        installPackageWithVerification(packageURI, observer, flags, installerPackageName, null, null, null);
    }

    @Override // android.content.pm.IPackageManager
    public void installPackageWithVerification(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName, Uri verificationURI, ManifestDigest manifestDigest, ContainerEncryptionParams encryptionParams) {
        VerificationParams verificationParams = new VerificationParams(verificationURI, null, null, -1, manifestDigest);
        installPackageWithVerificationAndEncryption(packageURI, observer, flags, installerPackageName, verificationParams, encryptionParams);
    }

    @Override // android.content.pm.IPackageManager
    public void installPackageWithVerificationAndEncryption(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName, VerificationParams verificationParams, ContainerEncryptionParams encryptionParams) {
        UserHandle user;
        int filteredFlags;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INSTALL_PACKAGES, null);
        int uid = Binder.getCallingUid();
        if (isUserRestricted(UserHandle.getUserId(uid), UserManager.DISALLOW_INSTALL_APPS)) {
            try {
                observer.packageInstalled("", PackageManager.INSTALL_FAILED_USER_RESTRICTED);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        if ((flags & 64) != 0) {
            user = UserHandle.ALL;
        } else {
            user = new UserHandle(UserHandle.getUserId(uid));
        }
        if (uid == 2000 || uid == 0) {
            filteredFlags = flags | 32;
        } else {
            filteredFlags = flags & (-33);
        }
        verificationParams.setInstallerUid(uid);
        Message msg = this.mHandler.obtainMessage(5);
        msg.obj = new InstallParams(packageURI, observer, filteredFlags, installerPackageName, verificationParams, encryptionParams, user);
        this.mHandler.sendMessage(msg);
    }

    private void sendPackageAddedForUser(String packageName, PackageSetting pkgSetting, int userId) {
        Bundle extras = new Bundle(1);
        extras.putInt(Intent.EXTRA_UID, UserHandle.getUid(userId, pkgSetting.appId));
        sendPackageBroadcast(Intent.ACTION_PACKAGE_ADDED, packageName, extras, null, null, new int[]{userId});
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            boolean isSystem = isSystemApp(pkgSetting) || isUpdatedSystemApp(pkgSetting);
            if (isSystem && am.isUserRunning(userId, false)) {
                Intent bcIntent = new Intent(Intent.ACTION_BOOT_COMPLETED).addFlags(32).setPackage(packageName);
                am.broadcastIntent(null, bcIntent, null, null, 0, null, null, null, -1, false, false, userId);
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "Unable to bootstrap installed package", e);
        }
    }

    private void sendPackageBlockedForUser(String packageName, PackageSetting pkgSetting, int userId) {
        PackageRemovedInfo info = new PackageRemovedInfo();
        info.removedPackage = packageName;
        info.removedUsers = new int[]{userId};
        info.uid = UserHandle.getUid(userId, pkgSetting.appId);
        info.sendBroadcast(false, false, false);
    }

    private boolean isUserRestricted(int userId, String restrictionKey) {
        Bundle restrictions = sUserManager.getUserRestrictions(userId);
        if (restrictions.getBoolean(restrictionKey, false)) {
            Log.w(TAG, "User is restricted: " + restrictionKey);
            return true;
        }
        return false;
    }

    @Override // android.content.pm.IPackageManager
    public void verifyPendingInstall(int id, int verificationCode) throws RemoteException {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.PACKAGE_VERIFICATION_AGENT, "Only package verification agents can verify applications");
        Message msg = this.mHandler.obtainMessage(15);
        PackageVerificationResponse response = new PackageVerificationResponse(verificationCode, Binder.getCallingUid());
        msg.arg1 = id;
        msg.obj = response;
        this.mHandler.sendMessage(msg);
    }

    @Override // android.content.pm.IPackageManager
    public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.PACKAGE_VERIFICATION_AGENT, "Only package verification agents can extend verification timeouts");
        PackageVerificationState state = this.mPendingVerification.get(id);
        PackageVerificationResponse response = new PackageVerificationResponse(verificationCodeAtTimeout, Binder.getCallingUid());
        if (millisecondsToDelay > 3600000) {
            millisecondsToDelay = 3600000;
        }
        if (millisecondsToDelay < 0) {
            millisecondsToDelay = 0;
        }
        if (verificationCodeAtTimeout == 1 || verificationCodeAtTimeout != -1) {
        }
        if (state != null && !state.timeoutExtended()) {
            state.extendTimeout();
            Message msg = this.mHandler.obtainMessage(15);
            msg.arg1 = id;
            msg.obj = response;
            this.mHandler.sendMessageDelayed(msg, millisecondsToDelay);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastPackageVerified(int verificationId, Uri packageUri, int verificationCode, UserHandle user) {
        Intent intent = new Intent(Intent.ACTION_PACKAGE_VERIFIED);
        intent.setDataAndType(packageUri, PACKAGE_MIME_TYPE);
        intent.addFlags(1);
        intent.putExtra(PackageManager.EXTRA_VERIFICATION_ID, verificationId);
        intent.putExtra(PackageManager.EXTRA_VERIFICATION_RESULT, verificationCode);
        this.mContext.sendBroadcastAsUser(intent, user, Manifest.permission.PACKAGE_VERIFICATION_AGENT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ComponentName matchComponentForVerifier(String packageName, List<ResolveInfo> receivers) {
        ActivityInfo targetReceiver = null;
        int NR = receivers.size();
        int i = 0;
        while (true) {
            if (i >= NR) {
                break;
            }
            ResolveInfo info = receivers.get(i);
            if (info.activityInfo == null || !packageName.equals(info.activityInfo.packageName)) {
                i++;
            } else {
                targetReceiver = info.activityInfo;
                break;
            }
        }
        if (targetReceiver == null) {
            return null;
        }
        return new ComponentName(targetReceiver.packageName, targetReceiver.name);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<ComponentName> matchVerifiers(PackageInfoLite pkgInfo, List<ResolveInfo> receivers, PackageVerificationState verificationState) {
        int verifierUid;
        if (pkgInfo.verifiers.length == 0) {
            return null;
        }
        int N = pkgInfo.verifiers.length;
        List<ComponentName> sufficientVerifiers = new ArrayList<>(N + 1);
        for (int i = 0; i < N; i++) {
            VerifierInfo verifierInfo = pkgInfo.verifiers[i];
            ComponentName comp = matchComponentForVerifier(verifierInfo.packageName, receivers);
            if (comp != null && (verifierUid = getUidForVerifier(verifierInfo)) != -1) {
                sufficientVerifiers.add(comp);
                verificationState.addSufficientVerifier(verifierUid);
            }
        }
        return sufficientVerifiers;
    }

    private int getUidForVerifier(VerifierInfo verifierInfo) {
        synchronized (this.mPackages) {
            PackageParser.Package pkg = this.mPackages.get(verifierInfo.packageName);
            if (pkg == null) {
                return -1;
            }
            if (pkg.mSignatures.length != 1) {
                Slog.i(TAG, "Verifier package " + verifierInfo.packageName + " has more than one signature; ignoring");
                return -1;
            }
            try {
                Signature verifierSig = pkg.mSignatures[0];
                PublicKey publicKey = verifierSig.getPublicKey();
                byte[] expectedPublicKey = publicKey.getEncoded();
                byte[] actualPublicKey = verifierInfo.publicKey.getEncoded();
                if (!Arrays.equals(actualPublicKey, expectedPublicKey)) {
                    Slog.i(TAG, "Verifier package " + verifierInfo.packageName + " does not have the expected public key; ignoring");
                    return -1;
                }
                return pkg.applicationInfo.uid;
            } catch (CertificateException e) {
                return -1;
            }
        }
    }

    @Override // android.content.pm.IPackageManager
    public void finishPackageInstall(int token) {
        enforceSystemOrRoot("Only the system is allowed to finish installs");
        Message msg = this.mHandler.obtainMessage(9, token, 0);
        this.mHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getVerificationTimeout() {
        return Settings.Global.getLong(this.mContext.getContentResolver(), Settings.Global.PACKAGE_VERIFIER_TIMEOUT, DEFAULT_VERIFICATION_TIMEOUT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDefaultVerificationResponse() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.PACKAGE_VERIFIER_DEFAULT_RESPONSE, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isVerificationEnabled(int flags) {
        return ((flags & 32) == 0 || !(ActivityManager.isRunningInTestHarness() || Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.PACKAGE_VERIFIER_INCLUDE_ADB, 1) == 0)) && Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.PACKAGE_VERIFIER_ENABLE, 1) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getUnknownSourcesSettings() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "install_non_market_apps", -1);
    }

    @Override // android.content.pm.IPackageManager
    public void setInstallerPackageName(String targetPackage, String installerPackageName) {
        PackageSetting installerPackageSetting;
        Signature[] callerSignature;
        PackageSetting setting;
        int uid = Binder.getCallingUid();
        synchronized (this.mPackages) {
            PackageSetting targetPackageSetting = this.mSettings.mPackages.get(targetPackage);
            if (targetPackageSetting == null) {
                throw new IllegalArgumentException("Unknown target package: " + targetPackage);
            }
            if (installerPackageName != null) {
                installerPackageSetting = this.mSettings.mPackages.get(installerPackageName);
                if (installerPackageSetting == null) {
                    throw new IllegalArgumentException("Unknown installer package: " + installerPackageName);
                }
            } else {
                installerPackageSetting = null;
            }
            Object obj = this.mSettings.getUserIdLPr(uid);
            if (obj != null) {
                if (obj instanceof SharedUserSetting) {
                    callerSignature = ((SharedUserSetting) obj).signatures.mSignatures;
                } else if (obj instanceof PackageSetting) {
                    callerSignature = ((PackageSetting) obj).signatures.mSignatures;
                } else {
                    throw new SecurityException("Bad object " + obj + " for uid " + uid);
                }
                if (installerPackageSetting != null && compareSignatures(callerSignature, installerPackageSetting.signatures.mSignatures) != 0) {
                    throw new SecurityException("Caller does not have same cert as new installer package " + installerPackageName);
                }
                if (targetPackageSetting.installerPackageName != null && (setting = this.mSettings.mPackages.get(targetPackageSetting.installerPackageName)) != null && compareSignatures(callerSignature, setting.signatures.mSignatures) != 0) {
                    throw new SecurityException("Caller does not have same cert as old installer package " + targetPackageSetting.installerPackageName);
                }
                targetPackageSetting.installerPackageName = installerPackageName;
                scheduleWriteSettingsLocked();
            } else {
                throw new SecurityException("Unknown calling uid " + uid);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processPendingInstall(final InstallArgs args, final int currentStatus) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageManagerService.5
            @Override // java.lang.Runnable
            public void run() {
                PackageManagerService.this.mHandler.removeCallbacks(this);
                PackageInstalledInfo res = new PackageInstalledInfo();
                res.returnCode = currentStatus;
                res.uid = -1;
                res.pkg = null;
                res.removedInfo = new PackageRemovedInfo();
                if (res.returnCode == 1) {
                    args.doPreInstall(res.returnCode);
                    synchronized (PackageManagerService.this.mInstallLock) {
                        PackageManagerService.this.installPackageLI(args, true, res);
                    }
                    args.doPostInstall(res.returnCode, res.uid);
                }
                boolean update = res.removedInfo.removedPackage != null;
                boolean doRestore = (update || res.pkg == null || res.pkg.applicationInfo.backupAgentName == null) ? false : true;
                if (PackageManagerService.this.mNextInstallToken < 0) {
                    PackageManagerService.this.mNextInstallToken = 1;
                }
                PackageManagerService packageManagerService = PackageManagerService.this;
                int token = packageManagerService.mNextInstallToken;
                packageManagerService.mNextInstallToken = token + 1;
                PostInstallData data = new PostInstallData(args, res);
                PackageManagerService.this.mRunningInstalls.put(token, data);
                if (res.returnCode == 1 && doRestore) {
                    IBackupManager bm = IBackupManager.Stub.asInterface(ServiceManager.getService(Context.BACKUP_SERVICE));
                    if (bm != null) {
                        try {
                            bm.restoreAtInstall(res.pkg.applicationInfo.packageName, token);
                        } catch (RemoteException e) {
                        } catch (Exception e2) {
                            Slog.e(PackageManagerService.TAG, "Exception trying to enqueue restore", e2);
                            doRestore = false;
                        }
                    } else {
                        Slog.e(PackageManagerService.TAG, "Backup Manager not found!");
                        doRestore = false;
                    }
                }
                if (!doRestore) {
                    Message msg = PackageManagerService.this.mHandler.obtainMessage(9, token, 0);
                    PackageManagerService.this.mHandler.sendMessage(msg);
                }
            }
        });
    }

    /* loaded from: PackageManagerService$HandlerParams.class */
    private abstract class HandlerParams {
        private static final int MAX_RETRIES = 4;
        private int mRetries = 0;
        private final UserHandle mUser;

        abstract void handleStartCopy() throws RemoteException;

        abstract void handleServiceError();

        abstract void handleReturnCode();

        HandlerParams(UserHandle user) {
            this.mUser = user;
        }

        UserHandle getUser() {
            return this.mUser;
        }

        final boolean startCopy() {
            boolean res;
            int i;
            try {
                i = this.mRetries + 1;
                this.mRetries = i;
            } catch (RemoteException e) {
                PackageManagerService.this.mHandler.sendEmptyMessage(10);
                res = false;
            }
            if (i > 4) {
                Slog.w(PackageManagerService.TAG, "Failed to invoke remote methods on default container service. Giving up");
                PackageManagerService.this.mHandler.sendEmptyMessage(11);
                handleServiceError();
                return false;
            }
            handleStartCopy();
            res = true;
            handleReturnCode();
            return res;
        }

        final void serviceError() {
            handleServiceError();
            handleReturnCode();
        }
    }

    /* loaded from: PackageManagerService$MeasureParams.class */
    class MeasureParams extends HandlerParams {
        private final PackageStats mStats;
        private boolean mSuccess;
        private final IPackageStatsObserver mObserver;

        public MeasureParams(PackageStats stats, IPackageStatsObserver observer) {
            super(new UserHandle(stats.userHandle));
            this.mObserver = observer;
            this.mStats = stats;
        }

        public String toString() {
            return "MeasureParams{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.mStats.packageName + "}";
        }

        @Override // com.android.server.pm.PackageManagerService.HandlerParams
        void handleStartCopy() throws RemoteException {
            boolean mounted;
            synchronized (PackageManagerService.this.mInstallLock) {
                this.mSuccess = PackageManagerService.this.getPackageSizeInfoLI(this.mStats.packageName, this.mStats.userHandle, this.mStats);
            }
            if (Environment.isExternalStorageEmulated()) {
                mounted = true;
            } else {
                String status = Environment.getExternalStorageState();
                mounted = Environment.MEDIA_MOUNTED.equals(status) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(status);
            }
            if (mounted) {
                Environment.UserEnvironment userEnv = new Environment.UserEnvironment(this.mStats.userHandle);
                this.mStats.externalCacheSize = PackageManagerService.calculateDirectorySize(PackageManagerService.this.mContainerService, userEnv.buildExternalStorageAppCacheDirs(this.mStats.packageName));
                this.mStats.externalDataSize = PackageManagerService.calculateDirectorySize(PackageManagerService.this.mContainerService, userEnv.buildExternalStorageAppDataDirs(this.mStats.packageName));
                this.mStats.externalDataSize -= this.mStats.externalCacheSize;
                this.mStats.externalMediaSize = PackageManagerService.calculateDirectorySize(PackageManagerService.this.mContainerService, userEnv.buildExternalStorageAppMediaDirs(this.mStats.packageName));
                this.mStats.externalObbSize = PackageManagerService.calculateDirectorySize(PackageManagerService.this.mContainerService, userEnv.buildExternalStorageAppObbDirs(this.mStats.packageName));
            }
        }

        @Override // com.android.server.pm.PackageManagerService.HandlerParams
        void handleReturnCode() {
            if (this.mObserver != null) {
                try {
                    this.mObserver.onGetStatsCompleted(this.mStats, this.mSuccess);
                } catch (RemoteException e) {
                    Slog.i(PackageManagerService.TAG, "Observer no longer exists.");
                }
            }
        }

        @Override // com.android.server.pm.PackageManagerService.HandlerParams
        void handleServiceError() {
            Slog.e(PackageManagerService.TAG, "Could not measure application " + this.mStats.packageName + " external storage");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long calculateDirectorySize(IMediaContainerService mcs, File[] paths) throws RemoteException {
        long result = 0;
        for (File path : paths) {
            result += mcs.calculateDirectorySize(path.getAbsolutePath());
        }
        return result;
    }

    private static void clearDirectory(IMediaContainerService mcs, File[] paths) {
        for (File path : paths) {
            try {
                mcs.clearDirectory(path.getAbsolutePath());
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$InstallParams.class */
    public class InstallParams extends HandlerParams {
        final IPackageInstallObserver observer;
        int flags;
        private final Uri mPackageURI;
        final String installerPackageName;
        final VerificationParams verificationParams;
        private InstallArgs mArgs;
        private int mRet;
        private File mTempPackage;
        final ContainerEncryptionParams encryptionParams;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.InstallParams.handleStartCopy():void, file: PackageManagerService$InstallParams.class
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
        @Override // com.android.server.pm.PackageManagerService.HandlerParams
        public void handleStartCopy() throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.InstallParams.handleStartCopy():void, file: PackageManagerService$InstallParams.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.InstallParams.handleStartCopy():void");
        }

        InstallParams(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName, VerificationParams verificationParams, ContainerEncryptionParams encryptionParams, UserHandle user) {
            super(user);
            this.mPackageURI = packageURI;
            this.flags = flags;
            this.observer = observer;
            this.installerPackageName = installerPackageName;
            this.verificationParams = verificationParams;
            this.encryptionParams = encryptionParams;
        }

        public String toString() {
            return "InstallParams{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.mPackageURI + "}";
        }

        public ManifestDigest getManifestDigest() {
            if (this.verificationParams == null) {
                return null;
            }
            return this.verificationParams.getManifestDigest();
        }

        private int installLocationPolicy(PackageInfoLite pkgLite, int flags) {
            String packageName = pkgLite.packageName;
            int installLocation = pkgLite.installLocation;
            boolean onSd = (flags & 8) != 0;
            synchronized (PackageManagerService.this.mPackages) {
                PackageParser.Package pkg = PackageManagerService.this.mPackages.get(packageName);
                if (pkg != null) {
                    if ((flags & 2) != 0) {
                        if ((flags & 128) == 0 && pkgLite.versionCode < pkg.mVersionCode) {
                            Slog.w(PackageManagerService.TAG, "Can't install update of " + packageName + " update version " + pkgLite.versionCode + " is older than installed version " + pkg.mVersionCode);
                            return -7;
                        } else if ((pkg.applicationInfo.flags & 1) != 0) {
                            if (onSd) {
                                Slog.w(PackageManagerService.TAG, "Cannot install update to system app on sdcard");
                                return -3;
                            }
                            return 1;
                        } else if (onSd) {
                            return 2;
                        } else {
                            if (installLocation == 1) {
                                return 1;
                            }
                            if (installLocation != 2) {
                                if (PackageManagerService.isExternal(pkg)) {
                                    return 2;
                                }
                                return 1;
                            }
                        }
                    } else {
                        return -4;
                    }
                }
                if (onSd) {
                    return 2;
                }
                return pkgLite.recommendedInstallLocation;
            }
        }

        /* renamed from: com.android.server.pm.PackageManagerService$InstallParams$1  reason: invalid class name */
        /* loaded from: PackageManagerService$InstallParams$1.class */
        class AnonymousClass1 extends BroadcastReceiver {
            final /* synthetic */ int val$verificationId;

            AnonymousClass1(int i) {
                this.val$verificationId = i;
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Message msg = PackageManagerService.this.mHandler.obtainMessage(16);
                msg.arg1 = this.val$verificationId;
                PackageManagerService.this.mHandler.sendMessageDelayed(msg, PackageManagerService.this.getVerificationTimeout());
            }
        }

        @Override // com.android.server.pm.PackageManagerService.HandlerParams
        void handleReturnCode() {
            if (this.mArgs != null) {
                PackageManagerService.this.processPendingInstall(this.mArgs, this.mRet);
                if (this.mTempPackage != null && !this.mTempPackage.delete()) {
                    Slog.w(PackageManagerService.TAG, "Couldn't delete temporary file: " + this.mTempPackage.getAbsolutePath());
                }
            }
        }

        @Override // com.android.server.pm.PackageManagerService.HandlerParams
        void handleServiceError() {
            this.mArgs = PackageManagerService.this.createInstallArgs(this);
            this.mRet = -110;
        }

        public boolean isForwardLocked() {
            return (this.flags & 1) != 0;
        }

        public Uri getPackageUri() {
            if (this.mTempPackage != null) {
                return Uri.fromFile(this.mTempPackage);
            }
            return this.mPackageURI;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$MoveParams.class */
    public class MoveParams extends HandlerParams {
        final IPackageMoveObserver observer;
        final int flags;
        final String packageName;
        final InstallArgs srcArgs;
        final InstallArgs targetArgs;
        int uid;
        int mRet;

        MoveParams(InstallArgs srcArgs, IPackageMoveObserver observer, int flags, String packageName, String dataDir, int uid, UserHandle user) {
            super(user);
            this.srcArgs = srcArgs;
            this.observer = observer;
            this.flags = flags;
            this.packageName = packageName;
            this.uid = uid;
            if (srcArgs != null) {
                Uri packageUri = Uri.fromFile(new File(srcArgs.getCodePath()));
                this.targetArgs = PackageManagerService.this.createInstallArgs(packageUri, flags, packageName, dataDir);
                return;
            }
            this.targetArgs = null;
        }

        public String toString() {
            return "MoveParams{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.packageName + "}";
        }

        @Override // com.android.server.pm.PackageManagerService.HandlerParams
        public void handleStartCopy() throws RemoteException {
            this.mRet = -4;
            if (!this.targetArgs.checkFreeStorage(PackageManagerService.this.mContainerService)) {
                Log.w(PackageManagerService.TAG, "Insufficient storage to install");
                return;
            }
            this.mRet = this.srcArgs.doPreCopy();
            if (this.mRet != 1) {
                return;
            }
            this.mRet = this.targetArgs.copyApk(PackageManagerService.this.mContainerService, false);
            if (this.mRet != 1) {
                this.srcArgs.doPostCopy(this.uid);
                return;
            }
            this.mRet = this.srcArgs.doPostCopy(this.uid);
            if (this.mRet != 1) {
                return;
            }
            this.mRet = this.targetArgs.doPreInstall(this.mRet);
            if (this.mRet != 1) {
            }
        }

        @Override // com.android.server.pm.PackageManagerService.HandlerParams
        void handleReturnCode() {
            this.targetArgs.doPostInstall(this.mRet, this.uid);
            int currentStatus = -6;
            if (this.mRet == 1) {
                currentStatus = 1;
            } else if (this.mRet == -4) {
                currentStatus = -1;
            }
            PackageManagerService.this.processPendingMove(this, currentStatus);
        }

        @Override // com.android.server.pm.PackageManagerService.HandlerParams
        void handleServiceError() {
            this.mRet = -110;
        }
    }

    private static boolean installOnSd(int flags) {
        if ((flags & 16) == 0 && (flags & 8) != 0) {
            return true;
        }
        return false;
    }

    private static boolean installForwardLocked(int flags) {
        return (flags & 1) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InstallArgs createInstallArgs(InstallParams params) {
        if (installOnSd(params.flags) || params.isForwardLocked()) {
            return new AsecInstallArgs(params);
        }
        return new FileInstallArgs(params);
    }

    private InstallArgs createInstallArgs(int flags, String fullCodePath, String fullResourcePath, String nativeLibraryPath) {
        boolean isInAsec;
        if (installOnSd(flags)) {
            isInAsec = true;
        } else if (installForwardLocked(flags) && !fullCodePath.startsWith(this.mDrmAppPrivateInstallDir.getAbsolutePath())) {
            isInAsec = true;
        } else {
            isInAsec = false;
        }
        if (isInAsec) {
            return new AsecInstallArgs(fullCodePath, fullResourcePath, nativeLibraryPath, installOnSd(flags), installForwardLocked(flags));
        }
        return new FileInstallArgs(fullCodePath, fullResourcePath, nativeLibraryPath);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InstallArgs createInstallArgs(Uri packageURI, int flags, String pkgName, String dataDir) {
        if (installOnSd(flags) || installForwardLocked(flags)) {
            String cid = getNextCodePath(packageURI.getPath(), pkgName, "/pkg.apk");
            return new AsecInstallArgs(packageURI, cid, installOnSd(flags), installForwardLocked(flags));
        }
        return new FileInstallArgs(packageURI, pkgName, dataDir);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$InstallArgs.class */
    public static abstract class InstallArgs {
        final IPackageInstallObserver observer;
        final int flags;
        final Uri packageURI;
        final String installerPackageName;
        final ManifestDigest manifestDigest;
        final UserHandle user;

        abstract void createCopyFile();

        abstract int copyApk(IMediaContainerService iMediaContainerService, boolean z) throws RemoteException;

        abstract int doPreInstall(int i);

        abstract boolean doRename(int i, String str, String str2);

        abstract int doPostInstall(int i, int i2);

        abstract String getCodePath();

        abstract String getResourcePath();

        abstract String getNativeLibraryPath();

        abstract void cleanUpResourcesLI();

        abstract boolean doPostDeleteLI(boolean z);

        abstract boolean checkFreeStorage(IMediaContainerService iMediaContainerService) throws RemoteException;

        InstallArgs(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName, ManifestDigest manifestDigest, UserHandle user) {
            this.packageURI = packageURI;
            this.flags = flags;
            this.observer = observer;
            this.installerPackageName = installerPackageName;
            this.manifestDigest = manifestDigest;
            this.user = user;
        }

        int doPreCopy() {
            return 1;
        }

        int doPostCopy(int uid) {
            return 1;
        }

        protected boolean isFwdLocked() {
            return (this.flags & 1) != 0;
        }

        UserHandle getUser() {
            return this.user;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$FileInstallArgs.class */
    public class FileInstallArgs extends InstallArgs {
        File installDir;
        String codeFileName;
        String resourceFileName;
        String libraryPath;
        boolean created;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.FileInstallArgs.checkFreeStorage(com.android.internal.app.IMediaContainerService):boolean, file: PackageManagerService$FileInstallArgs.class
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
        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        boolean checkFreeStorage(com.android.internal.app.IMediaContainerService r1) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.FileInstallArgs.checkFreeStorage(com.android.internal.app.IMediaContainerService):boolean, file: PackageManagerService$FileInstallArgs.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.FileInstallArgs.checkFreeStorage(com.android.internal.app.IMediaContainerService):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.FileInstallArgs.copyApk(com.android.internal.app.IMediaContainerService, boolean):int, file: PackageManagerService$FileInstallArgs.class
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
        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        int copyApk(com.android.internal.app.IMediaContainerService r1, boolean r2) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.FileInstallArgs.copyApk(com.android.internal.app.IMediaContainerService, boolean):int, file: PackageManagerService$FileInstallArgs.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.FileInstallArgs.copyApk(com.android.internal.app.IMediaContainerService, boolean):int");
        }

        FileInstallArgs(InstallParams params) {
            super(params.getPackageUri(), params.observer, params.flags, params.installerPackageName, params.getManifestDigest(), params.getUser());
            this.created = false;
        }

        FileInstallArgs(String fullCodePath, String fullResourcePath, String nativeLibraryPath) {
            super(null, null, 0, null, null, null);
            this.created = false;
            File codeFile = new File(fullCodePath);
            this.installDir = codeFile.getParentFile();
            this.codeFileName = fullCodePath;
            this.resourceFileName = fullResourcePath;
            this.libraryPath = nativeLibraryPath;
        }

        FileInstallArgs(Uri packageURI, String pkgName, String dataDir) {
            super(packageURI, null, 0, null, null, null);
            this.created = false;
            this.installDir = isFwdLocked() ? PackageManagerService.this.mDrmAppPrivateInstallDir : PackageManagerService.this.mAppInstallDir;
            String apkName = PackageManagerService.getNextCodePath(null, pkgName, ".apk");
            this.codeFileName = new File(this.installDir, apkName + ".apk").getPath();
            this.resourceFileName = getResourcePathFromCodePath();
            this.libraryPath = new File(PackageManagerService.this.mAppLibInstallDir, pkgName).getPath();
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        String getCodePath() {
            return this.codeFileName;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        void createCopyFile() {
            this.installDir = isFwdLocked() ? PackageManagerService.this.mDrmAppPrivateInstallDir : PackageManagerService.this.mAppInstallDir;
            this.codeFileName = PackageManagerService.this.createTempPackageFile(this.installDir).getPath();
            this.resourceFileName = getResourcePathFromCodePath();
            this.libraryPath = getLibraryPathFromCodePath();
            this.created = true;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        int doPreInstall(int status) {
            if (status != 1) {
                cleanUp();
            }
            return status;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        boolean doRename(int status, String pkgName, String oldCodePath) {
            if (status != 1) {
                cleanUp();
                return false;
            }
            File oldCodeFile = new File(getCodePath());
            File oldResourceFile = new File(getResourcePath());
            File oldLibraryFile = new File(getNativeLibraryPath());
            String apkName = PackageManagerService.getNextCodePath(oldCodePath, pkgName, ".apk");
            File newCodeFile = new File(this.installDir, apkName + ".apk");
            if (!oldCodeFile.renameTo(newCodeFile)) {
                return false;
            }
            this.codeFileName = newCodeFile.getPath();
            File newResFile = new File(getResourcePathFromCodePath());
            if (isFwdLocked() && !oldResourceFile.renameTo(newResFile)) {
                return false;
            }
            this.resourceFileName = newResFile.getPath();
            File newLibraryFile = new File(getLibraryPathFromCodePath());
            if (newLibraryFile.exists()) {
                NativeLibraryHelper.removeNativeBinariesFromDirLI(newLibraryFile);
                newLibraryFile.delete();
            }
            if (!oldLibraryFile.renameTo(newLibraryFile)) {
                Slog.e(PackageManagerService.TAG, "Cannot rename native library directory " + oldLibraryFile.getPath() + " to " + newLibraryFile.getPath());
                return false;
            }
            this.libraryPath = newLibraryFile.getPath();
            if (!setPermissions() || !SELinux.restorecon(newCodeFile)) {
                return false;
            }
            return true;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        int doPostInstall(int status, int uid) {
            if (status != 1) {
                cleanUp();
            }
            return status;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        String getResourcePath() {
            return this.resourceFileName;
        }

        private String getResourcePathFromCodePath() {
            String codePath = getCodePath();
            if (isFwdLocked()) {
                StringBuilder sb = new StringBuilder();
                sb.append(PackageManagerService.this.mAppInstallDir.getPath());
                sb.append('/');
                sb.append(PackageManagerService.getApkName(codePath));
                sb.append(".zip");
                if (codePath.endsWith(".tmp")) {
                    sb.append(".tmp");
                }
                return sb.toString();
            }
            return codePath;
        }

        private String getLibraryPathFromCodePath() {
            return new File(PackageManagerService.this.mAppLibInstallDir, PackageManagerService.getApkName(getCodePath())).getPath();
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        String getNativeLibraryPath() {
            if (this.libraryPath == null) {
                this.libraryPath = getLibraryPathFromCodePath();
            }
            return this.libraryPath;
        }

        private boolean cleanUp() {
            boolean ret = true;
            String sourceDir = getCodePath();
            String publicSourceDir = getResourcePath();
            if (sourceDir != null) {
                File sourceFile = new File(sourceDir);
                if (!sourceFile.exists()) {
                    Slog.w(PackageManagerService.TAG, "Package source " + sourceDir + " does not exist.");
                    ret = false;
                }
                sourceFile.delete();
            }
            if (publicSourceDir != null && !publicSourceDir.equals(sourceDir)) {
                File publicSourceFile = new File(publicSourceDir);
                if (!publicSourceFile.exists()) {
                    Slog.w(PackageManagerService.TAG, "Package public source " + publicSourceFile + " does not exist.");
                }
                if (publicSourceFile.exists()) {
                    publicSourceFile.delete();
                }
            }
            if (this.libraryPath != null) {
                File nativeLibraryFile = new File(this.libraryPath);
                NativeLibraryHelper.removeNativeBinariesFromDirLI(nativeLibraryFile);
                if (!nativeLibraryFile.delete()) {
                    Slog.w(PackageManagerService.TAG, "Couldn't delete native library directory " + this.libraryPath);
                }
            }
            return ret;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        void cleanUpResourcesLI() {
            int retCode;
            String sourceDir = getCodePath();
            if (cleanUp() && (retCode = PackageManagerService.this.mInstaller.rmdex(sourceDir)) < 0) {
                Slog.w(PackageManagerService.TAG, "Couldn't remove dex file for package:  at location " + sourceDir + ", retcode=" + retCode);
            }
        }

        private boolean setPermissions() {
            int retCode;
            if (!isFwdLocked() && (retCode = FileUtils.setPermissions(getCodePath(), 420, -1, -1)) != 0) {
                Slog.e(PackageManagerService.TAG, "Couldn't set new package file permissions for " + getCodePath() + ". The return code was: " + retCode);
                return false;
            }
            return true;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        boolean doPostDeleteLI(boolean delete) {
            cleanUpResourcesLI();
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAsecExternal(String cid) {
        String asecPath = PackageHelper.getSdFilesystem(cid);
        return !asecPath.startsWith(this.mAsecInternalPath);
    }

    static String cidFromCodePath(String fullCodePath) {
        int eidx = fullCodePath.lastIndexOf(Separators.SLASH);
        String subStr1 = fullCodePath.substring(0, eidx);
        int sidx = subStr1.lastIndexOf(Separators.SLASH);
        return subStr1.substring(sidx + 1, eidx);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$AsecInstallArgs.class */
    public class AsecInstallArgs extends InstallArgs {
        static final String RES_FILE_NAME = "pkg.apk";
        static final String PUBLIC_RES_FILE_NAME = "res.zip";
        String cid;
        String packagePath;
        String resourcePath;
        String libraryPath;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.AsecInstallArgs.checkFreeStorage(com.android.internal.app.IMediaContainerService):boolean, file: PackageManagerService$AsecInstallArgs.class
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
        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        boolean checkFreeStorage(com.android.internal.app.IMediaContainerService r1) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.AsecInstallArgs.checkFreeStorage(com.android.internal.app.IMediaContainerService):boolean, file: PackageManagerService$AsecInstallArgs.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.AsecInstallArgs.checkFreeStorage(com.android.internal.app.IMediaContainerService):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.AsecInstallArgs.copyApk(com.android.internal.app.IMediaContainerService, boolean):int, file: PackageManagerService$AsecInstallArgs.class
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
        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        int copyApk(com.android.internal.app.IMediaContainerService r1, boolean r2) throws android.os.RemoteException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.PackageManagerService.AsecInstallArgs.copyApk(com.android.internal.app.IMediaContainerService, boolean):int, file: PackageManagerService$AsecInstallArgs.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerService.AsecInstallArgs.copyApk(com.android.internal.app.IMediaContainerService, boolean):int");
        }

        AsecInstallArgs(InstallParams params) {
            super(params.getPackageUri(), params.observer, params.flags, params.installerPackageName, params.getManifestDigest(), params.getUser());
        }

        AsecInstallArgs(String fullCodePath, String fullResourcePath, String nativeLibraryPath, boolean isExternal, boolean isForwardLocked) {
            super(null, null, (isExternal ? 8 : 0) | (isForwardLocked ? 1 : 0), null, null, null);
            int eidx = fullCodePath.lastIndexOf(Separators.SLASH);
            String subStr1 = fullCodePath.substring(0, eidx);
            int sidx = subStr1.lastIndexOf(Separators.SLASH);
            this.cid = subStr1.substring(sidx + 1, eidx);
            setCachePath(subStr1);
        }

        AsecInstallArgs(String cid, boolean isForwardLocked) {
            super(null, null, (PackageManagerService.this.isAsecExternal(cid) ? 8 : 0) | (isForwardLocked ? 1 : 0), null, null, null);
            this.cid = cid;
            setCachePath(PackageHelper.getSdDir(cid));
        }

        AsecInstallArgs(Uri packageURI, String cid, boolean isExternal, boolean isForwardLocked) {
            super(packageURI, null, (isExternal ? 8 : 0) | (isForwardLocked ? 1 : 0), null, null, null);
            this.cid = cid;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        void createCopyFile() {
            this.cid = PackageManagerService.getTempContainerId();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final boolean isExternal() {
            return (this.flags & 8) != 0;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        String getCodePath() {
            return this.packagePath;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        String getResourcePath() {
            return this.resourcePath;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        String getNativeLibraryPath() {
            return this.libraryPath;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        int doPreInstall(int status) {
            if (status != 1) {
                PackageHelper.destroySdDir(this.cid);
            } else {
                boolean mounted = PackageHelper.isContainerMounted(this.cid);
                if (!mounted) {
                    String newCachePath = PackageHelper.mountSdDir(this.cid, PackageManagerService.this.getEncryptKey(), 1000);
                    if (newCachePath != null) {
                        setCachePath(newCachePath);
                    } else {
                        return -18;
                    }
                }
            }
            return status;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        boolean doRename(int status, String pkgName, String oldCodePath) {
            String newCachePath;
            String newCacheId = PackageManagerService.getNextCodePath(oldCodePath, pkgName, "/pkg.apk");
            if (PackageHelper.isContainerMounted(this.cid) && !PackageHelper.unMountSdDir(this.cid)) {
                Slog.i(PackageManagerService.TAG, "Failed to unmount " + this.cid + " before renaming");
                return false;
            }
            if (!PackageHelper.renameSdDir(this.cid, newCacheId)) {
                Slog.e(PackageManagerService.TAG, "Failed to rename " + this.cid + " to " + newCacheId + " which might be stale. Will try to clean up.");
                if (!PackageHelper.destroySdDir(newCacheId)) {
                    Slog.e(PackageManagerService.TAG, "Very strange. Cannot clean up stale container " + newCacheId);
                    return false;
                } else if (!PackageHelper.renameSdDir(this.cid, newCacheId)) {
                    Slog.e(PackageManagerService.TAG, "Failed to rename " + this.cid + " to " + newCacheId + " inspite of cleaning it up.");
                    return false;
                }
            }
            if (!PackageHelper.isContainerMounted(newCacheId)) {
                Slog.w(PackageManagerService.TAG, "Mounting container " + newCacheId);
                newCachePath = PackageHelper.mountSdDir(newCacheId, PackageManagerService.this.getEncryptKey(), 1000);
            } else {
                newCachePath = PackageHelper.getSdDir(newCacheId);
            }
            if (newCachePath == null) {
                Slog.w(PackageManagerService.TAG, "Failed to get cache path for  " + newCacheId);
                return false;
            }
            Log.i(PackageManagerService.TAG, "Succesfully renamed " + this.cid + " to " + newCacheId + " at new path: " + newCachePath);
            this.cid = newCacheId;
            setCachePath(newCachePath);
            return true;
        }

        private void setCachePath(String newCachePath) {
            File cachePath = new File(newCachePath);
            this.libraryPath = new File(cachePath, PackageManagerService.LIB_DIR_NAME).getPath();
            this.packagePath = new File(cachePath, RES_FILE_NAME).getPath();
            if (isFwdLocked()) {
                this.resourcePath = new File(cachePath, PUBLIC_RES_FILE_NAME).getPath();
            } else {
                this.resourcePath = this.packagePath;
            }
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        int doPostInstall(int status, int uid) {
            int groupOwner;
            String protectedFile;
            if (status != 1) {
                cleanUp();
            } else {
                if (isFwdLocked()) {
                    groupOwner = UserHandle.getSharedAppGid(uid);
                    protectedFile = RES_FILE_NAME;
                } else {
                    groupOwner = -1;
                    protectedFile = null;
                }
                if (uid < 10000 || !PackageHelper.fixSdPermissions(this.cid, groupOwner, protectedFile)) {
                    Slog.e(PackageManagerService.TAG, "Failed to finalize " + this.cid);
                    PackageHelper.destroySdDir(this.cid);
                    return -18;
                }
                boolean mounted = PackageHelper.isContainerMounted(this.cid);
                if (!mounted) {
                    PackageHelper.mountSdDir(this.cid, PackageManagerService.this.getEncryptKey(), Process.myUid());
                }
            }
            return status;
        }

        private void cleanUp() {
            PackageHelper.destroySdDir(this.cid);
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        void cleanUpResourcesLI() {
            String sourceFile = getCodePath();
            int retCode = PackageManagerService.this.mInstaller.rmdex(sourceFile);
            if (retCode < 0) {
                Slog.w(PackageManagerService.TAG, "Couldn't remove dex file for package:  at location " + sourceFile.toString() + ", retcode=" + retCode);
            }
            cleanUp();
        }

        boolean matchContainer(String app) {
            if (this.cid.startsWith(app)) {
                return true;
            }
            return false;
        }

        String getPackageName() {
            return PackageManagerService.getAsecPackageName(this.cid);
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        boolean doPostDeleteLI(boolean delete) {
            boolean ret = false;
            boolean mounted = PackageHelper.isContainerMounted(this.cid);
            if (mounted) {
                ret = PackageHelper.unMountSdDir(this.cid);
            }
            if (ret && delete) {
                cleanUpResourcesLI();
            }
            return ret;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        int doPreCopy() {
            if (isFwdLocked() && !PackageHelper.fixSdPermissions(this.cid, PackageManagerService.this.getPackageUid(PackageManagerService.DEFAULT_CONTAINER_PACKAGE, 0), RES_FILE_NAME)) {
                return -18;
            }
            return 1;
        }

        @Override // com.android.server.pm.PackageManagerService.InstallArgs
        int doPostCopy(int uid) {
            if (isFwdLocked()) {
                if (uid < 10000 || !PackageHelper.fixSdPermissions(this.cid, UserHandle.getSharedAppGid(uid), RES_FILE_NAME)) {
                    Slog.e(PackageManagerService.TAG, "Failed to finalize " + this.cid);
                    PackageHelper.destroySdDir(this.cid);
                    return -18;
                }
                return 1;
            }
            return 1;
        }
    }

    static String getAsecPackageName(String packageCid) {
        int idx = packageCid.lastIndexOf(INSTALL_PACKAGE_SUFFIX);
        if (idx == -1) {
            return packageCid;
        }
        return packageCid.substring(0, idx);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getNextCodePath(String oldCodePath, String prefix, String suffix) {
        int idx = 1;
        if (oldCodePath != null) {
            String subStr = oldCodePath;
            if (subStr.endsWith(suffix)) {
                subStr = subStr.substring(0, subStr.length() - suffix.length());
            }
            int sidx = subStr.lastIndexOf(prefix);
            if (sidx != -1) {
                String subStr2 = subStr.substring(sidx + prefix.length());
                if (subStr2 != null) {
                    if (subStr2.startsWith(INSTALL_PACKAGE_SUFFIX)) {
                        subStr2 = subStr2.substring(INSTALL_PACKAGE_SUFFIX.length());
                    }
                    try {
                        int idx2 = Integer.parseInt(subStr2);
                        if (idx2 <= 1) {
                            idx = idx2 + 1;
                        } else {
                            idx = idx2 - 1;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        String idxStr = INSTALL_PACKAGE_SUFFIX + Integer.toString(idx);
        return prefix + idxStr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean ignoreCodePath(String fullPathStr) {
        String apkName = getApkName(fullPathStr);
        int idx = apkName.lastIndexOf(INSTALL_PACKAGE_SUFFIX);
        if (idx != -1 && idx + 1 < apkName.length()) {
            String version = apkName.substring(idx + 1);
            try {
                Integer.parseInt(version);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    static String getApkName(String codePath) {
        if (codePath == null) {
            return null;
        }
        int sidx = codePath.lastIndexOf(Separators.SLASH);
        int eidx = codePath.lastIndexOf(Separators.DOT);
        if (eidx == -1) {
            eidx = codePath.length();
        } else if (eidx == 0) {
            Slog.w(TAG, " Invalid code path, " + codePath + " Not a valid apk name");
            return null;
        }
        return codePath.substring(sidx + 1, eidx);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$PackageInstalledInfo.class */
    public class PackageInstalledInfo {
        String name;
        int uid;
        int[] origUsers;
        int[] newUsers;
        PackageParser.Package pkg;
        int returnCode;
        PackageRemovedInfo removedInfo;

        PackageInstalledInfo() {
        }
    }

    private void installNewPackageLI(PackageParser.Package pkg, int parseFlags, int scanMode, UserHandle user, String installerPackageName, PackageInstalledInfo res) {
        String pkgName = pkg.packageName;
        boolean dataDirExists = getDataPathForPackage(pkg.packageName, 0).exists();
        synchronized (this.mPackages) {
            if (this.mSettings.mRenamedPackages.containsKey(pkgName)) {
                Slog.w(TAG, "Attempt to re-install " + pkgName + " without first uninstalling package running as " + this.mSettings.mRenamedPackages.get(pkgName));
                res.returnCode = -1;
            } else if (this.mPackages.containsKey(pkgName) || this.mAppDirs.containsKey(pkg.mPath)) {
                Slog.w(TAG, "Attempt to re-install " + pkgName + " without first uninstalling.");
                res.returnCode = -1;
            } else {
                this.mLastScanError = 1;
                PackageParser.Package newPackage = scanPackageLI(pkg, parseFlags, scanMode, System.currentTimeMillis(), user);
                if (newPackage == null) {
                    Slog.w(TAG, "Package couldn't be installed in " + pkg.mPath);
                    int i = this.mLastScanError;
                    res.returnCode = i;
                    if (i == 1) {
                        res.returnCode = -2;
                        return;
                    }
                    return;
                }
                updateSettingsLI(newPackage, installerPackageName, null, null, res);
                if (res.returnCode != 1) {
                    deletePackageLI(pkgName, UserHandle.ALL, false, null, null, dataDirExists ? 1 : 0, res.removedInfo, true);
                }
            }
        }
    }

    private void replacePackageLI(PackageParser.Package pkg, int parseFlags, int scanMode, UserHandle user, String installerPackageName, PackageInstalledInfo res) {
        String pkgName = pkg.packageName;
        synchronized (this.mPackages) {
            PackageParser.Package oldPackage = this.mPackages.get(pkgName);
            if (compareSignatures(oldPackage.mSignatures, pkg.mSignatures) != 0) {
                Slog.w(TAG, "New package has a different signature: " + pkgName);
                res.returnCode = PackageManager.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES;
                return;
            }
            PackageSetting ps = this.mSettings.mPackages.get(pkgName);
            int[] allUsers = sUserManager.getUserIds();
            boolean[] perUserInstalled = new boolean[allUsers.length];
            for (int i = 0; i < allUsers.length; i++) {
                perUserInstalled[i] = ps != null ? ps.getInstalled(allUsers[i]) : false;
            }
            boolean sysPkg = isSystemApp(oldPackage);
            if (sysPkg) {
                replaceSystemPackageLI(oldPackage, pkg, parseFlags, scanMode, user, allUsers, perUserInstalled, installerPackageName, res);
            } else {
                replaceNonSystemPackageLI(oldPackage, pkg, parseFlags, scanMode, user, allUsers, perUserInstalled, installerPackageName, res);
            }
        }
    }

    private void replaceNonSystemPackageLI(PackageParser.Package deletedPackage, PackageParser.Package pkg, int parseFlags, int scanMode, UserHandle user, int[] allUsers, boolean[] perUserInstalled, String installerPackageName, PackageInstalledInfo res) {
        long origUpdateTime;
        String pkgName = deletedPackage.packageName;
        boolean deletedPkg = true;
        boolean updatedSettings = false;
        if (pkg.mExtras != null) {
            origUpdateTime = ((PackageSetting) pkg.mExtras).lastUpdateTime;
        } else {
            origUpdateTime = 0;
        }
        if (!deletePackageLI(pkgName, null, true, null, null, 1, res.removedInfo, true)) {
            res.returnCode = -10;
            deletedPkg = false;
        } else {
            this.mLastScanError = 1;
            PackageParser.Package newPackage = scanPackageLI(pkg, parseFlags, scanMode | 64, System.currentTimeMillis(), user);
            if (newPackage == null) {
                Slog.w(TAG, "Package couldn't be installed in " + pkg.mPath);
                int i = this.mLastScanError;
                res.returnCode = i;
                if (i == 1) {
                    res.returnCode = -2;
                }
            } else {
                updateSettingsLI(newPackage, installerPackageName, allUsers, perUserInstalled, res);
                updatedSettings = true;
            }
        }
        if (res.returnCode != 1) {
            if (updatedSettings) {
                deletePackageLI(pkgName, null, true, allUsers, perUserInstalled, 1, res.removedInfo, true);
            }
            if (deletedPkg) {
                File restoreFile = new File(deletedPackage.mPath);
                boolean oldOnSd = isExternal(deletedPackage);
                int oldParseFlags = this.mDefParseFlags | 2 | (isForwardLocked(deletedPackage) ? 16 : 0) | (oldOnSd ? 32 : 0);
                int oldScanMode = (oldOnSd ? 0 : 1) | 8 | 64;
                if (scanPackageLI(restoreFile, oldParseFlags, oldScanMode, origUpdateTime, (UserHandle) null) == null) {
                    Slog.e(TAG, "Failed to restore package : " + pkgName + " after failed upgrade");
                    return;
                }
                synchronized (this.mPackages) {
                    updatePermissionsLPw(deletedPackage.packageName, deletedPackage, 1);
                    this.mSettings.writeLPr();
                }
                Slog.i(TAG, "Successfully restored package : " + pkgName + " after failed upgrade");
            }
        }
    }

    private void replaceSystemPackageLI(PackageParser.Package deletedPackage, PackageParser.Package pkg, int parseFlags, int scanMode, UserHandle user, int[] allUsers, boolean[] perUserInstalled, String installerPackageName, PackageInstalledInfo res) {
        boolean updatedSettings = false;
        int parseFlags2 = parseFlags | 3;
        if ((deletedPackage.applicationInfo.flags & 1073741824) != 0) {
            parseFlags2 |= 128;
        }
        String packageName = deletedPackage.packageName;
        res.returnCode = -10;
        if (packageName == null) {
            Slog.w(TAG, "Attempt to delete null packageName.");
            return;
        }
        synchronized (this.mPackages) {
            PackageParser.Package oldPkg = this.mPackages.get(packageName);
            PackageSetting oldPkgSetting = this.mSettings.mPackages.get(packageName);
            if (oldPkg == null || oldPkg.applicationInfo == null || oldPkgSetting == null) {
                Slog.w(TAG, "Couldn't find package:" + packageName + " information");
                return;
            }
            killApplication(packageName, oldPkg.applicationInfo.uid, "replace sys pkg");
            res.removedInfo.uid = oldPkg.applicationInfo.uid;
            res.removedInfo.removedPackage = packageName;
            removePackageLI(oldPkgSetting, true);
            synchronized (this.mPackages) {
                if (!this.mSettings.disableSystemPackageLPw(packageName) && deletedPackage != null) {
                    res.removedInfo.args = createInstallArgs(0, deletedPackage.applicationInfo.sourceDir, deletedPackage.applicationInfo.publicSourceDir, deletedPackage.applicationInfo.nativeLibraryDir);
                } else {
                    res.removedInfo.args = null;
                }
            }
            this.mLastScanError = 1;
            pkg.applicationInfo.flags |= 128;
            PackageParser.Package newPackage = scanPackageLI(pkg, parseFlags2, scanMode, 0L, user);
            if (newPackage == null) {
                Slog.w(TAG, "Package couldn't be installed in " + pkg.mPath);
                int i = this.mLastScanError;
                res.returnCode = i;
                if (i == 1) {
                    res.returnCode = -2;
                }
            } else {
                if (newPackage.mExtras != null) {
                    PackageSetting newPkgSetting = (PackageSetting) newPackage.mExtras;
                    newPkgSetting.firstInstallTime = oldPkgSetting.firstInstallTime;
                    newPkgSetting.lastUpdateTime = System.currentTimeMillis();
                }
                updateSettingsLI(newPackage, installerPackageName, allUsers, perUserInstalled, res);
                updatedSettings = true;
            }
            if (res.returnCode != 1) {
                if (newPackage != null) {
                    removeInstalledPackageLI(newPackage, true);
                }
                scanPackageLI(oldPkg, parseFlags2, 9, 0L, user);
                synchronized (this.mPackages) {
                    if (updatedSettings) {
                        this.mSettings.enableSystemPackageLPw(packageName);
                        this.mSettings.setInstallerPackageName(packageName, oldPkgSetting.installerPackageName);
                    }
                    this.mSettings.writeLPr();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int moveDexFilesLI(PackageParser.Package newPackage) {
        if ((newPackage.applicationInfo.flags & 4) != 0) {
            int retCode = this.mInstaller.movedex(newPackage.mScanPath, newPackage.mPath);
            if (retCode != 0) {
                if (this.mNoDexOpt) {
                    Slog.i(TAG, "dex file doesn't exist, skipping move: " + newPackage.mPath);
                    return 1;
                }
                Slog.e(TAG, "Couldn't rename dex file: " + newPackage.mPath);
                return -4;
            }
            return 1;
        }
        return 1;
    }

    private void updateSettingsLI(PackageParser.Package newPackage, String installerPackageName, int[] allUsers, boolean[] perUserInstalled, PackageInstalledInfo res) {
        PackageSetting ps;
        String pkgName = newPackage.packageName;
        synchronized (this.mPackages) {
            this.mSettings.setInstallStatus(pkgName, 0);
            this.mSettings.writeLPr();
        }
        int moveDexFilesLI = moveDexFilesLI(newPackage);
        res.returnCode = moveDexFilesLI;
        if (moveDexFilesLI != 1) {
            return;
        }
        synchronized (this.mPackages) {
            updatePermissionsLPw(newPackage.packageName, newPackage, 2 | (newPackage.permissions.size() > 0 ? 1 : 0));
            if (isSystemApp(newPackage) && (ps = this.mSettings.mPackages.get(pkgName)) != null) {
                if (res.origUsers != null) {
                    int[] arr$ = res.origUsers;
                    for (int userHandle : arr$) {
                        ps.setEnabled(0, userHandle, installerPackageName);
                    }
                }
                if (allUsers != null && perUserInstalled != null) {
                    for (int i = 0; i < allUsers.length; i++) {
                        ps.setInstalled(perUserInstalled[i], allUsers[i]);
                    }
                }
            }
            res.name = pkgName;
            res.uid = newPackage.applicationInfo.uid;
            res.pkg = newPackage;
            this.mSettings.setInstallStatus(pkgName, 1);
            this.mSettings.setInstallerPackageName(pkgName, installerPackageName);
            res.returnCode = 1;
            this.mSettings.writeLPr();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void installPackageLI(InstallArgs args, boolean newInstall, PackageInstalledInfo res) {
        int pFlags = args.flags;
        String installerPackageName = args.installerPackageName;
        File tmpPackageFile = new File(args.getCodePath());
        boolean forwardLocked = (pFlags & 1) != 0;
        boolean onSd = (pFlags & 8) != 0;
        boolean replace = false;
        int scanMode = (onSd ? 0 : 1) | 4 | 8 | (newInstall ? 16 : 0);
        res.returnCode = 1;
        int parseFlags = this.mDefParseFlags | 2 | (forwardLocked ? 16 : 0) | (onSd ? 32 : 0);
        PackageParser pp = new PackageParser(tmpPackageFile.getPath());
        pp.setSeparateProcesses(this.mSeparateProcesses);
        PackageParser.Package pkg = pp.parsePackage(tmpPackageFile, (String) null, this.mMetrics, parseFlags);
        if (pkg == null) {
            res.returnCode = pp.getParseError();
            return;
        }
        String str = pkg.packageName;
        res.name = str;
        String pkgName = str;
        if ((pkg.applicationInfo.flags & 256) != 0 && (pFlags & 4) == 0) {
            res.returnCode = -15;
        } else if (!pp.collectCertificates(pkg, parseFlags)) {
            res.returnCode = pp.getParseError();
        } else if (args.manifestDigest != null && !args.manifestDigest.equals(pkg.manifestDigest)) {
            res.returnCode = -23;
        } else {
            String oldCodePath = null;
            boolean systemApp = false;
            synchronized (this.mPackages) {
                if ((pFlags & 2) != 0) {
                    String oldName = this.mSettings.mRenamedPackages.get(pkgName);
                    if (pkg.mOriginalPackages != null && pkg.mOriginalPackages.contains(oldName) && this.mPackages.containsKey(oldName)) {
                        pkg.setPackageName(oldName);
                        pkgName = pkg.packageName;
                        replace = true;
                    } else if (this.mPackages.containsKey(pkgName)) {
                        replace = true;
                    }
                }
                PackageSetting ps = this.mSettings.mPackages.get(pkgName);
                if (ps != null) {
                    oldCodePath = this.mSettings.mPackages.get(pkgName).codePathString;
                    if (ps.pkg != null && ps.pkg.applicationInfo != null) {
                        systemApp = (ps.pkg.applicationInfo.flags & 1) != 0;
                    }
                    res.origUsers = ps.queryInstalledUsers(sUserManager.getUserIds(), true);
                }
            }
            if (systemApp && onSd) {
                Slog.w(TAG, "Cannot install updates to system apps on sdcard");
                res.returnCode = -19;
            } else if (!args.doRename(res.returnCode, pkgName, oldCodePath)) {
                res.returnCode = -4;
            } else {
                setApplicationInfoPaths(pkg, args.getCodePath(), args.getResourcePath());
                pkg.applicationInfo.nativeLibraryDir = args.getNativeLibraryPath();
                if (replace) {
                    replacePackageLI(pkg, parseFlags, scanMode, args.user, installerPackageName, res);
                } else {
                    installNewPackageLI(pkg, parseFlags, scanMode, args.user, installerPackageName, res);
                }
                synchronized (this.mPackages) {
                    PackageSetting ps2 = this.mSettings.mPackages.get(pkgName);
                    if (ps2 != null) {
                        res.newUsers = ps2.queryInstalledUsers(sUserManager.getUserIds(), true);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isForwardLocked(PackageParser.Package pkg) {
        return (pkg.applicationInfo.flags & 536870912) != 0;
    }

    private boolean isForwardLocked(PackageSetting ps) {
        return (ps.pkgFlags & 536870912) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isExternal(PackageParser.Package pkg) {
        return (pkg.applicationInfo.flags & 262144) != 0;
    }

    private static boolean isExternal(PackageSetting ps) {
        return (ps.pkgFlags & 262144) != 0;
    }

    private static boolean isSystemApp(PackageParser.Package pkg) {
        return (pkg.applicationInfo.flags & 1) != 0;
    }

    private static boolean isPrivilegedApp(PackageParser.Package pkg) {
        return (pkg.applicationInfo.flags & 1073741824) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isSystemApp(ApplicationInfo info) {
        return (info.flags & 1) != 0;
    }

    private static boolean isSystemApp(PackageSetting ps) {
        return (ps.pkgFlags & 1) != 0;
    }

    private static boolean isUpdatedSystemApp(PackageSetting ps) {
        return (ps.pkgFlags & 128) != 0;
    }

    private static boolean isUpdatedSystemApp(PackageParser.Package pkg) {
        return (pkg.applicationInfo.flags & 128) != 0;
    }

    private int packageFlagsToInstallFlags(PackageSetting ps) {
        int installFlags = 0;
        if (isExternal(ps)) {
            installFlags = 0 | 8;
        }
        if (isForwardLocked(ps)) {
            installFlags |= 1;
        }
        return installFlags;
    }

    private void deleteTempPackageFiles() {
        FilenameFilter filter = new FilenameFilter() { // from class: com.android.server.pm.PackageManagerService.6
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String name) {
                return name.startsWith("vmdl") && name.endsWith(".tmp");
            }
        };
        deleteTempPackageFilesInDirectory(this.mAppInstallDir, filter);
        deleteTempPackageFilesInDirectory(this.mDrmAppPrivateInstallDir, filter);
    }

    private static final void deleteTempPackageFilesInDirectory(File directory, FilenameFilter filter) {
        String[] tmpFilesList = directory.list(filter);
        if (tmpFilesList == null) {
            return;
        }
        for (String str : tmpFilesList) {
            File tmpFile = new File(directory, str);
            tmpFile.delete();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public File createTempPackageFile(File installDir) {
        try {
            File tmpPackageFile = File.createTempFile("vmdl", ".tmp", installDir);
            try {
                FileUtils.setPermissions(tmpPackageFile.getCanonicalPath(), 384, -1, -1);
                if (!SELinux.restorecon(tmpPackageFile)) {
                    return null;
                }
                return tmpPackageFile;
            } catch (IOException e) {
                Slog.e(TAG, "Trouble getting the canoncical path for a temp file.");
                return null;
            }
        } catch (IOException e2) {
            Slog.e(TAG, "Couldn't create temp file for downloaded package file.");
            return null;
        }
    }

    @Override // android.content.pm.IPackageManager
    public void deletePackageAsUser(final String packageName, final IPackageDeleteObserver observer, final int userId, final int flags) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DELETE_PACKAGES, null);
        int uid = Binder.getCallingUid();
        if (UserHandle.getUserId(uid) != userId) {
            this.mContext.enforceCallingPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL, "deletePackage for user " + userId);
        }
        if (isUserRestricted(userId, UserManager.DISALLOW_UNINSTALL_APPS)) {
            try {
                observer.packageDeleted(packageName, -3);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageManagerService.7
            @Override // java.lang.Runnable
            public void run() {
                PackageManagerService.this.mHandler.removeCallbacks(this);
                int returnCode = PackageManagerService.this.deletePackageX(packageName, userId, flags);
                if (observer != null) {
                    try {
                        observer.packageDeleted(packageName, returnCode);
                    } catch (RemoteException e2) {
                        Log.i(PackageManagerService.TAG, "Observer no longer exists.");
                    }
                }
            }
        });
    }

    private boolean isPackageDeviceAdmin(String packageName, int userId) {
        IDevicePolicyManager dpm = IDevicePolicyManager.Stub.asInterface(ServiceManager.getService(Context.DEVICE_POLICY_SERVICE));
        if (dpm != null) {
            try {
                if (dpm.packageHasActiveAdmins(packageName, userId)) {
                    return true;
                }
                if (dpm.isDeviceOwner(packageName)) {
                    return true;
                }
                return false;
            } catch (RemoteException e) {
                return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int deletePackageX(String packageName, int userId, int flags) {
        int[] allUsers;
        boolean[] perUserInstalled;
        boolean res;
        boolean systemUpdate;
        PackageRemovedInfo info = new PackageRemovedInfo();
        if (isPackageDeviceAdmin(packageName, userId)) {
            Slog.w(TAG, "Not removing package " + packageName + ": has active device admin");
            return -2;
        }
        boolean removedForAllUsers = false;
        synchronized (this.mPackages) {
            PackageSetting ps = this.mSettings.mPackages.get(packageName);
            allUsers = sUserManager.getUserIds();
            perUserInstalled = new boolean[allUsers.length];
            for (int i = 0; i < allUsers.length; i++) {
                perUserInstalled[i] = ps != null ? ps.getInstalled(allUsers[i]) : false;
            }
        }
        synchronized (this.mInstallLock) {
            res = deletePackageLI(packageName, (flags & 2) != 0 ? UserHandle.ALL : new UserHandle(userId), true, allUsers, perUserInstalled, flags | 65536, info, true);
            systemUpdate = info.isRemovedPackageSystemUpdate;
            if (res && !systemUpdate && this.mPackages.get(packageName) == null) {
                removedForAllUsers = true;
            }
        }
        if (res) {
            info.sendBroadcast(true, systemUpdate, removedForAllUsers);
            if (systemUpdate) {
                Bundle extras = new Bundle(1);
                extras.putInt(Intent.EXTRA_UID, info.removedAppId >= 0 ? info.removedAppId : info.uid);
                extras.putBoolean(Intent.EXTRA_REPLACING, true);
                sendPackageBroadcast(Intent.ACTION_PACKAGE_ADDED, packageName, extras, null, null, null);
                sendPackageBroadcast(Intent.ACTION_PACKAGE_REPLACED, packageName, extras, null, null, null);
                sendPackageBroadcast(Intent.ACTION_MY_PACKAGE_REPLACED, null, null, packageName, null, null);
            }
        }
        Runtime.getRuntime().gc();
        if (info.args != null) {
            synchronized (this.mInstallLock) {
                info.args.doPostDeleteLI(true);
            }
        }
        return res ? 1 : -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageManagerService$PackageRemovedInfo.class */
    public static class PackageRemovedInfo {
        String removedPackage;
        int uid = -1;
        int removedAppId = -1;
        int[] removedUsers = null;
        boolean isRemovedPackageSystemUpdate = false;
        InstallArgs args = null;

        PackageRemovedInfo() {
        }

        void sendBroadcast(boolean fullRemove, boolean replacing, boolean removedForAllUsers) {
            Bundle extras = new Bundle(1);
            extras.putInt(Intent.EXTRA_UID, this.removedAppId >= 0 ? this.removedAppId : this.uid);
            extras.putBoolean(Intent.EXTRA_DATA_REMOVED, fullRemove);
            if (replacing) {
                extras.putBoolean(Intent.EXTRA_REPLACING, true);
            }
            extras.putBoolean(Intent.EXTRA_REMOVED_FOR_ALL_USERS, removedForAllUsers);
            if (this.removedPackage != null) {
                PackageManagerService.sendPackageBroadcast(Intent.ACTION_PACKAGE_REMOVED, this.removedPackage, extras, null, null, this.removedUsers);
                if (fullRemove && !replacing) {
                    PackageManagerService.sendPackageBroadcast(Intent.ACTION_PACKAGE_FULLY_REMOVED, this.removedPackage, extras, null, null, this.removedUsers);
                }
            }
            if (this.removedAppId >= 0) {
                PackageManagerService.sendPackageBroadcast(Intent.ACTION_UID_REMOVED, null, extras, null, null, this.removedUsers);
            }
        }
    }

    private void removePackageDataLI(PackageSetting ps, int[] allUserHandles, boolean[] perUserInstalled, PackageRemovedInfo outInfo, int flags, boolean writeSettings) {
        PackageSetting deletedPs;
        String packageName = ps.name;
        removePackageLI(ps, (flags & 65536) != 0);
        synchronized (this.mPackages) {
            deletedPs = this.mSettings.mPackages.get(packageName);
            if (outInfo != null) {
                outInfo.removedPackage = packageName;
                outInfo.removedUsers = deletedPs != null ? deletedPs.queryInstalledUsers(sUserManager.getUserIds(), true) : null;
            }
        }
        if ((flags & 1) == 0) {
            removeDataDirsLI(packageName);
            schedulePackageCleaning(packageName, -1, true);
        }
        synchronized (this.mPackages) {
            if (deletedPs != null) {
                if ((flags & 1) == 0) {
                    if (outInfo != null) {
                        outInfo.removedAppId = this.mSettings.removePackageLPw(packageName);
                    }
                    if (deletedPs != null) {
                        updatePermissionsLPw(deletedPs.name, null, 0);
                        if (deletedPs.sharedUser != null) {
                            this.mSettings.updateSharedUserPermsLPw(deletedPs, this.mGlobalGids);
                        }
                    }
                    clearPackagePreferredActivitiesLPw(deletedPs.name, -1);
                }
                if (allUserHandles != null && perUserInstalled != null) {
                    for (int i = 0; i < allUserHandles.length; i++) {
                        ps.setInstalled(perUserInstalled[i], allUserHandles[i]);
                    }
                }
            }
            if (writeSettings) {
                this.mSettings.writeLPr();
            }
        }
        if (outInfo != null) {
            removeKeystoreDataIfNeeded(-1, outInfo.removedAppId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean locationIsPrivileged(File path) {
        try {
            String privilegedAppDir = new File(Environment.getRootDirectory(), "priv-app").getCanonicalPath();
            return path.getCanonicalPath().startsWith(privilegedAppDir);
        } catch (IOException e) {
            Slog.e(TAG, "Unable to access code path " + path);
            return false;
        }
    }

    private boolean deleteSystemPackageLI(PackageSetting newPs, int[] allUserHandles, boolean[] perUserInstalled, int flags, PackageRemovedInfo outInfo, boolean writeSettings) {
        PackageSetting disabledPs;
        int flags2;
        boolean applyUserRestrictions = (allUserHandles == null || perUserInstalled == null) ? false : true;
        synchronized (this.mPackages) {
            disabledPs = this.mSettings.getDisabledSystemPkgLPr(newPs.name);
        }
        if (disabledPs == null) {
            Slog.w(TAG, "Attempt to delete unknown system package " + newPs.name);
            return false;
        }
        outInfo.isRemovedPackageSystemUpdate = true;
        if (disabledPs.versionCode < newPs.versionCode) {
            flags2 = flags & (-2);
        } else {
            flags2 = flags | 1;
        }
        boolean ret = deleteInstalledPackageLI(newPs, true, flags2, allUserHandles, perUserInstalled, outInfo, writeSettings);
        if (!ret) {
            return false;
        }
        synchronized (this.mPackages) {
            this.mSettings.enableSystemPackageLPw(newPs.name);
            NativeLibraryHelper.removeNativeBinariesLI(newPs.nativeLibraryPathString);
        }
        int parseFlags = 5;
        if (locationIsPrivileged(disabledPs.codePath)) {
            parseFlags = 5 | 128;
        }
        PackageParser.Package newPkg = scanPackageLI(disabledPs.codePath, parseFlags, 33, 0L, (UserHandle) null);
        if (newPkg == null) {
            Slog.w(TAG, "Failed to restore system package:" + newPs.name + " with error:" + this.mLastScanError);
            return false;
        }
        synchronized (this.mPackages) {
            updatePermissionsLPw(newPkg.packageName, newPkg, 3);
            if (applyUserRestrictions) {
                PackageSetting ps = this.mSettings.mPackages.get(newPkg.packageName);
                for (int i = 0; i < allUserHandles.length; i++) {
                    ps.setInstalled(perUserInstalled[i], allUserHandles[i]);
                }
                this.mSettings.writeAllUsersPackageRestrictionsLPr();
            }
            if (writeSettings) {
                this.mSettings.writeLPr();
            }
        }
        return true;
    }

    private boolean deleteInstalledPackageLI(PackageSetting ps, boolean deleteCodeAndResources, int flags, int[] allUserHandles, boolean[] perUserInstalled, PackageRemovedInfo outInfo, boolean writeSettings) {
        if (outInfo != null) {
            outInfo.uid = ps.appId;
        }
        removePackageDataLI(ps, allUserHandles, perUserInstalled, outInfo, flags, writeSettings);
        if (deleteCodeAndResources && outInfo != null) {
            outInfo.args = createInstallArgs(packageFlagsToInstallFlags(ps), ps.codePathString, ps.resourcePathString, ps.nativeLibraryPathString);
            return true;
        }
        return true;
    }

    private boolean deletePackageLI(String packageName, UserHandle user, boolean deleteCodeAndResources, int[] allUserHandles, boolean[] perUserInstalled, int flags, PackageRemovedInfo outInfo, boolean writeSettings) {
        boolean ret;
        if (packageName == null) {
            Slog.w(TAG, "Attempt to delete null packageName.");
            return false;
        }
        int removeUser = -1;
        int appId = -1;
        synchronized (this.mPackages) {
            PackageSetting ps = this.mSettings.mPackages.get(packageName);
            if (ps == null) {
                Slog.w(TAG, "Package named '" + packageName + "' doesn't exist.");
                return false;
            }
            if ((!isSystemApp(ps) || (flags & 4) != 0) && user != null && user.getIdentifier() != -1) {
                ps.setUserState(user.getIdentifier(), 0, false, true, true, false, null, null, null);
                if (!isSystemApp(ps)) {
                    if (ps.isAnyInstalled(sUserManager.getUserIds())) {
                        removeUser = user.getIdentifier();
                        appId = ps.appId;
                        this.mSettings.writePackageRestrictionsLPr(removeUser);
                    } else {
                        ps.setInstalled(true, user.getIdentifier());
                    }
                } else {
                    removeUser = user.getIdentifier();
                    appId = ps.appId;
                    this.mSettings.writePackageRestrictionsLPr(removeUser);
                }
            }
            if (removeUser >= 0) {
                if (outInfo != null) {
                    outInfo.removedPackage = packageName;
                    outInfo.removedAppId = appId;
                    outInfo.removedUsers = new int[]{removeUser};
                }
                this.mInstaller.clearUserData(packageName, removeUser);
                removeKeystoreDataIfNeeded(removeUser, appId);
                schedulePackageCleaning(packageName, removeUser, false);
                return true;
            } else if (0 != 0) {
                removePackageDataLI(ps, null, null, outInfo, flags, writeSettings);
                return true;
            } else {
                this.mSettings.mKeySetManager.removeAppKeySetData(packageName);
                if (isSystemApp(ps)) {
                    ret = deleteSystemPackageLI(ps, allUserHandles, perUserInstalled, flags, outInfo, writeSettings);
                } else {
                    killApplication(packageName, ps.appId, "uninstall pkg");
                    ret = deleteInstalledPackageLI(ps, deleteCodeAndResources, flags, allUserHandles, perUserInstalled, outInfo, writeSettings);
                }
                return ret;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PackageManagerService$ClearStorageConnection.class */
    public final class ClearStorageConnection implements ServiceConnection {
        IMediaContainerService mContainerService;

        private ClearStorageConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (this) {
                this.mContainerService = IMediaContainerService.Stub.asInterface(service);
                notifyAll();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    @Override // android.content.pm.IPackageManager
    public void clearApplicationUserData(final String packageName, final IPackageDataObserver observer, final int userId) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CLEAR_APP_USER_DATA, null);
        enforceCrossUserPermission(Binder.getCallingUid(), userId, true, "clear application data");
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageManagerService.8
            @Override // java.lang.Runnable
            public void run() {
                boolean succeeded;
                DeviceStorageMonitorService dsm;
                PackageManagerService.this.mHandler.removeCallbacks(this);
                synchronized (PackageManagerService.this.mInstallLock) {
                    succeeded = PackageManagerService.this.clearApplicationUserDataLI(packageName, userId);
                }
                PackageManagerService.this.clearExternalStorageDataSync(packageName, userId, true);
                if (succeeded && (dsm = (DeviceStorageMonitorService) ServiceManager.getService(DeviceStorageMonitorService.SERVICE)) != null) {
                    dsm.updateMemory();
                }
                if (observer != null) {
                    try {
                        observer.onRemoveCompleted(packageName, succeeded);
                    } catch (RemoteException e) {
                        Log.i(PackageManagerService.TAG, "Observer no longer exists.");
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean clearApplicationUserDataLI(String packageName, int userId) {
        int appId;
        if (packageName == null) {
            Slog.w(TAG, "Attempt to delete null packageName.");
            return false;
        }
        boolean dataOnly = false;
        synchronized (this.mPackages) {
            PackageParser.Package p = this.mPackages.get(packageName);
            if (p == null) {
                dataOnly = true;
                PackageSetting ps = this.mSettings.mPackages.get(packageName);
                if (ps == null || ps.pkg == null) {
                    Slog.w(TAG, "Package named '" + packageName + "' doesn't exist.");
                    return false;
                }
                p = ps.pkg;
            }
            if (!dataOnly) {
                if (p == null) {
                    Slog.w(TAG, "Package named '" + packageName + "' doesn't exist.");
                    return false;
                }
                ApplicationInfo applicationInfo = p.applicationInfo;
                if (applicationInfo == null) {
                    Slog.w(TAG, "Package " + packageName + " has no applicationInfo.");
                    return false;
                }
            }
            if (p != null && p.applicationInfo != null) {
                appId = p.applicationInfo.uid;
            } else {
                appId = -1;
            }
            int retCode = this.mInstaller.clearUserData(packageName, userId);
            if (retCode < 0) {
                Slog.w(TAG, "Couldn't remove cache files for package: " + packageName);
                return false;
            }
            removeKeystoreDataIfNeeded(userId, appId);
            return true;
        }
    }

    private static void removeKeystoreDataIfNeeded(int userId, int appId) {
        if (appId < 0) {
            return;
        }
        KeyStore keyStore = KeyStore.getInstance();
        if (keyStore != null) {
            if (userId == -1) {
                int[] arr$ = sUserManager.getUserIds();
                for (int individual : arr$) {
                    keyStore.clearUid(UserHandle.getUid(individual, appId));
                }
                return;
            }
            keyStore.clearUid(UserHandle.getUid(userId, appId));
            return;
        }
        Slog.w(TAG, "Could not contact keystore to clear entries for app id " + appId);
    }

    @Override // android.content.pm.IPackageManager
    public void deleteApplicationCacheFiles(final String packageName, final IPackageDataObserver observer) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DELETE_CACHE_FILES, null);
        final int userId = UserHandle.getCallingUserId();
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageManagerService.9
            @Override // java.lang.Runnable
            public void run() {
                boolean succeded;
                PackageManagerService.this.mHandler.removeCallbacks(this);
                synchronized (PackageManagerService.this.mInstallLock) {
                    succeded = PackageManagerService.this.deleteApplicationCacheFilesLI(packageName, userId);
                }
                PackageManagerService.this.clearExternalStorageDataSync(packageName, userId, false);
                if (observer != null) {
                    try {
                        observer.onRemoveCompleted(packageName, succeded);
                    } catch (RemoteException e) {
                        Log.i(PackageManagerService.TAG, "Observer no longer exists.");
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean deleteApplicationCacheFilesLI(String packageName, int userId) {
        PackageParser.Package p;
        if (packageName == null) {
            Slog.w(TAG, "Attempt to delete null packageName.");
            return false;
        }
        synchronized (this.mPackages) {
            p = this.mPackages.get(packageName);
        }
        if (p == null) {
            Slog.w(TAG, "Package named '" + packageName + "' doesn't exist.");
            return false;
        }
        ApplicationInfo applicationInfo = p.applicationInfo;
        if (applicationInfo == null) {
            Slog.w(TAG, "Package " + packageName + " has no applicationInfo.");
            return false;
        }
        int retCode = this.mInstaller.deleteCacheFiles(packageName, userId);
        if (retCode < 0) {
            Slog.w(TAG, "Couldn't remove cache files for package: " + packageName + " u" + userId);
            return false;
        }
        return true;
    }

    @Override // android.content.pm.IPackageManager
    public void getPackageSizeInfo(String packageName, int userHandle, IPackageStatsObserver observer) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.GET_PACKAGE_SIZE, null);
        PackageStats stats = new PackageStats(packageName, userHandle);
        Message msg = this.mHandler.obtainMessage(5);
        msg.obj = new MeasureParams(stats, observer);
        this.mHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getPackageSizeInfoLI(String packageName, int userHandle, PackageStats pStats) {
        String secureContainerId;
        if (packageName == null) {
            Slog.w(TAG, "Attempt to get size of null packageName.");
            return false;
        }
        boolean dataOnly = false;
        String libDirPath = null;
        String asecPath = null;
        synchronized (this.mPackages) {
            PackageParser.Package p = this.mPackages.get(packageName);
            PackageSetting ps = this.mSettings.mPackages.get(packageName);
            if (p == null) {
                dataOnly = true;
                if (ps == null || ps.pkg == null) {
                    Slog.w(TAG, "Package named '" + packageName + "' doesn't exist.");
                    return false;
                }
                p = ps.pkg;
            }
            if (ps != null) {
                libDirPath = ps.nativeLibraryPathString;
            }
            if (p != null && ((isExternal(p) || isForwardLocked(p)) && (secureContainerId = cidFromCodePath(p.applicationInfo.sourceDir)) != null)) {
                asecPath = PackageHelper.getSdFilesystem(secureContainerId);
            }
            String publicSrcDir = null;
            if (!dataOnly) {
                ApplicationInfo applicationInfo = p.applicationInfo;
                if (applicationInfo == null) {
                    Slog.w(TAG, "Package " + packageName + " has no applicationInfo.");
                    return false;
                } else if (isForwardLocked(p)) {
                    publicSrcDir = applicationInfo.publicSourceDir;
                }
            }
            int res = this.mInstaller.getSizeInfo(packageName, userHandle, p.mPath, libDirPath, publicSrcDir, asecPath, pStats);
            if (res < 0) {
                return false;
            }
            if (!isExternal(p)) {
                pStats.codeSize += pStats.externalCodeSize;
                pStats.externalCodeSize = 0L;
                return true;
            }
            return true;
        }
    }

    @Override // android.content.pm.IPackageManager
    public void addPackageToPreferred(String packageName) {
        Slog.w(TAG, "addPackageToPreferred: this is now a no-op");
    }

    @Override // android.content.pm.IPackageManager
    public void removePackageFromPreferred(String packageName) {
        Slog.w(TAG, "removePackageFromPreferred: this is now a no-op");
    }

    @Override // android.content.pm.IPackageManager
    public List<PackageInfo> getPreferredPackages(int flags) {
        return new ArrayList();
    }

    private int getUidTargetSdkVersionLockedLPr(int uid) {
        int v;
        Object obj = this.mSettings.getUserIdLPr(uid);
        if (obj instanceof SharedUserSetting) {
            SharedUserSetting sus = (SharedUserSetting) obj;
            int vers = 10000;
            Iterator<PackageSetting> it = sus.packages.iterator();
            while (it.hasNext()) {
                PackageSetting ps = it.next();
                if (ps.pkg != null && (v = ps.pkg.applicationInfo.targetSdkVersion) < vers) {
                    vers = v;
                }
            }
            return vers;
        } else if (obj instanceof PackageSetting) {
            PackageSetting ps2 = (PackageSetting) obj;
            if (ps2.pkg != null) {
                return ps2.pkg.applicationInfo.targetSdkVersion;
            }
            return 10000;
        } else {
            return 10000;
        }
    }

    @Override // android.content.pm.IPackageManager
    public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId) {
        addPreferredActivityInternal(filter, match, set, activity, true, userId);
    }

    private void addPreferredActivityInternal(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, boolean always, int userId) {
        int callingUid = Binder.getCallingUid();
        enforceCrossUserPermission(callingUid, userId, true, "add preferred activity");
        synchronized (this.mPackages) {
            if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.SET_PREFERRED_APPLICATIONS) != 0) {
                if (getUidTargetSdkVersionLockedLPr(callingUid) < 8) {
                    Slog.w(TAG, "Ignoring addPreferredActivity() from uid " + callingUid);
                    return;
                }
                this.mContext.enforceCallingOrSelfPermission(Manifest.permission.SET_PREFERRED_APPLICATIONS, null);
            }
            Slog.i(TAG, "Adding preferred activity " + activity + " for user " + userId + " :");
            filter.dump(new LogPrinter(4, TAG), "  ");
            this.mSettings.editPreferredActivitiesLPw(userId).addFilter(new PreferredActivity(filter, match, set, activity, always));
            this.mSettings.writePackageRestrictionsLPr(userId);
        }
    }

    @Override // android.content.pm.IPackageManager
    public void replacePreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) {
        if (filter.countActions() != 1) {
            throw new IllegalArgumentException("replacePreferredActivity expects filter to have only 1 action.");
        }
        if (filter.countDataAuthorities() != 0 || filter.countDataPaths() != 0 || filter.countDataSchemes() != 0 || filter.countDataTypes() != 0) {
            throw new IllegalArgumentException("replacePreferredActivity expects filter to have no data authorities, paths, schemes or types.");
        }
        synchronized (this.mPackages) {
            if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.SET_PREFERRED_APPLICATIONS) != 0) {
                if (getUidTargetSdkVersionLockedLPr(Binder.getCallingUid()) < 8) {
                    Slog.w(TAG, "Ignoring replacePreferredActivity() from uid " + Binder.getCallingUid());
                    return;
                }
                this.mContext.enforceCallingOrSelfPermission(Manifest.permission.SET_PREFERRED_APPLICATIONS, null);
            }
            int callingUserId = UserHandle.getCallingUserId();
            ArrayList<PreferredActivity> removed = null;
            PreferredIntentResolver pir = this.mSettings.mPreferredActivities.get(callingUserId);
            if (pir != null) {
                Iterator<PreferredActivity> it = pir.filterIterator();
                String action = filter.getAction(0);
                String category = filter.getCategory(0);
                while (it.hasNext()) {
                    PreferredActivity pa = it.next();
                    if (pa.getAction(0).equals(action) && pa.getCategory(0).equals(category)) {
                        if (removed == null) {
                            removed = new ArrayList<>();
                        }
                        removed.add(pa);
                    }
                }
                if (removed != null) {
                    for (int i = 0; i < removed.size(); i++) {
                        pir.removeFilter(removed.get(i));
                    }
                }
            }
            addPreferredActivityInternal(filter, match, set, activity, true, callingUserId);
        }
    }

    @Override // android.content.pm.IPackageManager
    public void clearPackagePreferredActivities(String packageName) {
        int uid = Binder.getCallingUid();
        synchronized (this.mPackages) {
            PackageParser.Package pkg = this.mPackages.get(packageName);
            if ((pkg == null || pkg.applicationInfo.uid != uid) && this.mContext.checkCallingOrSelfPermission(Manifest.permission.SET_PREFERRED_APPLICATIONS) != 0) {
                if (getUidTargetSdkVersionLockedLPr(Binder.getCallingUid()) < 8) {
                    Slog.w(TAG, "Ignoring clearPackagePreferredActivities() from uid " + Binder.getCallingUid());
                    return;
                }
                this.mContext.enforceCallingOrSelfPermission(Manifest.permission.SET_PREFERRED_APPLICATIONS, null);
            }
            int user = UserHandle.getCallingUserId();
            if (clearPackagePreferredActivitiesLPw(packageName, user)) {
                this.mSettings.writePackageRestrictionsLPr(user);
                scheduleWriteSettingsLocked();
            }
        }
    }

    boolean clearPackagePreferredActivitiesLPw(String packageName, int userId) {
        ArrayList<PreferredActivity> removed = null;
        boolean changed = false;
        for (int i = 0; i < this.mSettings.mPreferredActivities.size(); i++) {
            int thisUserId = this.mSettings.mPreferredActivities.keyAt(i);
            PreferredIntentResolver pir = this.mSettings.mPreferredActivities.valueAt(i);
            if (userId == -1 || userId == thisUserId) {
                Iterator<PreferredActivity> it = pir.filterIterator();
                while (it.hasNext()) {
                    PreferredActivity pa = it.next();
                    if (packageName == null || (pa.mPref.mComponent.getPackageName().equals(packageName) && pa.mPref.mAlways)) {
                        if (removed == null) {
                            removed = new ArrayList<>();
                        }
                        removed.add(pa);
                    }
                }
                if (removed != null) {
                    for (int j = 0; j < removed.size(); j++) {
                        pir.removeFilter(removed.get(j));
                    }
                    changed = true;
                }
            }
        }
        return changed;
    }

    @Override // android.content.pm.IPackageManager
    public void resetPreferredActivities(int userId) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.SET_PREFERRED_APPLICATIONS, null);
        synchronized (this.mPackages) {
            int user = UserHandle.getCallingUserId();
            clearPackagePreferredActivitiesLPw(null, user);
            this.mSettings.readDefaultPreferredAppsLPw(this, user);
            this.mSettings.writePackageRestrictionsLPr(user);
            scheduleWriteSettingsLocked();
        }
    }

    @Override // android.content.pm.IPackageManager
    public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) {
        int userId = UserHandle.getCallingUserId();
        synchronized (this.mPackages) {
            PreferredIntentResolver pir = this.mSettings.mPreferredActivities.get(userId);
            if (pir != null) {
                Iterator<PreferredActivity> it = pir.filterIterator();
                while (it.hasNext()) {
                    PreferredActivity pa = it.next();
                    if (packageName == null || (pa.mPref.mComponent.getPackageName().equals(packageName) && pa.mPref.mAlways)) {
                        if (outFilters != null) {
                            outFilters.add(new IntentFilter(pa));
                        }
                        if (outActivities != null) {
                            outActivities.add(pa.mPref.mComponent);
                        }
                    }
                }
            }
        }
        return 0;
    }

    @Override // android.content.pm.IPackageManager
    public ComponentName getHomeActivities(List<ResolveInfo> allHomeCandidates) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        int callingUserId = UserHandle.getCallingUserId();
        List<ResolveInfo> list = queryIntentActivities(intent, null, 128, callingUserId);
        ResolveInfo preferred = findPreferredActivity(intent, null, 0, list, 0, true, false, false, callingUserId);
        allHomeCandidates.clear();
        if (list != null) {
            for (ResolveInfo ri : list) {
                allHomeCandidates.add(ri);
            }
        }
        if (preferred == null || preferred.activityInfo == null) {
            return null;
        }
        return new ComponentName(preferred.activityInfo.packageName, preferred.activityInfo.name);
    }

    @Override // android.content.pm.IPackageManager
    public void setApplicationEnabledSetting(String appPackageName, int newState, int flags, int userId, String callingPackage) {
        if (sUserManager.exists(userId)) {
            if (callingPackage == null) {
                callingPackage = Integer.toString(Binder.getCallingUid());
            }
            setEnabledSetting(appPackageName, null, newState, flags, userId, callingPackage);
        }
    }

    @Override // android.content.pm.IPackageManager
    public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags, int userId) {
        if (sUserManager.exists(userId)) {
            setEnabledSetting(componentName.getPackageName(), componentName.getClassName(), newState, flags, userId, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPackageChangedBroadcast(String packageName, boolean killFlag, ArrayList<String> componentNames, int packageUid) {
        Bundle extras = new Bundle(4);
        extras.putString(Intent.EXTRA_CHANGED_COMPONENT_NAME, componentNames.get(0));
        String[] nameList = new String[componentNames.size()];
        componentNames.toArray(nameList);
        extras.putStringArray(Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST, nameList);
        extras.putBoolean(Intent.EXTRA_DONT_KILL_APP, killFlag);
        extras.putInt(Intent.EXTRA_UID, packageUid);
        sendPackageBroadcast(Intent.ACTION_PACKAGE_CHANGED, packageName, extras, null, null, new int[]{UserHandle.getUserId(packageUid)});
    }

    @Override // android.content.pm.IPackageManager
    public void setPackageStoppedState(String packageName, boolean stopped, int userId) {
        if (sUserManager.exists(userId)) {
            int uid = Binder.getCallingUid();
            int permission = this.mContext.checkCallingOrSelfPermission(Manifest.permission.CHANGE_COMPONENT_ENABLED_STATE);
            boolean allowedByPermission = permission == 0;
            enforceCrossUserPermission(uid, userId, true, "stop package");
            synchronized (this.mPackages) {
                if (this.mSettings.setPackageStoppedStateLPw(packageName, stopped, allowedByPermission, uid, userId)) {
                    scheduleWritePackageRestrictionsLocked(userId);
                }
            }
        }
    }

    @Override // android.content.pm.IPackageManager
    public String getInstallerPackageName(String packageName) {
        String installerPackageNameLPr;
        synchronized (this.mPackages) {
            installerPackageNameLPr = this.mSettings.getInstallerPackageNameLPr(packageName);
        }
        return installerPackageNameLPr;
    }

    @Override // android.content.pm.IPackageManager
    public int getApplicationEnabledSetting(String packageName, int userId) {
        int applicationEnabledSettingLPr;
        if (sUserManager.exists(userId)) {
            int uid = Binder.getCallingUid();
            enforceCrossUserPermission(uid, userId, false, "get enabled");
            synchronized (this.mPackages) {
                applicationEnabledSettingLPr = this.mSettings.getApplicationEnabledSettingLPr(packageName, userId);
            }
            return applicationEnabledSettingLPr;
        }
        return 2;
    }

    @Override // android.content.pm.IPackageManager
    public int getComponentEnabledSetting(ComponentName componentName, int userId) {
        int componentEnabledSettingLPr;
        if (sUserManager.exists(userId)) {
            int uid = Binder.getCallingUid();
            enforceCrossUserPermission(uid, userId, false, "get component enabled");
            synchronized (this.mPackages) {
                componentEnabledSettingLPr = this.mSettings.getComponentEnabledSettingLPr(componentName, userId);
            }
            return componentEnabledSettingLPr;
        }
        return 2;
    }

    @Override // android.content.pm.IPackageManager
    public void enterSafeMode() {
        enforceSystemOrRoot("Only the system can request entering safe mode");
        if (!this.mSystemReady) {
            this.mSafeMode = true;
        }
    }

    @Override // android.content.pm.IPackageManager
    public void systemReady() {
        this.mSystemReady = true;
        boolean compatibilityModeEnabled = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.COMPATIBILITY_MODE, 1) == 1;
        PackageParser.setCompatibilityModeEnabled(compatibilityModeEnabled);
        synchronized (this.mPackages) {
            ArrayList<PreferredActivity> removed = new ArrayList<>();
            for (int i = 0; i < this.mSettings.mPreferredActivities.size(); i++) {
                PreferredIntentResolver pir = this.mSettings.mPreferredActivities.valueAt(i);
                removed.clear();
                for (PreferredActivity pa : pir.filterSet()) {
                    if (this.mActivities.mActivities.get(pa.mPref.mComponent) == null) {
                        removed.add(pa);
                    }
                }
                if (removed.size() > 0) {
                    for (int j = 0; j < removed.size(); j++) {
                        PreferredActivity pa2 = removed.get(i);
                        Slog.w(TAG, "Removing dangling preferred activity: " + pa2.mPref.mComponent);
                        pir.removeFilter(pa2);
                    }
                    this.mSettings.writePackageRestrictionsLPr(this.mSettings.mPreferredActivities.keyAt(i));
                }
            }
        }
        sUserManager.systemReady();
    }

    @Override // android.content.pm.IPackageManager
    public boolean isSafeMode() {
        return this.mSafeMode;
    }

    @Override // android.content.pm.IPackageManager
    public boolean hasSystemUidErrors() {
        return this.mHasSystemUidErrors;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String arrayToString(int[] array) {
        StringBuffer buf = new StringBuffer(128);
        buf.append('[');
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(array[i]);
            }
        }
        buf.append(']');
        return buf.toString();
    }

    /* loaded from: PackageManagerService$DumpState.class */
    static class DumpState {
        public static final int DUMP_LIBS = 1;
        public static final int DUMP_FEATURES = 2;
        public static final int DUMP_RESOLVERS = 4;
        public static final int DUMP_PERMISSIONS = 8;
        public static final int DUMP_PACKAGES = 16;
        public static final int DUMP_SHARED_USERS = 32;
        public static final int DUMP_MESSAGES = 64;
        public static final int DUMP_PROVIDERS = 128;
        public static final int DUMP_VERIFIERS = 256;
        public static final int DUMP_PREFERRED = 512;
        public static final int DUMP_PREFERRED_XML = 1024;
        public static final int DUMP_KEYSETS = 2048;
        public static final int OPTION_SHOW_FILTERS = 1;
        private int mTypes;
        private int mOptions;
        private boolean mTitlePrinted;
        private SharedUserSetting mSharedUser;

        DumpState() {
        }

        public boolean isDumping(int type) {
            return (this.mTypes == 0 && type != 1024) || (this.mTypes & type) != 0;
        }

        public void setDump(int type) {
            this.mTypes |= type;
        }

        public boolean isOptionEnabled(int option) {
            return (this.mOptions & option) != 0;
        }

        public void setOptionEnabled(int option) {
            this.mOptions |= option;
        }

        public boolean onTitlePrinted() {
            boolean printed = this.mTitlePrinted;
            this.mTitlePrinted = true;
            return printed;
        }

        public boolean getTitlePrinted() {
            return this.mTitlePrinted;
        }

        public void setTitlePrinted(boolean enabled) {
            this.mTitlePrinted = enabled;
        }

        public SharedUserSetting getSharedUser() {
            return this.mSharedUser;
        }

        public void setSharedUser(SharedUserSetting user) {
            this.mSharedUser = user;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getEncryptKey() {
        try {
            String sdEncKey = SystemKeyStore.getInstance().retrieveKeyHexString(SD_ENCRYPTION_KEYSTORE_NAME);
            if (sdEncKey == null) {
                sdEncKey = SystemKeyStore.getInstance().generateNewKeyHexString(128, SD_ENCRYPTION_ALGORITHM, SD_ENCRYPTION_KEYSTORE_NAME);
                if (sdEncKey == null) {
                    Slog.e(TAG, "Failed to create encryption keys");
                    return null;
                }
            }
            return sdEncKey;
        } catch (IOException ioe) {
            Slog.e(TAG, "Failed to retrieve encryption keys with exception: " + ioe);
            return null;
        } catch (NoSuchAlgorithmException nsae) {
            Slog.e(TAG, "Failed to create encryption keys with exception: " + nsae);
            return null;
        }
    }

    static String getTempContainerId() {
        int tmpIdx = 1;
        String[] list = PackageHelper.getSecureContainerList();
        if (list != null) {
            for (String name : list) {
                if (name != null && name.startsWith(mTempContainerPrefix)) {
                    String subStr = name.substring(mTempContainerPrefix.length());
                    try {
                        int cid = Integer.parseInt(subStr);
                        if (cid >= tmpIdx) {
                            tmpIdx = cid + 1;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        return mTempContainerPrefix + tmpIdx;
    }

    @Override // android.content.pm.IPackageManager
    public void updateExternalMediaStatus(final boolean mediaStatus, final boolean reportStatus) {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 0 && callingUid != 1000) {
            throw new SecurityException("Media status can only be updated by the system");
        }
        synchronized (this.mPackages) {
            Log.i(TAG, "Updating external media status from " + (this.mMediaMounted ? Environment.MEDIA_MOUNTED : Environment.MEDIA_UNMOUNTED) + " to " + (mediaStatus ? Environment.MEDIA_MOUNTED : Environment.MEDIA_UNMOUNTED));
            if (mediaStatus == this.mMediaMounted) {
                Message msg = this.mHandler.obtainMessage(12, reportStatus ? 1 : 0, -1);
                this.mHandler.sendMessage(msg);
                return;
            }
            this.mMediaMounted = mediaStatus;
            this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageManagerService.10
                @Override // java.lang.Runnable
                public void run() {
                    PackageManagerService.this.updateExternalMediaStatusInner(mediaStatus, reportStatus, true);
                }
            });
        }
    }

    public void scanAvailableAsecs() {
        updateExternalMediaStatusInner(true, false, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateExternalMediaStatusInner(boolean isMounted, boolean reportStatus, boolean externalStorage) {
        int[] uidArr = null;
        HashSet<String> removeCids = new HashSet<>();
        HashMap<AsecInstallArgs, String> processCids = new HashMap<>();
        String[] list = PackageHelper.getSecureContainerList();
        if (list == null || list.length == 0) {
            Log.i(TAG, "No secure containers on sdcard");
        } else {
            int[] uidList = new int[list.length];
            int num = 0;
            synchronized (this.mPackages) {
                for (String cid : list) {
                    String pkgName = getAsecPackageName(cid);
                    if (pkgName == null) {
                        removeCids.add(cid);
                    } else {
                        PackageSetting ps = this.mSettings.mPackages.get(pkgName);
                        if (ps == null) {
                            Log.i(TAG, "Deleting container with no matching settings " + cid);
                            removeCids.add(cid);
                        } else if (!externalStorage || isMounted || isExternal(ps)) {
                            AsecInstallArgs args = new AsecInstallArgs(cid, isForwardLocked(ps));
                            if (ps.codePathString != null && ps.codePathString.equals(args.getCodePath())) {
                                processCids.put(args, ps.codePathString);
                                int uid = ps.appId;
                                if (uid != -1) {
                                    int i = num;
                                    num++;
                                    uidList[i] = uid;
                                }
                            } else {
                                Log.i(TAG, "Deleting stale container for " + cid);
                                removeCids.add(cid);
                            }
                        }
                    }
                }
            }
            if (num > 0) {
                Arrays.sort(uidList, 0, num);
                uidArr = new int[num];
                uidArr[0] = uidList[0];
                int di = 0;
                for (int i2 = 1; i2 < num; i2++) {
                    if (uidList[i2 - 1] != uidList[i2]) {
                        int i3 = di;
                        di++;
                        uidArr[i3] = uidList[i2];
                    }
                }
            }
        }
        if (isMounted) {
            loadMediaPackages(processCids, uidArr, removeCids);
            startCleaningPackages();
            return;
        }
        unloadMediaPackages(processCids, uidArr, reportStatus);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendResourcesChangedBroadcast(boolean mediaStatus, boolean replacing, ArrayList<String> pkgList, int[] uidArr, IIntentReceiver finishedReceiver) {
        int size = pkgList.size();
        if (size > 0) {
            Bundle extras = new Bundle();
            extras.putStringArray("android.intent.extra.changed_package_list", (String[]) pkgList.toArray(new String[size]));
            if (uidArr != null) {
                extras.putIntArray("android.intent.extra.changed_uid_list", uidArr);
            }
            if (replacing && !mediaStatus) {
                extras.putBoolean(Intent.EXTRA_REPLACING, replacing);
            }
            String action = mediaStatus ? "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE" : "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE";
            sendPackageBroadcast(action, null, extras, null, finishedReceiver, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unloadAllContainers(Set<AsecInstallArgs> cidArgs) {
        for (AsecInstallArgs arg : cidArgs) {
            synchronized (this.mInstallLock) {
                arg.doPostDeleteLI(false);
            }
        }
    }

    private void unloadMediaPackages(HashMap<AsecInstallArgs, String> processCids, int[] uidArr, final boolean reportStatus) {
        ArrayList<String> pkgList = new ArrayList<>();
        ArrayList<AsecInstallArgs> failedList = new ArrayList<>();
        final Set<AsecInstallArgs> keys = processCids.keySet();
        for (AsecInstallArgs args : keys) {
            String pkgName = args.getPackageName();
            PackageRemovedInfo outInfo = new PackageRemovedInfo();
            synchronized (this.mInstallLock) {
                boolean res = deletePackageLI(pkgName, null, false, null, null, 1, outInfo, false);
                if (res) {
                    pkgList.add(pkgName);
                } else {
                    Slog.e(TAG, "Failed to delete pkg from sdcard : " + pkgName);
                    failedList.add(args);
                }
            }
        }
        synchronized (this.mPackages) {
            this.mSettings.writeLPr();
        }
        if (pkgList.size() > 0) {
            sendResourcesChangedBroadcast(false, false, pkgList, uidArr, new IIntentReceiver.Stub() { // from class: com.android.server.pm.PackageManagerService.11
                @Override // android.content.IIntentReceiver
                public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) throws RemoteException {
                    Message msg = PackageManagerService.this.mHandler.obtainMessage(12, reportStatus ? 1 : 0, 1, keys);
                    PackageManagerService.this.mHandler.sendMessage(msg);
                }
            });
            return;
        }
        Message msg = this.mHandler.obtainMessage(12, reportStatus ? 1 : 0, -1, keys);
        this.mHandler.sendMessage(msg);
    }

    @Override // android.content.pm.IPackageManager
    public void movePackage(String packageName, IPackageMoveObserver observer, int flags) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MOVE_PACKAGE, null);
        UserHandle user = new UserHandle(UserHandle.getCallingUserId());
        int returnCode = 1;
        int currFlags = 0;
        int newFlags = 0;
        synchronized (this.mPackages) {
            PackageParser.Package pkg = this.mPackages.get(packageName);
            if (pkg == null) {
                returnCode = -2;
            } else if (pkg.applicationInfo != null && isSystemApp(pkg)) {
                Slog.w(TAG, "Cannot move system application");
                returnCode = -3;
            } else if (pkg.mOperationPending) {
                Slog.w(TAG, "Attempt to move package which has pending operations");
                returnCode = -7;
            } else {
                if ((flags & 2) != 0 && (flags & 1) != 0) {
                    Slog.w(TAG, "Ambigous flags specified for move location.");
                    returnCode = -5;
                } else {
                    newFlags = (flags & 2) != 0 ? 8 : 16;
                    currFlags = isExternal(pkg) ? 8 : 16;
                    if (newFlags == currFlags) {
                        Slog.w(TAG, "No move required. Trying to move to same location");
                        returnCode = -5;
                    } else if (isForwardLocked(pkg)) {
                        currFlags |= 1;
                        newFlags |= 1;
                    }
                }
                if (returnCode == 1) {
                    pkg.mOperationPending = true;
                }
            }
            if (returnCode != 1) {
                processPendingMove(new MoveParams(null, observer, 0, packageName, null, -1, user), returnCode);
            } else {
                Message msg = this.mHandler.obtainMessage(5);
                InstallArgs srcArgs = createInstallArgs(currFlags, pkg.applicationInfo.sourceDir, pkg.applicationInfo.publicSourceDir, pkg.applicationInfo.nativeLibraryDir);
                MoveParams mp = new MoveParams(srcArgs, observer, newFlags, packageName, pkg.applicationInfo.dataDir, pkg.applicationInfo.uid, user);
                msg.obj = mp;
                this.mHandler.sendMessage(msg);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processPendingMove(final MoveParams mp, final int currentStatus) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageManagerService.12
            @Override // java.lang.Runnable
            public void run() {
                PackageManagerService.this.mHandler.removeCallbacks(this);
                int returnCode = currentStatus;
                if (currentStatus == 1) {
                    int[] uidArr = null;
                    ArrayList<String> pkgList = null;
                    synchronized (PackageManagerService.this.mPackages) {
                        PackageParser.Package pkg = PackageManagerService.this.mPackages.get(mp.packageName);
                        if (pkg == null) {
                            Slog.w(PackageManagerService.TAG, " Package " + mp.packageName + " doesn't exist. Aborting move");
                            returnCode = -2;
                        } else if (!mp.srcArgs.getCodePath().equals(pkg.applicationInfo.sourceDir)) {
                            Slog.w(PackageManagerService.TAG, "Package " + mp.packageName + " code path changed from " + mp.srcArgs.getCodePath() + " to " + pkg.applicationInfo.sourceDir + " Aborting move and returning error");
                            returnCode = -6;
                        } else {
                            uidArr = new int[]{pkg.applicationInfo.uid};
                            pkgList = new ArrayList<>();
                            pkgList.add(mp.packageName);
                        }
                    }
                    if (returnCode == 1) {
                        PackageManagerService.this.sendResourcesChangedBroadcast(false, true, pkgList, uidArr, null);
                        synchronized (PackageManagerService.this.mInstallLock) {
                            synchronized (PackageManagerService.this.mPackages) {
                                PackageParser.Package pkg2 = PackageManagerService.this.mPackages.get(mp.packageName);
                                if (pkg2 == null) {
                                    Slog.w(PackageManagerService.TAG, " Package " + mp.packageName + " doesn't exist. Aborting move");
                                    returnCode = -2;
                                } else if (!mp.srcArgs.getCodePath().equals(pkg2.applicationInfo.sourceDir)) {
                                    Slog.w(PackageManagerService.TAG, "Package " + mp.packageName + " code path changed from " + mp.srcArgs.getCodePath() + " to " + pkg2.applicationInfo.sourceDir + " Aborting move and returning error");
                                    returnCode = -6;
                                } else {
                                    String oldCodePath = pkg2.mPath;
                                    String newCodePath = mp.targetArgs.getCodePath();
                                    String newResPath = mp.targetArgs.getResourcePath();
                                    String newNativePath = mp.targetArgs.getNativeLibraryPath();
                                    File newNativeDir = new File(newNativePath);
                                    if (!PackageManagerService.isForwardLocked(pkg2) && !PackageManagerService.isExternal(pkg2)) {
                                        NativeLibraryHelper.copyNativeBinariesIfNeededLI(new File(newCodePath), newNativeDir);
                                    }
                                    int[] users = PackageManagerService.sUserManager.getUserIds();
                                    for (int user : users) {
                                        if (PackageManagerService.this.mInstaller.linkNativeLibraryDirectory(pkg2.packageName, newNativePath, user) < 0) {
                                            returnCode = -1;
                                        }
                                    }
                                    if (returnCode == 1) {
                                        pkg2.mPath = newCodePath;
                                        if (PackageManagerService.this.moveDexFilesLI(pkg2) != 1) {
                                            pkg2.mPath = pkg2.mScanPath;
                                            returnCode = -1;
                                        }
                                    }
                                    if (returnCode == 1) {
                                        pkg2.mScanPath = newCodePath;
                                        pkg2.applicationInfo.sourceDir = newCodePath;
                                        pkg2.applicationInfo.publicSourceDir = newResPath;
                                        pkg2.applicationInfo.nativeLibraryDir = newNativePath;
                                        PackageSetting ps = (PackageSetting) pkg2.mExtras;
                                        ps.codePath = new File(pkg2.applicationInfo.sourceDir);
                                        ps.codePathString = ps.codePath.getPath();
                                        ps.resourcePath = new File(pkg2.applicationInfo.publicSourceDir);
                                        ps.resourcePathString = ps.resourcePath.getPath();
                                        ps.nativeLibraryPathString = newNativePath;
                                        if ((mp.flags & 8) != 0) {
                                            pkg2.applicationInfo.flags |= 262144;
                                        } else {
                                            pkg2.applicationInfo.flags &= -262145;
                                        }
                                        ps.setFlags(pkg2.applicationInfo.flags);
                                        PackageManagerService.this.mAppDirs.remove(oldCodePath);
                                        PackageManagerService.this.mAppDirs.put(newCodePath, pkg2);
                                        PackageManagerService.this.mSettings.writeLPr();
                                    }
                                }
                            }
                        }
                        PackageManagerService.this.sendResourcesChangedBroadcast(true, false, pkgList, uidArr, null);
                    }
                }
                if (returnCode != 1) {
                    if (mp.targetArgs != null) {
                        mp.targetArgs.doPostInstall(-110, -1);
                    }
                } else {
                    Runtime.getRuntime().gc();
                    synchronized (PackageManagerService.this.mInstallLock) {
                        mp.srcArgs.doPostDeleteLI(true);
                    }
                }
                if (returnCode != -7) {
                    synchronized (PackageManagerService.this.mPackages) {
                        PackageParser.Package pkg3 = PackageManagerService.this.mPackages.get(mp.packageName);
                        if (pkg3 != null) {
                            pkg3.mOperationPending = false;
                        }
                    }
                }
                IPackageMoveObserver observer = mp.observer;
                if (observer != null) {
                    try {
                        observer.packageMoved(mp.packageName, returnCode);
                    } catch (RemoteException e) {
                        Log.i(PackageManagerService.TAG, "Observer no longer exists.");
                    }
                }
            }
        });
    }

    @Override // android.content.pm.IPackageManager
    public boolean setInstallLocation(int loc) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS, null);
        if (getInstallLocation() == loc) {
            return true;
        }
        if (loc == 0 || loc == 1 || loc == 2) {
            Settings.Global.putInt(this.mContext.getContentResolver(), Settings.Global.DEFAULT_INSTALL_LOCATION, loc);
            return true;
        }
        return false;
    }

    @Override // android.content.pm.IPackageManager
    public int getInstallLocation() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.DEFAULT_INSTALL_LOCATION, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cleanUpUserLILPw(int userHandle) {
        this.mDirtyUsers.remove(Integer.valueOf(userHandle));
        this.mSettings.removeUserLPr(userHandle);
        this.mPendingBroadcasts.remove(userHandle);
        if (this.mInstaller != null) {
            this.mInstaller.removeUserDataDirs(userHandle);
        }
    }

    void createNewUserLILPw(int userHandle, File path) {
        if (this.mInstaller != null) {
            this.mSettings.createNewUserLILPw(this, this.mInstaller, userHandle, path);
        }
    }

    @Override // android.content.pm.IPackageManager
    public VerifierDeviceIdentity getVerifierDeviceIdentity() throws RemoteException {
        VerifierDeviceIdentity verifierDeviceIdentityLPw;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.PACKAGE_VERIFICATION_AGENT, "Only package verification agents can read the verifier device identity");
        synchronized (this.mPackages) {
            verifierDeviceIdentityLPw = this.mSettings.getVerifierDeviceIdentityLPw();
        }
        return verifierDeviceIdentityLPw;
    }

    @Override // android.content.pm.IPackageManager
    @Deprecated
    public boolean isPermissionEnforced(String permission) {
        return true;
    }
}