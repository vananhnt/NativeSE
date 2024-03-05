package javax.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/* loaded from: DefaultServerSocketFactory.class */
final class DefaultServerSocketFactory extends ServerSocketFactory {
    @Override // javax.net.ServerSocketFactory
    public ServerSocket createServerSocket() throws IOException {
        return new ServerSocket();
    }

    @Override // javax.net.ServerSocketFactory
    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    @Override // javax.net.ServerSocketFactory
    public ServerSocket createServerSocket(int port, int backlog) throws IOException {
        return new ServerSocket(port, backlog);
    }

    @Override // javax.net.ServerSocketFactory
    public ServerSocket createServerSocket(int port, int backlog, InetAddress iAddress) throws IOException {
        return new ServerSocket(port, backlog, iAddress);
    }
}