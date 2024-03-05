package android.bluetooth;

import android.bluetooth.IBluetoothGattCallback;
import android.bluetooth.IBluetoothGattServerCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.RemoteException;
import java.util.List;

/* loaded from: IBluetoothGatt.class */
public interface IBluetoothGatt extends IInterface {
    List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] iArr) throws RemoteException;

    void startScan(int i, boolean z) throws RemoteException;

    void startScanWithUuids(int i, boolean z, ParcelUuid[] parcelUuidArr) throws RemoteException;

    void stopScan(int i, boolean z) throws RemoteException;

    void registerClient(ParcelUuid parcelUuid, IBluetoothGattCallback iBluetoothGattCallback) throws RemoteException;

    void unregisterClient(int i) throws RemoteException;

    void clientConnect(int i, String str, boolean z) throws RemoteException;

    void clientDisconnect(int i, String str) throws RemoteException;

    void clientListen(int i, boolean z) throws RemoteException;

    void setAdvData(int i, boolean z, boolean z2, boolean z3, int i2, int i3, int i4, byte[] bArr) throws RemoteException;

    void refreshDevice(int i, String str) throws RemoteException;

    void discoverServices(int i, String str) throws RemoteException;

    void readCharacteristic(int i, String str, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, int i5) throws RemoteException;

    void writeCharacteristic(int i, String str, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, int i5, int i6, byte[] bArr) throws RemoteException;

    void readDescriptor(int i, String str, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, int i5, ParcelUuid parcelUuid3, int i6) throws RemoteException;

    void writeDescriptor(int i, String str, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, int i5, ParcelUuid parcelUuid3, int i6, int i7, byte[] bArr) throws RemoteException;

    void registerForNotification(int i, String str, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, boolean z) throws RemoteException;

    void beginReliableWrite(int i, String str) throws RemoteException;

    void endReliableWrite(int i, String str, boolean z) throws RemoteException;

    void readRemoteRssi(int i, String str) throws RemoteException;

    void registerServer(ParcelUuid parcelUuid, IBluetoothGattServerCallback iBluetoothGattServerCallback) throws RemoteException;

    void unregisterServer(int i) throws RemoteException;

    void serverConnect(int i, String str, boolean z) throws RemoteException;

    void serverDisconnect(int i, String str) throws RemoteException;

    void beginServiceDeclaration(int i, int i2, int i3, int i4, ParcelUuid parcelUuid) throws RemoteException;

    void addIncludedService(int i, int i2, int i3, ParcelUuid parcelUuid) throws RemoteException;

    void addCharacteristic(int i, ParcelUuid parcelUuid, int i2, int i3) throws RemoteException;

    void addDescriptor(int i, ParcelUuid parcelUuid, int i2) throws RemoteException;

    void endServiceDeclaration(int i) throws RemoteException;

    void removeService(int i, int i2, int i3, ParcelUuid parcelUuid) throws RemoteException;

    void clearServices(int i) throws RemoteException;

    void sendResponse(int i, String str, int i2, int i3, int i4, byte[] bArr) throws RemoteException;

    void sendNotification(int i, String str, int i2, int i3, ParcelUuid parcelUuid, int i4, ParcelUuid parcelUuid2, boolean z, byte[] bArr) throws RemoteException;

    /* loaded from: IBluetoothGatt$Stub.class */
    public static abstract class Stub extends Binder implements IBluetoothGatt {
        private static final String DESCRIPTOR = "android.bluetooth.IBluetoothGatt";
        static final int TRANSACTION_getDevicesMatchingConnectionStates = 1;
        static final int TRANSACTION_startScan = 2;
        static final int TRANSACTION_startScanWithUuids = 3;
        static final int TRANSACTION_stopScan = 4;
        static final int TRANSACTION_registerClient = 5;
        static final int TRANSACTION_unregisterClient = 6;
        static final int TRANSACTION_clientConnect = 7;
        static final int TRANSACTION_clientDisconnect = 8;
        static final int TRANSACTION_clientListen = 9;
        static final int TRANSACTION_setAdvData = 10;
        static final int TRANSACTION_refreshDevice = 11;
        static final int TRANSACTION_discoverServices = 12;
        static final int TRANSACTION_readCharacteristic = 13;
        static final int TRANSACTION_writeCharacteristic = 14;
        static final int TRANSACTION_readDescriptor = 15;
        static final int TRANSACTION_writeDescriptor = 16;
        static final int TRANSACTION_registerForNotification = 17;
        static final int TRANSACTION_beginReliableWrite = 18;
        static final int TRANSACTION_endReliableWrite = 19;
        static final int TRANSACTION_readRemoteRssi = 20;
        static final int TRANSACTION_registerServer = 21;
        static final int TRANSACTION_unregisterServer = 22;
        static final int TRANSACTION_serverConnect = 23;
        static final int TRANSACTION_serverDisconnect = 24;
        static final int TRANSACTION_beginServiceDeclaration = 25;
        static final int TRANSACTION_addIncludedService = 26;
        static final int TRANSACTION_addCharacteristic = 27;
        static final int TRANSACTION_addDescriptor = 28;
        static final int TRANSACTION_endServiceDeclaration = 29;
        static final int TRANSACTION_removeService = 30;
        static final int TRANSACTION_clearServices = 31;
        static final int TRANSACTION_sendResponse = 32;
        static final int TRANSACTION_sendNotification = 33;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBluetoothGatt asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBluetoothGatt)) {
                return (IBluetoothGatt) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ParcelUuid _arg4;
            ParcelUuid _arg6;
            ParcelUuid _arg3;
            ParcelUuid _arg1;
            ParcelUuid _arg12;
            ParcelUuid _arg32;
            ParcelUuid _arg42;
            ParcelUuid _arg0;
            ParcelUuid _arg43;
            ParcelUuid _arg62;
            ParcelUuid _arg44;
            ParcelUuid _arg63;
            ParcelUuid _arg8;
            ParcelUuid _arg45;
            ParcelUuid _arg64;
            ParcelUuid _arg82;
            ParcelUuid _arg46;
            ParcelUuid _arg65;
            ParcelUuid _arg47;
            ParcelUuid _arg66;
            ParcelUuid _arg02;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _arg03 = data.createIntArray();
                    List<BluetoothDevice> _result = getDevicesMatchingConnectionStates(_arg03);
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    boolean _arg13 = 0 != data.readInt();
                    startScan(_arg04, _arg13);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    boolean _arg14 = 0 != data.readInt();
                    ParcelUuid[] _arg2 = (ParcelUuid[]) data.createTypedArray(ParcelUuid.CREATOR);
                    startScanWithUuids(_arg05, _arg14, _arg2);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    boolean _arg15 = 0 != data.readInt();
                    stopScan(_arg06, _arg15);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    IBluetoothGattCallback _arg16 = IBluetoothGattCallback.Stub.asInterface(data.readStrongBinder());
                    registerClient(_arg02, _arg16);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    unregisterClient(_arg07);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    String _arg17 = data.readString();
                    boolean _arg22 = 0 != data.readInt();
                    clientConnect(_arg08, _arg17, _arg22);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    String _arg18 = data.readString();
                    clientDisconnect(_arg09, _arg18);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    boolean _arg19 = 0 != data.readInt();
                    clientListen(_arg010, _arg19);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    boolean _arg110 = 0 != data.readInt();
                    boolean _arg23 = 0 != data.readInt();
                    boolean _arg33 = 0 != data.readInt();
                    int _arg48 = data.readInt();
                    int _arg5 = data.readInt();
                    int _arg67 = data.readInt();
                    byte[] _arg7 = data.createByteArray();
                    setAdvData(_arg011, _arg110, _arg23, _arg33, _arg48, _arg5, _arg67, _arg7);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    String _arg111 = data.readString();
                    refreshDevice(_arg012, _arg111);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    String _arg112 = data.readString();
                    discoverServices(_arg013, _arg112);
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    String _arg113 = data.readString();
                    int _arg24 = data.readInt();
                    int _arg34 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg47 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg47 = null;
                    }
                    int _arg52 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg66 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg66 = null;
                    }
                    int _arg72 = data.readInt();
                    readCharacteristic(_arg014, _arg113, _arg24, _arg34, _arg47, _arg52, _arg66, _arg72);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    String _arg114 = data.readString();
                    int _arg25 = data.readInt();
                    int _arg35 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg46 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg46 = null;
                    }
                    int _arg53 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg65 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg65 = null;
                    }
                    int _arg73 = data.readInt();
                    int _arg83 = data.readInt();
                    byte[] _arg9 = data.createByteArray();
                    writeCharacteristic(_arg015, _arg114, _arg25, _arg35, _arg46, _arg53, _arg65, _arg73, _arg83, _arg9);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg016 = data.readInt();
                    String _arg115 = data.readString();
                    int _arg26 = data.readInt();
                    int _arg36 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg45 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg45 = null;
                    }
                    int _arg54 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg64 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg64 = null;
                    }
                    int _arg74 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg82 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg82 = null;
                    }
                    int _arg92 = data.readInt();
                    readDescriptor(_arg016, _arg115, _arg26, _arg36, _arg45, _arg54, _arg64, _arg74, _arg82, _arg92);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    String _arg116 = data.readString();
                    int _arg27 = data.readInt();
                    int _arg37 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg44 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg44 = null;
                    }
                    int _arg55 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg63 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg63 = null;
                    }
                    int _arg75 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg8 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg8 = null;
                    }
                    int _arg93 = data.readInt();
                    int _arg10 = data.readInt();
                    byte[] _arg11 = data.createByteArray();
                    writeDescriptor(_arg017, _arg116, _arg27, _arg37, _arg44, _arg55, _arg63, _arg75, _arg8, _arg93, _arg10, _arg11);
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    String _arg117 = data.readString();
                    int _arg28 = data.readInt();
                    int _arg38 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg43 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg43 = null;
                    }
                    int _arg56 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg62 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg62 = null;
                    }
                    boolean _arg76 = 0 != data.readInt();
                    registerForNotification(_arg018, _arg117, _arg28, _arg38, _arg43, _arg56, _arg62, _arg76);
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg019 = data.readInt();
                    String _arg118 = data.readString();
                    beginReliableWrite(_arg019, _arg118);
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg020 = data.readInt();
                    String _arg119 = data.readString();
                    boolean _arg29 = 0 != data.readInt();
                    endReliableWrite(_arg020, _arg119, _arg29);
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg021 = data.readInt();
                    String _arg120 = data.readString();
                    readRemoteRssi(_arg021, _arg120);
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    IBluetoothGattServerCallback _arg121 = IBluetoothGattServerCallback.Stub.asInterface(data.readStrongBinder());
                    registerServer(_arg0, _arg121);
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg022 = data.readInt();
                    unregisterServer(_arg022);
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg023 = data.readInt();
                    String _arg122 = data.readString();
                    boolean _arg210 = 0 != data.readInt();
                    serverConnect(_arg023, _arg122, _arg210);
                    reply.writeNoException();
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg024 = data.readInt();
                    String _arg123 = data.readString();
                    serverDisconnect(_arg024, _arg123);
                    reply.writeNoException();
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg025 = data.readInt();
                    int _arg124 = data.readInt();
                    int _arg211 = data.readInt();
                    int _arg39 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg42 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg42 = null;
                    }
                    beginServiceDeclaration(_arg025, _arg124, _arg211, _arg39, _arg42);
                    reply.writeNoException();
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg026 = data.readInt();
                    int _arg125 = data.readInt();
                    int _arg212 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg32 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg32 = null;
                    }
                    addIncludedService(_arg026, _arg125, _arg212, _arg32);
                    reply.writeNoException();
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg027 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg12 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    int _arg213 = data.readInt();
                    int _arg310 = data.readInt();
                    addCharacteristic(_arg027, _arg12, _arg213, _arg310);
                    reply.writeNoException();
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg028 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg1 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    int _arg214 = data.readInt();
                    addDescriptor(_arg028, _arg1, _arg214);
                    reply.writeNoException();
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg029 = data.readInt();
                    endServiceDeclaration(_arg029);
                    reply.writeNoException();
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg030 = data.readInt();
                    int _arg126 = data.readInt();
                    int _arg215 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg3 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    removeService(_arg030, _arg126, _arg215, _arg3);
                    reply.writeNoException();
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg031 = data.readInt();
                    clearServices(_arg031);
                    reply.writeNoException();
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg032 = data.readInt();
                    String _arg127 = data.readString();
                    int _arg216 = data.readInt();
                    int _arg311 = data.readInt();
                    int _arg49 = data.readInt();
                    byte[] _arg57 = data.createByteArray();
                    sendResponse(_arg032, _arg127, _arg216, _arg311, _arg49, _arg57);
                    reply.writeNoException();
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg033 = data.readInt();
                    String _arg128 = data.readString();
                    int _arg217 = data.readInt();
                    int _arg312 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg4 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    int _arg58 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg6 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg6 = null;
                    }
                    boolean _arg77 = 0 != data.readInt();
                    byte[] _arg84 = data.createByteArray();
                    sendNotification(_arg033, _arg128, _arg217, _arg312, _arg4, _arg58, _arg6, _arg77, _arg84);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IBluetoothGatt$Stub$Proxy.class */
        private static class Proxy implements IBluetoothGatt {
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

            @Override // android.bluetooth.IBluetoothGatt
            public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(states);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<BluetoothDevice> _result = _reply.createTypedArrayList(BluetoothDevice.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void startScan(int appIf, boolean isServer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appIf);
                    _data.writeInt(isServer ? 1 : 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void startScanWithUuids(int appIf, boolean isServer, ParcelUuid[] ids) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appIf);
                    _data.writeInt(isServer ? 1 : 0);
                    _data.writeTypedArray(ids, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void stopScan(int appIf, boolean isServer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appIf);
                    _data.writeInt(isServer ? 1 : 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void registerClient(ParcelUuid appId, IBluetoothGattCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (appId != null) {
                        _data.writeInt(1);
                        appId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void unregisterClient(int clientIf) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void clientConnect(int clientIf, String address, boolean isDirect) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    _data.writeInt(isDirect ? 1 : 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void clientDisconnect(int clientIf, String address) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void clientListen(int clientIf, boolean start) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeInt(start ? 1 : 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void setAdvData(int clientIf, boolean setScanRsp, boolean inclName, boolean inclTxPower, int minInterval, int maxInterval, int appearance, byte[] manufacturerData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeInt(setScanRsp ? 1 : 0);
                    _data.writeInt(inclName ? 1 : 0);
                    _data.writeInt(inclTxPower ? 1 : 0);
                    _data.writeInt(minInterval);
                    _data.writeInt(maxInterval);
                    _data.writeInt(appearance);
                    _data.writeByteArray(manufacturerData);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void refreshDevice(int clientIf, String address) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void discoverServices(int clientIf, String address) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void readCharacteristic(int clientIf, String address, int srvcType, int srvcInstanceId, ParcelUuid srvcId, int charInstanceId, ParcelUuid charId, int authReq) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstanceId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(charInstanceId);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(authReq);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void writeCharacteristic(int clientIf, String address, int srvcType, int srvcInstanceId, ParcelUuid srvcId, int charInstanceId, ParcelUuid charId, int writeType, int authReq, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstanceId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(charInstanceId);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(writeType);
                    _data.writeInt(authReq);
                    _data.writeByteArray(value);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void readDescriptor(int clientIf, String address, int srvcType, int srvcInstanceId, ParcelUuid srvcId, int charInstanceId, ParcelUuid charId, int descrInstanceId, ParcelUuid descrUuid, int authReq) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstanceId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(charInstanceId);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(descrInstanceId);
                    if (descrUuid != null) {
                        _data.writeInt(1);
                        descrUuid.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(authReq);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void writeDescriptor(int clientIf, String address, int srvcType, int srvcInstanceId, ParcelUuid srvcId, int charInstanceId, ParcelUuid charId, int descrInstanceId, ParcelUuid descrId, int writeType, int authReq, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstanceId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(charInstanceId);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(descrInstanceId);
                    if (descrId != null) {
                        _data.writeInt(1);
                        descrId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(writeType);
                    _data.writeInt(authReq);
                    _data.writeByteArray(value);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void registerForNotification(int clientIf, String address, int srvcType, int srvcInstanceId, ParcelUuid srvcId, int charInstanceId, ParcelUuid charId, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstanceId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(charInstanceId);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void beginReliableWrite(int clientIf, String address) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void endReliableWrite(int clientIf, String address, boolean execute) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    _data.writeInt(execute ? 1 : 0);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void readRemoteRssi(int clientIf, String address) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(clientIf);
                    _data.writeString(address);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void registerServer(ParcelUuid appId, IBluetoothGattServerCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (appId != null) {
                        _data.writeInt(1);
                        appId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void unregisterServer(int serverIf) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void serverConnect(int servertIf, String address, boolean isDirect) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(servertIf);
                    _data.writeString(address);
                    _data.writeInt(isDirect ? 1 : 0);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void serverDisconnect(int serverIf, String address) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    _data.writeString(address);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void beginServiceDeclaration(int serverIf, int srvcType, int srvcInstanceId, int minHandles, ParcelUuid srvcId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstanceId);
                    _data.writeInt(minHandles);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void addIncludedService(int serverIf, int srvcType, int srvcInstanceId, ParcelUuid srvcId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstanceId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void addCharacteristic(int serverIf, ParcelUuid charId, int properties, int permissions) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(properties);
                    _data.writeInt(permissions);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void addDescriptor(int serverIf, ParcelUuid descId, int permissions) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    if (descId != null) {
                        _data.writeInt(1);
                        descId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(permissions);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void endServiceDeclaration(int serverIf) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void removeService(int serverIf, int srvcType, int srvcInstanceId, ParcelUuid srvcId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstanceId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void clearServices(int serverIf) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void sendResponse(int serverIf, String address, int requestId, int status, int offset, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    _data.writeString(address);
                    _data.writeInt(requestId);
                    _data.writeInt(status);
                    _data.writeInt(offset);
                    _data.writeByteArray(value);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGatt
            public void sendNotification(int serverIf, String address, int srvcType, int srvcInstanceId, ParcelUuid srvcId, int charInstanceId, ParcelUuid charId, boolean confirm, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serverIf);
                    _data.writeString(address);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstanceId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(charInstanceId);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(confirm ? 1 : 0);
                    _data.writeByteArray(value);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}