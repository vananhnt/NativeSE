package java.io;

import java.nio.CharBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Reader.class */
public abstract class Reader implements Readable, Closeable {
    protected Object lock;

    public abstract void close() throws IOException;

    public abstract int read(char[] cArr, int i, int i2) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public Reader() {
        throw new RuntimeException("Stub!");
    }

    protected Reader(Object lock) {
        throw new RuntimeException("Stub!");
    }

    public void mark(int readLimit) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public boolean markSupported() {
        throw new RuntimeException("Stub!");
    }

    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int read(char[] buf) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public boolean ready() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public long skip(long charCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Readable
    public int read(CharBuffer target) throws IOException {
        throw new RuntimeException("Stub!");
    }
}