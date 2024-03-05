package gov.nist.javax.sip.header;

/* loaded from: SupportedList.class */
public class SupportedList extends SIPHeaderList<Supported> {
    private static final long serialVersionUID = -4539299544895602367L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        SupportedList retval = new SupportedList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public SupportedList() {
        super(Supported.class, "Supported");
    }
}