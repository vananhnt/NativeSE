package android.content.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageParser;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.UserHandle;
import android.util.AndroidException;
import android.util.DisplayMetrics;
import gov.nist.core.Separators;
import java.io.File;
import java.util.List;

/* loaded from: PackageManager.class */
public abstract class PackageManager {
    public static final int GET_ACTIVITIES = 1;
    public static final int GET_RECEIVERS = 2;
    public static final int GET_SERVICES = 4;
    public static final int GET_PROVIDERS = 8;
    public static final int GET_INSTRUMENTATION = 16;
    public static final int GET_INTENT_FILTERS = 32;
    public static final int GET_SIGNATURES = 64;
    public static final int GET_RESOLVED_FILTER = 64;
    public static final int GET_META_DATA = 128;
    public static final int GET_GIDS = 256;
    public static final int GET_DISABLED_COMPONENTS = 512;
    public static final int GET_SHARED_LIBRARY_FILES = 1024;
    public static final int GET_URI_PERMISSION_PATTERNS = 2048;
    public static final int GET_PERMISSIONS = 4096;
    public static final int GET_UNINSTALLED_PACKAGES = 8192;
    public static final int GET_CONFIGURATIONS = 16384;
    public static final int GET_DISABLED_UNTIL_USED_COMPONENTS = 32768;
    public static final int MATCH_DEFAULT_ONLY = 65536;
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_DENIED = -1;
    public static final int SIGNATURE_MATCH = 0;
    public static final int SIGNATURE_NEITHER_SIGNED = 1;
    public static final int SIGNATURE_FIRST_NOT_SIGNED = -1;
    public static final int SIGNATURE_SECOND_NOT_SIGNED = -2;
    public static final int SIGNATURE_NO_MATCH = -3;
    public static final int SIGNATURE_UNKNOWN_PACKAGE = -4;
    public static final int COMPONENT_ENABLED_STATE_DEFAULT = 0;
    public static final int COMPONENT_ENABLED_STATE_ENABLED = 1;
    public static final int COMPONENT_ENABLED_STATE_DISABLED = 2;
    public static final int COMPONENT_ENABLED_STATE_DISABLED_USER = 3;
    public static final int COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED = 4;
    public static final int INSTALL_FORWARD_LOCK = 1;
    public static final int INSTALL_REPLACE_EXISTING = 2;
    public static final int INSTALL_ALLOW_TEST = 4;
    public static final int INSTALL_EXTERNAL = 8;
    public static final int INSTALL_INTERNAL = 16;
    public static final int INSTALL_FROM_ADB = 32;
    public static final int INSTALL_ALL_USERS = 64;
    public static final int INSTALL_ALLOW_DOWNGRADE = 128;
    public static final int DONT_KILL_APP = 1;
    public static final int INSTALL_SUCCEEDED = 1;
    public static final int INSTALL_FAILED_ALREADY_EXISTS = -1;
    public static final int INSTALL_FAILED_INVALID_APK = -2;
    public static final int INSTALL_FAILED_INVALID_URI = -3;
    public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE = -4;
    public static final int INSTALL_FAILED_DUPLICATE_PACKAGE = -5;
    public static final int INSTALL_FAILED_NO_SHARED_USER = -6;
    public static final int INSTALL_FAILED_UPDATE_INCOMPATIBLE = -7;
    public static final int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE = -8;
    public static final int INSTALL_FAILED_MISSING_SHARED_LIBRARY = -9;
    public static final int INSTALL_FAILED_REPLACE_COULDNT_DELETE = -10;
    public static final int INSTALL_FAILED_DEXOPT = -11;
    public static final int INSTALL_FAILED_OLDER_SDK = -12;
    public static final int INSTALL_FAILED_CONFLICTING_PROVIDER = -13;
    public static final int INSTALL_FAILED_NEWER_SDK = -14;
    public static final int INSTALL_FAILED_TEST_ONLY = -15;
    public static final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE = -16;
    public static final int INSTALL_FAILED_MISSING_FEATURE = -17;
    public static final int INSTALL_FAILED_CONTAINER_ERROR = -18;
    public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION = -19;
    public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE = -20;
    public static final int INSTALL_FAILED_VERIFICATION_TIMEOUT = -21;
    public static final int INSTALL_FAILED_VERIFICATION_FAILURE = -22;
    public static final int INSTALL_FAILED_PACKAGE_CHANGED = -23;
    public static final int INSTALL_FAILED_UID_CHANGED = -24;
    public static final int INSTALL_FAILED_VERSION_DOWNGRADE = -25;
    public static final int INSTALL_PARSE_FAILED_NOT_APK = -100;
    public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST = -101;
    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION = -102;
    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES = -103;
    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;
    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING = -105;
    public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME = -106;
    public static final int INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID = -107;
    public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED = -108;
    public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY = -109;
    public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;
    public static final int INSTALL_FAILED_USER_RESTRICTED = -111;
    public static final int DELETE_KEEP_DATA = 1;
    public static final int DELETE_ALL_USERS = 2;
    public static final int DELETE_SYSTEM_APP = 4;
    public static final int DELETE_SUCCEEDED = 1;
    public static final int DELETE_FAILED_INTERNAL_ERROR = -1;
    public static final int DELETE_FAILED_DEVICE_POLICY_MANAGER = -2;
    public static final int DELETE_FAILED_USER_RESTRICTED = -3;
    public static final int MOVE_SUCCEEDED = 1;
    public static final int MOVE_FAILED_INSUFFICIENT_STORAGE = -1;
    public static final int MOVE_FAILED_DOESNT_EXIST = -2;
    public static final int MOVE_FAILED_SYSTEM_PACKAGE = -3;
    public static final int MOVE_FAILED_FORWARD_LOCKED = -4;
    public static final int MOVE_FAILED_INVALID_LOCATION = -5;
    public static final int MOVE_FAILED_INTERNAL_ERROR = -6;
    public static final int MOVE_FAILED_OPERATION_PENDING = -7;
    public static final int MOVE_INTERNAL = 1;
    public static final int MOVE_EXTERNAL_MEDIA = 2;
    public static final int VERIFICATION_ALLOW_WITHOUT_SUFFICIENT = 2;
    public static final int VERIFICATION_ALLOW = 1;
    public static final int VERIFICATION_REJECT = -1;
    public static final long MAXIMUM_VERIFICATION_TIMEOUT = 3600000;
    public static final String FEATURE_AUDIO_LOW_LATENCY = "android.hardware.audio.low_latency";
    public static final String FEATURE_BLUETOOTH = "android.hardware.bluetooth";
    public static final String FEATURE_BLUETOOTH_LE = "android.hardware.bluetooth_le";
    public static final String FEATURE_CAMERA = "android.hardware.camera";
    public static final String FEATURE_CAMERA_AUTOFOCUS = "android.hardware.camera.autofocus";
    public static final String FEATURE_CAMERA_ANY = "android.hardware.camera.any";
    public static final String FEATURE_CAMERA_FLASH = "android.hardware.camera.flash";
    public static final String FEATURE_CAMERA_FRONT = "android.hardware.camera.front";
    public static final String FEATURE_CONSUMER_IR = "android.hardware.consumerir";
    public static final String FEATURE_LOCATION = "android.hardware.location";
    public static final String FEATURE_LOCATION_GPS = "android.hardware.location.gps";
    public static final String FEATURE_LOCATION_NETWORK = "android.hardware.location.network";
    public static final String FEATURE_MICROPHONE = "android.hardware.microphone";
    public static final String FEATURE_NFC = "android.hardware.nfc";
    public static final String FEATURE_NFC_HCE = "android.hardware.nfc.hce";
    public static final String FEATURE_NFC_HOST_CARD_EMULATION = "android.hardware.nfc.hce";
    public static final String FEATURE_SENSOR_ACCELEROMETER = "android.hardware.sensor.accelerometer";
    public static final String FEATURE_SENSOR_BAROMETER = "android.hardware.sensor.barometer";
    public static final String FEATURE_SENSOR_COMPASS = "android.hardware.sensor.compass";
    public static final String FEATURE_SENSOR_GYROSCOPE = "android.hardware.sensor.gyroscope";
    public static final String FEATURE_SENSOR_LIGHT = "android.hardware.sensor.light";
    public static final String FEATURE_SENSOR_PROXIMITY = "android.hardware.sensor.proximity";
    public static final String FEATURE_SENSOR_STEP_COUNTER = "android.hardware.sensor.stepcounter";
    public static final String FEATURE_SENSOR_STEP_DETECTOR = "android.hardware.sensor.stepdetector";
    public static final String FEATURE_TELEPHONY = "android.hardware.telephony";
    public static final String FEATURE_TELEPHONY_CDMA = "android.hardware.telephony.cdma";
    public static final String FEATURE_TELEPHONY_GSM = "android.hardware.telephony.gsm";
    public static final String FEATURE_USB_HOST = "android.hardware.usb.host";
    public static final String FEATURE_USB_ACCESSORY = "android.hardware.usb.accessory";
    public static final String FEATURE_SIP = "android.software.sip";
    public static final String FEATURE_SIP_VOIP = "android.software.sip.voip";
    public static final String FEATURE_TOUCHSCREEN = "android.hardware.touchscreen";
    public static final String FEATURE_TOUCHSCREEN_MULTITOUCH = "android.hardware.touchscreen.multitouch";
    public static final String FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT = "android.hardware.touchscreen.multitouch.distinct";
    public static final String FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND = "android.hardware.touchscreen.multitouch.jazzhand";
    public static final String FEATURE_FAKETOUCH = "android.hardware.faketouch";
    public static final String FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT = "android.hardware.faketouch.multitouch.distinct";
    public static final String FEATURE_FAKETOUCH_MULTITOUCH_JAZZHAND = "android.hardware.faketouch.multitouch.jazzhand";
    public static final String FEATURE_SCREEN_PORTRAIT = "android.hardware.screen.portrait";
    public static final String FEATURE_SCREEN_LANDSCAPE = "android.hardware.screen.landscape";
    public static final String FEATURE_LIVE_WALLPAPER = "android.software.live_wallpaper";
    public static final String FEATURE_APP_WIDGETS = "android.software.app_widgets";
    public static final String FEATURE_HOME_SCREEN = "android.software.home_screen";
    public static final String FEATURE_INPUT_METHODS = "android.software.input_methods";
    public static final String FEATURE_DEVICE_ADMIN = "android.software.device_admin";
    public static final String FEATURE_WIFI = "android.hardware.wifi";
    public static final String FEATURE_WIFI_DIRECT = "android.hardware.wifi.direct";
    public static final String FEATURE_TELEVISION = "android.hardware.type.television";
    public static final String ACTION_CLEAN_EXTERNAL_STORAGE = "android.content.pm.CLEAN_EXTERNAL_STORAGE";
    public static final String EXTRA_VERIFICATION_URI = "android.content.pm.extra.VERIFICATION_URI";
    public static final String EXTRA_VERIFICATION_ID = "android.content.pm.extra.VERIFICATION_ID";
    public static final String EXTRA_VERIFICATION_INSTALLER_PACKAGE = "android.content.pm.extra.VERIFICATION_INSTALLER_PACKAGE";
    public static final String EXTRA_VERIFICATION_INSTALL_FLAGS = "android.content.pm.extra.VERIFICATION_INSTALL_FLAGS";
    public static final String EXTRA_VERIFICATION_INSTALLER_UID = "android.content.pm.extra.VERIFICATION_INSTALLER_UID";
    public static final String EXTRA_VERIFICATION_PACKAGE_NAME = "android.content.pm.extra.VERIFICATION_PACKAGE_NAME";
    public static final String EXTRA_VERIFICATION_RESULT = "android.content.pm.extra.VERIFICATION_RESULT";
    public static final String EXTRA_VERIFICATION_VERSION_CODE = "android.content.pm.extra.VERIFICATION_VERSION_CODE";
    public static final String ACTION_REQUEST_PERMISSION = "android.content.pm.action.REQUEST_PERMISSION";
    public static final String EXTRA_REQUEST_PERMISSION_PERMISSION_LIST = "android.content.pm.extra.PERMISSION_LIST";

