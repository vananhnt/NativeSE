package gov.nist.javax.sip.header.extensions;

import javax.sip.InvalidArgumentException;
import javax.sip.header.ExtensionHeader;
import javax.sip.header.Header;
import javax.sip.header.Parameters;

/* loaded from: SessionExpiresHeader.class */
public interface SessionExpiresHeader extends Parameters, Header, ExtensionHeader {
    public static final String NAME = "Session-Expires";

    int getExpires();

    void setExpires(int i) throws InvalidArgumentException;

    String getRefresher();

    void setRefresher(String str);
}