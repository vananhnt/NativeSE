package org.apache.http.client;

import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RedirectHandler.class */
public interface RedirectHandler {
    boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext);

    URI getLocationURI(HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException;
}