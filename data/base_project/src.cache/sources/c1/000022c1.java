package java.io;

import java.security.BasicPermission;
import java.security.Permission;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SerializablePermission.class */
public final class SerializablePermission extends BasicPermission {
    public SerializablePermission(String permissionName) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    public SerializablePermission(String name, String actions) {
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