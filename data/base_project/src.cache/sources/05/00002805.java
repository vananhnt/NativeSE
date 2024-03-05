package javax.sip;

import java.io.Serializable;
import java.util.Iterator;
import javax.sip.address.Address;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/* loaded from: Dialog.class */
public interface Dialog extends Serializable {
    Object getApplicationData();

    void setApplicationData(Object obj);

    CallIdHeader getCallId();

    String getDialogId();

    Transaction getFirstTransaction();

    Address getLocalParty();

    int getLocalSequenceNumber();

    long getLocalSeqNumber();

    String getLocalTag();

    Address getRemoteParty();

    int getRemoteSequenceNumber();

    long getRemoteSeqNumber();

    String getRemoteTag();

    Address getRemoteTarget();

    Iterator getRouteSet();

    SipProvider getSipProvider();

    DialogState getState();

    boolean isSecure();

    boolean isServer();

    void delete();

    void incrementLocalSequenceNumber();

    Request createRequest(String str) throws SipException;

    Request createAck(long j) throws InvalidArgumentException, SipException;

    Request createPrack(Response response) throws DialogDoesNotExistException, SipException;

    Response createReliableProvisionalResponse(int i) throws InvalidArgumentException, SipException;

    void sendRequest(ClientTransaction clientTransaction) throws TransactionDoesNotExistException, SipException;

    void sendAck(Request request) throws SipException;

    void sendReliableProvisionalResponse(Response response) throws SipException;

    void setBackToBackUserAgent();

    void terminateOnBye(boolean z) throws SipException;
}