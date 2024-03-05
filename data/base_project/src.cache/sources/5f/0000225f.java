package gov.nist.javax.sip.stack;

import gov.nist.javax.sip.message.SIPResponse;

/* loaded from: ServerResponseInterface.class */
public interface ServerResponseInterface {
    void processResponse(SIPResponse sIPResponse, MessageChannel messageChannel, SIPDialog sIPDialog);

    void processResponse(SIPResponse sIPResponse, MessageChannel messageChannel);
}