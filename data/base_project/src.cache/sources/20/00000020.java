package android.accessibilityservice;

import android.accessibilityservice.IAccessibilityServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

/* loaded from: IAccessibilityServiceClient.class */
public interface IAccessibilityServiceClient extends IInterface {
    void setConnection(IAccessibilityServiceConnection iAccessibilityServiceConnection, int i) throws RemoteException;

    void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) throws RemoteException;

    void onInterrupt() throws RemoteException;

    void onGesture(int i) throws RemoteException;

    void clearAccessibilityNodeInfoCache() throws RemoteException;

    void onKeyEvent(KeyEvent keyEvent, int i) throws RemoteException;

    /* loaded from: IAccessibilityServiceClient$Stub.class */
    public static abstract class Stub extends Binder implements IAccessibilityServiceClient {
        private static final String DESCRIPTOR = "android.accessibilityservice.IAccessibilityServiceClient";
        static final int TRANSACTION_setConnection = 1;
        static final int TRANSACTION_onAccessibilityEvent = 2;
        static final int TRANSACTION_onInterrupt = 3;
        static final int TRANSACTION_onGesture = 4;
        static final int TRANSACTION_clearAccessibilityNodeInfoCache = 5;
        static final int TRANSACTION_onKeyEvent = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccessibilityServiceClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityServiceClient)) {
                return (IAccessibilityServiceClient) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            KeyEvent _arg0;
            AccessibilityEvent _arg02;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IAccessibilityServiceConnection _arg03 = IAccessibilityServiceConnection.Stub.asInterface(data.readStrongBinder());
                    int _arg1 = data.readInt();
                    setConnection(_arg03, _arg1);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = AccessibilityEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    onAccessibilityEvent(_arg02);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    onInterrupt();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    onGesture(_arg04);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    clearAccessibilityNodeInfoCache();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = KeyEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg12 = data.readInt();
                    onKeyEvent(_arg0, _arg12);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IAccessibilityServiceClient$Stub$Proxy.class */
        public static class Proxy implements IAccessibilityServiceClient {
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

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void setConnection(IAccessibilityServiceConnection connection, int connectionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(connection != null ? connection.asBinder() : null);
                    _data.writeInt(connectionId);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onAccessibilityEvent(AccessibilityEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(1);
                        event.writeToParcel(_data, 0);
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

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onInterrupt() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onGesture(int gesture) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(gesture);
                    this.mRemote.transact(4, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void clearAccessibilityNodeInfoCache() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceClient
            public void onKeyEvent(KeyEvent event, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(1);
                        event.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(sequence);
                    this.mRemote.transact(6, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}