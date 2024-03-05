package gov.nist.javax.sip.stack;

import java.util.EventListener;

/* loaded from: SIPDialogEventListener.class */
public interface SIPDialogEventListener extends EventListener {
    void dialogErrorEvent(SIPDialogErrorEvent sIPDialogErrorEvent);
}