package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import libcore.util.EmptyArray;

/* loaded from: DefaultSSLSocketFactory.class */
class DefaultSSLSocketFactory extends SSLSocketFactory {
    private final String errMessage;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultSSLSocketFactory(String mes) {
        this.errMessage = mes;
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public String[] getDefaultCipherSuites() {
        return EmptyArray.STRING;
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public String[] getSupportedCipherSuites() {
        return EmptyArray.STRING;
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        throw new SocketException(this.errMessage);
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        throw new SocketException(this.errMessage);
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        throw new SocketException(this.errMessage);
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(InetAddress host, int port) throws IOException {
        throw new SocketException(this.errMessage);
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        throw new SocketException(this.errMessage);
    }
}