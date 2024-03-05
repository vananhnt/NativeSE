package org.apache.http.client;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RequestDirector.class */
public interface RequestDirector {
    HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException;
}