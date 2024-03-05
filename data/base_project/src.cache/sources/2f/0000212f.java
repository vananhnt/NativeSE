package gov.nist.javax.sip.header;

import java.text.ParseException;
import javax.sip.header.ContentEncodingHeader;

/* loaded from: ContentEncoding.class */
public class ContentEncoding extends SIPHeader implements ContentEncodingHeader {
    private static final long serialVersionUID = 2034230276579558857L;
    protected String contentEncoding;

    public ContentEncoding() {
        super("Content-Encoding");
    }

    public ContentEncoding(String enc) {
        super("Content-Encoding");
        this.contentEncoding = enc;
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return this.contentEncoding;
    }

    @Override // javax.sip.header.Encoding
    public String getEncoding() {
        return this.contentEncoding;
    }

    @Override // javax.sip.header.Encoding
    public void setEncoding(String encoding) throws ParseException {
        if (encoding == null) {
            throw new NullPointerException("JAIN-SIP Exception,  encoding is null");
        }
        this.contentEncoding = encoding;
    }
}