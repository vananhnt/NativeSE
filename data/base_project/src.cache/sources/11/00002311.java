package java.lang;

import java.security.BasicPermission;
import java.security.Permission;

/* loaded from: RuntimePermission.class */
public final class RuntimePermission extends BasicPermission {
    public RuntimePermission(String permissionName) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    public RuntimePermission(String name, String actions) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.BasicPermission, java.security.Permission
    public String getActions() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.BasicPermission, java.security.Permission
    public boolean implies(Permission permission) {
        throw new RuntimeException("Stub!");
    }
}