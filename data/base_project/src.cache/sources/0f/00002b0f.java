package org.apache.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: StringEntity.class */
public class StringEntity extends AbstractHttpEntity implements Cloneable {
    protected final byte[] content = null;

    public StringEntity(String s, String charset) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public StringEntity(String s) throws UnsupportedEncodingException {
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
    public InputStream getContent() throws IOException {
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