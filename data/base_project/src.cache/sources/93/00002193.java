package gov.nist.javax.sip.header.ims;

import javax.sip.header.Header;

/* loaded from: PAssertedServiceHeader.class */
public interface PAssertedServiceHeader extends Header {
    public static final String NAME = "P-Asserted-Service";

    void setSubserviceIdentifiers(String str);

    String getSubserviceIdentifiers();

    void setApplicationIdentifiers(String str);

    String getApplicationIdentifiers();
}