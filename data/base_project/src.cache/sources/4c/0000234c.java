package java.lang.reflect;

import java.security.BasicPermission;
import java.security.Permission;

/* loaded from: ReflectPermission.class */
public final class ReflectPermission extends BasicPermission {
    public ReflectPermission(String name) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    public ReflectPermission(String name, String actions) {
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