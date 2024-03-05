package gov.nist.javax.sip;

import javax.sip.SipListener;

/* loaded from: SipListenerExt.class */
public interface SipListenerExt extends SipListener {
    void processDialogTimeout(DialogTimeoutEvent dialogTimeoutEvent);
}