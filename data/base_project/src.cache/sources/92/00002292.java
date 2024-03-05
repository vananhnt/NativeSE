package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FilterOutputStream.class */
public class FilterOutputStream extends OutputStream {
    protected OutputStream out;

    public FilterOutputStream(OutputStream out) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public void write(byte[] buffer, int offset, int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public void write(int oneByte) throws IOException {
        throw new RuntimeException("Stub!");
    }
}