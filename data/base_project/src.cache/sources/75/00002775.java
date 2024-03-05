package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: InflaterOutputStream.class */
public class InflaterOutputStream extends FilterOutputStream {
    protected final Inflater inf;
    protected final byte[] buf;

    public InflaterOutputStream(OutputStream out) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public InflaterOutputStream(OutputStream out, Inflater inf) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public InflaterOutputStream(OutputStream out, Inflater inf, int bufferSize) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void finish() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException, ZipException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bytes, int offset, int byteCount) throws IOException, ZipException {
        throw new RuntimeException("Stub!");
    }
}