package java.net;

import java.security.BasicPermission;
import java.security.Permission;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NetPermission.class */
public final class NetPermission extends BasicPermission {
    public NetPermission(String name) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    public NetPermission(String name, String actions) {
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