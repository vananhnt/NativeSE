package gov.nist.javax.sip.header.ims;

import gov.nist.core.Token;
import javax.sip.header.Header;
import javax.sip.header.Parameters;

/* loaded from: PVisitedNetworkIDHeader.class */
public interface PVisitedNetworkIDHeader extends Parameters, Header {
    public static final String NAME = "P-Visited-Network-ID";

    void setVisitedNetworkID(String str);

    void setVisitedNetworkID(Token token);

    String getVisitedNetworkID();
}