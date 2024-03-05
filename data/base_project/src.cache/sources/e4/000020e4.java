package gov.nist.javax.sip;

import javax.sip.ClientTransaction;
import javax.sip.address.Hop;

/* loaded from: ClientTransactionExt.class */
public interface ClientTransactionExt extends ClientTransaction, TransactionExt {
    @Override // javax.sip.ClientTransaction
    void setNotifyOnRetransmit(boolean z);

    @Override // javax.sip.ClientTransaction
    void alertIfStillInCallingStateBy(int i);

    @Override // javax.sip.ClientTransaction
    Hop getNextHop();

    boolean isSecure();
}