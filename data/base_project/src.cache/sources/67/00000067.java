package android.accounts;

import android.accounts.IAccountManagerResponse;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IAccountManager.class */
public interface IAccountManager extends IInterface {
    String getPassword(Account account) throws RemoteException;

    String getUserData(Account account, String str) throws RemoteException;

    AuthenticatorDescription[] getAuthenticatorTypes() throws RemoteException;

    Account[] getAccounts(String str) throws RemoteException;

    Account[] getAccountsForPackage(String str, int i) throws RemoteException;

    Account[] getAccountsByTypeForPackage(String str, String str2) throws RemoteException;

    Account[] getAccountsAsUser(String str, int i) throws RemoteException;

    void hasFeatures(IAccountManagerResponse iAccountManagerResponse, Account account, String[] strArr) throws RemoteException;

    void getAccountsByFeatures(IAccountManagerResponse iAccountManagerResponse, String str, String[] strArr) throws RemoteException;

    boolean addAccountExplicitly(Account account, String str, Bundle bundle) throws RemoteException;

    void removeAccount(IAccountManagerResponse iAccountManagerResponse, Account account) throws RemoteException;

    void invalidateAuthToken(String str, String str2) throws RemoteException;

    String peekAuthToken(Account account, String str) throws RemoteException;

    void setAuthToken(Account account, String str, String str2) throws RemoteException;

    void setPassword(Account account, String str) throws RemoteException;

    void clearPassword(Account account) throws RemoteException;

    void setUserData(Account account, String str, String str2) throws RemoteException;

    void updateAppPermission(Account account, String str, int i, boolean z) throws RemoteException;

    void getAuthToken(IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, boolean z2, Bundle bundle) throws RemoteException;

    void addAccount(IAccountManagerResponse iAccountManagerResponse, String str, String str2, String[] strArr, boolean z, Bundle bundle) throws RemoteException;

    void updateCredentials(IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, Bundle bundle) throws RemoteException;

    void editProperties(IAccountManagerResponse iAccountManagerResponse, String str, boolean z) throws RemoteException;

    void confirmCredentialsAsUser(IAccountManagerResponse iAccountManagerResponse, Account account, Bundle bundle, boolean z, int i) throws RemoteException;

    void getAuthTokenLabel(IAccountManagerResponse iAccountManagerResponse, String str, String str2) throws RemoteException;

    boolean addSharedAccountAsUser(Account account, int i) throws RemoteException;

    Account[] getSharedAccountsAsUser(int i) throws RemoteException;

    boolean removeSharedAccountAsUser(Account account, int i) throws RemoteException;

