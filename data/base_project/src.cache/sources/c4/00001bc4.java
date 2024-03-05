package com.android.server;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.AtomicFile;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.Slog;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RemoteViews;
import com.android.internal.R;
import com.android.internal.appwidget.IAppWidgetHost;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.widget.IRemoteViewsAdapterConnection;
import com.android.internal.widget.IRemoteViewsFactory;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: AppWidgetServiceImpl.class */
public class AppWidgetServiceImpl {
    private static final String KEYGUARD_HOST_PACKAGE = "com.android.keyguard";
    private static final int KEYGUARD_HOST_ID = 1262836039;
    private static final String TAG = "AppWidgetServiceImpl";
    private static final String SETTINGS_FILENAME = "appwidgets.xml";
    private static final int MIN_UPDATE_PERIOD = 1800000;
    private static final int CURRENT_VERSION = 1;
    private static boolean DBG = false;
    final Context mContext;
    final AlarmManager mAlarmManager;
    final int mUserId;
    final boolean mHasFeature;
    Locale mLocale;
    boolean mSafeMode;
    boolean mStateLoaded;
    int mMaxWidgetBitmapMemory;
    private final Handler mSaveStateHandler;
    private final HashMap<Pair<Integer, Intent.FilterComparison>, ServiceConnection> mBoundRemoteViewsServices = new HashMap<>();
    private final HashMap<Intent.FilterComparison, HashSet<Integer>> mRemoteViewsServicesAppWidgets = new HashMap<>();
    final ArrayList<Provider> mInstalledProviders = new ArrayList<>();
    int mNextAppWidgetId = 1;
    final ArrayList<AppWidgetId> mAppWidgetIds = new ArrayList<>();
    final ArrayList<Host> mHosts = new ArrayList<>();
    final HashSet<String> mPackagesWithBindWidgetPermission = new HashSet<>();
    ArrayList<Provider> mDeletedProviders = new ArrayList<>();
    ArrayList<Host> mDeletedHosts = new ArrayList<>();
    private final Runnable mSaveStateRunnable = new Runnable() { // from class: com.android.server.AppWidgetServiceImpl.2
        @Override // java.lang.Runnable
        public void run() {
            synchronized (AppWidgetServiceImpl.this.mAppWidgetIds) {
                AppWidgetServiceImpl.this.ensureStateLoadedLocked();
                AppWidgetServiceImpl.this.saveStateLocked();
            }
        }
    };
    final IPackageManager mPm = AppGlobals.getPackageManager();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AppWidgetServiceImpl$Provider.class */
    public static class Provider {
        int uid;
        AppWidgetProviderInfo info;
        ArrayList<AppWidgetId> instances = new ArrayList<>();
        PendingIntent broadcast;
        boolean zombie;
        int tag;

        Provider() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AppWidgetServiceImpl$Host.class */
    public static class Host {
        int uid;
        int hostId;
        String packageName;
        ArrayList<AppWidgetId> instances = new ArrayList<>();
        IAppWidgetHost callbacks;
        boolean zombie;
        int tag;

        Host() {
        }

