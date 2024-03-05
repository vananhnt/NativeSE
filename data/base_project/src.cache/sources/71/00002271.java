package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BufferedOutputStream.class */
public class BufferedOutputStream extends FilterOutputStream {
    protected byte[] buf;
    protected int count;

    public BufferedOutputStream(OutputStream out) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public BufferedOutputStream(OutputStream out, int size) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public synchronized void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public synchronized void write(byte[] buffer, int offset, int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable
    public synchronized void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public synchronized void write(int oneByte) throws IOException {
        throw new RuntimeException("Stub!");
    }
}