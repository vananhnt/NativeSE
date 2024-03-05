package android.bluetooth;

import android.bluetooth.IBluetoothManagerCallback;
import android.content.Context;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import gov.nist.core.Separators;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/* loaded from: BluetoothDevice.class */
public final class BluetoothDevice implements Parcelable {
    private static final String TAG = "BluetoothDevice";
    private static final boolean DBG = false;
    public static final int ERROR = Integer.MIN_VALUE;
    public static final String ACTION_FOUND = "android.bluetooth.device.action.FOUND";
    public static final String ACTION_DISAPPEARED = "android.bluetooth.device.action.DISAPPEARED";
    public static final String ACTION_CLASS_CHANGED = "android.bluetooth.device.action.CLASS_CHANGED";
    public static final String ACTION_ACL_CONNECTED = "android.bluetooth.device.action.ACL_CONNECTED";
    public static final String ACTION_ACL_DISCONNECT_REQUESTED = "android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED";
    public static final String ACTION_ACL_DISCONNECTED = "android.bluetooth.device.action.ACL_DISCONNECTED";
    public static final String ACTION_NAME_CHANGED = "android.bluetooth.device.action.NAME_CHANGED";
    public static final String ACTION_ALIAS_CHANGED = "android.bluetooth.device.action.ALIAS_CHANGED";
    public static final String ACTION_BOND_STATE_CHANGED = "android.bluetooth.device.action.BOND_STATE_CHANGED";
    public static final String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
    public static final String EXTRA_NAME = "android.bluetooth.device.extra.NAME";
    public static final String EXTRA_RSSI = "android.bluetooth.device.extra.RSSI";
    public static final String EXTRA_CLASS = "android.bluetooth.device.extra.CLASS";
    public static final String EXTRA_BOND_STATE = "android.bluetooth.device.extra.BOND_STATE";
    public static final String EXTRA_PREVIOUS_BOND_STATE = "android.bluetooth.device.extra.PREVIOUS_BOND_STATE";
    public static final int BOND_NONE = 10;
    public static final int BOND_BONDING = 11;
    public static final int BOND_BONDED = 12;
    public static final String EXTRA_REASON = "android.bluetooth.device.extra.REASON";
    public static final String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
    public static final String EXTRA_PAIRING_KEY = "android.bluetooth.device.extra.PAIRING_KEY";
    public static final int DEVICE_TYPE_UNKNOWN = 0;
    public static final int DEVICE_TYPE_CLASSIC = 1;
    public static final int DEVICE_TYPE_LE = 2;
    public static final int DEVICE_TYPE_DUAL = 3;
    public static final String ACTION_UUID = "android.bluetooth.device.action.UUID";
    public static final String ACTION_NAME_FAILED = "android.bluetooth.device.action.NAME_FAILED";
    public static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
    public static final String ACTION_PAIRING_CANCEL = "android.bluetooth.device.action.PAIRING_CANCEL";
    public static final String ACTION_CONNECTION_ACCESS_REQUEST = "android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST";
    public static final String ACTION_CONNECTION_ACCESS_REPLY = "android.bluetooth.device.action.CONNECTION_ACCESS_REPLY";
    public static final String ACTION_CONNECTION_ACCESS_CANCEL = "android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL";
    public static final String EXTRA_ACCESS_REQUEST_TYPE = "android.bluetooth.device.extra.ACCESS_REQUEST_TYPE";
    public static final int REQUEST_TYPE_PROFILE_CONNECTION = 1;
    public static final int REQUEST_TYPE_PHONEBOOK_ACCESS = 2;
    public static final int REQUEST_TYPE_MESSAGE_ACCESS = 3;
    public static final String EXTRA_PACKAGE_NAME = "android.bluetooth.device.extra.PACKAGE_NAME";
    public static final String EXTRA_CLASS_NAME = "android.bluetooth.device.extra.CLASS_NAME";
    public static final String EXTRA_CONNECTION_ACCESS_RESULT = "android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT";
    public static final int CONNECTION_ACCESS_YES = 1;
    public static final int CONNECTION_ACCESS_NO = 2;
    public static final String EXTRA_ALWAYS_ALLOWED = "android.bluetooth.device.extra.ALWAYS_ALLOWED";
    public static final int BOND_SUCCESS = 0;
    public static final int UNBOND_REASON_AUTH_FAILED = 1;
    public static final int UNBOND_REASON_AUTH_REJECTED = 2;
    public static final int UNBOND_REASON_AUTH_CANCELED = 3;
    public static final int UNBOND_REASON_REMOTE_DEVICE_DOWN = 4;
    public static final int UNBOND_REASON_DISCOVERY_IN_PROGRESS = 5;
    public static final int UNBOND_REASON_AUTH_TIMEOUT = 6;
    public static final int UNBOND_REASON_REPEATED_ATTEMPTS = 7;
    public static final int UNBOND_REASON_REMOTE_AUTH_CANCELED = 8;
    public static final int UNBOND_REASON_REMOVED = 9;
    public static final int PAIRING_VARIANT_PIN = 0;
    public static final int PAIRING_VARIANT_PASSKEY = 1;
    public static final int PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2;
    public static final int PAIRING_VARIANT_CONSENT = 3;
    public static final int PAIRING_VARIANT_DISPLAY_PASSKEY = 4;
    public static final int PAIRING_VARIANT_DISPLAY_PIN = 5;
    public static final int PAIRING_VARIANT_OOB_CONSENT = 6;
    public static final String EXTRA_UUID = "android.bluetooth.device.extra.UUID";
    private static IBluetooth sService;
    private final String mAddress;
    static IBluetoothManagerCallback mStateChangeCallback = new IBluetoothManagerCallback.Stub() { // from class: android.bluetooth.BluetoothDevice.1
        @Override // android.bluetooth.IBluetoothManagerCallback
        public void onBluetoothServiceUp(IBluetooth bluetoothService) throws RemoteException {
            synchronized (BluetoothDevice.class) {
                IBluetooth unused = BluetoothDevice.sService = bluetoothService;
            }
        }

        @Override // android.bluetooth.IBluetoothManagerCallback
        public void onBluetoothServiceDown() throws RemoteException {
            synchronized (BluetoothDevice.class) {
                IBluetooth unused = BluetoothDevice.sService = null;
            }
        }
    };
    public static final Parcelable.Creator<BluetoothDevice> CREATOR = new Parcelable.Creator<BluetoothDevice>() { // from class: android.bluetooth.BluetoothDevice.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BluetoothDevice createFromParcel(Parcel in) {
            return new BluetoothDevice(in.readString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BluetoothDevice[] newArray(int size) {
            return new BluetoothDevice[size];
        }
    };

    static IBluetooth getService() {
        synchronized (BluetoothDevice.class) {
            if (sService == null) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                sService = adapter.getBluetoothService(mStateChangeCallback);
            }
        }
        return sService;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothDevice(String address) {
        getService();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            throw new IllegalArgumentException(address + " is not a valid Bluetooth address");
        }
        this.mAddress = address;
    }

