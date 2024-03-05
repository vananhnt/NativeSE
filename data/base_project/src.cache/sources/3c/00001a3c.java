package com.android.internal.statusbar;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import com.android.internal.statusbar.IStatusBar;
import java.util.ArrayList;
import java.util.List;

/* loaded from: IStatusBarService.class */
public interface IStatusBarService extends IInterface {
    void expandNotificationsPanel() throws RemoteException;

    void collapsePanels() throws RemoteException;

    void disable(int i, IBinder iBinder, String str) throws RemoteException;

    void setIcon(String str, String str2, int i, int i2, String str3) throws RemoteException;

    void setIconVisibility(String str, boolean z) throws RemoteException;

    void removeIcon(String str) throws RemoteException;

    void topAppWindowChanged(boolean z) throws RemoteException;

    void setImeWindowStatus(IBinder iBinder, int i, int i2) throws RemoteException;

    void expandSettingsPanel() throws RemoteException;

    void setCurrentUser(int i) throws RemoteException;

    void registerStatusBar(IStatusBar iStatusBar, StatusBarIconList statusBarIconList, List<IBinder> list, List<StatusBarNotification> list2, int[] iArr, List<IBinder> list3) throws RemoteException;

    void onPanelRevealed() throws RemoteException;

    void onNotificationClick(String str, String str2, int i) throws RemoteException;

    void onNotificationError(String str, String str2, int i, int i2, int i3, String str3) throws RemoteException;

    void onClearAllNotifications() throws RemoteException;

    void onNotificationClear(String str, String str2, int i) throws RemoteException;

    void setSystemUiVisibility(int i, int i2) throws RemoteException;

    void setHardKeyboardEnabled(boolean z) throws RemoteException;

    void toggleRecentApps() throws RemoteException;

    void preloadRecentApps() throws RemoteException;

    void cancelPreloadRecentApps() throws RemoteException;

    void setWindowState(int i, int i2) throws RemoteException;

