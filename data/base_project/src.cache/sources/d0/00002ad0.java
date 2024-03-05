package org.apache.http.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpInetConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ManagedClientConnection.class */
public interface ManagedClientConnection extends HttpClientConnection, HttpInetConnection, ConnectionReleaseTrigger {
    boolean isSecure();

    HttpRoute getRoute();

    SSLSession getSSLSession();

    void open(HttpRoute httpRoute, HttpContext httpContext, HttpParams httpParams) throws IOException;

    void tunnelTarget(boolean z, HttpParams httpParams) throws IOException;

    void tunnelProxy(HttpHost httpHost, boolean z, HttpParams httpParams) throws IOException;

    void layerProtocol(HttpContext httpContext, HttpParams httpParams) throws IOException;

    void markReusable();

    void unmarkReusable();

    boolean isMarkedReusable();

    void setState(Object obj);

    Object getState();

    void setIdleDuration(long j, TimeUnit timeUnit);
}