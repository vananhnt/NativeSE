package java.net;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ServerSocket.class */
public class ServerSocket {
    public ServerSocket() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public ServerSocket(int port) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public ServerSocket(int port, int backlog) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public ServerSocket(int port, int backlog, InetAddress localAddress) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Socket accept() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public InetAddress getInetAddress() {
        throw new RuntimeException("Stub!");
    }

    public int getLocalPort() {
        throw new RuntimeException("Stub!");
    }

    public synchronized int getSoTimeout() throws IOException {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void implAccept(Socket aSocket) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public static synchronized void setSocketFactory(SocketImplFactory aFactory) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void setSoTimeout(int timeout) throws SocketException {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public void bind(SocketAddress localAddr) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void bind(SocketAddress localAddr, int backlog) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public SocketAddress getLocalSocketAddress() {
        throw new RuntimeException("Stub!");
    }

    public boolean isBound() {
        throw new RuntimeException("Stub!");
    }

    public boolean isClosed() {
        throw new RuntimeException("Stub!");
    }

    public void setReuseAddress(boolean reuse) throws SocketException {
        throw new RuntimeException("Stub!");
    }

    public boolean getReuseAddress() throws SocketException {
        throw new RuntimeException("Stub!");
    }

    public void setReceiveBufferSize(int size) throws SocketException {
        throw new RuntimeException("Stub!");
    }

    public int getReceiveBufferSize() throws SocketException {
        throw new RuntimeException("Stub!");
    }

    public ServerSocketChannel getChannel() {
        throw new RuntimeException("Stub!");
    }

    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        throw new RuntimeException("Stub!");
    }
}