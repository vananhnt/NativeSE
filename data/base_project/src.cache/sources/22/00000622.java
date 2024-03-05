package android.hardware.camera2;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.Surface;

/* loaded from: ICameraDeviceUser.class */
public interface ICameraDeviceUser extends IInterface {
    void disconnect() throws RemoteException;

    int submitRequest(CaptureRequest captureRequest, boolean z) throws RemoteException;

    int cancelRequest(int i) throws RemoteException;

    int deleteStream(int i) throws RemoteException;

    int createStream(int i, int i2, int i3, Surface surface) throws RemoteException;

    int createDefaultRequest(int i, CameraMetadataNative cameraMetadataNative) throws RemoteException;

    int getCameraInfo(CameraMetadataNative cameraMetadataNative) throws RemoteException;

    int waitUntilIdle() throws RemoteException;

    int flush() throws RemoteException;

    /* loaded from: ICameraDeviceUser$Stub.class */
    public static abstract class Stub extends Binder implements ICameraDeviceUser {
        private static final String DESCRIPTOR = "android.hardware.camera2.ICameraDeviceUser";
        static final int TRANSACTION_disconnect = 1;
        static final int TRANSACTION_submitRequest = 2;
        static final int TRANSACTION_cancelRequest = 3;
        static final int TRANSACTION_deleteStream = 4;
        static final int TRANSACTION_createStream = 5;
        static final int TRANSACTION_createDefaultRequest = 6;
        static final int TRANSACTION_getCameraInfo = 7;
        static final int TRANSACTION_waitUntilIdle = 8;
        static final int TRANSACTION_flush = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICameraDeviceUser asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICameraDeviceUser)) {
                return (ICameraDeviceUser) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Surface _arg3;
            CaptureRequest _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    disconnect();
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = CaptureRequest.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result = submitRequest(_arg0, 0 != data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _result2 = cancelRequest(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _result3 = deleteStream(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    int _arg1 = data.readInt();
                    int _arg2 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg3 = Surface.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    int _result4 = createStream(_arg02, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    CameraMetadataNative _arg12 = new CameraMetadataNative();
                    int _result5 = createDefaultRequest(_arg03, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    if (_arg12 != null) {
                        reply.writeInt(1);
                        _arg12.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    CameraMetadataNative _arg04 = new CameraMetadataNative();
                    int _result6 = getCameraInfo(_arg04);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    if (_arg04 != null) {
                        reply.writeInt(1);
                        _arg04.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _result7 = waitUntilIdle();
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _result8 = flush();
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: ICameraDeviceUser$Stub$Proxy.class */
        private static class Proxy implements ICameraDeviceUser {
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

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void disconnect() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int submitRequest(CaptureRequest request, boolean streaming) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (request != null) {
                        _data.writeInt(1);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(streaming ? 1 : 0);
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

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int cancelRequest(int requestId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(requestId);
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

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int deleteStream(int streamId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamId);
                    this.mRemote.transact(4, _data, _reply, 0);
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

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int createStream(int width, int height, int format, Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeInt(format);
                    if (surface != null) {
                        _data.writeInt(1);
                        surface.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int createDefaultRequest(int templateId, CameraMetadataNative request) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(templateId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        request.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int getCameraInfo(CameraMetadataNative info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
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

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int waitUntilIdle() throws RemoteException {
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

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int flush() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
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