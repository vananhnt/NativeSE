package gov.nist.javax.sip.stack;

import java.util.EventListener;

/* loaded from: SIPTransactionEventListener.class */
public interface SIPTransactionEventListener extends EventListener {
    void transactionErrorEvent(SIPTransactionErrorEvent sIPTransactionErrorEvent);
}