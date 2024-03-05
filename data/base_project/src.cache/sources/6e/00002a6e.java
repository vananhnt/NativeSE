package org.apache.http;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpEntityEnclosingRequest.class */
public interface HttpEntityEnclosingRequest extends HttpRequest {
    boolean expectContinue();

    void setEntity(HttpEntity httpEntity);

    HttpEntity getEntity();
}