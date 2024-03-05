package com.android.server.pm;

import android.content.pm.PackageParser;
import gov.nist.core.Separators;
import java.io.File;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: PackageSetting.class */
public final class PackageSetting extends PackageSettingBase {
    int appId;
    PackageParser.Package pkg;
    SharedUserSetting sharedUser;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackageSetting(String name, String realName, File codePath, File resourcePath, String nativeLibraryPathString, int pVersionCode, int pkgFlags) {
        super(name, realName, codePath, resourcePath, nativeLibraryPathString, pVersionCode, pkgFlags);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackageSetting(PackageSetting orig) {
        super(orig);
        this.appId = orig.appId;
        this.pkg = orig.pkg;
        this.sharedUser = orig.sharedUser;
    }

    public String toString() {
        return "PackageSetting{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.name + Separators.SLASH + this.appId + "}";
    }

    public int[] getGids() {
        return this.sharedUser != null ? this.sharedUser.gids : this.gids;
    }

    public boolean isPrivileged() {
        return (this.pkgFlags & 1073741824) != 0;
    }
}