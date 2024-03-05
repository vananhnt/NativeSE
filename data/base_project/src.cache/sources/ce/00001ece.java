package com.android.server.location;

import android.content.Context;
import android.hardware.location.GeofenceHardwareImpl;
import android.hardware.location.GeofenceHardwareRequestParcelable;
import android.hardware.location.IFusedLocationHardware;
import android.hardware.location.IFusedLocationHardwareSink;
import android.location.FusedBatchOptions;
import android.location.IFusedGeofenceHardware;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

/* loaded from: FlpHardwareProvider.class */
public class FlpHardwareProvider {
    private static FlpHardwareProvider sSingletonInstance = null;
    private static final String TAG = "FlpHardwareProvider";
    private final Context mContext;
    private static final int FLP_RESULT_SUCCESS = 0;
    private static final int FLP_RESULT_ERROR = -1;
    private static final int FLP_RESULT_INSUFFICIENT_MEMORY = -2;
    private static final int FLP_RESULT_TOO_MANY_GEOFENCES = -3;
    private static final int FLP_RESULT_ID_EXISTS = -4;
    private static final int FLP_RESULT_ID_UNKNOWN = -5;
    private static final int FLP_RESULT_INVALID_GEOFENCE_TRANSITION = -6;
    public static final String LOCATION = "Location";
    public static final String GEOFENCING = "Geofencing";
    private GeofenceHardwareImpl mGeofenceHardwareSink = null;
    private IFusedLocationHardwareSink mLocationSink = null;
    private final Object mLocationSinkLock = new Object();
    private final IFusedLocationHardware mLocationHardware = new IFusedLocationHardware.Stub() { // from class: com.android.server.location.FlpHardwareProvider.1
        @Override // android.hardware.location.IFusedLocationHardware
        public void registerSink(IFusedLocationHardwareSink eventSink) {
            synchronized (FlpHardwareProvider.this.mLocationSinkLock) {
                if (FlpHardwareProvider.this.mLocationSink == null) {
                    FlpHardwareProvider.this.mLocationSink = eventSink;
                } else {
                    throw new RuntimeException("IFusedLocationHardware does not support multiple sinks");
                }
            }
        }

        @Override // android.hardware.location.IFusedLocationHardware
        public void unregisterSink(IFusedLocationHardwareSink eventSink) {
            synchronized (FlpHardwareProvider.this.mLocationSinkLock) {
                if (FlpHardwareProvider.this.mLocationSink == eventSink) {
                    FlpHardwareProvider.this.mLocationSink = null;
                }
            }
        }

        @Override // android.hardware.location.IFusedLocationHardware
        public int getSupportedBatchSize() {
            return FlpHardwareProvider.this.nativeGetBatchSize();
        }

        @Override // android.hardware.location.IFusedLocationHardware
        public void startBatching(int requestId, FusedBatchOptions options) {
            FlpHardwareProvider.this.nativeStartBatching(requestId, options);
        }

        @Override // android.hardware.location.IFusedLocationHardware
        public void stopBatching(int requestId) {
            FlpHardwareProvider.this.nativeStopBatching(requestId);
        }

        @Override // android.hardware.location.IFusedLocationHardware
        public void updateBatchingOptions(int requestId, FusedBatchOptions options) {
            FlpHardwareProvider.this.nativeUpdateBatchingOptions(requestId, options);
        }

        @Override // android.hardware.location.IFusedLocationHardware
        public void requestBatchOfLocations(int batchSizeRequested) {
            FlpHardwareProvider.this.nativeRequestBatchedLocation(batchSizeRequested);
        }

        @Override // android.hardware.location.IFusedLocationHardware
        public boolean supportsDiagnosticDataInjection() {
            return FlpHardwareProvider.this.nativeIsDiagnosticSupported();
        }

        @Override // android.hardware.location.IFusedLocationHardware
        public void injectDiagnosticData(String data) {
            FlpHardwareProvider.this.nativeInjectDiagnosticData(data);
        }

        @Override // android.hardware.location.IFusedLocationHardware
        public boolean supportsDeviceContextInjection() {
            return FlpHardwareProvider.this.nativeIsDeviceContextSupported();
        }

        @Override // android.hardware.location.IFusedLocationHardware
        public void injectDeviceContext(int deviceEnabledContext) {
            FlpHardwareProvider.this.nativeInjectDeviceContext(deviceEnabledContext);
        }
    };
    private final IFusedGeofenceHardware mGeofenceHardwareService = new IFusedGeofenceHardware.Stub() { // from class: com.android.server.location.FlpHardwareProvider.2
        @Override // android.location.IFusedGeofenceHardware
        public boolean isSupported() {
            return FlpHardwareProvider.this.nativeIsGeofencingSupported();
        }

        @Override // android.location.IFusedGeofenceHardware
        public void addGeofences(GeofenceHardwareRequestParcelable[] geofenceRequestsArray) {
            FlpHardwareProvider.this.nativeAddGeofences(geofenceRequestsArray);
        }

        @Override // android.location.IFusedGeofenceHardware
        public void removeGeofences(int[] geofenceIds) {
            FlpHardwareProvider.this.nativeRemoveGeofences(geofenceIds);
        }

        @Override // android.location.IFusedGeofenceHardware
        public void pauseMonitoringGeofence(int geofenceId) {
            FlpHardwareProvider.this.nativePauseGeofence(geofenceId);
        }

        @Override // android.location.IFusedGeofenceHardware
        public void resumeMonitoringGeofence(int geofenceId, int monitorTransitions) {
            FlpHardwareProvider.this.nativeResumeGeofence(geofenceId, monitorTransitions);
        }

        @Override // android.location.IFusedGeofenceHardware
        public void modifyGeofenceOptions(int geofenceId, int lastTransition, int monitorTransitions, int notificationResponsiveness, int unknownTimer, int sourcesToUse) {
            FlpHardwareProvider.this.nativeModifyGeofenceOption(geofenceId, lastTransition, monitorTransitions, notificationResponsiveness, unknownTimer, sourcesToUse);
        }
    };

