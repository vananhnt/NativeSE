package gov.nist.javax.sip.header.ims;

import gov.nist.core.Separators;
import gov.nist.javax.sip.header.SIPHeader;
import java.text.ParseException;
import javax.sip.header.ExtensionHeader;

/* loaded from: PAssertedService.class */
public class PAssertedService extends SIPHeader implements PAssertedServiceHeader, SIPHeaderNamesIms, ExtensionHeader {
    private String subServiceIds;
    private String subAppIds;

    protected PAssertedService(String name) {
        super("P-Asserted-Service");
    }

    public PAssertedService() {
        super("P-Asserted-Service");
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        StringBuffer retval = new StringBuffer();
        retval.append(ParameterNamesIms.SERVICE_ID);
        if (this.subServiceIds != null) {
            retval.append(ParameterNamesIms.SERVICE_ID_LABEL).append(Separators.DOT);
            retval.append(getSubserviceIdentifiers());
        } else if (this.subAppIds != null) {
            retval.append(ParameterNamesIms.APPLICATION_ID_LABEL).append(Separators.DOT);
            retval.append(getApplicationIdentifiers());
        }
        return retval.toString();
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }

    @Override // gov.nist.javax.sip.header.ims.PAssertedServiceHeader
    public String getApplicationIdentifiers() {
        if (this.subAppIds.charAt(0) == '.') {
            return this.subAppIds.substring(1);
        }
        return this.subAppIds;
    }

    @Override // gov.nist.javax.sip.header.ims.PAssertedServiceHeader
    public String getSubserviceIdentifiers() {
        if (this.subServiceIds.charAt(0) == '.') {
            return this.subServiceIds.substring(1);
        }
        return this.subServiceIds;
    }

    @Override // gov.nist.javax.sip.header.ims.PAssertedServiceHeader
    public void setApplicationIdentifiers(String appids) {
        this.subAppIds = appids;
    }

    @Override // gov.nist.javax.sip.header.ims.PAssertedServiceHeader
    public void setSubserviceIdentifiers(String subservices) {
        this.subServiceIds = subservices;
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        return (other instanceof PAssertedServiceHeader) && super.equals(other);
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        PAssertedService retval = (PAssertedService) super.clone();
        return retval;
    }
}