package gov.nist.javax.sip.header;

import gov.nist.javax.sip.address.AddressImpl;
import javax.sip.address.Address;
import javax.sip.header.HeaderAddress;
import javax.sip.header.Parameters;

/* loaded from: AddressParametersHeader.class */
public abstract class AddressParametersHeader extends ParametersHeader implements Parameters {
    protected AddressImpl address;

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = (AddressImpl) address;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AddressParametersHeader(String name) {
        super(name);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AddressParametersHeader(String name, boolean sync) {
        super(name, sync);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        AddressParametersHeader retval = (AddressParametersHeader) super.clone();
        if (this.address != null) {
            retval.address = (AddressImpl) this.address.clone();
        }
        return retval;
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if ((other instanceof HeaderAddress) && (other instanceof Parameters)) {
            HeaderAddress o = (HeaderAddress) other;
            return getAddress().equals(o.getAddress()) && equalParameters((Parameters) o);
        }
        return false;
    }
}