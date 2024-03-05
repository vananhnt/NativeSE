package gov.nist.javax.sip.stack;

import java.util.EventObject;

/* loaded from: SIPTransactionErrorEvent.class */
public class SIPTransactionErrorEvent extends EventObject {
    private static final long serialVersionUID = -2713188471978065031L;
    public static final int TIMEOUT_ERROR = 1;
    public static final int TRANSPORT_ERROR = 2;
    public static final int TIMEOUT_RETRANSMIT = 3;
    private int errorID;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SIPTransactionErrorEvent(SIPTransaction sourceTransaction, int transactionErrorID) {
        super(sourceTransaction);
        this.errorID = transactionErrorID;
    }

    public int getErrorID() {
        return this.errorID;
    }
}