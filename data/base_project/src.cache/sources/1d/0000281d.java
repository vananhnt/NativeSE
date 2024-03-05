package javax.sip;

import java.util.EventObject;

/* loaded from: TransactionTerminatedEvent.class */
public class TransactionTerminatedEvent extends EventObject {
    private boolean mIsServerTransaction;
    private ServerTransaction mServerTransaction;
    private ClientTransaction mClientTransaction;

    public TransactionTerminatedEvent(Object source, ServerTransaction serverTransaction) {
        super(source);
        this.mServerTransaction = serverTransaction;
        this.mIsServerTransaction = true;
    }

    public TransactionTerminatedEvent(Object source, ClientTransaction clientTransaction) {
        super(source);
        this.mClientTransaction = clientTransaction;
        this.mIsServerTransaction = false;
    }

    public boolean isServerTransaction() {
        return this.mIsServerTransaction;
    }

    public ClientTransaction getClientTransaction() {
        return this.mClientTransaction;
    }

    public ServerTransaction getServerTransaction() {
        return this.mServerTransaction;
    }
}