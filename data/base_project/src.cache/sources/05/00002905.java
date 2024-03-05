package libcore.net;

import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import libcore.io.IoBridge;

/* loaded from: RawSocket.class */
public class RawSocket implements Closeable {
    public static final short ETH_P_IP = 2048;
    public static final short ETH_P_ARP = 2054;
    private final String mInterfaceName;
    private final short mProtocolType;
    private final CloseGuard guard = CloseGuard.get();
    private final FileDescriptor fd = new FileDescriptor();

    private static native void create(FileDescriptor fileDescriptor, short s, String str) throws SocketException;

    private static native int sendPacket(FileDescriptor fileDescriptor, String str, short s, byte[] bArr, byte[] bArr2, int i, int i2);

    private static native int recvPacket(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2, int i3, int i4);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.net.RawSocket.finalize():void, file: RawSocket.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    protected void finalize() throws java.lang.Throwable {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.net.RawSocket.finalize():void, file: RawSocket.class
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.net.RawSocket.finalize():void");
    }

    public RawSocket(String interfaceName, short protocolType) throws SocketException {
        this.mInterfaceName = interfaceName;
        this.mProtocolType = protocolType;
        create(this.fd, this.mProtocolType, this.mInterfaceName);
        this.guard.open("close");
    }

    public int read(byte[] packet, int offset, int byteCount, int destPort, int timeoutMillis) {
        if (packet == null) {
            throw new NullPointerException("packet == null");
        }
        Arrays.checkOffsetAndCount(packet.length, offset, byteCount);
        if (destPort > 65535) {
            throw new IllegalArgumentException("Port out of range: " + destPort);
        }
        return recvPacket(this.fd, packet, offset, byteCount, destPort, timeoutMillis);
    }

    public int write(byte[] destMac, byte[] packet, int offset, int byteCount) {
        if (destMac == null) {
            throw new NullPointerException("destMac == null");
        }
        if (packet == null) {
            throw new NullPointerException("packet == null");
        }
        Arrays.checkOffsetAndCount(packet.length, offset, byteCount);
        if (destMac.length != 6) {
            throw new IllegalArgumentException("MAC length must be 6: " + destMac.length);
        }
        return sendPacket(this.fd, this.mInterfaceName, this.mProtocolType, destMac, packet, offset, byteCount);
    }

    @Override // java.io.Closeable
    public void close() throws IOException {
        this.guard.close();
        IoBridge.closeSocket(this.fd);
    }
}