package java.security;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Permission.class */
public abstract class Permission implements Guard, Serializable {
    public abstract String getActions();

    public abstract boolean implies(Permission permission);

    public Permission(String name) {
        throw new RuntimeException("Stub!");
    }

    public final String getName() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.Guard
    public void checkGuard(Object obj) throws SecurityException {
        throw new RuntimeException("Stub!");
    }

    public PermissionCollection newPermissionCollection() {
        throw new RuntimeException("Stub!");
    }
}