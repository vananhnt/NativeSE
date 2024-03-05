package java.util.zip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CheckedInputStream.class */
public class CheckedInputStream extends FilterInputStream {
    public CheckedInputStream(InputStream is, Checksum csum) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buf, int off, int nbytes) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Checksum getChecksum() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}