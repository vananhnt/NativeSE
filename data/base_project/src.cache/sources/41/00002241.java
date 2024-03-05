package gov.nist.javax.sip.stack;

import com.android.internal.location.GpsNetInitiatedHandler;
import gov.nist.core.Host;
import gov.nist.core.HostPort;
import gov.nist.core.InternalErrorHandler;
import gov.nist.core.Separators;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.header.ContentLength;
import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.MessageFactoryImpl;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import javax.sip.address.Hop;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ServerHeader;

/* loaded from: MessageChannel.class */
public abstract class MessageChannel {
    protected int useCount;
    protected transient MessageProcessor messageProcessor;

    public abstract void close();

    public abstract SIPTransactionStack getSIPStack();

    public abstract String getTransport();

    public abstract boolean isReliable();

    public abstract boolean isSecure();

    public abstract void sendMessage(SIPMessage sIPMessage) throws IOException;

    public abstract String getPeerAddress();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract InetAddress getPeerInetAddress();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract String getPeerProtocol();

    public abstract int getPeerPort();

    public abstract int getPeerPacketSourcePort();

    public abstract InetAddress getPeerPacketSourceAddress();

    public abstract String getKey();

    public abstract String getViaHost();

    public abstract int getViaPort();

    protected abstract void sendMessage(byte[] bArr, InetAddress inetAddress, int i, boolean z) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public void uncache() {
    }

    public String getHost() {
        return getMessageProcessor().getIpAddress().getHostAddress();
    }

    public int getPort() {
        if (this.messageProcessor != null) {
            return this.messageProcessor.getPort();
        }
        return -1;
    }

    public void sendMessage(SIPMessage sipMessage, Hop hop) throws IOException {
        boolean isLoggingEnabled;
        long time = System.currentTimeMillis();
        InetAddress hopAddr = InetAddress.getByName(hop.getHost());
        try {
            try {
                MessageProcessor[] arr$ = getSIPStack().getMessageProcessors();
                for (MessageProcessor messageProcessor : arr$) {
                    if (messageProcessor.getIpAddress().equals(hopAddr) && messageProcessor.getPort() == hop.getPort() && messageProcessor.getTransport().equals(hop.getTransport())) {
                        MessageChannel messageChannel = messageProcessor.createMessageChannel(hopAddr, hop.getPort());
                        if (messageChannel instanceof RawMessageChannel) {
                            ((RawMessageChannel) messageChannel).processMessage(sipMessage);
                            if (getSIPStack().isLoggingEnabled()) {
                                getSIPStack().getStackLogger().logDebug("Self routing message");
                            }
                            if (isLoggingEnabled) {
                                return;
                            }
                            return;
                        }
                    }
                }
                byte[] msg = sipMessage.encodeAsBytes(getTransport());
                sendMessage(msg, hopAddr, hop.getPort(), sipMessage instanceof SIPRequest);
                if (getSIPStack().getStackLogger().isLoggingEnabled(16)) {
                    logMessage(sipMessage, hopAddr, hop.getPort(), time);
                }
            } catch (IOException ioe) {
                throw ioe;
            } catch (Exception ex) {
                if (getSIPStack().getStackLogger().isLoggingEnabled(4)) {
                    getSIPStack().getStackLogger().logError("Error self routing message cause by: ", ex);
                }
                throw new IOException("Error self routing message");
            }
        } finally {
            if (getSIPStack().getStackLogger().isLoggingEnabled(16)) {
                logMessage(sipMessage, hopAddr, hop.getPort(), time);
            }
        }
    }

    public void sendMessage(SIPMessage sipMessage, InetAddress receiverAddress, int receiverPort) throws IOException {
        long time = System.currentTimeMillis();
        byte[] bytes = sipMessage.encodeAsBytes(getTransport());
        sendMessage(bytes, receiverAddress, receiverPort, sipMessage instanceof SIPRequest);
        logMessage(sipMessage, receiverAddress, receiverPort, time);
    }

    public String getRawIpSourceAddress() {
        String sourceAddress = getPeerAddress();
        String rawIpSourceAddress = null;
        try {
            InetAddress sourceInetAddress = InetAddress.getByName(sourceAddress);
            rawIpSourceAddress = sourceInetAddress.getHostAddress();
        } catch (Exception ex) {
            InternalErrorHandler.handleException(ex);
        }
        return rawIpSourceAddress;
    }

    public static String getKey(InetAddress inetAddr, int port, String transport) {
        return (transport + Separators.COLON + inetAddr.getHostAddress() + Separators.COLON + port).toLowerCase();
    }

