package org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: StrictHostnameVerifier.class */
public class StrictHostnameVerifier extends AbstractVerifier {
    public StrictHostnameVerifier() {
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