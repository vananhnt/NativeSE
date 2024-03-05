package android.content.pm;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.os.UserHandle;
import android.provider.Telephony;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Slog;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import com.android.internal.R;
import com.android.internal.util.XmlUtils;
import gov.nist.core.Separators;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: PackageParser.class */
public class PackageParser {
    private static final boolean DEBUG_JAR = false;
    private static final boolean DEBUG_PARSER = false;
    private static final boolean DEBUG_BACKUP = false;
    private static final String ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml";
    private String mArchiveSourcePath;
    private String[] mSeparateProcesses;
    private boolean mOnlyCoreApps;
    private static final String SDK_CODENAME;
    private int mParseError = 1;
    private static final Object mSync;
    private static WeakReference<byte[]> mReadBuffer;
    private static boolean sCompatibilityModeEnabled;
    private static final int PARSE_DEFAULT_INSTALL_LOCATION = -1;
    private ParsePackageItemArgs mParseInstrumentationArgs;
    private ParseComponentArgs mParseActivityArgs;
    private ParseComponentArgs mParseActivityAliasArgs;
    private ParseComponentArgs mParseServiceArgs;
    private ParseComponentArgs mParseProviderArgs;
    private static final boolean RIGID_PARSER = false;
    private static final String TAG = "PackageParser";
    public static final int PARSE_IS_SYSTEM = 1;
    public static final int PARSE_CHATTY = 2;
    public static final int PARSE_MUST_BE_APK = 4;
    public static final int PARSE_IGNORE_PROCESSES = 8;
    public static final int PARSE_FORWARD_LOCK = 16;
    public static final int PARSE_ON_SDCARD = 32;
    public static final int PARSE_IS_SYSTEM_DIR = 64;
    public static final int PARSE_IS_PRIVILEGED = 128;
    private static final String ANDROID_RESOURCES = "http://schemas.android.com/apk/res/android";
    public static final NewPermissionInfo[] NEW_PERMISSIONS = {new NewPermissionInfo(Manifest.permission.WRITE_EXTERNAL_STORAGE, 4, 0), new NewPermissionInfo(Manifest.permission.READ_PHONE_STATE, 4, 0)};
    public static final SplitPermissionInfo[] SPLIT_PERMISSIONS = {new SplitPermissionInfo(Manifest.permission.WRITE_EXTERNAL_STORAGE, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, HapticFeedbackConstants.SAFE_MODE_ENABLED), new SplitPermissionInfo(Manifest.permission.READ_CONTACTS, new String[]{Manifest.permission.READ_CALL_LOG}, 16), new SplitPermissionInfo(Manifest.permission.WRITE_CONTACTS, new String[]{Manifest.permission.WRITE_CALL_LOG}, 16)};
    private static final int SDK_VERSION = Build.VERSION.SDK_INT;

    /* loaded from: PackageParser$IntentInfo.class */
    public static class IntentInfo extends IntentFilter {
        public boolean hasDefault;
        public int labelRes;
        public CharSequence nonLocalizedLabel;
        public int icon;
        public int logo;
        public int preferred;
    }

    /* loaded from: PackageParser$NewPermissionInfo.class */
    public static class NewPermissionInfo {
        public final String name;
        public final int sdkVersion;
        public final int fileVersion;

        public NewPermissionInfo(String name, int sdkVersion, int fileVersion) {
            this.name = name;
            this.sdkVersion = sdkVersion;
            this.fileVersion = fileVersion;
        }
    }

    /* loaded from: PackageParser$SplitPermissionInfo.class */
    public static class SplitPermissionInfo {
        public final String rootPerm;
        public final String[] newPerms;
        public final int targetSdk;

        public SplitPermissionInfo(String rootPerm, String[] newPerms, int targetSdk) {
            this.rootPerm = rootPerm;
            this.newPerms = newPerms;
            this.targetSdk = targetSdk;
        }
    }

