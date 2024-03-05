package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CheckedOutputStream.class */
public class CheckedOutputStream extends FilterOutputStream {
    public CheckedOutputStream(OutputStream os, Checksum cs) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public Checksum getChecksum() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int val) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] buf, int off, int nbytes) throws IOException {
        throw new RuntimeException("Stub!");
    }
}