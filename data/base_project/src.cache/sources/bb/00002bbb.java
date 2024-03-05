package org.apache.http.protocol;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DefaultedHttpContext.class */
public final class DefaultedHttpContext implements HttpContext {
    public DefaultedHttpContext(HttpContext local, HttpContext defaults) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.protocol.HttpContext
    public Object getAttribute(String id) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.protocol.HttpContext
    public Object removeAttribute(String id) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.protocol.HttpContext
    public void setAttribute(String id, Object obj) {
        throw new RuntimeException("Stub!");
    }

    public HttpContext getDefaults() {
        throw new RuntimeException("Stub!");
    }
}