package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: INetworkManagementEventObserver.class */
public interface INetworkManagementEventObserver extends IInterface {
    void interfaceStatusChanged(String str, boolean z) throws RemoteException;

    void interfaceLinkStateChanged(String str, boolean z) throws RemoteException;

    void interfaceAdded(String str) throws RemoteException;

    void interfaceRemoved(String str) throws RemoteException;

    void addressUpdated(String str, String str2, int i, int i2) throws RemoteException;

    void addressRemoved(String str, String str2, int i, int i2) throws RemoteException;

    void limitReached(String str, String str2) throws RemoteException;

    void interfaceClassDataActivityChanged(String str, boolean z) throws RemoteException;

    /* loaded from: INetworkManagementEventObserver$Stub.class */
    public static abstract class Stub extends Binder implements INetworkManagementEventObserver {
        private static final String DESCRIPTOR = "android.net.INetworkManagementEventObserver";
        static final int TRANSACTION_interfaceStatusChanged = 1;
        static final int TRANSACTION_interfaceLinkStateChanged = 2;
        static final int TRANSACTION_interfaceAdded = 3;
        static final int TRANSACTION_interfaceRemoved = 4;
        static final int TRANSACTION_addressUpdated = 5;
        static final int TRANSACTION_addressRemoved = 6;
        static final int TRANSACTION_limitReached = 7;
        static final int TRANSACTION_interfaceClassDataActivityChanged = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkManagementEventObserver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INetworkManagementEventObserver)) {
                return (INetworkManagementEventObserver) iin;
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
                    boolean _arg1 = 0 != data.readInt();
                    interfaceStatusChanged(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    boolean _arg12 = 0 != data.readInt();
                    interfaceLinkStateChanged(_arg02, _arg12);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    interfaceAdded(_arg03);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    interfaceRemoved(_arg04);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    String _arg13 = data.readString();
                    int _arg2 = data.readInt();
                    int _arg3 = data.readInt();
                    addressUpdated(_arg05, _arg13, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    String _arg14 = data.readString();
                    int _arg22 = data.readInt();
                    int _arg32 = data.readInt();
                    addressRemoved(_arg06, _arg14, _arg22, _arg32);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    String _arg15 = data.readString();
                    limitReached(_arg07, _arg15);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg08 = data.readString();
                    boolean _arg16 = 0 != data.readInt();
                    interfaceClassDataActivityChanged(_arg08, _arg16);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: INetworkManagementEventObserver$Stub$Proxy.class */
        private static class Proxy implements INetworkManagementEventObserver {
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

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceStatusChanged(String iface, boolean up) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(up ? 1 : 0);
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

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceLinkStateChanged(String iface, boolean up) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(up ? 1 : 0);
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

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceAdded(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
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

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceRemoved(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
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

            @Override // android.net.INetworkManagementEventObserver
            public void addressUpdated(String address, String iface, int flags, int scope) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeString(iface);
                    _data.writeInt(flags);
                    _data.writeInt(scope);
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

            @Override // android.net.INetworkManagementEventObserver
            public void addressRemoved(String address, String iface, int flags, int scope) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeString(iface);
                    _data.writeInt(flags);
                    _data.writeInt(scope);
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

            @Override // android.net.INetworkManagementEventObserver
            public void limitReached(String limitName, String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(limitName);
                    _data.writeString(iface);
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

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceClassDataActivityChanged(String label, boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(label);
                    _data.writeInt(active ? 1 : 0);
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
        }
    }
}