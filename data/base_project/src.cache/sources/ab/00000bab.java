package android.os.storage;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IMountServiceListener.class */
public interface IMountServiceListener extends IInterface {
    void onUsbMassStorageConnectionChanged(boolean z) throws RemoteException;

    void onStorageStateChanged(String str, String str2, String str3) throws RemoteException;

    /* loaded from: IMountServiceListener$Stub.class */
    public static abstract class Stub extends Binder implements IMountServiceListener {
        private static final String DESCRIPTOR = "IMountServiceListener";
        static final int TRANSACTION_onUsbMassStorageConnectionChanged = 1;
        static final int TRANSACTION_onStorageStateChanged = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMountServiceListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMountServiceListener)) {
                return (IMountServiceListener) iin;
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
                    boolean connected = 0 != data.readInt();
                    onUsbMassStorageConnectionChanged(connected);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String path = data.readString();
                    String oldState = data.readString();
                    String newState = data.readString();
                    onStorageStateChanged(path, oldState, newState);
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
        /* loaded from: IMountServiceListener$Stub$Proxy.class */
        public static class Proxy implements IMountServiceListener {
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

            @Override // android.os.storage.IMountServiceListener
            public void onUsbMassStorageConnectionChanged(boolean connected) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(connected ? 1 : 0);
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

            @Override // android.os.storage.IMountServiceListener
            public void onStorageStateChanged(String path, String oldState, String newState) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    _data.writeString(oldState);
                    _data.writeString(newState);
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