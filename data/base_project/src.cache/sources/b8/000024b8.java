package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PKIXParameters.class */
public class PKIXParameters implements CertPathParameters {
    public PKIXParameters(Set<TrustAnchor> trustAnchors) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    public PKIXParameters(KeyStore keyStore) throws KeyStoreException, InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    public Set<TrustAnchor> getTrustAnchors() {
        throw new RuntimeException("Stub!");
    }

    public void setTrustAnchors(Set<TrustAnchor> trustAnchors) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    public boolean isAnyPolicyInhibited() {
        throw new RuntimeException("Stub!");
    }

    public void setAnyPolicyInhibited(boolean anyPolicyInhibited) {
        throw new RuntimeException("Stub!");
    }

    public List<PKIXCertPathChecker> getCertPathCheckers() {
        throw new RuntimeException("Stub!");
    }

    public void setCertPathCheckers(List<PKIXCertPathChecker> certPathCheckers) {
        throw new RuntimeException("Stub!");
    }

    public void addCertPathChecker(PKIXCertPathChecker checker) {
        throw new RuntimeException("Stub!");
    }

    public List<CertStore> getCertStores() {
        throw new RuntimeException("Stub!");
    }

    public void setCertStores(List<CertStore> certStores) {
        throw new RuntimeException("Stub!");
    }

    public void addCertStore(CertStore store) {
        throw new RuntimeException("Stub!");
    }

    public Date getDate() {
        throw new RuntimeException("Stub!");
    }

    public void setDate(Date date) {
        throw new RuntimeException("Stub!");
    }

    public boolean isExplicitPolicyRequired() {
        throw new RuntimeException("Stub!");
    }

    public void setExplicitPolicyRequired(boolean explicitPolicyRequired) {
        throw new RuntimeException("Stub!");
    }

    public Set<String> getInitialPolicies() {
        throw new RuntimeException("Stub!");
    }

    public void setInitialPolicies(Set<String> initialPolicies) {
        throw new RuntimeException("Stub!");
    }

    public boolean isPolicyMappingInhibited() {
        throw new RuntimeException("Stub!");
    }

    public void setPolicyMappingInhibited(boolean policyMappingInhibited) {
        throw new RuntimeException("Stub!");
    }

    public boolean getPolicyQualifiersRejected() {
        throw new RuntimeException("Stub!");
    }

    public void setPolicyQualifiersRejected(boolean policyQualifiersRejected) {
        throw new RuntimeException("Stub!");
    }

    public boolean isRevocationEnabled() {
        throw new RuntimeException("Stub!");
    }

    public void setRevocationEnabled(boolean revocationEnabled) {
        throw new RuntimeException("Stub!");
    }

    public String getSigProvider() {
        throw new RuntimeException("Stub!");
    }

    public void setSigProvider(String sigProvider) {
        throw new RuntimeException("Stub!");
    }

    public CertSelector getTargetCertConstraints() {
        throw new RuntimeException("Stub!");
    }

    public void setTargetCertConstraints(CertSelector targetCertConstraints) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.cert.CertPathParameters
    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}