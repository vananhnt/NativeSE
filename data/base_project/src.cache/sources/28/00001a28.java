package com.android.internal.policy;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.internal.policy.IFaceLockCallback;

/* loaded from: IFaceLockInterface.class */
public interface IFaceLockInterface extends IInterface {
    void startUi(IBinder iBinder, int i, int i2, int i3, int i4, boolean z) throws RemoteException;

    void stopUi() throws RemoteException;

    void registerCallback(IFaceLockCallback iFaceLockCallback) throws RemoteException;

    void unregisterCallback(IFaceLockCallback iFaceLockCallback) throws RemoteException;

    /* loaded from: IFaceLockInterface$Stub.class */
    public static abstract class Stub extends Binder implements IFaceLockInterface {
        private static final String DESCRIPTOR = "com.android.internal.policy.IFaceLockInterface";
        static final int TRANSACTION_startUi = 1;
        static final int TRANSACTION_stopUi = 2;
        static final int TRANSACTION_registerCallback = 3;
        static final int TRANSACTION_unregisterCallback = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IFaceLockInterface asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IFaceLockInterface)) {
                return (IFaceLockInterface) iin;
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
                    IBinder _arg0 = data.readStrongBinder();
                    int _arg1 = data.readInt();
                    int _arg2 = data.readInt();
                    int _arg3 = data.readInt();
                    int _arg4 = data.readInt();
                    boolean _arg5 = 0 != data.readInt();
                    startUi(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    stopUi();
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    IFaceLockCallback _arg02 = IFaceLockCallback.Stub.asInterface(data.readStrongBinder());
                    registerCallback(_arg02);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IFaceLockCallback _arg03 = IFaceLockCallback.Stub.asInterface(data.readStrongBinder());
                    unregisterCallback(_arg03);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IFaceLockInterface$Stub$Proxy.class */
        private static class Proxy implements IFaceLockInterface {
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

            @Override // com.android.internal.policy.IFaceLockInterface
            public void startUi(IBinder containingWindowToken, int x, int y, int width, int height, boolean useLiveliness) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(containingWindowToken);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeInt(useLiveliness ? 1 : 0);
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

            @Override // com.android.internal.policy.IFaceLockInterface
            public void stopUi() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.policy.IFaceLockInterface
            public void registerCallback(IFaceLockCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IFaceLockInterface
            public void unregisterCallback(IFaceLockCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(4, _data, _reply, 0);
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