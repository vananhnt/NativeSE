package javax.security.auth;

import java.security.Permission;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PrivateCredentialPermission.class */
public final class PrivateCredentialPermission extends Permission {
    public PrivateCredentialPermission(String name, String action) {
        super("");
    }

    public String[][] getPrincipals() {
        return null;
    }

    public String getCredentialClass() {
        return null;
    }

    @Override // java.security.Permission
    public String getActions() {
        return null;
    }

    @Override // java.security.Permission
    public boolean implies(Permission permission) {
        return true;
    }
}