package javax.sip.header;

import javax.sip.InvalidArgumentException;

/* loaded from: AcceptHeader.class */
public interface AcceptHeader extends Header, MediaType, Parameters {
    public static final String NAME = "Accept";

    boolean allowsAllContentSubTypes();

    boolean allowsAllContentTypes();

    float getQValue();

    void setQValue(float f) throws InvalidArgumentException;

    boolean hasQValue();

    void removeQValue();
}