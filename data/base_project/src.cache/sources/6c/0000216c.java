package gov.nist.javax.sip.header;

import javax.sip.address.URI;

/* loaded from: SipRequestLine.class */
public interface SipRequestLine {
    URI getUri();

    String getMethod();

    String getSipVersion();

    void setUri(URI uri);

    void setMethod(String str);

    void setSipVersion(String str);

    String getVersionMajor();

    String getVersionMinor();
}