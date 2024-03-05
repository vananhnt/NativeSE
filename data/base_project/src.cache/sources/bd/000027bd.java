package javax.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ServerSocketFactory.class */
public abstract class ServerSocketFactory {
    private static ServerSocketFactory defaultFactory;

    public abstract ServerSocket createServerSocket(int i) throws IOException;

    public abstract ServerSocket createServerSocket(int i, int i2) throws IOException;

    public abstract ServerSocket createServerSocket(int i, int i2, InetAddress inetAddress) throws IOException;

    public static synchronized ServerSocketFactory getDefault() {
        if (defaultFactory == null) {
            defaultFactory = new DefaultServerSocketFactory();
        }
        return defaultFactory;
    }

    public ServerSocket createServerSocket() throws IOException {
        throw new SocketException("Unbound server sockets not implemented");
    }
}