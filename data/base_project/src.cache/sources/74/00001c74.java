package com.android.server;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.location.Address;
import android.location.Criteria;
import android.location.GeocoderParams;
import android.location.IGpsStatusListener;
import android.location.IGpsStatusProvider;
import android.location.ILocationListener;
import android.location.ILocationManager;
import android.location.INetInitiatedListener;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.LocationRequest;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.content.PackageMonitor;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import com.android.internal.os.BackgroundThread;
import com.android.server.location.FlpHardwareProvider;
import com.android.server.location.FusedProxy;
import com.android.server.location.GeocoderProxy;
import com.android.server.location.GeofenceManager;
import com.android.server.location.GeofenceProxy;
import com.android.server.location.GpsLocationProvider;
import com.android.server.location.LocationBlacklist;
import com.android.server.location.LocationFudger;
import com.android.server.location.LocationProviderInterface;
import com.android.server.location.LocationProviderProxy;
import com.android.server.location.MockProvider;
import com.android.server.location.PassiveProvider;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: LocationManagerService.class */
public class LocationManagerService extends ILocationManager.Stub {
    private static final String TAG = "LocationManagerService";
    private static final String WAKELOCK_KEY = "LocationManagerService";
    private static final int RESOLUTION_LEVEL_NONE = 0;
    private static final int RESOLUTION_LEVEL_COARSE = 1;
    private static final int RESOLUTION_LEVEL_FINE = 2;
    private static final String ACCESS_MOCK_LOCATION = "android.permission.ACCESS_MOCK_LOCATION";
    private static final String ACCESS_LOCATION_EXTRA_COMMANDS = "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS";
    private static final String INSTALL_LOCATION_PROVIDER = "android.permission.INSTALL_LOCATION_PROVIDER";
    private static final String NETWORK_LOCATION_SERVICE_ACTION = "com.android.location.service.v3.NetworkLocationProvider";
    private static final String FUSED_LOCATION_SERVICE_ACTION = "com.android.location.service.FusedLocationProvider";
    private static final int MSG_LOCATION_CHANGED = 1;
    private static final long NANOS_PER_MILLI = 1000000;
    private static final long HIGH_POWER_INTERVAL_MS = 300000;
    private static final int MAX_PROVIDER_SCHEDULING_JITTER_MS = 100;
    private final Context mContext;
    private final AppOpsManager mAppOps;
    private LocationFudger mLocationFudger;
    private GeofenceManager mGeofenceManager;
    private PackageManager mPackageManager;
    private PowerManager mPowerManager;
    private GeocoderProxy mGeocodeProvider;
    private IGpsStatusProvider mGpsStatusProvider;
    private INetInitiatedListener mNetInitiatedListener;
    private LocationWorkerHandler mLocationHandler;
    private PassiveProvider mPassiveProvider;
    private LocationBlacklist mBlacklist;
    public static final boolean D = Log.isLoggable("LocationManagerService", 3);
    private static final LocationRequest DEFAULT_LOCATION_REQUEST = new LocationRequest();
    private final Object mLock = new Object();
    private final Set<String> mEnabledProviders = new HashSet();
    private final Set<String> mDisabledProviders = new HashSet();
    private final HashMap<String, MockProvider> mMockProviders = new HashMap<>();
    private final HashMap<Object, Receiver> mReceivers = new HashMap<>();
    private final ArrayList<LocationProviderInterface> mProviders = new ArrayList<>();
    private final HashMap<String, LocationProviderInterface> mRealProviders = new HashMap<>();
    private final HashMap<String, LocationProviderInterface> mProvidersByName = new HashMap<>();
    private final HashMap<String, ArrayList<UpdateRecord>> mRecordsByProvider = new HashMap<>();
    private final HashMap<String, Location> mLastLocation = new HashMap<>();
    private final HashMap<String, Location> mLastLocationCoarseInterval = new HashMap<>();
    private final ArrayList<LocationProviderProxy> mProxyProviders = new ArrayList<>();
    private int mCurrentUserId = 0;
    private final PackageMonitor mPackageMonitor = new PackageMonitor() { // from class: com.android.server.LocationManagerService.4
        @Override // com.android.internal.content.PackageMonitor
        public void onPackageDisappeared(String packageName, int reason) {
            synchronized (LocationManagerService.this.mLock) {
                ArrayList<Receiver> deadReceivers = null;
                for (Receiver receiver : LocationManagerService.this.mReceivers.values()) {
                    if (receiver.mPackageName.equals(packageName)) {
                        if (deadReceivers == null) {
                            deadReceivers = new ArrayList<>();
                        }
                        deadReceivers.add(receiver);
                    }
                }
                if (deadReceivers != null) {
                    Iterator i$ = deadReceivers.iterator();
                    while (i$.hasNext()) {
                        LocationManagerService.this.removeUpdatesLocked(i$.next());
                    }
                }
            }
        }
    };

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.getProviders(android.location.Criteria, boolean):java.util.List<java.lang.String>, file: LocationManagerService.class
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
    @Override // android.location.ILocationManager
    public java.util.List<java.lang.String> getProviders(android.location.Criteria r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.getProviders(android.location.Criteria, boolean):java.util.List<java.lang.String>, file: LocationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.getProviders(android.location.Criteria, boolean):java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.requestLocationUpdates(android.location.LocationRequest, android.location.ILocationListener, android.app.PendingIntent, java.lang.String):void, file: LocationManagerService.class
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
    @Override // android.location.ILocationManager
    public void requestLocationUpdates(android.location.LocationRequest r1, android.location.ILocationListener r2, android.app.PendingIntent r3, java.lang.String r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.requestLocationUpdates(android.location.LocationRequest, android.location.ILocationListener, android.app.PendingIntent, java.lang.String):void, file: LocationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.requestLocationUpdates(android.location.LocationRequest, android.location.ILocationListener, android.app.PendingIntent, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.removeUpdates(android.location.ILocationListener, android.app.PendingIntent, java.lang.String):void, file: LocationManagerService.class
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
    @Override // android.location.ILocationManager
    public void removeUpdates(android.location.ILocationListener r1, android.app.PendingIntent r2, java.lang.String r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.removeUpdates(android.location.ILocationListener, android.app.PendingIntent, java.lang.String):void, file: LocationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.removeUpdates(android.location.ILocationListener, android.app.PendingIntent, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.getLastLocation(android.location.LocationRequest, java.lang.String):android.location.Location, file: LocationManagerService.class
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
    @Override // android.location.ILocationManager
    public android.location.Location getLastLocation(android.location.LocationRequest r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.getLastLocation(android.location.LocationRequest, java.lang.String):android.location.Location, file: LocationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.getLastLocation(android.location.LocationRequest, java.lang.String):android.location.Location");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.requestGeofence(android.location.LocationRequest, android.location.Geofence, android.app.PendingIntent, java.lang.String):void, file: LocationManagerService.class
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
    @Override // android.location.ILocationManager
    public void requestGeofence(android.location.LocationRequest r1, android.location.Geofence r2, android.app.PendingIntent r3, java.lang.String r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.requestGeofence(android.location.LocationRequest, android.location.Geofence, android.app.PendingIntent, java.lang.String):void, file: LocationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.requestGeofence(android.location.LocationRequest, android.location.Geofence, android.app.PendingIntent, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.removeGeofence(android.location.Geofence, android.app.PendingIntent, java.lang.String):void, file: LocationManagerService.class
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
    @Override // android.location.ILocationManager
    public void removeGeofence(android.location.Geofence r1, android.app.PendingIntent r2, java.lang.String r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.removeGeofence(android.location.Geofence, android.app.PendingIntent, java.lang.String):void, file: LocationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.removeGeofence(android.location.Geofence, android.app.PendingIntent, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.addGpsStatusListener(android.location.IGpsStatusListener, java.lang.String):boolean, file: LocationManagerService.class
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
    @Override // android.location.ILocationManager
    public boolean addGpsStatusListener(android.location.IGpsStatusListener r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.addGpsStatusListener(android.location.IGpsStatusListener, java.lang.String):boolean, file: LocationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.addGpsStatusListener(android.location.IGpsStatusListener, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.isProviderEnabled(java.lang.String):boolean, file: LocationManagerService.class
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
    @Override // android.location.ILocationManager
    public boolean isProviderEnabled(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.LocationManagerService.isProviderEnabled(java.lang.String):boolean, file: LocationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.isProviderEnabled(java.lang.String):boolean");
    }

    public LocationManagerService(Context context) {
        this.mContext = context;
        this.mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        if (D) {
            Log.d("LocationManagerService", "Constructed");
        }
    }

    public void systemRunning() {
        synchronized (this.mLock) {
            if (D) {
                Log.d("LocationManagerService", "systemReady()");
            }
            this.mPackageManager = this.mContext.getPackageManager();
            this.mPowerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
            this.mLocationHandler = new LocationWorkerHandler(BackgroundThread.get().getLooper());
            this.mLocationFudger = new LocationFudger(this.mContext, this.mLocationHandler);
            this.mBlacklist = new LocationBlacklist(this.mContext, this.mLocationHandler);
            this.mBlacklist.init();
            this.mGeofenceManager = new GeofenceManager(this.mContext, this.mBlacklist);
            AppOpsManager.OnOpChangedListener callback = new AppOpsManager.OnOpChangedInternalListener() { // from class: com.android.server.LocationManagerService.1
                @Override // android.app.AppOpsManager.OnOpChangedInternalListener
                public void onOpChanged(int op, String packageName) {
                    synchronized (LocationManagerService.this.mLock) {
                        for (Receiver receiver : LocationManagerService.this.mReceivers.values()) {
                            receiver.updateMonitoring(true);
                        }
                        LocationManagerService.this.applyAllProviderRequirementsLocked();
                    }
                }
            };
            this.mAppOps.startWatchingMode(0, (String) null, callback);
            loadProvidersLocked();
            updateProvidersLocked();
        }
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("location_providers_allowed"), true, new ContentObserver(this.mLocationHandler) { // from class: com.android.server.LocationManagerService.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                synchronized (LocationManagerService.this.mLock) {
                    LocationManagerService.this.updateProvidersLocked();
                }
            }
        }, -1);
        this.mPackageMonitor.register(this.mContext, this.mLocationHandler.getLooper(), true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_SWITCHED);
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.LocationManagerService.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_USER_SWITCHED.equals(action)) {
                    LocationManagerService.this.switchUser(intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0));
                }
            }
        }, UserHandle.ALL, intentFilter, null, this.mLocationHandler);
    }

    private void ensureFallbackFusedProviderPresentLocked(ArrayList<String> pkgs) {
        PackageManager pm = this.mContext.getPackageManager();
        String systemPackageName = this.mContext.getPackageName();
        ArrayList<HashSet<Signature>> sigSets = ServiceWatcher.getSignatureSets(this.mContext, pkgs);
        List<ResolveInfo> rInfos = pm.queryIntentServicesAsUser(new Intent(FUSED_LOCATION_SERVICE_ACTION), 128, this.mCurrentUserId);
        for (ResolveInfo rInfo : rInfos) {
            String packageName = rInfo.serviceInfo.packageName;
            try {
                PackageInfo pInfo = pm.getPackageInfo(packageName, 64);
                if (!ServiceWatcher.isSignatureMatch(pInfo.signatures, sigSets)) {
                    Log.w("LocationManagerService", packageName + " resolves service " + FUSED_LOCATION_SERVICE_ACTION + ", but has wrong signature, ignoring");
                } else if (rInfo.serviceInfo.metaData == null) {
                    Log.w("LocationManagerService", "Found fused provider without metadata: " + packageName);
                } else {
                    int version = rInfo.serviceInfo.metaData.getInt(ServiceWatcher.EXTRA_SERVICE_VERSION, -1);
                    if (version == 0) {
                        if ((rInfo.serviceInfo.applicationInfo.flags & 1) == 0) {
                            if (D) {
                                Log.d("LocationManagerService", "Fallback candidate not in /system: " + packageName);
                            }
                        } else if (pm.checkSignatures(systemPackageName, packageName) != 0) {
                            if (D) {
                                Log.d("LocationManagerService", "Fallback candidate not signed the same as system: " + packageName);
                            }
                        } else if (D) {
                            Log.d("LocationManagerService", "Found fallback provider: " + packageName);
                            return;
                        } else {
                            return;
                        }
                    } else if (D) {
                        Log.d("LocationManagerService", "Fallback candidate not version 0: " + packageName);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("LocationManagerService", "missing package: " + packageName);
            }
        }
        throw new IllegalStateException("Unable to find a fused location provider that is in the system partition with version 0 and signed with the platform certificate. Such a package is needed to provide a default fused location provider in the event that no other fused location provider has been installed or is currently available. For example, coreOnly boot mode when decrypting the data partition. The fallback must also be marked coreApp=\"true\" in the manifest");
    }

    private void loadProvidersLocked() {
        PassiveProvider passiveProvider = new PassiveProvider(this);
        addProviderLocked(passiveProvider);
        this.mEnabledProviders.add(passiveProvider.getName());
        this.mPassiveProvider = passiveProvider;
        GpsLocationProvider gpsProvider = new GpsLocationProvider(this.mContext, this, this.mLocationHandler.getLooper());
        if (GpsLocationProvider.isSupported()) {
            this.mGpsStatusProvider = gpsProvider.getGpsStatusProvider();
            this.mNetInitiatedListener = gpsProvider.getNetInitiatedListener();
            addProviderLocked(gpsProvider);
            this.mRealProviders.put(LocationManager.GPS_PROVIDER, gpsProvider);
        }
        Resources resources = this.mContext.getResources();
        ArrayList<String> providerPackageNames = new ArrayList<>();
        String[] pkgs = resources.getStringArray(R.array.config_locationProviderPackageNames);
        if (D) {
            Log.d("LocationManagerService", "certificates for location providers pulled from: " + Arrays.toString(pkgs));
        }
        if (pkgs != null) {
            providerPackageNames.addAll(Arrays.asList(pkgs));
        }
        ensureFallbackFusedProviderPresentLocked(providerPackageNames);
        LocationProviderProxy networkProvider = LocationProviderProxy.createAndBind(this.mContext, LocationManager.NETWORK_PROVIDER, NETWORK_LOCATION_SERVICE_ACTION, R.bool.config_enableNetworkLocationOverlay, R.string.config_networkLocationProviderPackageName, R.array.config_locationProviderPackageNames, this.mLocationHandler);
        if (networkProvider != null) {
            this.mRealProviders.put(LocationManager.NETWORK_PROVIDER, networkProvider);
            this.mProxyProviders.add(networkProvider);
            addProviderLocked(networkProvider);
        } else {
            Slog.w("LocationManagerService", "no network location provider found");
        }
        LocationProviderProxy fusedLocationProvider = LocationProviderProxy.createAndBind(this.mContext, LocationManager.FUSED_PROVIDER, FUSED_LOCATION_SERVICE_ACTION, R.bool.config_enableFusedLocationOverlay, R.string.config_fusedLocationProviderPackageName, R.array.config_locationProviderPackageNames, this.mLocationHandler);
        if (fusedLocationProvider != null) {
            addProviderLocked(fusedLocationProvider);
            this.mProxyProviders.add(fusedLocationProvider);
            this.mEnabledProviders.add(fusedLocationProvider.getName());
            this.mRealProviders.put(LocationManager.FUSED_PROVIDER, fusedLocationProvider);
        } else {
            Slog.e("LocationManagerService", "no fused location provider found", new IllegalStateException("Location service needs a fused location provider"));
        }
        this.mGeocodeProvider = GeocoderProxy.createAndBind(this.mContext, R.bool.config_enableGeocoderOverlay, R.string.config_geocoderProviderPackageName, R.array.config_locationProviderPackageNames, this.mLocationHandler);
        if (this.mGeocodeProvider == null) {
            Slog.e("LocationManagerService", "no geocoder provider found");
        }
        FlpHardwareProvider flpHardwareProvider = FlpHardwareProvider.getInstance(this.mContext);
        FusedProxy fusedProxy = FusedProxy.createAndBind(this.mContext, this.mLocationHandler, flpHardwareProvider.getLocationHardware(), R.bool.config_enableFusedLocationOverlay, R.string.config_fusedLocationProviderPackageName, R.array.config_locationProviderPackageNames);
        if (fusedProxy == null) {
            Slog.e("LocationManagerService", "No FusedProvider found.");
        }
        GeofenceProxy provider = GeofenceProxy.createAndBind(this.mContext, R.bool.config_enableGeofenceOverlay, R.string.config_geofenceProviderPackageName, R.array.config_locationProviderPackageNames, this.mLocationHandler, gpsProvider.getGpsGeofenceProxy(), flpHardwareProvider.getGeofenceHardware());
        if (provider == null) {
            Slog.e("LocationManagerService", "no geofence provider found");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchUser(int userId) {
        this.mBlacklist.switchUser(userId);
        this.mLocationHandler.removeMessages(1);
        synchronized (this.mLock) {
            this.mLastLocation.clear();
            this.mLastLocationCoarseInterval.clear();
            Iterator i$ = this.mProviders.iterator();
            while (i$.hasNext()) {
                LocationProviderInterface p = i$.next();
                updateProviderListenersLocked(p.getName(), false, this.mCurrentUserId);
            }
            this.mCurrentUserId = userId;
            updateProvidersLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LocationManagerService$Receiver.class */
    public final class Receiver implements IBinder.DeathRecipient, PendingIntent.OnFinished {
        final int mUid;
        final int mPid;
        final String mPackageName;
        final int mAllowedResolutionLevel;
        final ILocationListener mListener;
        final PendingIntent mPendingIntent;
        final WorkSource mWorkSource;
        final boolean mHideFromAppOps;
        final Object mKey;
        final HashMap<String, UpdateRecord> mUpdateRecords = new HashMap<>();
        boolean mOpMonitoring;
        boolean mOpHighPowerMonitoring;
        int mPendingBroadcasts;
        PowerManager.WakeLock mWakeLock;

        Receiver(ILocationListener listener, PendingIntent intent, int pid, int uid, String packageName, WorkSource workSource, boolean hideFromAppOps) {
            this.mListener = listener;
            this.mPendingIntent = intent;
            if (listener != null) {
                this.mKey = listener.asBinder();
            } else {
                this.mKey = intent;
            }
            this.mAllowedResolutionLevel = LocationManagerService.this.getAllowedResolutionLevel(pid, uid);
            this.mUid = uid;
            this.mPid = pid;
            this.mPackageName = packageName;
            if (workSource != null && workSource.size() <= 0) {
                workSource = null;
            }
            this.mWorkSource = workSource;
            this.mHideFromAppOps = hideFromAppOps;
            updateMonitoring(true);
            this.mWakeLock = LocationManagerService.this.mPowerManager.newWakeLock(1, "LocationManagerService");
            this.mWakeLock.setWorkSource(workSource == null ? new WorkSource(this.mUid, this.mPackageName) : workSource);
        }

        public boolean equals(Object otherObj) {
            if (otherObj instanceof Receiver) {
                return this.mKey.equals(((Receiver) otherObj).mKey);
            }
            return false;
        }

        public int hashCode() {
            return this.mKey.hashCode();
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("Reciever[");
            s.append(Integer.toHexString(System.identityHashCode(this)));
            if (this.mListener != null) {
                s.append(" listener");
            } else {
                s.append(" intent");
            }
            for (String p : this.mUpdateRecords.keySet()) {
                s.append(Separators.SP).append(this.mUpdateRecords.get(p).toString());
            }
            s.append("]");
            return s.toString();
        }

        public void updateMonitoring(boolean allow) {
            if (this.mHideFromAppOps) {
                return;
            }
            boolean requestingLocation = false;
            boolean requestingHighPowerLocation = false;
            if (allow) {
                Iterator i$ = this.mUpdateRecords.values().iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    UpdateRecord updateRecord = i$.next();
                    if (LocationManagerService.this.isAllowedByCurrentUserSettingsLocked(updateRecord.mProvider)) {
                        requestingLocation = true;
                        LocationProviderInterface locationProvider = (LocationProviderInterface) LocationManagerService.this.mProvidersByName.get(updateRecord.mProvider);
                        ProviderProperties properties = locationProvider != null ? locationProvider.getProperties() : null;
                        if (properties != null && properties.mPowerRequirement == 3 && updateRecord.mRequest.getInterval() < LocationManagerService.HIGH_POWER_INTERVAL_MS) {
                            requestingHighPowerLocation = true;
                            break;
                        }
                    }
                }
            }
            this.mOpMonitoring = updateMonitoring(requestingLocation, this.mOpMonitoring, 41);
            boolean wasHighPowerMonitoring = this.mOpHighPowerMonitoring;
            this.mOpHighPowerMonitoring = updateMonitoring(requestingHighPowerLocation, this.mOpHighPowerMonitoring, 42);
            if (this.mOpHighPowerMonitoring != wasHighPowerMonitoring) {
                Intent intent = new Intent(LocationManager.HIGH_POWER_REQUEST_CHANGE_ACTION);
                LocationManagerService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
            }
        }

        private boolean updateMonitoring(boolean allowMonitoring, boolean currentlyMonitoring, int op) {
            if (!currentlyMonitoring) {
                if (allowMonitoring) {
                    return LocationManagerService.this.mAppOps.startOpNoThrow(op, this.mUid, this.mPackageName) == 0;
                }
            } else if (!allowMonitoring || LocationManagerService.this.mAppOps.checkOpNoThrow(op, this.mUid, this.mPackageName) != 0) {
                LocationManagerService.this.mAppOps.finishOp(op, this.mUid, this.mPackageName);
                return false;
            }
            return currentlyMonitoring;
        }

        public boolean isListener() {
            return this.mListener != null;
        }

        public boolean isPendingIntent() {
            return this.mPendingIntent != null;
        }

        public ILocationListener getListener() {
            if (this.mListener != null) {
                return this.mListener;
            }
            throw new IllegalStateException("Request for non-existent listener");
        }

        public boolean callStatusChangedLocked(String provider, int status, Bundle extras) {
            if (this.mListener != null) {
                try {
                    synchronized (this) {
                        this.mListener.onStatusChanged(provider, status, extras);
                        incrementPendingBroadcastsLocked();
                    }
                    return true;
                } catch (RemoteException e) {
                    return false;
                }
            }
            Intent statusChanged = new Intent();
            statusChanged.putExtras(new Bundle(extras));
            statusChanged.putExtra("status", status);
            try {
                synchronized (this) {
                    this.mPendingIntent.send(LocationManagerService.this.mContext, 0, statusChanged, this, LocationManagerService.this.mLocationHandler, LocationManagerService.this.getResolutionPermission(this.mAllowedResolutionLevel));
                    incrementPendingBroadcastsLocked();
                }
                return true;
            } catch (PendingIntent.CanceledException e2) {
                return false;
            }
        }

        public boolean callLocationChangedLocked(Location location) {
            if (this.mListener != null) {
                try {
                    synchronized (this) {
                        this.mListener.onLocationChanged(new Location(location));
                        incrementPendingBroadcastsLocked();
                    }
                    return true;
                } catch (RemoteException e) {
                    return false;
                }
            }
            Intent locationChanged = new Intent();
            locationChanged.putExtra("location", new Location(location));
            try {
                synchronized (this) {
                    this.mPendingIntent.send(LocationManagerService.this.mContext, 0, locationChanged, this, LocationManagerService.this.mLocationHandler, LocationManagerService.this.getResolutionPermission(this.mAllowedResolutionLevel));
                    incrementPendingBroadcastsLocked();
                }
                return true;
            } catch (PendingIntent.CanceledException e2) {
                return false;
            }
        }

        public boolean callProviderEnabledLocked(String provider, boolean enabled) {
            updateMonitoring(true);
            if (this.mListener != null) {
                try {
                    synchronized (this) {
                        if (enabled) {
                            this.mListener.onProviderEnabled(provider);
                        } else {
                            this.mListener.onProviderDisabled(provider);
                        }
                        incrementPendingBroadcastsLocked();
                    }
                    return true;
                } catch (RemoteException e) {
                    return false;
                }
            }
            Intent providerIntent = new Intent();
            providerIntent.putExtra(LocationManager.KEY_PROVIDER_ENABLED, enabled);
            try {
                synchronized (this) {
                    this.mPendingIntent.send(LocationManagerService.this.mContext, 0, providerIntent, this, LocationManagerService.this.mLocationHandler, LocationManagerService.this.getResolutionPermission(this.mAllowedResolutionLevel));
                    incrementPendingBroadcastsLocked();
                }
                return true;
            } catch (PendingIntent.CanceledException e2) {
                return false;
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            if (LocationManagerService.D) {
                Log.d("LocationManagerService", "Location listener died");
            }
            synchronized (LocationManagerService.this.mLock) {
                LocationManagerService.this.removeUpdatesLocked(this);
            }
            synchronized (this) {
                clearPendingBroadcastsLocked();
            }
        }

        @Override // android.app.PendingIntent.OnFinished
        public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
            synchronized (this) {
                decrementPendingBroadcastsLocked();
            }
        }

        private void incrementPendingBroadcastsLocked() {
            int i = this.mPendingBroadcasts;
            this.mPendingBroadcasts = i + 1;
            if (i == 0) {
                this.mWakeLock.acquire();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void decrementPendingBroadcastsLocked() {
            int i = this.mPendingBroadcasts - 1;
            this.mPendingBroadcasts = i;
            if (i == 0 && this.mWakeLock.isHeld()) {
                this.mWakeLock.release();
            }
        }

        public void clearPendingBroadcastsLocked() {
            if (this.mPendingBroadcasts > 0) {
                this.mPendingBroadcasts = 0;
                if (this.mWakeLock.isHeld()) {
                    this.mWakeLock.release();
                }
            }
        }
    }

    @Override // android.location.ILocationManager
    public void locationCallbackFinished(ILocationListener listener) {
        synchronized (this.mLock) {
            IBinder binder = listener.asBinder();
            Receiver receiver = this.mReceivers.get(binder);
            if (receiver != null) {
                synchronized (receiver) {
                    long identity = Binder.clearCallingIdentity();
                    receiver.decrementPendingBroadcastsLocked();
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }
    }

    private void addProviderLocked(LocationProviderInterface provider) {
        this.mProviders.add(provider);
        this.mProvidersByName.put(provider.getName(), provider);
    }

    private void removeProviderLocked(LocationProviderInterface provider) {
        provider.disable();
        this.mProviders.remove(provider);
        this.mProvidersByName.remove(provider.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAllowedByCurrentUserSettingsLocked(String provider) {
        if (this.mEnabledProviders.contains(provider)) {
            return true;
        }
        if (this.mDisabledProviders.contains(provider)) {
            return false;
        }
        ContentResolver resolver = this.mContext.getContentResolver();
        return Settings.Secure.isLocationProviderEnabledForUser(resolver, provider, this.mCurrentUserId);
    }

    private boolean isAllowedByUserSettingsLocked(String provider, int uid) {
        if (UserHandle.getUserId(uid) != this.mCurrentUserId && !isUidALocationProvider(uid)) {
            return false;
        }
        return isAllowedByCurrentUserSettingsLocked(provider);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getResolutionPermission(int resolutionLevel) {
        switch (resolutionLevel) {
            case 1:
                return Manifest.permission.ACCESS_COARSE_LOCATION;
            case 2:
                return Manifest.permission.ACCESS_FINE_LOCATION;
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getAllowedResolutionLevel(int pid, int uid) {
        if (this.mContext.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, pid, uid) == 0) {
            return 2;
        }
        if (this.mContext.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, pid, uid) == 0) {
            return 1;
        }
        return 0;
    }

    private int getCallerAllowedResolutionLevel() {
        return getAllowedResolutionLevel(Binder.getCallingPid(), Binder.getCallingUid());
    }

    private void checkResolutionLevelIsSufficientForGeofenceUse(int allowedResolutionLevel) {
        if (allowedResolutionLevel < 2) {
            throw new SecurityException("Geofence usage requires ACCESS_FINE_LOCATION permission");
        }
    }

    private int getMinimumResolutionLevelForProviderUse(String provider) {
        ProviderProperties properties;
        if (LocationManager.GPS_PROVIDER.equals(provider) || LocationManager.PASSIVE_PROVIDER.equals(provider)) {
            return 2;
        }
        if (LocationManager.NETWORK_PROVIDER.equals(provider) || LocationManager.FUSED_PROVIDER.equals(provider)) {
            return 1;
        }
        LocationProviderInterface lp = this.mMockProviders.get(provider);
        if (lp == null || (properties = lp.getProperties()) == null || properties.mRequiresSatellite) {
            return 2;
        }
        if (properties.mRequiresNetwork || properties.mRequiresCell) {
            return 1;
        }
        return 2;
    }

    private void checkResolutionLevelIsSufficientForProviderUse(int allowedResolutionLevel, String providerName) {
        int requiredResolutionLevel = getMinimumResolutionLevelForProviderUse(providerName);
        if (allowedResolutionLevel < requiredResolutionLevel) {
            switch (requiredResolutionLevel) {
                case 1:
                    throw new SecurityException(Separators.DOUBLE_QUOTE + providerName + "\" location provider requires ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission.");
                case 2:
                    throw new SecurityException(Separators.DOUBLE_QUOTE + providerName + "\" location provider requires ACCESS_FINE_LOCATION permission.");
                default:
                    throw new SecurityException("Insufficient permission for \"" + providerName + "\" location provider.");
            }
        }
    }

    private void checkDeviceStatsAllowed() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.UPDATE_DEVICE_STATS, null);
    }

    private void checkUpdateAppOpsAllowed() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.UPDATE_APP_OPS_STATS, null);
    }

    public static int resolutionLevelToOp(int allowedResolutionLevel) {
        if (allowedResolutionLevel != 0) {
            if (allowedResolutionLevel == 1) {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    boolean reportLocationAccessNoThrow(int uid, String packageName, int allowedResolutionLevel) {
        int op = resolutionLevelToOp(allowedResolutionLevel);
        if (op >= 0 && this.mAppOps.noteOpNoThrow(op, uid, packageName) != 0) {
            return false;
        }
        return true;
    }

    boolean checkLocationAccess(int uid, String packageName, int allowedResolutionLevel) {
        int op = resolutionLevelToOp(allowedResolutionLevel);
        if (op >= 0 && this.mAppOps.checkOp(op, uid, packageName) != 0) {
            return false;
        }
        return true;
    }

    @Override // android.location.ILocationManager
    public List<String> getAllProviders() {
        ArrayList<String> out;
        synchronized (this.mLock) {
            out = new ArrayList<>(this.mProviders.size());
            Iterator i$ = this.mProviders.iterator();
            while (i$.hasNext()) {
                LocationProviderInterface provider = i$.next();
                String name = provider.getName();
                if (!LocationManager.FUSED_PROVIDER.equals(name)) {
                    out.add(name);
                }
            }
        }
        if (D) {
            Log.d("LocationManagerService", "getAllProviders()=" + out);
        }
        return out;
    }

    @Override // android.location.ILocationManager
    public String getBestProvider(Criteria criteria, boolean enabledOnly) {
        List<String> providers = getProviders(criteria, enabledOnly);
        if (!providers.isEmpty()) {
            String result = pickBest(providers);
            if (D) {
                Log.d("LocationManagerService", "getBestProvider(" + criteria + ", " + enabledOnly + ")=" + result);
            }
            return result;
        }
        List<String> providers2 = getProviders(null, enabledOnly);
        if (!providers2.isEmpty()) {
            String result2 = pickBest(providers2);
            if (D) {
                Log.d("LocationManagerService", "getBestProvider(" + criteria + ", " + enabledOnly + ")=" + result2);
            }
            return result2;
        } else if (D) {
            Log.d("LocationManagerService", "getBestProvider(" + criteria + ", " + enabledOnly + ")=" + ((String) null));
            return null;
        } else {
            return null;
        }
    }

    private String pickBest(List<String> providers) {
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        }
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        }
        return providers.get(0);
    }

    @Override // android.location.ILocationManager
    public boolean providerMeetsCriteria(String provider, Criteria criteria) {
        LocationProviderInterface p = this.mProvidersByName.get(provider);
        if (p == null) {
            throw new IllegalArgumentException("provider=" + provider);
        }
        boolean result = LocationProvider.propertiesMeetCriteria(p.getName(), p.getProperties(), criteria);
        if (D) {
            Log.d("LocationManagerService", "providerMeetsCriteria(" + provider + ", " + criteria + ")=" + result);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateProvidersLocked() {
        boolean changesMade = false;
        for (int i = this.mProviders.size() - 1; i >= 0; i--) {
            LocationProviderInterface p = this.mProviders.get(i);
            boolean isEnabled = p.isEnabled();
            String name = p.getName();
            boolean shouldBeEnabled = isAllowedByCurrentUserSettingsLocked(name);
            if (isEnabled && !shouldBeEnabled) {
                updateProviderListenersLocked(name, false, this.mCurrentUserId);
                changesMade = true;
            } else if (!isEnabled && shouldBeEnabled) {
                updateProviderListenersLocked(name, true, this.mCurrentUserId);
                changesMade = true;
            }
        }
        if (changesMade) {
            this.mContext.sendBroadcastAsUser(new Intent(LocationManager.PROVIDERS_CHANGED_ACTION), UserHandle.ALL);
            this.mContext.sendBroadcastAsUser(new Intent(LocationManager.MODE_CHANGED_ACTION), UserHandle.ALL);
        }
    }

    private void updateProviderListenersLocked(String provider, boolean enabled, int userId) {
        int listeners = 0;
        LocationProviderInterface p = this.mProvidersByName.get(provider);
        if (p == null) {
            return;
        }
        ArrayList<Receiver> deadReceivers = null;
        ArrayList<UpdateRecord> records = this.mRecordsByProvider.get(provider);
        if (records != null) {
            int N = records.size();
            for (int i = 0; i < N; i++) {
                UpdateRecord record = records.get(i);
                if (UserHandle.getUserId(record.mReceiver.mUid) == userId) {
                    if (!record.mReceiver.callProviderEnabledLocked(provider, enabled)) {
                        if (deadReceivers == null) {
                            deadReceivers = new ArrayList<>();
                        }
                        deadReceivers.add(record.mReceiver);
                    }
                    listeners++;
                }
            }
        }
        if (deadReceivers != null) {
            for (int i2 = deadReceivers.size() - 1; i2 >= 0; i2--) {
                removeUpdatesLocked(deadReceivers.get(i2));
            }
        }
        if (enabled) {
            p.enable();
            if (listeners > 0) {
                applyRequirementsLocked(provider);
                return;
            }
            return;
        }
        p.disable();
    }

    private void applyRequirementsLocked(String provider) {
        LocationProviderInterface p = this.mProvidersByName.get(provider);
        if (p == null) {
            return;
        }
        ArrayList<UpdateRecord> records = this.mRecordsByProvider.get(provider);
        WorkSource worksource = new WorkSource();
        ProviderRequest providerRequest = new ProviderRequest();
        if (records != null) {
            Iterator i$ = records.iterator();
            while (i$.hasNext()) {
                UpdateRecord record = i$.next();
                if (UserHandle.getUserId(record.mReceiver.mUid) == this.mCurrentUserId && checkLocationAccess(record.mReceiver.mUid, record.mReceiver.mPackageName, record.mReceiver.mAllowedResolutionLevel)) {
                    LocationRequest locationRequest = record.mRequest;
                    providerRequest.locationRequests.add(locationRequest);
                    if (locationRequest.getInterval() < providerRequest.interval) {
                        providerRequest.reportLocation = true;
                        providerRequest.interval = locationRequest.getInterval();
                    }
                }
            }
            if (providerRequest.reportLocation) {
                long thresholdInterval = ((providerRequest.interval + 1000) * 3) / 2;
                Iterator i$2 = records.iterator();
                while (i$2.hasNext()) {
                    UpdateRecord record2 = i$2.next();
                    if (UserHandle.getUserId(record2.mReceiver.mUid) == this.mCurrentUserId && record2.mRequest.getInterval() <= thresholdInterval) {
                        if (record2.mReceiver.mWorkSource != null && record2.mReceiver.mWorkSource.size() > 0 && record2.mReceiver.mWorkSource.getName(0) != null) {
                            worksource.add(record2.mReceiver.mWorkSource);
                        } else {
                            worksource.add(record2.mReceiver.mUid, record2.mReceiver.mPackageName);
                        }
                    }
                }
            }
        }
        if (D) {
            Log.d("LocationManagerService", "provider request: " + provider + Separators.SP + providerRequest);
        }
        p.setRequest(providerRequest, worksource);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LocationManagerService$UpdateRecord.class */
    public class UpdateRecord {
        final String mProvider;
        final LocationRequest mRequest;
        final Receiver mReceiver;
        Location mLastFixBroadcast;
        long mLastStatusBroadcast;

        UpdateRecord(String provider, LocationRequest request, Receiver receiver) {
            this.mProvider = provider;
            this.mRequest = request;
            this.mReceiver = receiver;
            ArrayList<UpdateRecord> records = (ArrayList) LocationManagerService.this.mRecordsByProvider.get(provider);
            if (records == null) {
                records = new ArrayList<>();
                LocationManagerService.this.mRecordsByProvider.put(provider, records);
            }
            if (!records.contains(this)) {
                records.add(this);
            }
        }

        void disposeLocked(boolean removeReceiver) {
            HashMap<String, UpdateRecord> receiverRecords;
            ArrayList<UpdateRecord> globalRecords = (ArrayList) LocationManagerService.this.mRecordsByProvider.get(this.mProvider);
            if (globalRecords != null) {
                globalRecords.remove(this);
            }
            if (removeReceiver && (receiverRecords = this.mReceiver.mUpdateRecords) != null) {
                receiverRecords.remove(this.mProvider);
                if (removeReceiver && receiverRecords.size() == 0) {
                    LocationManagerService.this.removeUpdatesLocked(this.mReceiver);
                }
            }
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("UpdateRecord[");
            s.append(this.mProvider);
            s.append(' ').append(this.mReceiver.mPackageName).append('(');
            s.append(this.mReceiver.mUid).append(')');
            s.append(' ').append(this.mRequest);
            s.append(']');
            return s.toString();
        }
    }

    private Receiver getReceiverLocked(ILocationListener listener, int pid, int uid, String packageName, WorkSource workSource, boolean hideFromAppOps) {
        IBinder binder = listener.asBinder();
        Receiver receiver = this.mReceivers.get(binder);
        if (receiver == null) {
            receiver = new Receiver(listener, null, pid, uid, packageName, workSource, hideFromAppOps);
            this.mReceivers.put(binder, receiver);
            try {
                receiver.getListener().asBinder().linkToDeath(receiver, 0);
            } catch (RemoteException e) {
                Slog.e("LocationManagerService", "linkToDeath failed:", e);
                return null;
            }
        }
        return receiver;
    }

    private Receiver getReceiverLocked(PendingIntent intent, int pid, int uid, String packageName, WorkSource workSource, boolean hideFromAppOps) {
        Receiver receiver = this.mReceivers.get(intent);
        if (receiver == null) {
            receiver = new Receiver(null, intent, pid, uid, packageName, workSource, hideFromAppOps);
            this.mReceivers.put(intent, receiver);
        }
        return receiver;
    }

    private LocationRequest createSanitizedRequest(LocationRequest request, int resolutionLevel) {
        LocationRequest sanitizedRequest = new LocationRequest(request);
        if (resolutionLevel < 2) {
            switch (sanitizedRequest.getQuality()) {
                case 100:
                    sanitizedRequest.setQuality(102);
                    break;
                case 203:
                    sanitizedRequest.setQuality(201);
                    break;
            }
            if (sanitizedRequest.getInterval() < LocationFudger.FASTEST_INTERVAL_MS) {
                sanitizedRequest.setInterval(LocationFudger.FASTEST_INTERVAL_MS);
            }
            if (sanitizedRequest.getFastestInterval() < LocationFudger.FASTEST_INTERVAL_MS) {
                sanitizedRequest.setFastestInterval(LocationFudger.FASTEST_INTERVAL_MS);
            }
        }
        if (sanitizedRequest.getFastestInterval() > sanitizedRequest.getInterval()) {
            request.setFastestInterval(request.getInterval());
        }
        return sanitizedRequest;
    }

    private void checkPackageName(String packageName) {
        if (packageName == null) {
            throw new SecurityException("invalid package name: " + packageName);
        }
        int uid = Binder.getCallingUid();
        String[] packages = this.mPackageManager.getPackagesForUid(uid);
        if (packages == null) {
            throw new SecurityException("invalid UID " + uid);
        }
        for (String pkg : packages) {
            if (packageName.equals(pkg)) {
                return;
            }
        }
        throw new SecurityException("invalid package name: " + packageName);
    }

    private void checkPendingIntent(PendingIntent intent) {
        if (intent == null) {
            throw new IllegalArgumentException("invalid pending intent: " + intent);
        }
    }

    private Receiver checkListenerOrIntentLocked(ILocationListener listener, PendingIntent intent, int pid, int uid, String packageName, WorkSource workSource, boolean hideFromAppOps) {
        if (intent == null && listener == null) {
            throw new IllegalArgumentException("need either listener or intent");
        }
        if (intent != null && listener != null) {
            throw new IllegalArgumentException("cannot register both listener and intent");
        }
        if (intent != null) {
            checkPendingIntent(intent);
            return getReceiverLocked(intent, pid, uid, packageName, workSource, hideFromAppOps);
        }
        return getReceiverLocked(listener, pid, uid, packageName, workSource, hideFromAppOps);
    }

    private void requestLocationUpdatesLocked(LocationRequest request, Receiver receiver, int pid, int uid, String packageName) {
        if (request == null) {
            request = DEFAULT_LOCATION_REQUEST;
        }
        String name = request.getProvider();
        if (name == null) {
            throw new IllegalArgumentException("provider name must not be null");
        }
        if (D) {
            Log.d("LocationManagerService", "request " + Integer.toHexString(System.identityHashCode(receiver)) + Separators.SP + name + Separators.SP + request + " from " + packageName + Separators.LPAREN + uid + Separators.RPAREN);
        }
        LocationProviderInterface provider = this.mProvidersByName.get(name);
        if (provider == null) {
            throw new IllegalArgumentException("provider doesn't exist: " + name);
        }
        UpdateRecord record = new UpdateRecord(name, request, receiver);
        UpdateRecord oldRecord = receiver.mUpdateRecords.put(name, record);
        if (oldRecord != null) {
            oldRecord.disposeLocked(false);
        }
        boolean isProviderEnabled = isAllowedByUserSettingsLocked(name, uid);
        if (isProviderEnabled) {
            applyRequirementsLocked(name);
        } else {
            receiver.callProviderEnabledLocked(name, false);
        }
        receiver.updateMonitoring(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeUpdatesLocked(Receiver receiver) {
        if (D) {
            Log.i("LocationManagerService", "remove " + Integer.toHexString(System.identityHashCode(receiver)));
        }
        if (this.mReceivers.remove(receiver.mKey) != null && receiver.isListener()) {
            receiver.getListener().asBinder().unlinkToDeath(receiver, 0);
            synchronized (receiver) {
                receiver.clearPendingBroadcastsLocked();
            }
        }
        receiver.updateMonitoring(false);
        HashSet<String> providers = new HashSet<>();
        HashMap<String, UpdateRecord> oldRecords = receiver.mUpdateRecords;
        if (oldRecords != null) {
            for (UpdateRecord record : oldRecords.values()) {
                record.disposeLocked(false);
            }
            providers.addAll(oldRecords.keySet());
        }
        Iterator i$ = providers.iterator();
        while (i$.hasNext()) {
            String provider = i$.next();
            if (isAllowedByCurrentUserSettingsLocked(provider)) {
                applyRequirementsLocked(provider);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyAllProviderRequirementsLocked() {
        Iterator i$ = this.mProviders.iterator();
        while (i$.hasNext()) {
            LocationProviderInterface p = i$.next();
            if (isAllowedByCurrentUserSettingsLocked(p.getName())) {
                applyRequirementsLocked(p.getName());
            }
        }
    }

    @Override // android.location.ILocationManager
    public void removeGpsStatusListener(IGpsStatusListener listener) {
        synchronized (this.mLock) {
            try {
                this.mGpsStatusProvider.removeGpsStatusListener(listener);
            } catch (Exception e) {
                Slog.e("LocationManagerService", "mGpsStatusProvider.removeGpsStatusListener failed", e);
            }
        }
    }

    @Override // android.location.ILocationManager
    public boolean sendExtraCommand(String provider, String command, Bundle extras) {
        if (provider == null) {
            throw new NullPointerException();
        }
        checkResolutionLevelIsSufficientForProviderUse(getCallerAllowedResolutionLevel(), provider);
        if (this.mContext.checkCallingOrSelfPermission("android.permission.ACCESS_LOCATION_EXTRA_COMMANDS") != 0) {
            throw new SecurityException("Requires ACCESS_LOCATION_EXTRA_COMMANDS permission");
        }
        synchronized (this.mLock) {
            LocationProviderInterface p = this.mProvidersByName.get(provider);
            if (p == null) {
                return false;
            }
            return p.sendExtraCommand(command, extras);
        }
    }

    @Override // android.location.ILocationManager
    public boolean sendNiResponse(int notifId, int userResponse) {
        if (Binder.getCallingUid() != Process.myUid()) {
            throw new SecurityException("calling sendNiResponse from outside of the system is not allowed");
        }
        try {
            return this.mNetInitiatedListener.sendNiResponse(notifId, userResponse);
        } catch (RemoteException e) {
            Slog.e("LocationManagerService", "RemoteException in LocationManagerService.sendNiResponse");
            return false;
        }
    }

    @Override // android.location.ILocationManager
    public ProviderProperties getProviderProperties(String provider) {
        LocationProviderInterface p;
        if (this.mProvidersByName.get(provider) == null) {
            return null;
        }
        checkResolutionLevelIsSufficientForProviderUse(getCallerAllowedResolutionLevel(), provider);
        synchronized (this.mLock) {
            p = this.mProvidersByName.get(provider);
        }
        if (p == null) {
            return null;
        }
        return p.getProperties();
    }

    private boolean isUidALocationProvider(int uid) {
        if (uid == 1000) {
            return true;
        }
        if (this.mGeocodeProvider == null || !doesPackageHaveUid(uid, this.mGeocodeProvider.getConnectedPackageName())) {
            Iterator i$ = this.mProxyProviders.iterator();
            while (i$.hasNext()) {
                LocationProviderProxy proxy = i$.next();
                if (doesPackageHaveUid(uid, proxy.getConnectedPackageName())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private void checkCallerIsProvider() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.INSTALL_LOCATION_PROVIDER") == 0 || isUidALocationProvider(Binder.getCallingUid())) {
            return;
        }
        throw new SecurityException("need INSTALL_LOCATION_PROVIDER permission, or UID of a currently bound location provider");
    }

    private boolean doesPackageHaveUid(int uid, String packageName) {
        if (packageName == null) {
            return false;
        }
        try {
            ApplicationInfo appInfo = this.mPackageManager.getApplicationInfo(packageName, 0);
            if (appInfo.uid != uid) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override // android.location.ILocationManager
    public void reportLocation(Location location, boolean passive) {
        checkCallerIsProvider();
        if (!location.isComplete()) {
            Log.w("LocationManagerService", "Dropping incomplete location: " + location);
            return;
        }
        this.mLocationHandler.removeMessages(1, location);
        Message m = Message.obtain(this.mLocationHandler, 1, location);
        m.arg1 = passive ? 1 : 0;
        this.mLocationHandler.sendMessageAtFrontOfQueue(m);
    }

    private static boolean shouldBroadcastSafe(Location loc, Location lastLoc, UpdateRecord record, long now) {
        if (lastLoc == null) {
            return true;
        }
        long minTime = record.mRequest.getFastestInterval();
        long delta = (loc.getElapsedRealtimeNanos() - lastLoc.getElapsedRealtimeNanos()) / NANOS_PER_MILLI;
        if (delta < minTime - 100) {
            return false;
        }
        double minDistance = record.mRequest.getSmallestDisplacement();
        if ((minDistance > 0.0d && loc.distanceTo(lastLoc) <= minDistance) || record.mRequest.getNumUpdates() <= 0 || record.mRequest.getExpireAt() < now) {
            return false;
        }
        return true;
    }

    private void handleLocationChangedLocked(Location location, boolean passive) {
        Location notifyLocation;
        Location lastLoc;
        if (D) {
            Log.d("LocationManagerService", "incoming location: " + location);
        }
        long now = SystemClock.elapsedRealtime();
        String provider = passive ? LocationManager.PASSIVE_PROVIDER : location.getProvider();
        LocationProviderInterface p = this.mProvidersByName.get(provider);
        if (p == null) {
            return;
        }
        Location noGPSLocation = location.getExtraLocation(Location.EXTRA_NO_GPS_LOCATION);
        Location lastLocation = this.mLastLocation.get(provider);
        if (lastLocation == null) {
            lastLocation = new Location(provider);
            this.mLastLocation.put(provider, lastLocation);
        } else {
            Location lastNoGPSLocation = lastLocation.getExtraLocation(Location.EXTRA_NO_GPS_LOCATION);
            if (noGPSLocation == null && lastNoGPSLocation != null) {
                location.setExtraLocation(Location.EXTRA_NO_GPS_LOCATION, lastNoGPSLocation);
            }
        }
        lastLocation.set(location);
        Location lastLocationCoarseInterval = this.mLastLocationCoarseInterval.get(provider);
        if (lastLocationCoarseInterval == null) {
            lastLocationCoarseInterval = new Location(location);
            this.mLastLocationCoarseInterval.put(provider, lastLocationCoarseInterval);
        }
        long timeDiffNanos = location.getElapsedRealtimeNanos() - lastLocationCoarseInterval.getElapsedRealtimeNanos();
        if (timeDiffNanos > 600000000000L) {
            lastLocationCoarseInterval.set(location);
        }
        Location noGPSLocation2 = lastLocationCoarseInterval.getExtraLocation(Location.EXTRA_NO_GPS_LOCATION);
        ArrayList<UpdateRecord> records = this.mRecordsByProvider.get(provider);
        if (records == null || records.size() == 0) {
            return;
        }
        Location coarseLocation = null;
        if (noGPSLocation2 != null) {
            coarseLocation = this.mLocationFudger.getOrCreate(noGPSLocation2);
        }
        long newStatusUpdateTime = p.getStatusUpdateTime();
        Bundle extras = new Bundle();
        int status = p.getStatus(extras);
        ArrayList<Receiver> deadReceivers = null;
        ArrayList<UpdateRecord> deadUpdateRecords = null;
        Iterator i$ = records.iterator();
        while (i$.hasNext()) {
            UpdateRecord r = i$.next();
            Receiver receiver = r.mReceiver;
            boolean receiverDead = false;
            int receiverUserId = UserHandle.getUserId(receiver.mUid);
            if (receiverUserId != this.mCurrentUserId && !isUidALocationProvider(receiver.mUid)) {
                if (D) {
                    Log.d("LocationManagerService", "skipping loc update for background user " + receiverUserId + " (current user: " + this.mCurrentUserId + ", app: " + receiver.mPackageName + Separators.RPAREN);
                }
            } else if (this.mBlacklist.isBlacklisted(receiver.mPackageName)) {
                if (D) {
                    Log.d("LocationManagerService", "skipping loc update for blacklisted app: " + receiver.mPackageName);
                }
            } else if (!reportLocationAccessNoThrow(receiver.mUid, receiver.mPackageName, receiver.mAllowedResolutionLevel)) {
                if (D) {
                    Log.d("LocationManagerService", "skipping loc update for no op app: " + receiver.mPackageName);
                }
            } else {
                if (receiver.mAllowedResolutionLevel < 2) {
                    notifyLocation = coarseLocation;
                } else {
                    notifyLocation = lastLocation;
                }
                if (notifyLocation != null && ((lastLoc = r.mLastFixBroadcast) == null || shouldBroadcastSafe(notifyLocation, lastLoc, r, now))) {
                    if (lastLoc == null) {
                        r.mLastFixBroadcast = new Location(notifyLocation);
                    } else {
                        lastLoc.set(notifyLocation);
                    }
                    if (!receiver.callLocationChangedLocked(notifyLocation)) {
                        Slog.w("LocationManagerService", "RemoteException calling onLocationChanged on " + receiver);
                        receiverDead = true;
                    }
                    r.mRequest.decrementNumUpdates();
                }
                long prevStatusUpdateTime = r.mLastStatusBroadcast;
                if (newStatusUpdateTime > prevStatusUpdateTime && (prevStatusUpdateTime != 0 || status != 2)) {
                    r.mLastStatusBroadcast = newStatusUpdateTime;
                    if (!receiver.callStatusChangedLocked(provider, status, extras)) {
                        receiverDead = true;
                        Slog.w("LocationManagerService", "RemoteException calling onStatusChanged on " + receiver);
                    }
                }
                if (r.mRequest.getNumUpdates() <= 0 || r.mRequest.getExpireAt() < now) {
                    if (deadUpdateRecords == null) {
                        deadUpdateRecords = new ArrayList<>();
                    }
                    deadUpdateRecords.add(r);
                }
                if (receiverDead) {
                    if (deadReceivers == null) {
                        deadReceivers = new ArrayList<>();
                    }
                    if (!deadReceivers.contains(receiver)) {
                        deadReceivers.add(receiver);
                    }
                }
            }
        }
        if (deadReceivers != null) {
            Iterator i$2 = deadReceivers.iterator();
            while (i$2.hasNext()) {
                removeUpdatesLocked(i$2.next());
            }
        }
        if (deadUpdateRecords != null) {
            Iterator i$3 = deadUpdateRecords.iterator();
            while (i$3.hasNext()) {
                i$3.next().disposeLocked(true);
            }
            applyRequirementsLocked(provider);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LocationManagerService$LocationWorkerHandler.class */
    public class LocationWorkerHandler extends Handler {
        public LocationWorkerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    LocationManagerService.this.handleLocationChanged((Location) msg.obj, msg.arg1 == 1);
                    return;
                default:
                    return;
            }
        }
    }

    private boolean isMockProvider(String provider) {
        boolean containsKey;
        synchronized (this.mLock) {
            containsKey = this.mMockProviders.containsKey(provider);
        }
        return containsKey;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleLocationChanged(Location location, boolean passive) {
        Location myLocation = new Location(location);
        String provider = myLocation.getProvider();
        if (!myLocation.isFromMockProvider() && isMockProvider(provider)) {
            myLocation.setIsFromMockProvider(true);
        }
        synchronized (this.mLock) {
            if (isAllowedByCurrentUserSettingsLocked(provider)) {
                if (!passive) {
                    this.mPassiveProvider.updateLocation(myLocation);
                }
                handleLocationChangedLocked(myLocation, passive);
            }
        }
    }

    @Override // android.location.ILocationManager
    public boolean geocoderIsPresent() {
        return this.mGeocodeProvider != null;
    }

    @Override // android.location.ILocationManager
    public String getFromLocation(double latitude, double longitude, int maxResults, GeocoderParams params, List<Address> addrs) {
        if (this.mGeocodeProvider != null) {
            return this.mGeocodeProvider.getFromLocation(latitude, longitude, maxResults, params, addrs);
        }
        return null;
    }

    @Override // android.location.ILocationManager
    public String getFromLocationName(String locationName, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude, int maxResults, GeocoderParams params, List<Address> addrs) {
        if (this.mGeocodeProvider != null) {
            return this.mGeocodeProvider.getFromLocationName(locationName, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRightLongitude, maxResults, params, addrs);
        }
        return null;
    }

    private void checkMockPermissionsSafe() {
        boolean allowMocks = Settings.Secure.getInt(this.mContext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) == 1;
        if (!allowMocks) {
            throw new SecurityException("Requires ACCESS_MOCK_LOCATION secure setting");
        }
        if (this.mContext.checkCallingPermission("android.permission.ACCESS_MOCK_LOCATION") != 0) {
            throw new SecurityException("Requires ACCESS_MOCK_LOCATION permission");
        }
    }

    @Override // android.location.ILocationManager
    public void addTestProvider(String name, ProviderProperties properties) {
        LocationProviderInterface p;
        checkMockPermissionsSafe();
        if (LocationManager.PASSIVE_PROVIDER.equals(name)) {
            throw new IllegalArgumentException("Cannot mock the passive location provider");
        }
        long identity = Binder.clearCallingIdentity();
        synchronized (this.mLock) {
            MockProvider provider = new MockProvider(name, this, properties);
            if ((LocationManager.GPS_PROVIDER.equals(name) || LocationManager.NETWORK_PROVIDER.equals(name) || LocationManager.FUSED_PROVIDER.equals(name)) && (p = this.mProvidersByName.get(name)) != null) {
                removeProviderLocked(p);
            }
            if (this.mProvidersByName.get(name) != null) {
                throw new IllegalArgumentException("Provider \"" + name + "\" already exists");
            }
            addProviderLocked(provider);
            this.mMockProviders.put(name, provider);
            this.mLastLocation.put(name, null);
            this.mLastLocationCoarseInterval.put(name, null);
            updateProvidersLocked();
        }
        Binder.restoreCallingIdentity(identity);
    }

    @Override // android.location.ILocationManager
    public void removeTestProvider(String provider) {
        checkMockPermissionsSafe();
        synchronized (this.mLock) {
            MockProvider mockProvider = this.mMockProviders.remove(provider);
            if (mockProvider == null) {
                throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
            }
            long identity = Binder.clearCallingIdentity();
            removeProviderLocked(this.mProvidersByName.get(provider));
            LocationProviderInterface realProvider = this.mRealProviders.get(provider);
            if (realProvider != null) {
                addProviderLocked(realProvider);
            }
            this.mLastLocation.put(provider, null);
            this.mLastLocationCoarseInterval.put(provider, null);
            updateProvidersLocked();
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.location.ILocationManager
    public void setTestProviderLocation(String provider, Location loc) {
        checkMockPermissionsSafe();
        synchronized (this.mLock) {
            MockProvider mockProvider = this.mMockProviders.get(provider);
            if (mockProvider == null) {
                throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
            }
            long identity = Binder.clearCallingIdentity();
            mockProvider.setLocation(loc);
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.location.ILocationManager
    public void clearTestProviderLocation(String provider) {
        checkMockPermissionsSafe();
        synchronized (this.mLock) {
            MockProvider mockProvider = this.mMockProviders.get(provider);
            if (mockProvider == null) {
                throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
            }
            mockProvider.clearLocation();
        }
    }

    @Override // android.location.ILocationManager
    public void setTestProviderEnabled(String provider, boolean enabled) {
        checkMockPermissionsSafe();
        synchronized (this.mLock) {
            MockProvider mockProvider = this.mMockProviders.get(provider);
            if (mockProvider == null) {
                throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
            }
            long identity = Binder.clearCallingIdentity();
            if (enabled) {
                mockProvider.enable();
                this.mEnabledProviders.add(provider);
                this.mDisabledProviders.remove(provider);
            } else {
                mockProvider.disable();
                this.mEnabledProviders.remove(provider);
                this.mDisabledProviders.add(provider);
            }
            updateProvidersLocked();
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.location.ILocationManager
    public void clearTestProviderEnabled(String provider) {
        checkMockPermissionsSafe();
        synchronized (this.mLock) {
            MockProvider mockProvider = this.mMockProviders.get(provider);
            if (mockProvider == null) {
                throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
            }
            long identity = Binder.clearCallingIdentity();
            this.mEnabledProviders.remove(provider);
            this.mDisabledProviders.remove(provider);
            updateProvidersLocked();
            Binder.restoreCallingIdentity(identity);
        }
    }

    @Override // android.location.ILocationManager
    public void setTestProviderStatus(String provider, int status, Bundle extras, long updateTime) {
        checkMockPermissionsSafe();
        synchronized (this.mLock) {
            MockProvider mockProvider = this.mMockProviders.get(provider);
            if (mockProvider == null) {
                throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
            }
            mockProvider.setStatus(status, extras, updateTime);
        }
    }

    @Override // android.location.ILocationManager
    public void clearTestProviderStatus(String provider) {
        checkMockPermissionsSafe();
        synchronized (this.mLock) {
            MockProvider mockProvider = this.mMockProviders.get(provider);
            if (mockProvider == null) {
                throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
            }
            mockProvider.clearStatus();
        }
    }

    private void log(String log) {
        if (Log.isLoggable("LocationManagerService", 2)) {
            Slog.d("LocationManagerService", log);
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump LocationManagerService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mLock) {
            pw.println("Current Location Manager state:");
            pw.println("  Location Listeners:");
            for (Receiver receiver : this.mReceivers.values()) {
                pw.println("    " + receiver);
            }
            pw.println("  Records by Provider:");
            for (Map.Entry<String, ArrayList<UpdateRecord>> entry : this.mRecordsByProvider.entrySet()) {
                pw.println("    " + entry.getKey() + Separators.COLON);
                Iterator i$ = entry.getValue().iterator();
                while (i$.hasNext()) {
                    UpdateRecord record = i$.next();
                    pw.println("      " + record);
                }
            }
            pw.println("  Last Known Locations:");
            for (Map.Entry<String, Location> entry2 : this.mLastLocation.entrySet()) {
                String provider = entry2.getKey();
                Location location = entry2.getValue();
                pw.println("    " + provider + ": " + location);
            }
            pw.println("  Last Known Locations Coarse Intervals:");
            for (Map.Entry<String, Location> entry3 : this.mLastLocationCoarseInterval.entrySet()) {
                String provider2 = entry3.getKey();
                Location location2 = entry3.getValue();
                pw.println("    " + provider2 + ": " + location2);
            }
            this.mGeofenceManager.dump(pw);
            if (this.mEnabledProviders.size() > 0) {
                pw.println("  Enabled Providers:");
                for (String i : this.mEnabledProviders) {
                    pw.println("    " + i);
                }
            }
            if (this.mDisabledProviders.size() > 0) {
                pw.println("  Disabled Providers:");
                for (String i2 : this.mDisabledProviders) {
                    pw.println("    " + i2);
                }
            }
            pw.append("  ");
            this.mBlacklist.dump(pw);
            if (this.mMockProviders.size() > 0) {
                pw.println("  Mock Providers:");
                for (Map.Entry<String, MockProvider> i3 : this.mMockProviders.entrySet()) {
                    i3.getValue().dump(pw, "      ");
                }
            }
            pw.append("  fudger: ");
            this.mLocationFudger.dump(fd, pw, args);
            if (args.length <= 0 || !"short".equals(args[0])) {
                Iterator i$2 = this.mProviders.iterator();
                while (i$2.hasNext()) {
                    LocationProviderInterface provider3 = i$2.next();
                    pw.print(provider3.getName() + " Internal State");
                    if (provider3 instanceof LocationProviderProxy) {
                        LocationProviderProxy proxy = (LocationProviderProxy) provider3;
                        pw.print(" (" + proxy.getConnectedPackageName() + Separators.RPAREN);
                    }
                    pw.println(Separators.COLON);
                    provider3.dump(fd, pw, args);
                }
            }
        }
    }
}