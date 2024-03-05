package javax.sip.header;

import javax.sip.InvalidArgumentException;

/* loaded from: MimeVersionHeader.class */
public interface MimeVersionHeader extends Header {
    public static final String NAME = "MIME-Version";

    int getMajorVersion();

    void setMajorVersion(int i) throws InvalidArgumentException;

    int getMinorVersion();

    void setMinorVersion(int i) throws InvalidArgumentException;
}