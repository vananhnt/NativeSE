package javax.net.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import java.util.EventObject;
import javax.security.cert.X509Certificate;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HandshakeCompletedEvent.class */
public class HandshakeCompletedEvent extends EventObject {
    private transient SSLSession session;

    public HandshakeCompletedEvent(SSLSocket sock, SSLSession s) {
        super(sock);
        this.session = s;
    }

    public SSLSession getSession() {
        return this.session;
    }

    public String getCipherSuite() {
        return this.session.getCipherSuite();
    }

    public Certificate[] getLocalCertificates() {
        return this.session.getLocalCertificates();
    }

    public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
        return this.session.getPeerCertificates();
    }

    public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
        return this.session.getPeerCertificateChain();
    }

    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        return this.session.getPeerPrincipal();
    }

    public Principal getLocalPrincipal() {
        return this.session.getLocalPrincipal();
    }

    public SSLSocket getSocket() {
        return (SSLSocket) this.source;
    }
}