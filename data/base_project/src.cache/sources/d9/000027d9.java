package javax.net.ssl;

import java.security.BasicPermission;
import java.security.Permission;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLPermission.class */
public final class SSLPermission extends BasicPermission {
    public SSLPermission(String name) {
        super("");
    }

    public SSLPermission(String name, String actions) {
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