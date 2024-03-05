package android.app;

import android.app.ITransientNotification;
import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.service.notification.INotificationListener;
import android.service.notification.StatusBarNotification;

/* loaded from: INotificationManager.class */
public interface INotificationManager extends IInterface {
    void cancelAllNotifications(String str, int i) throws RemoteException;

    void enqueueToast(String str, ITransientNotification iTransientNotification, int i) throws RemoteException;

    void cancelToast(String str, ITransientNotification iTransientNotification) throws RemoteException;

    void enqueueNotificationWithTag(String str, String str2, String str3, int i, Notification notification, int[] iArr, int i2) throws RemoteException;

    void cancelNotificationWithTag(String str, String str2, int i, int i2) throws RemoteException;

    void setNotificationsEnabledForPackage(String str, int i, boolean z) throws RemoteException;

    boolean areNotificationsEnabledForPackage(String str, int i) throws RemoteException;

    StatusBarNotification[] getActiveNotifications(String str) throws RemoteException;

    StatusBarNotification[] getHistoricalNotifications(String str, int i) throws RemoteException;

    void registerListener(INotificationListener iNotificationListener, ComponentName componentName, int i) throws RemoteException;

    void unregisterListener(INotificationListener iNotificationListener, int i) throws RemoteException;

    void cancelNotificationFromListener(INotificationListener iNotificationListener, String str, String str2, int i) throws RemoteException;

    void cancelAllNotificationsFromListener(INotificationListener iNotificationListener) throws RemoteException;

    StatusBarNotification[] getActiveNotificationsFromListener(INotificationListener iNotificationListener) throws RemoteException;

