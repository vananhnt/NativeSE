package com.android.server;

import android.Manifest;
import android.app.ActivityManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Telephony;
import android.util.Slog;
import android.util.SparseArray;
import android.widget.RemoteViews;
import com.android.internal.appwidget.IAppWidgetHost;
import com.android.internal.appwidget.IAppWidgetService;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

/* loaded from: AppWidgetService.class */
class AppWidgetService extends IAppWidgetService.Stub {
    private static final String TAG = "AppWidgetService";
    Context mContext;
    Locale mLocale;
    PackageManager mPackageManager;
    boolean mSafeMode;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.AppWidgetService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
                int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -10000);
                if (userId >= 0) {
                    AppWidgetService.this.getImplForUser(userId).sendInitialBroadcasts();
                } else {
                    Slog.w(AppWidgetService.TAG, "Incorrect user handle supplied in " + intent);
                }
            } else if (Intent.ACTION_CONFIGURATION_CHANGED.equals(action)) {
                for (int i = 0; i < AppWidgetService.this.mAppWidgetServices.size(); i++) {
                    ((AppWidgetServiceImpl) AppWidgetService.this.mAppWidgetServices.valueAt(i)).onConfigurationChanged();
                }
            } else {
                int sendingUser = getSendingUserId();
                if (sendingUser != -1) {
                    AppWidgetServiceImpl service = (AppWidgetServiceImpl) AppWidgetService.this.mAppWidgetServices.get(sendingUser);
                    if (service != null) {
                        service.onBroadcastReceived(intent);
                        return;
                    }
                    return;
                }
                for (int i2 = 0; i2 < AppWidgetService.this.mAppWidgetServices.size(); i2++) {
                    ((AppWidgetServiceImpl) AppWidgetService.this.mAppWidgetServices.valueAt(i2)).onBroadcastReceived(intent);
                }
            }
        }
    };
    private final Handler mSaveStateHandler = BackgroundThread.getHandler();
    private final SparseArray<AppWidgetServiceImpl> mAppWidgetServices = new SparseArray<>(5);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppWidgetService(Context context) {
        this.mContext = context;
        AppWidgetServiceImpl primary = new AppWidgetServiceImpl(context, 0, this.mSaveStateHandler);
        this.mAppWidgetServices.append(0, primary);
    }

    public void systemRunning(boolean safeMode) {
        this.mSafeMode = safeMode;
        this.mAppWidgetServices.get(0).systemReady(safeMode);
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, new IntentFilter(Intent.ACTION_BOOT_COMPLETED), null, null);
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED), null, null);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, filter, null, null);
        IntentFilter sdFilter = new IntentFilter();
        sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, sdFilter, null, null);
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction(Intent.ACTION_USER_REMOVED);
        userFilter.addAction(Intent.ACTION_USER_STOPPING);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.AppWidgetService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_USER_REMOVED.equals(intent.getAction())) {
                    AppWidgetService.this.onUserRemoved(intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -10000));
                } else if (Intent.ACTION_USER_STOPPING.equals(intent.getAction())) {
                    AppWidgetService.this.onUserStopping(intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -10000));
                }
            }
        }, userFilter);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public int allocateAppWidgetId(String packageName, int hostId, int userId) throws RemoteException {
        return getImplForUser(userId).allocateAppWidgetId(packageName, hostId);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public int[] getAppWidgetIdsForHost(int hostId, int userId) throws RemoteException {
        return getImplForUser(userId).getAppWidgetIdsForHost(hostId);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void deleteAppWidgetId(int appWidgetId, int userId) throws RemoteException {
        getImplForUser(userId).deleteAppWidgetId(appWidgetId);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void deleteHost(int hostId, int userId) throws RemoteException {
        getImplForUser(userId).deleteHost(hostId);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void deleteAllHosts(int userId) throws RemoteException {
        getImplForUser(userId).deleteAllHosts();
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void bindAppWidgetId(int appWidgetId, ComponentName provider, Bundle options, int userId) throws RemoteException {
        getImplForUser(userId).bindAppWidgetId(appWidgetId, provider, options);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public boolean bindAppWidgetIdIfAllowed(String packageName, int appWidgetId, ComponentName provider, Bundle options, int userId) throws RemoteException {
        return getImplForUser(userId).bindAppWidgetIdIfAllowed(packageName, appWidgetId, provider, options);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public boolean hasBindAppWidgetPermission(String packageName, int userId) throws RemoteException {
        return getImplForUser(userId).hasBindAppWidgetPermission(packageName);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void setBindAppWidgetPermission(String packageName, boolean permission, int userId) throws RemoteException {
        getImplForUser(userId).setBindAppWidgetPermission(packageName, permission);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void bindRemoteViewsService(int appWidgetId, Intent intent, IBinder connection, int userId) throws RemoteException {
        getImplForUser(userId).bindRemoteViewsService(appWidgetId, intent, connection);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public int[] startListening(IAppWidgetHost host, String packageName, int hostId, List<RemoteViews> updatedViews, int userId) throws RemoteException {
        return getImplForUser(userId).startListening(host, packageName, hostId, updatedViews);
    }

    public void onUserRemoved(int userId) {
        if (userId < 1) {
            return;
        }
        synchronized (this.mAppWidgetServices) {
            AppWidgetServiceImpl impl = this.mAppWidgetServices.get(userId);
            this.mAppWidgetServices.remove(userId);
            if (impl == null) {
                AppWidgetServiceImpl.getSettingsFile(userId).delete();
            } else {
                impl.onUserRemoved();
            }
        }
    }

    public void onUserStopping(int userId) {
        if (userId < 1) {
            return;
        }
        synchronized (this.mAppWidgetServices) {
            AppWidgetServiceImpl impl = this.mAppWidgetServices.get(userId);
            if (impl != null) {
                this.mAppWidgetServices.remove(userId);
                impl.onUserStopping();
            }
        }
    }

    private void checkPermission(int userId) {
        ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, true, getClass().getSimpleName(), getClass().getPackage().getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AppWidgetServiceImpl getImplForUser(int userId) {
        AppWidgetServiceImpl service;
        checkPermission(userId);
        boolean sendInitial = false;
        synchronized (this.mAppWidgetServices) {
            service = this.mAppWidgetServices.get(userId);
            if (service == null) {
                Slog.i(TAG, "Unable to find AppWidgetServiceImpl for user " + userId + ", adding");
                service = new AppWidgetServiceImpl(this.mContext, userId, this.mSaveStateHandler);
                service.systemReady(this.mSafeMode);
                this.mAppWidgetServices.append(userId, service);
                sendInitial = true;
            }
        }
        if (sendInitial) {
            service.sendInitialBroadcasts();
        }
        return service;
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public int[] getAppWidgetIds(ComponentName provider, int userId) throws RemoteException {
        return getImplForUser(userId).getAppWidgetIds(provider);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public AppWidgetProviderInfo getAppWidgetInfo(int appWidgetId, int userId) throws RemoteException {
        return getImplForUser(userId).getAppWidgetInfo(appWidgetId);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public RemoteViews getAppWidgetViews(int appWidgetId, int userId) throws RemoteException {
        return getImplForUser(userId).getAppWidgetViews(appWidgetId);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void updateAppWidgetOptions(int appWidgetId, Bundle options, int userId) {
        getImplForUser(userId).updateAppWidgetOptions(appWidgetId, options);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public Bundle getAppWidgetOptions(int appWidgetId, int userId) {
        return getImplForUser(userId).getAppWidgetOptions(appWidgetId);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public List<AppWidgetProviderInfo> getInstalledProviders(int categoryFilter, int userId) throws RemoteException {
        return getImplForUser(userId).getInstalledProviders(categoryFilter);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void notifyAppWidgetViewDataChanged(int[] appWidgetIds, int viewId, int userId) throws RemoteException {
        getImplForUser(userId).notifyAppWidgetViewDataChanged(appWidgetIds, viewId);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void partiallyUpdateAppWidgetIds(int[] appWidgetIds, RemoteViews views, int userId) throws RemoteException {
        getImplForUser(userId).partiallyUpdateAppWidgetIds(appWidgetIds, views);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void stopListening(int hostId, int userId) throws RemoteException {
        getImplForUser(userId).stopListening(hostId);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void unbindRemoteViewsService(int appWidgetId, Intent intent, int userId) throws RemoteException {
        getImplForUser(userId).unbindRemoteViewsService(appWidgetId, intent);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void updateAppWidgetIds(int[] appWidgetIds, RemoteViews views, int userId) throws RemoteException {
        getImplForUser(userId).updateAppWidgetIds(appWidgetIds, views);
    }

    @Override // com.android.internal.appwidget.IAppWidgetService
    public void updateAppWidgetProvider(ComponentName provider, RemoteViews views, int userId) throws RemoteException {
        getImplForUser(userId).updateAppWidgetProvider(provider, views);
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DUMP, TAG);
        synchronized (this.mAppWidgetServices) {
            IndentingPrintWriter ipw = new IndentingPrintWriter(pw, "  ");
            for (int i = 0; i < this.mAppWidgetServices.size(); i++) {
                pw.println("User: " + this.mAppWidgetServices.keyAt(i));
                ipw.increaseIndent();
                AppWidgetServiceImpl service = this.mAppWidgetServices.valueAt(i);
                service.dump(fd, ipw, args);
                ipw.decreaseIndent();
            }
        }
    }
}