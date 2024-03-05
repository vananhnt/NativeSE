package gov.nist.javax.sip.stack;

import gov.nist.javax.sip.message.SIPRequest;

/* loaded from: ServerRequestInterface.class */
public interface ServerRequestInterface {
    void processRequest(SIPRequest sIPRequest, MessageChannel messageChannel);
}