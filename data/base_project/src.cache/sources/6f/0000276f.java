package java.util.zip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DeflaterInputStream.class */
public class DeflaterInputStream extends FilterInputStream {
    protected final Deflater def;
    protected final byte[] buf;

    public DeflaterInputStream(InputStream in) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public DeflaterInputStream(InputStream in, Deflater deflater) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public DeflaterInputStream(InputStream in, Deflater deflater, int bufferSize) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
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

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void mark(int limit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }
}