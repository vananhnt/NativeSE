package java.security;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SecurityPermission.class */
public final class SecurityPermission extends BasicPermission {
    public SecurityPermission(String name) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    public SecurityPermission(String name, String action) {
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