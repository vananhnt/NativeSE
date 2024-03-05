package javax.sip.header;

import javax.sip.InvalidArgumentException;

/* loaded from: ExpiresHeader.class */
public interface ExpiresHeader extends Header {
    public static final String NAME = "Expires";

    int getExpires();

    void setExpires(int i) throws InvalidArgumentException;
}