    static {
        SDK_CODENAME = "REL".equals(Build.VERSION.CODENAME) ? null : Build.VERSION.CODENAME;
        mSync = new Object();
        sCompatibilityModeEnabled = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageParser$ParsePackageItemArgs.class */
    public static class ParsePackageItemArgs {
        final Package owner;
        final String[] outError;
        final int nameRes;
        final int labelRes;
        final int iconRes;
        final int logoRes;
        String tag;
        TypedArray sa;

        ParsePackageItemArgs(Package _owner, String[] _outError, int _nameRes, int _labelRes, int _iconRes, int _logoRes) {
            this.owner = _owner;
            this.outError = _outError;
            this.nameRes = _nameRes;
            this.labelRes = _labelRes;
            this.iconRes = _iconRes;
            this.logoRes = _logoRes;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PackageParser$ParseComponentArgs.class */
    public static class ParseComponentArgs extends ParsePackageItemArgs {
        final String[] sepProcesses;
        final int processRes;
        final int descriptionRes;
        final int enabledRes;
        int flags;

        ParseComponentArgs(Package _owner, String[] _outError, int _nameRes, int _labelRes, int _iconRes, int _logoRes, String[] _sepProcesses, int _processRes, int _descriptionRes, int _enabledRes) {
            super(_owner, _outError, _nameRes, _labelRes, _iconRes, _logoRes);
            this.sepProcesses = _sepProcesses;
            this.processRes = _processRes;
            this.descriptionRes = _descriptionRes;
            this.enabledRes = _enabledRes;
        }
    }

    /* loaded from: PackageParser$PackageLite.class */
    public static class PackageLite {
        public final String packageName;
        public final int versionCode;
        public final int installLocation;
        public final VerifierInfo[] verifiers;

        public PackageLite(String packageName, int versionCode, int installLocation, List<VerifierInfo> verifiers) {
            this.packageName = packageName;
            this.versionCode = versionCode;
            this.installLocation = installLocation;
            this.verifiers = (VerifierInfo[]) verifiers.toArray(new VerifierInfo[verifiers.size()]);
        }
    }

    public PackageParser(String archiveSourcePath) {
        this.mArchiveSourcePath = archiveSourcePath;
    }

    public void setSeparateProcesses(String[] procs) {
        this.mSeparateProcesses = procs;
    }

    public void setOnlyCoreApps(boolean onlyCoreApps) {
        this.mOnlyCoreApps = onlyCoreApps;
    }

    private static final boolean isPackageFilename(String name) {
        return name.endsWith(".apk");
    }

    public static PackageInfo generatePackageInfo(Package p, int[] gids, int flags, long firstInstallTime, long lastUpdateTime, HashSet<String> grantedPermissions, PackageUserState state) {
        return generatePackageInfo(p, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissions, state, UserHandle.getCallingUserId());
    }

    private static boolean checkUseInstalledOrBlocked(int flags, PackageUserState state) {
        return (state.installed && !state.blocked) || (flags & 8192) != 0;
    }

    public static PackageInfo generatePackageInfo(Package p, int[] gids, int flags, long firstInstallTime, long lastUpdateTime, HashSet<String> grantedPermissions, PackageUserState state, int userId) {
        int N;
        int N2;
        int N3;
        int N4;
        int N5;
        if (!checkUseInstalledOrBlocked(flags, state)) {
            return null;
        }
        PackageInfo pi = new PackageInfo();
        pi.packageName = p.packageName;
        pi.versionCode = p.mVersionCode;
        pi.versionName = p.mVersionName;
        pi.sharedUserId = p.mSharedUserId;
        pi.sharedUserLabel = p.mSharedUserLabel;
        pi.applicationInfo = generateApplicationInfo(p, flags, state, userId);
        pi.installLocation = p.installLocation;
        if ((pi.applicationInfo.flags & 1) != 0 || (pi.applicationInfo.flags & 128) != 0) {
            pi.requiredForAllUsers = p.mRequiredForAllUsers;
        }
        pi.restrictedAccountType = p.mRestrictedAccountType;
        pi.requiredAccountType = p.mRequiredAccountType;
        pi.firstInstallTime = firstInstallTime;
        pi.lastUpdateTime = lastUpdateTime;
        if ((flags & 256) != 0) {
            pi.gids = gids;
        }
        if ((flags & 16384) != 0) {
            int N6 = p.configPreferences.size();
            if (N6 > 0) {
                pi.configPreferences = new ConfigurationInfo[N6];
                p.configPreferences.toArray(pi.configPreferences);
            }
            int N7 = p.reqFeatures != null ? p.reqFeatures.size() : 0;
            if (N7 > 0) {
                pi.reqFeatures = new FeatureInfo[N7];
                p.reqFeatures.toArray(pi.reqFeatures);
            }
        }
        if ((flags & 1) != 0 && (N5 = p.activities.size()) > 0) {
            if ((flags & 512) != 0) {
                pi.activities = new ActivityInfo[N5];
            } else {
                int num = 0;
                for (int i = 0; i < N5; i++) {
                    if (p.activities.get(i).info.enabled) {
                        num++;
                    }
                }
                pi.activities = new ActivityInfo[num];
            }
            int j = 0;
            for (int i2 = 0; i2 < N5; i2++) {
                Activity activity = p.activities.get(i2);
                if (activity.info.enabled || (flags & 512) != 0) {
                    int i3 = j;
                    j++;
                    pi.activities[i3] = generateActivityInfo(p.activities.get(i2), flags, state, userId);
                }
            }
        }
        if ((flags & 2) != 0 && (N4 = p.receivers.size()) > 0) {
            if ((flags & 512) != 0) {
                pi.receivers = new ActivityInfo[N4];
            } else {
                int num2 = 0;
                for (int i4 = 0; i4 < N4; i4++) {
                    if (p.receivers.get(i4).info.enabled) {
                        num2++;
                    }
                }
                pi.receivers = new ActivityInfo[num2];
            }
            int j2 = 0;
            for (int i5 = 0; i5 < N4; i5++) {
                Activity activity2 = p.receivers.get(i5);
                if (activity2.info.enabled || (flags & 512) != 0) {
                    int i6 = j2;
                    j2++;
                    pi.receivers[i6] = generateActivityInfo(p.receivers.get(i5), flags, state, userId);
                }
            }
        }
        if ((flags & 4) != 0 && (N3 = p.services.size()) > 0) {
            if ((flags & 512) != 0) {
                pi.services = new ServiceInfo[N3];
            } else {
                int num3 = 0;
                for (int i7 = 0; i7 < N3; i7++) {
                    if (p.services.get(i7).info.enabled) {
                        num3++;
                    }
                }
                pi.services = new ServiceInfo[num3];
            }
            int j3 = 0;
            for (int i8 = 0; i8 < N3; i8++) {
                Service service = p.services.get(i8);
                if (service.info.enabled || (flags & 512) != 0) {
                    int i9 = j3;
                    j3++;
                    pi.services[i9] = generateServiceInfo(p.services.get(i8), flags, state, userId);
                }
            }
        }
        if ((flags & 8) != 0 && (N2 = p.providers.size()) > 0) {
            if ((flags & 512) != 0) {
                pi.providers = new ProviderInfo[N2];
            } else {
                int num4 = 0;
                for (int i10 = 0; i10 < N2; i10++) {
                    if (p.providers.get(i10).info.enabled) {
                        num4++;
                    }
                }
                pi.providers = new ProviderInfo[num4];
            }
            int j4 = 0;
            for (int i11 = 0; i11 < N2; i11++) {
                Provider provider = p.providers.get(i11);
                if (provider.info.enabled || (flags & 512) != 0) {
                    int i12 = j4;
                    j4++;
                    pi.providers[i12] = generateProviderInfo(p.providers.get(i11), flags, state, userId);
                }
            }
        }
        if ((flags & 16) != 0 && (N = p.instrumentation.size()) > 0) {
            pi.instrumentation = new InstrumentationInfo[N];
            for (int i13 = 0; i13 < N; i13++) {
                pi.instrumentation[i13] = generateInstrumentationInfo(p.instrumentation.get(i13), flags);
            }
        }
        if ((flags & 4096) != 0) {
            int N8 = p.permissions.size();
            if (N8 > 0) {
                pi.permissions = new PermissionInfo[N8];
                for (int i14 = 0; i14 < N8; i14++) {
                    pi.permissions[i14] = generatePermissionInfo(p.permissions.get(i14), flags);
                }
            }
            int N9 = p.requestedPermissions.size();
            if (N9 > 0) {
                pi.requestedPermissions = new String[N9];
                pi.requestedPermissionsFlags = new int[N9];
                for (int i15 = 0; i15 < N9; i15++) {
                    String perm = p.requestedPermissions.get(i15);
                    pi.requestedPermissions[i15] = perm;
                    if (p.requestedPermissionsRequired.get(i15).booleanValue()) {
                        int[] iArr = pi.requestedPermissionsFlags;
                        int i16 = i15;
                        iArr[i16] = iArr[i16] | 1;
                    }
                    if (grantedPermissions != null && grantedPermissions.contains(perm)) {
                        int[] iArr2 = pi.requestedPermissionsFlags;
                        int i17 = i15;
                        iArr2[i17] = iArr2[i17] | 2;
                    }
                }
            }
        }
        if ((flags & 64) != 0) {
            int N10 = p.mSignatures != null ? p.mSignatures.length : 0;
            if (N10 > 0) {
                pi.signatures = new Signature[N10];
                System.arraycopy(p.mSignatures, 0, pi.signatures, 0, N10);
            }
        }
        return pi;
    }

    private Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is = new BufferedInputStream(jarFile.getInputStream(je));
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            if (je != null) {
                return je.getCertificates();
            }
            return null;
        } catch (IOException e) {
            Slog.w(TAG, "Exception reading " + je.getName() + " in " + jarFile.getName(), e);
            return null;
        } catch (RuntimeException e2) {
            Slog.w(TAG, "Exception reading " + je.getName() + " in " + jarFile.getName(), e2);
            return null;
        }
    }

    public int getParseError() {
        return this.mParseError;
    }

    public Package parsePackage(File sourceFile, String destCodePath, DisplayMetrics metrics, int flags) {
        this.mParseError = 1;
        this.mArchiveSourcePath = sourceFile.getPath();
        if (!sourceFile.isFile()) {
            Slog.w(TAG, "Skipping dir: " + this.mArchiveSourcePath);
            this.mParseError = -100;
            return null;
        } else if (!isPackageFilename(sourceFile.getName()) && (flags & 4) != 0) {
            if ((flags & 1) == 0) {
                Slog.w(TAG, "Skipping non-package file: " + this.mArchiveSourcePath);
            }
            this.mParseError = -100;
            return null;
        } else {
            XmlResourceParser parser = null;
            AssetManager assmgr = null;
            Resources res = null;
            boolean assetError = true;
            try {
                assmgr = new AssetManager();
                int cookie = assmgr.addAssetPath(this.mArchiveSourcePath);
                if (cookie != 0) {
                    res = new Resources(assmgr, metrics, null);
                    assmgr.setConfiguration(0, 0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Build.VERSION.RESOURCES_SDK_INT);
                    parser = assmgr.openXmlResourceParser(cookie, ANDROID_MANIFEST_FILENAME);
                    assetError = false;
                } else {
                    Slog.w(TAG, "Failed adding asset path:" + this.mArchiveSourcePath);
                }
            } catch (Exception e) {
                Slog.w(TAG, "Unable to read AndroidManifest.xml of " + this.mArchiveSourcePath, e);
            }
            if (assetError) {
                if (assmgr != null) {
                    assmgr.close();
                }
                this.mParseError = PackageManager.INSTALL_PARSE_FAILED_BAD_MANIFEST;
                return null;
            }
            String[] errorText = new String[1];
            Package pkg = null;
            Exception errorException = null;
            try {
                pkg = parsePackage(res, parser, flags, errorText);
            } catch (Exception e2) {
                errorException = e2;
                this.mParseError = PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION;
            }
            if (pkg == null) {
                if (!this.mOnlyCoreApps || this.mParseError != 1) {
                    if (errorException != null) {
                        Slog.w(TAG, this.mArchiveSourcePath, errorException);
                    } else {
                        Slog.w(TAG, this.mArchiveSourcePath + " (at " + parser.getPositionDescription() + "): " + errorText[0]);
                    }
                    if (this.mParseError == 1) {
                        this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                    }
                }
                parser.close();
                assmgr.close();
                return null;
            }
            parser.close();
            assmgr.close();
            pkg.mPath = destCodePath;
            pkg.mScanPath = this.mArchiveSourcePath;
            pkg.mSignatures = null;
            return pkg;
        }
    }

    public boolean collectManifestDigest(Package pkg) {
        try {
            JarFile jarFile = new JarFile(this.mArchiveSourcePath);
            ZipEntry je = jarFile.getEntry(ANDROID_MANIFEST_FILENAME);
            if (je != null) {
                pkg.manifestDigest = ManifestDigest.fromInputStream(jarFile.getInputStream(je));
            }
            jarFile.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean collectCertificates(Package pkg, int flags) {
        WeakReference<byte[]> readBufferRef;
        pkg.mSignatures = null;
        byte[] readBuffer = null;
        synchronized (mSync) {
            readBufferRef = mReadBuffer;
            if (readBufferRef != null) {
                mReadBuffer = null;
                readBuffer = readBufferRef.get();
            }
            if (readBuffer == null) {
                readBuffer = new byte[8192];
                readBufferRef = new WeakReference<>(readBuffer);
            }
        }
        try {
            JarFile jarFile = new JarFile(this.mArchiveSourcePath);
            Certificate[] certs = null;
            if ((flags & 1) != 0) {
                JarEntry jarEntry = jarFile.getJarEntry(ANDROID_MANIFEST_FILENAME);
                certs = loadCertificates(jarFile, jarEntry, readBuffer);
                if (certs == null) {
                    Slog.e(TAG, "Package " + pkg.packageName + " has no certificates at entry " + jarEntry.getName() + "; ignoring!");
                    jarFile.close();
                    this.mParseError = PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES;
                    return false;
                }
            } else {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry je = entries.nextElement();
                    if (!je.isDirectory()) {
                        String name = je.getName();
                        if (!name.startsWith("META-INF/")) {
                            if (ANDROID_MANIFEST_FILENAME.equals(name)) {
                                pkg.manifestDigest = ManifestDigest.fromInputStream(jarFile.getInputStream(je));
                            }
                            Certificate[] localCerts = loadCertificates(jarFile, je, readBuffer);
                            if (localCerts == null) {
                                Slog.e(TAG, "Package " + pkg.packageName + " has no certificates at entry " + je.getName() + "; ignoring!");
                                jarFile.close();
                                this.mParseError = PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES;
                                return false;
                            } else if (certs == null) {
                                certs = localCerts;
                            } else {
                                for (int i = 0; i < certs.length; i++) {
                                    boolean found = false;
                                    int j = 0;
                                    while (true) {
                                        if (j >= localCerts.length) {
                                            break;
                                        } else if (certs[i] == null || !certs[i].equals(localCerts[j])) {
                                            j++;
                                        } else {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found || certs.length != localCerts.length) {
                                        Slog.e(TAG, "Package " + pkg.packageName + " has mismatched certificates at entry " + je.getName() + "; ignoring!");
                                        jarFile.close();
                                        this.mParseError = PackageManager.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES;
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            jarFile.close();
            synchronized (mSync) {
                mReadBuffer = readBufferRef;
            }
            if (certs != null && certs.length > 0) {
                int N = certs.length;
                pkg.mSignatures = new Signature[certs.length];
                for (int i2 = 0; i2 < N; i2++) {
                    pkg.mSignatures[i2] = new Signature(certs[i2].getEncoded());
                }
                pkg.mSigningKeys = new HashSet();
                for (Certificate certificate : certs) {
                    pkg.mSigningKeys.add(certificate.getPublicKey());
                }
                return true;
            }
            Slog.e(TAG, "Package " + pkg.packageName + " has no certificates; ignoring!");
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES;
            return false;
        } catch (IOException e) {
            Slog.w(TAG, "Exception reading " + this.mArchiveSourcePath, e);
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING;
            return false;
        } catch (RuntimeException e2) {
            Slog.w(TAG, "Exception reading " + this.mArchiveSourcePath, e2);
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION;
            return false;
        } catch (CertificateEncodingException e3) {
            Slog.w(TAG, "Exception reading " + this.mArchiveSourcePath, e3);
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING;
            return false;
        }
    }

    public static PackageLite parsePackageLite(String packageFilePath, int flags) {
        AssetManager assmgr = null;
        try {
            assmgr = new AssetManager();
            assmgr.setConfiguration(0, 0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Build.VERSION.RESOURCES_SDK_INT);
            int cookie = assmgr.addAssetPath(packageFilePath);
            if (cookie == 0) {
                return null;
            }
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            Resources res = new Resources(assmgr, metrics, null);
            XmlResourceParser parser = assmgr.openXmlResourceParser(cookie, ANDROID_MANIFEST_FILENAME);
            String[] errors = new String[1];
            PackageLite packageLite = null;
            try {
                try {
                    packageLite = parsePackageLite(res, parser, parser, flags, errors);
                    if (parser != null) {
                        parser.close();
                    }
                    if (assmgr != null) {
                        assmgr.close();
                    }
                } catch (IOException e) {
                    Slog.w(TAG, packageFilePath, e);
                    if (parser != null) {
                        parser.close();
                    }
                    if (assmgr != null) {
                        assmgr.close();
                    }
                } catch (XmlPullParserException e2) {
                    Slog.w(TAG, packageFilePath, e2);
                    if (parser != null) {
                        parser.close();
                    }
                    if (assmgr != null) {
                        assmgr.close();
                    }
                }
                if (packageLite == null) {
                    Slog.e(TAG, "parsePackageLite error: " + errors[0]);
                    return null;
                }
                return packageLite;
            } catch (Throwable th) {
                if (parser != null) {
                    parser.close();
                }
                if (assmgr != null) {
                    assmgr.close();
                }
                throw th;
            }
        } catch (Exception e3) {
            if (assmgr != null) {
                assmgr.close();
            }
            Slog.w(TAG, "Unable to read AndroidManifest.xml of " + packageFilePath, e3);
            return null;
        }
    }

    private static String validateName(String name, boolean requiresSeparator) {
        int N = name.length();
        boolean hasSep = false;
        boolean front = true;
        for (int i = 0; i < N; i++) {
            char c = name.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                front = false;
            } else if (front || ((c < '0' || c > '9') && c != '_')) {
                if (c == '.') {
                    hasSep = true;
                    front = true;
                } else {
                    return "bad character '" + c + Separators.QUOTE;
                }
            }
        }
        if (hasSep || !requiresSeparator) {
            return null;
        }
        return "must have at least one '.' separator";
    }

    private static String parsePackageName(XmlPullParser parser, AttributeSet attrs, int flags, String[] outError) throws IOException, XmlPullParserException {
        int type;
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            outError[0] = "No start tag found";
            return null;
        } else if (!parser.getName().equals("manifest")) {
            outError[0] = "No <manifest> tag";
            return null;
        } else {
            String pkgName = attrs.getAttributeValue(null, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
            if (pkgName == null || pkgName.length() == 0) {
                outError[0] = "<manifest> does not specify package";
                return null;
            }
            String nameError = validateName(pkgName, true);
            if (nameError != null && !"android".equals(pkgName)) {
                outError[0] = "<manifest> specifies bad package name \"" + pkgName + "\": " + nameError;
                return null;
            }
            return pkgName.intern();
        }
    }

    private static PackageLite parsePackageLite(Resources res, XmlPullParser parser, AttributeSet attrs, int flags, String[] outError) throws IOException, XmlPullParserException {
        int type;
        VerifierInfo verifier;
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            outError[0] = "No start tag found";
            return null;
        } else if (!parser.getName().equals("manifest")) {
            outError[0] = "No <manifest> tag";
            return null;
        } else {
            String pkgName = attrs.getAttributeValue(null, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
            if (pkgName == null || pkgName.length() == 0) {
                outError[0] = "<manifest> does not specify package";
                return null;
            }
            String nameError = validateName(pkgName, true);
            if (nameError != null && !"android".equals(pkgName)) {
                outError[0] = "<manifest> specifies bad package name \"" + pkgName + "\": " + nameError;
                return null;
            }
            int installLocation = -1;
            int versionCode = 0;
            int numFound = 0;
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                String attr = attrs.getAttributeName(i);
                if (attr.equals("installLocation")) {
                    installLocation = attrs.getAttributeIntValue(i, -1);
                    numFound++;
                } else if (attr.equals("versionCode")) {
                    versionCode = attrs.getAttributeIntValue(i, 0);
                    numFound++;
                }
                if (numFound >= 2) {
                    break;
                }
            }
            int searchDepth = parser.getDepth() + 1;
            List<VerifierInfo> verifiers = new ArrayList<>();
            while (true) {
                int type2 = parser.next();
                if (type2 == 1 || (type2 == 3 && parser.getDepth() < searchDepth)) {
                    break;
                } else if (type2 != 3 && type2 != 4 && parser.getDepth() == searchDepth && "package-verifier".equals(parser.getName()) && (verifier = parseVerifier(res, parser, attrs, flags, outError)) != null) {
                    verifiers.add(verifier);
                }
            }
            return new PackageLite(pkgName.intern(), versionCode, installLocation, verifiers);
        }
    }

    public static Signature stringToSignature(String str) {
        int N = str.length();
        byte[] sig = new byte[N];
        for (int i = 0; i < N; i++) {
            sig[i] = (byte) str.charAt(i);
        }
        return new Signature(sig);
    }

    /* JADX WARN: Code restructure failed: missing block: B:209:0x0794, code lost:
        if (r17 != false) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:211:0x079f, code lost:
        if (r0.instrumentation.size() != 0) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:212:0x07a2, code lost:
        r12[0] = "<manifest> does not contain an <application> or <instrumentation>";
        r8.mParseError = android.content.pm.PackageManager.INSTALL_PARSE_FAILED_MANIFEST_EMPTY;
     */
    /* JADX WARN: Code restructure failed: missing block: B:213:0x07af, code lost:
        r0 = android.content.pm.PackageParser.NEW_PERMISSIONS.length;
        r28 = null;
        r29 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:215:0x07bf, code lost:
        if (r29 >= r0) goto L132;
     */
    /* JADX WARN: Code restructure failed: missing block: B:216:0x07c2, code lost:
        r0 = android.content.pm.PackageParser.NEW_PERMISSIONS[r29];
     */
    /* JADX WARN: Code restructure failed: missing block: B:217:0x07d7, code lost:
        if (r0.applicationInfo.targetSdkVersion < r0.sdkVersion) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:220:0x07ea, code lost:
        if (r0.requestedPermissions.contains(r0.name) != false) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:222:0x07ef, code lost:
        if (r28 != null) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:223:0x07f2, code lost:
        r28 = new java.lang.StringBuilder(128);
        r28.append(r0.packageName);
        r28.append(": compat added ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:224:0x0815, code lost:
        r28.append(' ');
     */
    /* JADX WARN: Code restructure failed: missing block: B:225:0x081d, code lost:
        r28.append(r0.name);
        r0.requestedPermissions.add(r0.name);
        r0.requestedPermissionsRequired.add(java.lang.Boolean.TRUE);
     */
    /* JADX WARN: Code restructure failed: missing block: B:226:0x0842, code lost:
        r29 = r29 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:228:0x084a, code lost:
        if (r28 == null) goto L64;
     */
    /* JADX WARN: Code restructure failed: missing block: B:229:0x084d, code lost:
        android.util.Slog.i(android.content.pm.PackageParser.TAG, r28.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:230:0x0858, code lost:
        r0 = android.content.pm.PackageParser.SPLIT_PERMISSIONS.length;
        r30 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:232:0x0865, code lost:
        if (r30 >= r0) goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:233:0x0868, code lost:
        r0 = android.content.pm.PackageParser.SPLIT_PERMISSIONS[r30];
     */
    /* JADX WARN: Code restructure failed: missing block: B:234:0x087d, code lost:
        if (r0.applicationInfo.targetSdkVersion >= r0.targetSdk) goto L83;
     */
    /* JADX WARN: Code restructure failed: missing block: B:236:0x088d, code lost:
        if (r0.requestedPermissions.contains(r0.rootPerm) != false) goto L71;
     */
    /* JADX WARN: Code restructure failed: missing block: B:238:0x0893, code lost:
        r32 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:240:0x089e, code lost:
        if (r32 >= r0.newPerms.length) goto L80;
     */
    /* JADX WARN: Code restructure failed: missing block: B:241:0x08a1, code lost:
        r0 = r0.newPerms[r32];
     */
    /* JADX WARN: Code restructure failed: missing block: B:242:0x08b5, code lost:
        if (r0.requestedPermissions.contains(r0) != false) goto L79;
     */
    /* JADX WARN: Code restructure failed: missing block: B:243:0x08b8, code lost:
        r0.requestedPermissions.add(r0);
        r0.requestedPermissionsRequired.add(java.lang.Boolean.TRUE);
     */
    /* JADX WARN: Code restructure failed: missing block: B:244:0x08cf, code lost:
        r32 = r32 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:245:0x08d5, code lost:
        r30 = r30 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:247:0x08dd, code lost:
        if (r20 < 0) goto L131;
     */
    /* JADX WARN: Code restructure failed: missing block: B:249:0x08e2, code lost:
        if (r20 <= 0) goto L91;
     */
    /* JADX WARN: Code restructure failed: missing block: B:251:0x08ee, code lost:
        if (r0.applicationInfo.targetSdkVersion < 4) goto L91;
     */
    /* JADX WARN: Code restructure failed: missing block: B:252:0x08f1, code lost:
        r0.applicationInfo.flags |= 512;
     */
    /* JADX WARN: Code restructure failed: missing block: B:254:0x0903, code lost:
        if (r21 == 0) goto L94;
     */
    /* JADX WARN: Code restructure failed: missing block: B:255:0x0906, code lost:
        r0.applicationInfo.flags |= 1024;
     */
    /* JADX WARN: Code restructure failed: missing block: B:257:0x0918, code lost:
        if (r22 < 0) goto L130;
     */
    /* JADX WARN: Code restructure failed: missing block: B:259:0x091d, code lost:
        if (r22 <= 0) goto L100;
     */
    /* JADX WARN: Code restructure failed: missing block: B:261:0x0929, code lost:
        if (r0.applicationInfo.targetSdkVersion < 4) goto L100;
     */
    /* JADX WARN: Code restructure failed: missing block: B:262:0x092c, code lost:
        r0.applicationInfo.flags |= 2048;
     */
    /* JADX WARN: Code restructure failed: missing block: B:264:0x093e, code lost:
        if (r23 < 0) goto L129;
     */
    /* JADX WARN: Code restructure failed: missing block: B:266:0x0943, code lost:
        if (r23 <= 0) goto L106;
     */
    /* JADX WARN: Code restructure failed: missing block: B:268:0x0950, code lost:
        if (r0.applicationInfo.targetSdkVersion < 9) goto L106;
     */
    /* JADX WARN: Code restructure failed: missing block: B:269:0x0953, code lost:
        r0.applicationInfo.flags |= 524288;
     */
    /* JADX WARN: Code restructure failed: missing block: B:271:0x0965, code lost:
        if (r24 < 0) goto L128;
     */
    /* JADX WARN: Code restructure failed: missing block: B:273:0x096a, code lost:
        if (r24 <= 0) goto L112;
     */
    /* JADX WARN: Code restructure failed: missing block: B:275:0x0976, code lost:
        if (r0.applicationInfo.targetSdkVersion < 4) goto L112;
     */
    /* JADX WARN: Code restructure failed: missing block: B:276:0x0979, code lost:
        r0.applicationInfo.flags |= 4096;
     */
    /* JADX WARN: Code restructure failed: missing block: B:278:0x098b, code lost:
        if (r25 < 0) goto L127;
     */
    /* JADX WARN: Code restructure failed: missing block: B:280:0x0990, code lost:
        if (r25 <= 0) goto L118;
     */
    /* JADX WARN: Code restructure failed: missing block: B:282:0x099c, code lost:
        if (r0.applicationInfo.targetSdkVersion < 4) goto L118;
     */
    /* JADX WARN: Code restructure failed: missing block: B:283:0x099f, code lost:
        r0.applicationInfo.flags |= 8192;
     */
    /* JADX WARN: Code restructure failed: missing block: B:285:0x09b9, code lost:
        if (r0.applicationInfo.targetSdkVersion >= 18) goto L125;
     */
    /* JADX WARN: Code restructure failed: missing block: B:286:0x09bc, code lost:
        r30 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:288:0x09c9, code lost:
        if (r30 >= r0.requestedPermissionsRequired.size()) goto L124;
     */
    /* JADX WARN: Code restructure failed: missing block: B:289:0x09cc, code lost:
        r0.requestedPermissionsRequired.set(r30, java.lang.Boolean.TRUE);
        r30 = r30 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:291:0x09e2, code lost:
        return r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private android.content.pm.PackageParser.Package parsePackage(android.content.res.Resources r9, android.content.res.XmlResourceParser r10, int r11, java.lang.String[] r12) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 2531
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parsePackage(android.content.res.Resources, android.content.res.XmlResourceParser, int, java.lang.String[]):android.content.pm.PackageParser$Package");
    }

    private boolean parseUsesPermission(Package pkg, Resources res, XmlResourceParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestUsesPermission);
        String name = sa.getNonResourceString(0);
        int maxSdkVersion = 0;
        TypedValue val = sa.peekValue(1);
        if (val != null && val.type >= 16 && val.type <= 31) {
            maxSdkVersion = val.data;
        }
        sa.recycle();
        if ((maxSdkVersion == 0 || maxSdkVersion >= Build.VERSION.RESOURCES_SDK_INT) && name != null) {
            int index = pkg.requestedPermissions.indexOf(name);
            if (index != -1) {
                if (!pkg.requestedPermissionsRequired.get(index).booleanValue()) {
                    outError[0] = "conflicting <uses-permission> entries";
                    this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                    return false;
                }
            } else {
                pkg.requestedPermissions.add(name.intern());
                pkg.requestedPermissionsRequired.add(1 != 0 ? Boolean.TRUE : Boolean.FALSE);
            }
        }
        XmlUtils.skipCurrentTag(parser);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String buildClassName(String pkg, CharSequence clsSeq, String[] outError) {
        if (clsSeq == null || clsSeq.length() <= 0) {
            outError[0] = "Empty class name in package " + pkg;
            return null;
        }
        String cls = clsSeq.toString();
        char c = cls.charAt(0);
        if (c == '.') {
            return (pkg + cls).intern();
        }
        if (cls.indexOf(46) < 0) {
            return (pkg + '.' + cls).intern();
        } else if (c >= 'a' && c <= 'z') {
            return cls.intern();
        } else {
            outError[0] = "Bad class name " + cls + " in package " + pkg;
            return null;
        }
    }

    private static String buildCompoundName(String pkg, CharSequence procSeq, String type, String[] outError) {
        String proc = procSeq.toString();
        char c = proc.charAt(0);
        if (pkg != null && c == ':') {
            if (proc.length() < 2) {
                outError[0] = "Bad " + type + " name " + proc + " in package " + pkg + ": must be at least two characters";
                return null;
            }
            String subName = proc.substring(1);
            String nameError = validateName(subName, false);
            if (nameError != null) {
                outError[0] = "Invalid " + type + " name " + proc + " in package " + pkg + ": " + nameError;
                return null;
            }
            return (pkg + proc).intern();
        }
        String nameError2 = validateName(proc, true);
        if (nameError2 != null && !"system".equals(proc)) {
            outError[0] = "Invalid " + type + " name " + proc + " in package " + pkg + ": " + nameError2;
            return null;
        }
        return proc.intern();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String buildProcessName(String pkg, String defProc, CharSequence procSeq, int flags, String[] separateProcesses, String[] outError) {
        if ((flags & 8) != 0 && !"system".equals(procSeq)) {
            return defProc != null ? defProc : pkg;
        }
        if (separateProcesses != null) {
            for (int i = separateProcesses.length - 1; i >= 0; i--) {
                String sp = separateProcesses[i];
                if (sp.equals(pkg) || sp.equals(defProc) || sp.equals(procSeq)) {
                    return pkg;
                }
            }
        }
        if (procSeq == null || procSeq.length() <= 0) {
            return defProc;
        }
        return buildCompoundName(pkg, procSeq, "process", outError);
    }

    private static String buildTaskAffinityName(String pkg, String defProc, CharSequence procSeq, String[] outError) {
        if (procSeq == null) {
            return defProc;
        }
        if (procSeq.length() <= 0) {
            return null;
        }
        return buildCompoundName(pkg, procSeq, "taskAffinity", outError);
    }

    private boolean parseKeys(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        PublicKey currentKey = null;
        int currentKeyDepth = -1;
        Map<PublicKey, Set<String>> definedKeySets = new HashMap<>();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type == 3) {
                if (parser.getDepth() == currentKeyDepth) {
                    currentKey = null;
                    currentKeyDepth = -1;
                }
            } else {
                String tagname = parser.getName();
                if (tagname.equals("publicKey")) {
                    TypedArray sa = res.obtainAttributes(attrs, R.styleable.PublicKey);
                    String encodedKey = sa.getNonResourceString(0);
                    currentKey = parsePublicKey(encodedKey);
                    if (currentKey == null) {
                        Slog.w(TAG, "No valid key in 'publicKey' tag at " + parser.getPositionDescription());
                        sa.recycle();
                    } else {
                        currentKeyDepth = parser.getDepth();
                        definedKeySets.put(currentKey, new HashSet<>());
                        sa.recycle();
                    }
                } else if (tagname.equals("keyset")) {
                    if (currentKey == null) {
                        Slog.i(TAG, "'keyset' not in 'publicKey' tag at " + parser.getPositionDescription());
                    } else {
                        TypedArray sa2 = res.obtainAttributes(attrs, R.styleable.KeySet);
                        String name = sa2.getNonResourceString(0);
                        definedKeySets.get(currentKey).add(name);
                        sa2.recycle();
                    }
                } else {
                    Slog.w(TAG, "Unknown element under <keys>: " + parser.getName() + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
        owner.mKeySetMapping = new HashMap();
        for (Map.Entry<PublicKey, Set<String>> e : definedKeySets.entrySet()) {
            PublicKey key = e.getKey();
            Set<String> keySetNames = e.getValue();
            for (String alias : keySetNames) {
                if (owner.mKeySetMapping.containsKey(alias)) {
                    owner.mKeySetMapping.get(alias).add(key);
                } else {
                    Set<PublicKey> keys = new HashSet<>();
                    keys.add(key);
                    owner.mKeySetMapping.put(alias, keys);
                }
            }
        }
        return true;
    }

    private PermissionGroup parsePermissionGroup(Package owner, int flags, Resources res, XmlPullParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        PermissionGroup perm = new PermissionGroup(owner);
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestPermissionGroup);
        if (!parsePackageItemInfo(owner, perm.info, outError, "<permission-group>", sa, 2, 0, 1, 5)) {
            sa.recycle();
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        perm.info.descriptionRes = sa.getResourceId(4, 0);
        perm.info.flags = sa.getInt(6, 0);
        perm.info.priority = sa.getInt(3, 0);
        if (perm.info.priority > 0 && (flags & 1) == 0) {
            perm.info.priority = 0;
        }
        sa.recycle();
        if (!parseAllMetaData(res, parser, attrs, "<permission-group>", perm, outError)) {
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        owner.permissionGroups.add(perm);
        return perm;
    }

    private Permission parsePermission(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        Permission perm = new Permission(owner);
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestPermission);
        if (!parsePackageItemInfo(owner, perm.info, outError, "<permission>", sa, 2, 0, 1, 6)) {
            sa.recycle();
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        perm.info.group = sa.getNonResourceString(4);
        if (perm.info.group != null) {
            perm.info.group = perm.info.group.intern();
        }
        perm.info.descriptionRes = sa.getResourceId(5, 0);
        perm.info.protectionLevel = sa.getInt(3, 0);
        perm.info.flags = sa.getInt(7, 0);
        sa.recycle();
        if (perm.info.protectionLevel == -1) {
            outError[0] = "<permission> does not specify protectionLevel";
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        perm.info.protectionLevel = PermissionInfo.fixProtectionLevel(perm.info.protectionLevel);
        if ((perm.info.protectionLevel & 240) != 0 && (perm.info.protectionLevel & 15) != 2) {
            outError[0] = "<permission>  protectionLevel specifies a flag but is not based on signature type";
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        } else if (!parseAllMetaData(res, parser, attrs, "<permission>", perm, outError)) {
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        } else {
            owner.permissions.add(perm);
            return perm;
        }
    }

    private Permission parsePermissionTree(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        Permission perm = new Permission(owner);
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestPermissionTree);
        if (!parsePackageItemInfo(owner, perm.info, outError, "<permission-tree>", sa, 2, 0, 1, 3)) {
            sa.recycle();
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        sa.recycle();
        int index = perm.info.name.indexOf(46);
        if (index > 0) {
            index = perm.info.name.indexOf(46, index + 1);
        }
        if (index < 0) {
            outError[0] = "<permission-tree> name has less than three segments: " + perm.info.name;
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        perm.info.descriptionRes = 0;
        perm.info.protectionLevel = 0;
        perm.tree = true;
        if (!parseAllMetaData(res, parser, attrs, "<permission-tree>", perm, outError)) {
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        owner.permissions.add(perm);
        return perm;
    }

    private Instrumentation parseInstrumentation(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestInstrumentation);
        if (this.mParseInstrumentationArgs == null) {
            this.mParseInstrumentationArgs = new ParsePackageItemArgs(owner, outError, 2, 0, 1, 6);
            this.mParseInstrumentationArgs.tag = "<instrumentation>";
        }
        this.mParseInstrumentationArgs.sa = sa;
        Instrumentation a = new Instrumentation(this.mParseInstrumentationArgs, new InstrumentationInfo());
        if (outError[0] != null) {
            sa.recycle();
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        String str = sa.getNonResourceString(3);
        a.info.targetPackage = str != null ? str.intern() : null;
        a.info.handleProfiling = sa.getBoolean(4, false);
        a.info.functionalTest = sa.getBoolean(5, false);
        sa.recycle();
        if (a.info.targetPackage == null) {
            outError[0] = "<instrumentation> does not specify targetPackage";
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        } else if (!parseAllMetaData(res, parser, attrs, "<instrumentation>", a, outError)) {
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        } else {
            owner.instrumentation.add(a);
            return a;
        }
    }

    private boolean parseApplication(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, int flags, String[] outError) throws XmlPullParserException, IOException {
        String str;
        CharSequence pname;
        ApplicationInfo ai = owner.applicationInfo;
        String pkgName = owner.applicationInfo.packageName;
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestApplication);
        String name = sa.getNonConfigurationString(3, 0);
        if (name != null) {
            ai.className = buildClassName(pkgName, name, outError);
            if (ai.className == null) {
                sa.recycle();
                this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                return false;
            }
        }
        String manageSpaceActivity = sa.getNonConfigurationString(4, 1024);
        if (manageSpaceActivity != null) {
            ai.manageSpaceActivityName = buildClassName(pkgName, manageSpaceActivity, outError);
        }
        boolean allowBackup = sa.getBoolean(17, true);
        if (allowBackup) {
            ai.flags |= 32768;
            String backupAgent = sa.getNonConfigurationString(16, 1024);
            if (backupAgent != null) {
                ai.backupAgentName = buildClassName(pkgName, backupAgent, outError);
                if (sa.getBoolean(18, true)) {
                    ai.flags |= 65536;
                }
                if (sa.getBoolean(21, false)) {
                    ai.flags |= 131072;
                }
            }
        }
        TypedValue v = sa.peekValue(1);
        if (v != null) {
            int i = v.resourceId;
            ai.labelRes = i;
            if (i == 0) {
                ai.nonLocalizedLabel = v.coerceToString();
            }
        }
        ai.icon = sa.getResourceId(2, 0);
        ai.logo = sa.getResourceId(22, 0);
        ai.theme = sa.getResourceId(0, 0);
        ai.descriptionRes = sa.getResourceId(13, 0);
        if ((flags & 1) != 0 && sa.getBoolean(8, false)) {
            ai.flags |= 8;
        }
        if (sa.getBoolean(27, false)) {
            owner.mRequiredForAllUsers = true;
        }
        String restrictedAccountType = sa.getString(28);
        if (restrictedAccountType != null && restrictedAccountType.length() > 0) {
            owner.mRestrictedAccountType = restrictedAccountType;
        }
        String requiredAccountType = sa.getString(29);
        if (requiredAccountType != null && requiredAccountType.length() > 0) {
            owner.mRequiredAccountType = requiredAccountType;
        }
        if (sa.getBoolean(10, false)) {
            ai.flags |= 2;
        }
        if (sa.getBoolean(20, false)) {
            ai.flags |= 16384;
        }
        boolean hardwareAccelerated = sa.getBoolean(23, owner.applicationInfo.targetSdkVersion >= 14);
        if (sa.getBoolean(7, true)) {
            ai.flags |= 4;
        }
        if (sa.getBoolean(14, false)) {
            ai.flags |= 32;
        }
        if (sa.getBoolean(5, true)) {
            ai.flags |= 64;
        }
        if (sa.getBoolean(15, false)) {
            ai.flags |= 256;
        }
        if (sa.getBoolean(24, false)) {
            ai.flags |= 1048576;
        }
        if (sa.getBoolean(26, false)) {
            ai.flags |= 4194304;
        }
        String str2 = sa.getNonConfigurationString(6, 0);
        ai.permission = (str2 == null || str2.length() <= 0) ? null : str2.intern();
        if (owner.applicationInfo.targetSdkVersion >= 8) {
            str = sa.getNonConfigurationString(12, 1024);
        } else {
            str = sa.getNonResourceString(12);
        }
        ai.taskAffinity = buildTaskAffinityName(ai.packageName, ai.packageName, str, outError);
        if (outError[0] == null) {
            if (owner.applicationInfo.targetSdkVersion >= 8) {
                pname = sa.getNonConfigurationString(11, 1024);
            } else {
                pname = sa.getNonResourceString(11);
            }
            ai.processName = buildProcessName(ai.packageName, null, pname, flags, this.mSeparateProcesses, outError);
            ai.enabled = sa.getBoolean(9, true);
        }
        ai.uiOptions = sa.getInt(25, 0);
        sa.recycle();
        if (outError[0] != null) {
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return false;
        }
        int innerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return true;
            }
            if (type != 3 || parser.getDepth() > innerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals(Context.ACTIVITY_SERVICE)) {
                        Activity a = parseActivity(owner, res, parser, attrs, flags, outError, false, hardwareAccelerated);
                        if (a == null) {
                            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                            return false;
                        }
                        owner.activities.add(a);
                    } else if (tagName.equals("receiver")) {
                        Activity a2 = parseActivity(owner, res, parser, attrs, flags, outError, true, false);
                        if (a2 == null) {
                            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                            return false;
                        }
                        owner.receivers.add(a2);
                    } else if (tagName.equals("service")) {
                        Service s = parseService(owner, res, parser, attrs, flags, outError);
                        if (s == null) {
                            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                            return false;
                        }
                        owner.services.add(s);
                    } else if (tagName.equals("provider")) {
                        Provider p = parseProvider(owner, res, parser, attrs, flags, outError);
                        if (p == null) {
                            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                            return false;
                        }
                        owner.providers.add(p);
                    } else if (tagName.equals("activity-alias")) {
                        Activity a3 = parseActivityAlias(owner, res, parser, attrs, flags, outError);
                        if (a3 == null) {
                            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                            return false;
                        }
                        owner.activities.add(a3);
                    } else if (parser.getName().equals("meta-data")) {
                        Bundle parseMetaData = parseMetaData(res, parser, attrs, owner.mAppMetaData, outError);
                        owner.mAppMetaData = parseMetaData;
                        if (parseMetaData == null) {
                            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                            return false;
                        }
                    } else if (tagName.equals("library")) {
                        TypedArray sa2 = res.obtainAttributes(attrs, R.styleable.AndroidManifestLibrary);
                        String lname = sa2.getNonResourceString(0);
                        sa2.recycle();
                        if (lname != null) {
                            if (owner.libraryNames == null) {
                                owner.libraryNames = new ArrayList<>();
                            }
                            if (!owner.libraryNames.contains(lname)) {
                                owner.libraryNames.add(lname.intern());
                            }
                        }
                        XmlUtils.skipCurrentTag(parser);
                    } else if (tagName.equals("uses-library")) {
                        TypedArray sa3 = res.obtainAttributes(attrs, R.styleable.AndroidManifestUsesLibrary);
                        String lname2 = sa3.getNonResourceString(0);
                        boolean req = sa3.getBoolean(1, true);
                        sa3.recycle();
                        if (lname2 != null) {
                            if (req) {
                                if (owner.usesLibraries == null) {
                                    owner.usesLibraries = new ArrayList<>();
                                }
                                if (!owner.usesLibraries.contains(lname2)) {
                                    owner.usesLibraries.add(lname2.intern());
                                }
                            } else {
                                if (owner.usesOptionalLibraries == null) {
                                    owner.usesOptionalLibraries = new ArrayList<>();
                                }
                                if (!owner.usesOptionalLibraries.contains(lname2)) {
                                    owner.usesOptionalLibraries.add(lname2.intern());
                                }
                            }
                        }
                        XmlUtils.skipCurrentTag(parser);
                    } else if (tagName.equals("uses-package")) {
                        XmlUtils.skipCurrentTag(parser);
                    } else {
                        Slog.w(TAG, "Unknown element under <application>: " + tagName + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            } else {
                return true;
            }
        }
    }

    private boolean parsePackageItemInfo(Package owner, PackageItemInfo outInfo, String[] outError, String tag, TypedArray sa, int nameRes, int labelRes, int iconRes, int logoRes) {
        String name = sa.getNonConfigurationString(nameRes, 0);
        if (name == null) {
            outError[0] = tag + " does not specify android:name";
            return false;
        }
        outInfo.name = buildClassName(owner.applicationInfo.packageName, name, outError);
        if (outInfo.name == null) {
            return false;
        }
        int iconVal = sa.getResourceId(iconRes, 0);
        if (iconVal != 0) {
            outInfo.icon = iconVal;
            outInfo.nonLocalizedLabel = null;
        }
        int logoVal = sa.getResourceId(logoRes, 0);
        if (logoVal != 0) {
            outInfo.logo = logoVal;
        }
        TypedValue v = sa.peekValue(labelRes);
        if (v != null) {
            int i = v.resourceId;
            outInfo.labelRes = i;
            if (i == 0) {
                outInfo.nonLocalizedLabel = v.coerceToString();
            }
        }
        outInfo.packageName = owner.packageName;
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:147:0x05fb, code lost:
        if (r25 != false) goto L102;
     */
    /* JADX WARN: Code restructure failed: missing block: B:148:0x05fe, code lost:
        r0 = r0.info;
     */
    /* JADX WARN: Code restructure failed: missing block: B:149:0x060b, code lost:
        if (r0.intents.size() <= 0) goto L101;
     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x060e, code lost:
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:151:0x0612, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:152:0x0613, code lost:
        r0.exported = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:154:0x0618, code lost:
        return r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private android.content.pm.PackageParser.Activity parseActivity(android.content.pm.PackageParser.Package r15, android.content.res.Resources r16, org.xmlpull.v1.XmlPullParser r17, android.util.AttributeSet r18, int r19, java.lang.String[] r20, boolean r21, boolean r22) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 1561
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseActivity(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[], boolean, boolean):android.content.pm.PackageParser$Activity");
    }

    /* JADX WARN: Code restructure failed: missing block: B:82:0x03b6, code lost:
        if (r0 != false) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x03b9, code lost:
        r0 = r0.info;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x03c6, code lost:
        if (r0.intents.size() <= 0) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x03c9, code lost:
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x03cd, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x03ce, code lost:
        r0.exported = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x03d3, code lost:
        return r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private android.content.pm.PackageParser.Activity parseActivityAlias(android.content.pm.PackageParser.Package r15, android.content.res.Resources r16, org.xmlpull.v1.XmlPullParser r17, android.util.AttributeSet r18, int r19, java.lang.String[] r20) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 980
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseActivityAlias(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[]):android.content.pm.PackageParser$Activity");
    }

    private Provider parseProvider(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, int flags, String[] outError) throws XmlPullParserException, IOException {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestProvider);
        if (this.mParseProviderArgs == null) {
            this.mParseProviderArgs = new ParseComponentArgs(owner, outError, 2, 0, 1, 15, this.mSeparateProcesses, 8, 14, 6);
            this.mParseProviderArgs.tag = "<provider>";
        }
        this.mParseProviderArgs.sa = sa;
        this.mParseProviderArgs.flags = flags;
        Provider p = new Provider(this.mParseProviderArgs, new ProviderInfo());
        if (outError[0] != null) {
            sa.recycle();
            return null;
        }
        boolean providerExportedDefault = false;
        if (owner.applicationInfo.targetSdkVersion < 17) {
            providerExportedDefault = true;
        }
        p.info.exported = sa.getBoolean(7, providerExportedDefault);
        String cpname = sa.getNonConfigurationString(10, 0);
        p.info.isSyncable = sa.getBoolean(11, false);
        String permission = sa.getNonConfigurationString(3, 0);
        String str = sa.getNonConfigurationString(4, 0);
        if (str == null) {
            str = permission;
        }
        if (str == null) {
            p.info.readPermission = owner.applicationInfo.permission;
        } else {
            p.info.readPermission = str.length() > 0 ? str.toString().intern() : null;
        }
        String str2 = sa.getNonConfigurationString(5, 0);
        if (str2 == null) {
            str2 = permission;
        }
        if (str2 == null) {
            p.info.writePermission = owner.applicationInfo.permission;
        } else {
            p.info.writePermission = str2.length() > 0 ? str2.toString().intern() : null;
        }
        p.info.grantUriPermissions = sa.getBoolean(13, false);
        p.info.multiprocess = sa.getBoolean(9, false);
        p.info.initOrder = sa.getInt(12, 0);
        p.info.flags = 0;
        if (sa.getBoolean(16, false)) {
            p.info.flags |= 1073741824;
            if (p.info.exported) {
                Slog.w(TAG, "Provider exported request ignored due to singleUser: " + p.className + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                p.info.exported = false;
            }
        }
        sa.recycle();
        if ((owner.applicationInfo.flags & 268435456) != 0 && p.info.processName == owner.packageName) {
            outError[0] = "Heavy-weight applications can not have providers in main process";
            return null;
        } else if (cpname == null) {
            outError[0] = "<provider> does not include authorities attribute";
            return null;
        } else {
            p.info.authority = cpname.intern();
            if (!parseProviderTags(res, parser, attrs, p, outError)) {
                return null;
            }
            return p;
        }
    }

    private boolean parseProviderTags(Resources res, XmlPullParser parser, AttributeSet attrs, Provider outInfo, String[] outError) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return true;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    if (parser.getName().equals("intent-filter")) {
                        ProviderIntentInfo intent = new ProviderIntentInfo(outInfo);
                        if (!parseIntent(res, parser, attrs, true, intent, outError)) {
                            return false;
                        }
                        outInfo.intents.add(intent);
                    } else if (parser.getName().equals("meta-data")) {
                        Bundle parseMetaData = parseMetaData(res, parser, attrs, outInfo.metaData, outError);
                        outInfo.metaData = parseMetaData;
                        if (parseMetaData == null) {
                            return false;
                        }
                    } else if (parser.getName().equals("grant-uri-permission")) {
                        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestGrantUriPermission);
                        PatternMatcher pa = null;
                        String str = sa.getNonConfigurationString(0, 0);
                        if (str != null) {
                            pa = new PatternMatcher(str, 0);
                        }
                        String str2 = sa.getNonConfigurationString(1, 0);
                        if (str2 != null) {
                            pa = new PatternMatcher(str2, 1);
                        }
                        String str3 = sa.getNonConfigurationString(2, 0);
                        if (str3 != null) {
                            pa = new PatternMatcher(str3, 2);
                        }
                        sa.recycle();
                        if (pa != null) {
                            if (outInfo.info.uriPermissionPatterns == null) {
                                outInfo.info.uriPermissionPatterns = new PatternMatcher[1];
                                outInfo.info.uriPermissionPatterns[0] = pa;
                            } else {
                                int N = outInfo.info.uriPermissionPatterns.length;
                                PatternMatcher[] newp = new PatternMatcher[N + 1];
                                System.arraycopy(outInfo.info.uriPermissionPatterns, 0, newp, 0, N);
                                newp[N] = pa;
                                outInfo.info.uriPermissionPatterns = newp;
                            }
                            outInfo.info.grantUriPermissions = true;
                            XmlUtils.skipCurrentTag(parser);
                        } else {
                            Slog.w(TAG, "Unknown element under <path-permission>: " + parser.getName() + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                            XmlUtils.skipCurrentTag(parser);
                        }
                    } else if (parser.getName().equals("path-permission")) {
                        TypedArray sa2 = res.obtainAttributes(attrs, R.styleable.AndroidManifestPathPermission);
                        PathPermission pa2 = null;
                        String permission = sa2.getNonConfigurationString(0, 0);
                        String readPermission = sa2.getNonConfigurationString(1, 0);
                        if (readPermission == null) {
                            readPermission = permission;
                        }
                        String writePermission = sa2.getNonConfigurationString(2, 0);
                        if (writePermission == null) {
                            writePermission = permission;
                        }
                        boolean havePerm = false;
                        if (readPermission != null) {
                            readPermission = readPermission.intern();
                            havePerm = true;
                        }
                        if (writePermission != null) {
                            writePermission = writePermission.intern();
                            havePerm = true;
                        }
                        if (!havePerm) {
                            Slog.w(TAG, "No readPermission or writePermssion for <path-permission>: " + parser.getName() + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                            XmlUtils.skipCurrentTag(parser);
                        } else {
                            String path = sa2.getNonConfigurationString(3, 0);
                            if (path != null) {
                                pa2 = new PathPermission(path, 0, readPermission, writePermission);
                            }
                            String path2 = sa2.getNonConfigurationString(4, 0);
                            if (path2 != null) {
                                pa2 = new PathPermission(path2, 1, readPermission, writePermission);
                            }
                            String path3 = sa2.getNonConfigurationString(5, 0);
                            if (path3 != null) {
                                pa2 = new PathPermission(path3, 2, readPermission, writePermission);
                            }
                            sa2.recycle();
                            if (pa2 != null) {
                                if (outInfo.info.pathPermissions == null) {
                                    outInfo.info.pathPermissions = new PathPermission[1];
                                    outInfo.info.pathPermissions[0] = pa2;
                                } else {
                                    int N2 = outInfo.info.pathPermissions.length;
                                    PathPermission[] newp2 = new PathPermission[N2 + 1];
                                    System.arraycopy(outInfo.info.pathPermissions, 0, newp2, 0, N2);
                                    newp2[N2] = pa2;
                                    outInfo.info.pathPermissions = newp2;
                                }
                                XmlUtils.skipCurrentTag(parser);
                            } else {
                                Slog.w(TAG, "No path, pathPrefix, or pathPattern for <path-permission>: " + parser.getName() + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                                XmlUtils.skipCurrentTag(parser);
                            }
                        }
                    } else {
                        Slog.w(TAG, "Unknown element under <provider>: " + parser.getName() + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            } else {
                return true;
            }
        }
    }

    private Service parseService(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, int flags, String[] outError) throws XmlPullParserException, IOException {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestService);
        if (this.mParseServiceArgs == null) {
            this.mParseServiceArgs = new ParseComponentArgs(owner, outError, 2, 0, 1, 8, this.mSeparateProcesses, 6, 7, 4);
            this.mParseServiceArgs.tag = "<service>";
        }
        this.mParseServiceArgs.sa = sa;
        this.mParseServiceArgs.flags = flags;
        Service s = new Service(this.mParseServiceArgs, new ServiceInfo());
        if (outError[0] != null) {
            sa.recycle();
            return null;
        }
        boolean setExported = sa.hasValue(5);
        if (setExported) {
            s.info.exported = sa.getBoolean(5, false);
        }
        String str = sa.getNonConfigurationString(3, 0);
        if (str == null) {
            s.info.permission = owner.applicationInfo.permission;
        } else {
            s.info.permission = str.length() > 0 ? str.toString().intern() : null;
        }
        s.info.flags = 0;
        if (sa.getBoolean(9, false)) {
            s.info.flags |= 1;
        }
        if (sa.getBoolean(10, false)) {
            s.info.flags |= 2;
        }
        if (sa.getBoolean(11, false)) {
            s.info.flags |= 1073741824;
            if (s.info.exported) {
                Slog.w(TAG, "Service exported request ignored due to singleUser: " + s.className + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                s.info.exported = false;
            }
            setExported = true;
        }
        sa.recycle();
        if ((owner.applicationInfo.flags & 268435456) != 0 && s.info.processName == owner.packageName) {
            outError[0] = "Heavy-weight applications can not have services in main process";
            return null;
        }
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type != 3 && type != 4) {
                if (parser.getName().equals("intent-filter")) {
                    ServiceIntentInfo intent = new ServiceIntentInfo(s);
                    if (!parseIntent(res, parser, attrs, true, intent, outError)) {
                        return null;
                    }
                    s.intents.add(intent);
                } else if (parser.getName().equals("meta-data")) {
                    Bundle parseMetaData = parseMetaData(res, parser, attrs, s.metaData, outError);
                    s.metaData = parseMetaData;
                    if (parseMetaData == null) {
                        return null;
                    }
                } else {
                    Slog.w(TAG, "Unknown element under <service>: " + parser.getName() + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
        if (!setExported) {
            s.info.exported = s.intents.size() > 0;
        }
        return s;
    }

    private boolean parseAllMetaData(Resources res, XmlPullParser parser, AttributeSet attrs, String tag, Component outInfo, String[] outError) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return true;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    if (parser.getName().equals("meta-data")) {
                        Bundle parseMetaData = parseMetaData(res, parser, attrs, outInfo.metaData, outError);
                        outInfo.metaData = parseMetaData;
                        if (parseMetaData == null) {
                            return false;
                        }
                    } else {
                        Slog.w(TAG, "Unknown element under " + tag + ": " + parser.getName() + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            } else {
                return true;
            }
        }
    }

    private Bundle parseMetaData(Resources res, XmlPullParser parser, AttributeSet attrs, Bundle data, String[] outError) throws XmlPullParserException, IOException {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestMetaData);
        if (data == null) {
            data = new Bundle();
        }
        String name = sa.getNonConfigurationString(0, 0);
        if (name == null) {
            outError[0] = "<meta-data> requires an android:name attribute";
            sa.recycle();
            return null;
        }
        String name2 = name.intern();
        TypedValue v = sa.peekValue(2);
        if (v != null && v.resourceId != 0) {
            data.putInt(name2, v.resourceId);
        } else {
            TypedValue v2 = sa.peekValue(1);
            if (v2 != null) {
                if (v2.type == 3) {
                    CharSequence cs = v2.coerceToString();
                    data.putString(name2, cs != null ? cs.toString().intern() : null);
                } else if (v2.type == 18) {
                    data.putBoolean(name2, v2.data != 0);
                } else if (v2.type >= 16 && v2.type <= 31) {
                    data.putInt(name2, v2.data);
                } else if (v2.type == 4) {
                    data.putFloat(name2, v2.getFloat());
                } else {
                    Slog.w(TAG, "<meta-data> only supports string, integer, float, color, boolean, and resource reference types: " + parser.getName() + " at " + this.mArchiveSourcePath + Separators.SP + parser.getPositionDescription());
                }
            } else {
                outError[0] = "<meta-data> requires an android:value or android:resource attribute";
                data = null;
            }
        }
        sa.recycle();
        XmlUtils.skipCurrentTag(parser);
        return data;
    }

    private static VerifierInfo parseVerifier(Resources res, XmlPullParser parser, AttributeSet attrs, int flags, String[] outError) throws XmlPullParserException, IOException {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestPackageVerifier);
        String packageName = sa.getNonResourceString(0);
        String encodedPublicKey = sa.getNonResourceString(1);
        sa.recycle();
        if (packageName == null || packageName.length() == 0) {
            Slog.i(TAG, "verifier package name was null; skipping");
            return null;
        }
        if (encodedPublicKey == null) {
            Slog.i(TAG, "verifier " + packageName + " public key was null; skipping");
        }
        PublicKey publicKey = parsePublicKey(encodedPublicKey);
        if (publicKey != null) {
            return new VerifierInfo(packageName, publicKey);
        }
        return null;
    }

    public static final PublicKey parsePublicKey(String encodedPublicKey) {
        try {
            byte[] encoded = Base64.decode(encodedPublicKey, 0);
            EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(keySpec);
            } catch (NoSuchAlgorithmException e) {
                Log.wtf(TAG, "Could not parse public key because RSA isn't included in build");
                return null;
            } catch (InvalidKeySpecException e2) {
                try {
                    KeyFactory keyFactory2 = KeyFactory.getInstance("DSA");
                    return keyFactory2.generatePublic(keySpec);
                } catch (NoSuchAlgorithmException e3) {
                    Log.wtf(TAG, "Could not parse public key because DSA isn't included in build");
                    return null;
                } catch (InvalidKeySpecException e4) {
                    return null;
                }
            }
        } catch (IllegalArgumentException e5) {
            Slog.i(TAG, "Could not parse verifier public key; invalid Base64");
            return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x00bf, code lost:
        r11[0] = "No value supplied for <android:name>";
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x00c7, code lost:
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00fc, code lost:
        r11[0] = "No value supplied for <android:name>";
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0104, code lost:
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean parseIntent(android.content.res.Resources r6, org.xmlpull.v1.XmlPullParser r7, android.util.AttributeSet r8, boolean r9, android.content.pm.PackageParser.IntentInfo r10, java.lang.String[] r11) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 648
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseIntent(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, boolean, android.content.pm.PackageParser$IntentInfo, java.lang.String[]):boolean");
    }

    /* loaded from: PackageParser$Package.class */
    public static final class Package {
        public String packageName;
        public ArrayList<String> protectedBroadcasts;
        public String mPath;
        public int mVersionCode;
        public String mVersionName;
        public String mSharedUserId;
        public int mSharedUserLabel;
        public Signature[] mSignatures;
        public String mScanPath;
        public boolean mDidDexOpt;
        public Object mExtras;
        public boolean mOperationPending;
        public int installLocation;
        public boolean mRequiredForAllUsers;
        public String mRestrictedAccountType;
        public String mRequiredAccountType;
        public ManifestDigest manifestDigest;
        public Set<PublicKey> mSigningKeys;
        public Map<String, Set<PublicKey>> mKeySetMapping;
        public final ApplicationInfo applicationInfo = new ApplicationInfo();
        public final ArrayList<Permission> permissions = new ArrayList<>(0);
        public final ArrayList<PermissionGroup> permissionGroups = new ArrayList<>(0);
        public final ArrayList<Activity> activities = new ArrayList<>(0);
        public final ArrayList<Activity> receivers = new ArrayList<>(0);
        public final ArrayList<Provider> providers = new ArrayList<>(0);
        public final ArrayList<Service> services = new ArrayList<>(0);
        public final ArrayList<Instrumentation> instrumentation = new ArrayList<>(0);
        public final ArrayList<String> requestedPermissions = new ArrayList<>();
        public final ArrayList<Boolean> requestedPermissionsRequired = new ArrayList<>();
        public ArrayList<String> libraryNames = null;
        public ArrayList<String> usesLibraries = null;
        public ArrayList<String> usesOptionalLibraries = null;
        public String[] usesLibraryFiles = null;
        public ArrayList<ActivityIntentInfo> preferredActivityFilters = null;
        public ArrayList<String> mOriginalPackages = null;
        public String mRealPackage = null;
        public ArrayList<String> mAdoptPermissions = null;
        public Bundle mAppMetaData = null;
        public int mPreferredOrder = 0;
        public final ArrayList<ConfigurationInfo> configPreferences = new ArrayList<>();
        public ArrayList<FeatureInfo> reqFeatures = null;

        public Package(String _name) {
            this.packageName = _name;
            this.applicationInfo.packageName = _name;
            this.applicationInfo.uid = -1;
        }

        public void setPackageName(String newName) {
            this.packageName = newName;
            this.applicationInfo.packageName = newName;
            for (int i = this.permissions.size() - 1; i >= 0; i--) {
                this.permissions.get(i).setPackageName(newName);
            }
            for (int i2 = this.permissionGroups.size() - 1; i2 >= 0; i2--) {
                this.permissionGroups.get(i2).setPackageName(newName);
            }
            for (int i3 = this.activities.size() - 1; i3 >= 0; i3--) {
                this.activities.get(i3).setPackageName(newName);
            }
            for (int i4 = this.receivers.size() - 1; i4 >= 0; i4--) {
                this.receivers.get(i4).setPackageName(newName);
            }
            for (int i5 = this.providers.size() - 1; i5 >= 0; i5--) {
                this.providers.get(i5).setPackageName(newName);
            }
            for (int i6 = this.services.size() - 1; i6 >= 0; i6--) {
                this.services.get(i6).setPackageName(newName);
            }
            for (int i7 = this.instrumentation.size() - 1; i7 >= 0; i7--) {
                this.instrumentation.get(i7).setPackageName(newName);
            }
        }

        public boolean hasComponentClassName(String name) {
            for (int i = this.activities.size() - 1; i >= 0; i--) {
                if (name.equals(this.activities.get(i).className)) {
                    return true;
                }
            }
            for (int i2 = this.receivers.size() - 1; i2 >= 0; i2--) {
                if (name.equals(this.receivers.get(i2).className)) {
                    return true;
                }
            }
            for (int i3 = this.providers.size() - 1; i3 >= 0; i3--) {
                if (name.equals(this.providers.get(i3).className)) {
                    return true;
                }
            }
            for (int i4 = this.services.size() - 1; i4 >= 0; i4--) {
                if (name.equals(this.services.get(i4).className)) {
                    return true;
                }
            }
            for (int i5 = this.instrumentation.size() - 1; i5 >= 0; i5--) {
                if (name.equals(this.instrumentation.get(i5).className)) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            return "Package{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.packageName + "}";
        }
    }

    /* loaded from: PackageParser$Component.class */
    public static class Component<II extends IntentInfo> {
        public final Package owner;
        public final ArrayList<II> intents;
        public final String className;
        public Bundle metaData;
        ComponentName componentName;
        String componentShortName;

        public Component(Package _owner) {
            this.owner = _owner;
            this.intents = null;
            this.className = null;
        }

        public Component(ParsePackageItemArgs args, PackageItemInfo outInfo) {
            this.owner = args.owner;
            this.intents = new ArrayList<>(0);
            String name = args.sa.getNonConfigurationString(args.nameRes, 0);
            if (name == null) {
                this.className = null;
                args.outError[0] = args.tag + " does not specify android:name";
                return;
            }
            outInfo.name = PackageParser.buildClassName(this.owner.applicationInfo.packageName, name, args.outError);
            if (outInfo.name == null) {
                this.className = null;
                args.outError[0] = args.tag + " does not have valid android:name";
                return;
            }
            this.className = outInfo.name;
            int iconVal = args.sa.getResourceId(args.iconRes, 0);
            if (iconVal != 0) {
                outInfo.icon = iconVal;
                outInfo.nonLocalizedLabel = null;
            }
            int logoVal = args.sa.getResourceId(args.logoRes, 0);
            if (logoVal != 0) {
                outInfo.logo = logoVal;
            }
            TypedValue v = args.sa.peekValue(args.labelRes);
            if (v != null) {
                int i = v.resourceId;
                outInfo.labelRes = i;
                if (i == 0) {
                    outInfo.nonLocalizedLabel = v.coerceToString();
                }
            }
            outInfo.packageName = this.owner.packageName;
        }

        public Component(ParseComponentArgs args, ComponentInfo outInfo) {
            this((ParsePackageItemArgs) args, (PackageItemInfo) outInfo);
            CharSequence pname;
            if (args.outError[0] != null) {
                return;
            }
            if (args.processRes != 0) {
                if (this.owner.applicationInfo.targetSdkVersion >= 8) {
                    pname = args.sa.getNonConfigurationString(args.processRes, 1024);
                } else {
                    pname = args.sa.getNonResourceString(args.processRes);
                }
                outInfo.processName = PackageParser.buildProcessName(this.owner.applicationInfo.packageName, this.owner.applicationInfo.processName, pname, args.flags, args.sepProcesses, args.outError);
            }
            if (args.descriptionRes != 0) {
                outInfo.descriptionRes = args.sa.getResourceId(args.descriptionRes, 0);
            }
            outInfo.enabled = args.sa.getBoolean(args.enabledRes, true);
        }

        public Component(Component<II> clone) {
            this.owner = clone.owner;
            this.intents = clone.intents;
            this.className = clone.className;
            this.componentName = clone.componentName;
            this.componentShortName = clone.componentShortName;
        }

        public ComponentName getComponentName() {
            if (this.componentName != null) {
                return this.componentName;
            }
            if (this.className != null) {
                this.componentName = new ComponentName(this.owner.applicationInfo.packageName, this.className);
            }
            return this.componentName;
        }

        public void appendComponentShortName(StringBuilder sb) {
            ComponentName.appendShortString(sb, this.owner.applicationInfo.packageName, this.className);
        }

        public void printComponentShortName(PrintWriter pw) {
            ComponentName.printShortString(pw, this.owner.applicationInfo.packageName, this.className);
        }

        public void setPackageName(String packageName) {
            this.componentName = null;
            this.componentShortName = null;
        }
    }

    /* loaded from: PackageParser$Permission.class */
    public static final class Permission extends Component<IntentInfo> {
        public final PermissionInfo info;
        public boolean tree;
        public PermissionGroup group;

        public Permission(Package _owner) {
            super(_owner);
            this.info = new PermissionInfo();
        }

        public Permission(Package _owner, PermissionInfo _info) {
            super(_owner);
            this.info = _info;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            return "Permission{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.info.name + "}";
        }
    }

    /* loaded from: PackageParser$PermissionGroup.class */
    public static final class PermissionGroup extends Component<IntentInfo> {
        public final PermissionGroupInfo info;

        public PermissionGroup(Package _owner) {
            super(_owner);
            this.info = new PermissionGroupInfo();
        }

        public PermissionGroup(Package _owner, PermissionGroupInfo _info) {
            super(_owner);
            this.info = _info;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            return "PermissionGroup{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.info.name + "}";
        }
    }

    private static boolean copyNeeded(int flags, Package p, PackageUserState state, Bundle metaData, int userId) {
        if (userId != 0) {
            return true;
        }
        if (state.enabled != 0) {
            boolean enabled = state.enabled == 1;
            if (p.applicationInfo.enabled != enabled) {
                return true;
            }
        }
        if (!state.installed || state.blocked || state.stopped) {
            return true;
        }
        if ((flags & 128) != 0 && (metaData != null || p.mAppMetaData != null)) {
            return true;
        }
        if ((flags & 1024) != 0 && p.usesLibraryFiles != null) {
            return true;
        }
        return false;
    }

    public static ApplicationInfo generateApplicationInfo(Package p, int flags, PackageUserState state) {
        return generateApplicationInfo(p, flags, state, UserHandle.getCallingUserId());
    }

    private static void updateApplicationInfo(ApplicationInfo ai, int flags, PackageUserState state) {
        if (!sCompatibilityModeEnabled) {
            ai.disableCompatibilityMode();
        }
        if (state.installed) {
            ai.flags |= 8388608;
        } else {
            ai.flags &= -8388609;
        }
        if (state.blocked) {
            ai.flags |= 134217728;
        } else {
            ai.flags &= -134217729;
        }
        if (state.enabled == 1) {
            ai.enabled = true;
        } else if (state.enabled == 4) {
            ai.enabled = (flags & 32768) != 0;
        } else if (state.enabled == 2 || state.enabled == 3) {
            ai.enabled = false;
        }
        ai.enabledSetting = state.enabled;
    }

    public static ApplicationInfo generateApplicationInfo(Package p, int flags, PackageUserState state, int userId) {
        if (p == null || !checkUseInstalledOrBlocked(flags, state)) {
            return null;
        }
        if (!copyNeeded(flags, p, state, null, userId) && ((flags & 32768) == 0 || state.enabled != 4)) {
            updateApplicationInfo(p.applicationInfo, flags, state);
            return p.applicationInfo;
        }
        ApplicationInfo ai = new ApplicationInfo(p.applicationInfo);
        if (userId != 0) {
            ai.uid = UserHandle.getUid(userId, ai.uid);
            ai.dataDir = PackageManager.getDataDirForUser(userId, ai.packageName);
        }
        if ((flags & 128) != 0) {
            ai.metaData = p.mAppMetaData;
        }
        if ((flags & 1024) != 0) {
            ai.sharedLibraryFiles = p.usesLibraryFiles;
        }
        if (state.stopped) {
            ai.flags |= 2097152;
        } else {
            ai.flags &= -2097153;
        }
        updateApplicationInfo(ai, flags, state);
        return ai;
    }

    public static final PermissionInfo generatePermissionInfo(Permission p, int flags) {
        if (p == null) {
            return null;
        }
        if ((flags & 128) == 0) {
            return p.info;
        }
        PermissionInfo pi = new PermissionInfo(p.info);
        pi.metaData = p.metaData;
        return pi;
    }

    public static final PermissionGroupInfo generatePermissionGroupInfo(PermissionGroup pg, int flags) {
        if (pg == null) {
            return null;
        }
        if ((flags & 128) == 0) {
            return pg.info;
        }
        PermissionGroupInfo pgi = new PermissionGroupInfo(pg.info);
        pgi.metaData = pg.metaData;
        return pgi;
    }

    /* loaded from: PackageParser$Activity.class */
    public static final class Activity extends Component<ActivityIntentInfo> {
        public final ActivityInfo info;

        public Activity(ParseComponentArgs args, ActivityInfo _info) {
            super(args, (ComponentInfo) _info);
            this.info = _info;
            this.info.applicationInfo = args.owner.applicationInfo;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Activity{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final ActivityInfo generateActivityInfo(Activity a, int flags, PackageUserState state, int userId) {
        if (a == null || !checkUseInstalledOrBlocked(flags, state)) {
            return null;
        }
        if (!copyNeeded(flags, a.owner, state, a.metaData, userId)) {
            return a.info;
        }
        ActivityInfo ai = new ActivityInfo(a.info);
        ai.metaData = a.metaData;
        ai.applicationInfo = generateApplicationInfo(a.owner, flags, state, userId);
        return ai;
    }

    /* loaded from: PackageParser$Service.class */
    public static final class Service extends Component<ServiceIntentInfo> {
        public final ServiceInfo info;

        public Service(ParseComponentArgs args, ServiceInfo _info) {
            super(args, (ComponentInfo) _info);
            this.info = _info;
            this.info.applicationInfo = args.owner.applicationInfo;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Service{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final ServiceInfo generateServiceInfo(Service s, int flags, PackageUserState state, int userId) {
        if (s == null || !checkUseInstalledOrBlocked(flags, state)) {
            return null;
        }
        if (!copyNeeded(flags, s.owner, state, s.metaData, userId)) {
            return s.info;
        }
        ServiceInfo si = new ServiceInfo(s.info);
        si.metaData = s.metaData;
        si.applicationInfo = generateApplicationInfo(s.owner, flags, state, userId);
        return si;
    }

    /* loaded from: PackageParser$Provider.class */
    public static final class Provider extends Component<ProviderIntentInfo> {
        public final ProviderInfo info;
        public boolean syncable;

        public Provider(ParseComponentArgs args, ProviderInfo _info) {
            super(args, (ComponentInfo) _info);
            this.info = _info;
            this.info.applicationInfo = args.owner.applicationInfo;
            this.syncable = false;
        }

        public Provider(Provider existingProvider) {
            super(existingProvider);
            this.info = existingProvider.info;
            this.syncable = existingProvider.syncable;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Provider{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final ProviderInfo generateProviderInfo(Provider p, int flags, PackageUserState state, int userId) {
        if (p == null || !checkUseInstalledOrBlocked(flags, state)) {
            return null;
        }
        if (!copyNeeded(flags, p.owner, state, p.metaData, userId) && ((flags & 2048) != 0 || p.info.uriPermissionPatterns == null)) {
            return p.info;
        }
        ProviderInfo pi = new ProviderInfo(p.info);
        pi.metaData = p.metaData;
        if ((flags & 2048) == 0) {
            pi.uriPermissionPatterns = null;
        }
        pi.applicationInfo = generateApplicationInfo(p.owner, flags, state, userId);
        return pi;
    }

    /* loaded from: PackageParser$Instrumentation.class */
    public static final class Instrumentation extends Component {
        public final InstrumentationInfo info;

        public Instrumentation(ParsePackageItemArgs args, InstrumentationInfo _info) {
            super(args, _info);
            this.info = _info;
        }

        @Override // android.content.pm.PackageParser.Component
        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Instrumentation{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final InstrumentationInfo generateInstrumentationInfo(Instrumentation i, int flags) {
        if (i == null) {
            return null;
        }
        if ((flags & 128) == 0) {
            return i.info;
        }
        InstrumentationInfo ii = new InstrumentationInfo(i.info);
        ii.metaData = i.metaData;
        return ii;
    }

    /* loaded from: PackageParser$ActivityIntentInfo.class */
    public static final class ActivityIntentInfo extends IntentInfo {
        public final Activity activity;

        public ActivityIntentInfo(Activity _activity) {
            this.activity = _activity;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ActivityIntentInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            this.activity.appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    /* loaded from: PackageParser$ServiceIntentInfo.class */
    public static final class ServiceIntentInfo extends IntentInfo {
        public final Service service;

        public ServiceIntentInfo(Service _service) {
            this.service = _service;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ServiceIntentInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            this.service.appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    /* loaded from: PackageParser$ProviderIntentInfo.class */
    public static final class ProviderIntentInfo extends IntentInfo {
        public final Provider provider;

        public ProviderIntentInfo(Provider provider) {
            this.provider = provider;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ProviderIntentInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            this.provider.appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static void setCompatibilityModeEnabled(boolean compatibilityModeEnabled) {
        sCompatibilityModeEnabled = compatibilityModeEnabled;
    }
}