package javax.net.ssl;

import java.security.cert.CertPathParameters;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CertPathTrustManagerParameters.class */
public class CertPathTrustManagerParameters implements ManagerFactoryParameters {
    private final CertPathParameters param;

    public CertPathTrustManagerParameters(CertPathParameters parameters) {
        this.param = (CertPathParameters) parameters.clone();
    }

    public CertPathParameters getParameters() {
        return (CertPathParameters) this.param.clone();
    }
}