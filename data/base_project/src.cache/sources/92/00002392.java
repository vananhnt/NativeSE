package java.net;

import java.io.IOException;
import java.util.List;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ProxySelector.class */
public abstract class ProxySelector {
    public abstract List<Proxy> select(URI uri);

    public abstract void connectFailed(URI uri, SocketAddress socketAddress, IOException iOException);

    public ProxySelector() {
        throw new RuntimeException("Stub!");
    }

    public static ProxySelector getDefault() {
        throw new RuntimeException("Stub!");
    }

    public static void setDefault(ProxySelector selector) {
        throw new RuntimeException("Stub!");
    }
}