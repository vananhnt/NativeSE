package javax.sip.message;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ListIterator;
import javax.sip.SipException;
import javax.sip.header.ContentDispositionHeader;
import javax.sip.header.ContentEncodingHeader;
import javax.sip.header.ContentLanguageHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.Header;

/* loaded from: Message.class */
public interface Message extends Cloneable, Serializable {
    void addFirst(Header header) throws SipException, NullPointerException;

    void addHeader(Header header);

    void addLast(Header header) throws SipException, NullPointerException;

    Header getHeader(String str);

    void setHeader(Header header);

    void removeFirst(String str) throws NullPointerException;

    void removeLast(String str) throws NullPointerException;

    void removeHeader(String str);

    ListIterator getHeaderNames();

    ListIterator getHeaders(String str);

    ListIterator getUnrecognizedHeaders();

    Object getApplicationData();

    void setApplicationData(Object obj);

    ContentLengthHeader getContentLength();

    void setContentLength(ContentLengthHeader contentLengthHeader);

    ContentLanguageHeader getContentLanguage();

    void setContentLanguage(ContentLanguageHeader contentLanguageHeader);

    ContentEncodingHeader getContentEncoding();

    void setContentEncoding(ContentEncodingHeader contentEncodingHeader);

    ContentDispositionHeader getContentDisposition();

    void setContentDisposition(ContentDispositionHeader contentDispositionHeader);

    Object getContent();

    byte[] getRawContent();

    void setContent(Object obj, ContentTypeHeader contentTypeHeader) throws ParseException;

    void removeContent();

    ExpiresHeader getExpires();

    void setExpires(ExpiresHeader expiresHeader);

    String getSIPVersion();

    void setSIPVersion(String str) throws ParseException;

    Object clone();

    boolean equals(Object obj);

    int hashCode();

    String toString();
}