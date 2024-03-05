package java.security;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: UnresolvedPermission.class */
public final class UnresolvedPermission extends Permission implements Serializable {
    public UnresolvedPermission(String type, String name, String actions, java.security.cert.Certificate[] certs) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public String getUnresolvedName() {
        throw new RuntimeException("Stub!");
    }

    public String getUnresolvedActions() {
        throw new RuntimeException("Stub!");
    }

    public String getUnresolvedType() {
        throw new RuntimeException("Stub!");
    }

    public java.security.cert.Certificate[] getUnresolvedCerts() {
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