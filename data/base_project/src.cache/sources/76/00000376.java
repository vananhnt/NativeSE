package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IPackageStatsObserver.class */
public interface IPackageStatsObserver extends IInterface {
    void onGetStatsCompleted(PackageStats packageStats, boolean z) throws RemoteException;

    /* loaded from: IPackageStatsObserver$Stub.class */
    public static abstract class Stub extends Binder implements IPackageStatsObserver {
        private static final String DESCRIPTOR = "android.content.pm.IPackageStatsObserver";
        static final int TRANSACTION_onGetStatsCompleted = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPackageStatsObserver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPackageStatsObserver)) {
                return (IPackageStatsObserver) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            PackageStats _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = PackageStats.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    boolean _arg1 = 0 != data.readInt();
                    onGetStatsCompleted(_arg0, _arg1);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IPackageStatsObserver$Stub$Proxy.class */
        public static class Proxy implements IPackageStatsObserver {
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

            @Override // android.content.pm.IPackageStatsObserver
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pStats != null) {
                        _data.writeInt(1);
                        pStats.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(succeeded ? 1 : 0);
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