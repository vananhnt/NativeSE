package android.print;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* loaded from: IPrintSpoolerCallbacks.class */
public interface IPrintSpoolerCallbacks extends IInterface {
    void onGetPrintJobInfosResult(List<PrintJobInfo> list, int i) throws RemoteException;

    void onCancelPrintJobResult(boolean z, int i) throws RemoteException;

    void onSetPrintJobStateResult(boolean z, int i) throws RemoteException;

    void onSetPrintJobTagResult(boolean z, int i) throws RemoteException;

    void onGetPrintJobInfoResult(PrintJobInfo printJobInfo, int i) throws RemoteException;

    /* loaded from: IPrintSpoolerCallbacks$Stub.class */
    public static abstract class Stub extends Binder implements IPrintSpoolerCallbacks {
        private static final String DESCRIPTOR = "android.print.IPrintSpoolerCallbacks";
        static final int TRANSACTION_onGetPrintJobInfosResult = 1;
        static final int TRANSACTION_onCancelPrintJobResult = 2;
        static final int TRANSACTION_onSetPrintJobStateResult = 3;
        static final int TRANSACTION_onSetPrintJobTagResult = 4;
        static final int TRANSACTION_onGetPrintJobInfoResult = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPrintSpoolerCallbacks asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPrintSpoolerCallbacks)) {
                return (IPrintSpoolerCallbacks) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            PrintJobInfo _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    List<PrintJobInfo> _arg02 = data.createTypedArrayList(PrintJobInfo.CREATOR);
                    int _arg1 = data.readInt();
                    onGetPrintJobInfosResult(_arg02, _arg1);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg03 = 0 != data.readInt();
                    int _arg12 = data.readInt();
                    onCancelPrintJobResult(_arg03, _arg12);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg04 = 0 != data.readInt();
                    int _arg13 = data.readInt();
                    onSetPrintJobStateResult(_arg04, _arg13);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg05 = 0 != data.readInt();
                    int _arg14 = data.readInt();
                    onSetPrintJobTagResult(_arg05, _arg14);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = PrintJobInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg15 = data.readInt();
                    onGetPrintJobInfoResult(_arg0, _arg15);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IPrintSpoolerCallbacks$Stub$Proxy.class */
        private static class Proxy implements IPrintSpoolerCallbacks {
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

            @Override // android.print.IPrintSpoolerCallbacks
            public void onGetPrintJobInfosResult(List<PrintJobInfo> printJob, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(printJob);
                    _data.writeInt(sequence);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.print.IPrintSpoolerCallbacks
            public void onCancelPrintJobResult(boolean canceled, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(canceled ? 1 : 0);
                    _data.writeInt(sequence);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.print.IPrintSpoolerCallbacks
            public void onSetPrintJobStateResult(boolean success, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(success ? 1 : 0);
                    _data.writeInt(sequence);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.print.IPrintSpoolerCallbacks
            public void onSetPrintJobTagResult(boolean success, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(success ? 1 : 0);
                    _data.writeInt(sequence);
                    this.mRemote.transact(4, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.print.IPrintSpoolerCallbacks
            public void onGetPrintJobInfoResult(PrintJobInfo printJob, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printJob != null) {
                        _data.writeInt(1);
                        printJob.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(sequence);
                    this.mRemote.transact(5, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}