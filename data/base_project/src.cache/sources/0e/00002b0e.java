package org.apache.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SerializableEntity.class */
public class SerializableEntity extends AbstractHttpEntity {
    public SerializableEntity(Serializable ser, boolean bufferize) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public InputStream getContent() throws IOException, IllegalStateException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public long getContentLength() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public boolean isRepeatable() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public boolean isStreaming() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public void writeTo(OutputStream outstream) throws IOException {
        throw new RuntimeException("Stub!");
    }
}