package java.security;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ProtectionDomain.class */
public class ProtectionDomain {
    public ProtectionDomain(CodeSource cs, PermissionCollection permissions) {
        throw new RuntimeException("Stub!");
    }

    public ProtectionDomain(CodeSource cs, PermissionCollection permissions, ClassLoader cl, Principal[] principals) {
        throw new RuntimeException("Stub!");
    }

    public final ClassLoader getClassLoader() {
        throw new RuntimeException("Stub!");
    }

    public final CodeSource getCodeSource() {
        throw new RuntimeException("Stub!");
    }

    public final PermissionCollection getPermissions() {
        throw new RuntimeException("Stub!");
    }

    public final Principal[] getPrincipals() {
        throw new RuntimeException("Stub!");
    }

    public boolean implies(Permission permission) {
        throw new RuntimeException("Stub!");
    }
}