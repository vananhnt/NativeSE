package java.net;

import java.io.Serializable;
import java.security.Permission;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SocketPermission.class */
public final class SocketPermission extends Permission implements Serializable {
    public SocketPermission(String host, String action) {
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