    public static String getKey(HostPort hostPort, String transport) {
        return (transport + Separators.COLON + hostPort.getHost().getHostname() + Separators.COLON + hostPort.getPort()).toLowerCase();
    }

    public HostPort getHostPort() {
        HostPort retval = new HostPort();
        retval.setHost(new Host(getHost()));
        retval.setPort(getPort());
        return retval;
    }

    public HostPort getPeerHostPort() {
        HostPort retval = new HostPort();
        retval.setHost(new Host(getPeerAddress()));
        retval.setPort(getPeerPort());
        return retval;
    }

    public Via getViaHeader() {
        Via channelViaHeader = new Via();
        try {
            channelViaHeader.setTransport(getTransport());
        } catch (ParseException e) {
        }
        channelViaHeader.setSentBy(getHostPort());
        return channelViaHeader;
    }

    public HostPort getViaHostPort() {
        HostPort retval = new HostPort();
        retval.setHost(new Host(getViaHost()));
        retval.setPort(getViaPort());
        return retval;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logMessage(SIPMessage sipMessage, InetAddress address, int port, long time) {
        if (!getSIPStack().getStackLogger().isLoggingEnabled(16)) {
            return;
        }
        if (port == -1) {
            port = 5060;
        }
        getSIPStack().serverLogger.logMessage(sipMessage, getHost() + Separators.COLON + getPort(), address.getHostAddress().toString() + Separators.COLON + port, true, time);
    }

    public void logResponse(SIPResponse sipResponse, long receptionTime, String status) {
        int peerport = getPeerPort();
        if (peerport == 0 && sipResponse.getContactHeaders() != null) {
            ContactHeader contact = (ContactHeader) sipResponse.getContactHeaders().getFirst();
            peerport = ((AddressImpl) contact.getAddress()).getPort();
        }
        String from = getPeerAddress().toString() + Separators.COLON + peerport;
        String to = getHost() + Separators.COLON + getPort();
        getSIPStack().serverLogger.logMessage(sipResponse, from, to, status, false, receptionTime);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String createBadReqRes(String badReq, ParseException pe) {
        StringBuffer buf = new StringBuffer(512);
        buf.append("SIP/2.0 400 Bad Request (" + pe.getLocalizedMessage() + ')');
        if (!copyViaHeaders(badReq, buf) || !copyHeader("CSeq", badReq, buf) || !copyHeader("Call-ID", badReq, buf) || !copyHeader("From", badReq, buf) || !copyHeader("To", badReq, buf)) {
            return null;
        }
        int toStart = buf.indexOf("To");
        if (toStart != -1 && buf.indexOf("tag", toStart) == -1) {
            buf.append(";tag=badreq");
        }
        ServerHeader s = MessageFactoryImpl.getDefaultServerHeader();
        if (s != null) {
            buf.append(Separators.NEWLINE + s.toString());
        }
        int clength = badReq.length();
        if (!(this instanceof UDPMessageChannel) || clength + buf.length() + "Content-Type".length() + ": message/sipfrag\r\n".length() + "Content-Length".length() < 1300) {
            ContentTypeHeader cth = new ContentType(GpsNetInitiatedHandler.NI_INTENT_KEY_MESSAGE, "sipfrag");
            buf.append(Separators.NEWLINE + cth.toString());
            ContentLength clengthHeader = new ContentLength(clength);
            buf.append(Separators.NEWLINE + clengthHeader.toString());
            buf.append("\r\n\r\n" + badReq);
        } else {
            ContentLength clengthHeader2 = new ContentLength(0);
            buf.append(Separators.NEWLINE + clengthHeader2.toString());
        }
        return buf.toString();
    }

    private static final boolean copyHeader(String name, String fromReq, StringBuffer buf) {
        int end;
        int start = fromReq.indexOf(name);
        if (start != -1 && (end = fromReq.indexOf(Separators.NEWLINE, start)) != -1) {
            buf.append(fromReq.subSequence(start - 2, end));
            return true;
        }
        return false;
    }

    private static final boolean copyViaHeaders(String fromReq, StringBuffer buf) {
        int start = fromReq.indexOf("Via");
        boolean found = false;
        while (start != -1) {
            int end = fromReq.indexOf(Separators.NEWLINE, start);
            if (end != -1) {
                buf.append(fromReq.subSequence(start - 2, end));
                found = true;
                start = fromReq.indexOf("Via", end);
            } else {
                return false;
            }
        }
        return found;
    }

    public MessageProcessor getMessageProcessor() {
        return this.messageProcessor;
    }
}