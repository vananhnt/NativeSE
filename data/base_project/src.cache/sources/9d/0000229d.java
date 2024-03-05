package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
@Deprecated
/* loaded from: LineNumberInputStream.class */
public class LineNumberInputStream extends FilterInputStream {
    public LineNumberInputStream(InputStream in) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int getLineNumber() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void mark(int readlimit) {
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
    public void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void setLineNumber(int lineNumber) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}