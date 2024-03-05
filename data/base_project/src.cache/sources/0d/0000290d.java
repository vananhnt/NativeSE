package libcore.net.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import libcore.io.IoUtils;

/* loaded from: FtpURLInputStream.class */
class FtpURLInputStream extends InputStream {
    private InputStream is;
    private Socket controlSocket;

    public FtpURLInputStream(InputStream is, Socket controlSocket) {
        this.is = is;
        this.controlSocket = controlSocket;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        return this.is.read();
    }

    @Override // java.io.InputStream
    public int read(byte[] buf, int off, int nbytes) throws IOException {
        return this.is.read(buf, off, nbytes);
    }

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        this.is.reset();
    }

    @Override // java.io.InputStream
    public synchronized void mark(int limit) {
        this.is.mark(limit);
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return this.is.markSupported();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.AutoCloseable, java.io.InputStream] */
    @Override // java.io.InputStream, java.io.Closeable
    public void close() {
        IoUtils.closeQuietly((AutoCloseable) this.is);
        IoUtils.closeQuietly(this.controlSocket);
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return this.is.available();
    }

    @Override // java.io.InputStream
    public long skip(long byteCount) throws IOException {
        return this.is.skip(byteCount);
    }
}