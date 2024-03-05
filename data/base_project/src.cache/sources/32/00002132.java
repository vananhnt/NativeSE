package gov.nist.javax.sip.header;

/* loaded from: ContentLanguageList.class */
public final class ContentLanguageList extends SIPHeaderList<ContentLanguage> {
    private static final long serialVersionUID = -5302265987802886465L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        ContentLanguageList retval = new ContentLanguageList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public ContentLanguageList() {
        super(ContentLanguage.class, "Content-Language");
    }
}