package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothGattCallback;
import android.bluetooth.IBluetoothManager;
import android.bluetooth.IBluetoothManagerCallback;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.Context;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Pair;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/* loaded from: BluetoothAdapter.class */
public final class BluetoothAdapter {
    private static final String TAG = "BluetoothAdapter";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    public static final int ERROR = Integer.MIN_VALUE;
    public static final String ACTION_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";
    public static final String EXTRA_STATE = "android.bluetooth.adapter.extra.STATE";
    public static final String EXTRA_PREVIOUS_STATE = "android.bluetooth.adapter.extra.PREVIOUS_STATE";
    public static final int STATE_OFF = 10;
    public static final int STATE_TURNING_ON = 11;
    public static final int STATE_ON = 12;
    public static final int STATE_TURNING_OFF = 13;
    public static final String ACTION_REQUEST_DISCOVERABLE = "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE";
    public static final String EXTRA_DISCOVERABLE_DURATION = "android.bluetooth.adapter.extra.DISCOVERABLE_DURATION";
    public static final String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE";
    public static final String ACTION_SCAN_MODE_CHANGED = "android.bluetooth.adapter.action.SCAN_MODE_CHANGED";
    public static final String EXTRA_SCAN_MODE = "android.bluetooth.adapter.extra.SCAN_MODE";
    public static final String EXTRA_PREVIOUS_SCAN_MODE = "android.bluetooth.adapter.extra.PREVIOUS_SCAN_MODE";
    public static final int SCAN_MODE_NONE = 20;
    public static final int SCAN_MODE_CONNECTABLE = 21;
    public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 23;
    public static final String ACTION_DISCOVERY_STARTED = "android.bluetooth.adapter.action.DISCOVERY_STARTED";
    public static final String ACTION_DISCOVERY_FINISHED = "android.bluetooth.adapter.action.DISCOVERY_FINISHED";
    public static final String ACTION_LOCAL_NAME_CHANGED = "android.bluetooth.adapter.action.LOCAL_NAME_CHANGED";
    public static final String EXTRA_LOCAL_NAME = "android.bluetooth.adapter.extra.LOCAL_NAME";
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED";
    public static final String EXTRA_CONNECTION_STATE = "android.bluetooth.adapter.extra.CONNECTION_STATE";
    public static final String EXTRA_PREVIOUS_CONNECTION_STATE = "android.bluetooth.adapter.extra.PREVIOUS_CONNECTION_STATE";
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_DISCONNECTING = 3;
    public static final String BLUETOOTH_MANAGER_SERVICE = "bluetooth_manager";
    private static final int ADDRESS_LENGTH = 17;
    private static BluetoothAdapter sAdapter;
    private final IBluetoothManager mManagerService;
    private IBluetooth mService;
    private final Map<LeScanCallback, GattCallbackWrapper> mLeScanClients;
    private final IBluetoothManagerCallback mManagerCallback = new IBluetoothManagerCallback.Stub() { // from class: android.bluetooth.BluetoothAdapter.1
        @Override // android.bluetooth.IBluetoothManagerCallback
        public void onBluetoothServiceUp(IBluetooth bluetoothService) {
            synchronized (BluetoothAdapter.this.mManagerCallback) {
                BluetoothAdapter.this.mService = bluetoothService;
                Iterator i$ = BluetoothAdapter.this.mProxyServiceStateCallbacks.iterator();
                while (i$.hasNext()) {
                    IBluetoothManagerCallback cb = (IBluetoothManagerCallback) i$.next();
                    if (cb != null) {
                        try {
                            cb.onBluetoothServiceUp(bluetoothService);
                        } catch (Exception e) {
                            Log.e(BluetoothAdapter.TAG, "", e);
                        }
                    } else {
                        Log.d(BluetoothAdapter.TAG, "onBluetoothServiceUp: cb is null!!!");
                    }
                }
            }
        }

        @Override // android.bluetooth.IBluetoothManagerCallback
        public void onBluetoothServiceDown() {
            synchronized (BluetoothAdapter.this.mManagerCallback) {
                BluetoothAdapter.this.mService = null;
                Iterator i$ = BluetoothAdapter.this.mProxyServiceStateCallbacks.iterator();
                while (i$.hasNext()) {
                    IBluetoothManagerCallback cb = (IBluetoothManagerCallback) i$.next();
                    if (cb != null) {
                        try {
                            cb.onBluetoothServiceDown();
                        } catch (Exception e) {
                            Log.e(BluetoothAdapter.TAG, "", e);
                        }
                    } else {
                        Log.d(BluetoothAdapter.TAG, "onBluetoothServiceDown: cb is null!!!");
                    }
                }
            }
        }
    };
    private ArrayList<IBluetoothManagerCallback> mProxyServiceStateCallbacks = new ArrayList<>();

