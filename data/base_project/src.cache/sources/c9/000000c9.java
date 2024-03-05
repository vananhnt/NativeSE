package android.app;

import android.app.Activity;
import android.app.IActivityManager;
import android.app.backup.BackupAgent;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDebug;
import android.ddm.DdmHandleAppName;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.display.DisplayManagerGlobal;
import android.net.IConnectivityManager;
import android.net.Proxy;
import android.net.ProxyProperties;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.Telephony;
import android.renderscript.RenderScript;
import android.security.AndroidKeyStoreProvider;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SuperNotCalledException;
import android.view.Display;
import android.view.HardwareRenderer;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewManager;
import android.view.ViewRootImpl;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.os.BinderInternal;
import com.android.internal.os.RuntimeInit;
import com.android.internal.os.SamplingProfilerIntegration;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.Objects;
import com.android.org.conscrypt.OpenSSLSocketImpl;
import com.google.android.collect.Lists;
import dalvik.system.CloseGuard;
import dalvik.system.VMRuntime;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;
import javax.sip.header.SubscriptionStateHeader;
import libcore.io.DropBox;
import libcore.io.EventLogger;
import libcore.io.IoUtils;

/* loaded from: ActivityThread.class */
public final class ActivityThread {
    public static final String TAG = "ActivityThread";
    static final boolean localLOGV = false;
    static final boolean DEBUG_MESSAGES = false;
    public static final boolean DEBUG_BROADCAST = false;
    private static final boolean DEBUG_RESULTS = false;
    private static final boolean DEBUG_BACKUP = false;
    public static final boolean DEBUG_CONFIGURATION = false;
    private static final boolean DEBUG_SERVICE = false;
    private static final boolean DEBUG_MEMORY_TRIM = false;
    private static final boolean DEBUG_PROVIDER = false;
    private static final long MIN_TIME_BETWEEN_GCS = 5000;
    private static final int SQLITE_MEM_RELEASED_EVENT_LOG_TAG = 75003;
    private static final int LOG_ON_PAUSE_CALLED = 30021;
    private static final int LOG_ON_RESUME_CALLED = 30022;
    static IPackageManager sPackageManager;
    AppBindData mBoundApplication;
    Profiler mProfiler;
    int mCurDefaultDisplayDpi;
    boolean mDensityCompatMode;
    Configuration mConfiguration;
    Configuration mCompatConfiguration;
    Application mInitialApplication;
    private static ActivityThread sCurrentActivityThread;
    Instrumentation mInstrumentation;
    static Handler sMainThreadHandler;
    private static final Bitmap.Config THUMBNAIL_FORMAT = Bitmap.Config.RGB_565;
    private static final Pattern PATTERN_SEMICOLON = Pattern.compile(Separators.SEMICOLON);
    static ContextImpl mSystemContext = null;
    private static final ThreadLocal<Intent> sCurrentBroadcastIntent = new ThreadLocal<>();
    final ApplicationThread mAppThread = new ApplicationThread();
    final Looper mLooper = Looper.myLooper();
    final H mH = new H();
    final ArrayMap<IBinder, ActivityClientRecord> mActivities = new ArrayMap<>();
    ActivityClientRecord mNewActivities = null;
    int mNumVisibleActivities = 0;
    final ArrayMap<IBinder, Service> mServices = new ArrayMap<>();
    final ArrayList<Application> mAllApplications = new ArrayList<>();
    final ArrayMap<String, BackupAgent> mBackupAgents = new ArrayMap<>();
    String mInstrumentationAppDir = null;
    String mInstrumentationAppLibraryDir = null;
    String mInstrumentationAppPackage = null;
    String mInstrumentedAppDir = null;
    String mInstrumentedAppLibraryDir = null;
    boolean mSystemThread = false;
    boolean mJitEnabled = false;
    final ArrayMap<String, WeakReference<LoadedApk>> mPackages = new ArrayMap<>();
    final ArrayMap<String, WeakReference<LoadedApk>> mResourcePackages = new ArrayMap<>();
    final ArrayList<ActivityClientRecord> mRelaunchingActivities = new ArrayList<>();
    Configuration mPendingConfiguration = null;
    final ArrayMap<ProviderKey, ProviderClientRecord> mProviderMap = new ArrayMap<>();
    final ArrayMap<IBinder, ProviderRefCount> mProviderRefCountMap = new ArrayMap<>();
    final ArrayMap<IBinder, ProviderClientRecord> mLocalProviders = new ArrayMap<>();
    final ArrayMap<ComponentName, ProviderClientRecord> mLocalProvidersByName = new ArrayMap<>();
    final ArrayMap<Activity, ArrayList<OnActivityPausedListener>> mOnPauseListeners = new ArrayMap<>();
    final GcIdler mGcIdler = new GcIdler();
    boolean mGcIdlerScheduled = false;
    Bundle mCoreSettings = null;
    private Configuration mMainThreadConfig = new Configuration();
    private int mThumbnailWidth = -1;
    private int mThumbnailHeight = -1;
    private Bitmap mAvailThumbnailBitmap = null;
    private Canvas mThumbnailCanvas = null;
    private final ResourcesManager mResourcesManager = ResourcesManager.getInstance();

    /* JADX INFO: Access modifiers changed from: private */
    public native void dumpGraphicsInfo(FileDescriptor fileDescriptor);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActivityThread$ProviderKey.class */
    public static final class ProviderKey {
        final String authority;
        final int userId;

        public ProviderKey(String authority, int userId) {
            this.authority = authority;
            this.userId = userId;
        }

        public boolean equals(Object o) {
            if (o instanceof ProviderKey) {
                ProviderKey other = (ProviderKey) o;
                return Objects.equal(this.authority, other.authority) && this.userId == other.userId;
            }
            return false;
        }

