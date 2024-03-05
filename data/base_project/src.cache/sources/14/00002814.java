package javax.sip;

/* loaded from: SipListener.class */
public interface SipListener {
    void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent);

    void processIOException(IOExceptionEvent iOExceptionEvent);

    void processRequest(RequestEvent requestEvent);

    void processResponse(ResponseEvent responseEvent);

    void processTimeout(TimeoutEvent timeoutEvent);

    void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent);
}