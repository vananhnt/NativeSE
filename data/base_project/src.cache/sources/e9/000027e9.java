package javax.security.auth;

import java.security.BasicPermission;
import java.security.Permission;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AuthPermission.class */
public final class AuthPermission extends BasicPermission {
    public AuthPermission(String name) {
        super("");
    }

    public AuthPermission(String name, String actions) {
        super("", "");
    }

    @Override // java.security.BasicPermission, java.security.Permission
    public String getActions() {
        return null;
    }

    @Override // java.security.BasicPermission, java.security.Permission
    public boolean implies(Permission permission) {
        return true;
    }
}