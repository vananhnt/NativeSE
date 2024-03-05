package org.apache.http.protocol;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpExpectationVerifier.class */
public interface HttpExpectationVerifier {
    void verify(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException;
}