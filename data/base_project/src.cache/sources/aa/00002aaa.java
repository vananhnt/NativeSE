package org.apache.http.client.methods;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpEntityEnclosingRequestBase.class */
public abstract class HttpEntityEnclosingRequestBase extends HttpRequestBase implements HttpEntityEnclosingRequest {
    public HttpEntityEnclosingRequestBase() {
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

    @Override // org.apache.http.client.methods.HttpRequestBase
    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }
}