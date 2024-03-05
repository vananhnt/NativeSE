package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeader;
import java.text.ParseException;
import javax.sip.header.ExtensionHeader;

/* loaded from: Privacy.class */
public class Privacy extends SIPHeader implements PrivacyHeader, SIPHeaderNamesIms, ExtensionHeader {
    private String privacy;

    public Privacy() {
        super("Privacy");
    }

    public Privacy(String privacy) {
        this();
        this.privacy = privacy;
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return this.privacy;
    }

    @Override // gov.nist.javax.sip.header.ims.PrivacyHeader
    public String getPrivacy() {
        return this.privacy;
    }

    @Override // gov.nist.javax.sip.header.ims.PrivacyHeader
    public void setPrivacy(String privacy) throws ParseException {
        if (privacy == null || privacy == "") {
            throw new NullPointerException("JAIN-SIP Exception,  Privacy, setPrivacy(), privacy value is null or empty");
        }
        this.privacy = privacy;
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (other instanceof PrivacyHeader) {
            PrivacyHeader o = (PrivacyHeader) other;
            return getPrivacy().equals(o.getPrivacy());
        }
        return false;
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        Privacy retval = (Privacy) super.clone();
        if (this.privacy != null) {
            retval.privacy = this.privacy;
        }
        return retval;
    }
}