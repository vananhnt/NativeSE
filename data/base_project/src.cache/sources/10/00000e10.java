package android.service.notification;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: INotificationListener.class */
public interface INotificationListener extends IInterface {
    void onNotificationPosted(StatusBarNotification statusBarNotification) throws RemoteException;

    void onNotificationRemoved(StatusBarNotification statusBarNotification) throws RemoteException;

    /* loaded from: INotificationListener$Stub.class */
    public static abstract class Stub extends Binder implements INotificationListener {
        private static final String DESCRIPTOR = "android.service.notification.INotificationListener";
        static final int TRANSACTION_onNotificationPosted = 1;
        static final int TRANSACTION_onNotificationRemoved = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INotificationListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INotificationListener)) {
                return (INotificationListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            StatusBarNotification _arg0;
            StatusBarNotification _arg02;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = StatusBarNotification.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    onNotificationPosted(_arg02);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = StatusBarNotification.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onNotificationRemoved(_arg0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: INotificationListener$Stub$Proxy.class */
        public static class Proxy implements INotificationListener {
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

            @Override // android.service.notification.INotificationListener
            public void onNotificationPosted(StatusBarNotification notification) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (notification != null) {
                        _data.writeInt(1);
                        notification.writeToParcel(_data, 0);
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

            @Override // android.service.notification.INotificationListener
            public void onNotificationRemoved(StatusBarNotification notification) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (notification != null) {
                        _data.writeInt(1);
                        notification.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}