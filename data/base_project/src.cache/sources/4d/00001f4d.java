package com.android.server.pm;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageCleanItem;
import android.content.pm.PackageParser;
import android.content.pm.PackageUserState;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.content.pm.VerifierDeviceIdentity;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.FileUtils;
import android.os.PatternMatcher;
import android.os.UserHandle;
import android.provider.CallLog;
import android.provider.Telephony;
import android.security.KeyChain;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.XmlUtils;
import com.android.server.pm.PackageManagerService;
import gov.nist.core.Separators;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: Settings.class */
public final class Settings {
    private static final String TAG = "PackageSettings";
    private static final boolean DEBUG_STOPPED = false;
    private static final boolean DEBUG_MU = false;
    private static final String TAG_READ_EXTERNAL_STORAGE = "read-external-storage";
    private static final String ATTR_ENFORCEMENT = "enforcement";
    private static final String TAG_ITEM = "item";
    private static final String TAG_DISABLED_COMPONENTS = "disabled-components";
    private static final String TAG_ENABLED_COMPONENTS = "enabled-components";
    private static final String TAG_PACKAGE_RESTRICTIONS = "package-restrictions";
    private static final String TAG_PACKAGE = "pkg";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_USER = "user";
    private static final String ATTR_CODE = "code";
    private static final String ATTR_NOT_LAUNCHED = "nl";
    private static final String ATTR_ENABLED = "enabled";
    private static final String ATTR_ENABLED_CALLER = "enabledCaller";
    private static final String ATTR_STOPPED = "stopped";
    private static final String ATTR_BLOCKED = "blocked";
    private static final String ATTR_INSTALLED = "inst";
    private final File mSettingsFilename;
    private final File mBackupSettingsFilename;
    private final File mPackageListFilename;
    private final File mStoppedPackagesFilename;
    private final File mBackupStoppedPackagesFilename;
    final HashMap<String, PackageSetting> mPackages;
    private final HashMap<String, PackageSetting> mDisabledSysPackages;
    int mInternalSdkPlatform;
    int mExternalSdkPlatform;
    Boolean mReadExternalStorageEnforced;
    private VerifierDeviceIdentity mVerifierDeviceIdentity;
    final SparseArray<PreferredIntentResolver> mPreferredActivities;
    final HashMap<String, SharedUserSetting> mSharedUsers;
    private final ArrayList<Object> mUserIds;
    private final SparseArray<Object> mOtherUserIds;
    private final ArrayList<Signature> mPastSignatures;
    final HashMap<String, BasePermission> mPermissions;
    final HashMap<String, BasePermission> mPermissionTrees;
    final ArrayList<PackageCleanItem> mPackagesToBeCleaned;
    final HashMap<String, String> mRenamedPackages;
    final StringBuilder mReadMessages;
    private final ArrayList<PendingPackage> mPendingPackages;
    private final Context mContext;
    private final File mSystemDir;
    public final KeySetManager mKeySetManager;
    private static int mFirstAvailableUid = 0;
    static final Object[] FLAG_DUMP_SPEC = {1, "SYSTEM", 2, "DEBUGGABLE", 4, "HAS_CODE", 8, "PERSISTENT", 16, "FACTORY_TEST", 32, "ALLOW_TASK_REPARENTING", 64, "ALLOW_CLEAR_USER_DATA", 128, "UPDATED_SYSTEM_APP", 256, "TEST_ONLY", 16384, "VM_SAFE_MODE", 32768, "ALLOW_BACKUP", 65536, "KILL_AFTER_RESTORE", 131072, "RESTORE_ANY_VERSION", 262144, "EXTERNAL_STORAGE", 1048576, "LARGE_HEAP", 1073741824, "PRIVILEGED", 536870912, "FORWARD_LOCK", 268435456, "CANT_SAVE_STATE"};

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.Settings.readDefaultPreferredAppsLPw(com.android.server.pm.PackageManagerService, int):void, file: Settings.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void readDefaultPreferredAppsLPw(com.android.server.pm.PackageManagerService r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.Settings.readDefaultPreferredAppsLPw(com.android.server.pm.PackageManagerService, int):void, file: Settings.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.readDefaultPreferredAppsLPw(com.android.server.pm.PackageManagerService, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.Settings.getAllUsers():java.util.List<android.content.pm.UserInfo>, file: Settings.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private java.util.List<android.content.pm.UserInfo> getAllUsers() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.pm.Settings.getAllUsers():java.util.List<android.content.pm.UserInfo>, file: Settings.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.Settings.getAllUsers():java.util.List");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Settings(Context context) {
        this(context, Environment.getDataDirectory());
    }

    Settings(Context context, File dataDir) {
        this.mPackages = new HashMap<>();
        this.mDisabledSysPackages = new HashMap<>();
        this.mPreferredActivities = new SparseArray<>();
        this.mSharedUsers = new HashMap<>();
        this.mUserIds = new ArrayList<>();
        this.mOtherUserIds = new SparseArray<>();
        this.mPastSignatures = new ArrayList<>();
        this.mPermissions = new HashMap<>();
        this.mPermissionTrees = new HashMap<>();
        this.mPackagesToBeCleaned = new ArrayList<>();
        this.mRenamedPackages = new HashMap<>();
        this.mReadMessages = new StringBuilder();
        this.mPendingPackages = new ArrayList<>();
        this.mKeySetManager = new KeySetManager(this.mPackages);
        this.mContext = context;
        this.mSystemDir = new File(dataDir, "system");
        this.mSystemDir.mkdirs();
        FileUtils.setPermissions(this.mSystemDir.toString(), 509, -1, -1);
        this.mSettingsFilename = new File(this.mSystemDir, "packages.xml");
        this.mBackupSettingsFilename = new File(this.mSystemDir, "packages-backup.xml");
        this.mPackageListFilename = new File(this.mSystemDir, "packages.list");
        FileUtils.setPermissions(this.mPackageListFilename, 432, 1000, 1032);
        this.mStoppedPackagesFilename = new File(this.mSystemDir, "packages-stopped.xml");
        this.mBackupStoppedPackagesFilename = new File(this.mSystemDir, "packages-stopped-backup.xml");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackageSetting getPackageLPw(PackageParser.Package pkg, PackageSetting origPackage, String realName, SharedUserSetting sharedUser, File codePath, File resourcePath, String nativeLibraryPathString, int pkgFlags, UserHandle user, boolean add) {
        String name = pkg.packageName;
        PackageSetting p = getPackageLPw(name, origPackage, realName, sharedUser, codePath, resourcePath, nativeLibraryPathString, pkg.mVersionCode, pkgFlags, user, add, true);
        return p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackageSetting peekPackageLPr(String name) {
        return this.mPackages.get(name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInstallStatus(String pkgName, int status) {
        PackageSetting p = this.mPackages.get(pkgName);
        if (p != null && p.getInstallStatus() != status) {
            p.setInstallStatus(status);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInstallerPackageName(String pkgName, String installerPkgName) {
        PackageSetting p = this.mPackages.get(pkgName);
        if (p != null) {
            p.setInstallerPackageName(installerPkgName);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SharedUserSetting getSharedUserLPw(String name, int pkgFlags, boolean create) {
        SharedUserSetting s = this.mSharedUsers.get(name);
        if (s == null) {
            if (!create) {
                return null;
            }
            s = new SharedUserSetting(name, pkgFlags);
            s.userId = newUserIdLPw(s);
            Log.i("PackageManager", "New shared user " + name + ": id=" + s.userId);
            if (s.userId >= 0) {
                this.mSharedUsers.put(name, s);
            }
        }
        return s;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean disableSystemPackageLPw(String name) {
        PackageSetting p = this.mPackages.get(name);
        if (p == null) {
            Log.w("PackageManager", "Package:" + name + " is not an installed package");
            return false;
        }
        PackageSetting dp = this.mDisabledSysPackages.get(name);
        if (dp == null) {
            if (p.pkg != null && p.pkg.applicationInfo != null) {
                p.pkg.applicationInfo.flags |= 128;
            }
            this.mDisabledSysPackages.put(name, p);
            PackageSetting newp = new PackageSetting(p);
            replacePackageLPw(name, newp);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackageSetting enableSystemPackageLPw(String name) {
        PackageSetting p = this.mDisabledSysPackages.get(name);
        if (p == null) {
            Log.w("PackageManager", "Package:" + name + " is not disabled");
            return null;
        }
        if (p.pkg != null && p.pkg.applicationInfo != null) {
            p.pkg.applicationInfo.flags &= -129;
        }
        PackageSetting ret = addPackageLPw(name, p.realName, p.codePath, p.resourcePath, p.nativeLibraryPathString, p.appId, p.versionCode, p.pkgFlags);
        this.mDisabledSysPackages.remove(name);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDisabledSystemPackageLPr(String name) {
        return this.mDisabledSysPackages.containsKey(name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeDisabledSystemPackageLPw(String name) {
        this.mDisabledSysPackages.remove(name);
    }

    PackageSetting addPackageLPw(String name, String realName, File codePath, File resourcePath, String nativeLibraryPathString, int uid, int vc, int pkgFlags) {
        PackageSetting p = this.mPackages.get(name);
        if (p != null) {
            if (p.appId == uid) {
                return p;
            }
            PackageManagerService.reportSettingsProblem(6, "Adding duplicate package, keeping first: " + name);
            return null;
        }
        PackageSetting p2 = new PackageSetting(name, realName, codePath, resourcePath, nativeLibraryPathString, vc, pkgFlags);
        p2.appId = uid;
        if (addUserIdLPw(uid, p2, name)) {
            this.mPackages.put(name, p2);
            return p2;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SharedUserSetting addSharedUserLPw(String name, int uid, int pkgFlags) {
        SharedUserSetting s = this.mSharedUsers.get(name);
        if (s != null) {
            if (s.userId == uid) {
                return s;
            }
            PackageManagerService.reportSettingsProblem(6, "Adding duplicate shared user, keeping first: " + name);
            return null;
        }
        SharedUserSetting s2 = new SharedUserSetting(name, pkgFlags);
        s2.userId = uid;
        if (addUserIdLPw(uid, s2, name)) {
            this.mSharedUsers.put(name, s2);
            return s2;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pruneSharedUsersLPw() {
        ArrayList<String> removeStage = new ArrayList<>();
        for (Map.Entry<String, SharedUserSetting> entry : this.mSharedUsers.entrySet()) {
            SharedUserSetting sus = entry.getValue();
            if (sus == null || sus.packages.size() == 0) {
                removeStage.add(entry.getKey());
            }
        }
        for (int i = 0; i < removeStage.size(); i++) {
            this.mSharedUsers.remove(removeStage.get(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void transferPermissionsLPw(String origPkg, String newPkg) {
        int i = 0;
        while (i < 2) {
            HashMap<String, BasePermission> permissions = i == 0 ? this.mPermissionTrees : this.mPermissions;
            for (BasePermission bp : permissions.values()) {
                if (origPkg.equals(bp.sourcePackage)) {
                    bp.sourcePackage = newPkg;
                    bp.packageSetting = null;
                    bp.perm = null;
                    if (bp.pendingInfo != null) {
                        bp.pendingInfo.packageName = newPkg;
                    }
                    bp.uid = 0;
                    bp.gids = null;
                }
            }
            i++;
        }
    }

    private PackageSetting getPackageLPw(String name, PackageSetting origPackage, String realName, SharedUserSetting sharedUser, File codePath, File resourcePath, String nativeLibraryPathString, int vc, int pkgFlags, UserHandle installUser, boolean add, boolean allowInstall) {
        List<UserInfo> users;
        List<UserInfo> users2;
        PackageSetting p = this.mPackages.get(name);
        if (p != null) {
            if (!p.codePath.equals(codePath)) {
                if ((p.pkgFlags & 1) != 0) {
                    Slog.w("PackageManager", "Trying to update system app code path from " + p.codePathString + " to " + codePath.toString());
                } else {
                    Slog.i("PackageManager", "Package " + name + " codePath changed from " + p.codePath + " to " + codePath + "; Retaining data and using new");
                    p.nativeLibraryPathString = nativeLibraryPathString;
                }
            }
            if (p.sharedUser != sharedUser) {
                PackageManagerService.reportSettingsProblem(5, "Package " + name + " shared user changed from " + (p.sharedUser != null ? p.sharedUser.name : "<nothing>") + " to " + (sharedUser != null ? sharedUser.name : "<nothing>") + "; replacing with new");
                p = null;
            } else {
                int sysPrivFlags = pkgFlags & 1073741825;
                p.pkgFlags |= sysPrivFlags;
            }
        }
        if (p == null) {
            if (origPackage != null) {
                p = new PackageSetting(origPackage.name, name, codePath, resourcePath, nativeLibraryPathString, vc, pkgFlags);
                PackageSignatures s = p.signatures;
                p.copyFrom(origPackage);
                p.signatures = s;
                p.sharedUser = origPackage.sharedUser;
                p.appId = origPackage.appId;
                p.origPackage = origPackage;
                this.mRenamedPackages.put(name, origPackage.name);
                name = origPackage.name;
                p.setTimeStamp(codePath.lastModified());
            } else {
                p = new PackageSetting(name, realName, codePath, resourcePath, nativeLibraryPathString, vc, pkgFlags);
                p.setTimeStamp(codePath.lastModified());
                p.sharedUser = sharedUser;
                if ((pkgFlags & 1) == 0 && (users2 = getAllUsers()) != null && allowInstall) {
                    for (UserInfo user : users2) {
                        boolean installed = installUser == null || installUser.getIdentifier() == -1 || installUser.getIdentifier() == user.id;
                        p.setUserState(user.id, 0, installed, true, true, false, null, null, null);
                        writePackageRestrictionsLPr(user.id);
                    }
                }
                if (sharedUser != null) {
                    p.appId = sharedUser.userId;
                } else {
                    PackageSetting dis = this.mDisabledSysPackages.get(name);
                    if (dis != null) {
                        if (dis.signatures.mSignatures != null) {
                            p.signatures.mSignatures = (Signature[]) dis.signatures.mSignatures.clone();
                        }
                        p.appId = dis.appId;
                        p.grantedPermissions = new HashSet<>(dis.grantedPermissions);
                        List<UserInfo> users3 = getAllUsers();
                        if (users3 != null) {
                            for (UserInfo user2 : users3) {
                                int userId = user2.id;
                                p.setDisabledComponentsCopy(dis.getDisabledComponents(userId), userId);
                                p.setEnabledComponentsCopy(dis.getEnabledComponents(userId), userId);
                            }
                        }
                        addUserIdLPw(p.appId, p, name);
                    } else {
                        p.appId = newUserIdLPw(p);
                    }
                }
            }
            if (p.appId < 0) {
                PackageManagerService.reportSettingsProblem(5, "Package " + name + " could not be assigned a valid uid");
                return null;
            } else if (add) {
                addPackageSettingLPw(p, name, sharedUser);
            }
        } else if (installUser != null && allowInstall && (users = getAllUsers()) != null) {
            for (UserInfo user3 : users) {
                if (installUser.getIdentifier() == -1 || installUser.getIdentifier() == user3.id) {
                    boolean installed2 = p.getInstalled(user3.id);
                    if (!installed2) {
                        p.setInstalled(true, user3.id);
                        writePackageRestrictionsLPr(user3.id);
                    }
                }
            }
        }
        return p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void insertPackageSettingLPw(PackageSetting p, PackageParser.Package pkg) {
        p.pkg = pkg;
        String codePath = pkg.applicationInfo.sourceDir;
        String resourcePath = pkg.applicationInfo.publicSourceDir;
        if (!codePath.equalsIgnoreCase(p.codePathString)) {
            Slog.w("PackageManager", "Code path for pkg : " + p.pkg.packageName + " changing from " + p.codePathString + " to " + codePath);
            p.codePath = new File(codePath);
            p.codePathString = codePath;
        }
        if (!resourcePath.equalsIgnoreCase(p.resourcePathString)) {
            Slog.w("PackageManager", "Resource path for pkg : " + p.pkg.packageName + " changing from " + p.resourcePathString + " to " + resourcePath);
            p.resourcePath = new File(resourcePath);
            p.resourcePathString = resourcePath;
        }
        String nativeLibraryPath = pkg.applicationInfo.nativeLibraryDir;
        if (nativeLibraryPath != null && !nativeLibraryPath.equalsIgnoreCase(p.nativeLibraryPathString)) {
            p.nativeLibraryPathString = nativeLibraryPath;
        }
        if (pkg.mVersionCode != p.versionCode) {
            p.versionCode = pkg.mVersionCode;
        }
        if (p.signatures.mSignatures == null) {
            p.signatures.assignSignatures(pkg.mSignatures);
        }
        if (pkg.applicationInfo.flags != p.pkgFlags) {
            p.pkgFlags = pkg.applicationInfo.flags;
        }
        if (p.sharedUser != null && p.sharedUser.signatures.mSignatures == null) {
            p.sharedUser.signatures.assignSignatures(pkg.mSignatures);
        }
        addPackageSettingLPw(p, pkg.packageName, p.sharedUser);
    }

    private void addPackageSettingLPw(PackageSetting p, String name, SharedUserSetting sharedUser) {
        this.mPackages.put(name, p);
        if (sharedUser != null) {
            if (p.sharedUser != null && p.sharedUser != sharedUser) {
                PackageManagerService.reportSettingsProblem(6, "Package " + p.name + " was user " + p.sharedUser + " but is now " + sharedUser + "; I am not changing its files so it will probably fail!");
                p.sharedUser.removePackage(p);
            } else if (p.appId != sharedUser.userId) {
                PackageManagerService.reportSettingsProblem(6, "Package " + p.name + " was user id " + p.appId + " but is now user " + sharedUser + " with id " + sharedUser.userId + "; I am not changing its files so it will probably fail!");
            }
            sharedUser.addPackage(p);
            p.sharedUser = sharedUser;
            p.appId = sharedUser.userId;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSharedUserPermsLPw(PackageSetting deletedPs, int[] globalGids) {
        if (deletedPs == null || deletedPs.pkg == null) {
            Slog.i("PackageManager", "Trying to update info for null package. Just ignoring");
        } else if (deletedPs.sharedUser == null) {
        } else {
            SharedUserSetting sus = deletedPs.sharedUser;
            Iterator i$ = deletedPs.pkg.requestedPermissions.iterator();
            while (i$.hasNext()) {
                String eachPerm = i$.next();
                boolean used = false;
                if (sus.grantedPermissions.contains(eachPerm)) {
                    Iterator i$2 = sus.packages.iterator();
                    while (true) {
                        if (!i$2.hasNext()) {
                            break;
                        }
                        PackageSetting pkg = i$2.next();
                        if (pkg.pkg != null && !pkg.pkg.packageName.equals(deletedPs.pkg.packageName) && pkg.pkg.requestedPermissions.contains(eachPerm)) {
                            used = true;
                            break;
                        }
                    }
                    if (!used) {
                        sus.grantedPermissions.remove(eachPerm);
                    }
                }
            }
            int[] newGids = globalGids;
            Iterator i$3 = sus.grantedPermissions.iterator();
            while (i$3.hasNext()) {
                BasePermission bp = this.mPermissions.get(i$3.next());
                if (bp != null) {
                    newGids = PackageManagerService.appendInts(newGids, bp.gids);
                }
            }
            sus.gids = newGids;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int removePackageLPw(String name) {
        PackageSetting p = this.mPackages.get(name);
        if (p != null) {
            this.mPackages.remove(name);
            if (p.sharedUser != null) {
                p.sharedUser.removePackage(p);
                if (p.sharedUser.packages.size() == 0) {
                    this.mSharedUsers.remove(p.sharedUser.name);
                    removeUserIdLPw(p.sharedUser.userId);
                    return p.sharedUser.userId;
                }
                return -1;
            }
            removeUserIdLPw(p.appId);
            return p.appId;
        }
        return -1;
    }

    private void replacePackageLPw(String name, PackageSetting newp) {
        PackageSetting p = this.mPackages.get(name);
        if (p != null) {
            if (p.sharedUser != null) {
                p.sharedUser.removePackage(p);
                p.sharedUser.addPackage(newp);
            } else {
                replaceUserIdLPw(p.appId, newp);
            }
        }
        this.mPackages.put(name, newp);
    }

    private boolean addUserIdLPw(int uid, Object obj, Object name) {
        if (uid > 19999) {
            return false;
        }
        if (uid >= 10000) {
            int index = uid - 10000;
            for (int N = this.mUserIds.size(); index >= N; N++) {
                this.mUserIds.add(null);
            }
            if (this.mUserIds.get(index) != null) {
                PackageManagerService.reportSettingsProblem(6, "Adding duplicate user id: " + uid + " name=" + name);
                return false;
            }
            this.mUserIds.set(index, obj);
            return true;
        } else if (this.mOtherUserIds.get(uid) != null) {
            PackageManagerService.reportSettingsProblem(6, "Adding duplicate shared id: " + uid + " name=" + name);
            return false;
        } else {
            this.mOtherUserIds.put(uid, obj);
            return true;
        }
    }

    public Object getUserIdLPr(int uid) {
        if (uid >= 10000) {
            int N = this.mUserIds.size();
            int index = uid - 10000;
            if (index < N) {
                return this.mUserIds.get(index);
            }
            return null;
        }
        return this.mOtherUserIds.get(uid);
    }

    private void removeUserIdLPw(int uid) {
        if (uid >= 10000) {
            int N = this.mUserIds.size();
            int index = uid - 10000;
            if (index < N) {
                this.mUserIds.set(index, null);
            }
        } else {
            this.mOtherUserIds.remove(uid);
        }
        setFirstAvailableUid(uid + 1);
    }

    private void replaceUserIdLPw(int uid, Object obj) {
        if (uid >= 10000) {
            int N = this.mUserIds.size();
            int index = uid - 10000;
            if (index < N) {
                this.mUserIds.set(index, obj);
                return;
            }
            return;
        }
        this.mOtherUserIds.put(uid, obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PreferredIntentResolver editPreferredActivitiesLPw(int userId) {
        PreferredIntentResolver pir = this.mPreferredActivities.get(userId);
        if (pir == null) {
            pir = new PreferredIntentResolver();
            this.mPreferredActivities.put(userId, pir);
        }
        return pir;
    }

    private File getUserPackagesStateFile(int userId) {
        return new File(Environment.getUserSystemDirectory(userId), "package-restrictions.xml");
    }

    private File getUserPackagesStateBackupFile(int userId) {
        return new File(Environment.getUserSystemDirectory(userId), "package-restrictions-backup.xml");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeAllUsersPackageRestrictionsLPr() {
        List<UserInfo> users = getAllUsers();
        if (users == null) {
            return;
        }
        for (UserInfo user : users) {
            writePackageRestrictionsLPr(user.id);
        }
    }

    void readAllUsersPackageRestrictionsLPr() {
        List<UserInfo> users = getAllUsers();
        if (users == null) {
            readPackageRestrictionsLPr(0);
            return;
        }
        for (UserInfo user : users) {
            readPackageRestrictionsLPr(user.id);
        }
    }

    private void readPreferredActivitiesLPw(XmlPullParser parser, int userId) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals(TAG_ITEM)) {
                        PreferredActivity pa = new PreferredActivity(parser);
                        if (pa.mPref.getParseError() == null) {
                            editPreferredActivitiesLPw(userId).addFilter(pa);
                        } else {
                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <preferred-activity> " + pa.mPref.getParseError() + " at " + parser.getPositionDescription());
                        }
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Unknown element under <preferred-activities>: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            } else {
                return;
            }
        }
    }

    void readPackageRestrictionsLPr(int userId) {
        int type;
        FileInputStream str = null;
        File userPackagesStateFile = getUserPackagesStateFile(userId);
        File backupFile = getUserPackagesStateBackupFile(userId);
        if (backupFile.exists()) {
            try {
                str = new FileInputStream(backupFile);
                this.mReadMessages.append("Reading from backup stopped packages file\n");
                PackageManagerService.reportSettingsProblem(4, "Need to read from backup stopped packages file");
                if (userPackagesStateFile.exists()) {
                    Slog.w("PackageManager", "Cleaning up stopped packages file " + userPackagesStateFile);
                    userPackagesStateFile.delete();
                }
            } catch (IOException e) {
            }
        }
        if (str == null) {
            try {
                if (!userPackagesStateFile.exists()) {
                    this.mReadMessages.append("No stopped packages file found\n");
                    PackageManagerService.reportSettingsProblem(4, "No stopped packages file; assuming all started");
                    for (PackageSetting pkg : this.mPackages.values()) {
                        pkg.setUserState(userId, 0, true, false, false, false, null, null, null);
                    }
                    return;
                }
                str = new FileInputStream(userPackagesStateFile);
            } catch (IOException e2) {
                this.mReadMessages.append("Error reading: " + e2.toString());
                PackageManagerService.reportSettingsProblem(6, "Error reading settings: " + e2);
                Log.wtf("PackageManager", "Error reading package manager stopped packages", e2);
                return;
            } catch (XmlPullParserException e3) {
                this.mReadMessages.append("Error reading: " + e3.toString());
                PackageManagerService.reportSettingsProblem(6, "Error reading stopped packages: " + e3);
                Log.wtf("PackageManager", "Error reading package manager stopped packages", e3);
                return;
            }
        }
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(str, null);
        while (true) {
            type = parser.next();
            if (type == 2 || type == 1) {
                break;
            }
        }
        if (type != 2) {
            this.mReadMessages.append("No start tag found in package restrictions file\n");
            PackageManagerService.reportSettingsProblem(5, "No start tag found in package manager stopped packages");
            return;
        }
        int outerDepth = parser.getDepth();
        while (true) {
            int type2 = parser.next();
            if (type2 == 1 || (type2 == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type2 != 3 && type2 != 4) {
                String tagName = parser.getName();
                if (tagName.equals(TAG_PACKAGE)) {
                    String name = parser.getAttributeValue(null, "name");
                    PackageSetting ps = this.mPackages.get(name);
                    if (ps == null) {
                        Slog.w("PackageManager", "No package known for stopped package: " + name);
                        XmlUtils.skipCurrentTag(parser);
                    } else {
                        String enabledStr = parser.getAttributeValue(null, "enabled");
                        int enabled = enabledStr == null ? 0 : Integer.parseInt(enabledStr);
                        String enabledCaller = parser.getAttributeValue(null, ATTR_ENABLED_CALLER);
                        String installedStr = parser.getAttributeValue(null, ATTR_INSTALLED);
                        boolean installed = installedStr == null ? true : Boolean.parseBoolean(installedStr);
                        String stoppedStr = parser.getAttributeValue(null, ATTR_STOPPED);
                        boolean stopped = stoppedStr == null ? false : Boolean.parseBoolean(stoppedStr);
                        String blockedStr = parser.getAttributeValue(null, ATTR_BLOCKED);
                        boolean blocked = blockedStr == null ? false : Boolean.parseBoolean(blockedStr);
                        String notLaunchedStr = parser.getAttributeValue(null, ATTR_NOT_LAUNCHED);
                        boolean notLaunched = stoppedStr == null ? false : Boolean.parseBoolean(notLaunchedStr);
                        HashSet<String> enabledComponents = null;
                        HashSet<String> disabledComponents = null;
                        int packageDepth = parser.getDepth();
                        while (true) {
                            int type3 = parser.next();
                            if (type3 == 1 || (type3 == 3 && parser.getDepth() <= packageDepth)) {
                                break;
                            } else if (type3 != 3 && type3 != 4) {
                                String tagName2 = parser.getName();
                                if (tagName2.equals(TAG_ENABLED_COMPONENTS)) {
                                    enabledComponents = readComponentsLPr(parser);
                                } else if (tagName2.equals(TAG_DISABLED_COMPONENTS)) {
                                    disabledComponents = readComponentsLPr(parser);
                                }
                            }
                        }
                        ps.setUserState(userId, enabled, installed, stopped, notLaunched, blocked, enabledCaller, enabledComponents, disabledComponents);
                    }
                } else if (tagName.equals("preferred-activities")) {
                    readPreferredActivitiesLPw(parser, userId);
                } else {
                    Slog.w("PackageManager", "Unknown element under <stopped-packages>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
        str.close();
    }

    private HashSet<String> readComponentsLPr(XmlPullParser parser) throws IOException, XmlPullParserException {
        String componentName;
        HashSet<String> components = null;
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type != 3 && type != 4) {
                String tagName = parser.getName();
                if (tagName.equals(TAG_ITEM) && (componentName = parser.getAttributeValue(null, "name")) != null) {
                    if (components == null) {
                        components = new HashSet<>();
                    }
                    components.add(componentName);
                }
            }
        }
        return components;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writePreferredActivitiesLPr(XmlSerializer serializer, int userId, boolean full) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(null, "preferred-activities");
        PreferredIntentResolver pir = this.mPreferredActivities.get(userId);
        if (pir != null) {
            for (PreferredActivity pa : pir.filterSet()) {
                serializer.startTag(null, TAG_ITEM);
                pa.writeToXml(serializer, full);
                serializer.endTag(null, TAG_ITEM);
            }
        }
        serializer.endTag(null, "preferred-activities");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writePackageRestrictionsLPr(int userId) {
        File userPackagesStateFile = getUserPackagesStateFile(userId);
        File backupFile = getUserPackagesStateBackupFile(userId);
        new File(userPackagesStateFile.getParent()).mkdirs();
        if (userPackagesStateFile.exists()) {
            if (!backupFile.exists()) {
                if (!userPackagesStateFile.renameTo(backupFile)) {
                    Log.wtf("PackageManager", "Unable to backup user packages state file, current changes will be lost at reboot");
                    return;
                }
            } else {
                userPackagesStateFile.delete();
                Slog.w("PackageManager", "Preserving older stopped packages backup");
            }
        }
        try {
            FileOutputStream fstr = new FileOutputStream(userPackagesStateFile);
            BufferedOutputStream str = new BufferedOutputStream(fstr);
            XmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(str, "utf-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, TAG_PACKAGE_RESTRICTIONS);
            for (PackageSetting pkg : this.mPackages.values()) {
                PackageUserState ustate = pkg.readUserState(userId);
                if (ustate.stopped || ustate.notLaunched || !ustate.installed || ustate.enabled != 0 || ustate.blocked || ((ustate.enabledComponents != null && ustate.enabledComponents.size() > 0) || (ustate.disabledComponents != null && ustate.disabledComponents.size() > 0))) {
                    serializer.startTag(null, TAG_PACKAGE);
                    serializer.attribute(null, "name", pkg.name);
                    if (!ustate.installed) {
                        serializer.attribute(null, ATTR_INSTALLED, "false");
                    }
                    if (ustate.stopped) {
                        serializer.attribute(null, ATTR_STOPPED, "true");
                    }
                    if (ustate.notLaunched) {
                        serializer.attribute(null, ATTR_NOT_LAUNCHED, "true");
                    }
                    if (ustate.blocked) {
                        serializer.attribute(null, ATTR_BLOCKED, "true");
                    }
                    if (ustate.enabled != 0) {
                        serializer.attribute(null, "enabled", Integer.toString(ustate.enabled));
                        if (ustate.lastDisableAppCaller != null) {
                            serializer.attribute(null, ATTR_ENABLED_CALLER, ustate.lastDisableAppCaller);
                        }
                    }
                    if (ustate.enabledComponents != null && ustate.enabledComponents.size() > 0) {
                        serializer.startTag(null, TAG_ENABLED_COMPONENTS);
                        Iterator i$ = ustate.enabledComponents.iterator();
                        while (i$.hasNext()) {
                            String name = i$.next();
                            serializer.startTag(null, TAG_ITEM);
                            serializer.attribute(null, "name", name);
                            serializer.endTag(null, TAG_ITEM);
                        }
                        serializer.endTag(null, TAG_ENABLED_COMPONENTS);
                    }
                    if (ustate.disabledComponents != null && ustate.disabledComponents.size() > 0) {
                        serializer.startTag(null, TAG_DISABLED_COMPONENTS);
                        Iterator i$2 = ustate.disabledComponents.iterator();
                        while (i$2.hasNext()) {
                            String name2 = i$2.next();
                            serializer.startTag(null, TAG_ITEM);
                            serializer.attribute(null, "name", name2);
                            serializer.endTag(null, TAG_ITEM);
                        }
                        serializer.endTag(null, TAG_DISABLED_COMPONENTS);
                    }
                    serializer.endTag(null, TAG_PACKAGE);
                }
            }
            writePreferredActivitiesLPr(serializer, userId, true);
            serializer.endTag(null, TAG_PACKAGE_RESTRICTIONS);
            serializer.endDocument();
            str.flush();
            FileUtils.sync(fstr);
            str.close();
            backupFile.delete();
            FileUtils.setPermissions(userPackagesStateFile.toString(), 432, -1, -1);
        } catch (IOException e) {
            Log.wtf("PackageManager", "Unable to write package manager user packages state,  current changes will be lost at reboot", e);
            if (userPackagesStateFile.exists() && !userPackagesStateFile.delete()) {
                Log.i("PackageManager", "Failed to clean up mangled file: " + this.mStoppedPackagesFilename);
            }
        }
    }

    void readStoppedLPw() {
        int type;
        FileInputStream str = null;
        if (this.mBackupStoppedPackagesFilename.exists()) {
            try {
                str = new FileInputStream(this.mBackupStoppedPackagesFilename);
                this.mReadMessages.append("Reading from backup stopped packages file\n");
                PackageManagerService.reportSettingsProblem(4, "Need to read from backup stopped packages file");
                if (this.mSettingsFilename.exists()) {
                    Slog.w("PackageManager", "Cleaning up stopped packages file " + this.mStoppedPackagesFilename);
                    this.mStoppedPackagesFilename.delete();
                }
            } catch (IOException e) {
            }
        }
        if (str == null) {
            try {
                if (!this.mStoppedPackagesFilename.exists()) {
                    this.mReadMessages.append("No stopped packages file found\n");
                    PackageManagerService.reportSettingsProblem(4, "No stopped packages file file; assuming all started");
                    for (PackageSetting pkg : this.mPackages.values()) {
                        pkg.setStopped(false, 0);
                        pkg.setNotLaunched(false, 0);
                    }
                    return;
                }
                str = new FileInputStream(this.mStoppedPackagesFilename);
            } catch (IOException e2) {
                this.mReadMessages.append("Error reading: " + e2.toString());
                PackageManagerService.reportSettingsProblem(6, "Error reading settings: " + e2);
                Log.wtf("PackageManager", "Error reading package manager stopped packages", e2);
                return;
            } catch (XmlPullParserException e3) {
                this.mReadMessages.append("Error reading: " + e3.toString());
                PackageManagerService.reportSettingsProblem(6, "Error reading stopped packages: " + e3);
                Log.wtf("PackageManager", "Error reading package manager stopped packages", e3);
                return;
            }
        }
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(str, null);
        while (true) {
            type = parser.next();
            if (type == 2 || type == 1) {
                break;
            }
        }
        if (type != 2) {
            this.mReadMessages.append("No start tag found in stopped packages file\n");
            PackageManagerService.reportSettingsProblem(5, "No start tag found in package manager stopped packages");
            return;
        }
        int outerDepth = parser.getDepth();
        while (true) {
            int type2 = parser.next();
            if (type2 == 1 || (type2 == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type2 != 3 && type2 != 4) {
                String tagName = parser.getName();
                if (tagName.equals(TAG_PACKAGE)) {
                    String name = parser.getAttributeValue(null, "name");
                    PackageSetting ps = this.mPackages.get(name);
                    if (ps != null) {
                        ps.setStopped(true, 0);
                        if ("1".equals(parser.getAttributeValue(null, ATTR_NOT_LAUNCHED))) {
                            ps.setNotLaunched(true, 0);
                        }
                    } else {
                        Slog.w("PackageManager", "No package known for stopped package: " + name);
                    }
                    XmlUtils.skipCurrentTag(parser);
                } else {
                    Slog.w("PackageManager", "Unknown element under <stopped-packages>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
        str.close();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v102, types: [java.lang.AutoCloseable, java.io.BufferedOutputStream] */
    public void writeLPr() {
        if (this.mSettingsFilename.exists()) {
            if (!this.mBackupSettingsFilename.exists()) {
                if (!this.mSettingsFilename.renameTo(this.mBackupSettingsFilename)) {
                    Log.wtf("PackageManager", "Unable to backup package manager settings,  current changes will be lost at reboot");
                    return;
                }
            } else {
                this.mSettingsFilename.delete();
                Slog.w("PackageManager", "Preserving older settings backup");
            }
        }
        this.mPastSignatures.clear();
        try {
            FileOutputStream fstr = new FileOutputStream(this.mSettingsFilename);
            BufferedOutputStream str = new BufferedOutputStream(fstr);
            XmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(str, "utf-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, "packages");
            serializer.startTag(null, "last-platform-version");
            serializer.attribute(null, "internal", Integer.toString(this.mInternalSdkPlatform));
            serializer.attribute(null, "external", Integer.toString(this.mExternalSdkPlatform));
            serializer.endTag(null, "last-platform-version");
            if (this.mVerifierDeviceIdentity != null) {
                serializer.startTag(null, "verifier");
                serializer.attribute(null, UsbManager.EXTRA_DEVICE, this.mVerifierDeviceIdentity.toString());
                serializer.endTag(null, "verifier");
            }
            if (this.mReadExternalStorageEnforced != null) {
                serializer.startTag(null, TAG_READ_EXTERNAL_STORAGE);
                serializer.attribute(null, ATTR_ENFORCEMENT, this.mReadExternalStorageEnforced.booleanValue() ? "1" : "0");
                serializer.endTag(null, TAG_READ_EXTERNAL_STORAGE);
            }
            serializer.startTag(null, "permission-trees");
            for (BasePermission bp : this.mPermissionTrees.values()) {
                writePermissionLPr(serializer, bp);
            }
            serializer.endTag(null, "permission-trees");
            serializer.startTag(null, "permissions");
            for (BasePermission bp2 : this.mPermissions.values()) {
                writePermissionLPr(serializer, bp2);
            }
            serializer.endTag(null, "permissions");
            for (PackageSetting pkg : this.mPackages.values()) {
                writePackageLPr(serializer, pkg);
            }
            for (PackageSetting pkg2 : this.mDisabledSysPackages.values()) {
                writeDisabledSysPackageLPr(serializer, pkg2);
            }
            for (SharedUserSetting usr : this.mSharedUsers.values()) {
                serializer.startTag(null, "shared-user");
                serializer.attribute(null, "name", usr.name);
                serializer.attribute(null, "userId", Integer.toString(usr.userId));
                usr.signatures.writeXml(serializer, "sigs", this.mPastSignatures);
                serializer.startTag(null, "perms");
                Iterator i$ = usr.grantedPermissions.iterator();
                while (i$.hasNext()) {
                    String name = i$.next();
                    serializer.startTag(null, TAG_ITEM);
                    serializer.attribute(null, "name", name);
                    serializer.endTag(null, TAG_ITEM);
                }
                serializer.endTag(null, "perms");
                serializer.endTag(null, "shared-user");
            }
            if (this.mPackagesToBeCleaned.size() > 0) {
                Iterator i$2 = this.mPackagesToBeCleaned.iterator();
                while (i$2.hasNext()) {
                    PackageCleanItem item = i$2.next();
                    String userStr = Integer.toString(item.userId);
                    serializer.startTag(null, "cleaning-package");
                    serializer.attribute(null, "name", item.packageName);
                    serializer.attribute(null, ATTR_CODE, item.andCode ? "true" : "false");
                    serializer.attribute(null, "user", userStr);
                    serializer.endTag(null, "cleaning-package");
                }
            }
            if (this.mRenamedPackages.size() > 0) {
                for (Map.Entry<String, String> e : this.mRenamedPackages.entrySet()) {
                    serializer.startTag(null, "renamed-package");
                    serializer.attribute(null, CallLog.Calls.NEW, e.getKey());
                    serializer.attribute(null, "old", e.getValue());
                    serializer.endTag(null, "renamed-package");
                }
            }
            this.mKeySetManager.writeKeySetManagerLPr(serializer);
            serializer.endTag(null, "packages");
            serializer.endDocument();
            str.flush();
            FileUtils.sync(fstr);
            str.close();
            this.mBackupSettingsFilename.delete();
            FileUtils.setPermissions(this.mSettingsFilename.toString(), 432, -1, -1);
            File tempFile = new File(this.mPackageListFilename.getAbsolutePath() + ".tmp");
            JournaledFile journal = new JournaledFile(this.mPackageListFilename, tempFile);
            File writeTarget = journal.chooseForWrite();
            FileOutputStream fstr2 = new FileOutputStream(writeTarget);
            ?? bufferedOutputStream = new BufferedOutputStream(fstr2);
            try {
                FileUtils.setPermissions(fstr2.getFD(), 432, 1000, 1032);
                StringBuilder sb = new StringBuilder();
                for (PackageSetting pkg3 : this.mPackages.values()) {
                    if (pkg3.pkg == null || pkg3.pkg.applicationInfo == null) {
                        Slog.w(TAG, "Skipping " + pkg3 + " due to missing metadata");
                    } else {
                        ApplicationInfo ai = pkg3.pkg.applicationInfo;
                        String dataPath = ai.dataDir;
                        boolean isDebug = (ai.flags & 2) != 0;
                        int[] gids = pkg3.getGids();
                        if (dataPath.indexOf(Separators.SP) < 0) {
                            sb.setLength(0);
                            sb.append(ai.packageName);
                            sb.append(Separators.SP);
                            sb.append(ai.uid);
                            sb.append(isDebug ? " 1 " : " 0 ");
                            sb.append(dataPath);
                            sb.append(Separators.SP);
                            sb.append(ai.seinfo);
                            sb.append(Separators.SP);
                            if (gids != null && gids.length > 0) {
                                sb.append(gids[0]);
                                for (int i = 1; i < gids.length; i++) {
                                    sb.append(Separators.COMMA);
                                    sb.append(gids[i]);
                                }
                            } else {
                                sb.append("none");
                            }
                            sb.append(Separators.RETURN);
                            bufferedOutputStream.write(sb.toString().getBytes());
                        }
                    }
                }
                bufferedOutputStream.flush();
                FileUtils.sync(fstr2);
                bufferedOutputStream.close();
                journal.commit();
            } catch (Exception e2) {
                Log.wtf(TAG, "Failed to write packages.list", e2);
                IoUtils.closeQuietly((AutoCloseable) bufferedOutputStream);
                journal.rollback();
            }
            writeAllUsersPackageRestrictionsLPr();
        } catch (IOException e3) {
            Log.wtf("PackageManager", "Unable to write package manager settings, current changes will be lost at reboot", e3);
            if (!this.mSettingsFilename.exists() && !this.mSettingsFilename.delete()) {
                Log.wtf("PackageManager", "Failed to clean up mangled file: " + this.mSettingsFilename);
            }
        } catch (XmlPullParserException e4) {
            Log.wtf("PackageManager", "Unable to write package manager settings, current changes will be lost at reboot", e4);
            if (!this.mSettingsFilename.exists()) {
            }
        }
    }

    void writeDisabledSysPackageLPr(XmlSerializer serializer, PackageSetting pkg) throws IOException {
        serializer.startTag(null, "updated-package");
        serializer.attribute(null, "name", pkg.name);
        if (pkg.realName != null) {
            serializer.attribute(null, "realName", pkg.realName);
        }
        serializer.attribute(null, "codePath", pkg.codePathString);
        serializer.attribute(null, "ft", Long.toHexString(pkg.timeStamp));
        serializer.attribute(null, "it", Long.toHexString(pkg.firstInstallTime));
        serializer.attribute(null, "ut", Long.toHexString(pkg.lastUpdateTime));
        serializer.attribute(null, "version", String.valueOf(pkg.versionCode));
        if (!pkg.resourcePathString.equals(pkg.codePathString)) {
            serializer.attribute(null, "resourcePath", pkg.resourcePathString);
        }
        if (pkg.nativeLibraryPathString != null) {
            serializer.attribute(null, "nativeLibraryPath", pkg.nativeLibraryPathString);
        }
        if (pkg.sharedUser == null) {
            serializer.attribute(null, "userId", Integer.toString(pkg.appId));
        } else {
            serializer.attribute(null, "sharedUserId", Integer.toString(pkg.appId));
        }
        serializer.startTag(null, "perms");
        if (pkg.sharedUser == null) {
            Iterator i$ = pkg.grantedPermissions.iterator();
            while (i$.hasNext()) {
                String name = i$.next();
                BasePermission bp = this.mPermissions.get(name);
                if (bp != null) {
                    serializer.startTag(null, TAG_ITEM);
                    serializer.attribute(null, "name", name);
                    serializer.endTag(null, TAG_ITEM);
                }
            }
        }
        serializer.endTag(null, "perms");
        serializer.endTag(null, "updated-package");
    }

    void writePackageLPr(XmlSerializer serializer, PackageSetting pkg) throws IOException {
        serializer.startTag(null, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
        serializer.attribute(null, "name", pkg.name);
        if (pkg.realName != null) {
            serializer.attribute(null, "realName", pkg.realName);
        }
        serializer.attribute(null, "codePath", pkg.codePathString);
        if (!pkg.resourcePathString.equals(pkg.codePathString)) {
            serializer.attribute(null, "resourcePath", pkg.resourcePathString);
        }
        if (pkg.nativeLibraryPathString != null) {
            serializer.attribute(null, "nativeLibraryPath", pkg.nativeLibraryPathString);
        }
        serializer.attribute(null, "flags", Integer.toString(pkg.pkgFlags));
        serializer.attribute(null, "ft", Long.toHexString(pkg.timeStamp));
        serializer.attribute(null, "it", Long.toHexString(pkg.firstInstallTime));
        serializer.attribute(null, "ut", Long.toHexString(pkg.lastUpdateTime));
        serializer.attribute(null, "version", String.valueOf(pkg.versionCode));
        if (pkg.sharedUser == null) {
            serializer.attribute(null, "userId", Integer.toString(pkg.appId));
        } else {
            serializer.attribute(null, "sharedUserId", Integer.toString(pkg.appId));
        }
        if (pkg.uidError) {
            serializer.attribute(null, "uidError", "true");
        }
        if (pkg.installStatus == 0) {
            serializer.attribute(null, "installStatus", "false");
        }
        if (pkg.installerPackageName != null) {
            serializer.attribute(null, "installer", pkg.installerPackageName);
        }
        pkg.signatures.writeXml(serializer, "sigs", this.mPastSignatures);
        if ((pkg.pkgFlags & 1) == 0) {
            serializer.startTag(null, "perms");
            if (pkg.sharedUser == null) {
                Iterator i$ = pkg.grantedPermissions.iterator();
                while (i$.hasNext()) {
                    String name = i$.next();
                    serializer.startTag(null, TAG_ITEM);
                    serializer.attribute(null, "name", name);
                    serializer.endTag(null, TAG_ITEM);
                }
            }
            serializer.endTag(null, "perms");
        }
        writeSigningKeySetsLPr(serializer, pkg.keySetData);
        writeKeySetAliasesLPr(serializer, pkg.keySetData);
        serializer.endTag(null, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
    }

    void writeSigningKeySetsLPr(XmlSerializer serializer, PackageKeySetData data) throws IOException {
        long[] arr$ = data.getSigningKeySets();
        for (long id : arr$) {
            serializer.startTag(null, "signing-keyset");
            serializer.attribute(null, "identifier", Long.toString(id));
            serializer.endTag(null, "signing-keyset");
        }
    }

    void writeKeySetAliasesLPr(XmlSerializer serializer, PackageKeySetData data) throws IOException {
        for (Map.Entry<String, Long> e : data.getAliases().entrySet()) {
            serializer.startTag(null, "defined-keyset");
            serializer.attribute(null, KeyChain.EXTRA_ALIAS, e.getKey());
            serializer.attribute(null, "identifier", Long.toString(e.getValue().longValue()));
            serializer.endTag(null, "defined-keyset");
        }
    }

    void writePermissionLPr(XmlSerializer serializer, BasePermission bp) throws XmlPullParserException, IOException {
        if (bp.type != 1 && bp.sourcePackage != null) {
            serializer.startTag(null, TAG_ITEM);
            serializer.attribute(null, "name", bp.name);
            serializer.attribute(null, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, bp.sourcePackage);
            if (bp.protectionLevel != 0) {
                serializer.attribute(null, "protection", Integer.toString(bp.protectionLevel));
            }
            if (bp.type == 2) {
                PermissionInfo pi = bp.perm != null ? bp.perm.info : bp.pendingInfo;
                if (pi != null) {
                    serializer.attribute(null, "type", "dynamic");
                    if (pi.icon != 0) {
                        serializer.attribute(null, "icon", Integer.toString(pi.icon));
                    }
                    if (pi.nonLocalizedLabel != null) {
                        serializer.attribute(null, "label", pi.nonLocalizedLabel.toString());
                    }
                }
            }
            serializer.endTag(null, TAG_ITEM);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<PackageSetting> getListOfIncompleteInstallPackagesLPr() {
        HashSet<String> kList = new HashSet<>(this.mPackages.keySet());
        Iterator<String> its = kList.iterator();
        ArrayList<PackageSetting> ret = new ArrayList<>();
        while (its.hasNext()) {
            String key = its.next();
            PackageSetting ps = this.mPackages.get(key);
            if (ps.getInstallStatus() == 0) {
                ret.add(ps);
            }
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addPackageToCleanLPw(PackageCleanItem pkg) {
        if (!this.mPackagesToBeCleaned.contains(pkg)) {
            this.mPackagesToBeCleaned.add(pkg);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean readLPw(PackageManagerService service, List<UserInfo> users, int sdkVersion, boolean onlyCore) {
        int type;
        FileInputStream str = null;
        if (this.mBackupSettingsFilename.exists()) {
            try {
                str = new FileInputStream(this.mBackupSettingsFilename);
                this.mReadMessages.append("Reading from backup settings file\n");
                PackageManagerService.reportSettingsProblem(4, "Need to read from backup settings file");
                if (this.mSettingsFilename.exists()) {
                    Slog.w("PackageManager", "Cleaning up settings file " + this.mSettingsFilename);
                    this.mSettingsFilename.delete();
                }
            } catch (IOException e) {
            }
        }
        this.mPendingPackages.clear();
        this.mPastSignatures.clear();
        if (str == null) {
            try {
                if (!this.mSettingsFilename.exists()) {
                    this.mReadMessages.append("No settings file found\n");
                    PackageManagerService.reportSettingsProblem(4, "No settings file; creating initial state");
                    this.mExternalSdkPlatform = sdkVersion;
                    this.mInternalSdkPlatform = sdkVersion;
                    return false;
                }
                str = new FileInputStream(this.mSettingsFilename);
            } catch (IOException e2) {
                this.mReadMessages.append("Error reading: " + e2.toString());
                PackageManagerService.reportSettingsProblem(6, "Error reading settings: " + e2);
                Log.wtf("PackageManager", "Error reading package manager settings", e2);
            } catch (XmlPullParserException e3) {
                this.mReadMessages.append("Error reading: " + e3.toString());
                PackageManagerService.reportSettingsProblem(6, "Error reading settings: " + e3);
                Log.wtf("PackageManager", "Error reading package manager settings", e3);
            }
        }
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(str, null);
        while (true) {
            type = parser.next();
            if (type == 2 || type == 1) {
                break;
            }
        }
        if (type != 2) {
            this.mReadMessages.append("No start tag found in settings file\n");
            PackageManagerService.reportSettingsProblem(5, "No start tag found in package manager settings");
            Log.wtf("PackageManager", "No start tag found in package manager settings");
            return false;
        }
        int outerDepth = parser.getDepth();
        while (true) {
            int type2 = parser.next();
            if (type2 == 1 || (type2 == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type2 != 3 && type2 != 4) {
                String tagName = parser.getName();
                if (tagName.equals(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME)) {
                    readPackageLPw(parser);
                } else if (tagName.equals("permissions")) {
                    readPermissionsLPw(this.mPermissions, parser);
                } else if (tagName.equals("permission-trees")) {
                    readPermissionsLPw(this.mPermissionTrees, parser);
                } else if (tagName.equals("shared-user")) {
                    readSharedUserLPw(parser);
                } else if (!tagName.equals("preferred-packages")) {
                    if (tagName.equals("preferred-activities")) {
                        readPreferredActivitiesLPw(parser, 0);
                    } else if (tagName.equals("updated-package")) {
                        readDisabledSysPackageLPw(parser);
                    } else if (tagName.equals("cleaning-package")) {
                        String name = parser.getAttributeValue(null, "name");
                        String userStr = parser.getAttributeValue(null, "user");
                        String codeStr = parser.getAttributeValue(null, ATTR_CODE);
                        if (name != null) {
                            int userId = 0;
                            boolean andCode = true;
                            if (userStr != null) {
                                try {
                                    userId = Integer.parseInt(userStr);
                                } catch (NumberFormatException e4) {
                                }
                            }
                            if (codeStr != null) {
                                andCode = Boolean.parseBoolean(codeStr);
                            }
                            addPackageToCleanLPw(new PackageCleanItem(userId, name, andCode));
                        }
                    } else if (tagName.equals("renamed-package")) {
                        String nname = parser.getAttributeValue(null, CallLog.Calls.NEW);
                        String oname = parser.getAttributeValue(null, "old");
                        if (nname != null && oname != null) {
                            this.mRenamedPackages.put(nname, oname);
                        }
                    } else if (tagName.equals("last-platform-version")) {
                        this.mExternalSdkPlatform = 0;
                        this.mInternalSdkPlatform = 0;
                        try {
                            String internal = parser.getAttributeValue(null, "internal");
                            if (internal != null) {
                                this.mInternalSdkPlatform = Integer.parseInt(internal);
                            }
                            String external = parser.getAttributeValue(null, "external");
                            if (external != null) {
                                this.mExternalSdkPlatform = Integer.parseInt(external);
                            }
                        } catch (NumberFormatException e5) {
                        }
                    } else if (tagName.equals("verifier")) {
                        String deviceIdentity = parser.getAttributeValue(null, UsbManager.EXTRA_DEVICE);
                        try {
                            this.mVerifierDeviceIdentity = VerifierDeviceIdentity.parse(deviceIdentity);
                        } catch (IllegalArgumentException e6) {
                            Slog.w("PackageManager", "Discard invalid verifier device id: " + e6.getMessage());
                        }
                    } else if (TAG_READ_EXTERNAL_STORAGE.equals(tagName)) {
                        String enforcement = parser.getAttributeValue(null, ATTR_ENFORCEMENT);
                        this.mReadExternalStorageEnforced = Boolean.valueOf("1".equals(enforcement));
                    } else if (tagName.equals("keyset-settings")) {
                        this.mKeySetManager.readKeySetsLPw(parser);
                    } else {
                        Slog.w("PackageManager", "Unknown element under <packages>: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            }
        }
        str.close();
        int N = this.mPendingPackages.size();
        for (int i = 0; i < N; i++) {
            PendingPackage pp = this.mPendingPackages.get(i);
            Object idObj = getUserIdLPr(pp.sharedId);
            if (idObj != null && (idObj instanceof SharedUserSetting)) {
                PackageSetting p = getPackageLPw(pp.name, null, pp.realName, (SharedUserSetting) idObj, pp.codePath, pp.resourcePath, pp.nativeLibraryPathString, pp.versionCode, pp.pkgFlags, null, true, false);
                if (p == null) {
                    PackageManagerService.reportSettingsProblem(5, "Unable to create application package for " + pp.name);
                } else {
                    p.copyFrom(pp);
                }
            } else if (idObj != null) {
                String msg = "Bad package setting: package " + pp.name + " has shared uid " + pp.sharedId + " that is not a shared uid\n";
                this.mReadMessages.append(msg);
                PackageManagerService.reportSettingsProblem(6, msg);
            } else {
                String msg2 = "Bad package setting: package " + pp.name + " has shared uid " + pp.sharedId + " that is not defined\n";
                this.mReadMessages.append(msg2);
                PackageManagerService.reportSettingsProblem(6, msg2);
            }
        }
        this.mPendingPackages.clear();
        if (this.mBackupStoppedPackagesFilename.exists() || this.mStoppedPackagesFilename.exists()) {
            readStoppedLPw();
            this.mBackupStoppedPackagesFilename.delete();
            this.mStoppedPackagesFilename.delete();
            writePackageRestrictionsLPr(0);
        } else if (users == null) {
            readPackageRestrictionsLPr(0);
        } else {
            for (UserInfo user : users) {
                readPackageRestrictionsLPr(user.id);
            }
        }
        for (PackageSetting disabledPs : this.mDisabledSysPackages.values()) {
            Object id = getUserIdLPr(disabledPs.appId);
            if (id != null && (id instanceof SharedUserSetting)) {
                disabledPs.sharedUser = (SharedUserSetting) id;
            }
        }
        this.mReadMessages.append("Read completed successfully: " + this.mPackages.size() + " packages, " + this.mSharedUsers.size() + " shared uids\n");
        return true;
    }

    private void applyDefaultPreferredActivityLPw(PackageManagerService service, IntentFilter tmpPa, ComponentName cn, int userId) {
        Intent intent = new Intent();
        int flags = 0;
        intent.setAction(tmpPa.getAction(0));
        for (int i = 0; i < tmpPa.countCategories(); i++) {
            String cat = tmpPa.getCategory(i);
            if (cat.equals(Intent.CATEGORY_DEFAULT)) {
                flags |= 65536;
            } else {
                intent.addCategory(cat);
            }
        }
        boolean doNonData = true;
        for (int ischeme = 0; ischeme < tmpPa.countDataSchemes(); ischeme++) {
            boolean doScheme = true;
            String scheme = tmpPa.getDataScheme(ischeme);
            for (int issp = 0; issp < tmpPa.countDataSchemeSpecificParts(); issp++) {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(scheme);
                PatternMatcher ssp = tmpPa.getDataSchemeSpecificPart(issp);
                builder.opaquePart(ssp.getPath());
                Intent finalIntent = new Intent(intent);
                finalIntent.setData(builder.build());
                applyDefaultPreferredActivityLPw(service, finalIntent, flags, cn, scheme, ssp, null, null, null, userId);
                doScheme = false;
            }
            for (int iauth = 0; iauth < tmpPa.countDataAuthorities(); iauth++) {
                boolean doAuth = true;
                IntentFilter.AuthorityEntry auth = tmpPa.getDataAuthority(iauth);
                for (int ipath = 0; ipath < tmpPa.countDataPaths(); ipath++) {
                    Uri.Builder builder2 = new Uri.Builder();
                    builder2.scheme(scheme);
                    if (auth.getHost() != null) {
                        builder2.authority(auth.getHost());
                    }
                    PatternMatcher path = tmpPa.getDataPath(ipath);
                    builder2.path(path.getPath());
                    Intent finalIntent2 = new Intent(intent);
                    finalIntent2.setData(builder2.build());
                    applyDefaultPreferredActivityLPw(service, finalIntent2, flags, cn, scheme, null, auth, path, null, userId);
                    doScheme = false;
                    doAuth = false;
                }
                if (doAuth) {
                    Uri.Builder builder3 = new Uri.Builder();
                    builder3.scheme(scheme);
                    if (auth.getHost() != null) {
                        builder3.authority(auth.getHost());
                    }
                    Intent finalIntent3 = new Intent(intent);
                    finalIntent3.setData(builder3.build());
                    applyDefaultPreferredActivityLPw(service, finalIntent3, flags, cn, scheme, null, auth, null, null, userId);
                    doScheme = false;
                }
            }
            if (doScheme) {
                Uri.Builder builder4 = new Uri.Builder();
                builder4.scheme(scheme);
                Intent finalIntent4 = new Intent(intent);
                finalIntent4.setData(builder4.build());
                applyDefaultPreferredActivityLPw(service, finalIntent4, flags, cn, scheme, null, null, null, null, userId);
            }
            doNonData = false;
        }
        for (int idata = 0; idata < tmpPa.countDataTypes(); idata++) {
            Intent finalIntent5 = new Intent(intent);
            String mimeType = tmpPa.getDataType(idata);
            finalIntent5.setType(mimeType);
            applyDefaultPreferredActivityLPw(service, finalIntent5, flags, cn, null, null, null, null, mimeType, userId);
            doNonData = false;
        }
        if (doNonData) {
            applyDefaultPreferredActivityLPw(service, intent, flags, cn, null, null, null, null, null, userId);
        }
    }

    private void applyDefaultPreferredActivityLPw(PackageManagerService service, Intent intent, int flags, ComponentName cn, String scheme, PatternMatcher ssp, IntentFilter.AuthorityEntry auth, PatternMatcher path, String mimeType, int userId) {
        List<ResolveInfo> ri = service.mActivities.queryIntent(intent, intent.getType(), flags, 0);
        int match = 0;
        if (ri != null && ri.size() > 1) {
            boolean haveAct = false;
            boolean haveNonSys = false;
            ComponentName[] set = new ComponentName[ri.size()];
            int i = 0;
            while (true) {
                if (i >= ri.size()) {
                    break;
                }
                ActivityInfo ai = ri.get(i).activityInfo;
                set[i] = new ComponentName(ai.packageName, ai.name);
                if ((ai.applicationInfo.flags & 1) == 0) {
                    haveNonSys = true;
                    break;
                }
                if (cn.getPackageName().equals(ai.packageName) && cn.getClassName().equals(ai.name)) {
                    haveAct = true;
                    match = ri.get(i).match;
                }
                i++;
            }
            if (!haveAct || haveNonSys) {
                if (!haveNonSys) {
                    Slog.w(TAG, "No component found for default preferred activity " + cn);
                    return;
                }
                return;
            }
            IntentFilter filter = new IntentFilter();
            if (intent.getAction() != null) {
                filter.addAction(intent.getAction());
            }
            for (String cat : intent.getCategories()) {
                filter.addCategory(cat);
            }
            if ((flags & 65536) != 0) {
                filter.addCategory(Intent.CATEGORY_DEFAULT);
            }
            if (scheme != null) {
                filter.addDataScheme(scheme);
            }
            if (ssp != null) {
                filter.addDataSchemeSpecificPart(ssp.getPath(), ssp.getType());
            }
            if (auth != null) {
                filter.addDataAuthority(auth);
            }
            if (path != null) {
                filter.addDataPath(path);
            }
            PreferredActivity pa = new PreferredActivity(filter, match, set, cn, true);
            editPreferredActivitiesLPw(userId).addFilter(pa);
        }
    }

    private void readDefaultPreferredActivitiesLPw(PackageManagerService service, XmlPullParser parser, int userId) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals(TAG_ITEM)) {
                        PreferredActivity tmpPa = new PreferredActivity(parser);
                        if (tmpPa.mPref.getParseError() == null) {
                            applyDefaultPreferredActivityLPw(service, tmpPa, tmpPa.mPref.mComponent, userId);
                        } else {
                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <preferred-activity> " + tmpPa.mPref.getParseError() + " at " + parser.getPositionDescription());
                        }
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Unknown element under <preferred-activities>: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            } else {
                return;
            }
        }
    }

    private int readInt(XmlPullParser parser, String ns, String name, int defValue) {
        String v = parser.getAttributeValue(ns, name);
        if (v == null) {
            return defValue;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: attribute " + name + " has bad integer value " + v + " at " + parser.getPositionDescription());
            return defValue;
        }
    }

    private void readPermissionsLPw(HashMap<String, BasePermission> out, XmlPullParser parser) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals(TAG_ITEM)) {
                        String name = parser.getAttributeValue(null, "name");
                        String sourcePackage = parser.getAttributeValue(null, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
                        String ptype = parser.getAttributeValue(null, "type");
                        if (name != null && sourcePackage != null) {
                            boolean dynamic = "dynamic".equals(ptype);
                            BasePermission bp = new BasePermission(name, sourcePackage, dynamic ? 2 : 0);
                            bp.protectionLevel = readInt(parser, null, "protection", 0);
                            bp.protectionLevel = PermissionInfo.fixProtectionLevel(bp.protectionLevel);
                            if (dynamic) {
                                PermissionInfo pi = new PermissionInfo();
                                pi.packageName = sourcePackage.intern();
                                pi.name = name.intern();
                                pi.icon = readInt(parser, null, "icon", 0);
                                pi.nonLocalizedLabel = parser.getAttributeValue(null, "label");
                                pi.protectionLevel = bp.protectionLevel;
                                bp.pendingInfo = pi;
                            }
                            out.put(bp.name, bp);
                        } else {
                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: permissions has no name at " + parser.getPositionDescription());
                        }
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Unknown element reading permissions: " + parser.getName() + " at " + parser.getPositionDescription());
                    }
                    XmlUtils.skipCurrentTag(parser);
                }
            } else {
                return;
            }
        }
    }

    private void readDisabledSysPackageLPw(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name = parser.getAttributeValue(null, "name");
        String realName = parser.getAttributeValue(null, "realName");
        String codePathStr = parser.getAttributeValue(null, "codePath");
        String resourcePathStr = parser.getAttributeValue(null, "resourcePath");
        String nativeLibraryPathStr = parser.getAttributeValue(null, "nativeLibraryPath");
        if (resourcePathStr == null) {
            resourcePathStr = codePathStr;
        }
        String version = parser.getAttributeValue(null, "version");
        int versionCode = 0;
        if (version != null) {
            try {
                versionCode = Integer.parseInt(version);
            } catch (NumberFormatException e) {
            }
        }
        int pkgFlags = 0 | 1;
        File codePathFile = new File(codePathStr);
        if (PackageManagerService.locationIsPrivileged(codePathFile)) {
            pkgFlags |= 1073741824;
        }
        PackageSetting ps = new PackageSetting(name, realName, codePathFile, new File(resourcePathStr), nativeLibraryPathStr, versionCode, pkgFlags);
        String timeStampStr = parser.getAttributeValue(null, "ft");
        if (timeStampStr != null) {
            try {
                long timeStamp = Long.parseLong(timeStampStr, 16);
                ps.setTimeStamp(timeStamp);
            } catch (NumberFormatException e2) {
            }
        } else {
            String timeStampStr2 = parser.getAttributeValue(null, "ts");
            if (timeStampStr2 != null) {
                try {
                    long timeStamp2 = Long.parseLong(timeStampStr2);
                    ps.setTimeStamp(timeStamp2);
                } catch (NumberFormatException e3) {
                }
            }
        }
        String timeStampStr3 = parser.getAttributeValue(null, "it");
        if (timeStampStr3 != null) {
            try {
                ps.firstInstallTime = Long.parseLong(timeStampStr3, 16);
            } catch (NumberFormatException e4) {
            }
        }
        String timeStampStr4 = parser.getAttributeValue(null, "ut");
        if (timeStampStr4 != null) {
            try {
                ps.lastUpdateTime = Long.parseLong(timeStampStr4, 16);
            } catch (NumberFormatException e5) {
            }
        }
        String idStr = parser.getAttributeValue(null, "userId");
        ps.appId = idStr != null ? Integer.parseInt(idStr) : 0;
        if (ps.appId <= 0) {
            String sharedIdStr = parser.getAttributeValue(null, "sharedUserId");
            ps.appId = sharedIdStr != null ? Integer.parseInt(sharedIdStr) : 0;
        }
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type != 3 && type != 4) {
                String tagName = parser.getName();
                if (tagName.equals("perms")) {
                    readGrantedPermissionsLPw(parser, ps.grantedPermissions);
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <updated-package>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
        this.mDisabledSysPackages.put(name, ps);
    }

    private void readPackageLPw(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name = null;
        String idStr = null;
        String nativeLibraryPathStr = null;
        String installerPackageName = null;
        String uidError = null;
        int pkgFlags = 0;
        long timeStamp = 0;
        long firstInstallTime = 0;
        long lastUpdateTime = 0;
        PackageSettingBase packageSetting = null;
        int versionCode = 0;
        try {
            name = parser.getAttributeValue(null, "name");
            String realName = parser.getAttributeValue(null, "realName");
            idStr = parser.getAttributeValue(null, "userId");
            uidError = parser.getAttributeValue(null, "uidError");
            String sharedIdStr = parser.getAttributeValue(null, "sharedUserId");
            String codePathStr = parser.getAttributeValue(null, "codePath");
            String resourcePathStr = parser.getAttributeValue(null, "resourcePath");
            nativeLibraryPathStr = parser.getAttributeValue(null, "nativeLibraryPath");
            String version = parser.getAttributeValue(null, "version");
            if (version != null) {
                try {
                    versionCode = Integer.parseInt(version);
                } catch (NumberFormatException e) {
                }
            }
            installerPackageName = parser.getAttributeValue(null, "installer");
            String systemStr = parser.getAttributeValue(null, "flags");
            if (systemStr != null) {
                try {
                    pkgFlags = Integer.parseInt(systemStr);
                } catch (NumberFormatException e2) {
                }
            } else {
                String systemStr2 = parser.getAttributeValue(null, "system");
                if (systemStr2 != null) {
                    pkgFlags = 0 | ("true".equalsIgnoreCase(systemStr2) ? 1 : 0);
                } else {
                    pkgFlags = 0 | 1;
                }
            }
            String timeStampStr = parser.getAttributeValue(null, "ft");
            if (timeStampStr != null) {
                try {
                    timeStamp = Long.parseLong(timeStampStr, 16);
                } catch (NumberFormatException e3) {
                }
            } else {
                String timeStampStr2 = parser.getAttributeValue(null, "ts");
                if (timeStampStr2 != null) {
                    try {
                        timeStamp = Long.parseLong(timeStampStr2);
                    } catch (NumberFormatException e4) {
                    }
                }
            }
            String timeStampStr3 = parser.getAttributeValue(null, "it");
            if (timeStampStr3 != null) {
                try {
                    firstInstallTime = Long.parseLong(timeStampStr3, 16);
                } catch (NumberFormatException e5) {
                }
            }
            String timeStampStr4 = parser.getAttributeValue(null, "ut");
            if (timeStampStr4 != null) {
                try {
                    lastUpdateTime = Long.parseLong(timeStampStr4, 16);
                } catch (NumberFormatException e6) {
                }
            }
            int userId = idStr != null ? Integer.parseInt(idStr) : 0;
            if (resourcePathStr == null) {
                resourcePathStr = codePathStr;
            }
            if (realName != null) {
                realName = realName.intern();
            }
            if (name == null) {
                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <package> has no name at " + parser.getPositionDescription());
            } else if (codePathStr == null) {
                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <package> has no codePath at " + parser.getPositionDescription());
            } else if (userId > 0) {
                packageSetting = addPackageLPw(name.intern(), realName, new File(codePathStr), new File(resourcePathStr), nativeLibraryPathStr, userId, versionCode, pkgFlags);
                if (packageSetting == null) {
                    PackageManagerService.reportSettingsProblem(6, "Failure adding uid " + userId + " while parsing settings at " + parser.getPositionDescription());
                } else {
                    packageSetting.setTimeStamp(timeStamp);
                    packageSetting.firstInstallTime = firstInstallTime;
                    packageSetting.lastUpdateTime = lastUpdateTime;
                }
            } else if (sharedIdStr != null) {
                int userId2 = sharedIdStr != null ? Integer.parseInt(sharedIdStr) : 0;
                if (userId2 > 0) {
                    packageSetting = new PendingPackage(name.intern(), realName, new File(codePathStr), new File(resourcePathStr), nativeLibraryPathStr, userId2, versionCode, pkgFlags);
                    packageSetting.setTimeStamp(timeStamp);
                    packageSetting.firstInstallTime = firstInstallTime;
                    packageSetting.lastUpdateTime = lastUpdateTime;
                    this.mPendingPackages.add((PendingPackage) packageSetting);
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: package " + name + " has bad sharedId " + sharedIdStr + " at " + parser.getPositionDescription());
                }
            } else {
                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: package " + name + " has bad userId " + idStr + " at " + parser.getPositionDescription());
            }
        } catch (NumberFormatException e7) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: package " + ((String) null) + " has bad userId " + ((String) null) + " at " + parser.getPositionDescription());
        }
        if (packageSetting != null) {
            packageSetting.uidError = "true".equals(uidError);
            packageSetting.installerPackageName = installerPackageName;
            packageSetting.nativeLibraryPathString = nativeLibraryPathStr;
            String enabledStr = parser.getAttributeValue(null, "enabled");
            if (enabledStr != null) {
                try {
                    packageSetting.setEnabled(Integer.parseInt(enabledStr), 0, null);
                } catch (NumberFormatException e8) {
                    if (enabledStr.equalsIgnoreCase("true")) {
                        packageSetting.setEnabled(1, 0, null);
                    } else if (enabledStr.equalsIgnoreCase("false")) {
                        packageSetting.setEnabled(2, 0, null);
                    } else if (enabledStr.equalsIgnoreCase("default")) {
                        packageSetting.setEnabled(0, 0, null);
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: package " + name + " has bad enabled value: " + idStr + " at " + parser.getPositionDescription());
                    }
                }
            } else {
                packageSetting.setEnabled(0, 0, null);
            }
            String installStatusStr = parser.getAttributeValue(null, "installStatus");
            if (installStatusStr != null) {
                if (installStatusStr.equalsIgnoreCase("false")) {
                    packageSetting.installStatus = 0;
                } else {
                    packageSetting.installStatus = 1;
                }
            }
            int outerDepth = parser.getDepth();
            while (true) {
                int type = parser.next();
                if (type == 1) {
                    return;
                }
                if (type != 3 || parser.getDepth() > outerDepth) {
                    if (type != 3 && type != 4) {
                        String tagName = parser.getName();
                        if (tagName.equals(TAG_DISABLED_COMPONENTS)) {
                            readDisabledComponentsLPw(packageSetting, parser, 0);
                        } else if (tagName.equals(TAG_ENABLED_COMPONENTS)) {
                            readEnabledComponentsLPw(packageSetting, parser, 0);
                        } else if (tagName.equals("sigs")) {
                            packageSetting.signatures.readXml(parser, this.mPastSignatures);
                        } else if (tagName.equals("perms")) {
                            readGrantedPermissionsLPw(parser, packageSetting.grantedPermissions);
                            packageSetting.permissionsFixed = true;
                        } else if (tagName.equals("signing-keyset")) {
                            long id = Long.parseLong(parser.getAttributeValue(null, "identifier"));
                            packageSetting.keySetData.addSigningKeySet(id);
                        } else if (tagName.equals("defined-keyset")) {
                            long id2 = Long.parseLong(parser.getAttributeValue(null, "identifier"));
                            String alias = parser.getAttributeValue(null, KeyChain.EXTRA_ALIAS);
                            packageSetting.keySetData.addDefinedKeySet(id2, alias);
                        } else {
                            PackageManagerService.reportSettingsProblem(5, "Unknown element under <package>: " + parser.getName());
                            XmlUtils.skipCurrentTag(parser);
                        }
                    }
                } else {
                    return;
                }
            }
        } else {
            XmlUtils.skipCurrentTag(parser);
        }
    }

    private void readDisabledComponentsLPw(PackageSettingBase packageSetting, XmlPullParser parser, int userId) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals(TAG_ITEM)) {
                        String name = parser.getAttributeValue(null, "name");
                        if (name != null) {
                            packageSetting.addDisabledComponent(name.intern(), userId);
                        } else {
                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <disabled-components> has no name at " + parser.getPositionDescription());
                        }
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Unknown element under <disabled-components>: " + parser.getName());
                    }
                    XmlUtils.skipCurrentTag(parser);
                }
            } else {
                return;
            }
        }
    }

    private void readEnabledComponentsLPw(PackageSettingBase packageSetting, XmlPullParser parser, int userId) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals(TAG_ITEM)) {
                        String name = parser.getAttributeValue(null, "name");
                        if (name != null) {
                            packageSetting.addEnabledComponent(name.intern(), userId);
                        } else {
                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <enabled-components> has no name at " + parser.getPositionDescription());
                        }
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Unknown element under <enabled-components>: " + parser.getName());
                    }
                    XmlUtils.skipCurrentTag(parser);
                }
            } else {
                return;
            }
        }
    }

    private void readSharedUserLPw(XmlPullParser parser) throws XmlPullParserException, IOException {
        int pkgFlags = 0;
        SharedUserSetting su = null;
        try {
            String name = parser.getAttributeValue(null, "name");
            String idStr = parser.getAttributeValue(null, "userId");
            int userId = idStr != null ? Integer.parseInt(idStr) : 0;
            if ("true".equals(parser.getAttributeValue(null, "system"))) {
                pkgFlags = 0 | 1;
            }
            if (name == null) {
                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <shared-user> has no name at " + parser.getPositionDescription());
            } else if (userId == 0) {
                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: shared-user " + name + " has bad userId " + idStr + " at " + parser.getPositionDescription());
            } else {
                SharedUserSetting addSharedUserLPw = addSharedUserLPw(name.intern(), userId, pkgFlags);
                su = addSharedUserLPw;
                if (addSharedUserLPw == null) {
                    PackageManagerService.reportSettingsProblem(6, "Occurred while parsing settings at " + parser.getPositionDescription());
                }
            }
        } catch (NumberFormatException e) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: package " + ((String) null) + " has bad userId " + ((String) null) + " at " + parser.getPositionDescription());
        }
        if (su != null) {
            int outerDepth = parser.getDepth();
            while (true) {
                int type = parser.next();
                if (type == 1) {
                    return;
                }
                if (type != 3 || parser.getDepth() > outerDepth) {
                    if (type != 3 && type != 4) {
                        String tagName = parser.getName();
                        if (tagName.equals("sigs")) {
                            su.signatures.readXml(parser, this.mPastSignatures);
                        } else if (tagName.equals("perms")) {
                            readGrantedPermissionsLPw(parser, su.grantedPermissions);
                        } else {
                            PackageManagerService.reportSettingsProblem(5, "Unknown element under <shared-user>: " + parser.getName());
                            XmlUtils.skipCurrentTag(parser);
                        }
                    }
                } else {
                    return;
                }
            }
        } else {
            XmlUtils.skipCurrentTag(parser);
        }
    }

    private void readGrantedPermissionsLPw(XmlPullParser parser, HashSet<String> outPerms) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals(TAG_ITEM)) {
                        String name = parser.getAttributeValue(null, "name");
                        if (name != null) {
                            outPerms.add(name.intern());
                        } else {
                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <perms> has no name at " + parser.getPositionDescription());
                        }
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Unknown element under <perms>: " + parser.getName());
                    }
                    XmlUtils.skipCurrentTag(parser);
                }
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void createNewUserLILPw(PackageManagerService service, Installer installer, int userHandle, File path) {
        path.mkdir();
        FileUtils.setPermissions(path.toString(), 505, -1, -1);
        for (PackageSetting ps : this.mPackages.values()) {
            ps.setInstalled((ps.pkgFlags & 1) != 0, userHandle);
            installer.createUserData(ps.name, UserHandle.getUid(userHandle, ps.appId), userHandle);
        }
        readDefaultPreferredAppsLPw(service, userHandle);
        writePackageRestrictionsLPr(userHandle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeUserLPr(int userId) {
        Set<Map.Entry<String, PackageSetting>> entries = this.mPackages.entrySet();
        for (Map.Entry<String, PackageSetting> entry : entries) {
            entry.getValue().removeUser(userId);
        }
        this.mPreferredActivities.remove(userId);
        File file = getUserPackagesStateFile(userId);
        file.delete();
        File file2 = getUserPackagesStateBackupFile(userId);
        file2.delete();
    }

    private void setFirstAvailableUid(int uid) {
        if (uid > mFirstAvailableUid) {
            mFirstAvailableUid = uid;
        }
    }

    private int newUserIdLPw(Object obj) {
        int N = this.mUserIds.size();
        for (int i = mFirstAvailableUid; i < N; i++) {
            if (this.mUserIds.get(i) == null) {
                this.mUserIds.set(i, obj);
                return 10000 + i;
            }
        }
        if (N > 9999) {
            return -1;
        }
        this.mUserIds.add(obj);
        return 10000 + N;
    }

    public VerifierDeviceIdentity getVerifierDeviceIdentityLPw() {
        if (this.mVerifierDeviceIdentity == null) {
            this.mVerifierDeviceIdentity = VerifierDeviceIdentity.generate();
            writeLPr();
        }
        return this.mVerifierDeviceIdentity;
    }

    public PackageSetting getDisabledSystemPkgLPr(String name) {
        PackageSetting ps = this.mDisabledSysPackages.get(name);
        return ps;
    }

    private String compToString(HashSet<String> cmp) {
        return cmp != null ? Arrays.toString(cmp.toArray()) : "[]";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEnabledLPr(ComponentInfo componentInfo, int flags, int userId) {
        if ((flags & 512) != 0) {
            return true;
        }
        String pkgName = componentInfo.packageName;
        PackageSetting packageSettings = this.mPackages.get(pkgName);
        if (packageSettings == null) {
            return false;
        }
        PackageUserState ustate = packageSettings.readUserState(userId);
        if ((flags & 32768) != 0 && ustate.enabled == 4) {
            return true;
        }
        if (ustate.enabled == 2 || ustate.enabled == 3 || ustate.enabled == 4) {
            return false;
        }
        if (packageSettings.pkg != null && !packageSettings.pkg.applicationInfo.enabled && ustate.enabled == 0) {
            return false;
        }
        if (ustate.enabledComponents != null && ustate.enabledComponents.contains(componentInfo.name)) {
            return true;
        }
        if (ustate.disabledComponents != null && ustate.disabledComponents.contains(componentInfo.name)) {
            return false;
        }
        return componentInfo.enabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getInstallerPackageNameLPr(String packageName) {
        PackageSetting pkg = this.mPackages.get(packageName);
        if (pkg == null) {
            throw new IllegalArgumentException("Unknown package: " + packageName);
        }
        return pkg.installerPackageName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getApplicationEnabledSettingLPr(String packageName, int userId) {
        PackageSetting pkg = this.mPackages.get(packageName);
        if (pkg == null) {
            throw new IllegalArgumentException("Unknown package: " + packageName);
        }
        return pkg.getEnabled(userId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getComponentEnabledSettingLPr(ComponentName componentName, int userId) {
        String packageName = componentName.getPackageName();
        PackageSetting pkg = this.mPackages.get(packageName);
        if (pkg == null) {
            throw new IllegalArgumentException("Unknown component: " + componentName);
        }
        String classNameStr = componentName.getClassName();
        return pkg.getCurrentEnabledStateLPr(classNameStr, userId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setPackageStoppedStateLPw(String packageName, boolean stopped, boolean allowedByPermission, int uid, int userId) {
        int appId = UserHandle.getAppId(uid);
        PackageSetting pkgSetting = this.mPackages.get(packageName);
        if (pkgSetting == null) {
            throw new IllegalArgumentException("Unknown package: " + packageName);
        }
        if (!allowedByPermission && appId != pkgSetting.appId) {
            throw new SecurityException("Permission Denial: attempt to change stopped state from pid=" + Binder.getCallingPid() + ", uid=" + uid + ", package uid=" + pkgSetting.appId);
        }
        if (pkgSetting.getStopped(userId) != stopped) {
            pkgSetting.setStopped(stopped, userId);
            if (pkgSetting.getNotLaunched(userId)) {
                if (pkgSetting.installerPackageName != null) {
                    PackageManagerService.sendPackageBroadcast(Intent.ACTION_PACKAGE_FIRST_LAUNCH, pkgSetting.name, null, pkgSetting.installerPackageName, null, new int[]{userId});
                }
                pkgSetting.setNotLaunched(false, userId);
                return true;
            }
            return true;
        }
        return false;
    }

    static final void printFlags(PrintWriter pw, int val, Object[] spec) {
        pw.print("[ ");
        for (int i = 0; i < spec.length; i += 2) {
            int mask = ((Integer) spec[i]).intValue();
            if ((val & mask) != 0) {
                pw.print(spec[i + 1]);
                pw.print(Separators.SP);
            }
        }
        pw.print("]");
    }

    void dumpPackageLPr(PrintWriter pw, String prefix, PackageSetting ps, SimpleDateFormat sdf, Date date, List<UserInfo> users) {
        pw.print(prefix);
        pw.print("Package [");
        pw.print(ps.realName != null ? ps.realName : ps.name);
        pw.print("] (");
        pw.print(Integer.toHexString(System.identityHashCode(ps)));
        pw.println("):");
        if (ps.realName != null) {
            pw.print(prefix);
            pw.print("  compat name=");
            pw.println(ps.name);
        }
        pw.print(prefix);
        pw.print("  userId=");
        pw.print(ps.appId);
        pw.print(" gids=");
        pw.println(PackageManagerService.arrayToString(ps.gids));
        if (ps.sharedUser != null) {
            pw.print(prefix);
            pw.print("  sharedUser=");
            pw.println(ps.sharedUser);
        }
        pw.print(prefix);
        pw.print("  pkg=");
        pw.println(ps.pkg);
        pw.print(prefix);
        pw.print("  codePath=");
        pw.println(ps.codePathString);
        pw.print(prefix);
        pw.print("  resourcePath=");
        pw.println(ps.resourcePathString);
        pw.print(prefix);
        pw.print("  nativeLibraryPath=");
        pw.println(ps.nativeLibraryPathString);
        pw.print(prefix);
        pw.print("  versionCode=");
        pw.print(ps.versionCode);
        if (ps.pkg != null) {
            pw.print(" targetSdk=");
            pw.print(ps.pkg.applicationInfo.targetSdkVersion);
        }
        pw.println();
        if (ps.pkg != null) {
            pw.print(prefix);
            pw.print("  versionName=");
            pw.println(ps.pkg.mVersionName);
            pw.print(prefix);
            pw.print("  applicationInfo=");
            pw.println(ps.pkg.applicationInfo.toString());
            pw.print(prefix);
            pw.print("  flags=");
            printFlags(pw, ps.pkg.applicationInfo.flags, FLAG_DUMP_SPEC);
            pw.println();
            pw.print(prefix);
            pw.print("  dataDir=");
            pw.println(ps.pkg.applicationInfo.dataDir);
            if (ps.pkg.mOperationPending) {
                pw.print(prefix);
                pw.println("  mOperationPending=true");
            }
            pw.print(prefix);
            pw.print("  supportsScreens=[");
            boolean first = true;
            if ((ps.pkg.applicationInfo.flags & 512) != 0) {
                if (1 == 0) {
                    pw.print(", ");
                }
                first = false;
                pw.print("small");
            }
            if ((ps.pkg.applicationInfo.flags & 1024) != 0) {
                if (!first) {
                    pw.print(", ");
                }
                first = false;
                pw.print("medium");
            }
            if ((ps.pkg.applicationInfo.flags & 2048) != 0) {
                if (!first) {
                    pw.print(", ");
                }
                first = false;
                pw.print("large");
            }
            if ((ps.pkg.applicationInfo.flags & 524288) != 0) {
                if (!first) {
                    pw.print(", ");
                }
                first = false;
                pw.print("xlarge");
            }
            if ((ps.pkg.applicationInfo.flags & 4096) != 0) {
                if (!first) {
                    pw.print(", ");
                }
                first = false;
                pw.print("resizeable");
            }
            if ((ps.pkg.applicationInfo.flags & 8192) != 0) {
                if (!first) {
                    pw.print(", ");
                }
                pw.print("anyDensity");
            }
            pw.println("]");
            if (ps.pkg.libraryNames != null && ps.pkg.libraryNames.size() > 0) {
                pw.print(prefix);
                pw.println("  libraries:");
                for (int i = 0; i < ps.pkg.libraryNames.size(); i++) {
                    pw.print(prefix);
                    pw.print("    ");
                    pw.println(ps.pkg.libraryNames.get(i));
                }
            }
            if (ps.pkg.usesLibraries != null && ps.pkg.usesLibraries.size() > 0) {
                pw.print(prefix);
                pw.println("  usesLibraries:");
                for (int i2 = 0; i2 < ps.pkg.usesLibraries.size(); i2++) {
                    pw.print(prefix);
                    pw.print("    ");
                    pw.println(ps.pkg.usesLibraries.get(i2));
                }
            }
            if (ps.pkg.usesOptionalLibraries != null && ps.pkg.usesOptionalLibraries.size() > 0) {
                pw.print(prefix);
                pw.println("  usesOptionalLibraries:");
                for (int i3 = 0; i3 < ps.pkg.usesOptionalLibraries.size(); i3++) {
                    pw.print(prefix);
                    pw.print("    ");
                    pw.println(ps.pkg.usesOptionalLibraries.get(i3));
                }
            }
            if (ps.pkg.usesLibraryFiles != null && ps.pkg.usesLibraryFiles.length > 0) {
                pw.print(prefix);
                pw.println("  usesLibraryFiles:");
                for (int i4 = 0; i4 < ps.pkg.usesLibraryFiles.length; i4++) {
                    pw.print(prefix);
                    pw.print("    ");
                    pw.println(ps.pkg.usesLibraryFiles[i4]);
                }
            }
        }
        pw.print(prefix);
        pw.print("  timeStamp=");
        date.setTime(ps.timeStamp);
        pw.println(sdf.format(date));
        pw.print(prefix);
        pw.print("  firstInstallTime=");
        date.setTime(ps.firstInstallTime);
        pw.println(sdf.format(date));
        pw.print(prefix);
        pw.print("  lastUpdateTime=");
        date.setTime(ps.lastUpdateTime);
        pw.println(sdf.format(date));
        if (ps.installerPackageName != null) {
            pw.print(prefix);
            pw.print("  installerPackageName=");
            pw.println(ps.installerPackageName);
        }
        pw.print(prefix);
        pw.print("  signatures=");
        pw.println(ps.signatures);
        pw.print(prefix);
        pw.print("  permissionsFixed=");
        pw.print(ps.permissionsFixed);
        pw.print(" haveGids=");
        pw.print(ps.haveGids);
        pw.print(" installStatus=");
        pw.println(ps.installStatus);
        pw.print(prefix);
        pw.print("  pkgFlags=");
        printFlags(pw, ps.pkgFlags, FLAG_DUMP_SPEC);
        pw.println();
        for (UserInfo user : users) {
            pw.print(prefix);
            pw.print("  User ");
            pw.print(user.id);
            pw.print(": ");
            pw.print(" installed=");
            pw.print(ps.getInstalled(user.id));
            pw.print(" blocked=");
            pw.print(ps.getBlocked(user.id));
            pw.print(" stopped=");
            pw.print(ps.getStopped(user.id));
            pw.print(" notLaunched=");
            pw.print(ps.getNotLaunched(user.id));
            pw.print(" enabled=");
            pw.println(ps.getEnabled(user.id));
            String lastDisabledAppCaller = ps.getLastDisabledAppCaller(user.id);
            if (lastDisabledAppCaller != null) {
                pw.print(prefix);
                pw.print("    lastDisabledCaller: ");
                pw.println(lastDisabledAppCaller);
            }
            HashSet<String> cmp = ps.getDisabledComponents(user.id);
            if (cmp != null && cmp.size() > 0) {
                pw.print(prefix);
                pw.println("    disabledComponents:");
                Iterator i$ = cmp.iterator();
                while (i$.hasNext()) {
                    String s = i$.next();
                    pw.print(prefix);
                    pw.print("    ");
                    pw.println(s);
                }
            }
            HashSet<String> cmp2 = ps.getEnabledComponents(user.id);
            if (cmp2 != null && cmp2.size() > 0) {
                pw.print(prefix);
                pw.println("    enabledComponents:");
                Iterator i$2 = cmp2.iterator();
                while (i$2.hasNext()) {
                    String s2 = i$2.next();
                    pw.print(prefix);
                    pw.print("    ");
                    pw.println(s2);
                }
            }
        }
        if (ps.grantedPermissions.size() > 0) {
            pw.print(prefix);
            pw.println("  grantedPermissions:");
            Iterator i$3 = ps.grantedPermissions.iterator();
            while (i$3.hasNext()) {
                String s3 = i$3.next();
                pw.print(prefix);
                pw.print("    ");
                pw.println(s3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpPackagesLPr(PrintWriter pw, String packageName, PackageManagerService.DumpState dumpState) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        boolean printedSomething = false;
        List<UserInfo> users = getAllUsers();
        for (PackageSetting ps : this.mPackages.values()) {
            if (packageName == null || packageName.equals(ps.realName) || packageName.equals(ps.name)) {
                if (packageName != null) {
                    dumpState.setSharedUser(ps.sharedUser);
                }
                if (!printedSomething) {
                    if (dumpState.onTitlePrinted()) {
                        pw.println();
                    }
                    pw.println("Packages:");
                    printedSomething = true;
                }
                dumpPackageLPr(pw, "  ", ps, sdf, date, users);
            }
        }
        boolean printedSomething2 = false;
        if (this.mRenamedPackages.size() > 0) {
            for (Map.Entry<String, String> e : this.mRenamedPackages.entrySet()) {
                if (packageName == null || packageName.equals(e.getKey()) || packageName.equals(e.getValue())) {
                    if (!printedSomething2) {
                        if (dumpState.onTitlePrinted()) {
                            pw.println();
                        }
                        pw.println("Renamed packages:");
                        printedSomething2 = true;
                    }
                    pw.print("  ");
                    pw.print(e.getKey());
                    pw.print(" -> ");
                    pw.println(e.getValue());
                }
            }
        }
        boolean printedSomething3 = false;
        if (this.mDisabledSysPackages.size() > 0) {
            for (PackageSetting ps2 : this.mDisabledSysPackages.values()) {
                if (packageName == null || packageName.equals(ps2.realName) || packageName.equals(ps2.name)) {
                    if (!printedSomething3) {
                        if (dumpState.onTitlePrinted()) {
                            pw.println();
                        }
                        pw.println("Hidden system packages:");
                        printedSomething3 = true;
                    }
                    dumpPackageLPr(pw, "  ", ps2, sdf, date, users);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpPermissionsLPr(PrintWriter pw, String packageName, PackageManagerService.DumpState dumpState) {
        boolean printedSomething = false;
        for (BasePermission p : this.mPermissions.values()) {
            if (packageName == null || packageName.equals(p.sourcePackage)) {
                if (!printedSomething) {
                    if (dumpState.onTitlePrinted()) {
                        pw.println();
                    }
                    pw.println("Permissions:");
                    printedSomething = true;
                }
                pw.print("  Permission [");
                pw.print(p.name);
                pw.print("] (");
                pw.print(Integer.toHexString(System.identityHashCode(p)));
                pw.println("):");
                pw.print("    sourcePackage=");
                pw.println(p.sourcePackage);
                pw.print("    uid=");
                pw.print(p.uid);
                pw.print(" gids=");
                pw.print(PackageManagerService.arrayToString(p.gids));
                pw.print(" type=");
                pw.print(p.type);
                pw.print(" prot=");
                pw.println(PermissionInfo.protectionToString(p.protectionLevel));
                if (p.packageSetting != null) {
                    pw.print("    packageSetting=");
                    pw.println(p.packageSetting);
                }
                if (p.perm != null) {
                    pw.print("    perm=");
                    pw.println(p.perm);
                }
                if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(p.name)) {
                    pw.print("    enforced=");
                    pw.println(this.mReadExternalStorageEnforced);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpSharedUsersLPr(PrintWriter pw, String packageName, PackageManagerService.DumpState dumpState) {
        boolean printedSomething = false;
        for (SharedUserSetting su : this.mSharedUsers.values()) {
            if (packageName == null || su == dumpState.getSharedUser()) {
                if (!printedSomething) {
                    if (dumpState.onTitlePrinted()) {
                        pw.println();
                    }
                    pw.println("Shared users:");
                    printedSomething = true;
                }
                pw.print("  SharedUser [");
                pw.print(su.name);
                pw.print("] (");
                pw.print(Integer.toHexString(System.identityHashCode(su)));
                pw.println("):");
                pw.print("    userId=");
                pw.print(su.userId);
                pw.print(" gids=");
                pw.println(PackageManagerService.arrayToString(su.gids));
                pw.println("    grantedPermissions:");
                Iterator i$ = su.grantedPermissions.iterator();
                while (i$.hasNext()) {
                    String s = i$.next();
                    pw.print("      ");
                    pw.println(s);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpReadMessagesLPr(PrintWriter pw, PackageManagerService.DumpState dumpState) {
        pw.println("Settings parse messages:");
        pw.print(this.mReadMessages.toString());
    }
}