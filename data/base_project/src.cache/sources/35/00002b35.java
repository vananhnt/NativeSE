package org.apache.http.impl.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ProtocolException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: EntityEnclosingRequestWrapper.class */
public class EntityEnclosingRequestWrapper extends RequestWrapper implements HttpEntityEnclosingRequest {
    public EntityEnclosingRequestWrapper(HttpEntityEnclosingRequest request) throws ProtocolException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntityEnclosingRequest
    public HttpEntity getEntity() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntityEnclosingRequest
    public void setEntity(HttpEntity entity) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HttpEntityEnclosingRequest
    public boolean expectContinue() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.client.RequestWrapper
    public boolean isRepeatable() {
        throw new RuntimeException("Stub!");
    }
}