package gov.nist.javax.sip.header;

import java.util.Map;
import javax.sip.address.Address;
import javax.sip.header.Parameters;

/* loaded from: AddressParameters.class */
public interface AddressParameters extends Parameters {
    Address getAddress();

    void setAddress(Address address);

    Map<String, Map.Entry<String, String>> getParameters();
}