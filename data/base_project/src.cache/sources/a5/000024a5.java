package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Collection;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CertStore.class */
public class CertStore {
    protected CertStore(CertStoreSpi storeSpi, Provider provider, String type, CertStoreParameters params) {
        throw new RuntimeException("Stub!");
    }

    public static CertStore getInstance(String type, CertStoreParameters params) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static CertStore getInstance(String type, CertStoreParameters params, String provider) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static CertStore getInstance(String type, CertStoreParameters params, Provider provider) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    public final String getType() {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public final CertStoreParameters getCertStoreParameters() {
        throw new RuntimeException("Stub!");
    }

    public final Collection<? extends Certificate> getCertificates(CertSelector selector) throws CertStoreException {
        throw new RuntimeException("Stub!");
    }

    public final Collection<? extends CRL> getCRLs(CRLSelector selector) throws CertStoreException {
        throw new RuntimeException("Stub!");
    }

    public static final String getDefaultType() {
        throw new RuntimeException("Stub!");
    }
}