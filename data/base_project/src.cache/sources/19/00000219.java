package android.app.admin;

import android.app.admin.IDevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.org.conscrypt.TrustedCertificateStore;
import gov.nist.core.Separators;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: DevicePolicyManager.class */
public class DevicePolicyManager {
    private static String TAG = "DevicePolicyManager";
    private final Context mContext;
    private final IDevicePolicyManager mService = IDevicePolicyManager.Stub.asInterface(ServiceManager.getService(Context.DEVICE_POLICY_SERVICE));
    public static final String ACTION_ADD_DEVICE_ADMIN = "android.app.action.ADD_DEVICE_ADMIN";
    public static final String ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED = "android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED";
    public static final String EXTRA_DEVICE_ADMIN = "android.app.extra.DEVICE_ADMIN";
    public static final String EXTRA_ADD_EXPLANATION = "android.app.extra.ADD_EXPLANATION";
    public static final String ACTION_SET_NEW_PASSWORD = "android.app.action.SET_NEW_PASSWORD";
    public static final int PASSWORD_QUALITY_UNSPECIFIED = 0;
    public static final int PASSWORD_QUALITY_BIOMETRIC_WEAK = 32768;
    public static final int PASSWORD_QUALITY_SOMETHING = 65536;
    public static final int PASSWORD_QUALITY_NUMERIC = 131072;
    public static final int PASSWORD_QUALITY_ALPHABETIC = 262144;
    public static final int PASSWORD_QUALITY_ALPHANUMERIC = 327680;
    public static final int PASSWORD_QUALITY_COMPLEX = 393216;
    public static final int RESET_PASSWORD_REQUIRE_ENTRY = 1;
    public static final int WIPE_EXTERNAL_STORAGE = 1;
    public static final int ENCRYPTION_STATUS_UNSUPPORTED = 0;
    public static final int ENCRYPTION_STATUS_INACTIVE = 1;
    public static final int ENCRYPTION_STATUS_ACTIVATING = 2;
    public static final int ENCRYPTION_STATUS_ACTIVE = 3;
    public static final String ACTION_START_ENCRYPTION = "android.app.action.START_ENCRYPTION";
    public static final int KEYGUARD_DISABLE_FEATURES_NONE = 0;
    public static final int KEYGUARD_DISABLE_WIDGETS_ALL = 1;
    public static final int KEYGUARD_DISABLE_SECURE_CAMERA = 2;
    public static final int KEYGUARD_DISABLE_FEATURES_ALL = Integer.MAX_VALUE;

    private DevicePolicyManager(Context context, Handler handler) {
        this.mContext = context;
    }

    public static DevicePolicyManager create(Context context, Handler handler) {
        DevicePolicyManager me = new DevicePolicyManager(context, handler);
        if (me.mService != null) {
            return me;
        }
        return null;
    }

