package java.security;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DigestOutputStream.class */
public class DigestOutputStream extends FilterOutputStream {
    protected MessageDigest digest;

    public DigestOutputStream(OutputStream stream, MessageDigest digest) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public MessageDigest getMessageDigest() {
        throw new RuntimeException("Stub!");
    }

    public void setMessageDigest(MessageDigest digest) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void on(boolean on) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}