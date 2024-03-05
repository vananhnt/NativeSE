package java.security;

import java.io.Serializable;
import java.net.URL;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CodeSource.class */
public class CodeSource implements Serializable {
    public CodeSource(URL location, java.security.cert.Certificate[] certs) {
        throw new RuntimeException("Stub!");
    }

    public CodeSource(URL location, CodeSigner[] signers) {
        throw new RuntimeException("Stub!");
    }

    public final java.security.cert.Certificate[] getCertificates() {
        throw new RuntimeException("Stub!");
    }

    public final CodeSigner[] getCodeSigners() {
        throw new RuntimeException("Stub!");
    }

    public final URL getLocation() {
        throw new RuntimeException("Stub!");
    }

    public boolean implies(CodeSource cs) {
        throw new RuntimeException("Stub!");
    }
}