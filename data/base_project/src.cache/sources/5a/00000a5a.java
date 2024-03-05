package android.nfc;

import android.content.ComponentName;
import android.nfc.cardemulation.ApduServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* loaded from: INfcCardEmulation.class */
public interface INfcCardEmulation extends IInterface {
    boolean isDefaultServiceForCategory(int i, ComponentName componentName, String str) throws RemoteException;

    boolean isDefaultServiceForAid(int i, ComponentName componentName, String str) throws RemoteException;

    boolean setDefaultServiceForCategory(int i, ComponentName componentName, String str) throws RemoteException;

    boolean setDefaultForNextTap(int i, ComponentName componentName) throws RemoteException;

    List<ApduServiceInfo> getServices(int i, String str) throws RemoteException;

    /* loaded from: INfcCardEmulation$Stub.class */
    public static abstract class Stub extends Binder implements INfcCardEmulation {
        private static final String DESCRIPTOR = "android.nfc.INfcCardEmulation";
        static final int TRANSACTION_isDefaultServiceForCategory = 1;
        static final int TRANSACTION_isDefaultServiceForAid = 2;
        static final int TRANSACTION_setDefaultServiceForCategory = 3;
        static final int TRANSACTION_setDefaultForNextTap = 4;
        static final int TRANSACTION_getServices = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INfcCardEmulation asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INfcCardEmulation)) {
                return (INfcCardEmulation) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ComponentName _arg1;
            ComponentName _arg12;
            ComponentName _arg13;
            ComponentName _arg14;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg14 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg14 = null;
                    }
                    String _arg2 = data.readString();
                    boolean _result = isDefaultServiceForCategory(_arg0, _arg14, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg13 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    String _arg22 = data.readString();
                    boolean _result2 = isDefaultServiceForAid(_arg02, _arg13, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg12 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    String _arg23 = data.readString();
                    boolean _result3 = setDefaultServiceForCategory(_arg03, _arg12, _arg23);
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg1 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    boolean _result4 = setDefaultForNextTap(_arg04, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    String _arg15 = data.readString();
                    List<ApduServiceInfo> _result5 = getServices(_arg05, _arg15);
                    reply.writeNoException();
                    reply.writeTypedList(_result5);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: INfcCardEmulation$Stub$Proxy.class */
        public static class Proxy implements INfcCardEmulation {
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

            @Override // android.nfc.INfcCardEmulation
            public boolean isDefaultServiceForCategory(int userHandle, ComponentName service, String category) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    if (service != null) {
                        _data.writeInt(1);
                        service.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(category);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcCardEmulation
            public boolean isDefaultServiceForAid(int userHandle, ComponentName service, String aid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    if (service != null) {
                        _data.writeInt(1);
                        service.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(aid);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcCardEmulation
            public boolean setDefaultServiceForCategory(int userHandle, ComponentName service, String category) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    if (service != null) {
                        _data.writeInt(1);
                        service.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(category);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcCardEmulation
            public boolean setDefaultForNextTap(int userHandle, ComponentName service) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    if (service != null) {
                        _data.writeInt(1);
                        service.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcCardEmulation
            public List<ApduServiceInfo> getServices(int userHandle, String category) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeString(category);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<ApduServiceInfo> _result = _reply.createTypedArrayList(ApduServiceInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}