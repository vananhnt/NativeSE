package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLServerSocket.class */
public abstract class SSLServerSocket extends ServerSocket {
    public abstract String[] getEnabledCipherSuites();

    public abstract void setEnabledCipherSuites(String[] strArr);

    public abstract String[] getSupportedCipherSuites();

    public abstract String[] getSupportedProtocols();

    public abstract String[] getEnabledProtocols();

    public abstract void setEnabledProtocols(String[] strArr);

    public abstract void setNeedClientAuth(boolean z);

    public abstract boolean getNeedClientAuth();

    public abstract void setWantClientAuth(boolean z);

    public abstract boolean getWantClientAuth();

    public abstract void setUseClientMode(boolean z);

    public abstract boolean getUseClientMode();

    public abstract void setEnableSessionCreation(boolean z);

    public abstract boolean getEnableSessionCreation();

    protected SSLServerSocket() throws IOException {
    }

    protected SSLServerSocket(int port) throws IOException {
        super(port);
    }

    protected SSLServerSocket(int port, int backlog) throws IOException {
        super(port, backlog);
    }

    protected SSLServerSocket(int port, int backlog, InetAddress address) throws IOException {
        super(port, backlog, address);
    }
}