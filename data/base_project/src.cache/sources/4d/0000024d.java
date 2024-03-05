package android.appwidget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;
import com.android.internal.appwidget.IAppWidgetService;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.WeakHashMap;

/* loaded from: AppWidgetManager.class */
public class AppWidgetManager {
    static final String TAG = "AppWidgetManager";
    public static final String ACTION_APPWIDGET_PICK = "android.appwidget.action.APPWIDGET_PICK";
    public static final String ACTION_KEYGUARD_APPWIDGET_PICK = "android.appwidget.action.KEYGUARD_APPWIDGET_PICK";
    public static final String ACTION_APPWIDGET_BIND = "android.appwidget.action.APPWIDGET_BIND";
    public static final String ACTION_APPWIDGET_CONFIGURE = "android.appwidget.action.APPWIDGET_CONFIGURE";
    public static final String EXTRA_APPWIDGET_ID = "appWidgetId";
    public static final String OPTION_APPWIDGET_MIN_WIDTH = "appWidgetMinWidth";
    public static final String OPTION_APPWIDGET_MIN_HEIGHT = "appWidgetMinHeight";
    public static final String OPTION_APPWIDGET_MAX_WIDTH = "appWidgetMaxWidth";
    public static final String OPTION_APPWIDGET_MAX_HEIGHT = "appWidgetMaxHeight";
    public static final String OPTION_APPWIDGET_HOST_CATEGORY = "appWidgetCategory";
    public static final String EXTRA_APPWIDGET_OPTIONS = "appWidgetOptions";
    public static final String EXTRA_APPWIDGET_IDS = "appWidgetIds";
    public static final String EXTRA_APPWIDGET_PROVIDER = "appWidgetProvider";
    public static final String EXTRA_CUSTOM_INFO = "customInfo";
    public static final String EXTRA_CUSTOM_EXTRAS = "customExtras";
    public static final String EXTRA_CATEGORY_FILTER = "categoryFilter";
    public static final String EXTRA_CUSTOM_SORT = "customSort";
    public static final int INVALID_APPWIDGET_ID = 0;
    public static final String ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String ACTION_APPWIDGET_OPTIONS_CHANGED = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";
    public static final String ACTION_APPWIDGET_DELETED = "android.appwidget.action.APPWIDGET_DELETED";
    public static final String ACTION_APPWIDGET_DISABLED = "android.appwidget.action.APPWIDGET_DISABLED";
    public static final String ACTION_APPWIDGET_ENABLED = "android.appwidget.action.APPWIDGET_ENABLED";
    public static final String META_DATA_APPWIDGET_PROVIDER = "android.appwidget.provider";
    static WeakHashMap<Context, WeakReference<AppWidgetManager>> sManagerCache = new WeakHashMap<>();
    static IAppWidgetService sService;
    Context mContext;
    private DisplayMetrics mDisplayMetrics;

    public static AppWidgetManager getInstance(Context context) {
        AppWidgetManager appWidgetManager;
        synchronized (sManagerCache) {
            if (sService == null) {
                IBinder b = ServiceManager.getService(Context.APPWIDGET_SERVICE);
                sService = IAppWidgetService.Stub.asInterface(b);
            }
            WeakReference<AppWidgetManager> ref = sManagerCache.get(context);
            AppWidgetManager result = null;
            if (ref != null) {
                result = ref.get();
            }
            if (result == null) {
                result = new AppWidgetManager(context);
                sManagerCache.put(context, new WeakReference<>(result));
            }
            appWidgetManager = result;
        }
        return appWidgetManager;
    }

    private AppWidgetManager(Context context) {
        this.mContext = context;
        this.mDisplayMetrics = context.getResources().getDisplayMetrics();
    }