        boolean uidMatches(int callingUid) {
            if (UserHandle.getAppId(callingUid) == Process.myUid()) {
                return UserHandle.isSameApp(this.uid, callingUid);
            }
            return this.uid == callingUid;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AppWidgetServiceImpl$AppWidgetId.class */
    public static class AppWidgetId {
        int appWidgetId;
        Provider provider;
        RemoteViews views;
        Bundle options;
        Host host;

        AppWidgetId() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AppWidgetServiceImpl$ServiceConnectionProxy.class */
    public static class ServiceConnectionProxy implements ServiceConnection {
        private final IBinder mConnectionCb;

        ServiceConnectionProxy(Pair<Integer, Intent.FilterComparison> key, IBinder connectionCb) {
            this.mConnectionCb = connectionCb;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            IRemoteViewsAdapterConnection cb = IRemoteViewsAdapterConnection.Stub.asInterface(this.mConnectionCb);
            try {
                cb.onServiceConnected(service);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            disconnect();
        }

        public void disconnect() {
            IRemoteViewsAdapterConnection cb = IRemoteViewsAdapterConnection.Stub.asInterface(this.mConnectionCb);
            try {
                cb.onServiceDisconnected();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppWidgetServiceImpl(Context context, int userId, Handler saveStateHandler) {
        this.mContext = context;
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mUserId = userId;
        this.mSaveStateHandler = saveStateHandler;
        this.mHasFeature = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_APP_WIDGETS);
        computeMaximumWidgetBitmapMemory();
    }

    void computeMaximumWidgetBitmapMemory() {
        WindowManager wm = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        this.mMaxWidgetBitmapMemory = 6 * size.x * size.y;
    }

    public void systemReady(boolean safeMode) {
        this.mSafeMode = safeMode;
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
        }
    }

    private void log(String msg) {
        Slog.i(TAG, "u=" + this.mUserId + ": " + msg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onConfigurationChanged() {
        if (DBG) {
            log("Got onConfigurationChanged()");
        }
        Locale revised = Locale.getDefault();
        if (revised == null || this.mLocale == null || !revised.equals(this.mLocale)) {
            this.mLocale = revised;
            synchronized (this.mAppWidgetIds) {
                ensureStateLoadedLocked();
                ArrayList<Provider> installedProviders = new ArrayList<>(this.mInstalledProviders);
                HashSet<ComponentName> removedProviders = new HashSet<>();
                int N = installedProviders.size();
                for (int i = N - 1; i >= 0; i--) {
                    Provider p = installedProviders.get(i);
                    ComponentName cn = p.info.provider;
                    if (!removedProviders.contains(cn)) {
                        updateProvidersForPackageLocked(cn.getPackageName(), removedProviders);
                    }
                }
                saveStateAsync();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBroadcastReceived(Intent intent) {
        String pkgName;
        String[] pkgList;
        boolean added;
        if (DBG) {
            log("onBroadcast " + intent);
        }
        String action = intent.getAction();
        boolean changed = false;
        boolean providersModified = false;
        if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(action)) {
            pkgList = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
            added = true;
        } else if ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
            pkgList = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
            added = false;
        } else {
            Uri uri = intent.getData();
            if (uri == null || (pkgName = uri.getSchemeSpecificPart()) == null) {
                return;
            }
            pkgList = new String[]{pkgName};
            added = Intent.ACTION_PACKAGE_ADDED.equals(action);
            changed = Intent.ACTION_PACKAGE_CHANGED.equals(action);
        }
        if (pkgList == null || pkgList.length == 0) {
            return;
        }
        if (added || changed) {
            synchronized (this.mAppWidgetIds) {
                ensureStateLoadedLocked();
                Bundle extras = intent.getExtras();
                if (changed || (extras != null && extras.getBoolean(Intent.EXTRA_REPLACING, false))) {
                    for (String pkgName2 : pkgList) {
                        providersModified |= updateProvidersForPackageLocked(pkgName2, null);
                    }
                } else {
                    for (String pkgName3 : pkgList) {
                        providersModified |= addProvidersForPackageLocked(pkgName3);
                    }
                }
                saveStateAsync();
            }
        } else {
            Bundle extras2 = intent.getExtras();
            if (extras2 == null || !extras2.getBoolean(Intent.EXTRA_REPLACING, false)) {
                synchronized (this.mAppWidgetIds) {
                    ensureStateLoadedLocked();
                    String[] arr$ = pkgList;
                    for (String pkgName4 : arr$) {
                        providersModified |= removeProvidersForPackageLocked(pkgName4);
                        saveStateAsync();
                    }
                }
            }
        }
        if (providersModified) {
            synchronized (this.mAppWidgetIds) {
                ensureStateLoadedLocked();
                notifyHostsForProvidersChangedLocked();
            }
        }
    }

    private void dumpProvider(Provider p, int index, PrintWriter pw) {
        AppWidgetProviderInfo info = p.info;
        pw.print("  [");
        pw.print(index);
        pw.print("] provider ");
        pw.print(info.provider.flattenToShortString());
        pw.println(':');
        pw.print("    min=(");
        pw.print(info.minWidth);
        pw.print("x");
        pw.print(info.minHeight);
        pw.print(")   minResize=(");
        pw.print(info.minResizeWidth);
        pw.print("x");
        pw.print(info.minResizeHeight);
        pw.print(") updatePeriodMillis=");
        pw.print(info.updatePeriodMillis);
        pw.print(" resizeMode=");
        pw.print(info.resizeMode);
        pw.print(info.widgetCategory);
        pw.print(" autoAdvanceViewId=");
        pw.print(info.autoAdvanceViewId);
        pw.print(" initialLayout=#");
        pw.print(Integer.toHexString(info.initialLayout));
        pw.print(" uid=");
        pw.print(p.uid);
        pw.print(" zombie=");
        pw.println(p.zombie);
    }

    private void dumpHost(Host host, int index, PrintWriter pw) {
        pw.print("  [");
        pw.print(index);
        pw.print("] hostId=");
        pw.print(host.hostId);
        pw.print(' ');
        pw.print(host.packageName);
        pw.print('/');
        pw.print(host.uid);
        pw.println(':');
        pw.print("    callbacks=");
        pw.println(host.callbacks);
        pw.print("    instances.size=");
        pw.print(host.instances.size());
        pw.print(" zombie=");
        pw.println(host.zombie);
    }

    private void dumpAppWidgetId(AppWidgetId id, int index, PrintWriter pw) {
        pw.print("  [");
        pw.print(index);
        pw.print("] id=");
        pw.println(id.appWidgetId);
        pw.print("    hostId=");
        pw.print(id.host.hostId);
        pw.print(' ');
        pw.print(id.host.packageName);
        pw.print('/');
        pw.println(id.host.uid);
        if (id.provider != null) {
            pw.print("    provider=");
            pw.println(id.provider.info.provider.flattenToShortString());
        }
        if (id.host != null) {
            pw.print("    host.callbacks=");
            pw.println(id.host.callbacks);
        }
        if (id.views != null) {
            pw.print("    views=");
            pw.println(id.views);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mAppWidgetIds) {
            int N = this.mInstalledProviders.size();
            pw.println("Providers:");
            for (int i = 0; i < N; i++) {
                dumpProvider(this.mInstalledProviders.get(i), i, pw);
            }
            int N2 = this.mAppWidgetIds.size();
            pw.println(Separators.SP);
            pw.println("AppWidgetIds:");
            for (int i2 = 0; i2 < N2; i2++) {
                dumpAppWidgetId(this.mAppWidgetIds.get(i2), i2, pw);
            }
            int N3 = this.mHosts.size();
            pw.println(Separators.SP);
            pw.println("Hosts:");
            for (int i3 = 0; i3 < N3; i3++) {
                dumpHost(this.mHosts.get(i3), i3, pw);
            }
            int N4 = this.mDeletedProviders.size();
            pw.println(Separators.SP);
            pw.println("Deleted Providers:");
            for (int i4 = 0; i4 < N4; i4++) {
                dumpProvider(this.mDeletedProviders.get(i4), i4, pw);
            }
            int N5 = this.mDeletedHosts.size();
            pw.println(Separators.SP);
            pw.println("Deleted Hosts:");
            for (int i5 = 0; i5 < N5; i5++) {
                dumpHost(this.mDeletedHosts.get(i5), i5, pw);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureStateLoadedLocked() {
        if (this.mStateLoaded || !this.mHasFeature) {
            return;
        }
        loadAppWidgetListLocked();
        loadStateLocked();
        this.mStateLoaded = true;
    }

    public int allocateAppWidgetId(String packageName, int hostId) {
        int callingUid = enforceSystemOrCallingUid(packageName);
        synchronized (this.mAppWidgetIds) {
            if (!this.mHasFeature) {
                return -1;
            }
            ensureStateLoadedLocked();
            int appWidgetId = this.mNextAppWidgetId;
            this.mNextAppWidgetId = appWidgetId + 1;
            Host host = lookupOrAddHostLocked(callingUid, packageName, hostId);
            AppWidgetId id = new AppWidgetId();
            id.appWidgetId = appWidgetId;
            id.host = host;
            host.instances.add(id);
            this.mAppWidgetIds.add(id);
            saveStateAsync();
            if (DBG) {
                log("Allocating AppWidgetId for " + packageName + " host=" + hostId + " id=" + appWidgetId);
            }
            return appWidgetId;
        }
    }

    public void deleteAppWidgetId(int appWidgetId) {
        synchronized (this.mAppWidgetIds) {
            if (this.mHasFeature) {
                ensureStateLoadedLocked();
                AppWidgetId id = lookupAppWidgetIdLocked(appWidgetId);
                if (id != null) {
                    deleteAppWidgetLocked(id);
                    saveStateAsync();
                }
            }
        }
    }

    public void deleteHost(int hostId) {
        synchronized (this.mAppWidgetIds) {
            if (this.mHasFeature) {
                ensureStateLoadedLocked();
                int callingUid = Binder.getCallingUid();
                Host host = lookupHostLocked(callingUid, hostId);
                if (host != null) {
                    deleteHostLocked(host);
                    saveStateAsync();
                }
            }
        }
    }

    public void deleteAllHosts() {
        synchronized (this.mAppWidgetIds) {
            if (this.mHasFeature) {
                ensureStateLoadedLocked();
                int callingUid = Binder.getCallingUid();
                int N = this.mHosts.size();
                boolean changed = false;
                for (int i = N - 1; i >= 0; i--) {
                    Host host = this.mHosts.get(i);
                    if (host.uidMatches(callingUid)) {
                        deleteHostLocked(host);
                        changed = true;
                    }
                }
                if (changed) {
                    saveStateAsync();
                }
            }
        }
    }

    void deleteHostLocked(Host host) {
        int N = host.instances.size();
        for (int i = N - 1; i >= 0; i--) {
            AppWidgetId id = host.instances.get(i);
            deleteAppWidgetLocked(id);
        }
        host.instances.clear();
        this.mHosts.remove(host);
        this.mDeletedHosts.add(host);
        host.callbacks = null;
    }

    void deleteAppWidgetLocked(AppWidgetId id) {
        unbindAppWidgetRemoteViewsServicesLocked(id);
        Host host = id.host;
        host.instances.remove(id);
        pruneHostLocked(host);
        this.mAppWidgetIds.remove(id);
        Provider p = id.provider;
        if (p != null) {
            p.instances.remove(id);
            if (!p.zombie) {
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_DELETED);
                intent.setComponent(p.info.provider);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id.appWidgetId);
                this.mContext.sendBroadcastAsUser(intent, new UserHandle(this.mUserId));
                if (p.instances.size() == 0) {
                    cancelBroadcasts(p);
                    Intent intent2 = new Intent(AppWidgetManager.ACTION_APPWIDGET_DISABLED);
                    intent2.setComponent(p.info.provider);
                    this.mContext.sendBroadcastAsUser(intent2, new UserHandle(this.mUserId));
                }
            }
        }
    }

    void cancelBroadcasts(Provider p) {
        if (DBG) {
            log("cancelBroadcasts for " + p);
        }
        if (p.broadcast != null) {
            this.mAlarmManager.cancel(p.broadcast);
            long token = Binder.clearCallingIdentity();
            try {
                p.broadcast.cancel();
                Binder.restoreCallingIdentity(token);
                p.broadcast = null;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }
    }

    private void bindAppWidgetIdImpl(int appWidgetId, ComponentName provider, Bundle options) {
        if (DBG) {
            log("bindAppWidgetIdImpl appwid=" + appWidgetId + " provider=" + provider);
        }
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mAppWidgetIds) {
                if (this.mHasFeature) {
                    Bundle options2 = cloneIfLocalBinder(options);
                    ensureStateLoadedLocked();
                    AppWidgetId id = lookupAppWidgetIdLocked(appWidgetId);
                    if (id == null) {
                        throw new IllegalArgumentException("bad appWidgetId");
                    }
                    if (id.provider != null) {
                        throw new IllegalArgumentException("appWidgetId " + appWidgetId + " already bound to " + id.provider.info.provider);
                    }
                    Provider p = lookupProviderLocked(provider);
                    if (p == null) {
                        throw new IllegalArgumentException("not a appwidget provider: " + provider);
                    }
                    if (p.zombie) {
                        throw new IllegalArgumentException("can't bind to a 3rd party provider in safe mode: " + provider);
                    }
                    id.provider = p;
                    if (options2 == null) {
                        options2 = new Bundle();
                    }
                    id.options = options2;
                    if (!options2.containsKey(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY)) {
                        options2.putInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, 1);
                    }
                    p.instances.add(id);
                    int instancesSize = p.instances.size();
                    if (instancesSize == 1) {
                        sendEnableIntentLocked(p);
                    }
                    sendUpdateIntentLocked(p, new int[]{appWidgetId});
                    registerForBroadcastsLocked(p, getAppWidgetIds(p));
                    saveStateAsync();
                    Binder.restoreCallingIdentity(ident);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    public void bindAppWidgetId(int appWidgetId, ComponentName provider, Bundle options) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BIND_APPWIDGET, "bindAppWidgetId appWidgetId=" + appWidgetId + " provider=" + provider);
        bindAppWidgetIdImpl(appWidgetId, provider, options);
    }

    public boolean bindAppWidgetIdIfAllowed(String packageName, int appWidgetId, ComponentName provider, Bundle options) {
        if (!this.mHasFeature) {
            return false;
        }
        try {
            this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BIND_APPWIDGET, null);
        } catch (SecurityException e) {
            if (!callerHasBindAppWidgetPermission(packageName)) {
                return false;
            }
        }
        bindAppWidgetIdImpl(appWidgetId, provider, options);
        return true;
    }

    private boolean callerHasBindAppWidgetPermission(String packageName) {
        boolean contains;
        int callingUid = Binder.getCallingUid();
        try {
            if (!UserHandle.isSameApp(callingUid, getUidForPackage(packageName))) {
                return false;
            }
            synchronized (this.mAppWidgetIds) {
                ensureStateLoadedLocked();
                contains = this.mPackagesWithBindWidgetPermission.contains(packageName);
            }
            return contains;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasBindAppWidgetPermission(String packageName) {
        boolean contains;
        if (!this.mHasFeature) {
            return false;
        }
        this.mContext.enforceCallingPermission(Manifest.permission.MODIFY_APPWIDGET_BIND_PERMISSIONS, "hasBindAppWidgetPermission packageName=" + packageName);
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
            contains = this.mPackagesWithBindWidgetPermission.contains(packageName);
        }
        return contains;
    }

    public void setBindAppWidgetPermission(String packageName, boolean permission) {
        if (!this.mHasFeature) {
            return;
        }
        this.mContext.enforceCallingPermission(Manifest.permission.MODIFY_APPWIDGET_BIND_PERMISSIONS, "setBindAppWidgetPermission packageName=" + packageName);
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
            if (permission) {
                this.mPackagesWithBindWidgetPermission.add(packageName);
            } else {
                this.mPackagesWithBindWidgetPermission.remove(packageName);
            }
            saveStateAsync();
        }
    }

    public void bindRemoteViewsService(int appWidgetId, Intent intent, IBinder connection) {
        synchronized (this.mAppWidgetIds) {
            if (this.mHasFeature) {
                ensureStateLoadedLocked();
                AppWidgetId id = lookupAppWidgetIdLocked(appWidgetId);
                if (id == null) {
                    throw new IllegalArgumentException("bad appWidgetId");
                }
                ComponentName componentName = intent.getComponent();
                try {
                    ServiceInfo si = AppGlobals.getPackageManager().getServiceInfo(componentName, 4096, this.mUserId);
                    if (!Manifest.permission.BIND_REMOTEVIEWS.equals(si.permission)) {
                        throw new SecurityException("Selected service does not require android.permission.BIND_REMOTEVIEWS: " + componentName);
                    }
                    Intent.FilterComparison fc = new Intent.FilterComparison(intent);
                    Pair<Integer, Intent.FilterComparison> key = Pair.create(Integer.valueOf(appWidgetId), fc);
                    if (this.mBoundRemoteViewsServices.containsKey(key)) {
                        ServiceConnectionProxy conn = (ServiceConnectionProxy) this.mBoundRemoteViewsServices.get(key);
                        conn.disconnect();
                        this.mContext.unbindService(conn);
                        this.mBoundRemoteViewsServices.remove(key);
                    }
                    int userId = UserHandle.getUserId(id.provider.uid);
                    if (userId != this.mUserId) {
                        Slog.w(TAG, "AppWidgetServiceImpl of user " + this.mUserId + " binding to provider on user " + userId);
                    }
                    long token = Binder.clearCallingIdentity();
                    ServiceConnectionProxy conn2 = new ServiceConnectionProxy(key, connection);
                    this.mContext.bindServiceAsUser(intent, conn2, 1, new UserHandle(userId));
                    this.mBoundRemoteViewsServices.put(key, conn2);
                    Binder.restoreCallingIdentity(token);
                    incrementAppWidgetServiceRefCount(appWidgetId, fc);
                } catch (RemoteException e) {
                    throw new IllegalArgumentException("Unknown component " + componentName);
                }
            }
        }
    }

    public void unbindRemoteViewsService(int appWidgetId, Intent intent) {
        synchronized (this.mAppWidgetIds) {
            if (this.mHasFeature) {
                ensureStateLoadedLocked();
                Pair<Integer, Intent.FilterComparison> key = Pair.create(Integer.valueOf(appWidgetId), new Intent.FilterComparison(intent));
                if (this.mBoundRemoteViewsServices.containsKey(key)) {
                    AppWidgetId id = lookupAppWidgetIdLocked(appWidgetId);
                    if (id == null) {
                        throw new IllegalArgumentException("bad appWidgetId");
                    }
                    ServiceConnectionProxy conn = (ServiceConnectionProxy) this.mBoundRemoteViewsServices.get(key);
                    conn.disconnect();
                    this.mContext.unbindService(conn);
                    this.mBoundRemoteViewsServices.remove(key);
                }
            }
        }
    }

    private void unbindAppWidgetRemoteViewsServicesLocked(AppWidgetId id) {
        int appWidgetId = id.appWidgetId;
        Iterator<Pair<Integer, Intent.FilterComparison>> it = this.mBoundRemoteViewsServices.keySet().iterator();
        while (it.hasNext()) {
            Pair<Integer, Intent.FilterComparison> key = it.next();
            if (key.first.intValue() == appWidgetId) {
                ServiceConnectionProxy conn = (ServiceConnectionProxy) this.mBoundRemoteViewsServices.get(key);
                conn.disconnect();
                this.mContext.unbindService(conn);
                it.remove();
            }
        }
        decrementAppWidgetServiceRefCount(id);
    }

    private void destroyRemoteViewsService(final Intent intent, AppWidgetId id) {
        ServiceConnection conn = new ServiceConnection() { // from class: com.android.server.AppWidgetServiceImpl.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName name, IBinder service) {
                IRemoteViewsFactory cb = IRemoteViewsFactory.Stub.asInterface(service);
                try {
                    cb.onDestroy(intent);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e2) {
                    e2.printStackTrace();
                }
                AppWidgetServiceImpl.this.mContext.unbindService(this);
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        int userId = UserHandle.getUserId(id.provider.uid);
        long token = Binder.clearCallingIdentity();
        try {
            this.mContext.bindServiceAsUser(intent, conn, 1, new UserHandle(userId));
            Binder.restoreCallingIdentity(token);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
    }

    private void incrementAppWidgetServiceRefCount(int appWidgetId, Intent.FilterComparison fc) {
        HashSet<Integer> appWidgetIds;
        if (this.mRemoteViewsServicesAppWidgets.containsKey(fc)) {
            appWidgetIds = this.mRemoteViewsServicesAppWidgets.get(fc);
        } else {
            appWidgetIds = new HashSet<>();
            this.mRemoteViewsServicesAppWidgets.put(fc, appWidgetIds);
        }
        appWidgetIds.add(Integer.valueOf(appWidgetId));
    }

    private void decrementAppWidgetServiceRefCount(AppWidgetId id) {
        Iterator<Intent.FilterComparison> it = this.mRemoteViewsServicesAppWidgets.keySet().iterator();
        while (it.hasNext()) {
            Intent.FilterComparison key = it.next();
            HashSet<Integer> ids = this.mRemoteViewsServicesAppWidgets.get(key);
            if (ids.remove(Integer.valueOf(id.appWidgetId)) && ids.isEmpty()) {
                destroyRemoteViewsService(key.getIntent(), id);
                it.remove();
            }
        }
    }

    public AppWidgetProviderInfo getAppWidgetInfo(int appWidgetId) {
        synchronized (this.mAppWidgetIds) {
            if (!this.mHasFeature) {
                return null;
            }
            ensureStateLoadedLocked();
            AppWidgetId id = lookupAppWidgetIdLocked(appWidgetId);
            if (id != null && id.provider != null && !id.provider.zombie) {
                return cloneIfLocalBinder(id.provider.info);
            }
            return null;
        }
    }

    public RemoteViews getAppWidgetViews(int appWidgetId) {
        if (DBG) {
            log("getAppWidgetViews id=" + appWidgetId);
        }
        synchronized (this.mAppWidgetIds) {
            if (!this.mHasFeature) {
                return null;
            }
            ensureStateLoadedLocked();
            AppWidgetId id = lookupAppWidgetIdLocked(appWidgetId);
            if (id != null) {
                return cloneIfLocalBinder(id.views);
            }
            if (DBG) {
                log("   couldn't find appwidgetid");
            }
            return null;
        }
    }

    public List<AppWidgetProviderInfo> getInstalledProviders(int categoryFilter) {
        synchronized (this.mAppWidgetIds) {
            if (!this.mHasFeature) {
                return new ArrayList(0);
            }
            ensureStateLoadedLocked();
            int N = this.mInstalledProviders.size();
            ArrayList<AppWidgetProviderInfo> result = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                Provider p = this.mInstalledProviders.get(i);
                if (!p.zombie && (p.info.widgetCategory & categoryFilter) != 0) {
                    result.add(cloneIfLocalBinder(p.info));
                }
            }
            return result;
        }
    }

    public void updateAppWidgetIds(int[] appWidgetIds, RemoteViews views) {
        if (!this.mHasFeature || appWidgetIds == null) {
            return;
        }
        if (DBG) {
            log("updateAppWidgetIds views: " + views);
        }
        int bitmapMemoryUsage = 0;
        if (views != null) {
            bitmapMemoryUsage = views.estimateMemoryUsage();
        }
        if (bitmapMemoryUsage > this.mMaxWidgetBitmapMemory) {
            throw new IllegalArgumentException("RemoteViews for widget update exceeds maximum bitmap memory usage (used: " + bitmapMemoryUsage + ", max: " + this.mMaxWidgetBitmapMemory + ") The total memory cannot exceed that required to fill the device's screen once.");
        }
        if (appWidgetIds.length == 0) {
            return;
        }
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
            for (int i : appWidgetIds) {
                AppWidgetId id = lookupAppWidgetIdLocked(i);
                updateAppWidgetInstanceLocked(id, views);
            }
        }
    }

    private void saveStateAsync() {
        this.mSaveStateHandler.post(this.mSaveStateRunnable);
    }

    public void updateAppWidgetOptions(int appWidgetId, Bundle options) {
        synchronized (this.mAppWidgetIds) {
            if (this.mHasFeature) {
                Bundle options2 = cloneIfLocalBinder(options);
                ensureStateLoadedLocked();
                AppWidgetId id = lookupAppWidgetIdLocked(appWidgetId);
                if (id == null) {
                    return;
                }
                Provider p = id.provider;
                id.options.putAll(options2);
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED);
                intent.setComponent(p.info.provider);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id.appWidgetId);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS, id.options);
                this.mContext.sendBroadcastAsUser(intent, new UserHandle(this.mUserId));
                saveStateAsync();
            }
        }
    }

    public Bundle getAppWidgetOptions(int appWidgetId) {
        synchronized (this.mAppWidgetIds) {
            if (!this.mHasFeature) {
                return Bundle.EMPTY;
            }
            ensureStateLoadedLocked();
            AppWidgetId id = lookupAppWidgetIdLocked(appWidgetId);
            if (id != null && id.options != null) {
                return cloneIfLocalBinder(id.options);
            }
            return Bundle.EMPTY;
        }
    }

    public void partiallyUpdateAppWidgetIds(int[] appWidgetIds, RemoteViews views) {
        if (!this.mHasFeature || appWidgetIds == null || appWidgetIds.length == 0) {
            return;
        }
        int N = appWidgetIds.length;
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
            for (int i = 0; i < N; i++) {
                AppWidgetId id = lookupAppWidgetIdLocked(appWidgetIds[i]);
                if (id == null) {
                    Slog.w(TAG, "widget id " + appWidgetIds[i] + " not found!");
                } else if (id.views != null) {
                    updateAppWidgetInstanceLocked(id, views, true);
                }
            }
        }
    }

    public void notifyAppWidgetViewDataChanged(int[] appWidgetIds, int viewId) {
        if (!this.mHasFeature || appWidgetIds == null || appWidgetIds.length == 0) {
            return;
        }
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
            for (int i : appWidgetIds) {
                AppWidgetId id = lookupAppWidgetIdLocked(i);
                notifyAppWidgetViewDataChangedInstanceLocked(id, viewId);
            }
        }
    }

    public void updateAppWidgetProvider(ComponentName provider, RemoteViews views) {
        if (!this.mHasFeature) {
            return;
        }
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
            Provider p = lookupProviderLocked(provider);
            if (p == null) {
                Slog.w(TAG, "updateAppWidgetProvider: provider doesn't exist: " + provider);
                return;
            }
            ArrayList<AppWidgetId> instances = p.instances;
            int callingUid = Binder.getCallingUid();
            int N = instances.size();
            for (int i = 0; i < N; i++) {
                AppWidgetId id = instances.get(i);
                if (canAccessAppWidgetId(id, callingUid)) {
                    updateAppWidgetInstanceLocked(id, views);
                }
            }
        }
    }

    void updateAppWidgetInstanceLocked(AppWidgetId id, RemoteViews views) {
        updateAppWidgetInstanceLocked(id, views, false);
    }

    void updateAppWidgetInstanceLocked(AppWidgetId id, RemoteViews views, boolean isPartialUpdate) {
        if (id != null && id.provider != null && !id.provider.zombie && !id.host.zombie) {
            if (!isPartialUpdate) {
                id.views = views;
            } else {
                id.views.mergeRemoteViews(views);
            }
            if (id.host.callbacks != null) {
                try {
                    id.host.callbacks.updateAppWidget(id.appWidgetId, views, this.mUserId);
                } catch (RemoteException e) {
                    id.host.callbacks = null;
                }
            }
        }
    }

    void notifyAppWidgetViewDataChangedInstanceLocked(AppWidgetId id, int viewId) {
        if (id != null && id.provider != null && !id.provider.zombie && !id.host.zombie) {
            if (id.host.callbacks != null) {
                try {
                    id.host.callbacks.viewDataChanged(id.appWidgetId, viewId, this.mUserId);
                } catch (RemoteException e) {
                    id.host.callbacks = null;
                }
            }
            if (id.host.callbacks == null) {
                Set<Intent.FilterComparison> keys = this.mRemoteViewsServicesAppWidgets.keySet();
                for (Intent.FilterComparison key : keys) {
                    if (this.mRemoteViewsServicesAppWidgets.get(key).contains(Integer.valueOf(id.appWidgetId))) {
                        Intent intent = key.getIntent();
                        ServiceConnection conn = new ServiceConnection() { // from class: com.android.server.AppWidgetServiceImpl.3
                            @Override // android.content.ServiceConnection
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                IRemoteViewsFactory cb = IRemoteViewsFactory.Stub.asInterface(service);
                                try {
                                    cb.onDataSetChangedAsync();
                                } catch (RemoteException e2) {
                                    e2.printStackTrace();
                                } catch (RuntimeException e3) {
                                    e3.printStackTrace();
                                }
                                AppWidgetServiceImpl.this.mContext.unbindService(this);
                            }

                            @Override // android.content.ServiceConnection
                            public void onServiceDisconnected(ComponentName name) {
                            }
                        };
                        int userId = UserHandle.getUserId(id.provider.uid);
                        long token = Binder.clearCallingIdentity();
                        try {
                            this.mContext.bindServiceAsUser(intent, conn, 1, new UserHandle(userId));
                            Binder.restoreCallingIdentity(token);
                        } catch (Throwable th) {
                            Binder.restoreCallingIdentity(token);
                            throw th;
                        }
                    }
                }
            }
        }
    }

