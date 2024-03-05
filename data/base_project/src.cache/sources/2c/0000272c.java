package java.util.jar;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: JarInputStream.class */
public class JarInputStream extends ZipInputStream {
    public JarInputStream(InputStream stream, boolean verify) throws IOException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public JarInputStream(InputStream stream) throws IOException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public Manifest getManifest() {
        throw new RuntimeException("Stub!");
    }

    public JarEntry getNextJarEntry() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.ZipInputStream, java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buffer, int offset, int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.ZipInputStream
    public ZipEntry getNextEntry() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.ZipInputStream
    protected ZipEntry createZipEntry(String name) {
        throw new RuntimeException("Stub!");
    }
}