package java.util.zip;

import java.io.IOException;
import java.io.InputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: GZIPInputStream.class */
public class GZIPInputStream extends InflaterInputStream {
    public static final int GZIP_MAGIC = 35615;
    protected CRC32 crc;
    protected boolean eos;

    public GZIPInputStream(InputStream is) throws IOException {
        super(null, null, 0);
        throw new RuntimeException("Stub!");
    }

    public GZIPInputStream(InputStream is, int size) throws IOException {
        super(null, null, 0);
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buffer, int offset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}