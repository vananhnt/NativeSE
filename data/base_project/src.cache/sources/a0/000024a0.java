package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CertPathValidator.class */
public class CertPathValidator {
    protected CertPathValidator(CertPathValidatorSpi validatorSpi, Provider provider, String algorithm) {
        throw new RuntimeException("Stub!");
    }

    public final String getAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public static CertPathValidator getInstance(String algorithm) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static CertPathValidator getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static CertPathValidator getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public final CertPathValidatorResult validate(CertPath certPath, CertPathParameters params) throws CertPathValidatorException, InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    public static final String getDefaultType() {
        throw new RuntimeException("Stub!");
    }
}