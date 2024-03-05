package java.security.cert;

import java.util.Collection;
import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PKIXCertPathChecker.class */
public abstract class PKIXCertPathChecker implements Cloneable {
    public abstract void init(boolean z) throws CertPathValidatorException;

    public abstract boolean isForwardCheckingSupported();

    public abstract Set<String> getSupportedExtensions();

    public abstract void check(Certificate certificate, Collection<String> collection) throws CertPathValidatorException;

    protected PKIXCertPathChecker() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() {
        throw new RuntimeException("Stub!");
    }
}