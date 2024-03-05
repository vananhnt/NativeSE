package android.print;

import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IPrinterDiscoveryObserver.class */
public interface IPrinterDiscoveryObserver extends IInterface {
    void onPrintersAdded(ParceledListSlice parceledListSlice) throws RemoteException;

    void onPrintersRemoved(ParceledListSlice parceledListSlice) throws RemoteException;

    /* loaded from: IPrinterDiscoveryObserver$Stub.class */
    public static abstract class Stub extends Binder implements IPrinterDiscoveryObserver {
        private static final String DESCRIPTOR = "android.print.IPrinterDiscoveryObserver";
        static final int TRANSACTION_onPrintersAdded = 1;
        static final int TRANSACTION_onPrintersRemoved = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPrinterDiscoveryObserver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPrinterDiscoveryObserver)) {
                return (IPrinterDiscoveryObserver) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ParceledListSlice _arg0;
            ParceledListSlice _arg02;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = ParceledListSlice.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    onPrintersAdded(_arg02);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = ParceledListSlice.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onPrintersRemoved(_arg0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IPrinterDiscoveryObserver$Stub$Proxy.class */
        public static class Proxy implements IPrinterDiscoveryObserver {
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

            @Override // android.print.IPrinterDiscoveryObserver
            public void onPrintersAdded(ParceledListSlice printers) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printers != null) {
                        _data.writeInt(1);
                        printers.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.print.IPrinterDiscoveryObserver
            public void onPrintersRemoved(ParceledListSlice printerIds) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printerIds != null) {
                        _data.writeInt(1);
                        printerIds.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}