    public abstract PackageInfo getPackageInfo(String str, int i) throws NameNotFoundException;

    public abstract String[] currentToCanonicalPackageNames(String[] strArr);

    public abstract String[] canonicalToCurrentPackageNames(String[] strArr);

    public abstract Intent getLaunchIntentForPackage(String str);

    public abstract int[] getPackageGids(String str) throws NameNotFoundException;

    public abstract int getPackageUid(String str, int i) throws NameNotFoundException;

    public abstract PermissionInfo getPermissionInfo(String str, int i) throws NameNotFoundException;

    public abstract List<PermissionInfo> queryPermissionsByGroup(String str, int i) throws NameNotFoundException;

    public abstract PermissionGroupInfo getPermissionGroupInfo(String str, int i) throws NameNotFoundException;

    public abstract List<PermissionGroupInfo> getAllPermissionGroups(int i);

    public abstract ApplicationInfo getApplicationInfo(String str, int i) throws NameNotFoundException;

    public abstract ActivityInfo getActivityInfo(ComponentName componentName, int i) throws NameNotFoundException;

    public abstract ActivityInfo getReceiverInfo(ComponentName componentName, int i) throws NameNotFoundException;

    public abstract ServiceInfo getServiceInfo(ComponentName componentName, int i) throws NameNotFoundException;

