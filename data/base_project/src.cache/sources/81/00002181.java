package gov.nist.javax.sip.header.extensions;

import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ParametersHeader;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.ExtensionHeader;

/* loaded from: MinSE.class */
public class MinSE extends ParametersHeader implements ExtensionHeader, MinSEHeader {
    public static final String NAME = "Min-SE";
    private static final long serialVersionUID = 3134344915465784267L;
    public int expires;

    public MinSE() {
        super("Min-SE");
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        String retval = Integer.toString(this.expires);
        if (!this.parameters.isEmpty()) {
            retval = retval + Separators.SEMICOLON + this.parameters.encode();
        }
        return retval;
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }

    public int getExpires() {
        return this.expires;
    }

    public void setExpires(int expires) throws InvalidArgumentException {
        if (expires < 0) {
            throw new InvalidArgumentException("bad argument " + expires);
        }
        this.expires = expires;
    }
}