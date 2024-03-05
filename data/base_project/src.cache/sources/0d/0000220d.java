package gov.nist.javax.sip.parser;

import gov.nist.javax.sip.message.SIPMessage;

/* loaded from: SIPMessageListener.class */
public interface SIPMessageListener extends ParseExceptionListener {
    void processMessage(SIPMessage sIPMessage) throws Exception;
}