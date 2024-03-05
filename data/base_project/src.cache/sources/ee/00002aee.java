package org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BrowserCompatHostnameVerifier.class */
public class BrowserCompatHostnameVerifier extends AbstractVerifier {
    public BrowserCompatHostnameVerifier() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ssl.X509HostnameVerifier
    public final void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
        throw new RuntimeException("Stub!");
    }

    public final String toString() {
        throw new RuntimeException("Stub!");
    }
}