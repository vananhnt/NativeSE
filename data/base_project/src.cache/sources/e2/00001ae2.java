package com.android.internal.textservice;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.internal.textservice.ISpellCheckerSession;
import com.android.internal.textservice.ISpellCheckerSessionListener;

/* loaded from: ISpellCheckerService.class */
public interface ISpellCheckerService extends IInterface {
    ISpellCheckerSession getISpellCheckerSession(String str, ISpellCheckerSessionListener iSpellCheckerSessionListener, Bundle bundle) throws RemoteException;

    /* loaded from: ISpellCheckerService$Stub.class */
    public static abstract class Stub extends Binder implements ISpellCheckerService {
        private static final String DESCRIPTOR = "com.android.internal.textservice.ISpellCheckerService";
        static final int TRANSACTION_getISpellCheckerSession = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISpellCheckerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISpellCheckerService)) {
                return (ISpellCheckerService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg2;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    ISpellCheckerSessionListener _arg1 = ISpellCheckerSessionListener.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg2 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    ISpellCheckerSession _result = getISpellCheckerSession(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeStrongBinder(_result != null ? _result.asBinder() : null);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: ISpellCheckerService$Stub$Proxy.class */
        private static class Proxy implements ISpellCheckerService {
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

            @Override // com.android.internal.textservice.ISpellCheckerService
            public ISpellCheckerSession getISpellCheckerSession(String locale, ISpellCheckerSessionListener listener, Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(locale);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (bundle != null) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ISpellCheckerSession _result = ISpellCheckerSession.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}