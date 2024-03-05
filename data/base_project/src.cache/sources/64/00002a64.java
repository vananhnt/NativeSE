package org.apache.http;

import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConnectionReuseStrategy.class */
public interface ConnectionReuseStrategy {
    boolean keepAlive(HttpResponse httpResponse, HttpContext httpContext);
}