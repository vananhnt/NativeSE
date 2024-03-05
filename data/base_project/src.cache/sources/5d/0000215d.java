package gov.nist.javax.sip.header;

/* loaded from: RequireList.class */
public final class RequireList extends SIPHeaderList<Require> {
    private static final long serialVersionUID = -1760629092046963213L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        RequireList retval = new RequireList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public RequireList() {
        super(Require.class, "Require");
    }
}