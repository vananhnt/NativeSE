package gov.nist.javax.sip.address;

import javax.sip.address.SipURI;

/* loaded from: SipURIExt.class */
public interface SipURIExt extends SipURI {
    void removeHeaders();

    void removeHeader(String str);

    boolean hasGrParam();

    void setGrParam(String str);
}