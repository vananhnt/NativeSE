package android.database;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IContentObserver.class */
public interface IContentObserver extends IInterface {
    void onChange(boolean z, Uri uri) throws RemoteException;

    /* loaded from: IContentObserver$Stub.class */
    public static abstract class Stub extends Binder implements IContentObserver {
        private static final String DESCRIPTOR = "android.database.IContentObserver";
        static final int TRANSACTION_onChange = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IContentObserver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IContentObserver)) {
                return (IContentObserver) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Uri _arg1;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg0 = 0 != data.readInt();
                    if (0 != data.readInt()) {
                        _arg1 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    onChange(_arg0, _arg1);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IContentObserver$Stub$Proxy.class */
        private static class Proxy implements IContentObserver {
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

            @Override // android.database.IContentObserver
            public void onChange(boolean selfUpdate, Uri uri) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(selfUpdate ? 1 : 0);
                    if (uri != null) {
                        _data.writeInt(1);
                        uri.writeToParcel(_data, 0);
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
        }
    }
}