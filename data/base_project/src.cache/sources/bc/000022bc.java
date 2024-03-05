package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PushbackReader.class */
public class PushbackReader extends FilterReader {
    public PushbackReader(Reader in) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public PushbackReader(Reader in, int size) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterReader, java.io.Reader, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterReader, java.io.Reader
    public void mark(int readAheadLimit) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterReader, java.io.Reader
    public boolean markSupported() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read(char[] buffer, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterReader, java.io.Reader
    public boolean ready() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterReader, java.io.Reader
    public void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void unread(char[] buffer) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void unread(char[] buffer, int offset, int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void unread(int oneChar) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterReader, java.io.Reader
    public long skip(long charCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}