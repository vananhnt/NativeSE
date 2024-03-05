package android.view.accessibility;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.MagnificationSpec;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;

/* loaded from: IAccessibilityInteractionConnection.class */
public interface IAccessibilityInteractionConnection extends IInterface {
    void findAccessibilityNodeInfoByAccessibilityId(long j, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) throws RemoteException;

    void findAccessibilityNodeInfosByViewId(long j, String str, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) throws RemoteException;

    void findAccessibilityNodeInfosByText(long j, String str, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) throws RemoteException;

    void findFocus(long j, int i, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2, MagnificationSpec magnificationSpec) throws RemoteException;

    void focusSearch(long j, int i, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2, MagnificationSpec magnificationSpec) throws RemoteException;

    void performAccessibilityAction(long j, int i, Bundle bundle, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2) throws RemoteException;

    /* loaded from: IAccessibilityInteractionConnection$Stub.class */
    public static abstract class Stub extends Binder implements IAccessibilityInteractionConnection {
        private static final String DESCRIPTOR = "android.view.accessibility.IAccessibilityInteractionConnection";
        static final int TRANSACTION_findAccessibilityNodeInfoByAccessibilityId = 1;
        static final int TRANSACTION_findAccessibilityNodeInfosByViewId = 2;
        static final int TRANSACTION_findAccessibilityNodeInfosByText = 3;
        static final int TRANSACTION_findFocus = 4;
        static final int TRANSACTION_focusSearch = 5;
        static final int TRANSACTION_performAccessibilityAction = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccessibilityInteractionConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityInteractionConnection)) {
                return (IAccessibilityInteractionConnection) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg2;
            MagnificationSpec _arg7;
            MagnificationSpec _arg72;
            MagnificationSpec _arg73;
            MagnificationSpec _arg74;
            MagnificationSpec _arg6;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg0 = data.readLong();
                    int _arg1 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg22 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg3 = data.readInt();
                    int _arg4 = data.readInt();
                    long _arg5 = data.readLong();
                    if (0 != data.readInt()) {
                        _arg6 = MagnificationSpec.CREATOR.createFromParcel(data);
                    } else {
                        _arg6 = null;
                    }
                    findAccessibilityNodeInfoByAccessibilityId(_arg0, _arg1, _arg22, _arg3, _arg4, _arg5, _arg6);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg02 = data.readLong();
                    String _arg12 = data.readString();
                    int _arg23 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg32 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg42 = data.readInt();
                    int _arg52 = data.readInt();
                    long _arg62 = data.readLong();
                    if (0 != data.readInt()) {
                        _arg74 = MagnificationSpec.CREATOR.createFromParcel(data);
                    } else {
                        _arg74 = null;
                    }
                    findAccessibilityNodeInfosByViewId(_arg02, _arg12, _arg23, _arg32, _arg42, _arg52, _arg62, _arg74);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg03 = data.readLong();
                    String _arg13 = data.readString();
                    int _arg24 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg33 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg43 = data.readInt();
                    int _arg53 = data.readInt();
                    long _arg63 = data.readLong();
                    if (0 != data.readInt()) {
                        _arg73 = MagnificationSpec.CREATOR.createFromParcel(data);
                    } else {
                        _arg73 = null;
                    }
                    findAccessibilityNodeInfosByText(_arg03, _arg13, _arg24, _arg33, _arg43, _arg53, _arg63, _arg73);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg04 = data.readLong();
                    int _arg14 = data.readInt();
                    int _arg25 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg34 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg44 = data.readInt();
                    int _arg54 = data.readInt();
                    long _arg64 = data.readLong();
                    if (0 != data.readInt()) {
                        _arg72 = MagnificationSpec.CREATOR.createFromParcel(data);
                    } else {
                        _arg72 = null;
                    }
                    findFocus(_arg04, _arg14, _arg25, _arg34, _arg44, _arg54, _arg64, _arg72);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg05 = data.readLong();
                    int _arg15 = data.readInt();
                    int _arg26 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg35 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg45 = data.readInt();
                    int _arg55 = data.readInt();
                    long _arg65 = data.readLong();
                    if (0 != data.readInt()) {
                        _arg7 = MagnificationSpec.CREATOR.createFromParcel(data);
                    } else {
                        _arg7 = null;
                    }
                    focusSearch(_arg05, _arg15, _arg26, _arg35, _arg45, _arg55, _arg65, _arg7);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg06 = data.readLong();
                    int _arg16 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg2 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    int _arg36 = data.readInt();
                    IAccessibilityInteractionConnectionCallback _arg46 = IAccessibilityInteractionConnectionCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg56 = data.readInt();
                    int _arg66 = data.readInt();
                    long _arg75 = data.readLong();
                    performAccessibilityAction(_arg06, _arg16, _arg2, _arg36, _arg46, _arg56, _arg66, _arg75);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IAccessibilityInteractionConnection$Stub$Proxy.class */
        private static class Proxy implements IAccessibilityInteractionConnection {
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

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void findAccessibilityNodeInfoByAccessibilityId(long accessibilityNodeId, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeInt(interrogatingPid);
                    _data.writeLong(interrogatingTid);
                    if (spec != null) {
                        _data.writeInt(1);
                        spec.writeToParcel(_data, 0);
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

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void findAccessibilityNodeInfosByViewId(long accessibilityNodeId, String viewId, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeString(viewId);
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeInt(interrogatingPid);
                    _data.writeLong(interrogatingTid);
                    if (spec != null) {
                        _data.writeInt(1);
                        spec.writeToParcel(_data, 0);
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

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void findAccessibilityNodeInfosByText(long accessibilityNodeId, String text, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeString(text);
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeInt(interrogatingPid);
                    _data.writeLong(interrogatingTid);
                    if (spec != null) {
                        _data.writeInt(1);
                        spec.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void findFocus(long accessibilityNodeId, int focusType, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(focusType);
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeInt(interrogatingPid);
                    _data.writeLong(interrogatingTid);
                    if (spec != null) {
                        _data.writeInt(1);
                        spec.writeToParcel(_data, 0);
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

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void focusSearch(long accessibilityNodeId, int direction, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(accessibilityNodeId);
                    _data.writeInt(direction);
                    _data.writeInt(interactionId);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeInt(interrogatingPid);
                    _data.writeLong(interrogatingTid);
                    if (spec != null) {
                        _data.writeInt(1);
                        spec.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(5, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityInteractionConnection
            public void performAccessibilityAction(long accessibilityNodeId, int action, Bundle arguments, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                    _data.writeInt(flags);
                    _data.writeInt(interrogatingPid);
                    _data.writeLong(interrogatingTid);
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