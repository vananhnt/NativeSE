package java.net;

import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SecureCacheResponse.class */
public abstract class SecureCacheResponse extends CacheResponse {
    public abstract String getCipherSuite();

    public abstract List<Certificate> getLocalCertificateChain();

    public abstract List<Certificate> getServerCertificateChain() throws SSLPeerUnverifiedException;

    public abstract Principal getPeerPrincipal() throws SSLPeerUnverifiedException;

    public abstract Principal getLocalPrincipal();

    public SecureCacheResponse() {
        throw new RuntimeException("Stub!");
    }
}