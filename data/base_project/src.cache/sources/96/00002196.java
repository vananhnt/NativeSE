package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.header.SIPHeaderList;

/* loaded from: PAssociatedURIList.class */
public class PAssociatedURIList extends SIPHeaderList<PAssociatedURI> {
    private static final long serialVersionUID = 4454306052557362851L;

    public PAssociatedURIList() {
        super(PAssociatedURI.class, "P-Associated-URI");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        PAssociatedURIList retval = new PAssociatedURIList();
        return retval.clonehlist(this.hlist);
    }
}