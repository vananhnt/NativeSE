package gov.nist.javax.sip.stack;

import gov.nist.core.Separators;
import java.io.Serializable;
import javax.sip.ListeningPoint;
import javax.sip.address.Hop;

/* loaded from: HopImpl.class */
public final class HopImpl implements Hop, Serializable {
    protected String host;
    protected int port;
    protected String transport;
    protected boolean defaultRoute;
    protected boolean uriRoute;

    @Override // javax.sip.address.Hop
    public String toString() {
        return this.host + Separators.COLON + this.port + Separators.SLASH + this.transport;
    }

    public HopImpl(String hostName, int portNumber, String trans) {
        this.host = hostName;
        if (this.host.indexOf(Separators.COLON) >= 0 && this.host.indexOf("[") < 0) {
            this.host = "[" + this.host + "]";
        }
        this.port = portNumber;
        this.transport = trans;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HopImpl(String hop) throws IllegalArgumentException {
        String portstr;
        if (hop == null) {
            throw new IllegalArgumentException("Null arg!");
        }
        int brack = hop.indexOf(93);
        int colon = hop.indexOf(58, brack);
        int slash = hop.indexOf(47, colon);
        if (colon > 0) {
            this.host = hop.substring(0, colon);
            if (slash > 0) {
                portstr = hop.substring(colon + 1, slash);
                this.transport = hop.substring(slash + 1);
            } else {
                portstr = hop.substring(colon + 1);
                this.transport = ListeningPoint.UDP;
            }
            try {
                this.port = Integer.parseInt(portstr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Bad port spec");
            }
        } else if (slash > 0) {
            this.host = hop.substring(0, slash);
            this.transport = hop.substring(slash + 1);
            this.port = this.transport.equalsIgnoreCase("TLS") ? 5061 : 5060;
        } else {
            this.host = hop;
            this.transport = ListeningPoint.UDP;
            this.port = 5060;
        }
        if (this.host == null || this.host.length() == 0) {
            throw new IllegalArgumentException("no host!");
        }
        this.host = this.host.trim();
        this.transport = this.transport.trim();
        if (brack > 0 && this.host.charAt(0) != '[') {
            throw new IllegalArgumentException("Bad IPv6 reference spec");
        }
        if (this.transport.compareToIgnoreCase(ListeningPoint.UDP) != 0 && this.transport.compareToIgnoreCase("TLS") != 0 && this.transport.compareToIgnoreCase(ListeningPoint.TCP) != 0) {
            System.err.println("Bad transport string " + this.transport);
            throw new IllegalArgumentException(hop);
        }
    }

    @Override // javax.sip.address.Hop
    public String getHost() {
        return this.host;
    }

    @Override // javax.sip.address.Hop
    public int getPort() {
        return this.port;
    }

    @Override // javax.sip.address.Hop
    public String getTransport() {
        return this.transport;
    }

    @Override // javax.sip.address.Hop
    public boolean isURIRoute() {
        return this.uriRoute;
    }

    @Override // javax.sip.address.Hop
    public void setURIRouteFlag() {
        this.uriRoute = true;
    }
}