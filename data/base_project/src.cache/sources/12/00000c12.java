package android.print;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;

/* loaded from: ILayoutResultCallback.class */
public interface ILayoutResultCallback extends IInterface {
    void onLayoutFinished(PrintDocumentInfo printDocumentInfo, boolean z, int i) throws RemoteException;

    void onLayoutFailed(CharSequence charSequence, int i) throws RemoteException;

    /* loaded from: ILayoutResultCallback$Stub.class */
    public static abstract class Stub extends Binder implements ILayoutResultCallback {
        private static final String DESCRIPTOR = "android.print.ILayoutResultCallback";
        static final int TRANSACTION_onLayoutFinished = 1;
        static final int TRANSACTION_onLayoutFailed = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILayoutResultCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ILayoutResultCallback)) {
                return (ILayoutResultCallback) iin;
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
            PrintDocumentInfo _arg02;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = PrintDocumentInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    boolean _arg1 = 0 != data.readInt();
                    int _arg2 = data.readInt();
                    onLayoutFinished(_arg02, _arg1, _arg2);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg12 = data.readInt();
                    onLayoutFailed(_arg0, _arg12);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: ILayoutResultCallback$Stub$Proxy.class */
        private static class Proxy implements ILayoutResultCallback {
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

            @Override // android.print.ILayoutResultCallback
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(changed ? 1 : 0);
                    _data.writeInt(sequence);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.print.ILayoutResultCallback
            public void onLayoutFailed(CharSequence error, int sequence) throws RemoteException {
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