package android.content.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.IPackageStatsObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: IPackageManager.class */
public interface IPackageManager extends IInterface {
    PackageInfo getPackageInfo(String str, int i, int i2) throws RemoteException;

    int getPackageUid(String str, int i) throws RemoteException;

    int[] getPackageGids(String str) throws RemoteException;

    String[] currentToCanonicalPackageNames(String[] strArr) throws RemoteException;

    String[] canonicalToCurrentPackageNames(String[] strArr) throws RemoteException;

    PermissionInfo getPermissionInfo(String str, int i) throws RemoteException;

    List<PermissionInfo> queryPermissionsByGroup(String str, int i) throws RemoteException;

    PermissionGroupInfo getPermissionGroupInfo(String str, int i) throws RemoteException;

    List<PermissionGroupInfo> getAllPermissionGroups(int i) throws RemoteException;

    ApplicationInfo getApplicationInfo(String str, int i, int i2) throws RemoteException;

    ActivityInfo getActivityInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    ActivityInfo getReceiverInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    ServiceInfo getServiceInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    ProviderInfo getProviderInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    int checkPermission(String str, String str2) throws RemoteException;

    int checkUidPermission(String str, int i) throws RemoteException;

    boolean addPermission(PermissionInfo permissionInfo) throws RemoteException;

    void removePermission(String str) throws RemoteException;

    void grantPermission(String str, String str2) throws RemoteException;

    void revokePermission(String str, String str2) throws RemoteException;

    boolean isProtectedBroadcast(String str) throws RemoteException;

    int checkSignatures(String str, String str2) throws RemoteException;

    int checkUidSignatures(int i, int i2) throws RemoteException;

    String[] getPackagesForUid(int i) throws RemoteException;

    String getNameForUid(int i) throws RemoteException;

    int getUidForSharedUser(String str) throws RemoteException;

    int getFlagsForUid(int i) throws RemoteException;

