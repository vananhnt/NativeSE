package javax.sip.address;

import java.util.ListIterator;
import javax.sip.SipException;
import javax.sip.message.Request;

/* loaded from: Router.class */
public interface Router {
    Hop getNextHop(Request request) throws SipException;

    ListIterator getNextHops(Request request);

    Hop getOutboundProxy();
}