package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PipedInputStream.class */
public class PipedInputStream extends InputStream {
    protected byte[] buffer = null;
    protected int in;
    protected int out;
    protected static final int PIPE_SIZE = 1024;

    public PipedInputStream() {
        throw new RuntimeException("Stub!");
    }

    public PipedInputStream(PipedOutputStream out) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public PipedInputStream(int pipeSize) {
        throw new RuntimeException("Stub!");
    }

    public PipedInputStream(PipedOutputStream out, int pipeSize) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream, java.io.Closeable
    public synchronized void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void connect(PipedOutputStream src) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized int read(byte[] bytes, int offset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected synchronized void receive(int oneByte) throws IOException {
        throw new RuntimeException("Stub!");
    }
}