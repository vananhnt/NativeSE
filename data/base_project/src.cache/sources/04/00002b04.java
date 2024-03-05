package org.apache.http.entity;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractHttpEntity.class */
public abstract class AbstractHttpEntity implements HttpEntity {
    protected Header contentType;
    protected Header contentEncoding;
    protected boolean chunked;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractHttpEntity() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public Header getContentType() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public Header getContentEncoding() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public boolean isChunked() {
        throw new RuntimeException("Stub!");
    }

    public void setContentType(Header contentType) {
        throw new RuntimeException("Stub!");
    }

    public void setContentType(String ctString) {
        throw new RuntimeException("Stub!");
    }

    public void setContentEncoding(Header contentEncoding) {
        throw new RuntimeException("Stub!");
    }

    public void setContentEncoding(String ceString) {
        throw new RuntimeException("Stub!");
    }

    public void setChunked(boolean b) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntity
    public void consumeContent() throws IOException, UnsupportedOperationException {
        throw new RuntimeException("Stub!");
    }
}