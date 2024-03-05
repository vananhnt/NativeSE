package gov.nist.javax.sip.header.ims;

import java.text.ParseException;
import javax.sip.header.Header;

/* loaded from: PrivacyHeader.class */
public interface PrivacyHeader extends Header {
    public static final String NAME = "Privacy";

    void setPrivacy(String str) throws ParseException;

    String getPrivacy();
}