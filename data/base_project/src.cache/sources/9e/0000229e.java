package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LineNumberReader.class */
public class LineNumberReader extends BufferedReader {
    public LineNumberReader(Reader in) {
        super(null, 0);
        throw new RuntimeException("Stub!");
    }

    public LineNumberReader(Reader in, int size) {
        super(null, 0);
        throw new RuntimeException("Stub!");
    }

    public int getLineNumber() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public void mark(int readlimit) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public int read(char[] buffer, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.BufferedReader
    public String readLine() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void setLineNumber(int lineNumber) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public long skip(long charCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}