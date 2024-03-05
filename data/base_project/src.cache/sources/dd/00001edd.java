package com.android.server.location;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.location.GeofenceHardwareImpl;
import android.location.FusedBatchOptions;
import android.location.IGpsGeofenceHardware;
import android.location.IGpsStatusListener;
import android.location.IGpsStatusProvider;
import android.location.ILocationManager;
import android.location.INetInitiatedListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ProxyProperties;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.BatteryStats;
import android.os.Binder;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.BrowserContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.NtpTrustedTime;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IBatteryStats;
import com.android.internal.location.GpsNetInitiatedHandler;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import com.android.internal.telephony.Phone;
import gov.nist.core.Separators;
import gov.nist.javax.sip.parser.TokenNames;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/* loaded from: GpsLocationProvider.class */
public class GpsLocationProvider implements LocationProviderInterface {
    private static final String TAG = "GpsLocationProvider";
    private static final boolean DEBUG = Log.isLoggable("GpsLocationProvider", 3);
    private static final boolean VERBOSE = Log.isLoggable("GpsLocationProvider", 2);
    private static final ProviderProperties PROPERTIES = new ProviderProperties(true, true, false, false, true, true, true, 3, 1);
    private static final int GPS_POSITION_MODE_STANDALONE = 0;
    private static final int GPS_POSITION_MODE_MS_BASED = 1;
    private static final int GPS_POSITION_MODE_MS_ASSISTED = 2;
    private static final int GPS_POSITION_RECURRENCE_PERIODIC = 0;
    private static final int GPS_POSITION_RECURRENCE_SINGLE = 1;
    private static final int GPS_STATUS_NONE = 0;
    private static final int GPS_STATUS_SESSION_BEGIN = 1;
    private static final int GPS_STATUS_SESSION_END = 2;
    private static final int GPS_STATUS_ENGINE_ON = 3;
    private static final int GPS_STATUS_ENGINE_OFF = 4;
    private static final int GPS_REQUEST_AGPS_DATA_CONN = 1;
    private static final int GPS_RELEASE_AGPS_DATA_CONN = 2;
    private static final int GPS_AGPS_DATA_CONNECTED = 3;
    private static final int GPS_AGPS_DATA_CONN_DONE = 4;
    private static final int GPS_AGPS_DATA_CONN_FAILED = 5;
    private static final int LOCATION_INVALID = 0;
    private static final int LOCATION_HAS_LAT_LONG = 1;
    private static final int LOCATION_HAS_ALTITUDE = 2;
    private static final int LOCATION_HAS_SPEED = 4;
    private static final int LOCATION_HAS_BEARING = 8;
    private static final int LOCATION_HAS_ACCURACY = 16;
    private static final int GPS_DELETE_EPHEMERIS = 1;
    private static final int GPS_DELETE_ALMANAC = 2;
    private static final int GPS_DELETE_POSITION = 4;
    private static final int GPS_DELETE_TIME = 8;
    private static final int GPS_DELETE_IONO = 16;
    private static final int GPS_DELETE_UTC = 32;
    private static final int GPS_DELETE_HEALTH = 64;
    private static final int GPS_DELETE_SVDIR = 128;
    private static final int GPS_DELETE_SVSTEER = 256;
    private static final int GPS_DELETE_SADATA = 512;
    private static final int GPS_DELETE_RTI = 1024;
    private static final int GPS_DELETE_CELLDB_INFO = 32768;
    private static final int GPS_DELETE_ALL = 65535;
    private static final int GPS_CAPABILITY_SCHEDULING = 1;
    private static final int GPS_CAPABILITY_MSB = 2;
    private static final int GPS_CAPABILITY_MSA = 4;
    private static final int GPS_CAPABILITY_SINGLE_SHOT = 8;
    private static final int GPS_CAPABILITY_ON_DEMAND_TIME = 16;
    private static final int AGPS_TYPE_SUPL = 1;
    private static final int AGPS_TYPE_C2K = 2;
    private static final int AGPS_DATA_CONNECTION_CLOSED = 0;
    private static final int AGPS_DATA_CONNECTION_OPENING = 1;
    private static final int AGPS_DATA_CONNECTION_OPEN = 2;
    private static final int CHECK_LOCATION = 1;
    private static final int ENABLE = 2;
    private static final int SET_REQUEST = 3;
    private static final int UPDATE_NETWORK_STATE = 4;
    private static final int INJECT_NTP_TIME = 5;
    private static final int DOWNLOAD_XTRA_DATA = 6;
    private static final int UPDATE_LOCATION = 7;
    private static final int ADD_LISTENER = 8;
    private static final int REMOVE_LISTENER = 9;
    private static final int INJECT_NTP_TIME_FINISHED = 10;
    private static final int DOWNLOAD_XTRA_DATA_FINISHED = 11;
    private static final int AGPS_RIL_REQUEST_SETID_IMSI = 1;
    private static final int AGPS_RIL_REQUEST_SETID_MSISDN = 2;
    private static final int AGPS_RIL_REQUEST_REFLOC_CELLID = 1;
    private static final int AGPS_RIL_REQUEST_REFLOC_MAC = 2;
    private static final int AGPS_REF_LOCATION_TYPE_GSM_CELLID = 1;
    private static final int AGPS_REF_LOCATION_TYPE_UMTS_CELLID = 2;
    private static final int AGPS_REG_LOCATION_TYPE_MAC = 3;
    private static final int AGPS_SETID_TYPE_NONE = 0;
    private static final int AGPS_SETID_TYPE_IMSI = 1;
    private static final int AGPS_SETID_TYPE_MSISDN = 2;
    private static final String PROPERTIES_FILE = "/etc/gps.conf";
    private static final int GPS_GEOFENCE_UNAVAILABLE = 1;
    private static final int GPS_GEOFENCE_AVAILABLE = 2;
    private static final int GPS_GEOFENCE_OPERATION_SUCCESS = 0;
    private static final int GPS_GEOFENCE_ERROR_TOO_MANY_GEOFENCES = 100;
    private static final int GPS_GEOFENCE_ERROR_ID_EXISTS = -101;
    private static final int GPS_GEOFENCE_ERROR_ID_UNKNOWN = -102;
    private static final int GPS_GEOFENCE_ERROR_INVALID_TRANSITION = -103;
    private static final int GPS_GEOFENCE_ERROR_GENERIC = -149;
    private static final long RECENT_FIX_TIMEOUT = 10000;
    private static final int NO_FIX_TIMEOUT = 60000;
    private static final int GPS_POLLING_THRESHOLD_INTERVAL = 10000;
    private static final long NTP_INTERVAL = 86400000;
    private static final long RETRY_INTERVAL = 300000;
    private boolean mEnabled;
    private boolean mNetworkAvailable;
    private static final int STATE_PENDING_NETWORK = 0;
    private static final int STATE_DOWNLOADING = 1;
    private static final int STATE_IDLE = 2;
    private boolean mPeriodicTimeInjection;
    private boolean mNavigating;
    private boolean mEngineOn;
    private boolean mStarted;
    private boolean mSingleShot;
    private int mEngineCapabilities;
    private boolean mSupportsXtra;
    private long mLastFixTime;
    private int mPositionMode;
    private Properties mProperties;
    private String mSuplServerHost;
    private int mSuplServerPort;
    private String mC2KServerHost;
    private int mC2KServerPort;
    private final Context mContext;
    private final NtpTrustedTime mNtpTime;
    private final ILocationManager mILocationManager;
    private Handler mHandler;
    private String mAGpsApn;
    private int mAGpsDataConnectionState;
    private int mAGpsDataConnectionIpAddr;
    private final ConnectivityManager mConnMgr;
    private final GpsNetInitiatedHandler mNIHandler;
    private static final String WAKELOCK_KEY = "GpsLocationProvider";
    private final PowerManager.WakeLock mWakeLock;
    private static final String ALARM_WAKEUP = "com.android.internal.location.ALARM_WAKEUP";
    private static final String ALARM_TIMEOUT = "com.android.internal.location.ALARM_TIMEOUT";
    private final AlarmManager mAlarmManager;
    private final PendingIntent mWakeupIntent;
    private final PendingIntent mTimeoutIntent;
    private final IAppOpsService mAppOpsService;
    private final IBatteryStats mBatteryStats;
    private GeofenceHardwareImpl mGeofenceHardwareImpl;
    private static final int MAX_SVS = 32;
    private static final int EPHEMERIS_MASK = 0;
    private static final int ALMANAC_MASK = 1;
    private static final int USED_FOR_FIX_MASK = 2;
    private int mSvCount;
    private Object mLock = new Object();
    private int mLocationFlags = 0;
    private int mStatus = 1;
    private long mStatusUpdateTime = SystemClock.elapsedRealtime();
    private int mInjectNtpTimePending = 0;
    private int mDownloadXtraDataPending = 0;
    private int mFixInterval = 1000;
    private long mFixRequestTime = 0;
    private int mTimeToFirstFix = 0;
    private Location mLocation = new Location(LocationManager.GPS_PROVIDER);
    private Bundle mLocationExtras = new Bundle();
    private ArrayList<Listener> mListeners = new ArrayList<>();
    private WorkSource mClientSource = new WorkSource();
    private final IGpsStatusProvider mGpsStatusProvider = new IGpsStatusProvider.Stub() { // from class: com.android.server.location.GpsLocationProvider.1
        @Override // android.location.IGpsStatusProvider
        public void addGpsStatusListener(IGpsStatusListener listener) throws RemoteException {
            if (listener != null) {
                synchronized (GpsLocationProvider.this.mListeners) {
                    IBinder binder = listener.asBinder();
                    int size = GpsLocationProvider.this.mListeners.size();
                    for (int i = 0; i < size; i++) {
                        Listener test = (Listener) GpsLocationProvider.this.mListeners.get(i);
                        if (binder.equals(test.mListener.asBinder())) {
                            return;
                        }
                    }
                    Listener l = new Listener(listener);
                    binder.linkToDeath(l, 0);
                    GpsLocationProvider.this.mListeners.add(l);
                    return;
                }
            }
            throw new NullPointerException("listener is null in addGpsStatusListener");
        }

        @Override // android.location.IGpsStatusProvider
        public void removeGpsStatusListener(IGpsStatusListener listener) {
            if (listener != null) {
                synchronized (GpsLocationProvider.this.mListeners) {
                    IBinder binder = listener.asBinder();
                    Listener l = null;
                    int size = GpsLocationProvider.this.mListeners.size();
                    for (int i = 0; i < size && l == null; i++) {
                        Listener test = (Listener) GpsLocationProvider.this.mListeners.get(i);
                        if (binder.equals(test.mListener.asBinder())) {
                            l = test;
                        }
                    }
                    if (l != null) {
                        GpsLocationProvider.this.mListeners.remove(l);
                        binder.unlinkToDeath(l, 0);
                    }
                }
                return;
            }
            throw new NullPointerException("listener is null in addGpsStatusListener");
        }
    };
    private final BroadcastReceiver mBroadcastReciever = new BroadcastReceiver() { // from class: com.android.server.location.GpsLocationProvider.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int networkState;
            String action = intent.getAction();
            if (action.equals(GpsLocationProvider.ALARM_WAKEUP)) {
                if (GpsLocationProvider.DEBUG) {
                    Log.d("GpsLocationProvider", "ALARM_WAKEUP");
                }
                GpsLocationProvider.this.startNavigating(false);
            } else if (action.equals(GpsLocationProvider.ALARM_TIMEOUT)) {
                if (GpsLocationProvider.DEBUG) {
                    Log.d("GpsLocationProvider", "ALARM_TIMEOUT");
                }
                GpsLocationProvider.this.hibernate();
            } else if (action.equals(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION)) {
                GpsLocationProvider.this.checkSmsSuplInit(intent);
            } else if (action.equals(Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION)) {
                GpsLocationProvider.this.checkWapSuplInit(intent);
            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                    networkState = 1;
                } else {
                    networkState = 2;
                }
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                ConnectivityManager connManager = (ConnectivityManager) GpsLocationProvider.this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                GpsLocationProvider.this.updateNetworkState(networkState, connManager.getNetworkInfo(info.getType()));
            }
        }
    };
    private IGpsGeofenceHardware mGpsGeofenceBinder = new IGpsGeofenceHardware.Stub() { // from class: com.android.server.location.GpsLocationProvider.6
        @Override // android.location.IGpsGeofenceHardware
        public boolean isHardwareGeofenceSupported() {
            return GpsLocationProvider.access$1600();
        }

        @Override // android.location.IGpsGeofenceHardware
        public boolean addCircularHardwareGeofence(int geofenceId, double latitude, double longitude, double radius, int lastTransition, int monitorTransitions, int notificationResponsiveness, int unknownTimer) {
            return GpsLocationProvider.native_add_geofence(geofenceId, latitude, longitude, radius, lastTransition, monitorTransitions, notificationResponsiveness, unknownTimer);
        }

        @Override // android.location.IGpsGeofenceHardware
        public boolean removeHardwareGeofence(int geofenceId) {
            return GpsLocationProvider.native_remove_geofence(geofenceId);
        }

        @Override // android.location.IGpsGeofenceHardware
        public boolean pauseHardwareGeofence(int geofenceId) {
            return GpsLocationProvider.native_pause_geofence(geofenceId);
        }

        @Override // android.location.IGpsGeofenceHardware
        public boolean resumeHardwareGeofence(int geofenceId, int monitorTransition) {
            return GpsLocationProvider.native_resume_geofence(geofenceId, monitorTransition);
        }
    };
    private final INetInitiatedListener mNetInitiatedListener = new INetInitiatedListener.Stub() { // from class: com.android.server.location.GpsLocationProvider.7
        @Override // android.location.INetInitiatedListener
        public boolean sendNiResponse(int notificationId, int userResponse) {
            if (GpsLocationProvider.DEBUG) {
                Log.d("GpsLocationProvider", "sendNiResponse, notifId: " + notificationId + ", response: " + userResponse);
            }
            GpsLocationProvider.this.native_send_ni_response(notificationId, userResponse);
            return true;
        }
    };
    private int[] mSvs = new int[32];
    private float[] mSnrs = new float[32];
    private float[] mSvElevations = new float[32];
    private float[] mSvAzimuths = new float[32];
    private int[] mSvMasks = new int[3];
    private byte[] mNmeaBuffer = new byte[120];

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.location.GpsLocationProvider.getSelectedApn():java.lang.String, file: GpsLocationProvider.class
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
    private java.lang.String getSelectedApn() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.location.GpsLocationProvider.getSelectedApn():java.lang.String, file: GpsLocationProvider.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.location.GpsLocationProvider.getSelectedApn():java.lang.String");
    }

    private static native void class_init_native();

    private static native boolean native_is_supported();

    private native boolean native_init();

    private native void native_cleanup();

    private native boolean native_set_position_mode(int i, int i2, int i3, int i4, int i5);

    private native boolean native_start();

    private native boolean native_stop();

    private native void native_delete_aiding_data(int i);

    private native int native_read_sv_status(int[] iArr, float[] fArr, float[] fArr2, float[] fArr3, int[] iArr2);

    private native int native_read_nmea(byte[] bArr, int i);

    private native void native_inject_location(double d, double d2, float f);

    /* JADX INFO: Access modifiers changed from: private */
    public native void native_inject_time(long j, long j2, int i);

    private native boolean native_supports_xtra();

    /* JADX INFO: Access modifiers changed from: private */
    public native void native_inject_xtra_data(byte[] bArr, int i);

    private native String native_get_internal_state();

    private native void native_agps_data_conn_open(String str);

    private native void native_agps_data_conn_closed();

    private native void native_agps_data_conn_failed();

    private native void native_agps_ni_message(byte[] bArr, int i);

    private native void native_set_agps_server(int i, String str, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public native void native_send_ni_response(int i, int i2);

    private native void native_agps_set_ref_location_cellid(int i, int i2, int i3, int i4, int i5);

    private native void native_agps_set_id(int i, String str);

    private native void native_update_network_state(boolean z, int i, boolean z2, boolean z3, String str, String str2);

    private static native boolean native_is_geofence_supported();

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_add_geofence(int i, double d, double d2, double d3, int i2, int i3, int i4, int i5);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_remove_geofence(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_resume_geofence(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean native_pause_geofence(int i);

    static /* synthetic */ boolean access$1600() {
        return native_is_geofence_supported();
    }

    static {
        class_init_native();
    }

    /* loaded from: GpsLocationProvider$GpsRequest.class */
    private static class GpsRequest {
        public ProviderRequest request;
        public WorkSource source;

        public GpsRequest(ProviderRequest request, WorkSource source) {
            this.request = request;
            this.source = source;
        }
    }

    public IGpsStatusProvider getGpsStatusProvider() {
        return this.mGpsStatusProvider;
    }

    public IGpsGeofenceHardware getGpsGeofenceProxy() {
        return this.mGpsGeofenceBinder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkSmsSuplInit(Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage smsMessage : messages) {
            byte[] supl_init = smsMessage.getUserData();
            native_agps_ni_message(supl_init, supl_init.length);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkWapSuplInit(Intent intent) {
        byte[] supl_init = (byte[]) intent.getExtra("data");
        native_agps_ni_message(supl_init, supl_init.length);
    }

    public static boolean isSupported() {
        return native_is_supported();
    }

    public GpsLocationProvider(Context context, ILocationManager ilocationManager, Looper looper) {
        this.mContext = context;
        this.mNtpTime = NtpTrustedTime.getInstance(context);
        this.mILocationManager = ilocationManager;
        this.mNIHandler = new GpsNetInitiatedHandler(context);
        this.mLocation.setExtras(this.mLocationExtras);
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = powerManager.newWakeLock(1, "GpsLocationProvider");
        this.mWakeLock.setReferenceCounted(true);
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mWakeupIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ALARM_WAKEUP), 0);
        this.mTimeoutIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ALARM_TIMEOUT), 0);
        this.mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.mAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService(Context.APP_OPS_SERVICE));
        this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService(BatteryStats.SERVICE_NAME));
        this.mProperties = new Properties();
        try {
            File file = new File(PROPERTIES_FILE);
            FileInputStream stream = new FileInputStream(file);
            this.mProperties.load(stream);
            stream.close();
            this.mSuplServerHost = this.mProperties.getProperty("SUPL_HOST");
            String portString = this.mProperties.getProperty("SUPL_PORT");
            if (this.mSuplServerHost != null && portString != null) {
                try {
                    this.mSuplServerPort = Integer.parseInt(portString);
                } catch (NumberFormatException e) {
                    Log.e("GpsLocationProvider", "unable to parse SUPL_PORT: " + portString);
                }
            }
            this.mC2KServerHost = this.mProperties.getProperty("C2K_HOST");
            String portString2 = this.mProperties.getProperty("C2K_PORT");
            if (this.mC2KServerHost != null && portString2 != null) {
                try {
                    this.mC2KServerPort = Integer.parseInt(portString2);
                } catch (NumberFormatException e2) {
                    Log.e("GpsLocationProvider", "unable to parse C2K_PORT: " + portString2);
                }
            }
        } catch (IOException e3) {
            Log.w("GpsLocationProvider", "Could not open GPS configuration file /etc/gps.conf");
        }
        this.mHandler = new ProviderHandler(looper);
        listenForBroadcasts();
        this.mHandler.post(new Runnable() { // from class: com.android.server.location.GpsLocationProvider.3
            @Override // java.lang.Runnable
            public void run() {
                LocationManager locManager = (LocationManager) GpsLocationProvider.this.mContext.getSystemService("location");
                LocationRequest request = LocationRequest.createFromDeprecatedProvider(LocationManager.PASSIVE_PROVIDER, 0L, 0.0f, false);
                request.setHideFromAppOps(true);
                locManager.requestLocationUpdates(request, new NetworkLocationListener(), GpsLocationProvider.this.mHandler.getLooper());
            }
        });
    }

    private void listenForBroadcasts() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
        intentFilter.addDataScheme("sms");
        intentFilter.addDataAuthority(ProxyProperties.LOCAL_HOST, "7275");
        this.mContext.registerReceiver(this.mBroadcastReciever, intentFilter, null, this.mHandler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION);
        try {
            intentFilter2.addDataType("application/vnd.omaloc-supl-init");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.w("GpsLocationProvider", "Malformed SUPL init mime type");
        }
        this.mContext.registerReceiver(this.mBroadcastReciever, intentFilter2, null, this.mHandler);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction(ALARM_WAKEUP);
        intentFilter3.addAction(ALARM_TIMEOUT);
        intentFilter3.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.mContext.registerReceiver(this.mBroadcastReciever, intentFilter3, null, this.mHandler);
    }

    @Override // com.android.server.location.LocationProviderInterface
    public String getName() {
        return LocationManager.GPS_PROVIDER;
    }

    @Override // com.android.server.location.LocationProviderInterface
    public ProviderProperties getProperties() {
        return PROPERTIES;
    }

    public void updateNetworkState(int state, NetworkInfo info) {
        sendMessage(4, state, info);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUpdateNetworkState(int state, NetworkInfo info) {
        this.mNetworkAvailable = state == 2;
        if (DEBUG) {
            Log.d("GpsLocationProvider", "updateNetworkState " + (this.mNetworkAvailable ? "available" : "unavailable") + " info: " + info);
        }
        if (info != null) {
            boolean dataEnabled = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.MOBILE_DATA, 1) == 1;
            boolean networkAvailable = info.isAvailable() && dataEnabled;
            String defaultApn = getSelectedApn();
            if (defaultApn == null) {
                defaultApn = "dummy-apn";
            }
            native_update_network_state(info.isConnected(), info.getType(), info.isRoaming(), networkAvailable, info.getExtraInfo(), defaultApn);
        }
        if (info != null && info.getType() == 3 && this.mAGpsDataConnectionState == 1) {
            String apnName = info.getExtraInfo();
            if (this.mNetworkAvailable) {
                if (apnName == null) {
                    apnName = "dummy-apn";
                }
                this.mAGpsApn = apnName;
                if (DEBUG) {
                    Log.d("GpsLocationProvider", "mAGpsDataConnectionIpAddr " + this.mAGpsDataConnectionIpAddr);
                }
                if (this.mAGpsDataConnectionIpAddr != -1) {
                    if (DEBUG) {
                        Log.d("GpsLocationProvider", "call requestRouteToHost");
                    }
                    boolean route_result = this.mConnMgr.requestRouteToHost(3, this.mAGpsDataConnectionIpAddr);
                    if (!route_result) {
                        Log.d("GpsLocationProvider", "call requestRouteToHost failed");
                    }
                }
                if (DEBUG) {
                    Log.d("GpsLocationProvider", "call native_agps_data_conn_open");
                }
                native_agps_data_conn_open(apnName);
                this.mAGpsDataConnectionState = 2;
            } else {
                if (DEBUG) {
                    Log.d("GpsLocationProvider", "call native_agps_data_conn_failed");
                }
                this.mAGpsApn = null;
                this.mAGpsDataConnectionState = 0;
                native_agps_data_conn_failed();
            }
        }
        if (this.mNetworkAvailable) {
            if (this.mInjectNtpTimePending == 0) {
                sendMessage(5, 0, null);
            }
            if (this.mDownloadXtraDataPending == 0) {
                sendMessage(6, 0, null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleInjectNtpTime() {
        if (this.mInjectNtpTimePending == 1) {
            return;
        }
        if (!this.mNetworkAvailable) {
            this.mInjectNtpTimePending = 0;
            return;
        }
        this.mInjectNtpTimePending = 1;
        this.mWakeLock.acquire();
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() { // from class: com.android.server.location.GpsLocationProvider.4
            @Override // java.lang.Runnable
            public void run() {
                long delay;
                if (GpsLocationProvider.this.mNtpTime.getCacheAge() >= 86400000) {
                    GpsLocationProvider.this.mNtpTime.forceRefresh();
                }
                if (GpsLocationProvider.this.mNtpTime.getCacheAge() < 86400000) {
                    long time = GpsLocationProvider.this.mNtpTime.getCachedNtpTime();
                    long timeReference = GpsLocationProvider.this.mNtpTime.getCachedNtpTimeReference();
                    long certainty = GpsLocationProvider.this.mNtpTime.getCacheCertainty();
                    long now = System.currentTimeMillis();
                    Log.d("GpsLocationProvider", "NTP server returned: " + time + " (" + new Date(time) + ") reference: " + timeReference + " certainty: " + certainty + " system time offset: " + (time - now));
                    GpsLocationProvider.this.native_inject_time(time, timeReference, (int) certainty);
                    delay = 86400000;
                } else {
                    if (GpsLocationProvider.DEBUG) {
                        Log.d("GpsLocationProvider", "requestTime failed");
                    }
                    delay = 300000;
                }
                GpsLocationProvider.this.sendMessage(10, 0, null);
                if (GpsLocationProvider.this.mPeriodicTimeInjection) {
                    GpsLocationProvider.this.mHandler.sendEmptyMessageDelayed(5, delay);
                }
                GpsLocationProvider.this.mWakeLock.release();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDownloadXtraData() {
        if (this.mDownloadXtraDataPending == 1) {
            return;
        }
        if (!this.mNetworkAvailable) {
            this.mDownloadXtraDataPending = 0;
            return;
        }
        this.mDownloadXtraDataPending = 1;
        this.mWakeLock.acquire();
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() { // from class: com.android.server.location.GpsLocationProvider.5
            @Override // java.lang.Runnable
            public void run() {
                GpsXtraDownloader xtraDownloader = new GpsXtraDownloader(GpsLocationProvider.this.mContext, GpsLocationProvider.this.mProperties);
                byte[] data = xtraDownloader.downloadXtraData();
                if (data != null) {
                    if (GpsLocationProvider.DEBUG) {
                        Log.d("GpsLocationProvider", "calling native_inject_xtra_data");
                    }
                    GpsLocationProvider.this.native_inject_xtra_data(data, data.length);
                }
                GpsLocationProvider.this.sendMessage(11, 0, null);
                if (data == null) {
                    GpsLocationProvider.this.mHandler.sendEmptyMessageDelayed(6, GpsLocationProvider.RETRY_INTERVAL);
                }
                GpsLocationProvider.this.mWakeLock.release();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUpdateLocation(Location location) {
        if (location.hasAccuracy()) {
            native_inject_location(location.getLatitude(), location.getLongitude(), location.getAccuracy());
        }
    }

    @Override // com.android.server.location.LocationProviderInterface
    public void enable() {
        synchronized (this.mLock) {
            if (this.mEnabled) {
                return;
            }
            this.mEnabled = true;
            sendMessage(2, 1, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEnable() {
        if (DEBUG) {
            Log.d("GpsLocationProvider", "handleEnable");
        }
        boolean enabled = native_init();
        if (enabled) {
            this.mSupportsXtra = native_supports_xtra();
            if (this.mSuplServerHost != null) {
                native_set_agps_server(1, this.mSuplServerHost, this.mSuplServerPort);
            }
            if (this.mC2KServerHost != null) {
                native_set_agps_server(2, this.mC2KServerHost, this.mC2KServerPort);
                return;
            }
            return;
        }
        synchronized (this.mLock) {
            this.mEnabled = false;
        }
        Log.w("GpsLocationProvider", "Failed to enable location provider");
    }

    @Override // com.android.server.location.LocationProviderInterface
    public void disable() {
        synchronized (this.mLock) {
            if (this.mEnabled) {
                this.mEnabled = false;
                sendMessage(2, 0, null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisable() {
        if (DEBUG) {
            Log.d("GpsLocationProvider", "handleDisable");
        }
        stopNavigating();
        this.mAlarmManager.cancel(this.mWakeupIntent);
        this.mAlarmManager.cancel(this.mTimeoutIntent);
        native_cleanup();
    }

    @Override // com.android.server.location.LocationProviderInterface
    public boolean isEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mEnabled;
        }
        return z;
    }

    @Override // com.android.server.location.LocationProviderInterface
    public int getStatus(Bundle extras) {
        if (extras != null) {
            extras.putInt("satellites", this.mSvCount);
        }
        return this.mStatus;
    }

    private void updateStatus(int status, int svCount) {
        if (status != this.mStatus || svCount != this.mSvCount) {
            this.mStatus = status;
            this.mSvCount = svCount;
            this.mLocationExtras.putInt("satellites", svCount);
            this.mStatusUpdateTime = SystemClock.elapsedRealtime();
        }
    }

    @Override // com.android.server.location.LocationProviderInterface
    public long getStatusUpdateTime() {
        return this.mStatusUpdateTime;
    }

    @Override // com.android.server.location.LocationProviderInterface
    public void setRequest(ProviderRequest request, WorkSource source) {
        sendMessage(3, 0, new GpsRequest(request, source));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetRequest(ProviderRequest request, WorkSource source) {
        boolean singleShot = false;
        if (request.locationRequests != null && request.locationRequests.size() > 0) {
            singleShot = true;
            for (LocationRequest lr : request.locationRequests) {
                if (lr.getNumUpdates() != 1) {
                    singleShot = false;
                }
            }
        }
        if (DEBUG) {
            Log.d("GpsLocationProvider", "setRequest " + request);
        }
        if (request.reportLocation) {
            updateClientUids(source);
            this.mFixInterval = (int) request.interval;
            if (this.mFixInterval != request.interval) {
                Log.w("GpsLocationProvider", "interval overflow: " + request.interval);
                this.mFixInterval = Integer.MAX_VALUE;
            }
            if (this.mStarted && hasCapability(1)) {
                if (!native_set_position_mode(this.mPositionMode, 0, this.mFixInterval, 0, 0)) {
                    Log.e("GpsLocationProvider", "set_position_mode failed in setMinTime()");
                    return;
                }
                return;
            } else if (!this.mStarted) {
                startNavigating(singleShot);
                return;
            } else {
                return;
            }
        }
        updateClientUids(new WorkSource());
        stopNavigating();
        this.mAlarmManager.cancel(this.mWakeupIntent);
        this.mAlarmManager.cancel(this.mTimeoutIntent);
    }

    /* loaded from: GpsLocationProvider$Listener.class */
    private final class Listener implements IBinder.DeathRecipient {
        final IGpsStatusListener mListener;

        Listener(IGpsStatusListener listener) {
            this.mListener = listener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            if (GpsLocationProvider.DEBUG) {
                Log.d("GpsLocationProvider", "GPS status listener died");
            }
            synchronized (GpsLocationProvider.this.mListeners) {
                GpsLocationProvider.this.mListeners.remove(this);
            }
            if (this.mListener != null) {
                this.mListener.asBinder().unlinkToDeath(this, 0);
            }
        }
    }

    private void updateClientUids(WorkSource source) {
        WorkSource[] changes = this.mClientSource.setReturningDiffs(source);
        if (changes == null) {
            return;
        }
        WorkSource newWork = changes[0];
        WorkSource goneWork = changes[1];
        if (newWork != null) {
            int lastuid = -1;
            for (int i = 0; i < newWork.size(); i++) {
                try {
                    int uid = newWork.get(i);
                    this.mAppOpsService.startOperation(AppOpsManager.getToken(this.mAppOpsService), 2, uid, newWork.getName(i));
                    if (uid != lastuid) {
                        lastuid = uid;
                        this.mBatteryStats.noteStartGps(uid);
                    }
                } catch (RemoteException e) {
                    Log.w("GpsLocationProvider", "RemoteException", e);
                }
            }
        }
        if (goneWork != null) {
            int lastuid2 = -1;
            for (int i2 = 0; i2 < goneWork.size(); i2++) {
                try {
                    int uid2 = goneWork.get(i2);
                    this.mAppOpsService.finishOperation(AppOpsManager.getToken(this.mAppOpsService), 2, uid2, goneWork.getName(i2));
                    if (uid2 != lastuid2) {
                        lastuid2 = uid2;
                        this.mBatteryStats.noteStopGps(uid2);
                    }
                } catch (RemoteException e2) {
                    Log.w("GpsLocationProvider", "RemoteException", e2);
                }
            }
        }
    }

    @Override // com.android.server.location.LocationProviderInterface
    public boolean sendExtraCommand(String command, Bundle extras) {
        long identity = Binder.clearCallingIdentity();
        boolean result = false;
        if ("delete_aiding_data".equals(command)) {
            result = deleteAidingData(extras);
        } else if ("force_time_injection".equals(command)) {
            sendMessage(5, 0, null);
            result = true;
        } else if ("force_xtra_injection".equals(command)) {
            if (this.mSupportsXtra) {
                xtraDownloadRequest();
                result = true;
            }
        } else {
            Log.w("GpsLocationProvider", "sendExtraCommand: unknown command " + command);
        }
        Binder.restoreCallingIdentity(identity);
        return result;
    }

    private boolean deleteAidingData(Bundle extras) {
        int flags;
        if (extras == null) {
            flags = 65535;
        } else {
            flags = 0;
            if (extras.getBoolean("ephemeris")) {
                flags = 0 | 1;
            }
            if (extras.getBoolean("almanac")) {
                flags |= 2;
            }
            if (extras.getBoolean(BrowserContract.Bookmarks.POSITION)) {
                flags |= 4;
            }
            if (extras.getBoolean(DropBoxManager.EXTRA_TIME)) {
                flags |= 8;
            }
            if (extras.getBoolean("iono")) {
                flags |= 16;
            }
            if (extras.getBoolean("utc")) {
                flags |= 32;
            }
            if (extras.getBoolean(BatteryManager.EXTRA_HEALTH)) {
                flags |= 64;
            }
            if (extras.getBoolean("svdir")) {
                flags |= 128;
            }
            if (extras.getBoolean("svsteer")) {
                flags |= 256;
            }
            if (extras.getBoolean("sadata")) {
                flags |= 512;
            }
            if (extras.getBoolean("rti")) {
                flags |= 1024;
            }
            if (extras.getBoolean("celldb-info")) {
                flags |= 32768;
            }
            if (extras.getBoolean("all")) {
                flags |= 65535;
            }
        }
        if (flags != 0) {
            native_delete_aiding_data(flags);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNavigating(boolean singleShot) {
        String mode;
        if (!this.mStarted) {
            if (DEBUG) {
                Log.d("GpsLocationProvider", "startNavigating, singleShot is " + singleShot);
            }
            this.mTimeToFirstFix = 0;
            this.mLastFixTime = 0L;
            this.mStarted = true;
            this.mSingleShot = singleShot;
            this.mPositionMode = 0;
            if (Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.ASSISTED_GPS_ENABLED, 1) != 0) {
                if (singleShot && hasCapability(4)) {
                    this.mPositionMode = 2;
                } else if (hasCapability(2)) {
                    this.mPositionMode = 1;
                }
            }
            if (DEBUG) {
                switch (this.mPositionMode) {
                    case 0:
                        mode = "standalone";
                        break;
                    case 1:
                        mode = "MS_BASED";
                        break;
                    case 2:
                        mode = "MS_ASSISTED";
                        break;
                    default:
                        mode = "unknown";
                        break;
                }
                Log.d("GpsLocationProvider", "setting position_mode to " + mode);
            }
            int interval = hasCapability(1) ? this.mFixInterval : 1000;
            if (!native_set_position_mode(this.mPositionMode, 0, interval, 0, 0)) {
                this.mStarted = false;
                Log.e("GpsLocationProvider", "set_position_mode failed in startNavigating()");
            } else if (!native_start()) {
                this.mStarted = false;
                Log.e("GpsLocationProvider", "native_start failed in startNavigating()");
            } else {
                updateStatus(1, 0);
                this.mFixRequestTime = System.currentTimeMillis();
                if (!hasCapability(1) && this.mFixInterval >= 60000) {
                    this.mAlarmManager.set(2, SystemClock.elapsedRealtime() + DateUtils.MINUTE_IN_MILLIS, this.mTimeoutIntent);
                }
            }
        }
    }

    private void stopNavigating() {
        if (DEBUG) {
            Log.d("GpsLocationProvider", "stopNavigating");
        }
        if (this.mStarted) {
            this.mStarted = false;
            this.mSingleShot = false;
            native_stop();
            this.mTimeToFirstFix = 0;
            this.mLastFixTime = 0L;
            this.mLocationFlags = 0;
            updateStatus(1, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hibernate() {
        stopNavigating();
        this.mAlarmManager.cancel(this.mTimeoutIntent);
        this.mAlarmManager.cancel(this.mWakeupIntent);
        long now = SystemClock.elapsedRealtime();
        this.mAlarmManager.set(2, now + this.mFixInterval, this.mWakeupIntent);
    }

    private boolean hasCapability(int capability) {
        return (this.mEngineCapabilities & capability) != 0;
    }

    private void reportLocation(int flags, double latitude, double longitude, double altitude, float speed, float bearing, float accuracy, long timestamp) {
        if (VERBOSE) {
            Log.v("GpsLocationProvider", "reportLocation lat: " + latitude + " long: " + longitude + " timestamp: " + timestamp);
        }
        synchronized (this.mLocation) {
            this.mLocationFlags = flags;
            if ((flags & 1) == 1) {
                this.mLocation.setLatitude(latitude);
                this.mLocation.setLongitude(longitude);
                this.mLocation.setTime(timestamp);
                this.mLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }
            if ((flags & 2) == 2) {
                this.mLocation.setAltitude(altitude);
            } else {
                this.mLocation.removeAltitude();
            }
            if ((flags & 4) == 4) {
                this.mLocation.setSpeed(speed);
            } else {
                this.mLocation.removeSpeed();
            }
            if ((flags & 8) == 8) {
                this.mLocation.setBearing(bearing);
            } else {
                this.mLocation.removeBearing();
            }
            if ((flags & 16) == 16) {
                this.mLocation.setAccuracy(accuracy);
            } else {
                this.mLocation.removeAccuracy();
            }
            this.mLocation.setExtras(this.mLocationExtras);
            try {
                this.mILocationManager.reportLocation(this.mLocation, false);
            } catch (RemoteException e) {
                Log.e("GpsLocationProvider", "RemoteException calling reportLocation");
            }
        }
        this.mLastFixTime = System.currentTimeMillis();
        if (this.mTimeToFirstFix == 0 && (flags & 1) == 1) {
            this.mTimeToFirstFix = (int) (this.mLastFixTime - this.mFixRequestTime);
            if (DEBUG) {
                Log.d("GpsLocationProvider", "TTFF: " + this.mTimeToFirstFix);
            }
            synchronized (this.mListeners) {
                int size = this.mListeners.size();
                for (int i = 0; i < size; i++) {
                    Listener listener = this.mListeners.get(i);
                    try {
                        listener.mListener.onFirstFix(this.mTimeToFirstFix);
                    } catch (RemoteException e2) {
                        Log.w("GpsLocationProvider", "RemoteException in stopNavigating");
                        this.mListeners.remove(listener);
                        size--;
                    }
                }
            }
        }
        if (this.mSingleShot) {
            stopNavigating();
        }
        if (this.mStarted && this.mStatus != 2) {
            if (!hasCapability(1) && this.mFixInterval < 60000) {
                this.mAlarmManager.cancel(this.mTimeoutIntent);
            }
            Intent intent = new Intent(LocationManager.GPS_FIX_CHANGE_ACTION);
            intent.putExtra("enabled", true);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
            updateStatus(2, this.mSvCount);
        }
        if (!hasCapability(1) && this.mStarted && this.mFixInterval > 10000) {
            if (DEBUG) {
                Log.d("GpsLocationProvider", "got fix, hibernating");
            }
            hibernate();
        }
    }

    private void reportStatus(int status) {
        if (DEBUG) {
            Log.v("GpsLocationProvider", "reportStatus status: " + status);
        }
        synchronized (this.mListeners) {
            boolean wasNavigating = this.mNavigating;
            switch (status) {
                case 1:
                    this.mNavigating = true;
                    this.mEngineOn = true;
                    break;
                case 2:
                    this.mNavigating = false;
                    break;
                case 3:
                    this.mEngineOn = true;
                    break;
                case 4:
                    this.mEngineOn = false;
                    this.mNavigating = false;
                    break;
            }
            if (wasNavigating != this.mNavigating) {
                int size = this.mListeners.size();
                for (int i = 0; i < size; i++) {
                    Listener listener = this.mListeners.get(i);
                    try {
                        if (this.mNavigating) {
                            listener.mListener.onGpsStarted();
                        } else {
                            listener.mListener.onGpsStopped();
                        }
                    } catch (RemoteException e) {
                        Log.w("GpsLocationProvider", "RemoteException in reportStatus");
                        this.mListeners.remove(listener);
                        size--;
                    }
                }
                Intent intent = new Intent(LocationManager.GPS_ENABLED_CHANGE_ACTION);
                intent.putExtra("enabled", this.mNavigating);
                this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
            }
        }
    }

    private void reportSvStatus() {
        int svCount = native_read_sv_status(this.mSvs, this.mSnrs, this.mSvElevations, this.mSvAzimuths, this.mSvMasks);
        synchronized (this.mListeners) {
            int size = this.mListeners.size();
            for (int i = 0; i < size; i++) {
                Listener listener = this.mListeners.get(i);
                try {
                    listener.mListener.onSvStatusChanged(svCount, this.mSvs, this.mSnrs, this.mSvElevations, this.mSvAzimuths, this.mSvMasks[0], this.mSvMasks[1], this.mSvMasks[2]);
                } catch (RemoteException e) {
                    Log.w("GpsLocationProvider", "RemoteException in reportSvInfo");
                    this.mListeners.remove(listener);
                    size--;
                }
            }
        }
        if (VERBOSE) {
            Log.v("GpsLocationProvider", "SV count: " + svCount + " ephemerisMask: " + Integer.toHexString(this.mSvMasks[0]) + " almanacMask: " + Integer.toHexString(this.mSvMasks[1]));
            for (int i2 = 0; i2 < svCount; i2++) {
                Log.v("GpsLocationProvider", "sv: " + this.mSvs[i2] + " snr: " + (this.mSnrs[i2] / 10.0f) + " elev: " + this.mSvElevations[i2] + " azimuth: " + this.mSvAzimuths[i2] + ((this.mSvMasks[0] & (1 << (this.mSvs[i2] - 1))) == 0 ? "  " : " E") + ((this.mSvMasks[1] & (1 << (this.mSvs[i2] - 1))) == 0 ? "  " : " A") + ((this.mSvMasks[2] & (1 << (this.mSvs[i2] - 1))) == 0 ? "" : TokenNames.U));
            }
        }
        updateStatus(this.mStatus, Integer.bitCount(this.mSvMasks[2]));
        if (this.mNavigating && this.mStatus == 2 && this.mLastFixTime > 0 && System.currentTimeMillis() - this.mLastFixTime > RECENT_FIX_TIMEOUT) {
            Intent intent = new Intent(LocationManager.GPS_FIX_CHANGE_ACTION);
            intent.putExtra("enabled", false);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
            updateStatus(1, this.mSvCount);
        }
    }

    private void reportAGpsStatus(int type, int status, int ipaddr) {
        switch (status) {
            case 1:
                if (DEBUG) {
                    Log.d("GpsLocationProvider", "GPS_REQUEST_AGPS_DATA_CONN");
                }
                this.mAGpsDataConnectionState = 1;
                int result = this.mConnMgr.startUsingNetworkFeature(0, Phone.FEATURE_ENABLE_SUPL);
                this.mAGpsDataConnectionIpAddr = ipaddr;
                if (result == 0) {
                    if (DEBUG) {
                        Log.d("GpsLocationProvider", "PhoneConstants.APN_ALREADY_ACTIVE");
                    }
                    if (this.mAGpsApn != null) {
                        Log.d("GpsLocationProvider", "mAGpsDataConnectionIpAddr " + this.mAGpsDataConnectionIpAddr);
                        if (this.mAGpsDataConnectionIpAddr != -1) {
                            if (DEBUG) {
                                Log.d("GpsLocationProvider", "call requestRouteToHost");
                            }
                            boolean route_result = this.mConnMgr.requestRouteToHost(3, this.mAGpsDataConnectionIpAddr);
                            if (!route_result) {
                                Log.d("GpsLocationProvider", "call requestRouteToHost failed");
                            }
                        }
                        native_agps_data_conn_open(this.mAGpsApn);
                        this.mAGpsDataConnectionState = 2;
                        return;
                    }
                    Log.e("GpsLocationProvider", "mAGpsApn not set when receiving PhoneConstants.APN_ALREADY_ACTIVE");
                    this.mAGpsDataConnectionState = 0;
                    native_agps_data_conn_failed();
                    return;
                } else if (result == 1) {
                    if (DEBUG) {
                        Log.d("GpsLocationProvider", "PhoneConstants.APN_REQUEST_STARTED");
                        return;
                    }
                    return;
                } else {
                    if (DEBUG) {
                        Log.d("GpsLocationProvider", "startUsingNetworkFeature failed, value is " + result);
                    }
                    this.mAGpsDataConnectionState = 0;
                    native_agps_data_conn_failed();
                    return;
                }
            case 2:
                if (DEBUG) {
                    Log.d("GpsLocationProvider", "GPS_RELEASE_AGPS_DATA_CONN");
                }
                if (this.mAGpsDataConnectionState != 0) {
                    this.mConnMgr.stopUsingNetworkFeature(0, Phone.FEATURE_ENABLE_SUPL);
                    native_agps_data_conn_closed();
                    this.mAGpsDataConnectionState = 0;
                    return;
                }
                return;
            case 3:
                if (DEBUG) {
                    Log.d("GpsLocationProvider", "GPS_AGPS_DATA_CONNECTED");
                    return;
                }
                return;
            case 4:
                if (DEBUG) {
                    Log.d("GpsLocationProvider", "GPS_AGPS_DATA_CONN_DONE");
                    return;
                }
                return;
            case 5:
                if (DEBUG) {
                    Log.d("GpsLocationProvider", "GPS_AGPS_DATA_CONN_FAILED");
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void reportNmea(long timestamp) {
        synchronized (this.mListeners) {
            int size = this.mListeners.size();
            if (size > 0) {
                int length = native_read_nmea(this.mNmeaBuffer, this.mNmeaBuffer.length);
                String nmea = new String(this.mNmeaBuffer, 0, length);
                for (int i = 0; i < size; i++) {
                    Listener listener = this.mListeners.get(i);
                    try {
                        listener.mListener.onNmeaReceived(timestamp, nmea);
                    } catch (RemoteException e) {
                        Log.w("GpsLocationProvider", "RemoteException in reportNmea");
                        this.mListeners.remove(listener);
                        size--;
                    }
                }
            }
        }
    }

    private void setEngineCapabilities(int capabilities) {
        this.mEngineCapabilities = capabilities;
        if (!hasCapability(16) && !this.mPeriodicTimeInjection) {
            this.mPeriodicTimeInjection = true;
            requestUtcTime();
        }
    }

    private void xtraDownloadRequest() {
        if (DEBUG) {
            Log.d("GpsLocationProvider", "xtraDownloadRequest");
        }
        sendMessage(6, 0, null);
    }

    private Location buildLocation(int flags, double latitude, double longitude, double altitude, float speed, float bearing, float accuracy, long timestamp) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        if ((flags & 1) == 1) {
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setTime(timestamp);
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        if ((flags & 2) == 2) {
            location.setAltitude(altitude);
        }
        if ((flags & 4) == 4) {
            location.setSpeed(speed);
        }
        if ((flags & 8) == 8) {
            location.setBearing(bearing);
        }
        if ((flags & 16) == 16) {
            location.setAccuracy(accuracy);
        }
        return location;
    }

    private int getGeofenceStatus(int status) {
        switch (status) {
            case GPS_GEOFENCE_ERROR_GENERIC /* -149 */:
                return 5;
            case -103:
                return 4;
            case -102:
                return 3;
            case -101:
                return 2;
            case 0:
                return 0;
            case 100:
                return 1;
            default:
                return -1;
        }
    }

    private void reportGeofenceTransition(int geofenceId, int flags, double latitude, double longitude, double altitude, float speed, float bearing, float accuracy, long timestamp, int transition, long transitionTimestamp) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        Location location = buildLocation(flags, latitude, longitude, altitude, speed, bearing, accuracy, timestamp);
        this.mGeofenceHardwareImpl.reportGeofenceTransition(geofenceId, location, transition, transitionTimestamp, 0, FusedBatchOptions.SourceTechnologies.GNSS);
    }

    private void reportGeofenceStatus(int status, int flags, double latitude, double longitude, double altitude, float speed, float bearing, float accuracy, long timestamp) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        Location location = buildLocation(flags, latitude, longitude, altitude, speed, bearing, accuracy, timestamp);
        int monitorStatus = 1;
        if (status == 2) {
            monitorStatus = 0;
        }
        this.mGeofenceHardwareImpl.reportGeofenceMonitorStatus(0, monitorStatus, location, FusedBatchOptions.SourceTechnologies.GNSS);
    }

    private void reportGeofenceAddStatus(int geofenceId, int status) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        this.mGeofenceHardwareImpl.reportGeofenceAddStatus(geofenceId, getGeofenceStatus(status));
    }

    private void reportGeofenceRemoveStatus(int geofenceId, int status) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        this.mGeofenceHardwareImpl.reportGeofenceRemoveStatus(geofenceId, getGeofenceStatus(status));
    }

    private void reportGeofencePauseStatus(int geofenceId, int status) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        this.mGeofenceHardwareImpl.reportGeofencePauseStatus(geofenceId, getGeofenceStatus(status));
    }

    private void reportGeofenceResumeStatus(int geofenceId, int status) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        this.mGeofenceHardwareImpl.reportGeofenceResumeStatus(geofenceId, getGeofenceStatus(status));
    }

    public INetInitiatedListener getNetInitiatedListener() {
        return this.mNetInitiatedListener;
    }

    public void reportNiNotification(int notificationId, int niType, int notifyFlags, int timeout, int defaultResponse, String requestorId, String text, int requestorIdEncoding, int textEncoding, String extras) {
        Log.i("GpsLocationProvider", "reportNiNotification: entered");
        Log.i("GpsLocationProvider", "notificationId: " + notificationId + ", niType: " + niType + ", notifyFlags: " + notifyFlags + ", timeout: " + timeout + ", defaultResponse: " + defaultResponse);
        Log.i("GpsLocationProvider", "requestorId: " + requestorId + ", text: " + text + ", requestorIdEncoding: " + requestorIdEncoding + ", textEncoding: " + textEncoding);
        GpsNetInitiatedHandler.GpsNiNotification notification = new GpsNetInitiatedHandler.GpsNiNotification();
        notification.notificationId = notificationId;
        notification.niType = niType;
        notification.needNotify = (notifyFlags & 1) != 0;
        notification.needVerify = (notifyFlags & 2) != 0;
        notification.privacyOverride = (notifyFlags & 4) != 0;
        notification.timeout = timeout;
        notification.defaultResponse = defaultResponse;
        notification.requestorId = requestorId;
        notification.text = text;
        notification.requestorIdEncoding = requestorIdEncoding;
        notification.textEncoding = textEncoding;
        Bundle bundle = new Bundle();
        if (extras == null) {
            extras = "";
        }
        Properties extraProp = new Properties();
        try {
            extraProp.load(new StringReader(extras));
        } catch (IOException e) {
            Log.e("GpsLocationProvider", "reportNiNotification cannot parse extras data: " + extras);
        }
        for (Map.Entry<Object, Object> ent : extraProp.entrySet()) {
            bundle.putString((String) ent.getKey(), (String) ent.getValue());
        }
        notification.extras = bundle;
        this.mNIHandler.handleNiNotification(notification);
    }

    private void requestSetID(int flags) {
        String data_temp;
        TelephonyManager phone = (TelephonyManager) this.mContext.getSystemService("phone");
        int type = 0;
        String data = "";
        if ((flags & 1) == 1) {
            String data_temp2 = phone.getSubscriberId();
            if (data_temp2 != null) {
                data = data_temp2;
                type = 1;
            }
        } else if ((flags & 2) == 2 && (data_temp = phone.getLine1Number()) != null) {
            data = data_temp;
            type = 2;
        }
        native_agps_set_id(type, data);
    }

    private void requestUtcTime() {
        sendMessage(5, 0, null);
    }

    private void requestRefLocation(int flags) {
        int type;
        TelephonyManager phone = (TelephonyManager) this.mContext.getSystemService("phone");
        int phoneType = phone.getPhoneType();
        if (phoneType != 1) {
            if (phoneType == 2) {
                Log.e("GpsLocationProvider", "CDMA not supported.");
                return;
            }
            return;
        }
        GsmCellLocation gsm_cell = (GsmCellLocation) phone.getCellLocation();
        if (gsm_cell != null && phone.getNetworkOperator() != null && phone.getNetworkOperator().length() > 3) {
            int mcc = Integer.parseInt(phone.getNetworkOperator().substring(0, 3));
            int mnc = Integer.parseInt(phone.getNetworkOperator().substring(3));
            int networkType = phone.getNetworkType();
            if (networkType == 3 || networkType == 8 || networkType == 9 || networkType == 10 || networkType == 15) {
                type = 2;
            } else {
                type = 1;
            }
            native_agps_set_ref_location_cellid(type, mcc, mnc, gsm_cell.getLac(), gsm_cell.getCid());
            return;
        }
        Log.e("GpsLocationProvider", "Error getting cell location info.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage(int message, int arg, Object obj) {
        this.mWakeLock.acquire();
        this.mHandler.obtainMessage(message, arg, 1, obj).sendToTarget();
    }

    /* loaded from: GpsLocationProvider$ProviderHandler.class */
    private final class ProviderHandler extends Handler {
        public ProviderHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int message = msg.what;
            switch (message) {
                case 2:
                    if (msg.arg1 == 1) {
                        GpsLocationProvider.this.handleEnable();
                        break;
                    } else {
                        GpsLocationProvider.this.handleDisable();
                        break;
                    }
                case 3:
                    GpsRequest gpsRequest = (GpsRequest) msg.obj;
                    GpsLocationProvider.this.handleSetRequest(gpsRequest.request, gpsRequest.source);
                    break;
                case 4:
                    GpsLocationProvider.this.handleUpdateNetworkState(msg.arg1, (NetworkInfo) msg.obj);
                    break;
                case 5:
                    GpsLocationProvider.this.handleInjectNtpTime();
                    break;
                case 6:
                    if (GpsLocationProvider.this.mSupportsXtra) {
                        GpsLocationProvider.this.handleDownloadXtraData();
                        break;
                    }
                    break;
                case 7:
                    GpsLocationProvider.this.handleUpdateLocation((Location) msg.obj);
                    break;
                case 10:
                    GpsLocationProvider.this.mInjectNtpTimePending = 2;
                    break;
                case 11:
                    GpsLocationProvider.this.mDownloadXtraDataPending = 2;
                    break;
            }
            if (msg.arg2 == 1) {
                GpsLocationProvider.this.mWakeLock.release();
            }
        }
    }

    /* loaded from: GpsLocationProvider$NetworkLocationListener.class */
    private final class NetworkLocationListener implements LocationListener {
        private NetworkLocationListener() {
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider())) {
                GpsLocationProvider.this.handleUpdateLocation(location);
            }
        }

        @Override // android.location.LocationListener
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String provider) {
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String provider) {
        }
    }

    @Override // com.android.server.location.LocationProviderInterface
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        StringBuilder s = new StringBuilder();
        s.append("  mFixInterval=").append(this.mFixInterval).append(Separators.RETURN);
        s.append("  mEngineCapabilities=0x").append(Integer.toHexString(this.mEngineCapabilities)).append(" (");
        if (hasCapability(1)) {
            s.append("SCHED ");
        }
        if (hasCapability(2)) {
            s.append("MSB ");
        }
        if (hasCapability(4)) {
            s.append("MSA ");
        }
        if (hasCapability(8)) {
            s.append("SINGLE_SHOT ");
        }
        if (hasCapability(16)) {
            s.append("ON_DEMAND_TIME ");
        }
        s.append(")\n");
        s.append(native_get_internal_state());
        pw.append((CharSequence) s);
    }
}