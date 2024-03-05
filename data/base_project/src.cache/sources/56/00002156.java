package gov.nist.javax.sip.header;

/* loaded from: ReasonList.class */
public final class ReasonList extends SIPHeaderList<Reason> {
    private static final long serialVersionUID = 7459989997463160670L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        ReasonList retval = new ReasonList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public ReasonList() {
        super(Reason.class, "Reason");
    }
}