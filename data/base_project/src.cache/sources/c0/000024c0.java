package java.security.cert;

import java.math.BigInteger;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.security.auth.x500.X500Principal;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: X509Certificate.class */
public abstract class X509Certificate extends Certificate implements X509Extension {
    public abstract void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException;

    public abstract void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException;

    public abstract int getVersion();

    public abstract BigInteger getSerialNumber();

    public abstract Principal getIssuerDN();

    public abstract Principal getSubjectDN();

    public abstract Date getNotBefore();

    public abstract Date getNotAfter();

    public abstract byte[] getTBSCertificate() throws CertificateEncodingException;

    public abstract byte[] getSignature();

    public abstract String getSigAlgName();

    public abstract String getSigAlgOID();

    public abstract byte[] getSigAlgParams();

    public abstract boolean[] getIssuerUniqueID();

    public abstract boolean[] getSubjectUniqueID();

    public abstract boolean[] getKeyUsage();

    public abstract int getBasicConstraints();

    /* JADX INFO: Access modifiers changed from: protected */
    public X509Certificate() {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public X500Principal getIssuerX500Principal() {
        throw new RuntimeException("Stub!");
    }

    public X500Principal getSubjectX500Principal() {
        throw new RuntimeException("Stub!");
    }

    public List<String> getExtendedKeyUsage() throws CertificateParsingException {
        throw new RuntimeException("Stub!");
    }

    public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
        throw new RuntimeException("Stub!");
    }

    public Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException {
        throw new RuntimeException("Stub!");
    }
}