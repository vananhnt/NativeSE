package android.view.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.IWindow;
import android.view.accessibility.IAccessibilityInteractionConnection;
import android.view.accessibility.IAccessibilityManagerClient;
import java.util.List;

/* loaded from: IAccessibilityManager.class */
public interface IAccessibilityManager extends IInterface {
    int addClient(IAccessibilityManagerClient iAccessibilityManagerClient, int i) throws RemoteException;

    boolean sendAccessibilityEvent(AccessibilityEvent accessibilityEvent, int i) throws RemoteException;

    List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int i) throws RemoteException;

    List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int i, int i2) throws RemoteException;

    void interrupt(int i) throws RemoteException;

    int addAccessibilityInteractionConnection(IWindow iWindow, IAccessibilityInteractionConnection iAccessibilityInteractionConnection, int i) throws RemoteException;

    void removeAccessibilityInteractionConnection(IWindow iWindow) throws RemoteException;

    void registerUiTestAutomationService(IBinder iBinder, IAccessibilityServiceClient iAccessibilityServiceClient, AccessibilityServiceInfo accessibilityServiceInfo) throws RemoteException;

    void unregisterUiTestAutomationService(IAccessibilityServiceClient iAccessibilityServiceClient) throws RemoteException;

    void temporaryEnableAccessibilityStateUntilKeyguardRemoved(ComponentName componentName, boolean z) throws RemoteException;

    /* loaded from: IAccessibilityManager$Stub.class */
    public static abstract class Stub extends Binder implements IAccessibilityManager {
        private static final String DESCRIPTOR = "android.view.accessibility.IAccessibilityManager";
        static final int TRANSACTION_addClient = 1;
        static final int TRANSACTION_sendAccessibilityEvent = 2;
        static final int TRANSACTION_getInstalledAccessibilityServiceList = 3;
        static final int TRANSACTION_getEnabledAccessibilityServiceList = 4;
        static final int TRANSACTION_interrupt = 5;
        static final int TRANSACTION_addAccessibilityInteractionConnection = 6;
        static final int TRANSACTION_removeAccessibilityInteractionConnection = 7;
        static final int TRANSACTION_registerUiTestAutomationService = 8;
        static final int TRANSACTION_unregisterUiTestAutomationService = 9;
        static final int TRANSACTION_temporaryEnableAccessibilityStateUntilKeyguardRemoved = 10;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccessibilityManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityManager)) {
                return (IAccessibilityManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ComponentName _arg0;
            AccessibilityServiceInfo _arg2;
            AccessibilityEvent _arg02;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IAccessibilityManagerClient _arg03 = IAccessibilityManagerClient.Stub.asInterface(data.readStrongBinder());
                    int _arg1 = data.readInt();
                    int _result = addClient(_arg03, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = AccessibilityEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _arg12 = data.readInt();
                    boolean _result2 = sendAccessibilityEvent(_arg02, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    List<AccessibilityServiceInfo> _result3 = getInstalledAccessibilityServiceList(_arg04);
                    reply.writeNoException();
                    reply.writeTypedList(_result3);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    int _arg13 = data.readInt();
                    List<AccessibilityServiceInfo> _result4 = getEnabledAccessibilityServiceList(_arg05, _arg13);
                    reply.writeNoException();
                    reply.writeTypedList(_result4);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    interrupt(_arg06);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg07 = IWindow.Stub.asInterface(data.readStrongBinder());
                    IAccessibilityInteractionConnection _arg14 = IAccessibilityInteractionConnection.Stub.asInterface(data.readStrongBinder());
                    int _arg22 = data.readInt();
                    int _result5 = addAccessibilityInteractionConnection(_arg07, _arg14, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg08 = IWindow.Stub.asInterface(data.readStrongBinder());
                    removeAccessibilityInteractionConnection(_arg08);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg09 = data.readStrongBinder();
                    IAccessibilityServiceClient _arg15 = IAccessibilityServiceClient.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg2 = AccessibilityServiceInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    registerUiTestAutomationService(_arg09, _arg15, _arg2);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    IAccessibilityServiceClient _arg010 = IAccessibilityServiceClient.Stub.asInterface(data.readStrongBinder());
                    unregisterUiTestAutomationService(_arg010);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    boolean _arg16 = 0 != data.readInt();
                    temporaryEnableAccessibilityStateUntilKeyguardRemoved(_arg0, _arg16);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IAccessibilityManager$Stub$Proxy.class */
        private static class Proxy implements IAccessibilityManager {
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

            @Override // android.view.accessibility.IAccessibilityManager
            public int addClient(IAccessibilityManagerClient client, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    _data.writeInt(userId);
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

            @Override // android.view.accessibility.IAccessibilityManager
            public boolean sendAccessibilityEvent(AccessibilityEvent uiEvent, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (uiEvent != null) {
                        _data.writeInt(1);
                        uiEvent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    List<AccessibilityServiceInfo> _result = _reply.createTypedArrayList(AccessibilityServiceInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int feedbackType, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(feedbackType);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    List<AccessibilityServiceInfo> _result = _reply.createTypedArrayList(AccessibilityServiceInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void interrupt(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public int addAccessibilityInteractionConnection(IWindow windowToken, IAccessibilityInteractionConnection connection, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken != null ? windowToken.asBinder() : null);
                    _data.writeStrongBinder(connection != null ? connection.asBinder() : null);
                    _data.writeInt(userId);
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

            @Override // android.view.accessibility.IAccessibilityManager
            public void removeAccessibilityInteractionConnection(IWindow windowToken) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken != null ? windowToken.asBinder() : null);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void registerUiTestAutomationService(IBinder owner, IAccessibilityServiceClient client, AccessibilityServiceInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(owner);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.accessibility.IAccessibilityManager
            public void unregisterUiTestAutomationService(IAccessibilityServiceClient client) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
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

            @Override // android.view.accessibility.IAccessibilityManager
            public void temporaryEnableAccessibilityStateUntilKeyguardRemoved(ComponentName service, boolean touchExplorationEnabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (service != null) {
                        _data.writeInt(1);
                        service.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(touchExplorationEnabled ? 1 : 0);
                    this.mRemote.transact(10, _data, _reply, 0);
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