package gov.nist.javax.sip.message;

import javax.sip.header.ContentDispositionHeader;
import javax.sip.header.ContentTypeHeader;

/* loaded from: Content.class */
public interface Content {
    void setContent(Object obj);

    Object getContent();

    ContentTypeHeader getContentTypeHeader();

    ContentDispositionHeader getContentDispositionHeader();

    String toString();
}