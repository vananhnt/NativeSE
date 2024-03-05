package android.net;

import android.net.INetworkPolicyListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: INetworkPolicyManager.class */
public interface INetworkPolicyManager extends IInterface {
    void setUidPolicy(int i, int i2) throws RemoteException;

    int getUidPolicy(int i) throws RemoteException;

    int[] getUidsWithPolicy(int i) throws RemoteException;

    boolean isUidForeground(int i) throws RemoteException;

    void registerListener(INetworkPolicyListener iNetworkPolicyListener) throws RemoteException;

    void unregisterListener(INetworkPolicyListener iNetworkPolicyListener) throws RemoteException;

    void setNetworkPolicies(NetworkPolicy[] networkPolicyArr) throws RemoteException;

    NetworkPolicy[] getNetworkPolicies() throws RemoteException;

    void snoozeLimit(NetworkTemplate networkTemplate) throws RemoteException;

    void setRestrictBackground(boolean z) throws RemoteException;

    boolean getRestrictBackground() throws RemoteException;

    NetworkQuotaInfo getNetworkQuotaInfo(NetworkState networkState) throws RemoteException;

    boolean isNetworkMetered(NetworkState networkState) throws RemoteException;

    /* loaded from: INetworkPolicyManager$Stub.class */
    public static abstract class Stub extends Binder implements INetworkPolicyManager {
        private static final String DESCRIPTOR = "android.net.INetworkPolicyManager";
        static final int TRANSACTION_setUidPolicy = 1;
        static final int TRANSACTION_getUidPolicy = 2;
        static final int TRANSACTION_getUidsWithPolicy = 3;
        static final int TRANSACTION_isUidForeground = 4;
        static final int TRANSACTION_registerListener = 5;
        static final int TRANSACTION_unregisterListener = 6;
        static final int TRANSACTION_setNetworkPolicies = 7;
        static final int TRANSACTION_getNetworkPolicies = 8;
        static final int TRANSACTION_snoozeLimit = 9;
        static final int TRANSACTION_setRestrictBackground = 10;
        static final int TRANSACTION_getRestrictBackground = 11;
        static final int TRANSACTION_getNetworkQuotaInfo = 12;
        static final int TRANSACTION_isNetworkMetered = 13;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkPolicyManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INetworkPolicyManager)) {
                return (INetworkPolicyManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            NetworkState _arg0;
            NetworkState _arg02;
            NetworkTemplate _arg03;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    int _arg1 = data.readInt();
                    setUidPolicy(_arg04, _arg1);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    int _result = getUidPolicy(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    int[] _result2 = getUidsWithPolicy(_arg06);
                    reply.writeNoException();
                    reply.writeIntArray(_result2);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    boolean _result3 = isUidForeground(_arg07);
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    INetworkPolicyListener _arg08 = INetworkPolicyListener.Stub.asInterface(data.readStrongBinder());
                    registerListener(_arg08);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    INetworkPolicyListener _arg09 = INetworkPolicyListener.Stub.asInterface(data.readStrongBinder());
                    unregisterListener(_arg09);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkPolicy[] _arg010 = (NetworkPolicy[]) data.createTypedArray(NetworkPolicy.CREATOR);
                    setNetworkPolicies(_arg010);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkPolicy[] _result4 = getNetworkPolicies();
                    reply.writeNoException();
                    reply.writeTypedArray(_result4, 1);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = NetworkTemplate.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    snoozeLimit(_arg03);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg011 = 0 != data.readInt();
                    setRestrictBackground(_arg011);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result5 = getRestrictBackground();
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = NetworkState.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    NetworkQuotaInfo _result6 = getNetworkQuotaInfo(_arg02);
                    reply.writeNoException();
                    if (_result6 != null) {
                        reply.writeInt(1);
                        _result6.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = NetworkState.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    boolean _result7 = isNetworkMetered(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result7 ? 1 : 0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: INetworkPolicyManager$Stub$Proxy.class */
        private static class Proxy implements INetworkPolicyManager {
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

            @Override // android.net.INetworkPolicyManager
            public void setUidPolicy(int uid, int policy) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(policy);
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

            @Override // android.net.INetworkPolicyManager
            public int getUidPolicy(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(2, _data, _reply, 0);
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

            @Override // android.net.INetworkPolicyManager
            public int[] getUidsWithPolicy(int policy) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(policy);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.INetworkPolicyManager
            public boolean isUidForeground(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkPolicyManager
            public void registerListener(INetworkPolicyListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
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

            @Override // android.net.INetworkPolicyManager
            public void unregisterListener(INetworkPolicyListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.INetworkPolicyManager
            public void setNetworkPolicies(NetworkPolicy[] policies) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(policies, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.INetworkPolicyManager
            public NetworkPolicy[] getNetworkPolicies() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    NetworkPolicy[] _result = (NetworkPolicy[]) _reply.createTypedArray(NetworkPolicy.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.INetworkPolicyManager
            public void snoozeLimit(NetworkTemplate template) throws RemoteException {
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
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.INetworkPolicyManager
            public void setRestrictBackground(boolean restrictBackground) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(restrictBackground ? 1 : 0);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.INetworkPolicyManager
            public boolean getRestrictBackground() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkPolicyManager
            public NetworkQuotaInfo getNetworkQuotaInfo(NetworkState state) throws RemoteException {
                NetworkQuotaInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (state != null) {
                        _data.writeInt(1);
                        state.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkQuotaInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkPolicyManager
            public boolean isNetworkMetered(NetworkState state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (state != null) {
                        _data.writeInt(1);
                        state.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}