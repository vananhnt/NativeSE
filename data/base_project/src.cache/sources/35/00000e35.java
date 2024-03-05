package android.speech;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.speech.IRecognitionListener;

/* loaded from: IRecognitionService.class */
public interface IRecognitionService extends IInterface {
    void startListening(Intent intent, IRecognitionListener iRecognitionListener) throws RemoteException;

    void stopListening(IRecognitionListener iRecognitionListener) throws RemoteException;

    void cancel(IRecognitionListener iRecognitionListener) throws RemoteException;

    /* loaded from: IRecognitionService$Stub.class */
    public static abstract class Stub extends Binder implements IRecognitionService {
        private static final String DESCRIPTOR = "android.speech.IRecognitionService";
        static final int TRANSACTION_startListening = 1;
        static final int TRANSACTION_stopListening = 2;
        static final int TRANSACTION_cancel = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRecognitionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRecognitionService)) {
                return (IRecognitionService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Intent _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    IRecognitionListener _arg1 = IRecognitionListener.Stub.asInterface(data.readStrongBinder());
                    startListening(_arg0, _arg1);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IRecognitionListener _arg02 = IRecognitionListener.Stub.asInterface(data.readStrongBinder());
                    stopListening(_arg02);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    IRecognitionListener _arg03 = IRecognitionListener.Stub.asInterface(data.readStrongBinder());
                    cancel(_arg03);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IRecognitionService$Stub$Proxy.class */
        private static class Proxy implements IRecognitionService {
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

            @Override // android.speech.IRecognitionService
            public void startListening(Intent recognizerIntent, IRecognitionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (recognizerIntent != null) {
                        _data.writeInt(1);
                        recognizerIntent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.speech.IRecognitionService
            public void stopListening(IRecognitionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.speech.IRecognitionService
            public void cancel(IRecognitionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}