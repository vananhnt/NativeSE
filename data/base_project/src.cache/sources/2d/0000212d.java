package gov.nist.javax.sip.header;

/* loaded from: ContactList.class */
public class ContactList extends SIPHeaderList<Contact> {
    private static final long serialVersionUID = 1224806837758986814L;

    @Override // gov.nist.javax.sip.header.SIPHeaderList, gov.nist.core.GenericObject
    public Object clone() {
        ContactList retval = new ContactList();
        retval.clonehlist(this.hlist);
        return retval;
    }

    public ContactList() {
        super(Contact.class, "Contact");
    }
}