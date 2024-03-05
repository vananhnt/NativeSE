package gov.nist.javax.sip.header;

import java.text.ParseException;
import javax.sip.header.ExtensionHeader;
import javax.sip.header.SIPIfMatchHeader;

/* loaded from: SIPIfMatch.class */
public class SIPIfMatch extends SIPHeader implements SIPIfMatchHeader, ExtensionHeader {
    private static final long serialVersionUID = 3833745477828359730L;
    protected String entityTag;

    public SIPIfMatch() {
        super("SIP-If-Match");
    }

    public SIPIfMatch(String etag) throws ParseException {
        this();
        setETag(etag);
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return this.entityTag;
    }

    @Override // javax.sip.header.SIPETagHeader
    public String getETag() {
        return this.entityTag;
    }

    @Override // javax.sip.header.SIPETagHeader
    public void setETag(String etag) throws ParseException {
        if (etag == null) {
            throw new NullPointerException("JAIN-SIP Exception,SIP-If-Match, setETag(), the etag parameter is null");
        }
        this.entityTag = etag;
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        setETag(value);
    }
}