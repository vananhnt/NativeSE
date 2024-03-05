package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CharArrayReader.class */
public class CharArrayReader extends Reader {
    protected char[] buf = null;
    protected int pos;
    protected int markedPos;
    protected int count;

    public CharArrayReader(char[] buf) {
        throw new RuntimeException("Stub!");
    }

    public CharArrayReader(char[] buf, int offset, int length) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader, java.io.Closeable
    public void close() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public void mark(int readLimit) throws IOException {
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
    public int read(char[] buffer, int offset, int len) throws IOException {
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