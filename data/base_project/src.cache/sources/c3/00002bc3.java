package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpRequestHandler.class */
public interface HttpRequestHandler {
    void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException;
}