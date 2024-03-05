package java.security;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AccessControlException.class */
public class AccessControlException extends SecurityException {
    public AccessControlException(String message) {
        throw new RuntimeException("Stub!");
    }

    public AccessControlException(String message, Permission perm) {
        throw new RuntimeException("Stub!");
    }

    public Permission getPermission() {
        throw new RuntimeException("Stub!");
    }
}