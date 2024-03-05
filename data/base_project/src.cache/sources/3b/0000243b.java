package java.security;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BasicPermission.class */
public abstract class BasicPermission extends Permission implements Serializable {
    public BasicPermission(String name) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public BasicPermission(String name, String action) {
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