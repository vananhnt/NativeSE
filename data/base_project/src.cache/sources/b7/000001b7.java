package android.app;

import android.app.IServiceConnection;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.Trace;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.DisplayAdjustments;
import com.android.internal.util.ArrayUtils;
import gov.nist.core.Separators;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Enumeration;

/* loaded from: LoadedApk.class */
public final class LoadedApk {
    private static final String TAG = "LoadedApk";
    private final ActivityThread mActivityThread;
    private final ApplicationInfo mApplicationInfo;
    final String mPackageName;
    private final String mAppDir;
    private final String mResDir;
    private final String[] mSharedLibraries;
    private final String mDataDir;
    private final String mLibDir;
    private final File mDataDirFile;
    private final ClassLoader mBaseClassLoader;
    private final boolean mSecurityViolation;
    private final boolean mIncludeCode;
    Resources mResources;
    private ClassLoader mClassLoader;
    private Application mApplication;
    private final DisplayAdjustments mDisplayAdjustments = new DisplayAdjustments();
    private final ArrayMap<Context, ArrayMap<BroadcastReceiver, ReceiverDispatcher>> mReceivers = new ArrayMap<>();
    private final ArrayMap<Context, ArrayMap<BroadcastReceiver, ReceiverDispatcher>> mUnregisteredReceivers = new ArrayMap<>();
    private final ArrayMap<Context, ArrayMap<ServiceConnection, ServiceDispatcher>> mServices = new ArrayMap<>();
    private final ArrayMap<Context, ArrayMap<ServiceConnection, ServiceDispatcher>> mUnboundServices = new ArrayMap<>();
    int mClientCount = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Application getApplication() {
        return this.mApplication;
    }

    public LoadedApk(ActivityThread activityThread, ApplicationInfo aInfo, CompatibilityInfo compatInfo, ActivityThread mainThread, ClassLoader baseLoader, boolean securityViolation, boolean includeCode) {
        this.mActivityThread = activityThread;
        this.mApplicationInfo = aInfo;
        this.mPackageName = aInfo.packageName;
        this.mAppDir = aInfo.sourceDir;
        int myUid = Process.myUid();
        this.mResDir = aInfo.uid == myUid ? aInfo.sourceDir : aInfo.publicSourceDir;
        if (!UserHandle.isSameUser(aInfo.uid, myUid) && !Process.isIsolated()) {
            aInfo.dataDir = PackageManager.getDataDirForUser(UserHandle.getUserId(myUid), this.mPackageName);
        }
        this.mSharedLibraries = aInfo.sharedLibraryFiles;
        this.mDataDir = aInfo.dataDir;
        this.mDataDirFile = this.mDataDir != null ? new File(this.mDataDir) : null;
        this.mLibDir = aInfo.nativeLibraryDir;
        this.mBaseClassLoader = baseLoader;
        this.mSecurityViolation = securityViolation;
        this.mIncludeCode = includeCode;
        this.mDisplayAdjustments.setCompatibilityInfo(compatInfo);
        if (this.mAppDir == null) {
            if (ActivityThread.mSystemContext == null) {
                ActivityThread.mSystemContext = ContextImpl.createSystemContext(mainThread);
                ResourcesManager resourcesManager = ResourcesManager.getInstance();
                ActivityThread.mSystemContext.getResources().updateConfiguration(resourcesManager.getConfiguration(), resourcesManager.getDisplayMetricsLocked(0, this.mDisplayAdjustments), compatInfo);
            }
            this.mClassLoader = ActivityThread.mSystemContext.getClassLoader();
            this.mResources = ActivityThread.mSystemContext.getResources();
        }
    }

