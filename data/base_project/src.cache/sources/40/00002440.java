package java.security;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DigestInputStream.class */
public class DigestInputStream extends FilterInputStream {
    protected MessageDigest digest;

    public DigestInputStream(InputStream stream, MessageDigest digest) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public MessageDigest getMessageDigest() {
        throw new RuntimeException("Stub!");
    }

    public void setMessageDigest(MessageDigest digest) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void on(boolean on) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}