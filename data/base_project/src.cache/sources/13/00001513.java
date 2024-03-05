package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.IWindowFocusObserver;

/* loaded from: IWindowId.class */
public interface IWindowId extends IInterface {
    void registerFocusObserver(IWindowFocusObserver iWindowFocusObserver) throws RemoteException;

    void unregisterFocusObserver(IWindowFocusObserver iWindowFocusObserver) throws RemoteException;

    boolean isFocused() throws RemoteException;

    /* loaded from: IWindowId$Stub.class */
    public static abstract class Stub extends Binder implements IWindowId {
        private static final String DESCRIPTOR = "android.view.IWindowId";
        static final int TRANSACTION_registerFocusObserver = 1;
        static final int TRANSACTION_unregisterFocusObserver = 2;
        static final int TRANSACTION_isFocused = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWindowId asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWindowId)) {
                return (IWindowId) iin;
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
                    IWindowFocusObserver _arg0 = IWindowFocusObserver.Stub.asInterface(data.readStrongBinder());
                    registerFocusObserver(_arg0);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IWindowFocusObserver _arg02 = IWindowFocusObserver.Stub.asInterface(data.readStrongBinder());
                    unregisterFocusObserver(_arg02);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result = isFocused();
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IWindowId$Stub$Proxy.class */
        private static class Proxy implements IWindowId {
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

            @Override // android.view.IWindowId
            public void registerFocusObserver(IWindowFocusObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
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

            @Override // android.view.IWindowId
            public void unregisterFocusObserver(IWindowFocusObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
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

            @Override // android.view.IWindowId
            public boolean isFocused() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}