    public abstract ProviderInfo getProviderInfo(ComponentName componentName, int i) throws NameNotFoundException;

    public abstract List<PackageInfo> getInstalledPackages(int i);

    public abstract List<PackageInfo> getPackagesHoldingPermissions(String[] strArr, int i);

    public abstract List<PackageInfo> getInstalledPackages(int i, int i2);

    public abstract int checkPermission(String str, String str2);

    public abstract boolean addPermission(PermissionInfo permissionInfo);

    public abstract boolean addPermissionAsync(PermissionInfo permissionInfo);

    public abstract void removePermission(String str);

    public abstract void grantPermission(String str, String str2);

    public abstract void revokePermission(String str, String str2);

    public abstract int checkSignatures(String str, String str2);

    public abstract int checkSignatures(int i, int i2);

    public abstract String[] getPackagesForUid(int i);

    public abstract String getNameForUid(int i);

    public abstract int getUidForSharedUser(String str) throws NameNotFoundException;

    public abstract List<ApplicationInfo> getInstalledApplications(int i);

    public abstract String[] getSystemSharedLibraryNames();

    public abstract FeatureInfo[] getSystemAvailableFeatures();

    public abstract boolean hasSystemFeature(String str);

    public abstract ResolveInfo resolveActivity(Intent intent, int i);

