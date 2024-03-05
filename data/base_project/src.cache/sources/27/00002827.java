package javax.sip.header;

import javax.sip.InvalidArgumentException;

/* loaded from: AcceptEncodingHeader.class */
public interface AcceptEncodingHeader extends Encoding, Header, Parameters {
    public static final String NAME = "Accept-Encoding";

    float getQValue();

    void setQValue(float f) throws InvalidArgumentException;
}