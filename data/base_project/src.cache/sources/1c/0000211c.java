package gov.nist.javax.sip.header;

/* loaded from: AlertInfoList.class */
public class AlertInfoList extends SIPHeaderList<AlertInfo> {
    private static final long serialVersionUID = 1;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        AlertInfoList retval = new AlertInfoList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public AlertInfoList() {
        super(AlertInfo.class, "Alert-Info");
    }
}