    public abstract ResolveInfo resolveActivityAsUser(Intent intent, int i, int i2);

    public abstract List<ResolveInfo> queryIntentActivities(Intent intent, int i);

    public abstract List<ResolveInfo> queryIntentActivitiesAsUser(Intent intent, int i, int i2);

    public abstract List<ResolveInfo> queryIntentActivityOptions(ComponentName componentName, Intent[] intentArr, Intent intent, int i);

    public abstract List<ResolveInfo> queryBroadcastReceivers(Intent intent, int i);

    public abstract List<ResolveInfo> queryBroadcastReceivers(Intent intent, int i, int i2);

    public abstract ResolveInfo resolveService(Intent intent, int i);

    public abstract List<ResolveInfo> queryIntentServices(Intent intent, int i);

    public abstract List<ResolveInfo> queryIntentServicesAsUser(Intent intent, int i, int i2);

    public abstract List<ResolveInfo> queryIntentContentProvidersAsUser(Intent intent, int i, int i2);

    public abstract List<ResolveInfo> queryIntentContentProviders(Intent intent, int i);

    public abstract ProviderInfo resolveContentProvider(String str, int i);

    public abstract List<ProviderInfo> queryContentProviders(String str, int i, int i2);

    public abstract InstrumentationInfo getInstrumentationInfo(ComponentName componentName, int i) throws NameNotFoundException;

    public abstract List<InstrumentationInfo> queryInstrumentation(String str, int i);

    public abstract Drawable getDrawable(String str, int i, ApplicationInfo applicationInfo);

    public abstract Drawable getActivityIcon(ComponentName componentName) throws NameNotFoundException;

    public abstract Drawable getActivityIcon(Intent intent) throws NameNotFoundException;

    public abstract Drawable getDefaultActivityIcon();

    public abstract Drawable getApplicationIcon(ApplicationInfo applicationInfo);

    public abstract Drawable getApplicationIcon(String str) throws NameNotFoundException;

    public abstract Drawable getActivityLogo(ComponentName componentName) throws NameNotFoundException;

    public abstract Drawable getActivityLogo(Intent intent) throws NameNotFoundException;

    public abstract Drawable getApplicationLogo(ApplicationInfo applicationInfo);

    public abstract Drawable getApplicationLogo(String str) throws NameNotFoundException;

    public abstract CharSequence getText(String str, int i, ApplicationInfo applicationInfo);

    public abstract XmlResourceParser getXml(String str, int i, ApplicationInfo applicationInfo);

    public abstract CharSequence getApplicationLabel(ApplicationInfo applicationInfo);

    public abstract Resources getResourcesForActivity(ComponentName componentName) throws NameNotFoundException;

    public abstract Resources getResourcesForApplication(ApplicationInfo applicationInfo) throws NameNotFoundException;

    public abstract Resources getResourcesForApplication(String str) throws NameNotFoundException;

    public abstract Resources getResourcesForApplicationAsUser(String str, int i) throws NameNotFoundException;

    public abstract void installPackage(Uri uri, IPackageInstallObserver iPackageInstallObserver, int i, String str);

