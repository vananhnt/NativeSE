package org.apache.http.impl.conn;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DefaultHttpRoutePlanner.class */
public class DefaultHttpRoutePlanner implements HttpRoutePlanner {
    protected SchemeRegistry schemeRegistry;

    public DefaultHttpRoutePlanner(SchemeRegistry schreg) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.HttpRoutePlanner
    public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        throw new RuntimeException("Stub!");
    }
}