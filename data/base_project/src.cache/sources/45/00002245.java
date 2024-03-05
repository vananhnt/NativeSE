package gov.nist.javax.sip.stack;

import gov.nist.core.InternalErrorHandler;
import gov.nist.core.NameValueList;
import gov.nist.javax.sip.ClientTransactionExt;
import gov.nist.javax.sip.SIPConstants;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.RecordRoute;
import gov.nist.javax.sip.header.RecordRouteList;
import gov.nist.javax.sip.header.Route;
import gov.nist.javax.sip.header.RouteList;
import gov.nist.javax.sip.header.TimeStamp;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.stack.SIPTransaction;
import java.io.IOException;
import java.text.ParseException;
import java.util.ListIterator;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogState;
import javax.sip.InvalidArgumentException;
import javax.sip.ObjectInUseException;
import javax.sip.SipException;
import javax.sip.Timeout;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionState;
import javax.sip.address.Hop;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.message.Request;

/* loaded from: SIPClientTransaction.class */
public class SIPClientTransaction extends SIPTransaction implements ServerResponseInterface, ClientTransaction, ClientTransactionExt {
    private ConcurrentHashMap<String, SIPDialog> sipDialogs;
    private SIPRequest lastRequest;
    private int viaPort;
    private String viaHost;
    private transient ServerResponseInterface respondTo;
    private SIPDialog defaultDialog;
    private Hop nextHop;
    private boolean notifyOnRetransmit;
    private boolean timeoutIfStillInCallingState;
    private int callingStateTimeoutCount;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPClientTransaction.sendMessage(gov.nist.javax.sip.message.SIPMessage):void, file: SIPClientTransaction.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // gov.nist.javax.sip.stack.SIPTransaction, gov.nist.javax.sip.stack.MessageChannel
    public void sendMessage(gov.nist.javax.sip.message.SIPMessage r1) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPClientTransaction.sendMessage(gov.nist.javax.sip.message.SIPMessage):void, file: SIPClientTransaction.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPClientTransaction.sendMessage(gov.nist.javax.sip.message.SIPMessage):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPClientTransaction.inviteClientTransaction(gov.nist.javax.sip.message.SIPResponse, gov.nist.javax.sip.stack.MessageChannel, gov.nist.javax.sip.stack.SIPDialog):void, file: SIPClientTransaction.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void inviteClientTransaction(gov.nist.javax.sip.message.SIPResponse r1, gov.nist.javax.sip.stack.MessageChannel r2, gov.nist.javax.sip.stack.SIPDialog r3) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPClientTransaction.inviteClientTransaction(gov.nist.javax.sip.message.SIPResponse, gov.nist.javax.sip.stack.MessageChannel, gov.nist.javax.sip.stack.SIPDialog):void, file: SIPClientTransaction.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPClientTransaction.inviteClientTransaction(gov.nist.javax.sip.message.SIPResponse, gov.nist.javax.sip.stack.MessageChannel, gov.nist.javax.sip.stack.SIPDialog):void");
    }

    /* loaded from: SIPClientTransaction$TransactionTimer.class */
    public class TransactionTimer extends SIPStackTimerTask {
        public TransactionTimer() {
        }

        @Override // gov.nist.javax.sip.stack.SIPStackTimerTask
        protected void runTask() {
            SIPClientTransaction clientTransaction = SIPClientTransaction.this;
            SIPTransactionStack sipStack = clientTransaction.sipStack;
            if (clientTransaction.isTerminated()) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("removing  = " + clientTransaction + " isReliable " + clientTransaction.isReliable());
                }
                sipStack.removeTransaction(clientTransaction);
                try {
                    cancel();
                } catch (IllegalStateException e) {
                    if (!sipStack.isAlive()) {
                        return;
                    }
                }
                if (!sipStack.cacheClientConnections && clientTransaction.isReliable()) {
                    MessageChannel messageChannel = clientTransaction.getMessageChannel();
                    int newUseCount = messageChannel.useCount - 1;
                    messageChannel.useCount = newUseCount;
                    if (newUseCount <= 0) {
                        TimerTask myTimer = new SIPTransaction.LingerTimer();
                        sipStack.getTimer().schedule(myTimer, 8000L);
                        return;
                    }
                    return;
                } else if (sipStack.isLoggingEnabled() && clientTransaction.isReliable()) {
                    int useCount = clientTransaction.getMessageChannel().useCount;
                    if (sipStack.isLoggingEnabled()) {
                        sipStack.getStackLogger().logDebug("Client Use Count = " + useCount);
                        return;
                    }
                    return;
                } else {
                    return;
                }
            }
            clientTransaction.fireTimer();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SIPClientTransaction(SIPTransactionStack newSIPStack, MessageChannel newChannelToUse) {
        super(newSIPStack, newChannelToUse);
        setBranch(Utils.getInstance().generateBranchId());
        this.messageProcessor = newChannelToUse.messageProcessor;
        setEncapsulatedChannel(newChannelToUse);
        this.notifyOnRetransmit = false;
        this.timeoutIfStillInCallingState = false;
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Creating clientTransaction " + this);
            this.sipStack.getStackLogger().logStackTrace();
        }
        this.sipDialogs = new ConcurrentHashMap<>();
    }

    public void setResponseInterface(ServerResponseInterface newRespondTo) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Setting response interface for " + this + " to " + newRespondTo);
            if (newRespondTo == null) {
                this.sipStack.getStackLogger().logStackTrace();
                this.sipStack.getStackLogger().logDebug("WARNING -- setting to null!");
            }
        }
        this.respondTo = newRespondTo;
    }

    public MessageChannel getRequestChannel() {
        return this;
    }

    @Override // gov.nist.javax.sip.stack.SIPTransaction
    public boolean isMessagePartOfTransaction(SIPMessage messageToTest) {
        ViaList viaHeaders = messageToTest.getViaHeaders();
        String messageBranch = ((Via) viaHeaders.getFirst()).getBranch();
        boolean rfc3261Compliant = getBranch() != null && messageBranch != null && getBranch().toLowerCase().startsWith(SIPConstants.BRANCH_MAGIC_COOKIE_LOWER_CASE) && messageBranch.toLowerCase().startsWith(SIPConstants.BRANCH_MAGIC_COOKIE_LOWER_CASE);
        boolean transactionMatches = false;
        if (TransactionState.COMPLETED == getState()) {
            if (rfc3261Compliant) {
                transactionMatches = getBranch().equalsIgnoreCase(((Via) viaHeaders.getFirst()).getBranch()) && getMethod().equals(messageToTest.getCSeq().getMethod());
            } else {
                transactionMatches = getBranch().equals(messageToTest.getTransactionId());
            }
        } else if (!isTerminated()) {
            if (rfc3261Compliant) {
                if (viaHeaders != null && getBranch().equalsIgnoreCase(((Via) viaHeaders.getFirst()).getBranch())) {
                    transactionMatches = getOriginalRequest().getCSeq().getMethod().equals(messageToTest.getCSeq().getMethod());
                }
            } else {
                transactionMatches = getBranch() != null ? getBranch().equalsIgnoreCase(messageToTest.getTransactionId()) : getOriginalRequest().getTransactionId().equalsIgnoreCase(messageToTest.getTransactionId());
            }
        }
        return transactionMatches;
    }

    @Override // gov.nist.javax.sip.stack.ServerResponseInterface
    public synchronized void processResponse(SIPResponse transactionResponse, MessageChannel sourceChannel, SIPDialog dialog) {
        if (getState() == null) {
            return;
        }
        if ((TransactionState.COMPLETED == getState() || TransactionState.TERMINATED == getState()) && transactionResponse.getStatusCode() / 100 == 1) {
            return;
        }
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("processing " + transactionResponse.getFirstLine() + "current state = " + getState());
            this.sipStack.getStackLogger().logDebug("dialog = " + dialog);
        }
        this.lastResponse = transactionResponse;
        try {
            if (isInviteTransaction()) {
                inviteClientTransaction(transactionResponse, sourceChannel, dialog);
            } else {
                nonInviteClientTransaction(transactionResponse, sourceChannel, dialog);
            }
        } catch (IOException ex) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logException(ex);
            }
            setState(TransactionState.TERMINATED);
            raiseErrorEvent(2);
        }
    }

    private void nonInviteClientTransaction(SIPResponse transactionResponse, MessageChannel sourceChannel, SIPDialog sipDialog) throws IOException {
        int statusCode = transactionResponse.getStatusCode();
        if (TransactionState.TRYING == getState()) {
            if (statusCode / 100 == 1) {
                setState(TransactionState.PROCEEDING);
                enableRetransmissionTimer(8);
                enableTimeoutTimer(64);
                if (this.respondTo != null) {
                    this.respondTo.processResponse(transactionResponse, this, sipDialog);
                } else {
                    semRelease();
                }
            } else if (200 <= statusCode && statusCode <= 699) {
                if (this.respondTo != null) {
                    this.respondTo.processResponse(transactionResponse, this, sipDialog);
                } else {
                    semRelease();
                }
                if (!isReliable()) {
                    setState(TransactionState.COMPLETED);
                    enableTimeoutTimer(this.TIMER_K);
                    return;
                }
                setState(TransactionState.TERMINATED);
            }
        } else if (TransactionState.PROCEEDING == getState()) {
            if (statusCode / 100 == 1) {
                if (this.respondTo != null) {
                    this.respondTo.processResponse(transactionResponse, this, sipDialog);
                } else {
                    semRelease();
                }
            } else if (200 <= statusCode && statusCode <= 699) {
                if (this.respondTo != null) {
                    this.respondTo.processResponse(transactionResponse, this, sipDialog);
                } else {
                    semRelease();
                }
                disableRetransmissionTimer();
                disableTimeoutTimer();
                if (!isReliable()) {
                    setState(TransactionState.COMPLETED);
                    enableTimeoutTimer(this.TIMER_K);
                    return;
                }
                setState(TransactionState.TERMINATED);
            }
        } else {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug(" Not sending response to TU! " + getState());
            }
            semRelease();
        }
    }

    @Override // javax.sip.ClientTransaction
    public void sendRequest() throws SipException {
        SIPDialog dialog;
        SIPRequest sipRequest = getOriginalRequest();
        if (getState() != null) {
            throw new SipException("Request already sent");
        }
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("sendRequest() " + sipRequest);
        }
        try {
            sipRequest.checkHeaders();
            if (getMethod().equals("SUBSCRIBE") && sipRequest.getHeader("Expires") == null && this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logWarning("Expires header missing in outgoing subscribe -- Notifier will assume implied value on event package");
            }
            try {
                if (getOriginalRequest().getMethod().equals(Request.CANCEL) && this.sipStack.isCancelClientTransactionChecked()) {
                    SIPClientTransaction ct = (SIPClientTransaction) this.sipStack.findCancelTransaction(getOriginalRequest(), false);
                    if (ct == null) {
                        throw new SipException("Could not find original tx to cancel. RFC 3261 9.1");
                    }
                    if (ct.getState() == null) {
                        throw new SipException("State is null no provisional response yet -- cannot cancel RFC 3261 9.1");
                    }
                    if (!ct.getMethod().equals("INVITE")) {
                        throw new SipException("Cannot cancel non-invite requests RFC 3261 9.1");
                    }
                } else if (getOriginalRequest().getMethod().equals("BYE") || getOriginalRequest().getMethod().equals("NOTIFY")) {
                    SIPDialog dialog2 = this.sipStack.getDialog(getOriginalRequest().getDialogId(false));
                    if (getSipProvider().isAutomaticDialogSupportEnabled() && dialog2 != null) {
                        throw new SipException("Dialog is present and AutomaticDialogSupport is enabled for  the provider -- Send the Request using the Dialog.sendRequest(transaction)");
                    }
                }
                if (getMethod().equals("INVITE") && (dialog = getDefaultDialog()) != null && dialog.isBackToBackUserAgent() && !dialog.takeAckSem()) {
                    throw new SipException("Failed to take ACK semaphore");
                }
                this.isMapped = true;
                sendMessage(sipRequest);
            } catch (IOException ex) {
                setState(TransactionState.TERMINATED);
                throw new SipException("IO Error sending request", ex);
            }
        } catch (ParseException ex2) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("missing required header");
            }
            throw new SipException(ex2.getMessage());
        }
    }

    @Override // gov.nist.javax.sip.stack.SIPTransaction
    protected void fireRetransmissionTimer() {
        try {
            if (getState() == null || !this.isMapped) {
                return;
            }
            boolean inv = isInviteTransaction();
            TransactionState s = getState();
            if (((inv && TransactionState.CALLING == s) || (!inv && (TransactionState.TRYING == s || TransactionState.PROCEEDING == s))) && this.lastRequest != null) {
                if (this.sipStack.generateTimeStampHeader && this.lastRequest.getHeader("Timestamp") != null) {
                    long milisec = System.currentTimeMillis();
                    TimeStamp timeStamp = new TimeStamp();
                    try {
                        timeStamp.setTimeStamp((float) milisec);
                    } catch (InvalidArgumentException ex) {
                        InternalErrorHandler.handleException(ex);
                    }
                    this.lastRequest.setHeader(timeStamp);
                }
                super.sendMessage(this.lastRequest);
                if (this.notifyOnRetransmit) {
                    TimeoutEvent txTimeout = new TimeoutEvent(getSipProvider(), this, Timeout.RETRANSMIT);
                    getSipProvider().handleEvent(txTimeout, this);
                }
                if (this.timeoutIfStillInCallingState && getState() == TransactionState.CALLING) {
                    this.callingStateTimeoutCount--;
                    if (this.callingStateTimeoutCount == 0) {
                        TimeoutEvent timeoutEvent = new TimeoutEvent(getSipProvider(), this, Timeout.RETRANSMIT);
                        getSipProvider().handleEvent(timeoutEvent, this);
                        this.timeoutIfStillInCallingState = false;
                    }
                }
            }
        } catch (IOException e) {
            raiseIOExceptionEvent();
            raiseErrorEvent(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.stack.SIPTransaction
    public void fireTimeoutTimer() {
        SIPClientTransaction inviteTx;
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("fireTimeoutTimer " + this);
        }
        SIPDialog dialog = (SIPDialog) getDialog();
        if (TransactionState.CALLING == getState() || TransactionState.TRYING == getState() || TransactionState.PROCEEDING == getState()) {
            if (dialog != null && (dialog.getState() == null || dialog.getState() == DialogState.EARLY)) {
                getSIPStack();
                if (SIPTransactionStack.isDialogCreated(getOriginalRequest().getMethod())) {
                    dialog.delete();
                }
            } else if (dialog != null && getOriginalRequest().getMethod().equalsIgnoreCase("BYE") && dialog.isTerminatedOnBye()) {
                dialog.delete();
            }
        }
        if (TransactionState.COMPLETED != getState()) {
            raiseErrorEvent(1);
            if (!getOriginalRequest().getMethod().equalsIgnoreCase(Request.CANCEL) || (inviteTx = (SIPClientTransaction) getOriginalRequest().getInviteTransaction()) == null) {
                return;
            }
            if ((inviteTx.getState() == TransactionState.CALLING || inviteTx.getState() == TransactionState.PROCEEDING) && inviteTx.getDialog() != null) {
                inviteTx.setState(TransactionState.TERMINATED);
                return;
            }
            return;
        }
        setState(TransactionState.TERMINATED);
    }

    @Override // javax.sip.ClientTransaction
    public Request createCancel() throws SipException {
        SIPRequest originalRequest = getOriginalRequest();
        if (originalRequest == null) {
            throw new SipException("Bad state " + getState());
        }
        if (!originalRequest.getMethod().equals("INVITE")) {
            throw new SipException("Only INIVTE may be cancelled");
        }
        if (originalRequest.getMethod().equalsIgnoreCase("ACK")) {
            throw new SipException("Cannot Cancel ACK!");
        }
        SIPRequest cancelRequest = originalRequest.createCancelRequest();
        cancelRequest.setInviteTransaction(this);
        return cancelRequest;
    }

    @Override // javax.sip.ClientTransaction
    public Request createAck() throws SipException {
        SIPRequest originalRequest = getOriginalRequest();
        if (originalRequest == null) {
            throw new SipException("bad state " + getState());
        }
        if (getMethod().equalsIgnoreCase("ACK")) {
            throw new SipException("Cannot ACK an ACK!");
        }
        if (this.lastResponse == null) {
            throw new SipException("bad Transaction state");
        }
        if (this.lastResponse.getStatusCode() < 200) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("lastResponse = " + this.lastResponse);
            }
            throw new SipException("Cannot ACK a provisional response!");
        }
        SIPRequest ackRequest = originalRequest.createAckRequest((To) this.lastResponse.getTo());
        RecordRouteList recordRouteList = this.lastResponse.getRecordRouteHeaders();
        if (recordRouteList == null) {
            if (this.lastResponse.getContactHeaders() != null && this.lastResponse.getStatusCode() / 100 != 3) {
                Contact contact = (Contact) this.lastResponse.getContactHeaders().getFirst();
                URI uri = (URI) contact.getAddress().getURI().clone();
                ackRequest.setRequestURI(uri);
            }
            return ackRequest;
        }
        ackRequest.removeHeader("Route");
        RouteList routeList = new RouteList();
        ListIterator<RecordRoute> li = recordRouteList.listIterator(recordRouteList.size());
        while (li.hasPrevious()) {
            RecordRoute rr = li.previous();
            Route route = new Route();
            route.setAddress((AddressImpl) ((AddressImpl) rr.getAddress()).clone());
            route.setParameters((NameValueList) rr.getParameters().clone());
            routeList.add((RouteList) route);
        }
        Contact contact2 = null;
        if (this.lastResponse.getContactHeaders() != null) {
            contact2 = (Contact) this.lastResponse.getContactHeaders().getFirst();
        }
        if (!((SipURI) ((Route) routeList.getFirst()).getAddress().getURI()).hasLrParam()) {
            Route route2 = null;
            if (contact2 != null) {
                route2 = new Route();
                route2.setAddress((AddressImpl) ((AddressImpl) contact2.getAddress()).clone());
            }
            Route firstRoute = (Route) routeList.getFirst();
            routeList.removeFirst();
            URI uri2 = firstRoute.getAddress().getURI();
            ackRequest.setRequestURI(uri2);
            if (route2 != null) {
                routeList.add((RouteList) route2);
            }
            ackRequest.addHeader(routeList);
        } else if (contact2 != null) {
            URI uri3 = (URI) contact2.getAddress().getURI().clone();
            ackRequest.setRequestURI(uri3);
            ackRequest.addHeader(routeList);
        }
        return ackRequest;
    }

    private final Request createErrorAck() throws SipException, ParseException {
        SIPRequest originalRequest = getOriginalRequest();
        if (originalRequest == null) {
            throw new SipException("bad state " + getState());
        }
        if (!getMethod().equals("INVITE")) {
            throw new SipException("Can only ACK an INVITE!");
        }
        if (this.lastResponse == null) {
            throw new SipException("bad Transaction state");
        }
        if (this.lastResponse.getStatusCode() < 200) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("lastResponse = " + this.lastResponse);
            }
            throw new SipException("Cannot ACK a provisional response!");
        }
        return originalRequest.createErrorAck((To) this.lastResponse.getTo());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setViaPort(int port) {
        this.viaPort = port;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setViaHost(String host) {
        this.viaHost = host;
    }

    @Override // gov.nist.javax.sip.stack.SIPTransaction, gov.nist.javax.sip.stack.MessageChannel
    public int getViaPort() {
        return this.viaPort;
    }

    @Override // gov.nist.javax.sip.stack.SIPTransaction, gov.nist.javax.sip.stack.MessageChannel
    public String getViaHost() {
        return this.viaHost;
    }

    public Via getOutgoingViaHeader() {
        return getMessageProcessor().getViaHeader();
    }

    public void clearState() {
    }

    @Override // gov.nist.javax.sip.stack.SIPTransaction
    public void setState(TransactionState newState) {
        if (newState == TransactionState.TERMINATED && isReliable() && !getSIPStack().cacheClientConnections) {
            this.collectionTime = 64;
        }
        if (super.getState() != TransactionState.COMPLETED && (newState == TransactionState.COMPLETED || newState == TransactionState.TERMINATED)) {
            this.sipStack.decrementActiveClientTransactionCount();
        }
        super.setState(newState);
    }

    @Override // gov.nist.javax.sip.stack.SIPTransaction
    protected void startTransactionTimer() {
        if (this.transactionTimerStarted.compareAndSet(false, true)) {
            TimerTask myTimer = new TransactionTimer();
            if (this.sipStack.getTimer() != null) {
                this.sipStack.getTimer().schedule(myTimer, this.BASE_TIMER_INTERVAL, this.BASE_TIMER_INTERVAL);
            }
        }
    }

    @Override // javax.sip.Transaction
    public void terminate() throws ObjectInUseException {
        setState(TransactionState.TERMINATED);
    }

    public boolean checkFromTag(SIPResponse sipResponse) {
        String originalFromTag = ((SIPRequest) getRequest()).getFromTag();
        if (this.defaultDialog != null) {
            if ((originalFromTag == null) ^ (sipResponse.getFrom().getTag() == null)) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("From tag mismatch -- dropping response");
                    return false;
                }
                return false;
            } else if (originalFromTag != null && !originalFromTag.equalsIgnoreCase(sipResponse.getFrom().getTag())) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("From tag mismatch -- dropping response");
                    return false;
                }
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    @Override // gov.nist.javax.sip.stack.ServerResponseInterface
    public void processResponse(SIPResponse sipResponse, MessageChannel incomingChannel) {
        SIPDialog dialog = null;
        String method = sipResponse.getCSeq().getMethod();
        String dialogId = sipResponse.getDialogId(false);
        if (method.equals(Request.CANCEL) && this.lastRequest != null) {
            SIPClientTransaction ict = (SIPClientTransaction) this.lastRequest.getInviteTransaction();
            if (ict != null) {
                dialog = ict.defaultDialog;
            }
        } else {
            dialog = getDialog(dialogId);
        }
        if (dialog == null) {
            int code = sipResponse.getStatusCode();
            if (code > 100 && code < 300 && (sipResponse.getToTag() != null || this.sipStack.isRfc2543Supported())) {
                SIPTransactionStack sIPTransactionStack = this.sipStack;
                if (SIPTransactionStack.isDialogCreated(method)) {
                    synchronized (this) {
                        if (this.defaultDialog != null) {
                            if (sipResponse.getFromTag() != null) {
                                SIPResponse dialogResponse = this.defaultDialog.getLastResponse();
                                String defaultDialogId = this.defaultDialog.getDialogId();
                                if (dialogResponse == null || (method.equals("SUBSCRIBE") && dialogResponse.getCSeq().getMethod().equals("NOTIFY") && defaultDialogId.equals(dialogId))) {
                                    this.defaultDialog.setLastResponse(this, sipResponse);
                                    dialog = this.defaultDialog;
                                } else {
                                    dialog = this.sipStack.getDialog(dialogId);
                                    if (dialog == null && this.defaultDialog.isAssigned()) {
                                        dialog = this.sipStack.createDialog(this, sipResponse);
                                    }
                                }
                                if (dialog != null) {
                                    setDialog(dialog, dialog.getDialogId());
                                } else {
                                    this.sipStack.getStackLogger().logError("dialog is unexpectedly null", new NullPointerException());
                                }
                            } else {
                                throw new RuntimeException("Response without from-tag");
                            }
                        } else if (this.sipStack.isAutomaticDialogSupportEnabled) {
                            dialog = this.sipStack.createDialog(this, sipResponse);
                            setDialog(dialog, dialog.getDialogId());
                        }
                    }
                }
            }
            dialog = this.defaultDialog;
        } else {
            dialog.setLastResponse(this, sipResponse);
        }
        processResponse(sipResponse, incomingChannel, dialog);
    }

    @Override // gov.nist.javax.sip.stack.SIPTransaction, javax.sip.Transaction
    public Dialog getDialog() {
        Dialog retval = null;
        if (this.lastResponse != null && this.lastResponse.getFromTag() != null && this.lastResponse.getToTag() != null && this.lastResponse.getStatusCode() != 100) {
            String dialogId = this.lastResponse.getDialogId(false);
            retval = getDialog(dialogId);
        }
        if (retval == null) {
            retval = this.defaultDialog;
        }
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug(" sipDialogs =  " + this.sipDialogs + " default dialog " + this.defaultDialog + " retval " + retval);
        }
        return retval;
    }

    public SIPDialog getDialog(String dialogId) {
        SIPDialog retval = this.sipDialogs.get(dialogId);
        return retval;
    }

    @Override // gov.nist.javax.sip.stack.SIPTransaction
    public void setDialog(SIPDialog sipDialog, String dialogId) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("setDialog: " + dialogId + "sipDialog = " + sipDialog);
        }
        if (sipDialog == null) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("NULL DIALOG!!");
            }
            throw new NullPointerException("bad dialog null");
        }
        if (this.defaultDialog == null) {
            this.defaultDialog = sipDialog;
            if (getMethod().equals("INVITE") && getSIPStack().maxForkTime != 0) {
                getSIPStack().addForkedClientTransaction(this);
            }
        }
        if (dialogId != null && sipDialog.getDialogId() != null) {
            this.sipDialogs.put(dialogId, sipDialog);
        }
    }

    public SIPDialog getDefaultDialog() {
        return this.defaultDialog;
    }

    public void setNextHop(Hop hop) {
        this.nextHop = hop;
    }

    @Override // javax.sip.ClientTransaction
    public Hop getNextHop() {
        return this.nextHop;
    }

    @Override // javax.sip.ClientTransaction
    public void setNotifyOnRetransmit(boolean notifyOnRetransmit) {
        this.notifyOnRetransmit = notifyOnRetransmit;
    }

    public boolean isNotifyOnRetransmit() {
        return this.notifyOnRetransmit;
    }

    @Override // javax.sip.ClientTransaction
    public void alertIfStillInCallingStateBy(int count) {
        this.timeoutIfStillInCallingState = true;
        this.callingStateTimeoutCount = count;
    }
}