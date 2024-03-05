package javax.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/* loaded from: DefaultSocketFactory.class */
final class DefaultSocketFactory extends SocketFactory {
    @Override // javax.net.SocketFactory
    public Socket createSocket() throws IOException {
        return new Socket();
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return new Socket(host, port);
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return new Socket(host, port, localHost, localPort);
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return new Socket(host, port);
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return new Socket(address, port, localAddress, localPort);
    }
}