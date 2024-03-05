package gov.nist.javax.sip.header.extensions;

import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ParametersHeader;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.ExtensionHeader;

/* loaded from: SessionExpires.class */
public final class SessionExpires extends ParametersHeader implements ExtensionHeader, SessionExpiresHeader {
    private static final long serialVersionUID = 8765762413224043300L;
    public static final String NAME = "Session-Expires";
    public int expires;
    public static final String REFRESHER = "refresher";

    public SessionExpires() {
        super("Session-Expires");
    }

    @Override // gov.nist.javax.sip.header.extensions.SessionExpiresHeader
    public int getExpires() {
        return this.expires;
    }

    @Override // gov.nist.javax.sip.header.extensions.SessionExpiresHeader
    public void setExpires(int expires) throws InvalidArgumentException {
        if (expires < 0) {
            throw new InvalidArgumentException("bad argument " + expires);
        }
        this.expires = expires;
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        String retval = Integer.toString(this.expires);
        if (!this.parameters.isEmpty()) {
            retval = retval + Separators.SEMICOLON + this.parameters.encode();
        }
        return retval;
    }

    @Override // gov.nist.javax.sip.header.extensions.SessionExpiresHeader
    public String getRefresher() {
        return this.parameters.getParameter(REFRESHER);
    }

    @Override // gov.nist.javax.sip.header.extensions.SessionExpiresHeader
    public void setRefresher(String refresher) {
        this.parameters.set(REFRESHER, refresher);
    }
}