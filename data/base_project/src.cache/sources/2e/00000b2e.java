package android.os;

import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import java.util.List;

/* loaded from: IUserManager.class */
public interface IUserManager extends IInterface {
    UserInfo createUser(String str, int i) throws RemoteException;

    boolean removeUser(int i) throws RemoteException;

    void setUserName(int i, String str) throws RemoteException;

    void setUserIcon(int i, Bitmap bitmap) throws RemoteException;

    Bitmap getUserIcon(int i) throws RemoteException;

    List<UserInfo> getUsers(boolean z) throws RemoteException;

    UserInfo getUserInfo(int i) throws RemoteException;

    boolean isRestricted() throws RemoteException;

    void setGuestEnabled(boolean z) throws RemoteException;

    boolean isGuestEnabled() throws RemoteException;

    void wipeUser(int i) throws RemoteException;

    int getUserSerialNumber(int i) throws RemoteException;

    int getUserHandle(int i) throws RemoteException;

    Bundle getUserRestrictions(int i) throws RemoteException;

    void setUserRestrictions(Bundle bundle, int i) throws RemoteException;

    void setApplicationRestrictions(String str, Bundle bundle, int i) throws RemoteException;

    Bundle getApplicationRestrictions(String str) throws RemoteException;

    Bundle getApplicationRestrictionsForUser(String str, int i) throws RemoteException;

    boolean setRestrictionsChallenge(String str) throws RemoteException;

    int checkRestrictionsChallenge(String str) throws RemoteException;

    boolean hasRestrictionsChallenge() throws RemoteException;

    void removeRestrictions() throws RemoteException;

