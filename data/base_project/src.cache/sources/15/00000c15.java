package android.print;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.print.ILayoutResultCallback;
import android.print.IWriteResultCallback;

/* loaded from: IPrintDocumentAdapter.class */
public interface IPrintDocumentAdapter extends IInterface {
    void start() throws RemoteException;

    void layout(PrintAttributes printAttributes, PrintAttributes printAttributes2, ILayoutResultCallback iLayoutResultCallback, Bundle bundle, int i) throws RemoteException;

    void write(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, IWriteResultCallback iWriteResultCallback, int i) throws RemoteException;

    void finish() throws RemoteException;

    /* loaded from: IPrintDocumentAdapter$Stub.class */
    public static abstract class Stub extends Binder implements IPrintDocumentAdapter {
        private static final String DESCRIPTOR = "android.print.IPrintDocumentAdapter";
        static final int TRANSACTION_start = 1;
        static final int TRANSACTION_layout = 2;
        static final int TRANSACTION_write = 3;
        static final int TRANSACTION_finish = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPrintDocumentAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPrintDocumentAdapter)) {
                return (IPrintDocumentAdapter) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ParcelFileDescriptor _arg1;
            PrintAttributes _arg0;
            PrintAttributes _arg12;
            Bundle _arg3;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    start();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = PrintAttributes.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg12 = PrintAttributes.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    ILayoutResultCallback _arg2 = ILayoutResultCallback.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg3 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    int _arg4 = data.readInt();
                    layout(_arg0, _arg12, _arg2, _arg3, _arg4);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    PageRange[] _arg02 = (PageRange[]) data.createTypedArray(PageRange.CREATOR);
                    if (0 != data.readInt()) {
                        _arg1 = ParcelFileDescriptor.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    IWriteResultCallback _arg22 = IWriteResultCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg32 = data.readInt();
                    write(_arg02, _arg1, _arg22, _arg32);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    finish();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IPrintDocumentAdapter$Stub$Proxy.class */
        private static class Proxy implements IPrintDocumentAdapter {
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

            @Override // android.print.IPrintDocumentAdapter
            public void start() throws RemoteException {
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

            @Override // android.print.IPrintDocumentAdapter
            public void layout(PrintAttributes oldAttributes, PrintAttributes newAttributes, ILayoutResultCallback callback, Bundle metadata, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (oldAttributes != null) {
                        _data.writeInt(1);
                        oldAttributes.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (newAttributes != null) {
                        _data.writeInt(1);
                        newAttributes.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (metadata != null) {
                        _data.writeInt(1);
                        metadata.writeToParcel(_data, 0);
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

            @Override // android.print.IPrintDocumentAdapter
            public void write(PageRange[] pages, ParcelFileDescriptor fd, IWriteResultCallback callback, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(pages, 0);
                    if (fd != null) {
                        _data.writeInt(1);
                        fd.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(sequence);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.print.IPrintDocumentAdapter
            public void finish() throws RemoteException {
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
        }
    }
}