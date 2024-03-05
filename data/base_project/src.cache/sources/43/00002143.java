package gov.nist.javax.sip.header;

import javax.sip.InvalidArgumentException;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.TooManyHopsException;

/* loaded from: MaxForwards.class */
public class MaxForwards extends SIPHeader implements MaxForwardsHeader {
    private static final long serialVersionUID = -3096874323347175943L;
    protected int maxForwards;

    public MaxForwards() {
        super("Max-Forwards");
    }

    public MaxForwards(int m) throws InvalidArgumentException {
        super("Max-Forwards");
        setMaxForwards(m);
    }

    @Override // javax.sip.header.MaxForwardsHeader
    public int getMaxForwards() {
        return this.maxForwards;
    }

    @Override // javax.sip.header.MaxForwardsHeader
    public void setMaxForwards(int maxForwards) throws InvalidArgumentException {
        if (maxForwards < 0 || maxForwards > 255) {
            throw new InvalidArgumentException("bad max forwards value " + maxForwards);
        }
        this.maxForwards = maxForwards;
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return encodeBody(new StringBuffer()).toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.header.SIPHeader
    public StringBuffer encodeBody(StringBuffer buffer) {
        return buffer.append(this.maxForwards);
    }

    @Override // javax.sip.header.MaxForwardsHeader
    public boolean hasReachedZero() {
        return this.maxForwards == 0;
    }

    @Override // javax.sip.header.MaxForwardsHeader
    public void decrementMaxForwards() throws TooManyHopsException {
        if (this.maxForwards > 0) {
            this.maxForwards--;
            return;
        }
        throw new TooManyHopsException("has already reached 0!");
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MaxForwardsHeader) {
            MaxForwardsHeader o = (MaxForwardsHeader) other;
            return getMaxForwards() == o.getMaxForwards();
        }
        return false;
    }
}