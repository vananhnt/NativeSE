package gov.nist.javax.sip.header.ims;

import javax.sip.header.Header;

/* loaded from: PPreferredServiceHeader.class */
public interface PPreferredServiceHeader extends Header {
    public static final String NAME = "P-Preferred-Service";

    void setSubserviceIdentifiers(String str);

    String getSubserviceIdentifiers();

    void setApplicationIdentifiers(String str);

    String getApplicationIdentifiers();
}