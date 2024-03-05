package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeaderList;

/* loaded from: PrivacyList.class */
public class PrivacyList extends SIPHeaderList<Privacy> {
    private static final long serialVersionUID = 1798720509806307461L;

    public PrivacyList() {
        super(Privacy.class, "Privacy");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        PrivacyList retval = new PrivacyList();
        return retval.clonehlist(this.hlist);
    }
}