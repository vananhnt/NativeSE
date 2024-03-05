package gov.nist.javax.sip.address;

import gov.nist.core.NameValueList;
import gov.nist.core.Separators;
import java.text.ParseException;
import java.util.Iterator;
import javax.sip.address.TelURL;

/* loaded from: TelURLImpl.class */
public class TelURLImpl extends GenericURI implements TelURL {
    private static final long serialVersionUID = 5873527320305915954L;
    protected TelephoneNumber telephoneNumber;

    public TelURLImpl() {
        this.scheme = "tel";
    }

    public void setTelephoneNumber(TelephoneNumber telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    @Override // javax.sip.address.TelURL
    public String getIsdnSubAddress() {
        return this.telephoneNumber.getIsdnSubaddress();
    }

    @Override // javax.sip.address.TelURL
    public String getPostDial() {
        return this.telephoneNumber.getPostDial();
    }

    @Override // gov.nist.javax.sip.address.GenericURI, javax.sip.address.URI
    public String getScheme() {
        return this.scheme;
    }

    @Override // javax.sip.address.TelURL
    public boolean isGlobal() {
        return this.telephoneNumber.isGlobal();
    }

    @Override // gov.nist.javax.sip.address.GenericURI, javax.sip.address.URI
    public boolean isSipURI() {
        return false;
    }

    @Override // javax.sip.address.TelURL
    public void setGlobal(boolean global) {
        this.telephoneNumber.setGlobal(global);
    }

    @Override // javax.sip.address.TelURL
    public void setIsdnSubAddress(String isdnSubAddress) {
        this.telephoneNumber.setIsdnSubaddress(isdnSubAddress);
    }

    @Override // javax.sip.address.TelURL
    public void setPostDial(String postDial) {
        this.telephoneNumber.setPostDial(postDial);
    }

    @Override // javax.sip.address.TelURL
    public void setPhoneNumber(String telephoneNumber) {
        this.telephoneNumber.setPhoneNumber(telephoneNumber);
    }

    @Override // javax.sip.address.TelURL
    public String getPhoneNumber() {
        return this.telephoneNumber.getPhoneNumber();
    }

    @Override // gov.nist.javax.sip.address.GenericURI, gov.nist.javax.sip.address.NetObject, javax.sip.address.URI
    public String toString() {
        return this.scheme + Separators.COLON + this.telephoneNumber.encode();
    }

    @Override // gov.nist.javax.sip.address.GenericURI, gov.nist.core.GenericObject
    public String encode() {
        return encode(new StringBuffer()).toString();
    }

    @Override // gov.nist.javax.sip.address.GenericURI, gov.nist.core.GenericObject
    public StringBuffer encode(StringBuffer buffer) {
        buffer.append(this.scheme).append(':');
        this.telephoneNumber.encode(buffer);
        return buffer;
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        TelURLImpl retval = (TelURLImpl) super.clone();
        if (this.telephoneNumber != null) {
            retval.telephoneNumber = (TelephoneNumber) this.telephoneNumber.clone();
        }
        return retval;
    }

    @Override // javax.sip.header.Parameters
    public String getParameter(String parameterName) {
        return this.telephoneNumber.getParameter(parameterName);
    }

    @Override // javax.sip.header.Parameters
    public void setParameter(String name, String value) {
        this.telephoneNumber.setParameter(name, value);
    }

    @Override // javax.sip.header.Parameters
    public Iterator<String> getParameterNames() {
        return this.telephoneNumber.getParameterNames();
    }

    public NameValueList getParameters() {
        return this.telephoneNumber.getParameters();
    }

    @Override // javax.sip.header.Parameters
    public void removeParameter(String name) {
        this.telephoneNumber.removeParameter(name);
    }

    @Override // javax.sip.address.TelURL
    public void setPhoneContext(String phoneContext) throws ParseException {
        if (phoneContext == null) {
            removeParameter("phone-context");
        } else {
            setParameter("phone-context", phoneContext);
        }
    }

    @Override // javax.sip.address.TelURL
    public String getPhoneContext() {
        return getParameter("phone-context");
    }
}