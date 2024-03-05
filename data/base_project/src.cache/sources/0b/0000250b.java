package java.sql;

import java.io.Serializable;
import java.security.BasicPermission;
import java.security.Guard;
import java.security.Permission;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SQLPermission.class */
public final class SQLPermission extends BasicPermission implements Guard, Serializable {
    public SQLPermission(String name) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    public SQLPermission(String name, String actions) {
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