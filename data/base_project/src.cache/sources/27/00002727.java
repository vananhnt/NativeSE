package java.util.jar;

import java.io.IOException;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.zip.ZipEntry;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: JarEntry.class */
public class JarEntry extends ZipEntry {
    public JarEntry(String name) {
        super((ZipEntry) null);
        throw new RuntimeException("Stub!");
    }

    public JarEntry(ZipEntry entry) {
        super((ZipEntry) null);
        throw new RuntimeException("Stub!");
    }

    public JarEntry(JarEntry je) {
        super((ZipEntry) null);
        throw new RuntimeException("Stub!");
    }

    public Attributes getAttributes() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Certificate[] getCertificates() {
        throw new RuntimeException("Stub!");
    }

    public CodeSigner[] getCodeSigners() {
        throw new RuntimeException("Stub!");
    }
}