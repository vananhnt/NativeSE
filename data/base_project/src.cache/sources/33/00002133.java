package gov.nist.javax.sip.header;

import javax.sip.InvalidArgumentException;
import javax.sip.header.ContentLengthHeader;

/* loaded from: ContentLength.class */
public class ContentLength extends SIPHeader implements ContentLengthHeader {
    private static final long serialVersionUID = 1187190542411037027L;
    protected Integer contentLength;

    public ContentLength() {
        super("Content-Length");
    }

    public ContentLength(int length) {
        super("Content-Length");
        this.contentLength = Integer.valueOf(length);
    }

    @Override // javax.sip.header.ContentLengthHeader
    public int getContentLength() {
        return this.contentLength.intValue();
    }

    @Override // javax.sip.header.ContentLengthHeader
    public void setContentLength(int contentLength) throws InvalidArgumentException {
        if (contentLength < 0) {
            throw new InvalidArgumentException("JAIN-SIP Exception, ContentLength, setContentLength(), the contentLength parameter is <0");
        }
        this.contentLength = Integer.valueOf(contentLength);
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return encodeBody(new StringBuffer()).toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.header.SIPHeader
    public StringBuffer encodeBody(StringBuffer buffer) {
        if (this.contentLength == null) {
            buffer.append("0");
        } else {
            buffer.append(this.contentLength.toString());
        }
        return buffer;
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean match(Object other) {
        if (other instanceof ContentLength) {
            return true;
        }
        return false;
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (other instanceof ContentLengthHeader) {
            ContentLengthHeader o = (ContentLengthHeader) other;
            return getContentLength() == o.getContentLength();
        }
        return false;
    }
}