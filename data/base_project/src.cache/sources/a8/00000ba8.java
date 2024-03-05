package android.os.storage;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.storage.IMountServiceListener;
import android.os.storage.IMountShutdownObserver;
import android.os.storage.IObbActionListener;

/* loaded from: IMountService.class */
public interface IMountService extends IInterface {
    public static final int ENCRYPTION_STATE_NONE = 1;
    public static final int ENCRYPTION_STATE_OK = 0;
    public static final int ENCRYPTION_STATE_ERROR_UNKNOWN = -1;
    public static final int ENCRYPTION_STATE_ERROR_INCOMPLETE = -2;

    int createSecureContainer(String str, int i, String str2, String str3, int i2, boolean z) throws RemoteException;

    int destroySecureContainer(String str, boolean z) throws RemoteException;

    int finalizeSecureContainer(String str) throws RemoteException;

    void finishMediaUpdate() throws RemoteException;

    int formatVolume(String str) throws RemoteException;

    String getMountedObbPath(String str) throws RemoteException;

    String[] getSecureContainerList() throws RemoteException;

    String getSecureContainerPath(String str) throws RemoteException;

    int[] getStorageUsers(String str) throws RemoteException;

    String getVolumeState(String str) throws RemoteException;

    boolean isObbMounted(String str) throws RemoteException;

    boolean isSecureContainerMounted(String str) throws RemoteException;

    boolean isUsbMassStorageConnected() throws RemoteException;

    boolean isUsbMassStorageEnabled() throws RemoteException;

    void mountObb(String str, String str2, String str3, IObbActionListener iObbActionListener, int i) throws RemoteException;

    int mountSecureContainer(String str, String str2, int i) throws RemoteException;

    int mountVolume(String str) throws RemoteException;

    void registerListener(IMountServiceListener iMountServiceListener) throws RemoteException;

    int renameSecureContainer(String str, String str2) throws RemoteException;

    void setUsbMassStorageEnabled(boolean z) throws RemoteException;

    void shutdown(IMountShutdownObserver iMountShutdownObserver) throws RemoteException;

    void unmountObb(String str, boolean z, IObbActionListener iObbActionListener, int i) throws RemoteException;

    int unmountSecureContainer(String str, boolean z) throws RemoteException;

    void unmountVolume(String str, boolean z, boolean z2) throws RemoteException;

    void unregisterListener(IMountServiceListener iMountServiceListener) throws RemoteException;

    boolean isExternalStorageEmulated() throws RemoteException;

    int getEncryptionState() throws RemoteException;

    int decryptStorage(String str) throws RemoteException;

    int encryptStorage(String str) throws RemoteException;

    int changeEncryptionPassword(String str) throws RemoteException;

    int verifyEncryptionPassword(String str) throws RemoteException;

    StorageVolume[] getVolumeList() throws RemoteException;

    String getSecureContainerFilesystemPath(String str) throws RemoteException;

    int fixPermissionsSecureContainer(String str, int i, String str2) throws RemoteException;

    int mkdirs(String str, String str2) throws RemoteException;

