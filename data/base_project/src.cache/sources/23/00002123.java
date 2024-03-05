package gov.nist.javax.sip.header;

/* loaded from: AuthenticationInfoList.class */
public class AuthenticationInfoList extends SIPHeaderList<AuthenticationInfo> {
    private static final long serialVersionUID = 1;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        AuthenticationInfoList retval = new AuthenticationInfoList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public AuthenticationInfoList() {
        super(AuthenticationInfo.class, "Authentication-Info");
    }
}