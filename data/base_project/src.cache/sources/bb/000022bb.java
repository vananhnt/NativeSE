package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PushbackInputStream.class */
public class PushbackInputStream extends FilterInputStream {
    protected byte[] buf;
    protected int pos;

    public PushbackInputStream(InputStream in) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public PushbackInputStream(InputStream in, int size) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buffer, int offset, int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void unread(byte[] buffer) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void unread(byte[] buffer, int offset, int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void unread(int oneByte) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void mark(int readlimit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }
}