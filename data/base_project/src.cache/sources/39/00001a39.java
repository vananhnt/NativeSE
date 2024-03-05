package com.android.internal.statusbar;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;

/* loaded from: IStatusBar.class */
public interface IStatusBar extends IInterface {
    void setIcon(int i, StatusBarIcon statusBarIcon) throws RemoteException;

    void removeIcon(int i) throws RemoteException;

    void addNotification(IBinder iBinder, StatusBarNotification statusBarNotification) throws RemoteException;

    void updateNotification(IBinder iBinder, StatusBarNotification statusBarNotification) throws RemoteException;

    void removeNotification(IBinder iBinder) throws RemoteException;

    void disable(int i) throws RemoteException;

    void animateExpandNotificationsPanel() throws RemoteException;

    void animateExpandSettingsPanel() throws RemoteException;

    void animateCollapsePanels() throws RemoteException;

    void setSystemUiVisibility(int i, int i2) throws RemoteException;

    void topAppWindowChanged(boolean z) throws RemoteException;

    void setImeWindowStatus(IBinder iBinder, int i, int i2) throws RemoteException;

    void setHardKeyboardStatus(boolean z, boolean z2) throws RemoteException;

    void toggleRecentApps() throws RemoteException;

    void preloadRecentApps() throws RemoteException;

    void cancelPreloadRecentApps() throws RemoteException;

    void setWindowState(int i, int i2) throws RemoteException;

    /* loaded from: IStatusBar$Stub.class */
    public static abstract class Stub extends Binder implements IStatusBar {
        private static final String DESCRIPTOR = "com.android.internal.statusbar.IStatusBar";
        static final int TRANSACTION_setIcon = 1;
        static final int TRANSACTION_removeIcon = 2;
        static final int TRANSACTION_addNotification = 3;
        static final int TRANSACTION_updateNotification = 4;
        static final int TRANSACTION_removeNotification = 5;
        static final int TRANSACTION_disable = 6;
        static final int TRANSACTION_animateExpandNotificationsPanel = 7;
        static final int TRANSACTION_animateExpandSettingsPanel = 8;
        static final int TRANSACTION_animateCollapsePanels = 9;
        static final int TRANSACTION_setSystemUiVisibility = 10;
        static final int TRANSACTION_topAppWindowChanged = 11;
        static final int TRANSACTION_setImeWindowStatus = 12;
        static final int TRANSACTION_setHardKeyboardStatus = 13;
        static final int TRANSACTION_toggleRecentApps = 14;
        static final int TRANSACTION_preloadRecentApps = 15;
        static final int TRANSACTION_cancelPreloadRecentApps = 16;
        static final int TRANSACTION_setWindowState = 17;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IStatusBar asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IStatusBar)) {
                return (IStatusBar) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            StatusBarNotification _arg1;
            StatusBarNotification _arg12;
            StatusBarIcon _arg13;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg13 = StatusBarIcon.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    setIcon(_arg0, _arg13);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    removeIcon(_arg02);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg03 = data.readStrongBinder();
                    if (0 != data.readInt()) {
                        _arg12 = StatusBarNotification.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    addNotification(_arg03, _arg12);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg04 = data.readStrongBinder();
                    if (0 != data.readInt()) {
                        _arg1 = StatusBarNotification.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    updateNotification(_arg04, _arg1);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg05 = data.readStrongBinder();
                    removeNotification(_arg05);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    disable(_arg06);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    animateExpandNotificationsPanel();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    animateExpandSettingsPanel();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    animateCollapsePanels();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _arg14 = data.readInt();
                    setSystemUiVisibility(_arg07, _arg14);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg08 = 0 != data.readInt();
                    topAppWindowChanged(_arg08);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg09 = data.readStrongBinder();
                    int _arg15 = data.readInt();
                    int _arg2 = data.readInt();
                    setImeWindowStatus(_arg09, _arg15, _arg2);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg010 = 0 != data.readInt();
                    boolean _arg16 = 0 != data.readInt();
                    setHardKeyboardStatus(_arg010, _arg16);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    toggleRecentApps();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    preloadRecentApps();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    cancelPreloadRecentApps();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    int _arg17 = data.readInt();
                    setWindowState(_arg011, _arg17);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IStatusBar$Stub$Proxy.class */
        private static class Proxy implements IStatusBar {
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

            @Override // com.android.internal.statusbar.IStatusBar
            public void setIcon(int index, StatusBarIcon icon) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(index);
                    if (icon != null) {
                        _data.writeInt(1);
                        icon.writeToParcel(_data, 0);
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

            @Override // com.android.internal.statusbar.IStatusBar
            public void removeIcon(int index) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(index);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void addNotification(IBinder key, StatusBarNotification notification) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(key);
                    if (notification != null) {
                        _data.writeInt(1);
                        notification.writeToParcel(_data, 0);
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

            @Override // com.android.internal.statusbar.IStatusBar
            public void updateNotification(IBinder key, StatusBarNotification notification) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(key);
                    if (notification != null) {
                        _data.writeInt(1);
                        notification.writeToParcel(_data, 0);
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

            @Override // com.android.internal.statusbar.IStatusBar
            public void removeNotification(IBinder key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(key);
                    this.mRemote.transact(5, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void disable(int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    this.mRemote.transact(6, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void animateExpandNotificationsPanel() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void animateExpandSettingsPanel() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void animateCollapsePanels() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void setSystemUiVisibility(int vis, int mask) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(vis);
                    _data.writeInt(mask);
                    this.mRemote.transact(10, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void topAppWindowChanged(boolean menuVisible) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(menuVisible ? 1 : 0);
                    this.mRemote.transact(11, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void setImeWindowStatus(IBinder token, int vis, int backDisposition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(vis);
                    _data.writeInt(backDisposition);
                    this.mRemote.transact(12, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void setHardKeyboardStatus(boolean available, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(available ? 1 : 0);
                    _data.writeInt(enabled ? 1 : 0);
                    this.mRemote.transact(13, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void toggleRecentApps() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void preloadRecentApps() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void cancelPreloadRecentApps() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBar
            public void setWindowState(int window, int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(window);
                    _data.writeInt(state);
                    this.mRemote.transact(17, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}