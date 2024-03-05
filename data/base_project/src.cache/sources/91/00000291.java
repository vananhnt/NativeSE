package android.bluetooth;

import android.bluetooth.IBluetoothCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.ParcelUuid;
import android.os.RemoteException;

/* loaded from: IBluetooth.class */
public interface IBluetooth extends IInterface {
    boolean isEnabled() throws RemoteException;

    int getState() throws RemoteException;

    boolean enable() throws RemoteException;

    boolean enableNoAutoConnect() throws RemoteException;

    boolean disable() throws RemoteException;

    String getAddress() throws RemoteException;

    ParcelUuid[] getUuids() throws RemoteException;

    boolean setName(String str) throws RemoteException;

    String getName() throws RemoteException;

    int getScanMode() throws RemoteException;

    boolean setScanMode(int i, int i2) throws RemoteException;

    int getDiscoverableTimeout() throws RemoteException;

    boolean setDiscoverableTimeout(int i) throws RemoteException;

    boolean startDiscovery() throws RemoteException;

    boolean cancelDiscovery() throws RemoteException;

    boolean isDiscovering() throws RemoteException;

    int getAdapterConnectionState() throws RemoteException;

    int getProfileConnectionState(int i) throws RemoteException;

    BluetoothDevice[] getBondedDevices() throws RemoteException;

    boolean createBond(BluetoothDevice bluetoothDevice) throws RemoteException;

    boolean cancelBondProcess(BluetoothDevice bluetoothDevice) throws RemoteException;

    boolean removeBond(BluetoothDevice bluetoothDevice) throws RemoteException;

    int getBondState(BluetoothDevice bluetoothDevice) throws RemoteException;

    String getRemoteName(BluetoothDevice bluetoothDevice) throws RemoteException;

    int getRemoteType(BluetoothDevice bluetoothDevice) throws RemoteException;

    String getRemoteAlias(BluetoothDevice bluetoothDevice) throws RemoteException;

    boolean setRemoteAlias(BluetoothDevice bluetoothDevice, String str) throws RemoteException;

    int getRemoteClass(BluetoothDevice bluetoothDevice) throws RemoteException;

    ParcelUuid[] getRemoteUuids(BluetoothDevice bluetoothDevice) throws RemoteException;

    boolean fetchRemoteUuids(BluetoothDevice bluetoothDevice) throws RemoteException;

    boolean setPin(BluetoothDevice bluetoothDevice, boolean z, int i, byte[] bArr) throws RemoteException;

    boolean setPasskey(BluetoothDevice bluetoothDevice, boolean z, int i, byte[] bArr) throws RemoteException;

    boolean setPairingConfirmation(BluetoothDevice bluetoothDevice, boolean z) throws RemoteException;

    void sendConnectionStateChange(BluetoothDevice bluetoothDevice, int i, int i2, int i3) throws RemoteException;

    void registerCallback(IBluetoothCallback iBluetoothCallback) throws RemoteException;

    void unregisterCallback(IBluetoothCallback iBluetoothCallback) throws RemoteException;

    ParcelFileDescriptor connectSocket(BluetoothDevice bluetoothDevice, int i, ParcelUuid parcelUuid, int i2, int i3) throws RemoteException;

    ParcelFileDescriptor createSocketChannel(int i, String str, ParcelUuid parcelUuid, int i2, int i3) throws RemoteException;

    boolean configHciSnoopLog(boolean z) throws RemoteException;

