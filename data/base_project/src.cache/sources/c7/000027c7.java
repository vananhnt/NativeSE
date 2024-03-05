package javax.net.ssl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpsURLConnection.class */
public abstract class HttpsURLConnection extends HttpURLConnection {
    protected HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;

    public abstract String getCipherSuite();

    public abstract Certificate[] getLocalCertificates();

    public abstract Certificate[] getServerCertificates() throws SSLPeerUnverifiedException;

    /* loaded from: HttpsURLConnection$NoPreloadHolder.class */
    private static class NoPreloadHolder {
        public static HostnameVerifier defaultHostnameVerifier = new DefaultHostnameVerifier();
        public static SSLSocketFactory defaultSSLSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        private NoPreloadHolder() {
        }
    }

    public static void setDefaultHostnameVerifier(HostnameVerifier v) {
        if (v == null) {
            throw new IllegalArgumentException("HostnameVerifier is null");
        }
        NoPreloadHolder.defaultHostnameVerifier = v;
    }

    public static HostnameVerifier getDefaultHostnameVerifier() {
        return NoPreloadHolder.defaultHostnameVerifier;
    }

    public static void setDefaultSSLSocketFactory(SSLSocketFactory sf) {
        if (sf == null) {
            throw new IllegalArgumentException("SSLSocketFactory is null");
        }
        NoPreloadHolder.defaultSSLSocketFactory = sf;
    }

    public static SSLSocketFactory getDefaultSSLSocketFactory() {
        return NoPreloadHolder.defaultSSLSocketFactory;
    }

    protected HttpsURLConnection(URL url) {
        super(url);
        this.hostnameVerifier = NoPreloadHolder.defaultHostnameVerifier;
        this.sslSocketFactory = NoPreloadHolder.defaultSSLSocketFactory;
    }

    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        Certificate[] certs = getServerCertificates();
        if (certs == null || certs.length == 0 || !(certs[0] instanceof X509Certificate)) {
            throw new SSLPeerUnverifiedException("No server's end-entity certificate");
        }
        return ((X509Certificate) certs[0]).getSubjectX500Principal();
    }

    public Principal getLocalPrincipal() {
        Certificate[] certs = getLocalCertificates();
        if (certs == null || certs.length == 0 || !(certs[0] instanceof X509Certificate)) {
            return null;
        }
        return ((X509Certificate) certs[0]).getSubjectX500Principal();
    }

    public void setHostnameVerifier(HostnameVerifier v) {
        if (v == null) {
            throw new IllegalArgumentException("HostnameVerifier is null");
        }
        this.hostnameVerifier = v;
    }

    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }

    public void setSSLSocketFactory(SSLSocketFactory sf) {
        if (sf == null) {
            throw new IllegalArgumentException("SSLSocketFactory is null");
        }
        this.sslSocketFactory = sf;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return this.sslSocketFactory;
    }
}