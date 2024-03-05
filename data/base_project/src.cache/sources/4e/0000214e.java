package gov.nist.javax.sip.header;

/* loaded from: ProxyAuthenticateList.class */
public class ProxyAuthenticateList extends SIPHeaderList<ProxyAuthenticate> {
    private static final long serialVersionUID = 1;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        ProxyAuthenticateList retval = new ProxyAuthenticateList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public ProxyAuthenticateList() {
        super(ProxyAuthenticate.class, "Proxy-Authenticate");
    }
}