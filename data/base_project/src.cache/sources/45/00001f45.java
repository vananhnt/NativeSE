package com.android.server.pm;

import android.content.pm.PackageUserState;
import android.util.SparseArray;
import java.io.File;
import java.util.HashSet;

/* loaded from: PackageSettingBase.class */
class PackageSettingBase extends GrantedPermissions {
    static final int PKG_INSTALL_COMPLETE = 1;
    static final int PKG_INSTALL_INCOMPLETE = 0;
    final String name;
    final String realName;
    File codePath;
    String codePathString;
    File resourcePath;
    String resourcePathString;
    String nativeLibraryPathString;
    long timeStamp;
    long firstInstallTime;
    long lastUpdateTime;
    int versionCode;
    boolean uidError;
    PackageSignatures signatures;
    boolean permissionsFixed;
    boolean haveGids;
    PackageKeySetData keySetData;
    private static final PackageUserState DEFAULT_USER_STATE = new PackageUserState();
    private final SparseArray<PackageUserState> userState;
    int installStatus;
    PackageSettingBase origPackage;
    String installerPackageName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackageSettingBase(String name, String realName, File codePath, File resourcePath, String nativeLibraryPathString, int pVersionCode, int pkgFlags) {
        super(pkgFlags);
        this.signatures = new PackageSignatures();
        this.keySetData = new PackageKeySetData();
        this.userState = new SparseArray<>();
        this.installStatus = 1;
        this.name = name;
        this.realName = realName;
        init(codePath, resourcePath, nativeLibraryPathString, pVersionCode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackageSettingBase(PackageSettingBase base) {
        super(base);
        this.signatures = new PackageSignatures();
        this.keySetData = new PackageKeySetData();
        this.userState = new SparseArray<>();
        this.installStatus = 1;
        this.name = base.name;
        this.realName = base.realName;
        this.codePath = base.codePath;
        this.codePathString = base.codePathString;
        this.resourcePath = base.resourcePath;
        this.resourcePathString = base.resourcePathString;
        this.nativeLibraryPathString = base.nativeLibraryPathString;
        this.timeStamp = base.timeStamp;
        this.firstInstallTime = base.firstInstallTime;
        this.lastUpdateTime = base.lastUpdateTime;
        this.versionCode = base.versionCode;
        this.uidError = base.uidError;
        this.signatures = new PackageSignatures(base.signatures);
        this.permissionsFixed = base.permissionsFixed;
        this.haveGids = base.haveGids;
        this.userState.clear();
        for (int i = 0; i < base.userState.size(); i++) {
            this.userState.put(base.userState.keyAt(i), new PackageUserState(base.userState.valueAt(i)));
        }
        this.installStatus = base.installStatus;
        this.origPackage = base.origPackage;
        this.installerPackageName = base.installerPackageName;
        this.keySetData = new PackageKeySetData(base.keySetData);
    }

    void init(File codePath, File resourcePath, String nativeLibraryPathString, int pVersionCode) {
        this.codePath = codePath;
        this.codePathString = codePath.toString();
        this.resourcePath = resourcePath;
        this.resourcePathString = resourcePath.toString();
        this.nativeLibraryPathString = nativeLibraryPathString;
        this.versionCode = pVersionCode;
    }

    public void setInstallerPackageName(String packageName) {
        this.installerPackageName = packageName;
    }

    String getInstallerPackageName() {
        return this.installerPackageName;
    }

    public void setInstallStatus(int newStatus) {
        this.installStatus = newStatus;
    }

    public int getInstallStatus() {
        return this.installStatus;
    }

    public void setTimeStamp(long newStamp) {
        this.timeStamp = newStamp;
    }

    public void copyFrom(PackageSettingBase base) {
        this.grantedPermissions = base.grantedPermissions;
        this.gids = base.gids;
        this.timeStamp = base.timeStamp;
        this.firstInstallTime = base.firstInstallTime;
        this.lastUpdateTime = base.lastUpdateTime;
        this.signatures = base.signatures;
        this.permissionsFixed = base.permissionsFixed;
        this.haveGids = base.haveGids;
        this.userState.clear();
        for (int i = 0; i < base.userState.size(); i++) {
            this.userState.put(base.userState.keyAt(i), base.userState.valueAt(i));
        }
        this.installStatus = base.installStatus;
        this.keySetData = base.keySetData;
    }

    private PackageUserState modifyUserState(int userId) {
        PackageUserState state = this.userState.get(userId);
        if (state == null) {
            state = new PackageUserState();
            this.userState.put(userId, state);
        }
        return state;
    }

    public PackageUserState readUserState(int userId) {
        PackageUserState state = this.userState.get(userId);
        if (state != null) {
            return state;
        }
        return DEFAULT_USER_STATE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEnabled(int state, int userId, String callingPackage) {
        PackageUserState st = modifyUserState(userId);
        st.enabled = state;
        st.lastDisableAppCaller = callingPackage;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getEnabled(int userId) {
        return readUserState(userId).enabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getLastDisabledAppCaller(int userId) {
        return readUserState(userId).lastDisableAppCaller;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInstalled(boolean inst, int userId) {
        modifyUserState(userId).installed = inst;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getInstalled(int userId) {
        return readUserState(userId).installed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAnyInstalled(int[] users) {
        for (int user : users) {
            if (readUserState(user).installed) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] queryInstalledUsers(int[] users, boolean installed) {
        int num = 0;
        for (int user : users) {
            if (getInstalled(user) == installed) {
                num++;
            }
        }
        int[] res = new int[num];
        int num2 = 0;
        for (int user2 : users) {
            if (getInstalled(user2) == installed) {
                res[num2] = user2;
                num2++;
            }
        }
        return res;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getStopped(int userId) {
        return readUserState(userId).stopped;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStopped(boolean stop, int userId) {
        modifyUserState(userId).stopped = stop;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getNotLaunched(int userId) {
        return readUserState(userId).notLaunched;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNotLaunched(boolean stop, int userId) {
        modifyUserState(userId).notLaunched = stop;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getBlocked(int userId) {
        return readUserState(userId).blocked;
    }

    void setBlocked(boolean blocked, int userId) {
        modifyUserState(userId).blocked = blocked;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUserState(int userId, int enabled, boolean installed, boolean stopped, boolean notLaunched, boolean blocked, String lastDisableAppCaller, HashSet<String> enabledComponents, HashSet<String> disabledComponents) {
        PackageUserState state = modifyUserState(userId);
        state.enabled = enabled;
        state.installed = installed;
        state.stopped = stopped;
        state.notLaunched = notLaunched;
        state.blocked = blocked;
        state.lastDisableAppCaller = lastDisableAppCaller;
        state.enabledComponents = enabledComponents;
        state.disabledComponents = disabledComponents;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HashSet<String> getEnabledComponents(int userId) {
        return readUserState(userId).enabledComponents;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HashSet<String> getDisabledComponents(int userId) {
        return readUserState(userId).disabledComponents;
    }

    void setEnabledComponents(HashSet<String> components, int userId) {
        modifyUserState(userId).enabledComponents = components;
    }

    void setDisabledComponents(HashSet<String> components, int userId) {
        modifyUserState(userId).disabledComponents = components;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEnabledComponentsCopy(HashSet<String> components, int userId) {
        modifyUserState(userId).enabledComponents = components != null ? new HashSet<>(components) : null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDisabledComponentsCopy(HashSet<String> components, int userId) {
        modifyUserState(userId).disabledComponents = components != null ? new HashSet<>(components) : null;
    }

    PackageUserState modifyUserStateComponents(int userId, boolean disabled, boolean enabled) {
        PackageUserState state = modifyUserState(userId);
        if (disabled && state.disabledComponents == null) {
            state.disabledComponents = new HashSet<>(1);
        }
        if (enabled && state.enabledComponents == null) {
            state.enabledComponents = new HashSet<>(1);
        }
        return state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addDisabledComponent(String componentClassName, int userId) {
        modifyUserStateComponents(userId, true, false).disabledComponents.add(componentClassName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addEnabledComponent(String componentClassName, int userId) {
        modifyUserStateComponents(userId, false, true).enabledComponents.add(componentClassName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean enableComponentLPw(String componentClassName, int userId) {
        PackageUserState state = modifyUserStateComponents(userId, false, true);
        boolean changed = state.disabledComponents != null ? state.disabledComponents.remove(componentClassName) : false;
        return changed | state.enabledComponents.add(componentClassName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean disableComponentLPw(String componentClassName, int userId) {
        PackageUserState state = modifyUserStateComponents(userId, true, false);
        boolean changed = state.enabledComponents != null ? state.enabledComponents.remove(componentClassName) : false;
        return changed | state.disabledComponents.add(componentClassName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean restoreComponentLPw(String componentClassName, int userId) {
        PackageUserState state = modifyUserStateComponents(userId, true, true);
        boolean changed = state.disabledComponents != null ? state.disabledComponents.remove(componentClassName) : false;
        return changed | (state.enabledComponents != null ? state.enabledComponents.remove(componentClassName) : false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCurrentEnabledStateLPr(String componentName, int userId) {
        PackageUserState state = readUserState(userId);
        if (state.enabledComponents != null && state.enabledComponents.contains(componentName)) {
            return 1;
        }
        if (state.disabledComponents != null && state.disabledComponents.contains(componentName)) {
            return 2;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeUser(int userId) {
        this.userState.delete(userId);
    }
}