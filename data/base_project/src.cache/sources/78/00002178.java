package gov.nist.javax.sip.header;

import gov.nist.core.Host;
import gov.nist.core.HostPort;
import gov.nist.core.NameValue;
import gov.nist.core.NameValueList;
import gov.nist.core.Separators;
import gov.nist.javax.sip.stack.HopImpl;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.address.Hop;
import javax.sip.header.ViaHeader;

/* loaded from: Via.class */
public class Via extends ParametersHeader implements ViaHeader, ViaHeaderExt {
    private static final long serialVersionUID = 5281728373401351378L;
    public static final String BRANCH = "branch";
    public static final String RECEIVED = "received";
    public static final String MADDR = "maddr";
    public static final String TTL = "ttl";
    public static final String RPORT = "rport";
    protected Protocol sentProtocol;
    protected HostPort sentBy;
    protected String comment;
    private boolean rPortFlag;

    public Via() {
        super("Via");
        this.rPortFlag = false;
        this.sentProtocol = new Protocol();
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof ViaHeader) {
            ViaHeader o = (ViaHeader) other;
            return getProtocol().equalsIgnoreCase(o.getProtocol()) && getTransport().equalsIgnoreCase(o.getTransport()) && getHost().equalsIgnoreCase(o.getHost()) && getPort() == o.getPort() && equalParameters(o);
        }
        return false;
    }

    public String getProtocolVersion() {
        if (this.sentProtocol == null) {
            return null;
        }
        return this.sentProtocol.getProtocolVersion();
    }

    public Protocol getSentProtocol() {
        return this.sentProtocol;
    }

    public HostPort getSentBy() {
        return this.sentBy;
    }

    public Hop getHop() {
        HopImpl hop = new HopImpl(this.sentBy.getHost().getHostname(), this.sentBy.getPort(), this.sentProtocol.getTransport());
        return hop;
    }

    public NameValueList getViaParms() {
        return this.parameters;
    }

    public String getComment() {
        return this.comment;
    }

    public boolean hasPort() {
        return getSentBy().hasPort();
    }

    public boolean hasComment() {
        return this.comment != null;
    }

    public void removePort() {
        this.sentBy.removePort();
    }

    public void removeComment() {
        this.comment = null;
    }

    public void setProtocolVersion(String protocolVersion) {
        if (this.sentProtocol == null) {
            this.sentProtocol = new Protocol();
        }
        this.sentProtocol.setProtocolVersion(protocolVersion);
    }

    public void setHost(Host host) {
        if (this.sentBy == null) {
            this.sentBy = new HostPort();
        }
        this.sentBy.setHost(host);
    }

    public void setSentProtocol(Protocol s) {
        this.sentProtocol = s;
    }

    public void setSentBy(HostPort s) {
        this.sentBy = s;
    }

    public void setComment(String c) {
        this.comment = c;
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        return encodeBody(new StringBuffer()).toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.header.SIPHeader
    public StringBuffer encodeBody(StringBuffer buffer) {
        this.sentProtocol.encode(buffer);
        buffer.append(Separators.SP);
        this.sentBy.encode(buffer);
        if (!this.parameters.isEmpty()) {
            buffer.append(Separators.SEMICOLON);
            this.parameters.encode(buffer);
        }
        if (this.comment != null) {
            buffer.append(Separators.SP).append(Separators.LPAREN).append(this.comment).append(Separators.RPAREN);
        }
        if (this.rPortFlag) {
            buffer.append(";rport");
        }
        return buffer;
    }

    @Override // javax.sip.header.ViaHeader
    public void setHost(String host) throws ParseException {
        if (this.sentBy == null) {
            this.sentBy = new HostPort();
        }
        try {
            Host h = new Host(host);
            this.sentBy.setHost(h);
        } catch (Exception e) {
            throw new NullPointerException(" host parameter is null");
        }
    }

    @Override // javax.sip.header.ViaHeader
    public String getHost() {
        Host host;
        if (this.sentBy == null || (host = this.sentBy.getHost()) == null) {
            return null;
        }
        return host.getHostname();
    }

    @Override // javax.sip.header.ViaHeader
    public void setPort(int port) throws InvalidArgumentException {
        if (port != -1 && (port < 1 || port > 65535)) {
            throw new InvalidArgumentException("Port value out of range -1, [1..65535]");
        }
        if (this.sentBy == null) {
            this.sentBy = new HostPort();
        }
        this.sentBy.setPort(port);
    }

    @Override // javax.sip.header.ViaHeader
    public void setRPort() {
        this.rPortFlag = true;
    }

    @Override // javax.sip.header.ViaHeader
    public int getPort() {
        if (this.sentBy == null) {
            return -1;
        }
        return this.sentBy.getPort();
    }

    @Override // javax.sip.header.ViaHeader
    public int getRPort() {
        String strRport = getParameter("rport");
        if (strRport != null && !strRport.equals("")) {
            return Integer.valueOf(strRport).intValue();
        }
        return -1;
    }

    @Override // javax.sip.header.ViaHeader
    public String getTransport() {
        if (this.sentProtocol == null) {
            return null;
        }
        return this.sentProtocol.getTransport();
    }

    @Override // javax.sip.header.ViaHeader
    public void setTransport(String transport) throws ParseException {
        if (transport == null) {
            throw new NullPointerException("JAIN-SIP Exception, Via, setTransport(), the transport parameter is null.");
        }
        if (this.sentProtocol == null) {
            this.sentProtocol = new Protocol();
        }
        this.sentProtocol.setTransport(transport);
    }

    @Override // javax.sip.header.ViaHeader
    public String getProtocol() {
        if (this.sentProtocol == null) {
            return null;
        }
        return this.sentProtocol.getProtocol();
    }

    @Override // javax.sip.header.ViaHeader
    public void setProtocol(String protocol) throws ParseException {
        if (protocol == null) {
            throw new NullPointerException("JAIN-SIP Exception, Via, setProtocol(), the protocol parameter is null.");
        }
        if (this.sentProtocol == null) {
            this.sentProtocol = new Protocol();
        }
        this.sentProtocol.setProtocol(protocol);
    }

    @Override // javax.sip.header.ViaHeader
    public int getTTL() {
        int ttl = getParameterAsInt("ttl");
        return ttl;
    }

    @Override // javax.sip.header.ViaHeader
    public void setTTL(int ttl) throws InvalidArgumentException {
        if (ttl < 0 && ttl != -1) {
            throw new InvalidArgumentException("JAIN-SIP Exception, Via, setTTL(), the ttl parameter is < 0");
        }
        setParameter(new NameValue("ttl", Integer.valueOf(ttl)));
    }

    @Override // javax.sip.header.ViaHeader
    public String getMAddr() {
        return getParameter("maddr");
    }

    @Override // javax.sip.header.ViaHeader
    public void setMAddr(String mAddr) throws ParseException {
        if (mAddr == null) {
            throw new NullPointerException("JAIN-SIP Exception, Via, setMAddr(), the mAddr parameter is null.");
        }
        Host host = new Host();
        host.setAddress(mAddr);
        NameValue nameValue = new NameValue("maddr", host);
        setParameter(nameValue);
    }

    @Override // javax.sip.header.ViaHeader
    public String getReceived() {
        return getParameter("received");
    }

    @Override // javax.sip.header.ViaHeader
    public void setReceived(String received) throws ParseException {
        if (received == null) {
            throw new NullPointerException("JAIN-SIP Exception, Via, setReceived(), the received parameter is null.");
        }
        setParameter("received", received);
    }

    @Override // javax.sip.header.ViaHeader
    public String getBranch() {
        return getParameter("branch");
    }

    @Override // javax.sip.header.ViaHeader
    public void setBranch(String branch) throws ParseException {
        if (branch == null || branch.length() == 0) {
            throw new NullPointerException("JAIN-SIP Exception, Via, setBranch(), the branch parameter is null or length 0.");
        }
        setParameter("branch", branch);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        Via retval = (Via) super.clone();
        if (this.sentProtocol != null) {
            retval.sentProtocol = (Protocol) this.sentProtocol.clone();
        }
        if (this.sentBy != null) {
            retval.sentBy = (HostPort) this.sentBy.clone();
        }
        if (getRPort() != -1) {
            retval.setParameter("rport", getRPort());
        }
        return retval;
    }

    @Override // javax.sip.header.ViaHeader, gov.nist.javax.sip.header.ViaHeaderExt
    public String getSentByField() {
        if (this.sentBy != null) {
            return this.sentBy.encode();
        }
        return null;
    }

    @Override // javax.sip.header.ViaHeader, gov.nist.javax.sip.header.ViaHeaderExt
    public String getSentProtocolField() {
        if (this.sentProtocol != null) {
            return this.sentProtocol.encode();
        }
        return null;
    }
}