    /* loaded from: IBluetooth$Stub.class */
    public static abstract class Stub extends Binder implements IBluetooth {
        private static final String DESCRIPTOR = "android.bluetooth.IBluetooth";
        static final int TRANSACTION_isEnabled = 1;
        static final int TRANSACTION_getState = 2;
        static final int TRANSACTION_enable = 3;
        static final int TRANSACTION_enableNoAutoConnect = 4;
        static final int TRANSACTION_disable = 5;
        static final int TRANSACTION_getAddress = 6;
        static final int TRANSACTION_getUuids = 7;
        static final int TRANSACTION_setName = 8;
        static final int TRANSACTION_getName = 9;
        static final int TRANSACTION_getScanMode = 10;
        static final int TRANSACTION_setScanMode = 11;
        static final int TRANSACTION_getDiscoverableTimeout = 12;
        static final int TRANSACTION_setDiscoverableTimeout = 13;
        static final int TRANSACTION_startDiscovery = 14;
        static final int TRANSACTION_cancelDiscovery = 15;
        static final int TRANSACTION_isDiscovering = 16;
        static final int TRANSACTION_getAdapterConnectionState = 17;
        static final int TRANSACTION_getProfileConnectionState = 18;
        static final int TRANSACTION_getBondedDevices = 19;
        static final int TRANSACTION_createBond = 20;
        static final int TRANSACTION_cancelBondProcess = 21;
        static final int TRANSACTION_removeBond = 22;
        static final int TRANSACTION_getBondState = 23;
        static final int TRANSACTION_getRemoteName = 24;
        static final int TRANSACTION_getRemoteType = 25;
        static final int TRANSACTION_getRemoteAlias = 26;
        static final int TRANSACTION_setRemoteAlias = 27;
        static final int TRANSACTION_getRemoteClass = 28;
        static final int TRANSACTION_getRemoteUuids = 29;
        static final int TRANSACTION_fetchRemoteUuids = 30;
        static final int TRANSACTION_setPin = 31;
        static final int TRANSACTION_setPasskey = 32;
        static final int TRANSACTION_setPairingConfirmation = 33;
        static final int TRANSACTION_sendConnectionStateChange = 34;
        static final int TRANSACTION_registerCallback = 35;
        static final int TRANSACTION_unregisterCallback = 36;
        static final int TRANSACTION_connectSocket = 37;
        static final int TRANSACTION_createSocketChannel = 38;
        static final int TRANSACTION_configHciSnoopLog = 39;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBluetooth asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBluetooth)) {
                return (IBluetooth) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ParcelUuid _arg2;
            BluetoothDevice _arg0;
            ParcelUuid _arg22;
            BluetoothDevice _arg02;
            BluetoothDevice _arg03;
            BluetoothDevice _arg04;
            BluetoothDevice _arg05;
            BluetoothDevice _arg06;
            BluetoothDevice _arg07;
            BluetoothDevice _arg08;
            BluetoothDevice _arg09;
            BluetoothDevice _arg010;
            BluetoothDevice _arg011;
            BluetoothDevice _arg012;
            BluetoothDevice _arg013;
            BluetoothDevice _arg014;
            BluetoothDevice _arg015;
            BluetoothDevice _arg016;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result = isEnabled();
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _result2 = getState();
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result3 = enable();
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result4 = enableNoAutoConnect();
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result5 = disable();
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _result6 = getAddress();
                    reply.writeNoException();
                    reply.writeString(_result6);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    ParcelUuid[] _result7 = getUuids();
                    reply.writeNoException();
                    reply.writeTypedArray(_result7, 1);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg017 = data.readString();
                    boolean _result8 = setName(_arg017);
                    reply.writeNoException();
                    reply.writeInt(_result8 ? 1 : 0);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _result9 = getName();
                    reply.writeNoException();
                    reply.writeString(_result9);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _result10 = getScanMode();
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    int _arg1 = data.readInt();
                    boolean _result11 = setScanMode(_arg018, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result11 ? 1 : 0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _result12 = getDiscoverableTimeout();
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg019 = data.readInt();
                    boolean _result13 = setDiscoverableTimeout(_arg019);
                    reply.writeNoException();
                    reply.writeInt(_result13 ? 1 : 0);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result14 = startDiscovery();
                    reply.writeNoException();
                    reply.writeInt(_result14 ? 1 : 0);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result15 = cancelDiscovery();
                    reply.writeNoException();
                    reply.writeInt(_result15 ? 1 : 0);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result16 = isDiscovering();
                    reply.writeNoException();
                    reply.writeInt(_result16 ? 1 : 0);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    int _result17 = getAdapterConnectionState();
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg020 = data.readInt();
                    int _result18 = getProfileConnectionState(_arg020);
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    BluetoothDevice[] _result19 = getBondedDevices();
                    reply.writeNoException();
                    reply.writeTypedArray(_result19, 1);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg016 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg016 = null;
                    }
                    boolean _result20 = createBond(_arg016);
                    reply.writeNoException();
                    reply.writeInt(_result20 ? 1 : 0);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg015 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg015 = null;
                    }
                    boolean _result21 = cancelBondProcess(_arg015);
                    reply.writeNoException();
                    reply.writeInt(_result21 ? 1 : 0);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg014 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg014 = null;
                    }
                    boolean _result22 = removeBond(_arg014);
                    reply.writeNoException();
                    reply.writeInt(_result22 ? 1 : 0);
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg013 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg013 = null;
                    }
                    int _result23 = getBondState(_arg013);
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg012 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg012 = null;
                    }
                    String _result24 = getRemoteName(_arg012);
                    reply.writeNoException();
                    reply.writeString(_result24);
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg011 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg011 = null;
                    }
                    int _result25 = getRemoteType(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result25);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg010 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    String _result26 = getRemoteAlias(_arg010);
                    reply.writeNoException();
                    reply.writeString(_result26);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg09 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg09 = null;
                    }
                    String _arg12 = data.readString();
                    boolean _result27 = setRemoteAlias(_arg09, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result27 ? 1 : 0);
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    int _result28 = getRemoteClass(_arg08);
                    reply.writeNoException();
                    reply.writeInt(_result28);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    ParcelUuid[] _result29 = getRemoteUuids(_arg07);
                    reply.writeNoException();
                    reply.writeTypedArray(_result29, 1);
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    boolean _result30 = fetchRemoteUuids(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result30 ? 1 : 0);
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    boolean _arg13 = 0 != data.readInt();
                    int _arg23 = data.readInt();
                    byte[] _arg3 = data.createByteArray();
                    boolean _result31 = setPin(_arg05, _arg13, _arg23, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result31 ? 1 : 0);
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    boolean _arg14 = 0 != data.readInt();
                    int _arg24 = data.readInt();
                    byte[] _arg32 = data.createByteArray();
                    boolean _result32 = setPasskey(_arg04, _arg14, _arg24, _arg32);
                    reply.writeNoException();
                    reply.writeInt(_result32 ? 1 : 0);
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    boolean _arg15 = 0 != data.readInt();
                    boolean _result33 = setPairingConfirmation(_arg03, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result33 ? 1 : 0);
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _arg16 = data.readInt();
                    int _arg25 = data.readInt();
                    int _arg33 = data.readInt();
                    sendConnectionStateChange(_arg02, _arg16, _arg25, _arg33);
                    reply.writeNoException();
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    IBluetoothCallback _arg021 = IBluetoothCallback.Stub.asInterface(data.readStrongBinder());
                    registerCallback(_arg021);
                    reply.writeNoException();
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    IBluetoothCallback _arg022 = IBluetoothCallback.Stub.asInterface(data.readStrongBinder());
                    unregisterCallback(_arg022);
                    reply.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = BluetoothDevice.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg17 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg22 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    int _arg34 = data.readInt();
                    int _arg4 = data.readInt();
                    ParcelFileDescriptor _result34 = connectSocket(_arg0, _arg17, _arg22, _arg34, _arg4);
                    reply.writeNoException();
                    if (_result34 != null) {
                        reply.writeInt(1);
                        _result34.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg023 = data.readInt();
                    String _arg18 = data.readString();
                    if (0 != data.readInt()) {
                        _arg2 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    int _arg35 = data.readInt();
                    int _arg42 = data.readInt();
                    ParcelFileDescriptor _result35 = createSocketChannel(_arg023, _arg18, _arg2, _arg35, _arg42);
                    reply.writeNoException();
                    if (_result35 != null) {
                        reply.writeInt(1);
                        _result35.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg024 = 0 != data.readInt();
                    boolean _result36 = configHciSnoopLog(_arg024);
                    reply.writeNoException();
                    reply.writeInt(_result36 ? 1 : 0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IBluetooth$Stub$Proxy.class */
        private static class Proxy implements IBluetooth {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.bluetooth.IBluetooth
            public boolean isEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public int getState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean enable() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean enableNoAutoConnect() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean disable() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public String getAddress() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public ParcelUuid[] getUuids() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    ParcelUuid[] _result = (ParcelUuid[]) _reply.createTypedArray(ParcelUuid.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean setName(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public String getName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public int getScanMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean setScanMode(int mode, int duration) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeInt(duration);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public int getDiscoverableTimeout() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean setDiscoverableTimeout(int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(timeout);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean startDiscovery() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean cancelDiscovery() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean isDiscovering() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public int getAdapterConnectionState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public int getProfileConnectionState(int profile) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(profile);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public BluetoothDevice[] getBondedDevices() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    BluetoothDevice[] _result = (BluetoothDevice[]) _reply.createTypedArray(BluetoothDevice.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean createBond(BluetoothDevice device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean cancelBondProcess(BluetoothDevice device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean removeBond(BluetoothDevice device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public int getBondState(BluetoothDevice device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public String getRemoteName(BluetoothDevice device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public int getRemoteType(BluetoothDevice device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public String getRemoteAlias(BluetoothDevice device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean setRemoteAlias(BluetoothDevice device, String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(name);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public int getRemoteClass(BluetoothDevice device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public ParcelUuid[] getRemoteUuids(BluetoothDevice device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    ParcelUuid[] _result = (ParcelUuid[]) _reply.createTypedArray(ParcelUuid.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean fetchRemoteUuids(BluetoothDevice device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean setPin(BluetoothDevice device, boolean accept, int len, byte[] pinCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(accept ? 1 : 0);
                    _data.writeInt(len);
                    _data.writeByteArray(pinCode);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean setPasskey(BluetoothDevice device, boolean accept, int len, byte[] passkey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(accept ? 1 : 0);
                    _data.writeInt(len);
                    _data.writeByteArray(passkey);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean setPairingConfirmation(BluetoothDevice device, boolean accept) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(accept ? 1 : 0);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public void sendConnectionStateChange(BluetoothDevice device, int profile, int state, int prevState) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(profile);
                    _data.writeInt(state);
                    _data.writeInt(prevState);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public void registerCallback(IBluetoothCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public void unregisterCallback(IBluetoothCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetooth
            public ParcelFileDescriptor connectSocket(BluetoothDevice device, int type, ParcelUuid uuid, int port, int flag) throws RemoteException {
                ParcelFileDescriptor _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (device != null) {
                        _data.writeInt(1);
                        device.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(type);
                    if (uuid != null) {
                        _data.writeInt(1);
                        uuid.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(port);
                    _data.writeInt(flag);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ParcelFileDescriptor.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public ParcelFileDescriptor createSocketChannel(int type, String serviceName, ParcelUuid uuid, int port, int flag) throws RemoteException {
                ParcelFileDescriptor _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(serviceName);
                    if (uuid != null) {
                        _data.writeInt(1);
                        uuid.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(port);
                    _data.writeInt(flag);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ParcelFileDescriptor.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.bluetooth.IBluetooth
            public boolean configHciSnoopLog(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}