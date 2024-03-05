package gov.nist.javax.sip.stack;

import java.util.EventObject;

/* loaded from: SIPDialogErrorEvent.class */
public class SIPDialogErrorEvent extends EventObject {
    public static final int DIALOG_ACK_NOT_RECEIVED_TIMEOUT = 1;
    public static final int DIALOG_ACK_NOT_SENT_TIMEOUT = 2;
    public static final int DIALOG_REINVITE_TIMEOUT = 3;
    private int errorID;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SIPDialogErrorEvent(SIPDialog sourceDialog, int dialogErrorID) {
        super(sourceDialog);
        this.errorID = dialogErrorID;
    }

    public int getErrorID() {
        return this.errorID;
    }
}