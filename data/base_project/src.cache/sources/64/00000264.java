package android.bluetooth;

import android.bluetooth.IBluetoothGattCallback;
import android.content.Context;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.R;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* loaded from: BluetoothGatt.class */
public final class BluetoothGatt implements BluetoothProfile {
    private static final String TAG = "BluetoothGatt";
    private static final boolean DBG = true;
    private static final boolean VDBG = true;
    private final Context mContext;
    private IBluetoothGatt mService;
    private BluetoothGattCallback mCallback;
    private int mClientIf;
    private BluetoothDevice mDevice;
    private boolean mAutoConnect;
    private static final int CONN_STATE_IDLE = 0;
    private static final int CONN_STATE_CONNECTING = 1;
    private static final int CONN_STATE_CONNECTED = 2;
    private static final int CONN_STATE_DISCONNECTING = 3;
    private static final int CONN_STATE_CLOSED = 4;
    public static final int GATT_SUCCESS = 0;
    public static final int GATT_READ_NOT_PERMITTED = 2;
    public static final int GATT_WRITE_NOT_PERMITTED = 3;
    public static final int GATT_INSUFFICIENT_AUTHENTICATION = 5;
    public static final int GATT_REQUEST_NOT_SUPPORTED = 6;
    public static final int GATT_INSUFFICIENT_ENCRYPTION = 15;
    public static final int GATT_INVALID_OFFSET = 7;
    public static final int GATT_INVALID_ATTRIBUTE_LENGTH = 13;
    public static final int GATT_FAILURE = 257;
    static final int AUTHENTICATION_NONE = 0;
    static final int AUTHENTICATION_NO_MITM = 1;
    static final int AUTHENTICATION_MITM = 2;
    private boolean mAuthRetry = false;
    private final Object mStateLock = new Object();
    private final IBluetoothGattCallback mBluetoothGattCallback = new IBluetoothGattCallback.Stub() { // from class: android.bluetooth.BluetoothGatt.1
        @Override // android.bluetooth.IBluetoothGattCallback
        public void onClientRegistered(int status, int clientIf) {
            Log.d(BluetoothGatt.TAG, "onClientRegistered() - status=" + status + " clientIf=" + clientIf);
            synchronized (BluetoothGatt.this.mStateLock) {
                if (BluetoothGatt.this.mConnState != 1) {
                    Log.e(BluetoothGatt.TAG, "Bad connection state: " + BluetoothGatt.this.mConnState);
                }
            }
            BluetoothGatt.this.mClientIf = clientIf;
            if (status != 0) {
                BluetoothGatt.this.mCallback.onConnectionStateChange(BluetoothGatt.this, 257, 0);
                synchronized (BluetoothGatt.this.mStateLock) {
                    BluetoothGatt.this.mConnState = 0;
                }
                return;
            }
            try {
                BluetoothGatt.this.mService.clientConnect(BluetoothGatt.this.mClientIf, BluetoothGatt.this.mDevice.getAddress(), !BluetoothGatt.this.mAutoConnect);
            } catch (RemoteException e) {
                Log.e(BluetoothGatt.TAG, "", e);
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onClientConnectionState(int status, int clientIf, boolean connected, String address) {
            Log.d(BluetoothGatt.TAG, "onClientConnectionState() - status=" + status + " clientIf=" + clientIf + " device=" + address);
            if (!address.equals(BluetoothGatt.this.mDevice.getAddress())) {
                return;
            }
            int profileState = connected ? 2 : 0;
            try {
                BluetoothGatt.this.mCallback.onConnectionStateChange(BluetoothGatt.this, status, profileState);
            } catch (Exception ex) {
                Log.w(BluetoothGatt.TAG, "Unhandled exception in callback", ex);
            }
            synchronized (BluetoothGatt.this.mStateLock) {
                if (connected) {
                    BluetoothGatt.this.mConnState = 2;
                } else {
                    BluetoothGatt.this.mConnState = 0;
                }
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onScanResult(String address, int rssi, byte[] advData) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onGetService(String address, int srvcType, int srvcInstId, ParcelUuid srvcUuid) {
            Log.d(BluetoothGatt.TAG, "onGetService() - Device=" + address + " UUID=" + srvcUuid);
            if (address.equals(BluetoothGatt.this.mDevice.getAddress())) {
                BluetoothGatt.this.mServices.add(new BluetoothGattService(BluetoothGatt.this.mDevice, srvcUuid.getUuid(), srvcInstId, srvcType));
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onGetIncludedService(String address, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int inclSrvcType, int inclSrvcInstId, ParcelUuid inclSrvcUuid) {
            Log.d(BluetoothGatt.TAG, "onGetIncludedService() - Device=" + address + " UUID=" + srvcUuid + " Included=" + inclSrvcUuid);
            if (address.equals(BluetoothGatt.this.mDevice.getAddress())) {
                BluetoothGattService service = BluetoothGatt.this.getService(BluetoothGatt.this.mDevice, srvcUuid.getUuid(), srvcInstId, srvcType);
                BluetoothGattService includedService = BluetoothGatt.this.getService(BluetoothGatt.this.mDevice, inclSrvcUuid.getUuid(), inclSrvcInstId, inclSrvcType);
                if (service != null && includedService != null) {
                    service.addIncludedService(includedService);
                }
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onGetCharacteristic(String address, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, int charProps) {
            BluetoothGattService service;
            Log.d(BluetoothGatt.TAG, "onGetCharacteristic() - Device=" + address + " UUID=" + charUuid);
            if (address.equals(BluetoothGatt.this.mDevice.getAddress()) && (service = BluetoothGatt.this.getService(BluetoothGatt.this.mDevice, srvcUuid.getUuid(), srvcInstId, srvcType)) != null) {
                service.addCharacteristic(new BluetoothGattCharacteristic(service, charUuid.getUuid(), charInstId, charProps, 0));
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onGetDescriptor(String address, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, int descrInstId, ParcelUuid descUuid) {
            BluetoothGattService service;
            BluetoothGattCharacteristic characteristic;
            Log.d(BluetoothGatt.TAG, "onGetDescriptor() - Device=" + address + " UUID=" + descUuid);
            if (!address.equals(BluetoothGatt.this.mDevice.getAddress()) || (service = BluetoothGatt.this.getService(BluetoothGatt.this.mDevice, srvcUuid.getUuid(), srvcInstId, srvcType)) == null || (characteristic = service.getCharacteristic(charUuid.getUuid())) == null) {
                return;
            }
            characteristic.addDescriptor(new BluetoothGattDescriptor(characteristic, descUuid.getUuid(), descrInstId, 0));
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onSearchComplete(String address, int status) {
            Log.d(BluetoothGatt.TAG, "onSearchComplete() = Device=" + address + " Status=" + status);
            if (address.equals(BluetoothGatt.this.mDevice.getAddress())) {
                try {
                    BluetoothGatt.this.mCallback.onServicesDiscovered(BluetoothGatt.this, status);
                } catch (Exception ex) {
                    Log.w(BluetoothGatt.TAG, "Unhandled exception in callback", ex);
                }
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onCharacteristicRead(String address, int status, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, byte[] value) {
            BluetoothGattCharacteristic characteristic;
            Log.d(BluetoothGatt.TAG, "onCharacteristicRead() - Device=" + address + " UUID=" + charUuid + " Status=" + status);
            if (!address.equals(BluetoothGatt.this.mDevice.getAddress())) {
                return;
            }
            if ((status == 5 || status == 15) && !BluetoothGatt.this.mAuthRetry) {
                try {
                    BluetoothGatt.this.mAuthRetry = true;
                    BluetoothGatt.this.mService.readCharacteristic(BluetoothGatt.this.mClientIf, address, srvcType, srvcInstId, srvcUuid, charInstId, charUuid, 2);
                    return;
                } catch (RemoteException e) {
                    Log.e(BluetoothGatt.TAG, "", e);
                }
            }
            BluetoothGatt.this.mAuthRetry = false;
            BluetoothGattService service = BluetoothGatt.this.getService(BluetoothGatt.this.mDevice, srvcUuid.getUuid(), srvcInstId, srvcType);
            if (service == null || (characteristic = service.getCharacteristic(charUuid.getUuid(), charInstId)) == null) {
                return;
            }
            if (status == 0) {
                characteristic.setValue(value);
            }
            try {
                BluetoothGatt.this.mCallback.onCharacteristicRead(BluetoothGatt.this, characteristic, status);
            } catch (Exception ex) {
                Log.w(BluetoothGatt.TAG, "Unhandled exception in callback", ex);
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onCharacteristicWrite(String address, int status, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid) {
            BluetoothGattService service;
            BluetoothGattCharacteristic characteristic;
            Log.d(BluetoothGatt.TAG, "onCharacteristicWrite() - Device=" + address + " UUID=" + charUuid + " Status=" + status);
            if (!address.equals(BluetoothGatt.this.mDevice.getAddress()) || (service = BluetoothGatt.this.getService(BluetoothGatt.this.mDevice, srvcUuid.getUuid(), srvcInstId, srvcType)) == null || (characteristic = service.getCharacteristic(charUuid.getUuid(), charInstId)) == null) {
                return;
            }
            if ((status == 5 || status == 15) && !BluetoothGatt.this.mAuthRetry) {
                try {
                    BluetoothGatt.this.mAuthRetry = true;
                    BluetoothGatt.this.mService.writeCharacteristic(BluetoothGatt.this.mClientIf, address, srvcType, srvcInstId, srvcUuid, charInstId, charUuid, characteristic.getWriteType(), 2, characteristic.getValue());
                    return;
                } catch (RemoteException e) {
                    Log.e(BluetoothGatt.TAG, "", e);
                }
            }
            BluetoothGatt.this.mAuthRetry = false;
            try {
                BluetoothGatt.this.mCallback.onCharacteristicWrite(BluetoothGatt.this, characteristic, status);
            } catch (Exception ex) {
                Log.w(BluetoothGatt.TAG, "Unhandled exception in callback", ex);
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onNotify(String address, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, byte[] value) {
            BluetoothGattService service;
            BluetoothGattCharacteristic characteristic;
            Log.d(BluetoothGatt.TAG, "onNotify() - Device=" + address + " UUID=" + charUuid);
            if (!address.equals(BluetoothGatt.this.mDevice.getAddress()) || (service = BluetoothGatt.this.getService(BluetoothGatt.this.mDevice, srvcUuid.getUuid(), srvcInstId, srvcType)) == null || (characteristic = service.getCharacteristic(charUuid.getUuid(), charInstId)) == null) {
                return;
            }
            characteristic.setValue(value);
            try {
                BluetoothGatt.this.mCallback.onCharacteristicChanged(BluetoothGatt.this, characteristic);
            } catch (Exception ex) {
                Log.w(BluetoothGatt.TAG, "Unhandled exception in callback", ex);
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onDescriptorRead(String address, int status, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, int descrInstId, ParcelUuid descrUuid, byte[] value) {
            BluetoothGattService service;
            BluetoothGattCharacteristic characteristic;
            BluetoothGattDescriptor descriptor;
            Log.d(BluetoothGatt.TAG, "onDescriptorRead() - Device=" + address + " UUID=" + charUuid);
            if (!address.equals(BluetoothGatt.this.mDevice.getAddress()) || (service = BluetoothGatt.this.getService(BluetoothGatt.this.mDevice, srvcUuid.getUuid(), srvcInstId, srvcType)) == null || (characteristic = service.getCharacteristic(charUuid.getUuid(), charInstId)) == null || (descriptor = characteristic.getDescriptor(descrUuid.getUuid(), descrInstId)) == null) {
                return;
            }
            if (status == 0) {
                descriptor.setValue(value);
            }
            if ((status == 5 || status == 15) && !BluetoothGatt.this.mAuthRetry) {
                try {
                    BluetoothGatt.this.mAuthRetry = true;
                    BluetoothGatt.this.mService.readDescriptor(BluetoothGatt.this.mClientIf, address, srvcType, srvcInstId, srvcUuid, charInstId, charUuid, descrInstId, descrUuid, 2);
                } catch (RemoteException e) {
                    Log.e(BluetoothGatt.TAG, "", e);
                }
            }
            BluetoothGatt.this.mAuthRetry = true;
            try {
                BluetoothGatt.this.mCallback.onDescriptorRead(BluetoothGatt.this, descriptor, status);
            } catch (Exception ex) {
                Log.w(BluetoothGatt.TAG, "Unhandled exception in callback", ex);
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onDescriptorWrite(String address, int status, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, int descrInstId, ParcelUuid descrUuid) {
            BluetoothGattService service;
            BluetoothGattCharacteristic characteristic;
            BluetoothGattDescriptor descriptor;
            Log.d(BluetoothGatt.TAG, "onDescriptorWrite() - Device=" + address + " UUID=" + charUuid);
            if (!address.equals(BluetoothGatt.this.mDevice.getAddress()) || (service = BluetoothGatt.this.getService(BluetoothGatt.this.mDevice, srvcUuid.getUuid(), srvcInstId, srvcType)) == null || (characteristic = service.getCharacteristic(charUuid.getUuid(), charInstId)) == null || (descriptor = characteristic.getDescriptor(descrUuid.getUuid(), descrInstId)) == null) {
                return;
            }
            if ((status == 5 || status == 15) && !BluetoothGatt.this.mAuthRetry) {
                try {
                    BluetoothGatt.this.mAuthRetry = true;
                    BluetoothGatt.this.mService.writeDescriptor(BluetoothGatt.this.mClientIf, address, srvcType, srvcInstId, srvcUuid, charInstId, charUuid, descrInstId, descrUuid, characteristic.getWriteType(), 2, descriptor.getValue());
                } catch (RemoteException e) {
                    Log.e(BluetoothGatt.TAG, "", e);
                }
            }
            BluetoothGatt.this.mAuthRetry = false;
            try {
                BluetoothGatt.this.mCallback.onDescriptorWrite(BluetoothGatt.this, descriptor, status);
            } catch (Exception ex) {
                Log.w(BluetoothGatt.TAG, "Unhandled exception in callback", ex);
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onExecuteWrite(String address, int status) {
            Log.d(BluetoothGatt.TAG, "onExecuteWrite() - Device=" + address + " status=" + status);
            if (address.equals(BluetoothGatt.this.mDevice.getAddress())) {
                try {
                    BluetoothGatt.this.mCallback.onReliableWriteCompleted(BluetoothGatt.this, status);
                } catch (Exception ex) {
                    Log.w(BluetoothGatt.TAG, "Unhandled exception in callback", ex);
                }
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onReadRemoteRssi(String address, int rssi, int status) {
            Log.d(BluetoothGatt.TAG, "onReadRemoteRssi() - Device=" + address + " rssi=" + rssi + " status=" + status);
            if (address.equals(BluetoothGatt.this.mDevice.getAddress())) {
                try {
                    BluetoothGatt.this.mCallback.onReadRemoteRssi(BluetoothGatt.this, rssi, status);
                } catch (Exception ex) {
                    Log.w(BluetoothGatt.TAG, "Unhandled exception in callback", ex);
                }
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onListen(int status) {
            Log.d(BluetoothGatt.TAG, "onListen() - status=" + status);
        }
    };
    private List<BluetoothGattService> mServices = new ArrayList();
    private int mConnState = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothGatt(Context context, IBluetoothGatt iGatt, BluetoothDevice device) {
        this.mContext = context;
        this.mService = iGatt;
        this.mDevice = device;
    }

    public void close() {
        Log.d(TAG, "close()");
        unregisterApp();
        this.mConnState = 4;
    }

    BluetoothGattService getService(BluetoothDevice device, UUID uuid, int instanceId, int type) {
        for (BluetoothGattService svc : this.mServices) {
            if (svc.getDevice().equals(device) && svc.getType() == type && svc.getInstanceId() == instanceId && svc.getUuid().equals(uuid)) {
                return svc;
            }
        }
        return null;
    }

    private boolean registerApp(BluetoothGattCallback callback) {
        Log.d(TAG, "registerApp()");
        if (this.mService == null) {
            return false;
        }
        this.mCallback = callback;
        UUID uuid = UUID.randomUUID();
        Log.d(TAG, "registerApp() - UUID=" + uuid);
        try {
            this.mService.registerClient(new ParcelUuid(uuid), this.mBluetoothGattCallback);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    private void unregisterApp() {
        Log.d(TAG, "unregisterApp() - mClientIf=" + this.mClientIf);
        if (this.mService == null || this.mClientIf == 0) {
            return;
        }
        try {
            this.mCallback = null;
            this.mService.unregisterClient(this.mClientIf);
            this.mClientIf = 0;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean connect(Boolean autoConnect, BluetoothGattCallback callback) {
        Log.d(TAG, "connect() - device: " + this.mDevice.getAddress() + ", auto: " + autoConnect);
        synchronized (this.mStateLock) {
            if (this.mConnState != 0) {
                throw new IllegalStateException("Not idle");
            }
            this.mConnState = 1;
        }
        if (!registerApp(callback)) {
            synchronized (this.mStateLock) {
                this.mConnState = 0;
            }
            Log.e(TAG, "Failed to register callback");
            return false;
        }
        this.mAutoConnect = autoConnect.booleanValue();
        return true;
    }

    void listen(boolean start) {
        if (this.mContext == null || !this.mContext.getResources().getBoolean(R.bool.config_bluetooth_le_peripheral_mode_supported)) {
            throw new UnsupportedOperationException("BluetoothGatt#listen is blocked");
        }
        Log.d(TAG, "listen() - start: " + start);
        if (this.mService == null || this.mClientIf == 0) {
            return;
        }
        try {
            this.mService.clientListen(this.mClientIf, start);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }

    void setAdvData(boolean advData, boolean includeName, boolean includeTxPower, Integer minInterval, Integer maxInterval, Integer appearance, Byte[] manufacturerData) {
        if (this.mContext == null || !this.mContext.getResources().getBoolean(R.bool.config_bluetooth_le_peripheral_mode_supported)) {
            throw new UnsupportedOperationException("BluetoothGatt#setAdvData is blocked");
        }
        Log.d(TAG, "setAdvData()");
        if (this.mService == null || this.mClientIf == 0) {
            return;
        }
        byte[] data = new byte[0];
        if (manufacturerData != null) {
            data = new byte[manufacturerData.length];
            for (int i = 0; i != manufacturerData.length; i++) {
                data[i] = manufacturerData[i].byteValue();
            }
        }
        try {
            this.mService.setAdvData(this.mClientIf, !advData, includeName, includeTxPower, minInterval != null ? minInterval.intValue() : 0, maxInterval != null ? maxInterval.intValue() : 0, appearance != null ? appearance.intValue() : 0, data);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }

    public void disconnect() {
        Log.d(TAG, "cancelOpen() - device: " + this.mDevice.getAddress());
        if (this.mService == null || this.mClientIf == 0) {
            return;
        }
        try {
            this.mService.clientDisconnect(this.mClientIf, this.mDevice.getAddress());
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }

    public boolean connect() {
        try {
            this.mService.clientConnect(this.mClientIf, this.mDevice.getAddress(), false);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public boolean discoverServices() {
        Log.d(TAG, "discoverServices() - device: " + this.mDevice.getAddress());
        if (this.mService == null || this.mClientIf == 0) {
            return false;
        }
        this.mServices.clear();
        try {
            this.mService.discoverServices(this.mClientIf, this.mDevice.getAddress());
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public List<BluetoothGattService> getServices() {
        List<BluetoothGattService> result = new ArrayList<>();
        for (BluetoothGattService service : this.mServices) {
            if (service.getDevice().equals(this.mDevice)) {
                result.add(service);
            }
        }
        return result;
    }

    public BluetoothGattService getService(UUID uuid) {
        for (BluetoothGattService service : this.mServices) {
            if (service.getDevice().equals(this.mDevice) && service.getUuid().equals(uuid)) {
                return service;
            }
        }
        return null;
    }

    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        BluetoothGattService service;
        BluetoothDevice device;
        if ((characteristic.getProperties() & 2) == 0) {
            return false;
        }
        Log.d(TAG, "readCharacteristic() - uuid: " + characteristic.getUuid());
        if (this.mService == null || this.mClientIf == 0 || (service = characteristic.getService()) == null || (device = service.getDevice()) == null) {
            return false;
        }
        try {
            this.mService.readCharacteristic(this.mClientIf, device.getAddress(), service.getType(), service.getInstanceId(), new ParcelUuid(service.getUuid()), characteristic.getInstanceId(), new ParcelUuid(characteristic.getUuid()), 0);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        BluetoothGattService service;
        BluetoothDevice device;
        if ((characteristic.getProperties() & 8) == 0 && (characteristic.getProperties() & 4) == 0) {
            return false;
        }
        Log.d(TAG, "writeCharacteristic() - uuid: " + characteristic.getUuid());
        if (this.mService == null || this.mClientIf == 0 || (service = characteristic.getService()) == null || (device = service.getDevice()) == null) {
            return false;
        }
        try {
            this.mService.writeCharacteristic(this.mClientIf, device.getAddress(), service.getType(), service.getInstanceId(), new ParcelUuid(service.getUuid()), characteristic.getInstanceId(), new ParcelUuid(characteristic.getUuid()), characteristic.getWriteType(), 0, characteristic.getValue());
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean readDescriptor(BluetoothGattDescriptor descriptor) {
        BluetoothGattCharacteristic characteristic;
        BluetoothGattService service;
        BluetoothDevice device;
        Log.d(TAG, "readDescriptor() - uuid: " + descriptor.getUuid());
        if (this.mService == null || this.mClientIf == 0 || (characteristic = descriptor.getCharacteristic()) == null || (service = characteristic.getService()) == null || (device = service.getDevice()) == null) {
            return false;
        }
        try {
            this.mService.readDescriptor(this.mClientIf, device.getAddress(), service.getType(), service.getInstanceId(), new ParcelUuid(service.getUuid()), characteristic.getInstanceId(), new ParcelUuid(characteristic.getUuid()), descriptor.getInstanceId(), new ParcelUuid(descriptor.getUuid()), 0);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        BluetoothGattCharacteristic characteristic;
        BluetoothGattService service;
        BluetoothDevice device;
        Log.d(TAG, "writeDescriptor() - uuid: " + descriptor.getUuid());
        if (this.mService == null || this.mClientIf == 0 || (characteristic = descriptor.getCharacteristic()) == null || (service = characteristic.getService()) == null || (device = service.getDevice()) == null) {
            return false;
        }
        try {
            this.mService.writeDescriptor(this.mClientIf, device.getAddress(), service.getType(), service.getInstanceId(), new ParcelUuid(service.getUuid()), characteristic.getInstanceId(), new ParcelUuid(characteristic.getUuid()), descriptor.getInstanceId(), new ParcelUuid(descriptor.getUuid()), characteristic.getWriteType(), 0, descriptor.getValue());
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean beginReliableWrite() {
        Log.d(TAG, "beginReliableWrite() - device: " + this.mDevice.getAddress());
        if (this.mService == null || this.mClientIf == 0) {
            return false;
        }
        try {
            this.mService.beginReliableWrite(this.mClientIf, this.mDevice.getAddress());
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean executeReliableWrite() {
        Log.d(TAG, "executeReliableWrite() - device: " + this.mDevice.getAddress());
        if (this.mService == null || this.mClientIf == 0) {
            return false;
        }
        try {
            this.mService.endReliableWrite(this.mClientIf, this.mDevice.getAddress(), true);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public void abortReliableWrite() {
        Log.d(TAG, "abortReliableWrite() - device: " + this.mDevice.getAddress());
        if (this.mService == null || this.mClientIf == 0) {
            return;
        }
        try {
            this.mService.endReliableWrite(this.mClientIf, this.mDevice.getAddress(), false);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }

    public void abortReliableWrite(BluetoothDevice mDevice) {
        abortReliableWrite();
    }

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        BluetoothGattService service;
        BluetoothDevice device;
        Log.d(TAG, "setCharacteristicNotification() - uuid: " + characteristic.getUuid() + " enable: " + enable);
        if (this.mService == null || this.mClientIf == 0 || (service = characteristic.getService()) == null || (device = service.getDevice()) == null) {
            return false;
        }
        try {
            this.mService.registerForNotification(this.mClientIf, device.getAddress(), service.getType(), service.getInstanceId(), new ParcelUuid(service.getUuid()), characteristic.getInstanceId(), new ParcelUuid(characteristic.getUuid()), enable);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean refresh() {
        Log.d(TAG, "refresh() - device: " + this.mDevice.getAddress());
        if (this.mService == null || this.mClientIf == 0) {
            return false;
        }
        try {
            this.mService.refreshDevice(this.mClientIf, this.mDevice.getAddress());
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean readRemoteRssi() {
        Log.d(TAG, "readRssi() - device: " + this.mDevice.getAddress());
        if (this.mService == null || this.mClientIf == 0) {
            return false;
        }
        try {
            this.mService.readRemoteRssi(this.mClientIf, this.mDevice.getAddress());
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice device) {
        throw new UnsupportedOperationException("Use BluetoothManager#getConnectionState instead.");
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        throw new UnsupportedOperationException("Use BluetoothManager#getConnectedDevices instead.");
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        throw new UnsupportedOperationException("Use BluetoothManager#getDevicesMatchingConnectionStates instead.");
    }
}