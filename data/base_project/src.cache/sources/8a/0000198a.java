package com.android.internal.app;

import android.content.pm.ContainerEncryptionParams;
import android.content.pm.PackageInfoLite;
import android.content.res.ObbInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

/* loaded from: IMediaContainerService.class */
public interface IMediaContainerService extends IInterface {
    String copyResourceToContainer(Uri uri, String str, String str2, String str3, String str4, boolean z, boolean z2) throws RemoteException;

    int copyResource(Uri uri, ContainerEncryptionParams containerEncryptionParams, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    PackageInfoLite getMinimalPackageInfo(String str, int i, long j) throws RemoteException;

    boolean checkInternalFreeStorage(Uri uri, boolean z, long j) throws RemoteException;

    boolean checkExternalFreeStorage(Uri uri, boolean z) throws RemoteException;

    ObbInfo getObbInfo(String str) throws RemoteException;

    long calculateDirectorySize(String str) throws RemoteException;

    long[] getFileSystemStats(String str) throws RemoteException;

    void clearDirectory(String str) throws RemoteException;

    long calculateInstalledSize(String str, boolean z) throws RemoteException;

    /* loaded from: IMediaContainerService$Stub.class */
    public static abstract class Stub extends Binder implements IMediaContainerService {
        private static final String DESCRIPTOR = "com.android.internal.app.IMediaContainerService";
        static final int TRANSACTION_copyResourceToContainer = 1;
        static final int TRANSACTION_copyResource = 2;
        static final int TRANSACTION_getMinimalPackageInfo = 3;
        static final int TRANSACTION_checkInternalFreeStorage = 4;
        static final int TRANSACTION_checkExternalFreeStorage = 5;
        static final int TRANSACTION_getObbInfo = 6;
        static final int TRANSACTION_calculateDirectorySize = 7;
        static final int TRANSACTION_getFileSystemStats = 8;
        static final int TRANSACTION_clearDirectory = 9;
        static final int TRANSACTION_calculateInstalledSize = 10;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaContainerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaContainerService)) {
                return (IMediaContainerService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Uri _arg0;
            Uri _arg02;
            Uri _arg03;
            ContainerEncryptionParams _arg1;
            ParcelFileDescriptor _arg2;
            Uri _arg04;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    String _arg12 = data.readString();
                    String _arg22 = data.readString();
                    String _arg3 = data.readString();
                    String _arg4 = data.readString();
                    boolean _arg5 = 0 != data.readInt();
                    boolean _arg6 = 0 != data.readInt();
                    String _result = copyResourceToContainer(_arg04, _arg12, _arg22, _arg3, _arg4, _arg5, _arg6);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg1 = ContainerEncryptionParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg2 = ParcelFileDescriptor.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    int _result2 = copyResource(_arg03, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    int _arg13 = data.readInt();
                    long _arg23 = data.readLong();
                    PackageInfoLite _result3 = getMinimalPackageInfo(_arg05, _arg13, _arg23);
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
                        _arg02 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    boolean _arg14 = 0 != data.readInt();
                    long _arg24 = data.readLong();
                    boolean _result4 = checkInternalFreeStorage(_arg02, _arg14, _arg24);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    boolean _arg15 = 0 != data.readInt();
                    boolean _result5 = checkExternalFreeStorage(_arg0, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    ObbInfo _result6 = getObbInfo(_arg06);
                    reply.writeNoException();
                    if (_result6 != null) {
                        reply.writeInt(1);
                        _result6.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    long _result7 = calculateDirectorySize(_arg07);
                    reply.writeNoException();
                    reply.writeLong(_result7);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg08 = data.readString();
                    long[] _result8 = getFileSystemStats(_arg08);
                    reply.writeNoException();
                    reply.writeLongArray(_result8);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg09 = data.readString();
                    clearDirectory(_arg09);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    boolean _arg16 = 0 != data.readInt();
                    long _result9 = calculateInstalledSize(_arg010, _arg16);
                    reply.writeNoException();
                    reply.writeLong(_result9);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IMediaContainerService$Stub$Proxy.class */
        public static class Proxy implements IMediaContainerService {
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

            @Override // com.android.internal.app.IMediaContainerService
            public String copyResourceToContainer(Uri packageURI, String containerId, String key, String resFileName, String publicResFileName, boolean isExternal, boolean isForwardLocked) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (packageURI != null) {
                        _data.writeInt(1);
                        packageURI.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(containerId);
                    _data.writeString(key);
                    _data.writeString(resFileName);
                    _data.writeString(publicResFileName);
                    _data.writeInt(isExternal ? 1 : 0);
                    _data.writeInt(isForwardLocked ? 1 : 0);
                    this.mRemote.transact(1, _data, _reply, 0);
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

            @Override // com.android.internal.app.IMediaContainerService
            public int copyResource(Uri packageURI, ContainerEncryptionParams encryptionParams, ParcelFileDescriptor outStream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (packageURI != null) {
                        _data.writeInt(1);
                        packageURI.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (encryptionParams != null) {
                        _data.writeInt(1);
                        encryptionParams.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (outStream != null) {
                        _data.writeInt(1);
                        outStream.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // com.android.internal.app.IMediaContainerService
            public PackageInfoLite getMinimalPackageInfo(String packagePath, int flags, long threshold) throws RemoteException {
                PackageInfoLite _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packagePath);
                    _data.writeInt(flags);
                    _data.writeLong(threshold);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = PackageInfoLite.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IMediaContainerService
            public boolean checkInternalFreeStorage(Uri fileUri, boolean isForwardLocked, long threshold) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (fileUri != null) {
                        _data.writeInt(1);
                        fileUri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(isForwardLocked ? 1 : 0);
                    _data.writeLong(threshold);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IMediaContainerService
            public boolean checkExternalFreeStorage(Uri fileUri, boolean isForwardLocked) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (fileUri != null) {
                        _data.writeInt(1);
                        fileUri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(isForwardLocked ? 1 : 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IMediaContainerService
            public ObbInfo getObbInfo(String filename) throws RemoteException {
                ObbInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(filename);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ObbInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IMediaContainerService
            public long calculateDirectorySize(String directory) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(directory);
                    this.mRemote.transact(7, _data, _reply, 0);
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

            @Override // com.android.internal.app.IMediaContainerService
            public long[] getFileSystemStats(String path) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    long[] _result = _reply.createLongArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IMediaContainerService
            public void clearDirectory(String directory) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(directory);
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

            @Override // com.android.internal.app.IMediaContainerService
            public long calculateInstalledSize(String packagePath, boolean isForwardLocked) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packagePath);
                    _data.writeInt(isForwardLocked ? 1 : 0);
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
        }
    }
}