    public abstract void installPackageWithVerification(Uri uri, IPackageInstallObserver iPackageInstallObserver, int i, String str, Uri uri2, ManifestDigest manifestDigest, ContainerEncryptionParams containerEncryptionParams);

    public abstract void installPackageWithVerificationAndEncryption(Uri uri, IPackageInstallObserver iPackageInstallObserver, int i, String str, VerificationParams verificationParams, ContainerEncryptionParams containerEncryptionParams);

    public abstract int installExistingPackage(String str) throws NameNotFoundException;

    public abstract void verifyPendingInstall(int i, int i2);

    public abstract void extendVerificationTimeout(int i, int i2, long j);

    public abstract void setInstallerPackageName(String str, String str2);

    public abstract void deletePackage(String str, IPackageDeleteObserver iPackageDeleteObserver, int i);

    public abstract String getInstallerPackageName(String str);

    public abstract void clearApplicationUserData(String str, IPackageDataObserver iPackageDataObserver);

    public abstract void deleteApplicationCacheFiles(String str, IPackageDataObserver iPackageDataObserver);

    public abstract void freeStorageAndNotify(long j, IPackageDataObserver iPackageDataObserver);

    public abstract void freeStorage(long j, IntentSender intentSender);

    public abstract void getPackageSizeInfo(String str, int i, IPackageStatsObserver iPackageStatsObserver);

    @Deprecated
    public abstract void addPackageToPreferred(String str);

    @Deprecated
    public abstract void removePackageFromPreferred(String str);

    public abstract List<PackageInfo> getPreferredPackages(int i);

    @Deprecated
    public abstract void addPreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName);

    @Deprecated
    public abstract void replacePreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName);

    public abstract void clearPackagePreferredActivities(String str);

    public abstract int getPreferredActivities(List<IntentFilter> list, List<ComponentName> list2, String str);

    public abstract ComponentName getHomeActivities(List<ResolveInfo> list);

    public abstract void setComponentEnabledSetting(ComponentName componentName, int i, int i2);

    public abstract int getComponentEnabledSetting(ComponentName componentName);

    public abstract void setApplicationEnabledSetting(String str, int i, int i2);

    public abstract int getApplicationEnabledSetting(String str);

    public abstract boolean setApplicationBlockedSettingAsUser(String str, boolean z, UserHandle userHandle);

    public abstract boolean getApplicationBlockedSettingAsUser(String str, UserHandle userHandle);

    public abstract boolean isSafeMode();

    public abstract void movePackage(String str, IPackageMoveObserver iPackageMoveObserver, int i);

    public abstract VerifierDeviceIdentity getVerifierDeviceIdentity();

    /* loaded from: PackageManager$NameNotFoundException.class */
    public static class NameNotFoundException extends AndroidException {
        public NameNotFoundException() {
        }

        public NameNotFoundException(String name) {
            super(name);
        }
    }

    public Intent buildPermissionRequestIntent(String... permissions) {
        if (permissions == null) {
            throw new NullPointerException("permissions cannot be null");
        }
        for (String permission : permissions) {
            if (permission == null) {
                throw new IllegalArgumentException("permissions cannot contain null");
            }
        }
        Intent i = new Intent(ACTION_REQUEST_PERMISSION);
        i.putExtra(EXTRA_REQUEST_PERMISSION_PERMISSION_LIST, permissions);
        i.setPackage("com.android.packageinstaller");
        return i;
    }

    public PackageInfo getPackageArchiveInfo(String archiveFilePath, int flags) {
        PackageParser packageParser = new PackageParser(archiveFilePath);
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        File sourceFile = new File(archiveFilePath);
        PackageParser.Package pkg = packageParser.parsePackage(sourceFile, archiveFilePath, metrics, 0);
        if (pkg == null) {
            return null;
        }
        if ((flags & 64) != 0) {
            packageParser.collectCertificates(pkg, 0);
        }
        PackageUserState state = new PackageUserState();
        return PackageParser.generatePackageInfo(pkg, null, flags, 0L, 0L, null, state);
    }

    public void getPackageSizeInfo(String packageName, IPackageStatsObserver observer) {
        getPackageSizeInfo(packageName, UserHandle.myUserId(), observer);
    }

    public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId) {
        throw new RuntimeException("Not implemented. Must override in a subclass.");
    }

    public static String getDataDirForUser(int userId, String packageName) {
        return Environment.getDataDirectory().toString() + "/user/" + userId + Separators.SLASH + packageName;
    }
}