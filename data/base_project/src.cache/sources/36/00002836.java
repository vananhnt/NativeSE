package javax.sip.header;

import javax.sip.InvalidArgumentException;

/* loaded from: ContentLengthHeader.class */
public interface ContentLengthHeader extends Header {
    public static final String NAME = "Content-Length";

    int getContentLength();

    void setContentLength(int i) throws InvalidArgumentException;
}