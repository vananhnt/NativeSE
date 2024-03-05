package javax.sip.header;

import javax.sip.InvalidArgumentException;

/* loaded from: MaxForwardsHeader.class */
public interface MaxForwardsHeader extends Header {
    public static final String NAME = "Max-Forwards";

    void decrementMaxForwards() throws TooManyHopsException;

    int getMaxForwards();

    void setMaxForwards(int i) throws InvalidArgumentException;

    boolean hasReachedZero();
}