package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeaderList;

/* loaded from: SecurityVerifyList.class */
public class SecurityVerifyList extends SIPHeaderList<SecurityVerify> {
    private static final long serialVersionUID = 563201040577795125L;

    public SecurityVerifyList() {
        super(SecurityVerify.class, "Security-Verify");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        SecurityVerifyList retval = new SecurityVerifyList();
        return retval.clonehlist(this.hlist);
    }
}