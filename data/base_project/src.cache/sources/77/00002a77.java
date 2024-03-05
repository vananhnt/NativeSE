package org.apache.http;

import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpResponseFactory.class */
public interface HttpResponseFactory {
    HttpResponse newHttpResponse(ProtocolVersion protocolVersion, int i, HttpContext httpContext);

    HttpResponse newHttpResponse(StatusLine statusLine, HttpContext httpContext);
}