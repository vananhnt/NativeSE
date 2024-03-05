package android.bluetooth;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.RemoteException;

/* loaded from: IBluetoothGattServerCallback.class */
public interface IBluetoothGattServerCallback extends IInterface {
    void onServerRegistered(int i, int i2) throws RemoteException;

    void onScanResult(String str, int i, byte[] bArr) throws RemoteException;

    void onServerConnectionState(int i, int i2, boolean z, String str) throws RemoteException;

    void onServiceAdded(int i, int i2, int i3, ParcelUuid parcelUuid) throws RemoteException;

    void onCharacteristicReadRequest(String str, int i, int i2, boolean z, int i3, int i4, ParcelUuid parcelUuid, int i5, ParcelUuid parcelUuid2) throws RemoteException;

    void onDescriptorReadRequest(String str, int i, int i2, boolean z, int i3, int i4, ParcelUuid parcelUuid, int i5, ParcelUuid parcelUuid2, ParcelUuid parcelUuid3) throws RemoteException;

    void onCharacteristicWriteRequest(String str, int i, int i2, int i3, boolean z, boolean z2, int i4, int i5, ParcelUuid parcelUuid, int i6, ParcelUuid parcelUuid2, byte[] bArr) throws RemoteException;

    void onDescriptorWriteRequest(String str, int i, int i2, int i3, boolean z, boolean z2, int i4, int i5, ParcelUuid parcelUuid, int i6, ParcelUuid parcelUuid2, ParcelUuid parcelUuid3, byte[] bArr) throws RemoteException;

    void onExecuteWrite(String str, int i, boolean z) throws RemoteException;

