package gov.nist.javax.sip.header;

/* loaded from: AuthorizationList.class */
public class AuthorizationList extends SIPHeaderList<Authorization> {
    private static final long serialVersionUID = 1;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        AuthorizationList retval = new AuthorizationList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public AuthorizationList() {
        super(Authorization.class, "Authorization");
    }
}