    /* loaded from: BluetoothAdapter$BluetoothStateChangeCallback.class */
    public interface BluetoothStateChangeCallback {
        void onBluetoothStateChange(boolean z);
    }

    /* loaded from: BluetoothAdapter$LeScanCallback.class */
    public interface LeScanCallback {
        void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr);
    }

    public static synchronized BluetoothAdapter getDefaultAdapter() {
        if (sAdapter == null) {
            IBinder b = ServiceManager.getService(BLUETOOTH_MANAGER_SERVICE);
            if (b != null) {
                IBluetoothManager managerService = IBluetoothManager.Stub.asInterface(b);
                sAdapter = new BluetoothAdapter(managerService);
            } else {
                Log.e(TAG, "Bluetooth binder is null");
            }
        }
        return sAdapter;
    }

    BluetoothAdapter(IBluetoothManager managerService) {
        if (managerService == null) {
            throw new IllegalArgumentException("bluetooth manager service is null");
        }
        try {
            this.mService = managerService.registerAdapter(this.mManagerCallback);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        this.mManagerService = managerService;
        this.mLeScanClients = new HashMap();
    }

    public BluetoothDevice getRemoteDevice(String address) {
        return new BluetoothDevice(address);
    }

    public BluetoothDevice getRemoteDevice(byte[] address) {
        if (address == null || address.length != 6) {
            throw new IllegalArgumentException("Bluetooth address must have 6 bytes");
        }
        return new BluetoothDevice(String.format(Locale.US, "%02X:%02X:%02X:%02X:%02X:%02X", Byte.valueOf(address[0]), Byte.valueOf(address[1]), Byte.valueOf(address[2]), Byte.valueOf(address[3]), Byte.valueOf(address[4]), Byte.valueOf(address[5])));
    }

    public boolean isEnabled() {
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.isEnabled();
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public int getState() {
        try {
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        synchronized (this.mManagerCallback) {
            if (this.mService != null) {
                int state = this.mService.getState();
                return state;
            }
            Log.d(TAG, "" + hashCode() + ": getState() :  mService = null. Returning STATE_OFF");
            return 10;
        }
    }

    public boolean enable() {
        if (isEnabled()) {
            Log.d(TAG, "enable(): BT is already enabled..!");
            return true;
        }
        try {
            return this.mManagerService.enable();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean disable() {
        try {
            return this.mManagerService.disable(true);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean disable(boolean persist) {
        try {
            return this.mManagerService.disable(persist);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public String getAddress() {
        try {
            return this.mManagerService.getAddress();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public String getName() {
        try {
            return this.mManagerService.getName();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public boolean configHciSnoopLog(boolean enable) {
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.configHciSnoopLog(enable);
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public ParcelUuid[] getUuids() {
        if (getState() != 12) {
            return null;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.getUuids();
                }
                return null;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public boolean setName(String name) {
        if (getState() != 12) {
            return false;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.setName(name);
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public int getScanMode() {
        if (getState() != 12) {
            return 20;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.getScanMode();
                }
                return 20;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return 20;
        }
    }

    public boolean setScanMode(int mode, int duration) {
        if (getState() != 12) {
            return false;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.setScanMode(mode, duration);
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean setScanMode(int mode) {
        if (getState() != 12) {
            return false;
        }
        return setScanMode(mode, getDiscoverableTimeout());
    }

    public int getDiscoverableTimeout() {
        if (getState() != 12) {
            return -1;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.getDiscoverableTimeout();
                }
                return -1;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return -1;
        }
    }

    public void setDiscoverableTimeout(int timeout) {
        if (getState() != 12) {
            return;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    this.mService.setDiscoverableTimeout(timeout);
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }

    public boolean startDiscovery() {
        if (getState() != 12) {
            return false;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.startDiscovery();
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean cancelDiscovery() {
        if (getState() != 12) {
            return false;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.cancelDiscovery();
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean isDiscovering() {
        if (getState() != 12) {
            return false;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.isDiscovering();
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public Set<BluetoothDevice> getBondedDevices() {
        if (getState() != 12) {
            return toDeviceSet(new BluetoothDevice[0]);
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return toDeviceSet(this.mService.getBondedDevices());
                }
                return toDeviceSet(new BluetoothDevice[0]);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public int getConnectionState() {
        if (getState() != 12) {
            return 0;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.getAdapterConnectionState();
                }
                return 0;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getConnectionState:", e);
            return 0;
        }
    }

    public int getProfileConnectionState(int profile) {
        if (getState() != 12) {
            return 0;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    return this.mService.getProfileConnectionState(profile);
                }
                return 0;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getProfileConnectionState:", e);
            return 0;
        }
    }

    public BluetoothServerSocket listenUsingRfcommOn(int channel) throws IOException {
        BluetoothServerSocket socket = new BluetoothServerSocket(1, true, true, channel);
        int errno = socket.mSocket.bindListen();
        if (errno != 0) {
            throw new IOException("Error: " + errno);
        }
        return socket;
    }

    public BluetoothServerSocket listenUsingRfcommWithServiceRecord(String name, UUID uuid) throws IOException {
        return createNewRfcommSocketAndRecord(name, uuid, true, true);
    }

    public BluetoothServerSocket listenUsingInsecureRfcommWithServiceRecord(String name, UUID uuid) throws IOException {
        return createNewRfcommSocketAndRecord(name, uuid, false, false);
    }

    public BluetoothServerSocket listenUsingEncryptedRfcommWithServiceRecord(String name, UUID uuid) throws IOException {
        return createNewRfcommSocketAndRecord(name, uuid, false, true);
    }

    private BluetoothServerSocket createNewRfcommSocketAndRecord(String name, UUID uuid, boolean auth, boolean encrypt) throws IOException {
        BluetoothServerSocket socket = new BluetoothServerSocket(1, auth, encrypt, new ParcelUuid(uuid));
        socket.setServiceName(name);
        int errno = socket.mSocket.bindListen();
        if (errno != 0) {
            throw new IOException("Error: " + errno);
        }
        return socket;
    }

    public BluetoothServerSocket listenUsingInsecureRfcommOn(int port) throws IOException {
        BluetoothServerSocket socket = new BluetoothServerSocket(1, false, false, port);
        int errno = socket.mSocket.bindListen();
        if (errno != 0) {
            throw new IOException("Error: " + errno);
        }
        return socket;
    }

    public BluetoothServerSocket listenUsingEncryptedRfcommOn(int port) throws IOException {
        BluetoothServerSocket socket = new BluetoothServerSocket(1, false, true, port);
        int errno = socket.mSocket.bindListen();
        if (errno < 0) {
            throw new IOException("Error: " + errno);
        }
        return socket;
    }

    public static BluetoothServerSocket listenUsingScoOn() throws IOException {
        BluetoothServerSocket socket = new BluetoothServerSocket(2, false, false, -1);
        int errno = socket.mSocket.bindListen();
        if (errno < 0) {
        }
        return socket;
    }

    public Pair<byte[], byte[]> readOutOfBandData() {
        return getState() != 12 ? null : null;
    }

    public boolean getProfileProxy(Context context, BluetoothProfile.ServiceListener listener, int profile) {
        if (context == null || listener == null) {
            return false;
        }
        if (profile == 1) {
            new BluetoothHeadset(context, listener);
            return true;
        } else if (profile == 2) {
            new BluetoothA2dp(context, listener);
            return true;
        } else if (profile == 4) {
            new BluetoothInputDevice(context, listener);
            return true;
        } else if (profile == 5) {
            new BluetoothPan(context, listener);
            return true;
        } else if (profile == 3) {
            new BluetoothHealth(context, listener);
            return true;
        } else if (profile == 9) {
            new BluetoothMap(context, listener);
            return true;
        } else {
            return false;
        }
    }

    public void closeProfileProxy(int profile, BluetoothProfile proxy) {
        if (proxy == null) {
            return;
        }
        switch (profile) {
            case 1:
                BluetoothHeadset headset = (BluetoothHeadset) proxy;
                headset.close();
                return;
            case 2:
                BluetoothA2dp a2dp = (BluetoothA2dp) proxy;
                a2dp.close();
                return;
            case 3:
                BluetoothHealth health = (BluetoothHealth) proxy;
                health.close();
                return;
            case 4:
                BluetoothInputDevice iDev = (BluetoothInputDevice) proxy;
                iDev.close();
                return;
            case 5:
                BluetoothPan pan = (BluetoothPan) proxy;
                pan.close();
                return;
            case 6:
            default:
                return;
            case 7:
                BluetoothGatt gatt = (BluetoothGatt) proxy;
                gatt.close();
                return;
            case 8:
                BluetoothGattServer gattServer = (BluetoothGattServer) proxy;
                gattServer.close();
                return;
            case 9:
                BluetoothMap map = (BluetoothMap) proxy;
                map.close();
                return;
        }
    }

    public boolean enableNoAutoConnect() {
        if (isEnabled()) {
            Log.d(TAG, "enableNoAutoConnect(): BT is already enabled..!");
            return true;
        }
        try {
            return this.mManagerService.enableNoAutoConnect();
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean changeApplicationBluetoothState(boolean on, BluetoothStateChangeCallback callback) {
        return callback == null ? false : false;
    }

    /* loaded from: BluetoothAdapter$StateChangeCallbackWrapper.class */
    public class StateChangeCallbackWrapper extends IBluetoothStateChangeCallback.Stub {
        private BluetoothStateChangeCallback mCallback;

        StateChangeCallbackWrapper(BluetoothStateChangeCallback callback) {
            this.mCallback = callback;
        }

        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean on) {
            this.mCallback.onBluetoothStateChange(on);
        }
    }

    private Set<BluetoothDevice> toDeviceSet(BluetoothDevice[] devices) {
        Set<BluetoothDevice> deviceSet = new HashSet<>(Arrays.asList(devices));
        return Collections.unmodifiableSet(deviceSet);
    }

    protected void finalize() throws Throwable {
        try {
            try {
                this.mManagerService.unregisterAdapter(this.mManagerCallback);
                super.finalize();
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
                super.finalize();
            }
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public static boolean checkBluetoothAddress(String address) {
        if (address == null || address.length() != 17) {
            return false;
        }
        for (int i = 0; i < 17; i++) {
            char c = address.charAt(i);
            switch (i % 3) {
                case 0:
                case 1:
                    if ((c < '0' || c > '9') && (c < 'A' || c > 'F')) {
                        return false;
                    }
                    break;
                case 2:
                    if (c == ':') {
                        break;
                    } else {
                        return false;
                    }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IBluetoothManager getBluetoothManager() {
        return this.mManagerService;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IBluetooth getBluetoothService(IBluetoothManagerCallback cb) {
        synchronized (this.mManagerCallback) {
            if (cb == null) {
                Log.w(TAG, "getBluetoothService() called with no BluetoothManagerCallback");
            } else if (!this.mProxyServiceStateCallbacks.contains(cb)) {
                this.mProxyServiceStateCallbacks.add(cb);
            }
        }
        return this.mService;
    }

    void removeServiceStateCallback(IBluetoothManagerCallback cb) {
        synchronized (this.mManagerCallback) {
            this.mProxyServiceStateCallbacks.remove(cb);
        }
    }

    public boolean startLeScan(LeScanCallback callback) {
        return startLeScan(null, callback);
    }

    public boolean startLeScan(UUID[] serviceUuids, LeScanCallback callback) {
        IBluetoothGatt iGatt;
        Log.d(TAG, "startLeScan(): " + serviceUuids);
        if (callback == null) {
            Log.e(TAG, "startLeScan: null callback");
            return false;
        }
        synchronized (this.mLeScanClients) {
            if (this.mLeScanClients.containsKey(callback)) {
                Log.e(TAG, "LE Scan has already started");
                return false;
            }
            try {
                iGatt = this.mManagerService.getBluetoothGatt();
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
            }
            if (iGatt == null) {
                return false;
            }
            UUID uuid = UUID.randomUUID();
            GattCallbackWrapper wrapper = new GattCallbackWrapper(this, callback, serviceUuids);
            iGatt.registerClient(new ParcelUuid(uuid), wrapper);
            if (wrapper.scanStarted()) {
                this.mLeScanClients.put(callback, wrapper);
                return true;
            }
            return false;
        }
    }

    public void stopLeScan(LeScanCallback callback) {
        Log.d(TAG, "stopLeScan()");
        synchronized (this.mLeScanClients) {
            GattCallbackWrapper wrapper = this.mLeScanClients.remove(callback);
            if (wrapper == null) {
                return;
            }
            wrapper.stopLeScan();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: BluetoothAdapter$GattCallbackWrapper.class */
    public static class GattCallbackWrapper extends IBluetoothGattCallback.Stub {
        private static final int LE_CALLBACK_REG_TIMEOUT = 2000;
        private static final int LE_CALLBACK_REG_WAIT_COUNT = 5;
        private final LeScanCallback mLeScanCb;
        private int mLeHandle = 0;
        private final UUID[] mScanFilter;
        private WeakReference<BluetoothAdapter> mBluetoothAdapter;

        public GattCallbackWrapper(BluetoothAdapter bluetoothAdapter, LeScanCallback leScanCb, UUID[] uuid) {
            this.mBluetoothAdapter = new WeakReference<>(bluetoothAdapter);
            this.mLeScanCb = leScanCb;
            this.mScanFilter = uuid;
        }

        public boolean scanStarted() {
            synchronized (this) {
                if (this.mLeHandle == -1) {
                    return false;
                }
                for (int count = 0; this.mLeHandle == 0 && count < 5; count++) {
                    try {
                        wait(2000L);
                    } catch (InterruptedException e) {
                        Log.e(BluetoothAdapter.TAG, "Callback reg wait interrupted: " + e);
                    }
                }
                boolean started = this.mLeHandle > 0;
                return started;
            }
        }

        public void stopLeScan() {
            synchronized (this) {
                if (this.mLeHandle <= 0) {
                    Log.e(BluetoothAdapter.TAG, "Error state, mLeHandle: " + this.mLeHandle);
                    return;
                }
                BluetoothAdapter adapter = this.mBluetoothAdapter.get();
                if (adapter != null) {
                    try {
                        IBluetoothGatt iGatt = adapter.getBluetoothManager().getBluetoothGatt();
                        iGatt.stopScan(this.mLeHandle, false);
                        iGatt.unregisterClient(this.mLeHandle);
                    } catch (RemoteException e) {
                        Log.e(BluetoothAdapter.TAG, "Failed to stop scan and unregister" + e);
                    }
                } else {
                    Log.e(BluetoothAdapter.TAG, "stopLeScan, BluetoothAdapter is null");
                }
                this.mLeHandle = -1;
                notifyAll();
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onClientRegistered(int status, int clientIf) {
            Log.d(BluetoothAdapter.TAG, "onClientRegistered() - status=" + status + " clientIf=" + clientIf);
            synchronized (this) {
                if (this.mLeHandle == -1) {
                    Log.d(BluetoothAdapter.TAG, "onClientRegistered LE scan canceled");
                }
                if (status == 0) {
                    this.mLeHandle = clientIf;
                    IBluetoothGatt iGatt = null;
                    try {
                        BluetoothAdapter adapter = this.mBluetoothAdapter.get();
                        if (adapter != null) {
                            iGatt = adapter.getBluetoothManager().getBluetoothGatt();
                            if (this.mScanFilter == null) {
                                iGatt.startScan(this.mLeHandle, false);
                            } else {
                                ParcelUuid[] uuids = new ParcelUuid[this.mScanFilter.length];
                                for (int i = 0; i != uuids.length; i++) {
                                    uuids[i] = new ParcelUuid(this.mScanFilter[i]);
                                }
                                iGatt.startScanWithUuids(this.mLeHandle, false, uuids);
                            }
                        } else {
                            Log.e(BluetoothAdapter.TAG, "onClientRegistered, BluetoothAdapter null");
                            this.mLeHandle = -1;
                        }
                    } catch (RemoteException e) {
                        Log.e(BluetoothAdapter.TAG, "fail to start le scan: " + e);
                        this.mLeHandle = -1;
                    }
                    if (this.mLeHandle == -1 && iGatt != null) {
                        try {
                            iGatt.unregisterClient(this.mLeHandle);
                        } catch (RemoteException e2) {
                            Log.e(BluetoothAdapter.TAG, "fail to unregister callback: " + this.mLeHandle + " error: " + e2);
                        }
                    }
                } else {
                    this.mLeHandle = -1;
                }
                notifyAll();
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onClientConnectionState(int status, int clientIf, boolean connected, String address) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onScanResult(String address, int rssi, byte[] advData) {
            Log.d(BluetoothAdapter.TAG, "onScanResult() - Device=" + address + " RSSI=" + rssi);
            synchronized (this) {
                if (this.mLeHandle <= 0) {
                    return;
                }
                try {
                    BluetoothAdapter adapter = this.mBluetoothAdapter.get();
                    if (adapter == null) {
                        Log.d(BluetoothAdapter.TAG, "onScanResult, BluetoothAdapter null");
                    } else {
                        this.mLeScanCb.onLeScan(adapter.getRemoteDevice(address), rssi, advData);
                    }
                } catch (Exception ex) {
                    Log.w(BluetoothAdapter.TAG, "Unhandled exception: " + ex);
                }
            }
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onGetService(String address, int srvcType, int srvcInstId, ParcelUuid srvcUuid) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onGetIncludedService(String address, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int inclSrvcType, int inclSrvcInstId, ParcelUuid inclSrvcUuid) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onGetCharacteristic(String address, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, int charProps) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onGetDescriptor(String address, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, int descInstId, ParcelUuid descUuid) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onSearchComplete(String address, int status) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onCharacteristicRead(String address, int status, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, byte[] value) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onCharacteristicWrite(String address, int status, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onNotify(String address, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, byte[] value) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onDescriptorRead(String address, int status, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, int descInstId, ParcelUuid descrUuid, byte[] value) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onDescriptorWrite(String address, int status, int srvcType, int srvcInstId, ParcelUuid srvcUuid, int charInstId, ParcelUuid charUuid, int descInstId, ParcelUuid descrUuid) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onExecuteWrite(String address, int status) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onReadRemoteRssi(String address, int rssi, int status) {
        }

        @Override // android.bluetooth.IBluetoothGattCallback
        public void onListen(int status) {
        }
    }
}