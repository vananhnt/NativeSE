package gov.nist.javax.sip.header.ims;

import java.text.ParseException;
import javax.sip.header.ExtensionHeader;

/* loaded from: SecurityClient.class */
public class SecurityClient extends SecurityAgree implements SecurityClientHeader, ExtensionHeader {
    public SecurityClient() {
        super("Security-Client");
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }
}