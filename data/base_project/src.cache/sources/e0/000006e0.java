package android.location;

import android.location.IGpsStatusListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IGpsStatusProvider.class */
public interface IGpsStatusProvider extends IInterface {
    void addGpsStatusListener(IGpsStatusListener iGpsStatusListener) throws RemoteException;

    void removeGpsStatusListener(IGpsStatusListener iGpsStatusListener) throws RemoteException;

    /* loaded from: IGpsStatusProvider$Stub.class */
    public static abstract class Stub extends Binder implements IGpsStatusProvider {
        private static final String DESCRIPTOR = "android.location.IGpsStatusProvider";
        static final int TRANSACTION_addGpsStatusListener = 1;
        static final int TRANSACTION_removeGpsStatusListener = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IGpsStatusProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGpsStatusProvider)) {
                return (IGpsStatusProvider) iin;
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
                    IGpsStatusListener _arg0 = IGpsStatusListener.Stub.asInterface(data.readStrongBinder());
                    addGpsStatusListener(_arg0);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IGpsStatusListener _arg02 = IGpsStatusListener.Stub.asInterface(data.readStrongBinder());
                    removeGpsStatusListener(_arg02);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IGpsStatusProvider$Stub$Proxy.class */
        private static class Proxy implements IGpsStatusProvider {
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

            @Override // android.location.IGpsStatusProvider
            public void addGpsStatusListener(IGpsStatusListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
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

            @Override // android.location.IGpsStatusProvider
            public void removeGpsStatusListener(IGpsStatusListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
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
        }
    }
}