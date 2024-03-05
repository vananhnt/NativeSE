package org.apache.http;

import java.io.IOException;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpResponseInterceptor.class */
public interface HttpResponseInterceptor {
    void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException;
}