    private static native void nativeClassInit();

    private static native boolean nativeIsSupported();

    private native void nativeInit();

    /* JADX INFO: Access modifiers changed from: private */
    public native int nativeGetBatchSize();

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeStartBatching(int i, FusedBatchOptions fusedBatchOptions);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeUpdateBatchingOptions(int i, FusedBatchOptions fusedBatchOptions);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeStopBatching(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeRequestBatchedLocation(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeInjectLocation(Location location);

    private native void nativeCleanup();

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean nativeIsDiagnosticSupported();

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeInjectDiagnosticData(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean nativeIsDeviceContextSupported();

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeInjectDeviceContext(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean nativeIsGeofencingSupported();

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeAddGeofences(GeofenceHardwareRequestParcelable[] geofenceHardwareRequestParcelableArr);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativePauseGeofence(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeResumeGeofence(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeModifyGeofenceOption(int i, int i2, int i3, int i4, int i5, int i6);

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeRemoveGeofences(int[] iArr);

    static {
        nativeClassInit();
    }

    public static FlpHardwareProvider getInstance(Context context) {
        if (sSingletonInstance == null) {
            sSingletonInstance = new FlpHardwareProvider(context);
        }
        return sSingletonInstance;
    }

    private FlpHardwareProvider(Context context) {
        this.mContext = context;
        LocationManager manager = (LocationManager) this.mContext.getSystemService("location");
        LocationRequest request = LocationRequest.createFromDeprecatedProvider(LocationManager.PASSIVE_PROVIDER, 0L, 0.0f, false);
        request.setHideFromAppOps(true);
        manager.requestLocationUpdates(request, new NetworkLocationListener(), Looper.myLooper());
    }

    public static boolean isSupported() {
        return nativeIsSupported();
    }

    private void onLocationReport(Location[] locations) {
        IFusedLocationHardwareSink sink;
        for (Location location : locations) {
            location.setProvider(LocationManager.FUSED_PROVIDER);
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        synchronized (this.mLocationSinkLock) {
            sink = this.mLocationSink;
        }
        if (sink != null) {
            try {
                sink.onLocationAvailable(locations);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException calling onLocationAvailable");
            }
        }
    }

    private void onDataReport(String data) {
        IFusedLocationHardwareSink sink;
        synchronized (this.mLocationSinkLock) {
            sink = this.mLocationSink;
        }
        try {
            if (this.mLocationSink != null) {
                sink.onDiagnosticDataAvailable(data);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException calling onDiagnosticDataAvailable");
        }
    }

    private void onGeofenceTransition(int geofenceId, Location location, int transition, long timestamp, int sourcesUsed) {
        getGeofenceHardwareSink().reportGeofenceTransition(geofenceId, updateLocationInformation(location), transition, timestamp, 1, sourcesUsed);
    }

    private void onGeofenceMonitorStatus(int status, int source, Location location) {
        Location updatedLocation = null;
        if (location != null) {
            updatedLocation = updateLocationInformation(location);
        }
        getGeofenceHardwareSink().reportGeofenceMonitorStatus(1, status, updatedLocation, source);
    }

    private void onGeofenceAdd(int geofenceId, int result) {
        getGeofenceHardwareSink().reportGeofenceAddStatus(geofenceId, translateToGeofenceHardwareStatus(result));
    }

    private void onGeofenceRemove(int geofenceId, int result) {
        getGeofenceHardwareSink().reportGeofenceRemoveStatus(geofenceId, translateToGeofenceHardwareStatus(result));
    }

    private void onGeofencePause(int geofenceId, int result) {
        getGeofenceHardwareSink().reportGeofencePauseStatus(geofenceId, translateToGeofenceHardwareStatus(result));
    }

    private void onGeofenceResume(int geofenceId, int result) {
        getGeofenceHardwareSink().reportGeofenceResumeStatus(geofenceId, translateToGeofenceHardwareStatus(result));
    }

    public IFusedLocationHardware getLocationHardware() {
        nativeInit();
        return this.mLocationHardware;
    }

    public IFusedGeofenceHardware getGeofenceHardware() {
        nativeInit();
        return this.mGeofenceHardwareService;
    }

    /* loaded from: FlpHardwareProvider$NetworkLocationListener.class */
    private final class NetworkLocationListener implements LocationListener {
        private NetworkLocationListener() {
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider()) && location.hasAccuracy()) {
                FlpHardwareProvider.this.nativeInjectLocation(location);
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

    private GeofenceHardwareImpl getGeofenceHardwareSink() {
        if (this.mGeofenceHardwareSink == null) {
            this.mGeofenceHardwareSink = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        return this.mGeofenceHardwareSink;
    }

    private static int translateToGeofenceHardwareStatus(int flpHalResult) {
        switch (flpHalResult) {
            case -6:
                return 4;
            case -5:
                return 3;
            case -4:
                return 2;
            case -3:
                return 1;
            case -2:
            default:
                Log.e(TAG, String.format("Invalid FlpHal result code: %d", Integer.valueOf(flpHalResult)));
                return 5;
            case -1:
                return 5;
            case 0:
                return 0;
        }
    }

    private Location updateLocationInformation(Location location) {
        location.setProvider(LocationManager.FUSED_PROVIDER);
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        return location;
    }
}