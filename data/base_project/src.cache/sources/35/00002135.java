package gov.nist.javax.sip.header;

import gov.nist.core.NameValueList;
import gov.nist.core.Separators;

/* loaded from: Credentials.class */
public class Credentials extends SIPObject {
    private static final long serialVersionUID = -6335592791505451524L;
    private static String DOMAIN = "domain";
    private static String REALM = "realm";
    private static String OPAQUE = "opaque";
    private static String RESPONSE = "response";
    private static String URI = "uri";
    private static String NONCE = "nonce";
    private static String CNONCE = "cnonce";
    private static String USERNAME = "username";
    protected String scheme;
    protected NameValueList parameters = new NameValueList();

    public Credentials() {
        this.parameters.setSeparator(Separators.COMMA);
    }

    public NameValueList getCredentials() {
        return this.parameters;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String s) {
        this.scheme = s;
    }

    public void setCredentials(NameValueList c) {
        this.parameters = c;
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public String encode() {
        String retval = this.scheme;
        if (!this.parameters.isEmpty()) {
            retval = retval + Separators.SP + this.parameters.encode();
        }
        return retval;
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        Credentials retval = (Credentials) super.clone();
        if (this.parameters != null) {
            retval.parameters = (NameValueList) this.parameters.clone();
        }
        return retval;
    }
}