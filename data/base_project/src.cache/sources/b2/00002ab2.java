package org.apache.http.client.methods;

import java.net.URI;
import org.apache.http.HttpRequest;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpUriRequest.class */
public interface HttpUriRequest extends HttpRequest {
    String getMethod();

    URI getURI();

    void abort() throws UnsupportedOperationException;

    boolean isAborted();
}