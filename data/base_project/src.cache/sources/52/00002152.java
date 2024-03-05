package gov.nist.javax.sip.header;

/* loaded from: ProxyRequireList.class */
public class ProxyRequireList extends SIPHeaderList<ProxyRequire> {
    private static final long serialVersionUID = 5648630649476486042L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        ProxyRequireList retval = new ProxyRequireList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public ProxyRequireList() {
        super(ProxyRequire.class, "Proxy-Require");
    }
}