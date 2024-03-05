package android.location;

import android.hardware.location.IFusedLocationHardware;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IFusedProvider.class */
public interface IFusedProvider extends IInterface {
    void onFusedLocationHardwareChange(IFusedLocationHardware iFusedLocationHardware) throws RemoteException;

    /* loaded from: IFusedProvider$Stub.class */
    public static abstract class Stub extends Binder implements IFusedProvider {
        private static final String DESCRIPTOR = "android.location.IFusedProvider";
        static final int TRANSACTION_onFusedLocationHardwareChange = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IFusedProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IFusedProvider)) {
                return (IFusedProvider) iin;
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
                    IFusedLocationHardware _arg0 = IFusedLocationHardware.Stub.asInterface(data.readStrongBinder());
                    onFusedLocationHardwareChange(_arg0);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IFusedProvider$Stub$Proxy.class */
        private static class Proxy implements IFusedProvider {
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

            @Override // android.location.IFusedProvider
            public void onFusedLocationHardwareChange(IFusedLocationHardware instance) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(instance != null ? instance.asBinder() : null);
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
        }
    }
}