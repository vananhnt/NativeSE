package android.app;

import android.accounts.AccountManager;
import android.accounts.IAccountManager;
import android.app.IAlarmManager;
import android.app.LoadedApk;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.ConsumerIrManager;
import android.hardware.ISerialManager;
import android.hardware.SerialManager;
import android.hardware.SystemSensorManager;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.hardware.input.InputManager;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbManager;
import android.location.CountryDetector;
import android.location.ICountryDetector;
import android.location.ILocationManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.INetworkPolicyManager;
import android.net.NetworkPolicyManager;
import android.net.Uri;
import android.net.nsd.INsdManager;
import android.net.nsd.NsdManager;
import android.net.wifi.IWifiManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.IWifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.IUserManager;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemVibrator;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.print.IPrintManager;
import android.print.PrintManager;
import android.telephony.TelephonyManager;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.DisplayAdjustments;
import android.view.WindowManagerImpl;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.CaptioningManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IAppOpsService;
import com.android.internal.os.IDropBoxManagerService;
import com.android.internal.policy.PolicyManager;
import com.android.internal.util.Preconditions;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ContextImpl.class */
public class ContextImpl extends Context {
    private static final String TAG = "ContextImpl";
    private static final boolean DEBUG = false;
    private static ArrayMap<String, ArrayMap<String, SharedPreferencesImpl>> sSharedPrefs;
    LoadedApk mPackageInfo;
    private String mBasePackageName;
    private String mOpPackageName;
    private Resources mResources;
    ActivityThread mMainThread;
    private ApplicationContentResolver mContentResolver;
    private PackageManager mPackageManager;
    private Display mDisplay;
    private boolean mRestricted;
    private UserHandle mUser;
    private ResourcesManager mResourcesManager;
    @GuardedBy("mSync")
    private File mDatabasesDir;
    @GuardedBy("mSync")
    private File mPreferencesDir;
    @GuardedBy("mSync")
    private File mFilesDir;
    @GuardedBy("mSync")
    private File mCacheDir;
    @GuardedBy("mSync")
    private File[] mExternalObbDirs;
    @GuardedBy("mSync")
    private File[] mExternalFilesDirs;
    @GuardedBy("mSync")
    private File[] mExternalCacheDirs;
    private static final String[] EMPTY_FILE_LIST = new String[0];
    private static final HashMap<String, ServiceFetcher> SYSTEM_SERVICE_MAP = new HashMap<>();
    private static int sNextPerContextServiceCacheIndex = 0;
    private static ServiceFetcher WALLPAPER_FETCHER = new ServiceFetcher() { // from class: android.app.ContextImpl.1
        @Override // android.app.ContextImpl.ServiceFetcher
        public Object createService(ContextImpl ctx) {
            return new WallpaperManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
        }
    };
    private IBinder mActivityToken = null;
    private int mThemeResource = 0;
    private Resources.Theme mTheme = null;
    private Context mReceiverRestrictedContext = null;
    private final Object mSync = new Object();
    private final DisplayAdjustments mDisplayAdjustments = new DisplayAdjustments();
    final ArrayList<Object> mServiceCache = new ArrayList<>();
    private Context mOuterContext = this;

