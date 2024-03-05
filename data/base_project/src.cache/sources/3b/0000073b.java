package android.media;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IMediaScannerListener.class */
public interface IMediaScannerListener extends IInterface {
    void scanCompleted(String str, Uri uri) throws RemoteException;

    /* loaded from: IMediaScannerListener$Stub.class */
    public static abstract class Stub extends Binder implements IMediaScannerListener {
        private static final String DESCRIPTOR = "android.media.IMediaScannerListener";
        static final int TRANSACTION_scanCompleted = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaScannerListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaScannerListener)) {
                return (IMediaScannerListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Uri _arg1;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    if (0 != data.readInt()) {
                        _arg1 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    scanCompleted(_arg0, _arg1);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IMediaScannerListener$Stub$Proxy.class */
        private static class Proxy implements IMediaScannerListener {
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

            @Override // android.media.IMediaScannerListener
            public void scanCompleted(String path, Uri uri) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    if (uri != null) {
                        _data.writeInt(1);
                        uri.writeToParcel(_data, 0);
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
        }
    }
}