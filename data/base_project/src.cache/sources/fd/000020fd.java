package gov.nist.javax.sip.address;

import gov.nist.core.HostPort;
import gov.nist.core.Separators;
import javax.sip.address.Address;
import javax.sip.address.URI;

/* loaded from: AddressImpl.class */
public final class AddressImpl extends NetObject implements Address {
    private static final long serialVersionUID = 429592779568617259L;
    public static final int NAME_ADDR = 1;
    public static final int ADDRESS_SPEC = 2;
    public static final int WILD_CARD = 3;
    protected int addressType = 1;
    protected String displayName;
    protected GenericURI address;

    @Override // gov.nist.javax.sip.address.NetObject, gov.nist.core.GenericObject
    public boolean match(Object other) {
        if (other == null) {
            return true;
        }
        if (!(other instanceof Address)) {
            return false;
        }
        AddressImpl that = (AddressImpl) other;
        if (that.getMatcher() != null) {
            return that.getMatcher().match(encode());
        }
        if (that.displayName != null && this.displayName == null) {
            return false;
        }
        if (that.displayName == null) {
            return this.address.match(that.address);
        }
        return this.displayName.equalsIgnoreCase(that.displayName) && this.address.match(that.address);
    }

    public HostPort getHostPort() {
        if (!(this.address instanceof SipUri)) {
            throw new RuntimeException("address is not a SipUri");
        }
        SipUri uri = (SipUri) this.address;
        return uri.getHostPort();
    }

    @Override // javax.sip.address.Address
    public int getPort() {
        if (!(this.address instanceof SipUri)) {
            throw new RuntimeException("address is not a SipUri");
        }
        SipUri uri = (SipUri) this.address;
        return uri.getHostPort().getPort();
    }

    @Override // javax.sip.address.Address
    public String getUserAtHostPort() {
        if (this.address instanceof SipUri) {
            SipUri uri = (SipUri) this.address;
            return uri.getUserAtHostPort();
        }
        return this.address.toString();
    }

    @Override // javax.sip.address.Address
    public String getHost() {
        if (!(this.address instanceof SipUri)) {
            throw new RuntimeException("address is not a SipUri");
        }
        SipUri uri = (SipUri) this.address;
        return uri.getHostPort().getHost().getHostname();
    }

    public void removeParameter(String parameterName) {
        if (!(this.address instanceof SipUri)) {
            throw new RuntimeException("address is not a SipUri");
        }
        SipUri uri = (SipUri) this.address;
        uri.removeParameter(parameterName);
    }

    @Override // gov.nist.core.GenericObject
    public String encode() {
        return encode(new StringBuffer()).toString();
    }

    @Override // gov.nist.core.GenericObject
    public StringBuffer encode(StringBuffer buffer) {
        if (this.addressType == 3) {
            buffer.append('*');
        } else {
            if (this.displayName != null) {
                buffer.append(Separators.DOUBLE_QUOTE).append(this.displayName).append(Separators.DOUBLE_QUOTE).append(Separators.SP);
            }
            if (this.address != null) {
                if (this.addressType == 1 || this.displayName != null) {
                    buffer.append(Separators.LESS_THAN);
                }
                this.address.encode(buffer);
                if (this.addressType == 1 || this.displayName != null) {
                    buffer.append(Separators.GREATER_THAN);
                }
            }
        }
        return buffer;
    }

    public int getAddressType() {
        return this.addressType;
    }

    public void setAddressType(int atype) {
        this.addressType = atype;
    }

    @Override // javax.sip.address.Address
    public String getDisplayName() {
        return this.displayName;
    }

    @Override // javax.sip.address.Address
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.addressType = 1;
    }

    public void setAddess(URI address) {
        this.address = (GenericURI) address;
    }

    @Override // javax.sip.address.Address
    public int hashCode() {
        return this.address.hashCode();
    }

    @Override // gov.nist.javax.sip.address.NetObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Address) {
            Address o = (Address) other;
            return getURI().equals(o.getURI());
        }
        return false;
    }

    @Override // javax.sip.address.Address
    public boolean hasDisplayName() {
        return this.displayName != null;
    }

    public void removeDisplayName() {
        this.displayName = null;
    }

    @Override // javax.sip.address.Address
    public boolean isSIPAddress() {
        return this.address instanceof SipUri;
    }

    @Override // javax.sip.address.Address
    public URI getURI() {
        return this.address;
    }

    @Override // javax.sip.address.Address
    public boolean isWildcard() {
        return this.addressType == 3;
    }

    @Override // javax.sip.address.Address
    public void setURI(URI address) {
        this.address = (GenericURI) address;
    }

    public void setUser(String user) {
        ((SipUri) this.address).setUser(user);
    }

    @Override // javax.sip.address.Address
    public void setWildCardFlag() {
        this.addressType = 3;
        this.address = new SipUri();
        ((SipUri) this.address).setUser("*");
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        AddressImpl retval = (AddressImpl) super.clone();
        if (this.address != null) {
            retval.address = (GenericURI) this.address.clone();
        }
        return retval;
    }
}