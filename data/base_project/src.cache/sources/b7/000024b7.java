package java.security.cert;

import java.security.PublicKey;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PKIXCertPathValidatorResult.class */
public class PKIXCertPathValidatorResult implements CertPathValidatorResult {
    public PKIXCertPathValidatorResult(TrustAnchor trustAnchor, PolicyNode policyTree, PublicKey subjectPublicKey) {
        throw new RuntimeException("Stub!");
    }

    public PolicyNode getPolicyTree() {
        throw new RuntimeException("Stub!");
    }

    public PublicKey getPublicKey() {
        throw new RuntimeException("Stub!");
    }

    public TrustAnchor getTrustAnchor() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.cert.CertPathValidatorResult
    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}