package android.net.dhcp;

import java.net.InetAddress;
import java.nio.ByteBuffer;

/* loaded from: DhcpDeclinePacket.class */
class DhcpDeclinePacket extends DhcpPacket {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DhcpDeclinePacket(int transId, InetAddress clientIp, InetAddress yourIp, InetAddress nextIp, InetAddress relayIp, byte[] clientMac) {
        super(transId, clientIp, yourIp, nextIp, relayIp, clientMac, false);
    }

    @Override // android.net.dhcp.DhcpPacket
    public String toString() {
        String s = super.toString();
        return s + " DECLINE";
    }

    @Override // android.net.dhcp.DhcpPacket
    public ByteBuffer buildPacket(int encap, short destUdp, short srcUdp) {
        ByteBuffer result = ByteBuffer.allocate(1500);
        fillInPacket(encap, this.mClientIp, this.mYourIp, destUdp, srcUdp, result, (byte) 1, false);
        result.flip();
        return result;
    }

    @Override // android.net.dhcp.DhcpPacket
    void finishPacket(ByteBuffer buffer) {
    }

    @Override // android.net.dhcp.DhcpPacket
    public void doNextOp(DhcpStateMachine machine) {
        machine.onDeclineReceived(this.mClientMac, this.mRequestedIp);
    }
}