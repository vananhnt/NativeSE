package gov.nist.javax.sip.header.extensions;

import gov.nist.core.Separators;
import gov.nist.javax.sip.header.AddressParametersHeader;
import java.text.ParseException;
import javax.sip.header.ExtensionHeader;

/* loaded from: ReferredBy.class */
public final class ReferredBy extends AddressParametersHeader implements ExtensionHeader, ReferredByHeader {
    private static final long serialVersionUID = 3134344915465784267L;
    public static final String NAME = "Referred-By";

    public ReferredBy() {
        super("Referred-By");
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        if (this.address == null) {
            return null;
        }
        String retval = "";
        if (this.address.getAddressType() == 2) {
            retval = retval + Separators.LESS_THAN;
        }
        String retval2 = retval + this.address.encode();
        if (this.address.getAddressType() == 2) {
            retval2 = retval2 + Separators.GREATER_THAN;
        }
        if (!this.parameters.isEmpty()) {
            retval2 = retval2 + Separators.SEMICOLON + this.parameters.encode();
        }
        return retval2;
    }
}