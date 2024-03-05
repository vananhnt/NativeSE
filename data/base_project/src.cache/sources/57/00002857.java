package javax.sip.header;

import java.text.ParseException;

/* loaded from: SIPETagHeader.class */
public interface SIPETagHeader extends ExtensionHeader {
    public static final String NAME = "SIP-ETag";

    String getETag();

    void setETag(String str) throws ParseException;
}