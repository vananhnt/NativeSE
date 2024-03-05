package com.android.internal.app;

import android.app.AppOpsManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.internal.app.IAppOpsCallback;
import java.util.List;

/* loaded from: IAppOpsService.class */
public interface IAppOpsService extends IInterface {
    int checkOperation(int i, int i2, String str) throws RemoteException;

    int noteOperation(int i, int i2, String str) throws RemoteException;

    int startOperation(IBinder iBinder, int i, int i2, String str) throws RemoteException;

    void finishOperation(IBinder iBinder, int i, int i2, String str) throws RemoteException;

    void startWatchingMode(int i, String str, IAppOpsCallback iAppOpsCallback) throws RemoteException;

    void stopWatchingMode(IAppOpsCallback iAppOpsCallback) throws RemoteException;

    IBinder getToken(IBinder iBinder) throws RemoteException;

    int checkPackage(int i, String str) throws RemoteException;

    List<AppOpsManager.PackageOps> getPackagesForOps(int[] iArr) throws RemoteException;

    List<AppOpsManager.PackageOps> getOpsForPackage(int i, String str, int[] iArr) throws RemoteException;

    void setMode(int i, int i2, String str, int i3) throws RemoteException;

    void resetAllModes() throws RemoteException;

    /* loaded from: IAppOpsService$Stub.class */
    public static abstract class Stub extends Binder implements IAppOpsService {
        private static final String DESCRIPTOR = "com.android.internal.app.IAppOpsService";
        static final int TRANSACTION_checkOperation = 1;
        static final int TRANSACTION_noteOperation = 2;
        static final int TRANSACTION_startOperation = 3;
        static final int TRANSACTION_finishOperation = 4;
        static final int TRANSACTION_startWatchingMode = 5;
        static final int TRANSACTION_stopWatchingMode = 6;
        static final int TRANSACTION_getToken = 7;
        static final int TRANSACTION_checkPackage = 8;
        static final int TRANSACTION_getPackagesForOps = 9;
        static final int TRANSACTION_getOpsForPackage = 10;
        static final int TRANSACTION_setMode = 11;
        static final int TRANSACTION_resetAllModes = 12;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAppOpsService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAppOpsService)) {
                return (IAppOpsService) iin;
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
                    String _arg2 = data.readString();
                    int _result = checkOperation(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    int _arg12 = data.readInt();
                    String _arg22 = data.readString();
                    int _result2 = noteOperation(_arg02, _arg12, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg03 = data.readStrongBinder();
                    int _arg13 = data.readInt();
                    int _arg23 = data.readInt();
                    String _arg3 = data.readString();
                    int _result3 = startOperation(_arg03, _arg13, _arg23, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg04 = data.readStrongBinder();
                    int _arg14 = data.readInt();
                    int _arg24 = data.readInt();
                    String _arg32 = data.readString();
                    finishOperation(_arg04, _arg14, _arg24, _arg32);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    String _arg15 = data.readString();
                    IAppOpsCallback _arg25 = IAppOpsCallback.Stub.asInterface(data.readStrongBinder());
                    startWatchingMode(_arg05, _arg15, _arg25);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    IAppOpsCallback _arg06 = IAppOpsCallback.Stub.asInterface(data.readStrongBinder());
                    stopWatchingMode(_arg06);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg07 = data.readStrongBinder();
                    IBinder _result4 = getToken(_arg07);
                    reply.writeNoException();
                    reply.writeStrongBinder(_result4);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    String _arg16 = data.readString();
                    int _result5 = checkPackage(_arg08, _arg16);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _arg09 = data.createIntArray();
                    List<AppOpsManager.PackageOps> _result6 = getPackagesForOps(_arg09);
                    reply.writeNoException();
                    reply.writeTypedList(_result6);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    String _arg17 = data.readString();
                    int[] _arg26 = data.createIntArray();
                    List<AppOpsManager.PackageOps> _result7 = getOpsForPackage(_arg010, _arg17, _arg26);
                    reply.writeNoException();
                    reply.writeTypedList(_result7);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    int _arg18 = data.readInt();
                    String _arg27 = data.readString();
                    int _arg33 = data.readInt();
                    setMode(_arg011, _arg18, _arg27, _arg33);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    resetAllModes();
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IAppOpsService$Stub$Proxy.class */
        private static class Proxy implements IAppOpsService {
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

            @Override // com.android.internal.app.IAppOpsService
            public int checkOperation(int code, int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(1, _data, _reply, 0);
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

            @Override // com.android.internal.app.IAppOpsService
            public int noteOperation(int code, int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
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

            @Override // com.android.internal.app.IAppOpsService
            public int startOperation(IBinder token, int code, int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
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

            @Override // com.android.internal.app.IAppOpsService
            public void finishOperation(IBinder token, int code, int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void startWatchingMode(int op, String packageName, IAppOpsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(op);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
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

            @Override // com.android.internal.app.IAppOpsService
            public void stopWatchingMode(IAppOpsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
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

            @Override // com.android.internal.app.IAppOpsService
            public IBinder getToken(IBinder clientToken) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientToken);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public int checkPackage(int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(8, _data, _reply, 0);
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

            @Override // com.android.internal.app.IAppOpsService
            public List<AppOpsManager.PackageOps> getPackagesForOps(int[] ops) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(ops);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    List<AppOpsManager.PackageOps> _result = _reply.createTypedArrayList(AppOpsManager.PackageOps.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public List<AppOpsManager.PackageOps> getOpsForPackage(int uid, String packageName, int[] ops) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeIntArray(ops);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    List<AppOpsManager.PackageOps> _result = _reply.createTypedArrayList(AppOpsManager.PackageOps.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void setMode(int code, int uid, String packageName, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeInt(mode);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void resetAllModes() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
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