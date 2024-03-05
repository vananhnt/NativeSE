package gov.nist.javax.sip.header;

/* loaded from: ViaList.class */
public final class ViaList extends SIPHeaderList<Via> {
    private static final long serialVersionUID = 3899679374556152313L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        ViaList retval = new ViaList();
        return retval.clonehlist(this.hlist);
    }

    public ViaList() {
        super(Via.class, "Via");
    }
}