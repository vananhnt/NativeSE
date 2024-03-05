package com.android.internal.backup;

import android.app.backup.RestoreSet;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

/* loaded from: IBackupTransport.class */
public interface IBackupTransport extends IInterface {
    Intent configurationIntent() throws RemoteException;

    String currentDestinationString() throws RemoteException;

    String transportDirName() throws RemoteException;

    long requestBackupTime() throws RemoteException;

    int initializeDevice() throws RemoteException;

    int performBackup(PackageInfo packageInfo, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    int clearBackupData(PackageInfo packageInfo) throws RemoteException;

    int finishBackup() throws RemoteException;

    RestoreSet[] getAvailableRestoreSets() throws RemoteException;

    long getCurrentRestoreSet() throws RemoteException;

    int startRestore(long j, PackageInfo[] packageInfoArr) throws RemoteException;

    String nextRestorePackage() throws RemoteException;

    int getRestoreData(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    void finishRestore() throws RemoteException;

    /* loaded from: IBackupTransport$Stub.class */
    public static abstract class Stub extends Binder implements IBackupTransport {
        private static final String DESCRIPTOR = "com.android.internal.backup.IBackupTransport";
        static final int TRANSACTION_configurationIntent = 1;
        static final int TRANSACTION_currentDestinationString = 2;
        static final int TRANSACTION_transportDirName = 3;
        static final int TRANSACTION_requestBackupTime = 4;
        static final int TRANSACTION_initializeDevice = 5;
        static final int TRANSACTION_performBackup = 6;
        static final int TRANSACTION_clearBackupData = 7;
        static final int TRANSACTION_finishBackup = 8;
        static final int TRANSACTION_getAvailableRestoreSets = 9;
        static final int TRANSACTION_getCurrentRestoreSet = 10;
        static final int TRANSACTION_startRestore = 11;
        static final int TRANSACTION_nextRestorePackage = 12;
        static final int TRANSACTION_getRestoreData = 13;
        static final int TRANSACTION_finishRestore = 14;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBackupTransport asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBackupTransport)) {
                return (IBackupTransport) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ParcelFileDescriptor _arg0;
            PackageInfo _arg02;
            PackageInfo _arg03;
            ParcelFileDescriptor _arg1;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    Intent _result = configurationIntent();
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
                    String _result2 = currentDestinationString();
                    reply.writeNoException();
                    reply.writeString(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _result3 = transportDirName();
                    reply.writeNoException();
                    reply.writeString(_result3);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    long _result4 = requestBackupTime();
                    reply.writeNoException();
                    reply.writeLong(_result4);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _result5 = initializeDevice();
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = PackageInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg1 = ParcelFileDescriptor.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    int _result6 = performBackup(_arg03, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = PackageInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _result7 = clearBackupData(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _result8 = finishBackup();
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    RestoreSet[] _result9 = getAvailableRestoreSets();
                    reply.writeNoException();
                    reply.writeTypedArray(_result9, 1);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    long _result10 = getCurrentRestoreSet();
                    reply.writeNoException();
                    reply.writeLong(_result10);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg04 = data.readLong();
                    PackageInfo[] _arg12 = (PackageInfo[]) data.createTypedArray(PackageInfo.CREATOR);
                    int _result11 = startRestore(_arg04, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String _result12 = nextRestorePackage();
                    reply.writeNoException();
                    reply.writeString(_result12);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = ParcelFileDescriptor.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result13 = getRestoreData(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    finishRestore();
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IBackupTransport$Stub$Proxy.class */
        private static class Proxy implements IBackupTransport {
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

            @Override // com.android.internal.backup.IBackupTransport
            public Intent configurationIntent() throws RemoteException {
                Intent _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Intent.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public String currentDestinationString() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public String transportDirName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public long requestBackupTime() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public int initializeDevice() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
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

            @Override // com.android.internal.backup.IBackupTransport
            public int performBackup(PackageInfo packageInfo, ParcelFileDescriptor inFd) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (packageInfo != null) {
                        _data.writeInt(1);
                        packageInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (inFd != null) {
                        _data.writeInt(1);
                        inFd.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(6, _data, _reply, 0);
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

            @Override // com.android.internal.backup.IBackupTransport
            public int clearBackupData(PackageInfo packageInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (packageInfo != null) {
                        _data.writeInt(1);
                        packageInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(7, _data, _reply, 0);
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

            @Override // com.android.internal.backup.IBackupTransport
            public int finishBackup() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.backup.IBackupTransport
            public RestoreSet[] getAvailableRestoreSets() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    RestoreSet[] _result = (RestoreSet[]) _reply.createTypedArray(RestoreSet.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public long getCurrentRestoreSet() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public int startRestore(long token, PackageInfo[] packages) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(token);
                    _data.writeTypedArray(packages, 0);
                    this.mRemote.transact(11, _data, _reply, 0);
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

            @Override // com.android.internal.backup.IBackupTransport
            public String nextRestorePackage() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public int getRestoreData(ParcelFileDescriptor outFd) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (outFd != null) {
                        _data.writeInt(1);
                        outFd.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(13, _data, _reply, 0);
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

            @Override // com.android.internal.backup.IBackupTransport
            public void finishRestore() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
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