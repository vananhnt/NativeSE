package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FilterReader.class */
public abstract class FilterReader extends Reader {
    protected Reader in;

    /* JADX INFO: Access modifiers changed from: protected */
    public FilterReader(Reader in) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public synchronized void mark(int readlimit) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public boolean markSupported() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public int read(char[] buffer, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public boolean ready() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public long skip(long charCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}