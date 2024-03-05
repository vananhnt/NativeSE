package java.io;

import java.util.Enumeration;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SequenceInputStream.class */
public class SequenceInputStream extends InputStream {
    public SequenceInputStream(InputStream s1, InputStream s2) {
        throw new RuntimeException("Stub!");
    }

    public SequenceInputStream(Enumeration<? extends InputStream> e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public int read(byte[] buffer, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }
}