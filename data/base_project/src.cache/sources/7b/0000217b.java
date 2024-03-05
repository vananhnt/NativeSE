package gov.nist.javax.sip.header;

import gov.nist.javax.sip.header.ims.WWWAuthenticateHeaderIms;
import javax.sip.address.URI;
import javax.sip.header.WWWAuthenticateHeader;

/* loaded from: WWWAuthenticate.class */
public class WWWAuthenticate extends AuthenticationHeader implements WWWAuthenticateHeader, WWWAuthenticateHeaderIms {
    private static final long serialVersionUID = 115378648697363486L;

    public WWWAuthenticate() {
        super("WWW-Authenticate");
    }

    @Override // gov.nist.javax.sip.header.AuthenticationHeader, javax.sip.header.WWWAuthenticateHeader, javax.sip.header.AuthorizationHeader
    public URI getURI() {
        return null;
    }

    @Override // gov.nist.javax.sip.header.AuthenticationHeader, javax.sip.header.WWWAuthenticateHeader, javax.sip.header.AuthorizationHeader
    public void setURI(URI uri) {
    }
}