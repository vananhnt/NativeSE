package gov.nist.javax.sip;

import gov.nist.javax.sip.stack.SIPTransaction;
import java.util.EventObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: EventWrapper.class */
public class EventWrapper {
    protected EventObject sipEvent;
    protected SIPTransaction transaction;

    /* JADX INFO: Access modifiers changed from: package-private */
    public EventWrapper(EventObject sipEvent, SIPTransaction transaction) {
        this.sipEvent = sipEvent;
        this.transaction = transaction;
    }
}