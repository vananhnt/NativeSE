package android.accessibilityservice;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;

/* loaded from: IAccessibilityServiceConnection.class */
public interface IAccessibilityServiceConnection extends IInterface {
    void setServiceInfo(AccessibilityServiceInfo accessibilityServiceInfo) throws RemoteException;

    boolean findAccessibilityNodeInfoByAccessibilityId(int i, long j, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, long j2) throws RemoteException;

    boolean findAccessibilityNodeInfosByText(int i, long j, String str, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, long j2) throws RemoteException;

    boolean findAccessibilityNodeInfosByViewId(int i, long j, String str, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, long j2) throws RemoteException;

    boolean findFocus(int i, long j, int i2, int i3, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, long j2) throws RemoteException;

    boolean focusSearch(int i, long j, int i2, int i3, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, long j2) throws RemoteException;

    boolean performAccessibilityAction(int i, long j, int i2, Bundle bundle, int i3, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, long j2) throws RemoteException;

    AccessibilityServiceInfo getServiceInfo() throws RemoteException;

    boolean performGlobalAction(int i) throws RemoteException;

    void setOnKeyEventResult(boolean z, int i) throws RemoteException;

    /* loaded from: IAccessibilityServiceConnection$Stub.class */
    public static abstract class Stub extends Binder implements IAccessibilityServiceConnection {
        private static final String DESCRIPTOR = "android.accessibilityservice.IAccessibilityServiceConnection";
        static final int TRANSACTION_setServiceInfo = 1;
        static final int TRANSACTION_findAccessibilityNodeInfoByAccessibilityId = 2;
        static final int TRANSACTION_findAccessibilityNodeInfosByText = 3;
        static final int TRANSACTION_findAccessibilityNodeInfosByViewId = 4;
        static final int TRANSACTION_findFocus = 5;
        static final int TRANSACTION_focusSearch = 6;
        static final int TRANSACTION_performAccessibilityAction = 7;
        static final int TRANSACTION_getServiceInfo = 8;
        static final int TRANSACTION_performGlobalAction = 9;
        static final int TRANSACTION_setOnKeyEventResult = 10;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccessibilityServiceConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityServiceConnection)) {
                return (IAccessibilityServiceConnection) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg3;
            AccessibilityServiceInfo _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = AccessibilityServiceInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    setServiceInfo(_arg0);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    long _arg1 = data.readLong();
                    int _arg2 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg32 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg4 = data.readInt();
                    long _arg5 = data.readLong();
                    boolean _result = findAccessibilityNodeInfoByAccessibilityId(_arg02, _arg1, _arg2, _arg32, _arg4, _arg5);
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    long _arg12 = data.readLong();
                    String _arg22 = data.readString();
                    int _arg33 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg42 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    long _arg52 = data.readLong();
                    boolean _result2 = findAccessibilityNodeInfosByText(_arg03, _arg12, _arg22, _arg33, _arg42, _arg52);
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    long _arg13 = data.readLong();
                    String _arg23 = data.readString();
                    int _arg34 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg43 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    long _arg53 = data.readLong();
                    boolean _result3 = findAccessibilityNodeInfosByViewId(_arg04, _arg13, _arg23, _arg34, _arg43, _arg53);
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    long _arg14 = data.readLong();
                    int _arg24 = data.readInt();
                    int _arg35 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg44 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    long _arg54 = data.readLong();
                    boolean _result4 = findFocus(_arg05, _arg14, _arg24, _arg35, _arg44, _arg54);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    long _arg15 = data.readLong();
                    int _arg25 = data.readInt();
                    int _arg36 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg45 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    long _arg55 = data.readLong();
                    boolean _result5 = focusSearch(_arg06, _arg15, _arg25, _arg36, _arg45, _arg55);
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    long _arg16 = data.readLong();
                    int _arg26 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg3 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    int _arg46 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg56 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    long _arg6 = data.readLong();
                    boolean _result6 = performAccessibilityAction(_arg07, _arg16, _arg26, _arg3, _arg46, _arg56, _arg6);
                    reply.writeNoException();
                    reply.writeInt(_result6 ? 1 : 0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    AccessibilityServiceInfo _result7 = getServiceInfo();
                    reply.writeNoException();
                    if (_result7 != null) {
                        reply.writeInt(1);
                        _result7.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    boolean _result8 = performGlobalAction(_arg08);
                    reply.writeNoException();
                    reply.writeInt(_result8 ? 1 : 0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg09 = 0 != data.readInt();
                    int _arg17 = data.readInt();
                    setOnKeyEventResult(_arg09, _arg17);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IAccessibilityServiceConnection$Stub$Proxy.class */
        private static class Proxy implements IAccessibilityServiceConnection {
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

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setServiceInfo(AccessibilityServiceInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
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

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean findAccessibilityNodeInfoByAccessibilityId(int accessibilityWindowId, long accessibilityNodeId, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeLong(threadId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean findAccessibilityNodeInfosByText(int accessibilityWindowId, long accessibilityNodeId, String text, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeString(text);
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeLong(threadId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean findAccessibilityNodeInfosByViewId(int accessibilityWindowId, long accessibilityNodeId, String viewId, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeString(viewId);
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeLong(threadId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean findFocus(int accessibilityWindowId, long accessibilityNodeId, int focusType, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(focusType);
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeLong(threadId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean focusSearch(int accessibilityWindowId, long accessibilityNodeId, int direction, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(direction);
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeLong(threadId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean performAccessibilityAction(int accessibilityWindowId, long accessibilityNodeId, int action, Bundle arguments, int interactionId, IAccessibilityInteractionConnectionCallback callback, long threadId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(accessibilityWindowId);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(action);
                    if (arguments != null) {
                        _data.writeInt(1);
                        arguments.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeLong(threadId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public AccessibilityServiceInfo getServiceInfo() throws RemoteException {
                AccessibilityServiceInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = AccessibilityServiceInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public boolean performGlobalAction(int action) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(action);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accessibilityservice.IAccessibilityServiceConnection
            public void setOnKeyEventResult(boolean handled, int sequence) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(handled ? 1 : 0);
                    _data.writeInt(sequence);
                    this.mRemote.transact(10, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}