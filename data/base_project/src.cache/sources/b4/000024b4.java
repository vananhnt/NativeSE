package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PKIXBuilderParameters.class */
public class PKIXBuilderParameters extends PKIXParameters {
    public PKIXBuilderParameters(Set<TrustAnchor> trustAnchors, CertSelector targetConstraints) throws InvalidAlgorithmParameterException {
        super((Set<TrustAnchor>) null);
        throw new RuntimeException("Stub!");
    }

    public PKIXBuilderParameters(KeyStore keyStore, CertSelector targetConstraints) throws KeyStoreException, InvalidAlgorithmParameterException {
        super((KeyStore) null);
        throw new RuntimeException("Stub!");
    }

    public int getMaxPathLength() {
        throw new RuntimeException("Stub!");
    }

    public void setMaxPathLength(int maxPathLength) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.cert.PKIXParameters
    public String toString() {
        throw new RuntimeException("Stub!");
    }
}