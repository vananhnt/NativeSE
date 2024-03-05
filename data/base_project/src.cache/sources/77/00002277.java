package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CharArrayWriter.class */
public class CharArrayWriter extends Writer {
    protected char[] buf = null;
    protected int count;

    public CharArrayWriter() {
        throw new RuntimeException("Stub!");
    }

    public CharArrayWriter(int initialSize) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.io.Closeable
    public void close() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() {
        throw new RuntimeException("Stub!");
    }

    public void reset() {
        throw new RuntimeException("Stub!");
    }

    public int size() {
        throw new RuntimeException("Stub!");
    }

    public char[] toCharArray() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(char[] buffer, int offset, int len) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(int oneChar) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(String str, int offset, int count) {
        throw new RuntimeException("Stub!");
    }

    public void writeTo(Writer out) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.lang.Appendable
    public CharArrayWriter append(char c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.lang.Appendable
    public CharArrayWriter append(CharSequence csq) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.lang.Appendable
    public CharArrayWriter append(CharSequence csq, int start, int end) {
        throw new RuntimeException("Stub!");
    }
}