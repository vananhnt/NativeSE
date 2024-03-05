package gov.nist.javax.sip.header.ims;

import gov.nist.core.Separators;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.header.AddressParametersHeader;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.ExtensionHeader;

/* loaded from: PServedUser.class */
public class PServedUser extends AddressParametersHeader implements PServedUserHeader, SIPHeaderNamesIms, ExtensionHeader {
    public PServedUser(AddressImpl address) {
        super("P-Served-User");
        this.address = address;
    }

    public PServedUser() {
        super("P-Served-User");
    }

    @Override // gov.nist.javax.sip.header.ims.PServedUserHeader
    public String getRegistrationState() {
        return getParameter(ParameterNamesIms.REGISTRATION_STATE);
    }

    @Override // gov.nist.javax.sip.header.ims.PServedUserHeader
    public String getSessionCase() {
        return getParameter(ParameterNamesIms.SESSION_CASE);
    }

    @Override // gov.nist.javax.sip.header.ims.PServedUserHeader
    public void setRegistrationState(String registrationState) {
        if (registrationState != null) {
            if (registrationState.equals("reg") || registrationState.equals("unreg")) {
                try {
                    setParameter(ParameterNamesIms.REGISTRATION_STATE, registrationState);
                    return;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
            }
            try {
                throw new InvalidArgumentException("Value can be either reg or unreg");
            } catch (InvalidArgumentException e2) {
                e2.printStackTrace();
                return;
            }
        }
        throw new NullPointerException("regstate Parameter value is null");
    }

    @Override // gov.nist.javax.sip.header.ims.PServedUserHeader
    public void setSessionCase(String sessionCase) {
        if (sessionCase != null) {
            if (sessionCase.equals("orig") || sessionCase.equals("term")) {
                try {
                    setParameter(ParameterNamesIms.SESSION_CASE, sessionCase);
                    return;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
            }
            try {
                throw new InvalidArgumentException("Value can be either orig or term");
            } catch (InvalidArgumentException e2) {
                e2.printStackTrace();
                return;
            }
        }
        throw new NullPointerException("sess-case Parameter value is null");
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        StringBuffer retval = new StringBuffer();
        retval.append(this.address.encode());
        if (this.parameters.containsKey(ParameterNamesIms.REGISTRATION_STATE)) {
            retval.append(Separators.SEMICOLON).append(ParameterNamesIms.REGISTRATION_STATE).append(Separators.EQUALS).append(getRegistrationState());
        }
        if (this.parameters.containsKey(ParameterNamesIms.SESSION_CASE)) {
            retval.append(Separators.SEMICOLON).append(ParameterNamesIms.SESSION_CASE).append(Separators.EQUALS).append(getSessionCase());
        }
        return retval.toString();
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }

    @Override // gov.nist.javax.sip.header.AddressParametersHeader, gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (other instanceof PServedUser) {
            PServedUserHeader pServedUserHeader = (PServedUserHeader) other;
            return getAddress().equals(((PServedUser) other).getAddress());
        }
        return false;
    }

    @Override // gov.nist.javax.sip.header.AddressParametersHeader, gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        PServedUser retval = (PServedUser) super.clone();
        return retval;
    }
}