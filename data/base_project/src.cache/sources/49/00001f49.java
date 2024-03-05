package com.android.server.pm;

import java.io.File;

/* loaded from: PendingPackage.class */
final class PendingPackage extends PackageSettingBase {
    final int sharedId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PendingPackage(String name, String realName, File codePath, File resourcePath, String nativeLibraryPathString, int sharedId, int pVersionCode, int pkgFlags) {
        super(name, realName, codePath, resourcePath, nativeLibraryPathString, pVersionCode, pkgFlags);
        this.sharedId = sharedId;
    }
}