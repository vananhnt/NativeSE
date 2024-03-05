package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
@Deprecated
/* loaded from: StringBufferInputStream.class */
public class StringBufferInputStream extends InputStream {
    protected String buffer;
    protected int count;
    protected int pos;

    public StringBufferInputStream(String str) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized int available() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized int read() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized int read(byte[] buffer, int offset, int length) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized void reset() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public synchronized long skip(long charCount) {
        throw new RuntimeException("Stub!");
    }
}