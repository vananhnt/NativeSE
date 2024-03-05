package com.android.internal.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IInputMethodClient.class */
public interface IInputMethodClient extends IInterface {
    void setUsingInputMethod(boolean z) throws RemoteException;

    void onBindMethod(InputBindResult inputBindResult) throws RemoteException;

    void onUnbindMethod(int i) throws RemoteException;

    void setActive(boolean z) throws RemoteException;

    /* loaded from: IInputMethodClient$Stub.class */
    public static abstract class Stub extends Binder implements IInputMethodClient {
        private static final String DESCRIPTOR = "com.android.internal.view.IInputMethodClient";
        static final int TRANSACTION_setUsingInputMethod = 1;
        static final int TRANSACTION_onBindMethod = 2;
        static final int TRANSACTION_onUnbindMethod = 3;
        static final int TRANSACTION_setActive = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInputMethodClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IInputMethodClient)) {
                return (IInputMethodClient) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            InputBindResult _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg02 = 0 != data.readInt();
                    setUsingInputMethod(_arg02);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = InputBindResult.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onBindMethod(_arg0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    onUnbindMethod(_arg03);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg04 = 0 != data.readInt();
                    setActive(_arg04);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IInputMethodClient$Stub$Proxy.class */
        public static class Proxy implements IInputMethodClient {
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

            @Override // com.android.internal.view.IInputMethodClient
            public void setUsingInputMethod(boolean state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state ? 1 : 0);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputMethodClient
            public void onBindMethod(InputBindResult res) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (res != null) {
                        _data.writeInt(1);
                        res.writeToParcel(_data, 0);
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

            @Override // com.android.internal.view.IInputMethodClient
            public void onUnbindMethod(int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sequence);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputMethodClient
            public void setActive(boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(active ? 1 : 0);
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