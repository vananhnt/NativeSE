package android.app.backup;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IFullBackupRestoreObserver.class */
public interface IFullBackupRestoreObserver extends IInterface {
    void onStartBackup() throws RemoteException;

    void onBackupPackage(String str) throws RemoteException;

    void onEndBackup() throws RemoteException;

    void onStartRestore() throws RemoteException;

    void onRestorePackage(String str) throws RemoteException;

    void onEndRestore() throws RemoteException;

    void onTimeout() throws RemoteException;

    /* loaded from: IFullBackupRestoreObserver$Stub.class */
    public static abstract class Stub extends Binder implements IFullBackupRestoreObserver {
        private static final String DESCRIPTOR = "android.app.backup.IFullBackupRestoreObserver";
        static final int TRANSACTION_onStartBackup = 1;
        static final int TRANSACTION_onBackupPackage = 2;
        static final int TRANSACTION_onEndBackup = 3;
        static final int TRANSACTION_onStartRestore = 4;
        static final int TRANSACTION_onRestorePackage = 5;
        static final int TRANSACTION_onEndRestore = 6;
        static final int TRANSACTION_onTimeout = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IFullBackupRestoreObserver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IFullBackupRestoreObserver)) {
                return (IFullBackupRestoreObserver) iin;
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
                    onStartBackup();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    onBackupPackage(_arg0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    onEndBackup();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    onStartRestore();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    onRestorePackage(_arg02);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    onEndRestore();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    onTimeout();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IFullBackupRestoreObserver$Stub$Proxy.class */
        public static class Proxy implements IFullBackupRestoreObserver {
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

            @Override // android.app.backup.IFullBackupRestoreObserver
            public void onStartBackup() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IFullBackupRestoreObserver
            public void onBackupPackage(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IFullBackupRestoreObserver
            public void onEndBackup() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IFullBackupRestoreObserver
            public void onStartRestore() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IFullBackupRestoreObserver
            public void onRestorePackage(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(5, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IFullBackupRestoreObserver
            public void onEndRestore() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IFullBackupRestoreObserver
            public void onTimeout() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}