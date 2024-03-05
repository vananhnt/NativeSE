package gov.nist.javax.sip.header;

/* loaded from: ErrorInfoList.class */
public class ErrorInfoList extends SIPHeaderList<ErrorInfo> {
    private static final long serialVersionUID = 1;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        ErrorInfoList retval = new ErrorInfoList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public ErrorInfoList() {
        super(ErrorInfo.class, "Error-Info");
    }
}