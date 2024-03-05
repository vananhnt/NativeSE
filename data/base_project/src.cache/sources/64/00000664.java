package android.hardware.location;

import android.hardware.location.IGeofenceHardwareCallback;
import android.hardware.location.IGeofenceHardwareMonitorCallback;
import android.location.Location;
import android.os.RemoteException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/* loaded from: GeofenceHardware.class */
public final class GeofenceHardware {
    private IGeofenceHardware mService;
    static final int NUM_MONITORS = 2;
    public static final int MONITORING_TYPE_GPS_HARDWARE = 0;
    public static final int MONITORING_TYPE_FUSED_HARDWARE = 1;
    public static final int MONITOR_CURRENTLY_AVAILABLE = 0;
    public static final int MONITOR_CURRENTLY_UNAVAILABLE = 1;
    public static final int MONITOR_UNSUPPORTED = 2;
    public static final int GEOFENCE_ENTERED = 1;
    public static final int GEOFENCE_EXITED = 2;
    public static final int GEOFENCE_UNCERTAIN = 4;
    public static final int GEOFENCE_SUCCESS = 0;
    public static final int GEOFENCE_ERROR_TOO_MANY_GEOFENCES = 1;
    public static final int GEOFENCE_ERROR_ID_EXISTS = 2;
    public static final int GEOFENCE_ERROR_ID_UNKNOWN = 3;
    public static final int GEOFENCE_ERROR_INVALID_TRANSITION = 4;
    public static final int GEOFENCE_FAILURE = 5;
    public static final int GEOFENCE_ERROR_INSUFFICIENT_MEMORY = 6;
    private HashMap<GeofenceHardwareCallback, GeofenceHardwareCallbackWrapper> mCallbacks = new HashMap<>();
    private HashMap<GeofenceHardwareMonitorCallback, GeofenceHardwareMonitorCallbackWrapper> mMonitorCallbacks = new HashMap<>();

    public GeofenceHardware(IGeofenceHardware service) {
        this.mService = service;
    }

    public int[] getMonitoringTypes() {
        try {
            return this.mService.getMonitoringTypes();
        } catch (RemoteException e) {
            return new int[0];
        }
    }

    public int getStatusOfMonitoringType(int monitoringType) {
        try {
            return this.mService.getStatusOfMonitoringType(monitoringType);
        } catch (RemoteException e) {
            return 2;
        }
    }

