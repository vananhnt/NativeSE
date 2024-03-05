package javax.sip;

import javax.sip.address.Hop;
import javax.sip.message.Request;

/* loaded from: ClientTransaction.class */
public interface ClientTransaction extends Transaction {
    Request createAck() throws SipException;

    Request createCancel() throws SipException;

    void sendRequest() throws SipException;

    void alertIfStillInCallingStateBy(int i);

    Hop getNextHop();

    void setNotifyOnRetransmit(boolean z);
}