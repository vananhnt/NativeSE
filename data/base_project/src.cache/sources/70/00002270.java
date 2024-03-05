package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BufferedInputStream.class */
public class BufferedInputStream extends FilterInputStream {
    protected volatile byte[] buf;
    protected int count;
    protected int marklimit;
    protected int markpos;
    protected int pos;

    public BufferedInputStream(InputStream in) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public BufferedInputStream(InputStream in, int size) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void mark(int readlimit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized int read(byte[] buffer, int offset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized long skip(long byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}