    public void updateAppWidget(int[] appWidgetIds, RemoteViews views) {
        try {
            sService.updateAppWidgetIds(appWidgetIds, views, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void updateAppWidgetOptions(int appWidgetId, Bundle options) {
        try {
            sService.updateAppWidgetOptions(appWidgetId, options, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public Bundle getAppWidgetOptions(int appWidgetId) {
        try {
            return sService.getAppWidgetOptions(appWidgetId, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void updateAppWidget(int appWidgetId, RemoteViews views) {
        updateAppWidget(new int[]{appWidgetId}, views);
    }

    public void partiallyUpdateAppWidget(int[] appWidgetIds, RemoteViews views) {
        try {
            sService.partiallyUpdateAppWidgetIds(appWidgetIds, views, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void partiallyUpdateAppWidget(int appWidgetId, RemoteViews views) {
        partiallyUpdateAppWidget(new int[]{appWidgetId}, views);
    }

    public void updateAppWidget(ComponentName provider, RemoteViews views) {
        try {
            sService.updateAppWidgetProvider(provider, views, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void notifyAppWidgetViewDataChanged(int[] appWidgetIds, int viewId) {
        try {
            sService.notifyAppWidgetViewDataChanged(appWidgetIds, viewId, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void notifyAppWidgetViewDataChanged(int appWidgetId, int viewId) {
        notifyAppWidgetViewDataChanged(new int[]{appWidgetId}, viewId);
    }

    public List<AppWidgetProviderInfo> getInstalledProviders() {
        return getInstalledProviders(1);
    }

    public List<AppWidgetProviderInfo> getInstalledProviders(int categoryFilter) {
        try {
            List<AppWidgetProviderInfo> providers = sService.getInstalledProviders(categoryFilter, this.mContext.getUserId());
            for (AppWidgetProviderInfo info : providers) {
                info.minWidth = TypedValue.complexToDimensionPixelSize(info.minWidth, this.mDisplayMetrics);
                info.minHeight = TypedValue.complexToDimensionPixelSize(info.minHeight, this.mDisplayMetrics);
                info.minResizeWidth = TypedValue.complexToDimensionPixelSize(info.minResizeWidth, this.mDisplayMetrics);
                info.minResizeHeight = TypedValue.complexToDimensionPixelSize(info.minResizeHeight, this.mDisplayMetrics);
            }
            return providers;
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public AppWidgetProviderInfo getAppWidgetInfo(int appWidgetId) {
        try {
            AppWidgetProviderInfo info = sService.getAppWidgetInfo(appWidgetId, this.mContext.getUserId());
            if (info != null) {
                info.minWidth = TypedValue.complexToDimensionPixelSize(info.minWidth, this.mDisplayMetrics);
                info.minHeight = TypedValue.complexToDimensionPixelSize(info.minHeight, this.mDisplayMetrics);
                info.minResizeWidth = TypedValue.complexToDimensionPixelSize(info.minResizeWidth, this.mDisplayMetrics);
                info.minResizeHeight = TypedValue.complexToDimensionPixelSize(info.minResizeHeight, this.mDisplayMetrics);
            }
            return info;
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void bindAppWidgetId(int appWidgetId, ComponentName provider) {
        try {
            sService.bindAppWidgetId(appWidgetId, provider, null, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void bindAppWidgetId(int appWidgetId, ComponentName provider, Bundle options) {
        try {
            sService.bindAppWidgetId(appWidgetId, provider, options, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public boolean bindAppWidgetIdIfAllowed(int appWidgetId, ComponentName provider) {
        if (this.mContext == null) {
            return false;
        }
        try {
            return sService.bindAppWidgetIdIfAllowed(this.mContext.getPackageName(), appWidgetId, provider, null, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public boolean bindAppWidgetIdIfAllowed(int appWidgetId, ComponentName provider, Bundle options) {
        if (this.mContext == null) {
            return false;
        }
        try {
            return sService.bindAppWidgetIdIfAllowed(this.mContext.getPackageName(), appWidgetId, provider, options, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public boolean hasBindAppWidgetPermission(String packageName) {
        try {
            return sService.hasBindAppWidgetPermission(packageName, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void setBindAppWidgetPermission(String packageName, boolean permission) {
        try {
            sService.setBindAppWidgetPermission(packageName, permission, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void bindRemoteViewsService(int appWidgetId, Intent intent, IBinder connection, UserHandle userHandle) {
        try {
            sService.bindRemoteViewsService(appWidgetId, intent, connection, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void unbindRemoteViewsService(int appWidgetId, Intent intent, UserHandle userHandle) {
        try {
            sService.unbindRemoteViewsService(appWidgetId, intent, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public int[] getAppWidgetIds(ComponentName provider) {
        try {
            return sService.getAppWidgetIds(provider, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }
}