    ResolveInfo resolveIntent(Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentActivities(Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentActivityOptions(ComponentName componentName, Intent[] intentArr, String[] strArr, Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentReceivers(Intent intent, String str, int i, int i2) throws RemoteException;

    ResolveInfo resolveService(Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentServices(Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentContentProviders(Intent intent, String str, int i, int i2) throws RemoteException;

    ParceledListSlice getInstalledPackages(int i, int i2) throws RemoteException;

    ParceledListSlice getPackagesHoldingPermissions(String[] strArr, int i, int i2) throws RemoteException;

    ParceledListSlice getInstalledApplications(int i, int i2) throws RemoteException;

    List<ApplicationInfo> getPersistentApplications(int i) throws RemoteException;

    ProviderInfo resolveContentProvider(String str, int i, int i2) throws RemoteException;

    void querySyncProviders(List<String> list, List<ProviderInfo> list2) throws RemoteException;

    List<ProviderInfo> queryContentProviders(String str, int i, int i2) throws RemoteException;

    InstrumentationInfo getInstrumentationInfo(ComponentName componentName, int i) throws RemoteException;

    List<InstrumentationInfo> queryInstrumentation(String str, int i) throws RemoteException;

    void installPackage(Uri uri, IPackageInstallObserver iPackageInstallObserver, int i, String str) throws RemoteException;

    void finishPackageInstall(int i) throws RemoteException;

    void setInstallerPackageName(String str, String str2) throws RemoteException;

    void deletePackageAsUser(String str, IPackageDeleteObserver iPackageDeleteObserver, int i, int i2) throws RemoteException;

    String getInstallerPackageName(String str) throws RemoteException;

    void addPackageToPreferred(String str) throws RemoteException;

    void removePackageFromPreferred(String str) throws RemoteException;

    List<PackageInfo> getPreferredPackages(int i) throws RemoteException;

    void resetPreferredActivities(int i) throws RemoteException;

    ResolveInfo getLastChosenActivity(Intent intent, String str, int i) throws RemoteException;

    void setLastChosenActivity(Intent intent, String str, int i, IntentFilter intentFilter, int i2, ComponentName componentName) throws RemoteException;

    void addPreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName, int i2) throws RemoteException;

    void replacePreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName) throws RemoteException;

    void clearPackagePreferredActivities(String str) throws RemoteException;

    int getPreferredActivities(List<IntentFilter> list, List<ComponentName> list2, String str) throws RemoteException;

    ComponentName getHomeActivities(List<ResolveInfo> list) throws RemoteException;

    void setComponentEnabledSetting(ComponentName componentName, int i, int i2, int i3) throws RemoteException;

    int getComponentEnabledSetting(ComponentName componentName, int i) throws RemoteException;

    void setApplicationEnabledSetting(String str, int i, int i2, int i3, String str2) throws RemoteException;

    int getApplicationEnabledSetting(String str, int i) throws RemoteException;

    void setPackageStoppedState(String str, boolean z, int i) throws RemoteException;

    void freeStorageAndNotify(long j, IPackageDataObserver iPackageDataObserver) throws RemoteException;

    void freeStorage(long j, IntentSender intentSender) throws RemoteException;

    void deleteApplicationCacheFiles(String str, IPackageDataObserver iPackageDataObserver) throws RemoteException;

    void clearApplicationUserData(String str, IPackageDataObserver iPackageDataObserver, int i) throws RemoteException;

    void getPackageSizeInfo(String str, int i, IPackageStatsObserver iPackageStatsObserver) throws RemoteException;

    String[] getSystemSharedLibraryNames() throws RemoteException;

    FeatureInfo[] getSystemAvailableFeatures() throws RemoteException;

    boolean hasSystemFeature(String str) throws RemoteException;

    void enterSafeMode() throws RemoteException;

    boolean isSafeMode() throws RemoteException;

    void systemReady() throws RemoteException;

    boolean hasSystemUidErrors() throws RemoteException;

    void performBootDexOpt() throws RemoteException;

    boolean performDexOpt(String str) throws RemoteException;

    void updateExternalMediaStatus(boolean z, boolean z2) throws RemoteException;

    PackageCleanItem nextPackageToClean(PackageCleanItem packageCleanItem) throws RemoteException;

    void movePackage(String str, IPackageMoveObserver iPackageMoveObserver, int i) throws RemoteException;

    boolean addPermissionAsync(PermissionInfo permissionInfo) throws RemoteException;

    boolean setInstallLocation(int i) throws RemoteException;

    int getInstallLocation() throws RemoteException;

    void installPackageWithVerification(Uri uri, IPackageInstallObserver iPackageInstallObserver, int i, String str, Uri uri2, ManifestDigest manifestDigest, ContainerEncryptionParams containerEncryptionParams) throws RemoteException;

    void installPackageWithVerificationAndEncryption(Uri uri, IPackageInstallObserver iPackageInstallObserver, int i, String str, VerificationParams verificationParams, ContainerEncryptionParams containerEncryptionParams) throws RemoteException;

    int installExistingPackageAsUser(String str, int i) throws RemoteException;

    void verifyPendingInstall(int i, int i2) throws RemoteException;

    void extendVerificationTimeout(int i, int i2, long j) throws RemoteException;

    VerifierDeviceIdentity getVerifierDeviceIdentity() throws RemoteException;

    boolean isFirstBoot() throws RemoteException;

    boolean isOnlyCoreApps() throws RemoteException;

    void setPermissionEnforced(String str, boolean z) throws RemoteException;

    boolean isPermissionEnforced(String str) throws RemoteException;

    boolean isStorageLow() throws RemoteException;

    boolean setApplicationBlockedSettingAsUser(String str, boolean z, int i) throws RemoteException;

    boolean getApplicationBlockedSettingAsUser(String str, int i) throws RemoteException;

    /* loaded from: IPackageManager$Stub.class */
    public static abstract class Stub extends Binder implements IPackageManager {
        private static final String DESCRIPTOR = "android.content.pm.IPackageManager";
        static final int TRANSACTION_getPackageInfo = 1;
        static final int TRANSACTION_getPackageUid = 2;
        static final int TRANSACTION_getPackageGids = 3;
        static final int TRANSACTION_currentToCanonicalPackageNames = 4;
        static final int TRANSACTION_canonicalToCurrentPackageNames = 5;
        static final int TRANSACTION_getPermissionInfo = 6;
        static final int TRANSACTION_queryPermissionsByGroup = 7;
        static final int TRANSACTION_getPermissionGroupInfo = 8;
        static final int TRANSACTION_getAllPermissionGroups = 9;
        static final int TRANSACTION_getApplicationInfo = 10;
        static final int TRANSACTION_getActivityInfo = 11;
        static final int TRANSACTION_getReceiverInfo = 12;
        static final int TRANSACTION_getServiceInfo = 13;
        static final int TRANSACTION_getProviderInfo = 14;
        static final int TRANSACTION_checkPermission = 15;
        static final int TRANSACTION_checkUidPermission = 16;
        static final int TRANSACTION_addPermission = 17;
        static final int TRANSACTION_removePermission = 18;
        static final int TRANSACTION_grantPermission = 19;
        static final int TRANSACTION_revokePermission = 20;
        static final int TRANSACTION_isProtectedBroadcast = 21;
        static final int TRANSACTION_checkSignatures = 22;
        static final int TRANSACTION_checkUidSignatures = 23;
        static final int TRANSACTION_getPackagesForUid = 24;
        static final int TRANSACTION_getNameForUid = 25;
        static final int TRANSACTION_getUidForSharedUser = 26;
        static final int TRANSACTION_getFlagsForUid = 27;
        static final int TRANSACTION_resolveIntent = 28;
        static final int TRANSACTION_queryIntentActivities = 29;
        static final int TRANSACTION_queryIntentActivityOptions = 30;
        static final int TRANSACTION_queryIntentReceivers = 31;
        static final int TRANSACTION_resolveService = 32;
        static final int TRANSACTION_queryIntentServices = 33;
        static final int TRANSACTION_queryIntentContentProviders = 34;
        static final int TRANSACTION_getInstalledPackages = 35;
        static final int TRANSACTION_getPackagesHoldingPermissions = 36;
        static final int TRANSACTION_getInstalledApplications = 37;
        static final int TRANSACTION_getPersistentApplications = 38;
        static final int TRANSACTION_resolveContentProvider = 39;
        static final int TRANSACTION_querySyncProviders = 40;
        static final int TRANSACTION_queryContentProviders = 41;
        static final int TRANSACTION_getInstrumentationInfo = 42;
        static final int TRANSACTION_queryInstrumentation = 43;
        static final int TRANSACTION_installPackage = 44;
        static final int TRANSACTION_finishPackageInstall = 45;
        static final int TRANSACTION_setInstallerPackageName = 46;
        static final int TRANSACTION_deletePackageAsUser = 47;
        static final int TRANSACTION_getInstallerPackageName = 48;
        static final int TRANSACTION_addPackageToPreferred = 49;
        static final int TRANSACTION_removePackageFromPreferred = 50;
        static final int TRANSACTION_getPreferredPackages = 51;
        static final int TRANSACTION_resetPreferredActivities = 52;
        static final int TRANSACTION_getLastChosenActivity = 53;
        static final int TRANSACTION_setLastChosenActivity = 54;
        static final int TRANSACTION_addPreferredActivity = 55;
        static final int TRANSACTION_replacePreferredActivity = 56;
        static final int TRANSACTION_clearPackagePreferredActivities = 57;
        static final int TRANSACTION_getPreferredActivities = 58;
        static final int TRANSACTION_getHomeActivities = 59;
        static final int TRANSACTION_setComponentEnabledSetting = 60;
        static final int TRANSACTION_getComponentEnabledSetting = 61;
        static final int TRANSACTION_setApplicationEnabledSetting = 62;
        static final int TRANSACTION_getApplicationEnabledSetting = 63;
        static final int TRANSACTION_setPackageStoppedState = 64;
        static final int TRANSACTION_freeStorageAndNotify = 65;
        static final int TRANSACTION_freeStorage = 66;
        static final int TRANSACTION_deleteApplicationCacheFiles = 67;
        static final int TRANSACTION_clearApplicationUserData = 68;
        static final int TRANSACTION_getPackageSizeInfo = 69;
        static final int TRANSACTION_getSystemSharedLibraryNames = 70;
        static final int TRANSACTION_getSystemAvailableFeatures = 71;
        static final int TRANSACTION_hasSystemFeature = 72;
        static final int TRANSACTION_enterSafeMode = 73;
        static final int TRANSACTION_isSafeMode = 74;
        static final int TRANSACTION_systemReady = 75;
        static final int TRANSACTION_hasSystemUidErrors = 76;
        static final int TRANSACTION_performBootDexOpt = 77;
        static final int TRANSACTION_performDexOpt = 78;
        static final int TRANSACTION_updateExternalMediaStatus = 79;
        static final int TRANSACTION_nextPackageToClean = 80;
        static final int TRANSACTION_movePackage = 81;
        static final int TRANSACTION_addPermissionAsync = 82;
        static final int TRANSACTION_setInstallLocation = 83;
        static final int TRANSACTION_getInstallLocation = 84;
        static final int TRANSACTION_installPackageWithVerification = 85;
        static final int TRANSACTION_installPackageWithVerificationAndEncryption = 86;
        static final int TRANSACTION_installExistingPackageAsUser = 87;
        static final int TRANSACTION_verifyPendingInstall = 88;
        static final int TRANSACTION_extendVerificationTimeout = 89;
        static final int TRANSACTION_getVerifierDeviceIdentity = 90;
        static final int TRANSACTION_isFirstBoot = 91;
        static final int TRANSACTION_isOnlyCoreApps = 92;
        static final int TRANSACTION_setPermissionEnforced = 93;
        static final int TRANSACTION_isPermissionEnforced = 94;
        static final int TRANSACTION_isStorageLow = 95;
        static final int TRANSACTION_setApplicationBlockedSettingAsUser = 96;
        static final int TRANSACTION_getApplicationBlockedSettingAsUser = 97;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPackageManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPackageManager)) {
                return (IPackageManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Uri _arg0;
            VerificationParams _arg4;
            ContainerEncryptionParams _arg5;
            Uri _arg02;
            Uri _arg42;
            ManifestDigest _arg52;
            ContainerEncryptionParams _arg6;
            PermissionInfo _arg03;
            PackageCleanItem _arg04;
            IntentSender _arg1;
            ComponentName _arg05;
            ComponentName _arg06;
            IntentFilter _arg07;
            ComponentName _arg3;
            IntentFilter _arg08;
            ComponentName _arg32;
            Intent _arg09;
            IntentFilter _arg33;
            ComponentName _arg53;
            Intent _arg010;
            Uri _arg011;
            ComponentName _arg012;
            Intent _arg013;
            Intent _arg014;
            Intent _arg015;
            Intent _arg016;
            ComponentName _arg017;
            Intent _arg34;
            Intent _arg018;
            Intent _arg019;
            PermissionInfo _arg020;
            ComponentName _arg021;
            ComponentName _arg022;
            ComponentName _arg023;
            ComponentName _arg024;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg025 = data.readString();
                    int _arg12 = data.readInt();
                    int _arg2 = data.readInt();
                    PackageInfo _result = getPackageInfo(_arg025, _arg12, _arg2);
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
                    String _arg026 = data.readString();
                    int _arg13 = data.readInt();
                    int _result2 = getPackageUid(_arg026, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg027 = data.readString();
                    int[] _result3 = getPackageGids(_arg027);
                    reply.writeNoException();
                    reply.writeIntArray(_result3);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg028 = data.createStringArray();
                    String[] _result4 = currentToCanonicalPackageNames(_arg028);
                    reply.writeNoException();
                    reply.writeStringArray(_result4);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg029 = data.createStringArray();
                    String[] _result5 = canonicalToCurrentPackageNames(_arg029);
                    reply.writeNoException();
                    reply.writeStringArray(_result5);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg030 = data.readString();
                    int _arg14 = data.readInt();
                    PermissionInfo _result6 = getPermissionInfo(_arg030, _arg14);
                    reply.writeNoException();
                    if (_result6 != null) {
                        reply.writeInt(1);
                        _result6.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg031 = data.readString();
                    int _arg15 = data.readInt();
                    List<PermissionInfo> _result7 = queryPermissionsByGroup(_arg031, _arg15);
                    reply.writeNoException();
                    reply.writeTypedList(_result7);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg032 = data.readString();
                    int _arg16 = data.readInt();
                    PermissionGroupInfo _result8 = getPermissionGroupInfo(_arg032, _arg16);
                    reply.writeNoException();
                    if (_result8 != null) {
                        reply.writeInt(1);
                        _result8.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg033 = data.readInt();
                    List<PermissionGroupInfo> _result9 = getAllPermissionGroups(_arg033);
                    reply.writeNoException();
                    reply.writeTypedList(_result9);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg034 = data.readString();
                    int _arg17 = data.readInt();
                    int _arg22 = data.readInt();
                    ApplicationInfo _result10 = getApplicationInfo(_arg034, _arg17, _arg22);
                    reply.writeNoException();
                    if (_result10 != null) {
                        reply.writeInt(1);
                        _result10.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg024 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg024 = null;
                    }
                    int _arg18 = data.readInt();
                    int _arg23 = data.readInt();
                    ActivityInfo _result11 = getActivityInfo(_arg024, _arg18, _arg23);
                    reply.writeNoException();
                    if (_result11 != null) {
                        reply.writeInt(1);
                        _result11.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg023 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg023 = null;
                    }
                    int _arg19 = data.readInt();
                    int _arg24 = data.readInt();
                    ActivityInfo _result12 = getReceiverInfo(_arg023, _arg19, _arg24);
                    reply.writeNoException();
                    if (_result12 != null) {
                        reply.writeInt(1);
                        _result12.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg022 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg022 = null;
                    }
                    int _arg110 = data.readInt();
                    int _arg25 = data.readInt();
                    ServiceInfo _result13 = getServiceInfo(_arg022, _arg110, _arg25);
                    reply.writeNoException();
                    if (_result13 != null) {
                        reply.writeInt(1);
                        _result13.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg021 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg021 = null;
                    }
                    int _arg111 = data.readInt();
                    int _arg26 = data.readInt();
                    ProviderInfo _result14 = getProviderInfo(_arg021, _arg111, _arg26);
                    reply.writeNoException();
                    if (_result14 != null) {
                        reply.writeInt(1);
                        _result14.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg035 = data.readString();
                    String _arg112 = data.readString();
                    int _result15 = checkPermission(_arg035, _arg112);
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg036 = data.readString();
                    int _arg113 = data.readInt();
                    int _result16 = checkUidPermission(_arg036, _arg113);
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg020 = PermissionInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg020 = null;
                    }
                    boolean _result17 = addPermission(_arg020);
                    reply.writeNoException();
                    reply.writeInt(_result17 ? 1 : 0);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg037 = data.readString();
                    removePermission(_arg037);
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg038 = data.readString();
                    String _arg114 = data.readString();
                    grantPermission(_arg038, _arg114);
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg039 = data.readString();
                    String _arg115 = data.readString();
                    revokePermission(_arg039, _arg115);
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg040 = data.readString();
                    boolean _result18 = isProtectedBroadcast(_arg040);
                    reply.writeNoException();
                    reply.writeInt(_result18 ? 1 : 0);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg041 = data.readString();
                    String _arg116 = data.readString();
                    int _result19 = checkSignatures(_arg041, _arg116);
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg042 = data.readInt();
                    int _arg117 = data.readInt();
                    int _result20 = checkUidSignatures(_arg042, _arg117);
                    reply.writeNoException();
                    reply.writeInt(_result20);
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg043 = data.readInt();
                    String[] _result21 = getPackagesForUid(_arg043);
                    reply.writeNoException();
                    reply.writeStringArray(_result21);
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg044 = data.readInt();
                    String _result22 = getNameForUid(_arg044);
                    reply.writeNoException();
                    reply.writeString(_result22);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg045 = data.readString();
                    int _result23 = getUidForSharedUser(_arg045);
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg046 = data.readInt();
                    int _result24 = getFlagsForUid(_arg046);
                    reply.writeNoException();
                    reply.writeInt(_result24);
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg019 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg019 = null;
                    }
                    String _arg118 = data.readString();
                    int _arg27 = data.readInt();
                    int _arg35 = data.readInt();
                    ResolveInfo _result25 = resolveIntent(_arg019, _arg118, _arg27, _arg35);
                    reply.writeNoException();
                    if (_result25 != null) {
                        reply.writeInt(1);
                        _result25.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg018 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg018 = null;
                    }
                    String _arg119 = data.readString();
                    int _arg28 = data.readInt();
                    int _arg36 = data.readInt();
                    List<ResolveInfo> _result26 = queryIntentActivities(_arg018, _arg119, _arg28, _arg36);
                    reply.writeNoException();
                    reply.writeTypedList(_result26);
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg017 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg017 = null;
                    }
                    Intent[] _arg120 = (Intent[]) data.createTypedArray(Intent.CREATOR);
                    String[] _arg29 = data.createStringArray();
                    if (0 != data.readInt()) {
                        _arg34 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg34 = null;
                    }
                    String _arg43 = data.readString();
                    int _arg54 = data.readInt();
                    int _arg62 = data.readInt();
                    List<ResolveInfo> _result27 = queryIntentActivityOptions(_arg017, _arg120, _arg29, _arg34, _arg43, _arg54, _arg62);
                    reply.writeNoException();
                    reply.writeTypedList(_result27);
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg016 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg016 = null;
                    }
                    String _arg121 = data.readString();
                    int _arg210 = data.readInt();
                    int _arg37 = data.readInt();
                    List<ResolveInfo> _result28 = queryIntentReceivers(_arg016, _arg121, _arg210, _arg37);
                    reply.writeNoException();
                    reply.writeTypedList(_result28);
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg015 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg015 = null;
                    }
                    String _arg122 = data.readString();
                    int _arg211 = data.readInt();
                    int _arg38 = data.readInt();
                    ResolveInfo _result29 = resolveService(_arg015, _arg122, _arg211, _arg38);
                    reply.writeNoException();
                    if (_result29 != null) {
                        reply.writeInt(1);
                        _result29.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg014 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg014 = null;
                    }
                    String _arg123 = data.readString();
                    int _arg212 = data.readInt();
                    int _arg39 = data.readInt();
                    List<ResolveInfo> _result30 = queryIntentServices(_arg014, _arg123, _arg212, _arg39);
                    reply.writeNoException();
                    reply.writeTypedList(_result30);
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg013 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg013 = null;
                    }
                    String _arg124 = data.readString();
                    int _arg213 = data.readInt();
                    int _arg310 = data.readInt();
                    List<ResolveInfo> _result31 = queryIntentContentProviders(_arg013, _arg124, _arg213, _arg310);
                    reply.writeNoException();
                    reply.writeTypedList(_result31);
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg047 = data.readInt();
                    int _arg125 = data.readInt();
                    ParceledListSlice _result32 = getInstalledPackages(_arg047, _arg125);
                    reply.writeNoException();
                    if (_result32 != null) {
                        reply.writeInt(1);
                        _result32.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg048 = data.createStringArray();
                    int _arg126 = data.readInt();
                    int _arg214 = data.readInt();
                    ParceledListSlice _result33 = getPackagesHoldingPermissions(_arg048, _arg126, _arg214);
                    reply.writeNoException();
                    if (_result33 != null) {
                        reply.writeInt(1);
                        _result33.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg049 = data.readInt();
                    int _arg127 = data.readInt();
                    ParceledListSlice _result34 = getInstalledApplications(_arg049, _arg127);
                    reply.writeNoException();
                    if (_result34 != null) {
                        reply.writeInt(1);
                        _result34.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg050 = data.readInt();
                    List<ApplicationInfo> _result35 = getPersistentApplications(_arg050);
                    reply.writeNoException();
                    reply.writeTypedList(_result35);
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg051 = data.readString();
                    int _arg128 = data.readInt();
                    int _arg215 = data.readInt();
                    ProviderInfo _result36 = resolveContentProvider(_arg051, _arg128, _arg215);
                    reply.writeNoException();
                    if (_result36 != null) {
                        reply.writeInt(1);
                        _result36.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    List<String> _arg052 = data.createStringArrayList();
                    ArrayList createTypedArrayList = data.createTypedArrayList(ProviderInfo.CREATOR);
                    querySyncProviders(_arg052, createTypedArrayList);
                    reply.writeNoException();
                    reply.writeStringList(_arg052);
                    reply.writeTypedList(createTypedArrayList);
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg053 = data.readString();
                    int _arg129 = data.readInt();
                    int _arg216 = data.readInt();
                    List<ProviderInfo> _result37 = queryContentProviders(_arg053, _arg129, _arg216);
                    reply.writeNoException();
                    reply.writeTypedList(_result37);
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg012 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg012 = null;
                    }
                    int _arg130 = data.readInt();
                    InstrumentationInfo _result38 = getInstrumentationInfo(_arg012, _arg130);
                    reply.writeNoException();
                    if (_result38 != null) {
                        reply.writeInt(1);
                        _result38.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg054 = data.readString();
                    int _arg131 = data.readInt();
                    List<InstrumentationInfo> _result39 = queryInstrumentation(_arg054, _arg131);
                    reply.writeNoException();
                    reply.writeTypedList(_result39);
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg011 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg011 = null;
                    }
                    IPackageInstallObserver _arg132 = IPackageInstallObserver.Stub.asInterface(data.readStrongBinder());
                    int _arg217 = data.readInt();
                    String _arg311 = data.readString();
                    installPackage(_arg011, _arg132, _arg217, _arg311);
                    reply.writeNoException();
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg055 = data.readInt();
                    finishPackageInstall(_arg055);
                    reply.writeNoException();
                    return true;
                case 46:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg056 = data.readString();
                    String _arg133 = data.readString();
                    setInstallerPackageName(_arg056, _arg133);
                    reply.writeNoException();
                    return true;
                case 47:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg057 = data.readString();
                    IPackageDeleteObserver _arg134 = IPackageDeleteObserver.Stub.asInterface(data.readStrongBinder());
                    int _arg218 = data.readInt();
                    int _arg312 = data.readInt();
                    deletePackageAsUser(_arg057, _arg134, _arg218, _arg312);
                    reply.writeNoException();
                    return true;
                case 48:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg058 = data.readString();
                    String _result40 = getInstallerPackageName(_arg058);
                    reply.writeNoException();
                    reply.writeString(_result40);
                    return true;
                case 49:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg059 = data.readString();
                    addPackageToPreferred(_arg059);
                    reply.writeNoException();
                    return true;
                case 50:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg060 = data.readString();
                    removePackageFromPreferred(_arg060);
                    reply.writeNoException();
                    return true;
                case 51:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg061 = data.readInt();
                    List<PackageInfo> _result41 = getPreferredPackages(_arg061);
                    reply.writeNoException();
                    reply.writeTypedList(_result41);
                    return true;
                case 52:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg062 = data.readInt();
                    resetPreferredActivities(_arg062);
                    reply.writeNoException();
                    return true;
                case 53:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg010 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    String _arg135 = data.readString();
                    int _arg219 = data.readInt();
                    ResolveInfo _result42 = getLastChosenActivity(_arg010, _arg135, _arg219);
                    reply.writeNoException();
                    if (_result42 != null) {
                        reply.writeInt(1);
                        _result42.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 54:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg09 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg09 = null;
                    }
                    String _arg136 = data.readString();
                    int _arg220 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg33 = IntentFilter.CREATOR.createFromParcel(data);
                    } else {
                        _arg33 = null;
                    }
                    int _arg44 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg53 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg53 = null;
                    }
                    setLastChosenActivity(_arg09, _arg136, _arg220, _arg33, _arg44, _arg53);
                    reply.writeNoException();
                    return true;
                case 55:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = IntentFilter.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    int _arg137 = data.readInt();
                    ComponentName[] _arg221 = (ComponentName[]) data.createTypedArray(ComponentName.CREATOR);
                    if (0 != data.readInt()) {
                        _arg32 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg32 = null;
                    }
                    int _arg45 = data.readInt();
                    addPreferredActivity(_arg08, _arg137, _arg221, _arg32, _arg45);
                    reply.writeNoException();
                    return true;
                case 56:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = IntentFilter.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    int _arg138 = data.readInt();
                    ComponentName[] _arg222 = (ComponentName[]) data.createTypedArray(ComponentName.CREATOR);
                    if (0 != data.readInt()) {
                        _arg3 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    replacePreferredActivity(_arg07, _arg138, _arg222, _arg3);
                    reply.writeNoException();
                    return true;
                case 57:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg063 = data.readString();
                    clearPackagePreferredActivities(_arg063);
                    reply.writeNoException();
                    return true;
                case 58:
                    data.enforceInterface(DESCRIPTOR);
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    String _arg223 = data.readString();
                    int _result43 = getPreferredActivities(arrayList, arrayList2, _arg223);
                    reply.writeNoException();
                    reply.writeInt(_result43);
                    reply.writeTypedList(arrayList);
                    reply.writeTypedList(arrayList2);
                    return true;
                case 59:
                    data.enforceInterface(DESCRIPTOR);
                    ArrayList arrayList3 = new ArrayList();
                    ComponentName _result44 = getHomeActivities(arrayList3);
                    reply.writeNoException();
                    if (_result44 != null) {
                        reply.writeInt(1);
                        _result44.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    reply.writeTypedList(arrayList3);
                    return true;
                case 60:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    int _arg139 = data.readInt();
                    int _arg224 = data.readInt();
                    int _arg313 = data.readInt();
                    setComponentEnabledSetting(_arg06, _arg139, _arg224, _arg313);
                    reply.writeNoException();
                    return true;
                case 61:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    int _arg140 = data.readInt();
                    int _result45 = getComponentEnabledSetting(_arg05, _arg140);
                    reply.writeNoException();
                    reply.writeInt(_result45);
                    return true;
                case 62:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg064 = data.readString();
                    int _arg141 = data.readInt();
                    int _arg225 = data.readInt();
                    int _arg314 = data.readInt();
                    String _arg46 = data.readString();
                    setApplicationEnabledSetting(_arg064, _arg141, _arg225, _arg314, _arg46);
                    reply.writeNoException();
                    return true;
                case 63:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg065 = data.readString();
                    int _arg142 = data.readInt();
                    int _result46 = getApplicationEnabledSetting(_arg065, _arg142);
                    reply.writeNoException();
                    reply.writeInt(_result46);
                    return true;
                case 64:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg066 = data.readString();
                    boolean _arg143 = 0 != data.readInt();
                    int _arg226 = data.readInt();
                    setPackageStoppedState(_arg066, _arg143, _arg226);
                    reply.writeNoException();
                    return true;
                case 65:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg067 = data.readLong();
                    IPackageDataObserver _arg144 = IPackageDataObserver.Stub.asInterface(data.readStrongBinder());
                    freeStorageAndNotify(_arg067, _arg144);
                    reply.writeNoException();
                    return true;
                case 66:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg068 = data.readLong();
                    if (0 != data.readInt()) {
                        _arg1 = IntentSender.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    freeStorage(_arg068, _arg1);
                    reply.writeNoException();
                    return true;
                case 67:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg069 = data.readString();
                    IPackageDataObserver _arg145 = IPackageDataObserver.Stub.asInterface(data.readStrongBinder());
                    deleteApplicationCacheFiles(_arg069, _arg145);
                    reply.writeNoException();
                    return true;
                case 68:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg070 = data.readString();
                    IPackageDataObserver _arg146 = IPackageDataObserver.Stub.asInterface(data.readStrongBinder());
                    int _arg227 = data.readInt();
                    clearApplicationUserData(_arg070, _arg146, _arg227);
                    reply.writeNoException();
                    return true;
                case 69:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg071 = data.readString();
                    int _arg147 = data.readInt();
                    IPackageStatsObserver _arg228 = IPackageStatsObserver.Stub.asInterface(data.readStrongBinder());
                    getPackageSizeInfo(_arg071, _arg147, _arg228);
                    reply.writeNoException();
                    return true;
                case 70:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result47 = getSystemSharedLibraryNames();
                    reply.writeNoException();
                    reply.writeStringArray(_result47);
                    return true;
                case 71:
                    data.enforceInterface(DESCRIPTOR);
                    FeatureInfo[] _result48 = getSystemAvailableFeatures();
                    reply.writeNoException();
                    reply.writeTypedArray(_result48, 1);
                    return true;
                case 72:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg072 = data.readString();
                    boolean _result49 = hasSystemFeature(_arg072);
                    reply.writeNoException();
                    reply.writeInt(_result49 ? 1 : 0);
                    return true;
                case 73:
                    data.enforceInterface(DESCRIPTOR);
                    enterSafeMode();
                    reply.writeNoException();
                    return true;
                case 74:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result50 = isSafeMode();
                    reply.writeNoException();
                    reply.writeInt(_result50 ? 1 : 0);
                    return true;
                case 75:
                    data.enforceInterface(DESCRIPTOR);
                    systemReady();
                    reply.writeNoException();
                    return true;
                case 76:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result51 = hasSystemUidErrors();
                    reply.writeNoException();
                    reply.writeInt(_result51 ? 1 : 0);
                    return true;
                case 77:
                    data.enforceInterface(DESCRIPTOR);
                    performBootDexOpt();
                    reply.writeNoException();
                    return true;
                case 78:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg073 = data.readString();
                    boolean _result52 = performDexOpt(_arg073);
                    reply.writeNoException();
                    reply.writeInt(_result52 ? 1 : 0);
                    return true;
                case 79:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg074 = 0 != data.readInt();
                    boolean _arg148 = 0 != data.readInt();
                    updateExternalMediaStatus(_arg074, _arg148);
                    reply.writeNoException();
                    return true;
                case 80:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = PackageCleanItem.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    PackageCleanItem _result53 = nextPackageToClean(_arg04);
                    reply.writeNoException();
                    if (_result53 != null) {
                        reply.writeInt(1);
                        _result53.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 81:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg075 = data.readString();
                    IPackageMoveObserver _arg149 = IPackageMoveObserver.Stub.asInterface(data.readStrongBinder());
                    int _arg229 = data.readInt();
                    movePackage(_arg075, _arg149, _arg229);
                    reply.writeNoException();
                    return true;
                case 82:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = PermissionInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    boolean _result54 = addPermissionAsync(_arg03);
                    reply.writeNoException();
                    reply.writeInt(_result54 ? 1 : 0);
                    return true;
                case 83:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg076 = data.readInt();
                    boolean _result55 = setInstallLocation(_arg076);
                    reply.writeNoException();
                    reply.writeInt(_result55 ? 1 : 0);
                    return true;
                case 84:
                    data.enforceInterface(DESCRIPTOR);
                    int _result56 = getInstallLocation();
                    reply.writeNoException();
                    reply.writeInt(_result56);
                    return true;
                case 85:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    IPackageInstallObserver _arg150 = IPackageInstallObserver.Stub.asInterface(data.readStrongBinder());
                    int _arg230 = data.readInt();
                    String _arg315 = data.readString();
                    if (0 != data.readInt()) {
                        _arg42 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg42 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg52 = ManifestDigest.CREATOR.createFromParcel(data);
                    } else {
                        _arg52 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg6 = ContainerEncryptionParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg6 = null;
                    }
                    installPackageWithVerification(_arg02, _arg150, _arg230, _arg315, _arg42, _arg52, _arg6);
                    reply.writeNoException();
                    return true;
                case 86:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    IPackageInstallObserver _arg151 = IPackageInstallObserver.Stub.asInterface(data.readStrongBinder());
                    int _arg231 = data.readInt();
                    String _arg316 = data.readString();
                    if (0 != data.readInt()) {
                        _arg4 = VerificationParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg5 = ContainerEncryptionParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg5 = null;
                    }
                    installPackageWithVerificationAndEncryption(_arg0, _arg151, _arg231, _arg316, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                case 87:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg077 = data.readString();
                    int _arg152 = data.readInt();
                    int _result57 = installExistingPackageAsUser(_arg077, _arg152);
                    reply.writeNoException();
                    reply.writeInt(_result57);
                    return true;
                case 88:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg078 = data.readInt();
                    int _arg153 = data.readInt();
                    verifyPendingInstall(_arg078, _arg153);
                    reply.writeNoException();
                    return true;
                case 89:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg079 = data.readInt();
                    int _arg154 = data.readInt();
                    long _arg232 = data.readLong();
                    extendVerificationTimeout(_arg079, _arg154, _arg232);
                    reply.writeNoException();
                    return true;
                case 90:
                    data.enforceInterface(DESCRIPTOR);
                    VerifierDeviceIdentity _result58 = getVerifierDeviceIdentity();
                    reply.writeNoException();
                    if (_result58 != null) {
                        reply.writeInt(1);
                        _result58.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 91:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result59 = isFirstBoot();
                    reply.writeNoException();
                    reply.writeInt(_result59 ? 1 : 0);
                    return true;
                case 92:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result60 = isOnlyCoreApps();
                    reply.writeNoException();
                    reply.writeInt(_result60 ? 1 : 0);
                    return true;
                case 93:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg080 = data.readString();
                    boolean _arg155 = 0 != data.readInt();
                    setPermissionEnforced(_arg080, _arg155);
                    reply.writeNoException();
                    return true;
                case 94:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg081 = data.readString();
                    boolean _result61 = isPermissionEnforced(_arg081);
                    reply.writeNoException();
                    reply.writeInt(_result61 ? 1 : 0);
                    return true;
                case 95:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result62 = isStorageLow();
                    reply.writeNoException();
                    reply.writeInt(_result62 ? 1 : 0);
                    return true;
                case 96:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg082 = data.readString();
                    boolean _arg156 = 0 != data.readInt();
                    int _arg233 = data.readInt();
                    boolean _result63 = setApplicationBlockedSettingAsUser(_arg082, _arg156, _arg233);
                    reply.writeNoException();
                    reply.writeInt(_result63 ? 1 : 0);
                    return true;
                case 97:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg083 = data.readString();
                    int _arg157 = data.readInt();
                    boolean _result64 = getApplicationBlockedSettingAsUser(_arg083, _arg157);
                    reply.writeNoException();
                    reply.writeInt(_result64 ? 1 : 0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IPackageManager$Stub$Proxy.class */
        public static class Proxy implements IPackageManager {
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

            @Override // android.content.pm.IPackageManager
            public PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException {
                PackageInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = PackageInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public int getPackageUid(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
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

            @Override // android.content.pm.IPackageManager
            public int[] getPackageGids(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(3, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public String[] currentToCanonicalPackageNames(String[] names) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(names);
                    this.mRemote.transact(4, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public String[] canonicalToCurrentPackageNames(String[] names) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(names);
                    this.mRemote.transact(5, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public PermissionInfo getPermissionInfo(String name, int flags) throws RemoteException {
                PermissionInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(flags);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = PermissionInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(group);
                    _data.writeInt(flags);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    List<PermissionInfo> _result = _reply.createTypedArrayList(PermissionInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws RemoteException {
                PermissionGroupInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(flags);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = PermissionGroupInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<PermissionGroupInfo> getAllPermissionGroups(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    List<PermissionGroupInfo> _result = _reply.createTypedArrayList(PermissionGroupInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public ApplicationInfo getApplicationInfo(String packageName, int flags, int userId) throws RemoteException {
                ApplicationInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ApplicationInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public ActivityInfo getActivityInfo(ComponentName className, int flags, int userId) throws RemoteException {
                ActivityInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(1);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ActivityInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public ActivityInfo getReceiverInfo(ComponentName className, int flags, int userId) throws RemoteException {
                ActivityInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(1);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ActivityInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public ServiceInfo getServiceInfo(ComponentName className, int flags, int userId) throws RemoteException {
                ServiceInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(1);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ServiceInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public ProviderInfo getProviderInfo(ComponentName className, int flags, int userId) throws RemoteException {
                ProviderInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(1);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ProviderInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public int checkPermission(String permName, String pkgName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permName);
                    _data.writeString(pkgName);
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

            @Override // android.content.pm.IPackageManager
            public int checkUidPermission(String permName, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permName);
                    _data.writeInt(uid);
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

            @Override // android.content.pm.IPackageManager
            public boolean addPermission(PermissionInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public void removePermission(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
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

            @Override // android.content.pm.IPackageManager
            public void grantPermission(String packageName, String permissionName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
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

            @Override // android.content.pm.IPackageManager
            public void revokePermission(String packageName, String permissionName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
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

            @Override // android.content.pm.IPackageManager
            public boolean isProtectedBroadcast(String actionName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(actionName);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public int checkSignatures(String pkg1, String pkg2) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg1);
                    _data.writeString(pkg2);
                    this.mRemote.transact(22, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public int checkUidSignatures(int uid1, int uid2) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid1);
                    _data.writeInt(uid2);
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

            @Override // android.content.pm.IPackageManager
            public String[] getPackagesForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(24, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public String getNameForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
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

            @Override // android.content.pm.IPackageManager
            public int getUidForSharedUser(String sharedUserName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sharedUserName);
                    this.mRemote.transact(26, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public int getFlagsForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
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

            @Override // android.content.pm.IPackageManager
            public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                ResolveInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ResolveInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    List<ResolveInfo> _result = _reply.createTypedArrayList(ResolveInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics, String[] specificTypes, Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (caller != null) {
                        _data.writeInt(1);
                        caller.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeTypedArray(specifics, 0);
                    _data.writeStringArray(specificTypes);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    List<ResolveInfo> _result = _reply.createTypedArrayList(ResolveInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<ResolveInfo> queryIntentReceivers(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    List<ResolveInfo> _result = _reply.createTypedArrayList(ResolveInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public ResolveInfo resolveService(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                ResolveInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ResolveInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<ResolveInfo> queryIntentServices(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    List<ResolveInfo> _result = _reply.createTypedArrayList(ResolveInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<ResolveInfo> queryIntentContentProviders(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    List<ResolveInfo> _result = _reply.createTypedArrayList(ResolveInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public ParceledListSlice getInstalledPackages(int flags, int userId) throws RemoteException {
                ParceledListSlice _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ParceledListSlice.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public ParceledListSlice getPackagesHoldingPermissions(String[] permissions, int flags, int userId) throws RemoteException {
                ParceledListSlice _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(permissions);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ParceledListSlice.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public ParceledListSlice getInstalledApplications(int flags, int userId) throws RemoteException {
                ParceledListSlice _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ParceledListSlice.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<ApplicationInfo> getPersistentApplications(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    List<ApplicationInfo> _result = _reply.createTypedArrayList(ApplicationInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public ProviderInfo resolveContentProvider(String name, int flags, int userId) throws RemoteException {
                ProviderInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ProviderInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public void querySyncProviders(List<String> outNames, List<ProviderInfo> outInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(outNames);
                    _data.writeTypedList(outInfo);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    _reply.readStringList(outNames);
                    _reply.readTypedList(outInfo, ProviderInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(processName);
                    _data.writeInt(uid);
                    _data.writeInt(flags);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    List<ProviderInfo> _result = _reply.createTypedArrayList(ProviderInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public InstrumentationInfo getInstrumentationInfo(ComponentName className, int flags) throws RemoteException {
                InstrumentationInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(1);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = InstrumentationInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPackage);
                    _data.writeInt(flags);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    List<InstrumentationInfo> _result = _reply.createTypedArrayList(InstrumentationInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void installPackage(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (packageURI != null) {
                        _data.writeInt(1);
                        packageURI.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeString(installerPackageName);
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

            @Override // android.content.pm.IPackageManager
            public void finishPackageInstall(int token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(token);
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

            @Override // android.content.pm.IPackageManager
            public void setInstallerPackageName(String targetPackage, String installerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPackage);
                    _data.writeString(installerPackageName);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void deletePackageAsUser(String packageName, IPackageDeleteObserver observer, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
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

            @Override // android.content.pm.IPackageManager
            public String getInstallerPackageName(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(48, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public void addPackageToPreferred(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
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

            @Override // android.content.pm.IPackageManager
            public void removePackageFromPreferred(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public List<PackageInfo> getPreferredPackages(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    List<PackageInfo> _result = _reply.createTypedArrayList(PackageInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void resetPreferredActivities(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public ResolveInfo getLastChosenActivity(Intent intent, String resolvedType, int flags) throws RemoteException {
                ResolveInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ResolveInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public void setLastChosenActivity(Intent intent, String resolvedType, int flags, IntentFilter filter, int match, ComponentName activity) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    if (filter != null) {
                        _data.writeInt(1);
                        filter.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(match);
                    if (activity != null) {
                        _data.writeInt(1);
                        activity.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (filter != null) {
                        _data.writeInt(1);
                        filter.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(match);
                    _data.writeTypedArray(set, 0);
                    if (activity != null) {
                        _data.writeInt(1);
                        activity.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
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

            @Override // android.content.pm.IPackageManager
            public void replacePreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (filter != null) {
                        _data.writeInt(1);
                        filter.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(match);
                    _data.writeTypedArray(set, 0);
                    if (activity != null) {
                        _data.writeInt(1);
                        activity.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void clearPackagePreferredActivities(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readTypedList(outFilters, IntentFilter.CREATOR);
                    _reply.readTypedList(outActivities, ComponentName.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public ComponentName getHomeActivities(List<ResolveInfo> outHomeCandidates) throws RemoteException {
                ComponentName _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ComponentName.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.readTypedList(outHomeCandidates, ResolveInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        _data.writeInt(1);
                        componentName.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newState);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public int getComponentEnabledSetting(ComponentName componentName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        _data.writeInt(1);
                        componentName.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(61, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public void setApplicationEnabledSetting(String packageName, int newState, int flags, int userId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(newState);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public int getApplicationEnabledSetting(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(63, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public void setPackageStoppedState(String packageName, boolean stopped, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(stopped ? 1 : 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(freeStorageSize);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void freeStorage(long freeStorageSize, IntentSender pi) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(freeStorageSize);
                    if (pi != null) {
                        _data.writeInt(1);
                        pi.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void deleteApplicationCacheFiles(String packageName, IPackageDataObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void clearApplicationUserData(String packageName, IPackageDataObserver observer, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(userId);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void getPackageSizeInfo(String packageName, int userHandle, IPackageStatsObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userHandle);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public String[] getSystemSharedLibraryNames() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(70, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public FeatureInfo[] getSystemAvailableFeatures() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                    FeatureInfo[] _result = (FeatureInfo[]) _reply.createTypedArray(FeatureInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean hasSystemFeature(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public void enterSafeMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean isSafeMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public void systemReady() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean hasSystemUidErrors() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public void performBootDexOpt() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean performDexOpt(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public void updateExternalMediaStatus(boolean mounted, boolean reportStatus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mounted ? 1 : 0);
                    _data.writeInt(reportStatus ? 1 : 0);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public PackageCleanItem nextPackageToClean(PackageCleanItem lastPackage) throws RemoteException {
                PackageCleanItem _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (lastPackage != null) {
                        _data.writeInt(1);
                        lastPackage.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = PackageCleanItem.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public void movePackage(String packageName, IPackageMoveObserver observer, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(flags);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean addPermissionAsync(PermissionInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(82, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean setInstallLocation(int loc) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(loc);
                    this.mRemote.transact(83, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public int getInstallLocation() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(84, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public void installPackageWithVerification(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName, Uri verificationURI, ManifestDigest manifestDigest, ContainerEncryptionParams encryptionParams) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (packageURI != null) {
                        _data.writeInt(1);
                        packageURI.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeString(installerPackageName);
                    if (verificationURI != null) {
                        _data.writeInt(1);
                        verificationURI.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (manifestDigest != null) {
                        _data.writeInt(1);
                        manifestDigest.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (encryptionParams != null) {
                        _data.writeInt(1);
                        encryptionParams.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void installPackageWithVerificationAndEncryption(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName, VerificationParams verificationParams, ContainerEncryptionParams encryptionParams) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (packageURI != null) {
                        _data.writeInt(1);
                        packageURI.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeString(installerPackageName);
                    if (verificationParams != null) {
                        _data.writeInt(1);
                        verificationParams.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (encryptionParams != null) {
                        _data.writeInt(1);
                        encryptionParams.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public int installExistingPackageAsUser(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(87, _data, _reply, 0);
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

            @Override // android.content.pm.IPackageManager
            public void verifyPendingInstall(int id, int verificationCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(verificationCode);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(verificationCodeAtTimeout);
                    _data.writeLong(millisecondsToDelay);
                    this.mRemote.transact(89, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public VerifierDeviceIdentity getVerifierDeviceIdentity() throws RemoteException {
                VerifierDeviceIdentity _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = VerifierDeviceIdentity.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean isFirstBoot() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(91, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean isOnlyCoreApps() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(92, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public void setPermissionEnforced(String permission, boolean enforced) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permission);
                    _data.writeInt(enforced ? 1 : 0);
                    this.mRemote.transact(93, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean isPermissionEnforced(String permission) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permission);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean isStorageLow() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(95, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean setApplicationBlockedSettingAsUser(String packageName, boolean blocked, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(blocked ? 1 : 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(96, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.pm.IPackageManager
            public boolean getApplicationBlockedSettingAsUser(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(97, _data, _reply, 0);
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