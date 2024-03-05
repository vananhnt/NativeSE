package gov.nist.javax.sip.header.ims;

import gov.nist.core.Separators;
import gov.nist.core.Token;
import gov.nist.javax.sip.header.ParametersHeader;
import java.text.ParseException;
import javax.sip.header.ExtensionHeader;

/* loaded from: PVisitedNetworkID.class */
public class PVisitedNetworkID extends ParametersHeader implements PVisitedNetworkIDHeader, SIPHeaderNamesIms, ExtensionHeader {
    private String networkID;
    private boolean isQuoted;

    public PVisitedNetworkID() {
        super("P-Visited-Network-ID");
    }

    public PVisitedNetworkID(String networkID) {
        super("P-Visited-Network-ID");
        setVisitedNetworkID(networkID);
    }

    public PVisitedNetworkID(Token tok) {
        super("P-Visited-Network-ID");
        setVisitedNetworkID(tok.getTokenValue());
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        StringBuffer retval = new StringBuffer();
        if (getVisitedNetworkID() != null) {
            if (this.isQuoted) {
                retval.append(Separators.DOUBLE_QUOTE + getVisitedNetworkID() + Separators.DOUBLE_QUOTE);
            } else {
                retval.append(getVisitedNetworkID());
            }
        }
        if (!this.parameters.isEmpty()) {
            retval.append(Separators.SEMICOLON + this.parameters.encode());
        }
        return retval.toString();
    }

    @Override // gov.nist.javax.sip.header.ims.PVisitedNetworkIDHeader
    public void setVisitedNetworkID(String networkID) {
        if (networkID == null) {
            throw new NullPointerException(" the networkID parameter is null");
        }
        this.networkID = networkID;
        this.isQuoted = true;
    }

    @Override // gov.nist.javax.sip.header.ims.PVisitedNetworkIDHeader
    public void setVisitedNetworkID(Token networkID) {
        if (networkID == null) {
            throw new NullPointerException(" the networkID parameter is null");
        }
        this.networkID = networkID.getTokenValue();
        this.isQuoted = false;
    }

    @Override // gov.nist.javax.sip.header.ims.PVisitedNetworkIDHeader
    public String getVisitedNetworkID() {
        return this.networkID;
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (other instanceof PVisitedNetworkIDHeader) {
            PVisitedNetworkIDHeader o = (PVisitedNetworkIDHeader) other;
            return getVisitedNetworkID().equals(o.getVisitedNetworkID()) && equalParameters(o);
        }
        return false;
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        PVisitedNetworkID retval = (PVisitedNetworkID) super.clone();
        if (this.networkID != null) {
            retval.networkID = this.networkID;
        }
        retval.isQuoted = this.isQuoted;
        return retval;
    }
}