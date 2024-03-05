package java.security.cert;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CRL.class */
public abstract class CRL {
    public abstract boolean isRevoked(Certificate certificate);

    public abstract String toString();

    /* JADX INFO: Access modifiers changed from: protected */
    public CRL(String type) {
        throw new RuntimeException("Stub!");
    }

    public final String getType() {
        throw new RuntimeException("Stub!");
    }
}