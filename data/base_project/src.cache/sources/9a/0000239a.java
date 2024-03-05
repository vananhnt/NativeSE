package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SocketImpl.class */
public abstract class SocketImpl implements SocketOptions {
    protected InetAddress address;
    protected int port;
    protected FileDescriptor fd;
    protected int localport;

    protected abstract void accept(SocketImpl socketImpl) throws IOException;

    protected abstract int available() throws IOException;

    protected abstract void bind(InetAddress inetAddress, int i) throws IOException;

    protected abstract void close() throws IOException;

    protected abstract void connect(String str, int i) throws IOException;

    protected abstract void connect(InetAddress inetAddress, int i) throws IOException;

    protected abstract void create(boolean z) throws IOException;

    protected abstract InputStream getInputStream() throws IOException;

    protected abstract OutputStream getOutputStream() throws IOException;

    protected abstract void listen(int i) throws IOException;

    protected abstract void connect(SocketAddress socketAddress, int i) throws IOException;

    protected abstract void sendUrgentData(int i) throws IOException;

    public SocketImpl() {
        throw new RuntimeException("Stub!");
    }

    protected FileDescriptor getFileDescriptor() {
        throw new RuntimeException("Stub!");
    }

    protected InetAddress getInetAddress() {
        throw new RuntimeException("Stub!");
    }

    protected int getLocalPort() {
        throw new RuntimeException("Stub!");
    }

    protected int getPort() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    protected void shutdownInput() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void shutdownOutput() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected boolean supportsUrgentData() {
        throw new RuntimeException("Stub!");
    }

    protected void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        throw new RuntimeException("Stub!");
    }
}