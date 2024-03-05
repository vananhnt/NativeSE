package gov.nist.javax.sip.header.ims;

import javax.sip.InvalidArgumentException;
import javax.sip.header.Header;

/* loaded from: PMediaAuthorizationHeader.class */
public interface PMediaAuthorizationHeader extends Header {
    public static final String NAME = "P-Media-Authorization";

    void setMediaAuthorizationToken(String str) throws InvalidArgumentException;

    String getToken();
}