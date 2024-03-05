package android.appwidget;

import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;
import com.android.internal.appwidget.IAppWidgetHost;
import com.android.internal.appwidget.IAppWidgetService;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: AppWidgetHost.class */
public class AppWidgetHost {
    static final int HANDLE_UPDATE = 1;
    static final int HANDLE_PROVIDER_CHANGED = 2;
    static final int HANDLE_PROVIDERS_CHANGED = 3;
    static final int HANDLE_VIEW_DATA_CHANGED = 4;
    static final Object sServiceLock = new Object();
    static IAppWidgetService sService;
    private DisplayMetrics mDisplayMetrics;
    Context mContext;
    String mPackageName;
    Handler mHandler;
    int mHostId;
    Callbacks mCallbacks;
    final HashMap<Integer, AppWidgetHostView> mViews;
    private RemoteViews.OnClickHandler mOnClickHandler;

    /* loaded from: AppWidgetHost$Callbacks.class */
    class Callbacks extends IAppWidgetHost.Stub {
        Callbacks() {
        }

        @Override // com.android.internal.appwidget.IAppWidgetHost
        public void updateAppWidget(int appWidgetId, RemoteViews views, int userId) {
            if (AppWidgetHost.this.isLocalBinder() && views != null) {
                views = views.m1045clone();
                views.setUser(new UserHandle(userId));
            }
            Message msg = AppWidgetHost.this.mHandler.obtainMessage(1, appWidgetId, userId, views);
            msg.sendToTarget();
        }

        @Override // com.android.internal.appwidget.IAppWidgetHost
        public void providerChanged(int appWidgetId, AppWidgetProviderInfo info, int userId) {
            if (AppWidgetHost.this.isLocalBinder() && info != null) {
                info = info.m75clone();
            }
            Message msg = AppWidgetHost.this.mHandler.obtainMessage(2, appWidgetId, userId, info);
            msg.sendToTarget();
        }

        @Override // com.android.internal.appwidget.IAppWidgetHost
        public void providersChanged(int userId) {
            Message msg = AppWidgetHost.this.mHandler.obtainMessage(3, userId, 0);
            msg.sendToTarget();
        }

        @Override // com.android.internal.appwidget.IAppWidgetHost
        public void viewDataChanged(int appWidgetId, int viewId, int userId) {
            Message msg = AppWidgetHost.this.mHandler.obtainMessage(4, appWidgetId, viewId, Integer.valueOf(userId));
            msg.sendToTarget();
        }
    }

