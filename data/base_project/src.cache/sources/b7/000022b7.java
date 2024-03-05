package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PipedReader.class */
public class PipedReader extends Reader {
    public PipedReader() {
        throw new RuntimeException("Stub!");
    }

    public PipedReader(PipedWriter out) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public PipedReader(int pipeSize) {
        throw new RuntimeException("Stub!");
    }

    public PipedReader(PipedWriter out, int pipeSize) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader, java.io.Closeable
    public synchronized void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void connect(PipedWriter src) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public synchronized int read(char[] buffer, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public synchronized boolean ready() throws IOException {
        throw new RuntimeException("Stub!");
    }
}