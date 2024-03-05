package com.android.server.location;

import android.content.Context;
import android.hardware.location.IFusedLocationHardware;
import android.hardware.location.IFusedLocationHardwareSink;
import android.location.FusedBatchOptions;
import android.os.RemoteException;

/* loaded from: FusedLocationHardwareSecure.class */
public class FusedLocationHardwareSecure extends IFusedLocationHardware.Stub {
    private final IFusedLocationHardware mLocationHardware;
    private final Context mContext;
    private final String mPermissionId;

    public FusedLocationHardwareSecure(IFusedLocationHardware locationHardware, Context context, String permissionId) {
        this.mLocationHardware = locationHardware;
        this.mContext = context;
        this.mPermissionId = permissionId;
    }

    private void checkPermissions() {
        this.mContext.enforceCallingPermission(this.mPermissionId, String.format("Permission '%s' not granted to access FusedLocationHardware", this.mPermissionId));
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public void registerSink(IFusedLocationHardwareSink eventSink) throws RemoteException {
        checkPermissions();
        this.mLocationHardware.registerSink(eventSink);
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public void unregisterSink(IFusedLocationHardwareSink eventSink) throws RemoteException {
        checkPermissions();
        this.mLocationHardware.unregisterSink(eventSink);
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public int getSupportedBatchSize() throws RemoteException {
        checkPermissions();
        return this.mLocationHardware.getSupportedBatchSize();
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public void startBatching(int id, FusedBatchOptions batchOptions) throws RemoteException {
        checkPermissions();
        this.mLocationHardware.startBatching(id, batchOptions);
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public void stopBatching(int id) throws RemoteException {
        checkPermissions();
        this.mLocationHardware.stopBatching(id);
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public void updateBatchingOptions(int id, FusedBatchOptions batchoOptions) throws RemoteException {
        checkPermissions();
        this.mLocationHardware.updateBatchingOptions(id, batchoOptions);
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public void requestBatchOfLocations(int batchSizeRequested) throws RemoteException {
        checkPermissions();
        this.mLocationHardware.requestBatchOfLocations(batchSizeRequested);
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public boolean supportsDiagnosticDataInjection() throws RemoteException {
        checkPermissions();
        return this.mLocationHardware.supportsDiagnosticDataInjection();
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public void injectDiagnosticData(String data) throws RemoteException {
        checkPermissions();
        this.mLocationHardware.injectDiagnosticData(data);
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public boolean supportsDeviceContextInjection() throws RemoteException {
        checkPermissions();
        return this.mLocationHardware.supportsDeviceContextInjection();
    }

    @Override // android.hardware.location.IFusedLocationHardware
    public void injectDeviceContext(int deviceEnabledContext) throws RemoteException {
        checkPermissions();
        this.mLocationHardware.injectDeviceContext(deviceEnabledContext);
    }
}