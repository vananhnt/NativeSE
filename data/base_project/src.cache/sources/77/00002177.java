package gov.nist.javax.sip.header;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.sip.header.UserAgentHeader;

/* loaded from: UserAgent.class */
public class UserAgent extends SIPHeader implements UserAgentHeader {
    private static final long serialVersionUID = 4561239179796364295L;
    protected List productTokens;

    private String encodeProduct() {
        StringBuffer tokens = new StringBuffer();
        ListIterator it = this.productTokens.listIterator();
        while (it.hasNext()) {
            tokens.append((String) it.next());
        }
        return tokens.toString();
    }

    @Override // javax.sip.header.UserAgentHeader
    public void addProductToken(String pt) {
        this.productTokens.add(pt);
    }

    public UserAgent() {
        super("User-Agent");
        this.productTokens = new LinkedList();
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return encodeProduct();
    }

    @Override // javax.sip.header.UserAgentHeader
    public ListIterator getProduct() {
        if (this.productTokens == null || this.productTokens.isEmpty()) {
            return null;
        }
        return this.productTokens.listIterator();
    }

    @Override // javax.sip.header.UserAgentHeader
    public void setProduct(List product) throws ParseException {
        if (product == null) {
            throw new NullPointerException("JAIN-SIP Exception, UserAgent, setProduct(), the  product parameter is null");
        }
        this.productTokens = product;
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        UserAgent retval = (UserAgent) super.clone();
        if (this.productTokens != null) {
            retval.productTokens = new LinkedList(this.productTokens);
        }
        return retval;
    }
}