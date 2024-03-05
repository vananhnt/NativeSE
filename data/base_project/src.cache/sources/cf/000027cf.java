package javax.net.ssl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.SecureRandom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLContextSpi.class */
public abstract class SSLContextSpi {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(KeyManager[] keyManagerArr, TrustManager[] trustManagerArr, SecureRandom secureRandom) throws KeyManagementException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract SSLSocketFactory engineGetSocketFactory();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract SSLServerSocketFactory engineGetServerSocketFactory();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract SSLEngine engineCreateSSLEngine(String str, int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract SSLEngine engineCreateSSLEngine();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract SSLSessionContext engineGetServerSessionContext();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract SSLSessionContext engineGetClientSessionContext();

    /* JADX INFO: Access modifiers changed from: protected */
    public SSLParameters engineGetDefaultSSLParameters() {
        return createSSLParameters(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SSLParameters engineGetSupportedSSLParameters() {
        return createSSLParameters(true);
    }

    private SSLParameters createSSLParameters(boolean supported) {
        String[] cipherSuites;
        String[] protocols;
        try {
            SSLSocket s = (SSLSocket) engineGetSocketFactory().createSocket();
            SSLParameters p = new SSLParameters();
            if (supported) {
                cipherSuites = s.getSupportedCipherSuites();
                protocols = s.getSupportedProtocols();
            } else {
                cipherSuites = s.getEnabledCipherSuites();
                protocols = s.getEnabledProtocols();
            }
            p.setCipherSuites(cipherSuites);
            p.setProtocols(protocols);
            p.setNeedClientAuth(s.getNeedClientAuth());
            p.setWantClientAuth(s.getWantClientAuth());
            return p;
        } catch (IOException e) {
            throw new UnsupportedOperationException("Could not access supported SSL parameters");
        }
    }
}