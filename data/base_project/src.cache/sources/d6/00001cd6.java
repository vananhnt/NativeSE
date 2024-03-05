package com.android.server;

import android.app.ActivityManagerNative;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioService;
import android.net.wifi.p2p.WifiP2pService;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.service.dreams.DreamService;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.view.WindowManager;
import com.android.internal.R;
import com.android.internal.os.BinderInternal;
import com.android.server.accessibility.AccessibilityManagerService;
import com.android.server.accounts.AccountManagerService;
import com.android.server.am.ActivityManagerService;
import com.android.server.am.BatteryStatsService;
import com.android.server.content.ContentService;
import com.android.server.display.DisplayManagerService;
import com.android.server.dreams.DreamManagerService;
import com.android.server.input.InputManagerService;
import com.android.server.net.NetworkPolicyManagerService;
import com.android.server.net.NetworkStatsService;
import com.android.server.os.SchedulingPolicyService;
import com.android.server.pm.Installer;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.UserManagerService;
import com.android.server.power.PowerManagerService;
import com.android.server.power.ShutdownThread;
import com.android.server.print.PrintManagerService;
import com.android.server.search.SearchManagerService;
import com.android.server.usb.UsbService;
import com.android.server.wifi.WifiService;
import com.android.server.wm.WindowManagerService;
import dalvik.system.VMRuntime;
import dalvik.system.Zygote;
import java.io.File;

/* compiled from: SystemServer.java */
/* loaded from: ServerThread.class */
class ServerThread {
    private static final String TAG = "SystemServer";
    private static final String ENCRYPTING_STATE = "trigger_restart_min_framework";
    private static final String ENCRYPTED_STATE = "1";
    ContentResolver mContentResolver;

    void reportWtf(String msg, Throwable e) {
        Slog.w(TAG, "***********************************************");
        Log.wtf(TAG, "BOOT FAILURE " + msg, e);
    }

