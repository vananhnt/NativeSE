package gov.nist.javax.sip.address;

import javax.sip.address.Hop;
import javax.sip.address.Router;

/* loaded from: RouterExt.class */
public interface RouterExt extends Router {
    void transactionTimeout(Hop hop);
}