    /* loaded from: IMountService$Stub.class */
    public static abstract class Stub extends Binder implements IMountService {
        private static final String DESCRIPTOR = "IMountService";
        static final int TRANSACTION_registerListener = 1;
        static final int TRANSACTION_unregisterListener = 2;
        static final int TRANSACTION_isUsbMassStorageConnected = 3;
        static final int TRANSACTION_setUsbMassStorageEnabled = 4;
        static final int TRANSACTION_isUsbMassStorageEnabled = 5;
        static final int TRANSACTION_mountVolume = 6;
        static final int TRANSACTION_unmountVolume = 7;
        static final int TRANSACTION_formatVolume = 8;
        static final int TRANSACTION_getStorageUsers = 9;
        static final int TRANSACTION_getVolumeState = 10;
        static final int TRANSACTION_createSecureContainer = 11;
        static final int TRANSACTION_finalizeSecureContainer = 12;
        static final int TRANSACTION_destroySecureContainer = 13;
        static final int TRANSACTION_mountSecureContainer = 14;
        static final int TRANSACTION_unmountSecureContainer = 15;
        static final int TRANSACTION_isSecureContainerMounted = 16;
        static final int TRANSACTION_renameSecureContainer = 17;
        static final int TRANSACTION_getSecureContainerPath = 18;
        static final int TRANSACTION_getSecureContainerList = 19;
        static final int TRANSACTION_shutdown = 20;
        static final int TRANSACTION_finishMediaUpdate = 21;
        static final int TRANSACTION_mountObb = 22;
        static final int TRANSACTION_unmountObb = 23;
        static final int TRANSACTION_isObbMounted = 24;
        static final int TRANSACTION_getMountedObbPath = 25;
        static final int TRANSACTION_isExternalStorageEmulated = 26;
        static final int TRANSACTION_decryptStorage = 27;
        static final int TRANSACTION_encryptStorage = 28;
        static final int TRANSACTION_changeEncryptionPassword = 29;
        static final int TRANSACTION_getVolumeList = 30;
        static final int TRANSACTION_getSecureContainerFilesystemPath = 31;
        static final int TRANSACTION_getEncryptionState = 32;
        static final int TRANSACTION_verifyEncryptionPassword = 33;
        static final int TRANSACTION_fixPermissionsSecureContainer = 34;
        static final int TRANSACTION_mkdirs = 35;

        /* loaded from: IMountService$Stub$Proxy.class */
        private static class Proxy implements IMountService {
            private final IBinder mRemote;

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

