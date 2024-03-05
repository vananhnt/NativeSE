package gov.nist.javax.sip.header.ims;

import javax.sip.address.URI;
import javax.sip.header.Header;
import javax.sip.header.HeaderAddress;
import javax.sip.header.Parameters;

/* loaded from: PAssociatedURIHeader.class */
public interface PAssociatedURIHeader extends HeaderAddress, Parameters, Header {
    public static final String NAME = "P-Associated-URI";

    void setAssociatedURI(URI uri) throws NullPointerException;

    URI getAssociatedURI();
}