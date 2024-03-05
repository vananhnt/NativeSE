package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: INetworkStatsSession.class */
public interface INetworkStatsSession extends IInterface {
    NetworkStats getSummaryForNetwork(NetworkTemplate networkTemplate, long j, long j2) throws RemoteException;

    NetworkStatsHistory getHistoryForNetwork(NetworkTemplate networkTemplate, int i) throws RemoteException;

    NetworkStats getSummaryForAllUid(NetworkTemplate networkTemplate, long j, long j2, boolean z) throws RemoteException;

    NetworkStatsHistory getHistoryForUid(NetworkTemplate networkTemplate, int i, int i2, int i3, int i4) throws RemoteException;

    void close() throws RemoteException;

    /* loaded from: INetworkStatsSession$Stub.class */
    public static abstract class Stub extends Binder implements INetworkStatsSession {
        private static final String DESCRIPTOR = "android.net.INetworkStatsSession";
        static final int TRANSACTION_getSummaryForNetwork = 1;
        static final int TRANSACTION_getHistoryForNetwork = 2;
        static final int TRANSACTION_getSummaryForAllUid = 3;
        static final int TRANSACTION_getHistoryForUid = 4;
        static final int TRANSACTION_close = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkStatsSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INetworkStatsSession)) {
                return (INetworkStatsSession) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            NetworkTemplate _arg0;
            NetworkTemplate _arg02;
            NetworkTemplate _arg03;
            NetworkTemplate _arg04;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = NetworkTemplate.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    long _arg1 = data.readLong();
                    long _arg2 = data.readLong();
                    NetworkStats _result = getSummaryForNetwork(_arg04, _arg1, _arg2);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = NetworkTemplate.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    int _arg12 = data.readInt();
                    NetworkStatsHistory _result2 = getHistoryForNetwork(_arg03, _arg12);
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
                    if (0 != data.readInt()) {
                        _arg02 = NetworkTemplate.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    long _arg13 = data.readLong();
                    long _arg22 = data.readLong();
                    boolean _arg3 = 0 != data.readInt();
                    NetworkStats _result3 = getSummaryForAllUid(_arg02, _arg13, _arg22, _arg3);
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(1);
                        _result3.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = NetworkTemplate.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg14 = data.readInt();
                    int _arg23 = data.readInt();
                    int _arg32 = data.readInt();
                    int _arg4 = data.readInt();
                    NetworkStatsHistory _result4 = getHistoryForUid(_arg0, _arg14, _arg23, _arg32, _arg4);
                    reply.writeNoException();
                    if (_result4 != null) {
                        reply.writeInt(1);
                        _result4.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    close();
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: INetworkStatsSession$Stub$Proxy.class */
        private static class Proxy implements INetworkStatsSession {
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

            @Override // android.net.INetworkStatsSession
            public NetworkStats getSummaryForNetwork(NetworkTemplate template, long start, long end) throws RemoteException {
                NetworkStats _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (template != null) {
                        _data.writeInt(1);
                        template.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeLong(start);
                    _data.writeLong(end);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkStats.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkStatsSession
            public NetworkStatsHistory getHistoryForNetwork(NetworkTemplate template, int fields) throws RemoteException {
                NetworkStatsHistory _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (template != null) {
                        _data.writeInt(1);
                        template.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(fields);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkStatsHistory.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkStatsSession
            public NetworkStats getSummaryForAllUid(NetworkTemplate template, long start, long end, boolean includeTags) throws RemoteException {
                NetworkStats _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (template != null) {
                        _data.writeInt(1);
                        template.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeLong(start);
                    _data.writeLong(end);
                    _data.writeInt(includeTags ? 1 : 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkStats.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkStatsSession
            public NetworkStatsHistory getHistoryForUid(NetworkTemplate template, int uid, int set, int tag, int fields) throws RemoteException {
                NetworkStatsHistory _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (template != null) {
                        _data.writeInt(1);
                        template.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(uid);
                    _data.writeInt(set);
                    _data.writeInt(tag);
                    _data.writeInt(fields);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkStatsHistory.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkStatsSession
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
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