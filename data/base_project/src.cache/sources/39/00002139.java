package gov.nist.javax.sip.header;

import javax.sip.InvalidArgumentException;
import javax.sip.header.ExpiresHeader;

/* loaded from: Expires.class */
public class Expires extends SIPHeader implements ExpiresHeader {
    private static final long serialVersionUID = 3134344915465784267L;
    protected int expires;

    public Expires() {
        super("Expires");
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return encodeBody(new StringBuffer()).toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.header.SIPHeader
    public StringBuffer encodeBody(StringBuffer buffer) {
        return buffer.append(this.expires);
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