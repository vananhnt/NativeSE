package gov.nist.javax.sip.header;

/* loaded from: RecordRouteList.class */
public class RecordRouteList extends SIPHeaderList<RecordRoute> {
    private static final long serialVersionUID = 1724940469426766691L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        RecordRouteList retval = new RecordRouteList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public RecordRouteList() {
        super(RecordRoute.class, "Record-Route");
    }
}