package gov.nist.core.net;

import javax.sip.address.Hop;

/* loaded from: AddressResolver.class */
public interface AddressResolver {
    Hop resolveAddress(Hop hop);
}