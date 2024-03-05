package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DeflaterOutputStream.class */
public class DeflaterOutputStream extends FilterOutputStream {
    protected byte[] buf;
    protected Deflater def;

    public DeflaterOutputStream(OutputStream os, Deflater def) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public DeflaterOutputStream(OutputStream os) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    public DeflaterOutputStream(OutputStream os, Deflater def, int bufferSize) {
        super(null);
        this.buf = null;
        throw new RuntimeException("Stub!");
    }

    protected void deflate() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void finish() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int i) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] buffer, int offset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }
}