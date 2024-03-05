package com.android.server.pm;

import gov.nist.core.Separators;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: SharedUserSetting.class */
final class SharedUserSetting extends GrantedPermissions {
    final String name;
    int userId;
    int uidFlags;
    final HashSet<PackageSetting> packages;
    final PackageSignatures signatures;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SharedUserSetting(String _name, int _pkgFlags) {
        super(_pkgFlags);
        this.packages = new HashSet<>();
        this.signatures = new PackageSignatures();
        this.uidFlags = _pkgFlags;
        this.name = _name;
    }

    public String toString() {
        return "SharedUserSetting{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.name + Separators.SLASH + this.userId + "}";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removePackage(PackageSetting packageSetting) {
        if (this.packages.remove(packageSetting) && (this.pkgFlags & packageSetting.pkgFlags) != 0) {
            int aggregatedFlags = this.uidFlags;
            Iterator i$ = this.packages.iterator();
            while (i$.hasNext()) {
                PackageSetting ps = i$.next();
                aggregatedFlags |= ps.pkgFlags;
            }
            setFlags(aggregatedFlags);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addPackage(PackageSetting packageSetting) {
        if (this.packages.add(packageSetting)) {
            setFlags(this.pkgFlags | packageSetting.pkgFlags);
        }
    }
}