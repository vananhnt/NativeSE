package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: OutputStream.class */
public abstract class OutputStream implements Closeable, Flushable {
    public abstract void write(int i) throws IOException;

    public OutputStream() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void write(byte[] buffer) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void write(byte[] buffer, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }
}