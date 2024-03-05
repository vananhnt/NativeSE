package android.app.admin;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteCallback;
import android.os.RemoteException;
import java.util.List;

/* loaded from: IDevicePolicyManager.class */
public interface IDevicePolicyManager extends IInterface {
    void setPasswordQuality(ComponentName componentName, int i, int i2) throws RemoteException;

    int getPasswordQuality(ComponentName componentName, int i) throws RemoteException;

    void setPasswordMinimumLength(ComponentName componentName, int i, int i2) throws RemoteException;

    int getPasswordMinimumLength(ComponentName componentName, int i) throws RemoteException;

    void setPasswordMinimumUpperCase(ComponentName componentName, int i, int i2) throws RemoteException;

    int getPasswordMinimumUpperCase(ComponentName componentName, int i) throws RemoteException;

    void setPasswordMinimumLowerCase(ComponentName componentName, int i, int i2) throws RemoteException;

    int getPasswordMinimumLowerCase(ComponentName componentName, int i) throws RemoteException;

    void setPasswordMinimumLetters(ComponentName componentName, int i, int i2) throws RemoteException;

    int getPasswordMinimumLetters(ComponentName componentName, int i) throws RemoteException;

    void setPasswordMinimumNumeric(ComponentName componentName, int i, int i2) throws RemoteException;

    int getPasswordMinimumNumeric(ComponentName componentName, int i) throws RemoteException;

    void setPasswordMinimumSymbols(ComponentName componentName, int i, int i2) throws RemoteException;

    int getPasswordMinimumSymbols(ComponentName componentName, int i) throws RemoteException;

    void setPasswordMinimumNonLetter(ComponentName componentName, int i, int i2) throws RemoteException;

    int getPasswordMinimumNonLetter(ComponentName componentName, int i) throws RemoteException;

    void setPasswordHistoryLength(ComponentName componentName, int i, int i2) throws RemoteException;

    int getPasswordHistoryLength(ComponentName componentName, int i) throws RemoteException;

    void setPasswordExpirationTimeout(ComponentName componentName, long j, int i) throws RemoteException;

    long getPasswordExpirationTimeout(ComponentName componentName, int i) throws RemoteException;

    long getPasswordExpiration(ComponentName componentName, int i) throws RemoteException;

    boolean isActivePasswordSufficient(int i) throws RemoteException;

    int getCurrentFailedPasswordAttempts(int i) throws RemoteException;

    void setMaximumFailedPasswordsForWipe(ComponentName componentName, int i, int i2) throws RemoteException;

    int getMaximumFailedPasswordsForWipe(ComponentName componentName, int i) throws RemoteException;

    boolean resetPassword(String str, int i, int i2) throws RemoteException;

    void setMaximumTimeToLock(ComponentName componentName, long j, int i) throws RemoteException;

    long getMaximumTimeToLock(ComponentName componentName, int i) throws RemoteException;

    void lockNow() throws RemoteException;

    void wipeData(int i, int i2) throws RemoteException;

    ComponentName setGlobalProxy(ComponentName componentName, String str, String str2, int i) throws RemoteException;

    ComponentName getGlobalProxyAdmin(int i) throws RemoteException;

    int setStorageEncryption(ComponentName componentName, boolean z, int i) throws RemoteException;

    boolean getStorageEncryption(ComponentName componentName, int i) throws RemoteException;

    int getStorageEncryptionStatus(int i) throws RemoteException;

    void setCameraDisabled(ComponentName componentName, boolean z, int i) throws RemoteException;

    boolean getCameraDisabled(ComponentName componentName, int i) throws RemoteException;

    void setKeyguardDisabledFeatures(ComponentName componentName, int i, int i2) throws RemoteException;

    int getKeyguardDisabledFeatures(ComponentName componentName, int i) throws RemoteException;

    void setActiveAdmin(ComponentName componentName, boolean z, int i) throws RemoteException;

    boolean isAdminActive(ComponentName componentName, int i) throws RemoteException;

    List<ComponentName> getActiveAdmins(int i) throws RemoteException;

    boolean packageHasActiveAdmins(String str, int i) throws RemoteException;

    void getRemoveWarning(ComponentName componentName, RemoteCallback remoteCallback, int i) throws RemoteException;

    void removeActiveAdmin(ComponentName componentName, int i) throws RemoteException;

