package java.security.cert;

import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CertificateFactory.class */
public class CertificateFactory {
    protected CertificateFactory(CertificateFactorySpi certFacSpi, Provider provider, String type) {
        throw new RuntimeException("Stub!");
    }

    public static final CertificateFactory getInstance(String type) throws CertificateException {
        throw new RuntimeException("Stub!");
    }

    public static final CertificateFactory getInstance(String type, String provider) throws CertificateException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static final CertificateFactory getInstance(String type, Provider provider) throws CertificateException {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public final String getType() {
        throw new RuntimeException("Stub!");
    }

    public final Certificate generateCertificate(InputStream inStream) throws CertificateException {
        throw new RuntimeException("Stub!");
    }

    public final Iterator<String> getCertPathEncodings() {
        throw new RuntimeException("Stub!");
    }

    public final CertPath generateCertPath(InputStream inStream) throws CertificateException {
        throw new RuntimeException("Stub!");
    }

    public final CertPath generateCertPath(InputStream inputStream, String encoding) throws CertificateException {
        throw new RuntimeException("Stub!");
    }

    public final CertPath generateCertPath(List<? extends Certificate> certificates) throws CertificateException {
        throw new RuntimeException("Stub!");
    }

    public final Collection<? extends Certificate> generateCertificates(InputStream inStream) throws CertificateException {
        throw new RuntimeException("Stub!");
    }

    public final CRL generateCRL(InputStream inStream) throws CRLException {
        throw new RuntimeException("Stub!");
    }

    public final Collection<? extends CRL> generateCRLs(InputStream inStream) throws CRLException {
        throw new RuntimeException("Stub!");
    }
}