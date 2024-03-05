package com.android.server.pm;

import android.content.pm.PackageParser;
import android.content.pm.PermissionInfo;
import gov.nist.core.Separators;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: BasePermission.class */
public final class BasePermission {
    static final int TYPE_NORMAL = 0;
    static final int TYPE_BUILTIN = 1;
    static final int TYPE_DYNAMIC = 2;
    final String name;
    String sourcePackage;
    PackageSettingBase packageSetting;
    final int type;
    int protectionLevel = 2;
    PackageParser.Permission perm;
    PermissionInfo pendingInfo;
    int uid;
    int[] gids;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BasePermission(String _name, String _sourcePackage, int _type) {
        this.name = _name;
        this.sourcePackage = _sourcePackage;
        this.type = _type;
    }

    public String toString() {
        return "BasePermission{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.name + "}";
    }
}