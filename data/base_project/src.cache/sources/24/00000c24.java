package android.print;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IPrintSpoolerClient.class */
public interface IPrintSpoolerClient extends IInterface {
    void onPrintJobQueued(PrintJobInfo printJobInfo) throws RemoteException;

    void onAllPrintJobsForServiceHandled(ComponentName componentName) throws RemoteException;

    void onAllPrintJobsHandled() throws RemoteException;

    void onPrintJobStateChanged(PrintJobInfo printJobInfo) throws RemoteException;

    /* loaded from: IPrintSpoolerClient$Stub.class */
    public static abstract class Stub extends Binder implements IPrintSpoolerClient {
        private static final String DESCRIPTOR = "android.print.IPrintSpoolerClient";
        static final int TRANSACTION_onPrintJobQueued = 1;
        static final int TRANSACTION_onAllPrintJobsForServiceHandled = 2;
        static final int TRANSACTION_onAllPrintJobsHandled = 3;
        static final int TRANSACTION_onPrintJobStateChanged = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPrintSpoolerClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPrintSpoolerClient)) {
                return (IPrintSpoolerClient) iin;
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
            ComponentName _arg02;
            PrintJobInfo _arg03;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = PrintJobInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    onPrintJobQueued(_arg03);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    onAllPrintJobsForServiceHandled(_arg02);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    onAllPrintJobsHandled();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = PrintJobInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onPrintJobStateChanged(_arg0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IPrintSpoolerClient$Stub$Proxy.class */
        private static class Proxy implements IPrintSpoolerClient {
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

            @Override // android.print.IPrintSpoolerClient
            public void onPrintJobQueued(PrintJobInfo printJob) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printJob != null) {
                        _data.writeInt(1);
                        printJob.writeToParcel(_data, 0);
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

            @Override // android.print.IPrintSpoolerClient
            public void onAllPrintJobsForServiceHandled(ComponentName printService) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printService != null) {
                        _data.writeInt(1);
                        printService.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.print.IPrintSpoolerClient
            public void onAllPrintJobsHandled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.print.IPrintSpoolerClient
            public void onPrintJobStateChanged(PrintJobInfo printJob) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (printJob != null) {
                        _data.writeInt(1);
                        printJob.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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