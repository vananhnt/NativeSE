package javax.sip;

import java.io.Serializable;
import javax.sip.message.Request;

/* loaded from: Transaction.class */
public interface Transaction extends Serializable {
    Object getApplicationData();

    void setApplicationData(Object obj);

    String getBranchId();

    Dialog getDialog();

    String getHost();

    String getPeerAddress();

    int getPeerPort();

    int getPort();

    Request getRequest();

    SipProvider getSipProvider();

    TransactionState getState();

    String getTransport();

    int getRetransmitTimer() throws UnsupportedOperationException;

    void setRetransmitTimer(int i) throws UnsupportedOperationException;

    void terminate() throws ObjectInUseException;
}