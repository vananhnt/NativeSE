package gov.nist.javax.sip.header;

/* loaded from: UnsupportedList.class */
public class UnsupportedList extends SIPHeaderList<Unsupported> {
    private static final long serialVersionUID = -4052610269407058661L;

    public UnsupportedList() {
        super(Unsupported.class, "Unsupported");
    }

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        UnsupportedList retval = new UnsupportedList();
        return retval.clonehlist(this.hlist);
    }
}