package java.net;

import dalvik.system.CloseGuard;
import java.io.FileDescriptor;
import java.io.IOException;
import libcore.io.ErrnoException;
import libcore.io.IoBridge;
import libcore.io.Libcore;
import libcore.io.OsConstants;
import libcore.io.StructGroupReq;
import libcore.util.EmptyArray;

/* loaded from: PlainDatagramSocketImpl.class */
public class PlainDatagramSocketImpl extends DatagramSocketImpl {
    private volatile boolean isNativeConnected;
    private InetAddress connectedAddress;
    private final CloseGuard guard = CloseGuard.get();
    private int connectedPort = -1;

    public PlainDatagramSocketImpl(FileDescriptor fd, int localPort) {
        this.fd = fd;
        this.localPort = localPort;
        if (fd.valid()) {
            this.guard.open("close");
        }
    }

    public PlainDatagramSocketImpl() {
        this.fd = new FileDescriptor();
    }

    @Override // java.net.DatagramSocketImpl
    public void bind(int port, InetAddress address) throws SocketException {
        IoBridge.bind(this.fd, address, port);
        if (port != 0) {
            this.localPort = port;
        } else {
            this.localPort = IoBridge.getSocketLocalPort(this.fd);
        }
        try {
            setOption(32, Boolean.TRUE);
        } catch (IOException e) {
        }
    }

    @Override // java.net.DatagramSocketImpl
    public synchronized void close() {
        this.guard.close();
        try {
            IoBridge.closeSocket(this.fd);
        } catch (IOException e) {
        }
    }

    @Override // java.net.DatagramSocketImpl
    public void create() throws SocketException {
        this.fd = IoBridge.socket(false);
    }

    protected void finalize() throws Throwable {
        try {
            if (this.guard != null) {
                this.guard.warnIfOpen();
            }
            close();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    @Override // java.net.SocketOptions
    public Object getOption(int option) throws SocketException {
        return IoBridge.getSocketOption(this.fd, option);
    }

    @Override // java.net.DatagramSocketImpl
    public int getTimeToLive() throws IOException {
        return ((Integer) getOption(17)).intValue();
    }

    @Override // java.net.DatagramSocketImpl
    public byte getTTL() throws IOException {
        return (byte) getTimeToLive();
    }

    private static StructGroupReq makeGroupReq(InetAddress gr_group, NetworkInterface networkInterface) {
        int gr_interface = networkInterface != null ? networkInterface.getIndex() : 0;
        return new StructGroupReq(gr_interface, gr_group);
    }

    @Override // java.net.DatagramSocketImpl
    public void join(InetAddress addr) throws IOException {
        setOption(19, makeGroupReq(addr, null));
    }

    @Override // java.net.DatagramSocketImpl
    public void joinGroup(SocketAddress addr, NetworkInterface netInterface) throws IOException {
        if (addr instanceof InetSocketAddress) {
            InetAddress groupAddr = ((InetSocketAddress) addr).getAddress();
            setOption(19, makeGroupReq(groupAddr, netInterface));
        }
    }

    @Override // java.net.DatagramSocketImpl
    public void leave(InetAddress addr) throws IOException {
        setOption(20, makeGroupReq(addr, null));
    }

    @Override // java.net.DatagramSocketImpl
    public void leaveGroup(SocketAddress addr, NetworkInterface netInterface) throws IOException {
        if (addr instanceof InetSocketAddress) {
            InetAddress groupAddr = ((InetSocketAddress) addr).getAddress();
            setOption(20, makeGroupReq(groupAddr, netInterface));
        }
    }

    @Override // java.net.DatagramSocketImpl
    protected int peek(InetAddress sender) throws IOException {
        DatagramPacket packet = new DatagramPacket(EmptyArray.BYTE, 0);
        int result = peekData(packet);
        sender.ipaddress = packet.getAddress().getAddress();
        return result;
    }

    private void doRecv(DatagramPacket pack, int flags) throws IOException {
        IoBridge.recvfrom(false, this.fd, pack.getData(), pack.getOffset(), pack.getLength(), flags, pack, this.isNativeConnected);
        if (this.isNativeConnected) {
            updatePacketRecvAddress(pack);
        }
    }

    @Override // java.net.DatagramSocketImpl
    public void receive(DatagramPacket pack) throws IOException {
        doRecv(pack, 0);
    }

    @Override // java.net.DatagramSocketImpl
    public int peekData(DatagramPacket pack) throws IOException {
        doRecv(pack, OsConstants.MSG_PEEK);
        return pack.getPort();
    }

    @Override // java.net.DatagramSocketImpl
    public void send(DatagramPacket packet) throws IOException {
        int port = this.isNativeConnected ? 0 : packet.getPort();
        InetAddress address = this.isNativeConnected ? null : packet.getAddress();
        IoBridge.sendto(this.fd, packet.getData(), packet.getOffset(), packet.getLength(), 0, address, port);
    }

    @Override // java.net.SocketOptions
    public void setOption(int option, Object value) throws SocketException {
        IoBridge.setSocketOption(this.fd, option, value);
    }

    @Override // java.net.DatagramSocketImpl
    public void setTimeToLive(int ttl) throws IOException {
        setOption(17, Integer.valueOf(ttl));
    }

    @Override // java.net.DatagramSocketImpl
    public void setTTL(byte ttl) throws IOException {
        setTimeToLive(ttl & 255);
    }

    @Override // java.net.DatagramSocketImpl
    public void connect(InetAddress inetAddr, int port) throws SocketException {
        IoBridge.connect(this.fd, inetAddr, port);
        try {
            this.connectedAddress = InetAddress.getByAddress(inetAddr.getAddress());
            this.connectedPort = port;
            this.isNativeConnected = true;
        } catch (UnknownHostException e) {
            throw new SocketException("Host is unresolved: " + inetAddr.getHostName());
        }
    }

    @Override // java.net.DatagramSocketImpl
    public void disconnect() {
        try {
            Libcore.os.connect(this.fd, InetAddress.UNSPECIFIED, 0);
        } catch (SocketException e) {
        } catch (ErrnoException errnoException) {
            throw new AssertionError(errnoException);
        }
        this.connectedPort = -1;
        this.connectedAddress = null;
        this.isNativeConnected = false;
    }

    private void updatePacketRecvAddress(DatagramPacket packet) {
        packet.setAddress(this.connectedAddress);
        packet.setPort(this.connectedPort);
    }
}