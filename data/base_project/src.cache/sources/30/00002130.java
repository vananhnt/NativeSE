package gov.nist.javax.sip.header;

/* loaded from: ContentEncodingList.class */
public final class ContentEncodingList extends SIPHeaderList<ContentEncoding> {
    private static final long serialVersionUID = 7365216146576273970L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        ContentEncodingList retval = new ContentEncodingList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public ContentEncodingList() {
        super(ContentEncoding.class, "Content-Encoding");
    }
}