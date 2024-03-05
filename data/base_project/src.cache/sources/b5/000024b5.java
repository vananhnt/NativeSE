package java.security.cert;

import java.security.PublicKey;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PKIXCertPathBuilderResult.class */
public class PKIXCertPathBuilderResult extends PKIXCertPathValidatorResult implements CertPathBuilderResult {
    public PKIXCertPathBuilderResult(CertPath certPath, TrustAnchor trustAnchor, PolicyNode policyTree, PublicKey subjectPublicKey) {
        super(null, null, null);
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.cert.CertPathBuilderResult
    public CertPath getCertPath() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.cert.PKIXCertPathValidatorResult
    public String toString() {
        throw new RuntimeException("Stub!");
    }
}