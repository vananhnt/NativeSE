package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ByteArrayInputStream.class */
public class ByteArrayInputStream extends InputStream {
    protected byte[] buf = null;
    protected int pos;
    protected int mark;
    protected int count;

    public ByteArrayInputStream(byte[] buf) {
        throw new RuntimeException("Stub!");
    }

    public ByteArrayInputStream(byte[] buf, int offset, int length) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized int available() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized void mark(int readlimit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized int read() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized int read(byte[] buffer, int offset, int length) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized void reset() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized long skip(long byteCount) {
        throw new RuntimeException("Stub!");
    }
}