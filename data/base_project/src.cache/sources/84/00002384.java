package java.net;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: MulticastSocket.class */
public class MulticastSocket extends DatagramSocket {
    public MulticastSocket() throws IOException {
        super((DatagramSocketImpl) null);
        throw new RuntimeException("Stub!");
    }

    public MulticastSocket(int port) throws IOException {
        super((DatagramSocketImpl) null);
        throw new RuntimeException("Stub!");
    }

    public MulticastSocket(SocketAddress localAddress) throws IOException {
        super((DatagramSocketImpl) null);
        throw new RuntimeException("Stub!");
    }

    public InetAddress getInterface() throws SocketException {
        throw new RuntimeException("Stub!");
    }

    public NetworkInterface getNetworkInterface() throws SocketException {
        throw new RuntimeException("Stub!");
    }

    public int getTimeToLive() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public byte getTTL() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void joinGroup(InetAddress groupAddr) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void joinGroup(SocketAddress groupAddress, NetworkInterface netInterface) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void leaveGroup(InetAddress groupAddr) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void leaveGroup(SocketAddress groupAddress, NetworkInterface netInterface) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public void send(DatagramPacket packet, byte ttl) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void setInterface(InetAddress address) throws SocketException {
        throw new RuntimeException("Stub!");
    }

    public void setNetworkInterface(NetworkInterface networkInterface) throws SocketException {
        throw new RuntimeException("Stub!");
    }

    public void setTimeToLive(int ttl) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public void setTTL(byte ttl) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public boolean getLoopbackMode() throws SocketException {
        throw new RuntimeException("Stub!");
    }

    public void setLoopbackMode(boolean disable) throws SocketException {
        throw new RuntimeException("Stub!");
    }
}