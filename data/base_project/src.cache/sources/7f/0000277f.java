package java.util.zip;

import java.io.IOException;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ZipOutputStream.class */
public class ZipOutputStream extends DeflaterOutputStream {
    public static final int DEFLATED = 8;
    public static final int STORED = 0;

    public ZipOutputStream(OutputStream os) {
        super(null, null, 0);
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.DeflaterOutputStream, java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void closeEntry() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.DeflaterOutputStream
    public void finish() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void putNextEntry(ZipEntry ze) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void setComment(String comment) {
        throw new RuntimeException("Stub!");
    }

    public void setLevel(int level) {
        throw new RuntimeException("Stub!");
    }

    public void setMethod(int method) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.DeflaterOutputStream, java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] buffer, int offset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}