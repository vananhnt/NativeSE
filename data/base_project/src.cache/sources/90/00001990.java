package com.android.internal.app;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.internal.os.PkgUsageStats;

/* loaded from: IUsageStats.class */
public interface IUsageStats extends IInterface {
    void noteResumeComponent(ComponentName componentName) throws RemoteException;

    void notePauseComponent(ComponentName componentName) throws RemoteException;

    void noteLaunchTime(ComponentName componentName, int i) throws RemoteException;

    PkgUsageStats getPkgUsageStats(ComponentName componentName) throws RemoteException;

    PkgUsageStats[] getAllPkgUsageStats() throws RemoteException;

    /* loaded from: IUsageStats$Stub.class */
    public static abstract class Stub extends Binder implements IUsageStats {
        private static final String DESCRIPTOR = "com.android.internal.app.IUsageStats";
        static final int TRANSACTION_noteResumeComponent = 1;
        static final int TRANSACTION_notePauseComponent = 2;
        static final int TRANSACTION_noteLaunchTime = 3;
        static final int TRANSACTION_getPkgUsageStats = 4;
        static final int TRANSACTION_getAllPkgUsageStats = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUsageStats asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IUsageStats)) {
                return (IUsageStats) iin;
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
            ComponentName _arg02;
            ComponentName _arg03;
            ComponentName _arg04;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    noteResumeComponent(_arg04);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    notePauseComponent(_arg03);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _arg1 = data.readInt();
                    noteLaunchTime(_arg02, _arg1);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    PkgUsageStats _result = getPkgUsageStats(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    PkgUsageStats[] _result2 = getAllPkgUsageStats();
                    reply.writeNoException();
                    reply.writeTypedArray(_result2, 1);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IUsageStats$Stub$Proxy.class */
        public static class Proxy implements IUsageStats {
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

            @Override // com.android.internal.app.IUsageStats
            public void noteResumeComponent(ComponentName componentName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        _data.writeInt(1);
                        componentName.writeToParcel(_data, 0);
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

            @Override // com.android.internal.app.IUsageStats
            public void notePauseComponent(ComponentName componentName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        _data.writeInt(1);
                        componentName.writeToParcel(_data, 0);
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

            @Override // com.android.internal.app.IUsageStats
            public void noteLaunchTime(ComponentName componentName, int millis) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        _data.writeInt(1);
                        componentName.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(millis);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IUsageStats
            public PkgUsageStats getPkgUsageStats(ComponentName componentName) throws RemoteException {
                PkgUsageStats _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        _data.writeInt(1);
                        componentName.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = PkgUsageStats.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IUsageStats
            public PkgUsageStats[] getAllPkgUsageStats() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    PkgUsageStats[] _result = (PkgUsageStats[]) _reply.createTypedArray(PkgUsageStats.CREATOR);
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