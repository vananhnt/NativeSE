package com.android.server.location;

import android.Manifest;
import android.content.Context;
import android.hardware.location.IFusedLocationHardware;
import android.location.IFusedProvider;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.ServiceWatcher;

/* loaded from: FusedProxy.class */
public final class FusedProxy {
    private final String TAG = "FusedProxy";
    private final ServiceWatcher mServiceWatcher;
    private final FusedLocationHardwareSecure mLocationHardware;

    private FusedProxy(Context context, Handler handler, IFusedLocationHardware locationHardware, int overlaySwitchResId, int defaultServicePackageNameResId, int initialPackageNameResId) {
        this.mLocationHardware = new FusedLocationHardwareSecure(locationHardware, context, Manifest.permission.LOCATION_HARDWARE);
        Runnable newServiceWork = new Runnable() { // from class: com.android.server.location.FusedProxy.1
            @Override // java.lang.Runnable
            public void run() {
                FusedProxy.this.bindProvider(FusedProxy.this.mLocationHardware);
            }
        };
        this.mServiceWatcher = new ServiceWatcher(context, "FusedProxy", "com.android.location.service.FusedProvider", overlaySwitchResId, defaultServicePackageNameResId, initialPackageNameResId, newServiceWork, handler);
    }

    public static FusedProxy createAndBind(Context context, Handler handler, IFusedLocationHardware locationHardware, int overlaySwitchResId, int defaultServicePackageNameResId, int initialPackageNameResId) {
        FusedProxy fusedProxy = new FusedProxy(context, handler, locationHardware, overlaySwitchResId, defaultServicePackageNameResId, initialPackageNameResId);
        if (!fusedProxy.mServiceWatcher.start()) {
            return null;
        }
        return fusedProxy;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindProvider(IFusedLocationHardware locationHardware) {
        IFusedProvider provider = IFusedProvider.Stub.asInterface(this.mServiceWatcher.getBinder());
        if (provider == null) {
            Log.e("FusedProxy", "No instance of FusedProvider found on FusedLocationHardware connected.");
            return;
        }
        try {
            provider.onFusedLocationHardwareChange(locationHardware);
        } catch (RemoteException e) {
            Log.e("FusedProxy", e.toString());
        }
    }
}