package org.apache.http.conn.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLSocketFactory.class */
public class SSLSocketFactory implements LayeredSocketFactory {
    public static final String TLS = "TLS";
    public static final String SSL = "SSL";
    public static final String SSLV2 = "SSLv2";
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = null;
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = null;
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = null;

    public SSLSocketFactory(String algorithm, KeyStore keystore, String keystorePassword, KeyStore truststore, SecureRandom random, HostNameResolver nameResolver) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        throw new RuntimeException("Stub!");
    }

    public SSLSocketFactory(KeyStore keystore, String keystorePassword, KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        throw new RuntimeException("Stub!");
    }

    public SSLSocketFactory(KeyStore keystore, String keystorePassword) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        throw new RuntimeException("Stub!");
    }

    public SSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        throw new RuntimeException("Stub!");
    }

    public static SSLSocketFactory getSocketFactory() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.scheme.SocketFactory
    public Socket createSocket() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.scheme.SocketFactory
    public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.scheme.SocketFactory
    public boolean isSecure(Socket sock) throws IllegalArgumentException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.scheme.LayeredSocketFactory
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        throw new RuntimeException("Stub!");
    }

    public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
        throw new RuntimeException("Stub!");
    }

    public X509HostnameVerifier getHostnameVerifier() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: SSLSocketFactory$NoPreloadHolder.class */
    private static class NoPreloadHolder {
        private static final SSLSocketFactory DEFAULT_FACTORY = new SSLSocketFactory((AnonymousClass1) null);

        private NoPreloadHolder() {
        }
    }
}