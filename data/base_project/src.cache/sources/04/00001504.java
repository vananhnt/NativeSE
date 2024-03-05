package android.view;

import android.graphics.Region;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IMagnificationCallbacks.class */
public interface IMagnificationCallbacks extends IInterface {
    void onMagnifedBoundsChanged(Region region) throws RemoteException;

    void onRectangleOnScreenRequested(int i, int i2, int i3, int i4) throws RemoteException;

    void onRotationChanged(int i) throws RemoteException;

    void onUserContextChanged() throws RemoteException;

    /* loaded from: IMagnificationCallbacks$Stub.class */
    public static abstract class Stub extends Binder implements IMagnificationCallbacks {
        private static final String DESCRIPTOR = "android.view.IMagnificationCallbacks";
        static final int TRANSACTION_onMagnifedBoundsChanged = 1;
        static final int TRANSACTION_onRectangleOnScreenRequested = 2;
        static final int TRANSACTION_onRotationChanged = 3;
        static final int TRANSACTION_onUserContextChanged = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMagnificationCallbacks asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMagnificationCallbacks)) {
                return (IMagnificationCallbacks) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Region _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = Region.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onMagnifedBoundsChanged(_arg0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    int _arg1 = data.readInt();
                    int _arg2 = data.readInt();
                    int _arg3 = data.readInt();
                    onRectangleOnScreenRequested(_arg02, _arg1, _arg2, _arg3);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    onRotationChanged(_arg03);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    onUserContextChanged();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IMagnificationCallbacks$Stub$Proxy.class */
        public static class Proxy implements IMagnificationCallbacks {
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

            @Override // android.view.IMagnificationCallbacks
            public void onMagnifedBoundsChanged(Region bounds) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bounds != null) {
                        _data.writeInt(1);
                        bounds.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IMagnificationCallbacks
            public void onRectangleOnScreenRequested(int left, int top, int right, int bottom) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(left);
                    _data.writeInt(top);
                    _data.writeInt(right);
                    _data.writeInt(bottom);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IMagnificationCallbacks
            public void onRotationChanged(int rotation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rotation);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IMagnificationCallbacks
            public void onUserContextChanged() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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