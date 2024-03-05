package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CertPathBuilderSpi.class */
public abstract class CertPathBuilderSpi {
    public abstract CertPathBuilderResult engineBuild(CertPathParameters certPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException;

    public CertPathBuilderSpi() {
        throw new RuntimeException("Stub!");
    }
}