package android.content;

import android.accounts.Account;
import android.content.ISyncStatusObserver;
import android.database.IContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* loaded from: IContentService.class */
public interface IContentService extends IInterface {
    void unregisterContentObserver(IContentObserver iContentObserver) throws RemoteException;

    void registerContentObserver(Uri uri, boolean z, IContentObserver iContentObserver, int i) throws RemoteException;

    void notifyChange(Uri uri, IContentObserver iContentObserver, boolean z, boolean z2, int i) throws RemoteException;

    void requestSync(Account account, String str, Bundle bundle) throws RemoteException;

    void sync(SyncRequest syncRequest) throws RemoteException;

    void cancelSync(Account account, String str) throws RemoteException;

    boolean getSyncAutomatically(Account account, String str) throws RemoteException;

    void setSyncAutomatically(Account account, String str, boolean z) throws RemoteException;

    List<PeriodicSync> getPeriodicSyncs(Account account, String str) throws RemoteException;

    void addPeriodicSync(Account account, String str, Bundle bundle, long j) throws RemoteException;

    void removePeriodicSync(Account account, String str, Bundle bundle) throws RemoteException;

    int getIsSyncable(Account account, String str) throws RemoteException;

    void setIsSyncable(Account account, String str, int i) throws RemoteException;

    void setMasterSyncAutomatically(boolean z) throws RemoteException;

    boolean getMasterSyncAutomatically() throws RemoteException;

    boolean isSyncActive(Account account, String str) throws RemoteException;

    List<SyncInfo> getCurrentSyncs() throws RemoteException;

    SyncAdapterType[] getSyncAdapterTypes() throws RemoteException;

    SyncStatusInfo getSyncStatus(Account account, String str) throws RemoteException;

    boolean isSyncPending(Account account, String str) throws RemoteException;

    void addStatusChangeListener(int i, ISyncStatusObserver iSyncStatusObserver) throws RemoteException;

    void removeStatusChangeListener(ISyncStatusObserver iSyncStatusObserver) throws RemoteException;

