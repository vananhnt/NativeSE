package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeader;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.ExtensionHeader;

/* loaded from: PMediaAuthorization.class */
public class PMediaAuthorization extends SIPHeader implements PMediaAuthorizationHeader, SIPHeaderNamesIms, ExtensionHeader {
    private static final long serialVersionUID = -6463630258703731133L;
    private String token;

    public PMediaAuthorization() {
        super("P-Media-Authorization");
    }

    @Override // gov.nist.javax.sip.header.ims.PMediaAuthorizationHeader
    public String getToken() {
        return this.token;
    }

    @Override // gov.nist.javax.sip.header.ims.PMediaAuthorizationHeader
    public void setMediaAuthorizationToken(String token) throws InvalidArgumentException {
        if (token == null || token.length() == 0) {
            throw new InvalidArgumentException(" the Media-Authorization-Token parameter is null or empty");
        }
        this.token = token;
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        return this.token;
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (other instanceof PMediaAuthorizationHeader) {
            PMediaAuthorizationHeader o = (PMediaAuthorizationHeader) other;
            return getToken().equals(o.getToken());
        }
        return false;
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        PMediaAuthorization retval = (PMediaAuthorization) super.clone();
        if (this.token != null) {
            retval.token = this.token;
        }
        return retval;
    }
}