    private boolean isLocalBinder() {
        return Process.myPid() == Binder.getCallingPid();
    }

    private RemoteViews cloneIfLocalBinder(RemoteViews rv) {
        if (isLocalBinder() && rv != null) {
            return rv.m1045clone();
        }
        return rv;
    }

    private AppWidgetProviderInfo cloneIfLocalBinder(AppWidgetProviderInfo info) {
        if (isLocalBinder() && info != null) {
            return info.m75clone();
        }
        return info;
    }

    private Bundle cloneIfLocalBinder(Bundle bundle) {
        if (isLocalBinder() && bundle != null) {
            return (Bundle) bundle.clone();
        }
        return bundle;
    }

    public int[] startListening(IAppWidgetHost callbacks, String packageName, int hostId, List<RemoteViews> updatedViews) {
        int[] updatedIds;
        if (!this.mHasFeature) {
            return new int[0];
        }
        int callingUid = enforceCallingUid(packageName);
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
            Host host = lookupOrAddHostLocked(callingUid, packageName, hostId);
            host.callbacks = callbacks;
            updatedViews.clear();
            ArrayList<AppWidgetId> instances = host.instances;
            int N = instances.size();
            updatedIds = new int[N];
            for (int i = 0; i < N; i++) {
                AppWidgetId id = instances.get(i);
                updatedIds[i] = id.appWidgetId;
                updatedViews.add(cloneIfLocalBinder(id.views));
            }
        }
        return updatedIds;
    }

