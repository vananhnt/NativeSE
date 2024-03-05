package org.apache.http.conn.routing;

import java.net.InetAddress;
import org.apache.http.HttpHost;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RouteInfo.class */
public interface RouteInfo {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: RouteInfo$LayerType.class */
    public enum LayerType {
        LAYERED,
        PLAIN
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: RouteInfo$TunnelType.class */
    public enum TunnelType {
        PLAIN,
        TUNNELLED
    }

    HttpHost getTargetHost();

    InetAddress getLocalAddress();

    int getHopCount();

    HttpHost getHopTarget(int i);

    HttpHost getProxyHost();

    TunnelType getTunnelType();

    boolean isTunnelled();

    LayerType getLayerType();

    boolean isLayered();

    boolean isSecure();
}