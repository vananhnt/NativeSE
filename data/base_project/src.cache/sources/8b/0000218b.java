package gov.nist.javax.sip.header.ims;

import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.header.SIPHeader;
import javax.sip.address.Address;

/* loaded from: AddressHeaderIms.class */
public abstract class AddressHeaderIms extends SIPHeader {
    protected AddressImpl address;

    @Override // gov.nist.javax.sip.header.SIPHeader
    public abstract String encodeBody();

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = (AddressImpl) address;
    }

    public AddressHeaderIms(String name) {
        super(name);
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        AddressHeaderIms retval = (AddressHeaderIms) super.clone();
        if (this.address != null) {
            retval.address = (AddressImpl) this.address.clone();
        }
        return retval;
    }
}