package gov.nist.javax.sip.stack;

import gov.nist.javax.sip.address.ParameterNames;
import gov.nist.javax.sip.header.CSeq;
import gov.nist.javax.sip.header.CallID;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.RequestLine;
import gov.nist.javax.sip.header.StatusLine;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.parser.PipelinedMsgParser;
import gov.nist.javax.sip.parser.SIPMessageListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;

/* loaded from: TLSMessageChannel.class */
public final class TLSMessageChannel extends MessageChannel implements SIPMessageListener, Runnable, RawMessageChannel {
    private Socket mySock;
    private PipelinedMsgParser myParser;
    private InputStream myClientInputStream;
    private String key;
    protected boolean isCached;
    protected boolean isRunning;
    private Thread mythread;
    private String myAddress;
    private int myPort;
    private InetAddress peerAddress;
    private int peerPort;
    private String peerProtocol;
    private TLSMessageProcessor tlsMessageProcessor;
    private SIPTransactionStack sipStack;
    private HandshakeCompletedListener handshakeCompletedListener;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.TLSMessageChannel.processMessage(gov.nist.javax.sip.message.SIPMessage):void, file: TLSMessageChannel.class
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
    @Override // gov.nist.javax.sip.parser.SIPMessageListener, gov.nist.javax.sip.stack.RawMessageChannel
    public void processMessage(gov.nist.javax.sip.message.SIPMessage r1) throws java.lang.Exception {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.TLSMessageChannel.processMessage(gov.nist.javax.sip.message.SIPMessage):void, file: TLSMessageChannel.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.TLSMessageChannel.processMessage(gov.nist.javax.sip.message.SIPMessage):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.TLSMessageChannel.run():void, file: TLSMessageChannel.class
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
    @Override // java.lang.Runnable
    public void run() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.TLSMessageChannel.run():void, file: TLSMessageChannel.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.TLSMessageChannel.run():void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TLSMessageChannel(Socket sock, SIPTransactionStack sipStack, TLSMessageProcessor msgProcessor) throws IOException {
        if (sipStack.isLoggingEnabled()) {
            sipStack.getStackLogger().logDebug("creating new TLSMessageChannel (incoming)");
            sipStack.getStackLogger().logStackTrace();
        }
        this.mySock = (SSLSocket) sock;
        if (sock instanceof SSLSocket) {
            SSLSocket sslSock = (SSLSocket) sock;
            sslSock.setNeedClientAuth(true);
            this.handshakeCompletedListener = new HandshakeCompletedListenerImpl(this);
            sslSock.addHandshakeCompletedListener(this.handshakeCompletedListener);
            sslSock.startHandshake();
        }
        this.peerAddress = this.mySock.getInetAddress();
        this.myAddress = msgProcessor.getIpAddress().getHostAddress();
        this.myClientInputStream = this.mySock.getInputStream();
        this.mythread = new Thread(this);
        this.mythread.setDaemon(true);
        this.mythread.setName("TLSMessageChannelThread");
        this.sipStack = sipStack;
        this.tlsMessageProcessor = msgProcessor;
        this.myPort = this.tlsMessageProcessor.getPort();
        this.peerPort = this.mySock.getPort();
        this.messageProcessor = msgProcessor;
        this.mythread.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TLSMessageChannel(InetAddress inetAddr, int port, SIPTransactionStack sipStack, TLSMessageProcessor messageProcessor) throws IOException {
        if (sipStack.isLoggingEnabled()) {
            sipStack.getStackLogger().logDebug("creating new TLSMessageChannel (outgoing)");
            sipStack.getStackLogger().logStackTrace();
        }
        this.peerAddress = inetAddr;
        this.peerPort = port;
        this.myPort = messageProcessor.getPort();
        this.peerProtocol = "TLS";
        this.sipStack = sipStack;
        this.tlsMessageProcessor = messageProcessor;
        this.myAddress = messageProcessor.getIpAddress().getHostAddress();
        this.key = MessageChannel.getKey(this.peerAddress, this.peerPort, "TLS");
        this.messageProcessor = messageProcessor;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public boolean isReliable() {
        return true;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public void close() {
        try {
            if (this.mySock != null) {
                this.mySock.close();
            }
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Closing message Channel " + this);
            }
        } catch (IOException ex) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Error closing socket " + ex);
            }
        }
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public SIPTransactionStack getSIPStack() {
        return this.sipStack;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel, javax.sip.Transaction
    public String getTransport() {
        return ParameterNames.TLS;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel, javax.sip.Transaction
    public String getPeerAddress() {
        if (this.peerAddress != null) {
            return this.peerAddress.getHostAddress();
        }
        return getHost();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.stack.MessageChannel
    public InetAddress getPeerInetAddress() {
        return this.peerAddress;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public String getPeerProtocol() {
        return this.peerProtocol;
    }

    private void sendMessage(byte[] msg, boolean retry) throws IOException {
        Socket sock = this.sipStack.ioHandler.sendBytes(getMessageProcessor().getIpAddress(), this.peerAddress, this.peerPort, this.peerProtocol, msg, retry, this);
        if (sock != this.mySock && sock != null) {
            try {
                if (this.mySock != null) {
                    this.mySock.close();
                }
            } catch (IOException e) {
            }
            this.mySock = sock;
            this.myClientInputStream = this.mySock.getInputStream();
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.setName("TLSMessageChannelThread");
            thread.start();
        }
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public void sendMessage(SIPMessage sipMessage) throws IOException {
        byte[] msg = sipMessage.encodeAsBytes(getTransport());
        long time = System.currentTimeMillis();
        sendMessage(msg, sipMessage instanceof SIPRequest);
        if (this.sipStack.getStackLogger().isLoggingEnabled(16)) {
            logMessage(sipMessage, this.peerAddress, this.peerPort, time);
        }
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public void sendMessage(byte[] message, InetAddress receiverAddress, int receiverPort, boolean retry) throws IOException {
        if (message == null || receiverAddress == null) {
            throw new IllegalArgumentException("Null argument");
        }
        Socket sock = this.sipStack.ioHandler.sendBytes(this.messageProcessor.getIpAddress(), receiverAddress, receiverPort, "TLS", message, retry, this);
        if (sock != this.mySock && sock != null) {
            try {
                if (this.mySock != null) {
                    this.mySock.close();
                }
            } catch (IOException e) {
            }
            this.mySock = sock;
            this.myClientInputStream = this.mySock.getInputStream();
            Thread mythread = new Thread(this);
            mythread.setDaemon(true);
            mythread.setName("TLSMessageChannelThread");
            mythread.start();
        }
    }

    @Override // gov.nist.javax.sip.parser.ParseExceptionListener
    public void handleException(ParseException ex, SIPMessage sipMessage, Class hdrClass, String header, String message) throws ParseException {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logException(ex);
        }
        if (hdrClass != null && (hdrClass.equals(From.class) || hdrClass.equals(To.class) || hdrClass.equals(CSeq.class) || hdrClass.equals(Via.class) || hdrClass.equals(CallID.class) || hdrClass.equals(RequestLine.class) || hdrClass.equals(StatusLine.class))) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Encountered bad message \n" + message);
            }
            String msgString = sipMessage.toString();
            if (!msgString.startsWith("SIP/") && !msgString.startsWith("ACK ")) {
                String badReqRes = createBadReqRes(msgString, ex);
                if (badReqRes != null) {
                    if (this.sipStack.isLoggingEnabled()) {
                        this.sipStack.getStackLogger().logDebug("Sending automatic 400 Bad Request:");
                        this.sipStack.getStackLogger().logDebug(badReqRes);
                    }
                    try {
                        sendMessage(badReqRes.getBytes(), getPeerInetAddress(), getPeerPort(), false);
                    } catch (IOException e) {
                        this.sipStack.getStackLogger().logException(e);
                    }
                } else if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("Could not formulate automatic 400 Bad Request");
                }
            }
            throw ex;
        }
        sipMessage.addUnparsed(header);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.stack.MessageChannel
    public void uncache() {
        if (this.isCached && !this.isRunning) {
            this.tlsMessageProcessor.remove(this);
        }
    }

    public boolean equals(Object other) {
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        TLSMessageChannel that = (TLSMessageChannel) other;
        if (this.mySock != that.mySock) {
            return false;
        }
        return true;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public String getKey() {
        if (this.key != null) {
            return this.key;
        }
        this.key = MessageChannel.getKey(this.peerAddress, this.peerPort, "TLS");
        return this.key;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public String getViaHost() {
        return this.myAddress;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public int getViaPort() {
        return this.myPort;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel, javax.sip.Transaction
    public int getPeerPort() {
        return this.peerPort;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public int getPeerPacketSourcePort() {
        return this.peerPort;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public InetAddress getPeerPacketSourceAddress() {
        return this.peerAddress;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public boolean isSecure() {
        return true;
    }

    public void setHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListenerImpl) {
        this.handshakeCompletedListener = handshakeCompletedListenerImpl;
    }

    public HandshakeCompletedListenerImpl getHandshakeCompletedListener() {
        return (HandshakeCompletedListenerImpl) this.handshakeCompletedListener;
    }
}