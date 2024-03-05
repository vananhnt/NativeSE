package java.security.cert;

import java.security.GeneralSecurityException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CertPathValidatorException.class */
public class CertPathValidatorException extends GeneralSecurityException {
    public CertPathValidatorException(String msg, Throwable cause, CertPath certPath, int index) {
        throw new RuntimeException("Stub!");
    }

    public CertPathValidatorException(String msg, Throwable cause) {
        throw new RuntimeException("Stub!");
    }

    public CertPathValidatorException(Throwable cause) {
        throw new RuntimeException("Stub!");
    }

    public CertPathValidatorException(String msg) {
        throw new RuntimeException("Stub!");
    }

    public CertPathValidatorException() {
        throw new RuntimeException("Stub!");
    }

    public CertPath getCertPath() {
        throw new RuntimeException("Stub!");
    }

    public int getIndex() {
        throw new RuntimeException("Stub!");
    }
}