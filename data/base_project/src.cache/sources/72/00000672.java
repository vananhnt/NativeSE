package android.hardware.location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.location.IGeofenceHardware;
import android.location.IFusedGeofenceHardware;
import android.location.IGpsGeofenceHardware;
import android.os.Binder;
import android.os.IBinder;

/* loaded from: GeofenceHardwareService.class */
public class GeofenceHardwareService extends Service {
    private GeofenceHardwareImpl mGeofenceHardwareImpl;
    private Context mContext;
    private IBinder mBinder = new IGeofenceHardware.Stub() { // from class: android.hardware.location.GeofenceHardwareService.1
        @Override // android.hardware.location.IGeofenceHardware
        public void setGpsGeofenceHardware(IGpsGeofenceHardware service) {
            GeofenceHardwareService.this.mGeofenceHardwareImpl.setGpsHardwareGeofence(service);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public void setFusedGeofenceHardware(IFusedGeofenceHardware service) {
            GeofenceHardwareService.this.mGeofenceHardwareImpl.setFusedGeofenceHardware(service);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public int[] getMonitoringTypes() {
            GeofenceHardwareService.this.mContext.enforceCallingPermission(Manifest.permission.LOCATION_HARDWARE, "Location Hardware permission not granted to access hardware geofence");
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.getMonitoringTypes();
        }

        @Override // android.hardware.location.IGeofenceHardware
        public int getStatusOfMonitoringType(int monitoringType) {
            GeofenceHardwareService.this.mContext.enforceCallingPermission(Manifest.permission.LOCATION_HARDWARE, "Location Hardware permission not granted to access hardware geofence");
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.getStatusOfMonitoringType(monitoringType);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean addCircularFence(int id, int monitoringType, double lat, double longitude, double radius, int lastTransition, int monitorTransitions, int notificationResponsiveness, int unknownTimer, IGeofenceHardwareCallback callback) {
            GeofenceHardwareService.this.mContext.enforceCallingPermission(Manifest.permission.LOCATION_HARDWARE, "Location Hardware permission not granted to access hardware geofence");
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.addCircularFence(id, monitoringType, lat, longitude, radius, lastTransition, monitorTransitions, notificationResponsiveness, unknownTimer, callback);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean removeGeofence(int id, int monitoringType) {
            GeofenceHardwareService.this.mContext.enforceCallingPermission(Manifest.permission.LOCATION_HARDWARE, "Location Hardware permission not granted to access hardware geofence");
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.removeGeofence(id, monitoringType);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean pauseGeofence(int id, int monitoringType) {
            GeofenceHardwareService.this.mContext.enforceCallingPermission(Manifest.permission.LOCATION_HARDWARE, "Location Hardware permission not granted to access hardware geofence");
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.pauseGeofence(id, monitoringType);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean resumeGeofence(int id, int monitoringType, int monitorTransitions) {
            GeofenceHardwareService.this.mContext.enforceCallingPermission(Manifest.permission.LOCATION_HARDWARE, "Location Hardware permission not granted to access hardware geofence");
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.resumeGeofence(id, monitoringType, monitorTransitions);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean registerForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) {
            GeofenceHardwareService.this.mContext.enforceCallingPermission(Manifest.permission.LOCATION_HARDWARE, "Location Hardware permission not granted to access hardware geofence");
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.registerForMonitorStateChangeCallback(monitoringType, callback);
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean unregisterForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) {
            GeofenceHardwareService.this.mContext.enforceCallingPermission(Manifest.permission.LOCATION_HARDWARE, "Location Hardware permission not granted to access hardware geofence");
            GeofenceHardwareService.this.checkPermission(Binder.getCallingPid(), Binder.getCallingUid(), monitoringType);
            return GeofenceHardwareService.this.mGeofenceHardwareImpl.unregisterForMonitorStateChangeCallback(monitoringType, callback);
        }
    };

    @Override // android.app.Service
    public void onCreate() {
        this.mContext = this;
        this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mGeofenceHardwareImpl = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPermission(int pid, int uid, int monitoringType) {
        if (this.mGeofenceHardwareImpl.getAllowedResolutionLevel(pid, uid) < this.mGeofenceHardwareImpl.getMonitoringResolutionLevel(monitoringType)) {
            throw new SecurityException("Insufficient permissions to access hardware geofence for type: " + monitoringType);
        }
    }
}