    public LoadedApk(ActivityThread activityThread, String name, Context systemContext, ApplicationInfo info, CompatibilityInfo compatInfo) {
        this.mActivityThread = activityThread;
        this.mApplicationInfo = info != null ? info : new ApplicationInfo();
        this.mApplicationInfo.packageName = name;
        this.mPackageName = name;
        this.mAppDir = null;
        this.mResDir = null;
        this.mSharedLibraries = null;
        this.mDataDir = null;
        this.mDataDirFile = null;
        this.mLibDir = null;
        this.mBaseClassLoader = null;
        this.mSecurityViolation = false;
        this.mIncludeCode = true;
        this.mClassLoader = systemContext.getClassLoader();
        this.mResources = systemContext.getResources();
        this.mDisplayAdjustments.setCompatibilityInfo(compatInfo);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public ApplicationInfo getApplicationInfo() {
        return this.mApplicationInfo;
    }

    public boolean isSecurityViolation() {
        return this.mSecurityViolation;
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mDisplayAdjustments.getCompatibilityInfo();
    }

    public void setCompatibilityInfo(CompatibilityInfo compatInfo) {
        this.mDisplayAdjustments.setCompatibilityInfo(compatInfo);
    }

    private static String[] getLibrariesFor(String packageName) {
        try {
            ApplicationInfo ai = ActivityThread.getPackageManager().getApplicationInfo(packageName, 1024, UserHandle.myUserId());
            if (ai == null) {
                return null;
            }
            return ai.sharedLibraryFiles;
        } catch (RemoteException e) {
            throw new AssertionError(e);
        }
    }

    private static String combineLibs(String[] list1, String[] list2) {
        StringBuilder result = new StringBuilder(300);
        boolean first = true;
        if (list1 != null) {
            for (String s : list1) {
                if (first) {
                    first = false;
                } else {
                    result.append(':');
                }
                result.append(s);
            }
        }
        boolean dupCheck = !first;
        if (list2 != null) {
            for (String s2 : list2) {
                if (!dupCheck || !ArrayUtils.contains(list1, s2)) {
                    if (first) {
                        first = false;
                    } else {
                        result.append(':');
                    }
                    result.append(s2);
                }
            }
        }
        return result.toString();
    }

    public ClassLoader getClassLoader() {
        synchronized (this) {
            if (this.mClassLoader != null) {
                return this.mClassLoader;
            }
            if (this.mIncludeCode && !this.mPackageName.equals("android")) {
                String zip = this.mAppDir;
                String libraryPath = this.mLibDir;
                String instrumentationAppDir = this.mActivityThread.mInstrumentationAppDir;
                String instrumentationAppLibraryDir = this.mActivityThread.mInstrumentationAppLibraryDir;
                String instrumentationAppPackage = this.mActivityThread.mInstrumentationAppPackage;
                String instrumentedAppDir = this.mActivityThread.mInstrumentedAppDir;
                String instrumentedAppLibraryDir = this.mActivityThread.mInstrumentedAppLibraryDir;
                String[] instrumentationLibs = null;
                if (this.mAppDir.equals(instrumentationAppDir) || this.mAppDir.equals(instrumentedAppDir)) {
                    zip = instrumentationAppDir + Separators.COLON + instrumentedAppDir;
                    libraryPath = instrumentationAppLibraryDir + Separators.COLON + instrumentedAppLibraryDir;
                    if (!instrumentedAppDir.equals(instrumentationAppDir)) {
                        instrumentationLibs = getLibrariesFor(instrumentationAppPackage);
                    }
                }
                if (this.mSharedLibraries != null || instrumentationLibs != null) {
                    zip = combineLibs(this.mSharedLibraries, instrumentationLibs) + ':' + zip;
                }
                StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
                this.mClassLoader = ApplicationLoaders.getDefault().getClassLoader(zip, libraryPath, this.mBaseClassLoader);
                initializeJavaContextClassLoader();
                StrictMode.setThreadPolicy(oldPolicy);
            } else if (this.mBaseClassLoader == null) {
                this.mClassLoader = ClassLoader.getSystemClassLoader();
            } else {
                this.mClassLoader = this.mBaseClassLoader;
            }
            return this.mClassLoader;
        }
    }

    private void initializeJavaContextClassLoader() {
        IPackageManager pm = ActivityThread.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.mPackageName, 0, UserHandle.myUserId());
            if (pi == null) {
                throw new IllegalStateException("Unable to get package info for " + this.mPackageName + "; is package not installed?");
            }
            boolean sharedUserIdSet = pi.sharedUserId != null;
            boolean processNameNotDefault = (pi.applicationInfo == null || this.mPackageName.equals(pi.applicationInfo.processName)) ? false : true;
            boolean sharable = sharedUserIdSet || processNameNotDefault;
            ClassLoader contextClassLoader = sharable ? new WarningContextClassLoader() : this.mClassLoader;
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        } catch (RemoteException e) {
            throw new IllegalStateException("Unable to get package info for " + this.mPackageName + "; is system dying?", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LoadedApk$WarningContextClassLoader.class */
    public static class WarningContextClassLoader extends ClassLoader {
        private static boolean warned = false;

        private WarningContextClassLoader() {
        }

        private void warn(String methodName) {
            if (warned) {
                return;
            }
            warned = true;
            Thread.currentThread().setContextClassLoader(getParent());
            Slog.w(ActivityThread.TAG, "ClassLoader." + methodName + ": The class loader returned by Thread.getContextClassLoader() may fail for processes that host multiple applications. You should explicitly specify a context class loader. For example: Thread.setContextClassLoader(getClass().getClassLoader());");
        }

        @Override // java.lang.ClassLoader
        public URL getResource(String resName) {
            warn("getResource");
            return getParent().getResource(resName);
        }

        @Override // java.lang.ClassLoader
        public Enumeration<URL> getResources(String resName) throws IOException {
            warn("getResources");
            return getParent().getResources(resName);
        }

        @Override // java.lang.ClassLoader
        public InputStream getResourceAsStream(String resName) {
            warn("getResourceAsStream");
            return getParent().getResourceAsStream(resName);
        }

        @Override // java.lang.ClassLoader
        public Class<?> loadClass(String className) throws ClassNotFoundException {
            warn("loadClass");
            return getParent().loadClass(className);
        }

        @Override // java.lang.ClassLoader
        public void setClassAssertionStatus(String cname, boolean enable) {
            warn("setClassAssertionStatus");
            getParent().setClassAssertionStatus(cname, enable);
        }

        @Override // java.lang.ClassLoader
        public void setPackageAssertionStatus(String pname, boolean enable) {
            warn("setPackageAssertionStatus");
            getParent().setPackageAssertionStatus(pname, enable);
        }

        @Override // java.lang.ClassLoader
        public void setDefaultAssertionStatus(boolean enable) {
            warn("setDefaultAssertionStatus");
            getParent().setDefaultAssertionStatus(enable);
        }

        @Override // java.lang.ClassLoader
        public void clearAssertionStatus() {
            warn("clearAssertionStatus");
            getParent().clearAssertionStatus();
        }
    }

    public String getAppDir() {
        return this.mAppDir;
    }

    public String getLibDir() {
        return this.mLibDir;
    }

    public String getResDir() {
        return this.mResDir;
    }

    public String getDataDir() {
        return this.mDataDir;
    }

    public File getDataDirFile() {
        return this.mDataDirFile;
    }

    public AssetManager getAssets(ActivityThread mainThread) {
        return getResources(mainThread).getAssets();
    }

    public Resources getResources(ActivityThread mainThread) {
        if (this.mResources == null) {
            this.mResources = mainThread.getTopLevelResources(this.mResDir, 0, null, this);
        }
        return this.mResources;
    }

    public Application makeApplication(boolean forceDefaultAppClass, Instrumentation instrumentation) {
        if (this.mApplication != null) {
            return this.mApplication;
        }
        Application app = null;
        String appClass = this.mApplicationInfo.className;
        appClass = (forceDefaultAppClass || appClass == null) ? "android.app.Application" : "android.app.Application";
        try {
            ClassLoader cl = getClassLoader();
            ContextImpl appContext = new ContextImpl();
            appContext.init(this, (IBinder) null, this.mActivityThread);
            app = this.mActivityThread.mInstrumentation.newApplication(cl, appClass, appContext);
            appContext.setOuterContext(app);
        } catch (Exception e) {
            if (!this.mActivityThread.mInstrumentation.onException(app, e)) {
                throw new RuntimeException("Unable to instantiate application " + appClass + ": " + e.toString(), e);
            }
        }
        this.mActivityThread.mAllApplications.add(app);
        this.mApplication = app;
        if (instrumentation != null) {
            try {
                instrumentation.callApplicationOnCreate(app);
            } catch (Exception e2) {
                if (!instrumentation.onException(app, e2)) {
                    throw new RuntimeException("Unable to create application " + app.getClass().getName() + ": " + e2.toString(), e2);
                }
            }
        }
        return app;
    }

    public void removeContextRegistrations(Context context, String who, String what) {
        boolean reportRegistrationLeaks = StrictMode.vmRegistrationLeaksEnabled();
        ArrayMap<BroadcastReceiver, ReceiverDispatcher> rmap = this.mReceivers.remove(context);
        if (rmap != null) {
            for (int i = 0; i < rmap.size(); i++) {
                ReceiverDispatcher rd = rmap.valueAt(i);
                IntentReceiverLeaked leak = new IntentReceiverLeaked(what + Separators.SP + who + " has leaked IntentReceiver " + rd.getIntentReceiver() + " that was originally registered here. Are you missing a call to unregisterReceiver()?");
                leak.setStackTrace(rd.getLocation().getStackTrace());
                Slog.e(ActivityThread.TAG, leak.getMessage(), leak);
                if (reportRegistrationLeaks) {
                    StrictMode.onIntentReceiverLeaked(leak);
                }
                try {
                    ActivityManagerNative.getDefault().unregisterReceiver(rd.getIIntentReceiver());
                } catch (RemoteException e) {
                }
            }
        }
        this.mUnregisteredReceivers.remove(context);
        ArrayMap<ServiceConnection, ServiceDispatcher> smap = this.mServices.remove(context);
        if (smap != null) {
            for (int i2 = 0; i2 < smap.size(); i2++) {
                ServiceDispatcher sd = smap.valueAt(i2);
                ServiceConnectionLeaked leak2 = new ServiceConnectionLeaked(what + Separators.SP + who + " has leaked ServiceConnection " + sd.getServiceConnection() + " that was originally bound here");
                leak2.setStackTrace(sd.getLocation().getStackTrace());
                Slog.e(ActivityThread.TAG, leak2.getMessage(), leak2);
                if (reportRegistrationLeaks) {
                    StrictMode.onServiceConnectionLeaked(leak2);
                }
                try {
                    ActivityManagerNative.getDefault().unbindService(sd.getIServiceConnection());
                } catch (RemoteException e2) {
                }
                sd.doForget();
            }
        }
        this.mUnboundServices.remove(context);
    }

    public IIntentReceiver getReceiverDispatcher(BroadcastReceiver r, Context context, Handler handler, Instrumentation instrumentation, boolean registered) {
        IIntentReceiver iIntentReceiver;
        synchronized (this.mReceivers) {
            ReceiverDispatcher rd = null;
            ArrayMap<BroadcastReceiver, ReceiverDispatcher> map = null;
            if (registered) {
                map = this.mReceivers.get(context);
                if (map != null) {
                    rd = map.get(r);
                }
            }
            if (rd == null) {
                rd = new ReceiverDispatcher(r, context, handler, instrumentation, registered);
                if (registered) {
                    if (map == null) {
                        map = new ArrayMap<>();
                        this.mReceivers.put(context, map);
                    }
                    map.put(r, rd);
                }
            } else {
                rd.validate(context, handler);
            }
            rd.mForgotten = false;
            iIntentReceiver = rd.getIIntentReceiver();
        }
        return iIntentReceiver;
    }

    public IIntentReceiver forgetReceiverDispatcher(Context context, BroadcastReceiver r) {
        ReceiverDispatcher rd;
        ReceiverDispatcher rd2;
        IIntentReceiver iIntentReceiver;
        synchronized (this.mReceivers) {
            ArrayMap<BroadcastReceiver, ReceiverDispatcher> map = this.mReceivers.get(context);
            if (map != null && (rd2 = map.get(r)) != null) {
                map.remove(r);
                if (map.size() == 0) {
                    this.mReceivers.remove(context);
                }
                if (r.getDebugUnregister()) {
                    ArrayMap<BroadcastReceiver, ReceiverDispatcher> holder = this.mUnregisteredReceivers.get(context);
                    if (holder == null) {
                        holder = new ArrayMap<>();
                        this.mUnregisteredReceivers.put(context, holder);
                    }
                    RuntimeException ex = new IllegalArgumentException("Originally unregistered here:");
                    ex.fillInStackTrace();
                    rd2.setUnregisterLocation(ex);
                    holder.put(r, rd2);
                }
                rd2.mForgotten = true;
                iIntentReceiver = rd2.getIIntentReceiver();
            } else {
                ArrayMap<BroadcastReceiver, ReceiverDispatcher> holder2 = this.mUnregisteredReceivers.get(context);
                if (holder2 != null && (rd = holder2.get(r)) != null) {
                    throw new IllegalArgumentException("Unregistering Receiver " + r + " that was already unregistered", rd.getUnregisterLocation());
                }
                if (context == null) {
                    throw new IllegalStateException("Unbinding Receiver " + r + " from Context that is no longer in use: " + context);
                }
                throw new IllegalArgumentException("Receiver not registered: " + r);
            }
        }
        return iIntentReceiver;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LoadedApk$ReceiverDispatcher.class */
    public static final class ReceiverDispatcher {
        final IIntentReceiver.Stub mIIntentReceiver;
        final BroadcastReceiver mReceiver;
        final Context mContext;
        final Handler mActivityThread;
        final Instrumentation mInstrumentation;
        final boolean mRegistered;
        final IntentReceiverLeaked mLocation;
        RuntimeException mUnregisterLocation;
        boolean mForgotten;

        /* loaded from: LoadedApk$ReceiverDispatcher$InnerReceiver.class */
        static final class InnerReceiver extends IIntentReceiver.Stub {
            final WeakReference<ReceiverDispatcher> mDispatcher;
            final ReceiverDispatcher mStrongRef;

            InnerReceiver(ReceiverDispatcher rd, boolean strong) {
                this.mDispatcher = new WeakReference<>(rd);
                this.mStrongRef = strong ? rd : null;
            }

            @Override // android.content.IIntentReceiver
            public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
                ReceiverDispatcher rd = this.mDispatcher.get();
                if (rd != null) {
                    rd.performReceive(intent, resultCode, data, extras, ordered, sticky, sendingUser);
                    return;
                }
                IActivityManager mgr = ActivityManagerNative.getDefault();
                if (extras != null) {
                    try {
                        extras.setAllowFds(false);
                    } catch (RemoteException e) {
                        Slog.w(ActivityThread.TAG, "Couldn't finish broadcast to unregistered receiver");
                        return;
                    }
                }
                mgr.finishReceiver(this, resultCode, data, extras, false);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: LoadedApk$ReceiverDispatcher$Args.class */
        public final class Args extends BroadcastReceiver.PendingResult implements Runnable {
            private Intent mCurIntent;
            private final boolean mOrdered;

            public Args(Intent intent, int resultCode, String resultData, Bundle resultExtras, boolean ordered, boolean sticky, int sendingUser) {
                super(resultCode, resultData, resultExtras, ReceiverDispatcher.this.mRegistered ? 1 : 2, ordered, sticky, ReceiverDispatcher.this.mIIntentReceiver.asBinder(), sendingUser);
                this.mCurIntent = intent;
                this.mOrdered = ordered;
            }

            @Override // java.lang.Runnable
            public void run() {
                BroadcastReceiver receiver = ReceiverDispatcher.this.mReceiver;
                boolean ordered = this.mOrdered;
                IActivityManager mgr = ActivityManagerNative.getDefault();
                Intent intent = this.mCurIntent;
                this.mCurIntent = null;
                if (receiver == null || ReceiverDispatcher.this.mForgotten) {
                    if (ReceiverDispatcher.this.mRegistered && ordered) {
                        sendFinished(mgr);
                        return;
                    }
                    return;
                }
                Trace.traceBegin(64L, "broadcastReceiveReg");
                try {
                    ClassLoader cl = ReceiverDispatcher.this.mReceiver.getClass().getClassLoader();
                    intent.setExtrasClassLoader(cl);
                    setExtrasClassLoader(cl);
                    receiver.setPendingResult(this);
                    receiver.onReceive(ReceiverDispatcher.this.mContext, intent);
                } catch (Exception e) {
                    if (ReceiverDispatcher.this.mRegistered && ordered) {
                        sendFinished(mgr);
                    }
                    if (ReceiverDispatcher.this.mInstrumentation == null || !ReceiverDispatcher.this.mInstrumentation.onException(ReceiverDispatcher.this.mReceiver, e)) {
                        Trace.traceEnd(64L);
                        throw new RuntimeException("Error receiving broadcast " + intent + " in " + ReceiverDispatcher.this.mReceiver, e);
                    }
                }
                if (receiver.getPendingResult() != null) {
                    finish();
                }
                Trace.traceEnd(64L);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ReceiverDispatcher(BroadcastReceiver receiver, Context context, Handler activityThread, Instrumentation instrumentation, boolean registered) {
            if (activityThread == null) {
                throw new NullPointerException("Handler must not be null");
            }
            this.mIIntentReceiver = new InnerReceiver(this, !registered);
            this.mReceiver = receiver;
            this.mContext = context;
            this.mActivityThread = activityThread;
            this.mInstrumentation = instrumentation;
            this.mRegistered = registered;
            this.mLocation = new IntentReceiverLeaked(null);
            this.mLocation.fillInStackTrace();
        }

        void validate(Context context, Handler activityThread) {
            if (this.mContext != context) {
                throw new IllegalStateException("Receiver " + this.mReceiver + " registered with differing Context (was " + this.mContext + " now " + context + Separators.RPAREN);
            }
            if (this.mActivityThread != activityThread) {
                throw new IllegalStateException("Receiver " + this.mReceiver + " registered with differing handler (was " + this.mActivityThread + " now " + activityThread + Separators.RPAREN);
            }
        }

        IntentReceiverLeaked getLocation() {
            return this.mLocation;
        }

        BroadcastReceiver getIntentReceiver() {
            return this.mReceiver;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public IIntentReceiver getIIntentReceiver() {
            return this.mIIntentReceiver;
        }

        void setUnregisterLocation(RuntimeException ex) {
            this.mUnregisterLocation = ex;
        }

        RuntimeException getUnregisterLocation() {
            return this.mUnregisterLocation;
        }

        public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
            Args args = new Args(intent, resultCode, data, extras, ordered, sticky, sendingUser);
            if (!this.mActivityThread.post(args) && this.mRegistered && ordered) {
                IActivityManager mgr = ActivityManagerNative.getDefault();
                args.sendFinished(mgr);
            }
        }
    }

    public final IServiceConnection getServiceDispatcher(ServiceConnection c, Context context, Handler handler, int flags) {
        IServiceConnection iServiceConnection;
        synchronized (this.mServices) {
            ServiceDispatcher sd = null;
            ArrayMap<ServiceConnection, ServiceDispatcher> map = this.mServices.get(context);
            if (map != null) {
                sd = map.get(c);
            }
            if (sd == null) {
                sd = new ServiceDispatcher(c, context, handler, flags);
                if (map == null) {
                    map = new ArrayMap<>();
                    this.mServices.put(context, map);
                }
                map.put(c, sd);
            } else {
                sd.validate(context, handler);
            }
            iServiceConnection = sd.getIServiceConnection();
        }
        return iServiceConnection;
    }

    public final IServiceConnection forgetServiceDispatcher(Context context, ServiceConnection c) {
        ServiceDispatcher sd;
        ServiceDispatcher sd2;
        IServiceConnection iServiceConnection;
        synchronized (this.mServices) {
            ArrayMap<ServiceConnection, ServiceDispatcher> map = this.mServices.get(context);
            if (map != null && (sd2 = map.get(c)) != null) {
                map.remove(c);
                sd2.doForget();
                if (map.size() == 0) {
                    this.mServices.remove(context);
                }
                if ((sd2.getFlags() & 2) != 0) {
                    ArrayMap<ServiceConnection, ServiceDispatcher> holder = this.mUnboundServices.get(context);
                    if (holder == null) {
                        holder = new ArrayMap<>();
                        this.mUnboundServices.put(context, holder);
                    }
                    RuntimeException ex = new IllegalArgumentException("Originally unbound here:");
                    ex.fillInStackTrace();
                    sd2.setUnbindLocation(ex);
                    holder.put(c, sd2);
                }
                iServiceConnection = sd2.getIServiceConnection();
            } else {
                ArrayMap<ServiceConnection, ServiceDispatcher> holder2 = this.mUnboundServices.get(context);
                if (holder2 != null && (sd = holder2.get(c)) != null) {
                    throw new IllegalArgumentException("Unbinding Service " + c + " that was already unbound", sd.getUnbindLocation());
                }
                if (context == null) {
                    throw new IllegalStateException("Unbinding Service " + c + " from Context that is no longer in use: " + context);
                }
                throw new IllegalArgumentException("Service not registered: " + c);
            }
        }
        return iServiceConnection;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LoadedApk$ServiceDispatcher.class */
    public static final class ServiceDispatcher {
        private final ServiceConnection mConnection;
        private final Context mContext;
        private final Handler mActivityThread;
        private final int mFlags;
        private RuntimeException mUnbindLocation;
        private boolean mDied;
        private boolean mForgotten;
        private final ArrayMap<ComponentName, ConnectionInfo> mActiveConnections = new ArrayMap<>();
        private final InnerConnection mIServiceConnection = new InnerConnection(this);
        private final ServiceConnectionLeaked mLocation = new ServiceConnectionLeaked(null);

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: LoadedApk$ServiceDispatcher$ConnectionInfo.class */
        public static class ConnectionInfo {
            IBinder binder;
            IBinder.DeathRecipient deathMonitor;

            private ConnectionInfo() {
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: LoadedApk$ServiceDispatcher$InnerConnection.class */
        public static class InnerConnection extends IServiceConnection.Stub {
            final WeakReference<ServiceDispatcher> mDispatcher;

            InnerConnection(ServiceDispatcher sd) {
                this.mDispatcher = new WeakReference<>(sd);
            }

            @Override // android.app.IServiceConnection
            public void connected(ComponentName name, IBinder service) throws RemoteException {
                ServiceDispatcher sd = this.mDispatcher.get();
                if (sd != null) {
                    sd.connected(name, service);
                }
            }
        }

        ServiceDispatcher(ServiceConnection conn, Context context, Handler activityThread, int flags) {
            this.mConnection = conn;
            this.mContext = context;
            this.mActivityThread = activityThread;
            this.mLocation.fillInStackTrace();
            this.mFlags = flags;
        }

        void validate(Context context, Handler activityThread) {
            if (this.mContext != context) {
                throw new RuntimeException("ServiceConnection " + this.mConnection + " registered with differing Context (was " + this.mContext + " now " + context + Separators.RPAREN);
            }
            if (this.mActivityThread != activityThread) {
                throw new RuntimeException("ServiceConnection " + this.mConnection + " registered with differing handler (was " + this.mActivityThread + " now " + activityThread + Separators.RPAREN);
            }
        }

        void doForget() {
            synchronized (this) {
                for (int i = 0; i < this.mActiveConnections.size(); i++) {
                    ConnectionInfo ci = this.mActiveConnections.valueAt(i);
                    ci.binder.unlinkToDeath(ci.deathMonitor, 0);
                }
                this.mActiveConnections.clear();
                this.mForgotten = true;
            }
        }

        ServiceConnectionLeaked getLocation() {
            return this.mLocation;
        }

        ServiceConnection getServiceConnection() {
            return this.mConnection;
        }

        IServiceConnection getIServiceConnection() {
            return this.mIServiceConnection;
        }

        int getFlags() {
            return this.mFlags;
        }

        void setUnbindLocation(RuntimeException ex) {
            this.mUnbindLocation = ex;
        }

        RuntimeException getUnbindLocation() {
            return this.mUnbindLocation;
        }

        public void connected(ComponentName name, IBinder service) {
            if (this.mActivityThread != null) {
                this.mActivityThread.post(new RunConnection(name, service, 0));
            } else {
                doConnected(name, service);
            }
        }

        public void death(ComponentName name, IBinder service) {
            synchronized (this) {
                this.mDied = true;
                ConnectionInfo old = this.mActiveConnections.remove(name);
                if (old == null || old.binder != service) {
                    return;
                }
                old.binder.unlinkToDeath(old.deathMonitor, 0);
                if (this.mActivityThread != null) {
                    this.mActivityThread.post(new RunConnection(name, service, 1));
                } else {
                    doDeath(name, service);
                }
            }
        }

        public void doConnected(ComponentName name, IBinder service) {
            synchronized (this) {
                if (this.mForgotten) {
                    return;
                }
                ConnectionInfo old = this.mActiveConnections.get(name);
                if (old == null || old.binder != service) {
                    if (service != null) {
                        this.mDied = false;
                        ConnectionInfo info = new ConnectionInfo();
                        info.binder = service;
                        info.deathMonitor = new DeathMonitor(name, service);
                        try {
                            service.linkToDeath(info.deathMonitor, 0);
                            this.mActiveConnections.put(name, info);
                        } catch (RemoteException e) {
                            this.mActiveConnections.remove(name);
                            return;
                        }
                    } else {
                        this.mActiveConnections.remove(name);
                    }
                    if (old != null) {
                        old.binder.unlinkToDeath(old.deathMonitor, 0);
                    }
                    if (old != null) {
                        this.mConnection.onServiceDisconnected(name);
                    }
                    if (service != null) {
                        this.mConnection.onServiceConnected(name, service);
                    }
                }
            }
        }

        public void doDeath(ComponentName name, IBinder service) {
            this.mConnection.onServiceDisconnected(name);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: LoadedApk$ServiceDispatcher$RunConnection.class */
        public final class RunConnection implements Runnable {
            final ComponentName mName;
            final IBinder mService;
            final int mCommand;

            RunConnection(ComponentName name, IBinder service, int command) {
                this.mName = name;
                this.mService = service;
                this.mCommand = command;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (this.mCommand == 0) {
                    ServiceDispatcher.this.doConnected(this.mName, this.mService);
                } else if (this.mCommand == 1) {
                    ServiceDispatcher.this.doDeath(this.mName, this.mService);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: LoadedApk$ServiceDispatcher$DeathMonitor.class */
        public final class DeathMonitor implements IBinder.DeathRecipient {
            final ComponentName mName;
            final IBinder mService;

            DeathMonitor(ComponentName name, IBinder service) {
                this.mName = name;
                this.mService = service;
            }

            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                ServiceDispatcher.this.death(this.mName, this.mService);
            }
        }
    }
}