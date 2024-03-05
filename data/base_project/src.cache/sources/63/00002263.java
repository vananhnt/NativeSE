package gov.nist.javax.sip.stack;

import gov.nist.core.HostPort;
import gov.nist.core.InternalErrorHandler;
import gov.nist.javax.sip.address.ParameterNames;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import javax.sip.ListeningPoint;

/* loaded from: TCPMessageProcessor.class */
public class TCPMessageProcessor extends MessageProcessor {
    protected int nConnections;
    private boolean isRunning;
    private Hashtable tcpMessageChannels;
    private ArrayList<TCPMessageChannel> incomingTcpMessageChannels;
    private ServerSocket sock;
    protected int useCount;

    /* JADX INFO: Access modifiers changed from: protected */
    public TCPMessageProcessor(InetAddress ipAddress, SIPTransactionStack sipStack, int port) {
        super(ipAddress, port, ParameterNames.TCP, sipStack);
        this.sipStack = sipStack;
        this.tcpMessageChannels = new Hashtable();
        this.incomingTcpMessageChannels = new ArrayList<>();
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public void start() throws IOException {
        Thread thread = new Thread(this);
        thread.setName("TCPMessageProcessorThread");
        thread.setPriority(10);
        thread.setDaemon(true);
        this.sock = this.sipStack.getNetworkLayer().createServerSocket(getPort(), 0, getIpAddress());
        if (getIpAddress().getHostAddress().equals("0.0.0.0") || getIpAddress().getHostAddress().equals("::0")) {
            super.setIpAddress(this.sock.getInetAddress());
        }
        this.isRunning = true;
        thread.start();
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor, java.lang.Runnable
    public void run() {
        while (this.isRunning) {
            try {
                synchronized (this) {
                    while (this.sipStack.maxConnections != -1 && this.nConnections >= this.sipStack.maxConnections) {
                        try {
                            wait();
                            if (!this.isRunning) {
                                return;
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                    this.nConnections++;
                }
                Socket newsock = this.sock.accept();
                if (this.sipStack.isLoggingEnabled()) {
                    getSIPStack().getStackLogger().logDebug("Accepting new connection!");
                }
                this.incomingTcpMessageChannels.add(new TCPMessageChannel(newsock, this.sipStack, this));
            } catch (SocketException e2) {
                this.isRunning = false;
            } catch (IOException ex) {
                if (this.sipStack.isLoggingEnabled()) {
                    getSIPStack().getStackLogger().logException(ex);
                }
            } catch (Exception ex2) {
                InternalErrorHandler.handleException(ex2);
            }
        }
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public String getTransport() {
        return ParameterNames.TCP;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public SIPTransactionStack getSIPStack() {
        return this.sipStack;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public synchronized void stop() {
        this.isRunning = false;
        try {
            this.sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collection<TCPMessageChannel> en = this.tcpMessageChannels.values();
        for (TCPMessageChannel next : en) {
            next.close();
        }
        Iterator incomingMCIterator = this.incomingTcpMessageChannels.iterator();
        while (incomingMCIterator.hasNext()) {
            TCPMessageChannel next2 = incomingMCIterator.next();
            next2.close();
        }
        notify();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void remove(TCPMessageChannel tcpMessageChannel) {
        String key = tcpMessageChannel.getKey();
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug(Thread.currentThread() + " removing " + key);
        }
        if (this.tcpMessageChannels.get(key) == tcpMessageChannel) {
            this.tcpMessageChannels.remove(key);
        }
        this.incomingTcpMessageChannels.remove(tcpMessageChannel);
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public synchronized MessageChannel createMessageChannel(HostPort targetHostPort) throws IOException {
        String key = MessageChannel.getKey(targetHostPort, ListeningPoint.TCP);
        if (this.tcpMessageChannels.get(key) != null) {
            return (TCPMessageChannel) this.tcpMessageChannels.get(key);
        }
        TCPMessageChannel retval = new TCPMessageChannel(targetHostPort.getInetAddress(), targetHostPort.getPort(), this.sipStack, this);
        this.tcpMessageChannels.put(key, retval);
        retval.isCached = true;
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("key " + key);
            this.sipStack.getStackLogger().logDebug("Creating " + retval);
        }
        return retval;
    }

    protected synchronized void cacheMessageChannel(TCPMessageChannel messageChannel) {
        String key = messageChannel.getKey();
        TCPMessageChannel currentChannel = (TCPMessageChannel) this.tcpMessageChannels.get(key);
        if (currentChannel != null) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Closing " + key);
            }
            currentChannel.close();
        }
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Caching " + key);
        }
        this.tcpMessageChannels.put(key, messageChannel);
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public synchronized MessageChannel createMessageChannel(InetAddress host, int port) throws IOException {
        try {
            String key = MessageChannel.getKey(host, port, ListeningPoint.TCP);
            if (this.tcpMessageChannels.get(key) != null) {
                return (TCPMessageChannel) this.tcpMessageChannels.get(key);
            }
            TCPMessageChannel retval = new TCPMessageChannel(host, port, this.sipStack, this);
            this.tcpMessageChannels.put(key, retval);
            retval.isCached = true;
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("key " + key);
                this.sipStack.getStackLogger().logDebug("Creating " + retval);
            }
            return retval;
        } catch (UnknownHostException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public int getMaximumMessageSize() {
        return Integer.MAX_VALUE;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public boolean inUse() {
        return this.useCount != 0;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public int getDefaultTargetPort() {
        return 5060;
    }

    @Override // gov.nist.javax.sip.stack.MessageProcessor
    public boolean isSecure() {
        return false;
    }
}