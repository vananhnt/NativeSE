package java.net;

import java.io.FileDescriptor;
import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DatagramSocketImpl.class */
public abstract class DatagramSocketImpl implements SocketOptions {
    protected FileDescriptor fd;
    protected int localPort;

    protected abstract void bind(int i, InetAddress inetAddress) throws SocketException;

    protected abstract void close();

    protected abstract void create() throws SocketException;

    @Deprecated
    protected abstract byte getTTL() throws IOException;

    protected abstract int getTimeToLive() throws IOException;

    protected abstract void join(InetAddress inetAddress) throws IOException;

    protected abstract void joinGroup(SocketAddress socketAddress, NetworkInterface networkInterface) throws IOException;

    protected abstract void leave(InetAddress inetAddress) throws IOException;

    protected abstract void leaveGroup(SocketAddress socketAddress, NetworkInterface networkInterface) throws IOException;

    protected abstract int peek(InetAddress inetAddress) throws IOException;

    protected abstract void receive(DatagramPacket datagramPacket) throws IOException;

    protected abstract void send(DatagramPacket datagramPacket) throws IOException;

    protected abstract void setTimeToLive(int i) throws IOException;

    @Deprecated
    protected abstract void setTTL(byte b) throws IOException;

    protected abstract int peekData(DatagramPacket datagramPacket) throws IOException;

    public DatagramSocketImpl() {
        throw new RuntimeException("Stub!");
    }

    protected FileDescriptor getFileDescriptor() {
        throw new RuntimeException("Stub!");
    }

    protected int getLocalPort() {
        throw new RuntimeException("Stub!");
    }

    protected void connect(InetAddress inetAddr, int port) throws SocketException {
        throw new RuntimeException("Stub!");
    }

    protected void disconnect() {
        throw new RuntimeException("Stub!");
    }
}