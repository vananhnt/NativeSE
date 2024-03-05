package gov.nist.javax.sip.header;

import gov.nist.core.NameValue;
import gov.nist.core.NameValueList;
import gov.nist.core.Separators;
import gov.nist.javax.sip.address.AddressImpl;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.address.Address;
import javax.sip.header.ContactHeader;

/* loaded from: Contact.class */
public final class Contact extends AddressParametersHeader implements ContactHeader {
    private static final long serialVersionUID = 1677294871695706288L;
    public static final String ACTION = "action";
    public static final String PROXY = "proxy";
    public static final String REDIRECT = "redirect";
    public static final String EXPIRES = "expires";
    public static final String Q = "q";
    private ContactList contactList;
    protected boolean wildCardFlag;

    public Contact() {
        super("Contact");
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, javax.sip.header.Parameters
    public void setParameter(String name, String value) throws ParseException {
        NameValue nv = this.parameters.getNameValue(name);
        if (nv != null) {
            nv.setValueAsObject(value);
            return;
        }
        NameValue nv2 = new NameValue(name, value);
        if (name.equalsIgnoreCase("methods")) {
            nv2.setQuotedValue();
        }
        this.parameters.set(nv2);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        return encodeBody(new StringBuffer()).toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.header.SIPHeader
    public StringBuffer encodeBody(StringBuffer buffer) {
        if (this.wildCardFlag) {
            buffer.append('*');
        } else {
            if (this.address.getAddressType() == 1) {
                this.address.encode(buffer);
            } else {
                buffer.append('<');
                this.address.encode(buffer);
                buffer.append('>');
            }
            if (!this.parameters.isEmpty()) {
                buffer.append(Separators.SEMICOLON);
                this.parameters.encode(buffer);
            }
        }
        return buffer;
    }

    public ContactList getContactList() {
        return this.contactList;
    }

    public boolean getWildCardFlag() {
        return this.wildCardFlag;
    }

    @Override // gov.nist.javax.sip.header.AddressParametersHeader, javax.sip.header.HeaderAddress
    public Address getAddress() {
        return this.address;
    }

    public NameValueList getContactParms() {
        return this.parameters;
    }

    @Override // javax.sip.header.ContactHeader
    public int getExpires() {
        return getParameterAsInt("expires");
    }

    @Override // javax.sip.header.ContactHeader
    public void setExpires(int expiryDeltaSeconds) {
        Integer deltaSeconds = Integer.valueOf(expiryDeltaSeconds);
        this.parameters.set("expires", deltaSeconds);
    }

    @Override // javax.sip.header.ContactHeader
    public float getQValue() {
        return getParameterAsFloat("q");
    }

    public void setContactList(ContactList cl) {
        this.contactList = cl;
    }

    @Override // javax.sip.header.ContactHeader
    public void setWildCardFlag(boolean w) {
        this.wildCardFlag = true;
        this.address = new AddressImpl();
        this.address.setWildCardFlag();
    }

    @Override // gov.nist.javax.sip.header.AddressParametersHeader, javax.sip.header.HeaderAddress
    public void setAddress(Address address) {
        if (address == null) {
            throw new NullPointerException("null address");
        }
        this.address = (AddressImpl) address;
        this.wildCardFlag = false;
    }

    @Override // javax.sip.header.ContactHeader
    public void setQValue(float qValue) throws InvalidArgumentException {
        if (qValue != -1.0f && (qValue < 0.0f || qValue > 1.0f)) {
            throw new InvalidArgumentException("JAIN-SIP Exception, Contact, setQValue(), the qValue is not between 0 and 1");
        }
        this.parameters.set("q", Float.valueOf(qValue));
    }

    @Override // gov.nist.javax.sip.header.AddressParametersHeader, gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        Contact retval = (Contact) super.clone();
        if (this.contactList != null) {
            retval.contactList = (ContactList) this.contactList.clone();
        }
        return retval;
    }

    @Override // javax.sip.header.ContactHeader
    public void setWildCard() {
        setWildCardFlag(true);
    }

    @Override // javax.sip.header.ContactHeader
    public boolean isWildCard() {
        return this.address.isWildcard();
    }

    @Override // gov.nist.javax.sip.header.AddressParametersHeader, gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        return (other instanceof ContactHeader) && super.equals(other);
    }

    public void removeSipInstanceParam() {
        if (this.parameters != null) {
            this.parameters.delete(ParameterNames.SIP_INSTANCE);
        }
    }

    public String getSipInstanceParam() {
        return (String) this.parameters.getValue(ParameterNames.SIP_INSTANCE);
    }

    public void setSipInstanceParam(String value) {
        this.parameters.set(ParameterNames.SIP_INSTANCE, value);
    }

    public void removePubGruuParam() {
        if (this.parameters != null) {
            this.parameters.delete(ParameterNames.PUB_GRUU);
        }
    }

    public String getPubGruuParam() {
        return (String) this.parameters.getValue(ParameterNames.PUB_GRUU);
    }

    public void setPubGruuParam(String value) {
        this.parameters.set(ParameterNames.PUB_GRUU, value);
    }

    public void removeTempGruuParam() {
        if (this.parameters != null) {
            this.parameters.delete(ParameterNames.TEMP_GRUU);
        }
    }

    public String getTempGruuParam() {
        return (String) this.parameters.getValue(ParameterNames.TEMP_GRUU);
    }

    public void setTempGruuParam(String value) {
        this.parameters.set(ParameterNames.TEMP_GRUU, value);
    }
}