    public void stopListening(int hostId) {
        synchronized (this.mAppWidgetIds) {
            if (this.mHasFeature) {
                ensureStateLoadedLocked();
                Host host = lookupHostLocked(Binder.getCallingUid(), hostId);
                if (host != null) {
                    host.callbacks = null;
                    pruneHostLocked(host);
                }
            }
        }
    }

    boolean canAccessAppWidgetId(AppWidgetId id, int callingUid) {
        if (id.host.uidMatches(callingUid)) {
            return true;
        }
        if ((id.provider != null && id.provider.uid == callingUid) || this.mContext.checkCallingOrSelfPermission(Manifest.permission.BIND_APPWIDGET) == 0) {
            return true;
        }
        return false;
    }

    AppWidgetId lookupAppWidgetIdLocked(int appWidgetId) {
        int callingUid = Binder.getCallingUid();
        int N = this.mAppWidgetIds.size();
        for (int i = 0; i < N; i++) {
            AppWidgetId id = this.mAppWidgetIds.get(i);
            if (id.appWidgetId == appWidgetId && canAccessAppWidgetId(id, callingUid)) {
                return id;
            }
        }
        return null;
    }

    Provider lookupProviderLocked(ComponentName provider) {
        int N = this.mInstalledProviders.size();
        for (int i = 0; i < N; i++) {
            Provider p = this.mInstalledProviders.get(i);
            if (p.info.provider.equals(provider)) {
                return p;
            }
        }
        return null;
    }

