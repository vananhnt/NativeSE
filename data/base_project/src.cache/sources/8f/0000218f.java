package gov.nist.javax.sip.header.ims;

import gov.nist.core.Separators;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.header.AddressParametersHeader;
import java.text.ParseException;
import javax.sip.header.ExtensionHeader;

/* loaded from: PAssertedIdentity.class */
public class PAssertedIdentity extends AddressParametersHeader implements PAssertedIdentityHeader, SIPHeaderNamesIms, ExtensionHeader {
    public PAssertedIdentity(AddressImpl address) {
        super("P-Asserted-Identity");
        this.address = address;
    }

    public PAssertedIdentity() {
        super("P-Asserted-Identity");
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        StringBuffer retval = new StringBuffer();
        if (this.address.getAddressType() == 2) {
            retval.append(Separators.LESS_THAN);
        }
        retval.append(this.address.encode());
        if (this.address.getAddressType() == 2) {
            retval.append(Separators.GREATER_THAN);
        }
        if (!this.parameters.isEmpty()) {
            retval.append(Separators.COMMA + this.parameters.encode());
        }
        return retval.toString();
    }

    @Override // gov.nist.javax.sip.header.AddressParametersHeader, gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        PAssertedIdentity retval = (PAssertedIdentity) super.clone();
        return retval;
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }
}