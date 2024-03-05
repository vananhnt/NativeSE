package java.util.zip;

import java.io.IOException;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: GZIPOutputStream.class */
public class GZIPOutputStream extends DeflaterOutputStream {
    protected CRC32 crc;

    public GZIPOutputStream(OutputStream os) throws IOException {
        super(null, null, 0);
        throw new RuntimeException("Stub!");
    }

    public GZIPOutputStream(OutputStream os, int size) throws IOException {
        super(null, null, 0);
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.DeflaterOutputStream
    public void finish() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.DeflaterOutputStream, java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] buffer, int off, int nbytes) throws IOException {
        throw new RuntimeException("Stub!");
    }
}