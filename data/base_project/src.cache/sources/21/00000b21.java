package android.os;

/* loaded from: IPowerManager.class */
public interface IPowerManager extends IInterface {
    void acquireWakeLock(IBinder iBinder, int i, String str, String str2, WorkSource workSource) throws RemoteException;

    void acquireWakeLockWithUid(IBinder iBinder, int i, String str, String str2, int i2) throws RemoteException;

    void releaseWakeLock(IBinder iBinder, int i) throws RemoteException;

    void updateWakeLockWorkSource(IBinder iBinder, WorkSource workSource) throws RemoteException;

    boolean isWakeLockLevelSupported(int i) throws RemoteException;

    void userActivity(long j, int i, int i2) throws RemoteException;

    void wakeUp(long j) throws RemoteException;

    void goToSleep(long j, int i) throws RemoteException;

    void nap(long j) throws RemoteException;

    boolean isScreenOn() throws RemoteException;

    void reboot(boolean z, String str, boolean z2) throws RemoteException;

    void shutdown(boolean z, boolean z2) throws RemoteException;

    void crash(String str) throws RemoteException;

    void setStayOnSetting(int i) throws RemoteException;

    void setMaximumScreenOffTimeoutFromDeviceAdmin(int i) throws RemoteException;

    void setTemporaryScreenBrightnessSettingOverride(int i) throws RemoteException;

    void setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(float f) throws RemoteException;

    void setAttentionLight(boolean z, int i) throws RemoteException;