    /* loaded from: AppWidgetHost$UpdateHandler.class */
    class UpdateHandler extends Handler {
        public UpdateHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    AppWidgetHost.this.updateAppWidgetView(msg.arg1, (RemoteViews) msg.obj, msg.arg2);
                    return;
                case 2:
                    AppWidgetHost.this.onProviderChanged(msg.arg1, (AppWidgetProviderInfo) msg.obj);
                    return;
                case 3:
                    AppWidgetHost.this.onProvidersChanged();
                    return;
                case 4:
                    AppWidgetHost.this.viewDataChanged(msg.arg1, msg.arg2, ((Integer) msg.obj).intValue());
                    return;
                default:
                    return;
            }
        }
    }

    public AppWidgetHost(Context context, int hostId) {
        this(context, hostId, null, context.getMainLooper());
    }

    public AppWidgetHost(Context context, int hostId, RemoteViews.OnClickHandler handler, Looper looper) {
        this.mCallbacks = new Callbacks();
        this.mViews = new HashMap<>();
        this.mContext = context;
        this.mHostId = hostId;
        this.mOnClickHandler = handler;
        this.mHandler = new UpdateHandler(looper);
        this.mDisplayMetrics = context.getResources().getDisplayMetrics();
        bindService();
    }

    private static void bindService() {
        synchronized (sServiceLock) {
            if (sService == null) {
                IBinder b = ServiceManager.getService(Context.APPWIDGET_SERVICE);
                sService = IAppWidgetService.Stub.asInterface(b);
            }
        }
    }

    public void startListening() {
        ArrayList<RemoteViews> updatedViews = new ArrayList<>();
        int userId = this.mContext.getUserId();
        try {
            if (this.mPackageName == null) {
                this.mPackageName = this.mContext.getPackageName();
            }
            int[] updatedIds = sService.startListening(this.mCallbacks, this.mPackageName, this.mHostId, updatedViews, userId);
            int N = updatedIds.length;
            for (int i = 0; i < N; i++) {
                if (updatedViews.get(i) != null) {
                    updatedViews.get(i).setUser(new UserHandle(userId));
                }
                updateAppWidgetView(updatedIds[i], updatedViews.get(i), userId);
            }
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void stopListening() {
        try {
            sService.stopListening(this.mHostId, this.mContext.getUserId());
            clearViews();
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public int allocateAppWidgetId() {
        try {
            if (this.mPackageName == null) {
                this.mPackageName = this.mContext.getPackageName();
            }
            return sService.allocateAppWidgetId(this.mPackageName, this.mHostId, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public static int allocateAppWidgetIdForPackage(int hostId, int userId, String packageName) {
        checkCallerIsSystem();
        try {
            if (sService == null) {
                bindService();
            }
            return sService.allocateAppWidgetId(packageName, hostId, userId);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public int[] getAppWidgetIds() {
        try {
            if (sService == null) {
                bindService();
            }
            return sService.getAppWidgetIdsForHost(this.mHostId, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    private static void checkCallerIsSystem() {
        int uid = Process.myUid();
        if (UserHandle.getAppId(uid) == 1000 || uid == 0) {
            return;
        }
        throw new SecurityException("Disallowed call for uid " + uid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLocalBinder() {
        return Process.myPid() == Binder.getCallingPid();
    }

    public void deleteAppWidgetId(int appWidgetId) {
        synchronized (this.mViews) {
            this.mViews.remove(Integer.valueOf(appWidgetId));
            try {
                sService.deleteAppWidgetId(appWidgetId, this.mContext.getUserId());
            } catch (RemoteException e) {
                throw new RuntimeException("system server dead?", e);
            }
        }
    }

    public static void deleteAppWidgetIdForSystem(int appWidgetId, int userId) {
        checkCallerIsSystem();
        try {
            if (sService == null) {
                bindService();
            }
            sService.deleteAppWidgetId(appWidgetId, userId);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void deleteHost() {
        try {
            sService.deleteHost(this.mHostId, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public static void deleteAllHosts() {
        deleteAllHosts(UserHandle.myUserId());
    }

    public static void deleteAllHosts(int userId) {
        try {
            sService.deleteAllHosts(userId);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public final AppWidgetHostView createView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        int userId = this.mContext.getUserId();
        AppWidgetHostView view = onCreateView(this.mContext, appWidgetId, appWidget);
        view.setUserId(userId);
        view.setOnClickHandler(this.mOnClickHandler);
        view.setAppWidget(appWidgetId, appWidget);
        synchronized (this.mViews) {
            this.mViews.put(Integer.valueOf(appWidgetId), view);
        }
        try {
            RemoteViews views = sService.getAppWidgetViews(appWidgetId, userId);
            if (views != null) {
                views.setUser(new UserHandle(this.mContext.getUserId()));
            }
            view.updateAppWidget(views);
            return view;
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    protected AppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        return new AppWidgetHostView(context, this.mOnClickHandler);
    }

    protected void onProviderChanged(int appWidgetId, AppWidgetProviderInfo appWidget) {
        AppWidgetHostView v;
        appWidget.minWidth = TypedValue.complexToDimensionPixelSize(appWidget.minWidth, this.mDisplayMetrics);
        appWidget.minHeight = TypedValue.complexToDimensionPixelSize(appWidget.minHeight, this.mDisplayMetrics);
        appWidget.minResizeWidth = TypedValue.complexToDimensionPixelSize(appWidget.minResizeWidth, this.mDisplayMetrics);
        appWidget.minResizeHeight = TypedValue.complexToDimensionPixelSize(appWidget.minResizeHeight, this.mDisplayMetrics);
        synchronized (this.mViews) {
            v = this.mViews.get(Integer.valueOf(appWidgetId));
        }
        if (v != null) {
            v.resetAppWidget(appWidget);
        }
    }

    protected void onProvidersChanged() {
    }

    void updateAppWidgetView(int appWidgetId, RemoteViews views, int userId) {
        AppWidgetHostView v;
        synchronized (this.mViews) {
            v = this.mViews.get(Integer.valueOf(appWidgetId));
        }
        if (v != null) {
            v.updateAppWidget(views);
        }
    }

    void viewDataChanged(int appWidgetId, int viewId, int userId) {
        AppWidgetHostView v;
        synchronized (this.mViews) {
            v = this.mViews.get(Integer.valueOf(appWidgetId));
        }
        if (v != null) {
            v.viewDataChanged(viewId);
        }
    }

    protected void clearViews() {
        this.mViews.clear();
    }
}