    public boolean equals(Object o) {
        if (o instanceof BluetoothDevice) {
            return this.mAddress.equals(((BluetoothDevice) o).getAddress());
        }
        return false;
    }

    public int hashCode() {
        return this.mAddress.hashCode();
    }

    public String toString() {
        return this.mAddress;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mAddress);
    }

    public String getAddress() {
        return this.mAddress;
    }

    public String getName() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get Remote Device name");
            return null;
        }
        try {
            return sService.getRemoteName(this);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public int getType() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get Remote Device type");
            return 0;
        }
        try {
            return sService.getRemoteType(this);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return 0;
        }
    }

    public String getAlias() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get Remote Device Alias");
            return null;
        }
        try {
            return sService.getRemoteAlias(this);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public boolean setAlias(String alias) {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot set Remote Device name");
            return false;
        }
        try {
            return sService.setRemoteAlias(this, alias);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public String getAliasName() {
        String name = getAlias();
        if (name == null) {
            name = getName();
        }
        return name;
    }

    public boolean createBond() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot create bond to Remote Device");
            return false;
        }
        try {
            return sService.createBond(this);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean createBondOutOfBand(byte[] hash, byte[] randomizer) {
        return false;
    }

    public boolean setDeviceOutOfBandData(byte[] hash, byte[] randomizer) {
        return false;
    }

    public boolean cancelBondProcess() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot cancel Remote Device bond");
            return false;
        }
        try {
            return sService.cancelBondProcess(this);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean removeBond() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot remove Remote Device bond");
            return false;
        }
        try {
            return sService.removeBond(this);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public int getBondState() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get bond state");
            return 10;
        }
        try {
            return sService.getBondState(this);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return 10;
        } catch (NullPointerException npe) {
            Log.e(TAG, "NullPointerException for getBondState() of device (" + getAddress() + Separators.RPAREN, npe);
            return 10;
        }
    }

    public BluetoothClass getBluetoothClass() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get Bluetooth Class");
            return null;
        }
        try {
            int classInt = sService.getRemoteClass(this);
            if (classInt == -16777216) {
                return null;
            }
            return new BluetoothClass(classInt);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public boolean getTrustState() {
        return false;
    }

    public boolean setTrust(boolean value) {
        return false;
    }

    public ParcelUuid[] getUuids() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get remote device Uuids");
            return null;
        }
        try {
            return sService.getRemoteUuids(this);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public boolean fetchUuidsWithSdp() {
        try {
            return sService.fetchRemoteUuids(this);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public int getServiceChannel(ParcelUuid uuid) {
        return Integer.MIN_VALUE;
    }

    public boolean setPin(byte[] pin) {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot set Remote Device pin");
            return false;
        }
        try {
            return sService.setPin(this, true, pin.length, pin);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean setPasskey(int passkey) {
        return false;
    }

    public boolean setPairingConfirmation(boolean confirm) {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot set pairing confirmation");
            return false;
        }
        try {
            return sService.setPairingConfirmation(this, confirm);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean setRemoteOutOfBandData() {
        return false;
    }

    public boolean cancelPairingUserInput() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot create pairing user input");
            return false;
        }
        try {
            return sService.cancelBondProcess(this);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public boolean isBluetoothDock() {
        return false;
    }

    public BluetoothSocket createRfcommSocket(int channel) throws IOException {
        return new BluetoothSocket(1, -1, true, true, this, channel, null);
    }

    public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) throws IOException {
        return new BluetoothSocket(1, -1, true, true, this, -1, new ParcelUuid(uuid));
    }

    public BluetoothSocket createInsecureRfcommSocketToServiceRecord(UUID uuid) throws IOException {
        return new BluetoothSocket(1, -1, false, false, this, -1, new ParcelUuid(uuid));
    }

    public BluetoothSocket createInsecureRfcommSocket(int port) throws IOException {
        return new BluetoothSocket(1, -1, false, false, this, port, null);
    }

    public BluetoothSocket createScoSocket() throws IOException {
        return new BluetoothSocket(2, -1, true, true, this, -1, null);
    }

    public static byte[] convertPinToBytes(String pin) {
        if (pin == null) {
            return null;
        }
        try {
            byte[] pinBytes = pin.getBytes("UTF-8");
            if (pinBytes.length <= 0 || pinBytes.length > 16) {
                return null;
            }
            return pinBytes;
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UTF-8 not supported?!?");
            return null;
        }
    }

    public BluetoothGatt connectGatt(Context context, boolean autoConnect, BluetoothGattCallback callback) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        IBluetoothManager managerService = adapter.getBluetoothManager();
        try {
            IBluetoothGatt iGatt = managerService.getBluetoothGatt();
            if (iGatt == null) {
                return null;
            }
            BluetoothGatt gatt = new BluetoothGatt(context, iGatt, this);
            gatt.connect(Boolean.valueOf(autoConnect), callback);
            return gatt;
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }
}