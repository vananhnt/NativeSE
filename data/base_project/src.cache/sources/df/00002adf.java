package org.apache.http.conn.routing;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.RouteInfo;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpRoute.class */
public final class HttpRoute implements RouteInfo, Cloneable {
    public HttpRoute(HttpHost target, InetAddress local, HttpHost[] proxies, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered) {
        throw new RuntimeException("Stub!");
    }

    public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered) {
        throw new RuntimeException("Stub!");
    }

    public HttpRoute(HttpHost target, InetAddress local, boolean secure) {
        throw new RuntimeException("Stub!");
    }

    public HttpRoute(HttpHost target) {
        throw new RuntimeException("Stub!");
    }

    public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.RouteInfo
    public final HttpHost getTargetHost() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.RouteInfo
    public final InetAddress getLocalAddress() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.RouteInfo
    public final int getHopCount() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.RouteInfo
    public final HttpHost getHopTarget(int hop) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.RouteInfo
    public final HttpHost getProxyHost() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.RouteInfo
    public final RouteInfo.TunnelType getTunnelType() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.RouteInfo
    public final boolean isTunnelled() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.RouteInfo
    public final RouteInfo.LayerType getLayerType() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.RouteInfo
    public final boolean isLayered() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.routing.RouteInfo
    public final boolean isSecure() {
        throw new RuntimeException("Stub!");
    }

    public final boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    public final int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public final String toString() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }
}