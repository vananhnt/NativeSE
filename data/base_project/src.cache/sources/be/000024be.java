package java.security.cert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.security.auth.x500.X500Principal;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: X509CRLSelector.class */
public class X509CRLSelector implements CRLSelector {
    public X509CRLSelector() {
        throw new RuntimeException("Stub!");
    }

    public void setIssuers(Collection<X500Principal> issuers) {
        throw new RuntimeException("Stub!");
    }

    public void setIssuerNames(Collection<?> names) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void addIssuer(X500Principal issuer) {
        throw new RuntimeException("Stub!");
    }

    public void addIssuerName(String iss_name) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void addIssuerName(byte[] iss_name) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void setMinCRLNumber(BigInteger minCRL) {
        throw new RuntimeException("Stub!");
    }

    public void setMaxCRLNumber(BigInteger maxCRL) {
        throw new RuntimeException("Stub!");
    }

    public void setDateAndTime(Date dateAndTime) {
        throw new RuntimeException("Stub!");
    }

    public void setCertificateChecking(X509Certificate cert) {
        throw new RuntimeException("Stub!");
    }

    public Collection<X500Principal> getIssuers() {
        throw new RuntimeException("Stub!");
    }

    public Collection<Object> getIssuerNames() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger getMinCRL() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger getMaxCRL() {
        throw new RuntimeException("Stub!");
    }

    public Date getDateAndTime() {
        throw new RuntimeException("Stub!");
    }

    public X509Certificate getCertificateChecking() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.cert.CRLSelector
    public boolean match(CRL crl) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.cert.CRLSelector
    public Object clone() {
        throw new RuntimeException("Stub!");
    }
}