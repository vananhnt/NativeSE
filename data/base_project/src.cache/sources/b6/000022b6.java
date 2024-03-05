package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PipedOutputStream.class */
public class PipedOutputStream extends OutputStream {
    public PipedOutputStream() {
        throw new RuntimeException("Stub!");
    }

    public PipedOutputStream(PipedInputStream target) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void connect(PipedInputStream stream) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public void write(byte[] buffer, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public void write(int oneByte) throws IOException {
        throw new RuntimeException("Stub!");
    }
}