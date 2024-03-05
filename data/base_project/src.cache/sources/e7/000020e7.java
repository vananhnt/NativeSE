package gov.nist.javax.sip;

import gov.nist.core.InternalErrorHandler;
import gov.nist.core.Separators;
import gov.nist.javax.sip.address.ParameterNames;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.Event;
import gov.nist.javax.sip.header.RetryAfter;
import gov.nist.javax.sip.header.Route;
import gov.nist.javax.sip.header.RouteList;
import gov.nist.javax.sip.message.MessageFactoryImpl;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.stack.MessageChannel;
import gov.nist.javax.sip.stack.SIPClientTransaction;
import gov.nist.javax.sip.stack.SIPDialog;
import gov.nist.javax.sip.stack.SIPServerTransaction;
import gov.nist.javax.sip.stack.SIPTransaction;
import gov.nist.javax.sip.stack.ServerRequestInterface;
import gov.nist.javax.sip.stack.ServerResponseInterface;
import java.io.IOException;
import javax.sip.ClientTransaction;
import javax.sip.DialogState;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.TransactionState;
import javax.sip.header.ReferToHeader;
import javax.sip.header.ServerHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/* loaded from: DialogFilter.class */
class DialogFilter implements ServerRequestInterface, ServerResponseInterface {
    protected SIPTransaction transactionChannel;
    protected ListeningPointImpl listeningPoint;
    private SipStackImpl sipStack;

    public DialogFilter(SipStackImpl sipStack) {
        this.sipStack = sipStack;
    }

    private void sendRequestPendingResponse(SIPRequest sipRequest, SIPServerTransaction transaction) {
        SIPResponse sipResponse = sipRequest.createResponse(491);
        ServerHeader serverHeader = MessageFactoryImpl.getDefaultServerHeader();
        if (serverHeader != null) {
            sipResponse.setHeader(serverHeader);
        }
        try {
            RetryAfter retryAfter = new RetryAfter();
            retryAfter.setRetryAfter(1);
            sipResponse.setHeader(retryAfter);
            if (sipRequest.getMethod().equals("INVITE")) {
                this.sipStack.addTransactionPendingAck(transaction);
            }
            transaction.sendResponse((Response) sipResponse);
            transaction.releaseSem();
        } catch (Exception ex) {
            this.sipStack.getStackLogger().logError("Problem sending error response", ex);
            transaction.releaseSem();
            this.sipStack.removeTransaction(transaction);
        }
    }

    private void sendBadRequestResponse(SIPRequest sipRequest, SIPServerTransaction transaction, String reasonPhrase) {
        SIPResponse sipResponse = sipRequest.createResponse(400);
        if (reasonPhrase != null) {
            sipResponse.setReasonPhrase(reasonPhrase);
        }
        ServerHeader serverHeader = MessageFactoryImpl.getDefaultServerHeader();
        if (serverHeader != null) {
            sipResponse.setHeader(serverHeader);
        }
        try {
            if (sipRequest.getMethod().equals("INVITE")) {
                this.sipStack.addTransactionPendingAck(transaction);
            }
            transaction.sendResponse((Response) sipResponse);
            transaction.releaseSem();
        } catch (Exception ex) {
            this.sipStack.getStackLogger().logError("Problem sending error response", ex);
            transaction.releaseSem();
            this.sipStack.removeTransaction(transaction);
        }
    }

    private void sendCallOrTransactionDoesNotExistResponse(SIPRequest sipRequest, SIPServerTransaction transaction) {
        SIPResponse sipResponse = sipRequest.createResponse(Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);
        ServerHeader serverHeader = MessageFactoryImpl.getDefaultServerHeader();
        if (serverHeader != null) {
            sipResponse.setHeader(serverHeader);
        }
        try {
            if (sipRequest.getMethod().equals("INVITE")) {
                this.sipStack.addTransactionPendingAck(transaction);
            }
            transaction.sendResponse((Response) sipResponse);
            transaction.releaseSem();
        } catch (Exception ex) {
            this.sipStack.getStackLogger().logError("Problem sending error response", ex);
            transaction.releaseSem();
            this.sipStack.removeTransaction(transaction);
        }
    }

    private void sendLoopDetectedResponse(SIPRequest sipRequest, SIPServerTransaction transaction) {
        SIPResponse sipResponse = sipRequest.createResponse(Response.LOOP_DETECTED);
        ServerHeader serverHeader = MessageFactoryImpl.getDefaultServerHeader();
        if (serverHeader != null) {
            sipResponse.setHeader(serverHeader);
        }
        try {
            this.sipStack.addTransactionPendingAck(transaction);
            transaction.sendResponse((Response) sipResponse);
            transaction.releaseSem();
        } catch (Exception ex) {
            this.sipStack.getStackLogger().logError("Problem sending error response", ex);
            transaction.releaseSem();
            this.sipStack.removeTransaction(transaction);
        }
    }

