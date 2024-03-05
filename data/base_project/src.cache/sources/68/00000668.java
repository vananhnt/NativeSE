package android.hardware.location;

import android.Manifest;
import android.content.Context;
import android.location.IFusedGeofenceHardware;
import android.location.IGpsGeofenceHardware;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: GeofenceHardwareImpl.class */
public final class GeofenceHardwareImpl {
    private static final String TAG = "GeofenceHardwareImpl";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private final Context mContext;
    private static GeofenceHardwareImpl sInstance;
    private PowerManager.WakeLock mWakeLock;
    private IFusedGeofenceHardware mFusedService;
    private IGpsGeofenceHardware mGpsService;
    private static final int GEOFENCE_TRANSITION_CALLBACK = 1;
    private static final int ADD_GEOFENCE_CALLBACK = 2;
    private static final int REMOVE_GEOFENCE_CALLBACK = 3;
    private static final int PAUSE_GEOFENCE_CALLBACK = 4;
    private static final int RESUME_GEOFENCE_CALLBACK = 5;
    private static final int GEOFENCE_CALLBACK_BINDER_DIED = 6;
    private static final int GEOFENCE_STATUS = 1;
    private static final int CALLBACK_ADD = 2;
    private static final int CALLBACK_REMOVE = 3;
    private static final int MONITOR_CALLBACK_BINDER_DIED = 4;
    private static final int REAPER_GEOFENCE_ADDED = 1;
    private static final int REAPER_MONITOR_CALLBACK_ADDED = 2;
    private static final int REAPER_REMOVED = 3;
    private static final int LOCATION_INVALID = 0;
    private static final int LOCATION_HAS_LAT_LONG = 1;
    private static final int LOCATION_HAS_ALTITUDE = 2;
    private static final int LOCATION_HAS_SPEED = 4;
    private static final int LOCATION_HAS_BEARING = 8;
    private static final int LOCATION_HAS_ACCURACY = 16;
    private static final int RESOLUTION_LEVEL_NONE = 1;
    private static final int RESOLUTION_LEVEL_COARSE = 2;
    private static final int RESOLUTION_LEVEL_FINE = 3;
    private final SparseArray<IGeofenceHardwareCallback> mGeofences = new SparseArray<>();
    private final ArrayList<IGeofenceHardwareMonitorCallback>[] mCallbacks = new ArrayList[2];
    private final ArrayList<Reaper> mReapers = new ArrayList<>();
    private int[] mSupportedMonitorTypes = new int[2];
    private Handler mGeofenceHandler = new Handler() { // from class: android.hardware.location.GeofenceHardwareImpl.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            IGeofenceHardwareCallback callback;
            IGeofenceHardwareCallback callback2;
            IGeofenceHardwareCallback callback3;
            IGeofenceHardwareCallback callback4;
            IGeofenceHardwareCallback callback5;
            switch (msg.what) {
                case 1:
                    GeofenceTransition geofenceTransition = (GeofenceTransition) msg.obj;
                    synchronized (GeofenceHardwareImpl.this.mGeofences) {
                        callback = (IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.get(geofenceTransition.mGeofenceId);
                        if (GeofenceHardwareImpl.DEBUG) {
                            Log.d(GeofenceHardwareImpl.TAG, "GeofenceTransistionCallback: GPS : GeofenceId: " + geofenceTransition.mGeofenceId + " Transition: " + geofenceTransition.mTransition + " Location: " + geofenceTransition.mLocation + Separators.COLON + GeofenceHardwareImpl.this.mGeofences);
                        }
                    }
                    if (callback != null) {
                        try {
                            callback.onGeofenceTransition(geofenceTransition.mGeofenceId, geofenceTransition.mTransition, geofenceTransition.mLocation, geofenceTransition.mTimestamp, geofenceTransition.mMonitoringType);
                        } catch (RemoteException e) {
                        }
                    }
                    GeofenceHardwareImpl.this.releaseWakeLock();
                    return;
                case 2:
                    int geofenceId = msg.arg1;
                    synchronized (GeofenceHardwareImpl.this.mGeofences) {
                        callback5 = (IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.get(geofenceId);
                    }
                    if (callback5 != null) {
                        try {
                            callback5.onGeofenceAdd(geofenceId, msg.arg2);
                        } catch (RemoteException e2) {
                            Log.i(GeofenceHardwareImpl.TAG, "Remote Exception:" + e2);
                        }
                    }
                    GeofenceHardwareImpl.this.releaseWakeLock();
                    return;
                case 3:
                    int geofenceId2 = msg.arg1;
                    synchronized (GeofenceHardwareImpl.this.mGeofences) {
                        callback4 = (IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.get(geofenceId2);
                    }
                    if (callback4 != null) {
                        try {
                            callback4.onGeofenceRemove(geofenceId2, msg.arg2);
                        } catch (RemoteException e3) {
                        }
                        synchronized (GeofenceHardwareImpl.this.mGeofences) {
                            GeofenceHardwareImpl.this.mGeofences.remove(geofenceId2);
                        }
                    }
                    GeofenceHardwareImpl.this.releaseWakeLock();
                    return;
                case 4:
                    int geofenceId3 = msg.arg1;
                    synchronized (GeofenceHardwareImpl.this.mGeofences) {
                        callback3 = (IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.get(geofenceId3);
                    }
                    if (callback3 != null) {
                        try {
                            callback3.onGeofencePause(geofenceId3, msg.arg2);
                        } catch (RemoteException e4) {
                        }
                    }
                    GeofenceHardwareImpl.this.releaseWakeLock();
                    return;
                case 5:
                    int geofenceId4 = msg.arg1;
                    synchronized (GeofenceHardwareImpl.this.mGeofences) {
                        callback2 = (IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.get(geofenceId4);
                    }
                    if (callback2 != null) {
                        try {
                            callback2.onGeofenceResume(geofenceId4, msg.arg2);
                        } catch (RemoteException e5) {
                        }
                    }
                    GeofenceHardwareImpl.this.releaseWakeLock();
                    return;
                case 6:
                    IGeofenceHardwareCallback callback6 = (IGeofenceHardwareCallback) msg.obj;
                    if (GeofenceHardwareImpl.DEBUG) {
                        Log.d(GeofenceHardwareImpl.TAG, "Geofence callback reaped:" + callback6);
                    }
                    int monitoringType = msg.arg1;
                    synchronized (GeofenceHardwareImpl.this.mGeofences) {
                        for (int i = 0; i < GeofenceHardwareImpl.this.mGeofences.size(); i++) {
                            if (((IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.valueAt(i)).equals(callback6)) {
                                int geofenceId5 = GeofenceHardwareImpl.this.mGeofences.keyAt(i);
                                GeofenceHardwareImpl.this.removeGeofence(GeofenceHardwareImpl.this.mGeofences.keyAt(i), monitoringType);
                                GeofenceHardwareImpl.this.mGeofences.remove(geofenceId5);
                            }
                        }
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private Handler mCallbacksHandler = new Handler() { // from class: android.hardware.location.GeofenceHardwareImpl.2
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Location location = (Location) msg.obj;
                    int val = msg.arg1;
                    int monitoringType = msg.arg2;
                    boolean available = val == 0;
                    ArrayList<IGeofenceHardwareMonitorCallback> callbackList = GeofenceHardwareImpl.this.mCallbacks[monitoringType];
                    if (callbackList != null) {
                        if (GeofenceHardwareImpl.DEBUG) {
                            Log.d(GeofenceHardwareImpl.TAG, "MonitoringSystemChangeCallback: GPS : " + available);
                        }
                        Iterator i$ = callbackList.iterator();
                        while (i$.hasNext()) {
                            IGeofenceHardwareMonitorCallback c = i$.next();
                            try {
                                c.onMonitoringSystemChange(monitoringType, available, location);
                            } catch (RemoteException e) {
                            }
                        }
                    }
                    GeofenceHardwareImpl.this.releaseWakeLock();
                    return;
                case 2:
                    int monitoringType2 = msg.arg1;
                    IGeofenceHardwareMonitorCallback callback = (IGeofenceHardwareMonitorCallback) msg.obj;
                    ArrayList<IGeofenceHardwareMonitorCallback> callbackList2 = GeofenceHardwareImpl.this.mCallbacks[monitoringType2];
                    if (callbackList2 == null) {
                        callbackList2 = new ArrayList<>();
                        GeofenceHardwareImpl.this.mCallbacks[monitoringType2] = callbackList2;
                    }
                    if (!callbackList2.contains(callback)) {
                        callbackList2.add(callback);
                        return;
                    }
                    return;
                case 3:
                    int monitoringType3 = msg.arg1;
                    IGeofenceHardwareMonitorCallback callback2 = (IGeofenceHardwareMonitorCallback) msg.obj;
                    ArrayList<IGeofenceHardwareMonitorCallback> callbackList3 = GeofenceHardwareImpl.this.mCallbacks[monitoringType3];
                    if (callbackList3 != null) {
                        callbackList3.remove(callback2);
                        return;
                    }
                    return;
                case 4:
                    IGeofenceHardwareMonitorCallback callback3 = (IGeofenceHardwareMonitorCallback) msg.obj;
                    if (GeofenceHardwareImpl.DEBUG) {
                        Log.d(GeofenceHardwareImpl.TAG, "Monitor callback reaped:" + callback3);
                    }
                    ArrayList<IGeofenceHardwareMonitorCallback> callbackList4 = GeofenceHardwareImpl.this.mCallbacks[msg.arg1];
                    if (callbackList4 != null && callbackList4.contains(callback3)) {
                        callbackList4.remove(callback3);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private Handler mReaperHandler = new Handler() { // from class: android.hardware.location.GeofenceHardwareImpl.3
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    IGeofenceHardwareCallback callback = (IGeofenceHardwareCallback) msg.obj;
                    int monitoringType = msg.arg1;
                    Reaper r = new Reaper(callback, monitoringType);
                    if (!GeofenceHardwareImpl.this.mReapers.contains(r)) {
                        GeofenceHardwareImpl.this.mReapers.add(r);
                        IBinder b = callback.asBinder();
                        try {
                            b.linkToDeath(r, 0);
                            return;
                        } catch (RemoteException e) {
                            return;
                        }
                    }
                    return;
                case 2:
                    IGeofenceHardwareMonitorCallback monitorCallback = (IGeofenceHardwareMonitorCallback) msg.obj;
                    int monitoringType2 = msg.arg1;
                    Reaper r2 = new Reaper(monitorCallback, monitoringType2);
                    if (!GeofenceHardwareImpl.this.mReapers.contains(r2)) {
                        GeofenceHardwareImpl.this.mReapers.add(r2);
                        IBinder b2 = monitorCallback.asBinder();
                        try {
                            b2.linkToDeath(r2, 0);
                            return;
                        } catch (RemoteException e2) {
                            return;
                        }
                    }
                    return;
                case 3:
                    GeofenceHardwareImpl.this.mReapers.remove((Reaper) msg.obj);
                    return;
                default:
                    return;
            }
        }
    };

    public static synchronized GeofenceHardwareImpl getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new GeofenceHardwareImpl(context);
        }
        return sInstance;
    }

    private GeofenceHardwareImpl(Context context) {
        this.mContext = context;
        setMonitorAvailability(0, 2);
        setMonitorAvailability(1, 2);
    }

    private void acquireWakeLock() {
        if (this.mWakeLock == null) {
            PowerManager powerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
            this.mWakeLock = powerManager.newWakeLock(1, TAG);
        }
        this.mWakeLock.acquire();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseWakeLock() {
        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
    }

    private void updateGpsHardwareAvailability() {
        boolean gpsSupported;
        try {
            gpsSupported = this.mGpsService.isHardwareGeofenceSupported();
        } catch (RemoteException e) {
            Log.e(TAG, "Remote Exception calling LocationManagerService");
            gpsSupported = false;
        }
        if (gpsSupported) {
            setMonitorAvailability(0, 0);
        }
    }

    private void updateFusedHardwareAvailability() {
        boolean fusedSupported;
        try {
            fusedSupported = this.mFusedService.isSupported();
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException calling LocationManagerService");
            fusedSupported = false;
        }
        if (fusedSupported) {
            setMonitorAvailability(1, 0);
        }
    }

    public void setGpsHardwareGeofence(IGpsGeofenceHardware service) {
        if (this.mGpsService == null) {
            this.mGpsService = service;
            updateGpsHardwareAvailability();
        } else if (service == null) {
            this.mGpsService = null;
            Log.w(TAG, "GPS Geofence Hardware service seems to have crashed");
        } else {
            Log.e(TAG, "Error: GpsService being set again.");
        }
    }

    public void setFusedGeofenceHardware(IFusedGeofenceHardware service) {
        if (this.mFusedService == null) {
            this.mFusedService = service;
            updateFusedHardwareAvailability();
        } else if (service == null) {
            this.mFusedService = null;
            Log.w(TAG, "Fused Geofence Hardware service seems to have crashed");
        } else {
            Log.e(TAG, "Error: FusedService being set again");
        }
    }

    public int[] getMonitoringTypes() {
        boolean gpsSupported;
        boolean fusedSupported;
        synchronized (this.mSupportedMonitorTypes) {
            gpsSupported = this.mSupportedMonitorTypes[0] != 2;
            fusedSupported = this.mSupportedMonitorTypes[1] != 2;
        }
        return gpsSupported ? fusedSupported ? new int[]{0, 1} : new int[]{0} : fusedSupported ? new int[]{1} : new int[0];
    }

    public int getStatusOfMonitoringType(int monitoringType) {
        int i;
        synchronized (this.mSupportedMonitorTypes) {
            if (monitoringType >= this.mSupportedMonitorTypes.length || monitoringType < 0) {
                throw new IllegalArgumentException("Unknown monitoring type");
            }
            i = this.mSupportedMonitorTypes[monitoringType];
        }
        return i;
    }

    public boolean addCircularFence(int geofenceId, int monitoringType, double latitude, double longitude, double radius, int lastTransition, int monitorTransitions, int notificationResponsivenes, int unknownTimer, IGeofenceHardwareCallback callback) {
        boolean result;
        if (DEBUG) {
            Log.d(TAG, "addCircularFence: GeofenceId: " + geofenceId + " Latitude: " + latitude + " Longitude: " + longitude + " Radius: " + radius + " LastTransition: " + lastTransition + " MonitorTransition: " + monitorTransitions + " NotificationResponsiveness: " + notificationResponsivenes + " UnKnown Timer: " + unknownTimer + " MonitoringType: " + monitoringType);
        }
        synchronized (this.mGeofences) {
            this.mGeofences.put(geofenceId, callback);
        }
        switch (monitoringType) {
            case 0:
                if (this.mGpsService != null) {
                    try {
                        result = this.mGpsService.addCircularHardwareGeofence(geofenceId, latitude, longitude, radius, lastTransition, monitorTransitions, notificationResponsivenes, unknownTimer);
                        break;
                    } catch (RemoteException e) {
                        Log.e(TAG, "AddGeofence: Remote Exception calling LocationManagerService");
                        result = false;
                        break;
                    }
                } else {
                    return false;
                }
            case 1:
                if (this.mFusedService == null) {
                    return false;
                }
                GeofenceHardwareRequest request = GeofenceHardwareRequest.createCircularGeofence(latitude, longitude, radius);
                request.setUnknownTimer(unknownTimer);
                request.setNotificationResponsiveness(notificationResponsivenes);
                request.setMonitorTransitions(monitorTransitions);
                request.setLastTransition(lastTransition);
                GeofenceHardwareRequestParcelable parcelableRequest = new GeofenceHardwareRequestParcelable(geofenceId, request);
                try {
                    this.mFusedService.addGeofences(new GeofenceHardwareRequestParcelable[]{parcelableRequest});
                    result = true;
                    break;
                } catch (RemoteException e2) {
                    Log.e(TAG, "AddGeofence: RemoteException calling LocationManagerService");
                    result = false;
                    break;
                }
            default:
                result = false;
                break;
        }
        if (result) {
            Message m = this.mReaperHandler.obtainMessage(1, callback);
            m.arg1 = monitoringType;
            this.mReaperHandler.sendMessage(m);
        } else {
            synchronized (this.mGeofences) {
                this.mGeofences.remove(geofenceId);
            }
        }
        if (DEBUG) {
            Log.d(TAG, "addCircularFence: Result is: " + result);
        }
        return result;
    }

    public boolean removeGeofence(int geofenceId, int monitoringType) {
        boolean result;
        if (DEBUG) {
            Log.d(TAG, "Remove Geofence: GeofenceId: " + geofenceId);
        }
        synchronized (this.mGeofences) {
            if (this.mGeofences.get(geofenceId) == null) {
                throw new IllegalArgumentException("Geofence " + geofenceId + " not registered.");
            }
        }
        switch (monitoringType) {
            case 0:
                if (this.mGpsService != null) {
                    try {
                        result = this.mGpsService.removeHardwareGeofence(geofenceId);
                        break;
                    } catch (RemoteException e) {
                        Log.e(TAG, "RemoveGeofence: Remote Exception calling LocationManagerService");
                        result = false;
                        break;
                    }
                } else {
                    return false;
                }
            case 1:
                if (this.mFusedService == null) {
                    return false;
                }
                try {
                    this.mFusedService.removeGeofences(new int[]{geofenceId});
                    result = true;
                    break;
                } catch (RemoteException e2) {
                    Log.e(TAG, "RemoveGeofence: RemoteException calling LocationManagerService");
                    result = false;
                    break;
                }
            default:
                result = false;
                break;
        }
        if (DEBUG) {
            Log.d(TAG, "removeGeofence: Result is: " + result);
        }
        return result;
    }

    public boolean pauseGeofence(int geofenceId, int monitoringType) {
        boolean result;
        if (DEBUG) {
            Log.d(TAG, "Pause Geofence: GeofenceId: " + geofenceId);
        }
        synchronized (this.mGeofences) {
            if (this.mGeofences.get(geofenceId) == null) {
                throw new IllegalArgumentException("Geofence " + geofenceId + " not registered.");
            }
        }
        switch (monitoringType) {
            case 0:
                if (this.mGpsService != null) {
                    try {
                        result = this.mGpsService.pauseHardwareGeofence(geofenceId);
                        break;
                    } catch (RemoteException e) {
                        Log.e(TAG, "PauseGeofence: Remote Exception calling LocationManagerService");
                        result = false;
                        break;
                    }
                } else {
                    return false;
                }
            case 1:
                if (this.mFusedService == null) {
                    return false;
                }
                try {
                    this.mFusedService.pauseMonitoringGeofence(geofenceId);
                    result = true;
                    break;
                } catch (RemoteException e2) {
                    Log.e(TAG, "PauseGeofence: RemoteException calling LocationManagerService");
                    result = false;
                    break;
                }
            default:
                result = false;
                break;
        }
        if (DEBUG) {
            Log.d(TAG, "pauseGeofence: Result is: " + result);
        }
        return result;
    }

    public boolean resumeGeofence(int geofenceId, int monitoringType, int monitorTransition) {
        boolean result;
        if (DEBUG) {
            Log.d(TAG, "Resume Geofence: GeofenceId: " + geofenceId);
        }
        synchronized (this.mGeofences) {
            if (this.mGeofences.get(geofenceId) == null) {
                throw new IllegalArgumentException("Geofence " + geofenceId + " not registered.");
            }
        }
        switch (monitoringType) {
            case 0:
                if (this.mGpsService != null) {
                    try {
                        result = this.mGpsService.resumeHardwareGeofence(geofenceId, monitorTransition);
                        break;
                    } catch (RemoteException e) {
                        Log.e(TAG, "ResumeGeofence: Remote Exception calling LocationManagerService");
                        result = false;
                        break;
                    }
                } else {
                    return false;
                }
            case 1:
                if (this.mFusedService == null) {
                    return false;
                }
                try {
                    this.mFusedService.resumeMonitoringGeofence(geofenceId, monitorTransition);
                    result = true;
                    break;
                } catch (RemoteException e2) {
                    Log.e(TAG, "ResumeGeofence: RemoteException calling LocationManagerService");
                    result = false;
                    break;
                }
            default:
                result = false;
                break;
        }
        if (DEBUG) {
            Log.d(TAG, "resumeGeofence: Result is: " + result);
        }
        return result;
    }

    public boolean registerForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) {
        Message reaperMessage = this.mReaperHandler.obtainMessage(2, callback);
        reaperMessage.arg1 = monitoringType;
        this.mReaperHandler.sendMessage(reaperMessage);
        Message m = this.mCallbacksHandler.obtainMessage(2, callback);
        m.arg1 = monitoringType;
        this.mCallbacksHandler.sendMessage(m);
        return true;
    }

    public boolean unregisterForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) {
        Message m = this.mCallbacksHandler.obtainMessage(3, callback);
        m.arg1 = monitoringType;
        this.mCallbacksHandler.sendMessage(m);
        return true;
    }

    public void reportGeofenceTransition(int geofenceId, Location location, int transition, long transitionTimestamp, int monitoringType, int sourcesUsed) {
        if (location == null) {
            Log.e(TAG, String.format("Invalid Geofence Transition: location=%p", location));
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "GeofenceTransition| " + location + ", transition:" + transition + ", transitionTimestamp:" + transitionTimestamp + ", monitoringType:" + monitoringType + ", sourcesUsed:" + sourcesUsed);
        }
        GeofenceTransition geofenceTransition = new GeofenceTransition(geofenceId, transition, transitionTimestamp, location, monitoringType, sourcesUsed);
        acquireWakeLock();
        Message message = this.mGeofenceHandler.obtainMessage(1, geofenceTransition);
        message.sendToTarget();
    }

    public void reportGeofenceMonitorStatus(int monitoringType, int monitoringStatus, Location location, int source) {
        setMonitorAvailability(monitoringType, monitoringStatus);
        acquireWakeLock();
        Message message = this.mCallbacksHandler.obtainMessage(1, location);
        message.arg1 = monitoringStatus;
        message.arg2 = monitoringType;
        message.sendToTarget();
    }

    private void reportGeofenceOperationStatus(int operation, int geofenceId, int operationStatus) {
        acquireWakeLock();
        Message message = this.mGeofenceHandler.obtainMessage(operation);
        message.arg1 = geofenceId;
        message.arg2 = operationStatus;
        message.sendToTarget();
    }

    public void reportGeofenceAddStatus(int geofenceId, int status) {
        if (DEBUG) {
            Log.d(TAG, "AddCallback| id:" + geofenceId + ", status:" + status);
        }
        reportGeofenceOperationStatus(2, geofenceId, status);
    }

    public void reportGeofenceRemoveStatus(int geofenceId, int status) {
        if (DEBUG) {
            Log.d(TAG, "RemoveCallback| id:" + geofenceId + ", status:" + status);
        }
        reportGeofenceOperationStatus(3, geofenceId, status);
    }

    public void reportGeofencePauseStatus(int geofenceId, int status) {
        if (DEBUG) {
            Log.d(TAG, "PauseCallbac| id:" + geofenceId + ", status" + status);
        }
        reportGeofenceOperationStatus(4, geofenceId, status);
    }

    public void reportGeofenceResumeStatus(int geofenceId, int status) {
        if (DEBUG) {
            Log.d(TAG, "ResumeCallback| id:" + geofenceId + ", status:" + status);
        }
        reportGeofenceOperationStatus(5, geofenceId, status);
    }

    /* loaded from: GeofenceHardwareImpl$GeofenceTransition.class */
    private class GeofenceTransition {
        private int mGeofenceId;
        private int mTransition;
        private long mTimestamp;
        private Location mLocation;
        private int mMonitoringType;
        private int mSourcesUsed;

        GeofenceTransition(int geofenceId, int transition, long timestamp, Location location, int monitoringType, int sourcesUsed) {
            this.mGeofenceId = geofenceId;
            this.mTransition = transition;
            this.mTimestamp = timestamp;
            this.mLocation = location;
            this.mMonitoringType = monitoringType;
            this.mSourcesUsed = sourcesUsed;
        }
    }

    private void setMonitorAvailability(int monitor, int val) {
        synchronized (this.mSupportedMonitorTypes) {
            this.mSupportedMonitorTypes[monitor] = val;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMonitoringResolutionLevel(int monitoringType) {
        switch (monitoringType) {
            case 0:
                return 3;
            case 1:
                return 3;
            default:
                return 1;
        }
    }

    /* loaded from: GeofenceHardwareImpl$Reaper.class */
    class Reaper implements IBinder.DeathRecipient {
        private IGeofenceHardwareMonitorCallback mMonitorCallback;
        private IGeofenceHardwareCallback mCallback;
        private int mMonitoringType;

        Reaper(IGeofenceHardwareCallback c, int monitoringType) {
            this.mCallback = c;
            this.mMonitoringType = monitoringType;
        }

        Reaper(IGeofenceHardwareMonitorCallback c, int monitoringType) {
            this.mMonitorCallback = c;
            this.mMonitoringType = monitoringType;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            if (this.mCallback != null) {
                Message m = GeofenceHardwareImpl.this.mGeofenceHandler.obtainMessage(6, this.mCallback);
                m.arg1 = this.mMonitoringType;
                GeofenceHardwareImpl.this.mGeofenceHandler.sendMessage(m);
            } else if (this.mMonitorCallback != null) {
                Message m2 = GeofenceHardwareImpl.this.mCallbacksHandler.obtainMessage(4, this.mMonitorCallback);
                m2.arg1 = this.mMonitoringType;
                GeofenceHardwareImpl.this.mCallbacksHandler.sendMessage(m2);
            }
            Message reaperMessage = GeofenceHardwareImpl.this.mReaperHandler.obtainMessage(3, this);
            GeofenceHardwareImpl.this.mReaperHandler.sendMessage(reaperMessage);
        }

        public int hashCode() {
            int result = (31 * 17) + (this.mCallback != null ? this.mCallback.hashCode() : 0);
            return (31 * ((31 * result) + (this.mMonitorCallback != null ? this.mMonitorCallback.hashCode() : 0))) + this.mMonitoringType;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            Reaper rhs = (Reaper) obj;
            return rhs.mCallback == this.mCallback && rhs.mMonitorCallback == this.mMonitorCallback && rhs.mMonitoringType == this.mMonitoringType;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAllowedResolutionLevel(int pid, int uid) {
        if (this.mContext.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, pid, uid) == 0) {
            return 3;
        }
        if (this.mContext.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, pid, uid) == 0) {
            return 2;
        }
        return 1;
    }
}