package org.apache.http.impl.conn;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ProxySelectorRoutePlanner.class */
public class ProxySelectorRoutePlanner implements HttpRoutePlanner {
    protected SchemeRegistry schemeRegistry;
    protected ProxySelector proxySelector;

    public ProxySelectorRoutePlanner(SchemeRegistry schreg, ProxySelector prosel) {
        throw new RuntimeException("Stub!");
    }

    public ProxySelector getProxySelector() {
        throw new RuntimeException("Stub!");
    }

    public void setProxySelector(ProxySelector prosel) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.HttpRoutePlanner
    public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        throw new RuntimeException("Stub!");
    }

    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        throw new RuntimeException("Stub!");
    }

    protected String getHost(InetSocketAddress isa) {
        throw new RuntimeException("Stub!");
    }

    protected Proxy chooseProxy(List<Proxy> proxies, HttpHost target, HttpRequest request, HttpContext context) {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: org.apache.http.impl.conn.ProxySelectorRoutePlanner$1  reason: invalid class name */
    /* loaded from: ProxySelectorRoutePlanner$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$net$Proxy$Type = new int[Proxy.Type.values().length];

        static {
            try {
                $SwitchMap$java$net$Proxy$Type[Proxy.Type.DIRECT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$net$Proxy$Type[Proxy.Type.HTTP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$net$Proxy$Type[Proxy.Type.SOCKS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }
}