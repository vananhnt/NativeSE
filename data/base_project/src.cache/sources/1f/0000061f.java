package android.hardware.camera2;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: ICameraDeviceCallbacks.class */
public interface ICameraDeviceCallbacks extends IInterface {
    void onCameraError(int i) throws RemoteException;

    void onCameraIdle() throws RemoteException;

    void onCaptureStarted(int i, long j) throws RemoteException;

    void onResultReceived(int i, CameraMetadataNative cameraMetadataNative) throws RemoteException;

    /* loaded from: ICameraDeviceCallbacks$Stub.class */
    public static abstract class Stub extends Binder implements ICameraDeviceCallbacks {
        private static final String DESCRIPTOR = "android.hardware.camera2.ICameraDeviceCallbacks";
        static final int TRANSACTION_onCameraError = 1;
        static final int TRANSACTION_onCameraIdle = 2;
        static final int TRANSACTION_onCaptureStarted = 3;
        static final int TRANSACTION_onResultReceived = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICameraDeviceCallbacks asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICameraDeviceCallbacks)) {
                return (ICameraDeviceCallbacks) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            CameraMetadataNative _arg1;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    onCameraError(_arg0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    onCameraIdle();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    long _arg12 = data.readLong();
                    onCaptureStarted(_arg02, _arg12);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg1 = CameraMetadataNative.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    onResultReceived(_arg03, _arg1);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: ICameraDeviceCallbacks$Stub$Proxy.class */
        private static class Proxy implements ICameraDeviceCallbacks {
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

            @Override // android.hardware.camera2.ICameraDeviceCallbacks
            public void onCameraError(int errorCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceCallbacks
            public void onCameraIdle() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceCallbacks
            public void onCaptureStarted(int requestId, long timestamp) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(requestId);
                    _data.writeLong(timestamp);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceCallbacks
            public void onResultReceived(int requestId, CameraMetadataNative result) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(requestId);
                    if (result != null) {
                        _data.writeInt(1);
                        result.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(4, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}