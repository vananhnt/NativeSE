package gov.nist.javax.sip.header;

import javax.sip.InvalidArgumentException;
import javax.sip.header.MinExpiresHeader;

/* loaded from: MinExpires.class */
public class MinExpires extends SIPHeader implements MinExpiresHeader {
    private static final long serialVersionUID = 7001828209606095801L;
    protected int expires;

    public MinExpires() {
        super("Min-Expires");
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return Integer.toString(this.expires);
    }

    @Override // javax.sip.header.ExpiresHeader
    public int getExpires() {
        return this.expires;
    }

    @Override // javax.sip.header.ExpiresHeader
    public void setExpires(int expires) throws InvalidArgumentException {
        if (expires < 0) {
            throw new InvalidArgumentException("bad argument " + expires);
        }
        this.expires = expires;
    }
}