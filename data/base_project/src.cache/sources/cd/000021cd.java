package gov.nist.javax.sip.message;

import java.util.Iterator;
import javax.sip.header.ContentTypeHeader;

/* loaded from: MultipartMimeContent.class */
public interface MultipartMimeContent {
    boolean add(Content content);

    ContentTypeHeader getContentTypeHeader();

    String toString();

    void addContent(Content content);

    Iterator<Content> getContents();

    int getContentCount();
}