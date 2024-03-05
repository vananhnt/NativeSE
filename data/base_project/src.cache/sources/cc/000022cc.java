package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Writer.class */
public abstract class Writer implements Appendable, Closeable, Flushable {
    protected Object lock;

    public abstract void close() throws IOException;

    public abstract void flush() throws IOException;

    public abstract void write(char[] cArr, int i, int i2) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public Writer() {
        throw new RuntimeException("Stub!");
    }

    protected Writer(Object lock) {
        throw new RuntimeException("Stub!");
    }

    public void write(char[] buf) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void write(int oneChar) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void write(String str) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void write(String str, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public Writer append(char c) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public Writer append(CharSequence csq) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        throw new RuntimeException("Stub!");
    }
}