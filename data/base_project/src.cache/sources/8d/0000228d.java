package java.io;

import java.security.Permission;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FilePermission.class */
public final class FilePermission extends Permission implements Serializable {
    public FilePermission(String path, String actions) {
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