    public boolean isAdminActive(ComponentName who) {
        if (this.mService != null) {
            try {
                return this.mService.isAdminActive(who, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return false;
            }
        }
        return false;
    }

    public List<ComponentName> getActiveAdmins() {
        if (this.mService != null) {
            try {
                return this.mService.getActiveAdmins(UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return null;
            }
        }
        return null;
    }

    public boolean packageHasActiveAdmins(String packageName) {
        if (this.mService != null) {
            try {
                return this.mService.packageHasActiveAdmins(packageName, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return false;
            }
        }
        return false;
    }

    public void removeActiveAdmin(ComponentName who) {
        if (this.mService != null) {
            try {
                this.mService.removeActiveAdmin(who, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean hasGrantedPolicy(ComponentName admin, int usesPolicy) {
        if (this.mService != null) {
            try {
                return this.mService.hasGrantedPolicy(admin, usesPolicy, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return false;
            }
        }
        return false;
    }

    public void setPasswordQuality(ComponentName admin, int quality) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordQuality(admin, quality, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordQuality(ComponentName admin) {
        return getPasswordQuality(admin, UserHandle.myUserId());
    }

    public int getPasswordQuality(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordQuality(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public void setPasswordMinimumLength(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumLength(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumLength(ComponentName admin) {
        return getPasswordMinimumLength(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumLength(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumLength(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public void setPasswordMinimumUpperCase(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumUpperCase(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumUpperCase(ComponentName admin) {
        return getPasswordMinimumUpperCase(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumUpperCase(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumUpperCase(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public void setPasswordMinimumLowerCase(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumLowerCase(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumLowerCase(ComponentName admin) {
        return getPasswordMinimumLowerCase(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumLowerCase(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumLowerCase(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public void setPasswordMinimumLetters(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumLetters(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumLetters(ComponentName admin) {
        return getPasswordMinimumLetters(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumLetters(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumLetters(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public void setPasswordMinimumNumeric(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumNumeric(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumNumeric(ComponentName admin) {
        return getPasswordMinimumNumeric(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumNumeric(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumNumeric(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public void setPasswordMinimumSymbols(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumSymbols(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumSymbols(ComponentName admin) {
        return getPasswordMinimumSymbols(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumSymbols(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumSymbols(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public void setPasswordMinimumNonLetter(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumNonLetter(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumNonLetter(ComponentName admin) {
        return getPasswordMinimumNonLetter(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumNonLetter(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumNonLetter(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public void setPasswordHistoryLength(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordHistoryLength(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setPasswordExpirationTimeout(ComponentName admin, long timeout) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordExpirationTimeout(admin, timeout, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public long getPasswordExpirationTimeout(ComponentName admin) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordExpirationTimeout(admin, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0L;
            }
        }
        return 0L;
    }

    public long getPasswordExpiration(ComponentName admin) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordExpiration(admin, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0L;
            }
        }
        return 0L;
    }

    public int getPasswordHistoryLength(ComponentName admin) {
        return getPasswordHistoryLength(admin, UserHandle.myUserId());
    }

    public int getPasswordHistoryLength(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordHistoryLength(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public int getPasswordMaximumLength(int quality) {
        return 16;
    }

    public boolean isActivePasswordSufficient() {
        if (this.mService != null) {
            try {
                return this.mService.isActivePasswordSufficient(UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return false;
            }
        }
        return false;
    }

    public int getCurrentFailedPasswordAttempts() {
        if (this.mService != null) {
            try {
                return this.mService.getCurrentFailedPasswordAttempts(UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return -1;
            }
        }
        return -1;
    }

    public void setMaximumFailedPasswordsForWipe(ComponentName admin, int num) {
        if (this.mService != null) {
            try {
                this.mService.setMaximumFailedPasswordsForWipe(admin, num, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getMaximumFailedPasswordsForWipe(ComponentName admin) {
        return getMaximumFailedPasswordsForWipe(admin, UserHandle.myUserId());
    }

    public int getMaximumFailedPasswordsForWipe(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getMaximumFailedPasswordsForWipe(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public boolean resetPassword(String password, int flags) {
        if (this.mService != null) {
            try {
                return this.mService.resetPassword(password, flags, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return false;
            }
        }
        return false;
    }

    public void setMaximumTimeToLock(ComponentName admin, long timeMs) {
        if (this.mService != null) {
            try {
                this.mService.setMaximumTimeToLock(admin, timeMs, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public long getMaximumTimeToLock(ComponentName admin) {
        return getMaximumTimeToLock(admin, UserHandle.myUserId());
    }

    public long getMaximumTimeToLock(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getMaximumTimeToLock(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0L;
            }
        }
        return 0L;
    }

    public void lockNow() {
        if (this.mService != null) {
            try {
                this.mService.lockNow();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void wipeData(int flags) {
        if (this.mService != null) {
            try {
                this.mService.wipeData(flags, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public ComponentName setGlobalProxy(ComponentName admin, Proxy proxySpec, List<String> exclusionList) {
        String hostSpec;
        String exclSpec;
        if (proxySpec == null) {
            throw new NullPointerException();
        }
        if (this.mService != null) {
            try {
                if (proxySpec.equals(Proxy.NO_PROXY)) {
                    hostSpec = null;
                    exclSpec = null;
                } else if (!proxySpec.type().equals(Proxy.Type.HTTP)) {
                    throw new IllegalArgumentException();
                } else {
                    InetSocketAddress sa = (InetSocketAddress) proxySpec.address();
                    String hostName = sa.getHostName();
                    int port = sa.getPort();
                    StringBuilder hostBuilder = new StringBuilder();
                    hostSpec = hostBuilder.append(hostName).append(Separators.COLON).append(Integer.toString(port)).toString();
                    if (exclusionList == null) {
                        exclSpec = "";
                    } else {
                        StringBuilder listBuilder = new StringBuilder();
                        boolean firstDomain = true;
                        for (String exclDomain : exclusionList) {
                            if (!firstDomain) {
                                listBuilder = listBuilder.append(Separators.COMMA);
                            } else {
                                firstDomain = false;
                            }
                            listBuilder = listBuilder.append(exclDomain.trim());
                        }
                        exclSpec = listBuilder.toString();
                    }
                    android.net.Proxy.validate(hostName, Integer.toString(port), exclSpec);
                }
                return this.mService.setGlobalProxy(admin, hostSpec, exclSpec, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return null;
            }
        }
        return null;
    }

    public ComponentName getGlobalProxyAdmin() {
        if (this.mService != null) {
            try {
                return this.mService.getGlobalProxyAdmin(UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return null;
            }
        }
        return null;
    }

    public int setStorageEncryption(ComponentName admin, boolean encrypt) {
        if (this.mService != null) {
            try {
                return this.mService.setStorageEncryption(admin, encrypt, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public boolean getStorageEncryption(ComponentName admin) {
        if (this.mService != null) {
            try {
                return this.mService.getStorageEncryption(admin, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return false;
            }
        }
        return false;
    }

    public int getStorageEncryptionStatus() {
        return getStorageEncryptionStatus(UserHandle.myUserId());
    }

    public int getStorageEncryptionStatus(int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getStorageEncryptionStatus(userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public boolean installCaCert(byte[] certBuffer) {
        if (this.mService != null) {
            try {
                return this.mService.installCaCert(certBuffer);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return false;
            }
        }
        return false;
    }

    public void uninstallCaCert(byte[] certBuffer) {
        if (this.mService != null) {
            try {
                this.mService.uninstallCaCert(certBuffer);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public static boolean hasAnyCaCertsInstalled() {
        TrustedCertificateStore certStore = new TrustedCertificateStore();
        Set<String> aliases = certStore.userAliases();
        return (aliases == null || aliases.isEmpty()) ? false : true;
    }

    public boolean hasCaCertInstalled(byte[] certBuffer) {
        TrustedCertificateStore certStore = new TrustedCertificateStore();
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certBuffer));
            return certStore.getCertificateAlias(cert) != null;
        } catch (CertificateException ce) {
            Log.w(TAG, "Could not parse certificate", ce);
            return false;
        }
    }

    public void setCameraDisabled(ComponentName admin, boolean disabled) {
        if (this.mService != null) {
            try {
                this.mService.setCameraDisabled(admin, disabled, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean getCameraDisabled(ComponentName admin) {
        return getCameraDisabled(admin, UserHandle.myUserId());
    }

    public boolean getCameraDisabled(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getCameraDisabled(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return false;
            }
        }
        return false;
    }

    public void setKeyguardDisabledFeatures(ComponentName admin, int which) {
        if (this.mService != null) {
            try {
                this.mService.setKeyguardDisabledFeatures(admin, which, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getKeyguardDisabledFeatures(ComponentName admin) {
        return getKeyguardDisabledFeatures(admin, UserHandle.myUserId());
    }

    public int getKeyguardDisabledFeatures(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getKeyguardDisabledFeatures(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
                return 0;
            }
        }
        return 0;
    }

    public void setActiveAdmin(ComponentName policyReceiver, boolean refreshing) {
        if (this.mService != null) {
            try {
                this.mService.setActiveAdmin(policyReceiver, refreshing, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public DeviceAdminInfo getAdminInfo(ComponentName cn) {
        try {
            ActivityInfo ai = this.mContext.getPackageManager().getReceiverInfo(cn, 128);
            ResolveInfo ri = new ResolveInfo();
            ri.activityInfo = ai;
            try {
                return new DeviceAdminInfo(this.mContext, ri);
            } catch (IOException e) {
                Log.w(TAG, "Unable to parse device policy " + cn, e);
                return null;
            } catch (XmlPullParserException e2) {
                Log.w(TAG, "Unable to parse device policy " + cn, e2);
                return null;
            }
        } catch (PackageManager.NameNotFoundException e3) {
            Log.w(TAG, "Unable to retrieve device policy " + cn, e3);
            return null;
        }
    }

    public void getRemoveWarning(ComponentName admin, RemoteCallback result) {
        if (this.mService != null) {
            try {
                this.mService.getRemoveWarning(admin, result, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setActivePasswordState(int quality, int length, int letters, int uppercase, int lowercase, int numbers, int symbols, int nonletter, int userHandle) {
        if (this.mService != null) {
            try {
                this.mService.setActivePasswordState(quality, length, letters, uppercase, lowercase, numbers, symbols, nonletter, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void reportFailedPasswordAttempt(int userHandle) {
        if (this.mService != null) {
            try {
                this.mService.reportFailedPasswordAttempt(userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void reportSuccessfulPasswordAttempt(int userHandle) {
        if (this.mService != null) {
            try {
                this.mService.reportSuccessfulPasswordAttempt(userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean setDeviceOwner(String packageName) throws IllegalArgumentException, IllegalStateException {
        return setDeviceOwner(packageName, null);
    }

    public boolean setDeviceOwner(String packageName, String ownerName) throws IllegalArgumentException, IllegalStateException {
        if (this.mService != null) {
            try {
                return this.mService.setDeviceOwner(packageName, ownerName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to set device owner");
                return false;
            }
        }
        return false;
    }

    public boolean isDeviceOwnerApp(String packageName) {
        if (this.mService != null) {
            try {
                return this.mService.isDeviceOwner(packageName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to check device owner");
                return false;
            }
        }
        return false;
    }

    public boolean isDeviceOwner(String packageName) {
        return isDeviceOwnerApp(packageName);
    }

    public String getDeviceOwner() {
        if (this.mService != null) {
            try {
                return this.mService.getDeviceOwner();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to get device owner");
                return null;
            }
        }
        return null;
    }

    public String getDeviceOwnerName() {
        if (this.mService != null) {
            try {
                return this.mService.getDeviceOwnerName();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to get device owner");
                return null;
            }
        }
        return null;
    }
}