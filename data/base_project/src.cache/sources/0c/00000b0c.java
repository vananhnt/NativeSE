package android.os;

import android.os.IBatteryPropertiesListener;

/* loaded from: IBatteryPropertiesRegistrar.class */
public interface IBatteryPropertiesRegistrar extends IInterface {
    void registerListener(IBatteryPropertiesListener iBatteryPropertiesListener) throws RemoteException;

    void unregisterListener(IBatteryPropertiesListener iBatteryPropertiesListener) throws RemoteException;

    /* loaded from: IBatteryPropertiesRegistrar$Stub.class */
    public static abstract class Stub extends Binder implements IBatteryPropertiesRegistrar {
        private static final String DESCRIPTOR = "android.os.IBatteryPropertiesRegistrar";
        static final int TRANSACTION_registerListener = 1;
        static final int TRANSACTION_unregisterListener = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBatteryPropertiesRegistrar asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBatteryPropertiesRegistrar)) {
                return (IBatteryPropertiesRegistrar) iin;
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
                    IBatteryPropertiesListener _arg0 = IBatteryPropertiesListener.Stub.asInterface(data.readStrongBinder());
                    registerListener(_arg0);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IBatteryPropertiesListener _arg02 = IBatteryPropertiesListener.Stub.asInterface(data.readStrongBinder());
                    unregisterListener(_arg02);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IBatteryPropertiesRegistrar$Stub$Proxy.class */
        private static class Proxy implements IBatteryPropertiesRegistrar {
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

            @Override // android.os.IBatteryPropertiesRegistrar
            public void registerListener(IBatteryPropertiesListener listener) throws RemoteException {
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

            @Override // android.os.IBatteryPropertiesRegistrar
            public void unregisterListener(IBatteryPropertiesListener listener) throws RemoteException {
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