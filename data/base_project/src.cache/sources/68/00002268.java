package gov.nist.javax.sip.stack;

import gov.nist.core.HostPort;
import gov.nist.core.InternalErrorHandler;
import gov.nist.core.ThreadAuditor;
import gov.nist.javax.sip.address.ParameterNames;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;

/* loaded from: UDPMessageProcessor.class */
public class UDPMessageProcessor extends MessageProcessor {
    private int port;
    protected LinkedList messageQueue;
    protected LinkedList messageChannels;
    protected int threadPoolSize;
    protected DatagramSocket sock;
    protected boolean isRunning;
    private static final int HIGHWAT = 5000;
    private static final int LOWAT = 2500;

    /* JADX INFO: Access modifiers changed from: protected */
    public UDPMessageProcessor(InetAddress ipAddress, SIPTransactionStack sipStack, int port) throws IOException {
        super(ipAddress, port, ParameterNames.UDP, sipStack);
        this.sipStack = sipStack;
        this.messageQueue = new LinkedList();
        this.port = port;
        try {
            this.sock = sipStack.getNetworkLayer().createDatagramSocket(port, ipAddress);
            this.sock.setReceiveBufferSize(sipStack.getReceiveUdpBufferSize());
            this.sock.setSendBufferSize(sipStack.getSendUdpBufferSize());
            if (sipStack.getThreadAuditor().isEnabled()) {
                this.sock.setSoTimeout((int) sipStack.getThreadAuditor().getPingIntervalInMillisecs());
            }
            if (ipAddress.getHostAddress().equals("0.0.0.0") || ipAddress.getHostAddress().equals("::0")) {
                super.setIpAddress(this.sock.getLocalAddress());
            }
        } catch (SocketException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public int getPort() {
        return this.port;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public void start() throws IOException {
        this.isRunning = true;
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("UDPMessageProcessorThread");
        thread.setPriority(10);
        thread.start();
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor, java.lang.Runnable
    public void run() {
        DatagramPacket packet;
        this.messageChannels = new LinkedList();
        if (this.sipStack.threadPoolSize != -1) {
            for (int i = 0; i < this.sipStack.threadPoolSize; i++) {
                UDPMessageChannel channel = new UDPMessageChannel(this.sipStack, this);
                this.messageChannels.add(channel);
            }
        }
        ThreadAuditor.ThreadHandle threadHandle = this.sipStack.getThreadAuditor().addCurrentThread();
        while (this.isRunning) {
            try {
                threadHandle.ping();
                int bufsize = this.sock.getReceiveBufferSize();
                byte[] message = new byte[bufsize];
                packet = new DatagramPacket(message, bufsize);
                this.sock.receive(packet);
            } catch (SocketException e) {
                if (this.sipStack.isLoggingEnabled()) {
                    getSIPStack().getStackLogger().logDebug("UDPMessageProcessor: Stopping");
                }
                this.isRunning = false;
                synchronized (this.messageQueue) {
                    this.messageQueue.notifyAll();
                }
            } catch (SocketTimeoutException e2) {
            } catch (IOException ex) {
                this.isRunning = false;
                ex.printStackTrace();
                if (this.sipStack.isLoggingEnabled()) {
                    getSIPStack().getStackLogger().logDebug("UDPMessageProcessor: Got an IO Exception");
                }
            } catch (Exception ex2) {
                if (this.sipStack.isLoggingEnabled()) {
                    getSIPStack().getStackLogger().logDebug("UDPMessageProcessor: Unexpected Exception - quitting");
                }
                InternalErrorHandler.handleException(ex2);
                return;
            }
            if (this.sipStack.stackDoesCongestionControl) {
                if (this.messageQueue.size() >= 5000) {
                    if (this.sipStack.isLoggingEnabled()) {
                        this.sipStack.getStackLogger().logDebug("Dropping message -- queue length exceeded");
                    }
                } else if (this.messageQueue.size() > LOWAT && this.messageQueue.size() < 5000) {
                    float threshold = (this.messageQueue.size() - LOWAT) / 2500.0f;
                    boolean decision = Math.random() > 1.0d - ((double) threshold);
                    if (decision) {
                        if (this.sipStack.isLoggingEnabled()) {
                            this.sipStack.getStackLogger().logDebug("Dropping message with probability  " + (1.0d - threshold));
                        }
                    }
                }
            }
            if (this.sipStack.threadPoolSize != -1) {
                synchronized (this.messageQueue) {
                    this.messageQueue.add(packet);
                    this.messageQueue.notify();
                }
            } else {
                new UDPMessageChannel(this.sipStack, this, packet);
            }
        }
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public void stop() {
        synchronized (this.messageQueue) {
            this.isRunning = false;
            this.messageQueue.notifyAll();
            this.sock.close();
        }
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public String getTransport() {
        return ParameterNames.UDP;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public SIPTransactionStack getSIPStack() {
        return this.sipStack;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public MessageChannel createMessageChannel(HostPort targetHostPort) throws UnknownHostException {
        return new UDPMessageChannel(targetHostPort.getInetAddress(), targetHostPort.getPort(), this.sipStack, this);
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public MessageChannel createMessageChannel(InetAddress host, int port) throws IOException {
        return new UDPMessageChannel(host, port, this.sipStack, this);
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public int getDefaultTargetPort() {
        return 5060;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public boolean isSecure() {
        return false;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public int getMaximumMessageSize() {
        return 8192;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public boolean inUse() {
        boolean z;
        synchronized (this.messageQueue) {
            z = this.messageQueue.size() != 0;
        }
        return z;
    }
}