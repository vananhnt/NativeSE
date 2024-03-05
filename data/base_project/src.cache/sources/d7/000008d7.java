package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: INetworkPolicyListener.class */
public interface INetworkPolicyListener extends IInterface {
    void onUidRulesChanged(int i, int i2) throws RemoteException;

    void onMeteredIfacesChanged(String[] strArr) throws RemoteException;

    void onRestrictBackgroundChanged(boolean z) throws RemoteException;

    /* loaded from: INetworkPolicyListener$Stub.class */
    public static abstract class Stub extends Binder implements INetworkPolicyListener {
        private static final String DESCRIPTOR = "android.net.INetworkPolicyListener";
        static final int TRANSACTION_onUidRulesChanged = 1;
        static final int TRANSACTION_onMeteredIfacesChanged = 2;
        static final int TRANSACTION_onRestrictBackgroundChanged = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkPolicyListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INetworkPolicyListener)) {
                return (INetworkPolicyListener) iin;
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
                    int _arg0 = data.readInt();
                    int _arg1 = data.readInt();
                    onUidRulesChanged(_arg0, _arg1);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg02 = data.createStringArray();
                    onMeteredIfacesChanged(_arg02);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg03 = 0 != data.readInt();
                    onRestrictBackgroundChanged(_arg03);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: INetworkPolicyListener$Stub$Proxy.class */
        private static class Proxy implements INetworkPolicyListener {
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

            @Override // android.net.INetworkPolicyListener
            public void onUidRulesChanged(int uid, int uidRules) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(uidRules);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.INetworkPolicyListener
            public void onMeteredIfacesChanged(String[] meteredIfaces) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(meteredIfaces);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.INetworkPolicyListener
            public void onRestrictBackgroundChanged(boolean restrictBackground) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(restrictBackground ? 1 : 0);
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