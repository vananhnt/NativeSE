package android.bluetooth;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.List;

/* loaded from: BluetoothManager.class */
public final class BluetoothManager {
    private static final String TAG = "BluetoothManager";
    private static final boolean DBG = true;
    private static final boolean VDBG = true;
    private final BluetoothAdapter mAdapter;

    public BluetoothManager(Context context) {
        if (context.getApplicationContext() == null) {
            throw new IllegalArgumentException("context not associated with any application (using a mock context?)");
        }
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getAdapter() {
        return this.mAdapter;
    }

    public int getConnectionState(BluetoothDevice device, int profile) {
        Log.d(TAG, "getConnectionState()");
        List<BluetoothDevice> connectedDevices = getConnectedDevices(profile);
        for (BluetoothDevice connectedDevice : connectedDevices) {
            if (device.equals(connectedDevice)) {
                return 2;
            }
        }
        return 0;
    }

    public List<BluetoothDevice> getConnectedDevices(int profile) {
        IBluetoothGatt iGatt;
        Log.d(TAG, "getConnectedDevices");
        if (profile != 7 && profile != 8) {
            throw new IllegalArgumentException("Profile not supported: " + profile);
        }
        List<BluetoothDevice> connectedDevices = new ArrayList<>();
        try {
            IBluetoothManager managerService = this.mAdapter.getBluetoothManager();
            iGatt = managerService.getBluetoothGatt();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        if (iGatt == null) {
            return connectedDevices;
        }
        connectedDevices = iGatt.getDevicesMatchingConnectionStates(new int[]{2});
        return connectedDevices;
    }

    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int profile, int[] states) {
        IBluetoothGatt iGatt;
        Log.d(TAG, "getDevicesMatchingConnectionStates");
        if (profile != 7 && profile != 8) {
            throw new IllegalArgumentException("Profile not supported: " + profile);
        }
        List<BluetoothDevice> devices = new ArrayList<>();
        try {
            IBluetoothManager managerService = this.mAdapter.getBluetoothManager();
            iGatt = managerService.getBluetoothGatt();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        if (iGatt == null) {
            return devices;
        }
        devices = iGatt.getDevicesMatchingConnectionStates(states);
        return devices;
    }

    public BluetoothGattServer openGattServer(Context context, BluetoothGattServerCallback callback) {
        if (context == null || callback == null) {
            throw new IllegalArgumentException("null parameter: " + context + Separators.SP + callback);
        }
        try {
            IBluetoothManager managerService = this.mAdapter.getBluetoothManager();
            IBluetoothGatt iGatt = managerService.getBluetoothGatt();
            if (iGatt == null) {
                Log.e(TAG, "Fail to get GATT Server connection");
                return null;
            }
            BluetoothGattServer mGattServer = new BluetoothGattServer(context, iGatt);
            Boolean regStatus = Boolean.valueOf(mGattServer.registerCallback(callback));
            if (regStatus.booleanValue()) {
                return mGattServer;
            }
            return null;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }
}