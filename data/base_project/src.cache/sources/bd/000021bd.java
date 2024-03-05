package gov.nist.javax.sip.header.ims;

import java.text.ParseException;
import javax.sip.header.ExtensionHeader;

/* loaded from: SecurityVerify.class */
public class SecurityVerify extends SecurityAgree implements SecurityVerifyHeader, ExtensionHeader {
    public SecurityVerify() {
        super("Security-Verify");
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }
}