    private void sendServerInternalErrorResponse(SIPRequest sipRequest, SIPServerTransaction transaction) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Sending 500 response for out of sequence message");
        }
        SIPResponse sipResponse = sipRequest.createResponse(500);
        sipResponse.setReasonPhrase("Request out of order");
        if (MessageFactoryImpl.getDefaultServerHeader() != null) {
            ServerHeader serverHeader = MessageFactoryImpl.getDefaultServerHeader();
            sipResponse.setHeader(serverHeader);
        }
        try {
            RetryAfter retryAfter = new RetryAfter();
            retryAfter.setRetryAfter(10);
            sipResponse.setHeader(retryAfter);
            this.sipStack.addTransactionPendingAck(transaction);
            transaction.sendResponse((Response) sipResponse);
            transaction.releaseSem();
        } catch (Exception ex) {
            this.sipStack.getStackLogger().logError("Problem sending response", ex);
            transaction.releaseSem();
            this.sipStack.removeTransaction(transaction);
        }
    }

    @Override // gov.nist.javax.sip.stack.ServerRequestInterface
    public void processRequest(SIPRequest sipRequest, MessageChannel incomingMessageChannel) {
        RequestEvent sipEvent;
        int port;
        Contact contact;
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("PROCESSING INCOMING REQUEST " + sipRequest + " transactionChannel = " + this.transactionChannel + " listening point = " + this.listeningPoint.getIPAddress() + Separators.COLON + this.listeningPoint.getPort());
        }
        if (this.listeningPoint == null) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Dropping message: No listening point registered!");
                return;
            }
            return;
        }
        SipStackImpl sipStack = (SipStackImpl) this.transactionChannel.getSIPStack();
        SipProviderImpl sipProvider = this.listeningPoint.getProvider();
        if (sipProvider == null) {
            if (sipStack.isLoggingEnabled()) {
                sipStack.getStackLogger().logDebug("No provider - dropping !!");
                return;
            }
            return;
        }
        if (sipStack == null) {
            InternalErrorHandler.handleException("Egads! no sip stack!");
        }
        SIPServerTransaction transaction = (SIPServerTransaction) this.transactionChannel;
        if (transaction != null && sipStack.isLoggingEnabled()) {
            sipStack.getStackLogger().logDebug("transaction state = " + transaction.getState());
        }
        String dialogId = sipRequest.getDialogId(true);
        SIPDialog dialog = sipStack.getDialog(dialogId);
        if (dialog != null && sipProvider != dialog.getSipProvider() && (contact = dialog.getMyContactHeader()) != null) {
            SipUri contactUri = (SipUri) contact.getAddress().getURI();
            String ipAddress = contactUri.getHost();
            int contactPort = contactUri.getPort();
            String contactTransport = contactUri.getTransportParam();
            if (contactTransport == null) {
                contactTransport = ParameterNames.UDP;
            }
            if (contactPort == -1) {
                if (contactTransport.equals(ParameterNames.UDP) || contactTransport.equals(ParameterNames.TCP)) {
                    contactPort = 5060;
                } else {
                    contactPort = 5061;
                }
            }
            if (ipAddress != null && (!ipAddress.equals(this.listeningPoint.getIPAddress()) || contactPort != this.listeningPoint.getPort())) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("nulling dialog -- listening point mismatch!  " + contactPort + "  lp port = " + this.listeningPoint.getPort());
                }
                dialog = null;
            }
        }
        if (sipProvider.isAutomaticDialogSupportEnabled() && sipProvider.isDialogErrorsAutomaticallyHandled() && sipRequest.getToTag() == null) {
            SIPServerTransaction sipServerTransaction = sipStack.findMergedTransaction(sipRequest);
            if (sipServerTransaction != null) {
                sendLoopDetectedResponse(sipRequest, transaction);
                return;
            }
        }
        if (sipStack.isLoggingEnabled()) {
            sipStack.getStackLogger().logDebug("dialogId = " + dialogId);
            sipStack.getStackLogger().logDebug("dialog = " + dialog);
        }
        if (sipRequest.getHeader("Route") != null && transaction.getDialog() != null) {
            RouteList routes = sipRequest.getRouteHeaders();
            Route route = (Route) routes.getFirst();
            SipUri uri = (SipUri) route.getAddress().getURI();
            if (uri.getHostPort().hasPort()) {
                port = uri.getHostPort().getPort();
            } else if (this.listeningPoint.getTransport().equalsIgnoreCase("TLS")) {
                port = 5061;
            } else {
                port = 5060;
            }
            String host = uri.getHost();
            if ((host.equals(this.listeningPoint.getIPAddress()) || host.equalsIgnoreCase(this.listeningPoint.getSentBy())) && port == this.listeningPoint.getPort()) {
                if (routes.size() == 1) {
                    sipRequest.removeHeader("Route");
                } else {
                    routes.removeFirst();
                }
            }
        }
        if (sipRequest.getMethod().equals(Request.REFER) && dialog != null && sipProvider.isDialogErrorsAutomaticallyHandled()) {
            ReferToHeader sipHeader = (ReferToHeader) sipRequest.getHeader(ReferToHeader.NAME);
            if (sipHeader == null) {
                sendBadRequestResponse(sipRequest, transaction, "Refer-To header is missing");
                return;
            }
            SIPTransaction lastTransaction = dialog.getLastTransaction();
            if (lastTransaction != null && sipProvider.isDialogErrorsAutomaticallyHandled()) {
                SIPRequest lastRequest = (SIPRequest) lastTransaction.getRequest();
                if (lastTransaction instanceof SIPServerTransaction) {
                    if (!dialog.isAckSeen() && lastRequest.getMethod().equals("INVITE")) {
                        sendRequestPendingResponse(sipRequest, transaction);
                        return;
                    }
                } else if (lastTransaction instanceof SIPClientTransaction) {
                    long cseqno = lastRequest.getCSeqHeader().getSeqNumber();
                    String method = lastRequest.getMethod();
                    if (method.equals("INVITE") && !dialog.isAckSent(cseqno)) {
                        sendRequestPendingResponse(sipRequest, transaction);
                        return;
                    }
                }
            }
        } else if (sipRequest.getMethod().equals(Request.UPDATE)) {
            if (sipProvider.isAutomaticDialogSupportEnabled() && dialog == null) {
                sendCallOrTransactionDoesNotExistResponse(sipRequest, transaction);
                return;
            }
        } else if (sipRequest.getMethod().equals("ACK")) {
            if (transaction != null && transaction.isInviteTransaction()) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("Processing ACK for INVITE Tx ");
                }
            } else {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("Processing ACK for dialog " + dialog);
                }
                if (dialog == null) {
                    if (sipStack.isLoggingEnabled()) {
                        sipStack.getStackLogger().logDebug("Dialog does not exist " + sipRequest.getFirstLine() + " isServerTransaction = true");
                    }
                    SIPServerTransaction st = sipStack.getRetransmissionAlertTransaction(dialogId);
                    if (st != null && st.isRetransmissionAlertEnabled()) {
                        st.disableRetransmissionAlerts();
                    }
                    SIPServerTransaction ackTransaction = sipStack.findTransactionPendingAck(sipRequest);
                    if (ackTransaction != null) {
                        if (sipStack.isLoggingEnabled()) {
                            sipStack.getStackLogger().logDebug("Found Tx pending ACK");
                        }
                        try {
                            ackTransaction.setAckSeen();
                            sipStack.removeTransaction(ackTransaction);
                            sipStack.removeTransactionPendingAck(ackTransaction);
                            return;
                        } catch (Exception ex) {
                            if (sipStack.isLoggingEnabled()) {
                                sipStack.getStackLogger().logError("Problem terminating transaction", ex);
                                return;
                            }
                            return;
                        }
                    }
                } else if (!dialog.handleAck(transaction)) {
                    if (!dialog.isSequnceNumberValidation()) {
                        if (sipStack.isLoggingEnabled()) {
                            sipStack.getStackLogger().logDebug("Dialog exists with loose dialog validation " + sipRequest.getFirstLine() + " isServerTransaction = true dialog = " + dialog.getDialogId());
                        }
                        SIPServerTransaction st2 = sipStack.getRetransmissionAlertTransaction(dialogId);
                        if (st2 != null && st2.isRetransmissionAlertEnabled()) {
                            st2.disableRetransmissionAlerts();
                        }
                    } else {
                        if (sipStack.isLoggingEnabled()) {
                            sipStack.getStackLogger().logDebug("Dropping ACK - cannot find a transaction or dialog");
                        }
                        SIPServerTransaction ackTransaction2 = sipStack.findTransactionPendingAck(sipRequest);
                        if (ackTransaction2 != null) {
                            if (sipStack.isLoggingEnabled()) {
                                sipStack.getStackLogger().logDebug("Found Tx pending ACK");
                            }
                            try {
                                ackTransaction2.setAckSeen();
                                sipStack.removeTransaction(ackTransaction2);
                                sipStack.removeTransactionPendingAck(ackTransaction2);
                                return;
                            } catch (Exception ex2) {
                                if (sipStack.isLoggingEnabled()) {
                                    sipStack.getStackLogger().logError("Problem terminating transaction", ex2);
                                    return;
                                }
                                return;
                            }
                        }
                        return;
                    }
                } else {
                    transaction.passToListener();
                    dialog.addTransaction(transaction);
                    dialog.addRoute(sipRequest);
                    transaction.setDialog(dialog, dialogId);
                    if (sipRequest.getMethod().equals("INVITE") && sipProvider.isDialogErrorsAutomaticallyHandled()) {
                        sipStack.putInMergeTable(transaction, sipRequest);
                    }
                    if (sipStack.deliverTerminatedEventForAck) {
                        try {
                            sipStack.addTransaction(transaction);
                            transaction.scheduleAckRemoval();
                        } catch (IOException e) {
                        }
                    } else {
                        transaction.setMapped(true);
                    }
                }
            }
        } else if (sipRequest.getMethod().equals(Request.PRACK)) {
            if (sipStack.isLoggingEnabled()) {
                sipStack.getStackLogger().logDebug("Processing PRACK for dialog " + dialog);
            }
            if (dialog == null && sipProvider.isAutomaticDialogSupportEnabled()) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("Dialog does not exist " + sipRequest.getFirstLine() + " isServerTransaction = true");
                }
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("Sending 481 for PRACK - automatic dialog support is enabled -- cant find dialog!");
                }
                SIPResponse notExist = sipRequest.createResponse(Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);
                try {
                    sipProvider.sendResponse(notExist);
                } catch (SipException e2) {
                    sipStack.getStackLogger().logError("error sending response", e2);
                }
                if (transaction != null) {
                    sipStack.removeTransaction(transaction);
                    transaction.releaseSem();
                    return;
                }
                return;
            } else if (dialog != null) {
                if (!dialog.handlePrack(sipRequest)) {
                    if (sipStack.isLoggingEnabled()) {
                        sipStack.getStackLogger().logDebug("Dropping out of sequence PRACK ");
                    }
                    if (transaction != null) {
                        sipStack.removeTransaction(transaction);
                        transaction.releaseSem();
                        return;
                    }
                    return;
                }
                try {
                    sipStack.addTransaction(transaction);
                    dialog.addTransaction(transaction);
                    dialog.addRoute(sipRequest);
                    transaction.setDialog(dialog, dialogId);
                } catch (Exception ex3) {
                    InternalErrorHandler.handleException(ex3);
                }
            } else if (sipStack.isLoggingEnabled()) {
                sipStack.getStackLogger().logDebug("Processing PRACK without a DIALOG -- this must be a proxy element");
            }
        } else if (sipRequest.getMethod().equals("BYE")) {
            if (dialog != null && !dialog.isRequestConsumable(sipRequest)) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("Dropping out of sequence BYE " + dialog.getRemoteSeqNumber() + Separators.SP + sipRequest.getCSeq().getSeqNumber());
                }
                if (dialog.getRemoteSeqNumber() >= sipRequest.getCSeq().getSeqNumber() && transaction.getState() == TransactionState.TRYING) {
                    sendServerInternalErrorResponse(sipRequest, transaction);
                }
                if (transaction != null) {
                    sipStack.removeTransaction(transaction);
                    return;
                }
                return;
            } else if (dialog == null && sipProvider.isAutomaticDialogSupportEnabled()) {
                SIPResponse response = sipRequest.createResponse(Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);
                response.setReasonPhrase("Dialog Not Found");
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("dropping request -- automatic dialog support enabled and dialog does not exist!");
                }
                try {
                    transaction.sendResponse((Response) response);
                } catch (SipException ex4) {
                    sipStack.getStackLogger().logError("Error in sending response", ex4);
                }
                if (transaction != null) {
                    sipStack.removeTransaction(transaction);
                    transaction.releaseSem();
                    return;
                }
                return;
            } else {
                if (transaction != null && dialog != null) {
                    try {
                        if (sipProvider == dialog.getSipProvider()) {
                            sipStack.addTransaction(transaction);
                            dialog.addTransaction(transaction);
                            transaction.setDialog(dialog, dialogId);
                        }
                    } catch (IOException ex5) {
                        InternalErrorHandler.handleException(ex5);
                    }
                }
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("BYE Tx = " + transaction + " isMapped =" + transaction.isTransactionMapped());
                }
            }
        } else if (sipRequest.getMethod().equals(Request.CANCEL)) {
            SIPServerTransaction st3 = (SIPServerTransaction) sipStack.findCancelTransaction(sipRequest, true);
            if (sipStack.isLoggingEnabled()) {
                sipStack.getStackLogger().logDebug("Got a CANCEL, InviteServerTx = " + st3 + " cancel Server Tx ID = " + transaction + " isMapped = " + transaction.isTransactionMapped());
            }
            if (sipRequest.getMethod().equals(Request.CANCEL)) {
                if (st3 != null && st3.getState() == SIPTransaction.TERMINATED_STATE) {
                    if (sipStack.isLoggingEnabled()) {
                        sipStack.getStackLogger().logDebug("Too late to cancel Transaction");
                    }
                    try {
                        transaction.sendResponse(sipRequest.createResponse(200));
                        return;
                    } catch (Exception ex6) {
                        if (ex6.getCause() != null && (ex6.getCause() instanceof IOException)) {
                            st3.raiseIOExceptionEvent();
                            return;
                        }
                        return;
                    }
                } else if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("Cancel transaction = " + st3);
                }
            }
            if (transaction != null && st3 != null && st3.getDialog() != null) {
                transaction.setDialog((SIPDialog) st3.getDialog(), dialogId);
                dialog = (SIPDialog) st3.getDialog();
            } else if (st3 == null && sipProvider.isAutomaticDialogSupportEnabled() && transaction != null) {
                SIPResponse response2 = sipRequest.createResponse(Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("dropping request -- automatic dialog support enabled and INVITE ST does not exist!");
                }
                try {
                    sipProvider.sendResponse(response2);
                } catch (SipException ex7) {
                    InternalErrorHandler.handleException(ex7);
                }
                if (transaction != null) {
                    sipStack.removeTransaction(transaction);
                    transaction.releaseSem();
                    return;
                }
                return;
            }
            if (st3 != null) {
                if (transaction != null) {
                    try {
                        sipStack.addTransaction(transaction);
                        transaction.setPassToListener();
                        transaction.setInviteTransaction(st3);
                        st3.acquireSem();
                    } catch (Exception ex8) {
                        InternalErrorHandler.handleException(ex8);
                    }
                }
            }
        } else if (sipRequest.getMethod().equals("INVITE")) {
            SIPTransaction lastTransaction2 = dialog == null ? null : dialog.getInviteTransaction();
            if (dialog != null && transaction != null && lastTransaction2 != null && sipRequest.getCSeq().getSeqNumber() > dialog.getRemoteSeqNumber() && (lastTransaction2 instanceof SIPServerTransaction) && sipProvider.isDialogErrorsAutomaticallyHandled() && dialog.isSequnceNumberValidation() && lastTransaction2.isInviteTransaction() && lastTransaction2.getState() != TransactionState.COMPLETED && lastTransaction2.getState() != TransactionState.TERMINATED && lastTransaction2.getState() != TransactionState.CONFIRMED) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("Sending 500 response for out of sequence message");
                }
                sendServerInternalErrorResponse(sipRequest, transaction);
                return;
            }
            SIPTransaction lastTransaction3 = dialog == null ? null : dialog.getLastTransaction();
            if (dialog != null && sipProvider.isDialogErrorsAutomaticallyHandled() && lastTransaction3 != null && lastTransaction3.isInviteTransaction() && (lastTransaction3 instanceof ClientTransaction) && lastTransaction3.getLastResponse() != null && lastTransaction3.getLastResponse().getStatusCode() == 200 && !dialog.isAckSent(lastTransaction3.getLastResponse().getCSeq().getSeqNumber())) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("Sending 491 response for client Dialog ACK not sent.");
                }
                sendRequestPendingResponse(sipRequest, transaction);
                return;
            } else if (dialog != null && lastTransaction3 != null && sipProvider.isDialogErrorsAutomaticallyHandled() && lastTransaction3.isInviteTransaction() && (lastTransaction3 instanceof ServerTransaction) && !dialog.isAckSeen()) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("Sending 491 response for server Dialog ACK not seen.");
                }
                sendRequestPendingResponse(sipRequest, transaction);
                return;
            }
        }
        if (sipStack.isLoggingEnabled()) {
            sipStack.getStackLogger().logDebug("CHECK FOR OUT OF SEQ MESSAGE " + dialog + " transaction " + transaction);
        }
        if (dialog != null && transaction != null && !sipRequest.getMethod().equals("BYE") && !sipRequest.getMethod().equals(Request.CANCEL) && !sipRequest.getMethod().equals("ACK") && !sipRequest.getMethod().equals(Request.PRACK)) {
            if (!dialog.isRequestConsumable(sipRequest)) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("Dropping out of sequence message " + dialog.getRemoteSeqNumber() + Separators.SP + sipRequest.getCSeq());
                }
                if (dialog.getRemoteSeqNumber() < sipRequest.getCSeq().getSeqNumber() || !sipProvider.isDialogErrorsAutomaticallyHandled()) {
                    return;
                }
                if (transaction.getState() == TransactionState.TRYING || transaction.getState() == TransactionState.PROCEEDING) {
                    sendServerInternalErrorResponse(sipRequest, transaction);
                    return;
                }
                return;
            }
            try {
                if (sipProvider == dialog.getSipProvider()) {
                    sipStack.addTransaction(transaction);
                    dialog.addTransaction(transaction);
                    dialog.addRoute(sipRequest);
                    transaction.setDialog(dialog, dialogId);
                }
            } catch (IOException e3) {
                transaction.raiseIOExceptionEvent();
                sipStack.removeTransaction(transaction);
                return;
            }
        }
        if (sipStack.isLoggingEnabled()) {
            sipStack.getStackLogger().logDebug(sipRequest.getMethod() + " transaction.isMapped = " + transaction.isTransactionMapped());
        }
        if (dialog == null && sipRequest.getMethod().equals("NOTIFY")) {
            SIPClientTransaction pendingSubscribeClientTx = sipStack.findSubscribeTransaction(sipRequest, this.listeningPoint);
            if (sipStack.isLoggingEnabled()) {
                sipStack.getStackLogger().logDebug("PROCESSING NOTIFY  DIALOG == null " + pendingSubscribeClientTx);
            }
            if (sipProvider.isAutomaticDialogSupportEnabled() && pendingSubscribeClientTx == null && !sipStack.deliverUnsolicitedNotify) {
                try {
                    if (sipStack.isLoggingEnabled()) {
                        sipStack.getStackLogger().logDebug("Could not find Subscription for Notify Tx.");
                    }
                    Response errorResponse = sipRequest.createResponse(Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST);
                    errorResponse.setReasonPhrase("Subscription does not exist");
                    sipProvider.sendResponse(errorResponse);
                    return;
                } catch (Exception ex9) {
                    sipStack.getStackLogger().logError("Exception while sending error response statelessly", ex9);
                    return;
                }
            } else if (pendingSubscribeClientTx != null) {
                transaction.setPendingSubscribe(pendingSubscribeClientTx);
                SIPDialog subscriptionDialog = pendingSubscribeClientTx.getDefaultDialog();
                if (subscriptionDialog == null || subscriptionDialog.getDialogId() == null || !subscriptionDialog.getDialogId().equals(dialogId)) {
                    if (subscriptionDialog != null && subscriptionDialog.getDialogId() == null) {
                        subscriptionDialog.setDialogId(dialogId);
                    } else {
                        subscriptionDialog = pendingSubscribeClientTx.getDialog(dialogId);
                    }
                    if (sipStack.isLoggingEnabled()) {
                        sipStack.getStackLogger().logDebug("PROCESSING NOTIFY Subscribe DIALOG " + subscriptionDialog);
                    }
                    if (subscriptionDialog == null && (sipProvider.isAutomaticDialogSupportEnabled() || pendingSubscribeClientTx.getDefaultDialog() != null)) {
                        Event event = (Event) sipRequest.getHeader("Event");
                        if (sipStack.isEventForked(event.getEventType())) {
                            subscriptionDialog = SIPDialog.createFromNOTIFY(pendingSubscribeClientTx, transaction);
                        }
                    }
                    if (subscriptionDialog != null) {
                        transaction.setDialog(subscriptionDialog, dialogId);
                        subscriptionDialog.setState(DialogState.CONFIRMED.getValue());
                        sipStack.putDialog(subscriptionDialog);
                        pendingSubscribeClientTx.setDialog(subscriptionDialog, dialogId);
                        if (!transaction.isTransactionMapped()) {
                            this.sipStack.mapTransaction(transaction);
                            transaction.setPassToListener();
                            try {
                                this.sipStack.addTransaction(transaction);
                            } catch (Exception e4) {
                            }
                        }
                    }
                } else {
                    transaction.setDialog(subscriptionDialog, dialogId);
                    if (!transaction.isTransactionMapped()) {
                        this.sipStack.mapTransaction(transaction);
                        transaction.setPassToListener();
                        try {
                            this.sipStack.addTransaction(transaction);
                        } catch (Exception e5) {
                        }
                    }
                    sipStack.putDialog(subscriptionDialog);
                    if (pendingSubscribeClientTx != null) {
                        subscriptionDialog.addTransaction(pendingSubscribeClientTx);
                        pendingSubscribeClientTx.setDialog(subscriptionDialog, dialogId);
                    }
                }
                if (transaction != null && transaction.isTransactionMapped()) {
                    sipEvent = new RequestEvent(sipProvider, transaction, subscriptionDialog, sipRequest);
                } else {
                    sipEvent = new RequestEvent(sipProvider, null, subscriptionDialog, sipRequest);
                }
            } else {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("could not find subscribe tx");
                }
                sipEvent = new RequestEvent(sipProvider, null, null, sipRequest);
            }
        } else if (transaction != null && transaction.isTransactionMapped()) {
            sipEvent = new RequestEvent(sipProvider, transaction, dialog, sipRequest);
        } else {
            sipEvent = new RequestEvent(sipProvider, null, dialog, sipRequest);
        }
        sipProvider.handleEvent(sipEvent, transaction);
    }

    @Override // gov.nist.javax.sip.stack.ServerResponseInterface
    public void processResponse(SIPResponse response, MessageChannel incomingMessageChannel, SIPDialog dialog) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("PROCESSING INCOMING RESPONSE" + response.encodeMessage());
        }
        if (this.listeningPoint == null) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("Dropping message: No listening point registered!");
            }
        } else if (this.sipStack.checkBranchId() && !Utils.getInstance().responseBelongsToUs(response)) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("Dropping response - topmost VIA header does not originate from this stack");
            }
        } else {
            SipProviderImpl sipProvider = this.listeningPoint.getProvider();
            if (sipProvider == null) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logError("Dropping message:  no provider");
                }
            } else if (sipProvider.getSipListener() == null) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logError("No listener -- dropping response!");
                }
            } else {
                SIPClientTransaction transaction = (SIPClientTransaction) this.transactionChannel;
                SipStackImpl sipStackImpl = sipProvider.sipStack;
                if (this.sipStack.isLoggingEnabled()) {
                    sipStackImpl.getStackLogger().logDebug("Transaction = " + transaction);
                }
                if (transaction == null) {
                    if (dialog != null) {
                        if (response.getStatusCode() / 100 != 2) {
                            if (this.sipStack.isLoggingEnabled()) {
                                this.sipStack.getStackLogger().logDebug("Response is not a final response and dialog is found for response -- dropping response!");
                                return;
                            }
                            return;
                        } else if (dialog.getState() == DialogState.TERMINATED) {
                            if (this.sipStack.isLoggingEnabled()) {
                                this.sipStack.getStackLogger().logDebug("Dialog is terminated -- dropping response!");
                                return;
                            }
                            return;
                        } else {
                            boolean ackAlreadySent = false;
                            if (dialog.isAckSeen() && dialog.getLastAckSent() != null && dialog.getLastAckSent().getCSeq().getSeqNumber() == response.getCSeq().getSeqNumber()) {
                                ackAlreadySent = true;
                            }
                            if (ackAlreadySent && response.getCSeq().getMethod().equals(dialog.getMethod())) {
                                try {
                                    if (this.sipStack.isLoggingEnabled()) {
                                        this.sipStack.getStackLogger().logDebug("Retransmission of OK detected: Resending last ACK");
                                    }
                                    dialog.resendAck();
                                    return;
                                } catch (SipException ex) {
                                    this.sipStack.getStackLogger().logError("could not resend ack", ex);
                                }
                            }
                        }
                    }
                    if (this.sipStack.isLoggingEnabled()) {
                        this.sipStack.getStackLogger().logDebug("could not find tx, handling statelessly Dialog =  " + dialog);
                    }
                    ResponseEventExt sipEvent = new ResponseEventExt(sipProvider, transaction, dialog, response);
                    if (response.getCSeqHeader().getMethod().equals("INVITE")) {
                        SIPClientTransaction forked = this.sipStack.getForkedTransaction(response.getTransactionId());
                        sipEvent.setOriginalTransaction(forked);
                    }
                    sipProvider.handleEvent(sipEvent, transaction);
                    return;
                }
                ResponseEventExt responseEvent = new ResponseEventExt(sipProvider, transaction, dialog, response);
                if (response.getCSeqHeader().getMethod().equals("INVITE")) {
                    SIPClientTransaction forked2 = this.sipStack.getForkedTransaction(response.getTransactionId());
                    responseEvent.setOriginalTransaction(forked2);
                }
                if (dialog != null && response.getStatusCode() != 100) {
                    dialog.setLastResponse(transaction, response);
                    transaction.setDialog(dialog, dialog.getDialogId());
                }
                sipProvider.handleEvent(responseEvent, transaction);
            }
        }
    }

    public String getProcessingInfo() {
        return null;
    }

    @Override // gov.nist.javax.sip.stack.ServerResponseInterface
    public void processResponse(SIPResponse sipResponse, MessageChannel incomingChannel) {
        String dialogID = sipResponse.getDialogId(false);
        SIPDialog sipDialog = this.sipStack.getDialog(dialogID);
        String method = sipResponse.getCSeq().getMethod();
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("PROCESSING INCOMING RESPONSE: " + sipResponse.encodeMessage());
        }
        if (this.sipStack.checkBranchId() && !Utils.getInstance().responseBelongsToUs(sipResponse)) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("Detected stray response -- dropping");
            }
        } else if (this.listeningPoint == null) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Dropping message: No listening point registered!");
            }
        } else {
            SipProviderImpl sipProvider = this.listeningPoint.getProvider();
            if (sipProvider == null) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("Dropping message:  no provider");
                }
            } else if (sipProvider.getSipListener() == null) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("Dropping message:  no sipListener registered!");
                }
            } else {
                SIPClientTransaction transaction = (SIPClientTransaction) this.transactionChannel;
                if (sipDialog == null && transaction != null) {
                    sipDialog = transaction.getDialog(dialogID);
                    if (sipDialog != null && sipDialog.getState() == DialogState.TERMINATED) {
                        sipDialog = null;
                    }
                }
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("Transaction = " + transaction + " sipDialog = " + sipDialog);
                }
                if (this.transactionChannel != null) {
                    String originalFrom = ((SIPRequest) this.transactionChannel.getRequest()).getFromTag();
                    if ((originalFrom == null) ^ (sipResponse.getFrom().getTag() == null)) {
                        if (this.sipStack.isLoggingEnabled()) {
                            this.sipStack.getStackLogger().logDebug("From tag mismatch -- dropping response");
                            return;
                        }
                        return;
                    } else if (originalFrom != null && !originalFrom.equalsIgnoreCase(sipResponse.getFrom().getTag())) {
                        if (this.sipStack.isLoggingEnabled()) {
                            this.sipStack.getStackLogger().logDebug("From tag mismatch -- dropping response");
                            return;
                        }
                        return;
                    }
                }
                SipStackImpl sipStackImpl = this.sipStack;
                if (SipStackImpl.isDialogCreated(method) && sipResponse.getStatusCode() != 100 && sipResponse.getFrom().getTag() != null && sipResponse.getTo().getTag() != null && sipDialog == null) {
                    if (sipProvider.isAutomaticDialogSupportEnabled()) {
                        if (this.transactionChannel != null) {
                            if (sipDialog == null) {
                                sipDialog = this.sipStack.createDialog((SIPClientTransaction) this.transactionChannel, sipResponse);
                                this.transactionChannel.setDialog(sipDialog, sipResponse.getDialogId(false));
                            }
                        } else {
                            sipDialog = this.sipStack.createDialog(sipProvider, sipResponse);
                        }
                    }
                } else if (sipDialog != null && transaction == null && sipDialog.getState() != DialogState.TERMINATED) {
                    if (sipResponse.getStatusCode() / 100 != 2) {
                        if (this.sipStack.isLoggingEnabled()) {
                            this.sipStack.getStackLogger().logDebug("status code != 200 ; statusCode = " + sipResponse.getStatusCode());
                        }
                    } else if (sipDialog.getState() == DialogState.TERMINATED) {
                        if (this.sipStack.isLoggingEnabled()) {
                            this.sipStack.getStackLogger().logDebug("Dialog is terminated -- dropping response!");
                        }
                        if (sipResponse.getStatusCode() / 100 == 2 && sipResponse.getCSeq().getMethod().equals("INVITE")) {
                            try {
                                Request ackRequest = sipDialog.createAck(sipResponse.getCSeq().getSeqNumber());
                                sipDialog.sendAck(ackRequest);
                                return;
                            } catch (Exception ex) {
                                this.sipStack.getStackLogger().logError("Error creating ack", ex);
                                return;
                            }
                        }
                        return;
                    } else {
                        boolean ackAlreadySent = false;
                        if (sipDialog.isAckSeen() && sipDialog.getLastAckSent() != null && sipDialog.getLastAckSent().getCSeq().getSeqNumber() == sipResponse.getCSeq().getSeqNumber() && sipResponse.getDialogId(false).equals(sipDialog.getLastAckSent().getDialogId(false))) {
                            ackAlreadySent = true;
                        }
                        if (ackAlreadySent && sipResponse.getCSeq().getMethod().equals(sipDialog.getMethod())) {
                            try {
                                if (this.sipStack.isLoggingEnabled()) {
                                    this.sipStack.getStackLogger().logDebug("resending ACK");
                                }
                                sipDialog.resendAck();
                                return;
                            } catch (SipException e) {
                            }
                        }
                    }
                }
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("sending response to TU for processing ");
                }
                if (sipDialog != null && sipResponse.getStatusCode() != 100 && sipResponse.getTo().getTag() != null) {
                    sipDialog.setLastResponse(transaction, sipResponse);
                }
                ResponseEventExt responseEvent = new ResponseEventExt(sipProvider, transaction, sipDialog, sipResponse);
                if (sipResponse.getCSeq().getMethod().equals("INVITE")) {
                    ClientTransactionExt originalTx = this.sipStack.getForkedTransaction(sipResponse.getTransactionId());
                    responseEvent.setOriginalTransaction(originalTx);
                }
                sipProvider.handleEvent(responseEvent, transaction);
            }
        }
    }
}