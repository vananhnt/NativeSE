package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLSocket.class */
public abstract class SSLSocket extends Socket {
    public abstract String[] getSupportedCipherSuites();

    public abstract String[] getEnabledCipherSuites();

    public abstract void setEnabledCipherSuites(String[] strArr);

    public abstract String[] getSupportedProtocols();

    public abstract String[] getEnabledProtocols();

    public abstract void setEnabledProtocols(String[] strArr);

    public abstract SSLSession getSession();

    public abstract void addHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener);

    public abstract void removeHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener);

    public abstract void startHandshake() throws IOException;

    public abstract void setUseClientMode(boolean z);

    public abstract boolean getUseClientMode();

    public abstract void setNeedClientAuth(boolean z);

    public abstract void setWantClientAuth(boolean z);

    public abstract boolean getNeedClientAuth();

    public abstract boolean getWantClientAuth();

    public abstract void setEnableSessionCreation(boolean z);

    public abstract boolean getEnableSessionCreation();

    protected SSLSocket() {
    }

    protected SSLSocket(String host, int port) throws IOException, UnknownHostException {
        super(host, port);
    }

    protected SSLSocket(InetAddress address, int port) throws IOException {
        super(address, port);
    }

    protected SSLSocket(String host, int port, InetAddress clientAddress, int clientPort) throws IOException, UnknownHostException {
        super(host, port, clientAddress, clientPort);
    }

    protected SSLSocket(InetAddress address, int port, InetAddress clientAddress, int clientPort) throws IOException {
        super(address, port, clientAddress, clientPort);
    }

    @Override // java.net.Socket
    public void shutdownInput() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // java.net.Socket
    public void shutdownOutput() throws IOException {
        throw new UnsupportedOperationException();
    }

    public SSLParameters getSSLParameters() {
        SSLParameters p = new SSLParameters();
        p.setCipherSuites(getEnabledCipherSuites());
        p.setProtocols(getEnabledProtocols());
        p.setNeedClientAuth(getNeedClientAuth());
        p.setWantClientAuth(getWantClientAuth());
        return p;
    }

    public void setSSLParameters(SSLParameters p) {
        String[] cipherSuites = p.getCipherSuites();
        if (cipherSuites != null) {
            setEnabledCipherSuites(cipherSuites);
        }
        String[] protocols = p.getProtocols();
        if (protocols != null) {
            setEnabledProtocols(protocols);
        }
        if (p.getNeedClientAuth()) {
            setNeedClientAuth(true);
        } else if (p.getWantClientAuth()) {
            setWantClientAuth(true);
        } else {
            setWantClientAuth(false);
        }
    }
}