    /* loaded from: IContentService$Stub.class */
    public static abstract class Stub extends Binder implements IContentService {
        private static final String DESCRIPTOR = "android.content.IContentService";
        static final int TRANSACTION_unregisterContentObserver = 1;
        static final int TRANSACTION_registerContentObserver = 2;
        static final int TRANSACTION_notifyChange = 3;
        static final int TRANSACTION_requestSync = 4;
        static final int TRANSACTION_sync = 5;
        static final int TRANSACTION_cancelSync = 6;
        static final int TRANSACTION_getSyncAutomatically = 7;
        static final int TRANSACTION_setSyncAutomatically = 8;
        static final int TRANSACTION_getPeriodicSyncs = 9;
        static final int TRANSACTION_addPeriodicSync = 10;
        static final int TRANSACTION_removePeriodicSync = 11;
        static final int TRANSACTION_getIsSyncable = 12;
        static final int TRANSACTION_setIsSyncable = 13;
        static final int TRANSACTION_setMasterSyncAutomatically = 14;
        static final int TRANSACTION_getMasterSyncAutomatically = 15;
        static final int TRANSACTION_isSyncActive = 16;
        static final int TRANSACTION_getCurrentSyncs = 17;
        static final int TRANSACTION_getSyncAdapterTypes = 18;
        static final int TRANSACTION_getSyncStatus = 19;
        static final int TRANSACTION_isSyncPending = 20;
        static final int TRANSACTION_addStatusChangeListener = 21;
        static final int TRANSACTION_removeStatusChangeListener = 22;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IContentService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IContentService)) {
                return (IContentService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Account _arg0;
            Account _arg02;
            Account _arg03;
            Account _arg04;
            Account _arg05;
            Account _arg06;
            Bundle _arg2;
            Account _arg07;
            Bundle _arg22;
            Account _arg08;
            Account _arg09;
            Account _arg010;
            Account _arg011;
            SyncRequest _arg012;
            Account _arg013;
            Bundle _arg23;
            Uri _arg014;
            Uri _arg015;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IContentObserver _arg016 = IContentObserver.Stub.asInterface(data.readStrongBinder());
                    unregisterContentObserver(_arg016);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg015 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg015 = null;
                    }
                    boolean _arg1 = 0 != data.readInt();
                    IContentObserver _arg24 = IContentObserver.Stub.asInterface(data.readStrongBinder());
                    int _arg3 = data.readInt();
                    registerContentObserver(_arg015, _arg1, _arg24, _arg3);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg014 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg014 = null;
                    }
                    IContentObserver _arg12 = IContentObserver.Stub.asInterface(data.readStrongBinder());
                    boolean _arg25 = 0 != data.readInt();
                    boolean _arg32 = 0 != data.readInt();
                    int _arg4 = data.readInt();
                    notifyChange(_arg014, _arg12, _arg25, _arg32, _arg4);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg013 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg013 = null;
                    }
                    String _arg13 = data.readString();
                    if (0 != data.readInt()) {
                        _arg23 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg23 = null;
                    }
                    requestSync(_arg013, _arg13, _arg23);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg012 = SyncRequest.CREATOR.createFromParcel(data);
                    } else {
                        _arg012 = null;
                    }
                    sync(_arg012);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg011 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg011 = null;
                    }
                    String _arg14 = data.readString();
                    cancelSync(_arg011, _arg14);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg010 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    String _arg15 = data.readString();
                    boolean _result = getSyncAutomatically(_arg010, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg09 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg09 = null;
                    }
                    String _arg16 = data.readString();
                    boolean _arg26 = 0 != data.readInt();
                    setSyncAutomatically(_arg09, _arg16, _arg26);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    String _arg17 = data.readString();
                    List<PeriodicSync> _result2 = getPeriodicSyncs(_arg08, _arg17);
                    reply.writeNoException();
                    reply.writeTypedList(_result2);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    String _arg18 = data.readString();
                    if (0 != data.readInt()) {
                        _arg22 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    long _arg33 = data.readLong();
                    addPeriodicSync(_arg07, _arg18, _arg22, _arg33);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    String _arg19 = data.readString();
                    if (0 != data.readInt()) {
                        _arg2 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    removePeriodicSync(_arg06, _arg19, _arg2);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    String _arg110 = data.readString();
                    int _result3 = getIsSyncable(_arg05, _arg110);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    String _arg111 = data.readString();
                    int _arg27 = data.readInt();
                    setIsSyncable(_arg04, _arg111, _arg27);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg017 = 0 != data.readInt();
                    setMasterSyncAutomatically(_arg017);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result4 = getMasterSyncAutomatically();
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    String _arg112 = data.readString();
                    boolean _result5 = isSyncActive(_arg03, _arg112);
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    List<SyncInfo> _result6 = getCurrentSyncs();
                    reply.writeNoException();
                    reply.writeTypedList(_result6);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    SyncAdapterType[] _result7 = getSyncAdapterTypes();
                    reply.writeNoException();
                    reply.writeTypedArray(_result7, 1);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    String _arg113 = data.readString();
                    SyncStatusInfo _result8 = getSyncStatus(_arg02, _arg113);
                    reply.writeNoException();
                    if (_result8 != null) {
                        reply.writeInt(1);
                        _result8.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    String _arg114 = data.readString();
                    boolean _result9 = isSyncPending(_arg0, _arg114);
                    reply.writeNoException();
                    reply.writeInt(_result9 ? 1 : 0);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    ISyncStatusObserver _arg115 = ISyncStatusObserver.Stub.asInterface(data.readStrongBinder());
                    addStatusChangeListener(_arg018, _arg115);
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    ISyncStatusObserver _arg019 = ISyncStatusObserver.Stub.asInterface(data.readStrongBinder());
                    removeStatusChangeListener(_arg019);
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
        /* loaded from: IContentService$Stub$Proxy.class */
        public static class Proxy implements IContentService {
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

            @Override // android.content.IContentService
            public void unregisterContentObserver(IContentObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
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

            @Override // android.content.IContentService
            public void registerContentObserver(Uri uri, boolean notifyForDescendants, IContentObserver observer, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (uri != null) {
                        _data.writeInt(1);
                        uri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(notifyForDescendants ? 1 : 0);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(userHandle);
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

            @Override // android.content.IContentService
            public void notifyChange(Uri uri, IContentObserver observer, boolean observerWantsSelfNotifications, boolean syncToNetwork, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (uri != null) {
                        _data.writeInt(1);
                        uri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(observerWantsSelfNotifications ? 1 : 0);
                    _data.writeInt(syncToNetwork ? 1 : 0);
                    _data.writeInt(userHandle);
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

            @Override // android.content.IContentService
            public void requestSync(Account account, String authority, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(authority);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
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

            @Override // android.content.IContentService
            public void sync(SyncRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (request != null) {
                        _data.writeInt(1);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.content.IContentService
            public void cancelSync(Account account, String authority) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(authority);
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

            @Override // android.content.IContentService
            public boolean getSyncAutomatically(Account account, String providerName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(providerName);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void setSyncAutomatically(Account account, String providerName, boolean sync) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(providerName);
                    _data.writeInt(sync ? 1 : 0);
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

            @Override // android.content.IContentService
            public List<PeriodicSync> getPeriodicSyncs(Account account, String providerName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(providerName);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    List<PeriodicSync> _result = _reply.createTypedArrayList(PeriodicSync.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.IContentService
            public void addPeriodicSync(Account account, String providerName, Bundle extras, long pollFrequency) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(providerName);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeLong(pollFrequency);
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

            @Override // android.content.IContentService
            public void removePeriodicSync(Account account, String providerName, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(providerName);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.content.IContentService
            public int getIsSyncable(Account account, String providerName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(providerName);
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

            @Override // android.content.IContentService
            public void setIsSyncable(Account account, String providerName, int syncable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(providerName);
                    _data.writeInt(syncable);
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

            @Override // android.content.IContentService
            public void setMasterSyncAutomatically(boolean flag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flag ? 1 : 0);
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

            @Override // android.content.IContentService
            public boolean getMasterSyncAutomatically() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public boolean isSyncActive(Account account, String authority) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(authority);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public List<SyncInfo> getCurrentSyncs() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    List<SyncInfo> _result = _reply.createTypedArrayList(SyncInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.IContentService
            public SyncAdapterType[] getSyncAdapterTypes() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    SyncAdapterType[] _result = (SyncAdapterType[]) _reply.createTypedArray(SyncAdapterType.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.IContentService
            public SyncStatusInfo getSyncStatus(Account account, String authority) throws RemoteException {
                SyncStatusInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(authority);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = SyncStatusInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public boolean isSyncPending(Account account, String authority) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(authority);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void addStatusChangeListener(int mask, ISyncStatusObserver callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mask);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
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

            @Override // android.content.IContentService
            public void removeStatusChangeListener(ISyncStatusObserver callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
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