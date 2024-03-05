package javax.sip.header;

import javax.sip.address.URI;

/* loaded from: WWWAuthenticateHeader.class */
public interface WWWAuthenticateHeader extends AuthorizationHeader {
    public static final String NAME = "WWW-Authenticate";

    @Override // javax.sip.header.AuthorizationHeader
    URI getURI();

    @Override // javax.sip.header.AuthorizationHeader
    void setURI(URI uri);
}