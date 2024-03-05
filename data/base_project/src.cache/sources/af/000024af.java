package java.security.cert;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CertificateFactorySpi.class */
public abstract class CertificateFactorySpi {
    public abstract Certificate engineGenerateCertificate(InputStream inputStream) throws CertificateException;

    public abstract Collection<? extends Certificate> engineGenerateCertificates(InputStream inputStream) throws CertificateException;

    public abstract CRL engineGenerateCRL(InputStream inputStream) throws CRLException;

    public abstract Collection<? extends CRL> engineGenerateCRLs(InputStream inputStream) throws CRLException;

    public CertificateFactorySpi() {
        throw new RuntimeException("Stub!");
    }

    public CertPath engineGenerateCertPath(InputStream inStream) throws CertificateException {
        throw new RuntimeException("Stub!");
    }

    public CertPath engineGenerateCertPath(InputStream inStream, String encoding) throws CertificateException {
        throw new RuntimeException("Stub!");
    }

    public CertPath engineGenerateCertPath(List<? extends Certificate> certificates) throws CertificateException {
        throw new RuntimeException("Stub!");
    }

    public Iterator<String> engineGetCertPathEncodings() {
        throw new RuntimeException("Stub!");
    }
}