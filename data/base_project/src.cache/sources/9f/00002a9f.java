package org.apache.http.client;

import java.io.IOException;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpRequestRetryHandler.class */
public interface HttpRequestRetryHandler {
    boolean retryRequest(IOException iOException, int i, HttpContext httpContext);
}