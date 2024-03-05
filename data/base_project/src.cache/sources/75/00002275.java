package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ByteArrayOutputStream.class */
public class ByteArrayOutputStream extends OutputStream {
    protected byte[] buf = null;
    protected int count;

    public ByteArrayOutputStream() {
        throw new RuntimeException("Stub!");
    }

    public ByteArrayOutputStream(int size) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void reset() {
        throw new RuntimeException("Stub!");
    }

    public int size() {
        throw new RuntimeException("Stub!");
    }

    public synchronized byte[] toByteArray() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public String toString(int hibyte) {
        throw new RuntimeException("Stub!");
    }

    public String toString(String charsetName) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public synchronized void write(byte[] buffer, int offset, int len) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public synchronized void write(int oneByte) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        throw new RuntimeException("Stub!");
    }
}