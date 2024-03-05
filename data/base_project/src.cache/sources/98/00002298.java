package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: InputStream.class */
public abstract class InputStream implements Closeable {
    public abstract int read() throws IOException;

    public InputStream() {
        throw new RuntimeException("Stub!");
    }

    public int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void mark(int readlimit) {
        throw new RuntimeException("Stub!");
    }

    public boolean markSupported() {
        throw new RuntimeException("Stub!");
    }

    public int read(byte[] buffer) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int read(byte[] buffer, int offset, int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public long skip(long byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}