package android.content;

import android.content.ISyncContext;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IAnonymousSyncAdapter.class */
public interface IAnonymousSyncAdapter extends IInterface {
    void startSync(ISyncContext iSyncContext, Bundle bundle) throws RemoteException;

    void cancelSync(ISyncContext iSyncContext) throws RemoteException;

    /* loaded from: IAnonymousSyncAdapter$Stub.class */
    public static abstract class Stub extends Binder implements IAnonymousSyncAdapter {
        private static final String DESCRIPTOR = "android.content.IAnonymousSyncAdapter";
        static final int TRANSACTION_startSync = 1;
        static final int TRANSACTION_cancelSync = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAnonymousSyncAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAnonymousSyncAdapter)) {
                return (IAnonymousSyncAdapter) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg1;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    ISyncContext _arg0 = ISyncContext.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg1 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    startSync(_arg0, _arg1);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    ISyncContext _arg02 = ISyncContext.Stub.asInterface(data.readStrongBinder());
                    cancelSync(_arg02);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IAnonymousSyncAdapter$Stub$Proxy.class */
        private static class Proxy implements IAnonymousSyncAdapter {
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

            @Override // android.content.IAnonymousSyncAdapter
            public void startSync(ISyncContext syncContext, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(syncContext != null ? syncContext.asBinder() : null);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
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

            @Override // android.content.IAnonymousSyncAdapter
            public void cancelSync(ISyncContext syncContext) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(syncContext != null ? syncContext.asBinder() : null);
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