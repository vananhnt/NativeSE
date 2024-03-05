package gov.nist.javax.sip;

import gov.nist.core.Host;
import gov.nist.core.HostPort;
import gov.nist.core.InternalErrorHandler;
import gov.nist.core.Separators;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.stack.MessageChannel;
import gov.nist.javax.sip.stack.MessageProcessor;
import java.io.IOException;
import java.text.ParseException;
import javax.sip.ListeningPoint;
import javax.sip.SipStack;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ViaHeader;

/* loaded from: ListeningPointImpl.class */
public class ListeningPointImpl implements ListeningPoint, ListeningPointExt {
    protected String transport;
    int port;
    protected MessageProcessor messageProcessor;
    protected SipProviderImpl sipProvider;
    protected SipStackImpl sipStack;

    public static String makeKey(String host, int port, String transport) {
        return new StringBuffer(host).append(Separators.COLON).append(port).append(Separators.SLASH).append(transport).toString().toLowerCase();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getKey() {
        return makeKey(getIPAddress(), this.port, this.transport);
    }

    protected void setSipProvider(SipProviderImpl sipProviderImpl) {
        this.sipProvider = sipProviderImpl;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeSipProvider() {
        this.sipProvider = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ListeningPointImpl(SipStack sipStack, int port, String transport) {
        this.sipStack = (SipStackImpl) sipStack;
        this.port = port;
        this.transport = transport;
    }

    public Object clone() {
        ListeningPointImpl lip = new ListeningPointImpl(this.sipStack, this.port, null);
        lip.sipStack = this.sipStack;
        return lip;
    }

    @Override // javax.sip.ListeningPoint
    public int getPort() {
        return this.messageProcessor.getPort();
    }

    @Override // javax.sip.ListeningPoint
    public String getTransport() {
        return this.messageProcessor.getTransport();
    }

    public SipProviderImpl getProvider() {
        return this.sipProvider;
    }

    @Override // javax.sip.ListeningPoint
    public String getIPAddress() {
        return this.messageProcessor.getIpAddress().getHostAddress();
    }

    @Override // javax.sip.ListeningPoint
    public void setSentBy(String sentBy) throws ParseException {
        this.messageProcessor.setSentBy(sentBy);
    }

    @Override // javax.sip.ListeningPoint
    public String getSentBy() {
        return this.messageProcessor.getSentBy();
    }

    public boolean isSentBySet() {
        return this.messageProcessor.isSentBySet();
    }

    public Via getViaHeader() {
        return this.messageProcessor.getViaHeader();
    }

    public MessageProcessor getMessageProcessor() {
        return this.messageProcessor;
    }

    @Override // javax.sip.ListeningPoint
    public ContactHeader createContactHeader() {
        try {
            String ipAddress = getIPAddress();
            int port = getPort();
            SipURI sipURI = new SipUri();
            sipURI.setHost(ipAddress);
            sipURI.setPort(port);
            sipURI.setTransportParam(this.transport);
            Contact contact = new Contact();
            AddressImpl address = new AddressImpl();
            address.setURI(sipURI);
            contact.setAddress(address);
            return contact;
        } catch (Exception e) {
            InternalErrorHandler.handleException("Unexpected exception", this.sipStack.getStackLogger());
            return null;
        }
    }

    @Override // javax.sip.ListeningPoint
    public void sendHeartbeat(String ipAddress, int port) throws IOException {
        HostPort targetHostPort = new HostPort();
        targetHostPort.setHost(new Host(ipAddress));
        targetHostPort.setPort(port);
        MessageChannel messageChannel = this.messageProcessor.createMessageChannel(targetHostPort);
        SIPRequest siprequest = new SIPRequest();
        siprequest.setNullRequest();
        messageChannel.sendMessage(siprequest);
    }

    @Override // gov.nist.javax.sip.ListeningPointExt
    public ViaHeader createViaHeader() {
        return getViaHeader();
    }
}