package gov.nist.javax.sip.header;

import android.provider.Telephony;
import gov.nist.core.Separators;
import java.text.ParseException;
import javax.sip.header.ContentTypeHeader;

/* loaded from: ContentType.class */
public class ContentType extends ParametersHeader implements ContentTypeHeader {
    private static final long serialVersionUID = 8475682204373446610L;
    protected MediaRange mediaRange;

    public ContentType() {
        super("Content-Type");
    }

    public ContentType(String contentType, String contentSubtype) {
        this();
        setContentType(contentType, contentSubtype);
    }

    public int compareMediaRange(String media) {
        return (this.mediaRange.type + Separators.SLASH + this.mediaRange.subtype).compareToIgnoreCase(media);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return encodeBody(new StringBuffer()).toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.header.SIPHeader
    public StringBuffer encodeBody(StringBuffer buffer) {
        this.mediaRange.encode(buffer);
        if (hasParameters()) {
            buffer.append(Separators.SEMICOLON);
            this.parameters.encode(buffer);
        }
        return buffer;
    }

    public MediaRange getMediaRange() {
        return this.mediaRange;
    }

    public String getMediaType() {
        return this.mediaRange.type;
    }

    public String getMediaSubType() {
        return this.mediaRange.subtype;
    }

    @Override // javax.sip.header.MediaType
    public String getContentSubType() {
        if (this.mediaRange == null) {
            return null;
        }
        return this.mediaRange.getSubtype();
    }

    @Override // javax.sip.header.MediaType
    public String getContentType() {
        if (this.mediaRange == null) {
            return null;
        }
        return this.mediaRange.getType();
    }

    @Override // javax.sip.header.ContentTypeHeader
    public String getCharset() {
        return getParameter(Telephony.Mms.Addr.CHARSET);
    }

    public void setMediaRange(MediaRange m) {
        this.mediaRange = m;
    }

    @Override // javax.sip.header.ContentTypeHeader
    public void setContentType(String contentType, String contentSubType) {
        if (this.mediaRange == null) {
            this.mediaRange = new MediaRange();
        }
        this.mediaRange.setType(contentType);
        this.mediaRange.setSubtype(contentSubType);
    }

    @Override // javax.sip.header.MediaType
    public void setContentType(String contentType) throws ParseException {
        if (contentType == null) {
            throw new NullPointerException("null arg");
        }
        if (this.mediaRange == null) {
            this.mediaRange = new MediaRange();
        }
        this.mediaRange.setType(contentType);
    }

    @Override // javax.sip.header.MediaType
    public void setContentSubType(String contentType) throws ParseException {
        if (contentType == null) {
            throw new NullPointerException("null arg");
        }
        if (this.mediaRange == null) {
            this.mediaRange = new MediaRange();
        }
        this.mediaRange.setSubtype(contentType);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        ContentType retval = (ContentType) super.clone();
        if (this.mediaRange != null) {
            retval.mediaRange = (MediaRange) this.mediaRange.clone();
        }
        return retval;
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (other instanceof ContentTypeHeader) {
            ContentTypeHeader o = (ContentTypeHeader) other;
            return getContentType().equalsIgnoreCase(o.getContentType()) && getContentSubType().equalsIgnoreCase(o.getContentSubType()) && equalParameters(o);
        }
        return false;
    }
}