package android.security;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IKeyChainService.class */
public interface IKeyChainService extends IInterface {
    String requestPrivateKey(String str) throws RemoteException;

    byte[] getCertificate(String str) throws RemoteException;

    void installCaCertificate(byte[] bArr) throws RemoteException;

    boolean deleteCaCertificate(String str) throws RemoteException;

    boolean reset() throws RemoteException;

    void setGrant(int i, String str, boolean z) throws RemoteException;

    boolean hasGrant(int i, String str) throws RemoteException;

    /* loaded from: IKeyChainService$Stub.class */
    public static abstract class Stub extends Binder implements IKeyChainService {
        private static final String DESCRIPTOR = "android.security.IKeyChainService";
        static final int TRANSACTION_requestPrivateKey = 1;
        static final int TRANSACTION_getCertificate = 2;
        static final int TRANSACTION_installCaCertificate = 3;
        static final int TRANSACTION_deleteCaCertificate = 4;
        static final int TRANSACTION_reset = 5;
        static final int TRANSACTION_setGrant = 6;
        static final int TRANSACTION_hasGrant = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeyChainService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IKeyChainService)) {
                return (IKeyChainService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    String _result = requestPrivateKey(_arg0);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    byte[] _result2 = getCertificate(_arg02);
                    reply.writeNoException();
                    reply.writeByteArray(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg03 = data.createByteArray();
                    installCaCertificate(_arg03);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    boolean _result3 = deleteCaCertificate(_arg04);
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result4 = reset();
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    String _arg1 = data.readString();
                    boolean _arg2 = 0 != data.readInt();
                    setGrant(_arg05, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    String _arg12 = data.readString();
                    boolean _result5 = hasGrant(_arg06, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IKeyChainService$Stub$Proxy.class */
        private static class Proxy implements IKeyChainService {
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

            @Override // android.security.IKeyChainService
            public String requestPrivateKey(String alias) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    this.mRemote.transact(1, _data, _reply, 0);
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

            @Override // android.security.IKeyChainService
            public byte[] getCertificate(String alias) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.security.IKeyChainService
            public void installCaCertificate(byte[] caCertificate) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(caCertificate);
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

            @Override // android.security.IKeyChainService
            public boolean deleteCaCertificate(String alias) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.IKeyChainService
            public boolean reset() throws RemoteException {
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

            @Override // android.security.IKeyChainService
            public void setGrant(int uid, String alias, boolean value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(alias);
                    _data.writeInt(value ? 1 : 0);
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

            @Override // android.security.IKeyChainService
            public boolean hasGrant(int uid, String alias) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(alias);
                    this.mRemote.transact(7, _data, _reply, 0);
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