            @Override // android.os.storage.IMountService
            public void registerListener(IMountServiceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
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

            @Override // android.os.storage.IMountService
            public void unregisterListener(IMountServiceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
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

            @Override // android.os.storage.IMountService
            public boolean isUsbMassStorageConnected() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public void setUsbMassStorageEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
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

            @Override // android.os.storage.IMountService
            public boolean isUsbMassStorageEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int mountVolume(String mountPoint) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(mountPoint);
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

            @Override // android.os.storage.IMountService
            public void unmountVolume(String mountPoint, boolean force, boolean removeEncryption) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(mountPoint);
                    _data.writeInt(force ? 1 : 0);
                    _data.writeInt(removeEncryption ? 1 : 0);
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

            @Override // android.os.storage.IMountService
            public int formatVolume(String mountPoint) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(mountPoint);
                    this.mRemote.transact(8, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int[] getStorageUsers(String path) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    this.mRemote.transact(9, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public String getVolumeState(String mountPoint) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(mountPoint);
                    this.mRemote.transact(10, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int createSecureContainer(String id, int sizeMb, String fstype, String key, int ownerUid, boolean external) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    _data.writeInt(sizeMb);
                    _data.writeString(fstype);
                    _data.writeString(key);
                    _data.writeInt(ownerUid);
                    _data.writeInt(external ? 1 : 0);
                    this.mRemote.transact(11, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int destroySecureContainer(String id, boolean force) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    _data.writeInt(force ? 1 : 0);
                    this.mRemote.transact(13, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int finalizeSecureContainer(String id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    this.mRemote.transact(12, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int mountSecureContainer(String id, String key, int ownerUid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    _data.writeString(key);
                    _data.writeInt(ownerUid);
                    this.mRemote.transact(14, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int unmountSecureContainer(String id, boolean force) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    _data.writeInt(force ? 1 : 0);
                    this.mRemote.transact(15, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public boolean isSecureContainerMounted(String id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int renameSecureContainer(String oldId, String newId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(oldId);
                    _data.writeString(newId);
                    this.mRemote.transact(17, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public String getSecureContainerPath(String id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
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

            @Override // android.os.storage.IMountService
            public String[] getSecureContainerList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public void shutdown(IMountShutdownObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
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

            @Override // android.os.storage.IMountService
            public void finishMediaUpdate() throws RemoteException {
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

            @Override // android.os.storage.IMountService
            public void mountObb(String rawPath, String canonicalPath, String key, IObbActionListener token, int nonce) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rawPath);
                    _data.writeString(canonicalPath);
                    _data.writeString(key);
                    _data.writeStrongBinder(token != null ? token.asBinder() : null);
                    _data.writeInt(nonce);
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

            @Override // android.os.storage.IMountService
            public void unmountObb(String rawPath, boolean force, IObbActionListener token, int nonce) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rawPath);
                    _data.writeInt(force ? 1 : 0);
                    _data.writeStrongBinder(token != null ? token.asBinder() : null);
                    _data.writeInt(nonce);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.storage.IMountService
            public boolean isObbMounted(String rawPath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rawPath);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public String getMountedObbPath(String rawPath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rawPath);
                    this.mRemote.transact(25, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public boolean isExternalStorageEmulated() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.storage.IMountService
            public int getEncryptionState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int decryptStorage(String password) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(password);
                    this.mRemote.transact(27, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int encryptStorage(String password) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(password);
                    this.mRemote.transact(28, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int changeEncryptionPassword(String password) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(password);
                    this.mRemote.transact(29, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int verifyEncryptionPassword(String password) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(password);
                    this.mRemote.transact(33, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public StorageVolume[] getVolumeList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    StorageVolume[] _result = (StorageVolume[]) _reply.createTypedArray(StorageVolume.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.storage.IMountService
            public String getSecureContainerFilesystemPath(String id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    this.mRemote.transact(31, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int fixPermissionsSecureContainer(String id, int gid, String filename) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    _data.writeInt(gid);
                    _data.writeString(filename);
                    this.mRemote.transact(34, _data, _reply, 0);
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

            @Override // android.os.storage.IMountService
            public int mkdirs(String callingPkg, String path) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeString(path);
                    this.mRemote.transact(35, _data, _reply, 0);
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
        }

        public static IMountService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMountService)) {
                return (IMountService) iin;
            }
            return new Proxy(obj);
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IMountServiceListener listener = IMountServiceListener.Stub.asInterface(data.readStrongBinder());
                    registerListener(listener);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IMountServiceListener listener2 = IMountServiceListener.Stub.asInterface(data.readStrongBinder());
                    unregisterListener(listener2);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    boolean result = isUsbMassStorageConnected();
                    reply.writeNoException();
                    reply.writeInt(result ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    boolean enable = 0 != data.readInt();
                    setUsbMassStorageEnabled(enable);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    boolean result2 = isUsbMassStorageEnabled();
                    reply.writeNoException();
                    reply.writeInt(result2 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String mountPoint = data.readString();
                    int resultCode = mountVolume(mountPoint);
                    reply.writeNoException();
                    reply.writeInt(resultCode);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String mountPoint2 = data.readString();
                    boolean force = 0 != data.readInt();
                    boolean removeEncrypt = 0 != data.readInt();
                    unmountVolume(mountPoint2, force, removeEncrypt);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String mountPoint3 = data.readString();
                    int result3 = formatVolume(mountPoint3);
                    reply.writeNoException();
                    reply.writeInt(result3);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String path = data.readString();
                    int[] pids = getStorageUsers(path);
                    reply.writeNoException();
                    reply.writeIntArray(pids);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String mountPoint4 = data.readString();
                    String state = getVolumeState(mountPoint4);
                    reply.writeNoException();
                    reply.writeString(state);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    String id = data.readString();
                    int sizeMb = data.readInt();
                    String fstype = data.readString();
                    String key = data.readString();
                    int ownerUid = data.readInt();
                    boolean external = 0 != data.readInt();
                    int resultCode2 = createSecureContainer(id, sizeMb, fstype, key, ownerUid, external);
                    reply.writeNoException();
                    reply.writeInt(resultCode2);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String id2 = data.readString();
                    int resultCode3 = finalizeSecureContainer(id2);
                    reply.writeNoException();
                    reply.writeInt(resultCode3);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    String id3 = data.readString();
                    boolean force2 = 0 != data.readInt();
                    int resultCode4 = destroySecureContainer(id3, force2);
                    reply.writeNoException();
                    reply.writeInt(resultCode4);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String id4 = data.readString();
                    String key2 = data.readString();
                    int ownerUid2 = data.readInt();
                    int resultCode5 = mountSecureContainer(id4, key2, ownerUid2);
                    reply.writeNoException();
                    reply.writeInt(resultCode5);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    String id5 = data.readString();
                    boolean force3 = 0 != data.readInt();
                    int resultCode6 = unmountSecureContainer(id5, force3);
                    reply.writeNoException();
                    reply.writeInt(resultCode6);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    String id6 = data.readString();
                    boolean status = isSecureContainerMounted(id6);
                    reply.writeNoException();
                    reply.writeInt(status ? 1 : 0);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    String oldId = data.readString();
                    String newId = data.readString();
                    int resultCode7 = renameSecureContainer(oldId, newId);
                    reply.writeNoException();
                    reply.writeInt(resultCode7);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    String id7 = data.readString();
                    String path2 = getSecureContainerPath(id7);
                    reply.writeNoException();
                    reply.writeString(path2);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    String[] ids = getSecureContainerList();
                    reply.writeNoException();
                    reply.writeStringArray(ids);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    IMountShutdownObserver observer = IMountShutdownObserver.Stub.asInterface(data.readStrongBinder());
                    shutdown(observer);
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    finishMediaUpdate();
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    String rawPath = data.readString();
                    String canonicalPath = data.readString();
                    String key3 = data.readString();
                    IObbActionListener observer2 = IObbActionListener.Stub.asInterface(data.readStrongBinder());
                    int nonce = data.readInt();
                    mountObb(rawPath, canonicalPath, key3, observer2, nonce);
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    String filename = data.readString();
                    boolean force4 = 0 != data.readInt();
                    IObbActionListener observer3 = IObbActionListener.Stub.asInterface(data.readStrongBinder());
                    int nonce2 = data.readInt();
                    unmountObb(filename, force4, observer3, nonce2);
                    reply.writeNoException();
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    String filename2 = data.readString();
                    boolean status2 = isObbMounted(filename2);
                    reply.writeNoException();
                    reply.writeInt(status2 ? 1 : 0);
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    String filename3 = data.readString();
                    String mountedPath = getMountedObbPath(filename3);
                    reply.writeNoException();
                    reply.writeString(mountedPath);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    boolean emulated = isExternalStorageEmulated();
                    reply.writeNoException();
                    reply.writeInt(emulated ? 1 : 0);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    String password = data.readString();
                    int result4 = decryptStorage(password);
                    reply.writeNoException();
                    reply.writeInt(result4);
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    String password2 = data.readString();
                    int result5 = encryptStorage(password2);
                    reply.writeNoException();
                    reply.writeInt(result5);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    String password3 = data.readString();
                    int result6 = changeEncryptionPassword(password3);
                    reply.writeNoException();
                    reply.writeInt(result6);
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    StorageVolume[] result7 = getVolumeList();
                    reply.writeNoException();
                    reply.writeTypedArray(result7, 1);
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    String id8 = data.readString();
                    String path3 = getSecureContainerFilesystemPath(id8);
                    reply.writeNoException();
                    reply.writeString(path3);
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    int result8 = getEncryptionState();
                    reply.writeNoException();
                    reply.writeInt(result8);
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    String id9 = data.readString();
                    int gid = data.readInt();
                    String filename4 = data.readString();
                    int resultCode8 = fixPermissionsSecureContainer(id9, gid, filename4);
                    reply.writeNoException();
                    reply.writeInt(resultCode8);
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    String callingPkg = data.readString();
                    String path4 = data.readString();
                    int result9 = mkdirs(callingPkg, path4);
                    reply.writeNoException();
                    reply.writeInt(result9);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }
}