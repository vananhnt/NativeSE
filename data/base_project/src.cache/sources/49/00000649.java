package android.hardware.display;

import android.hardware.display.IDisplayManagerCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.DisplayInfo;
import android.view.Surface;

/* loaded from: IDisplayManager.class */
public interface IDisplayManager extends IInterface {
    DisplayInfo getDisplayInfo(int i) throws RemoteException;

    int[] getDisplayIds() throws RemoteException;

    void registerCallback(IDisplayManagerCallback iDisplayManagerCallback) throws RemoteException;

    void scanWifiDisplays() throws RemoteException;

    void connectWifiDisplay(String str) throws RemoteException;

    void disconnectWifiDisplay() throws RemoteException;

    void renameWifiDisplay(String str, String str2) throws RemoteException;

    void forgetWifiDisplay(String str) throws RemoteException;

    WifiDisplayStatus getWifiDisplayStatus() throws RemoteException;

    int createVirtualDisplay(IBinder iBinder, String str, String str2, int i, int i2, int i3, Surface surface, int i4) throws RemoteException;

    void releaseVirtualDisplay(IBinder iBinder) throws RemoteException;

    void pauseWifiDisplay() throws RemoteException;

    void resumeWifiDisplay() throws RemoteException;

    /* loaded from: IDisplayManager$Stub.class */
    public static abstract class Stub extends Binder implements IDisplayManager {
        private static final String DESCRIPTOR = "android.hardware.display.IDisplayManager";
        static final int TRANSACTION_getDisplayInfo = 1;
        static final int TRANSACTION_getDisplayIds = 2;
        static final int TRANSACTION_registerCallback = 3;
        static final int TRANSACTION_scanWifiDisplays = 4;
        static final int TRANSACTION_connectWifiDisplay = 5;
        static final int TRANSACTION_disconnectWifiDisplay = 6;
        static final int TRANSACTION_renameWifiDisplay = 7;
        static final int TRANSACTION_forgetWifiDisplay = 8;
        static final int TRANSACTION_getWifiDisplayStatus = 9;
        static final int TRANSACTION_createVirtualDisplay = 10;
        static final int TRANSACTION_releaseVirtualDisplay = 11;
        static final int TRANSACTION_pauseWifiDisplay = 12;
        static final int TRANSACTION_resumeWifiDisplay = 13;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDisplayManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDisplayManager)) {
                return (IDisplayManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Surface _arg6;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    DisplayInfo _result = getDisplayInfo(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _result2 = getDisplayIds();
                    reply.writeNoException();
                    reply.writeIntArray(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    IDisplayManagerCallback _arg02 = IDisplayManagerCallback.Stub.asInterface(data.readStrongBinder());
                    registerCallback(_arg02);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    scanWifiDisplays();
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    connectWifiDisplay(_arg03);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    disconnectWifiDisplay();
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    String _arg1 = data.readString();
                    renameWifiDisplay(_arg04, _arg1);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    forgetWifiDisplay(_arg05);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    WifiDisplayStatus _result3 = getWifiDisplayStatus();
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(1);
                        _result3.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg06 = data.readStrongBinder();
                    String _arg12 = data.readString();
                    String _arg2 = data.readString();
                    int _arg3 = data.readInt();
                    int _arg4 = data.readInt();
                    int _arg5 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg6 = Surface.CREATOR.createFromParcel(data);
                    } else {
                        _arg6 = null;
                    }
                    int _arg7 = data.readInt();
                    int _result4 = createVirtualDisplay(_arg06, _arg12, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg07 = data.readStrongBinder();
                    releaseVirtualDisplay(_arg07);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    pauseWifiDisplay();
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    resumeWifiDisplay();
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IDisplayManager$Stub$Proxy.class */
        public static class Proxy implements IDisplayManager {
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

            @Override // android.hardware.display.IDisplayManager
            public DisplayInfo getDisplayInfo(int displayId) throws RemoteException {
                DisplayInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = DisplayInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.display.IDisplayManager
            public int[] getDisplayIds() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.display.IDisplayManager
            public void registerCallback(IDisplayManagerCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.display.IDisplayManager
            public void scanWifiDisplays() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.display.IDisplayManager
            public void connectWifiDisplay(String address) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
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

            @Override // android.hardware.display.IDisplayManager
            public void disconnectWifiDisplay() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.display.IDisplayManager
            public void renameWifiDisplay(String address, String alias) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeString(alias);
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

            @Override // android.hardware.display.IDisplayManager
            public void forgetWifiDisplay(String address) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
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

            @Override // android.hardware.display.IDisplayManager
            public WifiDisplayStatus getWifiDisplayStatus() throws RemoteException {
                WifiDisplayStatus _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = WifiDisplayStatus.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.display.IDisplayManager
            public int createVirtualDisplay(IBinder token, String packageName, String name, int width, int height, int densityDpi, Surface surface, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(packageName);
                    _data.writeString(name);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeInt(densityDpi);
                    if (surface != null) {
                        _data.writeInt(1);
                        surface.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    this.mRemote.transact(10, _data, _reply, 0);
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

            @Override // android.hardware.display.IDisplayManager
            public void releaseVirtualDisplay(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.display.IDisplayManager
            public void pauseWifiDisplay() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.display.IDisplayManager
            public void resumeWifiDisplay() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
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