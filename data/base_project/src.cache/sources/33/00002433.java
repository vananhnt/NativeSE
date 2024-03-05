package java.security;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AccessController.class */
public final class AccessController {
    AccessController() {
        throw new RuntimeException("Stub!");
    }

    public static <T> T doPrivileged(PrivilegedAction<T> action) {
        throw new RuntimeException("Stub!");
    }

    public static <T> T doPrivileged(PrivilegedAction<T> action, AccessControlContext context) {
        throw new RuntimeException("Stub!");
    }

    public static <T> T doPrivileged(PrivilegedExceptionAction<T> action) throws PrivilegedActionException {
        throw new RuntimeException("Stub!");
    }

    public static <T> T doPrivileged(PrivilegedExceptionAction<T> action, AccessControlContext context) throws PrivilegedActionException {
        throw new RuntimeException("Stub!");
    }

    public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> action) {
        throw new RuntimeException("Stub!");
    }

    public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> action) throws PrivilegedActionException {
        throw new RuntimeException("Stub!");
    }

    public static void checkPermission(Permission permission) throws AccessControlException {
        throw new RuntimeException("Stub!");
    }

    public static AccessControlContext getContext() {
        throw new RuntimeException("Stub!");
    }
}