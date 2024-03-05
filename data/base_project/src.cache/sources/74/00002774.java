package java.util.zip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: InflaterInputStream.class */
public class InflaterInputStream extends FilterInputStream {
    protected Inflater inf;
    protected byte[] buf;
    protected int len;

    public InflaterInputStream(InputStream is) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public InflaterInputStream(InputStream is, Inflater inflater) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public InflaterInputStream(InputStream is, Inflater inflater, int bufferSize) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buffer, int offset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void fill() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void mark(int readlimit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        throw new RuntimeException("Stub!");
    }
}