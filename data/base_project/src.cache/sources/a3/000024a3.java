package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CertPathValidatorSpi.class */
public abstract class CertPathValidatorSpi {
    public abstract CertPathValidatorResult engineValidate(CertPath certPath, CertPathParameters certPathParameters) throws CertPathValidatorException, InvalidAlgorithmParameterException;

    public CertPathValidatorSpi() {
        throw new RuntimeException("Stub!");
    }
}