    /* loaded from: IStatusBarService$Stub.class */
    public static abstract class Stub extends Binder implements IStatusBarService {
        private static final String DESCRIPTOR = "com.android.internal.statusbar.IStatusBarService";
        static final int TRANSACTION_expandNotificationsPanel = 1;
        static final int TRANSACTION_collapsePanels = 2;
        static final int TRANSACTION_disable = 3;
        static final int TRANSACTION_setIcon = 4;
        static final int TRANSACTION_setIconVisibility = 5;
        static final int TRANSACTION_removeIcon = 6;
        static final int TRANSACTION_topAppWindowChanged = 7;
        static final int TRANSACTION_setImeWindowStatus = 8;
        static final int TRANSACTION_expandSettingsPanel = 9;
        static final int TRANSACTION_setCurrentUser = 10;
        static final int TRANSACTION_registerStatusBar = 11;
        static final int TRANSACTION_onPanelRevealed = 12;
        static final int TRANSACTION_onNotificationClick = 13;
        static final int TRANSACTION_onNotificationError = 14;
        static final int TRANSACTION_onClearAllNotifications = 15;
        static final int TRANSACTION_onNotificationClear = 16;
        static final int TRANSACTION_setSystemUiVisibility = 17;
        static final int TRANSACTION_setHardKeyboardEnabled = 18;
        static final int TRANSACTION_toggleRecentApps = 19;
        static final int TRANSACTION_preloadRecentApps = 20;
        static final int TRANSACTION_cancelPreloadRecentApps = 21;
        static final int TRANSACTION_setWindowState = 22;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IStatusBarService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IStatusBarService)) {
                return (IStatusBarService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int[] _arg4;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    expandNotificationsPanel();
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    collapsePanels();
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    IBinder _arg1 = data.readStrongBinder();
                    String _arg2 = data.readString();
                    disable(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    String _arg12 = data.readString();
                    int _arg22 = data.readInt();
                    int _arg3 = data.readInt();
                    String _arg42 = data.readString();
                    setIcon(_arg02, _arg12, _arg22, _arg3, _arg42);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    boolean _arg13 = 0 != data.readInt();
                    setIconVisibility(_arg03, _arg13);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    removeIcon(_arg04);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg05 = 0 != data.readInt();
                    topAppWindowChanged(_arg05);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg06 = data.readStrongBinder();
                    int _arg14 = data.readInt();
                    int _arg23 = data.readInt();
                    setImeWindowStatus(_arg06, _arg14, _arg23);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    expandSettingsPanel();
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    setCurrentUser(_arg07);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    IStatusBar _arg08 = IStatusBar.Stub.asInterface(data.readStrongBinder());
                    StatusBarIconList _arg15 = new StatusBarIconList();
                    List<IBinder> _arg24 = new ArrayList<>();
                    ArrayList arrayList = new ArrayList();
                    int _arg4_length = data.readInt();
                    if (_arg4_length < 0) {
                        _arg4 = null;
                    } else {
                        _arg4 = new int[_arg4_length];
                    }
                    List<IBinder> _arg5 = new ArrayList<>();
                    registerStatusBar(_arg08, _arg15, _arg24, arrayList, _arg4, _arg5);
                    reply.writeNoException();
                    if (_arg15 != null) {
                        reply.writeInt(1);
                        _arg15.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    reply.writeBinderList(_arg24);
                    reply.writeTypedList(arrayList);
                    reply.writeIntArray(_arg4);
                    reply.writeBinderList(_arg5);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    onPanelRevealed();
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg09 = data.readString();
                    String _arg16 = data.readString();
                    int _arg25 = data.readInt();
                    onNotificationClick(_arg09, _arg16, _arg25);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    String _arg17 = data.readString();
                    int _arg26 = data.readInt();
                    int _arg32 = data.readInt();
                    int _arg43 = data.readInt();
                    onNotificationError(_arg010, _arg17, _arg26, _arg32, _arg43, data.readString());
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    onClearAllNotifications();
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg011 = data.readString();
                    String _arg18 = data.readString();
                    int _arg27 = data.readInt();
                    onNotificationClear(_arg011, _arg18, _arg27);
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    int _arg19 = data.readInt();
                    setSystemUiVisibility(_arg012, _arg19);
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg013 = 0 != data.readInt();
                    setHardKeyboardEnabled(_arg013);
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    toggleRecentApps();
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    preloadRecentApps();
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    cancelPreloadRecentApps();
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    int _arg110 = data.readInt();
                    setWindowState(_arg014, _arg110);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IStatusBarService$Stub$Proxy.class */
        private static class Proxy implements IStatusBarService {
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void expandNotificationsPanel() throws RemoteException {
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void collapsePanels() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBarService
            public void disable(int what, IBinder token, String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(what);
                    _data.writeStrongBinder(token);
                    _data.writeString(pkg);
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void setIcon(String slot, String iconPackage, int iconId, int iconLevel, String contentDescription) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(slot);
                    _data.writeString(iconPackage);
                    _data.writeInt(iconId);
                    _data.writeInt(iconLevel);
                    _data.writeString(contentDescription);
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void setIconVisibility(String slot, boolean visible) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(slot);
                    _data.writeInt(visible ? 1 : 0);
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void removeIcon(String slot) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(slot);
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void topAppWindowChanged(boolean menuVisible) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(menuVisible ? 1 : 0);
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void setImeWindowStatus(IBinder token, int vis, int backDisposition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(vis);
                    _data.writeInt(backDisposition);
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void expandSettingsPanel() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void setCurrentUser(int newUserId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(newUserId);
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void registerStatusBar(IStatusBar callbacks, StatusBarIconList iconList, List<IBinder> notificationKeys, List<StatusBarNotification> notifications, int[] switches, List<IBinder> binders) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callbacks != null ? callbacks.asBinder() : null);
                    if (switches == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(switches.length);
                    }
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        iconList.readFromParcel(_reply);
                    }
                    _reply.readBinderList(notificationKeys);
                    _reply.readTypedList(notifications, StatusBarNotification.CREATOR);
                    _reply.readIntArray(switches);
                    _reply.readBinderList(binders);
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBarService
            public void onPanelRevealed() throws RemoteException {
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void onNotificationClick(String pkg, String tag, int id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(tag);
                    _data.writeInt(id);
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

            @Override // com.android.internal.statusbar.IStatusBarService
            public void onNotificationError(String pkg, String tag, int id, int uid, int initialPid, String message) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(tag);
                    _data.writeInt(id);
                    _data.writeInt(uid);
                    _data.writeInt(initialPid);
                    _data.writeString(message);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBarService
            public void onClearAllNotifications() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBarService
            public void onNotificationClear(String pkg, String tag, int id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(tag);
                    _data.writeInt(id);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBarService
            public void setSystemUiVisibility(int vis, int mask) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(vis);
                    _data.writeInt(mask);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBarService
            public void setHardKeyboardEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled ? 1 : 0);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBarService
            public void toggleRecentApps() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBarService
            public void preloadRecentApps() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBarService
            public void cancelPreloadRecentApps() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.statusbar.IStatusBarService
            public void setWindowState(int window, int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(window);
                    _data.writeInt(state);
                    this.mRemote.transact(22, _data, _reply, 0);
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