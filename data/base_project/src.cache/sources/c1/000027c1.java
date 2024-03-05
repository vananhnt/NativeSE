package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import libcore.util.EmptyArray;

/* loaded from: DefaultSSLServerSocketFactory.class */
class DefaultSSLServerSocketFactory extends SSLServerSocketFactory {
    private final String errMessage;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultSSLServerSocketFactory(String mes) {
        this.errMessage = mes;
    }

    @Override // javax.net.ssl.SSLServerSocketFactory
    public String[] getDefaultCipherSuites() {
        return EmptyArray.STRING;
    }

    @Override // javax.net.ssl.SSLServerSocketFactory
    public String[] getSupportedCipherSuites() {
        return EmptyArray.STRING;
    }

    @Override // javax.net.ServerSocketFactory
    public ServerSocket createServerSocket(int port) throws IOException {
        throw new SocketException(this.errMessage);
    }

    @Override // javax.net.ServerSocketFactory
    public ServerSocket createServerSocket(int port, int backlog) throws IOException {
        throw new SocketException(this.errMessage);
    }

    @Override // javax.net.ServerSocketFactory
    public ServerSocket createServerSocket(int port, int backlog, InetAddress iAddress) throws IOException {
        throw new SocketException(this.errMessage);
    }
}