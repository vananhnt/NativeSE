package java.security;

/* JADX WARN: Classes with same name are omitted:
  
 */
@Deprecated
/* loaded from: Signer.class */
public abstract class Signer extends Identity {
    protected Signer() {
        throw new RuntimeException("Stub!");
    }

    public Signer(String name) {
        throw new RuntimeException("Stub!");
    }

    public Signer(String name, IdentityScope scope) throws KeyManagementException {
        throw new RuntimeException("Stub!");
    }

    public PrivateKey getPrivateKey() {
        throw new RuntimeException("Stub!");
    }

    public final void setKeyPair(KeyPair pair) throws InvalidParameterException, KeyException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.Identity, java.security.Principal
    public String toString() {
        throw new RuntimeException("Stub!");
    }
}