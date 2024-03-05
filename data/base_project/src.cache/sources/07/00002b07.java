package org.apache.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ByteArrayEntity.class */
public class ByteArrayEntity extends AbstractHttpEntity implements Cloneable {
    protected final byte[] content = null;

    public ByteArrayEntity(byte[] b) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public boolean isRepeatable() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public long getContentLength() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public InputStream getContent() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public void writeTo(OutputStream outstream) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public boolean isStreaming() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }
}