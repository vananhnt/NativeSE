package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeaderList;

/* loaded from: PVisitedNetworkIDList.class */
public class PVisitedNetworkIDList extends SIPHeaderList<PVisitedNetworkID> {
    private static final long serialVersionUID = -4346667490341752478L;

    public PVisitedNetworkIDList() {
        super(PVisitedNetworkID.class, "P-Visited-Network-ID");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        PVisitedNetworkIDList retval = new PVisitedNetworkIDList();
        return retval.clonehlist(this.hlist);
    }
}