    /* loaded from: INotificationManager$Stub.class */
    public static abstract class Stub extends Binder implements INotificationManager {
        private static final String DESCRIPTOR = "android.app.INotificationManager";
        static final int TRANSACTION_cancelAllNotifications = 1;
        static final int TRANSACTION_enqueueToast = 2;
        static final int TRANSACTION_cancelToast = 3;
        static final int TRANSACTION_enqueueNotificationWithTag = 4;
        static final int TRANSACTION_cancelNotificationWithTag = 5;
        static final int TRANSACTION_setNotificationsEnabledForPackage = 6;
        static final int TRANSACTION_areNotificationsEnabledForPackage = 7;
        static final int TRANSACTION_getActiveNotifications = 8;
        static final int TRANSACTION_getHistoricalNotifications = 9;
        static final int TRANSACTION_registerListener = 10;
        static final int TRANSACTION_unregisterListener = 11;
        static final int TRANSACTION_cancelNotificationFromListener = 12;
        static final int TRANSACTION_cancelAllNotificationsFromListener = 13;
        static final int TRANSACTION_getActiveNotificationsFromListener = 14;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INotificationManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INotificationManager)) {
                return (INotificationManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ComponentName _arg1;
            Notification _arg4;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    int _arg12 = data.readInt();
                    cancelAllNotifications(_arg0, _arg12);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    ITransientNotification _arg13 = ITransientNotification.Stub.asInterface(data.readStrongBinder());
                    int _arg2 = data.readInt();
                    enqueueToast(_arg02, _arg13, _arg2);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    ITransientNotification _arg14 = ITransientNotification.Stub.asInterface(data.readStrongBinder());
                    cancelToast(_arg03, _arg14);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    String _arg15 = data.readString();
                    String _arg22 = data.readString();
                    int _arg3 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg4 = Notification.CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    int[] _arg5 = data.createIntArray();
                    int _arg6 = data.readInt();
                    enqueueNotificationWithTag(_arg04, _arg15, _arg22, _arg3, _arg4, _arg5, _arg6);
                    reply.writeNoException();
                    reply.writeIntArray(_arg5);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    String _arg16 = data.readString();
                    int _arg23 = data.readInt();
                    int _arg32 = data.readInt();
                    cancelNotificationWithTag(_arg05, _arg16, _arg23, _arg32);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    int _arg17 = data.readInt();
                    boolean _arg24 = 0 != data.readInt();
                    setNotificationsEnabledForPackage(_arg06, _arg17, _arg24);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    int _arg18 = data.readInt();
                    boolean _result = areNotificationsEnabledForPackage(_arg07, _arg18);
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg08 = data.readString();
                    StatusBarNotification[] _result2 = getActiveNotifications(_arg08);
                    reply.writeNoException();
                    reply.writeTypedArray(_result2, 1);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg09 = data.readString();
                    int _arg19 = data.readInt();
                    StatusBarNotification[] _result3 = getHistoricalNotifications(_arg09, _arg19);
                    reply.writeNoException();
                    reply.writeTypedArray(_result3, 1);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    INotificationListener _arg010 = INotificationListener.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg1 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    int _arg25 = data.readInt();
                    registerListener(_arg010, _arg1, _arg25);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    INotificationListener _arg011 = INotificationListener.Stub.asInterface(data.readStrongBinder());
                    int _arg110 = data.readInt();
                    unregisterListener(_arg011, _arg110);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    INotificationListener _arg012 = INotificationListener.Stub.asInterface(data.readStrongBinder());
                    String _arg111 = data.readString();
                    String _arg26 = data.readString();
                    int _arg33 = data.readInt();
                    cancelNotificationFromListener(_arg012, _arg111, _arg26, _arg33);
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    INotificationListener _arg013 = INotificationListener.Stub.asInterface(data.readStrongBinder());
                    cancelAllNotificationsFromListener(_arg013);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    INotificationListener _arg014 = INotificationListener.Stub.asInterface(data.readStrongBinder());
                    StatusBarNotification[] _result4 = getActiveNotificationsFromListener(_arg014);
                    reply.writeNoException();
                    reply.writeTypedArray(_result4, 1);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: INotificationManager$Stub$Proxy.class */
        private static class Proxy implements INotificationManager {
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

            @Override // android.app.INotificationManager
            public void cancelAllNotifications(String pkg, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeInt(userId);
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

            @Override // android.app.INotificationManager
            public void enqueueToast(String pkg, ITransientNotification callback, int duration) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(duration);
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

            @Override // android.app.INotificationManager
            public void cancelToast(String pkg, ITransientNotification callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
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

            @Override // android.app.INotificationManager
            public void enqueueNotificationWithTag(String pkg, String basePkg, String tag, int id, Notification notification, int[] idReceived, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(basePkg);
                    _data.writeString(tag);
                    _data.writeInt(id);
                    if (notification != null) {
                        _data.writeInt(1);
                        notification.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeIntArray(idReceived);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    _reply.readIntArray(idReceived);
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.INotificationManager
            public void cancelNotificationWithTag(String pkg, String tag, int id, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(tag);
                    _data.writeInt(id);
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

            @Override // android.app.INotificationManager
            public void setNotificationsEnabledForPackage(String pkg, int uid, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeInt(uid);
                    _data.writeInt(enabled ? 1 : 0);
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

            @Override // android.app.INotificationManager
            public boolean areNotificationsEnabledForPackage(String pkg, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeInt(uid);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.INotificationManager
            public StatusBarNotification[] getActiveNotifications(String callingPkg) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    StatusBarNotification[] _result = (StatusBarNotification[]) _reply.createTypedArray(StatusBarNotification.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.INotificationManager
            public StatusBarNotification[] getHistoricalNotifications(String callingPkg, int count) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeInt(count);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    StatusBarNotification[] _result = (StatusBarNotification[]) _reply.createTypedArray(StatusBarNotification.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.INotificationManager
            public void registerListener(INotificationListener listener, ComponentName component, int userid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (component != null) {
                        _data.writeInt(1);
                        component.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userid);
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

            @Override // android.app.INotificationManager
            public void unregisterListener(INotificationListener listener, int userid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    _data.writeInt(userid);
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

            @Override // android.app.INotificationManager
            public void cancelNotificationFromListener(INotificationListener token, String pkg, String tag, int id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token != null ? token.asBinder() : null);
                    _data.writeString(pkg);
                    _data.writeString(tag);
                    _data.writeInt(id);
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

            @Override // android.app.INotificationManager
            public void cancelAllNotificationsFromListener(INotificationListener token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token != null ? token.asBinder() : null);
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

            @Override // android.app.INotificationManager
            public StatusBarNotification[] getActiveNotificationsFromListener(INotificationListener token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token != null ? token.asBinder() : null);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    StatusBarNotification[] _result = (StatusBarNotification[]) _reply.createTypedArray(StatusBarNotification.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}