    Host lookupHostLocked(int uid, int hostId) {
        int N = this.mHosts.size();
        for (int i = 0; i < N; i++) {
            Host h = this.mHosts.get(i);
            if (h.uidMatches(uid) && h.hostId == hostId) {
                return h;
            }
        }
        return null;
    }

    Host lookupOrAddHostLocked(int uid, String packageName, int hostId) {
        int N = this.mHosts.size();
        for (int i = 0; i < N; i++) {
            Host h = this.mHosts.get(i);
            if (h.hostId == hostId && h.packageName.equals(packageName)) {
                return h;
            }
        }
        Host host = new Host();
        host.packageName = packageName;
        host.uid = uid;
        host.hostId = hostId;
        this.mHosts.add(host);
        return host;
    }

    void pruneHostLocked(Host host) {
        if (host.instances.size() == 0 && host.callbacks == null) {
            this.mHosts.remove(host);
        }
    }

    void loadAppWidgetListLocked() {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        try {
            List<ResolveInfo> broadcastReceivers = this.mPm.queryIntentReceivers(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 128, this.mUserId);
            int N = broadcastReceivers == null ? 0 : broadcastReceivers.size();
            for (int i = 0; i < N; i++) {
                ResolveInfo ri = broadcastReceivers.get(i);
                addProviderLocked(ri);
            }
        } catch (RemoteException e) {
        }
    }

    boolean addProviderLocked(ResolveInfo ri) {
        Provider p;
        if ((ri.activityInfo.applicationInfo.flags & 262144) == 0 && ri.activityInfo.isEnabled() && (p = parseProviderInfoXml(new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name), ri)) != null) {
            this.mInstalledProviders.add(p);
            return true;
        }
        return false;
    }

