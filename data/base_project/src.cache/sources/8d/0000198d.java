package com.android.internal.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: IProcessStats.class */
public interface IProcessStats extends IInterface {
    byte[] getCurrentStats(List<ParcelFileDescriptor> list) throws RemoteException;

    ParcelFileDescriptor getStatsOverTime(long j) throws RemoteException;

    int getCurrentMemoryState() throws RemoteException;

    /* loaded from: IProcessStats$Stub.class */
    public static abstract class Stub extends Binder implements IProcessStats {
        private static final String DESCRIPTOR = "com.android.internal.app.IProcessStats";
        static final int TRANSACTION_getCurrentStats = 1;
        static final int TRANSACTION_getStatsOverTime = 2;
        static final int TRANSACTION_getCurrentMemoryState = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IProcessStats asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IProcessStats)) {
                return (IProcessStats) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    ArrayList arrayList = new ArrayList();
                    byte[] _result = getCurrentStats(arrayList);
                    reply.writeNoException();
                    reply.writeByteArray(_result);
                    reply.writeTypedList(arrayList);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg0 = data.readLong();
                    ParcelFileDescriptor _result2 = getStatsOverTime(_arg0);
                    reply.writeNoException();
                    if (_result2 != null) {
                        reply.writeInt(1);
                        _result2.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _result3 = getCurrentMemoryState();
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IProcessStats$Stub$Proxy.class */
        private static class Proxy implements IProcessStats {
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

            @Override // com.android.internal.app.IProcessStats
            public byte[] getCurrentStats(List<ParcelFileDescriptor> historic) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    _reply.readTypedList(historic, ParcelFileDescriptor.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IProcessStats
            public ParcelFileDescriptor getStatsOverTime(long minTime) throws RemoteException {
                ParcelFileDescriptor _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(minTime);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ParcelFileDescriptor.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IProcessStats
            public int getCurrentMemoryState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
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