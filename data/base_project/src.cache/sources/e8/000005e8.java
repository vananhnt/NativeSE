package android.hardware;

import android.hardware.ICameraClient;
import android.hardware.ICameraServiceListener;
import android.hardware.IProCameraCallbacks;
import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.utils.BinderHolder;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: ICameraService.class */
public interface ICameraService extends IInterface {
    int getNumberOfCameras() throws RemoteException;

    int getCameraInfo(int i, CameraInfo cameraInfo) throws RemoteException;

    int connect(ICameraClient iCameraClient, int i, String str, int i2, BinderHolder binderHolder) throws RemoteException;

    int connectPro(IProCameraCallbacks iProCameraCallbacks, int i, String str, int i2, BinderHolder binderHolder) throws RemoteException;

    int connectDevice(ICameraDeviceCallbacks iCameraDeviceCallbacks, int i, String str, int i2, BinderHolder binderHolder) throws RemoteException;

    int addListener(ICameraServiceListener iCameraServiceListener) throws RemoteException;

    int removeListener(ICameraServiceListener iCameraServiceListener) throws RemoteException;

    int getCameraCharacteristics(int i, CameraMetadataNative cameraMetadataNative) throws RemoteException;

    /* loaded from: ICameraService$Stub.class */
    public static abstract class Stub extends Binder implements ICameraService {
        private static final String DESCRIPTOR = "android.hardware.ICameraService";
        static final int TRANSACTION_getNumberOfCameras = 1;
        static final int TRANSACTION_getCameraInfo = 2;
        static final int TRANSACTION_connect = 3;
        static final int TRANSACTION_connectPro = 4;
        static final int TRANSACTION_connectDevice = 5;
        static final int TRANSACTION_addListener = 6;
        static final int TRANSACTION_removeListener = 7;
        static final int TRANSACTION_getCameraCharacteristics = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICameraService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICameraService)) {
                return (ICameraService) iin;
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
                    int _result = getNumberOfCameras();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    CameraInfo _arg1 = new CameraInfo();
                    int _result2 = getCameraInfo(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    if (_arg1 != null) {
                        reply.writeInt(1);
                        _arg1.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    ICameraClient _arg02 = ICameraClient.Stub.asInterface(data.readStrongBinder());
                    int _arg12 = data.readInt();
                    String _arg2 = data.readString();
                    int _arg3 = data.readInt();
                    BinderHolder _arg4 = new BinderHolder();
                    int _result3 = connect(_arg02, _arg12, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    if (_arg4 != null) {
                        reply.writeInt(1);
                        _arg4.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IProCameraCallbacks _arg03 = IProCameraCallbacks.Stub.asInterface(data.readStrongBinder());
                    int _arg13 = data.readInt();
                    String _arg22 = data.readString();
                    int _arg32 = data.readInt();
                    BinderHolder _arg42 = new BinderHolder();
                    int _result4 = connectPro(_arg03, _arg13, _arg22, _arg32, _arg42);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    if (_arg42 != null) {
                        reply.writeInt(1);
                        _arg42.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    ICameraDeviceCallbacks _arg04 = ICameraDeviceCallbacks.Stub.asInterface(data.readStrongBinder());
                    int _arg14 = data.readInt();
                    String _arg23 = data.readString();
                    int _arg33 = data.readInt();
                    BinderHolder _arg43 = new BinderHolder();
                    int _result5 = connectDevice(_arg04, _arg14, _arg23, _arg33, _arg43);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    if (_arg43 != null) {
                        reply.writeInt(1);
                        _arg43.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    ICameraServiceListener _arg05 = ICameraServiceListener.Stub.asInterface(data.readStrongBinder());
                    int _result6 = addListener(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    ICameraServiceListener _arg06 = ICameraServiceListener.Stub.asInterface(data.readStrongBinder());
                    int _result7 = removeListener(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    CameraMetadataNative _arg15 = new CameraMetadataNative();
                    int _result8 = getCameraCharacteristics(_arg07, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    if (_arg15 != null) {
                        reply.writeInt(1);
                        _arg15.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: ICameraService$Stub$Proxy.class */
        private static class Proxy implements ICameraService {
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

            @Override // android.hardware.ICameraService
            public int getNumberOfCameras() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.hardware.ICameraService
            public int getCameraInfo(int cameraId, CameraInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cameraId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        info.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int connect(ICameraClient client, int cameraId, String clientPackageName, int clientUid, BinderHolder device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    _data.writeInt(cameraId);
                    _data.writeString(clientPackageName);
                    _data.writeInt(clientUid);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        device.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int connectPro(IProCameraCallbacks callbacks, int cameraId, String clientPackageName, int clientUid, BinderHolder device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callbacks != null ? callbacks.asBinder() : null);
                    _data.writeInt(cameraId);
                    _data.writeString(clientPackageName);
                    _data.writeInt(clientUid);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        device.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int connectDevice(ICameraDeviceCallbacks callbacks, int cameraId, String clientPackageName, int clientUid, BinderHolder device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callbacks != null ? callbacks.asBinder() : null);
                    _data.writeInt(cameraId);
                    _data.writeString(clientPackageName);
                    _data.writeInt(clientUid);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        device.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int addListener(ICameraServiceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
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

            @Override // android.hardware.ICameraService
            public int removeListener(ICameraServiceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
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

            @Override // android.hardware.ICameraService
            public int getCameraCharacteristics(int cameraId, CameraMetadataNative info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cameraId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        info.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}