    void removeProviderLocked(int index, Provider p) {
        int N = p.instances.size();
        for (int i = 0; i < N; i++) {
            AppWidgetId id = p.instances.get(i);
            updateAppWidgetInstanceLocked(id, null);
            cancelBroadcasts(p);
            id.host.instances.remove(id);
            this.mAppWidgetIds.remove(id);
            id.provider = null;
            pruneHostLocked(id.host);
            id.host = null;
        }
        p.instances.clear();
        this.mInstalledProviders.remove(index);
        this.mDeletedProviders.add(p);
        cancelBroadcasts(p);
    }

    void sendEnableIntentLocked(Provider p) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_ENABLED);
        intent.setComponent(p.info.provider);
        this.mContext.sendBroadcastAsUser(intent, new UserHandle(this.mUserId));
    }

    void sendUpdateIntentLocked(Provider p, int[] appWidgetIds) {
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intent.setComponent(p.info.provider);
            this.mContext.sendBroadcastAsUser(intent, new UserHandle(this.mUserId));
        }
    }

    void registerForBroadcastsLocked(Provider p, int[] appWidgetIds) {
        if (p.info.updatePeriodMillis > 0) {
            boolean alreadyRegistered = p.broadcast != null;
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intent.setComponent(p.info.provider);
            long token = Binder.clearCallingIdentity();
            try {
                p.broadcast = PendingIntent.getBroadcastAsUser(this.mContext, 1, intent, 134217728, new UserHandle(this.mUserId));
                Binder.restoreCallingIdentity(token);
                if (!alreadyRegistered) {
                    long period = p.info.updatePeriodMillis;
                    if (period < AlarmManager.INTERVAL_HALF_HOUR) {
                        period = 1800000;
                    }
                    this.mAlarmManager.setInexactRepeating(2, SystemClock.elapsedRealtime() + period, period, p.broadcast);
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }
    }

    static int[] getAppWidgetIds(Provider p) {
        int instancesSize = p.instances.size();
        int[] appWidgetIds = new int[instancesSize];
        for (int i = 0; i < instancesSize; i++) {
            appWidgetIds[i] = p.instances.get(i).appWidgetId;
        }
        return appWidgetIds;
    }

    public int[] getAppWidgetIds(ComponentName provider) {
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
            Provider p = lookupProviderLocked(provider);
            if (p != null && Binder.getCallingUid() == p.uid) {
                return getAppWidgetIds(p);
            }
            return new int[0];
        }
    }

    static int[] getAppWidgetIds(Host h) {
        int instancesSize = h.instances.size();
        int[] appWidgetIds = new int[instancesSize];
        for (int i = 0; i < instancesSize; i++) {
            appWidgetIds[i] = h.instances.get(i).appWidgetId;
        }
        return appWidgetIds;
    }

    public int[] getAppWidgetIdsForHost(int hostId) {
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
            int callingUid = Binder.getCallingUid();
            Host host = lookupHostLocked(callingUid, hostId);
            if (host != null) {
                return getAppWidgetIds(host);
            }
            return new int[0];
        }
    }

    private Provider parseProviderInfoXml(ComponentName component, ResolveInfo ri) {
        ActivityInfo activityInfo = ri.activityInfo;
        XmlResourceParser parser = null;
        try {
            try {
                XmlResourceParser parser2 = activityInfo.loadXmlMetaData(this.mContext.getPackageManager(), AppWidgetManager.META_DATA_APPWIDGET_PROVIDER);
                if (parser2 == null) {
                    Slog.w(TAG, "No android.appwidget.provider meta-data for AppWidget provider '" + component + '\'');
                    if (parser2 != null) {
                        parser2.close();
                    }
                    return null;
                }
                AttributeSet attrs = Xml.asAttributeSet(parser2);
                while (true) {
                    int type = parser2.next();
                    if (type == 1 || type == 2) {
                        break;
                    }
                }
                String nodeName = parser2.getName();
                if (!"appwidget-provider".equals(nodeName)) {
                    Slog.w(TAG, "Meta-data does not start with appwidget-provider tag for AppWidget provider '" + component + '\'');
                    if (parser2 != null) {
                        parser2.close();
                    }
                    return null;
                }
                Provider p = new Provider();
                AppWidgetProviderInfo info = new AppWidgetProviderInfo();
                p.info = info;
                info.provider = component;
                p.uid = activityInfo.applicationInfo.uid;
                Resources res = this.mContext.getPackageManager().getResourcesForApplicationAsUser(activityInfo.packageName, this.mUserId);
                TypedArray sa = res.obtainAttributes(attrs, R.styleable.AppWidgetProviderInfo);
                TypedValue value = sa.peekValue(0);
                info.minWidth = value != null ? value.data : 0;
                TypedValue value2 = sa.peekValue(1);
                info.minHeight = value2 != null ? value2.data : 0;
                TypedValue value3 = sa.peekValue(8);
                info.minResizeWidth = value3 != null ? value3.data : info.minWidth;
                TypedValue value4 = sa.peekValue(9);
                info.minResizeHeight = value4 != null ? value4.data : info.minHeight;
                info.updatePeriodMillis = sa.getInt(2, 0);
                info.initialLayout = sa.getResourceId(3, 0);
                info.initialKeyguardLayout = sa.getResourceId(10, 0);
                String className = sa.getString(4);
                if (className != null) {
                    info.configure = new ComponentName(component.getPackageName(), className);
                }
                info.label = activityInfo.loadLabel(this.mContext.getPackageManager()).toString();
                info.icon = ri.getIconResource();
                info.previewImage = sa.getResourceId(5, 0);
                info.autoAdvanceViewId = sa.getResourceId(6, -1);
                info.resizeMode = sa.getInt(7, 0);
                info.widgetCategory = sa.getInt(11, 1);
                sa.recycle();
                if (parser2 != null) {
                    parser2.close();
                }
                return p;
            } catch (Exception e) {
                Slog.w(TAG, "XML parsing failed for AppWidget provider '" + component + '\'', e);
                if (0 != 0) {
                    parser.close();
                }
                return null;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                parser.close();
            }
            throw th;
        }
    }

    int getUidForPackage(String packageName) throws PackageManager.NameNotFoundException {
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = this.mPm.getPackageInfo(packageName, 0, this.mUserId);
        } catch (RemoteException e) {
        }
        if (pkgInfo == null || pkgInfo.applicationInfo == null) {
            throw new PackageManager.NameNotFoundException();
        }
        return pkgInfo.applicationInfo.uid;
    }

    int enforceSystemOrCallingUid(String packageName) throws IllegalArgumentException {
        int callingUid = Binder.getCallingUid();
        if (UserHandle.getAppId(callingUid) == 1000 || callingUid == 0) {
            return callingUid;
        }
        return enforceCallingUid(packageName);
    }

    int enforceCallingUid(String packageName) throws IllegalArgumentException {
        int callingUid = Binder.getCallingUid();
        try {
            int packageUid = getUidForPackage(packageName);
            if (!UserHandle.isSameApp(callingUid, packageUid)) {
                throw new IllegalArgumentException("packageName and uid don't match packageName=" + packageName);
            }
            return callingUid;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException("packageName and uid don't match packageName=" + packageName);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendInitialBroadcasts() {
        synchronized (this.mAppWidgetIds) {
            ensureStateLoadedLocked();
            int N = this.mInstalledProviders.size();
            for (int i = 0; i < N; i++) {
                Provider p = this.mInstalledProviders.get(i);
                if (p.instances.size() > 0) {
                    sendEnableIntentLocked(p);
                    int[] appWidgetIds = getAppWidgetIds(p);
                    sendUpdateIntentLocked(p, appWidgetIds);
                    registerForBroadcastsLocked(p, appWidgetIds);
                }
            }
        }
    }

    void loadStateLocked() {
        AtomicFile file = savedStateFile();
        try {
            FileInputStream stream = file.openRead();
            readStateFromFileLocked(stream);
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Slog.w(TAG, "Failed to close state FileInputStream " + e);
                }
            }
        } catch (FileNotFoundException e2) {
            Slog.w(TAG, "Failed to read state: " + e2);
        }
    }

    void saveStateLocked() {
        if (!this.mHasFeature) {
            return;
        }
        AtomicFile file = savedStateFile();
        try {
            FileOutputStream stream = file.startWrite();
            if (writeStateToFileLocked(stream)) {
                file.finishWrite(stream);
            } else {
                file.failWrite(stream);
                Slog.w(TAG, "Failed to save state, restoring backup.");
            }
        } catch (IOException e) {
            Slog.w(TAG, "Failed open state file for write: " + e);
        }
    }

    boolean writeStateToFileLocked(FileOutputStream stream) {
        try {
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(stream, "utf-8");
            out.startDocument(null, true);
            out.startTag(null, "gs");
            out.attribute(null, "version", String.valueOf(1));
            int providerIndex = 0;
            int N = this.mInstalledProviders.size();
            for (int i = 0; i < N; i++) {
                Provider p = this.mInstalledProviders.get(i);
                if (p.instances.size() > 0) {
                    out.startTag(null, "p");
                    out.attribute(null, "pkg", p.info.provider.getPackageName());
                    out.attribute(null, Telephony.Mms.Part.CONTENT_LOCATION, p.info.provider.getClassName());
                    out.endTag(null, "p");
                    p.tag = providerIndex;
                    providerIndex++;
                }
            }
            int N2 = this.mHosts.size();
            for (int i2 = 0; i2 < N2; i2++) {
                Host host = this.mHosts.get(i2);
                out.startTag(null, "h");
                out.attribute(null, "pkg", host.packageName);
                out.attribute(null, "id", Integer.toHexString(host.hostId));
                out.endTag(null, "h");
                host.tag = i2;
            }
            int N3 = this.mAppWidgetIds.size();
            for (int i3 = 0; i3 < N3; i3++) {
                AppWidgetId id = this.mAppWidgetIds.get(i3);
                out.startTag(null, "g");
                out.attribute(null, "id", Integer.toHexString(id.appWidgetId));
                out.attribute(null, "h", Integer.toHexString(id.host.tag));
                if (id.provider != null) {
                    out.attribute(null, "p", Integer.toHexString(id.provider.tag));
                }
                if (id.options != null) {
                    out.attribute(null, "min_width", Integer.toHexString(id.options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)));
                    out.attribute(null, "min_height", Integer.toHexString(id.options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)));
                    out.attribute(null, "max_width", Integer.toHexString(id.options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)));
                    out.attribute(null, "max_height", Integer.toHexString(id.options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)));
                    out.attribute(null, "host_category", Integer.toHexString(id.options.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY)));
                }
                out.endTag(null, "g");
            }
            Iterator<String> it = this.mPackagesWithBindWidgetPermission.iterator();
            while (it.hasNext()) {
                out.startTag(null, "b");
                out.attribute(null, ContactsContract.Directory.PACKAGE_NAME, it.next());
                out.endTag(null, "b");
            }
            out.endTag(null, "gs");
            out.endDocument();
            return true;
        } catch (IOException e) {
            Slog.w(TAG, "Failed to write state: " + e);
            return false;
        }
    }

    void readStateFromFileLocked(FileInputStream stream) {
        int type;
        boolean success = false;
        int version = 0;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, null);
            int providerIndex = 0;
            HashMap<Integer, Provider> loadedProviders = new HashMap<>();
            do {
                type = parser.next();
                if (type == 2) {
                    String tag = parser.getName();
                    if ("gs".equals(tag)) {
                        String attributeValue = parser.getAttributeValue(null, "version");
                        try {
                            version = Integer.parseInt(attributeValue);
                        } catch (NumberFormatException e) {
                            version = 0;
                        }
                    } else if ("p".equals(tag)) {
                        String pkg = parser.getAttributeValue(null, "pkg");
                        String cl = parser.getAttributeValue(null, Telephony.Mms.Part.CONTENT_LOCATION);
                        IPackageManager packageManager = AppGlobals.getPackageManager();
                        try {
                            packageManager.getReceiverInfo(new ComponentName(pkg, cl), 0, this.mUserId);
                        } catch (RemoteException e2) {
                            String[] pkgs = this.mContext.getPackageManager().currentToCanonicalPackageNames(new String[]{pkg});
                            pkg = pkgs[0];
                        }
                        Provider p = lookupProviderLocked(new ComponentName(pkg, cl));
                        if (p == null && this.mSafeMode) {
                            p = new Provider();
                            p.info = new AppWidgetProviderInfo();
                            p.info.provider = new ComponentName(pkg, cl);
                            p.zombie = true;
                            this.mInstalledProviders.add(p);
                        }
                        if (p != null) {
                            loadedProviders.put(Integer.valueOf(providerIndex), p);
                        }
                        providerIndex++;
                    } else if ("h".equals(tag)) {
                        Host host = new Host();
                        host.packageName = parser.getAttributeValue(null, "pkg");
                        try {
                            host.uid = getUidForPackage(host.packageName);
                        } catch (PackageManager.NameNotFoundException e3) {
                            host.zombie = true;
                        }
                        if (!host.zombie || this.mSafeMode) {
                            host.hostId = Integer.parseInt(parser.getAttributeValue(null, "id"), 16);
                            this.mHosts.add(host);
                        }
                    } else if ("b".equals(tag)) {
                        String packageName = parser.getAttributeValue(null, ContactsContract.Directory.PACKAGE_NAME);
                        if (packageName != null) {
                            this.mPackagesWithBindWidgetPermission.add(packageName);
                        }
                    } else if ("g".equals(tag)) {
                        AppWidgetId id = new AppWidgetId();
                        id.appWidgetId = Integer.parseInt(parser.getAttributeValue(null, "id"), 16);
                        if (id.appWidgetId >= this.mNextAppWidgetId) {
                            this.mNextAppWidgetId = id.appWidgetId + 1;
                        }
                        Bundle options = new Bundle();
                        String minWidthString = parser.getAttributeValue(null, "min_width");
                        if (minWidthString != null) {
                            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, Integer.parseInt(minWidthString, 16));
                        }
                        String minHeightString = parser.getAttributeValue(null, "min_height");
                        if (minHeightString != null) {
                            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, Integer.parseInt(minHeightString, 16));
                        }
                        String maxWidthString = parser.getAttributeValue(null, "max_width");
                        if (maxWidthString != null) {
                            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, Integer.parseInt(maxWidthString, 16));
                        }
                        String maxHeightString = parser.getAttributeValue(null, "max_height");
                        if (maxHeightString != null) {
                            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, Integer.parseInt(maxHeightString, 16));
                        }
                        String categoryString = parser.getAttributeValue(null, "host_category");
                        if (categoryString != null) {
                            options.putInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, Integer.parseInt(categoryString, 16));
                        }
                        id.options = options;
                        String providerString = parser.getAttributeValue(null, "p");
                        if (providerString != null) {
                            int pIndex = Integer.parseInt(providerString, 16);
                            id.provider = loadedProviders.get(Integer.valueOf(pIndex));
                            if (id.provider == null) {
                            }
                        }
                        int hIndex = Integer.parseInt(parser.getAttributeValue(null, "h"), 16);
                        id.host = this.mHosts.get(hIndex);
                        if (id.host != null) {
                            if (id.provider != null) {
                                id.provider.instances.add(id);
                            }
                            id.host.instances.add(id);
                            this.mAppWidgetIds.add(id);
                        }
                    }
                }
            } while (type != 1);
            success = true;
        } catch (IOException e4) {
            Slog.w(TAG, "failed parsing " + e4);
        } catch (IndexOutOfBoundsException e5) {
            Slog.w(TAG, "failed parsing " + e5);
        } catch (NullPointerException e6) {
            Slog.w(TAG, "failed parsing " + e6);
        } catch (NumberFormatException e7) {
            Slog.w(TAG, "failed parsing " + e7);
        } catch (XmlPullParserException e8) {
            Slog.w(TAG, "failed parsing " + e8);
        }
        if (success) {
            for (int i = this.mHosts.size() - 1; i >= 0; i--) {
                pruneHostLocked(this.mHosts.get(i));
            }
            performUpgrade(version);
            return;
        }
        Slog.w(TAG, "Failed to read state, clearing widgets and hosts.");
        this.mAppWidgetIds.clear();
        this.mHosts.clear();
        int N = this.mInstalledProviders.size();
        for (int i2 = 0; i2 < N; i2++) {
            this.mInstalledProviders.get(i2).instances.clear();
        }
    }

    private void performUpgrade(int fromVersion) {
        if (fromVersion < 1) {
            Slog.v(TAG, "Upgrading widget database from " + fromVersion + " to 1 for user " + this.mUserId);
        }
        int version = fromVersion;
        if (version == 0) {
            for (int i = 0; i < this.mHosts.size(); i++) {
                Host host = this.mHosts.get(i);
                if (host != null && "android".equals(host.packageName) && host.hostId == KEYGUARD_HOST_ID) {
                    host.packageName = KEYGUARD_HOST_PACKAGE;
                }
            }
            version = 1;
        }
        if (version != 1) {
            throw new IllegalStateException("Failed to upgrade widget database");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static File getSettingsFile(int userId) {
        return new File(Environment.getUserSystemDirectory(userId), SETTINGS_FILENAME);
    }

    AtomicFile savedStateFile() {
        File dir = Environment.getUserSystemDirectory(this.mUserId);
        File settingsFile = getSettingsFile(this.mUserId);
        if (!settingsFile.exists() && this.mUserId == 0) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File oldFile = new File("/data/system/appwidgets.xml");
            oldFile.renameTo(settingsFile);
        }
        return new AtomicFile(settingsFile);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onUserStopping() {
        int N = this.mInstalledProviders.size();
        for (int i = N - 1; i >= 0; i--) {
            Provider p = this.mInstalledProviders.get(i);
            cancelBroadcasts(p);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onUserRemoved() {
        getSettingsFile(this.mUserId).delete();
    }

    boolean addProvidersForPackageLocked(String pkgName) {
        boolean providersAdded = false;
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setPackage(pkgName);
        try {
            List<ResolveInfo> broadcastReceivers = this.mPm.queryIntentReceivers(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 128, this.mUserId);
            int N = broadcastReceivers == null ? 0 : broadcastReceivers.size();
            for (int i = 0; i < N; i++) {
                ResolveInfo ri = broadcastReceivers.get(i);
                ActivityInfo ai = ri.activityInfo;
                if ((ai.applicationInfo.flags & 262144) == 0 && pkgName.equals(ai.packageName)) {
                    addProviderLocked(ri);
                    providersAdded = true;
                }
            }
            return providersAdded;
        } catch (RemoteException e) {
            return false;
        }
    }

    boolean updateProvidersForPackageLocked(String pkgName, Set<ComponentName> removedProviders) {
        boolean providersUpdated = false;
        HashSet<String> keep = new HashSet<>();
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setPackage(pkgName);
        try {
            List<ResolveInfo> broadcastReceivers = this.mPm.queryIntentReceivers(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 128, this.mUserId);
            int N = broadcastReceivers == null ? 0 : broadcastReceivers.size();
            for (int i = 0; i < N; i++) {
                ResolveInfo ri = broadcastReceivers.get(i);
                ActivityInfo ai = ri.activityInfo;
                if ((ai.applicationInfo.flags & 262144) == 0 && pkgName.equals(ai.packageName)) {
                    ComponentName component = new ComponentName(ai.packageName, ai.name);
                    Provider p = lookupProviderLocked(component);
                    if (p == null) {
                        if (addProviderLocked(ri)) {
                            keep.add(ai.name);
                            providersUpdated = true;
                        }
                    } else {
                        Provider parsed = parseProviderInfoXml(component, ri);
                        if (parsed != null) {
                            keep.add(ai.name);
                            p.info = parsed.info;
                            int M = p.instances.size();
                            if (M > 0) {
                                int[] appWidgetIds = getAppWidgetIds(p);
                                cancelBroadcasts(p);
                                registerForBroadcastsLocked(p, appWidgetIds);
                                for (int j = 0; j < M; j++) {
                                    AppWidgetId id = p.instances.get(j);
                                    id.views = null;
                                    if (id.host != null && id.host.callbacks != null) {
                                        try {
                                            id.host.callbacks.providerChanged(id.appWidgetId, p.info, this.mUserId);
                                        } catch (RemoteException e) {
                                            id.host.callbacks = null;
                                        }
                                    }
                                }
                                sendUpdateIntentLocked(p, appWidgetIds);
                                providersUpdated = true;
                            }
                        }
                    }
                }
            }
            int N2 = this.mInstalledProviders.size();
            for (int i2 = N2 - 1; i2 >= 0; i2--) {
                Provider p2 = this.mInstalledProviders.get(i2);
                if (pkgName.equals(p2.info.provider.getPackageName()) && !keep.contains(p2.info.provider.getClassName())) {
                    if (removedProviders != null) {
                        removedProviders.add(p2.info.provider);
                    }
                    removeProviderLocked(i2, p2);
                    providersUpdated = true;
                }
            }
            return providersUpdated;
        } catch (RemoteException e2) {
            return false;
        }
    }

    boolean removeProvidersForPackageLocked(String pkgName) {
        boolean providersRemoved = false;
        int N = this.mInstalledProviders.size();
        for (int i = N - 1; i >= 0; i--) {
            Provider p = this.mInstalledProviders.get(i);
            if (pkgName.equals(p.info.provider.getPackageName())) {
                removeProviderLocked(i, p);
                providersRemoved = true;
            }
        }
        int N2 = this.mHosts.size();
        for (int i2 = N2 - 1; i2 >= 0; i2--) {
            Host host = this.mHosts.get(i2);
            if (pkgName.equals(host.packageName)) {
                deleteHostLocked(host);
            }
        }
        return providersRemoved;
    }

    void notifyHostsForProvidersChangedLocked() {
        int N = this.mHosts.size();
        for (int i = N - 1; i >= 0; i--) {
            Host host = this.mHosts.get(i);
            try {
                if (host.callbacks != null) {
                    host.callbacks.providersChanged(this.mUserId);
                }
            } catch (RemoteException e) {
                host.callbacks = null;
            }
        }
    }
}