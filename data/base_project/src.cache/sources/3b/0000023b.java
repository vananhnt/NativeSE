package android.app.backup;

import android.app.backup.IRestoreObserver;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IRestoreSession.class */
public interface IRestoreSession extends IInterface {
    int getAvailableRestoreSets(IRestoreObserver iRestoreObserver) throws RemoteException;

    int restoreAll(long j, IRestoreObserver iRestoreObserver) throws RemoteException;

    int restoreSome(long j, IRestoreObserver iRestoreObserver, String[] strArr) throws RemoteException;

    int restorePackage(String str, IRestoreObserver iRestoreObserver) throws RemoteException;

    void endRestoreSession() throws RemoteException;

    /* loaded from: IRestoreSession$Stub.class */
    public static abstract class Stub extends Binder implements IRestoreSession {
        private static final String DESCRIPTOR = "android.app.backup.IRestoreSession";
        static final int TRANSACTION_getAvailableRestoreSets = 1;
        static final int TRANSACTION_restoreAll = 2;
        static final int TRANSACTION_restoreSome = 3;
        static final int TRANSACTION_restorePackage = 4;
        static final int TRANSACTION_endRestoreSession = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRestoreSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRestoreSession)) {
                return (IRestoreSession) iin;
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
                    IRestoreObserver _arg0 = IRestoreObserver.Stub.asInterface(data.readStrongBinder());
                    int _result = getAvailableRestoreSets(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg02 = data.readLong();
                    IRestoreObserver _arg1 = IRestoreObserver.Stub.asInterface(data.readStrongBinder());
                    int _result2 = restoreAll(_arg02, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg03 = data.readLong();
                    IRestoreObserver _arg12 = IRestoreObserver.Stub.asInterface(data.readStrongBinder());
                    String[] _arg2 = data.createStringArray();
                    int _result3 = restoreSome(_arg03, _arg12, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    IRestoreObserver _arg13 = IRestoreObserver.Stub.asInterface(data.readStrongBinder());
                    int _result4 = restorePackage(_arg04, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    endRestoreSession();
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IRestoreSession$Stub$Proxy.class */
        public static class Proxy implements IRestoreSession {
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

            @Override // android.app.backup.IRestoreSession
            public int getAvailableRestoreSets(IRestoreObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IRestoreSession
            public int restoreAll(long token, IRestoreObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(token);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IRestoreSession
            public int restoreSome(long token, IRestoreObserver observer, String[] packages) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(token);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeStringArray(packages);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IRestoreSession
            public int restorePackage(String packageName, IRestoreObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IRestoreSession
            public void endRestoreSession() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
        }
    }
}