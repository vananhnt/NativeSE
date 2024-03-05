package gov.nist.javax.sip.header.ims;

import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ParametersHeader;
import java.text.ParseException;
import javax.sip.header.ExtensionHeader;

/* loaded from: PAccessNetworkInfo.class */
public class PAccessNetworkInfo extends ParametersHeader implements PAccessNetworkInfoHeader, ExtensionHeader {
    private String accessType;
    private Object extendAccessInfo;

    public PAccessNetworkInfo() {
        super("P-Access-Network-Info");
        this.parameters.setSeparator(Separators.SEMICOLON);
    }

    public PAccessNetworkInfo(String accessTypeVal) {
        this();
        setAccessType(accessTypeVal);
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public void setAccessType(String accessTypeVal) {
        if (accessTypeVal == null) {
            throw new NullPointerException("JAIN-SIP Exception, P-Access-Network-Info, setAccessType(), the accessType parameter is null.");
        }
        this.accessType = accessTypeVal;
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public String getAccessType() {
        return this.accessType;
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public void setCGI3GPP(String cgi) throws ParseException {
        if (cgi == null) {
            throw new NullPointerException("JAIN-SIP Exception, P-Access-Network-Info, setCGI3GPP(), the cgi parameter is null.");
        }
        setParameter(ParameterNamesIms.CGI_3GPP, cgi);
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public String getCGI3GPP() {
        return getParameter(ParameterNamesIms.CGI_3GPP);
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public void setUtranCellID3GPP(String utranCellID) throws ParseException {
        if (utranCellID == null) {
            throw new NullPointerException("JAIN-SIP Exception, P-Access-Network-Info, setUtranCellID3GPP(), the utranCellID parameter is null.");
        }
        setParameter(ParameterNamesIms.UTRAN_CELL_ID_3GPP, utranCellID);
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public String getUtranCellID3GPP() {
        return getParameter(ParameterNamesIms.UTRAN_CELL_ID_3GPP);
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public void setDSLLocation(String dslLocation) throws ParseException {
        if (dslLocation == null) {
            throw new NullPointerException("JAIN-SIP Exception, P-Access-Network-Info, setDSLLocation(), the dslLocation parameter is null.");
        }
        setParameter(ParameterNamesIms.DSL_LOCATION, dslLocation);
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public String getDSLLocation() {
        return getParameter(ParameterNamesIms.DSL_LOCATION);
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public void setCI3GPP2(String ci3Gpp2) throws ParseException {
        if (ci3Gpp2 == null) {
            throw new NullPointerException("JAIN-SIP Exception, P-Access-Network-Info, setCI3GPP2(), the ci3Gpp2 parameter is null.");
        }
        setParameter(ParameterNamesIms.CI_3GPP2, ci3Gpp2);
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public String getCI3GPP2() {
        return getParameter(ParameterNamesIms.CI_3GPP2);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader
    public void setParameter(String name, Object value) {
        if (name.equalsIgnoreCase(ParameterNamesIms.CGI_3GPP) || name.equalsIgnoreCase(ParameterNamesIms.UTRAN_CELL_ID_3GPP) || name.equalsIgnoreCase(ParameterNamesIms.DSL_LOCATION) || name.equalsIgnoreCase(ParameterNamesIms.CI_3GPP2)) {
            try {
                super.setQuotedParameter(name, value.toString());
                return;
            } catch (ParseException e) {
                return;
            }
        }
        super.setParameter(name, value);
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public void setExtensionAccessInfo(Object extendAccessInfo) throws ParseException {
        if (extendAccessInfo == null) {
            throw new NullPointerException("JAIN-SIP Exception, P-Access-Network-Info, setExtendAccessInfo(), the extendAccessInfo parameter is null.");
        }
        this.extendAccessInfo = extendAccessInfo;
    }

    @Override // gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader
    public Object getExtensionAccessInfo() {
        return this.extendAccessInfo;
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        StringBuffer encoding = new StringBuffer();
        if (getAccessType() != null) {
            encoding.append(getAccessType());
        }
        if (!this.parameters.isEmpty()) {
            encoding.append("; " + this.parameters.encode());
        }
        if (getExtensionAccessInfo() != null) {
            encoding.append("; " + getExtensionAccessInfo().toString());
        }
        return encoding.toString();
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        return (other instanceof PAccessNetworkInfoHeader) && super.equals(other);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        PAccessNetworkInfo retval = (PAccessNetworkInfo) super.clone();
        return retval;
    }
}