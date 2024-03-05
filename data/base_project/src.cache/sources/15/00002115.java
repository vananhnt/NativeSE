package gov.nist.javax.sip.header;

/* loaded from: AcceptEncodingList.class */
public class AcceptEncodingList extends SIPHeaderList<AcceptEncoding> {
    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        AcceptEncodingList retval = new AcceptEncodingList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public AcceptEncodingList() {
        super(AcceptEncoding.class, "Accept-Encoding");
    }
}