    /* loaded from: IUserManager$Stub.class */
    public static abstract class Stub extends Binder implements IUserManager {
        private static final String DESCRIPTOR = "android.os.IUserManager";
        static final int TRANSACTION_createUser = 1;
        static final int TRANSACTION_removeUser = 2;
        static final int TRANSACTION_setUserName = 3;
        static final int TRANSACTION_setUserIcon = 4;
        static final int TRANSACTION_getUserIcon = 5;
        static final int TRANSACTION_getUsers = 6;
        static final int TRANSACTION_getUserInfo = 7;
        static final int TRANSACTION_isRestricted = 8;
        static final int TRANSACTION_setGuestEnabled = 9;
        static final int TRANSACTION_isGuestEnabled = 10;
        static final int TRANSACTION_wipeUser = 11;
        static final int TRANSACTION_getUserSerialNumber = 12;
        static final int TRANSACTION_getUserHandle = 13;
        static final int TRANSACTION_getUserRestrictions = 14;
        static final int TRANSACTION_setUserRestrictions = 15;
        static final int TRANSACTION_setApplicationRestrictions = 16;
        static final int TRANSACTION_getApplicationRestrictions = 17;
        static final int TRANSACTION_getApplicationRestrictionsForUser = 18;
        static final int TRANSACTION_setRestrictionsChallenge = 19;
        static final int TRANSACTION_checkRestrictionsChallenge = 20;
        static final int TRANSACTION_hasRestrictionsChallenge = 21;
        static final int TRANSACTION_removeRestrictions = 22;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUserManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IUserManager)) {
                return (IUserManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg1;
            Bundle _arg0;
            Bitmap _arg12;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    int _arg13 = data.readInt();
                    UserInfo _result = createUser(_arg02, _arg13);
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
                    int _arg03 = data.readInt();
                    boolean _result2 = removeUser(_arg03);
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    String _arg14 = data.readString();
                    setUserName(_arg04, _arg14);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg12 = Bitmap.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    setUserIcon(_arg05, _arg12);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    Bitmap _result3 = getUserIcon(_arg06);
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(1);
                        _result3.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg07 = 0 != data.readInt();
                    List<UserInfo> _result4 = getUsers(_arg07);
                    reply.writeNoException();
                    reply.writeTypedList(_result4);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    UserInfo _result5 = getUserInfo(_arg08);
                    reply.writeNoException();
                    if (_result5 != null) {
                        reply.writeInt(1);
                        _result5.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result6 = isRestricted();
                    reply.writeNoException();
                    reply.writeInt(_result6 ? 1 : 0);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg09 = 0 != data.readInt();
                    setGuestEnabled(_arg09);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result7 = isGuestEnabled();
                    reply.writeNoException();
                    reply.writeInt(_result7 ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    wipeUser(_arg010);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    int _result8 = getUserSerialNumber(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    int _result9 = getUserHandle(_arg012);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    Bundle _result10 = getUserRestrictions(_arg013);
                    reply.writeNoException();
                    if (_result10 != null) {
                        reply.writeInt(1);
                        _result10.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg15 = data.readInt();
                    setUserRestrictions(_arg0, _arg15);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg014 = data.readString();
                    if (0 != data.readInt()) {
                        _arg1 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    int _arg2 = data.readInt();
                    setApplicationRestrictions(_arg014, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg015 = data.readString();
                    Bundle _result11 = getApplicationRestrictions(_arg015);
                    reply.writeNoException();
                    if (_result11 != null) {
                        reply.writeInt(1);
                        _result11.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg016 = data.readString();
                    int _arg16 = data.readInt();
                    Bundle _result12 = getApplicationRestrictionsForUser(_arg016, _arg16);
                    reply.writeNoException();
                    if (_result12 != null) {
                        reply.writeInt(1);
                        _result12.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg017 = data.readString();
                    boolean _result13 = setRestrictionsChallenge(_arg017);
                    reply.writeNoException();
                    reply.writeInt(_result13 ? 1 : 0);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg018 = data.readString();
                    int _result14 = checkRestrictionsChallenge(_arg018);
                    reply.writeNoException();
                    reply.writeInt(_result14);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result15 = hasRestrictionsChallenge();
                    reply.writeNoException();
                    reply.writeInt(_result15 ? 1 : 0);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    removeRestrictions();
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IUserManager$Stub$Proxy.class */
        private static class Proxy implements IUserManager {
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

            @Override // android.os.IUserManager
            public UserInfo createUser(String name, int flags) throws RemoteException {
                UserInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(flags);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = UserInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IUserManager
            public boolean removeUser(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IUserManager
            public void setUserName(int userHandle, String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeString(name);
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

            @Override // android.os.IUserManager
            public void setUserIcon(int userHandle, Bitmap icon) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    if (icon != null) {
                        _data.writeInt(1);
                        icon.writeToParcel(_data, 0);
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

            @Override // android.os.IUserManager
            public Bitmap getUserIcon(int userHandle) throws RemoteException {
                Bitmap _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Bitmap.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IUserManager
            public List<UserInfo> getUsers(boolean excludeDying) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(excludeDying ? 1 : 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    List<UserInfo> _result = _reply.createTypedArrayList(UserInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.IUserManager
            public UserInfo getUserInfo(int userHandle) throws RemoteException {
                UserInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = UserInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IUserManager
            public boolean isRestricted() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IUserManager
            public void setGuestEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
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

            @Override // android.os.IUserManager
            public boolean isGuestEnabled() throws RemoteException {
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

            @Override // android.os.IUserManager
            public void wipeUser(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
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

            @Override // android.os.IUserManager
            public int getUserSerialNumber(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
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

            @Override // android.os.IUserManager
            public int getUserHandle(int userSerialNumber) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userSerialNumber);
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

            @Override // android.os.IUserManager
            public Bundle getUserRestrictions(int userHandle) throws RemoteException {
                Bundle _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IUserManager
            public void setUserRestrictions(Bundle restrictions, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (restrictions != null) {
                        _data.writeInt(1);
                        restrictions.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
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

            @Override // android.os.IUserManager
            public void setApplicationRestrictions(String packageName, Bundle restrictions, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (restrictions != null) {
                        _data.writeInt(1);
                        restrictions.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
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

            @Override // android.os.IUserManager
            public Bundle getApplicationRestrictions(String packageName) throws RemoteException {
                Bundle _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IUserManager
            public Bundle getApplicationRestrictionsForUser(String packageName, int userHandle) throws RemoteException {
                Bundle _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IUserManager
            public boolean setRestrictionsChallenge(String newPin) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(newPin);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IUserManager
            public int checkRestrictionsChallenge(String pin) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pin);
                    this.mRemote.transact(20, _data, _reply, 0);
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

            @Override // android.os.IUserManager
            public boolean hasRestrictionsChallenge() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.IUserManager
            public void removeRestrictions() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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