    /* loaded from: IAccountManager$Stub.class */
    public static abstract class Stub extends Binder implements IAccountManager {
        private static final String DESCRIPTOR = "android.accounts.IAccountManager";
        static final int TRANSACTION_getPassword = 1;
        static final int TRANSACTION_getUserData = 2;
        static final int TRANSACTION_getAuthenticatorTypes = 3;
        static final int TRANSACTION_getAccounts = 4;
        static final int TRANSACTION_getAccountsForPackage = 5;
        static final int TRANSACTION_getAccountsByTypeForPackage = 6;
        static final int TRANSACTION_getAccountsAsUser = 7;
        static final int TRANSACTION_hasFeatures = 8;
        static final int TRANSACTION_getAccountsByFeatures = 9;
        static final int TRANSACTION_addAccountExplicitly = 10;
        static final int TRANSACTION_removeAccount = 11;
        static final int TRANSACTION_invalidateAuthToken = 12;
        static final int TRANSACTION_peekAuthToken = 13;
        static final int TRANSACTION_setAuthToken = 14;
        static final int TRANSACTION_setPassword = 15;
        static final int TRANSACTION_clearPassword = 16;
        static final int TRANSACTION_setUserData = 17;
        static final int TRANSACTION_updateAppPermission = 18;
        static final int TRANSACTION_getAuthToken = 19;
        static final int TRANSACTION_addAccount = 20;
        static final int TRANSACTION_updateCredentials = 21;
        static final int TRANSACTION_editProperties = 22;
        static final int TRANSACTION_confirmCredentialsAsUser = 23;
        static final int TRANSACTION_getAuthTokenLabel = 24;
        static final int TRANSACTION_addSharedAccountAsUser = 25;
        static final int TRANSACTION_getSharedAccountsAsUser = 26;
        static final int TRANSACTION_removeSharedAccountAsUser = 27;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccountManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccountManager)) {
                return (IAccountManager) iin;
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
            Account _arg1;
            Bundle _arg2;
            Account _arg12;
            Bundle _arg4;
            Bundle _arg5;
            Account _arg13;
            Bundle _arg52;
            Account _arg03;
            Account _arg04;
            Account _arg05;
            Account _arg06;
            Account _arg07;
            Account _arg08;
            Account _arg14;
            Account _arg09;
            Bundle _arg22;
            Account _arg15;
            Account _arg010;
            Account _arg011;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg011 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg011 = null;
                    }
                    String _result = getPassword(_arg011);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg010 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    String _arg16 = data.readString();
                    String _result2 = getUserData(_arg010, _arg16);
                    reply.writeNoException();
                    reply.writeString(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    AuthenticatorDescription[] _result3 = getAuthenticatorTypes();
                    reply.writeNoException();
                    reply.writeTypedArray(_result3, 1);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg012 = data.readString();
                    Account[] _result4 = getAccounts(_arg012);
                    reply.writeNoException();
                    reply.writeTypedArray(_result4, 1);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg013 = data.readString();
                    int _arg17 = data.readInt();
                    Account[] _result5 = getAccountsForPackage(_arg013, _arg17);
                    reply.writeNoException();
                    reply.writeTypedArray(_result5, 1);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg014 = data.readString();
                    String _arg18 = data.readString();
                    Account[] _result6 = getAccountsByTypeForPackage(_arg014, _arg18);
                    reply.writeNoException();
                    reply.writeTypedArray(_result6, 1);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg015 = data.readString();
                    int _arg19 = data.readInt();
                    Account[] _result7 = getAccountsAsUser(_arg015, _arg19);
                    reply.writeNoException();
                    reply.writeTypedArray(_result7, 1);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    IAccountManagerResponse _arg016 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg15 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg15 = null;
                    }
                    String[] _arg23 = data.createStringArray();
                    hasFeatures(_arg016, _arg15, _arg23);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    IAccountManagerResponse _arg017 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                    String _arg110 = data.readString();
                    String[] _arg24 = data.createStringArray();
                    getAccountsByFeatures(_arg017, _arg110, _arg24);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg09 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg09 = null;
                    }
                    String _arg111 = data.readString();
                    if (0 != data.readInt()) {
                        _arg22 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    boolean _result8 = addAccountExplicitly(_arg09, _arg111, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result8 ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    IAccountManagerResponse _arg018 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg14 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg14 = null;
                    }
                    removeAccount(_arg018, _arg14);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg019 = data.readString();
                    String _arg112 = data.readString();
                    invalidateAuthToken(_arg019, _arg112);
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    String _arg113 = data.readString();
                    String _result9 = peekAuthToken(_arg08, _arg113);
                    reply.writeNoException();
                    reply.writeString(_result9);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    String _arg114 = data.readString();
                    String _arg25 = data.readString();
                    setAuthToken(_arg07, _arg114, _arg25);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    String _arg115 = data.readString();
                    setPassword(_arg06, _arg115);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    clearPassword(_arg05);
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    String _arg116 = data.readString();
                    String _arg26 = data.readString();
                    setUserData(_arg04, _arg116, _arg26);
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    String _arg117 = data.readString();
                    int _arg27 = data.readInt();
                    boolean _arg3 = 0 != data.readInt();
                    updateAppPermission(_arg03, _arg117, _arg27, _arg3);
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    IAccountManagerResponse _arg020 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg13 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    String _arg28 = data.readString();
                    boolean _arg32 = 0 != data.readInt();
                    boolean _arg42 = 0 != data.readInt();
                    if (0 != data.readInt()) {
                        _arg52 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg52 = null;
                    }
                    getAuthToken(_arg020, _arg13, _arg28, _arg32, _arg42, _arg52);
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    IAccountManagerResponse _arg021 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                    String _arg118 = data.readString();
                    String _arg29 = data.readString();
                    String[] _arg33 = data.createStringArray();
                    boolean _arg43 = 0 != data.readInt();
                    if (0 != data.readInt()) {
                        _arg5 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg5 = null;
                    }
                    addAccount(_arg021, _arg118, _arg29, _arg33, _arg43, _arg5);
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    IAccountManagerResponse _arg022 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg12 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    String _arg210 = data.readString();
                    boolean _arg34 = 0 != data.readInt();
                    if (0 != data.readInt()) {
                        _arg4 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    updateCredentials(_arg022, _arg12, _arg210, _arg34, _arg4);
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    IAccountManagerResponse _arg023 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                    String _arg119 = data.readString();
                    boolean _arg211 = 0 != data.readInt();
                    editProperties(_arg023, _arg119, _arg211);
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    IAccountManagerResponse _arg024 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg1 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg2 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    boolean _arg35 = 0 != data.readInt();
                    int _arg44 = data.readInt();
                    confirmCredentialsAsUser(_arg024, _arg1, _arg2, _arg35, _arg44);
                    reply.writeNoException();
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    IAccountManagerResponse _arg025 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                    String _arg120 = data.readString();
                    String _arg212 = data.readString();
                    getAuthTokenLabel(_arg025, _arg120, _arg212);
                    reply.writeNoException();
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _arg121 = data.readInt();
                    boolean _result10 = addSharedAccountAsUser(_arg02, _arg121);
                    reply.writeNoException();
                    reply.writeInt(_result10 ? 1 : 0);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg026 = data.readInt();
                    Account[] _result11 = getSharedAccountsAsUser(_arg026);
                    reply.writeNoException();
                    reply.writeTypedArray(_result11, 1);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg122 = data.readInt();
                    boolean _result12 = removeSharedAccountAsUser(_arg0, _arg122);
                    reply.writeNoException();
                    reply.writeInt(_result12 ? 1 : 0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IAccountManager$Stub$Proxy.class */
        private static class Proxy implements IAccountManager {
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

            @Override // android.accounts.IAccountManager
            public String getPassword(Account account) throws RemoteException {
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
                    this.mRemote.transact(1, _data, _reply, 0);
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

            @Override // android.accounts.IAccountManager
            public String getUserData(Account account, String key) throws RemoteException {
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
                    _data.writeString(key);
                    this.mRemote.transact(2, _data, _reply, 0);
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

            @Override // android.accounts.IAccountManager
            public AuthenticatorDescription[] getAuthenticatorTypes() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    AuthenticatorDescription[] _result = (AuthenticatorDescription[]) _reply.createTypedArray(AuthenticatorDescription.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accounts.IAccountManager
            public Account[] getAccounts(String accountType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(accountType);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    Account[] _result = (Account[]) _reply.createTypedArray(Account.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accounts.IAccountManager
            public Account[] getAccountsForPackage(String packageName, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(uid);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    Account[] _result = (Account[]) _reply.createTypedArray(Account.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accounts.IAccountManager
            public Account[] getAccountsByTypeForPackage(String type, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeString(packageName);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    Account[] _result = (Account[]) _reply.createTypedArray(Account.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accounts.IAccountManager
            public Account[] getAccountsAsUser(String accountType, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(accountType);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    Account[] _result = (Account[]) _reply.createTypedArray(Account.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accounts.IAccountManager
            public void hasFeatures(IAccountManagerResponse response, Account account, String[] features) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStringArray(features);
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

            @Override // android.accounts.IAccountManager
            public void getAccountsByFeatures(IAccountManagerResponse response, String accountType, String[] features) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    _data.writeString(accountType);
                    _data.writeStringArray(features);
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

            @Override // android.accounts.IAccountManager
            public boolean addAccountExplicitly(Account account, String password, Bundle extras) throws RemoteException {
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
                    _data.writeString(password);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void removeAccount(IAccountManagerResponse response, Account account) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
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

            @Override // android.accounts.IAccountManager
            public void invalidateAuthToken(String accountType, String authToken) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(accountType);
                    _data.writeString(authToken);
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

            @Override // android.accounts.IAccountManager
            public String peekAuthToken(Account account, String authTokenType) throws RemoteException {
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
                    _data.writeString(authTokenType);
                    this.mRemote.transact(13, _data, _reply, 0);
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

            @Override // android.accounts.IAccountManager
            public void setAuthToken(Account account, String authTokenType, String authToken) throws RemoteException {
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
                    _data.writeString(authTokenType);
                    _data.writeString(authToken);
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

            @Override // android.accounts.IAccountManager
            public void setPassword(Account account, String password) throws RemoteException {
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
                    _data.writeString(password);
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

            @Override // android.accounts.IAccountManager
            public void clearPassword(Account account) throws RemoteException {
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

            @Override // android.accounts.IAccountManager
            public void setUserData(Account account, String key, String value) throws RemoteException {
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
                    _data.writeString(key);
                    _data.writeString(value);
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

            @Override // android.accounts.IAccountManager
            public void updateAppPermission(Account account, String authTokenType, int uid, boolean value) throws RemoteException {
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
                    _data.writeString(authTokenType);
                    _data.writeInt(uid);
                    _data.writeInt(value ? 1 : 0);
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

            @Override // android.accounts.IAccountManager
            public void getAuthToken(IAccountManagerResponse response, Account account, String authTokenType, boolean notifyOnAuthFailure, boolean expectActivityLaunch, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(authTokenType);
                    _data.writeInt(notifyOnAuthFailure ? 1 : 0);
                    _data.writeInt(expectActivityLaunch ? 1 : 0);
                    if (options != null) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.accounts.IAccountManager
            public void addAccount(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    _data.writeString(accountType);
                    _data.writeString(authTokenType);
                    _data.writeStringArray(requiredFeatures);
                    _data.writeInt(expectActivityLaunch ? 1 : 0);
                    if (options != null) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.accounts.IAccountManager
            public void updateCredentials(IAccountManagerResponse response, Account account, String authTokenType, boolean expectActivityLaunch, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(authTokenType);
                    _data.writeInt(expectActivityLaunch ? 1 : 0);
                    if (options != null) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.accounts.IAccountManager
            public void editProperties(IAccountManagerResponse response, String accountType, boolean expectActivityLaunch) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    _data.writeString(accountType);
                    _data.writeInt(expectActivityLaunch ? 1 : 0);
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

            @Override // android.accounts.IAccountManager
            public void confirmCredentialsAsUser(IAccountManagerResponse response, Account account, Bundle options, boolean expectActivityLaunch, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    if (account != null) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (options != null) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(expectActivityLaunch ? 1 : 0);
                    _data.writeInt(userId);
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

            @Override // android.accounts.IAccountManager
            public void getAuthTokenLabel(IAccountManagerResponse response, String accountType, String authTokenType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(response != null ? response.asBinder() : null);
                    _data.writeString(accountType);
                    _data.writeString(authTokenType);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accounts.IAccountManager
            public boolean addSharedAccountAsUser(Account account, int userId) throws RemoteException {
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
                    _data.writeInt(userId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public Account[] getSharedAccountsAsUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    Account[] _result = (Account[]) _reply.createTypedArray(Account.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.accounts.IAccountManager
            public boolean removeSharedAccountAsUser(Account account, int userId) throws RemoteException {
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
                    _data.writeInt(userId);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}