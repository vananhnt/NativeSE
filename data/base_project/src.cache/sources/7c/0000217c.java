package gov.nist.javax.sip.header;

/* loaded from: WWWAuthenticateList.class */
public class WWWAuthenticateList extends SIPHeaderList<WWWAuthenticate> {
    private static final long serialVersionUID = -6978902284285501346L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        WWWAuthenticateList retval = new WWWAuthenticateList();
        return retval.clonehlist(this.hlist);
    }

    public WWWAuthenticateList() {
        super(WWWAuthenticate.class, "WWW-Authenticate");
    }
}