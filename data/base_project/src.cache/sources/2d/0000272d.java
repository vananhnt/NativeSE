package java.util.jar;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: JarOutputStream.class */
public class JarOutputStream extends ZipOutputStream {
    public JarOutputStream(OutputStream os, Manifest manifest) throws IOException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public JarOutputStream(OutputStream os) throws IOException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.ZipOutputStream
    public void putNextEntry(ZipEntry ze) throws IOException {
        throw new RuntimeException("Stub!");
    }
}