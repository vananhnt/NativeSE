package gov.nist.javax.sip.stack;

import gov.nist.core.Separators;
import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.address.ParameterNames;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: IOHandler.class */
public class IOHandler {
    private SipStackImpl sipStack;
    private static String TCP = ParameterNames.TCP;
    private static String TLS = ParameterNames.TLS;
    private Semaphore ioSemaphore = new Semaphore(1);
    private ConcurrentHashMap<String, Socket> socketTable = new ConcurrentHashMap<>();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.IOHandler.sendBytes(java.net.InetAddress, java.net.InetAddress, int, java.lang.String, byte[], boolean, gov.nist.javax.sip.stack.MessageChannel):java.net.Socket, file: IOHandler.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public java.net.Socket sendBytes(java.net.InetAddress r1, java.net.InetAddress r2, int r3, java.lang.String r4, byte[] r5, boolean r6, gov.nist.javax.sip.stack.MessageChannel r7) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.IOHandler.sendBytes(java.net.InetAddress, java.net.InetAddress, int, java.lang.String, byte[], boolean, gov.nist.javax.sip.stack.MessageChannel):java.net.Socket, file: IOHandler.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.IOHandler.sendBytes(java.net.InetAddress, java.net.InetAddress, int, java.lang.String, byte[], boolean, gov.nist.javax.sip.stack.MessageChannel):java.net.Socket");
    }

    protected static String makeKey(InetAddress addr, int port) {
        return addr.getHostAddress() + Separators.COLON + port;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public IOHandler(SIPTransactionStack sipStack) {
        this.sipStack = (SipStackImpl) sipStack;
    }

    protected void putSocket(String key, Socket sock) {
        this.socketTable.put(key, sock);
    }

    protected Socket getSocket(String key) {
        return this.socketTable.get(key);
    }

    protected void removeSocket(String key) {
        this.socketTable.remove(key);
    }

    private void writeChunks(OutputStream outputStream, byte[] bytes, int length) throws IOException {
        synchronized (outputStream) {
            for (int p = 0; p < length; p += 8192) {
                int chunk = p + 8192 < length ? 8192 : length - p;
                outputStream.write(bytes, p, chunk);
            }
        }
        outputStream.flush();
    }

    public SocketAddress obtainLocalAddress(InetAddress dst, int dstPort, InetAddress localAddress, int localPort) throws IOException {
        String key = makeKey(dst, dstPort);
        Socket clientSock = getSocket(key);
        if (clientSock == null) {
            clientSock = this.sipStack.getNetworkLayer().createSocket(dst, dstPort, localAddress, localPort);
            putSocket(key, clientSock);
        }
        return clientSock.getLocalSocketAddress();
    }

    public void closeAll() {
        Enumeration<Socket> values = this.socketTable.elements();
        while (values.hasMoreElements()) {
            Socket s = values.nextElement();
            try {
                s.close();
            } catch (IOException e) {
            }
        }
    }
}