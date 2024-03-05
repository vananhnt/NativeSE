package java.util.zip;

import java.io.IOException;
import java.io.InputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ZipInputStream.class */
public class ZipInputStream extends InflaterInputStream {
    public ZipInputStream(InputStream stream) {
        super(null, null, 0);
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void closeEntry() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public ZipEntry getNextEntry() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buffer, int offset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected ZipEntry createZipEntry(String name) {
        throw new RuntimeException("Stub!");
    }
}