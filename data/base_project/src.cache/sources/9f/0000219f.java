package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeaderList;

/* loaded from: PMediaAuthorizationList.class */
public class PMediaAuthorizationList extends SIPHeaderList<PMediaAuthorization> {
    private static final long serialVersionUID = -8226328073989632317L;

    public PMediaAuthorizationList() {
        super(PMediaAuthorization.class, "P-Media-Authorization");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        PMediaAuthorizationList retval = new PMediaAuthorizationList();
        return retval.clonehlist(this.hlist);
    }
}