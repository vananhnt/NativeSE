package java.net;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: URLStreamHandler.class */
public abstract class URLStreamHandler {
    protected abstract URLConnection openConnection(URL url) throws IOException;

    public URLStreamHandler() {
        throw new RuntimeException("Stub!");
    }

    protected URLConnection openConnection(URL u, Proxy proxy) throws IOException {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void parseURL(URL url, String spec, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    protected void setURL(URL u, String protocol, String host, int port, String file, String ref) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String path, String query, String ref) {
        throw new RuntimeException("Stub!");
    }

    protected String toExternalForm(URL url) {
        throw new RuntimeException("Stub!");
    }

    protected boolean equals(URL a, URL b) {
        throw new RuntimeException("Stub!");
    }

    protected int getDefaultPort() {
        throw new RuntimeException("Stub!");
    }

    protected InetAddress getHostAddress(URL url) {
        throw new RuntimeException("Stub!");
    }

    protected int hashCode(URL url) {
        throw new RuntimeException("Stub!");
    }

    protected boolean hostsEqual(URL a, URL b) {
        throw new RuntimeException("Stub!");
    }

    protected boolean sameFile(URL a, URL b) {
        throw new RuntimeException("Stub!");
    }
}