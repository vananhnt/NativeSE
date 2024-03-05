package java.net;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: JarURLConnection.class */
public abstract class JarURLConnection extends URLConnection {
    protected URLConnection jarFileURLConnection;

    public abstract JarFile getJarFile() throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public JarURLConnection(URL url) throws MalformedURLException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public Attributes getAttributes() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Certificate[] getCertificates() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public String getEntryName() {
        throw new RuntimeException("Stub!");
    }

    public JarEntry getJarEntry() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Manifest getManifest() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public URL getJarFileURL() {
        throw new RuntimeException("Stub!");
    }

    public Attributes getMainAttributes() throws IOException {
        throw new RuntimeException("Stub!");
    }
}