    boolean hasGrantedPolicy(ComponentName componentName, int i, int i2) throws RemoteException;

    void setActivePasswordState(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) throws RemoteException;

    void reportFailedPasswordAttempt(int i) throws RemoteException;

    void reportSuccessfulPasswordAttempt(int i) throws RemoteException;

    boolean setDeviceOwner(String str, String str2) throws RemoteException;

    boolean isDeviceOwner(String str) throws RemoteException;

    String getDeviceOwner() throws RemoteException;

    String getDeviceOwnerName() throws RemoteException;

    boolean installCaCert(byte[] bArr) throws RemoteException;

    void uninstallCaCert(byte[] bArr) throws RemoteException;

    /* loaded from: IDevicePolicyManager$Stub.class */
    public static abstract class Stub extends Binder implements IDevicePolicyManager {
        private static final String DESCRIPTOR = "android.app.admin.IDevicePolicyManager";
        static final int TRANSACTION_setPasswordQuality = 1;
        static final int TRANSACTION_getPasswordQuality = 2;
        static final int TRANSACTION_setPasswordMinimumLength = 3;
        static final int TRANSACTION_getPasswordMinimumLength = 4;
        static final int TRANSACTION_setPasswordMinimumUpperCase = 5;
        static final int TRANSACTION_getPasswordMinimumUpperCase = 6;
        static final int TRANSACTION_setPasswordMinimumLowerCase = 7;
        static final int TRANSACTION_getPasswordMinimumLowerCase = 8;
        static final int TRANSACTION_setPasswordMinimumLetters = 9;
        static final int TRANSACTION_getPasswordMinimumLetters = 10;
        static final int TRANSACTION_setPasswordMinimumNumeric = 11;
        static final int TRANSACTION_getPasswordMinimumNumeric = 12;
        static final int TRANSACTION_setPasswordMinimumSymbols = 13;
        static final int TRANSACTION_getPasswordMinimumSymbols = 14;
        static final int TRANSACTION_setPasswordMinimumNonLetter = 15;
        static final int TRANSACTION_getPasswordMinimumNonLetter = 16;
        static final int TRANSACTION_setPasswordHistoryLength = 17;
        static final int TRANSACTION_getPasswordHistoryLength = 18;
        static final int TRANSACTION_setPasswordExpirationTimeout = 19;
        static final int TRANSACTION_getPasswordExpirationTimeout = 20;
        static final int TRANSACTION_getPasswordExpiration = 21;
        static final int TRANSACTION_isActivePasswordSufficient = 22;
        static final int TRANSACTION_getCurrentFailedPasswordAttempts = 23;
        static final int TRANSACTION_setMaximumFailedPasswordsForWipe = 24;
        static final int TRANSACTION_getMaximumFailedPasswordsForWipe = 25;
        static final int TRANSACTION_resetPassword = 26;
        static final int TRANSACTION_setMaximumTimeToLock = 27;
        static final int TRANSACTION_getMaximumTimeToLock = 28;
        static final int TRANSACTION_lockNow = 29;
        static final int TRANSACTION_wipeData = 30;
        static final int TRANSACTION_setGlobalProxy = 31;
        static final int TRANSACTION_getGlobalProxyAdmin = 32;
        static final int TRANSACTION_setStorageEncryption = 33;
        static final int TRANSACTION_getStorageEncryption = 34;
        static final int TRANSACTION_getStorageEncryptionStatus = 35;
        static final int TRANSACTION_setCameraDisabled = 36;
        static final int TRANSACTION_getCameraDisabled = 37;
        static final int TRANSACTION_setKeyguardDisabledFeatures = 38;
        static final int TRANSACTION_getKeyguardDisabledFeatures = 39;
        static final int TRANSACTION_setActiveAdmin = 40;
        static final int TRANSACTION_isAdminActive = 41;
        static final int TRANSACTION_getActiveAdmins = 42;
        static final int TRANSACTION_packageHasActiveAdmins = 43;
        static final int TRANSACTION_getRemoveWarning = 44;
        static final int TRANSACTION_removeActiveAdmin = 45;
        static final int TRANSACTION_hasGrantedPolicy = 46;
        static final int TRANSACTION_setActivePasswordState = 47;
        static final int TRANSACTION_reportFailedPasswordAttempt = 48;
        static final int TRANSACTION_reportSuccessfulPasswordAttempt = 49;
        static final int TRANSACTION_setDeviceOwner = 50;
        static final int TRANSACTION_isDeviceOwner = 51;
        static final int TRANSACTION_getDeviceOwner = 52;
        static final int TRANSACTION_getDeviceOwnerName = 53;
        static final int TRANSACTION_installCaCert = 54;
        static final int TRANSACTION_uninstallCaCert = 55;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDevicePolicyManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDevicePolicyManager)) {
                return (IDevicePolicyManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ComponentName _arg0;
            ComponentName _arg02;
            ComponentName _arg03;
            RemoteCallback _arg1;
            ComponentName _arg04;
            ComponentName _arg05;
            ComponentName _arg06;
            ComponentName _arg07;
            ComponentName _arg08;
            ComponentName _arg09;
            ComponentName _arg010;
            ComponentName _arg011;
            ComponentName _arg012;
            ComponentName _arg013;
            ComponentName _arg014;
            ComponentName _arg015;
            ComponentName _arg016;
            ComponentName _arg017;
            ComponentName _arg018;
            ComponentName _arg019;
            ComponentName _arg020;
            ComponentName _arg021;
            ComponentName _arg022;
            ComponentName _arg023;
            ComponentName _arg024;
            ComponentName _arg025;
            ComponentName _arg026;
            ComponentName _arg027;
            ComponentName _arg028;
            ComponentName _arg029;
            ComponentName _arg030;
            ComponentName _arg031;
            ComponentName _arg032;
            ComponentName _arg033;
            ComponentName _arg034;
            ComponentName _arg035;
            ComponentName _arg036;
            ComponentName _arg037;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg037 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg037 = null;
                    }
                    int _arg12 = data.readInt();
                    int _arg2 = data.readInt();
                    setPasswordQuality(_arg037, _arg12, _arg2);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg036 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg036 = null;
                    }
                    int _arg13 = data.readInt();
                    int _result = getPasswordQuality(_arg036, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg035 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg035 = null;
                    }
                    int _arg14 = data.readInt();
                    int _arg22 = data.readInt();
                    setPasswordMinimumLength(_arg035, _arg14, _arg22);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg034 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg034 = null;
                    }
                    int _arg15 = data.readInt();
                    int _result2 = getPasswordMinimumLength(_arg034, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg033 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg033 = null;
                    }
                    int _arg16 = data.readInt();
                    int _arg23 = data.readInt();
                    setPasswordMinimumUpperCase(_arg033, _arg16, _arg23);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg032 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg032 = null;
                    }
                    int _arg17 = data.readInt();
                    int _result3 = getPasswordMinimumUpperCase(_arg032, _arg17);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg031 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg031 = null;
                    }
                    int _arg18 = data.readInt();
                    int _arg24 = data.readInt();
                    setPasswordMinimumLowerCase(_arg031, _arg18, _arg24);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg030 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg030 = null;
                    }
                    int _arg19 = data.readInt();
                    int _result4 = getPasswordMinimumLowerCase(_arg030, _arg19);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg029 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg029 = null;
                    }
                    int _arg110 = data.readInt();
                    int _arg25 = data.readInt();
                    setPasswordMinimumLetters(_arg029, _arg110, _arg25);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg028 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg028 = null;
                    }
                    int _arg111 = data.readInt();
                    int _result5 = getPasswordMinimumLetters(_arg028, _arg111);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg027 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg027 = null;
                    }
                    int _arg112 = data.readInt();
                    int _arg26 = data.readInt();
                    setPasswordMinimumNumeric(_arg027, _arg112, _arg26);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg026 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg026 = null;
                    }
                    int _arg113 = data.readInt();
                    int _result6 = getPasswordMinimumNumeric(_arg026, _arg113);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg025 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg025 = null;
                    }
                    int _arg114 = data.readInt();
                    int _arg27 = data.readInt();
                    setPasswordMinimumSymbols(_arg025, _arg114, _arg27);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg024 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg024 = null;
                    }
                    int _arg115 = data.readInt();
                    int _result7 = getPasswordMinimumSymbols(_arg024, _arg115);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg023 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg023 = null;
                    }
                    int _arg116 = data.readInt();
                    int _arg28 = data.readInt();
                    setPasswordMinimumNonLetter(_arg023, _arg116, _arg28);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg022 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg022 = null;
                    }
                    int _arg117 = data.readInt();
                    int _result8 = getPasswordMinimumNonLetter(_arg022, _arg117);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg021 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg021 = null;
                    }
                    int _arg118 = data.readInt();
                    int _arg29 = data.readInt();
                    setPasswordHistoryLength(_arg021, _arg118, _arg29);
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg020 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg020 = null;
                    }
                    int _arg119 = data.readInt();
                    int _result9 = getPasswordHistoryLength(_arg020, _arg119);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg019 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg019 = null;
                    }
                    long _arg120 = data.readLong();
                    int _arg210 = data.readInt();
                    setPasswordExpirationTimeout(_arg019, _arg120, _arg210);
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg018 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg018 = null;
                    }
                    int _arg121 = data.readInt();
                    long _result10 = getPasswordExpirationTimeout(_arg018, _arg121);
                    reply.writeNoException();
                    reply.writeLong(_result10);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg017 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg017 = null;
                    }
                    int _arg122 = data.readInt();
                    long _result11 = getPasswordExpiration(_arg017, _arg122);
                    reply.writeNoException();
                    reply.writeLong(_result11);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg038 = data.readInt();
                    boolean _result12 = isActivePasswordSufficient(_arg038);
                    reply.writeNoException();
                    reply.writeInt(_result12 ? 1 : 0);
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg039 = data.readInt();
                    int _result13 = getCurrentFailedPasswordAttempts(_arg039);
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg016 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg016 = null;
                    }
                    int _arg123 = data.readInt();
                    int _arg211 = data.readInt();
                    setMaximumFailedPasswordsForWipe(_arg016, _arg123, _arg211);
                    reply.writeNoException();
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg015 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg015 = null;
                    }
                    int _arg124 = data.readInt();
                    int _result14 = getMaximumFailedPasswordsForWipe(_arg015, _arg124);
                    reply.writeNoException();
                    reply.writeInt(_result14);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg040 = data.readString();
                    int _arg125 = data.readInt();
                    int _arg212 = data.readInt();
                    boolean _result15 = resetPassword(_arg040, _arg125, _arg212);
                    reply.writeNoException();
                    reply.writeInt(_result15 ? 1 : 0);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg014 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg014 = null;
                    }
                    long _arg126 = data.readLong();
                    int _arg213 = data.readInt();
                    setMaximumTimeToLock(_arg014, _arg126, _arg213);
                    reply.writeNoException();
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg013 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg013 = null;
                    }
                    int _arg127 = data.readInt();
                    long _result16 = getMaximumTimeToLock(_arg013, _arg127);
                    reply.writeNoException();
                    reply.writeLong(_result16);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    lockNow();
                    reply.writeNoException();
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg041 = data.readInt();
                    int _arg128 = data.readInt();
                    wipeData(_arg041, _arg128);
                    reply.writeNoException();
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg012 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg012 = null;
                    }
                    String _arg129 = data.readString();
                    String _arg214 = data.readString();
                    int _arg3 = data.readInt();
                    ComponentName _result17 = setGlobalProxy(_arg012, _arg129, _arg214, _arg3);
                    reply.writeNoException();
                    if (_result17 != null) {
                        reply.writeInt(1);
                        _result17.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg042 = data.readInt();
                    ComponentName _result18 = getGlobalProxyAdmin(_arg042);
                    reply.writeNoException();
                    if (_result18 != null) {
                        reply.writeInt(1);
                        _result18.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg011 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg011 = null;
                    }
                    boolean _arg130 = 0 != data.readInt();
                    int _arg215 = data.readInt();
                    int _result19 = setStorageEncryption(_arg011, _arg130, _arg215);
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg010 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    int _arg131 = data.readInt();
                    boolean _result20 = getStorageEncryption(_arg010, _arg131);
                    reply.writeNoException();
                    reply.writeInt(_result20 ? 1 : 0);
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg043 = data.readInt();
                    int _result21 = getStorageEncryptionStatus(_arg043);
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg09 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg09 = null;
                    }
                    boolean _arg132 = 0 != data.readInt();
                    int _arg216 = data.readInt();
                    setCameraDisabled(_arg09, _arg132, _arg216);
                    reply.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    int _arg133 = data.readInt();
                    boolean _result22 = getCameraDisabled(_arg08, _arg133);
                    reply.writeNoException();
                    reply.writeInt(_result22 ? 1 : 0);
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    int _arg134 = data.readInt();
                    int _arg217 = data.readInt();
                    setKeyguardDisabledFeatures(_arg07, _arg134, _arg217);
                    reply.writeNoException();
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    int _arg135 = data.readInt();
                    int _result23 = getKeyguardDisabledFeatures(_arg06, _arg135);
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    boolean _arg136 = 0 != data.readInt();
                    int _arg218 = data.readInt();
                    setActiveAdmin(_arg05, _arg136, _arg218);
                    reply.writeNoException();
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    int _arg137 = data.readInt();
                    boolean _result24 = isAdminActive(_arg04, _arg137);
                    reply.writeNoException();
                    reply.writeInt(_result24 ? 1 : 0);
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg044 = data.readInt();
                    List<ComponentName> _result25 = getActiveAdmins(_arg044);
                    reply.writeNoException();
                    reply.writeTypedList(_result25);
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg045 = data.readString();
                    int _arg138 = data.readInt();
                    boolean _result26 = packageHasActiveAdmins(_arg045, _arg138);
                    reply.writeNoException();
                    reply.writeInt(_result26 ? 1 : 0);
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg1 = RemoteCallback.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    int _arg219 = data.readInt();
                    getRemoveWarning(_arg03, _arg1, _arg219);
                    reply.writeNoException();
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _arg139 = data.readInt();
                    removeActiveAdmin(_arg02, _arg139);
                    reply.writeNoException();
                    return true;
                case 46:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg140 = data.readInt();
                    int _arg220 = data.readInt();
                    boolean _result27 = hasGrantedPolicy(_arg0, _arg140, _arg220);
                    reply.writeNoException();
                    reply.writeInt(_result27 ? 1 : 0);
                    return true;
                case 47:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg046 = data.readInt();
                    int _arg141 = data.readInt();
                    int _arg221 = data.readInt();
                    int _arg32 = data.readInt();
                    int _arg4 = data.readInt();
                    int _arg5 = data.readInt();
                    int _arg6 = data.readInt();
                    int _arg7 = data.readInt();
                    int _arg8 = data.readInt();
                    setActivePasswordState(_arg046, _arg141, _arg221, _arg32, _arg4, _arg5, _arg6, _arg7, _arg8);
                    reply.writeNoException();
                    return true;
                case 48:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg047 = data.readInt();
                    reportFailedPasswordAttempt(_arg047);
                    reply.writeNoException();
                    return true;
                case 49:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg048 = data.readInt();
                    reportSuccessfulPasswordAttempt(_arg048);
                    reply.writeNoException();
                    return true;
                case 50:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg049 = data.readString();
                    String _arg142 = data.readString();
                    boolean _result28 = setDeviceOwner(_arg049, _arg142);
                    reply.writeNoException();
                    reply.writeInt(_result28 ? 1 : 0);
                    return true;
                case 51:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg050 = data.readString();
                    boolean _result29 = isDeviceOwner(_arg050);
                    reply.writeNoException();
                    reply.writeInt(_result29 ? 1 : 0);
                    return true;
                case 52:
                    data.enforceInterface(DESCRIPTOR);
                    String _result30 = getDeviceOwner();
                    reply.writeNoException();
                    reply.writeString(_result30);
                    return true;
                case 53:
                    data.enforceInterface(DESCRIPTOR);
                    String _result31 = getDeviceOwnerName();
                    reply.writeNoException();
                    reply.writeString(_result31);
                    return true;
                case 54:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg051 = data.createByteArray();
                    boolean _result32 = installCaCert(_arg051);
                    reply.writeNoException();
                    reply.writeInt(_result32 ? 1 : 0);
                    return true;
                case 55:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg052 = data.createByteArray();
                    uninstallCaCert(_arg052);
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
        /* loaded from: IDevicePolicyManager$Stub$Proxy.class */
        public static class Proxy implements IDevicePolicyManager {
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordQuality(ComponentName who, int quality, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(quality);
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordQuality(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(2, _data, _reply, 0);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumLength(ComponentName who, int length, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(length);
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

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumLength(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(4, _data, _reply, 0);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumUpperCase(ComponentName who, int length, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(length);
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumUpperCase(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumLowerCase(ComponentName who, int length, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(length);
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumLowerCase(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumLetters(ComponentName who, int length, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(length);
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumLetters(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumNumeric(ComponentName who, int length, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(length);
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

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumNumeric(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumSymbols(ComponentName who, int length, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(length);
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumSymbols(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumNonLetter(ComponentName who, int length, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(length);
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

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumNonLetter(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(16, _data, _reply, 0);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordHistoryLength(ComponentName who, int length, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(length);
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordHistoryLength(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(18, _data, _reply, 0);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordExpirationTimeout(ComponentName who, long expiration, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeLong(expiration);
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public long getPasswordExpirationTimeout(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long getPasswordExpiration(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isActivePasswordSufficient(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getCurrentFailedPasswordAttempts(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(23, _data, _reply, 0);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setMaximumFailedPasswordsForWipe(ComponentName admin, int num, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (admin != null) {
                        _data.writeInt(1);
                        admin.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(num);
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public int getMaximumFailedPasswordsForWipe(ComponentName admin, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (admin != null) {
                        _data.writeInt(1);
                        admin.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(25, _data, _reply, 0);
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

            @Override // android.app.admin.IDevicePolicyManager
            public boolean resetPassword(String password, int flags, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(password);
                    _data.writeInt(flags);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setMaximumTimeToLock(ComponentName who, long timeMs, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeLong(timeMs);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long getMaximumTimeToLock(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void lockNow() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void wipeData(int flags, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ComponentName setGlobalProxy(ComponentName admin, String proxySpec, String exclusionList, int userHandle) throws RemoteException {
                ComponentName _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (admin != null) {
                        _data.writeInt(1);
                        admin.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(proxySpec);
                    _data.writeString(exclusionList);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ComponentName.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ComponentName getGlobalProxyAdmin(int userHandle) throws RemoteException {
                ComponentName _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ComponentName.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int setStorageEncryption(ComponentName who, boolean encrypt, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(encrypt ? 1 : 0);
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getStorageEncryption(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getStorageEncryptionStatus(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setCameraDisabled(ComponentName who, boolean disabled, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(disabled ? 1 : 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getCameraDisabled(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setKeyguardDisabledFeatures(ComponentName who, int which, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(which);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getKeyguardDisabledFeatures(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (who != null) {
                        _data.writeInt(1);
                        who.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(39, _data, _reply, 0);
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setActiveAdmin(ComponentName policyReceiver, boolean refreshing, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (policyReceiver != null) {
                        _data.writeInt(1);
                        policyReceiver.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(refreshing ? 1 : 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isAdminActive(ComponentName policyReceiver, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (policyReceiver != null) {
                        _data.writeInt(1);
                        policyReceiver.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<ComponentName> getActiveAdmins(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    List<ComponentName> _result = _reply.createTypedArrayList(ComponentName.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean packageHasActiveAdmins(String packageName, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void getRemoveWarning(ComponentName policyReceiver, RemoteCallback result, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (policyReceiver != null) {
                        _data.writeInt(1);
                        policyReceiver.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (result != null) {
                        _data.writeInt(1);
                        result.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void removeActiveAdmin(ComponentName policyReceiver, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (policyReceiver != null) {
                        _data.writeInt(1);
                        policyReceiver.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean hasGrantedPolicy(ComponentName policyReceiver, int usesPolicy, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (policyReceiver != null) {
                        _data.writeInt(1);
                        policyReceiver.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(usesPolicy);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setActivePasswordState(int quality, int length, int letters, int uppercase, int lowercase, int numbers, int symbols, int nonletter, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(quality);
                    _data.writeInt(length);
                    _data.writeInt(letters);
                    _data.writeInt(uppercase);
                    _data.writeInt(lowercase);
                    _data.writeInt(numbers);
                    _data.writeInt(symbols);
                    _data.writeInt(nonletter);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void reportFailedPasswordAttempt(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void reportSuccessfulPasswordAttempt(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setDeviceOwner(String packageName, String ownerName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(ownerName);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isDeviceOwner(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String getDeviceOwner() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(52, _data, _reply, 0);
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

            @Override // android.app.admin.IDevicePolicyManager
            public String getDeviceOwnerName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(53, _data, _reply, 0);
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

            @Override // android.app.admin.IDevicePolicyManager
            public boolean installCaCert(byte[] certBuffer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(certBuffer);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void uninstallCaCert(byte[] certBuffer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(certBuffer);
                    this.mRemote.transact(55, _data, _reply, 0);
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