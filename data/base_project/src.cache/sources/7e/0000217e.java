package gov.nist.javax.sip.header;

/* loaded from: WarningList.class */
public class WarningList extends SIPHeaderList<Warning> {
    private static final long serialVersionUID = -1423278728898430175L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        WarningList retval = new WarningList();
        return retval.clonehlist(this.hlist);
    }

    public WarningList() {
        super(Warning.class, "Warning");
    }
}