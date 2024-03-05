package org.apache.http.conn;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConnectionKeepAliveStrategy.class */
public interface ConnectionKeepAliveStrategy {
    long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext);
}