    /* loaded from: IBluetoothGattServerCallback$Stub.class */
    public static abstract class Stub extends Binder implements IBluetoothGattServerCallback {
        private static final String DESCRIPTOR = "android.bluetooth.IBluetoothGattServerCallback";
        static final int TRANSACTION_onServerRegistered = 1;
        static final int TRANSACTION_onScanResult = 2;
        static final int TRANSACTION_onServerConnectionState = 3;
        static final int TRANSACTION_onServiceAdded = 4;
        static final int TRANSACTION_onCharacteristicReadRequest = 5;
        static final int TRANSACTION_onDescriptorReadRequest = 6;
        static final int TRANSACTION_onCharacteristicWriteRequest = 7;
        static final int TRANSACTION_onDescriptorWriteRequest = 8;
        static final int TRANSACTION_onExecuteWrite = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBluetoothGattServerCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBluetoothGattServerCallback)) {
                return (IBluetoothGattServerCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ParcelUuid _arg8;
            ParcelUuid _arg10;
            ParcelUuid _arg11;
            ParcelUuid _arg82;
            ParcelUuid _arg102;
            ParcelUuid _arg6;
            ParcelUuid _arg83;
            ParcelUuid _arg9;
            ParcelUuid _arg62;
            ParcelUuid _arg84;
            ParcelUuid _arg3;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    int _arg1 = data.readInt();
                    onServerRegistered(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    int _arg12 = data.readInt();
                    byte[] _arg2 = data.createByteArray();
                    onScanResult(_arg02, _arg12, _arg2);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    int _arg13 = data.readInt();
                    boolean _arg22 = 0 != data.readInt();
                    String _arg32 = data.readString();
                    onServerConnectionState(_arg03, _arg13, _arg22, _arg32);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    int _arg14 = data.readInt();
                    int _arg23 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg3 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    onServiceAdded(_arg04, _arg14, _arg23, _arg3);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    int _arg15 = data.readInt();
                    int _arg24 = data.readInt();
                    boolean _arg33 = 0 != data.readInt();
                    int _arg4 = data.readInt();
                    int _arg5 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg62 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg62 = null;
                    }
                    int _arg7 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg84 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg84 = null;
                    }
                    onCharacteristicReadRequest(_arg05, _arg15, _arg24, _arg33, _arg4, _arg5, _arg62, _arg7, _arg84);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    int _arg16 = data.readInt();
                    int _arg25 = data.readInt();
                    boolean _arg34 = 0 != data.readInt();
                    int _arg42 = data.readInt();
                    int _arg52 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg6 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg6 = null;
                    }
                    int _arg72 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg83 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg83 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg9 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg9 = null;
                    }
                    onDescriptorReadRequest(_arg06, _arg16, _arg25, _arg34, _arg42, _arg52, _arg6, _arg72, _arg83, _arg9);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    int _arg17 = data.readInt();
                    int _arg26 = data.readInt();
                    int _arg35 = data.readInt();
                    boolean _arg43 = 0 != data.readInt();
                    boolean _arg53 = 0 != data.readInt();
                    int _arg63 = data.readInt();
                    int _arg73 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg82 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg82 = null;
                    }
                    int _arg92 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg102 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg102 = null;
                    }
                    byte[] _arg112 = data.createByteArray();
                    onCharacteristicWriteRequest(_arg07, _arg17, _arg26, _arg35, _arg43, _arg53, _arg63, _arg73, _arg82, _arg92, _arg102, _arg112);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg08 = data.readString();
                    int _arg18 = data.readInt();
                    int _arg27 = data.readInt();
                    int _arg36 = data.readInt();
                    boolean _arg44 = 0 != data.readInt();
                    boolean _arg54 = 0 != data.readInt();
                    int _arg64 = data.readInt();
                    int _arg74 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg8 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg8 = null;
                    }
                    int _arg93 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg10 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg10 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg11 = ParcelUuid.CREATOR.createFromParcel(data);
                    } else {
                        _arg11 = null;
                    }
                    byte[] _arg122 = data.createByteArray();
                    onDescriptorWriteRequest(_arg08, _arg18, _arg27, _arg36, _arg44, _arg54, _arg64, _arg74, _arg8, _arg93, _arg10, _arg11, _arg122);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg09 = data.readString();
                    int _arg19 = data.readInt();
                    boolean _arg28 = 0 != data.readInt();
                    onExecuteWrite(_arg09, _arg19, _arg28);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IBluetoothGattServerCallback$Stub$Proxy.class */
        public static class Proxy implements IBluetoothGattServerCallback {
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

            @Override // android.bluetooth.IBluetoothGattServerCallback
            public void onServerRegistered(int status, int serverIf) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeInt(serverIf);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.bluetooth.IBluetoothGattServerCallback
            public void onScanResult(String address, int rssi, byte[] advData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(rssi);
                    _data.writeByteArray(advData);
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

            @Override // android.bluetooth.IBluetoothGattServerCallback
            public void onServerConnectionState(int status, int serverIf, boolean connected, String address) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeInt(serverIf);
                    _data.writeInt(connected ? 1 : 0);
                    _data.writeString(address);
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

            @Override // android.bluetooth.IBluetoothGattServerCallback
            public void onServiceAdded(int status, int srvcType, int srvcInstId, ParcelUuid srvcId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.bluetooth.IBluetoothGattServerCallback
            public void onCharacteristicReadRequest(String address, int transId, int offset, boolean isLong, int srvcType, int srvcInstId, ParcelUuid srvcId, int charInstId, ParcelUuid charId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(transId);
                    _data.writeInt(offset);
                    _data.writeInt(isLong ? 1 : 0);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(charInstId);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.bluetooth.IBluetoothGattServerCallback
            public void onDescriptorReadRequest(String address, int transId, int offset, boolean isLong, int srvcType, int srvcInstId, ParcelUuid srvcId, int charInstId, ParcelUuid charId, ParcelUuid descrId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(transId);
                    _data.writeInt(offset);
                    _data.writeInt(isLong ? 1 : 0);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(charInstId);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (descrId != null) {
                        _data.writeInt(1);
                        descrId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.bluetooth.IBluetoothGattServerCallback
            public void onCharacteristicWriteRequest(String address, int transId, int offset, int length, boolean isPrep, boolean needRsp, int srvcType, int srvcInstId, ParcelUuid srvcId, int charInstId, ParcelUuid charId, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(transId);
                    _data.writeInt(offset);
                    _data.writeInt(length);
                    _data.writeInt(isPrep ? 1 : 0);
                    _data.writeInt(needRsp ? 1 : 0);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(charInstId);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeByteArray(value);
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

            @Override // android.bluetooth.IBluetoothGattServerCallback
            public void onDescriptorWriteRequest(String address, int transId, int offset, int length, boolean isPrep, boolean needRsp, int srvcType, int srvcInstId, ParcelUuid srvcId, int charInstId, ParcelUuid charId, ParcelUuid descrId, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(transId);
                    _data.writeInt(offset);
                    _data.writeInt(length);
                    _data.writeInt(isPrep ? 1 : 0);
                    _data.writeInt(needRsp ? 1 : 0);
                    _data.writeInt(srvcType);
                    _data.writeInt(srvcInstId);
                    if (srvcId != null) {
                        _data.writeInt(1);
                        srvcId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(charInstId);
                    if (charId != null) {
                        _data.writeInt(1);
                        charId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (descrId != null) {
                        _data.writeInt(1);
                        descrId.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeByteArray(value);
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

            @Override // android.bluetooth.IBluetoothGattServerCallback
            public void onExecuteWrite(String address, int transId, boolean execWrite) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(transId);
                    _data.writeInt(execWrite ? 1 : 0);
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
        }
    }
}