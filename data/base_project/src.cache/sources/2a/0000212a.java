package gov.nist.javax.sip.header;

/* loaded from: CallInfoList.class */
public class CallInfoList extends SIPHeaderList<CallInfo> {
    private static final long serialVersionUID = -4949850334388806423L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        CallInfoList retval = new CallInfoList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public CallInfoList() {
        super(CallInfo.class, "Call-Info");
    }
}