    public void initAndLoop() {
        String reason;
        EventLog.writeEvent(3010, SystemClock.uptimeMillis());
        Looper.prepareMainLooper();
        Process.setThreadPriority(-2);
        BinderInternal.disableBackgroundScheduling(true);
        Process.setCanSelfBackground(false);
        String shutdownAction = SystemProperties.get(ShutdownThread.SHUTDOWN_ACTION_PROPERTY, "");
        if (shutdownAction != null && shutdownAction.length() > 0) {
            boolean reboot = shutdownAction.charAt(0) == '1';
            if (shutdownAction.length() > 1) {
                reason = shutdownAction.substring(1, shutdownAction.length());
            } else {
                reason = null;
            }
            ShutdownThread.rebootOrShutdown(reboot, reason);
        }
        String factoryTestStr = SystemProperties.get("ro.factorytest");
        int factoryTest = "".equals(factoryTestStr) ? 0 : Integer.parseInt(factoryTestStr);
        final boolean headless = ENCRYPTED_STATE.equals(SystemProperties.get("ro.config.headless", "0"));
        Installer installer = null;
        AccountManagerService accountManager = null;
        ContentService contentService = null;
        LightsService lights = null;
        PowerManagerService power = null;
        DisplayManagerService display = null;
        BatteryService battery = null;
        VibratorService vibrator = null;
        AlarmManagerService alarm = null;
        MountService mountService = null;
        NetworkManagementService networkManagement = null;
        NetworkStatsService networkStats = null;
        NetworkPolicyManagerService networkPolicy = null;
        ConnectivityService connectivity = null;
        WifiP2pService wifiP2p = null;
        WifiService wifi = null;
        IPackageManager pm = null;
        Context context = null;
        WindowManagerService wm = null;
        DockObserver dock = null;
        UsbService usb = null;
        TwilightService twilight = null;
        UiModeManagerService uiMode = null;
        RecognitionManagerService recognition = null;
        NetworkTimeUpdateService networkTimeUpdater = null;
        CommonTimeManagementService commonTimeMgmtService = null;
        InputManagerService inputManager = null;
        TelephonyRegistry telephonyRegistry = null;
        HandlerThread wmHandlerThread = new HandlerThread("WindowManager");
        wmHandlerThread.start();
        Handler wmHandler = new Handler(wmHandlerThread.getLooper());
        wmHandler.post(new Runnable() { // from class: com.android.server.ServerThread.1
            @Override // java.lang.Runnable
            public void run() {
                Process.setThreadPriority(-4);
                Process.setCanSelfBackground(false);
                if (StrictMode.conditionallyEnableDebugLogging()) {
                    Slog.i(ServerThread.TAG, "Enabled StrictMode logging for WM Looper");
                }
            }
        });
        boolean onlyCore = false;
        boolean firstBoot = false;
        try {
            Slog.i(TAG, "Waiting for installd to be ready.");
            installer = new Installer();
            installer.ping();
            Slog.i(TAG, "Power Manager");
            power = new PowerManagerService();
            ServiceManager.addService(Context.POWER_SERVICE, power);
            Slog.i(TAG, "Activity Manager");
            context = ActivityManagerService.main(factoryTest);
        } catch (RuntimeException e) {
            Slog.e("System", "******************************************");
            Slog.e("System", "************ Failure starting bootstrap service", e);
        }
        boolean disableStorage = SystemProperties.getBoolean("config.disable_storage", false);
        boolean disableMedia = SystemProperties.getBoolean("config.disable_media", false);
        boolean disableBluetooth = SystemProperties.getBoolean("config.disable_bluetooth", false);
        SystemProperties.getBoolean("config.disable_telephony", false);
        boolean disableLocation = SystemProperties.getBoolean("config.disable_location", false);
        boolean disableSystemUI = SystemProperties.getBoolean("config.disable_systemui", false);
        boolean disableNonCoreServices = SystemProperties.getBoolean("config.disable_noncore", false);
        boolean disableNetwork = SystemProperties.getBoolean("config.disable_network", false);
        try {
            Slog.i(TAG, "Display Manager");
            display = new DisplayManagerService(context, wmHandler);
            ServiceManager.addService(Context.DISPLAY_SERVICE, display, true);
            Slog.i(TAG, "Telephony Registry");
            telephonyRegistry = new TelephonyRegistry(context);
            ServiceManager.addService("telephony.registry", telephonyRegistry);
            Slog.i(TAG, "Scheduling Policy");
            ServiceManager.addService("scheduling_policy", new SchedulingPolicyService());
            AttributeCache.init(context);
            if (!display.waitForDefaultDisplay()) {
                reportWtf("Timeout waiting for default display to be initialized.", new Throwable());
            }
            Slog.i(TAG, "Package Manager");
            String cryptState = SystemProperties.get("vold.decrypt");
            if (ENCRYPTING_STATE.equals(cryptState)) {
                Slog.w(TAG, "Detected encryption in progress - only parsing core apps");
                onlyCore = true;
            } else if (ENCRYPTED_STATE.equals(cryptState)) {
                Slog.w(TAG, "Device encrypted - only parsing core apps");
                onlyCore = true;
            }
            pm = PackageManagerService.main(context, installer, factoryTest != 0, onlyCore);
            try {
                firstBoot = pm.isFirstBoot();
            } catch (RemoteException e2) {
            }
            ActivityManagerService.setSystemProcess();
            Slog.i(TAG, "Entropy Mixer");
            ServiceManager.addService("entropy", new EntropyMixer(context));
            Slog.i(TAG, "User Service");
            ServiceManager.addService("user", UserManagerService.getInstance());
            this.mContentResolver = context.getContentResolver();
            Slog.i(TAG, "Account Manager");
            accountManager = new AccountManagerService(context);
            ServiceManager.addService("account", accountManager);
            Slog.i(TAG, "Content Manager");
            contentService = ContentService.main(context, factoryTest == 1);
            Slog.i(TAG, "System Content Providers");
            ActivityManagerService.installSystemProviders();
            Slog.i(TAG, "Lights Service");
            lights = new LightsService(context);
            Slog.i(TAG, "Battery Service");
            battery = new BatteryService(context, lights);
            ServiceManager.addService("battery", battery);
            Slog.i(TAG, "Vibrator Service");
            vibrator = new VibratorService(context);
            ServiceManager.addService(Context.VIBRATOR_SERVICE, vibrator);
            Slog.i(TAG, "Consumer IR Service");
            ConsumerIrService consumerIr = new ConsumerIrService(context);
            ServiceManager.addService(Context.CONSUMER_IR_SERVICE, consumerIr);
            power.init(context, lights, ActivityManagerService.self(), battery, BatteryStatsService.getService(), ActivityManagerService.self().getAppOpsService(), display);
            Slog.i(TAG, "Alarm Manager");
            alarm = new AlarmManagerService(context);
            ServiceManager.addService("alarm", alarm);
            Slog.i(TAG, "Init Watchdog");
            Watchdog.getInstance().init(context, battery, power, alarm, ActivityManagerService.self());
            Watchdog.getInstance().addThread(wmHandler, "WindowManager thread");
            Slog.i(TAG, "Input Manager");
            inputManager = new InputManagerService(context, wmHandler);
            Slog.i(TAG, "Window Manager");
            wm = WindowManagerService.main(context, power, display, inputManager, wmHandler, factoryTest != 1, !firstBoot, onlyCore);
            ServiceManager.addService(Context.WINDOW_SERVICE, wm);
            ServiceManager.addService(Context.INPUT_SERVICE, inputManager);
            ActivityManagerService.self().setWindowManager(wm);
            inputManager.setWindowManagerCallbacks(wm.getInputMonitor());
            inputManager.start();
            display.setWindowManager(wm);
            display.setInputManager(inputManager);
            if (SystemProperties.get("ro.kernel.qemu").equals(ENCRYPTED_STATE)) {
                Slog.i(TAG, "No Bluetooh Service (emulator)");
            } else if (factoryTest == 1) {
                Slog.i(TAG, "No Bluetooth Service (factory test)");
            } else if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
                Slog.i(TAG, "No Bluetooth Service (Bluetooth Hardware Not Present)");
            } else if (disableBluetooth) {
                Slog.i(TAG, "Bluetooth Service disabled by config");
            } else {
                Slog.i(TAG, "Bluetooth Manager Service");
                BluetoothManagerService bluetooth = new BluetoothManagerService(context);
                ServiceManager.addService(BluetoothAdapter.BLUETOOTH_MANAGER_SERVICE, bluetooth);
            }
        } catch (RuntimeException e3) {
            Slog.e("System", "******************************************");
            Slog.e("System", "************ Failure starting core service", e3);
        }
        DevicePolicyManagerService devicePolicy = null;
        StatusBarManagerService statusBar = null;
        InputMethodManagerService imm = null;
        AppWidgetService appWidget = null;
        NotificationManagerService notification = null;
        WallpaperManagerService wallpaper = null;
        LocationManagerService location = null;
        CountryDetectorService countryDetector = null;
        TextServicesManagerService tsms = null;
        LockSettingsService lockSettings = null;
        DreamManagerService dreamy = null;
        AssetAtlasService atlas = null;
        PrintManagerService printManager = null;
        if (factoryTest != 1) {
            try {
                Slog.i(TAG, "Input Method Service");
                imm = new InputMethodManagerService(context, wm);
                ServiceManager.addService(Context.INPUT_METHOD_SERVICE, imm);
            } catch (Throwable e4) {
                reportWtf("starting Input Manager Service", e4);
            }
            try {
                Slog.i(TAG, "Accessibility Manager");
                ServiceManager.addService(Context.ACCESSIBILITY_SERVICE, new AccessibilityManagerService(context));
            } catch (Throwable e5) {
                reportWtf("starting Accessibility Manager", e5);
            }
        }
        try {
            wm.displayReady();
        } catch (Throwable e6) {
            reportWtf("making display ready", e6);
        }
        try {
            pm.performBootDexOpt();
        } catch (Throwable e7) {
            reportWtf("performing boot dexopt", e7);
        }
        try {
            ActivityManagerNative.getDefault().showBootMessage(context.getResources().getText(R.string.android_upgrading_starting_apps), false);
        } catch (RemoteException e8) {
        }
        if (factoryTest != 1) {
            if (!disableStorage && !"0".equals(SystemProperties.get("system_init.startmountservice"))) {
                try {
                    Slog.i(TAG, "Mount Service");
                    mountService = new MountService(context);
                    ServiceManager.addService("mount", mountService);
                } catch (Throwable e9) {
                    reportWtf("starting Mount Service", e9);
                }
            }
            if (!disableNonCoreServices) {
                try {
                    Slog.i(TAG, "LockSettingsService");
                    lockSettings = new LockSettingsService(context);
                    ServiceManager.addService("lock_settings", lockSettings);
                } catch (Throwable e10) {
                    reportWtf("starting LockSettingsService service", e10);
                }
                try {
                    Slog.i(TAG, "Device Policy");
                    devicePolicy = new DevicePolicyManagerService(context);
                    ServiceManager.addService(Context.DEVICE_POLICY_SERVICE, devicePolicy);
                } catch (Throwable e11) {
                    reportWtf("starting DevicePolicyService", e11);
                }
            }
            if (!disableSystemUI) {
                try {
                    Slog.i(TAG, "Status Bar");
                    statusBar = new StatusBarManagerService(context, wm);
                    ServiceManager.addService(Context.STATUS_BAR_SERVICE, statusBar);
                } catch (Throwable e12) {
                    reportWtf("starting StatusBarManagerService", e12);
                }
            }
            if (!disableNonCoreServices) {
                try {
                    Slog.i(TAG, "Clipboard Service");
                    ServiceManager.addService(Context.CLIPBOARD_SERVICE, new ClipboardService(context));
                } catch (Throwable e13) {
                    reportWtf("starting Clipboard Service", e13);
                }
            }
            if (!disableNetwork) {
                try {
                    Slog.i(TAG, "NetworkManagement Service");
                    networkManagement = NetworkManagementService.create(context);
                    ServiceManager.addService(Context.NETWORKMANAGEMENT_SERVICE, networkManagement);
                } catch (Throwable e14) {
                    reportWtf("starting NetworkManagement Service", e14);
                }
            }
            if (!disableNonCoreServices) {
                try {
                    Slog.i(TAG, "Text Service Manager Service");
                    tsms = new TextServicesManagerService(context);
                    ServiceManager.addService(Context.TEXT_SERVICES_MANAGER_SERVICE, tsms);
                } catch (Throwable e15) {
                    reportWtf("starting Text Service Manager Service", e15);
                }
            }
            if (!disableNetwork) {
                try {
                    Slog.i(TAG, "NetworkStats Service");
                    networkStats = new NetworkStatsService(context, networkManagement, alarm);
                    ServiceManager.addService(Context.NETWORK_STATS_SERVICE, networkStats);
                } catch (Throwable e16) {
                    reportWtf("starting NetworkStats Service", e16);
                }
                try {
                    Slog.i(TAG, "NetworkPolicy Service");
                    networkPolicy = new NetworkPolicyManagerService(context, ActivityManagerService.self(), power, networkStats, networkManagement);
                    ServiceManager.addService(Context.NETWORK_POLICY_SERVICE, networkPolicy);
                } catch (Throwable e17) {
                    reportWtf("starting NetworkPolicy Service", e17);
                }
                try {
                    Slog.i(TAG, "Wi-Fi P2pService");
                    wifiP2p = new WifiP2pService(context);
                    ServiceManager.addService(Context.WIFI_P2P_SERVICE, wifiP2p);
                } catch (Throwable e18) {
                    reportWtf("starting Wi-Fi P2pService", e18);
                }
                try {
                    Slog.i(TAG, "Wi-Fi Service");
                    wifi = new WifiService(context);
                    ServiceManager.addService("wifi", wifi);
                } catch (Throwable e19) {
                    reportWtf("starting Wi-Fi Service", e19);
                }
                try {
                    Slog.i(TAG, "Connectivity Service");
                    connectivity = new ConnectivityService(context, networkManagement, networkStats, networkPolicy);
                    ServiceManager.addService(Context.CONNECTIVITY_SERVICE, connectivity);
                    networkStats.bindConnectivityManager(connectivity);
                    networkPolicy.bindConnectivityManager(connectivity);
                    wifiP2p.connectivityServiceReady();
                    wifi.checkAndStartWifi();
                } catch (Throwable e20) {
                    reportWtf("starting Connectivity Service", e20);
                }
                try {
                    Slog.i(TAG, "Network Service Discovery Service");
                    NsdService serviceDiscovery = NsdService.create(context);
                    ServiceManager.addService(Context.NSD_SERVICE, serviceDiscovery);
                } catch (Throwable e21) {
                    reportWtf("starting Service Discovery Service", e21);
                }
            }
            if (!disableNonCoreServices) {
                try {
                    Slog.i(TAG, "UpdateLock Service");
                    ServiceManager.addService(Context.UPDATE_LOCK_SERVICE, new UpdateLockService(context));
                } catch (Throwable e22) {
                    reportWtf("starting UpdateLockService", e22);
                }
            }
            if (mountService != null && !onlyCore) {
                mountService.waitForAsecScan();
            }
            if (accountManager != null) {
                try {
                    accountManager.systemReady();
                } catch (Throwable e23) {
                    reportWtf("making Account Manager Service ready", e23);
                }
            }
            if (contentService != null) {
                try {
                    contentService.systemReady();
                } catch (Throwable e24) {
                    reportWtf("making Content Service ready", e24);
                }
            }
            try {
                Slog.i(TAG, "Notification Manager");
                notification = new NotificationManagerService(context, statusBar, lights);
                ServiceManager.addService(Context.NOTIFICATION_SERVICE, notification);
                networkPolicy.bindNotificationManager(notification);
            } catch (Throwable e25) {
                reportWtf("starting Notification Manager", e25);
            }
            try {
                Slog.i(TAG, "Device Storage Monitor");
                ServiceManager.addService(DeviceStorageMonitorService.SERVICE, new DeviceStorageMonitorService(context));
            } catch (Throwable e26) {
                reportWtf("starting DeviceStorageMonitor service", e26);
            }
            if (!disableLocation) {
                try {
                    Slog.i(TAG, "Location Manager");
                    location = new LocationManagerService(context);
                    ServiceManager.addService("location", location);
                } catch (Throwable e27) {
                    reportWtf("starting Location Manager", e27);
                }
                try {
                    Slog.i(TAG, "Country Detector");
                    countryDetector = new CountryDetectorService(context);
                    ServiceManager.addService(Context.COUNTRY_DETECTOR, countryDetector);
                } catch (Throwable e28) {
                    reportWtf("starting Country Detector", e28);
                }
            }
            if (!disableNonCoreServices) {
                try {
                    Slog.i(TAG, "Search Service");
                    ServiceManager.addService("search", new SearchManagerService(context));
                } catch (Throwable e29) {
                    reportWtf("starting Search Service", e29);
                }
            }
            try {
                Slog.i(TAG, "DropBox Service");
                ServiceManager.addService(Context.DROPBOX_SERVICE, new DropBoxManagerService(context, new File("/data/system/dropbox")));
            } catch (Throwable e30) {
                reportWtf("starting DropBoxManagerService", e30);
            }
            if (!disableNonCoreServices && context.getResources().getBoolean(R.bool.config_enableWallpaperService)) {
                try {
                    Slog.i(TAG, "Wallpaper Service");
                    if (!headless) {
                        wallpaper = new WallpaperManagerService(context);
                        ServiceManager.addService(Context.WALLPAPER_SERVICE, wallpaper);
                    }
                } catch (Throwable e31) {
                    reportWtf("starting Wallpaper Service", e31);
                }
            }
            if (!disableMedia && !"0".equals(SystemProperties.get("system_init.startaudioservice"))) {
                try {
                    Slog.i(TAG, "Audio Service");
                    ServiceManager.addService(Context.AUDIO_SERVICE, new AudioService(context));
                } catch (Throwable e32) {
                    reportWtf("starting Audio Service", e32);
                }
            }
            if (!disableNonCoreServices) {
                try {
                    Slog.i(TAG, "Dock Observer");
                    dock = new DockObserver(context);
                } catch (Throwable e33) {
                    reportWtf("starting DockObserver", e33);
                }
            }
            if (!disableMedia) {
                try {
                    Slog.i(TAG, "Wired Accessory Manager");
                    inputManager.setWiredAccessoryCallbacks(new WiredAccessoryManager(context, inputManager));
                } catch (Throwable e34) {
                    reportWtf("starting WiredAccessoryManager", e34);
                }
            }
            if (!disableNonCoreServices) {
                try {
                    Slog.i(TAG, "USB Service");
                    usb = new UsbService(context);
                    ServiceManager.addService(Context.USB_SERVICE, usb);
                } catch (Throwable e35) {
                    reportWtf("starting UsbService", e35);
                }
                try {
                    Slog.i(TAG, "Serial Service");
                    SerialService serial = new SerialService(context);
                    ServiceManager.addService(Context.SERIAL_SERVICE, serial);
                } catch (Throwable e36) {
                    Slog.e(TAG, "Failure starting SerialService", e36);
                }
            }
            try {
                Slog.i(TAG, "Twilight Service");
                twilight = new TwilightService(context);
            } catch (Throwable e37) {
                reportWtf("starting TwilightService", e37);
            }
            try {
                Slog.i(TAG, "UI Mode Manager Service");
                uiMode = new UiModeManagerService(context, twilight);
            } catch (Throwable e38) {
                reportWtf("starting UiModeManagerService", e38);
            }
            if (!disableNonCoreServices) {
                try {
                    Slog.i(TAG, "Backup Service");
                    ServiceManager.addService(Context.BACKUP_SERVICE, new BackupManagerService(context));
                } catch (Throwable e39) {
                    Slog.e(TAG, "Failure starting Backup Service", e39);
                }
                try {
                    Slog.i(TAG, "AppWidget Service");
                    appWidget = new AppWidgetService(context);
                    ServiceManager.addService(Context.APPWIDGET_SERVICE, appWidget);
                } catch (Throwable e40) {
                    reportWtf("starting AppWidget Service", e40);
                }
                try {
                    Slog.i(TAG, "Recognition Service");
                    recognition = new RecognitionManagerService(context);
                } catch (Throwable e41) {
                    reportWtf("starting Recognition Service", e41);
                }
            }
            try {
                Slog.i(TAG, "DiskStats Service");
                ServiceManager.addService("diskstats", new DiskStatsService(context));
            } catch (Throwable e42) {
                reportWtf("starting DiskStats Service", e42);
            }
            try {
                Slog.i(TAG, "SamplingProfiler Service");
                ServiceManager.addService("samplingprofiler", new SamplingProfilerService(context));
            } catch (Throwable e43) {
                reportWtf("starting SamplingProfiler Service", e43);
            }
            if (!disableNetwork) {
                try {
                    Slog.i(TAG, "NetworkTimeUpdateService");
                    networkTimeUpdater = new NetworkTimeUpdateService(context);
                } catch (Throwable e44) {
                    reportWtf("starting NetworkTimeUpdate service", e44);
                }
            }
            if (!disableMedia) {
                try {
                    Slog.i(TAG, "CommonTimeManagementService");
                    commonTimeMgmtService = new CommonTimeManagementService(context);
                    ServiceManager.addService("commontime_management", commonTimeMgmtService);
                } catch (Throwable e45) {
                    reportWtf("starting CommonTimeManagementService service", e45);
                }
            }
            if (!disableNetwork) {
                try {
                    Slog.i(TAG, "CertBlacklister");
                    new CertBlacklister(context);
                } catch (Throwable e46) {
                    reportWtf("starting CertBlacklister", e46);
                }
            }
            if (!disableNonCoreServices && context.getResources().getBoolean(R.bool.config_dreamsSupported)) {
                try {
                    Slog.i(TAG, "Dreams Service");
                    dreamy = new DreamManagerService(context, wmHandler);
                    ServiceManager.addService(DreamService.DREAM_SERVICE, dreamy);
                } catch (Throwable e47) {
                    reportWtf("starting DreamManagerService", e47);
                }
            }
            if (!disableNonCoreServices) {
                try {
                    Slog.i(TAG, "Assets Atlas Service");
                    atlas = new AssetAtlasService(context);
                    ServiceManager.addService(AssetAtlasService.ASSET_ATLAS_SERVICE, atlas);
                } catch (Throwable e48) {
                    reportWtf("starting AssetAtlasService", e48);
                }
            }
            try {
                Slog.i(TAG, "IdleMaintenanceService");
                new IdleMaintenanceService(context, battery);
            } catch (Throwable e49) {
                reportWtf("starting IdleMaintenanceService", e49);
            }
            try {
                Slog.i(TAG, "Print Service");
                printManager = new PrintManagerService(context);
                ServiceManager.addService(Context.PRINT_SERVICE, printManager);
            } catch (Throwable e50) {
                reportWtf("starting Print Service", e50);
            }
        }
        final boolean safeMode = wm.detectSafeMode();
        if (safeMode) {
            ActivityManagerService.self().enterSafeMode();
            Zygote.systemInSafeMode = true;
            VMRuntime.getRuntime().disableJitCompilation();
        } else {
            VMRuntime.getRuntime().startJitCompilation();
        }
        try {
            vibrator.systemReady();
        } catch (Throwable e51) {
            reportWtf("making Vibrator Service ready", e51);
        }
        if (lockSettings != null) {
            try {
                lockSettings.systemReady();
            } catch (Throwable e52) {
                reportWtf("making Lock Settings Service ready", e52);
            }
        }
        if (devicePolicy != null) {
            try {
                devicePolicy.systemReady();
            } catch (Throwable e53) {
                reportWtf("making Device Policy Service ready", e53);
            }
        }
        if (notification != null) {
            try {
                notification.systemReady();
            } catch (Throwable e54) {
                reportWtf("making Notification Service ready", e54);
            }
        }
        try {
            wm.systemReady();
        } catch (Throwable e55) {
            reportWtf("making Window Manager Service ready", e55);
        }
        if (safeMode) {
            ActivityManagerService.self().showSafeModeOverlay();
        }
        Configuration config = wm.computeNewConfiguration();
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        w.getDefaultDisplay().getMetrics(metrics);
        context.getResources().updateConfiguration(config, metrics);
        try {
            power.systemReady(twilight, dreamy);
        } catch (Throwable e56) {
            reportWtf("making Power Manager Service ready", e56);
        }
        try {
            pm.systemReady();
        } catch (Throwable e57) {
            reportWtf("making Package Manager Service ready", e57);
        }
        try {
            display.systemReady(safeMode, onlyCore);
        } catch (Throwable e58) {
            reportWtf("making Display Manager Service ready", e58);
        }
        final Context contextF = context;
        final MountService mountServiceF = mountService;
        final BatteryService batteryF = battery;
        final NetworkManagementService networkManagementF = networkManagement;
        final NetworkStatsService networkStatsF = networkStats;
        final NetworkPolicyManagerService networkPolicyF = networkPolicy;
        final ConnectivityService connectivityF = connectivity;
        final DockObserver dockF = dock;
        final UsbService usbF = usb;
        final TwilightService twilightF = twilight;
        final UiModeManagerService uiModeF = uiMode;
        final AppWidgetService appWidgetF = appWidget;
        final WallpaperManagerService wallpaperF = wallpaper;
        final InputMethodManagerService immF = imm;
        final RecognitionManagerService recognitionF = recognition;
        final LocationManagerService locationF = location;
        final CountryDetectorService countryDetectorF = countryDetector;
        final NetworkTimeUpdateService networkTimeUpdaterF = networkTimeUpdater;
        final CommonTimeManagementService commonTimeMgmtServiceF = commonTimeMgmtService;
        final TextServicesManagerService textServiceManagerServiceF = tsms;
        final StatusBarManagerService statusBarF = statusBar;
        final DreamManagerService dreamyF = dreamy;
        final AssetAtlasService atlasF = atlas;
        final InputManagerService inputManagerF = inputManager;
        final TelephonyRegistry telephonyRegistryF = telephonyRegistry;
        final PrintManagerService printManagerF = printManager;
        ActivityManagerService.self().systemReady(new Runnable() { // from class: com.android.server.ServerThread.2
            @Override // java.lang.Runnable
            public void run() {
                Slog.i(ServerThread.TAG, "Making services ready");
                try {
                    ActivityManagerService.self().startObservingNativeCrashes();
                } catch (Throwable e59) {
                    ServerThread.this.reportWtf("observing native crashes", e59);
                }
                if (!headless) {
                    ServerThread.startSystemUi(contextF);
                }
                try {
                    if (mountServiceF != null) {
                        mountServiceF.systemReady();
                    }
                } catch (Throwable e60) {
                    ServerThread.this.reportWtf("making Mount Service ready", e60);
                }
                try {
                    if (batteryF != null) {
                        batteryF.systemReady();
                    }
                } catch (Throwable e61) {
                    ServerThread.this.reportWtf("making Battery Service ready", e61);
                }
                try {
                    if (networkManagementF != null) {
                        networkManagementF.systemReady();
                    }
                } catch (Throwable e62) {
                    ServerThread.this.reportWtf("making Network Managment Service ready", e62);
                }
                try {
                    if (networkStatsF != null) {
                        networkStatsF.systemReady();
                    }
                } catch (Throwable e63) {
                    ServerThread.this.reportWtf("making Network Stats Service ready", e63);
                }
                try {
                    if (networkPolicyF != null) {
                        networkPolicyF.systemReady();
                    }
                } catch (Throwable e64) {
                    ServerThread.this.reportWtf("making Network Policy Service ready", e64);
                }
                try {
                    if (connectivityF != null) {
                        connectivityF.systemReady();
                    }
                } catch (Throwable e65) {
                    ServerThread.this.reportWtf("making Connectivity Service ready", e65);
                }
                try {
                    if (dockF != null) {
                        dockF.systemReady();
                    }
                } catch (Throwable e66) {
                    ServerThread.this.reportWtf("making Dock Service ready", e66);
                }
                try {
                    if (usbF != null) {
                        usbF.systemReady();
                    }
                } catch (Throwable e67) {
                    ServerThread.this.reportWtf("making USB Service ready", e67);
                }
                try {
                    if (twilightF != null) {
                        twilightF.systemReady();
                    }
                } catch (Throwable e68) {
                    ServerThread.this.reportWtf("makin Twilight Service ready", e68);
                }
                try {
                    if (uiModeF != null) {
                        uiModeF.systemReady();
                    }
                } catch (Throwable e69) {
                    ServerThread.this.reportWtf("making UI Mode Service ready", e69);
                }
                try {
                    if (recognitionF != null) {
                        recognitionF.systemReady();
                    }
                } catch (Throwable e70) {
                    ServerThread.this.reportWtf("making Recognition Service ready", e70);
                }
                Watchdog.getInstance().start();
                try {
                    if (appWidgetF != null) {
                        appWidgetF.systemRunning(safeMode);
                    }
                } catch (Throwable e71) {
                    ServerThread.this.reportWtf("Notifying AppWidgetService running", e71);
                }
                try {
                    if (wallpaperF != null) {
                        wallpaperF.systemRunning();
                    }
                } catch (Throwable e72) {
                    ServerThread.this.reportWtf("Notifying WallpaperService running", e72);
                }
                try {
                    if (immF != null) {
                        immF.systemRunning(statusBarF);
                    }
                } catch (Throwable e73) {
                    ServerThread.this.reportWtf("Notifying InputMethodService running", e73);
                }
                try {
                    if (locationF != null) {
                        locationF.systemRunning();
                    }
                } catch (Throwable e74) {
                    ServerThread.this.reportWtf("Notifying Location Service running", e74);
                }
                try {
                    if (countryDetectorF != null) {
                        countryDetectorF.systemRunning();
                    }
                } catch (Throwable e75) {
                    ServerThread.this.reportWtf("Notifying CountryDetectorService running", e75);
                }
                try {
                    if (networkTimeUpdaterF != null) {
                        networkTimeUpdaterF.systemRunning();
                    }
                } catch (Throwable e76) {
                    ServerThread.this.reportWtf("Notifying NetworkTimeService running", e76);
                }
                try {
                    if (commonTimeMgmtServiceF != null) {
                        commonTimeMgmtServiceF.systemRunning();
                    }
                } catch (Throwable e77) {
                    ServerThread.this.reportWtf("Notifying CommonTimeManagementService running", e77);
                }
                try {
                    if (textServiceManagerServiceF != null) {
                        textServiceManagerServiceF.systemRunning();
                    }
                } catch (Throwable e78) {
                    ServerThread.this.reportWtf("Notifying TextServicesManagerService running", e78);
                }
                try {
                    if (dreamyF != null) {
                        dreamyF.systemRunning();
                    }
                } catch (Throwable e79) {
                    ServerThread.this.reportWtf("Notifying DreamManagerService running", e79);
                }
                try {
                    if (atlasF != null) {
                        atlasF.systemRunning();
                    }
                } catch (Throwable e80) {
                    ServerThread.this.reportWtf("Notifying AssetAtlasService running", e80);
                }
                try {
                    if (inputManagerF != null) {
                        inputManagerF.systemRunning();
                    }
                } catch (Throwable e81) {
                    ServerThread.this.reportWtf("Notifying InputManagerService running", e81);
                }
                try {
                    if (telephonyRegistryF != null) {
                        telephonyRegistryF.systemRunning();
                    }
                } catch (Throwable e82) {
                    ServerThread.this.reportWtf("Notifying TelephonyRegistry running", e82);
                }
                try {
                    if (printManagerF != null) {
                        printManagerF.systemRuning();
                    }
                } catch (Throwable e83) {
                    ServerThread.this.reportWtf("Notifying PrintManagerService running", e83);
                }
            }
        });
        if (StrictMode.conditionallyEnableDebugLogging()) {
            Slog.i(TAG, "Enabled StrictMode for system server main thread.");
        }
        Looper.loop();
        Slog.d(TAG, "System ServerThread is exiting!");
    }

    static final void startSystemUi(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.systemui", "com.android.systemui.SystemUIService"));
        context.startServiceAsUser(intent, UserHandle.OWNER);
    }
}