package android.net.dhcp;

import java.net.InetAddress;
import java.util.List;

/* loaded from: DhcpStateMachine.class */
interface DhcpStateMachine {
    void onOfferReceived(boolean z, int i, byte[] bArr, InetAddress inetAddress, InetAddress inetAddress2);

    void onNakReceived();

    void onAckReceived(InetAddress inetAddress, InetAddress inetAddress2, InetAddress inetAddress3, List<InetAddress> list, InetAddress inetAddress4, int i);

    void onDiscoverReceived(boolean z, int i, byte[] bArr, byte[] bArr2);

    void onRequestReceived(boolean z, int i, byte[] bArr, InetAddress inetAddress, byte[] bArr2, String str);

    void onInformReceived(int i, byte[] bArr, InetAddress inetAddress, byte[] bArr2);

    void onDeclineReceived(byte[] bArr, InetAddress inetAddress);
}