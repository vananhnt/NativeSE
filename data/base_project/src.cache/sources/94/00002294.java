package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FilterWriter.class */
public abstract class FilterWriter extends Writer {
    protected Writer out;

    protected FilterWriter(Writer out) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(char[] buffer, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(int oneChar) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(String str, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }
}