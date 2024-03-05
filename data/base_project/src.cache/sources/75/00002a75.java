package org.apache.http;

import java.io.IOException;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpRequestInterceptor.class */
public interface HttpRequestInterceptor {
    void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException;
}