package android.app;

import android.content.ComponentName;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IInstrumentationWatcher.class */
public interface IInstrumentationWatcher extends IInterface {
    void instrumentationStatus(ComponentName componentName, int i, Bundle bundle) throws RemoteException;

    void instrumentationFinished(ComponentName componentName, int i, Bundle bundle) throws RemoteException;

    /* loaded from: IInstrumentationWatcher$Stub.class */
    public static abstract class Stub extends Binder implements IInstrumentationWatcher {
        private static final String DESCRIPTOR = "android.app.IInstrumentationWatcher";
        static final int TRANSACTION_instrumentationStatus = 1;
        static final int TRANSACTION_instrumentationFinished = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInstrumentationWatcher asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IInstrumentationWatcher)) {
                return (IInstrumentationWatcher) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ComponentName _arg0;
            Bundle _arg2;
            ComponentName _arg02;
            Bundle _arg22;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _arg1 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg22 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    instrumentationStatus(_arg02, _arg1, _arg22);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg12 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg2 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    instrumentationFinished(_arg0, _arg12, _arg2);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IInstrumentationWatcher$Stub$Proxy.class */
        private static class Proxy implements IInstrumentationWatcher {
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

            @Override // android.app.IInstrumentationWatcher
            public void instrumentationStatus(ComponentName name, int resultCode, Bundle results) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (name != null) {
                        _data.writeInt(1);
                        name.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(resultCode);
                    if (results != null) {
                        _data.writeInt(1);
                        results.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.app.IInstrumentationWatcher
            public void instrumentationFinished(ComponentName name, int resultCode, Bundle results) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (name != null) {
                        _data.writeInt(1);
                        name.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(resultCode);
                    if (results != null) {
                        _data.writeInt(1);
                        results.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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