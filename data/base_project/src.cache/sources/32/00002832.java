package javax.sip.header;

import javax.sip.InvalidArgumentException;

/* loaded from: ContactHeader.class */
public interface ContactHeader extends HeaderAddress, Header, Parameters {
    public static final String NAME = "Contact";

    int getExpires();

    void setExpires(int i) throws InvalidArgumentException;

    float getQValue();

    void setQValue(float f) throws InvalidArgumentException;

    boolean isWildCard();

    void setWildCard();

    void setWildCardFlag(boolean z);
}