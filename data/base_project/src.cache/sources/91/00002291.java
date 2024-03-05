package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FilterInputStream.class */
public class FilterInputStream extends InputStream {
    protected volatile InputStream in;

    /* JADX INFO: Access modifiers changed from: protected */
    public FilterInputStream(InputStream in) {
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
    public synchronized void mark(int readlimit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
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

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public long skip(long byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}