    public boolean addGeofence(int geofenceId, int monitoringType, GeofenceHardwareRequest geofenceRequest, GeofenceHardwareCallback callback) {
        try {
            if (geofenceRequest.getType() == 0) {
                return this.mService.addCircularFence(geofenceId, monitoringType, geofenceRequest.getLatitude(), geofenceRequest.getLongitude(), geofenceRequest.getRadius(), geofenceRequest.getLastTransition(), geofenceRequest.getMonitorTransitions(), geofenceRequest.getNotificationResponsiveness(), geofenceRequest.getUnknownTimer(), getCallbackWrapper(callback));
            }
            throw new IllegalArgumentException("Geofence Request type not supported");
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean removeGeofence(int geofenceId, int monitoringType) {
        try {
            return this.mService.removeGeofence(geofenceId, monitoringType);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean pauseGeofence(int geofenceId, int monitoringType) {
        try {
            return this.mService.pauseGeofence(geofenceId, monitoringType);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean resumeGeofence(int geofenceId, int monitoringType, int monitorTransition) {
        try {
            return this.mService.resumeGeofence(geofenceId, monitoringType, monitorTransition);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean registerForMonitorStateChangeCallback(int monitoringType, GeofenceHardwareMonitorCallback callback) {
        try {
            return this.mService.registerForMonitorStateChangeCallback(monitoringType, getMonitorCallbackWrapper(callback));
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean unregisterForMonitorStateChangeCallback(int monitoringType, GeofenceHardwareMonitorCallback callback) {
        boolean result = false;
        try {
            result = this.mService.unregisterForMonitorStateChangeCallback(monitoringType, getMonitorCallbackWrapper(callback));
            if (result) {
                removeMonitorCallback(callback);
            }
        } catch (RemoteException e) {
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeCallback(GeofenceHardwareCallback callback) {
        synchronized (this.mCallbacks) {
            this.mCallbacks.remove(callback);
        }
    }

    private GeofenceHardwareCallbackWrapper getCallbackWrapper(GeofenceHardwareCallback callback) {
        GeofenceHardwareCallbackWrapper geofenceHardwareCallbackWrapper;
        synchronized (this.mCallbacks) {
            GeofenceHardwareCallbackWrapper wrapper = this.mCallbacks.get(callback);
            if (wrapper == null) {
                wrapper = new GeofenceHardwareCallbackWrapper(callback);
                this.mCallbacks.put(callback, wrapper);
            }
            geofenceHardwareCallbackWrapper = wrapper;
        }
        return geofenceHardwareCallbackWrapper;
    }

    private void removeMonitorCallback(GeofenceHardwareMonitorCallback callback) {
        synchronized (this.mMonitorCallbacks) {
            this.mMonitorCallbacks.remove(callback);
        }
    }

    private GeofenceHardwareMonitorCallbackWrapper getMonitorCallbackWrapper(GeofenceHardwareMonitorCallback callback) {
        GeofenceHardwareMonitorCallbackWrapper geofenceHardwareMonitorCallbackWrapper;
        synchronized (this.mMonitorCallbacks) {
            GeofenceHardwareMonitorCallbackWrapper wrapper = this.mMonitorCallbacks.get(callback);
            if (wrapper == null) {
                wrapper = new GeofenceHardwareMonitorCallbackWrapper(callback);
                this.mMonitorCallbacks.put(callback, wrapper);
            }
            geofenceHardwareMonitorCallbackWrapper = wrapper;
        }
        return geofenceHardwareMonitorCallbackWrapper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: GeofenceHardware$GeofenceHardwareMonitorCallbackWrapper.class */
    public class GeofenceHardwareMonitorCallbackWrapper extends IGeofenceHardwareMonitorCallback.Stub {
        private WeakReference<GeofenceHardwareMonitorCallback> mCallback;

        GeofenceHardwareMonitorCallbackWrapper(GeofenceHardwareMonitorCallback c) {
            this.mCallback = new WeakReference<>(c);
        }

        @Override // android.hardware.location.IGeofenceHardwareMonitorCallback
        public void onMonitoringSystemChange(int monitoringType, boolean available, Location location) {
            GeofenceHardwareMonitorCallback c = this.mCallback.get();
            if (c != null) {
                c.onMonitoringSystemChange(monitoringType, available, location);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: GeofenceHardware$GeofenceHardwareCallbackWrapper.class */
    public class GeofenceHardwareCallbackWrapper extends IGeofenceHardwareCallback.Stub {
        private WeakReference<GeofenceHardwareCallback> mCallback;

        GeofenceHardwareCallbackWrapper(GeofenceHardwareCallback c) {
            this.mCallback = new WeakReference<>(c);
        }

        @Override // android.hardware.location.IGeofenceHardwareCallback
        public void onGeofenceTransition(int geofenceId, int transition, Location location, long timestamp, int monitoringType) {
            GeofenceHardwareCallback c = this.mCallback.get();
            if (c != null) {
                c.onGeofenceTransition(geofenceId, transition, location, timestamp, monitoringType);
            }
        }

        @Override // android.hardware.location.IGeofenceHardwareCallback
        public void onGeofenceAdd(int geofenceId, int status) {
            GeofenceHardwareCallback c = this.mCallback.get();
            if (c != null) {
                c.onGeofenceAdd(geofenceId, status);
            }
        }

        @Override // android.hardware.location.IGeofenceHardwareCallback
        public void onGeofenceRemove(int geofenceId, int status) {
            GeofenceHardwareCallback c = this.mCallback.get();
            if (c != null) {
                c.onGeofenceRemove(geofenceId, status);
                GeofenceHardware.this.removeCallback(c);
            }
        }

        @Override // android.hardware.location.IGeofenceHardwareCallback
        public void onGeofencePause(int geofenceId, int status) {
            GeofenceHardwareCallback c = this.mCallback.get();
            if (c != null) {
                c.onGeofencePause(geofenceId, status);
            }
        }

        @Override // android.hardware.location.IGeofenceHardwareCallback
        public void onGeofenceResume(int geofenceId, int status) {
            GeofenceHardwareCallback c = this.mCallback.get();
            if (c != null) {
                c.onGeofenceResume(geofenceId, status);
            }
        }
    }
}