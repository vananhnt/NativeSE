package android.print;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;

/* loaded from: IWriteResultCallback.class */
public interface IWriteResultCallback extends IInterface {
    void onWriteFinished(PageRange[] pageRangeArr, int i) throws RemoteException;

    void onWriteFailed(CharSequence charSequence, int i) throws RemoteException;

    /* loaded from: IWriteResultCallback$Stub.class */
    public static abstract class Stub extends Binder implements IWriteResultCallback {
        private static final String DESCRIPTOR = "android.print.IWriteResultCallback";
        static final int TRANSACTION_onWriteFinished = 1;
        static final int TRANSACTION_onWriteFailed = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWriteResultCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWriteResultCallback)) {
                return (IWriteResultCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            CharSequence _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    PageRange[] _arg02 = (PageRange[]) data.createTypedArray(PageRange.CREATOR);
                    int _arg1 = data.readInt();
                    onWriteFinished(_arg02, _arg1);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg12 = data.readInt();
                    onWriteFailed(_arg0, _arg12);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IWriteResultCallback$Stub$Proxy.class */
        public static class Proxy implements IWriteResultCallback {
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

            @Override // android.print.IWriteResultCallback
            public void onWriteFinished(PageRange[] pages, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(pages, 0);
                    _data.writeInt(sequence);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.print.IWriteResultCallback
            public void onWriteFailed(CharSequence error, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (error != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(error, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(sequence);
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