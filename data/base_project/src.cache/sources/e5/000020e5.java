package gov.nist.javax.sip;

import gov.nist.core.net.AddressResolver;
import gov.nist.javax.sip.stack.HopImpl;
import gov.nist.javax.sip.stack.MessageProcessor;
import javax.sip.address.Hop;

/* loaded from: DefaultAddressResolver.class */
public class DefaultAddressResolver implements AddressResolver {
    @Override // gov.nist.core.net.AddressResolver
    public Hop resolveAddress(Hop inputAddress) {
        if (inputAddress.getPort() != -1) {
            return inputAddress;
        }
        return new HopImpl(inputAddress.getHost(), MessageProcessor.getDefaultPort(inputAddress.getTransport()), inputAddress.getTransport());
    }
}