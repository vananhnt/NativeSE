package gov.nist.javax.sip.message;

import gov.nist.core.Separators;
import javax.sip.header.ContentDispositionHeader;
import javax.sip.header.ContentTypeHeader;

/* loaded from: ContentImpl.class */
public class ContentImpl implements Content {
    private Object content;
    private String boundary;
    private ContentTypeHeader contentTypeHeader;
    private ContentDispositionHeader contentDispositionHeader;

    public ContentImpl(String content, String boundary) {
        this.content = content;
        this.boundary = boundary;
    }

    @Override // gov.nist.javax.sip.message.Content
    public void setContent(Object content) {
        this.content = content;
    }

    @Override // gov.nist.javax.sip.message.Content
    public ContentTypeHeader getContentTypeHeader() {
        return this.contentTypeHeader;
    }

    @Override // gov.nist.javax.sip.message.Content
    public Object getContent() {
        return this.content;
    }

    @Override // gov.nist.javax.sip.message.Content
    public String toString() {
        if (this.boundary == null) {
            return this.content.toString();
        }
        if (this.contentDispositionHeader != null) {
            return "--" + this.boundary + Separators.NEWLINE + getContentTypeHeader() + getContentDispositionHeader().toString() + Separators.NEWLINE + this.content.toString();
        }
        return "--" + this.boundary + Separators.NEWLINE + getContentTypeHeader() + Separators.NEWLINE + this.content.toString();
    }

    public void setContentDispositionHeader(ContentDispositionHeader contentDispositionHeader) {
        this.contentDispositionHeader = contentDispositionHeader;
    }

    @Override // gov.nist.javax.sip.message.Content
    public ContentDispositionHeader getContentDispositionHeader() {
        return this.contentDispositionHeader;
    }

    public void setContentTypeHeader(ContentTypeHeader contentTypeHeader) {
        this.contentTypeHeader = contentTypeHeader;
    }
}