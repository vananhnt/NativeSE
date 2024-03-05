package android.app.backup;

import android.app.backup.IFullBackupRestoreObserver;
import android.app.backup.IRestoreSession;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

/* loaded from: IBackupManager.class */
public interface IBackupManager extends IInterface {
    void dataChanged(String str) throws RemoteException;

    void clearBackupData(String str) throws RemoteException;

    void agentConnected(String str, IBinder iBinder) throws RemoteException;

    void agentDisconnected(String str) throws RemoteException;

    void restoreAtInstall(String str, int i) throws RemoteException;

    void setBackupEnabled(boolean z) throws RemoteException;

    void setAutoRestore(boolean z) throws RemoteException;

    void setBackupProvisioned(boolean z) throws RemoteException;

    boolean isBackupEnabled() throws RemoteException;

    boolean setBackupPassword(String str, String str2) throws RemoteException;

    boolean hasBackupPassword() throws RemoteException;

    void backupNow() throws RemoteException;

    void fullBackup(ParcelFileDescriptor parcelFileDescriptor, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, String[] strArr) throws RemoteException;

    void fullRestore(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    void acknowledgeFullBackupOrRestore(int i, boolean z, String str, String str2, IFullBackupRestoreObserver iFullBackupRestoreObserver) throws RemoteException;

    String getCurrentTransport() throws RemoteException;

    String[] listAllTransports() throws RemoteException;

    String selectBackupTransport(String str) throws RemoteException;

    Intent getConfigurationIntent(String str) throws RemoteException;

    String getDestinationString(String str) throws RemoteException;

    IRestoreSession beginRestoreSession(String str, String str2) throws RemoteException;

    void opComplete(int i) throws RemoteException;

    /* loaded from: IBackupManager$Stub.class */
    public static abstract class Stub extends Binder implements IBackupManager {
        private static final String DESCRIPTOR = "android.app.backup.IBackupManager";
        static final int TRANSACTION_dataChanged = 1;
        static final int TRANSACTION_clearBackupData = 2;
        static final int TRANSACTION_agentConnected = 3;
        static final int TRANSACTION_agentDisconnected = 4;
        static final int TRANSACTION_restoreAtInstall = 5;
        static final int TRANSACTION_setBackupEnabled = 6;
        static final int TRANSACTION_setAutoRestore = 7;
        static final int TRANSACTION_setBackupProvisioned = 8;
        static final int TRANSACTION_isBackupEnabled = 9;
        static final int TRANSACTION_setBackupPassword = 10;
        static final int TRANSACTION_hasBackupPassword = 11;
        static final int TRANSACTION_backupNow = 12;
        static final int TRANSACTION_fullBackup = 13;
        static final int TRANSACTION_fullRestore = 14;
        static final int TRANSACTION_acknowledgeFullBackupOrRestore = 15;
        static final int TRANSACTION_getCurrentTransport = 16;
        static final int TRANSACTION_listAllTransports = 17;
        static final int TRANSACTION_selectBackupTransport = 18;
        static final int TRANSACTION_getConfigurationIntent = 19;
        static final int TRANSACTION_getDestinationString = 20;
        static final int TRANSACTION_beginRestoreSession = 21;
        static final int TRANSACTION_opComplete = 22;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBackupManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBackupManager)) {
                return (IBackupManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ParcelFileDescriptor _arg0;
            ParcelFileDescriptor _arg02;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    dataChanged(_arg03);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    clearBackupData(_arg04);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    IBinder _arg1 = data.readStrongBinder();
                    agentConnected(_arg05, _arg1);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    agentDisconnected(_arg06);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    int _arg12 = data.readInt();
                    restoreAtInstall(_arg07, _arg12);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg08 = 0 != data.readInt();
                    setBackupEnabled(_arg08);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg09 = 0 != data.readInt();
                    setAutoRestore(_arg09);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg010 = 0 != data.readInt();
                    setBackupProvisioned(_arg010);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result = isBackupEnabled();
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg011 = data.readString();
                    String _arg13 = data.readString();
                    boolean _result2 = setBackupPassword(_arg011, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result3 = hasBackupPassword();
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    backupNow();
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = ParcelFileDescriptor.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    boolean _arg14 = 0 != data.readInt();
                    boolean _arg2 = 0 != data.readInt();
                    boolean _arg3 = 0 != data.readInt();
                    boolean _arg4 = 0 != data.readInt();
                    boolean _arg5 = 0 != data.readInt();
                    String[] _arg6 = data.createStringArray();
                    fullBackup(_arg02, _arg14, _arg2, _arg3, _arg4, _arg5, _arg6);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = ParcelFileDescriptor.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    fullRestore(_arg0);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    boolean _arg15 = 0 != data.readInt();
                    String _arg22 = data.readString();
                    String _arg32 = data.readString();
                    IFullBackupRestoreObserver _arg42 = IFullBackupRestoreObserver.Stub.asInterface(data.readStrongBinder());
                    acknowledgeFullBackupOrRestore(_arg012, _arg15, _arg22, _arg32, _arg42);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    String _result4 = getCurrentTransport();
                    reply.writeNoException();
                    reply.writeString(_result4);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result5 = listAllTransports();
                    reply.writeNoException();
                    reply.writeStringArray(_result5);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg013 = data.readString();
                    String _result6 = selectBackupTransport(_arg013);
                    reply.writeNoException();
                    reply.writeString(_result6);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg014 = data.readString();
                    Intent _result7 = getConfigurationIntent(_arg014);
                    reply.writeNoException();
                    if (_result7 != null) {
                        reply.writeInt(1);
                        _result7.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg015 = data.readString();
                    String _result8 = getDestinationString(_arg015);
                    reply.writeNoException();
                    reply.writeString(_result8);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg016 = data.readString();
                    String _arg16 = data.readString();
                    IRestoreSession _result9 = beginRestoreSession(_arg016, _arg16);
                    reply.writeNoException();
                    reply.writeStrongBinder(_result9 != null ? _result9.asBinder() : null);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    opComplete(_arg017);
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
        /* loaded from: IBackupManager$Stub$Proxy.class */
        public static class Proxy implements IBackupManager {
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

            @Override // android.app.backup.IBackupManager
            public void dataChanged(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
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

            @Override // android.app.backup.IBackupManager
            public void clearBackupData(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
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

            @Override // android.app.backup.IBackupManager
            public void agentConnected(String packageName, IBinder agent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(agent);
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

            @Override // android.app.backup.IBackupManager
            public void agentDisconnected(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
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

            @Override // android.app.backup.IBackupManager
            public void restoreAtInstall(String packageName, int token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(token);
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

            @Override // android.app.backup.IBackupManager
            public void setBackupEnabled(boolean isEnabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(isEnabled ? 1 : 0);
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

            @Override // android.app.backup.IBackupManager
            public void setAutoRestore(boolean doAutoRestore) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(doAutoRestore ? 1 : 0);
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

            @Override // android.app.backup.IBackupManager
            public void setBackupProvisioned(boolean isProvisioned) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(isProvisioned ? 1 : 0);
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

            @Override // android.app.backup.IBackupManager
            public boolean isBackupEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.backup.IBackupManager
            public boolean setBackupPassword(String currentPw, String newPw) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(currentPw);
                    _data.writeString(newPw);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.backup.IBackupManager
            public boolean hasBackupPassword() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.backup.IBackupManager
            public void backupNow() throws RemoteException {
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

            @Override // android.app.backup.IBackupManager
            public void fullBackup(ParcelFileDescriptor fd, boolean includeApks, boolean includeObbs, boolean includeShared, boolean allApps, boolean allIncludesSystem, String[] packageNames) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (fd != null) {
                        _data.writeInt(1);
                        fd.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(includeApks ? 1 : 0);
                    _data.writeInt(includeObbs ? 1 : 0);
                    _data.writeInt(includeShared ? 1 : 0);
                    _data.writeInt(allApps ? 1 : 0);
                    _data.writeInt(allIncludesSystem ? 1 : 0);
                    _data.writeStringArray(packageNames);
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

            @Override // android.app.backup.IBackupManager
            public void fullRestore(ParcelFileDescriptor fd) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (fd != null) {
                        _data.writeInt(1);
                        fd.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.app.backup.IBackupManager
            public void acknowledgeFullBackupOrRestore(int token, boolean allow, String curPassword, String encryptionPassword, IFullBackupRestoreObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeInt(allow ? 1 : 0);
                    _data.writeString(curPassword);
                    _data.writeString(encryptionPassword);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
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

            @Override // android.app.backup.IBackupManager
            public String getCurrentTransport() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IBackupManager
            public String[] listAllTransports() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IBackupManager
            public String selectBackupTransport(String transport) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(transport);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IBackupManager
            public Intent getConfigurationIntent(String transport) throws RemoteException {
                Intent _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(transport);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Intent.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.backup.IBackupManager
            public String getDestinationString(String transport) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(transport);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IBackupManager
            public IRestoreSession beginRestoreSession(String packageName, String transportID) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(transportID);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    IRestoreSession _result = IRestoreSession.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.backup.IBackupManager
            public void opComplete(int token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(token);
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