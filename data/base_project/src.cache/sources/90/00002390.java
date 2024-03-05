package java.net;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Proxy.class */
public class Proxy {
    public static final Proxy NO_PROXY = null;

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Proxy$Type.class */
    public enum Type {
        DIRECT,
        HTTP,
        SOCKS
    }

    public Proxy(Type type, SocketAddress sa) {
        throw new RuntimeException("Stub!");
    }

    public Type type() {
        throw new RuntimeException("Stub!");
    }

    public SocketAddress address() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public final boolean equals(Object obj) {
        throw new RuntimeException("Stub!");
    }

    public final int hashCode() {
        throw new RuntimeException("Stub!");
    }
}