        public int hashCode() {
            return (this.authority != null ? this.authority.hashCode() : 0) ^ this.userId;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$ActivityClientRecord.class */
    public static final class ActivityClientRecord {
        IBinder token;
        int ident;
        Intent intent;
        Bundle state;
        Activity activity;
        Window window;
        Activity.NonConfigurationInstances lastNonConfigurationInstances;
        Configuration newConfig;
        Configuration createdConfig;
        String profileFile;
        ParcelFileDescriptor profileFd;
        boolean autoStopProfiler;
        ActivityInfo activityInfo;
        CompatibilityInfo compatInfo;
        LoadedApk packageInfo;
        List<ResultInfo> pendingResults;
        List<Intent> pendingIntents;
        boolean startsNotResumed;
        boolean isForward;
        int pendingConfigChanges;
        boolean onlyLocalRequest;
        View mPendingRemoveWindow;
        WindowManager mPendingRemoveWindowManager;
        Activity parent = null;
        String embeddedID = null;
        boolean paused = false;
        boolean stopped = false;
        boolean hideForNow = false;
        ActivityClientRecord nextIdle = null;

        ActivityClientRecord() {
        }

        public boolean isPreHoneycomb() {
            return this.activity != null && this.activity.getApplicationInfo().targetSdkVersion < 11;
        }

        public String toString() {
            ComponentName componentName = this.intent != null ? this.intent.getComponent() : null;
            return "ActivityRecord{" + Integer.toHexString(System.identityHashCode(this)) + " token=" + this.token + Separators.SP + (componentName == null ? "no component name" : componentName.toShortString()) + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$ProviderClientRecord.class */
    public final class ProviderClientRecord {
        final String[] mNames;
        final IContentProvider mProvider;
        final ContentProvider mLocalProvider;
        final IActivityManager.ContentProviderHolder mHolder;

        ProviderClientRecord(String[] names, IContentProvider provider, ContentProvider localProvider, IActivityManager.ContentProviderHolder holder) {
            this.mNames = names;
            this.mProvider = provider;
            this.mLocalProvider = localProvider;
            this.mHolder = holder;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$NewIntentData.class */
    public static final class NewIntentData {
        List<Intent> intents;
        IBinder token;

        NewIntentData() {
        }

        public String toString() {
            return "NewIntentData{intents=" + this.intents + " token=" + this.token + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$ReceiverData.class */
    public static final class ReceiverData extends BroadcastReceiver.PendingResult {
        Intent intent;
        ActivityInfo info;
        CompatibilityInfo compatInfo;

        public ReceiverData(Intent intent, int resultCode, String resultData, Bundle resultExtras, boolean ordered, boolean sticky, IBinder token, int sendingUser) {
            super(resultCode, resultData, resultExtras, 0, ordered, sticky, token, sendingUser);
            this.intent = intent;
        }

        public String toString() {
            return "ReceiverData{intent=" + this.intent + " packageName=" + this.info.packageName + " resultCode=" + getResultCode() + " resultData=" + getResultData() + " resultExtras=" + getResultExtras(false) + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$CreateBackupAgentData.class */
    public static final class CreateBackupAgentData {
        ApplicationInfo appInfo;
        CompatibilityInfo compatInfo;
        int backupMode;

        CreateBackupAgentData() {
        }

        public String toString() {
            return "CreateBackupAgentData{appInfo=" + this.appInfo + " backupAgent=" + this.appInfo.backupAgentName + " mode=" + this.backupMode + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$CreateServiceData.class */
    public static final class CreateServiceData {
        IBinder token;
        ServiceInfo info;
        CompatibilityInfo compatInfo;
        Intent intent;

        CreateServiceData() {
        }

        public String toString() {
            return "CreateServiceData{token=" + this.token + " className=" + this.info.name + " packageName=" + this.info.packageName + " intent=" + this.intent + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$BindServiceData.class */
    public static final class BindServiceData {
        IBinder token;
        Intent intent;
        boolean rebind;

        BindServiceData() {
        }

        public String toString() {
            return "BindServiceData{token=" + this.token + " intent=" + this.intent + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$ServiceArgsData.class */
    public static final class ServiceArgsData {
        IBinder token;
        boolean taskRemoved;
        int startId;
        int flags;
        Intent args;

        ServiceArgsData() {
        }

        public String toString() {
            return "ServiceArgsData{token=" + this.token + " startId=" + this.startId + " args=" + this.args + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$AppBindData.class */
    public static final class AppBindData {
        LoadedApk info;
        String processName;
        ApplicationInfo appInfo;
        List<ProviderInfo> providers;
        ComponentName instrumentationName;
        Bundle instrumentationArgs;
        IInstrumentationWatcher instrumentationWatcher;
        IUiAutomationConnection instrumentationUiAutomationConnection;
        int debugMode;
        boolean enableOpenGlTrace;
        boolean restrictedBackupMode;
        boolean persistent;
        Configuration config;
        CompatibilityInfo compatInfo;
        String initProfileFile;
        ParcelFileDescriptor initProfileFd;
        boolean initAutoStopProfiler;

        AppBindData() {
        }

        public String toString() {
            return "AppBindData{appInfo=" + this.appInfo + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$Profiler.class */
    public static final class Profiler {
        String profileFile;
        ParcelFileDescriptor profileFd;
        boolean autoStopProfiler;
        boolean profiling;
        boolean handlingProfiling;

        Profiler() {
        }

        public void setProfiler(String file, ParcelFileDescriptor fd) {
            if (this.profiling) {
                if (fd != null) {
                    try {
                        fd.close();
                        return;
                    } catch (IOException e) {
                        return;
                    }
                }
                return;
            }
            if (this.profileFd != null) {
                try {
                    this.profileFd.close();
                } catch (IOException e2) {
                }
            }
            this.profileFile = file;
            this.profileFd = fd;
        }

        public void startProfiling() {
            if (this.profileFd == null || this.profiling) {
                return;
            }
            try {
                Debug.startMethodTracing(this.profileFile, this.profileFd.getFileDescriptor(), 8388608, 0);
                this.profiling = true;
            } catch (RuntimeException e) {
                Slog.w(ActivityThread.TAG, "Profiling failed on path " + this.profileFile);
                try {
                    this.profileFd.close();
                    this.profileFd = null;
                } catch (IOException e2) {
                    Slog.w(ActivityThread.TAG, "Failure closing profile fd", e2);
                }
            }
        }

        public void stopProfiling() {
            if (this.profiling) {
                this.profiling = false;
                Debug.stopMethodTracing();
                if (this.profileFd != null) {
                    try {
                        this.profileFd.close();
                    } catch (IOException e) {
                    }
                }
                this.profileFd = null;
                this.profileFile = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$DumpComponentInfo.class */
    public static final class DumpComponentInfo {
        ParcelFileDescriptor fd;
        IBinder token;
        String prefix;
        String[] args;

        DumpComponentInfo() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$ResultData.class */
    public static final class ResultData {
        IBinder token;
        List<ResultInfo> results;

        ResultData() {
        }

        public String toString() {
            return "ResultData{token=" + this.token + " results" + this.results + "}";
        }
    }

    /* loaded from: ActivityThread$ContextCleanupInfo.class */
    static final class ContextCleanupInfo {
        ContextImpl context;
        String what;
        String who;

        ContextCleanupInfo() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$ProfilerControlData.class */
    public static final class ProfilerControlData {
        String path;
        ParcelFileDescriptor fd;

        ProfilerControlData() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$DumpHeapData.class */
    public static final class DumpHeapData {
        String path;
        ParcelFileDescriptor fd;

        DumpHeapData() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$UpdateCompatibilityData.class */
    public static final class UpdateCompatibilityData {
        String pkg;
        CompatibilityInfo info;

        UpdateCompatibilityData() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$RequestAssistContextExtras.class */
    public static final class RequestAssistContextExtras {
        IBinder activityToken;
        IBinder requestToken;
        int requestType;

        RequestAssistContextExtras() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActivityThread$ApplicationThread.class */
    public class ApplicationThread extends ApplicationThreadNative {
        private static final String HEAP_FULL_COLUMN = "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s";
        private static final String HEAP_COLUMN = "%13s %8s %8s %8s %8s %8s %8s %8s";
        private static final String ONE_COUNT_COLUMN = "%21s %8d";
        private static final String TWO_COUNT_COLUMNS = "%21s %8d %21s %8d";
        private static final String DB_INFO_FORMAT = "  %8s %8s %14s %14s  %s";
        private static final int ACTIVITY_THREAD_CHECKIN_VERSION = 3;
        private int mLastProcessState;

        private ApplicationThread() {
            this.mLastProcessState = -1;
        }

        private void updatePendingConfiguration(Configuration config) {
            synchronized (ActivityThread.this.mResourcesManager) {
                if (ActivityThread.this.mPendingConfiguration == null || ActivityThread.this.mPendingConfiguration.isOtherSeqNewer(config)) {
                    ActivityThread.this.mPendingConfiguration = config;
                }
            }
        }

        @Override // android.app.IApplicationThread
        public final void schedulePauseActivity(IBinder token, boolean finished, boolean userLeaving, int configChanges) {
            ActivityThread.this.queueOrSendMessage(finished ? 102 : 101, token, userLeaving ? 1 : 0, configChanges);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleStopActivity(IBinder token, boolean showWindow, int configChanges) {
            ActivityThread.this.queueOrSendMessage(showWindow ? 103 : 104, token, 0, configChanges);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleWindowVisibility(IBinder token, boolean showWindow) {
            ActivityThread.this.queueOrSendMessage(showWindow ? 105 : 106, token);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleSleeping(IBinder token, boolean sleeping) {
            ActivityThread.this.queueOrSendMessage(137, token, sleeping ? 1 : 0);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleResumeActivity(IBinder token, int processState, boolean isForward) {
            updateProcessState(processState, false);
            ActivityThread.this.queueOrSendMessage(107, token, isForward ? 1 : 0);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleSendResult(IBinder token, List<ResultInfo> results) {
            ResultData res = new ResultData();
            res.token = token;
            res.results = results;
            ActivityThread.this.queueOrSendMessage(108, res);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleLaunchActivity(Intent intent, IBinder token, int ident, ActivityInfo info, Configuration curConfig, CompatibilityInfo compatInfo, int procState, Bundle state, List<ResultInfo> pendingResults, List<Intent> pendingNewIntents, boolean notResumed, boolean isForward, String profileName, ParcelFileDescriptor profileFd, boolean autoStopProfiler) {
            updateProcessState(procState, false);
            ActivityClientRecord r = new ActivityClientRecord();
            r.token = token;
            r.ident = ident;
            r.intent = intent;
            r.activityInfo = info;
            r.compatInfo = compatInfo;
            r.state = state;
            r.pendingResults = pendingResults;
            r.pendingIntents = pendingNewIntents;
            r.startsNotResumed = notResumed;
            r.isForward = isForward;
            r.profileFile = profileName;
            r.profileFd = profileFd;
            r.autoStopProfiler = autoStopProfiler;
            updatePendingConfiguration(curConfig);
            ActivityThread.this.queueOrSendMessage(100, r);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleRelaunchActivity(IBinder token, List<ResultInfo> pendingResults, List<Intent> pendingNewIntents, int configChanges, boolean notResumed, Configuration config) {
            ActivityThread.this.requestRelaunchActivity(token, pendingResults, pendingNewIntents, configChanges, notResumed, config, true);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleNewIntent(List<Intent> intents, IBinder token) {
            NewIntentData data = new NewIntentData();
            data.intents = intents;
            data.token = token;
            ActivityThread.this.queueOrSendMessage(112, data);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleDestroyActivity(IBinder token, boolean finishing, int configChanges) {
            ActivityThread.this.queueOrSendMessage(109, token, finishing ? 1 : 0, configChanges);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleReceiver(Intent intent, ActivityInfo info, CompatibilityInfo compatInfo, int resultCode, String data, Bundle extras, boolean sync, int sendingUser, int processState) {
            updateProcessState(processState, false);
            ReceiverData r = new ReceiverData(intent, resultCode, data, extras, sync, false, ActivityThread.this.mAppThread.asBinder(), sendingUser);
            r.info = info;
            r.compatInfo = compatInfo;
            ActivityThread.this.queueOrSendMessage(113, r);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleCreateBackupAgent(ApplicationInfo app, CompatibilityInfo compatInfo, int backupMode) {
            CreateBackupAgentData d = new CreateBackupAgentData();
            d.appInfo = app;
            d.compatInfo = compatInfo;
            d.backupMode = backupMode;
            ActivityThread.this.queueOrSendMessage(128, d);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleDestroyBackupAgent(ApplicationInfo app, CompatibilityInfo compatInfo) {
            CreateBackupAgentData d = new CreateBackupAgentData();
            d.appInfo = app;
            d.compatInfo = compatInfo;
            ActivityThread.this.queueOrSendMessage(129, d);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleCreateService(IBinder token, ServiceInfo info, CompatibilityInfo compatInfo, int processState) {
            updateProcessState(processState, false);
            CreateServiceData s = new CreateServiceData();
            s.token = token;
            s.info = info;
            s.compatInfo = compatInfo;
            ActivityThread.this.queueOrSendMessage(114, s);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleBindService(IBinder token, Intent intent, boolean rebind, int processState) {
            updateProcessState(processState, false);
            BindServiceData s = new BindServiceData();
            s.token = token;
            s.intent = intent;
            s.rebind = rebind;
            ActivityThread.this.queueOrSendMessage(121, s);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleUnbindService(IBinder token, Intent intent) {
            BindServiceData s = new BindServiceData();
            s.token = token;
            s.intent = intent;
            ActivityThread.this.queueOrSendMessage(122, s);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleServiceArgs(IBinder token, boolean taskRemoved, int startId, int flags, Intent args) {
            ServiceArgsData s = new ServiceArgsData();
            s.token = token;
            s.taskRemoved = taskRemoved;
            s.startId = startId;
            s.flags = flags;
            s.args = args;
            ActivityThread.this.queueOrSendMessage(115, s);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleStopService(IBinder token) {
            ActivityThread.this.queueOrSendMessage(116, token);
        }

        @Override // android.app.IApplicationThread
        public final void bindApplication(String processName, ApplicationInfo appInfo, List<ProviderInfo> providers, ComponentName instrumentationName, String profileFile, ParcelFileDescriptor profileFd, boolean autoStopProfiler, Bundle instrumentationArgs, IInstrumentationWatcher instrumentationWatcher, IUiAutomationConnection instrumentationUiConnection, int debugMode, boolean enableOpenGlTrace, boolean isRestrictedBackupMode, boolean persistent, Configuration config, CompatibilityInfo compatInfo, Map<String, IBinder> services, Bundle coreSettings) {
            if (services != null) {
                ServiceManager.initServiceCache(services);
            }
            setCoreSettings(coreSettings);
            AppBindData data = new AppBindData();
            data.processName = processName;
            data.appInfo = appInfo;
            data.providers = providers;
            data.instrumentationName = instrumentationName;
            data.instrumentationArgs = instrumentationArgs;
            data.instrumentationWatcher = instrumentationWatcher;
            data.instrumentationUiAutomationConnection = instrumentationUiConnection;
            data.debugMode = debugMode;
            data.enableOpenGlTrace = enableOpenGlTrace;
            data.restrictedBackupMode = isRestrictedBackupMode;
            data.persistent = persistent;
            data.config = config;
            data.compatInfo = compatInfo;
            data.initProfileFile = profileFile;
            data.initProfileFd = profileFd;
            data.initAutoStopProfiler = false;
            ActivityThread.this.queueOrSendMessage(110, data);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleExit() {
            ActivityThread.this.queueOrSendMessage(111, null);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleSuicide() {
            ActivityThread.this.queueOrSendMessage(130, null);
        }

        @Override // android.app.IApplicationThread
        public void requestThumbnail(IBinder token) {
            ActivityThread.this.queueOrSendMessage(117, token);
        }

        @Override // android.app.IApplicationThread
        public void scheduleConfigurationChanged(Configuration config) {
            updatePendingConfiguration(config);
            ActivityThread.this.queueOrSendMessage(118, config);
        }

        @Override // android.app.IApplicationThread
        public void updateTimeZone() {
            TimeZone.setDefault(null);
        }

        @Override // android.app.IApplicationThread
        public void clearDnsCache() {
            InetAddress.clearDnsCache();
        }

        @Override // android.app.IApplicationThread
        public void setHttpProxy(String host, String port, String exclList, String pacFileUrl) {
            Proxy.setHttpProxySystemProperty(host, port, exclList, pacFileUrl);
        }

        @Override // android.app.IApplicationThread
        public void processInBackground() {
            ActivityThread.this.mH.removeMessages(120);
            ActivityThread.this.mH.sendMessage(ActivityThread.this.mH.obtainMessage(120));
        }

        @Override // android.app.IApplicationThread
        public void dumpService(FileDescriptor fd, IBinder servicetoken, String[] args) {
            DumpComponentInfo data = new DumpComponentInfo();
            try {
                data.fd = ParcelFileDescriptor.dup(fd);
                data.token = servicetoken;
                data.args = args;
                ActivityThread.this.queueOrSendMessage(123, data);
            } catch (IOException e) {
                Slog.w(ActivityThread.TAG, "dumpService failed", e);
            }
        }

        @Override // android.app.IApplicationThread
        public void scheduleRegisteredReceiver(IIntentReceiver receiver, Intent intent, int resultCode, String dataStr, Bundle extras, boolean ordered, boolean sticky, int sendingUser, int processState) throws RemoteException {
            updateProcessState(processState, false);
            receiver.performReceive(intent, resultCode, dataStr, extras, ordered, sticky, sendingUser);
        }

        @Override // android.app.IApplicationThread
        public void scheduleLowMemory() {
            ActivityThread.this.queueOrSendMessage(124, null);
        }

        @Override // android.app.IApplicationThread
        public void scheduleActivityConfigurationChanged(IBinder token) {
            ActivityThread.this.queueOrSendMessage(125, token);
        }

        @Override // android.app.IApplicationThread
        public void profilerControl(boolean start, String path, ParcelFileDescriptor fd, int profileType) {
            ProfilerControlData pcd = new ProfilerControlData();
            pcd.path = path;
            pcd.fd = fd;
            ActivityThread.this.queueOrSendMessage(127, pcd, start ? 1 : 0, profileType);
        }

        @Override // android.app.IApplicationThread
        public void dumpHeap(boolean managed, String path, ParcelFileDescriptor fd) {
            DumpHeapData dhd = new DumpHeapData();
            dhd.path = path;
            dhd.fd = fd;
            ActivityThread.this.queueOrSendMessage(135, dhd, managed ? 1 : 0);
        }

        @Override // android.app.IApplicationThread
        public void setSchedulingGroup(int group) {
            try {
                Process.setProcessGroup(Process.myPid(), group);
            } catch (Exception e) {
                Slog.w(ActivityThread.TAG, "Failed setting process group to " + group, e);
            }
        }

        @Override // android.app.IApplicationThread
        public void dispatchPackageBroadcast(int cmd, String[] packages) {
            ActivityThread.this.queueOrSendMessage(133, packages, cmd);
        }

        @Override // android.app.IApplicationThread
        public void scheduleCrash(String msg) {
            ActivityThread.this.queueOrSendMessage(134, msg);
        }

        @Override // android.app.IApplicationThread
        public void dumpActivity(FileDescriptor fd, IBinder activitytoken, String prefix, String[] args) {
            DumpComponentInfo data = new DumpComponentInfo();
            try {
                data.fd = ParcelFileDescriptor.dup(fd);
                data.token = activitytoken;
                data.prefix = prefix;
                data.args = args;
                ActivityThread.this.queueOrSendMessage(136, data);
            } catch (IOException e) {
                Slog.w(ActivityThread.TAG, "dumpActivity failed", e);
            }
        }

        @Override // android.app.IApplicationThread
        public void dumpProvider(FileDescriptor fd, IBinder providertoken, String[] args) {
            DumpComponentInfo data = new DumpComponentInfo();
            try {
                data.fd = ParcelFileDescriptor.dup(fd);
                data.token = providertoken;
                data.args = args;
                ActivityThread.this.queueOrSendMessage(141, data);
            } catch (IOException e) {
                Slog.w(ActivityThread.TAG, "dumpProvider failed", e);
            }
        }

        @Override // android.app.IApplicationThread
        public void dumpMemInfo(FileDescriptor fd, Debug.MemoryInfo mem, boolean checkin, boolean dumpFullInfo, boolean dumpDalvik, String[] args) {
            FileOutputStream fout = new FileOutputStream(fd);
            PrintWriter pw = new FastPrintWriter(fout);
            try {
                dumpMemInfo(pw, mem, checkin, dumpFullInfo, dumpDalvik);
                pw.flush();
            } catch (Throwable th) {
                pw.flush();
                throw th;
            }
        }

        private void dumpMemInfo(PrintWriter pw, Debug.MemoryInfo memInfo, boolean checkin, boolean dumpFullInfo, boolean dumpDalvik) {
            long nativeMax = Debug.getNativeHeapSize() / 1024;
            long nativeAllocated = Debug.getNativeHeapAllocatedSize() / 1024;
            long nativeFree = Debug.getNativeHeapFreeSize() / 1024;
            Runtime runtime = Runtime.getRuntime();
            long dalvikMax = runtime.totalMemory() / 1024;
            long dalvikFree = runtime.freeMemory() / 1024;
            long dalvikAllocated = dalvikMax - dalvikFree;
            long viewInstanceCount = ViewDebug.getViewInstanceCount();
            long viewRootInstanceCount = ViewDebug.getViewRootImplCount();
            long appContextInstanceCount = Debug.countInstancesOfClass(ContextImpl.class);
            long activityInstanceCount = Debug.countInstancesOfClass(Activity.class);
            int globalAssetCount = AssetManager.getGlobalAssetCount();
            int globalAssetManagerCount = AssetManager.getGlobalAssetManagerCount();
            int binderLocalObjectCount = Debug.getBinderLocalObjectCount();
            int binderProxyObjectCount = Debug.getBinderProxyObjectCount();
            int binderDeathObjectCount = Debug.getBinderDeathObjectCount();
            long openSslSocketCount = Debug.countInstancesOfClass(OpenSSLSocketImpl.class);
            SQLiteDebug.PagerStats stats = SQLiteDebug.getDatabaseInfo();
            if (checkin) {
                String processName = ActivityThread.this.mBoundApplication != null ? ActivityThread.this.mBoundApplication.processName : "unknown";
                pw.print(3);
                pw.print(',');
                pw.print(Process.myPid());
                pw.print(',');
                pw.print(processName);
                pw.print(',');
                pw.print(nativeMax);
                pw.print(',');
                pw.print(dalvikMax);
                pw.print(',');
                pw.print("N/A,");
                pw.print(nativeMax + dalvikMax);
                pw.print(',');
                pw.print(nativeAllocated);
                pw.print(',');
                pw.print(dalvikAllocated);
                pw.print(',');
                pw.print("N/A,");
                pw.print(nativeAllocated + dalvikAllocated);
                pw.print(',');
                pw.print(nativeFree);
                pw.print(',');
                pw.print(dalvikFree);
                pw.print(',');
                pw.print("N/A,");
                pw.print(nativeFree + dalvikFree);
                pw.print(',');
                pw.print(memInfo.nativePss);
                pw.print(',');
                pw.print(memInfo.dalvikPss);
                pw.print(',');
                pw.print(memInfo.otherPss);
                pw.print(',');
                pw.print(memInfo.getTotalPss());
                pw.print(',');
                pw.print(memInfo.nativeSwappablePss);
                pw.print(',');
                pw.print(memInfo.dalvikSwappablePss);
                pw.print(',');
                pw.print(memInfo.otherSwappablePss);
                pw.print(',');
                pw.print(memInfo.getTotalSwappablePss());
                pw.print(',');
                pw.print(memInfo.nativeSharedDirty);
                pw.print(',');
                pw.print(memInfo.dalvikSharedDirty);
                pw.print(',');
                pw.print(memInfo.otherSharedDirty);
                pw.print(',');
                pw.print(memInfo.getTotalSharedDirty());
                pw.print(',');
                pw.print(memInfo.nativeSharedClean);
                pw.print(',');
                pw.print(memInfo.dalvikSharedClean);
                pw.print(',');
                pw.print(memInfo.otherSharedClean);
                pw.print(',');
                pw.print(memInfo.getTotalSharedClean());
                pw.print(',');
                pw.print(memInfo.nativePrivateDirty);
                pw.print(',');
                pw.print(memInfo.dalvikPrivateDirty);
                pw.print(',');
                pw.print(memInfo.otherPrivateDirty);
                pw.print(',');
                pw.print(memInfo.getTotalPrivateDirty());
                pw.print(',');
                pw.print(memInfo.nativePrivateClean);
                pw.print(',');
                pw.print(memInfo.dalvikPrivateClean);
                pw.print(',');
                pw.print(memInfo.otherPrivateClean);
                pw.print(',');
                pw.print(memInfo.getTotalPrivateClean());
                pw.print(',');
                for (int i = 0; i < 16; i++) {
                    pw.print(Debug.MemoryInfo.getOtherLabel(i));
                    pw.print(',');
                    pw.print(memInfo.getOtherPss(i));
                    pw.print(',');
                    pw.print(memInfo.getOtherSwappablePss(i));
                    pw.print(',');
                    pw.print(memInfo.getOtherSharedDirty(i));
                    pw.print(',');
                    pw.print(memInfo.getOtherSharedClean(i));
                    pw.print(',');
                    pw.print(memInfo.getOtherPrivateDirty(i));
                    pw.print(',');
                    pw.print(memInfo.getOtherPrivateClean(i));
                    pw.print(',');
                }
                pw.print(viewInstanceCount);
                pw.print(',');
                pw.print(viewRootInstanceCount);
                pw.print(',');
                pw.print(appContextInstanceCount);
                pw.print(',');
                pw.print(activityInstanceCount);
                pw.print(',');
                pw.print(globalAssetCount);
                pw.print(',');
                pw.print(globalAssetManagerCount);
                pw.print(',');
                pw.print(binderLocalObjectCount);
                pw.print(',');
                pw.print(binderProxyObjectCount);
                pw.print(',');
                pw.print(binderDeathObjectCount);
                pw.print(',');
                pw.print(openSslSocketCount);
                pw.print(',');
                pw.print(stats.memoryUsed / 1024);
                pw.print(',');
                pw.print(stats.memoryUsed / 1024);
                pw.print(',');
                pw.print(stats.pageCacheOverflow / 1024);
                pw.print(',');
                pw.print(stats.largestMemAlloc / 1024);
                for (int i2 = 0; i2 < stats.dbStats.size(); i2++) {
                    SQLiteDebug.DbStats dbStats = stats.dbStats.get(i2);
                    pw.print(',');
                    pw.print(dbStats.dbName);
                    pw.print(',');
                    pw.print(dbStats.pageSize);
                    pw.print(',');
                    pw.print(dbStats.dbSize);
                    pw.print(',');
                    pw.print(dbStats.lookaside);
                    pw.print(',');
                    pw.print(dbStats.cache);
                    pw.print(',');
                    pw.print(dbStats.cache);
                }
                pw.println();
                return;
            }
            if (dumpFullInfo) {
                printRow(pw, HEAP_FULL_COLUMN, "", "Pss", "Pss", "Shared", "Private", "Shared", "Private", "Swapped", "Heap", "Heap", "Heap");
                printRow(pw, HEAP_FULL_COLUMN, "", "Total", "Clean", "Dirty", "Dirty", "Clean", "Clean", "Dirty", "Size", "Alloc", "Free");
                printRow(pw, HEAP_FULL_COLUMN, "", "------", "------", "------", "------", "------", "------", "------", "------", "------", "------");
                printRow(pw, HEAP_FULL_COLUMN, "Native Heap", Integer.valueOf(memInfo.nativePss), Integer.valueOf(memInfo.nativeSwappablePss), Integer.valueOf(memInfo.nativeSharedDirty), Integer.valueOf(memInfo.nativePrivateDirty), Integer.valueOf(memInfo.nativeSharedClean), Integer.valueOf(memInfo.nativePrivateClean), Integer.valueOf(memInfo.nativeSwappedOut), Long.valueOf(nativeMax), Long.valueOf(nativeAllocated), Long.valueOf(nativeFree));
                printRow(pw, HEAP_FULL_COLUMN, "Dalvik Heap", Integer.valueOf(memInfo.dalvikPss), Integer.valueOf(memInfo.dalvikSwappablePss), Integer.valueOf(memInfo.dalvikSharedDirty), Integer.valueOf(memInfo.dalvikPrivateDirty), Integer.valueOf(memInfo.dalvikSharedClean), Integer.valueOf(memInfo.dalvikPrivateClean), Integer.valueOf(memInfo.dalvikSwappedOut), Long.valueOf(dalvikMax), Long.valueOf(dalvikAllocated), Long.valueOf(dalvikFree));
            } else {
                printRow(pw, HEAP_COLUMN, "", "Pss", "Private", "Private", "Swapped", "Heap", "Heap", "Heap");
                printRow(pw, HEAP_COLUMN, "", "Total", "Dirty", "Clean", "Dirty", "Size", "Alloc", "Free");
                printRow(pw, HEAP_COLUMN, "", "------", "------", "------", "------", "------", "------", "------", "------");
                printRow(pw, HEAP_COLUMN, "Native Heap", Integer.valueOf(memInfo.nativePss), Integer.valueOf(memInfo.nativePrivateDirty), Integer.valueOf(memInfo.nativePrivateClean), Integer.valueOf(memInfo.nativeSwappedOut), Long.valueOf(nativeMax), Long.valueOf(nativeAllocated), Long.valueOf(nativeFree));
                printRow(pw, HEAP_COLUMN, "Dalvik Heap", Integer.valueOf(memInfo.dalvikPss), Integer.valueOf(memInfo.dalvikPrivateDirty), Integer.valueOf(memInfo.dalvikPrivateClean), Integer.valueOf(memInfo.dalvikSwappedOut), Long.valueOf(dalvikMax), Long.valueOf(dalvikAllocated), Long.valueOf(dalvikFree));
            }
            int otherPss = memInfo.otherPss;
            int otherSwappablePss = memInfo.otherSwappablePss;
            int otherSharedDirty = memInfo.otherSharedDirty;
            int otherPrivateDirty = memInfo.otherPrivateDirty;
            int otherSharedClean = memInfo.otherSharedClean;
            int otherPrivateClean = memInfo.otherPrivateClean;
            int otherSwappedOut = memInfo.otherSwappedOut;
            for (int i3 = 0; i3 < 16; i3++) {
                int myPss = memInfo.getOtherPss(i3);
                int mySwappablePss = memInfo.getOtherSwappablePss(i3);
                int mySharedDirty = memInfo.getOtherSharedDirty(i3);
                int myPrivateDirty = memInfo.getOtherPrivateDirty(i3);
                int mySharedClean = memInfo.getOtherSharedClean(i3);
                int myPrivateClean = memInfo.getOtherPrivateClean(i3);
                int mySwappedOut = memInfo.getOtherSwappedOut(i3);
                if (myPss != 0 || mySharedDirty != 0 || myPrivateDirty != 0 || mySharedClean != 0 || myPrivateClean != 0 || mySwappedOut != 0) {
                    if (dumpFullInfo) {
                        printRow(pw, HEAP_FULL_COLUMN, Debug.MemoryInfo.getOtherLabel(i3), Integer.valueOf(myPss), Integer.valueOf(mySwappablePss), Integer.valueOf(mySharedDirty), Integer.valueOf(myPrivateDirty), Integer.valueOf(mySharedClean), Integer.valueOf(myPrivateClean), Integer.valueOf(mySwappedOut), "", "", "");
                    } else {
                        printRow(pw, HEAP_COLUMN, Debug.MemoryInfo.getOtherLabel(i3), Integer.valueOf(myPss), Integer.valueOf(myPrivateDirty), Integer.valueOf(myPrivateClean), Integer.valueOf(mySwappedOut), "", "", "");
                    }
                    otherPss -= myPss;
                    otherSwappablePss -= mySwappablePss;
                    otherSharedDirty -= mySharedDirty;
                    otherPrivateDirty -= myPrivateDirty;
                    otherSharedClean -= mySharedClean;
                    otherPrivateClean -= myPrivateClean;
                    otherSwappedOut -= mySwappedOut;
                }
            }
            if (dumpFullInfo) {
                printRow(pw, HEAP_FULL_COLUMN, SubscriptionStateHeader.UNKNOWN, Integer.valueOf(otherPss), Integer.valueOf(otherSwappablePss), Integer.valueOf(otherSharedDirty), Integer.valueOf(otherPrivateDirty), Integer.valueOf(otherSharedClean), Integer.valueOf(otherPrivateClean), Integer.valueOf(otherSwappedOut), "", "", "");
                printRow(pw, HEAP_FULL_COLUMN, "TOTAL", Integer.valueOf(memInfo.getTotalPss()), Integer.valueOf(memInfo.getTotalSwappablePss()), Integer.valueOf(memInfo.getTotalSharedDirty()), Integer.valueOf(memInfo.getTotalPrivateDirty()), Integer.valueOf(memInfo.getTotalSharedClean()), Integer.valueOf(memInfo.getTotalPrivateClean()), Integer.valueOf(memInfo.getTotalSwappedOut()), Long.valueOf(nativeMax + dalvikMax), Long.valueOf(nativeAllocated + dalvikAllocated), Long.valueOf(nativeFree + dalvikFree));
            } else {
                printRow(pw, HEAP_COLUMN, SubscriptionStateHeader.UNKNOWN, Integer.valueOf(otherPss), Integer.valueOf(otherPrivateDirty), Integer.valueOf(otherPrivateClean), Integer.valueOf(otherSwappedOut), "", "", "");
                printRow(pw, HEAP_COLUMN, "TOTAL", Integer.valueOf(memInfo.getTotalPss()), Integer.valueOf(memInfo.getTotalPrivateDirty()), Integer.valueOf(memInfo.getTotalPrivateClean()), Integer.valueOf(memInfo.getTotalSwappedOut()), Long.valueOf(nativeMax + dalvikMax), Long.valueOf(nativeAllocated + dalvikAllocated), Long.valueOf(nativeFree + dalvikFree));
            }
            if (dumpDalvik) {
                pw.println(Separators.SP);
                pw.println(" Dalvik Details");
                for (int i4 = 16; i4 < 21; i4++) {
                    int myPss2 = memInfo.getOtherPss(i4);
                    int mySwappablePss2 = memInfo.getOtherSwappablePss(i4);
                    int mySharedDirty2 = memInfo.getOtherSharedDirty(i4);
                    int myPrivateDirty2 = memInfo.getOtherPrivateDirty(i4);
                    int mySharedClean2 = memInfo.getOtherSharedClean(i4);
                    int myPrivateClean2 = memInfo.getOtherPrivateClean(i4);
                    int mySwappedOut2 = memInfo.getOtherSwappedOut(i4);
                    if (myPss2 != 0 || mySharedDirty2 != 0 || myPrivateDirty2 != 0 || mySharedClean2 != 0 || myPrivateClean2 != 0) {
                        if (dumpFullInfo) {
                            printRow(pw, HEAP_FULL_COLUMN, Debug.MemoryInfo.getOtherLabel(i4), Integer.valueOf(myPss2), Integer.valueOf(mySwappablePss2), Integer.valueOf(mySharedDirty2), Integer.valueOf(myPrivateDirty2), Integer.valueOf(mySharedClean2), Integer.valueOf(myPrivateClean2), Integer.valueOf(mySwappedOut2), "", "", "");
                        } else {
                            printRow(pw, HEAP_COLUMN, Debug.MemoryInfo.getOtherLabel(i4), Integer.valueOf(myPss2), Integer.valueOf(myPrivateDirty2), Integer.valueOf(myPrivateClean2), Integer.valueOf(mySwappedOut2), "", "", "");
                        }
                    }
                }
            }
            pw.println(Separators.SP);
            pw.println(" Objects");
            printRow(pw, TWO_COUNT_COLUMNS, "Views:", Long.valueOf(viewInstanceCount), "ViewRootImpl:", Long.valueOf(viewRootInstanceCount));
            printRow(pw, TWO_COUNT_COLUMNS, "AppContexts:", Long.valueOf(appContextInstanceCount), "Activities:", Long.valueOf(activityInstanceCount));
            printRow(pw, TWO_COUNT_COLUMNS, "Assets:", Integer.valueOf(globalAssetCount), "AssetManagers:", Integer.valueOf(globalAssetManagerCount));
            printRow(pw, TWO_COUNT_COLUMNS, "Local Binders:", Integer.valueOf(binderLocalObjectCount), "Proxy Binders:", Integer.valueOf(binderProxyObjectCount));
            printRow(pw, ONE_COUNT_COLUMN, "Death Recipients:", Integer.valueOf(binderDeathObjectCount));
            printRow(pw, ONE_COUNT_COLUMN, "OpenSSL Sockets:", Long.valueOf(openSslSocketCount));
            pw.println(Separators.SP);
            pw.println(" SQL");
            printRow(pw, ONE_COUNT_COLUMN, "MEMORY_USED:", Integer.valueOf(stats.memoryUsed / 1024));
            printRow(pw, TWO_COUNT_COLUMNS, "PAGECACHE_OVERFLOW:", Integer.valueOf(stats.pageCacheOverflow / 1024), "MALLOC_SIZE:", Integer.valueOf(stats.largestMemAlloc / 1024));
            pw.println(Separators.SP);
            int N = stats.dbStats.size();
            if (N > 0) {
                pw.println(" DATABASES");
                printRow(pw, DB_INFO_FORMAT, "pgsz", "dbsz", "Lookaside(b)", "cache", "Dbname");
                for (int i5 = 0; i5 < N; i5++) {
                    SQLiteDebug.DbStats dbStats2 = stats.dbStats.get(i5);
                    Object[] objArr = new Object[5];
                    objArr[0] = dbStats2.pageSize > 0 ? String.valueOf(dbStats2.pageSize) : Separators.SP;
                    objArr[1] = dbStats2.dbSize > 0 ? String.valueOf(dbStats2.dbSize) : Separators.SP;
                    objArr[2] = dbStats2.lookaside > 0 ? String.valueOf(dbStats2.lookaside) : Separators.SP;
                    objArr[3] = dbStats2.cache;
                    objArr[4] = dbStats2.dbName;
                    printRow(pw, DB_INFO_FORMAT, objArr);
                }
            }
            String assetAlloc = AssetManager.getAssetAllocations();
            if (assetAlloc != null) {
                pw.println(Separators.SP);
                pw.println(" Asset Allocations");
                pw.print(assetAlloc);
            }
        }

        @Override // android.app.IApplicationThread
        public void dumpGfxInfo(FileDescriptor fd, String[] args) {
            ActivityThread.this.dumpGraphicsInfo(fd);
            WindowManagerGlobal.getInstance().dumpGfxInfo(fd);
        }

        @Override // android.app.IApplicationThread
        public void dumpDbInfo(FileDescriptor fd, String[] args) {
            PrintWriter pw = new FastPrintWriter(new FileOutputStream(fd));
            PrintWriterPrinter printer = new PrintWriterPrinter(pw);
            SQLiteDebug.dump(printer, args);
            pw.flush();
        }

        @Override // android.app.IApplicationThread
        public void unstableProviderDied(IBinder provider) {
            ActivityThread.this.queueOrSendMessage(142, provider);
        }

        @Override // android.app.IApplicationThread
        public void requestAssistContextExtras(IBinder activityToken, IBinder requestToken, int requestType) {
            RequestAssistContextExtras cmd = new RequestAssistContextExtras();
            cmd.activityToken = activityToken;
            cmd.requestToken = requestToken;
            cmd.requestType = requestType;
            ActivityThread.this.queueOrSendMessage(143, cmd);
        }

        private void printRow(PrintWriter pw, String format, Object... objs) {
            pw.println(String.format(format, objs));
        }

        @Override // android.app.IApplicationThread
        public void setCoreSettings(Bundle coreSettings) {
            ActivityThread.this.queueOrSendMessage(138, coreSettings);
        }

        @Override // android.app.IApplicationThread
        public void updatePackageCompatibilityInfo(String pkg, CompatibilityInfo info) {
            UpdateCompatibilityData ucd = new UpdateCompatibilityData();
            ucd.pkg = pkg;
            ucd.info = info;
            ActivityThread.this.queueOrSendMessage(139, ucd);
        }

        @Override // android.app.IApplicationThread
        public void scheduleTrimMemory(int level) {
            ActivityThread.this.queueOrSendMessage(140, null, level);
        }

        @Override // android.app.IApplicationThread
        public void scheduleTranslucentConversionComplete(IBinder token, boolean drawComplete) {
            ActivityThread.this.queueOrSendMessage(144, token, drawComplete ? 1 : 0);
        }

        @Override // android.app.IApplicationThread
        public void setProcessState(int state) {
            updateProcessState(state, true);
        }

        public void updateProcessState(int processState, boolean fromIpc) {
            synchronized (this) {
                if (this.mLastProcessState != processState) {
                    this.mLastProcessState = processState;
                }
            }
        }

        @Override // android.app.IApplicationThread
        public void scheduleInstallProvider(ProviderInfo provider) {
            ActivityThread.this.queueOrSendMessage(145, provider);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActivityThread$H.class */
    public class H extends Handler {
        public static final int LAUNCH_ACTIVITY = 100;
        public static final int PAUSE_ACTIVITY = 101;
        public static final int PAUSE_ACTIVITY_FINISHING = 102;
        public static final int STOP_ACTIVITY_SHOW = 103;
        public static final int STOP_ACTIVITY_HIDE = 104;
        public static final int SHOW_WINDOW = 105;
        public static final int HIDE_WINDOW = 106;
        public static final int RESUME_ACTIVITY = 107;
        public static final int SEND_RESULT = 108;
        public static final int DESTROY_ACTIVITY = 109;
        public static final int BIND_APPLICATION = 110;
        public static final int EXIT_APPLICATION = 111;
        public static final int NEW_INTENT = 112;
        public static final int RECEIVER = 113;
        public static final int CREATE_SERVICE = 114;
        public static final int SERVICE_ARGS = 115;
        public static final int STOP_SERVICE = 116;
        public static final int REQUEST_THUMBNAIL = 117;
        public static final int CONFIGURATION_CHANGED = 118;
        public static final int CLEAN_UP_CONTEXT = 119;
        public static final int GC_WHEN_IDLE = 120;
        public static final int BIND_SERVICE = 121;
        public static final int UNBIND_SERVICE = 122;
        public static final int DUMP_SERVICE = 123;
        public static final int LOW_MEMORY = 124;
        public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
        public static final int RELAUNCH_ACTIVITY = 126;
        public static final int PROFILER_CONTROL = 127;
        public static final int CREATE_BACKUP_AGENT = 128;
        public static final int DESTROY_BACKUP_AGENT = 129;
        public static final int SUICIDE = 130;
        public static final int REMOVE_PROVIDER = 131;
        public static final int ENABLE_JIT = 132;
        public static final int DISPATCH_PACKAGE_BROADCAST = 133;
        public static final int SCHEDULE_CRASH = 134;
        public static final int DUMP_HEAP = 135;
        public static final int DUMP_ACTIVITY = 136;
        public static final int SLEEPING = 137;
        public static final int SET_CORE_SETTINGS = 138;
        public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;
        public static final int TRIM_MEMORY = 140;
        public static final int DUMP_PROVIDER = 141;
        public static final int UNSTABLE_PROVIDER_DIED = 142;
        public static final int REQUEST_ASSIST_CONTEXT_EXTRAS = 143;
        public static final int TRANSLUCENT_CONVERSION_COMPLETE = 144;
        public static final int INSTALL_PROVIDER = 145;

        private H() {
        }

        String codeToString(int code) {
            return Integer.toString(code);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    Trace.traceBegin(64L, "activityStart");
                    ActivityClientRecord r = (ActivityClientRecord) msg.obj;
                    r.packageInfo = ActivityThread.this.getPackageInfoNoCheck(r.activityInfo.applicationInfo, r.compatInfo);
                    ActivityThread.this.handleLaunchActivity(r, null);
                    Trace.traceEnd(64L);
                    return;
                case 101:
                    Trace.traceBegin(64L, "activityPause");
                    ActivityThread.this.handlePauseActivity((IBinder) msg.obj, false, msg.arg1 != 0, msg.arg2);
                    maybeSnapshot();
                    Trace.traceEnd(64L);
                    return;
                case 102:
                    Trace.traceBegin(64L, "activityPause");
                    ActivityThread.this.handlePauseActivity((IBinder) msg.obj, true, msg.arg1 != 0, msg.arg2);
                    Trace.traceEnd(64L);
                    return;
                case 103:
                    Trace.traceBegin(64L, "activityStop");
                    ActivityThread.this.handleStopActivity((IBinder) msg.obj, true, msg.arg2);
                    Trace.traceEnd(64L);
                    return;
                case 104:
                    Trace.traceBegin(64L, "activityStop");
                    ActivityThread.this.handleStopActivity((IBinder) msg.obj, false, msg.arg2);
                    Trace.traceEnd(64L);
                    return;
                case 105:
                    Trace.traceBegin(64L, "activityShowWindow");
                    ActivityThread.this.handleWindowVisibility((IBinder) msg.obj, true);
                    Trace.traceEnd(64L);
                    return;
                case 106:
                    Trace.traceBegin(64L, "activityHideWindow");
                    ActivityThread.this.handleWindowVisibility((IBinder) msg.obj, false);
                    Trace.traceEnd(64L);
                    return;
                case 107:
                    Trace.traceBegin(64L, "activityResume");
                    ActivityThread.this.handleResumeActivity((IBinder) msg.obj, true, msg.arg1 != 0, true);
                    Trace.traceEnd(64L);
                    return;
                case 108:
                    Trace.traceBegin(64L, "activityDeliverResult");
                    ActivityThread.this.handleSendResult((ResultData) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 109:
                    Trace.traceBegin(64L, "activityDestroy");
                    ActivityThread.this.handleDestroyActivity((IBinder) msg.obj, msg.arg1 != 0, msg.arg2, false);
                    Trace.traceEnd(64L);
                    return;
                case 110:
                    Trace.traceBegin(64L, "bindApplication");
                    AppBindData data = (AppBindData) msg.obj;
                    ActivityThread.this.handleBindApplication(data);
                    Trace.traceEnd(64L);
                    return;
                case 111:
                    if (ActivityThread.this.mInitialApplication != null) {
                        ActivityThread.this.mInitialApplication.onTerminate();
                    }
                    Looper.myLooper().quit();
                    return;
                case 112:
                    Trace.traceBegin(64L, "activityNewIntent");
                    ActivityThread.this.handleNewIntent((NewIntentData) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 113:
                    Trace.traceBegin(64L, "broadcastReceiveComp");
                    ActivityThread.this.handleReceiver((ReceiverData) msg.obj);
                    maybeSnapshot();
                    Trace.traceEnd(64L);
                    return;
                case 114:
                    Trace.traceBegin(64L, "serviceCreate");
                    ActivityThread.this.handleCreateService((CreateServiceData) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 115:
                    Trace.traceBegin(64L, "serviceStart");
                    ActivityThread.this.handleServiceArgs((ServiceArgsData) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 116:
                    Trace.traceBegin(64L, "serviceStop");
                    ActivityThread.this.handleStopService((IBinder) msg.obj);
                    maybeSnapshot();
                    Trace.traceEnd(64L);
                    return;
                case 117:
                    Trace.traceBegin(64L, "requestThumbnail");
                    ActivityThread.this.handleRequestThumbnail((IBinder) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 118:
                    Trace.traceBegin(64L, "configChanged");
                    ActivityThread.this.mCurDefaultDisplayDpi = ((Configuration) msg.obj).densityDpi;
                    ActivityThread.this.handleConfigurationChanged((Configuration) msg.obj, null);
                    Trace.traceEnd(64L);
                    return;
                case 119:
                    ContextCleanupInfo cci = (ContextCleanupInfo) msg.obj;
                    cci.context.performFinalCleanup(cci.who, cci.what);
                    return;
                case 120:
                    ActivityThread.this.scheduleGcIdler();
                    return;
                case 121:
                    Trace.traceBegin(64L, "serviceBind");
                    ActivityThread.this.handleBindService((BindServiceData) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 122:
                    Trace.traceBegin(64L, "serviceUnbind");
                    ActivityThread.this.handleUnbindService((BindServiceData) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 123:
                    ActivityThread.this.handleDumpService((DumpComponentInfo) msg.obj);
                    return;
                case 124:
                    Trace.traceBegin(64L, "lowMemory");
                    ActivityThread.this.handleLowMemory();
                    Trace.traceEnd(64L);
                    return;
                case 125:
                    Trace.traceBegin(64L, "activityConfigChanged");
                    ActivityThread.this.handleActivityConfigurationChanged((IBinder) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 126:
                    Trace.traceBegin(64L, "activityRestart");
                    ActivityThread.this.handleRelaunchActivity((ActivityClientRecord) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 127:
                    ActivityThread.this.handleProfilerControl(msg.arg1 != 0, (ProfilerControlData) msg.obj, msg.arg2);
                    return;
                case 128:
                    Trace.traceBegin(64L, "backupCreateAgent");
                    ActivityThread.this.handleCreateBackupAgent((CreateBackupAgentData) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 129:
                    Trace.traceBegin(64L, "backupDestroyAgent");
                    ActivityThread.this.handleDestroyBackupAgent((CreateBackupAgentData) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 130:
                    Process.killProcess(Process.myPid());
                    return;
                case 131:
                    Trace.traceBegin(64L, "providerRemove");
                    ActivityThread.this.completeRemoveProvider((ProviderRefCount) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 132:
                    ActivityThread.this.ensureJitEnabled();
                    return;
                case 133:
                    Trace.traceBegin(64L, "broadcastPackage");
                    ActivityThread.this.handleDispatchPackageBroadcast(msg.arg1, (String[]) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 134:
                    throw new RemoteServiceException((String) msg.obj);
                case 135:
                    ActivityThread.handleDumpHeap(msg.arg1 != 0, (DumpHeapData) msg.obj);
                    return;
                case 136:
                    ActivityThread.this.handleDumpActivity((DumpComponentInfo) msg.obj);
                    return;
                case 137:
                    Trace.traceBegin(64L, "sleeping");
                    ActivityThread.this.handleSleeping((IBinder) msg.obj, msg.arg1 != 0);
                    Trace.traceEnd(64L);
                    return;
                case 138:
                    Trace.traceBegin(64L, "setCoreSettings");
                    ActivityThread.this.handleSetCoreSettings((Bundle) msg.obj);
                    Trace.traceEnd(64L);
                    return;
                case 139:
                    ActivityThread.this.handleUpdatePackageCompatibilityInfo((UpdateCompatibilityData) msg.obj);
                    return;
                case 140:
                    Trace.traceBegin(64L, "trimMemory");
                    ActivityThread.this.handleTrimMemory(msg.arg1);
                    Trace.traceEnd(64L);
                    return;
                case 141:
                    ActivityThread.this.handleDumpProvider((DumpComponentInfo) msg.obj);
                    return;
                case 142:
                    ActivityThread.this.handleUnstableProviderDied((IBinder) msg.obj, false);
                    return;
                case 143:
                    ActivityThread.this.handleRequestAssistContextExtras((RequestAssistContextExtras) msg.obj);
                    return;
                case 144:
                    ActivityThread.this.handleTranslucentConversionComplete((IBinder) msg.obj, msg.arg1 == 1);
                    return;
                case 145:
                    ActivityThread.this.handleInstallProvider((ProviderInfo) msg.obj);
                    return;
                default:
                    return;
            }
        }

        private void maybeSnapshot() {
            Context context;
            if (ActivityThread.this.mBoundApplication != null && SamplingProfilerIntegration.isEnabled()) {
                String packageName = ActivityThread.this.mBoundApplication.info.mPackageName;
                PackageInfo packageInfo = null;
                try {
                    context = ActivityThread.this.getSystemContext();
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(ActivityThread.TAG, "cannot get package info for " + packageName, e);
                }
                if (context == null) {
                    Log.e(ActivityThread.TAG, "cannot get a valid context");
                    return;
                }
                PackageManager pm = context.getPackageManager();
                if (pm == null) {
                    Log.e(ActivityThread.TAG, "cannot get a valid PackageManager");
                    return;
                }
                packageInfo = pm.getPackageInfo(packageName, 1);
                SamplingProfilerIntegration.writeSnapshot(ActivityThread.this.mBoundApplication.processName, packageInfo);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActivityThread$Idler.class */
    public class Idler implements MessageQueue.IdleHandler {
        private Idler() {
        }

        /* JADX WARN: Removed duplicated region for block: B:22:0x0080  */
        @Override // android.os.MessageQueue.IdleHandler
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final boolean queueIdle() {
            /*
                r5 = this;
                r0 = r5
                android.app.ActivityThread r0 = android.app.ActivityThread.this
                android.app.ActivityThread$ActivityClientRecord r0 = r0.mNewActivities
                r6 = r0
                r0 = 0
                r7 = r0
                r0 = r5
                android.app.ActivityThread r0 = android.app.ActivityThread.this
                android.app.ActivityThread$AppBindData r0 = r0.mBoundApplication
                if (r0 == 0) goto L30
                r0 = r5
                android.app.ActivityThread r0 = android.app.ActivityThread.this
                android.app.ActivityThread$Profiler r0 = r0.mProfiler
                android.os.ParcelFileDescriptor r0 = r0.profileFd
                if (r0 == 0) goto L30
                r0 = r5
                android.app.ActivityThread r0 = android.app.ActivityThread.this
                android.app.ActivityThread$Profiler r0 = r0.mProfiler
                boolean r0 = r0.autoStopProfiler
                if (r0 == 0) goto L30
                r0 = 1
                r7 = r0
            L30:
                r0 = r6
                if (r0 == 0) goto L7c
                r0 = r5
                android.app.ActivityThread r0 = android.app.ActivityThread.this
                r1 = 0
                r0.mNewActivities = r1
                android.app.IActivityManager r0 = android.app.ActivityManagerNative.getDefault()
                r8 = r0
            L40:
                r0 = r6
                android.app.Activity r0 = r0.activity
                if (r0 == 0) goto L6a
                r0 = r6
                android.app.Activity r0 = r0.activity
                boolean r0 = r0.mFinished
                if (r0 != 0) goto L6a
                r0 = r8
                r1 = r6
                android.os.IBinder r1 = r1.token     // Catch: android.os.RemoteException -> L68
                r2 = r6
                android.content.res.Configuration r2 = r2.createdConfig     // Catch: android.os.RemoteException -> L68
                r3 = r7
                r0.activityIdle(r1, r2, r3)     // Catch: android.os.RemoteException -> L68
                r0 = r6
                r1 = 0
                r0.createdConfig = r1     // Catch: android.os.RemoteException -> L68
                goto L6a
            L68:
                r10 = move-exception
            L6a:
                r0 = r6
                r9 = r0
                r0 = r6
                android.app.ActivityThread$ActivityClientRecord r0 = r0.nextIdle
                r6 = r0
                r0 = r9
                r1 = 0
                r0.nextIdle = r1
                r0 = r6
                if (r0 != 0) goto L40
            L7c:
                r0 = r7
                if (r0 == 0) goto L8a
                r0 = r5
                android.app.ActivityThread r0 = android.app.ActivityThread.this
                android.app.ActivityThread$Profiler r0 = r0.mProfiler
                r0.stopProfiling()
            L8a:
                r0 = r5
                android.app.ActivityThread r0 = android.app.ActivityThread.this
                r0.ensureJitEnabled()
                r0 = 0
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.ActivityThread.Idler.queueIdle():boolean");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityThread$GcIdler.class */
    public final class GcIdler implements MessageQueue.IdleHandler {
        GcIdler() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public final boolean queueIdle() {
            ActivityThread.this.doGcIfNeeded();
            return false;
        }
    }

    public static ActivityThread currentActivityThread() {
        return sCurrentActivityThread;
    }

    public static String currentPackageName() {
        ActivityThread am = currentActivityThread();
        if (am == null || am.mBoundApplication == null) {
            return null;
        }
        return am.mBoundApplication.appInfo.packageName;
    }

    public static String currentProcessName() {
        ActivityThread am = currentActivityThread();
        if (am == null || am.mBoundApplication == null) {
            return null;
        }
        return am.mBoundApplication.processName;
    }

    public static Application currentApplication() {
        ActivityThread am = currentActivityThread();
        if (am != null) {
            return am.mInitialApplication;
        }
        return null;
    }

    public static IPackageManager getPackageManager() {
        if (sPackageManager != null) {
            return sPackageManager;
        }
        IBinder b = ServiceManager.getService(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
        sPackageManager = IPackageManager.Stub.asInterface(b);
        return sPackageManager;
    }

    Configuration applyConfigCompatMainThread(int displayDensity, Configuration config, CompatibilityInfo compat) {
        if (config == null) {
            return null;
        }
        if (!compat.supportsScreen()) {
            this.mMainThreadConfig.setTo(config);
            config = this.mMainThreadConfig;
            compat.applyToConfiguration(displayDensity, config);
        }
        return config;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Resources getTopLevelResources(String resDir, int displayId, Configuration overrideConfiguration, LoadedApk pkgInfo) {
        return this.mResourcesManager.getTopLevelResources(resDir, displayId, overrideConfiguration, pkgInfo.getCompatibilityInfo(), null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Handler getHandler() {
        return this.mH;
    }

    public final LoadedApk getPackageInfo(String packageName, CompatibilityInfo compatInfo, int flags) {
        return getPackageInfo(packageName, compatInfo, flags, UserHandle.myUserId());
    }

    public final LoadedApk getPackageInfo(String packageName, CompatibilityInfo compatInfo, int flags, int userId) {
        WeakReference<LoadedApk> ref;
        synchronized (this.mResourcesManager) {
            if ((flags & 1) != 0) {
                ref = this.mPackages.get(packageName);
            } else {
                ref = this.mResourcePackages.get(packageName);
            }
            LoadedApk packageInfo = ref != null ? ref.get() : null;
            if (packageInfo != null && (packageInfo.mResources == null || packageInfo.mResources.getAssets().isUpToDate())) {
                if (packageInfo.isSecurityViolation() && (flags & 2) == 0) {
                    throw new SecurityException("Requesting code from " + packageName + " to be run in process " + this.mBoundApplication.processName + Separators.SLASH + this.mBoundApplication.appInfo.uid);
                }
                return packageInfo;
            }
            ApplicationInfo ai = null;
            try {
                ai = getPackageManager().getApplicationInfo(packageName, 1024, userId);
            } catch (RemoteException e) {
            }
            if (ai != null) {
                return getPackageInfo(ai, compatInfo, flags);
            }
            return null;
        }
    }

    public final LoadedApk getPackageInfo(ApplicationInfo ai, CompatibilityInfo compatInfo, int flags) {
        boolean includeCode = (flags & 1) != 0;
        boolean securityViolation = includeCode && ai.uid != 0 && ai.uid != 1000 && (this.mBoundApplication == null || !UserHandle.isSameApp(ai.uid, this.mBoundApplication.appInfo.uid));
        if ((flags & 3) == 1 && securityViolation) {
            String msg = "Requesting code from " + ai.packageName + " (with uid " + ai.uid + Separators.RPAREN;
            if (this.mBoundApplication != null) {
                msg = msg + " to be run in process " + this.mBoundApplication.processName + " (with uid " + this.mBoundApplication.appInfo.uid + Separators.RPAREN;
            }
            throw new SecurityException(msg);
        }
        return getPackageInfo(ai, compatInfo, null, securityViolation, includeCode);
    }

    public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai, CompatibilityInfo compatInfo) {
        return getPackageInfo(ai, compatInfo, null, false, true);
    }

    public final LoadedApk peekPackageInfo(String packageName, boolean includeCode) {
        WeakReference<LoadedApk> ref;
        LoadedApk loadedApk;
        synchronized (this.mResourcesManager) {
            if (includeCode) {
                ref = this.mPackages.get(packageName);
            } else {
                ref = this.mResourcePackages.get(packageName);
            }
            loadedApk = ref != null ? ref.get() : null;
        }
        return loadedApk;
    }

    private LoadedApk getPackageInfo(ApplicationInfo aInfo, CompatibilityInfo compatInfo, ClassLoader baseLoader, boolean securityViolation, boolean includeCode) {
        WeakReference<LoadedApk> ref;
        LoadedApk loadedApk;
        synchronized (this.mResourcesManager) {
            if (includeCode) {
                ref = this.mPackages.get(aInfo.packageName);
            } else {
                ref = this.mResourcePackages.get(aInfo.packageName);
            }
            LoadedApk packageInfo = ref != null ? ref.get() : null;
            if (packageInfo == null || (packageInfo.mResources != null && !packageInfo.mResources.getAssets().isUpToDate())) {
                packageInfo = new LoadedApk(this, aInfo, compatInfo, this, baseLoader, securityViolation, includeCode && (aInfo.flags & 4) != 0);
                if (includeCode) {
                    this.mPackages.put(aInfo.packageName, new WeakReference<>(packageInfo));
                } else {
                    this.mResourcePackages.put(aInfo.packageName, new WeakReference<>(packageInfo));
                }
            }
            loadedApk = packageInfo;
        }
        return loadedApk;
    }

    ActivityThread() {
    }

    public ApplicationThread getApplicationThread() {
        return this.mAppThread;
    }

    public Instrumentation getInstrumentation() {
        return this.mInstrumentation;
    }

    public boolean isProfiling() {
        return (this.mProfiler == null || this.mProfiler.profileFile == null || this.mProfiler.profileFd != null) ? false : true;
    }

    public String getProfileFilePath() {
        return this.mProfiler.profileFile;
    }

    public Looper getLooper() {
        return this.mLooper;
    }

    public Application getApplication() {
        return this.mInitialApplication;
    }

    public String getProcessName() {
        return this.mBoundApplication.processName;
    }

    public ContextImpl getSystemContext() {
        synchronized (this) {
            if (mSystemContext == null) {
                ContextImpl context = ContextImpl.createSystemContext(this);
                LoadedApk info = new LoadedApk(this, "android", context, null, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO);
                context.init(info, (IBinder) null, this);
                context.getResources().updateConfiguration(this.mResourcesManager.getConfiguration(), this.mResourcesManager.getDisplayMetricsLocked(0));
                mSystemContext = context;
            }
        }
        return mSystemContext;
    }

    public void installSystemApplicationInfo(ApplicationInfo info) {
        synchronized (this) {
            ContextImpl context = getSystemContext();
            context.init(new LoadedApk(this, "android", context, info, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO), (IBinder) null, this);
            this.mProfiler = new Profiler();
        }
    }

    void ensureJitEnabled() {
        if (!this.mJitEnabled) {
            this.mJitEnabled = true;
            VMRuntime.getRuntime().startJitCompilation();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void scheduleGcIdler() {
        if (!this.mGcIdlerScheduled) {
            this.mGcIdlerScheduled = true;
            Looper.myQueue().addIdleHandler(this.mGcIdler);
        }
        this.mH.removeMessages(120);
    }

    void unscheduleGcIdler() {
        if (this.mGcIdlerScheduled) {
            this.mGcIdlerScheduled = false;
            Looper.myQueue().removeIdleHandler(this.mGcIdler);
        }
        this.mH.removeMessages(120);
    }

    void doGcIfNeeded() {
        this.mGcIdlerScheduled = false;
        long now = SystemClock.uptimeMillis();
        if (BinderInternal.getLastGcTime() + 5000 < now) {
            BinderInternal.forceGc("bg");
        }
    }

    public void registerOnActivityPausedListener(Activity activity, OnActivityPausedListener listener) {
        synchronized (this.mOnPauseListeners) {
            ArrayList<OnActivityPausedListener> list = this.mOnPauseListeners.get(activity);
            if (list == null) {
                list = new ArrayList<>();
                this.mOnPauseListeners.put(activity, list);
            }
            list.add(listener);
        }
    }

    public void unregisterOnActivityPausedListener(Activity activity, OnActivityPausedListener listener) {
        synchronized (this.mOnPauseListeners) {
            ArrayList<OnActivityPausedListener> list = this.mOnPauseListeners.get(activity);
            if (list != null) {
                list.remove(listener);
            }
        }
    }

    public final ActivityInfo resolveActivityInfo(Intent intent) {
        ActivityInfo aInfo = intent.resolveActivityInfo(this.mInitialApplication.getPackageManager(), 1024);
        if (aInfo == null) {
            Instrumentation.checkStartActivityResult(-2, intent);
        }
        return aInfo;
    }

    public final Activity startActivityNow(Activity parent, String id, Intent intent, ActivityInfo activityInfo, IBinder token, Bundle state, Activity.NonConfigurationInstances lastNonConfigurationInstances) {
        ActivityClientRecord r = new ActivityClientRecord();
        r.token = token;
        r.ident = 0;
        r.intent = intent;
        r.state = state;
        r.parent = parent;
        r.embeddedID = id;
        r.activityInfo = activityInfo;
        r.lastNonConfigurationInstances = lastNonConfigurationInstances;
        return performLaunchActivity(r, null);
    }

    public final Activity getActivity(IBinder token) {
        return this.mActivities.get(token).activity;
    }

    public final void sendActivityResult(IBinder token, String id, int requestCode, int resultCode, Intent data) {
        ArrayList<ResultInfo> list = new ArrayList<>();
        list.add(new ResultInfo(id, requestCode, resultCode, data));
        this.mAppThread.scheduleSendResult(token, list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queueOrSendMessage(int what, Object obj) {
        queueOrSendMessage(what, obj, 0, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queueOrSendMessage(int what, Object obj, int arg1) {
        queueOrSendMessage(what, obj, arg1, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queueOrSendMessage(int what, Object obj, int arg1, int arg2) {
        synchronized (this) {
            Message msg = Message.obtain();
            msg.what = what;
            msg.obj = obj;
            msg.arg1 = arg1;
            msg.arg2 = arg2;
            this.mH.sendMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void scheduleContextCleanup(ContextImpl context, String who, String what) {
        ContextCleanupInfo cci = new ContextCleanupInfo();
        cci.context = context;
        cci.who = who;
        cci.what = what;
        queueOrSendMessage(119, cci);
    }

    private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
        ActivityInfo aInfo = r.activityInfo;
        if (r.packageInfo == null) {
            r.packageInfo = getPackageInfo(aInfo.applicationInfo, r.compatInfo, 1);
        }
        ComponentName component = r.intent.getComponent();
        if (component == null) {
            component = r.intent.resolveActivity(this.mInitialApplication.getPackageManager());
            r.intent.setComponent(component);
        }
        if (r.activityInfo.targetActivity != null) {
            component = new ComponentName(r.activityInfo.packageName, r.activityInfo.targetActivity);
        }
        Activity activity = null;
        try {
            ClassLoader cl = r.packageInfo.getClassLoader();
            activity = this.mInstrumentation.newActivity(cl, component.getClassName(), r.intent);
            StrictMode.incrementExpectedActivityCount(activity.getClass());
            r.intent.setExtrasClassLoader(cl);
            if (r.state != null) {
                r.state.setClassLoader(cl);
            }
        } catch (Exception e) {
            if (!this.mInstrumentation.onException(activity, e)) {
                throw new RuntimeException("Unable to instantiate activity " + component + ": " + e.toString(), e);
            }
        }
        try {
            Application app = r.packageInfo.makeApplication(false, this.mInstrumentation);
            if (activity != null) {
                Context appContext = createBaseContextForActivity(r, activity);
                CharSequence title = r.activityInfo.loadLabel(appContext.getPackageManager());
                Configuration config = new Configuration(this.mCompatConfiguration);
                activity.attach(appContext, this, getInstrumentation(), r.token, r.ident, app, r.intent, r.activityInfo, title, r.parent, r.embeddedID, r.lastNonConfigurationInstances, config);
                if (customIntent != null) {
                    activity.mIntent = customIntent;
                }
                r.lastNonConfigurationInstances = null;
                activity.mStartedActivity = false;
                int theme = r.activityInfo.getThemeResource();
                if (theme != 0) {
                    activity.setTheme(theme);
                }
                activity.mCalled = false;
                this.mInstrumentation.callActivityOnCreate(activity, r.state);
                if (!activity.mCalled) {
                    throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString() + " did not call through to super.onCreate()");
                }
                r.activity = activity;
                r.stopped = true;
                if (!r.activity.mFinished) {
                    activity.performStart();
                    r.stopped = false;
                }
                if (!r.activity.mFinished && r.state != null) {
                    this.mInstrumentation.callActivityOnRestoreInstanceState(activity, r.state);
                }
                if (!r.activity.mFinished) {
                    activity.mCalled = false;
                    this.mInstrumentation.callActivityOnPostCreate(activity, r.state);
                    if (!activity.mCalled) {
                        throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString() + " did not call through to super.onPostCreate()");
                    }
                }
            }
            r.paused = true;
            this.mActivities.put(r.token, r);
        } catch (SuperNotCalledException e2) {
            throw e2;
        } catch (Exception e3) {
            if (!this.mInstrumentation.onException(activity, e3)) {
                throw new RuntimeException("Unable to start activity " + component + ": " + e3.toString(), e3);
            }
        }
        return activity;
    }

    private Context createBaseContextForActivity(ActivityClientRecord r, Activity activity) {
        ContextImpl appContext = new ContextImpl();
        appContext.init(r.packageInfo, r.token, this);
        appContext.setOuterContext(activity);
        Context baseContext = appContext;
        String pkgName = SystemProperties.get("debug.second-display.pkg");
        if (pkgName != null && !pkgName.isEmpty() && r.packageInfo.mPackageName.contains(pkgName)) {
            DisplayManagerGlobal dm = DisplayManagerGlobal.getInstance();
            int[] arr$ = dm.getDisplayIds();
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ >= len$) {
                    break;
                }
                int displayId = arr$[i$];
                if (displayId == 0) {
                    i$++;
                } else {
                    Display display = dm.getRealDisplay(displayId, r.token);
                    baseContext = appContext.createDisplayContext(display);
                    break;
                }
            }
        }
        return baseContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleLaunchActivity(ActivityClientRecord r, Intent customIntent) {
        unscheduleGcIdler();
        if (r.profileFd != null) {
            this.mProfiler.setProfiler(r.profileFile, r.profileFd);
            this.mProfiler.startProfiling();
            this.mProfiler.autoStopProfiler = r.autoStopProfiler;
        }
        handleConfigurationChanged(null, null);
        Activity a = performLaunchActivity(r, customIntent);
        if (a != null) {
            r.createdConfig = new Configuration(this.mConfiguration);
            Bundle oldState = r.state;
            handleResumeActivity(r.token, false, r.isForward, (r.activity.mFinished || r.startsNotResumed) ? false : true);
            if (!r.activity.mFinished && r.startsNotResumed) {
                try {
                    r.activity.mCalled = false;
                    this.mInstrumentation.callActivityOnPause(r.activity);
                    if (r.isPreHoneycomb()) {
                        r.state = oldState;
                    }
                } catch (SuperNotCalledException e) {
                    throw e;
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(r.activity, e2)) {
                        throw new RuntimeException("Unable to pause activity " + r.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
                    }
                }
                if (!r.activity.mCalled) {
                    throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString() + " did not call through to super.onPause()");
                }
                r.paused = true;
                return;
            }
            return;
        }
        try {
            ActivityManagerNative.getDefault().finishActivity(r.token, 0, null);
        } catch (RemoteException e3) {
        }
    }

    private void deliverNewIntents(ActivityClientRecord r, List<Intent> intents) {
        int N = intents.size();
        for (int i = 0; i < N; i++) {
            Intent intent = intents.get(i);
            intent.setExtrasClassLoader(r.activity.getClassLoader());
            r.activity.mFragments.noteStateNotSaved();
            this.mInstrumentation.callActivityOnNewIntent(r.activity, intent);
        }
    }

    public final void performNewIntents(IBinder token, List<Intent> intents) {
        ActivityClientRecord r = this.mActivities.get(token);
        if (r != null) {
            boolean resumed = !r.paused;
            if (resumed) {
                r.activity.mTemporaryPause = true;
                this.mInstrumentation.callActivityOnPause(r.activity);
            }
            deliverNewIntents(r, intents);
            if (resumed) {
                r.activity.performResume();
                r.activity.mTemporaryPause = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNewIntent(NewIntentData data) {
        performNewIntents(data.token, data.intents);
    }

    public void handleRequestAssistContextExtras(RequestAssistContextExtras cmd) {
        Bundle data = new Bundle();
        ActivityClientRecord r = this.mActivities.get(cmd.activityToken);
        if (r != null) {
            r.activity.getApplication().dispatchOnProvideAssistData(r.activity, data);
            r.activity.onProvideAssistData(data);
        }
        if (data.isEmpty()) {
            data = null;
        }
        IActivityManager mgr = ActivityManagerNative.getDefault();
        try {
            mgr.reportAssistContextExtras(cmd.requestToken, data);
        } catch (RemoteException e) {
        }
    }

    public void handleTranslucentConversionComplete(IBinder token, boolean drawComplete) {
        ActivityClientRecord r = this.mActivities.get(token);
        if (r != null) {
            r.activity.onTranslucentConversionComplete(drawComplete);
        }
    }

    public void handleInstallProvider(ProviderInfo info) {
        installContentProviders(this.mInitialApplication, Lists.newArrayList(new ProviderInfo[]{info}));
    }

    public static Intent getIntentBeingBroadcast() {
        return sCurrentBroadcastIntent.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleReceiver(ReceiverData data) {
        unscheduleGcIdler();
        String component = data.intent.getComponent().getClassName();
        LoadedApk packageInfo = getPackageInfoNoCheck(data.info.applicationInfo, data.compatInfo);
        IActivityManager mgr = ActivityManagerNative.getDefault();
        try {
            ClassLoader cl = packageInfo.getClassLoader();
            data.intent.setExtrasClassLoader(cl);
            data.setExtrasClassLoader(cl);
            BroadcastReceiver receiver = (BroadcastReceiver) cl.loadClass(component).newInstance();
            try {
                try {
                    Application app = packageInfo.makeApplication(false, this.mInstrumentation);
                    ContextImpl context = (ContextImpl) app.getBaseContext();
                    sCurrentBroadcastIntent.set(data.intent);
                    receiver.setPendingResult(data);
                    receiver.onReceive(context.getReceiverRestrictedContext(), data.intent);
                    sCurrentBroadcastIntent.set(null);
                } catch (Exception e) {
                    data.sendFinished(mgr);
                    if (!this.mInstrumentation.onException(receiver, e)) {
                        throw new RuntimeException("Unable to start receiver " + component + ": " + e.toString(), e);
                    }
                    sCurrentBroadcastIntent.set(null);
                }
                if (receiver.getPendingResult() != null) {
                    data.finish();
                }
            } catch (Throwable th) {
                sCurrentBroadcastIntent.set(null);
                throw th;
            }
        } catch (Exception e2) {
            data.sendFinished(mgr);
            throw new RuntimeException("Unable to instantiate receiver " + component + ": " + e2.toString(), e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCreateBackupAgent(CreateBackupAgentData data) {
        try {
            PackageInfo requestedPackage = getPackageManager().getPackageInfo(data.appInfo.packageName, 0, UserHandle.myUserId());
            if (requestedPackage.applicationInfo.uid != Process.myUid()) {
                Slog.w(TAG, "Asked to instantiate non-matching package " + data.appInfo.packageName);
                return;
            }
            unscheduleGcIdler();
            LoadedApk packageInfo = getPackageInfoNoCheck(data.appInfo, data.compatInfo);
            String packageName = packageInfo.mPackageName;
            if (packageName == null) {
                Slog.d(TAG, "Asked to create backup agent for nonexistent package");
            } else if (this.mBackupAgents.get(packageName) != null) {
                Slog.d(TAG, "BackupAgent   for " + packageName + " already exists");
            } else {
                String classname = data.appInfo.backupAgentName;
                if (classname == null && (data.backupMode == 1 || data.backupMode == 3)) {
                    classname = "android.app.backup.FullBackupAgent";
                }
                IBinder binder = null;
                try {
                    try {
                        ClassLoader cl = packageInfo.getClassLoader();
                        BackupAgent agent = (BackupAgent) cl.loadClass(classname).newInstance();
                        ContextImpl context = new ContextImpl();
                        context.init(packageInfo, (IBinder) null, this);
                        context.setOuterContext(agent);
                        agent.attach(context);
                        agent.onCreate();
                        binder = agent.onBind();
                        this.mBackupAgents.put(packageName, agent);
                    } catch (Exception e) {
                        Slog.e(TAG, "Agent threw during creation: " + e);
                        if (data.backupMode != 2 && data.backupMode != 3) {
                            throw e;
                        }
                    }
                    try {
                        ActivityManagerNative.getDefault().backupAgentCreated(packageName, binder);
                    } catch (RemoteException e2) {
                    }
                } catch (Exception e3) {
                    throw new RuntimeException("Unable to create BackupAgent " + classname + ": " + e3.toString(), e3);
                }
            }
        } catch (RemoteException e4) {
            Slog.e(TAG, "Can't reach package manager", e4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDestroyBackupAgent(CreateBackupAgentData data) {
        LoadedApk packageInfo = getPackageInfoNoCheck(data.appInfo, data.compatInfo);
        String packageName = packageInfo.mPackageName;
        BackupAgent agent = this.mBackupAgents.get(packageName);
        if (agent != null) {
            try {
                agent.onDestroy();
            } catch (Exception e) {
                Slog.w(TAG, "Exception thrown in onDestroy by backup agent of " + data.appInfo);
                e.printStackTrace();
            }
            this.mBackupAgents.remove(packageName);
            return;
        }
        Slog.w(TAG, "Attempt to destroy unknown backup agent " + data);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCreateService(CreateServiceData data) {
        unscheduleGcIdler();
        LoadedApk packageInfo = getPackageInfoNoCheck(data.info.applicationInfo, data.compatInfo);
        Service service = null;
        try {
            ClassLoader cl = packageInfo.getClassLoader();
            service = (Service) cl.loadClass(data.info.name).newInstance();
        } catch (Exception e) {
            if (!this.mInstrumentation.onException(service, e)) {
                throw new RuntimeException("Unable to instantiate service " + data.info.name + ": " + e.toString(), e);
            }
        }
        try {
            ContextImpl context = new ContextImpl();
            context.init(packageInfo, (IBinder) null, this);
            Application app = packageInfo.makeApplication(false, this.mInstrumentation);
            context.setOuterContext(service);
            service.attach(context, this, data.info.name, data.token, app, ActivityManagerNative.getDefault());
            service.onCreate();
            this.mServices.put(data.token, service);
            try {
                ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, 0, 0, 0);
            } catch (RemoteException e2) {
            }
        } catch (Exception e3) {
            if (!this.mInstrumentation.onException(service, e3)) {
                throw new RuntimeException("Unable to create service " + data.info.name + ": " + e3.toString(), e3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBindService(BindServiceData data) {
        Service s = this.mServices.get(data.token);
        if (s != null) {
            try {
                data.intent.setExtrasClassLoader(s.getClassLoader());
                try {
                    if (!data.rebind) {
                        IBinder binder = s.onBind(data.intent);
                        ActivityManagerNative.getDefault().publishService(data.token, data.intent, binder);
                    } else {
                        s.onRebind(data.intent);
                        ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, 0, 0, 0);
                    }
                    ensureJitEnabled();
                } catch (RemoteException e) {
                }
            } catch (Exception e2) {
                if (!this.mInstrumentation.onException(s, e2)) {
                    throw new RuntimeException("Unable to bind to service " + s + " with " + data.intent + ": " + e2.toString(), e2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUnbindService(BindServiceData data) {
        Service s = this.mServices.get(data.token);
        if (s != null) {
            try {
                data.intent.setExtrasClassLoader(s.getClassLoader());
                boolean doRebind = s.onUnbind(data.intent);
                try {
                    if (doRebind) {
                        ActivityManagerNative.getDefault().unbindFinished(data.token, data.intent, doRebind);
                    } else {
                        ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, 0, 0, 0);
                    }
                } catch (RemoteException e) {
                }
            } catch (Exception e2) {
                if (!this.mInstrumentation.onException(s, e2)) {
                    throw new RuntimeException("Unable to unbind to service " + s + " with " + data.intent + ": " + e2.toString(), e2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v11, types: [android.os.ParcelFileDescriptor, java.lang.AutoCloseable] */
    /* JADX WARN: Type inference failed for: r0v2, types: [android.os.ParcelFileDescriptor, java.lang.AutoCloseable] */
    public void handleDumpService(DumpComponentInfo info) {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            Service s = this.mServices.get(info.token);
            if (s != null) {
                PrintWriter pw = new FastPrintWriter(new FileOutputStream(info.fd.getFileDescriptor()));
                s.dump(info.fd.getFileDescriptor(), pw, info.args);
                pw.flush();
            }
        } finally {
            IoUtils.closeQuietly((AutoCloseable) info.fd);
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v11, types: [android.os.ParcelFileDescriptor, java.lang.AutoCloseable] */
    /* JADX WARN: Type inference failed for: r0v2, types: [android.os.ParcelFileDescriptor, java.lang.AutoCloseable] */
    public void handleDumpActivity(DumpComponentInfo info) {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            ActivityClientRecord r = this.mActivities.get(info.token);
            if (r != null && r.activity != null) {
                PrintWriter pw = new FastPrintWriter(new FileOutputStream(info.fd.getFileDescriptor()));
                r.activity.dump(info.prefix, info.fd.getFileDescriptor(), pw, info.args);
                pw.flush();
            }
        } finally {
            IoUtils.closeQuietly((AutoCloseable) info.fd);
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v11, types: [android.os.ParcelFileDescriptor, java.lang.AutoCloseable] */
    /* JADX WARN: Type inference failed for: r0v2, types: [android.os.ParcelFileDescriptor, java.lang.AutoCloseable] */
    public void handleDumpProvider(DumpComponentInfo info) {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            ProviderClientRecord r = this.mLocalProviders.get(info.token);
            if (r != null && r.mLocalProvider != null) {
                PrintWriter pw = new FastPrintWriter(new FileOutputStream(info.fd.getFileDescriptor()));
                r.mLocalProvider.dump(info.fd.getFileDescriptor(), pw, info.args);
                pw.flush();
            }
        } finally {
            IoUtils.closeQuietly((AutoCloseable) info.fd);
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleServiceArgs(ServiceArgsData data) {
        int res;
        Service s = this.mServices.get(data.token);
        if (s != null) {
            try {
                if (data.args != null) {
                    data.args.setExtrasClassLoader(s.getClassLoader());
                }
                if (!data.taskRemoved) {
                    res = s.onStartCommand(data.args, data.flags, data.startId);
                } else {
                    s.onTaskRemoved(data.args);
                    res = 1000;
                }
                QueuedWork.waitToFinish();
                try {
                    ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, 1, data.startId, res);
                } catch (RemoteException e) {
                }
                ensureJitEnabled();
            } catch (Exception e2) {
                if (!this.mInstrumentation.onException(s, e2)) {
                    throw new RuntimeException("Unable to start service " + s + " with " + data.args + ": " + e2.toString(), e2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStopService(IBinder token) {
        Service s = this.mServices.remove(token);
        if (s != null) {
            try {
                s.onDestroy();
                Context context = s.getBaseContext();
                if (context instanceof ContextImpl) {
                    String who = s.getClassName();
                    ((ContextImpl) context).scheduleFinalCleanup(who, "Service");
                }
                QueuedWork.waitToFinish();
                try {
                    ActivityManagerNative.getDefault().serviceDoneExecuting(token, 0, 0, 0);
                } catch (RemoteException e) {
                }
            } catch (Exception e2) {
                if (!this.mInstrumentation.onException(s, e2)) {
                    throw new RuntimeException("Unable to stop service " + s + ": " + e2.toString(), e2);
                }
            }
        }
    }

    public final ActivityClientRecord performResumeActivity(IBinder token, boolean clearHide) {
        ActivityClientRecord r = this.mActivities.get(token);
        if (r != null && !r.activity.mFinished) {
            if (clearHide) {
                r.hideForNow = false;
                r.activity.mStartedActivity = false;
            }
            try {
                r.activity.mFragments.noteStateNotSaved();
                if (r.pendingIntents != null) {
                    deliverNewIntents(r, r.pendingIntents);
                    r.pendingIntents = null;
                }
                if (r.pendingResults != null) {
                    deliverResults(r, r.pendingResults);
                    r.pendingResults = null;
                }
                r.activity.performResume();
                EventLog.writeEvent(30022, Integer.valueOf(UserHandle.myUserId()), r.activity.getComponentName().getClassName());
                r.paused = false;
                r.stopped = false;
                r.state = null;
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(r.activity, e)) {
                    throw new RuntimeException("Unable to resume activity " + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
                }
            }
        }
        return r;
    }

    static final void cleanUpPendingRemoveWindows(ActivityClientRecord r) {
        if (r.mPendingRemoveWindow != null) {
            r.mPendingRemoveWindowManager.removeViewImmediate(r.mPendingRemoveWindow);
            IBinder wtoken = r.mPendingRemoveWindow.getWindowToken();
            if (wtoken != null) {
                WindowManagerGlobal.getInstance().closeAll(wtoken, r.activity.getClass().getName(), "Activity");
            }
        }
        r.mPendingRemoveWindow = null;
        r.mPendingRemoveWindowManager = null;
    }

    final void handleResumeActivity(IBinder token, boolean clearHide, boolean isForward, boolean reallyResume) {
        unscheduleGcIdler();
        ActivityClientRecord r = performResumeActivity(token, clearHide);
        if (r != null) {
            Activity a = r.activity;
            int forwardBit = isForward ? 256 : 0;
            boolean willBeVisible = !a.mStartedActivity;
            if (!willBeVisible) {
                try {
                    willBeVisible = ActivityManagerNative.getDefault().willActivityBeVisible(a.getActivityToken());
                } catch (RemoteException e) {
                }
            }
            if (r.window == null && !a.mFinished && willBeVisible) {
                r.window = r.activity.getWindow();
                View decor = r.window.getDecorView();
                decor.setVisibility(4);
                ViewManager wm = a.getWindowManager();
                WindowManager.LayoutParams l = r.window.getAttributes();
                a.mDecor = decor;
                l.type = 1;
                l.softInputMode |= forwardBit;
                if (a.mVisibleFromClient) {
                    a.mWindowAdded = true;
                    wm.addView(decor, l);
                }
            } else if (!willBeVisible) {
                r.hideForNow = true;
            }
            cleanUpPendingRemoveWindows(r);
            if (!r.activity.mFinished && willBeVisible && r.activity.mDecor != null && !r.hideForNow) {
                if (r.newConfig != null) {
                    performConfigurationChanged(r.activity, r.newConfig);
                    freeTextLayoutCachesIfNeeded(r.activity.mCurrentConfig.diff(r.newConfig));
                    r.newConfig = null;
                }
                WindowManager.LayoutParams l2 = r.window.getAttributes();
                if ((l2.softInputMode & 256) != forwardBit) {
                    l2.softInputMode = (l2.softInputMode & (-257)) | forwardBit;
                    if (r.activity.mVisibleFromClient) {
                        ViewManager wm2 = a.getWindowManager();
                        wm2.updateViewLayout(r.window.getDecorView(), l2);
                    }
                }
                r.activity.mVisibleFromServer = true;
                this.mNumVisibleActivities++;
                if (r.activity.mVisibleFromClient) {
                    r.activity.makeVisible();
                }
            }
            if (!r.onlyLocalRequest) {
                r.nextIdle = this.mNewActivities;
                this.mNewActivities = r;
                Looper.myQueue().addIdleHandler(new Idler());
            }
            r.onlyLocalRequest = false;
            if (reallyResume) {
                try {
                    ActivityManagerNative.getDefault().activityResumed(token);
                    return;
                } catch (RemoteException e2) {
                    return;
                }
            }
            return;
        }
        try {
            ActivityManagerNative.getDefault().finishActivity(token, 0, null);
        } catch (RemoteException e3) {
        }
    }

    private Bitmap createThumbnailBitmap(ActivityClientRecord r) {
        int h;
        Bitmap thumbnail = this.mAvailThumbnailBitmap;
        if (thumbnail == null) {
            try {
                int w = this.mThumbnailWidth;
                if (w < 0) {
                    Resources res = r.activity.getResources();
                    int dimensionPixelSize = res.getDimensionPixelSize(17104897);
                    h = dimensionPixelSize;
                    this.mThumbnailHeight = dimensionPixelSize;
                    int dimensionPixelSize2 = res.getDimensionPixelSize(17104898);
                    w = dimensionPixelSize2;
                    this.mThumbnailWidth = dimensionPixelSize2;
                } else {
                    h = this.mThumbnailHeight;
                }
                if (w > 0 && h > 0) {
                    thumbnail = Bitmap.createBitmap(r.activity.getResources().getDisplayMetrics(), w, h, THUMBNAIL_FORMAT);
                    thumbnail.eraseColor(0);
                }
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(r.activity, e)) {
                    throw new RuntimeException("Unable to create thumbnail of " + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
                }
                thumbnail = null;
            }
        }
        if (thumbnail != null) {
            Canvas cv = this.mThumbnailCanvas;
            if (cv == null) {
                Canvas canvas = new Canvas();
                cv = canvas;
                this.mThumbnailCanvas = canvas;
            }
            cv.setBitmap(thumbnail);
            if (!r.activity.onCreateThumbnail(thumbnail, cv)) {
                this.mAvailThumbnailBitmap = thumbnail;
                thumbnail = null;
            }
            cv.setBitmap(null);
        }
        return thumbnail;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePauseActivity(IBinder token, boolean finished, boolean userLeaving, int configChanges) {
        ActivityClientRecord r = this.mActivities.get(token);
        if (r != null) {
            if (userLeaving) {
                performUserLeavingActivity(r);
            }
            r.activity.mConfigChangeFlags |= configChanges;
            performPauseActivity(token, finished, r.isPreHoneycomb());
            if (r.isPreHoneycomb()) {
                QueuedWork.waitToFinish();
            }
            try {
                ActivityManagerNative.getDefault().activityPaused(token);
            } catch (RemoteException e) {
            }
        }
    }

    final void performUserLeavingActivity(ActivityClientRecord r) {
        this.mInstrumentation.callActivityOnUserLeaving(r.activity);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Bundle performPauseActivity(IBinder token, boolean finished, boolean saveState) {
        ActivityClientRecord r = this.mActivities.get(token);
        if (r != null) {
            return performPauseActivity(r, finished, saveState);
        }
        return null;
    }

    final Bundle performPauseActivity(ActivityClientRecord r, boolean finished, boolean saveState) {
        ArrayList<OnActivityPausedListener> listeners;
        if (r.paused) {
            if (r.activity.mFinished) {
                return null;
            }
            RuntimeException e = new RuntimeException("Performing pause of activity that is not resumed: " + r.intent.getComponent().toShortString());
            Slog.e(TAG, e.getMessage(), e);
        }
        Bundle state = null;
        if (finished) {
            r.activity.mFinished = true;
        }
        try {
            if (!r.activity.mFinished && saveState) {
                state = new Bundle();
                state.setAllowFds(false);
                this.mInstrumentation.callActivityOnSaveInstanceState(r.activity, state);
                r.state = state;
            }
            r.activity.mCalled = false;
            this.mInstrumentation.callActivityOnPause(r.activity);
            EventLog.writeEvent(30021, Integer.valueOf(UserHandle.myUserId()), r.activity.getComponentName().getClassName());
        } catch (SuperNotCalledException e2) {
            throw e2;
        } catch (Exception e3) {
            if (!this.mInstrumentation.onException(r.activity, e3)) {
                throw new RuntimeException("Unable to pause activity " + r.intent.getComponent().toShortString() + ": " + e3.toString(), e3);
            }
        }
        if (!r.activity.mCalled) {
            throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString() + " did not call through to super.onPause()");
        }
        r.paused = true;
        synchronized (this.mOnPauseListeners) {
            listeners = this.mOnPauseListeners.remove(r.activity);
        }
        int size = listeners != null ? listeners.size() : 0;
        for (int i = 0; i < size; i++) {
            listeners.get(i).onPaused(r.activity);
        }
        return state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performStopActivity(IBinder token, boolean saveState) {
        ActivityClientRecord r = this.mActivities.get(token);
        performStopActivityInner(r, null, false, saveState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActivityThread$StopInfo.class */
    public static class StopInfo implements Runnable {
        ActivityClientRecord activity;
        Bundle state;
        Bitmap thumbnail;
        CharSequence description;

        private StopInfo() {
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                ActivityManagerNative.getDefault().activityStopped(this.activity.token, this.state, this.thumbnail, this.description);
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActivityThread$ProviderRefCount.class */
    public static final class ProviderRefCount {
        public final IActivityManager.ContentProviderHolder holder;
        public final ProviderClientRecord client;
        public int stableCount;
        public int unstableCount;
        public boolean removePending;

        ProviderRefCount(IActivityManager.ContentProviderHolder inHolder, ProviderClientRecord inClient, int sCount, int uCount) {
            this.holder = inHolder;
            this.client = inClient;
            this.stableCount = sCount;
            this.unstableCount = uCount;
        }
    }

    private void performStopActivityInner(ActivityClientRecord r, StopInfo info, boolean keepShown, boolean saveState) {
        if (r != null) {
            if (!keepShown && r.stopped) {
                if (r.activity.mFinished) {
                    return;
                }
                RuntimeException e = new RuntimeException("Performing stop of activity that is not resumed: " + r.intent.getComponent().toShortString());
                Slog.e(TAG, e.getMessage(), e);
            }
            if (info != null) {
                try {
                    info.thumbnail = null;
                    info.description = r.activity.onCreateDescription();
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(r.activity, e2)) {
                        throw new RuntimeException("Unable to save state of activity " + r.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
                    }
                }
            }
            if (!r.activity.mFinished && saveState) {
                if (r.state == null) {
                    Bundle state = new Bundle();
                    state.setAllowFds(false);
                    this.mInstrumentation.callActivityOnSaveInstanceState(r.activity, state);
                    r.state = state;
                } else {
                    Bundle bundle = r.state;
                }
            }
            if (!keepShown) {
                try {
                    r.activity.performStop();
                } catch (Exception e3) {
                    if (!this.mInstrumentation.onException(r.activity, e3)) {
                        throw new RuntimeException("Unable to stop activity " + r.intent.getComponent().toShortString() + ": " + e3.toString(), e3);
                    }
                }
                r.stopped = true;
            }
            r.paused = true;
        }
    }

    private void updateVisibility(ActivityClientRecord r, boolean show) {
        View v = r.activity.mDecor;
        if (v != null) {
            if (show) {
                if (!r.activity.mVisibleFromServer) {
                    r.activity.mVisibleFromServer = true;
                    this.mNumVisibleActivities++;
                    if (r.activity.mVisibleFromClient) {
                        r.activity.makeVisible();
                    }
                }
                if (r.newConfig != null) {
                    performConfigurationChanged(r.activity, r.newConfig);
                    freeTextLayoutCachesIfNeeded(r.activity.mCurrentConfig.diff(r.newConfig));
                    r.newConfig = null;
                }
            } else if (r.activity.mVisibleFromServer) {
                r.activity.mVisibleFromServer = false;
                this.mNumVisibleActivities--;
                v.setVisibility(4);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStopActivity(IBinder token, boolean show, int configChanges) {
        ActivityClientRecord r = this.mActivities.get(token);
        r.activity.mConfigChangeFlags |= configChanges;
        StopInfo info = new StopInfo();
        performStopActivityInner(r, info, show, true);
        updateVisibility(r, show);
        if (!r.isPreHoneycomb()) {
            QueuedWork.waitToFinish();
        }
        info.activity = r;
        info.state = r.state;
        this.mH.post(info);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performRestartActivity(IBinder token) {
        ActivityClientRecord r = this.mActivities.get(token);
        if (r.stopped) {
            r.activity.performRestart();
            r.stopped = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWindowVisibility(IBinder token, boolean show) {
        ActivityClientRecord r = this.mActivities.get(token);
        if (r == null) {
            Log.w(TAG, "handleWindowVisibility: no activity for token " + token);
            return;
        }
        if (!show && !r.stopped) {
            performStopActivityInner(r, null, show, false);
        } else if (show && r.stopped) {
            unscheduleGcIdler();
            r.activity.performRestart();
            r.stopped = false;
        }
        if (r.activity.mDecor != null) {
            updateVisibility(r, show);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSleeping(IBinder token, boolean sleeping) {
        ActivityClientRecord r = this.mActivities.get(token);
        if (r == null) {
            Log.w(TAG, "handleSleeping: no activity for token " + token);
        } else if (sleeping) {
            if (!r.stopped && !r.isPreHoneycomb()) {
                try {
                    r.activity.performStop();
                } catch (Exception e) {
                    if (!this.mInstrumentation.onException(r.activity, e)) {
                        throw new RuntimeException("Unable to stop activity " + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
                    }
                }
                r.stopped = true;
            }
            if (!r.isPreHoneycomb()) {
                QueuedWork.waitToFinish();
            }
            try {
                ActivityManagerNative.getDefault().activitySlept(r.token);
            } catch (RemoteException e2) {
            }
        } else if (r.stopped && r.activity.mVisibleFromServer) {
            r.activity.performRestart();
            r.stopped = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetCoreSettings(Bundle coreSettings) {
        synchronized (this.mResourcesManager) {
            this.mCoreSettings = coreSettings;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUpdatePackageCompatibilityInfo(UpdateCompatibilityData data) {
        LoadedApk apk = peekPackageInfo(data.pkg, false);
        if (apk != null) {
            apk.setCompatibilityInfo(data.info);
        }
        LoadedApk apk2 = peekPackageInfo(data.pkg, true);
        if (apk2 != null) {
            apk2.setCompatibilityInfo(data.info);
        }
        handleConfigurationChanged(this.mConfiguration, data.info);
        WindowManagerGlobal.getInstance().reportNewConfiguration(this.mConfiguration);
    }

    private void deliverResults(ActivityClientRecord r, List<ResultInfo> results) {
        int N = results.size();
        for (int i = 0; i < N; i++) {
            ResultInfo ri = results.get(i);
            try {
                if (ri.mData != null) {
                    ri.mData.setExtrasClassLoader(r.activity.getClassLoader());
                }
                r.activity.dispatchActivityResult(ri.mResultWho, ri.mRequestCode, ri.mResultCode, ri.mData);
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(r.activity, e)) {
                    throw new RuntimeException("Failure delivering result " + ri + " to activity " + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSendResult(ResultData res) {
        ActivityClientRecord r = this.mActivities.get(res.token);
        if (r != null) {
            boolean resumed = !r.paused;
            if (!r.activity.mFinished && r.activity.mDecor != null && r.hideForNow && resumed) {
                updateVisibility(r, true);
            }
            if (resumed) {
                try {
                    r.activity.mCalled = false;
                    r.activity.mTemporaryPause = true;
                    this.mInstrumentation.callActivityOnPause(r.activity);
                    if (!r.activity.mCalled) {
                        throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString() + " did not call through to super.onPause()");
                    }
                } catch (SuperNotCalledException e) {
                    throw e;
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(r.activity, e2)) {
                        throw new RuntimeException("Unable to pause activity " + r.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
                    }
                }
            }
            deliverResults(r, res.results);
            if (resumed) {
                r.activity.performResume();
                r.activity.mTemporaryPause = false;
            }
        }
    }

    public final ActivityClientRecord performDestroyActivity(IBinder token, boolean finishing) {
        return performDestroyActivity(token, finishing, 0, false);
    }

    private ActivityClientRecord performDestroyActivity(IBinder token, boolean finishing, int configChanges, boolean getNonConfigInstance) {
        ActivityClientRecord r = this.mActivities.get(token);
        Class<?> cls = null;
        if (r != null) {
            cls = r.activity.getClass();
            r.activity.mConfigChangeFlags |= configChanges;
            if (finishing) {
                r.activity.mFinished = true;
            }
            if (!r.paused) {
                try {
                    r.activity.mCalled = false;
                    this.mInstrumentation.callActivityOnPause(r.activity);
                    EventLog.writeEvent(30021, Integer.valueOf(UserHandle.myUserId()), r.activity.getComponentName().getClassName());
                } catch (SuperNotCalledException e) {
                    throw e;
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(r.activity, e2)) {
                        throw new RuntimeException("Unable to pause activity " + safeToComponentShortString(r.intent) + ": " + e2.toString(), e2);
                    }
                }
                if (!r.activity.mCalled) {
                    throw new SuperNotCalledException("Activity " + safeToComponentShortString(r.intent) + " did not call through to super.onPause()");
                }
                r.paused = true;
            }
            if (!r.stopped) {
                try {
                    r.activity.performStop();
                } catch (SuperNotCalledException e3) {
                    throw e3;
                } catch (Exception e4) {
                    if (!this.mInstrumentation.onException(r.activity, e4)) {
                        throw new RuntimeException("Unable to stop activity " + safeToComponentShortString(r.intent) + ": " + e4.toString(), e4);
                    }
                }
                r.stopped = true;
            }
            if (getNonConfigInstance) {
                try {
                    r.lastNonConfigurationInstances = r.activity.retainNonConfigurationInstances();
                } catch (Exception e5) {
                    if (!this.mInstrumentation.onException(r.activity, e5)) {
                        throw new RuntimeException("Unable to retain activity " + r.intent.getComponent().toShortString() + ": " + e5.toString(), e5);
                    }
                }
            }
            try {
                r.activity.mCalled = false;
                this.mInstrumentation.callActivityOnDestroy(r.activity);
                if (!r.activity.mCalled) {
                    throw new SuperNotCalledException("Activity " + safeToComponentShortString(r.intent) + " did not call through to super.onDestroy()");
                }
                if (r.window != null) {
                    r.window.closeAllPanels();
                }
            } catch (SuperNotCalledException e6) {
                throw e6;
            } catch (Exception e7) {
                if (!this.mInstrumentation.onException(r.activity, e7)) {
                    throw new RuntimeException("Unable to destroy activity " + safeToComponentShortString(r.intent) + ": " + e7.toString(), e7);
                }
            }
        }
        this.mActivities.remove(token);
        StrictMode.decrementExpectedActivityCount(cls);
        return r;
    }

    private static String safeToComponentShortString(Intent intent) {
        ComponentName component = intent.getComponent();
        return component == null ? "[Unknown]" : component.toShortString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDestroyActivity(IBinder token, boolean finishing, int configChanges, boolean getNonConfigInstance) {
        ActivityClientRecord r = performDestroyActivity(token, finishing, configChanges, getNonConfigInstance);
        if (r != null) {
            cleanUpPendingRemoveWindows(r);
            WindowManager wm = r.activity.getWindowManager();
            View v = r.activity.mDecor;
            if (v != null) {
                if (r.activity.mVisibleFromServer) {
                    this.mNumVisibleActivities--;
                }
                IBinder wtoken = v.getWindowToken();
                if (r.activity.mWindowAdded) {
                    if (r.onlyLocalRequest) {
                        r.mPendingRemoveWindow = v;
                        r.mPendingRemoveWindowManager = wm;
                    } else {
                        wm.removeViewImmediate(v);
                    }
                }
                if (wtoken != null && r.mPendingRemoveWindow == null) {
                    WindowManagerGlobal.getInstance().closeAll(wtoken, r.activity.getClass().getName(), "Activity");
                }
                r.activity.mDecor = null;
            }
            if (r.mPendingRemoveWindow == null) {
                WindowManagerGlobal.getInstance().closeAll(token, r.activity.getClass().getName(), "Activity");
            }
            Context c = r.activity.getBaseContext();
            if (c instanceof ContextImpl) {
                ((ContextImpl) c).scheduleFinalCleanup(r.activity.getClass().getName(), "Activity");
            }
        }
        if (finishing) {
            try {
                ActivityManagerNative.getDefault().activityDestroyed(token);
            } catch (RemoteException e) {
            }
        }
    }

    public final void requestRelaunchActivity(IBinder token, List<ResultInfo> pendingResults, List<Intent> pendingNewIntents, int configChanges, boolean notResumed, Configuration config, boolean fromServer) {
        ActivityClientRecord target = null;
        synchronized (this.mResourcesManager) {
            int i = 0;
            while (true) {
                if (i >= this.mRelaunchingActivities.size()) {
                    break;
                }
                ActivityClientRecord r = this.mRelaunchingActivities.get(i);
                if (r.token != token) {
                    i++;
                } else {
                    target = r;
                    if (pendingResults != null) {
                        if (r.pendingResults != null) {
                            r.pendingResults.addAll(pendingResults);
                        } else {
                            r.pendingResults = pendingResults;
                        }
                    }
                    if (pendingNewIntents != null) {
                        if (r.pendingIntents != null) {
                            r.pendingIntents.addAll(pendingNewIntents);
                        } else {
                            r.pendingIntents = pendingNewIntents;
                        }
                    }
                }
            }
            if (target == null) {
                target = new ActivityClientRecord();
                target.token = token;
                target.pendingResults = pendingResults;
                target.pendingIntents = pendingNewIntents;
                if (!fromServer) {
                    ActivityClientRecord existing = this.mActivities.get(token);
                    if (existing != null) {
                        target.startsNotResumed = existing.paused;
                    }
                    target.onlyLocalRequest = true;
                }
                this.mRelaunchingActivities.add(target);
                queueOrSendMessage(126, target);
            }
            if (fromServer) {
                target.startsNotResumed = notResumed;
                target.onlyLocalRequest = false;
            }
            if (config != null) {
                target.createdConfig = config;
            }
            target.pendingConfigChanges |= configChanges;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRelaunchActivity(ActivityClientRecord tmp) {
        unscheduleGcIdler();
        Configuration changedConfig = null;
        int configChanges = 0;
        synchronized (this.mResourcesManager) {
            int N = this.mRelaunchingActivities.size();
            IBinder token = tmp.token;
            ActivityClientRecord tmp2 = null;
            int i = 0;
            while (i < N) {
                ActivityClientRecord r = this.mRelaunchingActivities.get(i);
                if (r.token == token) {
                    tmp2 = r;
                    configChanges |= tmp2.pendingConfigChanges;
                    this.mRelaunchingActivities.remove(i);
                    i--;
                    N--;
                }
                i++;
            }
            if (tmp2 == null) {
                return;
            }
            if (this.mPendingConfiguration != null) {
                changedConfig = this.mPendingConfiguration;
                this.mPendingConfiguration = null;
            }
            if (tmp2.createdConfig != null && ((this.mConfiguration == null || (tmp2.createdConfig.isOtherSeqNewer(this.mConfiguration) && this.mConfiguration.diff(tmp2.createdConfig) != 0)) && (changedConfig == null || tmp2.createdConfig.isOtherSeqNewer(changedConfig)))) {
                changedConfig = tmp2.createdConfig;
            }
            if (changedConfig != null) {
                this.mCurDefaultDisplayDpi = changedConfig.densityDpi;
                updateDefaultDensity();
                handleConfigurationChanged(changedConfig, null);
            }
            ActivityClientRecord r2 = this.mActivities.get(tmp2.token);
            if (r2 == null) {
                return;
            }
            r2.activity.mConfigChangeFlags |= configChanges;
            r2.onlyLocalRequest = tmp2.onlyLocalRequest;
            Intent currentIntent = r2.activity.mIntent;
            r2.activity.mChangingConfigurations = true;
            if (!r2.paused) {
                performPauseActivity(r2.token, false, r2.isPreHoneycomb());
            }
            if (r2.state == null && !r2.stopped && !r2.isPreHoneycomb()) {
                r2.state = new Bundle();
                r2.state.setAllowFds(false);
                this.mInstrumentation.callActivityOnSaveInstanceState(r2.activity, r2.state);
            }
            handleDestroyActivity(r2.token, false, configChanges, true);
            r2.activity = null;
            r2.window = null;
            r2.hideForNow = false;
            r2.nextIdle = null;
            if (tmp2.pendingResults != null) {
                if (r2.pendingResults == null) {
                    r2.pendingResults = tmp2.pendingResults;
                } else {
                    r2.pendingResults.addAll(tmp2.pendingResults);
                }
            }
            if (tmp2.pendingIntents != null) {
                if (r2.pendingIntents == null) {
                    r2.pendingIntents = tmp2.pendingIntents;
                } else {
                    r2.pendingIntents.addAll(tmp2.pendingIntents);
                }
            }
            r2.startsNotResumed = tmp2.startsNotResumed;
            handleLaunchActivity(r2, currentIntent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRequestThumbnail(IBinder token) {
        ActivityClientRecord r = this.mActivities.get(token);
        Bitmap thumbnail = createThumbnailBitmap(r);
        CharSequence description = null;
        try {
            description = r.activity.onCreateDescription();
        } catch (Exception e) {
            if (!this.mInstrumentation.onException(r.activity, e)) {
                throw new RuntimeException("Unable to create description of activity " + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
            }
        }
        try {
            ActivityManagerNative.getDefault().reportThumbnail(token, thumbnail, description);
        } catch (RemoteException e2) {
        }
    }

    ArrayList<ComponentCallbacks2> collectComponentCallbacks(boolean allActivities, Configuration newConfig) {
        ArrayList<ComponentCallbacks2> callbacks = new ArrayList<>();
        synchronized (this.mResourcesManager) {
            int NAPP = this.mAllApplications.size();
            for (int i = 0; i < NAPP; i++) {
                callbacks.add(this.mAllApplications.get(i));
            }
            int NACT = this.mActivities.size();
            for (int i2 = 0; i2 < NACT; i2++) {
                ActivityClientRecord ar = this.mActivities.valueAt(i2);
                Activity a = ar.activity;
                if (a != null) {
                    Configuration thisConfig = applyConfigCompatMainThread(this.mCurDefaultDisplayDpi, newConfig, ar.packageInfo.getCompatibilityInfo());
                    if (!ar.activity.mFinished && (allActivities || !ar.paused)) {
                        callbacks.add(a);
                    } else if (thisConfig != null) {
                        ar.newConfig = thisConfig;
                    }
                }
            }
            int NSVC = this.mServices.size();
            for (int i3 = 0; i3 < NSVC; i3++) {
                callbacks.add(this.mServices.valueAt(i3));
            }
        }
        synchronized (this.mProviderMap) {
            int NPRV = this.mLocalProviders.size();
            for (int i4 = 0; i4 < NPRV; i4++) {
                callbacks.add(this.mLocalProviders.valueAt(i4).mLocalProvider);
            }
        }
        return callbacks;
    }

    private static void performConfigurationChanged(ComponentCallbacks2 cb, Configuration config) {
        Activity activity = cb instanceof Activity ? (Activity) cb : null;
        if (activity != null) {
            activity.mCalled = false;
        }
        boolean shouldChangeConfig = false;
        if (activity == null || activity.mCurrentConfig == null) {
            shouldChangeConfig = true;
        } else {
            int diff = activity.mCurrentConfig.diff(config);
            if (diff != 0 && ((activity.mActivityInfo.getRealConfigChanged() ^ (-1)) & diff) == 0) {
                shouldChangeConfig = true;
            }
        }
        if (shouldChangeConfig) {
            cb.onConfigurationChanged(config);
            if (activity != null) {
                if (!activity.mCalled) {
                    throw new SuperNotCalledException("Activity " + activity.getLocalClassName() + " did not call through to super.onConfigurationChanged()");
                }
                activity.mConfigChangeFlags = 0;
                activity.mCurrentConfig = new Configuration(config);
            }
        }
    }

    public final void applyConfigurationToResources(Configuration config) {
        synchronized (this.mResourcesManager) {
            this.mResourcesManager.applyConfigurationToResourcesLocked(config, null);
        }
    }

    final Configuration applyCompatConfiguration(int displayDensity) {
        Configuration config = this.mConfiguration;
        if (this.mCompatConfiguration == null) {
            this.mCompatConfiguration = new Configuration();
        }
        this.mCompatConfiguration.setTo(this.mConfiguration);
        if (this.mResourcesManager.applyCompatConfiguration(displayDensity, this.mCompatConfiguration)) {
            config = this.mCompatConfiguration;
        }
        return config;
    }

    final void handleConfigurationChanged(Configuration config, CompatibilityInfo compat) {
        synchronized (this.mResourcesManager) {
            if (this.mPendingConfiguration != null) {
                if (!this.mPendingConfiguration.isOtherSeqNewer(config)) {
                    config = this.mPendingConfiguration;
                    this.mCurDefaultDisplayDpi = config.densityDpi;
                    updateDefaultDensity();
                }
                this.mPendingConfiguration = null;
            }
            if (config == null) {
                return;
            }
            this.mResourcesManager.applyConfigurationToResourcesLocked(config, compat);
            if (this.mConfiguration == null) {
                this.mConfiguration = new Configuration();
            }
            if (this.mConfiguration.isOtherSeqNewer(config) || compat != null) {
                int configDiff = this.mConfiguration.diff(config);
                this.mConfiguration.updateFrom(config);
                Configuration config2 = applyCompatConfiguration(this.mCurDefaultDisplayDpi);
                ArrayList<ComponentCallbacks2> callbacks = collectComponentCallbacks(false, config2);
                WindowManagerGlobal.getInstance().trimLocalMemory();
                freeTextLayoutCachesIfNeeded(configDiff);
                if (callbacks != null) {
                    int N = callbacks.size();
                    for (int i = 0; i < N; i++) {
                        performConfigurationChanged(callbacks.get(i), config2);
                    }
                }
            }
        }
    }

    static void freeTextLayoutCachesIfNeeded(int configDiff) {
        if (configDiff != 0) {
            boolean hasLocaleConfigChange = (configDiff & 4) != 0;
            if (hasLocaleConfigChange) {
                Canvas.freeTextLayoutCaches();
            }
        }
    }

    final void handleActivityConfigurationChanged(IBinder token) {
        ActivityClientRecord r = this.mActivities.get(token);
        if (r == null || r.activity == null) {
            return;
        }
        performConfigurationChanged(r.activity, this.mCompatConfiguration);
        freeTextLayoutCachesIfNeeded(r.activity.mCurrentConfig.diff(this.mCompatConfiguration));
    }

    final void handleProfilerControl(boolean start, ProfilerControlData pcd, int profileType) {
        try {
            if (!start) {
                switch (profileType) {
                    default:
                        this.mProfiler.stopProfiling();
                        return;
                }
            }
            try {
            } catch (RuntimeException e) {
                Slog.w(TAG, "Profiling failed on path " + pcd.path + " -- can the process access this path?");
                try {
                    pcd.fd.close();
                } catch (IOException e2) {
                    Slog.w(TAG, "Failure closing profile fd", e2);
                }
            }
            switch (profileType) {
                default:
                    this.mProfiler.setProfiler(pcd.path, pcd.fd);
                    this.mProfiler.autoStopProfiler = false;
                    this.mProfiler.startProfiling();
                    try {
                        pcd.fd.close();
                    } catch (IOException e3) {
                        Slog.w(TAG, "Failure closing profile fd", e3);
                    }
                    return;
            }
        } catch (Throwable th) {
            try {
                pcd.fd.close();
            } catch (IOException e4) {
                Slog.w(TAG, "Failure closing profile fd", e4);
            }
            throw th;
        }
    }

    static final void handleDumpHeap(boolean managed, DumpHeapData dhd) {
        try {
            if (!managed) {
                Debug.dumpNativeHeap(dhd.fd.getFileDescriptor());
                return;
            }
            try {
                Debug.dumpHprofData(dhd.path, dhd.fd.getFileDescriptor());
                try {
                    dhd.fd.close();
                } catch (IOException e) {
                    Slog.w(TAG, "Failure closing profile fd", e);
                }
            } catch (IOException e2) {
                Slog.w(TAG, "Managed heap dump failed on path " + dhd.path + " -- can the process access this path?");
                try {
                    dhd.fd.close();
                } catch (IOException e3) {
                    Slog.w(TAG, "Failure closing profile fd", e3);
                }
            }
        } catch (Throwable th) {
            try {
                dhd.fd.close();
            } catch (IOException e4) {
                Slog.w(TAG, "Failure closing profile fd", e4);
            }
            throw th;
        }
    }

    final void handleDispatchPackageBroadcast(int cmd, String[] packages) {
        boolean hasPkgInfo = false;
        if (packages != null) {
            for (int i = packages.length - 1; i >= 0; i--) {
                if (!hasPkgInfo) {
                    WeakReference<LoadedApk> ref = this.mPackages.get(packages[i]);
                    if (ref != null && ref.get() != null) {
                        hasPkgInfo = true;
                    } else {
                        WeakReference<LoadedApk> ref2 = this.mResourcePackages.get(packages[i]);
                        if (ref2 != null && ref2.get() != null) {
                            hasPkgInfo = true;
                        }
                    }
                }
                this.mPackages.remove(packages[i]);
                this.mResourcePackages.remove(packages[i]);
            }
        }
        ApplicationPackageManager.handlePackageBroadcast(cmd, packages, hasPkgInfo);
    }

    final void handleLowMemory() {
        ArrayList<ComponentCallbacks2> callbacks = collectComponentCallbacks(true, null);
        int N = callbacks.size();
        for (int i = 0; i < N; i++) {
            callbacks.get(i).onLowMemory();
        }
        if (Process.myUid() != 1000) {
            int sqliteReleased = SQLiteDatabase.releaseMemory();
            EventLog.writeEvent((int) SQLITE_MEM_RELEASED_EVENT_LOG_TAG, sqliteReleased);
        }
        Canvas.freeCaches();
        Canvas.freeTextLayoutCaches();
        BinderInternal.forceGc("mem");
    }

    final void handleTrimMemory(int level) {
        WindowManagerGlobal windowManager = WindowManagerGlobal.getInstance();
        windowManager.startTrimMemory(level);
        ArrayList<ComponentCallbacks2> callbacks = collectComponentCallbacks(true, null);
        int N = callbacks.size();
        for (int i = 0; i < N; i++) {
            callbacks.get(i).onTrimMemory(level);
        }
        windowManager.endTrimMemory();
    }

    private void setupGraphicsSupport(LoadedApk info, File cacheDir) {
        if (Process.isIsolated()) {
            return;
        }
        try {
            int uid = Process.myUid();
            String[] packages = getPackageManager().getPackagesForUid(uid);
            if (packages != null && packages.length == 1) {
                HardwareRenderer.setupDiskCache(cacheDir);
                RenderScript.setupDiskCache(cacheDir);
            }
        } catch (RemoteException e) {
        }
    }

    private void updateDefaultDensity() {
        if (this.mCurDefaultDisplayDpi != 0 && this.mCurDefaultDisplayDpi != DisplayMetrics.DENSITY_DEVICE && !this.mDensityCompatMode) {
            Slog.i(TAG, "Switching default density from " + DisplayMetrics.DENSITY_DEVICE + " to " + this.mCurDefaultDisplayDpi);
            DisplayMetrics.DENSITY_DEVICE = this.mCurDefaultDisplayDpi;
            Bitmap.setDefaultDensity(160);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBindApplication(AppBindData data) {
        List<ProviderInfo> providers;
        this.mBoundApplication = data;
        this.mConfiguration = new Configuration(data.config);
        this.mCompatConfiguration = new Configuration(data.config);
        this.mProfiler = new Profiler();
        this.mProfiler.profileFile = data.initProfileFile;
        this.mProfiler.profileFd = data.initProfileFd;
        this.mProfiler.autoStopProfiler = data.initAutoStopProfiler;
        Process.setArgV0(data.processName);
        DdmHandleAppName.setAppName(data.processName, UserHandle.myUserId());
        if (data.persistent && !ActivityManager.isHighEndGfx()) {
            HardwareRenderer.disable(false);
        }
        if (this.mProfiler.profileFd != null) {
            this.mProfiler.startProfiling();
        }
        if (data.appInfo.targetSdkVersion <= 12) {
            AsyncTask.setDefaultExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        TimeZone.setDefault(null);
        Locale.setDefault(data.config.locale);
        this.mResourcesManager.applyConfigurationToResourcesLocked(data.config, data.compatInfo);
        this.mCurDefaultDisplayDpi = data.config.densityDpi;
        applyCompatConfiguration(this.mCurDefaultDisplayDpi);
        data.info = getPackageInfoNoCheck(data.appInfo, data.compatInfo);
        if ((data.appInfo.flags & 8192) == 0) {
            this.mDensityCompatMode = true;
            Bitmap.setDefaultDensity(160);
        }
        updateDefaultDensity();
        ContextImpl appContext = new ContextImpl();
        appContext.init(data.info, (IBinder) null, this);
        if (!Process.isIsolated()) {
            File cacheDir = appContext.getCacheDir();
            if (cacheDir != null) {
                System.setProperty("java.io.tmpdir", cacheDir.getAbsolutePath());
                setupGraphicsSupport(data.info, cacheDir);
            } else {
                Log.e(TAG, "Unable to setupGraphicsSupport due to missing cache directory");
            }
        }
        if ((data.appInfo.flags & 129) != 0) {
            StrictMode.conditionallyEnableDebugLogging();
        }
        if (data.appInfo.targetSdkVersion > 9) {
            StrictMode.enableDeathOnNetwork();
        }
        if (data.debugMode != 0) {
            Debug.changeDebugPort(8100);
            if (data.debugMode == 2) {
                Slog.w(TAG, "Application " + data.info.getPackageName() + " is waiting for the debugger on port 8100...");
                IActivityManager mgr = ActivityManagerNative.getDefault();
                try {
                    mgr.showWaitingForDebugger(this.mAppThread, true);
                } catch (RemoteException e) {
                }
                Debug.waitForDebugger();
                try {
                    mgr.showWaitingForDebugger(this.mAppThread, false);
                } catch (RemoteException e2) {
                }
            } else {
                Slog.w(TAG, "Application " + data.info.getPackageName() + " can be debugged on port 8100...");
            }
        }
        if (data.enableOpenGlTrace) {
            GLUtils.setTracingLevel(1);
        }
        boolean appTracingAllowed = (data.appInfo.flags & 2) != 0;
        Trace.setAppTracingAllowed(appTracingAllowed);
        IBinder b = ServiceManager.getService(Context.CONNECTIVITY_SERVICE);
        if (b != null) {
            IConnectivityManager service = IConnectivityManager.Stub.asInterface(b);
            try {
                ProxyProperties proxyProperties = service.getProxy();
                Proxy.setHttpProxySystemProperty(proxyProperties);
            } catch (RemoteException e3) {
            }
        }
        if (data.instrumentationName != null) {
            InstrumentationInfo ii = null;
            try {
                ii = appContext.getPackageManager().getInstrumentationInfo(data.instrumentationName, 0);
            } catch (PackageManager.NameNotFoundException e4) {
            }
            if (ii == null) {
                throw new RuntimeException("Unable to find instrumentation info for: " + data.instrumentationName);
            }
            this.mInstrumentationAppDir = ii.sourceDir;
            this.mInstrumentationAppLibraryDir = ii.nativeLibraryDir;
            this.mInstrumentationAppPackage = ii.packageName;
            this.mInstrumentedAppDir = data.info.getAppDir();
            this.mInstrumentedAppLibraryDir = data.info.getLibDir();
            ApplicationInfo instrApp = new ApplicationInfo();
            instrApp.packageName = ii.packageName;
            instrApp.sourceDir = ii.sourceDir;
            instrApp.publicSourceDir = ii.publicSourceDir;
            instrApp.dataDir = ii.dataDir;
            instrApp.nativeLibraryDir = ii.nativeLibraryDir;
            LoadedApk pi = getPackageInfo(instrApp, data.compatInfo, appContext.getClassLoader(), false, true);
            ContextImpl instrContext = new ContextImpl();
            instrContext.init(pi, (IBinder) null, this);
            try {
                ClassLoader cl = instrContext.getClassLoader();
                this.mInstrumentation = (Instrumentation) cl.loadClass(data.instrumentationName.getClassName()).newInstance();
                this.mInstrumentation.init(this, instrContext, appContext, new ComponentName(ii.packageName, ii.name), data.instrumentationWatcher, data.instrumentationUiAutomationConnection);
                if (this.mProfiler.profileFile != null && !ii.handleProfiling && this.mProfiler.profileFd == null) {
                    this.mProfiler.handlingProfiling = true;
                    File file = new File(this.mProfiler.profileFile);
                    file.getParentFile().mkdirs();
                    Debug.startMethodTracing(file.toString(), 8388608);
                }
            } catch (Exception e5) {
                throw new RuntimeException("Unable to instantiate instrumentation " + data.instrumentationName + ": " + e5.toString(), e5);
            }
        } else {
            this.mInstrumentation = new Instrumentation();
        }
        if ((data.appInfo.flags & 1048576) != 0) {
            VMRuntime.getRuntime().clearGrowthLimit();
        }
        StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskWrites();
        try {
            Application app = data.info.makeApplication(data.restrictedBackupMode, null);
            this.mInitialApplication = app;
            if (!data.restrictedBackupMode && (providers = data.providers) != null) {
                installContentProviders(app, providers);
                this.mH.sendEmptyMessageDelayed(132, 10000L);
            }
            try {
                this.mInstrumentation.onCreate(data.instrumentationArgs);
                try {
                    this.mInstrumentation.callApplicationOnCreate(app);
                } catch (Exception e6) {
                    if (!this.mInstrumentation.onException(app, e6)) {
                        throw new RuntimeException("Unable to create application " + app.getClass().getName() + ": " + e6.toString(), e6);
                    }
                }
            } catch (Exception e7) {
                throw new RuntimeException("Exception thrown in onCreate() of " + data.instrumentationName + ": " + e7.toString(), e7);
            }
        } finally {
            StrictMode.setThreadPolicy(savedPolicy);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void finishInstrumentation(int resultCode, Bundle results) {
        IActivityManager am = ActivityManagerNative.getDefault();
        if (this.mProfiler.profileFile != null && this.mProfiler.handlingProfiling && this.mProfiler.profileFd == null) {
            Debug.stopMethodTracing();
        }
        try {
            am.finishInstrumentation(this.mAppThread, resultCode, results);
        } catch (RemoteException e) {
        }
    }

    private void installContentProviders(Context context, List<ProviderInfo> providers) {
        ArrayList<IActivityManager.ContentProviderHolder> results = new ArrayList<>();
        for (ProviderInfo cpi : providers) {
            IActivityManager.ContentProviderHolder cph = installProvider(context, null, cpi, false, true, true);
            if (cph != null) {
                cph.noReleaseNeeded = true;
                results.add(cph);
            }
        }
        try {
            ActivityManagerNative.getDefault().publishContentProviders(getApplicationThread(), results);
        } catch (RemoteException e) {
        }
    }

    public final IContentProvider acquireProvider(Context c, String auth, int userId, boolean stable) {
        IContentProvider provider = acquireExistingProvider(c, auth, userId, stable);
        if (provider != null) {
            return provider;
        }
        IActivityManager.ContentProviderHolder holder = null;
        try {
            holder = ActivityManagerNative.getDefault().getContentProvider(getApplicationThread(), auth, userId, stable);
        } catch (RemoteException e) {
        }
        if (holder == null) {
            Slog.e(TAG, "Failed to find provider info for " + auth);
            return null;
        }
        return installProvider(c, holder, holder.info, true, holder.noReleaseNeeded, stable).provider;
    }

    private final void incProviderRefLocked(ProviderRefCount prc, boolean stable) {
        int unstableDelta;
        if (stable) {
            prc.stableCount++;
            if (prc.stableCount == 1) {
                if (prc.removePending) {
                    unstableDelta = -1;
                    prc.removePending = false;
                    this.mH.removeMessages(131, prc);
                } else {
                    unstableDelta = 0;
                }
                try {
                    ActivityManagerNative.getDefault().refContentProvider(prc.holder.connection, 1, unstableDelta);
                    return;
                } catch (RemoteException e) {
                    return;
                }
            }
            return;
        }
        prc.unstableCount++;
        if (prc.unstableCount == 1) {
            if (prc.removePending) {
                prc.removePending = false;
                this.mH.removeMessages(131, prc);
                return;
            }
            try {
                ActivityManagerNative.getDefault().refContentProvider(prc.holder.connection, 0, 1);
            } catch (RemoteException e2) {
            }
        }
    }

    public final IContentProvider acquireExistingProvider(Context c, String auth, int userId, boolean stable) {
        synchronized (this.mProviderMap) {
            ProviderKey key = new ProviderKey(auth, userId);
            ProviderClientRecord pr = this.mProviderMap.get(key);
            if (pr == null) {
                return null;
            }
            IContentProvider provider = pr.mProvider;
            IBinder jBinder = provider.asBinder();
            if (!jBinder.isBinderAlive()) {
                Log.i(TAG, "Acquiring provider " + auth + " for user " + userId + ": existing object's process dead");
                handleUnstableProviderDiedLocked(jBinder, true);
                return null;
            }
            ProviderRefCount prc = this.mProviderRefCountMap.get(jBinder);
            if (prc != null) {
                incProviderRefLocked(prc, stable);
            }
            return provider;
        }
    }

    public final boolean releaseProvider(IContentProvider provider, boolean stable) {
        if (provider == null) {
            return false;
        }
        IBinder jBinder = provider.asBinder();
        synchronized (this.mProviderMap) {
            ProviderRefCount prc = this.mProviderRefCountMap.get(jBinder);
            if (prc == null) {
                return false;
            }
            boolean lastRef = false;
            if (stable) {
                if (prc.stableCount == 0) {
                    return false;
                }
                prc.stableCount--;
                if (prc.stableCount == 0) {
                    lastRef = prc.unstableCount == 0;
                    try {
                        ActivityManagerNative.getDefault().refContentProvider(prc.holder.connection, -1, lastRef ? 1 : 0);
                    } catch (RemoteException e) {
                    }
                }
            } else if (prc.unstableCount == 0) {
                return false;
            } else {
                prc.unstableCount--;
                if (prc.unstableCount == 0) {
                    lastRef = prc.stableCount == 0;
                    if (!lastRef) {
                        try {
                            ActivityManagerNative.getDefault().refContentProvider(prc.holder.connection, 0, -1);
                        } catch (RemoteException e2) {
                        }
                    }
                }
            }
            if (lastRef) {
                if (!prc.removePending) {
                    prc.removePending = true;
                    Message msg = this.mH.obtainMessage(131, prc);
                    this.mH.sendMessage(msg);
                } else {
                    Slog.w(TAG, "Duplicate remove pending of provider " + prc.holder.info.name);
                }
            }
            return true;
        }
    }

    final void completeRemoveProvider(ProviderRefCount prc) {
        synchronized (this.mProviderMap) {
            if (prc.removePending) {
                prc.removePending = false;
                IBinder jBinder = prc.holder.provider.asBinder();
                ProviderRefCount existingPrc = this.mProviderRefCountMap.get(jBinder);
                if (existingPrc == prc) {
                    this.mProviderRefCountMap.remove(jBinder);
                }
                for (int i = this.mProviderMap.size() - 1; i >= 0; i--) {
                    ProviderClientRecord pr = this.mProviderMap.valueAt(i);
                    IBinder myBinder = pr.mProvider.asBinder();
                    if (myBinder == jBinder) {
                        this.mProviderMap.removeAt(i);
                    }
                }
                try {
                    ActivityManagerNative.getDefault().removeContentProvider(prc.holder.connection, false);
                } catch (RemoteException e) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void handleUnstableProviderDied(IBinder provider, boolean fromClient) {
        synchronized (this.mProviderMap) {
            handleUnstableProviderDiedLocked(provider, fromClient);
        }
    }

    final void handleUnstableProviderDiedLocked(IBinder provider, boolean fromClient) {
        ProviderRefCount prc = this.mProviderRefCountMap.get(provider);
        if (prc != null) {
            this.mProviderRefCountMap.remove(provider);
            for (int i = this.mProviderMap.size() - 1; i >= 0; i--) {
                ProviderClientRecord pr = this.mProviderMap.valueAt(i);
                if (pr != null && pr.mProvider.asBinder() == provider) {
                    Slog.i(TAG, "Removing dead content provider:" + pr.mProvider.toString());
                    this.mProviderMap.removeAt(i);
                }
            }
            if (fromClient) {
                try {
                    ActivityManagerNative.getDefault().unstableProviderDied(prc.holder.connection);
                } catch (RemoteException e) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void appNotRespondingViaProvider(IBinder provider) {
        synchronized (this.mProviderMap) {
            ProviderRefCount prc = this.mProviderRefCountMap.get(provider);
            if (prc != null) {
                try {
                    ActivityManagerNative.getDefault().appNotRespondingViaProvider(prc.holder.connection);
                } catch (RemoteException e) {
                }
            }
        }
    }

    private ProviderClientRecord installProviderAuthoritiesLocked(IContentProvider provider, ContentProvider localProvider, IActivityManager.ContentProviderHolder holder) {
        String[] auths = PATTERN_SEMICOLON.split(holder.info.authority);
        int userId = UserHandle.getUserId(holder.info.applicationInfo.uid);
        ProviderClientRecord pcr = new ProviderClientRecord(auths, provider, localProvider, holder);
        for (String auth : auths) {
            ProviderKey key = new ProviderKey(auth, userId);
            ProviderClientRecord existing = this.mProviderMap.get(key);
            if (existing != null) {
                Slog.w(TAG, "Content provider " + pcr.mHolder.info.name + " already published as " + auth);
            } else {
                this.mProviderMap.put(key, pcr);
            }
        }
        return pcr;
    }

    private IActivityManager.ContentProviderHolder installProvider(Context context, IActivityManager.ContentProviderHolder holder, ProviderInfo info, boolean noisy, boolean noReleaseNeeded, boolean stable) {
        IContentProvider provider;
        IActivityManager.ContentProviderHolder retHolder;
        ContentProvider localProvider = null;
        if (holder == null || holder.provider == null) {
            if (noisy) {
                Slog.d(TAG, "Loading provider " + info.authority + ": " + info.name);
            }
            Context c = null;
            ApplicationInfo ai = info.applicationInfo;
            if (context.getPackageName().equals(ai.packageName)) {
                c = context;
            } else if (this.mInitialApplication != null && this.mInitialApplication.getPackageName().equals(ai.packageName)) {
                c = this.mInitialApplication;
            } else {
                try {
                    c = context.createPackageContext(ai.packageName, 1);
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
            if (c == null) {
                Slog.w(TAG, "Unable to get context for package " + ai.packageName + " while loading content provider " + info.name);
                return null;
            }
            try {
                ClassLoader cl = c.getClassLoader();
                localProvider = (ContentProvider) cl.loadClass(info.name).newInstance();
                provider = localProvider.getIContentProvider();
                if (provider == null) {
                    Slog.e(TAG, "Failed to instantiate class " + info.name + " from sourceDir " + info.applicationInfo.sourceDir);
                    return null;
                }
                localProvider.attachInfo(c, info);
            } catch (Exception e2) {
                if (!this.mInstrumentation.onException(null, e2)) {
                    throw new RuntimeException("Unable to get provider " + info.name + ": " + e2.toString(), e2);
                }
                return null;
            }
        } else {
            provider = holder.provider;
        }
        synchronized (this.mProviderMap) {
            IBinder jBinder = provider.asBinder();
            if (localProvider != null) {
                ComponentName cname = new ComponentName(info.packageName, info.name);
                ProviderClientRecord pr = this.mLocalProvidersByName.get(cname);
                if (pr != null) {
                    IContentProvider iContentProvider = pr.mProvider;
                } else {
                    IActivityManager.ContentProviderHolder holder2 = new IActivityManager.ContentProviderHolder(info);
                    holder2.provider = provider;
                    holder2.noReleaseNeeded = true;
                    pr = installProviderAuthoritiesLocked(provider, localProvider, holder2);
                    this.mLocalProviders.put(jBinder, pr);
                    this.mLocalProvidersByName.put(cname, pr);
                }
                retHolder = pr.mHolder;
            } else {
                ProviderRefCount prc = this.mProviderRefCountMap.get(jBinder);
                if (prc != null) {
                    if (!noReleaseNeeded) {
                        incProviderRefLocked(prc, stable);
                        try {
                            ActivityManagerNative.getDefault().removeContentProvider(holder.connection, stable);
                        } catch (RemoteException e3) {
                        }
                    }
                } else {
                    ProviderClientRecord client = installProviderAuthoritiesLocked(provider, localProvider, holder);
                    if (noReleaseNeeded) {
                        prc = new ProviderRefCount(holder, client, 1000, 1000);
                    } else {
                        prc = stable ? new ProviderRefCount(holder, client, 1, 0) : new ProviderRefCount(holder, client, 0, 1);
                    }
                    this.mProviderRefCountMap.put(jBinder, prc);
                }
                retHolder = prc.holder;
            }
        }
        return retHolder;
    }

    private void attach(boolean system) {
        sCurrentActivityThread = this;
        this.mSystemThread = system;
        if (!system) {
            ViewRootImpl.addFirstDrawHandler(new Runnable() { // from class: android.app.ActivityThread.1
                @Override // java.lang.Runnable
                public void run() {
                    ActivityThread.this.ensureJitEnabled();
                }
            });
            DdmHandleAppName.setAppName("<pre-initialized>", UserHandle.myUserId());
            RuntimeInit.setApplicationObject(this.mAppThread.asBinder());
            IActivityManager mgr = ActivityManagerNative.getDefault();
            try {
                mgr.attachApplication(this.mAppThread);
            } catch (RemoteException e) {
            }
        } else {
            DdmHandleAppName.setAppName("system_process", UserHandle.myUserId());
            try {
                this.mInstrumentation = new Instrumentation();
                ContextImpl context = new ContextImpl();
                context.init(getSystemContext().mPackageInfo, (IBinder) null, this);
                Application app = Instrumentation.newApplication(Application.class, context);
                this.mAllApplications.add(app);
                this.mInitialApplication = app;
                app.onCreate();
            } catch (Exception e2) {
                throw new RuntimeException("Unable to instantiate Application():" + e2.toString(), e2);
            }
        }
        DropBox.setReporter(new DropBoxReporter());
        ViewRootImpl.addConfigCallback(new ComponentCallbacks2() { // from class: android.app.ActivityThread.2
            @Override // android.content.ComponentCallbacks
            public void onConfigurationChanged(Configuration newConfig) {
                synchronized (ActivityThread.this.mResourcesManager) {
                    if (ActivityThread.this.mResourcesManager.applyConfigurationToResourcesLocked(newConfig, null) && (ActivityThread.this.mPendingConfiguration == null || ActivityThread.this.mPendingConfiguration.isOtherSeqNewer(newConfig))) {
                        ActivityThread.this.mPendingConfiguration = newConfig;
                        ActivityThread.this.queueOrSendMessage(118, newConfig);
                    }
                }
            }

            @Override // android.content.ComponentCallbacks
            public void onLowMemory() {
            }

            @Override // android.content.ComponentCallbacks2
            public void onTrimMemory(int level) {
            }
        });
    }

    public static ActivityThread systemMain() {
        HardwareRenderer.disable(true);
        ActivityThread thread = new ActivityThread();
        thread.attach(true);
        return thread;
    }

    public final void installSystemProviders(List<ProviderInfo> providers) {
        if (providers != null) {
            installContentProviders(this.mInitialApplication, providers);
        }
    }

    public int getIntCoreSetting(String key, int defaultValue) {
        synchronized (this.mResourcesManager) {
            if (this.mCoreSettings != null) {
                return this.mCoreSettings.getInt(key, defaultValue);
            }
            return defaultValue;
        }
    }

    /* loaded from: ActivityThread$EventLoggingReporter.class */
    private static class EventLoggingReporter implements EventLogger.Reporter {
        private EventLoggingReporter() {
        }

        @Override // libcore.io.EventLogger.Reporter
        public void report(int code, Object... list) {
            EventLog.writeEvent(code, list);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActivityThread$DropBoxReporter.class */
    public class DropBoxReporter implements DropBox.Reporter {
        private DropBoxManager dropBox;

        public DropBoxReporter() {
            this.dropBox = (DropBoxManager) ActivityThread.this.getSystemContext().getSystemService(Context.DROPBOX_SERVICE);
        }

        @Override // libcore.io.DropBox.Reporter
        public void addData(String tag, byte[] data, int flags) {
            this.dropBox.addData(tag, data, flags);
        }

        @Override // libcore.io.DropBox.Reporter
        public void addText(String tag, String data) {
            this.dropBox.addText(tag, data);
        }
    }

    public static void main(String[] args) {
        SamplingProfilerIntegration.start();
        CloseGuard.setEnabled(false);
        Environment.initForCurrentUser();
        EventLogger.setReporter(new EventLoggingReporter());
        Security.addProvider(new AndroidKeyStoreProvider());
        Process.setArgV0("<pre-initialized>");
        Looper.prepareMainLooper();
        ActivityThread thread = new ActivityThread();
        thread.attach(false);
        if (sMainThreadHandler == null) {
            sMainThreadHandler = thread.getHandler();
        }
        AsyncTask.init();
        Looper.loop();
        throw new RuntimeException("Main thread loop unexpectedly exited");
    }
}