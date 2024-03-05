package java.util;

import java.security.BasicPermission;
import java.security.Permission;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PropertyPermission.class */
public final class PropertyPermission extends BasicPermission {
    public PropertyPermission(String name, String actions) {
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