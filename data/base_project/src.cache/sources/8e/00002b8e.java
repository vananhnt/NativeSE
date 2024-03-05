package org.apache.http.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.io.SessionOutputBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: IdentityOutputStream.class */
public class IdentityOutputStream extends OutputStream {
    public IdentityOutputStream(SessionOutputBuffer out) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        throw new RuntimeException("Stub!");
    }
}