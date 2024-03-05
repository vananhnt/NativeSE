package com.android.server.pm;

import java.util.HashSet;

/* loaded from: GrantedPermissions.class */
class GrantedPermissions {
    int pkgFlags;
    HashSet<String> grantedPermissions;
    int[] gids;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GrantedPermissions(int pkgFlags) {
        this.grantedPermissions = new HashSet<>();
        setFlags(pkgFlags);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GrantedPermissions(GrantedPermissions base) {
        this.grantedPermissions = new HashSet<>();
        this.pkgFlags = base.pkgFlags;
        this.grantedPermissions = (HashSet) base.grantedPermissions.clone();
        if (base.gids != null) {
            this.gids = (int[]) base.gids.clone();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlags(int pkgFlags) {
        this.pkgFlags = pkgFlags & 1610874881;
    }
}