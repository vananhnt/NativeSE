package android.os.storage;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IObbActionListener.class */
public interface IObbActionListener extends IInterface {
    void onObbResult(String str, int i, int i2) throws RemoteException;

    /* loaded from: IObbActionListener$Stub.class */
    public static abstract class Stub extends Binder implements IObbActionListener {
        private static final String DESCRIPTOR = "IObbActionListener";
        static final int TRANSACTION_onObbResult = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IObbActionListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IObbActionListener)) {
                return (IObbActionListener) iin;
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
                    String filename = data.readString();
                    int nonce = data.readInt();
                    int status = data.readInt();
                    onObbResult(filename, nonce, status);
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
        /* loaded from: IObbActionListener$Stub$Proxy.class */
        public static class Proxy implements IObbActionListener {
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

            @Override // android.os.storage.IObbActionListener
            public void onObbResult(String filename, int nonce, int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(filename);
                    _data.writeInt(nonce);
                    _data.writeInt(status);
                    this.mRemote.transact(1, _data, _reply, 1);
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