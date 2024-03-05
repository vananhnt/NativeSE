package javax.sip;

import java.util.EventObject;
import javax.sip.message.Response;

/* loaded from: ResponseEvent.class */
public class ResponseEvent extends EventObject {
    private Dialog mDialog;
    private Response mResponse;
    private ClientTransaction mClientTransaction;

    public ResponseEvent(Object source, ClientTransaction clientTransaction, Dialog dialog, Response response) {
        super(source);
        this.mDialog = dialog;
        this.mResponse = response;
        this.mClientTransaction = clientTransaction;
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public Response getResponse() {
        return this.mResponse;
    }

    public ClientTransaction getClientTransaction() {
        return this.mClientTransaction;
    }
}