    static {
        registerService(Context.ACCESSIBILITY_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.2
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object getService(ContextImpl ctx) {
                return AccessibilityManager.getInstance(ctx);
            }
        });
        registerService(Context.CAPTIONING_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.3
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object getService(ContextImpl ctx) {
                return new CaptioningManager(ctx);
            }
        });
        registerService("account", new ServiceFetcher() { // from class: android.app.ContextImpl.4
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService("account");
                IAccountManager service = IAccountManager.Stub.asInterface(b);
                return new AccountManager(ctx, service);
            }
        });
        registerService(Context.ACTIVITY_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.5
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new ActivityManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService("alarm", new ServiceFetcher() { // from class: android.app.ContextImpl.6
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService("alarm");
                IAlarmManager service = IAlarmManager.Stub.asInterface(b);
                return new AlarmManager(service, ctx);
            }
        });
        registerService(Context.AUDIO_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.7
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new AudioManager(ctx);
            }
        });
        registerService(Context.MEDIA_ROUTER_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.8
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new MediaRouter(ctx);
            }
        });
        registerService("bluetooth", new ServiceFetcher() { // from class: android.app.ContextImpl.9
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new BluetoothManager(ctx);
            }
        });
        registerService(Context.CLIPBOARD_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.10
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new ClipboardManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.CONNECTIVITY_SERVICE, new StaticServiceFetcher() { // from class: android.app.ContextImpl.11
            @Override // android.app.ContextImpl.StaticServiceFetcher
            public Object createStaticService() {
                IBinder b = ServiceManager.getService(Context.CONNECTIVITY_SERVICE);
                return new ConnectivityManager(IConnectivityManager.Stub.asInterface(b));
            }
        });
        registerService(Context.COUNTRY_DETECTOR, new StaticServiceFetcher() { // from class: android.app.ContextImpl.12
            @Override // android.app.ContextImpl.StaticServiceFetcher
            public Object createStaticService() {
                IBinder b = ServiceManager.getService(Context.COUNTRY_DETECTOR);
                return new CountryDetector(ICountryDetector.Stub.asInterface(b));
            }
        });
        registerService(Context.DEVICE_POLICY_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.13
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return DevicePolicyManager.create(ctx, ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.DOWNLOAD_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.14
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new DownloadManager(ctx.getContentResolver(), ctx.getPackageName());
            }
        });
        registerService("nfc", new ServiceFetcher() { // from class: android.app.ContextImpl.15
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new NfcManager(ctx);
            }
        });
        registerService(Context.DROPBOX_SERVICE, new StaticServiceFetcher() { // from class: android.app.ContextImpl.16
            @Override // android.app.ContextImpl.StaticServiceFetcher
            public Object createStaticService() {
                return ContextImpl.createDropBoxManager();
            }
        });
        registerService(Context.INPUT_SERVICE, new StaticServiceFetcher() { // from class: android.app.ContextImpl.17
            @Override // android.app.ContextImpl.StaticServiceFetcher
            public Object createStaticService() {
                return InputManager.getInstance();
            }
        });
        registerService(Context.DISPLAY_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.18
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new DisplayManager(ctx.getOuterContext());
            }
        });
        registerService(Context.INPUT_METHOD_SERVICE, new StaticServiceFetcher() { // from class: android.app.ContextImpl.19
            @Override // android.app.ContextImpl.StaticServiceFetcher
            public Object createStaticService() {
                return InputMethodManager.getInstance();
            }
        });
        registerService(Context.TEXT_SERVICES_MANAGER_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.20
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return TextServicesManager.getInstance();
            }
        });
        registerService(Context.KEYGUARD_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.21
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object getService(ContextImpl ctx) {
                return new KeyguardManager();
            }
        });
        registerService(Context.LAYOUT_INFLATER_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.22
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return PolicyManager.makeNewLayoutInflater(ctx.getOuterContext());
            }
        });
        registerService("location", new ServiceFetcher() { // from class: android.app.ContextImpl.23
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService("location");
                return new LocationManager(ctx, ILocationManager.Stub.asInterface(b));
            }
        });
        registerService(Context.NETWORK_POLICY_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.24
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new NetworkPolicyManager(INetworkPolicyManager.Stub.asInterface(ServiceManager.getService(Context.NETWORK_POLICY_SERVICE)));
            }
        });
        registerService(Context.NOTIFICATION_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.25
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                Context outerContext = ctx.getOuterContext();
                return new NotificationManager(new ContextThemeWrapper(outerContext, Resources.selectSystemTheme(0, outerContext.getApplicationInfo().targetSdkVersion, 16973835, 16973935, 16974126)), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.NSD_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.26
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.NSD_SERVICE);
                INsdManager service = INsdManager.Stub.asInterface(b);
                return new NsdManager(ctx.getOuterContext(), service);
            }
        });
        registerService(Context.POWER_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.27
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.POWER_SERVICE);
                IPowerManager service = IPowerManager.Stub.asInterface(b);
                return new PowerManager(ctx.getOuterContext(), service, ctx.mMainThread.getHandler());
            }
        });
        registerService("search", new ServiceFetcher() { // from class: android.app.ContextImpl.28
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new SearchManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.SENSOR_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.29
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new SystemSensorManager(ctx.getOuterContext(), ctx.mMainThread.getHandler().getLooper());
            }
        });
        registerService(Context.STATUS_BAR_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.30
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new StatusBarManager(ctx.getOuterContext());
            }
        });
        registerService(Context.STORAGE_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.31
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                try {
                    return new StorageManager(ctx.getContentResolver(), ctx.mMainThread.getHandler().getLooper());
                } catch (RemoteException rex) {
                    Log.e(ContextImpl.TAG, "Failed to create StorageManager", rex);
                    return null;
                }
            }
        });
        registerService("phone", new ServiceFetcher() { // from class: android.app.ContextImpl.32
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new TelephonyManager(ctx.getOuterContext());
            }
        });
        registerService(Context.UI_MODE_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.33
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new UiModeManager();
            }
        });
        registerService(Context.USB_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.34
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.USB_SERVICE);
                return new UsbManager(ctx, IUsbManager.Stub.asInterface(b));
            }
        });
        registerService(Context.SERIAL_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.35
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.SERIAL_SERVICE);
                return new SerialManager(ctx, ISerialManager.Stub.asInterface(b));
            }
        });
        registerService(Context.VIBRATOR_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.36
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new SystemVibrator(ctx);
            }
        });
        registerService(Context.WALLPAPER_SERVICE, WALLPAPER_FETCHER);
        registerService("wifi", new ServiceFetcher() { // from class: android.app.ContextImpl.37
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService("wifi");
                IWifiManager service = IWifiManager.Stub.asInterface(b);
                return new WifiManager(ctx.getOuterContext(), service);
            }
        });
        registerService(Context.WIFI_P2P_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.38
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.WIFI_P2P_SERVICE);
                IWifiP2pManager service = IWifiP2pManager.Stub.asInterface(b);
                return new WifiP2pManager(service);
            }
        });
        registerService(Context.WINDOW_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.39
            Display mDefaultDisplay;

            @Override // android.app.ContextImpl.ServiceFetcher
            public Object getService(ContextImpl ctx) {
                Display display = ctx.mDisplay;
                if (display == null) {
                    if (this.mDefaultDisplay == null) {
                        DisplayManager dm = (DisplayManager) ctx.getOuterContext().getSystemService(Context.DISPLAY_SERVICE);
                        this.mDefaultDisplay = dm.getDisplay(0);
                    }
                    display = this.mDefaultDisplay;
                }
                return new WindowManagerImpl(display);
            }
        });
        registerService("user", new ServiceFetcher() { // from class: android.app.ContextImpl.40
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService("user");
                IUserManager service = IUserManager.Stub.asInterface(b);
                return new UserManager(ctx, service);
            }
        });
        registerService(Context.APP_OPS_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.41
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.APP_OPS_SERVICE);
                IAppOpsService service = IAppOpsService.Stub.asInterface(b);
                return new AppOpsManager(ctx, service);
            }
        });
        registerService(Context.CAMERA_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.42
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new CameraManager(ctx);
            }
        });
        registerService(Context.PRINT_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.43
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                IBinder iBinder = ServiceManager.getService(Context.PRINT_SERVICE);
                IPrintManager service = IPrintManager.Stub.asInterface(iBinder);
                return new PrintManager(ctx.getOuterContext(), service, UserHandle.myUserId(), UserHandle.getAppId(Process.myUid()));
            }
        });
        registerService(Context.CONSUMER_IR_SERVICE, new ServiceFetcher() { // from class: android.app.ContextImpl.44
            @Override // android.app.ContextImpl.ServiceFetcher
            public Object createService(ContextImpl ctx) {
                return new ConsumerIrManager(ctx);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ContextImpl$ServiceFetcher.class */
    public static class ServiceFetcher {
        int mContextCacheIndex = -1;

        ServiceFetcher() {
        }

        public Object getService(ContextImpl ctx) {
            ArrayList<Object> cache = ctx.mServiceCache;
            synchronized (cache) {
                if (cache.size() == 0) {
                    for (int i = 0; i < ContextImpl.sNextPerContextServiceCacheIndex; i++) {
                        cache.add(null);
                    }
                } else {
                    Object service = cache.get(this.mContextCacheIndex);
                    if (service != null) {
                        return service;
                    }
                }
                Object service2 = createService(ctx);
                cache.set(this.mContextCacheIndex, service2);
                return service2;
            }
        }

        public Object createService(ContextImpl ctx) {
            throw new RuntimeException("Not implemented");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ContextImpl$StaticServiceFetcher.class */
    public static abstract class StaticServiceFetcher extends ServiceFetcher {
        private Object mCachedInstance;

        public abstract Object createStaticService();

        StaticServiceFetcher() {
        }

        @Override // android.app.ContextImpl.ServiceFetcher
        public final Object getService(ContextImpl unused) {
            synchronized (this) {
                Object service = this.mCachedInstance;
                if (service != null) {
                    return service;
                }
                Object createStaticService = createStaticService();
                this.mCachedInstance = createStaticService;
                return createStaticService;
            }
        }
    }

    private static void registerService(String serviceName, ServiceFetcher fetcher) {
        if (!(fetcher instanceof StaticServiceFetcher)) {
            int i = sNextPerContextServiceCacheIndex;
            sNextPerContextServiceCacheIndex = i + 1;
            fetcher.mContextCacheIndex = i;
        }
        SYSTEM_SERVICE_MAP.put(serviceName, fetcher);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ContextImpl getImpl(Context context) {
        Context nextContext;
        while ((context instanceof ContextWrapper) && (nextContext = ((ContextWrapper) context).getBaseContext()) != null) {
            context = nextContext;
        }
        return (ContextImpl) context;
    }

    @Override // android.content.Context
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    @Override // android.content.Context
    public Resources getResources() {
        return this.mResources;
    }

    @Override // android.content.Context
    public PackageManager getPackageManager() {
        if (this.mPackageManager != null) {
            return this.mPackageManager;
        }
        IPackageManager pm = ActivityThread.getPackageManager();
        if (pm != null) {
            ApplicationPackageManager applicationPackageManager = new ApplicationPackageManager(this, pm);
            this.mPackageManager = applicationPackageManager;
            return applicationPackageManager;
        }
        return null;
    }

    @Override // android.content.Context
    public ContentResolver getContentResolver() {
        return this.mContentResolver;
    }

    @Override // android.content.Context
    public Looper getMainLooper() {
        return this.mMainThread.getLooper();
    }

    @Override // android.content.Context
    public Context getApplicationContext() {
        return this.mPackageInfo != null ? this.mPackageInfo.getApplication() : this.mMainThread.getApplication();
    }

    @Override // android.content.Context
    public void setTheme(int resid) {
        this.mThemeResource = resid;
    }

    @Override // android.content.Context
    public int getThemeResId() {
        return this.mThemeResource;
    }

    @Override // android.content.Context
    public Resources.Theme getTheme() {
        if (this.mTheme == null) {
            this.mThemeResource = Resources.selectDefaultTheme(this.mThemeResource, getOuterContext().getApplicationInfo().targetSdkVersion);
            this.mTheme = this.mResources.newTheme();
            this.mTheme.applyStyle(this.mThemeResource, true);
        }
        return this.mTheme;
    }

    @Override // android.content.Context
    public ClassLoader getClassLoader() {
        return this.mPackageInfo != null ? this.mPackageInfo.getClassLoader() : ClassLoader.getSystemClassLoader();
    }

    @Override // android.content.Context
    public String getPackageName() {
        if (this.mPackageInfo != null) {
            return this.mPackageInfo.getPackageName();
        }
        return "android";
    }

    @Override // android.content.Context
    public String getBasePackageName() {
        return this.mBasePackageName != null ? this.mBasePackageName : getPackageName();
    }

    @Override // android.content.Context
    public String getOpPackageName() {
        return this.mOpPackageName != null ? this.mOpPackageName : getBasePackageName();
    }

    @Override // android.content.Context
    public ApplicationInfo getApplicationInfo() {
        if (this.mPackageInfo != null) {
            return this.mPackageInfo.getApplicationInfo();
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public String getPackageResourcePath() {
        if (this.mPackageInfo != null) {
            return this.mPackageInfo.getResDir();
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public String getPackageCodePath() {
        if (this.mPackageInfo != null) {
            return this.mPackageInfo.getAppDir();
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public File getSharedPrefsFile(String name) {
        return makeFilename(getPreferencesDir(), name + ".xml");
    }

    @Override // android.content.Context
    public SharedPreferences getSharedPreferences(String name, int mode) {
        synchronized (ContextImpl.class) {
            if (sSharedPrefs == null) {
                sSharedPrefs = new ArrayMap<>();
            }
            String packageName = getPackageName();
            ArrayMap<String, SharedPreferencesImpl> packagePrefs = sSharedPrefs.get(packageName);
            if (packagePrefs == null) {
                packagePrefs = new ArrayMap<>();
                sSharedPrefs.put(packageName, packagePrefs);
            }
            if (this.mPackageInfo.getApplicationInfo().targetSdkVersion < 19 && name == null) {
                name = "null";
            }
            SharedPreferencesImpl sp = packagePrefs.get(name);
            if (sp == null) {
                File prefsFile = getSharedPrefsFile(name);
                SharedPreferencesImpl sp2 = new SharedPreferencesImpl(prefsFile, mode);
                packagePrefs.put(name, sp2);
                return sp2;
            }
            if ((mode & 4) != 0 || getApplicationInfo().targetSdkVersion < 11) {
                sp.startReloadIfChangedUnexpectedly();
            }
            return sp;
        }
    }

    private File getPreferencesDir() {
        File file;
        synchronized (this.mSync) {
            if (this.mPreferencesDir == null) {
                this.mPreferencesDir = new File(getDataDirFile(), "shared_prefs");
            }
            file = this.mPreferencesDir;
        }
        return file;
    }

    @Override // android.content.Context
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        File f = makeFilename(getFilesDir(), name);
        return new FileInputStream(f);
    }

    @Override // android.content.Context
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        boolean append = (mode & 32768) != 0;
        File f = makeFilename(getFilesDir(), name);
        try {
            FileOutputStream fos = new FileOutputStream(f, append);
            setFilePermissionsFromMode(f.getPath(), mode, 0);
            return fos;
        } catch (FileNotFoundException e) {
            File parent = f.getParentFile();
            parent.mkdir();
            FileUtils.setPermissions(parent.getPath(), 505, -1, -1);
            FileOutputStream fos2 = new FileOutputStream(f, append);
            setFilePermissionsFromMode(f.getPath(), mode, 0);
            return fos2;
        }
    }

    @Override // android.content.Context
    public boolean deleteFile(String name) {
        File f = makeFilename(getFilesDir(), name);
        return f.delete();
    }

    @Override // android.content.Context
    public File getFilesDir() {
        synchronized (this.mSync) {
            if (this.mFilesDir == null) {
                this.mFilesDir = new File(getDataDirFile(), "files");
            }
            if (!this.mFilesDir.exists()) {
                if (!this.mFilesDir.mkdirs()) {
                    if (this.mFilesDir.exists()) {
                        return this.mFilesDir;
                    }
                    Log.w(TAG, "Unable to create files directory " + this.mFilesDir.getPath());
                    return null;
                }
                FileUtils.setPermissions(this.mFilesDir.getPath(), 505, -1, -1);
            }
            return this.mFilesDir;
        }
    }

    @Override // android.content.Context
    public File getExternalFilesDir(String type) {
        return getExternalFilesDirs(type)[0];
    }

    @Override // android.content.Context
    public File[] getExternalFilesDirs(String type) {
        File[] ensureDirsExistOrFilter;
        synchronized (this.mSync) {
            if (this.mExternalFilesDirs == null) {
                this.mExternalFilesDirs = Environment.buildExternalStorageAppFilesDirs(getPackageName());
            }
            File[] dirs = this.mExternalFilesDirs;
            if (type != null) {
                dirs = Environment.buildPaths(dirs, type);
            }
            ensureDirsExistOrFilter = ensureDirsExistOrFilter(dirs);
        }
        return ensureDirsExistOrFilter;
    }

    @Override // android.content.Context
    public File getObbDir() {
        return getObbDirs()[0];
    }

    @Override // android.content.Context
    public File[] getObbDirs() {
        File[] ensureDirsExistOrFilter;
        synchronized (this.mSync) {
            if (this.mExternalObbDirs == null) {
                this.mExternalObbDirs = Environment.buildExternalStorageAppObbDirs(getPackageName());
            }
            ensureDirsExistOrFilter = ensureDirsExistOrFilter(this.mExternalObbDirs);
        }
        return ensureDirsExistOrFilter;
    }

    @Override // android.content.Context
    public File getCacheDir() {
        synchronized (this.mSync) {
            if (this.mCacheDir == null) {
                this.mCacheDir = new File(getDataDirFile(), "cache");
            }
            if (!this.mCacheDir.exists()) {
                if (!this.mCacheDir.mkdirs()) {
                    if (this.mCacheDir.exists()) {
                        return this.mCacheDir;
                    }
                    Log.w(TAG, "Unable to create cache directory " + this.mCacheDir.getAbsolutePath());
                    return null;
                }
                FileUtils.setPermissions(this.mCacheDir.getPath(), 505, -1, -1);
            }
            return this.mCacheDir;
        }
    }

    @Override // android.content.Context
    public File getExternalCacheDir() {
        return getExternalCacheDirs()[0];
    }

    @Override // android.content.Context
    public File[] getExternalCacheDirs() {
        File[] ensureDirsExistOrFilter;
        synchronized (this.mSync) {
            if (this.mExternalCacheDirs == null) {
                this.mExternalCacheDirs = Environment.buildExternalStorageAppCacheDirs(getPackageName());
            }
            ensureDirsExistOrFilter = ensureDirsExistOrFilter(this.mExternalCacheDirs);
        }
        return ensureDirsExistOrFilter;
    }

    @Override // android.content.Context
    public File getFileStreamPath(String name) {
        return makeFilename(getFilesDir(), name);
    }

    @Override // android.content.Context
    public String[] fileList() {
        String[] list = getFilesDir().list();
        return list != null ? list : EMPTY_FILE_LIST;
    }

    @Override // android.content.Context
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return openOrCreateDatabase(name, mode, factory, null);
    }

    @Override // android.content.Context
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        File f = validateFilePath(name, true);
        int flags = 268435456;
        if ((mode & 8) != 0) {
            flags = 268435456 | 536870912;
        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(f.getPath(), factory, flags, errorHandler);
        setFilePermissionsFromMode(f.getPath(), mode, 0);
        return db;
    }

    @Override // android.content.Context
    public boolean deleteDatabase(String name) {
        try {
            File f = validateFilePath(name, false);
            return SQLiteDatabase.deleteDatabase(f);
        } catch (Exception e) {
            return false;
        }
    }

    @Override // android.content.Context
    public File getDatabasePath(String name) {
        return validateFilePath(name, false);
    }

    @Override // android.content.Context
    public String[] databaseList() {
        String[] list = getDatabasesDir().list();
        return list != null ? list : EMPTY_FILE_LIST;
    }

    private File getDatabasesDir() {
        File file;
        synchronized (this.mSync) {
            if (this.mDatabasesDir == null) {
                this.mDatabasesDir = new File(getDataDirFile(), "databases");
            }
            if (this.mDatabasesDir.getPath().equals("databases")) {
                this.mDatabasesDir = new File("/data/system");
            }
            file = this.mDatabasesDir;
        }
        return file;
    }

    @Override // android.content.Context
    public Drawable getWallpaper() {
        return getWallpaperManager().getDrawable();
    }

    @Override // android.content.Context
    public Drawable peekWallpaper() {
        return getWallpaperManager().peekDrawable();
    }

    @Override // android.content.Context
    public int getWallpaperDesiredMinimumWidth() {
        return getWallpaperManager().getDesiredMinimumWidth();
    }

    @Override // android.content.Context
    public int getWallpaperDesiredMinimumHeight() {
        return getWallpaperManager().getDesiredMinimumHeight();
    }

    @Override // android.content.Context
    public void setWallpaper(Bitmap bitmap) throws IOException {
        getWallpaperManager().setBitmap(bitmap);
    }

    @Override // android.content.Context
    public void setWallpaper(InputStream data) throws IOException {
        getWallpaperManager().setStream(data);
    }

    @Override // android.content.Context
    public void clearWallpaper() throws IOException {
        getWallpaperManager().clear();
    }

    @Override // android.content.Context
    public void startActivity(Intent intent) {
        warnIfCallingFromSystemProcess();
        startActivity(intent, null);
    }

    @Override // android.content.Context
    public void startActivityAsUser(Intent intent, UserHandle user) {
        startActivityAsUser(intent, null, user);
    }

    @Override // android.content.Context
    public void startActivity(Intent intent, Bundle options) {
        warnIfCallingFromSystemProcess();
        if ((intent.getFlags() & 268435456) == 0) {
            throw new AndroidRuntimeException("Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?");
        }
        this.mMainThread.getInstrumentation().execStartActivity(getOuterContext(), this.mMainThread.getApplicationThread(), (IBinder) null, (Activity) null, intent, -1, options);
    }

    @Override // android.content.Context
    public void startActivityAsUser(Intent intent, Bundle options, UserHandle user) {
        try {
            ActivityManagerNative.getDefault().startActivityAsUser(this.mMainThread.getApplicationThread(), getBasePackageName(), intent, intent.resolveTypeIfNeeded(getContentResolver()), null, null, 0, 268435456, null, null, options, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void startActivities(Intent[] intents) {
        warnIfCallingFromSystemProcess();
        startActivities(intents, null);
    }

    @Override // android.content.Context
    public void startActivitiesAsUser(Intent[] intents, Bundle options, UserHandle userHandle) {
        if ((intents[0].getFlags() & 268435456) == 0) {
            throw new AndroidRuntimeException("Calling startActivities() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag on first Intent. Is this really what you want?");
        }
        this.mMainThread.getInstrumentation().execStartActivitiesAsUser(getOuterContext(), this.mMainThread.getApplicationThread(), null, null, intents, options, userHandle.getIdentifier());
    }

    @Override // android.content.Context
    public void startActivities(Intent[] intents, Bundle options) {
        warnIfCallingFromSystemProcess();
        if ((intents[0].getFlags() & 268435456) == 0) {
            throw new AndroidRuntimeException("Calling startActivities() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag on first Intent. Is this really what you want?");
        }
        this.mMainThread.getInstrumentation().execStartActivities(getOuterContext(), this.mMainThread.getApplicationThread(), null, null, intents, options);
    }

    @Override // android.content.Context
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, null);
    }

    @Override // android.content.Context
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        String resolvedType = null;
        if (fillInIntent != null) {
            try {
                fillInIntent.migrateExtraStreamToClipData();
                fillInIntent.prepareToLeaveProcess();
                resolvedType = fillInIntent.resolveTypeIfNeeded(getContentResolver());
            } catch (RemoteException e) {
                return;
            }
        }
        int result = ActivityManagerNative.getDefault().startActivityIntentSender(this.mMainThread.getApplicationThread(), intent, fillInIntent, resolvedType, null, null, 0, flagsMask, flagsValues, options);
        if (result == -6) {
            throw new IntentSender.SendIntentException();
        }
        Instrumentation.checkStartActivityResult(result, null);
    }

    @Override // android.content.Context
    public void sendBroadcast(Intent intent) {
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, null, -1, false, false, getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendBroadcast(Intent intent, String receiverPermission) {
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, receiverPermission, -1, false, false, getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendBroadcast(Intent intent, String receiverPermission, int appOp) {
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, receiverPermission, appOp, false, false, getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, receiverPermission, -1, true, false, getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        sendOrderedBroadcast(intent, receiverPermission, -1, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override // android.content.Context
    public void sendOrderedBroadcast(Intent intent, String receiverPermission, int appOp, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        warnIfCallingFromSystemProcess();
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (this.mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = this.mPackageInfo.getReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, this.mMainThread.getInstrumentation(), false);
            } else {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = new LoadedApk.ReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, null, false).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, rd, initialCode, initialData, initialExtras, receiverPermission, appOp, true, false, getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, null, -1, false, false, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, receiverPermission, -1, false, false, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (this.mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = this.mPackageInfo.getReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, this.mMainThread.getInstrumentation(), false);
            } else {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = new LoadedApk.ReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, null, false).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, rd, initialCode, initialData, initialExtras, receiverPermission, -1, true, false, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendStickyBroadcast(Intent intent) {
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, null, -1, false, true, getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        warnIfCallingFromSystemProcess();
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (this.mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = this.mPackageInfo.getReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, this.mMainThread.getInstrumentation(), false);
            } else {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = new LoadedApk.ReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, null, false).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, rd, initialCode, initialData, initialExtras, null, -1, true, true, getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void removeStickyBroadcast(Intent intent) {
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        if (resolvedType != null) {
            intent = new Intent(intent);
            intent.setDataAndType(intent.getData(), resolvedType);
        }
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().unbroadcastIntent(this.mMainThread.getApplicationThread(), intent, getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, null, -1, false, true, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (this.mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = this.mPackageInfo.getReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, this.mMainThread.getInstrumentation(), false);
            } else {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = new LoadedApk.ReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, null, false).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, rd, initialCode, initialData, initialExtras, null, -1, true, true, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        if (resolvedType != null) {
            intent = new Intent(intent);
            intent.setDataAndType(intent.getData(), resolvedType);
        }
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().unbroadcastIntent(this.mMainThread.getApplicationThread(), intent, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return registerReceiver(receiver, filter, null, null);
    }

    @Override // android.content.Context
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return registerReceiverInternal(receiver, getUserId(), filter, broadcastPermission, scheduler, getOuterContext());
    }

    @Override // android.content.Context
    public Intent registerReceiverAsUser(BroadcastReceiver receiver, UserHandle user, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return registerReceiverInternal(receiver, user.getIdentifier(), filter, broadcastPermission, scheduler, getOuterContext());
    }

    private Intent registerReceiverInternal(BroadcastReceiver receiver, int userId, IntentFilter filter, String broadcastPermission, Handler scheduler, Context context) {
        IIntentReceiver rd = null;
        if (receiver != null) {
            if (this.mPackageInfo != null && context != null) {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = this.mPackageInfo.getReceiverDispatcher(receiver, context, scheduler, this.mMainThread.getInstrumentation(), true);
            } else {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = new LoadedApk.ReceiverDispatcher(receiver, context, scheduler, null, true).getIIntentReceiver();
            }
        }
        try {
            return ActivityManagerNative.getDefault().registerReceiver(this.mMainThread.getApplicationThread(), this.mBasePackageName, rd, filter, broadcastPermission, userId);
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.content.Context
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (this.mPackageInfo != null) {
            IIntentReceiver rd = this.mPackageInfo.forgetReceiverDispatcher(getOuterContext(), receiver);
            try {
                ActivityManagerNative.getDefault().unregisterReceiver(rd);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        throw new RuntimeException("Not supported in system context");
    }

    private void validateServiceIntent(Intent service) {
        if (service.getComponent() == null && service.getPackage() == null) {
            Log.w(TAG, "Implicit intents with startService are not safe: " + service + Separators.SP + Debug.getCallers(2, 3));
        }
    }

    @Override // android.content.Context
    public ComponentName startService(Intent service) {
        warnIfCallingFromSystemProcess();
        return startServiceCommon(service, this.mUser);
    }

    @Override // android.content.Context
    public boolean stopService(Intent service) {
        warnIfCallingFromSystemProcess();
        return stopServiceCommon(service, this.mUser);
    }

    @Override // android.content.Context
    public ComponentName startServiceAsUser(Intent service, UserHandle user) {
        return startServiceCommon(service, user);
    }

    private ComponentName startServiceCommon(Intent service, UserHandle user) {
        try {
            validateServiceIntent(service);
            service.prepareToLeaveProcess();
            ComponentName cn = ActivityManagerNative.getDefault().startService(this.mMainThread.getApplicationThread(), service, service.resolveTypeIfNeeded(getContentResolver()), user.getIdentifier());
            if (cn != null) {
                if (cn.getPackageName().equals("!")) {
                    throw new SecurityException("Not allowed to start service " + service + " without permission " + cn.getClassName());
                }
                if (cn.getPackageName().equals("!!")) {
                    throw new SecurityException("Unable to start service " + service + ": " + cn.getClassName());
                }
            }
            return cn;
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.content.Context
    public boolean stopServiceAsUser(Intent service, UserHandle user) {
        return stopServiceCommon(service, user);
    }

    private boolean stopServiceCommon(Intent service, UserHandle user) {
        try {
            validateServiceIntent(service);
            service.prepareToLeaveProcess();
            int res = ActivityManagerNative.getDefault().stopService(this.mMainThread.getApplicationThread(), service, service.resolveTypeIfNeeded(getContentResolver()), user.getIdentifier());
            if (res < 0) {
                throw new SecurityException("Not allowed to stop service " + service);
            }
            return res != 0;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.content.Context
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        warnIfCallingFromSystemProcess();
        return bindServiceCommon(service, conn, flags, Process.myUserHandle());
    }

    @Override // android.content.Context
    public boolean bindServiceAsUser(Intent service, ServiceConnection conn, int flags, UserHandle user) {
        return bindServiceCommon(service, conn, flags, user);
    }

    private boolean bindServiceCommon(Intent service, ServiceConnection conn, int flags, UserHandle user) {
        if (conn == null) {
            throw new IllegalArgumentException("connection is null");
        }
        if (this.mPackageInfo != null) {
            IServiceConnection sd = this.mPackageInfo.getServiceDispatcher(conn, getOuterContext(), this.mMainThread.getHandler(), flags);
            validateServiceIntent(service);
            try {
                IBinder token = getActivityToken();
                if (token == null && (flags & 1) == 0 && this.mPackageInfo != null && this.mPackageInfo.getApplicationInfo().targetSdkVersion < 14) {
                    flags |= 32;
                }
                service.prepareToLeaveProcess();
                int res = ActivityManagerNative.getDefault().bindService(this.mMainThread.getApplicationThread(), getActivityToken(), service, service.resolveTypeIfNeeded(getContentResolver()), sd, flags, user.getIdentifier());
                if (res < 0) {
                    throw new SecurityException("Not allowed to bind to service " + service);
                }
                return res != 0;
            } catch (RemoteException e) {
                return false;
            }
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public void unbindService(ServiceConnection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("connection is null");
        }
        if (this.mPackageInfo != null) {
            IServiceConnection sd = this.mPackageInfo.forgetServiceDispatcher(getOuterContext(), conn);
            try {
                ActivityManagerNative.getDefault().unbindService(sd);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        if (arguments != null) {
            try {
                arguments.setAllowFds(false);
            } catch (RemoteException e) {
                return false;
            }
        }
        return ActivityManagerNative.getDefault().startInstrumentation(className, profileFile, 0, arguments, null, null, getUserId());
    }

    @Override // android.content.Context
    public Object getSystemService(String name) {
        ServiceFetcher fetcher = SYSTEM_SERVICE_MAP.get(name);
        if (fetcher == null) {
            return null;
        }
        return fetcher.getService(this);
    }

    private WallpaperManager getWallpaperManager() {
        return (WallpaperManager) WALLPAPER_FETCHER.getService(this);
    }

    static DropBoxManager createDropBoxManager() {
        IBinder b = ServiceManager.getService(Context.DROPBOX_SERVICE);
        IDropBoxManagerService service = IDropBoxManagerService.Stub.asInterface(b);
        if (service == null) {
            return null;
        }
        return new DropBoxManager(service);
    }

    @Override // android.content.Context
    public int checkPermission(String permission, int pid, int uid) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }
        try {
            return ActivityManagerNative.getDefault().checkPermission(permission, pid, uid);
        } catch (RemoteException e) {
            return -1;
        }
    }

    @Override // android.content.Context
    public int checkCallingPermission(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }
        int pid = Binder.getCallingPid();
        if (pid != Process.myPid()) {
            return checkPermission(permission, pid, Binder.getCallingUid());
        }
        return -1;
    }

    @Override // android.content.Context
    public int checkCallingOrSelfPermission(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }
        return checkPermission(permission, Binder.getCallingPid(), Binder.getCallingUid());
    }

    private void enforce(String permission, int resultOfCheck, boolean selfToo, int uid, String message) {
        if (resultOfCheck != 0) {
            throw new SecurityException((message != null ? message + ": " : "") + (selfToo ? "Neither user " + uid + " nor current process has " : "uid " + uid + " does not have ") + permission + Separators.DOT);
        }
    }

    @Override // android.content.Context
    public void enforcePermission(String permission, int pid, int uid, String message) {
        enforce(permission, checkPermission(permission, pid, uid), false, uid, message);
    }

    @Override // android.content.Context
    public void enforceCallingPermission(String permission, String message) {
        enforce(permission, checkCallingPermission(permission), false, Binder.getCallingUid(), message);
    }

    @Override // android.content.Context
    public void enforceCallingOrSelfPermission(String permission, String message) {
        enforce(permission, checkCallingOrSelfPermission(permission), true, Binder.getCallingUid(), message);
    }

    @Override // android.content.Context
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        try {
            ActivityManagerNative.getDefault().grantUriPermission(this.mMainThread.getApplicationThread(), toPackage, uri, modeFlags);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public void revokeUriPermission(Uri uri, int modeFlags) {
        try {
            ActivityManagerNative.getDefault().revokeUriPermission(this.mMainThread.getApplicationThread(), uri, modeFlags);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.Context
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        try {
            return ActivityManagerNative.getDefault().checkUriPermission(uri, pid, uid, modeFlags);
        } catch (RemoteException e) {
            return -1;
        }
    }

    @Override // android.content.Context
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        int pid = Binder.getCallingPid();
        if (pid != Process.myPid()) {
            return checkUriPermission(uri, pid, Binder.getCallingUid(), modeFlags);
        }
        return -1;
    }

    @Override // android.content.Context
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return checkUriPermission(uri, Binder.getCallingPid(), Binder.getCallingUid(), modeFlags);
    }

    @Override // android.content.Context
    public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags) {
        if ((modeFlags & 1) != 0 && (readPermission == null || checkPermission(readPermission, pid, uid) == 0)) {
            return 0;
        }
        if ((modeFlags & 2) != 0 && (writePermission == null || checkPermission(writePermission, pid, uid) == 0)) {
            return 0;
        }
        if (uri != null) {
            return checkUriPermission(uri, pid, uid, modeFlags);
        }
        return -1;
    }

    private String uriModeFlagToString(int uriModeFlags) {
        switch (uriModeFlags) {
            case 1:
                return "read";
            case 2:
                return "write";
            case 3:
                return "read and write";
            default:
                throw new IllegalArgumentException("Unknown permission mode flags: " + uriModeFlags);
        }
    }

    private void enforceForUri(int modeFlags, int resultOfCheck, boolean selfToo, int uid, Uri uri, String message) {
        if (resultOfCheck != 0) {
            throw new SecurityException((message != null ? message + ": " : "") + (selfToo ? "Neither user " + uid + " nor current process has " : "User " + uid + " does not have ") + uriModeFlagToString(modeFlags) + " permission on " + uri + Separators.DOT);
        }
    }

    @Override // android.content.Context
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
        enforceForUri(modeFlags, checkUriPermission(uri, pid, uid, modeFlags), false, uid, uri, message);
    }

    @Override // android.content.Context
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
        enforceForUri(modeFlags, checkCallingUriPermission(uri, modeFlags), false, Binder.getCallingUid(), uri, message);
    }

    @Override // android.content.Context
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
        enforceForUri(modeFlags, checkCallingOrSelfUriPermission(uri, modeFlags), true, Binder.getCallingUid(), uri, message);
    }

    @Override // android.content.Context
    public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message) {
        enforceForUri(modeFlags, checkUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags), false, uid, uri, message);
    }

    private void warnIfCallingFromSystemProcess() {
        if (Process.myUid() == 1000) {
            Slog.w(TAG, "Calling a method in the system process without a qualified user: " + Debug.getCallers(5));
        }
    }

    @Override // android.content.Context
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        return createPackageContextAsUser(packageName, flags, this.mUser != null ? this.mUser : Process.myUserHandle());
    }

    @Override // android.content.Context
    public Context createPackageContextAsUser(String packageName, int flags, UserHandle user) throws PackageManager.NameNotFoundException {
        if (packageName.equals("system") || packageName.equals("android")) {
            ContextImpl context = new ContextImpl(this.mMainThread.getSystemContext());
            context.mRestricted = (flags & 4) == 4;
            context.init(this.mPackageInfo, null, this.mMainThread, this.mResources, this.mBasePackageName, user);
            return context;
        }
        LoadedApk pi = this.mMainThread.getPackageInfo(packageName, this.mResources.getCompatibilityInfo(), flags, user.getIdentifier());
        if (pi != null) {
            ContextImpl c = new ContextImpl();
            c.mRestricted = (flags & 4) == 4;
            c.init(pi, null, this.mMainThread, this.mResources, this.mBasePackageName, user);
            if (c.mResources != null) {
                return c;
            }
        }
        throw new PackageManager.NameNotFoundException("Application package " + packageName + " not found");
    }

    @Override // android.content.Context
    public Context createConfigurationContext(Configuration overrideConfiguration) {
        if (overrideConfiguration == null) {
            throw new IllegalArgumentException("overrideConfiguration must not be null");
        }
        ContextImpl c = new ContextImpl();
        c.init(this.mPackageInfo, (IBinder) null, this.mMainThread);
        c.mResources = this.mResourcesManager.getTopLevelResources(this.mPackageInfo.getResDir(), getDisplayId(), overrideConfiguration, this.mResources.getCompatibilityInfo(), this.mActivityToken);
        return c;
    }

    @Override // android.content.Context
    public Context createDisplayContext(Display display) {
        if (display == null) {
            throw new IllegalArgumentException("display must not be null");
        }
        int displayId = display.getDisplayId();
        ContextImpl context = new ContextImpl();
        context.init(this.mPackageInfo, (IBinder) null, this.mMainThread);
        context.mDisplay = display;
        DisplayAdjustments daj = getDisplayAdjustments(displayId);
        context.mResources = this.mResourcesManager.getTopLevelResources(this.mPackageInfo.getResDir(), displayId, null, daj.getCompatibilityInfo(), null);
        return context;
    }

    private int getDisplayId() {
        if (this.mDisplay != null) {
            return this.mDisplay.getDisplayId();
        }
        return 0;
    }

    @Override // android.content.Context
    public boolean isRestricted() {
        return this.mRestricted;
    }

    @Override // android.content.Context
    public DisplayAdjustments getDisplayAdjustments(int displayId) {
        return this.mDisplayAdjustments;
    }

    private File getDataDirFile() {
        if (this.mPackageInfo != null) {
            return this.mPackageInfo.getDataDirFile();
        }
        throw new RuntimeException("Not supported in system context");
    }

    @Override // android.content.Context
    public File getDir(String name, int mode) {
        File file = makeFilename(getDataDirFile(), "app_" + name);
        if (!file.exists()) {
            file.mkdir();
            setFilePermissionsFromMode(file.getPath(), mode, 505);
        }
        return file;
    }

    @Override // android.content.Context
    public int getUserId() {
        return this.mUser.getIdentifier();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ContextImpl createSystemContext(ActivityThread mainThread) {
        ContextImpl context = new ContextImpl();
        context.init(Resources.getSystem(), mainThread, Process.myUserHandle());
        return context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ContextImpl() {
    }

    public ContextImpl(ContextImpl context) {
        this.mPackageInfo = context.mPackageInfo;
        this.mBasePackageName = context.mBasePackageName;
        this.mOpPackageName = context.mOpPackageName;
        this.mResources = context.mResources;
        this.mMainThread = context.mMainThread;
        this.mContentResolver = context.mContentResolver;
        this.mUser = context.mUser;
        this.mDisplay = context.mDisplay;
        this.mDisplayAdjustments.setCompatibilityInfo(this.mPackageInfo.getCompatibilityInfo());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void init(LoadedApk packageInfo, IBinder activityToken, ActivityThread mainThread) {
        init(packageInfo, activityToken, mainThread, null, null, Process.myUserHandle());
    }

    final void init(LoadedApk packageInfo, IBinder activityToken, ActivityThread mainThread, Resources container, String basePackageName, UserHandle user) {
        this.mPackageInfo = packageInfo;
        if (basePackageName != null) {
            this.mOpPackageName = basePackageName;
            this.mBasePackageName = basePackageName;
        } else {
            this.mBasePackageName = packageInfo.mPackageName;
            ApplicationInfo ainfo = packageInfo.getApplicationInfo();
            if (ainfo.uid == 1000 && ainfo.uid != Process.myUid()) {
                this.mOpPackageName = ActivityThread.currentPackageName();
            } else {
                this.mOpPackageName = this.mBasePackageName;
            }
        }
        this.mResources = this.mPackageInfo.getResources(mainThread);
        this.mResourcesManager = ResourcesManager.getInstance();
        CompatibilityInfo compatInfo = container == null ? null : container.getCompatibilityInfo();
        if (this.mResources != null && ((compatInfo != null && compatInfo.applicationScale != this.mResources.getCompatibilityInfo().applicationScale) || activityToken != null)) {
            if (compatInfo == null) {
                compatInfo = packageInfo.getCompatibilityInfo();
            }
            this.mDisplayAdjustments.setCompatibilityInfo(compatInfo);
            this.mDisplayAdjustments.setActivityToken(activityToken);
            this.mResources = this.mResourcesManager.getTopLevelResources(this.mPackageInfo.getResDir(), 0, null, compatInfo, activityToken);
        } else {
            this.mDisplayAdjustments.setCompatibilityInfo(packageInfo.getCompatibilityInfo());
            this.mDisplayAdjustments.setActivityToken(activityToken);
        }
        this.mMainThread = mainThread;
        this.mActivityToken = activityToken;
        this.mContentResolver = new ApplicationContentResolver(this, mainThread, user);
        this.mUser = user;
    }

    final void init(Resources resources, ActivityThread mainThread, UserHandle user) {
        this.mPackageInfo = null;
        this.mBasePackageName = null;
        this.mOpPackageName = null;
        this.mResources = resources;
        this.mMainThread = mainThread;
        this.mContentResolver = new ApplicationContentResolver(this, mainThread, user);
        this.mUser = user;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void scheduleFinalCleanup(String who, String what) {
        this.mMainThread.scheduleContextCleanup(this, who, what);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performFinalCleanup(String who, String what) {
        this.mPackageInfo.removeContextRegistrations(getOuterContext(), who, what);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Context getReceiverRestrictedContext() {
        if (this.mReceiverRestrictedContext != null) {
            return this.mReceiverRestrictedContext;
        }
        ReceiverRestrictedContext receiverRestrictedContext = new ReceiverRestrictedContext(getOuterContext());
        this.mReceiverRestrictedContext = receiverRestrictedContext;
        return receiverRestrictedContext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setOuterContext(Context context) {
        this.mOuterContext = context;
    }

    final Context getOuterContext() {
        return this.mOuterContext;
    }

    final IBinder getActivityToken() {
        return this.mActivityToken;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setFilePermissionsFromMode(String name, int mode, int extraPermissions) {
        int perms = 432 | extraPermissions;
        if ((mode & 1) != 0) {
            perms |= 4;
        }
        if ((mode & 2) != 0) {
            perms |= 2;
        }
        FileUtils.setPermissions(name, perms, -1, -1);
    }

    private File validateFilePath(String name, boolean createDirectory) {
        File dir;
        File f;
        if (name.charAt(0) == File.separatorChar) {
            String dirPath = name.substring(0, name.lastIndexOf(File.separatorChar));
            dir = new File(dirPath);
            f = new File(dir, name.substring(name.lastIndexOf(File.separatorChar)));
        } else {
            dir = getDatabasesDir();
            f = makeFilename(dir, name);
        }
        if (createDirectory && !dir.isDirectory() && dir.mkdir()) {
            FileUtils.setPermissions(dir.getPath(), 505, -1, -1);
        }
        return f;
    }

    private File makeFilename(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            return new File(base, name);
        }
        throw new IllegalArgumentException("File " + name + " contains a path separator");
    }

    private File[] ensureDirsExistOrFilter(File[] dirs) {
        File[] result = new File[dirs.length];
        for (int i = 0; i < dirs.length; i++) {
            File dir = dirs[i];
            if (!dir.exists() && !dir.mkdirs() && !dir.exists()) {
                IMountService mount = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
                int res = -1;
                try {
                    res = mount.mkdirs(getPackageName(), dir.getAbsolutePath());
                } catch (RemoteException e) {
                }
                if (res != 0) {
                    Log.w(TAG, "Failed to ensure directory: " + dir);
                    dir = null;
                }
            }
            result[i] = dir;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ContextImpl$ApplicationContentResolver.class */
    public static final class ApplicationContentResolver extends ContentResolver {
        private final ActivityThread mMainThread;
        private final UserHandle mUser;

        public ApplicationContentResolver(Context context, ActivityThread mainThread, UserHandle user) {
            super(context);
            this.mMainThread = (ActivityThread) Preconditions.checkNotNull(mainThread);
            this.mUser = (UserHandle) Preconditions.checkNotNull(user);
        }

        @Override // android.content.ContentResolver
        protected IContentProvider acquireProvider(Context context, String auth) {
            return this.mMainThread.acquireProvider(context, auth, this.mUser.getIdentifier(), true);
        }

        @Override // android.content.ContentResolver
        protected IContentProvider acquireExistingProvider(Context context, String auth) {
            return this.mMainThread.acquireExistingProvider(context, auth, this.mUser.getIdentifier(), true);
        }

        @Override // android.content.ContentResolver
        public boolean releaseProvider(IContentProvider provider) {
            return this.mMainThread.releaseProvider(provider, true);
        }

        @Override // android.content.ContentResolver
        protected IContentProvider acquireUnstableProvider(Context c, String auth) {
            return this.mMainThread.acquireProvider(c, auth, this.mUser.getIdentifier(), false);
        }

        @Override // android.content.ContentResolver
        public boolean releaseUnstableProvider(IContentProvider icp) {
            return this.mMainThread.releaseProvider(icp, false);
        }

        @Override // android.content.ContentResolver
        public void unstableProviderDied(IContentProvider icp) {
            this.mMainThread.handleUnstableProviderDied(icp.asBinder(), true);
        }

        @Override // android.content.ContentResolver
        public void appNotRespondingViaProvider(IContentProvider icp) {
            this.mMainThread.appNotRespondingViaProvider(icp.asBinder());
        }
    }
}