    /* loaded from: IPowerManager$Stub.class */
    public static abstract class Stub extends Binder implements IPowerManager {
        private static final String DESCRIPTOR = "android.os.IPowerManager";
        static final int TRANSACTION_acquireWakeLock = 1;
        static final int TRANSACTION_acquireWakeLockWithUid = 2;
        static final int TRANSACTION_releaseWakeLock = 3;
        static final int TRANSACTION_updateWakeLockWorkSource = 4;
        static final int TRANSACTION_isWakeLockLevelSupported = 5;
        static final int TRANSACTION_userActivity = 6;
        static final int TRANSACTION_wakeUp = 7;
        static final int TRANSACTION_goToSleep = 8;
        static final int TRANSACTION_nap = 9;
        static final int TRANSACTION_isScreenOn = 10;
        static final int TRANSACTION_reboot = 11;
        static final int TRANSACTION_shutdown = 12;
        static final int TRANSACTION_crash = 13;
        static final int TRANSACTION_setStayOnSetting = 14;
        static final int TRANSACTION_setMaximumScreenOffTimeoutFromDeviceAdmin = 15;
        static final int TRANSACTION_setTemporaryScreenBrightnessSettingOverride = 16;
        static final int TRANSACTION_setTemporaryScreenAutoBrightnessAdjustmentSettingOverride = 17;
        static final int TRANSACTION_setAttentionLight = 18;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPowerManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPowerManager)) {
                return (IPowerManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            WorkSource _arg1;
            WorkSource _arg4;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg0 = data.readStrongBinder();
                    int _arg12 = data.readInt();
                    String _arg2 = data.readString();
                    String _arg3 = data.readString();
                    if (0 != data.readInt()) {
                        _arg4 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    acquireWakeLock(_arg0, _arg12, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg02 = data.readStrongBinder();
                    int _arg13 = data.readInt();
                    String _arg22 = data.readString();
                    String _arg32 = data.readString();
                    int _arg42 = data.readInt();
                    acquireWakeLockWithUid(_arg02, _arg13, _arg22, _arg32, _arg42);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg03 = data.readStrongBinder();
                    int _arg14 = data.readInt();
                    releaseWakeLock(_arg03, _arg14);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg04 = data.readStrongBinder();
                    if (0 != data.readInt()) {
                        _arg1 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    updateWakeLockWorkSource(_arg04, _arg1);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    boolean _result = isWakeLockLevelSupported(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg06 = data.readLong();
                    int _arg15 = data.readInt();
                    int _arg23 = data.readInt();
                    userActivity(_arg06, _arg15, _arg23);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg07 = data.readLong();
                    wakeUp(_arg07);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg08 = data.readLong();
                    int _arg16 = data.readInt();
                    goToSleep(_arg08, _arg16);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg09 = data.readLong();
                    nap(_arg09);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result2 = isScreenOn();
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg010 = 0 != data.readInt();
                    String _arg17 = data.readString();
                    boolean _arg24 = 0 != data.readInt();
                    reboot(_arg010, _arg17, _arg24);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg011 = 0 != data.readInt();
                    boolean _arg18 = 0 != data.readInt();
                    shutdown(_arg011, _arg18);
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg012 = data.readString();
                    crash(_arg012);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    setStayOnSetting(_arg013);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    setMaximumScreenOffTimeoutFromDeviceAdmin(_arg014);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    setTemporaryScreenBrightnessSettingOverride(_arg015);
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    float _arg016 = data.readFloat();
                    setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(_arg016);
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg017 = 0 != data.readInt();
                    int _arg19 = data.readInt();
                    setAttentionLight(_arg017, _arg19);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IPowerManager$Stub$Proxy.class */
        private static class Proxy implements IPowerManager {
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

            @Override // android.os.IPowerManager
            public void acquireWakeLock(IBinder lock, int flags, String tag, String packageName, WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeInt(flags);
                    _data.writeString(tag);
                    _data.writeString(packageName);
                    if (ws != null) {
                        _data.writeInt(1);
                        ws.writeToParcel(_data, 0);
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

            @Override // android.os.IPowerManager
            public void acquireWakeLockWithUid(IBinder lock, int flags, String tag, String packageName, int uidtoblame) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeInt(flags);
                    _data.writeString(tag);
                    _data.writeString(packageName);
                    _data.writeInt(uidtoblame);
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

            @Override // android.os.IPowerManager
            public void releaseWakeLock(IBinder lock, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeInt(flags);
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

            @Override // android.os.IPowerManager
            public void updateWakeLockWorkSource(IBinder lock, WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    if (ws != null) {
                        _data.writeInt(1);
                        ws.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.os.IPowerManager
            public boolean isWakeLockLevelSupported(int level) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(level);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IPowerManager
            public void userActivity(long time, int event, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    _data.writeInt(event);
                    _data.writeInt(flags);
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

            @Override // android.os.IPowerManager
            public void wakeUp(long time) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
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

            @Override // android.os.IPowerManager
            public void goToSleep(long time, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    _data.writeInt(reason);
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

            @Override // android.os.IPowerManager
            public void nap(long time) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
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

            @Override // android.os.IPowerManager
            public boolean isScreenOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IPowerManager
            public void reboot(boolean confirm, String reason, boolean wait) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(confirm ? 1 : 0);
                    _data.writeString(reason);
                    _data.writeInt(wait ? 1 : 0);
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

            @Override // android.os.IPowerManager
            public void shutdown(boolean confirm, boolean wait) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(confirm ? 1 : 0);
                    _data.writeInt(wait ? 1 : 0);
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

            @Override // android.os.IPowerManager
            public void crash(String message) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(message);
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

            @Override // android.os.IPowerManager
            public void setStayOnSetting(int val) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(val);
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

            @Override // android.os.IPowerManager
            public void setMaximumScreenOffTimeoutFromDeviceAdmin(int timeMs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(timeMs);
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

            @Override // android.os.IPowerManager
            public void setTemporaryScreenBrightnessSettingOverride(int brightness) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(brightness);
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

            @Override // android.os.IPowerManager
            public void setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(float adj) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(adj);
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

            @Override // android.os.IPowerManager
            public void setAttentionLight(boolean on, int color) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(on ? 1 : 0);
                    _data.writeInt(color);
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
        }
    }
}