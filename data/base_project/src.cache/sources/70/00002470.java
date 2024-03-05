package java.security;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Policy.class */
public abstract class Policy {
    public static final PermissionCollection UNSUPPORTED_EMPTY_COLLECTION = null;

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Policy$Parameters.class */
    public interface Parameters {
    }

    public Policy() {
        throw new RuntimeException("Stub!");
    }

    public static Policy getInstance(String type, Parameters params) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static Policy getInstance(String type, Parameters params, String provider) throws NoSuchProviderException, NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static Policy getInstance(String type, Parameters params, Provider provider) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public Parameters getParameters() {
        throw new RuntimeException("Stub!");
    }

    public Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public String getType() {
        throw new RuntimeException("Stub!");
    }

    public PermissionCollection getPermissions(CodeSource cs) {
        throw new RuntimeException("Stub!");
    }

    public void refresh() {
        throw new RuntimeException("Stub!");
    }

    public PermissionCollection getPermissions(ProtectionDomain domain) {
        throw new RuntimeException("Stub!");
    }

    public boolean implies(ProtectionDomain domain, Permission permission) {
        throw new RuntimeException("Stub!");
    }

    public static Policy getPolicy() {
        throw new RuntimeException("Stub!");
    }

    public static void setPolicy(Policy policy) {
        throw new RuntimeException("Stub!");
    }
}