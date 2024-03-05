package java.security;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AllPermission.class */
public final class AllPermission extends Permission {
    public AllPermission(String name, String actions) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public AllPermission() {
        super(null);
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.Permission
    public String getActions() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.Permission
    public boolean implies(Permission permission) {
        throw new RuntimeException("Stub!");
    }
}