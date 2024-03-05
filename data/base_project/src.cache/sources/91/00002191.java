package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeaderList;

/* loaded from: PAssertedIdentityList.class */
public class PAssertedIdentityList extends SIPHeaderList<PAssertedIdentity> {
    private static final long serialVersionUID = -6465152445570308974L;

    public PAssertedIdentityList() {
        super(PAssertedIdentity.class, "P-Asserted-Identity");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        PAssertedIdentityList retval = new PAssertedIdentityList();
        return retval.clonehlist(this.hlist);
    }
}