package gov.nist.javax.sip.address;

import gov.nist.core.Separators;
import java.text.ParseException;
import javax.sip.address.URI;

/* loaded from: GenericURI.class */
public class GenericURI extends NetObject implements URI {
    private static final long serialVersionUID = 3237685256878068790L;
    public static final String SIP = "sip";
    public static final String SIPS = "sips";
    public static final String TEL = "tel";
    public static final String POSTDIAL = "postdial";
    public static final String PHONE_CONTEXT_TAG = "context-tag";
    public static final String ISUB = "isub";
    public static final String PROVIDER_TAG = "provider-tag";
    protected String uriString;
    protected String scheme;

    /* JADX INFO: Access modifiers changed from: protected */
    public GenericURI() {
    }

    public GenericURI(String uriString) throws ParseException {
        try {
            this.uriString = uriString;
            int i = uriString.indexOf(Separators.COLON);
            this.scheme = uriString.substring(0, i);
        } catch (Exception e) {
            throw new ParseException("GenericURI, Bad URI format", 0);
        }
    }

    @Override // gov.nist.core.GenericObject
    public String encode() {
        return this.uriString;
    }

    @Override // gov.nist.core.GenericObject
    public StringBuffer encode(StringBuffer buffer) {
        return buffer.append(this.uriString);
    }

    @Override // gov.nist.javax.sip.address.NetObject, javax.sip.address.URI
    public String toString() {
        return encode();
    }

    @Override // javax.sip.address.URI
    public String getScheme() {
        return this.scheme;
    }

    @Override // javax.sip.address.URI
    public boolean isSipURI() {
        return this instanceof SipUri;
    }

    @Override // gov.nist.javax.sip.address.NetObject, gov.nist.core.GenericObject
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof URI) {
            URI o = (URI) that;
            return toString().equalsIgnoreCase(o.toString());
        }
        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }
}